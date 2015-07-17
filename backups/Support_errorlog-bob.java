/***************************************************************************************     
 *   Support_errorlog:  This servlet will read the errorlog db table and display the entries.
 *
 *   This is also used to search for a member by their email address. (search = yes)
 *
 *   called by:  support_main2.htm
 *
 ***************************************************************************************
 */
    
import java.io.*;
import java.util.*;
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
   String msg = "";
   String bgcolor = "#F5F5DC";
   String bgcolor1 = "#F5F5DC";
   String bgcolor2 = "#CCCCAA";

   int count = 1;
  
   out.println("<HTML><HEAD><TITLE>Display Error Log</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><H2>ForeTees Error Log</H2>");
   out.println("<A HREF=\"/" +rev+ "/support_main2.htm\">Return</A>");
   out.println("<BR><BR><b>Entries older than 1 week are automatically deleted.</b>");
   out.println("<BR><BR>");

   out.println("<Table border=\"1\" cellpadding=\"4\">");

   try {

      con = dbConn.Connect(db);                  // get a connection to Vx db

      if (con != null) {                         // if we got one

         stmt2 = con.createStatement();

         rs = stmt2.executeQuery("SELECT sdate, msg FROM errorlog ORDER BY date");

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

         con.close();             // close the connection to the system db
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
   //  Check if call is to Change a Member's Email Address
   //
   if (req.getParameter("btnChange") != null) {
       if (req.getParameter("btnChange").equals("Change")) {   

          changeAddr(req, out);
          return;
       }
   }
   
   //
   //  Check if call is to Remove a Member's Email Address
   //
   if (req.getParameter("remove") != null) {   

      removeAddr(req, out);
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
   out.println("<BR><BR>");

   out.println("<Table border=\"1\" cellpadding=\"4\">");

   try {

      stmt2 = con.createStatement();              // create a statement

      rs = stmt2.executeQuery("SELECT sdate, msg FROM sessionlog ORDER BY date, sdate");

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
                  out.println("<p align=\"center\"><u><b>Email 1</b></u></p>");
                  out.println("</font></td>");

               out.println("<td>");
                  out.println("<font size=\"2\">");
                  out.println("<p align=\"center\"><u><b>Email 2</b></u></p>");
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
               "SELECT username, password, name_last, name_first, name_mi, email, email2 " +
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

                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<input type=\"hidden\" name=\"remove\" value=\"email1\">");
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + memail + " &nbsp;&nbsp;");
                     if (!memail.equals( "" )) {
                        out.println("<input type=\"submit\" name=btnRemove value=\"Remove\">");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</p></font></td></form>");

                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +muser+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<input type=\"hidden\" name=\"remove\" value=\"email2\">");
                  out.println("<td>");
                     out.println("<font size=\"2\">");
                     out.println("<p align=\"center\">" + memail2 + " &nbsp;&nbsp;");
                     if (!memail2.equals( "" )) {
                        out.println("<input type=\"submit\" name=btnRemove value=\"Remove\">&nbsp; ");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</p></font></td></form>");
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

         } else {

            if (count > 5) {

            out.println("<p align=\"center\">Count = " + count + "</p>");

            }
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

      out.println("<script>");
      out.println("<!-- ");
      out.println("function cursor(){document.f.email.focus();}");
      out.println("// -->");
      out.println("</script>");

      out.println("</head>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onLoad=cursor()>");
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
                     out.println("</input>");
                  out.println("<br><br>");
                  out.println("<input type=\"submit\" value=\"Search\" name=\"subsearch\">");
                  out.println("</p>");
                  out.println("</font>");
               out.println("</td></tr>");
            out.println("</table>");
         out.println("</td></tr></form>");
         out.println("</table></font>");
         out.println("<font size=\"2\"><br><br>");

         out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</input></form>");               // return to searchmain2.htm
         out.println("</font>");

      out.println("</center></body></html>");
   }
 }


 // *********************************************************
 // Call is to remove an email address
 // *********************************************************

 private void removeAddr(HttpServletRequest req, PrintWriter out) {

   Connection con = null;
   ResultSet rs = null;
   PreparedStatement pstmt2 = null;

   int count = 0;

                  out.println("<input type=\"hidden\" name=\"username\" value=\"muser\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"club\">");
                  out.println("<input type=\"hidden\" name=\"remove\" value=\"email2\">");
   //
   //  Get the parms
   //
   String email = req.getParameter("remove");    
   String club = req.getParameter("club");
   String user = req.getParameter("username");


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

      if (email.equals( "email1" )) {
        
         pstmt2 = con.prepareStatement (
                  "UPDATE member2b SET " +
                  "email = '' " +
                  "WHERE username = ?");

      } else {

         pstmt2 = con.prepareStatement (
                  "UPDATE member2b SET " +
                  "email2 = '' " +
                  "WHERE username = ?");
      }

      pstmt2.clearParameters();
      pstmt2.setString(1, user);

      //pstmt2.executeUpdate();

      pstmt2.close();


      out.println(SystemUtils.HeadTitle("Done"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Email Address Removed</H3>");
      out.println("<BR><BR>Email address for " +user+ " at club " +club+ " has been removed.<br>");

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</input></form>");               // return to searchmain.htm
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
 // Call is to change an email address
 // *********************************************************

 private void changeAddr(HttpServletRequest req, PrintWriter out) {

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;

    String chgEmail = (req.getParameter("doChange") != null) ? req.getParameter("doChange") : "";
    String newEmail = (req.getParameter("newEmail") != null) ? req.getParameter("newEmail") : "";

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
    if (chgEmail.equals("")) {

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
                  out.println("<input type=\"hidden\" name=\"doChange\" value=\"email1\">");
                  out.println("<td>");
                     if (!memail.equals( "" )) {
                        out.println("<input type=\"text\" name=newEmail value=\"" + memail + "\" size=32>&nbsp; ");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</font></td></form>");

                  out.println("<form action=\"/" +rev+ "/servlet/Support_errorlog\" method=\"post\">");
                  out.println("<input type=\"hidden\" name=\"username\" value=\"" +user+ "\">");
                  out.println("<input type=\"hidden\" name=\"club\" value=\"" +club+ "\">");
                  out.println("<input type=\"hidden\" name=\"doChange\" value=\"email2\">");
                  out.println("<td>");
                     if (!memail2.equals( "" )) {
                        out.println("<input type=\"text\" name=newEmail value=\"" + memail2 + "\" size=32>&nbsp; ");
                        out.println("<input type=\"submit\" name=btnChange value=\"Change\">");
                     }
                     out.println("</font></td></form>");
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

          if (chgEmail.equals( "email1" )) {

             stmt = con.prepareStatement (
                      "UPDATE member2b SET " +
                      "email = ? " +
                      "WHERE username = ?");

          } else {

             stmt = con.prepareStatement (
                      "UPDATE member2b SET " +
                      "email2 = ? " +
                      "WHERE username = ?");
          }

          stmt.clearParameters();
          stmt.setString(1, newEmail);
          stmt.setString(2, user);
          stmt.executeUpdate();
          stmt.close();


          out.println(SystemUtils.HeadTitle("Done"));
          out.println("<BODY><CENTER><BR>");
          out.println("<BR><BR><H3>Email Address Updated</H3>");
          out.println("<BR><BR>Email address for " +user+ " at club " +club+ " has been updated.<br>");

         out.println("<form method=\"get\" action=\"/" +rev+ "/support_main2.htm\">");
         out.println("<input type=\"submit\" value=\"Support Menu\" style=\"text-decoration:underline; background:#CC9966\">");
         out.println("</form>"); 
         
          out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Support_errorlog\">");
          out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
          out.println("<input type=\"submit\" value=\"Search Again\" style=\"text-decoration:underline; background:#CC9966\">");
          out.println("</input></form>");               // return to searchmain.htm
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
}
