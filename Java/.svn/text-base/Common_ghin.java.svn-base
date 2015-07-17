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
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.net.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

//import org.xml.sax.SAXException;
//import org.xml.sax.*;
//import org.xml.sax.helpers.DefaultHandler;

import com.foretees.common.parmCourse;
import com.foretees.common.parmClub;
import com.foretees.common.getParms;
import com.foretees.common.getClub;


public class Common_ghin extends HttpServlet {

    static String rev = SystemUtils.REVLEVEL;
    static String VASP = "t7pv5";  // GHIN ES VASP CODE
    
 
 private static Document getDocument(DocumentBuilder builder, String urlString) {

    try {

        URL url = new URL( urlString );

        try {

            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            int responseCode = httpConnection.getResponseCode();
            
            if ( responseCode == HttpURLConnection.HTTP_OK) {

                InputStream in = httpConnection.getInputStream();

                try {
                    Document doc = builder.parse(in);
                    in.close();
                    return doc;
                } catch(org.xml.sax.SAXException e) {
                    e.printStackTrace() ;
                }
                
                in.close();
                
            } else {

                System.out.println( "HTTP connection response != HTTP_OK" );
            }
        } catch ( IOException e ) { 
            e.printStackTrace();
        }
    } catch ( MalformedURLException e ) {  
        e.printStackTrace ( ) ;
    }
    
    return null;
}
 
 
 public static String[] getRecentPostings(String user, String club, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {

    String ret[] = new String[22]; // array to return
    String success = "";
    String message = "";
        
    String request = "http://mt5493.ghinconnect.com/ghin/" +
        "iihbp58.cgi?" +
            "gm=" + getHdcpNum(user, con) + "&" +
            "club=" + clubNum + "&" +
            "assoc=" + clubAssocNum + "&" +
            "scores=1&" +
            "vasp=" + VASP;

    //out.println("<p>request=" + request + "</p>");
    
    try {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;
        
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
            ret[1] = "Success.getDate";
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
        String score = "";
        String type = "";
        String date = "";
        
        if (success.equals("1")) {
            
            for (int i = 0; i < 20; i++) {
                
                // get score
                try {
                    
                    node = document.getElementsByTagName("Score").item(i);
                    score = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                    
                    node = document.getElementsByTagName("Type").item(i);
                    type = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                    
                    node = document.getElementsByTagName("Date").item(i);
                    date = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                    
                    ret[i + 2] = score + "|" + type + "|" + date;
                    
                } catch (Exception ex) {
                    ret[0] = "0";
                    ret[1] = ex.toString();
                    return ret;
                }
                
            }
           
        } // end if success != 1
        
    } catch (ParserConfigurationException parserError) {
            ret[0] = "0";
            ret[1] = "ParserConfigurationException";
        return ret;
    }
    
    // log anything not successfull
    if (!success.equals("1")) {
        
        SystemUtils.logError("Common_ghin:getRecentPostings() Failed Inquiry: user=" + user + ", club=" + club + ", success=" + success + ", Message=" + message);
    }
    
    return ret;
 }
 
 
 public static String[] getCurrentHdcp(String user, String club, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {

    String ret[] = new String[6];
        
    String request = "http://mt5493.ghinconnect.com/ghin/" +
        "iihbp58.cgi?" +
            "gm=" + getHdcpNum(user, con) + "&" +
            "club=" + clubNum + "&" +
            "assoc=" + clubAssocNum + "&" +
            "scores=0&" +
            "vasp=" + VASP;

    //request = "http://mt5493.ghinconnect.com/ghininq/iihbp58.cgi?club=995&assoc=56&scores=0&gm=3096020%20&vasp=t7pv5";
    out.println("<!--request=" + request + "-->");
    
    // the vars we are after
    String success = "";
    String message = "";
    String hdcpIndex = "";
    String hdcpDiff = "";
    String hdcpDate = "";
    String status = "";
   

    try {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;
        
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
        
    } catch (ParserConfigurationException parserError) {
            ret[0] = "0";
            ret[1] = "ParserConfigurationException";
        return ret;
    }
    
    if (!success.equals("1")) {
        
        SystemUtils.logError("Common_ghin:getCurrentHdcp() user=" + user + ", club=" + club + ", success=" + success + ", Message=" + message);
    } else {
        ret[0] = success;
        ret[1] = message;
        ret[2] = hdcpIndex;
        ret[3] = hdcpDiff;
        ret[4] = hdcpDate;
        ret[5] = status;
    }
    
    return ret;
 }
 
 
 public static String[] postScore(String user, String club, long date, int tee_id, int score, String type, int holes, String clubNum, String clubAssocNum, PrintWriter out, Connection con) {
    
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    String hdcp_num = "";
    String course = "";
    String tee_name = "";
    String mem_name = "";
    String ret[] = new String[2]; // array to return
    double rating = 0;
    int slope = 0;
    
    hdcp_num = getHdcpNum(user, con);
    
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
    
    String request = "http://mt5493.ghinconnect.com/ghin/" +
        "iihbp52.cgi?" +
            "playno=" + hdcp_num + "&" +
            "datein=" + date + "&" +
            "ratein=" + rating + "&" +
            "slpin=" + slope + "&" +
            "scrin=" + score + "&" +
            "typein=" + type + "&" +
            "holin=" + tmp_holes + "&" +
            "clubno=" + clubAssocNum + "" + clubNum + "&" +
            "postassoc=" + clubAssocNum + "&" +
            "postclub=" + clubNum + "&" +
            "course=" + ((course.equals("")) ? club : scrubCourseName(course)) + "&" +
            "vasp=" + VASP;
            //((course.equals("")) ? "" : "course=" + scrubCourseName(course) + "&") +
    
    // DEBUG
    //out.println("<!-- request=" + request + " -->");
    
    // the vars we are after
    int success = 0;
    String message = "";

    try {
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = getDocument(builder, request);
        Node node = null;
        try {
            
            node = document.getElementsByTagName("Success").item(0);
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = ex.toString();
            return ret;
        }
        
        try {
            success = Integer.parseInt(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = ex.toString();
            return ret;
        }

        node = document.getElementsByTagName("Message").item(0);

        try {
            message = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
        } catch (Exception ex) {
            ret[0] = "0";
            ret[1] = ex.toString();
            return ret;
        }
    
    } catch (ParserConfigurationException parserError) {
        ret[0] = "0";
        ret[1] = parserError.toString(); //parserError.printStackTrace();
    }
    
    if (success != 1) {
        
        SystemUtils.logError("Common_ghin:postScore() user=" + user + ", club=" + club + ", success=" + success + ", Message=" + message);
        ret[0] = "0";
        ret[1] = message;
    } else {
        ret[0] = "1";
        ret[1] = message;
    }
    
    return ret;
    
 }
 
 
 public static void getPostedScoresForClub(String club, String startDate, String endDate, String service, Connection con, PrintWriter out) {
    
    
    String clubNum = "";
    String clubAssocNum = "";
    
    int count1 = 0;
    int count2 = 0;
    
    try {
        
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM score_postings;");
        if (rs.next()) count1 = rs.getInt(1);
        
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

            String request = "http://mt5493.ghinconnect.com/ghininq/" + 
                "iihbp25.cgi?" + 
                    "clubno=" + clubAssocNum + "" + clubNum + "" + service + // 00 is all services
                    "&rpt1=2" +  // always 2
                    "&rpt1dtb=" + startDate + 
                    "&rpt1dte=" + endDate + 
                    "&status=A" + // active members
                    "&stype=*" + // grab home scores only - not sure this if working properly
                    "&vasp=" + VASP;

            
            if (out != null) out.println("<!-- request=" + request + " -->");
            //request = "http://mt5493.ghinconnect.com/ghininq/iihbp25.cgi?clubno=5610300&rpt1=2&rpt1dtb=20061001&rpt1dte=20061101&status=A&VASP=t7pv5&stype=H";

            // the vars we are after
            String success = "";
            String message = "";

            String ghin = "";   // Ghinnum
            String type = "";   // Stype
            String hdcp = "";   // HCP
            String date = "";   // Date
            String rating = ""; // Rating
            int score = 0;      // Score
            double dbl_hdcp = 0;
            int slope = 0;      // Score
            double dbl_rating = 0;

            String [] parts = new String[3];

            int i = 0;
            int tee_id = 0;
            
            try {

                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = getDocument(builder, request);
                Node node = null;

                try {

                    node = document.getElementsByTagName("Success").item(0);
                    success = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                    node = document.getElementsByTagName("Message").item(0);
                    message = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                } catch (Exception ex) {

                    SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") Success=" + success + ", Message=" + message + ", FatalErr=" + ex.toString() + ", Request=" + request);
                    return;
                }


                // if our request was successfull then lets continue processing
                if (success.equals("1")) {

                    try {

                        // keep looping as long as we keep finding Player elements
                        while (document.getElementsByTagName("Players").item(i) != null) {

                            node = document.getElementsByTagName("Stype").item(i);
                            type = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());
                            
                            // only process this entry if it's a home score (these should be getting filtered out by our request but doesn't seem to work)
//                            if ( type.equalsIgnoreCase("H") ) {  (WE ARE NOW PROCESSING ALL DOWNLOADED SCORES!)

                                node = document.getElementsByTagName("Ghinnum").item(i);
                                ghin = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                                node = document.getElementsByTagName("Score").item(i);
                                score = Integer.parseInt(SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData()));

                                node = document.getElementsByTagName("Date").item(i);
                                date = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                                node = document.getElementsByTagName("HCP").item(i);
                                hdcp = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                                node = document.getElementsByTagName("Slope").item(i);
                                slope = Integer.parseInt(SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData()));

                                node = document.getElementsByTagName("Rating").item(i);
                                rating = SystemUtils.scrubString(((CharacterData)node.getFirstChild()).getData());

                                dbl_hdcp = parseHandicapValue(hdcp);
                                
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
                                        SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") err=" + exc.toString() + ", hdcp=" + hdcp);
                                    }
                                    
                                }
*/
                                try {

                                    dbl_rating = Double.parseDouble(rating);
                                } catch (NumberFormatException exc) {

                                    SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") err=" + exc.toString() + ", rating=" + rating);
                                }

                                // try to determin this tee from the slope/rating posted for this score
                                tee_id = 0;
                                if (dbl_rating != 0 && slope != 0) {

                                    tee_id = getTeeIdFromSlopeRating(slope, dbl_rating, con);
                                    if (out != null && tee_id == 0) out.println("<br>Can't find tee for slope=" + slope + " and rating=" + dbl_rating);
                                }

                                //out.println("<br>" + date + ", " + parts[2] + "" + parts[0] + "" + parts[1] + ", " + ghin + ", " + score + ", " + type);

                                // break the date up into chunks
                                parts = date.split("/");
                                date = parts[2] + "" + parts[0] + "" + parts[1];
                                
                                try {
                                    
                                    // Attempt to insert each record we received, and any that we aleady have (based
                                    // on the unique index of hdcpNum, date, score, type) are ignored and not inserted
                                    /*
                                    PreparedStatement pstmt = con.prepareStatement(
                                        "INSERT IGNORE INTO score_postings " +
                                        "(hdcpNum, date, score, type, hdcpIndex, tee_id) VALUES (?, ?, ?, ?, ?, ?)");
                                    */
                                    PreparedStatement pstmt = con.prepareStatement(
                                        "INSERT INTO score_postings " +
                                        "(hdcpNum, date, score, type, hdcpIndex, tee_id) VALUES (?, ?, ?, ?, ?, ?) " +
                                        "ON DUPLICATE KEY UPDATE hdcpIndex = ?");

                                    pstmt.clearParameters();
                                    pstmt.setString(1, ghin);
                                    pstmt.setString(2, date);
                                    pstmt.setInt(3, score);
                                    pstmt.setString(4, type);
                                    pstmt.setDouble(5, dbl_hdcp);
                                    pstmt.setInt(6, tee_id);
                                    pstmt.setDouble(7, dbl_hdcp);
                                    pstmt.executeUpdate();
                                    pstmt.close();
                                    
                                } catch (SQLException exc) {

                                    SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") INSERT Error. i=" + i + ".  Error=" + exc.toString() + ", Score=" + score + ", Type=" + type);
                                }
//                            } // end type equals 'H'  (WE ARE NOW PROCESSING ALL DOWNLOADING SCORES!)
                        
                            i++;
                                
                        } // while there are more player nodes
                    
                    } catch (Exception ex) { 

                        // an error occured while parsing the xml document we retrieved.
                        SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") Error parsing XML file.  i=" + i + ".  Error=" + ex.toString() + ".  URL=" + request);
                    }

                } else {

                    // success did not equal 1
                    SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") Success=" + success + ", Message=" + message + ", clubNum=" + clubNum + ", clubAssocNum=" + clubAssocNum);
                }

            } catch (ParserConfigurationException parserError) {

                SystemUtils.logError("Common_ghin:getPostedScoresForClub() ParserConfigurationException: " + parserError.toString());
                return;
            }
    
        } // end loop of unique club num/assoc num combinations
        
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT COUNT(*) FROM score_postings;");
        if (rs.next()) count2 = rs.getInt(1);
        stmt.close();
        
    } catch (Exception ex) { 

        SystemUtils.logError("Common_ghin:getPostedScoresForClub(" + club + ") Error: " + ex.toString());
    }
    
    //if (count2 - count1 > 0) SystemUtils.logError("Finished Common_ghin.getPostedScoresForClub(" + club + "): Downloaded " + (count2 - count1) + " postings for " + startDate + " - " + endDate);
    if (out != null) out.println("<p>Downloaded " + (count2 - count1) + " postings for " + startDate + " - " + endDate + "</p>");
 }
 
 
 public static void getPostedScores(PrintWriter out) {
     
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

    // only run on server #1 OR if called with a valid out object.  TimerGHIN will call this method will a null out obj.  Support_sync will call with valid out
    if (Common_Server.SERVER_ID == SystemUtils.TIMER_SERVER || out != null) {
        
        long startTime = System.currentTimeMillis();
        SystemUtils.logError("Starting Common_ghin.getPostedScores()");

        try {
            
            con = dbConn.Connect(rev);
        } catch (Exception e1) {

            SystemUtils.logError("Common_ghin.getPostedScores: Error connecting to v5 db. " + e1.getMessage());                                       // log it
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

                                startDate = null;
                                endDate = null;
                                altEndDate = null;
                                altStartDate = null;
                                int tmp_sdate = 0;
                                int tmp_edate = 0;
                                int tmp_adate = 0;
                                int tmp_date = 0;
                                int diff = 0;

                                // get the most recent date for which we have downloaded posted scores
                                // notes: 
                                //   startDate      is most recent date date for which we have downloaded posted scores, null if none d/l yet
                                //   endDate        will always be returned as todays date
                                //   altEndDate     will be used if endDate minus startDate is greater than 30 days (ghin only allows 30days to be retrieved per request)
                                //   altStartDate   will be returned, used if startDate is null, we assume this is first run and this date will be today minus 30 days
                                rs2 = stmt2.executeQuery("" +
                                        "SELECT " +
                                            "DATE_FORMAT(MAX(date), '%Y%m%d') AS startDate, " +
                                            "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 1 DAY), '%Y%m%d') AS endDate, " +
                                            "DATE_FORMAT(DATE_ADD(MAX(date), INTERVAL + 30 DAY), '%Y%m%d') AS altEndDate, " +
                                            "DATE_FORMAT(DATE_ADD(now(), INTERVAL - 28 DAY), '%Y%m%d') AS altStartDate, " +
                                            "DATEDIFF(now(), MAX(date)) AS diff " +
                                        "FROM score_postings;");

                                if (rs2.next()) {

                                    startDate = rs2.getString("startDate");
                                    endDate = rs2.getString("endDate");
                                    altEndDate = rs2.getString("altEndDate");
                                    altStartDate = rs2.getString("altStartDate");
                                    diff = rs2.getInt("diff");
                                    //diff = (rs2.getInt("diff") != null) ? rs2.getInt("diff") : 0;

                                    //if (diff > 30) {

                                        //endDate = altEndDate;

                                    //} else if (startDate == null) {

                                        startDate = altStartDate;

                                    //}
                                    //out.println("<p>" + startDate + ", " + endDate + ", " + altStartDate + ", " + altEndDate + ", " + diff + "</p>");

                                    // call routine that actually performs the data retrieval for a given club
                                    getPostedScoresForClub(club, startDate, endDate, "00", con2, out);

                                    // update the hdcp values in member2b for this club and update teecurr2 & event signups
                                    updateMemberHdcps(club, con2);

                                }

                            } // end if found club using ghin and in season

                            con2.close();

                        } else {

                            SystemUtils.logError("Common_ghin.getPostedScores: Can not connect to " + club + " db.");
                        }
                    
                    } // end if not demo sites
                    
                } // end loop of all clubs in v5.clubs
                
                stmt.close();

            }
            catch (Exception e2) {

                SystemUtils.logError("Common_ghin.getPostedScores: Error processing. Club=" + club + ".  Exception was " + e2.getMessage());
            }

            try {
                con.close();
            }
            catch (Exception ignored) { }
            
        } // end if con != null
            
        SystemUtils.logError("Finished Common_ghin.getPostedScores() run time:" + (System.currentTimeMillis() - startTime) + "ms (" + ((System.currentTimeMillis() - startTime) / 1000 / 60) + "min) using dates " + startDate + " -> " + endDate);
        
    } // end if master server

    TimerGHIN g_timer = new TimerGHIN();            // reset timer to update daily

 }
 
 
 private static String scrubGhinNum(String ghin) {
    
     ghin = ghin.replace("-", "");
     return ghin;
 }
 
 
 private static String scrubCourseName(String course) {
    
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
        
        SystemUtils.logError("Common_ghin:getTeeIdFromSlopeRating - Error: " + exp.toString());
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

    // UPDATE MEMBER2B TABLE WITH MOST RECENT HANDICAP DATA
    if (club.equals("cherryhills")) {

        try {

            // mens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'White') AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.hdcp_assoc_num_id = 1 AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for cherryhills (mens)  Error: " + exp.toString());
        }

        try {

            // womens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_name = 'Red - Women') AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.hdcp_assoc_num_id = 2 AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for cherryhills (womens)  Error: " + exp.toString());
        }

    } else if (club.equals("pelicansnest")) {
     
        try {

            // mens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 4) AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.gender = 'M' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for pelicansnest (mens)  Error: " + exp.toString());
        }

        try {

            // womens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 7) AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.gender = 'F' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " +
                  //"sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum AND " +
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for pelicansnest (womens)  Error: " + exp.toString());
        }
        
    } else if (club.equals("mnvalleycc")) {
    
        
        try {

            // mens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 2) AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.gender = 'M' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " + // REPLACE(m.ghin, '-', '') = sp.hdcpNum
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for mnvalleycc (mens)  Error: " + exp.toString());
        }

        try {

            // womens
            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "(SELECT tee_slope18 FROM tees WHERE tee_id = 7) AS s, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = ROUND((sp.hdcpIndex * s.tee_slope18) / 113) " + 
                "WHERE m.gender = 'F' AND t.date = sp.date AND " +
                    "sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED) AND " + // REPLACE(m.ghin, '-', '') = sp.hdcpNum
                    " ABS(sp.hdcpIndex) <> 99;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b for mnvalleycc (womens)  Error: " + exp.toString());
        }
        
    } else {
        
        // ALL OTHER CLUBS
        try {

            Statement stmt = con.createStatement();
            stmt.executeUpdate("" +
                "UPDATE " + 
                    "(SELECT MAX(date) AS date, hdcpNum FROM score_postings GROUP BY hdcpNum) AS t, " + 
                    "member2b m, score_postings sp " + 
                "SET m.g_hancap = sp.hdcpIndex " + 
                "WHERE t.date = sp.date AND sp.hdcpNum = t.hdcpNum AND CAST(sp.hdcpNum AS UNSIGNED) = CAST(REPLACE(m.ghin, '-', '') AS UNSIGNED);"); // AND REPLACE(m.ghin, '-', '') = sp.hdcpNum
                //"WHERE t.date = sp.date AND sp.hdcpNum = t.hdcpNum AND REPLACE(m.ghin, '-', '') = sp.hdcpNum;");
            stmt.close();

        } catch (Exception exp) { 

            SystemUtils.logError("Common_ghin: Updating member2b - Club: " + club + "  Error: " + exp.toString());
        }

    }
    
    // UPDATE TEECURR2 TABLE WITH MOST RECENT HANDICAP DATA
    try {
    
        Statement stmt = con.createStatement();
        for (int x=1;x<=5;x++) {
        
            stmt.executeUpdate("" + 
                "UPDATE teecurr2 t, member2b m " +
                "SET " +
                    "t.hndcp" + x + " = (IF(t.username" + x + " = m.username,m.g_hancap,t.hndcp" + x + ")) " + 
                "WHERE " +
                    "t.username" + x + " = m.username AND " +
                    "m.g_hancap <> -99 AND m.g_hancap <> 99");
        }
        stmt.close();
    
    } catch (Exception exp) { 
        
        SystemUtils.logError("Common_ghin: Updating teecurr2 - Club: " + club + "  Error: " + exp.toString());
    }
    
    
    // UPDATE EVNTSUP2B TABLE WITH MOST RECENT HANDICAP DATA
    try {
    
        Statement stmt = con.createStatement();
        for (int x=1;x<=5;x++) {
            
            stmt.executeUpdate("" + 
                "UPDATE evntsup2b e, member2b m " +
                "SET " +
                    "e.hndcp" + x + " = (IF(e.username" + x + " = m.username,m.g_hancap,e.hndcp" + x + ")) " + 
                "WHERE " +
                    "e.username" + x + " = m.username AND " +
                    "m.g_hancap <> -99 AND m.g_hancap <> 99");
        }
        stmt.close();
    
    } catch (Exception exp) { 
        
        SystemUtils.logError("Common_ghin: Updating evntsup2b - Club: " + club + "  Error: " + exp.toString());
    }
    
 } // end updateMemberHdcps
 
 
 
 public static double parseHandicapValue(String hdcp) {

    double dbl_hdcp;

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
            
            if (!hdcp.startsWith("+")) hdcp = "-" + hdcp; // incoming indexs are assumed to be negative unless specified as positive
            dbl_hdcp = Double.parseDouble(hdcp);
            
        } catch (NumberFormatException exc) {

            dbl_hdcp = -99;
            SystemUtils.logError("Common_ghin:parseHandicapValue() err=" + exc.toString() + ", hdcp=" + hdcp);
        }

    }
     
    return dbl_hdcp;
    
 } // end if parseHandicapValue
 
 
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