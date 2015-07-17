/***************************************************************************************     
 *   Proshop_slot:  This servlet will process the 'Reserve Tee Time' request from
 *                    the Proshop's Sheet page.
 *
 *
 *   called by:  Proshop_sheet (doPost)
 *               self on cancel request
 *               Proshop_searchmem (doPost)
 *
 *
 *   created: 1/04/2002   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        6/08/06   Added confirmation to 'Cancel Tee Time' button
 *        5/17/06   Rancho Bernardo - force the mode of trans to 'CCH' for all guest types.
 *        4/25/06   CC at Castle Pines - Juniors must be with an adult at all times.
 *        4/24/06   CC at Castle Pines - force the mode of trans to 'CRT' for all guest types.
 *        4/22/06   If ProshopKeeper, make sure the current show values are passed and set correctly.
 *        4/19/06   If the tee time is during a shotgun event, post a warning (in doGet) and send
 *                  the email notification as though it was an event signup.
 *        4/17/06   Hazeltine - add check for consecutive singles or 2-somes.
 *        4/17/06   Medinah - remove ARR processing per Medinah's instructions.
 *        4/11/06   Catamount Ranch - check for max number of advance tee times.
 *        3/23/06   Added 'Check In' option while editing tee times feature
 *        3/15/06   CC of the Rockies - check for max number of advance tee times.
 *        3/15/06   Bearpath - check for guests on weekdays during member-only times.
 *        3/14/06   Oakland Hills - if tee time is 8 days in advance, then no guests and no X's.
 *        3/14/06   Oakland Hills - add a custom restriction for tee time requests more than 8 days in advance.
 *        3/14/06   Oakland Hills - add a custom restriction for member type of Dependent.
 *        3/07/06   Add ability to list names by mship and/or mtype groups.
 *        2/28/06   Merion - color 'House' members' names red in the name lists.
 *        2/27/06   Whenever tee time is added or changed, call SystemUtils.updateHist to track it.
 *        2/27/06   Merion - add custom member restriction (checkMerionSched).
 *        2/27/06   Merion - add custom guest restriction (checkMerionGres).
 *        2/23/06   Merion - add custom guest restriction (checkMerionG).
 *        2/15/06   Remove the 'please be patient' message in the doGet method to prevent system hang.
 *        1/18/06   Lakewood - allow multiple players with the same name, and change the 'Guest Types' box title.
 *       12/21/05   Skaneateles - add a custom restriction for member type of Dependent.
 *       12/09/05   Add option to suppress emails for the tee time.
 *       12/05/05   Add call to checkRitz method for custom member restrictions for RitzCarlton.
 *       11/08/05   Always check for 'Exception' on the catch from calls to verifySlot.
 *       11/06/05   Cherry Hills - add a custom restriction for member types.
 *       11/03/05   The Lakes - force the mode of trans to 'NON' for all guest types.
 *       11/03/05   Cherry Hills - let the mode of trans default to null when player field is empty.
 *       10/17/05   The Stanwich Club - Juniors must be with an adult at specified times.
 *        9/15/05   Medinah - do not show guest types that are for one of the other courses.
 *        7/21/05   Medinah CC - Color code and indent name list according to mtype.
 *        7/14/05   Oakmont - change guest restrictions for Wed & Fri per Padge's instructions.
 *        6/02/05   Green Bay CC - do not allow more than 9 guests per hour.
 *        5/17/05   Medinah CC - add custom restrictions..
 *        5/05/05   Reject request if a member has an empty mship, mtype, mnum or username (verifySlot.getUsers).
 *        4/13/05   Custom for Portage - check Associate mships for max rounds per month and year.
 *        3/25/05   Custom for Oakmont - send email to Caddie Master for any guest times.
 *        3/08/05   Add check in doGet processing for valid parameter values - reject if not.
 *        3/02/05   Ver 5 - if player added on day of tee time, set 'show' value to indicte (Paul S).
 *        2/16/05   Include the name of the guest restriction in error message.
 *        1/05/05   Ver 5 - allow member to make up to 5 consecutive tee times at once.
 *       11/16/04   Ver 5 - Allow member to schedule more than one tee time per day.
 *                          Improve layout to provide quicker access to member names.
 *       11/12/04   Ver 5 - Add autocomplete=yes to tee time form (for MS IE 5+ only!).
 *        9/22/04   Add special processing for Oakmont CC.
 *        9/16/04   Ver 5 - change getClub from SystemUtils to common.
 *        9/08/04   Add custom code for The Lakes - only allow head pro (proshop5) to override
 *                  guest restrictions.
 *        6/16/04   Allow for course=-ALL- option - return to course=all if selected.
 *        6/10/04   Add 'List All' option to member list to list all members for name selection.
 *        5/25/04   Add doGet method to display a 'Be Patient' page.
 *        2/23/04   Add Unaccompanied Guests support - prompt for associated members.
 *        2/17/04   Move email notifications to common.
 *        2/09/04   Add separate 9-hole option.
 *        2/06/04   Add support for configurable transportation modes.
 *        1/22/04   Add checks for 'days in advance' violations based on mship type.
 *       12/18/03   Enhancements for Version 4 of the software.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        1/01/03   Add changes for V2.
 *                  Add processing for X now optional, also number of hours can be specified.
 *                  Add multiple 'Guest' names.
 *                  Add support for 5-somes.
 *                  Add Notes and Hide Notes option.
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
import com.foretees.common.parmSlot;
import com.foretees.common.verifySlot;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;
import com.foretees.common.medinahCustom;


public class Proshop_slot extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //*****************************************************
 // Process the request from Proshop_sheet
 //*****************************************************
 //
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


   String temps = "";
   int contimes = 0;

   //
   //  Check if pro selected a tee time during a shotgun event - if so, warn him
   //
   if (req.getParameter("shotgunevent") != null) {        // if time during a shotgun event

      //
      //  Get the parms passed from Member_sheet
      //
      String course = req.getParameter("course");               // get the course name passed
      String returnCourse = req.getParameter("returnCourse");  
      String index = req.getParameter("index");                 // get the day index value
      String date = req.getParameter("date");
      String day = req.getParameter("day");
      String p5 = req.getParameter("p5");
      String p5rest = req.getParameter("p5rest");
      String jump = req.getParameter("jump");

      String stime = "";
      String sfb = "";
      String backCourse = "";

      //
      //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
      //    The value contains the time value.
      //
      Enumeration enum1 = req.getParameterNames();        // get the parm name passed

      while (enum1.hasMoreElements()) {

         String pname = (String) enum1.nextElement();

         if (pname.startsWith( "time" )) {

            stime = req.getParameter(pname);              //  get value: time of tee time requested (hhmm AM/PM:)

            StringTokenizer tok = new StringTokenizer( pname, ":" );     // space is the default token, use ':'

            sfb = tok.nextToken();                        // skip past 'time:'
            sfb = tok.nextToken();                        // get the front/back indicator from name of submit button
         }
      }
        
      if (returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
         
         backCourse = course;
           
      } else {
        
         backCourse = returnCourse;
      }

      //
      //  Display a menu for user to either list the existing lottery requests or start a new one
      //
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<Title>Proshop Shotgun Confirmation Page</Title>");
      out.println("</HEAD>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

         out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR>");
         out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
         out.println("<tr><td valign=\"top\" align=\"center\">");

         out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
            out.println("<tr>");
            out.println("<td width=\"580\" align=\"center\">");
            out.println("<font size=\"3\">");
            out.println("<b>Caution: Tee Time During Shotgun Event</b><br></font>");
            out.println("<font size=\"2\">");
            out.println("<br>You have selected a tee time that is during a Shotgun Event.");
            out.println("<br>If you continue an email notification could be sent to the member(s) in the tee time.");
            out.println("<br>The email will specify the <b>Shotgun Starting time</b> instead of this tee time.");
            out.println("<br><br>You can prevent the email notification by selecting the<br>Suppress Email Notifications option on the next page.");
            out.println("<br><br><hr align=\"center\" width=\"75%\">");
            out.println("<br>If your intent is to register a group for the event,<br>you may want to return and use the <b>Event Sign Up</b> tab.");
            out.println("<br><br><hr align=\"center\" width=\"75%\">");
            out.println("<br>If you wish to continue, select the 'Continue' button below.");
            out.println("<br><br>If you wish to return to the Tee Sheet, select the 'Go Back' button below.<br><br>");
            out.println("</font></td></tr>");
            out.println("</table><br>");

            out.println("<table border=\"0\" cols=\"1\" cellpadding=\"3\">");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + sfb + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + stime + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\"></form>");
            out.println("</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + backCourse + "\">");
            out.println("<input type=\"submit\" value=\"Go Back\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form>");
            out.println("</font></td></tr>");
            out.println("</table>");

            out.println("</td>");
            out.println("</tr>");
         out.println("</table>");
         out.println("</font></center></body></html>");
         out.close();
         return;                 // exit and wait for reply
   }

   //
   // See if more than one tee time was requested
   //
   if (req.getParameter("contimes") != null) {        // if 'consecutive tee times' count provided

      temps = req.getParameter("contimes");
      contimes = Integer.parseInt(temps);
   }

   if (contimes > 1) {                              // if more than one tee time requested

      Proshop_slotm slotm = new Proshop_slotm();    // create an instance of Proshop_slotm so we can call it (static vs non-static)

      slotm.doPost(req, resp);                      // call 'doPost' method in _slotm

   } else {                                         

      doPost(req, resp);                           // go to Proshop_slot (below)
   }

 }     // done


/*        // old code

   //
   //  If call from self, then go to doPost
   //
   if (req.getParameter("go") != null) {         // if call from the following process

      doPost(req, resp);      // call doPost processing
   }

   //
   //  Otherwise this is a call from Proshop_sheet
   //
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   String pname = "";
   String jump = "0";                               // jump index - default to zero (for _sheet)
   String stime = "";
   String sfb = "";
   String returnCourse = "";
   String temps = "";

   int contimes = 0;


   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");         //  date of tee time requested (yyyymmdd)
   String day = req.getParameter("day");            //  name of the day
   String index = req.getParameter("index");        //  index value of day (needed by Proshop_sheet when returning)
   String p5 = req.getParameter("p5");              //  5-somes supported
   String p5rest = req.getParameter("p5rest");      //  5-somes restricted
   String course = req.getParameter("course");      //  Name of Course

   if (req.getParameter("returnCourse") != null) {   // if multi course club, get course to return to

      returnCourse = req.getParameter("returnCourse");
   }
   if (req.getParameter("jump") != null) {            // if jump index provided

      jump = req.getParameter("jump");
   }

   if (req.getParameter("contimes") != null) {        // if 'consecutive tee times' count provided

      temps = req.getParameter("contimes");
      contimes = Integer.parseInt(temps);
   }

   //
   //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
   //
   Enumeration enum1 = req.getParameterNames();     // get the parm name passed

   while (enum1.hasMoreElements()) {

      pname = (String) enum1.nextElement();             // get parm name

      if (pname.startsWith( "time" )) {

         stime = req.getParameter(pname);              //  value = time of tee time requested (hh:mm AM/PM)

         StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

         sfb = tok.nextToken();                        // skip past 'time '
         sfb = tok.nextToken();                        // get the front/back indicator value
      }
   }

   //
   //  Verify Parms
   //
   if (sdate == null || sdate.equals( "" ) || stime.equals( "" )) {

      out.println(SystemUtils.HeadTitle("DB Communication Error"));
      out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Communication Error</H3>");
      out.println("<BR><BR>Sorry, there was a procedural error.");
      out.println("<BR>Please try again. You may have to logout and log back in to correct the problem.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Output 'Be Patient' message in case we are busy
   //
   out.println(SystemUtils.HeadTitle("Tee Time Request In Process"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center><font size=\"3\" color=\"red\">");
   out.println("<BR><BR><H2>Processing Your Request - Please Be Patient!</H2>");
   out.println("<BR><BR>The system is preparing the tee time for processing.");
   out.println("<BR><BR>If this page is displayed long enough to read this, then the system is busy.");
   out.println("<BR><BR><b>DO NOT use your browser's Back Button or leave this page</b>");
   out.println("<BR>unless the tee time does not appear within 3 or 4 minutes.");
   out.println("<BR><BR>Exiting this page will only add to the system load and will hang the tee time.");
   out.println("</font></center></body></html>");

   //
   //  Create a script to jump to doPost processing
   //
   if (contimes > 1) {                                   // if more than one tee time requested

      out.println("<script type=\"text/javascript\">");
      out.println("document.location.href='/" +rev+ "/servlet/Proshop_slotm?go=yes&sdate=" +sdate+ "&stime=" +stime+ "&fb=" +sfb+ "&day=" +day+ "&index=" +index+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&jump=" +jump+ "&p5rest=" +p5rest+ "&p5=" +p5+ "&contimes=" +contimes+ "'");
      out.println("</script>");

   } else {       // go to Proshop_slot

      out.println("<script type=\"text/javascript\">");
      out.println("document.location.href='/" +rev+ "/servlet/Proshop_slot?go=yes&sdate=" +sdate+ "&stime=" +stime+ "&fb=" +sfb+ "&day=" +day+ "&index=" +index+ "&course=" +course+ "&returnCourse=" +returnCourse+ "&jump=" +jump+ "&p5rest=" +p5rest+ "&p5=" +p5+ "'");
      out.println("</script>");
   }
   out.close();
 }
*/


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

   if (session == null) {
     
      return;
   }
       
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
   String user = (String)session.getAttribute("user");  
   String club = (String)session.getAttribute("club");
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
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   slotParms.club = club;                        // save club name
           
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
   String orig_name = "";
   String last_user = "";
   String last_name = "";
   String conf = "";
   String suppressEmails = "";
   String returnCourse = "";
   String pname = "";

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

   if (req.getParameter("fb") != null) {            // if date was passed in sdate

      sfb = req.getParameter("fb");
   }

   if (req.getParameter("sdate") != null) {         // if date was passed in sdate

      sdate = req.getParameter("sdate");
   }

   if (req.getParameter("date") != null) {          // if date was passed in date

      sdate = req.getParameter("date");
   }

   if (req.getParameter("stime") != null) {         // if time was passed in stime

      stime = req.getParameter("stime");

   } else {                                           // call from Member_sheet

      //
      //    The name of the submit button (time) has the front/back indicator appended to it ('time:fb')
      //
      Enumeration enum1 = req.getParameterNames();     // get the parm name passed

      while (enum1.hasMoreElements()) {

         pname = (String) enum1.nextElement();             // get parm name

         if (pname.startsWith( "time" )) {

            stime = req.getParameter(pname);              //  value = time of tee time requested (hh:mm AM/PM)

            StringTokenizer tok = new StringTokenizer( pname, ":" );     // separate name around the colon

            sfb = tok.nextToken();                        // skip past 'time '
            sfb = tok.nextToken();                        // get the front/back indicator value
         }
      }
   }

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
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
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      //
      //  create a time string for display
      //
      hr = time / 100;
      min = time - (hr * 100);       

      ampm = " AM";
        
      if (hr > 11) {
        
         ampm = " PM";
           
         if (hr > 12) {
           
            hr = hr - 12;
         }
      }
      if (min < 10) {
         stime = hr + ":0" + min + ampm;
      } else {
         stime = hr + ":" + min + ampm;
      }

   } else {

      //
      //  Parse the time parm to separate hh, mm, am/pm and convert to military time
      //  (received as 'hh:mm xx'   where xx = am or pm)
      //
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
         // ignore error
      }

      if (ampm.equalsIgnoreCase ( "PM" )) {

         if (hr != 12) {

            hr = hr + 12;
         }
      }
     
      time = hr * 100;
      time = time + min;          // military time
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
      //  Get the players' names and check if this tee slot is already in use
      //***********************************************************************
      //
      slotParms.day = day_name;            // save day name

      //
      //  Verify the required parms exist
      //
      if (date == 0 || time == 0 || course == null || user.equals( "" ) || user == null) {

         //
         //  save message in /" +rev+ "/error.txt
         //
         String msg = "Error in Proshop_slot - checkInUse Parms - for user " +user+ " at " +club+ ".  Date= " +date+ ", time= " +time+ ", course= " +course+ ", fb= " +fb;   // build msg
         SystemUtils.logError(msg);                                   // log it

         in_use = 1;          // make like the time is busy

      } else {               // continue if parms ok

         try {

            in_use = verifySlot.checkInUse(date, time, fb, course, user, slotParms, con);

         }
         catch (Exception e1) {

            String msg = "Proshop_slot Check in use flag failed - Exception: " + e1.getMessage();

            SystemUtils.logError(msg);                                   // log it

            in_use = 1;          // make like the time is busy
         }
      }

      if (in_use != 0) {              // if time slot already in use

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
         out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
         out.println("<BR>Please select another time or try again later.");
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

      player1 = slotParms.player1;
      player2 = slotParms.player2;
      player3 = slotParms.player3;
      player4 = slotParms.player4;
      player5 = slotParms.player5;
      p1cw = slotParms.p1cw;
      p2cw = slotParms.p2cw;
      p3cw = slotParms.p3cw;
      p4cw = slotParms.p4cw;
      p5cw = slotParms.p5cw;
      p91 = slotParms.p91;
      p92 = slotParms.p92;
      p93 = slotParms.p93;
      p94 = slotParms.p94;
      p95 = slotParms.p95;
      show1 = slotParms.show1;
      show2 = slotParms.show2;
      show3 = slotParms.show3;
      show4 = slotParms.show4;
      show5 = slotParms.show5;
      last_user = slotParms.last_user;
      notes = slotParms.notes;
      hide = slotParms.hide;
      orig_by = slotParms.orig_by;
      conf = slotParms.conf;
   }              // end of 'letter' or 'return' if

   //
   //  Ensure that there are no null player fields
   //
   if (player1 == null ) {
      player1 = "";
   }
   if (player2 == null ) {
      player2 = "";
   }
   if (player3 == null ) {
      player3 = "";
   }
   if (player4 == null ) {
      player4 = "";
   }
   if (player5 == null ) {
      player5 = "";
   }
   if (p1cw == null ) {
      p1cw = "";
   }
   if (p2cw == null ) {
      p2cw = "";
   }
   if (p3cw == null ) {
      p3cw = "";
   }
   if (p4cw == null ) {
      p4cw = "";
   }
   if (p5cw == null ) {
      p5cw = "";
   }
   if (last_user == null ) {
      last_user = "";
   }
   if (notes == null ) {
      notes = "";
   }
   if (orig_by == null ) {
      orig_by = "";
   }
   if (conf == null ) {
      conf = "";
   }
   
   //
   //  Get the walk/cart options available and find the originators name 
   //
   PreparedStatement pstmtc = null;
      
   try {

      getParms.getTmodes(con, parmc, course);

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;        // if proshop, just use the username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";        // init
              
            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

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
                  "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username= ?");

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

      if (!last_user.equals( "" )) {         // if last_user exists (username of last person to change tee time)

         if (last_user.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            last_name = last_user;        // if proshop, just use the username

         } else {

            //
            //  Check member table and hotel table for match
            //
            last_name = "";        // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

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
                  "SELECT name_last, name_first, name_mi FROM hotel3 WHERE username= ?");

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
   //  Build the HTML page to prompt user for names
   //
   out.println("<HTML>");
   out.println("<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
   out.println("<Title>Proshop Tee Slot Page</Title>");

      //
      //*******************************************************************
      //  User clicked on a letter - submit the form for the letter
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Submit the form when clicking on a letter
      out.println("<!--");
      out.println("function subletter(x) {");

//      out.println("alert(x);");
      out.println("document.playerform.letter.value = x;");         // put the letter in the parm
      out.println("playerform.submit();");        // submit the form
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

   if (assign == 0) {   // if normal tee time

      //
      //*******************************************************************
      //  Erase player name (erase button selected next to player's name)
      //
      //    Remove the player's name and shift any other names up starting at player1
      //*******************************************************************
      //
      out.println("<script language='JavaScript'>");            // Erase name script
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
      out.println("<script language='JavaScript'>");            // Erase text area script
      out.println("<!--");
      out.println("function erasetext(pos1) {");
      out.println("document.playerform[pos1].value = '';");           // clear the text field
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

      out.println("<script language='JavaScript'>");             // Move Notes into textarea
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
      out.println("<script language='JavaScript'>");            // Move name script
      out.println("<!--");

      out.println("function movename(namewc) {");

      out.println("del = ':';");                               // deliminator is a colon
      out.println("array = namewc.split(del);");                 // split string into 2 pieces (name, wc)
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


        if (!club.equals( "lakewood" )) {
           if (p5.equals( "Yes" )) {
             out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4) || ( name == player5)) {");
           } else {
             out.println("if (( name == player1) || ( name == player2) || ( name == player3) || ( name == player4)) {");
           }
             out.println("skip = 1;");
           out.println("}");
        }
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
      out.println("<script language='JavaScript'>");            // Move Guest Name script
      out.println("<!--");

      out.println("function moveguest(namewc) {");

      out.println("var name = namewc;");

      out.println("var defCW = '';");
      if (club.equals( "lakes" )) {
         out.println("defCW = 'NON';");       // set default Mode of Trans
      }
      if (club.equals( "castlepines" )) {
         out.println("defCW = 'CRT';");       // set default Mode of Trans
      }
      if (club.equals( "ranchobernardo" )) {
         out.println("defCW = 'CCH';");       // set default Mode of Trans
      }

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
      out.println("<script language='JavaScript'>");            // Erase name script
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
      out.println("<script language='JavaScript'>");            // Move name script
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
     out.println("<font size=\"5\">Golf Shop Reservation</font>");
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
            out.println("<b>Warning</b>:&nbsp;&nbsp;You have <b>6 minutes</b> to complete this reservation.");
            out.println("&nbsp; If you want to return without completing a reservation, <b>do not ");
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
      if (assign == 0) {      // if normal tee time
         out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_instruct.htm', 'newwindow', config='Height=540, width=520, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      } else {
         out.println("<a href=\"#\" onClick=\"window.open ('/" +rev+ "/proshop_help_slot_unacomp.htm', 'newwindow', config='Height=380, width=500, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">");
      }
      out.println("<img src=\"/" +rev+ "/images/instructions.gif\" border=0>");
      out.println("<br>Click for Help</a>");

      out.println("</font><font size=\"2\">");
      out.println("<br><br><br>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" name=\"can\" autocomplete=\"yes\">");
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=" + day_name + ">");
      out.println("<input type=\"hidden\" name=\"date\" value=" + date + ">");
      out.println("<input type=\"hidden\" name=\"time\" value=" + time + ">");
      out.println("<input type=\"hidden\" name=\"fb\" value=" + fb + ">");
      out.println("Return<br>w/o Changes:<br>");
      out.println("<input type=\"submit\" value=\"Go Back\" name=\"cancel\"></form>");
      out.println("</font></td>");

      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" name=\"playerform\">");
      out.println("<td align=\"center\" valign=\"top\">");

         out.println("<font size=\"2\">");
         out.println("Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + mm + "/" + dd + "/" + yy + "</b>");
           out.println("&nbsp;&nbsp;Tee Time:&nbsp;&nbsp;<b>" + stime + "</b>");
           if (!course.equals( "" )) {
              out.println("<br>Course:&nbsp;&nbsp;<b>" + course + "</b>");
           }
           out.println("<br>");
           if (!orig_name.equals( "" ) || !last_name.equals( "" )) {         // if tee time already exists
              out.println("<br>");
              if (!orig_name.equals( "" )) {
                 out.println("Tee Time originated by: <b>" + orig_name + "</b><br>");
              }
              if (!last_name.equals( "" )) {
                 out.println("Tee Time last modified by: <b>" + last_name + "</b><br>");
              }
           }
           out.println("<br></font>");

         GregorianCalendar cal_pci = new GregorianCalendar();
         boolean show_checkin = (
            (!parmp.posType.equals( "Pro-ShopKeeper" )) &&
            mm == (cal_pci.get(cal_pci.MONTH) + 1) &&
            dd == cal_pci.get(cal_pci.DAY_OF_MONTH) &&
            yy == cal_pci.get(cal_pci.YEAR));         
         
         if (assign == 0) {      // if normal tee time

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
            //out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9-Holes&nbsp;");
            out.println("&nbsp;&nbsp;&nbsp;Trans&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/9hole.gif\" height=17 width=22>&nbsp;");
            
            
            if (show_checkin) {
                out.println("&nbsp;&nbsp;&nbsp;<img src=\"/" +rev+ "/images/checkin.gif\" width=30 height=17>&nbsp;");
                //out.println("&nbsp;&nbsp;&nbsp;&radic; In&nbsp;");
            }

            out.println("</nobr></b>");
            
            out.println("<br><img src=\"/" +rev+ "/images/erase.gif\" onClick=\"erasename('player1')\" style=\"cursor:hand\">");
            out.println("1:&nbsp;&nbsp;<input type=\"text\" name=\"player1\" value=\"" + player1 + "\" size=\"20\" maxlength=\"30\">");
              out.println("&nbsp;&nbsp;&nbsp;<select size=\"1\" name=\"p1cw\">");
              if (!p1cw.equals( "" ) || club.equals( "cherryhills" )) {
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
              if (!p2cw.equals( "" ) || club.equals( "cherryhills" )) {
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
              if (!p3cw.equals( "" ) || club.equals( "cherryhills" )) {
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
              if (!p4cw.equals( "" ) || club.equals( "cherryhills" )) {
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
              if (!p5cw.equals( "" ) || club.equals( "cherryhills" )) {
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
         out.println("<input type=\"hidden\" name=\"mm\" value=" + mm + ">");
         out.println("<input type=\"hidden\" name=\"yy\" value=" + yy + ">");
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
                  out.println(parmc.tmodea[i]+ " = " +parmc.tmode[i]+ "&nbsp;&nbsp;");
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
            out.println("<br><input type=submit value=\"Cancel Tee Time\" name=\"remove\" onclick=\"return confirm('Are you sure you want to remove ALL players from this tee time?')\">");
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
   //  parm block to hold the tee time parms
   //
   parmSlot slotParms = new parmSlot();          // allocate a parm block

   slotParms.hndcp1 = 99;     // init handicaps
   slotParms.hndcp2 = 99;
   slotParms.hndcp3 = 99;
   slotParms.hndcp4 = 99;
   slotParms.hndcp5 = 99;

   //
   // Get all the parameters entered
   //
   String sdate = req.getParameter("date");           //  date of tee time requested (yyyymmdd)
   String stime = req.getParameter("time");           //  time of tee time requested (hhmm)
   String smm = req.getParameter("mm");               //  month of tee time
   String syy = req.getParameter("yy");               //  year of tee time
   String index = req.getParameter("index");          //  day index value (needed by _sheet on return)
   String returnCourse = req.getParameter("returnCourse");        //  name of course to return to (multi)
     
   slotParms.p5 = req.getParameter("p5");                //  5-somes supported for this slot
   slotParms.p5rest = req.getParameter("p5rest");        //  5-somes restricted for this slot
   slotParms.course = req.getParameter("course");        //  name of course
   slotParms.player1 = req.getParameter("player1");
   slotParms.player2 = req.getParameter("player2");
   slotParms.player3 = req.getParameter("player3");
   slotParms.player4 = req.getParameter("player4");
   slotParms.player5 = req.getParameter("player5");
   slotParms.p1cw = req.getParameter("p1cw");
   slotParms.p2cw = req.getParameter("p2cw");
   slotParms.p3cw = req.getParameter("p3cw");
   slotParms.p4cw = req.getParameter("p4cw");
   slotParms.p5cw = req.getParameter("p5cw");
   slotParms.show1 = (req.getParameter("show1") == null) ? (short)0 : Short.parseShort(req.getParameter("show1"));
   slotParms.show2 = (req.getParameter("show2") == null) ? (short)0 : Short.parseShort(req.getParameter("show2"));
   slotParms.show3 = (req.getParameter("show3") == null) ? (short)0 : Short.parseShort(req.getParameter("show3"));
   slotParms.show4 = (req.getParameter("show4") == null) ? (short)0 : Short.parseShort(req.getParameter("show4"));
   slotParms.show5 = (req.getParameter("show5") == null) ? (short)0 : Short.parseShort(req.getParameter("show5"));
   slotParms.day = req.getParameter("day");                      // name of day
   slotParms.sfb = req.getParameter("fb");                       // Front/Back indicator
   slotParms.notes = req.getParameter("notes");                  // Proshop Notes
   slotParms.jump = req.getParameter("jump");                    // jump index for _sheet
   slotParms.conf = req.getParameter("conf");                    // confirmation # (or Id) for Hotels

   if (req.getParameter("hide") != null) {                       // if hide notes parm exists
      slotParms.hides = req.getParameter("hide");
   } else {
      slotParms.hides = "No";
   }
   if (req.getParameter("suppressEmails") != null) {             // if email parm exists
      suppressEmails = req.getParameter("suppressEmails");
   }

   //
   //  set 9-hole options
   //
   slotParms.p91 = 0;                       // init to 18 holes
   slotParms.p92 = 0;                       
   slotParms.p93 = 0;
   slotParms.p94 = 0;
   slotParms.p95 = 0;
     
   if (req.getParameter("p91") != null) {             // get 9-hole indicators if they were checked
      p9s = req.getParameter("p91");
      slotParms.p91 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p92") != null) {
      p9s = req.getParameter("p92");
      slotParms.p92 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p93") != null) {
      p9s = req.getParameter("p93");
      slotParms.p93 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p94") != null) {
      p9s = req.getParameter("p94");
      slotParms.p94 = Integer.parseInt(p9s);
   }
   if (req.getParameter("p95") != null) {
      p9s = req.getParameter("p95");
      slotParms.p95 = Integer.parseInt(p9s);
   }

   //
   //  Get member names for Unaccompanied Guests, if provided
   //
   if (req.getParameter("mem1") != null) {
      slotParms.mem1 = req.getParameter("mem1");
   }
   if (req.getParameter("mem2") != null) {
      slotParms.mem2 = req.getParameter("mem2");
   }
   if (req.getParameter("mem3") != null) {
      slotParms.mem3 = req.getParameter("mem3");
   }
   if (req.getParameter("mem4") != null) {
      slotParms.mem4 = req.getParameter("mem4");
   }
   if (req.getParameter("mem5") != null) {
      slotParms.mem5 = req.getParameter("mem5");
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
   if (slotParms.player1 == null ) {
      slotParms.player1 = "";
   }
   if (slotParms.player2 == null ) {
      slotParms.player2 = "";
   }
   if (slotParms.player3 == null ) {
      slotParms.player3 = "";
   }
   if (slotParms.player4 == null ) {
      slotParms.player4 = "";
   }
   if (slotParms.player5 == null ) {
      slotParms.player5 = "";
   }
   if (slotParms.p1cw == null ) {
      slotParms.p1cw = "";
   }
   if (slotParms.p2cw == null ) {
      slotParms.p2cw = "";
   }
   if (slotParms.p3cw == null ) {
      slotParms.p3cw = "";
   }
   if (slotParms.p4cw == null ) {
      slotParms.p4cw = "";
   }
   if (slotParms.p5cw == null ) {
      slotParms.p5cw = "";
   }

   //
   //  Convert date & time from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      mm = Integer.parseInt(smm);
      yy = Integer.parseInt(syy);
      fb = Integer.parseInt(slotParms.sfb);
      ind = Integer.parseInt(index);       // get numeric value of index
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  See if user wants to hide any notes from the Members
   //
   hide = 0;      // init
     
   if (slotParms.hides.equals( "Yes" )) {
    
      hide = 1;
   }

   //
   //  Get the length of Notes (max length of 254 chars)
   //
   int notesL = 0;     
    
   if (!slotParms.notes.equals( "" )) {
     
      notesL = slotParms.notes.length();       // get length of notes
   }

   //
   //   use yy and mm and date to determine dd (from tee time's date)
   //
   temp = yy * 10000;
   temp = temp + (mm * 100);
   dd = (int) date - temp;            // get day of month from date

   //
   //  put parms in Parameter Object for portability
   //
   slotParms.date = date;
   slotParms.time = time;
   slotParms.mm = mm;
   slotParms.yy = yy;
   slotParms.dd = dd;
   slotParms.fb = fb;
   slotParms.ind = ind;                      // index value
   slotParms.club = club;                    // name of club
   slotParms.returnCourse = returnCourse;    // name of course for return to _sheet
   slotParms.suppressEmails = suppressEmails;

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
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);         // put the parm in pstmt
      pstmt.setInt(2, time);
      pstmt.setInt(3, fb);
      pstmt.setString(4, slotParms.course);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {
                                                      
         event = rs.getString("event");
         slotParms.oldPlayer1 = rs.getString("player1");
         slotParms.oldPlayer2 = rs.getString("player2");
         slotParms.oldPlayer3 = rs.getString("player3");
         slotParms.oldPlayer4 = rs.getString("player4");
         slotParms.oldUser1 = rs.getString("username1");
         slotParms.oldUser2 = rs.getString("username2");
         slotParms.oldUser3 = rs.getString("username3");
         slotParms.oldUser4 = rs.getString("username4");
         slotParms.oldp1cw = rs.getString("p1cw");
         slotParms.oldp2cw = rs.getString("p2cw");
         slotParms.oldp3cw = rs.getString("p3cw");
         slotParms.oldp4cw = rs.getString("p4cw");
         slotParms.in_use = rs.getInt("in_use");
         slotParms.in_use_by = rs.getString("in_use_by");
         event_type = rs.getInt("event_type");
         //slotParms.show1 = rs.getShort("");
         //slotParms.show2 = rs.getShort("");
         //slotParms.show3 = rs.getShort("");
         //slotParms.show4 = rs.getShort("");
         slotParms.oldPlayer5 = rs.getString("player5");
         slotParms.oldUser5 = rs.getString("username5");
         slotParms.oldp5cw = rs.getString("p5cw");
         //slotParms.show5 = rs.getShort("");
         proNew = rs.getInt("proNew");
         proMod = rs.getInt("proMod");
         slotParms.userg1 = rs.getString("userg1");
         slotParms.userg2 = rs.getString("userg2");
         slotParms.userg3 = rs.getString("userg3");
         slotParms.userg4 = rs.getString("userg4");
         slotParms.userg5 = rs.getString("userg5");
         slotParms.orig_by = rs.getString("orig_by");
         slotParms.pos1 = rs.getShort("pos1");
         slotParms.pos2 = rs.getShort("pos2");
         slotParms.pos3 = rs.getShort("pos3");
         slotParms.pos4 = rs.getShort("pos4");
         slotParms.pos5 = rs.getShort("pos5");
      }
      pstmt.close();

      if (slotParms.orig_by.equals( "" )) {    // if originator field still empty (allow this person to grab this tee time again)

         slotParms.orig_by = user;             // set this user as the originator
      }

      if ((slotParms.in_use == 0) || (!slotParms.in_use_by.equalsIgnoreCase( user ))) {    // if time slot in use and not by this user

         out.println(SystemUtils.HeadTitle("DB Record In Use Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<CENTER><BR><BR><H1>Reservation Timer Expired</H1>");
         out.println("<BR><BR>Sorry, but this tee time slot has been returned to the system.<BR>");
         out.println("<BR>The system timed out and released the tee time.");
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
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
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

      if (slotParms.pos1 != 0 || slotParms.pos2 != 0 || slotParms.pos3 != 0 || slotParms.pos4 != 0 || slotParms.pos5 != 0) {

         posSent = true;        // indicate POS already sent for this group (warning)
      }
      
      slotParms.player1 = "";
      slotParms.player2 = "";
      slotParms.player3 = "";
      slotParms.player4 = "";
      slotParms.player5 = "";
      slotParms.p1cw = "";
      slotParms.p2cw = "";
      slotParms.p3cw = "";
      slotParms.p4cw = "";
      slotParms.p5cw = "";
      slotParms.user1 = "";
      slotParms.user2 = "";
      slotParms.user3 = "";
      slotParms.user4 = "";
      slotParms.user5 = "";
      slotParms.userg1 = "";
      slotParms.userg2 = "";
      slotParms.userg3 = "";
      slotParms.userg4 = "";
      slotParms.userg5 = "";
      slotParms.show1 = 0;
      slotParms.show2 = 0;
      slotParms.show3 = 0;
      slotParms.show4 = 0;
      slotParms.show5 = 0;
      slotParms.notes = "";
      hide = 0;
      slotParms.mNum1 = "";
      slotParms.mNum2 = "";
      slotParms.mNum3 = "";
      slotParms.mNum4 = "";
      slotParms.mNum5 = "";
      slotParms.orig_by = "";
      slotParms.conf = "";
      slotParms.p91 = 0;
      slotParms.p92 = 0;
      slotParms.p93 = 0;
      slotParms.p94 = 0;
      slotParms.p95 = 0;
      slotParms.pos1 = 0;
      slotParms.pos2 = 0;
      slotParms.pos3 = 0;
      slotParms.pos4 = 0;
      slotParms.pos5 = 0;

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

         slotParms.rnds = parm.rnds;
         slotParms.hrsbtwn = parm.hrsbtwn;
      }
      catch (Exception ignore) {
      }

      //
      //  Make sure at least 1 player contains a name
      //
      if ((slotParms.player1.equals( "" )) && (slotParms.player2.equals( "" )) && (slotParms.player3.equals( "" )) && (slotParms.player4.equals( "" )) && (slotParms.player5.equals( "" ))) {

         out.println(SystemUtils.HeadTitle("Data Entry Error"));
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<center>");
         out.println("<BR><BR><H3>Data Entry Error</H3>");
         out.println("<BR><BR>Required field has not been completed or is invalid.");
         out.println("<BR><BR>At least 1 Player field must contain a valid entry.");
         out.println("<BR>If you wish to remove all names from this slot, use the 'Cancel Tee Time' button.");
         out.println("<BR><BR>");

            //
            //  Return to _slot to change the player order
            //
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");

         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;

      }

      //
      //  At least 1 Player field is present - Make sure a C/W was specified for all players
      //
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.p1cw.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.p2cw.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.p3cw.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.p4cw.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.p5cw.equals( "" )))) {

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
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Shift players up if any empty spots (start with Player1 position)
      //
      verifySlot.shiftUp(slotParms);
        
      //
      //  Check if any player names are guest names (set userg1-5 if necessary)
      //
      try {
        
         verifySlot.parseGuests(slotParms, con);

      }
      catch (Exception e1) {
      }


      //
      //  Make sure there are no duplicate names
      //
      player = "";
        
      if (!club.equals( "lakewood" )) {

         if ((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) {

            if ((slotParms.player1.equalsIgnoreCase( slotParms.player2 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player3 )) ||
                (slotParms.player1.equalsIgnoreCase( slotParms.player4 )) || (slotParms.player1.equalsIgnoreCase( slotParms.player5 ))) {

               player = slotParms.player1;
            }
         }

         if ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) {

            if ((slotParms.player2.equalsIgnoreCase( slotParms.player3 )) || (slotParms.player2.equalsIgnoreCase( slotParms.player4 )) ||
                (slotParms.player2.equalsIgnoreCase( slotParms.player5 ))) {

               player = slotParms.player2;
            }
         }

         if ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) {

            if ((slotParms.player3.equalsIgnoreCase( slotParms.player4 )) ||
                (slotParms.player3.equalsIgnoreCase( slotParms.player5 ))) {

               player = slotParms.player3;
            }
         }

         if ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) {

            if (slotParms.player4.equalsIgnoreCase( slotParms.player5 )) {

               player = slotParms.player4;
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
            out.println("<font size=\"2\">");
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
            out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
         }
      }
        
      //
      //  Parse the names to separate first, last & mi
      //
      try {

         error = verifySlot.parseNames(slotParms, "pro");

      }
      catch (Exception ignore) {
      }

      if ( error == true ) {          // if problem

         invData(out);                        // reject
         return;
      }

 
      //
      //  Get the usernames, membership types and hndcp's for players if matching name found
      //
      try {

         verifySlot.getUsers(slotParms, con);

      }
      catch (Exception ignore) {
      }


      //
      //  Save the members' usernames for guest association 
      //
      memA[0] = slotParms.user1;
      memA[1] = slotParms.user2;
      memA[2] = slotParms.user3;
      memA[3] = slotParms.user4;
      memA[4] = slotParms.user5;

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
         if (slotParms.inval5 != 0) {

            err_name = slotParms.player5;
            invalNum = slotParms.inval5;
         }

         if (slotParms.inval4 != 0) {

            err_name = slotParms.player4;
            invalNum = slotParms.inval4;
         }

         if (slotParms.inval3 != 0) {

            err_name = slotParms.player3;
            invalNum = slotParms.inval3;
         }

         if (slotParms.inval2 != 0) {

            err_name = slotParms.player2;
            invalNum = slotParms.inval2;
         }

         if (slotParms.inval1 != 0) {

            err_name = slotParms.player1;
            invalNum = slotParms.inval1;
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
               out.println("<BR><BR>You will have to remove this name from your tee time request.");
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
            out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
            out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
            out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
            out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
            out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
            out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
            out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
            out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
            out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
            out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
            out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
            out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
            out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
            out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
            out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
            out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
            out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
            out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
            out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
            out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
            out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
            out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
            out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
            out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
            out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
            out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
            out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
            out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
            out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
            out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
            out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
            out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
            out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
            out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
            out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
            out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
              
            if (invalNum == 2) {        // if incomplete member record

               out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

            } else {
              
               out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"1\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
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
         if ((!slotParms.mship1.equals( "" )) ||
             (!slotParms.mship2.equals( "" )) ||
             (!slotParms.mship3.equals( "" )) ||
             (!slotParms.mship4.equals( "" )) ||
             (!slotParms.mship5.equals( "" ))) {   // if at least one name exists then check number of rounds

            error = false;                             // init error indicator

            try { 
              
               error = verifySlot.checkMaxRounds(slotParms, con);

               if (error == false) {       // if ok, check for Hazeltine special processing

                  //
                  //  If Hazeltine National, then check for National Memberships - max rounds
                  //
                  if (club.equals( "hazeltine" )) {      // if Hazeltine National

                     error = verifySlot.checkNational(slotParms, con);  // check for max rounds for National mships
                  }
                  //
                  //  If Portage CC, then process any Associate Memberships (2 rounds per month, 6 per year)
                  //
                  if (club.equals( "portage" )) {      // if Portage

                     error = verifySlot.checkPortage(slotParms, con);  // check for Associate mships
                  }
               }
            }
            catch (Exception e2) {
               String errorMsgX = "Check for Max Rounds (Proshop_slot): exception=" +e2;
               SystemUtils.logError(errorMsgX);        // log the error message
            }

            if (error == true) {      // a member exceed the max allowed tee times per week, month or year

               out.println(SystemUtils.HeadTitle("Max Limit Exceeded - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H3>Member Exceeded Limit</H3><BR>");
               out.println("<BR><BR>Warning:  " + slotParms.player + " is a " + slotParms.mship + " member and has exceeded the<BR>");
               out.println("maximum number of tee times allowed for this " + slotParms.period + ".");
               out.println("<BR><BR>Would you like to override the limit and allow this reservation?");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               returnToSlot(out, slotParms, 2);
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
         //  Check for max # of guests exceeded (per Member or per Tee Time)
         //************************************************************************
         //
         if (slotParms.guests != 0) {      // if any guests were included

            error = false;                             // init error indicator

            try {

               error = verifySlot.checkMaxGuests(slotParms, con);

            }
            catch (Exception e5) {

               dbError(out, e5);
               return;
            }

            if (error == true) {      // a member exceed the max allowed guests

                  out.println(SystemUtils.HeadTitle("Max Num Guests Exceeded - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Number of Guests Exceeded Limit</H3>");
                  out.println("<BR>Sorry, the maximum number of guests allowed for the<BR>");
                  out.println("time you are requesting is " +slotParms.grest_num+ " per " +slotParms.grest_per+ ".");
                  out.println("<BR>You have requested " +slotParms.guests+ " guests and " +slotParms.members+ " members.");
                  out.println("<BR><BR>Restriction Name = " +slotParms.rest_name);
                  if (club.equals("lakes") && !user.equalsIgnoreCase("proshop5")) {  // if Lakes and NOT head pro
                     out.println("<BR><BR>");
                  } else {       // not Lakes or is head pro
                     out.println("<BR><BR>Would you like to override the limit and allow this reservation?");
                     out.println("<BR><BR>");
                  }
                  //
                  //  Return to _slot to change the player order
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                  if (club.equals("lakes") && !user.equalsIgnoreCase("proshop5")) {  // if Lakes and NOT head pro
                     out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  } else {       // not Lakes or is head pro
                     out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                     out.println("</form>");

                     out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"skip\" value=\"3\">");
                     out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                     out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                     out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                     out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                     out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                     out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                     out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                     out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                     out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                     out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                     out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                     out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                     out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                     out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                     out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                     out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                     out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                     out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                     out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                     out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                     out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                     out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                     out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                     out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                     out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                     out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                     out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                     out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                     out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                     out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");
                     out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\">");
                  }
                  out.println("</form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
            }
              
            if (club.equals( "oakmont" )) {      // if Oakmont CC

               if (slotParms.guests > 1) {       // if 2 or more guests and Oakmont CC

                  //
                  // **********************************************************************
                  //  Oakmont - Check for max # of family guest tee times exceeded
                  // **********************************************************************
                  //
                  error = false;                             // init error indicator

                  try {

                     error = verifySlot.oakmontGuests(slotParms, con);

                  }
                  catch (Exception e5) {

                     dbError(out, e5);
                     return;
                  }

                  if (error == true) {      // a member exceed the max allowed tee times per month

                     out.println(SystemUtils.HeadTitle("Max Num Guest Tee Times Exceeded - Reject"));
                     out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");

                     out.println("<BR><H3>Number of Family Guest Tee Times Exceeded Limit For The Day</H3><BR>");
                     out.println("<BR><BR>Sorry, there are already 2 tee times with family guests<BR>");
                     out.println("scheduled for today.  You are allowed one family guest per member.");
                     out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                     out.println("<BR><BR>");
                     //
                     //  Return to _slot to change the player order
                     //
                     returnToSlot(out, slotParms, 3);
                     return;
                  }
               }
            }
              
            //
            //  If Green Bay, then check if more than 9 guests per hour
            //
            if (club.equals( "greenbay" )) {           // if Green Bay CC

               error = verifySlot.checkGBguests(slotParms, con);

               if (error == true) {      // more than 9 guests this hour

                  out.println(SystemUtils.HeadTitle("Max Number of Guests Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");

                  out.println("<BR><BR><H3>Maximum Number of Guests Exceeded</H3>");
                  out.println("<BR>Sorry, but there are already guests scheduled during this hour.");
                  out.println("<BR>No more than 9 guests are allowed per hour.  This request would exceed that total.");
                  out.println("<BR><BR>Please remove one or more guests, or try another time of the day.");

                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, 3);
                  return;
               }
            }      // end of if Green Bay CC
 
            //
            //  If Merion, then check for max guest times per hour
            //
            if (club.equals( "merion" ) && slotParms.course.equals( "East" )) {           // if Merion

               error = verifySlot.checkMerionG(slotParms, con);

               if (error == true) {      // max guest times exceeded this hour

                  out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Maximum Number of Guest Times Exceeded</H3>");
                  out.println("<BR>Sorry, but the maximum number of guest times are already scheduled during this hour.");
                  out.println("<BR><BR>Please try another time of the day.");

                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
             
                  returnToSlot(out, slotParms, 3);
                  return;
               }
            }      // end of if Merion

            //
            //  If Bearpath, then check for member-only time
            //
            if (club.equals( "bearpath" )) {           // if Bearpath

               error = verifySlot.checkBearpathGuests(slotParms.day, slotParms.date, slotParms.time, slotParms.ind);

               if (error == true) {      // no guests allowed

                  out.println(SystemUtils.HeadTitle("Max Number of Guest Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guests Not Allowed</H3>");
                  out.println("<BR>Sorry, but guests are not allowed during this time.  This is a member-only time.");
                  out.println("<BR><BR>Please try another time of the day.");
                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
                
                  returnToSlot(out, slotParms, 3);
                  return;
               }
            }      // end of if Bearpath

         }      // end of if guests

         //
         //  Custom for Oakmont CC
         //
         if (club.equals( "oakmont" )) {      // if Oakmont CC

            if (slotParms.ind > 14) {          // if this date is more than 14 days ahead

               //
               //  More than 14 days in advance - must have 3 guests !!
               //
               error = false;
               oakskip = true;                 // set in case we make it through here (for later)

               if (slotParms.members > 1) {

                  error = true;             // must be error

               } else {

                  if (slotParms.guests < 3) {

                     error = true;             // must be error
                  }
               }

               if (error == true) {        // if too many guests

                  out.println(SystemUtils.HeadTitle("Min Num Guests Not Specified - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H3>Insufficient Number of Guests Specified</H3><BR>");
                  out.println("<BR><BR>Sorry, you must have at least 1 guest per member when making<BR>");
                  out.println("a tee time more than 14 days in advance.");
                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, 3);
                  return;
               }
            }

            if (slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {  // if Wednesday or Friday

               //
               // **********************************************************************
               //  Oakmont - Check for dedicated guest tee times  (Wed & Fri)
               // **********************************************************************
               //
               error = false;                             // init error indicator

               try {

                  error = verifySlot.oakmontGuestsWF(slotParms, con);

               }
               catch (Exception e5) {

                  dbError(out, e5);
                  return;
               }

               if (error == true) {      // a member exceed the max allowed tee times per month

                  out.println(SystemUtils.HeadTitle("Max Num Guest Tee Times Exceeded - Reject"));
                  out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");

                  out.println("<BR><H3>Insufficient Number of Guests</H3><BR>");
                  out.println("<BR><BR>Sorry, you must include 3 guests during the selected time for this day.");
                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, 3);
                  return;
               }
            }
         }

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

            error = verifySlot.checkMemRests(slotParms, con);

         }
         catch (Exception e7) {

            dbError(out, e7);
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted</H3><BR>");
            out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");
            out.println("This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b><br><br>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");

            returnToSlot(out, slotParms, 4);
            return;
         }

         //
         //  If Medinah process custom restrictions
         //
         if (club.equals( "medinahcc" )) {

            //
            // *******************************************************************************
            //  Medinah CC - Check Contingent Member Restrictions
            //
            //     on return - 'medError' contains the error code
            //
            // *******************************************************************************
            //
            int medError = medinahCustom.checkContingent(slotParms);      // go check rest's

            if (medError > 0) {          // if we hit on a restriction

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is restricted from playing during this time.<br><br>");

               if (medError == 1) {
                  out.println("A Family Member (8 - 11) must be accompanied by an adult.<br><br>");
               }
               if (medError == 2) {
                  out.println("A Family Member (12 & 13) must be accompanied by an adult.<br><br>");
               }
               if (medError == 3) {
                  out.println("A Family Member (14 - 16) must be accompanied by a Member.<br><br>");
               }
               if (medError == 5 || medError == 10) {
                  out.println("A Family Member (17 and Over) must be accompanied by a Member.<br><br>");
               }
               if (medError == 6) {
                  out.println("A Family Member (12 & 13) must be accompanied by a Member.<br><br>");
               }
               if (medError == 7) {
                  out.println("A Family Member (8 - 11) must be accompanied by an adult.<br><br>");
               }
               if (medError == 4 || medError == 8 || medError == 9) {
                  out.println("A Spouse must be accompanied by a Member.<br><br>");
               }
               if (medError == 11) {
                  out.println("A Family Member (14 - 16) must be accompanied by an adult.<br><br>");
               }

               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
              
/*
            //
            // *******************************************************************************
            //  Medinah CC - Check for Max Advanced Reservation Rights
            //
            //     on return - 'medError' contains the number of rights used if max reached
            //
            // *******************************************************************************
            //
            try {

               medError = medinahCustom.checkARRmax(slotParms, con);

            }
            catch (Exception e7) {

               dbError(out, e7);
               return;
            }                             // end of member restriction tests

            if (medError > 0) {          // if we hit a max

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#FFFFFF\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Restricted</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already ");

               if (medError == 1) {

                  out.println("used an Advanced Reservation Right within 14 days of this request.<br><br>");

               } else {

                  if (medError == 99) {     // if temporarily changed

                     medError = 1;          // change it back
                  }
                  out.println("used " +medError+ " Advanced Reservation Rights this season.<br><br>");
               }

               out.println("Please remove this player or try a different time.<br>");
               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
*/              
  
         }

         //
         //  If Stanwich Club process custom restrictions
         //
         if (club.equals( "stanwichclub" )) {

            error = verifySlot.checkStanwichDependents(slotParms);     // check for Dependent w/o an Adult

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
               out.println("<BR><BR>Sorry, dependents must be accompanied by an adult for this day and time.");
               out.println("<BR><BR>All Tee Times with a Dependent must include at least 1 Adult during times specified by the golf shop.");
               out.println("<BR><BR>Please add an adult player or select a different time of the day or a different day.");
               out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
         }

         //
         //  If Castle Pines process custom restrictions
         //
         if (club.equals( "castlepines" )) {

            error = verifySlot.checkCastleDependents(slotParms);     // check for Dependent w/o an Adult

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Dependent Without An Adult</H3>");
               out.println("<BR><BR>Sorry, dependents must be accompanied by an adult at all times.");
               out.println("<BR><BR>Please add an adult player or return to the tee sheet.");
               out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
         }

         //
         //  Cherry Hills - custom member type and membership restrictions
         //
         if (club.equals( "cherryhills" )) {

            error = verifySlot.checkCherryHills(slotParms);    // process custom restrictions

            if (error == true) {

               out.println(SystemUtils.HeadTitle("Data Entry Error"));
               out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
               out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
               out.println("<center>");
               out.println("<BR><BR><BR><H3>Player Not Allowed</H3>");
               out.println("<BR><BR>Sorry, one or more players are not allowed to be part of a tee time for this day and time.");
               if (slotParms.day.equals( "Monday" ) || slotParms.day.equals( "Wednesday" ) || slotParms.day.equals( "Friday" )) {
                  out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
               } else {
                  if (slotParms.day.equals( "Tuesday" )) {
                     if (slotParms.time > 1100) {
                        out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                     } else {
                        out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 11 AM on Tuesdays.");
                     }
                  } else {
                     if (slotParms.day.equals( "Thursday" )) {
                        if (slotParms.time > 1000) {
                           out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                        } else {
                           out.println("<BR><BR>Only Spouses may make a request more than 1 day in advance for a tee time before 10 AM on Thursdays.");
                        }
                     } else {
                        if (slotParms.day.equals( "Sunday" )) {
                           if (slotParms.time > 1000) {
                              out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                           } else {
                              out.println("<BR><BR>Only Members may be included in a tee time before 10 AM on Sundays.");
                           }
                        } else {       // Saturday or Holiday
                           if (slotParms.time > 1100) {
                              out.println("<BR><BR>A Member must be included when making the request more than 1 day in advance.");
                           } else {
                              out.println("<BR><BR>Player not allowed to make a tee time more than 24 hours in advance on Saturdays and Holidays before 11 AM.");
                           }
                        }
                     }
                  }
               }
               out.println("<BR><BR>Please change players or select a different day or time of day.");
               out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");
               returnToSlot(out, slotParms, 4);
               return;
            }
         }
           
         //
         //  If Ritz-Carlton - check for max 'Club Golf' and 'Recip' times this hour
         //
         if (club.equals( "ritzcarlton" )) {

            error = verifySlot.checkRitz(slotParms, con);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Special Tee Time Quota Exceeded</H3>");
               out.println("<BR>Sorry, there are already 2 tee times with Club Golf members");
               out.println("<BR>or Recip guests scheduled this hour.<br><br>");
               out.println("Please select a different time of day, or change the players.<br><br>");
               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
         }
           
         //
         //  If Skaneateles - check for Dependent Restriction
         //
         if (club.equals( "skaneateles" )) {

            error = verifySlot.checkSkaneateles(slotParms);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
               out.println("<BR>Sorry, dependents must be accompanied by an adult after 4:00 PM each day.<br><br>");
               out.println("Please select a different time of day, or change the players.<br><br>");
               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
         }

         //
         //  If Oakland Hills - check for Dependents - must be accompanied by adult (always)
         //
         if (club.equals( "oaklandhills" )) {

            error = verifySlot.checkOaklandKids(slotParms);

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Unaccompanied Dependents Not Allowed</H3>");
               out.println("<BR>Sorry, dependents must be accompanied by an adult.<br><br>");
               out.println("Would you like to override the restriction and allow this reservation?");
               out.println("<BR><BR>");

               returnToSlot(out, slotParms, 4);
               return;
            }
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
         if ((!slotParms.player5.equals( "" )) && (slotParms.p5rest.equals( "Yes" ))) { // if 5-somes restricted prompt user to skip test

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
            returnToSlot(out, slotParms, 5);
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

            error = verifySlot.checkMemNum(slotParms, con);

         }
         catch (Exception e7) {

            dbError(out, e7);
            return;
         }                             // end of member restriction tests

         if (error == true) {          // if we hit on a restriction

            out.println(SystemUtils.HeadTitle("Member Number Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Restricted by Member Number</H3><BR>");
            out.println("<BR>Sorry, ");
            if (!slotParms.pnum1.equals( "" )) {
               out.println("<b>" + slotParms.pnum1 + "</b> ");
            }
            if (!slotParms.pnum2.equals( "" )) {
               out.println("<b>" + slotParms.pnum2 + "</b> ");
            }
            if (!slotParms.pnum3.equals( "" )) {
               out.println("<b>" + slotParms.pnum3 + "</b> ");
            }
            if (!slotParms.pnum4.equals( "" )) {
               out.println("<b>" + slotParms.pnum4 + "</b> ");
            }
            if (!slotParms.pnum5.equals( "" )) {
               out.println("<b>" + slotParms.pnum5 + "</b> ");
            }
            out.println("is/are restricted from playing during this time because the");
            out.println("<BR> number of members with the same member number has exceeded the maximum allowed.");
            out.println("<br><br>This time slot has the following restriction:  <b>" + slotParms.rest_name + "</b>");
            out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms, 6);
            return;
         }

         //
         //  Hazeltine - check for consecutive singles or 2-somes
         //
         if (club.equals( "hazeltine" )) {

            if (slotParms.player3.equals( "" ) && slotParms.player4.equals( "" ) && slotParms.player5.equals( "" )) {  // if 1 or 2 players

               error = verifySlot.checkHazGrps(slotParms, con);    // process custom restriction

               if (error == true) {

                  out.println(SystemUtils.HeadTitle("Data Entry Error"));
                  out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#0000FF\" vlink=\"#0000FF\" alink=\"#FF0000\" OnLoad=\"window.defaultStatus='© 2001 ForeTees, LLC';\">");
                  out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
                  out.println("<center>");
                  out.println("<BR><BR><BR><H3>Request Not Allowed</H3>");
                  out.println("<BR><BR>Sorry, there is already a small group immediately before or after this time.");
                  out.println("<BR><BR>There cannot be 2 consecutive small groups during this time.");
                  out.println("<BR><BR>Please add players or select a different time of day.");
                  out.println("<BR><BR>Would you like to override the restriction and allow this reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot to change the player order
                  //
                  returnToSlot(out, slotParms, 6);
                  return;
               }
            }
         }      // end of Hazeltine
                    
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
         slotParms.hit = false;                             // init error indicator
         slotParms.hit2 = false;                             // init error indicator
         String tmsg = "";
         int thr = 0;
         int tmin = 0;


         try {

            verifySlot.checkSched(slotParms, con);

         }
         catch (Exception e21) {

            dbError(out, e21);
            return;
         }

         if (slotParms.hit == true || slotParms.hit2 == true || slotParms.hit3 == true) { // if we hit on a duplicate res

            if (slotParms.time2 != 0) {                                  // if other time was returned

               thr = slotParms.time2 / 100;                      // set time string for message
               tmin = slotParms.time2 - (thr * 100);
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
               if (!slotParms.course2.equals( "" )) {        // if course provided

                  tmsg = tmsg + " on the " +slotParms.course2+ " course";
               }
            }
            out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Member Already Playing</H3><BR>");
            if (slotParms.rnds > 1) {       // if multiple rounds per day supported
               if (slotParms.hit3 == true) {       // if rounds too close together
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is scheduled to play another round within " +slotParms.hrsbtwn+ " hours.<br><br>");
                  out.println(slotParms.player + " is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
               } else {
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play the maximum number of times.<br><br>");
                  out.println("A player can only be scheduled " +slotParms.rnds+ " times per day.<br><br>");
               }
            } else {
               if (slotParms.hit2 == true) {
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is part of a lottery request for this date.<br><br>");
               } else {
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is already scheduled to play on this date at <b>" +tmsg+ "</b>.<br><br>");
               }
               out.println("A player can only be scheduled once per day.<br><br>");
            }
            out.println("<BR><BR>Would you like to override this and allow the reservation?");
            out.println("<BR><BR>");
            //
            //  Return to _slot to change the player order
            //
            returnToSlot(out, slotParms, 7);
            return;
         }
           
         //
         //   If Merion and East course, then check if any other family members are scheduled today - not allowed.
         //
         if (club.equals( "merion" ) && slotParms.course.equals( "East" )) {

            slotParms.hit = false;                             // init error indicator

            try {

               verifySlot.checkMerionSched(slotParms, con);

            }
            catch (Exception e21) {

               dbError(out, e21);
               return;
            }

            if (slotParms.hit == true) {      // if another family member is already booked today

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Member Already Scheduled</H3><BR>");
               out.println("<BR>Sorry, <b>" + slotParms.player + "</b> already has a family member scheduled to play today.<br><br>");
               out.println("Only one player per membership is allowed each day.<br><br>");
               out.println("Please remove this player or try a different date.<br>");
               out.println("<BR>");
               out.println("<BR><BR>Would you like to override this and allow the reservation?");
               out.println("<BR><BR>");
               //
               //  Return to _slot to change the player order
               //
               returnToSlot(out, slotParms, 7);
               return;
            }
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
         if (!slotParms.mship1.equals( "" ) || !slotParms.mship2.equals( "" ) || !slotParms.mship3.equals( "" ) || 
             !slotParms.mship4.equals( "" ) || !slotParms.mship5.equals( "" )) {
           
            //
            //  skip if Oakmont guest time
            //
            if (!club.equals( "oakmont" ) || oakskip == false) {

               try {

                  error = verifySlot.checkDaysAdv(slotParms, con);

               }
               catch (Exception e21) {

                  dbError(out, e21);
                  return;
               }

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Days in Advance Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is not allowed to be part of a tee time this far in advance.<br><br>");
                  out.println("This restriction is based on the 'Days In Advance' setting for each Membership Type.<br><br>");
                  out.println("<BR><BR>Would you like to override this and allow the reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot
                  //
                  returnToSlot(out, slotParms, 8);
                  return;
               }
            }
              
            //
            //  if Belle Haven - check for 'Elective' membership types - limited to 10 rounds per year on w/e's
            //
            if (club.equals( "bellehaven" )) {

               try {

                  error = verifySlot.checkBelleHaven(slotParms, con);

               }
               catch (Exception e22) {

                  dbError(out, e22);
                  return;
               }

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Weekend Tee Time Limit Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> is an Elective member and has");
                  out.println("<BR>already played 10 times on weekends or holidays this year.<br><br>");
                  out.println("<BR><BR>Would you like to override this and allow the reservation?");
                  out.println("<BR><BR>");
                  //
                  //  Return to _slot
                  //
                  returnToSlot(out, slotParms, 8);
                  return;
               }
            }  

            //
            //  If Oakland Hills check for advance reservations
            //
            if (club.equals( "oaklandhills" )) {

               if (slotParms.ind > 5) {

                  //
                  //   check for advance times if more than 5 days in adv
                  //
                  error = verifySlot.checkOaklandAdvTime1(slotParms, con);

                  if (error == true) {          // if we hit on a violation

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Member Has Already Used An Advance Request</H3>");
                     out.println("<BR>Sorry, each membership is entitled to only one advance tee time request.<br>");
                     out.println("<BR>" +slotParms.player+ " has already used his/her advance tee time request for the season.");
                     out.println("<BR><BR>Would you like to override this and allow the reservation?");
                     out.println("<BR><BR>");

                     returnToSlot(out, slotParms, 8);
                     return;
                  }

                  error = verifySlot.checkOaklandAdvTime2(slotParms, con);

                  if (error == true) {          // if we hit on a violation

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Maximum Allowed Advanced Tee Times Exist</H3>");
                     out.println("<BR>Sorry, the maximum number of advanced tee time requests already exist on the selected date.");
                     out.println("<BR><BR>Would you like to override this and allow the reservation?");
                     out.println("<BR><BR>");

                     returnToSlot(out, slotParms, 8);
                     return;
                  }
               }
                 
               if (slotParms.ind > 7) {        // if more than 7 days in advance

                  //
                  //  Cannot have X's in tee time - members only!!
                  //
                  if (slotParms.player1.equalsIgnoreCase( "x" ) || slotParms.player2.equalsIgnoreCase( "x" ) ||
                      slotParms.player3.equalsIgnoreCase( "x" ) || slotParms.player4.equalsIgnoreCase( "x" ) ||
                      slotParms.player5.equalsIgnoreCase( "x" )) {

                     out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                     out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                     out.println("<hr width=\"40%\">");
                     out.println("<BR><BR><H3>Invalid Player Selection</H3>");
                     out.println("<BR>Sorry, you cannot reserve player positions with an X more than 7 days in advance.<br>");
                     out.println("<BR><BR>Would you like to override this and allow the reservation?");
                     out.println("<BR><BR>");

                     returnToSlot(out, slotParms, 8);
                     return;
                  }
               }
            }       // end of IF Oakland Hills
              
            //
            //  If CC of the Rockies & Catamount Ranch - check for max number of advance tee times
            //
            if ((club.equals( "ccrockies" ) || club.equals( "catamount" )) && slotParms.ind > 0) {       // if not today

               error = verifySlot.checkRockies(slotParms, con);

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Member Already Has Max Allowed Advance Requests</H3>");
                  out.println("<BR>Sorry, " +slotParms.player+ " already has 5 advance tee time requests scheduled.<br>");
                  out.println("<BR><BR>Would you like to override this and allow the reservation?");
                  out.println("<BR><BR>");

                  returnToSlot(out, slotParms, 8);
                  return;
               }
            }           // end of IF CC of the Rockies

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
         if (slotParms.guests != 0 && slotParms.members != 0) {      // if both guests and members were included

            if (slotParms.g1.equals( "" )) {              // if slot 1 is not a guest

               //
               //  Both guests and members specified - determine guest owners by order
               //
               gi = 0;
               memberName = "";
               while (gi < 5) {                  // cycle thru arrays and find guests/members

                  if (!slotParms.gstA[gi].equals( "" )) {

                     usergA[gi] = memberName;       // get last players username
                  } else {
                     usergA[gi] = "";               // init field
                  }
                  if (!memA[gi].equals( "" )) {

                     memberName = memA[gi];        // get players username
                  }
                  gi++;
               }
               slotParms.userg1 = usergA[0];        // max of 4 guests since 1 player must be a member to get here
               slotParms.userg2 = usergA[1];
               slotParms.userg3 = usergA[2];
               slotParms.userg4 = usergA[3];
               slotParms.userg5 = usergA[4];
            }
              
            if (!slotParms.g1.equals( "" ) || slotParms.members > 1) {  // if slot 1 is a guest OR more than 1 member

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
               if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

                  out.println("Guests must be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("You cannot have a guest in the first player position when one or more members are also specified.");
                  out.println("<BR><BR>");
               } else {
                  out.println("Guests should be specified <b>immediately after</b> the member they belong to.<br><br>");
                  out.println("Please verify that the following order is correct:");
                  out.println("<BR><BR>");
                  out.println(slotParms.player1 + " <BR>");
                  out.println(slotParms.player2 + " <BR>");
                  if (!slotParms.player3.equals( "" )) {
                     out.println(slotParms.player3 + " <BR>");
                  }
                  if (!slotParms.player4.equals( "" )) {
                     out.println(slotParms.player4 + " <BR>");
                  }
                  if (!slotParms.player5.equals( "" )) {
                     out.println(slotParms.player5 + " <BR>");
                  }
                  out.println("<BR>Would you like to process the request as is?");
               }

               //
               //  Return to _slot to change the player order
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");

               if (!slotParms.g1.equals( "" ) && !posType.equals( "" ) && !slotParms.oldPlayer1.equals( slotParms.player1 )) {

                  out.println("<input type=\"submit\" value=\"Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

               } else {
                  out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</form></font>");

                  //
                  //  Return to process the players as they are
                  //
                  out.println("<font size=\"2\">");
                  out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
                  out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
                  out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
                  out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
                  out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
                  out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
                  out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
                  out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
                  out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
                  out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
                  out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
                  out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
                  out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
                  out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
                  out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
                  out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
                  out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
                  out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
                  out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
                  out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
                  out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
                  out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                  out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
                  out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
                  out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
                  out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
                  out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
                  out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
                  out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
                  out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
                  out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
                  out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
                  out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
                  out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
                  out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
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
            if (slotParms.guests != 0) {      // if all guests 

               //
               //  At least one guest and no member has been specified.
               //  Get associated member names if already assigned.
               //
               try {
                 
                  if (!slotParms.userg1.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg1);

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

                        slotParms.mem1 = mem_name.toString();                      // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg2.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg2);

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

                        slotParms.mem2 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg3.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg3);

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

                        slotParms.mem3 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg4.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg4);

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

                        slotParms.mem4 = mem_name.toString();                          // convert to one string
                     }
                     pstmtc.close();
                  }
                  if (!slotParms.userg5.equals( "" )) {

                     PreparedStatement pstmtc = con.prepareStatement (
                        "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

                     pstmtc.clearParameters();        // clear the parms
                     pstmtc.setString(1, slotParms.userg5);

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

                        slotParms.mem5 = mem_name.toString();                          // convert to one string
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

               // if Hazeltine National and sponsored guests
               if ((club.equals( "hazeltine" )) &&   
                   (slotParms.player1.startsWith(sponsored) || slotParms.player2.startsWith(sponsored) ||
                    slotParms.player3.startsWith(sponsored) || slotParms.player4.startsWith(sponsored) ||
                    slotParms.player5.startsWith(sponsored))) {   

                  out.println("You are requesting a tee time for a Sponsored Group.<br>");
                  out.println("Sponsored Groups must be associated with a member.<br><br>");
                  out.println("<BR>Would you like to assign a member to the Sponsored Group?");

               } else {

                  if (slotParms.guests == 1) {      // if one guest
                     out.println("You are requesting a tee time for an unaccompanied guest.<br>");
                     out.println("The guest should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guest, or change the assignment?");
                  } else {
                     out.println("You are requesting a tee time for unaccompanied guests.<br>");
                     out.println("Guests should be associated with a member.<br><br>");
                     out.println("<BR>Would you like to assign a member to the guests, or change the assignments?");
                  }
               }

               //
               //  Return to _slot (doPost) to assign members
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"mem1\" value=\"" + slotParms.mem1 + "\">");
               out.println("<input type=\"hidden\" name=\"mem2\" value=\"" + slotParms.mem2 + "\">");
               out.println("<input type=\"hidden\" name=\"mem3\" value=\"" + slotParms.mem3 + "\">");
               out.println("<input type=\"hidden\" name=\"mem4\" value=\"" + slotParms.mem4 + "\">");
               out.println("<input type=\"hidden\" name=\"mem5\" value=\"" + slotParms.mem5 + "\">");
               out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + suppressEmails + "\">");

               out.println("<input type=\"hidden\" name=\"assign\" value=\"yes\">");  // assign member to guests

               out.println("<input type=\"submit\" value=\"Yes - Assign Member\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</form></font>");

               //
               //  Return to process the players as they are
               //
               out.println("<font size=\"2\">");
               out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
               out.println("<input type=\"hidden\" name=\"skip\" value=\"9\">");
               out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
               out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
               out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
               out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
               out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
               out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
               out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
               out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
               out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
               out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
               out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
               out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
               out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
               out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
               out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
               out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
               out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
               out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
               out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
               out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
               out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
               out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
               out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
               out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
               out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
               out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
               out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
               out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
               out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
               out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
               out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
               out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
               out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
               out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
               out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
               out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
               out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
               out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
               out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
               out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
               out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
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
            slotParms.userg1 = req.getParameter("userg1"); 
            slotParms.userg2 = req.getParameter("userg2");
            slotParms.userg3 = req.getParameter("userg3");
            slotParms.userg4 = req.getParameter("userg4");
            slotParms.userg5 = req.getParameter("userg5");
         }
      }         // end of IF skip9 
        
      //
      //  NOTE:  skip 10 is set in doPost method above when 'skip 9' processing prompts for 'Assign'.
      //         slotParms.userg1-5 are set in verifySlot when 'mem1-5' parms are passed.
      //
      if (club.equals( "hazeltine" )) {      // if Hazeltine National

         if (skip == 9 || skip == 10) {   // *****NOTE:  skip=10 is set in doPost above from 'Assign' here ********

            //
            //  Member has been assigned to the Sponsored Group (unaccomp. guests)
            //
            int rcode = 0;
              
            try {

               rcode = verifySlot.checkSponsGrp(slotParms, con);  // verify Sponsored Group for Hazeltine
            }
            catch (Exception e29) {

               dbError(out, e29);
               return;
            }

            if (rcode > 0) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Sponsored Group Error - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Restriction For Sponsored Group Request</H3><BR>");
               out.println("<BR>Your request for a Sponsored Group has been rejected for the following reason:<br><br>");
               if (rcode == 1) {
                  out.println("The maximum number of Sponsored Groups have already been scheduled for this day.<br><br>");
               } else {
                  if (rcode == 2) {
                     out.println("Sponsored Groups are not allowed at this time of day.<br><br>");
                  } else {
                     out.println("Member already has 2 Sponsored Groups scheduled today.<br><br>");
                  }
               }
               out.println("<BR><BR>Would you like to override this and allow the reservation?");
               out.println("<BR><BR>");
               //
               //  Return to _slot
               //
               returnToSlot(out, slotParms, 11);
               return;
            }
         }
      }

      if (skip < 12) {


         //
         //***********************************************************************************************
         //
         //  Now that the guests are assigned, check for any Guest Quotas - if any guests requested
         //
         //***********************************************************************************************
         //
         if (!slotParms.userg1.equals( "" ) || !slotParms.userg2.equals( "" ) || !slotParms.userg3.equals( "" ) ||
             !slotParms.userg4.equals( "" ) || !slotParms.userg5.equals( "" )) {

            try {

               error = verifySlot.checkGuestQuota(slotParms, con);

            }
            catch (Exception e22) {
            }

            if (error == true) {          // if we hit on a violation

               out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
               out.println("<BR>Sorry, requesting <b>" + slotParms.player + "</b> exceeds the guest quota established for this guest type.");
               out.println("<br><br>You will have to remove the guest in order to complete this request.");
               out.println("<BR><BR>");
               out.println("<BR>Would you like to override this restriction and allow the tee time request?");
               //
               //  Return to _slot (doPost) to assign members
               //
               returnToSlot(out, slotParms, 12);
               return;
            }


            //
            //  Medinah Custom - check for guest quotas on Course #1 (max 12 guest per family between 6/01 - 8/31)
            //
            if (club.equals( "medinahcc" )) {

               if (slotParms.course.equals( "No 1" )) {                 // if Course #1

                  error = medinahCustom.checkNonRes1(slotParms, con);
               }

               if (slotParms.course.equals( "No 3" )) {                 // if Course #3

                  error = medinahCustom.checkGuests3(slotParms, con);
               }

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Quota Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already met the guest quota for June through August.");
                  out.println("<br><br>You will have to remove the guest in order to complete this request.");
                  out.println("<BR><BR>");
                  out.println("<BR>Would you like to override this restriction and allow the tee time request?");
                  //
                  //  Return to _slot (doPost) to assign members
                  //
                  returnToSlot(out, slotParms, 12);
                  return;
               }
            }

            //
            //  Merion Custom - check for guest quotas on East Course
            //
            if (club.equals( "merion" ) && slotParms.course.equals( "East" )) {

               error = verifySlot.checkMerionGres(slotParms, con);

               if (error == true) {          // if we hit on a violation

                  out.println(SystemUtils.HeadTitle("Member Restricted - Reject"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><BR><H3>Guest Time Quota Exceeded for Member</H3><BR>");
                  out.println("<BR>Sorry, <b>" + slotParms.player + "</b> has already met the quota for Guest Times.");
                  out.println("<br><br>You will have to remove the guest(s) in order to complete this request.");
                  out.println("<BR><BR>");
                  out.println("<BR>Would you like to override this restriction and allow the tee time request?");
                  //
                  //  Return to _slot (doPost) to assign members
                  //
                  returnToSlot(out, slotParms, 12);
                  return;
               }
            }
         }

      } else {   // skip 12 requested?

         if (skip == 12) {

            //
            //  We must restore the guest usernames
            //
            slotParms.userg1 = req.getParameter("userg1");
            slotParms.userg2 = req.getParameter("userg2");
            slotParms.userg3 = req.getParameter("userg3");
            slotParms.userg4 = req.getParameter("userg4");
            slotParms.userg5 = req.getParameter("userg5");
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
      if (((!slotParms.player1.equals( "" )) && (!slotParms.player1.equalsIgnoreCase( "x" )) && (slotParms.g1.equals( "" ))) ||
          ((!slotParms.player2.equals( "" )) && (!slotParms.player2.equalsIgnoreCase( "x" )) && (slotParms.g2.equals( "" ))) ||
          ((!slotParms.player3.equals( "" )) && (!slotParms.player3.equalsIgnoreCase( "x" )) && (slotParms.g3.equals( "" ))) ||
          ((!slotParms.player4.equals( "" )) && (!slotParms.player4.equalsIgnoreCase( "x" )) && (slotParms.g4.equals( "" ))) ||
          ((!slotParms.player5.equals( "" )) && (!slotParms.player5.equalsIgnoreCase( "x" )) && (slotParms.g5.equals( "" )))) {

         //
         //  If players changed, then set email flag
         //
         // see if the player has changed - send email notification to all if true
         // if new tee time oldPlayer1 will be empty 
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 )) {
            sendemail = 1;
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 )) {
            sendemail = 1;
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 )) {
            sendemail = 1;
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 )) {
            sendemail = 1;
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 )) {
            sendemail = 1;
         }

         //
         //  Verification complete -
         //   Set email type based on new or update request (cancel set above)
         //   Also, bump stats counters for reports
         //
         if ((!slotParms.oldPlayer1.equals( "" )) || (!slotParms.oldPlayer2.equals( "" )) || (!slotParms.oldPlayer3.equals( "" )) ||
             (!slotParms.oldPlayer4.equals( "" )) || (!slotParms.oldPlayer5.equals( "" ))) {

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
      if (slotParms.player1.equals( "" ) || slotParms.player1.equalsIgnoreCase( "x" )) {

         slotParms.show1 = 0;       // reset show parm if no player
      }
        
      if (slotParms.player2.equals( "" ) || slotParms.player2.equalsIgnoreCase( "x" )) {

         slotParms.show2 = 0;       // reset show parm if no player
      }

      if (slotParms.player3.equals( "" ) || slotParms.player3.equalsIgnoreCase( "x" )) {

         slotParms.show3 = 0;       // reset show parm if no player
      }

      if (slotParms.player4.equals( "" ) || slotParms.player4.equalsIgnoreCase( "x" )) {

         slotParms.show4 = 0;       // reset show parm if no player
      }

      if (slotParms.player5.equals( "" ) || slotParms.player5.equalsIgnoreCase( "x" )) {

         slotParms.show5 = 0;       // reset show parm if no player
      }

      //
      //   set show value if double check-in feature supported
      //
      if ((!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" ))) {

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
         if (!slotParms.player1.equals( slotParms.oldPlayer1 ) && slotParms.show1 == 0) {
            slotParms.show1 = tmp_pci;
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 ) && slotParms.show2 == 0) {
            slotParms.show2 = tmp_pci;
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 ) && slotParms.show3 == 0) {
            slotParms.show3 = tmp_pci;
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 ) && slotParms.show4 == 0) {
            slotParms.show4 = tmp_pci;
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 ) && slotParms.show5 == 0) {
            slotParms.show5 = tmp_pci;
         }
      }     // end set show values

      //
      //  Adjust POS values if necessary
      //
      if ((!slotParms.player1.equals( "" ) && !slotParms.player1.equalsIgnoreCase( "x" )) ||
          (!slotParms.player2.equals( "" ) && !slotParms.player2.equalsIgnoreCase( "x" )) ||
          (!slotParms.player3.equals( "" ) && !slotParms.player3.equalsIgnoreCase( "x" )) ||
          (!slotParms.player4.equals( "" ) && !slotParms.player4.equalsIgnoreCase( "x" )) ||
          (!slotParms.player5.equals( "" ) && !slotParms.player5.equalsIgnoreCase( "x" ))) {

         //
         //  If player has changed and pos already sent, then reset the pos value
         //
         if (!slotParms.player1.equals( slotParms.oldPlayer1 ) && slotParms.pos1 == 1) {
            slotParms.pos1 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotParms.player2.equals( slotParms.oldPlayer2 ) && slotParms.pos2 == 1) {
            slotParms.pos2 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotParms.player3.equals( slotParms.oldPlayer3 ) && slotParms.pos3 == 1) {
            slotParms.pos3 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotParms.player4.equals( slotParms.oldPlayer4 ) && slotParms.pos4 == 1) {
            slotParms.pos4 = 0;
            posSent = true;        // indicate POS already sent for this group (warning)
         }

         if (!slotParms.player5.equals( slotParms.oldPlayer5 ) && slotParms.pos5 == 1) {
            slotParms.pos5 = 0;
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
      pstmt6.setString(1, slotParms.player1);
      pstmt6.setString(2, slotParms.player2);
      pstmt6.setString(3, slotParms.player3);
      pstmt6.setString(4, slotParms.player4);
      pstmt6.setString(5, slotParms.user1);
      pstmt6.setString(6, slotParms.user2);
      pstmt6.setString(7, slotParms.user3);
      pstmt6.setString(8, slotParms.user4);
      pstmt6.setString(9, slotParms.p1cw);
      pstmt6.setString(10, slotParms.p2cw);
      pstmt6.setString(11, slotParms.p3cw);
      pstmt6.setString(12, slotParms.p4cw);
      pstmt6.setFloat(13, slotParms.hndcp1);
      pstmt6.setFloat(14, slotParms.hndcp2);
      pstmt6.setFloat(15, slotParms.hndcp3);
      pstmt6.setFloat(16, slotParms.hndcp4);
      //pstmt6.setShort(17, slotParms.show1);
      //pstmt6.setShort(18, slotParms.show2);
      //pstmt6.setShort(19, slotParms.show3);
      //pstmt6.setShort(20, slotParms.show4);
      pstmt6.setString(17, slotParms.player5);
      pstmt6.setString(18, slotParms.user5);
      pstmt6.setString(19, slotParms.p5cw);
      pstmt6.setFloat(20, slotParms.hndcp5);
      //pstmt6.setShort(25, slotParms.show5);
      pstmt6.setString(21, slotParms.notes);
      pstmt6.setInt(22, hide);
      pstmt6.setInt(23, proNew);
      pstmt6.setInt(24, proMod);
      pstmt6.setString(25, slotParms.mNum1);
      pstmt6.setString(26, slotParms.mNum2);
      pstmt6.setString(27, slotParms.mNum3);
      pstmt6.setString(28, slotParms.mNum4);
      pstmt6.setString(29, slotParms.mNum5);
      pstmt6.setString(30, slotParms.userg1);
      pstmt6.setString(31, slotParms.userg2);
      pstmt6.setString(32, slotParms.userg3);
      pstmt6.setString(33, slotParms.userg4);
      pstmt6.setString(34, slotParms.userg5);
      pstmt6.setString(35, slotParms.orig_by);
      pstmt6.setString(36, slotParms.conf);
      pstmt6.setInt(37, slotParms.p91);
      pstmt6.setInt(38, slotParms.p92);
      pstmt6.setInt(39, slotParms.p93);
      pstmt6.setInt(40, slotParms.p94);
      pstmt6.setInt(41, slotParms.p95);
      pstmt6.setInt(42, slotParms.pos1);
      pstmt6.setInt(43, slotParms.pos2);
      pstmt6.setInt(44, slotParms.pos3);
      pstmt6.setInt(45, slotParms.pos4);
      pstmt6.setInt(46, slotParms.pos5);
        
      pstmt6.setLong(47, slotParms.date);
      pstmt6.setInt(48, slotParms.time);
      pstmt6.setInt(49, slotParms.fb);
      pstmt6.setString(50, slotParms.course);

      /*
      pstmt6.setShort(17, slotParms.show1);
      pstmt6.setShort(18, slotParms.show2);
      pstmt6.setShort(19, slotParms.show3);
      pstmt6.setShort(20, slotParms.show4);
      pstmt6.setString(21, slotParms.player5);
      pstmt6.setString(22, slotParms.user5);
      pstmt6.setString(23, slotParms.p5cw);
      pstmt6.setFloat(24, slotParms.hndcp5);
      pstmt6.setShort(25, slotParms.show5);
      pstmt6.setString(26, slotParms.notes);
      pstmt6.setInt(27, hide);
      pstmt6.setInt(28, proNew);
      pstmt6.setInt(29, proMod);
      pstmt6.setString(30, slotParms.mNum1);
      pstmt6.setString(31, slotParms.mNum2);
      pstmt6.setString(32, slotParms.mNum3);
      pstmt6.setString(33, slotParms.mNum4);
      pstmt6.setString(34, slotParms.mNum5);
      pstmt6.setString(35, slotParms.userg1);
      pstmt6.setString(36, slotParms.userg2);
      pstmt6.setString(37, slotParms.userg3);
      pstmt6.setString(38, slotParms.userg4);
      pstmt6.setString(39, slotParms.userg5);
      pstmt6.setString(40, slotParms.orig_by);
      pstmt6.setString(41, slotParms.conf);
      pstmt6.setInt(42, slotParms.p91);
      pstmt6.setInt(43, slotParms.p92);
      pstmt6.setInt(44, slotParms.p93);
      pstmt6.setInt(45, slotParms.p94);
      pstmt6.setInt(46, slotParms.p95);
      pstmt6.setInt(47, slotParms.pos1);
      pstmt6.setInt(48, slotParms.pos2);
      pstmt6.setInt(49, slotParms.pos3);
      pstmt6.setInt(50, slotParms.pos4);
      pstmt6.setInt(51, slotParms.pos5);
        
      pstmt6.setLong(52, slotParms.date);
      pstmt6.setInt(53, slotParms.time);
      pstmt6.setInt(54, slotParms.fb);
      pstmt6.setString(55, slotParms.course);
      */
      count = pstmt6.executeUpdate();      // execute the prepared stmt

      //
      // Now update each of the showx fields for this tee time
      // 
      String strSQL = "";
      String tmpClause = "";
      
      strSQL = "UPDATE teecurr2 SET show1 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotParms.show1 == 0) ? " AND show1 != 2" : "";
     
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotParms.show1);
      pstmt6.setLong(2, slotParms.date);
      pstmt6.setInt(3, slotParms.time);
      pstmt6.setInt(4, slotParms.fb);
      pstmt6.setString(5, slotParms.course);
      pstmt6.executeUpdate();
         
      
      strSQL = "UPDATE teecurr2 SET show2 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotParms.show2 == 0) ? " AND show2 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotParms.show2);
      pstmt6.setLong(2, slotParms.date);
      pstmt6.setInt(3, slotParms.time);
      pstmt6.setInt(4, slotParms.fb);
      pstmt6.setString(5, slotParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show3 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotParms.show3 == 0) ? " AND show3 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotParms.show3);
      pstmt6.setLong(2, slotParms.date);
      pstmt6.setInt(3, slotParms.time);
      pstmt6.setInt(4, slotParms.fb);
      pstmt6.setString(5, slotParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show4 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotParms.show4 == 0) ? " AND show4 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotParms.show4);
      pstmt6.setLong(2, slotParms.date);
      pstmt6.setInt(3, slotParms.time);
      pstmt6.setInt(4, slotParms.fb);
      pstmt6.setString(5, slotParms.course);
      pstmt6.executeUpdate();
      
      strSQL = "UPDATE teecurr2 SET show5 = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?";
      tmpClause = (slotParms.show5 == 0) ? " AND show5 != 2" : ""; 
      pstmt6 = con.prepareStatement (strSQL + tmpClause);
      pstmt6.clearParameters();
      pstmt6.setShort(1, slotParms.show5);
      pstmt6.setLong(2, slotParms.date);
      pstmt6.setInt(3, slotParms.time);
      pstmt6.setInt(4, slotParms.fb);
      pstmt6.setString(5, slotParms.course);
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
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
      out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + returnCourse + "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
      out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
      out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
      out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
      out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
      out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
      out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
      out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
      out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
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
     
   if (slotParms.oldPlayer1.equals( "" ) && slotParms.oldPlayer2.equals( "" ) && slotParms.oldPlayer3.equals( "" ) &&
       slotParms.oldPlayer4.equals( "" ) && slotParms.oldPlayer5.equals( "" )) {

      //  new tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 0, con);

   } else {

      //  update tee time
      SystemUtils.updateHist(slotParms.date, slotParms.day, slotParms.time, slotParms.fb, slotParms.course, slotParms.player1, slotParms.player2, slotParms.player3,
                             slotParms.player4, slotParms.player5, user, fullName, 1, con);
   }

   //
   //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
   //
   if (club.equals( "hazeltine" )) {      // if Hazeltine National

      verifySlot.Htoggle(date, time, fb, slotParms, con);
   }

/*
   //
   //  If Medinah CC, then count if ARRs if necessary
   //
   if (club.equals( "medinahcc" ) && slotParms.ind > 2) {      // if Medinah and more than 2 days in adv

      if (!slotParms.user1.equals( "" ) || !slotParms.user2.equals( "" ) || !slotParms.user3.equals( "" ) ||
          !slotParms.user4.equals( "" )) {                      // if not a cancel request

         medinahCustom.addARR(slotParms, con);
      }
   }
*/

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
      out.println("<Title>Proshop Tee Slot Page</Title>");
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
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + returnCourse + "&jump=" + slotParms.jump + "\">");
         } else {
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + slotParms.course + "&jump=" + slotParms.jump + "\">");
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
         out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
      }
      out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
      out.println("<input type=\"hidden\" name=\"jump\" value=" + slotParms.jump + ">");
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
   if (sendemail != 0 && suppressEmails.equalsIgnoreCase( "no" )) {

      //
      //  allocate a parm block to hold the email parms
      //
      parmEmail parme = new parmEmail();          // allocate an Email parm block

      //
      //  Set the values in the email parm block
      //
      parme.type = "tee";         // type = tee time
      parme.date = slotParms.date;
      parme.time = slotParms.time;
      parme.fb = slotParms.fb;
      parme.mm = slotParms.mm;
      parme.dd = slotParms.dd;
      parme.yy = slotParms.yy;

      parme.user = user;
      parme.emailNew = emailNew;
      parme.emailMod = emailMod;
      parme.emailCan = emailCan;

      parme.p91 = slotParms.p91;
      parme.p92 = slotParms.p92;
      parme.p93 = slotParms.p93;
      parme.p94 = slotParms.p94;
      parme.p95 = slotParms.p95;

      parme.course = slotParms.course;
      parme.day = slotParms.day;
      parme.notes = slotParms.notes;

      parme.player1 = slotParms.player1;
      parme.player2 = slotParms.player2;
      parme.player3 = slotParms.player3;
      parme.player4 = slotParms.player4;
      parme.player5 = slotParms.player5;

      parme.oldplayer1 = slotParms.oldPlayer1;
      parme.oldplayer2 = slotParms.oldPlayer2;
      parme.oldplayer3 = slotParms.oldPlayer3;
      parme.oldplayer4 = slotParms.oldPlayer4;
      parme.oldplayer5 = slotParms.oldPlayer5;

      parme.user1 = slotParms.user1;
      parme.user2 = slotParms.user2;
      parme.user3 = slotParms.user3;
      parme.user4 = slotParms.user4;
      parme.user5 = slotParms.user5;

      parme.olduser1 = slotParms.oldUser1;
      parme.olduser2 = slotParms.oldUser2;
      parme.olduser3 = slotParms.oldUser3;
      parme.olduser4 = slotParms.oldUser4;
      parme.olduser5 = slotParms.oldUser5;

      parme.pcw1 = slotParms.p1cw;
      parme.pcw2 = slotParms.p2cw;
      parme.pcw3 = slotParms.p3cw;
      parme.pcw4 = slotParms.p4cw;
      parme.pcw5 = slotParms.p5cw;

      parme.oldpcw1 = slotParms.oldp1cw;
      parme.oldpcw2 = slotParms.oldp2cw;
      parme.oldpcw3 = slotParms.oldp3cw;
      parme.oldpcw4 = slotParms.oldp4cw;
      parme.oldpcw5 = slotParms.oldp5cw;

      //
      //  If tee time is during a shotgun event, then change this to an event email
      //
      if (!event.equals( "" ) && event_type == 1) { 
        
         int act_hr = 0;
         int act_min = 0;
           
         String act_ampm = "";
         String act_time = "";
           
         try {

            //
            //   Get the parms for this event
            //
            PreparedStatement pstmtev = con.prepareStatement (
               "SELECT act_hr, act_min FROM events2b " +
               "WHERE name = ?");

            pstmtev.clearParameters();        // clear the parms
            pstmtev.setString(1, event);
            rs = pstmtev.executeQuery();      // execute the prepared stmt

            if (rs.next()) {

               act_hr = rs.getInt("act_hr");
               act_min = rs.getInt("act_min");
            }
            pstmtev.close();

            //
            //  Create time value for email msg
            //
            act_ampm = " AM";

            if (act_hr == 0) {

               act_hr = 12;                 // change to 12 AM (midnight)

            } else {

               if (act_hr == 12) {

                  act_ampm = " PM";         // change to Noon
               }
            }
            if (act_hr > 12) {

               act_hr = act_hr - 12;
               act_ampm = " PM";             // change to 12 hr clock
            }

            //
            //  convert time to hour and minutes for email msg
            //
            if (act_min > 9) {

               act_time = act_hr + ":" + act_min + act_ampm;

            } else {

               act_time = act_hr + ":0" + act_min + act_ampm;
            }

         }
         catch (Exception e) {
         }

         parme.type = "event";         // type = event
         parme.time = 0;
         parme.fb = 0;
         parme.wuser1 = "";     // set Event-only fields
         parme.wuser2 = "";
         parme.wuser3 = "";
         parme.wuser4 = "";
         parme.wuser5 = "";
         parme.name = event;
         parme.etype = event_type;
         parme.act_time = act_time;       // actual time of event !!!
         parme.wait = 0;
         parme.checkWait = 0;
      }

      //
      //  Send the email
      //
      sendEmail.sendIt(parme, con);      // in common


      //
      //  If Oakmont CC, then check for any guests in the tee time - if so, send an email to Caddie Master
      //
      if (club.equals( "oakmont" )) {

         boolean foundOakGst = false;

         if (!slotParms.g1.equals( "" ) || !slotParms.g2.equals( "" ) || !slotParms.g3.equals( "" ) || !slotParms.g4.equals( "" ) ||
             !slotParms.g5.equals( "" )) {

            foundOakGst = true;                // guest in this tee time

         } else {                // check old tee time for guest

            if (!slotParms.oldPlayer1.equals( "" ) && slotParms.oldUser1.equals( "" ) && !slotParms.oldPlayer1.equalsIgnoreCase( "x" )) {

               foundOakGst = true;                // guest in the old tee time

            } else {

               if (!slotParms.oldPlayer2.equals( "" ) && slotParms.oldUser2.equals( "" ) && !slotParms.oldPlayer2.equalsIgnoreCase( "x" )) {

                  foundOakGst = true;                // guest in the old tee time

               } else {

                  if (!slotParms.oldPlayer3.equals( "" ) && slotParms.oldUser3.equals( "" ) && !slotParms.oldPlayer3.equalsIgnoreCase( "x" )) {

                     foundOakGst = true;                // guest in the old tee time

                  } else {

                     if (!slotParms.oldPlayer4.equals( "" ) && slotParms.oldUser4.equals( "" ) && !slotParms.oldPlayer4.equalsIgnoreCase( "x" )) {

                        foundOakGst = true;                // guest in the old tee time

                     } else {

                        if (!slotParms.oldPlayer5.equals( "" ) && slotParms.oldUser5.equals( "" ) && !slotParms.oldPlayer5.equalsIgnoreCase( "x" )) {

                           foundOakGst = true;                // guest in the old tee time
                        }
                     }
                  }
               }
            }

         }

         if (foundOakGst == true) {            // if guest found in new or old tee time

            sendEmail.sendOakmontEmail(parme, con);      // send an email to Caddie Master (below)
         }
      }

   }     // end of IF sendemail
 }       // end of Verify


 // *********************************************************
 //  Process cancel request from Proshop_slot (HTML) - 'Go Back'
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

   //
   //  Convert the values from string to int
   //
   try {
      date = Long.parseLong(sdate);
      time = Integer.parseInt(stime);
      fb = Integer.parseInt(sfb);
   }
   catch (NumberFormatException e) {
      // ignore error
   }

   //
   //  Clear the 'in_use' flag for this time slot in teecurr
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "UPDATE teecurr2 SET in_use = 0 WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);         // put the parm in pstmt1
      pstmt1.setInt(2, time);
      pstmt1.setInt(3, fb);
      pstmt1.setString(4, course);
      count = pstmt1.executeUpdate();      // execute the prepared stmt

      pstmt1.close();

   }
   catch (Exception ignore) {

   }

   //
   //  If Hazeltine National, then check for an associated tee time (w/e's and holidays)
   //
   if (club.equals( "hazeltine" )) {      // if Hazeltine National

      verifySlot.HclearInUse(date, time, fb, course, day, con);
   }

   //
   //  Prompt user to return to Proshop_sheet or Proshop_searchmem (index = 888)
   //
   //  These returns will pause for 1 second, then return automatically if meta supported, else user clicks on 'return'
   //
   if (index.equals( "888" )) {       // if originated from Proshop_main

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<Title>Proshop Tee Slot Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?search=yes\">");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
      out.println("<input type=\"hidden\" name=\"search\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");

   } else {

      if (!returnCourse.equals( "" )) {    // if multi course club, get course to return to (ALL?)
         course = returnCourse;
      }
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\"></link>");
      out.println("<Title>Proshop Tee Slot Page</Title>");
      out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Proshop_jump?index=" + index + "&course=" + course + "\">");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Return/Cancel Requested</H3>");
      out.println("<BR><BR>Thank you, the time slot has been returned to the system without changes.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_jump\" method=\"post\" target=\"_top\">");
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

 private void returnToSlot(PrintWriter out, parmSlot slotParms, int skip) {

   //
   //  Prompt user for return
   //
   out.println("<font size=\"2\">");
   out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"stime\" value=\"" + slotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotParms.suppressEmails + "\">");
   out.println("<input type=\"submit\" value=\"No - Return\" name=\"return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("<form action=\"/" +rev+ "/servlet/Proshop_slot\" method=\"post\" target=\"_top\">");
   out.println("<input type=\"hidden\" name=\"skip\" value=\"" +skip+ "\">");
   out.println("<input type=\"hidden\" name=\"player1\" value=\"" + slotParms.player1 + "\">");
   out.println("<input type=\"hidden\" name=\"player2\" value=\"" + slotParms.player2 + "\">");
   out.println("<input type=\"hidden\" name=\"player3\" value=\"" + slotParms.player3 + "\">");
   out.println("<input type=\"hidden\" name=\"player4\" value=\"" + slotParms.player4 + "\">");
   out.println("<input type=\"hidden\" name=\"player5\" value=\"" + slotParms.player5 + "\">");
   out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" + slotParms.p1cw + "\">");
   out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" + slotParms.p2cw + "\">");
   out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" + slotParms.p3cw + "\">");
   out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" + slotParms.p4cw + "\">");
   out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" + slotParms.p5cw + "\">");
   out.println("<input type=\"hidden\" name=\"p91\" value=\"" + slotParms.p91 + "\">");
   out.println("<input type=\"hidden\" name=\"p92\" value=\"" + slotParms.p92 + "\">");
   out.println("<input type=\"hidden\" name=\"p93\" value=\"" + slotParms.p93 + "\">");
   out.println("<input type=\"hidden\" name=\"p94\" value=\"" + slotParms.p94 + "\">");
   out.println("<input type=\"hidden\" name=\"p95\" value=\"" + slotParms.p95 + "\">");
   out.println("<input type=\"hidden\" name=\"show1\" value=\"" + slotParms.show1 + "\">");
   out.println("<input type=\"hidden\" name=\"show2\" value=\"" + slotParms.show2 + "\">");
   out.println("<input type=\"hidden\" name=\"show3\" value=\"" + slotParms.show3 + "\">");
   out.println("<input type=\"hidden\" name=\"show4\" value=\"" + slotParms.show4 + "\">");
   out.println("<input type=\"hidden\" name=\"show5\" value=\"" + slotParms.show5 + "\">");
   out.println("<input type=\"hidden\" name=\"date\" value=\"" + slotParms.date + "\">");
   out.println("<input type=\"hidden\" name=\"time\" value=\"" + slotParms.time + "\">");
   out.println("<input type=\"hidden\" name=\"mm\" value=\"" + slotParms.mm + "\">");
   out.println("<input type=\"hidden\" name=\"yy\" value=\"" + slotParms.yy + "\">");
   out.println("<input type=\"hidden\" name=\"index\" value=\"" + slotParms.ind + "\">");
   out.println("<input type=\"hidden\" name=\"p5\" value=\"" + slotParms.p5 + "\">");
   out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" + slotParms.p5rest + "\">");
   out.println("<input type=\"hidden\" name=\"course\" value=\"" + slotParms.course + "\">");
   out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" + slotParms.returnCourse + "\">");
   out.println("<input type=\"hidden\" name=\"day\" value=\"" + slotParms.day + "\">");
   out.println("<input type=\"hidden\" name=\"fb\" value=\"" + slotParms.fb + "\">");
   out.println("<input type=\"hidden\" name=\"notes\" value=\"" + slotParms.notes + "\">");
   out.println("<input type=\"hidden\" name=\"hide\" value=\"" + slotParms.hides + "\">");
   out.println("<input type=\"hidden\" name=\"jump\" value=\"" + slotParms.jump + "\">");
   out.println("<input type=\"hidden\" name=\"conf\" value=\"" + slotParms.conf + "\">");
   out.println("<input type=\"hidden\" name=\"orig_by\" value=\"" + slotParms.orig_by + "\">");
   out.println("<input type=\"hidden\" name=\"suppressEmails\" value=\"" + slotParms.suppressEmails + "\">");
     
   if (skip == 12) {
     
      out.println("<input type=\"hidden\" name=\"userg1\" value=\"" + slotParms.userg1 + "\">");
      out.println("<input type=\"hidden\" name=\"userg2\" value=\"" + slotParms.userg2 + "\">");
      out.println("<input type=\"hidden\" name=\"userg3\" value=\"" + slotParms.userg3 + "\">");
      out.println("<input type=\"hidden\" name=\"userg4\" value=\"" + slotParms.userg4 + "\">");
      out.println("<input type=\"hidden\" name=\"userg5\" value=\"" + slotParms.userg5 + "\">");
   }
   out.println("<input type=\"submit\" value=\"YES\" name=\"submitForm\"></form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception e1) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Unable to access the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>" + e1.getMessage());
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }


 // *********************************************************
 // Invalid data received - reject request
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Invalid Data - Reject"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H3>Invalid Data Received</H3><BR>");
   out.println("<BR><BR>Sorry, a name you entered is not valid.<BR>");
   out.println("Please check the names and try again.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
   return;
 }

}
