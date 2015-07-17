/***************************************************************************************
 *   Support_upgrade:  This servlet will process the upgrade request from Support's Upgrade page.
 *
 *
 *     ******* Use this job to add or change data base tables **********
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

import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.verifySlot;

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
   out.println("<BODY><CENTER><H3>WARNING: PERMANENT DATABASE CHANGES PENDING!</H3>");
   out.println("<BR><BR>Click 'Update' to start the job.");
   out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A><BR><BR>");
   
   out.println("<BR><BR>... Add custom question fields in evntsup2b for demov4 ....<BR><BR>");    // CHANGE THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   //out.println("<BR><BR>... Add custom question fields in evntsup2b for ALL CLUBS  ....<BR><BR>");    // CHANGE THIS !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
   
   out.println("<form method=post><input type=submit value=\"Update\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" <input type=hidden value=\"update\" name=\"todo\"></form>");

   out.println("<form method=post><input type=submit value=\"Update Dining\" onclick=\"return confirm('Are you sure?')\">");
   out.println(" <input type=hidden value=\"dining_update\" name=\"todo\"></form>");
   
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
   
   } else if (action.equals("test")) {
       
       doTest(out);
   
   } else if (action.equals("dining_update")) {
       
       doDiningUpdate(out);
   
   } else {
   
       out.println("<p>Nothing to do.</p>todo="+action);
   }

   return;

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
    out.println("<BODY><H3>Starting DB Update...</H3>");
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
      out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
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

            con2 = dbConn.Connect(club);            // get a connection to this club's db
            stmt2 = con2.createStatement();         // create a statement
            stmt3 = con2.createStatement();         // create another statement


            
            
            
                        
            if (!club.equals("demov4")) {     // use this to test with one club first before running for all clubs
                skip = true;
            } else {
                skip = false;
            }

            if (!skip) {

               out.println("<br><br>");
               out.print("[" + x1 + "/" + x2 + "] Starting " + club);
               out.flush();

               
               // Insert job code !!!!!!!!!!!!!!!!!!!!!!!!!!!!
               
               /*  NOTES:
               * 
               **********  remember to update Support_init!
               * 
               */  
                try {


                   
                   
                   
                   
                } catch (Exception exc) {
                    out.println(club + " failed!!!!!!!!!!!!!!!!!!!! " + exc.toString());
                }
               
            }     // end of IF skip
               

            stmt2.close(); 
            con2.close();

        } // loop all clubs

         //out.println("<br>" + i + " clubs have more than one course.");

        stmt1.close();
        con1.close();
                
        // catch is down at the bottom !!!!
        
        
                
               
               //  Add the following for all other clubs once the Invoice code is ready!!!!!!!!!!!!!!!!!!
               /*
               stmt2.executeUpdate("ALTER TABLE staff_list " +
                                   "ADD invoice_golf1 tinyint(4) NOT NULL default '0', " +
                                   "ADD invoice_flxrez1 tinyint(4) NOT NULL default '0', " +
                                   "ADD invoice_dining1 tinyint(4) NOT NULL default '0', " +
                                   "ADD invoice_golf2 tinyint(4) NOT NULL default '0', " +
                                   "ADD invoice_flxrez2 tinyint(4) NOT NULL default '0', " +
                                   "ADD invoice_dining2 tinyint(4) NOT NULL default '0';");
               
                
               stmt2.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS invoice (" +
                    "id int(11) NOT NULL auto_increment, " +
                    "invoiceNumber int(11) default '0', " +
                    "invStatus enum('0','1','2','3','4','5') NOT NULL default '0' COMMENT '[0=unknown, 1=Paid, 2=Pending, 3=AckReceipt, 4=AckPayment, 5=new]', " +
                    "payDueDate date default NULL, " +
                    "dateCreated date default NULL, " +
                    "paidDate date default NULL, " +
                    "totalCnt int(11) default '0', " +
                    "inactCnt int(11) default '0', " +
                    "excludedCnt int(11) default '0', " +
                    "billingRate enum('0','1','2','3') default '0' COMMENT '[0=none, 1= Per Adult, 2= Per Mship, 3= Setup]', " +
                    "billableCnt int(11) default NULL, " +
                    "amountDue decimal(10,0) default '0', " +
                    "checkNumber int(11) default '0', " +
                    "activity_id int(11) default '0', " +
                    "item varchar(50) default '', " +
                    "description text default '', " +
                    "PRIMARY KEY  (id) " +
                    ") ENGINE=MyISAM;");               
               
               
               stmt2.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS invoice_data (" +
                    "ForeTeesState enum('0','1','2','3','4','5','6') default '0', " +
                    "nextDueDate date default NULL, " +
                    "notifyDays int(11) default '0', " +
                    "currInvoice int(11) default '0', " +
                    "address1 varchar(40) default '', " +
                    "address2 varchar(20) default '', " +
                    "city varchar(30) default '', " +
                    "state varchar(4) default '', " +
                    "zipCode varchar(12) default '', " +
                    "invEmailContact varchar(40) default '', " +
                    "frequency enum('0','1','2') default '0', " +
                    "defBillType enum('0','1','2','3') default '0', " +
                    "invoiceOn tinyint(1) default '0' " +
                    ") ENGINE=MyISAM;");               
               
               
               stmt2.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS invoice_items (" +
                    "id int(11) NOT NULL auto_increment, " +
                    "date date default NULL, " +
                    "name varchar(30) default '', " +
                    "type tinyint(4) default '0', " +
                    "count int(11) default '0', " +
                    "selected tinyint(1) default '0', " +
                    "PRIMARY KEY  (id) " +
                    ") ENGINE=MyISAM;");               
               
               
                
                                            
               //  Add the TAI POS temp table - MUST ADD to other sites when TAI is ready !!!!!!!!!!!!!!

               /*
               stmt2.executeUpdate(
                      "CREATE TABLE IF NOT EXISTS POS_temp (" +
                         "id int(11) NOT NULL auto_increment, " +
                         "date bigint NOT NULL default '0', " +
                         "time int(11) NOT NULL default '0', " +
                         "posid varchar(15) NOT NULL default '', " +
                         "name varchar(42) NOT NULL default '', " +
                         "quantity int(11) NOT NULL default '0', " +
                         "item_code varchar(20) NOT NULL default '', " +
                         "item_fee varchar(8) NOT NULL default '', " +
                         "tax varchar(6) NOT NULL default '', " +
                         "gname1 varchar(43) NOT NULL default '', " +
                         "gname2 varchar(43) NOT NULL default '', " +
                         "gname3 varchar(43) NOT NULL default '', " +
                         "gname4 varchar(43) NOT NULL default '', " +
                         "gname5 varchar(43) NOT NULL default '', " +
                         "PRIMARY KEY (id)" +
                      ") ENGINE=MyISAM;");

                */
               
               
               
               


            
            
            









/*
 * 
 *            // Previous jobs :
 * 
 * 
 * 
 * 
 * 
 * 
                     stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                         "ADD customQuest1 varchar(30) NOT NULL default '' AFTER other5A3," + 
                         "ADD customQuest2 varchar(30) NOT NULL default '' AFTER customQuest1," + 
                         "ADD customQuest3 varchar(30) NOT NULL default '' AFTER customQuest2," + 
                         "ADD customQuest4 varchar(30) NOT NULL default '' AFTER customQuest3," + 
                         "ADD customQuest5 varchar(30) NOT NULL default '' AFTER customQuest4;");

* 
                     stmt2.executeUpdate("ALTER TABLE events2b " +
                         "ADD ask_custom1 tinyint(1) NOT NULL default '0' AFTER ask_otherA3," + 
                         "ADD req_custom1 tinyint(1) NOT NULL default '0' AFTER req_otherA3;");
                  
 * 
                     stmt2.executeUpdate("ALTER TABLE events2b " +
                                        "CHANGE otherQ1 otherQ1 varchar(64) NOT NULL DEFAULT '', " +
                                        "CHANGE otherQ2 otherQ2 varchar(64) NOT NULL DEFAULT '', " +
                                        "CHANGE otherQ3 otherQ3 varchar(64) NOT NULL DEFAULT '';");
                   

 * 
        
                    stmt2.executeUpdate("ALTER TABLE club5 "
                            + "ADD allow_responsive tinyint NOT NULL default '0', "
                            + "ADD force_responsive tinyint NOT NULL default '0';");
 
                     stmt2.executeUpdate("ALTER TABLE evntsup2b " +
                                        "CHANGE name name varchar(42) NOT NULL DEFAULT '';");
                   
                     stmt2.executeUpdate("ALTER TABLE teecurr2 " +
                                        "CHANGE event event varchar(42) NOT NULL DEFAULT '';");
                   
                    stmt2.executeUpdate("ALTER TABLE teepast2 " +
                                        "CHANGE event event varchar(42) NOT NULL DEFAULT '';");
                   
                    stmt2.executeUpdate("ALTER TABLE teepastempty " +
                                        "CHANGE event event varchar(42) NOT NULL DEFAULT '';");
                   
                    stmt2.executeUpdate("ALTER TABLE eventa4 " +
                                        "CHANGE name name varchar(42) NOT NULL DEFAULT '';");
*
 * 
                    stmt2.executeUpdate("ALTER TABLE events2b " +
                                        "ADD recur_id int NOT NULL default '0', " +
                                        "CHANGE name name varchar(42) NOT NULL DEFAULT '';");

 * 
                   // add email logging tables
                   stmt2.executeUpdate("" +
                           "CREATE TABLE IF NOT EXISTS `email_audit_log` (" +
                              "`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
                              "`email_type` tinyint(4) NOT NULL," +
                              "`sent_date` datetime DEFAULT NULL," +
                              "`recipients` int(11) NOT NULL," +
                              "`attachments` tinyint(4) NOT NULL," +
                              "`from_address` varchar(64) NOT NULL DEFAULT ''," +
                              "`subject` varchar(64) NOT NULL DEFAULT ''," +
                              "`message` text NOT NULL," +
                              "PRIMARY KEY (`id`)" +
                           ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
                   
                   stmt2.executeUpdate("" +
                           "CREATE TABLE IF NOT EXISTS `email_audit_log_details` (" +
                              "`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
                              "`audit_log_id` int(11) NOT NULL," +
                              "`username` varchar(15) DEFAULT ''," +
                              "`email` varchar(64) NOT NULL DEFAULT ''," +
                              "PRIMARY KEY (`id`)," + 
                              "KEY `audit_log_id_key` (`audit_log_id`)" +
                           ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");                   
               
                    stmt2.executeUpdate("UPDATE member2b SET emailOpt2 = 1 WHERE emailOpt = 1");    // set 2nd email address flag if #1 is set
                   
 *
                   stmt2.executeUpdate("" +
                              "ALTER TABLE member2b " +
                              "ADD emailOpt2 smallint NOT NULL default '0' AFTER emailOpt, " +
                              "ADD clubEmailOpt1 smallint NOT NULL default '1' AFTER emailOpt2, " +
                              "ADD clubEmailOpt2 smallint NOT NULL default '1' AFTER clubEmailOpt1, " +
                              "ADD memEmailOpt1 smallint NOT NULL default '1' AFTER clubEmailOpt2, " +
                              "ADD memEmailOpt2 smallint NOT NULL default '1' AFTER memEmailOpt1;");

                   
 * 
                    stmt2.executeUpdate("ALTER TABLE lreqs3 " +
                                        "ADD recur_id bigint NOT NULL default '0'");

                    stmt2.executeUpdate("ALTER TABLE lottery3 " +
                                        "ADD recur_days smallint NOT NULL default '0'");


 * 
                   stmt2.executeUpdate("ALTER TABLE lottery3 " +
                                   "ADD recurrpro tinyint(4) NOT NULL default 0, " +
                                   "ADD recurrmem tinyint(4) NOT NULL default 0;");
               
 * 
                   stmt2.executeUpdate("ALTER TABLE member2b " +
                                   "ADD display_partner_hndcp tinyint(4) NOT NULL default 0;");
 *
                    stmt2.executeUpdate("ALTER TABLE club5 ADD memberInact smallint NOT NULL default '0'");

                    stmt2.executeUpdate("ALTER TABLE member2b "
                                       + "ADD flexid varchar(15) NOT NULL default '';");
                    
                    stmt2.executeUpdate("ALTER TABLE club5 ADD ftConnect smallint NOT NULL default '0'");
                    
                    stmt2.executeUpdate("ALTER TABLE mem_notice ADD teetime_cal smallint NOT NULL default '0'");
* 
* 
                    stmt2.executeUpdate("ALTER TABLE email_content "
                                       + "ADD courseName varchar(30) NOT NULL default '';");
 *
 * 
 *
                    stmt2.executeUpdate("ALTER TABLE teepast2 "
                                       + "ADD event_type int NOT NULL default '0', "
                                       + "ADD hole varchar(4) default '';");

* 
* 
                    stmt2.executeUpdate("UPDATE lottery3 SET xvalue = 4 WHERE xvalue = 1");
                    
* 
* 
* 
* 
                    stmt2.executeUpdate("ALTER TABLE teecurr2 ADD related_id int NOT NULL default '0'");
                    
 

                   stmt2.executeUpdate("" +
                           "CREATE TABLE IF NOT EXISTS `teesheet_partner_config` (" +
                              "`id` int(11) unsigned NOT NULL AUTO_INCREMENT," +
                              "`partner_id` int(11) NOT NULL," +
                              "`auth_user` varchar(16) NOT NULL DEFAULT ''," +
                              "`auth_pass` varchar(16) NOT NULL DEFAULT ''," +
                              "`guest_type` varchar(20) NOT NULL DEFAULT ''," +
                              "`guest_tmode` char(3) NOT NULL DEFAULT ''," +
                              "`display_name` varchar(24) NOT NULL DEFAULT ''," +
                              "`allow_members_to_join` tinyint(4) NOT NULL DEFAULT '0'," +
                              "`hide_outside_times` tinyint(4) NOT NULL DEFAULT '0'," +
                              "`max_allowed_guests` tinyint(4) NOT NULL," +
                              "`availability_flags` char(4) NOT NULL DEFAULT ''," +
                              "`max_days_in_advance` int(11) NOT NULL," +
                              "`fee_per_player` double(5,2) NOT NULL," +
                              "`enabled` tinyint(4) NOT NULL DEFAULT '0'," +
                              "PRIMARY KEY (`id`)," +
                              "UNIQUE KEY `ukey_partner_id` (`partner_id`)" +
                           ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");



               // FIX UP THE SCORE POSTINGS TABLE WITH A NEW UID COLUMN AND REDO THE INDEXS
               stmt2.executeUpdate("" +
                       "ALTER TABLE `score_postings` " +
                           "ADD COLUMN `provider_uuid` VARCHAR(64) NULL AFTER `tee_id`, " +
                           "ADD COLUMN `last_touched` DATETIME NULL AFTER `provider_uuid`;");

               stmt2.executeUpdate("UPDATE `score_postings` SET provider_uuid = CONCAT('transitional_',posting_id);");

               stmt2.executeUpdate("" +
                       "ALTER TABLE `score_postings` DROP KEY `entry`;");

               stmt2.executeUpdate("" +
                       "ALTER TABLE `score_postings` DROP KEY `hdcpNum`;");

               stmt2.executeUpdate("" +
                       "ALTER TABLE `score_postings` " +
                           "ADD UNIQUE `idx_provider_uuid` (`provider_uuid`), " +
                           "ADD INDEX `idx_hdcpNum` (`hdcpNum`);");

               stmt2.executeUpdate("UPDATE `score_postings` SET hdcpNum = CAST(hdcpNum AS UNSIGNED);");




               stmt2.executeUpdate("DROP FUNCTION IF EXISTS `get_918_teepast_by_player`");
               stmt2.executeUpdate("" +
                       "CREATE FUNCTION `get_918_teepast_by_player`(uname CHAR(15), start_date " +
                       "INT, end_date INT) RETURNS CHAR(100) CHARSET latin1 " +
                       "READS SQL DATA " +
                       "BEGIN " +
                       "    RETURN (SELECT " +
                       "    CONCAT_WS(';',IFNULL(SUM(tt.p9=1),0),IFNULL(SUM(tt.p9=0),0)) FROM ( " +
                       "	SELECT p91 AS p9 FROM teepast2 " +
                       "		WHERE `date` BETWEEN start_date AND end_date " +
                       "		AND username1 = uname AND show1 = 1 " +
                       "	UNION ALL " +
                       "	SELECT p92 AS p9 FROM teepast2 " +
                       "		WHERE `date` BETWEEN start_date AND end_date " +
                       "		AND username2 = uname AND show2 = 1 " +
                       "	UNION ALL " +
                       "	SELECT p93 AS p9 FROM teepast2 " +
                       "		WHERE `date` BETWEEN start_date AND end_date " +
                       "		AND username3 = uname AND show3 = 1 " +
                       "	UNION ALL " +
                       "	SELECT p94 AS p9 FROM teepast2 " +
                       "		WHERE `date` BETWEEN start_date AND end_date " +
                       "		AND username4 = uname AND show4 = 1 " +
                       "	UNION ALL " +
                       "	SELECT p95 AS p9 FROM teepast2 " +
                       "		WHERE `date` BETWEEN start_date AND end_date " +
                       "		AND username5 = uname AND show5 = 1 " +
                       "	) AS tt); " +
                       "END");



                    stmt2.executeUpdate("ALTER TABLE events2b " +
                                        "ADD memedit tinyint(1) NOT NULL default '0';");
 
 
  
                // change the index on score_postings table
                stmt2.executeUpdate("DROP INDEX hdcpNum ON score_postings");
                stmt2.executeUpdate("CREATE INDEX idx_date ON score_postings (date);");


               //  Add the gn21_sourceClubId to club5
               stmt2.executeUpdate("ALTER TABLE club5 ADD gn21_sourceClubId varchar(32) NOT NULL default '';");

 

                //  add style sheet file name field to club5
               stmt2.executeUpdate("ALTER TABLE club5 " +
                                   "ADD custom_styles varchar(30) NOT NULL default '';");
               
               
               
 
 * 
 *
               stmt2.executeUpdate("ALTER TABLE teecurr2 " +
                                   "ADD nopost1 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost2 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost3 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost4 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost5 tinyint(4) NOT NULL default '0';");
                
               stmt2.executeUpdate("ALTER TABLE teepast2 " +
                                   "ADD nopost1 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost2 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost3 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost4 tinyint(4) NOT NULL default '0', " +
                                   "ADD nopost5 tinyint(4) NOT NULL default '0';");
                
                                            
 * 
 * 
 * 
 * 
                //  add boxgroove option to club5
               stmt2.executeUpdate("ALTER TABLE club5 " +
                                   "ADD boxgroove tinyint(4) NOT NULL default 0, " +
                                   "ADD boxUser varchar(40) NOT NULL default '', " +
                                   "ADD boxPW varchar(40) NOT NULL default '';");
               
 * 
 * 
 * 
   
                //  add member2b fields - 1 for tracking when user read new skin instructions, and 1 for tee sheet jump setting
               stmt2.executeUpdate("ALTER TABLE member2b " +
                                   "ADD tee_sheet_jump tinyint(4) NOT NULL default 0, " +
                                   "ADD read_login_msg tinyint(4) NOT NULL default 0;");
               
               
 * 
 * 
              //  Add only_if_unaccomp and no_guests to email_content (new options)  - 3/28/2012 BP
 * 
               stmt2.executeUpdate("ALTER TABLE email_content " +
                                   "ADD only_if_unaccomp tinyint(4) NOT NULL default 0, " +
                                   "ADD no_guests tinyint(4) NOT NULL default 0;");

 * 
              
       ///////////// Update current tee sheets to have a higher, non-conflicting teecurr_id (originally for Olympic Club)
             
              
             
               Statement stmtx = null;
               PreparedStatement pstmtx = null;
               ResultSet rsx = null;
               
               try {
                   
                   int teecurr_id = 0;
                   int temp_teecurr_id = 0;
                   int change_count = 0;
                   
                   stmtx = con2.createStatement();
                   
                   rsx = stmtx.executeQuery("SELECT teecurr_id FROM teecurr2 "
                           + "WHERE teecurr_id IN (SELECT teecurr_id FROM teepast2) order by teecurr_id");
                   
                   while (rsx.next()) {
                       
                       teecurr_id = rsx.getInt("teecurr_id");
                       temp_teecurr_id = teecurr_id;
                       
                       temp_teecurr_id += 150000;
                       
                       try {
                           pstmtx = con2.prepareStatement("UPDATE teecurr2 SET teecurr_id = ? WHERE teecurr_id = ?");
                           pstmtx.clearParameters();
                           pstmtx.setInt(1, temp_teecurr_id);
                           pstmtx.setInt(2, teecurr_id);

                           pstmtx.executeUpdate();

                           change_count++;
                       } catch (Exception exc) { 
                           out.println("<br>Error updating member. Old ID: " + teecurr_id + ", New ID: " + temp_teecurr_id + ", ERR: " + exc.toString());
                       } finally {
                           try { pstmtx.close(); }
                           catch (Exception ignore) { }
                       }
                   }
                   
                   out.println("<br>CHANGES: " + change_count);
                   
               } catch (Exception exc) {
                   out.println("<br>Error looking up members.  ERR: " + exc.toString());
               } finally {
                   try { rsx.close(); }
                   catch (Exception ignore) { }
                   
                   try { stmtx.close(); }
                   catch (Exception ignore) { }
               }
              
        /////////////  End "Update current tee sheets"     
              
       /////////////  Scioto Reserve / Kinsale - Username update + processing to update tee times, events, etc ///////////////
             
               String memid_new = "";
               String memid = "";
               String newName = "";
               int count = 0;
               
               Statement stmtx = null;
               PreparedStatement pstmtx = null;
               ResultSet rsx = null;
               
               try {
               
               
                   stmtx = con2.createStatement();
                   
                   rsx = stmtx.executeQuery("SELECT username, CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) as mem_name FROM member2b WHERE "
                           + "username like '%-%' AND username not like 'K%'");
               
               
                   while (rsx.next()) {
                       
                       count = 0;
                       memid = rsx.getString("username");
                       newName = rsx.getString("mem_name");
                       
                       memid_new = "S" + memid;
                       
                       
                       try {
                           
                           pstmtx = con2.prepareStatement("UPDATE member2b SET username = ? WHERE username = ?");
                           pstmtx.clearParameters();
                           pstmtx.setString(1, memid_new);
                           pstmtx.setString(2, memid);
                           
                           count = pstmtx.executeUpdate();
                           
                       } catch (Exception exc) {
                           out.println("<br>Error updating member data");
                       } finally {
                           
                           try { pstmtx.close(); }
                           catch (Exception ignore) { }
                       }
                       
                       if (count > 0) {
                         Admin_editmem.updTeecurr(newName, memid_new, memid, con2);      // update teecurr with new values

                         Admin_editmem.updTeepast(newName, memid_new, memid, con2);      // update teepast with new values

                         Admin_editmem.updLreqs(newName, memid_new, memid, con2);        // update lreqs with new values

                         Admin_editmem.updPartner(memid_new, memid, con2);               // update partner with new values

                         Admin_editmem.updEvents(newName, memid_new, memid, con2);        // update evntSignUp with new values

                         Admin_editmem.updLessons(newName, memid_new, memid, con2);       // update the lesson books with new values
                       }
                   }
                 
               } catch (Exception exc) {
                   out.println("<br>Error looking up member data");
               } finally {
                   
                   try { rsx.close(); }
                   catch (Exception ignore) { }
                   
                   try { stmtx.close(); }
                   catch (Exception ignore) { }
               } 
              
          ////////////   End Scioto Reserve / Kinsale ///////////   
 * 
 *
               //  Add the custom_int field to lottery3
               stmt2.executeUpdate("ALTER TABLE lottery3 ADD custom_int INT NOT NULL default 0;");


 
    /////////////////////////  Olympic Club - Gather round counts for all members with a member subtype of "NP" (for year 2011)

               Statement stmtx = null;
               PreparedStatement pstmtx = null;
               ResultSet rsx = null;
               ResultSet rsx2 = null;
               
               try {
                   out.print("<br>Last Name, First Name, MI, MemNum, Rounds (2011)");
                   String tempUser = "";
                   String tempMnum = "";
                   String tempDisplayName = "";
                   int tempRounds = 0;
                   
                   stmtx = con2.createStatement();
                   
                   rsx = stmtx.executeQuery("SELECT username, memNum, name_last, name_first, name_mi FROM member2b WHERE msub_type = 'NP' ORDER BY name_last, name_first");
                   
                   while (rsx.next()) {
                       
                       tempRounds = 0;
                       
                       tempUser = rsx.getString(1);
                       tempMnum = rsx.getString(2);
                       
                       try {
                           pstmtx = con2.prepareStatement("SELECT count(*) FROM teepast2 WHERE yy = 2011 AND ((username1 = ? AND show1=1) OR (username2 = ? AND show2=1) or (username3 = ? AND show3=1) or (username4 = ? AND show4=1) or (username5 = ? AND show5=1))");
                           pstmtx.clearParameters();
                           pstmtx.setString(1, tempUser);
                           pstmtx.setString(2, tempUser);
                           pstmtx.setString(3, tempUser);
                           pstmtx.setString(4, tempUser);
                           pstmtx.setString(5, tempUser);

                           rsx2 = pstmtx.executeQuery();

                           if (rsx2.next()) {
                               tempRounds = rsx2.getInt(1);
                           }
                       } catch (Exception exc) {
                           tempRounds = 0;
                           out.println("<br>Error looking up round counts for " + tempDisplayName);
                       } finally {
                           
                           try { rsx2.close(); }
                           catch (Exception ignore) { }
                           
                           try { pstmtx.close(); }
                           catch (Exception ignore) { }
                       }
                       
                       out.println("<br>" + rsx.getString("name_last") + ", " + rsx.getString("name_first") + ", " + rsx.getString("name_mi") + ", " + tempUser + ", " + tempRounds);                       
                   }
                   
               } catch (Exception exc) {
                   out.println("<br>Error looking up info");
               } finally {
                   
                   try { rsx.close(); }
                   catch (Exception ignore) { }
                   
                   try { pstmtx.close(); }
                   catch (Exception ignore) { }
               } 
             
    /////////////////////////         
             
               //  Add the new skin date field to club5
               stmt2.executeUpdate("ALTER TABLE club5 ADD new_skin_date INT NOT NULL default 20120601;");


               //  Add the pro distribution lists tables

               stmt2.executeUpdate(
                      "CREATE TABLE IF NOT EXISTS distribution_lists (" +
                         "id int(11) NOT NULL auto_increment, " +
                         "name varchar(60) NOT NULL default '', " +
                         "owner varchar(15) NOT NULL default '', " +
                         "enabled tinyint(4) NOT NULL default '1', " +
                         "PRIMARY KEY (id), UNIQUE KEY key1 (name, owner)" +
                      ") ENGINE=MyISAM;");


               stmt2.executeUpdate(
                      "CREATE TABLE IF NOT EXISTS distribution_lists_entries (" +
                         "id int(11) NOT NULL auto_increment, " +
                         "distribution_list_id int(11), " +
                         "username varchar(15) NOT NULL default '', " +
                         "date_removed datetime NOT NULL default '0000-00-00 00:00:00', " +
                         "PRIMARY KEY (id)" +
                      ") ENGINE=MyISAM;");

               //
               //  Now copy any old distribution lists from dist4p to the new tables
               //
               String name = "";
               String owner = "";       
               String[] all_names = new String[100];
               int d = 0;

               PreparedStatement stmt = con2.prepareStatement (
                        "SELECT * FROM dist4p WHERE name != ''");

               stmt.clearParameters();        // clear the parms

               ResultSet rs = stmt.executeQuery();      // execute the prepared stmt

               while (rs.next()) {

                  name = rs.getString("name");
                  owner = rs.getString("owner");
                   
                  for (d=0; d<100; d++) {                     
                     all_names[d] = rs.getString("user" +(d+1));   // get all usernames                  
                  }
                  
                  //  build new tables
                  
                   pstmt = con2.prepareStatement ("INSERT INTO distribution_lists (name, owner, enabled) VALUES (?,?,1)");

                   pstmt.clearParameters();        // clear the parms
                   pstmt.setString(1, name);       // put the parm in pstmt
                   pstmt.setString(2, owner);

                   pstmt.executeUpdate();          // execute the prepared stmt
                   
                   int list_id = 0;
                   pstmt = con2.prepareStatement("SELECT LAST_INSERT_ID()");

                   ResultSet rsLastID = pstmt.executeQuery();

                   if (rsLastID.next()) {
                      list_id = rsLastID.getInt(1);         // get the id of the list we just created
                   }

                   for (d=0; d < 100; d++) {

                      name = all_names[d];       // get a username 

                      if (!name.equals("") && name != null) {

                         pstmt = con2.prepareStatement ("INSERT INTO distribution_lists_entries (distribution_list_id, username) VALUES (?,?)");

                         pstmt.clearParameters();        // clear the parms
                         pstmt.setInt(1, list_id);       // put the parm in pstmt
                         pstmt.setString(2, name);

                         pstmt.executeUpdate();          // execute the prepared stmt
                      }
                   }
                   pstmt.close();   // close the stmt
                  
               }             // end of WHILE dist4p entries
               stmt.close();
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
               stmt2.executeUpdate("ALTER TABLE email_content ADD dining TINYINT NOT NULL default 0 AFTER wait_list_signup;");


                // Count mobile users set up and mobile logins for American Golf clubs
                if (Utilities.isAGCClub(club)) {

                    Statement stmtx = null;
                    ResultSet rsx = null;

                    int user_count = 0;
                    int login_count = 0;

                    try {

                        stmtx = con2.createStatement();

                        rsx = stmtx.executeQuery("SELECT (SELECT count(*) FROM member2b WHERE mobile_user <> '') AS user_count, (SELECT SUM(mobile_count) FROM member2b) AS login_count");

                        if (rsx.next()) {

                            user_count = rsx.getInt("user_count");
                            login_count = rsx.getInt("login_count");

                            total_users += user_count;
                            total_logins += login_count;

                            out.println("<br>Club: " + club + ", Members set up for mobile: " + user_count + ", Total mobile logins for this club: " + login_count);
                        }


                    } catch (Exception exc) {
                        out.println("<br>Error looking up data for " + club);
                    } finally {

                        try { rsx.close(); }
                        catch (Exception ignore) { }

                        try { stmtx.close(); }
                        catch (Exception ignore) { }
                    }

                }


          //  Add the pos_progress table
        stmt2.executeUpdate(
                "CREATE TABLE IF NOT EXISTS pos_progress (" +
                   "pos_progress_id int(11) NOT NULL auto_increment, " +
                   "date int NOT NULL default '0', " +
                   "start_time int NOT NULL default '0', " +
                   "end_time int NOT NULL default '0', " +
                   "in_progress tinyint(4) NOT NULL default '0', " +
                   "override tinyint(4) NOT NULL default '0', " +
                   "user varchar(15) NOT NULL default '', " +
                   "PRIMARY KEY  (pos_progress_id)" +
                ") ENGINE=MyISAM;");



        // update event_log table to change all delete entries to cancel so they color properly (member cancels were being put in as deletes and only pros where using cancel)
        //stmt2.executeUpdate("UPDATE event_log SET action = 'CANCEL' WHERE action = 'DELETE';");


        // ADD pos_batch_log TABLE
        stmt2.executeUpdate(
                "CREATE TABLE IF NOT EXISTS pos_batch_log (" +
                   "pos_batch_log_id int(11) NOT NULL auto_increment, " +
                   "date_time datetime NOT NULL default '0000-00-00 00:00:00', " +
                   "batch text NOT NULL default '', " +
                   "PRIMARY KEY  (pos_batch_log_id)" +
                ") ENGINE=MyISAM;");


            /* Import ProAm names from past tee sheets for CWCPGA
             *
                PreparedStatement pstmtx = null;
                PreparedStatement pstmt2 = null;
                ResultSet rsx = null;
                ResultSet rsx2 = null;

                try {

                    rsx = stmt2.executeQuery("SELECT * FROM (SELECT player2 AS player FROM teepast2 WHERE player2 LIKE 'ProAm%' " +
                            "UNION ALL SELECT player3 AS player FROM teepast2 WHERE player3 LIKE 'ProAm%' " +
                            "UNION ALL SELECT player4 AS player FROM teepast2 WHERE player4 LIKE 'ProAm%') AS p " +
                            "GROUP BY player ORDER BY player");

                    while (rsx.next()) {
                        String tempname = "";
                        String fname = "";
                        String lname = "";
                        String ghin = "";
                        String curr = "";
                        String user = "";
                        String pass = "";
                        String mNum = "";

                        tempname = rsx.getString("player");

                        StringTokenizer tokx = new StringTokenizer(tempname, " ");

                        tokx.nextToken(); // 'ProAm'

                        if (tokx.countTokens() == 2) {

                            fname = tokx.nextToken();
                            lname = tokx.nextToken();

                        } else if (tokx.countTokens() >= 3) {

                            fname = tokx.nextToken();
                            lname = tokx.nextToken();
                            ghin = tokx.nextToken();
                        }

                        fname = fname.trim();
                        lname = lname.trim();
                        ghin = ghin.trim();

                        if (fname.startsWith("-")) {
                            fname = fname.substring(1);
                        }

                        if (lname.endsWith("0") || lname.endsWith("1") || lname.endsWith("2") || lname.endsWith("3") || lname.endsWith("4") ||
                            lname.endsWith("5") || lname.endsWith("6") || lname.endsWith("7") || lname.endsWith("8") || lname.endsWith("9")) {

                            int k = 1;

                            curr = lname.substring(lname.length() - k, lname.length() - (k - 1));

                            while (curr.equals("0") || curr.equals("1") || curr.equals("2") || curr.equals("3") || curr.equals("4") ||
                                   curr.equals("5") || curr.equals("6") || curr.equals("7") || curr.equals("8") || curr.equals("9")) {

                                k++;

                                curr = lname.substring(lname.length() - k, lname.length() - (k - 1));
                            }
                            k--;
                            ghin = lname.substring(lname.length() - k);
                            lname = lname.substring(0, lname.length() - k);
                        }

                        if (ghin.contains(".")) {
                            ghin = "";
                        }

                        if (ghin.startsWith("-")) {
                            ghin = ghin.substring(1);
                        }

                        if (lname.endsWith("-")) {
                            lname = lname.substring(0, lname.length() - 1);
                        }

                        while (ghin.length() < 7) {
                            ghin = "0" + ghin;
                        }

                        pass = lname;

                        while (pass.length() < 4) {
                            pass += "1";
                        }

                        if (ghin.equals("")) {

                            user = fname.substring(0,1) + lname;
                            user = user.toLowerCase();
                            mNum = "";

                        } else {

                            user = ghin;
                            mNum = ghin;
                        }

                        try {

                          pstmtx = con2.prepareStatement("SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ''");
                          pstmtx.clearParameters();
                          pstmtx.setString(1, lname);
                          pstmtx.setString(2, fname);

                          rsx2 = pstmtx.executeQuery();

                          if (rsx2.next()) {
                              // skip
                          } else {

                              pstmt2 = con2.prepareStatement (
                                 "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                                 "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                                 "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, webid, " +
                                 "email_bounced, email2_bounced, gender, pri_indicator) " +
                                 "VALUES (?,?,?,?,?,?,?,?,0,?,?,?,'',?,?,?,'',?,?,?,'',?,?,?,'','',?,0,0,?,?)");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setString(1, ghin);        // put the parm in stmt
                              pstmt2.setString(2, pass);
                              pstmt2.setString(3, lname);
                              pstmt2.setString(4, fname);
                              pstmt2.setString(5, "");
                              pstmt2.setString(6, "Amateur");
                              pstmt2.setString(7, "Adult");
                              pstmt2.setString(8, "");
                              pstmt2.setFloat(9, 0);
                              pstmt2.setFloat(10, 0);
                              pstmt2.setString(11, "");
                              pstmt2.setInt(12, 0);
                              pstmt2.setString(13, mNum);
                              pstmt2.setString(14, ghin);
                              pstmt2.setString(15, "");
                              pstmt2.setInt(16, 0);
                              pstmt2.setString(17, "1");
                              pstmt2.setString(18, "");
                              pstmt2.setString(19, "");
                              pstmt2.setString(20, "");
                              pstmt2.setString(21, "");
                              pstmt2.setString(22, "");
                              pstmt2.setInt(23, 0);
                              pstmt2.executeUpdate();          // execute the prepared stmt

                              pstmt2.close();              // close the stmt
                          }

                          pstmtx.close();

                        } catch (Exception exc2) {

                        }
                    }

                    rsx.close();

                } catch (Exception exc) {
                    out.println("<br>Error processing names for CWCPGA: " + exc.getMessage());
                }



        // ADD locations FIELD TO mem_notice TABLE
        stmt2.executeUpdate("ALTER TABLE mem_notice ADD activity_id INT NOT NULL default 0 AFTER mem_notice_id;");
        stmt2.executeUpdate("ALTER TABLE mem_notice ADD locations TEXT NOT NULL;");
        // decided not to actually enforce this unique key
        //stmt2.executeUpdate("ALTER TABLE mem_notice ADD UNIQUE KEY `key1` (`activity_id`,`name`);");


        // ADD event_log TABLE
        stmt2.executeUpdate(
                "CREATE TABLE IF NOT EXISTS event_log (" +
                   "event_log_id int(11) NOT NULL auto_increment," +
                   "event_id int(11) NOT NULL default '0'," +
                   "event_signup_id int(11) NOT NULL default '0'," +
                   "date_time datetime NOT NULL default '0000-00-00 00:00:00'," +
                   "user varchar(15) NOT NULL default ''," +
                   "action varchar(16) NOT NULL default ''," +
                   "detail varchar(255) NOT NULL default ''," +
                   "entry_timestamp timestamp NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP," +
                   "PRIMARY KEY  (event_log_id)" +
                ") ENGINE=MyISAM;");

             */

            
            /*
             * Scramble passwords
             *
             *
               String message = "";     // init message field
               String pw = "";          // init password
               String pwChar = "";

               String [] pw_table = { "q", "w", "e", "6", "9", "2", "3", "5", "7", "a", "s", "d", "f", "g", "h",
                                      "j", "k", "8", "z", "x", "c", "v", "b", "n", "m", "r", "t", "y", "u", "p" };

               //
               //  Build a randomly generated password to use for the admin and proshop users (6 chars long)
               //
               for (int j=0; j<6; j++) {
                  double x = Math.random() * 30;       // get a number between 0 and 29
                  int y = (int) x;                     // create an int
                  pwChar = pw_table[y];                // get a character
                  pw = pw + pwChar;                    // biuld pw
               }

               PreparedStatement pstmtx = null;
               Statement stmtx = null;
               ResultSet rsx = null;

               String tempusername = "";

               try {

                   stmtx = con2.createStatement();

                   rsx = stmtx.executeQuery("SELECT username FROM member2b");

                   while (rsx.next()) {

                       pw = "";

                       for (int j=0; j<6; j++) {
                          double x = Math.random() * 30;       // get a number between 0 and 29
                          int y = (int) x;                     // create an int
                          pwChar = pw_table[y];                // get a character
                          pw = pw + pwChar;                    // biuld pw
                       }
                       tempusername = rsx.getString("username");

                       pstmtx = con2.prepareStatement("UPDATE member2b SET password = ? WHERE username = ?");

                       pstmtx.clearParameters();
                       pstmtx.setString(1, pw);
                       pstmtx.setString(2, tempusername);

                       pstmtx.executeUpdate();

                       pstmtx.close();

                   }

                   stmtx.close();

               } catch (Exception exc) {
                   out.println("<br>Error encountered updating password for " + tempusername);
               }




                Statement stmtx = null;
                PreparedStatement pstmtx = null;
                ResultSet rsx = null;

                String fname = "";
                String lname = "";
                String mi = "";
                String player = "";
                String username = "";
                String fname_curr = "";
                String lname_curr = "";
                String mi_curr = "";
                String player_curr = "";
                String username_curr = "";

                int rounds = 0;

                boolean first = true;

                try {

                    stmtx = con2.createStatement();
                    int playerCount = 0;
                    int date_curr = 0;
                    String event_curr = "";

                    rsx = stmtx.executeQuery(
                            "SELECT m.name_last, m.name_first, m.name_mi, username, player1 AS player, userg1 AS userg FROM teepast2 t LEFT OUTER JOIN member2b m ON t.userg1 = m.username WHERE gtype1<>'' AND DATE LIKE '2010%' AND userg1<>'' " +
                            "UNION ALL SELECT m.name_last, m.name_first, m.name_mi, username, player2 AS player, userg2 AS userg FROM teepast2 t LEFT OUTER JOIN member2b m ON t.userg2 = m.username WHERE gtype2<>'' AND DATE LIKE '2010%' AND userg2<>'' " +
                            "UNION ALL SELECT m.name_last, m.name_first, m.name_mi, username, player3 AS player, userg3 AS userg FROM teepast2 t LEFT OUTER JOIN member2b m ON t.userg3 = m.username WHERE gtype3<>'' AND DATE LIKE '2010%' AND userg3<>'' " +
                            "UNION ALL SELECT m.name_last, m.name_first, m.name_mi, username, player4 AS player, userg4 AS userg FROM teepast2 t LEFT OUTER JOIN member2b m ON t.userg4 = m.username WHERE gtype4<>'' AND DATE LIKE '2010%' AND userg4<>'' " +
                            "UNION ALL SELECT m.name_last, m.name_first, m.name_mi, username, player5 AS player, userg5 AS userg FROM teepast2 t LEFT OUTER JOIN member2b m ON t.userg5 = m.username WHERE gtype5<>'' AND DATE LIKE '2010%' AND userg5<>'' " +
                            "ORDER BY name_last, name_first, name_mi, userg, player;");

                    while (rsx.next()) {

                        fname = "";
                        lname = "";
                        mi = "";
                        username = "";
                        player = "";

                        if (rsx.getString("name_last") != null) {
                            lname = rsx.getString("name_last");
                        } else {
                            lname = "";
                        }

                        if (rsx.getString("name_first") != null) {
                            fname = rsx.getString("name_first");
                        } else {
                            fname = "";
                        }

                        if (rsx.getString("name_mi") != null) {
                            mi = rsx.getString("name_mi");
                        } else {
                            mi = "";
                        }

                        if (rsx.getString("username") != null) {
                            username = rsx.getString("username");
                        } else {
                            username = "";
                        }

                        if (rsx.getString("player") != null) {
                            player = rsx.getString("player");
                        } else {
                            player = "";
                        }

                        if (lname.equalsIgnoreCase(lname_curr) && fname.equalsIgnoreCase(fname_curr) && mi.equalsIgnoreCase(mi_curr) && username.equalsIgnoreCase(username_curr) && player.equalsIgnoreCase(player_curr)) {
                            rounds++;
                        } else {
                            if (first) {
                                first = false;
                                out.println("Last Name|First Name|MI|Username|Guest Name|Rounds Played");
                            } else {
                                out.println("<br>" + lname_curr + "|" + fname_curr + "|" + mi_curr + "|" + username_curr + "|" + player_curr + "|" + rounds);

                                lname_curr = lname;
                                fname_curr = fname;
                                mi_curr = mi;
                                username_curr = username;
                                player_curr = player;
                                rounds = 1;
                            }
                        }

                    }

                    stmtx.close();

                } catch (Exception exc) {
                    out.println("error encountered for " + club + ": " + exc.getMessage());
                }





            // ADD NEW IBS POS FIELDS TO CLUB5
            stmt2.executeUpdate("" +
                "ALTER TABLE club5 " +
                    "ADD pos_ws_url varchar(128) NOT NULL default '', " +
                    "ADD pos_ws_user varchar(32) NOT NULL default '', " +
                    "ADD pos_ws_pass varchar(32) NOT NULL default '', " +
                    "ADD ibs_uidDeptID varchar(50) NOT NULL default '', " +
                    "ADD ibs_uidTenderID varchar(50) NOT NULL default '', " +
                    "ADD ibs_uidInvMenuID varchar(50) NOT NULL default '', " +
                    "ADD ibs_uidTaxID varchar(50) NOT NULL default '';");




 * /////// Start code for generating past tee times from tee time history


                PreparedStatement pstmtx = null;
                PreparedStatement pstmtx2 = null;
                PreparedStatement pstmtx3 = null;
                ResultSet rsx = null;
                ResultSet rsx2 = null;
                ResultSet rsx3 = null;
                int date = 0;
                int time = 0;
                int mm = 0;
                int yy = 0;
                int dd = 0;
                int hr = 0;
                int min = 0;
                int temp = 0;
                int ind = -1;
                short fb = 0;
                String fname = "";
                String lname = "";
                String mi = "";
                String modUser = "";
                String day = "";

                String [] gtypeA = new String[5];
                String [] playerA = new String[5];
                String [] mshipA = new String[5];
                String [] mtypeA = new String[5];
                String [] userA = new String[5];
                String [] mNumA = new String[5];
                String [] wcA = new String[5];
                short [] showA = new short[5];
                ArrayList<String> gtypes = new ArrayList<String>();

                try {
                    rsx = stmt2.executeQuery("SELECT guest FROM guest5");

                    while (rsx.next()) {
                        gtypes.add(rsx.getString("guest"));
                    }

                    rsx.close();

                } catch (Exception exc) {
                    out.println("<br>Error gathering guest types");
                }

                try {

                    // get a list of all teehist entries for the Shore course, group by date, time, fb, so that we only get one entry per tee time
                    rsx = stmt2.executeQuery("SELECT * FROM teehist WHERE courseName = 'Shore' AND date < '20101130' GROUP BY DATE, TIME, fb ORDER BY DATE, time");

                    while (rsx.next()) {

                        for (int x=0; x<5; x++) {

                            gtypeA[x] = "";
                            playerA[x] = "";
                            mshipA[x] = "";
                            mtypeA[x] = "";
                            userA[x] = "";
                            mNumA[x] = "";
                            wcA[x] = "";
                            showA[x] = 0;
                        }


                        date = rsx.getInt("date");
                        time = rsx.getInt("time");
                        fb = rsx.getShort("fb");
                        day = rsx.getString("day");
                        modUser = rsx.getString("user");

                        yy = date / 10000;
                        temp = yy * 10000;
                        mm = date - temp;
                        temp = mm / 100;
                        temp = temp * 100;
                        dd = mm - temp;
                        mm = mm / 100;

                        hr = time / 100;
                        min = time - (hr * 100);

                        try {

                            pstmtx = con2.prepareStatement("SELECT teepast_id FROM teepast2 WHERE date = ? AND time = ? AND fb = ? AND courseName = 'Shore'");
                            pstmtx.clearParameters();
                            pstmtx.setInt(1, date);
                            pstmtx.setInt(2, time);
                            pstmtx.setShort(3, fb);

                            rsx2 = pstmtx.executeQuery();

                            if (rsx2.next()) {
                                // just ignore this one if past tee time was found
                            } else {

                                // if no past tee time was found, we need to build a past tee time out of the teehist entry as best as possible.
                                playerA[0] = rsx.getString("player1");
                                playerA[1] = rsx.getString("player2");
                                playerA[2] = rsx.getString("player3");
                                playerA[3] = rsx.getString("player4");
                                playerA[4] = rsx.getString("player5");

                                for (int j=0; j < 5; j++) {
                                    if (!playerA[j].equals("")) {
                                        showA[j] = 1;
                                    } else {
                                        showA[j] = 0;
                                    }
                                }

                                modUser = rsx.getString("user");

                                for (int j=0; j < 5; j++) {

                                    if (!playerA[j].equals("") && !playerA[j].equalsIgnoreCase("x")) {

                                        for (int k=0; k < gtypes.size(); k++) {

                                            if (playerA[j].startsWith(gtypes.get(k))) {
                                                gtypeA[j] = gtypes.get(k);
                                                wcA[j] = "PO";
                                                break;
                                            }
                                        }

                                        // if gtype value is blank, the player is most likely a member
                                        if (gtypeA[j].equals("")) {

                                            StringTokenizer tokx = new StringTokenizer(playerA[j], " ");

                                            if (tokx.countTokens() == 2) {
                                                fname = tokx.nextToken();
                                                mi = "";
                                                lname = tokx.nextToken();
                                            } else if (tokx.countTokens() > 2) {
                                                fname = tokx.nextToken();
                                                mi = tokx.nextToken();
                                                lname = tokx.nextToken();
                                            } else {
                                                fname = "";
                                                mi = "";
                                                lname = "";
                                            }

                                            try {

                                                pstmtx2 = con2.prepareStatement("SELECT username, m_ship, m_type, memNum, wc FROM member2b WHERE name_first = ? AND name_mi = ? AND name_last = ?");
                                                pstmtx2.clearParameters();
                                                pstmtx2.setString(1, fname);
                                                pstmtx2.setString(2, mi);
                                                pstmtx2.setString(3, lname);

                                                rsx3 = pstmtx2.executeQuery();

                                                if (rsx3.next()) {

                                                    userA[j] = rsx3.getString("username");
                                                    mshipA[j] = rsx3.getString("m_ship");
                                                    mtypeA[j] = rsx3.getString("m_type");
                                                    mNumA[j] = rsx3.getString("memNum");
                                                    wcA[j] = rsx3.getString("wc");

                                                }

                                                pstmtx2.close();

                                            } catch (Exception exc) {
                                                out.println("<br>Error looking up member info for " + fname + " " + (!mi.equals("") ? mi + " " : "") + lname + " Err: " + exc.getMessage());
                                            }
                                        }

                                    }
                                }

                               pstmtx2 = con2.prepareStatement (
                                  "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                                  "restriction, rest_color, player1, player2, player3, player4, username1, " +
                                  "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
                                  "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
                                  "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
                                  "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id, " +
                                  "custom_string, custom_int, pos1, pos2, pos3, pos4, pos5," +
                                  "mship1, mship2, mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, " +
                                  "gtype1, gtype2, gtype3, gtype4, gtype5, " +
                                  "grev1, grev2, grev3, grev4, grev5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5) " +
                                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                  "?, ?, ?, ?, ?, ?, ?, " +
                                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                               pstmtx2.clearParameters();        // clear the parms
                               pstmtx2.setLong(1, date);         // put the parms in pstmtx2 for tee slot
                               pstmtx2.setLong(2, mm);
                               pstmtx2.setLong(3, dd);
                               pstmtx2.setLong(4, yy);
                               pstmtx2.setString(5, "");
                               pstmtx2.setInt(6, hr);
                               pstmtx2.setInt(7, min);
                               pstmtx2.setInt(8, time);
                               pstmtx2.setString(9, "");
                               pstmtx2.setString(10, "");
                               pstmtx2.setString(11, "");
                               pstmtx2.setString(12, "");
                               pstmtx2.setString(13, playerA[0]);
                               pstmtx2.setString(14, playerA[1]);
                               pstmtx2.setString(15, playerA[2]);
                               pstmtx2.setString(16, playerA[3]);
                               pstmtx2.setString(17, userA[0]);
                               pstmtx2.setString(18, userA[1]);
                               pstmtx2.setString(19, userA[2]);
                               pstmtx2.setString(20, userA[3]);
                               pstmtx2.setString(21, wcA[0]);
                               pstmtx2.setString(22, wcA[1]);
                               pstmtx2.setString(23, wcA[2]);
                               pstmtx2.setString(24, wcA[3]);
                               pstmtx2.setShort(25, showA[0]);
                               pstmtx2.setShort(26, showA[1]);
                               pstmtx2.setShort(27, showA[2]);
                               pstmtx2.setShort(28, showA[3]);
                               pstmtx2.setShort(29, fb);
                               pstmtx2.setString(30, playerA[4]);
                               pstmtx2.setString(31, userA[4]);
                               pstmtx2.setString(32, wcA[4]);
                               pstmtx2.setShort(33, showA[4]);
                               pstmtx2.setString(34, "Shore");
                               pstmtx2.setInt(35, 0);
                               pstmtx2.setInt(36, 0);
                               pstmtx2.setInt(37, 0);
                               pstmtx2.setInt(38, 0);
                               pstmtx2.setString(39, mNumA[0]);
                               pstmtx2.setString(40, mNumA[1]);
                               pstmtx2.setString(41, mNumA[2]);
                               pstmtx2.setString(42, mNumA[3]);
                               pstmtx2.setString(43, mNumA[4]);
                               pstmtx2.setString(44, "");
                               pstmtx2.setString(45, "");
                               pstmtx2.setString(46, "");
                               pstmtx2.setString(47, "");
                               pstmtx2.setString(48, "");
                               pstmtx2.setInt(49, 0);
                               pstmtx2.setInt(50, 0);
                               pstmtx2.setString(51, modUser);
                               pstmtx2.setString(52, "");
                               pstmtx2.setString(53, "");
                               pstmtx2.setInt(54, 0);
                               pstmtx2.setInt(55, 0);
                               pstmtx2.setInt(56, 0);
                               pstmtx2.setInt(57, 0);
                               pstmtx2.setInt(58, 0);
                               pstmtx2.setInt(59, ind);
                               pstmtx2.setInt(60, 0);
                               pstmtx2.setString(61, "");
                               pstmtx2.setInt(62, 0);
                               pstmtx2.setInt(63, 0);
                               pstmtx2.setInt(64, 0);
                               pstmtx2.setInt(65, 0);
                               pstmtx2.setInt(66, 0);
                               pstmtx2.setInt(67, 0);
                               pstmtx2.setString(68, mshipA[0]);
                               pstmtx2.setString(69, mshipA[1]);
                               pstmtx2.setString(70, mshipA[2]);
                               pstmtx2.setString(71, mshipA[3]);
                               pstmtx2.setString(72, mshipA[4]);
                               pstmtx2.setString(73, mtypeA[0]);
                               pstmtx2.setString(74, mtypeA[1]);
                               pstmtx2.setString(75, mtypeA[2]);
                               pstmtx2.setString(76, mtypeA[3]);
                               pstmtx2.setString(77, mtypeA[4]);
                               pstmtx2.setString(78, gtypeA[0]);
                               pstmtx2.setString(79, gtypeA[1]);
                               pstmtx2.setString(80, gtypeA[2]);
                               pstmtx2.setString(81, gtypeA[3]);
                               pstmtx2.setString(82, gtypeA[4]);
                               pstmtx2.setInt(83, 0);
                               pstmtx2.setInt(84, 0);
                               pstmtx2.setInt(85, 0);
                               pstmtx2.setInt(86, 0);
                               pstmtx2.setInt(87, 0);
                               pstmtx2.setInt(88, 0);
                               pstmtx2.setInt(89, 0);
                               pstmtx2.setInt(90, 0);
                               pstmtx2.setInt(91, 0);
                               pstmtx2.setInt(92, 0);

                               pstmtx2.executeUpdate();        // move the tee slot to teepast

                               pstmtx2.close();
                            }

                            pstmtx.close();

                        } catch (Exception exc) {
                            out.println("<br>Error looking up teepast entry Err: " + exc.getMessage());
                        }

                        ind--;
                    }

                    rsx.close();

                } catch (Exception exc) {
                    out.println("<br>Error updating club: " + club);
                }
 ///////  End code for generating past tee times from tee time history

            try {

                stmt2.executeUpdate("ALTER TABLE lreqs3 " +
                        "ADD weight1 smallint NOT NULL default 0," +
                        "ADD weight2 smallint NOT NULL default 0," +
                        "ADD weight3 smallint NOT NULL default 0," +
                        "ADD weight4 smallint NOT NULL default 0," +
                        "ADD weight5 smallint NOT NULL default 0," +
                        "ADD weight6 smallint NOT NULL default 0," +
                        "ADD weight7 smallint NOT NULL default 0," +
                        "ADD weight8 smallint NOT NULL default 0," +
                        "ADD weight9 smallint NOT NULL default 0," +
                        "ADD weight10 smallint NOT NULL default 0," +
                        "ADD weight11 smallint NOT NULL default 0," +
                        "ADD weight12 smallint NOT NULL default 0," +
                        "ADD weight13 smallint NOT NULL default 0," +
                        "ADD weight14 smallint NOT NULL default 0," +
                        "ADD weight15 smallint NOT NULL default 0," +
                        "ADD weight16 smallint NOT NULL default 0," +
                        "ADD weight17 smallint NOT NULL default 0," +
                        "ADD weight18 smallint NOT NULL default 0," +
                        "ADD weight19 smallint NOT NULL default 0," +
                        "ADD weight20 smallint NOT NULL default 0," +
                        "ADD weight21 smallint NOT NULL default 0," +
                        "ADD weight22 smallint NOT NULL default 0," +
                        "ADD weight23 smallint NOT NULL default 0," +
                        "ADD weight24 smallint NOT NULL default 0," +
                        "ADD weight25 smallint NOT NULL default 0;");

                stmt2.executeUpdate("ALTER TABLE lassigns5 " +
                        "ADD time_req int NOT NULL default 0," +
                        "ADD course_req varchar(30) NOT NULL default ''," +
                        "ADD time_assign int NOT NULL default 0," +
                        "ADD course_assign varchar(30) NOT NULL default ''," +
                        "ADD weight smallint NOT NULL default 0," +
                        "ADD grp_weight int NOT NULL default 0," +
                        "ADD lreq_id int NOT NULL default 0;");

            } catch (Exception ignore) { out.println("<br>" + club + " Failed 1."); }


     time_req (int, default to zero)
     course_req (String - varchar(30))
     time_assign (int, default to zero)
     course_assign (String - varchar(30))
     weight (smallint, default to zero)
     grp_weight (int, default to zero)
     lreq_id (bigint, default to zero)

                    stmtx = con2.createStatement();

                    rsx = stmtx.executeQuery("SELECT username, m_ship FROM member2b WHERE (m_type LIKE 'Primary%' OR m_type LIKE 'Spouse%') AND m_ship<>'Golf'");

                    while (rsx.next()) {

                        username = rsx.getString("username");
                        m_ship = rsx.getString("m_ship");

                        pstmtx = con2.prepareStatement("UPDATE teepast2 SET mship1 = ? WHERE date >= '20101105' and username1 = ?");
                        pstmtx.clearParameters();
                        pstmtx.setString(1, m_ship);
                        pstmtx.setString(2, username);

                        pstmtx.executeUpdate();

                        pstmtx.close();

                        pstmtx = con2.prepareStatement("UPDATE teepast2 SET mship2 = ? WHERE date >= '20101105' and username2 = ?");
                        pstmtx.clearParameters();
                        pstmtx.setString(1, m_ship);
                        pstmtx.setString(2, username);

                        pstmtx.executeUpdate();

                        pstmtx.close();

                        pstmtx = con2.prepareStatement("UPDATE teepast2 SET mship3 = ? WHERE date >= '20101105' and username3 = ?");
                        pstmtx.clearParameters();
                        pstmtx.setString(1, m_ship);
                        pstmtx.setString(2, username);

                        pstmtx.executeUpdate();

                        pstmtx.close();

                        pstmtx = con2.prepareStatement("UPDATE teepast2 SET mship4 = ? WHERE date >= '20101105' and username4 = ?");
                        pstmtx.clearParameters();
                        pstmtx.setString(1, m_ship);
                        pstmtx.setString(2, username);

                        pstmtx.executeUpdate();

                        pstmtx.close();

                        pstmtx = con2.prepareStatement("UPDATE teepast2 SET mship5 = ? WHERE date >= '20101105' and username5 = ?");
                        pstmtx.clearParameters();
                        pstmtx.setString(1, m_ship);
                        pstmtx.setString(2, username);

                        pstmtx.executeUpdate();

                        pstmtx.close();

                    }

                    stmtx.close();

            // ADD NEW PERMISSON TO THE LOGIN2 TABLE
            //stmt2.executeUpdate("ALTER TABLE login2 ADD SYSCONFIG_MANAGECONTENT tinyint(4) NOT NULL default 1 AFTER SYSCONFIG_MEMBERNOTICES;");


            // CREATE NEW email_content TABLE
            stmt2.executeUpdate("CREATE TABLE IF NOT EXISTS email_content(" +
                                      "id int(11) NOT NULL auto_increment," +
                                      "activity_id int(11) NOT NULL default '0'," +
                                      "location_id tinyint(4) NOT NULL default '0'," +
                                      "content_type tinyint(4) NOT NULL default '0'," +
                                      "enabled tinyint(4) NOT NULL default '1'," +
                                      "name varchar(32) NOT NULL default ''," +
                                      "start_datetime datetime NOT NULL default '0000-00-00 00:00:00'," +
                                      "end_datetime datetime NOT NULL default '0000-00-00 00:00:00'," +
                                      "time_mode tinyint(4) NOT NULL default '0'," +
                                      "reservation tinyint(4) NOT NULL default '0'," +
                                      "event_signup tinyint(4) NOT NULL default '0'," +
                                      "lesson_signup tinyint(4) NOT NULL default '0'," +
                                      "lottery_signup tinyint(4) NOT NULL default '0'," +
                                      "wait_list_signup tinyint(4) NOT NULL default '0'," +
                                      "only_if_guests tinyint(4) NOT NULL default '0'," +
                                      "email_tool_pro tinyint(4) NOT NULL default '0'," +
                                      "email_tool_mem tinyint(4) NOT NULL default '0'," +
                                      "content text NOT NULL," +
                                      "PRIMARY KEY  (`id`))");
            
            // ADD locations FIELD TO rest_suspend TABLE
            //stmt2.executeUpdate("ALTER TABLE rest_suspend ADD locations TEXT NOT NULL;");


            // RESET MOBILE MESSAGE INDICATOR IN MEMBER2B FOR ALL CLUBS
            //stmt2.executeUpdate("UPDATE member2b SET message = 'msg001' WHERE message = 'msg002' and mobile_count = 0");
  
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

            // ADD SALES TAX COLUMNS TO NESSESARY TABLES
            stmt2.executeUpdate("ALTER TABLE guest5 ADD salestax double(5,3) NOT NULL default '0';");

            stmt2.executeUpdate("ALTER TABLE mship5 ADD salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE mship5 ADD 9as18 tinyint NOT NULL default '1';");

            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode1_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode2_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode3_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode4_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode5_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode6_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode7_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode8_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode9_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode10_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode11_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode12_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode13_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode14_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode15_salestax double(5,3) NOT NULL default '0';");
            stmt2.executeUpdate("ALTER TABLE clubparm2 ADD tmode16_salestax double(5,3) NOT NULL default '0';");
 

            // add peer_review field to club5 table
            if (!club.equals("demov4")) stmt2.executeUpdate("ALTER TABLE club5 ADD peer_review tinyint(4) NOT NULL default '1'");


            // CREATE NEW staff_list TABLE AND POPULATE IT WITH THE DEFAULTS

            stmt2.executeUpdate("CREATE TABLE staff_list ( " +
                                    "staff_id int(11) NOT NULL auto_increment, " +
                                    "activity_id int(11) NOT NULL default '0', " +
                                    "name varchar(40) NOT NULL default '', " +
                                    "short_name varchar(12) NOT NULL default '', " +
                                    "title varchar(40) NOT NULL default '', " +
                                    "address1 varchar(50) NOT NULL default '', " +
                                    "address2 varchar(50) NOT NULL default '', " +
                                    "email_bounced1 tinyint(4) NOT NULL default '0', " +
                                    "email_bounced2 tinyint(4) NOT NULL default '0', " +
                                    "receive_backups1 tinyint(4) NOT NULL default '0', " +
                                    "receive_news1 tinyint(4) NOT NULL default '0', " +
                                    "cc_on_emails1 tinyint(4) NOT NULL default '0', " +
                                    "receive_backups2 tinyint(4) NOT NULL default '0', " +
                                    "receive_news2 tinyint(4) NOT NULL default '0', " +
                                    "cc_on_emails2 tinyint(4) NOT NULL default '0', " +
                                    "tee_time_list tinyint(4) NOT NULL default '0', " +
                                    "PRIMARY KEY  (staff_id) " +
                               ") ENGINE=MyISAM DEFAULT CHARSET=latin1;");


            // from club5 copy contact, email, emailOpt
            ResultSet rs4 = stmt3.executeQuery("SELECT contact, email, emailOpt FROM club5");

            if (rs4.next()) {

                stmt2.executeUpdate("INSERT INTO staff_list (name, address1, receive_backups1) VALUES (\"" + rs4.getString(1) + "\", '" + rs4.getString(2) + "', '" + rs4.getInt(3) + "')");

            }


            // from backup_emails copy address to address1 and set receive_backups1 to 1
            rs4 = stmt3.executeQuery("SELECT address FROM backup_emails");

            while (rs4.next()) {

                stmt2.executeUpdate("INSERT INTO staff_list (address1, receive_backups1) VALUES ('" + rs4.getString(1) + "', '1')");

            }

 


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

   out.println("<BR><BR>Upgrade Finished!  The upgrade is complete for all clubs.");
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
        rs1 = stmt1.executeQuery("SELECT clubname FROM v5.clubs WHERE inactive = 0 ORDER BY clubname");

        while (rs1.next()) {

            club = rs1.getString(1);                // get a club name
            con2 = dbConn.Connect(club);            // get a connection to this club's db
            stmt2 = con2.createStatement();         // create a statement
            i = 0;
            found = false;
            int x = 0;
            int prob = 0;
            int count = 0;
            int count2 = 0;
            int count3 = 0;


            // FIND CLUBS WITH IBS INTERFACE
            rs2 = stmt2.executeQuery("SELECT * FROM club5 WHERE posType = 'IBS'");

            while ( rs2.next() ) {

                out.println("<p>" + rs2.getString("clubName") + " (" + club + ") &nbsp;&nbsp;" + rs2.getString("pos_ws_url"));
                tmp_total++;
            }


/*

            // COUNT ALL MEMBERS
            rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM member2b");

            if ( rs2.next() ) {
                count = rs2.getInt(1);
            }

            // COUNT ALL INACTIVE MEMBERS
            rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM member2b WHERE inact = 1");

            if ( rs2.next() ) {
                count2 = rs2.getInt(1);
            }

            // COUNT ALL INACTIVE MEMBERS
            rs2 = stmt2.executeQuery("SELECT COUNT(*) FROM member2b WHERE inact = 1 AND last_sync_date < '2012-11-15'");

            if ( rs2.next() ) {
                count3 = rs2.getInt(1);
            }

            out.println("<br>Club: " + club + "&nbsp;&nbsp;" + count + " member total, " + count2 + " member inactive, " + count3 + " since before '2012-11-15'<br>");
*/



/*
            // FIND FLXREZ CLUBS
            rs2 = stmt2.executeQuery("SELECT clubName, foretees_mode, genrez_mode FROM club5");

            if ( rs2.next() ) {

                if (rs2.getInt("genrez_mode") == 1) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u> configured to use FlxRez</font></b>");

                    found = true;

                }

                if (found) {

                    rs2 = stmt2.executeQuery("SELECT * FROM activities");

                    while ( rs2.next() ) {

                        count++;
                        if (rs2.getString("allowable_views").startsWith("1")) {

                            out.println("<br>&nbsp;&nbsp;" + rs2.getString(3) + " summary as default");

                        }

                    }

                    out.println("<br>&nbsp;&nbsp;found " + count + " activities<br>");

                }

            }
*/




/*
            String [] gtypes = new String[99];
            String response = "";
            String problem = "";

            //out.println("<br><br>Starting " + club);

            
            
            // FIND CLUBS WITH PROBLEM FB SET FOR AN EVENT
            rs2 = stmt2.executeQuery("SELECT * FROM events2b WHERE fb <> 'Front' AND fb <> 'Back' AND fb <> 'Both'");
            
            while ( rs2.next() ) {

                if (!found) {
                    out.print("<br><br><b><font size=+1><u>" + club + "</u></font></b>");
                    found = true;
                }
                
                out.println("<br>&nbsp;" + rs2.getString("name") + "&nbsp;&nbsp;" + rs2.getString("fb"));
                
            }
*/
            
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
        out.println("Error performing test to club '" + club + "'.");
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
 
 
 private void doDiningUpdate(PrintWriter out) {
     

    Connection con_d = null;                  // init DB objects
    Connection con = null;
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;


    try {

        con_d = Connect.getDiningCon();

    } catch (Exception exc) {

        // Error connecting to db....
        out.println("<BR><BR>Unable to connect to the Dining DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/v5/servlet/Support_main\">Return</A>.");
        out.println("</BODY></HTML>");
        return;
    }



    // Insert job code !!!!!!!!!!!!!!!!!!!!!!!!!!!!

    try {




        // Add address entries for any people that don't have them

        int address_id = 0, person_id = 0;

        stmt = con_d.createStatement();
        rs = stmt.executeQuery("SELECT id FROM people WHERE address_id IS NULL");

        while (rs.next()) {

            person_id = rs.getInt(1);

            // add an address table entry for this person
            pstmt2 = con_d.prepareStatement("" +
                   "INSERT INTO addresses (" +
                       "created_at, updated_at, lock_version, street_address, city, state_id, zip_or_postal_code, country, imported" +
                   ") VALUES (" +
                       "now(), now(), 0, '', '', NULL, '', 'United States', FALSE" +
                   ") RETURNING id");

            pstmt2.clearParameters();
            rs2 = pstmt2.executeQuery();
            if (rs2.next()) address_id = rs2.getInt(1);

            // update the people table entry
            pstmt2 = con_d.prepareStatement("UPDATE people SET address_id = ? WHERE id = ?");
            pstmt2.clearParameters();
            pstmt2.setInt(1, address_id);
            pstmt2.setInt(2, person_id);
            pstmt2.executeUpdate();
            pstmt2.close();

        }


    } catch (Exception exc) {

        out.println("<BR>FATAL ERROR: " + exc.toString());

    }


    

    // Store old job code below here

/*






*/

    try {
        con_d.close();
    } catch (Exception ignore) {}

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
