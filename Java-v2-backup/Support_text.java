/***************************************************************************************     
 *   Support_text:  This servlet will create a text file from the member database.
 *       
 *
 *
 *   called by:  support_text.htm
 *
 *
 *   Format of members.txt file:
 *
 *       
 *        7/18/03   Enhancements for Version 3 of the software.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;


public class Support_text extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                 // init DB objects
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
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Change the dir path below for the server running on -
   //
   PrintWriter fout = null;
     
   String omit = "";
   String username = "";
   String password = "";
   String mship = "";
   String mtype = "";
   String email = "";
   String lname = "";
   String mname = "";
   String fname = "";
   String wc = "";
   String memNum = "";
   String ghin = "";
   String locker = "";
   String bag = "";
   String posid = "";
   String msub_type = "";
   String line = "";

   int fail = 0;
   int birth = 0;
   int inact = 0;
   float c_hancap = 0;
   float g_hancap = 0;
   
   PreparedStatement stmt = null;

   //
   //  Create text file and Put header line in text file
   //
   try {
      //
      //  Dir path for the real server #1
      //
      fout = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//members.txt", true));

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try the other path
   //
   if (fail != 0) {

      fail = 0;         // reset
      try {
         //
         //  dir path for test pc
         //
         fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\members.txt", true));

      }
      catch (Exception ignore) {

         fail = 1;
      }
   }
       
   //
   //  Continue if we have a file
   //
   if (fail == 0) {

      //
      //  add header
      //
      fout.print("username(local#),password,mem#,fname,mi,lname,ghin#,email,membership type,member type");
      fout.println();      // output the line

      //
      //   Read the member db and create the text file
      //
      try {

         stmt = con.prepareStatement (
                  "SELECT * FROM member2b WHERE name_last != ?");

         stmt.clearParameters();               // clear the parms
         stmt.setString(1, omit);
         rs = stmt.executeQuery();            // execute the prepared stmt

         while(rs.next()) {

            username = rs.getString(1);
            password = rs.getString(2);
            lname = rs.getString(3);
            fname = rs.getString(4);
            mname = rs.getString(5);
            mship = rs.getString(6);
            mtype = rs.getString(7);
            email = rs.getString(8);
            c_hancap = rs.getFloat(10);
            g_hancap = rs.getFloat(11);
            wc = rs.getString(12);
            memNum = rs.getString(15);
            ghin = rs.getString(16);
            locker = rs.getString(17);
            bag = rs.getString(18);
            birth = rs.getInt(19);
            posid = rs.getString(20);
            msub_type = rs.getString(21);
            inact = rs.getInt("inact");

            fout.print(username);
            fout.print(",");            // seperate with comma
            fout.print(password);
            fout.print(",");
            fout.print(memNum);
            fout.print(",");
            fout.print(fname);
            fout.print(",");
            fout.print(mname);
            fout.print(",");
            fout.print(lname);
            fout.print(",");
            fout.print(ghin);
            fout.print(",");
            fout.print(email);
            fout.print(",");
            fout.print(mship);
            fout.print(",");
            fout.print(mtype);
            fout.print(",");
            fout.print(inact);
   //         fout.print(c_hancap);
   //         fout.print(",");
   //         fout.print(g_hancap);
   //         fout.print(",");
   //         fout.print(wc);
   //         fout.print(",");
   //         fout.print(locker);
   //         fout.print(",");
   //         fout.print(bag);

            fout.println();      // output the line

         }                       // end of while
         fout.close();
         //stmt.close();

      }
      catch (Exception e3) {

         out.println("<HTML><HEAD><TITLE>Text File Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>Unable to create text file for  " + club);
         out.println("<BR><BR>Exception Received: "+ e3.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         Connect.close(rs,stmt,con);
         return;
      } finally {
          Connect.close(rs,stmt,con);
      }
   } else {  // no output file

         out.println("<HTML><HEAD><TITLE>Text File Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>Unable to create text file for  " + club);
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         Connect.close(rs,stmt,con);
         return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Created From DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Created</H3>");
   out.println("<BR><BR>The members.txt Text File has Been Created.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   Connect.close(rs,stmt,con);

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
