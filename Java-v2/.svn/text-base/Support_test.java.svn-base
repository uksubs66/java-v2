/***************************************************************************************
 *   Support_test:  Use this for running misc tests
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_test extends HttpServlet {


 // Process the form request from support_test.htm.....

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects

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
   // Load the JDBC Driver and connect to DB    ********************* CHANGE THIS ************************
   //
   String club = "hyperion";

   try {

      con = dbConn.Connect(club);       // connect to Local db
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
   //    PUT TEST CODE HERE !!!!!!!!!!
   //
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    String user = "";
    String mtype= "";
    String mtypeNew = "";
    String mtype1 = "Jr 6 to 7";
    String mtype2 = "Jr 8 to 11";
    String mtype3 = "Jr 12 to 15";
    String mtype4 = "Jr 16 to 22";

    int birth = 0;
    int inact = 0;

    //
    //   Get current date
    //
    Calendar cal = new GregorianCalendar();        // get todays date
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH)+1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    year = year - 8;                              // date to determine if < 8 yrs old

    int date8 = (year * 10000) + (month * 100) + day;

    year = year - 4;                              // date to determine if < 12 yrs old

    int date12 = (year * 10000) + (month * 100) + day;

    year = year - 4;                              // date to determine if < 16 yrs old

    int date16 = (year * 10000) + (month * 100) + day;

    year = year - 7;                               // date to determine if < 23 yrs old

    int date23 = (year * 10000) + (month * 100) + day;

    
    
    SystemUtils.logError("Support_test Debug. date8="+date8+", date12="+date12+", date16="+date16+", date23="+date23);                                          
    


    //
    //  Check each Junior to see if the mtype should be changed
    //
    try {

      pstmt = con.prepareStatement (
               "SELECT username, m_type, birth FROM member2b " +
               "WHERE birth != 0 AND inact = 0");

      pstmt.clearParameters();
      rs = pstmt.executeQuery();

      while (rs.next()) {

         user = rs.getString(1);
         mtype = rs.getString(2);
         birth = rs.getInt(3);
         
         if (mtype.startsWith("Jr")) {      // if a dependent

            mtypeNew = "";

            if (birth > date8) {                   // if < 8 yrs old

               mtypeNew = mtype1;                  // Under 8

            } else if (birth > date12) {           // if < 12 yrs old

               mtypeNew = mtype2;                  // 8 - 11

            } else if (birth > date16) {           // if < 16 yrs old

               mtypeNew = mtype3;                  // 12 - 15

            } else if (birth > date23) {           // if < 23 yrs old

               mtypeNew = mtype4;                  // 16 - 22
            }

            //
            //  Update the record if mtype has changed
            //
            if (!mtypeNew.equals( "" )) {

               pstmt2 = con.prepareStatement (
                 "UPDATE member2b SET m_type = ? " +
                 "WHERE username = ?");

               pstmt2.clearParameters();        // clear the parms
               pstmt2.setString(1, mtypeNew);
               pstmt2.setString(2, user);
               pstmt2.executeUpdate();

               pstmt2.close();              // close the stmt
            }
         }
      }

      pstmt.close();
   
   
      
      
      
      con.close();
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Processing Error</H3>");
      out.println("<BR><BR>Error encountered in test code.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Upgrade Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Upgrade Complete</H3>");
   out.println("<BR><BR>Test complete for " +club+ ".");
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
