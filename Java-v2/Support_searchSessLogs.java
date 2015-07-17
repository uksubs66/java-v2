/***************************************************************************************
 *   Support_searchSessLogs:  This servlet will search each club's session log for the entered phrase and .
 *
 *
 *       Adds a column to a db table for ALL clubs
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

import com.foretees.common.Utilities;

public class Support_searchSessLogs extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    String support = "support";             // valid username

    HttpSession session = null;
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
   
   out.println("<HTML><HEAD><TITLE>Search Session Logs</TITLE></HEAD>");
   out.println("<BR><BR><BR><BR><center><h2><b>Session Log Search</b></h2></center><br><br>");
   out.println("<table border=\"0\" align=\"center\" cellpadding=\"4\">");
   out.println("<tr><td align=\"center\">Enter a search string:</td></tr>");

   out.println("<tr><td align=\"center\">");
   out.println("<form action=\"/v5/servlet/Support_searchSessLogs\" method=\"post\">");
   out.println("<input type=\"text\" name=\"searchString\" size=\"30\" value=\"\">");
   out.println("</td></tr><tr><td align=\"center\">");
   out.println("<input type=submit value=\"Search\">");
   out.println("</form>");
   out.println("</td></tr>");

   out.println("<tr><td align=\"center\">");
   out.println("<BR><BR> <A HREF=\"/v5/support_main2.htm\">Return</A><BR><BR>");
   out.println("</td></tr>");
   out.println("</table>");

   out.println("</BODY></HTML>");
   
   out.close();
   
 }
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    //PrintWriter out = new PrintWriter(resp.getOutputStream());


    String support = "support";             // valid username

    HttpSession session = null;
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

   String searchString = req.getParameter("searchString");

   if (!searchString.equals("")) {

        Connection con1 = null;                  // init DB objects
        Connection con2 = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        Statement stmt1 = null;
        ResultSet rs1 = null;

        String club = "";

        try {

            con1 = dbConn.Connect(rev);
        } catch (Exception exc) {

            // Error connecting to db....
            out.println("<BR><BR>Unable to connect to the DB.");
            out.println("<BR>Exception: "+ exc.getMessage());
            out.println("<BR><BR> <A HREF=\"/v5/support_main2.htm\">Return</A>.");
            out.println("</BODY></HTML>");
            return;
        }


        //
        // Get the club names from the 'clubs' table
        //
        //  Process each club in the table
        //
        int x1 = 0;
        int x2 = 0;

        String msg = "";
        String errmsg = "";
        String bgcolor = "#F5F5DC";
        String bgcolor1 = "#F5F5DC";
        String bgcolor2 = "#CCCCAA";

        String bgcolor3 = "#F5F5DC";
        String bgcolor3a = "#F5F5DC";
        String bgcolor3b = "#CCCCAA";

        out.println("<HTML><HEAD><TITLE>Search Session Logs</TITLE></HEAD>");
        out.println("<BR><BR><center><h2><b>Results Found</b></h2></center>");
        out.println("<br><center><A HREF=\"/v5/support_main2.htm\">Return</A></center><br>");

        out.println("<table border=\"1\" cellpadding=\"4\" align=\"center\">");

        boolean flip = false;

        try {


            flip = (flip == false);
            bgcolor3 = (flip) ? bgcolor3a : bgcolor3b;

            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname, (SELECT COUNT(*) FROM v5.clubs) AS total FROM v5.clubs ORDER BY clubname");

            while (rs1.next()) {

                x1++;

                club = rs1.getString(1);                // get a club name
                x2 = rs1.getInt(2);                     // get count of clubs

                pstmt = con1.prepareStatement("UPDATE v5.update_status SET club = ?");
                pstmt.clearParameters();
                pstmt.setString(1, club);
                pstmt.executeUpdate();

                con2 = dbConn.Connect(club);            // get a connection to this club's db

                msg = searchLog(searchString, con2);

                if (!msg.equals("")) {

                    if (msg.startsWith("ERR:")) {
                        errmsg += "<br>" + club + " - Error encountered - " + msg;
                    } else {
                        out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");
                        out.println("<font size=\"2\"> <b>" + club + "</b> </font>");
                        out.println("</td>");
                        out.println("<td align=\"left\" nowrap bgcolor=\""+ bgcolor3 +"\">");
                        out.println("<font size=\"2\">" + msg + "</font>"); // + " " + stime
                        out.println("</td></tr>");
                    }
                }

                con2.close();

            } // loop all clubs

            if (!errmsg.equals("")) {
                out.println("<table border=\"0\" cellpadding=\"4\" align=\"center\">");
                out.println("<tr><td align=\"center\"><h3><b>Errors Encountered</b></h3></td></tr>");
                out.println("<tr><td>" + errmsg + "</td></tr>");
            }

            out.println("</table>");

            stmt1.close();
            con1.close();

        }
        catch (Exception e) {

            // Error connecting to db....

            out.println("<BR><BR><H3>Fatal Error!</H3>");
            out.println("Error performing search on '" + club + "'.");
            out.println("<BR>Exception: "+ e.getMessage());
            out.println("<BR>Message: "+ e.toString());
            out.println("<BR><BR> <A HREF=\"/v5/support_main2.htm\">Return</A>.");
            out.println("</BODY></HTML>");
            out.close();
            return;
        }

        out.println("<br><center><A HREF=\"/v5/support_main2.htm\">Return</A></center><br>");

        out.println("</CENTER></BODY></HTML>");
   }
 }

 private String searchLog(String searchString, Connection con) {

     Statement stmt = null;
     ResultSet rs = null;
     
     String msg = "";

     try {

         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT msg FROM sessionlog WHERE msg LIKE '%" + searchString + "%' LIMIT 1");

         if (rs.next()) {
             msg = rs.getString("msg");
         }

     } catch (Exception exc) {
         msg = "ERR: " + exc.toString();
     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { stmt.close(); }
         catch (Exception ignore) { }
     }

     return msg;

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
   out.println("<BR><BR>Please <A HREF=\"/v5/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

 /*
 private static Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }
 */
}
