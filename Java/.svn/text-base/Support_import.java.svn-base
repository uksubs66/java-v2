/***************************************************************************************
 *   Support_import:  This class will import files for the club and update the member roster.
 *
 *
 *   called by:  support_main3.htm
 *
 *   File Names to Import:    email.csv
 *                            bag.csv
 *                            birth.csv
 *                            hndcp.csv          For current handicaps
 *                            ghinnum.csv        For GHIN Numbers
 *                            ghindata.csv       For GHIN Member Data (for GHIN interface)
 *                            webid.csv
 *                            mode.csv           For Modes of Trans Defaults per member
 *                            weight.csv         For adding lottery weights
 *
 *
 *  !!!! NOTE: Change dbConn if running from local machine !!!!!!!!!!!
 *
 *
 *
 *   created: 7/07/2005   Bob P.
 *
 *   last updated:
 *                  04-14-10  Updated a couple portWebid error messages to include webids
 *                  03-08-10  Added middle initial filter to GHINdata import
 *                  04-14-09  Added portDemoClubs import for demo club spreadsheets
 *                  08-01-08  Changed portGHIN error reporting to include GHIN #
 *                  07-10-08  Changed portGHINdata error reporting to include GHIN #
 *                  07-01-08  portWeight - Added method to import lottery weights.
 *                  04-21-08  portGHINdata - Added case for handling lname fname no comma (lnamenoc)
 *                  02-25-08  portGHINdata - if incoming ghin# is 6 chars add a leading zero
 *                  07-14-07  Added username format to portGHINdata.
 *                  07-07-07  Added processing to check for single name field to portWebid.
 *                  03-05-07  Added 'by name' processing to portWebid.
 *                  02-06-07  Updated portWebid to check for '?' in webid and mem_id fields.
 *                            Also, changed file names from *.txt to *.csv to make it quicker to prepare.
 *                  08-09-06  Added custom for fourbridges to portWebid
 *                  08-09-06  Added new byname functionality to portWebid
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.common.Utilities;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


public class Support_import extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();


   Connection con = null;                 // init DB objects

   HttpSession session = null;

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( "support" ) && !user.startsWith( "sales" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {

      con = dbConn.Connect(club);
   }
   catch (Exception exc) {

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.startsWith( "sales" )) {
         out.println("<BR><BR> <A HREF=\"/v5/sales_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/v5/support_main3.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Process according to the type of call
   //
   if (req.getParameter("email") != null) {

      portEmail(req, out, con, club);             // import email addresses
      return;
   }
     
   if (req.getParameter("bag") != null) {

      portBag(req, out, con, club);             // import bag room numbers
      return;
   }

   if (req.getParameter("birth") != null) {

      portBirth(req, out, con, club);             // import birth dates
      return;
   }

   if (req.getParameter("hndcp") != null) {

      portHndcp(req, out, con, club);             // update handicaps
      return;
   }

   if (req.getParameter("ghin") != null) {

      portGHIN(req, out, con, club);             // update GHIN Numbers
      return;
   }

   if (req.getParameter("ghindata") != null) {

      portGHINdata(req, out, con, club);             // update GHIN Numbers
      return;
   }

   if (req.getParameter("webid") != null) {

      portWebid(req, out, con, club);             // update handicaps
      return;
   }

   if (req.getParameter("mode") != null) {

      portMode(req, out, con, club);             // update modes of trans defaults
      return;
   }

   if (req.getParameter("weight") != null) {

      portWeight(req, out, con, club);             // update modes of trans defaults
      return;
   }
   
   if (req.getParameter("democlubs") != null) {
       
      portDemoClubs(req, out, con, club);
      return;
   }

 }


 // ********************************************************************
 //  Process the 'Import Email' Request
 // ********************************************************************

 private void portEmail(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
     
   Member member = new Member();

   int count = 0;
     
   String line = "";
   String mem_id = "";
   String email1 = "";
   String email2 = "";
   String fname = "";
   String lname = "";
   String msg = "";

   //
   //  read in the text file - must be named 'email.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//email.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\email.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {
     
      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //   File Format:
      //
      //                username, email1, email2
      //                    - or -
      //                lname, fname, email1, email2
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if (byname == false) {
           
            mem_id = tok.nextToken();

            while (mem_id.startsWith( "0" )) {    // if starts with a zero

               mem_id = remZeroS(mem_id);           // remove the leading zero
            }

         } else {

            lname = tok.nextToken();
            fname = tok.nextToken();
         }

         email1 = "";
         email2 = "";
           
         if ( tok.countTokens() > 0 ) {
            email1 = tok.nextToken();
         }

         if ( tok.countTokens() > 0 ) {
            email2 = tok.nextToken();
         }

         //
         //  Verify the email addresses
         //
         if (!email1.equals( "" )) {

            FeedBack feedback = (member.isEmailValid(email1));   // verify the address

            if (!feedback.isPositive()) {              // if error

               email1 = "";                             // do not use
            }
         }
         if (!email2.equals( "" )) {

            FeedBack feedback = (member.isEmailValid(email2));   // verify the address

            if (!feedback.isPositive()) {              // if error

               email2 = "";                             // do not use
            }
         }

         if (!email1.equals( "" )) {

            //
            // update member record
            //
            if (byname == false) {

               if (!email2.equals( "" )) {

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "email = ?, email2 = ? " +
                           "WHERE username = ?");

                  pstmt2.clearParameters();
                  pstmt2.setString(1, email1);
                  pstmt2.setString(2, email2);
                  pstmt2.setString(3, mem_id);
              
               } else {

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "email = ? " +
                           "WHERE username = ?");

                  pstmt2.clearParameters();
                  pstmt2.setString(1, email1);
                  pstmt2.setString(2, mem_id);
               }

            } else {
  
               if (!email2.equals( "" )) {

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "email = ?, email2 = ? " +
                           "WHERE name_last = ? AND name_first = ?");

                  pstmt2.clearParameters();
                  pstmt2.setString(1, email1);
                  pstmt2.setString(2, email2);
                  pstmt2.setString(3, lname);
                  pstmt2.setString(4, fname);

               } else {

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "email = ? " +
                           "WHERE name_last = ? AND name_first = ?");

                  pstmt2.clearParameters();
                  pstmt2.setString(1, email1);
                  pstmt2.setString(2, lname);
                  pstmt2.setString(3, fname);
               }
            }

            count = pstmt2.executeUpdate();   

            if (count == 0) {    // if not updated - record it
             
               if (byname == false) {

                  msg = "Member Id not found: " +mem_id+ ", email = " +email1;
               } else {
                  msg = "Member Name not found: " +lname+ ", " +fname+ ", email = " +email1;
               }

               logError(msg, club);                           // log it
            }
           
            pstmt2.close();           
         }

      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import Bag' Request
 // ********************************************************************

 private void portBag(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;

   String line = "";
   String mem_id = "";
   String bag = "";
   String fname = "";
   String lname = "";
   String msg = "";

   //
   //  read in the text file - must be named 'bag.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//bag.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\bag.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //**********************************************************************
      //   The text file contains the info to import - get each line
      //
      //   Format:    username, bag#
      //
      //      -OR-    lname, fname, bag#
      //
      //      -OR-    fname lname, bag#
      //
      //**********************************************************************
      
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         bag = "";

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if (byname == false) {

            mem_id = tok.nextToken();
            bag = tok.nextToken();

         } else {

            lname = tok.nextToken();          // get last name OR entire name

            if ( tok.countTokens() == 1 ) {    // if fname and bag# do not both exist, then try to split name

               bag = tok.nextToken();

               tok = new StringTokenizer( lname, " " );     // split fullname (fname lname)

               if ( tok.countTokens() > 1 ) {

                  fname = tok.nextToken();       // get fname
                  lname = tok.nextToken();       // get lname
               }

            } else {
              
               if ( tok.countTokens() > 1 ) {    

                  fname = tok.nextToken();       // get fname (already have lname)
                  bag = tok.nextToken();         // and bag#
               }
            }
         }

         if (!bag.equals( "" )) {

            //
            // update member record
            //
            if (byname == false) {

               pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET " +
                        "bag = ? " +
                        "WHERE username = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, bag);
               pstmt2.setString(2, mem_id);
                 
            } else {
  
               pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET " +
                        "bag = ? " +
                        "WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, bag);
               pstmt2.setString(2, lname);
               pstmt2.setString(3, fname);
            }
  
            count = pstmt2.executeUpdate();

            if (count == 0) {    // if not updated - record it

               if (byname == false) {

                  msg = "Member Id not found: " +mem_id+ ", bag = " +bag;
               } else {
                  msg = "Member Name not found: " +lname+ ", " +fname+ ", bag = " +bag;
               }

               logError(msg, club);                           // log it
            }

            pstmt2.close();
         }

      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import Birth' Request (mm/dd/yyyy)
 // ********************************************************************

 private void portBirth(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int birthi = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;

   String line = "";
   String mem_id = "";
   String birth = "";
   String temp = "";
   String fname = "";
   String lname = "";
   String msg = "";

   //
   //  read in the text file - must be named 'birth.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//birth.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\birth.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if (byname == false) {

            mem_id = tok.nextToken();

         } else {

            lname = tok.nextToken();
            fname = tok.nextToken();
         }

         birth = "";

         if ( tok.countTokens() > 0 ) {
            birth = tok.nextToken();
         }

         if (!birth.equals( "" )) {

            tok = new StringTokenizer( birth, "/" );     // delimiters are /

            if ( tok.countTokens() > 2 ) {               // birth date valid

               temp = tok.nextToken();
               mm = Integer.parseInt(temp);               // get month

               temp = tok.nextToken();
               dd = Integer.parseInt(temp);               // get day

               temp = tok.nextToken();
               yy = Integer.parseInt(temp);               // get year

               birthi = (yy * 10000) + (mm * 100) + dd;           // create date yyyymmdd
            }
              
            //
            // update member record
            //
            if (byname == false) {

               pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET " +
                        "birth = ? " +
                        "WHERE username = ?");

               pstmt2.clearParameters();
               pstmt2.setInt(1, birthi);
               pstmt2.setString(2, mem_id);
              
            } else {
  
               pstmt2 = con.prepareStatement (
                        "UPDATE member2b SET " +
                        "birth = ? " +
                        "WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setInt(1, birthi);
               pstmt2.setString(2, lname);
               pstmt2.setString(3, fname);
            }

            count = pstmt2.executeUpdate();

            if (count == 0) {    // if not updated - record it

               if (byname == false) {

                  msg = "Member Id not found: " +mem_id+ ", birth = " +birth;
               } else {
                  msg = "Member Name not found: " +lname+ ", " +fname+ ", birth = " +birth;
               }

               logError(msg, club);                           // log it
            }

            pstmt2.close();
         }

      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import Hndcp' Request (Course Hndcp, USAGA Hndcp Index)
 // ********************************************************************

 private void portHndcp(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;

   float u_hcap = 0;           // usga hndcp
   float c_hcap = 0;           // course hndcp

   String line = "";
   String mem_id = "";
   String temp = "";
   String ghin = "";
   String fname = "";
   String lname = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String name1 = "";
   String name2 = "";
   String msg = "";

   //
   //  Get type of request (by ghin# or by username)
   //
   String rtype = req.getParameter("hndcp");

   //
   //  read in the text file - must be named 'hndcp.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//hndcp.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\hndcp.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //  File Format:   username, course hndcp, hndcp index, ghin (optional)
      //
      //       -OR-      ghin#, course hndcp, hndcp index, name1 (optional), name2 (optional)  (name used for error message only)
      //
      //       -OR-      lname, fname, course hndcp, hndcp index, ghin (optional)
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         //
         //  Process by type of call
         //
         if (rtype.equals( "name" )) {            // if request is By name

            lname = tok.nextToken();              // get name 
            fname = tok.nextToken();                  

         } else {
           
            mem_id = tok.nextToken();             // get username or ghin#
         }
           
         c_hndcp = "";

         if ( tok.countTokens() > 0 ) {
            c_hndcp = tok.nextToken();            // course hndcp
         }
           
         u_hndcp = "";

         if ( tok.countTokens() > 0 ) {
            u_hndcp = tok.nextToken();            // hndcp index (usga)
         } else {
            u_hndcp = c_hndcp;
         }

         ghin = "";
         name1 = "";
         name2 = "";
           
         if (rtype.equals( "ghin" )) {            // if request is By ghin # look for optional names

            if ( tok.countTokens() > 0 ) {
               name1 = tok.nextToken();            // name1
            }
            if ( tok.countTokens() > 0 ) {
               name2 = tok.nextToken();            // name2
            }

         } else {

            if ( tok.countTokens() > 0 ) {
               ghin = tok.nextToken();            // ghin #
            }
         }

         if (u_hndcp.equals( "?" )) {         // if USGA Index not provided

            u_hndcp = c_hndcp;
         }

         //
         //  Convert the hndcp's from string to int
         //
         if ((u_hndcp.equalsIgnoreCase("NH")) || (u_hndcp.equalsIgnoreCase("NHL")) ||
             (u_hndcp.equalsIgnoreCase("NI")) || (u_hndcp.equalsIgnoreCase("N/I"))) {

            u_hcap = -99;                    // indicate no hndcp

         } else {

            u_hndcp = u_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
            u_hndcp = u_hndcp.replace('H', ' ');    //         or 'H' if present
            u_hndcp = u_hndcp.replace('N', ' ');    //         or 'N' if present
            u_hndcp = u_hndcp.replace('J', ' ');    //         or 'J' if present
            u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
            u_hndcp = u_hndcp.trim();

            u_hcap = Float.parseFloat(u_hndcp);                   // usga handicap

            if ((!u_hndcp.startsWith("+")) && (!u_hndcp.startsWith("-"))) {

               u_hcap = 0 - u_hcap;                       // make it a negative hndcp (normal)
            }
         }

         if ((c_hndcp.equalsIgnoreCase("NH")) || (c_hndcp.equalsIgnoreCase("NHL")) ||
             (c_hndcp.equalsIgnoreCase("NI")) || (c_hndcp.equalsIgnoreCase("N/I"))) {

            c_hcap = -99;                    // indicate no c_hndcp

         } else {

            c_hndcp = c_hndcp.replace('L', ' ');    // isolate the handicap - remove spaces and trailing 'L'
            c_hndcp = c_hndcp.replace('H', ' ');    //         or 'H' if present
            c_hndcp = c_hndcp.replace('N', ' ');    //         or 'N' if present
            c_hndcp = c_hndcp.replace('J', ' ');    //         or 'J' if present
            u_hndcp = u_hndcp.replace('R', ' ');    //         or 'R' if present
            c_hndcp = c_hndcp.trim();

            c_hcap = Float.parseFloat(c_hndcp);                   // course handicap

            if ((!c_hndcp.startsWith("+")) && (!c_hndcp.startsWith("-"))) {

               c_hcap = 0 - c_hcap;                       // make it a negative hndcp (normal)
            }
         }

         if (!c_hndcp.equals( "" )) {        // if handicap provided for this member

            //
            // update member record
            //
            if (rtype.equals( "name" )) {            // if request is By name
              
               if (!ghin.equals( "" )) {             // if ghin# provided

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "c_hancap = ?, g_hancap = ?, ghin = ? " +
                           "WHERE name_last = ? AND name_first = ?");

                  pstmt2.clearParameters();
                  pstmt2.setFloat(1, c_hcap);
                  pstmt2.setFloat(2, u_hcap);
                  pstmt2.setString(3, ghin);
                  pstmt2.setString(4, lname);
                  pstmt2.setString(5, fname);
                 
               } else {

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "c_hancap = ?, g_hancap = ? " +
                           "WHERE name_last = ? AND name_first = ?");

                  pstmt2.clearParameters();
                  pstmt2.setFloat(1, c_hcap);
                  pstmt2.setFloat(2, u_hcap);
                  pstmt2.setString(3, lname);
                  pstmt2.setString(4, fname);
               }

            } else {

               if (rtype.equals( "ghin" )) {            // if request is By GHIN #

                  pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET " +
                           "c_hancap = ?, g_hancap = ? " +
                           "WHERE ghin = ?");

                  pstmt2.clearParameters();
                  pstmt2.setFloat(1, c_hcap);
                  pstmt2.setFloat(2, u_hcap);
                  pstmt2.setString(3, mem_id);

               } else {

                  if (!ghin.equals( "" )) {             // if ghin# provided

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "c_hancap = ?, g_hancap = ?, ghin = ? " +
                              "WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setFloat(1, c_hcap);
                     pstmt2.setFloat(2, u_hcap);
                     pstmt2.setString(3, ghin);
                     pstmt2.setString(4, mem_id);
                                
                  } else {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "c_hancap = ?, g_hancap = ? " +
                              "WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setFloat(1, c_hcap);
                     pstmt2.setFloat(2, u_hcap);
                     pstmt2.setString(3, mem_id);
                  }
               }
            }

            count = pstmt2.executeUpdate();

            pstmt2.close();

            if (count == 0) {    // if not updated - record it

               if (rtype.equals( "name" )) {            // if request is By name
                  msg = "Member Name not found: " +lname+ ", " +fname;
               } else {
                  if (rtype.equals( "ghin" )) {            // if request is By ghin#
                     msg = "Member GHIN# not found: " +mem_id+ ", Name = " +name1+ " " +name2;
                  } else {
                     msg = "Member Username not found: " +mem_id;
                  }
               }

               logError(msg, club);                           // log it
            }
         }

      }   // end of while

   }
   catch (Exception e3) {

      if (rtype.equals( "name" )) {            // if request is By name
         msg = "SQL Exception: " +lname+ ", " +fname+ ", " +c_hcap+ ", " +u_hcap;
      } else {
         msg = "SQL Exception: " +mem_id+ ", " +c_hcap+ ", " +u_hcap;
      }
      logError(msg, club);                           // log it

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import GHIN Numbers' Request
 // ********************************************************************

 private void portGHIN(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;

   float u_hcap = 0;           // usga hndcp
   float c_hcap = 0;           // course hndcp

   String line = "";
   String mem_id = "";
   String temp = "";
   String ghin = "";
   String fname = "";
   String lname = "";
   String name1 = "";
   String name2 = "";
   String mi = "";
   String msg = "";

   //
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("ghin");

   //
   //  read in the text file - must be named 'ghinnum.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//ghinnum.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\ghinnum.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //  File Format:   ghin #, username, lname, fname (names optional)
      //
      //       -OR-      ghin #, lname, fname, mi (optional)
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         lname = "";
         fname = "";
         mi = "";
           
         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {              // must be at least 2

            ghin = tok.nextToken();                  // ghin #
              
            //
            //  Process by type of call
            //
            if (rtype.equals( "name" )) {            // if request is By name

               lname = tok.nextToken();              // get name
               fname = tok.nextToken();

               if ( tok.countTokens() > 0 ) {
                  mi = tok.nextToken();              // mi
               }

            } else {

               mem_id = tok.nextToken();             // get username or ghin#
                 
               if ( tok.countTokens() > 0 ) {
                  lname = tok.nextToken();              // lname
               }
               if ( tok.countTokens() > 0 ) {
                  fname = tok.nextToken();              // fname
               }
            }


            if (!ghin.equals( "" )) {            // if handicap # provided for this member

               //
               // update member record
               //
               if (rtype.equals( "name" )) {            // if request is By name

                  if (!mi.equals( "" )) {
                    
                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);
                     pstmt2.setString(4, mi);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  } else {
                    
                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE name_last = ? AND name_first = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();
                  }

               } else {

                  if (!mem_id.equals( "" )) {         

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, mem_id);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  }
               }

               if (count == 0) {    // if not updated - record it

                  if (rtype.equals( "name" )) {            // if request is By name
                     msg = "Member Name not found: " +lname+ ", " +fname+ ", Handicap Num = " +ghin;
                  } else {
                     msg = "Member Username not found: " +mem_id+ ", Handicap Num = " +ghin;
                  }

                  logError(msg, club);                           // log it
               }
            }
         }

      }   // end of while

   }
   catch (Exception e3) {

      if (rtype.equals( "name" )) {            // if request is By name
         msg = "SQL Exception: " +lname+ ", " +fname+ ", " +ghin;
      } else {
         msg = "SQL Exception: " +mem_id+ ", " +ghin;
      }
      logError(msg, club);                           // log it

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import GHIN Data' Request (for GHIN Interface)
 // ********************************************************************

 private void portGHINdata(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int found = 0;
   int notFound = 0;
   int dupFound = 0;
   int assoc = 0;
   int clubNum = 0;

   String line = "";
   String mem_id = "";
   String temp = "";
   String ghin = "";
   String name = "";
   String fname = "";
   String lname = "";
   String assocs = "";
   String clubNums = "";
   String name1 = "";
   String name2 = "";
   String msg = "";

   //
   //  read in the text file - must be named 'ghindata.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;
   StringTokenizer tok2 = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//ghindata.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\ghindata.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("rtype");

  
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //
      //   File Format:    name (fname lname), ghin#, Association#, Club#
      //
      //                   name (lname fname), ghin#, Association#, Club#
      //
      //                   name (lname, fname), ghin#, Association#, Club#
      //
      //                   username, ghin#, Association#, Club#
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {       // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );        // delimiters are comma

         //
         //  Process the name
         //
         name = "";
         fname = "";
         lname = "";
         mem_id = "";

         if (rtype.equalsIgnoreCase( "user" )) {          // if username provided
           
            mem_id = tok.nextToken();                     // get username
            
            mem_id = mem_id.trim();
              
            lname = mem_id;                               // save for messages
            name = mem_id;
            
         } else {
           
            if (rtype.equalsIgnoreCase( "fname" )) {

               name = tok.nextToken();               // get name

               tok2 = new StringTokenizer( name, " " );    // delimiters are spaces

               if ( tok2.countTokens() > 1 ) {

                  fname = tok2.nextToken();          // get fname
                  lname = tok2.nextToken();          // get lname

                  lname = lname.replace("\"", "");
                  fname = fname.replace("\"", "");

                  lname = lname.trim();
                  fname = fname.trim();

               }

            } 
            else if(rtype.equalsIgnoreCase( "lnamenoc" )){
                
               name = tok.nextToken();              // get name
               
               tok2 = new StringTokenizer( name, " " );     // delimiters are spaces
               
               if ( tok2.countTokens() > 1 ) {
                   
                   lname = tok2.nextToken();        // get lname
                   fname = tok2.nextToken();       // get fname
                   
                   lname = lname.replace("\"", "");
                   fname = fname.replace("\"", "");
                   
                   lname = lname.trim();
                   fname = fname.trim();
                   
               }
                       
            }else {  // must be lname

               lname = tok.nextToken();              // get lname
               fname = tok.nextToken();              // get fname

               lname = lname.replace("\"", "");
               fname = fname.replace("\"", "");

               StringTokenizer tok3 = new StringTokenizer(fname, " ");      // If fname has a space in it, mi is present.  Remove it.
               
               if (tok3.countTokens() > 1) {
                   fname = tok3.nextToken();
               }

               lname = lname.trim();
               fname = fname.trim();

               name = fname+ " " +lname;             // combine
            }
         }

         ghin = "";
         assocs = "";
         clubNums = "";
         assoc = 0;
         clubNum = 0;

         if ( tok.countTokens() > 0 ) {
           
            ghin = tok.nextToken();               // GHIN number
              
            if (ghin.equals ( "?" )) {
              
               ghin = "";
            } else {

                while (ghin.length() < 7) {
                    ghin = "0" + ghin;
                }
            }
         }
         
        
         // used when setting gender as well
         String gender = "";
         if ( tok.countTokens() > 0 ) {
             
             gender = tok.nextToken();
             
         }
         if (!gender.equals( "" )) {
             
             if (gender.equalsIgnoreCase("male")) {
                 
                 gender = "M";
             }
             else {
                 
                 gender = "F";
             }
         }
         // end of gender setting section
         
         if ( tok.countTokens() > 0 ) {
           
            clubNums = tok.nextToken();           // club number

            if (clubNums.equals ( "?" )) {

               clubNums = "";
            }
         }
         if ( tok.countTokens() > 0 ) {
           
            assocs = tok.nextToken();             // association number

            if (assocs.equals ( "?" )) {

               assocs = "";
            }
         }
         
/*         
         // used when under 'user' runtype and want to grab first and last names as well
         if ( tok.countTokens() > 0 ) {

            fname = tok.nextToken();           // fname

            if (fname.equals ( "?" )) {

               fname = "";
            }
         }
         if ( tok.countTokens() > 0 ) {

            lname = tok.nextToken();           // lname

            if (lname.equals ( "?" )) {

               lname = "";
            }
         }
           
*/         
         if (!assocs.equals( "" )) {              // if assoc# provided

            assoc = Integer.parseInt(assocs);     
         }

         if (!clubNums.equals( "" )) {             // if club# provided

            clubNum = Integer.parseInt(clubNums);
         }

         if (!ghin.equals( "" )) {             // if ghin# provided
           
            if (rtype.equalsIgnoreCase( "user" )) {          // if username provided

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET " +
                           "ghin = ?, " +
                           "hdcp_club_num_id = IFNULL(" +
                               "(SELECT hdcp_club_num_id FROM hdcp_club_num WHERE CAST(club_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "hdcp_assoc_num_id = IFNULL(" +
                               "(SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE CAST(assoc_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0) " +
                       "WHERE username = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, mem_id);

            } else {

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET " +
                           "ghin = ?, " +
                           "hdcp_club_num_id = IFNULL(" +
                               "(SELECT hdcp_club_num_id FROM hdcp_club_num WHERE CAST(club_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "hdcp_assoc_num_id = IFNULL(" +
                               "(SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE CAST(assoc_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0) " +
                       "WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, lname);
               pstmt2.setString(5, fname);
            
/*
               // used when setting gender as well
               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET " +
                           "ghin = ?, " +
                           "hdcp_club_num_id = IFNULL(" +
                               "(SELECT hdcp_club_num_id FROM hdcp_club_num WHERE CAST(club_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "hdcp_assoc_num_id = IFNULL(" +
                               "(SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE CAST(assoc_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "gender = ? " +
                       "WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, gender);
               pstmt2.setString(5, lname);
               pstmt2.setString(6, fname);
               // end of gender setting section
*/               
               // if ccrockies then some of their first names in member2b have an * preceeding the first name
               // so if the update doesn't do anything then try it again with an * after the fname
               if (club.equals("ccrockies")) {
                   
                   count = pstmt2.executeUpdate();
                   
                   if (count == 0) {
                       pstmt2 = con.prepareStatement (
                               "UPDATE member2b SET " +
                                   "ghin = ?, " +
                                   "hdcp_club_num_id = IFNULL(" +
                                       "(SELECT hdcp_club_num_id FROM hdcp_club_num WHERE CAST(club_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                                   "hdcp_assoc_num_id = IFNULL(" +
                                       "(SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE CAST(assoc_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0) " +
                               "WHERE name_last = ? AND name_first = ?");

                       pstmt2.clearParameters();
                       pstmt2.setString(1, ghin);
                       pstmt2.setInt(2, clubNum);
                       pstmt2.setInt(3, assoc);
                       pstmt2.setString(4, lname);
                       pstmt2.setString(5, fname+"*");
                   }
                   
               }
            }

            count = pstmt2.executeUpdate();

            pstmt2.close();

            if (count == 0) {    // if not updated - record it

               msg = "Member Name not found: " +lname+", " + fname + "  GHIN: " + ghin;
               logError(msg, club);                           // log it
               notFound++; 
                 
            } 
            else if (count >= 2){
             
               msg = "Multiple entries updated: name was " + lname + ", " + fname + "  GHIN: " + ghin;             
               logError(msg, club);
               dupFound++;
               
            }                 
            else { 

               found++;
            }
         } // end if ghin# provided

      }   // end of while

   }
   catch (Exception e3) {

      msg = "SQL Exception: " +name;
      logError(msg, club);                           // log it

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR>Members Found = " +found+ ", Members Not Found = " +notFound);
   out.println("<BR><BR>Duplicate Members Found = " + dupFound);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }



 // ********************************************************************
 //  Process the 'Import Webid' Request (Web Site Id for Interface)
 // ********************************************************************

 private void portWebid(HttpServletRequest req, PrintWriter out, Connection con, String club) {


    Statement stmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    int count = 0;
    int t = 0;
    int tmp = 0; // holds # of members found w/ queried name
    int updated = 0;
    int err_nf = 0; // not found
    int err_mf = 0; // multiple found
    int err_nu = 0; // not updated

    String line = "";
    String mem_id = "";
    String webid = "";
    String lname = "";
    String fname = "";
    String mNum = "";
    String msg = "";
    String skip = "";
    String here = "";
    String sql = "";
    String[] col;

    boolean byname = (req.getParameter("byname") != null) ? true : false;
    boolean found = false;

    //
    //  read in the text file 
    //
    boolean failed = false;
    FileReader fr = null;

    StringTokenizer tok = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//webid.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\webid.csv");

        }
        catch (Exception e2) {

            out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
            out.println("<BR><BR>File Read Failed for  " + club);
            out.println("<BR><BR>Exception Received: "+ e2.getMessage());
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
            out.println("</CENTER></BODY></HTML>");
        return;
        }
    }

    //
    //
    try {

        BufferedReader bfrin = new BufferedReader(fr);
        line = new String();

        //********************************************************************
        //   The text file contains the info to import - get each line
        //
        //  File Format:   webid, username
        //
        //       -OR-      webid, username, lname (optional), fname (optional)  (name used for error message only)
        //
        //       -OR-      webid, lname, fname, mNum (optional)  - byname option
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text
            
            //  parse the line to gather all the info
            t++;
            webid = "";
            mem_id = "";
            lname = "";
            skip = "";
            fname = "";

            if (byname == true) {           // process by name ?

                tok = new StringTokenizer( line, "," );     // delimiters are comma
                webid = tok.nextToken();              // get webid

                lname = "";
                fname = "";
                mNum = "";

                if ( tok.countTokens() > 0 ) {
                  
                   lname = tok.nextToken();            // lname
                }
                      
                if ( tok.countTokens() > 0 ) {         // if more
                  
                   fname = tok.nextToken();            // fname
                   
                   if ( tok.countTokens() > 0 ) {
                       mNum = tok.nextToken();
                   }

                } else {                                // only 1 name field - parse it
                  
                   tok = new StringTokenizer( lname, " " );     // get fname only (drop any mi)

                   if ( tok.countTokens() > 2 ) {           // first mi last ?
                     
                      fname = tok.nextToken();            // fname
                      lname = tok.nextToken();            // skip mi
                      lname = tok.nextToken();            // lname
                        
                   } else {

                      if ( tok.countTokens() > 1 ) {           // first last ?

                         fname = tok.nextToken();            // fname
                         lname = tok.nextToken();            // lname
                      }
                   }
                }

                tok = new StringTokenizer( fname, " " );     // get fname only (drop any mi)

                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();                // fname only
                }

                fname = fname.trim();
                lname = lname.trim();
                webid = webid.trim();
                  
            } else {
             
                tok = new StringTokenizer( line, "," );     // delimiters are comma
                webid = tok.nextToken();              // get webid
                mem_id = tok.nextToken();             // get username

                lname = "";
                fname = "";

                if ( tok.countTokens() > 0 ) {
                    lname = tok.nextToken();            // name1
                }
                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();            // name2
                }

            } // end if club ==

            here = "2";
            
            if (webid.equals( "?" )) {

               webid = "";
            }

            if (mem_id.equals( "?" )) {

               mem_id = "";
            }


            // if we retrieved a webid from the import file
            // then lets attempt to locate and update a record
            //
            if (!webid.equals( "" )) { 
                
                //
                //  Make sure value is alphanumeric (remove space char w/parity (A0 hex) from jonas strings !!!!!!!!!!!!!!!
                //
                webid = stripSP( webid );        // remove last digit if special char

                //
                // update member record
                //
                if (byname) {

                    //
                    // fist locate member record
                    //
                    tmp = 0;
                    if (!mNum.equals( "" )) {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ? AND memNum = ?";
                    } else {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ?";
                    }
                    pstmt2 = con.prepareStatement (sql);
                    pstmt2.clearParameters();
                    pstmt2.setString(1, lname);
                    pstmt2.setString(2, fname);
                    if (!mNum.equals( "" )) {
                       pstmt2.setString(3, mNum);
                    }
                    rs = pstmt2.executeQuery();
                    
                    if (rs.next()) tmp = rs.getInt(1);
                    
                    pstmt2.close();

                    if (tmp == 0) {
                        
                        logError("Member Not Found: name was " +lname+ ", " +fname+ " (" +webid+ ")", club);
                        err_nf++;
                    } else {
                        
                        if (tmp == 1) {
                            
                            if (!mNum.equals( "" )) {
                               sql = "UPDATE member2b SET webid = ? WHERE name_last = ? AND name_first = ? AND memNum = ?";
                            } else {
                               sql = "UPDATE member2b SET webid = ? WHERE name_last = ? AND name_first = ?";
                            }
                            pstmt2 = con.prepareStatement (sql);
                            pstmt2.clearParameters();
                            pstmt2.setString(1, webid);
                            pstmt2.setString(2, lname);
                            pstmt2.setString(3, fname);
                            if (!mNum.equals( "" )) {
                               pstmt2.setString(4, mNum);
                            }
                            count = pstmt2.executeUpdate();
                            if (count == 0) {
                                err_nu++;
                            } else {
                                updated++;
                            }

                            pstmt2.close();
                            
                        } else {
                            
                            logError("Multiple Members ("+tmp+") Found with name: " +lname+ ", " +fname+ " (" +webid+ ")", club);
                            err_mf++;
                        }
                    }
                    
                } else {          // by username

                   count = 0;
                     
                   if (!mem_id.equals( "" )) { 

                       sql = "UPDATE member2b SET webid = ? WHERE username = ?";
                       pstmt2 = con.prepareStatement (sql);
                       pstmt2.clearParameters();
                       pstmt2.setString(1, webid);
                       pstmt2.setString(2, mem_id);
                       count = pstmt2.executeUpdate();
                         
                       pstmt2.close();
                    }

                    if (count == 0) {    // if not updated - record it (maybe it didn't update because it was already set - not with this api)

                       msg = "Member Not Found: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + webid + ")";
                       logError(msg, club);                           // log it
                       err_nf++;
                          
                    } else {
                       updated++;
                    }
                }

            } else {     // no webid

               msg = "Webid Not Specified: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + webid + ")";
               logError(msg, club);                           // log it
               err_nf++;

            } // end if try to update

        }   // end of while
        
        logError("Total: "+t, club);
        if (err_nf != 0) logError("Total Not Found: "+err_nf, club);
        if (err_mf != 0) logError("Total Multiples Found: "+err_mf, club);
        //logError("Total Not Updated: "+err_nu, club); // doesn't work with this api to myqsl
        logError("Total Updated: "+updated, club);

    } catch (Exception e3) {

        msg = "SQL Exception: " +mem_id+ ", " +lname+ ", " +fname+ " (" + webid + ")";
        logError(msg, club);                           // log it

        out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
        out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
        out.println("<BR><BR>DB Import Failed for  " + club);
        out.println("<BR><BR>Exception Received: "+ e3.getMessage());
        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
    out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
    out.println("<BR><BR>The Web Id Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 } 


 // ********************************************************************
 //  Process the 'Import Modes' Request (Modes of Trans Defaults)
 // ********************************************************************

 private void portMode(HttpServletRequest req, PrintWriter out, Connection con, String club) {


    Statement stmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    int count = 0;
    int t = 0;
    int tmp = 0;     // holds # of members found w/ queried name
    int updated = 0;
    int err_nf = 0; // not found
    int err_mf = 0; // multiple found
    int err_nu = 0; // not updated

    String line = "";
    String mem_id = "";
    String mode = "";
    String name1 = "";
    String name2 = "";
    String mNum = "";
    String msg = "";
    String skip = "";
    String here = "";
    String sql = "";
    String[] col;

    boolean byname = (req.getParameter("byname") != null) ? true : false;
    boolean found = false;

    //
    //  read in the text file - must be named 'hndcp.csv'
    //
    boolean failed = false;
    FileReader fr = null;

    StringTokenizer tok = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//mode.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\mode.csv");

        }
        catch (Exception e2) {

            out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
            out.println("<BR><BR>File Read Failed for  " + club);
            out.println("<BR><BR>Exception Received: "+ e2.getMessage());
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
            out.println("</CENTER></BODY></HTML>");
        return;
        }
    }

    //
    //
    try {

        BufferedReader bfrin = new BufferedReader(fr);
        line = new String();

        //********************************************************************
        //   The text file contains the info to import - get each line
        //
        //  File Format:   
        //                 mNum, mode, name1 (optional), name2 (optional)  (name used for error message only)
        //
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text

           //  parse the line to gather all the info
           t++;
           mode = "";
           mem_id = "";
           name1 = "";
           skip = "";
           name2 = "";

            tok = new StringTokenizer( line, "," );     // delimiters are comma
            mNum = tok.nextToken();                     // get mNum (could change to username)
            mode = tok.nextToken();                     // get mode

            name1 = "";
            name2 = "";

            if ( tok.countTokens() > 0 ) {
                name1 = tok.nextToken();            // name1
            }
            if ( tok.countTokens() > 0 ) {
                name2 = tok.nextToken();            // name2
            }

            here = "2";

            if (mode.equals( "?" )) {

               mode = "";
            }

            if (mNum.equals( "?" )) {

               mNum = "";
            }


            // if we retrieved a mode from the import file
            // then lets attempt to locate and update a record
            //
            if (!mode.equals( "" )) {

                //
                // update member record
                //
                count = 0;

                if (!mNum.equals( "" )) {

                    sql = "UPDATE member2b SET wc = ? WHERE memNum = ?";
                    pstmt2 = con.prepareStatement (sql);
                    pstmt2.clearParameters();
                    pstmt2.setString(1, mode);
                    pstmt2.setString(2, mNum);
                    count = pstmt2.executeUpdate();

                    pstmt2.close();
                 }

                 if (count == 0) {    // if not updated - record it (maybe it didn't update because it was already set - not with this api)

                    msg = "Member Not Found: mNum was " +mNum+ ", name was " +name2+ ", " +name1;
                    logError(msg, club);                           // log it
                    err_nf++;

                 } else {
                    updated++;
                 }

            } else {     // no mode

               msg = "Mode Not Specified: mNum was " +mNum+ ", name was " +name2+ ", " +name1;
               logError(msg, club);                           // log it
               err_nf++;

            } // end if try to update

        }   // end of while

        logError("Total: "+t, club);
        if (err_nf != 0) logError("Total Not Found: "+err_nf, club);
        if (err_mf != 0) logError("Total Multiples Found: "+err_mf, club);
        //logError("Total Not Updated: "+err_nu, club); // doesn't work with this api to myqsl
        logError("Total Updated: "+updated, club);

    } catch (Exception e3) {

        msg = "SQL Exception: " +mNum+ ", " +name1+ ", " +name2;
        logError(msg, club);                           // log it

        out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
        out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
        out.println("<BR><BR>DB Import Failed for  " + club);
        out.println("<BR><BR>Exception Received: "+ e3.getMessage());
        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
    out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
    out.println("<BR><BR>The Web Id Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 // ********************************************************************
 //  Process the 'Import Lottery Weight' Request
 // ********************************************************************

 private void portWeight(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int wght = 0;

   String line = "";
   String mem_id = "";
   String weight = "";
   String fname = "";
   String lname = "";
   String msg = "";

   //
   //  read in the text file - must be named 'weight.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//weight.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\weight.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e2.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
   }

   //
   //
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //**********************************************************************
      //   The text file contains the info to import - get each line
      //
      //   Format:    username, weight#
      //
      //**********************************************************************
      
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         weight = "";

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {

            mem_id = tok.nextToken();
            weight = tok.nextToken();

         } else {

            weight = "";
         }

         if (!weight.equals( "" )) {

            //
            // add a weight entry for this user
            //
            wght = Integer.parseInt(weight);             
           
  
            pstmt2 = con.prepareStatement (
                        "INSERT INTO lassigns5 (username, lname, date, mins) " +
                        "VALUES (?, 'Saturday Lottery', 20080704, ?)");


            pstmt2.clearParameters();
            pstmt2.setString(1, mem_id);
            pstmt2.setString(2, weight);
  
            pstmt2.executeUpdate();

            pstmt2.close();
         }

      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Text File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 /**
  * ************************************************************************
  * portDemoClubs - Process the 'Import Lottery Weight' Request
  * 
  * File Format - comma delimited csv (Add extra columns of '?' if not all are present):
  * Club Name/Description (REQUIRED!)
  * Manufacturer
  * Club Type
  * ICN
  * Notes
  * For Sale - 0/1
  * Enabled - 0/1
  * 
  * ************************************************************************
  **/ 
 private void portDemoClubs(HttpServletRequest req, PrintWriter out, Connection con, String club) {
     
     Statement stmt = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int count = 0;
     int fcount = 0;
     int dupFound = 0;
     int mfr_id = 1;
     int forSale = 0;
     int enabled = 1;
     
     String line = "";
     String name = "";
     String mfr = "";           // Club Manufacturer
     String clubType = "";      // Driver, Iron, etc
     String icn = "";           // Inventory Control #
     String notes = "";
     String sforSale = "";
     String senabled = "";
     
     //
     //  read in the text file - must be named 'ghindata.csv'
     //
     boolean failed = false;
     boolean skip = false;
     boolean mfrPort = false;
     boolean mfrFound = false;
     
     FileReader fr = null;
     
     StringTokenizer tok = null;
     StringTokenizer tok2 = null;
     
     try {
         
         fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//democlubs.csv");
         
     }
     catch (Exception e1) {
         
         failed = true;
     }
     
     if (failed == true) {
         
         try {
             
             fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\democlubs.csv");
             
         }
         catch (Exception e2) {
             
             out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
             out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
             out.println("<BR><BR>File Read Failed for  " + club);
             out.println("<BR><BR>Exception Received: "+ e2.getMessage());
             out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
             out.println("</CENTER></BODY></HTML>");
             return;
         }
     }
  
     try {
         
         // See if we're to import unrecognized mfrs or not
         if (req.getParameter("mfrport") != null) {
             mfrPort = true;
         }
         
         BufferedReader bfrin = new BufferedReader(fr);
         line = new String();
         
         while ((line = bfrin.readLine()) != null) {       // get one line of text
             
             //  parse the line to gather all the info
             tok = new StringTokenizer( line, "," );        // delimiters are comma
             
             // Reset all variables
             name = "";
             mfr = "";
             mfr_id = 1;
             clubType = "";
             icn = "";
             notes = "";
             sforSale = "";
             senabled = "";
             forSale = 0;
             enabled = 1;
             
             skip = false;
             mfrFound = false;
             
             name = tok.nextToken().trim();
             mfr = tok.nextToken().trim();
             clubType = tok.nextToken().trim();
             icn = tok.nextToken().trim();
             notes = tok.nextToken().trim();
             
             sforSale = tok.nextToken().trim();
             if (sforSale.equals("1")) {
                 forSale = 1;
             } else {
                 forSale = 0;
             }
             
             senabled = tok.nextToken().trim();
             if (senabled.equals("0")) {
                 enabled = 0;
             } else {
                 enabled = 1;
             }
             
             if (name.equals("?")) {
                 name = "";
             }
             
             while (name.startsWith("\"")) {
                 name = name.substring(1);
             }
             while (name.endsWith("\"")) {
                 name = name.substring(0, name.length() - 1);
             }
             
             if (!name.equals("")) {        // At least the name/description must be present to continue
                 
                 if (mfr.equals("?")) {
                     mfr = "None/Unknown";
                 } else {
                     mfr = Utilities.toTitleCase(mfr);
                 }
                 
                 if (clubType.equals("?")) {
                     clubType = "Other";
                 }
                 
                 if (icn.equals("?")) {
                     icn = "";
                 }
                 
                 if (notes.equals("?")) {
                     notes = "";
                 }
                 
                 // Determine the mfr_id
                 stmt = con.createStatement();
                 rs = stmt.executeQuery("SELECT * FROM demo_clubs_mfr");
                 
                 while (rs.next()) {
                     if (mfr.toLowerCase().startsWith(rs.getString("mfr").toLowerCase())) {
                         mfr_id = rs.getInt("id");
                         mfrFound = true;
                     }
                 }
                 
                 stmt.close();
                 
                 // Mfr was not found, if set to add new mfrs, add this one
                 if (!mfrFound && mfrPort) {
                                          
                     pstmt = con.prepareStatement("INSERT INTO demo_clubs_mfr (mfr) VALUES (?)");
                     pstmt.clearParameters();
                     pstmt.setString(1, mfr);
                     
                     pstmt.executeUpdate();
                     
                     pstmt.close();
                     
                     pstmt = con.prepareStatement("SELECT id FROM demo_clubs_mfr WHERE mfr = ?");
                     pstmt.clearParameters();
                     pstmt.setString(1, mfr);
                     
                     rs = pstmt.executeQuery();
                     
                     if (rs.next()) {
                         mfr_id = rs.getInt("id");
                     }
                     
                     pstmt.close();
                 }
                 
                 // Check for invalid club type
                 if (!clubType.equalsIgnoreCase("Driver") && !clubType.equalsIgnoreCase("Wood") && !clubType.equalsIgnoreCase("Hybrid") &&
                     !clubType.equalsIgnoreCase("Iron") && !clubType.equalsIgnoreCase("Wedge") && !clubType.equalsIgnoreCase("Putter") &&
                     !clubType.equalsIgnoreCase("Specialty")  && !clubType.equalsIgnoreCase("Rental")) {
                     
                     clubType = "Other";
                 }  
                 
                 pstmt = con.prepareStatement("SELECT * FROM demo_clubs WHERE icn = ?");
                 pstmt.clearParameters();
                 pstmt.setString(1, icn);
                 
                 rs = pstmt.executeQuery();
                 
                 if (rs.next()) {
                     
                     String msg = "Club ICN already exists in database (must be unique!): " +mfr+ ", " + clubType + ", " + name + "  ICN: " + icn;
                     logError(msg, club);  
                     
                     dupFound++;
                     fcount++;
                     skip = true;
                 }
                 
                 pstmt.close();
                 
                 if (!skip) {
                     
                     pstmt = con.prepareStatement("INSERT INTO demo_clubs (name, type, mfr_id, notes, for_sale, enabled, icn) " +
                             "VALUES (?,?,?,?,?,?," + (icn.equals("") ? "NULL" : "?") + ")");
                     pstmt.clearParameters();
                     pstmt.setString(1, name);
                     pstmt.setString(2, clubType);
                     pstmt.setInt(3, mfr_id);
                     pstmt.setString(4, notes);
                     pstmt.setInt(5, forSale);
                     pstmt.setInt(6, enabled);
                     if (!icn.equals("")) {
                         pstmt.setString(7, icn);
                     }
                     
                     int tempCount = pstmt.executeUpdate();
                     if (tempCount > 0) {
                         count++;
                     } else {
                         
                         String msg = "Club import failed: " +mfr+ ", " + clubType + ", " + name + "  ICN: " + icn;
                         logError(msg, club);
                         fcount ++;
                     }
                 }
                 
             } else {
                 
                 String msg = "No club name found (REQUIRED!): " +mfr+ ", " + clubType + "  ICN: " + icn;
                 logError(msg, club);
                 fcount ++;
             }
         }
     } catch (Exception exc) {
         
         out.println("<HTML><HEAD><TITLE>Demo Clubs Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Demo Clubs Conversion Failed</H3>");
         out.println("<BR><BR>DB Import Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     out.println("<HTML><HEAD><TITLE>Demo Clubs Ported to DB</TITLE></HEAD>");
     out.println("<BODY><CENTER><H3>Demo Clubs Import Complete</H3>");
     out.println("<BR><BR>The Demo Clubs have Been Imported Successfully.");
     out.println("<BR><BR>Successful: " + count + "   Failed: " + fcount);
     out.println("<BR><BR>Duplicate ICN: " + dupFound);
     out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
     out.println("</CENTER></BODY></HTML>");
     out.close();
 }
 
 
 
 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************

 private void logError(String msg, String club) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//error.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(new java.util.Date() + space + msg);
      fout1.println();      // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\error.txt", true));

         //
         //  Put header line in text file
         //
         fout.print(new java.util.Date() + space + msg);
         fout.println();      // output the line

         fout.close();
      }
      catch (Exception ignore) {
      }
   }
 }  // end of logError


 // *********************************************************
 //  Strip zero '0' from start of alphanumeric string
 // *********************************************************

 private final static String remZeroS( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<ca2.length; i++ ) {
         ca2[i] = ca[i+1];
      } // end for

      return new String (ca2);

 } // end remZeroS


 // *********************************************************
 //  Strip special chars from string
 // *********************************************************

 private final static String stripSP( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];

      int n = 0;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= 'z' && oldLetter >= '#' ) {
            ca2[i] = oldLetter;
            n++;
         }
      } // end for

      char[] ca3 = new char [n];

      for ( int i=0; i<n; i++ ) {
         char oldLetter = ca2[i];
         ca3[i] = oldLetter;
      } // end for

      return new String (ca3);

 } // end stripSP


 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'></INPUT>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
