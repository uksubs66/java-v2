/*
 * Time conversion tools for forettes time
 * 
 * *** TODO: Change all methods to use joda-time DateTime instead of Calendar
 * 
 */
package com.foretees.common;

//import java.io.*;
import java.util.*;
import java.text.*;
//import java.util.regex.*;
import java.sql.*;
import javax.servlet.http.*;
//import org.apache.commons.lang.*;
//import java.net.URLEncoder;

import org.joda.time.*;
import org.joda.time.format.*;

/**
 *
 * @author Owner
 */
public class timeUtil {
    
    // For use with dateTime arrays
    public static final int DATE = 0;
    public static final int TIME = 1;
    
    // For use with date arrays
    public static final int YEAR = 0;
    public static final int MONTH = 1;
    public static final int DAY = 2;
    
    // For use with time arrays
    public static final int HOUR = 0;
    public static final int MINUTE = 1;
    
    public static final int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
    
    private static final String SOD_FORMAT = "yyyy-MM-dd '00:00:00.0'";
    private static final String EOD_FORMAT = "yyyy-MM-dd '23:59:59.999'";
    
    final public static String[] DAYS_OF_WEEK = new String[] {
                      "Sunday",
                      "Monday",
                      "Tuesday",
                      "Wednesday",
                      "Thursday",
                      "Friday",
                      "Saturday"
                    };
    
    final public static String[] DAYS_OF_WEEK_LOWER = new String[] {
                      "sunday",
                      "monday",
                      "tuesday",
                      "wednesday",
                      "thursday",
                      "friday",
                      "saturday"
                    };

    /*
     * 
     * Unix time utilities
     * 
     */
    private static final Map<String, String> ftToJavaTzMap = getFtToJavaTzMap();

    /*
     * Map to convert fourtees timezone strings to proper java timezone IDs
     */
    private static Map<String, String> getFtToJavaTzMap() {

        Map<String, String> result = new HashMap<String, String>();
        result.put("Eastern", "America/New_York");
        result.put("Mountain", "America/Denver"); // With DST
        result.put("Arizona", "America/Phoenix"); // No DST
        result.put("Pacific", "America/Los_Angeles");
        result.put("Hawaiian", "America/Adak");
        result.put("Saudi", "Asia/Riyadh");
        result.put("Central", "America/Chicago");
        return result;

        /*
         * see: http://joda-time.sourceforge.net/timezones.html
         */

    }
    
    public static DateTimeZone getServerTimeZone() {
        return getClubTimeZone("");
    }

    public static DateTimeZone getClubTimeZone(HttpServletRequest req) {
        return DateTimeZone.forID(getClubTimeZoneId(req));
    }
    
    public static DateTimeZone getClubTimeZone(Connection con) {
        return DateTimeZone.forID(getClubTimeZoneId(con));
    }
    
    public static DateTimeZone getClubTimeZone(String timeZoneId) {
        return DateTimeZone.forID(getClubTimeZoneId(timeZoneId));
    }
    
    public static String getClubTimeZoneId(HttpServletRequest req) {

        String javaTzId = reqUtil.getRequestString(req, ProcessConstants.RQA_TIMEZONE, null);

        if (javaTzId == null) {
            // TZ not cached
            // Lookup timezone
            Connection con = Connect.getCon(req);
            javaTzId = getClubTimeZoneId(con);
            // Cache for later use
            if(req != null){
                req.setAttribute(ProcessConstants.RQA_TIMEZONE, javaTzId);
            }
        }
        return javaTzId;

    }

    public static String getClubTimeZoneId(Connection con) {

        return getClubTimeZoneId(getClubFtTimeZoneId(con));

    }
    
    public static String getClubTimeZoneId(String timeZoneId) {

        if(DateTimeZone.getAvailableIDs().contains(timeZoneId)){
            // It's a valid timezone string
            return timeZoneId;
        }
        String javaTzId = ftToJavaTzMap.get(timeZoneId);
        if (javaTzId == null) {
            // Default to central timezone if none is found for the club
            javaTzId = "America/Chicago";
        }

        return javaTzId;

    }

    public static String getClubFtTimeZoneId(Connection con) {

        Statement stmt = null;
        ResultSet rs = null;

        String adv_zone = "";

        //
        //  get the club's time zone
        //
        try {

            stmt = con.createStatement();        // create a statement

            rs = stmt.executeQuery("SELECT adv_zone "
                    + "FROM club5 WHERE clubName != ''");

            if (rs.next()) {

                adv_zone = rs.getString(1);
            }
            stmt.close();

        } catch (Exception ignore) {
        }

        return adv_zone;

    }
    
    public static String getStringDateYYYYMMDD(int date){
        int[] date_a = parseIntDate(date);
        return String.format("%04d/%02d/%02d", date_a[YEAR], date_a[MONTH], date_a[DAY]);
    }
    public static String getDbDate(HttpServletRequest req){
        return getDbDate(getClubDate(req));
    }
    public static String getDbDate(int date){
        int[] date_a = parseIntDate(date);
        return String.format("%04d-%02d-%02d", date_a[YEAR], date_a[MONTH], date_a[DAY]);
    }
    public static String getDbDateTime(){
        Connection con = Connect.getCon(ProcessConstants.REV);
        Statement stmt = null;
        ResultSet rs = null;
        String cur_time = null;
        try {
            stmt = con.createStatement();        // create a statement
            rs = stmt.executeQuery("SELECT now() as cur_time");
            if (rs.next()) {
                cur_time = rs.getString("cur_time");
            }
            stmt.close();
        } catch (Exception e) {
            Connect.logError("timeUtil.getDbDateTime(): Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
        }
        return cur_time;
    }
    public static String getDbDateTime(int date, int time){
        int[] date_a = parseIntDate(date);
        int[] time_a = parseIntTime(time);
        return String.format("%04d-%02d-%02d %02d:%02d:00", date_a[YEAR], date_a[MONTH], date_a[DAY], time_a[HOUR], time_a[MINUTE]);
    }
    public static String getDbDateTime(DateTimeZone tz, long unixTime){
        int[] datetime = getDateTime(tz, unixTime);
        return getDbDateTime(datetime[DATE], datetime[TIME]);
    }
    public static String getStringDateMMDDYYYY(int date){
        int[] date_a = parseIntDate(date);
        return String.format("%02d/%02d/%04d", date_a[MONTH], date_a[DAY], date_a[YEAR]);
    }
    public static String getStringDateMDYYYY(int date){
        int[] date_a = parseIntDate(date);
        return date_a[MONTH]+"/"+date_a[DAY]+"/"+date_a[YEAR];
    }
    
    public static String get24HourTime(int time){
        int[] time_a = parseIntTime(time);
        return String.format("%02d:%02d", time_a[HOUR], time_a[MINUTE]);
    }
    
    public static String get12HourTime(int time){
        return formatDate(null, 20140101, time, "h:mm aaa");
    }

    public static int[] parseIntTime(int time) {

        if (time < 0) {
            time = 0;
        }

        int hr = time / 100;             // get hr
        int min = time - (hr * 100);    // get minute

        return new int[]{hr, min};

    }

    public static int[] parseIntDate(int date) {

        int year = date / 10000;
        int month = (date - (year * 10000)) / 100;
        int day = (date - (year * 10000)) - (month * 100);

        return new int[]{year, month, day};

    }

    public static int buildIntTime(int hr, int min) {
        return (hr * 100) + min;
    }

    public static int buildIntDate(int year, int month, int day) {
        return (year * 10000) + (month * 100) + day;
    }
    
    public static int buildIntDate(DateTime dt) {
        return buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth());
    }
    
    public static int getIntDateFromString(DateTimeZone tz, String date){
        DateTime dt = getJodaDateTimeFromDb(tz, date);
        return buildIntDate(dt);
    }

    public static long getCurrentUnixTime() {

        Calendar cal = Calendar.getInstance();
        return cal.getTimeInMillis();

    }
    
    public static String getDayOfWeek(int date){
        
        Calendar cal = getClubCalendar(null, date);
        return DAYS_OF_WEEK[cal.get(Calendar.DAY_OF_WEEK)-1];
        
    }
    
    public static String getDayOfWeekLower(int date){
        
        Calendar cal = getClubCalendar(null, date);
        return DAYS_OF_WEEK_LOWER[cal.get(Calendar.DAY_OF_WEEK)-1];
        
    }
    
    public static String getVerboseDuration(int minutes){
        
        StringBuilder result = new StringBuilder();
        
        double hours = Math.floor(minutes / 60);
        if(hours > 0){
            result.append((int)hours);
            result.append(" Hour");
            if(hours > 1){
                result.append("s");
            }
            double remainder = minutes - (hours * 60);
            if(remainder > 0){
                result.append(", ");
                result.append((int)remainder);
                result.append(" Minutes");
            }
        } else {
            result.append(minutes);
            result.append(" Minutes");
        }
        
        return result.toString();
        
    }
    
    public static String getVerboseDateTime(HttpServletRequest req, long utime){
        int[] dateTime = getClubDateTime(req, utime);
        return getVerboseDate(req, dateTime[DATE], dateTime[TIME]);
    }
    
    public static String getVerboseDate(HttpServletRequest req, long utime){
        int[] dateTime = getClubDateTime(req, utime);
        return getVerboseDate(req, dateTime[DATE]);
    }
    
    public static String getShortVerboseDate(HttpServletRequest req, int date){
        return formatDate(req, date, 0, "E, MMM '%od%', yyyy");
    }
    
    public static String getVerboseDate(HttpServletRequest req, int date){
        return formatDate(req, date, 0, "EEEE, MMMM '%od%', yyyy");
    }
    
    public static String getShortVerboseDate(HttpServletRequest req, int date, int time){
        return formatDate(req, date, time, "E., MMM. '%od%', yyyy 'at' h:mm aaa");
    }
    
    public static String getVerboseDate(HttpServletRequest req, int date, int time){
        return formatDate(req, date, time, "EEEE, MMMM '%od%', yyyy 'at' h:mm aaa");
    }
    
    public static String getDateForFileName(){
        long utime = getCurrentUnixTime();
        return formatTzDate(getServerTimeZone(), utime, "yyyyMMdd'_'hhmmss");
    }
    
    // Converts a given string date/datetime to YYYY-MM-DD 00:00:00.0
    public static String getMySqlSOD(String dateTime){
        return formatTzDate(dateTime, SOD_FORMAT);
    }
    
     // Converts a given string date/datetime to YYYY-MM-DD 23:59:59.999
    public static String getMySqlEOD(String dateTime){
        return formatTzDate(dateTime, EOD_FORMAT);
    }
    
    public static String formatTzDate(String dateTime, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        return formatTzDate(getServerTimeZone(), dateTime, format);
    }
    
    public static String formatTzDate(DateTimeZone tz, String dateTime, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        return formatTzDate(tz, getUnixTimeFromDb(tz, dateTime), format);
    }
    public static String formatTzDate(long utime, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        DateTimeZone tz = getServerTimeZone();
        int[] dateTime = getDateTime(tz, utime);
        return formatTzDate(tz, dateTime[DATE], dateTime[TIME], format);
    }
    
    public static String formatTzDate(DateTimeZone tz, long utime, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        int[] dateTime = getDateTime(tz, utime);
        return formatTzDate(tz, dateTime[DATE], dateTime[TIME], format);
    }
    
    public static String formatDate(HttpServletRequest req, long utime, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        int[] dateTime = getClubDateTime(req, utime);
        return formatDate(req, dateTime[DATE], dateTime[TIME], format);
    }
    
    public static String formatDate(HttpServletRequest req, int date, String format){
        /*
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        return formatDate(req, date, 0, format);
    }
    
    public static String formatDate(HttpServletRequest req, int date, int time, String format){
        
        /*
         * *** TODO: CONVERT TO USE JODA-TIME  ***
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        
        
        
        DateTime dt = getClubJodaDateTime(req, date, time);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(format.replaceAll("'*%od%'*","'"+getOrdinal(dt.getDayOfMonth())+"'"));
        // SimpleDateFormat dosn't support ordinals, so we'll replace '%od%' with the ordinal day
        return fmt.print(dt);
        
    }
    
    public static String formatTzDate(DateTimeZone tz, int date, int time, String format){
        
        /*
         * *** TODO: CONVERT TO USE JODA-TIME  ***
         * See: http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDateFormat.html
         */
        
        
        
        DateTime dt = getJodaDateTime(tz, date, time);
        DateTimeFormatter fmt = DateTimeFormat.forPattern(format.replaceAll("'*%od%'*","'"+getOrdinal(dt.getDayOfMonth())+"'"));
        // SimpleDateFormat dosn't support ordinals, so we'll replace '%od%' with the ordinal day
        return fmt.print(dt);
        
    }

    public static String getOrdinal(int day) {
        if (day >= 11 && day <= 13) {
            return day+"th";
        }
        switch (day % 10) {
            case 1:
                return day+"st";
            case 2:
                return day+"nd";
            case 3:
                return day+"rd";
            default:
                return day+"th";
        }
    }
    
    public static int[] serverToClubDateTimeFromCon(Connection con, int date, int time) {

        return getClubDateTimeFromCon(con,getClubUnixTime(null, date, time));
         
    }
    
    public static int[] serverToClubDateTimeFromTzId(String timeZoneId, int date, int time) {

        return getClubDateTimeFromTzId(timeZoneId,getClubUnixTime(null, date, time));
         
    }
    
    
    public static int[] serverToClubDateTime(HttpServletRequest req, int date, int time) {

        return getClubDateTime(req,getClubUnixTime(null, date, time));
         
    }
    
    public static int[] clubToServerDateTimeFromCon(Connection con, int date, int time) {
        
        return getClubDateTime(getClubUnixTimeFromCon(con, date, time));
         
    }
    
    public static int[] clubToServerDateTimeFromTzId(String timeZoneId, int date, int time) {

        return getClubDateTime(getClubUnixTimeFromTzId(timeZoneId, date, time));
         
    }
    
    public static int[] clubToServerDateTime(HttpServletRequest req, int date, int time) {

        return getClubDateTime(getClubUnixTime(req, date, time));
         
    }

    public static boolean isValidDate(String date){
        
        boolean result = false;
        try{
            Long test = timeUtil.getUnixTimeFromDb(date);
            result = true;
        }catch(Exception e){
            
        }
        
        return result;
        
    }
    
    public static Calendar getClubCalendarFromDb(HttpServletRequest req, String sdate_time) {
        
        return getCalendarFromDb(getClubTimeZone(req), sdate_time);
        
    }
    
    public static Calendar getCalendarFromDb(DateTimeZone tz, String sdate_time) {
        
        return getJodaDateTimeFromDb(tz, sdate_time).toCalendar(Locale.ENGLISH);
        
    }
    
    public static DateTime getJodaDateTimeFromDb(DateTimeZone tz, String sdate_time) {
        
        if(sdate_time.matches("^[0-9]{2}/[0-9]{2}/[0-9]{4}$")){
            return DateTime.parse(sdate_time,DateTimeFormat.forPattern("MM/dd/yyyy").withZone(tz));
        } else if(sdate_time.matches("^[0-9]{2}-[0-9]{2}-[0-9]{4}$")){
            return DateTime.parse(sdate_time,DateTimeFormat.forPattern("MM-dd-yyyy").withZone(tz));
        } else if(sdate_time.length() == 10){
            return DateTime.parse(sdate_time,DateTimeFormat.forPattern("yyyy-MM-dd").withZone(tz));
        } else if(sdate_time.length() == 19){
            return DateTime.parse(sdate_time,DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").withZone(tz));
        } else {
            return DateTime.parse(sdate_time,DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.S").withZone(tz));
        }
        
    }
    
    public static long getClubUnixTimeFromDb(HttpServletRequest req, String sdate_time) {

        return getJodaDateTimeFromDb(getClubTimeZone(req), sdate_time).getMillis();

    }
    
    public static long getServerUnixTimeFromDb(String sdate_time) {

        return getJodaDateTimeFromDb(getServerTimeZone(), sdate_time).getMillis();

    }
    
    public static int[] getClubDateTimeFromDb(HttpServletRequest req, String sdate_time) {
        
        return getClubDateTime(req, getClubUnixTimeFromDb(req, sdate_time));
        
    }
    
    public static int[] getIntDateTimeFromString(String sdate_time) {
        
        DateTime dt = getJodaDateTimeFromDb(getServerTimeZone(), sdate_time);
        
        return new int[]{buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()), buildIntTime(dt.getHourOfDay(), dt.getMinuteOfHour())};
        
    }
    
    public static int getIntDateFromString(String sdate_time) {
        return getIntDateTimeFromString(sdate_time)[DATE];
    }
    
    public static long getUnixTimeFromDb(DateTimeZone tz, String sdate_time) {
        
        return getJodaDateTimeFromDb(tz, sdate_time).getMillis();

    }
    
    public static long getUnixTimeFromDb(String sdate_time) {
        
        return getJodaDateTimeFromDb(getServerTimeZone(), sdate_time).getMillis();

    }
    
    public static int[] getDateTimeFromDb(DateTimeZone tz, String sdate_time) {
        
        return getDateTime(tz, getUnixTimeFromDb(tz, sdate_time));
        
    }
    
    public static long getClubUnixTimeFromTzId(String timeZoneId, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getClubUnixTimeFromTzId(timeZoneId, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }

    public static long getClubUnixTimeFromTzId(String timeZoneId, int year, int month, int day) {

        return getClubUnixTimeFromTzId(timeZoneId, year, month, day, 0, 0, 0);

    }

    public static long getClubUnixTimeFromTzId(String timeZoneId, int year, int month, int day, int hour, int min) {

        return getClubUnixTimeFromTzId(timeZoneId, year, month, day, hour, min, 0);

    }

    public static long getClubUnixTimeFromTzId(String timeZoneId, int year, int month, int day, int hour, int min, int second) {

        return getJodaDateTime(year, month, day, hour, min, second, getClubTimeZone(timeZoneId)).getMillis();
        //return getClubCalendar(req, year, month, day, hour, min, second).getTimeInMillis();

    }
    
    public static long getClubUnixTimeFromCon(Connection con, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getClubUnixTimeFromCon(con, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }

    public static long getClubUnixTimeFromCon(Connection con, int year, int month, int day) {

        return getClubUnixTimeFromCon(con, year, month, day, 0, 0, 0);

    }

    public static long getClubUnixTimeFromCon(Connection con, int year, int month, int day, int hour, int min) {

        return getClubUnixTimeFromCon(con, year, month, day, hour, min, 0);

    }

    public static long getClubUnixTimeFromCon(Connection con, int year, int month, int day, int hour, int min, int second) {

        return getJodaDateTime(year, month, day, hour, min, second, getClubTimeZone(con)).getMillis();
        //return getClubCalendar(req, year, month, day, hour, min, second).getTimeInMillis();

    }
    
    public static boolean isDST(HttpServletRequest req) {
        
        DateTimeZone tz  = getClubTimeZone(req);
        return tz.isStandardOffset(getCurrentUnixTime());

    }
    
    public static boolean isDST(HttpServletRequest req, int date) {
        
        DateTimeZone tz  = getClubTimeZone(req);
        return tz.isStandardOffset(getClubUnixTime(req, date, 0));

    }
    
    public static boolean isDST(HttpServletRequest req, int date, int time) {
        
        DateTimeZone tz  = getClubTimeZone(req);
        return tz.isStandardOffset(getClubUnixTime(req, date, time));

    }
    
    public static long getClubUnixTime(HttpServletRequest req, int date) {

        return getClubUnixTime(req, date, 0);

    }

    public static long getClubUnixTime(HttpServletRequest req, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getClubUnixTime(req, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }

    public static long getClubUnixTime(HttpServletRequest req, int year, int month, int day) {

        return getClubUnixTime(req, year, month, day, 0, 0, 0);

    }

    public static long getClubUnixTime(HttpServletRequest req, int year, int month, int day, int hour, int min) {

        return getClubUnixTime(req, year, month, day, hour, min, 0);

    }

    public static long getClubUnixTime(HttpServletRequest req, int year, int month, int day, int hour, int min, int second) {

        return getJodaDateTime(year, month, day, hour, min, second, getClubTimeZone(req)).getMillis();
        //return getClubCalendar(req, year, month, day, hour, min, second).getTimeInMillis();

    }

    private static Calendar getClubCalendar(HttpServletRequest req) {

        return new DateTime(getClubTimeZone(req)).toCalendar(Locale.ENGLISH);

    }

    private static Calendar getClubCalendar(HttpServletRequest req, int date) {

        return getClubCalendar(req, date, 0);

    }

    private static Calendar getClubCalendar(HttpServletRequest req, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getClubCalendar(req, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }
    
    
    private static Calendar getCalendar(int date, int time, String tz) {
        
        return getCalendar(date, time, DateTimeZone.forID(tz));

    }
    
    private static Calendar getCalendar(int date, int time, DateTimeZone tz) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getCalendar(datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0, tz);

    }


    private static Calendar getClubCalendar(HttpServletRequest req, int year, int month, int day, int hour, int min, int second) {
        return getCalendar(year, month, day, hour, min, second, getClubTimeZone(req));
    }
    
    private static Calendar getCalendar(int year, int month, int day, int hour, int min, int second, DateTimeZone tz) {
        return getJodaDateTime(year, month, day, hour, min, second, tz).toCalendar(Locale.ENGLISH);

    }
    
    public static int getDayNumOfWeek(int date) {
        
        return getClubJodaDateTime(null, date).getDayOfWeek();

    }
    
    public static DateTime getClubJodaDateTime(HttpServletRequest req) {

        return new DateTime(getClubTimeZone(req));

    }
    
    public static DateTime getClubJodaDateTime(HttpServletRequest req, int date) {

        int[] datea = parseIntDate(date);
        return getClubJodaDateTime(req, datea[YEAR], datea[MONTH], datea[DAY], 0, 0, 0);

    }
    
    public static DateTime getClubJodaDateTime(HttpServletRequest req, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getClubJodaDateTime(req, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }
    
    public static DateTime getClubJodaDateTime(HttpServletRequest req, int year, int month, int day, int hour, int min, int second) {
        return getJodaDateTime(year, month, day, hour, min, second, getClubTimeZone(req));
    }
    
    public static DateTime getJodaDateTime(DateTimeZone tz) {

        return new DateTime(tz);

    }
    
    public static DateTime getJodaDateTime(DateTimeZone tz, int date) {

        int[] datea = parseIntDate(date);
        return getJodaDateTime(tz, datea[YEAR], datea[MONTH], datea[DAY], 0, 0, 0);

    }
    
    public static DateTime getJodaDateTime(DateTimeZone tz, int date, int time) {

        int[] datea = parseIntDate(date);
        int[] timea = parseIntTime(time);
        return getJodaDateTime(tz, datea[YEAR], datea[MONTH], datea[DAY], timea[HOUR], timea[MINUTE], 0);

    }
    
    public static DateTime getJodaDateTime(DateTimeZone tz, int year, int month, int day, int hour, int min, int second) {
        return getJodaDateTime(year, month, day, hour, min, second, tz);
    }
    
    public static DateTime getJodaDateTime(int year, int month, int day, int hour, int min, int second, DateTimeZone tz) {
        if(month < 1){
            month = 1;
        }
        if(month > 12){
            month = 12;
        }
        DateTime dtc = new DateTime(year, month, 1, 1, 0, 0);
        int daysInMonth = dtc.dayOfMonth().getMaximumValue();
        int hoursInDay = dtc.hourOfDay().getMaximumValue();
        int minHours = dtc.hourOfDay().getMinimumValue();
        int minutesInHour = dtc.minuteOfHour().getMaximumValue();
        int minMinutes = dtc.minuteOfHour().getMinimumValue();
        int secondsInMinute = dtc.secondOfMinute().getMaximumValue();
        int minSeconds = dtc.secondOfMinute().getMinimumValue();
        if(day < 1){
            day = 1;
        }
        if(day > daysInMonth){
            day = daysInMonth;
        }
        if(hour > hoursInDay){
            hour = hoursInDay;
        }
        if(hour < minHours){
            hour = minHours;
        }
        if(min > minutesInHour){
            min = minutesInHour;
        }
        if(min < minMinutes){
            min = minMinutes;
        }
        if(second > secondsInMinute){
            second = secondsInMinute;
        }
        if(second < minSeconds){
            second = minSeconds;
        }
        
        return new DateTime(year, month, day, hour, min, second, tz);

    }
    
    public static DateTime getJodaDateTime(int year, int month, int day, int hour, int min) {
        if(month < 1){
            month = 1;
        }
        if(month > 12){
            month = 12;
        }
        DateTime dtc = new DateTime(year, month, 1, 1, 0, 0);
        int daysInMonth = dtc.dayOfMonth().getMaximumValue();
        int hoursInDay = dtc.hourOfDay().getMaximumValue();
        int minHours = dtc.hourOfDay().getMinimumValue();
        int minutesInHour = dtc.minuteOfHour().getMaximumValue();
        int minMinutes = dtc.minuteOfHour().getMinimumValue();
        //int secondsInMinute = dtc.secondOfMinute().getMaximumValue();
        //int minSeconds = dtc.secondOfMinute().getMinimumValue();
        if(day < 1){
            day = 1;
        }
        if(day > daysInMonth){
            day = daysInMonth;
        }
        if(hour > hoursInDay){
            hour = hoursInDay;
        }
        if(hour < minHours){
            hour = minHours;
        }
        if(min > minutesInHour){
            min = minutesInHour;
        }
        if(min < minMinutes){
            min = minMinutes;
        }
        
        return new DateTime(year, month, day, hour, min, 0);

    }
    
    public static DateTime getJodaDateTime(int year, int month, int day) {
        if(month < 1){
            month = 1;
        }
        if(month > 12){
            month = 12;
        }
        DateTime dtc = new DateTime(year, month, 1, 1, 0, 0);
        int daysInMonth = dtc.dayOfMonth().getMaximumValue();
        //int hoursInDay = dtc.hourOfDay().getMaximumValue();
        //int minHours = dtc.hourOfDay().getMinimumValue();
        //int minutesInHour = dtc.minuteOfHour().getMaximumValue();
        //int minMinutes = dtc.minuteOfHour().getMinimumValue();
        //int secondsInMinute = dtc.secondOfMinute().getMaximumValue();
        //int minSeconds = dtc.secondOfMinute().getMinimumValue();
        if(day < 1){
            day = 1;
        }
        if(day > daysInMonth){
            day = daysInMonth;
        }
        
        return new DateTime(year, month, day, 0, 0, 0);

    }
    
    public static int getClubTime(HttpServletRequest req, long unixTime) {
        return getClubDateTime(req, unixTime)[TIME];
    }
    
    public static int getClubDate(HttpServletRequest req, long unixTime) {
        return getClubDateTime(req, unixTime)[DATE];
    }
    
    public static int getTime() {
        return getDateTime(getServerTimeZone(), getCurrentUnixTime())[TIME];
    }
    
    public static int getDate() {
        return getDateTime(getServerTimeZone(), getCurrentUnixTime())[DATE];
    }
    
    public static int getTime(String club) {
        return getDateTime(getClubTimeZone(club), getCurrentUnixTime())[TIME];
    }
    
    public static int getDate(String club) {
        return getDateTime(getClubTimeZone(club), getCurrentUnixTime())[DATE];
    }
    
    public static int getTime(long unixTime) {
        return getDateTime(getServerTimeZone(), unixTime)[TIME];
    }
    
    public static int getDate(long unixTime) {
        return getDateTime(getServerTimeZone(), unixTime)[DATE];
    }
    
    public static int getTime(DateTimeZone tz, long unixTime) {
        return getDateTime(tz, unixTime)[TIME];
    }
    
    public static int getDate(DateTimeZone tz, long unixTime) {
        return getDateTime(tz, unixTime)[DATE];
    }
    
    public static int[] getClubDateTime(HttpServletRequest req) {
        return getClubDateTime(getClubTimeZone(req));
    }
    
    public static int[] getClubDateTime(Connection con) {
        return getClubDateTime(getClubTimeZone(con));
    }
    
    public static int[] getClubDateTime(DateTimeZone tz) {
/*
        Calendar cal = getClubCalendar(req);
        
        return new int[]{
                    buildIntDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)),
                    buildIntTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                };
  */      
        DateTime dt = new DateTime().withZone(tz);
        
        return new int[]{
                    buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()),
                    buildIntTime(dt.getHourOfDay(), dt.getMinuteOfHour())
                };
    }
    
    public static int getClubTime(HttpServletRequest req) {
        return getClubDateTime(req)[TIME];
    }
    
    public static int getClubDate(HttpServletRequest req) {
        return getClubDateTime(req)[DATE];
    }
    
    public static int getClubTime(Connection con) {
        return getClubDateTime(con)[TIME];
    }
    
    public static int getClubDate(Connection con) {
        return getClubDateTime(con)[DATE];
    }
    
    public static int[] getClubDateTimeFromTzId(String timeZoneId, long unixTime) {

        DateTime dt = new DateTime(unixTime).withZone(getClubTimeZone(timeZoneId));
        
        return new int[]{
                    buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()),
                    buildIntTime(dt.getHourOfDay(), dt.getMinuteOfHour())
                };
    }
    
    
    public static int[] getClubDateTimeFromCon(Connection con, long unixTime) {

        DateTime dt = new DateTime(unixTime).withZone(getClubTimeZone(con));
        
        return new int[]{
                    buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()),
                    buildIntTime(dt.getHourOfDay(), dt.getMinuteOfHour())
                };
    }
    
    public static int[] getClubDateTime(long unixTime) {
        HttpServletRequest no_req = null;
        return getDateTime(getClubTimeZone(no_req), unixTime);
        
    }
    
    public static int[] getClubDateTime(HttpServletRequest req, long unixTime) {
        
        return getDateTime(getClubTimeZone(req), unixTime);
        
    }
    
    public static int[] getDateTime(DateTimeZone tz, long unixTime) {

        DateTime dt = new DateTime(unixTime).withZone(tz);
        
        return new int[]{
                    buildIntDate(dt.getYear(), dt.getMonthOfYear(), dt.getDayOfMonth()),
                    buildIntTime(dt.getHourOfDay(), dt.getMinuteOfHour())
                };
    }
    
    public static int getServerTzOffset() {

        return getClubTimeZone("").getOffset(new DateTime())/1000/60/60;
        
    }
    
    public static int getClubTzOffset(HttpServletRequest req, long unixTime) {

        return getClubTimeZone(req).getOffset(new DateTime(unixTime))/1000/60/60;
        
    }
    
    public static int getClubTzOffset(Connection con) {
        
        return getClubTimeZone(con).getOffset(new DateTime())/1000/60/60;
        
    }
    
    public static int getClubTzOffset(String timeZoneId) {
        
        return getClubTimeZone(timeZoneId).getOffset(new DateTime())/1000/60/60;
        
    }
    
    public static int getClubTzOffset(HttpServletRequest req) {
        
        return getClubTimeZone(req).getOffset(new DateTime())/1000/60/60;
        
    }

    public static int[] add(HttpServletRequest req, int date, int time, int changeType, int change) {

        Calendar cal = getClubCalendar(req, date, time);
        cal.add(changeType, change);

        return new int[]{
                    buildIntDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)),
                    buildIntTime(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
                };
    }

    public static int[] addMinutes(HttpServletRequest req, int date, int time, int minuteChange) {
        return add(req, date, time, Calendar.MINUTE, minuteChange);
    }

    public static long addClubUnixTimeMinutes(HttpServletRequest req, int minuteChange) {

        Calendar cal = getClubCalendar(req);
        cal.add(Calendar.MINUTE, minuteChange);

        return cal.getTimeInMillis();
    }

    public static long addClubUnixTime(HttpServletRequest req, int changeType, int change) {

        Calendar cal = getClubCalendar(req);
        cal.add(changeType, change);

        return cal.getTimeInMillis();
    }

    public static long addClubUnixTime(HttpServletRequest req, int date, int time, int changeType, int change) {

        Calendar cal = getClubCalendar(req, date, time);
        cal.add(changeType, change);

        return cal.getTimeInMillis();
    }

    public static long addClubUnixTimeMinutes(HttpServletRequest req, int date, int time, int minuteChange) {
        return addClubUnixTime(req, date, time, Calendar.MINUTE, minuteChange);
    }
    
    public static long addUnixTimeMinutes(long unixTime, int minuteChange) {
        return new DateTime(unixTime).plusMinutes(minuteChange).getMillis();
    }
    
    public static long addUnixTimeDays(long unixTime, int dayChange) {
        return new DateTime(unixTime).plusDays(dayChange).getMillis();
    }
    
    public static long addUnixTimeHours(long unixTime, int hourChange) {
        return new DateTime(unixTime).plusDays(hourChange).getMillis();
    }
    
    public static int daysBetween(HttpServletRequest req, long startDate, long endDate) {

        return daysBetween(getClubDate(req, startDate), getClubDate(req, endDate));

    }
    
    public static int daysBetween(DateTimeZone tz, long startDate, long endDate) {

        return daysBetween(getDate(tz, startDate), getDate(tz, endDate));

    }
    
    public static int daysBetween(int startDate, int endDate) {
        
        int[] datea1 = parseIntDate(startDate);
        int[] datea2 = parseIntDate(endDate);
        
        // This is probably what we should use, but we need to test it first.
        // We should pass our request object for club TZ calc., since it could have
        // an effect on "days between" in some edge cases (though, those edge cases would require time in addition to date).  
        // The old way is susceptible to this as well.
        // Even without the club's TZ, this should be better than the old way. 
        //return Days.daysBetween(
        //        getJodaDateTime(datea1[YEAR], datea1[MONTH], datea1[DAY]).toLocalDate(), 
        //        getJodaDateTime(datea2[YEAR], datea2[MONTH], datea2[DAY]).toLocalDate()).getDays();

        // For now, we'll continue with this
        return daysBetween(
                new GregorianCalendar(datea1[YEAR], datea1[MONTH]-1, datea1[DAY]),
                new GregorianCalendar(datea2[YEAR], datea2[MONTH]-1, datea2[DAY])
                );

    }
    
    public static int partialMonthsBetween(int startDate, int endDate) {
        int[] sd = parseIntDate(startDate);
        int[] ed = parseIntDate(endDate);
        DateTimeZone tz = getClubTimeZone("");
        DateTime date1 = getJodaDateTime(sd[YEAR], sd[MONTH], 1, 0, 0, 0, tz);
        DateTime date2 = getJodaDateTime(ed[YEAR], ed[MONTH], 1, 0, 0, 0, tz);
        return Months.monthsBetween(date1, date2).getMonths();
    }
    
    public static int minutesBetween(int startDate, int startTime, int endDate, int endTime) {
        return minutesBetween(getClubUnixTime(null, startDate, startTime), getClubUnixTime(null, endDate, endTime));
    }
    
    public static int minutesBetween(long startUnixTime, long endUnixTime) {
        return Minutes.minutesBetween(new DateTime(startUnixTime), new DateTime(endUnixTime)).getMinutes();
    }
    
    public static int ceilMinutesBetween(long startUnixTime, long endUnixTime) {
        return (int)Math.ceil(secondsBetween(startUnixTime, endUnixTime)/60);
    }
    
    public static int secondsBetween(long startUnixTime, long endUnixTime) {
        return Seconds.secondsBetween(new DateTime(startUnixTime), new DateTime(endUnixTime)).getSeconds();
    }
    
    private static int daysBetween(Calendar startDate, Calendar endDate) {
        
        /*
         * TODO: CHANGE TO USE Joda-Time
         */

        /*
         * Complicated, because java.util.GregorianCalendar and Calendar
         * has no way of calculating this, and we need to account for leap seconds, DST, etc.
         * so simply dividing and comparing the unix time will not work in all cases
         */
        long endInstant = endDate.getTimeInMillis();
        
        int presumedDays = (int) ((endInstant - startDate.getTimeInMillis()) / MILLIS_IN_DAY);
        
        Calendar cursor = (Calendar) startDate.clone();
        
        cursor.add(Calendar.DAY_OF_YEAR, presumedDays);
        
        long instant = cursor.getTimeInMillis();
        
        if (instant == endInstant) {
            return presumedDays;
        }
        
        int step = instant < endInstant ? 1 : -1;
        
        do {
            cursor.add(Calendar.DAY_OF_MONTH, step);
            presumedDays += step;
        } while (cursor.getTimeInMillis() != endInstant);
        
        return presumedDays;
        
    }

    
}
