/***************************************************************************************     
 *   Support_report_member_teetimes:  List the total number of member tee times and the average
 *                                    number and percentage (of all active clubs).
 *
 *
 *   called by:  support_main2.htm and sales
 *              
 *
 *   created: 2/10/2014   Bob P.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Support_report_member_teetimes extends HttpServlet {
                
 String omit = "";

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 

 //****************************************************
 // Process the initial request 
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   HttpSession sess = verifyUser(req, out);       // check for intruder

   if (sess == null) {

      return;
   }

   Connection con = null;        
   Connection con2 = null;        

   
   String club = "v5";
   String fullname = "";

   int cnum = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int tproNew = 0;
   int tproMod = 0;
   int tmemNew = 0;
   int tmemMod = 0;

   try {

      con2 = dbConn.Connect(club);          // get con to V5
   }
   catch (Exception exc) {
   }

   try{

      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
    
   }
   catch (Exception exc) {
   }

   
   //
   //  Build Mobile Login report
   //
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
   out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("#");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Club Name");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Pro New");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Pro Mod");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Mem New");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("%");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Mem Mod");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Total New");
   out.println("</font></td>");
   out.println("<td align=\"center\" bgcolor=\"#336633\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("Total Mod");
   out.println("</font></td>");
   out.println("</tr>");


   //
   //  Get each club in the system and get its parms - skip inactive clubs, demo clubs and newer clubs (refer to startdate below !!!!!!!!!!!!!!)
   //
   try {

      stmt2 = con2.createStatement();           

      rs2 = stmt2.executeQuery("SELECT clubname, fullname FROM clubs WHERE inactive = 0 AND startdate < 20120701 ORDER BY clubname");      // get club names from V5 db

      while (rs2.next()) {

         club = rs2.getString(1);             // get a club name
         fullname = rs2.getString(2);         // get the full name

         //  weed out the demo clubs, etc.

         boolean skip = false;

         if (club.startsWith("demo") || club.startsWith("mfirst") || club.startsWith("testJonas") || 
               club.startsWith("notify") || club.startsWith("test") || club.equals("admiralscove2") ) {   

            skip = true;      // skip it
         }

         if (skip == false) {

            con = dbConn.Connect(club);          // get con to this club

            stmt = con.createStatement();        // create a statement

            //
            //  Get counts from past tee times - for year specified in query
            //
            rs = stmt.executeQuery("SELECT " +
                                       "SUM(proNew) AS proNew, SUM(proMod) AS proMod, " +
                                       "SUM(memNew) AS memNew, SUM(memMod) AS memMod " +
                                   "FROM teepast2 WHERE yy = 2013 AND event = ''");

            if (rs.next()) {

               proNew = rs.getInt(1);
               proMod = rs.getInt(2);
               memNew = rs.getInt(3);
               memMod = rs.getInt(4);
            }

            stmt.close();
            
            if (memNew > 2000) {           // skip newer clubs !!!

               tproNew += proNew;          // keep total count of all clubs
               tproMod += proMod;        
               tmemNew += memNew;
               tmemMod += memMod;

               cnum++;                    // club number (counter)

               out.println("<tr><td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(cnum);
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(fullname);
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(proNew+ " (" + ((proNew * 100)/(proNew + memNew)) + "%)");
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(proMod);
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(memNew+ " (" + ((memNew * 100)/(proNew + memNew)) + "%)");
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(((memNew * 100)/(proNew + memNew)));
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(memMod);
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(proNew + memNew);
               out.println("</font></td>");
               out.println("<td align=\"center\">");
               out.println("<font size=\"2\">");
               out.println(proMod + memMod);
               out.println("</font></td>");
               out.println("</tr>");
            }

            proNew = 0;           // init for next club
            proMod = 0;
            memNew = 0;
            memMod = 0;

            con.close();                           // close the connection to the club db
         }
      }                   // do all clubs

      stmt2.close();
      con2.close();

      out.println("<tr><td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\">");
      out.println("<b>Grand Totals:</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tproNew+ " (" + ((tproNew * 100)/(tproNew + tmemNew)) + "%)");
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tproMod);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tmemNew+ " (" + ((tmemNew * 100)/(tproNew + tmemNew)) + "%)");
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println("N/A");
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tmemMod);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tproNew + tmemNew);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"3\"><b>");
      out.println(tproMod + tmemMod);
      out.println("</b></font></td>");
      out.println("</tr>");
      out.println("<tr><td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("#");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Club Name");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Pro New");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Pro Mod");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Mem New");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("%");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Mem Mod");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Total New");
      out.println("</font></td>");
      out.println("<td align=\"center\" bgcolor=\"#336633\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\">");
      out.println("Total Mod");
      out.println("</font></td>");
      out.println("</tr>");
      out.println("</table>");
      
   }
   catch (Exception exc) {

      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Error processing the clubs.");
      out.println("<BR><BR>Exception: " + exc.getMessage());
      out.println("<BR><BR><form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
      out.println("</form>");
      return;

   }   // end of member name list

      
 }  // end of doGet
 
 

 // *********************************************************
 // Check for illegal access by user
 // *********************************************************

 private HttpSession verifyUser(HttpServletRequest verreq, PrintWriter out) {

   HttpSession session = null;

   String support = "support";
   String sales = "sales";

   //
   // Make sure user didn't enter illegally
   //
   session = verreq.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject

   } else {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.equalsIgnoreCase( support ) && !user.startsWith( sales )) {

         invalidUser(out);            // Intruder - reject
         session = null;
      }
   }
   return session;
 }
    
 
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER>");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>");
   //out.println("<a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
   out.println("</form>");
   out.println("</CENTER></BODY></HTML>");

 }

 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR>Please try again later.");
   out.println("<BR><BR>If problem persists, contact customer support.");
   // out.println("<BR><BR><a href=\"/" +rev+ "/support_main2.htm\">Return</a>");
   out.println("<BR><BR><form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#CC9966\">");
   out.println("</form>");
   out.println("</CENTER></BODY></HTML>");

 }

}
