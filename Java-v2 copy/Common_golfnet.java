/***************************************************************************************
 *   Common_golfnet:   This servlet contains all the common processing for the GolfNet.
 *
 *
 *   Called by:     Proshop_handicaps
 *                  Proshop_reports_handicap
 *                  Member_handicaps
 *                  Common_handicaps
 *
 *
 *   Created:       04/20/2012 - Paul S.
 *
 *
 *   Revisions:
 *
 *           1/16/14  Code updates for posting scores and for handicap lookups for users with no determined handicap yet
 *          12/18/13  Fixed issue with decimal values getting lost when pulling indexes, and with indexes not getting set to negative.
 *           5/16/13  Fix for posting scores
 *           5/15/13  Fix for automated nightly GolfNet downloads
 *           1/24/13  Do not reset Timer_GHIN at end of getPostedScores
 *          12/30/12  Updates for production GolfNet enviroment
 *          11/29/12  Add support for new score_postings table
 *           4/20/12  Initial compile and testing
 *
 *
 *
 *
 *
 * Username: ForeteesService (this will remain the same when going to production)
 * Password:  123456 (this will change when going to production)
 * Source: Foretees (this will remain the same)
 * SourceClubId: 3344 (this will be different for each club that uses the API)
 *
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import javax.xml.namespace.QName;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.DatatypeConstants;

import com.ngn.services._2007._03._20.headerdata.Credentials;

import com.ngn.services._2007._03._20.handicapservice.HandicapService;
import com.ngn.services._2007._03._20.handicapservice.HandicapService_Service;
import com.ngn.services._2007._03._20.handicapservice.FetchHandicapListByNetworkIdRequest;
import com.ngn.services._2007._03._20.handicapservice.FetchHandicapsResponse;
import com.ngn.services._2007._03._20.handicapdata.HandicapList;
import com.ngn.services._2007._03._20.handicapdata.HandicapInfo;

import com.ngn.services._2007._03._20.memberservice.MemberService;
import com.ngn.services._2007._03._20.memberservice.MemberService_Service;
import com.ngn.services._2007._03._20.memberservice.FetchMembersByClubRequest;
import com.ngn.services._2007._03._20.memberservice.FetchMembersResponse;
import com.ngn.services._2007._03._20.memberdata.MemberList;
import com.ngn.services._2007._03._20.memberdata.MemberInfo;
import com.ngn.services._2007._03._20.memberdata.ArrayOfMemberHandicapInfo;
import com.ngn.services._2007._03._20.memberdata.MemberHandicapInfo;

import com.ngn.services._2007._03._20.scoreservice.ScoreService;
import com.ngn.services._2007._03._20.scoreservice.ScoreService_Service;
import com.ngn.services._2007._03._20.scoreservice.FetchScoresByClubRequest;
import com.ngn.services._2007._03._20.scoreservice.FetchScoresByNetworkIdRequest;
import com.ngn.services._2007._03._20.scoreservice.FetchScoresResponse;
import com.ngn.services._2007._03._20.scoreservice.UpdateScoresRequest;
import com.ngn.services._2007._03._20.scoreservice.UpdateScoresResponse;
import com.ngn.services._2007._03._20.scoredata.ScoreList;
import com.ngn.services._2007._03._20.scoredata.ScoreInfo;
import com.ngn.services._2007._03._20.scoredata.ScoreType;
import com.ngn.services._2007._03._20.scoredata.ScoreAction;
import com.ngn.services._2007._03._20.scoredata.PlayType;
import com.ngn.services._2007._03._20.scoredata.ScoreResponseList;
import com.ngn.services._2007._03._20.scoredata.ScoreResponseInfo;

import com.ngn.services._2007._03._20.commondata.ResponseType;


import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
//import com.foretees.common.Connect;
//import com.foretees.common.parmCourse;
//import com.foretees.common.parmClub;
//import com.foretees.common.getParms;
//import com.foretees.common.getClub;


public class Common_golfnet extends HttpServlet {

    final static String rev = ProcessConstants.REV;

    final static String WS_USERNAME = "ForeteesService";
    final static String WS_PASSWORD = "Frt54ok987s";
    final static String WS_SOURCE = "Foretees";

    // TODO: this will need to be pulled from club5



 public static String[] postScore(String user, String club, long date, int tee_id, int score, int escScore, String type, int holes, int teecurr_id, PrintWriter out, Connection con) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String ret[] = new String[2]; // array to return
    String hdcp_num = getHdcpNum(user, con);

    String course = "";
    String tee_name = "";
    String service_num = "";

    int rating = 0;
    int slope = 0;

    int year = (int)date / 10000;
    int month = ((int)date - (year * 10000)) / 100;
    int day = ((int)date - (year * 10000)) - (month * 100);

    DatatypeFactory df = null;

    try {
        df = DatatypeFactory.newInstance();
    } catch (Exception ignore) {}


    XMLGregorianCalendar xmlPlayDate = df.newXMLGregorianCalendarDate(year, month, day, DatatypeConstants.FIELD_UNDEFINED);
    
    if (false) {
        
        ret[0] = "0";
        ret[1] = "year=" + year + ", month=" + month + ", day=" + day + ", xml=" + xmlPlayDate.toString();
        return ret;

    }

    //String play_date = year + "-" + Utilities.ensureDoubleDigit(month) + "-" + Utilities.ensureDoubleDigit(day);


    // get course & tee info
    String holesPlayed = "", tmp = "";
    try {

        if (holes == 1) {
            tmp = "18";
            holesPlayed = "18 Holes";
        } else if (holes == 2) {
            tmp = "F9";
            holesPlayed = "Front 9";
        } else if (holes == 3) {
            tmp = "B9";
            holesPlayed = "Back 9";
        }

        pstmt = con.prepareStatement("" +
                "SELECT c.courseName, t.tee_name, t.tee_rating" + tmp + " AS tee_rating, t.tee_slope" + tmp + " AS tee_slope " +
                "FROM clubparm2 c, tees t " +
                "WHERE c.clubparm_id = t.course_id AND t.tee_id = ?;");
        pstmt.clearParameters();
        pstmt.setInt(1, tee_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            course = rs.getString("courseName");
            tee_name = rs.getString("tee_name");
            rating = (int)rs.getDouble("tee_rating");
            slope = rs.getInt("tee_slope");

        } else {
            /*
            out.println("<!-- " +
                "SELECT c.courseName, t.tee_name, t.tee_rating" + tmp + " AS tee_rating, t.tee_slope" + tmp + " AS tee_slope " +
                "FROM clubparm2 c, tees t " +
                "WHERE c.clubparm_id = t.course_id AND t.tee_id = " + tee_id + "; -->");
            */
            ret[0] = "0";
            ret[1] = "Could not lookup tee information.";
            return ret;

        }

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up tee information.", exc.getMessage(), out, false);
        ret[0] = "0";
        ret[1] = exc.toString();
        return ret;
    }




    // make a unique score id
    String sourceScoreId = "";
    if (teecurr_id != 0) {
        
        sourceScoreId = teecurr_id + "|" + user;
        
    } else {
        
        sourceScoreId = System.currentTimeMillis() + user;
        
    }
    
    ScoreInfo scoreInfo = new ScoreInfo();
    
    scoreInfo.setSourceScoreId(sourceScoreId); // OUR OWN UNIQUE SCORE ID  teecurr_id + username
  //scoreInfo.setSourceUserId(hdcp_num);
    scoreInfo.setNetworkId(Integer.parseInt(hdcp_num));
    scoreInfo.setPlayDate(xmlPlayDate);
    scoreInfo.setCourseName( (( course.equals("") ) ? Utilities.getClubName(con, true) : course) );
    scoreInfo.setTeeBoxName(tee_name);
    scoreInfo.setSlope(slope);
    scoreInfo.setRating(rating * 10);
    scoreInfo.setGross(score);
    scoreInfo.setESC(escScore);

    scoreInfo.setHolesPlayed(holesPlayed);

    // set the type of play & score
    if (type.equals("H")) {
        scoreInfo.setScoreType(ScoreType.HOME);
        scoreInfo.setPlayType(PlayType.LEISURE);
    } else if (type.equals("T")) {
        scoreInfo.setScoreType(ScoreType.TOURNAMENT);
        scoreInfo.setPlayType(PlayType.TOURNAMENT);
    } else {
        // should ever fall in here - but in case we add more and forget to remap let default to home
        scoreInfo.setScoreType(ScoreType.HOME);
        scoreInfo.setPlayType(PlayType.LEISURE);
    }

    scoreInfo.setIsLocal(false); // always false - only true if posted from kiosk at club
    scoreInfo.setIsHoleByHole(false);
    scoreInfo.setAction(ScoreAction.ADD);


    ScoreList scoreList = new ScoreList();
    scoreList.getScore().add(scoreInfo);

    try {

        URL url = new URL("http://services.ngn.com/ScoreService.asmx");

        ScoreService_Service service = null;

        service = new ScoreService_Service(url, new QName("http://services.ngn.com/2007/03/20/ScoreService", "ScoreService"));

        ScoreService ss = service.getScoreService();

        UpdateScoresResponse response = ss.updateScores( getUpdateScoresRequest(club, getClubSourceId(club, con), scoreList) );

        ScoreResponseList sl = response.getScoreResponseList();

        List<ScoreResponseInfo> scores = sl.getScoreResponse();


        ScoreResponseInfo score_response = scores.get(0);

        if (score_response.getResponseType().equals(ResponseType.SUCCESS) ) {

            // success
            ret[0] = "1";   // indicate success
            ret[1] = "OK";  // (not used)

        } else if (score_response.getResponseType().equals(ResponseType.WARNING) ) {

            // warning
            ret[0] = "2";   // indicate warning
            ret[1] = "Warning: " + score_response.getResponseMessage();

        } else {

            // failure
            ret[0] = "0";   // indicate a failure
            ret[1] = "Error: " + score_response.getResponseMessage();

        }

    } catch (Exception exc) {

        ret[0] = "0";   // indicate a failure
        ret[1] = "Error: " + exc.toString();
    }

    Utilities.logError("GolfNet: club=" + club + ", user=" + user + ", hdcp_num=" + hdcp_num + ", ret[0]="+ret[0]+", ret[1]="+ret[1]);

    return ret;

 }


 private static UpdateScoresRequest getUpdateScoresRequest(String club, String sourceClubId, ScoreList scoreList) {


    UpdateScoresRequest request = new UpdateScoresRequest();
/*
    "transactionId",
    "credentials",
    "scoreList",
    "postScoreProcess",
    "checkForDuplicateScores"
*/

    request.setTransactionId("");
    request.setCredentials( getCredentials(club) );
    request.setScoreList(scoreList);
    request.setPostScoreProcess(true);
    request.setCheckForDuplicateScores(true);

    return request;

 }


 public static String[] getCurrentHdcp(String user, String club, Connection con) {


    String ret[] = new String[6];

    /*
    ret[0] = "1";          // success or fail.  1 = ok, anything eles fail, if fail look at [5] to determin if golfer is active or not
    ret[1] = "";           // error message or "OK"
    ret[2] = "14.7";       // hdcpIndex;
    ret[3] = "0";          // hdcpDiff;
    ret[4] = "20070201";   // hdcpDate;
    ret[5] = "1";          // active status
    */

    String hdcpNum = getHdcpNum(user, con);
    String date = "";
    Short hdcpIndex = 0;

    try {

        URL url = new URL("http://services.ngn.com/HandicapService.asmx");

        HandicapService_Service service = null;

        service = new HandicapService_Service(url, new QName("http://services.ngn.com/2007/03/20/HandicapService", "HandicapService"));

        HandicapService hs = service.getHandicapService();

        FetchHandicapsResponse response = hs.fetchHandicapListByNetworkId(getFetchHandicapListByNetworkIdRequest(club, hdcpNum));

        HandicapList hl = response.getHandicapList();

        List<HandicapInfo> handicap = hl.getHandicap();

        HandicapInfo hi = null;

        if (handicap.size() > 0) {
            
            hi = handicap.get(0);
            hdcpIndex = hi.getHandicapValue();
            date = hi.getEffectiveOn().toString();

        } else {

            // we did not receive a handicap in the response
            hdcpIndex = -99;

        }

        //Utilities.logError("GolfNet: handicap.size=" + handicap.size() + ", getEffectiveOn=" + hi.getEffectiveOn().toString());

        // plug the other values in to the ret array
        ret[0] = "1";
        ret[1] = "OK";
        ret[2] = Short.toString(hdcpIndex);
        ret[3] = "";
        ret[4] = date;
        ret[5] = "";


    } catch (Exception exc) {

        ret[0] = "0"; // fail
        ret[1] = exc.getMessage();

    }

    return ret;

 }


 private static FetchHandicapListByNetworkIdRequest getFetchHandicapListByNetworkIdRequest(String club, String hdcpNum) {

    /*
    @XmlType(name = "FetchHandicapListByNetworkIdRequest", propOrder = {
        "transactionId",
        "credentials",
        "networkId"
    })
    */

    FetchHandicapListByNetworkIdRequest request = new FetchHandicapListByNetworkIdRequest();

    request.setTransactionId("");
    request.setCredentials( getCredentials(club) );
    request.setNetworkId(Integer.parseInt(hdcpNum));

    return request;

 }


 private static Credentials getCredentials(String club) {

    // club is here only if we need to override these values

    Credentials auth = new Credentials();
    auth.setUsername(WS_USERNAME);
    auth.setPassword(WS_PASSWORD);
    auth.setSource(WS_SOURCE);

    return auth;

 }


 public static String[] getRecentPostings(String user, String club, PrintWriter out, Connection con) {

    String ret[] = new String[2]; // array to return

    String hdcp_num = getHdcpNum(user, con);

    try {

        URL url = new URL("http://services.ngn.com/ScoreService.asmx");

        ScoreService_Service service = null;

        service = new ScoreService_Service(url, new QName("http://services.ngn.com/2007/03/20/ScoreService", "ScoreService"));

        ScoreService ss = service.getScoreService();

        FetchScoresResponse response = ss.fetchScoresByNetworkId( getScoresByNetworkIdRequest(club, hdcp_num, getClubSourceId(club, con)) );

        ScoreList sl = response.getScoreList();

        List<ScoreInfo> scores = sl.getScore();

        ScoreInfo si = null;

        //Utilities.logError("GolfNet: scores.size=" + scores.size());

        // resize
        ret = new String[scores.size() + 2];

        for (int i = 0; i < scores.size(); i++) {

            si = scores.get(i);
            ret[i + 2] = si.getGross() + "|" + si.getScoreType() + "|" + si.getPlayDate();
        }

        ret[0] = "1";   // indicate success
        ret[1] = "OK";  // would be error msg

    } catch (Exception exc) {

        ret[0] = "0";   // indicate a failure
        ret[1] = "Error: " + exc.toString();

        Utilities.logError("Common_golfnet.getRecentPostings: club=" + club + ", user=" + user + ", error=" + exc.toString());

    }

    return ret;
 }


 private static FetchScoresByNetworkIdRequest getScoresByNetworkIdRequest(String club, String hdcpNum, String sourceClubId) {

    final int days_to_go_back = -45;

    FetchScoresByNetworkIdRequest request = new FetchScoresByNetworkIdRequest();
/*
    @XmlType(name = "FetchScoresByNetworkIdRequest", propOrder = {
        "transactionId",
        "credentials",
        "networkId",
        "fromDate",
        "toDate"
    })
*/

    DatatypeFactory df = null;

    try {
        df = DatatypeFactory.newInstance();
    } catch (Exception ignore) {}


    GregorianCalendar cal = new GregorianCalendar();         // get todays date

    XMLGregorianCalendar xmlToDate = df.newXMLGregorianCalendar(cal);

    cal.add(Calendar.DATE, days_to_go_back);        // apply the offset

    XMLGregorianCalendar xmlFromDate = df.newXMLGregorianCalendar(cal);

    request.setTransactionId("");
    request.setCredentials( getCredentials(club) );
    request.setNetworkId(Integer.parseInt(hdcpNum));
    request.setFromDate(xmlFromDate);
    request.setToDate(xmlToDate);

    //Utilities.logError("FetchScoresByNetworkId(" + club + "): hdcpNum=" + hdcpNum + ", xmlFromDate=" + xmlFromDate.toString() + ", xmlToDate=" + xmlToDate.toString());

    return request;

 }


 public static void getPostedScores(PrintWriter out) {

    Connection con = null;
    Connection con2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    Statement stmt2 = null;
    ResultSet rs2 = null;

    String club = "";

    // only run on server #1 OR if called with a valid out object.  TimerGHIN will call this method with a null out obj.  Support_sync will call with valid out
    if (Common_Server.SERVER_ID == SystemUtils.TIMER_SERVER || out != null) {

        long startTime = System.currentTimeMillis();
        Utilities.logError("Starting Common_golfnet.getPostedScores()");

        try {
            con = dbConn.Connect(rev);
        } catch (Exception e1) {
            Utilities.logError("Common_golfnet.getPostedScores: Error connecting to v5 db. " + e1.getMessage());                                       // log it
        }

        if (con != null) {

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

                // loop all active clubs
                while (rs.next()) {

                    club = rs.getString(1);

                    if (!club.startsWith("demo")) {

                        con2 = dbConn.Connect(club);

                        if (con2 != null) {

                            // only process if club is using golfnet
                            if (Common_handicaps.getClubHdcpOption(club, con2).equalsIgnoreCase("GN21")) {

                                stmt2 = con2.createStatement();
                                rs2 = stmt2.executeQuery("" +
                                        "SELECT gn21_sourceClubId, " +
                                            "DATE_FORMAT(DATE_ADD(now(), INTERVAL -30 DAY), '%Y%m%d') AS startDate, " +
                                            "DATE_FORMAT(NOW(), '%Y%m%d') AS endDate " +
                                        "FROM club5 " +
                                        "WHERE gn21_sourceClubId <> '';");

                                if (rs2.next()) {

                                    // call routine that actually performs the data retrieval for a given club
                                    getPostedScoresForClub(club, rs2.getInt("startDate"), rs2.getInt("endDate"), con2, out);

                                    // update the hdcp values in member2b for this club and update teecurr2 & event signups
                                    getHandicapsForClub(club, con2, out);

                                    updateMemberHdcps(club, con2);

                                }

                            } // end if club using gn21

                            try { stmt2.close(); }
                            catch (Exception ignore) { }

                            try { con2.close(); }
                            catch (Exception ignore) { }

                        } else {

                            Utilities.logError("Common_golfnet.getPostedScores: Can not connect to " + club + " db.");
                        }

                    } // end if not demo sites

                } // end loop of all clubs in v5.clubs

                try { stmt.close(); }
                catch (Exception ignore) { }

            }
            catch (Exception e2) {

                Utilities.logError("Common_golfnet.getPostedScores: Error processing club=" + club + ", Exception was " + e2.getMessage());
            }

            try {
                con.close();
            }
            catch (Exception ignored) { }

        } // end if con != null

        Utilities.logError("Finished Common_golfnet.getPostedScores() run time:" + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min)");

    } // end if master server

    //TimerGHIN g_timer = new TimerGHIN();            // the common ghin servlet will reset the timer when it completes

 }


 public static void getHandicapsForClub(String club, Connection con, PrintWriter out) {


    PreparedStatement pstmt = null;

    double hdcp = 0;
    int friendlyId = 0;
    int count = 0;

    try {

        URL url = new URL("http://services.ngn.com/MemberService.asmx");

        MemberService_Service service = null;

        service = new MemberService_Service(url, new QName("http://services.ngn.com/2007/03/20/MemberService", "MemberService"));

        MemberService ms = service.getMemberService();

        FetchMembersResponse response = ms.fetchMembersByClub( getFetchMembersByClubRequest(club, getClubSourceId(club, con)) );

        MemberList ml = response.getMemberList();

        List<MemberInfo> members = ml.getMember();

        MemberInfo mi = null;

        if (out != null) out.println("<br><b>Found " + members.size() + " members.</b>");

        for (int i = 0; i < members.size(); i++) {

            // get member info
            mi = members.get(i);

            friendlyId = mi.getFriendlyId(); // seems to be the networkId

            // get members hdcp array
            ArrayOfMemberHandicapInfo amhi = mi.getHandicapInfo();

            List<MemberHandicapInfo> mhi = amhi.getMemberHandicapInfo();

            // get hdcp info from hdcp array
            MemberHandicapInfo hdcpInfo = mhi.get(0);

            hdcp = hdcpInfo.getValue();

            if (out != null) out.println("<br>" + friendlyId + ", " + hdcp + ", " + hdcpInfo.getDisplayHandicap());

            if (hdcp == 999) {
                hdcp = -990;
            } else {
                hdcp *= -1;
            }

            try {

                pstmt = con.prepareStatement(
                    "UPDATE member2b SET g_hancap = ? WHERE ghin = ?");

                pstmt.clearParameters();
                pstmt.setDouble(1, Double.valueOf(hdcp / 10));
                pstmt.setString(2, String.valueOf(friendlyId));
                
                count += pstmt.executeUpdate();

            } catch (SQLException exc) {

                Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ") Update Error. i=" + i + ", golfnet#=" + friendlyId + ".  Error=" + exc.toString());

            } finally {

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

        } // end loop of all members return in query

        if (out != null) out.println("<p><b>Updated " + count + " members.</b></p>");

    } catch (Exception exc) {

        Utilities.logError("Common_golfnet.getHandicapsForClub() club=" + club + ", err=" + exc.getMessage());

    }

 }


 private static FetchMembersByClubRequest getFetchMembersByClubRequest(String club, String sourceClubId) {


    final int days_to_go_back = -365;

    FetchMembersByClubRequest request = new FetchMembersByClubRequest();
/*
    @XmlType(name = "FetchMembersByClubRequest", propOrder = {
        "transactionId",
        "credentials",
        "sourceClubId",
        "fromDate",
        "toDate",
        "activeOnly"
    })
*/

    DatatypeFactory df = null;

    try {
        df = DatatypeFactory.newInstance();
    } catch (Exception igenore) {}


    GregorianCalendar cal = new GregorianCalendar();    // get todays date

    XMLGregorianCalendar xmlToDate = df.newXMLGregorianCalendar(cal);

    cal.add(Calendar.DATE, days_to_go_back);            // apply the offset

    XMLGregorianCalendar xmlFromDate = df.newXMLGregorianCalendar(cal);

    request.setTransactionId("");
    request.setCredentials( getCredentials(club) );
    request.setSourceClubId(sourceClubId);
    request.setFromDate(xmlFromDate);
    request.setToDate(xmlToDate);
    request.setActiveOnly(true);

    return request;

 }


 public static void getPostedScoresForClub(String club, int startDate, int endDate, Connection con, PrintWriter out) {


    try {

        URL url = new URL("http://services.ngn.com/ScoreService.asmx");

        ScoreService_Service service = null;

        service = new ScoreService_Service(url, new QName("http://services.ngn.com/2007/03/20/ScoreService", "ScoreService"));

        ScoreService ss = service.getScoreService();

        FetchScoresResponse response = ss.fetchScoresByClub( getScoresByClubRequest(club, getClubSourceId(club, con), startDate, endDate) );

        ScoreList sl = response.getScoreList();

        List<ScoreInfo> scores = sl.getScore();

        Utilities.logError("Common_golfnet.getPostedScoresForClub(" + club + ") found " + scores.size() + " scores between " + startDate + " and " + endDate);
        if (out != null) out.println("<br>Found " + scores.size() + " scores between " + startDate + " and " + endDate + "<br>");

        ScoreInfo si = null;
        ScoreType st = null;

        String type = "";
        String hdcpNum = "";
        String date = "";

        int score = 0;
        int slope = 0;
        int rating = 0;
        int tee_id = 0;
        long uuid = 0;

        PreparedStatement pstmt = null;

        XMLGregorianCalendar playDate = null;

        int inserts = 0, updates = 0, skipped = 0, tmp = 0, unknown_types = 0;

        for (int i = 0; i < scores.size(); i++) {

            si = scores.get(i);

            hdcpNum = String.valueOf(si.getNetworkId()); // si.getSourceUserId();
            playDate = si.getPlayDate();
            score = si.getGross();
            slope = si.getSlope();
            rating = si.getRating();
            uuid = si.getScoreId(); // Is this a uuid?

            st = si.getScoreType();
            type = st.value();

            // convert to our type
            if (type.equalsIgnoreCase("Home")) {
                type = "H";
            } else if (type.startsWith("Internet")) {
                type = "I";
            } else if (type.startsWith("Tournament")) {
                type = "T";
            } else if (type.equalsIgnoreCase("Away")) {
                type = "A";
            } else if (type.equalsIgnoreCase("Away Internet")) {
                type = "AI";
            } else {
                //type = "";
                unknown_types++;
            }

            date = playDate.getYear() + "-" + String.format("%02d", playDate.getMonth()) + "-" + String.format("%02d", playDate.getDay()); // yyyymmdd (assumes XMLGregorianCalendar gives us leading zeros for month & day)

            // try to determin this tee from the slope/rating posted for this score
            tee_id = 0;
            if (rating != 0 && slope != 0) {

                tee_id = getTeeIdFromSlopeRating(slope, rating, con);
                if (out != null && tee_id == 0) out.println("<br>Can't find tee for slope=" + slope + " and rating=" + rating + " '" + type + "'");
            }

            
            try {

                pstmt = con.prepareStatement(
                                "INSERT INTO score_postings " +
                                "(hdcpNum, provider_uuid, date, score, type, tee_id, last_touched) " +
                                "VALUES (CAST(? AS UNSIGNED), ?, ?, ?, ?, ?, NOW()) " +
                                "ON DUPLICATE KEY UPDATE date = VALUES(date), score = VALUES(score), type = VALUES(type), tee_id = VALUES(tee_id), last_touched = NOW()");

                pstmt.clearParameters();
                pstmt.setString(1, hdcpNum);    // hdcpNum
                pstmt.setLong(2, uuid);         // part of uuid
                pstmt.setString(3, date);       // date insert
                pstmt.setInt(4, score);         // score insert
                pstmt.setString(5, type);       // type insert
                pstmt.setInt(6, tee_id);        // teeid insert
                
                tmp = pstmt.executeUpdate();
                if (tmp == 0) {
                    skipped++;
                } else if (tmp == 1) {
                    inserts++;
                } else if (tmp == 2) {
                    updates++;
                }

            } catch (SQLException exc) {

                Utilities.logError("Common_golfnet:getPostedScoresForClub 2 (" + club + ") INSERT Error. i=" + i + ".  Error=" + exc.toString() + ", Score=" + score + ", Type=" + type);

            } finally {

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

        }

        Utilities.logError("Common_golfnet.getPostedScoresForClub (" + club + ") Performed " + inserts + " inserts and " + updates + " updates and skipped " + skipped + " records. " + ((unknown_types>0) ? "*** " + unknown_types + " unknown types ***" : ""));

    } catch (Exception exc) {
        /*
        if (out != null) {
            out.println("<p>Common_golfnet:getPostedScoresForClub1(" + club + ") step=" + step + ", Error=" + exc.toString() + ", toString=" + exc.toString() + ", response=" + response + "</p>");
            out.println(Utilities.getStackTraceAsString(exc));
        }*/
        Utilities.logError("Common_golfnet:getPostedScoresForClub 1 (" + club + ") Error=" + exc.toString());

    }

 }

 
 private static FetchScoresByClubRequest getScoresByClubRequest(String club, String sourceClubId, int sdate, int edate) {


    FetchScoresByClubRequest request = new FetchScoresByClubRequest();
/*
    @XmlType(name = "FetchScoresByClubRequest", propOrder = {
        "transactionId",
        "credentials",
        "sourceClubId",
        "fromDate",
        "toDate"
    })
*/

    int s_yy = sdate / 10000;
    int s_mm = (sdate - (s_yy * 10000)) / 100;
    int s_dd = sdate - ((s_yy * 10000) + (s_mm * 100));

    int e_yy = edate / 10000;
    int e_mm = (edate - (e_yy * 10000)) / 100;
    int e_dd = edate - ((e_yy * 10000) + (e_mm * 100));

    DatatypeFactory df = null;

    try {
        df = DatatypeFactory.newInstance();
    } catch (Exception ignore) {}

    GregorianCalendar cal = new GregorianCalendar();

    cal.set(s_yy, s_mm - 1, s_dd);
    
    XMLGregorianCalendar xmlFromDate = df.newXMLGregorianCalendar(cal);

    cal.set(e_yy, e_mm - 1, e_dd);

    XMLGregorianCalendar xmlToDate = df.newXMLGregorianCalendar(cal);

    request.setTransactionId("");
    request.setCredentials( getCredentials(club) );
    request.setSourceClubId(sourceClubId);
    request.setFromDate(xmlFromDate);
    request.setToDate(xmlToDate);

    //Utilities.logError("FetchScoresByClubRequest(" + club + "): sourceClubId=" + sourceClubId + ", s_yy=" + s_yy + ", s_mm=" + s_mm + ", s_dd=" + s_dd + ", e_yy=" + e_yy + ", e_mm=" + e_mm + ", e_dd=" + e_dd + ", sdate=" + sdate + ", edate=" + edate + ", xmlFromDate=" + xmlFromDate.toString() + ", xmlToDate=" + xmlToDate.toString());

    return request;

 }


 public static void updateMemberHdcps(String club, Connection con) {


    Statement stmt = null;

    // UPDATE TEECURR2 TABLE WITH MOST RECENT HANDICAP DATA
    try {

        stmt = con.createStatement();
        for (int x=1;x<=5;x++) {

            stmt.executeUpdate("" +
                "UPDATE teecurr2 t, member2b m " +
                "SET " +
                    "t.hndcp" + x + " = (IF(t.username" + x + " = m.username,m.g_hancap,t.hndcp" + x + ")) " +
                "WHERE " +
                    "t.username" + x + " = m.username AND " +
                    "m.g_hancap <> -99 AND m.g_hancap <> 99");
        }

    } catch (Exception exp) {

        Utilities.logError("Common_golfnet: Updating teecurr2 - Club: " + club + "  Error: " + exp.toString());

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }


    // UPDATE EVNTSUP2B TABLE WITH MOST RECENT HANDICAP DATA
    try {

        stmt = con.createStatement();
        for (int x=1;x<=5;x++) {

            stmt.executeUpdate("" +
                "UPDATE evntsup2b e, member2b m " +
                "SET " +
                    "e.hndcp" + x + " = (IF(e.username" + x + " = m.username,m.g_hancap,e.hndcp" + x + ")) " +
                "WHERE " +
                    "e.username" + x + " = m.username AND " +
                    "m.g_hancap <> -99 AND m.g_hancap <> 99");
        }

    } catch (Exception exp) {

        Utilities.logError("Common_golfnet: Updating evntsup2b - Club: " + club + "  Error: " + exp.toString());

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 } // end updateMemberHdcps



 private static String getClubSourceId(String club, Connection con) {

    // lookup the gn21 source id for this club
    String result = "";

    Statement stmt = null;
    ResultSet rs = null;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT gn21_sourceClubId FROM club5");

        if (rs.next()) result = rs.getString(1);

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    return result;
    
 } // end getClubSourceId


 public static double parseHandicapValue(String hdcp) {

    double dbl_hdcp;

    hdcp = hdcp.trim();

    if (hdcp.equals("") || hdcp.equals("999")) {

        // if it's an empty string skip the rest and just return a -99
        dbl_hdcp = -99;

    } else if (hdcp.equalsIgnoreCase("NH") || hdcp.equalsIgnoreCase("NHL") ||
               hdcp.equalsIgnoreCase("NI") || hdcp.equalsIgnoreCase("N/I")) {

        // we may get a NH handicap, so if we can't convert to double then use -99
        dbl_hdcp = -99;

    } else {

        try {

            hdcp = hdcp.replace("L", "");
            hdcp = hdcp.replace("R", "");
            hdcp = hdcp.replace("H", "");
            hdcp = hdcp.replace("N", "");
            hdcp = hdcp.replace("J", "");
            hdcp = hdcp.replace("M", "");

            if (!hdcp.startsWith("+") && !hdcp.startsWith("-")) hdcp = "-" + hdcp; // incoming indexs are assumed to be negative unless specified as positive
            dbl_hdcp = Double.parseDouble(hdcp);

        } catch (NumberFormatException exc) {

            dbl_hdcp = -99;
            Utilities.logError("Common_golfnet:parseHandicapValue() err=" + exc.toString() + ", hdcp=" + hdcp);
        }

    }

    return dbl_hdcp;

 } // end if parseHandicapValue


 protected static String getHdcpNum(String user, Connection con) {

    // lookup golfer number for this user
    String hdcp_num = "";

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    try {

        pstmt = con.prepareStatement("SELECT ghin FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        rs = pstmt.executeQuery();
        if (rs.next()) hdcp_num = scrubGolfNetNum(rs.getString(1));

        // this was required for ghin, not sure about gn21
        //if (hdcp_num.length() == 5) { hdcp_num = "00" + hdcp_num;
        //} else if (hdcp_num.length() == 6) hdcp_num = "0" + hdcp_num;

    } catch (Exception ignore) {

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return hdcp_num;

 } // end getHdcpNum



 private static String scrubGolfNetNum(String hdcpNum) {

     return hdcpNum.replace("-", "");
 }


 /* if this method remains the same as the ghin one then we could make it common by moving it to common handicaps */
 protected static int getTeeIdFromSlopeRating(int slope, double rating, Connection con) {


    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int tee_id = 0;

    try {

        pstmt = con.prepareStatement (
                "SELECT tee_id " +
                "FROM tees " +
                "WHERE " +
                    "(tee_rating18 = ? AND tee_slope18 = ?)" +
                    " OR " +
                    "(tee_ratingF9 = ? AND tee_slopeF9 = ?)" +
                    " OR " +
                    "(tee_ratingB9 = ? AND tee_slopeB9 = ?)");

        pstmt.clearParameters();
        pstmt.setDouble(1, rating);
        pstmt.setInt(2, slope);
        pstmt.setDouble(3, rating);
        pstmt.setInt(4, slope);
        pstmt.setDouble(5, rating);
        pstmt.setInt(6, slope);

        rs = pstmt.executeQuery();

        if ( rs.next() ) tee_id = rs.getInt(1);

    } catch (Exception exp) {

        Utilities.logError("Common_golfnet:getTeeIdFromSlopeRating - Error: " + exp.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return tee_id;

 }

}  // end class







/*
 private static Document getDocument(DocumentBuilder builder, String urlString) {

    try {

        URL url = new URL( urlString );

        try {

            URLConnection URLconnection = url.openConnection();
            URLconnection.setDefaultUseCaches(false);
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            int responseCode = httpConnection.getResponseCode();

            if ( responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpConnection.getInputStream();

/*
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = br.readLine()) != null) {
                  sb.append(line);
                }
                br.close();
*//*
                try {
                    Document doc = builder.parse(in); // new InputSource(new StringReader(sb.toString()))
                    in.close();
                    return doc;
                } catch(org.xml.sax.SAXException e) {

                    Utilities.logError("Common_golfnet:getDocument() err=" + e.toString());
                    e.printStackTrace() ;
                }

                in.close();

            } else {

                System.out.println( "HTTP connection response != HTTP_OK" );
            }
        } catch ( IOException e ) {

            Utilities.logError("Common_golfnet:getDocument() IOException: urlString=" + urlString + ", err=" + e.toString());
            e.printStackTrace();

        } catch ( Exception e ) {

            Utilities.logError("Common_golfnet:getDocument() Exception1: urlString=" + urlString + ", err=" + e.toString());
        }

    } catch ( MalformedURLException e ) {

        Utilities.logError("Common_golfnet:getDocument() MalformedURLException: urlString=" + urlString + ", err=" + e.toString());
        e.printStackTrace ( ) ;

    } catch ( Exception e ) {

        Utilities.logError("Common_golfnet:getDocument() Exception2: urlString=" + urlString + ", err=" + e.toString());
    }

    return null;
}

 public static boolean sendChargeIBS(Connection con) {


    String returnCode = "";
    String club = "";

    boolean failed = false;

    Statement stmt = null;
    ResultSet rs = null;

    String ws_url = "";


    String err = "";

    try {

        URL url = new URL(ws_url);

        if (url != null) {

            IBSWebMemberAPI service = null;

            javax.xml.ws.Holder<String> holderMessage = new javax.xml.ws.Holder<String>();
            javax.xml.ws.Holder<Boolean> holderResult = new javax.xml.ws.Holder<Boolean>();

            service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

            // their port is the getIBSWebMemberAPISoap object
            IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

            int i = 0;
            ArrayList<ArrayList<String>> batch = new ArrayList<ArrayList<String>>();

            try {

                batch = buildXmlFileForIBS(parmp, deptID, tenderID, taxID, con);

                for (i = 0; i < batch.size(); i++) {

                    // trace the charges being sent
                    tracePOS(1, "", parmp, con);       // type = send, no return code

                    //Utilities.logError("sending batch " + (i + 1) + "/" + batch.size() + ": " + batch.get(i).get(1));

                    port.createTickets(ws_user, ws_pass, batch.get(i).get(1), holderMessage, holderResult);

                    if (holderResult.value == false) {

                       //  Check for meaningless error from IBS - a bug they haven't fixed - skip it, but trace it below

                       if (!holderMessage.value.startsWith( tempErr )) {     // if NOT the meaningless error - track it (TEMP check until IBS fixes this bug)

                           // failed - identify the member and the related message
                           if (holderMessage.value != null) parmp.returnCode1 += "<p>Charge for " + batch.get(i).get(0) + " failed. Reason: " + holderMessage.value + "</p>";
                           //parmp.returnCode1 += "<p>Charge for " + batch.get(i).get(0) + " failed. Reason: Testing</p>";

                           //
                           //  Save error code in pos_hist for reports
                           //
                           parmp.hist_posid = parmp.posid;

                           if (!parmp.player.equals( "" )) {
                              parmp.hist_player = parmp.player;          // if guest
                           } else {
                              parmp.hist_player = parmp.fname + " " + parmp.lname;   // else use member name
                           }

                           parmp.hist_price = "";
                           parmp.hist_item_num = "POS Rejected Charge";
                           returnCode = truncate(returnCode, 45);            // make sure it fits
                           parmp.hist_item_name = returnCode;

                           add_POS_hist(parmp, con);               // go make the entry
                       }
                    }

                    // trace the charges being sent
                    tracePOS(2, holderMessage.value, parmp, con);       // type = response, no return code

                }     // end of FOR loop - send all charges

               // clearWaiting(parmp, con);              // indicate we are no longer waiting for a reply (NOTE: may have to do this diefferently as several charges sent)!!!

            } catch (Exception exc) {

                 Utilities.logError("sendChargeIBS(): " + err + " Likely communication error for club=" + parmp.club + ", loop " + i + " of " + batch.size() + ", err=" +exc.getMessage() + ", err=" +exc.toString());

                 returnCode = "ERROR COMMUNICATING WITH THE IBS WEB SERVICE AT YOUR CLUB. PLEASE CONTACT IBS OR FORETEES FOR ASSISTANCE OR TRY AGAIN LATER.";

            } finally {

                 parmp.item = "";         // init name field (DO WE NEED THIS FOR IBS?)

            }

        } // end if URL != null

    } catch (Exception exc) {

        Utilities.logError(" club=" + club + ", Error = " + exc.getMessage());
        failed = true;

    }

    return(failed);

 }                   // end of sendChargeIBS
*/
