/***************************************************************************************     
 *   Support_sync:  This servlet will look in the v_/roster folder for a text file
 *                 that contains member records for the club being processed.  The
 *                 member records will be compared against those already in the system.
 *
 *
 *   called by: support_main2.htm (for single club)
 *
 *
 *   created:   4/07/06 Bob P.
 *
 *   updated:
 *
 *              3/19/14 Added Flexscape Premier files to the View Rosters page.
 *              1/13/14 Added support for the 'supportpro' limited user on the support side.
 *              1/30/13 When a manual sync is run for Desert Mountain, the custom sorting processing in Common_sync.sortMemberList will also be run.
 *              1/03/13 Updates for GolfNet
 *              8/03/12 Add calls to the Common_ghin getHandicaps and getHandicapsForClub methods
 *              7/26/12 Updated doGet to pull the forceSetInact parameter prior to running roster sync to allow for overriding the new bulk inact safety net.
 *              4/28/11 Updated getSyncDate method to close DB connections it's opening.  Was crashing DB server on large roster files!! Also closed stmt/rs/con in other methods.
 *              4/28/11 Replaced all dbConn.Connect calls with calls to Connect.getCon calls instead.
 *              7/01/09 Added setAllActive method to allow sales/support users to set all members of a club to 'active' in the database
 *              3/09/09 Added a way to invoke the posted scores download for ALL clubs
 *             12/02/08 Added manual service number entry to posted scores download
 *              9/15/08 Roster file list will now display in alphabetical order
 *              9/05/08 refreshMemberHdcps don't update hndcp #6-10 in updEvents - Fields no longer in db
 *              7/23/08 Fix for how last_sync_dates are looked up for clubcorp clubs
 *              7/17/08 Changed so Rosters and Sync Error Logs can be viewed from Support Side
 *              7/10/08 Alterations to roster displaying to convert clubcorp_## filenames, ignore filenames starting with numbers, and commented out meritsoft listing
 *              6/11/08 Changed link for invalid session to route to sales_main.htm instead of support_main2.htm
 *              6/10/08 Changes to formatting for displayErrorLogs()
 *              6/09/08 Changed displayErrorLogs() to order the listing of logs in alphabetical order
 *              6/06/08 Added displayErrorLogs() method for displaying roster sync error logs on sales_main.htm
 *              5/23/08 Enhanced roster printing by adding functionality for each member's last_sync_date
 *                      to be printed along with their data
 *              5/08/08 Added functionality to display RosterSync rosters through a link on sales_main.htm
 *              7/24/07 Added call to Common_ghin.updateMemberHdcps after getPostedScoresForClub call
 *              3/20/07 Added new call to Common_ghin.getPostedScoresForClub if posted_scores is present.
 *                      Invoked by new menu item on Menu #2 'Download Posted Scores For This Club'
 *              8/28/06 Added refreshMemberHdcps method for updating hdcps stored
 *                      in teecurr2 and evntsup2.  Invoked by new menu item on Menu #2
 *                      'Refresh All Saved Handicaps For This Club'
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.Connect;
import com.foretees.common.Utilities;


public class Support_sync extends HttpServlet {
                           

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = SystemUtils.support;             // valid username for normal use
 String supportpro = SystemUtils.supportpro;             // valid username for normal use
 String sales = SystemUtils.sales;                 // valid username for rtype="rosters"

 
 //*****************************************************************
 // Process the request from support_main2.htm for a single club
 //*****************************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects

   HttpSession session = null;

   String rtype = "";             


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Session Error</H3>");
      out.println("<BR><BR>Invalid Session.");
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");   // get club

   if (!user.startsWith(support) && (rtype.equals("rosters") && !user.startsWith( sales ))) {

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Session Error</H3>");
      out.println("<BR><BR>Invalid Session.");
      if (user.startsWith(support)){
          if (user.equals(supportpro)) {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
          } else {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");  
          }
      } else {
          out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (req.getParameter("rtype") != null) {

      rtype = req.getParameter("rtype");             // get rtype parameter
   }
   
   
   // If rtype == "rosters" run the displayRosters method
   if (rtype.equals("rosters")){
       displayRosters(req, resp, out);
       return;
   }
   
   // If rtype == "errorlogs" run the displayErrorLogs method
   if (rtype.equals("errorlogs")){
       displayErrorLogs(req, resp, out);
       return;
   }

   // Load the JDBC Driver and connect to DB.........
   try {
      con = Connect.getCon(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith(support)){
          if (user.equals(supportpro)) {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
          } else {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");  
          }
      } else {
          out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Set all members for current club to 'Active' in their database
   if (rtype.startsWith("active")) {
       setAllActive(req, user, club, con, out);
       return;
   }

   if (req.getParameter("posted_scores") != null && req.getParameter("posted_scores").equals("yes")) {

      String startDate = "";  // 30 days ago
      String endDate = "";    // today
      String hdcpSystem = "";
      String gn21_sourceClubId = "";

      try {

          Statement stmt = con.createStatement();
          ResultSet rs = stmt.executeQuery("" +
              "SELECT hdcpSystem, gn21_sourceClubId FROM club5");

          if (rs.next()) {

               hdcpSystem = rs.getString("hdcpSystem");
               gn21_sourceClubId = rs.getString("gn21_sourceClubId");
          }

          stmt.close();

      } catch (Exception exc) {

          SystemUtils.logError("Support_sync: Error getting hdcpSystem.  Exception was " + exc.getMessage());
      }

      if (!hdcpSystem.equalsIgnoreCase("GHIN") && !hdcpSystem.equalsIgnoreCase("GN21") ) {

          out.println("<HTML><HEAD><TITLE>Invalid handicap system</TITLE></HEAD>");
          out.println("<BODY><CENTER><H3>Club not configured with a compatible handicap system!</H3><br><br><br>");

      } else if (hdcpSystem.equalsIgnoreCase("GN21") && gn21_sourceClubId.equalsIgnoreCase("") ) {

          out.println("<HTML><HEAD><TITLE>Invalid handicap system</TITLE></HEAD>");
          out.println("<BODY><CENTER><H3>GolfNet configuration not complete, the club's SourceID is not defined.</H3><br><br><br>");

      } else {

          out.println("<HTML><HEAD><TITLE>Download Posted Scores</TITLE></HEAD>");
          out.println("<BODY><CENTER><H3>Downloading Posted Scores (" + hdcpSystem  + ")</H3>");
          out.println("<BR><BR>Club: " + club);

          if (req.getParameter("todo") == null) {

              try {

                  Statement stmt = con.createStatement();
                  ResultSet rs = stmt.executeQuery("" +
                      "SELECT " +
                      "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 30 DAY), '%Y%m%d') AS sdate, " +
                      "DATE_FORMAT(now(), '%Y%m%d') AS edate, " +
                      "DATE_FORMAT(now(), '%m/%d/%Y') AS endDate, " +
                      "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 30 DAY), '%m/%d/%Y') AS startDate;");

                  if (rs.next()) {
                      if (hdcpSystem.equalsIgnoreCase("GHIN")) {

                          startDate = rs.getString("startDate");
                          endDate = rs.getString("endDate");

                      } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

                          startDate = String.valueOf(rs.getInt("sdate"));
                          endDate = String.valueOf(rs.getInt("edate"));
                      }
                  }

                  stmt.close();

              } catch (Exception exc) {

                  SystemUtils.logError("Support_sync: Error getting dates.  Exception was " + exc.getMessage());
              }

              out.println("<BR><BR>");
              out.println("<form>");
              out.println("<input type=hidden value=yes name=posted_scores>");
              out.println("<input type=text name=startDate value=\"" + startDate + "\">");
              out.println("<input type=text name=endDate value=\"" + endDate + "\">");
              out.println("<BR><BR>");
              out.println("<input type=text name=service value=\"00\" size=3>");
              out.println("<input type=submit value=\" Get Scores \" name=todo>");
              out.println("</form>");

              out.println("<BR><BR>");
              out.println("<form>");
              out.println("<input type=hidden value=yes name=posted_scores>");
              out.println("<input type=hidden value=yes name=all_clubs>");
              out.println("<BR><BR>");
              out.println("<input type=submit value=\"Get Scores For ALL Clubs\" name=todo>");
              out.println("</form>");

              return;

          }

          if (req.getParameter("all_clubs") == null) {

              // do just this club
              startDate = req.getParameter("startDate");
              endDate = req.getParameter("endDate");
              String service = req.getParameter("service");

              out.println("<BR><BR>Dates Requested: " + startDate + " thru " + endDate + " service level " + service + "<BR><BR>");

              if (hdcpSystem.equalsIgnoreCase("GHIN")) {

                  Common_ghin.getPostedScoresForClub(club, startDate, endDate, service, con, out);
                  Common_ghin.updateMemberHdcps(club, con);

              } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

                  Common_golfnet.getPostedScoresForClub(club, Integer.parseInt(startDate), Integer.parseInt(endDate), con, out);
                  
              }

          } else {

              // do all clubs
              out.println("<BR><BR>Running " + hdcpSystem  + " Handicap Download For ALL Clubs...<BR><BR>");
              if (hdcpSystem.equalsIgnoreCase("GHIN")) {

                  Common_ghin.getPostedScores(out);

              } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

                  Common_golfnet.getPostedScores(out);
              }

          }

          out.println("<BR><BR>DONE!");
      }

      if (user.startsWith(support)){
          out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      } else {
          out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;

   } // end if posted_scores request
   
   
   if (req.getParameter("dl_hdcp") != null && req.getParameter("dl_hdcp").equals("yes")) {

      String hdcpSystem = Common_handicaps.getClubHdcpOption(club, con);

      out.println("<HTML><HEAD><TITLE>Download Handicaps</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Downloading Handicap Data</H3>");
      out.println("<BR><BR>Club: " + club + " (" + hdcpSystem  + ")");
       
      if (req.getParameter("all_clubs") == null) {

          if (hdcpSystem.equalsIgnoreCase("GHIN")) {

              Common_ghin.getHandicapsForClub(club, con, out);
              Common_ghin.updateMemberHdcps(club, con);

          } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

              Common_golfnet.getHandicapsForClub(club, con, out);
              Common_golfnet.updateMemberHdcps(club, con);
          }

      } else {

          // do all clubs
          out.println("<BR><BR>Running Handicap Download For ALL Clubs...<BR><BR>");
          if (hdcpSystem.equalsIgnoreCase("GHIN")) {

              Common_ghin.getHandicaps(out);

          } else if (hdcpSystem.equalsIgnoreCase("GN21")) {

              //Common_golfnet.getHandicaps(out);
          }
          
      }

      out.println("<BR><BR>DONE!");
      if (user.startsWith(support)){
          out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      } else {
          out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
       
   }


   if (req.getParameter("upd_hdcp") != null && req.getParameter("upd_hdcp").equals("yes")) {
    
       refreshMemberHdcps(club, con, out);
       
       // Member Handicaps Refreshed - inform support....
       //
       out.println("<HTML><HEAD><TITLE>Member Handicaps Refreshed</TITLE></HEAD>");
       out.println("<BODY><CENTER><H3>Member Handicaps Refreshed OK</H3>");
       out.println("<BR><BR>Member handicaps refreshed for: " +club);
       if (user.startsWith(support)){
           out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
       } else {
           out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
       }
       out.println("</CENTER></BODY></HTML>");
   }
   
   
   if (req.getParameter("support") != null && req.getParameter("support").equals("yes")) {
       
       boolean forceSetInact = false;
       
       if (req.getParameter("forceSetInact") != null) {
           forceSetInact = true;
       }
       
       //
       //  Go process this club (in Common_sync)
       //
       int result = Common_sync.clubSync(club, forceSetInact, con);              // go process this club!!!!!
       
       if (club.equals("desertmountain")) {
           Common_sync.sortMemberList(1, club, con);
       }
       
       // Roster Sync complete - inform support....
       //
       out.println("<HTML><HEAD><TITLE>Roster Sync Results</TITLE></HEAD><BODY><CENTER>");
       
       if (result == 0) {
           out.println("<H3>Club Not Using Roster Sync</H3>");
           out.println("<BR><BR>Roster Not Updated for: " +club);
       } else if (result == 1) {
           out.println("<H3>Roster Successfully Updated</H3>");
           out.println("<BR><BR>Roster Updated for: " +club);
       } else if (result == -1) {
           out.println("<H3>Roster Not Updated</H3>");
           out.println("<BR><BR>Roster Sync Failed for: " +club);
       }
       
       if (user.startsWith(support)){
          if (user.equals(supportpro)) {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
          } else {
              out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");  
          }
       } else {
           out.println("<BR><BR> <A HREF=\"/" + rev + "/sales_main.htm\">Return</A>");
       }
       out.println("</CENTER></BODY></HTML>");
   }

   // Attempt to close Connection object
   try { con.close(); }
   catch (Exception exc) {
       Utilities.logError("Support_sync - doGet - " + club + " - Error closing DB connection!");
   }

   return;

 }    // end of doGet from Support

 
 //
 //  Loop thru and update all instances of a members hndcp in the system
 //  updates tables teecurr2, evntsup2b with hdcp data from member2b
 //
 private void refreshMemberHdcps(String club, Connection con, PrintWriter out) {
     
     Common_ghin.updateMemberHdcps(club, con);
     
     
/*
    Statement stmt = null;
    String teecurr_sql = "";
    String evntsup_sql = "";

    try {
        
        stmt = con.createStatement();
        
        for (int x=1;x<=5;x++) {
            
            teecurr_sql = "" + 
                    "UPDATE teecurr2 t, member2b m " +
                    "SET " +
                        "t.hndcp" + x + " = (IF(t.username" + x + " = m.username,m.c_hancap,t.hndcp" + x + ")) " + 
                    "WHERE " +
                        "t.username" + x + " = m.username AND " +
                        "m.c_hancap <> -99 AND m.c_hancap <> 99";

            stmt.executeUpdate(teecurr_sql);
        }
        
        for (int x=1;x<=5;x++) {
            evntsup_sql = "" + 
                    "UPDATE evntsup2b e, member2b m " +
                    "SET " +
                        "e.hndcp" + x + " = (IF(e.username" + x + " = m.username,m.c_hancap,e.hndcp" + x + ")) " + 
                    "WHERE " +
                        "e.username" + x + " = m.username AND " +
                        "m.c_hancap <> -99 AND m.c_hancap <> 99";

            stmt.executeUpdate(evntsup_sql);
            
        } // end loop each player position
        
        stmt.close();
        
    } catch (Exception exc) {
      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
    }
 */
 }
 
 
    //
    //  Display selection options for user to select a club and then display
    //  roster for that club.
    //
    private void displayRosters(HttpServletRequest req, HttpServletResponse resp, PrintWriter out){
        
        StringTokenizer tok = null;
        BufferedReader bfr = null;
        FileReader fr = null;
        File f = null;
        
        String announce_path = "/home/rosters/";
        String file = "";
        String fileName = "";
        String club = "";
        String temp = "";
        String temp2 = "";
        String memid = "";
        String fname = "";
        String lname = "";
        String suffix = "";
        String lastSyncDate = "";
        int count = 0;
        
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
                
        HttpSession session = req.getSession(false);
        
        String user = (String)session.getAttribute("user");   // get username
        
        if (req.getParameter("file") != null){
            
            try{
                
                // grab file and print it to screen
                fileName = req.getParameter("file");
                f = new File(fileName);
                fr = new FileReader(f);
                bfr = new BufferedReader(fr);
               
                tok = new StringTokenizer(fileName, "/");
                while (tok.countTokens() > 1)
                    tok.nextToken();
                file = tok.nextToken();
                
                // get clubname from filename
                tok = new StringTokenizer(file, ".");
                if (tok.countTokens() == 2) {
                    club = tok.nextToken();
                }
                
                // if club name starts with clubcorp, get the real club name from the database
                if (club.startsWith("clubcorp")) {
                        
                    // attempt to look up the correct translational name for clubcorp_## rosters
                    try {
                        con = Connect.getCon(rev);
                        
                        stmt = con.prepareStatement("" +
                                "SELECT ft_name FROM clubcorp WHERE cc_name = ?");
                        stmt.clearParameters();
                        stmt.setString(1, club);
                        
                        rs = stmt.executeQuery();
                        
                        if (rs.next()) {
                            club = rs.getString("ft_name");
                        }
                    } catch (Exception exc) { }
                    finally {

                        try { rs.close(); }
                        catch (Exception ignore) { }

                        try { stmt.close(); }
                        catch (Exception ignore) { }

                        try { con.close(); }
                        catch (Exception exc) {
                            Utilities.logError("Support_sync - displayRosters - " + club + " - Error closing DB connection!");
                        }
                    }
                }
                
                
                try {            
                    resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
                    resp.setHeader("Content-Disposition", "attachment;filename=\"" + club + ".xls\"");
                } catch (Exception exc) { }
                
                
                // print each line of the file in a table
                out.println("<table border=\"1\" align=\"center\" bgcolor=\"#FFFFFF\" cellpadding=\"2\">");
                while ((temp = bfr.readLine()) != null){
                    
                    count = 0;
                    out.println("<tr style=\"font-size:10pt; white-space:nowrap\">");
                    tok = new StringTokenizer(temp, ",");
                    
                    // print each "cell" of the file in a seperate cell of the table
                    while (tok.hasMoreTokens()){
                        StringTokenizer tok2 = new StringTokenizer(tok.nextToken(), "\"");
                        if (tok2.hasMoreTokens()){
                            temp2 = tok2.nextToken();
                            out.println("<td style=\"white-space:inherit\">" + temp2 + "</td>");
                            
                            // gather data for looking up last_sync_date
                            switch (count){
                                case 1:             // get memNum
                                    memid = temp2;
                                    break;
                                case 2:             // get name_first
                                    fname = temp2;
                                    break;
                                case 4:             // get name_last
                                    lname = temp2;
                                    break;
                                case 5:             // get suffix
                                    suffix = temp2;
                                    break;
                                default:
                                    break;
                            }
                        }
                        else {
                            out.println("<td></td>");
                            memid = "";
                            fname = "";
                            lname = "";
                            suffix = "";
                        }
                        
                        count++;                        
                    }
                    
                    // tack on last sync date if possible
                    if (!memid.equals("") || !fname.equals("") || !lname.equals("") || !suffix.equals("")) {
                        
                        lastSyncDate = getSyncDate(club, memid, fname, lname, suffix);
                    }
                    
                    if (!lastSyncDate.equals("")) {
                        
                        out.println("<td>" + lastSyncDate + "</td>");
                    }
                    
                    out.println("</tr>");
                }
                out.println("</table>");
            } catch (Exception exc) {
                
            }
        } else {
            
            try {
                
                // grab file directories
                File ce = new File(announce_path + "ce/");
                File flexscape = new File(announce_path + "flexscape/");
                File flexscape_premier = new File(announce_path + "flexscape_premier/");
                //File meritsoft = new File(announce_path + "meritsoft/");
                File mfirst = new File(announce_path + "mfirst/");
                File northstar = new File(announce_path + "northstar/");

                // Print files out to a table
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<h2 align=\"center\">View Rosters</h2>");
                if (user.startsWith(support)) {
                    out.println("<br><CENTER><a href=\"/" + rev + "/support_main2.htm\">Return</a>");
                } else {
                    out.println("<br><CENTER><a href=\"/" + rev + "/sales_main.htm\">Return</a></CENTER>");
                }
                out.println("<br>");
                out.println("<table border=\"1\" align=\"center\" bgcolor=\"#F5F5DC\" cellpadding=\"0\" cellspacing=\"0\">");
                out.println("<tr valign=\"top\">");
                out.println("   <td>");
                String [] filesCE = ce.list();
                Arrays.sort(filesCE);
                printRosterFiles("Club Essential", announce_path + "ce/", filesCE, out);
                out.println("   </td>");
                out.println("   <td>");
                String [] filesMfirst = mfirst.list();
                Arrays.sort(filesMfirst);
                printRosterFiles("MembersFirst", announce_path + "mfirst/", filesMfirst, out);
                out.println("   </td>");
                out.println("   <td>");
                String [] filesFlexscape = flexscape.list();
                Arrays.sort(filesFlexscape);
                printRosterFiles("Flexscape", announce_path + "flexscape/", filesFlexscape, out);
                out.println("   </td>");
                out.println("   <td>");
                String [] filesFlexscapePremier = flexscape_premier.list();
                Arrays.sort(filesFlexscapePremier);
                printRosterFiles("Flexscape Premier", announce_path + "flexscape_premier/", filesFlexscapePremier, out);
                out.println("   </td>");
                out.println("   <td>");
                /*
                String [] filesMeritsoft = meritsoft.list();
                printRosterFiles("Meritsoft", announce_path + "meritsoft/", filesMeritsoft, out);
                out.println("   </td>");
                out.println("   <td>");
                */
                String [] filesNorthstar = northstar.list();
                Arrays.sort(filesNorthstar);
                printRosterFiles("Northstar", announce_path + "northstar/", filesNorthstar, out);
                out.println("</td></tr></table>");
                if (user.startsWith(support)) {
                    out.println("<br><CENTER><a href=\"/" + rev + "/support_main2.htm\">Return</a>");
                } else {
                    out.println("<br><CENTER><a href=\"/" + rev + "/sales_main.htm\">Return</a></CENTER>");
                }
                out.println("</font>");
            } catch (Exception exc) { 
                out.println("Failed: could not access specified files");
            }
        }
    }
        
    // 
    // Print a list of roster files to the page
    // 
    private void printRosterFiles(String providerName, String path, String [] rosters, PrintWriter out){
        
        String displayName = "";
        StringTokenizer tok = null;
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        out.println("<table border=\"0\" cellpadding=\"2\">");
        out.println("<tr bgcolor=\"#336633\" style=\"color:white\"><td>" + providerName + "</td></tr>");
        if (rosters.length > 0){
            for (int i=0; i < rosters.length; i++){
              
                displayName = rosters[i];
                
                // Do not display if name starts with a number (i.e. 50.txt)
                if (!displayName.startsWith("0") && !displayName.startsWith("1") && !displayName.startsWith("2") &&
                    !displayName.startsWith("3") && !displayName.startsWith("4") && !displayName.startsWith("5") &&
                    !displayName.startsWith("6") && !displayName.startsWith("7") && !displayName.startsWith("8") &&
                    !displayName.startsWith("9")) {
                    
                    if (providerName.equalsIgnoreCase("MembersFirst") && displayName.startsWith("clubcorp")) {
                        
                        // attempt to look up the correct translational name for clubcorp_## rosters
                        tok = new StringTokenizer(displayName, ".");

                        String ccName = tok.nextToken();    // remove .txt from the end
                        try {
                            con = Connect.getCon(rev);

                            stmt = con.prepareStatement("" +
                                    "SELECT ft_name FROM clubcorp WHERE cc_name = ?");
                            stmt.clearParameters();
                            stmt.setString(1, ccName);

                            rs = stmt.executeQuery();

                            if (rs.next()) {
                                displayName = rs.getString("ft_name") + ".txt";
                            }
                        } catch (Exception exc) { }
                        finally {

                            try { rs.close(); }
                            catch (Exception ignore) { }

                            try { stmt.close(); }
                            catch (Exception ignore) { }

                            try { con.close(); }
                            catch (Exception exc) {
                                Utilities.logError("Support_sync - printRosterFiles - Error closing DB connection!");
                            }
                        }
                    }

                    out.println("<tr><td>");
                    out.println("<a href=\"Support_sync?rtype=rosters&file=" + path + rosters[i] + "\" style=\"font-size:10pt; color:black\" target=\"_blank\">" + displayName + "</a>");
                    out.println("</td></tr>");
                }
            }
        }
        else
            out.println("<tr><td>No files found.</td></tr>");
        
        out.println("</table>");
    }

    //
    // Display error log files for user to select and view 
    //
    private void displayErrorLogs(HttpServletRequest req, HttpServletResponse resp, PrintWriter out) {
        
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        StringTokenizer tok = null;
        BufferedReader bfr = null;
        FileReader fr = null;
        File f = null;
        
        int count = 0;
        
        String announce_path = "/usr/local/tomcat/webapps/";
        String file = "";
        String fileName = "";
        String club = "";
        String temp = "";
        
        if (req.getParameter("file") != null) { 
            try {
                
                // grab file and create BufferedReader
                fileName = req.getParameter("file");
                f = new File(fileName);
                fr = new FileReader(f);
                bfr = new BufferedReader(fr);

                tok = new StringTokenizer(fileName, "/");
                while (tok.countTokens() > 1)
                    tok.nextToken();
                file = tok.nextToken();

                // get clubname from filename
                tok = new StringTokenizer(file, ".");
                if (tok.countTokens() == 2) {
                    club = tok.nextToken();
                }
            
                // Print header and return link
                out.println("<h2 align=\"center\">-" + club + "-</h2>");
                out.println("<CENTER><a href=\"Support_sync?rtype=errorlogs\">Return</a></CENTER><br>");
                out.println("<pre>");
                
                // Print file to screen
                while ((temp = bfr.readLine()) != null){
                    out.println(temp);
                }
                
                out.println("</pre>");
                out.println("<br><CENTER><a href=\"Support_sync?rtype=errorlogs\">Return</a></CENTER><br>");
                
            } catch (Exception exc) {
                out.println("Error Could not access file.");
            }
            
            
        } else {
            
            // Retrieve a list of all active clubs
            try {
                con = Connect.getCon(rev);
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive='0' ORDER BY clubname");

                HttpSession session = req.getSession(false);

                String user = (String)session.getAttribute("user");   // get username
                
                // Print files out to a table
                out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                out.println("<h2 align=\"center\">View Roster Sync Error Logs</h2>");
                if (user.startsWith(support)) {
                    out.println("<br><CENTER><a href=\"/" + rev + "/support_main2.htm\">Return</a>");
                } else {
                    out.println("<br><CENTER><a href=\"/" + rev + "/sales_main.htm\">Return</a></CENTER>");
                }
                out.println("<br><table border=\"1\" align=\"center\" bgcolor=\"#F5F5DC\" cellpadding=\"1\" cellspacing=\"1\">");
                out.println("<tr valign=\"top\">");

                // Loop through clubs and grab error log files for those that have them
                while (rs.next()) {
                    try {
                        f = new File(announce_path + rs.getString("clubname") + "/" + rs.getString("clubname") + "-rsync-log.txt");
                        if (f.exists()) {
                            out.println("<tr><td>");
                            out.println("<a href=\"Support_sync?rtype=errorlogs&file=" + f.getPath() + "\" style=\"font-size:10pt; color:black\">" + rs.getString("clubname") + "-rsync-log.txt" + "</a>");
                            out.println("</td></tr>");
                        }

                    } catch (Exception exc) {
                        out.println("Failed: could not access specified files");
                    }
                }
                out.println("</table>");
                if (user.startsWith(support)) {
                    out.println("<br><CENTER><a href=\"/" + rev + "/support_main2.htm\">Return</a>");
                } else {
                    out.println("<br><CENTER><a href=\"/" + rev + "/sales_main.htm\">Return</a></CENTER>");
                }
                out.println("</font>");
                 
            } catch (Exception exc) {
                out.println("Failed: Error communicating with database");
            } finally {

                try { rs.close(); }
                catch (Exception ignore) { }

                try { stmt.close(); }
                catch (Exception ignore) { }

                try { con.close(); }
                catch (Exception exc) {
                    Utilities.logError("Support_sync - displayErrorLogs - Error closing DB connection!");
                }
            }
        }
    }

    //
    // Attempts to connect to the club's database and retrieve the last sync date for the member specified.
    //
    private String getSyncDate(String club, String memid, String fname, String lname, String suffix) {
        
        Connection con = null;
        PreparedStatement pstmt = null;
        StringTokenizer tok = null;
        ResultSet rs = null;
        
        try {
            
            con = Connect.getCon(club);
            
            try {

                // Make sure formatting is correct on member data
                tok = new StringTokenizer(fname, " ");
                if (tok.countTokens() > 1) {
                    fname = tok.nextToken();            // Trim MI if present
                }
                
                fname.trim();
                lname.trim();

                if (!suffix.equals("?") && !suffix.equals("")) {
                    lname = lname + "_" + suffix;
                }
                
                // Attempt to search for the member in the database and get their last sync date
                pstmt = con.prepareStatement("SELECT last_sync_date FROM member2b " +
                        "WHERE (memNum = ? AND name_first = ? AND name_last = ?) OR (name_first = ? AND name_last = ?)");
                pstmt.clearParameters();
                pstmt.setString(1, memid);
                pstmt.setString(2, fname);
                pstmt.setString(3, lname);
                pstmt.setString(4, fname);
                pstmt.setString(5, lname);
                rs = pstmt.executeQuery();

                // if a result was found return the last_sync_date
                if (rs.next()) {
                    return String.valueOf(rs.getDate("last_sync_date"));
                } else {
                    return "could not locate " + fname + " " + lname + ", " + memid;
                }
                
            } catch (Exception exc) {
                return "SQL error";
            } finally {

                try { rs.close(); }
                catch (Exception ignore) { }

                try { pstmt.close(); }
                catch (Exception ignore) { }
            }
            
        } catch (Exception exc) {
            return "DB conn error";
        } finally {

            try { con.close(); }
            catch (Exception exc) {
                Utilities.logError("Support_sync - getSyncDate - " + club + " - Error closing DB connection!");
            }
        }
        
    }

    /**
     * setAllActive - Set all members of this club's inact flags to 0 (set all members active)
     *
     * @param user - user currently logged in
     * @param con - connection to club database
     * @param out - PrintWriter for outputting status messages
     */
    private void setAllActive(HttpServletRequest req, String user, String club, Connection con, PrintWriter out) {

        // Ensure user is using either a support or sales login
        if (user.equals("support") || user.startsWith("sales")) {

            // First time through, prompt the user to confirm
            if (req.getParameter("rtype") != null && req.getParameter("rtype").equals("active")) {

                out.println("<HTML><HEAD><TITLE>Activate All Members for " + club + "</TITLE></HEAD>");
                out.println("<BODY><CENTER><H3></H3>");
                out.println("<BR><BR>This will reset the inactive flag (to active) for all members at <b>" + club + "</b>." +
                        "<br><br>Are you sure you want to do this?");
                out.println("<br><br><button onclick=\"location.href='Support_sync?rtype=active2'\">Continue</button>");
                if (user.startsWith("sales")) {
                    out.println("&nbsp;&nbsp;<button onclick=\"location.href='/" +rev+ "/sales_main.htm'\">Return</button>");
                } else {
                    out.println("&nbsp;&nbsp;<button onclick=\"location.href='/" +rev+ "/support_main2.htm'\">Return</button>");
                }
                out.println("</CENTER></BODY></HTML>");

            } else if (req.getParameter("rtype") != null && req.getParameter("rtype").equals("active2")) {

                // Second time through, action confirmed, complete task
                Statement stmt = null;

                int count = 0;

                try {
                    stmt = con.createStatement();
                    count = stmt.executeUpdate("UPDATE member2b set inact='0'");

                    stmt.close();

                    if (count > 0) {
                        out.println("<HTML><HEAD><TITLE>Action Completed</TITLE></HEAD>");
                        out.println("<BODY><CENTER><H3>Action Completed Successfully</H3>");
                        out.println("<BR><BR>All members for this club have been set to active!");
                    } else {
                        out.println("<HTML><HEAD><TITLE>Action Completed</TITLE></HEAD>");
                        out.println("<BODY><CENTER><H3>No Inactive Members Found</H3>");
                        out.println("<BR><BR>No members appear to be set inactive for this club!");
                    }
                    if (user.startsWith("sales")) {
                        out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
                    } else {
                        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
                    }
                    out.println("</CENTER></BODY></HTML>");

                } catch (Exception exc) {

                    out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
                    out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
                    out.println("<BR><BR>Unable to connect to the DB. Action not completed!");
                    out.println("<BR>Exception: "+ exc.getMessage());
                    if (user.startsWith("sales")) {
                        out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>");
                    } else {
                        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
                    }
                    out.println("</CENTER></BODY></HTML>");
                }
            }
            
        } else {

            out.println("<HTML><HEAD><TITLE>Unauthorized Access</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>Unauthorized Access</H3>");
            out.println("<BR><BR>You are not authorized to perform this action.");
            out.println("<BR><BR> <A HREF=\"http://www.foretees.com\">Return</A>");
            out.println("</CENTER></BODY></HTML>");
        }
    }
}
