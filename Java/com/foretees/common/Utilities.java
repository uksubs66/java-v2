/***************************************************************************************
 *   Utilities:  Contains common methods used throughout the ForeTees/FlxRez Application
 *
 *   created: 01-19-09   Brad K.
 *
 *   last updated:
 *
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
import java.sql.*;
import javax.servlet.http.*;
/*
import javax.servlet.*;
import javax.mail.internet.*;
import javax.mail.*;
import javax.activation.*;
*/

public class Utilities {
    
    private static String rev = ProcessConstants.REV;

    
    //**************************************************************************
    //  Method to verify that the dining system is active and emails are present
    //  in the dining_emails table.
    //  
    //  Returns true if the system is active and one or more emails are present
    //**************************************************************************
    public static boolean checkDiningStatus (Connection con) {
        
        boolean status = false;
        
        try {
            Statement stmt = null;
            Statement stmt2 = null;
            ResultSet rs = null;
            ResultSet rs2 = null;

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

            stmt.close();
            stmt2.close();
            
        } catch (Exception exc) {
            status = false;
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
            
            
            out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt;\">");
            out.println("<tr><td align=\"center\">");
            out.println("<p>" + promptText);
            if (isProshop) {
                out.println("<br><br><a href=\"/" + rev + "/servlet/Proshop_dining?dReq" + params + "\">" + linkText + "</a><br><br>");
            } else {
                out.println("<br><br><a href=\"/" + rev + "/servlet/Member_dining?dReq" + params + "\">" + linkText + "</a>");
            }
            out.println("</td></tr></table><br><br>");
            
            
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
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
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
            if (checkDiningStatus(con)) {       // Only check further information if dining system active and emails present

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT " + type + " FROM dining_config");

                if (rs.next()) {
                    if (rs.getInt(type) == 1) {
                        status = true;
                    }
                }

                stmt.close();
            }
        } catch (Exception ignore) {

            status = false;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
        
        return status;
        
    }  // End of checkDiningLink

    
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

             if (rs.next()) {
                
                 if (rs.getInt("allow_mobile") == 1) {
                     status = true;
                 }
             }
             stmt.close();
           
        } catch (Exception ignore) {

            status = false;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
        
        return status;
        
    }  // End of checkMobileSupport

    
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
 
 
 
 // ********************************************************************
 //  Get member Member Number
 // ********************************************************************

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

        pstmt.close();

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
 
 
 public static String getELS(String club, String user) {
  
    String els = "";
    
    try {

        StringEncrypter encrypter = new StringEncrypter( StringEncrypter.DES_ENCRYPTION_SCHEME, StringEncrypter.DEFAULT_ENCRYPTION_KEY );
        els = encrypter.encrypt( club + ":" + user );
        els = urlEncode( els );

    } catch (Exception e) {
        
        logError("Encrypt Error: " + e.getMessage() );

    }
    
    return els;
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
         
         result = lname + ", " + fname;
         
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
            !player.startsWith("WFGC Donations") && !player.startsWith("Golf Panelist") && !player.startsWith("Son/Daughter") &&
            !player.startsWith("spouse")) {

            passed = true;
        }
     }

     return passed;
 }

 
 public static String get_mysql_timestamp(int date, int time) {
     
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

    result = "" + yy + "-" + mm + "-" + dd + " " + hr + ":" + min + ":00";
    
    return result;
     
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


   Statement stmt = null;
   ResultSet rs = null;

   int hour = 0;
   int min = 0;
   boolean roll = false;
   boolean DST = false;           // Day Light Savings

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
   catch (Exception ignore) {
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
   
   
   time = (hour * 100) + min;

   if (roll == true) {

      time = (0 - time);        // create negative value to indicate we rolled back one day
   }

   return( time );

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
   if (yy == 2009) {

      sdate = 20090308;
      edate = 20091031;

   } else if (yy == 2010) {

      sdate = 20100314;
      edate = 20101106;

   } else if (yy == 2011) {

      sdate = 20110313;
      edate = 20111105;

   } else if (yy == 2012) {

      sdate = 20120311;   
      edate = 20121103;
      
   } else if (yy == 2013) {

      sdate = 20130310;   
      edate = 20131102;
      
   } else if (yy == 2014) {

      sdate = 20140309;   
      edate = 20141101;
      
   } else if (yy == 2015) {

      sdate = 20150308;   
      edate = 20151031;
      
   }

   if (date < sdate || date > edate) {      // if not DST today
 
      DST = false;
   }

   return( DST );

 }  // end of checkDST


 
 
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
                "WHERE courseName != ''" +
                "ORDER BY sort_by, clubparm_id");

        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        while ( rs.next() ) {
            
           result.add( rs.getString(1) );       // add course name to the list
        }

    } catch (Exception exc) {

        throw new Exception("Utilities.getCourseNames: " +
                            "Error=" + exc.getMessage());

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


 //************************************************************************
 //
 //  logError - logs system error messages to db using new con
 //
 //************************************************************************

 public static void logError(String msg) {
   
   Connection con = null;
   PreparedStatement pstmt = null;

   Calendar cal = new GregorianCalendar();                      // get todays date

   try {

      con = Connect.getCon(rev);

      if (con != null) {

         pstmt = con.prepareStatement (
              "INSERT INTO errorlog (id, err_timestamp, date, sdate, msg) " +
              "VALUES (null,now(),?,?,?)");

         pstmt.clearParameters();
         pstmt.setLong(1, (cal.get(Calendar.YEAR) * 10000) + ((cal.get(Calendar.MONTH) + 1) * 100) + cal.get(Calendar.DAY_OF_MONTH));
         pstmt.setString(2, String.valueOf(new java.util.Date()));
         pstmt.setString(3, msg + " [NODE " + ProcessConstants.SERVER_ID + "]");
         pstmt.executeUpdate();

      }

   } catch (Exception exc) {

       // write it to the text error log
       logErrorTxt(String.valueOf(new java.util.Date()) + " - " + msg + " [NODE " + ProcessConstants.SERVER_ID + "]", rev);

       // then dump a stack trace to the catalina log file
       exc.printStackTrace();

   } finally {

       try { pstmt.close(); }
       catch (Exception ignore) {}

       try { con.close(); }
       catch (Exception ignore) {}

   }

 }  // end of logError


 public final static void logErrorTxt(String msg, String club) {


   PrintWriter fout1 = null;

   try {

      //
      //  Absolute path to the clubs error log file
      //
      fout1 = new PrintWriter(new FileWriter("//usr//local//tomcat//webapps//" + club + "//errorlog.txt", true));

      //
      //  Put header line in text file
      //
      fout1.print(msg);
      fout1.println();      // output the line

   } catch (Exception e2) {

   } finally {

       try { fout1.close(); }
       catch (Exception ignore) {}

   }

 }  // end of logErrorTxt


 public static boolean enableAdvAssist(HttpServletRequest req) {

   boolean result = true;

   String ua = req.getHeader("user-agent").toLowerCase();

   if (ua.indexOf("ipad") > -1 || ua.indexOf("ipod") > -1 || ua.indexOf("iphone") > -1) {

       result = false;

   }

   return result;

 }

}  // end of class
