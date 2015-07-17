/***************************************************************************************
 *   Support_import:  This class will import files for the club and update the member roster.
 *
 *
 *   called by:  support_main3.htm
 *
 *   File Names to Import:    email.txt
 *                            bag.txt
 *                            birth.txt
 *                            hndcp.txt
 *                            webid.txt
 *
 *
 *   created: 7/07/2005   Bob P.
 *
 *   last updated:
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

   if (req.getParameter("webid") != null) {

      portWebid(req, out, con, club);             // update handicaps
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
   //  read in the text file - must be named 'email.txt'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//email.txt");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\email.txt");

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
   //  read in the text file - must be named 'bag.txt'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//bag.txt");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\bag.txt");

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
      //   Format:    username, bag#
      //
      //      -OR-    lname, fname, bag#
      //
      //      -OR-    fname lname, bag#
      //
      //********************************************************************
      //
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
   //  read in the text file - must be named 'birth.txt'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//birth.txt");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\birth.txt");

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
   //  read in the text file - must be named 'hndcp.txt'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//hndcp.txt");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\hndcp.txt");

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
         if ((u_hndcp.equalsIgnoreCase("NH")) || (u_hndcp.equalsIgnoreCase("NHL"))) {

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

         if ((c_hndcp.equalsIgnoreCase("NH")) || (c_hndcp.equalsIgnoreCase("NHL"))) {

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
 //  Process the 'Import Webid' Request (Web Site Id for Interface)
 // ********************************************************************

 private void portWebid(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int t = 0;

   String line = "";
   String mem_id = "";
   String webid = "";
   String name1 = "";
   String name2 = "";
   String msg = "";
   String skip = "";
   String here = "";
   String sql = "";
   String[] col;
   
   boolean byname = (req.getParameter("byname") != null) ? true : false;
   
   //
   //  read in the text file - must be named 'hndcp.txt'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//webid.txt");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\webid.txt");

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
      //       -OR-      webid, username, name1 (optional), name2 (optional)  (name used for error message only)
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text
out.println("<br>");
         //  parse the line to gather all the info
         t++;
         webid = "";
         mem_id = "";
         name1 = "";
         skip = "";
         name2 = "";    
         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if (club.equals("fourbridges")) {     
         /*
             //t = tok.countTokens();
here = "0";
             
             webid = tok.nextToken();              // get webid
             
here = "1";
             name1 = tok.nextToken();               // fname
             if (name1 == null) name1 = "";
             
here = "2";
             skip = tok.nextToken();               // skip  
             
here = "3";
             name2 = tok.nextToken();               // lname
             if (name2 == null) name2 = "";
             
here = "4";
             mem_id = tok.nextToken();             // get username    
             if (mem_id == null) mem_id = "";         
             */
         
         col = line.split(",");
         for (int x=0; x<col.length; x++)
             out.println("<br>" + x + "=" + col[x]);
         
         webid = col[0];
         name1 = col[1];
         skip = col[2];
         name2 = col[3];
         mem_id = col[4];
         
         } else {
             
             webid = tok.nextToken();              // get webid
             mem_id = tok.nextToken();             // get username

             name1 = "";
             name2 = "";

             if ( tok.countTokens() > 0 ) {
                name1 = tok.nextToken();            // name1
             }
             if ( tok.countTokens() > 0 ) {
                name2 = tok.nextToken();            // name2
             }

         } // end if club ==
         
         //if (!mem_id.equals( "" ) && !webid.equals( "" )) {
         if (!webid.equals( "" )) {

            //
            // update member record
            //
             
             
            if (byname) {
                here = "5";
                //sql = "UPDATE member2b SET webid = ? WHERE name_last = ? AND name_first = ?";
                sql = "UPDATE member2b SET webid = " + webid + " WHERE name_last = " + name2 + " AND name_first = " + name1 + "";
                out.println("<br>" + sql);
                /*
                pstmt2 = con.prepareStatement (sql);

                pstmt2.clearParameters();
                pstmt2.setString(1, webid);
                pstmt2.setString(2, name2);
                pstmt2.setString(3, name1);
                */
            } else {
                here = "6";
                sql = "UPDATE member2b SET webid = ? WHERE username = ?";
                out.println("<br>" + sql);
                
                /*
                pstmt2 = con.prepareStatement (sql);

                pstmt2.clearParameters();
                pstmt2.setString(1, webid);
                pstmt2.setString(2, mem_id);
                */
            }

            /*
            count = pstmt2.executeUpdate();

            pstmt2.close();

            if (count == 0) {    // if not updated - record it

                /*
               if (!name1.equals( "" )) {          
                  msg = "Member not found: " +mem_id+ ", Name = " +name1+ " " +name2;
               } else {
                  msg = "Member not found: " +mem_id;
               }
                */
            /*
               if (byname) {
                   msg = "Member not found: " +mem_id+ ", Name = " +name1+ " " +name2;
               } else {
                   msg = "Member not found: " +mem_id;
               }
               
               logError(msg, club);                           // log it
            }
            */
         }

      }   // end of while

   }
   catch (Exception e3) {

      msg = "SQL Exception: " +mem_id+ ", " +name1+ ", " +name2;
      logError(msg, club);                           // log it

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR>Got to: "+ here);
      //out.println("<BR><BR>Found: "+ t);
      out.println("<BR><BR>Found lines: "+ t);
      out.println("<BR><BR>Got webid: "+ webid);
      out.println("<BR><BR>Got name1: "+ name1);
      out.println("<BR><BR>Got sjip: "+ skip);
      out.println("<BR><BR>Got name2: "+ name2);
      out.println("<BR><BR>Got mem_id: "+ mem_id);
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
