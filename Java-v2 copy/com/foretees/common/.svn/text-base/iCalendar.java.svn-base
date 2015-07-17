/*****************************************************************************************
 *   iCalendar:  This class will implement support for iCalendar files
 *
 *
 *   called by:  several
 *
 *   created: 07/27/2011   Paul S.
 *
 *   last updated:
 *
 *      1/04/13  Re-update populateICS method all types for mysql 5 compatability
 *     10/04/12  Update populateICS method, type evntsup, for mysql 5 compatability
 *      8/31/11  Minor fixes - now implemented in sendEmail
 * 
 *
 *   iCal reference: http://tools.ietf.org/html/rfc2445
 *
 *
 *****************************************************************************************
 */


package com.foretees.common;

import java.sql.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.net.InetAddress;
import java.lang.management.ManagementFactory;


/*
// TODO: wrap descriptions at 75 bytes
vCalMsg.append("" +
"BEGIN:VCALENDAR\n" +
"PRODID:-//ForeTees//NONSGML v1.0//EN\n" +
"METHOD:PUBLISH\n" +
"BEGIN:VEVENT\n" +
"DTSTAMP:" + DTSTAMP + "\n" +
"DTSTART:" + tmp_time + "\n" +
iCalFoldContentLine("SUMMARY:" + tmp_summary + "\n") +
"LOCATION:" + clubName + "\n" +
iCalFoldContentLine("DESCRIPTION:" + tmp_description + "\n") +
"URL:http://www1.foretees.com/" + club + "\n" +
"END:VEVENT\n" +
"END:VCALENDAR");

*/


/**
 *
 * @author sindep
 */
public class iCalendar {

    public int id = 0;                  // id of the record we look up
    public String id_type = "";         // type of record we look up
    public String club_name = "";

    // definable parameters
    public String DTSTAMP = "";
    public String DTSTART = "";
    public String DTEND = "";
    public String SUMMARY = "";
    public String LOCATION = "";
    public String DESCRIPTION = "";
    public final String URL = "http://www1.foretees.com";

    public String ICS_FILE = "";

    private final int MAX_LENGTH = 75;        // max length of a single line

    public final String LINE_SERPARATOR = "\r\n";
    private final String FOLD_PATTERN = "\r\n "; // "\r\n\t"

    private long uid_last_time = 0;

    
 public void buildICS(Connection con) {

     if (DTSTAMP.equals("")) {

         int time = Utilities.getTime(con);
         int hour = time / 100;                     // get adjusted hour
         int min = time - (hour * 100);             // get minute value
         Calendar cal = new GregorianCalendar();
         int sec = cal.get(Calendar.SECOND);

         if (hour > 11) hour = hour - 12;           // set to 12 hr clock
         if (hour == 0) hour = 12;

         DTSTAMP = Utilities.getDate(con) + "T" + Utilities.ensureDoubleDigit(hour) + Utilities.ensureDoubleDigit(min) + Utilities.ensureDoubleDigit(sec);
     }


     // sanity check for embedded html (enable if needed)
     //DESCRIPTION = DESCRIPTION.replaceAll("<!--.*?-->", "").replaceAll("<[^>]+>", "");


     // for now use vCal 2.0 format for maximum compatability
     ICS_FILE = "" +
        "BEGIN:VCALENDAR" + LINE_SERPARATOR +
        "VERSION:2.0" + LINE_SERPARATOR +
        "PRODID:-//ForeTees//NONSGML v1.0//EN" + LINE_SERPARATOR +
        "CALSCALE:GREGORIAN" + LINE_SERPARATOR +
        "METHOD:PUBLISH" + LINE_SERPARATOR +
        "BEGIN:VEVENT" + LINE_SERPARATOR +
        "UID:" + getUniqueId() + LINE_SERPARATOR +
        "DTSTAMP:" + DTSTAMP + LINE_SERPARATOR +
        "DTSTART:" + DTSTART + LINE_SERPARATOR +
        ((!DTEND.equals("")) ? "DTEND:" + DTEND + LINE_SERPARATOR : "") +
        foldLine("SUMMARY:" + escape(SUMMARY)) + LINE_SERPARATOR +
        "LOCATION:" + escape(LOCATION) + LINE_SERPARATOR +
        foldLine("DESCRIPTION:" + escape(DESCRIPTION)) + LINE_SERPARATOR +
        "URL:" + URL + "/" + club_name + LINE_SERPARATOR +
        "END:VEVENT" + LINE_SERPARATOR + 
        "END:VCALENDAR" + LINE_SERPARATOR;

 }


 /**
  * Escape backslash, new line and punctuation according to spec (4.3.11 - Text)
  * @param player string containing guest type to check
  * @param mship string containing membership type of host member
  * @return error - true if player IS NOT one of the listed guest types, false if player IS one of the listed guest types
  */
 private String escape (String line) {

    String result = "";

    result = line.replaceAll("\\\\", "\\\\\\\\");   // escape backslashes
    result = result.replaceAll("\r?\n", "\\\\n");   // escape new lines
    result = result.replaceAll("([,;])", "\\\\$1"); // escape punctuation

    return result;

 }


 public void populateICS(Connection con) {

    //populate the ical block with data from the database

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String sql = "";

    if (id_type.equals("tee")) {

        sql = "" +
            "SELECT date, time, hr, min, courseName, player1, player2, player3, player4, player5, p1cw, p2cw, p3cw, p4cw, p5cw, p91, " +
                "(SELECT clubName FROM club5) AS clubName " +
            "FROM teecurr2 " +
            "WHERE teecurr_id = ?";
            
    } else if (id_type.equals("event")) {

        sql = "" +
            "SELECT name, date, courseName, itin, act_hr, act_min, end_hr, end_min, c_date, c_time, format, " +
                "(SELECT clubName FROM club5) AS clubName " +
            "FROM events2b " +
            "WHERE event_id = ?";

    } else if (id_type.equals("evntsup")) {

        sql = "" +
            "SELECT e.date, e.act_hr, e.act_min, e.name, e.itin, es.courseName, es.player1, es.player2, es.player3, es.player4, es.player5, es.wait, " +
                "(SELECT clubName FROM club5) AS clubName " +
            "FROM evntsup2b es " +
            "LEFT OUTER JOIN events2b e ON e.name = es.name " +
            "WHERE id = ?";

    }

    try {
             
        pstmt = con.prepareStatement( sql );
        pstmt.setInt(1, id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            LOCATION = rs.getString("clubName");

            if (id_type.equals("tee")) {

                DTSTART = buildDateTimeString(rs.getInt("date"), rs.getInt("hr"), rs.getInt("min"), 0);
                int end_hr = rs.getInt("hr") + ( (rs.getInt("p91") == 0) ? 4 : 2 );
                DTEND = buildDateTimeString(rs.getInt("date"), end_hr, rs.getInt("min"), 0);
                SUMMARY = Utilities.getSimpleTime(rs.getInt("time")) + " Tee Time";
                if (!rs.getString("courseName").equals("")) {
                    SUMMARY += " on " + rs.getString("courseName");
                    DESCRIPTION = "Course: " + rs.getString("courseName") + LINE_SERPARATOR; // "\\n";
                }
                DESCRIPTION += buildPlayerString(rs.getString("player1"), rs.getString("player2"), rs.getString("player3"),rs.getString("player4"), rs.getString("player5"),
                                                 rs.getString("p1cw"), rs.getString("p2cw"), rs.getString("p3cw"), rs.getString("p4cw"), rs.getString("p5cw"));


            } else if (id_type.equals("event")) {

                DTSTART = buildDateTimeString(rs.getInt("date"), rs.getInt("act_hr"), rs.getInt("act_min"), 0);
                DTEND = buildDateTimeString(rs.getInt("date"), rs.getInt("end_hr"), rs.getInt("end_min"), 0);
                SUMMARY = Utilities.getSimpleTime(rs.getInt("act_hr"), rs.getInt("act_min")) + " Event";
                if (!rs.getString("courseName").equals("")) {
                    SUMMARY += " on " + rs.getString("courseName");
                    DESCRIPTION = "Course: " + rs.getString("courseName") + LINE_SERPARATOR; // "\\n";
                }
                if (!rs.getString("format").equals("")) {
                    DESCRIPTION += "Format: " + rs.getString("format") + LINE_SERPARATOR + LINE_SERPARATOR + rs.getString("itin"); // "\\n\\n"
                }

            } else if (id_type.equals("evntsup")) {

                DTSTART = buildDateTimeString(rs.getInt("date"), rs.getInt("act_hr"), rs.getInt("act_min"), 0);
                DTEND = buildDateTimeString(rs.getInt("date"), rs.getInt("act_hr") + 1, rs.getInt("act_min"), 0);
              //SUMMARY = Utilities.getSimpleTime(rs.getInt("act_hr"), rs.getInt("act_min")) + " Event";
                SUMMARY = rs.getString("name") + " Event";
                if (!rs.getString("courseName").equals("")) {
                    SUMMARY += " on " + rs.getString("courseName");
                    DESCRIPTION = "Course: " + rs.getString("courseName") + LINE_SERPARATOR; // "\\n";
                }
                DESCRIPTION += "You are " + ((rs.getInt("wait") > 0) ? "on the wait list" : "registered") + " for this event." + LINE_SERPARATOR; // \\n\\n
                DESCRIPTION += buildPlayerString(rs.getString("player1"), rs.getString("player2"), rs.getString("player3"),rs.getString("player4"), rs.getString("player5"),
                                                 "", "", "", "", "");
                DESCRIPTION += LINE_SERPARATOR + LINE_SERPARATOR + rs.getString("itin");
            
            }

        } else {

            // DEBUG
            SUMMARY = "NOT FOUND: type=" + id_type + ", id="+id;

        }

    } catch (Exception exc) {

        Utilities.logError("iCalendar.populateICS: id=" + id + ", id_type=" + id_type + ", Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

 }


 private String buildDateTimeString(int date, int hr, int min, int sec) {

     return (date + "T" + Utilities.ensureDoubleDigit(hr) + Utilities.ensureDoubleDigit(min) + Utilities.ensureDoubleDigit(sec));
     
 }


 private String foldLine(String text) {

    String result = "";

    try {

        // return empty description for null text (this shouldn't happen)
        if (text == null) return result;

        // return string as-is if length less than max allowed
        if (text.length() <= MAX_LENGTH) return text;

        Vector<String> lines = new Vector<String>();
        StringBuffer line = new StringBuffer();
        StringBuffer word = new StringBuffer();

        char [] chars = text.toCharArray();

        for (int i = 0; i < chars.length; i++) {

            word.append(chars[i]);

            if (chars[i] == ' ') {

                // determine if we should append to the current line or add new
                if ( ( line.length() + word.length() ) > MAX_LENGTH ) {

                    lines.add(line.toString());
                    line.delete(0, line.length());
                }

                line.append(word);
                word.delete(0, word.length());
            }
        }

        // handle any extra chars in current word
        if (word.length() > 0) {

            if ((line.length() + word.length()) > MAX_LENGTH) {

                lines.add(line.toString());
                line.delete(0, line.length());
            }

            line.append(word);
        }

        // handle extra line
        if (line.length() > 0) {

            lines.add(line.toString());
        }

        //String [] ret = new String[lines.size()];

        int c = 0; // counter
        for (Enumeration e = lines.elements(); e.hasMoreElements(); c++) {

            result += (String) e.nextElement() + FOLD_PATTERN;


/*
            if (c == 0) {

                result += (String) e.nextElement() + LINE_SERPARATOR; // LINE_SERPARATOR

            } else {

                // start each additional line with a space
                result += " " + (String) e.nextElement() + LINE_SERPARATOR + " "; // "\\r\\n

            }
*/
        }

    } catch (Exception exc) {

        Utilities.logError("iCalendar.foldLine text=" + text + ", result=" + result + ", Err=" + exc.toString());

    }

    if (result.endsWith(FOLD_PATTERN)) result.substring(0, result.length() - FOLD_PATTERN.length());
    if (result.endsWith(LINE_SERPARATOR)) result.substring(0, result.length() - LINE_SERPARATOR.length());

    return result;

 }


 /**
  * Returns a formatted string containing the player #'s, names and tmode options
  * @param player1 String - player 1's name
  * @param player2 String - player 2's name
  * @param player3 String - player 3's name
  * @param player4 String - player 4's name
  * @param player5 String - player 5's name
  * @param pcw1 String - player 1's tmode option
  * @param pcw2 String - player 2's tmode option
  * @param pcw3 String - player 3's tmode option
  * @param pcw4 String - player 4's tmode option
  * @param pcw5 String - player 5's tmode option
  * @return String result
  */
 private String buildPlayerString(String player1, String player2, String player3, String player4, String player5,
                                  String pcw1, String pcw2, String pcw3, String pcw4, String pcw5) {

    String result = "";

    if (!player1.equals( "" )) {

        //result += "\\nPlayer 1: " + player1 + "  " + pcw1;
        result += LINE_SERPARATOR + "Player 1: " + player1 + "  " + pcw1;
    }
    if (!player2.equals( "" )) {

        result += LINE_SERPARATOR + "Player 2: " + player2 + "  " + pcw2;
    }
    if (!player3.equals( "" )) {

        result += LINE_SERPARATOR + "Player 3: " + player3 + "  " + pcw3;
    }
    if (!player4.equals( "" )) {

        result += LINE_SERPARATOR + "Player 4: " + player4 + "  " + pcw4;
    }
    if (!player5.equals( "" )) {

        result += LINE_SERPARATOR + "Player 5: " + player5 + "  " + pcw5;
    }

    return result;

 }


 /**
  * Generate a globally unique id in addr-spec format and based upon the reccomendations in section 4.8.4.7 of the ical spec
  * @return string containing unique identifier
  */
 private String getUniqueId() {


    String host_address  = "";

    try {

        //String remote_ip = req.getHeader("x-forwarded-for");
        //if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();
        InetAddress addr = InetAddress.getLocalHost();
        host_address = addr.getHostAddress();

    } catch (Exception err) {

        Utilities.logError("getUniqueId: error getting host name. msg=" + err.toString());
        
    }

    StringBuffer uid = new StringBuffer();

    uid.append(getUniqueTimestamp());
    uid.append("-");
    uid.append(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
    uid.append("@");
    uid.append(host_address);
    uid.append("-");
    uid.append(ProcessConstants.SERVER_ID);

    return uid.toString();

 }



 /**
  * Generate a timestamp that is guaranteed to be unique to this jvm
  * @return long unique timestamp
  */
 private synchronized long getUniqueTimestamp() {

    long current_time = System.currentTimeMillis();

    // current time must be greater than last time otherwise set current to last
    if (current_time < uid_last_time) current_time = uid_last_time;

    // if difference between current and last is less than a second then add a second
    if (current_time - uid_last_time < 1000) current_time += 1000;

    uid_last_time = current_time;

    return current_time;

 }

} // end of iCalendar class