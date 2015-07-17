/***************************************************************************************
 *   ProcessConstants:  This class defines constants used in for processing purposes
 *
 *   created: 10/16/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *        12/02/13  Updated dates for 2014.
 *         7/23/13  Add CONNECT activity id.
 *        12/01/12  Updated dates for 2013.
 *        12/02/11  Updated holidays and other dates for 2012.
 *         8/30/11  Add Dining username and password for Dining Admin to login to ForeTees for configuration changes.
 *         5/18/11  Add Dining Activity Id (hard coded so we can use the FlxRez structure already in place).
 *         4/27/11  Updated dates for 2011.
 *         8/10/10  Update TRAILERPRO - remove notice informing members not to reply to the email since they 
 *                                      can reply to emails sent from the pros.
 *        12/01/09  Update the dates for 2010.
 *        10/03/09  Change 'Golf' to 'Club' in the pro trailer so it will work for Activities.
 *        04/29/09  Added TRAILERFB for emails sent out from food & bev proshop users (proshopfb) (case 1641).
 *        04/28/09  Added EFROMFB for emails sent out from food & bev proshop users (proshopfb) (case 1641).
 *        01/12/09  Updae holiday dates and add isHoliday method to return dates.
 *        09/17/08  Added SERVER_ID here instead of Common_Server
 *        08/13/07  Add Columbus Day Observed holiday  
 *        11/04/05  Add holiday dates so they are in one location.
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.util.Properties;
import java.util.*;
import javax.servlet.http.*;

/**
 ***************************************************************************************
 *
 *  This class is a utility that contains constants for processing.
 *
 ***************************************************************************************
 **/

public class ProcessConstants {

  
  //
  //  Holiday Dates to be changed annually  (NOTE: Should use isHoliday method below to check dates!!!!!)
  //
  final public static long nyDay = 20160101;          // New Year's Day
  final public static long mlkDay = 20160118;         // Martin Luther King Jr. Day
  final public static long presDay = 20160215;        // President's Day

  final public static long gFriDay = 20160325;        // Good Friday
  final public static long easterDay = 20160327;      // Easter Day
  final public static long mothersDay = 20160508;     // Mother's Day
  final public static long memDay = 20150525;         // Memorial Day      ********** Change these each Year *************
  final public static long fathersDay = 20150621;     // Father's Day
    
  final public static long july4 = 20150703;          // 4th of July - Monday or Friday (only when 7/04 is on or near Monday or Friday)
  final public static long july4b = 20150704;         // 4th of July - Actual

  final public static long laborDay = 20150907;       // Labor Day

  final public static long colDay = 20151012;         // Columbus Day (always 10/12)
  final public static long colDayObsrvd = 20151012;   // Columbus Day Observed (Monday)
  
  final public static long hallowDay = 20151031;      // Halloween Day

  final public static long tgDay = 20151126;          // Thanksgiving Day
  final public static long xmasDay = 20151225;        // Christmas Day
  final public static long nyEveDay = 20151231;       // New Year's Eve

  //
  //  Hazeltine dates
  //
  final public static long Hdate4 = 20141001;   // October 1st
  final public static long Hdate5 = 20140605;   // Junior Fridays Start (start on Thurs.)
  final public static long Hdate6 = 20140927;   // Junior Fridays End  (end on Sat.)


  //
  //  server id
  //
  static Properties props = System.getProperties();
  static public int SERVER_ID = Integer.parseInt(props.getProperty("server_id"));
  
  // Don't make these IDs public -- use the ".isXXX" methods below
  static final private int DEV_SERVER_ID = 4;
  static final private int TESTING_SERVER_ID = 70;
  static final private int TESTING_SERVER_END_ID = 79;
  static final private int STAGING_SERVER_ID = 60;
  static final private int STAGING_SERVER_END_ID = 69;
  static final private int TIMER1_SERVER_ID = 101;
  static final private int TIMER2_SERVER_ID = 102;
  
  static public String  DEV_SERVER_BASE_URL = "http://dev2.foretees.com";
  static public String  PRODUCTION_SERVER_BASE_URL = "http://web.foretees.com";
  static public String  STAGING_SERVER_BASE_URL = "http://staging.foretees.com";
  static public String  TESTING_SERVER_BASE_URL = "http://testing.foretees.com";
  static public String  THIS_SERVER_BASE_URL = getServerUrl();
  
  static public String  SERVLET_PATH = "/v5/servlet/";
    
  //
  //  user types
  //
  final public static String ADMIN = "admin";
  final public static String PROSHOP = "proshop";
  final public static String MEMBER = "member";
  
  final public static int EVENT_GENDER_UNCATOREGIZED = 0;
  final public static int EVENT_GENDER_MIXED = 1;
  final public static int EVENT_GENDER_MENS = 2;
  final public static int EVENT_GENDER_WOMENS = 3;


  //
  //  codebase
  //
  final public static String CODEBASE = "/v5/";   // NOTE:  see also SystemUtils & com/foretees/event/add_event.jsp
  final public static String REV = "v5";
  
  final public static int GOLF_ACTIVITY_ID = 0;          // use this to identify the dining system and users
  final public static int DINING_ACTIVITY_ID = 9999;          // use this to identify the dining system and users
  final public static int ALL_ACTIVITIES = 999;               // use this to identify all activities
  
  final public static int CONNECT_ID = 8000;                  // use this to identify FT Connect activities (Home page)
  final public static int CONNECT_DIRECTORY_ID = 8001;        // FT Connect Directory Tab
  final public static int CONNECT_STATEMENTS_ID = 8002;       // FT Connect Statements Tab
  final public static int CONNECT_NEWSLETTERS_ID = 8003;      // FT Connect Newsletters Tab
  
  final public static int MANAGERS_PORTAL = 9000;             // use this to identify the Manager's Portal
  
  final public static String DINING_USER = "proshopautofb";    // Dining Admin credentials to link over to ForeTees for configuration
  final public static String DINING_PW = "foretees";


  // ForeTees Connect Premier
  final public static String FT_PREMIER_CALLER = "FLEXWEBFT";
  
  // ForeTees "app_mode" bits (from 0 (lsb) to 30 (msb)) (31 is signed, and may not work consistantly)
  // Sent as integer in url /[club]_[activity]_m[app_mode]/
  // Set/Get using:
  // Utilities.setRequestBit(req, ProcessContants.RQA_APPMODE,ProcessContants.APPMODE_[name of bit]);
  // boolean nameOfBoolean = Utilities.getRequestBit(req, ProcessContants.RQA_APPMODE,ProcessContants.APPMODE_[name of bit]);
  final public static int APPMODE_HIDE_TOP_NAV = 0; //1
  final public static int APPMODE_HIDE_SUB_NAV = 1; //2
  final public static int APPMODE_HIDE_HOME_LINKS = 2; //4
  final public static int APPMODE_RWD = 3; //8  // If set, site will display in RWD (responsive) mode
  final public static int APPMODE_BLOCK_RWD_SWITCH = 4; //16  // If set, do not display links for changing to or from RWD mode.
  final public static int APPMODE_SEEMLESS = 5; //32  // If set, we are in "seemless" mode.  Set in Login upon seemless detection
  final public static int APPMODE_MOBILE_APP1 = 6; //64  // If set, we are in mobile app mode #1
  
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLE2 = 100;
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLEADMIN = 101;
  final public static int SCRIPT_MODE_PROSHOP_EDITOR_IFRAME = 102;
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLEEDITOR = 103;
  final public static int SCRIPT_MODE_PROSHOP_TRANSITIONAL = 104;
  final public static int SCRIPT_MODE_PROSHOP_HYBRID = 105;
  final public static int SCRIPT_MODE_ACCOUNTING = 200;
  final public static int SCRIPT_MODE_INVOICE = 201;
  final public static int SCRIPT_MODE_NEWSKIN = 0;
  final public static int SCRIPT_MODE_RWD = 1; // Responsive
  final public static int SCRIPT_MODE_LOGIN = 2; // We're on the login page
  final public static int SCRIPT_MODE_SLOT_TRANSITIONAL = 3; // RWD slot pagem but on "new skin"
  
  //
  //  Global ReQuest Attribute names
  //
  final public static String RQA_APPMODE = "app_mode"; // Integer - must be "app_mode".  It is accessed in javascript as well
  final public static String RQA_RWD = "req_rwd"; // Boolean
  final public static String RQA_ACCOUNTING = "req_accounting"; // Boolean
  final public static String RQA_PROSHOP_HYBRID = "req_proshop_hybrid"; // Boolean
  final public static String RQA_INVOICE = "req_invoice"; // Boolean
  final public static String RQA_TIMEZONE = "req_timezone"; // String -- Used to cache clubs timezone -- access via Utilities.getClubTimeZone(req)
  final public static String RQA_FT_CONNECT = "req_ft_connect"; // Connection -- fortees db connection object
  final public static String RQA_FT_CONNECT_CLUB = "req_ft_connect_club"; // String -- last clubname used to open fortees db connection
  final public static String RQA_ALLOW_RWD_SWITCH = "req_rwd_switch"; // Boolean
  final public static String RQA_LOGIN = "req_login"; // Boolean - Login mode
  final public static String RQA_ACCESS_ERROR = "req_access_error"; // Boolean - Access error mode
  final public static String RQA_ACTIVITY_ID = "req_activity_id"; // Integer - Activity ID for this request (set in verify mem)
  final public static String RQA_ACTIVITY_MODE = "req_activity_mode"; // String - Activity mode (golf, dining, flxrez) for this request (set in verify mem)
  final public static String RQA_FORCE_TRANSITIONAL = "req_force_transitional"; // String - Activity mode (golf, dining, flxrez) for this request (set in verify mem)

    
  final public static String RQA_PARMCLUB = "reqcc_parmClub_"; // parmClub
  
  final public static String RQA_BLOCKUSER = "req_api_block_user"; // Boolean - set by VerifyUser when user authentication has failed, so as not to retry or get stuck on loop.
  
  final public static String LOGIN_DESKTOP_TOOLTIP = "Use this option to access ForeTees just as you always have from your desktop or laptop computer.";
  final public static String LOGIN_MOBILE_TOOLTIP = "Use this option for ForeTees' new &#8220Mobile&#8221 interface.  It has all the capabilities of &#8220Desktop&#8221 mode, but is easy to use on almost any modern phone, tablet, desktop or laptop computer.";
  
  //
  // Errors (don't set final until we're sure they won't change)
  //
  public static int    ERROR_DB = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
  public static String ERROR_DB_TITLE = "Database Connection Error"; 
  public static String ERROR_DB_MSG = "Unable to connect to the Database.\nPlease try again later.";
  public static int    ERROR_ACCESS = HttpServletResponse.SC_FORBIDDEN;
  public static String ERROR_ACCESS_TITLE = "Access Denied"; 
  public static String ERROR_ACCESS_MSG = "You do not have access to view this page.";
  
  //
  //  Cookie Names
  //
  final public static String COOKIE_RWD_STATE = "foretees_rwd_state";
  
  //
  //  URI Request Parameter names
  //
  final public static String RP_CLUB = "s_c";
  final public static String RP_MODE = "s_m";
  final public static String RP_ACTIVITY = "s_a";
  
  //
  //  slotAccessMode bit map
  //
  final public static int SAM_GROUP_RESTRICTION = 0; //&1 - Only allow members of a group to access/edit group
  final public static int SAM_GROUP_NO_ADD = 1; //&2 - Do not allow players in a group to add players to empty slots
  final public static int SAM_GROUP_NO_CANCEL = 2; //&4 - Do not allow players in a group to add players to empty slots
  final public static int SAM_GROUP_RESTRICT_ORIG = 3; //&8 - Only allow players to edit themselves.
  final public static int SAM_GROUP_DESCENDANTS = 4; //&16 - Allow players in a group to edit decendants of players they added.
  final public static int SAM_GROUP_ANCESTORS = 5; //&32 - Allow players in a group to edit decendants and ancestors of players they added or that added them.
  // 6 & 7 reserved for any future "group" access
  //
  final public static int SAM_ALLOW_ORIG = 8; //&256 - Always allow slot orig to edit.
  final public static int SAM_PLAYER_DESCENDANTS = 9; //&512 - Allow players in a time to edit decendants of players they added.
  final public static int SAM_PLAYER_ANCESTORS = 10; //&1024 - Allow players in a time to edit decendants and ancestors of players they added or that added them.
  final public static int SAM_PLAYER_NO_ADD = 11; //&2048 - Only allow tee time originator to add players to empty slots.

  
  
  //
  //  email constants (see also SystemUtils)
  //
  //public static String HOST = "smtp5.mnwebhost.net";              // old mnwebhost.net smtp server
  //public static String HOST = "mail.foretees.com";                // smtp server
  //public static String HOST = "216.243.184.88";                   // smtp server
  final public static String HOST = "10.0.0.25";                          // smtp server

  //public static String PORT = "587";
  final public static String PORT = "20025";
  //public static String PORT = "25";                               // default port for emails



  final public static String EFROM = "auto-send@foretees.com";              // fake return address for notification emails
  final public static String EFROMPROA = "YourClub@foretees.com";           // fake return address for emails from pro
  final public static String EFROMPRO = "YourGolfShop@foretees.com";        // fake return address for emails from pro
  final public static String EFROMMEM = "aMemberOfYourClub@foretees.com";   // fake return address for emails from members
  final public static String EFROMFB = "YourFoodAndBevDept@foretees.com";   // fake return address for emails from food & bev proshop user (proshopfb only)


  final public static String HEADER = "";/*
  public static String HEADER = "****************************************************************************************" +
                   "\nNOTICE: Please DO NOT reply to this email. " +
                   "\nThis message has been generated by the ForeTees system which cannot process a reply. " +
                   "\n****************************************************************************************\n\n";*/


  final public static String TRAILER = "\n\n\n****************************************************************************************" +
                   "\nShould you prefer not to receive these email notifications, then login to the " +
                   "ForeTees Reservation System and select 'Settings'.  From there, select 'No' next to 'Do " +
                   "you wish to receive email notices of your tee times?' and click on 'Submit'.  If you " +
                   "require further assistance, contact your club professionals.  " +
                   "Thank you for using the ForeTees Reservation System." +
                   "\n****************************************************************************************";


  final public static String TRAILERAOL = "\n\n\n****************************************************************************************" +
                   "\nNOTICE AOL USERS: Please DO NOT report this email as SPAM.  If you do not wish to receive " +
                   "these email notifications PLEASE follow the instructions below OR contact us at SUPPORT@FORETEES.COM " +
                   "and we will do it for you.  Reporting this as spam to AOL could result in the loss of these emails to ALL members." +
                   "\n****************************************************************************************";


  final public static String TRAILERAOL2 = "\n\n****************************************************************************************" +
                   "\nNOTICE AOL USERS: Please DO NOT report this email as SPAM.  If you do not wish to receive " +
                   "these email notifications PLEASE contact your CLUB OR CONTACT FORETEES at SUPPORT@FORETEES.COM " +
                   "and we will remove your email address from the system.  Reporting this as spam to AOL could result in the loss of these emails to ALL members." +
                   "\n****************************************************************************************" +
                   "\n\n";

  /*
  public static String TRAILERPRO = "\n\n\n****************************************************************************************" +
                   "\nNOTICE: Please DO NOT reply to this email. " +
                   "\nThis message has been sent by your Club Professional via the ForeTees system which " +
                   "cannot process a reply. Should you prefer not to receive these email messages, " +
                   "please contact your Club Professional at ";
   */


  final public static String TRAILERPRO = "\n\n\n****************************************************************************************" +
                   "\nThis message has been sent by your Club Professional via the ForeTees system.  " +
                   "Should you prefer not to receive these email messages, " +
                   "please contact your Club Professional at ";


  final public static String TRAILERMEM = "\n\n\n****************************************************************************************" +
                   "\nNOTICE: This message has been sent by a member of your club via the ForeTees system. " +
                   "In order to comply with new anti-spam rules, we cannot use the member's email address in the FROM " +
                   "field. However, you may reply to the sender as their address is saved in the REPLYTO field. " +
                   "Thank you for using ForeTees.";

  final public static String TRAILERFB = "\n\n\n****************************************************************************************" +
                   "\nThis message has been sent by your Food and Beverage Staff via the ForeTees system. " +
                   "Should you prefer not to receive these email messages, please contact your Club Staff at ";

  final public static String[] DAYS_OF_WEEK = new String[] {
                      "Sunday",
                      "Monday",
                      "Tuesday",
                      "Wednesday",
                      "Thursday",
                      "Friday",
                      "Saturday"
                    };
  
  final public static String RESERVATION_DATE_FORMAT = "EEEE M/d/yyyy";
  final public static String RESERVATION_TIME_FORMAT = "h:mm aaa";
  final public static String RESERVATION_DATE_TIME_FORMAT = "EEEE M/d/yyyy h:mm aaa";
  final public static String RESERVATION_DATE_TIME_SHORT_FORMAT = "M/d/yyyy at h:mm aaa";

  
  final public static String PROTOCOL_HTTP = "http://";
  final public static String PROTOCOL_HTTPS = "https://";
  final public static String FORWARD_SLASH = "/";
  final public static String UNDERSCORE = "_";
  
  public static boolean isAdminUser(String user)
  {

    if (user.startsWith(ADMIN))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

  public static boolean isProshopUser(String user)
  {

    if (user.startsWith(PROSHOP))
    {
      return true;
    }
    else
    {
      return false;
    }

  }

    public static boolean isDevServer() {
        return isDevServer(SERVER_ID);
    }
    public static boolean isDevServer(int server_id) {
        return server_id == DEV_SERVER_ID;
    }
    
    public static boolean isTimer1Server() {
        return isTimer1Server(SERVER_ID);
    }
    public static boolean isTimer1Server(int server_id) {
        return server_id == TIMER1_SERVER_ID;
    }
    
    public static boolean isTimer2Server() {
        return isTimer2Server(SERVER_ID);
    }
    public static boolean isTimer2Server(int server_id) {
        return server_id == TIMER2_SERVER_ID;
    }
    
    public static boolean isTestingServer() {
        return isTestingServer(SERVER_ID);
    }
    public static boolean isTestingServer(int server_id) {
        return (server_id >= TESTING_SERVER_ID && server_id <= TESTING_SERVER_END_ID);
    }
    
    public static boolean isStagingServer() {
        return isStagingServer(SERVER_ID);
    }
    public static boolean isStagingServer(int server_id) {
        return (server_id >= STAGING_SERVER_ID && server_id <= STAGING_SERVER_END_ID);
    }
    
    public static boolean isPureProductionServer() {
        return (!isDevServer() && !isTimer1Server() && !isTimer1Server() && !isTestingServer() && !isStagingServer());
    }
    
    public static boolean isProductionStagingServer() {
        return (!isDevServer() && !isTimer1Server() && !isTimer1Server() && !isTestingServer());
    }
    
    public static boolean isProductionStagingTestingServer() {
        return (!isDevServer() && !isTimer1Server() && !isTimer1Server());
    }
    
    private static String getServerUrl() {
        return getServerUrl(SERVER_ID);
    }

    private static String getServerUrl(int server_id) {
        if(isDevServer(server_id)){
            return DEV_SERVER_BASE_URL; 
        } else if (isTestingServer(server_id)){
            return TESTING_SERVER_BASE_URL;
        } else if (isStagingServer(server_id)){
            return STAGING_SERVER_BASE_URL;
        } else {
            // We'll return production URL for timer servers as well.
            return PRODUCTION_SERVER_BASE_URL;
        }
    }
    
    // Used in XML/json output to calendar -- do not change!
    public static String getEventGender(int event_gender){
        switch (event_gender){
            case EVENT_GENDER_MIXED:
                return "mixed";
            case EVENT_GENDER_MENS:
                return "male";
            case EVENT_GENDER_WOMENS:
                return "female";
            default:
                return "uncategorized";
        }
    }
    
    // Used in fortees select lists. (or, will be used.)
    public static String getEventGenderText(int event_gender){
        switch (event_gender){
            case EVENT_GENDER_MIXED:
                return "Mixed";
            case EVENT_GENDER_MENS:
                return "Mens";
            case EVENT_GENDER_WOMENS:
                return "Womens";
            default:
                return "Uncategorized";
        }
    }
    

/**
 //************************************************************************
 //
 //  isHoliday
 //
 //      This method provides a common mechanism to check the date for a holiday.
 //
 //      Returns:  the name of the holiday, or empty string if none
 //
 //              memday          Memorial Day (last Monday in May)
 //              gfriday         Good Friday
 //              july4           July 4th (actual)
 //              july4other      July 4th Practiced (could be a Monday or Friday near the 4th)
 //              laborday        Labor Day (first Monday of Sept)
 //              columbusday     Columbus Day (always 10/12)
 //              coldayother     Columbus Day Practiced (??)
 //              thanksgiving    Thanksgiving Day (Thurs)
 //
 //************************************************************************
 **/

 public static String isHoliday(long date) {

   String holiday = "";
   
   int year = 0;
   int hdate = 0;
   int i = 0;
   int i2 = 0;
   int max = 12;         // # of years specified in arrays
   
   //
   //  Arrays containing the holiday dates for the next several years (update this regularly!!!) - see special dates below too!!
   //
   //  Specify all values in order so the same index can be used across all arrays.
   //
   int [] yearsA = { 2009, 2010, 2011, 2012, 2013, 2014, 2015, 2016, 2017, 2018, 2019, 2020 };          

   int [] memdayA = { 525, 531, 530, 528, 527, 526, 525, 530, 529, 528, 527, 525 };     // Memorial Day 2009 - 2020     

   int [] gfridayA = { 410, 402, 422, 406, 329, 418, 403, 325, 414, 330, 419, 410 };    // Good Friday 2009 - 2020      

   int [] labordayA = { 907, 906, 905, 903, 902, 901, 907, 905, 904, 903, 902, 907 };    // Labor Day 2009 - 2020          

   int [] thanksgivingA = { 1126, 1125, 1124, 1122, 1128, 1127, 1126, 1124, 1123, 1122, 1128, 1126 };    // Thanksgiving Day 2009 - 2020

     
   //
   //  Process the date received - check for a holiday based on the year
   //
   year = (int)(date / 10000);             // isolate the year
   
   hdate = (int)(date - (year * 10000));   // isolate the mmdd
   
   loop1:
   while (i < max) {          // locate the year to determine the index for holiday arrays
      
      if (year == yearsA[i]) {
         
         break loop1;          // exit - use this index
      }
      i++;
   }
   
   //
   //  Check if date matches any holidays
   //
   if (i < max) {         // if we found a matching year
      
      i2 = 0;  
      
      while (i2 < max && holiday.equals("")) {       

         if (hdate == memdayA[i]) {

            holiday = "memday";
         }
         i2++;
      }
   
      i2 = 0;  
      
      while (i2 < max && holiday.equals("")) {       

         if (hdate == gfridayA[i]) {

            holiday = "gfriday";
         }
         i2++;
      }
   
      i2 = 0;  
      
      while (i2 < max && holiday.equals("")) {       

         if (hdate == labordayA[i]) {

            holiday = "laborday";
         }
         i2++;
      }
   
      i2 = 0;  
      
      while (i2 < max && holiday.equals("")) {       

         if (hdate == thanksgivingA[i]) {

            holiday = "thanksgiving";
         }
         i2++;
      }
      
      if (holiday.equals("")) {
   
         if (hdate == 704) {

            holiday = "july4";             // 4th of July (actual)
         }
         
         if (date == 20090703 || date == 20100705 || date == 20110705 || date == 20150703 || date == 20160705 || date == 20200703) {

            holiday = "july4other";      // other July 4th day (Monday or Friday)
         }
         
         if (hdate == 1012) {

            holiday = "columbusday";
         }
         
         if (date == 20091013 || date == 20151013 || date == 20201013) {

            holiday = "coldayother";     // Columbus Day - other
         }         
      }
   }
   
    
   return(holiday); 
 }
 
}
