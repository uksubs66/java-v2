/***************************************************************************************
 *   Support_getProEmails: Displays a list of emails for all active clubs
 * 
 * 
 *   //  Remove duplicate email addresses from the file provided.
 *   //
 *   //    Input File:  name = email_list.csv (in club folder)
 *   //                 contents = single column of email addresses in alphabetical order!!!!
 *   //
 *   //    Output File:  name = emails.csv (in club folder)
 *   //                  contents = single column of email addresses with dups removed
 *   //
 * 
 * 
 *    8/24/10  Bob  Add methods to process an email csv file and remove the duplicate addresses.
 *    8/23/10  Bob  Get emails from staff_list instead of club5.
 *    6/01/09  Bob  Weed out any demo sites.
 * 
 *   12/12/08  Bob  Make text boxes a little larger and change number of addresses per box
 *                  from 100 to 90 to prevent server errors.
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import com.foretees.common.Connect;


public class Support_getProEmails extends HttpServlet {
    
    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 String sales = "sales";

 
 String SEPARATOR = ",";

    public void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

         HttpSession session = null; 


         // Make sure user didn't enter illegally.........

         session = req.getSession(false);  // Get user's session object (no new one)

         if (session == null) {

            invalidUser(out);            // Intruder - reject
            return;
         }

         String user = (String)session.getAttribute("user");   // get username
         String club = (String)session.getAttribute("club");   // get club name

         if (!user.equals( support ) && !user.startsWith ( sales )) {

            invalidUser(out);            // Intruder - reject
            return;
         }

        if (req.getParameter("names") != null) {       // if request is to get a list of emails with names

            try{

               resp.setContentType("application/vnd.ms-excel");    // response in Excel Format

            }
            catch (Exception exc) {
            }

            getNames(out);
            return;
        }


        if (req.getParameter("dups") != null) {       // if request is to Remove Dup Email Addresses from File

            remDups(user, club, out);
            return;
        }


        if (req.getParameter("list") != null) {       // if request is to List All Email Addresses

            listEmails(user, out);
            return;
        }


        //
        //   First call here - display a menu
        //
        out.println("<HTML><HEAD><TITLE>Get Pro Emails</TITLE></HEAD><CENTER><BR>");

        out.println("<H3>Get Pro Emails</H3>");
        out.println("<P>Select the method you would like to collect these emails.</P><BR>");
        
        out.println("<BR> <A HREF=\"/" +rev+ "/servlet/Support_getProEmails?list=yes\">Get List on Web Page</A>.");

        
        /*      // we might want to provide some options in the future
         * 
            out.println("<p align=\"center\">To download an Excel file, select the type of list and then click 'Continue' below.");
            out.println("<font size=\"2\"><br><br>");

            out.println("<form method=\"post\" action=\"Proshop_getProEmails\">");

            out.println("<input type=\"hidden\" name=\"todo\" value=\"getList\">");
          

            out.println("<b>Staff to Include:</b>&nbsp;&nbsp;");
            
            
            out.println("<input type=\"checkbox\" name=\"????\" value=\"1\">&nbsp;&nbsp;???????????");
            
            
            out.println("</p><br><br>");

            out.println("<p align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font></p>");
         */
        
            out.println("<p align=\"center\">");
            out.println("<font size=\"2\"><br><br>");
            out.println("<form method=\"post\" action=\"Support_getProEmails\">");
            out.println("<input type=\"hidden\" name=\"todo\" value=\"getList\">");
            out.println("<input type=\"submit\" value=\"Download Excel File for Newsletters\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font></p>");
          
        
         if (user.equals( support )) {                                              // if support (do not allow for sales)
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
         } else {
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
         }        
        out.println("</CENTER></BODY></HTML>");

        out.close();
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
        
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        
         HttpSession session = null; 


         // Make sure user didn't enter illegally.........

         session = req.getSession(false);  // Get user's session object (no new one)

         if (session == null) {

            invalidUser(out);            // Intruder - reject
            return;
         }

         String user = (String)session.getAttribute("user");   // get username
         String club = (String)session.getAttribute("club");   // get club name

        String action = "";
        if (req.getParameter("todo") != null) action = req.getParameter("todo");

        if (action.equals("refresh")) {
           
            getEmails(out);
            return;
        }
        
        if (action.equals("getList")) {

            try{

               resp.setContentType("application/vnd.ms-excel");    // response in Excel Format

            }
            catch (Exception exc) {
            }

            getList(user, req, out);     // output the requested list
            return;
        }

    }
    
    
    private void listEmails(String user, PrintWriter out){
       
        out.println("<HTML><HEAD><TITLE>Get Pro Emails</TITLE></HEAD><CENTER><BR>");

        out.println("<H3>Pro Email Addresses</H3>");
        out.println("<P>Below are email addresses found for the pro's at all active clubs.  Each box contains up to 90 email addresses.</P><BR>");
        
        getEmails(out);                                 // print emails on page in groups of 90

         if (user.equals( support )) {                                              // if support (do not allow for sales)
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
         } else {
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
         }        
        out.println("</CENTER></BODY></HTML>");

        out.close();
    }
       

    
    // *********************************************************
    //  Download an excel file with all club contacts that have
    //  requested newsletters.
    // *********************************************************

    private void getList(String user, HttpServletRequest req, PrintWriter out) {


        Connection con1 = null;
        Connection con2 = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String email = "";
        String email2 = "";
        String name = "";
        String title = "";
        String club = "";
        String fullname = "";
        int news1 = 0;
        int news2 = 0;
        boolean error = false;
        
        try {
            
            con1 = Connect.getCon(rev);
            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname, fullname FROM clubs WHERE inactive='0' ORDER BY fullname");
            
            out.println("<html><head><title>Get Staff Emails</title></head>");
            out.println("<BODY bgcolor=white>");
            out.println("<table>");

            //out.println("<tr><td><b>Full Name</b></td><td><b>Title</b></td><td><b>Club Name</b></td><td><b>email</b></td></tr>");
            
            while(rs1.next() && error == false) {
                
                club = rs1.getString(1);                // get a club name
                fullname = rs1.getString(2);                // get club's full name
                
                if (!club.startsWith( "demo" ) && !club.startsWith( "notify" ) && !club.equals( "mfirst" )) {         // if NOT a demo site
                
                   try{

                       con2 = Connect.getCon(club);            // get a connection to this club's db
                       stmt2 = con2.createStatement();         // create a statement

                       rs2 = stmt2.executeQuery("SELECT * FROM staff_list "
                                              + "WHERE (address1 <> '' AND receive_news1 > 0) OR (address2 <> '' AND receive_news2 > 0) "
                                              + "ORDER BY title"); 
                       
                       while (rs2.next()) {

                           name = rs2.getString("name");        
                           title = rs2.getString("title");        
                           email = rs2.getString("address1");           // get staff info
                           email2 = rs2.getString("address2");        
                           news1 = rs2.getInt("receive_news1");        
                           news2 = rs2.getInt("receive_news2");        
                           
                           if (!email.equals("") && email != null) email = email.trim();
                           if (!email2.equals("") && email2 != null) email2 = email2.trim();
                           
                           if (!email.equals("") && email != null && news1 > 0) {
                          
                               out.println("<tr><td>" +name+ "</td><td>" +title+ "</td><td>" +fullname+ "</td><td>" +email+ "</td></tr>");
                           }
                           
                           if (!email2.equals("") && email2 != null && news2 > 0) {
                          
                               out.println("<tr><td>" +name+ "</td><td>" +title+ "</td><td>" +fullname+ "</td><td>" +email2+ "</td></tr>");
                           }
                       }
                                              
                   } 
                   catch(Exception exc1){
                       out.println("Error processing staff_list for club " +club+ ";" + exc1.getMessage());
                       error = true;
                   }
                }
                
                stmt2.close();
                con2.close();
                
            }           // do each club
            
            out.println("</table></body></html>");
            
            stmt1.close();
            con1.close();
            
        }
        catch (Exception exc){
            out.println("Error processing request for " +club+ ";" + exc.getMessage());
        }
        
        out.close();
    
    }       // end of getList
       

    
    private void getEmails(PrintWriter out){

        Connection con1 = null;
        Connection con2 = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String club = "";
        String email = "";
        int count = 0;
        
        try {
            
            con1 = Connect.getCon(rev);
            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname FROM clubs WHERE inactive='0' ORDER BY fullname");
            
            out.println("<TEXTAREA ROWS='6' COLS='80'>");
            
            while(rs1.next()) {
                
                club = rs1.getString(1);                // get a club name
                
                if (!club.startsWith( "demo" ) && !club.startsWith( "notify" ) && !club.equals( "mfirst" )) {         // if NOT a demo site
                
                   try{

                       con2 = Connect.getCon(club);            // get a connection to this club's db
                       stmt2 = con2.createStatement();         // create a statement

                       rs2 = stmt2.executeQuery("SELECT address1 FROM staff_list WHERE address1 <> '' AND receive_news1 > 0;"); 
                       
                       while (rs2.next()) {

                           email = rs2.getString(1);           // get the club email
                           if (!email.equals("") && email != null) email = email.trim();

                           if (count >= 90){                  // if count >= 90, insert linebreaks and reset count
                               out.println("</TEXTAREA><BR/><BR/><TEXTAREA ROWS='6' COLS='80'>");
                               count = 0;
                           }

                           if (!email.equals("") && email != null) out.print(email + SEPARATOR + " ");            // print out the email with trailing comma
                           count++;

                       }
                       
                       rs2 = stmt2.executeQuery("SELECT address2 FROM staff_list WHERE address2 <> '' AND receive_news2 > 0;"); 
                       
                       while (rs2.next()) {

                           email = rs2.getString(1);           // get the club email
                           if (!email.equals("") && email != null) email = email.trim();

                           if (count >= 90){                  // if count >= 90, insert linebreaks and reset count
                               out.println("</TEXTAREA><BR/><BR/><TEXTAREA ROWS='6' COLS='80'>");
                               count = 0;
                           }

                           if (!email.equals("") && email != null) out.print(email + SEPARATOR + " ");            // print out the email with trailing comma
                           count++;

                       }
                       
                   } 
                   catch(Exception exc1){
                       //out.println("Error processing request 1;" + exc1.getMessage());
                   }
                }
                
                stmt2.close();
                con2.close();
                
            }           // do each club
            
            out.println("</TEXTAREA>");
            
            stmt1.close();
            con1.close();
            
        }
        catch (Exception exc){
            out.println("Error processing request for " +club+ ";" + exc.getMessage());
        }
    } 
    
    
    private void getEmailNames(PrintWriter out){

        Connection con1 = null;
        Connection con2 = null;
        Statement stmt1 = null;
        Statement stmt2 = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        String club = "";
        String fullname = "";
        String proname = "";
        String email = "";
        String email2 = "";
        String title = "";
        int rec_backups1 = 0;
        int rec_news1 = 0;
        int cc_emails1 = 0;
        int rec_backups2 = 0;
        int rec_news2 = 0;
        int cc_emails2 = 0;
        int tee_time_list = 0;
        
        try {
            
            con1 = Connect.getCon(rev);
            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname, fullname FROM clubs WHERE inactive='0'");
            
            while(rs1.next()) {
                
                club = rs1.getString(1);                // get a club name
                fullname = rs1.getString(2);               
                
                if (!club.startsWith( "demo" ) && !club.startsWith( "notify" ) && !club.equals( "mfirst" )) {         // if NOT a demo site
                
                   try{

                       con2 = Connect.getCon(club);            // get a connection to this club's db
                       stmt2 = con2.createStatement();         // create a statement

                       rs2 = stmt2.executeQuery("SELECT * FROM staff_list WHERE name <> '' OR address1 <> '' OR address2 <> '';"); 
                                           
                       while (rs2.next()) {

                           proname = rs2.getString("name");             // get the pros name
                           email = rs2.getString("address1");           // get the pro email
                           email2 = rs2.getString("address2");         
                           title = rs2.getString("title");         
                           rec_backups1 = rs2.getInt("receive_backups1");         
                           rec_news1 = rs2.getInt("receive_news1");         
                           cc_emails1 = rs2.getInt("cc_on_emails1");         
                           rec_backups2 = rs2.getInt("receive_backups2");         
                           rec_news2 = rs2.getInt("receive_news2");         
                           cc_emails2 = rs2.getInt("cc_on_emails2");         
                           tee_time_list = rs2.getInt("tee_time_list");         
                           
                           email = email.trim();
                           email2 = email2.trim();

                           out.println("<tr><td align=\"left\">");
                           out.println("<font size=\"2\">");
                           out.println(fullname+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(proname+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(title+ "</font></td>");                  
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(email+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(rec_backups1+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(rec_news1+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(cc_emails1+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(email2+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(rec_backups2+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(rec_news2+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(cc_emails2+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(tee_time_list+ "</font></td>");
                           out.println("</tr>");
                       }

                       stmt2.close();
                       con2.close();
                       
                   } 
                   catch(Exception exc1){
                       out.println("Error processing request 1;" + exc1.getMessage());
                   }
                }
            }      // do each club           
           
            stmt1.close();
            con1.close();
           
        }
        catch (Exception exc){
            out.println("Error processing request;" + exc.getMessage());
        }
        
    } 
    
    
    // *********************************************************
    // Output a list of email addresses with the club name and pro name
    // *********************************************************

    private void getNames(PrintWriter out) {


         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<table border=\"1\" cellpadding=\"5\">");
         out.println("<tr><td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println("<b>Club Name</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Pro Name</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Title</b></font></td>");                    // filler to match format of merged report (from salesforce)
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Email Addr 1</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Backups</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>News</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>CC</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Email Addr 2</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Backups</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>News</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>CC</b></font></td>");
         out.println("<td align=\"center\"><font size=\"2\">");
         out.println("<b>Tee Time List</b></font></td>");
         out.println("</tr>");
        
         getEmailNames(out);                       // print emails on page 

         out.close();
    } 
    
    
    // *********************************************************
    //  Remove duplicate email addresses from the file provided.
    //
    //    Input File:  name = email_list.csv (in club folder)
    //                 contents = single column of email addresses in alphabetical order!!!!
    //
    //    Output File:  name = emails.csv (in club folder)
    //                  contents = single column of email addresses with dups removed
    //
    // *********************************************************

    private void remDups(String user, String club, PrintWriter out) {


      int count = 0;
     
      String line1 = "";
      String line2 = "";
      
      boolean done = false;
       
      //
      //  read in the text file - must be named 'email.csv'
      //
      FileReader fr = null;

      try {

         fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//email_list.csv");

      }
      catch (Exception e1) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
         out.println("<BR><BR>File Read Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e1.getMessage());
         if (user.equals( support )) {                                              // if support (do not allow for sales)
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
         } else {
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
         }        
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      out.println("<HTML><HEAD><TITLE>Remove Dup Emails</TITLE></HEAD><CENTER><BR>");

      out.println("<H3>Remove Duplicate Email Addresses</H3>");
      out.println("<P>The results can be found in 'emails.csv' in the club folder.</P><BR>");

      try {

         BufferedReader bfrin = new BufferedReader(fr);

         line1 = bfrin.readLine();                          // get the first email

         line1 = line1.trim();
               
         line2 = bfrin.readLine();                          // get 2nd email
               
         while (!line2.equals("") && line2 != null) {       // check all email addresses from input file
            
            line2 = line2.trim();
                  
            if (!line2.equals("") && line2 != null) {     // if still there
            
               if (!line1.equals( line2 )) {              // if they are not the same

                  buildFile(line1, club);                 // save first email, else skip it

                  count++;
               }

               line1 = line2;                          // move 2nd to 1st position and go check it

               if ((line2 = bfrin.readLine()) == null) {      // get the next email if present

                  line2 = "";
               }
            }
            
         }   // end of while

         //  save the previous email (last one in file)
         buildFile(line1, club);                                              

         count++;
                  
      }
      catch (Exception e3) {

         out.println("<BR><BR>Email File Import Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ e3.getMessage());
         if (user.equals( support )) {                                              // if support (do not allow for sales)
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
         } else {
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
         }        
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //  done
      out.println("<BR><BR>Unique Email Addresses Found = " + count);
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }        
      out.println("</CENTER></BODY></HTML>");

      out.close(); 
    }
    
    
    private void buildFile(String msg, String club) {

      try {
         //
         //  Dir path for the real server
         //
         PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//emails.csv", true));

         //
         //  Put email address in csv file
         //
         fout1.print(msg);
         fout1.println();      // output the line

         fout1.close();

      }
      catch (Exception ignore) {
      }
    
    }  // end of buildFile

    
    
    // *********************************************************
    // Illegal access by user - force user to login....
    // *********************************************************

    private void invalidUser(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
      out.println("</CENTER></BODY></HTML>");

    }

}
