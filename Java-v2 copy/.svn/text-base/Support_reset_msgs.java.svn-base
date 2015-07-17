/***************************************************************************************
 *   Support_rest_msgs:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *
 *       Clear the 'message' field in member2b for ALL clubs
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


public class Support_reset_msgs extends HttpServlet {


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
   
   out.println("<HTML><HEAD><TITLE>Database Upgrade</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>WARNING: PERMENT DATABASE CHANGES PENDING!</H3>");
   out.println("<BR><BR>This job will clear the message field in member2b for all members in all clubs.");
   out.println("<BR><BR>Click 'Update' to start the job.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A><BR><BR>");
   
   out.println("<form method=post><input type=submit value=\"Update\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" <input type=hidden value=\"update\" name=\"todo\"></form>");
   
   out.println("<form method=post><input type=submit value=\"  Test  \">");
   out.println(" <input type=hidden value=\"test\" name=\"todo\"></form>");
   
   out.println("</CENTER></BODY></HTML>");
   
   out.close();
   
 }
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
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

   String action = "";
   if (req.getParameter("todo") != null) action = req.getParameter("todo");
   
   if (action.equals("update")) {
       
       doUpdate(out);
       return;
   }
   
   if (action.equals("test")) {
       
       doTest(out);
       return;
   }
   
   out.println("<p>Nothing to do.</p>todo="+action);
   
 }


 private void doUpdate(PrintWriter out) {
     

    Connection con1 = null;                  // init DB objects
    Connection con2 = null;
    PreparedStatement pstmt = null;
    Statement stmt1 = null;
    Statement stmt1a = null;
    Statement stmt2 = null;
    Statement stmt3 = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    out.println("<HTML><HEAD><TITLE>Database Update</TITLE></HEAD>");
    out.println("<BODY><H3>Starting Job to Clear ALL Members' Message Field</H3>");
    out.flush();

    String club = "";

    try {

        con1 = dbConn.Connect(rev);
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
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
    int i = 0;
  
    boolean skip = true;
    
    try {
       
        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

        while (rs1.next()) {

            x1++; 
            club = rs1.getString(1);                // get a club name

            con2 = dbConn.Connect(club);            // get a connection to this club's db
       
            stmt2 = con2.createStatement();
            stmt2.executeUpdate("UPDATE member2b SET message = ''");    // clear message field for all members

            out.println("<br><br>");
            out.print("[" + x1 + "] Starting " + club);
            
            stmt2.close();
            con2.close();
        }
            
        stmt1.close();
        con1.close();
      
   }
   catch (Exception e) {

      // Error connecting to db....

      out.println("<BR><BR><H3>Fatal Error!</H3>");
      out.println("Error performing update to club '" + club + "'.");
      out.println("<BR>Exception: "+ e.getMessage());
      out.println("<BR>Message: "+ e.toString());
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
      out.println("</BODY></HTML>");
      out.close();
      return;
   }

   out.println("<BR><BR>Upgrade Finished!  The message fields have been cleared for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   //out.flush();
   //out.close();
    
 }
 
 
 
 private void doTest(PrintWriter out) {

    Connection con1 = null;                  // init DB objects
    Connection con2 = null;
    Statement stmt1 = null;
    Statement stmt2 = null;
    PreparedStatement pstmt = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    int i = 0;
    int t = 0;
    int c = 0;
    boolean found = false;
    String club = "";
    
    out.println("<HTML><HEAD><TITLE>Database Test</TITLE></HEAD>");
    out.println("<BODY><H3>Starting DB Test...</H3>");
    out.flush();

    
    /*
    
    try {

        con1 = dbConn.Connect("v5");
        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

        while (rs1.next()) {

            c++;
            club = rs1.getString(1);                // get a club name
            con2 = dbConn.Connect(club);             // get a connection to this club's db
            stmt2 = con2.createStatement();           // create a statement
            i = 0;
            found = false;
            
            // find out how many clubs have members with same name
            //rs2 = stmt2.executeQuery("select * from (select fullname, count(*) as c from (select CONCAT_WS(' ,', name_first, name_mi, name_last) as fullname from member2b)as t group by fullname) as t2 where c > 1;");
            rs2 = stmt2.executeQuery("select count(*) as c, date, event from teepast2 where courseName = \"-ALL-\" GROUP by date with rollup;");
            
            
            while (rs2.next()) {

                if (!found) {
                    out.print("<p><b><font size=+1><u>" + club + "</u></font></b><p>");
                    out.println("<table border=1>");
                    found = true;
                    t++;
                }
                
                if (rs2.getString("date") == null) {
                    
                    out.println("<tr><td colspan=3><i>" + rs2.getString("c") + " total tee times with -ALL- in courseName</i></td></tr>");
                } else {
                    
                    out.println("<tr><td>" + rs2.getString("c") + " x</td><td>" + rs2.getString("date") + "</td><td>" + rs2.getString("event") + "&nbsp;</td></tr>");            
                }
                
            }
            
            
            if (found) out.println("</table><hr>");
            
            stmt2.close(); 
            con2.close();
            
            out.flush();

        } // loop all clubs

        out.println("<p><i>Found " + t + " of " + c + " clubs that contain -ALL- in their teepast2 table.</i></p>");

        stmt2.close(); 
        con2.close();
        stmt1.close();
        con1.close();

    } catch (Exception e) {

        // Error connecting to db....
        out.println("<BR><BR><H3>Fatal Error!</H3>");
        out.println("Error performing update to club '" + club + "'.");
        out.println("<BR>Exception: "+ e.getMessage());
        out.println("<BR>Message: "+ e.toString());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    
    
    
    
    
    
    
    /*
    try {

        con1 = dbConn.Connect("demov4");
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }

    String sql = "SELECT DATE_FORMAT('20060701', '%W %M %D, %Y') AS d1";
    out.println("<p>"+sql+"</p>");
    
    try {
        
        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery(sql);
     
        if (rs1.next()) {
            
            out.print("d1="+rs1.getString(1));
        }
        
    } catch (Exception e) {

        // Error connecting to db....
        out.println("<BR><BR><H3>Fatal Error!</H3>");
        out.println("<BR>Loop: "+ i);
        out.println("<BR>Exception: "+ e.getMessage());
        out.println("<BR>Message: "+ e.toString());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    */
    
    
    
    
    /*
    String test = "233";
    int test3 = 233;
    String test2 = "";
    int i = 0;
    try {
        
        pstmt = con1.prepareStatement("SELECT * FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, test3);
        rs1 = pstmt.executeQuery();
        
        while (rs1.next()) {

            test2 = rs1.getString("username");
            out.println("<br>" + i + " Found " + test2);
            i++;
        }

        pstmt.close();
        con1.close();

    } catch (Exception e) {

        // Error connecting to db....
        out.println("<BR><BR><H3>Fatal Error!</H3>");
        out.println("<BR>Loop: "+ i);
        out.println("<BR>Exception: "+ e.getMessage());
        out.println("<BR>Message: "+ e.toString());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    */
    

    int tmp_total = 0;
 
    try {

        con1 = dbConn.Connect(rev);
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }

    try {

        stmt1 = con1.createStatement();
        rs1 = stmt1.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

        while (rs1.next()) {

            club = rs1.getString(1);                // get a club name
            con2 = dbConn.Connect(club);             // get a connection to this club's db
            stmt2 = con2.createStatement();           // create a statement
            i = 0;
            found = false;
            int x = 0;
            int prob = 0;

            String [] gtypes = new String[99];
            String response = "";
            String problem = "";

            //out.println("<br><br>Starting " + club);

            
            
            // FIND CLUBS WITH DUPLICATE PARTNER LISTS
            rs2 = stmt2.executeQuery("SELECT * FROM events2b WHERE fb <> 'Front' AND fb <> 'Back' AND fb <> 'Both'");
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getString("name") + "&nbsp;&nbsp;" + rs2.getString("fb"));
                
            }
            
            
/*            
            // FIND CLUBS WITH DUPLICATE PARTNER LISTS
            rs2 = stmt2.executeQuery("SELECT * FROM (SELECT username, COUNT(*) AS c FROM (SELECT username FROM buddy) AS t GROUP BY username) AS t2 WHERE c > 1;");           
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getInt("c") + "&nbsp;&nbsp;" + rs2.getString("username"));
                
            }
*/            
            
/*            
  
  
 
            // FIND CLUBS WITH DUPLICATE BLOCKER NAMES
            rs2 = stmt2.executeQuery("SELECT * FROM (SELECT name, COUNT(*) AS c FROM (SELECT name FROM block2) AS t GROUP BY name) AS t2 WHERE c > 1;");           
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getInt("c") + "&nbsp;&nbsp;" + rs2.getString("name"));
                
            } 
  
 
            //if (club.equals("demov4")) {

                // load up all the guest types for this club
                rs2 = stmt2.executeQuery("SELECT guest FROM guest5");

                while (rs2.next()) {

                    gtypes[x] = rs2.getString(1).toLowerCase();
                    x++;
                }

                int g = x - 1;
                if (g>36) {
                    out.println(" <b>has " + g + " guest types!</b>: ");
                } else {
                    out.println(" has " + g + " guest types: ");
                }

                // check up for duplicate guest types
                for (x=0; x<=g; x++) {

                    for (int x2=0; x2<=g; x2++) {
                        //if (gtypes[x].substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x2])) {
                        if (x != x2 && gtypes[x].startsWith(gtypes[x2])) {

                            problem = problem + "<br>&nbsp; &nbsp; " + gtypes[x2] + " = " + gtypes[x];
                            prob++;
                        }
                    }
                }

            //}

            if (prob > 0) {
                tmp_total++;
                out.println("<b>" + prob + " problems:</b>" + problem);
            } else {
                out.println("no problems");
            }
*/
                
                
                

/*
            // FIND ALL CLUBS THAT HAVE teecurr2.auto_blocked SET TO ALLOW NULLS
            rs2 = stmt2.executeQuery("DESCRIBE teecurr2");
            
            while (rs2.next()) {
                
                if (rs2.getString(1).equals("auto_blocked")) {
                    if (rs2.getString(3).equals("YES")) {
                        
                        out.println("<br>" + club + " appears to allow null values.");
                    }
                }
            }
*/
            
/*            
            //rs2 = stmt2.executeQuery("SELECT * FROM (SELECT count(*) AS c, id FROM lreqs3 group by id) AS t WHERE c > 1;");
            rs2 = stmt2.executeQuery("select * from member2b where (email <> '' and email not like '%@%') OR (email2 <> '' and email2 not like '%@%');");
            
            while (rs2.next()) {
                
                if (!found) {
                    out.print("<p><b><font size=+1><u>" + club + "</u></font></b><p>");
                    out.println("<table border=1>");
                    out.println("<tr><td>Username</td><td>Email 1</td><td>Email 2</td></tr>");
                    found = true;
                    t++;
                }
                
                out.println("<tr><td>" + rs2.getString("username") + "</td><td>" + rs2.getString("email") + "</td><td>" + rs2.getString("email2") + "</td></tr>");
                i++;
                
            }
            
            if (found) out.println("</table><p><i>Found " + i + " bad email addresses.</i></p><hr>");
            
*/
            
/*
    // CHECK FOR DUPLICATE MEMBER NAMES
    
            // find out how many clubs have members with same name
            //rs2 = stmt2.executeQuery("select * from (select fullname, count(*) as c from (select CONCAT_WS(' ,', name_first, name_mi, name_last) as fullname from member2b)as t group by fullname) as t2 where c > 1;");
            rs2 = stmt2.executeQuery("select * from (select username, fullname, count(*) as c from (select username, CONCAT_WS(' ,', name_first, name_mi, name_last) as fullname from member2b) as t group by username) as t2 where c > 1");
            
            
            while (rs2.next()) {

                if (!found) {
                    out.print("<p><b><font size=+1><u>" + club + "</u></font></b><p>");
                    out.println("<table border=1>");
                    found = true;
                    t++;
                }
                
                //out.println("<tr><td>" + rs2.getString("c") + " x</td><td>" + rs2.getString("username") + "</td></tr>");
                out.println("<tr><td colspan=2>" + rs2.getString("c") + " \"" + rs2.getString("username") + "\" usernames</td></tr>");
                out.println("<tr><td></td><td>name</td><td>memNum</td><td>posid</td><td>webid</td><td>ghin</td><td>count</td></tr>");
                i++;
                
                pstmt = con2.prepareStatement("SELECT CONCAT_WS(' ', name_first, name_last) as fullname, webid, memNum, count, posid, ghin FROM member2b WHERE username = ?;");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("username"));
                rs3 = pstmt.executeQuery();
                
                while (rs3.next()) {
                    
                    out.println("<tr><td></td><td>" + rs3.getString("fullname") + "</td><td>" + rs3.getString("memNum") + "</td><td>" + rs3.getString("posid") + "</td><td>" + rs3.getString("webid") + "</td><td>" + rs3.getString("ghin") + "</td><td>" + rs3.getString("count") + "</td></tr>");
                }
                
                out.println("<tr><td colspan=4></td></tr>");
            
            }
            
            
            if (found) out.println("</table><p><i>Found " + i + " duplicate usernames.</i></p>&nbsp;<br><hr>");
            
    
            
            stmt2.close(); 
            con2.close();
            
            out.flush();
            
            tmp_total = tmp_total + i;
*/
                    
/*            
            // FIND ALL DISABLED EMAIL ADDRESSES
            rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM member2b WHERE email_bounced <> 0 OR email2_bounced <> 0;");
            
            i = 0;
            if (rs2.next()) {
                i = rs2.getInt(1);
                t++;
                tmp_total += i;
                
                if (i!=0) out.println("<br>" + club + " has " + i + " disabled emails.");
            }
*/            
  
            
/*            
          // Find clubs that scanTee failed on  
            rs2 = stmt2.executeQuery("select max(date) from teepast2;");
            if (rs2.next()) {
                i = rs2.getInt(1);
                if (i != 20070614) out.println("<br>" + club + " teepast2 most recent date is " + i + ".");
            }
*/
            
            
/*            
            // FIND ALL CLUBS USING LOTTERY
            rs2 = stmt2.executeQuery("SELECT lottery, clubName FROM club5 WHERE clubName <> '';");
            if (rs2.next()) {
                i = rs2.getInt(1);
                if (i == 1) out.println("<br>" + rs2.getString(2) + "  (" + club + ") is configured to use lottery.");
            }
*/            
           
/*            
            // FIND CLUBS WITH DUPLICATE RESTRICTION NAMES
            rs2 = stmt2.executeQuery("SELECT * FROM (SELECT name, COUNT(*) AS c FROM (SELECT name FROM restriction2) AS t GROUP BY name) AS t2 WHERE c > 1;");           
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getInt("c") + "&nbsp;&nbsp;" + rs2.getString("name"));
                
            }          
*/
                    
            
/*
            // FIND CLUBS WITH DUPLICATE RESTRICTION NAMES
            rs2 = stmt2.executeQuery("SELECT name FROM restriction2 WHERE name <> TRIM(name);");           
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getString("name"));
                
            }
*/
            
/*            
            // FIND CLUBS WITH NON UNIQUE USERNAME FIELDS IN MEMBER TABLE
            rs2 = stmt2.executeQuery("show index from member2b;");           
            
            while ( rs2.next() ) {

                if (rs2.getString("Non_unique").equals("1") && rs2.getString("Column_name").equals("username")) {
                    out.print("<br><b>" + club + " has non-unique index.</b>");
                }
            }
            
            
            // FIND CLUBS WITH NON UNIQUE USERNAME FIELDS IN MEMBER TABLE
            rs2 = stmt2.executeQuery("select count(*) from member2b where username = '';");           
            
            if ( rs2.next() ) {

                if (rs2.getInt(1) != 0) {
                    out.print("<br><b>" + club + " has an empty username!</b>");
                }
            }

 */
            stmt2.close(); 
            con2.close();
            
            out.flush();     
        
        } // loop all clubs

        out.println("<p><i>Found " + tmp_total); // + " total in " + t + " clubs.</i></p>");

        stmt2.close(); 
        con2.close();
        stmt1.close();
        con1.close();

    } catch (Exception e) {

        // Error connecting to db....
        out.println("<BR><BR><H3>Fatal Error!</H3>");
        out.println("Error performing update to club '" + club + "'.");
        out.println("<BR>Exception: "+ e.getMessage());
        out.println("<BR>Message: "+ e.toString());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    
   out.println("<BR><BR>Test Finished!  The test is complete for all clubs.");
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
