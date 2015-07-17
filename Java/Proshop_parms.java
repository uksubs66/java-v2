/***************************************************************************************     
 *   Proshop_parms:  This servlet will process the 'set parameters' request from Proshop's
 *                   course setup page.
 *
 *
 *   called by:  proshop menu (doGet) and Proshop_parms (via doPost in HTML built here)
 *
 *   created: 3/02/2002   Bob P.
 *
 *
 *   last updated:
 *
 *       11/11/09   Increase the interval minutes and alternate minutes max value from 15 to 90.
 *        9/08/09   Remove stats5 db table processing as we don't use this any longer.
 *        4/14/09   Add fix for pace_benchmark insert statement (wasn't setting invert field)
 *        2/19/09   Add Club Prophet Systems POS Type for new version of ProShopKeeper.
 *        2/13/09   Get the courseid from clubparm in buildParms in case Jonas POS I/F is used.
 *        9/14/08   Removed restrictions on new features
 *        9/04/08   Added Blackhawk CC (blackhawk) to Pro Only Tmodes testing
 *        9/02/08   Javascript compatability updates
 *        8/20/08   Added Northridge to Pro Only Tmodes testing
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        7/11/08   fixed issue with club parms insert statement
 *        7/02/08   fixed issues with club parms insert statements
 *        6/24/08   Added Pro Only checkboxes for manipulating Pro Only tmodes
 *        8/25/06   Add IBS, TAI and Club Soft to list of POS systems.
 *        7/26/06   Adjusted maxlength of POS related text boxes
 *        7/06/06   Added Pace of Play options for the course
 *        4/11/06   Add checks for CSG POS type.
 *        9/14/05   Add checks for Abacus POS type.
 *        7/14/05   Add checks for NorthStar POS type.
 *        1/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *       12/15/04   Allow 'mins_between' and 'alt_mins' to start with 3 instead of 6.
 *       10/08/04   Use tpos & t9pos fields instead of tmodeI fields for new Jonas I/F.
 *        9/30/04   Make changes for new Jonas I/F.
 *        6/14/04   Change all db tables if courseName changes.
 *        3/05/04   Add tmodeItems (tmode Item Group) for Jonas POS system I/F.
 *        2/04/04   Add POS system parms.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        8/15/02   Added 'alternate minutes between tee times' parm.
 *
 *       12/05/02   Enhancements for Version 2 of the software.
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Proshop_parms extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //******************************************************
 //
 // Process the initial request from Proshop menu
 //
 //******************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
   }
     
   String club = (String)session.getAttribute("club");
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Define parms - set defaults
   //
   String course = "";
   String posType = "";

   int multi = 0;        // multiple course support option
   int paceofplay = 0;

   //
   //  check if multi-couse is yes
   //     if multi, then we need to output a menu (new, update, delete)
   //     if not multi, then check for course parms already there
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi, paceofplay, posType FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         multi = rs.getInt(1);
         paceofplay = rs.getInt(2);
         posType = rs.getString(3);

      } else {

        // Parms do not exist yet - inform user to start with club setup

        out.println(SystemUtils.HeadTitle("Sequence Error"));
        out.println("<BODY><CENTER>");
        out.println("<BR><BR><H3>Setup Sequence Error</H3>");
        out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
        out.println("<BR>The club setup has not been completed.");
        out.println("<BR>Please return to Configuration and select Club Setup.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
        out.println("</CENTER></BODY></HTML>");
        return;
      }

      stmt.close();

      if (multi == 0) {

         //
         //  Go build the parms table 
         //
         buildParms(req, resp, out, con, multi, course, club, posType, lottery, paceofplay);  

      } else {
        
         //
         //  Multi=Yes - output a menu to prompt for action to perform (new, update or delete)
         //

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2");  // get all existing course names

         if (rs.next()) {

            course = rs.getString(1);     // get course name if entry exists
         }

         stmt.close();

         if (course.equals( "" )) {       // if no entries or one entry w/o a name

            buildParms(req, resp, out, con, multi, course, club, posType, lottery, paceofplay);   // go build parms table
            return;
         }

         //         
         //  At least one course already exists with a name - let user select options
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName FROM clubparm2");  // get all existing course names again

         out.println(SystemUtils.HeadTitle("Proshop - Course Selection Page"));

         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");

         out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<b>Course Selection</b>");
         out.println("<br>Select the course and task below.<br>");
         out.println("</font>");
         out.println("</td></tr></table><br><br><br><br>");

         out.println("<table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
         out.println("<form action=\"/" +rev+ "/servlet/Proshop_parms\" method=\"post\" target=\"bot\">");
         out.println("<tr>");

         out.println("<td align=\"right\">");
            out.println("<font size=\"2\">");
            out.println("<br>Select the course (or new): &nbsp;&nbsp;<br>");
            out.println("</font>");

         out.println("</td><td width=\"200\" align=\"left\"><font size=\"2\">");
            out.println("<br>&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");
            out.println("<option selected value=\"-new-\">-new-</option>");

         while (rs.next()) {

            course = rs.getString(1);
            out.println("<option value=\"" + course + "\">" + course + "</option>");
         }
         stmt.close();

            out.println("</select>");
            out.println("<br></font>");
         out.println("</td>");
         out.println("</tr><tr>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("<br>&nbsp;&nbsp;");
               out.println("<input type=\"submit\" name=\"Remove\" value=\"Remove\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("<br></font>");

            out.println("</td><td width=\"200\" align=\"left\"><font size=\"2\">");
               out.println("<font size=\"2\">");
               out.println("<br>&nbsp;&nbsp;");
               out.println("<input type=\"submit\" name=\"Add/Update\" value=\"Add/Update\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("<br></font>");
            out.println("</td>");
         out.println("</tr>");
         out.println("</form></table><br><br>");

         out.println("<table border=\"0\" align=\"center\">");
         out.println("<tr>");
         out.println("<td align=\"center\" width=\"200\">");
         out.println("<font size=\"2\">");
         out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
         out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</form></font></td></tr></table>");

         out.println("</center></font></body></html>");

      }
   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

 }  // end of doGet

 //
 //****************************************************************
 //
 // Process the form requests from doGet
 //
 //  Parms Received: 
 //
 //     for Multi=Yes and 1 or more courses already exist
 //        course = name of course
 //        submit button = 'remove' or 'add/update'
 //     
 //     for Delete Confirmation     
 //        submit button = 'conf'
 //
 //     for normal update request
 //        submit button = 'submit'
 //        course = course name
 //        oldName = original course name (may have changed)
 //        fives = 5some support (yes or no)
 //        mc, pc, ca, wa - chech boxes
 //        firsttee_hr (0 - 9)
 //        firsttee_min (0 - 59)
 //        lasttee_hr (16 - 21)
 //        lasttee_min (0 - 59)
 //        mins_between (3 - 90)
 //        alt_mins_between (0, 3 - 90)
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
     
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_CLUBCONFIG", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_CLUBCONFIG", out);
   }
     
   String club = (String)session.getAttribute("club");
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   boolean error = false;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only

   //String omit = "";
   String oldName = "";
   String course = "";
   String courseid = "";
   String sfives = "";
   String sfirst_hr = "";
   String sfirst_min = "";
   String slast_hr = "";
   String slast_min = "";
   String smins = "";
   String samins = "";
   String posType = "";
   //String tmode = "";
   //String temps = "";
     
   //
   //  arrays to position the tmode parms (start with an index of '1')
   //
   String [] tmodeA = new String [parm.MAX_Tmodes+1];     // array to hold the tmode names
   String [] tmodeaA = new String [parm.MAX_Tmodes+1];    // array to hold the tmode acronyms
   String [] tposA = new String [parm.MAX_Tmodes+1];  
   String [] tposcA = new String [parm.MAX_Tmodes+1];
   String [] t9posA = new String [parm.MAX_Tmodes+1];
   String [] t9poscA = new String [parm.MAX_Tmodes+1];
   int [] tOptA = new int [parm.MAX_Tmodes+1];
  
   //float tfloat = 0;

   int clubparm_id = 0;
   int i = 0;
   int i2 = 0;
   int multi = 0;
   int exist = 0;
   int temp = 0;
   int count = 0;
   int fives = 0;
   int paceofplay = 0;
   //int mc = 0;
   //int pc = 0;
   //int ca = 0;
   //int wa = 0;
   int xx = 0;        // current value for xx
   int first_hr = 99;       // initialize as invalid values
   int first_min = 99;
   int last_hr = 99;
   int last_min = 99;
   int mins = 99;
   int amins = 99;


   //
   //  init the string arrays
   //
   for (i=0; i<parm.MAX_Tmodes+1; i++) {
      tmodeA[i] = "";
      tmodeaA[i] = "";
      tposA[i] = "";
      t9posA[i] = "";
      tposcA[i] = "";
      t9poscA[i] = "";
      tOptA[i] = 0;
   }

   //
   // Process request according to which 'submit' button was selected
   //
   //      Submit - update the record
   //      Add/Update - output the parm table
   //      Remove - delete the record - prompt user for conf
   //      Conf - process the delete confirmation
   //
   if (req.getParameter("Add/Update") != null) {

      //
      //  get club parms
      //
      try {

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi, paceofplay, posType FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
            paceofplay = rs.getInt(2);
            posType = rs.getString(3);

         } else {

           // Parms do not exist yet - inform user to start with club setup

           out.println(SystemUtils.HeadTitle("Sequence Error"));
           out.println("<BODY><CENTER>");
           out.println("<BR><BR><H3>Setup Sequence Error</H3>");
           out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
           out.println("<BR>The club setup has not been completed.");
           out.println("<BR>Please return to Configuration and select Club Setup.");
           out.println("<BR><BR>If problem persists, contact customer support.");
           out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
           out.println("</CENTER></BODY></HTML>");
           return;
         }

         stmt.close();
      }
      catch (Exception ignore) {
      }

      course = req.getParameter("course");            // name of course to process
        
      //
      //  Go build the parms table
      //
      buildParms(req, resp, out, con, multi, course, club, posType, lottery, paceofplay); // build parm table and wait for Submit
      return;
   }

   if (req.getParameter("Remove") != null) {

      course = req.getParameter("course");            // name of course to process

      out.println(SystemUtils.HeadTitle("Proshop - Remove Course Confirmation Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

      out.println("<table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_parms\" method=\"post\" target=\"bot\">");
         out.println("<tr>");

            out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");

            out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<br>You have elected to Delete the " + course + " golf course from the database.<br>");
               out.println("<br><b>ARE YOU SURE YOU WANT TO DO THIS?</b>");
               out.println("</font></td>");

         out.println("</tr><tr>");
            out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<br>&nbsp;&nbsp;");
               out.println("<input type=\"submit\" name=\"Conf\" value=\"YES\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("<br></font></td>");
         out.println("</tr></form></table>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
      out.println("<td align=\"center\" width=\"200\">");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"No - Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font></td></tr></table>");

      out.println("</center></font></body></html>");
      return;
   }

   if (req.getParameter("Conf") != null) {         // Confirm a delete request

      course = req.getParameter("course");            // name of course to process

      //
      // Delete the entry from the parms table
      //
      try {

         PreparedStatement pstmt = con.prepareStatement (
                  "Delete FROM clubparm2 WHERE courseName = ?");

         pstmt.clearParameters();               // clear the parms
         pstmt.setString(1, course);            // put the parm in stmt
         count = pstmt.executeUpdate();         // execute the prepared stmt

         pstmt.close();
      }
      catch (Exception exc) {

      }

      //
      // Remove the tee sheets in teecurr table for Course Name  
      //
      try {

         PreparedStatement pstmt1 = con.prepareStatement (
           "DELETE FROM teecurr2 WHERE courseName = ?");

         pstmt1.clearParameters();               // clear the parms
         pstmt1.setString(1, course);            // put the parm in stmt
         count = pstmt1.executeUpdate();         // execute the prepared stmt

         pstmt1.close();
      }
      catch (Exception ignore) {

      }

      //
      //  inform the user that course has been deleted
      //
      out.println(SystemUtils.HeadTitle("Proshop - Remove Course Confirmation Page"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      SystemUtils.getProshopSubMenu(req, out, lottery);
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");

      out.println("<table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
         out.println("<tr>");
            out.println("<td>");
               out.println("<font size=\"2\">");
               out.println("<br>As requested, the " + course + " golf course was deleted from the database.<br>");
               out.println("</font></td>");
         out.println("</tr></table>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr>");
      out.println("<td align=\"center\" width=\"200\">");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</td></tr></table>");

      out.println("</center></font></body></html>");
      return;
   }

   if (req.getParameter("Submit") != null) {         // Process a request to add or change the parms

      //
      //  get club parms
      //
      try {
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT multi, paceofplay, posType FROM club5 WHERE clubName != ''");

         if (rs.next()) {

            multi = rs.getInt(1);
            paceofplay = rs.getInt(2);
            posType = rs.getString(3);
         }
         stmt.close();
      }
      catch (Exception ignore) {
      }

      //
      // Get all the parameters entered
      //
      course = "";
      courseid = "A";                                        // default for single course
        
      if (req.getParameter("courseid") != null) {

         courseid = req.getParameter("courseid");            // course id - Jonas
      }
        
      if (req.getParameter("course") != null) {      

         course = req.getParameter("course");            // course name

         //
         //  Course names cannot include special chars because of _jump - uri requirements
         //
         error = SystemUtils.scanName(course);           // check for special characters
           
         if (error == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>Special characters cannot be part of the course name.");
            out.println("<BR>You can only use the characters A-Z, a-z, 0-9 and space.");
            out.println("<BR>You entered:" + course);
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }

      sfirst_hr = req.getParameter("firsttee_hr");    // first tee time hr (05 - 09)
      sfirst_min = req.getParameter("firsttee_min");  // first tee time min (00 - 59)
      slast_hr = req.getParameter("lasttee_hr");      // last tee time hr (16 - 21)
      slast_min = req.getParameter("lasttee_min");    // last tee time min (00 - 59)
      smins = req.getParameter("mins_between");       // minutes between tee times (06 - 90)
      samins = req.getParameter("alt_mins_between");  // alt minutes between tee times (0 - 90)
      sfives = req.getParameter("fives");             // 5-somes (yes or no)
      oldName = req.getParameter("oldName");          // original name of course to process

      //
      //  Get modes of transportation
      //
      for (i=1; i<parm.MAX_Tmodes+1; i++) {
        
         if (req.getParameter("tmode" +i) != null) {
            tmodeA[i] = req.getParameter("tmode" +i);
         }
         if (req.getParameter("tmodea" +i) != null) {
            tmodeaA[i] = req.getParameter("tmodea" +i);
         }
         //
         //  Get POS system parms if specified
         //
         if (req.getParameter("tpos" +i) != null) {
            tposA[i] = req.getParameter("tpos" +i);
         }
         if (req.getParameter("t9pos" +i) != null) {
            t9posA[i] = req.getParameter("t9pos" +i);
         }
         if (req.getParameter("tposc" +i) != null) {
            tposcA[i] = req.getParameter("tposc" +i);
         }
         if (req.getParameter("t9posc" +i) != null) {
            t9poscA[i] = req.getParameter("t9posc" +i);
         }
         if (req.getParameter("tOpt" +i) != null) {
            if (req.getParameter("tOpt" +i).equals("1")) {
                tOptA[i] = 1;
            } else {
                tOptA[i] = 0;
            }
         }
      }

      fives = 0;                                 // 5 somes = no
        
      if (sfives.equalsIgnoreCase( "yes" )) {

         fives = 1;                              // 5 somes = yes
      }

      //
      // Convert the string parameters to Int's
      //
      try {
         first_hr = Integer.parseInt(sfirst_hr);
         first_min = Integer.parseInt(sfirst_min);
         last_hr = Integer.parseInt(slast_hr);
         last_min = Integer.parseInt(slast_min);
         mins = Integer.parseInt(smins);
         amins = Integer.parseInt(samins);
      }
      catch (NumberFormatException e) {
         // ignore error - let verify catch it
      }

      //
      //  get the current trans modes so we can determine if any changed (and grab the clubparm_id for this course for later use)
      //
      try {
         PreparedStatement pstmt = con.prepareStatement (
            "SELECT tmode1, tmode2, tmode3, tmode4, tmode5, tmode6, tmode7, tmode8, tmode9, tmode10, " + 
            "tmode11, tmode12, tmode13, tmode14, tmode15, tmode16, clubparm_id " + 
            "FROM clubparm2 WHERE courseName = ?");

         pstmt.clearParameters();            // clear the parms
         pstmt.setString(1, course);         // put the parm in stmt

         rs = pstmt.executeQuery();

         if (rs.next()) {

            parm.tmode[0] = rs.getString("tmode1");
            parm.tmode[1] = rs.getString("tmode2");
            parm.tmode[2] = rs.getString("tmode3");
            parm.tmode[3] = rs.getString("tmode4");
            parm.tmode[4] = rs.getString("tmode5");
            parm.tmode[5] = rs.getString("tmode6");
            parm.tmode[6] = rs.getString("tmode7");
            parm.tmode[7] = rs.getString("tmode8");
            parm.tmode[8] = rs.getString("tmode9");
            parm.tmode[9] = rs.getString("tmode10");
            parm.tmode[10] = rs.getString("tmode11");
            parm.tmode[11] = rs.getString("tmode12");
            parm.tmode[12] = rs.getString("tmode13");
            parm.tmode[13] = rs.getString("tmode14");
            parm.tmode[14] = rs.getString("tmode15");
            parm.tmode[15] = rs.getString("tmode16");
            clubparm_id = rs.getInt("clubparm_id");
              
         } else {

            for (i=0; i<parm.MAX_Tmodes; i++) {
              
               parm.tmode[i] = "";    // init to empty string
            }
         }
         pstmt.close();
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Verify the parameters
      //
      if ((first_hr < 5) || (first_hr > 9)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>First Tee Time Hour parameter must be in the range of 05 - 09.");
         out.println("<BR>You entered:" + first_hr);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((first_min < 0) || (first_min > 59)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>First Tee Time Minute parameter must be in the range of 00 - 59.");
         out.println("<BR>You entered:" + first_min);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((last_hr < 16) || (last_hr > 21)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Last Tee Time Hour parameter must be in the range of 04 - 09.");
         out.println("<BR>You entered:" + last_hr);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((last_min < 0) || (last_min > 59)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Last Tee Time Minute parameter must be in the range of 00 - 59.");
         out.println("<BR>You entered:" + last_min);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((mins < 3) || (mins > 90)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Minutes Between Tee Times parameter must be in the range of 3 - 90.");
         out.println("<BR>You entered:" + mins);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      if ((amins < 0) || (amins > 90)) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Alternate Minutes Between Tee Times parameter must be in the range of 0 - 90.");
         out.println("<BR>You entered:" + amins);
         out.println("<BR>Please try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
        
      if (posType.equals( "IBS" )) {         // if IBS, check for '$' in fee values (not allowed)
        
         error = false;

         for (i = 1; i < parm.MAX_Tmodes; i++) {                // check all tmodes

            if (tposcA[i].startsWith( "$" ) || t9poscA[i].startsWith( "$" )) {              

               error = true;
            }
         }

         if (error == true) {       // if duplicate acronyms found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
            out.println("<BR><BR>The $ (dollar Sign) is not necessary and will result in an error from the IBS POS System.");
            out.println("<BR>Please remove all dollar signs from the Fee values.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }

      //
      //  Check for duplicate modes of trans
      //
      error = false;
       
      for (i = 1; i < parm.MAX_Tmodes-1; i++) {                // do all but last tmode
        
         if (!tmodeaA[i].equals( "" )) {                       // if specified

            for (i2 = i+1; i2 < parm.MAX_Tmodes; i2++) {       // check all after this one

               if (tmodeaA[i].equals(tmodeaA[i2])) {

                  error = true;
               }
            }
         }
      }

      if (error == true) {       // if duplicate acronyms found

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
         out.println("<BR><BR>Acronyms for the Modes of Transportation must be unique.");
         out.println("<BR>Please correct this and try again.");
         out.println("<br><br><font size=\"2\">");
         out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
         out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
         out.println("</input></form></font>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      
        // get the benchmark pace times from the posted form
        boolean bad_pace = false;
        boolean bad_leeway = false;
        int tmp_pace = 0;
        int invert = 0;
        int [] hole = new int [19];
        String tmp_leeway = "";
        double pace_leeway_1 = 0;
        double pace_leeway_2 = 0;
            
                
        // only validate if paceofplay is enabled
        if ( paceofplay != 0 ) {
        
            for (i=1; i<19; i++) {

                if (req.getParameter("hole_" +i) != null) {

                    try {

                        tmp_pace = Integer.parseInt(req.getParameter("hole_" +i));
                    }
                    catch (NumberFormatException e) {
                        // ignore error
                    }

                    if (tmp_pace != 0) {
                        hole[i] = tmp_pace;
                    } else {
                        bad_pace = true;
                        break;
                    }

                } // end if parm present

            } // end for loop
            
            tmp_leeway = req.getParameter("pace_leeway_1");
            if (tmp_leeway == null || tmp_leeway.equals("")) {
                bad_leeway = true;
            } else {
                pace_leeway_1 = Double.parseDouble(tmp_leeway) * 100;
            }
            
            tmp_leeway = req.getParameter("pace_leeway_2");
            if (tmp_leeway == null || tmp_leeway.equals("")) {
                bad_leeway = true;
            } else {
                pace_leeway_2 = Double.parseDouble(tmp_leeway) * 100;
            }            

            pace_leeway_1 = Math.round(pace_leeway_1);
            pace_leeway_2 = Math.round(pace_leeway_2);

            pace_leeway_1 = pace_leeway_1 / 10000;
            pace_leeway_2 = pace_leeway_2 / 10000;
            
        } // end if paceofplay
      
        if (bad_pace == true || bad_leeway == true) {
            
            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Invalid Pace Benachmark Time Specified</H3>");
            out.println("<BR><BR>Valid pace times are 1-99 minutes.");
            if (bad_pace) {
                out.println("<BR>Hole " + i + " was give a time of " + tmp_pace + " minutes.");
            } else {
                out.println("<BR>Check your pace leeway percentages.  Use numbers and decimals only.");
            }
            out.println("<BR>Please try again.");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }
      
        
      //
      // ALL DATA PRESENT AND VALIDATED - SAVE TO DB
      // 
      try {
         
         PreparedStatement pstmt;
         
         //
         // Parms valid - add or update the parms in the database according to 'multiple course support'
         //  
         if (multi == 0) {

            //
            //   not multi - no names to worry about
            //
            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT first_hr, xx FROM clubparm2 WHERE first_hr != 0");

            if (rs.next()) {

               xx = rs.getShort("xx");            // get current 'build tees' value (retain it)

               //
               // Parms already exist - update them in the database
               //
               exist = 1;

               temp = rs.getInt("first_hr");

               PreparedStatement pstmt2 = con.prepareStatement (
                  "UPDATE clubparm2 SET first_hr = ?, first_min = ?, " +
                  "last_hr = ?, last_min = ?, betwn = ?, xx = ?, alt = ?, fives = ?, " +
                  "tmode1 = ?, tmodea1 = ?, tmode2 = ?, tmodea2 = ?, tmode3 = ?, tmodea3 = ?, tmode4 = ?, tmodea4 = ?, " +
                  "tmode5 = ?, tmodea5 = ?, tmode6 = ?, tmodea6 = ?, tmode7 = ?, tmodea7 = ?, tmode8 = ?, tmodea8 = ?, " +
                  "tmode9 = ?, tmodea9 = ?, tmode10 = ?, tmodea10 = ?, tmode11 = ?, tmodea11 = ?, tmode12 = ?, tmodea12 = ?, " +
                  "tmode13 = ?, tmodea13 = ?, tmode14 = ?, tmodea14 = ?, tmode15 = ?, tmodea15 = ?, tmode16 = ?, tmodea16 = ?, " +
                  "t9pos1 = ?, tpos1 = ?, t9pos2 = ?, tpos2 = ?, t9pos3 = ?, tpos3 = ?, t9pos4 = ?, tpos4 = ?, " +
                  "t9pos5 = ?, tpos5 = ?, t9pos6 = ?, tpos6 = ?, t9pos7 = ?, tpos7 = ?, t9pos8 = ?, tpos8 = ?, " +
                  "t9pos9 = ?, tpos9 = ?, t9pos10 = ?, tpos10 = ?, t9pos11 = ?, tpos11 = ?, t9pos12 = ?, tpos12 = ?, " +
                  "t9pos13 = ?, tpos13 = ?, t9pos14 = ?, tpos14 = ?, t9pos15 = ?, tpos15 = ?, t9pos16 = ?, tpos16 = ?, " +
                  "courseid = ?, " +
                  "t9posc1 = ?, tposc1 = ?, t9posc2 = ?, tposc2 = ?, t9posc3 = ?, tposc3 = ?, t9posc4 = ?, tposc4 = ?, " +
                  "t9posc5 = ?, tposc5 = ?, t9posc6 = ?, tposc6 = ?, t9posc7 = ?, tposc7 = ?, t9posc8 = ?, tposc8 = ?, " +
                  "t9posc9 = ?, tposc9 = ?, t9posc10 = ?, tposc10 = ?, t9posc11 = ?, tposc11 = ?, t9posc12 = ?, tposc12 = ?, " +
                  "t9posc13 = ?, tposc13 = ?, t9posc14 = ?, tposc14 = ?, t9posc15 = ?, tposc15 = ?, t9posc16 = ?, tposc16 = ?, " +
                  "tOpt1 = ?, tOpt2 = ?, tOpt3 = ?, tOpt4 = ?, tOpt5 = ?, tOpt6 = ?, tOpt7 = ?, tOpt8 = ?, tOpt9 = ?, tOpt10 = ?, " +
                  "tOpt11 = ?, tOpt12 = ?, tOpt13 = ?, tOpt14 = ?, tOpt15 = ?, tOpt16 = ? " +
                  "WHERE first_hr = ?");

               pstmt2.clearParameters();            // clear the parms
               pstmt2.setInt(1, first_hr);
               pstmt2.setInt(2, first_min);
               pstmt2.setInt(3, last_hr);
               pstmt2.setInt(4, last_min);
               pstmt2.setInt(5, mins);
               pstmt2.setInt(6, xx);
               pstmt2.setInt(7, amins);            // alt value
               pstmt2.setInt(8, fives);
               pstmt2.setString(9, tmodeA[1]);
               pstmt2.setString(10, tmodeaA[1]);
               pstmt2.setString(11, tmodeA[2]);
               pstmt2.setString(12, tmodeaA[2]);
               pstmt2.setString(13, tmodeA[3]);
               pstmt2.setString(14, tmodeaA[3]);
               pstmt2.setString(15, tmodeA[4]);
               pstmt2.setString(16, tmodeaA[4]);
               pstmt2.setString(17, tmodeA[5]);
               pstmt2.setString(18, tmodeaA[5]);
               pstmt2.setString(19, tmodeA[6]);
               pstmt2.setString(20, tmodeaA[6]);
               pstmt2.setString(21, tmodeA[7]);
               pstmt2.setString(22, tmodeaA[7]);
               pstmt2.setString(23, tmodeA[8]);
               pstmt2.setString(24, tmodeaA[8]);
               pstmt2.setString(25, tmodeA[9]);
               pstmt2.setString(26, tmodeaA[9]);
               pstmt2.setString(27, tmodeA[10]);
               pstmt2.setString(28, tmodeaA[10]);
               pstmt2.setString(29, tmodeA[11]);
               pstmt2.setString(30, tmodeaA[11]);
               pstmt2.setString(31, tmodeA[12]);
               pstmt2.setString(32, tmodeaA[12]);
               pstmt2.setString(33, tmodeA[13]);
               pstmt2.setString(34, tmodeaA[13]);
               pstmt2.setString(35, tmodeA[14]);
               pstmt2.setString(36, tmodeaA[14]);
               pstmt2.setString(37, tmodeA[15]);
               pstmt2.setString(38, tmodeaA[15]);
               pstmt2.setString(39, tmodeA[16]);
               pstmt2.setString(40, tmodeaA[16]);
               pstmt2.setString(41, t9posA[1]);
               pstmt2.setString(42, tposA[1]);
               pstmt2.setString(43, t9posA[2]);
               pstmt2.setString(44, tposA[2]);
               pstmt2.setString(45, t9posA[3]);
               pstmt2.setString(46, tposA[3]);
               pstmt2.setString(47, t9posA[4]);
               pstmt2.setString(48, tposA[4]);
               pstmt2.setString(49, t9posA[5]);
               pstmt2.setString(50, tposA[5]);
               pstmt2.setString(51, t9posA[6]);
               pstmt2.setString(52, tposA[6]);
               pstmt2.setString(53, t9posA[7]);
               pstmt2.setString(54, tposA[7]);
               pstmt2.setString(55, t9posA[8]);
               pstmt2.setString(56, tposA[8]);
               pstmt2.setString(57, t9posA[9]);
               pstmt2.setString(58, tposA[9]);
               pstmt2.setString(59, t9posA[10]);
               pstmt2.setString(60, tposA[10]);
               pstmt2.setString(61, t9posA[11]);
               pstmt2.setString(62, tposA[11]);
               pstmt2.setString(63, t9posA[12]);
               pstmt2.setString(64, tposA[12]);
               pstmt2.setString(65, t9posA[13]);
               pstmt2.setString(66, tposA[13]);
               pstmt2.setString(67, t9posA[14]);
               pstmt2.setString(68, tposA[14]);
               pstmt2.setString(69, t9posA[15]);
               pstmt2.setString(70, tposA[15]);
               pstmt2.setString(71, t9posA[16]);
               pstmt2.setString(72, tposA[16]);
               pstmt2.setString(73, courseid);
               pstmt2.setString(74, t9poscA[1]);
               pstmt2.setString(75, tposcA[1]);
               pstmt2.setString(76, t9poscA[2]);
               pstmt2.setString(77, tposcA[2]);
               pstmt2.setString(78, t9poscA[3]);
               pstmt2.setString(79, tposcA[3]);
               pstmt2.setString(80, t9poscA[4]);
               pstmt2.setString(81, tposcA[4]);
               pstmt2.setString(82, t9poscA[5]);
               pstmt2.setString(83, tposcA[5]);
               pstmt2.setString(84, t9poscA[6]);
               pstmt2.setString(85, tposcA[6]);
               pstmt2.setString(86, t9poscA[7]);
               pstmt2.setString(87, tposcA[7]);
               pstmt2.setString(88, t9poscA[8]);
               pstmt2.setString(89, tposcA[8]);
               pstmt2.setString(90, t9poscA[9]);
               pstmt2.setString(91, tposcA[9]);
               pstmt2.setString(92, t9poscA[10]);
               pstmt2.setString(93, tposcA[10]);
               pstmt2.setString(94, t9poscA[11]);
               pstmt2.setString(95, tposcA[11]);
               pstmt2.setString(96, t9poscA[12]);
               pstmt2.setString(97, tposcA[12]);
               pstmt2.setString(98, t9poscA[13]);
               pstmt2.setString(99, tposcA[13]);
               pstmt2.setString(100, t9poscA[14]);
               pstmt2.setString(101, tposcA[14]);
               pstmt2.setString(102, t9poscA[15]);
               pstmt2.setString(103, tposcA[15]);
               pstmt2.setString(104, t9poscA[16]);
               pstmt2.setString(105, tposcA[16]);
               pstmt2.setInt(106, tOptA[1]);
               pstmt2.setInt(107, tOptA[2]);
               pstmt2.setInt(108, tOptA[3]);
               pstmt2.setInt(109, tOptA[4]);
               pstmt2.setInt(110, tOptA[5]);
               pstmt2.setInt(111, tOptA[6]);
               pstmt2.setInt(112, tOptA[7]);
               pstmt2.setInt(113, tOptA[8]);
               pstmt2.setInt(114, tOptA[9]);
               pstmt2.setInt(115, tOptA[10]);
               pstmt2.setInt(116, tOptA[11]);
               pstmt2.setInt(117, tOptA[12]);
               pstmt2.setInt(118, tOptA[13]);
               pstmt2.setInt(119, tOptA[14]);
               pstmt2.setInt(120, tOptA[15]);
               pstmt2.setInt(121, tOptA[16]);
                 
               pstmt2.setInt(122, temp);
               count = pstmt2.executeUpdate();  // execute the prepared stmt

               pstmt2.close();

            } else {

               //
               // Parms do not exist - insert them in the database   note: (clubparm_id does not exist yet)
               //
               exist = 0;

               xx = 0;                         // set to wait to build tees (for config to complete)

               PreparedStatement pstmt3 = con.prepareStatement (
                  "INSERT INTO clubparm2 (" +
                  "courseName, first_hr, first_min, last_hr, last_min, betwn, xx, alt, fives, tmode1, " +       // 10 each line
                  "tmodea1, tmode2, tmodea2, tmode3, tmodea3, tmode4, tmodea4, tmode5, tmodea5, tmode6, " +
                  "tmodea6, tmode7, tmodea7, tmode8, tmodea8, tmode9, tmodea9, tmode10, tmodea10, tmode11, " +
                  "tmodea11, tmode12, tmodea12, tmode13, tmodea13, tmode14, tmodea14, tmode15, tmodea15, tmode16, " +
                  "tmodea16, t9pos1, tpos1, t9pos2, tpos2, t9pos3, tpos3, t9pos4, tpos4, t9pos5, " +
                  "tpos5, t9pos6, tpos6, t9pos7, tpos7, t9pos8, tpos8, t9pos9, tpos9, t9pos10, " +
                  "tpos10, t9pos11, tpos11, t9pos12, tpos12, t9pos13, tpos13, t9pos14, tpos14, t9pos15, " +
                  "tpos15, t9pos16, tpos16, courseid, t9posc1, tposc1, t9posc2, tposc2, t9posc3, tposc3, " +
                  "t9posc4, tposc4, t9posc5, tposc5, t9posc6, tposc6, t9posc7, tposc7, t9posc8, tposc8, " +
                  "t9posc9, tposc9, t9posc10, tposc10, t9posc11, tposc11, t9posc12, tposc12, t9posc13, tposc13, " +
                  "t9posc14, tposc14, t9posc15, tposc15, t9posc16, tposc16, tOpt1, tOpt2, tOpt3, tOpt4, " +
                  "tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, " +
                  "tOpt15, tOpt16) " +
                  "VALUES (" +
                  "?,?,?,?,?,?,?,?,?,?, " +         // 10 each line
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?,?,?,?,?,?,?,?,?, " +
                  "?,?)");

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setString(1, course);       // put the parm in stmt
               pstmt3.setInt(2, first_hr);
               pstmt3.setInt(3, first_min);
               pstmt3.setInt(4, last_hr);
               pstmt3.setInt(5, last_min);
               pstmt3.setInt(6, mins);
               pstmt3.setInt(7, xx);
               pstmt3.setInt(8, amins);
               pstmt3.setInt(9, fives);
               pstmt3.setString(10, tmodeA[1]);
               pstmt3.setString(11, tmodeaA[1]);
               pstmt3.setString(12, tmodeA[2]);
               pstmt3.setString(13, tmodeaA[2]);
               pstmt3.setString(14, tmodeA[3]);
               pstmt3.setString(15, tmodeaA[3]);
               pstmt3.setString(16, tmodeA[4]);
               pstmt3.setString(17, tmodeaA[4]);
               pstmt3.setString(18, tmodeA[5]);
               pstmt3.setString(19, tmodeaA[5]);
               pstmt3.setString(20, tmodeA[6]);
               pstmt3.setString(21, tmodeaA[6]);
               pstmt3.setString(22, tmodeA[7]);
               pstmt3.setString(23, tmodeaA[7]);
               pstmt3.setString(24, tmodeA[8]);
               pstmt3.setString(25, tmodeaA[8]);
               pstmt3.setString(26, tmodeA[9]);
               pstmt3.setString(27, tmodeaA[9]);
               pstmt3.setString(28, tmodeA[10]);
               pstmt3.setString(29, tmodeaA[10]);
               pstmt3.setString(30, tmodeA[11]);
               pstmt3.setString(31, tmodeaA[11]);
               pstmt3.setString(32, tmodeA[12]);
               pstmt3.setString(33, tmodeaA[12]);
               pstmt3.setString(34, tmodeA[13]);
               pstmt3.setString(35, tmodeaA[13]);
               pstmt3.setString(36, tmodeA[14]);
               pstmt3.setString(37, tmodeaA[14]);
               pstmt3.setString(38, tmodeA[15]);
               pstmt3.setString(39, tmodeaA[15]);
               pstmt3.setString(40, tmodeA[16]);
               pstmt3.setString(41, tmodeaA[16]);
               pstmt3.setString(42, t9posA[1]);
               pstmt3.setString(43, tposA[1]);
               pstmt3.setString(44, t9posA[2]);
               pstmt3.setString(45, tposA[2]);
               pstmt3.setString(46, t9posA[3]);
               pstmt3.setString(47, tposA[3]);
               pstmt3.setString(48, t9posA[4]);
               pstmt3.setString(49, tposA[4]);
               pstmt3.setString(50, t9posA[5]);
               pstmt3.setString(51, tposA[5]);
               pstmt3.setString(52, t9posA[6]);
               pstmt3.setString(53, tposA[6]);
               pstmt3.setString(54, t9posA[7]);
               pstmt3.setString(55, tposA[7]);
               pstmt3.setString(56, t9posA[8]);
               pstmt3.setString(57, tposA[8]);
               pstmt3.setString(58, t9posA[9]);
               pstmt3.setString(59, tposA[9]);
               pstmt3.setString(60, t9posA[10]);
               pstmt3.setString(61, tposA[10]);
               pstmt3.setString(62, t9posA[11]);
               pstmt3.setString(63, tposA[11]);
               pstmt3.setString(64, t9posA[12]);
               pstmt3.setString(65, tposA[12]);
               pstmt3.setString(66, t9posA[13]);
               pstmt3.setString(67, tposA[13]);
               pstmt3.setString(68, t9posA[14]);
               pstmt3.setString(69, tposA[14]);
               pstmt3.setString(70, t9posA[15]);
               pstmt3.setString(71, tposA[15]);
               pstmt3.setString(72, t9posA[16]);
               pstmt3.setString(73, tposA[16]);
               pstmt3.setString(74, courseid);
               pstmt3.setString(75, t9poscA[1]);
               pstmt3.setString(76, tposcA[1]);
               pstmt3.setString(77, t9poscA[2]);
               pstmt3.setString(78, tposcA[2]);
               pstmt3.setString(79, t9poscA[3]);
               pstmt3.setString(80, tposcA[3]);
               pstmt3.setString(81, t9poscA[4]);
               pstmt3.setString(82, tposcA[4]);
               pstmt3.setString(83, t9poscA[5]);
               pstmt3.setString(84, tposcA[5]);
               pstmt3.setString(85, t9poscA[6]);
               pstmt3.setString(86, tposcA[6]);
               pstmt3.setString(87, t9poscA[7]);
               pstmt3.setString(88, tposcA[7]);
               pstmt3.setString(89, t9poscA[8]);
               pstmt3.setString(90, tposcA[8]);
               pstmt3.setString(91, t9poscA[9]);
               pstmt3.setString(92, tposcA[9]);
               pstmt3.setString(93, t9poscA[10]);
               pstmt3.setString(94, tposcA[10]);
               pstmt3.setString(95, t9poscA[11]);
               pstmt3.setString(96, tposcA[11]);
               pstmt3.setString(97, t9poscA[12]);
               pstmt3.setString(98, tposcA[12]);
               pstmt3.setString(99, t9poscA[13]);
               pstmt3.setString(100, tposcA[13]);
               pstmt3.setString(101, t9poscA[14]);
               pstmt3.setString(102, tposcA[14]);
               pstmt3.setString(103, t9poscA[15]);
               pstmt3.setString(104, tposcA[15]);
               pstmt3.setString(105, t9poscA[16]);
               pstmt3.setString(106, tposcA[16]);
               pstmt3.setInt(107, tOptA[1]);
               pstmt3.setInt(108, tOptA[2]);
               pstmt3.setInt(109, tOptA[3]);
               pstmt3.setInt(110, tOptA[4]);
               pstmt3.setInt(111, tOptA[5]);
               pstmt3.setInt(112, tOptA[6]);
               pstmt3.setInt(113, tOptA[7]);
               pstmt3.setInt(114, tOptA[8]);
               pstmt3.setInt(115, tOptA[9]);
               pstmt3.setInt(116, tOptA[10]);
               pstmt3.setInt(117, tOptA[11]);
               pstmt3.setInt(118, tOptA[12]);
               pstmt3.setInt(119, tOptA[13]);
               pstmt3.setInt(120, tOptA[14]);
               pstmt3.setInt(121, tOptA[15]);
               pstmt3.setInt(122, tOptA[16]);
                 
               pstmt3.executeUpdate();          // execute the prepared stmt

               pstmt3.close();
               
               clubparm_id = SystemUtils.getClubParmIdFromCourseName(course, con);
            }

            stmt.close();

         } else {
            
            //
            //  Multi Course request - must check new name and old name values
            //
            //     If oldName is null, then request is either for a new course or
            //                              its replacing the course that existed when multi=no
            //
            //     Else - we are updating an existing course - must check if name has changed
            //
            if (oldName.equals( "" )) {
   
               //
               //  First, check if course already exists with this name
               //
               PreparedStatement pstmt4a = con.prepareStatement (
                      "SELECT xx FROM clubparm2 WHERE courseName = ?"); 

               pstmt4a.clearParameters();            // clear the parms
               pstmt4a.setString(1, course);
               rs = pstmt4a.executeQuery();

               if (rs.next()) {

                  out.println(SystemUtils.HeadTitle("Database Error"));
                  out.println("<BODY><CENTER>");
                  out.println("<BR><BR><H3>Procedure Error</H3>");
                  out.println("<BR><BR>The course name that you entered already exists.");
                  out.println("<BR>Please try again.");
                  out.println("<BR><BR>If problem persists, contact customer support.");
                  out.println("<font size=\"2\">");
                  out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
                  out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</input></form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  return;
               }
               pstmt4a.close();

               //
               //  Now check if name has changed
               //
               stmt = con.createStatement();        // create a statement

               rs = stmt.executeQuery("SELECT first_hr, xx FROM clubparm2 WHERE first_hr != 0 AND courseName = ''");

               if (rs.next()) {

                  temp = rs.getInt("first_hr");
                  xx = rs.getShort("xx");            // get current 'build tees' value (retain it)

                  //
                  // Parms already exist (was a single course club) - update them in the database
                  //
                  exist = 1;

                  PreparedStatement pstmt4 = con.prepareStatement (
                     "UPDATE clubparm2 SET courseName = ?, first_hr = ?, first_min = ?, " +
                     "last_hr = ?, last_min = ?, betwn = ?, xx = ?, alt = ?, fives = ?, " +
                     "tmode1 = ?, tmodea1 = ?, tmode2 = ?, tmodea2 = ?, tmode3 = ?, tmodea3 = ?, tmode4 = ?, tmodea4 = ?, " +
                     "tmode5 = ?, tmodea5 = ?, tmode6 = ?, tmodea6 = ?, tmode7 = ?, tmodea7 = ?, tmode8 = ?, tmodea8 = ?, " +
                     "tmode9 = ?, tmodea9 = ?, tmode10 = ?, tmodea10 = ?, tmode11 = ?, tmodea11 = ?, tmode12 = ?, tmodea12 = ?, " +
                     "tmode13 = ?, tmodea13 = ?, tmode14 = ?, tmodea14 = ?, tmode15 = ?, tmodea15 = ?, tmode16 = ?, tmodea16 = ?, " +
                     "t9pos1 = ?, tpos1 = ?, t9pos2 = ?, tpos2 = ?, t9pos3 = ?, tpos3 = ?, t9pos4 = ?, tpos4 = ?, " +
                     "t9pos5 = ?, tpos5 = ?, t9pos6 = ?, tpos6 = ?, t9pos7 = ?, tpos7 = ?, t9pos8 = ?, tpos8 = ?, " +
                     "t9pos9 = ?, tpos9 = ?, t9pos10 = ?, tpos10 = ?, t9pos11 = ?, tpos11 = ?, t9pos12 = ?, tpos12 = ?, " +
                     "t9pos13 = ?, tpos13 = ?, t9pos14 = ?, tpos14 = ?, t9pos15 = ?, tpos15 = ?, t9pos16 = ?, tpos16 = ?, " +
                     "courseid = ?, " +
                     "t9posc1 = ?, tposc1 = ?, t9posc2 = ?, tposc2 = ?, t9posc3 = ?, tposc3 = ?, t9posc4 = ?, tposc4 = ?, " +
                     "t9posc5 = ?, tposc5 = ?, t9posc6 = ?, tposc6 = ?, t9posc7 = ?, tposc7 = ?, t9posc8 = ?, tposc8 = ?, " +
                     "t9posc9 = ?, tposc9 = ?, t9posc10 = ?, tposc10 = ?, t9posc11 = ?, tposc11 = ?, t9posc12 = ?, tposc12 = ?, " +
                     "t9posc13 = ?, tposc13 = ?, t9posc14 = ?, tposc14 = ?, t9posc15 = ?, tposc15 = ?, t9posc16 = ?, tposc16 = ?, " +
                     "tOpt1 = ?, tOpt2 = ?, tOpt3 = ?, tOpt4 = ?, tOpt5 = ?, tOpt6 = ?, tOpt7 = ?, tOpt8 = ?, tOpt9 = ?, tOpt10 = ?, " +
                     "tOpt11 = ?, tOpt12 = ?, tOpt13 = ?, tOpt14 = ?, tOpt15 = ?, tOpt16 = ? " +
                     "WHERE first_hr = ?");

                  pstmt4.clearParameters();            // clear the parms
                  pstmt4.setString(1, course);
                  pstmt4.setInt(2, first_hr);
                  pstmt4.setInt(3, first_min);
                  pstmt4.setInt(4, last_hr);
                  pstmt4.setInt(5, last_min);
                  pstmt4.setInt(6, mins);
                  pstmt4.setInt(7, xx);
                  pstmt4.setInt(8, amins);            // alt value
                  pstmt4.setInt(9, fives);
                  pstmt4.setString(10, tmodeA[1]);
                  pstmt4.setString(11, tmodeaA[1]);
                  pstmt4.setString(12, tmodeA[2]);
                  pstmt4.setString(13, tmodeaA[2]);
                  pstmt4.setString(14, tmodeA[3]);
                  pstmt4.setString(15, tmodeaA[3]);
                  pstmt4.setString(16, tmodeA[4]);
                  pstmt4.setString(17, tmodeaA[4]);
                  pstmt4.setString(18, tmodeA[5]);
                  pstmt4.setString(19, tmodeaA[5]);
                  pstmt4.setString(20, tmodeA[6]);
                  pstmt4.setString(21, tmodeaA[6]);
                  pstmt4.setString(22, tmodeA[7]);
                  pstmt4.setString(23, tmodeaA[7]);
                  pstmt4.setString(24, tmodeA[8]);
                  pstmt4.setString(25, tmodeaA[8]);
                  pstmt4.setString(26, tmodeA[9]);
                  pstmt4.setString(27, tmodeaA[9]);
                  pstmt4.setString(28, tmodeA[10]);
                  pstmt4.setString(29, tmodeaA[10]);
                  pstmt4.setString(30, tmodeA[11]);
                  pstmt4.setString(31, tmodeaA[11]);
                  pstmt4.setString(32, tmodeA[12]);
                  pstmt4.setString(33, tmodeaA[12]);
                  pstmt4.setString(34, tmodeA[13]);
                  pstmt4.setString(35, tmodeaA[13]);
                  pstmt4.setString(36, tmodeA[14]);
                  pstmt4.setString(37, tmodeaA[14]);
                  pstmt4.setString(38, tmodeA[15]);
                  pstmt4.setString(39, tmodeaA[15]);
                  pstmt4.setString(40, tmodeA[16]);
                  pstmt4.setString(41, tmodeaA[16]);
                  pstmt4.setString(42, t9posA[1]);
                  pstmt4.setString(43, tposA[1]);
                  pstmt4.setString(44, t9posA[2]);
                  pstmt4.setString(45, tposA[2]);
                  pstmt4.setString(46, t9posA[3]);
                  pstmt4.setString(47, tposA[3]);
                  pstmt4.setString(48, t9posA[4]);
                  pstmt4.setString(49, tposA[4]);
                  pstmt4.setString(50, t9posA[5]);
                  pstmt4.setString(51, tposA[5]);
                  pstmt4.setString(52, t9posA[6]);
                  pstmt4.setString(53, tposA[6]);
                  pstmt4.setString(54, t9posA[7]);
                  pstmt4.setString(55, tposA[7]);
                  pstmt4.setString(56, t9posA[8]);
                  pstmt4.setString(57, tposA[8]);
                  pstmt4.setString(58, t9posA[9]);
                  pstmt4.setString(59, tposA[9]);
                  pstmt4.setString(60, t9posA[10]);
                  pstmt4.setString(61, tposA[10]);
                  pstmt4.setString(62, t9posA[11]);
                  pstmt4.setString(63, tposA[11]);
                  pstmt4.setString(64, t9posA[12]);
                  pstmt4.setString(65, tposA[12]);
                  pstmt4.setString(66, t9posA[13]);
                  pstmt4.setString(67, tposA[13]);
                  pstmt4.setString(68, t9posA[14]);
                  pstmt4.setString(69, tposA[14]);
                  pstmt4.setString(70, t9posA[15]);
                  pstmt4.setString(71, tposA[15]);
                  pstmt4.setString(72, t9posA[16]);
                  pstmt4.setString(73, tposA[16]);
                  pstmt4.setString(74, courseid);
                  pstmt4.setString(75, t9poscA[1]);
                  pstmt4.setString(76, tposcA[1]);
                  pstmt4.setString(77, t9poscA[2]);
                  pstmt4.setString(78, tposcA[2]);
                  pstmt4.setString(79, t9poscA[3]);
                  pstmt4.setString(80, tposcA[3]);
                  pstmt4.setString(81, t9poscA[4]);
                  pstmt4.setString(82, tposcA[4]);
                  pstmt4.setString(83, t9poscA[5]);
                  pstmt4.setString(84, tposcA[5]);
                  pstmt4.setString(85, t9poscA[6]);
                  pstmt4.setString(86, tposcA[6]);
                  pstmt4.setString(87, t9poscA[7]);
                  pstmt4.setString(88, tposcA[7]);
                  pstmt4.setString(89, t9poscA[8]);
                  pstmt4.setString(90, tposcA[8]);
                  pstmt4.setString(91, t9poscA[9]);
                  pstmt4.setString(92, tposcA[9]);
                  pstmt4.setString(93, t9poscA[10]);
                  pstmt4.setString(94, tposcA[10]);
                  pstmt4.setString(95, t9poscA[11]);
                  pstmt4.setString(96, tposcA[11]);
                  pstmt4.setString(97, t9poscA[12]);
                  pstmt4.setString(98, tposcA[12]);
                  pstmt4.setString(99, t9poscA[13]);
                  pstmt4.setString(100, tposcA[13]);
                  pstmt4.setString(101, t9poscA[14]);
                  pstmt4.setString(102, tposcA[14]);
                  pstmt4.setString(103, t9poscA[15]);
                  pstmt4.setString(104, tposcA[15]);
                  pstmt4.setString(105, t9poscA[16]);
                  pstmt4.setString(106, tposcA[16]);
                  pstmt4.setInt(107, tOptA[1]);
                  pstmt4.setInt(108, tOptA[2]);
                  pstmt4.setInt(109, tOptA[3]);
                  pstmt4.setInt(110, tOptA[4]);
                  pstmt4.setInt(111, tOptA[5]);
                  pstmt4.setInt(112, tOptA[6]);
                  pstmt4.setInt(113, tOptA[7]);
                  pstmt4.setInt(114, tOptA[8]);
                  pstmt4.setInt(115, tOptA[9]);
                  pstmt4.setInt(116, tOptA[10]);
                  pstmt4.setInt(117, tOptA[11]);
                  pstmt4.setInt(118, tOptA[12]);
                  pstmt4.setInt(119, tOptA[13]);
                  pstmt4.setInt(120, tOptA[14]);
                  pstmt4.setInt(121, tOptA[15]);
                  pstmt4.setInt(122, tOptA[16]);

                  pstmt4.setInt(123, temp);
                  count = pstmt4.executeUpdate();  // execute the prepared stmt

                  pstmt4.close();

                  //
                  //  Now change all tee times to reflect the new course name
                  //
                  PreparedStatement pstmt4b = con.prepareStatement (
                    "UPDATE teecurr2 SET courseName = ? WHERE courseName = ?");

                  pstmt4b.clearParameters();               // clear the parms
                  pstmt4b.setString(1,course);
                  pstmt4b.setString(2,oldName);
                  count = pstmt4b.executeUpdate();         // execute the prepared stmt

                  pstmt4b.close();

               } else {

                  //
                  // Parms with null name does not exist - insert them in the database - new course
                  //
                  exist = 0;

                  xx = 0;                         // set to wait to build tees (for config to complete)

                  PreparedStatement pstmt5 = con.prepareStatement (
                     "INSERT INTO clubparm2 (" +
                     "courseName, first_hr, first_min, last_hr, last_min, betwn, xx, alt, fives, tmode1, " +        // 10 each line
                     "tmodea1, tmode2, tmodea2, tmode3, tmodea3, tmode4, tmodea4, tmode5, tmodea5, tmode6, " +
                     "tmodea6, tmode7, tmodea7, tmode8, tmodea8, tmode9, tmodea9, tmode10, tmodea10, tmode11, " +
                     "tmodea11, tmode12, tmodea12, tmode13, tmodea13, tmode14, tmodea14, tmode15, tmodea15, tmode16, " +
                     "tmodea16, t9pos1, tpos1, t9pos2, tpos2, t9pos3, tpos3, t9pos4, tpos4, t9pos5, " +
                     "tpos5, t9pos6, tpos6, t9pos7, tpos7, t9pos8, tpos8, t9pos9, tpos9, t9pos10, " +
                     "tpos10, t9pos11, tpos11, t9pos12, tpos12, t9pos13, tpos13, t9pos14, tpos14, t9pos15, " +
                     "tpos15, t9pos16, tpos16, courseid, t9posc1, tposc1, t9posc2, tposc2, t9posc3, tposc3, " +
                     "t9posc4, tposc4, t9posc5, tposc5, t9posc6, tposc6, t9posc7, tposc7, t9posc8, tposc8, " +
                     "t9posc9, tposc9, t9posc10, tposc10, t9posc11, tposc11, t9posc12, tposc12, t9posc13, tposc13, " +
                     "t9posc14, tposc14, t9posc15, tposc15, t9posc16, tposc16, tOpt1, tOpt2, tOpt3, tOpt4, " +
                     "tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, " +
                     "tOpt15, tOpt16) " +
                     "VALUES (" +
                     "?,?,?,?,?,?,?,?,?,?, " +          // 10 each line
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?,?,?,?,?,?,?,?,?, " +
                     "?,?)");

                  pstmt5.clearParameters();        // clear the parms
                  pstmt5.setString(1, course);       // put the parm in stmt
                  pstmt5.setInt(2, first_hr);
                  pstmt5.setInt(3, first_min);
                  pstmt5.setInt(4, last_hr);
                  pstmt5.setInt(5, last_min);
                  pstmt5.setInt(6, mins);
                  pstmt5.setInt(7, xx);
                  pstmt5.setInt(8, amins);
                  pstmt5.setInt(9, fives);
                  pstmt5.setString(10, tmodeA[1]);
                  pstmt5.setString(11, tmodeaA[1]);
                  pstmt5.setString(12, tmodeA[2]);
                  pstmt5.setString(13, tmodeaA[2]);
                  pstmt5.setString(14, tmodeA[3]);
                  pstmt5.setString(15, tmodeaA[3]);
                  pstmt5.setString(16, tmodeA[4]);
                  pstmt5.setString(17, tmodeaA[4]);
                  pstmt5.setString(18, tmodeA[5]);
                  pstmt5.setString(19, tmodeaA[5]);
                  pstmt5.setString(20, tmodeA[6]);
                  pstmt5.setString(21, tmodeaA[6]);
                  pstmt5.setString(22, tmodeA[7]);
                  pstmt5.setString(23, tmodeaA[7]);
                  pstmt5.setString(24, tmodeA[8]);
                  pstmt5.setString(25, tmodeaA[8]);
                  pstmt5.setString(26, tmodeA[9]);
                  pstmt5.setString(27, tmodeaA[9]);
                  pstmt5.setString(28, tmodeA[10]);
                  pstmt5.setString(29, tmodeaA[10]);
                  pstmt5.setString(30, tmodeA[11]);
                  pstmt5.setString(31, tmodeaA[11]);
                  pstmt5.setString(32, tmodeA[12]);
                  pstmt5.setString(33, tmodeaA[12]);
                  pstmt5.setString(34, tmodeA[13]);
                  pstmt5.setString(35, tmodeaA[13]);
                  pstmt5.setString(36, tmodeA[14]);
                  pstmt5.setString(37, tmodeaA[14]);
                  pstmt5.setString(38, tmodeA[15]);
                  pstmt5.setString(39, tmodeaA[15]);
                  pstmt5.setString(40, tmodeA[16]);
                  pstmt5.setString(41, tmodeaA[16]);
                  pstmt5.setString(42, t9posA[1]);
                  pstmt5.setString(43, tposA[1]);
                  pstmt5.setString(44, t9posA[2]);
                  pstmt5.setString(45, tposA[2]);
                  pstmt5.setString(46, t9posA[3]);
                  pstmt5.setString(47, tposA[3]);
                  pstmt5.setString(48, t9posA[4]);
                  pstmt5.setString(49, tposA[4]);
                  pstmt5.setString(50, t9posA[5]);
                  pstmt5.setString(51, tposA[5]);
                  pstmt5.setString(52, t9posA[6]);
                  pstmt5.setString(53, tposA[6]);
                  pstmt5.setString(54, t9posA[7]);
                  pstmt5.setString(55, tposA[7]);
                  pstmt5.setString(56, t9posA[8]);
                  pstmt5.setString(57, tposA[8]);
                  pstmt5.setString(58, t9posA[9]);
                  pstmt5.setString(59, tposA[9]);
                  pstmt5.setString(60, t9posA[10]);
                  pstmt5.setString(61, tposA[10]);
                  pstmt5.setString(62, t9posA[11]);
                  pstmt5.setString(63, tposA[11]);
                  pstmt5.setString(64, t9posA[12]);
                  pstmt5.setString(65, tposA[12]);
                  pstmt5.setString(66, t9posA[13]);
                  pstmt5.setString(67, tposA[13]);
                  pstmt5.setString(68, t9posA[14]);
                  pstmt5.setString(69, tposA[14]);
                  pstmt5.setString(70, t9posA[15]);
                  pstmt5.setString(71, tposA[15]);
                  pstmt5.setString(72, t9posA[16]);
                  pstmt5.setString(73, tposA[16]);
                  pstmt5.setString(74, courseid);
                  pstmt5.setString(75, t9poscA[1]);
                  pstmt5.setString(76, tposcA[1]);
                  pstmt5.setString(77, t9poscA[2]);
                  pstmt5.setString(78, tposcA[2]);
                  pstmt5.setString(79, t9poscA[3]);
                  pstmt5.setString(80, tposcA[3]);
                  pstmt5.setString(81, t9poscA[4]);
                  pstmt5.setString(82, tposcA[4]);
                  pstmt5.setString(83, t9poscA[5]);
                  pstmt5.setString(84, tposcA[5]);
                  pstmt5.setString(85, t9poscA[6]);
                  pstmt5.setString(86, tposcA[6]);
                  pstmt5.setString(87, t9poscA[7]);
                  pstmt5.setString(88, tposcA[7]);
                  pstmt5.setString(89, t9poscA[8]);
                  pstmt5.setString(90, tposcA[8]);
                  pstmt5.setString(91, t9poscA[9]);
                  pstmt5.setString(92, tposcA[9]);
                  pstmt5.setString(93, t9poscA[10]);
                  pstmt5.setString(94, tposcA[10]);
                  pstmt5.setString(95, t9poscA[11]);
                  pstmt5.setString(96, tposcA[11]);
                  pstmt5.setString(97, t9poscA[12]);
                  pstmt5.setString(98, tposcA[12]);
                  pstmt5.setString(99, t9poscA[13]);
                  pstmt5.setString(100, tposcA[13]);
                  pstmt5.setString(101, t9poscA[14]);
                  pstmt5.setString(102, tposcA[14]);
                  pstmt5.setString(103, t9poscA[15]);
                  pstmt5.setString(104, tposcA[15]);
                  pstmt5.setString(105, t9poscA[16]);
                  pstmt5.setString(106, tposcA[16]);
                  pstmt5.setInt(107, tOptA[1]);
                  pstmt5.setInt(108, tOptA[2]);
                  pstmt5.setInt(109, tOptA[3]);
                  pstmt5.setInt(110, tOptA[4]);
                  pstmt5.setInt(111, tOptA[5]);
                  pstmt5.setInt(112, tOptA[6]);
                  pstmt5.setInt(113, tOptA[7]);
                  pstmt5.setInt(114, tOptA[8]);
                  pstmt5.setInt(115, tOptA[9]);
                  pstmt5.setInt(116, tOptA[10]);
                  pstmt5.setInt(117, tOptA[11]);
                  pstmt5.setInt(118, tOptA[12]);
                  pstmt5.setInt(119, tOptA[13]);
                  pstmt5.setInt(120, tOptA[14]);
                  pstmt5.setInt(121, tOptA[15]);
                  pstmt5.setInt(122, tOptA[16]);
                    
                  pstmt5.executeUpdate();          // execute the prepared stmt

                  pstmt5.close();
                  
                  clubparm_id = SystemUtils.getClubParmIdFromCourseName(course, con);
               }
  
               stmt.close();

            } else {
               //
               //  We are updating an existing course - set new name if name has changed
               //
               PreparedStatement pstmt6a = con.prepareStatement (
                      "SELECT xx FROM clubparm2 WHERE courseName = ?");

               pstmt6a.clearParameters();            // clear the parms
               pstmt6a.setString(1, oldName);
               rs = pstmt6a.executeQuery();

               if (rs.next()) {

                  xx = rs.getShort("xx");            // get current 'build tees' value (retain it)
               }
               pstmt6a.close();

               //
               //   Set new parms
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "UPDATE clubparm2 SET courseName = ?, first_hr = ?, first_min = ?, " +
                  "last_hr = ?, last_min = ?, betwn = ?, xx = ?, alt = ?, fives = ?, " +
                  "tmode1 = ?, tmodea1 = ?, tmode2 = ?, tmodea2 = ?, tmode3 = ?, tmodea3 = ?, tmode4 = ?, tmodea4 = ?, " +
                  "tmode5 = ?, tmodea5 = ?, tmode6 = ?, tmodea6 = ?, tmode7 = ?, tmodea7 = ?, tmode8 = ?, tmodea8 = ?, " +
                  "tmode9 = ?, tmodea9 = ?, tmode10 = ?, tmodea10 = ?, tmode11 = ?, tmodea11 = ?, tmode12 = ?, tmodea12 = ?, " +
                  "tmode13 = ?, tmodea13 = ?, tmode14 = ?, tmodea14 = ?, tmode15 = ?, tmodea15 = ?, tmode16 = ?, tmodea16 = ?, " +
                  "t9pos1 = ?, tpos1 = ?, t9pos2 = ?, tpos2 = ?, t9pos3 = ?, tpos3 = ?, t9pos4 = ?, tpos4 = ?, " +
                  "t9pos5 = ?, tpos5 = ?, t9pos6 = ?, tpos6 = ?, t9pos7 = ?, tpos7 = ?, t9pos8 = ?, tpos8 = ?, " +
                  "t9pos9 = ?, tpos9 = ?, t9pos10 = ?, tpos10 = ?, t9pos11 = ?, tpos11 = ?, t9pos12 = ?, tpos12 = ?, " +
                  "t9pos13 = ?, tpos13 = ?, t9pos14 = ?, tpos14 = ?, t9pos15 = ?, tpos15 = ?, t9pos16 = ?, tpos16 = ?, " +
                  "courseid = ?, " +
                  "t9posc1 = ?, tposc1 = ?, t9posc2 = ?, tposc2 = ?, t9posc3 = ?, tposc3 = ?, t9posc4 = ?, tposc4 = ?, " +
                  "t9posc5 = ?, tposc5 = ?, t9posc6 = ?, tposc6 = ?, t9posc7 = ?, tposc7 = ?, t9posc8 = ?, tposc8 = ?, " +
                  "t9posc9 = ?, tposc9 = ?, t9posc10 = ?, tposc10 = ?, t9posc11 = ?, tposc11 = ?, t9posc12 = ?, tposc12 = ?, " +
                  "t9posc13 = ?, tposc13 = ?, t9posc14 = ?, tposc14 = ?, t9posc15 = ?, tposc15 = ?, t9posc16 = ?, tposc16 = ?, " +
                  "tOpt1 = ?, tOpt2 = ?, tOpt3 = ?, tOpt4 = ?, tOpt5 = ?, tOpt6 = ?, tOpt7 = ?, tOpt8 = ?, tOpt9 = ?, tOpt10 = ?, " +
                  "tOpt11 = ?, tOpt12 = ?, tOpt13 = ?, tOpt14 = ?, tOpt15 = ?, tOpt16 = ? " +
                  "WHERE courseName = ?");

               pstmt6.clearParameters();            // clear the parms
               pstmt6.setString(1, course);
               pstmt6.setInt(2, first_hr);
               pstmt6.setInt(3, first_min);
               pstmt6.setInt(4, last_hr);
               pstmt6.setInt(5, last_min);
               pstmt6.setInt(6, mins);
               pstmt6.setInt(7, xx);
               pstmt6.setInt(8, amins);            // alt value
               pstmt6.setInt(9, fives);
               pstmt6.setString(10, tmodeA[1]);
               pstmt6.setString(11, tmodeaA[1]);
               pstmt6.setString(12, tmodeA[2]);
               pstmt6.setString(13, tmodeaA[2]);
               pstmt6.setString(14, tmodeA[3]);
               pstmt6.setString(15, tmodeaA[3]);
               pstmt6.setString(16, tmodeA[4]);
               pstmt6.setString(17, tmodeaA[4]);
               pstmt6.setString(18, tmodeA[5]);
               pstmt6.setString(19, tmodeaA[5]);
               pstmt6.setString(20, tmodeA[6]);
               pstmt6.setString(21, tmodeaA[6]);
               pstmt6.setString(22, tmodeA[7]);
               pstmt6.setString(23, tmodeaA[7]);
               pstmt6.setString(24, tmodeA[8]);
               pstmt6.setString(25, tmodeaA[8]);
               pstmt6.setString(26, tmodeA[9]);
               pstmt6.setString(27, tmodeaA[9]);
               pstmt6.setString(28, tmodeA[10]);
               pstmt6.setString(29, tmodeaA[10]);
               pstmt6.setString(30, tmodeA[11]);
               pstmt6.setString(31, tmodeaA[11]);
               pstmt6.setString(32, tmodeA[12]);
               pstmt6.setString(33, tmodeaA[12]);
               pstmt6.setString(34, tmodeA[13]);
               pstmt6.setString(35, tmodeaA[13]);
               pstmt6.setString(36, tmodeA[14]);
               pstmt6.setString(37, tmodeaA[14]);
               pstmt6.setString(38, tmodeA[15]);
               pstmt6.setString(39, tmodeaA[15]);
               pstmt6.setString(40, tmodeA[16]);
               pstmt6.setString(41, tmodeaA[16]);
               pstmt6.setString(42, t9posA[1]);
               pstmt6.setString(43, tposA[1]);
               pstmt6.setString(44, t9posA[2]);
               pstmt6.setString(45, tposA[2]);
               pstmt6.setString(46, t9posA[3]);
               pstmt6.setString(47, tposA[3]);
               pstmt6.setString(48, t9posA[4]);
               pstmt6.setString(49, tposA[4]);
               pstmt6.setString(50, t9posA[5]);
               pstmt6.setString(51, tposA[5]);
               pstmt6.setString(52, t9posA[6]);
               pstmt6.setString(53, tposA[6]);
               pstmt6.setString(54, t9posA[7]);
               pstmt6.setString(55, tposA[7]);
               pstmt6.setString(56, t9posA[8]);
               pstmt6.setString(57, tposA[8]);
               pstmt6.setString(58, t9posA[9]);
               pstmt6.setString(59, tposA[9]);
               pstmt6.setString(60, t9posA[10]);
               pstmt6.setString(61, tposA[10]);
               pstmt6.setString(62, t9posA[11]);
               pstmt6.setString(63, tposA[11]);
               pstmt6.setString(64, t9posA[12]);
               pstmt6.setString(65, tposA[12]);
               pstmt6.setString(66, t9posA[13]);
               pstmt6.setString(67, tposA[13]);
               pstmt6.setString(68, t9posA[14]);
               pstmt6.setString(69, tposA[14]);
               pstmt6.setString(70, t9posA[15]);
               pstmt6.setString(71, tposA[15]);
               pstmt6.setString(72, t9posA[16]);
               pstmt6.setString(73, tposA[16]);
               pstmt6.setString(74, courseid);
               pstmt6.setString(75, t9poscA[1]);
               pstmt6.setString(76, tposcA[1]);
               pstmt6.setString(77, t9poscA[2]);
               pstmt6.setString(78, tposcA[2]);
               pstmt6.setString(79, t9poscA[3]);
               pstmt6.setString(80, tposcA[3]);
               pstmt6.setString(81, t9poscA[4]);
               pstmt6.setString(82, tposcA[4]);
               pstmt6.setString(83, t9poscA[5]);
               pstmt6.setString(84, tposcA[5]);
               pstmt6.setString(85, t9poscA[6]);
               pstmt6.setString(86, tposcA[6]);
               pstmt6.setString(87, t9poscA[7]);
               pstmt6.setString(88, tposcA[7]);
               pstmt6.setString(89, t9poscA[8]);
               pstmt6.setString(90, tposcA[8]);
               pstmt6.setString(91, t9poscA[9]);
               pstmt6.setString(92, tposcA[9]);
               pstmt6.setString(93, t9poscA[10]);
               pstmt6.setString(94, tposcA[10]);
               pstmt6.setString(95, t9poscA[11]);
               pstmt6.setString(96, tposcA[11]);
               pstmt6.setString(97, t9poscA[12]);
               pstmt6.setString(98, tposcA[12]);
               pstmt6.setString(99, t9poscA[13]);
               pstmt6.setString(100, tposcA[13]);
               pstmt6.setString(101, t9poscA[14]);
               pstmt6.setString(102, tposcA[14]);
               pstmt6.setString(103, t9poscA[15]);
               pstmt6.setString(104, tposcA[15]);
               pstmt6.setString(105, t9poscA[16]);
               pstmt6.setString(106, tposcA[16]);
               pstmt6.setInt(107, tOptA[1]);
               pstmt6.setInt(108, tOptA[2]);
               pstmt6.setInt(109, tOptA[3]);
               pstmt6.setInt(110, tOptA[4]);
               pstmt6.setInt(111, tOptA[5]);
               pstmt6.setInt(112, tOptA[6]);
               pstmt6.setInt(113, tOptA[7]);
               pstmt6.setInt(114, tOptA[8]);
               pstmt6.setInt(115, tOptA[9]);
               pstmt6.setInt(116, tOptA[10]);
               pstmt6.setInt(117, tOptA[11]);
               pstmt6.setInt(118, tOptA[12]);
               pstmt6.setInt(119, tOptA[13]);
               pstmt6.setInt(120, tOptA[14]);
               pstmt6.setInt(121, tOptA[15]);
               pstmt6.setInt(122, tOptA[16]);
                 
               pstmt6.setString(123, oldName);
                 
               count = pstmt6.executeUpdate();  // execute the prepared stmt

               pstmt6.close();

               if (!course.equals( oldName )) {             

                  PreparedStatement pstmt7 = null;
                  //
                  //  name has changed - change the name in the db tables
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE teecurr2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE teepast2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE events2b SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE evntsup2b SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE restriction2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE fives2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE block2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE guestres2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE guestqta4 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE mnumres2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE dbltee2 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  /*      // stats5 no longer used
                  pstmt7 = con.prepareStatement (
                    "UPDATE stats5 SET course = ? WHERE course = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                   */
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE lottery3 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE lreqs3 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
                  //
                  pstmt7 = con.prepareStatement (
                    "UPDATE actlott3 SET courseName = ? WHERE courseName = ?");

                  pstmt7.clearParameters();               // clear the parms
                  pstmt7.setString(1,course);
                  pstmt7.setString(2,oldName);
                  count = pstmt7.executeUpdate();         // execute the prepared stmt

                  pstmt7.close();
               }
            }    // end of If newName is null
  
         }      // end of If Multi
         
         
         
        //  
        
        // finally lets save the pace entries if needed
        // had to wait till after all other processing to make sure clubparm_id was correct
        if (paceofplay != 0) {

            for (i=1; i<19; i++) {

               pstmt = con.prepareStatement("INSERT INTO pace_benchmarks (clubparm_id, hole_number, invert, hole_pace) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE hole_pace = ?");
               pstmt.clearParameters();
               pstmt.setInt(1, clubparm_id);
               pstmt.setInt(2, i);

               invert = (i<10) ? i+9 : i-9;
               pstmt.setInt(3, invert);

               pstmt.setInt(4, hole[i]);
               pstmt.setInt(5, hole[i]);
               pstmt.executeUpdate();

               if (tmp_pace != 0) {
                   hole[i] = tmp_pace;
               } else {
                   bad_pace = true;
                   break;
               }

            } // end for loop
            
            
            // update the pace status leeway times
            pstmt = con.prepareStatement("UPDATE pace_status SET pace_leeway = ? WHERE pace_status_id = 1");
            pstmt.clearParameters();
            pstmt.setDouble(1, pace_leeway_1);
            pstmt.executeUpdate();
            
            
            pstmt = con.prepareStatement("UPDATE pace_status SET pace_leeway = ? WHERE pace_status_id = 2");
            pstmt.clearParameters();
            pstmt.setDouble(1, pace_leeway_2);
            pstmt.executeUpdate();

        } // end if pace is enabled
          

      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }

      //
      // Database updated - inform user
      //
      out.println(SystemUtils.HeadTitle("Proshop Course Parms Changed"));
      out.println("<BODY>");
      SystemUtils.getProshopSubMenu(req, out, lottery);

      out.println("<CENTER>");
      out.println("<BR><BR><H3>Course Parameters Have Been Updated</H3>");

      if (exist == 1) {      // if parms already existed

         out.println("<BR><BR>Thank you, the Course parameters have been changed.");

      } else {             // first time adding parms

         out.println("<BR><BR>The Course parameters have been set.");
         out.println("<BR>Make sure the parameters are correct.  Then complete");
         out.println("<BR>the other configurations for this course before requesting that the tee");
         out.println("<BR>sheets be built.  Refer to the other menu options under 'System Config'.");
      }

      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_parms\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");

      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
      out.println("<input type=\"submit\" value=\"Exit\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");

      //
      //  We must now check if the Modes of Trans have been changed.
      //  If so, we must update the stats.
      //
      if (!parm.tmode[0].equals( "" ) && parm.tmode[0] != null) {  // if the previous value of first tmode was not null

         if ((!parm.tmode[0].equals( tmodeA[1])) || (!parm.tmode[1].equals( tmodeA[2])) || (!parm.tmode[2].equals( tmodeA[3])) ||
             (!parm.tmode[3].equals( tmodeA[4])) || (!parm.tmode[4].equals( tmodeA[5])) || (!parm.tmode[5].equals( tmodeA[6])) ||
             (!parm.tmode[6].equals( tmodeA[7])) || (!parm.tmode[7].equals( tmodeA[8])) || (!parm.tmode[8].equals( tmodeA[9])) ||
             (!parm.tmode[9].equals( tmodeA[10])) || (!parm.tmode[10].equals( tmodeA[11])) || (!parm.tmode[11].equals( tmodeA[12])) ||
             (!parm.tmode[12].equals( tmodeA[13])) || (!parm.tmode[13].equals( tmodeA[14])) || (!parm.tmode[14].equals( tmodeA[15])) ||
             (!parm.tmode[15].equals( tmodeA[16]))) {

            parm.tmodeNew[0] = tmodeA[1];       // save new values for checks
            parm.tmodeNew[1] = tmodeA[2];
            parm.tmodeNew[2] = tmodeA[3];
            parm.tmodeNew[3] = tmodeA[4];
            parm.tmodeNew[4] = tmodeA[5];
            parm.tmodeNew[5] = tmodeA[6];
            parm.tmodeNew[6] = tmodeA[7];
            parm.tmodeNew[7] = tmodeA[8];
            parm.tmodeNew[8] = tmodeA[9];
            parm.tmodeNew[9] = tmodeA[10];
            parm.tmodeNew[10] = tmodeA[11];
            parm.tmodeNew[11] = tmodeA[12];
            parm.tmodeNew[12] = tmodeA[13];
            parm.tmodeNew[13] = tmodeA[14];
            parm.tmodeNew[14] = tmodeA[15];
            parm.tmodeNew[15] = tmodeA[16];

            // first update the tmodes table for this club
            SystemUtils.doTableUpdate_tmodes(con);
         }
      }

   }  // end of IF submit

 }   // end of doPost   


 // *********************************************************
 // Build and output the Parms Selection Table
 // *********************************************************

 private void buildParms(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con, 
                         int multi, String course, String club, String posType, int lottery, int paceofplay) {

   resp.setContentType("text/html");

   Statement stmt = null;
   //Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
     
   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // golf only

   //
   // Define parms - set defaults
   //
   String courseName = "";
   String courseid = "";

   //
   //  arrays to position the tmode parms (start with an index of '1')
   //
   String [] tmode = new String [parm.MAX_Tmodes+1];     // array to hold the tmode names
   String [] tmodea = new String [parm.MAX_Tmodes+1];    // array to hold the tmode acronyms
   String [] tpos = new String [parm.MAX_Tmodes+1];
   String [] t9pos = new String [parm.MAX_Tmodes+1];
   String [] tposc = new String [parm.MAX_Tmodes+1];
   String [] t9posc = new String [parm.MAX_Tmodes+1];
   int [] tOpt = new int [parm.MAX_Tmodes+1];

   int first_hr = 05;    // first tee time hr (05 - 09)
   int first_min = 0;    // first tee time min (00 - 59)
   int last_hr = 16;     // last tee time hr (04 - 09 or 16 - 21 military)
   int last_min = 0;     // last tee time min (00 - 59)
   int mins = 06;        // minutes between tee times (06 - 90)
   int alt_mins = 0;     // alternate minutes between tee times (0 - 90)
   //int mc = 0;           // motorized cart
   //int pc = 0;           // pull cart
   //int wa = 0;           // walk
   //int ca = 0;           // caddy
   int xx = 0;           // xx (for when to build tee sheets)
   int fives = 0;        // 5 some support option
   int i = 0;
   int clubparm_id = 0;

   //
   //  init the string arrays
   //
   for (i=0; i<parm.MAX_Tmodes+1; i++) {
      tmode[i] = "";
      tmodea[i] = "";
      tpos[i] = "";
      t9pos[i] = "";
      tposc[i] = "";
      t9posc[i] = "";
      tOpt[i] = 0;
   }
     
   //
   //  If Multi=No or Course Name is null, then just get the parms and output them 
   //
   if ((multi == 0) || (course.equals( "" ))) {
      
      try {
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT * FROM clubparm2 WHERE first_hr != 0");

         if (rs.next()) {

            courseName = rs.getString("courseName");
            first_hr = rs.getInt("first_hr");
            first_min = rs.getInt("first_min");
            last_hr = rs.getInt("last_hr");
            last_min = rs.getInt("last_min");
            mins = rs.getInt("betwn");
            xx = rs.getInt("xx");
            alt_mins = rs.getInt("alt");
            fives = rs.getInt("fives");
            clubparm_id = rs.getInt("clubparm_id");
            courseid = rs.getString("courseid");
            
            for (int j=1; j <= 16; j++) {
                tmode[j] = rs.getString("tmode" + String.valueOf(j));
                tmodea[j] = rs.getString("tmodea" + String.valueOf(j));
                tpos[j] = rs.getString("tpos" + String.valueOf(j));
                t9pos[j] = rs.getString("t9pos" + String.valueOf(j));
                tposc[j] = rs.getString("tposc" + String.valueOf(j));
                t9posc[j] = rs.getString("t9posc" + String.valueOf(j));
                tOpt[j] = rs.getInt("tOpt" + String.valueOf(j));
            }     
         }
         stmt.close();
   
      }
      catch (Exception exc) {

         out.println(SystemUtils.HeadTitle("Database Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H1>Database Access Error</H1>");
         out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
         out.println("<BR>Please try again later.");
         out.println("<BR><br>Exception: " + exc.getMessage());
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
      }
    
   } else {
     
      //
      //  Multi=Yes and at least one entry with a name - the course desired is identifed by 'course' 
      //
      //    If course = '-new-', then use default values set above to start a new course entry
      //
      if (!course.equals( "-new-" )) {

         try {
            PreparedStatement pstmt = con.prepareStatement (
               "SELECT * FROM clubparm2 WHERE courseName = ?");

            pstmt.clearParameters();            // clear the parms
            pstmt.setString(1, course);         // put the parm in stmt

            rs = pstmt.executeQuery();

            if (rs.next()) {

               courseName = rs.getString("courseName");
               first_hr = rs.getInt("first_hr");
               first_min = rs.getInt("first_min");
               last_hr = rs.getInt("last_hr");
               last_min = rs.getInt("last_min");
               mins = rs.getInt("betwn");
               xx = rs.getInt("xx");
               alt_mins = rs.getInt("alt");
               fives = rs.getInt("fives");
               clubparm_id = rs.getInt("clubparm_id");
               courseid = rs.getString("courseid");
               
               for (int j=1; j <= 16; j++) {
                   tmode[j] = rs.getString("tmode" + String.valueOf(j));
                   tmodea[j] = rs.getString("tmodea" + String.valueOf(j));
                   tpos[j] = rs.getString("tpos" + String.valueOf(j));
                   t9pos[j] = rs.getString("t9pos" + String.valueOf(j));
                   tposc[j] = rs.getString("tposc" + String.valueOf(j));
                   t9posc[j] = rs.getString("t9posc" + String.valueOf(j));
                   tOpt[j] = rs.getInt("tOpt" + String.valueOf(j));
               }
               
            } else {

               out.println(SystemUtils.HeadTitle("Database Error"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Parameter Error</H3>");
               out.println("<BR><BR>Sorry, we are unable to process your request at this time.");
               out.println("<BR>We cannot locate the course you specified. Requested course = \"" + course + "\"");
               out.println("<BR>Please try again later.");
               out.println("<BR><BR>If problem persists, contact customer support.");
               out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
               out.println("</CENTER></BODY></HTML>");
               return;
            }
            pstmt.close();
         }
         catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H1>Database Access Error</H1>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Please try again later.");
            out.println("<BR><br>Exception: " + exc.getMessage());
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            return;
         }
      }   // end of Multi=Yes - If course=new
   }  // end of If Multi

   int last_hr2 = last_hr - 12;   // convert to hours


   //
   //  Build the HTML page to solicit new parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Update Course Parms"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function cursor() { document.forms['f'].course.focus(); }");
      out.println("// -->");
      out.println("</script>");
   out.println("</head>");
        
   if (multi != 0) {
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");

   } else {
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   }
     
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

      out.println("<center>");

      out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
      out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<b>Course Setup</b>");
      out.println("<br>Select the proper settings for the golf course.<br>Click on 'Submit' to process the settings.");
      out.println("<br><b>Note</b>:  For minute values (min) simply type in a number between 00 and 59.");
      out.println("<br><br>Click on the '?' for a description of each item.");
      if (multi == 0) {

         out.println("<br><br>If you wish to add a new course, you must go back to club setup and select Multiple Courses.");
      }
      out.println("</font>");
      out.println("</td></tr></table>");

      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_parms\" method=\"post\" target=\"bot\" name=\"f\">");

      out.println("<br><table border=\"2\" bgcolor=\"#F5F5DC\">");
         out.println("<tr>");

         if (multi != 0) {

            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_name.htm', 'newwindow', config='Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("<b>Name</b> of this course (Use A-Z, a-z, 0-9 and spaces <b>only</b>): &nbsp;&nbsp;");
               out.println("</font>");

            out.println("<input type=\"hidden\" name=\"oldName\" value=\"" + courseName + "\">");

            out.println("</td><td align=\"left\"><font size=\"2\">");
               out.println("&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"course\" value=\"" + courseName + "\" size=\"20\" maxlength=\"30\">");
               out.println("</font>");
            out.println("</td>");

            out.println("</tr><tr>");
         }

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_5s.htm', 'newwindow', config='Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Allow <b>5-somes</b> on this course?: &nbsp;&nbsp;");
               out.println("</font>");

            out.println("</td><td align=\"left\"><font size=\"2\">");
               out.println("&nbsp;&nbsp;");
               out.println("<select size=\"1\" name=\"fives\">");
               if (fives == 0) {
                 out.println("<option selected value=\"No\">No</option>");
              } else {
                 out.println("<option value=\"No\">No</option>");
              }
               if (fives == 1) {
                 out.println("<option selected value=\"Yes\">Yes</option>");
              } else {
                 out.println("<option value=\"Yes\">Yes</option>");
              }
               out.println("</select>");
               out.println("</font>");
            out.println("</td>");
         out.println("</tr>");

         //
         //  if multi and Jonas - prompt for 'course id' - required for Jonas I/F & Abacus21
         //
         if (multi != 0 && (posType.equals( "Abacus21" ) || posType.equals( "ClubSystems Group" ) || posType.equals( "Jonas" ))) {

            out.println("<tr>");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<td align=\"center\">");
            out.println("&nbsp;");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("<b>POS ID</b> of this course (Use A-Z, a-z, 0-9 or spaces <b>only</b>): &nbsp;&nbsp;<br>");
               out.println("This is the <b>Tee Time Course Code</b> specified in the POS System Setup. &nbsp;&nbsp;");
               out.println("</font>");

            out.println("</td><td align=\"left\"><font size=\"2\">");
               out.println("&nbsp;&nbsp;");
               out.println("<input type=\"text\" name=\"courseid\" value=\"" + courseid + "\" size=\"2\" maxlength=\"2\">");
               out.println("</font>");
            out.println("</td>");
            out.println("<tr>");
         }

         out.println("<tr>");
            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_walk.htm', 'newwindow', config='Height=275, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td valign=\"top\" align=\"right\">");
               out.println("<font size=\"2\"><br><br>");
               out.println("Member <b>transportation options</b> to support: &nbsp;&nbsp;");
               out.println("<br>(specify full name and acronym) &nbsp;&nbsp;");
               out.println("<br><br>A <b>Pro Only</b> transportation option will not be visible <br>" +
                       "to members and may only be selected by pros.");
               if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {       // if POS system specified in club parms
                  out.println("<br><br>Specify the <b>POS Product Charge Codes</b> &nbsp;&nbsp;");
                  out.println("<br>if appropriate. &nbsp;&nbsp;");
               } else {
                  if (posType.equals( "IBS" )) {
                        out.println("<br><br>Specify the <b>Sales Item Codes</b> &nbsp;&nbsp;");
                        out.println("<br>and the associated <b>Fee (i.e. 45.50)</b> &nbsp;&nbsp;");
                        out.println("<br>for the POS System for each &nbsp;&nbsp;");
                        out.println("<br>appropriate Transportation Mode.&nbsp;&nbsp;");
                  } else {
                     if (!posType.equals( "" ) && !posType.equals( "None" )) {
                        out.println("<br><br>Specify the <b>Sales Item Codes</b> &nbsp;&nbsp;");
                        out.println("<br>for the POS System for each &nbsp;&nbsp;");
                        out.println("<br>appropriate Transportation Mode.&nbsp;&nbsp;");
                     }
                  }
               }
               out.println("</font>");
               if (posType.equals( "Pro-ShopKeeper" ) || posType.equals( "ClubProphetV3" )) {       // if POS system specified in club parms
                  out.println("</td><td width=\"500\" align=\"left\">");
               } else {
                  if (posType.equals( "IBS" )) {
                     out.println("</td><td width=\"560\" align=\"left\">");
                  } else {
                     if (!posType.equals( "" ) && !posType.equals( "None" )) {
                        out.println("</td><td width=\"420\" align=\"left\">");
                     } else {
                        out.println("</td><td width=\"300\" align=\"left\">");
                     }
                  }
               }
               
               out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" style=\"font-size: 10pt\">");
               out.println("<b>");
               out.println("<tr>");
               out.println("<td></td>");
               out.println("<td align=\"center\">Full Name</td>");
               out.println("<td align=\"center\">Acronym</td>");              // REMOVE AFTER TESTING PERIOD
               out.println("<td align=\"center\">Pro<br>Only</td>");
               if (posType.equals("Pro-ShopKeeper") || posType.equals( "ClubProphetV3" )) {
                   out.println("<td align=\"center\">9 Hole<br>Chg Code</td>");
                   out.println("<td align=\"center\">18 Hole<br>Chg Code</td>");
               } else if (posType.equals("IBS")) {
                   out.println("<td align=\"center\">18 Hole<br>Item #</td>");
                   out.println("<td align=\"center\">18 Hole<br>Fee</td>");
                   out.println("<td align=\"center\">9 Hole<br>Item #</td>");
                   out.println("<td align=\"center\">9 Hole<br>Fee</td>");                   
               } else if (!posType.equals("") && !posType.equals("None")) {
                   out.println("<td align=\"center\">18 Hole<br>Item #</td>");
                   out.println("<td align=\"center\">9 Hole<br>Item #</td>");
               }
               out.println("</tr>");
               out.println("</b>");

               for (i = 1; i < parm.MAX_Tmodes + 1; i++) {              // do all tmodes
                   out.println("<tr>");
                   out.println("<td align=\"right\">" + i + "</td>");
                   out.println("<td><input type=\"text\" name=\"tmode" +i+ "\" value=\"" + tmode[i] + "\" size=\"20\" maxlength=\"20\"></td>");
                   out.println("<td><input type=\"text\" name=\"tmodea" +i+ "\" value=\"" + tmodea[i] + "\" size=\"6\" maxlength=\"3\"></td>");
                   if (tOpt[i] == 1) {
                       out.println("<td align=\"center\"><input type=\"checkbox\" name=\"tOpt" +i+ "\" value=\"1\" checked></td>");
                   } else {
                       out.println("<td align=\"center\"><input type=\"checkbox\" name=\"tOpt" +i+ "\" value=\"1\"></td>");
                   }
                   if (posType.equals("Pro-ShopKeeper") || posType.equals( "ClubProphetV3" )) {      // if a POS system was specified
                       out.println("<td><input type=\"text\" name=\"t9pos" +i+ "\" value=\"" + t9pos[i] + "\" size=\"15\" maxlength=\"30\"></td>");
                       out.println("<td><input type=\"text\" name=\"tpos" +i+ "\" value=\"" + tpos[i] + "\" size=\"15\" maxlength=\"30\"></td>");
                   } else if (posType.equals("IBS")) {
                       out.println("<td><input type=\"text\" name=\"tpos" +i+ "\" value=\"" + tpos[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                       out.println("<td><input type=\"text\" name=\"tposc" +i+ "\" value=\"" + tposc[i] + "\" size=\"6\" maxlength=\"6\"></td>");
                       out.println("<td><input type=\"text\" name=\"t9pos" +i+ "\" value=\"" + t9pos[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                       out.println("<td><input type=\"text\" name=\"t9posc" +i+ "\" value=\"" + t9posc[i] + "\" size=\"6\" maxlength=\"6\"></td>");
                   } else if (!posType.equals("") && !posType.equals("None")) {
                       out.println("<td><input type=\"text\" name=\"tpos" +i+ "\" value=\"" + tpos[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                       out.println("<td><input type=\"text\" name=\"t9pos" +i+ "\" value=\"" + t9pos[i] + "\" size=\"5\" maxlength=\"20\"></td>");
                   }
                   out.println("</tr>");
               }
               out.println("</table>");
  
               out.println("</font>");
            out.println("</td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\" colspan=\"3\" bgcolor=\"#336633\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("Changes to the following parameters (after initial setup) will not take affect for 365 days.");
            out.println("<br>See the <b>note</b> at the bottom of this page for more information.");
            out.println("</font></td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_first.htm', 'newwindow', config='Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Time of day for <b>first tee time</b>: &nbsp;&nbsp;");
               out.println("</font>");
            out.println("</td><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp; hr &nbsp;");
                 out.println("<select size=\"1\" name=\"firsttee_hr\">");
                if (first_hr == 5) {
                   out.println("<option selected value=" + first_hr + ">" + first_hr + "</option>");
                } else {
                   out.println("<option value=\"05\">5</option>");
                }
                if (first_hr == 6) {
                   out.println("<option selected value=" + first_hr + ">" + first_hr + "</option>");
                } else {
                   out.println("<option value=\"06\">6</option>");
                }
                if (first_hr == 7) {
                   out.println("<option selected value=" + first_hr + ">" + first_hr + "</option>");
                } else {
                   out.println("<option value=\"07\">7</option>");
                }
                if (first_hr == 8) {
                   out.println("<option selected value=" + first_hr + ">" + first_hr + "</option>");
                } else {
                   out.println("<option value=\"08\">8</option>");
                }
                if (first_hr == 9) {
                   out.println("<option selected value=" + first_hr + ">" + first_hr + "</option>");
                } else {
                   out.println("<option value=\"09\">9</option>");
                }
                 out.println("</select>");
                 out.println("&nbsp; min &nbsp;");
                if (first_min < 10) {
                  out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"0" + first_min + "\" name=\"firsttee_min\">");
                } else {
                  out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + first_min + " name=\"firsttee_min\">");
                }
                 out.println("</input>&nbsp;&nbsp;AM");
               out.println("</font>");
            out.println("</td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_last.htm', 'newwindow', config='Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Time of day for <b>last tee time</b>: &nbsp;&nbsp;");
               out.println("</font>");
            out.println("</td><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp; hr &nbsp;");
                 out.println("<select size=\"1\" name=\"lasttee_hr\">");
                if (last_hr == 16) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"16\">4</option>");
                }
                if (last_hr == 17) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"17\">5</option>");
                }
                if (last_hr == 18) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"18\">6</option>");
                }
                if (last_hr == 19) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"19\">7</option>");
                }
                if (last_hr == 20) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"20\">8</option>");
                }
                if (last_hr == 21) {
                   out.println("<option selected value=" + last_hr + ">" + last_hr2 + "</option>");
                } else {
                   out.println("<option value=\"21\">9</option>");
                }
                 out.println("</select>&nbsp;&nbsp; min &nbsp;");
                if (last_min < 10) {
                   out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"0" + last_min + "\" name=\"lasttee_min\">");
                } else {
                   out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=" + last_min + " name=\"lasttee_min\">");
                }
                 out.println("</input>&nbsp;&nbsp;PM");
               out.println("</font>");
            out.println("</td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_betwn.htm', 'newwindow', config='Height=200, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("Number of minutes <b>between</b> tee times: &nbsp;&nbsp;");
               out.println("</font>");
            out.println("</td><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;");
                   out.println("<select size=\"1\" name=\"mins_between\">");
                if (mins == 3) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"03\">3</option>");
                }
                if (mins == 4) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"04\">4</option>");
                }
                if (mins == 5) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"05\">5</option>");
                }
                if (mins == 6) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"06\">6</option>");
                }
                if (mins == 7) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"07\">7</option>");
                }
                if (mins == 8) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"08\">8</option>");
                }
                if (mins == 9) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"09\">9</option>");
                }
                if (mins == 10) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"10\">10</option>");
                }
                if (mins == 11) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"11\">11</option>");
                }
                if (mins == 12) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"12\">12</option>");
                }
                if (mins == 13) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"13\">13</option>");
                }
                if (mins == 14) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"14\">14</option>");
                }
                if (mins == 15) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"15\">15</option>");
                }
                if (mins == 20) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"20\">20</option>");
                }
                if (mins == 25) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"25\">25</option>");
                }
                if (mins == 30) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"30\">30</option>");
                }
                if (mins == 35) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"35\">35</option>");
                }
                if (mins == 40) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"40\">40</option>");
                }
                if (mins == 45) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"45\">45</option>");
                }
                if (mins == 60) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"60\">60</option>");
                }
                if (mins == 75) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"75\">75</option>");
                }
                if (mins == 90) {
                   out.println("<option selected value=" + mins + ">" + mins + "</option>");
                } else {
                   out.println("<option value=\"90\">90</option>");
                }
                 out.println("</select>");
               out.println("</font>");
            out.println("</td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_alt.htm', 'newwindow', config='Height=320, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\">");
               out.println("<font size=\"2\">");
               out.println("<b>Alternate</b> tee times (staggered)?: &nbsp;&nbsp;");
               out.println("<br># of minutes for even numbered tee times");
               out.println("</font>");
            out.println("</td><td align=\"left\">");
               out.println("<font size=\"2\">");
               out.println("&nbsp;&nbsp;");
                   out.println("<select size=\"1\" name=\"alt_mins_between\">");
                if (alt_mins == 0) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"0\">0</option>");
                }
                if (alt_mins == 3) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"03\">3</option>");
                }
                if (alt_mins == 4) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"04\">4</option>");
                }
                if (alt_mins == 5) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"05\">5</option>");
                }
                if (alt_mins == 6) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"06\">6</option>");
                }
                if (alt_mins == 7) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"07\">7</option>");
                }
                if (alt_mins == 8) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"08\">8</option>");
                }
                if (alt_mins == 9) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"09\">9</option>");
                }
                if (alt_mins == 10) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"10\">10</option>");
                }
                if (alt_mins == 11) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"11\">11</option>");
                }
                if (alt_mins == 12) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"12\">12</option>");
                }
                if (alt_mins == 13) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"13\">13</option>");
                }
                if (alt_mins == 14) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"14\">14</option>");
                }
                if (alt_mins == 15) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"15\">15</option>");
                }
                if (alt_mins == 20) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"20\">20</option>");
                }
                if (alt_mins == 25) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"25\">25</option>");
                }
                if (alt_mins == 30) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"30\">30</option>");
                }
                if (alt_mins == 35) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"35\">35</option>");
                }
                if (alt_mins == 40) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"40\">40</option>");
                }
                if (alt_mins == 45) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"45\">45</option>");
                }
                if (alt_mins == 60) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"60\">60</option>");
                }
                if (alt_mins == 75) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"75\">75</option>");
                }
                if (alt_mins == 90) {
                   out.println("<option selected value=" + alt_mins + ">" + alt_mins + "</option>");
                } else {
                   out.println("<option value=\"90\">90</option>");
                }
                 out.println("</select>");
               out.println("&nbsp;&nbsp;Zero (0) for 'NO'");
               
               
               out.println("</font>");
            out.println("</td>");
         out.println("</tr>");
         
        if (paceofplay != 0) {
         
            out.println("<tr><td align=\"center\" colspan=\"3\" bgcolor=\"#336633\">");
            out.println("<font color=\"#FFFFFF\" size=\"2\">");
            out.println("Specify the benchmark times of each hole for the Pace of Play feature.");
            out.println("<br>You don't need to collect pace times at each hole to use this feature, but");
            out.println("you should have a benchmark time for each hole.");
            out.println("</font></td>");
         out.println("</tr><tr>");

            out.println("<td align=\"center\">");
            out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_paceofplay.htm', 'newwindow', config='Height=320, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
            out.println("?</a>");
            out.println("</td>");

            out.println("<td align=\"right\" valign=top>");
               out.println("<font size=\"2\">");
               out.println("<br><b>Pace of Play</b> Benchmarks: &nbsp;&nbsp;");
               out.println("<br># of minutes allowed for each hole.");
               out.println("</font>");
            out.println("</td><td align=\"left\">");
               out.println("<font size=\"2\">");
               
            String tmp_x = "";
            PreparedStatement pstmt = null;
            Double leeway = 0.0;
            Double leeway_time = 0.0;
            
            try {
                
                for (int x=1;x<19;x++) {

                    pstmt = con.prepareStatement ("SELECT * FROM pace_benchmarks WHERE clubparm_id = ? AND hole_number = ? ORDER BY hole_number");
                    pstmt.clearParameters();
                    pstmt.setInt(1, clubparm_id);
                    pstmt.setInt(2, x);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        tmp_x = "" + rs.getInt("hole_pace");
                    } else {
                        tmp_x = "";
                    }
                    
                    out.println("&nbsp;&nbsp;Hole "+x+": &nbsp;<input type=text name=hole_"+x+" value=\"" + tmp_x +  "\" size=3 maxlength=2 onkeypress=\"return blockNonNumeric(event)\"><br>");
                
                } // end loop
            
               
                out.println("</font>");
                out.println("</td>");
            
                out.println("</tr><tr>");

                    out.println("<td align=\"center\">");
                    out.println("<a href=\"javascript:void(0)\" onClick=\"window.open ('/" +rev+ "/proshop_help_course_paceofplay2.htm', 'newwindow', config='Height=320, width=450, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
                    out.println("?</a>");
                    out.println("</td>");

                out.println("<td align=\"right\" valign=top>");
                   out.println("<font size=\"2\">");
                   out.println("<br><b>Pace of Play Leeway:</b> &nbsp;&nbsp;");
                   out.println("<br>");
                   out.println("(These are the same for each course)&nbsp; &nbsp;<br>");
                   out.println("</font>");
                out.println("</td><td align=\"left\">");
                   out.println("<font size=\"2\">");
                
                int last = 0;
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT COUNT(*) AS c FROM pace_status");
                if (rs.next()) last = rs.getInt("c");
                
                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT * FROM pace_status ORDER BY pace_status_sort");

                out.println("<table>");
                
                int x = 0;
                
                while (rs.next()) {
                    
                    x++;
                    leeway = rs.getDouble("pace_leeway");
                    
                    out.println("<td><td><font size=2>" + rs.getString("pace_status_name") + ":</td>");
                        
                    if (x != last) {
                        
                        out.println("<td><font size=2><input type=text name=pace_leeway_" + rs.getInt("pace_status_id") + " value=\"" + (leeway * 100) + "\" size=4 maxlength=5>%");

                        pstmt = con.prepareStatement("SELECT SUM(hole_pace) AS t, MAX(hole_number) AS m FROM pace_benchmarks WHERE clubparm_id = ? ORDER BY hole_number DESC LIMIT 1");
                        pstmt.clearParameters();
                        pstmt.setInt(1, clubparm_id);
                        rs2 = pstmt.executeQuery();
                        
                        if (rs2.next()) {
                            
                            leeway_time = rs2.getInt("t") * leeway;
                            out.println("&nbsp; (up to " + leeway_time.intValue() + " min at hole " + rs2.getInt("m") + ")</td></tr>");
                        }
                        
                    } else {
                        
                        out.println("<td><font size=2>Anything over " + leeway_time.intValue() + " minutes.</td></tr>");
                    }
                } // end loop
                
                out.println("</table>");
                
            } catch (Exception e) {
                
                SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
            }
         
        } // end if paceofplay
         
         
      out.println("</table><br>");

   out.println("<input type=\"submit\" name=\"Submit\" value=\"Submit\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form>");

   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr>");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
   out.println("<input type=\"submit\" value=\"Exit\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</font></td></form>");
   if (multi != 0) {
           
      out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_parms\">");
      out.println("<td align=\"right\">");
      out.println("<font size=\"2\">");
      out.println("<input type=\"submit\" value=\"Add or Update Another Course\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</font></td></form>");
   }
   out.println("</tr></table>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" width=\"75%\"><tr><td>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Note:</b>&nbsp;&nbsp;If you change this item after the initial setup, the new value ");
   out.println("will not take affect for 365 days.  This value affects how the tee sheets are built.  Initially, ");
   out.println("tee sheets are built for the first 365 days.  Each night a new tee sheet is built for the day which ");
   out.println("falls 365 days later.  Therefore, if this value is changed after the initial tee sheets are built, ");
   out.println("the change will not be reflected until the next sheet is built (for a date 365 days from now).");
   out.println("<br><br>Please contact ForeTees if you require this to be changed sooner.");
   out.println("</font></td></tr></table>");
   out.println("</center></font></body></html>");
   
   out.println("<script>");
   out.println("function blockNonNumeric(e) {");
   out.println(" //if(('1234567890').indexOf(window.event.keyCode)) return false;");
   out.println(" var k = -1;");
   out.println(" if (e && e.which) k = e.which;  // NS6+");
   out.println(" else if (window.event && window.event.keyCode) k = window.event.keyCode;  // IE");
   out.println(" if ( k == -1) return true; // allow NS6 to tab");
   out.println(" return (( k > 47 && k < 58) || k == 8) ? true : false;");
   out.println("}");
   out.println("</script>");
 }
}
