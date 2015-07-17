/***************************************************************************************     
 *   Login:  This servlet will process the initial login page's form request.
 *           It will process the 4 types of logins; support, admin, proshop and members.
 *
 *   called by:  index2.htm   
 *               server at init time
 *
 *   created: 11/20/2001   Bob P.
 *
 *
 *   last updated:       ******* keep this accurate *******
 *        
 *       10/09/06   Changes for bounced email address flagging
 *        9/12/06   Add support for Legendary Marketing - Harkers Hollow.
 *        9/12/06   Add support for Keating - Glen Oak CC.
 *        8/29/06   Add support for City Star - Colorado Springs CC.
 *        7/20/06   Add session vars for TLT system and for mobile users.
 *        7/19/06   Save timer value for X timer in init method (safety check for timers).
 *        7/12/06   Strip the leading zeros from the username for Jonas caller (for Mendakota).
 *        6/24/06   Improve some of the error messages returned to user, especially remote users.
 *        6/21/06   Added recordLoginStat method for new tracking of login statsistics
 *        6/15/06   Move call to scanTee on proshop user method so it runs in background.
 *        6/14/06   Scioto - custom days in adv for Spouse member types.
 *        6/01/06   In remoteUser, if primary=yes make sure the session gets built properly in case
 *                  there is only one member in the family - get the username and save it in the session.
 *                  Also, build and set the daysArray.
 *        5/02/06   Remove synchronized statements.
 *        5/02/06   Change calls to sessionLog to not include the pw if successful.
 *        5/01/06   Add support for Jay Van Vark caller (web site provider for Rancho Bernardo).
 *        4/25/06   Add support for Winding Oak caller (web site provider for Wayzata).
 *        4/10/06   Add support for Lightedge - Davenport CC.
 *        3/08/06   Add 2 new fields to the proshop session for member classes (mtypeOpt and mshipOpt).
 *        3/07/06   Oswego Lake (ZSmart) - trim the username to a max length of 15 chars (was 10).
 *        3/06/06   Change calls to sessionLog to include the connection.
 *        3/01/06   Strip the leading zeros from the memNum for CSG caller (for Hurstbourne).
 *        2/17/06   Add support for LogiSoft - Locust Hill CC.
 *        2/07/06   Add webid field to member2b for web site interface.  If web site interface is added
 *                  after our site is running, they can provide us with their member ids and we map them.
 *       11/09/05   Add support for MeritSoft (BuzWeb) - Providence CC.
 *       10/18/05   Add support for Cherry Hills caller (they do their own).
 *       10/06/05   Add support for Nakoma caller (web site provider for Nakoma - they do their own).
 *        9/29/05   Add support for Sedona Management Group caller (web site provider for Fairwood).
 *        9/22/05   Add a login log to track all logins.
 *        9/10/05   Medinah - allow Social Regular and Social Reg Probationary members to login.
 *        9/07/05   Oswego Lake (ZSmart) - trim the username to a max length of 10 chars (to match limit of password).
 *        9/02/05   Validate member email addresses when they login.  Post warning if invalid.
 *        7/07/05   Add support for email parms from MFirst.
 *        6/16/05   Add support for Grapevine caller (web site provider for Brooklawn).
 *        6/13/05   Add support for Flexscape caller (web site provider for Sciotto & Bishops Bay).
 *        6/13/05   Add support for ZSmart Web Marketing caller (web site provider for Oswego Lake).
 *        5/21/05   Custom for Medinah CC - If ARR member, change days in adv to 30 for all days of week.
 *        5/20/05   Medinah - do not allow dependents or social members to login.
 *        5/01/05   Add counters for logins - in SystemUtils.
 *        4/26/05   Add mtype to parms saved in the session for members.
 *        4/18/05   Add sales credentials and login, change support pw.
 *        4/10/05   Wichita - change web site providers from Gardner to JCook.
 *        4/07/05   Add mnum=yes for primary interface so web site can provide mNum instead of username.
 *        3/14/05   Add support for Gary Jonas Webs caller (web site provider for Interlachen & Meadowbrook).
 *        3/03/05   Add support for Gold Star Webs caller (web site provider for Rochester).
 *        2/21/05   Add V5 Changes page to welcome for members.
 *        2/14/05   Change SystemUtils.Connect to dbConn.Connect to allow 2 seperate servers.
 *        1/24/05   Ver 5 - change club2 to club5.
 *        1/21/05   Strip the leading zeros from the memNum for Web Sites 2000 caller.
 *        1/16/05   Add support for Web Sites 2000 caller (web site provider for Ironwood).
 *        1/06/05   Add support for Hidden Valley S/W caller (web site provider for North Ridge).
 *        1/04/05   Ver 5 - allow for more than one admin login.
 *       12/15/04   Add trace for Admin and Proshop Logins from Old Oaks.
 *       11/22/04   Add support for Primary-Only logins from CE.  Only the primary member resides
 *                  in their database so we must prompt for the family member.
 *       10/28/04   Add support for FELIX caller (web site provider for White Manor).
 *       10/25/04   Add support for MEDIACURRENT caller (web site provider for Piedmont Driving Club).
 *       10/11/04   Ver 5 - save lottery support indicator in the session for proshop users.
 *        9/22/04   Add new parm from clubs' index2.htm - zipcode= (for weather link).
 *        9/16/04   Ver 5 - change getClub and getDaysInAdv from SytemUtils to common.
 *        9/03/04   Save the club's POS Type for _slot processing.
 *        5/24/04   Save the member's mode of trans preference for Member_slot.
 *        5/19/04   Change member timeout to 5 minutes (from 8) and pro from 8 hrs to 4 hrs.
 *        5/05/04   Add INTRACLUB for Old Oaks and Bellerive.  Modify no-cache settings.
 *        5/04/04   Add 'cache-control' to responses to prevent session mangling.  Some proxy
 *                  servers were caching the pages and therefore interfering with the cookies.
 *        4/27/04   Add support for VELOTEL caller (web site provider for Hazeltine).
 *        4/22/04   RDP Add custom processing for Hazeltine Natl.  Allow women 14 days in adv.
 *        4/14/04   Change member session timeouts from 10 mins to 8 mins.
 *        4/08/04   Add support for GARDNER caller (web site provider for Wichita).
 *        4/07/04   Add support for PRIVATEGOLF caller (web site provider for Fort Collins).
 *        4/07/04   Add support for CLUBESSENTIAL caller (web site provider for Wakonda).
 *        2/09/04   Add support for NEMEX caller (web site provider for Forest Hills).
 *        1/21/04   Allow for 'days in adv' parms on a per membership and per day basis.
 *        1/08/04   Add DaysAdv array processing for members - establish and save
 *                  the days in advance values to make calendar building quicker.
 *       12/31/03   Add MONSON web site caller.
 *       11/18/03   Version 3 - add hotel user logins.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        9/29/02   Init the connection holder field in Support's session block so new
 *                  connection is made when logging into new club.
 *
 *       12/04/02   Enhancements for Version 2 of the software.
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
import com.foretees.common.DaysAdv;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.verifySlot;
import com.foretees.common.medinahCustom;
import com.foretees.common.FeedBack;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;


public class Login extends HttpServlet {

    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 String support = SystemUtils.support;    // class variables that never change
 String sales = SystemUtils.sales;
 String admin = SystemUtils.admin;
 String proshop = SystemUtils.proshop;
 String id = SystemUtils.id;
        
 //
 // The following password must be maintained here so we can
 //  login and initialize the databases when the site is installed.
 //
 String passwordSup = SystemUtils.passwordSup;       // password for support login...
 String passwordSales = SystemUtils.passwordSales;   // password for sales login...

 String omit = "";             // ommitted


 //*****************************************************
 // Perform initialization processing when server loads
 // this servlet for the first time.
 //*****************************************************
 //
 public void init()
         throws ServletException {

   //
   //  set a 2 minute system timer to check teecurr for inactive sessions
   //
   minTimer t2_timer = new minTimer();

   //
   //  set a 60 minute system timer to check teecurr for X's
   //
   min60Timer t4_timer = new min60Timer();

   //
   //   set timer to make sure we keep building new sheets daily
   //
   TeeTimer t_timer = new TeeTimer();  

   //
   //   set timer to send current tee sheet to Pro Shops (2 times per day)
   //
   //TsheetTimer t3_timer = new TsheetTimer();
     
   //
   //  Set the Roster Sync Timer for tonight (4:45 AM - check for MFirst Rosters)
   //
   TimerSync sync_timer = new TimerSync();

   //
   //  Reset the login counts for all clubs
   //
   initLogins();

   //
   //  Set the date/time when the 60 min timer (Xtimer) should expire by next (safety check to ensure timers keep running)
   //
   Calendar cal = new GregorianCalendar();   // get todays date

   cal.add(Calendar.MINUTE,90);              // roll ahead 90 minutes to give plenty of time

   long year = cal.get(Calendar.YEAR);
   long month = cal.get(Calendar.MONTH) +1;
   long day = cal.get(Calendar.DAY_OF_MONTH);
   long hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
   long min = cal.get(Calendar.MINUTE);

   //
   //  create date & time stamp value (yyyymmddhhmm) for compares
   //
   SystemUtils.min60Time = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // save date/time stamp

   try{

       //SystemUtils.purgeBouncedEmails();
   }
   catch (Exception ignore) {
   }
   
 }


 //*****************************************************
 //  Process doGet - request for help
 //*****************************************************
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   //
   //  Check if this is a request for help
   //
   if (req.getParameter("help") != null) {
     
      if (req.getParameter("browser") != null) {

         String browser = req.getParameter("browser");       // get browser selected
      
         if (browser.equals( "aol8" )) {
           
            helpAOL8(out);        // output help page
            return;
         }
         if (browser.equals( "aol7" )) {

            helpAOL7(out);        // output help page
            return;
         }
         if (browser.equals( "aol5" )) {

            helpAOL5(out);        // output help page
            return;
         }
         if (browser.equals( "ie6" )) {

            helpIE6(out);        // output help page
            return;
         }
         if (browser.equals( "ie5" )) {

            helpIE5(out);        // output help page
            return;
         }
         if (browser.equals( "ns7" )) {

            helpNS7(out);        // output help page
            return;
         }
         if (browser.equals( "mac5" )) {

            helpMAC5(out);        // output help page
            return;
         }
      }

      String club = req.getParameter("clubname");       // which club request came from

      //
      //  Output a page to provide help and a form to request the member's password
      //
      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");
        
      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Forgot your User Name (Login Id)?<br><br>");
      out.println("</b></p>");
      out.println("</td>");
      out.println("<td width=\"10\">&nbsp;</td>");
      out.println("<td valign=\"top\" height=\"230\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
      out.println("<td width=\"10\">&nbsp;</td>");

      out.println("<td align=\"left\" width=\"280\">");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("The User Name is normally either the <b>Local Number</b> assigned to you and used to enter scores on the Handicap System, ");
      out.println("or it is the <b>Member Number</b> that is used for billing purposes.");
      out.println("<br><br>");
      out.println("Your password should have been provided to you by the club.  You have the ability to change this once you are logged in.");
      out.println("<br><br>");
      out.println("Please contact your club's golf professionals for further assistance.");
      out.println("</font></p>");
      out.println("<A HREF=\"/" +club+ "/index2.htm\">Back to Login</A><br><br>");
      out.println("</td></tr>");
      out.println("</table>");

      out.println("<hr width=\"40%\">");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Forgot your password?<br><br>");
      out.println("Use the form on the right to have the system email your password. ");
      out.println("For this to work you must have already entered your email address in the system via the 'Settings' tab.");
      out.println("<br><br>");
      out.println("If you have not already entered your email address, please contact your club's golf professionals for assistance.");
      out.println("</b></p>");
      out.println("</td>");
      out.println("<td width=\"10\">&nbsp;</td>");
      out.println("<td valign=\"top\" height=\"220\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
      out.println("<td width=\"10\">&nbsp;</td>");

      out.println("<td align=\"left\" width=\"280\">");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("Enter your User Name (login id) and click on 'Send Password'. ");
      out.println("You will then receive an email containing your password.<br><br>");
      out.println("<form method=\"post\" action=\"/" +rev+ "/servlet/Login\">");
      out.println("<b>User Name:</b><br>");
      out.println("<input type=\"text\" name=\"user_name\" size=\"15\" maxlength=\"15\">");
      out.println("<input type=\"hidden\" name=\"clubname\" value=\"" +club+ "\">");
      out.println("<input type=\"hidden\" name=\"help\" value=\"yes\">");
      out.println("<br><br>");
      out.println("<input type=\"submit\" value=\"Send Password\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</font></p>");
      out.println("<A HREF=\"/" +club+ "/index2.htm\">Back to Login</A>");
      out.println("</td></tr>");
      out.println("</table>");

      out.println("<hr width=\"40%\">");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets?");
      out.println("<br><br>");
      out.println("Receiving an 'Access Error' after logging in?");
      out.println("</b></p>");
      out.println("</td>");
      out.println("<td width=\"10\">&nbsp;</td>");
      out.println("<td valign=\"top\" height=\"330\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
      out.println("<td width=\"10\">&nbsp;</td>");

      out.println("<td align=\"left\" width=\"280\">");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("There are several issues that can cause this problem:  ");
      out.println("<br><br>");
      out.println("<b>A.</b> Your browser's security setting is set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, click on your browser type: ");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=ie6\" target=\"_blank\">Microsoft Internet Explorer (IE) 6.x</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=ie5\" target=\"_blank\">Microsoft Internet Explorer (IE) 5.x</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol8\" target=\"_blank\">AOL 8.0 (or higher)</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol8\" target=\"_blank\">AOL 7.0 with IE 6.x</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol7\" target=\"_blank\">AOL 7.0 with IE 5.5</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol7\" target=\"_blank\">AOL 6.0</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=aol5\" target=\"_blank\">AOL 5.0</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=ns7\" target=\"_blank\">Netscape 7.x</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=ns7\" target=\"_blank\">Netscape 6.x</a>");
      out.println("<br><br>");
      out.println("<a href=\"/" +rev+ "/servlet/Login?help=yes&browser=mac5\" target=\"_blank\">MAC with IE 5.x</a>");
      out.println("<br><br>");
      out.println("If your browser is not listed above, then click on the 'Help' tab in your browser");
      out.println(" and search for 'cookie settings'.  You may also need to upgrade to a more current version");
      out.println(" (go to your browser provider's web site for information on upgrades).");
      out.println("<br><br><br>");
      out.println("<b>B.</b> It's possible that one or more of your internet files have been corrupted. ");
      out.println("These are files that your browser saves for quicker access to web sites that you frequent. ");
      out.println("To correct this problem, please try the following: ");
      out.println("<br><br>");
      out.println("For Microsoft Internet Explorer 6 <br><br>");
      out.println(" 1.  click on 'Tools'<br>");
      out.println(" 2.  select 'Internet Options'<br>");
      out.println(" 3.  click on the 'General' tab<br>");
      out.println(" 4.  click on the 'Delete Cookies' button, then select ‘Ok'<br>");
      out.println(" 5.  click on the 'Delete Files' button<br>");
      out.println(" 6.  select the option to 'Delete all offline content' and then click on 'Ok'<br>");
      out.println(" 7.  click on 'Ok' at the bottom of the Internet Options box<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("<br>");
      out.println("For AOL (from AOL browser prior to logging in)<br><br>");
      out.println(" 1.  click on 'Settings' and then 'Preferences'<br>");
      out.println(" 2.  select 'Internet Properties'<br>");
      out.println(" 3.  click on the 'General' tab<br>");
      out.println(" 4.  click on the 'Delete Cookies' button<br>");
      out.println(" 5.  click on the 'Delete Files' button<br>");
      out.println(" 6.  select the option to 'Delete all offline content' and then click on 'Ok'<br>");
      out.println(" 7.  click on 'Ok' at the bottom of the Internet Properties box<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("<br><br>");
      out.println("<b>C.</b> You may have a Firewall installed in your computer or network. ");
      out.println("If so, check the firewall settings to make sure that it is not blocking the use of cookies. ");
      out.println("<br><br>");
      out.println("<b>D.</b> If you are using Micorsoft's Internet Explorer you may need to update it. ");
      out.println("Microsoft has recently resolved some issues involving their privacy settings. ");
      out.println("You can update your browser free of charge at <A HREF=\"http://www.microsoft.com\">microsoft.com</A>. ");
      out.println("Select 'Downloads' or 'Windows Update' under <b>'Resources'</b> on the left side of the page (this may take several minutes).");
      out.println("</font></p>");
      out.println("<A HREF=\"/" +club+ "/index2.htm\">Back to Login</A><br><br>");
      out.println("</td></tr>");
      out.println("</table>");

      out.println("<hr width=\"40%\">");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"280\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Have other problems, want to contact ForeTees?");
      out.println("</b></p>");
      out.println("</td>");
      out.println("<td width=\"10\">&nbsp;</td>");
      out.println("<td valign=\"top\" height=\"300\" bgcolor=\"#000000\" width=\"1\"></td>");   // vert bar
      out.println("<td width=\"10\">&nbsp;</td>");

      out.println("<td align=\"left\" width=\"280\">");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("We welcome all correspondence. ");
      out.println("Please let us know if you are having problems with ForeTees, or have any suggestions ");
      out.println("on how we might improve the system.");
      out.println("<br><br>If reporting a problem with the system, please include your name, club name and a detailed description");
      out.println("of the problem.");
      out.println("<br><br>Please DO NOT send us emails regarding your tee times.  Contact your golf shop for tee time related issues.");
      out.println("<br><br>");
      out.println("To contact ForeTees, please send an email to:<br><br>");
      out.println(" <a href=\"mailto:support@foretees.com\">support@foretees.com</a>");
      out.println("<br><br>");
      out.println("If you would like to see ForeTees at another club, please send an email to:<br><br>");
      out.println(" <a href=\"mailto:sales@foretees.com\">sales@foretees.com</a><br>");
      out.println("</font></p>");
      out.println("<br><A HREF=\"/" +club+ "/index2.htm\">Back to Login</A>");
      out.println("</td></tr>");
      out.println("</table>");

      out.println("</CENTER></BODY></HTML>");
      out.close();

   } else {   

      doPost(req, resp);      // call doPost processing
   }
 }


 //*****************************************************
 // Perform doPost processing - someone is logging in
 //*****************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   //
   //  Set a P3P Compact Policy Statement in the HTTP Header.
   //  This informs the client of our intended use of the persistent cookie
   //  used to manage this session.
   //
   //  Definitions:
   //
   //      ALL (access=ALL):         We will provide access to ALL information collected.
   //      NOI (access=):            Web site does not collect identified data
   //      DSP (disputes=);          We will settle any disputes.
   //      COR (remedies=):          We will 'correct' any disputes
   //      NID (non-identifiable=):  This session cookie does not collect data and cannot identify the individual person  
   //      CURa (purpose=):          The information is used to complete the activity of the service (N/A if NID)          
   //      OUR (recipient=)          Only our service will process information received in cookie (N/A if NID)
   //      STP (retention=)          Information is retained for the 'stated purpose' (N/A if NID)
   //      PUR (categories=)         We may use the informaiton used to purchase a product or service (N/A if NID)
   //
//   resp.setHeader("P3P","CP=\"ALL DSP COR NID CURa OUR STP PUR\"");       // old version
   resp.setHeader("P3P","CP=\"NOI DSP COR NID\"");
     
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");                                      // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);    // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
     
   Connection con = null;                 // init DB objects
   ResultSet rs = null;

   //
   // Get the username and password entered.........  
   //
   String username = "";
   String userpw = "";
   String club = "";
   String errMsg = "";

   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");       // which club login came from
   }
   if (req.getParameter("user_name") != null) {

      username = req.getParameter("user_name");
   }

   //
   //   Check if user came from Club's Web Site provider (AMO or MembersFirst, etc.)
   //
   if (req.getParameter("caller") != null) {

      remoteUser(req, out, username, club);  // go process 'remote' user......
      return;
   }

   //
   //   Check if user is asking for password help
   //
   if (req.getParameter("help") != null) {

      userHelp(req, out, username, club);  // go process 'help' request
      return;
   }

   //
   //  normal foretees user
   //
   if (req.getParameter("password") != null) {

      userpw = req.getParameter("password");
   }
      
   //
   // Make sure both were entered.......
   //
   if ((username.equals( "" )) || (userpw.equals( "" ))) {
        
     errMsg = "Username or Password not provided.";

     invalidLogin(errMsg, req, out, con);   // process invalid login information.....
     return;
   }
       
   //
   // Make sure there were no spaces added
   //
   if ((username.startsWith( " " )) || (username.endsWith( " " ))) {

     errMsg = "Username or Password contains a space.";

     invalidLogin(errMsg, req, out, con);   // process invalid login information.....
     return;
   }

   if (username.equals( support )) {

     supportUser(req, out, userpw, username, club);   // go process 'support' user......

     return;
   }

   if (username.startsWith( sales )) {

     salesUser(req, out, userpw, username, club);   // go process 'sales' user......

     return;
   }

   //
   //  Make sure the club requested is currently running this version of ForeTees.
   //  The user may need to refresh the login page so they pull up the new page.
   //
   try {
      con = dbConn.Connect(rev);       // get a connection for this version level

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT fullname FROM clubs WHERE clubname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, club);
      rs = pstmt.executeQuery();

      if (!rs.next()) {          // if club not found in this version

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Login Rejected</H2><BR>");
         out.println("<BR>Your club is not yet authorized to access ForeTees.");
         out.println("<BR>The site must be completeed before you can proceed.<BR>");
         out.println("<BR>Please try again later.  Thank you.<BR>");
         out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">Return</A>.");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         pstmt.close();           // close the stmt
         return;                  // exit
      }
      pstmt.close();              // close the stmt
      con.close();                // close the connection
   }
   catch (Exception exc) {
      // Error connecting to db....
      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      out.println("<BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   if (username.startsWith( admin )) {

     adminUser(req, out, userpw, username, club);   // go process 'admin' user ........
     return;
   }

   if (username.startsWith( proshop )) {

     proshopUser(req, resp, out, userpw, username, club);  // go process 'proshop' user......
     return;

   } else {

     memberUser(req, out, username, userpw, club);   // go process 'member' user.......
   }
 }

 // *********************************************************
 // Process user = SUPPORT...(DB's may not exist yet!!!).
 // *********************************************************

 private void supportUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if (pw.equals( passwordSup )) {

     HttpSession session = req.getSession(true);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (30 mins)
     session.setMaxInactiveInterval(30*60);

     out.println("<HTML><HEAD><Title>Support Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/support_main.htm\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Support!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/support_main.htm\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</input></form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {
     
     String errMsg = "Invalid Password.";

     invalidLogin(errMsg, req, out, null);   // process invalid login information.....
   }
 }   

 // *********************************************************
 // Process user = Sales
 // *********************************************************

 private void salesUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   if (pw.equals( passwordSales )) {

     HttpSession session = req.getSession(true);  // Create a session object

     ConnHolder holder = null;                   // no con yet, get new one when needed

     session.setAttribute("connect", holder);    // clear connection holder so new one is allocated
     session.setAttribute("user", user);         // save username
     session.setAttribute("club", club);         // save club name
     session.setAttribute("caller", "none");     // save caller's name
     // set inactivity timer for this session (30 mins)
     session.setMaxInactiveInterval(30*60);

     out.println("<HTML><HEAD><Title>Sales Login Page</Title>");
     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/sales_main.htm\">");
     out.println("</HEAD>");
     out.println("<BODY><CENTER><p>&nbsp;</p><H2>Login Accepted</H2><BR>");
     out.println("<p>&nbsp;</p>");
     out.println("<BR>Welcome Sales!");
     out.println("<br><br><font size=\"2\">");
     out.println("<form method=\"get\" action=\"/" +rev+ "/sales_main.htm\">");
     out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
     out.println("</input></form></font>");
     out.println("</CENTER></BODY></HTML>");
     out.close();

   } else {
     String errMsg = "Invalid Password.";

     invalidLogin(errMsg, req, out, null);   // process invalid login information.....
   }
 }

 // *********************************************************
 // Process user = ADMIN....
 // *********************************************************

 private void adminUser(HttpServletRequest req, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   String errorMsg = "";

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //  Get the remote host id (for tracing client)
   //
   String hostip = req.getRemoteHost();

   //
   // Check password entered against password in DB.........
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password FROM login2 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (pw.equals( rs.getString("password") )) {

            //
            //  Count the number of users logged in
            //
            countLogin("admin", con);

            // new stats logging routine
            recordLoginStat(4);
               
            //
            //  Trace all login attempts
            //
            errorMsg = "Admin Login Successful";
            SystemUtils.sessionLog(errorMsg, user, "", club, omit, con);        // log it - no pw

            // Save the connection in the session block for later use.......
            
            HttpSession session = req.getSession(true);  // Create a session object
            
            ConnHolder holder = new ConnHolder(con);     // create a new holder from ConnHolder class
            
            //
            //  Get TLT indicator
            //
            int tlt = (getTLT(con)) ? 1 : 0;
            
            session.setAttribute("connect", holder);    // save DB connection holder
            session.setAttribute("user", user);         // save username
            session.setAttribute("club", club);         // save club name
            session.setAttribute("caller", "none");     // save caller's name
            session.setAttribute("tlt", tlt);           // timeless tees indicator
            // set inactivity timer for this session (1 hr)
            session.setMaxInactiveInterval(60*60);
            
            out.println("<HTML><HEAD><Title>Admin Login Page</Title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/admin_welcome.htm\">");
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<p>&nbsp;</p>");
            out.println("<BR><BR><H2>Login Accepted</H2><BR>");
            out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
            out.println("Welcome <b>System Administrator");
            out.println("</b>");
            out.println("</td></tr></table><br>");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/admin_welcome.htm\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

         } else {

            String errMsg = "Invalid Password.";

            invalidLogin(errMsg, req, out, con);   // process invalid login information.....

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }
         }    // end of if pw matches
      }       // end of if username found
      pstmt2.close();
   }
   catch (SQLException exc) {
      
      Connerror(out, exc, con);
      return;
   } 
 }

 // *********************************************************
 // Process user = PROSHOP....
 // *********************************************************

 private void proshopUser(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String pw, String user, String club) {

   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;
   String zipcode = "";
   String errorMsg = "";
   String logMsg = "";

   int mobile = 0; // flag for mobile user

   // Load the JDBC Driver and connect to DB.........

   try {
      con = dbConn.Connect(club);         // get connection to this club's db
   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   String errMsg = "Invalid Password.";

   //
   //  Get the remote host id (for tracing client)
   //
   String hostip = req.getRemoteHost();

   //
   //  Trace all login attempts for Old Oaks
   //
/*
   if (club.equals( "oldoaks" )) {

      errorMsg = "Proshop Login Received - User/PW & Club = ";

      //
      //  save message in /" +rev+ "/error.txt
      //
      errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Host Id = " +hostip;   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
   }
*/

   errorMsg = "Proshop Login Rejected - User/PW & Club = ";

   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }
      
   //
   //  check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      mobile = (req.getParameter("mobile").equals("yes")) ? 1 : 0;
   }
   
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   //
   //  Get lottery support indicator for club (for menu processing)
   //
   String lottery = getLottery(con);

   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;   
   
   // Check password entered against password in DB.........
   try {
     
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT password FROM login2 WHERE username = ?");

      pstmt2.clearParameters();         // clear the parms
      pstmt2.setString(1, user);        // put the username field in statement
      rs = pstmt2.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         if (pw.equals( rs.getString(1) )) {

            //
            //  Count the number of users logged in
            //
            countLogin("pro", con);

            // new stats logging routine
            recordLoginStat(3);
            
            //
            //  Trace all login attempts
            //
            logMsg = "Pro Login Successful";
            SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

            // Save the connection in the session block for later use.......

            HttpSession session = req.getSession(true);  // Create a session object

            ConnHolder holder = new ConnHolder(con);  // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);    // save DB connection holder
            session.setAttribute("user", user);         // save username
            session.setAttribute("club", club);         // save club name
            session.setAttribute("caller", "none");     // save caller's name
            session.setAttribute("posType", posType);   // save club's POS Type
            session.setAttribute("zipcode", zipcode);   // save club's ZIP Code
            session.setAttribute("lottery", lottery);   // save club's lottery support indicator
            session.setAttribute("mtypeOpt", "ALL");    // init member classes for name list (Proshop_slot, etc.)
            session.setAttribute("mshipOpt", "ALL");
            session.setAttribute("mobile", mobile);     // mobile user
            session.setAttribute("tlt", tlt);           // timeless tees indicator
            session.setMaxInactiveInterval(4*60*60);    // set inactivity timer for this session (4 hrs)

            out.println("<html><head><title>Proshop Login Page</title>");
            out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" + rev + "/" + ((mobile == 1) ? "proshop_mobile_home.htm" : "proshop_welcome.htm") + "\">");
            out.println("</head>");
            out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
            out.println("<hr width=\"40%\">");
            out.println("<p>&nbsp;</p>");
            out.println("<br><br><h2>Login Accepted</h2><br>");
             out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\"><p>");
             out.println("Welcome <b>Proshop</b>");
             out.println("</p></td></tr></table><br>");
            out.println("<br><br><font size=\"2\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/proshop_welcome.htm\">");
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</center></body></html>");
            out.close();


            //
            //   Call scanTee to make sure we have current tee sheets (if it hasn't already run today)
            //
            Calendar cal = new GregorianCalendar();        // get todays date
            long yy = cal.get(Calendar.YEAR);
            long mm = cal.get(Calendar.MONTH) +1;
            long dd = cal.get(Calendar.DAY_OF_MONTH);

            long date = (yy * 10000) + (mm * 100) + dd;     // save it
              
            if (date != SystemUtils.scanDate) {             // if not already run today
              
               try {

                  resp.flushBuffer();                   // force the repsonse to complete

                  b = SystemUtils.scanTee(con, club);   // check the tee sheets

               }
               catch (Exception ignore) {
               }
            }

         } else {                    // pw does not match

            //
            //  save error message in /" +rev+ "/error.txt
            //
            errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid PW";   // build error msg
 //           SystemUtils.logError(errorMsg);                           // log it

            errMsg = "Invalid Password.";

            invalidLogin(errMsg, req, out, con);   // process invalid login information.....

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }
         }                 // end of if password matches
            
      } else {             // no match found in database

         //
         //  save error message in /" +rev+ "/error.txt
         //
         errorMsg = errorMsg + user + ", " + pw + ", " + club + "  Invalid Username";   // build error msg
//         SystemUtils.logError(errorMsg);                           // log it

         errMsg = "Invalid Username.";

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }         // end of if username found
        
      pstmt2.close();
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
   }
 }

 // *********************************************************
 // Process user = Club Member or Hotel User
 // *********************************************************

 private void memberUser(HttpServletRequest req,
                 PrintWriter out, String user, String pw, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;

   Member member = new Member();

   String errorMsg = "Member Login Rejected - User/PW & Club = ";
   String logMsg = "";
   String clubName = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String email = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String zipcode = "";
   String errMsg = "";

   int email_bounced = 0;
   int email2_bounced = 0;
     
   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   int hotel = 0;
   boolean error = false;           // init error indicator
   int mobile = 0;          // mobile user indicator
   
   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

     Connerror(out, exc, con);              // go process connection error......
     return;
   }

   if (con == null) {
     Connerror2(out);              // go process connection error......
     return;
   }

   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }

   //
   //  Check for mobile device flag
   //
   if (req.getParameter("mobile") != null) {

      mobile = (req.getParameter("mobile").equals("yes")) ? 1 : 0;
   }
   
   //
   //  Get club's POS Type for Proshop_slot processing
   //
   String posType = getPOS(con);

   //
   //  Get TLT indicator
   //
   int tlt = (getTLT(con)) ? 1 : 0;

   //
   //  Get the remote host id (for tracing client)
   //
   String hostip = req.getRemoteHost();
            
   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT password, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, email2, email_bounced, email2_bounced " +
         "FROM member2b WHERE username = ?");

      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         //
         // Check password entered against password in DB.........
         //
         if (pw.equalsIgnoreCase( rs.getString("password") )) {

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string

            // Get the member's mship type and member type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            // Get the member's email addresses

            email = rs.getString("email");       
            email2 = rs.getString("email2");
            email_bounced = rs.getInt("email_bounced");       
            email2_bounced = rs.getInt("email2_bounced");

            // Get the number of visits and update it...

            int count = rs.getInt("count");       // Get count
            count++;                              // bump counter..
              
            //  Get wc and last message displayed at login
            wc = rs.getString("wc");          // Get w/c pref
            String lastMessage = rs.getString("message");                            // message

            String message = "";                 // init message to display 

            if (lastMessage.equals( "" )) {      // if message not already displayed
              
               message = "msg001";               // set new message to display
               lastMessage = "msg001";           // set new last message displayed

//            } else {

//               if (lastMessage.equals( "msg001" )) {      // if message 1 was last 

//                  message = "msg002";               // set new message to display
//                  lastMessage = "msg002";           // set new last message displayed
//               }
            }

            PreparedStatement stmt = con.prepareStatement (
               "UPDATE member2b SET count = ?, message = ? WHERE username = ?");

            stmt.clearParameters();           // clear the parms
            stmt.setInt(1, count);            // put the new count in statement
            stmt.setString(2, lastMessage);   // put in the message displayed
            stmt.setString(3, user);          // username 
            stmt.executeUpdate();

            stmt.close();

            //
            //  Create an array for the 'days in advance' that members can make tee times
            //  (this will save a great deal of processing in Member_sheet)
            //
            daysArray = daysInAdv(daysArray, club, mship, user, con);

            if (daysArray == null) {

               error = true;             // indicate an error occurred
               //
               //  save error message in /" +rev+ "/error.txt
               //
               errorMsg = errorMsg + user + " " + pw + " " + club + " Unable to allocate daysArray!";   // build error msg
               SystemUtils.sessionLog(errorMsg, user, "", club, omit, con);                   // log it

               errMsg = "System Not Yet Available.";
            } 

            //
            //  If Medinah CC - do not allow certain members to login
            //
            if (club.equals( "medinahcc" ) && error == false) {
              
               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) { 
  
                  error = true;     // do not allow
  
                  errMsg = "Membership Class Not Allowed.";
               }
            }  
            
            if (error == false) {
              
               //
               //  Count the number of users logged in
               //
               countLogin("mem", con);

               // new stats logging routine
               recordLoginStat(1);
            
               //
               //  Trace all login attempts
               //
               logMsg = "Member Login Successful";
               SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

               // Save the connection in the session block for later use.......
               HttpSession session = req.getSession(true);     // Create a new session object

               ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

               session.setAttribute("connect", holder);      // save DB connection holder
               session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
               session.setAttribute("user", user);           // save username
               session.setAttribute("name", name);           // save members full name
               session.setAttribute("club", club);           // save club name
               session.setAttribute("caller", "none");       // save caller's name
               session.setAttribute("daysArray", daysArray); // save 'days in adv' object
               session.setAttribute("mship", mship);         // save member's mship type
               session.setAttribute("mtype", mtype);         // save member's mtype
               session.setAttribute("wc", wc);               // save member's walk/cart pref (for _slot)
               session.setAttribute("posType", posType);     // save club's POS Type
               session.setAttribute("zipcode", zipcode);     // save club's zipcode
               session.setAttribute("mobile", mobile);       // mobile user
               session.setAttribute("tlt", tlt);             // timeless tees indicator
               //
               // set inactivity timer for this session
               //  use 10 mins to prevent user from hanging a tee slot and connection too long
               //
               session.setMaxInactiveInterval(10*60);

               //
               //   Validate the email addresses
               //
               if (!email.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(email));

                  if (!feedback.isPositive()) {              // if error

                     emailErr = feedback.get(0);             // get error message
                  }
               }
               if (!email2.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(email2));

                  if (!feedback.isPositive()) {              // if error

                     email2Err = feedback.get(0);             // get error message
                  }
               }

                 
               //
               //   Output welcome page
               //
               out.println("<HTML><HEAD><Title>Member Login Page</Title>");
               if (count > 1 && !email.equals( "" ) && email_bounced == 0 && email2_bounced == 0) {
                  if (emailErr.equals( "" ) && email2Err.equals( "" )) {
                     if (message.equals( "" )) {      // if no message to display
                        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/member_welcome.htm\">");
                     } else {
                        out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_msg\">");
                     }
                  }
               }
               out.println("</HEAD>");
               out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
               out.println("<hr width=\"40%\">");
               out.println("<BR><H2>Login Accepted</H2><BR>");
               out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");
               out.println("<font size=\"3\">");
               out.println("<BR>Welcome <b>" + name );

               out.println("</b><BR><BR>");
               out.println("Please note that this session will terminate if inactive for more than 10 minutes.");
               
               if (count == 1) {

                  out.println("<font size=\"3\">");
                  out.println("<br><br><b>Notice:</b>  Since this is your first visit, we strongly recommend that you <b>change your password</b>.<br>");
                  out.println("To do this, select the 'Settings' tab from the navigation bar on the top of most pages.");
                  out.println("</font>");
               }
               out.println("<font size=\"3\">");
                 
               if (!emailErr.equals( "" ) || email_bounced == 1) {        // problem with email address?

                  if (email_bounced == 1) {

                     out.println("<br><br><b>Warning:</b>  We recently tried to send you an email at "+email+" and it bounced back to us.<br>" +
                             "We've had to temporarily disabled sending you any emails until you resolve this problem.");
                  } else {
                       
                     out.println("<br><br><b>Warning:</b>  Your email address (" +email+ ") is invalid.");
                     out.println("<BR>" +emailErr);
                  }
                  out.println("<BR><BR>To correct this, select the 'Settings' tab from the navigation bar on the top of most pages.");
               }
               
               if (!email2Err.equals( "" ) || email2_bounced == 1) {        // problem with 2nd email address?

                  if (email2_bounced == 1) {

                     out.println("<br><br><b>Warning:</b>  We recently tried to send you an email at "+email2+" and it bounced back to us.<br>" +
                             "We've had to temporarily disabled sending you any emails at this address until you resolve the problem.");
                  } else {
                       
                     out.println("<br><br><b>Warning:</b>  Your email address (" +email2+ ") is invalid.");
                     out.println("<BR>" +email2Err);
                  }
                  out.println("<BR><BR>To correct this, select the 'Settings' tab from the navigation bar on the top of most pages.");
               }
               
               
               if (email.equals( "" )) {        // if email address not provided yet

                  out.println("<br><br><b>Notice:</b> In order for us to send email notifcations when you make or change tee times, you must provide a current, ");
                  out.println("working email address.");
                  out.println("<br><br>");
                  out.println("To provide your email address, click on the <b>'Settings'</b> tab in the navigation bar on top of the next page.");
                  out.println("<br><br>");
                  out.println("Thank you!");
               }

               out.println("<br><br>");
               
               out.println("</font></td></tr></table>");
               
               out.println("<br><br>");
               if (message.equals( "" )) {      // if no message to display
                  out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
               } else {
                  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
               }
               out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
               out.println("</input></form></font>");
               out.println("</CENTER></BODY></HTML>");
               out.close();
            }

         } else {

            error = true; // indicate an error occurred

            errMsg = "Invalid Password.";
            
            //
            //  Log this failed login
            //
            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW (IP=" + hostip + ")";    // build error msg
              
         } // end of if pw matches
           
      } else {        // member username not found in member2b table

          
         // if this club supports hotel users, lets check the hotel3 table to
         // see if this username exists
          
         //
         //  Check if Hotels are supported for this club
         //
         PreparedStatement pstmth1 = con.prepareStatement (
            "SELECT clubName, hotel " +
            "FROM club5");

         pstmth1.clearParameters();         // clear the parms
         rs = pstmth1.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            clubName = rs.getString(1);       // Get club's name
            hotel = rs.getInt(2);             // Get hotel indicator
         }
         pstmth1.close();
         
         if (hotel > 0) {          // if hotels supported

            PreparedStatement pstmth2 = con.prepareStatement (
               "SELECT password, name_last, name_first, name_mi, message " +
               "FROM hotel3 WHERE username = ?");

            // Get user's pw if there is a matching user...

            pstmth2.clearParameters();         // clear the parms
            pstmth2.setString(1, user);        // put the username field in statement
            rs = pstmth2.executeQuery();       // execute the prepared stmt

            if (rs.next()) {

               //
               // Check password entered against password in DB.........
               //
               if (pw.equals( rs.getString(1) )) {

                  //
                  //  Count the number of users logged in
                  //
                  countLogin("hotel", con);

                  //
                  //  Trace all login attempts
                  //
                  logMsg = "Hotel Login Successful";
                  SystemUtils.sessionLog(logMsg, user, "", club, omit, con);         // log it - no pw

                  // Get the member's full name.......

                  StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

                  String mi = rs.getString(4);                                // middle initial
                  if (!mi.equals( omit )) {
                     mem_name.append(" ");
                     mem_name.append(mi);
                  }
                  mem_name.append(" " + rs.getString(2));                     // last name

                  String name = mem_name.toString();                          // convert to one string

                  // Save the connection in the session block for later use.......
                  HttpSession session = req.getSession(true);     // Create a new session object

                  ConnHolder holder = new ConnHolder(con);    // create a new holder from ConnHolder class

                  session.setAttribute("connect", holder);    // save DB connection holder
                  session.setAttribute("sess_id", id);        // set session id for validation ("foretees")
                  session.setAttribute("user", user);         // save username
                  session.setAttribute("name", name);         // save members full name
                  session.setAttribute("club", club);         // save club name
                  session.setAttribute("caller", "none");     // save caller's name
                  session.setAttribute("zipcode", zipcode);   // save club's zipcode
                  session.setAttribute("tlt", tlt);           // timeless tees indicator
                  
                  //
                  // set inactivity timer for this session (2 hrs)
                  //
                  session.setMaxInactiveInterval(2*60*60);

                  out.println("<HTML><HEAD><Title>Hotel Login Page</Title>");
                  out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/hotel_selmain.htm\">");
                  out.println("</HEAD>");
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<p>&nbsp;</p>");
                  out.println("<BR><H2>Login Accepted</H2><BR>");
                  out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

                  out.println("<BR>Welcome to the ForeTees Tee Time Reservation system<br>for " + clubName );

                  out.println("<BR><BR>");
                  out.println("</td></tr></table>");
                  out.println("<br><br><font size=\"2\">");
                  out.println("<form method=\"get\" action=\"/" +rev+ "/hotel_selmain.htm\">");
                  out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
                  out.println("</input></form></font>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();

               } else {                     // password does not match

                  error = true;             // indicate an error occurred

                  errMsg = "Invalid Password.";

                  //
                  //  save error message in /" +rev+ "/error.txt
                  //
                  errorMsg = errorMsg + user + " " + pw + " " + club + " invalid PW";    // build error msg
//                  SystemUtils.logError(errorMsg);                           // log it

               }     // end of if hotel pw matches

            } else {                     // member & hotel username not found

               error = true;             // indicate an error occurred

               errMsg = "Invalid Username.";

               //
               //  save error message in /" +rev+ "/error.txt
               //
               errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg

//               SystemUtils.logError(errorMsg);                           // log it
            }
            pstmth2.close();

         } else {                     // member username not found and hotels not supported

            error = true;             // indicate an error occurred

            errMsg = "Invalid Username.";

            //
            //  save error message in /" +rev+ "/error.txt
            //
            errorMsg = errorMsg + user + " " + pw + " " + club + " invalid user";   // build error msg
//            SystemUtils.logError(errorMsg);                           // log it

         }     // end of if hotels supported 
      }        // end of if member username found

      pstmt.close();

      if (error == true) {         // if login failed   

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....

         if (con != null) {
            try {
               con.close();       // Close the db connection........
            }
            catch (SQLException ignored) {
            }
         }
      }        // end of IF error
   }
   catch (SQLException exc) {

      Connerror(out, exc, con);
      return;
   }
 }


 // *********************************************************
 // Process user = remote web site....
 // *********************************************************

 private void remoteUser(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;
   PreparedStatement stmt = null;

   Member member = new Member();

   String lname = "";
   String username = "";
   String mship = "";
   String mtype = "";
   String wc = "";
   String zipcode = "";
   String primary = "";
   String mNumParm = "";
   String mNum = "";
   String oldemail1 = "";
   String oldemail2 = "";
   String email1 = "";
   String email2 = "";
   String emailErr = "";
   String email2Err = "";
   String logMsg = "";
   String mapping = "";
   String errMsg = "";

   DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'

   //
   //  Make sure the club requested is currently running this version of ForeTees.
   //  The user may need to refresh the login page so they pull up the new page.
   //
   try {
      con = dbConn.Connect(rev);       // get a connection for this version level

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT fullname FROM clubs WHERE clubname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, club);
      rs = pstmt.executeQuery();

      if (!rs.next()) {          // if club not found in this version

         out.println(SystemUtils.HeadTitle("Invalid Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p><p>&nbsp;</p>");
         out.println("<BR><H2>Access Rejected</H2><BR>");
         out.println("<BR>Your club (" +club+ ") is not yet authorized to access ForeTees.");
         out.println("<BR>The site must be completeed before you can proceed.<BR>");
         out.println("<BR>Please try again later.  Thank you.<BR>");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         pstmt.close();           // close the stmt
         return;                  // exit
      }
      pstmt.close();              // close the stmt
      con.close();                // close the connection
   }
   catch (Exception exc) {
      // Error connecting to db....
      errMsg = "Unable to Connect to Database.";
        
      invalidRemote(errMsg, req, out, con);
      return;
   }


   //
   //  Get the club's zipcode if passed (for weather link)
   //
   if (req.getParameter("zipcode") != null) {

      zipcode = req.getParameter("zipcode");
   }

   //
   //  Get the 'primary' parm if passed (used by web site to indicate the member is primary - we must prompt)
   //
   primary = "No";
   mNumParm = "No";
     
   if (req.getParameter("primary") != null) {

      primary = req.getParameter("primary");
   }

   if (req.getParameter("mnum") != null) {

      mNumParm = req.getParameter("mnum");
   }

   mapping = "No";

   if (req.getParameter("mapping") != null) {

      mapping = req.getParameter("mapping");      // get mapping parm - used to map member ids
   }

   //
   //  Get the caller's id
   //
   String caller = req.getParameter("caller");

   if (caller.equals( "MEMFIRST" )) {       // if 'MembersFirst'
     
      lname = req.getParameter("lname");    // get last name value that was passed
        
      username = getUser(user);             // go extract user (decrypt it)
  
      if (username.equals( "" )) {
        
         out.println(SystemUtils.HeadTitle("Connection Error - Login"));
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
         out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
         out.println("<BR>Exception: Username Not Received");
         out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
         out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
      }

      //
      //  Get email address(es) if supplied
      //
      if (req.getParameter("email1") != null) {

         email1 = req.getParameter("email1");
      }
      if (req.getParameter("email2") != null) {

         email2 = req.getParameter("email2");
      }
        
   } else {     // not MEMFIRST
      
      username = user;
   }

   //
   // if 'AMO', 'MembersFirst', 'PRIVATEGOLF', 'CLUBESSENTIAL', 'NEMEX', or 'VELOTEL' or -
   //
   //  Legendary Marketing = Harkers Hollow GC
   //  Joe Keating (joetechonline.com) = Glen Oak CC
   //  City Star = Colorado Springs CC
   //  Jay Van Vark = CC of Rancho Bernardo
   //  Winding Oak = Wayzata CC
   //  Lightedge =  Davenport CC
   //  Club Systems Group (CSG7463) =  Hurstbourne CC
   //  LogiSoft (LOGISOFT7482) =  Locust Hill CC
   //  MeritSoft (BUZWEB4937) =  Providence CC
   //  Cherry Hills (CHCC0475) = Cherry Hills CC
   //  Nakoma (NAKOMA3273) = Fairwood
   //  Sedona Management Group (SEDONA3973) = Fairwood
   //  Grapevine Technology (GRAPEVINE2947) = Brooklawn
   //  Flexscape (FLEXSCAPE4865) = Oswego Lake
   //  ZSmart (ZSMART3573) = Oswego Lake
   //  Jake Cook (JCOOK78439) = Wichita
   //  SLG Development (SLGDEV4673) = Pine Hills
   //  Gary Jonas (GJONAS74912) = Interlachen & Meadowbrook
   //  Gold Star Webs (GOLDSTAR) = Rochester CC
   //  Web Sites 2000 (WEBS2000) = Ironwood CC
   //  Hidden Valley = North Ridge
   //  FELIX = White Manor
   //  MediaCurrent = Piedmont Driving Club (Atlanta)
   //  Monson now = 'GCWH'
   //  Hooker = Southview CC
   //  Velotel = Hazeltine (Mark Josefson)
   //  Intraclub = Skokie, Bellerive (Paul Niebuhr 203-984-9887)
   //  AMO = North Oaks, Hillcrest (David Carnes)
   //
   if (caller.equals( "AMO" ) || caller.equals( "MEMFIRST" ) || caller.equals( "GCWH" ) ||
       caller.equals( "NEMEX" ) || caller.equals( "HOOKER" ) || caller.equals( "PRIVATEGOLF" ) ||
       caller.equals( "CLUBESSENTIAL" ) || caller.equals( "JCOOK78439" ) || caller.equals( "VELOTEL" ) ||
       caller.equals( "INTRACLUB" ) || caller.equals( "MEDIACURRENT" ) || caller.equals( "FELIX" ) ||
       caller.equals( "HIDDENVALLEY" ) || caller.equals( "WEBS2000" ) || caller.equals( "GOLDSTAR" ) ||
       caller.equals( "GJONAS74912" ) || caller.equals( "SLGDEV4673" ) || caller.equals( "ZSMART3573" ) ||
       caller.equals( "FLEXSCAPE4865" ) || caller.equals( "GRAPEVINE2947" ) || caller.equals( "SEDONA3973" ) ||
       caller.equals( "NAKOMA3273" ) || caller.equals( "CHCC0475" ) || caller.equals( "BUZWEB4937" ) ||
       caller.equals( "LOGISOFT7482" ) || caller.equals( "CSG7463" ) || caller.equals( "LIGHTEDGE" ) ||
       caller.equals( "WINDINGOAK" ) || caller.equals( "VANVARK2754" ) || caller.equals( "CITYSTAR3976" ) ||
       caller.equals( "KEATING385" ) || caller.equals( "LEGENDARY294" )) {

      //
      // Load the JDBC Driver and connect to DB.........
      //
      try {
         con = dbConn.Connect(club);          // get a connection
      }
      catch (Exception exc) {

        errMsg = "Unable to Connect to Database.";

        invalidRemote(errMsg, req, out, null);              // go process connection error......
        return;
      }

      //
      //  Get club's POS Type for Proshop_slot processing
      //
      String posType = getPOS(con);

      //
      //  Get TLT indicator
      //
      int tlt = (getTLT(con)) ? 1 : 0;
   
      //
      //  Strip leading zeros in username (member #) if came from Web Sites 2000 (Ironwood CC) or CSG (Hurstbourne)
      //
      if (caller.equals( "WEBS2000" ) || caller.equals( "CSG7463" )) {     // if caller is Web Sites 2000 or CSG

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero(username);      // strip them
         }
      }

      //
      //  Strip leading zeros in username (member #) if came from St. Albans CC
      //
      if (caller.equals( "INTRACLUB" ) && club.equals( "stalbans" )) {     // if St. Albans CC

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      //
      //  Strip leading zeros in username if came from Jonas for Mendakota
      //
      if (caller.equals( "GJONAS74912" ) && club.equals( "mendakota" )) {    

         if (username.startsWith( "0" )) {     // if leading zero

            username = remZero2(username);      // strip them
         }
      }

      //
      //  Convert username field for Bishops Bay (Flexscape)
      //
      if (caller.equals( "FLEXSCAPE4865" ) && club.equals( "bishopsbay" )) {     // if Bishops Bay CC

         username = convertFlex(username);      // convert from xxxx-00n to xxxxn
      }

      //
      //  Trim the username for Oswego Lake (ZSMART) - they cannot pass us the member number - they can
      //  only pass us their username, which is last name + first initial (i.e. pariseb).  We set this
      //  value in our password field and use it to locate the member number (for the primary interface).
      //
      //  Our password is defined as 15 chars max, so we must trim if more than 15.  This is a little risky
      //  because there might be more than one family with a last name that matches the first 15 chars.
      //
      if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

         username = trimZsmart(username);      // 15 chars at max
      }

      //
      // use a prepared statement to find username (string) in the DB..
      //
      try {

         String stmtString = "";
           
         if (mNumParm.equalsIgnoreCase( "yes" )) {  // username = mNum (or webid for oswego)

            //
            //  If Oswego Lake (ZSmart), then the mNum is actually a member id that we save in our password.
            //
            if (caller.equals( "ZSMART3573" ) && club.equals( "oswegolake" )) {     // if Oswego Lake CC

               stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2 " +
                            "FROM member2b WHERE password = ?";

            } else {

               stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2 " +
                            "FROM member2b WHERE memNum = ?";
            }

         } else {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2 " +
                            "FROM member2b WHERE webid = ?";

            } else {
              
               stmtString = "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2 " +
                            "FROM member2b WHERE username = ?";
            }
         }

         PreparedStatement pstmt = con.prepareStatement (stmtString);

         // Get user's pw if there is a matching user...

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, username);    // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         if (rs.next()) {

            if (mapping.equalsIgnoreCase( "yes" )) {    // map username to web id for match ?

               username = rs.getString("username");                           // get our username
            }
              
            if (mNumParm.equalsIgnoreCase( "yes" )) {     // if mNum was supplied in username

               username = rs.getString("username");                           // get our username
            }
              
            String lastName = rs.getString("name_last");                           // get last name

            // Get the member's full name.......

            StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));  // get first name

            String mi = rs.getString("name_mi");                                // middle initial
            if (!mi.equals( omit )) {
               mem_name.append(" ");
               mem_name.append(mi);
            }
            mem_name.append(" " + rs.getString("name_last"));                     // last name

            String name = mem_name.toString();                          // convert to one string

            //
            //  if MembersFirst caller, check if last name matches (only check first 4 letters)
            //
            if (caller.equals( "MEMFIRST" )) {

               lname = checkName(lname, lastName);         // verify that the last names match

               if (!lname.equals( "" )) {
                 
                  out.println(SystemUtils.HeadTitle("Connection Error - Login"));
                  out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
                  out.println("<hr width=\"40%\">");
                  out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
                  out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
                  out.println("<BR>Exception: Invalid Last Name Received " + lname);
                  out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
                  out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
                  out.println("</CENTER></BODY></HTML>");
                  out.close();
                  return;
               }
            }

            // Get the member's membership type

            mship = rs.getString("m_ship");       // Get mship type
            mtype = rs.getString("m_type");       // Get member type

            // Get the member's email addresses

            oldemail1 = rs.getString("email");
            oldemail2 = rs.getString("email2");

            // Get the number of visits and update it...

            int count = rs.getInt("count");         // Get count
//            boolean b = rs.wasNull();       // If null, change to zero
//            if (b) {
//               count = 0;
//            }
            count++;                        // bump counter..

            //  Get wc and last message displayed at login

            wc = rs.getString("wc");                            // w/c pref
            String lastMessage = rs.getString("message");       // message
            mNum = rs.getString("memNum");                      // member #

            String message = "";                 // init message to display

            if (lastMessage.equals( "" )) {      // if message not already displayed

               message = "msg001";               // set new message to display
               lastMessage = "msg001";           // set new last message displayed

//            } else {

//               if (lastMessage.equals( "msg001" )) {      // if message 1 was last

//                  message = "msg002";               // set new message to display
//                  lastMessage = "msg002";           // set new last message displayed
//               }
            }

            pstmt.close();

            //
            //  Create an array for the 'days in advance' that members can make tee times
            //  (this will save a great deal of processing in Member_sheet)
            //
            daysArray = daysInAdv(daysArray, club, mship, username, con);

            if (daysArray == null) {

               //
               //  save error message in /" +rev+ "/error.txt
               //
               String errorMsg = "Login Error: " + username + " " + club + " Unable to allocate daysArray!";   // build error msg
//               SystemUtils.logError(errorMsg);                           // log it

               errMsg = "System Not Yet Available For Access.";

               invalidRemote(errMsg, req, out, con);
               return;
            }

            //
            //  If Medinah CC - do not allow certain members to login
            //
            if (club.equals( "medinahcc" )) {

               if (mship.equals( "Social" ) || mship.startsWith( "Social Pro" ) || mtype.startsWith( "FM " ) || mtype.startsWith( "Fam Member" )) {

                  errMsg = "Membership Class Not Allowed.";

                  invalidRemote(errMsg, req, out, con);
                  return;
               }
            }

            //
            //  Trace all login attempts
            //
            logMsg = "Remote Login Successful";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            // Save the connection in the session block for later use.......
            HttpSession session = req.getSession(true);   // Create a session object

            ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

            session.setAttribute("connect", holder);      // save DB connection holder
            session.setAttribute("sess_id", id);          // set session id for validation ("foretees")
            session.setAttribute("user", username);       // save username
            session.setAttribute("name", name);           // save members full name
            session.setAttribute("club", club);           // save club name
            session.setAttribute("caller", caller);       // save caller's name
            session.setAttribute("daysArray", daysArray); // save 'days in adv' object
            session.setAttribute("mship", mship);         // save member's mship type
            session.setAttribute("mtype", mtype);         // save member's mtype
            session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
            session.setAttribute("posType", posType);     // save club's POS Type
            session.setAttribute("zipcode", zipcode);     // save club's zipcode
            session.setAttribute("tlt", tlt);             // timeless tees indicator
            
            //
            // set inactivity timer for this session
            //  use 10 mins to prevent user from hanging a tee slot too long
            //
            session.setMaxInactiveInterval(10*60);

            //
            //  If this is the primary member and primary=yes, then we must prompt the user to see which
            //  family member this is.  Member_msg will process the reply.
            //
            if (primary.equalsIgnoreCase( "yes" ) && !mNum.equals( "" )) {

               boolean primaryDone = promptPrimary(mNum, lastMessage, club, out, con);

               if (primaryDone == true) {   // if we prompted (if more than one member)

                  return;                   // reply handled by Member_msg
               }
            }

            //
            //  Check if email addresses should be updated
            //
            if (!email1.equals( "" )) {         // if email address provided by caller (MFirst)
              
               FeedBack feedback = (member.isEmailValid(email1));    // validate it

               if (!feedback.isPositive()) {    // if error
                 
                  email1 = "";                  // ignore it
                    
               } else {

                  if (email2.equals( "" ) || email2 == null) {   // if 2nd address NOT provided

                     email2 = oldemail2;           // then set it equal to current email2 so not over-written
                       
                  } else {

                     feedback = (member.isEmailValid(email2));    // validate it

                     if (!feedback.isPositive()) {    // if error

                        email2 = "";                  // ignore it
                     }
                  }
               }
            }
               
            //
            //  Update the member record if email address provided by caller, 
            //  and it's different then what is in the database 
            //  currently only MFirst is passing the email1&2 parameters with the login
            //  note:  the roster sync performs this functionality for all new setups
            //
            
            int tmp_updateType = 0;
            String tmp_sql = "UPDATE member2b SET count = ?, message = ?";
            
            if (!email1.equals( "" ) && !email1.equalsIgnoreCase(oldemail1)) {
                tmp_updateType++;
                tmp_sql += ", email = ?, email_bounced = 0,";
            }
            
            if (!email2.equals( "" ) && !email2.equalsIgnoreCase(oldemail2)) {
                tmp_updateType = tmp_updateType + 2;
                tmp_sql += ", email2 = ?, email2_bounced = 0,";
            }
            
            tmp_sql += " WHERE username = ?";
            
            stmt = con.prepareStatement ( tmp_sql );
            stmt.clearParameters();
            stmt.setInt(1, count);            // put the new count in statement
            stmt.setString(2, lastMessage);   // put in the message displayed
            
            switch (tmp_updateType) {
                
                case 0:
                    
                    stmt.setString(3, username);
                    break;
                    
                case 1:        // clear the parms
                    
                    stmt.setString(3, email1);        // set email address
                    stmt.setString(4, username); 
                    break;
                    
                case 2:        // clear the parms
                    
                    stmt.setString(3, email2);        // set email address
                    stmt.setString(4, username); 
                    break;
                    
                case 3:
                    
                    stmt.setString(3, email1);        // set email address
                    stmt.setString(4, email2);        // set email address 2
                    stmt.setString(5, username); 
                    break;
            }
               stmt.executeUpdate();    
               stmt.close();
               
               
            /*
            if (!email1.equals( "" )) {
                
               stmt = con.prepareStatement (
                  "UPDATE member2b SET email = ?, count = ?, message = ?, email2 = ? WHERE username = ?");

               stmt.clearParameters();           // clear the parms
               stmt.setString(1, email1);        // set email address
               stmt.setInt(2, count);            // put the new count in statement
               stmt.setString(3, lastMessage);   // put in the message displayed
               stmt.setString(4, email2);        // set email address 2
               stmt.setString(5, username);       
               stmt.executeUpdate();    

               stmt.close();

            } else {
                
                // no email changes, just update the rest of the info
               stmt = con.prepareStatement (
                  "UPDATE member2b SET count = ?, message = ? WHERE username = ?");

               stmt.clearParameters();           // clear the parms
               stmt.setInt(1, count);            // put the new count in statement
               stmt.setString(2, lastMessage);   // put in the message displayed
               stmt.setString(3, username);         
               stmt.executeUpdate();   

               stmt.close();
            }
            */
                       
            if (!caller.equals( "MEMFIRST" )) {     // if not MFirst

               //
               //   Validate the email addresses
               //
               if (!oldemail1.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail1));

                  if (!feedback.isPositive()) {              // if error

                     emailErr = feedback.get(0);             // get error message
                  }
               }
               if (!oldemail2.equals( "" )) {                   // if specified

                  FeedBack feedback = (member.isEmailValid(oldemail2));

                  if (!feedback.isPositive()) {              // if error

                     email2Err = feedback.get(0);             // get error message
                  }
               }
            }

            //
            //  Count the number of users logged in
            //
            countLogin("mem", con);

            // new stats logging routine
            recordLoginStat(2);
               
            //
            //  Check if this club was upgraded from V4 to V5.  If so, offer a list of changes.
            //
//            boolean upgrade = checkUpgrade(con);


            //
            //  Output the response and route to system
            //
            out.println("<HTML><HEAD><Title>Member Login Page</Title>");

            if (!oldemail1.equals( "" ) || caller.equals( "MEMFIRST" )) {

               if (emailErr.equals( "" ) && email2Err.equals( "" )) {

                  if (message.equals( "" )) {      // if no message to display

                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" + rev + "/member_welcome.htm\">");
                  } else {
                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/servlet/Member_msg\">");
                  }
               }
            }
            out.println("</HEAD>");
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H2>Member Access Accepted</H2><BR>");
            out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

            out.println("<BR>Welcome <b>" + name );

            out.println("</b><BR><BR>");
            out.println("Please note that this session will terminate if inactive for more than 10 minutes.<BR><BR>");
            out.println("</td></tr></table>");

            out.println("<font size=\"3\">");

            if (!emailErr.equals( "" )) {        // problem with email address?

               out.println("<br><br><b>Warning:</b>  Your email address (" +oldemail1+ ") is invalid.");
               out.println("<BR>" +emailErr);
               out.println("<BR><BR>To correct this, select the 'Settings' tab from the navigation bar on the top of most pages.");
               out.println("</font>");
            }
            if (!email2Err.equals( "" )) {        // problem with 2nd email address?

               out.println("<br><br><b>Warning:</b>  Your email address (" +oldemail2+ ") is invalid.");
               out.println("<BR>" +email2Err);
               out.println("<BR><BR>To correct this, select the 'Settings' tab from the navigation bar on the top of most pages.");
               out.println("</font>");
            }

            if (oldemail1.equals( "" ) && !caller.equals( "MEMFIRST" )) {

               out.println("<br><br><b>Notice:</b> In order for us to send email notifcations when you make or change tee times, you must provide a current, ");
               out.println("working email address.");
               out.println("<br><br>");
               out.println("To provide your email address, click on the <b>'Settings'</b> tab in the navigation bar on top of the next page.");
               out.println("<br><br>");
               out.println("Thank you!");
            }

            out.println("<br><br>");

            if (message.equals( "" )) {      // if no message to display

               out.println("<form method=\"get\" action=\"/" + rev + "/member_welcome.htm\">");
            } else {
               out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Member_msg\">");
            }
            out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form></font>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

         } else {                               // username not found

            out.println(SystemUtils.HeadTitle("Connection Error - Login"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H2>Invalid Credentials Received</H2><BR>");
            out.println("<BR>Sorry, some required information is either missing or invalid.<BR>");
            out.println("<BR>Exception: Invalid Username Received");
            out.println("<BR>User Id " +username+ " does not exist in the ForeTees roster.");
            out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
            out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();

            //
            //  Trace all login attempts
            //
            logMsg = "Remote Login Failed - Invalid User";
            SystemUtils.sessionLog(logMsg, username, omit, club, caller, con);            // log it

            //
            //  save error message in /" +rev+ "/error.txt
            //
            String errorMsg = "Remote Login Error: from: " +caller+ ", for: " +club+ ", user: " +username+ " Invalid User Name";   // build error msg
//            SystemUtils.logError(errorMsg);                           // log it

            if (con != null) {
               try {
                  con.close();       // Close the db connection........
               }
               catch (SQLException ignored) {
               }
            }

         }        // end of if username found
      }
      catch (SQLException exc) {

         errMsg = "Database Access Error.";

         invalidRemote(errMsg, req, out, con);
         return;
      }
   } else {    // not AMO or MemFirst

      errMsg = "Invalid Parameter Received - Web Site Id Not Allowed.";

      invalidRemote(errMsg, req, out, con);
   }
 }

 // *********************************************************
 //   Extract the username from user field (encrypted)
 // *********************************************************

 private String getUser(String user) {


   char[] ca = user.toCharArray();         // put user value in char array
   int x = 0;
   int x2 = 0;
   int i = 0;
   int i2 = 0;
   int d1 = 0;
   int d2 = 0;
   int d3 = 0;
   int d4 = 0;
   int d5 = 0;
   int d6 = 99;
   int d7 = 99;
   int d8 = 99;
   int d9 = 99;
   int d10 = 99;
   int f1 = 0;
   int f2 = 0;
   int f3 = 0;
   int f4 = 0;
   int f5 = 0;
   int f6 = 0;
   int f7 = 0;
   int f8 = 0;
   int f9 = 0;
   int f10 = 0;
   int f11 = 0;

   String sd4 = "";
   String sd5 = "";
   String sd6 = "";
   String sd7 = "";
   String sd8 = "";
   String sd9 = "";
   String sd10 = "";
     
   String sf5 = "";
   String sf6 = "";
   String sf7 = "";
   String sf8 = "";
   String sf9 = "";
   String sf10 = "";
   String sf11 = "";

   int length = user.length();

   user = "";                            // init user field

   if (length < 8 || length > 22) {
     
      return (user);                     // return null if invalid length
   }

   //
   //  user (member's local #, is encrypted - we must decrypt it)
   //
   //      X F 1 F 2 F 3 F 4 F 5 F, etc.
   //
   //          X = 10 - # of digits in local# (min of 3, max of 10)
   //          F = filler (10-((prev digit + 5)/2)) 
   //          1, 2, 3, etc. = local# digits
   //
   //   Isolate each digit -
   //
   char [] charx = new char [1];
   charx[0] = ca[0];
   String sx = new String (charx);       // get x value (# of digits)

   charx[0] = ca[1];
   String sf1 = new String (charx);       // get filler 

   charx[0] = ca[2];
   String sd1 = new String (charx);       // get local# digit #1

   charx[0] = ca[3];
   String sf2 = new String (charx);       // get filler

   charx[0] = ca[4];
   String sd2 = new String (charx);       // get local# digit #2

   charx[0] = ca[5];
   String sf3 = new String (charx);       // get filler

   charx[0] = ca[6];
   String sd3 = new String (charx);       // get local# digit #3

   charx[0] = ca[7];
   String sf4 = new String (charx);       // get filler

   if (length > 8) {

      charx[0] = ca[8];
      sd4 = new String (charx);       // get local# digit #4

      charx[0] = ca[9];
      sf5 = new String (charx);       // get filler
   }
   if (length > 10) {

      charx[0] = ca[10];
      sd5 = new String (charx);       // get local# digit #5

      charx[0] = ca[11];
      sf6 = new String (charx);       // get filler
   }
   if (length > 12) {

      charx[0] = ca[12];
      sd6 = new String (charx);       // get local# digit #6

      charx[0] = ca[13];
      sf7 = new String (charx);       // get filler
   }
   if (length > 14) {

      charx[0] = ca[14];
      sd7 = new String (charx);       // get local# digit #7

      charx[0] = ca[15];
      sf8 = new String (charx);       // get filler
   }
   if (length > 16) {

      charx[0] = ca[16];
      sd8 = new String (charx);       // get local# digit #8

      charx[0] = ca[17];
      sf9 = new String (charx);       // get filler
   }
   if (length > 18) {

      charx[0] = ca[18];
      sd9 = new String (charx);       // get local# digit #9

      charx[0] = ca[19];
      sf10 = new String (charx);       // get filler
   }
   if (length > 20) {

      charx[0] = ca[20];
      sd10 = new String (charx);       // get local# digit #10

      charx[0] = ca[21];
      sf11 = new String (charx);       // get filler
   }

   try {
      x = Integer.parseInt(sx);          // get int value of X
      f1 = Integer.parseInt(sf1);        // get filler values          
      f2 = Integer.parseInt(sf2);
      f3 = Integer.parseInt(sf3);
      f4 = Integer.parseInt(sf4);
      d1 = Integer.parseInt(sd1);          // get local# digits 
      d2 = Integer.parseInt(sd2);
      d3 = Integer.parseInt(sd3);

      if (length > 8) {
         f5 = Integer.parseInt(sf5);
         d4 = Integer.parseInt(sd4);
      }
      if (length > 10) {
         f6 = Integer.parseInt(sf6);
         d5 = Integer.parseInt(sd5);
      }
      if (length > 12) {
         d6 = Integer.parseInt(sd6);
      }
      if (length > 14) {
         d7 = Integer.parseInt(sd7);
      }
      if (length > 16) {
         d8 = Integer.parseInt(sd8);
      }
      if (length > 18) {
         d9 = Integer.parseInt(sd9);
      }
      if (length > 20) {
         d10 = Integer.parseInt(sd10);
      }

   }
   catch (NumberFormatException e) {
      return (user);                     // return null user if failed
   }

   x2 = x;                              // save x value
   x = 10 - x;                          // get actual # of digits in username
   i = i + 2;                           // bump to first digit

   char[] ca2 = new char [x];           // empty array to move x chars

   while (x > 0) {

      ca2[i2] = ca[i];                  // move digits into ca2

      i2++;
      i = i + 2;
      x = x - 1;
   }

   //
   //  Verify all digits received -- filler must equal (10 - ((prev digit + 5) / 2))
   //
   //   ****** just verify first 3 for now **********
   //
   x2 = 10 - ((x2 + 5)/2);
   d1 = 10 - ((d1 + 5)/2);
   d2 = 10 - ((d2 + 5)/2);
   d3 = 10 - ((d3 + 5)/2);
     
   if (x2 != f1) {
      return (user);                     // return null user if failed
   }
   if (d1 != f2) {
      return (user);                     // return null user if failed
   }
   if (d2 != f3) {
      return (user);                     // return null user if failed
   }
   if (d3 != f4) {
      return (user);                     // return null user if failed
   }

   return new String (ca2);             // return extracted user
 }


 // *********************************************************
 //   Verify that the last name received from MemFirst matches the last name in db
 //
 //      Only check up to 4 letters in case MemFirst's name does not
 //      match exactly what we have in our db.  Also, there could be
 //      some extraneous characters in last name.
 //
 // *********************************************************

 private String checkName(String lname, String lastName) {


   char[] ca1 = lname.toCharArray();         // put lname value in char array
   char[] ca2 = lastName.toCharArray();      // put lastName value in char array
   int recLength = lname.length(); 
   int ourLength = lastName.length();
   int move = 4;
   int i = 0;
   int i2 = 0;

   if (recLength > ourLength) {
     
      return (lname);              // error
   }
   if (recLength < 2) {

      return (lname);              // error
   }
   if (recLength < 4) {

      move = recLength;     
   }
   
   char[] rec = new char [move];           // empty arrays to move names
   char[] our = new char [move];         

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca1[i2] != ' ' && ca1[i2] != '_') {
         rec[i] = ca1[i2];                  // move letters into received name
         i++;
      }
      i2++;
   }

   i = 0;
   i2 = 0;

   while (i < move) {

      //  do not copy spaces or underscores
      if (ca2[i2] != ' ' && ca2[i2] != '_') {
         our[i] = ca2[i2];                  // move letters into our name
         i++;
      }
      i2++;
   }

   lname = new String (rec);       // get new lname value
   lastName = new String (our);    // get new lastName value

   if (lname.equalsIgnoreCase( lastName )) {
     
      lname = "";                  // return null = ok
   }

   return (lname);
 }


 // *********************************************************
 // Process prompt user request - prompt for member to process
 // *********************************************************

 private boolean promptPrimary(String mNum, String message, String club, PrintWriter out, Connection con) {


   PreparedStatement pstmt = null;
   ResultSet rs = null;

   boolean done = false;
   boolean addit = false;
     
   String user = "";
   String fname = "";
   String mi = "";
   String lname = "";
   String mtype = "";

   int count = 0;
     
  
   //
   // find all members with this member# to display a selection list
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT m_type " +
         "FROM member2b WHERE memNum = ?");

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, mNum);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      while (rs.next()) {

         mtype = rs.getString(1);

         if (club.equals( "cherryhills" )) {       // weed out juniors if Cherry Hills CC
           
            if (!mtype.startsWith( "Junior" )) {   // if not a junior

               count++;                            // count the number of members with this member#
            }

         } else {

            count++;                   // count the number of members with this member#
         }
      }

      pstmt.close();

      //
      //  If more than one member, then prompt for the name
      //
      if (count > 1) {

         //
         //  Output page to prompt for real user
         //
         out.println("<HTML><HEAD><Title>Member Login Page</Title>");
         out.println("</HEAD>");
         out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
         out.println("<hr width=\"40%\">");
         out.println("<p>&nbsp;</p>");
         out.println("<BR><H2>Member Identification Required</H2><BR>");
         out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"6\"><tr><td align=\"center\">");

         out.println("<BR>Welcome to ForeTees");
         out.println("<font size=\"2\">");
         out.println("<BR><BR>");
         out.println("Please select your name from the following list:<BR><BR>");
           
         pstmt = con.prepareStatement (
            "SELECT username, name_last, name_first, name_mi, m_type " +
            "FROM member2b WHERE memNum = ?");

         pstmt.clearParameters();         // clear the parms
         pstmt.setString(1, mNum);        // put the username field in statement
         rs = pstmt.executeQuery();       // execute the prepared stmt

         while (rs.next()) {

            user = rs.getString(1);                         
            lname = rs.getString(2);
            fname = rs.getString(3);
            mi = rs.getString(4);
            mtype = rs.getString(5);

            addit = true;                // default to ok (use boolean in case we add more checks here)
              
            //
            // weed out juniors if Cherry Hills CC
            //
            if (club.equals( "cherryhills" ) && mtype.startsWith( "Junior" )) {  

               addit = false;                             // do not add this member
            }

            if (addit == true) {

               //
               //  Build the name
               //
               StringBuffer mem_name = new StringBuffer(fname);  // get first name

               if (!mi.equals( "" )) {
                  mem_name.append(" " + mi);             // add mi
               }
               mem_name.append(" " + lname);             // add last name

               String name = mem_name.toString();        // convert to one string

               //
               //   Output a link with the member's name
               //
               out.println("<a href=\"/" +rev+ "/servlet/Member_msg?user=" +user+ "&name=" +name+ "&message=" +message+ "\" style=\"color:#336633\" alt=\"" +name+ "\">");
               out.println(name+ "</a><br>");
            }
         }

         pstmt.close();

         out.println("</font>");
         out.println("</td></tr></table><br>");
         out.println("</CENTER></BODY></HTML>");
         out.close();

         done = true;        // set return indicator

      }    // end of IF count 

   }
   catch (SQLException exc) {
   }
     
   return(done);
 }


 // *********************************************************
 // Process user Help request - get password
 // *********************************************************

 private void userHelp(HttpServletRequest req,
                 PrintWriter out, String user, String club) {

   Connection con = null;                  // init DB objects
   ResultSet rs = null;

   String password = "";
   String email = "";

   String errMsg = "Required parameters not provided.";

   //
   //  Verify parms
   //
   if (user.equals( "" ) || club.equals( "" )) {     // if user or club not provided

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      return;
   }

   //
   // Load the JDBC Driver and connect to DB.........
   //
   try {
      con = dbConn.Connect(club);          // get a connection
   }
   catch (Exception exc) {

     con = null;
     invalidLogin(errMsg, req, out, con);   // process invalid login information.....
     return;
   }

   //
   // use a prepared statement to find username (string) in the DB..
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
         "SELECT password, name_last, name_first, name_mi, email " +
         "FROM member2b WHERE username = ?");

      // Get user's pw if there is a matching user...

      pstmt.clearParameters();         // clear the parms
      pstmt.setString(1, user);        // put the username field in statement
      rs = pstmt.executeQuery();       // execute the prepared stmt

      if (rs.next()) {

         password = rs.getString(1);                                  // get password
         email = rs.getString(5);                                     // get email address
         String lastName = rs.getString(2);                           // get last name

         // Get the member's full name.......

         StringBuffer mem_name = new StringBuffer(rs.getString(3));  // get first name

         String mi = rs.getString(4);                                // middle initial
         if (!mi.equals( omit )) {
            mem_name.append(" ");
            mem_name.append(mi);
         }
         mem_name.append(" " + rs.getString(2));                     // last name

         String name = mem_name.toString();                          // convert to one string

         //
         //  If email address specified, send the user his/her password
         //
         if (!password.equals( "" ) && !email.equals( "" )) {     // if password & email provided

            //
            //  allocate a parm block to hold the email parms
            //
            parmEmail parme = new parmEmail();          // allocate an Email parm block

            //
            //  Set the values in the email parm block
            //
            parme.type = "password";         // type = tee time
            parme.name = name;
            parme.user = user;
            parme.password = password;
            parme.email = email;

            //
            //  Send the email
            //
            sendEmail.sendIt(parme, con);      // in common

            //
            //  Done - reply ok
            //
            out.println(SystemUtils.HeadTitle("Help Reply"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H2>Login Credentials Have Been Emailed</H2><BR>");
            out.println("<BR>Thank you " +name+ ". You should receive an email shortly.<BR>");
            out.println("<form method=\"get\" action=\"/" +club+ "/index2.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;

         } else {  // password or email address not found

            out.println(SystemUtils.HeadTitle("Help Reply"));
            out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><H2>Unable to Email Credentials</H2><BR>");
            out.println("<BR>Sorry " +name+ ". We are unable to email your password<BR>");
            out.println("because you have not provided a valid email address.<BR>");
            out.println("<BR>Please contact your club's golf professionals for assistance.<BR>");
            out.println("<form method=\"get\" action=\"/" +club+ "/index2.htm\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
            out.println("</input></form>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;

         }

      } else {    // no match on username

         invalidLogin(errMsg, req, out, con);   // process invalid login information.....
      }
   }
   catch (SQLException exc) {

      invalidLogin(errMsg, req, out, con);   // process invalid login information.....
   }
 }


 // ***************************************************************
 //  countLogin
 //
 //      Track the number of users logged in for each club.
 //
 // ***************************************************************

 public static void countLogin(String type, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   int count = 0;


   try {
       
       
      stmt = con.createStatement();
      stmt.executeUpdate("UPDATE club5 SET logins = logins + 1");
      
       /*
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT logins FROM club5");          // get # of users logged in

      if (rs.next()) {

         count = rs.getInt("logins");
      }
      stmt.close();
        
      count++;                          

      PreparedStatement pstmt = con.prepareStatement (
               "UPDATE club5 SET logins = ?");                  // set new count

      pstmt.clearParameters();          
      pstmt.setInt(1, count);        
      pstmt.executeUpdate();

      pstmt.close();
        **/

   }
   catch (Exception ignore) {
   }
     
   //
   //  Keep internal counts in SystemUtils - these will reset each time tomcat is bounced
   //
   if (type.equals( "pro" ) || type.equals( "mem" )) {      // skip admin and hotel users
     
      if (SystemUtils.startDate.equals( "" )) {             // if first login since bounce
        
         SystemUtils.startDate = String.valueOf( new java.util.Date() );   // set new date & time                  
      }
        
      //
      //  Get the hour of day (24 hr clock)
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int hour = cal.get(Calendar.HOUR_OF_DAY);     // 24 hr clock (0 - 23)

      //
      //  Increment the counter for this hour
      //
      if (type.equals( "pro" )) {   
      
         SystemUtils.loginCountsPro[hour]++;
      }

      if (type.equals( "mem" )) {

         SystemUtils.loginCountsMem[hour]++;
      }
   }
     
 }


 // ***************************************************************
 //  initLogins
 //
 //      Reset the number of users logged in for each club.
 //
 // ***************************************************************

 public static void initLogins() {


   Connection con = null;                 // init DB objects
   Connection con2 = null;
   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   //
   // Load the JDBC Driver and connect to DB
   //
   String club = "v5";

   try {

      con2 = dbConn.Connect(club);
   }
   catch (Exception exc) {
      return;
   }

   //
   //   Get each club's login count and total them
   //
   try {
      stmt2 = con2.createStatement();              // create a statement

      rs2 = stmt2.executeQuery("SELECT clubname FROM clubs ORDER BY clubname");

      while (rs2.next()) {

         club = rs2.getString(1);                 // get a club name

         con = dbConn.Connect(club);         // get a connection to this club's db

         stmt = con.createStatement();           // create a statement

         stmt.executeUpdate("UPDATE club5 SET logins = 0");          // reset login count

         stmt.close();

         con.close();                           // close the connection to the club db
      }                                         // do all clubs
      stmt2.close();
      con2.close();
   }
   catch (Exception ignore) {
   }

 }

 
 // ***************************************************************
 //  getTLT(Connection con)
 //
 //      Get the club's time-less tees support indicator.
 //
 // ***************************************************************

 private boolean getTLT(Connection con) {

   Statement stmt = null;
   ResultSet rs = null;
   int tlt = 0;

   try {
       
      stmt = con.createStatement();
      rs = stmt.executeQuery("SELECT no_reservations FROM club5");
      if (rs.next()) tlt = rs.getInt(1);
      stmt.close();
      rs = null;
   }
   catch (Exception ignore) {
   }
   
   return ((tlt == 1) ? true : false);
 }
 

 // ***************************************************************
 //  getLottery
 //
 //      Get the club's Lottery Support Indicator.
 //
 // ***************************************************************

 private String getLottery(Connection con) {

   Statement stmt = null;
   ResultSet rs = null;

   int lottery = 0;
   String lotteryS = "0";

   try {
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT lottery FROM club5");          // get lottery flag

      if (rs.next()) {

         lottery = rs.getInt("lottery");
      }
      stmt.close();

   }
   catch (Exception ignore) {
   }
     
   if (lottery > 0) {
     
      lotteryS = "1";        // return 'lottery supported' indicator
   }

   return(lotteryS);
 }


 // ***************************************************************
 //  getPOS
 //
 //      Get the club's POS Type.
 //
 // ***************************************************************

 private String getPOS(Connection con) {

   Statement stmt = null;
   ResultSet rs = null;

   String posType = "";

   try {
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT posType FROM club5");          // get pos type

      if (rs.next()) {

         posType = rs.getString("posType");
      }
      stmt.close();

   }
   catch (Exception ignore) {
   }

   return(posType);
 }


 // ***************************************************************
 // Process request to build the 'Days in Adv' array for members
 //
 //   This method will calculate the days in advance that the member
 //   is allowed to edit tee sheets.  Each day is represented in the
 //   daysArray (int array) where the value indicates:
 //          0 = No
 //          1 = Yes, normal tee sheet access
 //          2 = Yes, Lottery access only
 //   This array is used each time a calendar is to be
 //   built for the member.
 //
 // ***************************************************************

 public static DaysAdv daysInAdv(DaysAdv daysArray, String club, String mship, String user, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;
   ResultSet rs4 = null;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   String lottery1 = "";
   String lottery2 = "";
   String mtype = "";
   String msubtype = "";

   int index = 0;
   int lott = 0;
   int days = 0;
   int days1 = 0;               // days in advance that members can make tee times
   int days2 = 0;               //         one per day of week (Sun - Sat)
   int days3 = 0;
   int days4 = 0;
   int days5 = 0;
   int days6 = 0;
   int days7 = 0;
   int sdays = 0;
   int cal_time = 0;            // calendar time for compares
   int count = 0;               // init day counter
   int col = 0;                 // init column counter
   int d = 0;                   // 'days in advance' value for current day of week

   long date = 0;

   //
   //  Array to hold the 'Days in Advance' value for each day of the week
   //
   int [] advdays = new int [7];                        // 0=Sun, 6=Sat

   //
   //  init the array (1 entry per day, relative to today)
   //
   int max = daysArray.MAXDAYS;           // get max days in advance (length of array)

   for (index = 0; index < max; index++) {

      daysArray.days[index] = 0;
   }

   //
   // Get the Lottery Option, days in advance and time for advance from the club db
   //
   try {

      getClub.getParms(con, parm);        // get the club parms

   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      String errorMsg = "Error1 in Login.daysInAdv for club: " + club + ". Exception: " +e1.getMessage();
      SystemUtils.logError(errorMsg);                           // log it
      return (null);                   // return error
   }

   //
   //  use the member's mship type to determine which 'days in advance' parms to use
   //
   verifySlot.getDaysInAdv(con, parm, mship);        // get the days in adv data for this member

   days1 = parm.advdays1;     // get days in adv for this type
   days2 = parm.advdays2;     // Monday
   days3 = parm.advdays3;
   days4 = parm.advdays4;
   days5 = parm.advdays5;
   days6 = parm.advdays6;
   days7 = parm.advdays7;     // Saturday

   lott = parm.lottery;


/*
   //
   //  If Medinah check for ARR member - if so, change days in adv to 30 (normally 2)
   //
   if (club.equals( "medinahcc" )) {

      boolean arrmem = medinahCustom.checkARRmem(user, con, mship, mtype);

      if (arrmem == true) {

         days1 = 30;           // ARR mem - change to 30 days
         days2 = 30;
         days3 = 30;
         days4 = 30;
         days5 = 30;
         days6 = 30;
         days7 = 30;
      }
   }
*/

   //
   //   Scioto Custom - change the days in adv for Spouses - Sun, Mon, Thur, Fri, Sat = 2, Tue, Wed = 3
   //
   if (club.equals( "sciotocc" )) {

      //
      //  Get the member's mtype to determine if change is needed
      //
      try {
         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT m_type " +
            "FROM member2b WHERE username = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, user);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mtype = rs.getString("m_type");
         }
         pstmt1.close();
      }
      catch (Exception ignore) {
      }

      if (mtype.startsWith( "Spouse" )) {

         days1 = 2;          // Sun = 2 days in advance (starting at 7:30 AM)
         days2 = 2;          // Mon = 2 days in advance (starting at 7:30 AM)
         days3 = 3;          // Tue = 3 days in advance (starting at 7:30 AM)
         days4 = 3;          // Wed = 3 days in advance (starting at 7:30 AM)
         days5 = 2;          // Thu = 2 days in advance (starting at 7:30 AM)
         days6 = 2;          // Fri = 2 days in advance (starting at 7:30 AM)
         days7 = 2;          // Sat = 2 days in advance (starting at 7:30 AM)
      }
   }

   //
   //  If Hazeltine, check if days in adv should change
   //
   if (club.equals( "hazeltine" )) {
    
      //
      //  Get the member's mtype and sub-type to determine if change is needed
      //
      try {
         PreparedStatement pstmt1 = con.prepareStatement (
            "SELECT m_type, msub_type " +
            "FROM member2b WHERE username = ?");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, user);
         rs = pstmt1.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            mtype = rs.getString("m_type");
            msubtype = rs.getString("msub_type");
         }
         pstmt1.close();
      }
      catch (Exception ignore) {
      }

      //
      //  If a Female and sub-type is 'After Hours', '9 holer', or combo, then set Tuesdays to 14 days adv.
      //
      //    Time Limits are enforced in Member_sheet !!!!!!!!!!!
      //
      if ((mtype.equals("Adult Female")) && (msubtype.equals("After Hours") || msubtype.equals("9 Holer") ||
          msubtype.startsWith("AH-") || msubtype.equals("9/18 Holer"))) {

         days3 = 14;      // set 14 days in advance for Tuesdays (all 'After Hours' and 9-Holers)
      }

      if ((mtype.equals("Adult Female")) && (msubtype.equals("18 Holer") || msubtype.startsWith("AH-9/18") ||
          msubtype.startsWith("AH-18") || msubtype.equals("9/18 Holer"))) {

         days5 = 14;      // set 14 days in advance for Thursdays (all 18-Holers)
      }
   }

   //
   //  Get today's date and setup parms
   //
   Calendar cal = new GregorianCalendar();             // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_am_pm = cal.get(Calendar.AM_PM);            // current time
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);
   int day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

   cal_time = (cal_hourDay * 100) + cal_min;

   cal_time = SystemUtils.adjustTime(con, cal_time);   // adjust the time

   if (cal_time < 0) {          // if negative, then we went back or ahead one day

      cal_time = 0 - cal_time;        // convert back to positive value - ok for compare below

      if (cal_time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                       // get next day's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                      // get yesterday's date
         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
         day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
      }
   }

   month++;                            //  adjust month

   //
   //  if its earlier than the time specified for days in advance, do not allow the last day_in_advance
   //
   //  Must check this again when building the calendars!!!!!!!!!
   //
   if (parm.advtime1 > cal_time) {
     
      days1--;
   }
   if (parm.advtime2 > cal_time) {

      days2--;
   }
   if (parm.advtime3 > cal_time) {

      days3--;
   }
   if (parm.advtime4 > cal_time) {

      days4--;
   }
   if (parm.advtime5 > cal_time) {

      days5--;
   }
   if (parm.advtime6 > cal_time) {

      days6--;
   }
   if (parm.advtime7 > cal_time) {

      days7--;
   }

   //
   //  put the 'days in advance' values in an array to be used below
   //
   advdays[0] = days1;
   advdays[1] = days2;
   advdays[2] = days3;
   advdays[3] = days4;
   advdays[4] = days5;
   advdays[5] = days6;
   advdays[6] = days7;

   //
   //  Set value in daysArray for each day up to max
   //
   day_num--;                           // convert today's day_num to index (0 - 6)

   for (index = 0; index < max; index++) {

      days = advdays[day_num];             // get days in advance for day of the week

      day_num++;                           // bump to next day of week

      if (day_num > 6) {                   // if wrapped past end of week

         day_num = 0;
      }

      date = (year * 10000) + (month * 100) + day;     // create date (yyyymmdd) for this day

      //
      // roll cal ahead 1 day for next time thru here
      //
      cal.add(Calendar.DATE,1);                       // get next day's date
      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month++;    // adjust month

      //
      // check if this day can be accessed by members
      //
      //    0 = No, 1 = Yes, 2 = Yes for Lottery only
      //
      if (days >= index) {               // if ok for this day (use index since today is automatic)

         daysArray.days[index] = 1;        // set ok in array

      } else {

         //
         //  determine if a lottery is setup for this day, and if the signup is longer than 'd' days
         //
         if (lott != 0) {                 // if lottery supported by this club

            int found = 0;     // init skip switch

            //
            //  Look for any lotteries on this date (any course) - up to 3 of them for one day
            //
            try {
               PreparedStatement pstmt1 = con.prepareStatement (
                  "SELECT lottery " +
                  "FROM teecurr2 WHERE date = ? AND lottery != ''");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setLong(1, date);
               rs = pstmt1.executeQuery();      // execute the prepared stmt

               if (rs.next()) {

                  lottery1 = rs.getString(1);

                  PreparedStatement pstmt1b = con.prepareStatement (
                     "SELECT sdays " +
                     "FROM lottery3 WHERE name = ?");

                  pstmt1b.clearParameters();        // clear the parms
                  pstmt1b.setString(1, lottery1);
                  rs2 = pstmt1b.executeQuery();      // execute the prepared stmt

                  if (rs2.next()) {

                     sdays = rs2.getInt(1);    // get days in advance to start taking requests

                  }  // end of IF lottery days 1
                  pstmt1b.close();

                  if (sdays >= index) {       // if ok for this day

                     found = 1;                // indicate found

                  } else {         // check for another (different) lottery on this date

                     PreparedStatement pstmt2 = con.prepareStatement (
                        "SELECT lottery " +
                        "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ''");

                     pstmt2.clearParameters();        // clear the parms
                     pstmt2.setLong(1, date);
                     pstmt2.setString(2, lottery1);
                     rs3 = pstmt2.executeQuery();      // execute the prepared stmt

                     if (rs3.next()) {

                        lottery2 = rs3.getString(1);

                        PreparedStatement pstmt2b = con.prepareStatement (
                           "SELECT sdays " +
                           "FROM lottery3 WHERE name = ?");

                        pstmt2b.clearParameters();        // clear the parms
                        pstmt2b.setString(1, lottery2);
                        rs2 = pstmt2b.executeQuery();      // execute the prepared stmt

                        if (rs2.next()) {

                           sdays = rs2.getInt(1);    // get days in advance to start taking requests

                        }  // end of IF lottery days 1
                        pstmt2b.close();

                        if (sdays >= index) {       // if ok for this day

                           found = 1;                // indicate found

                        } else {         // check for another (different) lottery on this date

                           PreparedStatement pstmt3 = con.prepareStatement (
                              "SELECT lottery " +
                              "FROM teecurr2 WHERE date = ? AND lottery != ? AND lottery != ? AND lottery != ''");

                           pstmt3.clearParameters();        // clear the parms
                           pstmt3.setLong(1, date);
                           pstmt3.setString(2, lottery1);
                           pstmt3.setString(3, lottery2);
                           rs4 = pstmt3.executeQuery();      // execute the prepared stmt

                           if (rs4.next()) {

                              lottery2 = rs4.getString(1);

                              PreparedStatement pstmt3b = con.prepareStatement (
                                 "SELECT sdays " +
                                 "FROM lottery3 WHERE name = ?");

                              pstmt3b.clearParameters();        // clear the parms
                              pstmt3b.setString(1, lottery2);
                              rs2 = pstmt3b.executeQuery();      // execute the prepared stmt

                              if (rs2.next()) {

                                 sdays = rs2.getInt(1);    // get days in advance to start taking requests

                              }  // end of IF lottery days 1
                              pstmt3b.close();

                              if (sdays >= index) {       // if ok for this day

                                 found = 1;                // indicate found
                              }
                           }  // end of IF lottery 3
                           pstmt3.close();

                        }  // end of IF found

                     }  // end of IF lottery 2
                     pstmt2.close();

                  }  // end of IF found

               }  // end of IF lottery 1
               pstmt1.close();
            }
            catch (Exception e1) {

               //
               //  save error message in /" +rev+ "/error.txt
               //
               String errorMsg = "Error2 in Login.daysInAdv for club: " + club + ". Exception: " +e1.getMessage();
               SystemUtils.logError(errorMsg);                           // log it
               return (null);                   // return error
            }

            if (found != 0) {                   // if a lottery was found for this day

               daysArray.days[index] = 2;       // set ok for lottery in array
            }

         }        // end of IF lottery supported

      }          // end of IF days check

   }  // end of FOR max

   return (daysArray);
 }


 // *********************************************************
 //  Convert username from xxxx-00n to xxxxn
 // *********************************************************

 private final static String convertFlex( String s ) {


   String part1 = s;
   String part2 = "";
     
   int suf = 0;

   //
   //  parse the string to get the '-00n'
   //
   StringTokenizer tok = new StringTokenizer( s, "-" );

   if ( tok.countTokens() > 1 ) {

      part1 = tok.nextToken();
      part2 = tok.nextToken();

      suf = Integer.parseInt(part2);     // convert to int to get absolute number (i.e.  001 = 1)
   }

   part1 = part1 + suf;                  // combine (default = xxxx0)

   return (part1);

 } // end convertFlex


 // ********************************************************************************
 //  Trim the username if more than 15 characters - ZSmart Web - Oswego Lake CC
 // ********************************************************************************

 private final static String trimZsmart( String s ) {


   char[] ca = s.toCharArray();

   char[] ca2 = new char [15];      // new char array

   String user = s;
     
   if (ca.length > 15) {
     
      for (int i=0; i < 15; i++) {

         ca2[i] = ca[i];               // copy first 15 chars
      }

      user = new String (ca2);         // return trimmed down username
   }

   return (user);

 } // end trimZsmart


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero( String s ) {


      int memid = 0;
      String newS = "";

      //
      //  convert string to int to drop leading zeros
      //
      try {
         memid = Integer.parseInt(s);
      }
      catch (NumberFormatException e) {
         // ignore error
      }

      newS = String.valueOf( memid );      // convert back to string

      return new String (newS);

 } // end remZero


 // *********************************************************
 //  Remove leading zeros in member id string
 // *********************************************************

 private final static String remZero2( String s ) {


      int i = 0;
      int count = 0;
      String s2 = s;
      char[] ca = s.toCharArray();

      loop1:
      while (i < ca.length) {
        
         char letter = ca[i];
         if ( letter == '0' ) {        // if leading zero
            count++;                   // count them           
         } else {
            break loop1;               // else exit
         }
         i++;
      }
        
      if (count > 0) {
           
         char[] ca2 = new char [ca.length - count];      // new char array
           
         i = 0;
         while (count < ca.length) {

            char letter = ca[count];                     // set new string w/o zeros
            ca2[i] = letter;
            i++;
            count++;
         }           
         s2 = new String (ca2);
      } 
      return (s2);

 } // end remZero2


 // *********************************************************
 // Check if this club was upgraded from V4 to V5
 // *********************************************************

 private boolean checkUpgrade(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   boolean upgrade = false;
     
   try {
      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT multi FROM club2");   // does club2 exist in this system ?

      if (rs.next()) {

         upgrade = true;
      }
      stmt.close();

   }
   catch (Exception ignore) {
   }

   return(upgrade);
 }


 // *********************************************************
 // Connection error received - inform user to try again....
 // *********************************************************

 private void Connerror(PrintWriter out, Exception exc, Connection con) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Exception: "+ exc.getMessage());
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
 }

 private void Connerror2(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Connection Error - Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Connection Error2</H2><BR>");
   out.println("<BR>Sorry, we are unable to connect to the system database at this time.<BR>");
   out.println("<BR>Please <A HREF=\"javascript:history.back(1)\">try again</A> later.");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Invalid Login data received - inform user to try again....
 // *********************************************************

 private void invalidLogin(String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {

  
   String user = "";
   String club = "";
   String pw = "";
     
   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");    
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {
     
      String logMsg = "Login Failed - Invalid Login - Error: " +errMsg;
      SystemUtils.sessionLog(logMsg, user, pw, club, omit, con);                   // log it
   }


   out.println(SystemUtils.HeadTitle("Invalid Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<p>&nbsp;</p><p>&nbsp;</p>");
   out.println("<BR><H2>Login Rejected</H2><BR>");
   out.println("<BR>The login information you submitted was either missing or invalid.");
   out.println("<BR><BR>Error: " +errMsg);
   out.println("<BR><BR><BR>Please <A HREF=\"javascript:history.back(1)\">try again</A>.");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }

 // *********************************************************
 // Invalid call from remote user - reject
 // *********************************************************

 private void invalidRemote(String errMsg, HttpServletRequest req, PrintWriter out, Connection con) {


   String user = "";
   String club = "";
   String pw = "";
   String caller = "";

   //
   //  Get club name and user name provided
   //
   if (req.getParameter("clubname") != null) {

      club = req.getParameter("clubname");
   }
   if (req.getParameter("user_name") != null) {

      user = req.getParameter("user_name");
   }

   if (req.getParameter("password") != null) {

      pw = req.getParameter("password");
   }

   if (req.getParameter("caller") != null) {

      caller = req.getParameter("caller");
   }

   //
   //  Trace all login attempts
   //
   if (con != null) {

      String logMsg = "Login Failed - Invalid Remote - Error: " +errMsg;
      SystemUtils.sessionLog(logMsg, user, pw, club, caller, con);                   // log it
   }


   out.println(SystemUtils.HeadTitle("Invalid Login"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<p>&nbsp;</p><p>&nbsp;</p>");
   out.println("<BR><H2>Access Rejected</H2><BR>");
   out.println("<BR>Some information provided was either missing or invalid.");
   out.println("<BR><BR>Error: " +errMsg);
   out.println("<BR><BR>Contact your Web Site Administrator or Web Site Provider for assistance (provide this message).");
   out.println("<BR><BR><form><input type=\"button\" value=\"EXIT\" onClick='self.close();'></form>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpAOL8(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  From the AOL Toolbar, click on 'Settings'<br>");
      out.println(" 2.  select 'Preferences'<br>");
      out.println(" 3.  select 'Internet Properties (WWW)'<br>");
      out.println(" 4.  select the 'Privacy' tab<br>");
      out.println(" 5.  Adjust the slidebar so the setting is on 'Medium High'<br>");
      out.println(" 6.  select 'Advanced'<br>");
      out.println(" 7.  Deselect the 'Override automatic cookie handling button' <br>");
      out.println(" 8.  click on 'Ok' to exit<br>");
      out.println(" 9.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpAOL7(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  From the AOL Toolbar, click on 'Settings'<br>");
      out.println(" 2.  select 'Preferences'<br>");
      out.println(" 3.  select 'Internet Properties (WWW)'<br>");
      out.println(" 4.  select the 'Security' tab<br>");
      out.println(" 5.  select the 'Custom Level' tab<br>");
      out.println(" 6.  under 'Allow per-session cookies (not stored)' click on 'Enable'<br>");
      out.println(" 7.  click on 'Ok' to save settings<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpAOL5(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  Go to 'My AOL'<br>");
      out.println(" 2.  select 'WWW'<br>");
      out.println(" 3.  select the 'Security' tab<br>");
      out.println(" 4.  go to 'Custom Level'<br>");
      out.println(" 5.  scroll down to find Cookie<br>");
      out.println(" 6.  click on 'Enable'<br>");
      out.println(" 7.  select 'OK'<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpIE6(PrintWriter out) {
    
      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  click on 'Tools' on the menu bar<br>");
      out.println(" 2.  select 'Internet Options'<br>");
      out.println(" 3.  click on the 'Privacy' tab<br>");
      out.println(" 4.  Adjust the slidebar so the setting is on 'Medium High'<br>");
      out.println(" 5.  select 'Advanced'<br>");
      out.println(" 6.  Deselect 'override automatic cookie handling'<br>");
      out.println(" 7.  click on 'Ok' to save and exit<br>");
      out.println(" 8.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpIE5(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  click on 'Tools' on the menu bar<br>");
      out.println(" 2.  select 'Internet Options'<br>");
      out.println(" 3.  click on the 'Security' tab<br>");
      out.println(" 4.  select the 'Custom Level' tab<br>");
      out.println(" 5.  under 'allow per-session cookies (not stored)' click 'Enable'<br>");
      out.println(" 6.  click on 'Ok' to save and exit<br>");
      out.println(" 7.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpNS7(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  click on 'Edit' from the Toolbar<br>");
      out.println(" 2.  select 'Preferences'<br>");
      out.println(" 3.  click on the 'Privacy and Security' category; expand the list to show the subcategories<br>");
      out.println(" 4.  select 'Cookies'<br>");
      out.println(" 5.  Three options are displayed.<br>");
      out.println(" 6.  click on 'Enable cookies for the originating web site only'<br>");
      out.println(" 7.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }


 // *********************************************************
 // Help Instructions - New Window
 // *********************************************************
 private void helpMAC5(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("Login Help"));
      out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<br><br>");

      out.println("<table border=\"0\" align=\"center\">");
      out.println("<tr valign=\"top\"><td align=\"left\" width=\"50\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p>&nbsp;</p>");
      out.println("</td>");
      out.println("<td align=\"left\">");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println("<p><b>");
      out.println("Able to login, but cannot access the tee sheets. ");
      out.println("Receiving an 'Access Error' after logging in.");
      out.println("</b></p>");
      out.println("<p>");
      out.println("<font face=\"arial\" size=\"2\">");
      out.println(" Your browser's security setting might be set too high (blocks all cookies). ");
      out.println(" The ForeTees system requires the use of 'Session Cookies'. ");
      out.println(" These are safe cookies that are commonly used by authenticated sites to ensure ");
      out.println(" that only the person that logged in gains access to that site (ForeTees, in this case). ");
      out.println(" To check if this is your problem, do the following: ");
      out.println("<br><br>");
      out.println(" 1.  click on 'Edit' (or 'Explorer')<br>");
      out.println(" 2.  select 'Preferences'<br>");
      out.println(" 3.  under the Receiving Files option, select 'Cookies'<br>");
      out.println(" 4.  under the 'When receiving cookies:', select the desired level<br>");
      out.println(" 5.  click on 'Ok' to save and exit<br>");
      out.println(" 6.  try ForeTees again<br>");
      out.println("</font></p>");
      out.println("</td></tr>");
      out.println("</table>");
      out.println("<p align=\"center\">");
         out.println("<form>");
         out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close This Window \" onClick='self.close()' alt=\"Close\">");
         out.println("</form></p>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
 }

 
 // *********************************************************
 // Increment the login counts in the Vx login_stats table
 // *********************************************************
 private void recordLoginStat(int user_type_id) {
     
    Connection con = null;
    Statement stmt = null;
    
    int server_id = Common_Server.SERVER_ID;            
    
    try {
        
        con = dbConn.Connect("v5");
        stmt = con.createStatement();
        stmt.executeUpdate("INSERT INTO login_stats (entry_date, hour, node, user_type_id, login_count) VALUES (now(), DATE_FORMAT(now(), \"%H\"), \"" + server_id + "\", \"" + user_type_id + "\", 1) ON DUPLICATE KEY UPDATE login_count = login_count + 1");
        stmt.close();
        
    }
    catch (Exception exp) {
        // maybe have this log to the db?        
    }
    
    con = null;
    stmt = null;
    
 }
 
}
