/***************************************************************************************     
 *   ProshopTLT_slot:  This servlet will process the 'Reserve Tee Time' request from
 *                     the Proshop's Sheet page.
 *
 *
 *
 *   created: 10/02/2006   Paul S.
 *
 *
 *   last updated:
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

// foretees imports
import com.foretees.common.parmCourse;
import com.foretees.common.parmPOS;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.parmNSlot;
import com.foretees.common.verifyNSlot;
//import com.foretees.common.verifyCustom;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;


public class ProshopTLT_slot extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //*****************************************************
 // Process the request from Proshop_dsheet
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    doPost(req, resp);
 }


 //*****************************************************
 // Process the request from doGet above and processing below
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   ResultSet rs = null;
   
   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder
   
   if (session == null) return;
       
   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Get this session's username (to be saved in teecurr)
   //
   String user = (String)session.getAttribute("user");  // "proshop4tea"; 
   String club = (String)session.getAttribute("club"); // "notify"; 
   String mshipOpt = (String)session.getAttribute("mshipOpt");
   String mtypeOpt = (String)session.getAttribute("mtypeOpt");

   if (mshipOpt.equals( "" ) || mshipOpt == null) {
     
      mshipOpt = "ALL";
   }
   if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

      mtypeOpt = "ALL";
   }
   
   //
   //  parm block to hold the tee time parms
   //
   parmNSlot slotNParms = new parmNSlot();          // allocate a parm block
   slotNParms.club = club;                        // save club name
           
   //
   // Process request according to which 'submit' button was selected
   //
   //      'cancel' - a cancel request from user via Proshop_slot
   //      'time:fb'   - a request from Proshop_sheet
   //      'submitForm' - a reservation request from Proshop_slot
   //      'remove' - a 'cancel reservation' request from Proshop_slot (remove all names)
   //      'letter' - a request to list member names from Proshop_slot
   //      'return' - a return to Proshop_slot from verify (from a skip)
   //
   if (req.getParameter("cancel") != null) {

      cancel(req, out, club, con);       // process cancel request
      return;
   }

   if ((req.getParameter("submitForm") != null) || (req.getParameter("remove") != null)) {

      verify(req, out, con, session, resp);                 // process reservation requests 
      return;
   }

   //
   //  Request from Proshop_sheet, Proshop_slot or Proshop_searchmem
   //
   int count = 0;
   int in_use = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int fb = 0;
   int visits = 0;
   int x = 0;
   int xCount = 0;
   int i = 0;
   int hide = 0;
   int nowc = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int assign = 0;
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   
   int notify_id = 0;
   int teecurr_id = 0;
  
   long mm = 0;
   long dd = 0;
   long yy = 0;
   long temp = 0;
   long date = 0;
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String mem1 = "";          // Name of Member associated with a guest player
   String mem2 = "";
   String mem3 = "";
   String mem4 = "";
   String mem5 = "";

   String sdate = "";
   String stime = "";
   String ampm = "";
   String sfb = "";
   String notes = "";
   String hides = "";
   String jump = "0";                     // jump index - default to zero (for _sheet)
   String orig_by = "";
   String orig_at = "";
   String orig_name = "";
   String last_user = "";
   String last_name = "";
   String conf = "";
   String suppressEmails = "";
   String returnCourse = "";
   String pname = "";

    String stid = "";
    String snid = "";
   
    boolean blnTooMany = false;
    String req_course = "";
    String req_stime = "";
    int req_fb = 0;
    long req_mm = 0;
    long req_dd = 0;
    long req_yy = 0;
    long req_hr = 0;
    long req_min = 0;

    int req_time = 0;
    int req_date = 0;
    //String req_day_name = "";

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub();          // allocate a parm block

    //
    //  parm block to hold the course parameters
    //
    parmCourse parmc = new parmCourse();          // allocate a parm block
   
   //
   // Get all the parameters entered
   //
   String day_name = req.getParameter("day");         //  name of the day        
   String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
   String p5 = req.getParameter("p5");                //  5-somes supported
   String p5rest = req.getParameter("p5rest");        //  5-somes restricted
   String course = req.getParameter("course");        //  Name of Course
   
   if (course == null ) course = "";
   if (p5 == null ) p5 = "";
   if (index == null ) index = "";
   
   if (req.getParameter("returnCourse") != null) {        // if returnCourse provided

      returnCourse = req.getParameter("returnCourse");
   }
   if (req.getParameter("jump") != null) {        // if jump index provided

      jump = req.getParameter("jump");
   }
   if (req.getParameter("assign") != null) {     // if this is to assign members to guests

      assign = 1;                                // indicate 'assign members to guest' (Unaccompanied Guests)
   }
   if (req.getParameter("suppressEmails") != null) {   // user wish to suppress emails? (option on this page only)

      suppressEmails = req.getParameter("suppressEmails");
   } else {

      suppressEmails = "no";
   }
   
   if (req.getParameter("stime") != null) {

      stime = req.getParameter("stime");
   }
   
   if (req.getParameter("sdate") != null) {

      sdate = req.getParameter("sdate");
   }

   if (req.getParameter("to_tid") != null) {

      stid = req.getParameter("to_tid");
   }
   
   if (req.getParameter("notifyId") != null) {

      snid = req.getParameter("notifyId");
   }

   //
   //  Convert the values from string to int
   //
   try {
      //fb = Integer.parseInt(sfb);
      notify_id = Integer.parseInt(snid);
      teecurr_id = Integer.parseInt(stid);
      date = Long.parseLong(sdate);
   }
   catch (NumberFormatException e) {
   }

    
   //
   //  parm block to hold the POS parameters
   //
   parmPOS parmp = new parmPOS();          // allocate a parm block for POS parms

   try {
        
       //
       //  Get the POS System Parameters for this Club & Course
       //
       getClub.getPOS(con, parmp, course);
   }
   catch (Exception e) {

       out.println(SystemUtils.HeadTitle("DB Error"));
       out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
       out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
       out.println("<BR><BR><H3>Database Access Error</H3>");
       out.println("<BR><BR>Unable to access the Database at this time (get pos parms).");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>If problem persists, contact customer support.");
       out.println("<BR><BR>" + e.getMessage());
       out.println("<BR><BR>");
       out.println("<a href=\"javascript:history.back(1)\">Return</a>");
       out.println("</BODY></HTML>");
       out.close();
        
       return;
        
   } // end try/catch

    //
    //  isolate yy, mm, dd
    //
    yy = date / 10000;                     
    temp = yy * 10000;
    mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    dd = mm - temp;
    mm = mm / 100;

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int thisYear = cal.get(Calendar.YEAR);            // get the year


    if (req.getParameter("return") != null) {     // if this is a return from verify - time = hhmm
        
        try {
            time = Integer.parseInt(stime);
        } catch (NumberFormatException e) {}

        //
        //  create a time string for display
        //
        hr = time / 100;
        min = time - (hr * 100);      
        ampm = " AM";
        if (hr > 11) {
            ampm = " PM";
            if (hr > 12) hr = hr - 12;
        }
        stime = hr + ":" + SystemUtils.ensureDoubleDigit(min) + ampm;

    }
    
    if (notify_id == 0) {

        out.println("<!-- stime=" + stime + " -->");
        out.println("<!-- sdate=" + sdate + " -->");
        
        req_stime = stime;
        
        //
        //  Parse the time parm to separate hh, mm, am/pm and convert to military time
        //  (received as 'hh:mm xx'   where xx = am or pm)
        //
        String shr = "";
        String smin = "";
        try {

            StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token
            shr = tok.nextToken();
            smin = tok.nextToken();
            ampm = tok.nextToken();
        }
        catch (NoSuchElementException e) {
            out.println("<p><b>ERROR: parsing time: " + stime + " - " + e.toString() + "</b></p>");
        }

        //
        //  Convert the values from string to int
        //
        try {

            date = Integer.parseInt(sdate);
            hr = Integer.parseInt(shr);
            min = Integer.parseInt(smin);
        }
        catch (NumberFormatException e) {
            out.println("<p><b>ERROR: converting values - " + e.toString() + "</b></p>");
        }

        if (ampm.equalsIgnoreCase("PM") && hr != 12) hr = hr + 12;

        time = hr * 100;
        time = time + min;          // military time
        
        
        //
        //  isolate yy, mm, dd
        //
        req_yy = date / 10000;
        temp = req_yy * 10000;
        req_mm = date - temp;
        temp = req_mm / 100;
        temp = temp * 100;
        req_dd = req_mm - temp;
        req_mm = req_mm / 100;

        // set day_name
        try {
            
            // get notifcation details
            PreparedStatement pstmtc = con.prepareStatement ( "" +
                    "SELECT DATE_FORMAT(?, '%W') AS day_name;" );
            pstmtc.clearParameters();
            pstmtc.setString(1, req_yy + "-" + SystemUtils.ensureDoubleDigit((int)req_mm) + "-" + SystemUtils.ensureDoubleDigit((int)req_dd) + " 00:00:00");
            rs = pstmtc.executeQuery();
            
            if (rs.next()) {
                
                day_name = rs.getString("day_name");
            }
            
        }
        catch (Exception e) {

            SystemUtils.buildDatabaseErrMsg(e.toString(), "Error getting day name for new notification.", out, false);
        }
        
    }


    if ((req.getParameter("letter") != null) || (req.getParameter("return") != null) ||
       (req.getParameter("mtypeopt") != null)) {                        // if user clicked on a name letter or mtype

        player1 = req.getParameter("player1");     // get the player info from the player table         
        player2 = req.getParameter("player2");
        player3 = req.getParameter("player3");
        player4 = req.getParameter("player4");
        player5 = req.getParameter("player5");
        p1cw = req.getParameter("p1cw");
        p2cw = req.getParameter("p2cw");
        p3cw = req.getParameter("p3cw");
        p4cw = req.getParameter("p4cw");
        p5cw = req.getParameter("p5cw");
        show1 = (req.getParameter("show1") != null) ? Integer.parseInt(req.getParameter("show1")) : 0;
        show2 = (req.getParameter("show2") != null) ? Integer.parseInt(req.getParameter("show2")) : 0;
        show3 = (req.getParameter("show3") != null) ? Integer.parseInt(req.getParameter("show3")) : 0;
        show4 = (req.getParameter("show4") != null) ? Integer.parseInt(req.getParameter("show4")) : 0;
        show5 = (req.getParameter("show5") != null) ? Integer.parseInt(req.getParameter("show5")) : 0;

        notes = req.getParameter("notes");
        orig_by = req.getParameter("orig_by");
        orig_at = req.getParameter("orig_at");
        conf = req.getParameter("conf");

        if (req.getParameter("hides") != null) {
            hides = req.getParameter("hide");
        } else {
            hides = "No";
        }

        if (req.getParameter("mtypeopt") != null) {

            mtypeOpt = req.getParameter("mtypeopt");
            session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
        }
        if (req.getParameter("mshipopt") != null) {
            mshipOpt = req.getParameter("mshipopt");
            session.setAttribute("mshipOpt", mshipOpt);
        }

        String p9s = "";

        if (req.getParameter("p91") != null) {
            p9s = req.getParameter("p91");
            p91 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p92") != null) {
            p9s = req.getParameter("p92");
            p92 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p93") != null) {
            p9s = req.getParameter("p93");
            p93 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p94") != null) {
            p9s = req.getParameter("p94");
            p94 = Integer.parseInt(p9s);
        }
        if (req.getParameter("p95") != null) {
            p9s = req.getParameter("p95");
            p95 = Integer.parseInt(p9s);
        }

        if (req.getParameter("mem1") != null) {
            mem1 = req.getParameter("mem1");
        }
        if (req.getParameter("mem2") != null) {
            mem2 = req.getParameter("mem2");
        }
        if (req.getParameter("mem3") != null) {
            mem3 = req.getParameter("mem3");
        }
        if (req.getParameter("mem4") != null) {
            mem4 = req.getParameter("mem4");
        }
        if (req.getParameter("mem5") != null) {
            mem5 = req.getParameter("mem5");
        }

        //
        //  Convert hide from string to int
        //
        hide = 0;                       // init to No
        if (hides.equals( "Yes" )) {
            hide = 1;
        }

    } else {

        //***********************************************************************
        //  Get the player names and check if this notification is already in use
        //***********************************************************************
        //
        slotNParms.day = day_name;            // save day name

        // see if we are here to load up an existing notification for editing
        if (notify_id != 0) {

            try {

                in_use = verifyNSlot.checkInUse(notify_id, user, slotNParms, con, out);
                course = slotNParms.course;
                orig_at = slotNParms.orig_at;

            } catch (Exception e) {
                out.println("<p><b>ERROR: checkInUse - " + e.toString() + "</b></p>");
            }
        }

        out.println("<!-- " +
                "notify_id=" + notify_id + " | " +
                "slotNParms.players=" + slotNParms.players + " | " +
                "in_use=" + in_use + " | " +
                "slotNParms.course=" + slotNParms.course + " | " +
                "day_name=" + day_name + " | " +
                "course=" + course + " -->");

        if (in_use != 0) {              // if time slot already in use

            out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<CENTER><BR><BR><H2>Notification Busy</H2>");
            out.println("<BR><BR>Sorry, but this notification is busy.<BR>");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>");
            out.println("<font size=\"2\">");
            //
            //  Prompt user to return to Proshop_sheet or Proshop_searchmem (index = 888)
            //
            if (index.equals( "888" )) {       // if originated from Proshop_main
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            } else {
                if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
                    course = returnCourse;
                }
                out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
                out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
            }
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        player1 = slotNParms.player1;
        player2 = slotNParms.player2;
        player3 = slotNParms.player3;
        player4 = slotNParms.player4;
        player5 = slotNParms.player5;
        p1cw = slotNParms.p1cw;
        p2cw = slotNParms.p2cw;
        p3cw = slotNParms.p3cw;
        p4cw = slotNParms.p4cw;
        p5cw = slotNParms.p5cw;
        p91 = slotNParms.p91;
        p92 = slotNParms.p92;
        p93 = slotNParms.p93;
        p94 = slotNParms.p94;
        p95 = slotNParms.p95;
        show1 = slotNParms.show1;
        show2 = slotNParms.show2;
        show3 = slotNParms.show3;
        show4 = slotNParms.show4;
        show5 = slotNParms.show5;
        last_user = slotNParms.last_user;
        notes = slotNParms.notes;
        hide = slotNParms.hide;
        orig_by = slotNParms.orig_by;
        conf = slotNParms.conf;
    }              // end of 'letter' or 'return' if

    //
    //  Ensure that there are no null player fields
    //
    if (player1 == null ) player1 = "";
    if (player2 == null ) player2 = "";
    if (player3 == null ) player3 = "";
    if (player4 == null ) player4 = "";
    if (player5 == null ) player5 = "";

    if (p1cw == null ) p1cw = "";
    if (p2cw == null ) p2cw = "";
    if (p3cw == null ) p3cw = "";
    if (p4cw == null ) p4cw = "";
    if (p5cw == null ) p5cw = "";

    if (last_user == null ) last_user = "";
    if (notes == null ) notes = "";
    if (orig_by == null ) orig_by = "";
    if (orig_at == null ) orig_at = "";
    if (conf == null ) conf = "";
   
    //
    //  Get the walk/cart options available and find the originators name 
    //
    PreparedStatement pstmtc = null;

    try {

        getParms.getTmodes(con, parmc, course);

        if (!orig_by.equals( "" )) {         // if originator exists (username of person originating)

            if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating)

                orig_name = orig_by;        // if proshop, just use the username

            } else {

                //
                //  Check member table and hotel table for match
                //
                orig_name = "";        // init
              
                pstmtc = con.prepareStatement (
                    "SELECT name_last, name_first, name_mi " +
                    "FROM member2b " +
                    "WHERE username = ?");

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setString(1, orig_by);

                rs = pstmtc.executeQuery();

                if (rs.next()) {
              
                   // Get the member's full name.......

                   StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                   String mi = rs.getString(3);                                // middle initial
                   if (!mi.equals( "" )) {
                      mem_name.append(" ");
                      mem_name.append(mi);
                   }
                   mem_name.append(" " + rs.getString(1));                     // last name

                   orig_name = mem_name.toString();                          // convert to one string
                }
                pstmtc.close();
              
                if (orig_name.equals( "" )) {       // if match not found - check hotel user table

                   pstmtc = con.prepareStatement (
                      "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username = ?");

                   pstmtc.clearParameters();        // clear the parms
                   pstmtc.setString(1, orig_by);

                   rs = pstmtc.executeQuery();

                   if (rs.next()) {

                      // Get the member's full name.......

                      StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                      String mi = rs.getString(3);                                // middle initial
                      if (!mi.equals( "" )) {
                         mem_name.append(" ");
                         mem_name.append(mi);
                      }
                      mem_name.append(" " + rs.getString(1));                     // last name

                      orig_name = mem_name.toString();                          // convert to one string
                   }
                   pstmtc.close();
                }
            }
        }

        if (!last_user.equals( "" )) {         // if last_user exists (username of last person to change)

            if (last_user.startsWith( "proshop" )) {  // if originator exists (username of person originating)

                last_name = last_user;        // if proshop, just use the username

            } else {

                //
                //  Check member table and hotel table for match
                //
                last_name = "";        // init

                pstmtc = con.prepareStatement (
                   "SELECT name_last, name_first, name_mi FROM member2b WHERE username = ?");

                pstmtc.clearParameters();        // clear the parms
                pstmtc.setString(1, last_user);

                rs = pstmtc.executeQuery();

                if (rs.next()) {

                   // Get the member's full name.......

                   StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                   String mi = rs.getString(3);                                // middle initial
                   if (!mi.equals( "" )) {
                      mem_name.append(" ");
                      mem_name.append(mi);
                   }
                   mem_name.append(" " + rs.getString(1));                     // last name

                   last_name = mem_name.toString();                          // convert to one string
                }
                pstmtc.close();

                if (last_name.equals( "" )) {       // if match not found - check hotel user table

                   pstmtc = con.prepareStatement (
                      "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username = ?");

                   pstmtc.clearParameters();        // clear the parms
                   pstmtc.setString(1, last_user);

                   rs = pstmtc.executeQuery();

                   if (rs.next()) {

                      // Get the member's full name.......

                      StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                      String mi = rs.getString(3);                                // middle initial
                      if (!mi.equals( "" )) {
                         mem_name.append(" ");
                         mem_name.append(mi);
                      }
                      mem_name.append(" " + rs.getString(1));                     // last name

                      last_name = mem_name.toString();                          // convert to one string
                   }
                   pstmtc.close();
                }
            }
        }
        
    }
    catch (Exception e1) {

        out.println(SystemUtils.HeadTitle("DB Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
        out.println("<BR><BR>Unable to access the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, please contact customer support.");
        out.println("<BR><BR>" + e1.getMessage());
        out.println("<BR><BR>");
        if (index.equals( "888" )) {       // if originated from Proshop_main
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
        } else {
        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
        course = returnCourse;
        }
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        }
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    //
    // See if this is a conversion request (notify->reservation)
    // 
    if (notify_id != 0) {
    
        try {
            
            // get notifcation details
            pstmtc = con.prepareStatement ( "" +
                    "SELECT *, " +
                        "DATE_FORMAT(req_datetime, '%W') AS day_name, " +
                        "DATE_FORMAT(req_datetime, '%H%i') AS stime, " +
                        "DATE_FORMAT(req_datetime, '%Y%m%d') AS sdate " +
                    "FROM notifications " +
                    "WHERE notification_id = ?;" );
            pstmtc.clearParameters();
            pstmtc.setInt(1, notify_id);
            rs = pstmtc.executeQuery();
            
            if (rs.next()) {
                
                notes = rs.getString("notes");
                req_time = rs.getInt("stime");
                req_date = rs.getInt("sdate");
                day_name = rs.getString("day_name");
                
                req_hr = req_time / 100;
                req_min = req_time - (req_hr * 100);

                ampm = " AM";
                if (req_hr > 11) {

                    ampm = " PM";
                    if (req_hr > 12) { req_hr = req_hr - 12; }
                }
                if (req_min < 10) {
                    req_stime = req_hr + ":0" + req_min + ampm;
                } else {
                    req_stime = req_hr + ":" + req_min + ampm;
                }
                
                //
                //  isolate yy, mm, dd
                //
                req_yy = req_date / 10000;
                temp = req_yy * 10000;
                req_mm = req_date - temp;
                temp = req_mm / 100;
                temp = temp * 100;
                req_dd = req_mm - temp;
                req_mm = req_mm / 100;
   
            }
            pstmtc.close();
            
            // get players
/*
"SELECT np.*, CONCAT_WS(' ', m.name_first, m.name_last) AS fullname " +
"FROM notifications_players np, member2b m " +
"WHERE notification_id = ? AND np.username = m.username ORDER BY np.pos;"
*/
            pstmtc = con.prepareStatement ( "" +
                    "SELECT * " +
                    "FROM notifications_players  " +
                    "WHERE notification_id = ? ORDER BY pos;" );
            pstmtc.clearParameters();
            pstmtc.setInt(1, notify_id);
            rs = pstmtc.executeQuery();

            int playerNum = 1;
            int nineHoles = 0;
            String username = "";
            String member = "";
            String tmode = "";
            String [] players = new String [6];
            String [] usernames = new String [6];
            String [] tmodes = new String [6];
            int [] holes = new int [6];

            while (rs.next()) {

                if (playerNum < 6) {
                    
                    username = rs.getString("username");
                    member = rs.getString("player_name");
                    tmode = rs.getString("cw");
                    nineHoles = rs.getInt("9hole");
                    
                    players[playerNum] = member;
                    usernames[playerNum] = username;
                    tmodes[playerNum] = tmode;
                    holes[playerNum] = nineHoles;

                    playerNum++;
                    
                } // end is full
                
            }
            pstmtc.close();
            
            player1 = players[1];
            player2 = (players[2] != null) ? players[2] : "";
            player3 = (players[3] != null) ? players[3] : "";
            player4 = (players[4] != null) ? players[4] : "";
            player5 = (players[5] != null) ? players[5] : "";
            p1cw = (tmodes[1] != null) ? tmodes[1] : "";
            p2cw = (tmodes[2] != null) ? tmodes[2] : "";
            p3cw = (tmodes[3] != null) ? tmodes[3] : "";
            p4cw = (tmodes[4] != null) ? tmodes[4] : "";
            p5cw = (tmodes[5] != null) ? tmodes[5] : "";
            p91 = holes[1];
            p92 = holes[2];
            p93 = holes[3];
            p94 = holes[4];
            p95 = holes[5];
            
        }
        catch (Exception e) {

            SystemUtils.buildDatabaseErrMsg(e.toString(), "", out, false);
        }
        
    }
/* else {
        
        // this is a new notification
        
        StringTokenizer tok = new StringTokenizer( stime, ": " );     // space is the default token
        String shr = tok.nextToken();
        String smin = tok.nextToken();
        ampm = tok.nextToken();

        //
        //  Convert the values from string to int
        //
        try {
            hr = Integer.parseInt(shr);
            min = Integer.parseInt(smin);
        }
        catch (NumberFormatException e) {
        }

        if (ampm.equalsIgnoreCase ( "PM" )) {

            if (hr != 12) hr = hr + 12;
        }

        //req_time = req_hr * 100;
        //req_time = req_time + min;
        
                req_hr = req_time / 100;
                req_min = req_time - (req_hr * 100);

                ampm = " AM";
                if (req_hr > 11) {

                    ampm = " PM";
                    if (req_hr > 12) { req_hr = req_hr - 12; }
                }
                if (req_min < 10) {
                    req_stime = req_hr + ":0" + req_min + ampm;
                } else {
                    req_stime = req_hr + ":" + req_min + ampm;
                }
                
                //
                //  isolate yy, mm, dd
                //
                req_yy = req_date / 10000;
                temp = req_yy * 10000;
                req_mm = req_date - temp;
                temp = req_mm / 100;
                temp = temp * 100;
                req_dd = req_mm - temp;
                req_mm = req_mm / 100;
                
        
    }// end if
 */
    //
    //  Build the HTML page to prompt user for names
    //
    out.println("<HTML>");
    out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
    out.println("<title>Proshop Tee Slot Page</title>");

    // 
    //  Notify user if there were too many players
    //
    if (blnTooMany) {
        
        out.println("" +
                "<script type='text/javascript'>" +
                "alert('We were unable to add all the players to this slot.');" +
                "</script>");
    }
    
    //
    //*******************************************************************
    //  User clicked on a letter - submit the form for the letter
    //*******************************************************************
    //
    out.println("<script type='text/javascript'>");            // Submit the form when clicking on a letter
    out.println("<!--");
    out.println("function subletter(x) {");

    out.println(" document.playerform.letter.value = x;");         // put the letter in the parm
    out.println(" playerform.submit();");        // submit the form
    out.println("}");                  // end of script function
    out.println("// -->");
    out.println("</script>");          // End of script

    if (assign == 0) {   // if normal 

        //
        //*******************************************************************
        //  Erase player name (erase button selected next to player's name)
        //
        //    Remove the player's name and shift any other names up starting at player1
        //*******************************************************************
        //
        out.println("<script type='text/javascript'>");            // Erase name script
        out.println("<!--");

        out.println("function erasename(pos1) {");

        out.println("document.playerform[pos1].value = '';");           // clear the player field
        out.println("}");                  // end of script function
        out.println("// -->");
        out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Erase text area - (Notes)
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("<script type='text/javascript'>");             // Move Notes into textarea
      out.println("<!--");
      out.println("function movenotes() {");
      out.println("var oldnotes = document.playerform.oldnotes.value;");
      out.println("document.playerform.notes.value = oldnotes;");   // put notes in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the tee slot
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");           // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");               // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");
      out.println("var wc = array[1];");
      out.println("skip = 0;");

      out.println("var player1 = document.playerform.player1.value;");
      out.println("var player2 = document.playerform.player2.value;");
      out.println("var player3 = document.playerform.player3.value;");
      out.println("var player4 = document.playerform.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = document.playerform.player5.value;");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");

      if (p5.equals( "Yes" )) {
         out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
      } else {
         out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
      }
      out.println("skip = 1;");
      out.println("}");
           
      out.println("}");                              // end of IF not x 

      out.println("if (skip == 0) {");

         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("document.playerform.player1.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("document.playerform.p1cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("document.playerform.player2.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("document.playerform.p2cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("document.playerform.player3.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("document.playerform.p3cw.value = wc;");
            out.println("}");
         out.println("} else {");

         out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("document.playerform.player4.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("document.playerform.p4cw.value = wc;");
            out.println("}");

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
            out.println("document.playerform.player5.value = name;");
            out.println("if ((wc != null) && (wc != '')) {");                    // if player is not 'X'
               out.println("document.playerform.p5cw.value = wc;");
            out.println("}");
         out.println("}");
       }

         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");

      out.println("}");                  // end of dup name chack

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script


      //
      //*******************************************************************
      //  Move a Guest Name or 'X' into the tee slot
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Move Guest Name script
      out.println("<!--");

      out.println("function moveguest(namewc) {");
      
      out.println("var name = namewc;");
      out.println("var defCW = '';");
      out.println("var player1 = document.playerform.player1.value;");
      out.println("var player2 = document.playerform.player2.value;");
      out.println("var player3 = document.playerform.player3.value;");
      out.println("var player4 = document.playerform.player4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var player5 = document.playerform.player5.value;");
      }

         out.println("if (player1 == '') {");                    // if player1 is empty
            out.println("document.playerform.player1.value = name;");
            out.println("if (defCW != '') {");                
               out.println("document.playerform.p1cw.value = defCW;");
            out.println("}");
         out.println("} else {");

         out.println("if (player2 == '') {");                    // if player2 is empty
            out.println("document.playerform.player2.value = name;");
            out.println("if (defCW != '') {");
               out.println("document.playerform.p2cw.value = defCW;");
            out.println("}");
         out.println("} else {");

         out.println("if (player3 == '') {");                    // if player3 is empty
            out.println("document.playerform.player3.value = name;");
            out.println("if (defCW != '') {");
               out.println("document.playerform.p3cw.value = defCW;");
            out.println("}");
         out.println("} else {");

         out.println("if (player4 == '') {");                    // if player4 is empty
            out.println("document.playerform.player4.value = name;");
            out.println("if (defCW != '') {");
               out.println("document.playerform.p4cw.value = defCW;");
            out.println("}");

      if (p5.equals( "Yes" )) {
         out.println("} else {");
         out.println("if (player5 == '') {");                    // if player5 is empty
            out.println("document.playerform.player5.value = name;");
            out.println("if (defCW != '') {");
               out.println("document.playerform.p5cw.value = defCW;");
            out.println("}");
         out.println("}");
       }

         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script

   } else {   // this is a prompt to assign a member to an Unaccompanied Guest

      //
      //*******************************************************************
      //  Erase Associated Member name (for Unaccompanied Guests Prompt)
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Erase name script
      out.println("<!--");

      out.println("function erasemem(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the member name field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      //
      //*******************************************************************
      //  Move a member name into the Associated Member slot for Unaccomp. Guests
      //*******************************************************************
      //
      out.println("<script type='text/javascript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                                // deliminator is a colon
      out.println("array = namewc.split(del);");                // split string into 2 pieces (name, wc)
      out.println("var name = array[0];");                      // just get the name

      out.println("var mem1 = document.playerform.mem1.value;");
      out.println("var mem2 = document.playerform.mem2.value;");
      out.println("var mem3 = document.playerform.mem3.value;");
      out.println("var mem4 = document.playerform.mem4.value;");

      if (p5.equals( "Yes" )) {
         out.println("var mem5 = document.playerform.mem5.value;");
      }

      out.println("if (( name != 'x') && ( name != 'X')) {");

         out.println("if (mem1 == '') {");                    // if mem1 is empty
            out.println("document.playerform.mem1.value = name;");
         out.println("} else {");

         out.println("if (mem2 == '') {");                    // if mem2 is empty
            out.println("document.playerform.mem2.value = name;");
         out.println("} else {");

         out.println("if (mem3 == '') {");                    // if mem3 is empty
            out.println("document.playerform.mem3.value = name;");
         out.println("} else {");

         out.println("if (mem4 == '') {");                    // if mem4 is empty
            out.println("document.playerform.mem4.value = name;");

         if (p5.equals( "Yes" )) {
            out.println("} else {");
            out.println("if (mem5 == '') {");                    // if mem5 is empty
               out.println("document.playerform.mem5.value = name;");
            out.println("}");
          }
         out.println("}");
         out.println("}");
         out.println("}");
         out.println("}");
      out.println("}");

      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");                               // End of script
   }

   out.println("</HEAD>");

   out.println("<body onLoad=\"movenotes()\" bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

   out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
   out.println("<tr><td valign=\"top\" align=\"center\">");

   out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\" align=\"center\" valign=\"top\">");
     out.println("<tr><td align=\"left\" width=\"300\">");
     out.println("&nbsp;&nbsp;&nbsp;<b>ForeTees</b>");
     out.println("</td>");
       
     out.println("<td align=\"center\">");
     out.println("<font size=\"5\">Golf Shop Notification</font>");
     out.println("</font></td>");
       
     out.println("<td align=\"center\" width=\"300\">");
     out.println("<font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC <br> " +thisYear+ " All rights reserved.");
     out.println("</font></td>");
   out.println("</tr></table>");

   out.println("<table border=\"0\" align=\"center\">");                           // table for main page
   out.println("<tr><td align=\"center\"><br>");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"620\" align=\"center\">");
         out.println("<font size=\"2\">");
         if (assign == 0) {
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this notification.");
            out.println("&nbsp; If you want to return without completing a notification, <b>do not ");
            out.println("use your browser's BACK</b> button/option.&nbsp; Instead select the <b>Go Back</b> ");
            out.println("option below.");
         } else {
            out.println("<b>Assign a Member for each Unaccompanied Guest.<br>");
            out.println("You can specify a member more than once.</b>");
         }
         out.println("</font></td></tr>");
      out.println("</table>");

      out.println("<table border=\"0\" align=\"center\" cellpadding=\"5\" cellspacing=\"5\">"); // table to contain 4 tables below
      out.println("<tr>");
      out.println("<td align=\"center\">");         // col for Instructions and Go Back button

      out.println("<font size=\"1\">");
      if (assign == 0) {      // if normal 
         out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_instruct.htm', 'newwindow', config='Height=540, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      } else {
         out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_unacomp.htm', 'newwindow', config='Height=380, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      }
      out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
      out.println("<br>Click for Help</a>");

      out.println("</font><font size=\"2\">");
      out.println("<br><br><br>");

      out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"get\" name=\"can\" autocomplete=\"yes\">");
      out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
      out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
      out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
      out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
      out.println("<input type=\"hidden\" name=\"yy\" value=" + req_yy + ">");
      out.println("<input type=\"hidden\" name=\"mm\" value=" + req_mm + ">");
      out.println("Return<br>w/o Changes:<br>");
      out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
      out.println("</font></td>");

      out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" name=\"playerform\">");
      out.println("<input type=\"hidden\" name=\"notifyId\" value=" + notify_id + ">");
      out.println("<td align=\"center\" valign=\"top\">");
      
      out.println("<table border=0><tr>");

        out.println("<td><font size=\"2\">Requested Date:</td>" +
                    "<td><font size=\"2\"><b>" + day_name + "</font></td>" +
                    "<td><font size=\"2\"><b>" + req_mm + "/" + req_dd + "/" + req_yy + "</b></td>" +
                    "<td><font size=\"2\">&nbsp; Time:</td>" +
                    "<td><font size=\"2\"><b>" + req_stime + "</b></td></tr>");

        
        out.println("<tr><td colspan=5 align=center><font size=\"2\">");
        
        if (notify_id != 0 && (!orig_name.equals( "" ) || !last_name.equals( "" ))) {
            
            if (!orig_name.equals( "" )) {
                out.println("Originated by: <b>" + orig_name + "</b> at " + orig_at + ".");
            }
            if (!last_name.equals( "" )) {
                //out.println("&nbsp; Last modified by: <b>" + last_name + "</b><br>");
            }
        }
        out.println("</font></td></tr>");

        if ( !course.equals( "" ) ) { // || !course.equals(req_course)
            out.println("<tr><td colspan=5 align=center><font size=\"2\">Course: <b>" + course + "</b></td></tr>");
        }
        
        out.println("</table><br>");
        
         GregorianCalendar cal_pci = new GregorianCalendar();
         boolean show_checkin = (
            (!parmp.posType.equals( "Pro-ShopKeeper" )) &&
            mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
            dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
            yy == cal_pci.get(cal_pci.YEAR));         
         
         if (assign == 0) {      // if normal

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"425\">");  // table for player selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Add or Remove Players</b>");
            out.println("</font></td></tr>");

            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\"><nobr>");

            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Players");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/9hole.gif\" height=17 width=22>&nbsp;");
            
            if (show_checkin) {
                out.println("&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/checkin.gif\" width=30 height=17>&nbsp;");
            }

            out.println("</nobr></b>");
            
            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\">");
              if (!p1cw.equals( "" )) {
                 out.println("<option selected value=" + p1cw + ">" + p1cw + "</option>");
              }
              for (i=0; i<16; i++) {        // get all c/w options

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p1cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p91 == 1) ? "checked " : "") + "name=\"p91\" value=\"1\">");
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show1 == 1) ? "checked " : "") + "name=\"show1\" value=\"1\">");
             

            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player2')\" style=\"cursor:hand\">");
            out.println("2:&nbsp;&nbsp;<input type=\"text\" name=\"player2\" value=\"" + player2 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p2cw\">");
              if (!p2cw.equals( "" )) {
                 out.println("<option selected value=" + p2cw + ">" + p2cw + "</option>");
              }
              for (i=0; i<16; i++) {        // get all c/w options

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p2cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p92 == 1) ? "checked " : "") + "name=\"p92\" value=\"1\">");
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show2 == 1) ? "checked " : "") + "name=\"show2\" value=\"1\">");


            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player3')\" style=\"cursor:hand\">");
            out.println("3:&nbsp;&nbsp;<input type=\"text\" name=\"player3\" value=\"" + player3 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p3cw\">");
              if (!p3cw.equals( "" )) {
                 out.println("<option selected value=" + p3cw + ">" + p3cw + "</option>");
              }
              for (i=0; i<16; i++) {        // get all c/w options

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p3cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p93 == 1) ? "checked " : "") + "name=\"p93\" value=\"1\">");
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show3 == 1) ? "checked " : "") + "name=\"show3\" value=\"1\">");


            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player4')\" style=\"cursor:hand\">");
            out.println("4:&nbsp;&nbsp;<input type=\"text\" name=\"player4\" value=\"" + player4 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p4cw\">");
              if (!p4cw.equals( "" )) {
                 out.println("<option selected value=" + p4cw + ">" + p4cw + "</option>");
              }
              for (i=0; i<16; i++) {        // get all c/w options

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p4cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p94 == 1) ? "checked " : "") + "name=\"p94\" value=\"1\">");
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show4 == 1) ? "checked " : "") + "name=\"show4\" value=\"1\">");


            if (p5.equals( "Yes" )) {

              out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player5')\" style=\"cursor:hand\">");
              out.println("5:&nbsp;&nbsp;<input type=\"text\" id=\"player5\" name=\"player5\" value=\"" + player5 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p5cw\" id=\"p5cw\">");
              if (!p5cw.equals( "" )) {
                 out.println("<option selected value=" + p5cw + ">" + p5cw + "</option>");
              }
              for (i=0; i<16; i++) {        // get all c/w options

                 if (!parmc.tmodea[i].equals( "" ) && !parmc.tmodea[i].equals( p5cw )) {
                    out.println("<option value=\"" +parmc.tmodea[i]+ "\">" +parmc.tmodea[i]+ "</option>");
                 }
              }
              out.println("</select>");
              out.println("&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((p95 == 1) ? "checked " : "") + "name=\"p95\" value=\"1\">");
              if (show_checkin) out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type=\"checkbox\" " + ((show5 == 1) ? "checked " : "") + "name=\"show5\" value=\"1\">");

            } else {

              out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
              out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            }

            //
            //   Notes
            //
            //   Script will put any existing notes in the textarea (value= doesn't work)
            //
            out.println("<input type=\"hidden\" name=\"oldnotes\" value=\"" + notes + "\">"); // hold notes for script

            out.println("<br><br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasetext('notes')\" style=\"cursor:hand\">");
            out.println("Notes:&nbsp;<textarea name=\"notes\" value=\"\" id=\"notes\" cols=\"28\" rows=\"2\">");
            out.println("</textarea>");

            out.println("<br>&nbsp;&nbsp;Hide Notes from Members?:&nbsp;&nbsp; ");
            if (hide != 0) {
               out.println("<input type=\"checkbox\" checked name=\"hide\" value=\"Yes\">");
            } else {
               out.println("<input type=\"checkbox\" name=\"hide\" value=\"Yes\">");
            }
            out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");

            out.println("<br>Suppress email notification?:&nbsp;&nbsp; ");
            if (suppressEmails.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"checkbox\" checked name=\"suppressEmails\" value=\"Yes\">");
            } else {
               out.println("<input type=\"checkbox\" name=\"suppressEmails\" value=\"Yes\">");
            }
            out.println("</font><font size=\"1\">&nbsp;(checked = yes)</font><font size=\"2\">");

         } else {     // assign = 1

            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\">");  // table for member selection
            out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
               out.println("<font color=\"#FFFFFF\" size=\"2\">");
               out.println("<b>Select a Member for Each Guest</b>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");

            //
            //  Prompt the user for member names to assign to Unaccompanied Guests
            //
            out.println("<p align=\"left\">");
            out.println("<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Associated Member");
            out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            out.println("Guests</b><br>");

            out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem1')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"mem1\" value=\"" + mem1 + "\" size=\"20\" maxlength=\"30\">");
            out.println("&nbsp;&nbsp;&nbsp;" + player1 + "&nbsp;&nbsp;&nbsp;<br>");

            if (!player2.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem2')\" style=\"cursor:hand\">");
               out.println("2:&nbsp;&nbsp;<input type=\"text\" name=\"mem2\" value=\"" + mem2 + "\" size=\"20\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player2 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem2\" value=\"\">");
            }
            if (!player3.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem3')\" style=\"cursor:hand\">");
               out.println("3:&nbsp;&nbsp;<input type=\"text\" name=\"mem3\" value=\"" + mem3 + "\" size=\"20\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player3 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem3\" value=\"\">");
            }
            if (!player4.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem4')\" style=\"cursor:hand\">");
               out.println("4:&nbsp;&nbsp;<input type=\"text\" name=\"mem4\" value=\"" + mem4 + "\" size=\"20\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player4 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem4\" value=\"\">");
            }
            if (!player5.equals( "" )) {

               out.println("&nbsp;&nbsp;<img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasemem('mem5')\" style=\"cursor:hand\">");
               out.println("5:&nbsp;&nbsp;<input type=\"text\" name=\"mem5\" value=\"" + mem5 + "\" size=\"20\" maxlength=\"30\">");
               out.println("&nbsp;&nbsp;&nbsp;" + player5 + "&nbsp;&nbsp;&nbsp;<br>");

            } else {

              out.println("<input type=\"hidden\" name=\"mem5\" value=\"\">");
            }
            out.println("</p>");
         }    // end of IF assign

         out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
         out.println("<input type=\"hidden\" name=\"sdate\" value=" + sdate + ">");
         out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
         out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
         out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
         out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
         out.println("<input type=\"hidden\" name=\"mm\" value=" + req_mm + ">");
         out.println("<input type=\"hidden\" name=\"yy\" value=" + req_yy + ">");
         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
         out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
         out.println("<input type=\"hidden\" name=\"p5\" value=" + p5 + ">");
         out.println("<input type=\"hidden\" name=\"p5rest\" value=" + p5rest + ">");
         out.println("<input type=\"hidden\" name=\"jump\" value=" + jump + ">");
         out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + orig_by + "\">");
         out.println("<input type=\"hidden\" name=\"conf\" value=\"" + conf + "\">");
         if (assign == 0) {
            out.println("<br><br><font size=\"1\">");
            for (i=0; i<16; i++) {
               if (!parmc.tmodea[i].equals( "" )) {
                  out.println("<nobr>" + parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "</nobr> &nbsp; ");
               }
            }
            out.println("</font><br>");

            if (show_checkin == false) {            // if check-in options not allowed - be sure to pass the current settings
              
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + show5 + "\">");
            }

         } else {
            out.println("<input type=\"hidden\" name=\"skip\" value=\"10\">");      // skip right to assign
            out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
            if (hide != 0) {
               out.println("<input type=\"hidden\" name=\"hide\" value=\"Yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"hide\" value=\"No\">");
            }
            if (suppressEmails.equalsIgnoreCase( "yes" )) {
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"Yes\">");
            } else {
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"No\">");
            }
         }
         out.println("<input type=submit value=\"Submit\" name=\"submitForm\"><br>");
         out.println("</font></td></tr>");
         out.println("</table>");
         if (assign == 0) {
            // the onclick routine could be selectivly dropped in via the users (club) preference
            out.println("<br><input type=submit value=\"Cancel Notification\" name=\"remove\" onclick=\"return confirm('Are you sure you want to delete this notification?')\">");
         }
      out.println("</td>");
      out.println("<td valign=\"top\">");

   // ********************************************************************************
   //   If we got control from user clicking on a letter in the Member List,
   //   then we must build the name list.
   // ********************************************************************************
   //
   String letter = "%";         // default is 'List All'

   if (req.getParameter("letter") != null) {

      letter = req.getParameter("letter");

      if (letter.equals( "List All" )) {
         letter = "%";
      } else {
         letter = letter + "%";
      }
   }

   //
   //   Output the List of Names
   //
   alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, parmc, out, con);


   out.println("</td>");                                      // end of this column
   out.println("<td valign=\"top\">");                        // add column for member list table
         
     
   //
   //   Output the Alphabit Table for Members' Last Names 
   //
   alphaTable.getTable(out, user);
     
   //
   //   Output the Mship and Mtype Options
   //
   alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);
           

   //
   //   Output the List of Guests
   //
   alphaTable.guestList(club, course, day_name, time, parm, out, con);


   out.println("</td>");
   out.println("</form>");
   out.println("</tr>");
    out.println("</table>");      // end of large table containg 3 smaller tables

   out.println("</font></td></tr>");
   out.println("</table>");                      // end of main page table
     
   //
   //  End of HTML page
   //
   out.println("</td></tr>");
   out.println("</table>");                      // end of whole page table
   out.println("</font></center></body></html>");
   out.close();
  
 }  // end of doPost



 // *********************************************************
 //  Process reservation request from Proshop_slot (HTML)
 // *********************************************************

 private void verify(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


    Statement stmt = null;
    Statement estmt = null;
    Statement stmtN = null;
    ResultSet rs = null;
    ResultSet rs7 = null;

    //
    //  Get this session's user name
    //
    String user = (String)session.getAttribute("user");
    String fullName = (String)session.getAttribute("name");
    String club = (String)session.getAttribute("club");
    String posType = (String)session.getAttribute("posType");

    //
    // Get our notify uid if we are here to edit an existing notification, if absent set to zero to indicate new notification
    //
    String snid = req.getParameter("notifyId");
    if (snid == null) snid = "0";
    int notify_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        notify_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) {
    }

    //
    // init all variables
    //
    int thisTime = 0;
    int time = 0;
    int dd = 0;
    int mm = 0;
    int yy = 0;
    int fb = 0;
    int fb2 = 0;
    int t_fb = 0;
    int x = 0;
    int xhrs = 0;
    int calYear = 0;
    int calMonth = 0;
    int calDay = 0;
    int calHr = 0;
    int calMin = 0;
    int memNew = 0;
    int memMod = 0;
    int i = 0;
    int ind = 0;
    int xcount = 0;
    int year = 0;
    int month = 0;
    int dayNum = 0;
    int mtimes = 0;
    int sendemail = 0;
    int emailNew = 0;
    int emailMod = 0;
    int emailCan = 0;
    int mems = 0;
    int players = 0;
    int oldplayers = 0;
    int lstate = 0;
    int gi = 0;
    int adv_time = 0;

    long temp = 0;
    long ldd = 0;
    long date = 0;
    long adv_date = 0;
    long dateStart = 0;
    long dateEnd = 0;

    String player = "";
    String sfb = "";
    String sfb2 = "";
    String course2 = "";
    String notes = "";
    String notes2 = "";
    String rcourse = "";
    String period = "";
    String mperiod = "";
    String msg = "";
    String plyr1 = "";
    String plyr2 = "";
    String plyr3 = "";
    String plyr4 = "";
    String plyr5 = "";
    String memberName = "";
    String p9s = "";
    String p1 = "";

    boolean error = false;
    boolean guestError = false;
    boolean oakskip = false;
    boolean posSent = false;

    //
    //  Arrays to hold member & guest names to tie guests to members
    //
    String [] memA = new String [5];     // members
    String [] usergA = new String [5];   // guests' associated member (username)

    //
    //  parm block to hold the club parameters
    //
    parmClub parm = new parmClub();          // allocate a parm block

    //
    //  parm block to hold the parms
    //
    parmNSlot nSlotParms = new parmNSlot();          // allocate a parm block

    nSlotParms.notify_id = notify_id;
    
    nSlotParms.hndcp1 = 99;     // init handicaps
    nSlotParms.hndcp2 = 99;
    nSlotParms.hndcp3 = 99;
    nSlotParms.hndcp4 = 99;
    nSlotParms.hndcp5 = 99;

    //
    // Get all the parameters entered
    //
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
    String sdate = req.getParameter("date");           //  date requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of requested (hhmm)
    String smm = req.getParameter("mm");               //  month
    String syy = req.getParameter("yy");               //  year
    String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
    sfb = req.getParameter("fb");                      //  Front/Back indicator

    nSlotParms.p5 = req.getParameter("p5");            //  5-somes supported for this slot
    nSlotParms.course = req.getParameter("course");    //  name of course
    nSlotParms.player1 = SystemUtils.scrubString(req.getParameter("player1"));
    nSlotParms.player2 = SystemUtils.scrubString(req.getParameter("player2"));
    nSlotParms.player3 = SystemUtils.scrubString(req.getParameter("player3"));
    nSlotParms.player4 = SystemUtils.scrubString(req.getParameter("player4"));
    nSlotParms.player5 = SystemUtils.scrubString(req.getParameter("player5"));
    nSlotParms.p1cw = req.getParameter("p1cw");
    nSlotParms.p2cw = req.getParameter("p2cw");
    nSlotParms.p3cw = req.getParameter("p3cw");
    nSlotParms.p4cw = req.getParameter("p4cw");
    nSlotParms.p5cw = req.getParameter("p5cw");
    nSlotParms.day = req.getParameter("day");          // name of day
    //nSlotParms.notes = SystemUtils.scrubString(req.getParameter("notes"));      // Notes
    nSlotParms.notes = req.getParameter("notes");      // Notes
    nSlotParms.hides = req.getParameter("hide");       // Hide Notes

    //
    //  set 9-hole options
    //
    nSlotParms.p91 = 0;                       // init to 18 holes
    nSlotParms.p92 = 0;
    nSlotParms.p93 = 0;
    nSlotParms.p94 = 0;
    nSlotParms.p95 = 0;

    if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      nSlotParms.p91 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      nSlotParms.p92 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      nSlotParms.p93 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      nSlotParms.p94 = Integer.parseInt(p9s);
    }
    if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      nSlotParms.p95 = Integer.parseInt(p9s);
    }

    //
    //  Ensure that there are no null player fields
    //
    if (nSlotParms.player1 == null ) nSlotParms.player1 = "";
    if (nSlotParms.player2 == null ) nSlotParms.player2 = "";
    if (nSlotParms.player3 == null ) nSlotParms.player3 = "";
    if (nSlotParms.player4 == null ) nSlotParms.player4 = "";
    if (nSlotParms.player5 == null ) nSlotParms.player5 = "";
    
    if (nSlotParms.p1cw == null ) nSlotParms.p1cw = "";
    if (nSlotParms.p2cw == null ) nSlotParms.p2cw = "";
    if (nSlotParms.p3cw == null ) nSlotParms.p3cw = "";
    if (nSlotParms.p4cw == null ) nSlotParms.p4cw = "";
    if (nSlotParms.p5cw == null ) nSlotParms.p5cw = "";

    if (returnCourse == null) returnCourse = "";
    
    //
    //  Convert date & time from string to int
    //
    try {
        
        date = Long.parseLong(sdate);
        time = Integer.parseInt(stime);
        mm = Integer.parseInt(smm);
        yy = Integer.parseInt(syy);
        fb = Integer.parseInt(sfb);
    }
    catch (NumberFormatException e) {
    }

    long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000 = 512)

    //
    //  convert the index value from string to numeric - save both
    //
    try {
        
        ind = Integer.parseInt(index);
    } catch (NumberFormatException e) {
    }

    String jump = "0";                     // jump index - default to zero (for _sheet)

    if (req.getParameter("jump") != null) {            // if jump index provided

        jump = req.getParameter("jump");
    }

    //
    //  Get the length of Notes (max length of 254 chars)
    //
    int notesL = 0;

    if (!nSlotParms.notes.equals( "" )) {

        notesL = nSlotParms.notes.length();       // get length of notes
    }

    //
    //   use yy and mm and date to determine dd (from notification date)
    //
    temp = yy * 10000;
    temp = temp + (mm * 100);
    ldd = date - temp;            // get day of month from date

    dd = (int) ldd;               // convert to int

    int hr = time / 100;
    int min = time - (hr * 100);

    //
    //  put parms in Parameter Object for portability
    //
    nSlotParms.req_datetime = yy + "-" + mm + "-" + dd + " " + SystemUtils.ensureDoubleDigit(hr) + ":" + SystemUtils.ensureDoubleDigit(min) + ":00";
    nSlotParms.date = date;
    nSlotParms.time = time;
    nSlotParms.mm = mm;
    nSlotParms.yy = yy;
    nSlotParms.dd = dd;
    nSlotParms.fb = fb;
    nSlotParms.ind = ind;      // index value
    nSlotParms.sfb = sfb; 
    nSlotParms.jump = jump;
    nSlotParms.club = club;    // name of club


    //
    //  Check if this tee slot is still 'in use' and still in use by this user??
    //
    //  This is necessary because the user may have gone away while holding this slot.  If the
    //  slot timed out (system timer), the slot would be marked 'not in use' and another
    //  user could pick it up.  The original holder could be trying to use it now.
    //
    if (notify_id != 0) {

        // we are here to edit an existing notification
        try {

            PreparedStatement pstmt = con.prepareStatement(
                "SELECT * " +
                "FROM notifications " +
                "WHERE notification_id = ? AND (in_use_by = '' || in_use_by = ?)");

            pstmt.clearParameters();        // clear the parms
            pstmt.setInt(1, notify_id);
            pstmt.setString(2, user);
            rs = pstmt.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

                nSlotParms.req_datetime = rs.getString( "req_datetime" );
                nSlotParms.course_id = rs.getInt( "course_id" );
                nSlotParms.last_user = rs.getString( "in_use_by" );
                nSlotParms.hideNotes = rs.getInt( "hideNotes" );
                nSlotParms.converted = rs.getInt( "converted" );
            }
            pstmt.close();

            pstmt = con.prepareStatement (
                "SELECT * " +
                "FROM notifications_players " +
                "WHERE notification_id = ? " +
                "ORDER BY pos");

            pstmt.clearParameters();
            pstmt.setInt(1, notify_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                nSlotParms.oldPlayer1 = rs.getString( "player_name" );
                nSlotParms.oldUser1 = rs.getString( "username" );
                nSlotParms.oldp1cw = rs.getString( "cw" );
                nSlotParms.oldp91 = rs.getInt( "9hole" );
                nSlotParms.players = 1;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer2 = rs.getString( "player_name" );
                nSlotParms.oldUser2 = rs.getString( "username" );
                nSlotParms.oldp2cw = rs.getString( "cw" );
                nSlotParms.oldp92 = rs.getInt( "9hole" );
                nSlotParms.players = 2;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer3 = rs.getString( "player_name" );
                nSlotParms.oldUser3 = rs.getString( "username" );
                nSlotParms.oldp3cw = rs.getString( "cw" );
                nSlotParms.oldp93 = rs.getInt( "9hole" );
                nSlotParms.players = 3;
            }

            if (rs.next()) {

                nSlotParms.oldPlayer4 = rs.getString( "player_name" );
                nSlotParms.oldUser4 = rs.getString( "username" );
                nSlotParms.oldp4cw = rs.getString( "cw" );
                nSlotParms.oldp94 = rs.getInt( "9hole" );
                nSlotParms.players = 4;
            }
                
            if (rs.next()) {

                nSlotParms.oldPlayer5 = rs.getString( "player_name" );
                nSlotParms.oldUser5 = rs.getString( "username" );
                nSlotParms.oldp5cw = rs.getString( "cw" );
                nSlotParms.oldp95 = rs.getInt( "9hole" );
                nSlotParms.players = 5;
            }
            
            pstmt.close();
            
            if (nSlotParms.orig_by.equals( "" )) {    // if originator field still empty

                nSlotParms.orig_by = user;             // set this user as the originator
            }
         
        }
        catch (Exception e) {

            msg = "Check if busy. ";
            dbError(out, e, msg);
            return;
        }

    } // end if notify_id != 0
    
    
    //
    //  Handle a remove request
    //
    if (req.getParameter("remove") != null) {

        try {

            PreparedStatement pstmt4 = con.prepareStatement (
                "DELETE FROM notifications WHERE notification_id = ?");

            pstmt4.clearParameters();
            pstmt4.setInt(1, notify_id);
            pstmt4.executeUpdate();

            pstmt4 = con.prepareStatement (
                "DELETE FROM notifications_players WHERE notification_id = ?");

            pstmt4.clearParameters();
            pstmt4.setInt(1, notify_id);
            pstmt4.executeUpdate();
            pstmt4.close();
            
        }
        catch (Exception e4) {

            msg = "Delete notification. ";
            dbError(out, e4, msg);
            return;
        }

    } else {        //  not a 'Cancel Notification' request

        //
        //  Normal request -
        //
        //   Get the guest names and other parms specified for this club
        //
        try {
            
            getClub.getParms(con, parm);        // get the club parms
            x = parm.x;
            xhrs = parm.xhrs;                      // save for later tests
            nSlotParms.rnds = parm.rnds;
            nSlotParms.hrsbtwn = parm.hrsbtwn;
        }
        catch (Exception ignore) {
        }

        //
        //  Shift players up if any empty spots
        //
        verifyNSlot.shiftUp(nSlotParms);

        //
        //  Check if any player names are guest names
        //
        try {

            verifyNSlot.parseGuests(nSlotParms, con);
        } catch (Exception e) {
            out.println("<p><b>ERROR: parseGuests - " + e.toString() + "</b></p>");
        }

        //
        //  Reject if any player was a guest type that is not allowed for members
        //
        if (!nSlotParms.gplayer.equals( "" )) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            if (nSlotParms.hit3 == true) {                      // if error was name not specified
                out.println("<BR><BR>You must specify the name of your guest(s).");
                out.println("<BR><b>" + nSlotParms.gplayer + "</b> does not include a valid name (must be at least first & last names).");
                out.println("<BR><BR>To specify the name, click in the player box where the guest is specified, ");
                out.println("<BR>move the cursor (use the arrow keys or mouse) to the end of the guest type value, ");
                out.println("<BR>use the space bar to enter a space and then type the guest's name.");
            } else {
                out.println("<BR><BR><b>" + nSlotParms.gplayer + "</b> specifies a Guest Type that is not allowed for member use.");
            }
            out.println("<BR><BR>If the Golf Shop had originally entered this guest, then it <b>must not</b> be changed.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        error = false;

        if (parm.unacompGuest == 0) {      // if unaccompanied guests not supported

            //
            //  Make sure at least 1 player contains a member
            //
            if (((nSlotParms.player1.equals( "" )) || (nSlotParms.player1.equalsIgnoreCase( "x" )) || (!nSlotParms.g1.equals( "" ))) &&
                ((nSlotParms.player2.equals( "" )) || (nSlotParms.player2.equalsIgnoreCase( "x" )) || (!nSlotParms.g2.equals( "" ))) &&
                ((nSlotParms.player3.equals( "" )) || (nSlotParms.player3.equalsIgnoreCase( "x" )) || (!nSlotParms.g3.equals( "" ))) &&
                ((nSlotParms.player4.equals( "" )) || (nSlotParms.player4.equalsIgnoreCase( "x" )) || (!nSlotParms.g4.equals( "" ))) &&
                ((nSlotParms.player5.equals( "" )) || (nSlotParms.player5.equalsIgnoreCase( "x" )) || (!nSlotParms.g5.equals( "" )))) {

            error = true;
            }

        } else {           // guests are ok

            //
            //  Make sure at least 1 player contains a member
            //
            if (((nSlotParms.player1.equals( "" )) || (nSlotParms.player1.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player2.equals( "" )) || (nSlotParms.player2.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player3.equals( "" )) || (nSlotParms.player3.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player4.equals( "" )) || (nSlotParms.player4.equalsIgnoreCase( "x" ))) &&
                ((nSlotParms.player5.equals( "" )) || (nSlotParms.player5.equalsIgnoreCase( "x" )))) {

                error = true;
            }
        }

        if (error == true) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>At least one player field must contain a name.");
            out.println("<BR>If you want to cancel the notification, use the 'Cancel Notification' button under the player fields.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Check the number of X's against max specified by proshop
        //
        xcount = 0;

        if (nSlotParms.player1.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player2.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player3.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player4.equalsIgnoreCase( "x" )) xcount++;
        if (nSlotParms.player5.equalsIgnoreCase( "x" )) xcount++;

        if (xcount > x) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>The number of X's requested (" + xcount + ") exceeds the number allowed (" + x + ").");
            out.println("<BR>Please try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  At least 1 Player is present - Make sure a C/W was specified for all players
        //
        if (((!nSlotParms.player1.equals( "" )) && (!nSlotParms.player1.equalsIgnoreCase( "x" )) && (nSlotParms.p1cw.equals( "" ))) ||
            ((!nSlotParms.player2.equals( "" )) && (!nSlotParms.player2.equalsIgnoreCase( "x" )) && (nSlotParms.p2cw.equals( "" ))) ||
            ((!nSlotParms.player3.equals( "" )) && (!nSlotParms.player3.equalsIgnoreCase( "x" )) && (nSlotParms.p3cw.equals( "" ))) ||
            ((!nSlotParms.player4.equals( "" )) && (!nSlotParms.player4.equalsIgnoreCase( "x" )) && (nSlotParms.p4cw.equals( "" ))) ||
            ((!nSlotParms.player5.equals( "" )) && (!nSlotParms.player5.equalsIgnoreCase( "x" )) && (nSlotParms.p5cw.equals( "" )))) {

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR>Required field has not been completed or is invalid.");
            out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Make sure there are no duplicate names
        //
        player = "";

        if ((!nSlotParms.player1.equals( "" )) && (!nSlotParms.player1.equalsIgnoreCase( "x" )) && (nSlotParms.g1.equals( "" ))) {

            if ((nSlotParms.player1.equalsIgnoreCase( nSlotParms.player2 )) || (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player3 )) ||
                (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player4 )) || (nSlotParms.player1.equalsIgnoreCase( nSlotParms.player5 ))) {

                player = nSlotParms.player1;
            }
        }

      if ((!nSlotParms.player2.equals( "" )) && (!nSlotParms.player2.equalsIgnoreCase( "x" )) && (nSlotParms.g2.equals( "" ))) {

         if ((nSlotParms.player2.equalsIgnoreCase( nSlotParms.player3 )) || (nSlotParms.player2.equalsIgnoreCase( nSlotParms.player4 )) ||
             (nSlotParms.player2.equalsIgnoreCase( nSlotParms.player5 ))) {

            player = nSlotParms.player2;
         }
      }

      if ((!nSlotParms.player3.equals( "" )) && (!nSlotParms.player3.equalsIgnoreCase( "x" )) && (nSlotParms.g3.equals( "" ))) {

         if ((nSlotParms.player3.equalsIgnoreCase( nSlotParms.player4 )) ||
             (nSlotParms.player3.equalsIgnoreCase( nSlotParms.player5 ))) {

            player = nSlotParms.player3;
         }
      }

      if ((!nSlotParms.player4.equals( "" )) && (!nSlotParms.player4.equalsIgnoreCase( "x" )) && (nSlotParms.g4.equals( "" ))) {

         if (nSlotParms.player4.equalsIgnoreCase( nSlotParms.player5 )) {

            player = nSlotParms.player4;
         }
      }

        if (!player.equals( "" )) {          // if dup name found

            out.println(SystemUtils.HeadTitle("Data Entry Error"));
            out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
            out.println("<center>");
            out.println("<BR><BR><H3>Data Entry Error</H3>");
            out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
            out.println("<BR><BR>Please correct this and try again.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }

        //
        //  Parse the names to separate first, last & mi
        //
        try {

            error = verifyNSlot.parseNames(nSlotParms, "mem");

        }
        catch (Exception e) {
            out.println("<p><b>ERROR w/ parseNames: " + e.toString() + "</b></p>");

            error = true;
        }

        if ( error == true ) {          // if problem

            out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Invalid Data Received</H3><BR>");
            out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + nSlotParms.player1 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(nSlotParms.player2 + "',&nbsp;&nbsp;&nbsp;'" + nSlotParms.player3 + "',&nbsp;&nbsp;&nbsp;'");
            out.println(nSlotParms.player4 + "',&nbsp;&nbsp;&nbsp;'" + nSlotParms.player5 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the notification.");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
        }


        //
        //  Get the usernames, membership types, & hndcp's for players if matching name found
        //
        try {

            verifyNSlot.getUsers(nSlotParms, con);
        }
        catch (Exception e1) {

            msg = "Check guest names. ";
            dbError(out, e1, msg);                        // reject
            return;
        }

        //
        //  Save the members' usernames for guest association
        //
        memA[0] = nSlotParms.user1;
        memA[1] = nSlotParms.user2;
        memA[2] = nSlotParms.user3;
        memA[3] = nSlotParms.user4;
        memA[4] = nSlotParms.user5;

        //
        //  Check if any of the names are invalid.  
        //
        int invalNum = 0;
        p1 = "";
        
        if (nSlotParms.inval1 != 0) {

            p1 = nSlotParms.player1;                        // reject
            invalNum = nSlotParms.inval1;
        }
        if (nSlotParms.inval2 != 0) {

            p1 = nSlotParms.player2;                        // reject
            invalNum = nSlotParms.inval2;
        }
        if (nSlotParms.inval3 != 0) {

            p1 = nSlotParms.player3;                        // reject
            invalNum = nSlotParms.inval3;
        }
        if (nSlotParms.inval4 != 0) {

            p1 = nSlotParms.player4;                        // reject
            invalNum = nSlotParms.inval4;
        }
        if (nSlotParms.inval5 != 0) {

            p1 = nSlotParms.player5;                        // reject
            invalNum = nSlotParms.inval5;
        }

      if (!p1.equals( "" )) {          // if rejected
        
         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
           
         if (invalNum == 2) {        // if incomplete member record
  
            out.println("<BR><H3>Incomplete Member Record</H3><BR>");
            out.println("<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.<BR>");
            out.println("<BR>Member Name:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
            out.println("<BR><BR>Please inform your golf professional of this error.");
            out.println("<BR><BR>You will have to remove this name from your notification.");
            out.println("<BR><BR>");

         } else {
           
            out.println("<BR><H3>Invalid Member Name Received</H3><BR>");
            out.println("<BR><BR>Sorry, a name you entered is not recognized as a valid member.<BR>");
            out.println("<BR>You entered:&nbsp;&nbsp;&nbsp;'" + p1 + "'");
            out.println("<BR><BR>Member names must be entered exactly as they exist in the system (so we can identify them).");
            out.println("<BR><BR>");
            out.println("Please use the Partner List or Member List on the right side of the page to select the member names.");
            out.println("<BR>Simply <b>click on the desired name</b> in the list to add the member to the notification.");
            out.println("<BR><BR>");
         }

         returnToSlot(out, nSlotParms);
         return;
      }


      //
      //  If any X's requested, make sure its not too late to request an X
      //
      //    from above - x = max x's allowed, xcount = # of x's requested, xhrs = # hrs in advance to remove x's
      //
      if (xcount > 0) {       // if any x's requested in notification

         if (xhrs != 0) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in tee sheet
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            Calendar cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            calYear = cal.get(Calendar.YEAR);
            calMonth = cal.get(Calendar.MONTH);
            calDay = cal.get(Calendar.DAY_OF_MONTH);
            calHr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            calMin = cal.get(Calendar.MINUTE);

            calMonth = calMonth + 1;                            // month starts at zero

            adv_date = calYear * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (calMonth * 100);
            adv_date = adv_date + calDay;                    // date = yyyymmdd (for comparisons)

            adv_time = calHr * 100;                          // create time field of hhmm
            adv_time = adv_time + calMin;

            //
            //  Compare the notification's date/time to the X deadline
            //
            if ((date < adv_date) || ((date == adv_date) && (time <= adv_time))) {

               out.println(SystemUtils.HeadTitle("Invalid Use of X - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Invalid use of the X option.</H3><BR>");
               out.println("<BR><BR>Sorry, 'X' is not allowed for this notification.<BR>");
               out.println("It is not far enough in advance to reserve a player position with an X.");
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }
         }
      }        // end of IF xcount

      //
      //************************************************************************
      //  Check any membership types for max rounds per week, month or year
      //************************************************************************
      //
      if (!nSlotParms.mship1.equals( "" ) ||
          !nSlotParms.mship2.equals( "" ) ||
          !nSlotParms.mship3.equals( "" ) ||
          !nSlotParms.mship4.equals( "" ) ||
          !nSlotParms.mship5.equals( "" )) {                // if at least one name exists then check number of rounds

         error = false;                             // init error indicator

         try {

            error = verifyNSlot.checkMaxRounds(nSlotParms, con);
         }
         catch (Exception e) {
            out.println("<p><b>ERROR: checkMaxRounds - " + e.toString() + "</b></p>");
         }

         if (error == true) {      // a member exceed the max allowed notifications per week, month or year

            out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H3>Member Exceeded Max Allowed Rounds</H3><BR>");
            out.println("<BR><BR>Sorry, " + nSlotParms.player + " is a " + nSlotParms.mship + " member and has exceeded the<BR>");
            out.println("maximum number of notifications allowed for this " + nSlotParms.period + ".");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
         }

      }  // end of mship if

      //
      // **************************************
      //  Check for max # of guests exceeded (per member)
      // **************************************
      //
      if (nSlotParms.guests != 0) {      // if any guests were included

         error = false;                             // init error indicator

         //
         //  if 1 guest and 3 members, then always ok (do not check restrictions)
         //
         if (nSlotParms.guests != 1 || nSlotParms.members < 3) {

            try {

               error = verifyNSlot.checkMaxGuests(nSlotParms, con);
            }
            catch (Exception e5) {

               msg = "Check Memberships and Guest Numbers. ";
               dbError(out, e5, msg);
               return;
            }

            if (error == true) {      // a member exceed the max allowed per month

               out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Number of Guests Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Sorry, the maximum number of guests allowed for the<BR>");
               out.println("time you are requesting is " + nSlotParms.grest_num + " per " +nSlotParms.grest_per+ ".");
               out.println("<BR><BR>Guest Restriction = " + nSlotParms.rest_name);
               out.println("<BR><BR>");

               returnToSlot(out, nSlotParms);
               return;
            }

         }

      }      // end of if guests

      //
      // *******************************************************************************
      //  Check member restrictions
      //
      //     First, find all restrictions within date & time constraints
      //     Then, find the ones for this day
      //     Then, find any for this member type or membership type (all players)
      //
      // *******************************************************************************
      //
      error = false;                             // init error indicator

      try {

         error = verifyNSlot.checkMemRests(nSlotParms, con);
      }
      catch (Exception e7) {

         msg = "Check Member Restrictions. ";

         dbError(out, e7, msg);
         return;
      }                             // end of member restriction tests

      if (error == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted</H3><BR>");
         out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is restricted from playing during this time.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + nSlotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }



      //
      // *******************************************************************************
      //  Check Member Number restrictions
      //
      //     First, find all restrictions within date & time constraints
      //     Then, find the ones for this day
      //     Then, check all players' member numbers against all others in the time period
      //
      // *******************************************************************************
      //
      error = false;                             // init error indicator

      try {

         error = verifyNSlot.checkMemNum(nSlotParms, con);
      }
      catch (Exception e7) {

         msg = "Check Member Number Restrictions. ";
         dbError(out, e7, msg);
         return;
      }                             // end of member restriction tests

      if (error == true) {          // if we hit on a restriction

         out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
         out.println("<BR>Sorry, ");
            if (!nSlotParms.pnum1.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum1 + "</b> ");
            }
            if (!nSlotParms.pnum2.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum2 + "</b> ");
            }
            if (!nSlotParms.pnum3.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum3 + "</b> ");
            }
            if (!nSlotParms.pnum4.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum4 + "</b> ");
            }
            if (!nSlotParms.pnum5.equals( "" )) {
               out.println("<b>" + nSlotParms.pnum5 + "</b> ");
            }
         out.println("is/are restricted from playing during this time because the");
         out.println("<BR> number of members with the same member number has exceeded the maximum allowed.<br><br>");
         out.println("This time slot has the following restriction:  <b>" + nSlotParms.rest_name + "</b><br><br>");
         out.println("Please remove this player(s) or try a different time.<br>");
         out.println("Contact the Golf Shop if you have any questions.<br>");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check if any of the players are already scheduled today (only 1 res per day)
      //
      //***********************************************************************************************
      //
      nSlotParms.hit = false;
      nSlotParms.hit2 = false;
      String tmsg = "";
      int thr = 0;
      int tmin = 0;

      try {

         verifyNSlot.checkSched(nSlotParms, con);
      }
      catch (Exception e21) {

         msg = "Check Members Already Scheduled. ";
         dbError(out, e21, msg);
         return;
      }

      if (nSlotParms.hit == true || nSlotParms.hit2 == true || nSlotParms.hit3 == true) { // if we hit on a duplicate res

         if (nSlotParms.time2 != 0) {                                  // if other time was returned
           
            thr = nSlotParms.time2 / 100;                      // set time string for message
            tmin = nSlotParms.time2 - (thr * 100);
            if (thr == 12) {
               if (tmin < 10) {
                  tmsg = thr+ ":0" +tmin+ " PM";
               } else {
                  tmsg = thr+ ":" +tmin+ " PM";
               }
            } else {
               if (thr > 12) {
                  thr = thr - 12;
                  if (tmin < 10) {
                     tmsg = thr+ ":0" +tmin+ " PM";
                  } else {
                     if (tmin < 10) {
                        tmsg = thr+ ":0" +tmin+ " PM";
                     } else {
                        tmsg = thr+ ":" +tmin+ " PM";
                     }
                  }
               } else {
                  if (tmin < 10) {
                     tmsg = thr+ ":0" +tmin+ " AM";
                  } else {
                     tmsg = thr+ ":" +tmin+ " AM";
                  }
               }
            }
            if (!nSlotParms.course2.equals( "" )) {        // if course provided
              
               tmsg = tmsg + " on the " +nSlotParms.course2+ " course";
            }
         }
         out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
         out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">"); 
         out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
         if (nSlotParms.rnds > 1) {       // if multiple rounds per day supported
            if (nSlotParms.hit3 == true) {       // if rounds too close together
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is scheduled to play another round within " +nSlotParms.hrsbtwn+ " hours.<br><br>");
               out.println(nSlotParms.player + " is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
               out.println("A player can only be scheduled " +nSlotParms.rnds+ " times per day.<br><br>");
            }
         } else {
            if (nSlotParms.hit2 == true) {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is part of a lottery request for this date.<br><br>");
            } else {
               out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
            }
            out.println("A player can only be scheduled once per day.<br><br>");
         }
         out.println("Please remove this player or try a different date.<br>");
         out.println("Contact the Golf Shop if you have any questions.");
         out.println("<BR><BR>");
         out.println("If you are already scheduled for this date and would like to remove yourself<br>");
         out.println("from that notification, use the 'Go Back' button to return to the tee sheet and <br>");
         out.println("locate the time stated above, or click on the 'My Notifications' tab.");
         out.println("<BR><BR>");

         returnToSlot(out, nSlotParms);
         return;
      }

      //
      //***********************************************************************************************
      //
      //    Now check all players for 'days in advance' - based on membership types
      //
      //***********************************************************************************************
      //
      if (!nSlotParms.mship1.equals( "" ) || !nSlotParms.mship2.equals( "" ) || !nSlotParms.mship3.equals( "" ) ||
          !nSlotParms.mship4.equals( "" ) || !nSlotParms.mship5.equals( "" )) {

           try {

              //error = verifyNSlot.checkDaysAdv(nSlotParms, con);
           }
           catch (Exception e21) {

              msg = "Check Days in Advance Error. ";
              dbError(out, e21, msg);
              return;
           }

           if (error == true) {          // if we hit on a violation

              out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
              out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
              out.println("<BR>Sorry, <b>" + nSlotParms.player + "</b> is not allowed to be part of a notification this far in advance.<br><br>");
              if (x > 0) {
                 out.println("You can use an 'X' to reserve this position until the player is allowed.<br><br>");
              } else {
                 out.println("Contact the golf shop if you wish to add this person at this time.<br><br>");
              }
              out.println("<BR><BR>");

              returnToSlot(out, nSlotParms);
              return;
           }
           
      }

      //
      //  Check if user has approved of the member/guest sequence (guest association)
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (req.getParameter("skip8") == null) {

         //
         //***********************************************************************************************
         //
         //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
         //
         //***********************************************************************************************
         //
         if (nSlotParms.guests > 0) {

            //
            //  If no members requested and Unaccompanied Guests are ok at this club
            //
            if (nSlotParms.members == 0 && parm.unacompGuest == 1) {  

               if (!nSlotParms.g1.equals( "" )) {  // if player is a guest

                  nSlotParms.userg1 = user;        // set username for guests 
               }
               if (!nSlotParms.g2.equals( "" )) {  

                  nSlotParms.userg2 = user;      
               }
               if (!nSlotParms.g3.equals( "" )) {

                  nSlotParms.userg3 = user;
               }
               if (!nSlotParms.g4.equals( "" )) {

                  nSlotParms.userg4 = user;
               }
               if (!nSlotParms.g5.equals( "" )) {

                  nSlotParms.userg5 = user;
               }
               
            } else {

               if (nSlotParms.members > 0) {     // if at least one member

                  //
                  //  Both guests and members specified (member verified above) - determine guest owners by order
                  //
                  gi = 0;
                  memberName = "";

                  while (gi < 5) {                  // cycle thru arrays and find guests/members

                     if (!nSlotParms.gstA[gi].equals( "" )) {

                        usergA[gi] = memberName;       // get last players username
                     } else {
                        usergA[gi] = "";               // init array entry
                     }
                     if (!memA[gi].equals( "" )) {

                        memberName = memA[gi];        // get players username
                     }
                     gi++;
                  }
                  nSlotParms.userg1 = usergA[0];        // set usernames for guests in teecurr
                  nSlotParms.userg2 = usergA[1];
                  nSlotParms.userg3 = usergA[2];
                  nSlotParms.userg4 = usergA[3];
                  nSlotParms.userg5 = usergA[4];
               }

               if (nSlotParms.members > 1 || !nSlotParms.g1.equals( "" )) {  // if multiple members OR slot 1 is a guest

                  //
                  //  At least one guest and 2 members have been specified, or P1 is a guest.
                  //  Prompt user to verify the order.
                  //
                  //  Only require positioning if a POS system was specified for this club (saved in Login)
                  //
                  out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

                  //
                  //  if player1 is a guest & POS & not already assigned
                  //
                  if (!nSlotParms.g1.equals( "" ) && !posType.equals( "" ) && !nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {

                     out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                     out.println("The first player position cannot contain a guest.  Please correct the order<br>");
                     out.println("of players.  This is what you requested:");

                  } else {

                     out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                     out.println("Please verify that the following order is correct:");
                  }
                  out.println("<BR><BR>");
                  out.println(nSlotParms.player1 + " <BR>");
                  out.println(nSlotParms.player2 + " <BR>");
                  if (!nSlotParms.player3.equals( "" )) {
                     out.println(nSlotParms.player3 + " <BR>");
                  }
                  if (!nSlotParms.player4.equals( "" )) {
                     out.println(nSlotParms.player4 + " <BR>");
                  }
                  if (!nSlotParms.player5.equals( "" )) {
                     out.println(nSlotParms.player5 + " <BR>");
                  }

                  if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {

                     out.println("<BR>Would you like to process the request as is?");
                  }

                  //
                  //  Return to _slot to change the player order
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + notify_id + "\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + time + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + nSlotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + nSlotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + nSlotParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + nSlotParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + nSlotParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + nSlotParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + nSlotParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + nSlotParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + nSlotParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + nSlotParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + nSlotParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + nSlotParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + nSlotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + nSlotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + nSlotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + nSlotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + nSlotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + nSlotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + nSlotParms.hides + "\">");

                  if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {

                     out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline;\">");

                  } else {
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline;\">");
                  }
                  out.println("</form></font>");

                  if (nSlotParms.g1.equals( "" ) || posType.equals( "" ) || nSlotParms.oldPlayer1.equals( nSlotParms.player1 )) {

                     //
                     //  Return to process the players as they are
                     //
                     out.println("<font size=\"2\">");
                     out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + notify_id + "\">");
                     out.println("<input type=\"hidden\" name=\"skip8\" value=\"yes\">");
                     out.println("<input type=\"hidden\" name=\"player1\" value=\"" + nSlotParms.player1 + "\">");
                     out.println("<input type=\"hidden\" name=\"player2\" value=\"" + nSlotParms.player2 + "\">");
                     out.println("<input type=\"hidden\" name=\"player3\" value=\"" + nSlotParms.player3 + "\">");
                     out.println("<input type=\"hidden\" name=\"player4\" value=\"" + nSlotParms.player4 + "\">");
                     out.println("<input type=\"hidden\" name=\"player5\" value=\"" + nSlotParms.player5 + "\">");
                     out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + nSlotParms.p1cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + nSlotParms.p2cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + nSlotParms.p3cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + nSlotParms.p4cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + nSlotParms.p5cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + nSlotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + nSlotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + nSlotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + nSlotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + nSlotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + time + "\">");
                     out.println("<input type=\"hidden\" name=\"mm\" value=\"" + mm + "\">");
                     out.println("<input type=\"hidden\" name=\"yy\" value=\"" + yy + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"" + nSlotParms.p5 + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
                     out.println("<input type=\"hidden\" name=\"day\" value=\"" + nSlotParms.day + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + fb + "\">");
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + nSlotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + nSlotParms.hides + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + nSlotParms.userg1 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + nSlotParms.userg2 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + nSlotParms.userg3 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + nSlotParms.userg4 + "\">");
                     out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + nSlotParms.userg5 + "\">");
                     out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\">");
                     out.println("</form></font>");
                  }
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;

               }   // end of IF more than 1 member or guest in spot #1
            }      // end of IF no members and unaccompanied guests are ok
  
         }         // end of IF any guests specified

      } else {   // skip 8 requested
          
         //
         //  User has responded to the guest association prompt - process request in specified order
         //
         nSlotParms.userg1 = req.getParameter("userg1");
         nSlotParms.userg2 = req.getParameter("userg2");
         nSlotParms.userg3 = req.getParameter("userg3");
         nSlotParms.userg4 = req.getParameter("userg4");
         nSlotParms.userg5 = req.getParameter("userg5");
      }         // end of IF skip8


      //
      //***********************************************************************************************
      //
      //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
      //
      //***********************************************************************************************
      //
      if (!nSlotParms.userg1.equals( "" ) || !nSlotParms.userg2.equals( "" ) || !nSlotParms.userg3.equals( "" ) ||
          !nSlotParms.userg4.equals( "" ) || !nSlotParms.userg5.equals( "" )) {

         try {

            error = verifyNSlot.checkGuestQuota(nSlotParms, con);

         }
         catch (Exception e22) {

            msg = "Check Guest Quotas. ";

            dbError(out, e22, msg);
            return;
         }

         if (error == true) {          // if we hit on a violation

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
            out.println("<BR>Sorry, requesting <b>" + nSlotParms.player + "</b> exceeds the guest quota established by the Golf Shop.");
            out.println("<br><br>You will have to remove the guest in order to complete this request.");
            out.println("<br><br>Contact the Golf Shop if you have any questions.<br>");
            out.println("<BR><BR>");

            returnToSlot(out, nSlotParms);
            return;
         }

      }


      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 0;         // init email flags
      emailNew = 0;
      emailMod = 0;
      
      // set to show values to 2 if feature is supported and teetime is today
      GregorianCalendar cal_pci = new GregorianCalendar();    
      short tmp_pci = (
        parm.precheckin == 1 &&
        mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
        dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
        yy == cal_pci.get(cal_pci.YEAR)
       ) ? (short)2 : (short)0;
         
      //
      //  If players changed, then init the no-show flag and send emails, else use the old no-show value
      //
      if (!nSlotParms.player1.equals( nSlotParms.oldPlayer1 )) {

         nSlotParms.show1 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player2.equals( nSlotParms.oldPlayer2 )) {

         nSlotParms.show2 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player3.equals( nSlotParms.oldPlayer3 )) {

         nSlotParms.show3 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player4.equals( nSlotParms.oldPlayer4 )) {

         nSlotParms.show4 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      if (!nSlotParms.player5.equals( nSlotParms.oldPlayer5 )) {

         nSlotParms.show5 = tmp_pci;        // init no-show flag
         sendemail = 1;    // player changed - send email notification to all
      }

      //
      //   Set email type based on new or update request (cancel set above)
      //   Also, bump stats counters for reports
      //
      if ((!nSlotParms.oldPlayer1.equals( "" )) || (!nSlotParms.oldPlayer2.equals( "" )) || (!nSlotParms.oldPlayer3.equals( "" )) ||
          (!nSlotParms.oldPlayer4.equals( "" )) || (!nSlotParms.oldPlayer5.equals( "" ))) {

         emailMod = 1;  // was modified
         memMod++;      // increment number of mods

      } else {

         emailNew = 1;  // is new
         memNew++;      // increment number of new notifications
      }

   }  // end of 'cancel this res' if - cancel will contain empty player fields


    int course_id = SystemUtils.getClubParmIdFromCourseName(nSlotParms.course, con);

    //
    //  Verification complete -
    //  Add or Update the notification entry in the notifications table
    //
    
    if (notify_id == 0) {

        out.println("<!-- ATTEMPTING INSERT [" + nSlotParms.req_datetime + "] -->");
        // add new notification
        try {

            // Add the notification
            PreparedStatement pstmt6 = con.prepareStatement (
                "INSERT INTO notifications " + 
                "(req_datetime, course_id, notes, created_by, created_datetime) VALUES (?, ?, ?, ?, now())");
            pstmt6.clearParameters();
            pstmt6.setString(1, nSlotParms.req_datetime);
            pstmt6.setInt(2, course_id);
            pstmt6.setString(3, nSlotParms.notes);
            pstmt6.setString(4, user);
            pstmt6.executeUpdate();

            int notification_id = 0;
            pstmt6 = con.prepareStatement("SELECT LAST_INSERT_ID()");
            ResultSet rsLastID = pstmt6.executeQuery();
            while (rsLastID.next()) {
                notification_id = rsLastID.getInt(1);
            }

            pstmt6.close();

            // add the players of this notifications
            if (!nSlotParms.player1.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 1)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user1);
                pstmt6.setString(3, nSlotParms.p1cw);
                pstmt6.setString(4, nSlotParms.player1);
                pstmt6.setInt(5, nSlotParms.p91);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player2.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 2)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user2);
                pstmt6.setString(3, nSlotParms.p2cw);
                pstmt6.setString(4, nSlotParms.player2);
                pstmt6.setInt(5, nSlotParms.p92);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player3.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 3)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user3);
                pstmt6.setString(3, nSlotParms.p3cw);
                pstmt6.setString(4, nSlotParms.player3);
                pstmt6.setInt(5, nSlotParms.p93);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player4.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 4)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user4);
                pstmt6.setString(3, nSlotParms.p4cw);
                pstmt6.setString(4, nSlotParms.player4);
                pstmt6.setInt(5, nSlotParms.p94);
                pstmt6.executeUpdate();
                pstmt6.close();

            }

            if (!nSlotParms.player5.equals("")) {

                pstmt6 = con.prepareStatement (
                    "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, 5)");

                pstmt6.clearParameters();
                pstmt6.setInt(1, notification_id);
                pstmt6.setString(2, nSlotParms.user5);
                pstmt6.setString(3, nSlotParms.p5cw);
                pstmt6.setString(4, nSlotParms.player5);
                pstmt6.setInt(5, nSlotParms.p95);
                pstmt6.executeUpdate();
                pstmt6.close();

            }
            
       }
       catch (Exception e6) {

          msg = "Add Notification. ";
          dbError(out, e6, msg);
          return;
       }
        
        
    } else {
        
        out.println("<!-- ATTEMPTING UPDATES [" + notify_id + "] -->");
        // update existing notification
        try {

            out.println("<!-- UPDATING NOTES [" + nSlotParms.notes + "] -->");
            PreparedStatement pstmt6 = con.prepareStatement (
                "UPDATE notifications " +
                "SET " +
                    "notes = ? " +
                "WHERE notification_id = ?");
            
            pstmt6.clearParameters();
            pstmt6.setString(1, nSlotParms.notes);
            pstmt6.setInt(2, notify_id);
            pstmt6.executeUpdate();
            
            
            // add/update players
            updateNotificationPlayers(notify_id, nSlotParms.player1, nSlotParms.oldPlayer1, nSlotParms.user1, nSlotParms.p1cw, nSlotParms.oldp1cw, nSlotParms.p91, nSlotParms.oldp91, 1, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player2, nSlotParms.oldPlayer2, nSlotParms.user2, nSlotParms.p2cw, nSlotParms.oldp2cw, nSlotParms.p92, nSlotParms.oldp92, 2, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player3, nSlotParms.oldPlayer3, nSlotParms.user3, nSlotParms.p3cw, nSlotParms.oldp3cw, nSlotParms.p93, nSlotParms.oldp93, 3, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player4, nSlotParms.oldPlayer4, nSlotParms.user4, nSlotParms.p4cw, nSlotParms.oldp4cw, nSlotParms.p94, nSlotParms.oldp94, 4, con, out);
            updateNotificationPlayers(notify_id, nSlotParms.player5, nSlotParms.oldPlayer5, nSlotParms.user5, nSlotParms.p5cw, nSlotParms.oldp5cw, nSlotParms.p95, nSlotParms.oldp95, 5, con, out);

            out.println("<!-- CLEARING IN-USE FLAG [" + notify_id + "] -->");
            // clear in_use fields
            pstmt6 = con.prepareStatement (
                "UPDATE notifications SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE notification_id = ?");

            pstmt6.clearParameters();
            pstmt6.setInt(1, notify_id);
            pstmt6.executeUpdate();
            pstmt6.close();
     
        }
        catch (Exception e6) {

            msg = "Update Notification.";
            dbError(out, e6, msg);
            return;
        }
            
    } // end if insert or update
    
    
    
/*
   //
   //  Track the history of this notification - make entry in 'teehist' table (check if new or update)
   //
   if (nSlotParms.oldPlayer1.equals( "" ) && nSlotParms.oldPlayer2.equals( "" ) && nSlotParms.oldPlayer3.equals( "" ) &&
       nSlotParms.oldPlayer4.equals( "" ) && nSlotParms.oldPlayer5.equals( "" )) {

      //  new
      SystemUtils.updateHist(date, nSlotParms.day, time, fb, nSlotParms.course, nSlotParms.player1, nSlotParms.player2, nSlotParms.player3,  
                             nSlotParms.player4, nSlotParms.player5, user, fullName, 0, con);
     
   } else {
     
      //  update
      SystemUtils.updateHist(date, nSlotParms.day, time, fb, nSlotParms.course, nSlotParms.player1, nSlotParms.player2, nSlotParms.player3,
                             nSlotParms.player4, nSlotParms.player5, user, fullName, 1, con);
   }
*/
/*
   //
   //  Build the HTML page to confirm notification for user
   //
   out.println(SystemUtils.HeadTitle("Proshop Tee Slot Page"));
   out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center><img src=\"/" +rev+ "/images/foretees.gif\"><hr width=\"40%\">");
   out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

   if (req.getParameter("remove") != null) {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;The notification has been cancelled.</p>");
   } else {

      out.println("<p>&nbsp;</p><p>&nbsp;Thank you!&nbsp;&nbsp;Your notification has been accepted and processed.</p>");

      if (xcount > 0 && xhrs > 0) {            // if any X's were specified 

         out.println("<p>&nbsp;</p>All player positions reserved by an 'X' must be filled within " + xhrs + " hours of the your requested time.");
         out.println("<br>If not, the system will automatically remove the X.<br>");
      }

      if (notesL > 254) {

      out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
      }
   }

   out.println("<p>&nbsp;</p></font>");
*/
   
   //
   //  Build the HTML page to confirm reservation for user
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //   
   //
   if (index.equals( "555" )) {         // if came from Proshop_notify

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<title>Proshop Tee Slot Page</title>");
      if (posSent == false) {        // if pos charges not already sent
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?newnotify=yes&course=" + nSlotParms.course + "&mm=" + mm + "&yy=" + yy + "\">");
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The notification has been cancelled.</p>");
           
         if (posSent == true) {        // if pos charges already sent for this group
           
            out.println("<p><b>WARNING</b>&nbsp;&nbsp;Charges have already been sent to the POS System for one or more players in this group.<br>");
            out.println("You should use the POS System to cancel the charges.</p>");
         }
           
      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your notification has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
      out.println("<p>&nbsp;</p></font>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"newnotify\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
   
   } else if (index.equals( "444" ) || notify_id != 0) {
        
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<title>Proshop Tee Slot Page</title>");
      if (posSent == false) {        // if pos charges not already sent
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?course=" + nSlotParms.course + "&index=0\">");
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The notification has been cancelled.</p>");
           
         if (posSent == true) {        // if pos charges already sent for this group
           
            out.println("<p><b>WARNING</b>&nbsp;&nbsp;Charges have already been sent to the POS System for one or more players in this group.<br>");
            out.println("You should use the POS System to cancel the charges.</p>");
         }
           
      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your notification has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
      out.println("<p>&nbsp;</p></font>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"get\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"0\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
        
   } else {                             // came from proshop_dsheet

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<title>Proshop Tee Slot Page</title>");
      if (posSent == false) {        // if pos charges not already sent
         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            //out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + returnCourse + "&jump=" + nSlotParms.jump + "\">");
         } else {
            //out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + nSlotParms.course + "&jump=" + nSlotParms.jump + "\">");
         }
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The notification has been cancelled.</p>");

      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your notification has been accepted and processed.</p>");

         if (notesL > 254) {

            out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
      
      if (posSent == true) {        // if pos charges already sent for this group

         out.println("<p><br><b>WARNING</b>&nbsp;&nbsp;Charges have already been sent to the POS System for one or more players in this group.<br>");
         out.println("You should use the POS System to cancel the charges.</p>");
      }

      out.println("<p>&nbsp;</p></font>");

      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#8B8970\" cellpadding=\"8\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + nSlotParms.course + "\">");
      }
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" + nSlotParms.jump + ">");
      out.println("<tr><td><font size=\"2\">");
      out.println("<input type=\"submit\" value=\"Return\">");
      out.println("</font></td></tr></form></table>");
   }

   //
   //  End of HTML page
   //
   out.println("</center></font></body></html>");
   out.close();

   try {

      resp.flushBuffer();      // force the repsonse to complete
   }
   catch (Exception ignore) {
   }

   //
   //***********************************************
   //  Send email notification if necessary
   //***********************************************
   //
   if (sendemail != 0) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "tee";
      parme.date = date;
      parme.time = time;
      parme.fb = fb;
      parme.mm = mm;
      parme.dd = dd;
      parme.yy = yy;

      parme.user = user;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;
      parme.emailCan = emailCan;

      parme.p91 = nSlotParms.p91;
      parme.p92 = nSlotParms.p92;
      parme.p93 = nSlotParms.p93;
      parme.p94 = nSlotParms.p94;
      parme.p95 = nSlotParms.p95;

      parme.course = nSlotParms.course;
      parme.day = nSlotParms.day;
      parme.notes = nSlotParms.notes;

      parme.player1 = nSlotParms.player1;
      parme.player2 = nSlotParms.player2;
      parme.player3 = nSlotParms.player3;
      parme.player4 = nSlotParms.player4;
      parme.player5 = nSlotParms.player5;

      parme.oldplayer1 = nSlotParms.oldPlayer1;
      parme.oldplayer2 = nSlotParms.oldPlayer2;
      parme.oldplayer3 = nSlotParms.oldPlayer3;
      parme.oldplayer4 = nSlotParms.oldPlayer4;
      parme.oldplayer5 = nSlotParms.oldPlayer5;

      parme.user1 = nSlotParms.user1;
      parme.user2 = nSlotParms.user2;
      parme.user3 = nSlotParms.user3;
      parme.user4 = nSlotParms.user4;
      parme.user5 = nSlotParms.user5;

      parme.olduser1 = nSlotParms.oldUser1;
      parme.olduser2 = nSlotParms.oldUser2;
      parme.olduser3 = nSlotParms.oldUser3;
      parme.olduser4 = nSlotParms.oldUser4;
      parme.olduser5 = nSlotParms.oldUser5;

      parme.pcw1 = nSlotParms.p1cw;
      parme.pcw2 = nSlotParms.p2cw;
      parme.pcw3 = nSlotParms.p3cw;
      parme.pcw4 = nSlotParms.p4cw;
      parme.pcw5 = nSlotParms.p5cw;

      parme.oldpcw1 = nSlotParms.oldp1cw;
      parme.oldpcw2 = nSlotParms.oldp2cw;
      parme.oldpcw3 = nSlotParms.oldp3cw;
      parme.oldpcw4 = nSlotParms.oldp4cw;
      parme.oldpcw5 = nSlotParms.oldp5cw;

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common

   }     // end of IF sendemail
   
 }       // end of verify

 
 
 private void verifyOld(HttpServletRequest req, PrintWriter out, Connection con, HttpSession session, HttpServletResponse resp) {


   Statement stmt = null;
   Statement estmt = null;
   Statement stmtN = null;
   ResultSet rs = null;
   ResultSet rs7 = null;

   //
   //  Get this session's attributes
   //
   String user = "";
   String club = "";
   String posType = "";
   user = (String)session.getAttribute("user");
   club = (String)session.getAttribute("club");
   posType = (String)session.getAttribute("posType");
  
   int reject = 0;
   int count = 0;
   int time2 = 0;
   int fb2 = 0;
   int t_fb = 0;
   int x = 0;
   int xhrs = 0;
   int xError = 0;
   int xUsed = 0;
   int hide = 0;
   int i = 0;
   int mm = 0;
   int yy = 0;
   int dd = 0;
   int fb = 0;
   int time = 0;
   int mtimes = 0;
   int year = 0;
   int month = 0;
   int dayNum = 0;
   int ind = 0;
   int temp = 0;
   int sendemail = 0;
   int emailNew = 0;
   int emailMod = 0;
   int emailCan = 0;
   int gi = 0;
   int proNew = 0;
   int proMod = 0;
   int skip = 0;
   int pos1 = 0;
   int pos2 = 0;
   int pos3 = 0;
   int pos4 = 0;
   int pos5 = 0;
   int event_type = 0;
      
   long date = 0;
   long dateStart = 0;
   long dateEnd = 0;

   String player = "";
   String err_name = "";
   String sfb2 = "";
   String notes2 = "";
   String period = "";
   String mperiod = "";
   String course2 = "";
   String memberName = "";
   String mship = "";
   String mtype = "";
   String skips = "";
   String p9s = "";
   String event = "";
   String suppressEmails = "no";
     
   String sponsored = "Spons";

   boolean hit = false;
   boolean hit2 = false;
   boolean check = false;
   boolean guestError = false;
   boolean error = false;
   boolean oakskip = false;
   boolean posSent = false;

   int [] mtimesA = new int [8];          // array to hold the mship max # of rounds value
   String [] periodA = new String [8];    // array to hold the mship periods (week, month, year)

   //
   //  Arrays to hold member & guest names to tie guests to members
   //
   String [] memA = new String [5];     // members
   String [] usergA = new String [5];   // guests' associated member (username)

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the parms
   //
   parmNSlot slotNParms = new parmNSlot();          // allocate a parm block

   slotNParms.hndcp1 = 99;     // init handicaps
   slotNParms.hndcp2 = 99;
   slotNParms.hndcp3 = 99;
   slotNParms.hndcp4 = 99;
   slotNParms.hndcp5 = 99;

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time requested (hhmm)
   String smm = req.getParameter("mm");               //  month
   String syy = req.getParameter("yy");               //  year
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
     
   slotNParms.p5 = req.getParameter("p5");                //  5-somes supported for this slot
   //slotNParms.p5rest = req.getParameter("p5rest");        //  5-somes restricted for this slot
   slotNParms.course = req.getParameter("course");        //  name of course
   slotNParms.player1 = req.getParameter("player1");
   slotNParms.player2 = req.getParameter("player2");
   slotNParms.player3 = req.getParameter("player3");
   slotNParms.player4 = req.getParameter("player4");
   slotNParms.player5 = req.getParameter("player5");
   slotNParms.p1cw = req.getParameter("p1cw");
   slotNParms.p2cw = req.getParameter("p2cw");
   slotNParms.p3cw = req.getParameter("p3cw");
   slotNParms.p4cw = req.getParameter("p4cw");
   slotNParms.p5cw = req.getParameter("p5cw");
   //slotNParms.show1 = (req.getParameter("show1") == null) ? (short)0 : Short.parseShort(req.getParameter("show1"));
   //slotNParms.show2 = (req.getParameter("show2") == null) ? (short)0 : Short.parseShort(req.getParameter("show2"));
   //slotNParms.show3 = (req.getParameter("show3") == null) ? (short)0 : Short.parseShort(req.getParameter("show3"));
   //slotNParms.show4 = (req.getParameter("show4") == null) ? (short)0 : Short.parseShort(req.getParameter("show4"));
   //slotNParms.show5 = (req.getParameter("show5") == null) ? (short)0 : Short.parseShort(req.getParameter("show5"));
   slotNParms.day = req.getParameter("day");                      // name of day
   //slotNParms.sfb = req.getParameter("fb");                     // Front/Back indicator
   slotNParms.notes = req.getParameter("notes");                  // Proshop Notes
   slotNParms.jump = req.getParameter("jump");                    // jump index for _sheet
   //slotNParms.conf = req.getParameter("conf");                  // confirmation # (or Id) for Hotels

   if (req.getParameter("hide") != null) {                        // if hide notes parm exists
      slotNParms.hides = req.getParameter("hide");
   } else {
      slotNParms.hides = "No";
   }
   if (req.getParameter("suppressEmails") != null) {              // if email parm exists
      suppressEmails = req.getParameter("suppressEmails");
   }

   //
   //  set 9-hole options
   //
   slotNParms.p91 = 0;                       // init to 18 holes
   slotNParms.p92 = 0;
   slotNParms.p93 = 0;
   slotNParms.p94 = 0;
   slotNParms.p95 = 0;
   
   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      slotNParms.p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      slotNParms.p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      slotNParms.p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      slotNParms.p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      slotNParms.p95 = Integer.parseInt(p9s);
   }

   //
   //  Get member names for Unaccompanied Guests, if provided
   //
   if (req.getParameter("mem1") != null) {
      slotNParms.mem1 = req.getParameter("mem1");
   }
   if (req.getParameter("mem2") != null) {
      slotNParms.mem2 = req.getParameter("mem2");
   }
   if (req.getParameter("mem3") != null) {
      slotNParms.mem3 = req.getParameter("mem3");
   }
   if (req.getParameter("mem4") != null) {
      slotNParms.mem4 = req.getParameter("mem4");
   }
   if (req.getParameter("mem5") != null) {
      slotNParms.mem5 = req.getParameter("mem5");
   }

   //
   //  Get skip parm if provided
   //
   if (req.getParameter("skip") != null) {
         
      skips = req.getParameter("skip");
      skip = Integer.parseInt(skips);
   }

   //
   //  Ensure that there are no null player fields
   //
   if (slotNParms.player1 == null ) {
      slotNParms.player1 = "";
   }
   if (slotNParms.player2 == null ) {
      slotNParms.player2 = "";
   }
   if (slotNParms.player3 == null ) {
      slotNParms.player3 = "";
   }
   if (slotNParms.player4 == null ) {
      slotNParms.player4 = "";
   }
   if (slotNParms.player5 == null ) {
      slotNParms.player5 = "";
   }
   if (slotNParms.p1cw == null ) {
      slotNParms.p1cw = "";
   }
   if (slotNParms.p2cw == null ) {
      slotNParms.p2cw = "";
   }
   if (slotNParms.p3cw == null ) {
      slotNParms.p3cw = "";
   }
   if (slotNParms.p4cw == null ) {
      slotNParms.p4cw = "";
   }
   if (slotNParms.p5cw == null ) {
      slotNParms.p5cw = "";
   }

   //
   //  Convert date & time from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(slotNParms.sfb);
      ind = Integer.parseInt(index);       // get numeric value of index
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init
     
   if (slotNParms.hides.equals( "Yes" )) {
    
      hide = 1;
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;     
    
   if (!slotNParms.notes.equals( "" )) {
     
      notesL = slotNParms.notes.length();       // get length of notes
   }

   //
   //   use yy and mm and date to determine dd
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   dd = (int) date - temp;            // get day of month from date

   //
   //  put parms in Parameter Object for portability
   //
   slotNParms.date = date;
   slotNParms.time = time;
   slotNParms.mm = mm;
   slotNParms.yy = yy;
   slotNParms.dd = dd;
   slotNParms.fb = fb;
   slotNParms.ind = ind;                      // index value
   slotNParms.club = club;                    // name of club
   slotNParms.returnCourse = returnCourse;    // name of course for return to _sheet
   slotNParms.suppressEmails = suppressEmails;

   //
   //  Check if this tee slot is still 'in use' and still in use by this user??
   //
   //  This is necessary because the user may have gone away while holding this slot.  If the
   //  slot timed out (system timer), the slot would be marked 'not in use' and another
   //  user could pick it up.  The original holder could be trying to use it now.
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT event, player1, player2, player3, player4, username1, username2, username3, " +
         "username4, p1cw, p2cw, p3cw, p4cw, in_use, in_use_by, event_type, " +
         "show1, show2, show3, show4, player5, username5, p5cw, show5, proNew, proMod, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by, pos1, pos2, pos3, pos4, pos5 " +
         "FROM teecurr2 " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, slotNParms.course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {
                                                      
         event = rs.getString("event");
         slotNParms.oldPlayer1 = rs.getString("player1");
         slotNParms.oldPlayer2 = rs.getString("player2");
         slotNParms.oldPlayer3 = rs.getString("player3");
         slotNParms.oldPlayer4 = rs.getString("player4");
         slotNParms.oldUser1 = rs.getString("username1");
         slotNParms.oldUser2 = rs.getString("username2");
         slotNParms.oldUser3 = rs.getString("username3");
         slotNParms.oldUser4 = rs.getString("username4");
         slotNParms.oldp1cw = rs.getString("p1cw");
         slotNParms.oldp2cw = rs.getString("p2cw");
         slotNParms.oldp3cw = rs.getString("p3cw");
         slotNParms.oldp4cw = rs.getString("p4cw");
         slotNParms.in_use = rs.getInt("in_use");
         slotNParms.in_use_by = rs.getString("in_use_by");
         event_type = rs.getInt("event_type");
         slotNParms.oldPlayer5 = rs.getString("player5");
         slotNParms.oldUser5 = rs.getString("username5");
         slotNParms.oldp5cw = rs.getString("p5cw");
         proNew = rs.getInt("proNew");
         proMod = rs.getInt("proMod");
         slotNParms.userg1 = rs.getString("userg1");
         slotNParms.userg2 = rs.getString("userg2");
         slotNParms.userg3 = rs.getString("userg3");
         slotNParms.userg4 = rs.getString("userg4");
         slotNParms.userg5 = rs.getString("userg5");
         slotNParms.orig_by = rs.getString("orig_by");
         slotNParms.pos1 = rs.getShort("pos1");
         slotNParms.pos2 = rs.getShort("pos2");
         slotNParms.pos3 = rs.getShort("pos3");
         slotNParms.pos4 = rs.getShort("pos4");
         slotNParms.pos5 = rs.getShort("pos5");
      }
      pstmt.close();

      if (slotNParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab again)

         slotNParms.orig_by = user;             // set this user as the originator
      }

      if ((slotNParms.in_use == 0) || (!slotNParms.in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
         out.println("<BR><BR>Sorry, but this notification has been returned to the system.<BR>");
         out.println("<BR>The system timed out.");
         out.println("<BR><BR>");

         if (index.equals( "888" )) {      // if from Proshop_searchmem via proshop_main

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

         } else {

            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
            } else {
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
            }
            out.println("</form></font>");
         }
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }
   }
   catch (Exception ignore) {
   }

   //
   //  If request is to 'Cancel This Res', then clear all fields for this slot
   //
   if (req.getParameter("remove") != null) {

      if (slotNParms.pos1 != 0 || slotNParms.pos2 != 0 || slotNParms.pos3 != 0 || slotNParms.pos4 != 0 || slotNParms.pos5 != 0) {

         posSent = true;        // indicate POS already sent for this group (warning)
      }
      
      slotNParms.player1 = "";
      slotNParms.player2 = "";
      slotNParms.player3 = "";
      slotNParms.player4 = "";
      slotNParms.player5 = "";
      slotNParms.p1cw = "";
      slotNParms.p2cw = "";
      slotNParms.p3cw = "";
      slotNParms.p4cw = "";
      slotNParms.p5cw = "";
      slotNParms.user1 = "";
      slotNParms.user2 = "";
      slotNParms.user3 = "";
      slotNParms.user4 = "";
      slotNParms.user5 = "";
      slotNParms.userg1 = "";
      slotNParms.userg2 = "";
      slotNParms.userg3 = "";
      slotNParms.userg4 = "";
      slotNParms.userg5 = "";
      slotNParms.show1 = 0;
      slotNParms.show2 = 0;
      slotNParms.show3 = 0;
      slotNParms.show4 = 0;
      slotNParms.show5 = 0;
      slotNParms.notes = "";
      hide = 0;
      slotNParms.mNum1 = "";
      slotNParms.mNum2 = "";
      slotNParms.mNum3 = "";
      slotNParms.mNum4 = "";
      slotNParms.mNum5 = "";
      slotNParms.orig_by = "";
      slotNParms.conf = "";
      slotNParms.p91 = 0;
      slotNParms.p92 = 0;
      slotNParms.p93 = 0;
      slotNParms.p94 = 0;
      slotNParms.p95 = 0;
      slotNParms.pos1 = 0;
      slotNParms.pos2 = 0;
      slotNParms.pos3 = 0;
      slotNParms.pos4 = 0;
      slotNParms.pos5 = 0;

      emailCan = 1;      // send email notification for Cancel Request
      sendemail = 1;

      proMod++;      // increment number of mods for reports

   } else {

      //
      //  Process normal res request
      //
      //   Get the parms specified for this club
      //
      try {
         getClub.getParms(con, parm);        // get the club parms

         slotNParms.rnds = parm.rnds;
         slotNParms.hrsbtwn = parm.hrsbtwn;
      }
      catch (Exception ignore) {
      }

      //
      //  Make sure at least 1 player contains a name
      //
      if ((slotNParms.player1.equals( "" )) && (slotNParms.player2.equals( "" )) && (slotNParms.player3.equals( "" )) && (slotNParms.player4.equals( "" )) && (slotNParms.player5.equals( "" ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
         out.println("<BR>If you wish to remove all names from this slot, use the 'Cancel Notification' button.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         return1(out, slotNParms);
         return;

      }

      //
      //  At least 1 Player field is present - Make sure a C/W was specified for all players
      //
      if (((!slotNParms.player1.equals( "" )) && (!slotNParms.player1.equalsIgnoreCase( "x" )) && (slotNParms.p1cw.equals( "" ))) ||
          ((!slotNParms.player2.equals( "" )) && (!slotNParms.player2.equalsIgnoreCase( "x" )) && (slotNParms.p2cw.equals( "" ))) ||
          ((!slotNParms.player3.equals( "" )) && (!slotNParms.player3.equalsIgnoreCase( "x" )) && (slotNParms.p3cw.equals( "" ))) ||
          ((!slotNParms.player4.equals( "" )) && (!slotNParms.player4.equalsIgnoreCase( "x" )) && (slotNParms.p4cw.equals( "" ))) ||
          ((!slotNParms.player5.equals( "" )) && (!slotNParms.player5.equalsIgnoreCase( "x" )) && (slotNParms.p5cw.equals( "" )))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>You must specify a Cart or Walk option for all players.");
         out.println("<BR><BR>");
         //
         //  Return to _slot to change the player order
         //
         return1(out, slotNParms);
         return;
      }

      //
      //  Shift players up if any empty spots (start with Player1 position)
      //
      verifyNSlot.shiftUp(slotNParms);
        
      //
      //  Check if any player names are guest names (set userg1-5 if necessary)
      //
      try {
        
         verifyNSlot.parseGuests(slotNParms, con);

      }
      catch (Exception e1) {
      }


      //
      //  Make sure there are no duplicate names
      //
      player = "";
        

     if ((!slotNParms.player1.equals( "" )) && (!slotNParms.player1.equalsIgnoreCase( "x" )) && (slotNParms.g1.equals( "" ))) {

        if ((slotNParms.player1.equalsIgnoreCase( slotNParms.player2 )) || (slotNParms.player1.equalsIgnoreCase( slotNParms.player3 )) ||
            (slotNParms.player1.equalsIgnoreCase( slotNParms.player4 )) || (slotNParms.player1.equalsIgnoreCase( slotNParms.player5 ))) {

           player = slotNParms.player1;
        }
     }

     if ((!slotNParms.player2.equals( "" )) && (!slotNParms.player2.equalsIgnoreCase( "x" )) && (slotNParms.g2.equals( "" ))) {

        if ((slotNParms.player2.equalsIgnoreCase( slotNParms.player3 )) || (slotNParms.player2.equalsIgnoreCase( slotNParms.player4 )) ||
            (slotNParms.player2.equalsIgnoreCase( slotNParms.player5 ))) {

           player = slotNParms.player2;
        }
     }

     if ((!slotNParms.player3.equals( "" )) && (!slotNParms.player3.equalsIgnoreCase( "x" )) && (slotNParms.g3.equals( "" ))) {

        if ((slotNParms.player3.equalsIgnoreCase( slotNParms.player4 )) ||
            (slotNParms.player3.equalsIgnoreCase( slotNParms.player5 ))) {

           player = slotNParms.player3;
        }
     }

     if ((!slotNParms.player4.equals( "" )) && (!slotNParms.player4.equalsIgnoreCase( "x" )) && (slotNParms.g4.equals( "" ))) {

        if (slotNParms.player4.equalsIgnoreCase( slotNParms.player5 )) {

           player = slotNParms.player4;
        }
     }

     if (!player.equals( "" )) {          // if dup name found

        out.println(SystemUtils.HeadTitle("Data Entry Error"));
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<center>");
        out.println("<BR><BR><H3>Data Entry Error</H3>");
        out.println("<BR><BR><b>" + player + "</b> was specified more than once.");
        out.println("<BR><BR>Please correct this and try again.");
        out.println("<BR><BR>");
        //
        //  Return to _slot to change the player order
        //
        return1(out, slotNParms);
        return;
     }
        
      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifyNSlot.parseNames(slotNParms, "pro");
      }
      catch (Exception ignore) {
      }

      if ( error == true ) {          // if problem

         out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H3>Invalid Data Received</H3><BR>");
         out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
         out.println("Please check the names and try again.");
         out.println("<BR><BR>");

         return1(out, slotNParms);
         return;
      }

 
      //
      //  Get the usernames, membership types and hndcp's for players if matching name found
      //
      try {

         verifyNSlot.getUsers(slotNParms, con);
      }
      catch (Exception ignore) {
      }


      //
      //  Save the members' usernames for guest association 
      //
      memA[0] = slotNParms.user1;
      memA[1] = slotNParms.user2;
      memA[2] = slotNParms.user3;
      memA[3] = slotNParms.user4;
      memA[4] = slotNParms.user5;

      //
      //  Check if proshop user requested that we skip the following name test.
      //
      //  If any skips are set, then we've already been through here.
      //
      if (skip == 0) {

         int invalNum = 0;
         err_name = "";
           
         //
         //  Check if any of the names are invalid.  If so, ask proshop if they want to ignore the error.
         //
         if (slotNParms.inval5 != 0) {

            err_name = slotNParms.player5;
            invalNum = slotNParms.inval5;
         }

         if (slotNParms.inval4 != 0) {

            err_name = slotNParms.player4;
            invalNum = slotNParms.inval4;
         }

         if (slotNParms.inval3 != 0) {

            err_name = slotNParms.player3;
            invalNum = slotNParms.inval3;
         }

         if (slotNParms.inval2 != 0) {

            err_name = slotNParms.player2;
            invalNum = slotNParms.inval2;
         }

         if (slotNParms.inval1 != 0) {

            err_name = slotNParms.player1;
            invalNum = slotNParms.inval1;
         }

         if (!err_name.equals( "" )) {      // invalid name received

            out.println(SystemUtils.HeadTitle("Player Not Found - Prompt"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
              
            if (invalNum == 2) {        // if incomplete member record

               out.println("<BR><H3>Incomplete Member Record</H3><BR>");
               out.println("<BR><BR>Sorry, a member you entered has an imcomplete member record and cannot be included at this time.<BR>");
               out.println("<BR>Member Name:&nbsp;&nbsp;&nbsp;'" +err_name+ "'");
               out.println("<BR><BR>Please update this member's record via Admin and complete the required fields.");
               out.println("<BR><BR>You will have to remove this name from your notification.");
               out.println("<BR><BR>");

            } else {
  
               out.println("<BR><H3>Player's Name Not Found in System</H3><BR>");
               out.println("<BR><BR>Warning:  " + err_name + " does not exist in the system database.");
               out.println("<BR><BR>Would you like to override this check and allow this reservation?");
               out.println("<BR><BR>");
            }
  
            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
              
            if (invalNum == 2) {        // if incomplete member record

               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            } else {
              
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotNParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotNParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotNParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotNParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\"></form>");
            }
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }       // end of skip1

      //
      //  Check if proshop user requested that we skip the mship test (member exceeded max and proshop
      //  wants to override the violation).
      //
      //  If this skip, or any of the following skips are set, then we've already been through these tests.
      //
      if (skip < 2) {

         //
         //************************************************************************
         //  No, normal request -
         //  Check any membership types for max rounds per week, month or year
         //************************************************************************
         //
         if ((!slotNParms.mship1.equals( "" )) ||
             (!slotNParms.mship2.equals( "" )) ||
             (!slotNParms.mship3.equals( "" )) ||
             (!slotNParms.mship4.equals( "" )) ||
             (!slotNParms.mship5.equals( "" ))) {   // if at least one name exists then check number of rounds

            error = false;                             // init error indicator

            try { 
              
               error = verifyNSlot.checkMaxRounds(slotNParms, con);
            } catch (Exception e2) {
                
               String errorMsgX = "Check for Max Rounds (ProshopTLT_slot): exception=" +e2;
               SystemUtils.logError(errorMsgX);        // log the error message
            }

            if (error == true) {      // a member exceed the max allowed per week, month or year

               out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Warning:  " + slotNParms.player + " is a " + slotNParms.mship + " member and has exceeded the<BR>");
               out.println("maximum number of notifications allowed for this " + slotNParms.period + ".");
               out.println("<BR><BR>Would you like to override the limit and allow this notification?");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               returnToSlot(out, slotNParms);
               return;
            }
         }      // end of mship if
      }         // end of skip2 if

      //
      //  Check if proshop user requested that we skip the max # of guests test
      //
      //  If this skip, or any of the following skips are set, then we've already been through these tests.
      //
      if (skip < 3) {

         //
         //************************************************************************
         //  Check for max # of guests exceeded (per Member or per Notification)
         //************************************************************************
         //
         if (slotNParms.guests != 0) {      // if any guests were included

            error = false;                             // init error indicator

            try {

               error = verifyNSlot.checkMaxGuests(slotNParms, con);

            }
            catch (Exception e5) {

               dbError(out, e5, "");
               return;
            }

            if (error == true) {      // a member exceed the max allowed guests

                  out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Number of Guests Exceeded Limit</H3>");
                  out.println("<BR>Sorry, the maximum number of guests allowed for the<BR>");
                  out.println("time you are requesting is " +slotNParms.grest_num+ " per " +slotNParms.grest_per+ ".");
                  out.println("<BR>You have requested " +slotNParms.guests+ " guests and " +slotNParms.members+ " members.");
                  out.println("<BR><BR>Restriction Name = " +slotNParms.rest_name);
                  out.println("<BR><BR>Would you like to override the limit and allow this reservation?");
                  out.println("<BR><BR>");
                  
                  //
                  //  Return to _slot to change the player order
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form>");

                 out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
                 out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
                 out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
                 out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
                 out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
                 out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
                 out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
                 out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
                 out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
                 out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
                 out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
                 out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
                 out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
                 out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
                 out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
                 out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
                 out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
                 out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
                 out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
                 out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
                 out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
                 out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
                 out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotNParms.date + "\">");
                 out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotNParms.time + "\">");
                 out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotNParms.mm + "\">");
                 out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotNParms.yy + "\">");
                 out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                 out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
                 out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
                 out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
                 out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
                 out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                 out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
                 out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
                 out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
                 out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
                 out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
                 out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
                 out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                 out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\">");

                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }
         
         }      // end of if guests

      }  // end of skip3 if


      //
      //  Check if proshop user requested that we skip the member restrictions test
      //
      //  If this skip, or any following skips are set, then we've already been through these tests.
      //
      if (skip < 4) {

         //
         // *******************************************************************************
         //  Check member restrictions
         //
         //     First, find all restrictions within date & time constraints on this course.
         //     Then, find the ones for this day.
         //     Then, find any for this member type or membership type (all 5 players).
         //
         // *******************************************************************************
         //
         error = false;                             // init error indicator

         try {

            error = verifyNSlot.checkMemRests(slotNParms, con);

         }
         catch (Exception e7) {

            dbError(out, e7, "");
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotNParms.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            returnToSlot(out, slotNParms);
            return;
         }

      }  // end of skip4 if

      //
      //  Check if proshop user requested that we skip the 5-some restrictions test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 5) {

         //
         // *******************************************************************************
         //  Check 5-some restrictions
         //
         //   If 5-somes are restricted during this tee time, warn the proshop user.
         // *******************************************************************************
         //
         if ((!slotNParms.player5.equals( "" )) && (slotNParms.p5rest.equals( "Yes" ))) { // if 5-somes restricted prompt user to skip test

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>5-somes</b> are restricted during this time.<br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotNParms);
            return;
         }

      }  // end of skip5 if


      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If either skip is set, then we've already been through these tests.
      //
      if (skip < 6) {

         //
         // *******************************************************************************
         //  Check Member Number restrictions
         //
         //     First, find all restrictions within date & time constraints
         //     Then, find the ones for this day
         //     Then, check all players' member numbers against all others in the time period
         //
         // *******************************************************************************
         //
         error = false;                             // init error indicator

         try {

            error = verifyNSlot.checkMemNum(slotNParms, con);

         }
         catch (Exception e7) {

            dbError(out, e7, "");
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
            out.println("<BR>Sorry, ");
            if (!slotNParms.pnum1.equals( "" )) {
               out.println("<b>" + slotNParms.pnum1 + "</b> ");
            }
            if (!slotNParms.pnum2.equals( "" )) {
               out.println("<b>" + slotNParms.pnum2 + "</b> ");
            }
            if (!slotNParms.pnum3.equals( "" )) {
               out.println("<b>" + slotNParms.pnum3 + "</b> ");
            }
            if (!slotNParms.pnum4.equals( "" )) {
               out.println("<b>" + slotNParms.pnum4 + "</b> ");
            }
            if (!slotNParms.pnum5.equals( "" )) {
               out.println("<b>" + slotNParms.pnum5 + "</b> ");
            }
            out.println("is/are restricted from playing during this time because the");
            out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
            out.println("<br><br>This time slot has the following restriction:  <b>" + slotNParms.rest_name + "</b>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotNParms);
            return;
         }
                    
      }         // end of IF skip6


      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 7) {

         //
         //***********************************************************************************************
         //
         //    Now check if any of the players are already scheduled today
         //
         //***********************************************************************************************
         //
         slotNParms.hit = false;                             // init error indicator
         slotNParms.hit2 = false;                             // init error indicator
         String tmsg = "";
         int thr = 0;
         int tmin = 0;


         try {

            verifyNSlot.checkSched(slotNParms, con);

         }
         catch (Exception e21) {

            dbError(out, e21, "");
            return;
         }

         if (slotNParms.hit == true || slotNParms.hit2 == true || slotNParms.hit3 == true) { // if we hit on a duplicate res

            if (slotNParms.time2 != 0) {                                  // if other time was returned

               thr = slotNParms.time2 / 100;                      // set time string for message
               tmin = slotNParms.time2 - (thr * 100);
               if (thr == 12) {
                  if (tmin < 10) {
                     tmsg = thr+ ":0" +tmin+ " PM";
                  } else {
                     tmsg = thr+ ":" +tmin+ " PM";
                  }
               } else {
                  if (thr > 12) {
                     thr = thr - 12;
                     if (tmin < 10) {
                        tmsg = thr+ ":0" +tmin+ " PM";
                     } else {
                        if (tmin < 10) {
                           tmsg = thr+ ":0" +tmin+ " PM";
                        } else {
                           tmsg = thr+ ":" +tmin+ " PM";
                        }
                     }
                  } else {
                     if (tmin < 10) {
                        tmsg = thr+ ":0" +tmin+ " AM";
                     } else {
                        tmsg = thr+ ":" +tmin+ " AM";
                     }
                  }
               }
               if (!slotNParms.course2.equals( "" )) {        // if course provided

                  tmsg = tmsg + " on the " +slotNParms.course2+ " course";
               }
            }
            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
            if (slotNParms.rnds > 1) {       // if multiple rounds per day supported
               if (slotNParms.hit3 == true) {       // if rounds too close together
                  out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is scheduled to play another round within " +slotNParms.hrsbtwn+ " hours.<br><br>");
                  out.println(slotNParms.player + " is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
               } else {
                  out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
                  out.println("A player can only be scheduled " +slotNParms.rnds+ " times per day.<br><br>");
               }
            } else {
               if (slotNParms.hit2 == true) {
                  out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is part of a lottery request for this date.<br><br>");
               } else {
                  out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
               }
               out.println("A player can only be scheduled once per day.<br><br>");
            }

            out.println("<BR><BR>Would you like to override this and allow the reservation?");
            out.println("<BR><BR>");
               
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotNParms);
            return;
         }
      
      }         // end of IF skip7

      if (skip < 8) {

         //
         //***********************************************************************************************
         //
         //    Now check all players for 'days in advance' - based on membership types
         //
         //***********************************************************************************************
         //
         if (!slotNParms.mship1.equals( "" ) || !slotNParms.mship2.equals( "" ) || !slotNParms.mship3.equals( "" ) || 
             !slotNParms.mship4.equals( "" ) || !slotNParms.mship5.equals( "" )) {

           try {

              error = verifyNSlot.checkDaysAdv(slotNParms, con);

           }
           catch (Exception e21) {

              dbError(out, e21, "");
              return;
           }

           if (error == true) {          // if we hit on a violation

              out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
              out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
              out.println("<hr width=\"40%\">");
              out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
              out.println("<BR>Sorry, <b>" + slotNParms.player + "</b> is not allowed to be part of a tee time this far in advance.<br><br>");
              out.println("This restriction is based on the 'Days In Advance' setting for each Membership Type.<br><br>");
              out.println("<BR><BR>Would you like to override this and allow the reservation?");
              out.println("<BR><BR>");
              //
              //  Return to _slot
              //
              returnToSlot(out, slotNParms);
              return;
           }
          
         }
      }         // end of IF skip8

      //
      //  Check if proshop user requested that we skip the following test
      //
      //  If this skip is set, then we've already been through these tests.
      //
      if (skip < 9) {     // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

         //
         //***********************************************************************************************
         //
         //    Now check the order of guests and members (guests must follow a member) - prompt to verify order
         //
         //***********************************************************************************************
         //
         if (slotNParms.guests != 0 && slotNParms.members != 0) {      // if both guests and members were included

            if (slotNParms.g1.equals( "" )) {              // if slot 1 is not a guest

               //
               //  Both guests and members specified - determine guest owners by order
               //
               gi = 0;
               memberName = "";
               while (gi < 5) {                  // cycle thru arrays and find guests/members

                  if (!slotNParms.gstA[gi].equals( "" )) {

                     usergA[gi] = memberName;       // get last players username
                  } else {
                     usergA[gi] = "";               // init field
                  }
                  if (!memA[gi].equals( "" )) {

                     memberName = memA[gi];        // get players username
                  }
                  gi++;
               }
               slotNParms.userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
               slotNParms.userg2 = usergA[1];
               slotNParms.userg3 = usergA[2];
               slotNParms.userg4 = usergA[3];
               slotNParms.userg5 = usergA[4];
            }
              
            if (!slotNParms.g1.equals( "" ) || slotNParms.members > 1) {  // if slot 1 is a guest OR more than 1 member

               //
               //  At least one guest and one member have been specified.
               //  Prompt user to verify the order.
               //
               //  Only require positioning if a POS system was specified for this club (saved in Login)
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

               //
               // if slot 1 is a guest & POS & not already assigned
               //
               if (!slotNParms.g1.equals( "" ) && !posType.equals( "" ) && !slotNParms.oldPlayer1.equals( slotNParms.player1 )) {

                  out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("You cannot have a guest in the first player position when one or more members are also specified.");
                  out.println("<BR><BR>");
               } else {
                  out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("Please verify that the following order is correct:");
                  out.println("<BR><BR>");
                  out.println(slotNParms.player1 + " <BR>");
                  out.println(slotNParms.player2 + " <BR>");
                  if (!slotNParms.player3.equals( "" )) {
                     out.println(slotNParms.player3 + " <BR>");
                  }
                  if (!slotNParms.player4.equals( "" )) {
                     out.println(slotNParms.player4 + " <BR>");
                  }
                  if (!slotNParms.player5.equals( "" )) {
                     out.println(slotNParms.player5 + " <BR>");
                  }
                  out.println("<BR>Would you like to process the request as is?");
               }

               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");

               if (!slotNParms.g1.equals( "" ) && !posType.equals( "" ) && !slotNParms.oldPlayer1.equals( slotNParms.player1 )) {

                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

                  //
                  //  Return to process the players as they are
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotNParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotNParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotNParms.mm + "\">");
                  out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotNParms.yy + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotNParms.userg1 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotNParms.userg2 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotNParms.userg3 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotNParms.userg4 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotNParms.userg5 + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  out.println("<input type=\"submit\" value=\"YES - Continue\" name=\"submitForm\"></form></font>");
               }
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;
            }

         } else {

            //
            //  Either all members or all guests - check for all guests (Unaccompanied Guests)
            //
            if (slotNParms.guests != 0) {      // if all guests 

               //
               //  At least one guest and no member has been specified.
               //  Get associated member names if already assigned.
               //
               try {
                 
                  if (!slotNParms.userg1.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotNParms.userg1);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotNParms.mem1 = mem_name.toString();                      // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotNParms.userg2.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotNParms.userg2);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotNParms.mem2 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotNParms.userg3.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotNParms.userg3);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotNParms.mem3 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotNParms.userg4.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotNParms.userg4);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotNParms.mem4 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotNParms.userg5.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotNParms.userg5);

                     rs = pstmtc.executeQuery();

                     if (rs.next()) {

                        // Get the member's full name.......

                        StringBuffer mem_name = new StringBuffer(rs.getString(2));  // get first name

                        String mi = rs.getString(3);                                // middle initial
                        if (!mi.equals( "" )) {
                           mem_name.append(" ");
                           mem_name.append(mi);
                        }
                        mem_name.append(" " + rs.getString(1));                     // last name

                        slotNParms.mem5 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
               }
               catch (Exception ignore) {
               }
                 
               //
               //  Prompt user to specify associated member(s) or skip.
               //
               out.println(SystemUtils.HeadTitle("Guests Specified - Prompt"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Player/Guest Association Prompt</H3><BR>");

               if (slotNParms.guests == 1) {      // if one guest
                  out.println("You are requesting a tee time for an unaccompanied guest.<br>");
                  out.println("The guest should be associated with a member.<br><br>");
                  out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
               } else {
                  out.println("You are requesting a tee time for unaccompanied guests.<br>");
                  out.println("Guests should be associated with a member.<br><br>");
                  out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
               }

               //
               //  Return to _slot (doPost) to assign members
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"mem1\" value=\"" + slotNParms.mem1 + "\">");
               out.println("<input type=\"hidden\" name=\"mem2\" value=\"" + slotNParms.mem2 + "\">");
               out.println("<input type=\"hidden\" name=\"mem3\" value=\"" + slotNParms.mem3 + "\">");
               out.println("<input type=\"hidden\" name=\"mem4\" value=\"" + slotNParms.mem4 + "\">");
               out.println("<input type=\"hidden\" name=\"mem5\" value=\"" + slotNParms.mem5 + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");  // assign member to guests
               out.println("<input type=\"submit\" value=\"Yes - Assign Member\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               //
               //  Return to process the players as they are
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotNParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotNParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotNParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotNParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotNParms.userg1 + "\">");
               out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotNParms.userg2 + "\">");
               out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotNParms.userg3 + "\">");
               out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotNParms.userg4 + "\">");
               out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotNParms.userg5 + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
               out.println("<input type=\"submit\" value=\"No - Continue\" name=\"submitForm\"></form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
               return;

            }
         }      // end of IF any guests specified

      } else {   // skip 9 requested?
        
         if (skip == 9) {   // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

            //
            //  User has responded to the guest association prompt - process tee time request in specified order
            //
            slotNParms.userg1 = req.getParameter("userg1"); 
            slotNParms.userg2 = req.getParameter("userg2");
            slotNParms.userg3 = req.getParameter("userg3");
            slotNParms.userg4 = req.getParameter("userg4");
            slotNParms.userg5 = req.getParameter("userg5");
         }
      }         // end of IF skip9 
      
      if (skip < 12) {

         //
         //***********************************************************************************************
         //
         //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
         //
         //***********************************************************************************************
         //
         if (!slotNParms.userg1.equals( "" ) || !slotNParms.userg2.equals( "" ) || !slotNParms.userg3.equals( "" ) ||
             !slotNParms.userg4.equals( "" ) || !slotNParms.userg5.equals( "" )) {

            try {

               error = verifyNSlot.checkGuestQuota(slotNParms, con);
            }
            catch (Exception e22) {
            }

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, requesting <b>" + slotNParms.player + "</b> exceeds the guest quota established for this guest type.");
               out.println("<br><br>You will have to remove the guest in order to complete this request.");
               out.println("<BR><BR>");
               out.println("<BR>Would you like to override this restriction and allow the tee time request?");
               //
               //  Return to _slot (doPost) to assign members
               //
               returnToSlot(out, slotNParms);
               return;
            }
                
         }   // end of IF guests

      } else {   // skip 12 requested?

         if (skip == 12) {

            //
            //  We must restore the guest usernames
            //
            slotNParms.userg1 = req.getParameter("userg1");
            slotNParms.userg2 = req.getParameter("userg2");
            slotNParms.userg3 = req.getParameter("userg3");
            slotNParms.userg4 = req.getParameter("userg4");
            slotNParms.userg5 = req.getParameter("userg5");
         }
      }     // end of IF skip 12


      //**************************************************************
      //  Verification Complete !!!!!!!!
      //**************************************************************

      sendemail = 0;         // init email flags
      emailNew = 0;
      emailMod = 0;

      //
      //  Make sure there is a member in the tee time slot
      //    If not, no email and no statistic counted
      //
      if (((!slotNParms.player1.equals( "" )) && (!slotNParms.player1.equalsIgnoreCase( "x" )) && (slotNParms.g1.equals( "" ))) ||
          ((!slotNParms.player2.equals( "" )) && (!slotNParms.player2.equalsIgnoreCase( "x" )) && (slotNParms.g2.equals( "" ))) ||
          ((!slotNParms.player3.equals( "" )) && (!slotNParms.player3.equalsIgnoreCase( "x" )) && (slotNParms.g3.equals( "" ))) ||
          ((!slotNParms.player4.equals( "" )) && (!slotNParms.player4.equalsIgnoreCase( "x" )) && (slotNParms.g4.equals( "" ))) ||
          ((!slotNParms.player5.equals( "" )) && (!slotNParms.player5.equalsIgnoreCase( "x" )) && (slotNParms.g5.equals( "" )))) {

         //
         //  If players changed, then set email flag
         //
         // see if the player has changed - send email notification to all if true
         // if new tee time oldPlayer1 will be empty 
         //
         if (!slotNParms.player1.equals( slotNParms.oldPlayer1 )) {
            sendemail = 1;
         }

         if (!slotNParms.player2.equals( slotNParms.oldPlayer2 )) {
            sendemail = 1;
         }

         if (!slotNParms.player3.equals( slotNParms.oldPlayer3 )) {
            sendemail = 1;
         }

         if (!slotNParms.player4.equals( slotNParms.oldPlayer4 )) {
            sendemail = 1;
         }

         if (!slotNParms.player5.equals( slotNParms.oldPlayer5 )) {
            sendemail = 1;
         }

         //
         //  Verification complete -
         //   Set email type based on new or update request (cancel set above)
         //   Also, bump stats counters for reports
         //
         if ((!slotNParms.oldPlayer1.equals( "" )) || (!slotNParms.oldPlayer2.equals( "" )) || (!slotNParms.oldPlayer3.equals( "" )) ||
             (!slotNParms.oldPlayer4.equals( "" )) || (!slotNParms.oldPlayer5.equals( "" ))) {

            proMod++;      // increment number of mods
            emailMod = 1;  // tee time was modified

         } else {

            proNew++;      // increment number of new tee times
            emailNew = 1;  // tee time is new
         }
      }

      //
      //  Set show values
      //
      if (slotNParms.player1.equals( "" ) || slotNParms.player1.equalsIgnoreCase( "x" )) {

         slotNParms.show1 = 0;       // reset show parm if no player
      }
        
      if (slotNParms.player2.equals( "" ) || slotNParms.player2.equalsIgnoreCase( "x" )) {

         slotNParms.show2 = 0;       // reset show parm if no player
      }

      if (slotNParms.player3.equals( "" ) || slotNParms.player3.equalsIgnoreCase( "x" )) {

         slotNParms.show3 = 0;       // reset show parm if no player
      }

      if (slotNParms.player4.equals( "" ) || slotNParms.player4.equalsIgnoreCase( "x" )) {

         slotNParms.show4 = 0;       // reset show parm if no player
      }

      if (slotNParms.player5.equals( "" ) || slotNParms.player5.equalsIgnoreCase( "x" )) {

         slotNParms.show5 = 0;       // reset show parm if no player
      }

      //
      //   set show value if double check-in feature supported
      //
      if ((!slotNParms.player1.equals( "" ) && !slotNParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player2.equals( "" ) && !slotNParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player3.equals( "" ) && !slotNParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player4.equals( "" ) && !slotNParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player5.equals( "" ) && !slotNParms.player5.equalsIgnoreCase( "x" ))) {

         // set show values to 2 if feature is supported and teetime is today
         GregorianCalendar cal_pci = new GregorianCalendar();
         short tmp_pci = (
            parm.precheckin == 1 &&
            mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
            dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
            yy == cal_pci.get(cal_pci.YEAR)
         ) ? (short)2 : (short)0;

         //
         //  If players changed and have not already been check in, then set the new no-show value
         //
         if (!slotNParms.player1.equals( slotNParms.oldPlayer1 ) && slotNParms.show1 == 0) {
            slotNParms.show1 = tmp_pci;
         }

         if (!slotNParms.player2.equals( slotNParms.oldPlayer2 ) && slotNParms.show2 == 0) {
            slotNParms.show2 = tmp_pci;
         }

         if (!slotNParms.player3.equals( slotNParms.oldPlayer3 ) && slotNParms.show3 == 0) {
            slotNParms.show3 = tmp_pci;
         }

         if (!slotNParms.player4.equals( slotNParms.oldPlayer4 ) && slotNParms.show4 == 0) {
            slotNParms.show4 = tmp_pci;
         }

         if (!slotNParms.player5.equals( slotNParms.oldPlayer5 ) && slotNParms.show5 == 0) {
            slotNParms.show5 = tmp_pci;
         }
      }     // end set show values

      //
      //  Adjust POS values if necessary
      //
      if ((!slotNParms.player1.equals( "" ) && !slotNParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player2.equals( "" ) && !slotNParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player3.equals( "" ) && !slotNParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player4.equals( "" ) && !slotNParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotNParms.player5.equals( "" ) && !slotNParms.player5.equalsIgnoreCase( "x" ))) {

         //
         //  If player has changed and pos already sent, then reset the pos value
         //
         if (!slotNParms.player1.equals( slotNParms.oldPlayer1 ) && slotNParms.pos1 == 1) {
            slotNParms.pos1 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotNParms.player2.equals( slotNParms.oldPlayer2 ) && slotNParms.pos2 == 1) {
            slotNParms.pos2 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotNParms.player3.equals( slotNParms.oldPlayer3 ) && slotNParms.pos3 == 1) {
            slotNParms.pos3 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotNParms.player4.equals( slotNParms.oldPlayer4 ) && slotNParms.pos4 == 1) {
            slotNParms.pos4 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotNParms.player5.equals( slotNParms.oldPlayer5 ) && slotNParms.pos5 == 1) {
            slotNParms.pos5 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }
      }        // end pos tests

   }  // end of 'Process normal res request' else portion of 'cancel this res' if

   //
   //  Update the tee slot in teecurr
   //
   try {

      // NOTE:
      // For the 'Check In' during tee time editing to work and not break
      // the precheckin functionality, we need to remove the showx update from this query
      // and process them individually
      //
      PreparedStatement pstmt6 = con.prepareStatement (
         "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
         "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
         "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
         //"hndcp4 = ?, show1 = ?, show2 = ?, show3 = ?, show4 = ?, player5 = ?, username5 = ?, " + 
         "hndcp4 = ?, player5 = ?, username5 = ?, " + 
         //"p5cw = ?, hndcp5 = ?, show5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
         "p5cw = ?, hndcp5 = ?, notes = ?, hideNotes = ?, proNew = ?, proMod = ?, " +
         "mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
         "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, conf = ?, " +
         "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ?, pos1 = ?, pos2 = ?, pos3 = ?, pos4 = ?, pos5 = ? " +
         "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt6.clearParameters();        // clear the parms
      pstmt6.setString(1, slotNParms.player1);
      pstmt6.setString(2, slotNParms.player2);
      pstmt6.setString(3, slotNParms.player3);
      pstmt6.setString(4, slotNParms.player4);
      pstmt6.setString(5, slotNParms.user1);
      pstmt6.setString(6, slotNParms.user2);
      pstmt6.setString(7, slotNParms.user3);
      pstmt6.setString(8, slotNParms.user4);
      pstmt6.setString(9, slotNParms.p1cw);
      pstmt6.setString(10, slotNParms.p2cw);
      pstmt6.setString(11, slotNParms.p3cw);
      pstmt6.setString(12, slotNParms.p4cw);
      pstmt6.setFloat(13, slotNParms.hndcp1);
      pstmt6.setFloat(14, slotNParms.hndcp2);
      pstmt6.setFloat(15, slotNParms.hndcp3);
      pstmt6.setFloat(16, slotNParms.hndcp4);
      //pstmt6.setShort(17, slotNParms.show1);
      //pstmt6.setShort(18, slotNParms.show2);
      //pstmt6.setShort(19, slotNParms.show3);
      //pstmt6.setShort(20, slotNParms.show4);
      pstmt6.setString(17, slotNParms.player5);
      pstmt6.setString(18, slotNParms.user5);
      pstmt6.setString(19, slotNParms.p5cw);
      pstmt6.setFloat(20, slotNParms.hndcp5);
      //pstmt6.setShort(25, slotNParms.show5);
      pstmt6.setString(21, slotNParms.notes);
      pstmt6.setInt(22, hide);
      pstmt6.setInt(23, proNew);
      pstmt6.setInt(24, proMod);
      pstmt6.setString(25, slotNParms.mNum1);
      pstmt6.setString(26, slotNParms.mNum2);
      pstmt6.setString(27, slotNParms.mNum3);
      pstmt6.setString(28, slotNParms.mNum4);
      pstmt6.setString(29, slotNParms.mNum5);
      pstmt6.setString(30, slotNParms.userg1);
      pstmt6.setString(31, slotNParms.userg2);
      pstmt6.setString(32, slotNParms.userg3);
      pstmt6.setString(33, slotNParms.userg4);
      pstmt6.setString(34, slotNParms.userg5);
      pstmt6.setString(35, slotNParms.orig_by);
      pstmt6.setString(36, slotNParms.conf);
      pstmt6.setInt(37, slotNParms.p91);
      pstmt6.setInt(38, slotNParms.p92);
      pstmt6.setInt(39, slotNParms.p93);
      pstmt6.setInt(40, slotNParms.p94);
      pstmt6.setInt(41, slotNParms.p95);
      pstmt6.setInt(42, slotNParms.pos1);
      pstmt6.setInt(43, slotNParms.pos2);
      pstmt6.setInt(44, slotNParms.pos3);
      pstmt6.setInt(45, slotNParms.pos4);
      pstmt6.setInt(46, slotNParms.pos5);
        
      pstmt6.setLong(47, slotNParms.date);
      pstmt6.setInt(48, slotNParms.time);
      pstmt6.setInt(49, slotNParms.fb);
      pstmt6.setString(50, slotNParms.course);

      count = pstmt6.executeUpdate();      // execute the prepared stmt

      //
      // Now update each of the showx fields for this tee time
      // 
      String strSQL = "";
      String tmpClause = "";
      
      strSQL = "UPDATE teecurr2 SET show1 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotNParms.show1 == 0) ? " AND show1 != 2" : "";
     
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotNParms.show1);
      pstmt6.setLong(2, slotNParms.date);
      pstmt6.setInt(3, slotNParms.time);
      pstmt6.setInt(4, slotNParms.fb);
      pstmt6.setString(5, slotNParms.course);
      pstmt6.executeUpdate();
         
      
      strSQL = "UPDATE teecurr2 SET show2 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotNParms.show2 == 0) ? " AND show2 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotNParms.show2);
      pstmt6.setLong(2, slotNParms.date);
      pstmt6.setInt(3, slotNParms.time);
      pstmt6.setInt(4, slotNParms.fb);
      pstmt6.setString(5, slotNParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show3 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotNParms.show3 == 0) ? " AND show3 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotNParms.show3);
      pstmt6.setLong(2, slotNParms.date);
      pstmt6.setInt(3, slotNParms.time);
      pstmt6.setInt(4, slotNParms.fb);
      pstmt6.setString(5, slotNParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show4 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotNParms.show4 == 0) ? " AND show4 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotNParms.show4);
      pstmt6.setLong(2, slotNParms.date);
      pstmt6.setInt(3, slotNParms.time);
      pstmt6.setInt(4, slotNParms.fb);
      pstmt6.setString(5, slotNParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show5 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotNParms.show5 == 0) ? " AND show5 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotNParms.show5);
      pstmt6.setLong(2, slotNParms.date);
      pstmt6.setInt(3, slotNParms.time);
      pstmt6.setInt(4, slotNParms.fb);
      pstmt6.setString(5, slotNParms.course);
      pstmt6.executeUpdate();
      
      pstmt6.close();

   }
   catch (Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<center>");
      out.println("<BR><BR><H2>Database Access Error</H2>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      //
      //  Return to _slot to change the player order
      //
      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
      out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
      out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
      out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
      out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
   //
   String fullName = "Proshop User";
     
   if (slotNParms.oldPlayer1.equals( "" ) && slotNParms.oldPlayer2.equals( "" ) && slotNParms.oldPlayer3.equals( "" ) &&
       slotNParms.oldPlayer4.equals( "" ) && slotNParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(slotNParms.date, slotNParms.day, slotNParms.time, slotNParms.fb, slotNParms.course, slotNParms.player1, slotNParms.player2, slotNParms.player3,
                             slotNParms.player4, slotNParms.player5, user, fullName, 0, con);

   } else {

      //  update tee time
      SystemUtils.updateHist(slotNParms.date, slotNParms.day, slotNParms.time, slotNParms.fb, slotNParms.course, slotNParms.player1, slotNParms.player2, slotNParms.player3,
                             slotNParms.player4, slotNParms.player5, user, fullName, 1, con);
   }

   //
   //  Build the HTML page to confirm reservation for user
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //   
   //
   if (index.equals( "888" )) {         // if came from proshop_searchmain

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<title>Proshop Tee Slot Page</title>");
      if (posSent == false) {        // if pos charges not already sent
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?search=yes\">");
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");
           
         if (posSent == true) {        // if pos charges already sent for this group
           
            out.println("<p><b>WARNING</b>&nbsp;&nbsp;Charges have already been sent to the POS System for one or more players in this group.<br>");
            out.println("You should use the POS System to cancel the charges.</p>");
         }
           
      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
      out.println("<p>&nbsp;</p></font>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {                             // came from proshop_sheet

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<Title>Proshop Tee Slot Page</Title>");
      if (posSent == false) {        // if pos charges not already sent
         if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + returnCourse + "&jump=" + slotNParms.jump + "\">");
         } else {
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + slotNParms.course + "&jump=" + slotNParms.jump + "\">");
         }
      }
      out.println("</HEAD>");
      out.println("<BODY bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<font size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");

      if (req.getParameter("remove") != null) {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;The reservation has been cancelled.</p>");

      } else {

         out.println("<p>&nbsp;</p><p>&nbsp;<b>Thank you!</b>&nbsp;&nbsp;Your reservation has been accepted and processed.</p>");

         if (notesL > 254) {

         out.println("<p>&nbsp;</p><b>Notice:</b>&nbsp;&nbsp;The notes you entered exceeded 254 characters in length.  All characters beyond 254 will be truncated.</p>");
         }
      }
        
      if (posSent == true) {        // if pos charges already sent for this group

         out.println("<p><br><b>WARNING</b>&nbsp;&nbsp;Charges have already been sent to the POS System for one or more players in this group.<br>");
         out.println("You should use the POS System to cancel the charges.</p>");
      }

      out.println("<p>&nbsp;</p></font>");

      out.println("<table border=\"0\" cols=\"1\" bgcolor=\"#8B8970\" cellpadding=\"8\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + returnCourse + "\">");
      } else {
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
      }
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" + slotNParms.jump + ">");
      out.println("<tr><td><font size=\"2\">");
      out.println("<input type=\"submit\" value=\"Return\">");
      out.println("</font></td></tr></form></table>");
   }

    //
    //  End of HTML page
    //
    out.println("</center></font></body></html>");
    out.close();

    try {

        resp.flushBuffer();      // force the repsonse to complete
    } catch (Exception ignore) {}
   
 }       // end of Verify


 // *********************************************************
 //  Process cancel request from ProshopTLT_slot (HTML) - 'Go Back'
 // *********************************************************

 private void cancel(HttpServletRequest req, PrintWriter out, String club, Connection con) {


    int count = 0;
    int time  = 0;
    int fb  = 0;
    long date  = 0;

    //
    // Get all the parameters entered
    //
    String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
    String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
    String sfb = req.getParameter("fb");               //  front/back indicator
    String index = req.getParameter("index");          //  index value of day (needed by Proshop_sheet when returning)
    String course = req.getParameter("course");        //  name of course
    String returnCourse = req.getParameter("returnCourse");        //  name of course to return to
    String day = req.getParameter("day");              //  name of the day
    String mm = req.getParameter("mm");              //  name of the day
    String yy = req.getParameter("yy");              //  name of the day

    //
    // Get our notify uid if we are here to edit an existing notification
    //
    String snid = req.getParameter("notifyId");
    if (snid == null) snid = "0";
    int notify_id = 0;

    //
    //  Convert the values from string to int
    //
    try {

        date = Long.parseLong(sdate);
        time = Integer.parseInt(stime);
        notify_id = Integer.parseInt(snid);
    }
    catch (NumberFormatException e) {
    }

    if (notify_id != 0) {
        
        //
        //  Clear the 'in_use' flag for this time slot in notifications
        //
        out.println("<!-- Clearing IN_USE flag for notification #" + notify_id + " -->");

        try {

            PreparedStatement pstmt1 = con.prepareStatement (
                "UPDATE notifications SET in_use_by = '', in_use_at = '0000-00-00 00:00:00' WHERE notification_id = ?");

            pstmt1.clearParameters();
            pstmt1.setInt(1, notify_id);
            count = pstmt1.executeUpdate();
            pstmt1.close();
        }
        catch (Exception ignore) {
        }
    }
    
    //
    //  Prompt user to return to Proshop_sheet or Proshop_notify    (CHANGED 888 was return to Proshop_searchmem)
    //
    //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
    //
    if (index.equals( "888" )) {       // if originated from Proshop_notify

        out.println(SystemUtils.HeadTitle("Create New Notification"));
        out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
        out.println("<frame name=\"top\" src=\"/" +rev+ "/servlet/Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
        out.println("<frame name=\"bot\" src=\"/" +rev+ "/servlet/Proshop_notify?index=" + index + "&course=" + course + "&mm=" + mm + "&yy=" + yy + "\" marginheight=\"1\">");
        out.println("</frameset>");

        out.println("</html>");

    } else {

        if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
            course = returnCourse;
        }
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
        out.println("<title>Proshop Tee Slot Page</title>");
        out.println("<meta http-equiv=\"refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_dsheet?index=" + index + "&course=" + course + "\">");
        out.println("</HEAD>");
        out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
        out.println("<hr width=\"40%\">");
        out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
        out.println("<BR><BR>Thank you, the notification has been returned to the system without changes.");
        out.println("<BR><BR>");

        out.println("<font size=\"2\">");
        out.println("<form action=\"/" +rev+ "/servlet/Proshop_dsheet\" method=\"post\" target=\"_top\">");
        out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
        out.println("</form></font>");
    }
    out.println("</CENTER></BODY></HTML>");
    out.close();
 }


 // *********************************************************
 //  Return to Proshop_slot
 // *********************************************************

 private void return1(PrintWriter out, parmNSlot slotNParms) {

   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotNParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotNParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotNParms.suppressEmails + "\">");
   out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }


 // *********************************************************
 //  Return to Proshop_slot
 // *********************************************************

 private void returnToSlot(PrintWriter out, parmNSlot slotNParms) {

   //
   //  Prompt user for return
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/ProshopTLT_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"notifyId\" value=\"" + slotNParms.notify_id + "\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotNParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotNParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotNParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotNParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotNParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotNParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotNParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotNParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotNParms.p5rest + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotNParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotNParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotNParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotNParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotNParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotNParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotNParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotNParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotNParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotNParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotNParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotNParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotNParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotNParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotNParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotNParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotNParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotNParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotNParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotNParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotNParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotNParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotNParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotNParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotNParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotNParms.suppressEmails + "\">");
   out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1, String msg) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>" + msg);
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }

 
 // *********************************************************
 //  Update players for a notification
 // *********************************************************
 
 private void updateNotificationPlayers(int notify_id, String player_name, String old_player, String username, String cw, String oldcw, int p9hole, int oldp9hole, int pos, Connection con, PrintWriter out) {

    try {
        
        // delete if this condition is true
        if ( player_name.equals("") && !old_player.equals("") ) {

            out.println("<!-- DELETING player"+pos+" | player_name=" + player_name + " | old_player=" + old_player + " -->");

            PreparedStatement pstmt = con.prepareStatement ("DELETE FROM notifications_players WHERE notification_id = ? AND pos = ?");
            pstmt.setInt(1, notify_id);
            pstmt.setInt(2, pos);
            pstmt.executeUpdate();
            pstmt.close();

        // update if any other these conditions are true
        } else if (!player_name.equals("") && ( !player_name.equals(old_player) || !cw.equals(oldcw) || p9hole != oldp9hole )) {

            out.println("<!-- UPDATING player"+pos+" [" + username + " | " + player_name + " | old_player=" + old_player + " | " + cw + " | " + p9hole + "] -->");

            PreparedStatement pstmt = con.prepareStatement (
                "INSERT INTO notifications_players " +
                    "(notification_id, username, cw, player_name, 9hole, pos) VALUES (?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE " +
                    "notification_id = VALUES(notification_id), " +
                    "username = VALUES(username), " +
                    "cw = VALUES(cw), " +
                    "player_name = VALUES(player_name), " +
                    "9hole = VALUES(9hole)");

            pstmt.clearParameters();
            pstmt.setInt(1, notify_id);
            pstmt.setString(2, username);
            pstmt.setString(3, cw);
            pstmt.setString(4, player_name);
            pstmt.setInt(5, p9hole);
            pstmt.setInt(6, pos);
            pstmt.executeUpdate();
            pstmt.close();

        } else {

            out.println("<!-- UNCHANGED player"+pos+" [" + username +  " | " + player_name + "] -->");
        }
        
    } catch (Exception e) {
        
        dbError(out, e, "Error updating player" + pos + " info for notification " + notify_id + ".");
    }
 }
 
}
