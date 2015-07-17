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
import com.foretees.common.Connect;
import com.foretees.common.sendEmail;



public class Support_utilities extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doPost(req, resp);      // call doPost processing
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
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

        String user = (String) session.getAttribute("user");   // get username

        if (!SystemUtils.verifySupport(user)) {

            invalidUser(out);            // Intruder - reject
            return;
        }

        String club = (String) session.getAttribute("club");   // get club name


        try {
            con = dbConn.Connect(club);

        } catch (Exception exc) {

            // Error connecting to db....

            out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the DB.");
            out.println("<BR>Exception: " + exc.getMessage());
            out.println("<BR><BR> <A HREF=\"/" + rev + "/servlet/Support_main\">Return</A>.");
            out.println("</CENTER></BODY></HTML>");
            return;
        }


        if (req.getParameter("AGC_NGCpush") != null) {
            doAGC_NGCpush(club, out, con);
            return;
        } else if (req.getParameter("AGC_NGCclear") != null) {
            doAGC_NGCclear(club, out, con);
            return;
        } else if (req.getParameter("eventCleanup") != null) {
            doEventCleanup(club, req, out, con);
            return;
        } else if (req.getParameter("nullCleanup") != null) {
            doNullCleanup(club, req, out, con);
            return;
        } else if (req.getParameter("sendEmails") != null) {
            sendEmail.sendReminderNotifications(club, con);
        }

        out.println("<html><head><title>Support Utilities</title></head>");
        out.println("<body>");
        out.println("<style>");
        out.println("a { color:black; }");
        out.println("</style>");
        out.println("<div style=\"margin-left:auto; margin-right:auto; width:100%; text-align:center;\">");
        out.println("<h2>Utilities Menu</h2>");
        out.println("<h3>Current Club: " + club + "</h3>");
        out.println("<a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a>");
        out.println("<br><a href=\"/" + rev + "/servlet/Logout\">Logout</a>");

        out.println("<br><br>");
        out.println("<h3>Clean Up</h3>");
        out.println("<a href='?eventCleanup'>Clean Up Past Events</a>");
        out.println("<br><a href='?nullCleanup'>Clean Up Null Member Bindings</a>");

        out.println("<br><br>");
        out.println("<h3>American Golf</h3>");
        out.println("<a href='?AGC_NGCpush'>American Golf - Push NGC changes from this club to all AGC clubs</a>");
        out.println("<br><a href='?AGC_NGCclear' onclick='return confirm(\"This will clear out all xx/yy/zz bag slot values on " + club + ". Are you sure you want to continue?\")'>American Golf - Clear out temporary NGC values from this club</a>");
        
        out.println("<br><br>");
        out.println("<h3>Misc</h3>");
        out.println("<a href='?sendEmails'>Send Email Reminders"
                + "<br>(Note: Emails won't go out if club5.remind_sent = today's date!)</a>");

        out.println("<br><br><br>");
        out.println("<a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a><br>");
        out.println("<a href=\"/" + rev + "/servlet/Logout\">Logout</a>");
        out.println("</div>");
        out.println("</body></html>");
    }
 
 private void doAGC_NGCpush(String club, PrintWriter out, Connection con) {
     
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

                 rs2 = stmt2.executeQuery("SELECT * FROM member2b WHERE bag IN ('xx', 'xxdel', 'yy', 'zz') ORDER BY bag, name_last, name_first");

                 while (rs2.next()) {

                     count = 0;
                     type = rs2.getString("bag");

                     if (type.equalsIgnoreCase("xxdel")) {    // 'ww' members need to be deleted from all AGC clubs.
                         
                         try {
                             
                             pstmt = con2.prepareStatement("DELETE FROM member2b WHERE username = ?");
                             pstmt.clearParameters();
                             pstmt.setString(1, rs2.getString("username"));
                             
                             count = pstmt.executeUpdate();
                             
                             if (count == 0) {
                                 out.println("<br><span style=\"color:red\">-Error removing <span style=\"font-weight:bold;\">" + rs2.getString("username") + "</span> from database - Username not found</span>");
                             }
                             
                         } catch (Exception exc) {
                             Utilities.logError("Support_utilities.doAGC_NGCpush - " + club + " - Error removing member from database - ERR: " + exc.toString());
                             out.println("<br><span style=\"color:red\">-Error removing <span style=\"font-weight:bold;\">" + rs2.getString("username") + " from database (check exception)</span>");
                         } finally {

                             try { pstmt.close(); }
                             catch (Exception ignore) {}
                         }
                     }
                     
                     if (type.equalsIgnoreCase("xx") || type.equalsIgnoreCase("xxdel")) {    // 'xx' members need to be added to all AGC clubs

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

                     } else if (type.equalsIgnoreCase("yy")) {    // 'yy' members need to be set inactive on all AGC clubs

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

                     } else if (type.equalsIgnoreCase("zz")) {    // 'zz' members need to be set active again on all AGC clubs

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
 
 
 private void doAGC_NGCclear(String club, PrintWriter out, Connection con) {
     
     Statement stmt = null;
     
     boolean error = false;
     
     out.println("<div style=\"margin-left:auto; margin-right:auto; width:60%; text-align:left;\">");
     
     // Remove 'xx' from records
     try {
         stmt = con.createStatement();

         stmt.executeUpdate("UPDATE member2b SET bag = '' WHERE bag = 'xx' OR bag = 'xxdel'");
         
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
 
    private void doEventCleanup(String club, HttpServletRequest req, PrintWriter out, Connection con) {
        
        int event_count = 0;
        
        long date = 0;
        long cur_date = Utilities.getDate(con);
        
        out.println("<html><head><title>Support Utilities</title></head>");
        out.println("<body>");
        out.println("<style>");
        out.println("a { color:black; }");
        out.println("</style>");
        out.println("<div style=\"margin-left:auto; margin-right:auto; width:75%; text-align:center;\">");

        out.println("<h2>Clean Up Past Events</h2>");
        out.println("<a href=\"/" + rev + "/servlet/Support_utilities\">Return</a>");
        out.println("<br><a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a>");
        out.println("<br><a href=\"/" + rev + "/servlet/Logout\">Logout</a>");
        out.println("<br><br><br>");
        
        if (req.getParameter("date") != null) {
            
            if (req.getParameter("date").length() == 8) {    // Verify that date is correct length (yyyymmdd format)
                try {
                    date = Long.parseLong(req.getParameter("date"));
                    
                    if (date < 20000000 || date >= cur_date) {
                        date = -1;
                    }
                } catch (Exception e) {
                    date = -1;
                }
            } else {
                date = -1;
            }
        }

        if (req.getParameter("eventCleanup2") == null || date == -1) {    // First time here, prompt for date input
            
            out.println("<span style=\"font-weight: bold;\">Current club</span>: " + club);
            
            if (date == -1) {
                out.println("<br><br><span style=\"color: red;\">The date entered was invalid. Please try again using yyyymmdd format.</span>");
            }
            
            out.println("<span style=\"text-align: left;\">");
            out.println("<br><br>Please enter a date below in yyyymmdd format. All events prior to and including the specified date will be cleaned up and hidden for this club.");
            out.println("<br><br><span style=\"font-weight: bold;\">Please be careful!</span> Double check the date before submitting.  These events <span style=\"font-weight: bold;\">will</span> be recoverable, but doing so will take a good amount of work!");
            out.println("</span>");

            out.println("<p><br><form method=\"POST\" action=\"Support_utilities\">");
            out.println("<input type=\"hidden\" name=\"eventCleanup\">");
            out.println("<span style=\"font-weight: bold;\">Enter Date (yyyymmdd)</span>: <input type=\"text\" name=\"date\" size=\"10\" maxlength=\"8\">");
            out.println("<br><br><input type=\"submit\" name=\"eventCleanup2\" value=\"Clean Up Events\">");
            out.println("</form></p>");
            
        } else {    // Date input submitted, process and inactivate events
            
            PreparedStatement pstmt = null;
            PreparedStatement pstmt2 = null;
            ResultSet rs = null;
            
            try {
                
                pstmt = con.prepareStatement("SELECT event_id FROM events2b WHERE date <= ? AND inactive = 0");
                pstmt.clearParameters();
                pstmt.setLong(1, date);
                
                rs = pstmt.executeQuery();
                
                while (rs.next()) {
                    
                    try {
                        
                        pstmt2 = con.prepareStatement("UPDATE events2b SET name = CONCAT('*** ', name), inactive = 1 WHERE event_id = ?");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, rs.getInt("event_id"));
                        
                        pstmt2.executeUpdate();                        
                        
                    } catch (Exception e) {
                        Utilities.logError("Support_utilities.doEventCleanup - " + club + " - Failed to inactivate club properly (events2b) - Error = " + e.toString());
                    } finally {
                        Connect.close(pstmt2);
                    }
                    
                    try {
                        
                        pstmt2 = con.prepareStatement("UPDATE evntsup2b SET name = CONCAT('*** ', name), inactive = 1 WHERE event_id = ? AND inactive = 0");
                        pstmt2.clearParameters();
                        pstmt2.setInt(1, rs.getInt("event_id"));
                        
                        pstmt2.executeUpdate();                        
                        
                    } catch (Exception e) {
                        Utilities.logError("Support_utilities.doEventCleanup - " + club + " - Failed to inactivate club properly (evntsup2b) - Error = " + e.toString());
                    } finally {
                        Connect.close(pstmt2);
                    }
                    
                    event_count++;
                }
                
            } catch (Exception e) {
                Utilities.logError("Support_utilities.doEventCleanup - " + club + " - Failed to load event data prior to (" + date + ") - Error = " + e.toString());
            } finally {
                Connect.close(rs, pstmt);
            }
            
            if (event_count == 0) {
                out.println("No events were found prior to " + date);
            } else if (event_count > 0) {
                out.println(event_count + " events prior to " + date + " were successfully inactivated and hidden from view.");
            }
        }
        
        out.println("<br><br>");
        out.println("<a href=\"/" + rev + "/servlet/Support_utilities\">Return</a><br>");
        out.println("<a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a><br>");
        out.println("<a href=\"/" + rev + "/servlet/Logout\">Logout</a>");
        out.println("</div>");
        out.println("</body></html>");
    }
    
    
    private void doNullCleanup(String club, HttpServletRequest req, PrintWriter out, Connection con) {
        
        int partner_count = 0;
        int gdb_count = 0;
        int dct_count = 0;
        
        out.println("<html><head><title>Support Utilities</title></head>");
        out.println("<body>");
        out.println("<style>");
        out.println("a { color:black; }");
        out.println("</style>");
        out.println("<div style=\"margin-left:auto; margin-right:auto; width:75%; text-align:center;\">");

        out.println("<h2>Clean Up Null Member Bindings</h2>");
        out.println("<a href=\"/" + rev + "/servlet/Support_utilities\">Return</a>");
        out.println("<br><a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a>");
        out.println("<br><a href=\"/" + rev + "/servlet/Logout\">Logout</a>");
        out.println("<br><br><br>");
        
        if (req.getParameter("nullCleanup2") == null) {    // First time here, prompt for confirmation
            
            out.println("<span style=\"font-weight: bold;\">Current club</span>: " + club);
            
            out.println("<span style=\"text-align: left;\">");
            out.println("<br><br>This job will clean up any null database entries resulting from deleting members for a club directly via their database (not via ForeTees).");
            out.println("<br><br>This should be run any time members are cleared out of a club's database at the request of the club, to avoid the chance of errors occuring within ForeTees.");
            out.println("<br>There is no harm in running this job multiple times, or on a database that doesn't actually have any null entries to be cleared!");
            out.println("</span>");

            out.println("<p><form method=\"POST\" action=\"Support_utilities\">");
            out.println("<input type=\"hidden\" name=\"nullCleanup\">");
            out.println("<br><br><input type=\"submit\" name=\"nullCleanup2\" value=\"Clean Up\">");
            out.println("</form></p>");
            
        } else {    // Confirmation received, perform clean up
            
            Statement stmt = null;
            
            try {

                stmt = con.createStatement();

                partner_count = stmt.executeUpdate("DELETE FROM partner WHERE partner_id NOT IN (SELECT username FROM member2b)");

            } catch (Exception e) {
                Utilities.logError("Support_utilities.doNullCleanup - " + club + " - Failed to clean up null partner entries - Error = " + e.toString());
            } finally {
                Connect.close(stmt);
            }

            try {

                stmt = con.createStatement();

                gdb_count = stmt.executeUpdate("DELETE FROM guestdb_hosts WHERE guest_id NOT IN (SELECT guest_id FROM guestdb_data);");

            } catch (Exception e) {
                Utilities.logError("Support_utilities.doNullCleanup - " + club + " - Failed to clean up null guestdb host entries - Error = " + e.toString());
            } finally {
                Connect.close(stmt);
            }
            
            try {

                stmt = con.createStatement();

                dct_count = stmt.executeUpdate("DELETE FROM demo_clubs_usage WHERE username NOT IN (SELECT username FROM member2b)");

            } catch (Exception e) {
                Utilities.logError("Support_utilities.doNullCleanup - " + club + " - Failed to clean up null demo club check-out entries - Error = " + e.toString());
            } finally {
                Connect.close(stmt);
            }
            
            out.println("Null member bindings have been cleaned up!");
            out.println("<br><br>" + partner_count + " null partner entries cleared.");
            out.println("<br>" + gdb_count + " null tracked guest host entries cleared.");
            out.println("<br>" + dct_count + " null demo equipment check-out entries cleared.");
        }
        
        out.println("<br><br>");
        out.println("<a href=\"/" + rev + "/servlet/Support_utilities\">Return</a><br>");
        out.println("<a href=\"/" + rev + "/servlet/Support_main\">Main Menu</a><br>");
        out.println("<a href=\"/" + rev + "/servlet/Logout\">Logout</a>");
        out.println("</div>");
        out.println("</body></html>");
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