/***************************************************************************************
 *   Support_import:  This class will import files for the club and update the member roster.
 *
 *
 *   called by:  support_main3.htm
 *
 *   File Names to Import:    email.csv
 *                            bag.csv
 *                            birth.csv
 *                            hndcp.csv          For current handicaps
 *                            ghinnum.csv        For GHIN Numbers
 *                            ghindata.csv       For GHIN Member Data (for GHIN interface)
 *                            webid.csv
 *                            mode.csv           For Modes of Trans Defaults per member
 *                            weight.csv         For adding lottery weight
 *                            posid.csv          For adding POS Ids
 *
 *
 *  !!!! NOTE: Change dbConn if running from local machine !!!!!!!!!!!
 *
 *
 *
 *   created: 7/07/2005   Bob P.
 *
 *   last updated:
 *                  02-06-14  Added portGolfNet import for importing GolfNet numbers.
 *                  02-04-14  Updated logError so that it allows a specific filename to be passed, so not every import is outputting "error.txt". Default is still "error.txt" if not specified otherwise.
 *                  09-01-11  portGHINdata - updated how hdcp club/assoc values are looked up.
 *                  05-02-11  Added portCDGA import for CDGA handicap numbers.
 *                  04-14-10  Updated a couple portWebid error messages to include webids
 *                  03-08-10  Added middle initial filter to GHINdata import
 *                  04-14-09  Added portDemoClubs import for demo club spreadsheets
 *                  08-01-08  Changed portGHIN error reporting to include GHIN #
 *                  07-10-08  Changed portGHINdata error reporting to include GHIN #
 *                  07-01-08  portWeight - Added method to import lottery weights.
 *                  04-21-08  portGHINdata - Added case for handling lname fname no comma (lnamenoc)
 *                  02-25-08  portGHINdata - if incoming ghin# is 6 chars add a leading zero
 *                  07-14-07  Added username format to portGHINdata.
 *                  07-07-07  Added processing to check for single name field to portWebid.
 *                  03-05-07  Added 'by name' processing to portWebid.
 *                  02-06-07  Updated portWebid to check for '?' in webid and mem_id fields.
 *                            Also, changed file names from *.txt to *.csv to make it quicker to prepare.
 *                  08-09-06  Added custom for fourbridges to portWebid
 *                  08-09-06  Added new byname functionality to portWebid
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.net.URL;
import javax.xml.ws.Holder;
import javax.xml.namespace.QName;


// foretees imports
import com.foretees.common.FeedBack;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

import org.ibsservices.*; // IBS


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

   if (!SystemUtils.verifySupport(user) && !user.startsWith( "sales" )) {

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

      portBag(req, out, con, club);                 // import bag room numbers
      return;
   }

   if (req.getParameter("birth") != null) {

      portBirth(req, out, con, club);               // import birth dates
      return;
   }

   if (req.getParameter("hndcp") != null) {

      portHndcp(req, out, con, club);               // update handicaps
      return;
   }

   if (req.getParameter("ghin") != null) {

      portGHIN(req, out, con, club);                // update GHIN Numbers
      return;
   }

   if (req.getParameter("ghindata") != null) {

      portGHINdata(req, out, con, club);            // update GHIN Numbers
      return;
   }

   if (req.getParameter("golfnet") != null) {

      portGolfNet(req, out, con, club);            // update GHIN Numbers
      return;
   }

   if (req.getParameter("cdga") != null) {

      portCDGA(req, out, con, club);
      return;
   }

   if (req.getParameter("webid") != null) {

      portWebid(req, out, con, club);               // update handicaps
      return;
   }

   if (req.getParameter("flexid") != null) {

      portFlexid(req, out, con, club);               // update handicaps
      return;
   }

   if (req.getParameter("mode") != null) {

      portMode(req, out, con, club);                // update modes of trans defaults
      return;
   }

   if (req.getParameter("weight") != null) {

      portWeight(req, out, con, club);              // update modes of trans defaults
      return;
   }

   if (req.getParameter("democlubs") != null) {     // import demo equipment data

      portDemoClubs(req, out, con, club);
      return;
   }

   if (req.getParameter("ibs-pos") != null) {       // import pos ids from ibs web service

      portIbsPos(req, out, con, club);
      out.println("<p>Done...</p>");
      return;
   }
   
   if (req.getParameter("posid") != null) {

      portPOSID(req, out, con, club);                // update GHIN Numbers
      return;
   }
   
   if (req.getParameter("pgavillage") != null) {

      portPGAVillage(req, out, con, club);                // import PGA Village custom Member Course listings
      return;
   }

 }


 private void portIbsPos(HttpServletRequest req, PrintWriter out, Connection con, String club) {

    // first we need to make sure club5 contains the correct data

    Statement stmt = null;
    ResultSet rs = null;

    String ws_url = "", ws_user = "", ws_pass = "", deptID = "", tenderID = "", invMenuID = "", taxID = "";

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT pos_ws_url, pos_ws_user, pos_ws_pass, ibs_uidDeptID, ibs_uidTenderID, ibs_uidInvMenuID, ibs_uidTaxID FROM club5");

        if ( rs.next() ) {

            ws_url = rs.getString("pos_ws_url");
            ws_user = rs.getString("pos_ws_user");
            ws_pass = rs.getString("pos_ws_pass");
            deptID = rs.getString("ibs_uidDeptID");
            tenderID = rs.getString("ibs_uidTenderID");
            invMenuID = rs.getString("ibs_uidInvMenuID");
            taxID = rs.getString("ibs_uidTaxID");

        }

    } catch (Exception exc) {

        out.println("<p>Fatal error loading up IBS specific values from club5!</p>Error = " + exc.toString());
        return;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

    }

    if (ws_url.equals("") || ws_user.equals("") || ws_pass.equals("")) {

        out.println("<p>FATAL ERROR!  Please configure IBS interface in club setup before performing import.</p>");
        return;
    }

    if (deptID.equals("") || tenderID.equals("") || invMenuID.equals("") || taxID.equals("")) {

        out.println("<p>WARNING!  Before this interface can be used the rest of the configuration must be set.</p>");

    }



    // then we pull the data and parse it creating a log of unmatched entries


    boolean ok = false;
    int failed = 0;
    int updated = 0;

    String errmsg = "";

    try {

        URL url = new URL(ws_url);

        IBSWebMemberAPI service = null;

        service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

        IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

        ok = port.areYouThere(ws_user, ws_pass);

        int c = 0;

        if (ok) {

            out.println("<p>Communication OK - Dump Out The Member Records</p>");

            javax.xml.ws.Holder<String> holderMessage = new javax.xml.ws.Holder<String>();
            javax.xml.ws.Holder<String> holderResponse = new javax.xml.ws.Holder<String>();

            // PULL ALL MEMBER DATA
            port.getAllMembersString(ws_user, ws_pass, "CSV", holderMessage, holderResponse);


/*
            String lineA = "";
            String all_lines = "";
            FileReader fr = null;
            fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//CsvOfMemberData.csv");

            BufferedReader bfrin = new BufferedReader(fr);
            lineA = new String();

            while ((lineA = bfrin.readLine()) != null) {            // get one line of text

                //  parse the line to gather all the info

                //tok = new StringTokenizer( lineA, "," );     // delimiters are comma

                all_lines += lineA + "\n";
                c++;
            }


            out.println("<p>Found " + c + " lines in CSV file.</p>");


            String [] line = all_lines.split("\\n");

*/


            String [] line = holderResponse.value.split("\\n");

            String [] fields = null;
            String [] data = null;

            String memberID = "";
            String memberExt = "";
            String firstName = "";
            String middleInitial = "";
            String lastName = "";
            String msg = "";


            String test1 = "";
            String test2 = "";
            String test3 = "";

            out.println("<p>Found " + line.length + " records before fix.</p>");

            // remove line breaks contained within the data fields!

            int index = 0;
            String LINE_SEPARATOR = System.getProperty("line.separator");
            String delimiter = ",";
            String textQualifier = "\"";
            String replaceNewline = "%BREAK%";
            boolean inTextQualifiedField = false;

            boolean error = false;

            String newCSV = "";

            for (int i=1; i < line.length; i++) { // start with 1 so we skip the header row

               boolean start = true;

               while( index >= 0 ) {

                   if( inTextQualifiedField ) {

                       index = line[i].indexOf(textQualifier, index);

                       if( index > -1 ) {
                           index += textQualifier.length();
                           inTextQualifiedField = false;
                       } else {
                           //Hit the end of line ( ie. newline in text qualified field )
                           // Add replaceNewline and continue;
                           newCSV += line[i];
                           newCSV += replaceNewline;
                           index = -2;
                       }

                   } else {

                       if( start ) {
                           //Check beginning of line for text qualifier
                           if( line[i].indexOf(textQualifier, index) == 0 ) {
                               inTextQualifiedField = true;
                               index += textQualifier.length();
                           }
                           start = false;
                           continue;
                       }

                       index = line[i].indexOf(delimiter, index);

                       if( index > -1 ) {
                           //found delimiter check next
                           index += delimiter.length();

                           if( line[i].indexOf(textQualifier, index) == index ) {
                               //Next character is textQualifier
                               inTextQualifiedField = true;
                               index += textQualifier.length();
                           }

                       }
                   }
               }

               if( !inTextQualifiedField ) {
                   newCSV += line[i];
                   newCSV += LINE_SEPARATOR;
               }

               index = 0;

            }


            // re-assign to new csv string
            line = newCSV.split("\\n");

            out.println("<p>Found " + line.length + " records after fix</p>");

            // debug output
            for (int i=0; i < line.length-1; i++) {     // start with the header row

                //out.println(line[i]);
                //out.println("");

                out.println("<p>" +line[i]+ "</p>");
            }

            out.println("<br><br><p>*****************************************************</p><br><br>");

            
           // comment out the following if you only want to display the member roster

            for (int i=1; i < line.length; i++) { // start with 1 so we skip the header row

                errmsg = "0|" + i;

                // reset
                memberID = "";
                memberExt = "";
                firstName = "";
                middleInitial = "";
                lastName = "";

                fields = line[i].split("\\,");    // get each column in to the fields string array

                StringTokenizer tok = new StringTokenizer( line[i], "," );

                //test1 = tok.nextToken();
                //test1 = tok.nextToken();
                //test1 = tok.nextToken();
                //test1 = tok.nextToken();
                //test2 = tok.nextToken();

                //out.println("<br>fields length=" + fields.length + " " + test1 + ", " + test2);



                errmsg = "fields length=" + fields.length + " ,tok count=" + tok.countTokens() + " for line " + i + "<br>" + line[i];


                // if Status (field3) is 'active' and Level (field4) is 'member' then continue
                if (Utilities.trimQuotes(fields[3]).equalsIgnoreCase("Active") && Utilities.trimQuotes(fields[4]).equalsIgnoreCase("Member")) {

                    errmsg = "1|" + i;

                    memberID = Utilities.trimQuotes(fields[0]);
                    memberExt = Utilities.trimQuotes(fields[2]);

                    errmsg = "2|" + i;

                    data = Utilities.trimQuotes(fields[6]).split("\\|");   // field that contains the name parts (FirstName|MiddleInitial|LastName|Suffix|Title|BirthDate|Greetings)

                    errmsg = "3|" + i;

                    error = false;

                    firstName = "";
                    middleInitial = "";
                    lastName = "";

                    try {

                        firstName = Utilities.trimQuotes(data[0]);
                        middleInitial = Utilities.trimQuotes(data[1]);
                        lastName = Utilities.trimQuotes(data[2]);

                    } catch (Exception exc) {
                        error = true;
                        out.print("<br>MISSING NAME! name field=" + fields[6]); // + " - Found: " + lastName + " " + firstName + " " + middleInitial);
                    }

                    errmsg = "4|" + i;
                    errmsg = "5|" + i + " name=" + lastName + ", " + firstName + " " + middleInitial + " posid=" + memberID + "-" + memberExt;

                    if (!error) {

                        msg = updateMemberIBS(firstName, middleInitial, lastName, memberID, memberExt, con);

                        if (msg == null) msg = "MSG WAS NULL!!!!";

                        errmsg = "6|" + i + " msg=" + msg;

                        if (!msg.equals("")) {

                            out.print("<br>" + lastName + ", " + firstName + " " + middleInitial + " : " + msg);
                            failed++;

                        } else {

                            updated++;

                        }

                        //out.println("<br>line " + i + " ok. " + lastName + ", " + firstName + " " + middleInitial);

                    }

                } // end if member active
                else {

                    out.println("<br>line " + i + " skipped. Not active or not member.");

                }

            } // loop of all members in CSV download

            // final results
            out.println("<p>Updated " + updated + " members and failed to update " + failed + "</p>");
                        

        } // found WS

    } catch (Exception exc) {

        out.println("<p>Fatal error processing member file!</p><br>Msg = " + errmsg + "<br>Error = " + exc.toString());
        return;

    } finally {


    }

 }


 private String updateMemberIBS(String firstName, String middleInitial, String lastName, String memberID, String memberExt, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int count = 0;
    String msg = "";

    try {

        pstmt = con.prepareStatement (
           "SELECT COUNT(username) " +
           "FROM member2b " +
           "WHERE name_first = ? AND name_mi = ? AND name_last = ?");

        pstmt.clearParameters();
        pstmt.setString(1, firstName);
        pstmt.setString(2, middleInitial);
        pstmt.setString(3, lastName);

        rs = pstmt.executeQuery();

        if (rs.next()) {

            count = rs.getInt(1);

        }

        if (count == 1) {

            // we found only one member with this name so let's update it
            pstmt = con.prepareStatement (
               "UPDATE member2b SET " +
               "usta_num = ? " +
               "WHERE name_first = ? AND name_mi = ? AND name_last = ?");

            pstmt.clearParameters();
            pstmt.setString(1, memberID + "-" + memberExt);
            pstmt.setString(2, firstName);
            pstmt.setString(3, middleInitial);
            pstmt.setString(4, lastName);

            count = pstmt.executeUpdate();

            // if the update didn't occur then inform user
            if (count == 0) {

                msg = "NOT UPDATED";

            }

        } else if (count == 0) {

            // we didn't find even one user with this name
            msg = "NOT FOUND";

        } else {

            // we found multiple members with the name name (shouldn't happen)
            msg = "FOUND " + count + " MEMBERS WITH SAME NAME";

        }

    } catch (Exception exc) {

        // msg += "<br>&nbsp; &nbsp;<b>ERROR: " + exc.toString() + "</b>";
        Utilities.logError("Update IBS User: err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { pstmt.close(); }
        catch (SQLException ignored) {}

    }

    return msg;

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
   //  read in the text file - must be named 'email.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//email.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\email.csv");

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

            while (mem_id.startsWith( "0" )) {    // if starts with a zero

               mem_id = remZeroS(mem_id);           // remove the leading zero
            }

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
         
         email1 = email1.trim();
         email2 = email2.trim();
         
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
      out.println("<BR><BR>Stake Trace: "+ Utilities.getStackTraceAsString(e3));
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
   //  read in the text file - must be named 'bag.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//bag.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\bag.csv");

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

      //**********************************************************************
      //   The text file contains the info to import - get each line
      //
      //   Format:    username, bag#
      //
      //      -OR-    lname, fname, bag#
      //
      //      -OR-    fname lname, bag#
      //
      //**********************************************************************

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
   //  read in the text file - must be named 'birth.csv'
   //
   boolean failed = false;
   boolean byname = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   if (req.getParameter("byname") != null) {

      byname = true;                             // use lname, fname to locate member
   }

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//birth.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\birth.csv");

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
   //  read in the text file - must be named 'hndcp.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//hndcp.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\hndcp.csv");

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
         if ((u_hndcp.equalsIgnoreCase("NH")) || (u_hndcp.equalsIgnoreCase("NHL")) ||
             (u_hndcp.equalsIgnoreCase("NI")) || (u_hndcp.equalsIgnoreCase("N/I"))) {

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

         if ((c_hndcp.equalsIgnoreCase("NH")) || (c_hndcp.equalsIgnoreCase("NHL")) ||
             (c_hndcp.equalsIgnoreCase("NI")) || (c_hndcp.equalsIgnoreCase("N/I"))) {

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
 //  Process the 'Import GHIN Numbers' Request
 // ********************************************************************

 private void portGHIN(HttpServletRequest req, PrintWriter out, Connection con, String club) {


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
   String name1 = "";
   String name2 = "";
   String mi = "";
   String msg = "";

   //
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("ghin");

   //
   //  read in the text file - must be named 'ghinnum.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//ghinnum.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\ghinnum.csv");

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
      //  File Format:   ghin #, username, lname, fname (names optional)
      //
      //       -OR-      ghin #, lname, fname, mi (optional)
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         lname = "";
         fname = "";
         mi = "";

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {              // must be at least 2

            ghin = tok.nextToken();                  // ghin #

            //
            //  Process by type of call
            //
            if (rtype.equals( "name" )) {            // if request is By name

               lname = tok.nextToken();              // get name
               fname = tok.nextToken();

               if ( tok.countTokens() > 0 ) {
                  mi = tok.nextToken();              // mi
               }

            } else {

               mem_id = tok.nextToken();             // get username or ghin#

               if ( tok.countTokens() > 0 ) {
                  lname = tok.nextToken();              // lname
               }
               if ( tok.countTokens() > 0 ) {
                  fname = tok.nextToken();              // fname
               }
            }


            if (!ghin.equals( "" )) {            // if handicap # provided for this member

               //
               // update member record
               //
               if (rtype.equals( "name" )) {            // if request is By name

                  if (!mi.equals( "" )) {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);
                     pstmt2.setString(4, mi);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  } else {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE name_last = ? AND name_first = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();
                  }

               } else {

                  if (!mem_id.equals( "" )) {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "ghin = ? " +
                              "WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, ghin);
                     pstmt2.setString(2, mem_id);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  }
               }

               if (count == 0) {    // if not updated - record it

                  if (rtype.equals( "name" )) {            // if request is By name
                     msg = "Member Name not found: " +lname+ ", " +fname+ ", Handicap Num = " +ghin;
                  } else {
                     msg = "Member Username not found: " +mem_id+ ", Handicap Num = " +ghin;
                  }

                  logError(msg, club);                           // log it
               }
            }
         }

      }   // end of while

   }
   catch (Exception e3) {

      if (rtype.equals( "name" )) {            // if request is By name
         msg = "SQL Exception: " +lname+ ", " +fname+ ", " +ghin;
      } else {
         msg = "SQL Exception: " +mem_id+ ", " +ghin;
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
 //  Process the 'Import GHIN Data' Request (for GHIN Interface)
 // ********************************************************************

 private void portGHINdata(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int found = 0;
   int notFound = 0;
   int dupFound = 0;
   int assoc = 0;
   int clubNum = 0;

   String line = "";
   String mem_id = "";
   String temp = "";
   String ghin = "";
   String name = "";
   String fname = "";
   String lname = "";
   String assocs = "";
   String clubNums = "";
   String name1 = "";
   String name2 = "";
   String msg = "";
   
   String filename = "error_ghin.txt";

   //
   //  read in the text file - must be named 'ghindata.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;
   StringTokenizer tok2 = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//ghindata.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\ghindata.csv");

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
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("rtype");


   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //
      //   File Format:    name (fname lname), ghin#, Club#, Association#
      //
      //                   name (lname fname), ghin#, Club#, Association#
      //
      //                   name (lname, fname), ghin#, Club#, Association#
      //
      //                   username, ghin#, Club#, Association#
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {       // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );        // delimiters are comma

         //
         //  Process the name
         //
         name = "";
         fname = "";
         lname = "";
         mem_id = "";

         if (rtype.equalsIgnoreCase( "user" )) {          // if username provided

            mem_id = tok.nextToken();                     // get username

            mem_id = mem_id.trim();

            lname = mem_id;                               // save for messages
            name = mem_id;

         } else {

            if (rtype.equalsIgnoreCase( "fname" )) {

               name = tok.nextToken();               // get name

               tok2 = new StringTokenizer( name, " " );    // delimiters are spaces

               if ( tok2.countTokens() > 1 ) {

                  fname = tok2.nextToken();          // get fname
                  lname = tok2.nextToken();          // get lname

                  lname = lname.replace("\"", "");
                  fname = fname.replace("\"", "");

                  lname = lname.trim();
                  fname = fname.trim();

               }

            }
            else if(rtype.equalsIgnoreCase( "lnamenoc" )){

               name = tok.nextToken();              // get name

               tok2 = new StringTokenizer( name, " " );     // delimiters are spaces

               if ( tok2.countTokens() > 1 ) {

                   lname = tok2.nextToken();        // get lname
                   fname = tok2.nextToken();       // get fname

                   lname = lname.replace("\"", "");
                   fname = fname.replace("\"", "");

                   lname = lname.trim();
                   fname = fname.trim();

               }

            }else {  // must be lname

               lname = tok.nextToken();              // get lname
               fname = tok.nextToken();              // get fname

               lname = lname.replace("\"", "");
               fname = fname.replace("\"", "");

               StringTokenizer tok3 = new StringTokenizer(fname, " ");      // If fname has a space in it, mi is present.  Remove it.

               if (tok3.countTokens() > 1) {
                   fname = tok3.nextToken();
               }

               lname = lname.trim();
               fname = fname.trim();

               name = fname+ " " +lname;             // combine
            }
         }

         ghin = "";
         assocs = "";
         clubNums = "";
         assoc = 0;
         clubNum = 0;

         if ( tok.countTokens() > 0 ) {

            ghin = tok.nextToken();               // GHIN number

            if (ghin.equals ( "?" )) {

               ghin = "";
            } else {

                while (ghin.length() < 7) {
                    ghin = "0" + ghin;
                }
            }
         }

         if ( tok.countTokens() > 0 ) {

            clubNums = tok.nextToken();           // club number

            if (clubNums.equals ( "?" )) {

               clubNums = "";
            }
         }
         if ( tok.countTokens() > 0 ) {

            assocs = tok.nextToken();             // association number

            if (assocs.equals ( "?" )) {

               assocs = "";
            }
         }

         while (clubNums.length() < 3) {
             clubNums = "0" + clubNums;
         }
         
         while (assocs.length() < 2) {
             assocs = "0" + assocs;
         }

         if (!clubNums.equals("")){
             try {
                 pstmt2 = con.prepareStatement("SELECT hdcp_club_num_id FROM hdcp_club_num WHERE club_num = ?");
                 pstmt2.clearParameters();
                 pstmt2.setString(1, clubNums);

                 rs = pstmt2.executeQuery();

                 if (rs.next()) {
                     clubNum = rs.getInt("hdcp_club_num_id");
                 } else {
                     clubNum = 0;
                 }

             } catch (Exception exc) {
                 Utilities.logError("Support_import.portGHINdata - " + club + " - Error looking up hdcp_club_num_id value - " + exc.toString());
             } finally {

                 try { rs.close(); }
                 catch (Exception ignore) { }

                 try { pstmt2.close(); }
                 catch (Exception ignore) { }
             }
         }

         if (!assocs.equals("")){
             try {
                 pstmt2 = con.prepareStatement("SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE assoc_num = ?");
                 pstmt2.clearParameters();
                 pstmt2.setString(1, assocs);

                 rs = pstmt2.executeQuery();

                 if (rs.next()) {
                     assoc = rs.getInt("hdcp_assoc_num_id");
                 } else {
                     assoc = 0;
                 }

             } catch (Exception exc) {
                 Utilities.logError("Support_import.portGHINdata - " + club + " - Error looking up hdcp_assoc_num_id value - " + exc.toString());
             } finally {

                 try { rs.close(); }
                 catch (Exception ignore) { }

                 try { pstmt2.close(); }
                 catch (Exception ignore) { }
             }
         }

         if (!ghin.equals( "" )) {             // if ghin# provided

            if (rtype.equalsIgnoreCase( "user" )) {          // if username provided

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? WHERE username = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, mem_id);

            } else {

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, lname);
               pstmt2.setString(5, fname);

/*
               // used when setting gender as well
               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET " +
                           "ghin = ?, " +
                           "hdcp_club_num_id = IFNULL(" +
                               "(SELECT hdcp_club_num_id FROM hdcp_club_num WHERE CAST(club_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "hdcp_assoc_num_id = IFNULL(" +
                               "(SELECT hdcp_assoc_num_id FROM hdcp_assoc_num WHERE CAST(assoc_num AS UNSIGNED) = CAST(? AS UNSIGNED)), 0), " +
                           "gender = ? " +
                       "WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, ghin);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, gender);
               pstmt2.setString(5, lname);
               pstmt2.setString(6, fname);
               // end of gender setting section
*/
               // if ccrockies then some of their first names in member2b have an * preceeding the first name
               // so if the update doesn't do anything then try it again with an * after the fname
               if (club.equals("ccrockies")) {

                   count = pstmt2.executeUpdate();

                   if (count == 0) {
                       pstmt2 = con.prepareStatement (
                               "UPDATE member2b SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? WHERE name_last = ? AND name_first = ?");

                       pstmt2.clearParameters();
                       pstmt2.setString(1, ghin);
                       pstmt2.setInt(2, clubNum);
                       pstmt2.setInt(3, assoc);
                       pstmt2.setString(4, lname);
                       pstmt2.setString(5, fname+"*");
                   }

               }
            }

            count = pstmt2.executeUpdate();

            pstmt2.close();

            if (count == 0) {    // if not updated - record it

               msg = "Member Name not found: " +lname+", " + fname + "  GHIN#: " + ghin;
               logError(msg, club, filename);                           // log it
               notFound++;

            }
            else if (count >= 2){

               msg = "Multiple entries updated: name was " + lname + ", " + fname + "  GHIN#: " + ghin;
               logError(msg, club, filename);
               dupFound++;

            }
            else {

               found++;
            }
         } // end if ghin# provided

      }   // end of while

   }
   catch (Exception e3) {

      msg = "SQL Exception: " +name;
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
   out.println("<BR><BR>Members Found = " +found+ ", Members Not Found = " +notFound);
   out.println("<BR><BR>Duplicate Members Found = " + dupFound);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }
 
 


 // ********************************************************************
 //  Process the 'Import GolfNet' Request (for GolfNet Interface)
 // ********************************************************************

 private void portGolfNet(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int found = 0;
   int notFound = 0;
   int dupFound = 0;
   int assoc = 0;
   int clubNum = 0;

   String line = "";
   String mem_id = "";
   String temp = "";
   String golfnet = "";
   String name = "";
   String fname = "";
   String lname = "";
   String assocs = "";
   String clubNums = "";
   String name1 = "";
   String name2 = "";
   String msg = "";
   
   String filename = "error_golfnet.txt";

   //
   //  read in the text file - must be named 'ghindata.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;
   StringTokenizer tok2 = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//golfnet.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\golfnet.csv");

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
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("golfnet");


   try {

      BufferedReader bfrin = new BufferedReader(fr);
      line = new String();

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //
      //   File Format:    name (fname lname), golfnet#
      //
      //                   name (lname fname), golfnet#
      //
      //                   name (lname, fname), golfnet#
      //
      //                   username, golfnet#
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {       // get one line of text

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );        // delimiters are comma

         //
         //  Process the name
         //
         name = "";
         fname = "";
         lname = "";
         mem_id = "";

         if (rtype.equalsIgnoreCase("user")) {          // if username provided
             
             mem_id = tok.nextToken();                     // get username
             
             mem_id = mem_id.trim();
             
             lname = mem_id;                               // save for messages
             name = mem_id;
             
         } else {
             
             lname = tok.nextToken();              // get lname
             fname = tok.nextToken();              // get fname
             
             lname = lname.replace("\"", "");
             fname = fname.replace("\"", "");
             
             StringTokenizer tok3 = new StringTokenizer(fname, " ");      // If fname has a space in it, mi is present.  Remove it.
             
             if (tok3.countTokens() > 1) {
                 fname = tok3.nextToken();
             }
             
             lname = lname.trim();
             fname = fname.trim();
             
             name = fname + " " + lname;             // combine
         }

         golfnet = "";
         assocs = "";
         clubNums = "";
         assoc = 0;
         clubNum = 0;

         if ( tok.countTokens() > 0 ) {

            golfnet = tok.nextToken();               // GolfNet number

            if (golfnet.equals ( "?" )) {

               golfnet = "";
            }
         }

         if (!golfnet.equals( "" )) {             // if golfnet# provided

            if (rtype.equalsIgnoreCase( "user" )) {          // if username provided

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? WHERE username = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, golfnet);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, mem_id);

            } else {    // by name (lname, fname)

               pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET ghin = ?, hdcp_club_num_id = ?, hdcp_assoc_num_id = ? WHERE name_last = ? AND name_first = ?");

               pstmt2.clearParameters();
               pstmt2.setString(1, golfnet);
               pstmt2.setInt(2, clubNum);
               pstmt2.setInt(3, assoc);
               pstmt2.setString(4, lname);
               pstmt2.setString(5, fname);
            }

            count = pstmt2.executeUpdate();

            pstmt2.close();

            if (count == 0) {    // if not updated - record it

               msg = "Member Name not found: " +lname+", " + fname + "  GolfNet#: " + golfnet;
               logError(msg, club, filename);                           // log it
               notFound++;

            }
            else if (count >= 2){

               msg = "Multiple entries updated: name was " + lname + ", " + fname + "  GolfNet#: " + golfnet;
               logError(msg, club, filename);
               dupFound++;

            }
            else {

               found++;
            }
         } // end if golfnet# provided

      }   // end of while

   }
   catch (Exception e3) {

      msg = "SQL Exception: " +name;
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
   out.println("<BR><BR>Members Found = " +found+ ", Members Not Found = " +notFound);
   out.println("<BR><BR>Duplicate Members Found = " + dupFound);
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // ********************************************************************
 //  Process the 'Import GHIN Numbers' Request
 // ********************************************************************

 private void portCDGA(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int found = 0;
   int notFound = 0;
   int dupFound = 0;

   float u_hcap = 0;           // usga hndcp
   float c_hcap = 0;           // course hndcp

   String line = "";
   String mem_id = "";
   String temp = "";
   String cdga = "";
   String fname = "";
   String lname = "";
   String name1 = "";
   String name2 = "";
   String mi = "";
   String salu = "";
   String gender = "";
   String miscDataFields = "";
   String msg = "";

   boolean incMiscData = false;

   //
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("rtype");
   
   if (req.getParameter("miscdata") != null) {
       incMiscData = true;
   }

   //
   //  read in the text file - must be named 'ghinnum.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//cdga.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\cdga.csv");

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

      bfrin.readLine(); // ignore column header line

      //********************************************************************
      //   The text file contains the info to import - get each line
      //
      //  File Format:   standardized format file from CDGA with HDCP# (Col 4), memNum (Col 5), lname (Col 6), fname (Col 7), mi (Col 8), salutation (Col 9), gender (Col 10) - rest can be ignored
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         lname = "";
         fname = "";
         mi = "";

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {              // must be at least 2

            // Ignore first three fields
            tok.nextToken();    // SiteID - Col A - Skipped
            tok.nextToken();    // ClubID - Col B - Skipped
            tok.nextToken();    // ClName - Col C - Skipped

            cdga = tok.nextToken();     // MemberID - Col D (HDCP#)

            mem_id = tok.nextToken();             // LocalNum - Col E (mNum)

            lname = tok.nextToken();     // Last - Col F
            fname = tok.nextToken();     // First - Col G
            mi = tok.nextToken();        // mi - Col H

            if (mi.equalsIgnoreCase("NULL")) {
                mi = "";
            }

            salu = tok.nextToken();      // Salu - Col I (Salutation)
            gender = tok.nextToken();    // Gender - Col J

            if (salu.equalsIgnoreCase("NULL")) {
                salu = "";
            }

            if (gender.equalsIgnoreCase("W")) {
                gender = "F";
            }

            // Ignore the rest of the columns

            if (!cdga.equals( "" )) {            // if handicap # provided for this member

               // update member record
               if (incMiscData) {       // If selected to import salutation and gender fields

                   if (rtype.equals( "name" )) {            // if request is By name

                      if (!mi.equals( "" )) {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ?, name_pre = ?, gender = ? " +
                                  "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, salu);
                         pstmt2.setString(3, gender);
                         pstmt2.setString(4, lname);
                         pstmt2.setString(5, fname);
                         pstmt2.setString(6, mi);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();

                      } else {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ?, name_pre = ?, gender = ? " +
                                  "WHERE name_last = ? AND name_first = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, salu);
                         pstmt2.setString(3, gender);
                         pstmt2.setString(4, lname);
                         pstmt2.setString(5, fname);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();
                      }

                   } else {

                      if (!mem_id.equals( "" )) {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ?, name_pre = ?, gender = ? " +
                                  "WHERE username = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, salu);
                         pstmt2.setString(3, gender);
                         pstmt2.setString(4, mem_id);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();

                      }
                   }
                   
               } else {     // do not import salutation and gender fields

                   if (rtype.equals( "name" )) {            // if request is By name

                      if (!mi.equals( "" )) {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ? " +
                                  "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, lname);
                         pstmt2.setString(3, fname);
                         pstmt2.setString(4, mi);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();

                      } else {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ? " +
                                  "WHERE name_last = ? AND name_first = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, lname);
                         pstmt2.setString(3, fname);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();
                      }

                   } else {

                      if (!mem_id.equals( "" )) {

                         pstmt2 = con.prepareStatement (
                                  "UPDATE member2b SET " +
                                  "ghin = ? " +
                                  "WHERE username = ?");

                         pstmt2.clearParameters();
                         pstmt2.setString(1, cdga);
                         pstmt2.setString(2, mem_id);

                         count = pstmt2.executeUpdate();

                         pstmt2.close();

                      }
                   }
               }

                if (count == 0) {    // if not updated - record it

                    if (rtype.equals( "name" )) {            // if request is By name
                        msg = "Member Name not found: " +lname+ ", " +fname+ ", Handicap Num = " +cdga;
                    } else {
                        msg = "Member Username not found: " +mem_id+ " - " + lname + ", " + fname + ", Handicap Num = " +cdga;
                    }
                    logError(msg, club);                           // log it
                    notFound++;

                } else if (count >= 2){

                   msg = "Multiple entries updated: name was " + lname + ", " + fname + "  CDGA: " + cdga;
                   logError(msg, club);
                   dupFound++;

                } else {

                   found++;
                }
            }
         }

      }   // end of while

   }
   catch (Exception e3) {

      if (rtype.equals( "name" )) {            // if request is By name
         msg = "SQL Exception: " +lname+ ", " +fname+ ", " + cdga;
      } else {
         msg = "SQL Exception: " +mem_id+ ", " + cdga;
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
   out.println("<BR><BR>Members Found = " +found+ ", Members Not Found = " +notFound);
   out.println("<BR><BR>Duplicate Members Found = " + dupFound);
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
    int tmp = 0; // holds # of members found w/ queried name
    int updated = 0;
    int err_nf = 0; // not found
    int err_mf = 0; // multiple found
    int err_nu = 0; // not updated

    String line = "";
    String mem_id = "";
    String webid = "";
    String lname = "";
    String fname = "";
    String mNum = "";
    String msg = "";
    String skip = "";
    String here = "";
    String sql = "";
    String[] col;
   
   String filename = "error_webid.txt";

    boolean byname = (req.getParameter("byname") != null) ? true : false;
    boolean found = false;

    //
    //  read in the text file
    //
    boolean failed = false;
    FileReader fr = null;

    StringTokenizer tok = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//webid.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\webid.csv");

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
        //       -OR-      webid, username, lname (optional), fname (optional)  (name used for error message only)
        //
        //       -OR-      webid, lname, fname, mNum (optional)  - byname option
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text

            //  parse the line to gather all the info
            t++;
            webid = "";
            mem_id = "";
            lname = "";
            skip = "";
            fname = "";

            if (byname == true) {           // process by name ?

                tok = new StringTokenizer( line, "," );     // delimiters are comma
                webid = tok.nextToken();              // get webid

                lname = "";
                fname = "";
                mNum = "";

                if ( tok.countTokens() > 0 ) {

                   lname = tok.nextToken();            // lname
                }

                if ( tok.countTokens() > 0 ) {         // if more

                   fname = tok.nextToken();            // fname

                   if ( tok.countTokens() > 0 ) {
                       mNum = tok.nextToken();
                   }

                } else {                                // only 1 name field - parse it

                   tok = new StringTokenizer( lname, " " );     // get fname only (drop any mi)

                   if ( tok.countTokens() > 2 ) {           // first mi last ?

                      fname = tok.nextToken();            // fname
                      lname = tok.nextToken();            // skip mi
                      lname = tok.nextToken();            // lname

                   } else {

                      if ( tok.countTokens() > 1 ) {           // first last ?

                         fname = tok.nextToken();            // fname
                         lname = tok.nextToken();            // lname
                      }
                   }
                }

                tok = new StringTokenizer( fname, " " );     // get fname only (drop any mi)

                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();                // fname only
                }

                fname = fname.trim();
                lname = lname.trim();
                webid = webid.trim();

            } else {

                tok = new StringTokenizer( line, "," );     // delimiters are comma
                webid = tok.nextToken();              // get webid
                mem_id = tok.nextToken();             // get username

                lname = "";
                fname = "";

                if ( tok.countTokens() > 0 ) {
                    lname = tok.nextToken();            // name1
                }
                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();            // name2
                }

            } // end if club ==

            here = "2";

            if (webid.equals( "?" )) {

               webid = "";
            }

            if (mem_id.equals( "?" )) {

               mem_id = "";
            }


            // if we retrieved a webid from the import file
            // then lets attempt to locate and update a record
            //
            if (!webid.equals( "" )) {

                //
                //  Make sure value is alphanumeric (remove space char w/parity (A0 hex) from jonas strings !!!!!!!!!!!!!!!
                //
                webid = stripSP( webid );        // remove last digit if special char

                //
                // update member record
                //
                if (byname) {

                    //
                    // fist locate member record
                    //
                    tmp = 0;
                    if (!mNum.equals( "" )) {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ? AND memNum = ?";
                    } else {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ?";
                    }
                    pstmt2 = con.prepareStatement (sql);
                    pstmt2.clearParameters();
                    pstmt2.setString(1, lname);
                    pstmt2.setString(2, fname);
                    if (!mNum.equals( "" )) {
                       pstmt2.setString(3, mNum);
                    }
                    rs = pstmt2.executeQuery();

                    if (rs.next()) tmp = rs.getInt(1);

                    pstmt2.close();

                    if (tmp == 0) {

                        logError("Member Not Found: name was " +lname+ ", " +fname+ " (" +webid+ ")", club, filename);
                        err_nf++;
                    } else {

                        if (tmp == 1) {

                            if (!mNum.equals( "" )) {
                               sql = "UPDATE member2b SET webid = ? WHERE name_last = ? AND name_first = ? AND memNum = ?";
                            } else {
                               sql = "UPDATE member2b SET webid = ? WHERE name_last = ? AND name_first = ?";
                            }
                            pstmt2 = con.prepareStatement (sql);
                            pstmt2.clearParameters();
                            pstmt2.setString(1, webid);
                            pstmt2.setString(2, lname);
                            pstmt2.setString(3, fname);
                            if (!mNum.equals( "" )) {
                               pstmt2.setString(4, mNum);
                            }
                            count = pstmt2.executeUpdate();
                            if (count == 0) {
                                err_nu++;
                            } else {
                                updated++;
                            }

                            pstmt2.close();

                        } else {

                            logError("Multiple Members ("+tmp+") Found with name: " +lname+ ", " +fname+ " (" +webid+ ")", club, filename);
                            err_mf++;
                        }
                    }

                } else {          // by username

                   count = 0;

                   if (!mem_id.equals( "" )) {

                       sql = "UPDATE member2b SET webid = ? WHERE username = ?";
                       pstmt2 = con.prepareStatement (sql);
                       pstmt2.clearParameters();
                       pstmt2.setString(1, webid);
                       pstmt2.setString(2, mem_id);
                       count = pstmt2.executeUpdate();

                       pstmt2.close();
                    }

                    if (count == 0) {    // if not updated - record it (maybe it didn't update because it was already set - not with this api)

                       msg = "Member Not Found: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + webid + ")";
                       logError(msg, club, filename);                           // log it
                       err_nf++;

                    } else {
                       updated++;
                    }
                }

            } else {     // no webid

               msg = "Webid Not Specified: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + webid + ")";
               logError(msg, club, filename);                           // log it
               err_nf++;

            } // end if try to update

        }   // end of while

        logError("Total: "+t, club, filename);
        if (err_nf != 0) logError("Total Not Found: "+err_nf, club, filename);
        if (err_mf != 0) logError("Total Multiples Found: "+err_mf, club, filename);
        //logError("Total Not Updated: "+err_nu, club, filename); // doesn't work with this api to myqsl
        logError("Total Updated: "+updated, club, filename);

    } catch (Exception e3) {

        msg = "SQL Exception: " +mem_id+ ", " +lname+ ", " +fname+ " (" + webid + ")";
        logError(msg, club, filename);                           // log it

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
    out.println("<BR><BR>The Web Id Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }



 // ********************************************************************
 //  Process the 'Import Flexid' Request (Flex Id for Premier Interface)
 // ********************************************************************

 private void portFlexid(HttpServletRequest req, PrintWriter out, Connection con, String club) {


    Statement stmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    int count = 0;
    int t = 0;
    int tmp = 0; // holds # of members found w/ queried name
    int updated = 0;
    int err_nf = 0; // not found
    int err_mf = 0; // multiple found
    int err_nu = 0; // not updated

    String line = "";
    String mem_id = "";
    String flexid = "";
    String lname = "";
    String fname = "";
    String mNum = "";
    String msg = "";
    String skip = "";
    String here = "";
    String sql = "";
    String[] col;
   
   String filename = "error_flexid.txt";

    boolean byname = (req.getParameter("byname") != null) ? true : false;
    boolean found = false;

    //
    //  read in the text file
    //
    boolean failed = false;
    FileReader fr = null;

    StringTokenizer tok = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//flexid.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\flexid.csv");

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
        //  File Format:   flexid, username
        //
        //       -OR-      flexid, username, lname (optional), fname (optional)  (name used for error message only)
        //
        //       -OR-      flexid, lname, fname, mNum (optional)  - byname option
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text

            //  parse the line to gather all the info
            t++;
            flexid = "";
            mem_id = "";
            lname = "";
            skip = "";
            fname = "";

            if (byname == true) {           // process by name ?

                tok = new StringTokenizer( line, "," );     // delimiters are comma
                flexid = tok.nextToken();              // get flexid

                lname = "";
                fname = "";
                mNum = "";

                if ( tok.countTokens() > 0 ) {

                   lname = tok.nextToken();            // lname
                }

                if ( tok.countTokens() > 0 ) {         // if more

                   fname = tok.nextToken();            // fname

                   if ( tok.countTokens() > 0 ) {
                       mNum = tok.nextToken();
                   }

                } else {                                // only 1 name field - parse it

                   tok = new StringTokenizer( lname, " " );     // get fname only (drop any mi)

                   if ( tok.countTokens() > 2 ) {           // first mi last ?

                      fname = tok.nextToken();            // fname
                      lname = tok.nextToken();            // skip mi
                      lname = tok.nextToken();            // lname

                   } else {

                      if ( tok.countTokens() > 1 ) {           // first last ?

                         fname = tok.nextToken();            // fname
                         lname = tok.nextToken();            // lname
                      }
                   }
                }

                tok = new StringTokenizer( fname, " " );     // get fname only (drop any mi)

                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();                // fname only
                }

                fname = fname.trim();
                lname = lname.trim();
                flexid = flexid.trim();

            } else {

                tok = new StringTokenizer( line, "," );     // delimiters are comma
                flexid = tok.nextToken();              // get flexid
                mem_id = tok.nextToken();             // get username

                lname = "";
                fname = "";

                if ( tok.countTokens() > 0 ) {
                    lname = tok.nextToken();            // name1
                }
                if ( tok.countTokens() > 0 ) {
                    fname = tok.nextToken();            // name2
                }

            } // end if club ==

            here = "2";

            if (flexid.equals( "?" )) {

               flexid = "";
            }

            if (mem_id.equals( "?" )) {

               mem_id = "";
            }


            // if we retrieved a flexid from the import file
            // then lets attempt to locate and update a record
            //
            if (!flexid.equals( "" )) {

                //
                //  Make sure value is alphanumeric (remove space char w/parity (A0 hex) from jonas strings !!!!!!!!!!!!!!!
                //
                flexid = stripSP( flexid );        // remove last digit if special char

                //
                // update member record
                //
                if (byname) {

                    //
                    // fist locate member record
                    //
                    tmp = 0;
                    if (!mNum.equals( "" )) {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ? AND memNum = ?";
                    } else {
                       sql = "SELECT COUNT(*) FROM member2b WHERE name_last = ? AND name_first = ?";
                    }
                    pstmt2 = con.prepareStatement (sql);
                    pstmt2.clearParameters();
                    pstmt2.setString(1, lname);
                    pstmt2.setString(2, fname);
                    if (!mNum.equals( "" )) {
                       pstmt2.setString(3, mNum);
                    }
                    rs = pstmt2.executeQuery();

                    if (rs.next()) tmp = rs.getInt(1);

                    pstmt2.close();

                    if (tmp == 0) {

                        logError("Member Not Found: name was " +lname+ ", " +fname+ " (" +flexid+ ")", club, filename);
                        err_nf++;
                    } else {

                        if (tmp == 1) {

                            if (!mNum.equals( "" )) {
                               sql = "UPDATE member2b SET flexid = ? WHERE name_last = ? AND name_first = ? AND memNum = ?";
                            } else {
                               sql = "UPDATE member2b SET flexid = ? WHERE name_last = ? AND name_first = ?";
                            }
                            pstmt2 = con.prepareStatement (sql);
                            pstmt2.clearParameters();
                            pstmt2.setString(1, flexid);
                            pstmt2.setString(2, lname);
                            pstmt2.setString(3, fname);
                            if (!mNum.equals( "" )) {
                               pstmt2.setString(4, mNum);
                            }
                            count = pstmt2.executeUpdate();
                            if (count == 0) {
                                err_nu++;
                            } else {
                                updated++;
                            }

                            pstmt2.close();

                        } else {

                            logError("Multiple Members ("+tmp+") Found with name: " +lname+ ", " +fname+ " (" +flexid+ ")", club, filename);
                            err_mf++;
                        }
                    }

                } else {          // by username

                   count = 0;

                   if (!mem_id.equals( "" )) {

                       sql = "UPDATE member2b SET flexid = ? WHERE username = ?";
                       pstmt2 = con.prepareStatement (sql);
                       pstmt2.clearParameters();
                       pstmt2.setString(1, flexid);
                       pstmt2.setString(2, mem_id);
                       count = pstmt2.executeUpdate();

                       pstmt2.close();
                    }

                    if (count == 0) {    // if not updated - record it (maybe it didn't update because it was already set - not with this api)

                       msg = "Member Not Found: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + flexid + ")";
                       logError(msg, club, filename);                           // log it
                       err_nf++;

                    } else {
                       updated++;
                    }
                }

            } else {     // no flexid

               msg = "Flexid Not Specified: mem_id was " +mem_id+ ", name was " +lname+ ", " +fname+ " (" + flexid + ")";
               logError(msg, club, filename);                           // log it
               err_nf++;

            } // end if try to update

        }   // end of while

        logError("Total: "+t, club, filename);
        if (err_nf != 0) logError("Total Not Found: "+err_nf, club, filename);
        if (err_mf != 0) logError("Total Multiples Found: "+err_mf, club, filename);
        //logError("Total Not Updated: "+err_nu, club, filename); // doesn't work with this api to myqsl
        logError("Total Updated: "+updated, club, filename);

    } catch (Exception e3) {

        msg = "SQL Exception: " +mem_id+ ", " +lname+ ", " +fname+ " (" + flexid + ")";
        logError(msg, club, filename);                           // log it

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
    out.println("<BR><BR>The Flex Id Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 // ********************************************************************
 //  Process the 'Import Modes' Request (Modes of Trans Defaults)
 // ********************************************************************

 private void portMode(HttpServletRequest req, PrintWriter out, Connection con, String club) {


    Statement stmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;

    int count = 0;
    int t = 0;
    int tmp = 0;     // holds # of members found w/ queried name
    int updated = 0;
    int err_nf = 0; // not found
    int err_mf = 0; // multiple found
    int err_nu = 0; // not updated

    String line = "";
    String mem_id = "";
    String mode = "";
    String name1 = "";
    String name2 = "";
    String mNum = "";
    String msg = "";
    String skip = "";
    String here = "";
    String sql = "";
    String[] col;

    boolean byname = (req.getParameter("byname") != null) ? true : false;
    boolean found = false;

    //
    //  read in the text file - must be named 'hndcp.csv'
    //
    boolean failed = false;
    FileReader fr = null;

    StringTokenizer tok = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//mode.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\mode.csv");

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
        //  File Format:
        //                 mNum, mode, name1 (optional), name2 (optional)  (name used for error message only)
        //
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text

           //  parse the line to gather all the info
           t++;
           mode = "";
           mem_id = "";
           name1 = "";
           skip = "";
           name2 = "";

            tok = new StringTokenizer( line, "," );     // delimiters are comma
            mNum = tok.nextToken();                     // get mNum (could change to username)
            mode = tok.nextToken();                     // get mode

            name1 = "";
            name2 = "";

            if ( tok.countTokens() > 0 ) {
                name1 = tok.nextToken();            // name1
            }
            if ( tok.countTokens() > 0 ) {
                name2 = tok.nextToken();            // name2
            }

            here = "2";

            if (mode.equals( "?" )) {

               mode = "";
            }

            if (mNum.equals( "?" )) {

               mNum = "";
            }


            // if we retrieved a mode from the import file
            // then lets attempt to locate and update a record
            //
            if (!mode.equals( "" )) {

                //
                // update member record
                //
                count = 0;

                if (!mNum.equals( "" )) {

                    sql = "UPDATE member2b SET wc = ? WHERE memNum = ?";
                    pstmt2 = con.prepareStatement (sql);
                    pstmt2.clearParameters();
                    pstmt2.setString(1, mode);
                    pstmt2.setString(2, mNum);
                    count = pstmt2.executeUpdate();

                    pstmt2.close();
                 }

                 if (count == 0) {    // if not updated - record it (maybe it didn't update because it was already set - not with this api)

                    msg = "Member Not Found: mNum was " +mNum+ ", name was " +name2+ ", " +name1;
                    logError(msg, club);                           // log it
                    err_nf++;

                 } else {
                    updated++;
                 }

            } else {     // no mode

               msg = "Mode Not Specified: mNum was " +mNum+ ", name was " +name2+ ", " +name1;
               logError(msg, club);                           // log it
               err_nf++;

            } // end if try to update

        }   // end of while

        logError("Total: "+t, club);
        if (err_nf != 0) logError("Total Not Found: "+err_nf, club);
        if (err_mf != 0) logError("Total Multiples Found: "+err_mf, club);
        //logError("Total Not Updated: "+err_nu, club); // doesn't work with this api to myqsl
        logError("Total Updated: "+updated, club);

    } catch (Exception e3) {

        msg = "SQL Exception: " +mNum+ ", " +name1+ ", " +name2;
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
    out.println("<BR><BR>The Web Id Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 // ********************************************************************
 //  Process the 'Import Lottery Weight' Request
 // ********************************************************************

 private void portWeight(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int wght = 0;

   String line = "";
   String mem_id = "";
   String weight = "";
   String fname = "";
   String lname = "";
   String msg = "";

   //
   //  read in the text file - must be named 'weight.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//weight.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\weight.csv");

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

      //**********************************************************************
      //   The text file contains the info to import - get each line
      //
      //   Format:    username, weight#
      //
      //**********************************************************************

      while ((line = bfrin.readLine()) != null) {            // get one line of text

         //  parse the line to gather all the info

         weight = "";

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {

            mem_id = tok.nextToken();
            weight = tok.nextToken();

         } else {

            weight = "";
         }

         if (!weight.equals( "" )) {

            //
            // add a weight entry for this user
            //
            wght = Integer.parseInt(weight);


            pstmt2 = con.prepareStatement (
                        "INSERT INTO lassigns5 (username, lname, date, mins) " +
                        "VALUES (?, 'Saturday Lottery', 20080704, ?)");


            pstmt2.clearParameters();
            pstmt2.setString(1, mem_id);
            pstmt2.setString(2, weight);

            pstmt2.executeUpdate();

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


 /**
  * ************************************************************************
  * portDemoClubs - Process the 'Import Lottery Weight' Request
  *
  * File Format - comma delimited csv (Add extra columns of '?' if not all are present):
  * Club Name/Description (REQUIRED!)
  * Manufacturer
  * Club Type
  * ICN
  * Notes
  * For Sale - 0/1
  * Enabled - 0/1
  *
  * ************************************************************************
  **/
 private void portDemoClubs(HttpServletRequest req, PrintWriter out, Connection con, String club) {

     Statement stmt = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int count = 0;
     int fcount = 0;
     int dupFound = 0;
     int mfr_id = 1;
     int forSale = 0;
     int enabled = 1;

     String line = "";
     String name = "";
     String mfr = "";           // Club Manufacturer
     String clubType = "";      // Driver, Iron, etc
     String icn = "";           // Inventory Control #
     String notes = "";
     String sforSale = "";
     String senabled = "";

     //
     //  read in the text file - must be named 'ghindata.csv'
     //
     boolean failed = false;
     boolean skip = false;
     boolean mfrPort = false;
     boolean mfrFound = false;

     FileReader fr = null;

     StringTokenizer tok = null;
     StringTokenizer tok2 = null;

     try {

         fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//democlubs.csv");

     }
     catch (Exception e1) {

         failed = true;
     }

     if (failed == true) {

         try {

             fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\democlubs.csv");

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

         // See if we're to import unrecognized mfrs or not
         if (req.getParameter("mfrport") != null) {
             mfrPort = true;
         }

         BufferedReader bfrin = new BufferedReader(fr);
         line = new String();

         while ((line = bfrin.readLine()) != null) {       // get one line of text

             //  parse the line to gather all the info
             tok = new StringTokenizer( line, "," );        // delimiters are comma

             // Reset all variables
             name = "";
             mfr = "";
             mfr_id = 1;
             clubType = "";
             icn = "";
             notes = "";
             sforSale = "";
             senabled = "";
             forSale = 0;
             enabled = 1;

             skip = false;
             mfrFound = false;

             name = tok.nextToken().trim();
             mfr = tok.nextToken().trim();
             clubType = tok.nextToken().trim();
             icn = tok.nextToken().trim();
             notes = tok.nextToken().trim();

             sforSale = tok.nextToken().trim();
             if (sforSale.equals("1")) {
                 forSale = 1;
             } else {
                 forSale = 0;
             }

             senabled = tok.nextToken().trim();
             if (senabled.equals("0")) {
                 enabled = 0;
             } else {
                 enabled = 1;
             }

             if (name.equals("?")) {
                 name = "";
             }

             while (name.startsWith("\"")) {
                 name = name.substring(1);
             }
             while (name.endsWith("\"")) {
                 name = name.substring(0, name.length() - 1);
             }

             if (!name.equals("")) {        // At least the name/description must be present to continue

                 if (mfr.equals("?")) {
                     mfr = "None/Unknown";
                 } else {
                     mfr = Utilities.toTitleCase(mfr);
                 }

                 if (clubType.equals("?")) {
                     clubType = "Other";
                 }

                 if (icn.equals("?")) {
                     icn = "";
                 }

                 if (notes.equals("?")) {
                     notes = "";
                 }

                 // Determine the mfr_id
                 stmt = con.createStatement();
                 rs = stmt.executeQuery("SELECT * FROM demo_clubs_mfr");

                 while (rs.next()) {
                     if (mfr.toLowerCase().startsWith(rs.getString("mfr").toLowerCase())) {
                         mfr_id = rs.getInt("id");
                         mfrFound = true;
                     }
                 }

                 stmt.close();

                 // Mfr was not found, if set to add new mfrs, add this one
                 if (!mfrFound && mfrPort) {

                     pstmt = con.prepareStatement("INSERT INTO demo_clubs_mfr (mfr) VALUES (?)");
                     pstmt.clearParameters();
                     pstmt.setString(1, mfr);

                     pstmt.executeUpdate();

                     pstmt.close();

                     pstmt = con.prepareStatement("SELECT id FROM demo_clubs_mfr WHERE mfr = ?");
                     pstmt.clearParameters();
                     pstmt.setString(1, mfr);

                     rs = pstmt.executeQuery();

                     if (rs.next()) {
                         mfr_id = rs.getInt("id");
                     }

                     pstmt.close();
                 }

                 // Check for invalid club type
                 if (!clubType.equalsIgnoreCase("Driver") && !clubType.equalsIgnoreCase("Wood") && !clubType.equalsIgnoreCase("Hybrid") &&
                     !clubType.equalsIgnoreCase("Iron") && !clubType.equalsIgnoreCase("Wedge") && !clubType.equalsIgnoreCase("Putter") &&
                     !clubType.equalsIgnoreCase("Specialty")  && !clubType.equalsIgnoreCase("Rental")) {

                     clubType = "Other";
                 }

                 pstmt = con.prepareStatement("SELECT * FROM demo_clubs WHERE icn = ?");
                 pstmt.clearParameters();
                 pstmt.setString(1, icn);

                 rs = pstmt.executeQuery();

                 if (rs.next()) {

                     String msg = "Club ICN already exists in database (must be unique!): " +mfr+ ", " + clubType + ", " + name + "  ICN: " + icn;
                     logError(msg, club);

                     dupFound++;
                     fcount++;
                     skip = true;
                 }

                 pstmt.close();

                 if (!skip) {

                     pstmt = con.prepareStatement("INSERT INTO demo_clubs (name, type, mfr_id, notes, for_sale, enabled, icn) " +
                             "VALUES (?,?,?,?,?,?," + (icn.equals("") ? "NULL" : "?") + ")");
                     pstmt.clearParameters();
                     pstmt.setString(1, name);
                     pstmt.setString(2, clubType);
                     pstmt.setInt(3, mfr_id);
                     pstmt.setString(4, notes);
                     pstmt.setInt(5, forSale);
                     pstmt.setInt(6, enabled);
                     if (!icn.equals("")) {
                         pstmt.setString(7, icn);
                     }

                     int tempCount = pstmt.executeUpdate();
                     if (tempCount > 0) {
                         count++;
                     } else {

                         String msg = "Club import failed: " +mfr+ ", " + clubType + ", " + name + "  ICN: " + icn;
                         logError(msg, club);
                         fcount ++;
                     }
                 }

             } else {

                 String msg = "No club name found (REQUIRED!): " +mfr+ ", " + clubType + "  ICN: " + icn;
                 logError(msg, club);
                 fcount ++;
             }
         }
     } catch (Exception exc) {

         out.println("<HTML><HEAD><TITLE>Demo Clubs Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>Demo Clubs Conversion Failed</H3>");
         out.println("<BR><BR>DB Import Failed for  " + club);
         out.println("<BR><BR>Exception Received: "+ exc.getMessage());
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }

     out.println("<HTML><HEAD><TITLE>Demo Clubs Ported to DB</TITLE></HEAD>");
     out.println("<BODY><CENTER><H3>Demo Clubs Import Complete</H3>");
     out.println("<BR><BR>The Demo Clubs have Been Imported Successfully.");
     out.println("<BR><BR>Successful: " + count + "   Failed: " + fcount);
     out.println("<BR><BR>Duplicate ICN: " + dupFound);
     out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
     out.println("</CENTER></BODY></HTML>");
     out.close();
 }



 // ********************************************************************
 //  Process the 'Import POS Ids' Request
 // ********************************************************************

 private void portPOSID(HttpServletRequest req, PrintWriter out, Connection con, String club) {


   Statement stmt = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;

   String line = "";
   String mem_id = "";
   String temp = "";
   String posid = "";
   String fname = "";
   String lname = "";
   String name1 = "";
   String name2 = "";
   String mi = "";
   String msg = "";

   //
   //  Get type of request (by name or by username)
   //
   String rtype = req.getParameter("posid");

   //
   //  read in the text file - must be named 'ghinnum.csv'
   //
   boolean failed = false;
   FileReader fr = null;

   StringTokenizer tok = null;

   try {

      fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//posid.csv");

   }
   catch (Exception e1) {

      failed = true;
   }

   if (failed == true) {

      try {

         fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\posid.csv");

      }
      catch (Exception e2) {

         out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
         out.println("<BODY><CENTER><H3>POSID File Conversion Failed</H3>");
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
      //  File Format:   posid, username, lname, fname (names optional)
      //
      //       -OR-      posid, lname, fname, mi (mi optional)
      //
      //********************************************************************
      //
      while ((line = bfrin.readLine()) != null) {            // get one line of text

         lname = "";
         fname = "";
         mi = "";

         //  parse the line to gather all the info

         tok = new StringTokenizer( line, "," );     // delimiters are comma

         if ( tok.countTokens() > 1 ) {              // must be at least 2

            posid = tok.nextToken();                  // get posid

            posid = posid.trim();

            //
            //  Process by type of call
            //
            if (rtype.equals( "name" )) {            // if request is By name

               lname = tok.nextToken();              // get name
               fname = tok.nextToken();

               if ( tok.countTokens() > 0 ) {
                  mi = tok.nextToken();              // mi
               }

            } else {

               mem_id = tok.nextToken();             // get username 

               mem_id = mem_id.trim();

               if ( tok.countTokens() > 0 ) {
                  lname = tok.nextToken();              // lname
               }
               if ( tok.countTokens() > 0 ) {
                  fname = tok.nextToken();              // fname
               }
            }


            if (!posid.equals( "" )) {            // if posid provided for this member

               //
               // update member record
               //
               if (rtype.equals( "name" )) {            // if request is By name

                  if (!mi.equals( "" )) {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "posid = ? " +
                              "WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, posid);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);
                     pstmt2.setString(4, mi);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  } else {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "posid = ? " +
                              "WHERE name_last = ? AND name_first = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, posid);
                     pstmt2.setString(2, lname);
                     pstmt2.setString(3, fname);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();
                  }

               } else {

                  if (!mem_id.equals( "" )) {

                     pstmt2 = con.prepareStatement (
                              "UPDATE member2b SET " +
                              "posid = ? " +
                              "WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, posid);
                     pstmt2.setString(2, mem_id);

                     count = pstmt2.executeUpdate();

                     pstmt2.close();

                  }
               }

               if (count == 0) {    // if not updated - record it

                  if (rtype.equals( "name" )) {            // if request is By name
                     msg = "Member Name not found: " +lname+ ", " +fname+ ", POS Id = " +posid;
                  } else {
                     msg = "Member Username not found: " +mem_id+ ", POS Id = " +posid;
                  }

                  logError(msg, club);                           // log it
               }
            }
         }

      }   // end of while

   }
   catch (Exception e3) {

      if (rtype.equals( "name" )) {            // if request is By name
         msg = "SQL Exception: " +lname+ ", " +fname+ ", " +posid;
      } else {
         msg = "SQL Exception: " +mem_id+ ", " +posid;
      }
      logError(msg, club);                           // log it

      out.println("<HTML><HEAD><TITLE>Text File Port Failed</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>Text File Conversion Failed</H3>");
      out.println("<BR><BR>POSID Import Failed for  " + club);
      out.println("<BR><BR>Exception Received: "+ e3.getMessage());
      out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   out.println("<HTML><HEAD><TITLE>Text File Ported to DB</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Text File Conversion Complete</H3>");
   out.println("<BR><BR>The POSID File has Been Imported Successfully.");
   out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }                // end of Port POS Ids



 // ********************************************************************
 //  Process the 'Import PGA Village' Request (member course designations)
 // ********************************************************************
 private void portPGAVillage(HttpServletRequest req, PrintWriter out, Connection con, String club) {


    PreparedStatement pstmt = null;
    
    int month = 0;
    int day = 0;
    int year = 0;
    
    long date = 0;
    
    String line = "";
    String courses = "";
   
    String filename = "error_pgavillage.txt";

    //  read in the text file
    boolean failed = false;
    FileReader fr = null;

    try {

        fr = new FileReader("//usr//local//tomcat//webapps//" +club+ "//pgavillage.csv");

    }
    catch (Exception e1) {

        failed = true;
    }

    if (failed == true) {

        try {

            fr = new FileReader("c:\\java\\tomcat\\webapps\\" + club + "\\pgavillage.csv");

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
        line = new String();

        //********************************************************************
        //   The text file contains the info to import - get each line
        //
        //  File Format:   year, month, day, username
        //
        //********************************************************************
        //
        while ((line = bfrin.readLine()) != null) {            // get one line of text

            
                
            String[] temp = line.split(",");

            year = Integer.parseInt(temp[0]);
            month = Integer.parseInt(temp[1]);
            day = Integer.parseInt(temp[2]);

            courses = temp[3];

            date = (year * 10000) + (month * 100) + day;

            String[] temp_courses = courses.split("/");
            
            for (int i = 0; i < temp_courses.length; i++) {
                
                try {
                    pstmt = con.prepareStatement("INSERT IGNORE INTO custom_pgavillage (date, courseName) VALUES (?,?)");
                    pstmt.clearParameters();
                    pstmt.setLong(1, date);
                    pstmt.setString(2, temp_courses[i]);
                    
                    pstmt.executeUpdate();
                    
                } catch (Exception e) {
                    Utilities.logError("Support_import.portPGAVillage - " + club + " - Error inserting date/course listing into custom_pgavillage: " + e.toString());
                } finally {
                    Connect.close(pstmt);
                }
            }
            

        }   // end of while
        
    } catch (Exception e3) {

        String msg = "SQL Exception: " + month + "/" + day + "/" + year + " (" + courses + ")";
        logError(msg, club, filename);                           // log it

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
    out.println("<BR><BR>The PGA Village Text File has Been Imported Successfully.");
    out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main3.htm\">Return</A>");
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 //************************************************************************
 //  logError - logs error messages to a text file in the club's folder
 //************************************************************************
 private void logError(String msg, String club) {
     logError(msg, club, "error.txt");
 }
 
 private void logError(String msg, String club, String filename) {

   String space = "  ";
   int fail = 0;

   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" +club+ "//" + filename, true));

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
         PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +club+ "\\" + filename, true));

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
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR> <FORM>");
   out.println("<INPUT TYPE='BUTTON' Value='Close' onClick='self.close()'>");
   out.println("</FORM></CENTER></BODY></HTML>");

 }

}
