/***************************************************************************************
 *   Common_ghin:   This servlet contains all the common process for the ghin scorelink
 *
 *
 *   Called by:     Proshop_handicaps
 *                  Proshop_reports_handicap
 *                  Member_handicaps
 *                  Common_handicaps
 *
 *
 *   Created:       09/21/2006 - Paul S.
 *
 *
 *   Revisions:
 *
 *                  9/24/13 Now using the "Display" element of the XML response from GHIN for the handicap index value, instead of the "Value" element, in order to properly determine correct signage.
 *                 11/29/12 Add support for new score_postings table
 *                  8/22/12 Fixed problem with members looking up other members handicaps & recent postings
 *                  8/03/12 Add the getHandicaps and getHandicapsForClub methods
 *                  7/28/12 Re-implement the getPostedScoresForClub method
 *                  7/12/12 Implement new GHIN interface
 *                  5/14/12 Ensure all xml documents we pull are encoded with utf-8 before parsing.
 *                  4/15/11 Minor change to parseHandicapValue so that we don't spam the log with empty handicap errors
 *                  9/21/10 MN Valley CC (mnvalleycc) - updated updateMemberHdcps custom (case 1354).
 *                  5/04/10 Udpated updateMemberHdcps to try and better match on hdcp numbers with leading zeros
 *                  5/13/09 Changed insert statement in getPostedScoresForClub to update hdcpIndex on duplicate key
 *                  3/09/09 Changed getPostedScores to run for server #1 OR if valid out exists
 *                          This allows us to call this from Support_sync to run download for ALL clubs
 *                 12/02/08 Added service value to getPostedScoresForClub (can be specified when called manually from Support_sync)
 *                 11/17/08 Exclude demo sites from getPostedScores method
 *                  9/05/08 updateMemberHdcps don't update hndcp #6-10 in updEvents - Fields no longer in db
 *                  6/11/08 postScore - if not multi course - send club name instead of course name
 *                  6/03/08 Reset the GHIN Timer (TimerGHIN - new) after updating the ghin records for each club.
 *                  5/13/08 Changed close call for InputStream in getDocument
 *                  5/02/08 Added import of java.util.* and getScoreTypes() method
 *                  2/15/08 Changed getHdcpNum to add a leading 0 to hdcpNum if it's 6 chars in length
 *                  1/03/08 Removed hdcp season check for downloading posted scores
 *                 12/31/07 MN Valley CC - updateMemberHdcps - Convert indexes to course hndps
 *                 12/17/07 Pelican's Nest - updateMemberHdcps - Convert indexes to course hndps
 *                 10/19/07 Change posted scores download to retrieve all posted scores not just Home scores
 *                 10/04/07 Added additional debug information
 *                  8/21/07 Cherry Hills - updateMemberHdcps - Convert indexes to course hndps
 *                  7/26/07 Incoming indexs from GHIN ES are assumed to be negative unless specified as positive
 *                  6/04/07 Added additional hdcp value validation and error trapping
 *
 *
 *
 ***************************************************************************************
 */


import java.io.*;
import java.io.InputStreamReader;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
//import org.w3c.dom.*;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

//import org.apache.commons.lang.*;

import com.foretees.common.Utilities;


/*  NEW URLS
 *
 * Nigthly D/L
 * http://ghp.ghin.com/ghponline/dataservices/scoremethods.asmx/ClubMemberScores?username=ws98&password=ws98&Association=98&Club=30&Service=1&activeOnly=true&datePlayedBegin=1/1/1900&datePlayedEnd=1/1/2013&datePostedBegin=1/1/1900&datePostedEnd=1/1/2013&teeAssociationNumber=0&teeClubNumber=0&teeCourseNumber=0&teeTeeNumber=0&crpTeeID=0&courseName=%&scoreType=All&scoreLow=0&scoreHigh=0
 *
 *
 *
 *
 *
 *
 */





public class Common_ghin extends HttpServlet {

    static final String rev = SystemUtils.REVLEVEL;

    static final String VASP = "t7pv5";  // GHIN ES VASP CODE

    static final String ghp_user = "ForeTees";
    static final String ghp_pass = "T7PV5";


 private static Document getDocument(DocumentBuilder builder, String urlString) {

     // System.getProperties().setProperty("sun.net.http.retryPost", "false" );
     // httpConnection.getConnectTimeout();
    try {


        URL url = new URL( urlString );

        try {

            URLConnection URLconnection = url.openConnection();
            URLconnection.setDefaultUseCaches(false); // If true, the protocol is allowed to use caching whenever it can. If false, the protocol must always try to get a fresh copy of the object.
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            int responseCode = httpConnection.getResponseCode();

            if ( responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpConnection.getInputStream();

                // the xml documents we receive are inconstantly encoded
                // they appear to be utf8 but obviously are not
                // so before we attempt to parse the document lets ensure
                // it is encoded in utf8 first
                InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = br.readLine()) != null) {
                  sb.append(line);
                }
                br.close();

                // note: this is probably redundant - instead I'll just push the sb through a StringReader/InputSource below
                //ByteArrayInputStream input = new ByteArrayInputStream(sb.toString().getBytes("UTF-8"));

                try {

                    Document doc = builder.parse(new InputSource(new StringReader(sb.toString()))); // input
                    in.close();
                    return doc;

                } catch(org.xml.sax.SAXException e) {

                    Utilities.logError("Common_ghin:getDocument() err=" + e.toString());
                    e.printStackTrace() ;
                }

                in.close();

            } else {

                System.out.println( "HTTP connection response != HTTP_OK.  It was " + responseCode + ", message=" + httpConnection.getResponseMessage() );
            }

        } catch ( IOException e ) {

            Utilities.logError("Common_ghin:getDocument() IOException: urlString=" + urlString + ", err=" + e.toString());
            e.printStackTrace();

        } catch ( Exception e ) {

            Utilities.logError("Common_ghin:getDocument() Exception1: urlString=" + urlString + ", err=" + e.toString());
        }

    } catch ( MalformedURLException e ) {

        Utilities.logError("Common_ghin:getDocument() MalformedURLException: urlString=" + urlString + ", err=" + e.toString());
        e.printStackTrace ( ) ;

    } catch ( Exception e ) {

        Utilities.logError("Common_ghin:getDocument() Exception2: urlString=" + urlString + ", err=" + e.toString());
    }

    return null;
}


 public static String[] getRecentPostings(String user, String club, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {

    String ret[] = new String[22]; // array to return
    String hdcp_num = getHdcpNum(user, con);

    // values we find and return
    String score = "";
    String type = "";
    String date = "";

    // ghp.ghin.com/ghponline/dataservices/scoremethods.asmx/MostRecentScoresFilterDetailed?userName=string&password=string&ghinNumber=string&datePlayedBegin=string&datePlayedEnd=string&teeAssociationNumber=string&teeClubNumber=string&teeCourseNumber=string&teeTeeNumber=string&courseName=string&scoreType=string&scoreLow=string&scoreHigh=string&scoreCount=string HTTP/1.1

    String request = "http://ghp.ghin.com/ghponline/dataservices/scoremethods.asmx/" +
        "MostRecentScoresFilterDetailed" +
            "?username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) +
            "&password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) +
            "&ghinNumber=" + getHdcpNum(user, con) +
            "&datePlayedBegin=1/1/1900" +
            "&datePlayedEnd=1/1/1900" +
            "&teeAssociationNumber=0" +
            "&teeClubNumber=0" + // clubNum +
            "&teeCourseNumber=0" +
            "&teeTeeNumber=0" +
            "&CourseName=" +
            "&scoreType=All" +
            "&scoreLow=0" +
            "&scoreHigh=0" +
            "&scoreCount=22"; // extra two for sanity


    if (Common_Server.SERVER_ID == 4) out.println("<!-- request=" + request + " -->");

    try {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;

        // see if document returned has the container element we are looking for - if not error
        try {
            node = document.getElementsByTagName("ArrayOfScore").item(0);
        } catch (Exception ex) {
        }

        if (node == null) {

            ret[0] = "0";
            ret[1] = "We did not receive a valid response from the GHIN server. If your GHIN number (" + hdcp_num + ") is correct then it's likely a communication error between our servers. Please let us know if this problem persists."; // <!-- " + ex.toString() + " -->
            return ret;
        }

        if (Common_Server.SERVER_ID == 4) out.println("<!-- GOT WHAT APPEARS TO BE A VALID RESPONSE -- PARSING NOW -->");

        for (int i = 0; i < 20; i++) {

            // less than 20 may have been returned so we'll try/catch the indiviidual "Score" elements of the respone tree
            try {

                node = document.getElementsByTagName("ScoreValue").item(i);
                score = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                node = document.getElementsByTagName("Type").item(i);
                type = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                node = document.getElementsByTagName("DatePlayed").item(i);
                date = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                ret[i + 2] = score + "|" + type + "|" + date;

                //if (Common_Server.SERVER_ID == 4) out.println("<!-- FOUND " + ret[i + 2] + " ON PASS " + i + " -->");

            } catch (Exception ignore) {
                //if (Common_Server.SERVER_ID == 4) out.println("<!-- ERROR ON PASS " + i + " -->");
                break;
            }

        }

    } catch (ParserConfigurationException parserError) {
            ret[0] = "0";
            ret[1] = "ParserConfigurationException";
            Utilities.logError("Common_ghin:getRecentPostings() Failed Inquiry - parserError: user=" + user + ", club=" + club + ", err==" + parserError.toString() + ", request=" + request);
        return ret;
    }

    ret[0] = "1";
    ret[1] = "";

    return ret;
 }


 public static String[] getCurrentHdcp(String user, String club, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {

    String ret[] = new String[6];

    String hdcp_num = getHdcpNum(user, con);

    String service_num = "1"; // default to 1
    String success = "";
    String message = "";

    //
    // FIRST LETS DETERMIN THE MEMBERS GHIN SERVICE NUMBER
    //

    String request = "http://ghp.ghin.com/ghponline/dataservices/golfermethods.asmx/FindGolfer?" +
                    "username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) + "&" +
                    "password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) + "&" +
                    "ghinNumber=" + hdcp_num + "&" +
                    "association=" + clubAssocNum + "&" +
                    "club=" + clubNum + "&" +
                    "service=0&" +
                    "lastName=&" +
                    "firstName=&" +
                    "gender=&" +
                    "activeOnly=true&" +
                    "includeLowHandicapIndex=false&" +
                    "includeAffiliateClubs=false";

    // DEBUG
    if (Common_Server.SERVER_ID == 4) out.println("<!-- request=" + request + " -->");


    try {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;

        if (Common_Server.SERVER_ID == 4) out.println("<!-- response=" + getStringFromDocument(document) + " -->");
/*
        // make sure we have a valid document
        try {

            node = document.getElementsByTagName("Golfer").item(0);

        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "It's likely that the GHIN number provided (" + hdcp_num + ") is incorrect or is inactive. Please verify the GHIN number is correct. <!-- " + ex.toString() + " -->";
            return ret;
        }
*/

        try {
            node = document.getElementsByTagName("Service").item(0);
            service_num = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
          //ret[1] = "Error parsing service number. " + ex.toString();
            ret[1] = "It's likely that the GHIN number provided (" + hdcp_num + ") is incorrect or is inactive or the Club Number("+clubNum+") or Association Number ("+clubAssocNum+") is incorrect. Please verify these are correct and try again. <!-- " + ex.toString() + " -->";
            return ret;
        }

    } catch (ParserConfigurationException parserError) {
        ret[0] = "0";
        ret[1] = parserError.toString(); //parserError.printStackTrace();
    }



    //
    // NOW PERFORRM THE HANDICAP LOOKUP
    //
    request = "http://ghp.ghin.com/ghponline/dataservices/golfermethods.asmx/HandicapHistoryCount" +
            "?username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) +
            "&password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) +
            "&GHINNumber=" + hdcp_num +
            "&Assoc=" + clubAssocNum +
            "&Club=" + clubNum +
            "&Service=" + service_num +
            "&revCount=1";



    if (Common_Server.SERVER_ID == 4) out.println("<!--request=" + request + "-->");

    // the vars we are after
    String hdcpIndex = "";
    String hdcpDiff = "";
    String hdcpDisplay = "";
    String hdcpDate = "";
    String status = "";


    try {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;


        // see if document returned has the container element we are looking for - if not error
        try {

            node = document.getElementsByTagName("HIHistory").item(0);

        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "It's likely that the GHIN number provided (" + hdcp_num + ") is incorrect. Please verify the GHIN number is correct. <!-- " + ex.toString() + " -->";
            return ret;
        }

        // get hdcp value
        try {

            node = document.getElementsByTagName("Value").item(0);
            hdcpIndex = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Value - " + ex.toString();
            return ret;
        }

        // get hdcp display value
        try {

            node = document.getElementsByTagName("Display").item(0);
            hdcpDisplay = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Display - " + ex.toString();
            return ret;
        }

        // get hdcpDate
        try {

            node = document.getElementsByTagName("RevDate").item(0);
            hdcpDate = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "RevDate - " + ex.toString();
            return ret;
        }

        success = "1";


/*      OLD CODE

            try {
                node = document.getElementsByTagName("Success").item(0);
            } catch (Exception ex) {
                ret[0] = "0";
                ret[1] = "Success.item";
                return ret;
            }

            try {
                success = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
            } catch (Exception ex) {
                ret[0] = "0";
                ret[1] = "Success.getData: " + ex.toString();
                return ret;
            }

            node = document.getElementsByTagName("Message").item(0);

            try {
                message = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
            } catch (Exception ex) {
                ret[0] = "0";
                ret[1] = "Message.getData";
                return ret;
            }

            // these may not be here in a failure

            if (success.equals("1")) {

                // get hdcpIndex
                try {

                    node = document.getElementsByTagName("HcpIndex").item(0);
                    hdcpIndex = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                } catch (Exception ex) {
                    ret[0] = "0";
                    ret[1] = "HcpIndex - " + ex.toString();
                    return ret;
                }

                // get hdcpDate
                try {

                    node = document.getElementsByTagName("HcpDate").item(0);
                    hdcpDate = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                } catch (Exception ex) {
                    ret[0] = "0";
                    ret[1] = "HcpDate - " + ex.toString();
                    return ret;
                }

                // get status
                try {

                    node = document.getElementsByTagName("Status").item(0);
                    status = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                } catch (Exception ex) {
                    ret[0] = "0";
                    ret[1] = "Status - " + ex.toString();
                    return ret;
                }

            } // end if success != 1

        }
*/
    } catch (ParserConfigurationException parserError) {
            ret[0] = "0";
            ret[1] = "ParserConfigurationException";
        return ret;
    }

    if (!success.equals("1")) {

        Utilities.logError("Common_ghin:getCurrentHdcp() user=" + user + ", club=" + club + ", success=" + success + ", Message=" + message);

    } else {

        ret[0] = success;
        ret[1] = "OK";
        ret[2] = hdcpIndex;
        ret[3] = hdcpDisplay;
        ret[4] = hdcpDate;
        ret[5] = "1";


/*  OLD RETURN DATA
            ret[0] = success;
            ret[1] = message;
            ret[2] = hdcpIndex;
            ret[3] = hdcpDiff;
            ret[4] = hdcpDate;
            ret[5] = status;
*/
    }

    return ret;
 }


 public static String[] postScore(String user, String club, long date, int tee_id, int score, String type, int holes, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {

    ResultSet rs = null;
    PreparedStatement pstmt = null;

    String ret[] = new String[2]; // array to return
    String hdcp_num = getHdcpNum(user, con);
    String course = "";
    String tee_name = "";
    String service_num = "";

    double rating = 0;
    int slope = 0;



    // /ghponline/dataservices/golfermethods.asmx/FindGolfer?username=string&password=string&ghinNumber=string&association=string&club=string&service=string&lastName=string&firstName=string&gender=string&activeOnly=string&includeLowHandicapIndex=string&includeAffiliateClubs=string HTTP/1.1

    String last_name = Utilities.getLastNameFromUsername(user, con);

    String request = "http://ghp.ghin.com/ghponline/dataservices/golfermethods.asmx/FindGolfer?" +
                    "username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) + "&" +
                    "password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) + "&" +
                    "ghinNumber=" + hdcp_num + "&" +
                    "association=" + clubAssocNum + "&" +
                    "club=" + clubNum + "&" +
                    "service=0&" +
                    "lastName=&" +
                    "firstName=&" +
                    "gender=&" +
                    "activeOnly=true&" +
                    "includeLowHandicapIndex=false&" +
                    "includeAffiliateClubs=false";

    // DEBUG
    if (Common_Server.SERVER_ID == 4) out.println("<!-- request=" + request + " -->");

    // the vars we are after
    String success = "";
    String message = "";

    String response = "default";

    try {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;

        // make sure we have a valid document
        try {

            node = document.getElementsByTagName("Golfer").item(0);

        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "It's likely that the GHIN number provided (" + hdcp_num + ") is incorrect or is inactive. Please verify the GHIN number is correct. <!-- " + ex.toString() + " -->";
            return ret;
        }

        try {

            node = document.getElementsByTagName("LastName").item(0);
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = ex.toString();
            return ret;
        }

        try {
            last_name = ((CharacterData)node.getFirstChild()).getData();
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Error looking up last name.  " + ex.toString();
            return ret;
        }

        node = document.getElementsByTagName("Service").item(0);

        try {
            service_num = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Error lookup up service number. " + ex.toString();
            return ret;
        }

    } catch (ParserConfigurationException parserError) {
        ret[0] = "0";
        ret[1] = parserError.toString(); //parserError.printStackTrace();
    }



    int year = (int)date / 10000;
    int month = ((int)date - (year * 10000)) / 100;
    int day = ((int)date - (year * 10000)) - (month * 100);

    // build the date string for ghin - could add the time as well 2012-06-16T00:00:00
    String ghin_date = year + "-" + Utilities.ensureDoubleDigit(month) + "-" + Utilities.ensureDoubleDigit(day);


    // get course & tee info
    String tmp = "";
    int tmp_holes = 1;
    try {
        if (holes == 1) {
            tmp = "18";
        } else if (holes == 2) {
            tmp = "F9";
            tmp_holes = 2;
        } else if (holes == 3) {
            tmp = "B9";
            tmp_holes = 2;
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
            rating = rs.getDouble("tee_rating");
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

    // set the type of score
    if (type.equals("H")) {
        type = "Home";
    } else if (type.equals("T")) {
        type = "Tournament";
    } else {
        // should ever fall in here - but in case we add more and forget to remap let default to home
        type = "Home";
    }

    tmp_holes = (tmp_holes == 1) ? 18 : 9;

    // date needs to be in mm/dd/yyyy format or 2012-06-16T00:00:00



    //
    // POST THE SCORE TO THE GHIN SERVER
    //


    request = "http://ghp.ghin.com/ghponline/dataservices/scoremethods.asmx/PostScoreCourseName" +
                "?username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) +
                "&password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) +
                "&ghinNumber=" + hdcp_num +
                "&lastName=" + scrubCourseName(last_name) +
                "&postFromAssoc=" + clubAssocNum +
                "&postFromClub=" + clubNum +
                "&postFromService=" + service_num +
                "&holes=" + tmp_holes +
                "&scoreDate=" + ghin_date +
                "&score=" + score +
                "&courseRating=" + rating +
                "&slope=" + slope +
                "&scoreType=" + type +
                "&internet=true" +
                "&courseName=" + ((course.equals("")) ? scrubCourseName(Utilities.getClubName(con, true)) : scrubCourseName(course)) +
                "&overrideWarning=true";

    // DEBUG
    if (Common_Server.SERVER_ID == 4) out.println("<!-- request=" + request + " -->");

    try {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;

        response = getStringFromDocument(document);

        // make sure we have a valid document
        try {

            node = document.getElementsByTagName("ScorePostingReturn").item(0);

        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "It's likely that the GHIN number provided (" + hdcp_num + ") is incorrect or is inactive. Please verify the GHIN number is correct. <!-- " + ex.toString() + " -->";
            if (Common_Server.SERVER_ID == 4) out.println("<!-- response=" + response + " -->");
            Utilities.logError("Common_ghin:postScore(): ScorePostingReturn not found: err=" + ex.toString() + ", user=" + user + ", club=" + club + ", request=" + request + ", response=" + response);
            return ret;
        }

        try {
            node = document.getElementsByTagName("IsSuccess").item(0);
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Get IsSuccess: " + ex.toString();
            Utilities.logError("Common_ghin:postScore(): Get IsSuccess: " + ex.toString() + ", user=" + user + ", club=" + club + ", request=" + request + ", response=" + response);
            return ret;
        }

        try {
            success = ((CharacterData)node.getFirstChild()).getData();
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Parse IsSuccess: " + ex.toString();
            Utilities.logError("Common_ghin:postScore(): Parse IsSuccess: " + ex.toString() + ", user=" + user + ", club=" + club + ", request=" + request + ", response=" + response);
            return ret;
        }

        if (Common_Server.SERVER_ID == 4) out.println("<!-- success=" + success + " -->");

        node = document.getElementsByTagName("Message").item(0);

        try {
            //message = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
            message = ((CharacterData)node.getFirstChild()).getData();
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = "Parse Message: " + ex.toString();
            Utilities.logError("Common_ghin:postScore(): Parse Message: " + ex.toString() + ", user=" + user + ", club=" + club + ", request=" + request + ", response=" + response);
            return ret;
        }

        if (Common_Server.SERVER_ID == 4) out.println("<!-- message=" + message + " -->");

    } catch (ParserConfigurationException parserError) {
        ret[0] = "0";
        ret[1] = parserError.toString(); //parserError.printStackTrace();
        Utilities.logError("Common_ghin:postScore(): ParserConfigurationException: " + parserError.toString() + ", user=" + user + ", club=" + club + ", request=" + request + ", response=" + response);
    }

    if (!success.equalsIgnoreCase("true")) {

        // if here then the score was not accepted for some reason.  we probably don't want to log all of these since the reason is
        // likely between the golfer and ghin and not because of a server problem, communication error or a bug
        // however we do want to log some like the "Golfer Validation Failed" responses since those are because we are not sending the correct info
        if (message.equalsIgnoreCase("Score outside usual range") ||
            message.equalsIgnoreCase("This score is a duplicate") ||
            message.equalsIgnoreCase("Score higher than max allowed or lower than min allowed") ||
            message.equalsIgnoreCase("Score Too Low")) {
            // do not log

        } else {
            // Golfer Validation Failed - may not be sending correct last name
            // Course Rating out of range - course values not defined properly in club setup
            // Posted From golf service not found or invalid
            Utilities.logError("Common_ghin:postScore() user=" + user + ", club=" + club + ", success=" + success + ", Message=" + message + ", request=" + request);
        }
        ret[0] = "0";
        ret[1] = message;

    } else {

        ret[0] = "1";
        ret[1] = message;

    }

    return ret;

 }


 public static void getHandicaps(PrintWriter out) {

    Connection con = null;
    Connection con2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    String club = "";

    long startTime = System.currentTimeMillis();

    Utilities.logError("Starting Common_ghin.getHandicaps()");

    try {

        con = dbConn.Connect(rev);
    } catch (Exception e1) {

        Utilities.logError("Common_ghin.getHandicaps: FATAL! Error connecting to v5 db. " + e1.getMessage());                                       // log it
    }

    if (con != null) {

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

            // loop all clubs
            while (rs.next()) {

                club = rs.getString(1);

                if (!club.startsWith("demo")) {

                    con2 = dbConn.Connect(club);

                    if (con2 != null) {

                        // only process if club is using ghin
                        if (Common_handicaps.getClubHdcpOption(club, con2).equalsIgnoreCase("GHIN")) {

                            // perform handicap queryf or club
                            getHandicapsForClub(club, con2, out);

                            // update the hdcp values in member2b for this club and update teecurr2 & event signups
                            updateMemberHdcps(club, con2);

                        }

                        try { con2.close();
                        } catch (Exception ignored) { }

                    } else {

                        Utilities.logError("Common_ghin.getHandicaps: FATAL! Could not connect to " + club + " db.");
                    }

                } // end if not demo sites

            } // end loop of all clubs in v5.clubs

            stmt.close();

        } catch (Exception e2) {

            Utilities.logError("Common_ghin.getHandicaps: Error processing. Club=" + club + ".  Exception was " + e2.getMessage());
        }

        try {
            con.close();
        } catch (Exception ignored) { }

    }

    Utilities.logError("Finished Common_ghin.getHandicaps() run time:" + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min)");

 }


 public static void getHandicapsForClub(String club, Connection con, PrintWriter out) {


    String clubNum = "";
    String clubAssocNum = "";
    String request = "";

    int count1 = 0;
    int count2 = 0;

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {

        // gather all the club number / club association number combinations that are in use
        // in the member2b table, then use these combinations to query the GHIN ES server
        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT cn.club_num, ca.assoc_num " +
                "FROM member2b m " +
                "LEFT OUTER JOIN hdcp_club_num cn ON cn.hdcp_club_num_id = m.hdcp_club_num_id " +
                "LEFT OUTER JOIN hdcp_assoc_num ca ON ca.hdcp_assoc_num_id = m.hdcp_assoc_num_id " +
                "WHERE m.hdcp_club_num_id <> 0 AND m.hdcp_assoc_num_id <> 0 " +
                "GROUP BY m.hdcp_club_num_id, m.hdcp_assoc_num_id;");

        while ( rs.next() ) {

          clubNum = rs.getString("club_num");
          clubAssocNum = rs.getString("assoc_num");

          if (clubNum != null && clubAssocNum != null) {
              
            request = "http://ghp.ghin.com/ghponline/dataservices/golfermethods.asmx/FindGolfer?" +
                    "username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) + "&" +
                    "password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) + "&" +
                    "ghinNumber=0&" +
                    "association=" + clubAssocNum + "&" +
                    "club=" + clubNum + "&" +
                    "service=0&" +
                    "lastName=&" +
                    "firstName=&" +
                    "gender=&" +
                    "activeOnly=true&" +
                    "includeLowHandicapIndex=false&" +
                    "includeAffiliateClubs=false";


            if (out != null) out.println("<!-- request=" + request + " -->");

            String ghin = "";       // GHINNumber
            String hdcp = "";      // Value
            double dbl_hdcp = 0;    // handicap value

            int i = 0;

            try {

                long startTime = System.currentTimeMillis();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = getDocument(builder, request);

                if (document == null) {

                    if (out != null) out.println("<p>Fatal Error: The document returned from the GHIN server is invalid (null).</p>");

                    Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ")  - document is null!  Encoding issue? request=" + request);
                    return;
                }

                // LOG DATA RETRIEVAL TIME
                //Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ")  - INFO - Data retrieval took " + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000) + " seconds)");


                //
                // We received what appears to be a valid XML document, now let's try and parse it
                //

                Node node = null;


                // see if document returned has the container element we are looking for - if not error
                try {
                    node = document.getElementsByTagName("ArrayOfGolfer").item(0);
                } catch (Exception ex) {

                    if (out != null) out.println("<p>Fatal Error: The document returned from the GHIN server does not contain an 'ArrayOfGolfer' node.</p>");

                    Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ") - No ArrayOfGolfer node found. Error: " + ex.toString() + "");
                    return;
                }


                // if our request was successfull then lets continue processing

                try {

                    startTime = System.currentTimeMillis();

                    // keep looping as long as we keep finding Player elements
                    while (document.getElementsByTagName("Golfer").item(i) != null) {

                        node = document.getElementsByTagName("GHINNumber").item(i);
                        ghin = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        // sanity check to ensure we don't run an update statement on an empty ghin number
                        if (ghin != null && !ghin.equals("")) {

                            node = document.getElementsByTagName("Display").item(i);
                            hdcp = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                            dbl_hdcp = parseHandicapValue(hdcp);

                            int count = 0;

                            try {

                                pstmt = con.prepareStatement(
                                    "UPDATE member2b SET g_hancap = ? WHERE CONVERT(CAST(REPLACE(ghin, '-', '') AS UNSIGNED), CHAR) = ?");

                                pstmt.clearParameters();
                                pstmt.setDouble(1, dbl_hdcp);
                                pstmt.setString(2, ghin);
                                count = pstmt.executeUpdate();

                            } catch (SQLException exc) {

                                Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ") Update Error. i=" + i + ", ghin=" + ghin + ".  Error=" + exc.toString());

                            } finally {

                                try { pstmt.close(); }
                                catch (Exception ignore) {}

                            }

                            if (out != null) out.println("<br>Found " + dbl_hdcp + " for " + ghin + "." + ((count != 0) ? "Saved." : "Update failed!!!"));

                        } else {
                            if (out != null) out.println("<br>GHINNumber not found.");
                        }

                        i++;

                    } // while there are more Golfer nodes found

                    // LOG DATA PARSING TIME
                    //Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ")  - INFO - Data parsing took " + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000) + " seconds)");

                } catch (Exception ex) {

                    // an error occured while parsing the xml document we retrieved.
                    Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ") Error parsing XML file.  i=" + i + ".  Error=" + ex.toString() + ".  URL=" + request + ", strace=" + Utilities.getStackTraceAsString(ex));
                }

            } catch (ParserConfigurationException parserError) {

                Utilities.logError("Common_ghin:getHandicapsForClub() ParserConfigurationException: " + parserError.toString());
                return;
            }

          } // end if null
          
        } // end loop of unique club num/assoc num combinations to query data for

    } catch (Exception ex) {

        Utilities.logError("Common_ghin:getHandicapsForClub(" + club + ") Error: " + ex.toString());

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 }

 
 public static void getPostedScoresForClub(String club, String startDate, String endDate, String service, Connection con, PrintWriter out) {


    String clubNum = "";
    String clubAssocNum = "";
    String request = "";
    String job_start_time = "";   // Date

    String startDateDB  = "";
    String endDateDB  = "";

    String [] dateparts = new String[3];

    dateparts = startDate.split("/");

    startDateDB = dateparts[2] + "-" + dateparts[0] + "-" + dateparts[1];

    dateparts = endDate.split("/");

    endDateDB = dateparts[2] + "-" + dateparts[0] + "-" + dateparts[1];

    int count1 = 0;
    int count2 = 0;

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) FROM score_postings;");
        if (rs.next()) count1 = rs.getInt(1);

        // Get time of DB server as our start time
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT NOW() AS db_time");
        if (rs.next()) job_start_time = rs.getString("db_time");

        // gather all the club number / club association number combinations that are in use
        // in the member2b table, then use these combinations to query the GHIN ES server
        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT cn.club_num, ca.assoc_num " +
                "FROM member2b m " +
                "LEFT OUTER JOIN hdcp_club_num cn ON cn.hdcp_club_num_id = m.hdcp_club_num_id " +
                "LEFT OUTER JOIN hdcp_assoc_num ca ON ca.hdcp_assoc_num_id = m.hdcp_assoc_num_id " +
                "WHERE m.hdcp_club_num_id <> 0 AND m.hdcp_assoc_num_id <> 0 " +
                "GROUP BY m.hdcp_club_num_id, m.hdcp_assoc_num_id;");

        while ( rs.next()) {

          clubNum = rs.getString("club_num");
          clubAssocNum = rs.getString("assoc_num");

          if (clubNum != null && clubAssocNum != null) {
            
            request = "http://ghp.ghin.com/ghponline/dataservices/scoremethods.asmx/ClubMemberScores" +
                    "?username=" + ((club.startsWith("demo")) ? "ws98" : ghp_user) +
                    "&password=" + ((club.startsWith("demo")) ? "ws98" : ghp_pass) +
                    "&Association=" + clubAssocNum +
                    "&Club=" + clubNum +
                    "&Service=0" +
                    "&activeOnly=false" +
                    "&datePlayedBegin=" + startDate +
                    "&datePlayedEnd=" + endDate +
                    "&datePostedBegin=1/1/1900" +
                    "&datePostedEnd=1/1/2050" +
                    "&teeAssociationNumber=0" +
                    "&teeClubNumber=0" +
                    "&teeCourseNumber=0" +
                    "&teeTeeNumber=0" +
                    "&crpTeeID=0" +
                    "&courseName=" +
                    "&scoreType=All" +
                    "&scoreLow=0" +
                    "&scoreHigh=0";



            if (out != null) out.println("<!-- request=" + request + " -->");

            String ghin = "";   // Ghinnum
            String type = "";   // Stype
            String hdcp = "";   // HCP
            String date = "";   // Date
            String posted = "";   // Date

            String rating = ""; // Rating
            int score = 0;      // Score
            double dbl_hdcp = 0;
            int slope = 0;      // Score
            double dbl_rating = 0;

            String [] parts = new String[3];

            int i = 0;
            int tee_id = 0;

            try {

                long startTime = System.currentTimeMillis();

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = getDocument(builder, request);

                if (document == null) {

                    if (out != null) out.println("<p>Fatal Error: The document returned from the GHIN server is invalid (null).</p>");

                    Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ")  - document is null!  Encoding issue? request=" + request);
                    return;
                }

              //Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ")  - INFO - Data retrieval took " + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000) + " seconds)");


                //
                // We received what appears to be a valid XML document, now let's try and parse it
                //

                Node node = null;


                // see if document returned has the container element we are looking for - if not error
                try {
                    node = document.getElementsByTagName("ArrayOfScore").item(0);
                } catch (Exception ex) {

                    if (out != null) out.println("<p>Fatal Error: The document returned from the GHIN server does not contain a 'ArrayOfScore' node.</p>");

                    Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") - No ArrayOfScore node found. Error: " + ex.toString() + "");
                    return;
                }


                // if our request was successfull then lets continue processing

                try {

                    startTime = System.currentTimeMillis();

                    // keep looping as long as we keep finding Player elements
                    while (document.getElementsByTagName("Score").item(i) != null) {

                        node = document.getElementsByTagName("Type").item(i);
                        type = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        // only process this entry if it's a home score (these should be getting filtered out by our request but doesn't seem to work)
//                      if ( type.equalsIgnoreCase("H") ) {  (WE ARE NOW PROCESSING ALL DOWNLOADED SCORES!)

                        node = document.getElementsByTagName("GHINNumber").item(i);
                        ghin = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        node = document.getElementsByTagName("ScoreValue").item(i);
                        score = Integer.parseInt(SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData()));

                        node = document.getElementsByTagName("DatePlayed").item(i);
                        date = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        node = document.getElementsByTagName("DateUpdated").item(i);
                        posted = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        //node = document.getElementsByTagName("HCP").item(i);
                        //hdcp = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                        node = document.getElementsByTagName("Slope").item(i);
                        slope = Integer.parseInt(SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData()));

                        node = document.getElementsByTagName("CR").item(i);
                        rating = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());


                        //dbl_hdcp = parseHandicapValue(hdcp);

/*
                        // we may get a NH handicap, so if we can't convert to double then use -99
                        if (hdcp.equalsIgnoreCase("NH") || hdcp.equalsIgnoreCase("NHL") ||
                            hdcp.equalsIgnoreCase("NI") || hdcp.equalsIgnoreCase("N/I")) {

                            dbl_hdcp = -99;

                        } else {

                            try {
                                hdcp = hdcp.replace("L", "");
                                hdcp = hdcp.replace("R", "");
                                hdcp = hdcp.replace("H", "");
                                hdcp = hdcp.replace("N", "");
                                hdcp = hdcp.replace("J", "");
                                hdcp = hdcp.replace("M", "");
                                hdcp = hdcp.trim();
                                //if (!hdcp.startsWith("+")) hdcp.replace("+", "-"); // incoming indexs are assumed to be negative unless specified as positive
                                if (!hdcp.startsWith("+")) hdcp = "-" + hdcp; // incoming indexs are assumed to be negative unless specified as positive
                                dbl_hdcp = Double.parseDouble(hdcp);
                            } catch (NumberFormatException exc) {

                                dbl_hdcp = -99;
                                Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") err=" + exc.toString() + ", hdcp=" + hdcp);
                            }

                        }
*/
                        try {

                            dbl_rating = Double.parseDouble(rating);
                        } catch (NumberFormatException exc) {

                            Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") err=" + exc.toString() + ", rating=" + rating);
                        }

                        // try to determin this tee from the slope/rating posted for this score
                        tee_id = 0;
                        if (dbl_rating != 0 && slope != 0) {

                            tee_id = getTeeIdFromSlopeRating(slope, dbl_rating, con);
                            if (out != null && tee_id == 0) out.println("<br>Can't find tee for slope=" + slope + " and rating=" + dbl_rating);
                        }

                        // break the date up into chunks

                        // date format is 2012-07-01T00:00:00
                        parts = date.split("T");
                        parts = parts[0].split("-");
                        date = parts[0] + "" + parts[1] + "" + parts[2]; // yyyymmdd (assumes ghin always gives us leading zeros for month & day)

                        try {

                            // Attempt to insert each record we received, and any that we aleady have (based
                            // on the unique index of hdcpNum, date, score, type) are ignored and not inserted
                            // INSERT...ON DUPLICATE KEY UPDATE is a non-standard, proprietary inventions specific to MySQL.
                            // ANSI SQL 2003 defines a MERGE statement that can solve the same need (and more), but MySQL does not support the MERGE statement...yet
                            /*
                            PreparedStatement pstmt = con.prepareStatement(
                                "INSERT IGNORE INTO score_postings " +
                                "(hdcpNum, date, score, type, hdcpIndex, tee_id) VALUES (?, ?, ?, ?, ?, ?)");
                            */

                            pstmt = con.prepareStatement(
                                "INSERT INTO score_postings " +
                                "(hdcpNum, provider_uuid, date, score, type, tee_id, last_touched) " +
                                "VALUES (CAST(? AS UNSIGNED), CONCAT_WS(':',CAST(? AS UNSIGNED),?,?), ?, ?, ?, ?, NOW()) " +
                                "ON DUPLICATE KEY UPDATE date = VALUES(date), score = VALUES(score), type = VALUES(type), tee_id = VALUES(tee_id), last_touched = NOW()");

                            pstmt.clearParameters();
                            pstmt.setString(1, ghin); // hdcpNum
                            pstmt.setString(2, ghin); // part of uuid
                            pstmt.setString(3, posted); // part of uuid
                            pstmt.setString(4, date); // part of uuid
                            pstmt.setString(5, date); // date insert
                            pstmt.setInt(6, score); // score insert
                            pstmt.setString(7, type); // type insert
                            pstmt.setInt(8, tee_id); // teeid insert
                            pstmt.executeUpdate();
                            pstmt.close();



                        } catch (SQLException exc) {

                            Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") INSERT Error. i=" + i + ".  Error=" + exc.toString() + ", Score=" + score + ", Type=" + type);

                        } finally {

                            try { pstmt.close(); }
                            catch (Exception ignore) {}

                        }

                        i++;

                    } // while there are more score nodes found


                    try {

                        // Delete any score posting withing the specified date range that were not updated/inserted durring this cycle
                        pstmt = con.prepareStatement(
                            "DELETE FROM score_postings " +
                            "WHERE (last_touched < ? OR last_touched IS NULL) " +
                            "AND `date` BETWEEN ? AND ?");

                        pstmt.clearParameters();
                        pstmt.setString(1, job_start_time); // hdcpNum
                        pstmt.setString(2, startDateDB); // part of uuid
                        pstmt.setString(3, endDateDB); // part of uuid
                        pstmt.executeUpdate();
                        pstmt.close();

                    } catch (SQLException exc) {

                        Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") DELETE Error. i=" + i + ".  Error=" + exc.toString() + ", Score=" + score + ", Type=" + type);

                    } finally {

                        try { pstmt.close(); }
                        catch (Exception ignore) {}

                    }



                  //Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ")  - INFO - Data parsing took " + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000) + " seconds)");

                } catch (Exception ex) {

                    // an error occured while parsing the xml document we retrieved.
                    Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") Error parsing XML file.  i=" + i + ".  Error=" + ex.toString() + ".  URL=" + request + ", strace=" + Utilities.getStackTraceAsString(ex));
                }

            } catch (ParserConfigurationException parserError) {

                Utilities.logError("Common_ghin:getPostedScoresForClub() ParserConfigurationException: " + parserError.toString());
                return;
            }
            
          } // if not null
        
        } // end loop of unique club num/assoc num combinations to query data for

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) FROM score_postings;");
        if (rs.next()) count2 = rs.getInt(1);

    } catch (Exception ex) {

        Utilities.logError("Common_ghin:getPostedScoresForClub(" + club + ") Error: " + ex.toString());

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    //if (count2 - count1 > 0) Utilities.logError("Finished Common_ghin.getPostedScoresForClub(" + club + "): Downloaded " + (count2 - count1) + " postings for " + startDate + " - " + endDate);
    if (out != null) out.println("<p>Added " + (count2 - count1) + " score postings for " + startDate + " - " + endDate + "</p>");
 }


 public static void getPostedScores(PrintWriter out) {


    // only run on server #1 OR if called with a valid out object.  TimerGHIN will call this method with a null out obj.  Support_sync will call with valid out
    if (Common_Server.SERVER_ID == SystemUtils.TIMER_SERVER || out != null) {

        Connection con = null;
        Connection con2 = null;
        Statement stmt = null;
        ResultSet rs = null;
        Statement stmt2 = null;
        ResultSet rs2 = null;

        String club = "";
        String startDate = null;
        String endDate = null;
        String altEndDate = null;
        String altStartDate = null;

        int processed_clubs = 0;

        long startTime = System.currentTimeMillis();
        Utilities.logError("Starting Common_ghin.getPostedScores()");

        try {

            con = dbConn.Connect(rev);
        } catch (Exception e1) {

            Utilities.logError("Common_ghin.getPostedScores: Error connecting to v5 db. " + e1.getMessage());                                       // log it
        }

        if (con != null) {

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT clubname FROM clubs WHERE inactive = 0 ORDER BY clubname");

                // loop all clubs
                while (rs.next()) {

                    club = rs.getString(1);

                    if (!club.startsWith("demo")) {

                        con2 = dbConn.Connect(club);

                        if (con2 != null) {

                            // query this club to see if they are ghin and are in-season
                            stmt2 = con2.createStatement();
                            rs2 = stmt2.executeQuery("" +
                                    "SELECT " +
                                        "hdcpSystem, lastHdcpSync, " +
                                        "DATE_FORMAT(hdcpStartDate, '%Y%m%d') AS startDate, " +
                                        "DATE_FORMAT(hdcpEndDate, '%Y%m%d') AS endDate " +
                                    "FROM club5 " +
                                    "WHERE " +
                                        "hdcpSystem = 'GHIN';"); /* AND " +
                                        "hdcpStartDate <= DATE_FORMAT(now(), '%Y%m%d') AND " +
                                        "hdcpEndDate >= DATE_FORMAT(now(), '%Y%m%d');"); */

                            if (rs2.next()) {

                                processed_clubs++;

                                startDate = null;
                                endDate = null;
                                altEndDate = null;
                                altStartDate = null;
                                //int tmp_sdate = 0;
                                //int tmp_edate = 0;
                                //int tmp_adate = 0;
                                //int tmp_date = 0;
                                //int diff = 0;

                                // get the most recent date for which we have downloaded posted scores
                                // notes:
                                //   startDate      is most recent date date for which we have downloaded posted scores, null if none d/l yet
                                //   endDate        will always be returned as todays date
                                //   altEndDate     will be used if endDate minus startDate is greater than 30 days (ghin only allows 30days to be retrieved per request)
                                //   altStartDate   will be returned, used if startDate is null, we assume this is first run and this date will be today minus 30 days
                                rs2 = stmt2.executeQuery("" +
                                        "SELECT " +
                                            "DATE_FORMAT(MAX(date), '%Y%m%d') AS startDate, " +
                                            "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 1 DAY), '%m/%d/%Y') AS endDate, " +
                                            "DATE_FORMAT(DATE_ADD(MAX(date), INTERVAL + 30 DAY), '%Y%m%d') AS altEndDate, " +
                                            "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 35 DAY), '%m/%d/%Y') AS altStartDate, " + // '%Y%m%d'  WAS 28
                                            "DATEDIFF(now(), MAX(date)) AS diff " +
                                        "FROM score_postings;");

                                if (rs2.next()) {

                                    startDate = rs2.getString("altStartDate");
                                    endDate = rs2.getString("endDate");
                                    altEndDate = rs2.getString("altEndDate");
                                    altStartDate = rs2.getString("altStartDate");
                                    //diff = rs2.getInt("diff");
                                    //diff = (rs2.getInt("diff") != null) ? rs2.getInt("diff") : 0;

                                    //if (diff > 30) {

                                        //endDate = altEndDate;

                                    //} else if (startDate == null) {

                                        //startDate = altStartDate;

                                    //}
                                    //out.println("<p>" + startDate + ", " + endDate + ", " + altStartDate + ", " + altEndDate + ", " + diff + "</p>");

                                    // call routine that actually performs the data retrieval for a given club
                                    getPostedScoresForClub(club, startDate, endDate, "00", con2, out);

                                    // update the hdcp values in member2b for this club and update teecurr2 & event signups
                                    getHandicapsForClub(club, con2, out);
                                    updateMemberHdcps(club, con2);

                                }

                            } // end if found club using ghin and in season

                            con2.close();

                        } else {

                            Utilities.logError("Common_ghin.getPostedScores: Can not connect to " + club + " db.");
                        }

                    } // end if not demo sites

                } // end loop of all clubs in v5.clubs

                stmt.close();

            } catch (Exception e2) {

                Utilities.logError("Common_ghin.getPostedScores: Error processing. Club=" + club + ".  Exception was " + e2.getMessage());
            }

            try { con.close(); }
            catch (Exception ignored) { }

        } // end if con != null

        Utilities.logError("Finished Common_ghin.getPostedScores() Process clubs: " + processed_clubs + ", Run time:" + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min) using dates " + startDate + " -> " + endDate);

    } // end if timer server OR manual run

    TimerGHIN g_timer = new TimerGHIN();            // reset timer to update daily

 }


 private static String scrubGhinNum(String ghin) {

     ghin = ghin.replace("-", "");
     return ghin;
 }


 private static String scrubCourseName(String course) {

     // StringEscapeUtils.escapeHtml()

     course = course.replace(" ", "+");
     course = course.replace("&", "%26");
     return course;
 }


 protected static int getTeeIdFromSlopeRating(int slope, double rating, Connection con) {

    int tee_id = 0;

    try {

        PreparedStatement pstmt = con.prepareStatement (
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

        ResultSet rs = pstmt.executeQuery();

        if ( rs.next() ) tee_id = rs.getInt(1);

        pstmt.close();

    } catch (Exception exp) {

        Utilities.logError("Common_ghin:getTeeIdFromSlopeRating - Error: " + exp.toString());
    }

    return tee_id;

 }


 protected static String getHdcpNum(String user, Connection con) {

    // lookup ghin # for this user
    String hdcp_num = "";
    try {

        PreparedStatement pstmt = con.prepareStatement("SELECT ghin FROM member2b WHERE username = ?");
        pstmt.clearParameters();
        pstmt.setString(1, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) hdcp_num = scrubGhinNum(rs.getString(1));

        if (hdcp_num.length() == 5) { hdcp_num = "00" + hdcp_num;
        } else if (hdcp_num.length() == 6) hdcp_num = "0" + hdcp_num;

    } catch (Exception ignore) { }

    return hdcp_num;

 } // end getHdcpNum


 public static void updateMemberHdcps(String club, Connection con) {

     /*
      *
        // find the most played tee
        SELECT sp.hdcpIndex, sp.hdcpNum,
        (SELECT tee_id FROM score_postings sp2 WHERE sp2.hdcpNum = sp.hdcpNum GROUP BY sp.tee_id ORDER BY COUNT(*) DESC LIMIT 1) AS common_tee
        FROM score_postings sp
        GROUP BY sp.hdcpNum
        ORDER BY sp.date DESC;

      *
      */

    // c_hdcp = (g_hdcp * slope) / 113;

    Statement stmt = null;

    // UPDATE MEMBER2B TABLE WITH MOST RECENT HANDICAP DATA
    // NEW GHIN HDCO DOWNLOADS PUTS INDEX DIRECTLY IN MEMBER2B SO ONLY NEED TO
    // RUN CUSTOM HDCP INDEX TO COURSE HDCP CONVERSION QUERIES

    if (club.equals("cherryhills")) {

        try {

            // mens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'White') AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((m.g_hancap * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.hdcp_assoc_num_id = 1 AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'White') AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.hdcp_assoc_num_id = 1 AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");*/

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for cherryhills (mens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        try {

            // womens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'Red - Women') AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((m.g_hancap * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.hdcp_assoc_num_id = 2 AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'Red - Women') AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.hdcp_assoc_num_id = 2 AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");*/

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for cherryhills (womens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

    } else if (club.equals("pelicansnest")) {

        try {

            // mens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 4) AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.gender = 'M' AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 4) AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.gender = 'M' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");*/

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for pelicansnest (mens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        try {

            // womens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 7) AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.gender = 'F' AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 7) AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.gender = 'F' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");*/

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for pelicansnest (womens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

    } else if (club.equals("mnvalleycc")) {


        try {

            // mens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 9) AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.gender = 'M' AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 9) AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.gender = 'M' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " + // REPLACE(m.ghin, '-', '') = sp.hdcpNum
                    " ABS(sp.hdcpIndex) <> 99;");*/


        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for mnvalleycc (mens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        try {

            // womens
            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 14) AS s, " +
                    "member2b m " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE " +
                    "m.gender = 'F' AND " +
                    "ABS(m.g_hancap) <> 99;");

            /*stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 14) AS s, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " +
                "WHERE m.gender = 'F' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " + // REPLACE(m.ghin, '-', '') = sp.hdcpNum
                    " ABS(sp.hdcpIndex) <> 99;");*/

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b for mnvalleycc (womens)  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

    } else {


        /*  NEW GHIN HDCO DOWNLOADS PUTS INDEX DIRECTLY IN MEMBER2B SO NO NEED TO RUN THIS QUERY

        // ALL OTHER CLUBS
        try {

            stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " +
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " +
                    "member2b m, score_postings sp " +
                "SET m.g_hancap = sp.hdcpIndex " +
                "WHERE t.date = sp.date AND sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED);"); // AND REPLACE(m.ghin, '-', '') = sp.hdcpNum
                //"WHERE t.date = sp.date AND sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum;");

        } catch (Exception exp) {

            Utilities.logError("Common_ghin: Updating member2b - Club: " + club + "  Error: " + exp.toString());

        } finally {

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        */
    }

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

        Utilities.logError("Common_ghin: Updating teecurr2 - Club: " + club + "  Error: " + exp.toString());

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

        Utilities.logError("Common_ghin: Updating evntsup2b - Club: " + club + "  Error: " + exp.toString());

    } finally {

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

 } // end updateMemberHdcps



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
            Utilities.logError("Common_ghin:parseHandicapValue() err=" + exc.toString() + ", hdcp=" + hdcp);
        }

    }

    return dbl_hdcp;

 } // end if parseHandicapValue


 public static String getStringFromDocument(Document doc)
{
    try {

       DOMSource domSource = new DOMSource(doc);
       StringWriter writer = new StringWriter();
       StreamResult result = new StreamResult(writer);
       TransformerFactory tf = TransformerFactory.newInstance();
       Transformer transformer = tf.newTransformer();
       transformer.transform(domSource, result);
       return writer.toString();

    } catch(TransformerException ex) {

       ex.printStackTrace();
       return null;
    }
}

 // ********************************************************************
 // Returns an ArrayList containing abbreviations and descriptions for GHIN score types
 // ********************************************************************
 protected static ArrayList<String> getScoreTypes (){

        ArrayList<String> scoreTypes = new ArrayList<String>();

        scoreTypes.add("H - Home");
        scoreTypes.add("A - Away");
        scoreTypes.add("T - Tournament");
        scoreTypes.add("I - Internet");
        scoreTypes.add("AI - Away Internet");
        scoreTypes.add("TI - Tournament Internet");
        scoreTypes.add("C - Combined Nines");
        scoreTypes.add("CI - Combined Internet");
        scoreTypes.add("P - Penalty");

        return scoreTypes;
 }

}