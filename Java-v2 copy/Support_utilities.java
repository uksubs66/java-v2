/***************************************************************************************
 *   Support_utilities: This servlet to be used for utility methods called from a support login.
 *
 *
 *   called by: 
 *
 *   created: 12/03/2013   Brad K.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *   12/04/13   Initial creation
 *              
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.activation.*;

import com.foretees.common.Utilities;



public class Support_utilities extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 
 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     
   HttpSession session = null; 
   Connection con = null;

   
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
   
   String club = (String)session.getAttribute("club");   // get club name


   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   
   if (req.getParameter("AGC_NGCpush") != null) {
       doAGC_NGCpush(club, out, con);
       return;
   } else if (req.getParameter("AGC_NGCclear") != null) {
       doAGC_NGCclear(club, out, con);
       return;
   }
   
   out.println("<html><head><title>Support Utilities</title></head>");
   out.println("<style>");
   out.println("a { color:black; }");
   out.println("</style>");
   out.println("<div style=\"margin-left:auto; margin-right:auto; width:100%; text-align:center;\">");
   out.println("<h3>Utilities Menu</h3>");
   out.println("<a href='?AGC_NGCpush'>American Golf - Push NGC changes from this club to all AGC clubs</a><br>");
   out.println("<a href='?AGC_NGCclear' onclick='return confirm(\"This will clear out all xx/yy/zz bag slot values on " + club + ". Are you sure you want to continue?\")'>American Golf - Clear out temporary NGC values from this club</a><br><br><br>");
   out.println("<a href=\"/" +rev+ "/servlet/Support_main\">Main Menu</a><br>");
   out.println("<a href=\"/" +rev+ "/servlet/Logout\">Logout</a>");
   out.println("</div>");
 }
 
 private void doAGC_NGCpush (String club, PrintWriter out, Connection con) {
     
     Connection con1 = null;
     Connection con2 = null;
     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     Statement stmt = null;
     Statement stmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;
     ResultSet rs3 = null;
     
     String type = "";
     String clubname = "";
     String fullname = "";
     
     int count = 0;
     
     boolean dupName = false;
     
     out.println("<div style=\"margin-left:auto; margin-right:auto; width:60%; text-align:left;\">");
     
     try {

         con1 = dbConn.Connect(rev);
     } catch (Exception exc) {

         // Error connecting to db....
         out.println("<BR><BR>Unable to connect to the DB.");
         out.println("<BR>Exception: " + exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
         out.println("</BODY></HTML>");
         return;
     }
     
     try {
         
         stmt = con1.createStatement();
         
         rs = stmt.executeQuery("SELECT clubname, fullname FROM clubs WHERE group_name = 'AGC' AND inactive = 0 ORDER BY clubname");
         
         // Loop through all AGC clubs
         while (rs.next()) {
             
             clubname = rs.getString("clubname");
             fullname = rs.getString("fullname");

             // skip if it's the club we're currently operating out of, or marbellacc (don't accept NGC play).
             if (clubname.equals(club) || clubname.equals("marbellacc")) {
                 
                 out.println("<br><br>Skipping " + clubname + " (" + fullname + ")");
                 continue;
             }
             
             out.println("<br><br>Starting " + clubname + " (" + fullname + "):");
             
             try {

                 con2 = dbConn.Connect(clubname);
                 
             } catch (Exception exc) {

                 // Error connecting to db....
                 out.println("<BR><BR>Unable to connect to the DB.");
                 out.println("<BR>Exception: " + exc.getMessage());
                 out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
                 out.println("</BODY></HTML>");
                 return;
             }
             
             try {

                 stmt2 = con.createStatement();

                 rs2 = stmt2.executeQuery("SELECT * FROM member2b WHERE (bag = 'xx' OR bag = 'yy' OR bag = 'zz') ORDER BY bag, name_last, name_first");

                 while (rs2.next()) {

                     count = 0;
                     type = rs2.getString("bag");

                     if (type.equalsIgnoreCase("xx")) {    // 'xx' members need to be added to all AGC clubs

                         dupName = false;
                         
                         // First make sure there's no one with that name in the target database
                         try {
                             
                             pstmt = con2.prepareStatement("SELECT inact FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");
                             pstmt.clearParameters();
                             pstmt.setString(1, rs2.getString("name_last"));
                             pstmt.setString(2, rs2.getString("name_first"));
                             pstmt.setString(3, rs2.getString("name_mi"));
                             
                             rs3 = pstmt.executeQuery();
                             
                             if (rs3.next()) {
                                 dupName = true;
                             }
                             
                         } catch (Exception exc) {
                             Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error checking for existing member - ERR: " + exc.toString());
                             out.println("<br><span style=\"color:red\">-Error looking up existing member for <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") + "</span>");
                         } finally {
                             
                             try { rs3.close(); }
                             catch (Exception ignore) {}

                             try { pstmt.close(); }
                             catch (Exception ignore) {}
                         }
                         
                         // If name isn't a duplicate, try adding it to this club database
                         if (!dupName) {
                             
                             try {
                                 
                                 pstmt = con2.prepareStatement(
                                         "INSERT INTO member2b (username, password, name_last, name_first, name_mi, "
                                         + "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, "
                                         + "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid, "
                                         + "email_bounced, email2_bounced, gender, pri_indicator) "
                                         + "VALUES (?,?,?,?,?,?,?,?,0,?,?,?,?,?,?,?,'','',?,?,?,?,?,?,'','',?,0,0,?,?)");
                                 
                                 pstmt.clearParameters();
                                 pstmt.setString(1, rs2.getString("username"));
                                 pstmt.setString(2, rs2.getString("password"));
                                 pstmt.setString(3, rs2.getString("name_last"));
                                 pstmt.setString(4, rs2.getString("name_first"));
                                 pstmt.setString(5, rs2.getString("name_mi"));
                                 pstmt.setString(6, rs2.getString("m_ship"));
                                 pstmt.setString(7, rs2.getString("m_type"));
                                 pstmt.setString(8, rs2.getString("email"));
                                 pstmt.setFloat(9, rs2.getFloat("c_hancap"));
                                 pstmt.setFloat(10, rs2.getFloat("g_hancap"));
                                 pstmt.setString(11, rs2.getString("wc"));
                                 pstmt.setString(12, rs2.getString("message"));
                                 pstmt.setInt(13, rs2.getInt("emailOpt"));
                                 pstmt.setString(14, rs2.getString("memNum"));
                                 pstmt.setString(15, rs2.getString("ghin"));
                                 pstmt.setInt(16, rs2.getInt("birth"));
                                 pstmt.setString(17, rs2.getString("posid"));
                                 pstmt.setString(18, rs2.getString("msub_type"));
                                 pstmt.setString(19, rs2.getString("email2"));
                                 pstmt.setString(20, rs2.getString("phone1"));
                                 pstmt.setString(21, rs2.getString("phone2"));
                                 pstmt.setString(22, rs2.getString("webid"));
                                 pstmt.setString(23, rs2.getString("gender"));
                                 pstmt.setInt(24, rs2.getInt("pri_indicator"));
                                 
                                 count = pstmt.executeUpdate();

                                 //out.println("<br><span style=\"color:green\">-<span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") + "</span> successfully added</span>");

                             } catch (Exception exc) {
                                 
                                 // This means the username was already in use - print error
                                 out.println("<br><span style=\"color:red\">-Error adding <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") 
                                         + "</span> - Username '" + rs2.getString("username") + "' already exists - ERR: " + exc.toString() + "</span>");
                             } finally {

                                 try { pstmt.close(); }
                                 catch (Exception ignore) {}
                             }
                             
                         } else {
                             
                             // Duplicate name was found - print error
                             out.println("<br><span style=\"color:red\">-Error adding <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") 
                                     + "</span> - Duplicate name found</span>");
                         }

                     } else if (type.equalsIgnoreCase("yy")) {

                         // 'yy' members need to be set inactive on all AGC clubs
                         try {
                             
                             pstmt = con2.prepareStatement("UPDATE member2b SET inact = 1 WHERE username = ?");
                             pstmt.clearParameters();
                             pstmt.setString(1, rs2.getString("username"));
                             
                             count = pstmt.executeUpdate();
                             
                             if (count == 0) {
                                 
                                 // See if the member already exists and is inactive, since if so, we don't need to print an error since they're already okay.
                                 try {
                                     
                                     pstmt2 = con2.prepareStatement("SELECT username FROM member2b WHERE username = ? AND inact = 1");
                                     pstmt2.clearParameters();
                                     pstmt2.setString(1, rs2.getString("username"));
                                     
                                     rs3 = pstmt2.executeQuery();
                                     
                                     if (!rs3.next()) {
                                         out.println("<br><span style=\"color:red\">-Error setting <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") 
                                                 + "</span> inactive - Username not found</span>");
                                     }
                                     
                                 } catch (Exception exc) {
                                     Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error checking for existing, inactive member - ERR: " + exc.toString());
                                 } finally {
                                     
                                     try { rs3.close(); }
                                     catch (Exception ignore) {}
                                     
                                     try { pstmt2.close(); }
                                     catch (Exception ignore) {}
                                 }
                             }
                             
                         } catch (Exception exc) {
                             Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error setting member inactive - ERR: " + exc.toString());
                             out.println("<br><span style=\"color:red\">-Error setting <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") + " inactive (check exception)</span>");
                         } finally {

                             try { pstmt.close(); }
                             catch (Exception ignore) {}
                         }

                     } else if (type.equalsIgnoreCase("zz")) {

                         // 'zz' members need to be set active again on all AGC clubs
                         try {
                             
                             pstmt = con2.prepareStatement("UPDATE member2b SET inact = 0 WHERE username = ?");
                             pstmt.clearParameters();
                             pstmt.setString(1, rs2.getString("username"));
                             
                             count = pstmt.executeUpdate();
                             
                             if (count == 0) {
                                 
                                 // See if the member already exists and is active, since if so, we don't need to print an error since they're already okay.
                                 try {
                                     
                                     pstmt2 = con2.prepareStatement("SELECT username FROM member2b WHERE username = ? AND inact = 0");
                                     pstmt2.clearParameters();
                                     pstmt2.setString(1, rs2.getString("username"));
                                     
                                     rs3 = pstmt2.executeQuery();
                                     
                                     if (!rs3.next()) {
                                         out.println("<br><span style=\"color:red\">-Error setting <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") 
                                                 + "</span> active - Username not found</span>");
                                     }
                                     
                                 } catch (Exception exc) {
                                     Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error checking for existing, active member - ERR: " + exc.toString());
                                 } finally {
                                     
                                     try { rs3.close(); }
                                     catch (Exception ignore) {}
                                     
                                     try { pstmt2.close(); }
                                     catch (Exception ignore) {}
                                 }
                             }
                             
                         } catch (Exception exc) {
                             Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error setting member inactive - ERR: " + exc.toString());
                             out.println("<br><span style=\"color:red\">-Error setting <span style=\"font-weight:bold;\">" + rs2.getString("name_last") + ", " + rs2.getString("name_first") + " " + rs2.getString("name_mi") + "</span> active (check exception)</span>");
                         } finally {

                             try { pstmt.close(); }
                             catch (Exception ignore) {}
                         }
                     }
                 }    // End of member while loop

             } catch (Exception exc) {
                 Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error looking up NGC changes to push - ERR: " + exc.toString());
             } finally {

                 try { rs2.close(); }
                 catch (Exception ignore) {}

                 try { stmt2.close(); }
                 catch (Exception ignore) {}
             }
             
             try { con2.close(); }
             catch (Exception ignore) {}
             
         }    // end of club while loop
     } catch (Exception exc) {
         Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error looking up list of AGC clubs - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     try { con1.close(); } 
     catch (Exception ignore) {}
     
     out.println("</div>");
     out.println("<div style=\"margin-left:auto; margin-right:auto; width:60%; text-align:center;\">");
     out.println("<br><br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Support_utilities\">Return</a>");
     out.println("<br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Support_main\">Main Menu</a>");
     out.println("<br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Logout\">Logout</a>");
     out.println("</div>");
 }
 
 
 private void doAGC_NGCclear (String club, PrintWriter out, Connection con) {
     
     Statement stmt = null;
     
     boolean error = false;
     
     out.println("<div style=\"margin-left:auto; margin-right:auto; width:60%; text-align:left;\">");
     
     // Remove 'xx' from records
     try {
         stmt = con.createStatement();

         stmt.executeUpdate("UPDATE member2b SET bag = '' WHERE bag = 'xx'");
         
     } catch (Exception exc) {
         error = true;
         out.println("<br>Error removing 'xx' bag indicators - check log for details");
         Utilities.logError("Support_utilities.doAGC_NGCclear - " + club + " - Error removing 'xx' bag indicators - ERR: " + exc.toString());
     } finally {
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     // Remove 'yy' from records and mark them inactive
     try {
         stmt = con.createStatement();

         stmt.executeUpdate("UPDATE member2b SET bag = '', inact = 1 WHERE bag = 'yy'");
         
     } catch (Exception exc) {
         error = true;
         out.println("<br>Error removing 'yy' bag indicators and setting those members inactive - check log for details");
         Utilities.logError("Support_utilities.doAGC_NGCclear - " + club + " - Error removing 'yy' bag indicators and setting those members inactive - ERR: " + exc.toString());
     } finally {
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     
     
     try {
         stmt = con.createStatement();

         stmt.executeUpdate("UPDATE member2b SET bag = '', inact = 0 WHERE bag = 'zz'");
         
     } catch (Exception exc) {
         error = true;
         out.println("<br>Error removing 'zz' bag indicators and setting those members active - check log for details");
         Utilities.logError("Support_utilities.doAGC_NGCclear - " + club + " - Error removing 'zz' bag indicators and setting those members active - ERR: " + exc.toString());
     } finally {
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     if (!error) {
         out.println("<br>Bag slot values cleared successfully from " + club + " database!");
     }
     
     out.println("</div>");
     out.println("<div style=\"margin-left:auto; margin-right:auto; width:60%; text-align:center;\">");
     
     out.println("<br><br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Support_utilities\">Return</a>");
     out.println("<br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Support_main\">Main Menu</a>");
     out.println("<br><a style=\"color:black;\" href=\"/" +rev+ "/servlet/Logout\">Logout</a>");
 }
 
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<body class=\"serifFont\">");
   out.println("<div id=\"wrapper\">");
   out.println("<img src=\"/" +rev+ "/images/foretees.gif\" /><br />");
   out.println("<hr class=\"menu\">");
   out.println("<br /><h2>Access Error</h2><br />");
   out.println("<br /><br />Sorry, you must login before attempting to access these features.<br />");
   out.println("<br /><br />Please <a href=\"Logout\">login</a>");
   out.println("</div>    <!-- wrapper  -->");
   out.println("</body></html>");

 }
 
} // end of servlet class