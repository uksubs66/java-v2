/***************************************************************************************
 *   Class file:    stats
 *
 *
 *   Called by:     called directly
 *
 *
 *   Created:       4/17/08 by Brad
 *
 *
 *   Last Updated:  4/17/08
 *
 *     12/06/12  Changes for new server configurations and mysql 5.5
 *      3/20/11  Add @SuppressWarnings annotations to applicable methods
 *      9/01/10  Added ability to track hits to the external links from various partners
 *      6/04/09  Allow logins=0-3 for overall, today, YTD and added percentages
 *
 *                  
 *                  
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.sql.*;
import java.net.*;
import java.text.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.server.UID;



public class stats extends HttpServlet {

    String rev = SystemUtils.REVLEVEL; 
 
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        if (req.getParameter("countHit") != null && req.getParameter("provider") != null) {

            countHit(req.getParameter("provider"), out);

        } else if (req.getParameter("db") != null) {

            displayMasterDBStats(req, out);

        } else if (req.getParameter("dbs1") != null) {

            displaySlaveDBStats(out);

        } else if (req.getParameter("dbs2") != null) {

            displaySlaveDBStats2(out);

        } else if (req.getParameter("login") != null) {

            displayLoginStats(req, out);

        } else if (req.getParameter("email") != null) {

            displayBouncedCount(req, out);
            displayQueueStats(req, out);

        } else if (req.getParameter("login-graph") != null) {

            displayLoginHourGraph(req, out);

        } else if (req.getParameter("lb-full") != null) {

            displayFullLBstat(req, out);

        } else if (req.getParameter("lb-csv") != null) {

            displayLBcsv(req, out);

        } else if (req.getParameter("jvm-mem") != null) {
            
            if (req.getParameter("all") != null) {
                
                displayJVMmemAll(req, out);
            
            } else {
                
                displayJVMmem(req, out);
                
            }

        } else {

            GregorianCalendar cal = new GregorianCalendar();
            DateFormat df_date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
            String display_date = df_date.format(cal.getTime());

            out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
            out.println("<html>");
            out.println("<head>");
            out.println(" <meta http-equiv=\"refresh\" content=\"120\">");
            out.println(" <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
            out.println(" <meta http-equiv=\"Content-Language\" content=\"en-us\">");
            out.println("<title>Application Statistics</title>");
            out.println("</head>");
            out.println("<body bgcolor=white>");
            out.println("<font size=4><u>Application Statistics</u></font><br/>");

            out.println("<font size=2>Node " + Common_Server.SERVER_ID + " reporting<br/>" + display_date + "</font></p>");


            // Display Master Database Stats
            out.println("<font size=4><u>Master Database Statistics</u></font><br/>");
            displayMasterDBStats(req, out);
            out.println("<br/><br/>");

            // Display Slave Database Stats
            out.println("<font size=4><u>Slave Database Statistics</u></font><br/>");
            out.println("<br/><font size=3><u>dbm</u></font><br/>");
            displaySlaveDBStats(out);
            out.println("<br/><font size=3><u>dbs1</u></font><br/>");
            displaySlaveDBStats2(out);
            out.println("<br/><br/>");

            // Display Login Stats
            out.println("<font size=4><u>Login Statistics</u></font><br/>");
            displayLoginStats(req, out);
            out.println("<br/><br/>");

            // Display Bounced Count
            out.println("<font size=4><u>Emails</u></font><br/>");
            displayBouncedCount(req, out);
            displayQueueStats(req, out);
            out.println("<br/><br/>");

            displayLoginHourGraph(req, out);

            out.println("</body></html>");
        }

        out.close();
    }   // end of doGet method
       
    
    private void displayMasterDBStats(HttpServletRequest req, PrintWriter out){
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        int max_connections = 0;
        int threads_connected = 0;
        long questions = 0;
        long qcache_hits = 0;
        int uptime = 0;
        int key_reads = 0;
        long bytes_in = 0;
        double raw_bytes_out = 0;
        double bytes_out = 0;
        String size_in = "MB";
        String size_out = "MB";
        int com_insert = 0;
        int com_update_multi = 0;
        int com_update = 0;
        int com_delete = 0;
        double com_select = 0;
        
        String err = "";
        
        try{
            con = dbConn.Connect(rev);
            stmt = con.createStatement();

            String sql_fix = (true) ? "GLOBAL" : "";

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Max_used_connections'");      // Max_connections
            err = "max_connections";
            if (rs.next()) max_connections = rs.getInt(2);
            
            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Threads_connected'");         // Threads_connected
            err = "threads_connected";
            if (rs.next()) threads_connected = rs.getInt(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Questions'");                 // Questions
            err = "questions";
            if (rs.next()) questions = rs.getLong(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Uptime'");                    // Uptime
            err = "uptime";
            if (rs.next()) uptime = rs.getInt(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Bytes_received'");            // Bytes_received
            err = "bytes_in";
            if (rs.next()) bytes_in = rs.getLong(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Bytes_sent'");                // Bytes_sent
            err = "bytes_out";
            if (rs.next()) raw_bytes_out = rs.getDouble(2);
            
            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Com_insert'");                // Insert statements
            err = "com_insert";
            if (rs.next()) com_insert = rs.getInt(2);
            
            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Com_update_multi'");                // Insert statements
            err = "com_update_multi";
            if (rs.next()) com_update_multi = rs.getInt(2);
            
            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Com_update'");                // Insert statements
            err = "com_update";
            if (rs.next()) com_update = rs.getInt(2);
            
            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Com_delete'");                // Insert statements
            err = "com_delete";
            if (rs.next()) com_delete = rs.getInt(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Com_select'");                // Insert statements
            err = "com_select";
            if (rs.next()) com_select = rs.getDouble(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Qcache_hits'");                 // Qcache_hits
            err = "Qcache_hits";
            if (rs.next()) qcache_hits = rs.getLong(2);

            rs = stmt.executeQuery("SHOW " + sql_fix + " STATUS LIKE 'Key_reads'");                 // Qcache_hits
            err = "Key_reads";
            if (rs.next()) key_reads = rs.getInt(2);
            
            con.close();
        }
        catch(Exception exc){
            out.println("<BR/><BR/>Master Database Error!");
            out.println("<BR/>Exception: "+ err + " : " + exc.getMessage());
            return;
        }
        
        double qpers = (questions / uptime);
        
        bytes_in = bytes_in / 1024 / 1024;                // convert bytes_in to MB
        bytes_out = raw_bytes_out / 1024 / 1024;               // convert bytes_out to MB
        
        // if larger than 1000 MB, convert to GB
        if (bytes_in > 1024){
            bytes_in /= 1024;
            size_in = "GB";
        }
        if (bytes_out > 1024){
            bytes_out /= 1024;
            size_out = "GB";
        }

        int key_cache_miss_rate = key_reads / uptime;

        double com_writes = (com_insert + com_update + com_update_multi + com_delete);
        double com_other = questions - com_select - com_writes - qcache_hits;

        DecimalFormat df = new DecimalFormat( "#,###,###,##0" );
        DecimalFormat df2 = new DecimalFormat("#.##");

        double sel_percent = (com_select / questions) * 100;
        double qcache_hits_percent = (qcache_hits / questions) * 100;
        double total_sel_percent = ((com_select + qcache_hits) / questions) * 100;
        double write_percent = (com_writes / questions) * 100;
        double other_percent = (com_other / questions) * 100;

        double sel_percent2 = (com_select / (questions - com_other)) * 100;
        double write_percent2 = (com_writes / (questions - com_other)) * 100;

        double sel_disk_percent = (com_select / (com_select + qcache_hits)) * 100;
        double sel_cached_percent = (qcache_hits / (com_select + qcache_hits)) * 100;

        // print results to screen
        out.println("<font size=2>");
        out.println("Uptime: " + formatIntoDDHHMMSS(uptime) + "<br/>");
        out.println("Con: " + threads_connected + "/" + max_connections + "<br/>");
        out.println("Qsec: " + qpers + "<br/>");
        out.println("Data: " + (int)bytes_in + size_in + "/" + (int)bytes_out + size_out + "<br/>");
        out.println("Questions: " + df.format(questions) + "<br/>");
        out.println("Writes: " + df.format(com_writes) + " (" + df2.format(write_percent)  + "%) <br/>"); // (" + df2.format(write_percent2)  + "%)
        out.println("Selects: " + df.format(com_select + qcache_hits) + " (" + df2.format(total_sel_percent) + "%) <br/>"); // (" + df2.format(sel_percent2) + "%)
        out.println("Other: " + df.format(com_other) + " (" + df2.format(other_percent) + "%)<br/>");

        out.println("Disk Selects: " + df.format(com_select) + " (" + df2.format(sel_disk_percent) + "%)<br/>");
        out.println("Cached Selects: " + df.format(qcache_hits) + " (" + df2.format(sel_cached_percent) + "%)<br/>");
        out.println("Key Cache Miss Rate: " + df.format(key_cache_miss_rate) + "<br/>");

        //Calendar cal = Calendar.getInstance();
        //double current_time = cal.getTime();

        long unix_time = System.currentTimeMillis() / 1000L;

        HttpSession session = req.getSession(true);

        if (session.getAttribute("last_time") == null) {

            out.println("Initial Load - Saving Values...<br/>");
            
        } else {

            // read values if present
            
            String mark = "A";

            try {

                long long_last_time = (Long) session.getAttribute("last_time");

                long elasped_time = unix_time - long_last_time;

                long last_questions = (Long) session.getAttribute("last_questions");

                double last_bytes_out = (Double) session.getAttribute("last_bytes_out");

                double last_com_writes = (Double) session.getAttribute("last_com_writes");

                double last_com_select = (Double) session.getAttribute("last_com_select");

                long last_qcache_hits = (Long) session.getAttribute("last_qcache_hits");

                double last_com_other = (Double) session.getAttribute("last_com_other");

                int last_key_reads = (Integer) session.getAttribute("last_key_reads");


                String time = "";

                if (elasped_time > 60) {

                    int m = (int)elasped_time / 60;
                    int s = (int)elasped_time % 60;

                    time = m + " minutes " + s + " seconds";

                } else {

                    time = elasped_time + " seconds";

                }

                out.println("<br/>Elasped Time: " + time + "<br/>");

                out.println("Questions: " + df2.format((questions - last_questions) / elasped_time) + " per second<br/>");
                out.println("Writes: " + df2.format((com_writes - last_com_writes) / elasped_time) + " per second<br/>");
                out.println("Disk Selects: " + df2.format((com_select - last_com_select) / elasped_time) + " per second<br/>");
                out.println("Cached Selects: " + df2.format((qcache_hits - last_qcache_hits) / elasped_time) + " per second<br/>");
                out.println("Other: " + df2.format((com_other - last_com_other) / elasped_time) + " per second<br/>");
                out.println("Data Sent: " + df.format(((raw_bytes_out - last_bytes_out) / elasped_time) / 1024 / 1024) + " MB/sec<br/>");
                out.println("Key Cache Miss Rate: " + df.format((key_reads - last_key_reads) / elasped_time) + "<br/>");

            } catch (Exception exc) {
                out.println("Error: mark=" + mark + ", " + exc.toString() + ", " + exc.getMessage());
            }
        }

        out.println("</font>");


        // write new values
        session.setMaxInactiveInterval(60 * 60);    // one hour
        session.setAttribute("last_time", unix_time);
        session.setAttribute("last_questions", questions);
        session.setAttribute("last_com_writes", com_writes);
        session.setAttribute("last_com_select", com_select);
        session.setAttribute("last_qcache_hits", qcache_hits);
        session.setAttribute("last_com_other", com_other);
        session.setAttribute("last_bytes_out", raw_bytes_out);
        session.setAttribute("last_key_reads", key_reads);



    }   // end of displayMasterDBStats method


    private void displaySlaveDBStats(PrintWriter out) {
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String slave_IOstate = "";
        String slave_IOrunning = "";
        String slave_SQLrunning = "";
        int lastErrno = 0;
        String lastErr = "";
        int secsBehind = 0;
        
        String err = "";
        
        try{
            con = dbConn_slave.Connect(rev, 1);
            stmt = con.createStatement();
            
            rs = stmt.executeQuery("SHOW SLAVE STATUS");
            if (rs.next()) {
                slave_IOstate = rs.getString("Slave_IO_State");
                err = "slave_IOstate";
                slave_IOrunning = rs.getString("Slave_IO_Running");
                err = "slave_IOrunning";
                slave_SQLrunning = rs.getString("Slave_SQL_Running");
                err = "slave_SQLrunning";
                lastErrno = rs.getInt("Last_Errno");
                err = "lastErrno";
                lastErr = rs.getString("Last_Error");
                err = "lastErr";
                secsBehind = rs.getInt("Seconds_Behind_Master");
                err = "secsBehind";
            }
        }
        catch(Exception exc){
            out.println("<BR/><BR/>Slave Database Error!");
            out.println("<BR/>Exception: "+ err + " : " + exc.getMessage());
            return;            
        }

        out.println("<font size=2>");
        out.println("IO state: " + slave_IOstate + "<br/>");
        out.println("IO running: " + slave_IOrunning + "<br/>");
        out.println("SQL running: " + slave_SQLrunning + "<br/>");
        if (lastErrno != 0 || !lastErr.equals(""))
            out.println("Last Error: " + lastErr + " (" + lastErrno + ")<br/>");
        out.println("Seconds behind master: " + secsBehind);
        out.println("</font>");

    }   // end displaySlaveDBStatus method


    private void displaySlaveDBStats2(PrintWriter out) {

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        String slave_IOstate = "";
        String slave_IOrunning = "";
        String slave_SQLrunning = "";
        int lastErrno = 0;
        String lastErr = "";
        int secsBehind = 0;

        String err = "";

        // connect to second slave
        try{
            con = dbConn_slave.Connect(rev, 2);
            stmt = con.createStatement();

            rs = stmt.executeQuery("SHOW SLAVE STATUS");
            if (rs.next()){
                slave_IOstate = rs.getString("Slave_IO_State");
                err = "slave_IOstate";
                slave_IOrunning = rs.getString("Slave_IO_Running");
                err = "slave_IOrunning";
                slave_SQLrunning = rs.getString("Slave_SQL_Running");
                err = "slave_SQLrunning";
                lastErrno = rs.getInt("Last_Errno");
                err = "lastErrno";
                lastErr = rs.getString("Last_Error");
                err = "lastErr";
                secsBehind = rs.getInt("Seconds_Behind_Master");
                err = "secsBehind";
            }
        }
        catch(Exception exc){
            out.println("<BR/><BR/>Slave Database Error!");
            out.println("<BR/>Exception: "+ err + " : " + exc.getMessage());
            return;
        }

        out.println("<font size=2>");
        out.println("IO state: " + slave_IOstate + "<br/>");
        out.println("IO running: " + slave_IOrunning + "<br/>");
        out.println("SQL running: " + slave_SQLrunning + "<br/>");
        if (lastErrno != 0 || !lastErr.equals(""))
            out.println("Last Error: " + lastErr + " (" + lastErrno + ")<br/>");
        out.println("Seconds behind master: " + secsBehind);
        out.println("</font>");

    }   // end displaySlaveDBStatus method

    
    private void displayLoginStats(HttpServletRequest req, PrintWriter out) {

        Connection con = null;          
        Statement stmt = null;
        ResultSet rs = null;

        NumberFormat nf;
        nf = NumberFormat.getNumberInstance();

        int today = 0;
        int today_pro = 0;
        int today_mem = 0;
        int today_memL = 0;
        int today_memR = 0;
        double this_month = 0;
        double this_month_ly = 0;
        double last_month = 0;
        double this_year = 0;
        double last_year = 0;
        double last_year_todate = 0;
        double increase = 0;
        double increase_todate = 0;

        try {
            con = dbConn.Connect(rev);
            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE entry_date = now() AND (user_type_id = 3 OR user_type_id = 4)");
            if (rs.next()) today_pro = rs.getInt(1);

            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE entry_date = now() AND user_type_id = 1");
            if (rs.next()) today_memL = rs.getInt(1);

            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE entry_date = now() AND user_type_id = 2");
            if (rs.next()) today_memR = rs.getInt(1);

            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE MONTH(entry_date) = MONTH(now()) AND YEAR(entry_date) = YEAR(now())");
            if (rs.next()) this_month = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE MONTH(entry_Date) = MONTH(now()) AND YEAR(entry_date) = YEAR(DATE_ADD(now(), INTERVAL -1 YEAR)) AND DAYOFYEAR(entry_date) <= DAYOFYEAR(now())");
            if (rs.next()) this_month_ly = rs.getInt(1);

            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE MONTH(entry_date) = MONTH(DATE_ADD(now(), INTERVAL -1 MONTH)) AND YEAR(entry_date) = YEAR(DATE_ADD(now(), INTERVAL -1 MONTH))");
            if (rs.next()) last_month = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE YEAR(entry_date) = YEAR(now())");
            if (rs.next()) this_year = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE YEAR(entry_date) = YEAR(DATE_ADD(now(), INTERVAL -1 YEAR))");
            if (rs.next()) last_year = rs.getInt(1);
            
            rs = stmt.executeQuery("SELECT sum(login_count) AS today FROM login_stats WHERE DAYOFYEAR(entry_date) <= DAYOFYEAR(now()) AND YEAR(entry_date) = YEAR(DATE_ADD(now(), INTERVAL -1 YEAR))");
            if (rs.next()) last_year_todate = rs.getInt(1);
                        
            rs.close();
            con.close();
        }
        catch (Exception exc) {
            out.println("<p>Database Error!");
            out.println("<br>Exception: "+ exc.getMessage() + "</p>");
            return;
        }

        today_mem = today_memR + today_memL;
        today = today_pro + today_mem;
        DecimalFormat df = new DecimalFormat("#.##");
        increase = ((this_month - this_month_ly) / this_month_ly) * 100;
        increase_todate = ((this_year - last_year_todate) / last_year_todate) * 100;

        
        out.println("<font size=2>");
        out.println("<nobr>Today: " + nf.format(today) + " (" + nf.format(today_pro) + "P / " + nf.format(today_memL) + "L / " + nf.format(today_memR) + "R)</nobr><br/>");
        //if (req.getParameter("logins").equals("1")) return; // if logins=1 then just show todays logins
        out.println("This Month: " + nf.format(this_month) + "<br/>");
        out.println("Last Month: " + nf.format(last_month) + "<br/>");  
        out.println("Increase From LY (Cur Month): " + df.format(increase) + "%<br/>");
        out.println("This Year: " + nf.format(this_year) + "<br/>");
        out.println("Last Year (Total): " + nf.format(last_year) + "<br/>");
        out.println("Last Year (To Cur): " + nf.format(last_year_todate) + "<br/>");
        out.println("Increase From LY (To Cur): " + df.format(increase_todate) + "%<br/>");
        
        out.println("</font>");
    }  // end displayLoginStats method

    
    private void displayBouncedCount(HttpServletRequest req, PrintWriter out) {
        
        Connection con = null;           
        Statement stmt = null;
        ResultSet rs = null;

        int bounced = 0;
        
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://216.243.184.88/xmail_bounces", "xmail_filters", "xmfilmail");
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT COUNT(*) FROM bounced;");
            if (rs.next())
                bounced = rs.getInt(1);

            con.close();
        }
        catch(Exception exc){
            out.println("<BR><BR>Database Error!");
            out.println("<BR>Exception: Bounced - " + exc.getMessage());
            return;
        }
        
        out.println("<font size=2>Bounced Emails: " + bounced + "</font>");
    }   // end displayBouncedCount method
        
    
 private void displayLoginHourGraph(HttpServletRequest req, PrintWriter out) {
     
    Connection con = null;           
    Statement stmt = null;
    ResultSet rs = null;
    
    int [] memLogins = new int [24];     // one per hour of day     
    int [] proLogins = new int [24];   
    int [] combinedLogins = new int [24];
    int user_type = 0;
    int logins = 0;
    int hour = 0;
    int tmp_highest = 0;
    int line = 0;
    int tmp_total = 0;
    int year = 0;
    long total = 1;
    double tmp = 0;
    double pct = 0;
    String oldest_date= "";
    String tmp_hour = "";
    String sql = "";
    String period = (req.getParameter("logins") == null) ? "" : req.getParameter("logins").trim();
    String y = (req.getParameter("y") == null) ? "" : req.getParameter("y").trim();
    String year_clause = "";
    
    try { 
        year = Integer.parseInt(y); 
    } catch (Exception ignore) {}
    
    if (year > 2001 && year < 2020) { // little sanity check
        year_clause = "YEAR(entry_date) = '" + year + "'";
    } else {
        year_clause = "YEAR(entry_date) = YEAR(now())";
    }

    if (period == null) period = "overall";

    NumberFormat nf;
    nf = NumberFormat.getNumberInstance();

    try {
        
        con = dbConn.Connect("v5");
        stmt = con.createStatement();
        /*
        rs = stmt.executeQuery("SELECT entry_date FROM login_stats ORDER BY entry_date ASC LIMIT 1");
        if (rs.next()) oldest_date = rs.getString(1);
        
        out.println("<br><u>Logins Since " + oldest_date + "</u>");
        */
        if (period.equals("all")) {
            sql = "SELECT hour, SUM(login_count) AS logins FROM login_stats GROUP BY hour WITH ROLLUP";
        } else if (period.equals("year")) {
            sql = "SELECT hour, SUM(login_count) AS logins FROM login_stats WHERE " + year_clause + " GROUP BY hour WITH ROLLUP";
        } else {
            sql = "SELECT hour, SUM(login_count) AS logins FROM login_stats WHERE entry_date = DATE(now()) GROUP BY hour WITH ROLLUP";
        }
        
        out.println("<br>");
        rs = stmt.executeQuery(sql);
        
        rs.last();
        if (rs.getRow() != 1) total = rs.getLong("logins");
        rs.beforeFirst();


        
        while (rs.next()) {
            if (rs.getString(1) != null) {
                //user_type = rs.getInt("user_type_id");
                logins = rs.getInt("logins");
                hour = rs.getInt("hour");

                //if (user_type == 1 || user_type == 2) memLogins[hour] += logins;
                //if (user_type == 3 || user_type == 4) proLogins[hour] += logins;

                //combinedLogins[hour] = memLogins[hour] + proLogins[hour];
                combinedLogins[hour] = logins;
            }
        }

        for (int x=0;x<24;x++) {
            
            if (tmp_highest < combinedLogins[x]) tmp_highest = combinedLogins[x];
        }
        
        out.println("<u>Logins by Hour ");
    
        if (period.equals("all")) {
            out.println("Since June 22nd 2006");
        } else if (period.equals("year")) {
            out.println("for This Year");
        } else {
            out.println("for Today");
        }

        out.println("</u><br><table cellpadding=0 cellspacing=0>");
            
        for (int x=0;x<24;x++) {
            
            try {
                line = ((combinedLogins[x] / tmp_highest) * 100);
                tmp = ((double)combinedLogins[x] / (double)tmp_highest) * 150;
                if (tmp < 1 && tmp > 0) tmp = 1; // make sure any thing greater than zero rounds up to 1
                tmp_hour = ((x < 12) ? x + " AM" : (x - 12) + " PM");
                if (x == 0) tmp_hour = "12 AM";
                if (x == 12) tmp_hour = "12 PM";

                if ((int)tmp > 0) {
                    out.println("<tr><td align=right nowrap>" + tmp_hour + "</td>");
                    pct = (double)(combinedLogins[x] * 100) / total;
                    out.println("<td nowrap>&nbsp;<img src=/v5/images/black.gif height=7 width=\"" + tmp + "\" border=0> &nbsp;"+nf.format(combinedLogins[x])+" &nbsp;<font size=2>(" + nf.format(pct) + "%)</font></td>");
                    out.println("</tr>");
                }
            } catch(Exception exc) {
                out.println("<tr><td align=right nowrap>" + tmp_hour + "</td>");
                out.println("<td nowrap>&nbsp;<img src=/v5/images/black.gif height=7 width=0 border=0> &nbsp;"+nf.format(combinedLogins[x]) + "</td>");
                out.println("</tr>");
            }
        }
        
        out.println("</table>");
        
        con.close();
        
    } catch (Exception exc) {
      out.println("<BR><BR>Database Error!!");
      out.println("<BR>Exception: "+ exc.toString());
      return;
    }
    
 }


 @SuppressWarnings("deprecation")
 private void displayQueueStats(HttpServletRequest req, PrintWriter out) {

    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;

    out.println("<font size=2>");

    try {

        u = new URL("http", "mailstats.foretees.com", 80, "/xqm.php");

        is = u.openStream();         // throws an IOException
        dis = new DataInputStream(new BufferedInputStream(is));
        
        while ((s = dis.readLine()) != null) {
            out.println(s);
        }

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    } finally {

        try { is.close(); }
        catch (Exception ignore) {}

    }

    out.println("</font>");

 }


 @SuppressWarnings("deprecation")
 private void displayFullLBstat(HttpServletRequest req, PrintWriter out) {

    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;

    out.println("<font size=2>");

    try {

        u = new URL("http", "web.foretees.com", 80, "/lb?stats");

        is = u.openStream();         // throws an IOException
        dis = new DataInputStream(new BufferedInputStream(is));

        while ((s = dis.readLine()) != null) {
            out.println(s);
        }

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    } finally {

        try { is.close(); }
        catch (Exception ignore) {}

    }

    out.println("</font>");

 }

 @SuppressWarnings("deprecation")
 private void displayLBcsv(HttpServletRequest req, PrintWriter out) {

    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;

    out.println("<font size=2>");

    try {

        u = new URL("http", "web.foretees.com", 80, "/lb?stats;csv;up");

        is = u.openStream();         // throws an IOException
        dis = new DataInputStream(new BufferedInputStream(is));

        while ((s = dis.readLine()) != null) {
            out.println(s);
        }

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    } finally {

        try { is.close(); }
        catch (Exception ignore) {}

    }

    out.println("</font>");

 }


 private void displayJVMmem(HttpServletRequest req, PrintWriter out) {
/*
    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s;
*/
    String node = req.getParameter("n");
    String instance = req.getParameter("i");

    String ip = "";
    int port = 8080;

    if (node.equals("1")) {
        ip = "10.0.0.1";
        if (instance.equals("2")) port++;
    } else if (node.equals("2")) {
        ip = "10.0.0.2";
        if (instance.equals("2")) port++;
    } else if (node.equals("3")) {
        ip = "10.0.0.3";
        if (instance.equals("2")) {
            port = 8180;
        } else if (instance.equals("2")) {
            port = 8280;
        } else if (instance.equals("3")) {
            port = 8380;
        }
    }


    out.println("<font size=2>");

    try {

        out.println(getJVMmem(ip, port));

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    }

/*
    try {

        u = new URL("http", ip, port, "/mem.jsp");

        is = u.openStream();         // throws an IOException
        dis = new DataInputStream(new BufferedInputStream(is));

        while ((s = dis.readLine()) != null) {
            out.println(s);
        }

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    } finally {

        try { is.close(); }
        catch (Exception ignore) {}

    }
*/
    out.println("</font>");

 }


 private void displayJVMmemAll(HttpServletRequest req, PrintWriter out) {


    out.println("<font size=2>");

    try {

        out.println("<a href=\"http://216.243.184.81:8080/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE1.1</a> - " + getJVMmem("10.0.0.1", 8080) + "<br>");
        out.println("<a href=\"http://216.243.184.81:8081/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE1.2</a> - " + getJVMmem("10.0.0.1", 8081) + "<br>");
        out.println("<a href=\"http://216.243.184.82:8080/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE2.1</a> - " + getJVMmem("10.0.0.2", 8080) + "<br>");
        out.println("<a href=\"http://216.243.184.82:8081/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE2.2</a> - " + getJVMmem("10.0.0.2", 8081) + "<br>");
        out.println("<a href=\"http://216.243.184.83:8180/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE3.1</a> - " + getJVMmem("10.0.0.3", 8180) + "<br>");
        out.println("<a href=\"http://216.243.184.83:8280/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE3.2</a> - " + getJVMmem("10.0.0.3", 8280) + "<br>");
        out.println("<a href=\"http://216.243.184.83:8380/mem2.jsp\" target=\"memDetails\" class=\"statLink\">TCNODE3.3</a> - " + getJVMmem("10.0.0.3", 8380));

    } catch (Exception exc) {

        out.println("<br>Error: " + exc.toString());

    }

    out.println("</font>");
    
 }


 @SuppressWarnings("deprecation")
 private String getJVMmem(String ip, int port)
    throws Exception {


    URL u;
    InputStream is = null;
    DataInputStream dis;
    String s, result = "";

    u = new URL("http", ip, port, "/mem.jsp");

    is = u.openStream();         // throws an IOException
    dis = new DataInputStream(new BufferedInputStream(is));

    while ((s = dis.readLine()) != null) {
        result += s;
    }

    return result;
 }

 
 private void countHit(String provider, PrintWriter out) {

    Connection con = null;
    PreparedStatement pstmt = null;

    try {

        con = dbConn.Connect(rev);
        pstmt = con.prepareStatement ("INSERT INTO external_link_hits (provider, count) VALUES (?, 1) ON DUPLICATE KEY UPDATE count = count + 1");

        pstmt.clearParameters();
        pstmt.setString(1, provider);

        pstmt.executeUpdate();

    } catch (Exception e) {

        SystemUtils.logError(e.getMessage());

    } finally {

       try {
          pstmt.close();
       } catch (SQLException ignored) {}

       try {
          con.close();
       } catch (SQLException ignored) {}
    }

    pstmt = null;
    con = null;

 }

 private String formatIntoDDHHMMSS(int time_in_seconds) {

     
    int day = (int)TimeUnit.SECONDS.toDays(time_in_seconds);
    long hours = TimeUnit.SECONDS.toHours(time_in_seconds) - (day * 24);
    long minute = TimeUnit.SECONDS.toMinutes(time_in_seconds) - (TimeUnit.SECONDS.toHours(time_in_seconds) * 60);
    long second = TimeUnit.SECONDS.toSeconds(time_in_seconds) - (TimeUnit.SECONDS.toMinutes(time_in_seconds) * 60);

    return ( day + "d " + hours + "h " + minute + "m " + second + "s");

/*
    int hours = time_in_seconds / 3600,
        remainder = time_in_seconds % 3600,
        minutes = remainder / 60,
        seconds = remainder % 60;

    return ( (hours < 10 ? "0" : "") + hours
        + ":" + (minutes < 10 ? "0" : "") + minutes
        + ":" + (seconds< 10 ? "0" : "") + seconds );
*/
 }

}   // end of stats2 class