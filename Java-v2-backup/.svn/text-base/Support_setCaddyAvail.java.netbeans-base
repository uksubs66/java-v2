/***************************************************************************************     
 * 
 *   Support_setCaddyAvail:  This servlet will create availability entries 
 *      for all caddies on a demo/notify caddie site for the next 2 weeks.  
 *      Any existing entries from today forward will be removed.
 * 
 *      Created By:  BSK * 
 * 
 *       5/09/12   Removed portion where we limited this job to certain clubs.
 *       5/08/12   The Country Club - Brookline (tcclub) - Added tcclub to the list of clubs the set caddy avail job can be run for.
 *       1/20/12   Updated to work with all sales logins, instead of just 'sales'
 *      11/15/11   Servlet Created
 * 
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.Utilities;
import com.foretees.common.LoginCredentials;
import com.foretees.common.Connect;


public class Support_setCaddyAvail extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection c_con = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   
   int yy = 0;
   int mm = 0;
   int dd = 0;
   int date = 0;
   int caddie_autoid = 0;
   
   boolean first = true;
     
   HttpSession session = null; 

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {
       
      invalidUser(out);            // Intruder - reject
      return;
   }

   String support = LoginCredentials.support;;
   String sales = LoginCredentials.sales;;

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.startsWith(sales)) {

      invalidUser(out);            // Intruder - reject
      return;
   }
   
   String DB_PARMS = "?jdbcCompliantTruncation=false&autoReconnect=true&tcpKeepAlive=true";
   

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {

      if (req.getParameter("beta") != null) {       // request for beta site for testing (pebblebeach42) ?

          Class.forName("com.mysql.jdbc.Driver").newInstance();

          // the user/pass needs to be the same as in /srv/www/vhosts/caddie.foretees.com/1/host_club_information.php
          c_con = DriverManager.getConnection("jdbc:mysql://10.0.0.40/pebblebeach42" + DB_PARMS, "caddyapp", "caddypass");

      } else {

         c_con = Connect.getCaddieCon(club);      // connect to normal caddie site
      }

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());

      if (user.equalsIgnoreCase(support)) {
          out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      } else if (user.startsWith(sales)) {
          out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   Calendar cal = new GregorianCalendar();

   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH) + 1;
   dd = cal.get(Calendar.DAY_OF_MONTH);

   date = (yy * 10000) + (mm * 100) + dd;       // create value of yyyymmdd

   // Clear out any existing availability entries (today forward)
   try {

       pstmt = c_con.prepareStatement("DELETE FROM caddy_picks WHERE DATE_FORMAT(date, '%Y%m%d') >= ?");
       pstmt.clearParameters();
       pstmt.setInt(1, date);

       pstmt.executeUpdate();

   } catch (Exception exc) {
       Utilities.logError("Support_setCaddyAvail.doGet - " + club + " - Error clearing existing availability entries - ERR: " + exc.toString());
   } finally {

       try { pstmt.close(); }
       catch (Exception ignore) { }
   }

   try {

       stmt = c_con.createStatement();
       rs = stmt.executeQuery("SELECT autoid FROM caddies");

       while (rs.next()) {

           first = true;

           caddie_autoid = rs.getInt("autoid");

           Calendar cal2 = new GregorianCalendar();

           for (int i=0; i<14; i++) {       // Create availability entries for the next 2 weeks

               if (!first) {        // Roll forward a day
                   cal2.add(Calendar.DATE, 1);
               }  else {
                   first = false;
               }

               yy = cal2.get(Calendar.YEAR);
               mm = cal2.get(Calendar.MONTH) + 1;
               dd = cal2.get(Calendar.DAY_OF_MONTH);

               date = (yy * 10000) + (mm * 100) + dd;       // create value of yyyymmdd

               try {

                   pstmt = c_con.prepareStatement("INSERT INTO caddy_picks (date, caddy_autoid, created_on, available_am, modified_on, available_pm) VALUES (?,?,now(),1,now(),1)");
                   pstmt.clearParameters();
                   pstmt.setInt(1, date);
                   pstmt.setInt(2, caddie_autoid);

                   pstmt.executeUpdate();

               } catch (Exception exc) {
                   Utilities.logError("Support_setCaddyAvail.doGet - " + club + " - Error inserting availability for id=" + caddie_autoid + ", date=" + date + " - ERR: " + exc.toString());
               } finally {

                   try { pstmt.close(); }
                   catch (Exception ignore) { }
               }
           }
       }

   } catch (Exception exc) {
       Utilities.logError("Support_setCaddyAvail.doGet - " + club + " - Error getting caddie ids - ERR: " + exc.toString());
   } finally {

       try { rs.close(); }
       catch (Exception ignore) { }

       try { stmt.close(); }
       catch (Exception ignore) { }
   }


   out.println("<HTML><HEAD><TITLE>Availability Entries Built</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Availability Entries Built</H3>");
   out.println("<BR><BR>All caddies have been set available for the next 2 weeks.");
   if (user.equalsIgnoreCase(support)) {
       out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
   } else if (user.startsWith(sales)) {
       out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
   }
   out.println("</CENTER></BODY></HTML>");
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
   out.println("<BR><BR> <A HREF=\"javascript:history.back(1)\">Return</A>");
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
   out.println("<BR><BR><a href=\"javascript:history.back(1)\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }
 
}
