/***************************************************************************************     
 *   Support_cluboptions:  This servlet will process the add or init club options request from Support's Init page.
 *
 *                   This is used to set some club options we need before the pro configures the site.
 *
 *
 *   called by:  support_main.htm
 *
 *   created: 6/04/2007   Bob P.
 *
 *   last updated:
 *
 *      6/29/10   Added stripalpha and stripdash options for configuring seamless interfaces.
 *      4/23/10   Added abilty to switch clubs, create symlinks and sync-webapps
 *      4/21/10   Removed guest1-guest36 from club5 INSERT statement.
 *      2/04/10   Added 'website_url' field to club5 option form.
 *      1/25/10   Add allow-mobile option to indicate if club will allow members to use the mobile interface.
 *     10/09/09   Add foretees_mode and genrez_mode.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_cluboptions extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*********************************************************************************
 // Process the request from support_main.htm
 //*********************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   String support = "support";             // valid username
   //String clubname = "";

   HttpSession session = null; 


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


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name


   if (req.getParameter("switchClubs") != null) {

       switchClubs(req, out);
       return;
   }


   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   boolean exists = false;
     
   String zipcode = "";
   String website_url = "";

   int notification = 0;
   int seamless = 0;
   int rsync = 0;
   int primaryif = 0;
   int mnum = 0;
   int mapping = 0;
   int stripzero = 0;
   int stripalpha = 0;
   int stripdash = 0;
   int lottery = 0;
   int foretees_mode = 0;
   int genrez_mode = 0;
   int mobile = 0;
   
   String lottery_text = "";

   //
   //  See if the club5 table exists yet
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT clubName FROM club5");   // check for club5 table entry

      if (rs.next()) {

         // club5 DB Table already exists

         exists = true;

      } else {
        
         exists = false;
      }

      stmt.close();

   }
   catch (Exception exc) {

      exists = false;
   }


   if (exists == false ) {             // create entry if club5 does not exist yet

      try {

         PreparedStatement pstmt1 = con.prepareStatement (
            "INSERT INTO club5 (clubName, multi, lottery, contact, email, " +
            "mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
            "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
            "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24, " +
            "mship1, mship2, mship3, mship4, mship5, mship6, mship7, mship8, " +
            "mship9, mship10, mship11, mship12, mship13, mship14, mship15, mship16, " +
            "mship17, mship18, mship19, mship20, mship21, mship22, mship23, mship24, " +
            "x, xhrs, adv_zone, emailOpt, lottid, hotel, userlock, " +
            "unacompGuest, hndcpProSheet, hndcpProEvent, hndcpMemSheet, hndcpMemEvent, " +
            "posType, logins, rndsperday, hrsbtwn, forcegnames, hidenames, " +
            "constimesm, constimesp, precheckin, paceofplay, no_reservations, " +
            "salestax, nwindow_starttime, nwindow_endtime, notify_interval, " +
            "hdcpSystem, allowMemPost, lastHdcpSync, hdcpStartDate, hdcpEndDate, " +
            "rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero) " +
            "VALUES ('',0,0,'','', " +
            "'','','','','','','','','','','','','','','','','','','','','','','','', " +
            "'','','','','','','','','','','','','','','','','','','','','','','','', " +
            "0,0,'',0,0,0,0, " +
            "0,0,0,0,0, " +
            "'',0,0,0,0,0, " +
            "0,0,0,0,0, " +
            "0,'00:00:00','00:00:00',0," +
            "'',0,'0000-00-00','0000-00-00 00:00:00','0000-00-00 00:00:00', " +
            "0,0,'',0,0,0,0)");

         pstmt1.executeUpdate();          // execute the prepared stmt

         pstmt1.close();

      }
      catch (Exception exc) {

         out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
         out.println("<BR><BR>Error inserting row in club5 for new club.");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   } else {
   
      //
      //  club5 exists - get current values
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT lottery, no_reservations, rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, " +
                                "lottery_text, foretees_mode, genrez_mode, allow_mobile, website_url, stripalpha, stripdash " +
                                "FROM club5");

         if (rs.next()) {

            lottery = rs.getInt(1);
            notification = rs.getInt(2);
            rsync = rs.getInt(3);
            seamless = rs.getInt(4);
            zipcode = rs.getString(5);
            primaryif = rs.getInt(6);
            mnum = rs.getInt(7);
            mapping = rs.getInt(8);
            stripzero = rs.getInt(9);
            lottery_text = rs.getString(10);
            foretees_mode = rs.getInt("foretees_mode");
            genrez_mode = rs.getInt("genrez_mode");
            mobile = rs.getInt("allow_mobile");
            website_url = rs.getString("website_url");
            stripalpha = rs.getInt("stripalpha");
            stripdash = rs.getInt("stripdash");
         }
         stmt.close();
        
      }
      catch (Exception exc) {

         out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
         out.println("<BR><BR>Error getting club5 data.");
         out.println("<BR>Exception: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

   }  // end of IF exists

   //
   //  Now present a form to display and gather the options
   //
   out.println("<HTML><HEAD><TITLE>Support Club Options</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><H3>Set Club Options</H3><b>" + club + "</b><br>");
   out.println("<BR><b>NOTE:</b> We must set the following options for all new clubs.");
   out.println("<BR><BR>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"550\">");
      out.println("<tr bgcolor=\"#336633\"><td align=\"center\" colspan=\"2\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("<font size=3><b>Club Options</b></font><br>");
      out.println("<br>Change the desired information below.<br>");
      out.println("Click on <b>Submit</b> to submit the record.");
      out.println("</font></td></tr>");
        
      out.println("<form action=\"/" +rev+ "/servlet/Support_cluboptions\" method=\"post\">");
        
      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;ForeTees Golf System (either)?:&nbsp;&nbsp;");      // ForeTees Golf (Tee Times or Notification)
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"foretees_mode\">");
      if (foretees_mode == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Notification System (NO Tee Times)?:&nbsp;&nbsp;");       // Notification
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"notification\">");
      if (notification == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;FlxRez System (Courts, etc)?:&nbsp;&nbsp;");             // GenRez
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"genrez_mode\">");
      if (genrez_mode == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Allow Mobile Access?:&nbsp;&nbsp;");           // Mobile
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"mobile\">");
      if (mobile == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Roster Sync?:&nbsp;&nbsp;");             // Roster Sync
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"rsync\">");
      if (rsync == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Seamless Interface?:&nbsp;&nbsp;");        // Seamless
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"seamless\">");
      if (seamless == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Primary-Only Interface?:&nbsp;&nbsp;");       // Primary
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"primaryif\">");
      if (primaryif == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Username = Member Number (mNum)?:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"mnum\">");
      if (mnum == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Use Mapping (to webid)?:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"mapping\">");
      if (mapping == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Strip Leading Zero?:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"stripzero\">");
      if (stripzero == 0) {
         out.println("<option selected value=\"No\">No</option>");
         out.println("<option value=\"Yes\">Yes</option>");
      } else {
         out.println("<option value=\"No\">No</option>");
         out.println("<option selected value=\"Yes\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Strip Trailing Alpha Character?:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"stripalpha\">");
      if (stripalpha == 0) {
         out.println("<option selected value=\"0\">No</option>");
         out.println("<option value=\"1\">Yes</option>");
      } else {
         out.println("<option value=\"0\">No</option>");
         out.println("<option selected value=\"1\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Strip Trailing Unique Dash Identifier (e.g. -001)?:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<select size=\"1\" name=\"stripdash\">");
      if (stripdash == 0) {
         out.println("<option selected value=\"0\">No</option>");
         out.println("<option value=\"1\">Yes</option>");
      } else {
         out.println("<option value=\"0\">No</option>");
         out.println("<option selected value=\"1\">Yes</option>");
      }
      out.println("</select>");
      out.println("</td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Zip Code:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("<input type=\"text\" id=\"zipcode\" name=\"zipcode\" value=\"" +zipcode+ "\" size=\"5\" maxlength=\"5\">");
      out.println("</font></td></tr>");

      out.println("<tr><td align=\"left\" width=\"300\">");
      out.println("<font size=\"2\"><br>");
      out.println("&nbsp;&nbsp;Website:&nbsp;&nbsp;");
      out.println("</td><td align=\"left\">");
      out.println("http://<input type=\"text\" id=\"website\" name=\"website\" value=\"" +website_url+ "\" size=\"20\" maxlength=\"100\">");
      out.println("</font></td></tr>");
      
      if (lottery > 0) {
         out.println("<tr><td align=\"left\" width=\"300\">");
         out.println("<font size=\"2\"><br>");
         out.println("&nbsp;&nbsp;Replacement text for 'Lottery' word:&nbsp;&nbsp;<br>");
         out.println("&nbsp;&nbsp;(i.e. Tee Time Request or Draw Request)&nbsp;&nbsp;");
         out.println("</td><td align=\"left\">");
         out.println("<input type=\"text\" id=\"lottery_text\" name=\"lottery_text\" value=\"" +lottery_text+ "\" size=\"20\" maxlength=\"30\">");
         out.println("</font></td></tr>");
      }

      // if this is running on node #1 (Server id of 7 or 8)
      if (Common_Server.SERVER_ID == 8 || Common_Server.SERVER_ID == 7 || Common_Server.SERVER_ID == 4) {

          out.println("<tr><td colspan=2 align=\"center\">");
          out.println("<font size=\"2\"><form method=post action=\"/v5/servlet/Support_cluboptions\">");
          out.println("<input type=submit name=symlinks value=\" Create Symlinks \">");

        // don't allow sync to be called from dev server
        if (Common_Server.SERVER_ID != 4) {
          out.println("&nbsp;&nbsp;&nbsp;&nbsp;");
          out.println("<input type=submit name=syncwebapps value=\" Sync Servers \">");
        }
          out.println("</font></td></tr>");

      }

      out.println("</table>"); 
      out.println("<br><br>");

      out.println("<p align=\"center\">");
      out.println("<input type=\"submit\" name=\"Submit\" value=\"Submit\">");
      out.println("</form>");

   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return - Cancel</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }        // end of doGet
 

 //*********************************************************************************
 // Process the form request from above
 //*********************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   //Statement stmt = null;
   //ResultSet rs = null;

   String support = "support";             // valid username
   //String clubname = "";

   HttpSession session = null;


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


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name


   if (req.getParameter("symlinks") != null && (Common_Server.SERVER_ID == 8 || Common_Server.SERVER_ID == 7 || Common_Server.SERVER_ID == 4)) {

       doSymlinks(club, out);
       return;

   } else if (req.getParameter("syncwebapps") != null && (Common_Server.SERVER_ID == 8 || Common_Server.SERVER_ID == 7)) {

       doSyncwebapps(out);
       return;

   }


   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   //
   //  Get the parms passed
   //
   boolean exist = false;
   
   String lottery_text = "";
   String temp = "";
   String zipcode = "";
   String website_url = "";

   int notification = 0;
   int seamless = 0;
   int rsync = 0;
   int primaryif = 0;
   int mnum = 0;
   int mapping = 0;
   int stripzero = 0;
   int stripalpha = 0;
   int stripdash = 0;
   int foretees_mode = 0;
   int genrez_mode = 0;
   int mobile = 0;

   //
   //  Get the parms 
   //
   temp = req.getParameter("notification");           //  get club options
   
   if (temp.equals( "Yes" )) {
      notification = 1;
   }
   
   temp = req.getParameter("foretees_mode");
   
   if (temp.equals( "Yes" )) {
      foretees_mode = 1;
   }
   
   temp = req.getParameter("genrez_mode");
   
   if (temp.equals( "Yes" )) {
      genrez_mode = 1;
   }
   
   temp = req.getParameter("mobile");
   
   if (temp.equals( "Yes" )) {
      mobile = 1;
   }
   
   temp = req.getParameter("seamless");
   
   if (temp.equals( "Yes" )) {
      seamless = 1;
   }
   
   temp = req.getParameter("rsync");
   
   if (temp.equals( "Yes" )) {
      rsync = 1;
   }
   
   temp = req.getParameter("primaryif");
   
   if (temp.equals( "Yes" )) {
      primaryif = 1;
   }
   
   temp = req.getParameter("mnum");
   
   if (temp.equals( "Yes" )) {
      mnum = 1;
   }
   
   temp = req.getParameter("mapping");
   
   if (temp.equals( "Yes" )) {
      mapping = 1;
   }
   
   temp = req.getParameter("stripzero");
   
   if (temp.equals( "Yes" )) {
      stripzero = 1;
   }   
   
   zipcode = req.getParameter("zipcode");
   website_url = req.getParameter("website");
   stripalpha = Integer.parseInt(req.getParameter("stripalpha"));
   stripdash = Integer.parseInt(req.getParameter("stripdash"));

   if (req.getParameter("lottery_text") != null) {

      lottery_text = req.getParameter("lottery_text");
   }



   //
   //  Update the club5 table
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "UPDATE club5 SET no_reservations = ?, rsync = ?, seamless = ?, zipcode = ?, primaryif = ?, " +
         "mnum = ?, mapping = ?, stripzero = ?, lottery_text = ?, foretees_mode = ?, genrez_mode = ?," +
         "allow_mobile = ?, website_url = ?, stripalpha = ?, stripdash = ?");

      pstmt.clearParameters();            // clear the parms
      pstmt.setInt(1, notification);
      pstmt.setInt(2, rsync);
      pstmt.setInt(3, seamless);
      pstmt.setString(4, zipcode);
      pstmt.setInt(5, primaryif);
      pstmt.setInt(6, mnum);
      pstmt.setInt(7, mapping);
      pstmt.setInt(8, stripzero);
      pstmt.setString(9, lottery_text);
      pstmt.setInt(10, foretees_mode);
      pstmt.setInt(11, genrez_mode);
      pstmt.setInt(12, mobile);
      pstmt.setString(13, website_url);
      pstmt.setInt(14, stripalpha);
      pstmt.setInt(15, stripdash);
        
      pstmt.executeUpdate();  // execute the prepared stmt

      pstmt.close();

   }
   catch (Exception exc) {

      // SQL Error ....

      out.println("<HTML><HEAD><TITLE>SQL Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>SQL Type Error</H3>");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // DB Table setup complete - inform support....

   out.println("<HTML><HEAD><TITLE>Club5 Table Update Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Club5 Table Successfully Updated</H3>");
   out.println("<BR><BR>Please continue.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
 }   
    
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

 private void switchClubs(HttpServletRequest req, PrintWriter out) {

    HttpSession session = req.getSession(false);

    if (session == null) {

        invalidUser(out);
        return;
    }

    if (req.getParameter("doSwitch") != null) {

        // perform the switch
        session.setAttribute("club", req.getParameter("newclub") );
        out.println("<br><center><h4>Switching to club: " + req.getParameter("newclub") + "</h4>");
        out.println("<form><input type=hidden name=\"switchClubs\"><input type=hidden name=\"verifySwitch\"><input type=submit name=verifySwitch value=\" Continue \"></form></center>");
        out.println("<script>setTimeout('document.forms[0].submit();', 1500)</script>");
        
    } else if (req.getParameter("verifySwitch") != null) {

        String club = (String)session.getAttribute("club");
        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/support_main.htm\">");
        out.println("<br><center><h3>Current Club is now: " + club + "</h3></center>");
        out.println("<p align=\"center\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><a href=\"/" + rev + "/support_main.htm\" target=\"_top\">Main Menu</a></font></p>");

    } else {

        Connection con = null;

        // display current club name & a list of all available clubs
        String club = (String)session.getAttribute("club");

        try { con = dbConn.Connect("v5"); }
        catch (Exception exc) {

            // Error connecting to db....

            out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the DB.");
            out.println("<BR>Exception: "+ exc.getMessage());
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
            out.println("</CENTER></BODY></HTML>");
            return;
        }

        Statement stmt = null;
        ResultSet rs = null;
        boolean found_inact = false;

        out.println("<br><center><h3>Current Club is: " + club + "</h3>");

        out.println("<p>Select new club from list.</p>");
        out.println("<form>");
        out.println("<input type=hidden name=\"switchClubs\">");
        //out.println("<input type=hidden name=\"doSwitch\">");

        out.println("<select size=1 name=\"newclub\">");

        try {

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT * FROM v5.clubs ORDER BY inactive = 0 DESC, clubname");

            out.println("<optgroup label=\"Active Clubs\">");

            while (rs.next()) {

                //out.println("<option value=\"" + rs.getString("clubname") + "\">" + rs.getString("clubname") + ((rs.getInt("inactive") == 1) ? "*" : "") + "</option>");

                if (!found_inact && rs.getInt("inactive") == 1) {

                    out.println("</optgroup>");
                    out.println("<optgroup label=\"Inactive Clubs\">");
                    found_inact = true;
                }

                out.println("<option>" + rs.getString("clubname") + "</option>");



            }
            stmt.close();


        } catch (Exception exc) {

            out.println("<p>ERROR: " + exc.toString() + "</p>");

        }

        out.println("</optgroup>");
        out.println("</select>");
        out.println("<input type=submit name=doSwitch value=\" Switch \">");
        out.println("</form></center>");

    }
    

 }


 private void doSymlinks(String club, PrintWriter out) {

    String result = "";

    try {

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Create Symlinks for " + club + "</title>");
        out.println("</head>");
        out.println("<body>");


        //
        // ADD CHECK TO MAKE SURE USER IS RUNINNG THIS ON NODE #1 or dev!!!
        //

        String cm = "ln -s /usr/local/tomcat/webapps/ROOT/login.jsp /usr/local/tomcat/webapps/" + club + "/login.jsp";
        Process p = Runtime.getRuntime().exec(cm);
        out.println("<pre>" + cm + "</pre>");

        cm = "ln -s /usr/local/tomcat/webapps/ROOT/mlogin.jsp /usr/local/tomcat/webapps/" + club + "/mlogin.jsp";
        p = Runtime.getRuntime().exec(cm);
        out.println("<pre>" + cm + "</pre>");

        cm = "ls -lh /usr/local/tomcat/webapps/" + club;
        p = Runtime.getRuntime().exec(cm);
        out.println("<pre>" + cm + "</pre>");

        InputStream in = p.getInputStream();
        int ch;
        StringBuffer sb = new StringBuffer(512);

        while ( ( ch = in.read() ) != -1 )
        {	sb.append((char) ch); }

        result = sb.toString();

    } catch (Exception exc) {

        out.println("ERROR:" + exc.toString());

    }

    out.println("<pre>" + result + "</pre>");

    out.println("<p align=\"center\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><a href=\"/" + rev + "/servlet/Support_cluboptions\" target=\"_top\">Club Options</a></font></p>");
    out.println("<p align=\"center\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><a href=\"/" + rev + "/support_main.htm\" target=\"_top\">Main Menu</a></font></p>");

    out.println("</body>");
    out.println("</html>");
    out.close();

 }


 private void doSyncwebapps(PrintWriter out) {

    String result = "";

    try {

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Sync webapps Folders</title>");
        out.println("</head>");
        out.println("<body>");

        String cm = "/root/scripts/sync-webapps.sh";

        Process p = Runtime.getRuntime().exec(cm);
        out.println("<pre>" + cm + "</pre>");

        InputStream in = p.getInputStream();
        int ch;
        StringBuffer sb = new StringBuffer(512);

        while ( ( ch = in.read() ) != -1 )
        {	sb.append((char) ch); }

        result = sb.toString();

    } catch (Exception exc) {

        out.println("ERROR:" + exc.toString());

    }

    out.println("<pre>" + result + "</pre>");

    out.println("<p align=\"center\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><a href=\"/" + rev + "/servlet/Support_cluboptions\" target=\"_top\">Club Options</a></font></p>");
    out.println("<p align=\"center\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><a href=\"/" + rev + "/support_main.htm\" target=\"_top\">Main Menu</a></font></p>");

    out.println("</body>");
    out.println("</html>");
    out.close();

 }

}
