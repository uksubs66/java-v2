/***************************************************************************************
 *   Proshop_report_guest_names: This servlet will ouput all guests that played during the
 *                               selected date range.  It will attempt to display by their
 *                               names if provided.
 *
 *
 *   Called by:     reports tab menu option
 *
 *   Created:       7/29/2009
 *
 *
 *   Revisions:  
 * 
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.Math;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import com.foretees.common.Connect;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_report_guest_names extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    NumberFormat nf = NumberFormat.getNumberInstance();

 
 //*****************************************************
 // Display a report selection menu page and offer help
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    
   if (req.getParameter("sortby") != null) {         // if a sort by request from doPost - go there

       doPost(req, resp);      // call doPost processing
       return;
   }

    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html"); 
    PrintWriter out = resp.getWriter(); 
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }

    Connection con = Connect.getCon(req);                   // get DB connection
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle2("Guest Name Report"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
     
    // prompt user for the custom date range
    
    getCustomDate(req, out, con);    
    
    
    out.println("</body>");
    out.println("</html>");
    out.close();
    
 } // end of doGet routine


 //*****************************************************
 // Process initial report request to compute the 
 // dates needed for this report or direct to page
 // that gets dates from user.  Once dates are present
 // in the request object call out appropriately
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    PreparedStatement pstmt3 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    String range_begin = "";
    String range_end = "";
    String host = "";
    String player = "";
    String start_date = "";
    String end_date = "";
    String sortby = "";
    String fname = "";
    String mi = "";
    String mid = "";
    String lname = "";
    String temp = "";
    String last_userg = "";
    String last_host = "";
    
    long edate = 0;
    long sdate = 0;
    int yy = 0;
    int mm = 0;
    int dd = 0;
    int hr = 0;
    int min = 0;
    int start_year = 0;
    int start_month = 0;
    int start_day = 0;
    int end_year = 0;
    int end_month = 0;
    int end_day = 0;
    int no_name_count = 0;
    int rcount = 0;
    int guest_id = 0;
    int guest_cols = 0;
    
    boolean guest_tracking_enabled = false;
    boolean found_name = false;
    
    String [] playerA = new String [5];    
    String [] usergA = new String [5];    
    String [] gtypeA = new String [5];    
    int [] showA = new int [5];    
    int [] p9A = new int [5];    
    int [] guest_idA = new int[5];
    
    Map<String, String> guestdb_config = new HashMap<String, String>();
    Map<String, Object> guest_data = new HashMap<String, Object>();
        
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }
    
    Connection con = Connect.getCon(req);                   // get DB connection
    if (con == null) {
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
    
    guest_tracking_enabled = Utilities.isGuestTrackingConfigured(0, con);
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    if (req.getParameter("sdate") != null) {         // if dates provided from below
       
       start_date = (req.getParameter("sdate") != null) ? req.getParameter("sdate")  : "";
       end_date = (req.getParameter("edate") != null) ? req.getParameter("edate")  : "";
       sortby = (req.getParameter("sortby") != null) ? req.getParameter("sortby")  : "";
       
    } else {            // if not get them from calendar above

       start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
       end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    }
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    String club = (String)session.getAttribute("club");
    int lottery = Integer.parseInt(templott);
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            resp.setHeader("Content-Disposition", "attachment;filename=\""+club+".xls\"");
        }
    }
    catch (Exception exc) {
    }

    out.println(SystemUtils.HeadTitle2("Guest Name Report"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");

    
    if (!excel.equals("yes")) {                // if not excel
    
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }
    
    //
    //  Use the custom date range provided to generate the report
    //

   // check to see if the date is here, and if not then jump to display calendar routine
   if (start_date.equals("")) {
       getCustomDate(req, out, con); 
       return;
   }

   // if no ending date then default to starting date for 1 day report
   if (end_date.equals("")) {
      end_date = start_date;
   }

   // if the date passed are in our long format already (from being passed around)
   //  then we can skip this first round of validation
   try {
       sdate = Long.parseLong(start_date);
       edate = Long.parseLong(end_date);
   } catch (Exception e) {

       // make sure the dates here are valid, if not redisplay the calendars
       try {

           int dash1 = start_date.indexOf("-");
           int dash2 = start_date.indexOf("-", dash1 + 1);
           start_year = Integer.parseInt(start_date.substring(0, 4));
           start_month = Integer.parseInt(start_date.substring(dash1 + 1, dash2));
           start_day = Integer.parseInt(start_date.substring(dash2 + 1));

           dash1 = end_date.indexOf("-");
           dash2 = end_date.indexOf("-", dash1 + 1);
           end_year = Integer.parseInt(end_date.substring(0, 4));
           end_month = Integer.parseInt(end_date.substring(dash1 + 1, dash2));
           end_day = Integer.parseInt(end_date.substring(dash2 + 1));

       } catch (Exception e2) {
           // invalid dates here, bailout and call form again
           getCustomDate(req, out, con);
           return;
       }

       // build our date variables for use in query
       sdate = start_year * 10000;                    // create a date field of yyyymmdd
       sdate = sdate + (start_month * 100);
       sdate = sdate + start_day;

       edate = end_year * 10000;                      // create a date field of yyyymmdd
       edate = edate + end_month * 100;
       edate = edate + end_day;

    }

    if (sdate > edate) {
       // start date is after the end date, jump out and call form again
       getCustomDate(req, out, con);
       return;
    }

    //
    //  we now have our date range - generate the report
    //
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " -->");
    
            
    // get pretty dates for the report
    try {
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT DATE_FORMAT('" + sdate + "', '%W %M %D, %Y') AS range_begin, DATE_FORMAT('" + edate + "', '%W %M %D, %Y') AS range_end");
        if (rs.next()) {
            range_begin = rs.getString("range_begin");
            range_end = rs.getString("range_end");
        }
        rs.close();
        stmt.close();
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting formatted dates.", e.getMessage(), out, false);
        return;
    }

    if (sortby.equals("lname")) {      // sort by last name
        
        //  User wishes to sort the guests by last name (name 3) - we will need to build a new db table in order to sort by the guest names since they are not separated in teepast.
        try {
            stmt = con.createStatement();

            stmt.executeUpdate("DROP TABLE IF EXISTS guestreport");

        } catch (Exception ignore) {
            // disregard if fails
        } finally {
            Connect.close(stmt);
        }

        try {

            stmt = con.createStatement();        // create a statement

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS guestreport("
                    + "mm smallint, dd smallint, "
                    + "yy integer, hr smallint, min smallint, "
                    + "host varchar(43), player varchar(43), fname varchar(30), mi varchar(30), lname varchar(30), guest_id int(11))");

        } catch (Exception exc1) {
            SystemUtils.buildDatabaseErrMsg(exc1.toString(), "", out, false);
            return;
        } finally {
            Connect.close(stmt);
        }
    }
    
    
    // If this club uses guest tracking, look up their config options so we know which fields to print
    if (guest_tracking_enabled) {
        
        try {
            
            stmt = con.createStatement();
            
            rs = stmt.executeQuery("SELECT * FROM guestdb");
            
            if (rs.next()) {
                guestdb_config.put("email", rs.getString("email"));
                guestdb_config.put("phone", rs.getString("phone"));
                guestdb_config.put("address", rs.getString("address"));
                guestdb_config.put("gender", rs.getString("gender"));
                guestdb_config.put("hdcp_num", rs.getString("hdcp_num"));
                guestdb_config.put("hdcp_index", rs.getString("hdcp_index"));
                guestdb_config.put("home_club", rs.getString("home_club"));
            }
            
        } catch (Exception e) {
            SystemUtils.buildDatabaseErrMsg(e.toString(), "", out, false);
            return;
        } finally {
            Connect.close(stmt);
        }
    }
    
                              
    // setup our report header
    out.println("<center><font size=+2>Guest Name Report<br></font>" + 
            "<font size=+1><nobr>From " + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr></font></center>");
   
    if (!excel.equals("yes")) {     // if normal request

        out.println("<center><form method=post action=\"Proshop_report_guest_names\" name=frmRequestExcel id=frmRequestExcel target=\"_blank\">");
        out.println("<input type=hidden name=sdate value=\"" + sdate + "\">");
        out.println("<input type=hidden name=edate value=\"" + edate + "\">");
        out.println("<input type=hidden name=sortby value=\"" + sortby + "\">");
        out.println("<input type=hidden name=excel value=\"yes\">");
        out.println("<input type=submit value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
                
       out.println("<p align=center><font size=2>Name 1, Name 2, Name 3 contain the Guest Names as best as can be determined.</font></p>");
       
       if (sortby.equals("lname")) {
          out.println("<p align=center><font size=2><b>NOTE:</b> Click on the <u>Date</u> heading to sort the results by date.</font></p>");
       } else {
          out.println("<p align=center><font size=2><b>NOTE:</b> Click on the <u>Name 3</u> heading to sort the results by guest name.</font></p>");
       }
    }
   
    out.println("<br><table bgcolor=\"#F5F5DC\" align=center border=1 cellpadding=6>");
    out.println("<tr bgcolor=\"#336633\">");
  
    if (sortby.equals("date") || excel.equals("yes")) {
       out.println("<td><font color=white><b>Date</b></font></td>");
    } else {
       out.println("<td><font color=white><b><a href='Proshop_report_guest_names?sortby=date&sdate=" +sdate+ "&edate=" +edate+ "' style='color:white'>Date</a></b></font></td>");
    }
    
    out.println("<td><font color=white><b>Time</b></font></td>" +
                    "<td><font color=white><b>Hosting Member</b></font></td>" +
                    "<td><font color=white><b>Guest (from Tee Time)</b></font></td>" +
                    "<td><font color=white><b>Name 1</b></font></td>" +
                    "<td><font color=white><b>Name 2</b></font></td>");
   
    if (sortby.equals("lname") || excel.equals("yes")) {
       out.println("<td><font color=white><b>Name 3</b></font></td>");
    } else {
       out.println("<td><font color=white><b><a href='Proshop_report_guest_names?sortby=lname&sdate=" +sdate+ "&edate=" +edate+ "' style='color:white'>Name 3</a></b></font></td>");
    }
    
     if (guest_tracking_enabled && !guestdb_config.isEmpty()) {

         if (!guestdb_config.get("email").equals("N")) {
             guest_cols += 3;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Email 1</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">Email 2</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">Emails Enabled?</span></td>");
         }

         if (!guestdb_config.get("phone").equals("N")) {
             guest_cols += 2;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Phone 1</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">Phone 2</span></td>");
         }

         if (!guestdb_config.get("address").equals("N")) {
             guest_cols += 5;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Address 1</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">Address 2</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">City</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">State</span></td>");
             out.println("<td><span style=\"color:white; font-weight:bold;\">Zip</span></td>");
         }

         if (!guestdb_config.get("gender").equals("N")) {
             guest_cols++;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Gender</span></td>");
         }

         if (!guestdb_config.get("hdcp_num").equals("N")) {
             guest_cols++;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Handicap #</span></td>");
         }

         if (!guestdb_config.get("hdcp_index").equals("N")) {
             guest_cols++;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Handicap Index</span></td>");
         }

         if (!guestdb_config.get("home_club").equals("N")) {
             guest_cols++;
             out.println("<td><span style=\"color:white; font-weight:bold;\">Home Club</span></td>");
         }
     }
    
    out.println("</tr>");
    
    
    // build and display the report data
    try {
       
        pstmt = con.prepareStatement("" +
                "SELECT mm, dd, yy, hr, min, player1, player2, player3, player4, player5, " +
                "show1, show2, show3, show4, show5, IFNULL(userg1,''), IFNULL(userg2,''), IFNULL(userg3,''), IFNULL(userg4,''), IFNULL(userg5,''), " +
                "gtype1, gtype2, gtype3, gtype4, gtype5, p91, p92, p93, p94, p95, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5 " +
                "FROM teepast2 " +
                "WHERE date >= ? AND date <= ? AND (gtype1 != '' OR gtype2 != '' OR gtype3 != '' OR gtype4 != '' OR gtype5 != '') " +
                "ORDER BY date, time");
                
        pstmt.clearParameters();
        pstmt.setLong(1, sdate);
        pstmt.setLong(2, edate);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            mm = rs.getInt("mm");
            dd = rs.getInt("dd");
            yy = rs.getInt("yy");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            playerA[0] = rs.getString("player1").trim();
            playerA[1] = rs.getString("player2").trim();
            playerA[2] = rs.getString("player3").trim();
            playerA[3] = rs.getString("player4").trim();
            playerA[4] = rs.getString("player5").trim();
            showA[0] = rs.getInt("show1");
            showA[1] = rs.getInt("show2");
            showA[2] = rs.getInt("show3");
            showA[3] = rs.getInt("show4");
            showA[4] = rs.getInt("show5");
            usergA[0] = rs.getString(16);
            usergA[1] = rs.getString(17);
            usergA[2] = rs.getString(18);
            usergA[3] = rs.getString(19);
            usergA[4] = rs.getString(20);
            gtypeA[0] = rs.getString("gtype1");
            gtypeA[1] = rs.getString("gtype2");
            gtypeA[2] = rs.getString("gtype3");
            gtypeA[3] = rs.getString("gtype4");
            gtypeA[4] = rs.getString("gtype5");
            p9A[0] = rs.getInt("p91");
            p9A[1] = rs.getInt("p92");
            p9A[2] = rs.getInt("p93");
            p9A[3] = rs.getInt("p94");
            p9A[4] = rs.getInt("p95");
            guest_idA[0] = rs.getInt("guest_id1");
            guest_idA[1] = rs.getInt("guest_id2");
            guest_idA[2] = rs.getInt("guest_id3");
            guest_idA[3] = rs.getInt("guest_id4");
            guest_idA[4] = rs.getInt("guest_id5");
            
            rcount++;         // track # of records searched
            
            //
            //  Check for guests that have played
            //
            for (int i = 0; i < 5; i++) {            // check each player for a guest

                fname = "";
                mi = "";
                lname = "";
                host = "Unknown";
                
                found_name = false;
                
                guest_data.clear();

                if (!gtypeA[i].equals("") && !gtypeA[i].equals(null) && showA[i] == 1) {     // if guest and checked in
                    // if (!gtypeA[i].equals("") && !gtypeA[i].equals( null ) && showA[i] == 1 && p9A[i] == 1) {     // CUSTOM to show 9-hole only guests

                    //  parse the guest value to see if there is a name
                    if ((guest_tracking_enabled && guest_idA[i] != 0) || playerA[i].length() > gtypeA[i].length()) {

                        if (guest_tracking_enabled && guest_idA[i] != 0) {
                            
                            try {
                                
                                pstmt2 = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, guest_idA[i]);
                                
                                rs2 = pstmt2.executeQuery();
                                
                                if (rs2.next()) {
                                    
                                    found_name = true;
                                    fname = rs2.getString("name_first");
                                    mi = rs2.getString("name_mi");
                                    lname = rs2.getString("name_last");
                                    
                                    // Don't bother storing this data now if we're going to store the guest_id in guestreport for later use.
                                    if (!sortby.equals("lname")) {
                                        
                                        if (!guestdb_config.get("email").equals("N")) {
                                            guest_data.put("email1", (String) rs2.getString("email1"));
                                            guest_data.put("email2", (String) rs2.getString("email2"));
                                            guest_data.put("emailOpt", (Integer) rs2.getInt("emailOpt"));
                                        }

                                        if (!guestdb_config.get("phone").equals("N")) {
                                            guest_data.put("phone1", (String) rs2.getString("phone1"));
                                            guest_data.put("phone2", (String) rs2.getString("phone2"));
                                        }

                                        if (!guestdb_config.get("address").equals("N")) {
                                            guest_data.put("address1", (String) rs2.getString("address1"));
                                            guest_data.put("address2", (String) rs2.getString("address2"));
                                            guest_data.put("city", (String) rs2.getString("city"));
                                            guest_data.put("state", (String) rs2.getString("state"));
                                            guest_data.put("zip", (String) rs2.getString("zip"));
                                        }

                                        if (!guestdb_config.get("gender").equals("N")) {
                                            guest_data.put("gender", (String) rs2.getString("gender"));
                                        }

                                        if (!guestdb_config.get("hdcp_num").equals("N")) {
                                            guest_data.put("hdcp_num", (String) rs2.getString("hdcp_num"));
                                        }

                                        if (!guestdb_config.get("hdcp_index").equals("N")) {
                                            guest_data.put("hdcp_index", (Double) rs2.getDouble("hdcp_index"));
                                        }

                                        if (!guestdb_config.get("home_club").equals("N")) {
                                            guest_data.put("home_club", (String) rs2.getString("home_club"));
                                        }
                                    }
                                }
                                
                            } catch (Exception e) {
                                Utilities.logError("Proshop_report_guest_names.doPost - Error looking up tracked guest info for guest_id: " + guest_idA[i] + " - ERR: " + e.toString());
                            } finally {
                                Connect.close(rs2, pstmt2);
                            }
                            
                        }
                        
                        if (!found_name) {
                            
                            String[] temp_player = playerA[i].substring(gtypeA[i].length()).trim().split("[ -,/]");
                            
                            if (temp_player.length > 0) {
                               
                                found_name = true;
                                
                                if (temp_player.length > 2) {
                                    fname = temp_player[0];
                                    mi = temp_player[1];
                                    lname = temp_player[2];
                                } else if (temp_player.length == 2) {
                                    fname = temp_player[0];
                                    lname = temp_player[1];
                                } else {
                                    lname = temp_player[0];
                                }
                            }
                        }

                        if (found_name) {

                            //  get the name of the host member
                            if (!usergA[i].equals("")) {

                                if (!usergA[i].equals(last_userg)) {

                                    try {

                                        pstmt2 = con.prepareStatement(
                                                "SELECT CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) as mem_name "
                                                + "FROM member2b WHERE username = ?");

                                        pstmt2.clearParameters();
                                        pstmt2.setString(1, usergA[i]);
                                        rs2 = pstmt2.executeQuery();

                                        if (rs2.next()) {
                                            host = rs2.getString("mem_name");
                                        }

                                    } catch (Exception ignore) { 
                                        // ignore, host will remain as 'Unknown'.
                                    } finally {
                                        Connect.close(rs2, pstmt2);
                                    }

                                    last_host = host;          // save this name
                                    last_userg = usergA[i];    // save this uername

                                } else {
                                    host = last_host;
                                }

                            }

                            if (sortby.equals("lname")) {      // sort by last name?

                                try {

                                    //  put the teepast info into the guestreport table to be sorted
                                    pstmt2 = con.prepareStatement(
                                            "INSERT INTO guestreport (mm, dd, yy, hr, min, host, player, fname, mi, lname, guest_id) "
                                            + "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

                                    pstmt2.clearParameters();        // clear the parms
                                    pstmt2.setInt(1, mm);
                                    pstmt2.setInt(2, dd);
                                    pstmt2.setInt(3, yy);
                                    pstmt2.setInt(4, hr);
                                    pstmt2.setInt(5, min);
                                    pstmt2.setString(6, host);
                                    pstmt2.setString(7, playerA[i]);
                                    pstmt2.setString(8, fname);
                                    pstmt2.setString(9, mi);
                                    pstmt2.setString(10, lname);
                                    pstmt2.setInt(11, guest_idA[i]);

                                    pstmt2.executeUpdate();        // move the tee slot to teepast

                                } catch (Exception exc1) {
                                    SystemUtils.buildDatabaseErrMsg(exc1.toString(), "", out, false);
                                    return;
                                } finally {
                                    Connect.close(pstmt2);
                                }

                            } else {

                                //  output the line
                                out.println("<tr>");
                                out.println("<td>" + mm + "/" + SystemUtils.ensureDoubleDigit(dd) + "/" + yy + "</td>"
                                        + "<td>" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + "</td>"
                                        + "<td>" + host + "</td>"
                                        + "<td>" + playerA[i] + "</td>"
                                        + "<td>" + fname + "</td>"
                                        + "<td>" + mi + "</td>"
                                        + "<td>" + lname + "</td>");
                                
                                if (guest_tracking_enabled && !guestdb_config.isEmpty()) {
                                    
                                    if (guest_idA[i] != 0) {

                                        if (!guestdb_config.get("email").equals("N")) {
                                            
                                            if ((String) guest_data.get("email1") != null) {
                                                out.println("<td>" + (String) guest_data.get("email1") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                            
                                            if ((String) guest_data.get("email2") != null) {
                                                out.println("<td>" + (String) guest_data.get("email2") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                            
                                            if ((Integer) guest_data.get("emailOpt") != null) {
                                                out.println("<td>" + ((Integer) guest_data.get("emailOpt") == 1 ? "Yes" : "No") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                        }

                                        if (!guestdb_config.get("phone").equals("N")) {
                                            
                                            if ((String) guest_data.get("phone1") != null) {
                                                out.println("<td>" + (String) guest_data.get("phone1") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                            
                                            if ((String) guest_data.get("phone2") != null) {
                                                out.println("<td>" + (String) guest_data.get("phone2") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                        }

                                        if (!guestdb_config.get("address").equals("N")) {
                                            
                                            if ((String) guest_data.get("address1") != null) {
                                                out.println("<td>" + (String) guest_data.get("address1") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                            
                                            if ((String) guest_data.get("address2") != null) {
                                                out.println("<td>" + (String) guest_data.get("address2") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");
                                            }
                                            
                                            if ((String) guest_data.get("city") != null) {
                                                out.println("<td>" + (String) guest_data.get("city") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }    
                                            
                                            if ((String) guest_data.get("state") != null) {
                                                out.println("<td>" + (String) guest_data.get("state") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }   
                                            
                                            if ((String) guest_data.get("zip") != null) {
                                                out.println("<td>" + (String) guest_data.get("zip") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }   
                                        }

                                        if (!guestdb_config.get("gender").equals("N")) {
                                            if ((String) guest_data.get("gender") != null) {
                                                out.println("<td>" + (String) guest_data.get("gender") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }   
                                        }


                                        if (!guestdb_config.get("hdcp_num").equals("N")) {
                                            if ((String) guest_data.get("hdcp_num") != null) {
                                                out.println("<td>" + (String) guest_data.get("hdcp_num") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }   
                                        }
                                        
                                        if (!guestdb_config.get("hdcp_index").equals("N")) {
                                            if ((Double) guest_data.get("hdcp_index") != null) {
                                                out.println("<td>" + (Double) guest_data.get("hdcp_index") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }   
                                        }

                                        if (!guestdb_config.get("home_club").equals("N")) {
                                            if ((String) guest_data.get("home_club") != null) {
                                                out.println("<td>" + (String) guest_data.get("home_club") + "</td>");
                                            } else {
                                                out.println("<td style=\"text-align:center\">N/A</td>");              
                                            }  
                                        }                                          
                                   
                                    } else {
                                        for (int j = 0; j < guest_cols; j++) {
                                            out.println("<td style=\"text-align:center\">N/A</td>");
                                        }
                                    }
                                }
                                
                                out.println("</tr>");

                            }       // end of sort by last name processing

                        } else {
                            no_name_count++;           // bump no name counter
                        }
                    } else {
                        no_name_count++;           // bump no name counter
                    }
                }
            }      // end of FOR loop for this tee time

        } // end while loop
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg(e.toString(), "", out, false);
        return;
    } finally {
        Connect.close(rs, pstmt);
    }
    
     //  if sorting by last name, then we need to build the table rows now
     if (sortby.equals("lname")) {      // sort by last name?

         try {

             //  Get the report entries in order of guests' last name
             pstmt = con.prepareStatement("SELECT * FROM guestreport ORDER BY lname, fname, mi");

             pstmt.clearParameters();
             rs = pstmt.executeQuery();

             while (rs.next()) {

                 mm = rs.getInt("mm");
                 dd = rs.getInt("dd");
                 yy = rs.getInt("yy");
                 hr = rs.getInt("hr");
                 min = rs.getInt("min");
                 host = rs.getString("host");
                 player = rs.getString("player");
                 fname = rs.getString("fname");
                 mi = rs.getString("mi");
                 lname = rs.getString("lname");
                 guest_id = rs.getInt("guest_id");

                 //  output the line
                 out.println("<tr>");
                 out.println("<td>" + mm + "/" + SystemUtils.ensureDoubleDigit(dd) + "/" + yy + "</td>"
                         + "<td>" + hr + ":" + SystemUtils.ensureDoubleDigit(min) + "</td>"
                         + "<td>" + host + "</td>"
                         + "<td>" + player + "</td>"
                         + "<td>" + fname + "</td>"
                         + "<td>" + mi + "</td>"
                         + "<td>" + lname + "</td>");
                 
                 
                 if (guest_tracking_enabled && !guestdb_config.isEmpty()) {
                     
                     if (guest_id != 0) {
                         try {

                             pstmt2 = con.prepareStatement("SELECT * FROM guestdb_data WHERE guest_id = ?");
                             pstmt2.clearParameters();
                             pstmt2.setInt(1, guest_id);

                             rs2 = pstmt2.executeQuery();

                             if (rs2.next()) {

                                 if (!guestdb_config.get("email").equals("N")) {
                                     out.println("<td>" + rs2.getString("email2") + "</td>");
                                     out.println("<td>" + rs2.getString("email2") + "</td>");
                                     out.println("<td>" + (rs2.getInt("emailOpt") == 1 ? "Yes" : "No") + "</td>");
                                 }

                                 if (!guestdb_config.get("phone").equals("N")) {
                                     out.println("<td>" + rs2.getString("phone1") + "</td>");
                                     out.println("<td>" + rs2.getString("phone2") + "</td>");
                                 }

                                 if (!guestdb_config.get("address").equals("N")) {
                                     out.println("<td>" + rs2.getString("address1") + "</td>");
                                     out.println("<td>" + rs2.getString("address2") + "</td>");
                                     out.println("<td>" + rs2.getString("city") + "</td>");
                                     out.println("<td>" + rs2.getString("state") + "</td>");
                                     out.println("<td>" + rs2.getString("zip") + "</td>");
                                 }

                                 if (!guestdb_config.get("gender").equals("N")) {
                                     out.println("<td>" + rs2.getString("gender") + "</td>");
                                 }

                                 if (!guestdb_config.get("hdcp_num").equals("N")) {
                                     out.println("<td>" + rs2.getString("hdcp_num") + "</td>");
                                 }

                                 if (!guestdb_config.get("hdcp_index").equals("N")) {
                                     out.println("<td>" + rs2.getDouble("hdcp_index") + "</td>");
                                 }

                                 if (!guestdb_config.get("home_club").equals("N")) {
                                     out.println("<td>" + rs2.getString("home_club") + "</td>");
                                 }
                             } else {
                                 out.println("<td colspan=\"" + guest_cols + "\">Guest data not found</td>");
                             }

                         } catch (Exception e) {
                             out.println("<td colspan=\"" + guest_cols + "\">Error locating guest info</td>");
                             Utilities.logError("Proshop_report_guest_names.doPost - " + club + " - Error printing out sorted guest info - Error: " + e.toString());
                         } finally {
                             Connect.close(rs2, pstmt2);
                         }
                     } else {
                         for (int j = 0; j < guest_cols; j++) {
                             out.println("<td style=\"text-align:center\">N/A</td>");
                         }
                     }
                 }
                 
                 out.println("</tr>");

             }   // end of while

         } catch (Exception e) {
             SystemUtils.buildDatabaseErrMsg(e.toString(), "", out, false);
         } finally {
             Connect.close(rs, pstmt);
         }
     }      // end of IF sort by last name 

     try {

         //  We are done with the table entries so delete them
         stmt = con.createStatement();
         stmt.executeUpdate("DELETE FROM guestreport");

     } catch (Exception e) {
         Utilities.logError("Proshop_report_guest_names.doPost - " + club + " - Error deleting entries from guestreport table - Error: " + e.toString());
     } finally {
         Connect.close(stmt);
     }

     out.println("</table>");

     out.println("<p align=center><br>There were " + no_name_count + " guests found without a name added.</p>");

     out.println("<center><BR><form method=get action=\"Proshop_report_guest_names\">");
     out.println("<input type=submit value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</form></center>");

     out.println("</body>");
     out.println("</html>");
     out.close();
 }

 
 
 //**************************************************
 // Used by the custom date range reports this routine
 // presents the user with date selection options
 // and posts the date back to request the report
 //**************************************************
 //
 private void getCustomDate(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;


    // our oldest date variables (how far back calendars go)
    long old_date = 0;
    int oldest_mm = 0;
    int oldest_dd = 0;
    int oldest_yy = 0;
    
    // lookup oldest date in teepast2 that has a pace status
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT MIN(date) FROM teepast2");

        if (rs.next()) {
           
            old_date = rs.getLong(1);
        }
        
    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Proshop_report_guest_names Error looking up oldest tee time.", e.getMessage(), out, false);
        return;
    }
    
           
   //
   //  Determine oldest date values - month, day, year
   //
   int oldest_date = (int)old_date;      
   oldest_yy = oldest_date / 10000;
   oldest_mm = (oldest_date - (oldest_yy * 10000)) / 100;
   oldest_dd = oldest_date - ((oldest_yy * 10000) + (oldest_mm * 100));
           
            
            
    // set calendar vars
    Calendar cal_date = new GregorianCalendar();
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);
    
    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    //out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

    // start main table for this page
    out.println("<table border=\"0\" align=\"center\">");
    out.println("<tr><td>");
    
    // output instructions
    out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
    out.println("<tr><td align=\"center\">");
    out.println("<font face=\"verdana\" color=\"#FFFFFF\" size=\"2\">");
    out.println("<font size=\"3\"><b>Guest Name Report</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("Only rounds before today will be included in the counts.<br><br>");
    out.println("Click on <b>Generate Report</b> to build the report.<BR><BR>" +
                "<b>NOTE:</b>  This report is most useful if you require names for guests.</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"Proshop_report_guest_names\" method=\"post\">");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");   

    // report button (go)
    out.println("<p align=\"center\"><input type=\"submit\" value=\"  Generate Report  \" style=\"background:#8B8970\"></p>");
    
    // end date submission form
    out.println("</form>");
    
    // output back button form
    out.println("<form method=\"get\" action=\"Proshop_announce\">");
     out.println("<p align=\"center\"><input type=\"submit\" value=\" Cancel \" style=\"background:#8B8970\"></p>");
    out.println("</form>");
    
    // end of main page table
    out.println("</td></tr></table>");
    
    // start calendar javascript setup code
    out.println("<script type=\"text/javascript\">");
    
     out.println("var g_cal_bg_color = '#F5F5DC';");
     out.println("var g_cal_header_color = '#8B8970';");
     out.println("var g_cal_border_color = '#8B8970';");
  
     out.println("var g_cal_count = 2;"); // number of calendars on this page
     out.println("var g_cal_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

    
     // set calendar date parts in js
     out.println("g_cal_month[0] = " + cal_month + ";");
     out.println("g_cal_year[0] = " + cal_year + ";");
     out.println("g_cal_beginning_month[0] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[0] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[0] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = " + cal_day + ";");
     out.println("g_cal_ending_year[0] = " + cal_year + ";"); 
    
     out.println("g_cal_month[1] = " + cal_month + ";");
     out.println("g_cal_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_month[1] = " + oldest_mm + ";");
     out.println("g_cal_beginning_year[1] = " + oldest_yy + ";");
     out.println("g_cal_beginning_day[1] = " + oldest_dd + ";");
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + cal_year + ";");

     out.println("function sd(pCal, pMonth, pDay, pYear) {");
     out.println(" f = document.getElementById(\"cal_box_\"+pCal);");
     out.println(" f.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
     out.println("}");
   
    out.println("</script>");

    out.println("<script type=\"text/javascript\">\n doCalendar('0');\n doCalendar('1');\n</script>");

 }
 

 //**************************************************
 // Convert minutes to nice human readable format 
 //**************************************************
 //
 private static String minToTime(int pMinutes) {

    return (pMinutes / 60) + "h " + SystemUtils.ensureDoubleDigit((pMinutes % 60)) + "min";
 }
 

 //**************************************************
 // Common Method for Displaying Values 
 //**************************************************
 //
 private String buildDisplayValue(int pSubTotal, int pGrandTotal, String pExcel) {

    String retVal = "";
    if (pSubTotal < 1 || pGrandTotal < 1) {
        return "" + nf.format(pSubTotal) + "</td><td>";
    } else {
        double tmp = (pSubTotal * 100) / pGrandTotal;
        if (pExcel.equals("")) {
            return nf.format(pSubTotal) + "</td><td><font size=\"2\">(" + ((tmp < 1) ? "<1" : nf.format(tmp)) + "%)";
        } else {
            return nf.format(pSubTotal) + "</td><td>";
        }
    }
    
 } // end buildDisplayValue
 
}  // end class