/***************************************************************************************
 *   Proshop_announcements: This servlet will post any announcements that we want the proshop users to see.  
 *
 *
 *   called by:  Proshop_maintop - mail box image where club logo is located (if db entry found for current date)
 *
 * 
 * 
 *   To post a message - Create an HTML file and add an entry in the 'v5.announcements' db table (add dates, activity indicators, and file name)
 * 
 * 
 *
 *   created: 6/05/2014   Bob P.
 *
 *   last updated:            ******* keep this accurate *******
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Proshop_announcements extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process the initial request from Proshop_main
 //*****************************************************
 //
 @SuppressWarnings("deprecation")
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

    
   String club = (String)session.getAttribute("club");            // get club name
   String user = (String)session.getAttribute("user");            // get username
   String templott = (String)session.getAttribute("lottery");     // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   
   //
   //   Get the message to display (file name from the announcements db table)
   //
   String fileName = (req.getParameter("fileName") == null) ? "" : req.getParameter("fileName");
   
   if (!fileName.equals("")) {
   
       //
       //  Display the content of the file
       //
       showFile(req, out, fileName, lottery);
   
       //
       //  ********  Log this announcement view for this club ************
       //
       logView(club, user, fileName);
       
   } else {
       
       //
       //  No File specified - get list of announcements for user to select
       //
       Connection con = null;
       ResultSet rs = null;
       PreparedStatement stmt = null;
       
       String title = "";
       int edate = 0;

       //  get today's date

       Calendar cal = new GregorianCalendar();         
       int year = cal.get(Calendar.YEAR);
       int month = cal.get(Calendar.MONTH) +1;
       int day = cal.get(Calendar.DAY_OF_MONTH);

       int date = (year * 10000) + (month * 100) + day;   // today's date  

       
       out.println("<html><head>");
       out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
       out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
       out.println("<title> \"ForeTees Proshop Announcements Page\"</title>");
       out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
       out.println("</head>");
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\"><CENTER>");
       out.println("<br><br><h2>Past Announcements</h2>");       
       out.println("<p align=center>Select the announcement you would like to view:</p><BR>");       
   

       SystemUtils.getProshopSubMenu(req, out, lottery);
   
       try {

            con = dbConn.Connect(rev);       // get a connection for this club
            
            //
            //  get all announcements
            //
            stmt = con.prepareStatement ("SELECT announce_id, edate, fileName, title FROM announcements "
                                       + "WHERE sdate < ? ORDER BY edate DESC");
            
            stmt.clearParameters();        // clear the parms
            stmt.setInt(1, date);
            rs = stmt.executeQuery();

            while (rs.next()) {

                edate = rs.getInt("edate");
                fileName = rs.getString("fileName");
                title = rs.getString("title");
                
                int yy = edate / 10000;
                int mm = (edate - (yy * 10000)) / 100;
                int dd = edate - ((yy * 10000) + (mm * 100));
                
                //
                //  List them so user can select one - links return at doGet above and display like those from Mailbox
                //
                out.println("<p align=center><a href=\"Proshop_announcements?fileName=" +fileName+ "\">" +title+ "</a>&nbsp;&nbsp;&nbsp; Last Posted: " +mm+ "/" +dd+ "/" +yy+ "</p>");
            }

       } catch (Exception exc) {

            SystemUtils.logError("Error in Proshop_announcements - Unable to connect to db. Error: " + exc.toString());

       } finally {

            try { rs.close(); }
            catch (SQLException ignored) {}

            try { stmt.close(); }
            catch (SQLException ignored) {}

            try { con.close(); }
            catch (SQLException ignored) {}
       }
       
       out.println("<BR><p align=center><a href=\"Proshop_announce\">Home</a></p>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
   
   }    
   
   
 }  // end of doGet
    
   
   
   
 private void showFile(HttpServletRequest req, PrintWriter out, String fileName, int lottery) {
   
   //
   //  Call is to display the announcement
   //
   out.println("<html><head>");
   out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
   out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
   out.println("<title> \"ForeTees Proshop Announcements Page\"</title>");
   out.println("<script language=\"JavaScript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
   out.println("</head>");
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

   SystemUtils.getProshopSubMenu(req, out, lottery);
   
   
   if (!fileName.equals("")) {         // if file provided (i.e.  announce1.html)
  
        File f;
        FileReader fr;
        BufferedReader br;
        String tmp = "";
        String path = "";

        try {
            path = req.getRealPath("");
            tmp = "/announcements/" +fileName;               //  /v5/announcements/xxxx.html
            f = new File(path + tmp);
            fr = new FileReader(f);
            br = new BufferedReader(fr);
            if (!f.isFile()) {
                // do nothing
            }
        }
        catch (FileNotFoundException e) {
            out.println("<br><br><p align=center>Sorry, Missing Announcement File.");
            out.println("<BR><BR><a href=\"Proshop_announce\">Home</a></p>");
            out.println("</BODY></HTML>");
            out.close();
            return;
        }
        catch (SecurityException se) {
            out.println("<br><br><p align=center>Access Denied.");
            out.println("<BR><BR><a href=\"Proshop_announce\">Home</a></p>");
            out.println("</BODY></HTML>");
            out.close();
            return;
        }
        
        try {

            while( (tmp = br.readLine()) != null ) {

                out.println(tmp);
            }

            br.close();
            
        }
        catch (Exception exc) {
            out.println("<br><br><p align=center>Unexpected File Error.");
            out.println("<BR><BR><a href=\"Proshop_announce\">Home</a></p>");
            out.println("</BODY></HTML>");
            out.close();
            return;
        }
                    
   } else {
       
       out.println("<br><br><p align=center>Sorry, Missing Announcement File.");       
       out.println("<BR><BR><a href=\"Proshop_announce\">Home</a></p>");
   }
      
   out.println("</BODY></HTML>");
   out.close();
   
 }  // end of showFile
 
 
 //  logView - log this view
 
 private void logView(String club, String user, String fileName) {
 

    Connection con = null;
    ResultSet rs = null;
    PreparedStatement stmt = null;
    
    int id = 0;
    
     
    try {

        con = dbConn.Connect(rev);       // get a connection for this club
        
        //
        // first get the id for the fileName 
        //
        stmt = con.prepareStatement ("SELECT announce_id FROM announcements WHERE fileName = ?");
        stmt.clearParameters();        // clear the parms
        stmt.setString(1, fileName);
        rs = stmt.executeQuery();
        
        if (rs.next()) {

           id = rs.getInt("announce_id");
        }
        

        stmt = con.prepareStatement ("INSERT INTO announce_views (club, user, announce_id, date_time) VALUES (?, ?, ?, now())");
        stmt.clearParameters();        // clear the parms
        stmt.setString(1, club);
        stmt.setString(2, user);
        stmt.setInt(3, id);
        stmt.executeUpdate();

    } catch (Exception exc) {
        
        SystemUtils.logError("Error in Proshop_announcements - Trying to log the view (club=" +club+ ", user=" +user+ "). Error: " + exc.toString());

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

        try { con.close(); }
        catch (SQLException ignored) {}
    }

 }    // end of logView

}
