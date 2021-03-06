/***************************************************************************************     
 *   Support_port_dining:  This servlet will port all members from a ForeTees club member roster
 *                         over to the dining system database.
 *
 * 
 *
 *   last updated:
 *
 *        1/13/14   Fixed address_id not being added to people record
 *       11/17/11   Updated import process to pull members with a null dining_id instead of members with a dining_id of 0.
 *        8/30/11   Updated import process to better match on the newly created record and to properly pad the user_identity field
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.sql.*;
import javax.naming.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.*;
import org.apache.commons.fileupload.servlet.*;
import org.apache.commons.io.*;
import org.apache.commons.lang.*;
import org.apache.commons.lang.text.StrTokenizer;


// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.BasicSHA256;
import com.foretees.common.parmDining;


public class Support_port_dining extends HttpServlet {
                           
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                 // init DB objects
   Statement stmt = null;
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

   int organization_id = Utilities.getOrganizationId(con);

   out.println("<HTML><HEAD><TITLE>Port ForeTees Members to Dining</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Port ForeTees Members to Dining</H3>");

   if (organization_id > 0) {
       out.println("<br><br>organization_id found for this club (" + organization_id + ").");
       out.println("<br><br>This job will port all members in this club's ForeTees database over to the dining system.");
       out.println("<br><br>Are you sure you want to continue?");
       out.println("<form method=\"POST\" action=\"Support_port_dining\">");
       out.println("<input type=\"hidden\" name=\"organization_id\" value=\"" + organization_id + "\">");
       out.println("<br><br><input type=\"submit\" name=\"port\" value=\"Port Members\">");
       out.println("</form>");
       
       
       
       out.println("<br><br>This job will import member preferences into this club's dining database.");
       out.println("<br><br>Are you sure you want to continue?");
       out.println("<form method=\"POST\" action=\"Support_port_dining\">");
       out.println("<input type=\"hidden\" name=\"organization_id\" value=\"" + organization_id + "\">");
       out.println("<br><br><input type=\"submit\" name=\"preferences\" value=\"Import Preferences\">");
       out.println("</form>");
       
       
   } else {
       out.println("<br><br>No organization_id found for this club.  Make sure club is configured for dining and club5 contains the appropriate organization_id before continuing.");
   }

   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
      
 }
 

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     
     if (req.getParameter("port") != null) {
         
         doPushForeTeesUserToDining(req, resp);
         
     } else if (req.getParameter("preferences") != null) {
         
         doPreferencesImport(req, resp);
         
     }
     
 }

 
 public void doPushForeTeesUserToDining(HttpServletRequest req, HttpServletResponse resp) {

   resp.setContentType("text/html");
   PrintWriter out = null;
   
   try {
       out = resp.getWriter();
   } catch (Exception ignore) {}

   Connection con = null;                 // init DB objects
   Connection con_d = null;
   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

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

   try {
       con_d = Connect.getDiningCon();
   } catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Dining DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   String username = "";
   String name_pre = "";
   String name_last = "";
   String name_first = "";
   String name_mi = "";
   String name_suf = "";
   String phone1 = "";
   String phone2 = "";
   String email = "";
   String birth_date = "";
   String password_salt = "";
   String password_hash = "";

   int birth = 0;
   int emailOpt = 0;
   int dining_id = 0;
   int organization_id = 0;
   int address_id = 0;
   int count = 0;
   int yy = 0;
   int mm = 0;
   int dd = 0;
   int temp = 0;

   long tempdate = 0;

   java.util.Date bday = null;
   java.sql.Date bday2 = null;

   Calendar cal = new GregorianCalendar();

   boolean emailOptBool = false;

   // Both connections prepared. Query up all players from ForeTees member2b table and loop through, adding them to the ForeTees Dining database.
   try {

       organization_id = Integer.parseInt(req.getParameter("organization_id"));

       stmt = con.createStatement();

       rs = stmt.executeQuery("SELECT * FROM member2b WHERE ISNULL(dining_id) ORDER BY name_last, name_first");

       while (rs.next()) {

           // Gather member data from member2b table
           username = rs.getString("username");
           name_pre = rs.getString("name_pre");
           name_last = rs.getString("name_last");
           name_first = rs.getString("name_first");
           name_mi = rs.getString("name_mi");
           name_suf = rs.getString("name_suf");
           phone1 = rs.getString("phone1");
           phone2 = rs.getString("phone2");
           email = rs.getString("email");
           emailOpt = rs.getInt("emailOpt");
           birth = rs.getInt("birth");

           if (birth > 0) {
               
               yy = birth / 10000;
               temp = yy * 10000;
               mm = birth - temp;
               temp = mm / 100;
               temp = temp * 100;
               dd = mm - temp;
               mm = mm / 100;

               cal.set(yy, mm - 1, dd);

               bday = cal.getTime();

               tempdate = bday.getTime();

               bday2 = new java.sql.Date(tempdate);

           } else {
               bday = null;
               bday2 = null;
           }


           // reset
           dining_id = 0;
           count = 0;
           emailOptBool = (emailOpt == 1);
           password_salt = "";
           password_hash = "";
           address_id = 0;
           
           try {
               

               // add an address table entry for this person and get its id back
               pstmt = con_d.prepareStatement("" +
                       "INSERT INTO addresses (" +
                           "created_at, updated_at, lock_version, street_address, city, state_id, zip_or_postal_code, country, imported" +
                       ") VALUES (" +
                           "now(), now(), 0, '', '', NULL, '', 'United States', FALSE" +
                       ") RETURNING id");

               pstmt.clearParameters();
               rs2 = pstmt.executeQuery();
               
               if (rs2.next()) {
                   
                   address_id = rs2.getInt(1);
                   
               }

               pstmt.close();

               
               String user_identity = parmDining.buildUserIdentity(username, name_pre, name_last, name_first, name_mi, name_suf);

               /*
               String user_identity = username + ":" + " " + name_last + ", " + name_first;

               int pos = user_identity.indexOf(":");

               for (int i=pos; i < 10; i++) {

                   user_identity = " " + user_identity;
               }
               */

               // First add this member to the "people" table
               pstmt = con_d.prepareStatement("INSERT INTO people (" +
                       "created_at, updated_at, lock_version, prefix, last_name, first_name, middle_name, suffix, occupation, home_phone, " +
                       "work_phone, work_extension, mobile_phone, email_address, birth_date, other_is_spouse, other_last_name, other_first_name, other_middle_name, " + // children,
                       "organization_id, food_preference, cocktail_preference, special_requests, auto_email_for_dining_reservations, user_identity, member, auto_email_for_event_reservations, lottery_multiplyer, address_id" +
                       ") VALUES (" +
                       "now(),now(),1,?,?,?,?,?,'',?," +
                       "?,'','',?,?,FALSE,'','',''," + // '',
                       "?,'','','',?,?,TRUE,?,0,?" +
                       ") RETURNING id");

               pstmt.clearParameters();
               pstmt.setString(1, name_pre);
               pstmt.setString(2, name_last);
               pstmt.setString(3, name_first);
               pstmt.setString(4, name_mi);
               pstmt.setString(5, name_suf);
               pstmt.setString(6, phone1);
               pstmt.setString(7, phone2);
               pstmt.setString(8, email);
               pstmt.setDate(9, bday2);
               pstmt.setInt(10, organization_id);
               pstmt.setBoolean(11, emailOptBool);
               pstmt.setString(12, user_identity);
               pstmt.setBoolean(13, emailOptBool);
               pstmt.setInt(14, address_id);

               //count = pstmt.executeUpdate();
               
               rs2 = pstmt.executeQuery();
               
               if (rs2.next()) {
                   
                   dining_id = rs2.getInt(1);
                   
               }

               pstmt.close();
               
/*
               ResultSet rsi = pstmt.getGeneratedKeys();
               if (rsi != null && rsi.next()) dining_id = rsi.getInt(1);
               pstmt.close();
*/
/*
               // look up the dining id (use the user_identity & org_id since they should always be unique)
               pstmt = con_d.prepareStatement("SELECT id FROM people WHERE organization_id = ? AND user_identity = ?");
               pstmt.clearParameters();
               pstmt.setInt(1, organization_id);
               pstmt.setString(2, user_identity);

               rs2 = pstmt.executeQuery();

               if (rs2.next()) {

                   dining_id = rs2.getInt("id");

               }
*/

               // if we successfully added the member and got their new dining_id (people_id) then add them to the users table too
               if (dining_id != 0) { // count > 0 &&

                   password_salt = BasicSHA256.getSalt(6);
                   password_hash = BasicSHA256.SHA256(name_last + password_salt);

                   // Now add this member to the "users" table
                   pstmt = con_d.prepareStatement("" +
                           "INSERT INTO users (" +
                               "created_at, updated_at, lock_version, username, password_salt, password_hash, role_id, person_id, login_allowed" +
                           ") VALUES (" +
                               "now(), now(), 0, ?, ?, ?, 6, ?, TRUE" +
                           ")");

                   pstmt.clearParameters();
                   pstmt.setString(1, username);
                   pstmt.setString(2, password_salt);
                   pstmt.setString(3, password_hash);
                   pstmt.setInt(4, dining_id);

                   count = pstmt.executeUpdate();

                   pstmt.close();

                   if (count == 0) out.println("<br><br>Failed to insert user record for people_id " + dining_id + ", " + name_last + ", " + name_first);

               } else {
                   out.println("<br>Not adding user record for people_id " + dining_id + ", " + name_last + ", " + name_first + ", count=" + count);
               }

           } catch (Exception e2) {
               out.println("<br><br>Error inserting dining user record for id " + dining_id + ", " + name_last + ", " + name_first + ": " + e2.getMessage());
           } finally {
               Connect.close(rs2, pstmt);
           }
/*
           // If insert was successful, get dining_id from record and populate it into member2b record
           if (dining_id == 0 && count > 0) {

               try {

                   pstmt = con_d.prepareStatement("SELECT id FROM people WHERE organization_id = ? AND last_name = ? AND first_name = ? AND middle_name = ? AND email_address = ? AND home_phone = ? AND work_phone = ?");
                   pstmt.clearParameters();
                   pstmt.setInt(1, organization_id);
                   pstmt.setString(2, name_last);
                   pstmt.setString(3, name_first);
                   pstmt.setString(4, name_mi);
                   pstmt.setString(5, email);
                   pstmt.setString(6, phone1);
                   pstmt.setString(7, phone2);

                   rs2 = pstmt.executeQuery();

                   if (rs2.next()) {
                       dining_id = rs2.getInt("id");

                   }

               } catch (Exception e2) {
                   out.println("<br><br>Error getting dining_id: " + e2.getMessage());
               }
           }
*/
           if (dining_id != 0) {

               try {
                   pstmt2 = con.prepareStatement("UPDATE member2b SET dining_id = ? WHERE username = ?");
                   pstmt2.clearParameters();
                   pstmt2.setInt(1, dining_id);
                   pstmt2.setString(2, username);

                   count = pstmt2.executeUpdate();

                   //pstmt2.close();

               } catch (Exception e3) {
                   out.println("<br><br>Error updating member2b dining_id: " + e3.getMessage());
               } finally {
                   Connect.close(pstmt2);
               }

           }
       }

       //stmt.close();
       
   } catch (Exception e1) {
       out.println("<br><br>Error gathering all members from member2b: " + e1.getMessage());
   } finally {
       Connect.close(rs,stmt,con);
   }

   out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Import Complete</H3>");
   out.println("<BR><BR>");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
      
 }
        
 
 private void doPreferencesImport(HttpServletRequest req, HttpServletResponse resp) {
    
   
   resp.setContentType("text/html");
   PrintWriter out = null;
   
   try {
       out = resp.getWriter();
   } catch (Exception ignore) {}

   Connection con = null;                 // init DB objects
   Connection con_d = null;
   Statement stmt = null;
   PreparedStatement pstmt = null;
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

   try {
       con_d = Connect.getDiningCon();
   } catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Dining DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


    //
    //  read in the text file - must be named 'roster.csv'
    //
    FileReader fr = null;
    int count = 0;
    int count2 = 0;
    int tokcount = 0;
    int dining_id = 0;
    String line = "";
    String id = "";
    String fname = "";
    String lname = "";
    String alergy = "";
    String drink = "";
    String food = "";
    String health = "";
    String service = "";
    String username = "";
    String sql = "";
    String spec_req = "";

    try {

        int organization_id = Integer.parseInt(req.getParameter("organization_id"));

        fr = new FileReader("//srv//webapps//" +club+ "//preferences.csv");

        BufferedReader bfrin = new BufferedReader(fr);
        line = new String();

        // loop over each line
        while ((line = bfrin.readLine()) != null) {                     // get one line of text

            count++;                                                    // keep track of line #
            dining_id = 0;                                              // reset
            sql = "";
            spec_req = "";
            
            //String[] parts = StringUtils.splitPreserveAllTokens(line, ",");
            //String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            //line = line.replace("\"", "");                              // remove any double quotes
            
            StrTokenizer tokenizer = new StrTokenizer(line, ',', '"');
            tokenizer.setEmptyTokenAsNull(true);
            tokenizer.setIgnoreEmptyTokens(false);
            
            String[] parts = new String[tokenizer.size()];
            for (int i=0; i < 5; i++) {
              parts[i] = tokenizer.nextToken();
            }
            
            tokcount = parts.length;
            
            id = parts[0];
            lname = parts[1];
            fname = parts[2];
            alergy = parts[3];
            drink = parts[4];
            //food = parts[5];
            //health = parts[6];
            //service = parts[7];
            
//            if (id.endsWith("A")) {
                id = id.substring(0, id.length()-1);
                id =id+ "-1";
//            }
            // sanity
            if (alergy == null) alergy = "";
            if (food == null) food = "";
            if (drink == null) drink = "";
            if (alergy.equals("?")) alergy = "";
            if (food.equals("?")) food = "";
            if (drink.equals("?")) drink = "";
            if (health == null) health = "";
            if (service == null) service = "";
            
            
            // combine data fields
            if (!alergy.isEmpty()) {
                spec_req = "Alergy: " + alergy + ";  ";
            }
//            if (!health.isEmpty()) {
//                spec_req += "Health: " + health + ";  ";
//            }
//            if (!service.isEmpty()) {
//                spec_req += service;
//            }

            
            // scrub the id to match it to our username
            // first strip off all leading zeros
            while (id.startsWith("0")) {

                id = id.substring(1);
            }
            // next remove any dashes
            //id = id.replace("-", "");

            // try and find this member in ForeTees and make sure they have a dining record
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT dining_id FROM member2b WHERE username = \"" + id + "\" AND ISNULL(dining_id) = false");

            if (rs.next()) dining_id = rs.getInt(1);
            
            rs.close();
            stmt.close();

            // now update if we found the member
            if (dining_id != 0) {

                // update the members 'people' record in dining
                sql = "UPDATE people SET " +
                           "food_preference = \"" + food.replace("\"", "") + "\", cocktail_preference = \"" + drink.replace("\"", "") + "\", special_requests = \"" + service.replace("\"", "") + "\", updated_at = NOW() " +
                       "WHERE organization_id = ? AND id = ?";
                
                pstmt = con_d.prepareStatement("" + 
                       "UPDATE people SET " +
                           "food_preference = ?, cocktail_preference = ?, special_requests = ?, updated_at = NOW() " +
                       "WHERE organization_id = ? AND id = ?");

                pstmt.clearParameters();
                pstmt.setString(1, food.replace("\"", ""));
                pstmt.setString(2, drink.replace("\"", ""));
                pstmt.setString(3, spec_req.replace("\"", ""));

                pstmt.setInt(4, organization_id);   // sanity
                pstmt.setInt(5, dining_id);

                count2 = pstmt.executeUpdate();

                pstmt.close();

            } else {
                
                // output something indicating a problem with this record (user not found)
                out.println("<br>Username '" + id + "' not found from line #" + count + ": " + line);
                
            }
         
      } // end of while

    } catch (Exception exc) {

        out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
        out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
        out.println("<BR><BR>DB Add or Update Failed for  " + club);
        out.println("<BR><BR>Exception Received on Line Number " + count + ", Tok Count = " + tokcount + ", dining_id = " +dining_id+ ", username = " + id + " <br>line=" + line + " <br>sql=" + sql + " <br>Error = "+ exc.toString()+ ", <br><br>strace = "+ Utilities.getStackTraceAsString(exc));
        out.println("<BR><BR> <A HREF=\"/" +rev+ "/servlet/Support_main\">Return</A>");
        out.println("</CENTER></BODY></HTML>");
        return;

    } finally {
        
        Connect.close(rs, pstmt, con);
        Connect.close(con_d);
    }
   
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

}
