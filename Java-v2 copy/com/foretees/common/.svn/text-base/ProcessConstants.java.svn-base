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
  final public static long memDay = 20140526;         // Memorial Day   ********** Change these each Year *************
    
  final public static long gFriDay = 20140418;         // Good Friday
    
  final public static long july4 = 20140704;          // 4th of July - Monday or Friday (only when 7/04 is on or near Monday or Friday)
  final public static long july4b = 20140704;         // 4th of July - Actual

  final public static long laborDay = 20140901;       // Labor Day

  final public static long colDay = 20141013;         // Columbus Day (always 10/12)
  final public static long colDayObsrvd = 20141013;   // Columbus Day Observed (Monday)

  final public static long tgDay = 20141127;          // Thanksgiving Day

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
  final public static int SERVER_ID = Integer.parseInt(props.getProperty("server_id"));
    
  //
  //  user types
  //
  final public static String ADMIN = "admin";
  final public static String PROSHOP = "proshop";
  final public static String MEMBER = "member";


  //
  //  codebase
  //
  final public static String CODEBASE = "/v5/";   // NOTE:  see also SystemUtils & com/foretees/event/add_event.jsp
  final public static String REV = "v5";
  
  final public static int DINING_ACTIVITY_ID = 9999;          // use this to identify the dining system and users
  final public static int ALL_ACTIVITIES = 999;               // use this to identify all activities
  
  final public static int CONNECT_ID = 8000;                  // use this to identify FT Connect activities (Home page)
  final public static int CONNECT_DIRECTORY_ID = 8001;        // FT Connect Directory Tab
  final public static int CONNECT_STATEMENTS_ID = 8002;       // FT Connect Statements Tab
  final public static int CONNECT_NEWSLETTERS_ID = 8003;      // FT Connect Newsletters Tab
  
  final public static String DINING_USER = "proshopautofb";    // Dining Admin credentials to link over to ForeTees for configuration
  final public static String DINING_PW = "foretees";


  // ForeTees Connect Premier
  final public static String FT_PREMIER_CALLER = "FLEXWEBFT";
  
  // ForeTees "app_mode" bits (from 0 (lsb) to 31 (msb))
  // Sent as integer in url /[club]_[activity]_m[app_mode]/
  final public static int APPMODE_HIDE_TOP_NAV = 0;
  final public static int APPMODE_HIDE_SUB_NAV = 1;
  final public static int APPMODE_HIDE_HOME_LINKS = 2;
  
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLE2 = 100;
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLEADMIN = 101;
  final public static int SCRIPT_MODE_PROSHOP_EDITOR_IFRAME = 102;
  final public static int SCRIPT_MODE_PROSHOP_HEADTITLEEDITOR = 103;
  final public static int SCRIPT_MODE_PROSHOP_TRANSITIONAL = 104;
  final public static int SCRIPT_MODE_NEWSKIN = 0;
  final public static int SCRIPT_MODE_RWD = 1; // Responsive

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
