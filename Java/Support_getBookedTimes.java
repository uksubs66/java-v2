/***************************************************************************************     
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_getBookedTimes extends HttpServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
        

       resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       PrintWriter out = resp.getWriter();


       Connection con = null;                 // init DB objects

       HttpSession session = null;

       //
       // Make sure user didn't enter illegally
       //
       session = req.getSession(false);  // Get user's session object (no new one)

       if (session == null) {

       }

       String user = (String)session.getAttribute("user");   // get username


       //
       // Load the JDBC Driver and connect to DB
       //
       String club = (String)session.getAttribute("club");   // get club name
       
       try {

          con = dbConn.Connect(club);
       }
       catch (Exception exc) {

          out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
          out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
          out.println("<BR><BR>Unable to connect to the DB.");
          out.println("<BR>Exception: "+ exc.getMessage());
          if (user.startsWith( "sales" )) {
             out.println("<BR><BR> <A HREF=\"/v5/sales_main.htm\">Return</A>.");
          } else {
             out.println("<BR><BR> <A HREF=\"/v5/support_main3.htm\">Return</A>.");
          }
          out.println("</CENTER></BODY></HTML>");
          return;
       }
       
       PreparedStatement pstmt = null;
       ResultSet rs = null;
       
       double proNew = 0.0;
       double memNew = 0.0;
       int totalNew = 0;
       int memMod = 0;
       String[] months = new String[12];
       months[0] = "January";
       months[1] = "February";
       months[2] = "March";
       months[3] = "April";
       months[4] = "May";
       months[5] = "June";
       months[6] = "July";
       months[7] = "August";
       months[8] = "September";
       months[9] = "October";
       months[10] = "November";
       months[11] = "December";
       
       String year = "2007";
       
       try {
           
           out.println("<table><tr><td><b>Month</b></td><td><b>Total Times</b></td><td><b>Proshop Times</b></td><td><b>Member Times</b></td><td><b>Member Modified</b></td></tr>");
           for (int t=0; t<2; t++) {
               if (t == 1) { year = "2008"; }
               
               for (int i=1; i<=12; i++) {

                   pstmt = con.prepareStatement("SELECT count(*) FROM teepast2 WHERE proNew=1 AND mm = ? AND yy = ?");
                   pstmt.clearParameters();
                   pstmt.setInt(1, i);
                   pstmt.setString(2, year);

                   rs = pstmt.executeQuery();

                   if (rs.next()) {
                       proNew = rs.getDouble(1);
                   }

                   pstmt.close();
                   
                   
                   pstmt = con.prepareStatement("SELECT count(*) FROM teepast2 WHERE memNew=1 AND mm = ? AND yy = ?");
                   pstmt.clearParameters();
                   pstmt.setInt(1, i);
                   pstmt.setString(2, year);

                   rs = pstmt.executeQuery();

                   if (rs.next()) {
                       memNew = rs.getDouble(1);
                   }

                   pstmt.close();

                   
                   pstmt = con.prepareStatement("SELECT count(*) FROM teepast2 WHERE memMod=1 AND mm = ? AND yy = ?");
                   pstmt.clearParameters();
                   pstmt.setInt(1, i);
                   pstmt.setString(2, year);

                   rs = pstmt.executeQuery();

                   if (rs.next()) {
                       memMod = rs.getInt(1);
                   }

                   pstmt.close();
                   
                   totalNew = (int)(proNew + memNew);
                   
                   double proPer = (proNew/totalNew)*100;
                   double memPer = (memNew/totalNew)*100;

                   String proOut = "";
                   String memOut = "";

                   out.println("<tr><td>" + months[i-1] + " " + year + "</td>" +
                       "<td>" + totalNew + "</td>" +
                       "<td>" + (int)proNew + " (" + (int)proPer + "%)</td>" +
                       "<td>" + (int)memNew + " (" + (int)memPer + "%)</td>" +
                       "<td>" + memMod + "</td></tr>");


               }
           }
           out.println("</table>");
       } catch (Exception e) {
           out.println("failure: " + e.getMessage());
       }
       
    }
    
}