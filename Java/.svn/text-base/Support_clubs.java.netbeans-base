/***************************************************************************************     
 *   Support_clubs:  This servlet will process the add or init clubs request from Support's Init page.
 *                   It will perform the processing necessary to initialize the v5 clubs database or add
 *                   a new club to the database. 
 *
 *          Clubs Database:   DB name = v5
 *                            Table name = clubs
 *
 *
 *   called by:  support_initclubs.htm
 *               support_addclubs.htm
 *
 *   created: 6/13/2002   Bob P.
 *
 *   last updated:
 *
 *        4/10/08   Add processing to mark club inactive - from Support_displayClubs.
 *        3/05/08   Updated SQL statements for changes made to v5.clubs table (removed legacy fields, added inactive)
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


public class Support_clubs extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 // Process the form request from support_initclubs.htm or support_addclubs.htm

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   String support = "support";             // valid username
   String clubname = "";

   //String demo = "('demov5', 'Demo Club for ForeTees v5', 'none', 'none', 'none', 'none', 'none', 'none', 'none', 'none', '20020618')";
   String demo = "('demov5', 'Demo Club for ForeTees v5', 20020618, 0)";

   HttpSession session = null; 


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = "" +rev+ "";            // get database name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   int count = 0;
   
   //
   //**********************************************
   //  Check if call is for 'init' or 'add'
   //**********************************************
   //
   if (req.getParameter("init") != null) {

      // Check if the DB Tables already exist.........

      try {

         stmt = con.createStatement();        // create a statement
         rs = stmt.executeQuery("SELECT * FROM clubs");   // check for clubs table...

         stmt.close();

            // DB Table already exists - do not continue....

            out.println("<HTML><HEAD><TITLE>Database Already Exists Warning</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>Database Tables Already Exist</H3>");
            out.println("<BR><BR><b>Warning,</b> the clubs database table already exists.");
            out.println("<BR><BR>To start from scratch you will have to first drop the database via MySQL.");
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
            out.println("</CENTER></BODY></HTML>");
            return;

      }
      catch (Exception exc) {

         // This is good - table does not exist - create it now
      }


      //
      //  Create the database table 
      //
      try {
/*
         stmt.executeUpdate("CREATE TABLE clubs (clubname varchar(15), fullname varchar(40), " +
                            "phone varchar(20), fax varchar(20), gmname varchar(40), " +
                            "gmphone varchar(20), gmemail varchar(30), proname varchar(40), " +
                            "prophone varchar(20), proemail varchar(30), startdate bigint)");
*/
                 
         stmt.executeUpdate("CREATE TABLE clubs (" +
                                "clubname varchar(30), " +
                                "fullname varchar(40), " +
                                "startdate int, " +
                                "inactive tinyint NOT NULL DEFAULT 0)");

         //
         //  Now add the demo site
         //
         stmt.executeUpdate("INSERT INTO clubs VALUES " + demo );

         stmt.close();
         
      }
      catch (Exception exc) {

         // SQL Error ....

         out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>SQL Type Error</H3>");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      // DB Table setup complete - inform support....

      out.println("<HTML><HEAD><TITLE>Database Creation Complete</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Database Table Successfully Built</H3>");
      out.println("<BR><BR>Please continue.");
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
          
   }       // end of if init call

   //
   //**********************************************
   //  Check if call is for 'add'
   //**********************************************
   //
   if (req.getParameter("add") != null) {

      //
      // Get all the parameters entered
      //
      clubname = req.getParameter("clubname").trim();           //  club name - short version for db name
      String fullname = req.getParameter("fullname").trim();    //  club's full name
      
      
/*      
      
//      String phone = req.getParameter("phone");                 //  phone number
//      String fax = req.getParameter("fax");                     //  fax number
//      String gmname = req.getParameter("gmname");               //  GM's name
//      String gmphone = req.getParameter("gmphone");             //  GM's phone
//      String gmemail = req.getParameter("gmemail");             //  GM's email address
//      String proname = req.getParameter("proname");             //  Pro's name
//      String prophone = req.getParameter("prophone");           //  Pro's phone
//      String proemail = req.getParameter("proemail");           //  Pro's email address

      String phone = "";                //  phone number
      String fax = "";                  //  fax number
      String gmname = "";               //  GM's name
      String gmphone = "";              //  GM's phone
      String gmemail = "";              //  GM's email address
      String proname = "";              //  Pro's name
      String prophone = "";             //  Pro's phone
      String proemail = "";             //  Pro's email address
*/
      
      //
      //  Verify that some fields were entered
      //
      if (clubname.equals( "" ) || fullname.equals( "" )) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>You must enter at least the club name, full name and phone.");
         out.println("<BR>Please try again.");
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<a href=\"javascript:history.back(1)\">Return</a>");
         out.println("</font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }


      //
      //  Get today's date and create a date value for the db table
      //
      Calendar cal = new GregorianCalendar();      // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month++;                                     // month starts at zero

      int date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + day;

      //
      //  make sure it doesn't already exist
      //
      try {

         boolean exist = false;

         PreparedStatement pstmt = con.prepareStatement (
           "SELECT * FROM clubs WHERE clubname = ?");

         pstmt.clearParameters();
         pstmt.setString(1, clubname);
         pstmt.executeQuery();
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
         
            exist = true;
         }
         pstmt.close();   // close the stmt
         
         if (exist == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The club name already exists.");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }

         //
         //  add club info to the database
         //
         pstmt = con.prepareStatement (
           "INSERT INTO clubs (clubname, fullname, startdate, inactive) VALUES (?,?,?,0)");

         pstmt.clearParameters();
         pstmt.setString(1, clubname);
         pstmt.setString(2, fullname);
         pstmt.setInt(3, date);
         pstmt.executeUpdate();

         pstmt.close();

         out.println("<HTML><HEAD><TITLE>Club Addition Complete</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Club Info Successfully Added</H3>");
         out.println("<BR><BR>Please continue.");
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR>Exception:   " + exc.getMessage());
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/support_main.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   }       // end of if add call
   
   //
   //**********************************************
   //  Check if call is for 'Delete' or 'Status Change''
   //**********************************************
   //
   if (req.getParameter("delete") != null || req.getParameter("Act") != null || req.getParameter("Inact") != null) {

      clubname = req.getParameter("clubname");           //  club name - short version for db name

      out.println(SystemUtils.HeadTitle("Club Change Request"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Club Removal or Status Change Request</H3>");
      
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Support_clubs\">");
      out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +clubname+ "\">");

      if (req.getParameter("delete") != null) {
         out.println("<BR><BR>You have requested that " +clubname+ " be DELETED from the Club Table.");
         out.println("<input type=\"hidden\" name=\"confdelete\" value=\"yes\">");
      }

      if (req.getParameter("Inact") != null) {        // if status change and status was Inactive
         out.println("<BR><BR>You have requested that the status for " +clubname+ " be changed to ACTIVE.");
         out.println("<input type=\"hidden\" name=\"confact\" value=\"yes\">");
      }

      if (req.getParameter("Act") != null) {        // if status change and status was Active
         out.println("<BR><BR>You have requested that the status for " +clubname+ " be changed to INACTIVE.");
         out.println("<input type=\"hidden\" name=\"confinact\" value=\"yes\">");
      }
      out.println("<BR><BR>ARE YOU SURE YOU WANT TO DO THIS?");
      out.println("<font size=\"2\">");
      
      out.println("<BR><BR><input type=\"submit\" value=\"YES - Continue\">");
      out.println("</FORM>");
      
      out.println("<BR><BR> <FORM>");
      out.println("<INPUT TYPE='BUTTON' Value='NO - CANCEL' onClick='self.close()'></INPUT>");
      out.println("</FORM>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }       // end of if delete call

   
   //
   //**********************************************
   //  Check if call is for 'Confirm Status Change'
   //**********************************************
   //
   if (req.getParameter("confact") != null || req.getParameter("confinact") != null) {
      
      int status = 0;
      
      if (req.getParameter("confinact") != null) {   // if request to mark Inactive

         status = 1;   

      } else {

         status = 0;          // request to mark Active
      }      

      clubname = req.getParameter("clubname");           //  club name - short version for db name

      try {

         PreparedStatement pstmt = con.prepareStatement (
           "UPDATE clubs SET inactive = ? WHERE clubname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setInt(1, status);
         pstmt.setString(2, clubname);
         pstmt.executeUpdate();         // execute the prepared stmt

         pstmt.close();   // close the stmt

         out.println("<HTML><HEAD><TITLE>Club Status Change Complete</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Club Status Has Been Changed For " +clubname+ "</H3>");
         out.println("<BR><BR>Please continue.");
         out.println("<BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM>");
         out.println("</CENTER></BODY></HTML>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR>Exception:   " + exc.getMessage());
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   }       // end of if delete call

   
   //
   //**********************************************
   //  Check if call is for 'Confirm Delete'
   //**********************************************
   //
   if (req.getParameter("confdelete") != null) {

      clubname = req.getParameter("clubname");           //  club name - short version for db name

      try {

         PreparedStatement pstmt = con.prepareStatement (
           "DELETE FROM clubs WHERE clubname = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, clubname);
         pstmt.executeUpdate();         // execute the prepared stmt

         pstmt.close();   // close the stmt

         out.println("<HTML><HEAD><TITLE>Club Deletion Complete</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Club " +clubname+ " Has Been Removed From The Club Table</H3>");
         out.println("<BR><BR>Please continue.");
         out.println("<BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM>");
         out.println("</CENTER></BODY></HTML>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR>Exception:   " + exc.getMessage());
         out.println("<BR><BR>");
         out.println("<font size=\"2\">");
         out.println("<BR><BR> <FORM>");
         out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
         out.println("</FORM>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   }       // end of if delete call

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
   out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
