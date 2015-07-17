/***************************************************************************************     
 *   Support_upgrade_insert_medinah:  
 *
 *
 *       Insert tee times to Specific Tee Sheets
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


public class Support_upgrade_insert_medinah extends HttpServlet {
                           
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
        
   Connection con = null;                 // init DB objects
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
     
   HttpSession session = null; 

   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String userName = (String)session.getAttribute("user");  

   if (!userName.equals( "support" )) {

      invalidUser(out);            
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "medinahcc";                       //  !!!!!!!!!!!!  CHANGE PER CLUB !!!!!!!!!!!!!!!!!!!!
   
   
   if (req.getParameter("continue") == null) {     // if first time thru here

      //
      //  Prompt user to make sure this is what they want to do !!!!!!!!!!!!!!!!
      //
      out.println("<HTML><HEAD><TITLE>Upgrade Prompt User</TITLE></HEAD>");     // **** Change this MESSAGE **********
      out.println("<BODY><CENTER><H3>Support Upgrade</H3>");
      out.println("<BR><BR>This job is for: " +club);
      out.println(" and it will insert tee times in specific tee sheets.");     // change this line!
      out.println("<BR><BR>Do you wish to continue?");
      out.println("<BR><BR>");
      out.println("<form action=\"Support_upgrade_insert_medinah\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"continue\" value=\"continue\">");
      out.println("<input type=\"submit\" value=\"Yes - Continue\"></form>");
      
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">No - Return</A>.");
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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   String day = "Friday";     // first day
   
   int yy = 2009;              // start date
   int mm = 5;
   int dd = 15;
   
   int time = 630;             // first time
   int hr = 6;
   int min = 30;

   int interval = 10;

   long date = 0;
   long sdate = 20090514;
  

   Calendar cal = new GregorianCalendar();                // get todays date
   cal.set(Calendar.YEAR,yy);                             // set year in cal
   cal.set(Calendar.MONTH,mm-1);                          // set month in cal
   cal.set(Calendar.DAY_OF_MONTH,dd);                     // set day in cal

   date = (yy * 10000) + (mm * 100) + dd;              // start with 20090513

  
   //
   //   First delete all the empty tee times on Tues - Fri
   //
   //   Then insert new tee times with 10 minute intervals
   //
   try {
      
      pstmt2 = con.prepareStatement (
               "DELETE FROM teecurr2 WHERE date > ? AND player1 = '' AND courseName = 'No 3' AND " +
               "(day = 'Tuesday' OR day = 'Wednesday' OR day = 'Thursday' OR day = 'Friday')");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, sdate);   // put the parms in stmt for tee slot
      pstmt2.executeUpdate();          // delete all tee slots for oldest date

      pstmt2.close();

            

      while (date < 20100505) {                             // end date !!!!!!!!!!
         
         if (day.equals("Tuesday") || day.equals("Wednesday") || day.equals("Thursday") || day.equals("Friday")) {
         
            while (time < 1801) {                        // until 6:00 PM

               
               SystemUtils.insertTee(date, time, 0, "No 3", day, con);     // insert new tee time


               min += interval;                      // do next time!!!!!!!!!!!

               if (min > 59) {

                  min = min - 60;     // adjust past hour count
                  hr++;
               }

               time = (hr * 100) + min;    
            }
            
         }
               
         cal.add(Calendar.DATE,1);                     // get next day
         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH) +1;
         dd = cal.get(Calendar.DAY_OF_MONTH);

         date = (yy * 10000) + (mm * 100) + dd;            
   
         int dayNum = cal.get(Calendar.DAY_OF_WEEK);               // day of week
     
         day = day_table[dayNum];                         // get name for day

         interval = 10;
         
         hr = 6;
         min = 30;
         time = 630;                      // start with 7:30

      }    // end of WHILE date
   
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
   out.println("<BODY><CENTER><H3>Upgrade Complete</H3>");
   out.println("<BR><BR>The tee times have been added for club " +club+ ".");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }   
                                 

 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 private void logError(String msg, String club) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//error.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(new java.util.Date() + space + msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\error.txt", true));

         //
         //  Put header line in text file
         //
         fout.print(new java.util.Date() + space + msg);
         fout.println();      // output the line

         fout.close();
      }
      catch (Exception ignore) {
      }
   }
 }  // end of logError


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>" + e.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
