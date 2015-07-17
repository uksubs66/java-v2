/***************************************************************************************
 *   Support_getProEmails: Displays a list of emails for all active clubs
 * 
 * 
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


        out.println("<HTML><HEAD><TITLE>Get Pro Emails</TITLE></HEAD><CENTER><BR>");

        out.println("<H3>Pro Email Addresses</H3>");
        out.println("<P>Below are email addresses found for the pro's at all active clubs.  Each box contains up to 90 email addresses.</P><BR>");
        
        getEmails(out);                                 // print emails on page in groups of 90

         if (user.equals( support )) {                                              // if support (do not allow for sales)
            out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
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
        
        String action = "";
        if (req.getParameter("todo") != null) action = req.getParameter("todo");

        if (action.equals("refresh")) {
            getEmails(out);
            return;
        }
        
    }
    
    
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
            
            con1 = dbConn.Connect(rev);
            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname FROM clubs WHERE inactive='0'");
            
            out.println("<TEXTAREA ROWS='6' COLS='80'>");
            
            while(rs1.next()) {
                
                club = rs1.getString(1);                // get a club name
                
                if (!club.startsWith( "demo" ) && !club.startsWith( "notify" ) && !club.equals( "mfirst" )) {         // if NOT a demo site
                
                   try{

                       con2 = dbConn.Connect(club);            // get a connection to this club's db
                       stmt2 = con2.createStatement();         // create a statement

                       rs2 = stmt2.executeQuery("SELECT email FROM club5 WHERE email <> '';"); 
                       
                       if (rs2.next()) {

                           email = rs2.getString(1);           // get the club email
                           email = email.trim();

                           if (count >= 90){                  // if count >= 90, insert linebreaks and reset count
                               out.println("</TEXTAREA><BR/><BR/><TEXTAREA ROWS='6' COLS='80'>");
                               count = 0;
                           }

                           if (!email.equals("")) out.print(email + SEPARATOR + " ");            // print out the email with trailing comma
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
            out.println("Error processing request;" + exc.getMessage());
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
        
        try {
            
            con1 = dbConn.Connect(rev);
            stmt1 = con1.createStatement();
            rs1 = stmt1.executeQuery("SELECT clubname, fullname FROM clubs WHERE inactive='0'");
            
            while(rs1.next()) {
                
                club = rs1.getString(1);                // get a club name
                fullname = rs1.getString(2);               
                
                if (!club.startsWith( "demo" ) && !club.startsWith( "notify" ) && !club.equals( "mfirst" )) {         // if NOT a demo site
                
                   try{

                       con2 = dbConn.Connect(club);            // get a connection to this club's db
                       stmt2 = con2.createStatement();         // create a statement

                       rs2 = stmt2.executeQuery("SELECT contact, email FROM club5 WHERE email <> '';"); 
                       
                       if (rs2.next()) {

                           proname = rs2.getString(1);         // get the pros name
                           email = rs2.getString(2);           // get the pro email
                           email = email.trim();

                           out.println("<tr><td align=\"left\">");
                           out.println("<font size=\"2\">");
                           out.println(fullname+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(proname+ "</font></td>");
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println("&nbsp;</font></td>");                    // filler to match format of merged report (from salesforce)
                           out.println("<td align=\"center\"><font size=\"2\">");
                           out.println(email+ "</font></td>");
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
         out.println("<b>Email Addr</b></font></td>");
         out.println("</tr>");
        
         getEmailNames(out);                       // print emails on page 

         out.close();
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

}
