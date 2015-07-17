/***************************************************************************************     
 *   Support_errorlog:  This servlet will read the errorlog db table and display the entries.
 *
 *   This is also used to search for a member by their email address. (search = yes)
 *
 *   called by:  support_main2.htm
 *
 *   last updated:
 *
 *        5/27/09   Added iCal option toggling to email search and added toggleICalOpt method to handle this operation
 *        9/02/08   Javascript compatability updates
 *        5/19/08   changeAddr() if statement changed from || to && to fix a bug where updating an email 
 *                  when only one email was present for the account would fail to update it.  Changed
 *                  removeAddr() to only set emailOpt=0 when email1 is being removed.  
 *                  ensureCorrectEmailSetup() changed to static.
 *        5/06/08   Added clearBouncedStatus, ensureCorrectEmailSetup, and toggleEmailOpt methods
 *                  Added ability to clear bounced indicators (button), ability to toggle emailOpt value,
 *                  ability to change both emails simultaneously, and checking to ensure that emails are
 *                  set up properly (i.e. email not blank while email2 populated, bounced values not set
 *                  incorrectly).   
 ***************************************************************************************
 */
    
import java.io.*;
//import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class Support_errorlog extends HttpServlet {
                           

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String support = "support";

   String user = (String)session.getAttribute("user");   // get username
   String club = (String)session.getAttribute("club");   // get club name


   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   //  Check if call is to Search for a Member's Email Address
   //
   if (req.getParameter("search") != null) {        // if call is for a member search

      search(req, out);
      return;
   }
   
   if (req.getParameter("bulkSearch") != null) {        // if call is for a member search

      bulkSearch(req, out);
      return;
   }
   
   //
   //  Check if call is to Display the Session Log 
   //
   if (req.getParameter("sess") != null) {      

      sessLog(req, out, club);
      return;
   }

   //
   //**********************************************
   //  Display the error log table
   //**********************************************
   //
   Statement stmt2 = null;
   ResultSet rs = null;
   Connection con = null;
   String db = rev;                       // get db name to use for 'clubs' table

   String sdate = "";
   String stime = "";
   String msg = "";
   String bgcolor = "#F5F5DC";
   String bgcolor1 = "#F5F5DC";
   String bgcolor2 = "#CCCCAA";

   String bgcolor3 = "#F5F5DC";
   String bgcolor3a = "#F5F5DC";
   String bgcolor3b = "#CCCCAA";
   
   boolean flip = false;
   String flip_date = "";
   
   int count = 1;
  
   out.println("<HTML><HEAD><TITLE>Display Error Log</TITLE>" +
           "<meta http-equiv='Refresh' content='900; url=/v5/servlet/Support_errorlog'>" +
           "</HEAD>");
   out.println("<BODY><CENTER><BR><H2>ForeTees Error Log</H2>");
   out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("<BR><BR><b>Entries older than 1 week are automatically deleted.</b>");
   out.println("<BR><BR>");

   out.println("<table border=\"1\" cellpadding=\"4\">");

   try {

      con = dbConn.Connect(db);                  // get a connection to Vx db

      if (con != null) {                         // if we got one

         stmt2 = con.createStatement();

         rs = stmt2.executeQuery("" +
                 "SELECT msg, " +
                 "DATE_FORMAT(err_timestamp, \"%a %b %d\") AS sdate, " +
                 "DATE_FORMAT(err_timestamp, \"%h:%i %p\") AS stime " +
                 "FROM errorlog " +
                 "ORDER BY err_timestamp DESC, id ASC");
         
         while (rs.next()) {

            sdate = rs.getString("sdate");
            stime = rs.getString("stime");
            msg = rs.getString("msg");

            if (!flip_date.equals(sdate)) {
                flip = (flip == false);
                flip_date = sdate;
                bgcolor3 = (flip) ? bgcolor3a : bgcolor3b;
            }
            
            //  display the line
            out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");
            out.println("<font size=\"2\"> " + count + " </font>");
            out.println("</td>");
            out.println("<td align=\"left\" nowrap bgcolor=\""+ bgcolor3 +"\">");
            out.println("<font size=\"2\">" + sdate + "</font>"); // + " " + stime
            out.println("</td>");
            out.println("<td align=\"left\" nowrap bgcolor=\""+ bgcolor3 +"\">");
            out.println("<font size=\"2\">" + stime + "</font>");
            out.println("</td>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">" + msg + "</font>");
            out.println("</td></tr>");

            count++;

            if (bgcolor.equals( bgcolor1 )) {

               bgcolor = bgcolor2;

            } else {

               bgcolor = bgcolor1;
            }

         }   // end of while
         
      }

   }
   catch (Exception e3) {

      out.println("<H3>Text File Read Failed</H3>");
      out.println("<BR><BR>Error.txt Failed");
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }
   finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt2.close(); }
        catch (Exception ignore) {}

        try { con.close(); }
        catch (Exception ignore) {}

    }

   out.println("</table>");
   out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }   
    

 //*************************************************************
 //  doPost
 //*************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String support = "support";

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   //  Check if call is to Search for a Member's Email Address
   //
   if (req.getParameter("search") != null) {        // if call is for a member search

      search(req, out);
      return;
   }
   
   //
   //  Check if call is to Change a Member's emailOpt value
   //   
   if (req.getParameter("btnToggle") != null && (req.getParameter("btnToggle").equals("Toggle"))) {
       
       toggleEmailOpt(req, out);
       return;
   }

   //
   //  Check if call is to Change a Member's Email Address
   //
   if (req.getParameter("btnChange") != null && (req.getParameter("btnChange").equals("Change")
                                              || req.getParameter("btnChange").equals("Submit Changes"))) {   

       changeAddr(req, out);
       return;
   }
   
   //
   //  Check if call is to Clear a Member's Email_Bounced status
   //   
   if (req.getParameter("btnClear") != null && req.getParameter("btnClear").equals("Clear Bounced Status")) {
       
       clearBouncedStatus(req, out);
       return;
   }

   //
   //  Check if call is to toggle a member's iCal option
   //
   if (req.getParameter("btnICal1Toggle") != null || req.getParameter("btnICal2Toggle") != null) {

       toggleICalOpt(req, out);
       return;
   }

   //
   //  Check if call is to Remove a Member's Email Address
   //
   if (req.getParameter("remove") != null) {   

      removeAddr(req, out);
      return;
   }

   
   //
   //  Check if call is to Remove a Member's Email Address
   //
   if (req.getParameter("bulk") != null) {   

      removeBulkAddr(req, out);
      return;
   }
   
 }


 // *********************************************************
 // Call is to display the Session Log - current month only
 // *********************************************************

 private void sessLog(HttpServletRequest req, PrintWriter out, String club) {


   Statement stmt2 = null;
   ResultSet rs = null;
   Connection con = null;


   try {
      con = dbConn.Connect(club);               // get a connection
   }
   catch (Exception e1) {

      return;
   }

   //
   //  read in the text file
   //
   int fail = 0;
   int count = 1;
   String sdate = "";
   String msg = "";
   String bgcolor = "#F5F5DC";
   String bgcolor1 = "#F5F5DC";
   String bgcolor2 = "#CCCCAA";


   out.println("<HTML><HEAD><TITLE>Display Session Log</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><H2>ForeTees Session Log - " +club+ "</H2>");
   out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("<BR><BR><b>Entries older than 1 week are automatically deleted.</b>");
   out.println("<BR><BR><b>Most recent sessions are listed first.</b>");
   out.println("<BR><BR>");

   out.println("<Table border=\"1\" cellpadding=\"4\">");

   try {

      stmt2 = con.createStatement();              // create a statement

      rs = stmt2.executeQuery("SELECT sdate, msg FROM sessionlog ORDER BY date DESC, sdate DESC");

      while (rs.next()) {

         sdate = rs.getString(1);
         msg = rs.getString(2);

         //  display the line
         out.println("<tr bgcolor=\"" +bgcolor+ "\"><td align=\"center\">");
         out.println("<font size=\"2\"> " + count + " </font>");
         out.println("</td>");
         out.println("<td align=\"left\">");
         out.println("<font size=\"2\">" + sdate + "</font>");
         out.println("</td>");
         out.println("<td align=\"left\">");
         out.println("<font size=\"2\">" + msg + "</font>");
         out.println("</td></tr>");
           
         count++;
           
         if (bgcolor.equals( bgcolor1 )) {
           
            bgcolor = bgcolor2;
              
         } else {
           
            bgcolor = bgcolor1;
         }

      }   // end of while

      stmt2.close();

   }
   catch (Exception e3) {

      out.println("<H3>Display Session Log Failed</H3>");
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;

   }

   out.println("</table>");
   out.println("<BR><A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Call is for a search
 // *********************************************************

 private void search(HttpServletRequest req, PrintWriter out) {


   Connection con = null;
   Connection con2 = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String muser = "";
   String mpw = "";
   String mlast = "";
   String mfirst = "";
   String mmi = "";
   String memail = "";
   String memail2 = "";
   String fname = "";
   String last_sync_date = "";
   int emailOpt = 0;
   int email_bounced = 0;
   int email2_bounced = 0;
   int iCalOpt1 = 0;
   int iCalOpt2 = 0;
   int count = 0;

   
   //
   //  First call will display a page to prompt for the member's email address.
   //  Second call will perform the search and display the results.
   //
   if (req.getParameter("email") != null) {        // if 2nd call call

      //
      //  Called to search for an email address - get the addr and search for it
      //
      String email = req.getParameter("email");        //  address or portion of it
      email = email.trim();

      int length = email.length();                    // get length of email requested

      //
      //  verify the required fields
      //
      if ((email.equals( "" )) || (length > 60)) {

         invData(out);    // inform the user and return
         return;
      }

      //
      //   Add a % to the email provided so search will match anything close
      //
      StringBuffer buf = new StringBuffer("%");
      buf.append( email );
      buf.append("%");
      String semail = buf.toString();

      //
      //  Perform search for each club in the system database 'clubs' table
      //
      String club = "" +rev+ "";                       // get db name to use for 'clubs' table

      try {
         con = dbConn.Connect(club);               // get a connection
      }
      catch (Exception e1) {

         return;
      }

      //
      //   build the HTML page for the display
      //
      out.println(SystemUtils.HeadTitle("Support Email Search Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr><td align=\"center\" valign=\"top\">");

      out.println("<img src=\"/" +rev+ "/images/foretees.gif\">");
      out.println("<hr width=\"40%\"><br>");

      out.println("<font size=\"3\">");
      out.println("<p><b>Search Results</b></p>");
      out.println("</font><font size=\"2\">");

         out.println("<table border=\"1\" bgcolor=\"#FFFFCC\" cellpadding=\"5\">");
            out.println("<tr bgcolor=\"#CC9966\"><td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>User</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Password</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Name</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Club</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Email Opt</b></u></p>");
                  out.println("</font></td>");
                  
               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Email 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>iCal Opt 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Email 2</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>iCal Opt 2</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Last Sync</b></u></p>");
                  out.println("</font></td>");
            out.println("</tr>");

      //
      //  Get each record and display it
      //
      count = 0;             // number of records found

      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         stmt2 = con.createStatement();              // create a statement

         rs2 = stmt2.executeQuery("SELECT clubname FROM clubs");

         while (rs2.next()) {
         
            club = rs2.getString(1);                 // get a club name
            con2 = dbConn.Connect(club);        // get a connection to this club's db

            //
            // use the name to search table
            //
            PreparedStatement stmt = con2.prepareStatement (
               "SELECT username, password, name_last, name_first, name_mi, email, email2, emailOpt, email_bounced, email2_bounced, DATE_FORMAT(last_sync_date, '%Y-%m-%d') AS lastSyncd, iCal1, iCal2 " +
               "FROM member2b " +
               "WHERE email LIKE ? OR email2 LIKE ?");

            stmt.clearParameters();        // clear the parms
            stmt.setString(1, semail);
            stmt.setString(2, semail);
            rs = stmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               count++;

               muser = rs.getString(1);
               mpw = rs.getString(2);
               mlast = rs.getString(3);
               mfirst = rs.getString(4);
               mmi = rs.getString(5);
               memail = rs.getString(6);
               memail2 = rs.getString(7);
               emailOpt = rs.getInt(8);
               email_bounced = rs.getInt(9);
               email2_bounced = rs.getInt(10);
               last_sync_date = rs.getString(11);
               iCalOpt1 = rs.getInt("iCal1");
               iCalOpt2 = rs.getInt("iCal2");

               if (mmi.equals( "" )) {

                  fname = mfirst + " " + mlast;
               } else {

                  fname = mfirst + " " + mmi + " " + mlast;
               }

               out.println("<tr><td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + muser + "</u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + mpw + "</u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + fname + "</p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + club + "</p>");
                     out.println("</font></td>");

                  out.println("<form action=\"/" + rev + "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + emailOpt);
                     out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                     out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                     out.println("<input type=\"hidden\" name=\"email\" value=\"" +memail+ "\">");
                     out.println("<br><br><input type=\"submit\" name=\"btnToggle\" value=\"Toggle\">");
                     out.println("</p></font></td></form>");
                     
                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<input type=\"hidden\" name=\"email\" value=\"" +memail+ "\">");
                  out.println("<input type=\"hidden\" name=\"remove\" value=\"email1\">");
                  out.println("<td nowrap " + ((email_bounced != 0) ? "style=\"background-color: yellow\"" : "") + ">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + memail + " &nbsp;&nbsp;");
                     if (!memail.equals( "" )) {
                        if (email_bounced != 0)
                            out.println("<br><br><input type=\"submit\" name=\"btnClear\" value=\"Clear Bounced Status\">&nbsp;");
                        out.println("<input type=\"submit\" name=btnRemove value=\"Remove\">&nbsp;");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</p></font></td></form>");
                     
                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + iCalOpt1);
                     out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                     out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                     out.println("<input type=\"hidden\" name=\"email\" value=\"" +memail+ "\">");
                     out.println("<br><br><input type=\"submit\" name=\"btnICal1Toggle\" value=\"Toggle\">");
                     out.println("</p></font></td></form>");

                  out.println("<form action=\"/" + rev + "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<input type=\"hidden\" name=\"email\" value=\"" +memail2+ "\">");
                  out.println("<input type=\"hidden\" name=\"remove\" value=\"email2\">");
                  out.println("<td nowrap " + ((email2_bounced != 0) ? "style=\"background-color: yellow\"" : "") + ">");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + memail2 + " &nbsp;&nbsp;");
                     if (!memail2.equals( "" )) {
                        if (email2_bounced != 0)
                            out.println("<br><br><input type=\"submit\" name=\"btnClear\" value=\"Clear Bounced Status\">&nbsp;");
                        out.println("<input type=\"submit\" name=btnRemove value=\"Remove\">&nbsp; ");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</p></font></td></form>");
                     
                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + iCalOpt2);
                     out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                     out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                     out.println("<input type=\"hidden\" name=\"email\" value=\"" +memail2+ "\">");
                     out.println("<br><br><input type=\"submit\" name=\"btnICal2Toggle\" value=\"Toggle\">");
                     out.println("</p></font></td></form>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + last_sync_date + "</p>");
                     out.println("</font></td>");
               out.println("</tr>");

            }    // end of while

            stmt.close();

            con2.close();                           // close the connection to the club db
         }                        // end of while clubs

         stmt2.close();

         con.close();                              // close the connection to the system db

         out.println("</font></table>");

         if (count == 0) {

            out.println("<p align=\"center\">No records found for " + email + ".</p>");

         } else if (count > 5) {

            out.println("<p align=\"center\">Count = " + count + "</p>");

         }
         out.println("</td></tr></table></td>");                // end of main page table & column
         out.println("</font>");
         out.println("<font size=\"2\">");

         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
         out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
         out.println("<input type=\"submit\" value=\"Search Again\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</form>");
         
         out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
         out.println("<input type=\"submit\" value=\"Support Menu\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</form>");               // return to searchmain.htm
                  
         out.println("</font>");

         //
         //  End of HTML page
         //
         out.println("</center></font></body></html>");

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER><BR>");
         out.println("<BR><BR><H3>Database Access Error</H3>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Error:" + exc.getMessage());
         out.println("<BR><BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
         out.println("</CENTER></BODY></HTML>");
      }
      return;

   } else {  // first call - prompt for email address to search for

      out.println("<HTML><HEAD><Title>Support Search Page</Title>");

      out.println("<script type=\"text/javascript\">");
      out.println("<!-- ");
      out.println("function cursor() { document.forms['f'].email.focus(); }");
      out.println("// -->");
      out.println("</script>");

      out.println("</head>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

         out.println("<img src=\"/" +rev+ "/images/foretees.gif\">");
         out.println("<hr width=\"40%\"><br>");

         out.println("<table border=\"1\" bgcolor=\"#CC9966\" cellpadding=\"5\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<p>To locate a member, enter the email address,<br>");
            out.println("or any portion of it, as it may exist in the member database.<br></p>");
            out.println("</font>");
         out.println("</td></tr></table>");

         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

         out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">");

         out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\" name=\"f\">");
         out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
         out.println("<tr><td valign=\"top\" align=\"center\">");

            out.println("<table border=\"1\" bgcolor=\"#FFFFCC\" align=\"center\">");
               out.println("<tr><td width=\"250\" align=\"center\">");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><br>Email Address: &nbsp;");
                     out.println("<input type=\"text\" name=\"email\" size=\"30\" maxlength=\"60\">");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"subsearch\">");
                  out.println("</p>");
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");
         out.println("</td></tr></form>");
         out.println("</table></font>");
         out.println("<font size=\"2\"><br><br>");

         out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"get\">");
         out.println("<input type=hidden name=bulkSearch value=yes>");
         out.println("<input type=\"submit\" value=\"Bulk Remove\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</form>");
         
         out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</form><br>");
         
         out.println("</font>");

      out.println("</center></body></html>");
   }
 }

 
 private void bulkSearch(HttpServletRequest req, PrintWriter out) {
     
     out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
     out.println("<input type=hidden name=bulk value=''>");
     out.println("<textarea name=emailList cols=30 rows=30></textarea>");
     out.println("<br><input type=submit>");
     out.println("</form>");
 }
 
 
 // *********************************************************
 // Call is to remove an email address
 // *********************************************************

 private void removeAddr(HttpServletRequest req, PrintWriter out) {

   Connection con = null;
   ResultSet rs = null;
   PreparedStatement pstmt2 = null;

   int count = 0;

   //out.println("<input type=\"hidden\" name=\"username\" value=\"muser\">");
   //out.println("<input type=\"hidden\" name=\"club\" value=\"club\">");
   //out.println("<input type=\"hidden\" name=\"remove\" value=\"email2\">");
   
   //
   //  Get the parms
   //
   String remove = req.getParameter("remove");    
   String club = req.getParameter("club");
   String user = req.getParameter("username");
   String email = req.getParameter("email");


   try {
      con = dbConn.Connect(club);               // get a connection
   }
   catch (Exception e1) {

      return;
   }

   //
   //  remove the email address
   //
   try {

      pstmt2 = con.prepareStatement (
          "UPDATE member2b SET " +
          ((remove.equals( "email1" )) ? "emailOpt = 0, " : "") +
          ((remove.equals( "email1" )) ? "email" : "email2") + " = '' " +
          "WHERE username = ?");

      pstmt2.clearParameters();
      pstmt2.setString(1, user);
      pstmt2.executeUpdate();
      pstmt2.close();


      out.println(SystemUtils.HeadTitle("Done"));
      out.println("<BODY><CENTER><BR>");
      
      // run check to ensure email is not blank with email2 populated
      ensureCorrectEmailSetup(user, club, out);
      
      out.println("<BR><BR><H3>Email Address Removed</H3>");
      out.println("<BR><BR>Email address for " +user+ " at club " +club+ " has been removed.<br>");

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form>");               // return to searchmain.htm
      
      out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"text\" name=\"email\" size=\"30\" maxlength=\"60\" value=\"" + email + "\">");
      out.println("<input type=\"submit\" value=\"Search Again\" name=\"subsearch\">");
      out.println("</form>");
                  
      out.println("</font>");
      out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">Logout</A>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }

 }

 
 private void removeBulkAddr(HttpServletRequest req, PrintWriter out) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    Connection con = null;
    Connection con2 = null;
    
    String club = "";
    String emails = req.getParameter("emailList");
    String [] email = null;
    email = emails.split("\\r");

    int count = 0;

    out.println("<html><body bgcolor=white>");
    out.println("<h3>Results</h3>");

    try {
        con = dbConn.Connect("v5");               // get a connection
    }
    catch (Exception e1) {
        return;
    }
    
    for (int x=0; x < email.length; x++) {
        
         //out.println("<br>" + email[x]);
     
        email[x] = email[x].trim();
        
        if (!email[x].equals("")) {

            try {

                stmt2 = con.createStatement();              // create a statement
                rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

                while (rs2.next()) {

                    club = rs2.getString(1);
                    con2 = dbConn.Connect(club);
                    
                    // get usernames from database for ensuring corect email setup later
                    pstmt2 = con2.prepareStatement(
                            "SELECT username FROM member2b " +
                            "WHERE email = ? OR email2 = ?");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, email[x]);
                    pstmt2.setString(2, email[x]);
                    rs = pstmt2.executeQuery();
                    
                    pstmt2 = con2.prepareStatement (
                        "UPDATE member2b " +
                        "SET " +
                            "emailOpt = IF(email=?, 0, emailOpt), " +
                            "emailOpt = IF(email2=?, 0, emailOpt), " +
                            "email = IF(email=?, '', email), " +
                            "email2 = IF(email2=?, '', email2) " + 
                            "WHERE email = ? OR email2 = ?;");

                    pstmt.clearParameters();
                    pstmt.setString(1, email[x]);
                    pstmt.setString(2, email[x]);
                    pstmt.setString(3, email[x]);
                    pstmt.setString(4, email[x]);
                    pstmt.setString(5, email[x]);
                    pstmt.setString(6, email[x]);
                    count = count + pstmt.executeUpdate();
                    
                    pstmt.close();
                    con2.close();
                    
                    while (rs.next()) {
                        ensureCorrectEmailSetup(rs.getString("username"), club, out);
                    }

                    
                }  // end of while clubs

                if (count == 0) {
                    out.println("<br>" + email[x] + " was NOT found.");
                } else if (count > 1) {
                    out.println("<br>" + email[x] + " was removed from " + count + " member records.");
                } else {
                    out.println("<br>" + email[x] + " was removed.");
                }
                
                count = 0; // reset
                
                stmt2.close();
                rs2 = null;
                
            } catch (Exception exc) {

                out.println(SystemUtils.HeadTitle("Database Error"));
                out.println("<BODY><CENTER><BR>");
                out.println("<BR><BR><H3>Database Access Error</H3>");
                out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                out.println("<BR>Error:" + exc.getMessage());
                out.println("<BR><BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact customer support.");
                out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
                out.println("</CENTER></BODY></HTML>");
            }

        } // end empty email check
        
    } // end for loop

    //con.close();                              // close the connection to the system db


    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
    out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
    out.println("<input type=\"submit\" value=\"Search Again\" style=\"text-decoration:underline; background:#CC9966\">");
    out.println("</form>");

    out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
    out.println("<input type=\"submit\" value=\"Support Menu\" style=\"text-decoration:underline; background:#CC9966\">");
    out.println("</form>");               // return to searchmain.htm

    out.println("</body></html>");
     
 }
 
 
 // *********************************************************
 // Call is to change an email address
 // *********************************************************

 private void changeAddr(HttpServletRequest req, PrintWriter out) {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;

    String newEmail = (req.getParameter("newEmail") != null) ? req.getParameter("newEmail") : null;
    String newEmail2 = (req.getParameter("newEmail2") != null) ? req.getParameter("newEmail2") : null;

    //
    //  Get the parms
    //
    String email = req.getParameter("remove");    
    String club = req.getParameter("club");
    String user = req.getParameter("username");

    String muser = "";
    String mpw = "";
    String mlast = "";
    String mfirst = "";
    String mmi = "";
    String memail = "";
    String memail2 = "";
    String fname = "";


    try {
        con = dbConn.Connect(club);               // get a connection
    }
    catch (Exception e1) {

        return;
    }

   
    // see if we need to do update now or display form for getting new email address
    if (newEmail == null && newEmail2 == null) {

        //
        //   build the HTML page for the display
        //
        out.println(SystemUtils.HeadTitle("Support Email Update Page"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<table border=\"0\" align=\"center\">");
        out.println("<tr><td align=\"center\" valign=\"top\">");

        out.println("<img src=\"/" +rev+ "/images/foretees.gif\">");
        out.println("<hr width=\"40%\"><br>");

        out.println("<font size=\"3\">");
        out.println("<p><b>Update Member Email Address</b></p>");
        out.println("</font><font size=\"2\">");

        out.println("<table border=\"1\" bgcolor=\"#FFFFCC\" cellpadding=\"5\">");
        out.println("<tr bgcolor=\"#CC9966\"><td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>User</b></u></p>");
        out.println("</font></td>");

        out.println("<td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>Password</b></u></p>");
        out.println("</font></td>");

        out.println("<td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>Name</b></u></p>");
        out.println("</font></td>");

        out.println("<td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>Club</b></u></p>");
        out.println("</font></td>");

        out.println("<td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>Email 1</b></u></p>");
        out.println("</font></td>");

        out.println("<td>");
        out.println("<font size=\"2\">");
        out.println("<p align=\"center\"><u><b>Email 2</b></u></p>");
        out.println("</font></td>");
        out.println("</tr>");
            
       try {
           
            //
            // use the name to search table
            //
            stmt = con.prepareStatement (
               "SELECT password, name_last, name_first, name_mi, email, email2 " +
               "FROM member2b " +
               "WHERE username = ?");

            stmt.clearParameters();        // clear the parms
            stmt.setString(1, user);
            rs = stmt.executeQuery();      // execute the prepared stmt

            while (rs.next()) {

               mpw = rs.getString(1);
               mlast = rs.getString(2);
               mfirst = rs.getString(3);
               mmi = rs.getString(4);
               memail = rs.getString(5);
               memail2 = rs.getString(6);

               if (mmi.equals( "" )) {

                  fname = mfirst + " " + mlast;
               } else {

                  fname = mfirst + " " + mmi + " " + mlast;
               }

               out.println("<tr><td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + user + "</u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + mpw + "</u></p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + fname + "</p>");
                     out.println("</font></td>");

                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + club + "</p>");
                     out.println("</font></td>");

                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +user+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<td>");
                  if (!memail.equals( "" )) {
                     out.println("<input type=\"text\" name=newEmail value=\"" + memail + "\" size=32>&nbsp; ");
                  }
                  out.println("</font></td>");
                  out.println("<td>");
                  if (!memail2.equals( "" )) {
                     out.println("<input type=\"text\" name=newEmail2 value=\"" + memail2 + "\" size=32>&nbsp; ");
                  }
                  out.println("</font></td>");
                  out.println("</tr>");
                  out.println("<tr>");
                  out.println("<td colspan=\"6\" align=\"center\">");
                  out.println("<input type=\"submit\" name=\"btnChange\" value=\"Submit Changes\">");       
                  out.println("</td></form>");
                  out.println("</tr>");
                  
            }    // end of while

            out.println("</table>");
            
            out.println("<form>");
            out.println("<input type=\"button\" value=\"Back\" onclick=\"history.go(-1);\" style=\"width: 80px;text-decoration:underline; background:#CC9966\">");
            out.println("</form>"); 
            
            out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
            out.println("<input type=\"submit\" value=\"Support Menu\" style=\"text-decoration:underline; background:#CC9966\">");
            out.println("</form>"); 
         
            out.println("</body></html>");
            
            stmt.close();

            con.close();
   
       }
       catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Database Error"));
          out.println("<BODY><CENTER><BR>");
          out.println("<BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Error:" + exc.getMessage());
          out.println("<BR><BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
       }
       
   } else {
   
       //
       //  update the email address
       //
        
       try {         
           
          // get old emails for comparison
          stmt = con.prepareStatement (
               "SELECT email, email2 " +
               "FROM member2b " +
               "WHERE username = ?");

          stmt.clearParameters();        // clear the parms
          stmt.setString(1, user);
          rs = stmt.executeQuery();      // execute the prepared stmt
          
          // if submitted emails are altered from their current value, update them
          if (rs.next() && (newEmail != null && !newEmail.equalsIgnoreCase(rs.getString("email"))) 
             || (newEmail2 != null && !newEmail2.equalsIgnoreCase(rs.getString("email2")))) {   // check if at least one email changed
   
              String query = "UPDATE member2b SET ";
              
              if (newEmail != null && !newEmail.equalsIgnoreCase(rs.getString("email")))    // check if email was changed
                  query += "email = '" + newEmail + "', email_bounced = 0 ";
          
              if (newEmail2 != null && !newEmail2.equalsIgnoreCase(rs.getString("email2"))) {   // check if email2 was changed
              
                  if (newEmail != null && !newEmail.equalsIgnoreCase(rs.getString("email")))    // if both emails were changed, add a ','
                      query += ", ";
               
                  query += "email2 = '" + newEmail2 + "', email2_bounced = 0 ";
              }
              query += "WHERE username = '" + user + "'";
          
          
              stmt = con.prepareStatement(query);
              stmt.executeUpdate();
              stmt.close();

              out.println(SystemUtils.HeadTitle("Done"));
              out.println("<BODY><CENTER><BR>");
              
              // run check to ensure email is not blank with email2 populated
              ensureCorrectEmailSetup(user, club, out);

              out.println("<BR><BR><H3>Email Address Updated</H3>");
              out.println("<BR><BR>Email address for " +user+ " at club " +club+ " has been updated.<br>");
          }
          else {
               
              out.println(SystemUtils.HeadTitle("Done"));
              out.println("<BODY><CENTER><BR>");
              
              // run check to ensure email is not blank with email2 populated
              ensureCorrectEmailSetup(user, club, out);
              
              out.println("<BR><BR><H3>Email Address Updated</H3>");
              out.println("<BR><BR>No updates have been made to " + user + " at club " + club + ".<br>");
          }
         
          out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
          out.println("<input type=\"submit\" value=\"Support Menu\" style=\"text-decoration:underline; background:#CC9966\">");
          out.println("</form>"); 
         
          out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
          out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
          out.println("<input type=\"submit\" value=\"Search Again\" style=\"text-decoration:underline; background:#CC9966\">");
          out.println("</form>");               // return to searchmain.htm
          out.println("</font>");
          out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">Logout</A>");
          
          //
          //  End of HTML page
          //
          out.println("</center></font></body></html>");

       }
       catch (Exception exc) {

          out.println(SystemUtils.HeadTitle("Database Error"));
          out.println("<BODY><CENTER><BR>");
          out.println("<BR><BR><H3>Database Access Error</H3>");
          out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
          out.println("<BR>Error:" + exc.getMessage());
          out.println("<BR><BR>Please try again later.");
          out.println("<BR><BR>If problem persists, contact customer support.");
          out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
          out.println("</CENTER></BODY></HTML>");
       }
   
   } // end if update or display form
    
 }
 
 
 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the email address, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
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
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
 }
 
  // *********************************************************
  // Clear user's email_bounced status back to 0
  // ********************************************************* 
 private void clearBouncedStatus(HttpServletRequest req, PrintWriter out) {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;


    //
    //  Get the parms
    //
    String remove = req.getParameter("remove");    
    String club = req.getParameter("club");
    String user = req.getParameter("username");
    String email = req.getParameter("email");


    try {
      con = dbConn.Connect(club);               // get a connection
    }
    catch (Exception e1) {

      return;
    }

    //
    //  remove the email address
    //
    try {

      pstmt = con.prepareStatement (
          "UPDATE member2b SET " +
          ((remove.equals( "email1" )) ? "email_bounced" : "email2_bounced") + " = '0' " +
          "WHERE username = ?");

      pstmt.clearParameters();
      pstmt.setString(1, user);
      pstmt.executeUpdate();
      pstmt.close();


      out.println(SystemUtils.HeadTitle("Done"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Email Bounced Status Reset</H3>");
      out.println("<BR><BR>Email Bounced status for " +user+ " at club " +club+ " has been reset.<br>");

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form>");               // return to searchmain.htm

      out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\" name=\"f\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"text\" name=\"email\" size=\"30\" maxlength=\"60\" value=\"" + email + "\">");
      out.println("<input type=\"submit\" value=\"Search Again\" name=\"subsearch\">");
      out.println("</form>");

      out.println("</font>");
      out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">Logout</A>");

      //
      //  End of HTML page
      //
      out.println("</center></font></body></html>");

    }
    catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<br><br><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
    }
 }
  
  // *********************************************************
  // If email is empty and email2 is populated, move email2 to email.
  // Also checks for incorrectly set email_bounced and email2_bounced.
  // ********************************************************* 
  protected static void ensureCorrectEmailSetup(String user, String club, PrintWriter out) {

      Connection con = null;
      ResultSet rs = null;
      PreparedStatement pstmt = null;
      
      String email = "";
      String email2 = "";
      
      try {
          con = dbConn.Connect(club);               // get a connection
      } catch (Exception e1) {
          out.println("<br>ensureCorrectEmailSetup: Database connection failed.");
          return;
      }
      
      try {
          pstmt = con.prepareStatement(
                  "SELECT email, email2, email_bounced, email2_bounced " +
                  "FROM member2b " +
                  "WHERE username = ?");
          pstmt.clearParameters();
          pstmt.setString(1, user);
          rs = pstmt.executeQuery();
     
          try {
              if (rs.next()){

                  email = rs.getString("email");
                  email2 = rs.getString("email2");

                  if (email.equals("") && !email2.equals("")){
                      // email is empty and email2 contains an email address
                      // copy email2 to email and set email2 = ""
                      pstmt = con.prepareStatement(
                              "UPDATE member2b " +
                              "SET email = ?, email2 = '', " +
                              "email_bounced = IF(email2_bounced='1', '1', '0'), " +
                              "email2_bounced = '0' " +
                              "WHERE username = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, email2);
                      pstmt.setString(2, user);
                      pstmt.executeUpdate();
                  }
                  else if (email.equals("") && rs.getString("email_bounced").equals("1")){
                      pstmt = con.prepareStatement(
                              "UPDATE member2b " +
                              "SET email_bounced = '0' " +
                              "WHERE username = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, user);
                      pstmt.executeUpdate();
                  }
                  else if (email2.equals("") && rs.getString("email2_bounced").equals("1")){
                      pstmt = con.prepareStatement(
                              "UPDATE member2b " +
                              "SET email2_bounced = '0' " +
                              "WHERE username = ?");
                      pstmt.clearParameters();
                      pstmt.setString(1, user);
                      pstmt.executeUpdate();
                  }
              }
          } catch (Exception e3) {
              out.println("<br>ensureCorrectEmailSetup: Failed to update emails in database.");
              return;
          }
      } catch (Exception e2) {
          out.println("<br>ensureCorrectEmailSetup: Failed to access current email data from database.");
          return;
      }     
    
  }
  
  
    private void toggleEmailOpt(HttpServletRequest req, PrintWriter out) {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;


        //
        //  Get the parms
        // 
        String club = req.getParameter("club");
        String user = req.getParameter("username");
        String email = req.getParameter("email");
        
        try {
            con = dbConn.Connect(club);               // get a connection
        } catch (Exception e1) {
            out.println("<br>toggleEmailOpt: Database connection failed.");
            return;
        }
        
        try {
            
            pstmt = con.prepareStatement(
                    "UPDATE member2b " +
                    "SET emailOpt = IF(emailOpt='1', '0', '1') " +
                    "WHERE username = ?");
            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.executeUpdate();
            
            out.println(SystemUtils.HeadTitle("Done"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>Email Bounced Status Reset</H3>");
            out.println("<BR><BR>emailOpt toggled for " +user+ " at club " +club+ ".<br>");

            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
            out.println("</form>");               // return to searchmain.htm

            out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\" name=\"f\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"text\" name=\"email\" size=\"30\" maxlength=\"60\" value=\"" + email + "\">");
            out.println("<input type=\"submit\" value=\"Search Again\" name=\"subsearch\">");
            out.println("</form>");

            out.println("</font>");
            out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">Logout</A>");
            
            //
            //  End of HTML page
            //
            out.println("</center></font></body></html>");

            
        } catch (Exception e2) {
            out.println("<br>toggleEmailOpt: Failed to toggle emailOpt value in database.");
            return;
        }
        
    }

    /**
     * Toggles a member's iCal option (1 or 2)
     *
     * @param req - HttpServletRequest from form submission
     * @param out - PrintWriter object for printing result messages
     */
    private void toggleICalOpt(HttpServletRequest req, PrintWriter out) {

        Connection con = null;
        ResultSet rs = null;
        PreparedStatement pstmt = null;


        //
        //  Get the parms
        //
        String club = req.getParameter("club");
        String user = req.getParameter("username");
        String email = req.getParameter("email");

        int iCalNum = 0;

        if (req.getParameter("btnICal1Toggle") != null) {
            iCalNum = 1;
        } else {        // this tells us btnICal2Toggle != null
            iCalNum = 2;
        }


        try {
            con = dbConn.Connect(club);               // get a connection
        } catch (Exception e1) {
            out.println("<br>toggleICalOpt: Database connection failed.");
            return;
        }

        try {

            pstmt = con.prepareStatement(
                    "UPDATE member2b " +
                    "SET iCal" + iCalNum + " = IF(iCal" + iCalNum + "='1', '0', '1') " +
                    "WHERE username = ?");
            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.executeUpdate();

            out.println(SystemUtils.HeadTitle("Done"));
            out.println("<BODY><CENTER><BR>");
            out.println("<BR><BR><H3>iCal " + iCalNum + " Toggled</H3>");
            out.println("<BR><BR>iCal" + iCalNum + " toggled for " +user+ " at club " +club+ ".<br>");

            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
            out.println("</form>");               // return to searchmain.htm

            out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\" name=\"f\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"text\" name=\"email\" size=\"30\" maxlength=\"60\" value=\"" + email + "\">");
            out.println("<input type=\"submit\" value=\"Search Again\" name=\"subsearch\">");
            out.println("</form>");

            out.println("</font>");
            out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">Logout</A>");

            //
            //  End of HTML page
            //
            out.println("</center></font></body></html>");


        } catch (Exception e2) {
            out.println("<br>toggleICalOpt: Failed to toggle iCal" + iCalNum + " value in database.");
            return;
        }
    }
}