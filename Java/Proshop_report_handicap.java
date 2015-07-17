/***************************************************************************************
 *   Proshop_report_handicap:   This servlet will implement the hdcp peer reporting functionality
 *
 *
 *   Called by:     Menu tabs and self
 *
 *
 *   Created:       02/14/2007
 *
 *
 *   Revisions:     
 *
 *        4/28/10   Made lookupHistory a public method so we can call it from the members peer review report
 *        4/20/10   Added hyperlinks to member names in the getNonPosters summary report that link to the individual member report
 *                  also updated viewSavedScores to include the unmatched score postings we found and a summary total of
 *                  rounds played vs. rounds posted during this period at the bottom of the table
 *        9/02/09   Added a copy set of Excel/Home/Back buttons to the top of two reports
 *        9/02/09   Added rptType field to Excel button form so the form printed to the excel document is the same format as originally displayed
 *        8/06/09   Changed SQL statement in viewSavedScores to return 40 scores and no 'AI' types
 *        7/30/09   Updated SQL statement in doNonPosters to match better on ghin numbers by casting to int
 *        5/29/09   Added new Missing Postings By Member Report
 *        3/25/09   Changed viewSavedScores to forget posted scores once displayed
 *       11/14/08   Change viewSavedScores so first loop doesn't grab Away scores
 *       09/02/08   Javascript compatability updates
 *       07/31/08   Added score type and 9/18 hole columns to the report
 *       07/18/08   Added limited access proshop users checks
 *       07/16/08   Removed text referring to List All Members button and minor browser fixes
 *       06/20/07   Fixed blank report problem if no posted scores found for member
 *       06/04/07   Added Excel export buttons
 *
 *                  
 *                  
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
//import java.text.SimpleDateFormat;


public class Proshop_report_handicap extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    DateFormat df_full = DateFormat.getDateInstance(DateFormat.MEDIUM);
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

     
     
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    
    PrintWriter out = resp.getWriter();

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
        SystemUtils.restrictProshop("REPORTS", out);
        return;
    }
    
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String club = (String)session.getAttribute("club");
    String templott = (String)session.getAttribute("lottery");
    int lottery = Integer.parseInt(templott);
    
    // START DEFAULT PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    if (!excel.equals("yes")) SystemUtils.getProshopSubMenu(req, out, lottery);
    
    
    // MAKE SURE THIS CLUB HAS A HDCP SYSTEM ENABLED
    String hdcpSystem = "";
    try {

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT hdcpSystem FROM club5");
        if (rs.next()) hdcpSystem = rs.getString("hdcpSystem");
    
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        return;
    }
    
    // MAKE SURE CLUB IS SETUP TO USE HDCP FEATURE
    if (hdcpSystem.equals("") || hdcpSystem.equalsIgnoreCase("other")) {
        
        out.println("<br><br><p align=center><b><i>Your club does not have a handicap system that allows online access.</i></b></p>");
        return;
    }
    
    // DONE VALIDATING THIS REQUEST - WE'RE READY TO GO
    
    
    // HANDLE POST SCORE REQUEST
    if (todo.equals("nonPosters")) { //  || req.getParameter("missingPosts") != null
        getNonPosters(req, session, con, out);
    } else if (todo.equals("view")) {
        out.println("<br><br><p align=center><b><i>Something is wrong.  Missing data.</i></b></p>");
        return;
        //viewSavedScores(req, club, session, con, out);
    } else if (todo.equals("view2")) {
        viewTeeSheetPostings(req, session, con, out);
    } else {
        getViewReport(req, session, con, out);
    }
    
    out.println("</body></html>");
    out.close();
    
 } // end of doGet routine
 
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
     
     
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    
    PrintWriter out = resp.getWriter();

    String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";
    
    // set response content type
    try{
        if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
            resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
        } else {
            resp.setContentType("text/html");
        }
    }
    catch (Exception exc) {
    }
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String name = (req.getParameter("name") == null) ? "" : req.getParameter("name");
    String club = (String)session.getAttribute("club");
    String templott = (String)session.getAttribute("lottery");
    int lottery = Integer.parseInt(templott);
    
    // START DEFAULT PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    
    // HANDLE REPORT REQUEST
    //if (todo.equals("view")) {
    if (todo.equals("doNonPosters")) {
        doNonPosters(req, session, con, out);
    } else if (!name.equals("") && !todo.equals("")) {
        //out.println("name="+name);
        viewSavedScores(req, name, session, con, out);
    } else {
        getViewReport(req, session, con, out);
    }
    
    out.println("</body></html>");
    
 } // end of doPost routine
 
 
 //
 // This is the Handicap Peer Review Report that is accessed from the Reports menu
 //
 private void viewSavedScores(HttpServletRequest req, String user, HttpSession session, Connection con, PrintWriter out) {
    
    
    // Get the hdcp number from the username
    String hdcp_num = Common_handicaps.getHdcpNum(user, con);
    String fullName = "";
    String tee_name = "";

    ArrayList<Integer> posting_dates = new ArrayList<Integer>(20);
    ArrayList<Integer> posting_scores = new ArrayList<Integer>(20);
    ArrayList<Integer> posting_used = new ArrayList<Integer>(20);
    ArrayList<String> posting_types = new ArrayList<String>(20);
    ArrayList<String> posting_tees = new ArrayList<String>(20);
    
    int multi = 0;
    int last_date = 0;
    
    try {

        // get the last 20 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT (SELECT multi FROM club5) AS multi, CONCAT(name_first, ' ', name_last) AS fullName FROM member2b WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        
        if (rs.next()) {
            fullName = rs.getString("fullName");
            multi = rs.getInt("multi");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    out.println("<p align=center><font size=5>Handicap Peer Review Report</font>");
    
    out.println("<br><br><font size=4><i>For " + fullName + " (" + hdcp_num + ")</i></font></p><br>");
    
    out.println("<table align=center>");
    out.println("<table width=400 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    //"<td align=center colspan=2><font size=3 color=white><b>Member</b></font></td>" +
                    "<td align=center colspan=" + ((multi == 0) ? "2" : "3") + "><font size=3><b>Rounds Played</b></font></td>" +
                    "<td align=center colspan=4><font size=3><b>Scores Posted</b></font></td>" +
                "</tr>");
                
    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" + // #CCCCAA
                    "<td align=center>Date</td>" +
                    ((multi == 0) ? "" : "<td align=center>Course</td>") +
                    "<td align=center>9/18</td><td align=center>Date</td>" +
                    "<td align=center>Score</td><td align=center>Type</td>" +
                    "<td align=center>Tees</td>" +
                "</tr>");
    
    
    try {
        // get the last 40 scores posted
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT DATE_FORMAT(sp.date, '%Y%m%d') AS date, sp.score, sp.type, t.tee_name " +
                "FROM score_postings sp " +
                "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
                "WHERE hdcpNum = ? AND type <> 'A' AND type <> 'AI' " +
                "ORDER BY date DESC LIMIT 40;");
        
        pstmt.clearParameters();
        pstmt.setString(1, hdcp_num);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            posting_dates.add(rs.getInt("date"));
            posting_scores.add(rs.getInt("score"));
            posting_types.add(rs.getString("type"));
            posting_used.add(0); // default it to not used
            if (rs.getString("tee_name") != null) {
                posting_tees.add(rs.getString("tee_name"));
            } else {
                posting_tees.add("N/A");
            }
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    try {
        
        // get the last 20 rounds played
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT date, courseName, " +
                    "IF(" +
                        "(username1 = ? && p91 = 1) || " +
                        "(username2 = ? && p92 = 1) || " +
                        "(username3 = ? && p93 = 1) || " +
                        "(username4 = ? && p94 = 1) || " +
                        "(username5 = ? && p95 = 1), 1, 0) " +
                    "AS 9hole " +
                "FROM teepast2 " +
                "WHERE " +
                "(" +
                    "(username1 = ? && show1 = 1) || " +
                    "(username2 = ? && show2 = 1) || " +
                    "(username3 = ? && show3 = 1) || " +
                    "(username4 = ? && show4 = 1) || " +
                    "(username5 = ? && show5 = 1)" +
                ") " +
                "ORDER BY date DESC " +
                "LIMIT 20;");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        pstmt.setString(2, user);
        pstmt.setString(3, user);
        pstmt.setString(4, user);
        pstmt.setString(5, user);
        pstmt.setString(6, user);
        pstmt.setString(7, user);
        pstmt.setString(8, user);
        pstmt.setString(9, user);
        pstmt.setString(10, user);
        ResultSet rs = pstmt.executeQuery();
        
        int post_date = 0;
        int play_date = 0;
        int score = 0;
        //int value = 0;
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int i = 0;
        int temp = 0;
        int holes = 0;
        int used = 0;
        String course = "";
        String post_sdate = "";
        String play_sdate = "";
        String type = "";
        String tees = "";
        boolean found = false;
        
        while (rs.next()) {
            
            play_date = rs.getInt("date");
            course = rs.getString("courseName");
            holes = (rs.getInt("9hole") == 1) ? 9 : 18;
            found = false;           
            // see if there is a posting for this round
            for (i = 0; i < posting_dates.size(); i++) {
                
                post_date = (Integer)posting_dates.get(i).intValue();
                score = (Integer)posting_scores.get(i).intValue();
                type = (String)posting_types.get(i);
                tees = (String)posting_tees.get(i);
                used = (Integer)posting_used.get(i).intValue();

                yy = post_date / 10000;                     
                temp = yy * 10000;
                mm = post_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                post_sdate = mm + "/" + dd + "/" + yy;
                   
                yy = play_date / 10000;                     
                temp = yy * 10000;
                mm = play_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                play_sdate = mm + "/" + dd + "/" + yy;
                if (post_date == play_date && used == 0) { //  bgcolor=#F5F5DC   bgcolor=#86B686

                    posting_used.set(i, 1);
                    out.println("<tr align=\"center\" bgcolor=\"#86B686\">" +
                            "<td nowrap>" + play_sdate + "</td>" +
                            ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                            "<td>" + holes + "</td> <td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");
                    found = true;
                    break;
                    
                } // end if matching dates
            
            } // end for loop
            
            // if no posted score was found for this round
            if (!found) { // && i != 0) {
                yy = play_date / 10000;
                temp = yy * 10000;
                mm = play_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                play_sdate = mm + "/" + dd + "/" + yy;
                out.println("<tr align=\"center\"><td nowrap>" + play_sdate + "</td>" +
                        ((multi == 0) ? "" : "<td nowrap>" + course + "</td>") +
                        "<td>" + holes + "</td> <td colspan=\"4\" bgcolor=\"#FFFF8F\">&nbsp;</td></tr>");
            }

            last_date = play_date; // remember the last date of play
            
        } // end while

        boolean didHeader = false;

        for (i = 0; i < posting_dates.size(); i++) {

            post_date = (Integer)posting_dates.get(i).intValue();
            used = (Integer)posting_used.get(i).intValue();

            if (post_date >= last_date && used == 0) {

                score = (Integer)posting_scores.get(i).intValue();
                type = (String)posting_types.get(i);
                tees = (String)posting_tees.get(i);

                yy = post_date / 10000;
                temp = yy * 10000;
                mm = post_date - temp;
                temp = mm / 100;
                temp = temp * 100;
                dd = mm - temp;
                mm = mm / 100;
                post_sdate = mm + "/" + dd + "/" + yy;

                if (!didHeader) {

                    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                                "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Unmatched Score Postings</td></tr>");
                    didHeader = true;
                }

                out.println("<tr align=\"center\">" +
                        "<td bgcolor=#FFFF8F colspan=\"" + ((multi == 0) ? "2" : "3") + "\">&nbsp;</td>" +
                        "<td nowrap>" + post_sdate + "</td><td>" + score + "</td><td>" + type + "</td><td>" + tees + "</td></tr>");

            } // end unmatched postings loop

        } // end for loop

        int [] rounds = new int[3];
        int today = (int)SystemUtils.getDate(con);
        rounds = lookupHistory(last_date, today, user, con, out);
/*
        yy = last_date / 10000;
        temp = yy * 10000;
        mm = last_date - temp;
        temp = mm / 100;
        temp = temp * 100;
        dd = mm - temp;
        mm = mm / 100;
        post_sdate = mm + "/" + dd + "/" + yy;
        // Total Rounds Played vs. Scores Posted<br>From " + post_sdate + " Through Yesterday
*/
        out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td colspan=\"" + ((multi == 0) ? "6" : "7") + "\" align=\"center\">Rounds Played vs. Scores Posted<br><font size=2>(for last twenty rounds)</font></td></tr>");

        out.println("<tr bgcolor=\"#86B686\">" +
                "<td align=\"center\" colspan=\"" + ((multi == 0) ? "2" : "3") + "\">9 hole: <b>" + rounds[0] + "</b> &nbsp; &nbsp; 18 hole: <b>" + rounds[1] + "</b></td>" +
                "<td align=\"center\" colspan=\"4\">Posted: <b>" + rounds[2] + "</b></td></tr>");

    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    out.println("</table>");
     
    out.println("<br><br>");
        
    //out.println("<center>" +  + "</center>");
        

    if (req.getParameter("popup") != null) {

        out.println("<center><button onclick=\"window.close()\">Close Window</button></center>");

    } else {

        if (req.getParameter("excel") == null) {     // if normal request
            out.println("<center><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
            out.println("<input type=\"hidden\" name=\"name\" value=\"" + user + "\">");
            out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></center>");

            out.println("" +
                "<table align=center><tr>" +
                "<td><form><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
                "<td>&nbsp; &nbsp; &nbsp;</td>" +
                "<td><form action=/" + rev + "/servlet/Proshop_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
                "</tr></table>");
        } // end if not excel

    }

 }    
  
 
 private void getViewReport(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {

   //Statement stmt = null;
   ResultSet rs = null;

   //int thisYear = 0;
   //int calYear = 0;
   //int firstYear = 0;

   String name = "";
   String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

   if (req.getParameter("name") != null) {        // if user specified a name to search for

      name = req.getParameter("name");            // name to search for

      //if (req.getParameter("allmems") != null) name = "ALL"; // is List All submit button sent, then change to ALL
      
      if (!name.equals( "" )) {
         
         name = SystemUtils.getUsernameFromFullName(name, con);  
         viewSavedScores(req, name, session, con, out);
         return;
      }
      
   }

   out.println("<script type=\"text/javascript\">");
   out.println("<!--");
   out.println("function cursor() { document.forms['f'].name.focus(); }");
   out.println("function movename(name) {");
   out.println(" document.forms['f'].name.value = name;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");
      
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

    out.println("<p align=center><font size=5>Individual Peer Review Report</font></p>");

   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=\"600\">");
   out.println("<tr><td>");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<p>To review recent postings for a member, enter the name, ");
      out.println("or use the Member List to select the first letter of their last name.&nbsp; ");
      out.println("This will search for all names that start with the letter you select.&nbsp; ");
/*      out.println("You may also lookup all members by clicking the 'List All Members' button.&nbsp; " +
              "Check the 'Include Last 20 Postings' to see the members last 20 scores posted.<br>" +
              "<i>Note: Selecting all members will disregard the last 20 postings option.</i>"); 
*/
      out.println("</p></font>");
   out.println("</td></tr></table>");

   out.println("<font size=\"2\" face=\"Courier New\">");
   out.println("<p align=\"center\">(Click on <b>'Member List'</b> on right to view a list of members)</p>");
   out.println("<br></font>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_report_handicap\" method=\"post\" target=\"bot\" name=\"f\">");
   //out.println("<input type=\"hidden\" name=\"todo\" value=\"view\">");
   out.println("<input type=\"hidden\" name=\"todo\" value=" + todo + ">");

   out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
         out.println("<td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Name: &nbsp;");
                     out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\">");
                     out.println("</input>");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"search\">");
                  out.println("</p>");
                  
                  //out.println("<input type=checkbox name=inc20 value=yes> Include last 20 postings<br><br>");
                  
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");
/*
            // add a button to list all members tee times
            //
            out.println("<br><br>");
            out.println("<input type=\"submit\" value=\"Missing Postings By Member\" name=\"missingPosts\" onclick=\"document.forms['f'].method='get'\">");
*/
         out.println("</td>");

   if (req.getParameter("letter") != null) {     // if user clicked on a name letter

      String letter = req.getParameter("letter");      // get the letter
      letter = letter + "%";

      String first = "";
      String mid = "";
      String last = "";
      name = "";
      String wname = "";
      String dname = "";

         out.println("<td valign=\"top\" align=\"center\">");
         out.println("<table border=\"1\" width=\"140\" bgcolor=\"#F5F5DC\">");      // name list
         out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Name List</b>");
               out.println("</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println("Click on name to add");
            out.println("</font></td></tr>");

         try {

            PreparedStatement stmt2 = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi " +
                    "FROM member2b " +
                    "WHERE ghin <> '' AND name_last LIKE ? " +
                    "ORDER BY name_last, name_first, name_mi");

            stmt2.clearParameters();               // clear the parms
            stmt2.setString(1, letter);            // put the parm in stmt
            rs = stmt2.executeQuery();             // execute the prepared stmt

            out.println("<tr><td align=\"left\"><font size=\"2\">");
            out.println("<select size=\"8\" name=\"bname\" onClick=\"movename(this.form.bname.value)\">");

            while(rs.next()) {

               last = rs.getString(1);
               first = rs.getString(2);
               mid = rs.getString(3);

               if (mid.equals("")) {

                  name = first + " " + last;
                  dname = last + ", " + first;
               } else {

                  name = first + " " + mid + " " + last;
                  dname = last + ", " + first + " " + mid;
               }

               out.println("<option value=\"" + name + "\">" + dname + "</option>");
            }

            out.println("</select>");
            out.println("</font></td></tr>");

            stmt2.close();
         }
         catch (Exception ignore) {

         }
         out.println("</table>");

         out.println("</td>");   // end of name list column
           
   } else {
      out.println("<td valign=\"top\" width=\"30\">");
      out.println("&nbsp;");  
      out.println("</td>");   // end of empty column
     
   }  // end of if Letter

         out.println("<td valign=\"top\" align=\"center\">");  
            out.println("<table border=\"2\" align=\"center\" bgcolor=\"#F5F5DC\">");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\" bgcolor=\"#336633\">");
                     out.println("<font color=\"#FFFFFF\" size=\"2\">");
                     out.println("<b>Member List</b>");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                     out.println("<font size=\"2\">Name begins with:");
                     out.println("</font>");
                  out.println("</td>");
               out.println("</tr>");
               out.println("<tr>");
                  out.println("<td align=\"center\"><font size=\"1\">");
                     out.println("<input type=\"submit\" value=\"A\" name=\"letter\"></font></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"B\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"C\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"D\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"E\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"F\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"G\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"H\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"I\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"J\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"K\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"L\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"M\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"N\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"O\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"P\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Q\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"R\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"S\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"T\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"U\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"V\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"W\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"X\" name=\"letter\"></td>");
               out.println("</tr>");

               out.println("<tr>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Y\" name=\"letter\"></td>");
                  out.println("<td align=\"center\">");
                     out.println("<input type=\"submit\" value=\"Z\" name=\"letter\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
                  out.println("<td align=\"center\"></td>");
               out.println("</tr>");
            out.println("</table>");
         out.println("</td>");
      out.println("</tr>");
      out.println("</table>");
      out.println("</select></font>");
      out.println("</td></tr></table>");
      out.println("</form>");
/*
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
*/
    out.println("</font>");
    
 }
 
 
 //
 // This is the Posted Scores report that is accessed from old tee sheets
 //
 private void viewTeeSheetPostings(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {
    
     
    //String fullName = "";
    //String tee_name = "";
    String sdate = "";
    String course = "";
    String tmp_holes = "";
    
    course = req.getParameter("course");
    sdate = req.getParameter("date");
    
    int index = 0;
    long date = 0;
    int multi = 0;
    
    try {

        date = Integer.parseInt(sdate);
        
        index = SystemUtils.getIndexFromToday(date, con);
        
        PreparedStatement pstmt = con.prepareStatement("SELECT multi, DATE_FORMAT(?, '%m-%d-%Y') AS sdate FROM club5;");
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        ResultSet rs = pstmt.executeQuery();
        
        if ( rs.next() ) {
            sdate = rs.getString("sdate");
            multi = rs.getInt("multi");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading member data for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    
    
    out.println("<br><p align=center><font size=5>Handicap Posting Review Report</font>");
    
    out.println("<br><br><font size=4>For " + sdate + ((multi == 0) ? "" : " on " + course) + "</font></p><br>");

    if (req.getParameter("excel") == null) {     // if normal request

        out.println("<center><form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=/" + rev + "/servlet/Proshop_oldsheets method=post>" +
            "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">" +
            "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">" +
            "<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=/" + rev + "/servlet/Proshop_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
    } // end if not excel
    
    //out.println("<table align=center>");
    out.println("<table width=400 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td align=center><font size=3><b>Member</b></font></td>" +
                    "<td align=center><font size=3><b>Time</b></font></td>" +
                    "<td align=center nowrap><font size=3><b>9 / 18</b></font></td>" +
                    "<td align=center><font size=3><b>Tees</b></font></td>" +
                    "<td align=center><font size=3><b>Type</b></font></td>" +
                    "<td align=center><font size=3><b>Posted Score</b></font></td>" +
                "</tr>");
    
                //    ((multi == 0) ? "" : "<td align=center>" + course + "</td>") +
                            
    try {
        
        // all posted scores for players on given day from old tee sheets
        PreparedStatement pstmt = con.prepareStatement("" +
                "SELECT tp.time, tp.p91, tp.p92, tp.p93, tp.p94, tp.p95, sp.score, sp.type, t.tee_name, " +
                    "CONCAT(m.name_last, ', ', m.name_first) AS fullName, " +
                    "IF(m.username=tp.username1, 1, 0) AS pos1, " +
                    "IF(m.username=tp.username2, 1, 0) AS pos2, " +
                    "IF(m.username=tp.username3, 1, 0) AS pos3, " +
                    "IF(m.username=tp.username4, 1, 0) AS pos4, " +
                    "IF(m.username=tp.username5, 1, 0) AS pos5 " +
                "FROM teepast2 tp, member2b m " +
                "LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date = ? " +
                "LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id " +
                "WHERE " +
                    "tp.date = ? AND " +
                    ((multi==1) ? "tp.courseName = ? AND " : "") +
                    "m.ghin <> '' AND " +
                    "( " +
                        "(tp.show1 = 1 AND tp.username1 = m.username) OR " +
                        "(tp.show2 = 1 AND tp.username2 = m.username) OR " +
                        "(tp.show3 = 1 AND tp.username3 = m.username) OR " +
                        "(tp.show4 = 1 AND tp.username4 = m.username) OR " +
                        "(tp.show5 = 1 AND tp.username5 = m.username) " +
                    ") " +
                "ORDER BY fullName, time;");
        
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, date);
        if (multi == 1) pstmt.setString(3, course);
        ResultSet rs = pstmt.executeQuery();
        
        while ( rs.next() ) {
            
            tmp_holes = "18";
            
            if (rs.getInt("p91") + rs.getInt("pos1") == 2 || 
                rs.getInt("p92") + rs.getInt("pos2") == 2 || 
                rs.getInt("p93") + rs.getInt("pos3") == 2 || 
                rs.getInt("p94") + rs.getInt("pos4") == 2 || 
                rs.getInt("p95") + rs.getInt("pos5") == 2) {
                
                tmp_holes = "9";
            }
            
            out.println("<tr align=center bgcolor=" + ((rs.getInt("score") != 0) ? "#86B686" : "#FFFF8F") + ">" +
                        "<td nowrap>" + rs.getString("fullName") + "</td>" +
                        "<td nowrap>" + SystemUtils.getSimpleTime(rs.getInt("time") / 100, rs.getInt("time") % 100) + "</td>" +
                        "<td>" + tmp_holes + "</td>" +
                        "<td nowrap>" + ((rs.getString("tee_name") == null) ? "&nbsp;" : rs.getString("tee_name")) + "</td>" +
                        "<td>" + ((rs.getString("type") == null) ? "&nbsp;" : rs.getString("type")) + "</td>" +
                        "<td>" + ((rs.getInt("score") == 0) ? "&nbsp;" : rs.getInt("score")) + "</td>" +
                        "</tr>");
        
                        //  ((multi == 0) ? "" : "<td nowrap>" + rs.getString("course") + "</td>") +
                                
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for handicap peer report.", exc.getMessage(), out, false);
        return;
    }
    
    
    out.println("</table>");
    
    if (req.getParameter("excel") == null) {     // if normal request
        
        out.println("<br><br>");
        
        out.println("<center><form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"view2\">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
        
        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=/" + rev + "/servlet/Proshop_oldsheets method=post>" +
            "<input type=\"hidden\" name=\"date\" value=\"" + date + "\">" +
            "<input type=\"hidden\" name=\"course\" value=\"" + course + "\">" +
            "<input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=/" + rev + "/servlet/Proshop_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
    } // end if not excel
    
 } 
 
 
private void getNonPosters(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {

    Statement stmt = null;
    ResultSet rs = null;

    // summary report can be sorted by name or by # of missing postings
    // detail report can be sorted by name or by oldest missing posting
    // (summary can't sort by oldest round and detail can't sort by count)

    out.println("<script type=\"text/javascript\">");
    out.println("function updateSorts(selectedIndex) {");
    out.println(" var optSort = document.forms['frmHdcpRpt'].sort;");
    out.println(" if (optSort) {");
    out.println("  for (i=optSort.options.length-1;i>=0;i--) {");
    out.println("   optSort.remove(i);");
    out.println("   //if(optSort.options[i].selected) optSort.remove(i);");
    out.println("  }");
    out.println("  if (selectedIndex == 0) { ");
    out.println("   // detailed");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Last Name';");
    out.println("   optn.value = 'last';");
    out.println("   optSort.options.add(optn);");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Oldest Round';");
    out.println("   optn.value = 'oldest';");
    out.println("   optSort.options.add(optn);");
    out.println("  } else { ");
    out.println("   // summary");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Last Name';");
    out.println("   optn.value = 'last';");
    out.println("   optSort.options.add(optn);");
    out.println("   optn = document.createElement(\"OPTION\");");
    out.println("   optn.text = 'By Total Missing';");
    out.println("   optn.value = 'count';");
    out.println("   optSort.options.add(optn);");
    out.println("  }");
    out.println(" }");
    out.println("}");
    out.println("</script>");
    
    
    out.println("<p align=center><font size=5>Missed Postings by Member</font></p>");

    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\" width=\"600\">");
    out.println("<tr><td>");
    out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
    out.println("<p>Select the days you wish to look back as well as a grace period.&nbsp; <!--If you wish to look back at all ");
      out.println("play history then specify a zero.&nbsp; -->If you wish to allow a grace period you can specify the number ");
      out.println("of days.&nbsp; <!--Entering a zero for grace period means rounds played yesterday will be inluded.&nbsp; -->");
      out.println("Slecting '1' for grace period means rounds played yesteday will be excluded from the report.");
      out.println("</p></font>");
    out.println("</td></tr></table>");

    out.println("<br><br>");

    //out.println("<br><p align=center><font size=3><b>Configure Report</b></font></p>");

    out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\" width=\"400\">");

    out.println("<form method=post action=\"/" +rev+ "/servlet/Proshop_report_handicap\" name=\"frmHdcpRpt\">");
    out.println("<input type=hidden name=todo value='doNonPosters'>");

    out.println("<input type=hidden name=clubid value='0'>");
    out.println("<input type=hidden name=associd value='0'>");

    out.println("<tr><td colspan=2 align=center><font size=3><b>Configure Report</b></font></td></tr>");

    out.println("<tr>" +
                 "<td><font size=2>Days to look back</td>" +
                 "<td>&nbsp;&nbsp;<input type=text name=period value=\"30\" size=3 maxlength=3></td>" +
                "</tr>");

    out.print("<tr><td><font size=2>Grace Period</td>");
    out.print("<td>&nbsp;&nbsp;");
     out.print("<select name=grace size=1>");
     for (int i=1;i<=7;i++) {
       out.print("<option>" +i+ "</option>");
     }
     out.print("</select>");
    out.println("</td></tr>");
    
    
    out.println("<tr><td><font size=2>Report Type</font></td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=rptType size=1 onchange=\"updateSorts(this.selectedIndex)\">" +
             "<option value=detail>Detail" +
             "<option value=summary>Summary" +
            "</select>" +
            "</td></tr>");
/*
    out.println("<tr><td><font size=2>Match Dates</font></td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=matchDates size=1>" +
             "<option value=yes>Yes" +
             "<option value=no>No" +
            "</select>" +
            "</td></tr>");
*/
    out.println("<tr><td><font size=2>Sorting Method</td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=sort size=1>" + // style=\"width: 100px\"
             "<option value=last>By Last Name" +
             "<option value=oldest>By Oldest Round" +
             //"<option value=count>By Total Missing" +
            "</select>" +
            "</td></tr>");
    
    out.println("<tr><td nowrap><font size=2>Include Member Type</td>");
    out.println("<td>&nbsp;&nbsp;" +
            "<select name=dsplyMType size=1>" +
             "<option value=0>No" +
             "<option value=1>Yes" +
            "</select>" +
            "</td></tr>");
    
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM hdcp_club_num");
        rs.last();
        
        if (rs.getRow() > 1) {

            out.print("<tr><td><font size=2>Club Number</font></td><td>&nbsp;&nbsp;");
            out.print("<select name=club_num_id size=1>");
    
            rs.beforeFirst();

            while (rs.next()) {

                out.print("<option value=" + rs.getInt("hdcp_club_num_id") + ">" + rs.getString("club_name") + " (" + rs.getInt("hdcp_club_num_id") + ")</option>"); // + " (" + rs.getString("club_name")
            }
            
            out.print("</select>");
            out.println("</td></tr>");
            
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { stmt.close(); }
        catch (Exception ignore) {}
        
    }
    
        
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM hdcp_assoc_num");
        rs.last();
        
        if (rs.getRow() > 1) {

            out.print("<tr><td><font size=2>Club Association</font></td><td>&nbsp;&nbsp;");
            out.print("<select name=club_num_id size=1>");
    
            rs.beforeFirst();

            while (rs.next()) {

                out.print("<option value=" + rs.getInt("hdcp_assoc_num_id") + ">" + rs.getString("assoc_name") + " (" + rs.getInt("hdcp_assoc_num_id") + ")</option>"); // + " (" + rs.getString("club_name")
            }
            
            out.print("</select>");
            out.println("</td></tr>");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { stmt.close(); }
        catch (Exception ignore) {}
        
    }

        
    out.println("<tr><td colspan=2 align=center><br><input type=button value=\" Home \" onclick=\"location.href='/"+rev+"/servlet/Proshop_announce'\">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<input type=submit value=\" Get Report \"><br>&nbsp;</td></tr>");
    
    out.println("</form>");
    
    out.println("</table><br><br>");
          
}

 
private void doNonPosters(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out) {
    

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String fullName = "";
    String tee_name = "";
    String sdate = "";
    String tmp_holes = "";
    
    String period = (req.getParameter("period") != null) ? req.getParameter("period")  : "30";
    String sgrace = (req.getParameter("grace") != null) ? req.getParameter("grace")  : "0";
    String clubid = (req.getParameter("clubid") != null) ? req.getParameter("clubid")  : "0";
    String associd = (req.getParameter("associd") != null) ? req.getParameter("associd")  : "0";
    String rptType = (req.getParameter("rptType") != null) ? req.getParameter("rptType")  : "";
    String sort = (req.getParameter("sort") != null) ? req.getParameter("sort")  : "";
    String orderby = "fullName, tp.date, time";
    
    int dsplyMType = (req.getParameter("dsplyMType") != null && req.getParameter("dsplyMType").equals("1")) ? 1 : 0;
    //int match_dates = (req.getParameter("matchDates") != null && req.getParameter("matchDates").equals("yes")) ? 1 : 0;
    int multi = SystemUtils.getMulti(con);
    int club_id = 0;
    int assoc_id = 0;
    int days = 0;
    int grace = 0;
    
    try {
        
        days = Integer.parseInt(period);
        club_id = Integer.parseInt(clubid);
        assoc_id = Integer.parseInt(associd);
        grace = Integer.parseInt(sgrace);
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error converting strings and loading multi.", exc.getMessage(), out, false);
        return;
    }
    
    // enforce sane defaults
    if (!sort.equals("count") && !sort.equals("oldest")) sort = "last";
    
    if (sort.equals("oldest")) {
        
        orderby = "tp.date, time, fullname";
        
    }
    
    if (!rptType.equals("detail")) {
        
        rptType = "summary";
        
        if (sort.equals("last")) {
            orderby = "fullname, c DESC";
        } else {
            orderby = "c DESC, fullname";
        }
    }
    
    out.println("<br><p align=center><font size=5>Missed Postings by Member</font>"); // Members With Missing Posted Scores
    
    out.println("<br><br><font size=3>for the last " + days + " days</font>");
    out.println("<br><br><font size=2>" + buildDisplayDateTime() + "</font>");
    if (grace > 0) out.println("<br><font size=2>with a grace period of " + grace + " days");
    out.println("</p>");

    //  Print Excel/Back/Home buttons
    if (req.getParameter("excel") == null) {     // if normal request

        out.println("<center><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
        out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
        out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
        out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
        out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
        out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");

        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=/" + rev + "/servlet/Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=/" + rev + "/servlet/Proshop_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
    } // end if not excel
    
    // detail  report columns - member, (member type), date, course
    // summary report columns - member, (member type), count, oldest date
    
    //out.println("<table align=center>");
    out.println("<table width=400 align=center cellspacing=0 cellpadding=5 border=1 bgcolor=#F5F5DC>"); // style=\"border: 1px solid #336633\"  F5F5DC  86B686
    out.println("<tr style=\"font-weight:bold;background-color:#336633;color:white;\">" +
                    "<td nowrap align=center><font size=3><b>&nbsp;Member's Name&nbsp;</b></font></td>" +
                    ((dsplyMType == 1) ? "<td align=center nowrap><font size=3><b>&nbsp;Member Type&nbsp;</b></font></td>" : "") +
                    "<td align=center nowrap><font size=3><b>&nbsp;Handicap #&nbsp;</b></font></td>");
    if (rptType.equals("summary")) {
        out.println("<td align=center><font size=3><b>Count</b></font></td>");
        out.println("<td align=center nowrap><font size=3><b>&nbsp;Oldest Date&nbsp;</b></font></td>");
    } else {
        out.println("<td align=center><font size=3><b>Date</b></font></td>");
        if (multi == 1) out.println("<td align=center><font size=3><b>Course</b></font></td>");
    }
    
    
    try {
        
        // find all members with old tee times without a posted score

        String sql = "" +
                "SELECT " + ((rptType.equals("summary")) ? "COUNT(*) AS c, DATE_FORMAT(MIN(tp.date), '%m-%d-%Y') AS sdate, CONCAT(m.name_first, ' ', m.name_last) AS searchName," : "DATE_FORMAT(tp.date, '%m-%d-%Y') AS sdate,") + " tp.time, tp.courseName, m.ghin, m.m_type, CONCAT(m.name_last, ', ', m.name_first) AS fullName " +
                "FROM teepast2 tp, member2b m " +
                "LEFT OUTER JOIN score_postings sp ON CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND sp.date = tp.date " +
                "WHERE 1 = ? AND " +
                    "tp.date > DATE_FORMAT(DATE_ADD(now(), INTERVAL -" +days+ " DAY), '%Y%m%d') AND " +
                    ((grace > 0) ? "tp.date < DATE_FORMAT(DATE_ADD(now(), INTERVAL -" +grace+ " DAY), '%Y%m%d') AND " : "") +
                    "m.ghin <> '' AND ISNULL(sp.score) AND " +
                    ((club_id != 0) ? "m.hdcp_club_num_id = ? AND " : "") +
                    ((assoc_id != 0) ? "m.hdcp_assoc_num_id = ? AND " : "") +
                    "( " +
                        "(tp.show1 = 1 AND tp.p91 = 0 AND tp.username1 = m.username) OR " +
                        "(tp.show2 = 1 AND tp.p92 = 0 AND tp.username2 = m.username) OR " +
                        "(tp.show3 = 1 AND tp.p93 = 0 AND tp.username3 = m.username) OR " +
                        "(tp.show4 = 1 AND tp.p94 = 0 AND tp.username4 = m.username) OR " +
                        "(tp.show5 = 1 AND tp.p95 = 0 AND tp.username5 = m.username) " +
                    ") " +
                    ((rptType.equals("summary")) ? "GROUP BY m.username " : "") +
                "ORDER BY " + orderby + ";";

        //out.println("<!-- " + sql + " -->");

        pstmt = con.prepareStatement(sql);
        
        int parmIdx = 1;
        pstmt.clearParameters();
        pstmt.setInt(parmIdx, 1);
        if (club_id != 0) {
            parmIdx++;
            pstmt.setInt(parmIdx, club_id);
        }
        if (assoc_id != 0) {
            parmIdx++;
            pstmt.setInt(parmIdx, assoc_id);
        }
        rs = pstmt.executeQuery();
        
        while ( rs.next() ) {
            
            out.println("<tr>" +
                        "<td nowrap>" + ((rptType.equals("summary")) ? "<a href=\"/" + rev + "/servlet/Proshop_report_handicap?search=Search" +
                        "&popup=yes&name=" + rs.getString("searchName") + "\" style=\"color:black\" target=_hdcpPopup>" : "") + rs.getString("fullName") +
                        ((rptType.equals("summary")) ? "</a>" : "") + "</td>" +
                        ((dsplyMType == 1) ? "<td nowrap align=center>" + rs.getString("m_type") + "</td>" : "") +
                        "<td nowrap align=center>" + formatHdcpNum(rs.getString("ghin"), 1) + "</td>");
            
            if (rptType.equals("summary")) {
                
                out.print("<td align=center>" + rs.getInt("c") + "</td>");
                out.print("<td align=center>" + rs.getString("sdate") + "</td>");
                
            } else {
                
                out.print("<td nowrap align=center>" +rs.getString("sdate") + "</td>");
                if (multi == 1) out.println("<td nowrap align=center>" + rs.getString("courseName") + "</td>");
                
            }
            out.println("</tr>");
        }
        
    } catch (Exception exc) {
        
        SystemUtils.buildDatabaseErrMsg("Error loading posted scores for non posters report.", exc.getMessage(), out, false);
        
    } finally {
        
        try { rs.close(); }
        catch (Exception ignore) {}
        
        try { pstmt.close(); }
        catch (Exception ignore) {}
        
    }
    
    
    out.println("</table>");
     
    if (req.getParameter("excel") == null) {     // if normal request
        
        out.println("<br><br>");
        
        out.println("<center><form method=\"post\" action=\"/" +rev+ "/servlet/Proshop_report_handicap\">");
        out.println("<input type=\"hidden\" name=\"excel\" value=\"yes\">");
        out.println("<input type=\"hidden\" name=\"todo\" value=\"doNonPosters\">");
        out.println("<input type=\"hidden\" name=\"grace\" value=\"" + grace + "\">");
        out.println("<input type=\"hidden\" name=\"period\" value=\"" + days + "\">");
        out.println("<input type=\"hidden\" name=\"clubid\" value=\"" + clubid + "\">");
        out.println("<input type=\"hidden\" name=\"associd\" value=\"" + associd + "\">");
        out.println("<input type=\"hidden\" name=\"rptType\" value=\"" + rptType + "\">");
        out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></center>");
        
        out.println("" +
            "<table align=center><tr>" +
            "<td><form action=/" + rev + "/servlet/Proshop_report_handicap method=get><input type=hidden name=todo value=nonPosters><input type=submit value=\"Back\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "<td>&nbsp; &nbsp; &nbsp;</td>" +
            "<td><form action=/" + rev + "/servlet/Proshop_announce><input type=submit value=\"Home\" style=\"width: 75px; background:#8B8970\"></form></td>" +
            "</tr></table>");
    } // end if not excel
    
}


 //**************************************************
 // Common Method for Displaying Date/Time of Report
 //**************************************************
 //
 private String buildDisplayDateTime() {

    GregorianCalendar cal = new GregorianCalendar();
    DateFormat df_full = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    return "<i>This report was generated on " + df_full.format(cal.getTime()) + "</i>";

 }
 
 
 
 private String formatHdcpNum(String hdcpNum, int style) {

     String tmp = "";

     if (hdcpNum != null) {

         // regardless of which style, lets default to removing any dashes
         tmp = hdcpNum.replace("-", "");

         if (style == 1) {

             // first ensure ghin # is 7 chars long - add leading zeros to fix
             while (tmp.length() < 7) {

                 tmp = "0" + tmp;
             }

             // add the dash back in
             tmp = tmp.substring(0,3) + "-" + tmp.substring(3);

         } // add additional formats as needed below in else if clauses

     }

     return tmp;
     
 }


 public static int[] lookupHistory(int sdate, int edate, String username, Connection con, PrintWriter out) {

    int [] result = new int [3];

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        String sql = "" +
                "SELECT " +
                    "SUM(IF(" +
                        "(username1 = ? && p91 = 1) || " +
                        "(username2 = ? && p92 = 1) || " +
                        "(username3 = ? && p93 = 1) || " +
                        "(username4 = ? && p94 = 1) || " +
                        "(username5 = ? && p95 = 1), 1, 0) ) " +
                    "AS played9, " +
                    "SUM(IF(" +
                        "(username1 = ? && p91 = 0) || " +
                        "(username2 = ? && p92 = 0) || " +
                        "(username3 = ? && p93 = 0) || " +
                        "(username4 = ? && p94 = 0) || " +
                        "(username5 = ? && p95 = 0), 1, 0) ) " +
                    "AS player18,  " +
                    "(" +
                        "SELECT COUNT(*) AS posted_scores " +
                        "FROM member2b m " +
                        "LEFT OUTER JOIN score_postings sp ON CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) " +
                        "WHERE m.username = ? AND sp.date >= ? AND sp.date <= ? AND sp.type <> 'A' AND sp.type <> 'AI' " +
                        "ORDER BY date DESC" +
                    ") AS posted_scores " +
                "FROM teepast2 tp " +
                "WHERE " +
                "tp.date >= ? AND tp.date <= ? AND " +
                    "( " +
                        "(username1 = ? && show1 = 1) || " +
                        "(username2 = ? && show2 = 1) || " +
                        "(username3 = ? && show3 = 1) || " +
                        "(username4 = ? && show4 = 1) || " +
                        "(username5 = ? && show5 = 1) " +
                    ") " +
                    "ORDER BY date DESC;";

        out.println("<!-- " + sql + " -->");
        out.println("<!-- USING: user=" + username + ",sdate=" + sdate + ",edate=" + edate + " -->");

        pstmt = con.prepareStatement(sql);

        pstmt.clearParameters();
        pstmt.setString(1, username);
        pstmt.setString(2, username);
        pstmt.setString(3, username);
        pstmt.setString(4, username);
        pstmt.setString(5, username);
        pstmt.setString(6, username);
        pstmt.setString(7, username);
        pstmt.setString(8, username);
        pstmt.setString(9, username);
        pstmt.setString(10, username);
        pstmt.setString(11, username);
        pstmt.setInt(12, sdate);
        pstmt.setInt(13, edate);
        pstmt.setInt(14, sdate);
        pstmt.setInt(15, edate);
        pstmt.setString(16, username);
        pstmt.setString(17, username);
        pstmt.setString(18, username);
        pstmt.setString(19, username);
        pstmt.setString(20, username);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            result[0] = rs.getInt(1);
            result[1] = rs.getInt(2);
            result[2] = rs.getInt(3);

        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading rounds/posted score counts for " + username, exc.getMessage(), out, false);

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }

} // end Proshop_report_handicap




/*
 
  
  
 
SELECT tp.time, tp.date, m.ghin, CONCAT(m.name_last, ', ', m.name_first) AS fullName
FROM teepast2 tp, member2b m 
LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date > 20090500 
LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id 
WHERE 
    tp.date > 20090500 AND 
    tp.courseName = 'Members Course' AND 
    m.ghin <> '' AND 
    ( 
        (tp.show1 = 1 AND tp.p91 = 0 AND tp.username1 = m.username) OR 
        (tp.show2 = 1 AND tp.p92 = 0 AND tp.username2 = m.username) OR 
        (tp.show3 = 1 AND tp.p93 = 0 AND tp.username3 = m.username) OR 
        (tp.show4 = 1 AND tp.p94 = 0 AND tp.username4 = m.username) OR 
        (tp.show5 = 1 AND tp.p95 = 0 AND tp.username5 = m.username) 
    ) 
ORDER BY fullName, date, time;

 
 
 
 SELECT tp.time, tp.p91, tp.p92, tp.p93, tp.p94, tp.p95, sp.score, sp.type, t.tee_name, 
    CONCAT(m.name_last, ', ', m.name_first) AS fullName, 
    IF(m.username=tp.username1, 1, 0) AS pos1, 
    IF(m.username=tp.username2, 1, 0) AS pos2, 
    IF(m.username=tp.username3, 1, 0) AS pos3, 
    IF(m.username=tp.username4, 1, 0) AS pos4, 
    IF(m.username=tp.username5, 1, 0) AS pos5 
FROM teepast2 tp, member2b m 
LEFT OUTER JOIN score_postings sp ON sp.hdcpNum = REPLACE(m.ghin, '-', '') AND sp.date = 20090315 
LEFT OUTER JOIN tees t ON t.tee_id = sp.tee_id 
WHERE 
    tp.date = 20090315 AND 
    tp.courseName = "Lake Valley" AND 
    m.ghin <> '' AND 
    ( 
        (tp.show1 = 1 AND tp.username1 = m.username) OR 
        (tp.show2 = 1 AND tp.username2 = m.username) OR 
        (tp.show3 = 1 AND tp.username3 = m.username) OR 
        (tp.show4 = 1 AND tp.username4 = m.username) OR 
        (tp.show5 = 1 AND tp.username5 = m.username) 
    ) 
ORDER BY fullName, time
 
 */