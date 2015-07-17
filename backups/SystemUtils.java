/***************************************************************************************
 *   SYSTEMUTILS:  This servlet will provide some common utilites for other servlets.
 *
 *   utilities:  HeadTitle (creates a HTML header and title)
 *               Connect (gets a connection to the DB)
 *               getCon (gets a db connection)
 *               verifyMem (verify the user - prevent unauthorized access)
 *               verifyPro (verify the user - prevent unauthorized access)
 *               verifyAdmin (verify the user - prevent unauthorized access)
 *               verifyHotel (verify the user - prevent unauthorized access)
 *               buildTee (builds a tee sheet for one day into teecurr2 table)
 *               buildHTee (builds a custom tee sheet for Hazeltine National)
 *               moveTee (moves oldest teesheet from teecurr2 to teepast2)
 *               scanTee & teeTimer (scans teecurr2 for missing days and builds them if needed)
 *               buildDblTee (builds double tees into the tee sheets when a Double Tee has been added or changed)
 *               removeDblTee (removes double tees from the tee sheets when a Double Tee has been changed)
 *               filter (filters strings to replace special characters for HTML display)
 *               unfilter (changes special characters back for jump code)
 *               scanName (scans a name for special characters)
 *               updateTeecurr (scans block, event & restriction tables and updates teecurr2 if necessary)
 *               inactTimer (processes 2 minute system timer expiration - check teecurr2 for in-use entries)
 *               xTimer (processes 60 minute system timer expiration - check teecurr2 for X's)
 *               tsheetTimer (sends current tee sheet to all Pro Shops 2 times per day)
 *               getLottId (get the next sequential lottery id from club table)
 *               processLott (process a lottery when inactTimer detects its time)
 *               moveReqs (move lottery requests from lreqs to teecurr after times have been assigned)
 *               adjustTime (adjust the time for club's time zone)
 *               adjustTimeBack (adjust the time Back for club's time zone - opposite of adjustTime)
 *               newGuest (correct the stats table after Guest Types are changed)
 *               newMem (correct the stats table after Member Types are changed)
 *               newMship (correct the stats table after Membership Types are changed)
 *               newTmode (correct the stats table after any Modes of Transportation are changed)
 *               insertTee (insert a new tee time - Proshop_insert)
 *               insertTee2 (insert a new tee time - Proshop_evntChkAll)
 *               getDate (get today's date)
 *               getTime (get current time - adjusted for time zone)
 *               logError (log system errors to a text file)
 *               getStackAsString (get the stack trace from an exception and return it in String)
 *         getClubParmIdFromTeeCurrID(returns the uid for a course from a given tee time uid)
 *         getClubParmIdFromCourseName(returns the uid for a course from a given course name)
 *
 *
 *   created: 11/20/2001   Bob P.
 *
 *
 *   last updated:
 *
 *        9/30/06   Meadow Springs - move the start date of a member restriction each week.
 *        9/28/06   CC of St. Albans - move the start date of a member restriction each week.
 *        9/20/06   Bellerive - use custom tee sheets for fall of 2006 while their new grass grows.
 *        9/19/06   Check restrictions when assigning times for Cherry Hills lotteries (enable this feature now).
 *        9/06/06   Change the custom tee sheets for Piedmont from 8 mins to 6 mins.
 *        9/05/06   Martin Downs - add email addresses for backup tee sheets.
 *        8/22/06   Changes to getMemberMainMenu & getMemberSubMenu / getProshopMainMenu & getProshopSubMenufor TLT System
 *        8/18/06   Add special reporting for Cordillera and Catamount.  Build files containing the day's tee time
 *                  information and send to their consultant.
 *        8/03/06   xTees - Custom for Mission Viejo - X's removed 7 days in advance
 *        7/29/06   Modified moveTee so that it copies teecurr_id & pace_status_id from teecurr2 -> teepast2
 *        7/28/06   xTees & xEvents - correct the date in the email message for when X's will be removed.
 *        7/28/06   xTimer - seperate the processing for each club so that if one fails it does not stop the others.
 *        7/28/06   Lottery - get1WeightP - correct the weight calculation.
 *        7/26/06   Always reset the 2 min timer and 60 min timer to ensure that they are running - if exception.
 *        7/14/06   Add some safety checks for the 2 min timer and 60 min timer to ensure that they are running.
 *        7/12/06   Lottery processing - if assigning a time other than that requested, check member access restrictions.
 *        7/11/06   Add custom tee sheets for North Oaks.
 *        6/28/06   Modified moveReqs to use the 'assigned' fb indicator from lreqs instead of the original fb.
 *        6/26/06   Modified do1Blocker so that tee times unblocked via auto-blocker feature will not become re-blocked
 *        6/23/06   Added buildDatabaseErrMsg global method for database errors
 *        6/22/06   Add custom tee sheets for El Niguel.
 *        6/15/06   Save the date when scanTee was last run so we don't have to call it every time a pro logs in.
 *        6/05/06   Changed the moveTee function to access db field names instead of #s
 *        6/01/06   Changed the dates for Westchester's custom tee sheets.
 *        5/30/06   Added getSimpleTime(hr,min) method for returning "3:05 PM" from 15 5
 *        5/30/06   Add ensureDoubleDigit() method for returning '08' from an '8'
 *        5/22/06   Changes to support the added primary key column in teecurr2 (teecurr_id)
 *        5/09/06   Skaneateles - add custom to automatically move the start date of some member rests.
 *        5/02/06   Remove synchronized statements.
 *        4/24/06   Add custom tee sheets for Brooklawn.
 *        4/24/06   Use the MYSQL 'ORDER BY RAND()' option in the Random Lottery processing.
 *        4/19/06   Add custom tee sheets for Wee Burn.
 *        4/17/06   Add custom tee sheets for TPC-TC.
 *        4/17/06   Add custom tee sheets for Apawamis CC.
 *        4/11/06   CC at Castle Pines - add more email addresses for backup tee sheets.
 *        4/03/06   Add custom tee sheets for New Canaan CC.
 *        3/29/06   CC at Castle Pines - add a 2nd email address for backup tee sheets.
 *        3/29/06   Mission Viejo - add a 2nd email address for backup tee sheets.
 *        3/08/06   Cherry Hills - move the start date of a member restriction each week.
 *        3/07/06   Change the errorlog method to save msg in db table.
 *        3/06/06   Change sessionLog method from using a text file to log data to using a db table.
 *        3/01/06   Add Common_Server class file processing where we check the id of the server we are running in
 *                  when timer processes are triggered.  If not server #1, do not process the timer (let it run though).
 *        3/01/06   Remove the 'Order By' command on the Lottery queries so that the groups are processed
 *                  in random order versus by the largest groups first.
 *        2/27/06   Add updateHist method to track all changes to tee times.
 *        2/08/06   Inverness - add a 2nd email address for backup tee sheets.
 *       12/30/05   When moving tee times from teecurr to teepast, move empty tee times to new table (teepastempty).
 *       12/21/05   Add custom tee sheets for Skaneateles CC.
 *       12/08/05   The Lakes - move the start date of a member restriction each day.
 *       12/06/05   Add doTableUpdate_tmodes method for new rounds report feature.
 *       11/09/05   Add custom tee sheets for Tripoli CC.
 *       11/06/05   Set the mNums in teecurr2 when building tee times for Proshop_evntChkAll so reports will work on tee sheet.
 *       10/16/05   Add custom tee sheets for Meadow Springs CC.
 *       10/11/05   Add custom tee sheets for Nakoma CC.
 *       10/07/05   Add custom tee sheets for The Stanwich Club.
 *        9/24/05   Correct the exception displays in the logerror calls.
 *        9/22/05   Add a login log to track all logins.
 *       09/13/05   processLott - reset i2 when a spot is taken - before checking before & after times.
 *                        This corrects problem where the last group in a multi group is unassigned.
 *       09/01/05   Add a 2nd email address for the back-up tee sheets for CC of St. Albans.
 *       08/21/05   Add displayOldNotes method - copied from Proshop_oldsheet so others can access it.
 *       08/19/05   Add displayNotes method - copied from Proshop_sheet so others can access it.
 *       08/07/05   Add custom tee sheets for Mission Viejo.
 *       06/07/05   Add new weighted lottery type processing.
 *       05/04/05   Old Warson - add custom to set precheckin in todays tee time at midnight.
 *       04/18/05   Move login credentials from Login to here for security purposes.
 *       04/15/05   Add custom tee sheets for Skokie.
 *       04/04/05   Add custom tee sheets for Hudson National.
 *       03/25/05   Add custom tee sheets for Woodway, Big Springs & Westchester.
 *       03/20/05   Remove DOCTYPE statement from page headers.
 *       03/09/05   Added getDate and getTime methods.
 *       03/03/05   Ver 5 - changed some expressions (show_) for precheckin feature
 *       02/14/05   Move Connect method to its own class file (dbConn) so we can maintain multiple copies.
 *       02/11/05   Add custom tee sheets for Piedmont Driving Club.
 *       01/24/05   Ver 5 - change club2 to club5 and stats2 to stats5.
 *       01/18/05   Add custom processing for North Ridge and Rogue Valley - deact member restrictions daily.
 *       12/07/04   Ver 5 - Check lesson books for busy time slots in inactTimer processing.
 *       11/11/04   Ver 5 - Add removeLessonBlockers method to clean up lessonblock table.
 *       10/17/04   Ver 5 - Add HeadTitle2 for servlets that require javascript.
 *       10/05/04   Ver 5 - Add getMemberMainMenu & getMemberSubMenu for navigation bar drop-down menus.
 *       09/17/04   Ver 5 - Change stats2 table processing to allow for more mem and mship types.
 *       09/16/04   Ver 5 - Remove getDaysInAdv - use com/foretees/common/verifySlot.getDaysInAdv.
 *       09/16/04   Ver 5 - Remove getClub - use com/foretees/common/getClub.
 *       08/02/04   Only process one lottery per club per timer expiration (wait for next timer).
 *       07/08/04   Add optimize method to optimize the db tables every Wed morning.
 *       04/20/04   Add buildHTee for Hazeltine National - custom tee sheets.
 *       03/04/04   Add adjustTimeBack method to make time adjustment.
 *       02/13/04   Add insertTee2 method to insert the tee times for Events.
 *       02/10/04   Add newTmode method to process changes in Modes of Trans..
 *       02/06/04   Add support for configurable modes of transportation in MoveStats.
 *       01/21/04   Add getDaysInAdv method.
 *       01/15/04   Add newGuest, newMem and newMship methods.
 *       01/14/04   Change tee sheets from 30 days to 365 days.
 *                  Add methods to process double tees - buildDblTee and removeDblTee
 *       01/07/03   Added stylesheet and js file link includes for HeadTitle method
 *       12/20/03   Upgrade to Version 4.
 *       12/15/03   Do not add member restriction to teecurr if show=no was specified.
 *       11/25/03   Enhancements for Version 3 - add RevLevel.
 *       11/19/03   Enhancements for Version 3 - add hotel changes to teecurr2 and teepast.
 *        7/18/03   Enhancements for Version 3 of the software.
 *                    - add doLotteries
 *                    - add getLottId
 *                    - add processLott
 *        4/30/03   Add support for F/B tees on events.
 *        2/13/03   Version 2 - add event sign up processing (scan in_use entries, and X's).
 *        1/04/03   Enhancements for Version 2 of the software.
 *                  inactTimer - change processing for removing X's from tee sheets (use hours specified).
 *                  Add support for multiple courses.
 *                  Support new fields in database tables.
 *                  Add support for Front/Back option in Block and Restrictions.
 *                  Add tsheetTimer to email pro shops the current tee sheet.
 *                  Add xTimer to process X's on tee sheet.
 *
 *        8/15/02   Added 'alternate minutes between tee times' processing to buildTee
 *
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;

// foretees imports
import com.foretees.common.parmTee;
import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;
import com.foretees.common.ProcessConstants;


public class SystemUtils {


   //********************************************************
   // Some constants for use by other servlets
   //********************************************************
   //
   // NOTE:  see also com\foretees\common\ProcessConstants !!!!!!!
   //
   public static final String REVLEVEL = "v5";          // change for new version of software!!!!!!!!!!!!!!!!!

   public static final int COURSEL = 30;                // length of courseName field in clubparm2 table
   public static final int EVENTL = 30;                 // length of name field in events2b table
   public static final int EVNTSUPL = 30;               // length of name field in evntsup2 table
   public static final int RESTL = 30;                  // length of name field in restriction2 table
   public static final int FIVEL = 30;                  // length of name field in fives2 table
   public static final int BLOCKL = 30;                 // length of name field in block2 table
   public static final int GRESL = 30;                  // length of name field in guestres2 table
   public static final int GQTAL = 30;                  // length of name field in guestqta4 table
   public static final int MNUML = 30;                  // length of name field in mnumres table
   public static final int DBLTL = 30;                  // length of name field in dbltee table
   public static final int LOTTL = 30;                  // length of name field in lottery table

   //
   //  Database Connection Credentials (keep here to protect)
   //
   public static String db_user = "bobp";
   public static String db_password = "rdp0805";

   //
   //  Login credentials - for Login.java
   //
   public static String support = "support";  // class variables that never change
   public static String sales = "sales";
   public static String admin = "admin";
   public static String proshop = "proshop";
   public static String id = "foretees";
   public static String passwordSup = "fourtea01";   // password for support login...
   public static String passwordSales = "four0101";   // password for sales login...

   //
   //   Date that scanTee last ran
   //
   public static long scanDate = 0;    

   //
   //   Login and Tee Sheet Disply Statistics - Login, Member_sheet, Proshop_sheet (dsiplayed in Support)
   //
   public static String startDate = "";     // date and time of first login

   public static int [] loginCountsMem = new int [24];     // one per hour of day     
   public static int [] loginCountsPro = new int [24];
   
   public static int [] sheetCountsMem = new int [24];
   public static int [] sheetCountsPro = new int [24];


   //
   //   Email server info - ******** see also .../com/foretees/common/ProcessConstants **********
   //
   //public static String HOST = "smtp5.mnwebhost.net";      // smtp server - must change for host server!!!!!!!!!!!!!!
  
   public static String HOST = "mail.foretees.com";           // ForeTees mail server
   //public static String HOST = "smtp.warpdriveonline.com";   // *** for Travis localhost (may not need any longer!!) ******
    
//   public static String PORT = "587";                       // TRAVIS port for emails
   public static String PORT = "25";                        // default port for emails

   public static String EFROM = "auto-send@foretees.com";   // fake return address

   public static String HEADER = "******************************************************************************************" +
                    "\nNOTICE: Please DO NOT reply to this email. " +
                    "\nThis message has been generated by the ForeTees system which cannot process a reply. " +
                    "\n**************************************************************************************** \n\n";

   public static String TRAILER = "\n\n\n*****************************************************************************************" +
                    "\nShould you prefer not to receive these email notifications, then login to the " +
                    "ForeTees Reservation System and select 'Settings'.  From there, select 'No' next to 'Do " +
                    "you wish to receive email notices of your tee times?' and click on 'Submit'.  If you " +
                    "require further assistance, contact your golf professionals. " +
                    "Thank you for using the ForeTees Reservation System." +
                    "\n********************************************************************************************";


   //********************************************************
   // Some constants for within this class
   //********************************************************
   //
   static int server_master = 1;                              // Master Server Id value

   static String rev = REVLEVEL;

   static int errorCount = 0;

   static String host = HOST;

   static String port = PORT;

   static String efrom = EFROM;

   static String header = HEADER;

   static String trailer = TRAILER;

   //
   //  Holiday dates for buildHtee (build tee sheets for Hazeltine Natl.)
   //
   //    also, see com/foretees/common/ProcessConstants (Hdate_ labels) !!!!!!!!!!!!
   //
   static long memDay1 = ProcessConstants.memDay;        // Memorial Day (Monday)
   static long memDay2 = 20070528;                  
   static long july4th1 = ProcessConstants.july4b;       // Actual 4th of July
   static long july4th2 = 20070704;
   static long july3rd1 = ProcessConstants.july4;        // Monday (IF necessary) 
   static long july3rd2 = 20070704;
   static long laborDay1 = ProcessConstants.laborDay;    // Labor Day (Monday)
   static long laborDay2 = 20070903;

   //
   //  Time value save area for Timer verification
   //
   static int inactTime = 0;
   static long min2Time = 0;      // date and time when 2 min timer should expire by (next expiration)
   static long min60Time = 0;     // date and time when 60 min timer should expire by (next expiration)


 //**********************************************************
 // HeadTitle - Build HTML Header with a Title provided by caller.....
 //**********************************************************

 public static final String DOCTYPE =
  "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">";


 public static String HeadTitle(String title) {

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

//    return(DOCTYPE + "\n" +
      return("<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD>" + //getBaseTag()  +
           "<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>" +
           "\n\n<!--- ************* Server Id = " +server_id+ " ************* --->\n\n" +
           "<TITLE>" + title + "</TITLE></HEAD>\n");
 }

//  same as above except no end HEAD (/head) - allows unique js to be added in servlet
 public static String HeadTitle2(String title) {

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

//    return(DOCTYPE + "\n" +
    return("<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD>" + //getBaseTag()  +
           "<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>" +
           "\n\n<!--- ************* Server Id = " +server_id+ " ************* --->\n\n" +
           "<TITLE>" + title + "</TITLE>\n");
 }

 public static String HeadTitleAdmin(String title) {

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

//    return(DOCTYPE + "\n" +
    return("<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
           "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
           "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
           "\nReproduction is strictly prohibited.-->\n" +
           "<HEAD><link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>" +
           "\n\n<!--- ************* Server Id = " +server_id+ " ************* --->\n\n" +
           "<TITLE>" + title + "</TITLE></HEAD>\n");
 }
 

 // *****************************************************************************
 // getMemberMainMenu - build the Member drop-down menu for navigation bar
 // *****************************************************************************

 public static void getMemberMainMenu(HttpServletRequest req, PrintWriter out, String caller)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //This is needed for the Member tabs

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesMember";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesMember";
    String mnuJsFileName = "foreteesMember.js";
        
    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberMainMenu: Error: No Session Found.");
        return;
    }
    
    int tmp_tlt = 0;

    try {
      
       tmp_tlt = (Integer)session.getAttribute("tlt");

    }
    catch (Exception ignore) {      // default to zero if error
    }


    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) 
    {
        mnuId = "xawmMenuPathImg-foreteesMemberTLT";
        mnuImgNameAndId = "awmMenuPathImg-foreteesMemberTLT";
        mnuJsFileName = "foreteesMemberTLT.js";
    } else {
    
        // if called by other web site (MFirst)
        if (caller.equals( "MEMFIRST" ) || caller.equals( "AMO" ) || caller.equals( "CLUBESSENTIAL" ))
        {
            //request came from an external site, remove the email tab
            mnuId = "xawmMenuPathImg-foreteesMemberCond1";
            mnuImgNameAndId = "awmMenuPathImg-foreteesMemberCond1";                                
            mnuJsFileName = "foreteesMemberCond1.js";
        }
        
    } // end if IS_TLT
    
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'>");
    out.println("<img name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web utilities/member/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [2]', awmBN='532'; awmAltUrl='';</script>");
    out.println("<script src='/" +rev+ "/web utilities/member/" + mnuJsFileName + "' language='JavaScript1.2' type='text/javascript'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");
    out.println("<hr class=\"gblHdr\">");
    out.println("<div style=\"position:absolute; top:-100px\">");
    out.println("<form name=\"mnuHlp\" target=\"bot\">");
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form>");
    out.println("</div>");
 }


 // *****************************************************************************
 // getMemberSubMenu - build the Member drop-down sub-menus for navigation bar
 // *****************************************************************************

 public static void getMemberSubMenu(HttpServletRequest req, PrintWriter out, String caller)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //within the "bot" Frame.  This is needed for the Member sub menus

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesMember_sub";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesMember_sub";
    String mnuJsFileName = "foreteesMember_sub.js";
    
    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }

    int tmp_tlt = 0;

    try {

       tmp_tlt = (Integer)session.getAttribute("tlt");

    }
    catch (Exception ignore) {      // default to zero if error
    }

    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) 
    {
        
       mnuId = "xawmMenuPathImg-foreteesMemberTLT_sub";
       mnuImgNameAndId = "awmMenuPathImg-foreteesMemberTLT_sub";
       mnuJsFileName = "foreteesMemberTLT_sub.js";
    } else {
        
       // if called by other web site (MFirst)
       if (caller.equals( "MEMFIRST" ) || caller.equals( "AMO" ) || caller.equals( "CLUBESSENTIAL" ))
       {
         //request came from an external site
          mnuId = "xawmMenuPathImg-foreteesMemberCond1_sub";
          mnuImgNameAndId = "awmMenuPathImg-foreteesMemberCond1_sub";
          mnuJsFileName = "foreteesMemberCond1_sub.js";
       }
    } // end if IS_TLT
    
   //<!-- DO NOT MOVE! The following AllWebMenus code must always be placed right AFTER the BODY tag-->
   out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'>");
   out.println("<img name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web utilities/member/awmmenupath.gif' alt=''>");
   out.println("</span><script type='text/javascript'>var MenuLinkedBy='AllWebMenus [2]', awmBN='532'; awmAltUrl='';</script>");
   out.println("<script src='/" + REVLEVEL + "/web utilities/member/" + mnuJsFileName + "' language='JavaScript1.2' type='text/javascript'></script>");
   out.println("<script type='text/javascript'>awmBuildMenu();</script><br>");
   out.println("<form name=\"mnuHlp\" target=\"bot\">");
   out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\"></form>");
 }


 // *****************************************************************************
 // getProshopMainMenu - build the Proshop drop-down menu for navigation bar
 // *****************************************************************************

 public static void getProshopMainMenu(HttpServletRequest req, PrintWriter out, int lottery)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //This is needed for the Proshop tabs

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesProshop";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesProshop";
    String mnuJsFileName = "foreteesProshop.js";
   
    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }
    
    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) 
    {
        
        mnuId = "xawmMenuPathImg-foreteesProshopTLT";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopTLT";
        mnuJsFileName = "foreteesProshopTLT.js";
    } else {
        
        if (lottery == 0)
        {
            //  lottery not supported, remove the lottery tab
            mnuId = "xawmMenuPathImg-foreteesProshopNoLot";
            mnuImgNameAndId = "awmMenuPathImg-foreteesProshopNoLot";
            mnuJsFileName = "foreteesProshopNoLot.js";
        }
    }


    // if the club does not support the lottery

    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'>");
    out.println("<img name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web utilities/proshop/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [2]', awmBN='526'; awmAltUrl='';</script>");
    out.println("<script src='/" +rev+ "/web utilities/proshop/" + mnuJsFileName + "' language='JavaScript1.2' type='text/javascript'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script>");
    out.println("<hr class=\"gblHdr\">");
    out.println("<form name=\"mnuHlp\" action=\"\">");                          // to be filled by exeMenuAction js
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\">");
    out.println("</form>");
 }


 // *****************************************************************************
 // getProshopSubMenu - build the Proshop drop-down sub-menus for navigation bar
 // *****************************************************************************

 public static void getProshopSubMenu(HttpServletRequest req, PrintWriter out, int lottery)
 {

    //The following AllWebMenus code must always be placed right AFTER the BODY tag
    //within the "bot" Frame.  This is needed for the Proshop sub menus

    //These are the default values
    String mnuId = "xawmMenuPathImg-foreteesProshop_sub";
    String mnuImgNameAndId = "awmMenuPathImg-foreteesProshop_sub";
    String mnuJsFileName = "foreteesProshop_sub.js";

    HttpSession session = null;
    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        logError("getMemberSubMenu: Error: No Session Found.");
        return;
    }
    
    int tmp_tlt = (Integer)session.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    if (IS_TLT) 
    {
        
        mnuId = "xawmMenuPathImg-foreteesProshopTLT_sub";
        mnuImgNameAndId = "awmMenuPathImg-foreteesProshopTLT_sub";
        mnuJsFileName = "foreteesProshopTLT_sub.js";
    } else {
        
        // if the club does not support the lottery
        if (lottery == 0)
        {
            //  lottery not supported
            mnuId = "xawmMenuPathImg-foreteesProshopNoLot_sub";
            mnuImgNameAndId = "awmMenuPathImg-foreteesProshopNoLot_sub";
            mnuJsFileName = "foreteesProshopNoLot_sub.js";
        }
    } // end if IS_TLT

    //<!-- DO NOT MOVE! The following AllWebMenus code must always be placed right AFTER the BODY tag-->
    out.println("<span id='" + mnuId + "' style='position:absolute;top:-50px'>");
    out.println("<img name='" + mnuImgNameAndId + "' id='" + mnuImgNameAndId + "' src='/" +rev+ "/web utilities/proshop/awmmenupath.gif' alt=''></span>");
    out.println("<script type='text/javascript'>var MenuLinkedBy='AllWebMenus [2]', awmBN='526'; awmAltUrl='';</script>");
    out.println("<script src='/" + REVLEVEL + "/web utilities/proshop/" + mnuJsFileName + "' language='JavaScript1.2' type='text/javascript'></script>");
    out.println("<script type='text/javascript'>awmBuildMenu();</script><br>");
    out.println("<form name=\"mnuHlp\">");               // to be filled by exeMenuAction js
    out.println("<input type=\"hidden\" name=\"revLevel\" value=\"" + rev + "\">");
    out.println("</form>");
 }


 // *********************************************************
 // Get a DB connection from the session or new
 // *********************************************************
 //
// public synchronized static Connection getCon(HttpSession sess) {
 public static Connection getCon(HttpSession sess) {

   Connection con = null;

   //
   // Get the connection holder saved in the session object
   //
   ConnHolder holder = (ConnHolder) sess.getAttribute("connect");

   if (holder == null) {

      //
      // Lost the connection - get and save a new one
      //
      String club = (String)sess.getAttribute("club");   // get club name

      if (!club.equals( "" )) {

         try {
            con = dbConn.Connect(club);
            holder = new ConnHolder(con);            // create a new holder from ConnHolder class
            sess.setAttribute("connect", holder);    // save DB connection holder

         }
         catch (Exception exc) {

            // Error connecting to db....
            return null;
         }
      }
   }

   if (holder != null) {

      con = holder.getConn();      // get the connection and return it
   }
   return con;
 }


 // *********************************************************
 // verifyMem - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyMem(HttpServletRequest req, PrintWriter out) {


   String id = "foretees";                   // session id

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Verify Member: User Verification Error (cookies). Agent: " + agent + ", ";


   HttpSession session = null;

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " No Session Found.";    // build error msg

//      logError(errorMsg);                           // log it

   } else {

      String sess_id = (String)session.getAttribute("sess_id");   // get session id
      String user = (String)session.getAttribute("user");         // get username
      String name = (String)session.getAttribute("name");         // get user's full name
      String club = (String)session.getAttribute("club");         // get club's name
      String caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)

      if (!sess_id.equals( id ) || user.equals( "" )) {

         //
         //  save error message in /v2/error.txt
         //
         errorMsg = errorMsg + " Invalid session info: Session id = " +sess_id+ ", User = " +user+ ", Name = " +name+ ", Club = " +club+ ", Caller = " +caller;

         logError(errorMsg);                           // log it

         session = null;
      }
   }

   if (session == null) {

      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<H2>Access Error - Please Read</H2>");
      out.println("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
      out.println("<BR><BR>This site requires the use of Cookies for security purposes.");
      out.println("<BR>We use them to verify your session and prevent unauthorized access.");
      out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
      out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
      out.println("<BR><BR>If you have a firewall, please check its settings as well.");
      out.println("<BR><BR><HR width=\"500\">");
      out.println("<b>NOTE:</b> You must be logged in to access the ForeTees system. You cannot bookmark a page");
      out.println("<BR>within ForeTees and then return to it later without logging in.  If you access ForeTees");
      out.println("<BR>from your club web site, then you must login to the web site.  If you access ForeTees");
      out.println("<BR>directly, then you must do so through the ForeTees Login page.");
      out.println("<BR><HR width=\"500\"><BR>");
      out.println("If you have tried all of the above and still receive this message,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
      out.println("<BR>Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }

   return session;
 }

 //
 //  same as above wihtout the error msg - called by Member_maintop (to prevent double error msg)
 //
 public static HttpSession verifyMem2(HttpServletRequest req, PrintWriter out) {

   String id = "foretees";                   // session id

   HttpSession session = null;

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      String sess_id = (String)session.getAttribute("sess_id");   // get session id
      String user = (String)session.getAttribute("user");         // get username
      String name = (String)session.getAttribute("name");         // get user's full name
      String club = (String)session.getAttribute("club");         // get club's name
      String caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)

      if (!sess_id.equals( id ) || user.equals( "" )) {

         session = null;
      }
   }
   return session;
 }


 // *********************************************************
 // verifyHotel - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyHotel(HttpServletRequest req, PrintWriter out) {


   String id = "foretees";                   // session id

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Member_select: User Verification Error (cookies). Agent: " + agent + ", ";


   HttpSession session = null;

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " Cannot access session info - cookies blocked??";    // build error msg

//      logError(errorMsg);                           // log it

   } else {

      String sess_id = (String)session.getAttribute("sess_id");   // get session id
      String user = (String)session.getAttribute("user");         // get username
      String name = (String)session.getAttribute("name");         // get user's full name
      String club = (String)session.getAttribute("club");         // get club's name
      String caller = (String)session.getAttribute("caller");     // get caller (none, or AMO or MFirst)

      if (!sess_id.equals( id ) || user.equals( "" )) {

         //
         //  save error message in /v2/error.txt
         //
         errorMsg = errorMsg + " Invalid session info: Session id = " +sess_id+ ", User = " +user+ ", Name = " +name+ ", Club = " +club+ ", Caller = " +caller;

         logError(errorMsg);                           // log it

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>This site requires the use of Cookies for security purposes.");
      out.println("<BR>We use them to verify your session and prevent unauthorized access.");
      out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
      out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
      out.println("<BR><BR>");
      out.println("<BR>If you have changed or verified the setting above and still receive this message,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name or member number and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }

   return session;
 }


 // *********************************************************
 // verifyPro - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyPro(HttpServletRequest req, PrintWriter out) {

   HttpSession session = null;

   String proshop = "proshop";
   String user = "";

   String agent = req.getHeader("User-Agent");  // Get user's browser info for error msg

   String errorMsg = "Proshop User Rejected: User Verification Error (cookies). Agent: " + agent + ", ";

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( proshop )) {

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>Your session has most likely timed out.");
      out.println("<BR><BR>");
      out.println("<BR>If you feel you have received this message in error,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      //
      //  save error message in /v2/error.txt
      //
      errorMsg = errorMsg + " User=" +user;    // build error msg - display user if present
//      logError(errorMsg);                           // log it
   }

   return session;
 }


 // *********************************************************
 // verifyAdmin - Check for illegal access by user
 // *********************************************************

 public static HttpSession verifyAdmin(HttpServletRequest req, PrintWriter out) {

   HttpSession session = null;

   String admin = "admin";
   String admin2 = "Admin";

   //
   // Make sure user didn't enter illegally
   //
   session = req.getSession(false);  // Get user's session object (no new one)

   if (session != null) {

      String user = (String)session.getAttribute("user");   // get username

      if (!user.startsWith( admin ) && !user.startsWith( admin2 )) {

         session = null;
      }
   }

   if (session == null) {

//      out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
      out.println("<HTML>");
      out.println("<HEAD>");
      out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
      out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
      out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
      out.println("<TITLE>Access Error - Redirect</TITLE></HEAD>");
      out.println("<BODY><CENTER>");
      out.println("<BR><H2>Access Error</H2><BR>");
      out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
      out.println("<BR>This site requires the use of Cookies for security purposes.");
      out.println("<BR>We use them to verify your session and prevent unauthorized access.");
      out.println("<BR><BR>Please check your 'Privacy' settings, under 'Tools', 'Internet Options'");
      out.println("<BR>(for MS Internet Explorer).  This must be set to 'Medium High' or lower.");
      out.println("<BR><BR>");
      out.println("<BR>If you have changed or verified the setting above and still receive this message,");
      out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
      out.println("<BR>Provide your name and the name of your club.  Thank you.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Logout\" target=\"_top\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
   }

   return session;
 }


 //************************************************************************
 // buildTee - Builds a tee sheet for the date requested into teecurr2 table
 //
 //   called by: scanTee (below) to build missing tee sheets
 //
 //************************************************************************

 public static void buildTee(int year, int month, int day, int day_name, String course, String club, Connection con)
         throws Exception {


   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   String error = "None";

   int count = 0;
   int f_hr = 0;
   int f_min = 0;
   int l_hr = 0;
   int l_min = 0;
   int betwn = 0;
   int betwn2 = 0;
   int alt_betwn = 0;
   int alt = 0;
   int first = 0;
   short xx = 0;

   boolean WCfiveMinDay = false;
   boolean WWsixMinDay = false;
   boolean BSsaturday = false;
   boolean BSholiday = false;
   boolean SkokieDay1 = false;
   boolean SkokieDay2 = false;
   boolean NCspec1 = false;
   boolean NCspec2 = false;
   boolean NCspec3 = false;
   boolean APspec1 = false;
   boolean APspec2 = false;

   //
   //  Gather all the info we need to build the table
   //
   long date = year * 10000;          // create a date field from input values
   date = date + (month * 100);
   date = date + day;                 // date = yyyymmdd (for comparisons)

   long shortDate = date - ((date / 10000) * 10000);       // get mmdd (i.e.  20060512 - 20060000)

   //
   //  day_name is an index representing the day of the week (01 = sun, 02 = mon, etc.)
   //
   if (day_name > 7) {

      throw new Exception("Invalid day_name value received - buildTee");
   }

   String name = day_table[day_name];  // get name for day

   //
   //  Get the club parameters from clubparm table for the requested course
   //
   try {
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT * FROM clubparm2 WHERE courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, course);
      rs2 = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs2.next()) {

         f_hr = rs2.getInt("first_hr");
         f_min = rs2.getInt("first_min");
         l_hr = rs2.getInt("last_hr");
         l_min = rs2.getInt("last_min");
         betwn = rs2.getInt("betwn");
         xx = rs2.getShort("xx");
         alt_betwn = rs2.getInt("alt");
           
      } else {
        
         xx = 0;
      }                // end of if clubparms (for this course)
      pstmt1.close();

      //
      //  if we are to wait for initial setup, then skip
      //
      //  xx is set to zero in Proshop_parms when we set the club parms for the first time
      //  zero indicates we should wait for config completion before building the tees
      //
      if (xx != 0) {

         //
         //  init some variables to be used in building the tee slots
         //
         int hour = f_hr;                       // hour value for tee time slot
         int min = f_min;                       // minute value for slot
         int thistime = (f_hr * 100) + f_min;   // start time
         int lasttime = (l_hr * 100) + l_min;   // end time

         short fb = 0;
         short fb0 = 0;
         short fb1 = 1;
         String dbl_recurr = "";
         String sfb = "";
         boolean check_dbl = false;

         error = "Tee Times Exist?";

         //
         //  Check if any tee time slots already exist for this date on this course
         //
         PreparedStatement pstmt = con.prepareStatement (
            "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setString(2, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {               // if no tee times exist

            pstmt.close();
            return;                     // return if already exist 
         }     

         pstmt.close();

         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', '', '', '', '', " +
            "'', '', '', '', '', '', ?, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +
            "'', ?, '', 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, '', '', " +
            "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '')");

         error = "Check Dbl Tees?";

         //
         // See if we need to check the dbltee table each time
         //
         PreparedStatement pstmt8 = con.prepareStatement (
                "SELECT name FROM dbltee2 WHERE sdate <= ? AND edate >= ? AND (courseName = ? OR courseName = '-ALL-')");

         pstmt8.clearParameters();        // clear the parms
         pstmt8.setLong(1, date);         // put the parm in stmt
         pstmt8.setLong(2, date);
         pstmt8.setString(3, course);
         rs1 = pstmt8.executeQuery();      // execute the prepared stmt

         if (rs1.next()) {

            check_dbl = true;    // we have to check dbltee table
         }

         pstmt8.close();


         //
         //******************************************************************************************
         //   Custom sheets for Santa Ana CC
         //
         //   Sat & Sun      8 minute intervals
         //   Week Days      7 & 8 minute intervals (set as norm in config)
         //******************************************************************************************
         //
         if (club.equals( "santaana" )) {       // if Santa Ana
           
            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;
               alt_betwn = 0;
            }
         }

/*
         //
         //******************************************************************************************
         //   Custom sheets for Merion  (NOTE:  Merion changed their minds on this!!)
         //
         //   Sat & Sun & Holidays      7:30 start time and 10 minute intervals
         // 
         //******************************************************************************************
         //
         if (club.equals( "merion" ) && course.equals( "East" )) {       // if Merion and East course

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                date == memDay1 || date == july4th1 || date == laborDay1) {

               betwn = 10;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
            }
         }
*/

         //
         //******************************************************************************************
         //   Custom sheets for Westchester CC - South Course ONLY
         //
         //      ************* See also Member_slot ********************
         //
         //   5 minute intervals starting at 7:40 AM to 11:00 AM for the following dates:
         //  
         //         Every Wed from 6/14 - 10/19, except 7/26 & 9/06
         //
         //         Every Thurs from 6/15 - 10/19, except 7/13 & 9/07 (2006)
         //
         //
         //******************************************************************************************
         //
         if (club.equals( "westchester" ) && course.equalsIgnoreCase( "south" )) {     

            if (shortDate > 607 && shortDate < 1020 && (name.equals( "Wednesday" ) || name.equals( "Thursday" ))) {

               WCfiveMinDay = true;

               if (name.equals( "Wednesday" ) && ((shortDate > 719 && shortDate < 727) || (shortDate > 900 && shortDate < 907))) {  // skip these Wed's

                  WCfiveMinDay = false;
               }

               if (name.equals( "Thursday" ) && ((shortDate > 706 && shortDate < 714) || (shortDate > 900 && shortDate < 908))) {  // skip these Thur's

                  WCfiveMinDay = false;
               }
            }

            if (WCfiveMinDay == true) {
              
               betwn2 = betwn;        // save config'd between value
               betwn = 5;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:40 AM
               min = 40;           
               thistime = 740;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Woodway CC 
         //
         //        ************* See also Member_slot ********************
         //
         //   6 minute intervals starting at 8:00 AM to 11:00 AM for the following dates:
         //
         //      April 18
         //      May 2
         //      June 1, 20
         //      July 6, 18, 25
         //      August 1, 8, 22, 29
         //      Sept  26
         //      Oct   12
         //
         //******************************************************************************************
         //
         if (club.equals( "woodway" )) {

            if (month == 4) {         // April

               if (day == 18) {

                  WWsixMinDay = true;
               }
            }

            if (month == 5) {         // May


               if (day == 2) {

                  WWsixMinDay = true;
               }
            }

            if (month == 6) {         // June


               if (day == 1 || day == 20) {

                  WWsixMinDay = true;
               }
            }

            if (month == 7) {         // July


               if (day == 6 || day == 18 || day == 25) {

                  WWsixMinDay = true;
               }
            }

            if (month == 8) {         // August


               if (day == 1 || day == 8 || day == 22 || day == 29) {

                  WWsixMinDay = true;
               }
            }

            if (month == 9) {         // Sept


               if (day == 26) {

                  WWsixMinDay = true;
               }
            }

            if (month == 10) {         // October


               if (day == 12) {

                  WWsixMinDay = true;
               }
            }

            if (WWsixMinDay == true) {

               betwn2 = 9;           // switch to 9 min after 11:00
               betwn = 6;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Big Spring CC
         //
         //     Special times for Saturdays and Holidays
         //
         //      April 16, 23, 30
         //      May 7, 14, 21, 28, (30 = holiday)
         //      June 4, 11, 18, 25
         //      July 2, 9, 16, 23, 30, (4 = holiday) 
         //      August 6, 13, 20, 27 
         //      Sept  3, 10, 17, 24, (5 = holiday)
         //      Oct   1
         //
         //******************************************************************************************
         //
         if (club.equals( "bigsprings" )) {

            if (shortDate > 400 && shortDate < 1001) {         // April - Sept

               if (name.equalsIgnoreCase( "saturday" )) {      // if Saturday

                  BSsaturday = true;
               }
                 
               if (date == memDay1 || date == july4th1 || date == laborDay1) {   // if a Holiday
  
                  BSholiday = true;
               }
            }

            if (BSsaturday == true) {   // if SATURDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
               check_dbl = false;     // do custom dbl tees
            }

            if (BSholiday == true) {   // if HOLIDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
               check_dbl = false;     // do custom dbl tees
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Skokie CC
         //
         //     Special double tees for Tuesdays and Thursdays
         //
         //      May 10, 17 (SkokieDay1)
         //      June 2, 14, 21,
         //      July 7,
         //      August 2, 
         //      Sept  8, 13, 20, 27 (SkokieDay2)
         //
         //******************************************************************************************
         //
         if (club.equals( "skokie" )) {

            if (month == 5) {         // May

               if (day == 10 || day == 17) {

                  SkokieDay1 = true;
               }
            }

            if (month == 6) {         // June

               if (day == 2 || day == 14 || day == 21) {

                  SkokieDay2 = true;
               }
            }

            if (month == 7) {         // July

               if (day == 7) {

                  SkokieDay2 = true;
               }
            }

            if (month == 8) {         // August

               if (day == 2) {

                  SkokieDay2 = true;
               }
            }

            if (month == 9) {         // Sept

               if (day == 8 || day == 13 || day == 20 || day == 27) {

                  SkokieDay2 = true;
               }
            }

            if (SkokieDay1 == true) {   // if Tuesday in May

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
               check_dbl = false;      // do custom dbl tees
            }

            if (SkokieDay2 == true) {   // if other special day

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
               check_dbl = false;     // do custom dbl tees
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for New Canaan CC
         //
         //     Special 2-some times based on time of year and day of week
         //
         //******************************************************************************************
         //
         if (club.equals( "newcanaan" )) {

            //
            //  Off-Peak Season (401 - 5/26 OR 9/05 - 11/01) 
            //
            if ((shortDate > 331 && shortDate < 527) || (shortDate > 904 && shortDate < 1102)) {  

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:30 AM
                  min = 30;
                  thistime = 730;

                  NCspec1 = true;        // indicate special times #1 for below
               }
            }

            //
            //  Peak Season (5/27 - 9/04)
            //
            if (shortDate > 526 && shortDate < 905) {

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) || 
                   date == memDay1 || date == july4th1 || date == july3rd1 || date == laborDay1) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:00 AM
                  min = 0;
                  thistime = 700;

                  NCspec2 = true;        // indicate special times #2 for below
               }
            }

            //
            //  Weekday Season (4/11 - 10/30)
            //
            if (shortDate > 410 && shortDate < 1031) {

               if ((name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" ) || name.equalsIgnoreCase( "thursday" ) ||
                   name.equalsIgnoreCase( "friday" )) && shortDate != 704) {
                 
                  betwn2 = betwn;        // save config'd between value
                  betwn = 5;
                  alt_betwn = 0;
                  hour = 8;              // start these days at 8:00 AM
                  min = 0;
                  thistime = 800;

                  NCspec3 = true;        // indicate special times #3 for below
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The Apawamis Club
         //
         //     Special 2-some times on Sundays based on time of year
         //
         //******************************************************************************************
         //
         if (club.equals( "apawamis" )) {

            //
            //   Sundays only
            //
            if (name.equals( "Sunday" )) {

               //
               //  Peak Season (5/28 - 9/03)
               //
               if (shortDate > 527 && shortDate < 904) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 6;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:00 AM
                  min = 0;
                  thistime = 700;

                  APspec1 = true;        // indicate special times for below
               }

               //
               //  Off-Peak Season (9/04 - 10/30)
               //
               if (shortDate > 903 && shortDate < 1031) {

                  betwn2 = betwn;        // save config'd between value
                  betwn = 6;
                  alt_betwn = 0;
                  hour = 7;              // start these days at 7:30 AM
                  min = 30;
                  thistime = 730;

                  APspec1 = true;        // indicate special times for below
               }
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for North Oaks
         //
         //     Tuesdays after Aug 1st have diferent times.
         //
         //******************************************************************************************
         //
         if (club.equals( "noaks" )) {

            //
            //   Tuesdays on & after 8/01 only
            //
            if (name.equals( "Tuesday" ) && shortDate > 800) {     

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Brooklawn CC
         //******************************************************************************************
         //
         if (club.equals( "brooklawn" )) {

            if (name.equals( "Tuesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equals( "Wednesday" ) || name.equals( "Thursday" ) || name.equals( "Friday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 9;
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equals( "Saturday" ) || name.equals( "Sunday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for El Niguel CC
         //******************************************************************************************
         //
         if (club.equals( "elniguelcc" )) {

            if (name.equals( "Tuesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }

            if (name.equals( "Thursday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 8;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:28 AM
               min = 28;
               thistime = 728;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The TPC-TC
         //******************************************************************************************
         //
         if (club.equals( "tpctc" )) {

            betwn2 = betwn;        // save config'd between value
            betwn = 8;             // start with 8 min intervals
            alt_betwn = 0;
            hour = 7;              // start these days at 7:00 AM
            min = 0;
            thistime = 700;

         }

         //
         //******************************************************************************************
         //   Custom sheets for Wee Burn
         //
         //     Special 2-some times on Wednesdays (see below)
         //
         //******************************************************************************************
         //
         if (club.equals( "weeburn" )) {

            betwn2 = betwn;        // save config'd between value
         }

         //
         //******************************************************************************************
         //   Custom sheets for Hudson National CC
         //
         //     Special times for Weekends and Holidays
         //
         //******************************************************************************************
         //
         if (club.equals( "hudsonnatl" )) {

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               BSsaturday = true;
            }

            if (date == memDay1 || date == july4th1 || date == laborDay1 || date == memDay2 || date == july4th2 || date == laborDay2) {

               BSsaturday = true;
            }

            if (BSsaturday == true) {   // if SATURDAY

               betwn2 = betwn;        // save config'd between value
               betwn = 10;
               alt_betwn = 0;
               hour = 7;              // start these days at 7:30 AM
               min = 30;
               thistime = 730;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Mission Viejo CC
         //
         //     8 minute intervals for Sundays (normally 10 minute)
         //
         //******************************************************************************************
         //
         if (club.equals( "missionviejo" )) {

            if (name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;             // 8 minute intervals on Sundays            
               alt_betwn = 0;
               hour = 6;              // start these days at 6:32 AM
               min = 32;
               thistime = 632;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for The Stanwich Club
         //
         //     6 minute intervals early each day for 2-some-only times (normally 8 minute)
         //
         //******************************************************************************************
         //
         if (club.equals( "stanwichclub" )) {

            if (name.equalsIgnoreCase( "saturday" )  || name.equalsIgnoreCase( "sunday" )  || name.equalsIgnoreCase( "monday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 6;             // start with 6 minute intervals
               alt_betwn = 0;
               hour = 7;              // start these days at 7:00 AM
               min = 0;
               thistime = 700;
            }
              
            if (name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" )) {

               betwn2 = betwn;        // save config'd between value
               betwn = 6;             // start with 6 minute intervals
               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM
               min = 0;
               thistime = 800;
            }

            if (name.equalsIgnoreCase( "thursday" ) || name.equalsIgnoreCase( "friday" )) {

               alt_betwn = 0;
               hour = 8;              // start these days at 8:00 AM (8 min intervals all day)
               min = 0;
               thistime = 800;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Bellerive (Fall & Winter 2006/2007 only)
         //
         //     15 minute intervals on weekdays and 12 minute on weekends
         //
         //******************************************************************************************
         //
         if (club.equals( "bellerive" ) && date < 20070416) {

            betwn2 = betwn;        // save config'd between value

            if (name.equalsIgnoreCase( "saturday" )  || name.equalsIgnoreCase( "sunday" )) {

               betwn = 12;             // 12 minute intervals
               alt_betwn = 0;
                 
            } else {

               betwn = 15;             // 15 minute intervals
               alt_betwn = 0;
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Fairwood CC
         //
         //     8 minute intervals for Saturdays and Sundays (normally 10 minute)
         //
         //******************************************************************************************
         //
         if (club.equals( "fairwood" )) {

            if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" )) {

               betwn = 8;             // 8 minute intervals on Sundays
               alt_betwn = 0;
               hour = 6;              // start these days at 6:30 AM
               min = 30;
               thistime = 630;
               lasttime = 2100;           // end of tee sheet (9:00 PM)
            }
         }

         //
         //******************************************************************************************
         //   Custom sheets for Piedmont Driving Club in Atlanta
         //
         //   Start Times:
         //              4/01 - 10/31     Nov & Mar       12/01 - 2/29
         //              ------------     ---------       ------------
         //   Sat & Sun      8:00            8:30             9:00
         //   (& Mem & Labor Days)
         //   Week Days      8:30            9:00             9:30
         //  -----------------------------------------------------------
         //
         //  Intervals:
         //
         //     Sat & Sun -  All year long the 1st 6 tee times are 6 minute intervals, then
         //     (& holidays) the rest of the day its 10 minute intervals, though the 10 minute
         //                  interval times must start on an even 10's minute (i.e. 8:40).
         //
         //     Weekdays -   Always 10 minute intervals (as specified in course parms)
         //******************************************************************************************
         //
         if (club.equals( "piedmont" )) {       // if Piedmont Driving Club

            //
            //  Reset parms based on time of year and day of week
            //
            hour = 8;              // hour value for tee time slot
            min = 0;               // minute value for slot

            if (month == 3 || month == 11) {       // March or Nov

               if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                   date == memDay1 || date == laborDay1) {

                  min = 30;               // minute value to start (8:30)

               } else {

                  hour = 9;              // hour value to start (9:00)
               }
            } else {

               if (month == 1 || month == 2 || month == 12) {       // Jan, Feb or Dec

                  if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                      date == memDay1 || date == laborDay1) {

                     hour = 9;              // hour value to start (9:00)

                  } else {

                     hour = 9;              // hour value to start 
                     min = 30;              // minute value to start (9:30)
                  }
               } else {             // rest of the year (8:00 or 8:30)

                  if (!name.equalsIgnoreCase( "saturday" ) && !name.equalsIgnoreCase( "sunday" ) &&
                      date != memDay1 && date != laborDay1) {

                     min = 30;              // minute value to start (8:30)
                  }
               }
            }

            thistime = (hour * 100) + min;  // hr & min in one value (start time)
         }                                  // end of IF piedmont
           
         //*****************************************************************************************
         //  End of Custom Tee Sheet Checks (might be more to finish below)
         //*****************************************************************************************

         first = 1;       // first slot indicator

         //
         //  Now we can build the tee time slots
         //
         while (thistime <= lasttime) {

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
            pstmt3.setInt(2, month);
            pstmt3.setInt(3, day);
            pstmt3.setInt(4, year);
            pstmt3.setString(5, name);
            pstmt3.setInt(6, hour);
            pstmt3.setInt(7, min);
            pstmt3.setInt(8, thistime);

            //
            // check for double tees if there are any for this day
            //
            fb = 0;

            if (check_dbl) {             // must we check double tees?

               //
               // Prepared statement to search the double tees table for 'double tee' times
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                  "AND stime1 <= ? AND etime1 >= ? AND (courseName = ? OR courseName = '-ALL-')");

               error = "Check for Dbl Tees";

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setLong(1, date);         // put the parms in stmt
               pstmt6.setLong(2, date);
               pstmt6.setInt(3, thistime);
               pstmt6.setInt(4, thistime);
               pstmt6.setString(5, course);
               rs3 = pstmt6.executeQuery();      // see if Front and Back to be built

               loop2:
               while (rs3.next()) {

                  dbl_recurr = rs3.getString(1);   // dbl tee found for this time

                  //
                  //  We must check the recurrence for this day (Monday, etc.)
                  //
                  if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                      (!name.equalsIgnoreCase( "saturday" )) &&
                      (!name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "saturday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

               }   // end of loop2 while loop

               pstmt6.close();

               if (fb == 0) {                        // if no hit, check 'no tees' times (cross-over)

                  //
                  // Prepared statement to search the double tees table for 'no tee' times (cross-overs)
                  //
                  PreparedStatement pstmt7 = con.prepareStatement (
                     "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                     "AND stime2 <= ? AND etime2 >= ? AND (courseName = ? OR courseName = '-ALL-')");

                  error = "Check for Cross-Over Tees";

                  pstmt7.clearParameters();        // clear the parms
                  pstmt7.setLong(1, date);         // put the parms in stmt
                  pstmt7.setLong(2, date);
                  pstmt7.setInt(3, thistime);
                  pstmt7.setInt(4, thistime);
                  pstmt7.setString(5, course);
                  rs1 = pstmt7.executeQuery();      // check for cross-over time

                  loop3:
                  while (rs1.next()) {

                     dbl_recurr = rs1.getString(1);   // dbl tee found for this time

                     //
                     //  We must check the recurrence for this day (Monday, etc.)
                     //
                     if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                         (!name.equalsIgnoreCase( "saturday" )) &&
                         (!name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "saturday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }
                  }      // end of loop3 while

                  pstmt7.close();

               }     // end of 2nd double tee if

            }        // end of 'check dbl' if

            if (club.equals( "bigsprings" ) && (BSsaturday == true || BSholiday == true)) {

               fb = 2;                // do dbl tees (no cross-overs needed)
            }
              
            if (club.equals( "skokie" ) && (SkokieDay1 == true || SkokieDay2 == true)) {

               fb = 2;                // do dbl tees (no cross-overs needed)
            }

            if (club.equals( "nakoma" ) && name.equalsIgnoreCase( "thursday" )) {

               if (thistime > 1156 && thistime < 1357) { 

                  fb = 2;                           // do dbl tees (no cross-overs needed)

                  if (thistime == 1157) {           // if this is 11:57 AM

                     alt_betwn = 8;                 // then alternate between 9 and 8 minute intervals
                  }

                  if (thistime == 1356) {           // if this is 1:56 PM

                     alt_betwn = 0;                 // then stop alternate intervals
                  }
               }
            }
              
            //
            //  Done checking for double tees
            //
            //     fb:  0 = no dbl tees (build all front tees - 0)
            //          2 = dbl tees found (build both - 0 & 1)
            //          9 = cross over time (build cross-over tee time - 9)
            //
            pstmt3.setInt(9, first);          // set first indicator

            if (fb == 2) {                    // double tee (do both front and back for this time) ?

               pstmt3.setShort(10, fb0);      // do front 9 first

            } else {

               pstmt3.setShort(10, fb);        // 0 = front, 9 = cross-over

            }
            pstmt3.setString(11, course);              // course name

            error = "Add Entry To Teecurr ";

            //
            //  Put the entry in the teecurr2 table and do the next one
            //
            pstmt3.executeUpdate();        // execute the prepared stmt

            first = 0;             // no longer first slot

            //
            // if 'double tee' was found for this time, build a 2nd tee time for the back 9
            //
            if (fb == 2) {

               error = "Add Entry To Teecurr 2";

               pstmt3.setInt(9, first);       // set first indicator
               pstmt3.setShort(10, fb1);        // do back 9 (1)

               pstmt3.executeUpdate();        // execute the prepared stmt

            }

            //
            //  Manually set interval if Piedmont Driving Club
            //
            if (club.equals( "piedmont" )) {       // if Piedmont Driving Club

               if ((name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) ||
                    date == memDay1 || date == laborDay1) && count < 5) {
                    
                  min = min + 6;            // 1st 6 intervals on Sat & Sun are 6 minutes

               } else {

                  min = min + betwn;         // use normal interval (10)
               }

            } else {                  // NOT Piedmont

               //
               //******************************************************************************************
               //   Custom sheets for Wee Burn
               //
               //     Special 2-some times on Tuesdays & Wednesdays based on time of year (July 6th for 2006 only!)
               //
               //     NOTE:  this checks a range of dates that allows for Wednesdays changing each year!!!
               //            (i.e. instead of checking for 5/31, it checks the whole week)
               //
               //******************************************************************************************
               //
               if (club.equals( "weeburn" )) {

                  if (name.equals( "Wednesday" ) || date == 20060706) {       // only check 7/06 in 2006

                     if (shortDate > 419 && shortDate < 927) {       // April - Sept. (every Wed)

                        if ((shortDate > 524 && shortDate < 608) || date == 20060706) {

                           if (thistime == 800) {            // if we just did 8:00 AM

                              betwn = 6;                     // then switch to 6 min intervals

                           } else {

                              if (thistime == 1100) {        // if we just did 11:00 AM

                                 betwn = betwn2;             // then return to normal intervals
                              }
                           }

                        } else {

                           if ((shortDate > 823 && shortDate < 831) || (shortDate > 831 && shortDate < 907)) {

                              if (thistime == 800) {            // if we just did 8:00 AM

                                 betwn = 6;                     // then switch to 6 min intervals

                              } else {

                                 if (thistime == 1030) {        // if we just did 10:30 AM

                                    betwn = betwn2;             // then return to normal intervals
                                 }
                              }

                           } else {                             // all other Wednesdays in date range

                              if (thistime == 900) {            // if we just did 9:00 AM

                                 betwn = 6;                     // then switch to 6 min intervals

                              } else {

                                 if (thistime == 1030) {        // if we just did 10:30 AM

                                    betwn = betwn2;             // then return to normal intervals
                                 }
                              }
                           }
                        }
                     }
                  }
                    
                  if (name.equals( "Tuesday" )) {         // Tuesdays    

                     if ((shortDate > 502 && shortDate < 627) || (shortDate > 704 && shortDate < 920)) {    

                        if (thistime == 800) {            // if we just did 8:00 AM

                           betwn = 6;                     // then switch to 6 min intervals

                        } else {

                           if (thistime == 1030) {        // if we just did 10:30 AM

                              betwn = betwn2;             // then return to normal intervals
                           }
                        }
                     }
                  }

               }         // end of IF Weeburn


               if (club.equals( "westchester" ) && WCfiveMinDay == true) {

                  if (thistime == 1100) {           // if we just did 11:00 AM
                    
                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "newcanaan" ) && NCspec1 == true) {

                  if (thistime == 800) {            // if we just did 8:00 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }
               if (club.equals( "newcanaan" ) && NCspec2 == true) {

                  if (thistime == 730) {            // if we just did 7:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }
               if (club.equals( "newcanaan" ) && NCspec3 == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "apawamis" ) && APspec1 == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "tpctc" )) {

                  if (thistime == 900) {           // if we just did 9:00 AM

                     betwn = 9;                    // switch to 9 min intervals
                       
                  } else {

                     if (thistime == 1200) {          // if we just did 12:00 Noon

                        betwn = 10;                    // switch to 10 min intervals

                     } else {

                        if (thistime == 1400) {          // if we just did 2:00 PM

                           betwn = 9;                    // switch back to 9 min intervals
                        }
                     }
                  }
               }

               if (club.equals( "brooklawn" )) {

                  if (name.equals( "Tuesday" ) && thistime == 1104) {   // if we just did 11:04 AM

                     betwn = 9;                    // switch to 9 min intervals

                  } else {

                     if ((name.equals( "Saturday" ) || name.equals( "Sunday" )) && thistime == 1028) {   // if we just did 10:28 AM

                        betwn = 9;                    // switch to 9 min intervals
                     }
                  }
               }

               if (club.equals( "noaks" )) {

                  if (name.equals( "Tuesday" ) && thistime == 1154) {   // if we just did 11:54 AM

                     betwn = betwn2;                // then switch to normal intervals
                  } 
               }


               if (club.equals( "elniguelcc" )) {

                  if (name.equals( "Tuesday" ) && thistime == 956) {   // if we just did 9:56 AM

                     thistime = 952;              // change time so we skip to 10:00
                     hour = 9;
                     min = 52;

                  }

                  if (name.equals( "Tuesday" ) && thistime == 1000) {   // if we just did 10:00 AM

                     betwn = 7;                    // switch to 7 & 8 min intervals
                     alt_betwn = 8;
                     alt = 0;                      // init the alt switch

                  }

                  if (name.equals( "Thursday" ) && thistime == 1048) {   // if we just did 10:48 AM

                     thistime = 1052;              // change time so we skip to 11:00
                     hour = 10;
                     min = 52;
                  }

                  if (name.equals( "Thursday" ) && thistime == 1100) {   // if we just did 11:00 AM

                     betwn = 7;                    // switch to 7 & 8 min intervals
                     alt_betwn = 8;
                     alt = 0;                      // init the alt switch
                  }
               }

               if (club.equals( "woodway" ) && WWsixMinDay == true) {

                  if (thistime == 1100) {           // if we just did 11:00 AM

                     betwn = betwn2;                // then switch to normal intervals
                  }
               }

               if (club.equals( "bigsprings" ) && (BSsaturday == true || BSholiday == true)) {

                  if (thistime == 848) {           // if we just did 8:48 AM

                     thistime = 852;                // change time so we skip to 9:00
                     hour = 8;
                     min = 52;
                       
                  } else {

                     if (thistime == 948) {           // if we just did 9:48 AM

                        betwn = betwn2;                // then switch to normal intervals (10 mins)

                        if (BSsaturday == true) {
                          
                           thistime = 1210;                // change time so we skip to 12:20
                           hour = 12;
                           min = 10;
                           BSsaturday = false;             // quit doing dbl tees and custom
                        
                        } else {

                           if (BSholiday == true) {

                              thistime = 1220;                // change time so we skip to 12:30 (keep doing dbl tees)
                              hour = 12;
                              min = 20;
                           }
                        }
                          
                     } else {
                        
                        if (BSholiday == true && thistime == 1430) {   // if we just did 2:30 PM

                           thistime = 1650;                // change time so we skip to 5:00
                           hour = 16;
                           min = 50;
                           BSholiday = false;              // quit doing dbl tees and custom
                        }   
                     }
                  }
               }

               if (club.equals( "skokie" )) {        // Skokie custom

                  if (SkokieDay1 == true) {

                     if (thistime == 920) {           // if we just did 9:20 AM

                        betwn = betwn2;                // then switch to normal intervals (10 mins)
                        SkokieDay1 = false;            // quit doing dbl tees and custom
                     }

                  } else {

                     if (SkokieDay2 == true) {

                        if (thistime == 922) {           // if we just did 9:22 AM

                           betwn = betwn2;                // then switch to normal intervals (10 mins)
                           SkokieDay2 = false;            // quit doing dbl tees and custom
                           thistime = 920;                // change time so we skip to 9:30 
                           hour = 9;
                           min = 20;
                        }
                     }
                  }
               }

               if (club.equals( "hudsonnatl" ) && BSsaturday == true) {

                  if (thistime == 830) {           // if we just did 8:30 AM

                     betwn = betwn2;                // then switch to normal intervals (15 mins)
                  }
               }

               //
               //  The Stanwich Club - switch from 6 min ints to 8 min ints based on day
               //
               if (club.equals( "stanwichclub" )) {

                  if (name.equalsIgnoreCase( "saturday" )  || name.equalsIgnoreCase( "sunday" )  || name.equalsIgnoreCase( "monday" )) {

                     if (thistime == 824) {           // if we just did 8:24 AM

                        betwn = betwn2;                // then switch to normal intervals (8 mins)
                     }
                  }

                  if (name.equalsIgnoreCase( "tuesday" ) || name.equalsIgnoreCase( "wednesday" )) {

                     if (thistime == 1006) {           // if we just did 10:06 AM

                        betwn = betwn2;                // then switch to normal intervals (8 mins)
                     }
                  }
               }

               //
               //  Meadow Springs - 8 min intervals except last interval of each hour is 12 minutes
               //                   (i.e.  7:00 - 7:48 then skip to 8:00, etc.
               //
               if (club.equals( "meadowsprings" )) {        // Meadow Srings custom

                  if (min == 48) {                    // if xx:48 

                     thistime = thistime + 4;         // change time so we skip to xy:00
                     min = 52;
                  }
               }

               //
               //  Skaneateles - 10 min intervals except the following are 8 minutes
               //
               //       Mondays starting at Noon (1200), except for Memorial Day, 7/03 and Labor Day
               //       W/Es and Holidays at 11:00 AM       
               //
               if (club.equals( "skaneateles" )) {        // Skaneateles custom

                  if (name.equalsIgnoreCase( "monday" )) {                           // Monday ?

                     if (date != memDay1 && date != july3rd1 && date != laborDay1 && date != memDay2 && date != july3rd2 && date != laborDay2) {

                        if (thistime == 1200) {           // if we just did 12:00 PM (noon)

                           betwn = 8;                     // then switch to 8 min intervals 
                        }
                     }
                  }

                  if (name.equalsIgnoreCase( "saturday" ) || name.equalsIgnoreCase( "sunday" ) || date == july3rd1 || date == july3rd2 || 
                      date == memDay1 || date == july4th1 || date == laborDay1 || date == memDay2 || date == july4th2 || date == laborDay2) {

                     if (thistime == 1100) {           // if we just did 11:00 AM

                        betwn = 8;                     // then switch to 8 min intervals
                     }
                  }
               }

               //
               //  Tripoli - every hour has these times:  :00, :08, :17, :25, :34, :42, :51
               //
               if (club.equals( "tripoli" )) {        // Tripoli custom

                  if (min == 0) {

                     min = 8;        // next time
                       
                  } else {
                    
                     if (min == 8) {

                        min = 17;

                     } else {

                        if (min == 17) {

                           min = 25;

                        } else {

                           if (min == 25) {

                              min = 34;

                           } else {

                              if (min == 34) {

                                 min = 42;

                              } else {

                                 if (min == 42) {

                                    min = 51;

                                 } else {        // must have just done :51 - now start next hour (xx:00)

                                    min = 0;
                                    hour++;        // next hour
                                 }
                              }
                           }
                        }
                     }
                  }

               } else {       // not Tripoli (all other clubs)

                  //
                  //   Bump time to next interval
                  //
                  if (alt_betwn > 0) {        // if alternate minutes specified in parms

                     if (alt == 1) {          //  and this is an alternate time (every other)

                        min = min + alt_betwn;     // bump minutes by alt minutes between times
                        alt = 0;                   // toggle the alt switch

                     } else {

                        min = min + betwn;         // bump minutes by minutes between times
                        alt = 1;                   // toggle the alt switch
                     }
                  } else {

                     min = min + betwn;     // no alt minutes - bump minutes by minutes between times
                  }
                    
               }        // end of IF tripoli
            }           // end of IF piedmont

            if (min > 59) {

               min = min - 60;     // adjust past hour count
               hour = hour + 1;
            }

            count++;                     // bump tee time counter

            thistime = hour * 100;       // recalc thistime (hhmm)
            thistime = thistime + min;

         }   // end of while loop - build all slots for this day

         error = "Done - Close Stmts ";

         pstmt3.close();
      }             // end of IF xx = 0 (ready to build sheets)

   }
   catch (Exception e) {

      throw new Exception("Error Building New Tee Sheets - buildTee " + error + "  " + e.getMessage());
   }

 }


 //************************************************************************
 // buildHTee - Builds a custom tee sheet for Hazeltine Natl. for the date requested.
 //
 //   called by: scanTee (below) to build missing tee sheets
 //
 //************************************************************************

 public static void buildHTee(int year, int month, int day, int day_name, String course, Connection con)
         throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   String error = "None";

   //
   //  init some variables to be used in building the tee slots
   //
   int hour = 0;              // hour value for tee time slot
   int min = 0;              // minute value for slot
   int time = 0;
   int first = 0;
   int wday = 0;
   int length = 0;

   short xx = 0;
   short fb = 0;
   short fb0 = 0;
   short fb1 = 1;
   String dbl_recurr = "";
   String sfb = "";
   boolean check_dbl = false;

   //
   //   Array for day names
   //
   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //   Array for Weekday Times
   //
   int wdLength = 68; 
   int [] weekdays = { 724, 733, 742, 751, 800, 809, 818, 827, 836, 850, 900, 910, 920, 930, 940, 950, 1000, 
                       1010, 1020, 1030, 1040, 1100, 1110, 1120, 1130, 1140, 1150, 1200, 1210, 1220, 1230,
                       1240, 1250, 1300, 1310, 1320, 1330, 1340, 1350, 1400, 1410, 1420, 1438, 1447, 1456,
                       1505, 1514, 1523, 1532, 1541, 1556, 1605, 1614, 1623, 1632, 1641, 1650, 1659, 1708,
                       1717, 1726, 1735, 1744, 1753, 1802, 1811, 1820, 1829 };

   //
   //   Array for Weekend Front Times
   //
   int weLength = 72;
   int [] weekends = { 710, 719, 728, 737, 746, 755, 804, 813, 822, 831, 840, 849, 858, 907, 916, 925, 934, 943, 952,
                       1010, 1019, 1028, 1037, 1046, 1055, 1110, 1119, 1128, 1137, 1146, 1155, 1204, 1213, 1222,
                       1231, 1240, 1249, 1307, 1316, 1325, 1334, 1343, 1352, 1401, 1410, 1419, 1428, 1446, 1455,
                       1504, 1513, 1522, 1531, 1540, 1549, 1558, 1607, 1625, 1634, 1643, 1652, 1701, 1710,
                       1719, 1728, 1737, 1746, 1755, 1804, 1813, 1822, 1831 };

   //
   //   Array for Weekend Back Times
   //
   int weLength2 = 6;
   int [] weekend2 = { 800, 809, 818, 827, 836, 845 };

   //
   //  Gather all the info we need to build the table
   //
   long date = year * 10000;          // create a date field from input values
   date = date + (month * 100);
   date = date + day;                 // date = yyyymmdd (for comparisons)

   //
   //  day_name is an index representing the day of the week (01 = sun, 02 = mon, etc.)
   //
   if (day_name > 7) {

      throw new Exception("Invalid day_name value received - buildHTee");
   }

   String name = day_table[day_name];  // get name for day

   //
   //  Determine which tee sheet to build:  0 = weekday, 1 = weekend
   //
   //    weekends = Sun, Sat, Memorial Day, July 4th Monday, Labor Day
   //
   wday = 0;              // default to weekday values
   length = wdLength;       
     
   if (day_name == 1 || day_name == 7 || date == memDay1 || date == july4th1 || date == laborDay1 ||
       date == memDay2 || date == july4th2 || date == laborDay2) {

      wday = 1;            // weekend tee sheet
      length = weLength;
   }

   //
   //
   try {
      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT xx FROM clubparm2 WHERE courseName = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, course);
      rs2 = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs2.next()) {

         xx = rs2.getShort(1);
           
      } else {
        
         xx = 0;
      }                // end of if clubparms (for this course)
      pstmt1.close();

      //
      //  if we are to wait for initial setup, then skip
      //
      //  xx is set to zero in Proshop_parms when we set the club parms for the first time
      //  zero indicates we should wait for config completion before building the tees
      //
      if (xx != 0) {

         error = "Tee Times Exist?";

         //
         //  Check if any tee time slots already exist for this date on this course
         //
         PreparedStatement pstmt = con.prepareStatement (
            "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);         // put the parm in pstmt
         pstmt.setString(2, course);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         if (rs.next()) {               // if no tee times exist

            pstmt.close();
            return;                     // not ready yet
         }        
         pstmt.close();
           
         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         PreparedStatement pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', '', '', '', '', " +
            "'', '', '', '', '', '', ?, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 0, ?, '', '', '', 0, 0, '', 0, " +
            "'', ?, '', 0, 0, 0, 0, '', '', '', '', '', '', '', '', '', '', '', '', '', 0, 0, '', '', " +
            "0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '')");

         error = "Check Dbl Tees?";

         //
         // See if we need to check the dbltee table each time
         //
         PreparedStatement pstmt8 = con.prepareStatement (
                "SELECT name FROM dbltee2 WHERE sdate <= ? AND edate >= ? AND (courseName = ? OR courseName = '-ALL-')");

         pstmt8.clearParameters();        // clear the parms
         pstmt8.setLong(1, date);         // put the parm in stmt
         pstmt8.setLong(2, date);
         pstmt8.setString(3, course);
         rs1 = pstmt8.executeQuery();      // execute the prepared stmt

         if (rs1.next()) {

            check_dbl = true;    // we have to check dbltee table
         }

         pstmt8.close();


         first = 1;       // first slot indicator

         //
         //  Now we can build the tee time slots
         //
         for (int i = 0; i < length; i++) {           // length = # of times in appropriate array

            //
            //  get the time for this slot
            //
            if (wday == 0) {       // if weekday

               time = weekdays[i];     // get weekday time

            } else {

               time = weekends[i];     // get weekend time
            }
              
            hour = time / 100;
            min = time - (hour * 100);

            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
            pstmt3.setInt(2, month);
            pstmt3.setInt(3, day);
            pstmt3.setInt(4, year);
            pstmt3.setString(5, name);
            pstmt3.setInt(6, hour);
            pstmt3.setInt(7, min);
            pstmt3.setInt(8, time);

            //
            // check for double tees if there are any for this day
            //
            fb = 0;

            if (check_dbl) {             // must we check double tees?

               //
               // Prepared statement to search the double tees table for 'double tee' times
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                  "AND stime1 <= ? AND etime1 >= ? AND (courseName = ? OR courseName = '-ALL-')");

               error = "Check for Dbl Tees";

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setLong(1, date);         // put the parms in stmt
               pstmt6.setLong(2, date);
               pstmt6.setInt(3, time);
               pstmt6.setInt(4, time);
               pstmt6.setString(5, course);
               rs3 = pstmt6.executeQuery();      // see if Front and Back to be built

               loop2:
               while (rs3.next()) {

                  dbl_recurr = rs3.getString(1);   // dbl tee found for this time

                  //
                  //  We must check the recurrence for this day (Monday, etc.)
                  //
                  if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                      (!name.equalsIgnoreCase( "saturday" )) &&
                      (!name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "saturday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

                  if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                      (name.equalsIgnoreCase( "sunday" ))) {

                     fb = 2;                        // set double tees for this time
                     break loop2;                   // done checking - exit while loop
                  }

               }   // end of loop2 while loop

               pstmt6.close();

               if (fb == 0) {                        // if no hit, check 'no tees' times (cross-over)

                  //
                  // Prepared statement to search the double tees table for 'no tee' times (cross-overs)
                  //
                  PreparedStatement pstmt7 = con.prepareStatement (
                     "SELECT recurr FROM dbltee2 WHERE sdate <= ? AND edate >= ? " +
                     "AND stime2 <= ? AND etime2 >= ? AND (courseName = ? OR courseName = '-ALL-')");

                  error = "Check for Cross-Over Tees";

                  pstmt7.clearParameters();        // clear the parms
                  pstmt7.setLong(1, date);         // put the parms in stmt
                  pstmt7.setLong(2, date);
                  pstmt7.setInt(3, time);
                  pstmt7.setInt(4, time);
                  pstmt7.setString(5, course);
                  rs1 = pstmt7.executeQuery();      // check for cross-over time

                  loop3:
                  while (rs1.next()) {

                     dbl_recurr = rs1.getString(1);   // dbl tee found for this time

                     //
                     //  We must check the recurrence for this day (Monday, etc.)
                     //
                     if (dbl_recurr.equalsIgnoreCase( "every " + name )) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if (dbl_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekdays" )) &&
                         (!name.equalsIgnoreCase( "saturday" )) &&
                         (!name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "saturday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }

                     if ((dbl_recurr.equalsIgnoreCase( "all weekends" )) &&
                         (name.equalsIgnoreCase( "sunday" ))) {

                        fb = 9;                        // set double tees for this time
                        break loop3;                   // done checking - exit while loop
                     }
                  }      // end of loop3 while

                  pstmt7.close();

               }     // end of 2nd double tee if

            }        // end of 'check dbl' if

            //
            //  Done checking for double tees
            //
            //     fb:  0 = no dbl tees (build all front tees - 0)
            //          2 = dbl tees found (build both - 0 & 1)
            //          9 = cross over time (build cross-over tee time - 9)
            //
            pstmt3.setInt(9, first);          // set first indicator

            if (fb == 2) {                    // double tee (do both front and back for this time) ?

               pstmt3.setShort(10, fb0);      // do front 9 first

            } else {

               pstmt3.setShort(10, fb);        // 0 = front, 9 = cross-over

            }
            pstmt3.setString(11, course);              // course name

            error = "Add Entry To Teecurr ";

            //
            //  Put the entry in the teecurr2 table and do the next one
            //
            pstmt3.executeUpdate();        // execute the prepared stmt

            first = 0;             // no longer first slot

            //
            // if 'double tee' was found for this time, build a 2nd tee time for the back 9
            //
            if (fb == 2) {

               error = "Add Entry To Teecurr 2";

               pstmt3.setInt(9, first);       // set first indicator
               pstmt3.setShort(10, fb1);        // do back 9 (1)

               pstmt3.executeUpdate();        // execute the prepared stmt

            }

         }   // end of FOR loop - build all slots for this day

         //
         //  Now, if weekend tee sheet, build the back tees (6 of them)
         //
         if (wday == 1) {          // if weekend

            first = 0;             // no longer first slot
            fb = 1;                // back tees

            for (int i = 0; i < weLength2; i++) {    

               time = weekend2[i];              // get weekend back tee time

               hour = time / 100;
               min = time - (hour * 100);

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);         // put the parms in stmt for tee slot
               pstmt3.setInt(2, month);
               pstmt3.setInt(3, day);
               pstmt3.setInt(4, year);
               pstmt3.setString(5, name);
               pstmt3.setInt(6, hour);
               pstmt3.setInt(7, min);
               pstmt3.setInt(8, time);
               pstmt3.setInt(9, first);        // set first indicator
               pstmt3.setShort(10, fb);        // do back 9 (1)
               pstmt3.setString(11, course);   // course name

               pstmt3.executeUpdate();        // execute the prepared stmt
            }
         }
         pstmt3.close();

      }             // end of IF xx = 0 (ready to build sheets)

   }
   catch (Exception e) {

      throw new Exception("Error Building New Tee Sheets - buildHTee " + error + "  " + e.getMessage());
   }

 }


 //************************************************************************
 //  moveTee - moves the oldest tee sheet from teecurr2 to teepast2
 //
 //  called by:  scanTee if too many sheets on teecurr2
 //
 //************************************************************************

 public static void moveTee(Connection con, long today_date, String club)
           throws Exception {


   ResultSet rs = null;

   long date = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int hotelNew = 0;
   int hotelMod = 0;
   int gotsome = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int teecurr_id = 0;
   int pace_status_id = 0;


   String day = "";
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String notes = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String course = "";
   String orig_by = "";
   String conf = "";
   String blocker = "";
   String errorMsg = "";
  
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   short fb = 0;

   PreparedStatement pstmt;
   PreparedStatement pstmt1;
   PreparedStatement pstmt2;


   //
   //  Create table to hold empty tee times in case it has not already been added for this club
   //
   try {

      Statement stmt = con.createStatement();        // create a statement

      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teepastempty(" +
                         "date bigint, mm smallint, dd smallint, " +
                         "yy integer, day varchar(10), hr smallint, min smallint, time integer, " +
                         "event varchar(30), event_color varchar(24), restriction varchar(30), " +
                         "rest_color varchar(24), fb smallint, courseName varchar(30), " +
                         "proNew integer, proMod integer, memNew integer, memMod integer, " +
                         "hotelNew integer, hotelMod integer, orig_by varchar(15), conf varchar(15), " +
                         "index ind1 (date, time, fb, courseName), " +
                         "index ind2 (date, courseName))");

      stmt.close();

   }
   catch (SQLException e) {

      errorMsg = "Error001 in SystemUtils moveTee for club: " +club+ ". Exception= ";
      errorMsg = errorMsg + e.getMessage();                     // build error msg

      logError(errorMsg);                                       // log it and continue
   }

   //
   //  Get all tee sheets in teecurr2 older than today (one sheet per day)
   //
   //  ************* today_date has been adjusted for time zone by scanTee *********
   //
   try {

      pstmt1 = con.prepareStatement (
                "SELECT * FROM teecurr2 WHERE date < ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, today_date);   // put the parms in stmt for tee slot
      rs = pstmt1.executeQuery();      // get all tee slots older than today

      while (rs.next()) {

         teecurr_id = rs.getInt("teecurr_id");
         date = rs.getLong("date");
         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         day = rs.getString("day");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         time = rs.getInt("time");
         event = rs.getString("event");
         ecolor = rs.getString("event_color");
         rest = rs.getString("restriction");
         rcolor = rs.getString("rest_color");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         p1cw = rs.getString("p1cw");
         p2cw = rs.getString("p2cw");
         p3cw = rs.getString("p3cw");
         p4cw = rs.getString("p4cw");
         show1 = rs.getShort("show1");
         show2 = rs.getShort("show2");
         show3 = rs.getShort("show3");
         show4 = rs.getShort("show4");
         fb = rs.getShort("fb");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         p5cw = rs.getString("p5cw");
         show5 = rs.getShort("show5");
         notes = rs.getString("notes");
         course = rs.getString("courseName");
         blocker = rs.getString("blocker");
         proNew = rs.getInt("proNew");
         proMod = rs.getInt("proMod");
         memNew = rs.getInt("memNew");
         memMod = rs.getInt("memMod");
         mNum1 = rs.getString("mNum1");
         mNum2 = rs.getString("mNum2");
         mNum3 = rs.getString("mNum3");
         mNum4 = rs.getString("mNum4");
         mNum5 = rs.getString("mNum5");
         userg1 = rs.getString("userg1");
         userg2 = rs.getString("userg2");
         userg3 = rs.getString("userg3");
         userg4 = rs.getString("userg4");
         userg5 = rs.getString("userg5");
         hotelNew = rs.getInt("hotelNew");
         hotelMod = rs.getInt("hotelMod");
         orig_by = rs.getString("orig_by");
         conf = rs.getString("conf");
         p91 = rs.getInt("p91");
         p92 = rs.getInt("p92");
         p93 = rs.getInt("p93");
         p94 = rs.getInt("p94");
         p95 = rs.getInt("p95");
         pace_status_id = rs.getInt("pace_status_id");


         //
         //  If the tee time is not empty, then save it in teepast
         //
         if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {

            gotsome = 1;         // indicate at least one tee time

            try {

               //
               //  Setup the prepared stmt for teepast
               //
               pstmt = con.prepareStatement (
                  "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                  "restriction, rest_color, player1, player2, player3, player4, username1, " +
                  "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, fb, " +
                  "player5, username5, p5cw, show5, courseName, proNew, proMod, memNew, memMod, " +
                  "mNum1, mNum2, mNum3, mNum4, mNum5, userg1, userg2, userg3, userg4, userg5, hotelNew, " +
                  "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95, teecurr_id, pace_status_id) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
               pstmt.setInt(2, mm);
               pstmt.setInt(3, dd);
               pstmt.setInt(4, yy);
               pstmt.setString(5, day);
               pstmt.setInt(6, hr);
               pstmt.setInt(7, min);
               pstmt.setInt(8, time);
               pstmt.setString(9, event);
               pstmt.setString(10, ecolor);
               pstmt.setString(11, rest);
               pstmt.setString(12, rcolor);
               pstmt.setString(13, player1);
               pstmt.setString(14, player2);
               pstmt.setString(15, player3);
               pstmt.setString(16, player4);
               pstmt.setString(17, user1);
               pstmt.setString(18, user2);
               pstmt.setString(19, user3);
               pstmt.setString(20, user4);
               pstmt.setString(21, p1cw);
               pstmt.setString(22, p2cw);
               pstmt.setString(23, p3cw);
               pstmt.setString(24, p4cw);
               pstmt.setShort(25, show1);
               pstmt.setShort(26, show2);
               pstmt.setShort(27, show3);
               pstmt.setShort(28, show4);
               pstmt.setShort(29, fb);
               pstmt.setString(30, player5);
               pstmt.setString(31, user5);
               pstmt.setString(32, p5cw);
               pstmt.setShort(33, show5);
               pstmt.setString(34, course);
               pstmt.setInt(35, proNew);
               pstmt.setInt(36, proMod);
               pstmt.setInt(37, memNew);
               pstmt.setInt(38, memMod);
               pstmt.setString(39, mNum1);
               pstmt.setString(40, mNum2);
               pstmt.setString(41, mNum3);
               pstmt.setString(42, mNum4);
               pstmt.setString(43, mNum5);
               pstmt.setString(44, userg1);
               pstmt.setString(45, userg2);
               pstmt.setString(46, userg3);
               pstmt.setString(47, userg4);
               pstmt.setString(48, userg5);
               pstmt.setInt(49, hotelNew);
               pstmt.setInt(50, hotelMod);
               pstmt.setString(51, orig_by);
               pstmt.setString(52, conf);
               pstmt.setString(53, notes);
               pstmt.setInt(54, p91);
               pstmt.setInt(55, p92);
               pstmt.setInt(56, p93);
               pstmt.setInt(57, p94);
               pstmt.setInt(58, p95);
               pstmt.setInt(59, teecurr_id);
               pstmt.setInt(60, pace_status_id);

               pstmt.executeUpdate();        // move the tee slot to teepast

               pstmt.close();

            }
            catch (Exception exc2) {

               errorMsg = "Error1 in SystemUtils moveTee for club: " +club+ ". Exception= ";
               errorMsg = errorMsg + exc2.getMessage();                                // build error msg

               logError(errorMsg);                                       // log it

               throw new Exception("Error reading Oldest Tee Sheet - moveTee: " + exc2.getMessage());
            }
               
         } else {         
           
            //
            //  tee time is empty - move it to the old empty tee time table (teepastempty)
            //
            if (blocker.equals( "" )) {         // if tee time not blocked

               try {

                  //
                  //  Setup the prepared stmt for teepast
                  //
                  pstmt = con.prepareStatement (
                     "INSERT INTO teepastempty (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
                     "restriction, rest_color, fb, courseName, " +
                     "proNew, proMod, memNew, memMod, " +
                     "hotelNew, hotelMod, orig_by, conf) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setLong(1, date);         // put the parms in pstmt for tee slot
                  pstmt.setInt(2, mm);
                  pstmt.setInt(3, dd);
                  pstmt.setInt(4, yy);
                  pstmt.setString(5, day);
                  pstmt.setInt(6, hr);
                  pstmt.setInt(7, min);
                  pstmt.setInt(8, time);
                  pstmt.setString(9, event);
                  pstmt.setString(10, ecolor);
                  pstmt.setString(11, rest);
                  pstmt.setString(12, rcolor);
                  pstmt.setShort(13, fb);
                  pstmt.setString(14, course);
                  pstmt.setInt(15, proNew);
                  pstmt.setInt(16, proMod);
                  pstmt.setInt(17, memNew);
                  pstmt.setInt(18, memMod);
                  pstmt.setInt(19, hotelNew);
                  pstmt.setInt(20, hotelMod);
                  pstmt.setString(21, orig_by);
                  pstmt.setString(22, conf);

                  pstmt.executeUpdate();        // move the tee slot to teepast

                  pstmt.close();

               }
               catch (Exception exc2) {

                  errorMsg = "Error1b in SystemUtils moveTee for club: " +club+ ". Exception= ";
                  errorMsg = errorMsg + exc2.getMessage();                                // build error msg

                  logError(errorMsg);                                       // log it

                  throw new Exception("Error moving empty tee time - moveTee: " + exc2.getMessage());
               }
            }
  
         }     // end of if empty test

      }   // end of while loop

      pstmt1.close();

      //
      //   Gather the stats from this tee sheet and place in the stats table
      //
      if (gotsome != 0) {       // if any tee times are present

         moveStats(con, club);         // go build stats

         //
         // If club is Cordillera or Catamount, go build a report file for them for the past day's tee sheet
         //
         if (club.equals( "cordillera" ) || club.equals( "catamount" )) {

            buildCordilleraReport(con, club, today_date);         // go build report file
         }
      }

      //
      //  Remove the oldest day's tee slots (rows) from teecurr2 table
      //
      try {

         pstmt2 = con.prepareStatement (
                      "DELETE FROM teecurr2 WHERE date < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

      }
      catch (SQLException exc1) {

         errorMsg = "Error2 in SystemUtils moveTee for club: " +club+ ". Exception= ";
         errorMsg = errorMsg + exc1.getMessage();                                // build error msg

         logError(errorMsg);                                       // log it

         throw new Exception("Error removing Oldest Tee Sheet - moveTee: " + exc1.getMessage());
      }

      //
      //  Remove any old lottery requests (general clean up)
      //
      try {

         pstmt2 = con.prepareStatement (
                      "DELETE FROM lreqs3 WHERE date < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

         pstmt2 = con.prepareStatement (
                      "DELETE FROM actlott3 WHERE pdate < ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
         pstmt2.executeUpdate();          // delete all tee slots for oldest date

         pstmt2.close();

      }
      catch (SQLException exc1) {

         errorMsg = "Error3 in SystemUtils moveTee for club: " +club+ ". Exception= ";
         errorMsg = errorMsg + exc1.getMessage();                                // build error msg

         logError(errorMsg);                                       // log it

         throw new Exception("Error removing Old Lottery Requests - moveTee: " + exc1.getMessage());
      }

   }
   catch (SQLException e) {

      errorMsg = "Error4 in SystemUtils moveTee for club: " +club+ ". Exception= ";
      errorMsg = errorMsg + e.getMessage();                                // build error msg

      logError(errorMsg);                                       // log it

      throw new Exception("Error Getting Old Tee Sheets in teecurr2 - moveTee: " + e.getMessage());
   }

 }  // end of moveTee


 // *********************************************************
 //  buildCordilleraReport - create a custom report file
 //                          for Coridllera and Catamount.
 //                          These files will be ftp'd nightly
 //                          to their server.
 // *********************************************************

   private static void buildCordilleraReport(Connection con, String club, long today_date) {


   PreparedStatement pstmt1;
   ResultSet rs = null;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int mCount = 0;
   int i = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String gtype = "";
   String course = "";
   String sdate = "";
   String stime = "";
   String ampm = "";
   String record = "";
   String fname = "";

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //   Get guest types for this club
   //
   try {
      getClub.getParms(con, parm);
   }
   catch (Exception e1) {
   }

   //
   //  Arrays for guests
   //
   int [] gCounts = new int [parm.MAX_Guests];     // # of each guest type in tee time


   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }


   //
   //  Get all tee times in teecurr2 older than today
   //
   try {

      pstmt1 = con.prepareStatement (
                "SELECT mm, dd, yy, hr, min, player1, player2, player3, player4, username1, username2, username3, username4, " +
                "show1, show2, show3, show4, player5, username5, show5, courseName " +
                "FROM teecurr2 WHERE date < ? ORDER BY date, time");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, today_date);   // put the parms in stmt for tee slot
      rs = pstmt1.executeQuery();      // get all tee slots older than today

      while (rs.next()) {

         mm = rs.getInt("mm");
         dd = rs.getInt("dd");
         yy = rs.getInt("yy");
         hr = rs.getInt("hr");
         min = rs.getInt("min");
         player1 = rs.getString("player1");
         player2 = rs.getString("player2");
         player3 = rs.getString("player3");
         player4 = rs.getString("player4");
         user1 = rs.getString("username1");
         user2 = rs.getString("username2");
         user3 = rs.getString("username3");
         user4 = rs.getString("username4");
         show1 = rs.getShort("show1");
         show2 = rs.getShort("show2");
         show3 = rs.getShort("show3");
         show4 = rs.getShort("show4");
         player5 = rs.getString("player5");
         user5 = rs.getString("username5");
         show5 = rs.getShort("show5");
         course = rs.getString("courseName");

         //
         //  If the tee time is not empty, then process it
         //
         if (!player1.equals("") || !player2.equals("") || !player3.equals("") || !player4.equals("") || !player5.equals("")) {

            //
            //  init counters
            //
            mCount = 0;

            for (i = 0; i < parm.MAX_Guests; i++) {

               gCounts[i] = 0;
            }

            //
            //  Process each player
            //
            if (!player1.equals("") && show1 == 1) {        // if player exists and played

               if (!user1.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop1:
                  while (i < parm.MAX_Guests) {

                     if (player1.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop1;
                     }
                     i++;
                  }
               }
            }

            if (!player2.equals("") && show2 == 1) {        // if player exists

               if (!user2.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop2:
                  while (i < parm.MAX_Guests) {

                     if (player2.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop2;
                     }
                     i++;
                  }
               }
            }

            if (!player3.equals("") && show3 == 1) {        // if player exists

               if (!user3.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop3:
                  while (i < parm.MAX_Guests) {

                     if (player3.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop3;
                     }
                     i++;
                  }
               }
            }

            if (!player4.equals("") && show4 == 1) {        // if player exists

               if (!user4.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop4:
                  while (i < parm.MAX_Guests) {

                     if (player4.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop4;
                     }
                     i++;
                  }
               }
            }

            if (!player5.equals("") && show5 == 1) {        // if player exists

               if (!user5.equals("")) {       // if member

                  mCount++;                   // bump # of members in tee time

               } else {

                  i = 0;
                  ploop5:
                  while (i < parm.MAX_Guests) {

                     if (player5.startsWith( parm.guest[i] )) {

                        gCounts[i]++;          // count # of this guest type
                        break ploop5;
                     }
                     i++;
                  }
               }
            }

            //
            //  Now build the record(s):
            //            date, time, course, # of players, player type
            //
            fname = club + "_" + mm + "-" + dd + "-" + yy;     // i.e.  cordillera_8-17-2006.csv (.csv added below)

            if (dd < 10) {

               sdate = mm + "/0" + dd + "/" + yy;         // mm/dd/yyyy

            } else {

               sdate = mm + "/" + dd + "/" + yy;         // mm/dd/yyyy
            }

            ampm = " AM";
            if (hr > 12) {

               ampm = " PM";
               hr = hr - 12;       // convert from military time
            }
            if (hr == 12) {

               ampm = " PM";
            }
            if (hr == 0) {

               hr = 12;
               ampm = " AM";
            }

            if (min < 10) {

               stime = hr + ":0" + min + ampm;

            } else {

               stime = hr + ":" + min + ampm;
            }

            //
            //  Build the member record if any members
            //
            if (mCount > 0) {

               record = sdate + "," + stime + "," + course + "," + mCount + ",Member";

               addCordilleraRecord(record, fname);       // add the record to the file
            }

            //
            //  Build the guest records if any guests
            //
            i = 0;
            while (i < parm.MAX_Guests) {

               if (gCounts[i] > 0) {        // if any of this guest type

                  gtype = parm.guest[i];    // get the guest type

                  record = sdate + "," + stime + "," + course + "," + gCounts[i] + ",Guest: " + gtype;

                  addCordilleraRecord(record, fname);       // add the record to the file
               }
               i++;
            }

         }     // end of if empty test

      }   // end of while loop

      pstmt1.close();

   }
   catch (SQLException e) {

      String errorMsg = "Error in SystemUtils.buildCordilleraReport for club: " +club+ ". Exception= ";
      errorMsg = errorMsg + e.getMessage();                                // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of buildCordilleraReport


 // *********************************************************
 //  addCordilleraRecord - build a report file and add a record
 // *********************************************************

   private static void addCordilleraRecord(String record, String fname) {


   int fail = 0;

   String dirname = "//home//reports//cordillera//";   // create directory name
   String filename = fname + ".csv";                   // create full file name
   String fileDest = dirname + filename;               // destination (temp file)


   try {
      //
      //  Dir path for the real server
      //
      PrintWriter fout1 = new PrintWriter(new FileWriter(fileDest, true));

      //
      //  Put record in text file
      //
      fout1.print(record);
      fout1.println();                            // output the line

      fout1.close();

   }
   catch (Exception e2) {

      fail = 1;
   }

   //
   //  if above failed, try local pc
   //
   if (fail != 0) {

      dirname = "c:\\java\\tomcat\\webapps\\" +rev+ "\\reports\\cordillera\\";     // create directory name
      fileDest = dirname + filename;

      try {
         //
         //  dir path for test pc
         //
         PrintWriter fout = new PrintWriter(new FileWriter(fileDest, true));

         //
         //  Put record in text file
         //
         fout.print(record);
         fout.println();                              // output the line

         fout.close();

      }
      catch (Exception ignore) {
      }
   }

 }  // end of addCordilleraRecord


 // *********************************************************
 //  moveStats - gather stats from teecurr before moving them and put in stats table
 // *********************************************************

   private static void moveStats(Connection con, String club) {


   Statement stmt = null;
   ResultSet rs = null;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //   Get multi option, member types, and guest types
   //
   try {
      getClub.getParms(con, parm);
   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      String errorMsg = "Error1 in SystemUtils moveStats for club " +club+ ": ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

   int i  = 0;
   int i2  = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;
   int multi = 0;                 // multiple course support
   int index= 0;
   int count2 = 0;                 // number of courses

   long date = 0;


   //
   //  Array to hold the course names
   //
   String [] course = new String [20];                     // max of 20 courses per club

   String courseName = "";        // course names
   String ind = "move";           // from here

   //
   //   Get Yesterday's date
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   cal.add(Calendar.DATE,-1);                     // get yesterday's date
   yy = cal.get(Calendar.YEAR);
   mm = cal.get(Calendar.MONTH);
   dd = cal.get(Calendar.DAY_OF_MONTH);

   mm++;                         // month starts at zero

   date = (yy * 10000) + (mm * 100) + dd;                     // Yesterday

   //
   //   Check for multiple courses
   //
   multi = parm.multi;
   count2 = 1;                  // init to 1 course

   if (multi != 0) {           // if multiple courses supported for this club

      while (index < 20) {

         course[index] = "";       // init the course array
         index++;
      }

      index = 0;

      try {
         //
         //  Get the names of all courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && index < 20) {

            courseName = rs.getString(1);

            course[index] = courseName;      // add course name to array
            index++;
         }
         stmt.close();
         count2 = index;                      // number of courses
      }
      catch (Exception exc) {
         //
         //  save error message in /" +rev+ "/error.txt
         //
         String errorMsg2 = "Error2 in SystemUtils moveStats for club " +club+ ": ";
         errorMsg2 = errorMsg2 + exc.getMessage();                              // build error msg

         logError(errorMsg2);                                       // log it
      }
   }

   courseName = "";            // init as not multi
   index = 0;

   //
   // execute searches and display for each course (count2 = number of courses, or 1 if only one)
   //
   while (count2 > 0) {

      if (multi != 0) {           // if multiple courses supported for this club

         courseName = course[index];      // get first course name
      }

      moveStatsCom(date, ind, courseName, con, club);   // go process the stats

      count2--;           // decrement # of courses
      index++;            // bump course index
   }                  
 }


 // *****************************************************************************
 //  moveStatsCom - gather stats from teecurr or teepast and put in stats table
 //
 //  called by:  moveStats (above)
 //              Proshop_oldsheets
 // *****************************************************************************

   public static void moveStatsCom(long date, String ind, String courseName, Connection con, String club) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmt4 = null;


   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   //
   //  parm block to hold the course parameters
   //
   parmCourse parmc = new parmCourse();          // allocate a parm block

   //
   //   Get multi option, member types, and guest types
   //
   try {
      getClub.getParms(con, parm);
   }
   catch (Exception e1) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      String errorMsg = "Error1 in SystemUtils moveStats for club " +club+ ": ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

   boolean guest = false;
   boolean found = false;

   long year = 0;
   long month = 0;
   long day = 0;
     
   int i  = 0;
   int i2  = 0;
   int mm  = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int count = 0;

   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   int mem9Unknown = 0;
   int mship9Unknown = 0;
   int mem18Unknown = 0;
   int mship18Unknown = 0;

   int cart9Rounds = 0;
   int cady9Rounds = 0;
   int wa9Rounds = 0;
   int pc9Rounds = 0;
   int cart18Rounds = 0;
   int cady18Rounds = 0;
   int wa18Rounds = 0;
   int pc18Rounds = 0;

   int [] tmode9R = new int [parm.MAX_Tmodes];       // modes of trans (up to 16)
   int [] tmode18R = new int [parm.MAX_Tmodes];

   int memnoshow9 = 0;
   int memnoshow18 = 0;
   int gstnoshow9 = 0;
   int gstnoshow18 = 0;

   int other9Rounds = 0;
   int other18Rounds = 0;

   int [] memxRounds9 = new int [parm.MAX_Mems];
   int [] memxRounds18 = new int [parm.MAX_Mems];

   int [] mshipxRounds9 = new int [parm.MAX_Mships];
   int [] mshipxRounds18 = new int [parm.MAX_Mships];

   //
   //  Arrays for guest rounds
   //
   int [] g9Rounds = new int [parm.MAX_Guests];     // guest 9 hole rounds
   int [] g18Rounds = new int [parm.MAX_Guests];    // guest 18 hole rounds

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
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";

   String mtype = "";
   String mship = "";

   //
   //   Remove any guest types that are null - for tests below
   //
   for (i = 0; i < parm.MAX_Guests; i++) {

      if (parm.guest[i].equals( "" )) {

         parm.guest[i] = "$@#!^&*";      // make so it won't match player name
      }
   }

   //
   //   Get date parms for records
   //
   year = date / 10000;                             // get year
   month = (date - (year * 10000)) / 100;              // get month
   day = (date - (year * 10000)) - (month * 100);       // get day

   mm = (int)month;
   dd = (int)day;
   yy = (int)year;

   //
   // use the date and course to search the tee times tables
   //
   //
   //  init count fields for each course
   //
   mem9Unknown = 0;
   mship9Unknown = 0;
   mem18Unknown = 0;
   mship18Unknown = 0;

   cart9Rounds = 0;
   cady9Rounds = 0;
   wa9Rounds = 0;
   pc9Rounds = 0;
   cart18Rounds = 0;
   cady18Rounds = 0;
   wa18Rounds = 0;
   pc18Rounds = 0;

   memnoshow9 = 0;
   memnoshow18 = 0;
   gstnoshow9 = 0;
   gstnoshow18 = 0;

   other9Rounds = 0;
   other18Rounds = 0;

   for (i=0; i<parm.MAX_Mems; i++) {
      memxRounds9[i] = 0;
      memxRounds18[i] = 0;
   }

   for (i=0; i<parm.MAX_Mships; i++) {
      mshipxRounds9[i] = 0;
      mshipxRounds18[i] = 0;
   }

   //
   //  init the guest round arrays
   //
   for (i = 0; i < parm.MAX_Guests; i++) {
      g9Rounds[i] = 0;
      g18Rounds[i] = 0;
   }

   //
   //  Init the Modes of Trans arrays
   //
   for (i = 0; i < parm.MAX_Tmodes; i++) {
      tmode9R[i] = 0;
      tmode18R[i] = 0;
   }

   //
   //   Get tee times for the date provided
   //
   try {

      //
      //  Get the System Parameters for this Course
      //
      getParms.getCourse(con, parmc, courseName);

      //
      //  build an entry in the stats table
      //
      pstmt2 = con.prepareStatement (
         "INSERT INTO stats5 (date, year, month, day, course, " +
          "mem1Rounds9, mem1Rounds18, mem2Rounds9, mem2Rounds18, " +
          "mem3Rounds9, mem3Rounds18, mem4Rounds9, mem4Rounds18, " +
          "mem5Rounds9, mem5Rounds18, mem6Rounds9, mem6Rounds18, " +
          "mem7Rounds9, mem7Rounds18, mem8Rounds9, mem8Rounds18, " +
          "mem9Rounds9, mem9Rounds18, mem10Rounds9, mem10Rounds18, " +
          "mem11Rounds9, mem11Rounds18, mem12Rounds9, mem12Rounds18, " +
          "mem13Rounds9, mem13Rounds18, mem14Rounds9, mem14Rounds18, " +
          "mem15Rounds9, mem15Rounds18, mem16Rounds9, mem16Rounds18, " +
          "mem17Rounds9, mem17Rounds18, mem18Rounds9, mem18Rounds18, " +
          "mem19Rounds9, mem19Rounds18, mem20Rounds9, mem20Rounds18, " +
          "mem21Rounds9, mem21Rounds18, mem22Rounds9, mem22Rounds18, " +
          "mem23Rounds9, mem23Rounds18, mem24Rounds9, mem24Rounds18, " +
          "mship1Rounds9, mship1Rounds18, mship2Rounds9, mship2Rounds18, " +
          "mship3Rounds9, mship3Rounds18, mship4Rounds9, mship4Rounds18, " +
          "mship5Rounds9, mship5Rounds18, mship6Rounds9, mship6Rounds18, " +
          "mship7Rounds9, mship7Rounds18, mship8Rounds9, mship8Rounds18, " +
          "mship9Rounds9, mship9Rounds18, mship10Rounds9, mship10Rounds18, " +
          "mship11Rounds9, mship11Rounds18, mship12Rounds9, mship12Rounds18, " +
          "mship13Rounds9, mship13Rounds18, mship14Rounds9, mship14Rounds18, " +
          "mship15Rounds9, mship15Rounds18, mship16Rounds9, mship16Rounds18, " +
          "mship17Rounds9, mship17Rounds18, mship18Rounds9, mship18Rounds18, " +
          "mship19Rounds9, mship19Rounds18, mship20Rounds9, mship20Rounds18, " +
          "mship21Rounds9, mship21Rounds18, mship22Rounds9, mship22Rounds18, " +
          "mship23Rounds9, mship23Rounds18, mship24Rounds9, mship24Rounds18, " +
          "gst1Rounds9, gst1Rounds18, gst2Rounds9, gst2Rounds18, " +
          "gst3Rounds9, gst3Rounds18, gst4Rounds9, gst4Rounds18, " +
          "gst5Rounds9, gst5Rounds18, gst6Rounds9, gst6Rounds18, " +
          "gst7Rounds9, gst7Rounds18, gst8Rounds9, gst8Rounds18, " +
          "gst9Rounds9, gst9Rounds18, gst10Rounds9, gst10Rounds18, " +
          "gst11Rounds9, gst11Rounds18, gst12Rounds9, gst12Rounds18, " +
          "gst13Rounds9, gst13Rounds18, gst14Rounds9, gst14Rounds18, " +
          "gst15Rounds9, gst15Rounds18, gst16Rounds9, gst16Rounds18, " +
          "gst17Rounds9, gst17Rounds18, gst18Rounds9, gst18Rounds18, " +
          "gst19Rounds9, gst19Rounds18, gst20Rounds9, gst20Rounds18, " +
          "gst21Rounds9, gst21Rounds18, gst22Rounds9, gst22Rounds18, " +
          "gst23Rounds9, gst23Rounds18, gst24Rounds9, gst24Rounds18, " +
          "gst25Rounds9, gst25Rounds18, gst26Rounds9, gst26Rounds18, " +
          "gst27Rounds9, gst27Rounds18, gst28Rounds9, gst28Rounds18, " +
          "gst29Rounds9, gst29Rounds18, gst30Rounds9, gst30Rounds18, " +
          "gst31Rounds9, gst31Rounds18, gst32Rounds9, gst32Rounds18, " +
          "gst33Rounds9, gst33Rounds18, gst34Rounds9, gst34Rounds18, " +
          "gst35Rounds9, gst35Rounds18, gst36Rounds9, gst36Rounds18, " +
          "tmode1R9, tmode1R18, tmode2R9, tmode2R18, tmode3R9, tmode3R18, tmode4R9, tmode4R18, " +
          "tmode5R9, tmode5R18, tmode6R9, tmode6R18, tmode7R9, tmode7R18, tmode8R9, tmode8R18, " +
          "tmode9R9, tmode9R18, tmode10R9, tmode10R18, tmode11R9, tmode11R18, tmode12R9, tmode12R18, " +
          "tmode13R9, tmode13R18, tmode14R9, tmode14R18, tmode15R9, tmode15R18, tmode16R9, tmode16R18, " +
          "otherRounds9, otherRounds18, cartsRounds9, cartsRounds18, " +
          "caddyRounds9, caddyRounds18, pullcartRounds9, pullcartRounds18, " +
          "walkRounds9, walkRounds18, memnoshow9, memnoshow18, " +
          "gstnoshow9, gstnoshow18, mem9unknown, mem18unknown, mship9unknown, mship18unknown) " +
          "VALUES (?,?,?,?,?," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0," +
          "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, date);
      pstmt2.setInt(2, yy);
      pstmt2.setInt(3, mm);
      pstmt2.setInt(4, dd);
      pstmt2.setString(5, courseName);
      pstmt2.executeUpdate();          // execute the prepared stmt

      pstmt2.close();

      //
      //  if call from moveTee, then build new stats entry and get stats from teecurr, else rebuild stats from teepast
      //
      if (ind.equals( "move" )) {
        
         //
         //  Gather the stats for yesterday
         //
         pstmt1 = con.prepareStatement (
            "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, " +
            "player5, username5, p5cw, show5, p91, p92, p93, p94, p95 " +
            "FROM teecurr2 WHERE date = ? AND courseName = ?");

      } else {                // rebuild from teepast

         //
         //  Gather the stats for date provided from old sheets
         //
         pstmt1 = con.prepareStatement (
            "SELECT player1, player2, player3, player4, username1, username2, username3, username4, " +
            "p1cw, p2cw, p3cw, p4cw, show1, show2, show3, show4, " +
            "player5, username5, p5cw, show5, p91, p92, p93, p94, p95 " +
            "FROM teepast2 WHERE date = ? AND courseName = ?");
      }

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setLong(1, date);
      pstmt1.setString(2, courseName);
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      while (rs.next()) {

         player1 = rs.getString(1);
         player2 = rs.getString(2);
         player3 = rs.getString(3);
         player4 = rs.getString(4);
         username1 = rs.getString(5);
         username2 = rs.getString(6);
         username3 = rs.getString(7);
         username4 = rs.getString(8);
         p1cw = rs.getString(9);
         p2cw = rs.getString(10);
         p3cw = rs.getString(11);
         p4cw = rs.getString(12);
         show1 = rs.getInt(13);
         show2 = rs.getInt(14);
         show3 = rs.getInt(15);
         show4 = rs.getInt(16);
         player5 = rs.getString(17);
         username5 = rs.getString(18);
         p5cw = rs.getString(19);
         show5 = rs.getInt(20);
         p91 = rs.getInt(21);
         p92 = rs.getInt(22);
         p93 = rs.getInt(23);
         p94 = rs.getInt(24);
         p95 = rs.getInt(25);

         if ((!player1.equals( "" )) && (!player1.equalsIgnoreCase( "x" ))) {

            guest = false;
            i = 0;

            ploop1:
            while (i < parm.MAX_Guests) {

               if (player1.startsWith( parm.guest[i] )) {

                  guest = true;
                  break ploop1;
               }
               i++;
            }

            if (guest == true) {

               if (show1 != 1) {           // if guest is a no-show

                  if (p91 == 1) {                          // 9 holes

                     gstnoshow9++;

                  } else {

                     gstnoshow18++;
                  }

               } else {                    // guest was a show

                  if (p91 == 1) {                          // 9 holes

                     g9Rounds[i]++;

                  } else {

                     g18Rounds[i]++;                       // 18 holes
                  }

                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop1a:
                  while (i < parm.MAX_Tmodes) {

                     if (p1cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p91 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop1a;
                     }
                     i++;
                  }

               }         // end of IF guest is a show

            } else {     // else not a guest

               //
               //  player must be a member
               //
               if (show1 != 1) {           // if member is a no-show

                  if (p91 == 1) {                          // 9 holes

                     memnoshow9++;

                  } else {

                     memnoshow18++;
                  }

               } else {                     // member is a show

                  if (username1.equals( "" )) {               // unrecognized name

                     if (p91 == 1) {                          // 9 holes

                        other9Rounds++;

                     } else {

                        other18Rounds++;                       // 18 holes
                     }

                  } else {

                     //
                     //  Member - get member type and membership type for this member
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE " +
                        "username = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, username1);
                     rs2 = pstmt4.executeQuery();

                     if (rs2.next()) {

                        mship = rs2.getString(1);
                        mtype = rs2.getString(2);

                        //
                        //  Process the Membership Type (mship)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mships; i++) {

                           if (mship.equalsIgnoreCase( parm.mship[i] )) {

                              if (p91 == 1) {              // 9 holes

                                 mshipxRounds9[i]++;

                              } else {

                                 mshipxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                          if (p91 == 1) {              // 9 holes

                             mship9Unknown++;

                          } else {

                             mship18Unknown++;
                          }
                        }
                        //
                        //  Process the Member Type (mtype)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mems; i++) {

                           if (mtype.equalsIgnoreCase( parm.mem[i] )) {

                              if (p91 == 1) {              // 9 holes

                                 memxRounds9[i]++;

                              } else {

                                 memxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                           if (p91 == 1) {              // 9 holes

                              mem9Unknown++;

                           } else {

                              mem18Unknown++;
                           }
                        }

                     } else {         // username not found in member table

                        if (p91 == 1) {              // 9 holes

                           other9Rounds++;

                        } else {

                           other18Rounds++;
                        }
                     }
                     pstmt4.close();
                  }

                  //
                  //  Member is a show
                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop1b:
                  while (i < parm.MAX_Tmodes) {
                     if (p1cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p91 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop1b;
                     }
                     i++;
                  }
               }    // end of IF no-show
            }       // end of IF guest or member
         }          // end of IF player is null or x

         if ((!player2.equals( "" )) && (!player2.equalsIgnoreCase( "x" ))) {

            guest = false;
            i = 0;

            ploop2:
            while (i < parm.MAX_Guests) {

               if (player2.startsWith( parm.guest[i] )) {

                  guest = true;
                  break ploop2;
               }
               i++;
            }

            if (guest == true) {

               if (show2 != 1) {           // if guest is a no-show

                  if (p92 == 1) {              // 9 holes

                     gstnoshow9++;

                  } else {

                     gstnoshow18++;
                  }

               } else {                    // guest was a show

                  if (p92 == 1) {              // 9 holes

                     g9Rounds[i]++;

                  } else {

                     g18Rounds[i]++;                       // 18 holes
                  }

                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop2a:
                  while (i < parm.MAX_Tmodes) {
                     if (p2cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p92 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop2a;
                     }
                     i++;
                  }
               }         // end of IF guest is a show

            } else {     // else not a guest

               //
               //  player must be a member
               //
               if (show2 != 1) {           // if member is a no-show

                  if (p92 == 1) {              // 9 holes

                     memnoshow9++;

                  } else {

                     memnoshow18++;
                  }

               } else {                     // member is a show

                  if (username2.equals( "" )) {               // unrecognized name

                     if (p92 == 1) {              // 9 holes

                        other9Rounds++;

                     } else {

                        other18Rounds++;                       // 18 holes
                     }

                  } else {

                     //
                     //  Member - get member type and membership type for this member
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE " +
                        "username = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, username2);
                     rs2 = pstmt4.executeQuery();

                     if (rs2.next()) {

                        mship = rs2.getString(1);
                        mtype = rs2.getString(2);

                        //
                        //  Process the Membership Type (mship)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mships; i++) {

                           if (mship.equalsIgnoreCase( parm.mship[i] )) {

                              if (p92 == 1) {              // 9 holes

                                 mshipxRounds9[i]++;

                              } else {

                                 mshipxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                          if (p92 == 1) {              // 9 holes

                             mship9Unknown++;

                          } else {

                             mship18Unknown++;
                          }
                        }
                        //
                        //  Process the Member Type (mtype)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mems; i++) {

                           if (mtype.equalsIgnoreCase( parm.mem[i] )) {

                              if (p92 == 1) {              // 9 holes

                                 memxRounds9[i]++;

                              } else {

                                 memxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                           if (p92 == 1) {              // 9 holes

                              mem9Unknown++;

                           } else {

                              mem18Unknown++;
                           }
                        }

                     } else {         // username not found in member table

                        if (p92 == 1) {              // 9 holes

                           other9Rounds++;

                        } else {

                           other18Rounds++;
                        }
                     }
                     pstmt4.close();
                  }

                  //
                  //  Member is a show
                  //
                  i = 0;
                  loop2b:
                  while (i < parm.MAX_Tmodes) {
                     if (p2cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p92 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop2b;
                     }
                     i++;
                  }
               }    // end of IF no-show
            }       // end of IF guest or member
         }          // end of IF player is null or x

         if ((!player3.equals( "" )) && (!player3.equalsIgnoreCase( "x" ))) {

            guest = false;
            i = 0;

            ploop3:
            while (i < parm.MAX_Guests) {

               if (player3.startsWith( parm.guest[i] )) {

                  guest = true;
                  break ploop3;
               }
               i++;
            }

            if (guest == true) {

               if (show3 != 1) {           // if guest is a no-show

                  if (p93 == 1) {              // 9 holes

                     gstnoshow9++;

                  } else {

                     gstnoshow18++;
                  }

               } else {                    // guest was a show

                  if (p93 == 1) {              // 9 holes

                     g9Rounds[i]++;

                  } else {

                     g18Rounds[i]++;                       // 18 holes
                  }

                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop3a:
                  while (i < parm.MAX_Tmodes) {
                     if (p3cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p93 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop3a;
                     }
                     i++;
                  }

               }         // end of IF guest is a show

            } else {     // else not a guest

               //
               //  player must be a member
               //
               if (show3 != 1) {           // if member is a no-show

                  if (p93 == 1) {              // 9 holes

                     memnoshow9++;

                  } else {

                     memnoshow18++;
                  }

               } else {                     // member is a show

                  if (username3.equals( "" )) {               // unrecognized name

                     if (p93 == 1) {              // 9 holes

                        other9Rounds++;

                     } else {

                        other18Rounds++;                       // 18 holes
                     }

                  } else {

                     //
                     //  Member - get member type and membership type for this member
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE " +
                        "username = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, username3);
                     rs2 = pstmt4.executeQuery();

                     if (rs2.next()) {

                        mship = rs2.getString(1);
                        mtype = rs2.getString(2);

                        //
                        //  Process the Membership Type (mship)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mships; i++) {

                           if (mship.equalsIgnoreCase( parm.mship[i] )) {

                              if (p93 == 1) {              // 9 holes

                                 mshipxRounds9[i]++;

                              } else {

                                 mshipxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                          if (p93 == 1) {              // 9 holes

                             mship9Unknown++;

                          } else {

                             mship18Unknown++;
                          }
                        }
                        //
                        //  Process the Member Type (mtype)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mems; i++) {

                           if (mtype.equalsIgnoreCase( parm.mem[i] )) {

                              if (p93 == 1) {              // 9 holes

                                 memxRounds9[i]++;

                              } else {

                                 memxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                           if (p93 == 1) {              // 9 holes

                              mem9Unknown++;

                           } else {

                              mem18Unknown++;
                           }
                        }

                     } else {         // username not found in member table

                        if (p93 == 1) {              // 9 holes

                           other9Rounds++;

                        } else {

                           other18Rounds++;
                        }
                     }
                     pstmt4.close();
                  }

                  //
                  //  Member is a show
                  //
                  i = 0;
                  loop3b:
                  while (i < parm.MAX_Tmodes) {
                     if (p3cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p93 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop3b;
                     }
                     i++;
                  }
               }    // end of IF no-show
            }       // end of IF guest or member
         }          // end of IF player is null or x

         if ((!player4.equals( "" )) && (!player4.equalsIgnoreCase( "x" ))) {

            guest = false;
            i = 0;

            ploop4:
            while (i < parm.MAX_Guests) {

               if (player4.startsWith( parm.guest[i] )) {

                  guest = true;
                  break ploop4;
               }
               i++;
            }

            if (guest == true) {

               if (show4 != 1) {           // if guest is a no-show

                  if (p94 == 1) {              // 9 holes

                     gstnoshow9++;

                  } else {

                     gstnoshow18++;
                  }

               } else {                    // guest was a show

                  if (p94 == 1) {              // 9 holes

                     g9Rounds[i]++;

                  } else {

                     g18Rounds[i]++;                       // 18 holes
                  }

                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop4a:
                  while (i < parm.MAX_Tmodes) {
                     if (p4cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p94 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop4a;
                     }
                     i++;
                  }

               }         // end of IF guest is a show

            } else {     // else not a guest

               //
               //  player must be a member
               //
               if (show4 != 1) {           // if member is a no-show

                  if (p94 == 1) {              // 9 holes

                     memnoshow9++;

                  } else {

                     memnoshow18++;
                  }

               } else {                     // member is a show

                  if (username4.equals( "" )) {               // unrecognized name

                     if (p94 == 1) {              // 9 holes

                        other9Rounds++;

                     } else {

                        other18Rounds++;                       // 18 holes
                     }

                  } else {

                     //
                     //  Member - get member type and membership type for this member
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE " +
                        "username = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, username4);
                     rs2 = pstmt4.executeQuery();

                     if (rs2.next()) {

                        mship = rs2.getString(1);
                        mtype = rs2.getString(2);

                        //
                        //  Process the Membership Type (mship)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mships; i++) {

                           if (mship.equalsIgnoreCase( parm.mship[i] )) {

                              if (p94 == 1) {              // 9 holes

                                 mshipxRounds9[i]++;

                              } else {

                                 mshipxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                          if (p94 == 1) {              // 9 holes

                             mship9Unknown++;

                          } else {

                             mship18Unknown++;
                          }
                        }
                        //
                        //  Process the Member Type (mtype)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mems; i++) {

                           if (mtype.equalsIgnoreCase( parm.mem[i] )) {

                              if (p94 == 1) {              // 9 holes

                                 memxRounds9[i]++;

                              } else {

                                 memxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                           if (p94 == 1) {              // 9 holes

                              mem9Unknown++;

                           } else {

                              mem18Unknown++;
                           }
                        }

                     } else {         // username not found in member table

                        if (p94 == 1) {              // 9 holes

                           other9Rounds++;

                        } else {

                           other18Rounds++;
                        }
                     }
                     pstmt4.close();
                  }

                  //
                  //  Member is a show
                  //
                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop4b:
                  while (i < parm.MAX_Tmodes) {
                     if (p4cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p94 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop4b;
                     }
                     i++;
                  }
               }    // end of IF no-show
            }       // end of IF guest or member
         }          // end of IF player is null or x

         if ((!player5.equals( "" )) && (!player5.equalsIgnoreCase( "x" ))) {

            guest = false;
            i = 0;

            ploop5:
            while (i < parm.MAX_Guests) {

               if (player5.startsWith( parm.guest[i] )) {

                  guest = true;
                  break ploop5;
               }
               i++;
            }

            if (guest == true) {

               if (show5 != 1) {           // if guest is a no-show

                  if (p95 == 1) {              // 9 holes

                     gstnoshow9++;

                  } else {

                     gstnoshow18++;
                  }

               } else {                    // guest was a show

                  if (p95 == 1) {              // 9 holes

                     g9Rounds[i]++;

                  } else {

                     g18Rounds[i]++;                       // 18 holes
                  }

                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop5a:
                  while (i < parm.MAX_Tmodes) {
                     if (p5cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p95 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop5a;
                     }
                     i++;
                  }

               }         // end of IF guest is a show

            } else {     // else not a guest

               //
               //  player must be a member
               //
               if (show5 != 1) {           // if member is a no-show

                  if (p95 == 1) {              // 9 holes

                     memnoshow9++;

                  } else {

                     memnoshow18++;
                  }

               } else {                     // member is a show

                  if (username5.equals( "" )) {               // unrecognized name

                     if (p95 == 1) {              // 9 holes

                        other9Rounds++;

                     } else {

                        other18Rounds++;                       // 18 holes
                     }

                  } else {

                     //
                     //  Member - get member type and membership type for this member
                     //
                     pstmt4 = con.prepareStatement (
                        "SELECT m_ship, m_type FROM member2b WHERE " +
                        "username = ?");

                     pstmt4.clearParameters();
                     pstmt4.setString(1, username5);
                     rs2 = pstmt4.executeQuery();

                     if (rs2.next()) {

                        mship = rs2.getString(1);
                        mtype = rs2.getString(2);

                        //
                        //  Process the Membership Type (mship)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mships; i++) {

                           if (mship.equalsIgnoreCase( parm.mship[i] )) {

                              if (p95 == 1) {              // 9 holes

                                 mshipxRounds9[i]++;

                              } else {

                                 mshipxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                          if (p95 == 1) {              // 9 holes

                             mship9Unknown++;

                          } else {

                             mship18Unknown++;
                          }
                        }
                        //
                        //  Process the Member Type (mtype)
                        //
                        found = false;
                        for (i = 0; i < parm.MAX_Mems; i++) {

                           if (mtype.equalsIgnoreCase( parm.mem[i] )) {

                              if (p95 == 1) {              // 9 holes

                                 memxRounds9[i]++;

                              } else {

                                 memxRounds18[i]++;
                              }
                              found = true;
                           }
                        }

                        if (found == false) {

                           if (p95 == 1) {              // 9 holes

                              mem9Unknown++;

                           } else {

                              mem18Unknown++;
                           }
                        }

                     } else {         // username not found in member table

                        if (p95 == 1) {              // 9 holes

                           other9Rounds++;

                        } else {

                           other18Rounds++;
                        }
                     }
                     pstmt4.close();
                  }

                  //
                  //  Member is a show
                  //
                  //
                  // check all modes of trans
                  //
                  i = 0;
                  loop5b:
                  while (i < parm.MAX_Tmodes) {
                     if (p5cw.equals( parmc.tmodea[i] )) {   // if matches mode of trans

                        if (p95 == 1) {                          // 9 holes

                           tmode9R[i]++;

                        } else {

                           tmode18R[i]++;
                        }
                        break loop5b;
                     }
                     i++;
                  }
               }    // end of IF no-show
            }       // end of IF guest or member

         }      // end of IF username exists

      }         // end of while this day and course

      pstmt1.close();

      //
      //  Now store the stats
      //
      pstmt3 = con.prepareStatement (
         "UPDATE stats5 SET " +
          "mem1Rounds9=?, mem1Rounds18=?, mem2Rounds9=?, mem2Rounds18=?, " +
          "mem3Rounds9=?, mem3Rounds18=?, mem4Rounds9=?, mem4Rounds18=?, " +
          "mem5Rounds9=?, mem5Rounds18=?, mem6Rounds9=?, mem6Rounds18=?, " +
          "mem7Rounds9=?, mem7Rounds18=?, mem8Rounds9=?, mem8Rounds18=?, " +
          "mem9Rounds9=?, mem9Rounds18=?, mem10Rounds9=?, mem10Rounds18=?, " +
          "mem11Rounds9=?, mem11Rounds18=?, mem12Rounds9=?, mem12Rounds18=?, " +
          "mem13Rounds9=?, mem13Rounds18=?, mem14Rounds9=?, mem14Rounds18=?, " +
          "mem15Rounds9=?, mem15Rounds18=?, mem16Rounds9=?, mem16Rounds18=?, " +
          "mem17Rounds9=?, mem17Rounds18=?, mem18Rounds9=?, mem18Rounds18=?, " +
          "mem19Rounds9=?, mem19Rounds18=?, mem20Rounds9=?, mem20Rounds18=?, " +
          "mem21Rounds9=?, mem21Rounds18=?, mem22Rounds9=?, mem22Rounds18=?, " +
          "mem23Rounds9=?, mem23Rounds18=?, mem24Rounds9=?, mem24Rounds18=?, " +
          "mship1Rounds9=?, mship1Rounds18=?, mship2Rounds9=?, mship2Rounds18=?, " +
          "mship3Rounds9=?, mship3Rounds18=?, mship4Rounds9=?, mship4Rounds18=?, " +
          "mship5Rounds9=?, mship5Rounds18=?, mship6Rounds9=?, mship6Rounds18=?, " +
          "mship7Rounds9=?, mship7Rounds18=?, mship8Rounds9=?, mship8Rounds18=?, " +
          "mship9Rounds9=?, mship9Rounds18=?, mship10Rounds9=?, mship10Rounds18=?, " +
          "mship11Rounds9=?, mship11Rounds18=?, mship12Rounds9=?, mship12Rounds18=?, " +
          "mship13Rounds9=?, mship13Rounds18=?, mship14Rounds9=?, mship14Rounds18=?, " +
          "mship15Rounds9=?, mship15Rounds18=?, mship16Rounds9=?, mship16Rounds18=?, " +
          "mship17Rounds9=?, mship17Rounds18=?, mship18Rounds9=?, mship18Rounds18=?, " +
          "mship19Rounds9=?, mship19Rounds18=?, mship20Rounds9=?, mship20Rounds18=?, " +
          "mship21Rounds9=?, mship21Rounds18=?, mship22Rounds9=?, mship22Rounds18=?, " +
          "mship23Rounds9=?, mship23Rounds18=?, mship24Rounds9=?, mship24Rounds18=?, " +
          "gst1Rounds9=?, gst1Rounds18=?, gst2Rounds9=?, gst2Rounds18=?, " +
          "gst3Rounds9=?, gst3Rounds18=?, gst4Rounds9=?, gst4Rounds18=?, " +
          "gst5Rounds9=?, gst5Rounds18=?, gst6Rounds9=?, gst6Rounds18=?, " +
          "gst7Rounds9=?, gst7Rounds18=?, gst8Rounds9=?, gst8Rounds18=?, " +
          "gst9Rounds9=?, gst9Rounds18=?, gst10Rounds9=?, gst10Rounds18=?, " +
          "gst11Rounds9=?, gst11Rounds18=?, gst12Rounds9=?, gst12Rounds18=?, " +
          "gst13Rounds9=?, gst13Rounds18=?, gst14Rounds9=?, gst14Rounds18=?, " +
          "gst15Rounds9=?, gst15Rounds18=?, gst16Rounds9=?, gst16Rounds18=?, " +
          "gst17Rounds9=?, gst17Rounds18=?, gst18Rounds9=?, gst18Rounds18=?, " +
          "gst19Rounds9=?, gst19Rounds18=?, gst20Rounds9=?, gst20Rounds18=?, " +
          "gst21Rounds9=?, gst21Rounds18=?, gst22Rounds9=?, gst22Rounds18=?, " +
          "gst23Rounds9=?, gst23Rounds18=?, gst24Rounds9=?, gst24Rounds18=?, " +
          "gst25Rounds9=?, gst25Rounds18=?, gst26Rounds9=?, gst26Rounds18=?, " +
          "gst27Rounds9=?, gst27Rounds18=?, gst28Rounds9=?, gst28Rounds18=?, " +
          "gst29Rounds9=?, gst29Rounds18=?, gst30Rounds9=?, gst30Rounds18=?, " +
          "gst31Rounds9=?, gst31Rounds18=?, gst32Rounds9=?, gst32Rounds18=?, " +
          "gst33Rounds9=?, gst33Rounds18=?, gst34Rounds9=?, gst34Rounds18=?, " +
          "gst35Rounds9=?, gst35Rounds18=?, gst36Rounds9=?, gst36Rounds18=?, " +
          "tmode1R9=?, tmode1R18=?, tmode2R9=?, tmode2R18=?, tmode3R9=?, tmode3R18=?, tmode4R9=?, tmode4R18=?, " +
          "tmode5R9=?, tmode5R18=?, tmode6R9=?, tmode6R18=?, tmode7R9=?, tmode7R18=?, tmode8R9=?, tmode8R18=?, " +
          "tmode9R9=?, tmode9R18=?, tmode10R9=?, tmode10R18=?, tmode11R9=?, tmode11R18=?, tmode12R9=?, tmode12R18=?, " +
          "tmode13R9=?, tmode13R18=?, tmode14R9=?, tmode14R18=?, tmode15R9=?, tmode15R18=?, tmode16R9=?, tmode16R18=?, " +
          "otherRounds9=?, otherRounds18=?, cartsRounds9=?, cartsRounds18=?, " +
          "caddyRounds9=?, caddyRounds18=?, pullcartRounds9=?, pullcartRounds18=?, " +
          "walkRounds9=?, walkRounds18=?, memnoshow9=?, memnoshow18=?, " +
          "gstnoshow9=?, gstnoshow18=?, mem9unknown=?, mem18unknown=?, mship9unknown=?, mship18unknown=? " +
          "WHERE date=? AND course=?");

      pstmt3.clearParameters();        // clear the parms
      i2 = 1;
      for (i = 0; i < parm.MAX_Mems; i++) {
         pstmt3.setInt(i2, memxRounds9[i]);
         i2++;
         pstmt3.setInt(i2, memxRounds18[i]);
         i2++;
      }
      for (i = 0; i < parm.MAX_Mships; i++) {
         pstmt3.setInt(i2, mshipxRounds9[i]);
         i2++;
         pstmt3.setInt(i2, mshipxRounds18[i]);
         i2++;
      }
      for (i = 0; i < parm.MAX_Guests; i++) {
         pstmt3.setInt(i2, g9Rounds[i]);
         i2++;
         pstmt3.setInt(i2, g18Rounds[i]);
         i2++;
      }
      for (i = 0; i < parm.MAX_Tmodes; i++) {
         pstmt3.setInt(i2, tmode9R[i]);
         i2++;
         pstmt3.setInt(i2, tmode18R[i]);
         i2++;
      }
      pstmt3.setInt(i2, other9Rounds);
      i2++;
      pstmt3.setInt(i2, other18Rounds);
      i2++;
      pstmt3.setInt(i2, cart9Rounds);
      i2++;
      pstmt3.setInt(i2, cart18Rounds);
      i2++;
      pstmt3.setInt(i2, cady9Rounds);
      i2++;
      pstmt3.setInt(i2, cady18Rounds);
      i2++;
      pstmt3.setInt(i2, pc9Rounds);
      i2++;
      pstmt3.setInt(i2, pc18Rounds);
      i2++;
      pstmt3.setInt(i2, wa9Rounds);
      i2++;
      pstmt3.setInt(i2, wa18Rounds);
      i2++;
      pstmt3.setInt(i2, memnoshow9);
      i2++;
      pstmt3.setInt(i2, memnoshow18);
      i2++;
      pstmt3.setInt(i2, gstnoshow9);
      i2++;
      pstmt3.setInt(i2, gstnoshow18);
      i2++;
      pstmt3.setInt(i2, mem9Unknown);
      i2++;
      pstmt3.setInt(i2, mem18Unknown);
      i2++;
      pstmt3.setInt(i2, mship9Unknown);
      i2++;
      pstmt3.setInt(i2, mship18Unknown);
      i2++;

      pstmt3.setLong(i2, date);
      i2++;
      pstmt3.setString(i2, courseName);
      pstmt3.executeUpdate();          // execute the prepared stmt

      pstmt3.close();

   }
   catch (Exception exc) {
      //
      //  save error message in /" +rev+ "/error.txt
      //
      String errorMsg3 = "Error3 in SystemUtils moveStatsCom for club " +club+ ": ";
      errorMsg3 = errorMsg3 + exc.getMessage();                               // build error msg

      logError(errorMsg3);                                       // log it
   }

 }


 //************************************************************************
 //  teeTimer - Calls scanTee and then resets timer.
 //
 //     called by: TeeTimer (when timer expires)
 //
 //************************************************************************

 public static void teeTimer()
                          throws Exception {


   Connection con = null;
   Connection con2 = null;
   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (server_id == server_master) {

      //
      //  Perform timer function for each club in the system database 'clubs' table
      //
      String club = rev;                       // get db name to use for 'clubs' table

      try {
         con = dbConn.Connect(club);                       // get a connection
      }
      catch (Exception e1) {

         String errorMsg1 = "Error1 in SystemUtils teeTimer: ";
         errorMsg1 = errorMsg1 + e1.getMessage();                                // build error msg

         logError(errorMsg1);                                       // log it
      }

      if (con != null) {

         //
         // Get the club names from the 'clubs' table
         //
         //  Process each club in the table
         //
         try {

            stmt = con.createStatement();              // create a statement

            rs = stmt.executeQuery("SELECT clubname FROM clubs");

            while (rs.next()) {

               club = rs.getString(1);                 // get a club name

               con2 = dbConn.Connect(club);                   // get a connection to this club's db

               if (con2 != null) {

                  b = scanTee(con2, club);                // build new tee sheets for each club

                  optimize(con2);                         // Optimize this club's db tables (every Wed)

                  con2.close();                           // close the connection to the club db

               } else {
                  String errorMsg3 = "Error3 in SystemUtils teeTimer: Connection failed to " +club;
                  logError(errorMsg3);                                       // log it
               }
            }
              
            //
            //   Get today's date
            //
            Calendar cal = new GregorianCalendar();        // get todays date
            long yy = cal.get(Calendar.YEAR);
            long mm = cal.get(Calendar.MONTH) +1;
            long dd = cal.get(Calendar.DAY_OF_MONTH);

            scanDate = (yy * 10000) + (mm * 100) + dd;     // save it

         }
         catch (Exception e2) {

            String errorMsg2 = "Error2 in SystemUtils teeTimer: ";
            errorMsg2 = errorMsg2 + e2.getMessage();                                // build error msg

            logError(errorMsg2);                                       // log it
         }
         stmt.close();

         try {
            con.close();                              // close the connection to the system db
         }
         catch (Exception ignored) {
         }
      }
   }

   TeeTimer t_timer = new TeeTimer();            // reset timer to keep building tee sheets daily

 }



 //************************************************************************
 //  scanTee - Scans teecurr2 to make sure there are 365 days of tee sheets.
 //            This is done in case the server goes down and our timer
 //            does not expire.
 //
 //    called by:  Login (when Proshop logs in)
 //                Proshop_buildTees
 //                teeTimer (above when timer expires)
 //
 //    calls:   buildTee (above)
 //             moveTee
 //             updateTeecurr
 //
 //************************************************************************

 public static boolean scanTee(Connection con, String club)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String course = "";

   long date = 0;

   int year = 0;
   int month = 0;
   int day = 0;
   int day_name = 0;
   int time = 0;
   int hr = 0;
   int min = 0;
   int i = 0;

   short xx = 0;
   short skip = 0;

   boolean doneSome = false;

   Calendar cal = null;    


   //
   //  Get the 'build tees' parm from clubparm table - check if we should wait to build tee times
   //
   try {
      stmt = con.createStatement();        // create a statement

      rs2 = stmt.executeQuery("SELECT courseName, xx FROM clubparm2 WHERE first_hr != 0");

      while (rs2.next()) {

         course = rs2.getString(1);
         xx = rs2.getShort(2);

         if (xx != 0) {                                    // if course ready for tee sheets

            //
            //  start at the end of period and work backwards (faster)!!!!!!!!!!!
            //
            cal = new GregorianCalendar();        // get todays date
            cal.add(Calendar.DATE,365);                    // roll ahead one year !!!!!!

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            day_name = cal.get(Calendar.DAY_OF_WEEK);
            hr = cal.get(Calendar.HOUR_OF_DAY);
            min = cal.get(Calendar.MINUTE);

            //
            //  Build the 'time' string for display
            //
            //    Adjust the time based on the club's time zone (we are Central)
            //
            time = (hr * 100) + min;

            time = adjustTime(con, time);       // adjust for time zone

            if (time < 0) {                // if negative, then we went back or ahead one day

               time = 0 - time;          // convert back to positive value

               if (time < 100) {           // if hour is zero, then we rolled ahead 1 day
                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH);
                  day = cal.get(Calendar.DAY_OF_MONTH);
                  day_name = cal.get(Calendar.DAY_OF_WEEK);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH);
                  day = cal.get(Calendar.DAY_OF_MONTH);
                  day_name = cal.get(Calendar.DAY_OF_WEEK);
               }
            }

            month = month + 1;                            // month starts at zero

            //
            //  Create numeric date for movetee (today's date)
            //
            date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

            //
            //  check all 365 days
            //
            i = 0;
            loopb1:
            while (i < 366) {        // go 1 extra for today

               skip = 0;
               //
               //  See if tee sheet already exist for this course and day
               //
               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT fb FROM teecurr2 WHERE date = ? AND courseName = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setString(2, course);
               rs = pstmt.executeQuery();      // execute the prepared stmt

               if (rs.next()) {                // if tee times exist

                  skip = 1;                    // found one - we're done checking this course
               }

               pstmt.close();
               i++;


               if (skip == 0) {             // check all days !!!!! 6/14/2006

//               if (skip == 1) {

//                  break loopb1;                // get out of loop - we're done with this course
//               }

                  //
                  //  If Hazeltine, build custom tee sheets
                  //
                  if (club.equals( "hazeltine" )) {
                     buildHTee(year, month, day, day_name, course, con);   // go build tee sheet for this day
                  } else {
                     buildTee(year, month, day, day_name, course, club, con);  // go build tee sheet for this day
                  }

                  doneSome = true;
               }                          

               cal.add(Calendar.DATE,-1);                 // roll back one day
               day = cal.get(Calendar.DAY_OF_MONTH);      // get new day
               month = cal.get(Calendar.MONTH);
               year = cal.get(Calendar.YEAR);             // get year
               day_name = cal.get(Calendar.DAY_OF_WEEK);  // get name of new day (01 - 07)

               month = month + 1;                               // adjust our new month
               date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

            }   // end of while loop (check all 365 days)

         }      // end of if ready
      }         // end of while more courses
      stmt.close();

   }
   catch (Exception e2) {

      String errorMsg1 = "Error1 in SystemUtils scanTee for club: " +club+ ". Exception= ";
      errorMsg1 = errorMsg1 + e2.getMessage();                                // build error msg

      logError(errorMsg1);                                       // log it

      throw new Exception("Error reading Club Parameters - scanTee Exception: " + e2.getMessage());
   }

   //
   //  Get today's date and time
   //
   cal = new GregorianCalendar();       
   year = cal.get(Calendar.YEAR);
   month = cal.get(Calendar.MONTH);
   day = cal.get(Calendar.DAY_OF_MONTH);
   day_name = cal.get(Calendar.DAY_OF_WEEK);
   hr = cal.get(Calendar.HOUR_OF_DAY);
   min = cal.get(Calendar.MINUTE);

   //
   //    Create numeric date for movetee (today's date - adjusted for time zone)
   //    Adjust the time based on the club's time zone (we are Central)
   //
   time = (hr * 100) + min;

   time = adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value

      if (time < 100) {           // if hour is zero, then we rolled ahead 1 day
         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
      }
   }

   month = month + 1;                            // month starts at zero

   date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)


   //
   //  Now move any old tee sheets from teecurr to teepast
   //
   try {

      //
      //   Move old tee sheets if more than 365 on teecurr2
      //
      moveTee(con, date, club);

   }
   catch (Exception e3) {

      String errorMsg2 = "Error2 in SystemUtils scanTee for " +club+ ". Exception= ";
      errorMsg2 = errorMsg2 + e3.getMessage();                                // build error msg

      logError(errorMsg2);                                       // log it

      throw new Exception("scanTee - Error from moveTee -  " + e3.getMessage());
   }

   if (doneSome == true) {             // if we built any new tee times

      try {

         //
         //  call updateTeecurr to add any restrictions, etc. to the new tee times.
         //
         updateTeecurr(con);

         //
         //   Remove any old blockers from the lesson books for this club
         //
         removeLessonBlockers(con, date, club);

      }
      catch (Exception e3) {

         String errorMsg3 = "Error3 in SystemUtils scanTee for " +club+ ". Exception= ";
         errorMsg3 = errorMsg3 + e3.getMessage();                                // build error msg

         logError(errorMsg3);                                       // log it

         throw new Exception("scanTee - Error from updateTeecurr -  " + e3.getMessage());
      }
   }
     
   //
   //  Delete any old session log entries
   //
   try {
     
      cal = new GregorianCalendar();                 // get todays date again for movetee
      cal.add(Calendar.DATE,-7);                     // get last week's date

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH) +1;
      day = cal.get(Calendar.DAY_OF_MONTH);

      date = (year * 10000) + (month * 100) + day;     // date = yyyymmdd (for comparisons)

      PreparedStatement pstmt3 = con.prepareStatement (
          "DELETE FROM sessionlog " +
          "WHERE date < ?");

      //
      //  execute the prepared statement to update the tee time slot
      //
      pstmt3.clearParameters();        // clear the parms
      pstmt3.setLong(1, date);
      pstmt3.executeUpdate();

      pstmt3.close();

   }
   catch (Exception ignore) {
   }


   return(doneSome);

 }  // end of scanTee


 //************************************************************************
 //  buildDblTee - Builds double tees into the tee sheets.
 //
 //    called by:  Proshop_adddbltee
 //                Proshop_editdbltee
 //
 //
 //    returns status:  0 = no tee times exist
 //                     1 = double tees built
 //                     2 = double tees built, some had problems
 //                     9 = double tee not found - nothing done
 //
 //************************************************************************

 public static int buildDblTee(String name, Connection con)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String course = "";
   String courseName = "";
   String recurr = "";
   String day = "";
   String day_name = "";
   String statement = "";
   String statement2 = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";

   long date = 0;
   long sdate = 0;
   long edate = 0;

   int status = 0;
   int time = 0;
   int stime1 = 0;
   int etime1 = 0;
   int stime2 = 0;
   int etime2 = 0;

   short fb = 0;
   short ok = 0;
   short skip = 0;


   //
   //  Get the 'double tee' parms for the name passed
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
               "SELECT * FROM dbltee2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in pstmt1
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong("sdate");
         stime1 = rs.getInt("stime1");
         edate = rs.getLong("edate");
         etime1 = rs.getInt("etime1");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         recurr = rs.getString("recurr");
         course = rs.getString("courseName");
           
      } else {

         ok = 1;        // not ok to proceed
         status = 9;    // error - dbl tee not found
      }

      pstmt1.close();              // close the stmt

      if (ok == 0) {             // if ok to proceed
        
         //
         //  Use this info to scan the tee times and build the double tees whenever possible
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }
         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }
         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }
         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }
         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }
         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }
         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //   Statements to use based on the double tee config - for adding 'Back 9' tee time
         //
         String ps1 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1+ " AND " +
                      "courseName = '" +course+ "'";                                           // day & course

         String ps2 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                             // day & any course

         String ps3 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                 // every day & course

         String ps4 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                            // every day & any course

         String ps5 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                   // weekdays & course

         String ps6 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                           // weekdays & any course

         String ps7 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND time >= " +stime1+ " AND " +
                      "time <= " +etime1+ " AND courseName = '" +course+ "'";                   // weekends & course

         String ps8 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND " +
                      "time >= " +stime1+ " AND time <= " +etime1;                           // weekends & any course

         //
         //   Statements to use based on the double tee config - for adding 'Crossover' tee time
         //
         String cs1 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2+ " AND " +
                      "courseName = '" +course+ "'";                                           // day & course

         String cs2 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND day = '" +day+ "' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                             // day & any course

         String cs3 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                 // every day & course

         String cs4 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                            // every day & any course

         String cs5 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                   // weekdays & course

         String cs6 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Saturday' AND day != 'Sunday' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                           // weekdays & any course

         String cs7 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND time >= " +stime2+ " AND " +
                      "time <= " +etime2+ " AND courseName = '" +course+ "'";                   // weekends & course

         String cs8 = "SELECT * FROM teecurr2 " +
                      "WHERE date >= " +sdate+ " AND date <= " +edate+ " AND " +
                      "day != 'Monday' AND day != 'Tuesday' AND day != 'Wednesday' AND " +
                      "day != 'Thursday' AND day != 'Friday' AND " +
                      "time >= " +stime2+ " AND time <= " +etime2;                           // weekends & any course

         //
         //  Determine which statement to use and then build the 'Back 9' tee times
         //
         if (course.equals( "-ALL-" ) || course.equals( "" )) {    // if ALL Courses or none

            if (!day.equals( "" )) {    // if an individual day

               statement = ps2;                                               // days specified & any course
               statement2 = cs2;

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  statement = ps4;                                          // every day specified & any course
                  statement2 = cs4;

               } else {

                  if ( recurr.equalsIgnoreCase( "all weekdays" )) {   // if Monday - Friday

                     statement = ps6;                                       // all week days & any course
                     statement2 = cs6;

                  } else {

                     if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                        statement = ps8;                                       // weekends & any course
                        statement2 = cs8;

                     }
                  }
               }
            }

         } else {      // course name was specified

            if (!day.equals( "" )) {    // if an individual day

               statement = ps1;                                               // days specified & course
               statement2 = cs1;

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  statement = ps3;                                          // every day specified & course
                  statement2 = cs3;

               } else {

                  if ( recurr.equalsIgnoreCase( "all weekdays" )) {   // if Monday - Friday

                     statement = ps5;                                       // all week days & course
                     statement2 = cs5;

                  } else {

                     if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

                        statement = ps7;                                       // weekends & course
                        statement2 = cs7;
                     }
                  }
               }
            }
         }        // end of statement tests

         //
         //  Now execute the selected statement and build the double tees
         //
         PreparedStatement pstmt5 = con.prepareStatement (statement);

         pstmt5.clearParameters();
         rs = pstmt5.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            day_name = rs.getString("day");
            time = rs.getInt("time");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            if (fb != 1) {       // if not already a 'Back 9' tee time (else skip)

               //
               //  Must be either a 'Front 9' or a 'Crossover'
               //  If Crossover, conver to 'Front 9'
               //
               if (fb == 9) {

                  PreparedStatement pstmt3 = con.prepareStatement (
                      "UPDATE teecurr2 SET fb = 0 " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setLong(1, date);
                  pstmt3.setInt(2, time);
                  pstmt3.setInt(3, fb);
                  pstmt3.setString(4, courseName);
                  pstmt3.executeUpdate();

                  pstmt3.close();
               }

               skip = 0;

               //
               //  Now check for a 'Back 9' tee time with matching date/time/course
               //
               PreparedStatement pstmt = con.prepareStatement (
                  "SELECT mm FROM teecurr2 WHERE date = ? AND time = ? AND fb = 1 AND courseName = ?");

               pstmt.clearParameters();        // clear the parms
               pstmt.setLong(1, date);         // put the parm in pstmt
               pstmt.setInt(2, time);
               pstmt.setString(3, courseName);
               rs2 = pstmt.executeQuery();      // execute the prepared stmt

               if (rs2.next()) {                // if tee time already exists

                  skip = 1;
                  status = 2;                    // tee times changed (may require attention from pro)
               }
               pstmt.close();

               //
               //  If none found, then we can add one
               //
               if (skip == 0) {

                  fb = 1;

                  insertTee(date, time, fb, courseName, day_name, con);     // insert new tee time (see directly below)

                  if (status == 0) {
                     status = 1;                    // built at least one
                  }
               }

            }  // end of IF NOT 'back 9' tee time

         }  // end of WHILE tee times

         pstmt5.close();

         //
         //  Now change the existing tee times to cross-overs
         //
         pstmt5 = con.prepareStatement (statement2);

         pstmt5.clearParameters();
         rs = pstmt5.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            if (fb != 9) {       // if not already a 'Crossover' tee time (else skip)

               if (!player1.equals( "" ) || !player2.equals( "" ) || !player3.equals( "" ) || !player4.equals( "" ) ||
                   !player5.equals( "" )) {

                  status = 2;       // inform user there may be a problem
               }

               //
               // change tee time to a crossover
               //
               PreparedStatement pstmt3 = con.prepareStatement (
                   "UPDATE teecurr2 SET fb = 9 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);
               pstmt3.setInt(2, time);
               pstmt3.setInt(3, fb);
               pstmt3.setString(4, courseName);
               pstmt3.executeUpdate();

               pstmt3.close();

            }

         }  // end of WHILE tee times

         pstmt5.close();
      
      }                    // end of IF ok

   }
   catch (Exception e2) {

      String errorMsg1 = "Error1 in SystemUtils buildDblTee: ";
      errorMsg1 = errorMsg1 + e2.getMessage();                                // build error msg

      logError(errorMsg1);                                       // log it

      throw new Exception("Error adding double tee - buildDblTee Exception: " + e2.getMessage());
   }

   return(status);

 }  // end of buildDblTee


 //************************************************************************
 //  removeDblTee - Removes double tees from the tee sheets.
 //
 //    called by:  Proshop_editdbltee
 //
 //
 //    returns status:  0 = no double tees exist
 //                     1 = double tees (back tees) found
 //                     2 = double tees (back tees) removed
 //                     3 = double tees removed, some had problems
 //
 //************************************************************************

 public static int removeDblTee(String course, String recurr, long sdate, long edate, int stime1, int etime1,
                                int stime2, int etime2, Connection con) throws Exception {


   Statement stmt = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;

   ResultSet rs = null;
   ResultSet rs2 = null;

   String courseName = "";
   String day = "";
   String day_name = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String errorMsg1 = "";

   long date = 0;

   int status = 0;
   int time = 0;

   short fb = 0;
   short skip = 0;


   try {

      if (sdate > 0) {          // if ok to continue
        
         //
         //  Use this info to scan the tee times and build the double tees whenever possible
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }
         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }
         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }
         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }
         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }
         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }
         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  Determine which statement to use and then remove the 'Back 9' tee times
         //
         if (course.equals( "-ALL-" ) || course.equals( "" )) {    // if ALL Courses or none

            if (!day.equals( "" )) {    // if an individual day

               pstmt = con.prepareStatement (
                  "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 1");                               // day & any course

               pstmt.clearParameters();
               pstmt.setLong(1, sdate);
               pstmt.setLong(2, edate);
               pstmt.setString(3, day);
               pstmt.setInt(4, stime1);
               pstmt.setInt(5, etime1);

               pstmt2 = con.prepareStatement (
                  "SELECT date, time, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 9");                                  // day & any course

               pstmt2.clearParameters();
               pstmt2.setLong(1, sdate);
               pstmt2.setLong(2, edate);
               pstmt2.setString(3, day);
               pstmt2.setInt(4, stime2);
               pstmt2.setInt(5, etime2);

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {                // if everyday

                  pstmt = con.prepareStatement (
                     "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND " +
                      "time >= ? AND time <= ? AND fb = 1");                           // every day & any course

                  pstmt.clearParameters();
                  pstmt.setLong(1, sdate);
                  pstmt.setLong(2, edate);
                  pstmt.setInt(3, stime1);
                  pstmt.setInt(4, etime1);

                  pstmt2 = con.prepareStatement (
                     "SELECT date, time, fb, courseName FROM teecurr2 " +
                     "WHERE date >= ? AND date <= ? AND " +
                     "time >= ? AND time <= ? AND fb = 9");                           // every day & any course

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, sdate);
                  pstmt2.setLong(2, edate);
                  pstmt2.setInt(3, stime2);
                  pstmt2.setInt(4, etime2);

               } else {

                  if ( recurr.equals( "All Weekdays" )) {                    // if Monday - Friday

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND " +
                        "time >= ? AND time <= ? AND fb = 1");                         // weekdays & any course

                     pstmt.clearParameters();
                     pstmt.setLong(1, sdate);
                     pstmt.setLong(2, edate);
                     pstmt.setInt(3, stime1);
                     pstmt.setInt(4, etime1);

                     pstmt2 = con.prepareStatement (
                         "SELECT date, time, fb, courseName FROM teecurr2 " +
                         "WHERE date >= ? AND date <= ? AND " +
                         "day != 'Saturday' AND day != 'Sunday' AND " +
                         "time >= ? AND time <= ? AND fb = 9");                   // weekdays & any course

                     pstmt2.clearParameters();
                     pstmt2.setLong(1, sdate);
                     pstmt2.setLong(2, edate);
                     pstmt2.setInt(3, stime2);
                     pstmt2.setInt(4, etime2);

                  } else {

                     if ( recurr.equals( "All Weekends" ) ) {                   // if Saturday & Sunday

                        pstmt = con.prepareStatement (
                           "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND " +
                           "time >= ? AND time <= ? AND fb = 1");                       // weekends & any course

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);
                        pstmt.setLong(2, edate);
                        pstmt.setInt(3, stime1);
                        pstmt.setInt(4, etime1);

                        pstmt2 = con.prepareStatement (
                           "SELECT date, time, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND " +
                           "time >= ? AND time <= ? AND fb = 9");                       // weekends & any course

                        pstmt2.clearParameters();
                        pstmt2.setLong(1, sdate);
                        pstmt2.setLong(2, edate);
                        pstmt2.setInt(3, stime2);
                        pstmt2.setInt(4, etime2);
                     }
                  }
               }
            }

         } else {      // course name was specified

            if (!day.equals( "" )) {                      // if an individual day

               pstmt = con.prepareStatement (
                  "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 1 AND " +
                  "courseName = ?");                                           // day & course

               pstmt.clearParameters();
               pstmt.setLong(1, sdate);
               pstmt.setLong(2, edate);
               pstmt.setString(3, day);
               pstmt.setInt(4, stime1);
               pstmt.setInt(5, etime1);
               pstmt.setString(6, course);

               pstmt2 = con.prepareStatement (
                  "SELECT date, time, fb, courseName FROM teecurr2 " +
                  "WHERE date >= ? AND date <= ? AND day = ? AND " +
                  "time >= ? AND time <= ? AND fb = 9 AND " +
                  "courseName = ?");                                               // day & course

               pstmt2.clearParameters();
               pstmt2.setLong(1, sdate);
               pstmt2.setLong(2, edate);
               pstmt2.setString(3, day);
               pstmt2.setInt(4, stime2);
               pstmt2.setInt(5, etime2);
               pstmt2.setString(6, course);

            } else {

               if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

                  pstmt = con.prepareStatement (
                      "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND time >= ? AND " +
                      "time <= ? AND fb = 1 AND courseName = ?");                   // every day & course

                  pstmt.clearParameters();
                  pstmt.setLong(1, sdate);
                  pstmt.setLong(2, edate);
                  pstmt.setInt(3, stime1);
                  pstmt.setInt(4, etime1);
                  pstmt.setString(5, course);

                  pstmt2 = con.prepareStatement (
                      "SELECT date, time, fb, courseName FROM teecurr2 " +
                      "WHERE date >= ? AND date <= ? AND time >= ? AND " +
                      "time <= ? AND fb = 9 AND courseName = ?");                    // every day & course

                  pstmt2.clearParameters();
                  pstmt2.setLong(1, sdate);
                  pstmt2.setLong(2, edate);
                  pstmt2.setInt(3, stime2);
                  pstmt2.setInt(4, etime2);
                  pstmt2.setString(5, course);

               } else {

                  if ( recurr.equals( "All Weekdays" )) {   // if Monday - Friday

                     pstmt = con.prepareStatement (
                        "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND time >= ? AND " +
                        "time <= ? AND fb = 1 AND courseName = ?");                    // weekdays & course

                     pstmt.clearParameters();
                     pstmt.setLong(1, sdate);
                     pstmt.setLong(2, edate);
                     pstmt.setInt(3, stime1);
                     pstmt.setInt(4, etime1);
                     pstmt.setString(5, course);

                     pstmt2 = con.prepareStatement (
                        "SELECT date, time, fb, courseName FROM teecurr2 " +
                        "WHERE date >= ? AND date <= ? AND " +
                        "day != 'Saturday' AND day != 'Sunday' AND time >= ? AND " +
                        "time <= ? AND fb = 9 AND courseName = ?");                   // weekdays & course

                     pstmt2.clearParameters();
                     pstmt2.setLong(1, sdate);
                     pstmt2.setLong(2, edate);
                     pstmt2.setInt(3, stime2);
                     pstmt2.setInt(4, etime2);
                     pstmt2.setString(5, course);

                  } else {

                     if ( recurr.equals( "All Weekends" ) ) {   // if Saturday & Sunday

                        pstmt = con.prepareStatement (
                           "SELECT date, time, player1, player2, player3, player4, player5, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND time >= ? AND " +
                           "time <= ? AND fb = 1 AND courseName = ?");                  // weekends & course

                        pstmt.clearParameters();
                        pstmt.setLong(1, sdate);
                        pstmt.setLong(2, edate);
                        pstmt.setInt(3, stime1);
                        pstmt.setInt(4, etime1);
                        pstmt.setString(5, course);

                        pstmt2 = con.prepareStatement (
                           "SELECT date, time, fb, courseName FROM teecurr2 " +
                           "WHERE date >= ? AND date <= ? AND " +
                           "(day = 'Saturday' OR day = 'Sunday') AND time >= ? AND " +
                           "time <= ? AND fb = 9 AND courseName = ?");                   // weekends & course

                        pstmt2.clearParameters();
                        pstmt2.setLong(1, sdate);
                        pstmt2.setLong(2, edate);
                        pstmt2.setInt(3, stime2);
                        pstmt2.setInt(4, etime2);
                        pstmt2.setString(5, course);
                     }
                  }
               }
            }
         }        // end of statement tests

         //
         //  Now execute the selected statement and remove the double tees (Back 9 tees)
         //
         rs = pstmt.executeQuery();         // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");
  
            if (status == 0) {
               status = 1;                    // found at least one
            }

            //
            //  remove all double tee (Back) slots that aren't occupied !!!!!!!!!!
            //
            if (player1.equals( "" ) && player2.equals( "" ) && player3.equals( "" ) && player4.equals( "" ) &&
                player5.equals( "" )) {

               pstmt3 = con.prepareStatement (
                   "DELETE FROM teecurr2 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               //
               //  execute the prepared statement to update the tee time slot
               //
               pstmt3.clearParameters();        // clear the parms
               pstmt3.setLong(1, date);
               pstmt3.setInt(2, time);
               pstmt3.setInt(3, fb);
               pstmt3.setString(4, courseName);
               pstmt3.executeUpdate();

               pstmt3.close();

               if (status < 2) {
                  status = 2;                    // removed at least one
               }

            } else {

               status = 3;            // unable to remove all Back 9 tee times
            }

         }  // end of WHILE tee times

         pstmt.close();

         //
         //  Now change the cross-overs back to Front tees
         //
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         while (rs.next()) {                // if tee times exist

            date = rs.getInt("date");
            time = rs.getInt("time");
            fb = rs.getShort("fb");
            courseName = rs.getString("courseName");

            //
            //  remove all cross-over tee slots that aren't occupied
            //
            pstmt3 = con.prepareStatement (
                "UPDATE teecurr2 SET fb = 0 " +
                "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt3.clearParameters();        // clear the parms
            pstmt3.setLong(1, date);
            pstmt3.setInt(2, time);
            pstmt3.setInt(3, fb);
            pstmt3.setString(4, courseName);
            pstmt3.executeUpdate();

            pstmt3.close();

         }  // end of WHILE tee times

         pstmt2.close();
      }                           // end of IF ok

   }
   catch (Exception e2) {

      errorMsg1 = "Error1 in SystemUtils removeDblTee: ";
      errorMsg1 = errorMsg1 + e2.getMessage();                                // build error msg

      logError(errorMsg1);                                       // log it

      throw new Exception("Error adding double tee - removeDblTee Exception: " + e2.getMessage());
   }

   return(status);

 }  // end of removeDblTee


 //************************************************************************
 //  insertTee - Inserts a tee time into teecurr
 //
 //    called by:  buildDbltee above
 //                Proshop_insert
 //
 //
 //************************************************************************

 public static void insertTee(long date, int time, int fb, String course, String day_name, Connection con)
                          throws Exception {


   ResultSet rs = null;

   //
   //  variables for this method
   //
   int year = 0;
   int month = 0;
   int day = 0;
   int day_num = 0;
   int hr = 0;
   int min = 0;
   int ampm = 0;
   int index = 0;
   int event_type = 0;
   int stime = 0;
   int etime = 0;
   int stime2 = 0;
   int etime2 = 0;

   long month2 = 0;
   long day2 = 0;
   long year2 = 0;

   String sfb = "";
   String sfb2 = "";
   String sfb3 = "";
   String event = "";
   String event_color = "";
   String rest = "";
   String rest2 = "";
   String rest_color = "";
   String rest_color2 = "";
   String rest_recurr = "";
   String rest5 = "";                      // default values
   String rest52 = "";
   String rest5_color = "";
   String rest5_color2 = "";
   String rest5_recurr = "";
   String lott = "";                      // lottery name
   String lott2 = "";                      // lottery name
   String lott_color = "";
   String lott_color2 = "";
   String lott_recurr = "";

   //
   //  Gather parms for tee time
   //
   year2 = date / 10000;
   month2 = (date - (year2 * 10000)) / 100;
   day2 = (date - (year2 * 10000)) - (month2 * 100);

   month = (int)month2;
   day = (int)day2;
   year = (int)year2;

   hr = time / 100;
   min = time - (hr * 100);

   try {
      //
      // Prepared statement to search the events table
      //
      PreparedStatement pstmt2 = con.prepareStatement (
         "SELECT name, stime, etime, color, type, fb, stime2, etime2, fb2 FROM events2b " +
         "WHERE date = ? AND (courseName = ? OR courseName = '-ALL-')");

      //
      // Prepared statement to search the restrictions tables
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM restriction2 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      PreparedStatement pstmt3a = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM fives2 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      PreparedStatement pstmt3b = con.prepareStatement (
         "SELECT name, recurr, color, fb FROM lottery3 WHERE sdate <= ? AND edate >= ? " +
         "AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-')");

      //
      //  Prep'd statement to insert tee time in teecurr
      //
      PreparedStatement pstmt4 = con.prepareStatement (
         "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
         "restriction, rest_color, player1, player2, player3, player4, username1, " +
         "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
         "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
         "player5, username5, p5cw, hndcp5, show5, notes, hideNotes, lottery, courseName, blocker, " +
         "proNew, proMod, memNew, memMod, rest5, rest5_color, " +
         "mNum1, mNum2, mNum3, mNum4, mNum5, lottery_color, userg1, userg2, " +
         "userg3, userg4, userg5, hotelNew, hotelMod, orig_by, conf, p91, p92, p93, p94, p95, " +
         "pos1, pos2, pos3, pos4, pos5, hole) " +
         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '', '', '', '', '', '', " +
         "'', '', '', '', '', '', 0, '0', '', ?, 0, 0, 0, 0, 0, 0, 0, 0, ?, '', " +
         "'', '', 0, 0, '', 0, ?, ?, '', 0, 0, 0, 0, ?, ?, '', '', '', '', '', " +
         "?, '', '', '', '', '', 0, 0, '', '', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, '')");

      //
      //   Check for any events during this time
      //
      sfb = "Front";
      if (fb == 1) {       // if back requested

         sfb = "Back";
      }

      event = "";                      // default values
      event_color = "";
      event_type = 0;

      pstmt2.clearParameters();              // clear the parms
      pstmt2.setLong(1, date);               // put the parm in stmt
      pstmt2.setString(2, course);
      rs = pstmt2.executeQuery();            // execute the prepared stmt

      if (rs.next()) {

         stime = rs.getInt(2);
         etime = rs.getInt(3);
         sfb2 = rs.getString(6);
         stime2 = rs.getInt(7);
         etime2 = rs.getInt(8);
         sfb3 = rs.getString(9);

         if ((time >= stime && time <= etime) && (sfb2.equals( "Both" ) || sfb2.equals( sfb ))) {

            event = rs.getString(1);            // event exists for this slot
            event_color = rs.getString(4);
            event_type = rs.getInt(5);
           
         } else {           // check 2nd set of blockers for event 

            if ((time >= stime2 && time <= etime2) && (sfb3.equals( "Both" ) || sfb3.equals( sfb ))) {

               event = rs.getString(1);            // event exists for this slot
               event_color = rs.getString(4);
               event_type = rs.getInt(5);
            }
         }
      }
      pstmt2.close();   // close the stmt

      //
      //   Check for any restrictions during this time
      //
      rest = "";                      // default values
      rest2 = "";
      rest_color = "";
      rest_color2 = "";
      rest_recurr = "";

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setLong(1, date);         // put the parms in stmt
      pstmt3.setLong(2, date);
      pstmt3.setInt(3, time);
      pstmt3.setInt(4, time);
      pstmt3.setString(5, course);
      rs = pstmt3.executeQuery();      // execute the prepared stmt

      loop1:
      while (rs.next()) {

         rest2 = rs.getString(1);          // rest exists for this slot time
         rest_recurr = rs.getString(2);
         rest_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (rest_recurr.equalsIgnoreCase( "every " + day_name )) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if (rest_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }

            if ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               rest = rest2;
               rest_color = rest_color2;
               break loop1;                   // done checking - exit while loop
            }
         }

      }   // end of loop1 while loop

      pstmt3.close();   // close the stmt

      //
      //   Check for any 5-some restrictions during this time
      //
      rest5 = "";                      // default values
      rest52 = "";
      rest5_color = "";
      rest5_color2 = "";
      rest5_recurr = "";

      pstmt3a.clearParameters();        // clear the parms
      pstmt3a.setLong(1, date);         // put the parms in stmt
      pstmt3a.setLong(2, date);
      pstmt3a.setInt(3, time);
      pstmt3a.setInt(4, time);
      pstmt3a.setString(5, course);
      rs = pstmt3a.executeQuery();      // execute the prepared stmt

      loop2:
      while (rs.next()) {

         rest52 = rs.getString(1);          // 5-some rest exists for this slot time
         rest5_recurr = rs.getString(2);
         rest5_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (rest5_recurr.equalsIgnoreCase( "every " + day_name )) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if (rest5_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }

            if ((rest5_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               rest5 = rest52;
               rest5_color = rest5_color2;
               break loop2;                   // done checking - exit while loop
            }
         }

      }   // end of loop2 while loop

      pstmt3a.close();   // close the stmt

      //
      //   Check for any lotteries during this time
      //
      lott = "";                      // default values
      lott2 = "";
      lott_color = "";
      lott_color2 = "";
      lott_recurr = "";

      pstmt3b.clearParameters();        // clear the parms
      pstmt3b.setLong(1, date);         // put the parms in stmt
      pstmt3b.setLong(2, date);
      pstmt3b.setInt(3, time);
      pstmt3b.setInt(4, time);
      pstmt3b.setString(5, course);
      rs = pstmt3b.executeQuery();      // execute the prepared stmt

      loop3:
      while (rs.next()) {

         lott2 = rs.getString(1);          // lottery exists for this slot time
         lott_recurr = rs.getString(2);
         lott_color2 = rs.getString(3);
         sfb2 = rs.getString(4);

         if (sfb2.equals( "Both" ) || sfb2.equals( sfb )) {

            //
            //  We must check the recurrence for this day (Monday, etc.)
            //
            if (lott_recurr.equalsIgnoreCase( "every " + day_name )) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if (lott_recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekdays" )) &&
                (!day_name.equalsIgnoreCase( "saturday" )) &&
                (!day_name.equalsIgnoreCase( "sunday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "saturday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }

            if ((lott_recurr.equalsIgnoreCase( "all weekends" )) &&
                (day_name.equalsIgnoreCase( "sunday" ))) {

               lott = lott2;
               lott_color = lott_color2;
               break loop3;                   // done checking - exit while loop
            }
         }

      }   // end of loop2 while loop

      pstmt3b.close();   // close the stmt

      //
      //   Add this time slot to teecurr
      //
      pstmt4.clearParameters();        // clear the parms
      pstmt4.setLong(1, date);
      pstmt4.setInt(2, month);
      pstmt4.setInt(3, day);
      pstmt4.setInt(4, year);
      pstmt4.setString(5, day_name);
      pstmt4.setInt(6, hr);
      pstmt4.setInt(7, min);
      pstmt4.setInt(8, time);
      pstmt4.setString(9, event);
      pstmt4.setString(10, event_color);
      pstmt4.setString(11, rest);
      pstmt4.setString(12, rest_color);
      pstmt4.setInt(13, event_type);
      pstmt4.setInt(14, fb);
      pstmt4.setString(15, lott);
      pstmt4.setString(16, course);
      pstmt4.setString(17, rest5);
      pstmt4.setString(18, rest5_color);
      pstmt4.setString(19, lott_color);

      pstmt4.executeUpdate();        // execute the prepared stmt - insert the tee time slot

      pstmt4.close();   // close the stmt

   }
   catch (Exception e) {

      throw new Exception("Error inserting tee time - SystemUtils.insertTee " + e.getMessage());
   }

 }  // end of insertTee


 //************************************************************************
 //  insertTee2 - Inserts a tee time into teecurr for events
 //
 //    called by:  Proshop_evntChkAll
 //
 //
 //************************************************************************

 public static void insertTee2(parmTee parm, Connection con)
                          throws Exception {


   PreparedStatement pstmt3 = null;
   ResultSet rs = null;

   String [] mnumA = new String [5];             // array to hold the member numbers
   String [] userA = new String [5];             // array to hold the member usernames

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   int i = 0;
   int day = 0;
   int month = parm.mm - 1;

  
   //
   //  Caller does not set the day name, so get it now for teecurr and teepast
   //
   Calendar cal = new GregorianCalendar();            // get todays date

   cal.set(Calendar.YEAR,parm.yy);                    // set year in cal
   cal.set(Calendar.MONTH,month);                     // set month in cal
   cal.set(Calendar.DAY_OF_MONTH,parm.dd);            // set day in cal

   day = cal.get(Calendar.DAY_OF_WEEK);               // day of week
     
   parm.day = day_table[day];                         // get name for day


   try {

      if (parm.edate >= parm.date) {       // if today or later then use teecurr

         userA[0] = parm.username1;
         userA[1] = parm.username2;
         userA[2] = parm.username3;
         userA[3] = parm.username4;
         userA[4] = parm.username5;
         mnumA[0] = "";
         mnumA[1] = "";
         mnumA[2] = "";
         mnumA[3] = "";
         mnumA[4] = "";

         //
         //  Proshop_evntChkAll does not set the member number - do this now
         //
         for (i=0; i<5; i++) {
         
            if (!userA[i].equals( "" )) {       // if username provided
              
               pstmt3 = con.prepareStatement (
                  "SELECT memNum FROM member2b WHERE username= ?");

               pstmt3.clearParameters();        // clear the parms
               pstmt3.setString(1, userA[i]);

               rs = pstmt3.executeQuery();

               if (rs.next()) {

                  mnumA[i] = rs.getString(1);
               }
               pstmt3.close();
            }
         }

         //
         // Prepared statement to put this days tee times in the teecurr2 table
         //
         pstmt3 = con.prepareStatement (
            "INSERT INTO teecurr2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, first, in_use, in_use_by, " +
            "event_type, hndcp1, hndcp2, hndcp3, hndcp4, show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, hndcp5, show5, notes, hidenotes, lottery, courseName, blocker, " +
            "proNew, proMod, memNew, memMod, rest5, rest5_color, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "lottery_color, userg1, userg2, userg3, userg4, userg5, hotelNew, hotelMod, orig_by, " +
            "conf, p91, p92, p93, p94, p95, pos1, pos2, pos3, pos4, pos5, hole) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "'', '', ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, 0, 0, '', " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, '', 0, '', ?, '', " +
            "0, 0, 0, 0, '', '', ?, ?, ?, ?, ?, " +
            "'', ?, ?, ?, ?, ?, 0, 0, '', " +
            "'', ?, ?, ?, ?, ?, 0, 0, 0, 0, 0, '')");

         pstmt3.clearParameters();         // clear the parms
         pstmt3.setLong(1, parm.edate);         // put the parms in stmt for tee slot
         pstmt3.setInt(2, parm.mm);
         pstmt3.setInt(3, parm.dd);
         pstmt3.setInt(4, parm.yy);
         pstmt3.setString(5, parm.day);     
         pstmt3.setInt(6, parm.hr);
         pstmt3.setInt(7, parm.min);
         pstmt3.setInt(8, parm.time);
         pstmt3.setString(9, parm.event);            // event name
         pstmt3.setString(10, parm.event_color);           // event color
         pstmt3.setString(11, parm.player1);
         pstmt3.setString(12, parm.player2);
         pstmt3.setString(13, parm.player3);
         pstmt3.setString(14, parm.player4);
         pstmt3.setString(15, parm.username1);
         pstmt3.setString(16, parm.username2);
         pstmt3.setString(17, parm.username3);
         pstmt3.setString(18, parm.username4);
         pstmt3.setString(19, parm.p1cw);
         pstmt3.setString(20, parm.p2cw);
         pstmt3.setString(21, parm.p3cw);
         pstmt3.setString(22, parm.p4cw);
         pstmt3.setInt(23, parm.event_type);
         pstmt3.setFloat(24, parm.hndcp1);
         pstmt3.setFloat(25, parm.hndcp2);
         pstmt3.setFloat(26, parm.hndcp3);
         pstmt3.setFloat(27, parm.hndcp4);
         pstmt3.setInt(28, parm.show1);
         pstmt3.setInt(29, parm.show2);
         pstmt3.setInt(30, parm.show3);
         pstmt3.setInt(31, parm.show4);
         pstmt3.setInt(32, parm.fb);
         pstmt3.setString(33, parm.player5);
         pstmt3.setString(34, parm.username5);
         pstmt3.setString(35, parm.p5cw);
         pstmt3.setFloat(36, parm.hndcp5);
         pstmt3.setInt(37, parm.show5);
         pstmt3.setString(38, parm.courseName);
         pstmt3.setString(39, mnumA[0]);
         pstmt3.setString(40, mnumA[1]);
         pstmt3.setString(41, mnumA[2]);
         pstmt3.setString(42, mnumA[3]);
         pstmt3.setString(43, mnumA[4]);
         pstmt3.setString(44, parm.userg1);
         pstmt3.setString(45, parm.userg2);
         pstmt3.setString(46, parm.userg3);
         pstmt3.setString(47, parm.userg4);
         pstmt3.setString(48, parm.userg5);
         pstmt3.setInt(49, parm.p91);
         pstmt3.setInt(50, parm.p92);
         pstmt3.setInt(51, parm.p93);
         pstmt3.setInt(52, parm.p94);
         pstmt3.setInt(53, parm.p95);

         pstmt3.executeUpdate();          // execute the prepared stmt

         pstmt3.close();

      } else {          // not today - use teepast

         //
         // Prepared statement to put this days tee times in the teepast table
         //
         pstmt3 = con.prepareStatement (
            "INSERT INTO teepast2 (date, mm, dd, yy, day, hr, min, time, event, event_color, " +
            "restriction, rest_color, player1, player2, player3, player4, username1, " +
            "username2, username3, username4, p1cw, p2cw, p3cw, p4cw, " +
            "show1, show2, show3, show4, fb, " +
            "player5, username5, p5cw, show5, courseName, " +
            "proNew, proMod, memNew, memMod, mNum1, mNum2, mNum3, mNum4, mNum5, " +
            "userg1, userg2, userg3, userg4, userg5, hotelNew, " +
            "hotelMod, orig_by, conf, notes, p91, p92, p93, p94, p95) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '', '', ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, 0, 0, 0, 0, '', '', '', '', '', ?, ?, ?, ?, ?, " +
            "0, 0, '', '', '', ?, ?, ?, ?, ?)");

         pstmt3.clearParameters();         // clear the parms
         pstmt3.setLong(1, parm.edate);         // put the parms in stmt for tee slot
         pstmt3.setInt(2, parm.mm);
         pstmt3.setInt(3, parm.dd);
         pstmt3.setInt(4, parm.yy);
         pstmt3.setString(5, parm.day);
         pstmt3.setInt(6, parm.hr);
         pstmt3.setInt(7, parm.min);
         pstmt3.setInt(8, parm.time);
         pstmt3.setString(9, parm.event);            // event name
         pstmt3.setString(10, parm.event_color);           // event color
         pstmt3.setString(11, parm.player1);
         pstmt3.setString(12, parm.player2);
         pstmt3.setString(13, parm.player3);
         pstmt3.setString(14, parm.player4);
         pstmt3.setString(15, parm.username1);
         pstmt3.setString(16, parm.username2);
         pstmt3.setString(17, parm.username3);
         pstmt3.setString(18, parm.username4);
         pstmt3.setString(19, parm.p1cw);
         pstmt3.setString(20, parm.p2cw);
         pstmt3.setString(21, parm.p3cw);
         pstmt3.setString(22, parm.p4cw);
         pstmt3.setInt(23, parm.show1);
         pstmt3.setInt(24, parm.show2);
         pstmt3.setInt(25, parm.show3);
         pstmt3.setInt(26, parm.show4);
         pstmt3.setInt(27, parm.fb);
         pstmt3.setString(28, parm.player5);
         pstmt3.setString(29, parm.username5);
         pstmt3.setString(30, parm.p5cw);
         pstmt3.setInt(31, parm.show5);
         pstmt3.setString(32, parm.courseName);
         pstmt3.setString(33, parm.userg1);
         pstmt3.setString(34, parm.userg2);
         pstmt3.setString(35, parm.userg3);
         pstmt3.setString(36, parm.userg4);
         pstmt3.setString(37, parm.userg5);
         pstmt3.setInt(38, parm.p91);
         pstmt3.setInt(39, parm.p92);
         pstmt3.setInt(40, parm.p93);
         pstmt3.setInt(41, parm.p94);
         pstmt3.setInt(42, parm.p95);

         pstmt3.executeUpdate();          // execute the prepared stmt

         pstmt3.close();

         //
         //  Delete the stats record for this date
         //
         pstmt3 = con.prepareStatement (
                  "Delete FROM stats5 WHERE date = ? AND course = ?");

         pstmt3.clearParameters();               // clear the parms
         pstmt3.setLong(1, parm.edate);
         pstmt3.setString(2, parm.courseName);
         pstmt3.executeUpdate();         // execute the prepared pstmt3

         pstmt3.close();

         //
         //  Go rebuild the stats record for this date and course
         //
         moveStatsCom(parm.edate, "teepast", parm.courseName, con, "forEvent");

      }  // end of IF Date

      //
      //  Track the history of this tee time - make entry in 'teehist' table (check if new or update)
      //
      String user = "Proshop";
      String fullName = "Proshop Event";

      updateHist(parm.edate, parm.day, parm.time, parm.fb, parm.courseName, parm.player1, parm.player2, parm.player3,
                             parm.player4, parm.player5, user, fullName, 0, con);

   }
   catch (Exception e) {

      throw new Exception("Error inserting tee time - SystemUtils.insertTee2 " + e.getMessage());
   }

 }  // end of insertTee2


 //************************************************************************
 //  filter - Searches the string provided for special characters and
 //           replaces them with HTML friendly characters.
 //
 //           < becomes &lt
 //           > becomes &gt
 //           " becomes &quot
 //           & becomes &amp
 //           # becomes &#35 (hash - not pound)
 //
 //
 //    called by:  Proshop_addevnt
 //                Proshop_events
 //                Proshop_editevnt
 //                Proshop_mrest
 //                Proshop_addmrest
 //                Proshop_editmrest
 //                Proshop_grest
 //                Proshop_addgrest
 //                Proshop_editgrest
 //                ...and others
 //
 //************************************************************************

 public static String filter(String input) {


    StringBuffer filtered = new StringBuffer(input.length());

    char c;

    for(int i=0; i<input.length(); i++) {

       c = input.charAt(i);
       if (c == '<') {                  // if special char
          filtered.append("&lt;");      // change to html char
       } else if (c == '>') {
          filtered.append("&gt;");
       } else if (c == '"') {
          filtered.append("&quot;");
       } else if (c == '&') {
          filtered.append("&amp;");
       } else if (c == '#') {
          filtered.append("&#35;");
       } else if (c == '!') {
          filtered.append("&#33;");
       } else if (c == '$') {
          filtered.append("&#36;");
       } else if (c == '%') {
          filtered.append("&#37;");
       } else if (c == '(') {
          filtered.append("&#40;");
       } else if (c == ')') {
          filtered.append("&#41;");
       } else if (c == '*') {
          filtered.append("&#42;");
       } else if (c == '+') {
          filtered.append("&#43;");
       } else if (c == ',') {
          filtered.append("&#44;");
       } else if (c == '-') {
          filtered.append("&#45;");
       } else if (c == '.') {
          filtered.append("&#46;");
       } else if (c == '/') {
          filtered.append("&#47;");
       } else if (c == '=') {
          filtered.append("&#61;");
       } else if (c == '?') {
          filtered.append("&#63;");
       } else if (c == '@') {
          filtered.append("&#64;");
       } else if (c == '[') {
          filtered.append("&#91;");
       } else if (c == ']') {
          filtered.append("&#93;");
       } else if (c == '^') {
          filtered.append("&#94;");
       } else if (c == '_') {
          filtered.append("&#95;");
       } else if (c == '{') {
          filtered.append("&#123;");
       } else if (c == '|') {
          filtered.append("&#124;");
       } else if (c == '}') {
          filtered.append("&#125;");
       } else if (c == '~') {
          filtered.append("&#126;");
       } else {
          filtered.append(c);
       }
    }

    return(filtered.toString());

 }  // end of filter


 //************************************************************************
 //  unfilter - Searches the string provided for special HTML characters and
 //             replaces them with normal special characters.
 //
 //           &lt becomes <
 //           &gt becomes >
 //           &quot becomes "
 //           &amp becomes &
 //
 //
 //    called by:  Send_email
 //
 //************************************************************************

 public static String unfilter(String input) {


    StringBuffer unfiltered = new StringBuffer(input.length());
    StringBuffer spec = new StringBuffer(5);

    int length = input.length();
    char c;
    String specs = "";

    for(int i=0; i<length; i++) {

       c = input.charAt(i);
       if (c == '&') {                  // if special html char

          spec = new StringBuffer(5);   // init string

          while (c != ';' && i<length) {   // go till end of spec char - semi-colon

             spec.append(c);            // put in buffer
             i++;
             c = input.charAt(i);       // get next
          }                             // end of WHILE

          specs = spec.toString();      // put in string form

          if (specs.equals("&lt")) {                  // if special char
             unfiltered.append('<');                 // change back to normal spec char
          } else if (specs.equals("&gt")) {
             unfiltered.append('>');
          } else if (specs.equals("&quot")) {
             unfiltered.append('"');
          } else if (specs.equals("&amp")) {
             unfiltered.append('&');
          } else if (specs.equals("&#35")) {
             unfiltered.append('#');
          } else if (specs.equals("&#33")) {
             unfiltered.append('!');
          } else if (specs.equals("&#36")) {
             unfiltered.append("$");
          } else if (specs.equals("&#37")) {
             unfiltered.append('%');
          } else if (specs.equals("&#40")) {
             unfiltered.append('(');
          } else if (specs.equals("&#41")) {
             unfiltered.append(')');
          } else if (specs.equals("&#42")) {
             unfiltered.append('*');
          } else if (specs.equals("&#43")) {
             unfiltered.append('+');
          } else if (specs.equals("&#44")) {
             unfiltered.append(',');
          } else if (specs.equals("&#45")) {
             unfiltered.append('-');
          } else if (specs.equals("&#46")) {
             unfiltered.append('.');
          } else if (specs.equals("&#47")) {
             unfiltered.append('/');
          } else if (specs.equals("&#61")) {
             unfiltered.append('=');
          } else if (specs.equals("&#63")) {
             unfiltered.append('?');
          } else if (specs.equals("&#64")) {
             unfiltered.append('@');
          } else if (specs.equals("&#91")) {
             unfiltered.append('[');
          } else if (specs.equals("&#93")) {
             unfiltered.append(']');
          } else if (specs.equals("&#94")) {
             unfiltered.append('^');
          } else if (specs.equals("&#95")) {
             unfiltered.append('_');
          } else if (specs.equals("&#123")) {
             unfiltered.append('{');
          } else if (specs.equals("&#124")) {
             unfiltered.append('|');
          } else if (specs.equals("&#125")) {
             unfiltered.append('}');
          } else if (specs.equals("&#126")) {
             unfiltered.append('~');
          }
       } else {
          unfiltered.append(c);

       }          // end of IF spec char
    }             // end of DO loop

    return(unfiltered.toString());

 }  // end of unfilter


 //************************************************************************
 //  scanName - Searches the string provided for special characters and
 //             returns an error if found.
 //
 //
 //    called by:  Proshop_parms
 //
 //
 //************************************************************************

 public static boolean scanName(String name) {


    boolean error = false;

    char [] c = { ' ' };
    String cs = "";

    String space = " ";    // space char               x020
    String n1 = "0";       // low numeric              x030 -
    String n2 = "9";       // high numeric             x039
    String a1 = "A";       // first alpha char         x041 -
    String a2 = "Z";       // upper alpha end          x05A
    String a3 = "a";       // 2nd alpha lower limit    x061 -
    String a4 = "z";       // upper limit              x07A

    for(int i=0; i<name.length(); i++) {

       c[0] = name.charAt(i);
       cs = new String ( c );

       if (cs.compareTo( space ) < 0) {                             // if less than a space char
          error = true;
       } else {
          if ((cs.compareTo( space ) > 0) && (cs.compareTo( n1 ) < 0)) {   // if > space char & < 0
             error = true;
          } else {
             if ((cs.compareTo( n2 ) > 0) && (cs.compareTo( a1 ) < 0)) {   // if > 9 & < A
                error = true;
             } else {
                if ((cs.compareTo( a2 ) > 0) && (cs.compareTo( a3 ) < 0)) {   // if > Z & < a
                   error = true;
                } else {
                   if (cs.compareTo( a4 ) > 0) {                         // if > z
                      error = true;
                   }
                }
             }
          }
       }
    }         // check all characters in name

    return(error);

 }  // end of scanName


 //************************************************************************
 //  scanQuote - Searches the string provided for a single quote and 
 //              returns an error if found.
 //
 //
 //    called by:  Proshop_mrest, etc. (configs)
 //
 //
 //************************************************************************

 public static boolean scanQuote(String name) {


    boolean error = false;

    char [] c = { ' ' };
    String cs = "";

    String quote = "'";    // single quote char

    for(int i=0; i<name.length(); i++) {

       c[0] = name.charAt(i);
       cs = new String ( c );

       if (cs.equals( quote )) {                             // if quote
          error = true;
       }
    }         // check all characters in name
    return(error);

 }  // end of scanName


 //************************************************************************
 // updateTeecurr - Scans the block, event, restriction & lottery tables for
 //                 new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  buildTee above after new tee times have been built
 //
 //************************************************************************

 public static void updateTeecurr(Connection con) {


   //
   //  check the restriction tables
   //
   doRests(con);

   //
   //  check the 5-Some tables
   //
   doFives(con);

   //
   //  check the Event tables
   //
   doEvents(con);

   //
   //  check the Blocker tables
   //
   doBlockers(con);

   //
   //  check the Lottery tables
   //
   doLotteries(con);

 }  // end of updateTecurr


 //************************************************************************
 // doRests - Scans the restriction tables for
 //           new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doRests(Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;

   String name = "";

   //
   //****************************************************************
   //  Get all the restrictions currently in the restriction table
   //****************************************************************
   //
   try {

      stmt2 = con.createStatement();        // create a statement

      rs = stmt2.executeQuery("SELECT name " +
                              "FROM restriction2");

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  go process the single restriction
         //
         do1Rest(con, name);

      }               // end of while for restriction table
      stmt2.close();
   }
   catch (Exception e9) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg9 = "Error9 in SystemUtils doRests: ";
      errorMsg9 = errorMsg9 + e9.getMessage();                                 // build error msg

      logError(errorMsg9);                                       // log it
   }

 }  // end of doRests


 //************************************************************************
 // do1Rest - Update teecurr for the restriction
 //
 //
 //   called by:  Proshop_addmrest
 //               Proshop_editmrest
 //               doEvents (above)
 //
 //************************************************************************

 public static void do1Rest(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";
   long date = 0;
   int stime = 0;
   int etime = 0;
   int type = 0;

   int count = 0;
   int fb = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";
   String show = "";

   //
   //****************************************************************
   //  Get the restriction named and process teecurr
   //****************************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, color, courseName, fb FROM restriction2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         color = rs.getString(6);
         courseName = rs.getString(7);
         sfb = rs.getString(8);

         day = "";    // init day value

         //
         //  Put restriction info in teecurr2 slots if matching date/time and restriction not already there
         //  (this is done based on recurrence sepcified in restriction)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" ) || courseName.equals( "" )) {    // if ALL Courses or none

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt2 = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND restriction = ? AND rest_color = ? ");

                  PreparedStatement pstmt5 = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                  PreparedStatement pstmt2b = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                  PreparedStatement pstmt5b = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt2.clearParameters();               // clear the parms
                        pstmt2.setString(1, name);              // put the parms in stmt
                        pstmt2.setString(2, color);
                        pstmt2.setLong(3, sdate);
                        pstmt2.setLong(4, edate);
                        pstmt2.setString(5, day);
                        pstmt2.setInt(6, stime);
                        pstmt2.setInt(7, etime);
                        pstmt2.setString(8, omit);
                        pstmt2.setString(9, omit);
                        count = pstmt2.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt2b.clearParameters();               // clear the parms
                        pstmt2b.setString(1, name);              // put the parms in stmt
                        pstmt2b.setString(2, color);
                        pstmt2b.setLong(3, sdate);
                        pstmt2b.setLong(4, edate);
                        pstmt2b.setString(5, day);
                        pstmt2b.setInt(6, stime);
                        pstmt2b.setInt(7, etime);
                        pstmt2b.setString(8, omit);
                        pstmt2b.setString(9, omit);
                        pstmt2b.setInt(10, fb);
                        count = pstmt2b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {   // color not default

                     if (sfb.equals( "Both" )) {

                        pstmt5.clearParameters();               // clear the parms
                        pstmt5.setString(1, name);              // put the parms in stmt
                        pstmt5.setString(2, color);
                        pstmt5.setLong(3, sdate);
                        pstmt5.setLong(4, edate);
                        pstmt5.setString(5, day);
                        pstmt5.setInt(6, stime);
                        pstmt5.setInt(7, etime);
                        pstmt5.setString(8, def);               // replace if existing color is default
                        pstmt5.setString(9, omit);              // or null
                        count = pstmt5.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt5b.clearParameters();               // clear the parms
                        pstmt5b.setString(1, name);              // put the parms in stmt
                        pstmt5b.setString(2, color);
                        pstmt5b.setLong(3, sdate);
                        pstmt5b.setLong(4, edate);
                        pstmt5b.setString(5, day);
                        pstmt5b.setInt(6, stime);
                        pstmt5b.setInt(7, etime);
                        pstmt5b.setInt(8, fb);
                        pstmt5b.setString(9, def);               // replace if existing color is defualt
                        pstmt5b.setString(10, omit);             // or null
                        count = pstmt5b.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt2.close();
                  pstmt5.close();
                  pstmt2b.close();
                  pstmt5b.close();
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg = "Error1 in SystemUtils do1Rest: ";
                  errorMsg = errorMsg + e2.getMessage();                                 // build error msg

                  logError(errorMsg);                                       // log it

               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt1a = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? ");

                        pstmt1a.clearParameters();               // clear the parms
                        pstmt1a.setString(1, name);              // put the parms in stmt
                        pstmt1a.setString(2, color);
                        pstmt1a.setLong(3, sdate);
                        pstmt1a.setLong(4, edate);
                        pstmt1a.setInt(5, stime);
                        pstmt1a.setInt(6, etime);
                        pstmt1a.setString(7, omit);
                        pstmt1a.setString(8, omit);
                        count = pstmt1a.executeUpdate();         // execute the prepared stmt

                        pstmt1a.close();

                     } else {

                        PreparedStatement pstmt1b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                        pstmt1b.clearParameters();               // clear the parms
                        pstmt1b.setString(1, name);              // put the parms in stmt
                        pstmt1b.setString(2, color);
                        pstmt1b.setLong(3, sdate);
                        pstmt1b.setLong(4, edate);
                        pstmt1b.setInt(5, stime);
                        pstmt1b.setInt(6, etime);
                        pstmt1b.setString(7, omit);
                        pstmt1b.setString(8, omit);
                        pstmt1b.setInt(9, fb);
                        count = pstmt1b.executeUpdate();         // execute the prepared stmt

                        pstmt1b.close();
                     }

                  } else {   // rest color not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt1a = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt1a.clearParameters();               // clear the parms
                        pstmt1a.setString(1, name);              // put the parms in stmt
                        pstmt1a.setString(2, color);
                        pstmt1a.setLong(3, sdate);
                        pstmt1a.setLong(4, edate);
                        pstmt1a.setInt(5, stime);
                        pstmt1a.setInt(6, etime);
                        pstmt1a.setString(7, def);
                        pstmt1a.setString(8, omit);
                        count = pstmt1a.executeUpdate();         // execute the prepared stmt

                        pstmt1a.close();

                     } else {

                        PreparedStatement pstmt1b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt1b.clearParameters();               // clear the parms
                        pstmt1b.setString(1, name);              // put the parms in stmt
                        pstmt1b.setString(2, color);
                        pstmt1b.setLong(3, sdate);
                        pstmt1b.setLong(4, edate);
                        pstmt1b.setInt(5, stime);
                        pstmt1b.setInt(6, etime);
                        pstmt1b.setInt(7, fb);
                        pstmt1b.setString(8, def);
                        pstmt1b.setString(9, omit);
                        count = pstmt1b.executeUpdate();         // execute the prepared stmt

                        pstmt1b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg2 = "Error2 in SystemUtils do1Rest: ";
                  errorMsg2 = errorMsg2 + e2.getMessage();                                 // build error msg

                  logError(errorMsg2);                                       // log it

               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt3 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? ");

                        pstmt3.clearParameters();               // clear the parms
                        pstmt3.setString(1, name);              // put the parms in stmt
                        pstmt3.setString(2, color);
                        pstmt3.setLong(3, sdate);
                        pstmt3.setLong(4, edate);
                        pstmt3.setInt(5, stime);
                        pstmt3.setInt(6, etime);
                        pstmt3.setString(7, omit);
                        pstmt3.setString(8, omit);
                        count = pstmt3.executeUpdate();         // execute the prepared stmt

                        pstmt3.close();

                     } else {

                        PreparedStatement pstmt3b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND fb = ?");

                        pstmt3b.clearParameters();               // clear the parms
                        pstmt3b.setString(1, name);              // put the parms in stmt
                        pstmt3b.setString(2, color);
                        pstmt3b.setLong(3, sdate);
                        pstmt3b.setLong(4, edate);
                        pstmt3b.setInt(5, stime);
                        pstmt3b.setInt(6, etime);
                        pstmt3b.setString(7, omit);
                        pstmt3b.setString(8, omit);
                        pstmt3b.setInt(9, fb);
                        count = pstmt3b.executeUpdate();         // execute the prepared stmt

                        pstmt3b.close();
                     }

                  } else {    // rest color is not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt3 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt3.clearParameters();               // clear the parms
                        pstmt3.setString(1, name);              // put the parms in stmt
                        pstmt3.setString(2, color);
                        pstmt3.setLong(3, sdate);
                        pstmt3.setLong(4, edate);
                        pstmt3.setInt(5, stime);
                        pstmt3.setInt(6, etime);
                        pstmt3.setString(7, def);
                        pstmt3.setString(8, omit);
                        count = pstmt3.executeUpdate();         // execute the prepared stmt

                        pstmt3.close();

                     } else {

                        PreparedStatement pstmt3b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt3b.clearParameters();               // clear the parms
                        pstmt3b.setString(1, name);              // put the parms in stmt
                        pstmt3b.setString(2, color);
                        pstmt3b.setLong(3, sdate);
                        pstmt3b.setLong(4, edate);
                        pstmt3b.setInt(5, stime);
                        pstmt3b.setInt(6, etime);
                        pstmt3b.setInt(7, fb);
                        pstmt3b.setString(8, def);
                        pstmt3b.setString(9, omit);
                        count = pstmt3b.executeUpdate();         // execute the prepared stmt

                        pstmt3b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg3 = "Error3 in SystemUtils do1Rest: ";
                  errorMsg3 = errorMsg3 + e2.getMessage();                                 // build error msg

                  logError(errorMsg3);                                       // log it

               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt4 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND " +
                          "restriction = ? AND rest_color = ? ");

                        pstmt4.clearParameters();               // clear the parms
                        pstmt4.setString(1, name);              // put the parms in stmt
                        pstmt4.setString(2, color);
                        pstmt4.setLong(3, sdate);
                        pstmt4.setLong(4, edate);
                        pstmt4.setInt(5, stime);
                        pstmt4.setInt(6, etime);
                        pstmt4.setString(7, omit);
                        pstmt4.setString(8, omit);
                        count = pstmt4.executeUpdate();         // execute the prepared stmt

                        pstmt4.close();

                     } else {

                        PreparedStatement pstmt4b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND " +
                          "restriction = ? AND rest_color = ? AND fb = ?");

                        pstmt4b.clearParameters();               // clear the parms
                        pstmt4b.setString(1, name);              // put the parms in stmt
                        pstmt4b.setString(2, color);
                        pstmt4b.setLong(3, sdate);
                        pstmt4b.setLong(4, edate);
                        pstmt4b.setInt(5, stime);
                        pstmt4b.setInt(6, etime);
                        pstmt4b.setString(7, omit);
                        pstmt4b.setString(8, omit);
                        pstmt4b.setInt(9, fb);
                        count = pstmt4b.executeUpdate();         // execute the prepared stmt

                        pstmt4b.close();
                     }

                  } else {     // rest color is not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt4 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ?) AND " +
                          "(rest_color = ? OR rest_color = ?))");

                        pstmt4.clearParameters();               // clear the parms
                        pstmt4.setString(1, name);              // put the parms in stmt
                        pstmt4.setString(2, color);
                        pstmt4.setLong(3, sdate);
                        pstmt4.setLong(4, edate);
                        pstmt4.setInt(5, stime);
                        pstmt4.setInt(6, etime);
                        pstmt4.setString(7, def);
                        pstmt4.setString(8, omit);
                        count = pstmt4.executeUpdate();         // execute the prepared stmt

                        pstmt4.close();

                     } else {

                        PreparedStatement pstmt4b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND fb = ?) AND " +
                          "(rest_color = ? OR rest_color = ?))");

                        pstmt4b.clearParameters();               // clear the parms
                        pstmt4b.setString(1, name);              // put the parms in stmt
                        pstmt4b.setString(2, color);
                        pstmt4b.setLong(3, sdate);
                        pstmt4b.setLong(4, edate);
                        pstmt4b.setInt(5, stime);
                        pstmt4b.setInt(6, etime);
                        pstmt4b.setInt(7, fb);
                        pstmt4b.setString(8, def);
                        pstmt4b.setString(9, omit);
                        count = pstmt4b.executeUpdate();         // execute the prepared stmt

                        pstmt4b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg4 = "Error4 in SystemUtils do1Rest: ";
                  errorMsg4 = errorMsg4 + e2.getMessage();                                 // build error msg

                  logError(errorMsg4);                                       // log it
               }

            }  // end of IF all weekends

         } else {                       // a specific courseName

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt2 = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ?");

                  PreparedStatement pstmt5 = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                  PreparedStatement pstmt2b = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                  PreparedStatement pstmt5b = con.prepareStatement (
                    "UPDATE teecurr2 SET restriction = ?, rest_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt2.clearParameters();               // clear the parms
                        pstmt2.setString(1, name);              // put the parms in stmt
                        pstmt2.setString(2, color);
                        pstmt2.setLong(3, sdate);
                        pstmt2.setLong(4, edate);
                        pstmt2.setString(5, day);
                        pstmt2.setInt(6, stime);
                        pstmt2.setInt(7, etime);
                        pstmt2.setString(8, omit);
                        pstmt2.setString(9, omit);
                        pstmt2.setString(10, courseName);
                        count = pstmt2.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt2b.clearParameters();               // clear the parms
                        pstmt2b.setString(1, name);              // put the parms in stmt
                        pstmt2b.setString(2, color);
                        pstmt2b.setLong(3, sdate);
                        pstmt2b.setLong(4, edate);
                        pstmt2b.setString(5, day);
                        pstmt2b.setInt(6, stime);
                        pstmt2b.setInt(7, etime);
                        pstmt2b.setString(8, omit);
                        pstmt2b.setString(9, omit);
                        pstmt2b.setString(10, courseName);
                        pstmt2b.setInt(11, fb);
                        count = pstmt2b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {   // color not default

                     if (sfb.equals( "Both" )) {

                        pstmt5.clearParameters();               // clear the parms
                        pstmt5.setString(1, name);              // put the parms in stmt
                        pstmt5.setString(2, color);
                        pstmt5.setLong(3, sdate);
                        pstmt5.setLong(4, edate);
                        pstmt5.setString(5, day);
                        pstmt5.setInt(6, stime);
                        pstmt5.setInt(7, etime);
                        pstmt5.setString(8, courseName);
                        pstmt5.setString(9, def);               // replace if existing color is default
                        pstmt5.setString(10, omit);              // or null
                        count = pstmt5.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt5b.clearParameters();               // clear the parms
                        pstmt5b.setString(1, name);              // put the parms in stmt
                        pstmt5b.setString(2, color);
                        pstmt5b.setLong(3, sdate);
                        pstmt5b.setLong(4, edate);
                        pstmt5b.setString(5, day);
                        pstmt5b.setInt(6, stime);
                        pstmt5b.setInt(7, etime);
                        pstmt5b.setString(8, courseName);
                        pstmt5b.setInt(9, fb);
                        pstmt5b.setString(10, def);               // replace if existing color is defualt
                        pstmt5b.setString(11, omit);              // or null
                        count = pstmt5b.executeUpdate();          // execute the prepared stmt
                     }
                  }

                  pstmt2.close();
                  pstmt5.close();
                  pstmt2b.close();
                  pstmt5b.close();
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg5 = "Error5 in SystemUtils do1Rest: ";
                  errorMsg5 = errorMsg5 + e2.getMessage();                                 // build error msg

                  logError(errorMsg5);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt1a = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? ");

                        pstmt1a.clearParameters();               // clear the parms
                        pstmt1a.setString(1, name);              // put the parms in stmt
                        pstmt1a.setString(2, color);
                        pstmt1a.setLong(3, sdate);
                        pstmt1a.setLong(4, edate);
                        pstmt1a.setInt(5, stime);
                        pstmt1a.setInt(6, etime);
                        pstmt1a.setString(7, omit);
                        pstmt1a.setString(8, omit);
                        pstmt1a.setString(9, courseName);
                        count = pstmt1a.executeUpdate();         // execute the prepared stmt

                        pstmt1a.close();

                     } else {

                        PreparedStatement pstmt1b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                        pstmt1b.clearParameters();               // clear the parms
                        pstmt1b.setString(1, name);              // put the parms in stmt
                        pstmt1b.setString(2, color);
                        pstmt1b.setLong(3, sdate);
                        pstmt1b.setLong(4, edate);
                        pstmt1b.setInt(5, stime);
                        pstmt1b.setInt(6, etime);
                        pstmt1b.setString(7, omit);
                        pstmt1b.setString(8, omit);
                        pstmt1b.setString(9, courseName);
                        pstmt1b.setInt(10, fb);
                        count = pstmt1b.executeUpdate();         // execute the prepared stmt

                        pstmt1b.close();
                     }

                  } else {   // rest color not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt1a = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt1a.clearParameters();               // clear the parms
                        pstmt1a.setString(1, name);              // put the parms in stmt
                        pstmt1a.setString(2, color);
                        pstmt1a.setLong(3, sdate);
                        pstmt1a.setLong(4, edate);
                        pstmt1a.setInt(5, stime);
                        pstmt1a.setInt(6, etime);
                        pstmt1a.setString(7, courseName);
                        pstmt1a.setString(8, def);
                        pstmt1a.setString(9, omit);
                        count = pstmt1a.executeUpdate();         // execute the prepared stmt

                        pstmt1a.close();

                     } else {

                        PreparedStatement pstmt1b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND time >= ? AND " +
                          "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt1b.clearParameters();               // clear the parms
                        pstmt1b.setString(1, name);              // put the parms in stmt
                        pstmt1b.setString(2, color);
                        pstmt1b.setLong(3, sdate);
                        pstmt1b.setLong(4, edate);
                        pstmt1b.setInt(5, stime);
                        pstmt1b.setInt(6, etime);
                        pstmt1b.setString(7, courseName);
                        pstmt1b.setInt(8, fb);
                        pstmt1b.setString(9, def);
                        pstmt1b.setString(10, omit);
                        count = pstmt1b.executeUpdate();         // execute the prepared stmt

                        pstmt1b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg6 = "Error6 in SystemUtils do1Rest: ";
                  errorMsg6 = errorMsg6 + e2.getMessage();                                 // build error msg

                  logError(errorMsg6);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt3 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? ");

                        pstmt3.clearParameters();               // clear the parms
                        pstmt3.setString(1, name);              // put the parms in stmt
                        pstmt3.setString(2, color);
                        pstmt3.setLong(3, sdate);
                        pstmt3.setLong(4, edate);
                        pstmt3.setInt(5, stime);
                        pstmt3.setInt(6, etime);
                        pstmt3.setString(7, omit);
                        pstmt3.setString(8, omit);
                        pstmt3.setString(9, courseName);
                        count = pstmt3.executeUpdate();         // execute the prepared stmt

                        pstmt3.close();

                     } else {

                        PreparedStatement pstmt3b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                        pstmt3b.clearParameters();               // clear the parms
                        pstmt3b.setString(1, name);              // put the parms in stmt
                        pstmt3b.setString(2, color);
                        pstmt3b.setLong(3, sdate);
                        pstmt3b.setLong(4, edate);
                        pstmt3b.setInt(5, stime);
                        pstmt3b.setInt(6, etime);
                        pstmt3b.setString(7, omit);
                        pstmt3b.setString(8, omit);
                        pstmt3b.setString(9, courseName);
                        pstmt3b.setInt(10, fb);
                        count = pstmt3b.executeUpdate();         // execute the prepared stmt

                        pstmt3b.close();
                     }

                  } else {    // rest color is not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt3 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND courseName = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt3.clearParameters();               // clear the parms
                        pstmt3.setString(1, name);              // put the parms in stmt
                        pstmt3.setString(2, color);
                        pstmt3.setLong(3, sdate);
                        pstmt3.setLong(4, edate);
                        pstmt3.setInt(5, stime);
                        pstmt3.setInt(6, etime);
                        pstmt3.setString(7, courseName);
                        pstmt3.setString(8, def);
                        pstmt3.setString(9, omit);
                        count = pstmt3.executeUpdate();         // execute the prepared stmt

                        pstmt3.close();

                     } else {

                        PreparedStatement pstmt3b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Saturday'  " +
                          "AND day != 'Sunday' AND time >= ? AND " +
                          "time <= ? AND courseName = ? AND fb = ?) AND (rest_color = ? OR rest_color = ?))");

                        pstmt3b.clearParameters();               // clear the parms
                        pstmt3b.setString(1, name);              // put the parms in stmt
                        pstmt3b.setString(2, color);
                        pstmt3b.setLong(3, sdate);
                        pstmt3b.setLong(4, edate);
                        pstmt3b.setInt(5, stime);
                        pstmt3b.setInt(6, etime);
                        pstmt3b.setString(7, courseName);
                        pstmt3b.setInt(8, fb);
                        pstmt3b.setString(9, def);
                        pstmt3b.setString(10, omit);
                        count = pstmt3b.executeUpdate();         // execute the prepared stmt

                        pstmt3b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg7 = "Error7 in SystemUtils do1Rest: ";
                  errorMsg7 = errorMsg7 + e2.getMessage();                                 // build error msg

                  logError(errorMsg7);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt4 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND " +
                          "restriction = ? AND rest_color = ? AND courseName = ? ");

                        pstmt4.clearParameters();               // clear the parms
                        pstmt4.setString(1, name);              // put the parms in stmt
                        pstmt4.setString(2, color);
                        pstmt4.setLong(3, sdate);
                        pstmt4.setLong(4, edate);
                        pstmt4.setInt(5, stime);
                        pstmt4.setInt(6, etime);
                        pstmt4.setString(7, omit);
                        pstmt4.setString(8, omit);
                        pstmt4.setString(9, courseName);
                        count = pstmt4.executeUpdate();         // execute the prepared stmt

                        pstmt4.close();

                     } else {

                        PreparedStatement pstmt4b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND " +
                          "restriction = ? AND rest_color = ? AND courseName = ? AND fb = ?");

                        pstmt4b.clearParameters();               // clear the parms
                        pstmt4b.setString(1, name);              // put the parms in stmt
                        pstmt4b.setString(2, color);
                        pstmt4b.setLong(3, sdate);
                        pstmt4b.setLong(4, edate);
                        pstmt4b.setInt(5, stime);
                        pstmt4b.setInt(6, etime);
                        pstmt4b.setString(7, omit);
                        pstmt4b.setString(8, omit);
                        pstmt4b.setString(9, courseName);
                        pstmt4b.setInt(10, fb);
                        count = pstmt4b.executeUpdate();         // execute the prepared stmt

                        pstmt4b.close();
                     }

                  } else {     // rest color is not default

                     if (sfb.equals( "Both" )) {

                        PreparedStatement pstmt4 = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND courseName = ?) AND " +
                          "(rest_color = ? OR rest_color = ?))");

                        pstmt4.clearParameters();               // clear the parms
                        pstmt4.setString(1, name);              // put the parms in stmt
                        pstmt4.setString(2, color);
                        pstmt4.setLong(3, sdate);
                        pstmt4.setLong(4, edate);
                        pstmt4.setInt(5, stime);
                        pstmt4.setInt(6, etime);
                        pstmt4.setString(7, courseName);
                        pstmt4.setString(8, def);
                        pstmt4.setString(9, omit);
                        count = pstmt4.executeUpdate();         // execute the prepared stmt

                        pstmt4.close();

                     } else {

                        PreparedStatement pstmt4b = con.prepareStatement (
                          "UPDATE teecurr2 SET restriction = ?, rest_color = ? WHERE " +
                          "((date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                          "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                          "AND time >= ? AND time <= ? AND courseName = ? AND fb = ?) AND " +
                          "(rest_color = ? OR rest_color = ?))");

                        pstmt4b.clearParameters();               // clear the parms
                        pstmt4b.setString(1, name);              // put the parms in stmt
                        pstmt4b.setString(2, color);
                        pstmt4b.setLong(3, sdate);
                        pstmt4b.setLong(4, edate);
                        pstmt4b.setInt(5, stime);
                        pstmt4b.setInt(6, etime);
                        pstmt4b.setString(7, courseName);
                        pstmt4b.setInt(8, fb);
                        pstmt4b.setString(9, def);
                        pstmt4b.setString(10, omit);
                        count = pstmt4b.executeUpdate();         // execute the prepared stmt

                        pstmt4b.close();
                     }
                  }
               }
               catch (Exception e2) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg8 = "Error8 in SystemUtils do1Rest: ";
                  errorMsg8 = errorMsg8 + e2.getMessage();                                 // build error msg

                  logError(errorMsg8);                                       // log it
               }

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for restriction table
      pstmt1.close();
   }
   catch (Exception e9) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg9 = "Error9 in SystemUtils do1Rest: ";
      errorMsg9 = errorMsg9 + e9.getMessage();                                 // build error msg

      logError(errorMsg9);                                       // log it
   }

 }  // end of do1Rest


 //************************************************************************
 // doFives - Scans the 5-some restriction tables for
 //           new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doFives(Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;

   String name = "";

   //
   //****************************************************************
   //  Get all the 5-some restrictions currently in the fives2 table
   //****************************************************************
   //
   try {

      stmt2 = con.createStatement();        // create a statement

      rs = stmt2.executeQuery("SELECT name " +
                              "FROM fives2");

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  go process the single rest
         //
         do1Five(con, name);

      }               // end of while for restriction table
      stmt2.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils doFives: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of doFives


 //************************************************************************
 // do1Five - Update teecurr for the 5-some restriction passed
 //
 //   called by:  Proshop_addfives
 //                      _editfives
 //               doFives (above)
 //
 //************************************************************************

 public static void do1Five(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // restriction table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //****************************************************************
   //  Get the 5-some restriction and process teecurr
   //****************************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, color, courseName, fb FROM fives2 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         color = rs.getString(6);
         courseName = rs.getString(7);
         sfb = rs.getString(8);

         day = "";    // init day value

         //
         //  Put restriction info in teecurr2 slots if matching date/time and restriction not already there
         //  (this is done based on recurrence sepcified in restriction)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52 = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? ");

                  PreparedStatement pstmt55 = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ?) AND (rest5_color = ? OR rest5_color = ?))");

                  PreparedStatement pstmt52b = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                  PreparedStatement pstmt55b = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND fb = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52.clearParameters();               // clear the parms
                        pstmt52.setString(1, name);              // put the parms in stmt
                        pstmt52.setString(2, color);
                        pstmt52.setLong(3, sdate);
                        pstmt52.setLong(4, edate);
                        pstmt52.setString(5, day);
                        pstmt52.setInt(6, stime);
                        pstmt52.setInt(7, etime);
                        pstmt52.setString(8, omit);
                        pstmt52.setString(9, omit);
                        count = pstmt52.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52b.clearParameters();               // clear the parms
                        pstmt52b.setString(1, name);              // put the parms in stmt
                        pstmt52b.setString(2, color);
                        pstmt52b.setLong(3, sdate);
                        pstmt52b.setLong(4, edate);
                        pstmt52b.setString(5, day);
                        pstmt52b.setInt(6, stime);
                        pstmt52b.setInt(7, etime);
                        pstmt52b.setString(8, omit);
                        pstmt52b.setString(9, omit);
                        pstmt52b.setInt(10, fb);
                        count = pstmt52b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55.clearParameters();               // clear the parms
                        pstmt55.setString(1, name);              // put the parms in stmt
                        pstmt55.setString(2, color);
                        pstmt55.setLong(3, sdate);
                        pstmt55.setLong(4, edate);
                        pstmt55.setString(5, day);
                        pstmt55.setInt(6, stime);
                        pstmt55.setInt(7, etime);
                        pstmt55.setString(8, def);
                        pstmt55.setString(9, omit);
                        count = pstmt55.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55b.clearParameters();               // clear the parms
                        pstmt55b.setString(1, name);              // put the parms in stmt
                        pstmt55b.setString(2, color);
                        pstmt55b.setLong(3, sdate);
                        pstmt55b.setLong(4, edate);
                        pstmt55b.setString(5, day);
                        pstmt55b.setInt(6, stime);
                        pstmt55b.setInt(7, etime);
                        pstmt55b.setInt(8, fb);
                        pstmt55b.setString(9, def);
                        pstmt55b.setString(10, omit);
                        count = pstmt55b.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52.close();
                  pstmt55.close();
                  pstmt52b.close();
                  pstmt55b.close();
               }
               catch (Exception e10) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg10 = "Error10 in SystemUtils do1Five: ";
                  errorMsg10 = errorMsg10 + e10.getMessage();                                 // build error msg

                  logError(errorMsg10);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt51 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? ");

                     pstmt51.clearParameters();               // clear the parms
                     pstmt51.setString(1, name);              // put the parms in stmt
                     pstmt51.setString(2, color);
                     pstmt51.setLong(3, sdate);
                     pstmt51.setLong(4, edate);
                     pstmt51.setInt(5, stime);
                     pstmt51.setInt(6, etime);
                     pstmt51.setString(7, omit);
                     pstmt51.setString(8, omit);
                     count = pstmt51.executeUpdate();         // execute the prepared stmt

                     pstmt51.close();

                  } else {

                     PreparedStatement pstmt51b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt51b.clearParameters();               // clear the parms
                     pstmt51b.setString(1, name);              // put the parms in stmt
                     pstmt51b.setString(2, color);
                     pstmt51b.setLong(3, sdate);
                     pstmt51b.setLong(4, edate);
                     pstmt51b.setInt(5, stime);
                     pstmt51b.setInt(6, etime);
                     pstmt51b.setString(7, omit);
                     pstmt51b.setString(8, omit);
                     pstmt51b.setInt(9, fb);
                     count = pstmt51b.executeUpdate();         // execute the prepared stmt

                     pstmt51b.close();
                  }
               }
               catch (Exception e11) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg11 = "Error11 in SystemUtils do1Five: ";
                  errorMsg11 = errorMsg11 + e11.getMessage();                                 // build error msg

                  logError(errorMsg11);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt53 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? ");

                     pstmt53.clearParameters();               // clear the parms
                     pstmt53.setString(1, name);              // put the parms in stmt
                     pstmt53.setString(2, color);
                     pstmt53.setLong(3, sdate);
                     pstmt53.setLong(4, edate);
                     pstmt53.setInt(5, stime);
                     pstmt53.setInt(6, etime);
                     pstmt53.setString(7, omit);
                     pstmt53.setString(8, omit);
                     count = pstmt53.executeUpdate();         // execute the prepared stmt

                     pstmt53.close();

                  } else {

                     PreparedStatement pstmt53b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt53b.clearParameters();               // clear the parms
                     pstmt53b.setString(1, name);              // put the parms in stmt
                     pstmt53b.setString(2, color);
                     pstmt53b.setLong(3, sdate);
                     pstmt53b.setLong(4, edate);
                     pstmt53b.setInt(5, stime);
                     pstmt53b.setInt(6, etime);
                     pstmt53b.setString(7, omit);
                     pstmt53b.setString(8, omit);
                     pstmt53b.setInt(9, fb);
                     count = pstmt53b.executeUpdate();         // execute the prepared stmt

                     pstmt53b.close();
                  }
               }
               catch (Exception e12) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg12 = "Error12 in SystemUtils do1Five: ";
                  errorMsg12 = errorMsg12 + e12.getMessage();                                 // build error msg

                  logError(errorMsg12);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt54 = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "rest5 = ? AND rest5_color = ? ");

                     pstmt54.clearParameters();               // clear the parms
                     pstmt54.setString(1, name);              // put the parms in stmt
                     pstmt54.setString(2, color);
                     pstmt54.setLong(3, sdate);
                     pstmt54.setLong(4, edate);
                     pstmt54.setInt(5, stime);
                     pstmt54.setInt(6, etime);
                     pstmt54.setString(7, omit);
                     pstmt54.setString(8, omit);
                     count = pstmt54.executeUpdate();         // execute the prepared stmt

                     pstmt54.close();

                  } else {

                     PreparedStatement pstmt54b = con.prepareStatement (
                       "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "rest5 = ? AND rest5_color = ? AND fb = ?");

                     pstmt54b.clearParameters();               // clear the parms
                     pstmt54b.setString(1, name);              // put the parms in stmt
                     pstmt54b.setString(2, color);
                     pstmt54b.setLong(3, sdate);
                     pstmt54b.setLong(4, edate);
                     pstmt54b.setInt(5, stime);
                     pstmt54b.setInt(6, etime);
                     pstmt54b.setString(7, omit);
                     pstmt54b.setString(8, omit);
                     pstmt54b.setInt(9, fb);
                     count = pstmt54b.executeUpdate();         // execute the prepared stmt

                     pstmt54b.close();
                  }
               }
               catch (Exception e13) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg13 = "Error13 in SystemUtils do1Five: ";
                  errorMsg13 = errorMsg13 + e13.getMessage();                                 // build error msg

                  logError(errorMsg13);                                       // log it
               }

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? ");

                  PreparedStatement pstmt55a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  PreparedStatement pstmt52ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  PreparedStatement pstmt55ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ? AND fb = ?) AND (rest5_color = ? OR rest5_color = ?))");

                  //
                  //   If restriction color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52a.clearParameters();               // clear the parms
                        pstmt52a.setString(1, name);              // put the parms in stmt
                        pstmt52a.setString(2, color);
                        pstmt52a.setLong(3, sdate);
                        pstmt52a.setLong(4, edate);
                        pstmt52a.setString(5, day);
                        pstmt52a.setInt(6, stime);
                        pstmt52a.setInt(7, etime);
                        pstmt52a.setString(8, omit);
                        pstmt52a.setString(9, omit);
                        pstmt52a.setString(10, courseName);
                        count = pstmt52a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52ab.clearParameters();               // clear the parms
                        pstmt52ab.setString(1, name);              // put the parms in stmt
                        pstmt52ab.setString(2, color);
                        pstmt52ab.setLong(3, sdate);
                        pstmt52ab.setLong(4, edate);
                        pstmt52ab.setString(5, day);
                        pstmt52ab.setInt(6, stime);
                        pstmt52ab.setInt(7, etime);
                        pstmt52ab.setString(8, omit);
                        pstmt52ab.setString(9, omit);
                        pstmt52ab.setString(10, courseName);
                        pstmt52ab.setInt(11, fb);
                        count = pstmt52ab.executeUpdate();         // execute the prepared stmt
                     }
                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55a.clearParameters();               // clear the parms
                        pstmt55a.setString(1, name);              // put the parms in stmt
                        pstmt55a.setString(2, color);
                        pstmt55a.setLong(3, sdate);
                        pstmt55a.setLong(4, edate);
                        pstmt55a.setString(5, day);
                        pstmt55a.setInt(6, stime);
                        pstmt55a.setInt(7, etime);
                        pstmt55a.setString(8, courseName);
                        pstmt55a.setString(9, def);
                        pstmt55a.setString(10, omit);
                        count = pstmt55a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55a.clearParameters();               // clear the parms
                        pstmt55a.setString(1, name);              // put the parms in stmt
                        pstmt55a.setString(2, color);
                        pstmt55a.setLong(3, sdate);
                        pstmt55a.setLong(4, edate);
                        pstmt55a.setString(5, day);
                        pstmt55a.setInt(6, stime);
                        pstmt55a.setInt(7, etime);
                        pstmt55a.setString(8, courseName);
                        pstmt55ab.setInt(9, fb);
                        pstmt55a.setString(10, def);
                        pstmt55a.setString(11, omit);
                        count = pstmt55a.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52a.close();
                  pstmt55a.close();
                  pstmt52ab.close();
                  pstmt55ab.close();
               }
               catch (Exception e14) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg14 = "Error14 in SystemUtils do1Five: ";
                  errorMsg14 = errorMsg14 + e14.getMessage();                                 // build error msg

                  logError(errorMsg14);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               try {
                  PreparedStatement pstmt51a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt51ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt51a.clearParameters();               // clear the parms
                     pstmt51a.setString(1, name);              // put the parms in stmt
                     pstmt51a.setString(2, color);
                     pstmt51a.setLong(3, sdate);
                     pstmt51a.setLong(4, edate);
                     pstmt51a.setInt(5, stime);
                     pstmt51a.setInt(6, etime);
                     pstmt51a.setString(7, omit);
                     pstmt51a.setString(8, omit);
                     pstmt51a.setString(9, courseName);
                     count = pstmt51a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt51ab.clearParameters();               // clear the parms
                     pstmt51ab.setString(1, name);              // put the parms in stmt
                     pstmt51ab.setString(2, color);
                     pstmt51ab.setLong(3, sdate);
                     pstmt51ab.setLong(4, edate);
                     pstmt51ab.setInt(5, stime);
                     pstmt51ab.setInt(6, etime);
                     pstmt51ab.setString(7, omit);
                     pstmt51ab.setString(8, omit);
                     pstmt51ab.setString(9, courseName);
                     pstmt51ab.setInt(10, fb);
                     count = pstmt51ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt51a.close();
                  pstmt51ab.close();
               }
               catch (Exception e15) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg15 = "Error15 in SystemUtils do1Five: ";
                  errorMsg15 = errorMsg15 + e15.getMessage();                                 // build error msg

                  logError(errorMsg15);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {
                  PreparedStatement pstmt53a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt53ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt53a.clearParameters();               // clear the parms
                     pstmt53a.setString(1, name);              // put the parms in stmt
                     pstmt53a.setString(2, color);
                     pstmt53a.setLong(3, sdate);
                     pstmt53a.setLong(4, edate);
                     pstmt53a.setInt(5, stime);
                     pstmt53a.setInt(6, etime);
                     pstmt53a.setString(7, omit);
                     pstmt53a.setString(8, omit);
                     pstmt53a.setString(9, courseName);
                     count = pstmt53a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt53ab.clearParameters();               // clear the parms
                     pstmt53ab.setString(1, name);              // put the parms in stmt
                     pstmt53ab.setString(2, color);
                     pstmt53ab.setLong(3, sdate);
                     pstmt53ab.setLong(4, edate);
                     pstmt53ab.setInt(5, stime);
                     pstmt53ab.setInt(6, etime);
                     pstmt53ab.setString(7, omit);
                     pstmt53ab.setString(8, omit);
                     pstmt53ab.setString(9, courseName);
                     pstmt53ab.setInt(10, fb);
                     count = pstmt53ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt53a.close();
                  pstmt53ab.close();
               }
               catch (Exception e16) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg16 = "Error16 in SystemUtils do1Five: ";
                  errorMsg16 = errorMsg16 + e16.getMessage();                                 // build error msg

                  logError(errorMsg16);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {
                  PreparedStatement pstmt54a = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "rest5 = ? AND rest5_color = ? AND courseName = ?");

                  PreparedStatement pstmt54ab = con.prepareStatement (
                    "UPDATE teecurr2 SET rest5 = ?, rest5_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "rest5 = ? AND rest5_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt54a.clearParameters();               // clear the parms
                     pstmt54a.setString(1, name);              // put the parms in stmt
                     pstmt54a.setString(2, color);
                     pstmt54a.setLong(3, sdate);
                     pstmt54a.setLong(4, edate);
                     pstmt54a.setInt(5, stime);
                     pstmt54a.setInt(6, etime);
                     pstmt54a.setString(7, omit);
                     pstmt54a.setString(8, omit);
                     pstmt54a.setString(9, courseName);
                     count = pstmt54a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt54ab.clearParameters();               // clear the parms
                     pstmt54ab.setString(1, name);              // put the parms in stmt
                     pstmt54ab.setString(2, color);
                     pstmt54ab.setLong(3, sdate);
                     pstmt54ab.setLong(4, edate);
                     pstmt54ab.setInt(5, stime);
                     pstmt54ab.setInt(6, etime);
                     pstmt54ab.setString(7, omit);
                     pstmt54ab.setString(8, omit);
                     pstmt54ab.setString(9, courseName);
                     pstmt54ab.setInt(10, fb);
                     count = pstmt54ab.executeUpdate();         // execute the prepared stmt
                  }

                  pstmt54a.close();
                  pstmt54ab.close();
               }
               catch (Exception e17) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg17 = "Error17 in SystemUtils do1Five: ";
                  errorMsg17 = errorMsg17 + e17.getMessage();                                 // build error msg

                  logError(errorMsg17);                                       // log it
               }

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for restriction table
      pstmt1.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils do1Five: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of do1Five


 //************************************************************************
 // doEvents - Scans the event tables for
 //            new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doEvents(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   String name = "";

   //
   //*****************************************************
   //  Get all the events currently in the event table
   //*****************************************************
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT name FROM events2b");

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  go process the single event
         //
         do1Event(con, name);

      }  // end of while for events table

      stmt.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils doEvents: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of doEvents


 //************************************************************************
 // do1Event - Update teecurr for the single event.
 //
 //
 //   called by:  Proshop_editevnt
 //               doEvents (above)
 //
 //************************************************************************

 public static void do1Event(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // event table variables
   int stime = 0;
   int etime = 0;
   int stime2 = 0;
   int etime2 = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;
   int fb2 = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String sfb2 = "";
   String omit = "";
   String color = "";

   //
   //  Get today's date and the current time for event processing
   //
   Calendar cal = new GregorianCalendar();            // get todays date

   int yy = cal.get(Calendar.YEAR);
   int mm = cal.get(Calendar.MONTH);
   int dd = cal.get(Calendar.DAY_OF_MONTH);
   mm++;                                                  // month starts at zero
   long today_date = (yy * 10000) + (mm * 100) + dd;      // create a date field of yyyymmdd

   //
   //*****************************************************
   //  Get the event and use it to update teecurr
   //*****************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT date, stime, etime, color, type, courseName, fb, stime2, etime2, fb2 " +
              "FROM events2b WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         date = rs.getLong("date");
         stime = rs.getInt("stime");
         etime = rs.getInt("etime");
         color = rs.getString("color");
         type = rs.getInt("type");
         courseName = rs.getString("courseName");
         sfb = rs.getString("fb");
         stime2 = rs.getInt("stime2");
         etime2 = rs.getInt("etime2");
         sfb2 = rs.getString("fb2");

         if (date >= today_date) {           // if event hasn't passed

            //
            //  prepare F/B indicators
            //
            //     sfb = Front, Back or Both
            //
            fb = 0;                       // init to Front
            fb2 = 0;                  

            if (sfb.equals( "Back" )) {
               fb = 1;     // back
            }
            if (sfb2.equals( "Back" )) {
               fb2 = 1;     // back
            }

            //
            //  Put event info in teecurr slots if matching date/time and event not already there
            //
            try {

               if (courseName.equals( "-ALL-" )) {    // if ALL Courses

                  if (sfb.equalsIgnoreCase( "both" )) {

                     PreparedStatement pstmte1 = con.prepareStatement (
                       "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                       "time >= ? AND time <= ? AND event = ? AND event_color = ? ");

                     pstmte1.clearParameters();               // clear the parms
                     pstmte1.setString(1, name);              // put the parms in stmt
                     pstmte1.setString(2, color);
                     pstmte1.setInt(3, type);
                     pstmte1.setLong(4, date);
                     pstmte1.setInt(5, stime);
                     pstmte1.setInt(6, etime);
                     pstmte1.setString(7, omit);
                     pstmte1.setString(8, omit);
                     count = pstmte1.executeUpdate();         // execute the prepared stmt

                     pstmte1.close();

                  } else {

                     PreparedStatement pstmte2 = con.prepareStatement (
                       "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                       "time >= ? AND time <= ? AND event = ? AND event_color = ? AND fb = ?");

                     pstmte2.clearParameters();               // clear the parms
                     pstmte2.setString(1, name);              // put the parms in stmt
                     pstmte2.setString(2, color);
                     pstmte2.setInt(3, type);
                     pstmte2.setLong(4, date);
                     pstmte2.setInt(5, stime);
                     pstmte2.setInt(6, etime);
                     pstmte2.setString(7, omit);
                     pstmte2.setString(8, omit);
                     pstmte2.setInt(9, fb);
                     count = pstmte2.executeUpdate();         // execute the prepared stmt

                     pstmte2.close();
                  }
                  //
                  //  now do second set of blockers if necessary
                  //
                  if (stime2 != etime2) {      // if 2nd blockers requested
                    
                     if (sfb2.equalsIgnoreCase( "both" )) {

                        PreparedStatement pstmte1 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? ");

                        pstmte1.clearParameters();               // clear the parms
                        pstmte1.setString(1, name);              // put the parms in stmt
                        pstmte1.setString(2, color);
                        pstmte1.setInt(3, type);
                        pstmte1.setLong(4, date);
                        pstmte1.setInt(5, stime2);
                        pstmte1.setInt(6, etime2);
                        pstmte1.setString(7, omit);
                        pstmte1.setString(8, omit);
                        count = pstmte1.executeUpdate();         // execute the prepared stmt

                        pstmte1.close();

                     } else {

                        PreparedStatement pstmte2 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND fb = ?");

                        pstmte2.clearParameters();               // clear the parms
                        pstmte2.setString(1, name);              // put the parms in stmt
                        pstmte2.setString(2, color);
                        pstmte2.setInt(3, type);
                        pstmte2.setLong(4, date);
                        pstmte2.setInt(5, stime2);
                        pstmte2.setInt(6, etime2);
                        pstmte2.setString(7, omit);
                        pstmte2.setString(8, omit);
                        pstmte2.setInt(9, fb2);
                        count = pstmte2.executeUpdate();         // execute the prepared stmt

                        pstmte2.close();
                     }
                  }

               } else {     // courseName != ALL

                  if (sfb.equalsIgnoreCase( "both" )) {

                     PreparedStatement pstmta = con.prepareStatement (
                       "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                       "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ?");

                     pstmta.clearParameters();               // clear the parms
                     pstmta.setString(1, name);              // put the parms in stmt
                     pstmta.setString(2, color);
                     pstmta.setInt(3, type);
                     pstmta.setLong(4, date);
                     pstmta.setInt(5, stime);
                     pstmta.setInt(6, etime);
                     pstmta.setString(7, omit);
                     pstmta.setString(8, omit);
                     pstmta.setString(9, courseName);
                     count = pstmta.executeUpdate();         // execute the prepared stmt

                     pstmta.close();

                  } else {

                     PreparedStatement pstmta2 = con.prepareStatement (
                       "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                       "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ? AND fb = ?");

                     pstmta2.clearParameters();               // clear the parms
                     pstmta2.setString(1, name);              // put the parms in stmt
                     pstmta2.setString(2, color);
                     pstmta2.setInt(3, type);
                     pstmta2.setLong(4, date);
                     pstmta2.setInt(5, stime);
                     pstmta2.setInt(6, etime);
                     pstmta2.setString(7, omit);
                     pstmta2.setString(8, omit);
                     pstmta2.setString(9, courseName);
                     pstmta2.setInt(10, fb);
                     count = pstmta2.executeUpdate();         // execute the prepared stmt

                     pstmta2.close();
                  }
                  //
                  //  now do second set of blockers if necessary
                  //
                  if (stime2 != etime2) {      // if 2nd blockers requested

                     if (sfb2.equalsIgnoreCase( "both" )) {

                        PreparedStatement pstmta = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ?");

                        pstmta.clearParameters();               // clear the parms
                        pstmta.setString(1, name);              // put the parms in stmt
                        pstmta.setString(2, color);
                        pstmta.setInt(3, type);
                        pstmta.setLong(4, date);
                        pstmta.setInt(5, stime2);
                        pstmta.setInt(6, etime2);
                        pstmta.setString(7, omit);
                        pstmta.setString(8, omit);
                        pstmta.setString(9, courseName);
                        count = pstmta.executeUpdate();         // execute the prepared stmt

                        pstmta.close();

                     } else {

                        PreparedStatement pstmta2 = con.prepareStatement (
                          "UPDATE teecurr2 SET event = ?, event_color = ?, event_type = ? WHERE date = ? AND " +
                          "time >= ? AND time <= ? AND event = ? AND event_color = ? AND courseName = ? AND fb = ?");

                        pstmta2.clearParameters();               // clear the parms
                        pstmta2.setString(1, name);              // put the parms in stmt
                        pstmta2.setString(2, color);
                        pstmta2.setInt(3, type);
                        pstmta2.setLong(4, date);
                        pstmta2.setInt(5, stime2);
                        pstmta2.setInt(6, etime2);
                        pstmta2.setString(7, omit);
                        pstmta2.setString(8, omit);
                        pstmta2.setString(9, courseName);
                        pstmta2.setInt(10, fb2);
                        count = pstmta2.executeUpdate();         // execute the prepared stmt

                        pstmta2.close();
                     }
                  }
               }
            }
            catch (Exception e19) {
               //
               //  save error message in /v_x/error.txt
               //
               String errorMsg19 = "Error19 in SystemUtils do1Event: ";
               errorMsg19 = errorMsg19 + e19.getMessage();                                 // build error msg

               logError(errorMsg19);                                       // log it
            }
         }     // end of IF date ok

      }  // end of IF event

      pstmt1.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils do1Event: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of do1Event


 //************************************************************************
 // doBlockers - Scans the block tables for
 //              new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doBlockers(Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;

   String name = "";

   //
   //*****************************************************
   //  Get all the blockers currently in the block table
   //*****************************************************
   //
   try {

      stmt2 = con.createStatement();        // create a statement

      rs = stmt2.executeQuery("SELECT name " +
                              "FROM block2");

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  go process the single blocker
         //
         do1Blocker(con, name);

      }               // end of while for restriction table
      stmt2.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils doBlockers: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of doBlockers


 //************************************************************************
 // do1Blocker - Update teecurr for the named blocker
 //
 //   called by:  Proshop_addblock
 //                  "   _editblock
 //               doBlcokers (above)
 //
 //************************************************************************

 public static void do1Blocker(Connection con, String name) {


   ResultSet rs = null;


   long date = 0;                 // event table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;                 // restriction table variables
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //*****************************************************
   //  Get the blocker and process teecurr
   //*****************************************************
   //
   try {

      PreparedStatement pstmtx = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, courseName, fb FROM block2 WHERE name = ?");

      pstmtx.clearParameters();        // clear the parms
      pstmtx.setString(1, name);       // put the parm in stmt
      rs = pstmtx.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         courseName = rs.getString(6);
         sfb = rs.getString(7);

         day = "";    // init day value

         //
         //  Put blocker info in teecurr2 slots if matching date/time and restriction not already there
         //  (this is done based on recurrence sepcified in restriction)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               PreparedStatement pstmt5 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE auto_blocked = 0 AND date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                 "time <= ?");

               PreparedStatement pstmt5b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE auto_blocked = 0 AND date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                 "time <= ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt5.clearParameters();               // clear the parms
                     pstmt5.setString(1, name);              // put the parms in stmt
                     pstmt5.setLong(2, sdate);
                     pstmt5.setLong(3, edate);
                     pstmt5.setString(4, day);
                     pstmt5.setInt(5, stime);
                     pstmt5.setInt(6, etime);
                     count = pstmt5.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt5b.clearParameters();               // clear the parms
                     pstmt5b.setString(1, name);              // put the parms in stmt
                     pstmt5b.setLong(2, sdate);
                     pstmt5b.setLong(3, edate);
                     pstmt5b.setString(4, day);
                     pstmt5b.setInt(5, stime);
                     pstmt5b.setInt(6, etime);
                     pstmt5b.setInt(7, fb);
                     count = pstmt5b.executeUpdate();         // execute the prepared stmt
                  }
               pstmt5.close();
               pstmt5b.close();

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               PreparedStatement pstmt1 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? ");

               PreparedStatement pstmt1b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt1.clearParameters();               // clear the parms
                  pstmt1.setString(1, name);              // put the parms in stmt
                  pstmt1.setLong(2, sdate);
                  pstmt1.setLong(3, edate);
                  pstmt1.setInt(4, stime);
                  pstmt1.setInt(5, etime);
                  count = pstmt1.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt1b.clearParameters();               // clear the parms
                  pstmt1b.setString(1, name);              // put the parms in stmt
                  pstmt1b.setLong(2, sdate);
                  pstmt1b.setLong(3, edate);
                  pstmt1b.setInt(4, stime);
                  pstmt1b.setInt(5, etime);
                  pstmt1b.setInt(6, fb);
                  count = pstmt1b.executeUpdate();         // execute the prepared stmt
               }
               pstmt1.close();
               pstmt1b.close();

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               PreparedStatement pstmt3 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ?");

               PreparedStatement pstmt3b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt3.clearParameters();               // clear the parms
                  pstmt3.setString(1, name);              // put the parms in stmt
                  pstmt3.setLong(2, sdate);
                  pstmt3.setLong(3, edate);
                  pstmt3.setInt(4, stime);
                  pstmt3.setInt(5, etime);
                  count = pstmt3.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt3b.clearParameters();               // clear the parms
                  pstmt3b.setString(1, name);              // put the parms in stmt
                  pstmt3b.setLong(2, sdate);
                  pstmt3b.setLong(3, edate);
                  pstmt3b.setInt(4, stime);
                  pstmt3b.setInt(5, etime);
                  pstmt3b.setInt(6, fb);
                  count = pstmt3b.executeUpdate();         // execute the prepared stmt
               }
               pstmt3.close();
               pstmt3b.close();

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               PreparedStatement pstmt4 = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? ");

               PreparedStatement pstmt4b = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt4.clearParameters();               // clear the parms
                  pstmt4.setString(1, name);              // put the parms in stmt
                  pstmt4.setLong(2, sdate);
                  pstmt4.setLong(3, edate);
                  pstmt4.setInt(4, stime);
                  pstmt4.setInt(5, etime);
                  count = pstmt4.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt4b.clearParameters();               // clear the parms
                  pstmt4b.setString(1, name);              // put the parms in stmt
                  pstmt4b.setLong(2, sdate);
                  pstmt4b.setLong(3, edate);
                  pstmt4b.setInt(4, stime);
                  pstmt4b.setInt(5, etime);
                  pstmt4b.setInt(6, fb);
                  count = pstmt4b.executeUpdate();         // execute the prepared stmt
               }
               pstmt4b.close();
               pstmt4.close();

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               PreparedStatement pstmt5a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND auto_blocked = 0 AND " +
                 "time <= ? AND courseName = ?))");

               PreparedStatement pstmt5ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? " +
                 "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND auto_blocked = 0 AND " +
                 "time <= ? AND courseName = ? AND fb = ?))");

               if (sfb.equals( "Both" )) {

                  pstmt5a.clearParameters();               // clear the parms
                  pstmt5a.setString(1, name);              // put the parms in stmt
                  pstmt5a.setLong(2, sdate);
                  pstmt5a.setLong(3, edate);
                  pstmt5a.setString(4, day);
                  pstmt5a.setInt(5, stime);
                  pstmt5a.setInt(6, etime);
                  pstmt5a.setString(7, courseName);
                  count = pstmt5a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt5ab.clearParameters();               // clear the parms
                  pstmt5ab.setString(1, name);              // put the parms in stmt
                  pstmt5ab.setLong(2, sdate);
                  pstmt5ab.setLong(3, edate);
                  pstmt5ab.setString(4, day);
                  pstmt5ab.setInt(5, stime);
                  pstmt5ab.setInt(6, etime);
                  pstmt5ab.setString(7, courseName);
                  pstmt5ab.setInt(8, fb);
                  count = pstmt5ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt5ab.close();
               pstmt5a.close();

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               PreparedStatement pstmt1a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND courseName = ?");

               PreparedStatement pstmt1ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND time >= ? AND " +
                 "time <= ? AND courseName = ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt1a.clearParameters();               // clear the parms
                  pstmt1a.setString(1, name);              // put the parms in stmt
                  pstmt1a.setLong(2, sdate);
                  pstmt1a.setLong(3, edate);
                  pstmt1a.setInt(4, stime);
                  pstmt1a.setInt(5, etime);
                  pstmt1a.setString(6, courseName);
                  count = pstmt1a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt1ab.clearParameters();               // clear the parms
                  pstmt1ab.setString(1, name);              // put the parms in stmt
                  pstmt1ab.setLong(2, sdate);
                  pstmt1ab.setLong(3, edate);
                  pstmt1ab.setInt(4, stime);
                  pstmt1ab.setInt(5, etime);
                  pstmt1ab.setString(6, courseName);
                  pstmt1ab.setInt(7, fb);
                  count = pstmt1ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt1ab.close();
               pstmt1a.close();

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               PreparedStatement pstmt3a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND courseName = ?");

               PreparedStatement pstmt3ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Saturday'  " +
                 "AND day != 'Sunday' AND time >= ? AND " +
                 "time <= ? AND courseName = ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt3a.clearParameters();               // clear the parms
                  pstmt3a.setString(1, name);              // put the parms in stmt
                  pstmt3a.setLong(2, sdate);
                  pstmt3a.setLong(3, edate);
                  pstmt3a.setInt(4, stime);
                  pstmt3a.setInt(5, etime);
                  pstmt3a.setString(6, courseName);
                  count = pstmt3a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt3ab.clearParameters();               // clear the parms
                  pstmt3ab.setString(1, name);              // put the parms in stmt
                  pstmt3ab.setLong(2, sdate);
                  pstmt3ab.setLong(3, edate);
                  pstmt3ab.setInt(4, stime);
                  pstmt3ab.setInt(5, etime);
                  pstmt3ab.setString(6, courseName);
                  pstmt3ab.setInt(7, fb);
                  count = pstmt3ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt3ab.close();
               pstmt3a.close();

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               PreparedStatement pstmt4a = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND courseName = ?");

               PreparedStatement pstmt4ab = con.prepareStatement (
                 "UPDATE teecurr2 SET blocker = ? WHERE auto_blocked = 0 AND " +
                 "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                 "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                 "AND time >= ? AND time <= ? AND courseName = ? AND fb = ?");

               if (sfb.equals( "Both" )) {

                  pstmt4a.clearParameters();               // clear the parms
                  pstmt4a.setString(1, name);              // put the parms in stmt
                  pstmt4a.setLong(2, sdate);
                  pstmt4a.setLong(3, edate);
                  pstmt4a.setInt(4, stime);
                  pstmt4a.setInt(5, etime);
                  pstmt4a.setString(6, courseName);
                  count = pstmt4a.executeUpdate();         // execute the prepared stmt

               } else {

                  pstmt4ab.clearParameters();               // clear the parms
                  pstmt4ab.setString(1, name);              // put the parms in stmt
                  pstmt4ab.setLong(2, sdate);
                  pstmt4ab.setLong(3, edate);
                  pstmt4ab.setInt(4, stime);
                  pstmt4ab.setInt(5, etime);
                  pstmt4ab.setString(6, courseName);
                  pstmt4ab.setInt(7, fb);
                  count = pstmt4ab.executeUpdate();         // execute the prepared stmt
               }
               pstmt4ab.close();
               pstmt4a.close();

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for restriction table
      pstmtx.close();
   }
   catch (Exception e20) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg20 = "Error20 in SystemUtils do1Blocker: ";
      errorMsg20 = errorMsg20 + e20.getMessage();                                 // build error msg

      logError(errorMsg20);                                       // log it
   }

 }  // end of do1Blocker


 //************************************************************************
 // doLotteries - Scans the Lottery tables for
 //               new/modified entries and updates teecurr2 if necessary.
 //
 //
 //   called by:  Proshop_updateTeecurr
 //
 //************************************************************************

 public static void doLotteries(Connection con) {


   Statement stmt2 = null;
   ResultSet rs = null;

   String name = "";

   //
   //****************************************************************
   //  Get the lottery and process teecurr
   //****************************************************************
   //
   try {

      stmt2 = con.createStatement();        // create a statement

      rs = stmt2.executeQuery("SELECT name " +
                              "FROM lottery3");

      while (rs.next()) {

         name = rs.getString(1);

         //
         //  go process the single lottery
         //
         do1Lottery(con, name);

      }               // end of while for lottery table
      stmt2.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils doLottery: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of doLotteries


 //************************************************************************
 // do1Lottery - Update teecurr for this lottery
 //
 //   called by:  Proshop_addlottery
 //                 "    _editlott
 //               doLotteries (above)
 //
 //************************************************************************

 public static void do1Lottery(Connection con, String name) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;


   long date = 0;                 // table variables
   int stime = 0;
   int etime = 0;
   int type = 0;

   long sdate = 0;
   long edate = 0;
   String recurr = "";
   String day = "";

   int count = 0;
   int fb = 0;

   String event_name = "";        // teecurr2 table variables
   String event_color = "";
   String def = "default";

   String course = "";           // common variables
   String courseName = "";
   String sfb = "";
   String omit = "";
   String color = "";

   //
   //****************************************************************
   //  Get the lottery and process teecurr
   //****************************************************************
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
              "SELECT sdate, stime, edate, etime, recurr, color, courseName, fb FROM lottery3 WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);       // put the parm in stmt
      rs = pstmt1.executeQuery();      // execute the prepared stmt
        
      if (rs.next()) {

         sdate = rs.getLong(1);
         stime = rs.getInt(2);
         edate = rs.getLong(3);
         etime = rs.getInt(4);
         recurr = rs.getString(5);
         color = rs.getString(6);
         courseName = rs.getString(7);
         sfb = rs.getString(8);

         day = "";    // init day value

         //
         //  Put lottery info in teecurr2 slots if matching date/time and lottery not already there
         //  (this is done based on recurrence specified in lottery)
         //
         if ( recurr.equalsIgnoreCase( "every sunday" ) ) {   // if every Sunday

            day = "Sunday";
         }

         if ( recurr.equalsIgnoreCase( "every monday" ) ) {   // if every Monday

            day = "Monday";
         }

         if ( recurr.equalsIgnoreCase( "every tuesday" ) ) {   // if every Tuesday

            day = "Tuesday";
         }

         if ( recurr.equalsIgnoreCase( "every wednesday" ) ) {   // if every Wednesday

            day = "Wednesday";
         }

         if ( recurr.equalsIgnoreCase( "every thursday" ) ) {   // if every Thursday

            day = "Thursday";
         }

         if ( recurr.equalsIgnoreCase( "every friday" ) ) {   // if every Friday

            day = "Friday";
         }

         if ( recurr.equalsIgnoreCase( "every saturday" ) ) {   // if every Satruday

            day = "Saturday";
         }

         //
         //  prepare F/B indicator
         //
         fb = 0;                       // init to Front

         if (sfb.equals( "Back" )) {

            fb = 1;     // back
         }

         //
         //   courseName = '-ALL-', or name of course, or null
         //
         //   sfb = 'Front', 'Back', or 'Both'
         //
         if (courseName.equals( "-ALL-" )) {    // if ALL Courses

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52 = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? ");

                  PreparedStatement pstmt55 = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ?) AND (lottery_color = ? OR lottery_color = ?))");

                  PreparedStatement pstmt52b = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                  PreparedStatement pstmt55b = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND fb = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  //
                  //   If lottery color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52.clearParameters();               // clear the parms
                        pstmt52.setString(1, name);              // put the parms in stmt
                        pstmt52.setString(2, color);
                        pstmt52.setLong(3, sdate);
                        pstmt52.setLong(4, edate);
                        pstmt52.setString(5, day);
                        pstmt52.setInt(6, stime);
                        pstmt52.setInt(7, etime);
                        pstmt52.setString(8, omit);
                        pstmt52.setString(9, omit);
                        count = pstmt52.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52b.clearParameters();               // clear the parms
                        pstmt52b.setString(1, name);              // put the parms in stmt
                        pstmt52b.setString(2, color);
                        pstmt52b.setLong(3, sdate);
                        pstmt52b.setLong(4, edate);
                        pstmt52b.setString(5, day);
                        pstmt52b.setInt(6, stime);
                        pstmt52b.setInt(7, etime);
                        pstmt52b.setString(8, omit);
                        pstmt52b.setString(9, omit);
                        pstmt52b.setInt(10, fb);
                        count = pstmt52b.executeUpdate();         // execute the prepared stmt
                     }

                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55.clearParameters();               // clear the parms
                        pstmt55.setString(1, name);              // put the parms in stmt
                        pstmt55.setString(2, color);
                        pstmt55.setLong(3, sdate);
                        pstmt55.setLong(4, edate);
                        pstmt55.setString(5, day);
                        pstmt55.setInt(6, stime);
                        pstmt55.setInt(7, etime);
                        pstmt55.setString(8, def);
                        pstmt55.setString(9, omit);
                        count = pstmt55.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55b.clearParameters();               // clear the parms
                        pstmt55b.setString(1, name);              // put the parms in stmt
                        pstmt55b.setString(2, color);
                        pstmt55b.setLong(3, sdate);
                        pstmt55b.setLong(4, edate);
                        pstmt55b.setString(5, day);
                        pstmt55b.setInt(6, stime);
                        pstmt55b.setInt(7, etime);
                        pstmt55b.setInt(8, fb);
                        pstmt55b.setString(9, def);
                        pstmt55b.setString(10, omit);
                        count = pstmt55b.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52.close();
                  pstmt55.close();
                  pstmt52b.close();
                  pstmt55b.close();
               }
               catch (Exception e10) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg10 = "Error10 in SystemUtils do1Lottery: ";
                  errorMsg10 = errorMsg10 + e10.getMessage();                                 // build error msg

                  logError(errorMsg10);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" )) {   // if everyday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt51 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? ");

                     pstmt51.clearParameters();               // clear the parms
                     pstmt51.setString(1, name);              // put the parms in stmt
                     pstmt51.setString(2, color);
                     pstmt51.setLong(3, sdate);
                     pstmt51.setLong(4, edate);
                     pstmt51.setInt(5, stime);
                     pstmt51.setInt(6, etime);
                     pstmt51.setString(7, omit);
                     pstmt51.setString(8, omit);
                     count = pstmt51.executeUpdate();         // execute the prepared stmt

                     pstmt51.close();

                  } else {

                     PreparedStatement pstmt51b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt51b.clearParameters();               // clear the parms
                     pstmt51b.setString(1, name);              // put the parms in stmt
                     pstmt51b.setString(2, color);
                     pstmt51b.setLong(3, sdate);
                     pstmt51b.setLong(4, edate);
                     pstmt51b.setInt(5, stime);
                     pstmt51b.setInt(6, etime);
                     pstmt51b.setString(7, omit);
                     pstmt51b.setString(8, omit);
                     pstmt51b.setInt(9, fb);
                     count = pstmt51b.executeUpdate();         // execute the prepared stmt

                     pstmt51b.close();
                  }
               }
               catch (Exception e11) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg11 = "Error11 in SystemUtils do1Lottery: ";
                  errorMsg11 = errorMsg11 + e11.getMessage();                                 // build error msg

                  logError(errorMsg11);                                       // log it
               }                      
            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt53 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? ");

                     pstmt53.clearParameters();               // clear the parms
                     pstmt53.setString(1, name);              // put the parms in stmt
                     pstmt53.setString(2, color);
                     pstmt53.setLong(3, sdate);
                     pstmt53.setLong(4, edate);
                     pstmt53.setInt(5, stime);
                     pstmt53.setInt(6, etime);
                     pstmt53.setString(7, omit);
                     pstmt53.setString(8, omit);
                     count = pstmt53.executeUpdate();         // execute the prepared stmt

                     pstmt53.close();

                  } else {

                     PreparedStatement pstmt53b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Saturday'  " +
                       "AND day != 'Sunday' AND time >= ? AND " +
                       "time <= ? AND lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt53b.clearParameters();               // clear the parms
                     pstmt53b.setString(1, name);              // put the parms in stmt
                     pstmt53b.setString(2, color);
                     pstmt53b.setLong(3, sdate);
                     pstmt53b.setLong(4, edate);
                     pstmt53b.setInt(5, stime);
                     pstmt53b.setInt(6, etime);
                     pstmt53b.setString(7, omit);
                     pstmt53b.setString(8, omit);
                     pstmt53b.setInt(9, fb);
                     count = pstmt53b.executeUpdate();         // execute the prepared stmt

                     pstmt53b.close();
                  }
               }
               catch (Exception e12) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg12 = "Error12 in SystemUtils do1Lottery: ";
                  errorMsg12 = errorMsg12 + e12.getMessage();                                 // build error msg

                  logError(errorMsg12);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {

                  if (sfb.equals( "Both" )) {

                     PreparedStatement pstmt54 = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "lottery = ? AND lottery_color = ? ");

                     pstmt54.clearParameters();               // clear the parms
                     pstmt54.setString(1, name);              // put the parms in stmt
                     pstmt54.setString(2, color);
                     pstmt54.setLong(3, sdate);
                     pstmt54.setLong(4, edate);
                     pstmt54.setInt(5, stime);
                     pstmt54.setInt(6, etime);
                     pstmt54.setString(7, omit);
                     pstmt54.setString(8, omit);
                     count = pstmt54.executeUpdate();         // execute the prepared stmt

                     pstmt54.close();

                  } else {

                     PreparedStatement pstmt54b = con.prepareStatement (
                       "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                       "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                       "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                       "AND time >= ? AND time <= ? AND " +
                       "lottery = ? AND lottery_color = ? AND fb = ?");

                     pstmt54b.clearParameters();               // clear the parms
                     pstmt54b.setString(1, name);              // put the parms in stmt
                     pstmt54b.setString(2, color);
                     pstmt54b.setLong(3, sdate);
                     pstmt54b.setLong(4, edate);
                     pstmt54b.setInt(5, stime);
                     pstmt54b.setInt(6, etime);
                     pstmt54b.setString(7, omit);
                     pstmt54b.setString(8, omit);
                     pstmt54b.setInt(9, fb);
                     count = pstmt54b.executeUpdate();         // execute the prepared stmt

                     pstmt54b.close();
                  }
               }
               catch (Exception e13) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg13 = "Error13 in SystemUtils do1Lottery: ";
                  errorMsg13 = errorMsg13 + e13.getMessage();                                 // build error msg

                  logError(errorMsg13);                                       // log it
               }

            }  // end of IF all weekends

         } else {     // courseName != ALL

            if (!day.equals( "" )) {    // if an individual day

               try {

                  PreparedStatement pstmt52a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? ");

                  PreparedStatement pstmt55a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  PreparedStatement pstmt52ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  PreparedStatement pstmt55ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? " +
                    "WHERE ((date >= ? AND date <= ? AND day = ? AND time >= ? AND " +
                    "time <= ? AND courseName = ? AND fb = ?) AND (lottery_color = ? OR lottery_color = ?))");

                  //
                  //   If lottery color is not 'default' and a rest exists in teecurr2 that is default, override
                  //
                  if (color.equalsIgnoreCase("default")) {    // if default color - only add if no other rest

                     if (sfb.equals( "Both" )) {

                        pstmt52a.clearParameters();               // clear the parms
                        pstmt52a.setString(1, name);              // put the parms in stmt
                        pstmt52a.setString(2, color);
                        pstmt52a.setLong(3, sdate);
                        pstmt52a.setLong(4, edate);
                        pstmt52a.setString(5, day);
                        pstmt52a.setInt(6, stime);
                        pstmt52a.setInt(7, etime);
                        pstmt52a.setString(8, omit);
                        pstmt52a.setString(9, omit);
                        pstmt52a.setString(10, courseName);
                        count = pstmt52a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt52ab.clearParameters();               // clear the parms
                        pstmt52ab.setString(1, name);              // put the parms in stmt
                        pstmt52ab.setString(2, color);
                        pstmt52ab.setLong(3, sdate);
                        pstmt52ab.setLong(4, edate);
                        pstmt52ab.setString(5, day);
                        pstmt52ab.setInt(6, stime);
                        pstmt52ab.setInt(7, etime);
                        pstmt52ab.setString(8, omit);
                        pstmt52ab.setString(9, omit);
                        pstmt52ab.setString(10, courseName);
                        pstmt52ab.setInt(11, fb);
                        count = pstmt52ab.executeUpdate();         // execute the prepared stmt
                     }
                  } else {

                     if (sfb.equals( "Both" )) {

                        pstmt55a.clearParameters();               // clear the parms
                        pstmt55a.setString(1, name);              // put the parms in stmt
                        pstmt55a.setString(2, color);
                        pstmt55a.setLong(3, sdate);
                        pstmt55a.setLong(4, edate);
                        pstmt55a.setString(5, day);
                        pstmt55a.setInt(6, stime);
                        pstmt55a.setInt(7, etime);
                        pstmt55a.setString(8, courseName);
                        pstmt55a.setString(9, def);
                        pstmt55a.setString(10, omit);
                        count = pstmt55a.executeUpdate();         // execute the prepared stmt

                     } else {

                        pstmt55ab.clearParameters();               // clear the parms
                        pstmt55ab.setString(1, name);              // put the parms in stmt
                        pstmt55ab.setString(2, color);
                        pstmt55ab.setLong(3, sdate);
                        pstmt55ab.setLong(4, edate);
                        pstmt55ab.setString(5, day);
                        pstmt55ab.setInt(6, stime);
                        pstmt55ab.setInt(7, etime);
                        pstmt55ab.setString(8, courseName);
                        pstmt55ab.setInt(9, fb);
                        pstmt55ab.setString(10, def);
                        pstmt55ab.setString(11, omit);
                        count = pstmt55ab.executeUpdate();         // execute the prepared stmt
                     }
                  }

                  pstmt52a.close();
                  pstmt55a.close();
                  pstmt52ab.close();
                  pstmt55ab.close();
               }
               catch (Exception e14) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg14 = "Error14 in SystemUtils do1Lottery: ";
                  errorMsg14 = errorMsg14 + e14.getMessage();                                 // build error msg

                  logError(errorMsg14);                                       // log it
               }

            }  // end of IF individual day

            if ( recurr.equalsIgnoreCase( "every day" ) ) {   // if everyday

               try {
                  PreparedStatement pstmt51a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt51ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt51a.clearParameters();               // clear the parms
                     pstmt51a.setString(1, name);              // put the parms in stmt
                     pstmt51a.setString(2, color);
                     pstmt51a.setLong(3, sdate);
                     pstmt51a.setLong(4, edate);
                     pstmt51a.setInt(5, stime);
                     pstmt51a.setInt(6, etime);
                     pstmt51a.setString(7, omit);
                     pstmt51a.setString(8, omit);
                     pstmt51a.setString(9, courseName);
                     count = pstmt51a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt51ab.clearParameters();               // clear the parms
                     pstmt51ab.setString(1, name);              // put the parms in stmt
                     pstmt51ab.setString(2, color);
                     pstmt51ab.setLong(3, sdate);
                     pstmt51ab.setLong(4, edate);
                     pstmt51ab.setInt(5, stime);
                     pstmt51ab.setInt(6, etime);
                     pstmt51ab.setString(7, omit);
                     pstmt51ab.setString(8, omit);
                     pstmt51ab.setString(9, courseName);
                     pstmt51ab.setInt(10, fb);
                     count = pstmt51ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt51a.close();
                  pstmt51ab.close();
               }
               catch (Exception e15) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg15 = "Error15 in SystemUtils do1Lottery: ";
                  errorMsg15 = errorMsg15 + e15.getMessage();                                 // build error msg

                  logError(errorMsg15);                                       // log it
               }

            }  // end of IF every day

            if ( recurr.equalsIgnoreCase( "all weekdays" ) ) {   // if Monday through Friday

               try {
                  PreparedStatement pstmt53a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt53ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Saturday'  " +
                    "AND day != 'Sunday' AND time >= ? AND " +
                    "time <= ? AND lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt53a.clearParameters();               // clear the parms
                     pstmt53a.setString(1, name);              // put the parms in stmt
                     pstmt53a.setString(2, color);
                     pstmt53a.setLong(3, sdate);
                     pstmt53a.setLong(4, edate);
                     pstmt53a.setInt(5, stime);
                     pstmt53a.setInt(6, etime);
                     pstmt53a.setString(7, omit);
                     pstmt53a.setString(8, omit);
                     pstmt53a.setString(9, courseName);
                     count = pstmt53a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt53ab.clearParameters();               // clear the parms
                     pstmt53ab.setString(1, name);              // put the parms in stmt
                     pstmt53ab.setString(2, color);
                     pstmt53ab.setLong(3, sdate);
                     pstmt53ab.setLong(4, edate);
                     pstmt53ab.setInt(5, stime);
                     pstmt53ab.setInt(6, etime);
                     pstmt53ab.setString(7, omit);
                     pstmt53ab.setString(8, omit);
                     pstmt53ab.setString(9, courseName);
                     pstmt53ab.setInt(10, fb);
                     count = pstmt53ab.executeUpdate();         // execute the prepared stmt
                  }
                  pstmt53a.close();
                  pstmt53ab.close();
               }
               catch (Exception e16) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg16 = "Error16 in SystemUtils do1Lottery: ";
                  errorMsg16 = errorMsg16 + e16.getMessage();                                 // build error msg

                  logError(errorMsg16);                                       // log it
               }

            }  // end of IF all weekdays

            if ( recurr.equalsIgnoreCase( "all weekends" ) ) {   // if Saturday & Sunday

               try {
                  PreparedStatement pstmt54a = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "lottery = ? AND lottery_color = ? AND courseName = ?");

                  PreparedStatement pstmt54ab = con.prepareStatement (
                    "UPDATE teecurr2 SET lottery = ?, lottery_color = ? WHERE " +
                    "date >= ? AND date <= ? AND day != 'Monday' AND day != 'Tuesday' " +
                    "AND day != 'Wednesday' AND day != 'Thursday' AND day != 'Friday' " +
                    "AND time >= ? AND time <= ? AND " +
                    "lottery = ? AND lottery_color = ? AND courseName = ? AND fb = ?");

                  if (sfb.equals( "Both" )) {

                     pstmt54a.clearParameters();               // clear the parms
                     pstmt54a.setString(1, name);              // put the parms in stmt
                     pstmt54a.setString(2, color);
                     pstmt54a.setLong(3, sdate);
                     pstmt54a.setLong(4, edate);
                     pstmt54a.setInt(5, stime);
                     pstmt54a.setInt(6, etime);
                     pstmt54a.setString(7, omit);
                     pstmt54a.setString(8, omit);
                     pstmt54a.setString(9, courseName);
                     count = pstmt54a.executeUpdate();         // execute the prepared stmt

                  } else {

                     pstmt54ab.clearParameters();               // clear the parms
                     pstmt54ab.setString(1, name);              // put the parms in stmt
                     pstmt54ab.setString(2, color);
                     pstmt54ab.setLong(3, sdate);
                     pstmt54ab.setLong(4, edate);
                     pstmt54ab.setInt(5, stime);
                     pstmt54ab.setInt(6, etime);
                     pstmt54ab.setString(7, omit);
                     pstmt54ab.setString(8, omit);
                     pstmt54ab.setString(9, courseName);
                     pstmt54ab.setInt(10, fb);
                     count = pstmt54ab.executeUpdate();         // execute the prepared stmt
                  }

                  pstmt54a.close();
                  pstmt54ab.close();
               }
               catch (Exception e17) {
                  //
                  //  save error message in /v_x/error.txt
                  //
                  String errorMsg17 = "Error17 in SystemUtils do1Lottery: ";
                  errorMsg17 = errorMsg17 + e17.getMessage();                                 // build error msg

                  logError(errorMsg17);                                       // log it
               }

            }  // end of IF all weekends

         }  // end of IF courseName=ALL

      }               // end of while for lottery table
      pstmt1.close();
   }
   catch (Exception e18) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg18 = "Error18 in SystemUtils do1Lottery: ";
      errorMsg18 = errorMsg18 + e18.getMessage();                                 // build error msg

      logError(errorMsg18);                                       // log it
   }

 }  // end of do1Lottery


 //************************************************************************
 // getLottId - get the next sequential lottery id from club table
 //
 //   called by:  Proshop_lott
 //               Member_lott
 //
 //************************************************************************

 public static long getLottId(Connection con) {


   Statement stmtm = null;
   ResultSet rs = null;

   long id = 0;

   //
   //   Get last lottery id used for this club
   //
   try {

      stmtm = con.createStatement();        // create a statement

      rs = stmtm.executeQuery("SELECT lottid " +
                             "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         id = rs.getLong(1);

      }
      stmtm.close();

      id++;                  // determine next value

      if (id <= 0) {

         id = 1;             // start over if we wrapped
      }

      //
      //  save the new value
      //
      PreparedStatement pstmt = con.prepareStatement (
         "UPDATE club5 SET lottid = ?");

      pstmt.clearParameters();            // clear the parms
      pstmt.setLong(1, id);

      pstmt.executeUpdate();  // execute the prepared stmt

      pstmt.close();
   }
   catch (Exception ignore) {

      id = 0;   // indicate failed
   }
   return(id);
 }


 //************************************************************************
 // inactTimer - Process 2 minute system timer expiration.  This timer is set
 //              at init time by Login and reset every 2 minutes so we
 //              can check for tee time slots that have been held on to for
 //              too long (more than 6 minutes).  Members' sessions timeout
 //              after 15 minutes and they could leave a tee slot hanging.
 //
 //              Also, check evntsup entries (event sign up table) and
 //              the lottery requests.
 //
 //   called by:  minTimer on timer expiration
 //
 //************************************************************************

 public static void inactTimer() {


   Connection con2 = null;
   Statement stmt2 = null;
   ResultSet rs2 = null;

   int count = 0;
   int use = 0;
   int fb = 0;
   int time = 0;
   int id = 0;
   long date = 0;

   String club = "";
   String course = "";
   String name = "";
   String errorMsg = "";
     
   boolean tooShort = false;

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (server_id == server_master) {

      //
      //  Set the date/time when this timer should expire by next (safety check to ensure timers run)
      //
      Calendar cal = new GregorianCalendar();   // get todays date

      cal.add(Calendar.MINUTE,4);              // roll ahead 4 minutes to give plenty of time

      long yy = cal.get(Calendar.YEAR);
      long mm = cal.get(Calendar.MONTH) +1;
      long dd = cal.get(Calendar.DAY_OF_MONTH);
      long hh = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
      long mn = cal.get(Calendar.MINUTE);

      //
      //  create date & time stamp value (yyyymmddhhmm) for compares
      //
      min2Time = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // save date/time stamp

      //
      //  Now verify that the 60 minute timer is running
      //
      cal = new GregorianCalendar();              // get current date & time

      yy = cal.get(Calendar.YEAR);
      mm = cal.get(Calendar.MONTH) +1;
      dd = cal.get(Calendar.DAY_OF_MONTH);
      hh = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
      mn = cal.get(Calendar.MINUTE);

      //
      //  create date & time stamp value (yyyymmddhhmm) for compares
      //
      long currTime = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // form date/time stamp

      if (currTime > min60Time) {            // if 60 min timer not yet set OR did not fire when expected

         min60Timer t2_timer = new min60Timer();     // reset the 60 min timer

         //
         //  log this so we can track how often it must be reset
         //
         errorMsg = "SystemUtils.inactTimer: Had to reset the 60 min timer (X Timer). currTime = " +currTime+ ", min60Time = " +min60Time;
         logError(errorMsg);                                       // log it
           
         //
         //  set new 'expected timeout' value so we don't do this again in 2 minutes
         //
         cal = new GregorianCalendar();              // get current date & time

         cal.add(Calendar.MINUTE,90);                 // roll ahead 90 minutes to give plenty of time

         yy = cal.get(Calendar.YEAR);
         mm = cal.get(Calendar.MONTH) +1;
         dd = cal.get(Calendar.DAY_OF_MONTH);
         hh = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
         mn = cal.get(Calendar.MINUTE);

         min60Time = (yy * 100000000) + (mm * 1000000) + (dd * 10000) + (hh * 100) + mn;   // set new date/time for 60 min timer
      }


      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         //
         //  Get today's date and the current time for lottery processing
         //
         cal = new GregorianCalendar();            // get todays date

         int year = cal.get(Calendar.YEAR);
         int month = cal.get(Calendar.MONTH);
         int day = cal.get(Calendar.DAY_OF_MONTH);
         int hr = cal.get(Calendar.HOUR_OF_DAY);            // get 24 hr clock value
         int min = cal.get(Calendar.MINUTE);
         int sec = cal.get(Calendar.SECOND);
         int day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

         String day_name = day_table[day_num];         // get name for day

         month = month + 1;                                 // month starts at zero

         date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

         //
         //  do not adjust the time!!!  pdate/ptime in actlott3 have already been adjusted to central time!!!
         //
         time = (hr * 100) + min;                          // create time field of hhmm

         //
         //  Verify that at least 2 minutes passed since the last expiration
         //
   //      tooShort = verifyTimer(hr, min, sec);

         if (tooShort == false) {        // if timer lenght was ok

            //
            //  Process each club
            //
            con2 = dbConn.Connect( rev );          // get a connection to the current rev

            if (con2 != null) {

               stmt2 = con2.createStatement();        // create a statement

               rs2 = stmt2.executeQuery("SELECT clubname FROM clubs WHERE clubname != ''");

               while (rs2.next()) {               // process all clubs

                  club = rs2.getString(1);        // get a club name

                  //
                  //  call checkTime to process each club
                  //
                  checkTime(club, date, time, day_name);

               }      // end of while for all clubs

               stmt2.close();
               con2.close();             // close the connection to the system db

            } else {

               //
               //  save error message in /v_x/error.txt
               //
               errorMsg = "Error in SystemUtils inactTimer - DB Connection failed.";
               logError(errorMsg);                                       // log it
            }
         }

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils inactTimer: ";
         errorMsg = errorMsg + e2.getMessage();                                 // build error msg
         logError(errorMsg);                                       // log it
           
         //
         //  reset the 2 minute timer
         //
         minTimer t_timer = new minTimer();
         return;
      }
   }

   //
   //  reset the 2 minute timer
   //
   minTimer t_timer = new minTimer();

 }  // end of inactTimer


 //************************************************************************
 // checkTime - Process 2 minute system timer expiration from inactTimer.
 //
 //
 //   called by:  inactTimer above
 //
 //         pdate & ptime = today's date & time
 //
 //************************************************************************

 public static void checkTime(String club, long pdate, int ptime, String day_name) {


   Connection con = null;
   Statement stmt = null;
   PreparedStatement pstmt1 = null;
   PreparedStatement pstmt2 = null;
   ResultSet rs = null;

   int count = 0;
   int use = 0;
   int not_in_use = 0;
   int fb = 0;
   int time = 0;
   int id = 0;
   long date = 0;

   String course = "";
   String name = "";
   String errorMsg = "";

   //
   //  Process the club passed
   //
   try {
      con = dbConn.Connect(club);                           // get a connection to this club's db

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in SystemUtils checkTime - cannot get con for " +club+ ": ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

//      logError(errorMsg);                                       // log it
      return;
   }

   //
   //  Make sure we have a connection (club may not be setup yet)
   //
   if (con != null) {

      //
      //  Increment the in_use counter in all tee slots where in_use <> 0
      //
      try {
         pstmt1 = con.prepareStatement (
                "UPDATE teecurr2 SET in_use = ? WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

         pstmt2 = con.prepareStatement (
                "SELECT date, time, in_use, fb, courseName FROM teecurr2 WHERE in_use != ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setInt(1, not_in_use);
         rs = pstmt2.executeQuery();

         while (rs.next()) {

            date = rs.getLong(1);
            time = rs.getInt(2);
            use = rs.getInt(3);
            fb = rs.getInt(4);
            course = rs.getString(5);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments (it starts with 1).
            //
            use++;                             // increment in_use value (# of 2 minute ticks)

            if (use > 3) {           // this actually means 'at least 4 mins' !!!!!

               use = 0;                        // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setInt(1, use);
            pstmt1.setLong(2, date);
            pstmt1.setInt(3, time);
            pstmt1.setInt(4, fb);
            pstmt1.setString(5, course);
            count = pstmt1.executeUpdate();

         }      // end of while

         pstmt1.close();
         pstmt2.close();

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime (check teecurr) for  " +club+ ": ";
         errorMsg = errorMsg + e2.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      try {
         //****************************************************************************
         //  Increment the in_use counter in all Event Entries where in_use <> 0
         //****************************************************************************
         //
         pstmt1 = con.prepareStatement (
                "UPDATE evntsup2b SET in_use = ? WHERE name = ? AND courseName = ? AND id = ?");

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT name, courseName, in_use, id FROM evntsup2b WHERE in_use != 0");

         while (rs.next()) {

            name = rs.getString(1);
            course = rs.getString(2);
            use = rs.getInt(3);
            id = rs.getInt(4);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            use++;                             // increment in_use value (# of 2 minute ticks)

            if (use > 3) {           // this actually means 'at least 4 mins' !!!!!

               use = 0;                        // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setInt(1, use);
            pstmt1.setString(2, name);
            pstmt1.setString(3, course);
            pstmt1.setInt(4, id);
            count = pstmt1.executeUpdate();

         }      // end of while

         pstmt1.close();
         stmt.close();

      }
      catch (Exception e3) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime (check events) for  " +club+ ": ";
         errorMsg = errorMsg + e3.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      try {
         //****************************************************************************
         //  Increment the in_use counter in all Lesson Book Entries where in_use <> 0
         //****************************************************************************
         //
         pstmt1 = con.prepareStatement (
                "UPDATE lessonbook5 SET in_use = ? WHERE proid = ? AND date = ? AND time = ?");

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT proid, date, time, in_use FROM lessonbook5 WHERE in_use != 0");

         while (rs.next()) {

            int proid = rs.getInt(1);
            date = rs.getLong(2);
            time = rs.getInt(3);
            use = rs.getInt(4);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            use++;                             // increment in_use value (# of 2 minute ticks)

            if (use > 3) {           // this actually means 'at least 4 mins' !!!!!

               use = 0;                        // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setInt(1, use);
            pstmt1.setInt(2, proid);
            pstmt1.setLong(3, date);
            pstmt1.setInt(4, time);
            count = pstmt1.executeUpdate();

         }      // end of while

         pstmt1.close();
         stmt.close();

      }
      catch (Exception e3) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime (check lesson books) for  " +club+ ": ";
         errorMsg = errorMsg + e3.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      try {
         //****************************************************************************
         //  Increment the in_use counter in all Lottery Requests where in_use <> 0
         //****************************************************************************
         //
         long lid = 0;

         pstmt1 = con.prepareStatement (
                "UPDATE lreqs3 SET in_use = ? WHERE id = ?");

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT in_use, id FROM lreqs3 WHERE in_use != 0");

         while (rs.next()) {

            use = rs.getInt(1);
            lid = rs.getLong(2);

            //
            //  The in_use parm indicates if the slot is currently in use and if so,
            //  the value indicates the number of 2 minute increments.
            //
            use++;                             // increment in_use value (# of 2 minute ticks)

            if (use > 5) {         // allow 10 minutes for lottery reqs

               use = 0;                        // too much time - reset in_use (timeout - return slot to system)
            }

            //
            //  execute the prepared statement to update the tee time slot
            //
            pstmt1.clearParameters();        // clear the parms
            pstmt1.setInt(1, use);
            pstmt1.setLong(2, lid);
            count = pstmt1.executeUpdate();

         }      // end of while

         pstmt1.close();
         stmt.close();

      }
      catch (Exception e4) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime (check lottery) for  " +club+ ": ";
         errorMsg = errorMsg + e4.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      try {
         //****************************************************************************
         //  Check for any Lotteries that are ready to be processed.
         //****************************************************************************
         //
         //  Is there a lottery to process?  (entry added in _lott when lottery time requested - only once)
         //
         pstmt1 = con.prepareStatement (
                "SELECT name, date FROM actlott3 WHERE pdate < ? OR (pdate = ? AND ptime <= ?)");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setLong(1, pdate);        // current date
         pstmt1.setLong(2, pdate);        // current date
         pstmt1.setInt(3, ptime);         // current time
         rs = pstmt1.executeQuery();

         if (rs.next()) {                 // only one per timer expiration (if more, wait for next timer exp)

            name = rs.getString(1);
            date = rs.getLong(2);              // actual date of lottery

            processLott(name, date, club, con);      // process this lottery
         } 

         pstmt1.close();

      }
      catch (Exception e5) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = "Error in SystemUtils checkTime (process lottery) for  " +club+ ": ";
         errorMsg = errorMsg + e5.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

      //
      // **********************************************************
      //  Custom processing for Old Warson - set noshows to '2'
      // **********************************************************
      //
      if (club.equals( "oldwarson" )) {

         if (ptime > 01 && ptime < 30) {       // do after midnight and make sure it gets done (more than once)

            setNoShowOW(pdate, con);           // go change the noshow values to 'pre-checkin'
         }
      }

      //
      // *************************************************************************************************
      //  Custom processing for North Ridge, Rogue Valley, Manito, The Lakes, Cherry Hills, and others
      // *************************************************************************************************
      //
      if (club.equals( "cherryhills" ) && pdate > 20060415 && pdate < 20060931) {

         if (day_name.equals( "Monday" ) && ptime > 826 && ptime < 830) {        // release restrictions by 7:30 AM MT

            releaseRestCH1(con);                   // go change the restrictions' start date
         }

         if (day_name.equals( "Wednesday" ) && ptime > 826 && ptime < 830) {        // release restrictions by 7:30 AM MT

            releaseRestCH2(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "stalbans" )) {

         if (day_name.equals( "Thursday" ) && ptime > 757 && ptime < 803) {        // release restrictions around 8:00 AM CT

            releaseRestStAlb(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "meadowsprings" ) && pdate < 20061031) {

         if (day_name.equals( "Friday" ) && ptime > 957 && ptime < 1003) {        // release restrictions around 8:00 AM PT

            releaseRestMeadow(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "northridge" )) {

         if (ptime > 859 && ptime < 904) {        // release restrictions at 7:00 AM PT

            releaseRestNR(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "roguevalley" )) {

//         if (ptime > 929 && ptime < 934) {      // release restrictions at 7:30 AM PT
         if (ptime > 1029 && ptime < 1034) {      // release restrictions at 8:30 AM PT (Off-Season)

            releaseRestRV(con);                   // go change the restrictions' start date
         }
      }

      if (club.equals( "manitocc" )) {

         if (ptime > 1509 && ptime < 1514) {      // release restrictions at 1:10 PM PT

            releaseRestMAN(con);                  // go change the restrictions' start date
         }
      }

      if (club.equals( "lakes" )) {

         if (ptime > 927 && ptime < 931) {        // release restrictions by 7:30 AM PT
                                                 
            releaseRestLAKES(con);                // go change the restrictions' start date
         }
      }

      if (club.equals( "skaneateles" )) {

         if (ptime > 627 && ptime < 631) {              // release restrictions by 7:30 AM ET

            String skanRest_name = "";

            if (day_name.equals( "Monday" )) {       

               skanRest_name = "White Times Up To 3 Days Thur";    // Thurs rest - adv on Monday
            }
            if (day_name.equals( "Thursday" )) {

               skanRest_name = "Red Times Up to 3 Days";           // Sunday Rest - adv on Thurs
            }
            if (day_name.equals( "Saturday" )) {

               skanRest_name = "White Times Up To 3 Days";         // Tuesday Rest - adv on Sat
            }
              
            if (!skanRest_name.equals( "" )) {

               releaseRestSKANEATELES(skanRest_name, con);         // go change the restriction's start date
            }
         }
      }

      try {
         con.close();         // close the connection to this club
      }
      catch (Exception ignore) {
      }
   }     // end of IF con

 }  // end of checkTime


 //************************************************************************
 // processLott - Process a lottery by processing all the lottery requests
 //               queued for a specific lottery and day.
 //
 //
 //   called by:  checkTime above
 //
 //       parms:  name   = name of the lottery
 //               date   = actual date of the lottery event
 //               con    = db connection
 //
 //************************************************************************

 private static void processLott(String name, long date, String club, Connection con) {


   Statement stmt = null;
   ResultSet rs = null;
   ResultSet rs3 = null;


   boolean ok = false;
   boolean fail = false;

   String color = "";
   String course = "";
   String fb = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String in_use_by = "";

   String errorMsg = "";

   int adays = 0;
   int wdpts = 0;
   int wepts = 0;
   int evpts = 0;
   int gpts = 0;
   int nopts = 0;
   int selection = 0;
   int guest = 0;
   int slots = 0;
   int players = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int hr = 0;
   int min = 0;
   int time = 0;
   int time2 = 0;
   int stime = 0;
   int etime = 0;
   int ttime = 0;
   int before = 0;
   int after = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int in_use = 0;
   int groups = 0;
   int grps = 0;
   int state = 0;
   int index = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int error = 0;
   int count = 0;
   int courseCount = 0;
   int full = 0;
   int checkothers = 0;

   short tfb = 0;
   short tfb2 = 0;
   short save_fb = 0;

   long id = 0;
   long tid = 0;

   //
   //  Put the parms in a parm block for use in subr's
   //
   parmLott parm = new parmLott();          // allocate a parm block

   parm.date = date;                     // these are static through this function
   parm.lottName = name;
   parm.club = club;                     // save club name
   parm.course = "";                     // empty for now

   parmLottC parmc = null;               // assign a parm block name for course parm blocks

   //
   //  First, make sure there are still some lottery requests for this lottery
   //   (may have been all cancelled, or already processed).
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
             "SELECT mm FROM lreqs3 WHERE name = ? AND date = ? AND state = 0");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      pstmt1.setLong(2, date);
      rs = pstmt1.executeQuery();

      if (rs.next()) {

         ok = true;     // ok to continue
      }
      pstmt1.close();
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in SystemUtils processLott (get actlott3): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   if (ok == true) {          // if requests still exist

      errorMsg = "Error in SystemUtils processLott (get lottery info): ";
      ok = false;             // default to not ok

      try {
         //
         //  Get the lottery info for the requested lottery
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "SELECT stime, etime, color, courseName, fb, type, adays, wdpts, wepts, evpts, gpts, " +
            "nopts, selection, guest, slots, pref, approve " +
            "FROM lottery3 " +
            "WHERE name = ?");

         pstmt2.clearParameters();        // clear the parms
         pstmt2.setString(1, name);
         rs = pstmt2.executeQuery();      // execute the prepared stmt

         if (rs.next()) {

            stime = rs.getInt(1);
            etime = rs.getInt(2);
            color = rs.getString(3);
            parm.course = rs.getString(4);       // course from Lottery
            fb = rs.getString(5);
            type = rs.getString(6);
            adays = rs.getInt(7);
            wdpts = rs.getInt(8);
            wepts = rs.getInt(9);
            evpts = rs.getInt(10);
            gpts = rs.getInt(11);
            nopts = rs.getInt(12);
            selection = rs.getInt(13);
            guest = rs.getInt(14);
            slots = rs.getInt(15);
            pref = rs.getString(16);
            approve = rs.getString(17);

            parm.approve = approve;        // save

            ok = true;                     // ok to proceed
         }
         pstmt2.close();

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = errorMsg + "Exception1= " +e2.getMessage();                  // build error msg

         logError(errorMsg);                                       // log it
         ok = false;           
      }
   }

   //
   //  Determine number of courses for lottery
   //
   try {

      courseCount = 1;                              // init to 1 course

      if (parm.course.equals( "-ALL-" )) {          // if lottery for ALL courses

         courseCount = 0;                           // start at zero

         //
         //  Get the number of courses for this club
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && courseCount < 21) {

            courseCount++;
         }
         stmt.close();
      }

   }
   catch (Exception e1) {

      errorMsg = "Error in SystemUtils processLott (count courses): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   //
   //  Arrays to hold course information
   //
   String [] courseA = new String [courseCount];           // course names
   parmLottC [] parmcA = new parmLottC [courseCount];      // course parm blocks

   try {

      //
      //  Build arrays for courses and their parm blocks
      //
      if (parm.course.equals( "-ALL-" )) {          // if lottery for ALL courses

         //
         //  Get the names of all courses for this club
         //
         i = 0;

         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT courseName " +
                                 "FROM clubparm2 WHERE first_hr != 0");

         while (rs.next() && i < 20) {

            course = rs.getString(1);

            courseA[i] = course;               // add course name to array
            parmc = new parmLottC();           // get a new course parm block
            parmcA[i] = parmc;                 // save it
            i++;
         }
         stmt.close();

      } else {

         courseA[0] = parm.course;             // set course name
         parmc = new parmLottC();              // get a new course parm block
         parmcA[0] = parmc;                    // save it
      }

   }
   catch (Exception e1) {

      errorMsg = "Error in SystemUtils processLott (get course names): ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
      ok = false;                                               // not ok to continue
   }

   //
   //  If still ok to proceed, then process according to the lottery type (Proshop, Random or Weighted)
   //
   if (ok == true) {       // if lottery still exist (if still ok to proceed)

      parm.sfb = fb;            // save parms
      parm.stime = stime;
      parm.etime = etime;
      parm.ltype = type;        // lottery type

      if (type.equals( "Proshop" )) {       // if proshop to process the requests manually

         errorMsg = "Error in SystemUtils processLott (type=Proshop): ";

         //**************************************************************************************
         //  Type = Proshop (Proshop will manually assign the times via _mlottery)
         //
         //  Set the type and change the state of each request to tell Proshop_mlottery
         //  they are ready for processing. State 1 = 'Processed, Not Assigned'
         //                                 State 2 = 'Processed and Assigned, Not Approved'
         //**************************************************************************************
         //
         try {

            PreparedStatement pstmt4 = con.prepareStatement (
                "UPDATE lreqs3 SET type = ?, state = 2 WHERE name = ? AND date = ?");

            pstmt4.clearParameters();        // clear the parms
            pstmt4.setString(1, type);
            pstmt4.setString(2, name);
            pstmt4.setLong(3, date);
            pstmt4.executeUpdate();

            pstmt4.close();

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + "Exception= " +e2.getMessage();                  // build error msg

            logError(errorMsg);                                       // log it
         }

      } else {        // Weighted or Random

         //**************************************************************************************
         //  Type = Weighted or Random
         //
         //  Setup the tee time arrays for processing below
         //**************************************************************************************
         //
         try {

            //
            //  Now setup the arrays for each course
            //
            for (i=0; i < courseCount; i++) {        // do all courses

               errorMsg = "Error in SystemUtils processLott - buildArrays (type=Weighted or Random): ";

               parmc = parmcA[i];                    // get parm block for course

               parmc.course = courseA[i];            // get course name

               buildArrays(con, parm, parmc);        // build arrays

               errorMsg = "Error in SystemUtils processLott - order requests (type=Weighted or Random): ";

               //
               //  Now put the requests in the arrays in the order to process
               //
               if (type.equals( "Random" )) {                   // Random Lottery ?

                  orderReqsRan(con, parm, parmc);               // put requests in order

               } else {

                  if (type.equals( "WeightedBR" )) {             // Weighted By Rounds

                     orderReqsWBR(con, parm, parmc);             // put requests in order

                  } else {                                       // Weighted By Proximity

                     orderReqsWBP(con, parm, parmc);             // put requests in order
                  }
               }

            }          // end of DO ALL courses loop

            i = 0;     // reset

            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, set state): ";

            //
            //  set the state to 'Processed, Not Assigned (1)' and init the assigned time for all requests
            //
            PreparedStatement pstmt7 = con.prepareStatement (
                "UPDATE lreqs3 SET state = 1, atime1 = 0, atime2 = 0, atime3 = 0, atime4 = 0, atime5 = 0 " +
                "WHERE name = ? AND date = ?");

            pstmt7.clearParameters();        // clear the parms
            pstmt7.setString(1, name);
            pstmt7.setLong(2, date);

            pstmt7.executeUpdate();

            pstmt7.close();

            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, assign times): ";

            //**************************************************************************************
            //  Type = Random or Weighted
            //
            //  Assign Times - process the requests for each course in the order listed
            //
            //**************************************************************************************
            //
            for (index=0; index < courseCount; index++) {     // do all courses

               parmc = parmcA[index];                         // get parm block for course

               assignTime(con, parm, parmc);                  // assign a tee time for each request
            }


            //*********************************************************************************
            //
            //  The requests for all courses (if more than one) have been processed and possibly assigned.
            //
            //  Now check for any requests that didn't get assigned and try other courses if possible.
            //
            //*********************************************************************************
            //
            if (courseCount > 1) {                      // if more than one course specified for this lottery

               assignTime2(con, parm, parmcA, courseCount);  // try to assign a tee time for each remaining request
            }

/*
            //*********************************************************************************
            //  Now check for any requests that still didn't get assigned (state = 1).
            //
            //  If any, look for partial groups and fill in where possible.
            //*********************************************************************************
            //
            errorMsg = "Error in SystemUtils processLott (type=Random/Weighted, check unassigned): ";

            PreparedStatement pstmt8 = con.prepareStatement (
               "SELECT time, minsbefore, minsafter, " +
               "fb, id, groups, p5, players " +
               "FROM lreqs3 " +
               "WHERE name = ? AND date = ? AND courseName = ? AND state = 1 AND groups = 1");

            pstmt8.clearParameters();        // clear the parms
            pstmt8.setString(1, name);
            pstmt8.setLong(2, date);
            pstmt8.setString(3, course);
            rs = pstmt8.executeQuery();

            while (rs.next()) {          // get all of them

               time = rs.getInt(1);
               before = rs.getInt(2);
               after = rs.getInt(3);
               parm.rfb = rs.getShort(4);
               id = rs.getLong(5);
               groups = rs.getInt(6);
               p5 = rs.getString(7);
               players = rs.getInt(8);

               //
               //  check for partial groups
               //
               count = 4;
               if (p5.equals( "Yes" )) {

                  count = 5;
               }
               if (players < count) {        // if partial group

                  //
                  //  scan the tee time array for a matching time
                  //
                  i = 0;
                  i2 = 0;
                  i3 = 0;
                  time2 = time;              // preserve original time requested

                  loop8:
                  while (i < 100) {

                     ttime = parm.timeA[i];                              // get tee time from array

                     if (ttime == 0) {       // if none

                        break loop8;         // then we're done
                     }

                     if (ttime == time2) {                          // if matching time found

                        if ((players + parm.playersA[i]) <= count) {     // if room in this tee time

                           //
                           //  move players to the request already in the tee time slot
                           //
                           tid = parmc.idA[i];            // get the request id assigned to this time slot

                           movePlayers(con, id, tid);     // go move the players

                           break loop8;      // done - go check next request

                        } else {     // check before and after times

                           i2 = i;          // copy index for before
                           i3 = i;          // copy index for after
                           toggle = 0;      // init before/after switch
                           astat = 0;       // init after times status = ok
                           bstat = 0;       // init before times status = ok
                           //
                           //  determine earliest and latest times to accept
                           //
                           parm.ftime = time;   // init
                           parm.ltime = time;

                           if (before > 0) {

                              parm.ftime = getFirstTime(time, before);    // get earliest time for this request
                           }

                           if (after > 0) {

                              parm.ltime = getLastTime(time, after);     // get latest time for this request
                           }

                           loop9:
                           while (astat == 0 || bstat == 0) {

                              if (toggle == 0) {    // check before times

                                 toggle = 1;        // switch

                                 if (bstat == 0 && i2 > 0) {

                                    i2--;                    // go back one slot
                                    ttime = parm.timeA[i2];       // get tee time from array

                                    if (ttime >= parm.ftime) {    // if tee time is acceptable

                                       if ((players + parm.playersA[i2]) <= count) { // if room in this tee time

                                          //
                                          //  move players to the request already in the tee time slot
                                          //
                                          tid = parmc.idA[i2];        // get the request id assigned to this time slot

                                          movePlayers(con, id, tid);     // go move the players

                                          break loop9;      // done
                                       }

                                    } else {

                                       bstat = 1;            // done going back
                                    }
                                 }
                                 if (i2 == 0) {

                                    bstat = 1;            // done going back
                                 }

                              } else {              // check after times

                                 toggle = 0;        // switch back to before

                                 if (astat == 0 && i3 < 100) {

                                    i3++;              // go ahead one slot
                                    ttime = parm.timeA[i3];       // get tee time from array

                                    if (ttime <= parm.ltime) {    // if tee time is acceptable

                                       if ((players + parm.playersA[i3]) <= count) { // if room in this tee time

                                          //
                                          //  move players to the request already in the tee time slot
                                          //
                                          tid = parmc.idA[i3];        // get the request id assigned to this time slot

                                          movePlayers(con, id, tid);     // go move the players

                                          break loop9;      // done
                                       }

                                    } else {

                                       astat = 1;            // done going ahead
                                    }
                                 }
                                 if (i3 >= 100) {

                                    astat = 1;            // done going ahead
                                 }
                              }
                           }         // end of WHILE i2 and i3 (loop9)

                           break loop8;      // done - go check next request

                        }
                     }      // end of IF matching tee time found
                  }         // end of WHILE i < 100 (loop8)
               }            // end of IF partial group
            }               // end of WHILE more requests to process
            pstmt8.close();
*/

            errorMsg = "Error in SystemUtils processLott (type=Random or Weighted, move requests): ";

            //
            //*********************************************************************************
            //  now check if proshop wants to pre-approve the assignments before putting them in teecurr
            //*********************************************************************************
            //
            if (approve.equals( "No" )) {
              
               //
               //  Move the requests that have been assigned into teecurr
               //
               for (i=0; i < courseCount; i++) {        // do all courses

                  course = courseA[i];                  // get course name

                  moveReqs(name, date, course, con);    // move the requests for this course
               }
            }

         }
         catch (Exception e2) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + "Exception2= " +e2.getMessage();                  // build error msg

            logError(errorMsg);                                       // log it
         }

      }         // end of IF type = proshop, random or weighted

   }            // end of IF ok
   //
   //  remove the lottery from the active queue - done processing
   //
   try {

      PreparedStatement pstmt10 = con.prepareStatement (
               "Delete FROM actlott3 WHERE name = ? AND date = ?");

      pstmt10.clearParameters();               // clear the parms
      pstmt10.setString(1, name);
      pstmt10.setLong(2, date);
      pstmt10.executeUpdate();

      pstmt10.close();
   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in SystemUtils processLott (delete actlott3): ";
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of processLott


 //************************************************************************
 //  buildArrays - build tee time arrays for use in processLott
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void buildArrays(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;

   int i = 0;
   int tfb = 0;

   String errorMsg = "Error in SystemUtils buildArrays (type=Random or Weighted, get info): ";

   try {
      if (parm.sfb.equals( "Both" )) {

         PreparedStatement pstmt5 = con.prepareStatement (
            "SELECT time, fb " +
            "FROM teecurr2 WHERE date = ? AND courseName = ? AND time >= ? AND time <= ? AND fb < 2 AND " +
            "event = '' AND blocker = '' " +
            "ORDER BY time, fb");

         pstmt5.clearParameters();        // clear the parms
         pstmt5.setLong(1, parm.date);
         pstmt5.setString(2, parmc.course);
         pstmt5.setInt(3, parm.stime);
         pstmt5.setInt(4, parm.etime);

         rs = pstmt5.executeQuery();      // execute the prepared stmt

         i = 0;

         while (rs.next() && i < 100) {

            parmc.timeA[i] = rs.getInt(1);        // put tee time in array
            parmc.fbA[i] = rs.getShort(2);        // put f/b in array
            i++;
         }
         pstmt5.close();

      } else {

         tfb = 0;         // default = Front

         if (parm.sfb.equals( "Back" )) {

            tfb = 1;         // back tee
         }

         PreparedStatement pstmt6 = con.prepareStatement (
            "SELECT time, fb " +
            "FROM teecurr2 WHERE date = ? AND courseName = ? AND fb = ? AND time >= ? AND time <= ? AND " +
            "event = '' AND blocker = '' " +
            "ORDER BY time");

         pstmt6.clearParameters();        // clear the parms
         pstmt6.setLong(1, parm.date);
         pstmt6.setString(2, parmc.course);
         pstmt6.setInt(3, tfb);
         pstmt6.setInt(4, parm.stime);
         pstmt6.setInt(5, parm.etime);

         rs = pstmt6.executeQuery();      // execute the prepared stmt

         i = 0;

         while (rs.next() && i < 100) {

            parmc.timeA[i] = rs.getInt(1);        // put tee time in array
            parmc.fbA[i] = rs.getShort(2);        // put f/b in array
            i++;
         }
         pstmt6.close();

      }      // end of IF fb (common weighted and random processing)

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of buildArrays


 //************************************************************************
 //  orderReqsRan - put lottery reqs in arrays in order for use in processLott
 //
 //    For Random lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsRan(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;

   int i = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsRan (type=Random, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ? " +
         "ORDER BY RAND()");                                  // Random order 

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {       // get all lottery requests up to 100

         id = rs.getLong(1);

         parmc.idA[i] = id;                // set id in array

         i++;
      }
        
      pstmt3.close();

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsRan


 //************************************************************************
 //  orderReqsWBR - put lottery reqs in arrays in order for use in processLott
 //
 //    For 'Weighted By Rounds' lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsWBR(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   long [] idA = new long [100];          // request id array
   int [] wghtA = new int [100];          // weight of this request
   int [] playersA = new int [100];       // # of players in this request

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsWBR (type=Weighted By Rounds, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ?");
//         "ORDER BY players DESC");                    // process larger groups first

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {        // get all lottery requests up to 100

         id = rs.getLong(1);
         playersA[i] = rs.getInt(2);        // set # of players in array

         idA[i] = id;                       // set id in array

         //
         //  get the weight value for this entry
         //
         weight = getWeight(con, id, parm.lottName);

         wghtA[i] = weight;                // set weight in array

         i++;
      }

      pstmt3.close();

      i = 0;

      //
      //  Now move the values from the temp arrays above into the parm block (order by weight)
      //
      while (i < 100) {
        
         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player
           
            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot
              
            while (i2 < 100) {                   // compare against the rest
              
               if (idA[i2] > 0 && playersA[i2] > 1) {    // if id still there & more than one player

                  if (weight > wghtA[i2]) {      // if this weight is lower - switch to this one

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight
              
            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index
              
         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

      //
      //  Now move any single payers from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning
        
      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists 

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

               if (idA[i2] > 0) {                // if id still there 

                  if (weight > wghtA[i2]) {      // if this weight is lower - switch to this one

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsWBR


 //************************************************************************
 //  orderReqsWBP - put lottery reqs in arrays in order for use in processLott
 //
 //    For 'Weighted By Proximity' lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void orderReqsWBP(Connection con, parmLott parm, parmLottC parmc) {

   ResultSet rs = null;


   long [] idA = new long [100];          // request id array
   int [] wghtA = new int [100];          // weight of this request
   int [] playersA = new int [100];       // # of players in this request

   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;

   long id = 0;


   String errorMsg = "Error in SystemUtils orderReqsWBP (type=Weighted By Proximity, order Reqs): ";

   try {

      String course = parmc.course;           // get course name

      //
      //  Get the lottery requests for the lottery name and date that were passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT id, players " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ?");
//         "ORDER BY players DESC");                    // process larger groups first

      pstmt3.clearParameters();        // clear the parms
      pstmt3.setString(1, parm.lottName);
      pstmt3.setLong(2, parm.date);
      pstmt3.setString(3, course);
      rs = pstmt3.executeQuery();

      while (rs.next() && i < 100) {              // get all lottery requests up to 100

         id = rs.getLong(1);
         playersA[i] = rs.getInt(2);        // set # of players in array

         idA[i] = id;                       // set id in array

         //
         //  get the weight value for this entry
         //
         weight = getWeightP(con, id, parm.lottName);

         wghtA[i] = weight;                // set weight in array

         i++;
      }

      pstmt3.close();

      i = 0;

      //
      //  Now move the values from the temp arrays above into the parm block (order by weight)
      //
      //    The weight values for this type are 'the average # of minutes previous requests were assigned within requested time'
      //
      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0 && playersA[i] > 1) {     // if id exists and more than one player

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

               if (idA[i2] > 0 && playersA[i2] > 1) {    // if id still there & more than one player

                  if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one (larger value gets priority)

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

      //
      //  Now move any single payers from the temp arrays above into the parm block (order by weight)
      //
      i = 0;                      // start at beginning

      while (i < 100) {

         id = idA[i];             // get an id
         weight = wghtA[i];       // get its weight

         if (id > 0) {     // if id exists

            i3 = i;               // save for move

            i2 = i + 1;           // start at next spot

            while (i2 < 100) {                   // compare against the rest

               if (idA[i2] > 0) {                // if id still there

                  if (weight < wghtA[i2]) {      // if this weight is higher - switch to this one (larger value gets priority)

                     id = idA[i2];               // get the id
                     weight = wghtA[i2];         // get its weight

                     i3 = i2;
                  }
               }
               i2++;
            }

            //
            //  i3 points to the lowest weight, i4 is the save index
            //
            parmc.idA[i4] = id;            // set the id
            parmc.wghtA[i4] = weight;      // set its weight

            idA[i3] = 0;                   // remove the id from temp array
            i4++;                          // bump save index

         } else {                          // id already set in real array

            i++;                           // do next id (do not bump i until its id is zero)
         }
      }

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
 }  // end of orderReqsWBP


 //************************************************************************
 //  assignTime - assign a Tee Time to the lottery requests for the course passed
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //************************************************************************

 public static void assignTime(Connection con, parmLott parm, parmLottC parmc) {


   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int weight = 0;
   int ttime = 0;
   short tfb = 0;
   long id = 0;

   boolean fail = false;

   String errorMsg = "Error in SystemUtils assignTime: ";


   for (i=0; i < 100; i++) {         // do all requests for this course

      id = parmc.idA[i];               // get an id
      weight = parmc.wghtA[i];         // get its weight

      if (id > 0) {                     // if exists

         fail = assign1Time(con, parm, parmc, id, weight);          // try to assign a time

         //
         //  If req failed, then we must update the weight (Weighted type only)
         //
         if (fail == true && parm.ltype.startsWith( "Weighted" )) {

            try {

               PreparedStatement pstmtf = con.prepareStatement (
                   "UPDATE lreqs3 SET weight = ? WHERE id = ?");

               pstmtf.clearParameters();        // clear the parms
               pstmtf.setInt(1, weight);
               pstmtf.setLong(2, id);
               pstmtf.executeUpdate();

               pstmtf.close();

            }
            catch (Exception e3) {
               //
               //  save error message in /v_x/error.txt
               //
               errorMsg = errorMsg + e3.getMessage();                                 // build error msg
               logError(errorMsg);                                       // log it
            }
         }

      }
   }


   //*****************************************************************************
   //  Lottery for this course has been processed - set the assigned times in the requests
   //*****************************************************************************
   //
   i = 0;

   loop5:
   while (i < 100) {

      ttime = parmc.timeA[i];        // get tee time from array

      if (ttime == 0) {              // if reached end of tee times
        
         break loop5;                // exit loop
      }

      tfb = parmc.fbA[i];            // get the f/b for this time slot
      id = parmc.id2A[i];            // get the request id assigned to this time slot
      weight = parmc.wght2A[i];      // get the weight for this time slot

      if (id != 0) {                 // if assigned

         parmc.id2A[i] = 999999;     // mark it done
         i2 = i + 1;                 // get next slot
         i3 = i2 + 11;               // furthest we should have to search for matching id
         i4 = 1;
         parmc.atimeA[0] = ttime;    // init time values - set 1st one
         parmc.atimeA[1] = 0;
         parmc.atimeA[2] = 0;
         parmc.atimeA[3] = 0;
         parmc.atimeA[4] = 0;

         while (i2 < i3 && i2 < 100 && i4 < 5) {    // look for matching id's (more than 1 tee time)

            if (parmc.id2A[i2] == id) {               // if match

               parmc.atimeA[i4] = parmc.timeA[i2];    // save time value
               parmc.id2A[i2] = 999999;               // mark it done
               i4++;
            }
            i2++;                                   // next
         }
         //
         //  set the assigned time and f/b in the req for later processing
         //
         //  State = 2 (Processed & Assigned)
         //
         errorMsg = "Error in SystemUtils assignTime (type=Random/Weighted, set assigned): ";

         try {

            PreparedStatement pstmt9 = con.prepareStatement (
                "UPDATE lreqs3 SET type = ?, state = 2, atime1 = ?, atime2 = ?, atime3 = ?, " +
                "atime4 = ?, atime5 = ?, afb = ?, weight = ? WHERE id = ?");

            pstmt9.clearParameters();        // clear the parms
            pstmt9.setString(1, parm.ltype);       // weighted or random
            pstmt9.setInt(2, parmc.atimeA[0]);
            pstmt9.setInt(3, parmc.atimeA[1]);
            pstmt9.setInt(4, parmc.atimeA[2]);
            pstmt9.setInt(5, parmc.atimeA[3]);
            pstmt9.setInt(6, parmc.atimeA[4]);
            pstmt9.setShort(7, tfb);
            pstmt9.setInt(8, weight);
            pstmt9.setLong(9, id);

            pstmt9.executeUpdate();

            pstmt9.close();

         }
         catch (Exception e3) {
            //
            //  save error message in /v_x/error.txt
            //
            errorMsg = errorMsg + e3.getMessage();                                 // build error msg
            logError(errorMsg);                                       // log it
         }
      }
      i++;          // next time slot

   }                // end of loop5 WHILE

 }  // end of assignTime


 //************************************************************************
 //  assign1Time - assign a Tee Time to the lottery request passed
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  assignTime
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmc  = lottery parm block for this course
 //               id     = lottery request id
 //               weight = lottery request's weight (if any)
 //************************************************************************

 public static boolean assign1Time(Connection con, parmLott parm, parmLottC parmc, long id, int weight) {

   ResultSet rs = null;


   int time = 0;
   int time2 = 0;
   int ttime = 0;
   int before = 0;
   int after = 0;
   int groups = 0;
   int players = 0;
   int full = 0;
   int stat = 0;
   int astat = 0;
   int bstat = 0;
   int toggle = 0;
   int index = 0;
   int grps = 0;
   int error = 0;
   int count = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;

   short tfb = 0;
   short save_fb = 0;

   boolean fail = false;
   boolean restricted = false;

   String p5 = "";

   String errorMsg = "Error in SystemUtils assign1Time: ";


   try {

      //
      //  get the request info for id passed
      //
      PreparedStatement pstmt3 = con.prepareStatement (
         "SELECT time, minsbefore, minsafter, " +
         "fb, groups, p5, players " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt3.clearParameters();
      pstmt3.setLong(1, id);
      rs = pstmt3.executeQuery();

      if (rs.next()) {

         time = rs.getInt(1);
         before = rs.getInt(2);
         after = rs.getInt(3);
         parm.rfb = rs.getShort(4);
         groups = rs.getInt(5);
         p5 = rs.getString(6);
         players = rs.getInt(7);

         //
         //  determine earliest and latest times to accept
         //
         parm.ftime = time;   // init
         parm.ltime = time;

         if (before > 0) {

            parm.ftime = getFirstTime(time, before);    // get earliest time for this request
         }

         if (after > 0) {

            parm.ltime = getLastTime(time, after);     // get latest time for this request
         }


         //
         // determine how many players constitute a full group
         //
         if (p5.equals( "Yes" )) {       // determine # of players for this group
            full = 5;
         } else {
            full = 4;
         }

         //
         //  scan the tee time array for a matching time
         //
         i = 0;
         time2 = time;              // preserve original time requested
         stat = 0;                  // init status = ok
         astat = 0;                 // init after times status = ok
         bstat = 0;                 // init before times status = ok
         toggle = 0;                // init before/after toggle
         save_fb = parm.rfb;        // save f/b from request
         fail = false;

         //
         //  Process this request
         //
         loop1:
         while (i < 100) {               // loop through arrays for a matching time

            ttime = parmc.timeA[i];       // get tee time from array
            tfb = parmc.fbA[i];           // get associated f/b

            if (ttime == time2 && tfb == parm.rfb) {   // if matching time/fb found

               grps = groups;         // # of groups requested - temp for work below
               i3 = i;                // temp index for work below
               parm.beforei = i;      // init before and after indexes
               parm.afteri = i;
               i2 = 999;              // init index save
               restricted = false;    // init restricted flag 

               //
               //  Time found that matches the requested time - check if available times for all groups
               //
               while (grps > 0) {

                  if (astat == 0 || bstat == 0) {   // if before or after still open to check

                     if (parmc.id2A[i3] == 0 && parmc.timeA[i3] > 0 && restricted == false) {  // if spot open (check associated entry in Id Array)

                        error = 0;                     // init

                        if (i2 == 999) {

                           i2 = i3;           // save first open spot
                        }

                        i3++;                 // next spot
                        grps--;               // next grp

                        if (grps > 0) {          // if still more groups

                           tfb = parmc.fbA[i3];          // get associated f/b of next slot

                           if (tfb != parm.rfb) {        // if not matching f/b

                              i3++;                      // next spot
                              tfb = parmc.fbA[i3];       // get associated f/b of next slot

                              if (tfb != parm.rfb) {     // if not matching f/b

                                 error = 1;              // error!! only need to check 2 slots
                                 i2 = 999;
                              }
                           }
                        }
                          
                     } else {

                        error = 1;                        // indicate error if not available
                        i2 = 999;                         // reset first available index                      
                     }

                     if (error > 0) {                     // if error - try another time slot

                        if (astat == 0 || bstat == 0) {   // if before or after still open to check

                           if (toggle == 0) {             // check before time ?

                              toggle = 1;                 // check after time next

                              if (bstat == 0) {           // if before still open to check

                                 parm.groups = groups;        // save current parms

                                 bstat = checkBefore(parm, parmc);   // go check for a before time

                                 if (bstat == 1 && astat == 0) {     // if 'before' failed and 'after' ok to check

                                    astat = checkAfter(parm, parmc);    // go check for an after time
                                 }

                                 i3 = parm.i3;
                                 grps = parm.grps;
                              }                    // end of IF bstat = 0

                           } else {                // check 'after' times

                              toggle = 0;                 // check before time next

                              if (astat == 0) {           // if after still open to check

                                 parm.groups = groups;        // save current parms

                                 astat = checkAfter(parm, parmc);    // go check for an after time

                                 if (astat == 1) {                   // if 'after' failed 

                                    if (bstat == 0) {     // if 'before' ok to check

                                       bstat = checkBefore(parm, parmc);    // go check for a before time
                                         
                                    } else {

                                       stat = 1;                        // done checking - failed for this request
                                       parm.grps = 0;                   // done with this one
                                    }
                                 }

                                 i3 = parm.i3;
                                 grps = parm.grps;
                              }                    // end of IF astat = 0

                           }            // end of IF toggle (before or after times check)

                           //
                           //  Before and/or After times checked.  If we found some, then make sure the players
                           //  are allowed to play during this time(s).
                           //
                           if (parm.club.equals( "cherryhills" )) {            // just do them for now

                              if (stat == 0 && (astat == 0 || bstat == 0)) {   // if we haven't failed yet

                                 parm.lottid = id;                        // set id

                                 Common_Lott.getParmValues(parm, con);    // go set parm values needed for restriction processing

                                 parm.time = parmc.timeA[i3];      // override time and fb
                                 parm.fb = parm.rfb;
                                 parm.ind = 1;                     // index not pertinent for this - use 1 to get through restrictions

                                 restricted = Common_Lott.checkRests(0, parm, con);  // check restrictions (returns true if a member is restricted)
                                                                                     // if true, forces a new search (above)
                              }
                           }

                        } else {        // before and after times already checked
               
                           stat = 1;    // done checking - failed for this request
                           grps = 0;    // terminate this loop
                        }               // end of IF stat or bstat
                     }                  // end of IF spot open (if ok for this group)

                  } else {              // before and after times already checked

                     stat = 1;          // done checking - failed for this request
                     grps = 0;          // terminate this loop
                  }                     // end of IF before or after still ok

               }                     // end of WHILE grps

               if (stat == 0) {      // if spot(s) open

                  //**************************************************************************
                  //  Found spot(s) for the lottery request - save the req id in the array
                  //**************************************************************************
                  //
                  grps = groups;                       // restore # of groups requested

                  while (grps > 0 && i2 < 100) {       // make sure we don't go past end of array

                     tfb = parmc.fbA[i2];              // get associated f/b

                     if (tfb == parm.rfb) {            // if matching f/b found

                        if (players > full) {           // determine # of players for this group
                           count = full;                // 4 or 5
                           players = players - full;    // new count
                        } else {
                           count = players;
                           players = 0;
                        }

                        parmc.id2A[i2] = id;            // put this id in array to reserve this spot
                        parmc.players2A[i2] = count;    // set # of players in this tee time
                        parmc.wght2A[i2] = weight;      // set weight for this tee time
                        weight = 9999;                  // weight for susequent grps (only 1st has weight)
                        grps--;
                     }
                     i2++;
                  }

                  break loop1;                // done with this request - exit while loop

               } else {                       // request failed - try other tees?

                  if (parm.sfb.equals( "Both" )) {   // if both tees used for this lottery

                     if (parm.rfb == save_fb) {     // if we just tried the first f/b

                        if (parm.rfb == 0) {        // if front

                           parm.rfb = 1;            // try back tees
                        } else {
                           parm.rfb = 0;            // try front;
                        }
                        stat = 0;              // init status = ok
                        astat = 0;             // init after times status = ok
                        bstat = 0;             // init before times status = ok
                        toggle = 0;            // init before/after toggle
                        i = 999;               // start over

                     } else {

                        fail = true;           // this req failed
                        break loop1;           // done with this request - exit while loop
                     }
                  } else {

                     fail = true;              // this req failed
                     break loop1;              // done with this request - exit while loop
                  }
               }
            }             // end of IF time/fb matches

            if (i == 999) {        // start over?
               i = 0;
            } else {
               i++;                   // check next entry in Time Array
            }
         }           // end of loop1 WHILE - still entries in array

      }     // end of IF lottery request found

      pstmt3.close();

   }
   catch (Exception e3) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e3.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   return(fail);

 }  // end of assign1Time


 //************************************************************************
 //  assignTime2 - try to assign a Tee Time to the remaining lottery requests for the course passed
 //                (try other courses)
 //
 //    For Random and Weighted lottery types
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = the lottery parameter block
 //               con    = db connection
 //               parmcA = lottery parm block array - one per course
 //               count  = # of courses
 //************************************************************************

 public static void assignTime2(Connection con, parmLott parm, parmLottC [] parmcA, int count) {


   ResultSet rs = null;
   ResultSet rs2 = null;

   int index = 0;
   int i = 0;
   int i2 = 0;
   int i3 = 0;
   int i4 = 0;
   int tid = 0;
   int weight = 0;
   int ttime = 0;
   short tfb = 0;

   long id = 0;

   boolean fail = false;

   parmLottC parmc = null;                  // parm block for course to check
   parmLottC parmc2 = null;                 // parm block for other courses

   String errorMsg = "Error in SystemUtils assignTime2: ";


   for (i=0; i < count; i++) {              // do all courses

      parmc = parmcA[i];                    // get parm block for course to check

      //
      //  Get each unassigned request and try to assign a time from another course (if requested)
      //
      try {

         PreparedStatement pstmt8 = con.prepareStatement (
            "SELECT id, weight " +
            "FROM lreqs3 " +
            "WHERE name = ? AND date = ? AND courseName = ? AND state = 1 AND checkothers = 1");

         pstmt8.clearParameters();
         pstmt8.setString(1, parm.lottName);
         pstmt8.setLong(2, parm.date);
         pstmt8.setString(3, parmc.course);          // just for this course
         rs = pstmt8.executeQuery();

         while (rs.next()) {                         // get all of them

            id = rs.getLong(1);
            weight = rs.getInt(2);

            if (i == 0) {                  // if this is first course (i = course index, passed by caller)

               i3 = 1;                     // start with next course

            } else {

               i3 = 0;                     // start with first course
            }

            loopCourse1:
            while (i3 < count) {           // check each course if necessary

               parmc2 = parmcA[i3];        // get course to try

               fail = assign1Time(con, parm, parmc2, id, weight);   // try to assign a time to the new course

               if (fail == false) {              // if assigned

                  //
                  //  Assigned - save the assigned time(s) - update the request record
                  //
                  loopFindId1:
                  for (index=0; index<100; index++) {        // find the assigned times

                     if (id == parmc2.id2A[index]) {         // if assigned to this id

                        ttime = parmc2.timeA[index];        // get tee time from array
                        tfb = parmc2.fbA[index];            // get the f/b for this time slot
                        weight = parmc2.wght2A[index];      // get the weight for this time slot

                        parmc2.id2A[index] = 999999;        // mark it done
                        i2 = index + 1;                    // get next slot
                        i3 = i2 + 11;                      // furthest we should have to search for matching id
                        i4 = 1;
                        parmc2.atimeA[0] = ttime;           // init time values - set 1st one
                        parmc2.atimeA[1] = 0;
                        parmc2.atimeA[2] = 0;
                        parmc2.atimeA[3] = 0;
                        parmc2.atimeA[4] = 0;

                        while (i2 < i3 && i2 < 100 && i4 < 5) {    // look for matching id's (more than 1 tee time)

                           if (parmc2.id2A[i2] == id) {               // if match

                              parmc2.atimeA[i4] = parmc2.timeA[i2];    // save time value
                              parmc2.id2A[i2] = 999999;               // mark it done
                              i4++;
                           }
                           i2++;                                   // next
                        }
                        //
                        //  set the assigned time and f/b in the req for later processing
                        //
                        //  State = 2 (Processed & Assigned)
                        //
                        errorMsg = "Error in SystemUtils assignTime2 (type=Random/Weighted, set assigned): ";

                        try {

                           PreparedStatement pstmt9 = con.prepareStatement (
                               "UPDATE lreqs3 SET courseName = ?, type = ?, state = 2, atime1 = ?, atime2 = ?, " +
                               "atime3 = ?, atime4 = ?, atime5 = ?, afb = ?, weight = ? WHERE id = ?");

                           pstmt9.clearParameters();
                           pstmt9.setString(1, parmc2.course);       // new course name
                           pstmt9.setString(2, parm.ltype);          // weighted or random
                           pstmt9.setInt(3, parmc2.atimeA[0]);
                           pstmt9.setInt(4, parmc2.atimeA[1]);
                           pstmt9.setInt(5, parmc2.atimeA[2]);
                           pstmt9.setInt(6, parmc2.atimeA[3]);
                           pstmt9.setInt(7, parmc2.atimeA[4]);
                           pstmt9.setShort(8, tfb);
                           pstmt9.setInt(9, weight);
                           pstmt9.setLong(10, id);

                           pstmt9.executeUpdate();

                           pstmt9.close();

                        }
                        catch (Exception e3) {
                           //
                           //  save error message in /v_x/error.txt
                           //
                           errorMsg = errorMsg + e3.getMessage();                                 // build error msg
                           logError(errorMsg);                                       // log it
                        }

                        break loopFindId1;       // done - exit loop
                     }
                  }                              // end of FOR loop

                  break loopCourse1;             // assinged - quit trying
               }

               i3++;                          // next course to try

               if (i3 == i) {                 // if same course

                  i3++;                       // check next one
               }
            }         // end of loopCourse1
         }            // end of WHILE

         pstmt8.close();

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         errorMsg = errorMsg + "Exception2= " +e2.getMessage();                  // build error msg

         logError(errorMsg);                                       // log it
      }

   }

 }  // end of assignTime2


 //************************************************************************
 // checkBefore - Look for an available time before the requested time
 // checkAfter - Look for an available time after the requested time
 //
 //   called by:  processLott above
 //
 //       parms:  parm   = lottery parm block
 //
 //************************************************************************

 public static int checkBefore(parmLott parm, parmLottC parmc) {


    int bstat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;
    short tfb = 0;


    i3 = parm.beforei;                  // get latest before index
    grps = parm.groups;                 // # of groups to back up

    if (i3 >= parm.groups) {            // if room to back up

       loop2:
       while (i3 > 0 && grps > 0) {     // while room to back up

          i3--;                         // back up one tee time slot
          parm.beforei--;               // save new index
          ttime = parmc.timeA[i3];       // get tee time from array

          if (ttime >= parm.ftime) {    // if tee time is acceptable

             tfb = parmc.fbA[i3];        // get associated f/b

             if (tfb == parm.rfb) {     // if matching f/b found

                grps--;                 // match found, continue to back up
             }
          } else {

             bstat = 1;                // can't go back any further
             grps = parm.groups;       // reset # of groups
             i3++;                     // went too far - go back so check fails
             break loop2;              // exit while loop
          }
       }                               // end of loop2 WHILE

       if (grps == 0) {                // did we get all the way back ?

          grps = parm.groups;          // yes, reset # of groups and go check again

       } else {                        // no

          bstat = 1;                   // can't go back any more
       }                               // end of IF i3 > groups

    } else {

       bstat = 1;           // can't go back any more
    }                       // end of IF i3 > groups

    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(bstat);

 }  // end of checkBefore


 public static int checkAfter(parmLott parm, parmLottC parmc) {


    short tfb = 0;
    int astat = 0;
    int i3 = 0;
    int grps = 0;
    int ttime = 0;
    int i4 = 999;                      // init start location

    parm.afteri++;                     // bump to next after spot
    i3 = parm.afteri;
    grps = parm.groups;                // restore # of groups

    if ((i3 + parm.groups) < 100) {    // if room to go ahead

       loop3:
       while (i3 < 100 && grps > 0) {   // while room to search ahead

          ttime = parmc.timeA[i3];       // get tee time from array

          if (ttime > 0 && ttime <= parm.ltime) {    // if tee time is acceptable

             tfb = parmc.fbA[i3];        // get associated f/b

             if (tfb == parm.rfb) {     // if matching f/b found

                grps--;            // match found, continue to go ahead

                if (i4 == 999) {   // first find?

                   i4 = i3;        // yes, save start index
                }
             }
          } else {

             astat = 1;             // can't go ahead any further
             grps = parm.groups;    // yes, reset # of groups and check others
             break loop3;           // exit while loop
          }
          i3++;                    // go ahead one tee time slot
            
       }                          // end of loop3 WHILE

       if (grps == 0) {     // did we get all the way ahead ?

          grps = parm.groups;    // yes, reset # of groups and go check again

          if (i4 != 999) {

             i3 = i4;       // set new start index
          }

       } else {           // no

          astat = 1;      // can't go ahead any more
       }                 // end of IF i3 > groups

    } else {

       astat = 1;           // can't go ahead any more
    }                 // end of IF i3 < 100

    parm.i3 = i3;           // set new values for return
    parm.grps = grps;

    return(astat);

 }  // end of checkAfter


 //************************************************************************
 // moveReqs - Move lottery requests from lreqs to teecurr.
 //
 //   called by:  processLott above
 //
 //       parms:  name   = name of the lottery
 //               date   = date of the lottery requests
 //               course = name of course
 //               con    = db connection
 //************************************************************************

 public static void moveReqs(String name, long date, String course, Connection con) {


   Statement estmt = null;
   Statement stmtN = null;
   PreparedStatement pstmt = null;
   PreparedStatement pstmtd = null;
   PreparedStatement pstmtd2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String player6 = "";
   String player7 = "";
   String player8 = "";
   String player9 = "";
   String player10 = "";
   String player11 = "";
   String player12 = "";
   String player13 = "";
   String player14 = "";
   String player15 = "";
   String player16 = "";
   String player17 = "";
   String player18 = "";
   String player19 = "";
   String player20 = "";
   String player21 = "";
   String player22 = "";
   String player23 = "";
   String player24 = "";
   String player25 = "";

   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String p6cw = "";
   String p7cw = "";
   String p8cw = "";
   String p9cw = "";
   String p10cw = "";
   String p11cw = "";
   String p12cw = "";
   String p13cw = "";
   String p14cw = "";
   String p15cw = "";
   String p16cw = "";
   String p17cw = "";
   String p18cw = "";
   String p19cw = "";
   String p20cw = "";
   String p21cw = "";
   String p22cw = "";
   String p23cw = "";
   String p24cw = "";
   String p25cw = "";

   String user1 = "";
   String user2 = "";
   String user3 = "";
   String user4 = "";
   String user5 = "";
   String user6 = "";
   String user7 = "";
   String user8 = "";
   String user9 = "";
   String user10 = "";
   String user11 = "";
   String user12 = "";
   String user13 = "";
   String user14 = "";
   String user15 = "";
   String user16 = "";
   String user17 = "";
   String user18 = "";
   String user19 = "";
   String user20 = "";
   String user21 = "";
   String user22 = "";
   String user23 = "";
   String user24 = "";
   String user25 = "";

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String userg6 = "";
   String userg7 = "";
   String userg8 = "";
   String userg9 = "";
   String userg10 = "";
   String userg11 = "";
   String userg12 = "";
   String userg13 = "";
   String userg14 = "";
   String userg15 = "";
   String userg16 = "";
   String userg17 = "";
   String userg18 = "";
   String userg19 = "";
   String userg20 = "";
   String userg21 = "";
   String userg22 = "";
   String userg23 = "";
   String userg24 = "";
   String userg25 = "";

   String mNum1 = "";
   String mNum2 = "";
   String mNum3 = "";
   String mNum4 = "";
   String mNum5 = "";

   String color = "";
   String p5 = "";
   String type = "";
   String pref = "";
   String approve = "";
   String day = "";
   String notes = "";
   String in_use_by = "";
   String orig_by = "";
   String parm = "";
   String hndcps = "";

   String player5T = "";
   String user5T = "";
   String p5cwT = "";

   String errorMsg = "";

   String [] userA = new String [25];            // array to hold usernames

   long id = 0;

   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;
   int p96 = 0;
   int p97 = 0;
   int p98 = 0;
   int p99 = 0;
   int p910 = 0;
   int p911 = 0;
   int p912 = 0;
   int p913 = 0;
   int p914 = 0;
   int p915 = 0;
   int p916 = 0;
   int p917 = 0;
   int p918 = 0;
   int p919 = 0;
   int p920 = 0;
   int p921 = 0;
   int p922 = 0;
   int p923 = 0;
   int p924 = 0;
   int p925 = 0;

   int i = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int fb = 0;
   int afb = 0;
   int count = 0;
   int groups = 0;
   int time = 0;
   int rtime = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int players = 0;
   int hide = 0;
   int proNew = 0;
   int proMod = 0;
   int memNew = 0;
   int memMod = 0;
   int proxMins = 0;

   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;

   float hndcp1 = 99;
   float hndcp2 = 99;
   float hndcp3 = 99;
   float hndcp4 = 99;
   float hndcp5 = 99;

   boolean ok = true;


   try {
      //
      //  Get the lottery type for the requested lottery
      //
      pstmt = con.prepareStatement (
         "SELECT type " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         type = rs.getString(1);
      }
      pstmt.close();

   }
   catch (Exception e2) {
        
      errorMsg = "Error in SystemUtils moveReqs (get lottery type): ";
      errorMsg = errorMsg + "Exception: " +e2.getMessage();                  // build error msg

      logError(errorMsg);                                       // log it
   }

   try {

      errorMsg = "Error in SystemUtils moveReqs (get lottery requests): ";

      //
      //  Get the Lottery Requests for the lottery passed
      //
      pstmt = con.prepareStatement (
         "SELECT mm, dd, yy, day, time, " +
         "player1, player2, player3, player4, player5, player6, player7, player8, player9, player10, " +
         "player11, player12, player13, player14, player15, player16, player17, player18, player19, player20, " +
         "player21, player22, player23, player24, player25, " +
         "user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, " +
         "user11, user12, user13, user14, user15, user16, user17, user18, user19, user20, " +
         "user21, user22, user23, user24, user25, " +
         "p1cw, p2cw, p3cw, p4cw, p5cw, p6cw, p7cw, p8cw, p9cw, p10cw, " +
         "p11cw, p12cw, p13cw, p14cw, p15cw, p16cw, p17cw, p18cw, p19cw, p20cw, " +
         "p21cw, p22cw, p23cw, p24cw, p25cw, " +
         "notes, hideNotes, fb, proNew, proMod, memNew, memMod, id, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5, players, userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, " +
         "userg9, userg10, userg11, userg12, userg13, userg14, userg15, userg16, userg17, userg18, userg19, " +
         "userg20, userg21, userg22, userg23, userg24, userg25, orig_by, " +
         "p91, p92, p93, p94, p95, p96, p97, p98, p99, p910, " +
         "p911, p912, p913, p914, p915, p916, p917, p918, p919, p920, " +
         "p921, p922, p923, p924, p925 " +
         "FROM lreqs3 " +
         "WHERE name = ? AND date = ? AND courseName = ? AND state = 2");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      pstmt.setLong(2, date);
      pstmt.setString(3, course);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      while (rs.next()) {

         mm = rs.getInt(1);
         dd = rs.getInt(2);
         yy = rs.getInt(3);
         day = rs.getString(4);
         rtime = rs.getInt(5);
         player1 = rs.getString(6);
         player2 = rs.getString(7);
         player3 = rs.getString(8);
         player4 = rs.getString(9);
         player5 = rs.getString(10);
         player6 = rs.getString(11);
         player7 = rs.getString(12);
         player8 = rs.getString(13);
         player9 = rs.getString(14);
         player10 = rs.getString(15);
         player11 = rs.getString(16);
         player12 = rs.getString(17);
         player13 = rs.getString(18);
         player14 = rs.getString(19);
         player15 = rs.getString(20);
         player16 = rs.getString(21);
         player17 = rs.getString(22);
         player18 = rs.getString(23);
         player19 = rs.getString(24);
         player20 = rs.getString(25);
         player21 = rs.getString(26);
         player22 = rs.getString(27);
         player23 = rs.getString(28);
         player24 = rs.getString(29);
         player25 = rs.getString(30);
         user1 = rs.getString(31);
         user2 = rs.getString(32);
         user3 = rs.getString(33);
         user4 = rs.getString(34);
         user5 = rs.getString(35);
         user6 = rs.getString(36);
         user7 = rs.getString(37);
         user8 = rs.getString(38);
         user9 = rs.getString(39);
         user10 = rs.getString(40);
         user11 = rs.getString(41);
         user12 = rs.getString(42);
         user13 = rs.getString(43);
         user14 = rs.getString(44);
         user15 = rs.getString(45);
         user16 = rs.getString(46);
         user17 = rs.getString(47);
         user18 = rs.getString(48);
         user19 = rs.getString(49);
         user20 = rs.getString(50);
         user21 = rs.getString(51);
         user22 = rs.getString(52);
         user23 = rs.getString(53);
         user24 = rs.getString(54);
         user25 = rs.getString(55);
         p1cw = rs.getString(56);
         p2cw = rs.getString(57);
         p3cw = rs.getString(58);
         p4cw = rs.getString(59);
         p5cw = rs.getString(60);
         p6cw = rs.getString(61);
         p7cw = rs.getString(62);
         p8cw = rs.getString(63);
         p9cw = rs.getString(64);
         p10cw = rs.getString(65);
         p11cw = rs.getString(66);
         p12cw = rs.getString(67);
         p13cw = rs.getString(68);
         p14cw = rs.getString(69);
         p15cw = rs.getString(70);
         p16cw = rs.getString(71);
         p17cw = rs.getString(72);
         p18cw = rs.getString(73);
         p19cw = rs.getString(74);
         p20cw = rs.getString(75);
         p21cw = rs.getString(76);
         p22cw = rs.getString(77);
         p23cw = rs.getString(78);
         p24cw = rs.getString(79);
         p25cw = rs.getString(80);
         notes = rs.getString(81);
         hide = rs.getInt(82);
         fb = rs.getInt(83);
         proNew = rs.getInt(84);
         proMod = rs.getInt(85);
         memNew = rs.getInt(86);
         memMod = rs.getInt(87);
         id = rs.getLong(88);
         groups = rs.getInt(89);
         atime1 = rs.getInt(90);
         atime2 = rs.getInt(91);
         atime3 = rs.getInt(92);
         atime4 = rs.getInt(93);
         atime5 = rs.getInt(94);
         afb = rs.getInt(95);
         p5 = rs.getString(96);
         players = rs.getInt(97);
         userg1 = rs.getString(98);
         userg2 = rs.getString(99);
         userg3 = rs.getString(100);
         userg4 = rs.getString(101);
         userg5 = rs.getString(102);
         userg6 = rs.getString(103);
         userg7 = rs.getString(104);
         userg8 = rs.getString(105);
         userg9 = rs.getString(106);
         userg10 = rs.getString(107);
         userg11 = rs.getString(108);
         userg12 = rs.getString(109);
         userg13 = rs.getString(110);
         userg14 = rs.getString(111);
         userg15 = rs.getString(112);
         userg16 = rs.getString(113);
         userg17 = rs.getString(114);
         userg18 = rs.getString(115);
         userg19 = rs.getString(116);
         userg20 = rs.getString(117);
         userg21 = rs.getString(118);
         userg22 = rs.getString(119);
         userg23 = rs.getString(120);
         userg24 = rs.getString(121);
         userg25 = rs.getString(122);
         orig_by = rs.getString(123);
         p91 = rs.getInt(124);
         p92 = rs.getInt(125);
         p93 = rs.getInt(126);
         p94 = rs.getInt(127);
         p95 = rs.getInt(128);
         p96 = rs.getInt(129);
         p97 = rs.getInt(130);
         p98 = rs.getInt(131);
         p99 = rs.getInt(132);
         p910 = rs.getInt(133);
         p911 = rs.getInt(134);
         p912 = rs.getInt(135);
         p913 = rs.getInt(136);
         p914 = rs.getInt(137);
         p915 = rs.getInt(138);
         p916 = rs.getInt(139);
         p917 = rs.getInt(140);
         p918 = rs.getInt(141);
         p919 = rs.getInt(142);
         p920 = rs.getInt(143);
         p921 = rs.getInt(144);
         p922 = rs.getInt(145);
         p923 = rs.getInt(146);
         p924 = rs.getInt(147);
         p925 = rs.getInt(148);

         if (atime1 != 0) {          // only process if its assigned

            ok = checkInUse(con, id);     // check if assigned tee times are currently in use

            if (ok == true) {             // if ok to proceed (no tee times are in use)

               //
               //  Save the usernames
               //
               userA[0] = user1;
               userA[1] = user2;
               userA[2] = user3;
               userA[3] = user4;
               userA[4] = user5;
               userA[5] = user6;
               userA[6] = user7;
               userA[7] = user8;
               userA[8] = user9;
               userA[9] = user10;
               userA[10] = user11;
               userA[11] = user12;
               userA[12] = user13;
               userA[13] = user14;
               userA[14] = user15;
               userA[15] = user16;
               userA[16] = user17;
               userA[17] = user18;
               userA[18] = user19;
               userA[19] = user20;
               userA[20] = user21;
               userA[21] = user22;
               userA[22] = user23;
               userA[23] = user24;
               userA[24] = user25;

               //
               //  create 1 tee time for each group requested (groups = )
               //
               time = atime1;    // time for this tee time
               hndcp1 = 99;      // init
               hndcp2 = 99;
               hndcp3 = 99;
               hndcp4 = 99;
               hndcp5 = 99;
               mNum1 = "";
               mNum2 = "";
               mNum3 = "";
               mNum4 = "";
               mNum5 = "";

               //
               //  Save area for tee time and email processing - by groups
               //
               String g1user1 = user1;
               String g1user2 = user2;
               String g1user3 = user3;
               String g1user4 = user4;
               String g1user5 = "";
               String g1player1 = player1;
               String g1player2 = player2;
               String g1player3 = player3;
               String g1player4 = player4;
               String g1player5 = "";
               String g1p1cw = p1cw;
               String g1p2cw = p2cw;
               String g1p3cw = p3cw;
               String g1p4cw = p4cw;
               String g1p5cw = "";
               String g1userg1 = userg1;
               String g1userg2 = userg2;
               String g1userg3 = userg3;
               String g1userg4 = userg4;
               String g1userg5 = "";
               int g1p91 = p91;
               int g1p92 = p92;
               int g1p93 = p93;
               int g1p94 = p94;
               int g1p95 = 0;

               String g2user1 = "";
               String g2user2 = "";
               String g2user3 = "";
               String g2user4 = "";
               String g2user5 = "";
               String g2player1 = "";
               String g2player2 = "";
               String g2player3 = "";
               String g2player4 = "";
               String g2player5 = "";
               String g2p1cw = "";
               String g2p2cw = "";
               String g2p3cw = "";
               String g2p4cw = "";
               String g2p5cw = "";
               String g2userg1 = "";
               String g2userg2 = "";
               String g2userg3 = "";
               String g2userg4 = "";
               String g2userg5 = "";
               int g2p91 = 0;
               int g2p92 = 0;
               int g2p93 = 0;
               int g2p94 = 0;
               int g2p95 = 0;

               String g3user1 = "";
               String g3user2 = "";
               String g3user3 = "";
               String g3user4 = "";
               String g3user5 = "";
               String g3player1 = "";
               String g3player2 = "";
               String g3player3 = "";
               String g3player4 = "";
               String g3player5 = "";
               String g3p1cw = "";
               String g3p2cw = "";
               String g3p3cw = "";
               String g3p4cw = "";
               String g3p5cw = "";
               String g3userg1 = "";
               String g3userg2 = "";
               String g3userg3 = "";
               String g3userg4 = "";
               String g3userg5 = "";
               int g3p91 = 0;
               int g3p92 = 0;
               int g3p93 = 0;
               int g3p94 = 0;
               int g3p95 = 0;

               String g4user1 = "";
               String g4user2 = "";
               String g4user3 = "";
               String g4user4 = "";
               String g4user5 = "";
               String g4player1 = "";
               String g4player2 = "";
               String g4player3 = "";
               String g4player4 = "";
               String g4player5 = "";
               String g4p1cw = "";
               String g4p2cw = "";
               String g4p3cw = "";
               String g4p4cw = "";
               String g4p5cw = "";
               String g4userg1 = "";
               String g4userg2 = "";
               String g4userg3 = "";
               String g4userg4 = "";
               String g4userg5 = "";
               int g4p91 = 0;
               int g4p92 = 0;
               int g4p93 = 0;
               int g4p94 = 0;
               int g4p95 = 0;

               String g5user1 = "";
               String g5user2 = "";
               String g5user3 = "";
               String g5user4 = "";
               String g5user5 = "";
               String g5player1 = "";
               String g5player2 = "";
               String g5player3 = "";
               String g5player4 = "";
               String g5player5 = "";
               String g5p1cw = "";
               String g5p2cw = "";
               String g5p3cw = "";
               String g5p4cw = "";
               String g5p5cw = "";
               String g5userg1 = "";
               String g5userg2 = "";
               String g5userg3 = "";
               String g5userg4 = "";
               String g5userg5 = "";
               int g5p91 = 0;
               int g5p92 = 0;
               int g5p93 = 0;
               int g5p94 = 0;
               int g5p95 = 0;

               errorMsg = "Error in SystemUtils moveReqs (get mem# and hndcp): ";

               //
               //  Get Member# and Handicap for each member
               //
               if (!user1.equals( "" )) {        // if player is a member

                  parm = getUser(con, user1);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum1 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user2.equals( "" )) {        // if player is a member

                  parm = getUser(con, user2);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum2 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user3.equals( "" )) {        // if player is a member

                  parm = getUser(con, user3);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum3 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (!user4.equals( "" )) {        // if player is a member

                  parm = getUser(con, user4);     // get mNum and hndcp for member

                  StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                  mNum4 = tok.nextToken();         // member Number
                  hndcps = tok.nextToken();       // handicap

                  hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
               }
               if (p5.equals( "Yes" )) {

                  if (!user5.equals( "" )) {        // if player is a member

                     parm = getUser(con, user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  g1player5 = player5;
                  g1user5 = user5;
                  g1p5cw = p5cw;
                  g1userg5 = userg5;
                  g1p95 = p95;
               }

               if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                  mNum1 = "";                  // convert back to null
               }
               if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                  mNum2 = "";                  // convert back to null
               }
               if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                  mNum3 = "";                  // convert back to null
               }
               if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                  mNum4 = "";                  // convert back to null
               }
               if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                  mNum5 = "";                  // convert back to null
               }

               errorMsg = "Error in SystemUtils moveReqs (put group 1 in tee sheet): ";
               //
               //  Update the tee slot in teecurr
               //
               //  Clear the lottery name so this tee time is displayed in _sheet even though there
               //  may be some requests still outstanding (state = 4).
               //
               PreparedStatement pstmt6 = con.prepareStatement (
                  "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                  "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                  "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                  "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                  "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                  "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                  "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                  "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                  "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

               pstmt6.clearParameters();        // clear the parms
               pstmt6.setString(1, g1player1);
               pstmt6.setString(2, g1player2);
               pstmt6.setString(3, g1player3);
               pstmt6.setString(4, g1player4);
               pstmt6.setString(5, g1user1);
               pstmt6.setString(6, g1user2);
               pstmt6.setString(7, g1user3);
               pstmt6.setString(8, g1user4);
               pstmt6.setString(9, g1p1cw);
               pstmt6.setString(10, g1p2cw);
               pstmt6.setString(11, g1p3cw);
               pstmt6.setString(12, g1p4cw);
               pstmt6.setFloat(13, hndcp1);
               pstmt6.setFloat(14, hndcp2);
               pstmt6.setFloat(15, hndcp3);
               pstmt6.setFloat(16, hndcp4);
               pstmt6.setString(17, g1player5);
               pstmt6.setString(18, g1user5);
               pstmt6.setString(19, g1p5cw);
               pstmt6.setFloat(20, hndcp5);
               pstmt6.setString(21, notes);
               pstmt6.setInt(22, hide);
               pstmt6.setInt(23, proNew);
               pstmt6.setInt(24, proMod);
               pstmt6.setInt(25, memNew);
               pstmt6.setInt(26, memMod);
               pstmt6.setString(27, mNum1);
               pstmt6.setString(28, mNum2);
               pstmt6.setString(29, mNum3);
               pstmt6.setString(30, mNum4);
               pstmt6.setString(31, mNum5);
               pstmt6.setString(32, g1userg1);
               pstmt6.setString(33, g1userg2);
               pstmt6.setString(34, g1userg3);
               pstmt6.setString(35, g1userg4);
               pstmt6.setString(36, g1userg5);
               pstmt6.setString(37, orig_by);
               pstmt6.setInt(38, g1p91);
               pstmt6.setInt(39, g1p92);
               pstmt6.setInt(40, g1p93);
               pstmt6.setInt(41, g1p94);
               pstmt6.setInt(42, g1p95);

               pstmt6.setLong(43, date);
               pstmt6.setInt(44, time);
               pstmt6.setInt(45, afb);
               pstmt6.setString(46, course);

               pstmt6.executeUpdate();      // execute the prepared stmt

               pstmt6.close();

               //
               //  Do next group, if there is one
               //
               if (groups > 1) {

                  time = atime2;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g2player1 = player6;
                     g2player2 = player7;
                     g2player3 = player8;
                     g2player4 = player9;
                     g2player5 = player10;
                     g2user1 = user6;
                     g2user2 = user7;
                     g2user3 = user8;
                     g2user4 = user9;
                     g2user5 = user10;
                     g2p1cw = p6cw;
                     g2p2cw = p7cw;
                     g2p3cw = p8cw;
                     g2p4cw = p9cw;
                     g2p5cw = p10cw;
                     g2userg1 = userg6;
                     g2userg2 = userg7;
                     g2userg3 = userg8;
                     g2userg4 = userg9;
                     g2userg5 = userg10;
                     g2p91 = p96;
                     g2p92 = p97;
                     g2p93 = p98;
                     g2p94 = p99;
                     g2p95 = p910;

                  } else {

                     g2player1 = player5;
                     g2player2 = player6;
                     g2player3 = player7;
                     g2player4 = player8;
                     g2user1 = user5;
                     g2user2 = user6;
                     g2user3 = user7;
                     g2user4 = user8;
                     g2p1cw = p5cw;
                     g2p2cw = p6cw;
                     g2p3cw = p7cw;
                     g2p4cw = p8cw;
                     g2userg1 = userg5;
                     g2userg2 = userg6;
                     g2userg3 = userg7;
                     g2userg4 = userg8;
                     g2p91 = p95;
                     g2p92 = p96;
                     g2p93 = p97;
                     g2p94 = p98;
                  }

                  if (!g2user1.equals( "" )) {        // if player is a member

                     parm = getUser(con, g2user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user2.equals( "" )) {        // if player is a member

                     parm = getUser(con, g2user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user3.equals( "" )) {        // if player is a member

                     parm = getUser(con, g2user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user4.equals( "" )) {        // if player is a member

                     parm = getUser(con, g2user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g2user5.equals( "" )) {        // if player is a member

                     parm = getUser(con, g2user5);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum5 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 2 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g2player1);
                  pstmt6.setString(2, g2player2);
                  pstmt6.setString(3, g2player3);
                  pstmt6.setString(4, g2player4);
                  pstmt6.setString(5, g2user1);
                  pstmt6.setString(6, g2user2);
                  pstmt6.setString(7, g2user3);
                  pstmt6.setString(8, g2user4);
                  pstmt6.setString(9, g2p1cw);
                  pstmt6.setString(10, g2p2cw);
                  pstmt6.setString(11, g2p3cw);
                  pstmt6.setString(12, g2p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g2player5);
                  pstmt6.setString(18, g2user5);
                  pstmt6.setString(19, g2p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g2userg1);
                  pstmt6.setString(33, g2userg2);
                  pstmt6.setString(34, g2userg3);
                  pstmt6.setString(35, g2userg4);
                  pstmt6.setString(36, g2userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g2p91);
                  pstmt6.setInt(39, g2p92);
                  pstmt6.setInt(40, g2p93);
                  pstmt6.setInt(41, g2p94);
                  pstmt6.setInt(42, g2p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb);
                  pstmt6.setString(46, course);

                  pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 2) {

                  time = atime3;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g3player1 = player11;
                     g3player2 = player12;
                     g3player3 = player13;
                     g3player4 = player14;
                     g3player5 = player15;
                     g3user1 = user11;
                     g3user2 = user12;
                     g3user3 = user13;
                     g3user4 = user14;
                     g3user5 = user15;
                     g3p1cw = p11cw;
                     g3p2cw = p12cw;
                     g3p3cw = p13cw;
                     g3p4cw = p14cw;
                     g3p5cw = p15cw;
                     g3userg1 = userg11;
                     g3userg2 = userg12;
                     g3userg3 = userg13;
                     g3userg4 = userg14;
                     g3userg5 = userg15;
                     g3p91 = p911;
                     g3p92 = p912;
                     g3p93 = p913;
                     g3p94 = p914;
                     g3p95 = p915;

                  } else {

                     g3player1 = player9;
                     g3player2 = player10;
                     g3player3 = player11;
                     g3player4 = player12;
                     g3user1 = user9;
                     g3user2 = user10;
                     g3user3 = user11;
                     g3user4 = user12;
                     g3p1cw = p9cw;
                     g3p2cw = p10cw;
                     g3p3cw = p11cw;
                     g3p4cw = p12cw;
                     g3userg1 = userg9;
                     g3userg2 = userg10;
                     g3userg3 = userg11;
                     g3userg4 = userg12;
                     g3p91 = p99;
                     g3p92 = p910;
                     g3p93 = p911;
                     g3p94 = p912;
                  }

                  if (!g3user1.equals( "" )) {        // if player is a member

                     parm = getUser(con, g3user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user2.equals( "" )) {        // if player is a member

                     parm = getUser(con, g3user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user3.equals( "" )) {        // if player is a member

                     parm = getUser(con, g3user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g3user4.equals( "" )) {        // if player is a member

                     parm = getUser(con, g3user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g3user5.equals( "" )) {        // if player is a member

                        parm = getUser(con, g3user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 3 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g3player1);
                  pstmt6.setString(2, g3player2);
                  pstmt6.setString(3, g3player3);
                  pstmt6.setString(4, g3player4);
                  pstmt6.setString(5, g3user1);
                  pstmt6.setString(6, g3user2);
                  pstmt6.setString(7, g3user3);
                  pstmt6.setString(8, g3user4);
                  pstmt6.setString(9, g3p1cw);
                  pstmt6.setString(10, g3p2cw);
                  pstmt6.setString(11, g3p3cw);
                  pstmt6.setString(12, g3p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g3player5);
                  pstmt6.setString(18, g3user5);
                  pstmt6.setString(19, g3p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g3userg1);
                  pstmt6.setString(33, g3userg2);
                  pstmt6.setString(34, g3userg3);
                  pstmt6.setString(35, g3userg4);
                  pstmt6.setString(36, g3userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g3p91);
                  pstmt6.setInt(39, g3p92);
                  pstmt6.setInt(40, g3p93);
                  pstmt6.setInt(41, g3p94);
                  pstmt6.setInt(42, g3p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb);
                  pstmt6.setString(46, course);

                  pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 3) {

                  time = atime4;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g4player1 = player16;
                     g4player2 = player17;
                     g4player3 = player18;
                     g4player4 = player19;
                     g4player5 = player20;
                     g4user1 = user16;
                     g4user2 = user17;
                     g4user3 = user18;
                     g4user4 = user19;
                     g4user5 = user20;
                     g4p1cw = p16cw;
                     g4p2cw = p17cw;
                     g4p3cw = p18cw;
                     g4p4cw = p19cw;
                     g4p5cw = p20cw;
                     g4userg1 = userg16;
                     g4userg2 = userg17;
                     g4userg3 = userg18;
                     g4userg4 = userg19;
                     g4userg5 = userg20;
                     g4p91 = p916;
                     g4p92 = p917;
                     g4p93 = p918;
                     g4p94 = p919;
                     g4p95 = p920;

                  } else {

                     g4player1 = player13;
                     g4player2 = player14;
                     g4player3 = player15;
                     g4player4 = player16;
                     g4user1 = user13;
                     g4user2 = user14;
                     g4user3 = user15;
                     g4user4 = user16;
                     g4p1cw = p13cw;
                     g4p2cw = p14cw;
                     g4p3cw = p15cw;
                     g4p4cw = p16cw;
                     g4userg1 = userg13;
                     g4userg2 = userg14;
                     g4userg3 = userg15;
                     g4userg4 = userg16;
                     g4p91 = p913;
                     g4p92 = p914;
                     g4p93 = p915;
                     g4p94 = p916;
                  }

                  if (!g4user1.equals( "" )) {        // if player is a member

                     parm = getUser(con, g4user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user2.equals( "" )) {        // if player is a member

                     parm = getUser(con, g4user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user3.equals( "" )) {        // if player is a member

                     parm = getUser(con, g4user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g4user4.equals( "" )) {        // if player is a member

                     parm = getUser(con, g4user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g4user5.equals( "" )) {        // if player is a member

                        parm = getUser(con, g4user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 4 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g4player1);
                  pstmt6.setString(2, g4player2);
                  pstmt6.setString(3, g4player3);
                  pstmt6.setString(4, g4player4);
                  pstmt6.setString(5, g4user1);
                  pstmt6.setString(6, g4user2);
                  pstmt6.setString(7, g4user3);
                  pstmt6.setString(8, g4user4);
                  pstmt6.setString(9, g4p1cw);
                  pstmt6.setString(10, g4p2cw);
                  pstmt6.setString(11, g4p3cw);
                  pstmt6.setString(12, g4p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g4player5);
                  pstmt6.setString(18, g4user5);
                  pstmt6.setString(19, g4p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g4userg1);
                  pstmt6.setString(33, g4userg2);
                  pstmt6.setString(34, g4userg3);
                  pstmt6.setString(35, g4userg4);
                  pstmt6.setString(36, g4userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g4p91);
                  pstmt6.setInt(39, g4p92);
                  pstmt6.setInt(40, g4p93);
                  pstmt6.setInt(41, g4p94);
                  pstmt6.setInt(42, g4p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb);
                  pstmt6.setString(46, course);

                  pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //
               //  Do next group, if there is one
               //
               if (groups > 4) {

                  time = atime5;    // time for this tee time
                  hndcp1 = 99;      // init
                  hndcp2 = 99;
                  hndcp3 = 99;
                  hndcp4 = 99;
                  hndcp5 = 99;
                  mNum1 = "";
                  mNum2 = "";
                  mNum3 = "";
                  mNum4 = "";
                  mNum5 = "";

                  if (p5.equals( "Yes" )) {

                     g5player1 = player21;
                     g5player2 = player22;
                     g5player3 = player23;
                     g5player4 = player24;
                     g5player5 = player25;
                     g5user1 = user21;
                     g5user2 = user22;
                     g5user3 = user23;
                     g5user4 = user24;
                     g5user5 = user25;
                     g5p1cw = p21cw;
                     g5p2cw = p22cw;
                     g5p3cw = p23cw;
                     g5p4cw = p24cw;
                     g5p5cw = p25cw;
                     g5userg1 = userg21;
                     g5userg2 = userg22;
                     g5userg3 = userg23;
                     g5userg4 = userg24;
                     g5userg5 = userg25;
                     g5p91 = p921;
                     g5p92 = p922;
                     g5p93 = p923;
                     g5p94 = p924;
                     g5p95 = p925;

                  } else {

                     g5player1 = player17;
                     g5player2 = player18;
                     g5player3 = player19;
                     g5player4 = player20;
                     g5user1 = user17;
                     g5user2 = user18;
                     g5user3 = user19;
                     g5user4 = user20;
                     g5p1cw = p17cw;
                     g5p2cw = p18cw;
                     g5p3cw = p19cw;
                     g5p4cw = p20cw;
                     g5userg1 = userg17;
                     g5userg2 = userg18;
                     g5userg3 = userg19;
                     g5userg4 = userg20;
                     g5p91 = p917;
                     g5p92 = p918;
                     g5p93 = p919;
                     g5p94 = p920;
                  }

                  if (!g5user1.equals( "" )) {        // if player is a member

                     parm = getUser(con, g5user1);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum1 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp1 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user2.equals( "" )) {        // if player is a member

                     parm = getUser(con, g5user2);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum2 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp2 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user3.equals( "" )) {        // if player is a member

                     parm = getUser(con, g5user3);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum3 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp3 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (!g5user4.equals( "" )) {        // if player is a member

                     parm = getUser(con, g5user4);     // get mNum and hndcp for member

                     StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                     mNum4 = tok.nextToken();         // member Number
                     hndcps = tok.nextToken();       // handicap

                     hndcp4 = Float.parseFloat(hndcps);   // convert back to floating int
                  }
                  if (p5.equals( "Yes" )) {

                     if (!g5user5.equals( "" )) {        // if player is a member

                        parm = getUser(con, g5user5);     // get mNum and hndcp for member

                        StringTokenizer tok = new StringTokenizer( parm, "," );     // delimiters are comma - parse the parm

                        mNum5 = tok.nextToken();         // member Number
                        hndcps = tok.nextToken();       // handicap

                        hndcp5 = Float.parseFloat(hndcps);   // convert back to floating int
                     }
                  }

                  if (mNum1.equals( "*@&" )) {    // if garbage so parm would work

                     mNum1 = "";                  // convert back to null
                  }
                  if (mNum2.equals( "*@&" )) {    // if garbage so parm would work

                     mNum2 = "";                  // convert back to null
                  }
                  if (mNum3.equals( "*@&" )) {    // if garbage so parm would work

                     mNum3 = "";                  // convert back to null
                  }
                  if (mNum4.equals( "*@&" )) {    // if garbage so parm would work

                     mNum4 = "";                  // convert back to null
                  }
                  if (mNum5.equals( "*@&" )) {    // if garbage so parm would work

                     mNum5 = "";                  // convert back to null
                  }

                  errorMsg = "Error in SystemUtils moveReqs (put group 5 in tee sheet): ";
                  //
                  //  Update the tee slot in teecurr
                  //
                  //  Clear the lottery name so this tee time is displayed in _sheet even though there
                  //  may be some requests still outstanding (state = 4).
                  //
                  pstmt6 = con.prepareStatement (
                     "UPDATE teecurr2 SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, " +
                     "username1 = ?, username2 = ?, username3 = ?, username4 = ?, p1cw = ?, " +
                     "p2cw = ?, p3cw = ?, p4cw = ?,  in_use = 0, hndcp1 = ?, hndcp2 = ?, hndcp3 = ?, " +
                     "hndcp4 = ?, show1 = 0, show2 = 0, show3 = 0, show4 = 0, player5 = ?, username5 = ?, " +
                     "p5cw = ?, hndcp5 = ?, show5 = 0, notes = ?, hideNotes = ?, lottery = '', proNew = ?, proMod = ?, " +
                     "memNew = ?, memMod = ?, mNum1 = ?, mNum2 = ?, mNum3 = ?, mNum4 = ?, mNum5 = ?, " +
                     "userg1 = ?, userg2 = ?, userg3 = ?, userg4 = ?, userg5 = ?, orig_by = ?, " +
                     "p91 = ?, p92 = ?, p93 = ?, p94 = ?, p95 = ? " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt6.clearParameters();        // clear the parms
                  pstmt6.setString(1, g5player1);
                  pstmt6.setString(2, g5player2);
                  pstmt6.setString(3, g5player3);
                  pstmt6.setString(4, g5player4);
                  pstmt6.setString(5, g5user1);
                  pstmt6.setString(6, g5user2);
                  pstmt6.setString(7, g5user3);
                  pstmt6.setString(8, g5user4);
                  pstmt6.setString(9, g5p1cw);
                  pstmt6.setString(10, g5p2cw);
                  pstmt6.setString(11, g5p3cw);
                  pstmt6.setString(12, g5p4cw);
                  pstmt6.setFloat(13, hndcp1);
                  pstmt6.setFloat(14, hndcp2);
                  pstmt6.setFloat(15, hndcp3);
                  pstmt6.setFloat(16, hndcp4);
                  pstmt6.setString(17, g5player5);
                  pstmt6.setString(18, g5user5);
                  pstmt6.setString(19, g5p5cw);
                  pstmt6.setFloat(20, hndcp5);
                  pstmt6.setString(21, notes);
                  pstmt6.setInt(22, hide);
                  pstmt6.setInt(23, proNew);
                  pstmt6.setInt(24, proMod);
                  pstmt6.setInt(25, memNew);
                  pstmt6.setInt(26, memMod);
                  pstmt6.setString(27, mNum1);
                  pstmt6.setString(28, mNum2);
                  pstmt6.setString(29, mNum3);
                  pstmt6.setString(30, mNum4);
                  pstmt6.setString(31, mNum5);
                  pstmt6.setString(32, g5userg1);
                  pstmt6.setString(33, g5userg2);
                  pstmt6.setString(34, g5userg3);
                  pstmt6.setString(35, g5userg4);
                  pstmt6.setString(36, g5userg5);
                  pstmt6.setString(37, orig_by);
                  pstmt6.setInt(38, g5p91);
                  pstmt6.setInt(39, g5p92);
                  pstmt6.setInt(40, g5p93);
                  pstmt6.setInt(41, g5p94);
                  pstmt6.setInt(42, g5p95);

                  pstmt6.setLong(43, date);
                  pstmt6.setInt(44, time);
                  pstmt6.setInt(45, afb);
                  pstmt6.setString(46, course);

                  pstmt6.executeUpdate();      // execute the prepared stmt

                  pstmt6.close();

               }    // end of IF groups

               //*****************************************************************************
               //  Send an email to all in this request
               //*****************************************************************************
               //
               errorMsg = "Error in SystemUtils moveReqs (send email): ";

               String clubName = "";

               try {

                  estmt = con.createStatement();        // create a statement

                  rs2 = estmt.executeQuery("SELECT clubName " +
                                          "FROM club5 WHERE clubName != ''");

                  if (rs2.next()) {

                     clubName = rs2.getString(1);
                  }
                  estmt.close();
               }
               catch (Exception ignore) {
               }

               //
               //  Get today's date and time for email processing
               //
               Calendar ecal = new GregorianCalendar();               // get todays date
               int eyear = ecal.get(Calendar.YEAR);
               int emonth = ecal.get(Calendar.MONTH);
               int eday = ecal.get(Calendar.DAY_OF_MONTH);
               int e_hourDay = ecal.get(Calendar.HOUR_OF_DAY);
               int e_min = ecal.get(Calendar.MINUTE);

               int e_time = 0;
               long e_date = 0;

               //
               //  Build the 'time' string for display
               //
               //    Adjust the time based on the club's time zone (we are Central)
               //
               e_time = (e_hourDay * 100) + e_min;

               e_time = adjustTime(con, e_time);       // adjust for time zone

               if (e_time < 0) {          // if negative, then we went back or ahead one day

                  e_time = 0 - e_time;        // convert back to positive value

                  if (e_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                     //
                     // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                     //
                     ecal.add(Calendar.DATE,1);                     // get next day's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);

                  } else {                        // we rolled back 1 day

                     //
                     // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                     //
                     ecal.add(Calendar.DATE,-1);                     // get yesterday's date

                     eyear = ecal.get(Calendar.YEAR);
                     emonth = ecal.get(Calendar.MONTH);
                     eday = ecal.get(Calendar.DAY_OF_MONTH);
                  }
               }

               int e_hour = e_time / 100;                // get adjusted hour
               e_min = e_time - (e_hour * 100);          // get minute value
               int e_am_pm = 0;                         // preset to AM

               if (e_hour > 11) {

                  e_am_pm = 1;                // PM
                  e_hour = e_hour - 12;       // set to 12 hr clock
               }
               if (e_hour == 0) {

                  e_hour = 12;
               }

               String email_time = "";

               emonth = emonth + 1;                            // month starts at zero
               e_date = (eyear * 10000) + (emonth * 100) + eday;

               //
               //  get date/time string for email message
               //
               if (e_am_pm == 0) {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " AM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " AM";
                  }
               } else {
                  if (e_min < 10) {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":0" + e_min + " PM";
                  } else {
                     email_time = emonth + "/" + eday + "/" + eyear + " at " + e_hour + ":" + e_min + " PM";
                  }
               }

               //
               //***********************************************
               //  Send email notification if necessary
               //***********************************************
               //
               String to = "";                          // to address
               String f_b = "";
               String eampm = "";
               String etime = "";
               String enewMsg = "";
               int emailOpt = 0;                        // user's email option parm
               int ehr = 0;
               int emin = 0;
               int send = 0;

               PreparedStatement pstmte1 = null;

               //
               //  set the front/back value
               //
               f_b = "Front";

               if (afb == 1) {

                  f_b = "Back";
               }

               String enew1 = "";
               String enew2 = "";
               String subject = "";

               if (clubName.startsWith( "Old Oaks" )) {
                 
                  enew1 = "The following Tee Time has been ASSIGNED.\n\n";
                  enew2 = "The following Tee Times have been ASSIGNED.\n\n";
                  subject = "ForeTees Tee Time Assignment Notification";

               } else {

                  if (clubName.startsWith( "Westchester" )) {

                     enew1 = "The following Draw Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Draw Tee Times have been ASSIGNED.\n\n";
                     subject = "Your Tee Time for Weekend Draw";

                  } else {

                     enew1 = "The following Lottery Tee Time has been ASSIGNED.\n\n";
                     enew2 = "The following Lottery Tee Times have been ASSIGNED.\n\n";
                     subject = "ForeTees Lottery Assignment Notification";
                  }
               }

               if (!clubName.equals( "" )) {

                  subject = subject + " - " + clubName;
               }

               Properties properties = new Properties();
               properties.put("mail.smtp.host", host);                      // set outbound host address
               properties.put("mail.smtp.port", port);                      // set outbound port
               properties.put("mail.smtp.auth", "true");                    // set 'use authentication'

               Session mailSess = Session.getInstance(properties, getAuthenticator());   // get session properties

               MimeMessage message = new MimeMessage(mailSess);

               try {

                  message.setFrom(new InternetAddress(efrom));                               // set from addr

                  message.setSubject( subject );                                            // set subject line
                  message.setSentDate(new java.util.Date());                                // set date/time sent
               }
               catch (Exception ignore) {
               }

               //
               //  Set the recipient addresses
               //
               if (!g1user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g1user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g1user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g2user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g2user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g3user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g3user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               if (!g4user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g4user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g4user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user1.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user1);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user2.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user2);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user3.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user3);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user4.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user4);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }
               if (!g5user5.equals( "" )) {       // if new user exist and not same as old usernames

                  try {
                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();        // clear the parms
                     pstmte1.setString(1, g5user5);
                     rs2 = pstmte1.executeQuery();      // execute the prepared stmt

                     if (rs2.next()) {

                        to = rs2.getString(1);        // user's email address
                        emailOpt = rs2.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                           send = 1;
                        }
                     }
                     pstmte1.close();              // close the stmt
                  }
                  catch (Exception ignore) {
                  }
               }

               //
               //  send email if anyone to send it to
               //
               if (send != 0) {        // if any email addresses specified for members
                  //
                  //  Create the message content
                  //
                  if (groups > 1) {
                     enewMsg = header + enew2 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                  } else {
                     enewMsg = header + enew1 + day + " " + mm + "/" + dd + "/" + yy + " " +
                                      "on the " + f_b + " tee ";
                  }
                  if (!course.equals( "" )) {

                     enewMsg = enewMsg + "of Course: " + course;
                  }

                  //
                  //  convert time to hour and minutes for email msg
                  //
                  time = atime1;              // time for this tee time
                  ehr = time / 100;
                  emin = time - (ehr * 100);
                  eampm = " AM";
                  if (ehr > 12) {

                     eampm = " PM";
                     ehr = ehr - 12;       // convert from military time
                  }
                  if (ehr == 12) {

                     eampm = " PM";
                  }
                  if (ehr == 0) {

                     ehr = 12;
                     eampm = " AM";
                  }

                  if (emin < 10) {

                     etime = ehr + ":0" + emin + eampm;

                  } else {

                     etime = ehr + ":" + emin + eampm;
                  }

                  enewMsg = enewMsg + "\n at " + etime + "\n";

                  if (!g1player1.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 1: " + g1player1 + "  " + g1p1cw;
                  }
                  if (!g1player2.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 2: " + g1player2 + "  " + g1p2cw;
                  }
                  if (!g1player3.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 3: " + g1player3 + "  " + g1p3cw;
                  }
                  if (!g1player4.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 4: " + g1player4 + "  " + g1p4cw;
                  }
                  if (!g1player5.equals( "" )) {

                     enewMsg = enewMsg + "\nPlayer 5: " + g1player5 + "  " + g1p5cw;
                  }

                  if (groups > 1) {

                     time = atime2;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g2player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g2player1 + "  " + g2p1cw;
                     }
                     if (!g2player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g2player2 + "  " + g2p2cw;
                     }
                     if (!g2player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g2player3 + "  " + g2p3cw;
                     }
                     if (!g2player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g2player4 + "  " + g2p4cw;
                     }
                     if (!g2player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g2player5 + "  " + g2p5cw;
                     }
                  }

                  if (groups > 2) {

                     time = atime3;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g3player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g3player1 + "  " + g3p1cw;
                     }
                     if (!g3player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g3player2 + "  " + g3p2cw;
                     }
                     if (!g3player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g3player3 + "  " + g3p3cw;
                     }
                     if (!g3player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g3player4 + "  " + g3p4cw;
                     }
                     if (!g3player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g3player5 + "  " + g3p5cw;
                     }
                  }

                  if (groups > 3) {

                     time = atime4;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g4player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g4player1 + "  " + g4p1cw;
                     }
                     if (!g4player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g4player2 + "  " + g4p2cw;
                     }
                     if (!g4player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g4player3 + "  " + g4p3cw;
                     }
                     if (!g4player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g4player4 + "  " + g4p4cw;
                     }
                     if (!g4player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g4player5 + "  " + g4p5cw;
                     }
                  }

                  if (groups > 4) {

                     time = atime5;              // time for this tee time
                     ehr = time / 100;
                     emin = time - (ehr * 100);
                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin < 10) {

                        etime = ehr + ":0" + emin + eampm;

                     } else {

                        etime = ehr + ":" + emin + eampm;
                     }

                     enewMsg = enewMsg + "\n\n at " + etime + "\n";

                     if (!g5player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + g5player1 + "  " + g5p1cw;
                     }
                     if (!g5player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + g5player2 + "  " + g5p2cw;
                     }
                     if (!g5player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + g5player3 + "  " + g5p3cw;
                     }
                     if (!g5player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + g5player4 + "  " + g5p4cw;
                     }
                     if (!g5player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + g5player5 + "  " + g5p5cw;
                     }
                  }

                  enewMsg = enewMsg + trailer;

                  try {
                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!
                  }
                  catch (Exception ignore) {
                  }
               }     // end of IF send

               //
               // delete the request after players have been moved
               //
               pstmtd = con.prepareStatement (
                        "Delete FROM lreqs3 WHERE id = ?");

               pstmtd.clearParameters();               // clear the parms
               pstmtd.setLong(1, id);
               pstmtd.executeUpdate();

               pstmtd.close();

               //
               //  If lottery type = Weighted By Proximity, determine time between request and assigned
               //
               if (type.equals( "WeightedBP" )) {
                 
                  proxMins = calcProxTime(rtime, atime1);      // calculate mins difference
                    
                  pstmtd2 = con.prepareStatement (
                        "INSERT INTO lassigns5 (username, lname, date, mins) " +
                        "VALUES (?, ?, ?, ?)");

                  //
                  //  Save each members' weight for this request
                  //
                  for (i=0; i<25; i++) {          // check all 25 possible players

                     if (!userA[i].equals( "" )) {     // if player is a member

                        pstmtd2.clearParameters();
                        pstmtd2.setString(1, userA[i]);
                        pstmtd2.setString(2, name);
                        pstmtd2.setLong(3, date);
                        pstmtd2.setInt(4, proxMins);

                        pstmtd2.executeUpdate();
                     }
                  }
                  pstmtd2.close();

               }                // end of IF Weighted by Proximity lottery type

            }  // end of IF ok (tee times in use?)

         } else {     // req is NOT assigned

            //
            //  Change the state to 5 (processed & approved) so _sheet will show the others
            //
            PreparedStatement pstmt7s = con.prepareStatement (
                "UPDATE lreqs3 SET state = 5 " +
                "WHERE id = ?");

            pstmt7s.clearParameters();        // clear the parms
            pstmt7s.setLong(1, id);

            pstmt7s.executeUpdate();

            pstmt7s.close();

         }     // end of IF req is assigned

      }    // end of WHILE lreqs - process next request

      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
      return;
   }

 }  // end of moveReqs


 // *********************************************************
 //  determine minutes between 2 time values
 // *********************************************************

 private static int calcProxTime(int rtime, int atime) {


   int time1 = 0;
   int time2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int mins = 0;

   //
   //  determine the number of minutes between the 2 time values passed
   //
   if (rtime != atime) {          // if different, else return zero
     
      if (rtime > atime) {          // determine which is larger

         time1 = atime;             // put smaller value in time1
         time2 = rtime;

      } else {

         time1 = rtime;             // put smaller value in time1
         time2 = atime;
      }

      hr1 = time1 / 100;             // get hr
      min1 = time1 - (hr1 * 100);    // get minute

      hr2 = time2 / 100;            
      min2 = time2 - (hr2 * 100);   

      while (hr2 > hr1) {
        
         mins += 60;        // add 60 minutes
         hr2--;             // reduce hour
      }

      if (min2 > min1) {
        
         mins = mins + (min2 - min1);      // add the difference
           
      } else {

         mins = mins - (min1 - min2);      // subtract the difference
      }
   }

   return(mins);

 }   // end of getEndTime


 //************************************************************************
 //  checkInUse - check tee times to see if they are in use
 //
 //   called by:  moveRegs above
 //
 //       parms:  con = db connection
 //               id  = id of the lottery request
 //
 //************************************************************************

 private static boolean checkInUse(Connection con, long id) {


   PreparedStatement pstmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;

   String errorMsg = "";
   String name = "";
   String course = "";
   String p5 = "";

   int groups = 0;
   int fb = 0;
   int atime1 = 0;
   int atime2 = 0;
   int atime3 = 0;
   int atime4 = 0;
   int atime5 = 0;
   int in_use = 0;

   long date = 0;

   boolean ok = true;


   try {

      errorMsg = "Error in SystemUtils checkInUse: ";

      //
      //  Get the Lottery Request for the lottery id passed
      //
      PreparedStatement pstmt = con.prepareStatement (
         "SELECT name, date, courseName, groups, atime1, atime2, atime3, " +
         "atime4, atime5, afb, p5 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, id);

      rs = pstmt.executeQuery();      // execute the prepared stmt again to start with first

      if (rs.next()) {

         name = rs.getString(1);
         date = rs.getLong(2);
         course = rs.getString(3);
         groups = rs.getInt(4);
         atime1 = rs.getInt(5);
         atime2 = rs.getInt(6);
         atime3 = rs.getInt(7);
         atime4 = rs.getInt(8);
         atime5 = rs.getInt(9);
         fb = rs.getInt(10);
         p5 = rs.getString(11);

         if (atime1 != 0) {          // only process if its assigned

            //
            //  Check the in-use flag for this tee time
            //
            pstmt2 = con.prepareStatement (
               "SELECT in_use " +
               "FROM teecurr2 " +
               "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, date);
            pstmt2.setInt(2, atime1);
            pstmt2.setInt(3, fb);
            pstmt2.setString(4, course);

            rs2 = pstmt2.executeQuery();

            if (rs2.next()) {

               in_use = rs2.getInt(1);
            }
            pstmt2.close();

            if (in_use == 0) {

               //
               //  Do next group, if there is one
               //
               if (groups > 1) {

                  //
                  //  Check the in-use flag for this tee time
                  //
                  pstmt2 = con.prepareStatement (
                     "SELECT in_use " +
                     "FROM teecurr2 " +
                     "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  pstmt2.clearParameters();        // clear the parms
                  pstmt2.setLong(1, date);
                  pstmt2.setInt(2, atime2);
                  pstmt2.setInt(3, fb);
                  pstmt2.setString(4, course);

                  rs2 = pstmt2.executeQuery();

                  if (rs2.next()) {

                     in_use = rs2.getInt(1);
                  }
                  pstmt2.close();

                  if (in_use == 0) {

                     //
                     //  Do next group, if there is one
                     //
                     if (groups > 2) {

                        //
                        //  Check the in-use flag for this tee time
                        //
                        pstmt2 = con.prepareStatement (
                           "SELECT in_use " +
                           "FROM teecurr2 " +
                           "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                        pstmt2.clearParameters();        // clear the parms
                        pstmt2.setLong(1, date);
                        pstmt2.setInt(2, atime3);
                        pstmt2.setInt(3, fb);
                        pstmt2.setString(4, course);

                        rs2 = pstmt2.executeQuery();

                        if (rs2.next()) {

                           in_use = rs2.getInt(1);
                        }
                        pstmt2.close();

                        if (in_use == 0) {

                           //
                           //  Do next group, if there is one
                           //
                           if (groups > 3) {

                              //
                              //  Check the in-use flag for this tee time
                              //
                              pstmt2 = con.prepareStatement (
                                 "SELECT in_use " +
                                 "FROM teecurr2 " +
                                 "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                              pstmt2.clearParameters();        // clear the parms
                              pstmt2.setLong(1, date);
                              pstmt2.setInt(2, atime4);
                              pstmt2.setInt(3, fb);
                              pstmt2.setString(4, course);

                              rs2 = pstmt2.executeQuery();

                              if (rs2.next()) {

                                 in_use = rs2.getInt(1);
                              }
                              pstmt2.close();

                              if (in_use == 0) {

                                 //
                                 //  Do next group, if there is one
                                 //
                                 if (groups > 4) {

                                    //
                                    //  Check the in-use flag for this tee time
                                    //
                                    pstmt2 = con.prepareStatement (
                                       "SELECT in_use " +
                                       "FROM teecurr2 " +
                                       "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                                    pstmt2.clearParameters();        // clear the parms
                                    pstmt2.setLong(1, date);
                                    pstmt2.setInt(2, atime5);
                                    pstmt2.setInt(3, fb);
                                    pstmt2.setString(4, course);

                                    rs2 = pstmt2.executeQuery();

                                    if (rs2.next()) {

                                       in_use = rs2.getInt(1);
                                    }
                                    pstmt2.close();

                                    if (in_use > 0) {

                                       ok = false;           // do not change these tee times
                                    }
                                 }                      // end of IF groups > 4

                              } else {

                                 ok = false;           // do not change these tee times
                              }
                           }                      // end of IF groups > 3

                        } else {

                           ok = false;           // do not change these tee times
                        }
                     }                      // end of IF groups > 2

                  } else {

                     ok = false;           // do not change these tee times
                  }
               }                           // end of IF groups > 1

            } else {

               ok = false;           // do not change these tee times
            }
         }                           // end of IF atime1 > 0
      }            // end of IF lottery req

      pstmt.close();
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }
   return(ok);
 }  // end of checkInUse


 //************************************************************************
 // getUser - get User information - get user's handicap and member number.
 //
 //   called by:  moveRegs above
 //
 //       parms:  con   = db connection
 //               user  = username of the member
 //
 //************************************************************************

 private static String getUser(Connection con, String user) {


   ResultSet rs = null;

   String parm = "";
   String mNum = "";
   String hndcps = "";

   float hndcp = 99;

   try {

      PreparedStatement pstmt = con.prepareStatement (
               "SELECT c_hancap, memNum FROM member2b WHERE username = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, user);
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         hndcp = rs.getFloat(1);
         mNum = rs.getString(2);
      }
      pstmt.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils getUser: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   if (mNum.equals( "" )) {

      mNum = "*@&";         // so return parm will work
   }

   hndcps = String.valueOf( hndcp );     // create string value of hndcp
   parm = mNum + "," + hndcps;           // create one parm to return

   return(parm);
 }  // end of getUser


 //************************************************************************
 // movePlayers - Move players from a partial lottery request to a lottery
 //               request with space available.
 //
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request to move from
 //               tid   = id of the lottery request to move to
 //
 //************************************************************************

 private static void movePlayers(Connection con, long id, long tid) {


   ResultSet rs = null;

   int i = 0;
   int i2 = 0;

   String [] playersA = new String [25];       // players in the 'to' request
   String [] playersB = new String [4];        // players in the 'from' request

   //
   //  Get the players from the 'from' request (can't be more than 4)
   //
   try {
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT player1, player2, player3, player4 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {          // get all of them

         playersB[0] = rs.getString(1);
         playersB[1] = rs.getString(2);
         playersB[2] = rs.getString(3);
         playersB[3] = rs.getString(4);
      }
      pstmt8.close();

      PreparedStatement pstmt9 = con.prepareStatement (
         "SELECT player1, player2, player3, player4, player5, player6, player7, player8, " +
         "player9, player10, player11, player12, player13, player14, player15, player16, " +
         "player17, player18, player19, player20, player21, player22, player23, player24, player25 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt9.clearParameters();        // clear the parms
      pstmt9.setLong(1, tid);
      rs = pstmt9.executeQuery();

      if (rs.next()) {          // get all of them

         playersA[0] = rs.getString(1);
         playersA[1] = rs.getString(2);
         playersA[2] = rs.getString(3);
         playersA[3] = rs.getString(4);
         playersA[4] = rs.getString(5);
         playersA[5] = rs.getString(6);
         playersA[6] = rs.getString(7);
         playersA[7] = rs.getString(8);
         playersA[8] = rs.getString(9);
         playersA[9] = rs.getString(10);
         playersA[10] = rs.getString(11);
         playersA[11] = rs.getString(12);
         playersA[12] = rs.getString(13);
         playersA[13] = rs.getString(14);
         playersA[14] = rs.getString(15);
         playersA[15] = rs.getString(16);
         playersA[16] = rs.getString(17);
         playersA[17] = rs.getString(18);
         playersA[18] = rs.getString(19);
         playersA[19] = rs.getString(20);
         playersA[20] = rs.getString(21);
         playersA[21] = rs.getString(22);
         playersA[22] = rs.getString(23);
         playersA[23] = rs.getString(24);
         playersA[24] = rs.getString(25);
      }
      pstmt9.close();

      //
      //  Find the available spots and move the players
      //
      loop1:
      while (i < 25 && i2 < 5) {

         if (playersA[i].equals( "" )) {

            playersA[i] = playersB[i2];       // move player
            i2++;                             // next player
         }
         i++;
      }
      //
      //  Put all the players into the 'to' request
      //
      PreparedStatement pstmt2 = con.prepareStatement (
          "UPDATE lreqs3 " +
          "SET player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, player6 = ?, player7 = ?, player8 = ?, " +
          "player9 = ?, player10 = ?, player11 = ?, player12 = ?, player13 = ?, player14 = ?, player15 = ?, player16 = ?, " +
          "player17 = ?, player18 = ?, player19 = ?, player20 = ?, player21 = ?, player22 = ?, player23 = ?, " +
          "player24 = ?, player25 = ? " +
          "WHERE id = ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setString(1, playersA[0]);
      pstmt2.setString(2, playersA[1]);
      pstmt2.setString(3, playersA[2]);
      pstmt2.setString(4, playersA[3]);
      pstmt2.setString(5, playersA[4]);
      pstmt2.setString(6, playersA[5]);
      pstmt2.setString(7, playersA[6]);
      pstmt2.setString(8, playersA[7]);
      pstmt2.setString(9, playersA[8]);
      pstmt2.setString(10, playersA[9]);
      pstmt2.setString(11, playersA[10]);
      pstmt2.setString(12, playersA[11]);
      pstmt2.setString(13, playersA[12]);
      pstmt2.setString(14, playersA[13]);
      pstmt2.setString(15, playersA[14]);
      pstmt2.setString(16, playersA[15]);
      pstmt2.setString(17, playersA[16]);
      pstmt2.setString(18, playersA[17]);
      pstmt2.setString(19, playersA[18]);
      pstmt2.setString(20, playersA[19]);
      pstmt2.setString(21, playersA[20]);
      pstmt2.setString(22, playersA[21]);
      pstmt2.setString(23, playersA[22]);
      pstmt2.setString(24, playersA[23]);
      pstmt2.setString(25, playersA[24]);
      pstmt2.setLong(26, tid);

      pstmt2.executeUpdate();

      pstmt2.close();

      //
      // delete the request after players have been moved
      //
      PreparedStatement pstmt1 = con.prepareStatement (
               "Delete FROM lreqs3 WHERE id = ?");

      pstmt1.clearParameters();               // clear the parms
      pstmt1.setLong(1, id);
      pstmt1.executeUpdate();

      pstmt1.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils movePlayer: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

 }  // end of movePlayers


 //************************************************************************
 //  getFirstTime - determine the earliest time member will accept
 //                 (for assigning a tee time - lottery)
 //
 //   called by:  processLott - above
 //               Proshop_mlottery
 //
 //       parms:  time   = requested time from lottery request
 //               before = number of minutes before req'd time member will accept
 //
 //   return:  time to accept
 //
 //************************************************************************

 public static int getFirstTime(int time, int before) {


   int hr = 0;
   int min = 0;

   hr = time / 100;
   min = time - (hr * 100);

   while (before > 0) {

      if (before <= min) {

         min = min - before;
         before = 0;

      } else {     // before > min

         before = before - min;   // before will still be > 0

         if (hr > 0) {

            min = 60;
            hr--;

         } else {      // hr is 0 or less - exit

            before = 0;
            hr = 0;
         }
      }
   }
   if (hr == 0) {

      time = 0100;         // this is early enough
   } else {

      time = (hr * 100) + min;
   }
   return(time);

 }  // end of getFirstTime


 //************************************************************************
 //  getLastTime - determine the latest time member will accept
 //                 (for assigning a tee time - lottery)
 //
 //   called by:  processLott - above
 //               Proshop_mlottery
 //
 //       parms:  time   = requested time from lottery request
 //               after = number of minutes after req'd time member will accept
 //
 //   return:  time to accept
 //
 //************************************************************************

 public static int getLastTime(int time, int after) {


   int hr = 0;
   int min = 0;

   hr = time / 100;
   min = time - (hr * 100);

   while (after > 0) {

      if (after >= 60) {

         if (hr < 23) {

            hr++;
            after = after - 60;

         } else {      // hr is too high - exit

            after = 0;
            hr = 23;
         }

      } else {     // after < 60

         min = min + after;
         after = 0;               // done

         if (min > 59) {

            min = min - 60;
            hr++;

            if (hr > 23) {

               hr = 23;
            }
         }
      }
   }

   time = (hr * 100) + min;

   return(time);

 }  // end of getLastTime


 //************************************************************************
 // getWeight - Determine the weight of all the players in the lottery
 //               request that was passed.
 //
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request
 //               name  = name of lottery
 //
 //************************************************************************

 private static int getWeight(Connection con, long id, String name) {


   ResultSet rs = null;

   int i = 0;
   int weight = 0;     // total weight for req
   int high = 0;       // highest
   int low = 9999;     // lowest
   int avg = 0;        // average
   int w = 0;
   int players = 0;    // # of players in req

   int adays = 0;      // days to accumulate points
   int wdpts = 0;      // points for weekday rounds
   int wepts = 0;      // points for weekend rounds
   int evpts = 0;      // points for event rounds
   int gpts = 0;       // points for each guest round
   int nopts = 0;      // points for each no-show
   int select = 0;     // Lottery selection based on; total pts, avg pts, highest, lowest
   int guest = 0;      // points for each guest in request
   int days = 0;

   long sdate = 0;
   long edate = 0;

   String user = "";

   String [] userA = new String [25];       // players in the request
   String [] usergA = new String [25];      // guests (owning players) in the request

   //
   //  get lottery data (pts, etc.) for the lottery requested
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT adays, wdpts, wepts, evpts, gpts, nopts, selection, guest " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      rs = pstmt1.executeQuery();

      if (rs.next()) {          // get lottery

         adays = rs.getInt(1);
         wdpts = rs.getInt(2);
         wepts = rs.getInt(3);
         evpts = rs.getInt(4);
         gpts = rs.getInt(5);
         nopts = rs.getInt(6);
         select = rs.getInt(7);
         guest = rs.getInt(8);
      }
      pstmt1.close();

      //
      //  Determine the start and end dates for searches
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      edate = year * 10000;
      edate = edate + (month * 100);
      edate = edate + day;                          // end date = yyyymmdd

      //
      // roll cal back to find the start date
      //
      days = 0 - adays;                             // create negative number

      cal.add(Calendar.DATE,days);                  // roll back 'adays' days

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      sdate = year * 10000;
      sdate = sdate + (month * 100);
      sdate = sdate + day;                          // date = yyyymmdd

      //
      //  Get the players from the request
      //
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, " +
         "user14, user15, user16, user17, user18, user19, user20, user21, user22, user23, user24, user25, players, " +
         "userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, " +
         "userg14, userg15, userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {                 // get the request info

         userA[0] = rs.getString(1);
         userA[1] = rs.getString(2);
         userA[2] = rs.getString(3);
         userA[3] = rs.getString(4);
         userA[4] = rs.getString(5);
         userA[5] = rs.getString(6);
         userA[6] = rs.getString(7);
         userA[7] = rs.getString(8);
         userA[8] = rs.getString(9);
         userA[9] = rs.getString(10);
         userA[10] = rs.getString(11);
         userA[11] = rs.getString(12);
         userA[12] = rs.getString(13);
         userA[13] = rs.getString(14);
         userA[14] = rs.getString(15);
         userA[15] = rs.getString(16);
         userA[16] = rs.getString(17);
         userA[17] = rs.getString(18);
         userA[18] = rs.getString(19);
         userA[19] = rs.getString(20);
         userA[20] = rs.getString(21);
         userA[21] = rs.getString(22);
         userA[22] = rs.getString(23);
         userA[23] = rs.getString(24);
         userA[24] = rs.getString(25);
         players = rs.getInt(26);
         usergA[0] = rs.getString(27);
         usergA[1] = rs.getString(28);
         usergA[2] = rs.getString(29);
         usergA[3] = rs.getString(30);
         usergA[4] = rs.getString(31);
         usergA[5] = rs.getString(32);
         usergA[6] = rs.getString(33);
         usergA[7] = rs.getString(34);
         usergA[8] = rs.getString(35);
         usergA[9] = rs.getString(36);
         usergA[10] = rs.getString(37);
         usergA[11] = rs.getString(38);
         usergA[12] = rs.getString(39);
         usergA[13] = rs.getString(40);
         usergA[14] = rs.getString(41);
         usergA[15] = rs.getString(42);
         usergA[16] = rs.getString(43);
         usergA[17] = rs.getString(44);
         usergA[18] = rs.getString(45);
         usergA[19] = rs.getString(46);
         usergA[20] = rs.getString(47);
         usergA[21] = rs.getString(48);
         usergA[22] = rs.getString(49);
         usergA[23] = rs.getString(50);
         usergA[24] = rs.getString(51);
      }
      pstmt8.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils getWeight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   if (players > 0) {                         // if ok to continue

      for (i=0; i<25; i++) {

         user = userA[i];                      // get user

         if (!user.equals( "" )) {

            w = get1Weight(con, user, wdpts, wepts, evpts, gpts, nopts, sdate, edate);  // get this user's weight

            //
            //  Accumulate total weight, highest weight, lowest weight, avg weight
            //
            weight = weight + w;              // accumulate total weight for request

            if (w > high) {

               high = w;          // new high
            }
            if (w < low) {    

               low = w;           // new low
            }
         }
      }

      //
      //  calculate guests' weight
      //
      for (i=0; i<25; i++) {

         if (!usergA[i].equals( "" )) {      // each non-null represents one guest

            if (guest == 0) {           // if guest don't count

               players--;               // remove guest from # of players (so avg is correct)
            }
            if (guest == 1) {           // if guest counts same as high member

               weight = weight + high;
            }
            if (guest == 2) {           // if guest counts same as low member

               weight = weight + low;
            }
         }
      }

      //
      //  calculate average weight per player
      //
      avg = weight/players;
        
      if (avg == 0) {       
        
         if (weight > 0) {     // if there was a weight
           
            avg = 1;           // round up
         }
      }

      //
      //  determine which weight to return based on lottery options
      //
      if (select == 2) {     // if Average Points of group

         weight = avg;       // return average weight
      }
      if (select == 3) {     // if Highest Points of group members

         weight = high;       // return highest weight
      }
      if (select == 4) {     // if Lowest Points of group members

         weight = low;       // return lowest weight
      }
      // else - return total weight (select = 1)
   }

   return(weight);

 }  // end of getWeight


 //************************************************************************
 // get1Weight - Determine the lottery weight of the player requested.
 //
 //
 //   called by:  getWeight above
 //
 //       parms:  con   = db connection
 //               user  = username of the player to check
 //               wdpts = weekday points
 //               wepts = weekend points
 //               evpts = event points
 //               gpts  = guest points
 //               nopts = no-show points
 //               sdate = date to start looking
 //               edate = date to stop looking
 //
 //************************************************************************

 public static int get1Weight(Connection con, String user, int wdpts, int wepts, int evpts,
                               int gpts, int nopts, long sdate, long edate) {


   ResultSet rs = null;

   String day = "";
   String event = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int weight = 0;

   //
   //  calculate the weight for this user based on the parms passed and his/her past tee times
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
                "SELECT day, event " +
                "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                "((username1 = ? && show1 = 1) || (username2 = ? && show2 = 1) || (username3 = ? && show3 = 1) || " +
                "(username4 = ? && show4 = 1) || (username5 = ? && show5 = 1))");

      //
      //  find tee slots for the specified date period
      //
      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, sdate);
      pstmt2.setLong(2, edate);
      pstmt2.setString(3, user);
      pstmt2.setString(4, user);
      pstmt2.setString(5, user);
      pstmt2.setString(6, user);
      pstmt2.setString(7, user);
      rs = pstmt2.executeQuery();

      while (rs.next()) {

         day = rs.getString(1);
         event = rs.getString(2);

         if (evpts > 0 && !event.equals( "" )) {

            weight = weight + evpts;          // add event points

         } else {

            if (!day.equalsIgnoreCase( "Saturday" ) && !day.equalsIgnoreCase( "Sunday" )) {   // if not a w/e

               weight = weight + wdpts;       // or weekday points

            } else {

               weight = weight + wepts;       // or weekend points
            }
         }
      }
      pstmt2.close();

      if (gpts > 0) {             // if points for guest rounds

         pstmt2 = con.prepareStatement (
                   "SELECT userg1, userg2, userg3, userg4, userg5 " +
                   "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                   "(userg1 = ? || userg2 = ? || userg3 = ? || userg4 = ? || userg5 = ?)");

         //
         //  find tee times where this member had a guest
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, sdate);
         pstmt2.setLong(2, edate);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         rs = pstmt2.executeQuery();

         while (rs.next()) {

            userg1 = rs.getString(1);
            userg2 = rs.getString(2);
            userg3 = rs.getString(3);
            userg4 = rs.getString(4);
            userg5 = rs.getString(5);

            if (user.equalsIgnoreCase( userg1 )) {

               weight = weight + gpts;          // add guest points for each guest
            }
            if (user.equalsIgnoreCase( userg2 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg3 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg4 )) {

               weight = weight + gpts;
            }
            if (user.equalsIgnoreCase( userg5 )) {

               weight = weight + gpts;
            }
         }
         pstmt2.close();
      }

      if (nopts > 0) {             // if points for no-shows

         pstmt2 = con.prepareStatement (
                   "SELECT mm " +
                   "FROM teepast2 WHERE date >= ? AND date <= ? AND " +
                   "((username1 = ? && show1 <> 1) || (username2 = ? && show2 <> 1) || (username3 = ? && show3 <> 1) || " +
                   "(username4 = ? && show4 <> 1) || (username5 = ? && show5 <> 1))");

         //
         //  find tee times where this member had a guest
         //
         pstmt2.clearParameters();        // clear the parms
         pstmt2.setLong(1, sdate);
         pstmt2.setLong(2, edate);
         pstmt2.setString(3, user);
         pstmt2.setString(4, user);
         pstmt2.setString(5, user);
         pstmt2.setString(6, user);
         pstmt2.setString(7, user);
         rs = pstmt2.executeQuery();

         while (rs.next()) {

            weight = weight + nopts;
         }
         pstmt2.close();
      }
   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils get1Weight: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   return(weight);

 }  // end of get1Weight


 //************************************************************************
 // getWeightP - Determine the weight of all the players in the lottery
 //              request that was passed.
 //
 //     For 'Weighted By Proximity' type Lottery
 //
 //   called by:  processLott above
 //
 //       parms:  con   = db connection
 //               id    = id of the lottery request
 //               name  = name of lottery
 //
 //************************************************************************

 private static int getWeightP(Connection con, long id, String name) {


   ResultSet rs = null;

   int i = 0;
   int weight = 0;     // total weight for req
   int high = 0;       // highest
   int low = 9999;     // lowest
   int avg = 0;        // average
   int w = 0;
   int players = 0;    // # of players in req

   int adays = 0;      // days to accumulate points
   int select = 0;     // Lottery selection based on; total pts, avg pts, highest, lowest
   int guest = 0;      // points for each guest in request
   int days = 0;

   long sdate = 0;
   long edate = 0;

   String user = "";

   String [] userA = new String [25];       // players in the request
   String [] usergA = new String [25];      // guests (owning players) in the request

   //
   //  get lottery data (pts, etc.) for the lottery requested
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT adays, selection, guest " +
         "FROM lottery3 " +
         "WHERE name = ?");

      pstmt1.clearParameters();        // clear the parms
      pstmt1.setString(1, name);
      rs = pstmt1.executeQuery();

      if (rs.next()) {          // get lottery

         adays = rs.getInt(1);
         select = rs.getInt(2);
         guest = rs.getInt(3);
      }
      pstmt1.close();

      //
      //  Determine the start and end dates for searches
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      edate = year * 10000;
      edate = edate + (month * 100);
      edate = edate + day;                          // end date = yyyymmdd

      //
      // roll cal back to find the start date
      //
      days = 0 - adays;                             // create negative number

      cal.add(Calendar.DATE,days);                  // roll back 'adays' days

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                            // month starts at zero

      sdate = year * 10000;
      sdate = sdate + (month * 100);
      sdate = sdate + day;                          // date = yyyymmdd

      //
      //  Get the players from the request
      //
      PreparedStatement pstmt8 = con.prepareStatement (
         "SELECT user1, user2, user3, user4, user5, user6, user7, user8, user9, user10, user11, user12, user13, " +
         "user14, user15, user16, user17, user18, user19, user20, user21, user22, user23, user24, user25, players, " +
         "userg1, userg2, userg3, userg4, userg5, userg6, userg7, userg8, userg9, userg10, userg11, userg12, userg13, " +
         "userg14, userg15, userg16, userg17, userg18, userg19, userg20, userg21, userg22, userg23, userg24, userg25 " +
         "FROM lreqs3 " +
         "WHERE id = ?");

      pstmt8.clearParameters();        // clear the parms
      pstmt8.setLong(1, id);
      rs = pstmt8.executeQuery();

      if (rs.next()) {                 // get the request info

         userA[0] = rs.getString(1);
         userA[1] = rs.getString(2);
         userA[2] = rs.getString(3);
         userA[3] = rs.getString(4);
         userA[4] = rs.getString(5);
         userA[5] = rs.getString(6);
         userA[6] = rs.getString(7);
         userA[7] = rs.getString(8);
         userA[8] = rs.getString(9);
         userA[9] = rs.getString(10);
         userA[10] = rs.getString(11);
         userA[11] = rs.getString(12);
         userA[12] = rs.getString(13);
         userA[13] = rs.getString(14);
         userA[14] = rs.getString(15);
         userA[15] = rs.getString(16);
         userA[16] = rs.getString(17);
         userA[17] = rs.getString(18);
         userA[18] = rs.getString(19);
         userA[19] = rs.getString(20);
         userA[20] = rs.getString(21);
         userA[21] = rs.getString(22);
         userA[22] = rs.getString(23);
         userA[23] = rs.getString(24);
         userA[24] = rs.getString(25);
         players = rs.getInt(26);
         usergA[0] = rs.getString(27);
         usergA[1] = rs.getString(28);
         usergA[2] = rs.getString(29);
         usergA[3] = rs.getString(30);
         usergA[4] = rs.getString(31);
         usergA[5] = rs.getString(32);
         usergA[6] = rs.getString(33);
         usergA[7] = rs.getString(34);
         usergA[8] = rs.getString(35);
         usergA[9] = rs.getString(36);
         usergA[10] = rs.getString(37);
         usergA[11] = rs.getString(38);
         usergA[12] = rs.getString(39);
         usergA[13] = rs.getString(40);
         usergA[14] = rs.getString(41);
         usergA[15] = rs.getString(42);
         usergA[16] = rs.getString(43);
         usergA[17] = rs.getString(44);
         usergA[18] = rs.getString(45);
         usergA[19] = rs.getString(46);
         usergA[20] = rs.getString(47);
         usergA[21] = rs.getString(48);
         usergA[22] = rs.getString(49);
         usergA[23] = rs.getString(50);
         usergA[24] = rs.getString(51);
      }
      pstmt8.close();

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils getWeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

   if (players > 0) {                         // if ok to continue

      for (i=0; i<25; i++) {

         user = userA[i];                      // get user

         if (!user.equals( "" )) {

            w = get1WeightP(con, user, sdate, edate);     // get this user's weight

            //
            //  Accumulate total weight, highest weight, lowest weight, avg weight
            //
            weight = weight + w;              // accumulate total weight for request

            if (w > high) {

               high = w;          // new high
            }
            if (w < low) {

               low = w;           // new low
            }
         }
      }

      //
      //  calculate guests' weight
      //
      for (i=0; i<25; i++) {

         if (!usergA[i].equals( "" )) {      // each non-null represents one guest

            if (guest == 0) {           // if guest don't count

               players--;               // remove guest from # of players (so avg is correct)
            }
            if (guest == 1) {           // if guest counts same as high member

               weight = weight + high;
            }
            if (guest == 2) {           // if guest counts same as low member

               weight = weight + low;
            }
         }
      }

      //
      //  calculate average weight per player
      //
      avg = weight/players;

      if (avg == 0) {

         if (weight > 0) {     // if there was a weight

            avg = 1;           // round up
         }
      }

      //
      //  determine which weight to return based on lottery options
      //
      if (select == 2) {     // if Average Points of group

         weight = avg;       // return average weight
      }
      if (select == 3) {     // if Highest Points of group members

         weight = high;       // return highest weight
      }
      if (select == 4) {     // if Lowest Points of group members

         weight = low;       // return lowest weight
      }
      // else - return total weight (select = 1)
   }

   return(weight);

 }  // end of getWeightP


 //************************************************************************
 // get1WeightP - Determine the lottery weight of the player requested.
 //
 //
 //   called by:  getWeight above
 //
 //       parms:  con   = db connection
 //               user  = username of the player to check
 //               sdate = date to start looking
 //               edate = date to stop looking
 //
 //************************************************************************

 public static int get1WeightP(Connection con, String user, long sdate, long edate) {


   ResultSet rs = null;

   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";

   int count = 0;
   int mins = 0;
   int total = 0;
   int weight = 0;

   //
   //  calculate the weight for this user based on the parms passed and his/her past lottery results
   //
   try {

      PreparedStatement pstmt2 = con.prepareStatement (
                "SELECT mins " +
                "FROM lassigns5 WHERE username = ? AND date >= ? AND date <= ?");

      pstmt2.clearParameters();     
      pstmt2.setString(1, user);
      pstmt2.setLong(2, sdate);
      pstmt2.setLong(3, edate);
      rs = pstmt2.executeQuery();

      while (rs.next()) {

         mins = rs.getInt(1);        // minutes from requested time

         total = total + mins;       // keep running total

         count++;                    // count number of req's that were filled
           
      }
      pstmt2.close();
       
      //
      //  Determine weight
      //
      if (count == 0) {              // if no weight yet

         weight = 200;               // assign a default weight that should give good result
           
      } else {
        
         if (total > 0) {              // make sure weight is non-zero (0 indicates user got all requested times)

            weight = total / count;     // calculate weight (average minutes)
         }
      }

   }
   catch (Exception e1) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils get1WeightP: ";
      errorMsg = errorMsg + e1.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
        
      weight = 20;               // default weight
   }

   return(weight);

 }  // end of get1WeightP


 //************************************************************************
 // xTimer - Process 60 minute system timer expiration.  This timer is set
 //              at init time by Login and reset every 60 minutes so we
 //              can check for X's on the tee sheets and remove them when
 //              requested (indicated by club parms).
 //
 //          The same is done for the Event Sign Up table (remove X's).
 //
 //   called by:  min60Timer on timer expiration
 //
 //************************************************************************

 public static void xTimer() {


   Connection con = null;
   Connection con2 = null;
   Statement stmt = null;
   ResultSet rs = null;

   String errorMsg = "";
     

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!

   try {

      //
      //  This must be the master server!!!  If not, let the timer run in case master goes down.
      //
      if (server_id == server_master) {
        
         //
         //  Set the date/time when this timer should expire by next (safety check to ensure timers keep running)
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
         min60Time = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // save date/time stamp

         //
         //  Now verify that the 2 minute timer is running
         //
         cal = new GregorianCalendar();              // get current date & time

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         day = cal.get(Calendar.DAY_OF_MONTH);
         hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value
         min = cal.get(Calendar.MINUTE);

         //
         //  create date & time stamp value (yyyymmddhhmm) for compares
         //
         long currTime = (year * 100000000) + (month * 1000000) + (day * 10000) + (hr * 100) + min;   // form date/time stamp

         if (currTime > min2Time) {    // if 2 min timer not yet set OR did not fire when expected

            minTimer t_timer = new minTimer();      // reset the 2 min timer

            //
            //  log this so we can track how often it must be reset
            //
            errorMsg = "SystemUtils.xTimer: Had to reset the 2 min timer. currTime = " +currTime+ ", min2Time = " +min2Time;
            logError(errorMsg);                                       // log it
         }
                  

         //
         //  Perform timer function for each club in the system database 'clubs' table
         //
         String club = rev;          // get db name for 'clubs' table

         con = dbConn.Connect(club);                           // get a connection


         //
         // Get the club names from the 'clubs' table
         //
         //  Process each club in the table
         //
         stmt = con.createStatement();        // create a statement

         rs = stmt.executeQuery("SELECT clubname FROM clubs");

         while (rs.next()) {               // process each club individually

            club = rs.getString(1);        // get a club name

            xTimerClubs(club);             // go process it
         }  

         stmt.close();

         con.close();             // close the connection to the system db

      }     // end of IF master

      //
      //  reset the 60 minute timer
      //
      min60Timer t2_timer = new min60Timer();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in SystemUtils xTimer: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it

      //
      //  reset the 60 minute timer
      //
      min60Timer t2_timer = new min60Timer();
   }

 }  // end of xTimer


 //************************************************************************
 //  xTimerClubs - subr of xTimer to process each club individually
 //************************************************************************

 public static void xTimerClubs(String club) {


   Connection con = null;

   String errorMsg = "";


   try {

      //
      //  Process the club that was passed 
      //
      con = dbConn.Connect(club);     // get a connection to this club's db

      if (con != null) {

         xTees(club, con);            // check this club's tee sheets for X's

         xEvents(club, con);          // check this club's event signup sheets for X's

         con.close();                 // close the connection to this club

      } else {

         errorMsg = "Error in SystemUtils xTimerClubs - unable to get con for club: " +club;
         logError(errorMsg);                                       // log it
      }

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = "Error in SystemUtils xTimerClubs: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of xTimerClubs


 //************************************************************************
 //  xTees - subr of xTimer to remove X's from Tee Sheets
 //************************************************************************

 public static void xTees(String club, Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int adv_time = 0;
   int adv_time2 = 0;
   int act_hr = 0;
   int act_min = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String course = "";
   String name = "";
   String dayName = "";
   String clubName = "";
   String subject = "";

   String errorMsg = "SystemUtils.xTees Error for club " +club+ " - Exception = ";

   String to = "";                          // to address
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   int emailOpt = 0;                        // user's email option parm
   int send = 0;

   String enew = "WARNING:  The following tee time contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

   //
   //   Get x parms and check tee times for any x's
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT clubName, x, xhrs FROM club5 WHERE clubName != ''");

      if (rs.next()) {                     // one club5 table per club

         clubName = rs.getString(1);
         x = rs.getInt(2);
         xhrs = rs.getInt(3);

         if (club.equals("missionviejo")) xhrs = (24 * 7); // 7 days in advance
         
         if ((x != 0) && (xhrs != 0)) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in tee sheet
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            Calendar cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) +1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            //
            //  adjust hours for the time zone
            //
            adv_time = (hr * 100) + min;                        // get time in hhmm format

            adv_time = adjustTime(con, adv_time);   // adjust the time

            if (adv_time < 0) {          // if negative, then we went back or ahead one day

               adv_time = 0 - adv_time;        // convert back to positive value

               if (adv_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
               }
            }

            //
            //  make sure we have the new time
            //
            hr = adv_time / 100;
            min = adv_time - (hr * 100);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            //
            //  Remove all 'X' players within 'xhrs' hours from now
            //
            pstmt2 = con.prepareStatement (
                      "SELECT date, time, player1, player2, player3, player4, fb, player5, courseName " +
                      "FROM teecurr2 WHERE date <= ? AND time <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            pstmt2.setInt(2, adv_time);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               date = rs2.getLong(1);
               time = rs2.getInt(2);
               player1 = rs2.getString(3);
               player2 = rs2.getString(4);
               player3 = rs2.getString(5);
               player4 = rs2.getString(6);
               fb = rs2.getInt(7);
               player5 = rs2.getString(8);
               course = rs2.getString(9);

               change = 0;                  // init change slot indicator

               //
               //  If player = 'X' then clear it (make available to other players)
               //
               //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
               //
               if ((!player1.equalsIgnoreCase("x")) && (!player1.equals( "" ))) {

                  if (player2.equalsIgnoreCase("x")) {

                     player2 = "";
                     change = 1;
                  }

                  if (player3.equalsIgnoreCase("x")) {

                     player3 = "";
                     change = 1;
                  }

                  if (player4.equalsIgnoreCase("x")) {

                     player4 = "";
                     change = 1;
                  }

                  if (player5.equalsIgnoreCase("x")) {

                     player5 = "";
                     change = 1;
                  }
               }

               if (change == 1) {

                  pstmt3 = con.prepareStatement (
                      "UPDATE teecurr2 SET player2 = ?, player3 = ?, player4 = ?, player5 = ? " +
                              "WHERE date = ? AND time = ? AND fb = ? AND courseName = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, player2);
                  pstmt3.setString(2, player3);
                  pstmt3.setString(3, player4);
                  pstmt3.setString(4, player5);
                  pstmt3.setLong(5, date);
                  pstmt3.setInt(6, time);
                  pstmt3.setInt(7, fb);
                  pstmt3.setString(8, course);
                  count = pstmt3.executeUpdate();

                  pstmt3.close();

               }
            }      // end of while to remove X's

            pstmt2.close();

            //
            //  Send email warnings 24 hrs in advance (X's will be removed)
            //
            cal.add(Calendar.HOUR_OF_DAY,24);             // roll ahead another 24 hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH) +1;
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            adv_time = hr * 100;                          // create time field of hhmm
            adv_time2 = adv_time + 59;

            //
            //  Get today's date and then go up by 24 hours (when x's will be removed)
            //
            cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY, 24);    // roll ahead 24 hours (rest should adjust)

            eyear = cal.get(Calendar.YEAR);
            emonth = cal.get(Calendar.MONTH) +1;
            eday = cal.get(Calendar.DAY_OF_MONTH);

            //
            //  Find all 'X' players within the next 24 hour period (do one hour at a time)
            //
            pstmt2 = con.prepareStatement (
                      "SELECT mm, dd, yy, day, hr, min, player1, player2, player3, player4,  " +
                      "username1, username2, username3, username4, fb, player5, username5, courseName " +
                      "FROM teecurr2 WHERE date = ? AND time >= ? AND time <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setLong(1, adv_date);
            pstmt2.setInt(2, adv_time);
            pstmt2.setInt(3, adv_time2);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               mm = rs2.getInt(1);
               dd = rs2.getInt(2);
               yy = rs2.getInt(3);
               dayName = rs2.getString(4);
               ehr = rs2.getInt(5);
               emin = rs2.getInt(6);
               player1 = rs2.getString(7);
               player2 = rs2.getString(8);
               player3 = rs2.getString(9);
               player4 = rs2.getString(10);
               username1 = rs2.getString(11);
               username2 = rs2.getString(12);
               username3 = rs2.getString(13);
               username4 = rs2.getString(14);
               fb = rs2.getInt(15);
               player5 = rs2.getString(16);
               username5 = rs2.getString(17);
               course = rs2.getString(18);

               //
               //  if any X's in the tee time, then send email to all players with email enabled
               //
               if (player1.equalsIgnoreCase( "x" ) || player2.equalsIgnoreCase( "x" ) ||
                   player3.equalsIgnoreCase( "x" ) || player4.equalsIgnoreCase( "x" ) ||
                   player5.equalsIgnoreCase( "x" )) {

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", host);                // set outbound host address
                  properties.put("mail.smtp.port", port);                // set outbound port
                  properties.put("mail.smtp.auth", "true");              // set 'use authentication'

                  Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

                  MimeMessage message = new MimeMessage(mailSess);
                  message.setFrom(new InternetAddress(efrom));                  // set from addr
                  message.setSentDate(new java.util.Date());                             // set date/time sent

                  subject = "Tee Time Warning";

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  message.setSubject( subject );                      // set subject line

                  enew = enew + emonth + "/" + eday + "/" + eyear + " at approximately " + hr + ":00.\n" +
                                "If you do not fill these positions before that time, the positions will be " +
                                "made available to other members.\n\n";

                  send = 0;                                                    // init

                  if (!username1.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username2.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username3.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username4.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username5.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (send != 0) {                 // shuld we send an email ?

                     eampm = " AM";
                     if (ehr > 12) {

                        eampm = " PM";
                        ehr = ehr - 12;       // convert from military time
                     }
                     if (ehr == 12) {

                        eampm = " PM";
                     }
                     if (ehr == 0) {

                        ehr = 12;
                        eampm = " AM";
                     }

                     if (emin > 9) {

                        etime = ehr + ":" + emin + eampm;

                     } else {

                        etime = ehr + ":0" + emin + eampm;
                     }

                     //
                     //  set the front/back value
                     //
                     f_b = "Front";

                     if (fb == 1) {

                        f_b = "Back";
                     }

                     //
                     //  Create the message content
                     //
                     String enewMsg = header + enew + "Tee Time: " + dayName +
                                      " " + mm + "/" + dd + "/" + yy + " at " + etime + " " +
                                      "on the " + f_b + " tee ";

                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "of Course: " + course;
                     }

                     enewMsg = enewMsg + "\n";

                     if (!player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + player1;
                     }
                     if (!player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + player2;
                     }
                     if (!player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + player3;
                     }
                     if (!player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + player4;
                     }
                     if (!player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + player5;
                     }

                     enewMsg = enewMsg + trailer;

                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!

                  }    // end of IF send

               }  // end of IF any X's

            }      // end of while to send email warnings

            pstmt2.close();

         }               // end of IF X option
      }                  // end of IF rs.next
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg
      logError(errorMsg);                                       // log it
   }

 }  // end of xTees


 //************************************************************************
 //  xEvents - subr of xTimer to remove X's from Tee Sheets
 //************************************************************************

 public static void xEvents(String club, Connection con) {


   Statement stmt = null;
   Statement stmt2 = null;
   ResultSet rs = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   PreparedStatement pstmte = null;
   PreparedStatement pstmt2 = null;
   PreparedStatement pstmt3 = null;
   PreparedStatement pstmte1 = null;

   int hr = 0;
   int min = 0;
   int ehr = 0;
   int emin = 0;
   int eyear = 0;          // date for email msg
   int emonth = 0;
   int eday = 0;
   int e_year = 0;         // date for event email msg
   int e_month = 0;
   int e_day = 0;
   int year = 0;
   int thisYear = 0;
   int month = 0;
   int day = 0;
   int mm = 0;
   int dd = 0;
   int yy = 0;
   int change = 0;
   int x = 0;
   int xhrs = 0;
   int count = 0;
   int fb = 0;
   int id = 0;
   int time = 0;
   int adv_time = 0;
   int adv_time2 = 0;
   int act_hr = 0;
   int act_min = 0;

   long date = 0;
   long adv_date = 0;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String username1 = "";
   String username2 = "";
   String username3 = "";
   String username4 = "";
   String username5 = "";
   String course = "";
   String name = "";
   String dayName = "";
   String clubName = "";
   String subject = "";

   String errorMsg = "SystemUtils.xEvents Error for club " +club+ " - Exception = ";

   String to = "";                          // to address
   String f_b = "";
   String eampm = "";
   String act_ampm = "";
   String act_time = "";
   String etime = "";
   int emailOpt = 0;                        // user's email option parm
   int send = 0;

   String emsg = "WARNING:  The following Event Registration contains one or more player positions reserved with an 'X'.\n" +
                 "The X's will be removed by the system on ";

   //
   //  Get this year for event processing
   //
   Calendar cal = new GregorianCalendar();       // get todays date
   thisYear = cal.get(Calendar.YEAR);

   try {

      //**************************************************************
      //  Process the Event Sign Up tables
      //
      //  Get the parms from the events db for this club.
      //**************************************************************
      //
      pstmte = con.prepareStatement (
                "SELECT name, year, month, day, act_hr, act_min, courseName, x, xhrs " +
                "FROM events2b WHERE name != '' AND year >= ?");

      pstmte.clearParameters();        // clear the parms
      pstmte.setInt(1, thisYear);
      rs = pstmte.executeQuery();

      while (rs.next()) {                     // process each event individually

         name = rs.getString(1);
         e_year = rs.getInt(2);
         e_month = rs.getInt(3);
         e_day = rs.getInt(4);
         act_hr = rs.getInt(5);
         act_min = rs.getInt(6);
         course = rs.getString(7);
         x = rs.getInt(8);
         xhrs = rs.getInt(9);

         //
         //  Create time values
         //
         act_ampm = "AM";

         if (act_hr == 0) {

            act_hr = 12;                 // change to 12 AM (midnight)

         } else {

            if (act_hr == 12) {

               act_ampm = "PM";         // change to Noon
            }
         }
         if (act_hr > 12) {

            act_hr = act_hr - 12;
            act_ampm = "PM";             // change to 12 hr clock
         }


         if ((x != 0) && (xhrs != 0)) {     // if club wants to remove X's

            //
            //  Set date/time values to be used to check for X's in Event Sign Up table
            //
            //  Get today's date and then go up by 'xhrs' hours
            //
            cal = new GregorianCalendar();                // get todays date

            cal.add(Calendar.HOUR_OF_DAY,xhrs);           // roll ahead 'xhrs' hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH) +1;
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            //
            //  adjust hours for the time zone
            //
            adv_time = (hr * 100) + min;                        // get time in hhmm format

            adv_time = adjustTime(con, adv_time);   // adjust the time

            if (adv_time < 0) {          // if negative, then we went back or ahead one day

               adv_time = 0 - adv_time;        // convert back to positive value

               if (adv_time < 100) {           // if hour is zero, then we rolled ahead 1 day

                  //
                  // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                  //
                  cal.add(Calendar.DATE,1);                     // get next day's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);

               } else {                        // we rolled back 1 day

                  //
                  // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                  //
                  cal.add(Calendar.DATE,-1);                     // get yesterday's date

                  year = cal.get(Calendar.YEAR);
                  month = cal.get(Calendar.MONTH) +1;
                  day = cal.get(Calendar.DAY_OF_MONTH);
               }
            }

            //
            //  make sure we have the new time
            //
            hr = adv_time / 100;
            min = adv_time - (hr * 100);

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            //
            //  Remove all 'X' players within 'xhrs' hours from now
            //
            pstmt2 = con.prepareStatement (
                      "SELECT player1, player2, player3, player4, player5, id " +
                      "FROM evntsup2b WHERE name = ? AND courseName = ? AND c_date <= ? AND c_time <= ?");

            //
            //  find event entries for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, name);
            pstmt2.setString(2, course);
            pstmt2.setLong(3, adv_date);
            pstmt2.setInt(4, adv_time);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               player1 = rs2.getString(1);
               player2 = rs2.getString(2);
               player3 = rs2.getString(3);
               player4 = rs2.getString(4);
               player5 = rs2.getString(5);
               id = rs2.getInt(6);

               change = 0;                  // init change slot indicator

               //
               //  If player = 'X' then clear it (make available to other players)
               //
               //   If player1 = 'x', then ignore rest as only proshop can put an x in slot 1
               //
               if ((!player1.equalsIgnoreCase("x")) && (!player1.equals( "" ))) {

                  if (player2.equalsIgnoreCase("x")) {

                     player2 = "";
                     change = 1;
                  }

                  if (player3.equalsIgnoreCase("x")) {

                     player3 = "";
                     change = 1;
                  }

                  if (player4.equalsIgnoreCase("x")) {

                     player4 = "";
                     change = 1;
                  }

                  if (player5.equalsIgnoreCase("x")) {

                     player5 = "";
                     change = 1;
                  }
               }

               if (change == 1) {

                  pstmt3 = con.prepareStatement (
                      "UPDATE evntsup2b SET player2 = ?, player3 = ?, player4 = ?, player5 = ? " +
                      "WHERE name = ? AND courseName = ? AND id = ?");

                  //
                  //  execute the prepared statement to update the tee time slot
                  //
                  pstmt3.clearParameters();        // clear the parms
                  pstmt3.setString(1, player2);
                  pstmt3.setString(2, player3);
                  pstmt3.setString(3, player4);
                  pstmt3.setString(4, player5);
                  pstmt3.setString(5, name);
                  pstmt3.setString(6, course);
                  pstmt3.setInt(7, id);
                  count = pstmt3.executeUpdate();

                  pstmt3.close();
               }
            }      // end of while to remove X's

            pstmt2.close();

            //
            //  Send email warnings 24 hrs in advance (X's will be removed)
            //
            cal.add(Calendar.HOUR_OF_DAY,24);             // roll ahead another 24 hours (rest should adjust)

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);
            hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value
            min = cal.get(Calendar.MINUTE);

            month = month + 1;                            // month starts at zero

            adv_date = year * 10000;                      // create a date field of yyyymmdd
            adv_date = adv_date + (month * 100);
            adv_date = adv_date + day;                    // date = yyyymmdd (for comparisons)

            adv_time = hr * 100;                          // create time field of hhmm
            adv_time2 = adv_time + 59;

            //
            //  Get today's date and then go up by 24 hours (when x's will be removed)
            //
            cal = new GregorianCalendar();       // get todays date

            cal.add(Calendar.HOUR_OF_DAY,24);    // roll ahead 24 hours (rest should adjust)

            eyear = cal.get(Calendar.YEAR);
            emonth = cal.get(Calendar.MONTH) +1;
            eday = cal.get(Calendar.DAY_OF_MONTH);

            //
            //  Check for 'X' players within 'xhrs' + 24 hours from now for this event
            //
            pstmt2 = con.prepareStatement (
                "SELECT player1, player2, player3, player4, player5, " +
                "username1, username2, username3, username4, username5, id " +
                "FROM evntsup2b WHERE name = ? AND courseName = ? AND c_date = ? AND c_time >= ? AND c_time <= ?");

            //
            //  find tee slots for the specified date and time periods
            //
            pstmt2.clearParameters();        // clear the parms
            pstmt2.setString(1, name);
            pstmt2.setString(2, course);
            pstmt2.setLong(3, adv_date);
            pstmt2.setInt(4, adv_time);
            pstmt2.setInt(5, adv_time2);
            rs2 = pstmt2.executeQuery();

            while (rs2.next()) {

               player1 = rs2.getString(1);
               player2 = rs2.getString(2);
               player3 = rs2.getString(3);
               player4 = rs2.getString(4);
               player5 = rs2.getString(5);
               username1 = rs2.getString(6);
               username2 = rs2.getString(7);
               username3 = rs2.getString(8);
               username4 = rs2.getString(9);
               username5 = rs2.getString(10);
               id = rs2.getInt(11);

               //
               //  if any X's in the tee time, then send email to all players with email enabled
               //
               if (player1.equalsIgnoreCase( "x" ) || player2.equalsIgnoreCase( "x" ) ||
                   player3.equalsIgnoreCase( "x" ) || player4.equalsIgnoreCase( "x" ) ||
                   player5.equalsIgnoreCase( "x" )) {

                  Properties properties = new Properties();
                  properties.put("mail.smtp.host", host);                      // set outbound host address
                  properties.put("mail.smtp.port", port);                      // set outbound port
                  properties.put("mail.smtp.auth", "true");              // set 'use authentication'

                  Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

                  MimeMessage message = new MimeMessage(mailSess);
                  message.setFrom(new InternetAddress(efrom));                  // set from addr
                  message.setSentDate(new java.util.Date());                   // set date/time sent

                  subject = "Event Registration Warning";

                  if (!clubName.equals( "" )) {

                     subject = subject + " - " + clubName;
                  }

                  message.setSubject( subject );                      // set subject line

                  emsg = emsg + emonth + "/" + eday + "/" + eyear + " at approximately " + hr + ":00.\n" +
                                "If you do not fill these positions before that time, the positions will be " +
                                "made available to other members.\n\n";

                  send = 0;                                                    // init

                  if (!username1.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username1);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username2.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username2);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username3.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username3);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username4.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username4);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (!username5.equals( "" )) {

                     pstmte1 = con.prepareStatement (
                              "SELECT email, emailOpt FROM member2b WHERE username = ?");

                     pstmte1.clearParameters();
                     pstmte1.setString(1, username5);
                     rs3 = pstmte1.executeQuery();       // get member's email info

                     if (rs3.next()) {

                        to = rs3.getString(1);           // user's email address
                        emailOpt = rs3.getInt(2);        // email option

                        if ((emailOpt != 0) && (!to.equals( "" ))) {    // if user wants email notifications

                           message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

                           send = 1;                     // send email
                        }
                     }
                     pstmte1.close();              // close the stmt

                  }

                  if (send != 0) {                 // shuld we send an email ?

                     //
                     //  Create the message content
                     //
                     if (act_min < 10) {

                        act_time = act_hr + ":0" + act_min + " " + act_ampm;

                     } else {

                        act_time = act_hr + ":" + act_min + " " + act_ampm;
                     }

                     String enewMsg = header + emsg + " Event: " + name + " " +
                                   " Date: " + e_month + "/" + e_day + "/" + e_year + " at " +
                                   " " + act_time;

                     if (!course.equals( "" )) {

                        enewMsg = enewMsg + "on Course: " + course;
                     }

                     enewMsg = enewMsg + "\n";

                     if (!player1.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 1: " + player1;
                     }
                     if (!player2.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 2: " + player2;
                     }
                     if (!player3.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 3: " + player3;
                     }
                     if (!player4.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 4: " + player4;
                     }
                     if (!player5.equals( "" )) {

                        enewMsg = enewMsg + "\nPlayer 5: " + player5;
                     }

                     enewMsg = enewMsg + trailer;

                     message.setText( enewMsg );  // put msg in email text area

                     Transport.send(message);     // send it!!

                  }    // end of IF send

               }  // end of IF any X's

            }      // end of while to send email warnings

            pstmt2.close();

         }               // end of IF X option
      }                  // end of WHILE rs.next (events)
      pstmte.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it

   }

 }  // end of xEvents


 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 private static Authenticator getAuthenticator() {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }


 //************************************************************************
 // tsheetTimer - Process system timer expiration.  This timer is set
 //               to periodically send an email to each proshop with the
 //               current tee sheets.  For back-up purposes.
 //
 //               Sends sheets at 10:00 PM and 4:00 AM each day.
 //
 //
 //   called by:  TsheetTimer on timer expiration
 //
 //************************************************************************

 public static void tsheetTimer() {


   Connection con1 = null;
   Connection con2 = null;
   Statement stmt1 = null;
   Statement stmt2 = null;
   Statement stmt3 = null;
   ResultSet rs = null;
   ResultSet rs1 = null;
   ResultSet rs2 = null;
   ResultSet rs3 = null;

   int hr = 0;
   int min = 0;
   int year = 0;
   int month = 0;
   int day = 0;
   int fives = 0;
   int hndcp = 0;

   int players = 0;
   int fb = 0;
   int day_num = 0;
   int type = 0;
   int emailOpt = 0;            // email option - send tee sheets via email?
   int shotgun = 1;             // event type = shotgun

   int g1 = 0;                  // guest indicators (1 per player)
   int g2 = 0;
   int g3 = 0;
   int g4 = 0;
   int g5 = 0;
   int p91 = 0;
   int p92 = 0;
   int p93 = 0;
   int p94 = 0;
   int p95 = 0;

   float hndcp1 = 0;
   float hndcp2 = 0;
   float hndcp3 = 0;
   float hndcp4 = 0;
   float hndcp5 = 0;

   short show = 0;
   short show1 = 0;
   short show2 = 0;
   short show3 = 0;
   short show4 = 0;
   short show5 = 0;
   String event = "";
   String ecolor = "";
   String rest = "";
   String rcolor = "";
   String player = "";
   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String p1 = "";
   String p2 = "";
   String p3 = "";
   String p4 = "";
   String p5 = "";
   String p1cw = "";
   String p2cw = "";
   String p3cw = "";
   String p4cw = "";
   String p5cw = "";
   String ampm = "";
   String bgcolor = "";
   String sfb = "";
   String blocker = "";

   String day_name = "";

   String eMsg = "";        // email message text (html page)
   String email = "";
   String email2 = "";
   String support = "backup@foretees.com";                      // email account for backup tee sheets
   String course = "";

   String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub();          // allocate a parm block

   int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!


   //
   //  This must be the master server!!!  If not, let the timer run in case master goes down.
   //
   if (server_id == server_master) {

      //
      //  Perform timer function for each club in the system database 'clubs' table
      //
      String club = rev;          // get db name for 'clubs' table

      try {
         con1 = dbConn.Connect(club);                           // get a connection
      }
      catch (Exception e1) {

         return;
      }

      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();       // get todays date

      hr = cal.get(Calendar.HOUR_OF_DAY);           // get 24 hr clock value

      if (hr > 21) {                                // if current server time is 10:00 PM

         cal.add(Calendar.DATE,1);                  // roll ahead one day
      }

      year = cal.get(Calendar.YEAR);
      month = cal.get(Calendar.MONTH);
      day = cal.get(Calendar.DAY_OF_MONTH);
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      month = month + 1;                            // month starts at zero

      day_name = day_table[day_num];                // get name for day

      long date = year * 10000;                     // create a date field of yyyymmdd
      date = date + (month * 100);
      date = date + day;                            // date = yyyymmdd (for comparisons)


      //
      // Get the club names from the 'clubs' table
      //
      //  Process each club in the table
      //
      try {

         stmt1 = con1.createStatement();        // create a statement

         rs1 = stmt1.executeQuery("SELECT clubname FROM clubs");

         while (rs1.next()) {

            club = rs1.getString(1);        // get a club name

            players = 0;                    // init number of players on tee sheets

            //
            //   Process each club - get guest names and email address
            //
            con2 = dbConn.Connect(club);                                 // get a connection to this club's db

            //
            //   Get the club parms for this club
            //
            getClub.getParms(con2, parm);

            email = parm.email;
            emailOpt = parm.emailOpt;

            //
            //   Remove any guest types that are null - for tests below
            //
            int i = 0;
            while (i < parm.MAX_Guests) {

               if (parm.guest[i].equals( "" )) {

                  parm.guest[i] = "$@#!^&*";      // make so it won't match player name
               }
               i++;
            }         // end of while loop

            //
            //  if email address was specified and pro wants emails, build today's tee sheet and email it to the club
            //
            if ((!email.equals( "" )) && (emailOpt != 0)) {

               String to = "";                          // to address

               Properties properties = new Properties();
               properties.put("mail.smtp.host", host);                // set outbound host address
               properties.put("mail.smtp.port", port);                      // set outbound port
               properties.put("mail.smtp.auth", "true");              // set 'use authentication'

               Session mailSess = Session.getInstance(properties, getAuthenticator()); // get session properties

               MimeMessage message = new MimeMessage(mailSess);
               message.setFrom(new InternetAddress(efrom));                  // set 'from' addr
               message.setSentDate(new java.util.Date());                   // set date/time sent

               message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));  // 'to' address

               if (club.equals( "stalbans" )) {        // 2 recipients for CC of St. Albans

                  email2 = "cthompson@ccstalbans.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }

               if (club.equals( "inverness" )) {        // 2 recipients for Inverness

                  email2 = "davesteinmetz@destinationhotels.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }

               if (club.equals( "missionviejo" )) {        // multiple recipients for Mission Viejo

                  email2 = "armando@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "steve@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "chef@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "cheryl@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "frontdesk@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "lorraine@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "rbeymer@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "khutchins@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "shannon@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "mcerciello@missionviejocc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }

               if (club.equals( "castlepines" )) {        // 6 recipients for CC at Castle Pines

                  email2 = "jogden@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "dpuleo@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "cmurphy@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "bnishi@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "tsnell@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "skmccue@msn.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "astookesberry@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "bpollock@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "dmatlock@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "mlaboda@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "bparker@ccofcastlepines.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }

               if (club.equals( "lakewoodranch" )) { 
               
                  email2 = "brian.branch@lakewoodranchgolf.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
                  
                  email2 = "aaron.dressel@lakewoodranchgolf.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
                  
                  email2 = "eddie.rodriguez@lakewoodranchgolf.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }
               
               if (club.equals( "martindowns" )) {

                  email2 = "cjunk@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "bmcintosh@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "ctregelles@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "edgifford@bellsouth.net";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "khornbeck@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "jbondeson@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "battmasters@yahoo.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address

                  email2 = "cowen@martindownscc.com";

                  message.addRecipient(Message.RecipientType.TO, new InternetAddress(email2));  // 'to' address
               }

               //
               //  copy Support - comment this out to disable
               //
               message.addRecipient(Message.RecipientType.BCC, new InternetAddress(support));  // 'bcc' address

               message.setSubject("ForeTees Backup Tee Sheet");             // set subject line

               //
               //  Get parms for each course
               //
               stmt3 = con2.createStatement();                       // create a statement

               rs3 = stmt3.executeQuery("SELECT courseName, fives FROM clubparm2");   // get the email address

               eMsg = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=us-ascii\">" +
                  "</head><body><center>";

               while (rs3.next()) {

                  course = rs3.getString(1);
                  fives = rs3.getInt(2);

                  eMsg = eMsg + "<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">" +
                  "Date:&nbsp;&nbsp;<b>" + day_name + "&nbsp;&nbsp;" + month + "/" + day + "/" + year + "</b></font>";
                  if (!course.equals( "" )) {
                     eMsg = eMsg + "&nbsp;&nbsp;&nbsp;&nbsp;Course:&nbsp;&nbsp;<b>" + course + "</b>";
                  }
                  eMsg = eMsg + "<table border=\"1\" bgcolor=\"#FFFFCC\" width=\"85%\">" +
                  "<tr bgcolor=\"#CC9966\"><td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>Time</b></u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>F/B</b></u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>Player 1</b></u> " +
                        "</font><font size=\"1\"><u>hndcp</u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>C/W</b></u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>Player 2</b></u> " +
                        "</font><font size=\"1\"><u>hndcp</u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>C/W</b></u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>Player 3</b></u> " +
                        "</font><font size=\"1\"><u>hndcp</u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>C/W</b></u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>Player 4</b></u> " +
                        "</font><font size=\"1\"><u>hndcp</u>" +
                        "</font></td>" +

                     "<td align=\"center\">" +
                        "<font size=\"1\">" +
                        "<u><b>C/W</b></u>" +
                        "</font></td>";

                     if (fives != 0 ) {

                        eMsg = eMsg + "<td align=\"center\">" +
                           "<font size=\"1\">" +
                           "<u><b>Player 5</b></u> " +
                           "</font><font size=\"1\"><u>hndcp</u>" +
                           "</font></td>" +

                        "<td align=\"center\">" +
                           "<font size=\"1\">" +
                           "<u><b>C/W</b></u>" +
                           "</font></td>";
                     }
                     eMsg = eMsg + "</tr>";

                  //
                  //  Get all tee sheet entries for this club
                  //
                  PreparedStatement pstmt = con2.prepareStatement (
                     "SELECT hr, min, time, event, event_color, restriction, rest_color, player1, player2, " +
                     "player3, player4, p1cw, p2cw, p3cw, p4cw, in_use, event_type, hndcp1, hndcp2, hndcp3, hndcp4, " +
                     "show1, show2, show3, show4, fb, player5, p5cw, hndcp5, show5, blocker, " +
                     "rest5, rest5_color, p91, p92, p93, p94, p95 " +
                     "FROM teecurr2 WHERE date = ? AND courseName = ? ORDER BY time, fb");

                  pstmt.clearParameters();        // clear the parms
                  pstmt.setLong(1, date);         // put the parm in pstmt
                  pstmt.setString(2, course);
                  rs = pstmt.executeQuery();      // execute the prepared stmt

                  while (rs.next()) {

                     hr = rs.getInt(1);
                     min = rs.getInt(2);
                     event = rs.getString(4);
                     ecolor = rs.getString(5);
                     rest = rs.getString(6);
                     rcolor = rs.getString(7);
                     player1 = rs.getString(8);
                     player2 = rs.getString(9);
                     player3 = rs.getString(10);
                     player4 = rs.getString(11);
                     p1cw = rs.getString(12);
                     p2cw = rs.getString(13);
                     p3cw = rs.getString(14);
                     p4cw = rs.getString(15);
                     type = rs.getInt(17);
                     hndcp1 = rs.getFloat(18);
                     hndcp2 = rs.getFloat(19);
                     hndcp3 = rs.getFloat(20);
                     hndcp4 = rs.getFloat(21);
                     show1 = rs.getShort(22);
                     show2 = rs.getShort(23);
                     show3 = rs.getShort(24);
                     show4 = rs.getShort(25);
                     fb = rs.getShort(26);
                     player5 = rs.getString(27);
                     p5cw = rs.getString(28);
                     hndcp5 = rs.getFloat(29);
                     show5 = rs.getShort(30);
                     blocker = rs.getString("blocker");
                     p91 = rs.getInt("p91");
                     p92 = rs.getInt("p92");
                     p93 = rs.getInt("p93");
                     p94 = rs.getInt("p94");
                     p95 = rs.getInt("p95");

                     if (blocker.equals( "" )) {    // continue if tee time not blocked - else skip

                        ampm = " AM";
                        if (hr == 12) {
                           ampm = " PM";
                        }
                        if (hr > 12) {
                           ampm = " PM";
                           hr = hr - 12;    // convert to conventional time
                        }

                        bgcolor = "#FFFFCC";               //default

                        if (!event.equals("")) {
                           bgcolor = ecolor;
                        } else {

                           if (!rest.equals("")) {
                              bgcolor = rcolor;
                           }
                        }

                        if (bgcolor.equals("Default")) {
                           bgcolor = "#FFFFCC";              //default
                        }

                        if (p91 == 1) {              // if 9 hole round
                           p1cw = p1cw + "9";
                        }
                        if (p92 == 1) {
                           p2cw = p2cw + "9";
                        }
                        if (p93 == 1) {
                           p3cw = p3cw + "9";
                        }
                        if (p94 == 1) {
                           p4cw = p4cw + "9";
                        }
                        if (p95 == 1) {
                           p5cw = p5cw + "9";
                        }

                        if (player1.equals("")) {
                           p1cw = "";
                        }
                        if (player2.equals("")) {
                           p2cw = "";
                        }
                        if (player3.equals("")) {
                           p3cw = "";
                        }
                        if (player4.equals("")) {
                           p4cw = "";
                        }
                        if (player5.equals("")) {
                           p5cw = "";
                        }

                        g1 = 0;     // init guest indicators
                        g2 = 0;
                        g3 = 0;
                        g4 = 0;
                        g5 = 0;

                        //
                        //  Check if any player names are guest names
                        //
                        if (!player1.equals( "" )) {

                           i = 0;
                           ploop1:
                           while (i < parm.MAX_Guests) {

                              if (player1.startsWith( parm.guest[i] )) {
                                 g1 = 1;
                                 break ploop1;
                              }
                              i++;
                           }
                        }
                        if (!player2.equals( "" )) {

                           i = 0;
                           ploop2:
                           while (i < parm.MAX_Guests) {

                              if (player2.startsWith( parm.guest[i] )) {
                                 g2 = 1;
                                 break ploop2;
                              }
                              i++;
                           }
                        }
                        if (!player3.equals( "" )) {

                           i = 0;
                           ploop3:
                           while (i < parm.MAX_Guests) {

                              if (player3.startsWith( parm.guest[i] )) {
                                 g3 = 1;
                                 break ploop3;
                              }
                              i++;
                           }
                        }
                        if (!player4.equals( "" )) {

                           i = 0;
                           ploop4:
                           while (i < parm.MAX_Guests) {

                              if (player4.startsWith( parm.guest[i] )) {
                                 g4 = 1;
                                 break ploop4;
                              }
                              i++;
                           }
                        }
                        if (!player5.equals( "" )) {

                           i = 0;
                           ploop5:
                           while (i < parm.MAX_Guests) {

                              if (player5.startsWith( parm.guest[i] )) {
                                 g5 = 1;
                                 break ploop5;
                              }
                              i++;
                           }
                        }

                        //
                        //  Process the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
                        //
                        sfb = "F";       // default Front 9

                        if (fb == 1) {

                           sfb = "B";
                        }

                        if (fb == 9) {

                           sfb = "O";
                        }

                        if (type == shotgun) {

                           sfb = "S";            // there's an event and its type is 'shotgun'
                        }

                        eMsg = eMsg + "<tr>" +
                        "<td align=\"center\">" +
                        "<font size=\"1\">";

                        if (min < 10) {                                 // if min value is only 1 digit
                           eMsg = eMsg + hr + ":0" + min + ampm;
                        } else {                                        // min value is 2 digits
                           eMsg = eMsg + hr + ":" + min + ampm;
                        }
                        eMsg = eMsg + "</font></td>" +

                        "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           sfb +
                           "</font></td>";

                        if (!player1.equals("")) {

                           players++;                 // bump number of players on tee sheets

                           if (player1.equalsIgnoreCase("x")) {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">" +
                              player1 +
                              "</font></td>";

                           } else {
                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">";

                              p1 = player1;                                       // copy name only

                              if (g1 == 0) {        // if not guest then add hndcp

                                 if ((hndcp1 == 99) || (hndcp1 == -99)) {
                                    p1 = p1 + "  NH";
                                 } else {
                                    if (hndcp1 <= 0) {
                                       hndcp1 = 0 - hndcp1;                          // convert to non-negative
                                       hndcp = Math.round(hndcp1);                   // round it off
                                       p1 = (p1 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp1);                   // round it off
                                       p1 = (p1 + "  +" + hndcp);
                                    }
                                 }
                              }
                              if (show1 == 1) {                         // if player has checked in
                                 eMsg = eMsg + "X   ";
                              }
                              eMsg = eMsg + p1 +"</font></td>";
                           }

                        } else {
                           eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                           "<font size=\"1\">" +
                           "&nbsp;" +
                           "</font></td>";
                        }

                        if ((!player1.equals("")) && (!player1.equalsIgnoreCase( "x" ))) {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           p1cw;
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           "&nbsp;";
                        }
                           eMsg = eMsg + "</font></td>";

                        if (!player2.equals("")) {

                           players++;                 // bump number of players on tee sheets

                           if (player2.equalsIgnoreCase("x")) {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">" +
                              player2 +
                              "</font></td>";

                           } else {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">";

                              p2 = player2;                                       // copy name only

                              if (g2 == 0) {         // if not guest then add ndcp

                                 if ((hndcp2 == 99) || (hndcp2 == -99)) {
                                    p2 = p2 + "  NH";
                                 } else {
                                    if (hndcp2 <= 0) {
                                       hndcp2 = 0 - hndcp2;                          // convert to non-negative
                                       hndcp = Math.round(hndcp2);                   // round it off
                                       p2 = (p2 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp2);                   // round it off
                                       p2 = (p2 + "  +" + hndcp);
                                    }
                                 }
                              }
                              if (show2 == 1) {                         // if player has checked in
                                 eMsg = eMsg + "X   ";
                              }
                              eMsg = eMsg + p2 +"</font></td>";
                           }
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                           "<font size=\"1\">" +
                           "&nbsp;" +
                           "</font></td>" ;
                        }

                        if ((!player2.equals("")) && (!player2.equalsIgnoreCase( "x" ))) {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           p2cw;
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"white\">" +
                           "<font size=\"1\">" +
                           "&nbsp;";
                        }
                        eMsg = eMsg + "</font></td>";

                        if (!player3.equals("")) {

                           players++;                 // bump number of players on tee sheets

                           if (player3.equalsIgnoreCase("x")) {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">" +
                              player3 +
                              "</font></td>";

                           } else {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">";

                              p3 = player3;                                       // copy name only

                              if (g3 == 0) {         // if not guest then add hndcp

                                 if ((hndcp3 == 99) || (hndcp3 == -99)) {
                                    p3 = p3 + "  NH";
                                 } else {
                                    if (hndcp3 <= 0) {
                                       hndcp3 = 0 - hndcp3;                          // convert to non-negative
                                       hndcp = Math.round(hndcp3);                   // round it off
                                       p3 = (p3 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp3);                   // round it off
                                       p3 = (p3 + "  +" + hndcp);
                                    }
                                 }
                              }
                              if (show3 == 1) {                         // if player has checked in
                                 eMsg = eMsg + "X   ";
                              }
                              eMsg = eMsg + p3 +"</font></td>";
                           }

                        } else {
                           eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                           "<font size=\"1\">" +
                           "&nbsp;" +
                           "</font></td>";
                        }

                        if ((!player3.equals("")) && (!player3.equalsIgnoreCase( "x" ))) {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           p3cw;
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"white\">" +
                           "<font size=\"1\">" +
                           "&nbsp;";
                        }
                           eMsg = eMsg + "</font></td>";

                        if (!player4.equals("")) {

                           players++;                 // bump number of players on tee sheets

                           if (player4.equalsIgnoreCase("x")) {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">" +
                              player4 +
                              "</font></td>";

                           } else {

                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                              "<font size=\"1\">";

                              p4 = player4;                                       // copy name only

                              if (g4 == 0) {         // if not guest then add hndcp

                                 if ((hndcp4 == 99) || (hndcp4 == -99)) {
                                    p4 = p4 + "  NH";
                                 } else {
                                    if (hndcp4 <= 0) {
                                       hndcp4 = 0 - hndcp4;                          // convert to non-negative
                                       hndcp = Math.round(hndcp4);                   // round it off
                                       p4 = (p4 + "  " + hndcp);
                                    } else {
                                       hndcp = Math.round(hndcp4);                   // round it off
                                       p4 = (p4 + "  +" + hndcp);
                                    }
                                 }
                              }
                              if (show4 == 1) {                         // if player has checked in
                                 eMsg = eMsg + "X   ";
                              }
                              eMsg = eMsg + p4 +"</font></td>";
                           }
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                           "<font size=\"1\">" +
                           "&nbsp;" +
                           "</font></td>";
                        }

                        if ((!player4.equals("")) && (!player4.equalsIgnoreCase( "x" ))) {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           p4cw;
                        } else {
                           eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                           "<font size=\"1\">" +
                           "&nbsp;";
                        }
                        eMsg = eMsg + "</font></td>";

                        if (fives != 0) {

                           if (!player5.equals("")) {

                              players++;                 // bump number of players on tee sheets

                              if (player5.equalsIgnoreCase("x")) {

                                 eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                                 "<font size=\"1\">" +
                                 player5 +
                                 "</font></td>";

                              } else {

                                 eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\" align=\"center\">" +
                                 "<font size=\"1\">";

                                 p5 = player5;                                       // copy name only

                                 if (g5 == 0) {

                                    if ((hndcp5 == 99) || (hndcp5 == -99)) {
                                       p5 = p5 + "  NH";
                                    } else {
                                       if (hndcp5 <= 0) {
                                          hndcp5 = 0 - hndcp5;                          // convert to non-negative
                                          hndcp = Math.round(hndcp5);                   // round it off
                                          p5 = (p5 + "  " + hndcp);
                                       } else {
                                          hndcp = Math.round(hndcp5);                   // round it off
                                          p5 = (p5 + "  +" + hndcp);
                                       }
                                    }
                                 }
                                 if (show5 == 1) {                         // if player has checked in
                                    eMsg = eMsg + "X   ";
                                 }
                                 eMsg = eMsg + p5 +"</font></td>";
                              }
                           } else {
                              eMsg = eMsg + "<td bgcolor=\"" + bgcolor + "\">" +
                              "<font size=\"1\">" +
                              "&nbsp;" +
                              "</font></td>";
                           }

                           if ((!player5.equals("")) && (!player5.equalsIgnoreCase( "x" ))) {
                              eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                              "<font size=\"1\">" +
                              p5cw;
                           } else {
                              eMsg = eMsg + "<td bgcolor=\"white\" align=\"center\">" +
                              "<font size=\"1\">" +
                              "&nbsp;";
                           }
                           eMsg = eMsg + "</font></td>";
                        }
                        eMsg = eMsg + "</tr>";

                     }  // end of IF Blocker

                  }  // end of while

                  pstmt.close();


                  eMsg = eMsg + "</td></tr></table></font><br><br><br>";  // end of course table

               }   // end of while more courses

               if (players != 0) {           // do not send sheets if no players on any of them

                  eMsg = eMsg + "</center></body></html>";            // end of html page

                  message.setContent(eMsg, "text/html");             // put msg in email text area
                  message.setHeader("Content-Type", "text/html");    // set html type content

                  Transport.send(message);                           // send it!!
               }
            }                                                     // end of IF email address
            con2.close();         // close the connection to this club

         }      // end of while for all clubs

         stmt1.close();

         con1.close();             // close the connection to the system db

      }
      catch (Exception e2) {
         //
         //  save error message in /v_x/error.txt
         //
         String errorMsg = "Error in SystemUtils.tsheetTimer for club " +club+ ", error: ";
         errorMsg = errorMsg + e2.getMessage();                                 // build error msg

         logError(errorMsg);                                       // log it
      }

   }

   //
   //  reset the Tee Sheet timer for next period
   //
   TsheetTimer t_timer = new TsheetTimer();

 }  // end of tsheetTimer


 //************************************************************************
 //  adjustTime - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
 //
 //   **************** See also adjustTime2 below ****************
 //
 //  Called by: 
 //              Proshop_select
 //              Member_select
 //              moveReqs (and others above)
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static int adjustTime(Connection con, int time) {


   Statement stmt = null;
   ResultSet rs = null;

   int hour = 0;
   int min = 0;
   boolean roll = false;

   String adv_zone = "";

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

   //
   //  get the club's time zone
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT adv_zone " +
                              "FROM club5");

      if (rs.next()) {

         adv_zone = rs.getString(1);
      }
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils adjustTime: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

//      logError(errorMsg);                                       // log it
   }

   if (adv_zone.equals( "Eastern" )) {      // Eastern Time = +1 hr

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled ahead a day
      }
   }

   if (adv_zone.equals( "Mountain" )) {      // Mountain Time = -1 hr

      if (hour == 0) {

         hour = 24;          // change so it can be adjusted below
         roll = true;        // rolled back a day
      }

      hour--;                // adjust the hour value
   }

   if (adv_zone.equals( "Pacific" )) {      // Pacific Time = -2 hrs

      if (hour == 0) {

         hour = 22;          // adjust it
         roll = true;        // rolled back a day

      } else {

         if (hour == 1) {

            hour = 23;          // adjust it
            roll = true;        // rolled back a day

         } else {

            hour = hour - 2;             // adjust the hour value
         }
      }
   }
   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled back one day
   }

   return( time );

 }  // end of adjustTime


 //************************************************************************
 //  adjustTime2 - receives a time value (hhmm) and adjusts it for the club's
 //               specified time zone.
 //
 //  ******* Same as adjustTime above except that it uses 'long' values - for Reports ***********
 //
 //
 //  Called by:
 //              Proshop_reports
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static long adjustTime2(Connection con, long time) {


   Statement stmt = null;
   ResultSet rs = null;

   long hour = 0;
   long min = 0;
   boolean roll = false;

   String adv_zone = "";

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

   //
   //  get the club's time zone
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT adv_zone " +
                              "FROM club5");

      if (rs.next()) {

         adv_zone = rs.getString(1);
      }
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils adjustTime: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

//      logError(errorMsg);                                       // log it
   }

   if (adv_zone.equals( "Eastern" )) {      // Eastern Time = +1 hr

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled ahead a day
      }
   }

   if (adv_zone.equals( "Mountain" )) {      // Mountain Time = -1 hr

      if (hour == 0) {

         hour = 24;          // change so it can be adjusted below
         roll = true;        // rolled back a day
      }

      hour--;                // adjust the hour value
   }

   if (adv_zone.equals( "Pacific" )) {      // Pacific Time = -2 hrs

      if (hour == 0) {

         hour = 22;          // adjust it
         roll = true;        // rolled back a day

      } else {

         if (hour == 1) {

            hour = 23;          // adjust it
            roll = true;        // rolled back a day

         } else {

            hour = hour - 2;             // adjust the hour value
         }
      }
   }
   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled back one day
   }

   return( time );

 }  // end of adjustTime2


 //************************************************************************
 //  adjustTimeBack - receives a time value (hhmm) and adjusts it for the club's
 //                   specified time zone.  This adjusts in the opposite 
 //                   direction as adjustTime above.  
 //
 //                   This is used to adjust a time value to be used to process
 //                   an event at the correct time in the future.
 //
 //
 //  Called by:  Proshop_lott
 //              Member_lott
 //
 //
 //   returns: time (hhmm) - negative value if it rolled back or ahead a day
 //
 //************************************************************************

 public static int adjustTimeBack(Connection con, int time) {


   Statement stmt = null;
   ResultSet rs = null;

   int hour = 0;
   int min = 0;
   boolean roll = false;

   String adv_zone = "";

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

   //
   //  get the club's time zone
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT adv_zone " +
                              "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         adv_zone = rs.getString(1);
      }
      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils adjustTimeBack: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

   if (adv_zone.equals( "Eastern" )) {      // Eastern Time = -1 hr

      if (hour == 0) {

         hour = 24;          // change so it can be adjusted below
         roll = true;        // rolled back a day
      }

      hour--;                // adjust the hour value
   }

   if (adv_zone.equals( "Mountain" )) {      // Mountain Time = +1 hr

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled ahead a day
      }
   }

   if (adv_zone.equals( "Pacific" )) {      // Pacific Time = +2 hrs

      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled back a day
      } 
      hour++;                // adjust the hour value

      if (hour == 24) {

         hour = 0;           // keep from 0 to 23
         roll = true;        // rolled back a day
      }

   }
   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled back one day
   }

   return( time );

 }  // end of adjustTimeBack


 //************************************************************************
 //  newGuest - Correct the stats after the Guest Types were changed.
 //             This is done when the names or the order has changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newGuest(Connection con, parmClub parm)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;

   int [] gr9 = new int [parm.MAX_Guests];       // current stats staging area
   int [] gr18 = new int [parm.MAX_Guests];

   int [] ngr9 = new int [parm.MAX_Guests];       // new stats staging area
   int [] ngr18 = new int [parm.MAX_Guests];

   int other9 = 0;
   int other18 = 0;

   int i = 0;
   int i2 = 0;
   int found = 0;

   long date = 0;
   String course = "";


   //
   //  Gather the stats one day at a time and check for changed guest types
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * " +
                             "FROM stats5 WHERE date != 0");

      while (rs.next()) {

         date = rs.getLong("date");
         course = rs.getString("course");
         gr9[0] = rs.getInt("gst1Rounds9");
         gr18[0] = rs.getInt("gst1Rounds18");
         gr9[1] = rs.getInt("gst2Rounds9");
         gr18[1] = rs.getInt("gst2Rounds18");
         gr9[2] = rs.getInt("gst3Rounds9");
         gr18[2] = rs.getInt("gst3Rounds18");
         gr9[3] = rs.getInt("gst4Rounds9");
         gr18[3] = rs.getInt("gst4Rounds18");
         gr9[4] = rs.getInt("gst5Rounds9");
         gr18[4] = rs.getInt("gst5Rounds18");
         gr9[5] = rs.getInt("gst6Rounds9");
         gr18[5] = rs.getInt("gst6Rounds18");
         gr9[6] = rs.getInt("gst7Rounds9");
         gr18[6] = rs.getInt("gst7Rounds18");
         gr9[7] = rs.getInt("gst8Rounds9");
         gr18[7] = rs.getInt("gst8Rounds18");
         other9 = rs.getInt("otherRounds9");         // get other rounds too
         other18 = rs.getInt("otherRounds18");
         gr9[8] = rs.getInt("gst9Rounds9");
         gr18[8] = rs.getInt("gst9Rounds18");
         gr9[9] = rs.getInt("gst10Rounds9");
         gr18[9] = rs.getInt("gst10Rounds18");
         gr9[10] = rs.getInt("gst11Rounds9");
         gr18[10] = rs.getInt("gst11Rounds18");
         gr9[11] = rs.getInt("gst12Rounds9");
         gr18[11] = rs.getInt("gst12Rounds18");
         gr9[12] = rs.getInt("gst13Rounds9");
         gr18[12] = rs.getInt("gst13Rounds18");
         gr9[13] = rs.getInt("gst14Rounds9");
         gr18[13] = rs.getInt("gst14Rounds18");
         gr9[14] = rs.getInt("gst15Rounds9");
         gr18[14] = rs.getInt("gst15Rounds18");
         gr9[15] = rs.getInt("gst16Rounds9");
         gr18[15] = rs.getInt("gst16Rounds18");
         gr9[16] = rs.getInt("gst17Rounds9");
         gr18[16] = rs.getInt("gst17Rounds18");
         gr9[17] = rs.getInt("gst18Rounds9");
         gr18[17] = rs.getInt("gst18Rounds18");
         gr9[18] = rs.getInt("gst19Rounds9");
         gr18[18] = rs.getInt("gst19Rounds18");
         gr9[19] = rs.getInt("gst20Rounds9");
         gr18[19] = rs.getInt("gst20Rounds18");
         gr9[20] = rs.getInt("gst21Rounds9");
         gr18[20] = rs.getInt("gst21Rounds18");
         gr9[21] = rs.getInt("gst22Rounds9");
         gr18[21] = rs.getInt("gst22Rounds18");
         gr9[22] = rs.getInt("gst23Rounds9");
         gr18[22] = rs.getInt("gst23Rounds18");
         gr9[23] = rs.getInt("gst24Rounds9");
         gr18[23] = rs.getInt("gst24Rounds18");
         gr9[24] = rs.getInt("gst25Rounds9");
         gr18[24] = rs.getInt("gst25Rounds18");
         gr9[25] = rs.getInt("gst26Rounds9");
         gr18[25] = rs.getInt("gst26Rounds18");
         gr9[26] = rs.getInt("gst27Rounds9");
         gr18[26] = rs.getInt("gst27Rounds18");
         gr9[27] = rs.getInt("gst28Rounds9");
         gr18[27] = rs.getInt("gst28Rounds18");
         gr9[28] = rs.getInt("gst29Rounds9");
         gr18[28] = rs.getInt("gst29Rounds18");
         gr9[29] = rs.getInt("gst30Rounds9");
         gr18[29] = rs.getInt("gst30Rounds18");
         gr9[30] = rs.getInt("gst31Rounds9");
         gr18[30] = rs.getInt("gst31Rounds18");
         gr9[31] = rs.getInt("gst32Rounds9");
         gr18[31] = rs.getInt("gst32Rounds18");
         gr9[32] = rs.getInt("gst33Rounds9");
         gr18[32] = rs.getInt("gst33Rounds18");
         gr9[33] = rs.getInt("gst34Rounds9");
         gr18[33] = rs.getInt("gst34Rounds18");
         gr9[34] = rs.getInt("gst35Rounds9");
         gr18[34] = rs.getInt("gst35Rounds18");
         gr9[35] = rs.getInt("gst36Rounds9");
         gr18[35] = rs.getInt("gst36Rounds18");

         //
         //  now check the guest types
         //
         for (i = 0; i < parm.MAX_Guests; i++) {

            if (!parm.newguest[i].equals( parm.guest[i] )) {    // if new not = old

               if (i < 35) {                  // if not at the end

                  for (i2 = i+1; i2 < parm.MAX_Guests; i2++) {   // start looking at the next old one (leave counts = 0 if no match found)

                     if (parm.newguest[i].equals( parm.guest[i2] )) {   // find match in old types ?

                        ngr9[i] = gr9[i2];        // move them up
                        ngr18[i] = gr18[i2];
                        gr9[i2] = 0;              // remove the old values
                        gr18[i2] = 0;

                     }
                  }
                  //
                  //  try to find a match on the old type in this position
                  //
                  found = 0;

                  for (i2 = i+1; i2 < parm.MAX_Guests; i2++) {     // start looking at the next new one

                     if (parm.newguest[i2].equals( parm.guest[i] )) {   // find match in new types ?

                        ngr9[i2] = gr9[i];        // move them down
                        ngr18[i2] = gr18[i];

                        found = 1;
                     }                            // if no match, then this guest type was deleted
                  }
                  //
                  //  if could not find a match for the old guest type, then add its ocunt to 'others'
                  //
                  if (found == 0) {

                     other9 = other9 + gr9[i];        // save counts from deleted guest type
                     other18 = other18 + gr18[i];
                  }
               }

            } else {

               ngr9[i] = gr9[i];      // same guest type - save counts
               ngr18[i] = gr18[i];
            }
         }      // end of FOR loop - check all guest types

         //
         //  Now save the new stats back in db table
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "UPDATE stats5 SET  " +
             "gst1Rounds9= ?, gst1Rounds18= ?, gst2Rounds9= ?, gst2Rounds18= ?, " +
             "gst3Rounds9= ?, gst3Rounds18= ?, gst4Rounds9= ?, gst4Rounds18= ?, " +
             "gst5Rounds9= ?, gst5Rounds18= ?, gst6Rounds9= ?, gst6Rounds18= ?, " +
             "gst7Rounds9= ?, gst7Rounds18= ?, gst8Rounds9= ?, gst8Rounds18= ?, " +
             "otherRounds9 = ?, otherRounds18 = ?, " +
             "gst9Rounds9= ?, gst9Rounds18= ?, gst10Rounds9= ?, gst10Rounds18= ?, " +
             "gst11Rounds9= ?, gst11Rounds18= ?, gst12Rounds9= ?, gst12Rounds18= ?, " +
             "gst13Rounds9= ?, gst13Rounds18= ?, gst14Rounds9= ?, gst14Rounds18= ?, " +
             "gst15Rounds9= ?, gst15Rounds18= ?, gst16Rounds9= ?, gst16Rounds18= ?, " +
             "gst17Rounds9= ?, gst17Rounds18= ?, gst18Rounds9= ?, gst18Rounds18= ?, " +
             "gst19Rounds9= ?, gst19Rounds18= ?, gst20Rounds9= ?, gst20Rounds18= ?, " +
             "gst21Rounds9= ?, gst21Rounds18= ?, gst22Rounds9= ?, gst22Rounds18= ?, " +
             "gst23Rounds9= ?, gst23Rounds18= ?, gst24Rounds9= ?, gst24Rounds18= ?, " +
             "gst25Rounds9= ?, gst25Rounds18= ?, gst26Rounds9= ?, gst26Rounds18= ?, " +
             "gst27Rounds9= ?, gst27Rounds18= ?, gst28Rounds9= ?, gst28Rounds18= ?, " +
             "gst29Rounds9= ?, gst29Rounds18= ?, gst30Rounds9= ?, gst30Rounds18= ?, " +
             "gst31Rounds9= ?, gst31Rounds18= ?, gst32Rounds9= ?, gst32Rounds18= ?, " +
             "gst33Rounds9= ?, gst33Rounds18= ?, gst34Rounds9= ?, gst34Rounds18= ?, " +
             "gst35Rounds9= ?, gst35Rounds18= ?, gst36Rounds9= ?, gst36Rounds18 = ? " +
             "WHERE date = ? AND course = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1,ngr9[0]);          // set the counts for the new guest type order
         pstmt2.setInt(2,ngr18[0]);
         pstmt2.setInt(3,ngr9[1]);
         pstmt2.setInt(4,ngr18[1]);
         pstmt2.setInt(5,ngr9[2]);
         pstmt2.setInt(6,ngr18[2]);
         pstmt2.setInt(7,ngr9[3]);
         pstmt2.setInt(8,ngr18[3]);
         pstmt2.setInt(9,ngr9[4]);
         pstmt2.setInt(10,ngr18[4]);
         pstmt2.setInt(11,ngr9[5]);
         pstmt2.setInt(12,ngr18[5]);
         pstmt2.setInt(13,ngr9[6]);
         pstmt2.setInt(14,ngr18[6]);
         pstmt2.setInt(15,ngr9[7]);
         pstmt2.setInt(16,ngr18[7]);
         pstmt2.setInt(17,other9);
         pstmt2.setInt(18,other18);
         pstmt2.setInt(19,ngr9[8]);
         pstmt2.setInt(20,ngr18[8]);
         pstmt2.setInt(21,ngr9[9]);
         pstmt2.setInt(22,ngr18[9]);
         pstmt2.setInt(23,ngr9[10]);
         pstmt2.setInt(24,ngr18[10]);
         pstmt2.setInt(25,ngr9[11]);
         pstmt2.setInt(26,ngr18[11]);
         pstmt2.setInt(27,ngr9[12]);
         pstmt2.setInt(28,ngr18[12]);
         pstmt2.setInt(29,ngr9[13]);
         pstmt2.setInt(30,ngr18[13]);
         pstmt2.setInt(31,ngr9[14]);
         pstmt2.setInt(32,ngr18[14]);
         pstmt2.setInt(33,ngr9[15]);
         pstmt2.setInt(34,ngr18[15]);
         pstmt2.setInt(35,ngr9[16]);
         pstmt2.setInt(36,ngr18[16]);
         pstmt2.setInt(37,ngr9[17]);
         pstmt2.setInt(38,ngr18[17]);
         pstmt2.setInt(39,ngr9[18]);
         pstmt2.setInt(40,ngr18[18]);
         pstmt2.setInt(41,ngr9[19]);
         pstmt2.setInt(42,ngr18[19]);
         pstmt2.setInt(43,ngr9[20]);
         pstmt2.setInt(44,ngr18[20]);
         pstmt2.setInt(45,ngr9[21]);
         pstmt2.setInt(46,ngr18[21]);
         pstmt2.setInt(47,ngr9[22]);
         pstmt2.setInt(48,ngr18[22]);
         pstmt2.setInt(49,ngr9[23]);
         pstmt2.setInt(50,ngr18[23]);
         pstmt2.setInt(51,ngr9[24]);
         pstmt2.setInt(52,ngr18[24]);
         pstmt2.setInt(53,ngr9[25]);
         pstmt2.setInt(54,ngr18[25]);
         pstmt2.setInt(55,ngr9[26]);
         pstmt2.setInt(56,ngr18[26]);
         pstmt2.setInt(57,ngr9[27]);
         pstmt2.setInt(58,ngr18[27]);
         pstmt2.setInt(59,ngr9[28]);
         pstmt2.setInt(60,ngr18[28]);
         pstmt2.setInt(61,ngr9[29]);
         pstmt2.setInt(62,ngr18[29]);
         pstmt2.setInt(63,ngr9[30]);
         pstmt2.setInt(64,ngr18[30]);
         pstmt2.setInt(65,ngr9[31]);
         pstmt2.setInt(66,ngr18[31]);
         pstmt2.setInt(67,ngr9[32]);
         pstmt2.setInt(68,ngr18[32]);
         pstmt2.setInt(69,ngr9[33]);
         pstmt2.setInt(70,ngr18[33]);
         pstmt2.setInt(71,ngr9[34]);
         pstmt2.setInt(72,ngr18[34]);
         pstmt2.setInt(73,ngr9[35]);
         pstmt2.setInt(74,ngr18[35]);

         pstmt2.setLong(75, date);
         pstmt2.setString(76, course);
         pstmt2.executeUpdate();          // execute the prepared stmt

         pstmt2.close();

      }             // end of WHILE - do all stats records

      stmt.close();
   }
   catch (Exception e2) {
      //
      //  save error message in /v3/error.txt
      //
      String errorMsg = "Error in SystemUtils newGuest: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of newGuest


 //************************************************************************
 //  newMem - Correct the stats after the Member Types were changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newMem(Connection con, parmClub parm)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;

   int [] mr9 = new int [parm.MAX_Mems];       // current stats staging area
   int [] mr18 = new int [parm.MAX_Mems];

   int [] nmr9 = new int [parm.MAX_Mems];       // new stats staging area
   int [] nmr18 = new int [parm.MAX_Mems];

   int other9 = 0;
   int other18 = 0;

   int found = 0;
   int i = 0;
   int i2 = 0;

   long date = 0;
   String course = "";


   //
   //  Gather the stats one day at a time and check for changed member types
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * " +
                             "FROM stats5 WHERE date != 0");

      while (rs.next()) {

         date = rs.getLong("date");
         course = rs.getString("course");
         mr9[0] = rs.getInt("mem1Rounds9");
         mr18[0] = rs.getInt("mem1Rounds18");
         mr9[1] = rs.getInt("mem2Rounds9");
         mr18[1] = rs.getInt("mem2Rounds18");
         mr9[2] = rs.getInt("mem3Rounds9");
         mr18[2] = rs.getInt("mem3Rounds18");
         mr9[3] = rs.getInt("mem4Rounds9");
         mr18[3] = rs.getInt("mem4Rounds18");
         mr9[4] = rs.getInt("mem5Rounds9");
         mr18[4] = rs.getInt("mem5Rounds18");
         mr9[5] = rs.getInt("mem6Rounds9");
         mr18[5] = rs.getInt("mem6Rounds18");
         mr9[6] = rs.getInt("mem7Rounds9");
         mr18[6] = rs.getInt("mem7Rounds18");
         mr9[7] = rs.getInt("mem8Rounds9");
         mr18[7] = rs.getInt("mem8Rounds18");
         mr9[8] = rs.getInt("mem9Rounds9");
         mr18[8] = rs.getInt("mem9Rounds18");
         mr9[9] = rs.getInt("mem10Rounds9");
         mr18[9] = rs.getInt("mem10Rounds18");
         mr9[10] = rs.getInt("mem11Rounds9");
         mr18[10] = rs.getInt("mem11Rounds18");
         mr9[11] = rs.getInt("mem12Rounds9");
         mr18[11] = rs.getInt("mem12Rounds18");
         mr9[12] = rs.getInt("mem13Rounds9");
         mr18[12] = rs.getInt("mem13Rounds18");
         mr9[13] = rs.getInt("mem14Rounds9");
         mr18[13] = rs.getInt("mem14Rounds18");
         mr9[14] = rs.getInt("mem15Rounds9");
         mr18[14] = rs.getInt("mem15Rounds18");
         mr9[15] = rs.getInt("mem16Rounds9");
         mr18[15] = rs.getInt("mem16Rounds18");
         mr9[16] = rs.getInt("mem17Rounds9");
         mr18[16] = rs.getInt("mem17Rounds18");
         mr9[17] = rs.getInt("mem18Rounds9");
         mr18[17] = rs.getInt("mem18Rounds18");
         mr9[18] = rs.getInt("mem19Rounds9");
         mr18[18] = rs.getInt("mem19Rounds18");
         mr9[19] = rs.getInt("mem20Rounds9");
         mr18[19] = rs.getInt("mem20Rounds18");
         mr9[20] = rs.getInt("mem21Rounds9");
         mr18[20] = rs.getInt("mem21Rounds18");
         mr9[21] = rs.getInt("mem22Rounds9");
         mr18[21] = rs.getInt("mem22Rounds18");
         mr9[22] = rs.getInt("mem23Rounds9");
         mr18[22] = rs.getInt("mem23Rounds18");
         mr9[23] = rs.getInt("mem24Rounds9");
         mr18[23] = rs.getInt("mem24Rounds18");
         other9 = rs.getInt("otherRounds9");         // get other rounds too
         other18 = rs.getInt("otherRounds18");

         //
         //  now check the member types
         //
         for (i = 0; i < parm.MAX_Mems; i++) {
  
            if (!parm.newmem[i].equals( parm.mem[i] )) {    // if new not = old

               floop1:               
               for (i2 = i + 1; i2 < parm.MAX_Mems; i2++) {
                  //
                  //  these are now different - check if it moved (if type is new, then leave counts as 0)
                  //
                  if (parm.newmem[i].equals( parm.mem[i2] )) {

                     nmr9[i] = mr9[i2];          // move them up
                     nmr18[i] = mr18[i2];
                     mr9[i2] = 0;                // remove the old values
                     mr18[i2] = 0;
                     break floop1;               // exit
                  }
               }       // end of i2 FOR loop

               //
               //  Now try to find a match on the old type in this position
               //
               found = 0;
               floop2:
               for (i2 = i + 1; i2 < parm.MAX_Mems; i2++) {

                  if (parm.newmem[i2].equals( parm.mem[i] )) {   // find match in new types ?

                     nmr9[i2] = mr9[i];          // move them down
                     nmr18[i2] = mr18[i];
                     found = 1;
                     break floop2;
                  }
               }

               //
               //  if could not find a match for the old member type, then add its count to 'others'
               //
               if (found == 0) {

                  other9 = other9 + mr9[i];        // save counts from deleted member type
                  other18 = other18 + mr18[i];
               }

            } else {        // mem types match - no change on this one

               nmr9[i] = mr9[i];      // same member type - save counts
               nmr18[i] = mr18[i];

            }   // end of mem type check
         }   // end of FOR loop

         //
         //  Now save the new stats back in db table
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "UPDATE stats5 SET  " +
             "mem1Rounds9= ?, mem1Rounds18= ?, mem2Rounds9= ?, mem2Rounds18= ?, " +
             "mem3Rounds9= ?, mem3Rounds18= ?, mem4Rounds9= ?, mem4Rounds18= ?, " +
             "mem5Rounds9= ?, mem5Rounds18= ?, mem6Rounds9= ?, mem6Rounds18= ?, " +
             "mem7Rounds9= ?, mem7Rounds18= ?, mem8Rounds9= ?, mem8Rounds18= ?, " +
             "mem9Rounds9= ?, mem9Rounds18= ?, mem10Rounds9= ?, mem10Rounds18= ?, " +
             "mem11Rounds9= ?, mem11Rounds18= ?, mem12Rounds9= ?, mem12Rounds18= ?, " +
             "mem13Rounds9= ?, mem13Rounds18= ?, mem14Rounds9= ?, mem14Rounds18= ?, " +
             "mem15Rounds9= ?, mem15Rounds18= ?, mem16Rounds9= ?, mem16Rounds18= ?, " +
             "mem17Rounds9= ?, mem17Rounds18= ?, mem18Rounds9= ?, mem18Rounds18= ?, " +
             "mem19Rounds9= ?, mem19Rounds18= ?, mem20Rounds9= ?, mem20Rounds18= ?, " +
             "mem21Rounds9= ?, mem21Rounds18= ?, mem22Rounds9= ?, mem22Rounds18= ?, " +
             "mem23Rounds9= ?, mem23Rounds18= ?, mem24Rounds9= ?, mem24Rounds18= ?, " +
             "otherRounds9 = ?, otherRounds18 = ? " +
             "WHERE date = ? AND course = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1,nmr9[0]);          // set the counts for the new member type order
         pstmt2.setInt(2,nmr18[0]);
         pstmt2.setInt(3,nmr9[1]);
         pstmt2.setInt(4,nmr18[1]);
         pstmt2.setInt(5,nmr9[2]);
         pstmt2.setInt(6,nmr18[2]);
         pstmt2.setInt(7,nmr9[3]);
         pstmt2.setInt(8,nmr18[3]);
         pstmt2.setInt(9,nmr9[4]);
         pstmt2.setInt(10,nmr18[4]);
         pstmt2.setInt(11,nmr9[5]);
         pstmt2.setInt(12,nmr18[5]);
         pstmt2.setInt(13,nmr9[6]);
         pstmt2.setInt(14,nmr18[6]);
         pstmt2.setInt(15,nmr9[7]);
         pstmt2.setInt(16,nmr18[7]);
         pstmt2.setInt(17,nmr9[8]);
         pstmt2.setInt(18,nmr18[8]);
         pstmt2.setInt(19,nmr9[9]);
         pstmt2.setInt(20,nmr18[9]);
         pstmt2.setInt(21,nmr9[10]);
         pstmt2.setInt(22,nmr18[10]);
         pstmt2.setInt(23,nmr9[11]);
         pstmt2.setInt(24,nmr18[11]);
         pstmt2.setInt(25,nmr9[12]);
         pstmt2.setInt(26,nmr18[12]);
         pstmt2.setInt(27,nmr9[13]);
         pstmt2.setInt(28,nmr18[13]);
         pstmt2.setInt(29,nmr9[14]);
         pstmt2.setInt(30,nmr18[14]);
         pstmt2.setInt(31,nmr9[15]);
         pstmt2.setInt(32,nmr18[15]);
         pstmt2.setInt(33,nmr9[16]);
         pstmt2.setInt(34,nmr18[16]);
         pstmt2.setInt(35,nmr9[17]);
         pstmt2.setInt(36,nmr18[17]);
         pstmt2.setInt(37,nmr9[18]);
         pstmt2.setInt(38,nmr18[18]);
         pstmt2.setInt(39,nmr9[19]);
         pstmt2.setInt(40,nmr18[19]);
         pstmt2.setInt(41,nmr9[20]);
         pstmt2.setInt(42,nmr18[20]);
         pstmt2.setInt(43,nmr9[21]);
         pstmt2.setInt(44,nmr18[21]);
         pstmt2.setInt(45,nmr9[22]);
         pstmt2.setInt(46,nmr18[22]);
         pstmt2.setInt(47,nmr9[23]);
         pstmt2.setInt(48,nmr18[23]);
         pstmt2.setInt(49,other9);
         pstmt2.setInt(50,other18);

         pstmt2.setLong(51, date);
         pstmt2.setString(52, course);
         pstmt2.executeUpdate();          // execute the prepared stmt

         pstmt2.close();
      }             // end of WHILE - do all stats records

      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v3/error.txt
      //
      String errorMsg = "Error in SystemUtils newMem: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of newMem


 //************************************************************************
 //  newTmode - Correct the stats after a Mode of Transportation was changed.
 //
 //  Called by:  Proshop_parm
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newTmode(Connection con, parmClub parm, String course) {


   Statement stmt = null;
   ResultSet rs = null;


   int [] tr9 = new int [parm.MAX_Tmodes];       // current stats staging area
   int [] tr18 = new int [parm.MAX_Tmodes];

   int [] ntr9 = new int [parm.MAX_Tmodes];       // new stats staging area
   int [] ntr18 = new int [parm.MAX_Tmodes];

   int tmodeOldR9 = 0;
   int tmodeOldR18 = 0;

   int found = 0;
   int i = 0;
   int i2 = 0;

   long date = 0;


   //
   //  Gather the stats one day at a time and check for changed modes of trans
   //
   try {

      PreparedStatement pstmt1 = con.prepareStatement (
         "SELECT * " +
         "FROM stats5 WHERE course = ?");

      pstmt1.clearParameters();
      pstmt1.setString(1, course);

      rs = pstmt1.executeQuery();          // execute the prepared stmt

      while (rs.next()) {

         date = rs.getLong("date");
         tr9[0] = rs.getInt("tmode1R9");
         tr18[0] = rs.getInt("tmode1R18");
         tr9[1] = rs.getInt("tmode2R9");
         tr18[1] = rs.getInt("tmode2R18");
         tr9[2] = rs.getInt("tmode3R9");
         tr18[2] = rs.getInt("tmode3R18");
         tr9[3] = rs.getInt("tmode4R9");
         tr18[3] = rs.getInt("tmode4R18");
         tr9[4] = rs.getInt("tmode5R9");
         tr18[4] = rs.getInt("tmode5R18");
         tr9[5] = rs.getInt("tmode6R9");
         tr18[5] = rs.getInt("tmode6R18");
         tr9[6] = rs.getInt("tmode7R9");
         tr18[6] = rs.getInt("tmode7R18");
         tr9[7] = rs.getInt("tmode8R9");
         tr18[7] = rs.getInt("tmode8R18");
         tr9[8] = rs.getInt("tmode9R9");
         tr18[8] = rs.getInt("tmode9R18");
         tr9[9] = rs.getInt("tmode10R9");
         tr18[9] = rs.getInt("tmode10R18");
         tr9[10] = rs.getInt("tmode11R9");
         tr18[10] = rs.getInt("tmode11R18");
         tr9[11] = rs.getInt("tmode12R9");
         tr18[11] = rs.getInt("tmode12R18");
         tr9[12] = rs.getInt("tmode13R9");
         tr18[12] = rs.getInt("tmode13R18");
         tr9[13] = rs.getInt("tmode14R9");
         tr18[13] = rs.getInt("tmode14R18");
         tr9[14] = rs.getInt("tmode15R9");
         tr18[14] = rs.getInt("tmode15R18");
         tr9[15] = rs.getInt("tmode16R9");
         tr18[15] = rs.getInt("tmode16R18");
         tmodeOldR9 = rs.getInt("tmodeOldR9");         // get other old rounds too
         tmodeOldR18 = rs.getInt("tmodeOldR18");

         //
         //  now check the modes of trans
         //
         for (i = 0; i < parm.MAX_Tmodes; i++) {

            if (!parm.tmodeNew[i].equals( parm.tmode[i] )) {    // if new not = old

               if (i < 15) {                  // if not at the end

                  for (i2 = i+1; i2 < parm.MAX_Tmodes; i2++) {   // start looking at the next old one (leave counts = 0 if no match found)

                     if (parm.tmodeNew[i].equals( parm.tmode[i2] )) {   // find match in old types ?

                        ntr9[i] = tr9[i2];        // move them up
                        ntr18[i] = tr18[i2];
                        tr9[i2] = 0;              // remove the old values
                        tr18[i2] = 0;

                     }
                  }
                  //
                  //  try to find a match on the old type in this position
                  //
                  found = 0;

                  for (i2 = i+1; i2 < parm.MAX_Tmodes; i2++) {     // start looking at the next new one

                     if (parm.tmodeNew[i2].equals( parm.tmode[i] )) {   // find match in new types ?

                        ntr9[i2] = tr9[i];        // move them down
                        ntr18[i2] = tr18[i];

                        found = 1;
                     }                            // if no match, then this guest type was deleted
                  }
                  //
                  //  if could not find a match for the old guest type, then add its ocunt to 'tmodeOldRs'
                  //
                  if (found == 0) {

                     tmodeOldR9 = tmodeOldR9 + tr9[i];        // save counts from deleted guest type
                     tmodeOldR18 = tmodeOldR18 + tr18[i];
                  }
               }

            } else {

               ntr9[i] = tr9[i];      // same guest type - save counts
               ntr18[i] = tr18[i];
            }
         }      // end of FOR loop - check all guest types

         //
         //  Now save the new stats back in db table
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "UPDATE stats5 SET  " +
             "tmode1R9= ?, tmode1R18= ?, tmode2R9= ?, tmode2R18= ?, " +
             "tmode3R9= ?, tmode3R18= ?, tmode4R9= ?, tmode4R18= ?, " +
             "tmode5R9= ?, tmode5R18= ?, tmode6R9= ?, tmode6R18= ?, " +
             "tmode7R9= ?, tmode7R18= ?, tmode8R9= ?, tmode8R18= ?, " +
             "tmode9R9= ?, tmode9R18= ?, tmode10R9= ?, tmode10R18= ?, " +
             "tmode11R9= ?, tmode11R18= ?, tmode12R9= ?, tmode12R18= ?, " +
             "tmode13R9= ?, tmode13R18= ?, tmode14R9= ?, tmode14R18= ?, " +
             "tmode15R9= ?, tmode15R18= ?, tmode16R9= ?, tmode16R18= ?, " +
             "tmodeOldR9 = ?, tmodeOldR18 = ? " +
             "WHERE date = ? AND course = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1,ntr9[0]);          // set the counts for the new order
         pstmt2.setInt(2,ntr18[0]);
         pstmt2.setInt(3,ntr9[1]);
         pstmt2.setInt(4,ntr18[1]);
         pstmt2.setInt(5,ntr9[2]);
         pstmt2.setInt(6,ntr18[2]);
         pstmt2.setInt(7,ntr9[3]);
         pstmt2.setInt(8,ntr18[3]);
         pstmt2.setInt(9,ntr9[4]);
         pstmt2.setInt(10,ntr18[4]);
         pstmt2.setInt(11,ntr9[5]);
         pstmt2.setInt(12,ntr18[5]);
         pstmt2.setInt(13,ntr9[6]);
         pstmt2.setInt(14,ntr18[6]);
         pstmt2.setInt(15,ntr9[7]);
         pstmt2.setInt(16,ntr18[7]);
         pstmt2.setInt(17,ntr9[8]);
         pstmt2.setInt(18,ntr18[8]);
         pstmt2.setInt(19,ntr9[9]);
         pstmt2.setInt(20,ntr18[9]);
         pstmt2.setInt(21,ntr9[10]);
         pstmt2.setInt(22,ntr18[10]);
         pstmt2.setInt(23,ntr9[11]);
         pstmt2.setInt(24,ntr18[11]);
         pstmt2.setInt(25,ntr9[12]);
         pstmt2.setInt(26,ntr18[12]);
         pstmt2.setInt(27,ntr9[13]);
         pstmt2.setInt(28,ntr18[13]);
         pstmt2.setInt(29,ntr9[14]);
         pstmt2.setInt(30,ntr18[14]);
         pstmt2.setInt(31,ntr9[15]);
         pstmt2.setInt(32,ntr18[15]);
         pstmt2.setInt(33,tmodeOldR9);
         pstmt2.setInt(34,tmodeOldR18);

         pstmt2.setLong(35, date);
         pstmt2.setString(36, course);
         pstmt2.executeUpdate();          // execute the prepared stmt

         pstmt2.close();

      }             // end of WHILE - do all stats records

      pstmt1.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v3/error.txt
      //
      String errorMsg = "Error in SystemUtils newTmode: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of newTmode


 //************************************************************************
 //  newMship - Correct the stats after the Membership Types were changed.
 //
 //  Called by:  Proshop_club
 //
 //
 //   receives:  a club parm block
 //
 //   returns: void
 //
 //************************************************************************

 public static void newMship(Connection con, parmClub parm)
                          throws Exception {


   Statement stmt = null;
   ResultSet rs = null;

   int [] mr9 = new int [parm.MAX_Mships];       // current stats staging area
   int [] mr18 = new int [parm.MAX_Mships];

   int [] nmr9 = new int [parm.MAX_Mships];       // new stats staging area
   int [] nmr18 = new int [parm.MAX_Mships];

   int other9 = 0;
   int other18 = 0;

   int found = 0;
   int i = 0;
   int i2 = 0;

   long date = 0;
   String course = "";


   //
   //  Gather the stats one day at a time and check for changed membership types
   //
   try {

      stmt = con.createStatement();        // create a statement

      rs = stmt.executeQuery("SELECT * " +
                             "FROM stats5 WHERE date != 0");

      while (rs.next()) {

         date = rs.getLong("date");
         course = rs.getString("course");
         mr9[0] = rs.getInt("mship1Rounds9");
         mr18[0] = rs.getInt("mship1Rounds18");
         mr9[1] = rs.getInt("mship2Rounds9");
         mr18[1] = rs.getInt("mship2Rounds18");
         mr9[2] = rs.getInt("mship3Rounds9");
         mr18[2] = rs.getInt("mship3Rounds18");
         mr9[3] = rs.getInt("mship4Rounds9");
         mr18[3] = rs.getInt("mship4Rounds18");
         mr9[4] = rs.getInt("mship5Rounds9");
         mr18[4] = rs.getInt("mship5Rounds18");
         mr9[5] = rs.getInt("mship6Rounds9");
         mr18[5] = rs.getInt("mship6Rounds18");
         mr9[6] = rs.getInt("mship7Rounds9");
         mr18[6] = rs.getInt("mship7Rounds18");
         mr9[7] = rs.getInt("mship8Rounds9");
         mr18[7] = rs.getInt("mship8Rounds18");
         mr9[8] = rs.getInt("mship9Rounds9");
         mr18[8] = rs.getInt("mship9Rounds18");
         mr9[9] = rs.getInt("mship10Rounds9");
         mr18[9] = rs.getInt("mship10Rounds18");
         mr9[10] = rs.getInt("mship11Rounds9");
         mr18[10] = rs.getInt("mship11Rounds18");
         mr9[11] = rs.getInt("mship12Rounds9");
         mr18[11] = rs.getInt("mship12Rounds18");
         mr9[12] = rs.getInt("mship13Rounds9");
         mr18[12] = rs.getInt("mship13Rounds18");
         mr9[13] = rs.getInt("mship14Rounds9");
         mr18[13] = rs.getInt("mship14Rounds18");
         mr9[14] = rs.getInt("mship15Rounds9");
         mr18[14] = rs.getInt("mship15Rounds18");
         mr9[15] = rs.getInt("mship16Rounds9");
         mr18[15] = rs.getInt("mship16Rounds18");
         mr9[16] = rs.getInt("mship17Rounds9");
         mr18[16] = rs.getInt("mship17Rounds18");
         mr9[17] = rs.getInt("mship18Rounds9");
         mr18[17] = rs.getInt("mship18Rounds18");
         mr9[18] = rs.getInt("mship19Rounds9");
         mr18[18] = rs.getInt("mship19Rounds18");
         mr9[19] = rs.getInt("mship20Rounds9");
         mr18[19] = rs.getInt("mship20Rounds18");
         mr9[20] = rs.getInt("mship21Rounds9");
         mr18[20] = rs.getInt("mship21Rounds18");
         mr9[21] = rs.getInt("mship22Rounds9");
         mr18[21] = rs.getInt("mship22Rounds18");
         mr9[22] = rs.getInt("mship23Rounds9");
         mr18[22] = rs.getInt("mship23Rounds18");
         mr9[23] = rs.getInt("mship24Rounds9");
         mr18[23] = rs.getInt("mship24Rounds18");
         other9 = rs.getInt("otherRounds9");         // get other rounds too
         other18 = rs.getInt("otherRounds18");
           
         //
         //  now check the membership types
         //
         for (i = 0; i < parm.MAX_Mships; i++) {

            if (!parm.newmship[i].equals( parm.mship[i] )) {    // if new not = old

               floop1:
               for (i2 = i + 1; i2 < parm.MAX_Mships; i2++) {
                  //
                  //  these are now different - check if it moved (if type is new, then leave counts as 0)
                  //
                  if (parm.newmship[i].equals( parm.mship[i2] )) {

                     nmr9[i] = mr9[i2];          // move them up
                     nmr18[i] = mr18[i2];
                     mr9[i2] = 0;                // remove the old values
                     mr18[i2] = 0;
                     break floop1;               // exit
                  }
               }       // end of i2 FOR loop

               //
               //  Now try to find a match on the old type in this position
               //
               found = 0;
               floop2:
               for (i2 = i + 1; i2 < parm.MAX_Mships; i2++) {

                  if (parm.newmship[i2].equals( parm.mship[i] )) {   // find match in new types ?

                     nmr9[i2] = mr9[i];          // move them down
                     nmr18[i2] = mr18[i];
                     found = 1;
                     break floop2;
                  }
               }

               //
               //  if could not find a match for the old membership type, then add its count to 'others'
               //
               if (found == 0) {

                  other9 = other9 + mr9[i];        // save counts from deleted membership type
                  other18 = other18 + mr18[i];
               }

            } else {        // memship types match - no change on this one

               nmr9[i] = mr9[i];      // same membersip type - save counts
               nmr18[i] = mr18[i];

            }   // end of memship type check
         }   // end of FOR loop

         //
         //  Now save the new stats back in db table
         //
         PreparedStatement pstmt2 = con.prepareStatement (
            "UPDATE stats5 SET  " +
             "mship1Rounds9= ?, mship1Rounds18= ?, mship2Rounds9= ?, mship2Rounds18= ?, " +
             "mship3Rounds9= ?, mship3Rounds18= ?, mship4Rounds9= ?, mship4Rounds18= ?, " +
             "mship5Rounds9= ?, mship5Rounds18= ?, mship6Rounds9= ?, mship6Rounds18= ?, " +
             "mship7Rounds9= ?, mship7Rounds18= ?, mship8Rounds9= ?, mship8Rounds18= ?, " +
             "mship9Rounds9= ?, mship9Rounds18= ?, mship10Rounds9= ?, mship10Rounds18= ?, " +
             "mship11Rounds9= ?, mship11Rounds18= ?, mship12Rounds9= ?, mship12Rounds18= ?, " +
             "mship13Rounds9= ?, mship13Rounds18= ?, mship14Rounds9= ?, mship14Rounds18= ?, " +
             "mship15Rounds9= ?, mship15Rounds18= ?, mship16Rounds9= ?, mship16Rounds18= ?, " +
             "mship17Rounds9= ?, mship17Rounds18= ?, mship18Rounds9= ?, mship18Rounds18= ?, " +
             "mship19Rounds9= ?, mship19Rounds18= ?, mship20Rounds9= ?, mship20Rounds18= ?, " +
             "mship21Rounds9= ?, mship21Rounds18= ?, mship22Rounds9= ?, mship22Rounds18= ?, " +
             "mship23Rounds9= ?, mship23Rounds18= ?, mship24Rounds9= ?, mship24Rounds18= ?, " +
             "otherRounds9 = ?, otherRounds18 = ? " +
             "WHERE date = ? AND course = ?");

         pstmt2.clearParameters();
         pstmt2.setInt(1,nmr9[0]);          // set the counts for the new member type order
         pstmt2.setInt(2,nmr18[0]);
         pstmt2.setInt(3,nmr9[1]);
         pstmt2.setInt(4,nmr18[1]);
         pstmt2.setInt(5,nmr9[2]);
         pstmt2.setInt(6,nmr18[2]);
         pstmt2.setInt(7,nmr9[3]);
         pstmt2.setInt(8,nmr18[3]);
         pstmt2.setInt(9,nmr9[4]);
         pstmt2.setInt(10,nmr18[4]);
         pstmt2.setInt(11,nmr9[5]);
         pstmt2.setInt(12,nmr18[5]);
         pstmt2.setInt(13,nmr9[6]);
         pstmt2.setInt(14,nmr18[6]);
         pstmt2.setInt(15,nmr9[7]);
         pstmt2.setInt(16,nmr18[7]);
         pstmt2.setInt(17,nmr9[8]);
         pstmt2.setInt(18,nmr18[8]);
         pstmt2.setInt(19,nmr9[9]);
         pstmt2.setInt(20,nmr18[9]);
         pstmt2.setInt(21,nmr9[10]);
         pstmt2.setInt(22,nmr18[10]);
         pstmt2.setInt(23,nmr9[11]);
         pstmt2.setInt(24,nmr18[11]);
         pstmt2.setInt(25,nmr9[12]);
         pstmt2.setInt(26,nmr18[12]);
         pstmt2.setInt(27,nmr9[13]);
         pstmt2.setInt(28,nmr18[13]);
         pstmt2.setInt(29,nmr9[14]);
         pstmt2.setInt(30,nmr18[14]);
         pstmt2.setInt(31,nmr9[15]);
         pstmt2.setInt(32,nmr18[15]);
         pstmt2.setInt(33,nmr9[16]);
         pstmt2.setInt(34,nmr18[16]);
         pstmt2.setInt(35,nmr9[17]);
         pstmt2.setInt(36,nmr18[17]);
         pstmt2.setInt(37,nmr9[18]);
         pstmt2.setInt(38,nmr18[18]);
         pstmt2.setInt(39,nmr9[19]);
         pstmt2.setInt(40,nmr18[19]);
         pstmt2.setInt(41,nmr9[20]);
         pstmt2.setInt(42,nmr18[20]);
         pstmt2.setInt(43,nmr9[21]);
         pstmt2.setInt(44,nmr18[21]);
         pstmt2.setInt(45,nmr9[22]);
         pstmt2.setInt(46,nmr18[22]);
         pstmt2.setInt(47,nmr9[23]);
         pstmt2.setInt(48,nmr18[23]);
         pstmt2.setInt(49,other9);
         pstmt2.setInt(50,other18);

         pstmt2.setLong(51, date);
         pstmt2.setString(52, course);
         pstmt2.executeUpdate();          // execute the prepared stmt

         pstmt2.close();
      }             // end of WHILE - do all stats records

      stmt.close();

   }
   catch (Exception e2) {
      //
      //  save error message in /v3/error.txt
      //
      String errorMsg = "Error in SystemUtils newMship: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of newMship


 //************************************************************************
 //  removeLessonBlockers - removes any old blockers from the Lesson Books
 //
 //       This is done to keep the db table clean and to eliminate some
 //       duplicate name errors when pro is creating new blockers.
 //
 //  called by:  scanTee
 //
 //************************************************************************

 public static void removeLessonBlockers(Connection con, long today_date, String club)
           throws Exception {


   PreparedStatement pstmt2;

   //
   //  ************* today_date has been adjusted for time zone by scanTee *********
   //
   //  Remove all blockers that ended before today
   //
   try {

      pstmt2 = con.prepareStatement (
                   "DELETE FROM lessonblock5 WHERE edate < ?");

      pstmt2.clearParameters();        // clear the parms
      pstmt2.setLong(1, today_date);   // put the parms in stmt for tee slot
      pstmt2.executeUpdate();          // delete all tee slots for oldest date

      pstmt2.close();

   }
   catch (SQLException exc1) {

      throw new Exception("Error removing Old Lesson Blockers - removeLessonBlocker");
   }

 }  // end of removeLessonBlocker


 //************************************************************************
 //  optimize - optimize the club's db tables every Wednesday morning
 //
 //  Called by:  teeTimer (above) when timer expires at 2:30 AM    
 //
 //
 //************************************************************************

 public static void optimize(Connection con) {


   Statement stmt = null;
   ResultSet rs = null;

   boolean b = false;

   //
   //  Get the current day
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int day_name = cal.get(Calendar.DAY_OF_WEEK);
      
   if (day_name == 4) {             // if Wednesday
  
      try {
         //
         //  Optimize the tables
         //
         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE member2b");          // member2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE teecurr2");          // teecurr2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE events2b");          // events2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE evntsup2b");          // evntsup2b

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE restriction2");          // restriction2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE fives2");          // fives2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE block2");          // block2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE guestres2");          // guestres2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE dbltee2");          // dbltee2

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE buddy");          // buddy

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE lottery3");          // lottery3

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE lreqs3");          // lreqs3

         stmt.close();

         stmt = con.createStatement();           // create a statement

         b = stmt.execute("OPTIMIZE TABLE actlott3");          // actlott3

         stmt.close();

      }
      catch (Exception ignore) {
      }
   }
 }  // end of optimize


 //************************************************************************
 // setNoShowOW - Custom Processing for Old Warson CC
 //             
 //   If the 'Pre-CheckIn' feature is enabled then set all players in today's 
 //   tee sheet to 'new' ('2').  They use this for the bag room to know when
 //   bags have been prepared.  the bag room staff will check-in the players
 //   the first time after the player's bag is put out.
 //
 //   called by:  inactTimer 
 //
 //************************************************************************

 public static void setNoShowOW(long date, Connection con) {


   PreparedStatement pstmt = null;
   PreparedStatement pstmt1 = null;
   ResultSet rs = null;


   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
     
   int show1 = 0;
   int show2 = 0;
   int show3 = 0;
   int show4 = 0;
   int show5 = 0;
   int fb = 0;
   int time = 0;
   int precheckin = 0;

   try {
      //
      //  Check if 'Pre-Checkin' feature is enabled
      //
      pstmt = con.prepareStatement (
         "SELECT precheckin FROM club5");

      pstmt.clearParameters();        // clear the parms
      rs = pstmt.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         precheckin = rs.getInt(1);
      }            
      pstmt.close();

      if (precheckin > 0) {
       
         //
         //  Check each tee time for today
         //
         pstmt = con.prepareStatement (
            "SELECT time, player1, player2, player3, player4, fb, player5 FROM teecurr2 " +
            "WHERE date = ? AND (player1 != '' OR player2 != '' OR player3 != '' OR player4 != '' OR player5 != '')");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         rs = pstmt.executeQuery();      // execute the prepared stmt

         while (rs.next()) {

            time = rs.getInt(1);
            player1 = rs.getString(2);
            player2 = rs.getString(3);
            player3 = rs.getString(4);
            player4 = rs.getString(5);
            fb = rs.getInt(6);
            player5 = rs.getString(7);

            show1 = 0;      // init
            show2 = 0;
            show3 = 0;
            show4 = 0;
            show5 = 0;

            //
            //  Check for member or guest
            //
            if (!player1.equals( "" ) && !player1.equalsIgnoreCase( "x" )) {

               show1 = 2;         // set pre-checkin value
            }
            if (!player2.equals( "" ) && !player2.equalsIgnoreCase( "x" )) {

               show2 = 2;         // set pre-checkin value
            }
            if (!player3.equals( "" ) && !player3.equalsIgnoreCase( "x" )) {

               show3 = 2;         // set pre-checkin value
            }
            if (!player4.equals( "" ) && !player4.equalsIgnoreCase( "x" )) {

               show4 = 2;         // set pre-checkin value
            }
            if (!player5.equals( "" ) && !player5.equalsIgnoreCase( "x" )) {

               show5 = 2;         // set pre-checkin value
            }

            if (show1 == 2 || show2 == 2 || show3 == 2 || show4 == 2 || show5 == 2) {

               //
               //  Check each tee time for today
               //
               pstmt1 = con.prepareStatement (
                 "UPDATE teecurr2 SET show1 = ?, show2 = ?, show3 = ?, show4 = ?, show5 = ? " +
                 "WHERE date = ? AND time = ? AND fb = ?");

               pstmt1.clearParameters();        // clear the parms
               pstmt1.setInt(1, show1);
               pstmt1.setInt(2, show2);
               pstmt1.setInt(3, show3);
               pstmt1.setInt(4, show4);
               pstmt1.setInt(5, show5);
               pstmt1.setLong(6, date);
               pstmt1.setInt(7, time);
               pstmt1.setInt(8, fb);

               pstmt1.executeUpdate();     // execute the prepared stmt

               pstmt1.close();
            }
         }
         pstmt.close();
      }

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils setNoShowOW: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of setNoShowOW
                                              
                                              
 //************************************************************************
 // releaseRestNR - Custom Processing for North Ridge CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'WALK IN TIME' so they are not enforced for the current day.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestNR(Connection con) {


   PreparedStatement pstmt = null;


   String name = "WALK IN TIME%";            // beginning of name of restrictions

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,1);                          // get tomorrow's date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                                 // month starts at zero

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestNR: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestNR


 //************************************************************************
 // releaseRestStAlb - Custom Processing for CC of St. Albans
 //
 //      Change the start date of a member restrictions whose name is 
 //      'Member Only Play' so it is not enforced until next week.
 //
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestStAlb(Connection con) {


   PreparedStatement pstmt = null;


   String name = "Member only play";            // name of restriction

   try {
      //
      //  Get date 7 days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,7);                          // get next week
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestStAlb: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestStAlb


 //************************************************************************
 // releaseRestMeadow - Custom Processing for Meadow Springs
 //
 //      Change the start date of a member restrictions whose name is
 //      'Saturday Gangsome' or 'Sunday Gangsome' so it is not enforced until next week.
 //
 //    For 2006 only !!!!!!!!!!!!!!
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestMeadow(Connection con) {


   PreparedStatement pstmt = null;


   String name1 = "Satuday Gangsome";            // name of restriction for Sat
   String name2 = "Sunday Gangsome";             // name of restriction for Sun

   try {
      //
      //  Get date 7 days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,7);                          // get next week
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      if (date > 20061231) {        // if past end of rest period

         date = 20070101;           // do not go too far
      }

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ? OR name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name1);
      pstmt.setString(6, name2);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND (restriction = ? OR restriction = ?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name1);
      pstmt.setString(3, name2);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestMeadow: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestMeadow


 //************************************************************************
 // releaseRestCH1 - Custom Processing for Cherry Hills CC
 //
 //      Change the start date of a member restrictions whose name is
 //      'SPOUSE PRIORITY Open to 11 am' so it is not enforced until
 //      next week.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestCH1(Connection con) {


   PreparedStatement pstmt = null;


   String name = "SPOUSE PRIORITY Open to 11 am";            // name of restriction

   try {
      //
      //  Get date 2 days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,2);                          // get the day after tomorrow
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestCH1: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestCH1


 //************************************************************************
 // releaseRestCH2 - Custom Processing for Cherry Hills CC
 //
 //      Change the start date of a member restrictions whose name is
 //      'SPOUSE PRIORITY Open to 10 am' so it is not enforced until
 //      next week.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestCH2(Connection con) {


   PreparedStatement pstmt = null;


   String name = "SPOUSE PRIORITY Open to 10 am";            // name of restriction

   try {
      //
      //  Get date 2 days from now
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,2);                          // get the day after tomorrow
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestCH2: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestCH2


 //************************************************************************
 // releaseRestMAN - Custom Processing for Manito CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'Alternate Time' so they are not enforced for the current day
 //      and the next day (selected tee times open up 1 day in advance).
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestMAN(Connection con) {


   PreparedStatement pstmt = null;


   String name = "Alternate Time%";            // beginning of name of restrictions

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,2);                          // get the day after tomorrow
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestMAN: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestMAN


 //************************************************************************
 // releaseRestLAKES - Custom Processing for The Lakes CC
 //
 //      Change the start date of a specified Guest Restriction (3 Day Guest Restriction).
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestLAKES(Connection con) {


   PreparedStatement pstmt = null;


   String name = "3 Day Guest Restriction";            // name of restriction

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,4);                          // get the date 4 days from now
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE guestres2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET rest5 = '', rest5_color = '' " +
        "WHERE date < ? AND rest5 = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the guest restriction tables and rebuild teecurr entries if needed
      //
      doFives(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestLAKES: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestLAKES


 //************************************************************************
 // releaseRestSKANEATELES - Custom Processing for Skaneateles CC
 //
 //      Change the start date of a specified Member Restriction (3 Days in advance).
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestSKANEATELES(String name, Connection con) {


   PreparedStatement pstmt = null;


   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,7);                          // get the date 7 days from now
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) + 1;
      int day = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestSKANEATELES: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestSKANEATELES


 //************************************************************************
 // releaseRestRV - Custom Processing for Rogue Valley CC
 //
 //      Change the start date of all member restrictions whose names start
 //      with 'Phone Only' so they are not enforced for the day 2 days from today.
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static void releaseRestRV(Connection con) {


   PreparedStatement pstmt = null;


   String name = "Phone Only%";            // beginning of name of restrictions

   try {
      //
      //  Get today's date
      //
      Calendar cal = new GregorianCalendar();            // get todays date

      cal.add(Calendar.DATE,3);                          // get the new start date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH);
      int day = cal.get(Calendar.DAY_OF_MONTH);

      month = month + 1;                                 // month starts at zero

      long date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

      //
      //  Change the start dates
      //
      pstmt = con.prepareStatement (
        "UPDATE restriction2 SET sdate = ?, start_mm = ?, start_dd = ?, start_yy = ? " +
        "WHERE name LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setInt(2, month);
      pstmt.setInt(3, day);
      pstmt.setInt(4, year);
      pstmt.setString(5, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Remove the restrictions from teecurr
      //
      pstmt = con.prepareStatement (
        "UPDATE teecurr2 SET restriction = '', rest_color = '' " +
        "WHERE date < ? AND restriction LIKE ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setLong(1, date);
      pstmt.setString(2, name);

      pstmt.executeUpdate();     // execute the prepared stmt

      pstmt.close();

      //
      //  Now, check the restriction tables and rebuild teecurr entries if needed
      //
      doRests(con);

   }
   catch (Exception e2) {
      //
      //  save error message in /v_x/error.txt
      //
      String errorMsg = "Error in SystemUtils releaseRestRV: ";
      errorMsg = errorMsg + e2.getMessage();                                 // build error msg

      logError(errorMsg);                                       // log it
   }

 }  // end of releaseRestRV


 //************************************************************************
 // getDate
 //
 //    Common method to get the current date (adjusted for time zone)
 //************************************************************************

 public static long getDate(Connection con) {

   
   long date = 0;

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   int time = (cal_hourDay * 100) + cal_min;

   time = adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value

      if (time < 100) {           // if hour is zero, then we rolled ahead 1 day

         //
         // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
         //
         cal.add(Calendar.DATE,1);                     // get next day's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);

      } else {                        // we rolled back 1 day

         //
         // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
         //
         cal.add(Calendar.DATE,-1);                     // get yesterday's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH);
         day = cal.get(Calendar.DAY_OF_MONTH);
      }
   }

   month++;                            // month starts at zero
   date = (year * 10000) + (month * 100) + day;

   return(date);

 }  // end of getDate


 //************************************************************************
 // getTime
 //
 //    Common method to get the current time (adjusted for time zone)
 //************************************************************************

 public static int getTime(Connection con) {


   int time = 0;

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();        // get todays date
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   //
   //  Build the 'time' string for display
   //
   //    Adjust the time based on the club's time zone (we are Central)
   //
   time = (cal_hourDay * 100) + cal_min;

   time = adjustTime(con, time);       // adjust for time zone

   if (time < 0) {                // if negative, then we went back or ahead one day

      time = 0 - time;          // convert back to positive value (getDate above should have adjusted the date)
   }

   return(time);

 }  // end of getTime


 // *********************************************************
 //  Display Notes in new pop-up window
 //
 //
 //   called by:  Proshop_sheet
 //               Proshop_searchmem
 //
 // *********************************************************

 public static void displayNotes(String stime, String sfb, long date, String courseName1, PrintWriter out, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String notes = "";
   String orig_by = "";
   String orig_name = "";
   String assoc_name = "";
   String conf = "";
   String day_name = "";

   int month = 0;
   int day = 0;
   int year = 0;

   boolean unaccomp = false;

   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT mm, dd, yy, day, player1, player2, player3, player4, player5, notes, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by, conf " +
         "FROM teecurr2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setShort(3, fb);
      pstmt2s.setString(4, courseName1);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month = rs.getInt(1);
         day = rs.getInt(2);
         year = rs.getInt(3);
         day_name = rs.getString(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         player5 = rs.getString(9);
         notes = rs.getString(10);
         userg1 = rs.getString(11);
         userg2 = rs.getString(12);
         userg3 = rs.getString(13);
         userg4 = rs.getString(14);
         userg5 = rs.getString(15);
         orig_by = rs.getString(16);
         conf = rs.getString(17);
      }

      pstmt2s.close();

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;         // use proshop username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";           // init

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

      //
      //  Check for Unaccompanied Guests
      //
      unaccomp = false;

      if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {    // if any guests

         unaccomp = true;     // default = unaccompanied guests

         if (!player1.equals("") && !player1.equalsIgnoreCase( "x" ) && userg1.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player2.equals("") && !player2.equalsIgnoreCase( "x" ) && userg2.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player3.equals("") && !player3.equalsIgnoreCase( "x" ) && userg3.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player4.equals("") && !player4.equalsIgnoreCase( "x" ) && userg4.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player5.equals("") && !player5.equalsIgnoreCase( "x" ) && userg5.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
      }

      if (unaccomp == true) {

         if (!player1.equals("") && !userg1.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg1);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player1 = player1 + " is associated with member " + assoc_name;  // create string
         } else {
            player1 = "";
         }
         if (!player2.equals("") && !userg2.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg2);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player2 = player2 + " is associated with member " + assoc_name;  // create string
         } else {
            player2 = "";
         }
         if (!player3.equals("") && !userg3.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg3);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player3 = player3 + " is associated with member " + assoc_name;  // create string
         } else {
            player3 = "";
         }
         if (!player4.equals("") && !userg4.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg4);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player4 = player4 + " is associated with member " + assoc_name;  // create string
         } else {
            player4 = "";
         }
         if (!player5.equals("") && !userg5.equals( "" )) {   // if player is a guest

            assoc_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg5);

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

               assoc_name = mem_name.toString();                          // convert to one string
            }
            pstmtc.close();

            player5 = player5 + " is associated with member " + assoc_name;  // create string
         } else {
            player5 = "";
         }
      }
   }
   catch (Exception ignore) {
      return;
   }

   out.println(SystemUtils.HeadTitle("Show Notes"));
   out.println("<BODY><CENTER><BR>");
   out.println("<font size=\"3\"><b>Tee Time Notes</b></font><br><BR>");
   out.println("<font size=\"2\"><b>For " + day_name + " " +month+ "/" +day+ "/" +year+ " on the ");
   if (fb == 0) {
      out.println("Front Tee");
   } else {
      out.println("Back Tee");
   }
   if (!courseName1.equals( "" )) {
      out.println(" of the " + courseName1);
   }
   out.println("</b>");
   if (!orig_name.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Originated by: &nbsp;&nbsp;<b>" + orig_name + "</b>");
   }
   if (!conf.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Confirmation # or Id: &nbsp;&nbsp;<b>" + conf + "</b>");
   }

   if (unaccomp == true) {

      out.println("<br><br>Unaccompanied Guests");
      if (!player1.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 1&nbsp;&nbsp;</b>" + player1);
      }
      if (!player2.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 2&nbsp;&nbsp;</b>" + player2);
      }
      if (!player3.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 3&nbsp;&nbsp;</b>" + player3);
      }
      if (!player4.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 4&nbsp;&nbsp;</b>" + player4);
      }
      if (!player5.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 5&nbsp;&nbsp;</b>" + player5);
      }
   }
   if (!notes.equals( "" )) {
      out.println("<br><br>");
      out.println("<b>Notes:</b> &nbsp;&nbsp;" + notes );
   }
   out.println("<BR>");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970; width:80px\" value=\"Close\" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }                   // end of displayNotes


 // *********************************************************
 //  Display Notes in new pop-up window
 //
 //
 //   called by:  Proshop_sheet
 //               Proshop_searchmem
 //
 // *********************************************************

 public static void displayOldNotes(String stime, String sfb, long date, String courseName1, PrintWriter out, Connection con) {


   PreparedStatement pstmtc = null;
   ResultSet rs = null;

   String player1 = "";
   String player2 = "";
   String player3 = "";
   String player4 = "";
   String player5 = "";
   String userg1 = "";
   String userg2 = "";
   String userg3 = "";
   String userg4 = "";
   String userg5 = "";
   String notes = "";
   String orig_by = "";
   String orig_name = "";
   String conf = "";
   String day_name = "";

   int month = 0;
   int day = 0;
   int year = 0;

   boolean unaccomp = false;

   //
   //  Convert the common string values to int's
   //
   int time = Integer.parseInt(stime);
   short fb = Short.parseShort(sfb);

   try {

      PreparedStatement pstmt2s = con.prepareStatement (
         "SELECT mm, dd, yy, day, player1, player2, player3, player4, player5, notes, " +
         "userg1, userg2, userg3, userg4, userg5, orig_by, conf " +
         "FROM teepast2 WHERE date = ? AND time = ? AND fb = ? and courseName = ?");

      pstmt2s.clearParameters();        // clear the parms
      pstmt2s.setLong(1, date);
      pstmt2s.setInt(2, time);
      pstmt2s.setShort(3, fb);
      pstmt2s.setString(4, courseName1);

      rs = pstmt2s.executeQuery();      // execute the prepared stmt

      if (rs.next()) {

         month = rs.getInt(1);
         day = rs.getInt(2);
         year = rs.getInt(3);
         day_name = rs.getString(4);
         player1 = rs.getString(5);
         player2 = rs.getString(6);
         player3 = rs.getString(7);
         player4 = rs.getString(8);
         player5 = rs.getString(9);
         notes = rs.getString(10);
         userg1 = rs.getString(11);
         userg2 = rs.getString(12);
         userg3 = rs.getString(13);
         userg4 = rs.getString(14);
         userg5 = rs.getString(15);
         orig_by = rs.getString(16);
         conf = rs.getString(17);
      }

      pstmt2s.close();

      if (!orig_by.equals( "" )) {         // if originator exists (username of person originating tee time)

         if (orig_by.startsWith( "proshop" )) {  // if originator exists (username of person originating tee time)

            orig_name = orig_by;         // use proshop username

         } else {

            //
            //  Check member table and hotel table for match
            //
            orig_name = "";           // init

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

      //
      //  Check for Unaccompanied Guests
      //
      unaccomp = false;

      if (!userg1.equals( "" ) || !userg2.equals( "" ) || !userg3.equals( "" ) || !userg4.equals( "" ) || !userg5.equals( "" )) {    // if any guests

         unaccomp = true;     // default = unaccompanied guests

         if (!player1.equals("") && !player1.equalsIgnoreCase( "x" ) && userg1.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player2.equals("") && !player2.equalsIgnoreCase( "x" ) && userg2.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player3.equals("") && !player3.equalsIgnoreCase( "x" ) && userg3.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player4.equals("") && !player4.equalsIgnoreCase( "x" ) && userg4.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
         if (!player5.equals("") && !player5.equalsIgnoreCase( "x" ) && userg5.equals( "" )) { // if player and NOT a guest
            unaccomp = false;
         }
      }

      if (unaccomp == true) {

         if (!player1.equals("") && !userg1.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg1);

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

            player1 = player1 + " is associated with member " + orig_name;  // create string
         } else {
            player1 = "";
         }
         if (!player2.equals("") && !userg2.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg2);

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

            player2 = player2 + " is associated with member " + orig_name;  // create string
         } else {
            player2 = "";
         }
         if (!player3.equals("") && !userg3.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg3);

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

            player3 = player3 + " is associated with member " + orig_name;  // create string
         } else {
            player3 = "";
         }
         if (!player4.equals("") && !userg4.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg4);

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

            player4 = player4 + " is associated with member " + orig_name;  // create string
         } else {
            player4 = "";
         }
         if (!player5.equals("") && !userg5.equals( "" )) {   // if player is a guest

            orig_name = "";           // init

            pstmtc = con.prepareStatement (
               "SELECT name_last, name_first, name_mi FROM member2b WHERE username= ?");

            pstmtc.clearParameters();        // clear the parms
            pstmtc.setString(1, userg5);

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

            player5 = player5 + " is associated with member " + orig_name;  // create string
         } else {
            player5 = "";
         }
      }
   }
   catch (Exception ignore) {
      return;
   }

   out.println(SystemUtils.HeadTitle("Show Notes"));
   out.println("<BODY><CENTER><BR>");
   out.println("<font size=\"3\"><b>Tee Time Notes</b></font><br><BR>");
   out.println("<font size=\"2\"><b>For " + day_name + " " +month+ "/" +day+ "/" +year+ " on the ");
   if (fb == 0) {
      out.println("Front Tee");
   } else {
      out.println("Back Tee");
   }
   if (!courseName1.equals( "" )) {
      out.println(" of the " + courseName1);
   }
   out.println("</b>");
   if (!orig_name.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Originated by: &nbsp;&nbsp;<b>" + orig_name + "</b>");
   }
   if (!conf.equals( "" )) {
      out.println("<br><br>");
      out.println("Tee Time Confirmation # or Id: &nbsp;&nbsp;<b>" + conf + "</b>");
   }

   if (unaccomp == true) {

      out.println("<br><br>Unaccompanied Guests");
      if (!player1.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 1&nbsp;&nbsp;</b>" + player1);
      }
      if (!player2.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 2&nbsp;&nbsp;</b>" + player2);
      }
      if (!player3.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 3&nbsp;&nbsp;</b>" + player3);
      }
      if (!player4.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 4&nbsp;&nbsp;</b>" + player4);
      }
      if (!player5.equals( "" )) {
         out.println("<br>");
         out.println("<b>Player 5&nbsp;&nbsp;</b>" + player5);
      }
   }
   if (!notes.equals( "" )) {
      out.println("<br><br>");
      out.println("<b>Notes:</b> &nbsp;&nbsp;" + notes );
   }
   out.println("<BR>");
   out.println("<form>");
   out.println("<input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Close  \" onClick='self.close()' alt=\"Close\">");
   out.println("</form>");
   out.println("</font></CENTER></BODY></HTML>");
   out.close();

 }                   // end of displayOldNotes


 //************************************************************************
 //  doTableUpdate_tmodes - creates the tmodes db table if it does not already exist
 //                       - also populates it from tmode info in clubparm2 table.
 //
 //   called by:  Proshop_reports_course_rounds
 //               Proshop_parms
 //
 //************************************************************************

 public static void doTableUpdate_tmodes(Connection con) {

     String tmp_error = "";

     try {

        Statement stmt = con.createStatement();        // create a statement

        tmp_error = "SystemUtils.doTableUpdate_tmodes: Create tmodes ";

        // if needed create tmodes table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS tmodes(" +
            "courseName CHAR(30) NOT NULL, " +
            "tmodea CHAR(3) NOT NULL, " +
            "tmode CHAR(20) NOT NULL, " +
            "PRIMARY KEY (courseName, tmodea));");

        tmp_error = "SystemUtils.doTableUpdate_tmodes: Update tmodes ";

        // populate table
        stmt.executeUpdate("REPLACE INTO tmodes " +
            "SELECT courseName, tmodea, tmode FROM (" +
            "SELECT tmode1 AS tmode, tmodea1 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode2 AS tmode, tmodea2 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode3 AS tmode, tmodea3 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode4 AS tmode, tmodea4 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode5 AS tmode, tmodea5 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode6 AS tmode, tmodea6 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode7 AS tmode, tmodea7 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode8 AS tmode, tmodea8 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode9 AS tmode, tmodea9 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode10 AS tmode, tmodea10 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode12 AS tmode, tmodea12 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode13 AS tmode, tmodea13 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode14 AS tmode, tmodea14 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode15 AS tmode, tmodea15 AS tmodea, courseName FROM clubparm2 " +
            "UNION ALL " +
            "SELECT tmode16 AS tmode, tmodea16 AS tmodea, courseName FROM clubparm2 " +
            ") AS tmodes WHERE courseName IS NOT NULL AND tmodea IS NOT NULL AND tmode IS NOT NULL AND tmodea <> '';");

        stmt.close();

     } catch (Exception exc) {

         tmp_error = tmp_error + exc.getMessage();                  // build error msg
         logError(tmp_error);                                       // log it
     }          
       
  }


 //************************************************************************
 //  updateHist - track all changes to tee times - maintain a history log
 //
 //   called by:  Proshop_slot
 //               Proshop_slotm
 //               Proshop_insert
 //               Proshop_evntChkAll
 //               Member_slot
 //               Member_slotm
 //
 //************************************************************************

 public static void updateHist(long date, String day, int time, int fb, String course, String p1, String p2, String p3, String p4, String p5,  
                               String user, String mName, int type, Connection con) {


     String tmp_error = "";

     //
     //  Get the current date/time
     //
     Calendar cal = new GregorianCalendar();        // get todays date
     int year = cal.get(Calendar.YEAR);
     int month = cal.get(Calendar.MONTH) +1;
     int daynum = cal.get(Calendar.DAY_OF_MONTH);

     long mdate = (year * 10000) + (month * 100) + daynum;         // date value for today
       
     year--;
     long odate = (year * 10000) + (month * 100) + daynum;         // create a date value of one year ago

     String sdate = String.valueOf(new java.util.Date());


     //
     //   Create the teehist table if it does not already exist
     //
     try {

        Statement stmt = con.createStatement();        // create a statement

        tmp_error = "SystemUtils.updateHist: Create teehist ";

        // if needed create teehist table
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS teehist (" +
                         "date bigint, " +
                         "day varchar(10), time integer, " +
                         "fb smallint, courseName varchar(30), " +
                         "player1 varchar(43), player2 varchar(43), " +
                         "player3 varchar(43), player4 varchar(43), player5 varchar(43), " +
                         "user varchar(15), mname varchar(30), " +
                         "mdate bigint, sdate varchar(36), type smallint, " +
                         "INDEX ind1 (date, time, fb, courseName))");

        stmt.close();

     } catch (Exception exc) {

         tmp_error = tmp_error + exc.getMessage();                  // build error msg
         logError(tmp_error);                                       // log it
     }

     tmp_error = "SystemUtils.updateHist: Update teehist ";

     //
     // Insert an entry in the history table.  Save the new tee time info and some tracking data.
     //
     //   user = username of person making the change
     //   mname = their name
     //   mdate = current date in yyyymmdd format for searches
     //   sdate = use current date/time stamp in Central Time
     //   type = 0 is new tee time, 1 = modified tee time
     //
     if (date > 0 && time > 0 && !user.equals( "" )) {

        try {

           //
           //  Insert a history entry
           //
           PreparedStatement pstmt = con.prepareStatement (
                      "INSERT INTO teehist (date, day, time, fb, courseName, " +
                      "player1, player2, player3, player4, player5, " +
                      "user, mname, mdate, sdate, type) " +
                      "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

           pstmt.clearParameters();      
           pstmt.setLong(1, date);
           pstmt.setString(2, day);     
           pstmt.setInt(3, time);
           pstmt.setInt(4, fb);
           pstmt.setString(5, course);
           pstmt.setString(6, p1);
           pstmt.setString(7, p2);
           pstmt.setString(8, p3);
           pstmt.setString(9, p4);
           pstmt.setString(10, p5);
           pstmt.setString(11, user);
           pstmt.setString(12, mName);
           pstmt.setLong(13, mdate);
           pstmt.setString(14, sdate);
           pstmt.setInt(15, type);

           pstmt.executeUpdate();          // execute the prepared stmt

           pstmt.close();  
     
        } catch (Exception exc) {

            tmp_error = tmp_error + exc.getMessage();                  // build error msg
            logError(tmp_error);                                       // log it
        }
     }

     //
     //  Now delete any entries that are more than 1 year old
     //
     tmp_error = "SystemUtils.updateHist: Delete old entries ";

     try {

        PreparedStatement pstmt = con.prepareStatement (
               "Delete FROM teehist WHERE mdate < ?");

        pstmt.clearParameters();
        pstmt.setLong(1, odate);

        pstmt.executeUpdate();          // execute the prepared stmt

        pstmt.close();

     } catch (Exception exc) {

         tmp_error = tmp_error + exc.getMessage();                  // build error msg
         logError(tmp_error);                                       // log it
     }

  }       // end of updateHist


/*
  
 //************************************************************************
 //  verifyTimer - verify that the 2 minute inactTimer was actually 2 minutes
 //
 //   called by:  inactTimer
 //
 //************************************************************************

 public static boolean verifyTimer(int hr, int min, int sec) {


   boolean error = false;
       
   String tmp_error = "SystemUtils.verifyTimer: 2 minute tee time timer expired too early.";

   int newTime = (hr * 10000) + (min * 100) + sec;     // create time stamp from current time

   if (inactTime == 0) {                               // if first time here
     
      inactTime - newTime;                             // set this time
        
   } else {

              // Do not need this method right now - finish this later if needed
   }
     
   if ( ) {
     
         error = true;                        // indicate error 

         logError(tmp_error);                 // log it
   }

   return(error);
  }

*/


 //************************************************************************
 //
 //  sessionLog - logs each time a user logs in
 //
 //   called by:  Login (see Logout also)
 //
 //************************************************************************

 public static void sessionLog(String msg, String user, String pw, String club, String caller, Connection con) {


   String errorMsg = "";
     
  
   try {
      //
      //   Get current month
      //
      Calendar cal = new GregorianCalendar();                      // get todays date
      int year = cal.get(Calendar.YEAR);
      int month = cal.get(Calendar.MONTH) +1;
      int daynum = cal.get(Calendar.DAY_OF_MONTH);

      long date = (year * 10000) + (month * 100) + daynum;         // date value for today

      String sdate = String.valueOf(new java.util.Date());         // get date and time string

      //
      //  Build the message
      //
      msg = msg + " User=" +user+ ", PW=" +pw+ ", Club=" +club+ ", Caller=" +caller;

      //
      //  Create table to hold log in case it has not already been added for this club
      //
      if (con != null) {

         Statement stmt = con.createStatement();        // create a statement

         stmt.executeUpdate("CREATE TABLE IF NOT EXISTS sessionlog(" +
                            "date bigint, sdate varchar(36), msg text, " +
                            "index ind1 (date))");

         stmt.close();

         //
         //  Save the session message in the db table
         //
         PreparedStatement pstmt = con.prepareStatement (
              "INSERT INTO sessionlog (date, sdate, msg) " +
              "VALUES (?,?,?)");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setString(2, sdate);
         pstmt.setString(3, msg);
         pstmt.executeUpdate();          // execute the prepared stmt

         pstmt.close();   // close the stmt
      }

   }
   catch (Exception e) {

      errorMsg = "Error in SystemUtils.sessionLog for club: " +club+ ". Exception= ";
      errorMsg = errorMsg + e.getMessage();                     // build error msg

      logError(errorMsg);                                       // log it and continue
   }
  
 }  // end of sessionLog


 //************************************************************************
 //  logError - logs system error messages to a db table (errorlog) in Vx db
 //
 //
 //   called by:  Login
 //               Member_slot and others
 //
 //************************************************************************

 public static void logError(String msg) {
   
  
   Connection con = null;
   String club = rev;                       // get db name to use for 'clubs' table

   //
   //   Get current month
   //
   Calendar cal = new GregorianCalendar();                      // get todays date
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH) +1;
   int daynum = cal.get(Calendar.DAY_OF_MONTH);

   long date = (year * 10000) + (month * 100) + daynum;         // date value for today

   String sdate = String.valueOf(new java.util.Date());         // get date and time string

   try {

      con = dbConn.Connect(club);                // get a connection

      if (con != null) {                         // if we got one

         Statement stmt = con.createStatement();     

         stmt.executeUpdate("CREATE TABLE IF NOT EXISTS errorlog(" +
                            "date bigint, sdate varchar(36), msg text, " +
                            "index ind1 (date))");

         stmt.close();

         //
         //  Save the session message in the db table
         //
         PreparedStatement pstmt = con.prepareStatement (
              "INSERT INTO errorlog (date, sdate, msg) " +
              "VALUES (?,?,?)");

         pstmt.clearParameters();        // clear the parms
         pstmt.setLong(1, date);
         pstmt.setString(2, sdate);
         pstmt.setString(3, msg);
         pstmt.executeUpdate();          // execute the prepared stmt

         pstmt.close();   // close the stmt

         //
         //  Delete any old session log entries
         //
         cal = new GregorianCalendar();                 // get todays date again
         cal.add(Calendar.DATE,-7);                     // get last week's date

         year = cal.get(Calendar.YEAR);
         month = cal.get(Calendar.MONTH) +1;
         daynum = cal.get(Calendar.DAY_OF_MONTH);

         date = (year * 10000) + (month * 100) + daynum;     // date = yyyymmdd (for comparisons)

         PreparedStatement pstmt3 = con.prepareStatement (
             "DELETE FROM errorlog " +
             "WHERE date < ?");

         //
         //  execute the prepared statement to update the tee time slot
         //
         pstmt3.clearParameters();        // clear the parms
         pstmt3.setLong(1, date);
         pstmt3.executeUpdate();

         pstmt3.close();

         con.close();             // close the connection to the system db
      }

   }
   catch (Exception ignore) {
   }

 }  // end of logError


 //************************************************************************
 //  logError2 - logs system error messages to a text file
 //
 //       *** NOT USED ***
 //
 //************************************************************************

 public static void logError2(String msg, parmLott parm, parmLottC parmc) {

   String space = "  ";
 
   int time = 0;
   int fb = 0;
   int wght = 0;
   long id = 0;


   try {
      //
      //  dir path for test pc
      //
      PrintWriter fout = new PrintWriter(new FileWriter("c:\\java\\tomcat\\webapps\\" +rev+ "\\error.txt", true));

      //
      //  Put header line in text file
      //
      fout.print(new java.util.Date() + space + msg);
      fout.println();      // output the line

      fout.print(parm.sfb+ ", " +parm.stime+ ", " +parm.etime+ ", " +parm.ltype);
      fout.println();      // output the line

      for (int i=0; i<100; i++) {
        
         time = parmc.timeA[i];
         fb = parmc.fbA[i];
         id = parmc.idA[i];
         wght = parmc.wghtA[i];

         if (time > 0 || id > 0) {
           
            fout.print(time+ ", " +fb+ ", " +id+ ", " +wght);
            fout.println();      // output the line
  
         } else {
           
            break;
         }
      }

      fout.close();
   }
   catch (Exception ignore) {
   }
  
 }  // end of logError2


 //************************************************************************
 //  getStackAsString - convert the stack trace to a string given the exception
 //
 //         (taken from oreilly - see page 152 in Java Servlet Programming)
 //
 //
 //************************************************************************

 public static String getStackAsString(Throwable t) {


   ByteArrayOutputStream bytes = new ByteArrayOutputStream();

   PrintWriter writer = new PrintWriter(bytes, true);
    
   t.printStackTrace(writer);
     
   return bytes.toString();

 }  // end of getStackAsString

 
 //************************************************************************
 //
 // Return a string with a leading zero is nessesary
 //
 //************************************************************************
 public static String ensureDoubleDigit(int value) {

    return ((value < 10) ? "0" + value : "" + value);
     
 }
 
 
 //************************************************************************
 //
 // Returns a formated string from passed in military time
 //
 //************************************************************************
 public static String getSimpleTime(int hr, int min) {
    
    String ampm = " AM";
    
    if (hr == 12) ampm = " PM";
    if (hr > 12) { ampm = " PM"; hr = hr - 12; }    // convert to conventional time
    if (hr == 0) hr = 12;

    
    return hr + ":" + ensureDoubleDigit(min) + ampm; 
    
 }
 
 
 //************************************************************************
 // 
 // Display a standard database error message
 //
 //************************************************************************
 public static void buildDatabaseErrMsg(String pMessage, String pException, PrintWriter out, boolean pNewPage) {
    
    if (pMessage == null) pMessage = "";
    if (pException == null) pException = "";

    if (pNewPage) {
        out.println(SystemUtils.HeadTitle("Database Error"));
        out.println("<BODY BGCOLOR=\"white\">");
    }
    
    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    
    if (!pMessage.equals("")) out.println("<BR>Fatal Error: " + pMessage);
    if (!pException.equals("")) out.println("<BR>Exception: " + pException);
    
    out.println("<BR>If problem persists, contact customer support.");
    out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
    out.println("</BODY></HTML>");
     
 }


 //************************************************************************
 //
 // Returns the UID clubparm_id for the specified tee time.  Returns 
 // if method fails or not found.
 //
 //************************************************************************
 public static int getClubParmIdFromTeeCurrID(int pTeeCurrID, Connection con) {
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    int clubparm_id = 0;
    String course = "";
    
    try {
        
        pstmt = con.prepareStatement("SELECT courseName FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            // we found a course name, now lets look up its uid
            clubparm_id = getClubParmIdFromCourseName(rs.getString("courseName"), con);
        }
    }
    catch (Exception ignore) {
    }
    
    return clubparm_id;
 }

 
 //************************************************************************
 //
 // Returns the UID clubparm_id for the specified course name.  Returns 
 // if method fails or not found.
 //
 //************************************************************************
 public static int getClubParmIdFromCourseName(String pCourseName, Connection con) {
        
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    int clubparm_id = 0;
    
    try {
        pstmt = con.prepareStatement("SELECT clubparm_id FROM clubparm2 WHERE courseName = ?");
        pstmt.clearParameters();
        pstmt.setString(1, pCourseName);
        rs = pstmt.executeQuery();
        if (rs.next()) clubparm_id = rs.getInt(1);
        rs.close();
        pstmt = null;
    }
    catch (Exception ignore) {
    }
    
    return clubparm_id;
 }
 
 
 //************************************************************************
 //
 // Connect to mail server database and retrieved rs of all bounced emails
 // and purge them from the club databases.
 //
 //************************************************************************
 public static void purgeBouncedEmails() {
    
    Connection con = null; // initally remote db
    Connection con2 = null; // local
    Statement stmt = null;
    Statement stmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    int count = 0;
    String club = "";
    String email = "";
    String blah = "0";
    
    int server_id = Common_Server.SERVER_ID;            // get the id of the server we are running in!!!!
    
    //
    //  This must be the master server!!!  If not, let the timer run in case master goes down.
    //
    if (server_id == server_master) {
    
        // move records from mail server db to the foretees db
        try {
            
            // get con to mail servers db
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection("jdbc:mysql://216.243.184.88/xmail_bounces", "xmail_filters", "xmfilmail");
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM bounced WHERE email <> ''");
            
            // con to main db
            con2 = dbConn.Connect("xmail_bounces");
            
            while (rs.next()) {
                
                email = rs.getString("email");
                stmt2 = con2.createStatement();
                stmt2.executeUpdate("INSERT INTO bounced (email) VALUES ('" + email + "') ON DUPLICATE KEY UPDATE email = '" + email + "'");
                count++;
            }
            
            logError("XMail Bouncer: Retrieved " + count + " email addresses from bounce backs."); 
            
            // delete any empty email address (this shouldn't happen now, but it causes problems if present)
            stmt = con2.createStatement();
            stmt.executeUpdate("DELETE FROM bounced WHERE email = ''");
            stmt.close();
            
            // empty addresses stored on mail server
            //stmt = con.createStatement();
            //stmt.executeUpdate("DELETE FROM bounced");
            //stmt.close();   
        }
        catch (Exception e0) {
            
            logError("XMail Bouncer: Error moving records. " + blah + " " + e0.getMessage()); 
        }
        
        //
        //  Perform timer function for each club in the system database 'clubs' table
        //
        try {
            con = dbConn.Connect(rev);
        }
        catch (Exception e1) {
            
            logError("Error connecting to " + rev + " in SystemUtils.purgeBouncedEmails: " + e1.getMessage()); 
        }

        if (con != null) {

            //
            // Get the club names from the 'clubs' table
            //
            //  Process each club in the table
            //
            try {

                stmt = con.createStatement();                       // create a statement
                rs = stmt.executeQuery("SELECT clubname FROM clubs");

                while (rs.next()) {

                    club = rs.getString(1);                 // get a club name
                    con2 = dbConn.Connect(club);            // get a connection to this club's db
                    
                    stmt2 = con2.createStatement();
                    count = stmt2.executeUpdate("" +
                            "UPDATE member2b m, xmail_bounces.bounced b " +
                            "SET " +
                                "m.email = IF(m.email=b.email, '', m.email), " +
                                "m.email2 = if(m.email2=b.email, '', m.email2) " +
                            "WHERE " +
                                "m.email = b.email " +
                                "OR " +
                                "m.email2 = b.email");
                    stmt2.close();
                    
                    if (count > 0) {
                        
                        // we removed an address from this club - log it
                        logError("XMail Bouncer: Removed " + count + " from " + club);  
                    }
                    
                } // end while loop of all clubs
                
                // delete bounced records on foretees db server
                con2 = dbConn.Connect("xmail_bounces");
                stmt = con2.createStatement();
                stmt.executeUpdate("DELETE FROM bounced");
                stmt.close();    
                
            } catch (Exception e2) {
                
                logError("Error2 in SystemUtils.purgeBouncedEmails: club=" + club + ", " + e2.getMessage()); 
            } // end try/catch
            
        } // end con check
        
    } // end if server_master

 }

}  // end of utility class
