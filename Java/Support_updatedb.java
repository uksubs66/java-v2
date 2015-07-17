/***************************************************************************************
 *   Support_upgrade:  This servlet will process the upgrade request from Support's Upgrade page.
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


public class Support_updatedb extends HttpServlet {


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
   out.println("<BR><BR>Click 'Run' to start the job.");
out.println("<br>Fox Hill -&gt; Fort Collins Tee Time Transfer");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A><BR><BR>");
   
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
    Connection con3 = null;
    PreparedStatement pstmt = null;
    Statement stmt1 = null;
    Statement stmt1a = null;
    Statement stmt2 = null;
    Statement stmt3 = null;
    ResultSet rs1 = null;
    ResultSet rs2 = null;
    ResultSet rs3 = null;

    out.println("<HTML><HEAD><TITLE>Database Update</TITLE></HEAD>");
    out.println("<BODY><H3>Starting DB Update...</H3>");
    out.flush();

    String club = "";

    try {

        con1 = dbConn.Connect(rev);
    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }
    
/*
    try {
    // ADD TIMESTAMP TO ERROR LOG
    //stmt2.executeUpdate("ALTER TABLE errorlog ADD err_timestamp datetime");
            
    stmt1 = con1.createStatement();
    
    // BOB ONLY!!!
    stmt1.executeUpdate("" +
        "CREATE TABLE login_stats IF NOT EXISTS ( " +                                        
            "login_stat_entry_id int(11) NOT NULL auto_increment, " +                                  
            "entry_date date, " +                                                        
            "hour tinyint(4), " +                                                        
            "node tinyint(4), " +                                                         
            "user_type_id int(11), " +                                                   
            "login_count int(11), " +                                                    
            "PRIMARY KEY (login_stat_entry_id), " +
            "UNIQUE KEY entry_date (entry_date, hour, node, user_type_id) " +
        ") ENGINE=MyISAM;");
            
            
    stmt1.executeUpdate("" +
        "CREATE TABLE user_types IF NOT EXISTS ( " +
            "user_type_id int(11) NOT NULL auto_increment, " + 
            "user_type varchar(32) NOT NULL default '', " + 
            "PRIMARY KEY (user_type_id) " + 
        ") ENGINE=MyISAM;");

    stmt1.executeUpdate("" + 
        "INSERT INTO user_types VALUES " + 
            "(1,'Member'), " + 
            "(2,'Remote'), " + 
            "(3,'Proshop'), " + 
            "(4,'Admin'), " + 
            "(5,'Support'), " + 
            "(6,'Sales');");

   }
   catch (Exception e) {

      // Error connecting to db....

      out.println("<BR><BR><H3>Fatal Error!</H3>");
      out.println("Error performing update to v5.");
      out.println("<BR>Exception: "+ e.getMessage());
      out.println("<BR>Message: "+ e.toString());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</BODY></HTML>");
      out.close();
      return;
   }
*/
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
        rs1 = stmt1.executeQuery("SELECT clubname, (SELECT COUNT(*) FROM v5.clubs) AS total FROM v5.clubs ORDER BY clubname");

        while (rs1.next()) {

            x1++; 

            club = rs1.getString(1);                // get a club name
            x2 = rs1.getInt(2);                     // get count of clubs

            pstmt = con1.prepareStatement("UPDATE v5.update_status SET club = ?");
            pstmt.clearParameters();
            pstmt.setString(1, club);
            pstmt.executeUpdate();

            con2 = dbConn.Connect("foxhill");            // get a connection to this club's db
            con3 = dbConn.Connect("fortcollins");
            stmt2 = con2.createStatement();         // create a statement
            stmt3 = con2.createStatement();         // create another statement
            
            out.println("<br><br>");
            out.print("[" + x1 + "/" + x2 + "] Starting " + club);
            out.flush();


            /*  NOTES:
             *  remember to update Support_init!
             * 
             * 
             * 
             */
            
            
            //if (club.equals("mayfieldsr")) skip = false;

            //if (!club.equals("demov4") && !club.equals("demopaul") && !club.equals("demobrock")) {
            //}
//
//            if (club.equals("demobrad")) {
//                skip = true;
//            } else {
//                skip = false;
//            }

            if (club.equals("foxhill")) {
                skip = false;
            } else {
                skip = true;
            }
            
            if (!skip) {

                int up_count = 0;

                try {

                    //con2=foxhill
                    //con3=fortcollins

                    Statement stmtfh = null;
                    PreparedStatement pstmtft = null;
                    ResultSet rsfh = null;

                    stmtfh = con2.createStatement();

                    rsfh = stmtfh.executeQuery("SELECT * FROM teecurr2 WHERE player1<>''");

                    while (rsfh.next()) {

                        try {
                            pstmtft = con3.prepareStatement("UPDATE teecurr2 SET " +
                                    "player1 = ?, player2 = ?, player3 = ?, " +
                                    "player4 = ?, player5 = ?, username1 = ?, username2 = ?, username3 = ?, " +
                                    "username4 = ?, username5 = ?, p1cw = ?, p2cw = ?, p3cw = ?, " +
                                    "p4cw = ?, p5cw = ?, first = ?, " +
                                    "hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, hndcp4 = ?, " +
                                    "hndcp5 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, " +
                                    "show5 = ?, notes = ?, hideNotes = ?, " +
                                    "proNew = ?, proMod = ?, memNew = ?, " +
                                    "memMod = ?, mNum1 = ?, mNum2 = ?, " +
                                    "mNum3 = ?, mNum4 = ?, mNum5 = ?, userg1 = ?, " +
                                    "userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, guest_id1 = ?, " +
                                    "guest_id2 = ?, guest_id3 = ?, guest_id4 = ?, guest_id5 = ?, hotelNew = ?, " +
                                    "hotelMod = ?, orig_by = ?, conf = ?, p91 = ?, p92 = ?, " +
                                    "p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, " +
                                    "pos3 = ?, pos4 = ?, pos5 = ?, hole = ?, auto_blocked = ?, " +
                                    "pace_status_id = ?, custom_disp1 = ?, custom_disp2 = ?, custom_disp3 = ?, custom_disp4 = ?, " +
                                    "custom_disp5 = ?, custom_string = ?, custom_int = ?, " +
                                    "lottery_email = ?, tflag1 = ?, tflag2 = ?, tflag3 = ?, tflag4 = ?, " +
                                    "tflag5 = ?, hideIt = ?, orig1 = ?, orig2 = ?, orig3 = ?, " +
                                    "orig4 = ?, orig5 = ? " +
                                    "WHERE date = ? AND time = ? AND courseName = 'Fox Hill CC' AND fb = ?");

                            pstmtft.clearParameters();
                            pstmtft.setString(1, rsfh.getString("player1"));
                            pstmtft.setString(2, rsfh.getString("player2"));
                            pstmtft.setString(3, rsfh.getString("player3"));
                            pstmtft.setString(4, rsfh.getString("player4"));
                            pstmtft.setString(5, rsfh.getString("player5"));
                            pstmtft.setString(6, rsfh.getString("username1"));
                            pstmtft.setString(7, rsfh.getString("username2"));
                            pstmtft.setString(8, rsfh.getString("username3"));
                            pstmtft.setString(9, rsfh.getString("username4"));
                            pstmtft.setString(10, rsfh.getString("username5"));
                            pstmtft.setString(11, rsfh.getString("p1cw"));
                            pstmtft.setString(12, rsfh.getString("p2cw"));
                            pstmtft.setString(13, rsfh.getString("p3cw"));
                            pstmtft.setString(14, rsfh.getString("p4cw"));
                            pstmtft.setString(15, rsfh.getString("p5cw"));
                            pstmtft.setInt(16, rsfh.getInt("first"));
                            pstmtft.setDouble(17, rsfh.getDouble("hndcp1"));
                            pstmtft.setDouble(18, rsfh.getDouble("hndcp2"));
                            pstmtft.setDouble(19, rsfh.getDouble("hndcp3"));
                            pstmtft.setDouble(20, rsfh.getDouble("hndcp4"));
                            pstmtft.setDouble(21, rsfh.getDouble("hndcp5"));
                            pstmtft.setInt(22, rsfh.getInt("show1"));
                            pstmtft.setInt(23, rsfh.getInt("show2"));
                            pstmtft.setInt(24, rsfh.getInt("show3"));
                            pstmtft.setInt(25, rsfh.getInt("show4"));
                            pstmtft.setInt(26, rsfh.getInt("show5"));
                            pstmtft.setString(27, rsfh.getString("notes"));
                            pstmtft.setInt(28, rsfh.getInt("hideNotes"));
                            pstmtft.setInt(29, rsfh.getInt("proNew"));
                            pstmtft.setInt(30, rsfh.getInt("proMod"));
                            pstmtft.setInt(31, rsfh.getInt("memNew"));
                            pstmtft.setInt(32, rsfh.getInt("memMod"));
                            pstmtft.setString(33, rsfh.getString("mNum1"));
                            pstmtft.setString(34, rsfh.getString("mNum2"));
                            pstmtft.setString(35, rsfh.getString("mNum3"));
                            pstmtft.setString(36, rsfh.getString("mNum4"));
                            pstmtft.setString(37, rsfh.getString("mNum5"));
                            pstmtft.setString(38, rsfh.getString("userg1"));
                            pstmtft.setString(39, rsfh.getString("userg2"));
                            pstmtft.setString(40, rsfh.getString("userg3"));
                            pstmtft.setString(41, rsfh.getString("userg4"));
                            pstmtft.setString(42, rsfh.getString("userg5"));
                            pstmtft.setInt(43, rsfh.getInt("guest_id1"));
                            pstmtft.setInt(44, rsfh.getInt("guest_id2"));
                            pstmtft.setInt(45, rsfh.getInt("guest_id3"));
                            pstmtft.setInt(46, rsfh.getInt("guest_id4"));
                            pstmtft.setInt(47, rsfh.getInt("guest_id5"));
                            pstmtft.setInt(48, rsfh.getInt("hotelNew"));
                            pstmtft.setInt(49, rsfh.getInt("hotelMod"));
                            pstmtft.setString(50, rsfh.getString("orig_by"));
                            pstmtft.setString(51, rsfh.getString("conf"));
                            pstmtft.setInt(52, rsfh.getInt("p91"));
                            pstmtft.setInt(53, rsfh.getInt("p92"));
                            pstmtft.setInt(54, rsfh.getInt("p93"));
                            pstmtft.setInt(55, rsfh.getInt("p94"));
                            pstmtft.setInt(56, rsfh.getInt("p95"));
                            pstmtft.setInt(57, rsfh.getInt("pos1"));
                            pstmtft.setInt(58, rsfh.getInt("pos2"));
                            pstmtft.setInt(59, rsfh.getInt("pos3"));
                            pstmtft.setInt(60, rsfh.getInt("pos4"));
                            pstmtft.setInt(61, rsfh.getInt("pos5"));
                            pstmtft.setString(62, rsfh.getString("hole"));
                            pstmtft.setInt(63, rsfh.getInt("auto_blocked"));
                            pstmtft.setInt(64, rsfh.getInt("pace_status_id"));
                            pstmtft.setString(65, rsfh.getString("custom_disp1"));
                            pstmtft.setString(66, rsfh.getString("custom_disp2"));
                            pstmtft.setString(67, rsfh.getString("custom_disp3"));
                            pstmtft.setString(68, rsfh.getString("custom_disp4"));
                            pstmtft.setString(69, rsfh.getString("custom_disp5"));
                            pstmtft.setString(70, rsfh.getString("custom_string"));
                            pstmtft.setInt(71, rsfh.getInt("custom_int"));
                            pstmtft.setInt(72, rsfh.getInt("lottery_email"));
                            pstmtft.setString(73, rsfh.getString("tflag1"));
                            pstmtft.setString(74, rsfh.getString("tflag2"));
                            pstmtft.setString(75, rsfh.getString("tflag3"));
                            pstmtft.setString(76, rsfh.getString("tflag4"));
                            pstmtft.setString(77, rsfh.getString("tflag5"));
                            pstmtft.setInt(78, rsfh.getInt("hideIt"));
                            pstmtft.setString(79, rsfh.getString("orig1"));
                            pstmtft.setString(80, rsfh.getString("orig2"));
                            pstmtft.setString(81, rsfh.getString("orig3"));
                            pstmtft.setString(82, rsfh.getString("orig4"));
                            pstmtft.setString(83, rsfh.getString("orig5"));

                            pstmtft.setInt(84, rsfh.getInt("date"));
                            pstmtft.setInt(85, rsfh.getInt("time"));
                            pstmtft.setInt(86, rsfh.getInt("fb"));

                            up_count = pstmtft.executeUpdate();

                            if (up_count == 0) out.println("<br>No Row Updated: " + rsfh.getInt("date") + " : " + rsfh.getInt("time") + " : " + rsfh.getInt("fb"));

                            pstmtft.close();


                        } catch (Exception exc2) {
                            out.println("<br>Failed: " + exc2.getMessage());
                        }

                    }

                    stmtfh.close();

                } catch (Exception exc) {
                    out.println("<br>" + club + " Failed. " + exc.getMessage());
                }
            }

/*
                    stmt2.executeUpdate("delete from guest5");

                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Reg. Guest',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Fam. Guest',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('PCN',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Public',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Social',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('BML',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Recip.',0,1,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('PM',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('GC',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('PGA Comp',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Jr. Guest',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Maint.',0,0,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Tourn.',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('GCSAA',0,1,'','','','',0,0)");
*/

        /*
                    Statement tstmt = null;
                    PreparedStatement tpstmt = null;
                    ResultSet trs = null;

                    int act_id = 0;
                    int consec_mem = 0;
                    String consec_mem_csv = "";
                    int consec_pro = 0;
                    String consec_pro_csv = "";

                    // Add consec csv fields
                    stmt2.executeUpdate("ALTER TABLE activities " +
                            "ADD consec_mem_csv text NOT NULL default '' AFTER consec_mem, " +
                            "ADD consec_pro_csv text NOT NULL default '' AFTER consec_pro");

                    // Get activity_id, consec_mem, and consec_pro values
                    tstmt = con2.createStatement();
                    trs = tstmt.executeQuery("SELECT activity_id, consec_mem, consec_pro FROM activities");

                    while (trs.next()) {

                        act_id = trs.getInt("activity_id");
                        consec_mem = trs.getInt("consec_mem");
                        consec_pro = trs.getInt("consec_pro");
                        consec_mem_csv = "";
                        consec_pro_csv = "";

                        for (int j=1; j<=consec_mem; j++) {
                            consec_mem_csv += j + (j < consec_mem ? "," : "");
                        }

                        for (int j=1; j<=consec_pro; j++) {
                            consec_pro_csv += j + (j < consec_pro ? "," : "");
                        }

                        tpstmt = con2.prepareStatement("UPDATE activities SET consec_mem_csv = ?, consec_pro_csv = ? WHERE activity_id = ?");
                        tpstmt.clearParameters();
                        tpstmt.setString(1, consec_mem_csv);
                        tpstmt.setString(2, consec_pro_csv);
                        tpstmt.setInt(3, act_id);

                        tpstmt.executeUpdate();

                        tpstmt.close();
                    }

                    tstmt.close();


                    int tempdate = 0;
                    int temptime = 0;
                    int tempfb = 0;
                    String tempcourse = "";
                    int repCount = 0;

                    PreparedStatement temppstmt = null;
                    PreparedStatement temppstmt2 = null;
                    ResultSet temprs2 = null;

                    Statement tempstmt = con2.createStatement();
                    ResultSet temprs = tempstmt.executeQuery("SELECT date, time, courseName, fb FROM teepastempty WHERE courseName = 'Brookhaven' ORDER BY date, time");

                    while (temprs.next()) {
                        tempdate = temprs.getInt("date");
                        temptime = temprs.getInt("time");
                        tempfb = temprs.getInt("fb");
                        tempcourse = temprs.getString("courseName");

                        temppstmt = con2.prepareStatement("SELECT count(*) as numentries FROM teepastempty WHERE date = ? AND time = ? AND courseName = ? AND fb = ?");
                        temppstmt.clearParameters();
                        temppstmt.setInt(1, tempdate);
                        temppstmt.setInt(2, temptime);
                        temppstmt.setString(3, tempcourse);
                        temppstmt.setInt(4, tempfb);

                        temprs2 = temppstmt.executeQuery();

                        if (temprs2.next()) {

                            if (temprs2.getInt("numentries") > 1) {

                                temppstmt2 = con2.prepareStatement("DELETE FROM teepastempty WHERE date = ? AND time = ? AND courseName = ? AND fb = ? LIMIT 1");
                                temppstmt2.clearParameters();
                                temppstmt2.setInt(1, tempdate);
                                temppstmt2.setInt(2, temptime);
                                temppstmt2.setString(3, tempcourse);
                                temppstmt2.setInt(4, tempfb);

                                temppstmt2.executeUpdate();

                                temppstmt2.close();
                            }
                        }

                        temppstmt.close();
                    }

                    tempstmt.close();
*/
/*
                    int tempdate = 0;
                    int temptime = 0;
                    int tempfb = 0;
                    String tempcourse = "";

                    PreparedStatement temppstmt = null;
                    PreparedStatement temppstmt2 = null;
                    ResultSet temprs2 = null;

                    Statement tempstmt = con2.createStatement();
                    ResultSet temprs = tempstmt.executeQuery("SELECT date, time, courseName, fb FROM teepastempty WHERE courseName = 'Brookhaven' ORDER BY date, time");

                    while (temprs.next()) {
                        tempdate = temprs.getInt("date");
                        temptime = temprs.getInt("time");
                        tempfb = temprs.getInt("fb");
                        tempcourse = temprs.getString("courseName");

                        temppstmt = con2.prepareStatement("SELECT teepast_id FROM teepast2 WHERE date = ? AND time = ? AND courseName = ? AND fb = ?");
                        temppstmt.clearParameters();
                        temppstmt.setInt(1, tempdate);
                        temppstmt.setInt(2, temptime);
                        temppstmt.setString(3, tempcourse);
                        temppstmt.setInt(4, tempfb);

                        temprs2 = temppstmt.executeQuery();

                        if (temprs2.next()) {

                            temppstmt2 = con2.prepareStatement("DELETE FROM teepastempty WHERE date = ? AND time = ? AND courseName = ? AND fb = ?");
                            temppstmt2.clearParameters();
                            temppstmt2.setInt(1, tempdate);
                            temppstmt2.setInt(2, temptime);
                            temppstmt2.setString(3, tempcourse);
                            temppstmt2.setInt(4, tempfb);

                            temppstmt2.executeUpdate();

                            temppstmt2.close();

                        }

                        temppstmt.close();
                    }

                    tempstmt.close();
            */

            /*
                    stmt2.executeUpdate("ALTER TABLE club5 ADD stripalpha tinyint(4) NOT NULL default 0 AFTER stripzero");
                    stmt2.executeUpdate("ALTER TABLE club5 ADD stripdash tinyint(4) NOT NULL default 0 AFTER stripalpha");
            */
            /*
                    stmt2.executeUpdate("ALTER TABLE guestdb ADD allow_tba tinyint(1) NOT NULL default 0");
            */

            /*
                    stmt2.executeUpdate("delete from guest5");

                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Guest',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Family',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Grandchild',0,0,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('~',0,1,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('PGA',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('X  lottery use only',0,0,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Pro',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('University of Tulsa',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Oral Roberts',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Oklahoma State',0,1,'','','','',0,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Rider',0,1,'','','','',1,0)");
                    stmt2.executeUpdate("INSERT INTO guest5 (guest, activity_id, gOpt, gpos, g9pos, gstItem, gst9Item, revenue, use_guestdb) " +
                                                    "VALUES ('Employee',0,1,'','','','',0,0)");
*/

    /*
                    stmt2.executeUpdate("INSERT INTO hotel3 " +
                            "(username, password, name_last, name_first, name_mi, " +
                            "days1, days2, days3, days4, days5, days6, days7, " +
                            "guest1, guest2, guest3, guest4, guest5, " +
                            "guest6, guest7, guest8, guest9, guest10, " +
                            "guest11, guest12, guest13, guest14, guest15, " +
                            "guest16, guest17, guest18, guest19, guest20, " +
                            "guest21, guest22, guest23, guest24, guest25, " +
                            "guest26, guest27, guest28, guest29, guest30, " +
                            "guest31, guest32, guest33, guest34, guest35, " +
                            "guest36, message) " +
                            "VALUES (" +
                            "'passport8', 'book4u', '', '', '', " +
                            "'16','14','14','14','14','14','15', " +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','','','',''," +
                            "'','')");


                    Statement stmt4 = con2.createStatement();

                    ResultSet rs5 = stmt4.executeQuery("SELECT guest_type from hotel3_gtypes WHERE username='passport7'");
                    PreparedStatement pstmt5 = null;

                    while (rs5.next()) {

                        pstmt5 = con2.prepareStatement("INSERT INTO hotel3_gtypes (username, guest_type) VALUES ('passport8',?)");
                        pstmt5.clearParameters();
                        pstmt5.setString(1, rs5.getString("guest_type"));

                        pstmt5.executeUpdate();

                        pstmt5.close();
                    }

                    stmt4.close();


                stmt2.executeUpdate("ALTER TABLE demo_clubs_mfr ADD activity_id int(11) NOT NULL default '0' AFTER id");
     
                stmt2.executeUpdate("ALTER TABLE teecurr2 ADD orig1 varchar(15) NOT NULL default '', " +
                        "ADD orig2 varchar(15) NOT NULL default '', " +
                        "ADD orig3 varchar(15) NOT NULL default '', " +
                        "ADD orig4 varchar(15) NOT NULL default '', " +
                        "ADD orig5 varchar(15) NOT NULL default ''");


                String playerusername = "";
                String playername = "";
                    PreparedStatement pstmtt = null;

                    ResultSet rst = null;
                    rst = stmt2.executeQuery("SELECT username, CONCAT(name_first, ' ', IF(name_mi<>'', CONCAT(name_mi, ' '), ''), name_last) as playername FROM member2b");

                    while (rst.next()) {

                        playerusername = rst.getString("username");
                        playername = rst.getString("playername");

                        pstmtt = con2.prepareStatement("UPDATE teecurr2 SET username1 = ? WHERE player1 = ? AND username1 <> ?");
                        pstmtt.clearParameters();
                        pstmtt.setString(1, playerusername);
                        pstmtt.setString(2, playername);
                        pstmtt.setString(3, playerusername);

                        pstmtt.executeUpdate();

                        pstmtt.close();

                        pstmtt = con2.prepareStatement("UPDATE teecurr2 SET username2 = ? WHERE player2 = ? AND username2 <> ?");
                        pstmtt.clearParameters();
                        pstmtt.setString(1, playerusername);
                        pstmtt.setString(2, playername);
                        pstmtt.setString(3, playerusername);

                        pstmtt.executeUpdate();

                        pstmtt.close();

                        pstmtt = con2.prepareStatement("UPDATE teecurr2 SET username3 = ? WHERE player3 = ? AND username3 <> ?");
                        pstmtt.clearParameters();
                        pstmtt.setString(1, playerusername);
                        pstmtt.setString(2, playername);
                        pstmtt.setString(3, playerusername);

                        pstmtt.executeUpdate();

                        pstmtt.close();

                        pstmtt = con2.prepareStatement("UPDATE teecurr2 SET username4 = ? WHERE player4 = ? AND username4 <> ?");
                        pstmtt.clearParameters();
                        pstmtt.setString(1, playerusername);
                        pstmtt.setString(2, playername);
                        pstmtt.setString(3, playerusername);

                        pstmtt.executeUpdate();

                        pstmtt.close();

                        pstmtt = con2.prepareStatement("UPDATE teecurr2 SET username5 = ? WHERE player5 = ? AND username5 <> ?");
                        pstmtt.clearParameters();
                        pstmtt.setString(1, playerusername);
                        pstmtt.setString(2, playername);
                        pstmtt.setString(3, playerusername);

                        pstmtt.executeUpdate();

                        pstmtt.close();
*/

                    /*
                    stmt2.executeUpdate("DROP TABLE guestdb");
                    stmt2.executeUpdate("CREATE TABLE guestdb (" +
                            "activity_id int(11) NOT NULL default '0', " +
                            "uid enum('N','O','R') NOT NULL default 'N', " +
                            "name enum('N','O','R') NOT NULL default 'R', " +
                            "email enum('N','O','R') NOT NULL default 'N', " +
                            "phone enum('N','O','R') NOT NULL default 'N', " +
                            "address enum('N','O','R') NOT NULL default 'N', " +
                            "gender enum('N','O','R') NOT NULL default 'N', " +
                            "hdcp_num enum('N','O','R') NOT NULL default 'N', " +
                            "hdcp_index enum('N','O','R') NOT NULL default 'N', " +
                            "home_club enum('N','O','R') NOT NULL default 'N', " +
                            "force_uid tinyint(1) NOT NULL default '0', " +
                            "display_uid tinyint(1) NOT NULL default '0' " +
                            ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
                     



                    String newName = "";
                    String memid_new = "";
                    String memid = "";
                    String memNum = "";

                    PreparedStatement pstmtb = null;
                    Statement stmtb = null;
                    ResultSet rsb = null;

                    stmtb = con2.createStatement();

                    rsb = stmtb.executeQuery("SELECT username, memNum, CONCAT(name_first, ' ', IF(name_mi<>'',CONCAT(name_mi, ' '), ''), name_last) as newName FROM member2b WHERE memNum=''");

                    while (rsb.next()) {

                        newName = rsb.getString("newName");
                        memNum = "C" + rsb.getString("memNum");
                        memid = rsb.getString("username");
                        memid_new = "C" + memid;

                        try {

                            pstmtb = con2.prepareStatement("UPDATE member2b SET username = ? WHERE username = ?");
                            pstmtb.clearParameters();
                            pstmtb.setString(1, memid_new);
                            pstmtb.setString(2, memid);

                            pstmtb.executeUpdate();

                            pstmtb.close();

                        } catch (Exception exc2) {
                            out.println("<br>Error: " + exc2.getMessage());
                        }

                         Admin_editmem.updTeecurr(newName, memid_new, memid, con2);      // update teecurr with new values

                         Admin_editmem.updTeepast(newName, memid_new, memid, con2);      // update teepast with new values

                         Admin_editmem.updLreqs(newName, memid_new, memid, con2);        // update lreqs with new values

                         Admin_editmem.updPartner(memid_new, memid, con2);               // update partner with new values

                         Admin_editmem.updEvents(newName, memid_new, memid, con2);        // update evntSignUp with new values

                         Admin_editmem.updLessons(newName, memid_new, memid, con2);       // update the lesson books with new values

                    }

                    stmtb.close();
                     *
                     */
                /*
                // changes for guest database feature
                stmt2.executeUpdate("ALTER TABLE activity_sheets_players DROP guest_id");
                stmt2.executeUpdate("ALTER TABLE activity_sheets_players ADD guest_id INT(11) NOT NULL DEFAULT '0'");

                stmt2.executeUpdate("ALTER TABLE wait_list_signups_players DROP guest_id");
                stmt2.executeUpdate("ALTER TABLE wait_list_signups_players ADD guest_id INT(11) NOT NULL DEFAULT '0'");

                // Update evntsup2b
                stmt2.executeUpdate("ALTER TABLE evntsup2b DROP guest_id1");
                stmt2.executeUpdate("ALTER TABLE evntsup2b DROP guest_id2");
                stmt2.executeUpdate("ALTER TABLE evntsup2b DROP guest_id3");
                stmt2.executeUpdate("ALTER TABLE evntsup2b DROP guest_id4");
                stmt2.executeUpdate("ALTER TABLE evntsup2b DROP guest_id5");
                stmt2.executeUpdate("ALTER TABLE evntsup2b ADD guest_id1 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id2 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id3 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id4 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id5 INT(11) NOT NULL DEFAULT '0'");

                // Update teepast2
                stmt2.executeUpdate("ALTER TABLE teepast2 DROP guest_id1");
                stmt2.executeUpdate("ALTER TABLE teepast2 DROP guest_id2");
                stmt2.executeUpdate("ALTER TABLE teepast2 DROP guest_id3");
                stmt2.executeUpdate("ALTER TABLE teepast2 DROP guest_id4");
                stmt2.executeUpdate("ALTER TABLE teepast2 DROP guest_id5");
                stmt2.executeUpdate("ALTER TABLE teepast2 ADD guest_id1 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id2 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id3 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id4 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id5 INT(11) NOT NULL DEFAULT '0'");

                // Update teecurr2
                stmt2.executeUpdate("ALTER TABLE teecurr2 DROP guest_id1");
                stmt2.executeUpdate("ALTER TABLE teecurr2 DROP guest_id2");
                stmt2.executeUpdate("ALTER TABLE teecurr2 DROP guest_id3");
                stmt2.executeUpdate("ALTER TABLE teecurr2 DROP guest_id4");
                stmt2.executeUpdate("ALTER TABLE teecurr2 DROP guest_id5");
                stmt2.executeUpdate("ALTER TABLE teecurr2 ADD guest_id1 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id2 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id3 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id4 INT(11) NOT NULL DEFAULT '0', " +
                        "ADD guest_id5 INT(11) NOT NULL DEFAULT '0'");
*/
/*
                try {
                    stmt2.executeUpdate("ALTER TABLE club5 ADD website_url varchar(255) NOT NULL DEFAULT '' AFTER email");
                } catch (Exception exc) {
                    out.println("<br>" + club + " Failed. " + exc.getMessage());
                }
 */
/*
                try {
                    stmt2.executeUpdate("DELETE FROM partner WHERE user_id NOT IN (SELECT username FROM member2b) OR partner_id NOT IN (SELECT username FROM member2b)");
                } catch (Exception exc) {
                    out.println("<br>" + club + " Failed. " + exc.getMessage());
                }
 */
                /*
                try {
                    stmt2.executeUpdate("CREATE TABLE guestdb_hosts (" +
                            "host_id int(11) NOT NULL auto_increment, " +
                            "guest_Id int(11) NOT NULL default '0', " +
                            "username varchar(15) NOT NULL default '', " +
                            "PRIMARY KEY  (host_id), " +
                            "UNIQUE KEY username (guest_id, username)" +
                            ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
                } catch (Exception exc) {
                    out.println("<br>" + club + " Failed. " + exc.getMessage());
                }
*/

/*
            try {
            }
            catch (Exception ignore) { out.println("<br>" + club + " Failed 1."); }


            try {
                
            }
            catch (Exception ignore) { out.println("<br>" + club + " Failed 2."); }

*/


            // NEED TO RUN YET

            // also need to drop the (activity_id, name) unique keys on restriction2 and block2 and redo them as (name, activity_id)
/*
        if (!club.startsWith("demo")) {

        }


/*** WAIT A FEW DAYS BEFORE RUNNING THESE DROP STATEMENTS
            stmt2.executeUpdate("ALTER TABLE guestres2 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");


            stmt2.executeUpdate("ALTER TABLE guestqta4 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");


            stmt2.executeUpdate("ALTER TABLE hotel3 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");


*/
/*
 *
 *
 * ********* DO NOT RUN THIS COMMAND FOR FORT COLLINS!!!!!!!!!!!!!!
 *
        try {
            stmt2.executeUpdate("ALTER TABLE club5 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");
        } catch (Exception exc) {

        }
*/
/*
            
            
/*
            // add sort_by field to clubparm2 table
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD sort_by tinyint(4) NOT NULL default '0'");

            // add unique index for username in hotel3
            stmt2.executeUpdate("CREATE UNIQUE INDEX ind1 ON hotel3 (username);");

            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS hotel3_gtypes (" +
                                "id int(11) NOT NULL auto_increment," +
                                "username varchar(15) NOT NULL default ''," +
                                "guest_type varchar(20) NOT NULL default ''," +
                                "PRIMARY KEY (id)," +
                                "UNIQUE KEY key1 (username,guest_type))");

            ResultSet rs4 = stmt3.executeQuery("SELECT * FROM hotel3");

            while (rs4.next()) {

                if (!rs4.getString("guest1").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest1") + "\")");
                if (!rs4.getString("guest2").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest2") + "\")");
                if (!rs4.getString("guest3").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest3") + "\")");
                if (!rs4.getString("guest4").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest4") + "\")");
                if (!rs4.getString("guest5").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest5") + "\")");
                if (!rs4.getString("guest6").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest6") + "\")");
                if (!rs4.getString("guest7").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest7") + "\")");
                if (!rs4.getString("guest8").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest8") + "\")");
                if (!rs4.getString("guest9").equals(""))  stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest9") + "\")");
                if (!rs4.getString("guest10").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest10") + "\")");
                if (!rs4.getString("guest11").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest11") + "\")");
                if (!rs4.getString("guest12").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest12") + "\")");
                if (!rs4.getString("guest13").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest13") + "\")");
                if (!rs4.getString("guest14").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest14") + "\")");
                if (!rs4.getString("guest15").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest15") + "\")");
                if (!rs4.getString("guest16").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest16") + "\")");
                if (!rs4.getString("guest17").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest17") + "\")");
                if (!rs4.getString("guest18").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest18") + "\")");
                if (!rs4.getString("guest19").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest19") + "\")");
                if (!rs4.getString("guest20").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest20") + "\")");
                if (!rs4.getString("guest21").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest21") + "\")");
                if (!rs4.getString("guest22").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest22") + "\")");
                if (!rs4.getString("guest23").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest23") + "\")");
                if (!rs4.getString("guest24").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest24") + "\")");
                if (!rs4.getString("guest25").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest25") + "\")");
                if (!rs4.getString("guest26").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest26") + "\")");
                if (!rs4.getString("guest27").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest27") + "\")");
                if (!rs4.getString("guest28").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest28") + "\")");
                if (!rs4.getString("guest29").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest29") + "\")");
                if (!rs4.getString("guest30").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest30") + "\")");
                if (!rs4.getString("guest31").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest31") + "\")");
                if (!rs4.getString("guest32").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest32") + "\")");
                if (!rs4.getString("guest33").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest33") + "\")");
                if (!rs4.getString("guest34").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest34") + "\")");
                if (!rs4.getString("guest35").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest35") + "\")");
                if (!rs4.getString("guest36").equals("")) stmt2.executeUpdate("INSERT INTO hotel3_gtypes VALUES (NULL, \"" + rs4.getString("username") + "\", \"" + rs4.getString("guest36") + "\")");

            }

            // add new tables to store guest types
            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS guestqta4_gtypes (" +
                                "id int(11) NOT NULL auto_increment," +
                                "guestqta_id int(11) default NULL," +
                                "guest_type varchar(20) NOT NULL default ''," +
                                "PRIMARY KEY (id)," +
                                "UNIQUE KEY key1 (guestqta_id,guest_type))");
 
            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS guestqta4_gtypes (" +
                                "id int(11) NOT NULL auto_increment," +
                                "guestqta_id int(11) default NULL," +
                                "guest_type varchar(20) NOT NULL default ''," +
                                "PRIMARY KEY (id)," +
                                "UNIQUE KEY key1 (guestqta_id,guest_type))");
 
            // populate the new guestres2_gtypes table with the related entries in guestres2
            ResultSet rs4 = stmt3.executeQuery("SELECT * FROM guestres2");
            while (rs4.next()) {

                if (!rs4.getString("guest1").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest1") + "\")");
                if (!rs4.getString("guest2").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest2") + "\")");
                if (!rs4.getString("guest3").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest3") + "\")");
                if (!rs4.getString("guest4").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest4") + "\")");
                if (!rs4.getString("guest5").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest5") + "\")");
                if (!rs4.getString("guest6").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest6") + "\")");
                if (!rs4.getString("guest7").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest7") + "\")");
                if (!rs4.getString("guest8").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest8") + "\")");
                if (!rs4.getString("guest9").equals(""))  stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest9") + "\")");
                if (!rs4.getString("guest10").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest10") + "\")");
                if (!rs4.getString("guest11").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest11") + "\")");
                if (!rs4.getString("guest12").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest12") + "\")");
                if (!rs4.getString("guest13").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest13") + "\")");
                if (!rs4.getString("guest14").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest14") + "\")");
                if (!rs4.getString("guest15").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest15") + "\")");
                if (!rs4.getString("guest16").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest16") + "\")");
                if (!rs4.getString("guest17").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest17") + "\")");
                if (!rs4.getString("guest18").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest18") + "\")");
                if (!rs4.getString("guest19").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest19") + "\")");
                if (!rs4.getString("guest20").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest20") + "\")");
                if (!rs4.getString("guest21").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest21") + "\")");
                if (!rs4.getString("guest22").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest22") + "\")");
                if (!rs4.getString("guest23").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest23") + "\")");
                if (!rs4.getString("guest24").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest24") + "\")");
                if (!rs4.getString("guest25").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest25") + "\")");
                if (!rs4.getString("guest26").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest26") + "\")");
                if (!rs4.getString("guest27").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest27") + "\")");
                if (!rs4.getString("guest28").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest28") + "\")");
                if (!rs4.getString("guest29").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest29") + "\")");
                if (!rs4.getString("guest30").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest30") + "\")");
                if (!rs4.getString("guest31").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest31") + "\")");
                if (!rs4.getString("guest32").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest32") + "\")");
                if (!rs4.getString("guest33").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest33") + "\")");
                if (!rs4.getString("guest34").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest34") + "\")");
                if (!rs4.getString("guest35").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest35") + "\")");
                if (!rs4.getString("guest36").equals("")) stmt2.executeUpdate("INSERT INTO guestres2_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest36") + "\")");
                
            }


            // populate the new guestqta4_gtypes table with the related entries in guestqta4
            rs4 = stmt3.executeQuery("SELECT * FROM guestqta4");
            while (rs4.next()) {

                if (!rs4.getString("guest1").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest1") + "\")");
                if (!rs4.getString("guest2").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest2") + "\")");
                if (!rs4.getString("guest3").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest3") + "\")");
                if (!rs4.getString("guest4").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest4") + "\")");
                if (!rs4.getString("guest5").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest5") + "\")");
                if (!rs4.getString("guest6").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest6") + "\")");
                if (!rs4.getString("guest7").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest7") + "\")");
                if (!rs4.getString("guest8").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest8") + "\")");
                if (!rs4.getString("guest9").equals(""))  stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest9") + "\")");
                if (!rs4.getString("guest10").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest10") + "\")");
                if (!rs4.getString("guest11").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest11") + "\")");
                if (!rs4.getString("guest12").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest12") + "\")");
                if (!rs4.getString("guest13").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest13") + "\")");
                if (!rs4.getString("guest14").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest14") + "\")");
                if (!rs4.getString("guest15").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest15") + "\")");
                if (!rs4.getString("guest16").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest16") + "\")");
                if (!rs4.getString("guest17").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest17") + "\")");
                if (!rs4.getString("guest18").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest18") + "\")");
                if (!rs4.getString("guest19").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest19") + "\")");
                if (!rs4.getString("guest20").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest20") + "\")");
                if (!rs4.getString("guest21").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest21") + "\")");
                if (!rs4.getString("guest22").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest22") + "\")");
                if (!rs4.getString("guest23").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest23") + "\")");
                if (!rs4.getString("guest24").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest24") + "\")");
                if (!rs4.getString("guest25").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest25") + "\")");
                if (!rs4.getString("guest26").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest26") + "\")");
                if (!rs4.getString("guest27").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest27") + "\")");
                if (!rs4.getString("guest28").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest28") + "\")");
                if (!rs4.getString("guest29").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest29") + "\")");
                if (!rs4.getString("guest30").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest30") + "\")");
                if (!rs4.getString("guest31").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest31") + "\")");
                if (!rs4.getString("guest32").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest32") + "\")");
                if (!rs4.getString("guest33").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest33") + "\")");
                if (!rs4.getString("guest34").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest34") + "\")");
                if (!rs4.getString("guest35").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest35") + "\")");
                if (!rs4.getString("guest36").equals("")) stmt2.executeUpdate("INSERT INTO guestqta4_gtypes VALUES (NULL, \"" + rs4.getInt("id") + "\", \"" + rs4.getString("guest36") + "\")");

            }

/*** WAIT A FEW DAYS BEFORE RUNNING THESE DROP STATEMENTS
            stmt2.executeUpdate("ALTER TABLE guestres2 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");


            stmt2.executeUpdate("ALTER TABLE guestqta4 " +
                                "drop guest1,  drop guest2,  drop guest3,  drop guest4,  drop guest5,  drop guest6, " +
                                "drop guest7,  drop guest8,  drop guest9,  drop guest10, drop guest11, drop guest12, " +
                                "drop guest13, drop guest14, drop guest15, drop guest16, drop guest17, drop guest18, " +
                                "drop guest19, drop guest20, drop guest21, drop guest22, drop guest23, drop guest24, " +
                                "drop guest25, drop guest26, drop guest27, drop guest28, drop guest29, drop guest30, " +
                                "drop guest31, drop guest32, drop guest33, drop guest34, drop guest35, drop guest36");
*/
/*
            try {
                stmt2.executeUpdate("ALTER TABLE guestqta4 ADD locations TEXT NOT NULL;");
            } catch (Exception exc) {
                out.println("<br>" + exc.toString());
            }
}
            
     */
/*

 
            // add locations field to guestqta4 table
            stmt2.executeUpdate("ALTER TABLE guestqta4 ADD locations TEXT NOT NULL;");
 
            //
            // ensure every club has these tables - moved from reg code to support_init
            //
            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS teehist (" +
                                "date bigint, " +
                                "day varchar(10), time integer, " +
                                "fb smallint, courseName varchar(30), " +
                                "player1 varchar(43), player2 varchar(43), " +
                                "player3 varchar(43), player4 varchar(43), player5 varchar(43), " +
                                "user varchar(15), mname varchar(50), " +
                                "mdate bigint, sdate varchar(36), type smallint, " +
                                "INDEX ind1 (date, time, fb, courseName))");

            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS teepastempty(" +
                                "date bigint, mm smallint, dd smallint, " +
                                "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                                "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                                "rest_color varchar(24), fb smallint, courseName varchar(30), " +
                                "proNew integer, proMod integer, memNew integer, memMod integer, " +
                                "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                                "index ind1 (date, time, fb, courseName), " +
                                "index ind2 (date, courseName))");


            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS sessionlog (" +
                                "date bigint, sdate varchar(36), msg text, " +
                                "index ind1 (date))");


            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS tmodes(" +
                                "courseName CHAR(30) NOT NULL, " +
                                "tmodea CHAR(3) NOT NULL, " +
                                "tmode CHAR(20) NOT NULL, " +
                                "PRIMARY KEY (courseName, tmodea));");


            // add limited access settings for tee sheet notes
            stmt2.executeUpdate("ALTER TABLE login2 ADD TS_NOTES_VIEW tinyint(4) NOT NULL default 1 AFTER TS_PAST_UPDATE;");
            stmt2.executeUpdate("ALTER TABLE login2 ADD TS_NOTES_UPDATE tinyint(4) NOT NULL default 1 AFTER TS_NOTES_VIEW;");

            // build tee sheet notes table
            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS activity_sheet_notes ( " +
                                "note_id int(11) NOT NULL auto_increment, " +
                                "activity_id int(11) NOT NULL default '0', " +
                                "`date` date NOT NULL default '0000-00-00', " +
                                "notes text NOT NULL, " +
                                "PRIMARY KEY (note_id), " +
                                "UNIQUE KEY ind1 (activity_id,`date`) " +
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            // add two new fields (seamless_caller, allow_mobile) to club5
            stmt2.executeUpdate("ALTER TABLE club5 " +
                    "ADD seamless_caller varchar(24) NOT NULL default '', " +
                    "ADD allow_mobile tinyint NOT NULL default '0';");
 
            //
            stmt2.executeUpdate("ALTER TABLE guest5 DROP KEY guest;");
            stmt2.executeUpdate("ALTER TABLE guest5 ADD UNIQUE KEY key1 (guest,activity_id)");


            // changes for guest database feature
            stmt2.executeUpdate("ALTER TABLE activity_sheets_players ADD guest_id INT(11) NOT NULL DEFAULT '0' AFTER userg");
            stmt2.executeUpdate("ALTER TABLE wait_list_signups_players ADD guest_id INT(11) NOT NULL DEFAULT '0' AFTER username");
            stmt2.executeUpdate("ALTER TABLE activities ADD guestdb tinyint(4) NOT NULL DEFAULT '0' AFTER consec_pro");
            stmt2.executeUpdate("ALTER TABLE club5 ADD guestdb tinyint(4) NOT NULL DEFAULT '0' AFTER dining");
            stmt2.executeUpdate("ALTER TABLE guest5 ADD use_guestdb tinyint(4) NOT NULL DEFAULT '0' AFTER revenue");

            // Update lreqs3
            stmt2.executeUpdate("ALTER TABLE lreqs3 ADD guest_id1 INT(11) NOT NULL DEFAULT '0' AFTER userg25, " +
                    "ADD guest_id2 INT(11) NOT NULL DEFAULT '0' AFTER guest_id1, " +
                    "ADD guest_id3 INT(11) NOT NULL DEFAULT '0' AFTER guest_id2, " +
                    "ADD guest_id4 INT(11) NOT NULL DEFAULT '0' AFTER guest_id3, " +
                    "ADD guest_id5 INT(11) NOT NULL DEFAULT '0' AFTER guest_id4, " +
                    "ADD guest_id6 INT(11) NOT NULL DEFAULT '0' AFTER guest_id5, " +
                    "ADD guest_id7 INT(11) NOT NULL DEFAULT '0' AFTER guest_id6, " +
                    "ADD guest_id8 INT(11) NOT NULL DEFAULT '0' AFTER guest_id7, " +
                    "ADD guest_id9 INT(11) NOT NULL DEFAULT '0' AFTER guest_id8, " +
                    "ADD guest_id10 INT(11) NOT NULL DEFAULT '0' AFTER guest_id9, " +
                    "ADD guest_id11 INT(11) NOT NULL DEFAULT '0' AFTER guest_id10, " +
                    "ADD guest_id12 INT(11) NOT NULL DEFAULT '0' AFTER guest_id11, " +
                    "ADD guest_id13 INT(11) NOT NULL DEFAULT '0' AFTER guest_id12, " +
                    "ADD guest_id14 INT(11) NOT NULL DEFAULT '0' AFTER guest_id13, " +
                    "ADD guest_id15 INT(11) NOT NULL DEFAULT '0' AFTER guest_id14, " +
                    "ADD guest_id16 INT(11) NOT NULL DEFAULT '0' AFTER guest_id15, " +
                    "ADD guest_id17 INT(11) NOT NULL DEFAULT '0' AFTER guest_id16, " +
                    "ADD guest_id18 INT(11) NOT NULL DEFAULT '0' AFTER guest_id17, " +
                    "ADD guest_id19 INT(11) NOT NULL DEFAULT '0' AFTER guest_id18, " +
                    "ADD guest_id20 INT(11) NOT NULL DEFAULT '0' AFTER guest_id19, " +
                    "ADD guest_id21 INT(11) NOT NULL DEFAULT '0' AFTER guest_id20, " +
                    "ADD guest_id22 INT(11) NOT NULL DEFAULT '0' AFTER guest_id21, " +
                    "ADD guest_id23 INT(11) NOT NULL DEFAULT '0' AFTER guest_id22, " +
                    "ADD guest_id24 INT(11) NOT NULL DEFAULT '0' AFTER guest_id23, " +
                    "ADD guest_id25 INT(11) NOT NULL DEFAULT '0' AFTER guest_id24");
 
            // Update evntsup2b
            stmt2.executeUpdate("ALTER TABLE evntsup2b ADD guest_id1 INT(11) NOT NULL DEFAULT '0' AFTER userg5, " +
                    "ADD guest_id2 INT(11) NOT NULL DEFAULT '0' AFTER guest_id1, " +
                    "ADD guest_id3 INT(11) NOT NULL DEFAULT '0' AFTER guest_id2, " +
                    "ADD guest_id4 INT(11) NOT NULL DEFAULT '0' AFTER guest_id3, " +
                    "ADD guest_id5 INT(11) NOT NULL DEFAULT '0' AFTER guest_id4");

            // Update teepast2
            stmt2.executeUpdate("ALTER TABLE teepast2 ADD guest_id1 INT(11) NOT NULL DEFAULT '0' AFTER userg5, " +
                    "ADD guest_id2 INT(11) NOT NULL DEFAULT '0' AFTER guest_id1, " +
                    "ADD guest_id3 INT(11) NOT NULL DEFAULT '0' AFTER guest_id2, " +
                    "ADD guest_id4 INT(11) NOT NULL DEFAULT '0' AFTER guest_id3, " +
                    "ADD guest_id5 INT(11) NOT NULL DEFAULT '0' AFTER guest_id4");
 
            // Update teecurr2
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD guest_id1 INT(11) NOT NULL DEFAULT '0' AFTER userg5, " +
                    "ADD guest_id2 INT(11) NOT NULL DEFAULT '0' AFTER guest_id1, " +
                    "ADD guest_id3 INT(11) NOT NULL DEFAULT '0' AFTER guest_id2, " +
                    "ADD guest_id4 INT(11) NOT NULL DEFAULT '0' AFTER guest_id3, " +
                    "ADD guest_id5 INT(11) NOT NULL DEFAULT '0' AFTER guest_id4");
 
            // Create guestdb_bindings table
            stmt2.executeUpdate("CREATE TABLE guestdb_bindings (" +
                    "binding_id int(11) NOT NULL auto_increment, " +
                    "username varchar(15) NOT NULL default '', " +
                    "guest_Id int(11) NOT NULL default '0', " +
                    "PRIMARY KEY  (binding_id), " +
                    "UNIQUE KEY username (username,guest_Id)" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            // Create guestdb_data table
            stmt2.executeUpdate("CREATE TABLE guestdb_data (" +
                    "guest_id int(11) NOT NULL auto_increment, " +
                    "unique_id varchar(24) default NULL, " +
                    "uid_type varchar(24) NOT NULL default '', " +
                    "name_pre varchar(4) NOT NULL default '', " +
                    "name_first varchar(20) NOT NULL default '', " +
                    "name_mi char(1) NOT NULL default '', " +
                    "name_last varchar(20) NOT NULL default '', " +
                    "name_suf varchar(4) NOT NULL default '', " +
                    "email1 varchar(50) NOT NULL default '', " +
                    "email2 varchar(50) NOT NULL default '', " +
                    "email_bounced1 tinyint(4) NOT NULL default '0', " +
                    "email_bounced2 tinyint(4) NOT NULL default '0', " +
                    "emailOpt tinyint(4) NOT NULL default '0', " +
                    "phone1 varchar(24) NOT NULL default '', " +
                    "phone2 varchar(24) NOT NULL default '', " +
                    "address1 varchar(64) NOT NULL default '', " +
                    "address2 varchar(64) NOT NULL default '', " +
                    "city varchar(30) NOT NULL default '', " +
                    "state varchar(24) NOT NULL default '', " +
                    "zip varchar(10) NOT NULL default '', " +
                    "gender enum('','M','F') NOT NULL default '', " +
                    "hdcp_num varchar(16) NOT NULL default '', " +
                    "hdcp_index double NOT NULL default '-99', " +
                    "home_club varchar(30) NOT NULL default '', " +
                    "count int(11) NOT NULL default '0', " +
                    "PRIMARY KEY  (guest_id), " +
                    "UNIQUE KEY unique_id (unique_id)" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
 
            // Create guestdb table
            stmt2.executeUpdate("CREATE TABLE guestdb (" +
                    "activity_id int(11) NOT NULL default '0', " +
                    "ask_name tinyint(1) NOT NULL default '0', " +
                    "ask_email tinyint(1) NOT NULL default '0', " +
                    "ask_phone tinyint(1) NOT NULL default '0', " +
                    "ask_address tinyint(1) NOT NULL default '0', " +
                    "ask_gender tinyint(1) NOT NULL default '0', " +
                    "ask_hdcp_num tinyint(1) NOT NULL default '0', " +
                    "ask_hdcp_index tinyint(1) NOT NULL default '0', " +
                    "ask_home_club tinyint(1) NOT NULL default '0', " +
                    "req_uid tinyint(1) NOT NULL default '0', " +
                    "req_name tinyint(1) NOT NULL default '0', " +
                    "req_email tinyint(1) NOT NULL default '0', " +
                    "req_phone tinyint(1) NOT NULL default '0', " +
                    "req_address tinyint(1) NOT NULL default '0', " +
                    "req_gender tinyint(1) NOT NULL default '0', " +
                    "req_hdcp_num tinyint(1) NOT NULL default '0', " +
                    "req_hdcp_index tinyint(1) NOT NULL default '0', " +
                    "req_home_club tinyint(1) NOT NULL default '0', " +
                    "force_uid tinyint(1) NOT NULL default '0', " +
                    "display_uid tinyint(1) NOT NULL default '0'" +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
 
            // add new fields to activity_sheets for consecutive times
            stmt2.executeUpdate("ALTER TABLE activity_sheets " +
                    "ADD related_ids text NOT NULL default '', " +
                    "ADD report_ignore tinyint NOT NULL default '0';");
            
            // add unique index for mobile_user field in member2b
            stmt2.executeUpdate("CREATE UNIQUE INDEX ind4 ON member2b (mobile_user);");
 
            // add new mobile fields to member2b
            stmt2.executeUpdate("ALTER TABLE member2b " +
                    "ADD mobile_user varchar(15) DEFAULT NULL, " +
                    "ADD mobile_pass varchar(15) DEFAULT NULL," +
                    "ADD mobile_count int NOT NULL DEFAULT '0'," +
                    "ADD mobile_iphone int NOT NULL DEFAULT '0';");

            // add consecutive fields to activities table
            stmt2.executeUpdate("ALTER TABLE activities ADD consec_mem tinyint NOT NULL default '0' AFTER allowable_views;");
            stmt2.executeUpdate("ALTER TABLE activities ADD consec_pro tinyint NOT NULL default '0' AFTER consec_mem;");

            try {

                Statement stmtp = con2.createStatement();
                PreparedStatement pstmtp = null;

                stmtp.executeUpdate("DELETE FROM partner");  // wipe out all current entries

                stmtp.close();

                stmtp = con2.createStatement();


                ResultSet rsp = stmtp.executeQuery("SELECT * FROM buddy WHERE buddy1<>''");

                while (rsp.next()) {

                    String user_id = rsp.getString("username");
                    String partner_id = "";

                    int activity_id = rsp.getInt("activity_id");

                    for (int j=1; j<=25; j++) {

                        if (rsp.getString("user" + j) != null && !rsp.getString("user" + j).equals("")) {

                            partner_id = rsp.getString("user" + j);

                            try {

                                pstmtp = con2.prepareStatement("INSERT INTO partner (user_id, activity_id, partner_id, priority) " +
                                        "VALUES(?,?,?,?)");
                                pstmtp.clearParameters();
                                pstmtp.setString(1, user_id);
                                pstmtp.setInt(2, activity_id);
                                pstmtp.setString(3, partner_id);
                                pstmtp.setInt(4, j);

                                pstmtp.executeUpdate();

                                pstmtp.close();
                            } catch (Exception exc) {
                                //out.println("<br>" + user_id + " Failed: " + exc.getMessage());
                            }

                        } else {
                            break;
                        }
                    }
                }

                stmtp.close();

            } catch (Exception exc) {
                out.println("<br>" + club + " Failed: " + exc.getMessage());
            }


            try {


                stmt2.executeUpdate("CREATE TABLE partner (" +
                        "id int(11) NOT NULL auto_increment, " +
                        "user_id varchar(15) NOT NULL default '', " +
                        "activity_id int(11) NOT NULL default '0', " +
                        "partner_id varchar(15) NOT NULL default '', " +
                        "priority int(11) NOT NULL default '1', " +
                        "PRIMARY KEY  (id), " +
                        "UNIQUE KEY user_id (user_id,activity_id,partner_id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            } catch (Exception exc) {
                out.println("<br>" + club + " Failed: " + exc.getMessage());
            }
*/
/*


            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS activity_sheet_history ( " +
                                "history_id int(11) NOT NULL auto_increment, " +
                                "sheet_id int(11) NOT NULL default '0', " +
                                "date_time datetime NOT NULL default '0000-00-00 00:00:00', " +
                                "username varchar(15) NOT NULL default '', " +
                                "players text NOT NULL, " +
                                "PRIMARY KEY  (history_id) " +
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS activity_sheets ( " +
                                "sheet_id int(11) NOT NULL auto_increment, " +
                                "activity_id int(11) NOT NULL default '0', " +
                                "date_time datetime NOT NULL default '0000-00-00 00:00:00', " +
                                "event_id int(11) NOT NULL default '0', " +
                                "lesson_id int(11) NOT NULL default '0', " +
                                "rest_id int(11) NOT NULL default '0', " +
                                "blocker_id int(11) NOT NULL default '0', " +
                                "auto_blocked tinyint(4) NOT NULL default '0', " +
                                "in_use_by varchar(15) NOT NULL default '', " +
                                "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " +
                                "notes text NOT NULL, " +
                                "hideNotes tinyint(4) NOT NULL default '0', " +
                                "last_mod_by varchar(15) NOT NULL default '', " +
                                "last_mod_date datetime NOT NULL default '0000-00-00 00:00:00', " +
                                "proNew tinyint(4) NOT NULL default '0', " +
                                "proMod tinyint(4) NOT NULL default '0', " +
                                "memNew tinyint(4) NOT NULL default '0', " +
                                "memMod tinyint(4) NOT NULL default '0', " +
                                "disallow_joins tinyint(4) NOT NULL default '0', " +
                                "PRIMARY KEY  (sheet_id) " +
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS activity_sheets_players ( " +
                                "activity_sheets_player_id int(11) NOT NULL auto_increment, " +
                                "activity_sheet_id int(11) NOT NULL default '0', " +
                                "username varchar(15) NOT NULL default '', " +
                                "userg varchar(12) NOT NULL default '', " +
                                "player_name varchar(64) NOT NULL default '', " +
                                "pos tinyint(4) NOT NULL default '0', " +
                                "`show` tinyint(4) NOT NULL default '0', " +
                                "PRIMARY KEY  (activity_sheets_player_id), " +
                                "UNIQUE KEY pos (activity_sheet_id,pos) " +
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            stmt2.executeUpdate("ALTER TABLE activities ADD common_name varchar(16) NOT NULL default '' AFTER activity_name;");
            stmt2.executeUpdate("ALTER TABLE activities ADD first_time int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD last_time int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD `interval` int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD alt_interval int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD disallow_joins tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD use_hdcp_equiv tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD hdcp_equiv_name varchar(16) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE activities ADD hdcp_joining_range double(3,1) NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE activities ADD allowable_views varchar(8) NOT NULL default '1,2,3';");
            stmt2.executeUpdate("ALTER TABLE activities ADD enabled tinyint NOT NULL default 1;");
 
            stmt2.executeUpdate("UPDATE demo_clubs dc SET type_id = (SELECT dct.id FROM demo_clubs_types dct where dct.type = dc.type)");
            stmt2.executeUpdate("ALTER TABLE member2b ADD ntrp_rating double(2,1) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE member2b ADD usta_num varchar(16) NOT NULL default '';");

            stmt2.executeUpdate(
                    "CREATE TABLE demo_clubs_types (" +
                    "id int(11) NOT NULL auto_increment, " +
                    "activity_id int(11) NOT NULL default '0', " +
                    "type varchar(32) NOT NULL default '', " +
                    "PRIMARY KEY  (id), " +
                    "UNIQUE KEY type (activity_id, type) " +
                    ") ENGINE=MyISAM DEFAULT CHARSET=latin1");

            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Driver')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Wood')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Hybrid')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Iron')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Wedge')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Putter')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Specialty')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Other')");
            stmt2.executeUpdate("INSERT INTO demo_clubs_types (activity_id,type) VALUES (0, 'Rental')");
         
            stmt2.executeUpdate("DROP INDEX name ON restriction2");
            stmt2.executeUpdate("ALTER TABLE guestres2 ADD UNIQUE key1 (name, activity_id);");

            stmt2.executeUpdate("ALTER TABLE restriction2 ADD locations TEXT NOT NULL;");
            stmt2.executeUpdate("ALTER TABLE guestres2 ADD locations TEXT NOT NULL;");
            stmt2.executeUpdate("ALTER TABLE block2 ADD locations TEXT NOT NULL;");

            // update evntsup2b to not allow null values again
            stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                                "CHANGE homeclub1 homeclub1 varchar(50) NOT NULL DEFAULT '', " +
                                "CHANGE homeclub2 homeclub2 varchar(50) NOT NULL DEFAULT '', " +
                                "CHANGE homeclub3 homeclub3 varchar(50) NOT NULL DEFAULT '', " +
                                "CHANGE homeclub4 homeclub4 varchar(50) NOT NULL DEFAULT '', " +
                                "CHANGE homeclub5 homeclub5 varchar(50) NOT NULL DEFAULT '', " +
                                "CHANGE address1 address1 varchar(64) NOT NULL DEFAULT '', " +
                                "CHANGE address2 address2 varchar(64) NOT NULL DEFAULT '', " +
                                "CHANGE address3 address3 varchar(64) NOT NULL DEFAULT '', " +
                                "CHANGE address4 address4 varchar(64) NOT NULL DEFAULT '', " +
                                "CHANGE address5 address5 varchar(64) NOT NULL DEFAULT '';");

            // repair the text nulls
            stmt2.executeUpdate("UPDATE evntsup2b SET homeclub1 = '' WHERE homeclub1 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET homeclub2 = '' WHERE homeclub2 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET homeclub3 = '' WHERE homeclub3 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET homeclub4 = '' WHERE homeclub4 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET homeclub5 = '' WHERE homeclub5 = 'null';");

            stmt2.executeUpdate("UPDATE evntsup2b SET address1 = '' WHERE address1 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET address2 = '' WHERE address2 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET address3 = '' WHERE address3 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET address4 = '' WHERE address4 = 'null';");
            stmt2.executeUpdate("UPDATE evntsup2b SET address5 = '' WHERE address5 = 'null';");

 
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD clinic tinyint NOT NULL default 0;");

            stmt2.executeUpdate("ALTER TABLE demo_clubs ADD activity_id INT NOT NULL default 0 AFTER id;");

            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD edate bigint NOT NULL default 0 AFTER date;");
            stmt2.executeUpdate("ALTER TABLE lessontype5 ADD locations TEXT NOT NULL;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD locations TEXT NOT NULL;");

            //
            //  Add Primary Key 'lesson_id' field to lessongrp5 (forgot to do it replaction safe!!!)
            //
            stmt2.executeUpdate("ALTER TABLE lessongrp5 DROP lesson_id");
            stmt2.executeUpdate("CREATE TABLE t1 LIKE lessongrp5");
            stmt2.executeUpdate("ALTER TABLE t1 ADD lesson_id INT AUTO_INCREMENT PRIMARY KEY FIRST");
            stmt2.executeUpdate("INSERT INTO t1 (proid, activity_id, lname, date, stime, etime, max, cost, color, descript," +
                    "sunday, monday, tuesday, wednesday, thursday, friday, saturday, eo_week) " +
                    "(SELECT proid, activity_id, lname, date, stime, etime, max, cost, color, descript," +
                     "sunday, monday, tuesday, wednesday, thursday, friday, saturday, eo_week " +
                     "FROM lessongrp5 ORDER BY proid, activity_id, lname, date, stime, etime, max, cost, color, descript," +
                     "sunday, monday, tuesday, wednesday, thursday, friday, saturday, eo_week)");
            stmt2.executeUpdate("DROP TABLE lessongrp5");
            stmt2.executeUpdate("ALTER TABLE t1 RENAME lessongrp5");

            stmt2.executeUpdate("ALTER TABLE lessonblock5 ADD activity_id INT NOT NULL default 0 AFTER proid;");

            stmt2.executeUpdate("ALTER TABLE lessonbook5 ADD activity_id INT NOT NULL default 0 AFTER proid;");

            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD lesson_id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD activity_id INT NOT NULL default 0 AFTER proid;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD sunday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD monday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD tuesday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD wednesday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD thursday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD friday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD saturday tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessongrp5 ADD eo_week tinyint NOT NULL default 0;");

            stmt2.executeUpdate("ALTER TABLE lessontime5 ADD activity_id INT NOT NULL default 0 AFTER proid;");

            stmt2.executeUpdate("ALTER TABLE lessontype5 ADD activity_id INT NOT NULL default 0 AFTER proid;");

            stmt2.executeUpdate("ALTER TABLE lgrpsignup5 ADD lesson_id INT NOT NULL default 0 FIRST;");

            stmt2.executeUpdate("ALTER TABLE lreqs3 ADD fail_code tinyint NOT NULL default 0;");

            stmt2.executeUpdate("ALTER TABLE events2b ADD inactive tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE evntsup2b ADD inactive tinyint NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE lessonpro5 ADD activity_id INT NOT NULL default 0;");
 
            stmt2.executeUpdate("ALTER TABLE buddy DROP INDEX ind1;");
            stmt2.executeUpdate("ALTER TABLE buddy ADD activity_id INT NOT NULL default 0 AFTER username;");
            stmt2.executeUpdate("ALTER TABLE buddy ADD UNIQUE key1 (username, activity_id);");
            
            stmt2.executeUpdate("ALTER TABLE events2b ADD event_id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            stmt2.executeUpdate("ALTER TABLE events2b ADD activity_id INT NOT NULL default 0 AFTER name;");
            stmt2.executeUpdate("ALTER TABLE events2b ADD locations TEXT NOT NULL;");
 
            stmt2.executeUpdate("ALTER TABLE block2 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            stmt2.executeUpdate("ALTER TABLE block2 ADD activity_id int NOT NULL default 0 AFTER name;");
            stmt2.executeUpdate("ALTER TABLE block2 ADD UNIQUE key1 (activity_id,name);");

            stmt2.executeUpdate("ALTER TABLE member2b ADD default_activity_id int NOT NULL default '0';");
 
            stmt2.executeUpdate("ALTER TABLE login2 ADD REST_OVERRIDE tinyint(4) NOT NULL default 1 AFTER REPORTS;");
            stmt2.executeUpdate("ALTER TABLE login2 ADD TS_PAST_VIEW tinyint(4) NOT NULL default 1 AFTER TS_PACE_UPDATE;");
            stmt2.executeUpdate("ALTER TABLE login2 ADD TS_PAST_UPDATE tinyint(4) NOT NULL default 1 AFTER TS_PAST_VIEW;");

            stmt2.executeUpdate("ALTER TABLE guestqta4 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            stmt2.executeUpdate("ALTER TABLE guestqta4 ADD activity_id int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE guestqta4 ADD UNIQUE key1 (activity_id,name);");

            stmt2.executeUpdate("ALTER TABLE restriction2 ADD activity_id int NOT NULL default 0 AFTER id;");
            stmt2.executeUpdate("ALTER TABLE restriction2 ADD UNIQUE key1 (activity_id,name);");

            stmt2.executeUpdate("ALTER TABLE mnumres2 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            stmt2.executeUpdate("ALTER TABLE mnumres2 ADD activity_id int NOT NULL default 0 AFTER name;");
            stmt2.executeUpdate("ALTER TABLE mnumres2 ADD UNIQUE key1 (activity_id,name);");

            stmt2.executeUpdate("DROP INDEX mship ON mship5");
            stmt2.executeUpdate("ALTER TABLE mship5 ADD UNIQUE key1 (activity_id,mship);");

            // add new fields to club5
            stmt2.executeUpdate("ALTER TABLE club5 " +
                                    "ADD foretees_mode int NOT NULL default 0," +
                                    "ADD genrez_mode int NOT NULL default 0;");

            stmt2.executeUpdate("UPDATE club5 SET foretees_mode = 1 WHERE clubName <> '';");

            // Add activity_id to mship5, guest5, guestres2
            stmt2.executeUpdate("ALTER TABLE guest5 ADD activity_id int NOT NULL default 0 AFTER guest;");
            stmt2.executeUpdate("ALTER TABLE mship5 ADD activity_id int NOT NULL default 0 AFTER mship;");
            stmt2.executeUpdate("ALTER TABLE guestres2 ADD activity_id int NOT NULL default 0 AFTER id;");

            stmt2.executeUpdate("ALTER TABLE login2 ADD activity_id int NOT NULL default 0 AFTER username;");
            stmt2.executeUpdate("ALTER TABLE login2 ADD default_entry tinyint NOT NULL default 0 AFTER activity_id;");
            stmt2.executeUpdate("ALTER TABLE login2 ADD UNIQUE key1 (username,activity_id);");
                
            // add activities table
            stmt2.executeUpdate("CREATE TABLE activities (" + 
                                  "activity_id int(11) NOT NULL auto_increment," + 
                                  "parent_id int(11) NOT NULL default '0'," + 
                                  "activity_name varchar(32) NOT NULL default ''," + 
                                  "max_players tinyint(4) NOT NULL default '0'," + 
                                  "allow_guests tinyint(4) NOT NULL default '0'," + 
                                  "allow_x tinyint(4) NOT NULL default '0'," + 
                                  "xhrs tinyint(4) NOT NULL default '0'," + 
                                  "contact varchar(40) NOT NULL default ''," + 
                                  "email varchar(64) NOT NULL default ''," + 
                                  "emailOpt tinyint(4) NOT NULL default '0'," + 
                                  "unacompGuest tinyint(4) NOT NULL default '0'," + 
                                  "forceg tinyint(4) NOT NULL default '0'," + 
                                  "hndcpProSheet tinyint(4) NOT NULL default '0'," + 
                                  "hndcpMemSheet tinyint(4) NOT NULL default '0'," + 
                                  "rndsperday tinyint(4) NOT NULL default '0'," + 
                                  "minutesbtwn smallint(8) NOT NULL default '0'," + 
                                  "PRIMARY KEY  (activity_id)" + 
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
*/
            
            
/*
        if (club.equals("valleyclub")) {
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy1 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b1cw = member2b.wc " +
                    "WHERE user1 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy2 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b2cw = member2b.wc " +
                    "WHERE user2 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy3 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b3cw = member2b.wc " +
                    "WHERE user3 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy4 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b4cw = member2b.wc " +
                    "WHERE user4 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy5 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b5cw = member2b.wc " +
                    "WHERE user5 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy6 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b6cw = member2b.wc " +
                    "WHERE user6 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy7 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b7cw = member2b.wc " +
                    "WHERE user7 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy8 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b8cw = member2b.wc " +
                    "WHERE user8 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy9 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b9cw = member2b.wc " +
                    "WHERE user9 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy10 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b10cw = member2b.wc " +
                    "WHERE user10 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy11 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b11cw = member2b.wc " +
                    "WHERE user11 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy12 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b12cw = member2b.wc " +
                    "WHERE user12 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy13 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b13cw = member2b.wc " +
                    "WHERE user13 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy14 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b14cw = member2b.wc " +
                    "WHERE user14 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy15 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b15cw = member2b.wc " +
                    "WHERE user15 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy16 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b16cw = member2b.wc " +
                    "WHERE user16 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy17 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b17cw = member2b.wc " +
                    "WHERE user17 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy18 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b18cw = member2b.wc " +
                    "WHERE user18 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy19 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b19cw = member2b.wc " +
                    "WHERE user19 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy20 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b20cw = member2b.wc " +
                    "WHERE user20 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy21 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b21cw = member2b.wc " +
                    "WHERE user21 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy22 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b22cw = member2b.wc " +
                    "WHERE user22 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy23 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b23cw = member2b.wc " +
                    "WHERE user23 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy24 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b24cw = member2b.wc " +
                    "WHERE user24 = member2b.username;");
            
            stmt2.executeUpdate("UPDATE buddy, member2b SET " +
                    "buddy25 = CONCAT(member2b.name_first, ' ', IF(member2b.name_mi='','',CONCAT(member2b.name_mi, ' ')), member2b.name_last), " +
                    "b25cw = member2b.wc " +
                    "WHERE user25 = member2b.username;");
        }
*/

/*
        if (!club.equals("demov4")) {

            // ADD NEW iCal FIELD TO MEMBER2B
            stmt2.executeUpdate("ALTER TABLE member2b " +
                    "ADD iCal1 tinyint NOT NULL default '-1', " +
                    "ADD iCal2 tinyint NOT NULL default '-1';");

            // ADD NEW iCal FIELD TO LESSONPRO5
            stmt2.executeUpdate("ALTER TABLE lessonpro5 " +
                    "ADD iCal1 tinyint NOT NULL default 0, " +
                    "ADD iCal2 tinyint NOT NULL default 0;");

        }
*/
            
/*
            // increase mname field in teehist table
            stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                                "CHANGE homeclub1 homeclub1 varchar(50), " +
                                "CHANGE homeclub2 homeclub2 varchar(50), " +
                                "CHANGE homeclub3 homeclub3 varchar(50), " +
                                "CHANGE homeclub4 homeclub4 varchar(50), " +
                                "CHANGE homeclub5 homeclub5 varchar(50), " +
                                "CHANGE address1 address1 varchar(64), " +
                                "CHANGE address2 address2 varchar(64), " +
                                "CHANGE address3 address3 varchar(64), " +
                                "CHANGE address4 address4 varchar(64), " +
                                "CHANGE address5 address5 varchar(64);");
*/

            // ADD NEW HIDETEETIME FIELD TO TEECURR2

            //stmt2.executeUpdate("ALTER TABLE teecurr2 ADD hideit tinyint NOT NULL default 0;");

            
/*
            // POPULATE THE NEW GTYPE FIELDS IN TEEPAST2

            String player1 = "";
            String player2 = "";
            String player3 = "";
            String player4 = "";
            String player5 = "";

            String user1 = "";
            String user2 = "";
            String user3 = "";
            String user4 = "";
            String user5 = "";
            
            String gtype1 = "";
            String gtype2 = "";
            String gtype3 = "";
            String gtype4 = "";
            String gtype5 = "";

            int grev1 = 0;
            int grev2 = 0;
            int grev3 = 0;
            int grev4 = 0;
            int grev5 = 0;

            String [] gtypes = new String[36];
            int [] grevs = new int[36];
            
            int x = 0;
            int l = 0;
            int tid = 0;

            //if (club.equals("demov4")) {

                // load up all the guest types for this club
                rs2 = stmt2.executeQuery("SELECT guest, revenue FROM guest5");

                while (rs2.next()) {

                    gtypes[x] = rs2.getString(1);
                    grevs[x] = rs2.getInt(2);
                    x++;
                }

                out.println("<br>Found " + x + " guest types.");
                
                int g = 0;
                if (x > 0) g = x - 1;

                rs2 = stmt2.executeQuery("SELECT * FROM teepast2 WHERE date > 20090400");

                while (rs2.next() && g > 0) {

                    gtype1 = "";
                    gtype2 = "";
                    gtype3 = "";
                    gtype4 = "";
                    gtype5 = "";
                    grev1 = 0;
                    grev2 = 0;
                    grev3 = 0;
                    grev4 = 0;
                    grev5 = 0;

                    tid = rs2.getInt("teepast_id");
                    user1 = rs2.getString("username1");
                    user2 = rs2.getString("username2");
                    user3 = rs2.getString("username3");
                    user4 = rs2.getString("username4");
                    user5 = rs2.getString("username5");

                    player1 = rs2.getString("player1");
                    player2 = rs2.getString("player2");
                    player3 = rs2.getString("player3");
                    player4 = rs2.getString("player4");
                    player5 = rs2.getString("player5");

                    gtype1 = rs2.getString("gtype1");
                    gtype2 = rs2.getString("gtype2");
                    gtype3 = rs2.getString("gtype3");
                    gtype4 = rs2.getString("gtype4");
                    gtype5 = rs2.getString("gtype5");

                    grev1 = rs2.getInt("grev1");
                    grev2 = rs2.getInt("grev2");
                    grev3 = rs2.getInt("grev3");
                    grev4 = rs2.getInt("grev4");
                    grev5 = rs2.getInt("grev5");

                    // if not a member but player present
                    if (user1.equals("") && !player1.equals("") && !gtype1.equals("")) {

                        // isolate guest type
                        loop1:
                        for (x=0; x<=g; x++) {
                            try {
                            if (player1.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                                gtype1 = gtypes[x];
                                grev1 = grevs[x];
                                break;
                            }
                            } catch (IndexOutOfBoundsException ignore) {}
                        }

                    }

                    // if not a member
                    if (user2.equals("") && !player2.equals("") && !gtype2.equals("")) {

                        // isolate guest type
                        loop2:
                        for (x=0; x<=g; x++) {

                            try {
                            if (player2.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                                gtype2 = gtypes[x];
                                grev2 = grevs[x];
                                break;
                            }
                            } catch (IndexOutOfBoundsException ignore) {}
                        }

                    }

                    // if not a member
                    if (user3.equals("") && !player3.equals("") && !gtype3.equals("")) {

                        // isolate guest type
                        loop3:
                        for (x=0; x<=g; x++) {

                            try {
                            if (player3.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                                gtype3 = gtypes[x];
                                grev3 = grevs[x];
                                break;
                            }
                            } catch (IndexOutOfBoundsException ignore) {}
                        }

                    }

                    // if not a member
                    if (user4.equals("") && !player4.equals("") && !gtype4.equals("")) {

                        // isolate guest type
                        loop4:
                        for (x=0; x<=g; x++) {

                            try {
                            if (player4.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                                gtype4 = gtypes[x];
                                grev4 = grevs[x];
                                break;
                            }
                            } catch (IndexOutOfBoundsException ignore) {}
                        }

                    }

                    // if not a member
                    if (user5.equals("") && !player5.equals("") && !gtype5.equals("")) {

                        // isolate guest type
                        loop5:
                        for (x=0; x<=g; x++) {

                            try {
                            if (player5.substring(0, gtypes[x].length()).equalsIgnoreCase(gtypes[x])) {

                                gtype5 = gtypes[x];
                                grev5 = grevs[x];
                                break;
                            }
                            } catch (IndexOutOfBoundsException ignore) {}
                        }

                    }
/*
                    // update teepast2 with the new gtypes & revenue flags
                    pstmt = con2.prepareStatement("" +
                            "UPDATE teepast2 " +
                            "SET " +
                            //  "gtype1 = ?, gtype2 = ?, gtype3 = ?, gtype4 = ?, gtype5 = ?, " +
                                "grev1 = ?, grev2 = ?, grev3 = ?, grev4 = ?, grev5 = ? " +
                            "WHERE teepast_id = ?");
                    pstmt.clearParameters();
                    //pstmt.setString(1, gtype1);
                    //pstmt.setString(2, gtype2);
                    //pstmt.setString(3, gtype3);
                    //pstmt.setString(4, gtype4);
                    //pstmt.setString(5, gtype5);
                    pstmt.setInt(1, grev1);
                    pstmt.setInt(2, grev2);
                    pstmt.setInt(3, grev3);
                    pstmt.setInt(4, grev4);
                    pstmt.setInt(5, grev5);
                    pstmt.setInt(6, tid);
                    pstmt.executeUpdate();
*/

//                } // end teepast2 loop




            //} // end if demov4
/*

            // POPULATE NEW TEEPAST2 FIELDS

            rs2 = stmt2.executeQuery("SELECT username, m_ship, m_type FROM member2b");

            while (rs2.next()) {

                pstmt = con2.prepareStatement("UPDATE teepast2 SET mship1 = ?, mtype1 = ? WHERE username1 = ? AND mship1 = '' AND date > 20090400");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("m_ship"));
                pstmt.setString(2, rs2.getString("m_type"));
                pstmt.setString(3, rs2.getString("username"));
                pstmt.executeUpdate();

                pstmt = con2.prepareStatement("UPDATE teepast2 SET mship2 = ?, mtype2 = ? WHERE username2 = ? AND mship2 = '' AND date > 20090400");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("m_ship"));
                pstmt.setString(2, rs2.getString("m_type"));
                pstmt.setString(3, rs2.getString("username"));
                pstmt.executeUpdate();

                pstmt = con2.prepareStatement("UPDATE teepast2 SET mship3 = ?, mtype3 = ? WHERE username3 = ? AND mship3 = '' AND date > 20090400");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("m_ship"));
                pstmt.setString(2, rs2.getString("m_type"));
                pstmt.setString(3, rs2.getString("username"));
                pstmt.executeUpdate();

                pstmt = con2.prepareStatement("UPDATE teepast2 SET mship4 = ?, mtype4 = ? WHERE username4 = ? AND mship4 = '' AND date > 20090400");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("m_ship"));
                pstmt.setString(2, rs2.getString("m_type"));
                pstmt.setString(3, rs2.getString("username"));
                pstmt.executeUpdate();

                pstmt = con2.prepareStatement("UPDATE teepast2 SET mship5 = ?, mtype5 = ? WHERE username5 = ? AND mship5 = '' AND date > 20090400");
                pstmt.clearParameters();
                pstmt.setString(1, rs2.getString("m_ship"));
                pstmt.setString(2, rs2.getString("m_type"));
                pstmt.setString(3, rs2.getString("username"));
                pstmt.executeUpdate();

            }
*/

            
            

/*
            // DELETE ANY GUESTS FROMS GUEST5 IF THEY ARE NOT IN CLUB5
            String [] gtypes = new String[36];

            // load up all the guest types for this club
            rs2 = stmt2.executeQuery("SELECT * FROM club5");

            if (rs2.next()) {

                gtypes[0] = rs2.getString("guest1").toLowerCase();
                gtypes[1] = rs2.getString("guest2").toLowerCase();
                gtypes[2] = rs2.getString("guest3").toLowerCase();
                gtypes[3] = rs2.getString("guest4").toLowerCase();
                gtypes[4] = rs2.getString("guest5").toLowerCase();
                gtypes[5] = rs2.getString("guest6").toLowerCase();
                gtypes[6] = rs2.getString("guest7").toLowerCase();
                gtypes[7] = rs2.getString("guest8").toLowerCase();
                gtypes[8] = rs2.getString("guest9").toLowerCase();
                gtypes[9] = rs2.getString("guest10").toLowerCase();
                gtypes[10] = rs2.getString("guest11").toLowerCase();
                gtypes[11] = rs2.getString("guest12").toLowerCase();
                gtypes[12] = rs2.getString("guest13").toLowerCase();
                gtypes[13] = rs2.getString("guest14").toLowerCase();
                gtypes[14] = rs2.getString("guest15").toLowerCase();
                gtypes[15] = rs2.getString("guest16").toLowerCase();
                gtypes[16] = rs2.getString("guest17").toLowerCase();
                gtypes[17] = rs2.getString("guest18").toLowerCase();
                gtypes[18] = rs2.getString("guest19").toLowerCase();
                gtypes[19] = rs2.getString("guest20").toLowerCase();
                gtypes[20] = rs2.getString("guest21").toLowerCase();
                gtypes[21] = rs2.getString("guest22").toLowerCase();
                gtypes[22] = rs2.getString("guest23").toLowerCase();
                gtypes[23] = rs2.getString("guest24").toLowerCase();
                gtypes[24] = rs2.getString("guest25").toLowerCase();
                gtypes[25] = rs2.getString("guest26").toLowerCase();
                gtypes[26] = rs2.getString("guest27").toLowerCase();
                gtypes[27] = rs2.getString("guest28").toLowerCase();
                gtypes[28] = rs2.getString("guest29").toLowerCase();
                gtypes[29] = rs2.getString("guest30").toLowerCase();
                gtypes[30] = rs2.getString("guest31").toLowerCase();
                gtypes[31] = rs2.getString("guest32").toLowerCase();
                gtypes[32] = rs2.getString("guest33").toLowerCase();
                gtypes[33] = rs2.getString("guest34").toLowerCase();
                gtypes[34] = rs2.getString("guest35").toLowerCase();
                gtypes[35] = rs2.getString("guest36").toLowerCase();

            }

            // only contine if club is setup and we found at least one guest type
            if (gtypes[0] != null) {

                String tmp_guests = "";

                for (i=0; i<=35; i++) {

                    if (!gtypes[i].equals("")) {

                        tmp_guests = tmp_guests + "\"" + gtypes[i] + "\",";
                    }
                }

                // this check is probably not needed any longer
                if (tmp_guests.length() > 0 ) {

                    tmp_guests = tmp_guests.substring(0, tmp_guests.length()-1); // trim the last comma

                    out.println("<br>tmp_guests="+tmp_guests);

                    stmt2.executeUpdate("DELETE FROM guest5 WHERE guest NOT IN (" + tmp_guests + ")");

                }

            }
*/

            
            // CREATE NEW PROSHOPFB USER AND BLAST EMAIL TO PRO
/*
            Properties properties = new Properties();
            properties.put("mail.smtp.host", "216.243.184.88");                           // set outbound host address
            properties.put("mail.smtp.port", "20025");                           // set port address
            properties.put("mail.smtp.auth", "true");      // set 'use authentication'

            //
            // Create a connection to the mail server
            //
            Session mailSess;

            mailSess = Session.getInstance(properties, getAuthenticator("support@foretees.com", "fikd18"));


            String contact = "";
            String email = "";
            String msgBody = "";

            rs2 = stmt2.executeQuery("SELECT contact, email FROM club5 WHERE clubName <> '';");

            if (rs2.next()) {

                contact = rs2.getString(1);
                email = rs2.getString(2);

                String pw = "";          // init password
                String pwChar = "";

                String [] pw_table = { "q", "w", "e", "6", "9", "2", "3", "5", "7", "a", "s", "d", "f", "g", "h",
                                       "j", "k", "8", "z", "x", "c", "v", "b", "n", "m", "r", "t", "y", "u", "p" };

                //
                //  Build a randomly generated password to use for the admin and proshop users (6 chars long)
                //
                for (int i2=0; i2<6; i2++) {
                    double x = Math.random() * 30;       // get a number between 0 and 29
                    int y = (int) x;                     // create an int
                    pw = pw + pw_table[y];               // append new char to pw
                }

                // add new user
                stmt2.executeUpdate("INSERT INTO login2 VALUES ('proshopfb', '" + pw + "', '', '', '', '', 0, 0, 0, 0, 0, 0, " +
                        "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1)");

                // build email message
                msgBody = "" +
                        "Dear " + contact + ",\n\n" +
                        "We've add a new limited access proshop user called 'proshopfb' for you to provide to the F&B Staff. " +
                        "The password to login to this account is "+pw+". After they login they can change their password by going to Tools > Settings. \n\n" +
                        "By default, this limited access account can only access the Dining System Configuration pages and the Dining Requst pages.\n\n" +
                        "Please contact prosupport@foretees.com if you have any questions.\n\n" +
                        "Thank you for using ForeTees!\n\n" +
                        "Sincerely,\n\n" +
                        "ForeTees Pro Support\n" +
                        "651-765-6006\n" + 
                        "prosupport@foretees.com\n";

                // send email
                try {

                    MimeMessage message = new MimeMessage(mailSess);
                    message.setFrom(new InternetAddress("prosupport@foretees.com"));                  // set from addr
                    message.setSubject( "New 'proshopfb' User Account for ForeTees" );                                       // set subject line
                    message.setSentDate(new java.util.Date());                              // set date/time sent
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

                    Multipart multipart = new MimeMultipart();
                    BodyPart msgBodyPart = new MimeBodyPart();


                    //
                    // ADD PLAIN TEXT BODY
                    //
                    msgBodyPart.setText(msgBody);

                    multipart.addBodyPart(msgBodyPart);

                    message.setContent(multipart);

                    Transport.send(message);

                } catch (Exception exc) {

                    out.println("<br>Error sending new fb user. club="+club+", pass="+pw+", err=" + exc.getMessage());
                }

            }

*/

            /*
            if (!club.equals("demov4") && !club.equals("demobrock")) {

                // drop bad table
                stmt2.executeUpdate("RENAME TABLE demo_mfr TO demo_clubs_mfr;");
                

                // populate demo_club_mfr table with defaults
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('1','None/Unknown');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('2','Other');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('3','Nike');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('4','Callaway');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('5','Cleveland');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('6','Cobra');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('7','Adams');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('8','Mizuno');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('9','Odyssey');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('10','Ping');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('11','Titleist');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('12','TaylorMade');");
                stmt2.executeUpdate("INSERT INTO demo_clubs_mfr (id,mfr) VALUES ('13','Tour Edge');");




                // add new permissions to login2 table
                stmt2.executeUpdate("" +
                    "ALTER TABLE login2 " +
                        "ADD DEMOCLUBS_CHECKIN tinyint(4) NOT NULL default '1', " +
                        "ADD DEMOCLUBS_MANAGE tinyint(4) NOT NULL default '1', " +
                        "ADD DINING_REQUEST tinyint(4) NOT NULL default '1', " +
                        "ADD DINING_CONFIG tinyint(4) NOT NULL default '0'");

            }
*/

/*
            stmt2.executeUpdate("" +
                "ALTER TABLE teepast2 " +
                    "ADD grev1 tinyint(4) NOT NULL DEFAULT '0', " +
                    "ADD grev2 tinyint(4) NOT NULL DEFAULT '0', " +
                    "ADD grev3 tinyint(4) NOT NULL DEFAULT '0', " +
                    "ADD grev4 tinyint(4) NOT NULL DEFAULT '0', " +
                    "ADD grev5 tinyint(4) NOT NULL DEFAULT '0'");
*/

/*
            stmt2.executeUpdate("" +
                "ALTER TABLE teepast2 " +
                    "ADD mship1 varchar(30) NOT NULL, " +
                    "ADD mship2 varchar(30) NOT NULL, " +
                    "ADD mship3 varchar(30) NOT NULL, " +
                    "ADD mship4 varchar(30) NOT NULL, " +
                    "ADD mship5 varchar(30) NOT NULL, " +
                    "ADD mtype1 varchar(30) NOT NULL, " +
                    "ADD mtype2 varchar(30) NOT NULL, " +
                    "ADD mtype3 varchar(30) NOT NULL, " +
                    "ADD mtype4 varchar(30) NOT NULL, " +
                    "ADD mtype5 varchar(30) NOT NULL, " +
                    "ADD gtype1 varchar(20) NOT NULL, " +
                    "ADD gtype2 varchar(20) NOT NULL, " +
                    "ADD gtype3 varchar(20) NOT NULL, " +
                    "ADD gtype4 varchar(20) NOT NULL, " +
                    "ADD gtype5 varchar(20) NOT NULL");



            if (!club.equals("demov4") && !club.equals("demobrock")) {

                // create demo_clubs
                stmt2.executeUpdate("CREATE TABLE demo_clubs (" +
                        "id int(11) NOT NULL auto_increment," +
                        "mfr_id int(11) NOT NULL default '0'," +
                        "name varchar(255) NOT NULL default ''," +
                        "icn varchar(16) default ''," +
                        "type enum('','Driver','Wood','Hybrid','Iron','Wedge','Putter','Specialty','Other','Rental') NOT NULL default ''," +
                        "notes text NOT NULL default ''," +
                        "for_sale tinyint(4) NOT NULL default '0'," +
                        "enabled tinyint(4) NOT NULL default '0'," +
                        "PRIMARY KEY (id)," +
                        "UNIQUE KEY name (name)," +
                        "UNIQUE KEY icn (icn)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


                // create demo_clubs
                stmt2.executeUpdate("CREATE TABLE demo_clubs_mfr (" +
                        "id int(11) NOT NULL auto_increment," +
                        "mfr varchar(32) default NULL," +
                        "PRIMARY KEY (id)," +
                        "UNIQUE KEY mfr (mfr)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


                // create demo_clubs_usage
                stmt2.executeUpdate("CREATE TABLE demo_clubs_usage (" +
                        "id int(11) NOT NULL auto_increment," +
                        "club_id int(11) NOT NULL default '0'," +
                        "username varchar(15) NOT NULL default ''," +
                        "out_by varchar(15) NOT NULL default ''," +
                        "datetime_out datetime NOT NULL default '0000-00-00 00:00:00'," +
                        "in_by varchar(15) NOT NULL default ''," +
                        "datetime_in datetime NOT NULL default '0000-00-00 00:00:00'," +
                        "notes text NOT NULL," +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=latin1");


                // DINING CLUB TABLES

                // create dining_config
                stmt2.executeUpdate("CREATE TABLE dining_config (" +
                        "form_text text NOT NULL," +
                        "link_text varchar(80) NOT NULL default 'Make a Dining Request'," +
                        "prompt_text text NOT NULL," +
                        "pro_main tinyint(4) NOT NULL default '0'," +
                        "pro_teetime tinyint(4) NOT NULL default '0'," +
                        "pro_lesson tinyint(4) NOT NULL default '0'," +
                        "mem_main tinyint(4) NOT NULL default '0'," +
                        "mem_teetime tinyint(4) NOT NULL default '0'," +
                        "mem_lesson tinyint(4) NOT NULL default '0'," +
                        "email_teetime tinyint(4) NOT NULL default '0'," +
                        "email_lesson tinyint(4) NOT NULL default '0'," +
                        "custom_url varchar(50) NOT NULL default ''" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


                // create dining_emails
                stmt2.executeUpdate("CREATE TABLE dining_emails (" +
                        "id tinyint(4) NOT NULL auto_increment," +
                        "address varchar(50) default NULL," +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


                // create dining_messages
                stmt2.executeUpdate("CREATE TABLE dining_messages (" +
                        "id int(11) NOT NULL auto_increment," +
                        "active tinyint(4) default '1'," +
                        "name varchar(30) default NULL," +
                        "sdate date default NULL," +
                        "edate date default NULL," +
                        "form_text text," +
                        "prompt_text text," +
                        "link_text varchar(80) default NULL," +
                        "priority int(11) default '0'," +
                        "eo_week tinyint(4) default '0'," +
                        "sunday tinyint(4) default '0'," +
                        "monday tinyint(4) default '0'," +
                        "tuesday tinyint(4) default '0'," +
                        "wednesday tinyint(4) default '0'," +
                        "thursday tinyint(4) default '0'," +
                        "friday tinyint(4) default '0'," +
                        "saturday tinyint(4) default '0'," +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


                // create dining_rooms
                stmt2.executeUpdate("CREATE TABLE dining_rooms (" +
                        "id int(11) NOT NULL auto_increment," +
                        "name varchar(30) default NULL," +
                        "description text," +
                        "PRIMARY KEY (id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


                // create dining_rooms
                stmt2.executeUpdate("CREATE TABLE dining_stats (" +
                        "id int(11) NOT NULL auto_increment," +
                        "date date default NULL," +
                        "date_time datetime default NULL," +
                        "type enum('','view','submit') default NULL," +
                        "user_group enum('','proshop','member','email') default NULL," +
                        "caller enum('','main','teetime','lesson','email') default NULL," +
                        "custom_id int(11) default NULL," +
                        "count int(11) NOT NULL default '1'," +
                        "PRIMARY KEY (id)," +
                        "UNIQUE KEY date (date,date_time,type,user_group,caller,custom_id)" +
                        ") ENGINE=MyISAM DEFAULT CHARSET=utf8");


                // add demo club days & dining flag to club5
                stmt2.executeUpdate("ALTER TABLE club5 " +
                        "ADD democlub_days smallint(6) NOT NULL default '0'," +
                        "ADD dining tinyint(4) NOT NULL default '0'");

            }
            
*/
/*
                //
                //  Restriction Suspension Table
                //
                stmt2.executeUpdate("CREATE TABLE rest_suspend (" +
                        "id int(11) NOT NULL auto_increment," +
                        "mrest_id int(11) default NULL," +
                        "grest_id int(11) default NULL," +
                        "courseName varchar(30) NOT NULL default ''," +
                        "sdate int(11) NOT NULL default '0'," +
                        "edate int(11) NOT NULL default '0'," +
                        "sunday tinyint(4) NOT NULL default '0'," +
                        "monday tinyint(4) NOT NULL default '0'," +
                        "tuesday tinyint(4) NOT NULL default '0'," +
                        "wednesday tinyint(4) NOT NULL default '0'," +
                        "thursday tinyint(4) NOT NULL default '0'," +
                        "friday tinyint(4) NOT NULL default '0'," +
                        "saturday tinyint(4) NOT NULL default '0'," +
                        "stime smallint(6) NOT NULL default '0'," +
                        "etime smallint(6) NOT NULL default '0'," +
                        "eo_week tinyint(4) NOT NULL default '0'," +
                        "UNIQUE KEY id (id)" +
                        ") ENGINE=MyISAM");
                
                
                //
                //  Add Primary Key 'id' field to restriction2
                //
                stmt2.executeUpdate("CREATE TABLE t1 LIKE restriction2");
                stmt2.executeUpdate("ALTER TABLE t1 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST");
                stmt2.executeUpdate("INSERT INTO t1 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
                        "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, mem1, mem2, mem3, mem4, mem5, mem6, mem7, " +
                        "mem8, mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, mem17, mem18, mem19, mem20, mem21, mem22, " +
                        "mem23, mem24, mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, mship9, mship10, mship11, " +
                        "mship12, mship13, mship14, mship15, mship16, mship17, mship18, mship19, mship20, mship21, mship22, mship23, " +
                        "mship24, color, courseName, fb, showit) (SELECT name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
                        "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, mem1, mem2, mem3, mem4, mem5, mem6, mem7, " +
                        "mem8, mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, mem17, mem18, mem19, mem20, mem21, mem22, " +
                        "mem23, mem24, mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, mship9, mship10, mship11, " +
                        "mship12, mship13, mship14, mship15, mship16, mship17, mship18, mship19, mship20, mship21, mship22, mship23, " +
                        "mship24, color, courseName, fb, showit FROM restriction2 ORDER BY name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, " +
                        "edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, mem1, mem2, mem3, mem4, mem5, mem6, mem7, " +
                        "mem8, mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, mem17, mem18, mem19, mem20, mem21, mem22, " +
                        "mem23, mem24, mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, mship9, mship10, mship11, " +
                        "mship12, mship13, mship14, mship15, mship16, mship17, mship18, mship19, mship20, mship21, mship22, mship23, " +
                        "mship24, color, courseName, fb, showit)");
                stmt2.executeUpdate("DROP TABLE restriction2");
                stmt2.executeUpdate("ALTER TABLE t1 RENAME restriction2");
                
                
                //
                //  Add Primary Key 'id' field to guestres2
                //
                stmt2.executeUpdate("CREATE TABLE t1 LIKE guestres2");
                stmt2.executeUpdate("ALTER TABLE t1 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST");
                stmt2.executeUpdate("INSERT INTO t1 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, edate, " +
                        "end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, num_guests, guest1, guest2, guest3, guest4, guest5, guest6, " +
                        "guest7, guest8, courseName, fb, color, guest9, guest10, guest11, guest12, guest13, guest14, guest15, guest16, guest17, " +
                        "guest18, guest19, guest20, guest21, guest22, guest23, guest24, guest25, guest26, guest27, guest28, guest29, guest30, " +
                        "guest31, guest32, guest33, guest34, guest35, guest36, per) (SELECT name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, edate, " +
                        "end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, num_guests, guest1, guest2, guest3, guest4, guest5, guest6, " +
                        "guest7, guest8, courseName, fb, color, guest9, guest10, guest11, guest12, guest13, guest14, guest15, guest16, guest17, " +
                        "guest18, guest19, guest20, guest21, guest22, guest23, guest24, guest25, guest26, guest27, guest28, guest29, guest30, " +
                        "guest31, guest32, guest33, guest34, guest35, guest36, per FROM guestres2 ORDER BY name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, stime, edate, " +
                        "end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, num_guests, guest1, guest2, guest3, guest4, guest5, guest6, " +
                        "guest7, guest8, courseName, fb, color, guest9, guest10, guest11, guest12, guest13, guest14, guest15, guest16, guest17, " +
                        "guest18, guest19, guest20, guest21, guest22, guest23, guest24, guest25, guest26, guest27, guest28, guest29, guest30, " +
                        "guest31, guest32, guest33, guest34, guest35, guest36, per)");
                stmt2.executeUpdate("DROP TABLE guestres2");
                stmt2.executeUpdate("ALTER TABLE t1 RENAME guestres2");
                
            }
*/



/*
            if (club.equals("demov4")) {


            // add true datetime field in teehist table
            stmt2.executeUpdate("ALTER TABLE teehist ADD mdatetime datetime default '0000-00-00 00:00:00';");


            rs2 = stmt2.executeQuery("SELECT sdate FROM teehist ORDER BY date, time, fb");

            //  Wed Jan 09 16:53:50 CST 2008

            String date = "";
            String smonth = "";
            String day = "";
            String time = "";
            String tz = "";
            String year = "";

            while (rs2.next()) {

                date = rs2.getString(1);

                StringTokenizer date_parts = new StringTokenizer(date);

                smonth = date_parts.nextToken();
                smonth = date_parts.nextToken();
                day = date_parts.nextToken();
                time = date_parts.nextToken();
                tz = date_parts.nextToken();
                year = date_parts.nextToken();

                if (smonth.equals("Jan")) {
                    smonth = "01";
                } else if (smonth.equals("Feb")) {
                    smonth = "02";
                } else if (smonth.equals("Mar")) {
                    smonth = "03";
                } else if (smonth.equals("Apr")) {
                    smonth = "04";
                } else if (smonth.equals("May")) {
                    smonth = "05";
                } else if (smonth.equals("Jun")) {
                    smonth = "06";
                } else if (smonth.equals("Jul")) {
                    smonth = "07";
                } else if (smonth.equals("Aug")) {
                    smonth = "08";
                } else if (smonth.equals("Sep")) {
                    smonth = "09";
                } else if (smonth.equals("Oct")) {
                    smonth = "10";
                } else if (smonth.equals("Nov")) {
                    smonth = "11";
                } else if (smonth.equals("Dec")) {
                    smonth = "12";
                }

                String new_datetime = year + "-" + smonth + "-" + day + " " + time;

                try {

                    // fix any event gender issues
                    stmt3.executeUpdate("UPDATE teehist SET mdatetime = \"" + new_datetime + "\" WHERE sdate = \"" + date + "\";");
                    //stmt3.close();

                } catch (Exception exp) {
                    out.println("<br>Error updating teehist for " + club + ". (" + date + ") Exception=" + exp.toString());
                }

            }

            }
*/
/*
            stmt2.executeUpdate("ALTER TABLE events2b " +
                "ADD member_item_code varchar(30) NOT NULL, " +
                "ADD guest_item_code varchar(30) NOT NULL, " + 
                "ADD ask_homeclub tinyint(1) NOT NULL default '0'," + 
                "ADD ask_phone tinyint(1) NOT NULL default '0'," + 
                "ADD ask_address tinyint(1) NOT NULL default '0', " + 
                "ADD ask_hdcp tinyint(1) NOT NULL default '0', " + 
                "ADD ask_email tinyint(1) NOT NULL default '0', " + 
                "ADD ask_gender tinyint(1) NOT NULL default '0', " + 
                "ADD ask_shirtsize tinyint(1) NOT NULL default '0', " + 
                "ADD ask_shoesize tinyint(1) NOT NULL default '0', " + 
                "ADD ask_otherA1 tinyint(1) NOT NULL default '0', " + 
                "ADD ask_otherA2 tinyint(1) NOT NULL default '0', " + 
                "ADD ask_otherA3 tinyint(1) NOT NULL default '0', " + 
                "ADD req_guestname tinyint(1) NOT NULL default '0', " + 
                "ADD req_homeclub tinyint(1) NOT NULL default '0', " + 
                "ADD req_phone tinyint(1) NOT NULL default '0', " + 
                "ADD req_address tinyint(1) NOT NULL default '0', " + 
                "ADD req_hdcp tinyint(1) NOT NULL default '0', " + 
                "ADD req_email tinyint(1) NOT NULL default '0', " + 
                "ADD req_gender tinyint(1) NOT NULL default '0', " + 
                "ADD req_shirtsize tinyint(1) NOT NULL default '0', " + 
                "ADD req_shoesize tinyint(1) NOT NULL default '0', " + 
                "ADD req_otherA1 tinyint(1) NOT NULL default '0', " + 
                "ADD req_otherA2 tinyint(1) NOT NULL default '0', " + 
                "ADD req_otherA3 tinyint(1) NOT NULL default '0', " + 
                "ADD otherQ1 varchar(32) NOT NULL default '', " + 
                "ADD otherQ2 varchar(32) NOT NULL default '', " + 
                "ADD otherQ3 varchar(32) NOT NULL default ''");
 */

 /*
        // not yet
        ADD who_shirtsize tinyint(1) NOT NULL default '0',
        ADD who_shoesize tinyint(1) NOT NULL default '0',
        ADD who_otherQ1 tinyint(1) NOT NULL default '0',
        ADD who_otherQ2 tinyint(1) NOT NULL default '0',
        ADD who_otherQ3 tinyint(1) NOT NULL default '0'
 */

 /*
            stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                "CHANGE notes notes text NOT NULL, " + 
                "ADD homeclub1 varchar(24) NOT NULL default '', " + 
                "ADD homeclub2 varchar(24) NOT NULL default '', " + 
                "ADD homeclub3 varchar(24) NOT NULL default '', " +  
                "ADD homeclub4 varchar(24) NOT NULL default '', " +  
                "ADD homeclub5 varchar(24) NOT NULL default '', " +  
                "ADD phone1 varchar(20) NOT NULL default '', " +  
                "ADD phone2 varchar(20) NOT NULL default '', " +  
                "ADD phone3 varchar(20) NOT NULL default '', " +  
                "ADD phone4 varchar(20) NOT NULL default '', " +  
                "ADD phone5 varchar(20) NOT NULL default '', " +  
                "ADD address1 varchar(32) NOT NULL default '', " +  
                "ADD address2 varchar(32) NOT NULL default '', " +  
                "ADD address3 varchar(32) NOT NULL default '', " +  
                "ADD address4 varchar(32) NOT NULL default '', " +  
                "ADD address5 varchar(32) NOT NULL default '', " +  
                "ADD email1 varchar(50) NOT NULL default '', " +  
                "ADD email2 varchar(50) NOT NULL default '', " +  
                "ADD email3 varchar(50) NOT NULL default '', " +  
                "ADD email4 varchar(50) NOT NULL default '', " +  
                "ADD email5 varchar(50) NOT NULL default '', " +  
                "ADD shirtsize1 varchar(8) NOT NULL default '', " +  
                "ADD shirtsize2 varchar(8) NOT NULL default '', " + 
                "ADD shirtsize3 varchar(8) NOT NULL default '', " + 
                "ADD shirtsize4 varchar(8) NOT NULL default '', " + 
                "ADD shirtsize5 varchar(8) NOT NULL default '', " + 
                "ADD shoesize1 varchar(8) NOT NULL default '', " +  
                "ADD shoesize2 varchar(8) NOT NULL default '', " +  
                "ADD shoesize3 varchar(8) NOT NULL default '', " +  
                "ADD shoesize4 varchar(8) NOT NULL default '', " +  
                "ADD shoesize5 varchar(8) NOT NULL default '', " +  
                "ADD other1A1 varchar(24) NOT NULL default '', " + 
                "ADD other1A2 varchar(24) NOT NULL default '', " + 
                "ADD other1A3 varchar(24) NOT NULL default '', " + 
                "ADD other2A1 varchar(24) NOT NULL default '', " + 
                "ADD other2A2 varchar(24) NOT NULL default '', " + 
                "ADD other2A3 varchar(24) NOT NULL default '', " + 
                "ADD other3A1 varchar(24) NOT NULL default '', " + 
                "ADD other3A2 varchar(24) NOT NULL default '', " + 
                "ADD other3A3 varchar(24) NOT NULL default '', " + 
                "ADD other4A1 varchar(24) NOT NULL default '', " + 
                "ADD other4A2 varchar(24) NOT NULL default '', " + 
                "ADD other4A3 varchar(24) NOT NULL default '', " + 
                "ADD other5A1 varchar(24) NOT NULL default '', " + 
                "ADD other5A2 varchar(24) NOT NULL default '', " + 
                "ADD other5A3 varchar(24) NOT NULL default ''");
        
            }
*/
            
/*
            stmt2.executeUpdate("ALTER TABLE wait_list CHANGE auto_assign member_view_teesheet tinyint(4) NOT NULL DEFAULT '0'");
            
            stmt2.executeUpdate("ALTER TABLE club5 ADD lottery_text varchar(30) NOT NULL"); 
            
            stmt2.executeUpdate("ALTER TABLE lreqs3 ADD courseReq varchar(30) NOT NULL");
            
            
            out.println("<br>Added columns to mem_notice.");
            
            stmt2.executeUpdate("ALTER TABLE mem_notice ADD teesheet tinyint NOT NULL DEFAULT '0'");
            stmt2.executeUpdate("ALTER TABLE mem_notice ADD bgColor varchar(24) NOT NULL");
            
            
            out.println("<br>Droping columns evntsup2b. ");
            
            // drop the 25 unused fields from evntsup2b
            stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                                    "DROP COLUMN player6, " +
                                    "DROP COLUMN player7, " +
                                    "DROP COLUMN player8, " +
                                    "DROP COLUMN player9, " +
                                    "DROP COLUMN player10, " +
                                    "DROP COLUMN username6, " +
                                    "DROP COLUMN username7, " +
                                    "DROP COLUMN username8, " +
                                    "DROP COLUMN username9, " +
                                    "DROP COLUMN username10, " +
                                    "DROP COLUMN p6cw, " +
                                    "DROP COLUMN p7cw, " +
                                    "DROP COLUMN p8cw, " +
                                    "DROP COLUMN p9cw, " +
                                    "DROP COLUMN p10cw, " +
                                    "DROP COLUMN hndcp6, " +
                                    "DROP COLUMN hndcp7, " +
                                    "DROP COLUMN hndcp8, " +
                                    "DROP COLUMN hndcp9, " +
                                    "DROP COLUMN hndcp10, " +
                                    "DROP COLUMN userg6, " +
                                    "DROP COLUMN userg7, " +
                                    "DROP COLUMN userg8, " +
                                    "DROP COLUMN userg9, " +
                                    "DROP COLUMN userg10;");
            
            out.println("Rebuilding evntsup2b.");  
            out.flush();
            
            stmt2.executeUpdate("CREATE TABLE t1 LIKE evntsup2b");
            stmt2.executeUpdate("ALTER TABLE t1 DROP COLUMN id");
            stmt2.executeUpdate("ALTER TABLE t1 ADD id INT AUTO_INCREMENT PRIMARY KEY FIRST");
            stmt2.executeUpdate("INSERT INTO t1 (name, courseName, player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, in_use, in_use_by, hndcp1, hndcp2, hndcp3, hndcp4, hndcp5, notes, hideNotes, c_date, c_time, r_date, r_time, wait, userg1, userg2, userg3, userg4, userg5, hole, moved, gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5) " +
                                    "(SELECT name, courseName, player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, in_use, in_use_by, hndcp1, hndcp2, hndcp3, hndcp4, hndcp5, notes, hideNotes, c_date, c_time, r_date, r_time, wait, userg1, userg2, userg3, userg4, userg5, hole, moved, gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5 " +
                                        "FROM evntsup2b ORDER BY name, courseName, player1, player2, player3, player4, player5, username1, username2, username3, username4, username5, p1cw, p2cw, p3cw, p4cw, p5cw, in_use, in_use_by, hndcp1, hndcp2, hndcp3, hndcp4, hndcp5, notes, hideNotes, c_date, c_time, r_date, r_time, wait, userg1, userg2, userg3, userg4, userg5, hole, moved, gender1, gender2, gender3, gender4, gender5, ghin1, ghin2, ghin3, ghin4, ghin5)");
            stmt2.executeUpdate("DROP TABLE evntsup2b");
            stmt2.executeUpdate("ALTER TABLE t1 RENAME evntsup2b");
            
        }
            
            // add permissions fields to login2
            stmt2.executeUpdate("ALTER TABLE login2 " +
                                    "ADD SYSCONFIG_CLUBCONFIG tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_TEESHEETS tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_EVENT tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_LOTTERY tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_WAITLIST tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_RESTRICTIONS tinyint(4) NOT NULL default '1', " +
                                    "ADD SYSCONFIG_MEMBERNOTICES tinyint(4) NOT NULL default '1'");
 
 
            // add backup_emails table
            stmt2.executeUpdate("CREATE TABLE backup_emails ( " +
                                 "address varchar(50) NOT NULL default '' " +
                                ") ENGINE=MyISAM");
*/
            
/* 
            // add permissions fields to login2
            stmt2.executeUpdate("ALTER TABLE login2 " +
                                    "ADD name_first varchar(20) NOT NULL default '', " + 
                                    "ADD name_last varchar(20) NOT NULL default '', " + 
                                    "ADD name_mi char(1) NOT NULL default '', " + 
                                    "ADD inact tinyint(4) NOT NULL default '0', " + 
                                    "ADD start_time int(11) NOT NULL default '0', " + 
                                    "ADD end_time int(11) NOT NULL default '0', " + 
                                    "ADD display_bag tinyint(4) NOT NULL default '0', " + 
                                    "ADD display_mnum tinyint(4) NOT NULL default '0', " + 
                                    "ADD display_hdcp tinyint(4) NOT NULL default '0', " + 
                                    "ADD SYS_CONFIG tinyint(4) NOT NULL default '1', " + 
                                    "ADD TOOLS_ANNOUNCE tinyint(4) NOT NULL default '1', " + 
                                    "ADD TOOLS_EMAIL tinyint(4) NOT NULL default '1', " + 
                                    "ADD TOOLS_SEARCHTS tinyint(4) NOT NULL default '1', " + 
                                    "ADD TOOLS_HDCP tinyint(4) NOT NULL default '1', " + 
                                    "ADD REPORTS tinyint(4) NOT NULL default '1', " + 
                                    "ADD LOTT_UPDATE tinyint(4) NOT NULL default '1', " + 
                                    "ADD LOTT_APPROVE tinyint(4) NOT NULL default '1', " + 
                                    "ADD LESS_CONFIG tinyint(4) NOT NULL default '1', " + 
                                    "ADD LESS_VIEW tinyint(4) NOT NULL default '1', " + 
                                    "ADD LESS_UPDATE tinyint(4) NOT NULL default '1', " + 
                                    "ADD EVNTSUP_UPDATE tinyint(4) NOT NULL default '1', " + 
                                    "ADD EVNTSUP_VIEW tinyint(4) NOT NULL default '1', " + 
                                    "ADD EVNTSUP_MANAGE tinyint(4) NOT NULL default '1', " + 
                                    "ADD WAITLIST_UPDATE tinyint(4) NOT NULL default '1', " + 
                                    "ADD WAITLIST_VIEW tinyint(4) NOT NULL default '1', " + 
                                    "ADD WAITLIST_MANAGE tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_VIEW tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_UPDATE tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_CHECKIN tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_PRINT tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_POS tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_CTRL_FROST tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_CTRL_TSEDIT tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_CTRL_EMAIL tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_PACE_VIEW tinyint(4) NOT NULL default '1', " + 
                                    "ADD TS_PACE_UPDATE tinyint(4) NOT NULL default '1'");
                
                
       
            // WAIT LIST TABLES 
            stmt2.executeUpdate("CREATE TABLE wait_list (" + 
                                    "wait_list_id int(11) NOT NULL auto_increment, " +
                                    "name varchar(32) NOT NULL default '', " + 
                                    "sdatetime datetime default '0000-00-00 00:00:00', " + 
                                    "edatetime datetime default '0000-00-00 00:00:00', " + 
                                    "cutoff_days tinyint(4) NOT NULL default '0', " + 
                                    "cutoff_time smallint(6) NOT NULL default '0', " + 
                                    "sunday tinyint(4) NOT NULL default '0', " + 
                                    "monday tinyint(4) NOT NULL default '0', " + 
                                    "tuesday tinyint(4) NOT NULL default '0', " + 
                                    "wednesday tinyint(4) NOT NULL default '0', " + 
                                    "thursday tinyint(4) NOT NULL default '0', " + 
                                    "friday tinyint(4) NOT NULL default '0', " + 
                                    "saturday tinyint(4) NOT NULL default '0', " + 
                                    "course varchar(32) NOT NULL default '0', " + 
                                    "auto_assign tinyint(4) NOT NULL default '0', " + 
                                    "member_access tinyint(4) NOT NULL default '0', " + 
                                    "member_view tinyint(4) NOT NULL default '0', " + 
                                    "max_team_size tinyint(4) NOT NULL default '0', " + 
                                    "max_list_size tinyint(4) NOT NULL default '0', " + 
                                    "allow_guests tinyint(4) NOT NULL default '0', " + 
                                    "allow_x tinyint(4) NOT NULL default '0', " + 
                                    "enabled tinyint(4) NOT NULL default '0', " + 
                                    "notice text NOT NULL, " + 
                                    "color varchar(24) NOT NULL default '', " + 
                                    "PRIMARY KEY  (wait_list_id), " + 
                                    "UNIQUE KEY name (name), " + 
                                    "KEY course_id (course) " + 
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
            

            stmt2.executeUpdate("CREATE TABLE wait_list_signups ( " + 
                                    "wait_list_signup_id int(11) NOT NULL auto_increment, " + 
                                    "wait_list_id int(11) default NULL, " + 
                                    "created_by varchar(32) NOT NULL default '', " + 
                                    "created_datetime datetime NOT NULL default '0000-00-00 00:00:00', " + 
                                    "date date NOT NULL default '0000-00-00', " + 
                                    "ok_stime smallint(6) NOT NULL default '0', " + 
                                    "ok_etime smallint(6) NOT NULL default '0', " + 
                                    "in_use_by varchar(15) NOT NULL default '', " + 
                                    "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                                    "hideNotes tinyint(4) NOT NULL default '0', " + 
                                    "notes text, " + 
                                    "converted tinyint(4) NOT NULL default '0', " + 
                                    "converted_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                                    "converted_by varchar(15) NOT NULL default '', " + 
                                    "teecurr_id int(11) NOT NULL default '0', " + 
                                    "teepast_id int(11) NOT NULL default '0', " + 
                                    "PRIMARY KEY  (wait_list_signup_id) " + 
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
        
        
            stmt2.executeUpdate("CREATE TABLE wait_list_signups_players ( " + 
                                    "wait_list_signup_player_id int(11) NOT NULL auto_increment, " + 
                                    "wait_list_signup_id int(11) NOT NULL default '0', " + 
                                    "username varchar(12) NOT NULL default '', " + 
                                    "cw varchar(4) NOT NULL default '', " + 
                                    "player_name varchar(64) NOT NULL default '', " + 
                                    "9hole tinyint(4) NOT NULL default '0', " + 
                                    "pos tinyint(4) NOT NULL default '0', " + 
                                    "PRIMARY KEY  (wait_list_signup_player_id), " + 
                                    "UNIQUE KEY pos (wait_list_signup_id,pos) " + 
                                ") ENGINE=MyISAM DEFAULT CHARSET=latin1");
*/
            
            /*
            // find clubs with gender issues in events2b
            rs2 = stmt2.executeQuery("select count(*) from events2b where gender = -1;");
            
            if (rs2.next()) {
                
                // if no dups found
                if (rs2.getInt(1) > 0) {
                    
                    try {

                        // fix any event gender issues
                        stmt3.executeUpdate("UPDATE events2b SET gender = 0 WHERE gender = -1;");
                        stmt3.close();
                        
                    } catch (Exception exp) {
                        out.println("<br>Error updating events2b for " + club + ".  Exception=" + exp.toString());
                    }
                    
                    out.println("<br>Fixed " + rs2.getInt(1) + " events for " + club + ".");
                    
                } // end if == 0
            
            } // end if rs2
            */
            
            /*
            if (!club.equals("demov4") && !club.equals("noaks")) {
                stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                                     "ADD gender1 ENUM('','M','F') NOT NULL default '', " +
                                     "ADD gender2 ENUM('','M','F') NOT NULL default '', " +
                                     "ADD gender3 ENUM('','M','F') NOT NULL default '', " +
                                     "ADD gender4 ENUM('','M','F') NOT NULL default '', " +
                                     "ADD gender5 ENUM('','M','F') NOT NULL default '', " +
                                     "ADD ghin1 varchar(16) NOT NULL default '', " +
                                     "ADD ghin2 varchar(16) NOT NULL default '', " +
                                     "ADD ghin3 varchar(16) NOT NULL default '', " +
                                     "ADD ghin4 varchar(16) NOT NULL default '', " +
                                     "ADD ghin5 varchar(16) NOT NULL default '';");
            }
            */
            
            /*
            try {
                stmt2.executeUpdate("ALTER TABLE teecurr2 ADD UNIQUE NoDup (date, time, fb, courseName)");
            } catch (Exception e2) {
                out.println(" ERROR - CAN NOT MAKE UNIQUE KEY");
            } finally {
                out.print(" OK");
            }
            */
            
            //stmt2.executeUpdate("ALTER TABLE events2b CHANGE gender gender tinyint NOT NULL default 0");
            
            /*
            
            stmt2.executeUpdate("ALTER TABLE club5 " +
                                 "ADD smtp_addr varchar(50) NOT NULL default '', " +
                                 "ADD smtp_port smallint NOT NULL default 25, " +
                                 "ADD smtp_auth tinyint NOT NULL default 0, " +
                                 "ADD smtp_user varchar(50) NOT NULL default '', " +
                                 "ADD smtp_pass varchar(32) NOT NULL default '', " +
                                 "ADD email_from varchar(50) NOT NULL default '', " +
                                 "ADD email_from_pro varchar(50) NOT NULL default '', " +
                                 "ADD email_from_mem varchar(50) NOT NULL default '';");
            
            stmt2.executeUpdate("ALTER TABLE member2b ADD tflag varchar(4) NOT NULL default '';");
            
            stmt2.executeUpdate("ALTER TABLE mship5 ADD tflag varchar(4) NOT NULL default '';");
            
            stmt2.executeUpdate("ALTER TABLE teecurr2 " +
                                 "ADD tflag1 varchar(9) NOT NULL default '', " +
                                 "ADD tflag2 varchar(9) NOT NULL default '', " +
                                 "ADD tflag3 varchar(9) NOT NULL default '', " +
                                 "ADD tflag4 varchar(9) NOT NULL default '', " +
                                 "ADD tflag5 varchar(9) NOT NULL default '';");
            
            stmt2.executeUpdate("ALTER TABLE guest5 ADD revenue tinyint NOT NULL default 0;");
            
            stmt2.executeUpdate("ALTER TABLE teepast2 " + 
                                 "ADD pos1 tinyint NOT NULL default 0, " + 
                                 "ADD pos2 tinyint NOT NULL default 0, " + 
                                 "ADD pos3 tinyint NOT NULL default 0, " + 
                                 "ADD pos4 tinyint NOT NULL default 0, " + 
                                 "ADD pos5 tinyint NOT NULL default 0");
            
            stmt2.executeUpdate("ALTER TABLE events2b " +
                                 "ADD gender varchar(6) NOT NULL default '', " +
                                 "ADD email1 varchar(50) NOT NULL default '', " +
                                 "ADD email2 varchar(50) NOT NULL default '', " +
                                 "ADD season tinyint NOT NULL default 0, " +
                                 "ADD export_type tinyint NOT NULL default 0;");
            
            stmt2.executeUpdate("ALTER TABLE lottery3 " +
                                 "ADD minsbefore smallint NOT NULL default 120, " +
                                 "ADD minsafter smallint NOT NULL default 120, " +
                                 "ADD allowmins tinyint NOT NULL default 0;");
            
            */
            
            // add alternating week field to custom_sheets table
            //stmt2.executeUpdate("ALTER TABLE custom_sheets ADD eo_week tinyint NOT NULL default 0;");
            
            
            /*
            
            // add cutoffDays & cutoffTime to club5 table
            stmt2.executeUpdate("ALTER TABLE club5 ADD cutoffDays tinyint NOT NULL default 99;");
            stmt2.executeUpdate("ALTER TABLE club5 ADD cutoffTime smallint NOT NULL default 0;");
            
            
            stmt2.executeUpdate("CREATE TABLE custom_sheets (" + 
                                "custom_sheet_id int(11) NOT NULL auto_increment," + 
                                "name varchar(32) NOT NULL default ''," + 
                                "course varchar(30) NOT NULL default ''," + 
                                "start_date int(11) NOT NULL default '0'," + 
                                "end_date int(11) NOT NULL default '0'," + 
                                "sunday tinyint(4) NOT NULL default '0'," + 
                                "monday tinyint(4) NOT NULL default '0'," + 
                                "tuesday tinyint(4) NOT NULL default '0'," + 
                                "wednesday tinyint(4) NOT NULL default '0'," + 
                                "thursday tinyint(4) NOT NULL default '0'," + 
                                "friday tinyint(4) NOT NULL default '0'," + 
                                "saturday tinyint(4) NOT NULL default '0'," + 
                                "stime smallint(6) NOT NULL default '0'," + 
                                "etime smallint(6) NOT NULL default '0'," + 
                                "alt tinyint(4) NOT NULL default '0'," + 
                                "betwn tinyint(4) NOT NULL default '0'," + 
                                "PRIMARY KEY  (custom_sheet_id)," + 
                                "UNIQUE KEY name (name)" + 
                                ") ENGINE=MyISAM");
            
            
            stmt2.executeUpdate("CREATE TABLE custom_tee_times (" + 
                                "custom_tee_time_id int(11) NOT NULL auto_increment," + 
                                "custom_sheet_id int(11) NOT NULL default '0'," + 
                                "time int(11) NOT NULL default '0'," + 
                                "fb smallint(6) NOT NULL default '0'," + 
                                "PRIMARY KEY  (custom_tee_time_id)," + 
                                "UNIQUE KEY ind1 (custom_sheet_id,time,fb)" + 
                                ") ENGINE=MyISAM");
            */
            
            
            // add fb field to teereport4 table
            //stmt2.executeUpdate("ALTER TABLE teereport4 ADD fb CHAR(1);");
            
            
            // change score_postings 'score' field to a smallint
            //stmt2.executeUpdate("ALTER TABLE score_postings CHANGE score score smallint NOT NULL default 0;");
            
            // add max_originations to club5 table
            //stmt2.executeUpdate("ALTER TABLE club5 ADD max_originations tinyint NOT NULL default 0;");
            
            // change score_postings 'type' field length from 1 to 2
            //stmt2.executeUpdate("ALTER TABLE score_postings CHANGE type type VARCHAR(2) NOT NULL default '';");
            
            //if (!club.equals("demov4")) stmt2.executeUpdate("ALTER TABLE events2b ADD minsize tinyint NOT NULL default 0;");
    
            /*
            if (club.equals("santaana")) skip = false;
            
            if (!skip) {
            
                stmt2.executeUpdate("ALTER TABLE club5 ADD emailPass varchar(16) NOT NULL default '';");
                stmt2.executeUpdate("ALTER TABLE member2b ADD gender enum('','M','F') NOT NULL, ADD pri_indicator tinyint NOT NULL default '0';");
            }
            */
            //stmt2.executeUpdate("ALTER TABLE mem_notice ADD proside tinyint NOT NULL default 0;");

/*            
            // redifine the double fields as double(n,n) fields
            stmt2.executeUpdate("ALTER TABLE member2b MODIFY g_hancap double(3,1);");
            
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp1 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp2 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp3 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp4 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp5 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp6 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp7 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp8 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp9 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE evntsup2b MODIFY hndcp10 double(3,1);");
            
            stmt2.executeUpdate("ALTER TABLE teecurr2 MODIFY hndcp1 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE teecurr2 MODIFY hndcp2 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE teecurr2 MODIFY hndcp3 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE teecurr2 MODIFY hndcp4 double(3,1);");
            stmt2.executeUpdate("ALTER TABLE teecurr2 MODIFY hndcp5 double(3,1);");
*/            
            
            // add moved flag to event signups table
            //stmt2.executeUpdate("ALTER TABLE evntsup2b ADD moved tinyint NOT NULL default 0;");
            
/*
            // add index to lreqs3 table
            stmt2.executeUpdate("CREATE INDEX name ON lreqs3 (name);");
            stmt2.executeUpdate("CREATE INDEX date ON lreqs3 (date);");
            stmt2.executeUpdate("CREATE INDEX in_use ON lreqs3 (in_use);");
            
            // and index to lottery3 table
            stmt2.executeUpdate("CREATE UNIQUE INDEX name ON lottery3 (name);");
            stmt2.executeUpdate("CREATE INDEX dates ON lottery3 (sdate,edate);");
            
            // add index to clubparm2 table
            stmt2.executeUpdate("CREATE UNIQUE INDEX courseName ON clubparm2 (courseName);");
            
            // and index to restriction2 table
            stmt2.executeUpdate("DROP INDEX ind1 ON restriction2;");
            stmt2.executeUpdate("DROP INDEX ind2 ON restriction2;");
            stmt2.executeUpdate("DROP INDEX ind3 ON restriction2;");
            stmt2.executeUpdate("CREATE UNIQUE INDEX name ON restriction2 (name);");
            stmt2.executeUpdate("CREATE INDEX dates ON restriction2 (sdate,edate);");
            stmt2.executeUpdate("CREATE INDEX courseName ON restriction2 (courseName(10));");
            
            // add index to lessonbook5 table
            stmt2.executeUpdate("CREATE UNIQUE INDEX recid ON lessonbook5 (recid);");
            stmt2.executeUpdate("DROP INDEX ind4 ON lessonbook5;");
            stmt2.executeUpdate("DROP INDEX ind3 ON lessonbook5;");
            stmt2.executeUpdate("DROP INDEX ind2 ON lessonbook5;");
            stmt2.executeUpdate("DROP INDEX ind1 ON lessonbook5;");
            stmt2.executeUpdate("CREATE INDEX date ON lessonbook5 (date);");
            stmt2.executeUpdate("CREATE INDEX in_use ON lessonbook5 (in_use);");
*/            
            
            //stmt2.executeUpdate("CREATE UNIQUE INDEX mship ON mship5 (mship);");
            //stmt2.executeUpdate("CREATE UNIQUE INDEX guest ON guest5 (guest);");
            
            /*
            // Add fields for new seamless login interface 
            stmt2.executeUpdate("ALTER TABLE club5 " +
                               "ADD rsync tinyint NOT NULL default '0', ADD seamless tinyint NOT NULL default '0', " +
                               "ADD zipcode varchar(5) NOT NULL, ADD primaryif tinyint NOT NULL default '0', " +
                               "ADD mnum tinyint NOT NULL default '0', ADD mapping tinyint NOT NULL default '0', " +
                               "ADD stripzero tinyint NOT NULL default '0'");
             */
            
            
            // increase size of score field from tinyint to smallint
            //stmt2.executeUpdate("ALTER TABLE score_postings MODIFY score smallint;");
            
            /*
             *  make all member2b tables unique if possible
            rs2 = stmt2.executeQuery("select count(*) from (select count(*) as c from (select username from member2b) as t group by username) as t2 where c > 1;");
            
            if (rs2.next()) {
                
                // if no dups found
                if (rs2.getInt(1) == 0) {
                    
                    try {

                        stmt3.executeUpdate("ALTER TABLE member2b DROP INDEX ind1;");
                        stmt3.executeUpdate("ALTER TABLE member2b ADD UNIQUE ind1 (username);");
                        stmt3.close();
                        
                    } catch (Exception exp) {
                        out.println("<br>Error updating index for " + club + ".  Exception=" + exp.toString());
                    }
                    
                } // end if == 0
            
            } // end if rs2
            
            */
            
            //stmt2.executeUpdate("ALTER TABLE score_postings ADD tee_id int NOT NULL default 0");
            
            
            
            //stmt2.executeUpdate("ALTER TABLE mship5 ADD viewdays smallint NOT NULL default 30");
            
            
            /*
            // fix '0' emails addresses
            stmt2.executeUpdate("UPDATE member2b SET email = '' WHERE email = '0';");
            stmt2.executeUpdate("UPDATE member2b SET email2 = '' WHERE email2 = '0';");
            // if email is empty but email2 isn't, then move in into email instead
            stmt2.executeUpdate("UPDATE member2b SET email = email2 WHERE email = '' AND email2 <> '';");
            // if emai1 and email2 are the same, then clear email2 field
            stmt2.executeUpdate("UPDATE member2b SET email2 = '' WHERE email = email2;");
            */
            
            //stmt2.executeUpdate("ALTER TABLE teepast2 ADD custom_string varchar(20) NOT NULL default '';");
            //stmt2.executeUpdate("ALTER TABLE teepast2 ADD custom_int int NOT NULL default 0;");
            
            
            
            //stmt2.executeUpdate("ALTER TABLE teecurr2 ADD lottery_email int NOT NULL default 0;");
            
            
            
            /*
            // increase mname field in teehist table
            stmt2.executeUpdate("ALTER TABLE teehist CHANGE mname mname varchar(50);");


            // teecurr2 changes
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_disp1 varchar(10) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_disp2 varchar(10) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_disp3 varchar(10) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_disp4 varchar(10) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_disp5 varchar(10) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_string varchar(20) NOT NULL default '';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD custom_int int NOT NULL default 0;");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD create_date datetime NOT NULL default '0000-00-00 00:00:00';");
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD last_mod_date datetime NOT NULL default '0000-00-00 00:00:00';");


            // teepast2 changes
            stmt2.executeUpdate("ALTER TABLE teepast2 ADD create_date datetime NOT NULL default '0000-00-00 00:00:00';");
            stmt2.executeUpdate("ALTER TABLE teepast2 ADD last_mod_date datetime NOT NULL default '0000-00-00 00:00:00';");
            
            
            // add new member2b fields
            //stmt2.executeUpdate("ALTER TABLE member2b ADD inact tinyint NOT NULL default '0'");
            //stmt2.executeUpdate("ALTER TABLE member2b ADD billable tinyint NOT NULL default '1'");
            //stmt2.executeUpdate("ALTER TABLE member2b ADD last_sync_date date NOT NULL default '0000-00-00'");
            
            
            // add unique index to lreqs3 table
            //stmt2.executeUpdate("CREATE UNIQUE INDEX id ON lreqs3 (id);");
*/
            
/*            
            // change assoc_num & club_num to varchar from int
            stmt2.executeUpdate("ALTER TABLE hdcp_assoc_num CHANGE assoc_num assoc_num varchar(8);");
            stmt2.executeUpdate("ALTER TABLE hdcp_club_num CHANGE club_num club_num varchar(8);");
            
            // add unique index to score_postings table
            stmt2.executeUpdate("CREATE UNIQUE INDEX entry ON score_postings (hdcpNum,date,score,type);");
*/            
            
            // change evntsup2 itinerary field from varchar to text
            //stmt2.executeUpdate("ALTER TABLE events2b CHANGE itin itin TEXT;");
            
            // drop hdcp fields from club5
            //stmt2.executeUpdate("ALTER TABLE club5 DROP COLUMN clubNum, DROP COLUMN clubAssocNum;");
            
/*
            stmt2.executeUpdate("ALTER TABLE member2b ADD hdcp_club_num_id int NOT NULL default '0'");
            stmt2.executeUpdate("ALTER TABLE member2b ADD hdcp_assoc_num_id int NOT NULL default '0'");
            stmt2.executeUpdate("ALTER TABLE member2b ADD default_tee_id int NOT NULL default '0'");
            stmt2.executeUpdate("ALTER TABLE member2b ADD default_holes tinyint NOT NULL default '0'");
            stmt2.executeUpdate("ALTER TABLE member2b ADD displayHdcp tinyint NOT NULL default '0'");
            
            
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS hdcp_assoc_num ( " + 
                  "hdcp_assoc_num_id int(11) NOT NULL auto_increment, " +
                  "assoc_num int(11) NOT NULL, " +
                  "assoc_name varchar(16) NOT NULL, " +
                  "PRIMARY KEY (hdcp_assoc_num_id) " +
                ") ENGINE=MyISAM;");
            
            
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS hdcp_club_num ( " +
                 "hdcp_club_num_id int(11) NOT NULL auto_increment, " +
                 "club_num int(11) NOT NULL, " +
                 "club_name varchar(16) NOT NULL, " +
                 "PRIMARY KEY (hdcp_club_num_id) " + 
               ") ENGINE=MyISAM;");
/*                    
                    
            
            // BOB ONLY!!!
            //stmt2.executeUpdate("ALTER TABLE member2b ADD email_bounced tinyint(4) NOT NULL default '0'");
            //stmt2.executeUpdate("ALTER TABLE member2b ADD email2_bounced tinyint(4) NOT NULL default '0'");
                       
            
            
            //out.println("<br>Updating club5"); 
            /*
            stmt2.executeUpdate("ALTER TABLE club5 ADD nwindow_starttime time");
            stmt2.executeUpdate("ALTER TABLE club5 ADD nwindow_endtime time");
            stmt2.executeUpdate("ALTER TABLE club5 ADD notify_interval tinyint NOT NULL");
            
            stmt2.executeUpdate("ALTER TABLE club5 ADD hdcpSystem varchar(8) NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD clubNum varchar(8) NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD clubAssocNum varchar(8) NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD allowMemPost tinyint NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD lastHdcpSync datetime NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD hdcpStartDate date NOT NULL");
            stmt2.executeUpdate("ALTER TABLE club5 ADD hdcpEndDate date NOT NULL");
            */
            
            /*
            // Add notification tables for TLT system
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS notifications ( " +                                    
                    "notification_id int(11) NOT NULL auto_increment, " +              
                    "created_by varchar(32) NOT NULL, " +                              
                    "created_datetime datetime NOT NULL, " +                           
                    "req_datetime datetime NOT NULL default '0000-00-00 00:00:00', " + 
                    "course_id int(11) NOT NULL default '0', " +                       
                    "in_use_by varchar(15) NOT NULL default '', " +                    
                    "in_use_at datetime NOT NULL default '0000-00-00 00:00:00', " +    
                    "hideNotes tinyint(4) NOT NULL default '0', " +                    
                    "notes text, " +                                                   
                    "converted tinyint(4) NOT NULL default '0', " +                    
                    "converted_at datetime NOT NULL default '0000-00-00 00:00:00', " + 
                    "converted_by varchar(15) NOT NULL default '', " +                 
                    "teecurr_id int(11) NOT NULL default '0', " +                      
                    "teepast_id int(11) NOT NULL default '0', " +                      
                    "PRIMARY KEY (notification_id) " +                           
                ") ENGINE=MyISAM;");
            
            
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS notifications_players ( " + 
                     "notification_player_id int(11) NOT NULL auto_increment, " + 
                     "notification_id int(11) NOT NULL default '0', " + 
                     "username varchar(12) NOT NULL default '', " + 
                     "cw varchar(4) NOT NULL default '', " + 
                     "player_name varchar(64) NOT NULL default '', " + 
                     "9hole tinyint(4) NOT NULL default '0', " + 
                     "pos tinyint(4) NOT NULL default '0', " + 
                     "PRIMARY KEY (notification_player_id), " + 
                     "UNIQUE KEY pos (notification_id, pos) " + 
                ") ENGINE=MyISAM;");
            
            
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS score_postings (" + 
                    "posting_id int(11) NOT NULL auto_increment, " +
                    "hdcpNum varchar(15) NOT NULL, " +
                    "date date NOT NULL, " +
                    "score tinyint(4) NOT NULL, " +
                    "type varchar(1) NOT NULL, " +
                    "hdcpIndex double(3,1) NOT NULL, " +
                    "PRIMARY KEY (posting_id), " +
                    "KEY hdcpNum (hdcpNum) " +
                ") ENGINE=MyISAM;");
            
            
            stmt2.executeUpdate("" +
                "CREATE TABLE IF NOT EXISTS tees (" +
                    "tee_id int(11) NOT NULL auto_increment, " + 
                    "course_id int(11) NOT NULL, " +
                    "tee_name varchar(24) NOT NULL, " +
                    "tee_rating18 double(3,1) NOT NULL, " +
                    "tee_slope18 int(11) NOT NULL, " +
                    "tee_ratingF9 double(3,1) NOT NULL, " +
                    "tee_slopeF9 int(11) NOT NULL, " +
                    "tee_ratingB9 double(3,1) NOT NULL, " +
                    "tee_slopeB9 int(11) NOT NULL, " +
                    "PRIMARY KEY  (tee_id) " +
                ") ENGINE=MyISAM;");
            */
            
            /*
             * GET ALL CLUBS THAT HAVE MORE THAN ONE COURSE
            rs2 = stmt2.executeQuery("SELECT COUNT(*) AS total FROM clubparm2");
            
            while (rs2.next()) {
                
               if (rs2.getInt(1) > 1) {
                   
                   out.println("<br>" + club + " has " + rs2.getInt(1));
                   i++;
               }
            }
            
            */
            
            
            //  START auto_blocker UPDATES
            
            //stmt2.executeUpdate("ALTER TABLE teecurr2 ADD auto_blocked TINYINT NOT NULL");
            //stmt2.executeUpdate("UPDATE teecurr2 SET auto_blocked = 1 WHERE blocker = \"Auto-Blocker\""); // probably not needed but why not...
            
            //  END auto_blocked UPDATE
            
            
            
            
            
            // START adv email updates member2b table
            
            /*
            out.print("<br>Updating member2b");
            out.flush();
            
            stmt2.executeUpdate("CREATE TABLE t1 LIKE member2b");
            stmt2.executeUpdate("DROP INDEX ind1 ON t1");
            stmt2.executeUpdate("DROP INDEX ind2 ON t1");
            stmt2.executeUpdate("DROP INDEX ind3 ON t1");
            stmt2.executeUpdate("ALTER TABLE t1 ADD member_id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
            //stmt2.executeUpdate("ALTER TABLE t1 ADD email_teetimes INT;");
            stmt2.executeUpdate("ALTER TABLE t1 ADD email_proshop INT;");
            stmt2.executeUpdate("ALTER TABLE t1 ADD email_members INT;");
            stmt2.executeUpdate("ALTER TABLE t1 ADD email_bounce_hard INT;");
            stmt2.executeUpdate("ALTER TABLE t1 ADD email_bounce_soft INT;");
            stmt2.executeUpdate("ALTER TABLE t1 ADD private_tt INT;");
            stmt2.executeUpdate("CREATE INDEX username ON t1 (username)");
            stmt2.executeUpdate("CREATE INDEX m_ship ON t1 (m_ship)");
            stmt2.executeUpdate("CREATE INDEX m_type ON t1 (m_type)");
            stmt2.executeUpdate("CREATE INDEX memNum ON t1 (memNum)");
            stmt2.executeUpdate("CREATE INDEX webid ON t1 (webid)");
            stmt2.executeUpdate("INSERT INTO t1 (username, password, name_last, name_first, name_mi, m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid) (SELECT * FROM member2b ORDER BY username, password, name_last, name_first, name_mi, m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid);");
            stmt2.executeUpdate("DROP TABLE member2b");
            stmt2.executeUpdate("ALTER TABLE t1 RENAME member2b");
            
            
            out.print(", done.");
             */
            /*
            out.println("<br>Updating clubparm2");  
            out.flush();
            
            stmt2.executeUpdate("CREATE TABLE t1 LIKE clubparm2");
            stmt2.executeUpdate("ALTER TABLE t1 ADD clubparm_id INT AUTO_INCREMENT PRIMsARY KEY FIRST");
            stmt2.executeUpdate("INSERT INTO t1 (courseName,first_hr,first_min,last_hr,last_min,betwn,xx,alt,fives,tmode1,tmodea1,tmode2,tmodea2,tmode3,tmodea3,tmode4,tmodea4,tmode5,tmodea5,tmode6,tmodea6,tmode7,tmodea7,tmode8,tmodea8,tmode9,tmodea9,tmode10,tmodea10,tmode11,tmodea11,tmode12,tmodea12,tmode13,tmodea13,tmode14,tmodea14,tmode15,tmodea15,tmode16,tmodea16,t9pos1,tpos1,t9pos2,tpos2,t9pos3,tpos3,t9pos4,tpos4,t9pos5,tpos5,t9pos6,tpos6,t9pos7,tpos7,t9pos8,tpos8,t9pos9,tpos9,t9pos10,tpos10,t9pos11,tpos11,t9pos12,tpos12,t9pos13,tpos13,t9pos14,tpos14,t9pos15,tpos15,t9pos16,tpos16,courseid,tOpt1,tOpt2,tOpt3,tOpt4,tOpt5,tOpt6,tOpt7,tOpt8,tOpt9,tOpt10,tOpt11,tOpt12,tOpt13,tOpt14,tOpt15,tOpt16) (SELECT * FROM clubparm2 ORDER BY courseName,first_hr,first_min,last_hr,last_min,betwn,xx,alt,fives,tmode1,tmodea1,tmode2,tmodea2,tmode3,tmodea3,tmode4,tmodea4,tmode5,tmodea5,tmode6,tmodea6,tmode7,tmodea7,tmode8,tmodea8,tmode9,tmodea9,tmode10,tmodea10,tmode11,tmodea11,tmode12,tmodea12,tmode13,tmodea13,tmode14,tmodea14,tmode15,tmodea15,tmode16,tmodea16,t9pos1,tpos1,t9pos2,tpos2,t9pos3,tpos3,t9pos4,tpos4,t9pos5,tpos5,t9pos6,tpos6,t9pos7,tpos7,t9pos8,tpos8,t9pos9,tpos9,t9pos10,tpos10,t9pos11,tpos11,t9pos12,tpos12,t9pos13,tpos13,t9pos14,tpos14,t9pos15,tpos15,t9pos16,tpos16,courseid,tOpt1,tOpt2,tOpt3,tOpt4,tOpt5,tOpt6,tOpt7,tOpt8,tOpt9,tOpt10,tOpt11,tOpt12,tOpt13,tOpt14,tOpt15,tOpt16)");
            stmt2.executeUpdate("DROP TABLE clubparm2");
            stmt2.executeUpdate("ALTER TABLE t1 RENAME clubparm2");
            */
             
            
            /*
            //  START PACE OF PLAY UPDATES
            
            
            out.println("<br>Updating club5"); 
            stmt2.executeUpdate("ALTER TABLE club5 ADD paceofplay tinyint NOT NULL default '0'");
            */
            /*
            out.println("<br>Updating teepast2"); 
            
            stmt2.executeUpdate("CREATE TABLE t1 LIKE teepast2");
            stmt2.executeUpdate("DROP INDEX ind1 ON t1");
            stmt2.executeUpdate("DROP INDEX ind2 ON t1");
            stmt2.executeUpdate("DROP INDEX ind3 ON t1");
            stmt2.executeUpdate("DROP INDEX ind4 ON t1");
            stmt2.executeUpdate("DROP INDEX ind5 ON t1");
            stmt2.executeUpdate("ALTER TABLE t1 ADD teecurr_id INT NULL FIRST");
            stmt2.executeUpdate("ALTER TABLE t1 ADD teepast_id INT AUTO_INCREMENT PRIMARY KEY FIRST");
            stmt2.executeUpdate("ALTER TABLE t1 ADD pace_status_id INT NOT NULL default '0'");
            stmt2.executeUpdate("CREATE UNIQUE INDEX teecurr_id ON t1 (teecurr_id)");
            stmt2.executeUpdate("CREATE INDEX pace_status_id ON t1 (pace_status_id)");
            stmt2.executeUpdate("CREATE INDEX date ON t1 (date)");
            stmt2.executeUpdate("CREATE INDEX time ON t1 (time)");
            stmt2.executeUpdate("CREATE INDEX fb ON t1 (fb)");
            stmt2.executeUpdate("CREATE INDEX courseName ON t1 (courseName)");
            stmt2.executeUpdate("CREATE INDEX mm ON t1 (mm)");
            stmt2.executeUpdate("CREATE INDEX yy ON t1 (yy)");
            stmt2.executeUpdate("CREATE INDEX player1 ON t1 (player1)");
            stmt2.executeUpdate("CREATE INDEX player2 ON t1 (player2)");
            stmt2.executeUpdate("CREATE INDEX player3 ON t1 (player3)");
            stmt2.executeUpdate("CREATE INDEX player4 ON t1 (player4)");
            stmt2.executeUpdate("CREATE INDEX player5 ON t1 (player5)");
            stmt2.executeUpdate("CREATE INDEX username1 ON t1 (username1)");
            stmt2.executeUpdate("CREATE INDEX username2 ON t1 (username2)");
            stmt2.executeUpdate("CREATE INDEX username3 ON t1 (username3)");
            stmt2.executeUpdate("CREATE INDEX username4 ON t1 (username4)");
            stmt2.executeUpdate("CREATE INDEX username5 ON t1 (username5)");
            stmt2.executeUpdate("INSERT INTO t1 (date, mm, dd, yy, day, hr, min, time, event, event_color, restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95) (SELECT * FROM teepast2 ORDER BY date, mm, dd, yy, day, hr, min, time, event, event_color, restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95);");
            stmt2.executeUpdate("DROP TABLE teepast2");
            stmt2.executeUpdate("ALTER TABLE t1 RENAME teepast2");
            
            out.print(", done.");
            */
            
            
            /*
            out.println("<br>Updating teecurr2"); 
            out.flush();
            
            stmt2.executeUpdate("ALTER TABLE teecurr2 ADD pace_status_id INT NOT NULL default '0'");
            stmt2.executeUpdate("CREATE INDEX pace_status_id ON teecurr2 (pace_status_id)");
            
            */
            /*
            out.print(", done.");
            out.println("<br>Creating pace_status"); 
            out.flush();
            
            stmt2.executeUpdate("CREATE TABLE pace_status (" +
                                "pace_status_id int NOT NULL auto_increment, " +
                                "pace_status_sort int default NULL, " +
                                "pace_status_name varchar(16) default NULL, " +
                                "pace_status_color varchar(16) default NULL, " +
                                "pace_leeway double default NULL, " +
                                "PRIMARY KEY (pace_status_id) " +
                                ") ENGINE=MyISAM");
            
            out.print(", populating defaults"); 
            out.flush();
            
            stmt2.executeUpdate("INSERT INTO pace_status (pace_status_sort, pace_status_name, pace_status_color, pace_leeway) VALUES " +
                                "(\"1\", \"On Pace\", \"green\", \"0.045\"), " +
                                "(\"2\", \"Falling Behind\", \"yellow\", \"0.09\"), " + 
                                "(\"3\", \"Behind\", \"pink\", \"0.12\")");
            
            
            out.print(", done.");
            out.println("<br>Creating pace_entries"); 
            out.flush();
                     
            stmt2.executeUpdate("CREATE TABLE pace_entries (" +
                                "pace_entry_id int(11) NOT NULL auto_increment," +
                                "teecurr_id int(11) default NULL," +
                                "hole_number tinyint(4) default NULL," +
                                "invert tinyint(4) default NULL," +
                                "hole_timestamp time default NULL," +
                                "PRIMARY KEY (pace_entry_id), " +
                                "UNIQUE KEY teecurr_id (teecurr_id, hole_number)" +
                                ") ENGINE=MyISAM");
            
            out.print(", done.");
            out.print("dropping pace_bechmarks.");
            stmt2.executeUpdate("DROP TABLE pace_benchmarks");
            
            
            out.print(", done.");
            out.println("<br>Creating pace_benchmarks"); 
             
            stmt2.executeUpdate("CREATE TABLE pace_benchmarks (" +
                                "pace_benchmark_id int(11) NOT NULL auto_increment," +
                                "clubparm_id int(11) default NULL," +
                                "hole_number tinyint(4) default NULL," +
                                "hole_pace int(11) default NULL," +
                                "invert tinyint(4) default NULL," +
                                "PRIMARY KEY (pace_benchmark_id), " +
                                "UNIQUE KEY clubparm_id (clubparm_id, hole_number)" +
                                ") ENGINE=MyISAM");
            //stmt2.executeUpdate("CREATE INDEX clubparm_id ON pace_benchmarks (clubparm_id)");
            
            */
            
            
            // END OF PACE OF PLAY UPDATES
            
            
            
            
            
            
            
            
            
            // STRART POS field lengthing updates
            /*
            
            stmt2.executeUpdate("ALTER TABLE guest5 CHANGE gstItem gstItem varchar(20)");
            stmt2.executeUpdate("ALTER TABLE guest5 CHANGE gst9Item gst9Item varchar(20)");
            
            stmt2.executeUpdate("ALTER TABLE mship5 CHANGE mpos mpos varchar(20)");
            stmt2.executeUpdate("ALTER TABLE mship5 CHANGE mposc mposc varchar(20)");
            stmt2.executeUpdate("ALTER TABLE mship5 CHANGE m9posc m9posc varchar(20)");
            stmt2.executeUpdate("ALTER TABLE mship5 CHANGE mshipItem mshipItem varchar(20)");
            stmt2.executeUpdate("ALTER TABLE mship5 CHANGE mship9Item mship9Item varchar(20)");
            
            */
            // END OF POS field lengthing updates
            
            
            
            
            
            
            
            /*
            out.println("<br>Creating user_types"); 
            out.flush();
            
            stmt2.executeUpdate("CREATE TABLE user_types (" +
                                "user_type_id int(11) NOT NULL auto_increment," +
                                "user_type varchar(32) NOT NULL," +
                                "PRIMARY KEY (user_type_id) " +
                                ") ENGINE=MyISAM");
                    
            out.print(", populating defaults"); 
            out.flush();
            
            stmt2.executeUpdate("INSERT INTO user_types (user_type) VALUES " +
                                "(\"Member\"), " +
                                "(\"Remote\"), " + 
                                "(\"Proshop\"), " + 
                                "(\"Admin\"), " + 
                                "(\"Support\"), " + 
                                "(\"Sales\") ");
                    
            out.print(", done.");
            out.flush();
            /*
/*
            // stmt2.executeUpdate("ALTER TABLE lessonbook5 ADD recid INTEGER NOT NULL AUTO_INCREMENT, ADD INDEX ind4 (recid)");
            
            // CREATE TABLE t1 LIKE teecurr2;
            // ALTER TABLE t1 ADD teetime_id INT AUTO_INCREMENT PRIMARY KEY FIRST;
            // INSERT INTO t1 SELECT * FROM teecurr2 ORDER BY date, mm, dd, tt, day, hr, min, time, event, event_color, restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, player5, username5, p5cw, hndcp5, show5, notes, hideNotes, lottery, courseName, blocker, proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole;
            
         stmt2.executeUpdate("CREATE TABLE t1 LIKE teecurr2");
         out.print(".");
         //out.flush();
         //resp.flushBuffer();
         stmt2.executeUpdate("DROP INDEX ind1 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("DROP INDEX ind2 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("DROP INDEX ind3 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("DROP INDEX ind4 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("DROP INDEX ind5 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("DROP INDEX ind6 ON t1");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("ALTER TABLE t1 ADD teecurr_id INT AUTO_INCREMENT PRIMARY KEY FIRST;");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX date ON t1 (date)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX time ON t1 (time)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX fb ON t1 (fb)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX courseName ON t1 (courseName)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX in_use ON t1 (in_use)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX blocker ON t1 (blocker)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX lottery ON t1 (lottery)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX mm ON t1 (mm)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX dd ON t1 (dd)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX yy ON t1 (yy)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX player1 ON t1 (player1)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX player2 ON t1 (player2)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX player3 ON t1 (player3)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX player4 ON t1 (player4)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX player5 ON t1 (player5)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX username1 ON t1 (username1)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX username2 ON t1 (username2)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX username3 ON t1 (username3)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX username4 ON t1 (username4)");
         out.print(".");
         //out.flush();
         stmt2.executeUpdate("CREATE INDEX username5 ON t1 (username5)");
         out.print(".(inserting)");
         out.flush();
         stmt2.executeUpdate("INSERT INTO t1 (date, mm, dd, yy, day, hr, min, time, event, event_color, restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, player5, username5, p5cw, hndcp5, show5, notes, hideNotes, lottery, courseName, blocker, proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole) (SELECT * FROM teecurr2 ORDER BY date, mm, dd, yy, day, hr, min, time, event, event_color, restriction, rest_color, player1, player2, player3, player4, username1, username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, player5, username5, p5cw, hndcp5, show5, notes, hideNotes, lottery, courseName, blocker, proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole);");
         out.print(".");
         out.flush();
         stmt2.executeUpdate("DROP TABLE teecurr2");
         out.print(".");
         out.flush();
         stmt2.executeUpdate("ALTER TABLE t1 RENAME teecurr2");
         out.print(". &nbsp; Done.");
         out.flush();
*/
         stmt2.close(); 
         con2.close();
         
      } // loop all clubs
      
      //out.println("<br>" + i + " clubs have more than one course.");
            
      stmt1.close();
      con1.close();
      
   }
   catch (Exception e) {

      // Error connecting to db....

      out.println("<BR><BR><H3>Fatal Error!</H3>");
      out.println("Error performing update to club '" + club + "'.");
      out.println("<BR>Exception: "+ e.getMessage());
      out.println("<BR>Message: "+ e.toString());
      out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
      out.println("</BODY></HTML>");
      out.close();
      return;
   }

   out.println("<BR><BR>Upgrade Finished!  The upgrade is complete for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
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
        out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>.");
        out.println("</BODY></HTML>");
        out.close();
        return;
    }
    
   out.println("<BR><BR>Test Finished!  The test is complete for all clubs.");
   out.println("<BR><BR> <A HREF=\"/v5/support_main.htm\">Return</A>");
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