/***************************************************************************************     
 *   Support_display:  This servlet will process the display request from Support's display page.
 *                     It will display the requested database table.
 *
 *
 *   called by:  support_display.htm
 *
 *   created: 12/05/2001   Bob P.
 *
 *   last updated:
 *
 *        9/08/09   Remove stats5 db table processing as we don't use this any longer.
 *        8/28/09   Changed db query to login2 table so it references columns explicitly instead of numerically
 *        1/24/05    Ver 5 - change club2 to club5 and stats2 to stats5.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
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


public class Support_display extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //
 //  Control from Sales Menu
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing
 }

 //
 // Process the form request from support_display.htm.....
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   String login = "login";                 // table name values
   String member = "member";
   String clubparm = "clubparm";
   String events = "events";
   String restriction = "restriction";
   String guestres = "guestres";
   String teecurr = "teecurr";
   String teepast = "teepast";
   String buddy = "buddy";
   String stats = "stats";
   String lreqs = "lreqs";
   String lottery = "lottery";
   String actlott = "actlott";
   String assigns = "assigns";
   String hotel = "hotel";
   String dist = "dist";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   String support = "support";             // valid username

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

   if (!user.equals( support ) && !user.startsWith( "sales" )) {

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
      out.println("<BR><BR> <FORM>");
      out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
      out.println("</FORM></CENTER></BODY></HTML>");
      return;
   }

   //
   //  Get today's date and then get the date 30 days from now for display purposes - tecurr
   //
   Calendar cal = new GregorianCalendar();       // get todays date

   cal.add(Calendar.DATE,31);                    // add 31 days
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);

   month = month + 1;                            // month starts at zero
   long date = year * 10000;                     // create a date field of yyyymmdd
   date = date + (month * 100);
   date = date + day;                            // date = yyyymmdd (for comparisons)

   //
   // Get the table name requested
   //
   String table_name = req.getParameter("table");

   try {
       
      stmt = con.createStatement();        // create a statement

      //
      // Process the table results according to the table requested
      //
      if (table_name.equals( login )) {

         rs = stmt.executeQuery("SELECT * FROM login2 ORDER BY username");
          
         out.println(SystemUtils.HeadTitle("Display Login2 Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Login Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\" width=\"400\"><tr>");
         out.println("<td><p><b>Username</b></p></td>");
         out.println("<td><p><b>Password</b></p></td>");
         out.println("<td><p><b>Msg Id</b></p></td></tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("username"));
            out.println("</td><td>" + rs.getString("password"));
            out.println("</td><td>" + rs.getString("message"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( member )) {

         rs = stmt.executeQuery("SELECT * FROM member2b ORDER BY name_last");

         out.println(SystemUtils.HeadTitle("Display member2b Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Member Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Username</b></p></td>");
         out.println("<td><p><b>Password</b></p></td>");
         out.println("<td><p><b>Last Name</b></p></td>");
         out.println("<td><p><b>First Name</b></p></td>");
         out.println("<td><p><b>MI</b></p></td>");
         out.println("<td><p><b>Membership</b></p></td>");
         out.println("<td><p><b>Member Type</b></p></td>");
         out.println("<td><p><b>Email</b></p></td>");
         out.println("<td><p><b>Visits</b></p></td>");
         out.println("<td><p><b>c_hcp</b></p></td>");
         out.println("<td><p><b>g_hcp</b></p></td>");
         out.println("<td><p><b>wc</b></p></td>");
         out.println("<td><p><b>msg</b></p></td>");
         out.println("<td><p><b>eopt</b></p></td>");
         out.println("<td><p><b>mNum</b></p></td>");
         out.println("<td><p><b>ghin</b></p></td>");
         out.println("<td><p><b>lock</b></p></td>");
         out.println("<td><p><b>bag</b></p></td>");
         out.println("<td><p><b>birth</b></p></td>");
         out.println("<td><p><b>pos</b></p></td>");
         out.println("<td><p><b>sub_type</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getString(2));
            out.println("</td><td>" + rs.getString(3));
            out.println("</td><td>" + rs.getString(4));
            out.println("</td><td>" + rs.getString(5));
            out.println("</td><td>" + rs.getString(6));
            out.println("</td><td>" + rs.getString(7));
            out.println("</td><td>" + rs.getString(8));
            out.println("</td><td align=\"right\">" + rs.getString(9));
            out.println("</td><td>" + rs.getFloat(10));
            out.println("</td><td>" + rs.getFloat(11));
            out.println("</td><td>" + rs.getString(12));
            out.println("</td><td>" + rs.getString(13));
            out.println("</td><td>" + rs.getInt(14));
            out.println("</td><td>" + rs.getString(15));
            out.println("</td><td>" + rs.getString(16));
            out.println("</td><td>" + rs.getString(17));
            out.println("</td><td>" + rs.getString(18));
            out.println("</td><td>" + rs.getInt(19));
            out.println("</td><td>" + rs.getString(20));
            out.println("</td><td>" + rs.getString(21));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( clubparm )) {

         rs = stmt.executeQuery("SELECT * FROM club5");

         out.println(SystemUtils.HeadTitle("Display Club Parameters Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Club Parameters Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Club Name</b></p></td>");
         out.println("<td><p><b>Guest 1</b></p></td>");
         out.println("<td><p><b>Guest 2</b></p></td>");
         out.println("<td><p><b>Guest 3</b></p></td>");
         out.println("<td><p><b>Guest 4</b></p></td>");
         out.println("<td><p><b>Mem 1</b></p></td>");
         out.println("<td><p><b>Mem 2</b></p></td>");
         out.println("<td><p><b>Mem 3</b></p></td>");
         out.println("<td><p><b>Mem 4</b></p></td>");
         out.println("<td><p><b>Mship 1</b></p></td>");
         out.println("<td><p><b>Mship 2</b></p></td>");
         out.println("<td><p><b>Mship 3</b></p></td>");
         out.println("<td><p><b>Mship 4</b></p></td></tr>");

         while(rs.next()) {

            out.println("<tr align=\"center\"><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getString("guest1"));
            out.println("</td><td>" + rs.getString("guest2"));
            out.println("</td><td>" + rs.getString("guest3"));
            out.println("</td><td>" + rs.getString("guest4"));
            out.println("</td><td>" + rs.getString("mem1"));
            out.println("</td><td>" + rs.getString("mem2"));
            out.println("</td><td>" + rs.getString("mem3"));
            out.println("</td><td>" + rs.getString("mem4"));
            out.println("</td><td>" + rs.getString("mship1"));
            out.println("</td><td>" + rs.getString("mship2"));
            out.println("</td><td>" + rs.getString("mship3"));
            out.println("</td><td>" + rs.getString("mship4"));
            out.println("</td></tr>");
         }

         stmt.close();

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT * FROM clubparm2");

         out.println("</TABLE><BR>");
         out.println("<BR><H2>Course Parms Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Course Name</b></p></td>");
         out.println("<td><p><b>1st Hr</b></p></td>");
         out.println("<td><p><b>1st Min</b></p></td>");
         out.println("<td><p><b>Last Hr</b></p></td>");
         out.println("<td><p><b>Last Min</b></p></td>");
         out.println("<td><p><b>Btwn</b></p></td>");
         out.println("<td><p><b>XX</b></p></td>");
         out.println("<td><p><b>Alt</b></p></td>");
         out.println("<td><p><b>Fives</b></p></td>");
         out.println("<td><p><b>Tmode1</b></p></td>");
         out.println("<td><p><b>Tmode2</b></p></td>");
         out.println("<td><p><b>Tmode3</b></p></td>");
         out.println("<td><p><b>Tmode4</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr align=\"center\"><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getInt(2));
            out.println("</td><td>" + rs.getInt(3));
            out.println("</td><td>" + rs.getInt(4));
            out.println("</td><td>" + rs.getInt(5));
            out.println("</td><td>" + rs.getInt(6));
            out.println("</td><td>" + rs.getInt(7));
            out.println("</td><td>" + rs.getInt(8));
            out.println("</td><td>" + rs.getInt(9));
            out.println("</td><td>" + rs.getString(11));
            out.println("</td><td>" + rs.getString(13));
            out.println("</td><td>" + rs.getString(15));
            out.println("</td><td>" + rs.getString(17));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( events )) {

         rs = stmt.executeQuery("SELECT * FROM events2b ORDER BY date");

         out.println(SystemUtils.HeadTitle("Display Events Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Special Events Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Event Name</b></p></td>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>Start Hr</b></p></td>");
         out.println("<td><p><b>Start Min</b></p></td>");
         out.println("<td><p><b>End Hr</b></p></td>");
         out.println("<td><p><b>End Min</b></p></td>");
         out.println("<td><p><b>Color</b></p></td>");
         out.println("<td><p><b>Type</b></p></td></tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getLong(2));
            out.println("</td><td>" + rs.getShort(6));
            out.println("</td><td>" + rs.getShort(7));
            out.println("</td><td>" + rs.getShort(9));
            out.println("</td><td>" + rs.getShort(10));
            out.println("</td><td>" + rs.getString(12));
            out.println("</td><td>" + rs.getInt(13));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( restriction )) {

         rs = stmt.executeQuery("SELECT * FROM restriction2 ORDER BY name");

         out.println(SystemUtils.HeadTitle("Display Restriction Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Restrictions Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Restrict Name</b></p></td>");
         out.println("<td><p><b>Start Date</b></p></td>");
         out.println("<td><p><b>Start Hr</b></p></td>");
         out.println("<td><p><b>Start Min</b></p></td>");
         out.println("<td><p><b>End Date</b></p></td>");
         out.println("<td><p><b>End Hr</b></p></td>");
         out.println("<td><p><b>End Min</b></p></td>");
         out.println("<td><p><b>Recurr.</b></p></td>");
         out.println("<td><p><b>Mem-1</b></p></td>");
         out.println("<td><p><b>Mem-2</b></p></td>");
         out.println("<td><p><b>Mem-3</b></p></td>");
         out.println("<td><p><b>Mem-4</b></p></td>");
         out.println("<td><p><b>Mship-1</b></p></td>");
         out.println("<td><p><b>Mship-2</b></p></td>");
         out.println("<td><p><b>Mship-3</b></p></td>");
         out.println("<td><p><b>Mship-4</b></p></td>");
         out.println("<td><p><b>Color</b></p></td>");
         out.println("<td><p><b>Course</b></p></td>");
         out.println("<td><p><b>F/B</b></p></td>");
         out.println("<td><p><b>Showit</b></p></td></tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString(1));     // name
            out.println("</td><td>" + rs.getLong(2));      // s date
            out.println("</td><td>" + rs.getShort(6));     // s hr
            out.println("</td><td>" + rs.getShort(7));     // s min
            out.println("</td><td>" + rs.getLong(9));      // e date
            out.println("</td><td>" + rs.getShort(13));    // e hr
            out.println("</td><td>" + rs.getShort(14));    // e min
            out.println("</td><td>" + rs.getString(16));   // recurr
            out.println("</td><td>" + rs.getString("mem1"));    //  mem types
            out.println("</td><td>" + rs.getString("mem2"));    //
            out.println("</td><td>" + rs.getString("mem3"));    //
            out.println("</td><td>" + rs.getString("mem4"));    //
            out.println("</td><td>" + rs.getString("mship1"));    // mship types
            out.println("</td><td>" + rs.getString("mship2"));    //
            out.println("</td><td>" + rs.getString("mship3"));    //
            out.println("</td><td>" + rs.getString("mship4"));    //
            out.println("</td><td>" + rs.getString("color"));    // color
            out.println("</td><td>" + rs.getString("courseName"));    // course
            out.println("</td><td>" + rs.getString("fb"));    // f/b
            out.println("</td><td>" + rs.getString("showit"));    // showit flag
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( guestres )) {

         rs = stmt.executeQuery("SELECT * FROM guestres2 ORDER BY sdate");

         out.println(SystemUtils.HeadTitle("Display Guest Restriction Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Guest Restrictions Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Name</b></p></td>");
         out.println("<td><p><b>Start Date</b></p></td>");
         out.println("<td><p><b>Start Hr</b></p></td>");
         out.println("<td><p><b>Start Min</b></p></td>");
         out.println("<td><p><b>End Date</b></p></td>");
         out.println("<td><p><b>End Hr</b></p></td>");
         out.println("<td><p><b>End Min</b></p></td>");
         out.println("<td><p><b>Recurr.</b></p></td>");
         out.println("<td><p><b>Num Guests</b></p></td>");
         out.println("<td><p><b>G1</b></p></td>");
         out.println("<td><p><b>G2</b></p></td>");
         out.println("<td><p><b>G3</b></p></td>");
         out.println("<td><p><b>G4</b></p></td>");
         out.println("<td><p><b>G5</b></p></td>");
         out.println("<td><p><b>G6</b></p></td>");
         out.println("<td><p><b>G7</b></p></td>");
         out.println("<td><p><b>G8</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getLong(2));
            out.println("</td><td>" + rs.getShort(6));
            out.println("</td><td>" + rs.getShort(7));
            out.println("</td><td>" + rs.getLong(9));
            out.println("</td><td>" + rs.getShort(13));
            out.println("</td><td>" + rs.getShort(14));
            out.println("</td><td>" + rs.getString(16));
            out.println("</td><td>" + rs.getShort(17));
            out.println("</td><td>" + rs.getString(18));
            out.println("</td><td>" + rs.getString(19));
            out.println("</td><td>" + rs.getString(20));
            out.println("</td><td>" + rs.getString(21));
            out.println("</td><td>" + rs.getString(22));
            out.println("</td><td>" + rs.getString(23));
            out.println("</td><td>" + rs.getString(24));
            out.println("</td><td>" + rs.getString(25));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( teecurr )) {

         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT * " +
            "FROM teecurr2 WHERE date < ? ORDER BY date, time");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, date);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         out.println(SystemUtils.HeadTitle("Current Tee Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Current Tee Times Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>Day</b></p></td>");
         out.println("<td><p><b>Hour</b></p></td>");
         out.println("<td><p><b>Min</b></p></td>");
         out.println("<td><p><b>Event</b></p></td>");
         out.println("<td><p><b>Restriction</b></p></td>");
         out.println("<td><p><b>Player 1</b></p></td>");
         out.println("<td><p><b>Player 2</b></p></td>");
         out.println("<td><p><b>Player 3</b></p></td>");
         out.println("<td><p><b>Player 4</b></p></td>");
         out.println("<td><p><b>C/W1</b></p></td>");
         out.println("<td><p><b>C/W2</b></p></td>");
         out.println("<td><p><b>C/W3</b></p></td>");
         out.println("<td><p><b>C/W4</b></p></td>");
         out.println("<td><p><b>First</b></p></td>");
         out.println("<td><p><b>In Use</b></p></td>");
         out.println("<td><p><b>In Use By</b></p></td>");
         out.println("<td><p><b>Player 5</b></p></td>");
         out.println("<td><p><b>C/W5</b></p></td>");
         out.println("<td><p><b>Course</b></p></td>");
         out.println("<td><p><b>Lottery</b></p></td>");
         out.println("<td><p><b>Lott Color</b></p></td>");
         out.println("<td><p><b>Userg1</b></p></td>");
         out.println("<td><p><b>Userg2</b></p></td>");
         out.println("<td><p><b>Userg3</b></p></td>");
         out.println("<td><p><b>Userg4</b></p></td>");
         out.println("<td><p><b>Userg5</b></p></td>");
         out.println("<td><p><b>H-New</b></p></td>");
         out.println("<td><p><b>H-Mod</b></p></td>");
         out.println("<td><p><b>Orig</b></p></td>");
         out.println("<td><p><b>Conf</b></p></td>");
         out.println("<td><p><b>P91</b></p></td>");
         out.println("<td><p><b>P92</b></p></td>");
         out.println("<td><p><b>P93</b></p></td>");
         out.println("<td><p><b>P94</b></p></td>");
         out.println("<td><p><b>P95</b></p></td>");
         out.println("<td><p><b>mNum1</b></p></td>");
         out.println("<td><p><b>mNum2</b></p></td>");
         out.println("<td><p><b>mNum3</b></p></td>");
         out.println("<td><p><b>mNum4</b></p></td>");
         out.println("<td><p><b>mNum5</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getLong(1));
            out.println("</td><td>" + rs.getString(5));
            out.println("</td><td>" + rs.getShort(6));
            out.println("</td><td>" + rs.getShort(7));
            out.println("</td><td>" + rs.getString(9));
            out.println("</td><td>" + rs.getString(11));
            out.println("</td><td>" + rs.getString(13));
            out.println("</td><td>" + rs.getString(14));
            out.println("</td><td>" + rs.getString(15));
            out.println("</td><td>" + rs.getString(16));
            out.println("</td><td>" + rs.getString(21));
            out.println("</td><td>" + rs.getString(22));
            out.println("</td><td>" + rs.getString(23));
            out.println("</td><td>" + rs.getString(24));
            out.println("</td><td>" + rs.getShort(25));
            out.println("</td><td>" + rs.getShort(26));
            out.println("</td><td>" + rs.getString(27));
            out.println("</td><td>" + rs.getString(38));      // player 5
            out.println("</td><td>" + rs.getString(40));
            out.println("</td><td>" + rs.getString(46));      // course name
            out.println("</td><td>" + rs.getString("lottery"));   
            out.println("</td><td>" + rs.getString("lottery_color"));     
            out.println("</td><td>" + rs.getString("userg1")); // member usernames for guests
            out.println("</td><td>" + rs.getString("userg2"));
            out.println("</td><td>" + rs.getString("userg3"));
            out.println("</td><td>" + rs.getString("userg4"));
            out.println("</td><td>" + rs.getString("userg5"));
            out.println("</td><td>" + rs.getString("hotelNew"));
            out.println("</td><td>" + rs.getString("hotelMod"));
            out.println("</td><td>" + rs.getString("orig_by"));
            out.println("</td><td>" + rs.getString("conf"));
            out.println("</td><td>" + rs.getString("p91"));
            out.println("</td><td>" + rs.getString("p92"));
            out.println("</td><td>" + rs.getString("p93"));
            out.println("</td><td>" + rs.getString("p94"));
            out.println("</td><td>" + rs.getString("p95"));
            out.println("</td><td>" + rs.getString("mNum1"));
            out.println("</td><td>" + rs.getString("mNum2"));
            out.println("</td><td>" + rs.getString("mNum3"));
            out.println("</td><td>" + rs.getString("mNum4"));
            out.println("</td><td>" + rs.getString("mNum5"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
           
         pstmt1.close();
      }

      if (table_name.equals( teepast )) {

         rs = stmt.executeQuery("SELECT * FROM teepast2 ORDER BY date");

         out.println(SystemUtils.HeadTitle("Past Tee Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Past Tee Times Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>Day</b></p></td>");
         out.println("<td><p><b>Hour</b></p></td>");
         out.println("<td><p><b>Min</b></p></td>");
         out.println("<td><p><b>Event</b></p></td>");
         out.println("<td><p><b>Restriction</b></p></td>");
         out.println("<td><p><b>Player 1</b></p></td>");
         out.println("<td><p><b>Player 2</b></p></td>");
         out.println("<td><p><b>Player 3</b></p></td>");
         out.println("<td><p><b>Player 4</b></p></td>");
         out.println("<td><p><b>User 1</b></p></td>");
         out.println("<td><p><b>User 2</b></p></td>");
         out.println("<td><p><b>User 3</b></p></td>");
         out.println("<td><p><b>User 4</b></p></td>");
         out.println("<td><p><b>C/W1</b></p></td>");
         out.println("<td><p><b>C/W2</b></p></td>");
         out.println("<td><p><b>C/W3</b></p></td>");
         out.println("<td><p><b>C/W4</b></p></td>");
         out.println("<td><p><b>P91</b></p></td>");
         out.println("<td><p><b>P92</b></p></td>");
         out.println("<td><p><b>P93</b></p></td>");
         out.println("<td><p><b>P94</b></p></td>");
         out.println("<td><p><b>P95</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getLong(1));
            out.println("</td><td>" + rs.getString(5));
            out.println("</td><td>" + rs.getShort(6));
            out.println("</td><td>" + rs.getShort(7));
            out.println("</td><td>" + rs.getString(9));
            out.println("</td><td>" + rs.getString(11));
            out.println("</td><td>" + rs.getString(13));
            out.println("</td><td>" + rs.getString(14));
            out.println("</td><td>" + rs.getString(15));
            out.println("</td><td>" + rs.getString(16));
            out.println("</td><td>" + rs.getString(17));
            out.println("</td><td>" + rs.getString(18));
            out.println("</td><td>" + rs.getString(19));
            out.println("</td><td>" + rs.getString(20));
            out.println("</td><td>" + rs.getString(21));
            out.println("</td><td>" + rs.getString(22));
            out.println("</td><td>" + rs.getString(23));
            out.println("</td><td>" + rs.getString(24));
            out.println("</td><td>" + rs.getInt("p91"));
            out.println("</td><td>" + rs.getInt("p92"));
            out.println("</td><td>" + rs.getInt("p93"));
            out.println("</td><td>" + rs.getInt("p94"));
            out.println("</td><td>" + rs.getInt("p95"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( buddy )) {

         rs = stmt.executeQuery("SELECT * FROM buddy ORDER BY username");

         out.println(SystemUtils.HeadTitle("Partner Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Partner Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Member</b></p></td>");
         out.println("<td><p><b>Partner 1</b></p></td>");
         out.println("<td><p><b>Partner 2</b></p></td>");
         out.println("<td><p><b>Partner 3</b></p></td>");
         out.println("<td><p><b>Partner 4</b></p></td>");
         out.println("<td><p><b>Partner 5</b></p></td>");
         out.println("<td><p><b>Partner 6</b></p></td>");
         out.println("<td><p><b>Partner 7</b></p></td></tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString(1));
            out.println("</td><td>" + rs.getString(2));
            out.println("</td><td>" + rs.getString(3));
            out.println("</td><td>" + rs.getString(4));
            out.println("</td><td>" + rs.getString(5));
            out.println("</td><td>" + rs.getString(6));
            out.println("</td><td>" + rs.getString(7));
            out.println("</td><td>" + rs.getString(8));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( lottery )) {

         rs = stmt.executeQuery("SELECT * FROM lottery3 ORDER BY name");

         out.println(SystemUtils.HeadTitle("Lottery Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Lottery Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Name</b></p></td>");
         out.println("<td><p><b>S Date</b></p></td>");
         out.println("<td><p><b>S Time</b></p></td>");
         out.println("<td><p><b>E Date</b></p></td>");
         out.println("<td><p><b>E Time</b></p></td>");
         out.println("<td><p><b>Recurr</b></p></td>");
         out.println("<td><p><b>Course</b></p></td>");
         out.println("<td><p><b>F/B</b></p></td>");
         out.println("<td><p><b>Sd Days</b></p></td>");
         out.println("<td><p><b>Sd Time</b></p></td>");
         out.println("<td><p><b>Ed Days</b></p></td>");
         out.println("<td><p><b>Ed Time</b></p></td>");
         out.println("<td><p><b>P Days</b></p></td>");
         out.println("<td><p><b>P Time</b></p></td>");
         out.println("<td><p><b>Type</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("name"));
            out.println("</td><td>" + rs.getLong("sdate"));
            out.println("</td><td>" + rs.getInt("stime"));
            out.println("</td><td>" + rs.getLong("edate"));
            out.println("</td><td>" + rs.getInt("etime"));
            out.println("</td><td>" + rs.getString("recurr"));
            out.println("</td><td>" + rs.getString("courseName"));
            out.println("</td><td>" + rs.getString("fb"));
            out.println("</td><td>" + rs.getInt("sdays"));
            out.println("</td><td>" + rs.getInt("sdtime"));
            out.println("</td><td>" + rs.getInt("edays"));
            out.println("</td><td>" + rs.getInt("edtime"));
            out.println("</td><td>" + rs.getInt("pdays"));
            out.println("</td><td>" + rs.getInt("ptime"));
            out.println("</td><td>" + rs.getString("type"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( lreqs )) {

         rs = stmt.executeQuery("SELECT * FROM lreqs3 ORDER BY name");

         out.println(SystemUtils.HeadTitle("Lottery Request Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Lottery Request Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Name</b></p></td>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>Time</b></p></td>");
         out.println("<td><p><b>Before</b></p></td>");
         out.println("<td><p><b>After</b></p></td>");
         out.println("<td><p><b>Player 1</b></p></td>");
         out.println("<td><p><b>Player 2</b></p></td>");
         out.println("<td><p><b>Player 3</b></p></td>");
         out.println("<td><p><b>Player 4</b></p></td>");
         out.println("<td><p><b>Player 5</b></p></td>");
         out.println("<td><p><b>F/B</b></p></td>");
         out.println("<td><p><b>Course</b></p></td>");
         out.println("<td><p><b>ID</b></p></td>");
         out.println("<td><p><b>Grps</b></p></td>");
         out.println("<td><p><b>Type</b></p></td>");
         out.println("<td><p><b>State</b></p></td>");
         out.println("<td><p><b>ATime1</b></p></td>");
         out.println("<td><p><b>Players</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("name"));
            out.println("</td><td>" + rs.getLong("date"));
            out.println("</td><td>" + rs.getInt("time"));
            out.println("</td><td>" + rs.getInt("minsbefore"));
            out.println("</td><td>" + rs.getInt("minsafter"));
            out.println("</td><td>" + rs.getString("player1"));
            out.println("</td><td>" + rs.getString("player2"));
            out.println("</td><td>" + rs.getString("player3"));
            out.println("</td><td>" + rs.getString("player4"));
            out.println("</td><td>" + rs.getString("player5"));
            out.println("</td><td>" + rs.getInt("fb"));
            out.println("</td><td>" + rs.getString("courseName"));
            out.println("</td><td>" + rs.getLong("id"));
            out.println("</td><td>" + rs.getInt("groups"));
            out.println("</td><td>" + rs.getString("type"));
            out.println("</td><td>" + rs.getInt("state"));
            out.println("</td><td>" + rs.getInt("atime1"));
            out.println("</td><td>" + rs.getInt("players"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( actlott )) {

         rs = stmt.executeQuery("SELECT * FROM actlott3 ORDER BY name");

         out.println(SystemUtils.HeadTitle("Active Lottery Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Active Lottery Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Name</b></p></td>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>P Date</b></p></td>");
         out.println("<td><p><b>P Time</b></p></td>");
         out.println("<td><p><b>Course</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("name"));
            out.println("</td><td>" + rs.getLong("date"));
            out.println("</td><td>" + rs.getLong("pdate"));
            out.println("</td><td>" + rs.getInt("ptime"));
            out.println("</td><td>" + rs.getString("courseName"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( assigns )) {

         rs = stmt.executeQuery("SELECT * FROM lassigns5 ORDER BY date and lname and username");

         out.println(SystemUtils.HeadTitle("Lottery WeightedBP Assigns Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>WeightedBP Lottery Weight Assigns Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>User</b></p></td>");
         out.println("<td><p><b>Lottery</b></p></td>");
         out.println("<td><p><b>Date</b></p></td>");
         out.println("<td><p><b>Minutes</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("username"));
            out.println("</td><td>" + rs.getString("lname"));
            out.println("</td><td>" + rs.getLong("date"));
            out.println("</td><td>" + rs.getInt("mins"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( hotel )) {

         rs = stmt.executeQuery("SELECT * FROM hotel3 ORDER BY username");

         out.println(SystemUtils.HeadTitle("Hotel Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Hotel User Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Username</b></p></td>");
         out.println("<td><p><b>Password</b></p></td>");
         out.println("<td><p><b>L Name</b></p></td>");
         out.println("<td><p><b>F Name</b></p></td>");
         out.println("<td><p><b>D1</b></p></td>");
         out.println("<td><p><b>D2</b></p></td>");
         out.println("<td><p><b>D3</b></p></td>");
         out.println("<td><p><b>D4</b></p></td>");
         out.println("<td><p><b>D5</b></p></td>");
         out.println("<td><p><b>D6</b></p></td>");
         out.println("<td><p><b>D7</b></p></td>");
         out.println("<td><p><b>G1</b></p></td>");
         out.println("<td><p><b>G2</b></p></td>");
         out.println("<td><p><b>G3</b></p></td>");
         out.println("<td><p><b>G4</b></p></td>");
         out.println("<td><p><b>G5</b></p></td>");
         out.println("<td><p><b>G6</b></p></td>");
         out.println("<td><p><b>G7</b></p></td>");
         out.println("<td><p><b>G8</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("username"));
            out.println("</td><td>" + rs.getString("password"));
            out.println("</td><td>" + rs.getString("name_last"));
            out.println("</td><td>" + rs.getString("name_first"));
            out.println("</td><td>" + rs.getInt("days1"));
            out.println("</td><td>" + rs.getInt("days2"));
            out.println("</td><td>" + rs.getInt("days3"));
            out.println("</td><td>" + rs.getInt("days4"));
            out.println("</td><td>" + rs.getInt("days5"));
            out.println("</td><td>" + rs.getInt("days6"));
            out.println("</td><td>" + rs.getInt("days7"));
            out.println("</td><td>" + rs.getString("guest1"));
            out.println("</td><td>" + rs.getString("guest2"));
            out.println("</td><td>" + rs.getString("guest3"));
            out.println("</td><td>" + rs.getString("guest4"));
            out.println("</td><td>" + rs.getString("guest5"));
            out.println("</td><td>" + rs.getString("guest6"));
            out.println("</td><td>" + rs.getString("guest7"));
            out.println("</td><td>" + rs.getString("guest8"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      if (table_name.equals( dist )) {

         rs = stmt.executeQuery("SELECT * FROM dist4 ORDER BY owner");

         out.println(SystemUtils.HeadTitle("Dist Table"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><H2>Email Distribution List Table</H2><BR><BR>");
         out.println("<Table border=\"1\" align=\"center\"><tr>");
         out.println("<td><p><b>Name</b></p></td>");
         out.println("<td><p><b>Owner</b></p></td>");
         out.println("<td><p><b>User1</b></p></td>");
         out.println("<td><p><b>User2</b></p></td>");
         out.println("<td><p><b>User3</b></p></td>");
         out.println("<td><p><b>User4</b></p></td>");
         out.println("<td><p><b>User5</b></p></td>");
         out.println("<td><p><b>User6</b></p></td>");
         out.println("<td><p><b>User7</b></p></td>");
         out.println("<td><p><b>User8</b></p></td>");
         out.println("<td><p><b>User9</b></p></td>");
         out.println("<td><p><b>User10</b></p></td>");
         out.println("</tr>");

         while(rs.next()) {

            out.println("<tr><td>" + rs.getString("name"));
            out.println("</td><td>" + rs.getString("owner"));
            out.println("</td><td>" + rs.getString("user1"));
            out.println("</td><td>" + rs.getString("user2"));
            out.println("</td><td>" + rs.getString("user3"));
            out.println("</td><td>" + rs.getString("user4"));
            out.println("</td><td>" + rs.getString("user5"));
            out.println("</td><td>" + rs.getString("user6"));
            out.println("</td><td>" + rs.getString("user7"));
            out.println("</td><td>" + rs.getString("user8"));
            out.println("</td><td>" + rs.getString("user9"));
            out.println("</td><td>" + rs.getString("user10"));
            out.println("</td></tr>");
         }

         out.println("</TABLE><BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM></CENTER></BODY></HTML>");
      }

      stmt.close();

   }
   catch (Exception exc) {             // SQL Error

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H1>SQL Type Error</H1>");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <FORM>");
      out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
      out.println("</FORM></CENTER></BODY></HTML>");
     
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
