/***************************************************************************************
 * 
 *   Support_reorder_gtypes: 
 *
 *
 *    This will delete all the guest types and then re-insert them in the order 
 *    requested by the club.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_reorder_gtypes extends HttpServlet {


 // Process the form request from support_upgrade.htm.....

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   Statement stmt = null;

   String support = "support";             // valid username

   HttpSession session = null;


   String guest = "";
   String gpos = "";
   String g9pos = "";
   String gstItem = "";
   String gst9Item = "";
   
   int activity_id = 0;
   int gOpt = 0;
   int revenue = 0;
   int use_guestdb = 0;
     

   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String userName = (String)session.getAttribute("user");   // get username
   
   
   String club = "xyzclub";       // get club                                   ***** specify club here !!!!!!!!!!
  

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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   try {

      stmt = con.createStatement();        

      stmt.executeUpdate("DELETE FROM guest5");          // DELETE the existing guest types

      stmt.close();
     
            
      //
      //  Now add the guest types back in the order specified
      //
      guest = "WkDay Tue-Thur";
      gpos = "";
      g9pos = "";
      gstItem = "7051";
      gst9Item = "7052"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Direct Family";
      gpos = "";
      g9pos = "";
      gstItem = "7061";
      gst9Item = "7062"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Comp";
      gpos = "";
      g9pos = "";
      gstItem = "83461";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Trny Guest";
      gpos = "";
      g9pos = "";
      gstItem = "";
      gst9Item = "tg0000"; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Outing Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83475";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Employee";
      gpos = "";
      g9pos = "";
      gstItem = "0001J4";
      gst9Item = "0001J4"; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Recip Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83472";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "PGA Comp";
      gpos = "";
      g9pos = "";
      gstItem = "83471";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Junior Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83473";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "O of S Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83474";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "4 or More Guest";
      gpos = "";
      g9pos = "";
      gstItem = "0000JG";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Unaccompanied Guest";
      gpos = "";
      g9pos = "";
      gstItem = "0004om";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "WkEnd Sat-Sun";
      gpos = "";
      g9pos = "";
      gstItem = "7053";
      gst9Item = "7054"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Holiday";
      gpos = "";
      g9pos = "";
      gstItem = "7059";
      gst9Item = "7060"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Friday Guest";
      gpos = "";
      g9pos = "";
      gstItem = "7055";
      gst9Item = "7056"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "WFG WkDay Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83464";
      gst9Item = "83465"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Prem Weekday";
      gpos = "";
      g9pos = "";
      gstItem = "83466";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "WFG Saturday Guest";
      gpos = "";
      g9pos = "";
      gstItem = "83462";
      gst9Item = "83463"; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Prem Weekend";
      gpos = "";
      g9pos = "";
      gstItem = "83467";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 0;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "Men 1 Day M/G";
      gpos = "";
      g9pos = "";
      gstItem = "00CC33";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "9 Hole Mixed M/G";
      gpos = "";
      g9pos = "";
      gstItem = "";
      gst9Item = ""; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "O/S to 4/15 Weekday";
      gpos = "";
      g9pos = "";
      gstItem = "306630";
      gst9Item = "306632"; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "O/S to 4/15 Weekend";
      gpos = "";
      g9pos = "";
      gstItem = "306631";
      gst9Item = "306633"; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
     
      //
      guest = "School Play";
      gpos = "";
      g9pos = "";
      gstItem = "83461";
      gst9Item = "83461"; 
      activity_id = 0;
      gOpt = 1;
      revenue = 0;
      use_guestdb = 0;
     
      insertGst(guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb, con);     
           
            
   }
   catch (Exception e2) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Error</H3>");
      out.println("<BR><BR>Error trying to update the DB - error2.");
      out.println("<BR>Exception: "+ e2.getMessage());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   try {      
      con.close();                           // close the connection to the club db
   }
   catch (Exception ignore) {
   }
            
   out.println("<HTML><HEAD><TITLE>Reorder Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Reorder Complete</H3>");
   out.println("<BR><BR>The Guest Types for club " + club + " have been reorded.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }




 // *********************************************************
 // Insert the guest type into guest5
 // *********************************************************

 private void insertGst(String guest, int activity_id, int gOpt, String gpos, String g9pos, String gstItem, String gst9Item, int revenue, int use_guestdb, Connection con)
              throws Exception {

   PreparedStatement pstmt = null;
        
    try {
       
      pstmt = con.prepareStatement (
               "INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

      pstmt.clearParameters();        
      pstmt.setString(1, guest);
      pstmt.setInt(2, activity_id);
      pstmt.setInt(3, gOpt);
      pstmt.setString(4, gpos);
      pstmt.setString(5, g9pos);
      pstmt.setString(6, gstItem);
      pstmt.setString(7, gst9Item);
      pstmt.setInt(8, revenue);
      pstmt.setInt(9, use_guestdb);
      pstmt.executeUpdate();    

      pstmt.close();
        
   }
   catch (Exception e) {

      throw new Exception("Error inserting guest type " +guest+ ". " + e.getMessage());
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
