
/***************************************************************************************
 *   Utilities:  Contains common methods used throughout the ForeTees/FlxRez Application
 *
 *   created: 01-19-09   Brad K.
 *
 *   last updated:
 *
 *               6/04/14  Added checkFTannouncement to check for a ForeTees announcement for this date.
 *               4/21/14  Add club name to all error log entries.
 *               4/16/14  Added getDateDiff method to provide an easy way to get an "index" value in places where the slotParms.ind value can't be trusted (could be a high # when coming from calendar/list/search).
 *               4/11/14  Added isPosPayNow method to be used to determine if the Pay Now feature is being used, and if the advanced options are enabled.
 *               4/03/14  Added isFTCPflexidAllowed to be used to determine if the flexid field should be displayed when editing a member record.
 *               2/27/14  Added getEventIdFromName method.
 *               2/21/14  Added getCourseId and getTeeOptions for Interlachen custom for event signup question.
 *               2/06/14  Added isLotteryClub() to provide a common method for looking up whether a club uses lottery.
 *              12/04/13  Changed isAGCClub() to isClubInGroup() instead, and a group_name value will now be passed. This can be used to check if a club is part of a particular group_name in v5/clubs
 *               9/17/13  Added isSubTypeClub to determine if the club uses the member sub_types.
 *               9/12/13  Updated isFTCPclub to use a different method of determining if a client is a Premier client.
 *               9/11/13  Added isFTAdminUser to determine whether the current user is logged in with a ForeTees SuperAdmin account.
 *               9/11/13  Added isFTCPclub to determine whether or not a club is a Premier client.
 *               8/31/13  Added getRecurOption to get the Recurrence options from the lottery specified.
 *               8/20/13  Corrected comments in isHandicapSysConfigured.
 *               7-17-13  Added overloaded createEvengLogEntry method call to allow an email_suppressed indicator to be passed optionally (defaults to N/A if not).
 *               5-29-13  Update checkRests to check for customs to lift restrictions.
 *               4-23-13  Added isFTCPuser methods for detecting ForeTees Connect Premier users
 *               4-18-13  Removed dovecanyonclub from isAGCClub().
 *               3-25-13  Added lomassantafecc to isAGCClub().
 *               2-05-13  Added getGuestTrackingGsts to get all the guest types that require guest tracking.
 *               1-30-13  Added get_ms_timestamp method
 *               1-03-13  Added getLessonBioPageFileName method
 *               1-03-13  Added getExternalBaseUrl method
 *              12-21-12  Changed getNameDataFromMemNum to use the username rather than member number (needed for Desert Mountain - Oly Club can use either).
 *              11-26-12  Added getSubtypeFromUsername.
 *              11-08-12  Added getMemeditOpt to get the memedit setting in an event.
 *              11-07/12  Added getConfNum for GolfSwitch interface - builds and saves a tee time confirmation number.
 *              10-14-12  Added validateRequestedActivity method for validiating requested activities and a club's configuration.
 *              10-02-12  Added checkTees() method to check a particular course to make sure it has tees configured for it.
 *               9-25-12  Added checkMemberAccess() method to check both the checkMshipAccess and checkMtypeAccess methods from a single method call.
 *               9-25-12  Added checkMtypeAccess() method to check if a particular mtype exists and should have access to the system.
 *               9-15-12  Added getMins method to get the minutes between two time values (for verifySlot.checkInUseN).
 *               9-08-12  Added getCustomStyles method to check for custom style sheet.
 *               8-30-12  Added getClubDbName() method
 *               8-23-12  Added checkMshipAccess() method to check if a particular mship should have access to a specific activity_id.
 *               7-11-12  Updated isHandicapSysConfigured to check for anything except 'Other'.
 *               5-23-12  Updated getProShortNames so that it's also passed an activity_id, and only returns pro short names for that activity.
 *               5-10-12  Winged Foot (wingedfoot) - Updated guest custom with additional guest types (case 1096).
 *               3-14-12  Added clubUsesSeamless method
 *               1-20-12  Added isHandicapSysConfigured & isLessonBookConfigured methods
 *               1-13-12  Added isNotificiationClub method
 *              12-20-11  Added new methods for use with the new event category feature: getEventCategoryNameFromId, buildEventCategoryIdList, buildEventCategoryNameList, buildEventCategoryListFromReq,
 *                        checkEventCategoryBindings, updateEventCategoryBindings (case 2076).
 *              11-10-11  Added isNewSkinActive method
 *              10-26-11  Added logDebug methods to print debug messages to the debuglog table in v5.
 *              10-14-11  Updated getNameDataFromMemNum() method to only look at active members.
 *              09-14-11  Updated getDayNameFromDate method to subtract 1 from month value when setting calendar date, since the calendar object uses months 0-11.
 *              09-06-11  Added titleCase method
 *              07-19-11  Updated checkWingedFootGuestTypes() custom to include '350rule/peer' guest type to not be counted towards member guest quotas (case 1096).
 *              07-13-11  Added an overloaded method call for adjustTime to allow it be passed a specific time-zone string, or a connection object to look up the time-zone from the club database.
 *              06-16-11  logErrorTxt - added date/time and server id to the error message.
 *                        Added another logError method to allow caller to pass an existing connection to v5 (saves another connect).
 *              05-11-11  Added isWeekend() method to return whether or not a given date is on a weekend or weekday.
 *              05-11-11  Added isEvenDate() method to return whether or not a given date is an even or odd numbered day.
 *              03-12-11  Added buildPlayerString, parseInt, createEventLogEntry methods
 *              02-18-11  Added getPersonId method (returns the person_id from ForeTees Dining for a particular member)
 *              02-16-11  Added getExtMainTop to add a header for external login users (entry from email links).
 *              01-27-11  Fixed an issue with checkRests() not clearing the ArrayLists of mships and mtypes for each restriction.
 *              01-19-11  Added checkRests() method to return whether or not a given mship/mtype are restricted on a specific date/time/course/fb.
 *              01-17-11  Added getDateString() method to return the current date in a string format using a specified separator between mm/yy/dd values.
 *              01-17-11  Added getOrganizationId() method to return the organization id for the current club (corresponds to their org id in the dining system)
 *              01-07-11  Los Altos G&CC (lagcc) - Added custom processing to swapNameOrder() method to strip middle initials from member display names (case 1926)
 *              09-22-10  Added getDateFromYYYYMMDD method - pass it an date in yyyymmdd returns a formated mm/dd/yyyy format - expand this later
 *              09-22-10  Added getEventName method - pass it an event_id and it returns the name
 *              09-17-10  Added isHotelUser method - returns true if user is a hotel user, false if not
 *              09-15-10  Added isStaffListEmpty method - returns whether or not staff list contains at least 1 entry w/ email address for a given activity_id
 *              08-10-10  Added ecryptTTdata and decryptTTdata methods
 *              08-06-10  Added isAGCClub method to return whether or not the given club is an American Golf club
 *              07-20-10  Added getProShortNames (case 1555).
 *              07-12-10  Added buildActivityConsecList() method to take a csv String of consec options and return an ArrayList of the options
 *              06-24-10  Added enableAdvAssist method.
 *              06-16-10  isGuestTrackingNameRequired() changed to isGuestTrackingTbaAllowed() and processing updated.
 *              06-04-10  Added isGuestTrackingNameRequired() method to provide a common utility method to see if the name field is required or optional
 *              04-19-10  Added isGuestTrackingConfigured() method to provide a common utility method to check if guest tracking is active
 *              04-13-10  All - added method to get the course names and return in an arraylist so we can support
 *                              an unlimited number of courses.
 *              03-27-10  Winged Foot - add 2010 filters to guests in checkWingedFootGuestTypes (case 1096).
 *              02-04-10  Add getmNum to get a member's member number.
 *              12-02-09  Added adjustTime (moved from sendEmail).
 *              12-01-09  Update getDate to allow for Saudi time (+8 or +9 hours).
 *              10-28-09  Added new getDate method that excepts an offset
 *              10-09-09  Added trimDoubleSpaces() method to trim all double spaces from a string
 *              10-08-09  Added isFTProshopUser() method to return whether or not the user is a ForeTees proshop user with unrestricted access
 *              09-04-09  Added logError / logErrorTxt methods
 *              09-02-09  Added isProOnlyTmode method to check if a MoT is pro only or not
 *              08-27-09  Check for empty player in checkWingedFootGuestTypes.
 *              07-16-09  Added 'Golf Panelist' guest type to checkwingedFootGuestTypes() filtering (case 1096).
 *              06-18-09  Added checkWingedFootGuestTypes() for use with wingedfoot guest checking (case 1096).
 *              06-16-09  Added urlEncode method for use in getELS
 *              05-01-09  Fix to swapNameOrder to account for more tokens in name
 *              04-23-09  Added swapNameOrder method to swap first & last names on the tee sheet
 *              04-19-09  Added getBirthValue method to convert birthdays into the correct format
 *              04-17-09  Added checkBirthday method to check if specified date matches a user's birthday
 *              04-14-09  Added toTitleCase for common use - properly capitalizes strings
 *              03-02-09  Added getELS method which converts username and club name to an encoded string
 *              02-19-09  Added isValidDate method to check validity of a certain date (i.e. Feb 31 does not exist, etc)
 *              02-13-09  Added getCustomDiningText method and added custom message checking to printDiningPrompt
 *              01-28-09  Added getHdcpNum and getGender methods
 *              01-19-09  Dining system check to see if diningh system is active and emails a present in the dining_emails table
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;
import java.util.*;
import java.text.*;
//import java.util.regex.*;
import java.sql.*;
import javax.servlet.http.*;
import org.apache.commons.lang.*;
/*
import javax.servlet.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
*/
import java.net.URLEncoder;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.mail.*;

//import com.foretees.api.ApiAccess;
import com.foretees.api.ApiCommon;
import com.foretees.api.ApiConstants;

//import com.foretees.common.Connect;

public class Utilities {
    
    
    static final String dynjs =      (ProcessConstants.SERVER_ID == 4) ? "dyn-search.js" : "dyn-search-20130131.js";

    private static String rev = ProcessConstants.REV;


    //**************************************************************************
    //  Method to verify that the dining system is active and emails are present
    //  in the dining_emails table.
    //
    //  Returns true if the system is active and one or more emails are present
    //**************************************************************************
    public static boolean checkDiningStatus (Connection con) {

        boolean status = false;

        Statement stmt = null;
        Statement stmt2 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;

        // if dining system is enabled then skip the other checks (there may be no emails configured if using club dining)
        if (getOrganizationId(con) != 0) {

            status = true;

        } else {

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT dining FROM club5");

                if (rs.next()) {

                    if (rs.getInt("dining") == 1) {

                        stmt2 = con.createStatement();
                        rs2 = stmt2.executeQuery("SELECT count(*) FROM dining_emails");

                        if (rs2.next()) {

                            if (rs2.getInt(1) > 0) {

                                status = true;
                            }
                        }
                    }
                }

            } catch (Exception exc) {

                status = false;

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}
                try { rs2.close(); }
                catch (Exception ignore) {}

                try { stmt.close(); }
                catch (Exception ignore) {}
                try { stmt2.close(); }
                catch (Exception ignore) {}

            }

        }

        return status;

    }   // end of checkDiningStatus


    //**************************************************************************
    //  Method to print the custom prompt and link for this club's dining request
    //  system.
    //**************************************************************************
    public static int printDiningPrompt (PrintWriter out, Connection con, String date, String day_name, String username, int playerCount, String caller, String params, boolean isProshop) {

        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        String promptText = "";
        String linkText = "";
        String url = "";

        int id = 0;
        int customId = 0;
        int organization_id = Utilities.getOrganizationId(con);

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT prompt_text, link_text FROM dining_config");

            if (rs.next()) {
                promptText = rs.getString("prompt_text");
                linkText = rs.getString("link_text");
            }

            stmt.close();

            pstmt = con.prepareStatement("SELECT id, prompt_text, link_text FROM dining_messages " +
                    "WHERE active=1 AND sdate <= ? AND edate >= ? AND " + day_name + "=1 AND " +
                    "(eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) " +
                    "ORDER BY priority DESC");
            pstmt.clearParameters();
            pstmt.setString(1, date);
            pstmt.setString(2, date);
            pstmt.setString(3, date);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");

                String temp = rs.getString("prompt_text");
                if (!temp.equals("")) {
                    customId = id;
                    promptText = temp;
                }
                temp = rs.getString("link_text");
                if (!temp.equals("")) {
                    customId = id;
                    linkText = temp;
                }
            }

            pstmt.close();

            params += "&caller=" + caller;

            if (customId != 0) {
                params += "&customId=" + customId;
            }
            if (playerCount != 0) {
                params += "&num=" + playerCount;
            }
            if (isProshop) {
                params += "&usr=" + username;
            }

            params += "&date=" + date;
/*
            if (organization_id == 0) {

                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt;\">");
                out.println("<tr><td align=\"center\">");
                out.println("<p>" + promptText);
                if (isProshop) {
                    out.println("<br><br><a href=\"Proshop_dining?dReq" + params + "\">" + linkText + "</a><br><br>");
                } else {
                    out.println("<br><br><a href=\"Member_dining?dReq" + params + "\">" + linkText + "</a>");
                }
                out.println("</td></tr></table><br><br>");

            } else {

                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt;\">");
                out.println("<tr><td align=\"center\">");
                out.println("<p>" + promptText);
                if (!isProshop) {
                    out.println("<br><br><a href=\"http://216.243.184.69/self_service/reservations/member_login?username=" + username + "&organization_id=" + organization_id + "&landing=dining\" target=\"_dining\">" + linkText + "</a>");
                }
                out.println("</td></tr></table><br><br>");

            }
*/
        } catch (Exception exc) {
            
            out.println("<HTML><!--Copyright notice:  This software (including any images, servlets, applets, photographs, animations, video, music and text incorporated into the software) " +
                    "is the proprietary property of ForeTees, LLC and its use, modification and distribution are protected " +
                    "and limited by United States copyright laws and international treaty provisions and all other applicable national laws. " +
                    "\nReproduction is strictly prohibited.-->\n" +
                    "<HEAD>" + //getBaseTag()  +
                    "<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees2.css\" type=\"text/css\">" +
                    "<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>" +
                    "<TITLE>DB Connection Error</TITLE></HEAD>\n");
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact customer support.");
            out.println("<BR><BR>");
            out.println("<a href=\"Proshop_announce\">Home</a>");
            out.println("</CENTER></BODY></HTML>");
            
        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        return customId;

    }   // end of printDiningPrompt

    //**************************************************************************
    //  Method to check whether or not a dining link should be displayed at a
    //  given location.  Also runs checkDiningStatus to verify that the dining
    //  system is active and there are emails present in dining_emails.
    //
    //  Returns true if link is to be displayed.
    //
    //  List of Types (contained in the 'type' parameter):
    //  pro_main - Proshop side - Maintop
    //  pro_teetime - Proshop side - Tee time confirmation page
    //  pro_lesson - Proshop side - Lesson book confirmation page
    //  mem_main - Member side - Maintop
    //  mem_teetime - Member side - Tee time confirmation page
    //  mem_lesson - Member Side - Lesson book confirmation page
    //  email_teetime - Email notifications for tee times
    //  email_lesson - Email notifications for lessons
    //
    //**************************************************************************
    public static boolean checkDiningLink (String type, Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        boolean status = false;

        try {
        
            if (checkDiningStatus(con)) {       // Only check further information if FT Dining or old dining system active and emails present

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT " + type + " FROM dining_config");

                if (rs.next()) status = (rs.getInt(type) == 1);

            }
        
        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return status;

    }  // End of checkDiningLink


    //**************************************************************************
    //  Method to check whether or not the club uses the Boxgroove Network and
    //  wants a link on the menu page.
    //
    //  Returns the username and password for the link if configured.
    //
    //**************************************************************************
    public static String checkBoxgroove (Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        int bg = 0;
        String boxUser = "";
        String boxPW = "";
        String boxgroove = "";

        try {
        
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT boxgroove, boxUser, boxPW FROM club5");

            if (rs.next()) {
                
                bg = rs.getInt("boxgroove");
                boxUser = rs.getString("boxUser");
                boxPW = rs.getString("boxPW");
            }

            if (bg == 1 && !boxUser.equals("") && !boxPW.equals("")) {
                
                boxgroove = boxUser+ "|" +boxPW;      // return both values
            }
        
        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return (boxgroove);

    }  // End of checkBoxgroove


    //**************************************************************************
    //  Method to check whether or not the club supports the Mobile Interface
    //
    //  Returns true if Mobile is supported.
    //
    //**************************************************************************
    public static boolean checkMobileSupport (Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        boolean status = false;

        try {

             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT allow_mobile FROM club5");

             if (rs.next()) status = (rs.getInt("allow_mobile") == 1);

        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return status;

    }  // End of checkMobileSupport
    
    
    //**************************************************************************
    //  Method to check whether or not the club supports RWD Member Select in Proshop
    //
    //  Returns true if Member Select in Proshop is supported.
    //
    //**************************************************************************
    public static boolean checkProshopHybridSlot (Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        boolean status = false;

        try {

             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT useProshopHybridSlot FROM club5");

             if (rs.next()) status = rs.getBoolean("useProshopHybridSlot");

        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return status;

    }  // End of useProshopHybridSlot


    /**
     * checkRests - Checks a given date/time/fb/course for restrictions to see if the specified mship/mtype are restricted for that time.
     *
     * @param date Date of tee time
     * @param time Time of tee time
     * @param fb Whether tee time is a front or back time
     * @param course Course the tee time is on
     * @param day Day of the week the tee time is on
     * @param mship Membership Type to check restrictions for
     * @param mtype Member Type to check restrictions for
     * @param con Connection to club database
     *
     * @return status - True if passed mship or mtype are restricted at the specified time, false otherwlse.
     * 
     * @throws Exception
     */
    public static boolean checkRests(long date, int time, int fb, String course, String day, String mship, String mtype, String club, Connection con)
             throws Exception {


       PreparedStatement pstmt1 = null;
       ResultSet rs = null;

       boolean status = false;
       boolean lifted = false;
   
       int mrest_id = 0;
       int memLimit = Labels.MAX_MEMS;
       int mshipLimit = Labels.MAX_MSHIPS;

       String rest_recurr = "";
       String rfb = "";
       String rest_name = "";

       ArrayList<String> mtypeS = new ArrayList<String>();             //  member types specified
       ArrayList<String> mshipS = new ArrayList<String>();           //  membership types specified

       //   Check for any events during this time
       rfb = "Front";
       if (fb == 1) {       // if back requested
          rfb = "Back";
       }

       //  get the Rests's for this date and time
       try {

          pstmt1 = con.prepareStatement (
             "SELECT * FROM restriction2 WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND " +
             "(courseName = ? OR courseName = '-ALL-') AND (fb = ? OR fb = 'Both')");

          pstmt1.clearParameters();          // clear the parms
          pstmt1.setLong(1, date);
          pstmt1.setLong(2, date);
          pstmt1.setInt(3, time);
          pstmt1.setInt(4, time);
          pstmt1.setString(5, course);
          pstmt1.setString(6, rfb);

          rs = pstmt1.executeQuery();      // find all matching restrictions, if any

          loop1:
          while (rs.next() && status == false) {

             mtypeS.clear();
             mshipS.clear();

             rest_name = rs.getString("name");
             rest_recurr = rs.getString("recurr");
             mrest_id = rs.getInt("id");

             for (int i=0; i<memLimit; i++) {
                mtypeS.add(rs.getString("mem" + (i+1)));
             }
             for (int i=0; i<mshipLimit; i++) {
                mshipS.add(rs.getString("mship" + (i+1)));
             }

             lifted = false; 

             //
             //  Check for any customs that lift this restriction x hours prior to the date requested
             //
             lifted = verifyCustom.checkRestLift(date, day, rest_name, club, 0, con); 

             if (lifted == false) {       // skip this rest if its been lifted 
             
               //  We must check the recurrence for this day (Monday, etc.)
               if ((rest_recurr.equals( "Every " + day )) ||          // if this day
                  (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                  ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                     (!day.equalsIgnoreCase( "saturday" )) &&
                     (!day.equalsIgnoreCase( "sunday" ))) ||
                  ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                     (day.equalsIgnoreCase( "saturday" ))) ||
                  ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                     (day.equalsIgnoreCase( "sunday" )))) {


                  //  Check for any suspensions
                  PreparedStatement pstmtSusp = con.prepareStatement(
                           "SELECT id FROM rest_suspend WHERE mrest_id = ? AND sdate <= ? AND edate >= ? " +
                           "AND stime <= ? AND etime >= ? AND " + day.toLowerCase() + " = 1 " +
                           "AND (eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) " +
                           "LIMIT 1");

                  pstmtSusp.clearParameters();
                  pstmtSusp.setInt(1, mrest_id);
                  pstmtSusp.setInt(2, (int)date);
                  pstmtSusp.setInt(3, (int)date);
                  pstmtSusp.setInt(4, time);
                  pstmtSusp.setInt(5, time);
                  pstmtSusp.setInt(6, (int)date);

                  ResultSet rsSusp = pstmtSusp.executeQuery();

                  if (rsSusp.next()) {

                     continue loop1;

                  } else {

                     //  Found a restriction that matches - check mtypes & mships
                     if (!mship.equals("")) {

                           loop2:
                           for (int i=0; i < mshipLimit; i++) {    // check each allowed mship type to see if it was specified

                              if (mshipS.get(i).equals(mship)) {
                                 status = true;
                                 break loop2;
                              }
                           }
                     }

                     if (!status && !mtype.equals("")) {         // if not all matched - check Mem types

                           loop3:
                           for (int i=0; i < memLimit; i++) {

                              if (mtypeS.get(i).equals(mtype)) {
                                 status = true;
                                 break loop3;
                              }
                           }
                     }
                  }

                  pstmtSusp.close();

               }     // end of 'day' if
               
             }       // end of IF lifted
             
          }       // end of while (no more restrictions)

          pstmt1.close();

       }
       catch (Exception e) {
           
           status = true;
           logError("Error looking up restrictions. Utilities.checkRests for club: " +club+ ", Date: " +date+ ", Time: " +time+ ", Course: " +course);
       }

       return(status);
     }
    
    public static boolean check5SomeRests (long date, int time, String course, int fb, String day, String club, HttpServletRequest req) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String rest_recurr = "";
        
        int fives = 0;
        
        boolean status = false;
        
        Connection con = Connect.getCon(req);
        
        
        // First make sure the club uses 
        try {
            
            pstmt = con.prepareStatement("SELECT fives FROM clubparm2 WHERE courseName = ?");
            pstmt.clearParameters();
            pstmt.setString(1, course);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                fives = rs.getInt("fives");
            }
            
        } catch (Exception e) {
            Utilities.logError("Utilities.check5SomeRests - " + club + " - Failed checking if course allows 5-somes - Error=" + e.toString());
        } finally {
            Connect.close(rs, pstmt);
        }
        
        // If this course allowed 5-somes, see if there's currently a 5-some restriction applying to this tee time
        if (fives == 1) {
            
            try {

                pstmt = con.prepareStatement("SELECT recurr FROM fives2 WHERE sdate <= ? AND edate >= ? AND stime <= ? AND etime >= ? AND (courseName = ? OR courseName = '-ALL-') "
                        + "AND (fb = ? OR fb = 'Both')");
                pstmt.clearParameters();
                pstmt.setLong(1, date);
                pstmt.setLong(2, date);
                pstmt.setInt(3, time);
                pstmt.setInt(4, time);
                pstmt.setString(5, course);
                pstmt.setInt(6, fb);

                rs = pstmt.executeQuery();

                while (rs.next()) {

                    rest_recurr = rs.getString("recurr");

                    // Check recurr setting to see if this 5some restriction applies
                    if (rest_recurr.equals("Every " + day) // if this day
                            || rest_recurr.equalsIgnoreCase("Every Day") // or everyday
                            || (rest_recurr.equalsIgnoreCase("All Weekdays") // or all weekdays (and this is one)
                            && !day.equalsIgnoreCase("Saturday") && !day.equalsIgnoreCase("Sunday"))
                            || (rest_recurr.equalsIgnoreCase("All Weekends") // or all weekends (and this is one)
                            && (day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday")))) {

                        status = true;
                        break;
                    }
                }

            } catch (Exception e) {
                Utilities.logError("Utilities.check5SomeRests - " + club + " - Failed while looking up 5-some restrictions - Error=" + e.toString());
            } finally {
                Connect.close(rs, pstmt);
            }
        }
        
        return status;        
    }



    // ********************************************************************
    //  Get custom dining text of the specified type and for the given id
    //
    //  Type must be one of the following values:
    //      form_text - text at top of dining form
    //      prompt_text - text displayed in emails and after booking a teetime/lesson
    //      link_text - link dipslayed along with prompt_text to take user to request form
    //
    // ********************************************************************
    public static String getCustomDiningText(String type, int id, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String customText = "";

        if (id != 0) {  // 0 is default
            try {
                pstmt = con.prepareStatement("SELECT " + type + " FROM dining_messages WHERE id = ? AND active=1");
                pstmt.clearParameters();
                pstmt.setInt(1, id);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    customText = rs.getString(type);
                }

                pstmt.close();

            } catch (Exception ignore) {

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }
        }

        return customText;
    }


 // ********************************************************************
 //  Get member hdcp number
 // ********************************************************************

 public static String getHdcpNum(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String hdcpNum = "";

    try {

        pstmt = con.prepareStatement (
                    "SELECT ghin FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) hdcpNum = rs.getString(1);

        pstmt.close();

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(hdcpNum);

 }   // end of getHdcpNum
 
 
 public static double getCourseHdcp(String user, HttpServletRequest req) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     Connection con = Connect.getCon(req);
     
     double c_hancap = 0;
     
     try {
         pstmt = con.prepareStatement("SELECT c_hancap FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, user);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             c_hancap = rs.getDouble("c_hancap");
         }
         
     } catch (Exception e) {
         Utilities.logError("Utilities.getCourseHdcp - Failed to look up course handicap for member (user = " + user + ") - Error = " + e.toString());
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return c_hancap;     
 }
 
 
 public static double getHdcpIndex(String user, HttpServletRequest req) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     Connection con = Connect.getCon(req);
     
     double g_hancap = 0;
     
     try {
         pstmt = con.prepareStatement("SELECT g_hancap FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, user);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             g_hancap = rs.getDouble("g_hancap");
         }
         
     } catch (Exception e) {
         Utilities.logError("Utilities.getCourseHdcp - Failed to look up handicap index for member (user = " + user + ") - Error = " + e.toString());
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return g_hancap;     
 }


 // ********************************************************************
 //  Get member Gender
 // ********************************************************************

 public static String getGender(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String gender = "";

    try {

        pstmt = con.prepareStatement (
                    "SELECT gender FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) gender = rs.getString(1);

        pstmt.close();

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(gender);

 }   // end of getGender



 /**
  * getmNum - Returns the member number for a given user.
  *
  * @param user String containing the username of the member to lookup
  *
  * @param con Connection to the club database
  *
  * @return memNum - The memNum corresponding to this member
  */
 public static String getmNum(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String mNum = "";

    try {

        pstmt = con.prepareStatement (
                    "SELECT memNum FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) mNum = rs.getString(1);

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(mNum);

 }   // end of getmNum


 /**
  * getUsernameFromPersonId - Returns the ForeTees username for a member.
  *
  * @param person_id int person id within the Dining system
  *
  * @param con Connection to the club database
  *
  * @return user - username of member in ForeTees, empty if not found
  */

 public static String getUsernameFromPersonId(int person_id, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String user = "";

    try {

        pstmt = con.prepareStatement (
                    "SELECT username FROM member2b WHERE dining_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, person_id);

        rs = pstmt.executeQuery();

        if (rs.next()) user = rs.getString(1);

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return( user );

 }   // end of getUsernameFromPersonId


 public static String getClubFromOrgId(int organization_id) {

     if (organization_id == 1) {

         return "demov4";

     } else if (organization_id == 2) {

         return "interlachen";

     } else {

         return "";

     }

 }
 
 public static boolean clubAllowRwd(HttpServletRequest req, String club) {
     
     if(ProcessConstants.SERVER_ID == 4){
         return true;
     } else {
         return false;
     }
     
 }
 
  public static boolean clubAllowRwdSwitch(HttpServletRequest req, String club) {
     /*
     if(ProcessConstants.SERVER_ID == 4){
         return true;
     } else {
         return false;
     }
       * 
       */
      return true;
     
 }


 /**
  * getOrganizationId - Returns the organization_id for this club.  If dining system is not in use, return an id of 0
  *
  * @param con Connection to the club database
  *
  * @return organization_id - The id corresponding to this club in the dining system, 0 if system not in use
  */
 public static int getOrganizationId(Connection con) {

     Statement stmt = null;
     ResultSet rs = null;

     int organization_id = 0;

     try {

         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT organization_id FROM club5");

         if (rs.next()) {
             organization_id = rs.getInt("organization_id");
         }

     } catch (Exception exc) {

         organization_id = 0;
       //logError("Utilities.getOrganizationId: Error looking up organization_id. Err=" + exc.getMessage() + ", strace=" + Utilities.getStackTraceAsString(exc));

     } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

     return organization_id;
 }


 /**
  * getPeopleId - Returns the people_id for a member.  If dining system is not in use, return an id of 0
  *
  * @param user String containing the username for a member
  * 
  * @param con Connection to the club database
  *
  * @return people_id - The id corresponding to this user in the dining system, 0 if system not in use or not found
  */

 public static int getPersonId(String user, Connection con) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    int people_id = 0;

    try {

        pstmt = con.prepareStatement (
                    "SELECT dining_id FROM member2b WHERE username = ?");

        pstmt.clearParameters();
        pstmt.setString(1, user);

        rs = pstmt.executeQuery();

        if (rs.next()) people_id = rs.getInt(1);

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return(people_id);

 }   // end of getPeopleId
 
 
  public static String getUserNameFromDiningId(int diningId, Connection con) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    String username = null;
    
    try {

        pstmt = con.prepareStatement (
                    "SELECT dining_id, username FROM member2b WHERE dining_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, diningId);

        rs = pstmt.executeQuery();

        if (rs.next()) username = rs.getString("username");

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return username;

 }   // end of getPeopleId


 public static int getUserId(String user, Connection con) {

     return getUserId( getPersonId(user, con) );

 }


 public static int getUserId(int person_id) {


    ResultSet rs = null;
    PreparedStatement pstmt = null;
    Connection con_d = null;

    int user_id = 0;

    //
    //  Lookup the user_id for a dining person
    //
    try {

        con_d = Connect.getDiningCon();

        if(con_d != null) {

            pstmt = con_d.prepareStatement (
                    "SELECT id " +
                    "FROM users " +
                    "WHERE person_id = ?");

            pstmt.setInt(1, person_id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) user_id = rs.getInt(1);

        }

    } catch (Exception e) {

        logError("Utilities.getUserId: Error looking up user_id for person_id #" +person_id+ ", Error: " + e.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

        try { con_d.close(); }
        catch (Exception ignore) {}

    }

    return user_id;

 }


 /**
  * Checks month & day combination to determine if it's a valid date (i.e. not Feb 30, April 31, etc)
  * @param month month of year
  * @param day day of month
  * @param year desired year
  * @return true if valid, false if invalid
  */

 public static boolean isValidDate(int month, int day, int year) {

     boolean result = false;

     //
     //  Num of days in each month
     //
     int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

     //
     //  Num of days in Feb indexed by year starting with 2000 - 2040
     //
     int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
             28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

     if (month != 0 && ((month != 2 && day <= numDays_table[month]) || (month == 2 && day <= feb_table[year - 2000]))) {
         result = true;
     }

     return result;
 }

 /**
  * isEvenDate - Returns true if date is even, false if odd
  *
  * @param date Date to check - yyyymmdd format
  *
  * @return result - True if even, false if odd
  */
 public static boolean isEvenDate(long date) {

     boolean result = false;

     if (date % 2 == 0) result = true;

     return result;
 }


 /**
  * isWeekend - Returns true if day is weekend, false if weekday
  *
  * @param day Day of week (String)
  *
  * @return result - True if weekend, false if weekday
  */
 public static boolean isWeekend(String day) {

     boolean result = false;

     if (day.equalsIgnoreCase("Saturday") || day.equalsIgnoreCase("Sunday")) result = true;

     return result;
 }


 public static String getELS(String club, String user) {

    String els = "";

    try {

        StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
        els = encrypter.encrypt( club + ":" + user );
        els = urlEncode( els );

    } catch (Exception e) {

        logError("Utilities.getELS Encrypt Error for club " +club+ ", User " +user+ ", Error: " + e.getMessage() );

    }

    return els;
 }
 
   /**
  * implode - Returns concatenated string of string array
  *
  * @param sArray Array<String> to implode
  * @param sep Separator
  *
  * @return result
  */
 
public static String implode (List<String> sArray, String sep) {
    return StringUtils.join(sArray.toArray(new String[sArray.size()]),sep);
}
 
  /**
  * implode - Returns concatenated string of string array
  *
  * @param sArray Array<String> to implode
  * @param sep Separator
  *
  * @return result
  */
 
public static String implode (String[] sArray, String sep) {
    return StringUtils.join(sArray,sep);
}


 //
 // Replace all the reserved chars with their encoded counterparts
 //
 private static String urlEncode( String els ) {

    els = els.replace("$", "%24");
    els = els.replace("&", "%26");
    els = els.replace("+", "%2B");
    els = els.replace(",", "%2C");
    els = els.replace("/", "%2F");
    els = els.replace(":", "%3A");
    els = els.replace(";", "%3B");
    els = els.replace("=", "%3D");
    els = els.replace("?", "%3F");
    els = els.replace("@", "%40");

    return els;
 }

 
 public static boolean checkUrlExists(URL url) {
     
     boolean exists = false;
     
     HttpURLConnection.setFollowRedirects(false);
     HttpURLConnection httpCon = null;
     
     try {
         httpCon = (HttpURLConnection) url.openConnection();
         httpCon.setRequestMethod("HEAD");
         exists = httpCon.getResponseCode() == HttpURLConnection.HTTP_OK;
     } catch (Exception e) {
         logError("Utilities.checkUrlExists - Error checking if file at URL exists (" + url.toString() + ") - Error: " + e.toString());
         exists = false;
     } finally {
         try { httpCon.disconnect(); }
         catch (Exception ignore) {}
     }
     
     return exists;
 }

 
 /**
  * Convert String input formatting to properly apply capitalization rules
  * @param s string to be converted
  * @return converted string
  */
 public final static String toTitleCase( String s ) {

      char[] ca = s.toCharArray();

      boolean changed = false;
      boolean capitalise = true;

      for ( int i=0; i<ca.length; i++ ) {
         char oldLetter = ca[i];
         if ( oldLetter <= '/'
              || ':' <= oldLetter && oldLetter <= '?'
              || ']' <= oldLetter && oldLetter <= '`' ) {
            /* whitespace, control chars or punctuation */
            /* Next normal char should be capitalized */
            capitalise = true;
         } else {
            char newLetter  = capitalise
                              ? Character.toUpperCase(oldLetter)
                              : Character.toLowerCase(oldLetter);
            ca[i] = newLetter;
            changed |= (newLetter != oldLetter);
            capitalise = false;
         }
      } // end for

      return new String (ca);

 } // end toTitleCase

 /**
  * Convert String input formatting to properly apply capitalization rules
  * @param s string to be converted
  * @return converted string
  */
 public static boolean checkBirthday( String user, long date, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean isBirthday = false;

     try {
         pstmt = con.prepareStatement("SELECT birth FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, user);
         rs = pstmt.executeQuery();

         if (rs.next()) {
             int birth = rs.getInt("birth");

             if (birth > 0) {
                 String sdate = String.valueOf(date);
                 String sbirth = String.valueOf(birth);

                 sdate = sdate.substring(4);
                 sbirth = sbirth.substring(4);

                 if (sdate.equals(sbirth)) {
                     isBirthday = true;
                 }
             }
         }

         pstmt.close();

     } catch (Exception ignore) {

         isBirthday = false;

     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

     }

     return isBirthday;

 } // end checkBirthday

 /**
 * Return appropriately formated birth value based on given input format
 * The passed runMode param corresponds to which formatting is being used.
 * The following runModes may be used:
 * @param 0: default: no birth provided, no action taken
 * @param 1: mmddyyyy or mddyyyy (i.e. 01121956 or 1121956)
 * @param 2: dd-Month-yy (i.e. 12-Jan-56)
 * @param 3: Month dd/yy (i.e. Jan 12/56)
 * @param 4: mm.dd.yy or mm/dd/yy or mm/dd/yyyy (i.e. 01.12.56 or 01/12/56 or 01/12/1956)
 * @return int - birth in yyyymmdd format
 */
 public static int getBirthValue(String temp, int runMode){

     int birth = 0;
     int mm = 0;
     int dd = 0;
     int yy = 0;

     switch(runMode){
         case 1:
              if (!temp.equals( "" )) {          // if birth provided

                birth = Integer.parseInt(temp);  // mddyyyy or mmddyyyy

                if (birth > 9999999) {         // if mmddyyyy

                   if (temp.startsWith( "10" )) {

                      mm = 10;
                      birth = birth - 10000000;    // strip month

                   } else {

                      if (temp.startsWith( "11" )) {

                         mm = 11;
                         birth = birth - 11000000;    // strip month

                      } else {

                         if (temp.startsWith( "12" )) {

                            mm = 12;
                            birth = birth - 12000000;    // strip month

                         } else {

                            birth = 0;
                         }
                      }
                   }

                } else {             // mddyyyy

                    char first = temp.charAt(0);

                    if (first >= 49 && first <= 57){    // first is a value 1-9 (ascii comparison)
                        mm = first;
                        int m = Integer.parseInt(String.valueOf(first));
                        m = m * 1000000;
                        birth -= m;
                    }
                }

                if (birth > 0) {                 // if still ok - get dd (now have ddyyyy)

                   dd = birth / 10000;

                   yy = birth - (dd * 10000);

                   birth = (yy * 10000) + (mm * 100) + dd;        // yyyymmdd
                }
             }

             break;
         case 2:
             if (!temp.equals( "" ) && !temp.equals( "0" )) {          // if birth provided

               StringTokenizer tok = new StringTokenizer( temp, " -" );

               if ( tok.countTokens() > 2 ) {                // "12-Jan-56"

                  temp = tok.nextToken();
                  dd = Integer.parseInt(temp);

                  temp = tok.nextToken();

                  if (temp.startsWith( "Jan" )) {
                     mm = 1;
                  } else {
                   if (temp.startsWith( "Feb" )) {
                      mm = 2;
                   } else {
                    if (temp.startsWith( "Mar" )) {
                       mm = 3;
                    } else {
                     if (temp.startsWith( "Apr" )) {
                        mm = 4;
                     } else {
                      if (temp.startsWith( "May" )) {
                         mm = 5;
                      } else {
                       if (temp.startsWith( "Jun" )) {
                          mm = 6;
                       } else {
                        if (temp.startsWith( "Jul" )) {
                           mm = 7;
                        } else {
                         if (temp.startsWith( "Aug" )) {
                            mm = 8;
                         } else {
                          if (temp.startsWith( "Sep" )) {
                             mm = 9;
                          } else {
                           if (temp.startsWith( "Oct" )) {
                              mm = 10;
                           } else {
                            if (temp.startsWith( "Nov" )) {
                               mm = 11;
                            } else {
                             if (temp.startsWith( "Dec" )) {
                                mm = 12;
                             } else {
                                mm = Integer.parseInt(temp);
                             }
                            }
                           }
                          }
                         }
                        }
                       }
                      }
                     }
                    }
                   }
                  }
                  temp = tok.nextToken();
                  yy = Integer.parseInt(temp);

                  if (yy < 10) {
                     yy = yy + 2000;
                  } else {
                     yy = yy + 1900;
                  }

                  birth = (yy * 10000) + (mm * 100) + dd;        // yyyymmdd
               }
            }
            break;
         case 3:
            if (!temp.equals( "" ) && !temp.equals( "0" )) {          // if birth provided

               StringTokenizer tok = new StringTokenizer( temp, " /" );

               if ( tok.countTokens() > 2 ) {                // "Jan 12/56"

                  temp = tok.nextToken();

                  if (temp.startsWith( "Jan" )) {
                     mm = 1;
                  } else {
                   if (temp.startsWith( "Feb" )) {
                      mm = 2;
                   } else {
                    if (temp.startsWith( "Mar" )) {
                       mm = 3;
                    } else {
                     if (temp.startsWith( "Apr" )) {
                        mm = 4;
                     } else {
                      if (temp.startsWith( "May" )) {
                         mm = 5;
                      } else {
                       if (temp.startsWith( "Jun" )) {
                          mm = 6;
                       } else {
                        if (temp.startsWith( "Jul" )) {
                           mm = 7;
                        } else {
                         if (temp.startsWith( "Aug" )) {
                            mm = 8;
                         } else {
                          if (temp.startsWith( "Sep" )) {
                             mm = 9;
                          } else {
                           if (temp.startsWith( "Oct" )) {
                              mm = 10;
                           } else {
                            if (temp.startsWith( "Nov" )) {
                               mm = 11;
                            } else {
                             if (temp.startsWith( "Dec" )) {
                                mm = 12;
                             } else {
                                mm = Integer.parseInt(temp);
                             }
                            }
                           }
                          }
                         }
                        }
                       }
                      }
                     }
                    }
                   }
                  }
                  temp = tok.nextToken();
                  dd = Integer.parseInt(temp);

                  temp = tok.nextToken();
                  yy = Integer.parseInt(temp);

                  if (yy < 10) {
                     yy = yy + 2000;
                  } else {
                     yy = yy + 1900;
                  }

                  birth = (yy * 10000) + (mm * 100) + dd;        // yyyymmdd
               }
            }
            break;
         case 4:
            if (!temp.equals( "" )) {          // if birth provided

               StringTokenizer tok = new StringTokenizer(temp, "." );   // mm.dd.yy
               if ( tok.countTokens() < 2 ) {
                   tok = new StringTokenizer(temp, "/");    // mm/dd/yy
               }
               if ( tok.countTokens() > 2 ) {

                  temp = tok.nextToken();
                  if (temp.length() == 1) {
                      temp = "0" + temp;
                  }
                  mm = Integer.parseInt(temp);
                  temp = tok.nextToken();
                  if (temp.length() == 1) {
                      temp = "0" + temp;
                  }
                  dd = Integer.parseInt(temp);
                  temp = tok.nextToken();
                  yy = Integer.parseInt(temp);

                  if (yy < 10) {
                     yy = yy + 2000;
                  } else if (yy < 100) {
                     yy = yy + 1900;
                  }

                  birth = (yy * 10000) + (mm * 100) + dd;        // yyyymmdd
               }
            }
            break;
         default:
            break;
     }

     return birth;
 }  // end of getBirthValue

 /**
  * Take a name in 'fname (mi) lname' format and return it in 'lname, fname (mi)' format
  * @param player string containing player name to convert
  * @param club string containing club name
  * @return player - name in correct format
  */
 public static String swapNameOrder(String club, String player){

     String lname = "";
     String fname = "";
     String mi = "";
     String suffix = "";
     String result = "";

     StringTokenizer tempTok = new StringTokenizer(player, " ");

     if (tempTok.countTokens() == 2) {

         fname = tempTok.nextToken();
         lname = tempTok.nextToken();

         if (club.equals("lagcc")) {
             result = fname + " " + lname;
         } else {
         result = lname + ", " + fname;
         }

     } else if (tempTok.countTokens() > 2) {

         fname = tempTok.nextToken();
         mi = tempTok.nextToken();
         lname = tempTok.nextToken();
         if (tempTok.countTokens() > 0) {
             suffix = tempTok.nextToken();
         }

         if (club.equals("mountvernoncc") && (lname.equals("SOC") || lname.equals("NR") || suffix.equals("SOC") || suffix.equals("NR"))) {

             if (!suffix.equals("")) {
                 result = lname + ", " + fname + " " + mi + " " + suffix;  // this means it's in the format John Doe SOC
             } else {
                 result = mi + ", " + fname + " " + lname;  // this means it's in the format John Doe SOC
             }
         } else if (club.equals("lagcc")) {

             result = fname + " " + lname;

         } else {
             result = lname + ", " + fname + " " + mi;
         }
     } else {

         result = player;
     }

     return result;
 }  // end of swapNameOrder


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
 // Returns a specific bit from an integer
 // 
 // 
 //
 //************************************************************************
 public static boolean getBit(int number, int bit){
     return ((number >> bit) & 1) == 1;
 }
 
 //************************************************************************
 //
 // Set or clear a specific bit from an integer
 // 
 //************************************************************************
 public static int setBit(int number, int bit){
     return setBit(number, bit, true);
 }
 
 public static int clearBit(int number, int bit){
     return setBit(number, bit, false);
 }
 
 public static int setBit(int number, int bit, boolean trueOrFalse){
     if(trueOrFalse){
         return number | (1 << bit);
     } else {
         return number & ~(1 << bit);
     }
 }
 
  //************************************************************************
 //
 // Gets bit from session attribute
 // 
 //************************************************************************
 public static boolean getBitFromSession(HttpServletRequest req, String param, int bit){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getBitFromSession(session, param, bit);
 }
 
 public static boolean getBitFromSession(HttpSession session, String param, int bit){
     boolean result = false;
     Object attr = null;
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             result = getBit((Integer) attr, bit);
         }
     }
     return result;
 }
 
   //************************************************************************
 //
 // Gets bit from request attribute
 // 
 //************************************************************************
 
 public static boolean getBitFromRequest(HttpServletRequest req, String param, int bit){
     boolean result = false;
     Object attr = null;
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             result = getBit((Integer) attr, bit);
         }
     }
     return result;
 }
 
  public static boolean setBitInRequest(HttpServletRequest req, String param, int bit){
     return setBitInRequest(req, param, bit, true);
 }
  
  public static boolean clearBitInRequest(HttpServletRequest req, String param, int bit){
     return setBitInRequest(req, param, bit, false);
 }
  
  public static boolean setBitInRequest(HttpServletRequest req, String param, int bit, boolean value){
     boolean result = false;
     int attr = getRequestInteger(req,param,0);
     if(req != null){
         attr = setBit((Integer) attr, bit, value);
         req.setAttribute(param, attr);
     }
     return result;
 }
 

 //************************************************************************
 //
 // Returns a formated string from passed in military time
 //
 //************************************************************************
 public static String getSimpleTime(int time) {

    return getSimpleTime(time / 100, time - ((time / 100) * 100));

 }


 //************************************************************************
 //
 // Returns a formated string from passed in parts
 //
 //************************************************************************
 public static String getSimpleTime(int hr, int min) {

    String ampm = " AM";

    if (hr == 12) ampm = " PM";
    if (hr > 12) { ampm = " PM"; hr = hr - 12; }    // convert to conventional time
    if (hr == 0) hr = 12;

    return hr + ":" + ensureDoubleDigit(min) + ampm;

 }
 
 public static String get24HourTime(int time) {

     if(time < 0) {
         return "";
     } else {
        return get24HourTime(time / 100, time - ((time / 100) * 100));
     }

 }
 
  public static String get24HourTime(int hr, int min) {

        return Utilities.ensureDoubleDigit(hr) + ":" + Utilities.ensureDoubleDigit(min);

  }


 //************************************************************************
 //
 // Accepts time parts from user input (12hr with AM/PM indicator)
 // and returns our common time integer in 24hr format
 //
 //************************************************************************
 public static int getIntTime(int hr, int min, String ampm) {

    int time = 0;

    if ((ampm.equals("PM") || ampm.equals("12")) && hr != 12) hr = hr + 12;
    if ((ampm.equals("AM") || ampm.equals("00")) && hr == 12) hr = 0;
    time = hr * 100;
    time = time + min;

    return time;

 }
 
 
    public static int getIntTime(String time) {

        String stime = time.toUpperCase().trim();

        if (stime.matches("^[0-9]{1,2}:[0-9]{1,2} {0,1}(AM|PM)$")) {
            // AM/PM time
            String ampm = stime.matches("AM$") ? "AM" : "PM";
            String itime = stime.replace(" {0,1}(AM|PM)$", "");
            String[] timea = itime.split(":");
            int hr = 0;
            int min = 0;
            try {
                hr = Integer.parseInt(timea[0]);
                min = Integer.parseInt(timea[1]);
            } catch (Exception ignore) {
                //
                hr = 0;
                min = 0;
            }

            return getIntTime(hr, min, ampm);

        } else if (stime.matches("^[0-9]{1,2}:{0,1}[0-9]{1,2}$")) {
            // Military time with or without ":"
            String tmp = stime.replace(":", "");
            int timei = 0;
            try {
                timei = Integer.parseInt(tmp);
            } catch (Exception ignore) {
                /* */
                timei = 0;
            }
            return timei;
        } else {
            // Invalid or empty time
            return -1;
            
        }
    }

    
    public static int getIntDate(String date) {

        String sdate = date.toUpperCase().trim();
        
        int year = 0;
        int month = 0;
        int day = 0;

        if (sdate.matches("^[0-9]{1,2}(/|-)[0-9]{1,2}(/|-)[0-9]{4}$")) {
            // MM/DD/YYYY
            String[] timea = sdate.split("/|-");
            
            try {
                month = Integer.parseInt(timea[0]);
                day = Integer.parseInt(timea[1]);
                year = Integer.parseInt(timea[2]);
                
            } catch (Exception ignore) {
                //
                month = 0;
                day = 0;
                year = 0;
            }
            
            return (year * 10000) + (month * 100) + day;

        } else if (sdate.matches("^[0-9]{4}(/|-)[0-9]{1,2}(/|-)[0-9]{1,2}$")) {
            // YYYY/MM/DD
            String[] timea = sdate.split("/|-");
            
            try {
                year = Integer.parseInt(timea[0]);
                month = Integer.parseInt(timea[1]);
                day = Integer.parseInt(timea[2]);
            } catch (Exception ignore) {
                //
                month = 0;
                day = 0;
                year = 0;
            }
            
            return (year * 10000) + (month * 100) + day;
            
        } else if (sdate.matches("^[0-9]{8}$")) {
            // YYYYMMDD
            int idate = 0;
            try {
                idate = Integer.parseInt(sdate);
            } catch (Exception ignore) {
                //
                idate = 0;
            }
            return idate;
        } else {
            return 0;
        }
        
        
        
    }
    

 //************************************************************************
 //
 // Returns a string containing a formal date and time
 //
 //************************************************************************
 public static String getLongDateTime(int date, int time, String seperator, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String sdate = "";
    String sql = "SELECT DATE_FORMAT(?, '%a %b %D, %Y') AS d";

    if (date == 0) sql = "SELECT DATE_FORMAT(now(), '%a %b %D, %Y') AS d";

    try {

        pstmt = con.prepareStatement ( sql );
        pstmt.clearParameters();
        if (date != 0) pstmt.setInt(1, date);
        rs = pstmt.executeQuery();
        if (rs.next()) sdate = rs.getString(1);

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return sdate + seperator + getSimpleTime(time);

 }


 //************************************************************************
 // getDate
 //
 //    Common method to get the current date (adjusted for time zone)
 //************************************************************************

 public static long getDate(Connection con) {

     return getDate(con, 0);
 }


 public static long getDate(Connection con, int dayOffset) {


   long date = 0;

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();          // get todays date
   cal.add(Calendar.DATE, dayOffset);               // apply the offset
   int year = cal.get(Calendar.YEAR);
   int month = cal.get(Calendar.MONTH);
   int day = cal.get(Calendar.DAY_OF_MONTH);
   int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
   int cal_min = cal.get(Calendar.MINUTE);

   if (con != null) {

       //
       //  Build the 'time' string for display
       //
       //    Adjust the time based on the club's time zone (we are Central)
       //
       int time = (cal_hourDay * 100) + cal_min;

       time = adjustTime(con, time);       // adjust for time zone

       if (time < 0) {                // if negative, then we went back or ahead one day

          time = 0 - time;          // convert back to positive value

          if (time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Araabia and others east of us)

             //
             // roll cal ahead 1 day (its now just after midnight, the next day - Eastern Time or Saudi)
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
   }
   
   month++;                            // month starts at zero
   date = (year * 10000) + (month * 100) + day;

   return(date);

 }  // end of getDate


 public static int getDate(int date, int dayOffset) {


    int year = date / 10000;
    int month = (date - (year * 10000)) / 100;
    int day = (date - (year * 10000)) - (month * 100);

    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);

    //
    // adjust day according to dayOffset value
    //
    cal.add(Calendar.DATE, dayOffset);

    year = cal.get(Calendar.YEAR);
    month = cal.get(Calendar.MONTH) + 1;
    day = cal.get(Calendar.DAY_OF_MONTH);

    return( (year * 10000) + (month * 100) + day );

 }  // end of getDate
 
 public static int getDateDiff(long date, Connection con) {
      
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int result = 0;
     
     try {
         
         pstmt = con.prepareStatement("SELECT DATEDIFF(?, curdate()) as date_diff");
         pstmt.clearParameters();
         pstmt.setLong(1, date);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             result = rs.getInt("date_diff");
         }         
         
         if (result < 0) {
             result = 0;
         }
         
     } catch (Exception exc) {
         logError("Utilities.getDateDiff - Error getting difference in dates. Date= " +date+ ", Err=" + exc.toString());
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return result;
 }

 
 public static String convertStringDate(String sDate, String inFormat, String outFormat){
    SimpleDateFormat inDate = new SimpleDateFormat(inFormat);
    SimpleDateFormat outDate = new SimpleDateFormat(outFormat);
    String sOutDate = null;
    try {
        sOutDate = outDate.format(inDate.parse(sDate));
    } catch (ParseException e) {
        // Do nothing
    }
    return sOutDate;
 }

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



 public static String getDateString(Connection con, int dayOffset, String separator) {


   String date = "";

   //
   //   Get current date and time
   //
   Calendar cal = new GregorianCalendar();          // get todays date
   cal.add(Calendar.DATE, dayOffset);               // apply the offset
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

      if (time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi Araabia and others east of us)

         //
         // roll cal ahead 1 day (its now just after midnight, the next day - Eastern Time or Saudi)
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
   date = year + separator + month + separator + day;

   return(date);
 }

 
 
 // *********************************************************
 //  determine # of minutes between two time values
 // *********************************************************

 public static int getMins(int timea, int timeb) {


   int hr1 = 0;
   int hr2 = 0;
   int min1 = 0;
   int min2 = 0;
   int mins = 0;
   int time1 = 0;
   int time2 = 0;
   
   
   //  make sure that time2 contains the higher time value
   
   if (timea > timeb) {
       
       time2 = timea;
       time1 = timeb;
       
   } else {
       
       time1 = timea;
       time2 = timeb;
   }

   //
   //  Calculate the number of minutes between time1 and time2
   //
   hr1 = time1 / 100;             // get hr
   min1 = time1 - (hr1 * 100);    // get minute

   hr2 = time2 / 100;             // get hr
   min2 = time2 - (hr2 * 100);    // get minute

   hr1 = hr2 - hr1;            
     
   if (hr1 > 0) {
     
      mins = hr1 * 60;               // get minutes
   }
   
   if (min2 > min1) {        

      mins = mins + (min2 - min1);   // get total minutes
        
   } else {

      min1 = min1 - min2;

      mins = mins - min1;           
   }

   return(mins);

 }   // end of getMins


 

 public static StringBuffer build_vEventCard(int teecurr_id) {

    StringBuffer vCalMsg = new StringBuffer();
/*
    String tmp_course = "";
    String tmp_time = date + "T" + ((time < 1000) ? "0" + time : time) + "00";

    etime = ehr + ":" + ensureDoubleDigit(emin) + eampm;

    if (!parms.course.equals("")) tmp_course = "Course: " + parms.course + "\\n";

    //String DTSTAMP = e_date + "T" + ensureDoubleDigit(e_hour) + ensureDoubleDigit(e_min) + ensureDoubleDigit(e_sec);

    // TODO: wrap descriptions at 75 bytes
    vCalMsg.append("" +
        "BEGIN:VCALENDAR\n" +
        "PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
        "METHOD:PUBLISH\n" +
        "BEGIN:VEVENT\n" +
        "DTSTAMP:" + DTSTAMP + "\n" +
        "DTSTART:" + tmp_time + "\n" +
        "SUMMARY:" + etime + " Tee Time\n" +
        "LOCATION:" + clubName + "\n" +
        "DESCRIPTION:" + tmp_course + players.replace("\n", "\\n") + "\n" +
        "URL:http://www1.foretees.com/" + club + "\n" +
        "END:VEVENT\n" +
        "END:VCALENDAR");
*/
    return vCalMsg;

 }

 /**
  * Consolidates the Winged Foot (wingedfoot) guest type checking to one location
  * @param player string containing guest type to check
  * @param mship string containing membership type of host member
  * @return error - true if player IS NOT one of the listed guest types, false if player IS one of the listed guest types
  */
 public static boolean checkWingedFootGuestTypes(String player, String mship) {

     boolean passed = false;

     if (!mship.equals("Non Resident") || !player.startsWith("family guest")) {  // do not count if Non Resident member with family guest

        if (!player.equals("") && !player.startsWith("Event Guest") && !player.startsWith("comp") &&
            !player.startsWith("single guest") && !player.startsWith("4pm guests") && !player.startsWith("wfgc guest day") &&
            !player.startsWith("WFGC Donations") && !player.startsWith("Golf Panelist") && !player.startsWith("275 guest") &&
            !player.startsWith("rule of 7 and 11") && !player.startsWith("350rule/peer") && !player.startsWith("child/spouse") && 
            !player.startsWith("east course guest")) {

            passed = true;
        }
     }

     return passed;
 }


 public static String get_mysql_timestamp(int date, int time) {

     return get_mysql_timestamp(date, time, 0);

 }

 
 public static String get_mysql_timestamp(int date, int time, int seconds) {

    String result = "";

    int hr = time / 100;
    int min = time - ((time / 100) * 100);

    int yy = date / 10000;
    int temp = yy * 10000;
    int mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    int dd = mm - temp;
    mm = mm / 100;

    result = "" + yy + "-" + mm + "-" + dd + " " + hr + ":" + min + ":" + seconds;

    return result;

 }
 
 /**
  * Returns a timestamp in "Wed Aug 12 hh:mm:ss:ms" format.
  * @param con Connection to club database
  * @return String
  */
 public static String get_ms_timestamp(Connection con) {
     
     String day_name = "";
     String mm_name = "";
     String sdate = "";

     String [] mm_table = { "inv", "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                          "Sep", "Oct", "Nov", "Dec" };

     String [] day_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

     //
     //  Get the current date/time
     //
     Calendar cal = new GregorianCalendar();        // get todays date
     int year = cal.get(Calendar.YEAR);
     int month = cal.get(Calendar.MONTH);
     int day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
     int daynum = cal.get(Calendar.DAY_OF_MONTH);
     int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);    // 24 hr clock (0 - 23)
     int cal_min = cal.get(Calendar.MINUTE);
     int cal_sec = cal.get(Calendar.SECOND);
     int cal_mil = cal.get(Calendar.MILLISECOND);

      //
      //    Adjust the time based on the club's time zone (we are Central)
      //
      int cal_time = (cal_hourDay * 100) + cal_min;     // get time in hhmm format

      cal_time = adjustTime(con, cal_time);             // adjust the time to club's time zone

      if (cal_time < 0) {          // if negative, then we went back or ahead one day

         cal_time = 0 - cal_time;        // convert back to positive value

         if (cal_time < 1200) {           // if AM, then we rolled ahead 1 day (allow for Saudi time)

            //
            // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
            //
            cal.add(Calendar.DATE,1);                     // get next day's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

         } else {                        // we rolled back 1 day

            //
            // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
            //
            cal.add(Calendar.DATE,-1);                     // get yesterday's date

            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            daynum = cal.get(Calendar.DAY_OF_MONTH);
            day_wk = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)
         }
      }

      month++;                                      // month starts at zero

      int cal_hour = cal_time / 100;                // get adjusted hour
      cal_min = cal_time - (cal_hour * 100);        // get minute value

      day_name = day_table[day_wk];         // get name for day

      mm_name = mm_table[month];             // get name for month


     long mdate = (year * 10000) + (month * 100) + daynum;         // date value for today (for sorting the records)
     
     
     //  Build a date and time string for the history entry (i.e.  Wed Aug 12 10:04:52:123 AM - 123 is milli-seconds)
     if (cal_hour < 10) {

        sdate = day_name + " " + mm_name + " " + daynum + " 0" + cal_hour;

     } else {

        sdate = day_name + " " + mm_name + " " + daynum + " " + cal_hour;
     }

     if (cal_min < 10) {

        sdate += ":0" + cal_min;

     } else {

        sdate += ":" + cal_min;
     }

     if (cal_sec < 10) {

        sdate += ":0" + cal_sec;

     } else {

        sdate += ":" + cal_sec;
     }


     if (cal_mil < 10) {

        sdate += ".00" + cal_mil;

     } else {

        if (cal_mil < 100) {

           sdate += ".0" + cal_mil;

        } else {

           sdate += "." + cal_mil;
        }
     }
     
     return (sdate);
 }

 /**
  * isProOnlyTmode - Returns true if the passed mode of transportation is pro only, false if not.
  *
  * @param tmode Mode of transportation to check
  * @param con Connection to club database
  *
  * @return proOnly - true if MoT is pro only, false otherwise
  */
 public static boolean isProOnlyTmode(String tmode, Connection con) {

     boolean proOnly = false;

     Statement stmt = null;
     ResultSet rs = null;

     try {
         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT " +
                 "tmodea1, tmodea2, tmodea3, tmodea4, tmodea5, " +
                 "tmodea6, tmodea7, tmodea8, tmodea9, tmodea10," +
                 "tmodea11, tmodea12, tmodea13, tmodea14, tmodea15, " +
                 "tmodea16, tOpt1, tOpt2, tOpt3, tOpt4, " +
                 "tOpt5, tOpt6, tOpt7, tOpt8, tOpt9, " +
                 "tOpt10, tOpt11, tOpt12, tOpt13, tOpt14, " +
                 "tOpt15, tOpt16 " +
                 "FROM clubparm2");

         if (rs.next()) {

             for (int i=0; i<Labels.MAX_TMODES; i++) {
                 if (tmode.equals(rs.getString("tmodea" + (i + 1))) && rs.getInt("tOpt" + (i + 1)) == 1) {
                     proOnly = true;
                     break;
                 }
             }
         }

         stmt.close();

     } catch (Exception exc) {

         proOnly = false;

     } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

     }

     return proOnly;
 }

 /**
  * isFTProshopUser - Simple method to return whether or not this is a ForeTees proshop user with unrestricted access, but keeps the names of these logins better hidden
  *
  * @param user Username of current user
  *
  * @return result - True if user is a ForeTees proshop user, false otherwise
  */
 public static boolean isFTProshopUser(String user) {

     boolean result = false;

     if (user.equals("proshop4tea")) {
         result = true;
     }

     return result;
 }

 /**
  * isFTProshopUser - Simple method to return whether or not this is a ForeTees admin user with unrestricted access, but keeps the names of these logins better hidden
  *
  * @param user Username of current user
  *
  * @return result - True if user is a ForeTees admin user, false otherwise
  */
 public static boolean isFTAdminUser(String user) {

     boolean result = false;

     if (user.equals("admin4tea")) {
         result = true;
     }

     return result;
 }
 
 
 /**
  * Returns whether the current proshop user should have access to ForeTees Connect management items.
  * @param user Username to be checked
  * @param req Request object for accessing a connection to the club's database
  * @return 
  */
 public static boolean isFTConnectProUser(String user, HttpServletRequest req) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean result = false;

     Connection con = Connect.getCon(req);
     
     if (isFTProshopUser(user)) {    // Always allow ForeTees Staff proshop users
         result = true;
     } else {

         try {
             pstmt = con.prepareStatement("SELECT username FROM login2 WHERE username = ? AND allow_connect = 1 LIMIT 1");
             pstmt.clearParameters();
             pstmt.setString(1, user);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 result = true;
             }


         } catch (Exception e) {
             logError("Utilities.isFTConnectProUser - Failed while checking for allow_connect status - Error: " + e.toString());
         } finally {
             Connect.close(rs, pstmt);
         }
     }

     return result;
 }
 
 /**
  * Returns whether or not the current club is using the ForeTees Connect setup
  * @param req Request object used to grab an open connection to the current club's database
  * @return int 0 = disabled, 1 = enabled, 2 = staging. Proshop functions should be allowed in staging mode, Member functions should not.
  */
 public static int isFTConnectClub(HttpServletRequest req) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     int result = 0;
     
     Connection con = Connect.getCon(req);
     
     if(con == null){
         return 0;
     }
     
     String club = getClubDbName(con);      // get club's site name
 
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT ftConnect FROM club5");
         
         if (rs.next()) {
             result = rs.getInt("ftConnect");
         }
         
     } catch (Exception e) {
         logError("Utilities.isFTConnectClub - Error checking ForeTees Connect club status - Club= " +club+ ", ERR: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
     } finally {
         
         Connect.close(rs, stmt);
     }
     
     return result;
 }

 /**
  * trimDoubleSpaces - Replaces all double spaces in a string with single spaces
  *
  * @param toTrim String to trim
  *
  * @return trimmed - Trimmed string
  */
 public static String trimDoubleSpaces(String toTrim) {

     String trimmed = toTrim;

     while (trimmed.contains("  ")) {
         trimmed = trimmed.replace("  ", " ");
     }

     return trimmed;
 }


 
  //****************************************************************************
 //
 //  adjustTime - adjust the current time based on the time zone for the club
 //
 //****************************************************************************
 public static int adjustTime(Connection con, int time) {
    /* 
      * 
   int date = timeUtil.getClubDate(null); // Get current server date (since it wasn't passed, we'll assume we're adjusting for today)
     int[] clubDateTime = timeUtil.serverToClubDateTimeFromCon(con, date, time);
     int new_time = clubDateTime[timeUtil.TIME];
     if(clubDateTime[timeUtil.DATE]  < date){
         return new_time *= -1;
     } else {
         return new_time;
     }
*/
   Statement stmt = null;
   ResultSet rs = null;

   int hour = 0;
   int min = 0;
   int result_time = 0;
   boolean roll = false;
   boolean DST = false;           // Day Light Savings

   String adv_zone = "";

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
   catch (Exception ignore) {
   }

   result_time = adjustTime(adv_zone, time);

   return result_time;

 }

 public static int adjustTime(String adv_zone, int time) {
/*     
     int date = timeUtil.getClubDate(null); // Get current server date (since it wasn't passed, we'll assume we're adjusting for today)
     int[] clubDateTime = timeUtil.serverToClubDateTimeFromTzId(adv_zone, date, time);
     int new_time = clubDateTime[timeUtil.TIME];
     if(clubDateTime[timeUtil.DATE]  < date){
         return new_time *= -1;
     } else {
         return new_time;
     }
*/
   int hour = 0;
   int min = 0;
   int result_time = 0;
   boolean roll = false;
   boolean DST = false;           // Day Light Savings

   //
   //  separate hour and min from time
   //
   hour = time / 100;                    // 00 - 23
   min = time - (hour * 100);            // 00 - 59

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


   //
   //  Arizona - no DST
   //
   if (adv_zone.equals( "Arizona" )) {      // Mountain or Pacific Time (no DST)

      DST = checkDST();                     // check if DST here in CT zone

      if (hour == 0) {

         if (DST == true) {
            hour = 22;          // adjust it -2 in summer
         } else {
            hour = 23;          // adjust it -1
         }
         roll = true;           // rolled back a day

      } else {

         if (hour == 1) {

            if (DST == true) {
               hour = 23;           // adjust it -2 in summer
               roll = true;         // rolled back a day
            } else {
               hour = 0;            // adjust it -1 (no roll back)
            }

         } else {                    // its 2:00 AM or later in CT zone

            if (DST == true) {
               hour = hour - 2;             // -2 in summer
            } else {
               hour = hour - 1;             // -1 in winter
            }
         }
      }
   }


   //
   //  Hawaii time - never goes to DST.  If our DST, then subtract 5 hrs, else -4.
   //
   if (adv_zone.equals( "Hawaiian" )) {      // Hawaiian Time = -4 or -5 hrs (no DST in Hawaii)

      DST = checkDST();                  // check if DST here

      if (hour == 0) {

         if (DST == true) {
            hour = 19;          // adjust it -5
         } else {
            hour = 20;          // adjust it -4
         }
         roll = true;        // rolled back a day

      } else {

         if (hour == 1) {

            if (DST == true) {
               hour = 20;          // adjust it -5
            } else {
               hour = 21;          // adjust it -4
            }
            roll = true;        // rolled back a day

         } else {

            if (hour == 2) {

               if (DST == true) {
                  hour = 21;          // adjust it -5
               } else {
                  hour = 22;          // adjust it -4
               }
               roll = true;        // rolled back a day

            } else {

               if (hour == 3) {

                  if (DST == true) {
                     hour = 22;          // adjust it -5
                  } else {
                     hour = 23;          // adjust it -4
                  }
                  roll = true;        // rolled back a day

               } else {

                  if (hour == 4) {

                     if (DST == true) {
                        hour = 23;          // adjust it -5
                        roll = true;        // rolled back a day
                     } else {
                        hour = 0;          // adjust it -4 (no roll back)
                     }

                  } else {

                     if (DST == true) {
                        hour = hour - 5;             // adjust the hour value
                     } else {
                        hour = hour - 4;             // adjust the hour value
                     }
                  }
               }
            }
         }
      }
   }


   //
   //  Saudi Arabia time (GMT + 3) - never goes to DST.  If our DST, then add 8 hrs, else add 9.
   //
   if (adv_zone.equals( "Saudi" )) {      // Saudi Time = +8 or +9 hrs (no DST)

      DST = checkDST();                  // check if DST here

      if (DST == true) {

         hour += 8;                   // DST - roll ahead 8 hours

      } else {

         hour += 9;                   // NOT DST - roll ahead 9 hours
      }

      if (hour == 24) {

         hour = 0;
         roll = true;                 // midnight the next day

      } else if (hour > 24) {

         hour = hour - 24;
         roll = true;                 // some time the next morning
      }

   }      // end of Saudi Arabia


   result_time = (hour * 100) + min;

   if (roll == true) {

      result_time = (0 - result_time);        // create negative value to indicate we rolled back one day
   }

   return( result_time );

 }  // end of adjustTime



 //************************************************************************
 //
 //  checkDST - Check if we are now in Daylight Savings Time
 //
 //************************************************************************

 public static boolean checkDST() {


   boolean DST = true;

   int sdate = 0;
   int edate = 0;

   //
   //   Get current date
   //
   Calendar cal = new GregorianCalendar();                      // get todays date
   int yy = cal.get(Calendar.YEAR);
   int mm = cal.get(Calendar.MONTH) +1;
   int dd = cal.get(Calendar.DAY_OF_MONTH);

   int date = (yy * 10000) + (mm * 100) + dd;      // get today

   //
   //  Determine start and end of Daylight Saving Time
   //
   if (yy == 2015) {

      sdate = 20150308;
      edate = 20151031;

   } else if (yy == 2016) {

      sdate = 20160313;
      edate = 20161105;

   } else if (yy == 2017) {

      sdate = 20170312;
      edate = 20171104;

   } else if (yy == 2018) {

      sdate = 20180311;
      edate = 20181103;

   } else if (yy == 2019) {

      sdate = 20190310;
      edate = 20191102;

   }

   if (date < sdate || date > edate) {      // if not DST today

      DST = false;
   }

   return( DST );

 }  // end of checkDST

 
 public static String getClubCorp(String club, Connection con) {

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //
        //  If MFirst and a ClubCorp club, then we must convert the name (i.e. clubcorp_132 = trophyclubcc)
        //

        if (club.startsWith("clubcorp_")) {       // if 'MembersFirst' and ClubCorp

            try {
                pstmt = con.prepareStatement(
                        "SELECT ft_name FROM clubcorp WHERE cc_name = ?");

                pstmt.clearParameters();        // clear the parms
                pstmt.setString(1, club);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    club = rs.getString(1);    // get our name for this club (if not found it will fail below)
                }

                pstmt.close();

            } catch (Exception exc) {
                //club = null;
            }
        }

        return club;
    }
 
 // Check if club exists
 public static String validateClub(String club){

    Connection con = null;                 // init DB objects
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {
       
      con = Connect.getCon(rev);       // get a connection for this version level
      
      club = getClubCorp(club, con);

      pstmt = con.prepareStatement (
               "SELECT fullname, inactive FROM clubs WHERE clubname = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, club);
      rs = pstmt.executeQuery();

      if (!rs.next()) {          // if club not found in this version
         club = null;
      }

      pstmt.close();              // close the stmt
      con.close();                // close the connection
      
   }
   catch (Exception exc) {
      club = null;
   } finally{
        Connect.close(rs, pstmt, con);
    }
    return club;
 }
 
 //  get the database name this con is for - used when 'club' is not available
 public static String getClubDbName(Connection con) {


    String club = "";

    Statement stmt = null;
    ResultSet rs = null;

    if (con != null) {
       
      try {

         stmt = con.createStatement();

         rs = stmt.executeQuery("SELECT DATABASE()");

         if (rs.next()) club = rs.getString(1);

      } catch (Exception e) {
         
         String error = e.getMessage();
         
         if (!error.endsWith("Connection is closed.")) {

             logError("Error getting database name - Utilities.getClubDbName " + error);
         }

      } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { stmt.close(); }
         catch (Exception ignore) {}

      }
    }

    return( club );

 }
 
 //************************************************************************
 //
 //  getClubName - get the full name of the club, from the request or cache it in the request
 //
 //************************************************************************

 public static String getClubName (HttpServletRequest req) {
     
     String clubName = reqUtil.getRequestString(req, "rqacache_club_name", null);
     
     if(clubName == null){
         clubName = getClubName (Connect.getCon(req),true);
         req.setAttribute("rqacache_club_name", clubName);
     }
     return clubName;
 }

 //************************************************************************
 //
 //  getClubName - get the full name of the club, do not throw exception
 //
 //************************************************************************

 public static String getClubName (Connection con, boolean no_exception) {
     
     String clubName = "";
         
        try {

            clubName = Utilities.getClubName(con);

        } catch (Exception exc) {
            
            String error = exc.getMessage();

            if (!error.endsWith("Connection is closed.") && !error.endsWith("null")) {
                logError("Utilities.getClubName: Error=" + exc.getMessage());
            }
        }
        
     
     return clubName;
     
 }

 //************************************************************************
 //
 //  getClubName - get the full name of the club
 //
 //************************************************************************

 public static String getClubName (Connection con)
         throws Exception {


    Statement stmt = null;
    ResultSet rs = null;

    String result = "";

    if (con != null) {
       
         try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubName FROM club5 WHERE clubName != ''");
            if (rs.next()) result = StringEscapeUtils.unescapeHtml(rs.getString(1)); // unfilter(rs.getString(1));  // convert any html special characters to normal

         } catch (Exception exc) {
            
                throw new Exception("Utilities.getClubName  (with filter): club=" + Utilities.getClubDbName(con) + ", result=" + result + ", Error=" + exc.getMessage());

         } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
         }
    }

    return result;
 }


 //************************************************************************
 //
 //  getCourseNames - get all the course names in the system
 //
 //************************************************************************

 public static ArrayList<String> getCourseNames (Connection con)
         throws Exception {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String> result = new ArrayList<String>();

    try {

        pstmt = con.prepareStatement("" +
                "SELECT courseName " +
                "FROM clubparm2 " +
                "WHERE courseName != '' " +
                "ORDER BY sort_by, clubparm_id");

        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        while ( rs.next() ) {

           result.add( rs.getString(1) );       // add course name to the list
        }

    } catch (Exception exc) {

        throw new Exception("Utilities.getCourseNames: " +
                            "Club=" + Utilities.getClubDbName(con) + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    return result;
 }


 /**
  *************************************************************************
  *
  *  getProShortNames - get all the course names in the system
  *
  *************************************************************************
  */

 public static ArrayList<String> getProShortNames (int activity_id, Connection con)
         throws Exception {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String> result = new ArrayList<String>();

    try {

        pstmt = con.prepareStatement("" +
                "SELECT short_name " +
                "FROM staff_list " +
                "WHERE activity_id = ? AND short_name != '' AND tee_time_list > 0 " +
                "ORDER BY short_name");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        rs = pstmt.executeQuery();

        while ( rs.next() ) {

           result.add( rs.getString(1) );       // add course name to the list
        }

    } catch (Exception exc) {

        throw new Exception("Utilities.getProShortNames: " +
                            "Club=" + Utilities.getClubDbName(con) + ", Error=" + exc.getMessage());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    return result;
 }

 /**
  * isStaffListEmpty - Determines whether or not staff members with email address have been added to the staff list for a given activity_id
  *
  * @param activity_id Activity_id to check for
  * @param con Connection to club database
  *
  * @return result - True if no staff members with emails are present for that activity_id, false otherwise
  */
 public static boolean isStaffListEmpty (int activity_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean result = true;

     try {

         pstmt = con.prepareStatement("SELECT staff_id FROM staff_list WHERE activity_id = ? AND (address1 <> '' OR address2 <> '') LIMIT 1");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);

         rs = pstmt.executeQuery();

         if (rs.next()) result = false;

     } catch (Exception ignore) {

         result = true;

     } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
    }

    return result;
 }


 /**
  * buildActivityConsecList - Returns an array list built out of the string of consec values
  *
  * @param consec_csv String of comma separated consec integers
  *
  * @return consec - ArrayList containing all consec options for mem/pro and this activity
  */

 public static ArrayList<Integer> buildActivityConsecList (String consec_csv) {

     ArrayList<Integer> consec = new ArrayList<Integer>();

     StringTokenizer tok = new StringTokenizer(consec_csv, ",");

     while (tok.hasMoreTokens()) {

         try {
             consec.add(Integer.parseInt(tok.nextToken()));
         } catch (Exception ignore) { }
     }

     return consec;
 }



 /**
  * isGuestTrackingConfigured - Returns whether or not the guest database system is configured for this activity
  *
  * @param activity_id Activity id to check under
  * @param con Connection to club database
  *
  * @return result - True if guest database system is configured for this activity id, false otherwise
  */
 public static boolean isGuestTrackingConfigured(int activity_id, Connection con) {

     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;

     boolean result = false;

     int guestdb = 0;

     try {

         // First check to see whether or not guestdb flag is toggled on in club5 or activities
         if (activity_id == 0) {

             stmt = con.createStatement();

             rs = stmt.executeQuery("SELECT guestdb FROM club5");

             if (rs.next()) {
                 guestdb = rs.getInt("guestdb");
             }

             stmt.close();

         } else if (activity_id > 0) {

             pstmt = con.prepareStatement("SELECT guestdb FROM activities WHERE activity_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, activity_id);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 guestdb = rs.getInt("guestdb");
             }

             pstmt.close();
         }

         // If the system is toggled on for this activity_id, then check to see if a guestdb entry exists for this activity_id
         if (guestdb == 1) {

             pstmt = con.prepareStatement("SELECT activity_id FROM guestdb WHERE activity_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, activity_id);

             rs = pstmt.executeQuery();

             // If found, then system is configured for this activity_id
             if (rs.next()) result = true;

             pstmt.close();
         }

     } catch (Exception exc) {
         result = false;
     }

     return result;
 }

 /**
  * isGuestTrackingTbaAllowed - Returns whether or not the 'TBA' option should be allowed when selecting a tracked guest during bookings.
  *
  * @param activity_id Activity_id to check under
  * @param isProshop true if proshop user, false if member
  * @param con Connection to club database
  *
  * @return result - True if 'TBA' is allowed, false if not.
  */
 public static boolean isGuestTrackingTbaAllowed(int activity_id, boolean isProshop, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     boolean result = false;     // Name is required by default

     try {

         pstmt = con.prepareStatement("SELECT allow_tba FROM guestdb WHERE activity_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             if ((isProshop && rs.getInt("allow_tba") >= 1) || (!isProshop && rs.getInt("allow_tba") == 2)) {
                 result = true;
             } else {
                 result = false;
             }
         }

         pstmt.close();

     } catch (Exception exc) {
         result = false;
     }

     return result;
 }


 /**
  * getGuestTrackingGsts - Returns the guest types that specify Guest Tracking for Members or Pros
  *
  * @param activity_id Activity_id to check under
  * @param isProshop true if proshop user, false if member
  * @param con Connection to club database
  *
  * @return result - True if 'TBA' is allowed, false if not.
  */
 public static ArrayList getGuestTrackingGsts(int activity_id, boolean isProshop, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     ArrayList<String> result = new ArrayList<String>();
     
     String guest = "";
     int gOpt = 0;

     try {

         pstmt = con.prepareStatement("SELECT guest, gOpt FROM guest5 WHERE activity_id = ? AND use_guestdb > 0 AND inactive = 0");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);

         rs = pstmt.executeQuery();

         while (rs.next()) {
             
             guest = rs.getString("guest");
             gOpt = rs.getInt("gOpt");
             
             if (isProshop == true || (isProshop == false && gOpt == 0)) {   // if we should add this guest type
               
                 result.add(guest);         // add the guest type
             }
         }
         pstmt.close();

     } catch (Exception exc) {
     }

     return result;
 }


 //************************************************************************
 //
 //  logError - logs system error messages to db using new con
 //
 //************************************************************************

 public static void logError(String msg) {

     Connect.logError(msg);

 }  // end of logError


 //************************************************************************
 //
 //  logError - logs system error messages to db using the con passed **** MUST be rev (v5) ***********
 //
 //************************************************************************

 public static void logError(String msg, Connection con) {

     Connect.logError(msg, con);
  
 }  // end of logError


 public final static void logErrorTxt(String msg, String club) {


     Connect.logErrorTxt(msg, club);

 }  // end of logErrorTxt


 //************************************************************************
 //
 //  logDebug - logs system debug messages to db using new con
 //
 //************************************************************************

 public static void logDebug(String initials, String msg) {
     
     Connect.logDebug(initials, msg);

 }  // end of logError


 //************************************************************************
 //
 //  logDebug - logs system debug messages to db using the con passed **** MUST be rev (v5) ***********
 //
 //************************************************************************

 public static void logDebug(String initials, String msg, Connection con) {
     
     Connect.logDebug(initials, msg, con);

 }  // end of logError


 public static boolean enableAdvAssist(HttpServletRequest req) {

   boolean result = true;

   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("ipad") > -1 || ua.indexOf("ipod") > -1 || ua.indexOf("iphone") > -1) {

       result = false;

   }

   return result;

 }

 /**
  * isClubInGroup - returns whether or not a club is listed as part of a particular group in v5/clubs
  *
  * @param club Name of club in question
  * @param group_name Name of the group to check for
  *
  * @return boolean - True if club is in the specified group, false otherwise
  */
 public static boolean isClubInGroup(String club, String group_name) {
 
     boolean isClubInGroup = false;
     
     Connection con = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     try {
         con = Connect.getCon("v5");
     } catch (Exception exc) {
         Utilities.logError("Utilities.isClubInGroup - Club: " + club + ", Group: " +group_name+ " - Error connecting to v5 database - ERR: " + exc.toString());
         return false;
     }
     
     try {
         
         pstmt = con.prepareStatement("SELECT clubname FROM clubs WHERE clubname = ? AND group_name = ?");
         pstmt.clearParameters();
         pstmt.setString(1, club);
         pstmt.setString(2, group_name);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             isClubInGroup = true;
         }
         
     } catch (Exception exc) {
         Utilities.logError("Utilities.isClubInGroup - " + club + ", Group: " +group_name+ " - Error looking up group_name status - ERR: " + exc.toString());
     } finally {
         Connect.close(rs, pstmt, con);
     }

     return isClubInGroup;
 }
 
 public static String getClubGroupName(String club) {
     
     String group_name = "";
     
     Connection con = null;
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     try {
         con = Connect.getCon("v5");
     } catch (Exception exc) {
         Utilities.logError("Utilities.getClubGroupName - Club: " + club + " - Error connecting to v5 database - ERR: " + exc.toString());
     }
     
     if (con != null) {
         try {

             pstmt = con.prepareStatement("SELECT group_name FROM clubs WHERE clubname = ?");
             pstmt.clearParameters();
             pstmt.setString(1, club);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 group_name = rs.getString("group_name");
             }

         } catch (Exception exc) {
             Utilities.logError("Utilities.getClubGroupName - " + club + " - Error looking up group_name for club - ERR: " + exc.toString());
         } finally {
             Connect.close(rs, pstmt, con);
         }
     }

     return group_name;
 }
 
 public static String filterDisplayName(String display_name, String club) {
     return filterDisplayName(display_name, club, null);
 }
 
 public static String filterDisplayName(String display_name, String club, String group_name) {
     
     if ((group_name != null && group_name.equalsIgnoreCase("Bay Clubs")) || (group_name == null && isClubInGroup(club, "Bay Clubs"))) {
         return display_name.replaceAll("_\\d{3}$", "");
     } else {
         return display_name;
     }
 }

 /**
  * isHotelUser - Determines whether or not user is a hotel user
  *
  * @param user Username to check
  * @param con Connection to club database
  *
  * @return result - True if user is a hotel user, false otherwise
  */
 public static boolean isHotelUser(String user, Connection con) {

     PreparedStatement pstmt = null;
     PreparedStatement pstmt2 = null;
     ResultSet rs = null;
     ResultSet rs2 = null;

     boolean result = false;

     try {

         // See if this is a member first, if so, reject
         pstmt = con.prepareStatement("SELECT username FROM member2b WHERE username = ?");
         pstmt.clearParameters();
         pstmt.setString(1, user);

         rs = pstmt.executeQuery();

         if (rs.next()) {
             result = false;
         } else {

             // If not a member, check to see if it is a hotel user
             pstmt2 = con.prepareStatement("SELECT username FROM hotel3 WHERE username = ?");
             pstmt2.clearParameters();
             pstmt2.setString(1, user);

             rs2 = pstmt2.executeQuery();

             if (rs2.next()) {
                 result = true;
             }

             pstmt2.close();
         }

         pstmt.close();


     } catch (Exception exc) {
         result = false;
     }

     return result;
 }


 public static String decryptTTdata(String data) {

    String result = "";

    try {

        StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );

        result = encrypter.decrypt( data );

    } catch (Exception e) {
        logError("decryptTTdata: Decrypt Error: Data= " +data+ ", Error= " + e.getMessage() );
    }

    return result;

 }
 
 public static Long decryptTTdataLong(String data) {

    Long result = null;

    try {

        StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );

        String dec = encrypter.decrypt( data );
        
        result = Long.parseLong(dec);

    } catch (Exception e) {
        logError("decryptTTdataLong: Decrypt Error: Data= " +data+ ", Error= " + e.getMessage() );
        result = null;
    }

    return result;

 }


 public static String encryptTTdata(String data) {

    String result = "";

    try {

        StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );

        result = encrypter.encrypt( data );

    } catch (Exception e) {

        logError("encryptTTdata: Encrypt Error: Data= " +data+ ", Error= " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));

    }

    return result;
 }


 /**
  * Pass in an event_id and it returns the name of the event or empty string if not found
  * @param event_id UID of the event
  * @param con Database connection object
  * @return String containing name of event, empty string if not found
  */
 public static String getEventName (int event_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";
    
    String club = getClubDbName(con);      // get club's site name

    try {

        pstmt = con.prepareStatement("" +
                "SELECT name " +
                "FROM events2b " +
                "WHERE event_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, event_id);

        rs = pstmt.executeQuery();

        if (rs.next()) result = rs.getString("name");

    } catch (Exception exc) {

        logError("Utilities.getEventName: club=" + club + ", event_id=" + event_id + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }
 
 public static int getEventIdFromName (String name, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int event_id = 0;

     String club = getClubDbName(con);      // get club's site name

    try {

        pstmt = con.prepareStatement("" +
                "SELECT event_id " +
                "FROM events2b " +
                "WHERE name = ?");

        pstmt.clearParameters();
        pstmt.setString(1, name);

        rs = pstmt.executeQuery();

        if (rs.next()) event_id = rs.getInt("event_id");

    } catch (Exception exc) {
        logError("Utilities.getEventIdFromName: club=" + club + ", name=" + name + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    } 
     
     return event_id;
 }
 
 public static String getDateFromParts(int mm,int dd,int yy, int format) {

    String result;

    switch (format) {

        case 1:

            result = yy + "-" + mm + "-" + dd;
            break;

        case 2:

            result = mm + "/" + dd + "/" + yy;
            break;
            
        case 3:

            result = String.format("%02d/%02d/%04d", mm, dd, yy); //"01/01/2014" (With leading zeros, if needed)
            break;

        default:

            result = mm + "/" + dd + "/" + yy;
            break;
    }

    return result;

 }

 public static String getDateFromYYYYMMDD(int date, int format) {

    //
    //  isolate yy, mm, dd
    //
    int yy = date / 10000;
    int temp = yy * 10000;
    int mm = date - temp;
    temp = mm / 100;
    temp = temp * 100;
    int dd = mm - temp;
    mm = mm / 100;

    return getDateFromParts(mm, dd, yy, format);

 }

 
 //  Get number of days between today and the date provided
 public static int getDaysBetween(long date) {
     
     
     //
     //  break down date provided
     //
     long yy = date / 10000;                             // get year
     long mm = (date - (yy * 10000)) / 100;              // get month
     long dd = (date - (yy * 10000)) - (mm * 100);       // get day
     
     int month = (int) mm;
     int day = (int) dd;
     int year = (int) yy;
     
     //
     //  Check if this tee time is within 30 days of the current date (today)
     //
     BigDate today = BigDate.localToday();                 // get today's date
     BigDate thisdate = new BigDate(year, month, day);     // get requested date
     
     int ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between
     
     return (ind);
     
 }       // end of getDaysBetween
 

 //
 //  Return the multi option for club
 //
 public static int getMulti(Connection con) {

    int multi = 0;
    Statement stmt = null;
    ResultSet rs = null;

    String club = getClubDbName(con);      // get club's site name

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT multi FROM club5");
        if (rs.next()) multi = rs.getInt(1);

    } catch (Exception exc) {
        logError("Utilities.getMulti: club=" + club + ", Error=" + exc.getMessage());
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    return multi;

 } // end getMulti



 //
 //  Return the multi option for club
 //
 public static void getExtMainTop(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con) {


    String club = (String)session.getAttribute("club");
    
    int tmp_tlt = 0;

    //
    // See what activity mode we are in
    //
    int sess_activity_id = 0;

    try { sess_activity_id = (Integer)session.getAttribute("activity_id"); }
    catch (Exception ignore) { }

    //
    //  See if we are in the timeless tees mode
    //
    try { tmp_tlt = (Integer)session.getAttribute("tlt"); }
    catch (Exception ignore) { }
      
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year


     //
     //  Build the Header Table
     //
     out.println("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

     out.println("<tr valign=\"top\">");
     out.print("<td width=\"24%\" align=\"center\" valign=\"middle\">");
     out.print("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
     out.print("</td>\n");

     out.println("<td width=\"52%\" align=\"center\">");
     out.println("<font size=\"5\">ForeTees Reservation System</font><br>");
     out.print("</td>");

     out.print("<td width=\"24%\" align=\"center\" valign=\"middle\">");
     out.print("<a href=\"http://www.foretees.com\" target=\"_blank\">");
     if (sess_activity_id > 0) {
        out.print("<img src=\"/" +rev+ "/images/FlxRez_nav.gif\" border=0></a>&nbsp;");
     } else {
        out.print("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
     }
     out.print("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
     out.print("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
     out.print("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
     out.print("</font>\n");

     out.print("</td>");
     out.println("</tr>");
     out.println("</table><HR width=\"100%\"><BR><BR>");
         
 } // end getExtMainTop


  public static String trimQuotes( String value ) {

    if ( value == null )
      return value;

    value = value.trim(); // first trim any whitespace

    if ( value.startsWith( "\"" ) && value.endsWith( "\"" ) ) {

        return value.substring( 1, value.length() - 1 );

    }

    return value;

  }


  public static int parseInt( String value ) {

      int result = 0;

      try { result = Integer.parseInt( value ); }
      catch (NumberFormatException ignore) { }

      return result;

  }

// add event log entry for when we send an email
  public static void createEventLogEntry(String user, int event_id, int event_signup_id, String action, String detail, Connection con) {
      
      createEventLogEntry(user, event_id, event_signup_id, action, detail, 0, con);
  }
  
  public static void createEventLogEntry(String user, int event_id, int event_signup_id, String action, String detail, int email_suppressed, Connection con) {

    int time = 0;

    String club = getClubDbName(con);      // get club's site name

    //
    //   Get current date and time
    //
    Calendar cal = new GregorianCalendar();
    int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);
    int cal_min = cal.get(Calendar.MINUTE);
    int cal_sec = cal.get(Calendar.SECOND);

    //
    //  Build the 'time' string for display
    //
    //    Adjust the time based on the club's time zone (we are Central)
    //
    time = (cal_hourDay * 100) + cal_min;

    time = adjustTime(con, time);       // adjust for time zone

    if (time < 0) {                     // if negative, then we went back or ahead one day

      time = 0 - time;                  // convert back to positive value (getDate above should have adjusted the date)
    }

    PreparedStatement pstmt = null;

    try {

        pstmt = con.prepareStatement (
            "INSERT INTO event_log " +
                "(event_id, event_signup_id, user, action, date_time, detail, email_suppressed) " +
            "VALUES " +
                "(?, ?, ?, ?, ?, ?, ?)");

        pstmt.clearParameters();
        pstmt.setInt(1, event_id);
        pstmt.setInt(2, event_signup_id);
        pstmt.setString(3, user);
        pstmt.setString(4, action);
        pstmt.setString(5, get_mysql_timestamp((int)getDate(con), time, cal_sec));
        pstmt.setString(6, detail);
        pstmt.setInt(7, email_suppressed);

        pstmt.executeUpdate();

    } catch (Exception exc) {

        logError("Utilities.createEventLogEntry: Error creating log entry. Club= " +club+ ", Err=" + exc.toString());
        // then dump a stack trace to the catalina log file
        exc.printStackTrace();

    } finally {

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

  }


  public static String buildPlayerString(String player1, String player2, String player3, String player4, String player5, String delim) {

    StringBuffer result = new StringBuffer(player1);

    if (!player2.equals("")) {

        result.append(delim);
        result.append(player2);

    }

    if (!player3.equals("")) {

        result.append(delim);
        result.append(player3);

    }

    if (!player4.equals("")) {

        result.append(delim);
        result.append(player4);

    }

    if (!player5.equals("")) {

        result.append(delim);
        result.append(player5);

    }

    return result.toString();

  }

 public static String getFullNameFromUsername(String username, Connection con) {

    String ret = "";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT CONCAT(name_first, ' ', name_last) AS fullName " +
                "FROM member2b " +
                "WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();

        if (rs.next()) ret = rs.getString("fullName");

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return ret;

 }


 public static String getLastNameFromUsername(String username, Connection con) {

    String ret = "";
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT name_last " +
                "FROM member2b " +
                "WHERE username = ?;");
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();

        if (rs.next()) ret = rs.getString("name_last");

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return ret;

 }


 public static String getSubtypeFromUsername(String username, Connection con) {

    String ret = "";
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    if (!username.equals("") && username != null) {

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT msub_type " +
                    "FROM member2b " +
                    "WHERE username = ?;");
            pstmt.clearParameters();
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) ret = rs.getString("msub_type");

        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }

    return ret;

 }


 public static String getDayNameFromDate(int date) {


    int year = date / 10000;
    int month = (date - (year * 10000)) / 100;
    int day = (date - (year * 10000)) - (month * 100);

    Calendar cal = Calendar.getInstance();
    cal.set(year, month - 1, day);

    return ProcessConstants.DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK) - 1];

 }

 //
 //   Get the member's name from the username passed (not mNum)
 //
 public static String getNameDataFromMemNum(String memNum, Connection con) {

    String ret = "";
    String first = "";
    String mid = "";
    String last = "";
    String name = "";
    String wc = "";
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String club = getClubDbName(con);      // get club's site name

    try {

        pstmt = con.prepareStatement("" +
                "SELECT name_first, name_mi, name_last, wc, ghin, gender " +
                "FROM member2b " +
                "WHERE username = ? AND inact = 0;");
        
        pstmt.clearParameters();
        pstmt.setString(1, memNum);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            if (rs.getString("name_mi").equals("")) {

                name = rs.getString("name_first") + " " + rs.getString("name_last");

            } else {

                name = rs.getString("name_first") + " " + rs.getString("name_mi") + " " + rs.getString("name_last");
            }

            // combine name:wc for script
            ret = name + ":" + rs.getString("wc");

        }

        // if there was more than one name found then just return nothing because this club shouldn't be using it
        //if (rs.next()) ret = "";

    } catch (Exception exc) {

        logError("getNameDataFromMemNum: club= " +club+ ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return ret;

 }
         
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
    try {
    StringBuffer spec = new StringBuffer(5);

    int length = input.length();
    int i = 0;
    char c;
    String specs = "";

    for(i=0; i<length; i++) {

       c = input.charAt(i);
       if (c == '&') {                  // if special html char

          spec = new StringBuffer(5);   // init string

          while (c != ';' && i<length) {   // go till end of spec char - semi-colon

             spec.append(c);                        // put in buffer
             i++;
             if (i < length) c = input.charAt(i);   // get next
          }                                         // end of WHILE

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
    } catch (Exception exc) {

        logError("Utilities.unfilter() input=" +input+ ", err=" + exc.getMessage() + ", trace=" + Utilities.getStackTraceAsString(exc));
        
    }
    return(unfiltered.toString());

 }  // end of unfilter


 /**
  * titleCase - Simple method to title case a single word
  *
  * @param word Word to title case
  *
  * @return result - Return the word title cased
  */
 public static String titleCase(String word) {

    String result = "";

    for (int i = 0; i < word.length(); i++) {

        if (i == 0){

            result += word.substring(i, i + 1).toUpperCase();

        } else {

            result += word.substring(i, i + 1).toLowerCase();

        }

    }

    return result;

 }


 /**
  * isNewSkinActive - Returns true if new skin is active for this club.  If new skin is not active, return false
  *
  * @param club String club name
  * @param con Connection to the club database
  *
  * @return boolean - true for active, false for not active
  */
 public static boolean isNewSkinActive(String club, Connection con) {
     
     return true;
     
 /*
     Statement stmt = null;
     ResultSet rs = null;

     boolean result = false;

     // for now, new skin runs only on dev server
     //if (ProcessConstants.SERVER_ID == 4) {

         int today = (int)getDate(con); // today's date

         try {

             stmt = con.createStatement();

             rs = stmt.executeQuery("SELECT new_skin_date FROM club5");

             if (rs.next()) {

                 result = (rs.getInt("new_skin_date") <= today) ;
             }

         } catch (Exception exc) {

             logError("Utilities.isNewSkinActive: Error looking up new_skin_date. club=" + club + ", Err=" + exc.getMessage() );

         } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

         }

     //}

     return result;
 */
 }


 public static int getIndexFromToday(long date, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    long today = getDate(con);
    int index = 0;

    try {

        pstmt = con.prepareStatement ("SELECT DATEDIFF(?, ?) AS i");
        pstmt.clearParameters();
        pstmt.setLong(1, date);
        pstmt.setLong(2, today);

        rs = pstmt.executeQuery();

        if ( rs.next() ) index = rs.getInt(1);

    } catch (Exception e1) {

        logError("Error in SystemUtils.getIndexFromToday(): today=" + today + ", date=" + date + ", Exception: " +e1.getMessage() );

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return index;
 }
/*
 public static TreeMap<String, String> findMatchingFiles(String basePath, String path, String file, String filewildcard) {
     
     TreeMap fileList = new TreeMap<String, String>();
     
     String fileTest = "";
     String fileName = "";
     File folder = new File(basePath + path);
     
     if(folder != null){
         File[] listOfFiles = folder.listFiles();
         if(listOfFiles != null){
             for(int i = 0; i < listOfFiles.length; i++){
                 if (listOfFiles[i].isFile()){
                     fileName = listOfFiles[i].getName();
                     fileTest = Utilities.buildRegexFromWildcard(fileName, filewildcard);
                     if((Pattern.matches(fileTest, file)) == true){
                         fileList.put(fileName,path + '/' + fileName);
                     }
                 }
             }
         }else{
             //fileList.put(basePath + path ,basePath + path + "/" + file + ":null list");
         }
     }else{
         //fileList.put(basePath + path ,basePath + path + "/" + file + ":null folder");
     }

     
     return fileList;
     
 }
 
 public static String buildRegexFromWildcard(String str, String wildcard){
     
     String result = "";
     String[] temp;
     int length = 0;
     
     temp = str.split(wildcard);
     for(int i = 0; i < temp.length; i++){
         if(result.length() > 0){
             result += "(.*)"; 
         }
         result += Pattern.quote(temp[i]);
     }
     
     return result;
     
 }
 */
 @Deprecated
 public static String findFileToString(String basePath, String path, String file, String startStr, String endStr){
 
     String result = "";
     File checkFile = new File(basePath + path + "/" + file);
     if(checkFile != null && checkFile.isFile()){
    //TreeMap<String, String> fileList = Utilities.findMatchingFiles(basePath, path, file, wildcard);
    //for(Map.Entry<String,String> entry : fileList.entrySet()) {
        result += startStr+StringEscapeUtils.escapeHtml(path + "/" + file)+endStr;
    //}
     }
     return result;
 }
 
 public static void addIfFileExists(List<String> scriptList, String basePath, String path, String file){
     addIfFileExists(scriptList, basePath, path, file, null, false);
 }
 
 public static void addIfFileExists(List<String> scriptList, String basePath, String path, String file, String baseVersion){
     addIfFileExists(scriptList, basePath, path, file, baseVersion, false);
 }
 
 public static void addIfFileExists(List<String> scriptList, String basePath, String path, String file, String baseVersion, boolean forceAdd){
     
     File checkFile = new File(basePath + path + "/" + file);
     if(checkFile != null && checkFile.isFile()){
        if(baseVersion != null){
            scriptList.add(path + "/" + file + "?v=" + (checkFile.lastModified()/1000) + baseVersion);
        } else {
            scriptList.add(path + "/" + file);
        }
     } else if(forceAdd) {
         scriptList.add(path + "/" + file);
     }
 }
 
 
 /**
  * getEventCategoryNameFromId - Looks up and returns the name of an event category based on the passed category_id
  * 
  * @param category_id Category_id for the event category to look up
  * @param con Connection to the club database
  * 
  * @return name - Returns the name of the event category that matches the passed category_id
  */
 public static String getEventCategoryNameFromId(int category_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     String name = "";
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT category_name FROM event_categories WHERE category_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, category_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             name = rs.getString(1);
         }
         
     } catch (Exception exc) {
         logError("Utilities.getEventCategoryNameFromId - Error getting category name - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return name;
 }
 
 
 /**
  * buildEventCategoryList - Builds and returns an ArrayList of all the event category_ids for this activity_id in alphabetical order (based on category_name)
  * 
  * @param activity_id Activity_id to look up event categories for
  * @param con Connection to club database
  * 
  * @return  category_ids - ArrayList containing the category_ids of all event categories for this activity_id in alphabetical order (based on category_name)
  */
 public static ArrayList<Integer> buildEventCategoryIdList(int activity_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     ArrayList<Integer> category_ids = new ArrayList<Integer>();
    
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT category_id FROM event_categories WHERE activity_id = ? ORDER BY sort_by <> 0 DESC, sort_by ASC, category_name ASC");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             category_ids.add(rs.getInt(1));
         }
         
     } catch (Exception exc) {
         logError("Utilities.buildEventCategoryIdList - Error getting category ids - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return category_ids;
 }
 
 
 /**
  * buildEventCategoryNameList - Builds and returns an ArrayList of all the event category names for this activity_id in alphabetical order
  * 
  * @param activity_id Activity_id to look up event categories for
  * @param con Connection to club database
  * 
  * @return  category_names - ArrayList containing the names of all event categories for this activity_id in alphabetical order
  */
 public static ArrayList<String> buildEventCategoryNameList(int activity_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     ArrayList<String> category_names = new ArrayList<String>();
    
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT category_name FROM event_categories WHERE activity_id = ? ORDER BY sort_by <> 0 DESC, sort_by ASC, category_name ASC");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             category_names.add(rs.getString(1));
         }
         
     } catch (Exception exc) {
         logError("Utilities.buildEventCategoryNameList - Error getting category names - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return category_names;
 }
 
 
 /**
  * buildEventCategoryListFromReq - Checks to see which of the possible category_ids for this activity_id had their checkboxes ticked, and puts the corresponding category_ids into an ArrayList
  * 
  * @param req Request object containing the checkbox results
  * @param activity_id Activity_id to check category_ids for
  * @param con Connection to the club database
  *
  * @return category_ids - ArrayList containing all the category_ids that had their checkboxes checked.
  */
 public static ArrayList<Integer> buildEventCategoryListFromReq(HttpServletRequest req, int activity_id, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     ArrayList<Integer> category_ids = new ArrayList<Integer>();
     
     int curr_category_id = 0;
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT category_id FROM event_categories WHERE activity_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, activity_id);
         
         rs = pstmt.executeQuery();
         
         while (rs.next()) {
             
             curr_category_id = rs.getInt("category_id");
             
             if (req.getParameter("category_id_" + curr_category_id) != null) {
                 category_ids.add(curr_category_id);
             }
         }
         
     } catch (Exception exc) {
         logError("Utilities.buildEventCategoryListFromReq - Error looking up event categories for Club= " +club+ ", activity_id = " + activity_id + " - ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return category_ids;
 }

 
 /**
  * checkEventCategoryBindings - Check to see whether all of the passed event categories apply to a given event
  * 
  * @param event_id Id of the event to look for
  * @param category_ids ArrayList of event category ids of the category to look for
  * @param con Connection to the club database
  * 
  * @return hideEvent - Returns blank if event should be displayed.  If a certain event category was not found for this event, return the name of the category for use in skip comment
  */
 public static String checkEventCategoryBindings(int event_id, ArrayList<Integer> category_ids, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     String category_name = "";
     
     String club = getClubDbName(con);      // get club's site name

     for (int i=0; i<category_ids.size(); i++) {
         
         try {
       
             pstmt = con.prepareStatement("SELECT * FROM event_category_bindings WHERE event_id = ? and category_id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, event_id);
             pstmt.setInt(2, category_ids.get(i));

             rs = pstmt.executeQuery();

             if (!rs.next()) {
                 category_name = Utilities.getEventCategoryNameFromId(category_ids.get(i), con);
                 break;
             }

         } catch (Exception exc) {
             logError("Utilities.checkEventCategoryBindings - Error checking for an event binding between event_id = " + event_id + " and category_id = " + category_ids.get(i) + " - Club= " +club+ ", ERR: " + exc.toString());
         } finally {

             try { rs.close(); }
             catch (Exception ignore) { }

             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
     }
     
     return category_name;
 }
 
 
 /**
  * UpdateEventCategoryBindings - Overloaded method call for instances where we don't know the event_id, but instead have the name and activity_id.  This will look up the correct event_id and then run the standard method.
  * 
  * @param name Name of the event to look up
  * @param activity_id Activity_id which the event is built for
  * @param category_ids List of category_ids that need to be applied for this event
  * @param con Connection to the club database
  * 
  * @return error - Returns whether or not an error was encountered while updating the event category bindings for this event
  */
 public static boolean updateEventCategoryBindings(String name, int activity_id, ArrayList<Integer> category_ids, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int event_id = 0;
     
     boolean error = false;
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT event_id FROM events2b WHERE name = ? and activity_id = ?");
         pstmt.clearParameters();
         pstmt.setString(1, name);
         pstmt.setInt(2, activity_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             event_id = rs.getInt(1);
         } 
         
         error = updateEventCategoryBindings(event_id, category_ids, con);
         
     } catch (Exception exc) {
         logError("Utilities.updateEventCategoryBindings - Error looking up event_id for Club= " +club+ ", event name = " + name + " and activity_id = " + activity_id + " - ERR: " + exc.toString());
         error = true;
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return error;     
 }
 
 /**
  * updateEventCategoryBindings - Clears out all existing event category bindings for this event, and then inserts new entries for the new set of categories that were selected
  * 
  * @param event_id ID corresponding to the event to update
  * @param category_ids List of category_ids that need to be applied for this event
  * @param con Connection to the club database
  * 
  * @return error - Returns whether or not an error was encountered while updating the event category bindings for this event
  */
 public static boolean updateEventCategoryBindings(int event_id, ArrayList<Integer> category_ids, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean error = false;
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         // First wipe out all pre-existing category_id bindings for this event
         pstmt = con.prepareStatement("DELETE FROM event_category_bindings WHERE event_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, event_id);
         
         pstmt.executeUpdate();
         
     } catch (Exception exc) {
         logError("Utilities.updateEventCategoryBindings - Error removing old event category bindings for Club= " +club+ ", event_id = " + event_id + " - ERR: " + exc.toString());
         error = true;
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
              
     
     for (int i=0; i<category_ids.size(); i++) {
         
         try {
             
             if (category_ids.get(i) != 0) {
                 
                 pstmt = con.prepareStatement("INSERT INTO event_category_bindings (event_id, category_id) VALUES (?,?)");
                 pstmt.clearParameters();
                 pstmt.setInt(1, event_id);
                 pstmt.setInt(2, category_ids.get(i));
                 
                 pstmt.executeUpdate();
             }
             
         } catch (Exception exc) {
             logError("Utilities.updateEventCategoryBindings - Error updating event category bindings for Club= " +club+ ", event_id = " + event_id + " - ERR: " + exc.toString());
             error = true;
         } finally {
             
             try { rs.close(); }
             catch (Exception ignore) { }
             
             try { pstmt.close(); }
             catch (Exception ignore) { }
         }
     }
     
     return error;
 }


 /**
  * getRecurOption - get the recurrence option from the requested lottery for the pro or member
  * 
  * @param user Pro or member to get the option for
  * @param name Name of the lottery 
  * @param con Connection to the club database
  * 
  * @return recur - Returns the recurrence setting requested 
  */
 public static int getRecurOption(String user, String name, Connection con) {
     
     PreparedStatement pstmt3 = null;
     ResultSet rs = null;
     
     int recur = 0;
     int recurrpro = 0;
     int recurrmem = 0;
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt3 = con.prepareStatement (
                  "SELECT recurrpro, recurrmem FROM lottery3 WHERE name = ?");

         pstmt3.clearParameters();     
         pstmt3.setString(1, name);     
         rs = pstmt3.executeQuery();     

         if (rs.next()) {

            recurrpro = rs.getInt("recurrpro");      // Get the pro recurrence option
            recurrmem = rs.getInt("recurrmem");      // Get the member recurrence option
         }

     } catch (Exception exc) {
         logError("Utilities.getRecurOption - Error getting lottery recurrence option - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt3.close(); }
         catch (Exception ignore) { }
     }
     
     if (user.startsWith("proshop")) {
        
        recur = recurrpro;
        
     } else {
        
        recur = recurrmem;
     }
     
     return recur;
 }
 
 
 /**
  * isNotificiationClub - Get the club's time-less tees support indicator.
  *
  * @param con Connection to the club database
  *
  * @return boolean true if club is configured as a notification site, false if not
 **/
 public static boolean isNotificiationClub(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int tlt = 0;

    String club = getClubDbName(con);      // get club's site name

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT no_reservations FROM club5");
        if (rs.next()) tlt = rs.getInt(1);

    } catch (Exception exc) {

         logError("Utilities.isNotificiationClub - Error getting notification option - Club= " +club+ ", ERR: " + exc.toString());
         
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return ((tlt == 1) ? true : false);
 }

 /**
  * isHandicapSysConfigured - Get the club's handicap system support indicator.
  *
  * @param activity_id The activity we are going to check for (future use)
  * @param con Connection to the club database
  *
  * @return boolean true if club has a handicap system configured, false if not
 **/
 public static boolean isHandicapSysConfigured(int activity_id, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    boolean result = false;

    String club = getClubDbName(con);      // get club's site name

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT hdcpSystem FROM club5");
        //if (rs.next()) result = rs.getString(1).equalsIgnoreCase("GHIN");
        if (rs.next()) result = !rs.getString(1).equalsIgnoreCase("Other");     // if NOT Other

    } catch (Exception exc) {

         logError("Utilities.isHandicapSysConfigured - Error getting handicap option - Club= " +club+ ", ERR: " + exc.toString());
         
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return result;
 }
 
 /**
  * Gets the name of the club's currently configured handicap system
  * @param activity_id The activity we are going to check for (future use)
  * @param con Connection to the club's database
  * @return String - name of handicap system
  */
 public static String getHandicapSys(int activity_id, Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     String hdcpSys = "Other";
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT hdcpSystem FROM club5");
         
         if (rs.next()) {
             hdcpSys = rs.getString("hdcpSystem");
         }
         
     } catch (Exception e) {
     
         String club = getClubDbName(con);      // get club's site name
         
         hdcpSys = "Other";
         logError("Utilities.getHandicapSys - Error looking up handicap system - Club= " +club+ ", ERR: " + e.toString());
     } finally {
         Connect.close(rs, stmt);
     }
     
     return hdcpSys;
 }


 /**
  * isLessonBookConfigured - Figure out if the club is using the lesson book feature
  *
  * @param activity_id The activity we are going to check for lesson pros
  * @param con Connection to the club database
  *
  * @return boolean true if club has active lesson pros, false if not
 **/
 public static boolean isLessonBookConfigured(int activity_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean result = false;

    String club = getClubDbName(con);      // get club's site name

    try {

        pstmt = con.prepareStatement("SELECT id FROM lessonpro5 WHERE active = 1 AND activity_id = ? LIMIT 1");
        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);
        
        rs = pstmt.executeQuery();

        result = rs.next();

    } catch (Exception exc) {

         logError("Utilities.isLessonBookConfigured - Error getting lesson option - Club= " +club+ ", ERR: " + exc.toString());
         
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (pstmt != null) {
           try {
              pstmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return result;
 }


 /**
  * getAnnouncementPageFileName - Return the filename of the announcement page for a given club and activity
  *
  * @param activity_id The activity we are building filename for.
  * @param user Could contain the username of the user.  Only the system dining user makes use of this.
  * @param club Club we are building filename for.
  * @param timestamp The timestamp to use when building a backup filename.
  * @param bak True if we are building a backup filename.
  *
  * @return The filename.
 **/
 public static String getAnnouncementPageFileName(int activity_id, String user, String club, String timestamp, boolean bak) {

    // tmp hack 
    if (user.equals("proshopfb")) activity_id = ProcessConstants.DINING_ACTIVITY_ID;
     
    StringBuilder sbExt = new StringBuilder();
    StringBuilder sbFileName = new StringBuilder();

    if (bak == true) {     // if backup file

       sbExt.append("-");
       sbExt.append(timestamp);
       sbExt.append(".bak");

    } else {

       sbExt.append(".htm");
    }

    if (activity_id == ProcessConstants.DINING_ACTIVITY_ID || user.equals(ProcessConstants.DINING_USER)) {

        sbFileName.append(club);
        sbFileName.append("_announce_dining");
        sbFileName.append(sbExt);

    } else if (activity_id == 0) {

        sbFileName.append(club);
        sbFileName.append("_announce");
        sbFileName.append(sbExt);

    } else {

        sbFileName.append(club);
        sbFileName.append("_announce_");
        sbFileName.append(activity_id);
        sbFileName.append(sbExt);
        
    }

    return sbFileName.toString();

 }


 /**
  * getLessonBioPageFileName - Return the filename of the lesson bio page for a given club, activity, lesson pro
  *
  * @param pro_id The id of the lesson pro
  * @param club Club we are building filename for.
  * @param timestamp The timestamp to use when building a backup filename.
  * @param bak True if we are building a backup filename.
  *
  * @return The filename.
 **/
 public static String getLessonBioPageFileName(int pro_id, String club, String timestamp, boolean bak) {


    StringBuilder sbExt = new StringBuilder();
    StringBuilder sbFileName = new StringBuilder();

    if (bak == true) {     // if backup file

       sbExt.append("-");
       sbExt.append(timestamp);
       sbExt.append(".bak");

    } else {

       sbExt.append(".htm");
    }

    sbFileName.append(club);
    sbFileName.append("_bio");
    sbFileName.append(pro_id);
    sbFileName.append(sbExt);

    return sbFileName.toString();

 }
 
  /**
  * getSessionString - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static String getSessionString(HttpServletRequest req, String param, String def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionString(session, param, def);
 }
 
  public static String getSessionString(HttpSession session, String param, String def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof String){
             return (String) attr;
         }
     }
     return def;
 }
  
    /**
  * getSessionBoolean - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Boolean getSessionBoolean(HttpServletRequest req, String param, Boolean def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionBoolean(session, param, def);
 }
 
  public static Boolean getSessionBoolean(HttpSession session, String param, Boolean def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Boolean){
             return (Boolean) attr;
         }
     }
     return def;
 }
  
   /**
  * getSessionInteger - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Integer getSessionInteger(HttpServletRequest req, String param, Integer def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionInteger(session, param, def);
 }
 
  public static Integer getSessionInteger(HttpSession session, String param, Integer def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             return (Integer) attr;
         }
     }
     return def;
 }
  
  /**
  * getSessionLong - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Long getSessionLong(HttpServletRequest req, String param, Long def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionLong(session, param, def);
 }
 
  public static Long getSessionLong(HttpSession session, String param, Long def) {
      
     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null && attr instanceof Long){
             return (Long) attr;
         }
     }
     return def;
 }
  
   /**
  * getSessionObject - returns string parameter from session, or default
  *
  * @param req or session (request or session object).
  * @param param name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Object getSessionObject(HttpServletRequest req, String param, Object def){
     HttpSession session = null;
     if(req != null){
         session = req.getSession(false);
     }
     return getSessionObject(session, param, def);
 }
 
  public static Object getSessionObject(HttpSession session, String param, Object def) {

     Object attr = null;
     
     if(session != null){
         attr = session.getAttribute(param);
         if(attr != null){
             return (Object) attr;
         }
     }
     return def;
 }
  
 /**
  * getRequestString - returns string attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static String getRequestString(HttpServletRequest req, String param, String def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof String){
             return (String) attr;
         }
     }
     return def;
 }
  
 /**
  * getRequestInteger - returns integer attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Integer getRequestInteger(HttpServletRequest req, String param, Integer def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Integer){
             return (Integer) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestLong - returns long attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Long getRequestLong(HttpServletRequest req, String param, Long def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Long){
             return (Long) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestBoolean - returns boolean attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Boolean getRequestBoolean(HttpServletRequest req, String param, Boolean def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Boolean){
             return (Boolean) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestStringBuilder - returns StringBuilder attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static StringBuilder getRequestStringBuilder(HttpServletRequest req, String param, StringBuilder def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof StringBuilder){
             return (StringBuilder) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestTimeZone - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static TimeZone getRequestTimeZone(HttpServletRequest req, String param, TimeZone def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof TimeZone){
             return (TimeZone) attr;
         }
     }
     return def;
 }
  
  /**
  * getRequestObject - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Object getRequestObject(HttpServletRequest req, String param, Object def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null){
             return (Object) attr;
         }
     }
     return def;
 }
  
    /**
  * getRequestMap - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Map getRequestMap(HttpServletRequest req, String param, Map def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Map){
             return (Map) attr;
         }
     }
     return def;
 }
  
   /**
  * getRequestConnection - returns object attribute from request attribute, or default
  *
  * @param req (request object).
  * @param attribute name if attribute.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
 
  public static Connection getRequestConnection(HttpServletRequest req, String param, Connection def) {

     Object attr = null;
     
     if(req != null){
         attr = req.getAttribute(param);
         if(attr != null && attr instanceof Connection){
             return (Connection) attr;
         }
     }
     return def;
 }
  

   /**
  * getParameterString - returns string attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
     public static String getParameterString(HttpServletRequest req, String param, String def) {

         Object p = null;
         if(req != null){
             p = req.getParameter(param);
             if(p != null && p instanceof String){
                 return (String) p;
             }
         }
         return def;
     }
  
    /**
  * getParameterInteger - returns integer attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Integer getParameterInteger(HttpServletRequest req, String param, Integer def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            Integer r = def;
            try {
                r = Integer.parseInt(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
     /**
  * getParameterLong - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Long getParameterLong(HttpServletRequest req, String param, Long def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            Long r = def;
            try {
                r = Long.parseLong(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    /**
  * getParameterBoolean - returns long attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Boolean getParameterBoolean(HttpServletRequest req, String param, Boolean def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            Boolean r = def;
            try {
                r = Boolean.parseBoolean(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
    
    /**
  * getParameterFloat - returns Float attribute from request parameters
  *
  * @param req (request object).
  * @param param name of parameter.
  * @param def default value to return if does not exist or wrong type.
  *
  * @return result.
 **/
    public static Float getParameterFloat(HttpServletRequest req, String param, Float def) {

        String p = null;
        if (req != null) {
            p = req.getParameter(param);
            Float r = def;
            try {
                r = Float.parseFloat(p);
            } catch (NumberFormatException nfe) {
            }
            return r;
        }
        return def;
    }
  
   /**
  * getSessionCookie - returns cookie for current session
  *
  * @param req or session (request or session object).
  *
  * @return session cookie, or null.
 **/
 
  public static Cookie getSessionCookie(HttpServletRequest req) {

      return getCookie( req, "JSESSIONID");
 
  }
  
  
  public static Cookie getCookie(HttpServletRequest req, String cookieName) {
     
     Cookie result = null;
     
     if(req != null){
         if (req.getCookies() != null) {
            for (Cookie cookie : req.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    result = cookie;
                }
            }
        }
     }
     
     return result;
 }
  
 public static void setRwdCookie(HttpServletResponse resp, HttpServletRequest req, boolean rwd) {
     Cookie rwdCookie = new Cookie(ProcessConstants.COOKIE_RWD_STATE,(rwd?"ON":"OFF"));
     rwdCookie.setPath("/"+rev);
     rwdCookie.setDomain(req.getServerName());
     rwdCookie.setMaxAge(31557600);
     resp.addCookie(rwdCookie);
     return;
 }
 
 
  
  
  
 /**
  * setSessionPath - Sets the path for the cookie of the current session, based status of request/session
  *
  * @param req The request object
  *
 **/
 
 public static void setSessionPath(HttpServletRequest req) {
     
     if(getSessionString(req,"new_skin","0").equals("1")){
         // Only set path in new_skin mode
         Cookie cookie = getSessionCookie(req);
         if(cookie != null){
             String club = getSessionString(req,"club","NullClubName");
             cookie.setPath("/v5/" + club);
         }  
     }

 }
 
 /**
  * getNewSession - Create a new, empty session
  *
  * @param req The request object
  *
 **/
 
 public static HttpSession getNewSession(HttpServletRequest req) {
     
     HttpSession session = req.getSession(false);
     if(session != null){
         /*
         // If there is already a session, destroy it and start again
         session.invalidate();
         session = null;
          * 
          */
        // To keep user experience as unchanged as possible, only reset a few values
         //session.removeAttribute("app_mode"); // This is now stored in request, not session
         session.removeAttribute(ApiConstants.session_auth_token_key); // Used by Accounting
         session.removeAttribute(ApiConstants.sess_user_id); // Used by Accounting

     }
     return req.getSession(true);

 }
 
 public static String URLEncode(String string){
     try {
         return URLEncoder.encode(string,"UTF-8");
     } catch (Exception e) {
         return "";
     }
 }
 

 /**
  * getBaseUrl - Returns the initial portion of our URL
  *
  * @param activity_id The activity we are building a URL for.
  * @param club Club we are building a URL for.
  *
  * @return The base URL.
 **/
 
    public static String getBaseUrl(HttpServletRequest req) {

        return getBaseUrl(req, null, null, null);

    }
 
    //public static String getBaseUrl(HttpServletRequest req, int activity_id, String club, int app_mode) {

    //    return getBaseUrl(req, new Integer(activity_id), club, new Integer(app_mode));

    //}
    //public static String getBaseUrl(HttpServletRequest req, int activity_id, String club) {

    //    return getBaseUrl(req, new Integer(activity_id), club, null);

    //}
    public static String getBaseUrl(HttpServletRequest req, Integer activity_id, String club) {

        return getBaseUrl(req, activity_id, club, null);

    }
    public static String getBaseUrl(HttpServletRequest req, Integer activity_id, String club, Integer app_mode) {

        //String base_url = "";
        String activity = null;

        if(app_mode == null){
            app_mode = Utilities.getRequestInteger(req,ProcessConstants.RQA_APPMODE,0);
        }
        if(activity_id == null){
            activity_id = Utilities.getSessionInteger(req,"activity_id",0);
        }
        if(club == null){
            club = Utilities.getSessionString(req,"club","undefined");
        }

        // Convert inverted activities back to normal.
        if(activity_id < 0){
            activity_id = activity_id * -1;
        }
        if(activity_id.equals(999)){
            activity_id = 0;
        }

        if (activity_id.equals(ProcessConstants.DINING_ACTIVITY_ID)) {

            activity = "dining";

        } else if (activity_id.equals(0)){

            activity = "golf";

        } else {
            
            activity_id = getActivity.getRootIdFromActivityId (activity_id, req);

            activity = "flxrez" + activity_id;

        }

        return "../"+club + "_" + activity + "_m" + app_mode + "/";

    }


 /**
  * getExternalBaseUrl - Returns the full leading portion of our URL
  *
  * @param activity_id The activity we are building a URL for.
  * @param club Club we are building a URL for.
  *
  * @return The base URL to be used for external linking.
 **/
    public static String getExternalBaseUrl(HttpServletRequest req, int activity_id, String club) {
        return getExternalBaseUrl(req, new Integer(activity_id), club, null);
    }
    
    //public static String getExternalBaseUrl(HttpServletRequest req, int activity_id, String club, int app_mode) {
    //    return getExternalBaseUrl(req, new Integer(activity_id), club, new Integer(app_mode));
    //}
    
    public static String getExternalBaseUrl(HttpServletRequest req, Integer activity_id, String club, Integer app_mode) {

        
        StringBuilder tmp = new StringBuilder();

        String uri = getBaseUrl(req, activity_id, club, app_mode);
        if(!uri.matches("^http")){
            tmp.append(reqUtil.getServerUrl(req));
            tmp.append(uri.replaceFirst("../", "/"+rev+"/"));
        } else {
            tmp.append(uri);
        }
        
        /*
        StringBuilder tmp = new StringBuilder("http://");
        if (ProcessConstants.SERVER_ID == 4) {

            //tmp.append("dev2.foretees.com/" + rev  + "/");
            tmp.append(req.getServerName()+"/" + rev  + "/");

        } else {

            tmp.append("www1.foretees.com/" + rev  + "/");

        }
        tmp.append(getBaseUrl(req, activity_id, club, app_mode).replaceFirst("../", ""));
         * 
         */

        
        return tmp.toString();

    }

    
  /**
  * usesGroupedSlotEdit - Returns the boolean indicating the club does or does not use a seamless interface
  *
  * @param req HttpServletRequest
  *
  * @return boolean
 **/
    public static boolean usesGroupedSlotEdit(HttpServletRequest req) {
        
     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT use_groupEdit FROM club5");
         
         if (rs.next() && rs.getInt("use_groupEdit") == 1) {
             result = true;
         }
         
     } catch (Exception exc) {
         
         logError("Utilities.usesGroupedSlotEdit - Error getting use_groupEdit option from club5. Club=" + club + ", Err=" + exc.toString());
         
     } finally {
         
         Connect.close(rs, stmt);
     }
     
     return result;


    }

    

 /**
  * clubUsesSeamless - Returns the boolean indicating the club does or does not use a seamless interface
  *
  * @param club The name of the club.
  *
  * @return boolean
 **/
    public static boolean clubUsesSeamless(String club) {


        return !getClubCaller(club).equals("");

    }


 /**
  * getClubCaller - Returns the seamless interface name (caller) for the supplied club
  *
  * @param club The name of the club.
  *
  * @return boolean
 **/
    public static String getClubCaller(String club) {

        String result = "";

        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {

            con = Connect.getCon(club);

            stmt = con.createStatement();

            rs = stmt.executeQuery("SELECT seamless_caller FROM club5");

            if (rs.next()) result = rs.getString(1);

        } catch (Exception exc) {

            logError("Utilities.getClubCaller: Error looking up calller. Club= " +club+ ", Err=" + exc.getMessage() );

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

            try { con.close(); }
            catch (Exception ignore) {}

        }

        return result;

    }


    /**
     * Returns whether or not a given mship has access to the specified activity_id.
     * @param mship Membership Type to check access for
     * @param activity_id Activity ID mship is requesting access to
     * @param con Connection to club database
     * @return boolean - true if mship has access
     */
    public static boolean checkMshipAccess(String mship, int activity_id, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        boolean hasAccess = false;
        
        String club = getClubDbName(con);      // get club's site name

        try {
            pstmt = con.prepareStatement("SELECT mship FROM mship5 WHERE mship = ? AND activity_id = ?");
            pstmt.clearParameters();
            pstmt.setString(1, mship);
            pstmt.setInt(2, activity_id);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                hasAccess = true;
            }
            
        } catch (Exception exc) {
            logError("Utilities.chckMshipAccess: Error checking if mship has access to activity_id (" + activity_id + ").  Club= " +club+ ", Err=" + exc.toString());
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
        
        return hasAccess;
    }

    /**
     * Returns whether or not a given mtype has access to ForeTees (all activities).
     * @param mtype Member Type to check access for
     * @param con Connection to club database
     * @return boolean - true if mtype has access
     */
    public static boolean checkMtypeAccess(String mtype, Connection con) {
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        boolean hasAccess = false;
        
        String club = getClubDbName(con);      // get club's site name

        try {
            pstmt = con.prepareStatement("SELECT clubName FROM club5 WHERE mem1 = ? OR mem2 = ? OR mem3 = ? OR mem4 = ? OR mem5 = ? OR mem6 = ? OR mem7 = ? OR mem8 = ? OR mem9 = ? OR mem10 = ? "
                    + "OR mem11 = ? OR mem12 = ? OR mem13 = ? OR mem14 = ? OR mem15 = ? OR mem16 = ? OR mem17 = ? OR mem18 = ? OR mem19 = ? OR mem20 = ? "
                    + "OR mem21 = ? OR mem22 = ? OR mem23 = ? OR mem24 = ?");
            pstmt.clearParameters();
            pstmt.setString(1, mtype);
            pstmt.setString(2, mtype);
            pstmt.setString(3, mtype);
            pstmt.setString(4, mtype);
            pstmt.setString(5, mtype);
            pstmt.setString(6, mtype);
            pstmt.setString(7, mtype);
            pstmt.setString(8, mtype);
            pstmt.setString(9, mtype);
            pstmt.setString(10, mtype);
            pstmt.setString(11, mtype);
            pstmt.setString(12, mtype);
            pstmt.setString(13, mtype);
            pstmt.setString(14, mtype);
            pstmt.setString(15, mtype);
            pstmt.setString(16, mtype);
            pstmt.setString(17, mtype);
            pstmt.setString(18, mtype);
            pstmt.setString(19, mtype);
            pstmt.setString(20, mtype);
            pstmt.setString(21, mtype);
            pstmt.setString(22, mtype);
            pstmt.setString(23, mtype);
            pstmt.setString(24, mtype);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                hasAccess = true;
            }
            
        } catch (Exception exc) {
            logError("Utilities.chckMtypeAccess: Error checking if mtype has access to ForeTees systems.  Club= " +club+ ", Err=" + exc.toString());
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}
            
            try { pstmt.close(); }
            catch (Exception ignore) {}
        }
        
        return hasAccess;
    }

    /**
     * Returns whether or not a given member has access to the specified activity.
     * @param mship Membership Type to check access for
     * @param mtype Member Type to check access for
     * @param activity_id Activity ID member is requesting access for
     * @param con Connection to club database
     * @return boolean - true if member has access
     */
    public static boolean checkMemberAccess(String mship, String mtype, int activity_id, Connection con) {
        
        boolean hasAccess = false;
        
        // Run both the Mship and Mtype access chcks to determine if member has access.
        if (checkMshipAccess(mship, activity_id, con) && checkMtypeAccess(mtype, con)) {
            hasAccess = true;
        }
        
        return hasAccess;
    }
    
    public static boolean copyFile(String source_file, String dest_file) {

        boolean result = false;

        try {

            File inFile  = new File(source_file);  // file to read from
            File outFile = new File(dest_file);  // file to write to

            BufferedReader reader = new BufferedReader(new FileReader(inFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));

            String line = null;
            while ( (line=reader.readLine()) != null ) {
                writer.write(line);
                writer.newLine();   // Write system dependent end of line.
            }

            reader.close();  // Close to unlock.
            writer.close();  // Close to unlock and flush to disk.

            result = true;

        } catch (Exception exc) {

            logError("Utilities.copyFile(): source=" + source_file + ", dest=" + dest_file + ", err=" + exc.toString());

        } finally {


        }

        return result;
    }

    
 /**
 * Gets the exception stack trace as a string. 
 * @param exception 
 * @return 
 */
 public static String getStackTraceAsString(Exception exception) {

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);

    pw.print(" [ ");
    pw.print(exception.getClass().getName());
    pw.print(" ] ");
    pw.print(exception.getMessage());

    exception.printStackTrace(pw);

    return sw.toString();

 }

 
 
 /**
  * getCustomStyles - gets the name of the custom style sheet file for the club, if selected
  * 
  * @param con Connection to the club database
  * 
  * @return customStyles - Returns the name of the custom style sheet file
  */
 public static String getCustomStyles(Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     String customStyles = "";
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT custom_styles FROM club5");
         pstmt.clearParameters();
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             customStyles = rs.getString(1);
         }
         
     } catch (Exception exc) {
         logError("Utilities.getCustomStyles - Error getting file name - Club= " +club+ ", ERR: " + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(exc));
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) { }
         
         try { pstmt.close(); }
         catch (Exception ignore) { }
     }
     
     return customStyles;
 }
 
 
 /**
  * Checks to see if tees exist for a particular course_id
  * @param course_id ID for the course in question
  * @param con Connection to clubd database
  * @return boolean - True if tees exist, false otherwise
  * @throws Exception 
  */
 public static boolean checkTees(int course_id, Connection con) 
             throws Exception {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean hasTees = false;
     
     String club = getClubDbName(con);      // get club's site name

     try {
         
         pstmt = con.prepareStatement("SELECT tee_id FROM tees WHERE course_id = ? LIMIT 1");
         pstmt.clearParameters();
         pstmt.setInt(1, course_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             hasTees = true;
         }
         
     } catch (Exception exc) {

         logError("Utilities.checkTees - Error getting tee ids for course - Club= " +club+ ", ERR: " +exc.getMessage());
         
         throw new Exception("Utilities.checkTees: "
                 + "Error=" + exc.getMessage());
         
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { pstmt.close(); }
         catch (Exception ignore) {}
     }
     
     return hasTees;
 }
 

 
 /*
  * // NOTE:  Use com.foretees.api.records.ForeTeesAnnouncement object to access announcements instead of direclty accessing announcments via SQL
 
 // Check if we have an announcement for the proshop users for this day
 public static String checkFTannouncement(int activity_id, String club) {

    Connection con = null;                 // init DB objects
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    //  get today's date
    
    Calendar cal = new GregorianCalendar();         
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) +1;
    int day = cal.get(Calendar.DAY_OF_MONTH);

    int date = (year * 10000) + (month * 100) + day;   // today's date  
    
    String fileName = "";
    String whereClause = "";
    
    if (club.startsWith("demo")) {      
        
        whereClause = "sdate > 0";     // always show on demo sites if within date range (set all activities = 0 in entry to test on demo site)
        
    } else {
    
        if (activity_id == 0) {

            whereClause = "golf = 1";

        } else {

            whereClause = "flxrez = 1";    // assume FlxRez for now since this is only called by Proshop_maintop (add dining, etc later?)
        }
    }

    try {
       
       con = Connect.getCon(rev);       // get a connection for this version level (v5)
      
       pstmt = con.prepareStatement (
               "SELECT fileName FROM announcements WHERE sdate <= ? AND edate >= ? AND " +whereClause);

       pstmt.clearParameters();       
       pstmt.setInt(1, date);
       pstmt.setInt(2, date);
       rs = pstmt.executeQuery();

       if (rs.next()) {    
          
          fileName = rs.getString("fileName");
       }

    }
    catch (Exception exc) {
     
        logError("Utilities.checkFTannouncement: err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close();}
        catch (Exception ignore) {}

        try { con.close();}
        catch (Exception ignore) {}
    }
    
    return(fileName);
 }                         // end of checkFTannouncement
 
 
 */
 
 
 public static boolean validateRequestedActivity(int activity_id, Connection con) {

    Statement stmt = null;
    ResultSet rs = null;

    boolean allow = false;

    int foretees_mode = 0;
    int dining_mode = 0;
    int flxrez_staging = 0;
    int dining_staging = 0;
    int genrez_mode = 0;

    String club = getClubDbName(con);      // get club's site name

    try {

        // Get foretees_mode, dining_mode and # of activities from database
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT foretees_mode, genrez_mode, organization_id, flxrez_staging, dining_staging FROM club5 WHERE clubName <> '';");

        if (rs.next()) {

            foretees_mode = rs.getInt("foretees_mode") > 0 ? 1 : 0;
            genrez_mode = rs.getInt("genrez_mode") > 0 ? 1 : 0;
            dining_mode = rs.getInt("organization_id") > 0 ? 1 : 0;

            flxrez_staging = rs.getInt("flxrez_staging") > 0 ? 1 : 0;
            dining_staging = rs.getInt("dining_staging") > 0 ? 1 : 0;
        }

        // Now validate the requested activity
        if (activity_id == 0 && foretees_mode == 1) {

            allow = true;

        } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID && dining_mode == 1) {

            allow = true;

        } else if (activity_id > 0 && activity_id != ProcessConstants.DINING_ACTIVITY_ID && genrez_mode == 1) {

            allow = true;

        }

    } catch (Exception exc) {

        logError("Utilities.validateRequestedActivity(): Club= " +club+ ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close();}
        catch (Exception ignore) {}
    }

    return allow;

 }
 
 //**********************************************************************************************
 //
 //    getMemeditOpt - get the memedit option setting from the event passed
 //
 //**********************************************************************************************

 public static int getMemeditOpt(String ename, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int memedit = 0;

    String club = getClubDbName(con);      // get club's site name

    if (!ename.equals("")) {       // if event name provided

        try {

            pstmt = con.prepareStatement("SELECT memedit FROM events2b WHERE name = ?");
            pstmt.clearParameters();
            pstmt.setString(1, ename);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                memedit = rs.getInt("memedit");
            }

        } catch (Exception exc) {

            logError("Utilities.getMemeditOpt: Club= " +club+ ", err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close();}
            catch (Exception ignore) {}
        }
    }

    return memedit;

 }   // end of getMemeditOpt


 /**
  * Returns the name corresponding to the provided course_id
  * @param course_id ID for the course in question
  * @param con Connection to club database
  * @return String - Name of course or NULL if error occured
  */
 public static String getCourseName(int course_id, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String courseName = null;

     String club = getClubDbName(con);      // get club's site name
 
     try {

         pstmt = con.prepareStatement("SELECT courseName FROM clubparm2 WHERE clubparm_id = ?");
         pstmt.clearParameters();
         pstmt.setInt(1, course_id);
         rs = pstmt.executeQuery();
         if (rs.next()) courseName = rs.getString(1);
         
     } catch (Exception exc) {

         logError("Utilities.getCourseName - Error getting course name. Club= " +club+ ", course_id=" + course_id + ", Err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return courseName;
 }


 /**
  * Returns the id corresponding to the provided course_name
  * @param course_name Name of the course in question
  * @param con Connection to club database
  * @return int - Id of course or 0 if error occurred
  */
 public static int getCourseId(String course_name, Connection con) {

     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int courseId = 0;

     String club = getClubDbName(con);      // get club's site name
 
     try {

         pstmt = con.prepareStatement("SELECT clubparm_id FROM clubparm2 WHERE courseName = ?");
         pstmt.clearParameters();
         pstmt.setString(1, course_name);
         rs = pstmt.executeQuery();
         if (rs.next()) courseId = rs.getInt(1);
         
     } catch (Exception exc) {

         logError("Utilities.getCourseId - Error getting course id. Club= " +club+ ", course_name=" + course_name + ", Err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return courseId;
 }

 
 /**
  * Returns the club id corresponding to the provided club name
  * @param club_id ID for the club in question
  * @param con Connection to the database (if null a new one will be created)
  * @return int - id of club or 0 if error occured
  */
 public static int getClubId(String club, Connection con) {

     Long result;
     // ApiCommon will use a cached lookup for club_id, if possible -- less database overhead this way
     if(con != null){
         result = ApiCommon.getClubId(club, con);
     } else {
         result = ApiCommon.getClubId(club); 
     }
     
     if(result == null){
         logError("Utilities.getClubId - Invalid Club database name provided: club=" + club);
         return 0;
     }
     return result.intValue();
     
     /*
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     int id = 0;
     boolean close_con = false;

     if (con == null) {

        con = Connect.getCon(rev);
        close_con = true;
     }

     try {

         pstmt = con.prepareStatement("SELECT id FROM v5.clubs WHERE clubname = ? AND inactive = 0");
         pstmt.clearParameters();
         pstmt.setString(1, club);
         rs = pstmt.executeQuery();
         if (rs.next()) id = rs.getInt(1);

     } catch (Exception exc) {

         logError("Utilities.getClubId - BUG! Error getting club name. club=" + club + ", Err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }

         if (close_con) {
             try { con.close(); }
             catch (Exception ignore) { }
         }

     }
     
     return id;
      * 
      */
 }

 /**
  * Returns the club name corresponding to the provided club_id
  * @param club_id ID for the club in question
  * @param con Connection to the database (if null a new one will be created)
  * @return String - Name of club or NULL if error occured
  */
 public static String getClubName(int club_id, Connection con) {
     
     if(con == null){
         return ApiCommon.getClubCode((long)club_id);
     } else {
         return ApiCommon.getClubCode((long)club_id, con);
     }

     /*
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     String clubName = null;
     boolean close_con = false;

     if (con == null) {

        con = Connect.getCon(rev);
        close_con = true;
     }

     try {

         pstmt = con.prepareStatement("SELECT clubname FROM v5.clubs WHERE id = ? AND inactive = 0");
         pstmt.clearParameters();
         pstmt.setInt(1, club_id);
         rs = pstmt.executeQuery();
         if (rs.next()) clubName = rs.getString(1);

     } catch (Exception exc) {

         logError("Utilities.getClubName - BUG! Error getting club name. club_id=" + club_id + ", Err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }

         if (close_con) {
             try { con.close(); }
             catch (Exception ignore) { }
         }

     }
     
     return clubName;
      * 
      */
 }


 public static String generateConfCode(int vendor_id, int teecurr_id, String club) {


     String conf_code = null;

     final int max = 10; // max attemps to generate confirmation code
     int i = 0;

     while (conf_code == null && i < max) {

         i++;
         conf_code = getConfNum(vendor_id, teecurr_id, club);
     }

     // log multiple attempts
     if (i > 1) {
         
         if (conf_code == null || conf_code.equals("")) {
             // we failed to generate a code
             logError("CRITICAL: Utilties.generateConfNum() WAS NOT ABLE TO GENERATE A UNIQUE CODE AFTER " + i + " ATTEMPTS!!!!!!");
         } else {
             // took mulitiple attempts but we got it done
             logError("NOTICE: Utilties.generateConfNum() took " + i + " loops to generate a unique code!");
         }
     }

     return conf_code;

 }


 //**********************************************************************************************
 //
 //    getEventTime - get the actual start time of the shotgun event passed
 //
 //**********************************************************************************************

 public static int getEventTime(String ename, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int time = 0;
    int hr = 0;
    int min = 0;
    
    String club = getClubDbName(con);      // get club's site name
 
    if (!ename.equals("")) {       // if event name provided

        try {

            pstmt = con.prepareStatement("SELECT act_hr, act_min FROM events2b WHERE name = ?");
            pstmt.clearParameters();
            pstmt.setString(1, ename);
            
            rs = pstmt.executeQuery();
         
            if (rs.next()) {

                hr = rs.getInt("act_hr");
                min = rs.getInt("act_min");
            }
            
            time = (hr * 100) + min;  

        } catch (Exception exc) {

            logError("Utilities.getEventTime: Club= " +club+ ", err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close();}
            catch (Exception ignore) {}
        }
    }

    return time;

 }   // end of getEventTime
 
 

 //**********************************************************************************************
 //
 //  getConfNum - create, save and return a tee time confirmation number to be used in 3rd party
 //               public tee time interfaces (i.e. GolfSwitch or GolfNow).
 //
 //  The Confirmation Number will consist of the following values:
 //
 //         Vendor Id (1 = GolfSwitch, 2 = GolfNow, etc)
 //         Club Id (from v5.clubs table)
 //         Node # (server id)
 //         2 random numbers between 0 - 26
 //         Time stamp (current time down to milli-seconds)
 //
 //   Returns:  Unique Confirmation Number or empty string (if fails)
 //
 //      NOTE:  Caller will have to try again if this fails (meaning the number generated was not unique)
 //
 //**********************************************************************************************
 
 private static String getConfNum(int vendor_id, int teecurr_id, String club) {

    Connection con = null;    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String confNum = "";
    String confNumFinal = null;
    
    int club_id = 0;
    int random1 = 0;
    int random2 = 0;
    int node_id = ProcessConstants.SERVER_ID;   // get the id of the server we are running on
    
    long time = System.currentTimeMillis();    // get the current time in ms


    try {

        con = Connect.getCon(rev);      //  get a connection to v5 db
       
        //  Get this club's club-id
        
        pstmt = con.prepareStatement("SELECT id FROM clubs WHERE clubname = ?");
        pstmt.clearParameters();
        pstmt.setString(1, club);
         
        rs = pstmt.executeQuery();
         
        if (rs.next()) {
             
            club_id = rs.getInt(1);      // get the club id  
        }
        
        // get 2 random numbers in the range of 0 - 26
        
        Random randomGen = new Random();        // create a random generator object
        
        random1 = randomGen.nextInt(26);        // get 2 random numbers between 0 - 26  (do we need to skip any zeros??)     
        random2 = randomGen.nextInt(26);      
        
        
        // add a dash to the middle of the time string
        String stime =  String.valueOf(time);
        int pos = stime.length() / 2;
        stime = stime.substring(0,pos) + "-" + stime.substring(pos + 1);


        //  Create a Conf Number by combining the above values into a string
        
        StringBuffer conf = new StringBuffer();
        conf.append(vendor_id);
        conf.append(club_id);
        conf.append(node_id);
        conf.append("-");
        conf.append(random1);
        conf.append(stime);
        conf.append(random2);

        confNum = conf.toString();      // create a string from the above values
        
        
        //  Now save the conf num - if this fails, then most likely the conf num is not unique (caller will try again)
        
        pstmt = con.prepareStatement (
                  "INSERT INTO tsp_conf_nums (conf_code, club_id, partner_id, node, date_time, teecurr_id) " +
                  "VALUES (?, ?, ?, ?, now(), ?)");

        pstmt.clearParameters();
        pstmt.setString(1, confNum);           
        pstmt.setInt(2, club_id);           
        pstmt.setInt(3, vendor_id);           
        pstmt.setInt(4, node_id);           
        pstmt.setInt(5, teecurr_id);           

        pstmt.executeUpdate();
        
        confNumFinal = confNum;         // ok to set this now - it must have worked
        
    } catch (Exception exc) {

        logError("Utilities.getConfNum: err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
        
        try { con.close(); } 
        catch (Exception ignore) {}
    }

    return confNumFinal;

 }
 
 public static boolean getRestrictByOrig(HttpServletRequest req) {
     
     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT use_restrictByOrig FROM club5");
         
         if (rs.next() && rs.getInt("use_restrictByOrig") == 1) {
             result = true;
         }
         
     } catch (Exception exc) {
         
         logError("Utilities.getRestrictByOrig - Error getting use_restrictByOrig option from club5. Club=" + club + ", Err=" + exc.toString());
         
     } finally {
         
         Connect.close(rs, stmt);
     }
     
     return result;
 }
 
 
 public static int getSlotAccessMethod(HttpServletRequest req) {
     
     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     Statement stmt = null;
     ResultSet rs = null;
     
     int result = 0;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT slotAccessMethod FROM club5");
         if (rs.next()){
             result = rs.getInt("slotAccessMethod");
         }
         
         
     } catch (Exception exc) {
         
         logError("Utilities.getSlotAccessMethod - Error getting getSlotAccessMethod option from club5. Club=" + club + ", Err=" + exc.toString());
         
     } finally {
         
         Connect.close(rs, stmt);
     }

     return result;
 }
 
 public static int getEventSignupCount(int event_id, HttpServletRequest req) {
     
     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     int signup_count = 0;
     
     try {
         pstmt = con.prepareStatement("SELECT count(*) as signup_count FROM evntsup2b WHERE event_id = ? AND player1 <> '' AND inactive = 0");
         pstmt.clearParameters();
         pstmt.setInt(1, event_id);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             signup_count = rs.getInt("signup_count");
         }
         
     } catch (Exception e) {
         Utilities.logError("Utilities.getEventSignupCount - " + club + " - Failed getting count of event signups - Error: " + e.toString());
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return signup_count;
 }
 
 public static boolean isWaialaeHoliday(long date) {
     
     boolean holiday = false;
     
     long yy = date / 10000;                             // get year
     long mm = (date - (yy * 10000)) / 100;              // get month
     long dd = (date - (yy * 10000)) - (mm * 100);       // get day

     long dateShort = (mm * 100) + dd;
     
     if (dateShort == 101                            // New Year's Day
             || date == ProcessConstants.presDay     // President's Day
             || date == ProcessConstants.memDay      // Memorial Day
             || date == ProcessConstants.july4       // Independence Day
             || date == ProcessConstants.laborDay    // Labor Day
             || dateShort == 1111                    // Veteran's Day
             || date == ProcessConstants.tgDay       // Thanksgiving Day
             || dateShort == 1224                    // Christmas Eve
             || dateShort == 1231) {                 // New Year's Eve
         
         holiday = true;
     }
     
     return holiday;
 }
 
 public static String getWaialaePrivCategory(String mship) {
     
     String member_class = "";
     
     if (mship.equalsIgnoreCase("Ex Res Regular Golf") || mship.equalsIgnoreCase("Non Res Regular Golf") || mship.equalsIgnoreCase("Resident Regular Golf") 
             || mship.equalsIgnoreCase("Special Resident Golf")) {
         member_class = "A";
     } else if (mship.equalsIgnoreCase("Ex Res Limited Golf") || mship.equalsIgnoreCase("Non Res Limited Golf") || mship.equalsIgnoreCase("Resident Limited Golf") 
             || mship.equalsIgnoreCase("Senior Limited Golf")) {
         member_class = "B";         
     } else if (mship.equalsIgnoreCase("Ex Res Widow Golf") || mship.equalsIgnoreCase("Non Res Widow Golf") || mship.equalsIgnoreCase("Resident Widow Golf") 
             || mship.equalsIgnoreCase("Spouse/Dependent Golf")) {
         member_class = "C";
     } else if (mship.equalsIgnoreCase("Ex Res Non Golf") || mship.equalsIgnoreCase("Non Res Non Golf") || mship.equalsIgnoreCase("Res Non Golf Widow") 
             || mship.equalsIgnoreCase("Social") || mship.equalsIgnoreCase("Spouse/Dependent Non Golf") || mship.equalsIgnoreCase("Tennis")) {
         member_class = "D";
     }
     
     return member_class;
 }
 
 public static String getWaialaeTimeCategory(long date, int time, String day) {
     
     String time_category = "";
     
     if (isWaialaeHoliday(date)) {    // Holiday Dates Only
         
         if (time >= 700 && time <= 854) {
             time_category = "HOL_A";
         } else if (time >= 900 && time <= 954) {
             time_category = "HOL_B";
         } else if (time >= 1000 && time <= 1154) {
             time_category = "HOL_C";
         } else if (time >= 1200 && time <= 1324) {
             time_category = "HOL_D";
         } else if (time >= 1330) {
             time_category = "HOL_E";
         }
     } else if (day.equalsIgnoreCase("Monday")) {
         
         if (time >= 1030 && time <= 1454) {
             time_category = "MON_A";
         } else if (time >= 1500) {
             time_category = "MON_B";
         }
     } else if (day.equalsIgnoreCase("Tuesday")) {
         
         if (time >= 700 && time <= 1154) {
             time_category = "TUE_A";
         } else if (time >= 1200 && time <= 1324) {
             time_category = "TUE_B";
         } else if (time >= 1330 && time <= 1454) {
             time_category = "TUE_C";
         } else if (time >= 1500) {
             time_category = "TUE_D";
         }
     } else if (day.equalsIgnoreCase("Wednesday")) {
         
         if (time >= 700 && time <= 754) {
             time_category = "WED_A";
         } else if (time >= 800 && time <= 924) {
             time_category = "WED_B";
         } else if (time >= 930 && time <= 1124) {
             time_category = "WED_C";
         } else if (time >= 1130 && time <= 1324) {
             time_category = "WED_D";
         } else if (time >= 1330 && time <= 1454) {
             time_category = "WED_E";
         } else if (time >= 1500) {
             time_category = "WED_F";
         }
     } else if (day.equalsIgnoreCase("Thursday")) {
         
         if (time >= 700 && time <= 754) {
             time_category = "THU_A";
         } else if (time >= 800 && time <= 954) {
             time_category = "THU_B";
         } else if (time >= 1000 && time <= 1124) {
             time_category = "THU_C";
         } else if (time >= 1130 && time <= 1324) {
             time_category = "THU_D";
         } else if (time >= 1330 && time <= 1454) {
             time_category = "THU_E";
         } else if (time >= 1500) {
             time_category = "THU_F";
         }
     } else if (day.equalsIgnoreCase("Friday")) {
         
         if (time >= 700 && time <= 754) {
             time_category = "FRI_A";
         } else if (time >= 800 && time <= 954) {
             time_category = "FRI_B";
         } else if (time >= 1000 && time <= 1124) {
             time_category = "FRI_C";
         } else if (time >= 1130 && time <= 1324) {
             time_category = "FRI_D";
         } else if (time >= 1330 && time <= 1454) {
             time_category = "FRI_E";
         } else if (time >= 1500) {
             time_category = "FRI_F";
         }
     } else if (day.equalsIgnoreCase("Saturday")) {
         
         if (time >= 700 && time <= 924) {
             time_category = "SAT_A";
         } else if (time >= 930 && time <= 1024) {
             time_category = "SAT_B";
         } else if (time >= 1030 && time <= 1054) {
             time_category = "SAT_C";
         } else if (time >= 1100 && time <= 1154) {
             time_category = "SAT_D";
         } else if (time >= 1200 && time <= 1354) {
             time_category = "SAT_E";
         } else if (time >= 1400 && time <= 1454) {
             time_category = "SAT_F";
         } else if (time >= 1500) {
             time_category = "SAT_G";
         }
     } else if (day.equalsIgnoreCase("Sunday")) {
         
         if (time >= 700 && time <= 854) {
             time_category = "SUN_A";
         } else if (time >= 900 && time <= 1024) {
             time_category = "SUN_B";
         } else if (time >= 1030 && time <= 1324) {
             time_category = "SUN_C";
         } else if (time >= 1330 && time <= 1454) {
             time_category = "SUN_D";
         } else if (time >= 1500) {
             time_category = "SUN_E";
         }
     }
     
     return time_category;
 }
 
  
 public static void proSlotScripts(PrintWriter out) {
      
      out.println("<script type=\"text/javascript\" src=\"/" + rev + "/" + dynjs + "\"></script>");
}


 //
 // If we don't have a session object handy we can pass in the req object
 //
 public static boolean isFTCPuser(HttpServletRequest req) {

    HttpSession session = req.getSession(false);

    return isFTCPuser(session);
 }


 // if we already have a session object then we can simply pass it in here
 // we may want to check another variable in the session or something in the future
 public static boolean isFTCPuser(HttpSession session) {


    if (session != null) {

        try {

            String caller = (String)session.getAttribute("caller");
            if (caller != null && caller.equals( ProcessConstants.FT_PREMIER_CALLER )) return true;

        } catch (Exception ignore) { }
    }

    return false;
 }
 
 /**
  * Returns whether or not the current club is using the ForeTees Premier setup with Flexscape, based on whether the seam_caller in club5 is Premier
  * @param con Connection to club database
  * @return boolean
  */
 public static boolean isFTCPclub(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     String club = getClubDbName(con);      // get club's site name
 
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT seamless_caller FROM club5");
         
         if (rs.next()) {
             if (rs.getString("seamless_caller").equals(ProcessConstants.FT_PREMIER_CALLER)) {
                 result = true;
             }
         }
         
     } catch (Exception exc) {
         logError("Utilities.isFTCPclub - Error checking premier club status - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return result;
 }
 
 /**
  * Returns whether or not the flexid field should be displayed and editable on the admin side for this club. We need this to be able to display prior to clubs being live with Premier.
  * @param con Connection to club database
  * @return boolean
  */
 public static boolean isFTCPflexidAllowed(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     String club = getClubDbName(con);      // get club's site name
 
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT allow_flexid FROM club5");
         
         if (rs.next()) {
             if (rs.getInt("allow_flexid") == 1) {
                 result = true;
             }
         }
         
     } catch (Exception exc) {
         logError("Utilities.isFTCPflexidAllowed - Error checking allow_flexid status - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return result;
 }


 /**
  * Returns whether or not the current club is using any member sub_types
  * @param con Connection to club database
  * @return boolean
  */
 public static boolean isSubTypeClub(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     String club = getClubDbName(con);      // get club's site name
 
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT count(*) " +
                                    "FROM member2b WHERE msub_type != ''");

         if (rs.next()) {

            if (rs.getInt(1) > 0) result = true;
         }
         
     } catch (Exception exc) {
         logError("Utilities.isSubTypeClub - Error checking if club uses sub_types - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return result;
 }
 
 
 public static int isLotteryClub(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     int lottery = 0;
     
     String club = getClubDbName(con);      // get club's site name
 
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT lottery FROM club5");
         
         if (rs.next()) {
             lottery = rs.getInt("lottery");
         }
         
     } catch (Exception exc) {
         logError("Utilities.isLotteryClub - Error checking if club uses lotteries - Club= " +club+ ", ERR: " + exc.toString());
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return lottery;
 }
 
 public static Map<String, String> getHiddenMems(HttpServletRequest req) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     Map<String, String> hidden_mems = new HashMap<String, String>();
     
     try {
         
         stmt = Connect.getCon(req).createStatement();
         rs = stmt.executeQuery("SELECT username, memNum FROM member2b WHERE msub_type = 'Hide Member'");
         
         while (rs.next()) {
             hidden_mems.put(rs.getString("username"), rs.getString("memNum"));
         }
         
     } catch (Exception e) {
         Utilities.logError("Utilities.getHiddenMems - error while looking up all members with a subtype of 'Hide Member' - Error=" + e.toString());
     } finally {
         Connect.close(rs, stmt);
     }
     
     return hidden_mems;
 }


 /**
  * Returns whether or not the current club has the responsive skin enabled
  * @param con Connection to club database
  * @return boolean
  */
 public static boolean isResponsiveAllowed(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT allow_responsive FROM club5");

         if (rs.next()) result = rs.getInt(1) == 1;
         
     } catch (Exception exc) {
         
         logError("Utilities.isResponsiveAllowed - Error checking if club setting - club=" + Utilities.getClubDbName(con) + ", ERR: " + exc.toString() + ", trace=" + Utilities.getStackTraceAsString(exc));
   
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return result;
 }
 
 
 /**
  * Returns whether or not the current club has the responsive skin forced on
  * @param con Connection to club database
  * @return boolean
  */
 public static boolean isResponsiveForced(Connection con) {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean result = false;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT force_responsive FROM club5");

         if (rs.next()) result = rs.getInt(1) == 1;
         
     } catch (Exception exc) {
         
         logError("Utilities.isResponsiveForced - Error checking if club setting - club=" + Utilities.getClubDbName(con) + ", ERR: " + exc.toString() + ", trace=" + Utilities.getStackTraceAsString(exc));
         
     } finally {
         
         try { rs.close(); }
         catch (Exception ignore) {}
         
         try { stmt.close(); }
         catch (Exception ignore) {}
     }
     
     return result;
 }
 
 /**
  * Returns lists containing Tee ids
  * @param course_name Name of the course in question
  * @param req HttpServletRequest
  * @return List<String>
  */
 public static List<String> getTeeOptionIds(String course_name, HttpServletRequest req) {
     List<String>[] result = getTeeOptionsWithIds(course_name, req);
     return result[1];
 }
 
 /**
  * Returns lists containing Tee names
  * @param course_name Name of the course in question
  * @param req HttpServletRequest
  * @return List<String>
  */
 public static List<String> getTeeOptionNames(String course_name, HttpServletRequest req) {
     List<String>[] result = getTeeOptionsWithIds(course_name, req);
     return result[0];
 }
 /**
  * Returns an array of lists containing Tee names and tee_ids (tees on the tee boxes - Blue, White, etc)
  * @param course_name Name of the course in question
  * @param req HttpServletRequest
  * @return List<String>[]
  */
 public static List<String>[] getTeeOptionsWithIds(String course_name, HttpServletRequest req) {

     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;

     List<String> teeNames = new ArrayList<String>();
     List<String> teeIds = new ArrayList<String>();
     int courseId = 0;
     boolean hasTees = false;
     
     //
     //  Get the course id for the course name specified
     //
     courseId = getCourseId(course_name, con);
     
     try {

         if (courseId > 0) {

            hasTees = checkTees(courseId, con);    // does the club have tees configured?
         }
         
         if (hasTees) {
             if (club.equalsIgnoreCase("interlachen")) {
                 pstmt = con.prepareStatement(""
                         + "SELECT "
                         + "  GROUP_CONCAT(tee_name ORDER BY sort_by SEPARATOR '\\t') AS tee_names, "
                         + "  GROUP_CONCAT(tee_id ORDER BY sort_by SEPARATOR ',') AS tee_ids "
                         + " FROM tees "
                         + " WHERE course_id = ? "
                         + " AND tee_name != ? "
                         + " GROUP BY course_id ");
                 pstmt.clearParameters();
                 pstmt.setInt(1, courseId);
                 pstmt.setString(2, "Blue-Men");
             } else {
                 pstmt = con.prepareStatement(""
                         + "SELECT "
                         + "  GROUP_CONCAT(tee_name ORDER BY sort_by SEPARATOR '\\t') AS tee_names, "
                         + "  GROUP_CONCAT(tee_id ORDER BY sort_by SEPARATOR ',') AS tee_ids "
                         + " FROM tees "
                         + " WHERE course_id = ? "
                         + " GROUP BY course_id ");
                 pstmt.clearParameters();
                 pstmt.setInt(1, courseId);
             }

            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                teeNames = Arrays.asList(rs.getString("tee_names").split("\t"));// parse the individual tee names by tab
                teeIds = Arrays.asList(rs.getString("tee_ids").split(","));// parse the individual tee names by comma
            }
         }
         
     } catch (Exception exc) {

         logError("Utilities.getTeeOptionsWithIds - Error getting Tee Box Names. Club=" +club+ ", course_name=" + course_name + ", Err=" + exc.toString());

     } finally {

         try { rs.close(); }
         catch (Exception ignore) { }

         try { pstmt.close(); }
         catch (Exception ignore) { }
     }

     return new List[]{teeNames, teeIds};
 }
 
 public static int isPosPayNow(HttpServletRequest req) {
     
     Connection con = Connect.getCon(req);
     String club = reqUtil.getSessionString(req, "club", "");
     
     Statement stmt = null;
     ResultSet rs = null;
     
     int result = 0;
     
     try {
         
         stmt = con.createStatement();
         
         rs = stmt.executeQuery("SELECT pos_paynow, pos_paynow_adv FROM club5");
         
         if (rs.next()) {
             if (rs.getInt("pos_paynow_adv") == 1) {
                 result = 2;
             } else if (rs.getInt("pos_paynow") == 1) {
                 result = 1;
             }
         }
         
     } catch (Exception exc) {
         
         logError("Utilities.isPosPaynow - Error getting paynow options from club5. Club=" + club + ", Err=" + exc.toString());
         
     } finally {
         
         Connect.close(rs, stmt);
     }
     
     return result;
 }
 
 
 public static boolean checkDiaryEntry(long date, Connection con) {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     boolean diary_exists = false;
     
     try {
         pstmt = con.prepareStatement("SELECT diary_entry_id FROM diary WHERE diary_date = ?");
         pstmt.clearParameters();
         pstmt.setLong(1, date);
         
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             diary_exists = true;
         }
         
     } catch (Exception e) {
         logError("Utilities.checkDiaryEntry - Failed to look up existing diary entry - Error = " + e.toString());
     } finally {
         Connect.close(rs, pstmt);
     }
     
     return diary_exists;
 }
 
 
 /**
  * This method checks to see if a club is allowed to use the mobile app
  * @param club club name
  * @param con database connection
  * @return boolean true if successful
  */
 public static boolean allowMobileApp(String club, Connection con) { // HttpServletRequest req
     
     
    //Connection con = Connect.getCon(req);
    //String club = reqUtil.getSessionString(req, "club", "");
     
    boolean result = false;

    ResultSet rs = null;
    PreparedStatement pstmt = null;

    try {

        if (!club.equals("")) {

            // find the mobile site auth code
            pstmt = con.prepareStatement("SELECT msac FROM v5.clubs WHERE clubname = ? AND inactive = 0");
            pstmt.clearParameters();
            pstmt.setString(1, club);
            rs = pstmt.executeQuery();
            
            // if we found a record then the caller is authorized
            if (rs.next()) {

                result = (rs.getString("msac") != null && !rs.getString("msac").isEmpty()); // if not null and not empty then true
            }
        }

    } catch (Exception exc) { // any error is a fail but let's log it
        
        Utilities.logError("Utilities.allowMobileApp() club=" + club + ", error=" + exc.toString());
        
    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}
        
        //try { con.close(); }
        //catch (Exception ignore) { }
    
    }

    return result;
    
 }
 
 
 public static String getFirstLetterOfLastName(String user, Connection con) {

    String ret = "";
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT name_last " +
                "FROM member2b " +
                "WHERE username = ?;");
        
        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();

        if (rs.next()) {
            
            ret = rs.getString(1).substring(0, 1);
        }

    } catch (Exception exc) {

        logError("Utilities.getFirstLetterOfLastName: user= " +user+ ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return ret;

 }
         
 
 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 public static Authenticator getAuthenticator() {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }



/*
    public static boolean outsideAccessEnabled (int activity_id, Connection con) {


        boolean result = false;

        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM teesheet_partner_config WHERE enabled = 1;"); // add activity check if we ever do flxrez equiv

            if (rs.next()) {
                
                result = true;
            }

        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return result;

    }


    public static ArrayList<String> getAllowedOutsideGuests (int activity_id, Connection con) {


        ArrayList<String> guestList = new ArrayList<String>();

        Statement stmt = null;
        ResultSet rs = null;

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM teesheet_partner_config WHERE enabled = 1;");

            while (rs.next()) {

                guestList.add(rs.getString("guest_type"));
            }

        } catch (Exception ignore) {

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        return guestList;

    }
*/
 
 
            
}  // end of class
