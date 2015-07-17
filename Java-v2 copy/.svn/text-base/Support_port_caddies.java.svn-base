/***************************************************************************************     
 * 
 *   Support_port_caddies:  This servlet will import a list of caddies from a text file
 *                          and create records for them in the 'caddies' table of the
 *                          caddie database for the club selected (logged in from).
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
import com.foretees.common.Connect;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


public class Support_port_caddies extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
     
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
   // Load the JDBC Driver and connect to DB
   //
   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = Connect.getCaddieCon(club);                        //  ADD THIS !!!!!!!!!!!!!!!!!!!!!!!

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   Member member = new Member();

   String line = "";
   String name = "";
   String fname = "";
   String mi = "";
   String suffix = "";
   String lname = "";
   String mem_id = "";
   String password = "";
   String ctype = "";
   String phone = "";
   String cell_phone = "";
   String email = "";      
   String temp = "";
   int count = 0;
   int tokcount = 0;
   int caddie_type = 0;
   int i = 0;
   int birth = 0;
   int length = 0;
   int dup = 0;
   boolean replace = false;


   //
   //  There are 2 calls to this class:
   //
   //     submit=add - to add or update the member table 
   //
   //     submit=replace - to delete the member table and then replace it
   //
   if (req.getParameter("replace") != null) {

      replace = true;

      try {

         stmt = con.createStatement();        // create a statement

         stmt.executeUpdate("DELETE FROM caddies");          // delete all records from the table

         stmt.close();              // close the stmt

      }
      catch (Exception ignore) {

      }
   }
   //
   //  read in the text file - must be named 'caddies.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//caddies.csv");

   }
   catch (Exception e1) {
      
      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>File Read Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e1.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   
   /***************************************************************************************     
    * 
    *  Read the caddie file and create db records in caddie system
    * 
    *  File:  webapps/'clubname'/caddies.csv     (comma delineated file with caddie data)
    * 
    *  Record Format:
    *           Last Name
    *           First Name
    *           Rating (i.e.  A, B, Honor, etc)
    *           Email (opt)
    *           Phone (opt)
    *           Cell Phone (opt)
    * 
    * **** NOTICE: ****  YOU MUST CHANGE THE CADDIE TYPES BELOW TO MATCH THE IDs IN THE SYSTEM !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    * 
    ***************************************************************************************
    */
   
   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         count++;                                            // keep track of line #

         //  parse the line to gather all the info

         StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

         tokcount = tok.countTokens();


         lname = tok.nextToken();
         fname = tok.nextToken();
//         mi = tok.nextToken();
         ctype = tok.nextToken();
       
         // make sure we have the essentials
         
         if (!lname.equals("?") && !lname.equals("") &&
             !fname.equals("?") && !fname.equals("") &&   
             !ctype.equals("?") && !ctype.equals("")) {   
    
            email = "";
            phone = "";
            cell_phone = "";
           
            if ( tok.countTokens() > 0 ) {

               email = tok.nextToken();
            }
            if (email.equals( "?" )) {

               email = "";
            }
       
            if ( tok.countTokens() > 0 ) {

               phone = tok.nextToken();
            }
            if (phone.equals( "?" )) {

               phone = "";
            }

            if ( tok.countTokens() > 0 ) {

               cell_phone = tok.nextToken();
            }
            if (cell_phone.equals( "?" )) {

               cell_phone = "";
            } 
            
            //
            //  Remove any leading or trailing spaces, etc.
            //
            ctype = ctype.trim();
            fname = fname.trim();
            lname = lname.trim();

            if (!email.equals("")) {
              email = email.trim();
            }
            if (!phone.equals("")) {
              phone = phone.trim();
            }
            if (!cell_phone.equals("")) {
              cell_phone = cell_phone.trim();
            }

            
            // ************************************************************************************* 
            //   CUSTOM PROCESSING - Change this each time !!!!!!!!!!!!!!!!!!!
            // ************************************************************************************* 
            
            caddie_type = 3;       // default
            
            if (ctype.equalsIgnoreCase("AA")) {    // map these to the autoid value in the caddie_types table
               
               caddie_type = 1;
               
            } else if (ctype.equalsIgnoreCase("A")) {
               
               caddie_type = 2;
               
            } else if (ctype.equalsIgnoreCase("B")) {
               
               caddie_type = 3;
            }
               
            
            
      
            //*****************************************************************************************
            //  Common Processing
            //*****************************************************************************************
            //
            tok = new StringTokenizer( fname, " " );        // check first name for mi

            if ( tok.countTokens() > 1 ) {
               fname = tok.nextToken();
               mi = tok.nextToken();
            }

            fname = toTitleCase( fname );

            suffix = "";

            tok = new StringTokenizer( lname, " _" );        // check last name for suffix

            if ( tok.countTokens() > 1 ) {
               lname = tok.nextToken();
               suffix = tok.nextToken();
            }

            lname = toTitleCase( lname );

            password = lname;                    // use last name for password !!!

            if (!suffix.equals( "" )) {    

               lname = lname + "_" + suffix;
            }
            
            mem_id = getUsername(fname, lname);     // username = first initial + lname (i.e. tjones)


            //
            //  verify the email address
            //
            if (!email.equals( "" )) {

               FeedBack feedback = (member.isEmailValid(email));   // verify the address

               if (!feedback.isPositive()) {              // if error

                  email = "";                             // do not use
               }
            }

            //
            //  first check if username already exists
            //
            temp = mem_id;              // save original username
            boolean dupName = true;
            i = 1;

            pstmt2 = con.prepareStatement (
                     "SELECT type FROM caddies WHERE login_username = ?");

            while (dupName == true) {
               
               pstmt2.clearParameters();
               pstmt2.setString(1, mem_id);
               rs = pstmt2.executeQuery();            // execute the prepared stmt

               if (rs.next()) {
                  
                  i++; 
                  mem_id = (temp + i);     // match found - change the username and try again
                  
               } else {

                  dupName = false;       // username is unique - continue
               }
            }
            
            pstmt2.close();              // close the stmt


            try {

               //
               // Add the caddie
               //
               pstmt2 = con.prepareStatement (
                  "INSERT INTO caddies (created_on, modified_on, enabled, last_name, first_name, " +
                  "address, city, state, zip, telephone, email, login_username, login_password, " +
                  "last_accessed_on, this_accessed_on, cell, type, cellphone_provider, confirm_password) " +
                  "VALUES (now(), now(), 1, ?, ?, " +
                  "'', '', '', '', ?, ?, ?, ?, " +
                  "now(), now(), ?, ?, '', ?)");

               pstmt2.clearParameters();        // clear the parms
               pstmt2.setString(1, lname);        // put the parm in stmt
               pstmt2.setString(2, fname);
               pstmt2.setString(3, phone);
               pstmt2.setString(4, email);
               pstmt2.setString(5, mem_id);
               pstmt2.setString(6, password);
               pstmt2.setString(7, cell_phone);
               pstmt2.setInt(8, caddie_type);
               pstmt2.setString(9, password);
               pstmt2.executeUpdate();          // execute the prepared stmt

               pstmt2.close();              // close the stmt

            }
              catch (Exception exc) {
                  out.println("<br>Insert failed for " + lname + ", " + fname + "(" + exc.getMessage() + ")");
            }
              
         
         }   // end of IF record is ok (name exists)   
         
         
      }   // end of while

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Add or Update Failed for  " + club);
      out.println("<BR><BR>Exception Received on Line Number " + count + ", Tok Count = " + tokcount + ", memid = " +mem_id+ ", Error = "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }



   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The Caddie Text File has Been Converted.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   
   
 }   
                                 
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
 //  Strip letter 'A' from end of string
 // *********************************************************

 private final static String stripA( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '9' ) {
            ca2[i] = oldLetter;
         }
      } // end for

      return new String (ca2);

 } // end stripA


 // *********************************************************
 //  Strip last letter from end of string
 // *********************************************************

 private final static String stripA2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length - 1];


      for ( int i=0; i<(ca.length-1); i++ ) {
         char oldLetter = ca[i];
         ca2[i] = oldLetter;
      } // end for

      return new String (ca2);

 } // end stripA2


 // *********************************************************
 //  Convert Upper case names to title case (Bob P...)
 // *********************************************************

 private final static String toTitleCase( String s ) {

      char[] ca = s.toCharArray();

      boolean changed = false;
      boolean capitalise = true;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '/'
              || ':' <= oldLetter && oldLetter <= '?'
              || ']' <= oldLetter && oldLetter <= '`' ) {
            /* whitespace, control chars or punctuation */
            /* Next normal char should be capitalized */
            capitalise = true;
         } else {
            char newLetter  = capitalise
                              ? Character.toUpperCase(oldLetter)
                              : Character.toLowerCase(oldLetter);
            ca[i] = newLetter;
            changed |= (newLetter != oldLetter);
            capitalise = false;
         }
      } // end for

      return new String (ca);

 } // end toTitleCase


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero( String s ) {


      int memid = 0;
      String newS = "";
           
      //
      //  convert string to int to drop leading zeros
      //
      try {
         memid = Integer.parseInt(s);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      newS = String.valueOf( memid );      // convert back to string

      return new String (newS);

 } // end remZero


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
 //  Create a username from the first and last names
 // *********************************************************

 private final static String getUsername( String first, String last ) {

      char[] caf = first.toCharArray();         // first name
      char[] cal = last.toCharArray();          // last name
      char[] cau = new char [cal.length + 1];   // username

      cau[0] = caf[0];          // get first letter of first name

      for ( int i=0; i<cal.length; i++ ) {
         cau[i+1] = cal[i];
      } // end for

      return new String (cau);      // return the username

 } // end remZeroS


 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate


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
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");

 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>" + e.getMessage());
   out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Support_main\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }
 
}
