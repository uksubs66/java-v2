/***************************************************************************************     
 *   Support_port_teetimes:  This servlet will port a text file containing past tee times
 *                           from a new client into teepast.
 *
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


public class Support_port_teetimes extends HttpServlet {
                           
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


   String line = "";
   String dates = "";
   String times = "";
   String course = "";
   String username = "";
   String fname = "";
   String lname = "";
   String gtype = "";
   String temp = "";
   String last_course = "";
   
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String gtype1 = "";
   String gtype2 = "";
   String gtype3 = "";
   String gtype4 = "";
   String gtype5 = "";
   
   int count = 0;
   int tokcount = 0;
   int i = 0;
   int time = 0;
   int last_time = 0;
   int hr = 0;
   int min = 0;
   int teecurr_id = 100001;  
   short show = 1;
   long mm = 0;
   long dd = 0;
   long yy = 0;
   long date = 0;
   long last_date = 0;


   //
   //  read in the text file - must be named 'teetimes.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//teetimes.csv");

   }
   catch (Exception e1) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>File Read Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e1.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   
   try {
      
      // First, delete any residue tee times from teepast (we want to start fresh)
      
      stmt = con.createStatement();      

      stmt.executeUpdate("DELETE FROM teepast2");        

      stmt.close();                 
   }
   catch (Exception ignore) {
   }
   
   try {
      
      //  Now read in the file

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         count++;                                            // keep track of line #

         //  parse the line to gather all the info

         StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

         tokcount = tok.countTokens();

         // make sure we have the essentials
         
         if (tokcount > 6) {   
    
            dates = tok.nextToken();
            times = tok.nextToken();
            course = tok.nextToken();
            username = tok.nextToken();
            fname = tok.nextToken();
            lname = tok.nextToken();
            gtype = tok.nextToken();
              
            
            tok = new StringTokenizer( dates, " " );        // date = "mm/dd/yy 0:00"

            if ( tok.countTokens() > 1 ) {
               dates = tok.nextToken();              //  get mm/dd/yy
            }

            tok = new StringTokenizer( dates, "/" );        // date = "mm/dd/yy"

            if ( tok.countTokens() > 2 ) {
               temp = tok.nextToken();              //  mm
               mm = Long.parseLong(temp);
               temp = tok.nextToken();              //  dd
               dd = Long.parseLong(temp);
               temp = tok.nextToken();              //  yy               
               yy = Long.parseLong(temp);
               yy += 2000;                          // yy = 2010
               date = (yy * 10000) + (mm * 100) + dd;        // yyyymmdd
            } else {
               date = 9999;
            }

            tok = new StringTokenizer( times, ":" );        // time = "00:00:00"

            if ( tok.countTokens() > 1 ) {
               temp = tok.nextToken();              //  hr
               hr = Integer.parseInt(temp);
               temp = tok.nextToken();              //  min
               min = Integer.parseInt(temp);
               time = (hr * 100) + min;             // hhmm
            } else {
               time = 9999;
            }

            if (course.startsWith("Shore")) {
               course = "Shore";
            } else {
               course = "Dunes";
            }
            
            if (username.equals("0")) {
               
               username = "";     // guest
               
               if (gtype.equalsIgnoreCase("Guest of Member")) {
                  gtype = "Guest w/Member";
               } else if (gtype.equalsIgnoreCase("Clergy")) {
                  gtype = "Clergy / Spec";
               } else if (gtype.equalsIgnoreCase("PGA Guest Pro Shop")) {
                  gtype = "PGA GST of Pro";
               } else if (gtype.startsWith("Unaccompanied Guest")) {
                  gtype = "Guest w/o Member";
               } else if (gtype.equalsIgnoreCase("Honorary Member") || gtype.equalsIgnoreCase("Free Guest")) {
                  gtype = "Member Free";
               } else if (gtype.equalsIgnoreCase("Guest of Manager")) {
                  gtype = "Guest of MGR";
               } else if (gtype.equalsIgnoreCase("Junior Dependent")) {
                  gtype = "Jr Dependent";
               } else if (gtype.equalsIgnoreCase("PGA Guest Member")) {
                  gtype = "PGA GST of Member";
               } else if (gtype.equalsIgnoreCase("PGA Guest Pro Shop")) {
                  gtype = "PGA GST of Pro";
               } else if (gtype.equalsIgnoreCase("Special Group")) {
                  gtype = "Clergy / Spec";
               } else if (gtype.equalsIgnoreCase("Spouse")) {
                  gtype = "Relative";
               } else if (gtype.equalsIgnoreCase("Staff")) {
                  gtype = "Staff";
               } else if (gtype.startsWith("Tournament")) {
                  gtype = "Tournament GST";
               } else {
                  gtype = "Relative";
               }  
               player = gtype + " " + fname + " " + lname;
               
            } else {              // member
               
               player = fname + " " + lname;
            }
            
            
            //
            //  Gather all players for each tee time and then create the tee time
            //
            if (date == last_date && time == last_time && course.equals(last_course)) {  // if same as last entry - keep building the tee time
               
               //  place player info in next available slot
               
               if (player2.equals("")) {
                  player2 = player;
                  user2 = username;    
                  if (username.equals("")) {
                     gtype2 = gtype;
                  } else {
                     gtype2 = "";
                  }
               } else if (player3.equals("")) {
                  player3 = player;
                  user3 = username;
                  if (username.equals("")) {
                     gtype3 = gtype;
                  } else {
                     gtype3 = "";
                  }
               } else if (player4.equals("")) {
                  player4 = player;
                  user4 = username;
                  if (username.equals("")) {
                     gtype4 = gtype;
                  } else {
                     gtype4 = "";
                  }
               } else {
                  player5 = player;
                  user5 = username;
                  if (username.equals("")) {
                     gtype5 = gtype;
                  } else {
                     gtype5 = "";
                  }
               }
               
            } else {          // new tee time - save previous tee time
               
               if (last_date != 0) {   // if not the first time
                  
                   yy = last_date / 10000;
                   mm = (last_date - (yy * 10000)) / 100;
                   dd = (last_date - (yy * 10000)) - (mm * 100);
            
                   hr = last_time / 100;
                   min = last_time - (hr * 100);
                  
                   //  create tee time from previous info
                  
                   pstmt = con.prepareStatement (
                      "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                      "restriction, rest_color, player1, player2, player3, player4, username1, " +
                      "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
                      "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
                      "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
                      "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id, " +
                      "custom_string, custom_int, pos1, pos2, pos3, pos4, pos5," +
                      "mship1, mship2, mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, " +
                      "gtype1, gtype2, gtype3, gtype4, gtype5, " +
                      "grev1, grev2, grev3, grev4, grev5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                      "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                      "?, ?, ?, ?, ?, ?, ?, " +
                      "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                      "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");      

                   pstmt.clearParameters();        // clear the parms
                   pstmt.setLong(1, last_date);         // put the parms in pstmt for tee slot
                   pstmt.setLong(2, mm);
                   pstmt.setLong(3, dd);
                   pstmt.setLong(4, yy);
                   pstmt.setString(5, "");
                   pstmt.setInt(6, hr);
                   pstmt.setInt(7, min);
                   pstmt.setInt(8, last_time);
                   pstmt.setString(9, "");
                   pstmt.setString(10, "");
                   pstmt.setString(11, "");
                   pstmt.setString(12, "");
                   pstmt.setString(13, player1);
                   pstmt.setString(14, player2);
                   pstmt.setString(15, player3);
                   pstmt.setString(16, player4);
                   pstmt.setString(17, user1);
                   pstmt.setString(18, user2);
                   pstmt.setString(19, user3);
                   pstmt.setString(20, user4);
                   pstmt.setString(21, "PO");
                   pstmt.setString(22, "PO");
                   pstmt.setString(23, "PO");
                   pstmt.setString(24, "PO");
                   pstmt.setShort(25, show);
                   pstmt.setShort(26, show);
                   pstmt.setShort(27, show);
                   pstmt.setShort(28, show);
                   pstmt.setShort(29, show);
                   pstmt.setString(30, player5);
                   pstmt.setString(31, user5);
                   pstmt.setString(32, "PO");
                   pstmt.setShort(33, show);
                   pstmt.setString(34, last_course);
                   pstmt.setInt(35, 0);
                   pstmt.setInt(36, 0);
                   pstmt.setInt(37, 0);
                   pstmt.setInt(38, 0);
                   pstmt.setString(39, user1);
                   pstmt.setString(40, user2);
                   pstmt.setString(41, user3);
                   pstmt.setString(42, user4);
                   pstmt.setString(43, user5);
                   pstmt.setString(44, "");
                   pstmt.setString(45, "");
                   pstmt.setString(46, "");
                   pstmt.setString(47, "");
                   pstmt.setString(48, "");
                   pstmt.setInt(49, 0);
                   pstmt.setInt(50, 0);
                   pstmt.setString(51, "");
                   pstmt.setString(52, "");
                   pstmt.setString(53, "");
                   pstmt.setInt(54, 0);
                   pstmt.setInt(55, 0);
                   pstmt.setInt(56, 0);
                   pstmt.setInt(57, 0);
                   pstmt.setInt(58, 0);
                   pstmt.setInt(59, teecurr_id);
                   pstmt.setInt(60, 0);
                   pstmt.setString(61, "");         
                   pstmt.setInt(62, 0);
                   pstmt.setInt(63, 0);
                   pstmt.setInt(64, 0);
                   pstmt.setInt(65, 0);
                   pstmt.setInt(66, 0);
                   pstmt.setInt(67, 0);
                   pstmt.setString(68, "");
                   pstmt.setString(69, "");
                   pstmt.setString(70, "");
                   pstmt.setString(71, "");
                   pstmt.setString(72, "");
                   pstmt.setString(73, "");
                   pstmt.setString(74, "");
                   pstmt.setString(75, "");
                   pstmt.setString(76, "");
                   pstmt.setString(77, "");
                   pstmt.setString(78, gtype1);
                   pstmt.setString(79, gtype2);
                   pstmt.setString(80, gtype3);
                   pstmt.setString(81, gtype4);
                   pstmt.setString(82, gtype5);
                   pstmt.setInt(83, 0);
                   pstmt.setInt(84, 0);
                   pstmt.setInt(85, 0);
                   pstmt.setInt(86, 0);
                   pstmt.setInt(87, 0);
                   pstmt.setInt(88, 0);
                   pstmt.setInt(89, 0);
                   pstmt.setInt(90, 0);
                   pstmt.setInt(91, 0);
                   pstmt.setInt(92, 0);

                   pstmt.executeUpdate();        // move the tee slot to teepast

                   pstmt.close();
                   
                   teecurr_id++;  
               }
                              
               player1 = player;     // init fields for next tee time
               player2 = "";  
               player3 = "";  
               player4 = "";  
               player5 = "";  
               user1 = username;  
               user2 = "";  
               user3 = "";  
               user4 = "";  
               user5 = "";
               if (username.equals("")) {
                  gtype1 = gtype;
               } else {
                  gtype1 = "";
               }
               gtype2 = "";
               gtype3 = "";
               gtype4 = "";
               gtype5 = "";
               
               last_date = date;
               last_time = time; 
               last_course = course;
            }
         }
         
      }   // end of while
      
      //
      //  Add the last tee time if there is one
      //
      if (date > 20100900 && !player1.equals("")) {
         
          yy = date / 10000;
          mm = (date - (yy * 10000)) / 100;
          dd = (date - (yy * 10000)) - (mm * 100);

          hr = time / 100;
          min = time - (hr * 100);
         
          pstmt = con.prepareStatement (
             "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
             "restriction, rest_color, player1, player2, player3, player4, username1, " +
             "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
             "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
             "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
             "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id, " +
             "custom_string, custom_int, pos1, pos2, pos3, pos4, pos5," +
             "mship1, mship2, mship3, mship4, mship5, mtype1, mtype2, mtype3, mtype4, mtype5, " +
             "gtype1, gtype2, gtype3, gtype4, gtype5, " +
             "grev1, grev2, grev3, grev4, grev5, guest_id1, guest_id2, guest_id3, guest_id4, guest_id5) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
             "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");      

          pstmt.clearParameters();        // clear the parms
          pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
          pstmt.setLong(2, mm);
          pstmt.setLong(3, dd);
          pstmt.setLong(4, yy);
          pstmt.setString(5, "");
          pstmt.setInt(6, hr);
          pstmt.setInt(7, min);
          pstmt.setInt(8, time);
          pstmt.setString(9, "");
          pstmt.setString(10, "");
          pstmt.setString(11, "");
          pstmt.setString(12, "");
          pstmt.setString(13, player1);
          pstmt.setString(14, player2);
          pstmt.setString(15, player3);
          pstmt.setString(16, player4);
          pstmt.setString(17, user1);
          pstmt.setString(18, user2);
          pstmt.setString(19, user3);
          pstmt.setString(20, user4);
          pstmt.setString(21, "PO");
          pstmt.setString(22, "PO");
          pstmt.setString(23, "PO");
          pstmt.setString(24, "PO");
          pstmt.setShort(25, show);
          pstmt.setShort(26, show);
          pstmt.setShort(27, show);
          pstmt.setShort(28, show);
          pstmt.setShort(29, show);
          pstmt.setString(30, player5);
          pstmt.setString(31, user5);
          pstmt.setString(32, "PO");
          pstmt.setShort(33, show);
          pstmt.setString(34, course);
          pstmt.setInt(35, 0);
          pstmt.setInt(36, 0);
          pstmt.setInt(37, 0);
          pstmt.setInt(38, 0);
          pstmt.setString(39, user1);
          pstmt.setString(40, user2);
          pstmt.setString(41, user3);
          pstmt.setString(42, user4);
          pstmt.setString(43, user5);
          pstmt.setString(44, "");
          pstmt.setString(45, "");
          pstmt.setString(46, "");
          pstmt.setString(47, "");
          pstmt.setString(48, "");
          pstmt.setInt(49, 0);
          pstmt.setInt(50, 0);
          pstmt.setString(51, "");
          pstmt.setString(52, "");
          pstmt.setString(53, "");
          pstmt.setInt(54, 0);
          pstmt.setInt(55, 0);
          pstmt.setInt(56, 0);
          pstmt.setInt(57, 0);
          pstmt.setInt(58, 0);
          pstmt.setInt(59, teecurr_id);
          pstmt.setInt(60, 0);
          pstmt.setString(61, "");         
          pstmt.setInt(62, 0);
          pstmt.setInt(63, 0);
          pstmt.setInt(64, 0);
          pstmt.setInt(65, 0);
          pstmt.setInt(66, 0);
          pstmt.setInt(67, 0);
          pstmt.setString(68, "");
          pstmt.setString(69, "");
          pstmt.setString(70, "");
          pstmt.setString(71, "");
          pstmt.setString(72, "");
          pstmt.setString(73, "");
          pstmt.setString(74, "");
          pstmt.setString(75, "");
          pstmt.setString(76, "");
          pstmt.setString(77, "");
          pstmt.setString(78, gtype1);
          pstmt.setString(79, gtype2);
          pstmt.setString(80, gtype3);
          pstmt.setString(81, gtype4);
          pstmt.setString(82, gtype5);
          pstmt.setInt(83, 0);
          pstmt.setInt(84, 0);
          pstmt.setInt(85, 0);
          pstmt.setInt(86, 0);
          pstmt.setInt(87, 0);
          pstmt.setInt(88, 0);
          pstmt.setInt(89, 0);
          pstmt.setInt(90, 0);
          pstmt.setInt(91, 0);
          pstmt.setInt(92, 0);

          pstmt.executeUpdate();        // move the tee slot to teepast

          pstmt.close();
      }

   }
   catch (Exception e3) {

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>DB Add or Update Failed for  " + club);
      out.println("<BR><BR>Exception Received on Line Number " + count + ", Error = "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Import Complete</H3>");
   out.println("<BR><BR>" +count+ " Tee Times Added for  " + club);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
      
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
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>");
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
   out.println("<BR><BR><a href=\"/" +rev+ "/support_main.htm\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }
 
}
