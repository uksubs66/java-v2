/***************************************************************************************
 *   Proshop_search_guests: This servlet will search for the selected guests that played during the
 *                               selected date range.  User can enter all or a portion of a
 *                               a guest type and/or guest name.
 *
 *
 *   Called by:     Tools tab menu option
 *
 *   Created:       7/31/2009
 *
 *
 *   Revisions:
 *
 *        9/15/09   Change Limited Access Proshop User calls for TS_VIEW to the "past"-specific version: TS_PAST_VIEW
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

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getClub;


public class Proshop_search_guests extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    NumberFormat nf = NumberFormat.getNumberInstance();

    boolean g_debug = false;     // change to true to enable debug mode
    
    
 
 //*****************************************************
 // Display a report selection menu page and offer help
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html"); 
    PrintWriter out = resp.getWriter(); 
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }

    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
        
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "TOOLS_SEARCHTS", con, out)) {
       SystemUtils.restrictProshop("TOOLS_SEARCHTS", out);
       return;
   }
   if (!SystemUtils.verifyProAccess(req, "TS_PAST_VIEW", con, out)) {
       SystemUtils.restrictProshop("TS_PAST_VIEW", out);
       return;
   }
   
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    out.println(SystemUtils.HeadTitle2("Guest Search"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");
    
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    
     
    // prompt user for the custom date range and guest name
    
    getCustomDate(req, out, con);    
    
    
    out.println("</body>");
    out.println("</html>");
    out.close();
    
 } // end of doGet routine


 //*****************************************************
 // Process the search request to compute the 
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
    ResultSet rs3 = null;
    
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
    String gname = "";
    String event = "";
    String course = "";
    
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
    int multi = 0;
    int i2 = 0;
    int teepast_id = 0;
    
    String [] playerA = new String [5];    
    String [] usergA = new String [5];    
    String [] gtypeA = new String [5];    
    int [] showA = new int [5];    
    
        
    resp.setHeader("Pragma","no-cache");                            // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                                // prevents caching at the proxy server
    resp.setContentType("text/html");                               
    PrintWriter out = resp.getWriter();                             // normal output stream
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) { return; }
    
    Connection con = SystemUtils.getCon(session);                   // get DB connection
    if (con == null) {
        SystemUtils.buildDatabaseErrMsg("Can not establish connection.", "", out, true);
        return;
    }
    
    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    if (req.getParameter("sdate") != null) {         // if dates provided from below
       
       start_date = (req.getParameter("sdate") != null) ? req.getParameter("sdate")  : "";
       end_date = (req.getParameter("edate") != null) ? req.getParameter("edate")  : "";
       
    } else {            // if not get them from calendar above

       start_date = (req.getParameter("cal_box_0") != null) ? req.getParameter("cal_box_0")  : "";
       end_date = (req.getParameter("cal_box_1") != null) ? req.getParameter("cal_box_1")  : "";
    }
    
    if (req.getParameter("gname") != null) {         // if guest type/name provided
       
       gname = req.getParameter("gname");    
    }
    
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    // handle excel output
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        }
    }
    catch (Exception exc) {
    }

    out.println(SystemUtils.HeadTitle2("Guest Search List"));
   
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\">");

    
    if (!excel.equals("yes")) {                // if not excel
    
       SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }
    
    //
    //  Use the custom date range provided to generate the report
    //

   // check to see if the guest string is here, and if not then jump to display calendar routine
   if (gname.equals("")) {
       getCustomDate(req, out, con); 
       return;
   }

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

    try {
         //
         //  Determine if multiple courses are supported
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
         }
         stmt.close();

    } catch (Exception e) {
        SystemUtils.buildDatabaseErrMsg("Error getting club options.", e.getMessage(), out, false);
        return;
    }

    //
    //  we now have our date range - generate the report
    //
    if (g_debug) out.println("<!-- sdate=" + sdate + " | edate=" + edate + " | gname=" + gname + " -->");
    
            
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

    // setup our report header
    out.println("<center><font size=+2>Guest Search List<br></font>" + 
            "<font size=+1><nobr>From " + range_begin + "</nobr> thru <nobr>" + range_end + "</nobr>" +
            "<br>Search for " +gname+ "</font></center>");
   
    if (!excel.equals("yes")) {     // if normal request

        out.println("<center><form method=post action=\"/"+rev+"/servlet/Proshop_search_guests\" name=frmRequestExcel id=frmRequestExcel target=\"_blank\">");
        out.println("<input type=hidden name=sdate value=\"" + sdate + "\">");
        out.println("<input type=hidden name=edate value=\"" + edate + "\">");
        out.println("<input type=hidden name=excel value=\"yes\">");
        out.println("<input type=submit value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
    }
   
    //  table heading
    out.println("<br><table bgcolor=\"#F5F5DC\" align=center border=1 cellpadding=6>");
    out.println("<tr bgcolor=\"#336633\">");
      out.println("<td><font color=white><b>Date</b></font></td>" +
                  "<td><font color=white><b>Time</b></font></td>");
      if (multi > 0) {
         out.println("<td><font color=white><b>Course</b></font></td>");
      }
      out.println("<td><font color=white><b>Event</b></font></td>" +
                  "<td><font color=white><b>Hosting Member</b></font></td>" +
                  "<td><font color=white><b>Guest (from Tee Time)</b></font></td>");
    out.println("</tr>");
    

    //
    //   Add a % to the name provided so search will match anything close
    //
    StringBuffer buf = new StringBuffer("%");
    buf.append( gname );
    buf.append("%");
    gname = buf.toString();

   
    // build and display the report data
    try {
     
       /*
        pstmt = con.prepareStatement("" +
                "SELECT teepast_id, mm, dd, yy, hr, min, player1, player2, player3, player4, player5, " +
                "show1, show2, show3, show4, show5, IFNULL(userg1,''), IFNULL(userg2,''), " +
                "IFNULL(userg3,''), IFNULL(userg4,''), IFNULL(userg5,''), " +
                "gtype1, gtype2, gtype3, gtype4, gtype5, event, courseName " +
                "FROM teepast2 " +
                "WHERE date >= ? AND date <= ? AND " +
                "((player1 LIKE ? AND gtype1 != '' AND gtype1 != NULL) OR (player2 LIKE ? AND gtype2 != '' AND gtype2 != NULL) OR " +
                "(player3 LIKE ? AND gtype3 != '' AND gtype3 != NULL) OR (player4 LIKE ? AND gtype4 != '' AND gtype4 != NULL) OR " +
                "(player5 LIKE ? AND gtype5 != '' AND gtype5 != NULL)) " +
                "ORDER BY date, time");
        */
                
        pstmt = con.prepareStatement("" +
                "SELECT teepast_id, mm, dd, yy, hr, min, player1, player2, player3, player4, player5, " +
                "show1, show2, show3, show4, show5, IFNULL(userg1,''), IFNULL(userg2,''), " +
                "IFNULL(userg3,''), IFNULL(userg4,''), IFNULL(userg5,''), " +
                "gtype1, gtype2, gtype3, gtype4, gtype5, event, courseName " +
                "FROM teepast2 " +
                "WHERE date >= ? AND date <= ? AND " +
                "((player1 LIKE ? AND gtype1 != '') OR (player2 LIKE ? AND gtype2 != '') OR " +
                "(player3 LIKE ? AND gtype3 != '') OR (player4 LIKE ? AND gtype4 != '') OR " +
                "(player5 LIKE ? AND gtype5 != '')) " +
                "ORDER BY date, time");
                
        pstmt.clearParameters();
        pstmt.setLong(1, sdate);
        pstmt.setLong(2, edate);
        pstmt.setString(3, gname);
        pstmt.setString(4, gname);
        pstmt.setString(5, gname);
        pstmt.setString(6, gname);
        pstmt.setString(7, gname);
        
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            teepast_id = rs.getInt("teepast_id");
            mm = rs.getInt("mm");
            dd = rs.getInt("dd");
            yy = rs.getInt("yy");
            hr = rs.getInt("hr");
            min = rs.getInt("min");
            playerA[0] = rs.getString("player1");
            playerA[1] = rs.getString("player2");
            playerA[2] = rs.getString("player3");
            playerA[3] = rs.getString("player4");
            playerA[4] = rs.getString("player5");
            showA[0] = rs.getInt("show1");
            showA[1] = rs.getInt("show2");
            showA[2] = rs.getInt("show3");
            showA[3] = rs.getInt("show4");
            showA[4] = rs.getInt("show5");
            usergA[0] = rs.getString(17);
            usergA[1] = rs.getString(18);
            usergA[2] = rs.getString(19);
            usergA[3] = rs.getString(20);
            usergA[4] = rs.getString(21);
            gtypeA[0] = rs.getString("gtype1");
            gtypeA[1] = rs.getString("gtype2");
            gtypeA[2] = rs.getString("gtype3");
            gtypeA[3] = rs.getString("gtype4");
            gtypeA[4] = rs.getString("gtype5");
            event = rs.getString("event");
            course = rs.getString("CourseName");
            
            if (g_debug) out.println("<BR>Tee time found, id=" + teepast_id);
    
                        
            
           //
           //  We have a tee time that contains one or more guests that matched the search criteria.
           //  Now we want to list the matches individually so we need to locate them within the tee time.
           //
           for (int i=0; i<5; i++) {            // check each player for a matching guest
              
              i2 = i + 1;  
               
              if (!gtypeA[i].equals("") && !gtypeA[i].equals( null ) && showA[i] == 1) {     // if guest and checked in
                              
                 //  see if this guest is the one we are looking for
                 pstmt2 = con.prepareStatement("" +
                      "SELECT mm " +
                      "FROM teepast2 " +
                      "WHERE teepast_id = ? AND player" +i2+ " LIKE ?");
           
                 pstmt2.clearParameters();
                 pstmt2.setInt(1, teepast_id);
                 pstmt2.setString(2, gname);

                 rs2 = pstmt2.executeQuery();

                 if (rs2.next()) {

                     if (g_debug) out.println("<BR>Player found, id=" + teepast_id);
    
                     //  guest matches our search criteria
                     if (!usergA[i].equals("")) {               // if owner is assigned

                        if (!usergA[i].equals(last_userg)) {    // if owner is not same as last

                           try {

                              pstmt3 = con.prepareStatement (
                                  "SELECT name_last, name_first, name_mi " +
                                  "FROM member2b WHERE username = ?");

                              pstmt3.clearParameters();         
                              pstmt3.setString(1, usergA[i]);       
                              rs3 = pstmt3.executeQuery();      

                              if (rs3.next()) {

                                 // Get the member's full name.......

                                 StringBuffer mem_name = new StringBuffer(rs3.getString("name_first"));  // get first name

                                 mid = rs3.getString("name_mi");                                // middle initial
                                 if (!mid.equals( "" )) {
                                    mem_name.append(" ");
                                    mem_name.append(mid);
                                 }
                                 mem_name.append(" " + rs3.getString("name_last"));    // last name

                                 host = mem_name.toString();                          // convert to one string

                              } else {

                                 host = "unknown";
                              }

                              pstmt3.close();

                              last_host = host;          // save this name

                              last_userg = usergA[i];    // save this uername

                           } catch (Exception ignore) {
                              host = "unknown";
                           }

                        } else {

                           host = last_host;  
                        }                           

                     } else {

                        host = "unknown";
                     }

                     //  output the line
                     out.println("<tr>");
                     out.println("<td>" +mm+ "/" + SystemUtils.ensureDoubleDigit(dd) + "/" +yy+ "</td>" +
                              "<td>" +hr+ ":" + SystemUtils.ensureDoubleDigit(min) + "</td>");
                     if (multi > 0) {
                        out.println("<td>" +course+ "</td>");
                     }
                     out.println("<td>" +event+ "</td>" +
                              "<td>" +host+ "</td>" +
                              "<td>" +playerA[i]+ "</td>" +
                              "</tr>");
                                               
                    rcount++;         // track # of records searched
            
                 }    // end of IF matching guest
                 
               }      // end of IF guest and checked in  
            
            }     // end of for loop
                        
            pstmt2.close();
            
        } // end while loop
        
        pstmt.close();
                
        out.println("</table>");
                        
        out.println("<p align=center><br>There were " + rcount+ " guests found that matched the search criteria.</p>");
        
        out.println("<center><BR><form method=get action=\"/"+rev+"/servlet/Proshop_search_guests\">");
        out.println("<input type=submit value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
        
        out.println("</body>");
        out.println("</html>");
        out.close();
       
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg(exc.toString(), "", out, false);
        return;
    }

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
        SystemUtils.buildDatabaseErrMsg("Proshop_search_guests Error looking up oldest tee time.", e.getMessage(), out, false);
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
    cal_date.add(Calendar.DATE,-1);                     // get yesterday's date (no teepast entries for today)
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
    out.println("<font size=\"3\"><b>Guest Search</b></font><br>");
    out.println("<br>Select the date range below.<br>");
    out.println("Then enter the desired guest type and/or guest name for the search.<br><br>");
    out.println("You may enter all or any part of the guest type or name.<br>");
    out.println("<b>For instance, 'Reg Gst Joe Smith' or just 'Smith' (do not enter the quotes).</b><br><br>");
    out.println("Click on <b>Search</b> to start the search.</font></td></tr>");
    out.println("</table><br>");

    // start date submission form
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_search_guests\" method=\"post\">");

    // output table that hold calendars and their related text boxes
    out.println("<table align=center border=0>\n<tr valign=top>\n<td align=center>");
     out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_0 id=cal_box_0>");
     out.println("</td>\n<td>&nbsp; &nbsp;</td>\n<td align=center>");
     out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");
     out.println(" <input type=text name=cal_box_1 id=cal_box_1>");
    out.println("</td>\n</tr></table>\n");   

    //  output the guest type/name text box    
    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
      out.println("<tr><td width=\"250\" align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<p align=\"center\"><br>Guest Type and/or Name: &nbsp;");
      out.println("<input type=\"text\" name=\"gname\" size=\"20\" maxlength=\"43\">");
      out.println("");
      out.println("");
    out.println("</p></font></td></tr></table>");
    
    // search button (go)
    out.println("<p align=\"center\"><input type=\"submit\" value=\"  Search  \" style=\"background:#8B8970\"></p>");
    
    // end submission form
    out.println("</form>");
    
    // output back button form
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
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