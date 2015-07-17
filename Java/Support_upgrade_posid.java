/***************************************************************************************
 *   Support_upgrade_posid:  For PSK clubs.
 *
 *
 *    
 *      PSK Clubs - set the posid field based in mNum (i.e. 273 = 0273-000).
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_upgrade_posid extends HttpServlet {


 // Process the form request from support_upgrade.htm.....

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   String support = "support";             // valid username

   HttpSession session = null;


   String mNum = "";
   String user = "";
   String posid = "";
     

   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String userName = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");       // get club            ***** USE CLUB WE ARE LOGGED INTO !!
  

   if (!userName.equals( support )) {

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


   try {

      stmt = con.createStatement();           // create a statement

      rs = stmt.executeQuery("SELECT username, memNum FROM member2b");

      while (rs.next()) {

         user = rs.getString(1);
         mNum = rs.getString(2);

         if (!mNum.equals( "" )) {        // make sure member has an mNum

            if (!mNum.endsWith( "0" ) && !mNum.endsWith( "1" ) && !mNum.endsWith( "2" ) && !mNum.endsWith( "3" ) &&
                !mNum.endsWith( "4" ) && !mNum.endsWith( "5" ) && !mNum.endsWith( "6" ) && !mNum.endsWith( "7" ) &&
                !mNum.endsWith( "8" ) && !mNum.endsWith( "9" )) {

               mNum = stripA(mNum);           // remove trailing alpha
            }
              
            if (mNum.length() == 1) {
               
               posid = "000" + mNum;           // mNum must be 4 digits
               
            } else if (mNum.length() == 2) {

               posid = "00" + mNum;          
               
            } else if (mNum.length() == 3) {

               posid = "0" + mNum;    
               
            } else {
               
               posid = mNum;    
            }
            
            posid = posid + "-000";               // add extension
            
            
            pstmt = con.prepareStatement (
                     "UPDATE member2b SET memNum = ?, posid = ? " +
                     "WHERE username = ?");

            pstmt.clearParameters();        
            pstmt.setString(1, mNum);
            pstmt.setString(2, posid);
            pstmt.setString(3, user);
            pstmt.executeUpdate();    

            pstmt.close();
         }
      }

      stmt.close();
        
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Error</H3>");
      out.println("<BR><BR>Error trying to update the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Upgrade Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Upgrade Complete</H3>");
   out.println("<BR><BR>The posids for club " + club + " have been updated.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }


 // *********************************************************
 //  Strip last letter from end of string
 // *********************************************************

 private final static String stripA( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      } // end for

      return new String (ca2);

 } // end stripA2


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
