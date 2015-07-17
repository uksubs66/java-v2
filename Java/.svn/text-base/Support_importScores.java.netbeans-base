/* *************************************************************************************
 *   Support_importScores:  Imports scores from csv file to the score_postings table
 *
 *
 *   Called by:     
 *
 *
 *   Created:       05/01/08 - Brad K.
 *
 *
 *   Revisions: 
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


public class Support_importScores extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
         
         resp.setContentType("text/html");
         PrintWriter out = resp.getWriter();
         
         Connection con = null;
         
         HttpSession session = null;
    
/*
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
*/       
         //String club = (String)session.getAttribute("club");   // get club name
         String club = "marbellacc";      // TEMP ASSIGNMENT
         
         try {

            con = dbConn.Connect(club);
         }
         catch (Exception exc) {

            out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
            out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the DB.");
            out.println("<BR>Exception: "+ exc.getMessage());
            out.println("</CENTER></BODY></HTML>");
            return;
         }
        
         // run method to import scores from 'scores.csv'
         importScores(con, out, club);
         
    }
    
    // ********************************************************************
    // Import scores from 'scores.csv' file to the score_postings table
    // ********************************************************************
    private void importScores(Connection con, PrintWriter out, String club) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        boolean failed = false;
        boolean byname = false;
        FileReader fr = null;
        java.sql.Date datePlayed = null;
        String line = "";
        String hdcpNum = "";
        String date = "";
        String lname = "";
        double rating = 0.0;
        int type = 0;
        int slope = 0;
        int tee_id = 0;
        int score = 0;
        int count = 0;
        int skipped = 0;
        int succeed = 0;
        int fail = 0;
        
        
        //
        //  read in the text file - must be named 'email.csv'
        //
        try {

            fr = new FileReader("//usr//local//tomcat//webapps//" + club + "//scores.csv");

        }
        catch (Exception e1) {

            failed = true;
        }

        if (failed == true) {

            try {

                //fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\scores.csv");
                fr = new FileReader("/Applications/apache-tomcat-5.5.26/webapps/" + club + "/scores.csv");

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

            BufferedReader bfrin = new BufferedReader(fr);
            bfrin.readLine();       // eat header line
            line = new String();
            
            StringTokenizer tok = null;
            
            while ((line = bfrin.readLine()) != null) { 
                
                count++; 
                
                //  parse the line to grab needed info and discard unwanted columns
                tok = new StringTokenizer( line, "," );             // delimiters are comma
                
                if (tok.countTokens() == 19){
                    
                    hdcpNum = tok.nextToken();                      // SCGA# (hdcpNum)
                    lname = tok.nextToken();                        // last name (kept for error logging purposes)
                    date = tok.nextToken();                         // date played
                    while (tok.countTokens() > 11){
                        tok.nextToken();                            // eat 'date time created', 'facility code', 'club code', 'course name', and 'score location' tokens
                    }
                    score = Integer.parseInt(tok.nextToken());      // adjusted score
                    tok.nextToken();                                // eat 'score type' token
                    type = Integer.parseInt(tok.nextToken());       // final score type
                    while (tok.countTokens() > 5){
                        tok.nextToken();                            // eat 'differential', 'tees played', and 'rating #' tokens
                    }
                    rating = Double.parseDouble(tok.nextToken());   // course rating
                    slope = Integer.parseInt(tok.nextToken());      // course slope
                    // rest are ignored
                    
                } else {
                    count--;    // current row not readable, continue to next (don't count current row in count)
                    skipped++;
                    continue;
                }
                
                // determine tee_id based on course slope and course rating
                tee_id = Common_ghin.getTeeIdFromSlopeRating(slope, rating, con);
                
                // convert date input to proper java.sql.Date format    
                if (date.length() > 4){
                    int yearDif = date.length() - 4;
                    String yy = date.substring(0, yearDif);
                    yy = String.valueOf(Integer.parseInt(yy) + 2000);
                    String mm = date.substring(yearDif + 1, yearDif + 2);
                    String dd = date.substring(yearDif + 3, yearDif + 4);
                    datePlayed = java.sql.Date.valueOf(yy + "-" + mm + "-" + dd);
                }
                    
                // insert into database
                pstmt = con.prepareStatement(
                        "INSERT IGNORE INTO score_postings " +
                        "(hdcpNum, date, score, type, tee_id, hdcpIndex) " +
                        "VALUES(?,?,?,?,?,'0')");
                
                pstmt.clearParameters();
                pstmt.setString(1, hdcpNum);
                pstmt.setDate(2, datePlayed);
                pstmt.setInt(3, score);
                pstmt.setString(4, Common_scga.getScoreType(type));
                pstmt.setInt(5, tee_id);
                
                int res = pstmt.executeUpdate();
                
                if (res == 1){
                    succeed++;
                } else {
                    fail++;
                    logError("Insert failed for: " + lname + ", " + hdcpNum + ", " + datePlayed.toString(), "jonathanslanding");
                }
            }
            
        } catch (Exception exc) { 
        
                out.println("<HTML><HEAD><TITLE>Booo :(</TITLE></HEAD>");
                out.println("<BODY><CENTER><H3>End of while loop failure</H3>");
                out.println("<BR><BR>Loop Failed for  " + club);
                out.println("<BR><BR>Exception Received: "+ exc.getMessage());
                out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
                out.println("</CENTER></BODY></HTML>");
                return;
        }
        
        
        out.println("<HTML><HEAD><TITLE>Yay? :]</TITLE></HEAD>");
        out.println("<BODY><CENTER>");
        out.println("<p>Total of " + count + " entries parsed.");
        out.println("<br><br>" + succeed + " entries added successfully, " + fail + " entries failed, " + (skipped-1) + " entries skipped.");
        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
        out.println("</CENTER></BODY></HTML>");
    }
    
    // ********************************************************************
    // log error to 'error.txt' file
    // ********************************************************************
    private void logError(String msg, String club) {

        int fail = 0;

        try {
          //
          //  Dir path for the real server
          //
          PrintWriter fout1 = new PrintWriter(new FileWriter("/Applications/apache-tomcat-5.5.26/webapps/" + club + "/error.txt", true));

          //
          //  Put header line in text file
          //
          fout1.print(new java.util.Date() + "    " + msg);
          fout1.println();      // output the line

          fout1.close();

        }
        catch (Exception e2) { }
    }
}