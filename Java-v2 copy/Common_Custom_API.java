/***************************************************************************************
 *   Common_Custom_API:  This class is used to create a custom API for 3rd party providers
 *                       to access certain information in ForeTees.  This utilizes an 
 *                       interface similar to the seamless i/f in Login, but can be used
 *                       for others as well.
 *
 *
 *   created: 6/21/2013   Bob   For The DeBordieu Club - BlackSky Technologies
 *
 *   last updated:     
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.StringEncrypter;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;
import com.foretees.common.ProcessConstants;


public class Common_Custom_API extends HttpServlet {

 static String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //
 //   Must use doPost
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
   resp.setHeader("Pragma","no-cache");                                   
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");   
   resp.setDateHeader("Expires",0);    // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     

   String club = "";
   String caller = "";
   String zip_code = "";
   String errMsg = "";
   
   //
   //   Verify the caller and parameters passed
   //
   if (req.getParameter("caller") == null || req.getParameter("clubname") == null || req.getParameter("zip_code") == null) {
     
      errMsg = "One or more required parameters missing.";
     
      invalidAccess(errMsg, req, out);
      return;
   }
   
   
   //
   //  Get the required parameters
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");       // which club 
   }
   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");       // who is calling on us
   }
   if (req.getParameter("zip_code") != null) {

      zip_code = req.getParameter("zip_code");       // club's zip code for verification
   }

   //
   //  Now verify parm values for the specific API and go process
   //
   if (caller.equals("BLACKSKY") && club.equals("debordieuclub") && zip_code.equals("29440")) {
      
      doBlackSky(club, req, resp, out);
      
      
   } else {         // can add others here !!!!!!!!!!
      
      
      errMsg = "One or more required parameters missing.";
     
      invalidAccess(errMsg, req, out);
      return;      
   }
       
 }     // end of doPost processing
    
 
 //
 //   Must use doPost
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
   resp.setHeader("Pragma","no-cache");                                    
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");   
   resp.setDateHeader("Expires",0);    // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     
   invalidAccess("Invalid Access - Please contact ForeTees", req, out);
    
 }
   

 //
 //   BlackSky Technologies (The DeBordieu Club) - provide the current day's guest names for all guests on the tee sheet.
 //              They use this for creating temp badges at the guard gate.
 //
 private void doBlackSky(String club, HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {

   Connection con = null;                 // init DB objects
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   
   String player = "";
   String userg = "";
   String mNum = "";
   String memName = "";
   String line = "";
   
   
   //  Use the date and time for the file name to keep them unique
   
   Calendar cal = new GregorianCalendar();       // get current date/time
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int daynum = cal.get(Calendar.DAY_OF_MONTH);
   int hr = cal.get(Calendar.HOUR_OF_DAY);       // 24 hr clock (0 - 23)
   int min = cal.get(Calendar.MINUTE);
   int sec = cal.get(Calendar.SECOND);

   int date = (year * 10000) + (month * 100) + daynum;      // create a date field of yyyymmdd

   int time = (hr * 10000) + (min * 100) + sec;  

   String filename = "ForeTees-" +date+ "-" +time;   
   
   //
   //  Get a connection to this club's db
   //
   try {
      
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

      invalidAccess("Database connection error - Please contact ForeTees", req, out);
   }   
   
   //   setup so we create a csv file for the response
   resp.setContentType("text/csv");                         // text file
   resp.setHeader("Content-Disposition", "attachment;filename=\"" +filename+ "\"");         // default file name

   //
   //   Get all guest names from today's tee sheet and create the csv file
   //
   try { 
   
      pstmt = con.prepareStatement (
         "SELECT time, player1 AS player, userg1 AS userg FROM teecurr2 WHERE date = ? AND player1<>'' AND player1<>'x' AND username1='' " +
         "UNION ALL " +
         "SELECT time, player2 AS player, userg2 AS userg FROM teecurr2 WHERE date = ? AND player2<>'' AND player2<>'x' AND username2='' " +
         "UNION ALL " +
         "SELECT time, player3 AS player, userg3 AS userg FROM teecurr2 WHERE date = ? AND player3<>'' AND player3<>'x' AND username3='' " +
         "UNION ALL " +
         "SELECT time, player4 AS player, userg4 AS userg FROM teecurr2 WHERE date = ? AND player4<>'' AND player4<>'x' AND username4='' " +
         "UNION ALL " +
         "SELECT time, player5 AS player, userg5 AS userg FROM teecurr2 WHERE date = ? AND player5<>'' AND player5<>'x' AND username5='' " +
         "ORDER BY time, player");

      pstmt.clearParameters();        
      pstmt.setLong(1, date);       // use today's date
      pstmt.setLong(2, date);     
      pstmt.setLong(3, date);     
      pstmt.setLong(4, date);     
      pstmt.setLong(5, date);     

      rs = pstmt.executeQuery();      

      while (rs.next()) {   

         player = rs.getString("player");      // get guest player
         userg = rs.getString("userg");        // get username of member, if assigned
         
         memName = "Unknown";                 // defaults in case no member assigned to guest
         mNum = "Unknown";
         
         if (!userg.equals("")) {          // if memebr assigned to guest
            
            try {
            
               pstmt2 = con.prepareStatement (
                  "SELECT name_last, name_first, name_mi, memNum FROM member2b WHERE username = ?");

               pstmt2.clearParameters();        
               pstmt2.setString(1, userg);      

               rs2 = pstmt2.executeQuery();      

               if (rs2.next()) {   

                  mNum = rs2.getString("memNum");    
                  
                  StringBuffer mem_name = new StringBuffer(rs2.getString("name_first"));  // get first name

                  String mi = rs2.getString("name_mi");                                // middle initial
                  if (!mi.equals( "" )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs2.getString("name_last"));                     // last name

                  memName = mem_name.toString();                          // convert to one string
               }
            
               pstmt2.close();
            
            } catch (Exception exc) {

               Utilities.logError("DB Error2 in Common_Custom_API: "+ exc.getMessage());

            } finally {

                  try { pstmt2.close(); }
                  catch (Exception ignore) {}
            }
         }
         
         //
         //  Create a record in the csv file
         //
         StringBuffer tempSB = new StringBuffer(player);   // put player (guest) in string buffer
         tempSB.append(",");                          
         tempSB.append(memName);                           // member name
         tempSB.append(",");
         tempSB.append(mNum);                              // member number

         line = tempSB.toString();                         // save as string value

         out.print(line);
         out.println();      // output the line
      }
      
      pstmt.close();
      
   } catch (Exception exc) {

      Utilities.logError("DB Error in Common_Custom_API: "+ exc.getMessage());
      
      resp.setContentType("text/html");                   // normal html response
      resp.setHeader("Content-Disposition", "inline");    // undo attachment change from above

      out.println(SystemUtils.HeadTitle("ForeTees Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><H3>ForeTees Database Error</H3>");
      out.println("<br>Please contact ForeTees if problem continues.");
      out.println("</CENTER></BODY></HTML>");
   
   } finally {

         try { pstmt.close(); }
         catch (Exception ignore) {}
   }
   
   out.close();              // close the file

   try {

      resp.flushBuffer();      // make sure the repsonse completes
   }
   catch (Exception ignore) {
   }

 }   // end of doBlackSky
   
 
 
 private void invalidAccess(String errMsg, HttpServletRequest req, PrintWriter out) {

      Utilities.logError("Common_Custom_API: Access Error - " + errMsg);

      out.println(SystemUtils.HeadTitle("Invalid Access"));
      out.println("<BODY><CENTER>");
      out.println("<p>&nbsp;</p><p>&nbsp;</p>");
      out.println("<BR><H2>Access Rejected</H2><BR>");
      out.println("<BR>Some information you provided was either missing or invalid.");
      out.println("<BR><BR>Error: " +errMsg);
      out.println("<BR><BR>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }
   
}