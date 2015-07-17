/***************************************************************************************
 *   Connect:  This servlet will provide connection objects to various db servers
 *
 *       called by:  dbConn, verifySlot
 *
 *
 *   created:  3/07/2006   Bob P.
 *
 *
 *   last updated:
 *
 *       11/16/12  Point connection objects to new master db server
 *       11/11/11  Added closeCon method
 *        4/27/11  Added getCaddieCon method
 *        4/14/11  Added testConnection and ensureConnection methods
 *        4/13/11  Added tcpKeepAlive=true option to the JDBC URL parameter list (requires Connector/J 5.0.7+)
 *       12/17/10  Added getDiningCon method for getting connections to the PostgeSQL server
 *        3/15/10  Rewrote
 *        8/15/08  Change from synchronized to not.
 *
 *
 *   notes: As of 5/4/09 we were using url =
 *          "jdbc:mysql://10.0.0.10/" + club + "?jdbcCompliantTruncation=false&autoReconnect=true";
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.sql.*;          // mysql
import javax.sql.*;         // postgres
import javax.naming.*;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.UUID;
//import org.apache.commons.dbcp.*;

public class Connect {

    // CONNECTION SETTINGS
    //private final static String DB_URL_READ = "jdbc:mysql://10.0.5.1/";
    //private final static String DB_URL_READ_CRITICAL = "jdbc:mysql://10.0.5.1/";
    //private final static String DB_URL_WRITE = "jdbc:mysql://10.0.5.1/";
    //private final static String DB_PARMS = "?jdbcCompliantTruncation=false"; // &autoReconnect=true&tcpKeepAlive=true
    //private final static String DB_USER = "bobp";
    //private final static String DB_PASS = "rdp0805";
    // USED TO DETERMIN WHICH DB SERVER TO MAKE THE CONNECTION TO
    //public final static int READ = 1;
    //public final static int READ_CRITICAL = 2;
    //public final static int WRITE = 3;
    private final static int CONNECT_RETRIES = 10; // How many times we'll try to get a connection
    
    private final static String INIT_CONTEXT = "java:comp/env";
    
    private final static String DINING_DB = "jdbc/postgres";
    private final static String FORETEES_DB = "jdbc/ftmysql";
    private final static String CADDIE_DB = "jdbc/caddie";

    private static DataSource getDataSource(String lookup) {

        DataSource ds = null;
        try {
            Context iCtx = new InitialContext();
            Context eCtx = (Context)iCtx.lookup(INIT_CONTEXT);
            ds = (DataSource) eCtx.lookup(lookup);
        } catch (Exception exc) {

            logErrorTxt("Connect: getDataSource:, err=" + exc.toString(), ProcessConstants.REV);

        }

        return ds;

    }

    /**
     * getCon - Returns a default connection to the MySQL database
     *
     * @param club Name of club (and database) to connect to
     * @param queryType Used to specify which database server the connection will be for
     *
     * @return Connection - Returns a connection object for the requested club (database)
     */
    public static Connection getCon(String club) {

        Connection con = null;

        int attempts = 0;

        while (con == null && attempts < CONNECT_RETRIES) {
            try {

                //Class.forName("com.mysql.jdbc.Driver").newInstance();

                //if (queryType == WRITE || queryType == READ_CRITICAL) {

                con = getDataSource(FORETEES_DB).getConnection(); // Get the connection
                con.setAutoCommit(true);  // Make sure that every statment will be commited by default.
                // It's possible that another process left the connection with
                // AutoCommit off.
                // If caller wants transactional support, they should setAutoCommit(false)
                // after getting the Connection object, and then setAutoCommit(true) when done.
                con.setCatalog(club); // Set the club database


                //} else {

                //  con = DriverManager.getConnection(DB_URL_READ + club + DB_PARMS, DB_USER, DB_PASS);

                //}

            } catch (Exception exc) {

                con = null;
                logErrorTxt("Connect: club=" + club + ", err=" + exc.toString(), ProcessConstants.REV);

            }
            attempts++;
        }
        return con;

    }

    /**
     * getCon - Returns the default (writable) connection to the MySQL database
     *
     * @param club Name of club (and database) to connect to
     *
     * @return Connection - Returns a connection object for the requested club (database)
     */
    //public static Connection getCon(String club) {
//     return getCon(club, WRITE);
// }
    public static Connection getCon(HttpServletRequest req, String club) {
        return getCon(req, club, true);
    }

    public static Connection getCon(HttpServletRequest req) {
        return getCon(req, null, true);
    }

    public static Connection getCon(HttpServletRequest req, boolean createIfNone) {
        return getCon(req, null, createIfNone);
    }

    public static Connection getCon(HttpServletRequest req, String club, boolean createIfNone) {

        Connection con = reqUtil.getRequestConnection(req, ProcessConstants.RQA_FT_CONNECT, null);
        String conClub = reqUtil.getRequestString(req, ProcessConstants.RQA_FT_CONNECT_CLUB, "");

        if (club == null) {
            // Get club name from session, then try request
            club = reqUtil.getSessionString(req, "club", null);
            if (club == null) {
                club = reqUtil.getParameterString(req, "s_c", null);
            }
        }

        if ((club != null && !club.equals(conClub)) || con == null) {
            // Different club db, or no new connection
            if (con != null) {
                // Close any existing connection
              /*
                try { con.rollback(); }
                catch (Exception ignore) {}
                 * 
                 */
                try {
                    con.close();
                } catch (Exception ignore) {
                }
            }
            if (createIfNone && club != null) {
                con = Connect.getCon(club);
                if (con != null) {
                    req.setAttribute(ProcessConstants.RQA_FT_CONNECT, con);
                    req.setAttribute(ProcessConstants.RQA_FT_CONNECT_CLUB, club);
                }
            }
        }

        return con;
    }

    /**
     * getDiningCon - Returns a connection to the PostgreSQL dining database
     *
     * @return Connection - Returns a connection object for the dining database server (PostgreSQL)
     */
    public static Connection getDiningCon() {


        Connection con_d = null;

        int attempts = 0;

        while (con_d == null && attempts < CONNECT_RETRIES) {
            try {
                /*
                Context ctx = new InitialContext();
                if(ctx == null )
                throw new Exception("Boom - No Context");
                
                javax.sql.DataSource ds = (javax.sql.DataSource)ctx.lookup("java:comp/env/jdbc/postgres");
                
                if (ds != null) con_d = ds.getConnection();
                 */
                con_d = getDataSource(DINING_DB).getConnection(); // Get the connection

            } catch (Exception exc) {

                logError("Connect.getDiningCon(): err=" + exc.toString());

            } finally {
            }
            attempts++;
        }

        return con_d;

    }

    /**
     * getCaddieCon - Returns a default connection to the Caddie MySQL database
     *
     * @param club Name of club (and database) to connect to
     * @param queryType Used to specify which database server the connection will be for
     *
     * @return Connection - Returns a connection object for the requested club (database)
     */
    public static Connection getCaddieCon(String club) {

        Connection c_con = null;

        int attempts = 0;

        while (c_con == null && attempts < CONNECT_RETRIES) {

            try {

                /*
                Class.forName("com.mysql.jdbc.Driver").newInstance();
                
                // the user/pass needs to be the same as in /srv/www/vhosts/caddie.foretees.com/1/host_club_information.php
                c_con = DriverManager.getConnection("jdbc:mysql://10.0.0.40/" + club + "_caddie" + DB_PARMS, "caddyapp", "caddypass");
                 */
                c_con = getDataSource(CADDIE_DB).getConnection();   // Get the connection
                c_con.setCatalog(club + "_caddie");                             // Set the club database

            } catch (Exception exc) {

                logErrorTxt("Connect.getCaddieCon(): club=" + club + ", err=" + exc.toString(), ProcessConstants.REV);

            }

            attempts++;
        }

        return c_con;

    }

    /**
     * ensureConnection - Tests the current connection and returns true if connection is good
     *
     * @param con Existing connection to database to test
     *
     * @return Boolean - Returns true of ok false if bad
     */
    public static boolean testConnection(Connection con) {


        boolean result = false;

        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT DATABASE()");

            if (rs.next()) {
                result = true;
            }

        } catch (Exception exc) {
            //logError("FATAL: testConnection - " + Utilities.getStackTraceAsString(exc));
        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                stmt.close();
            } catch (Exception ignore) {
            }

        }

        return (result);

    }

    /**
     * ensureConnection - Tests the current connection and returns it or a new one if needed
     *
     * @param club Name of club (and database) to connect to
     * @param con Existing connection to database to test
     *
     * @return Connection - Returns a connection object for the requested club (database)
     */
    public static Connection ensureConnection(String club, Connection con) {


        // test conneciton and if it fails attempt to reconnect
        if (testConnection(con) == false) {

            // get new con for club
            con = getCon(club);

            // test it again
            if (testConnection(con) == false) {

                try {
                    con.close();
                } catch (Exception ignore) {
                }

                return null;

            }

        }

        return con;

    }

    /**
     * closeCon - Closes the passed connection object
     *
     * @param con The existing connection to database to close
     *
     */
    public static void closeCon(Connection con) {

        if (con != null) {
            /*
            try { con.close(); }
            catch (Exception ignore) {}
             */
        }

    }

    public static void closeCon(HttpServletRequest req) {

        Connection con = getCon(req, false);
        if (con != null) {
            /*
            try { con.rollback(); }
            catch (Exception ignore) {}
             * 
             */
            try {
                con.close();
            } catch (Exception ignore) {
            }
        }

    }
    
    public static void close(ResultSet rs, PreparedStatement stmt, Connection con){
        close(rs);
        close(stmt);
        close(con);
    }
    
    public static void close(ResultSet rs, Statement stmt, Connection con){
        close(rs);
        close(stmt);
        close(con);
    }
    
    public static void close(ResultSet rs, PreparedStatement stmt){
        close(rs);
        close(stmt);
    }
    
    public static void close(ResultSet rs, Statement stmt){
        close(rs);
        close(stmt);
    }
    
    public static void close(Statement stmt, Connection con){
        close(stmt);
        close(con);
    }
    
    public static void close(PreparedStatement stmt, Connection con){
        close(stmt);
        close(con);
    }
    
    public static void close(PreparedStatement stmt){
        try{
            stmt.close();
        }catch(Exception e){
            
        }
    }
    
    public static void close(Statement stmt){
        try{
            stmt.close();
        }catch(Exception e){
            
        }
    }
    
    public static void close(Connection con){
        try{
            con.close();
        }catch(Exception e){
            
        }
    }
    
    public static void close(ResultSet rs){
        try{
            rs.close();
        }catch(Exception e){
            
        }
    }
    
    public static void rollback(Connection con) {
        try {
            con.rollback();
        } catch (Exception exc) {
            logError("Connect.rollback: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
    }
    
    public static void rollbackTo(Connection con, Savepoint save_point) {
        try {
            con.rollback(save_point);
        } catch (Exception exc) {
            logError("Connect.rollbackTo: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
    }
    
    // Rollback all or to savepoint
    public static void rollbackTo(Connection con, boolean prevAutoCommitMode, Savepoint save_point) {
        
        if(!prevAutoCommitMode){
            // We started this transaction, rollback the whole thing.
            Connect.rollback(con);
        } else {
            // Roll back to the savepoint
            Connect.rollbackTo(con, save_point);
        }
        
    }
    
    public static Savepoint startTransaction(Connection con) {
        Savepoint result = null;
        boolean prevAutoCommitMode = enableTransactions(con);
        if(!prevAutoCommitMode){
            // Not in a previous auto commit.
            // We'll need to use a save point
            try {
                result = con.setSavepoint(UUID.randomUUID().toString());
            } catch (Exception exc) {
                logError("Connect.setSavepoint: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
                // Probably should trow an exception here?
            }
        }
        return result;
    }
    
    // Rollback transaction and restore previous state
    public static void cancelTransaction(Connection con, Savepoint save_point) {
        if(save_point == null){
            rollback(con);
            setAutoCommitMode(con, false);
        } else {
            rollbackTo(con, save_point);
        }
    }
    
    // Commit transaction and restore previous state
    public static void commitTransaction(Connection con, Savepoint save_point) {
        if(save_point == null){
            commit(con);
            setAutoCommitMode(con, false);
        } else {
            releaseSavepoint(con, save_point);
        }
    }
    
    public static Savepoint setSavepoint(Connection con, String save_point_name) {
        Savepoint result = null;
        try {
            result = con.setSavepoint(save_point_name);
        } catch (Exception exc) {
            logError("Connect.setSavepoint: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
        return result;
    }
    
    public static boolean releaseSavepoint(Connection con, Savepoint save_point) {
        boolean result = false;
        try {
            con.releaseSavepoint(save_point);
            result = true;
        } catch (Exception exc) {
            logError("Connect.releaseSavepoint: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
        return result;
    }
    
    public static void commit(Connection con) {
        try {
            con.commit();
        } catch (Exception exc) {
            logError("Connect.commit: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
    }
    
    public static void setAutoCommitModeIfTrue(Connection con, boolean mode, boolean set) {
        if(set){
            setAutoCommitMode(con, mode);
        }
        
    }
    
    
    
    // Enable transactions, and return previous state
    public static boolean enableTransactions(Connection con) {
        boolean autoCommitMode = getAutoCommitMode(con);
        if (autoCommitMode) {
            try {
                // Set auto commit status to false to enable transactions
                con.setAutoCommit(false);
            } catch (Exception exc) {
                logError("Connect.enableTransactions: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
            }
        }
        return autoCommitMode;
    }
    
    public static void setAutoCommitMode(Connection con, boolean mode) {
        try {
            // Set auto commit status
            con.setAutoCommit(mode);
        } catch (Exception exc) {
            logError("Connect.setAutoCommitMode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
    }
    
    public static boolean getAutoCommitMode(Connection con) {
        boolean autoCommitMode = false;
        try {
            // Save our current auto commit status
            autoCommitMode = con.getAutoCommit();
        } catch (Exception exc) {
            logError("Connect.getAutoCommitMode: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
        }
        return autoCommitMode;
    }
    
    public static long getLastInsertId(Connection con) {
        long result = 0;
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
                pstmt = con.prepareStatement(
                        "SELECT LAST_INSERT_ID() AS last_id");

                rs = pstmt.executeQuery();

                if (rs.next()) {
                    result = rs.getLong("last_id");
                }

        } catch (Exception e) {

            logError("Connect.getLastInsertId: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {

            Connect.close(rs, pstmt);

        }
        return result;
    }

    //************************************************************************
    //
    //  sessionLog - logs each time a user logs in
    //
    //   called by:  Login (see Logout also)
    //
    //************************************************************************
    public static void sessionLog(String msg, String user, String pw, String club, String caller, Connection con) {

        Statement stmt = null;
        PreparedStatement pstmt = null;

        try {

            //
            //   Get current date
            //
            Calendar cal = new GregorianCalendar();                      // get todays date
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH) + 1;
            int daynum = cal.get(Calendar.DAY_OF_MONTH);

            long date = (year * 10000) + (month * 100) + daynum;         // date value for today

            String sdate = String.valueOf(new java.util.Date());         // get date and time string

            //
            //  Build the message
            //
            msg = msg + " User=" + user + ", PW=" + pw + ", Club=" + club + ", Caller=" + caller;

            //
            //  Create table to hold log in case it has not already been added for this club
            //
            if (con != null) {
                /*
                stmt = con.createStatement();        // create a statement
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sessionlog(" +
                "date bigint, sdate varchar(36), msg text, " +
                "index ind1 (date))");
                stmt.close();
                 */
                //
                //  Save the session message in the db table
                //
                pstmt = con.prepareStatement(
                        "INSERT INTO sessionlog (date, sdate, msg) VALUES (?,?,?)");

                pstmt.clearParameters();
                pstmt.setLong(1, date);
                pstmt.setString(2, sdate);
                pstmt.setString(3, msg);
                pstmt.executeUpdate();
            }

        } catch (Exception e) {


            logError("Error in SystemUtils.sessionLog for club: " + club + ". Exception= " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

        } finally {

            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (Exception ignore) {
            }

            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ignore) {
            }

        }

    }  // end of sessionLog
    
     public static void logErrorTxt(String msg, String club) {


        PrintWriter fout1 = null;

        //
        //   Add the date/time and node id
        //
        msg = String.valueOf(new java.util.Date()) + " - " + msg + " [NODE " + ProcessConstants.SERVER_ID + "]";


        try {

            //
            //  Absolute path to the clubs error log file
            //
            if (club != null & !club.equals("")) {
                fout1 = new PrintWriter(new FileWriter("/srv/webapps/" + club + "/errorlog.txt", true));
            } else {
                fout1 = new PrintWriter(new FileWriter("/srv/webapps/errorlog.txt", true));
            }


            //
            //  Put header line in text file
            //
            fout1.print(msg);
            fout1.println();      // output the line

        } catch (Exception e2) {
        } finally {

            try {
                fout1.close();
            } catch (Exception ignore) {
            }

        }

    }  // end of logErrorTxt
     
    public static void logError(String msg) {

        Connection con = null;

        try {

            con = getCon(ProcessConstants.REV);

            logError(msg, con);           // go log the msg

        } catch (Exception exc) {

            // write it to the text error log
            logErrorTxt(msg, ProcessConstants.REV);

            // then dump a stack trace to the catalina log file
            exc.printStackTrace();

        } finally {

            try {
                con.close();
            } catch (Exception ignore) {
            }
        }

    }  // end of logError

    public static void logError(String msg, Connection con) {

        PreparedStatement pstmt = null;

        Calendar cal = new GregorianCalendar();                      // get todays date

        try {

            if (con != null) {      // con to v5 passed ?     ******* MUST be v5 or current rev level **********

                pstmt = con.prepareStatement(
                        "INSERT INTO errorlog (id, err_timestamp, date, sdate, msg) "
                        + "VALUES (null,now(),?,?,?)");

                pstmt.clearParameters();
                pstmt.setLong(1, (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH));
                pstmt.setString(2, String.valueOf(new java.util.Date()));
                pstmt.setString(3, msg + " [NODE " + ProcessConstants.SERVER_ID + "]");
                pstmt.executeUpdate();

            } else {

                // write it to the text error log
                logErrorTxt(msg, ProcessConstants.REV);
            }

        } catch (Exception exc) {

            // write it to the text error log
            logErrorTxt(msg, ProcessConstants.REV);

            // then dump a stack trace to the catalina log file
            exc.printStackTrace();

        } finally {

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

    }  // end of logError
    
    public static String stackTraceToString(StackTraceElement[] stackTrace){
        StringBuilder result = new StringBuilder();
        for(StackTraceElement ste : stackTrace){
            result.append(ste.toString());
            result.append("\n");
        }
        return result.toString();
    }

    //************************************************************************
    //
    //  logDebug - logs system debug messages to db using new con
    //
    //************************************************************************
    public static void logDebug(String initials, String msg) {

        Connection con = null;

        try {

            con = getCon(ProcessConstants.REV);

            logDebug(initials, msg, con);           // go log the msg

        } catch (Exception exc) {
            // Don't bother writing to an alternate location for debug messages
        } finally {

            try {
                con.close();
            } catch (Exception ignore) {
            }
        }

    }  // end of logError

    //************************************************************************
    //
    //  logDebug - logs system debug messages to db using the con passed **** MUST be rev (v5) ***********
    //
    //************************************************************************
    public static void logDebug(String initials, String msg, Connection con) {

        PreparedStatement pstmt = null;

        Calendar cal = new GregorianCalendar();                      // get todays date

        try {

            if (con != null) {      // con to v5 passed ?     ******* MUST be v5 or current rev level **********

                pstmt = con.prepareStatement(
                        "INSERT INTO v5.debuglog (id, initials, debug_timestamp, date, sdate, msg) "
                        + "VALUES (null,?,now(),?,?,?)");

                pstmt.clearParameters();
                pstmt.setString(1, initials);
                pstmt.setLong(2, (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH));
                pstmt.setString(3, String.valueOf(new java.util.Date()));
                pstmt.setString(4, msg + " [NODE " + ProcessConstants.SERVER_ID + "]");
                pstmt.executeUpdate();

            }

        } catch (Exception exc) {
            // Don't bother writing to an alternate location for debug messages
        } finally {

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }
        }

    }  // end of logError

    
}  // end of Connect class

