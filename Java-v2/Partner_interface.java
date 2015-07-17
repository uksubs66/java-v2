/***************************************************************************************
 *   Proshop_interface:  This servlet will provide a RESTful public interface allowing
 *                       third-party outside access to a club's tee sheet.
 *
 *
 *   called by:  Third Parties
 *
 *   created: 09/28/2012
 *
 *
 *   last updated:
 *
 *       12/12/2012  Added debug output to file for the checkTeeTime method
 *       11/17/2012  Various fixes for GolfSwitch
 *       10/22/2012  Implement new XML handler
 *
 *
 *
 *
 ***************************************************************************************
 */


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.verifySlot;

import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

//import javax.xml.bind.JAXBContext;
//import javax.xml.bind.JAXBException;
//import javax.xml.bind.Marshaller;


import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
//import javax.xml.stream.XMLStreamException;

//import java.io.StringWriter;
import java.io.InputStream;
import java.io.ByteArrayInputStream;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

import com.google.gson.*;

/**
 *
 * @author sindep
 */
public class Partner_interface extends HttpServlet {

    final static String rev = "v5";


    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


        final long benchmark_start = System.currentTimeMillis();
        
        //resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        
        // get the passed parameters in to local variables
        String clubid = req.getParameter("club_id") == null ? "" : req.getParameter("club_id");
        String auth_user = req.getParameter("auth_user") == null ? "" : req.getParameter("auth_user");
        String auth_pass = req.getParameter("auth_pass") == null ? "" : req.getParameter("auth_pass");
        String tsp_code = req.getParameter("tsp_code") == null ? "" : req.getParameter("tsp_code");     // reseller code
        String type = req.getParameter("type") == null ? "" : req.getParameter("type");                 // action
        String respType = req.getParameter("respType") == null ? "" : req.getParameter("respType");     // response type
        String transaction = req.getParameter("transaction") == null ? "" : req.getParameter("transaction");     // transaction id

        int partner_id = 0;
        
        String club = "";
        String result = "";             // the json string returned by the action functions
        String configured_user = "";
        String configured_pass = "";

        // get the users ip address - our loadbalancer populates the x-forward-for header
        // so if that's there, use it - otherwise grab the normal one
        boolean ip_allowed = false; // default
        String remote_ip = req.getHeader("x-forwarded-for");
        if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

        // verify user credentials (user access & club is configured for access)
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
    
        try {

            con = Connect.getCon(rev);

            club = Utilities.getClubName(Integer.valueOf(clubid), con);

            if (club != null && !club.equals("")) {
            
                pstmt = con.prepareStatement (
                            "SELECT t1.id, t2.auth_user, t2.auth_pass " +
                            "FROM v5.teesheet_partners t1 " +
                            "LEFT OUTER JOIN " + club + ".teesheet_partner_config t2 ON t1.id = t2.partner_id " +
                            "WHERE t1.enabled = 1 AND t2.enabled = 1 AND t1.reseller_code = ?");
                
                //          "LEFT OUTER JOIN v5.tsp_ip_address t3 ON t3.partner_id = t1.partner_id " +

                pstmt.clearParameters();
                pstmt.setString(1, tsp_code);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    partner_id = rs.getInt("id");
                    configured_user = rs.getString("auth_user");
                    configured_pass = rs.getString("auth_pass");
                }


                // only do IP verification on production servers
                if (ProcessConstants.SERVER_ID != 4) {

                    // now look up the allowable IP
                    pstmt = con.prepareStatement (
                                "SELECT id " +
                                "FROM v5.tsp_ip_addresses " +
                                "WHERE ip = ?");

                    pstmt.clearParameters();
                    pstmt.setString(1, remote_ip);
                    rs = pstmt.executeQuery();

                    if (rs.next()) ip_allowed = true;

                } else {

                    // on dev server so allow it regardless
                    ip_allowed = true;

                }
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.processRequest() Error loading initial data. club_id=" + clubid + ", club=" + club + ", remote_ip=" + remote_ip + ", err=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

            try { con.close(); }
            catch (Exception ignore) {}

        }


        // only continue if club, partner and authentication are all valid
        if (club == null || club.equals("")) {

            // return error message
            result = errorMessage("BAD_CLUB_ID","The club requested was not found. " + clubid);

        } else if (partner_id == 0) {

            // return error message
            result = errorMessage("BAD_PARTNER_CODE","Partner code not found or not authorized for this club.");

        } else if (!auth_user.equals(configured_user) || !auth_pass.equals(configured_pass) ) {
            
            // auth_user, auth_pass check
            result = errorMessage("NOT_AUTHENTICATED","Authentication failed.");
            
        } else if (!ip_allowed) {

            // return error message
            result = errorMessage("UNAUTHORIZED_IP","Only authorized IP addresses can connect to the production enviroment.");

        } else {


            //
            // PROCESS REQUEST
            //

            try {

                // reset connection to the correct club
                con = Connect.getCon(club);

                if (type.equalsIgnoreCase("BOOKING") && req.getMethod().equals("POST")) {

                    // process a tee time booking request
                    result = doBookingRequest(req, club, partner_id, transaction, benchmark_start, con);

                } else if (type.equalsIgnoreCase("AVAILABILITY") && req.getMethod().equals("GET")) {

                    // process a tee time availablity request
                    result = doAvailabilityRequest(req, club, partner_id, transaction, benchmark_start, con);

                } else if (type.equalsIgnoreCase("CANCEL") && req.getMethod().equals("POST")) {

                    // process a tee time cancel request
                    result = doCancelRequest(req, club, partner_id, transaction, benchmark_start, con);

                } else if (type.equalsIgnoreCase("COURSELIST") && req.getMethod().equals("GET")) {

                    // process a course list request
                    result = doCourseRequest(req, club, partner_id, transaction, benchmark_start, con);

                } else {

                    // return error response
                    result = errorMessage("INVALID_ACTION","Nothing to do.");

                }

            } catch (Exception ignore) {

            } finally {

                try { con.close(); }
                catch (Exception ignore) {}

            }

        }


        //
        // Output the result default is json - convert to xml if requested
        //
        if (respType.equalsIgnoreCase("XML")) {
            
            // Convert json to xml

            // Use staxon to convert the json string to XML
            // (it should be possible to stream this directly from gson without converting to string first,
            // but gson and staxon documentation was not clear)
            InputStream input = new ByteArrayInputStream(result.getBytes());
            
            //OutputStream output = out;
            /*
             * If the <code>multiplePI</code> property is
             * set to <code>true</code>, the StAXON reader will generate
             * <code>&lt;xml-multiple&gt;</code> processing instructions
             * which would be copied to the XML output.
             * These can be used by StAXON when converting back to JSON
             * to trigger array starts.
             * Set to <code>false</code> if you don't need to go back to JSON.
             */
            JsonXMLConfig config = new JsonXMLConfigBuilder().multiplePI(false).build();
            try {
                    /*
                     * Create reader (JSON).
                     */
                    XMLEventReader reader = new JsonXMLInputFactory(config).createXMLEventReader(input);

                    /*
                     * Create writer (XML).
                     */
                    XMLEventWriter writer = XMLOutputFactory.newInstance().createXMLEventWriter(out);
                    writer = new PrettyXMLEventWriter(writer); // format output

                    /*
                     * Copy events from reader to writer.
                     */
                    writer.add(reader);

                    /*
                     * Close reader/writer.
                     */
                    reader.close();
                    writer.close();

            } catch (Exception exc) {

                // Error
                out.println("<p>Error: " + exc.toString() + "</p>");

            } finally {

                /*
                 * As per StAX specification, XMLEventReader/Writer.close() doesn't close
                 * the underlying stream.
                 */
                input.close();

            }

        } else {
            
            out.print(result);
            
        }
        
        out.close();

    }


    private String doCourseRequest(HttpServletRequest req, String club, int partner_id, String transaction, long benchmark_start, Connection con) {
        
        
        Statement stmt = null;
        ResultSet rs = null;
        
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        

        //
        // BUILD RESPONSE
        //
        List<Object> courseList = new ArrayList<Object>();
        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesResp", response_map);

        response_map.put("transactionId", transaction);
        response_map.put("respDateTime", sdfDate.format(now));
        response_map.put("respEpochTime", System.currentTimeMillis());
        response_map.put("serverId", ProcessConstants.SERVER_ID);
        response_map.put("courseData", courseList); // put the array list in now and we'll populate it below

        // load all the courses and add them to the map
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT clubparm_id, courseName, fives " +
                    "FROM clubparm2 " +
                    "ORDER BY sort_by");

            while ( rs.next() ) {

                Map<String, String> course_map = new HashMap<String, String>();

                course_map.put("courseId", rs.getString("clubparm_id"));
                course_map.put("course", rs.getString("courseName"));
                course_map.put("allowFives", String.valueOf(rs.getInt("fives")));

                courseList.add(course_map);
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCourseRequest() Error loading courses. club=" + club + ", err=" + exc.getMessage());
            return errorMessage("ERROR", "An error occured loading the course data.");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");

        Gson gson_obj = new Gson();

        return gson_obj.toJson(container_map);
        
    }



    private String doCancelRequest(HttpServletRequest req, String club, int partner_id, String transaction, long benchmark_start, Connection con) {


        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        //
        // GET ALL PARAMETERS OF THE REQUEST
        //
        String conf_code = req.getParameter("conf_code") == null ? "" : req.getParameter("conf_code");
        String user = req.getParameter("session_user") == null ? "" : req.getParameter("session_user");
        String courseid = req.getParameter("course_id") == null ? "" : req.getParameter("course_id");
        String date = req.getParameter("date") == null ? "" : req.getParameter("date");
        String courseName = "";

        // abort right away if session user is invalid
        if (user.equals("") || user.length() > 15 || !user.startsWith(req.getParameter("tsp_code"))) {

            return errorMessage("INVALID_SESSION_USER", "You must provide a unique user name for this session. Valid user names are up to 15 characters in length and should start with your partner code. Using the same user name for multiple concurrent sessions could cause consistency problems.");
        }
        
        int book_date = 0;
        int course_id = 0;

        try {
            book_date = Integer.parseInt(date);
            course_id = Integer.parseInt(courseid);
        } catch (Exception ignore) {}


        // validate the date
        if (book_date == 0) {

            return errorMessage("BAD_DATE", "The date provided did not resolve. Make sure the date is an Integer (YYYYMMDD).");
        }

        // validate the course id passed in was numeric
        if (course_id == 0) {

            return errorMessage("BAD_COURSE_ID", "The course ID did not resolve.");
        
        } else {

            courseName = Utilities.getCourseName(course_id, con);

        }


        String guest_type = "";
        String partner_name = "";

        int teecurr_id = 0;
        int players = 0;
        int max_days_in_advance = 0;
        int cancelled_id = 0;
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT guest_type, max_days_in_advance, t2.name AS partner_name " +
                    "FROM teesheet_partner_config t1 " +
                    "LEFT OUTER JOIN v5.teesheet_partners t2 ON t1.partner_id = t2.id " +
                    "WHERE t1.partner_id = " + partner_id);

            if ( rs.next() ) {

                partner_name = rs.getString("partner_name");
                guest_type = rs.getString("guest_type");
                max_days_in_advance = rs.getInt("max_days_in_advance");
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCancelRequest() Error loading allowed guest_type. club=" + club + ", partner_id=" + partner_id + ", err=" + exc.getMessage());
            return errorMessage("CONFIG", exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }
        
/*
        // if we want to we could verify the data is within range but why??
        int max_date = (int)Utilities.getDate(con, max_days_in_advance);
        if (book_date > max_date) {

            return errorMessage("BAD_DATE", "The date specified is outside the allowable range. Tee times can only be accessed " + max_days_in_advance + " days in advance.");
        }
*/

        int orig_conf_id = 0;

        try {

            pstmt = con.prepareStatement (
                        "SELECT * " +
                        "FROM v5.tsp_conf_nums " +
                        "WHERE conf_code = ?");

            pstmt.clearParameters();
            pstmt.setString(1, conf_code);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                orig_conf_id = rs.getInt("id");
                teecurr_id = rs.getInt("teecurr_id");
                players = rs.getInt("players");
                cancelled_id = rs.getInt("cancelled_id");
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCancelRequest() Error loading tee time data. club=" + club + ", conf_code=" + conf_code + ", err=" + exc.getMessage());
            return errorMessage("ERROR", "An error was encountered while locating the supplied confirmation code.");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        // abort if we couldn't find a record
        if (teecurr_id == 0) {

            Utilities.logError("Partner_interface.doCancelRequest() The confirmation code supplied was not found. club=" + club + ", conf_code=" + conf_code);
            return errorMessage("NOT_FOUND", "The confirmation code supplied was not found.");
        }

        // abort if its already been cancelled
        if (cancelled_id != 0) {

            Utilities.logError("Partner_interface.doCancelRequest() The confirmation code supplied was already cancelled. club=" + club + ", conf_code=" + conf_code);
            return errorMessage("ALREADY_CANCELLED", "The confirmation code supplied was already cancelled.");
        }

        



        // load up the tee time and check the names and find some to remove
        String player1 = "";
        String player2 = "";
        String player3 = "";
        String player4 = "";
        String player5 = "";
        String player1_tmode = "";
        String player2_tmode = "";
        String player3_tmode = "";
        String player4_tmode = "";
        String player5_tmode = "";
        String player1_orig = "";
        String player2_orig = "";
        String player3_orig = "";
        String player4_orig = "";
        String player5_orig = "";
        String day = "";        // for updating tee time history
        int fb = 0;             // for updating tee time history
        int time = 0;

        boolean allow5somes = false;
        boolean found = false;
        
        try {

            pstmt = con.prepareStatement (
                        "SELECT * " +
                        "FROM teecurr2 " +
                        "WHERE teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, teecurr_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                
                player1 = rs.getString("player1");
                player2 = rs.getString("player2");
                player3 = rs.getString("player3");
                player4 = rs.getString("player4");
                player5 = rs.getString("player5");
                player1_tmode = rs.getString("p1cw");
                player2_tmode = rs.getString("p2cw");
                player3_tmode = rs.getString("p3cw");
                player4_tmode = rs.getString("p4cw");
                player5_tmode = rs.getString("p5cw");
                day = rs.getString("day");
                fb = rs.getInt("fb");
                time = rs.getInt("time");

                found = true;

            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCancelRequest() Error loading tee time data. club=" + club + ", err=" + exc.getMessage());
            return errorMessage("ERROR", "An error was encountered loading the requested tee time data.");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        // abort if we didn't find a matching tee time
        if (found != true) {

            Utilities.logError("Partner_interface.doCancelRequest() WARNING! teecurr_id not found! club=" + club + ", teecurr_id=" + teecurr_id);
            return errorMessage("NOT_FOUND", "The tee time referenced by the confirmation code no longer exists. Please contact the golf club.");

        }


        // now remove the specified number of members
        int removed = 0;

        if (player5.startsWith(guest_type)) {

            player5 = "";
            player5_tmode = "";
            removed++;
        }

        if (removed < players && player4.startsWith(guest_type)) {

            player4 = "";
            player4_tmode = "";
            removed++;
        }

        if (removed < players && player3.startsWith(guest_type)) {

            player3 = "";
            player3_tmode = "";
            removed++;
        }

        if (removed < players && player2.startsWith(guest_type)) {

            player2 = "";
            player2_tmode = "";
            removed++;
        }

        if (removed < players && player1.startsWith(guest_type)) {

            player1 = "";
            player1_tmode = "";
            removed++;
        }


        // player1 must either be enpty or have the configured guest in it in order to continue
        if (removed != players) {

            // this time is now booked with a member in player1 - no not allow access to this tee time!
            return errorMessage("ERROR", "The tee time has changed significantly since it was booked. Please contact the golf club.");
        }



        // we successfully removed the correct # of players
        // however, it this tee had two seperate bookings then
        // the grouping of the names may have been disrupted


        // update the tee time (clear the in_use_by so that if someone has this tee time in use they are kicked out and have to reload it)
        try {

            pstmt = con.prepareStatement(
                    "UPDATE teecurr2 " +
                    "SET " +
                        "last_mod_date = now(), " +
                        "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
                        "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, in_use_by = '' " +
                    "WHERE teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setString(1, player1);
            pstmt.setString(2, player2);
            pstmt.setString(3, player3);
            pstmt.setString(4, player4);
            pstmt.setString(5, player5);
            pstmt.setString(6, player1_tmode);
            pstmt.setString(7, player2_tmode);
            pstmt.setString(8, player3_tmode);
            pstmt.setString(9, player4_tmode);
            pstmt.setString(10, player5_tmode);
            pstmt.setInt(11, teecurr_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCancelRequest() Error saving changes to database! club=" + club + ", teecurr_id=" + teecurr_id + ", err=" + exc.toString());
            return errorMessage("ERROR", "Error saving tee time. The tee has not been canceled!");

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
        
        
        // add a tee time history entry
        SystemUtils.updateHist(book_date, day, time, fb, courseName, player1, player2, player3, player4, player5, user, partner_name, 0, con);

        
        // generate a new confirmation code for this
        String new_conf_code = Utilities.generateConfCode(partner_id, teecurr_id, club);


        // save the confirmation data
        int cancel_conf_id = 0;

        try {

            // get the id of the one we just created
            pstmt = con.prepareStatement (
                        "SELECT id " +
                        "FROM v5.tsp_conf_nums " +
                        "WHERE conf_code = ?");

            pstmt.clearParameters();
            pstmt.setString(1, new_conf_code);

            rs = pstmt.executeQuery();

            if (rs.next()) cancel_conf_id = rs.getInt(1);

            // update the original conf record to point to this new cancel record
            pstmt = con.prepareStatement(
                    "UPDATE v5.tsp_conf_nums " +
                    "SET cancelled_id = ? " +
                    "WHERE id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, cancel_conf_id);
            pstmt.setInt(2, orig_conf_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doCancelRequest() BUG! Error getting/updating the cancelled_id to v5.tsp_conf_nums club=" + club + ", orig_conf_id=" + orig_conf_id + ", cancel_conf_id=" + cancel_conf_id + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        
        // build the response

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesResp", response_map);

        response_map.put("teeTimeId", teecurr_id);
        response_map.put("confirmationCode", new_conf_code);
        response_map.put("result", "SUCCESS");
        response_map.put("transactionId", transaction);
        response_map.put("respDateTime", sdfDate.format(now));
        response_map.put("respEpochTime", System.currentTimeMillis());
        response_map.put("serverId", ProcessConstants.SERVER_ID);
        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");

        Gson gson_obj = new Gson();

        // return the response in json format
        return gson_obj.toJson(container_map);

    }


    private String doBookingRequest(HttpServletRequest req, String club, int partner_id, String transaction, long benchmark_start, Connection con) {


        //
        // GET ALL PARAMETERS OF THE REQUEST
        //
        String date = req.getParameter("date") == null ? "" : req.getParameter("date");
        String time = req.getParameter("time") == null ? "" : req.getParameter("time");
        String tid = req.getParameter("id") == null ? "" : req.getParameter("id");
        String user = req.getParameter("session_user") == null ? "" : req.getParameter("session_user");

        // abort right away if session user is invalid
        if (user.equals("") || user.length() > 15 || !user.startsWith(req.getParameter("tsp_code"))) {

            return errorMessage("INVALID_SESSION_USER", "You must provide a unique user name for this session. Valid user names are up to 15 characters in length and should start with your partner code. Using the same user name for multiple concurrent sessions could cause consistency problems.");
        }

        // abort if course id passed is invalid
        String courseid = req.getParameter("course_id") == null ? "" : req.getParameter("course_id");
        String courseName = Utilities.getCourseName(Integer.valueOf(courseid), con);
        if (courseName == null) {

            return errorMessage("COURSE_NOT_FOUND", "The course you requested was not found.");
        }

        String player_count = req.getParameter("player_count") == null ? "" : req.getParameter("player_count");

        // these are all the optional data bits that may or may not be included with the request
        String first_name = req.getParameter("first_name") == null ? "" : req.getParameter("first_name");
        String last_name = req.getParameter("last_name") == null ? "" : req.getParameter("last_name");
        String address1 = req.getParameter("address1") == null ? "" : req.getParameter("address1");
        String address2 = req.getParameter("address2") == null ? "" : req.getParameter("address2");
        String city = req.getParameter("city") == null ? "" : req.getParameter("city");
        String state = req.getParameter("state") == null ? "" : req.getParameter("state");
        String country = req.getParameter("country") == null ? "" : req.getParameter("country");
        String postal_code = req.getParameter("postal_code") == null ? "" : req.getParameter("postal_code");
        String phone = req.getParameter("phone") == null ? "" : req.getParameter("phone");
        String email = req.getParameter("email") == null ? "" : req.getParameter("email");
        String membership_num = req.getParameter("membership_num") == null ? "" : req.getParameter("membership_num");
        String per_player_fee = req.getParameter("per_player_fee") == null ? "" : req.getParameter("per_player_fee");
        String notes = req.getParameter("notes") == null ? "" : req.getParameter("notes").trim();

        
        
        //
        // LOAD INTERFACE CONFIG DATA
        //
        String guest_type = "";
        String guest_tmode = "";
        String availability_flags = "";
        String partner_name = "";
        int max_allowed_guests = 0;
        int max_days_in_advance = 0;

        Statement stmt = null;
        ResultSet rs = null;
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT guest_type, guest_tmode, max_allowed_guests, availability_flags, max_days_in_advance, t2.name AS partner_name " +
                    "FROM teesheet_partner_config t1 " +
                    "LEFT OUTER JOIN v5.teesheet_partners t2 ON t1.partner_id = t2.id " +
                    "WHERE t1.partner_id = " + partner_id);

            if ( rs.next() ) {

                partner_name = rs.getString("partner_name");
                guest_type = rs.getString("guest_type");
                guest_tmode = rs.getString("guest_tmode");
                max_allowed_guests = rs.getInt("max_allowed_guests");
                availability_flags = rs.getString("availability_flags");
                max_days_in_advance = rs.getInt("max_days_in_advance");
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doBookingRequest() Error loading allowed guest_type. club=" + club + ", partner_id=" + partner_id + ", err=" + exc.getMessage());
            return errorMessage("CONFIG", exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }


        //
        // VERIFY REQUEST PARAMETERS (only the date, time, tid, course, player_count, first_name, last_name are required)
        //
        int book_date = 0;
        int book_time = 0;
        int teecurr_id = 0;
        int players = 0;

        try {
            book_date = Integer.parseInt(date);
            book_time = Integer.parseInt(time);
            teecurr_id = Integer.parseInt(tid);
            players = Integer.parseInt(player_count);
        } catch (Exception ignore) {}



        // validate the date
        if (players == 0 || players > max_allowed_guests) {

            return errorMessage("INVALID_PLAYER_COUNT", "The number of players requested is invalid. Valid numbers for this course are 1 - " + max_allowed_guests + ".");
        }

        // validate the date
        if (book_date == 0) {

            return errorMessage("BAD_DATE", "The date provided did not resolve. Make sure the date is an Integer (YYYYMMDD).");
        }
        
        // validate the time
        if (book_time == 0 || book_time < 0 || book_time > 2359) {

            return errorMessage("BAD_TIME", "The time provided did not resolve. Make sure the time is an Integer in military format (HHMM).");
        }

        // check that the date is not too far in advance based upon the clubs settings
        int max_date = (int)Utilities.getDate(con, max_days_in_advance);
        if (book_date > max_date) {

            return errorMessage("BAD_DATE", "The date specified is outside the allowable range. Tee times can only be booked " + max_days_in_advance + " days in advance.");
        }

        // make sure we have a full name of the person booking the request
        if (first_name.equals("") || last_name.equals("")) {

            return errorMessage("MISSING_NAME", "Both the first name and last name are required to book a tee time.");
        }

        // validate the tid
        if (teecurr_id == 0) {

            return errorMessage("BAD_TEETIME_ID", "The time time ID provided did not resolve. The ID is an interger and is required.");
        }

        // ensure the number of players being requested is okay
        


        //
        // do we want to call checkTeeTime again on this to ensure that it's not restricted?
        //
        

        // first lets lock this tee time

        PreparedStatement pstmt = null;

        int count = 0;

        try {
            pstmt = con.prepareStatement(
                    "UPDATE teecurr2 " +
                    "SET in_use = 1, in_use_by = ? " +
                    "WHERE in_use = 0 AND teecurr_id = ? AND date = ? AND time = ? AND " +
                    "event = '' AND lottery = '' AND blocker = ''");
            // add a check for player = '' || player1 LIKE guest_type to the where clause

            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, teecurr_id);
            pstmt.setInt(3, book_date);
            pstmt.setInt(4, book_time);

            count = pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doBookingRequest() Error setting tee time in use. club=" + club + ", teecurr_id=" + teecurr_id + ", err=" + exc.getMessage());
            return errorMessage("Locked", "The tee time you requested is unavailable.");

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        // return if tee time is not locked
        if (count == 0) {

            Utilities.logError("Partner_interface: Requested tee time is in use. club=" + club + ", tid=" + tid + ", date=" + date + ", time=" + time);
            return errorMessage("IN USE", "The tee time you requested is either now in use or the data and time passed did not match that of the tee time ID.");

        }


        //
        //  If we are still here then we now own the tee time - if we exit we must unlock the tee time!
        //



        //
        // PROCESS REQUEST
        //


        // first load up the tee time and see if we can fit the players into it
        String oldPlayer1 = "";
        String oldPlayer2 = "";
        String oldPlayer3 = "";
        String oldPlayer4 = "";
        String oldPlayer5 = "";
        String oldPlayer1_tmode = "";
        String oldPlayer2_tmode = "";
        String oldPlayer3_tmode = "";
        String oldPlayer4_tmode = "";
        String oldPlayer5_tmode = "";
        String oldPlayer1_orig = "";
        String oldPlayer2_orig = "";
        String oldPlayer3_orig = "";
        String oldPlayer4_orig = "";
        String oldPlayer5_orig = "";
        String oldNotes = "";
        String orig_by = "";
        String day = "";        // for updating tee time history
        int fb = 0;             // for updating tee time history

        boolean allow5somes = false;

        try {

            pstmt = con.prepareStatement (
                        "SELECT * " +
                        "FROM teecurr2 " +
                        "WHERE teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, teecurr_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {

                oldPlayer1 = rs.getString("player1");
                oldPlayer2 = rs.getString("player2");
                oldPlayer3 = rs.getString("player3");
                oldPlayer4 = rs.getString("player4");
                oldPlayer5 = rs.getString("player5");
                oldPlayer1_tmode = rs.getString("p1cw");
                oldPlayer2_tmode = rs.getString("p2cw");
                oldPlayer3_tmode = rs.getString("p3cw");
                oldPlayer4_tmode = rs.getString("p4cw");
                oldPlayer5_tmode = rs.getString("p5cw");
                oldNotes = rs.getString("notes");
                orig_by = rs.getString("orig_by");
                day = rs.getString("day");
                fb = rs.getInt("fb");

            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doBookingRequest() Error loading tee time data. club=" + club + ", err=" + exc.getMessage());
            unlockTeeTime(teecurr_id, user, con);
            return errorMessage("ERROR", "An error was encountered loading the requested tee time data.");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        // player1 must either be enpty or have the configured guest in it in order to continue
        if (!oldPlayer1.equals("") && !oldPlayer1.startsWith(guest_type)) {

            // this time is now booked with a member in player1 - no not allow access to this tee time!
            unlockTeeTime(teecurr_id, user, con);
            return errorMessage("BLOCKED", "The tee time you are trying to book is now reserved by a member.");
        }

        // count the available open player positions
        int avail_slots = 0;
        if (oldPlayer1.equals("")) avail_slots++;
        if (oldPlayer2.equals("")) avail_slots++;
        if (oldPlayer3.equals("")) avail_slots++;
        if (oldPlayer4.equals("")) avail_slots++;
        if (allow5somes && oldPlayer5.equals("")) avail_slots++;

        if (avail_slots < players) {

            // this time is now booked with a member in player1 - no not allow access to this tee time!
            unlockTeeTime(teecurr_id, user, con);
            return errorMessage("BLOCKED", "The tee time you are trying to book only has " + avail_slots + " openings and you requested " + players + ".");
        }

        // there should be room in this tee time
        
        // default the new player values to the oldPlayer values
        String player1 = oldPlayer1;
        String player2 = oldPlayer2;
        String player3 = oldPlayer3;
        String player4 = oldPlayer4;
        String player5 = oldPlayer5;
        String player1_tmode = oldPlayer1_tmode;
        String player2_tmode = oldPlayer2_tmode;
        String player3_tmode = oldPlayer3_tmode;
        String player4_tmode = oldPlayer4_tmode;
        String player5_tmode = oldPlayer5_tmode;
        String orig1 = oldPlayer1_orig;
        String orig2 = oldPlayer2_orig;
        String orig3 = oldPlayer3_orig;
        String orig4 = oldPlayer4_orig;
        String orig5 = oldPlayer5_orig;
        String fullname = first_name + " " + last_name;
        
        // add the new players - only add to empty slots - stop when we hit the # of req players - make sure first one added includes full name
        int added = 0;
        if (player1.equals("")) {
            
            player1 = buildPlayerName(added, guest_type, fullname);
            player1_tmode = guest_tmode;
            orig1 = user;
            added++;
        }
        if (player2.equals("") && players != added) {

            player2 = buildPlayerName(added, guest_type, fullname);
            player2_tmode = guest_tmode;
            orig2 = user;
            added++;
        }
        if (player3.equals("") && players != added) {

            player3 = buildPlayerName(added, guest_type, fullname);
            player3_tmode = guest_tmode;
            orig3 = user;
            added++;
        }
        if (player4.equals("") && players != added) {

            player4 = buildPlayerName(added, guest_type, fullname);
            player4_tmode = guest_tmode;
            orig4 = user;
            added++;
        }
        if (allow5somes && player5.equals("") && players != added) {

            player5 = buildPlayerName(added, guest_type, fullname);
            player5_tmode = guest_tmode;
            orig5 = user;
            added++;
        }

        // sanity check to make sure we in fact added all the players (since we already knew there was enough space, this shouldn't happen)
        if (players != added) {

            // this time is now booked with a member in player1 - no not allow access to this tee time!
            unlockTeeTime(teecurr_id, user, con);
            Utilities.logError("Partner_interface.doBookingRequest() BUG!! Error fitting requested players in to tee time! Only added " + added + " of " + players + ". club=" + club + ", teecurr_id=" + teecurr_id);
            return errorMessage("BLOCKED", "The requested tee time does not have enough space. There is only room for " + added + " of the " + players + " players you requested.");
        }

        // append the new notes to the old notes
        notes = oldNotes + ((!oldNotes.equals("")) ? "\\n\\n" : "") + notes;



        
        // book the time
        try {

            pstmt = con.prepareStatement(
                    "UPDATE teecurr2 " +
                    "SET " +
                        "last_mod_date = now(), in_use = 0, " +
                        "player1 = ?, player2 = ?, player3 = ?, player4 = ?, player5 = ?, " +
                        "p1cw = ?, p2cw = ?, p3cw = ?, p4cw = ?, p5cw = ?, " +
                        "orig1 = ?, orig2 = ?, orig3 = ?, orig4 = ?, orig5 = ?, " +
                        "orig_by = ?, notes = ? " +
                    "WHERE " +
                    "event = '' AND lottery = '' AND blocker = '' AND " +
                    "teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setString(1, player1);
            pstmt.setString(2, player2);
            pstmt.setString(3, player3);
            pstmt.setString(4, player4);
            pstmt.setString(5, player5);
            pstmt.setString(6, player1_tmode);
            pstmt.setString(7, player2_tmode);
            pstmt.setString(8, player3_tmode);
            pstmt.setString(9, player4_tmode);
            pstmt.setString(10, player5_tmode);
            pstmt.setString(11, orig1);
            pstmt.setString(12, orig2);
            pstmt.setString(13, orig3);
            pstmt.setString(14, orig4);
            pstmt.setString(15, orig5);
            pstmt.setString(16, (orig_by.equals("")) ? user : orig_by);
            pstmt.setString(17, notes);
            pstmt.setInt(18, teecurr_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            unlockTeeTime(teecurr_id, user, con);
            Utilities.logError("Partner_interface.doBookingRequest() Error saving changes to database! club=" + club + ", teecurr_id=" + teecurr_id + ", err=" + exc.toString());
            return errorMessage("ERROR", "Error saving tee time. The tee has not been reserved!");

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        // tee time saved - generate a conf number and save the audit trail
        Gson gson_obj = new Gson();
        String conf_code = Utilities.generateConfCode(partner_id, teecurr_id, club);
        Map<String, Object> optional_data_map = new HashMap<String, Object>();

        optional_data_map.put("player_count", player_count);
        optional_data_map.put("first_name", first_name);
        optional_data_map.put("last_name", last_name);
        optional_data_map.put("address1", address1);
        optional_data_map.put("address2", address2);
        optional_data_map.put("city", city);
        optional_data_map.put("state", state);
        optional_data_map.put("country", country);
        optional_data_map.put("postal_code", postal_code);
        optional_data_map.put("phone", phone);
        optional_data_map.put("email", email);
        optional_data_map.put("membership_num", membership_num);
        optional_data_map.put("per_player_fee", per_player_fee);

        String optional_data = gson_obj.toJson(optional_data_map);

        // save the confirmation data
        try {

            pstmt = con.prepareStatement(
                    "UPDATE v5.tsp_conf_nums " +
                    "SET optional_data = ?, players = ? " +
                    "WHERE conf_code = ?");

            pstmt.clearParameters();
            pstmt.setString(1, optional_data);
            pstmt.setInt(2, players);
            pstmt.setString(3, conf_code);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doBookingRequest() BUG! Error saving optional data to v5.tsp_conf_nums club=" + club + ", conf_code=" + conf_code + ",teecurr_id=" + teecurr_id + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        // add a tee time history entry
        SystemUtils.updateHist(book_date, day, book_time, fb, courseName, player1, player2, player3, player4, player5, user, partner_name, 0, con);


        // build the response
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesResp", response_map);

        response_map.put("teeTimeId", teecurr_id);
        response_map.put("confirmationCode", conf_code);
        response_map.put("result", "SUCCESS");
        response_map.put("transactionId", transaction);
        response_map.put("respDateTime", sdfDate.format(now));
        response_map.put("respEpochTime", System.currentTimeMillis());
        response_map.put("serverId", ProcessConstants.SERVER_ID);
        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");


        // return the response in json format
        return gson_obj.toJson(container_map);

    }


    private String doAvailabilityRequest(HttpServletRequest req, String club, int partner_id, String transaction, long benchmark_start, Connection con) {


        //
        // GET ALL PARAMETERS OF THE REQUEST
        //
        String sdate = req.getParameter("sdate") == null ? "" : req.getParameter("sdate");
        String edate = req.getParameter("edate") == null ? "" : req.getParameter("edate");
        String players = req.getParameter("players") == null ? "" : req.getParameter("players");
      //String courseName = req.getParameter("course") == null ? "" : req.getParameter("course");
        String courseid = req.getParameter("course_id") == null ? "" : req.getParameter("course_id");


        //
        // VERIFY REQUEST PARAMETERS
        //
        int start_date = 0;
        int end_date = 0;
        int min_players = 0;
        int course_id = 0;

        try {
            start_date = Integer.parseInt(sdate);
            end_date = Integer.parseInt(edate);
            min_players = Integer.parseInt(players);  // players is optional don't any more to this block
        } catch (Exception ignore) {}

        try {
            course_id = Integer.parseInt(courseid);
        } catch (Exception ignore) {}


        // set the course name
        String courseName = (course_id == 0) ? "-ALL-" : Utilities.getCourseName(course_id, con);


        if (start_date == 0 || end_date == 0) {

            return errorMessage("BAD_DATES", "The date(s) provided did not resolve. Make sure dates are Integers YYYYMMDD.");
        
        } else if (start_date > end_date) {

            return errorMessage("BAD_DATES", "The dates provided are incorrect. Make sure the start date is before the ending date.");
        
        } else if (end_date - start_date > 7) {

            // disable this check
            //return errorMessage("BAD_DATES", "The date range you specified is invalid. Only 7 days may be requested at a time.");
        
        } else if (min_players <= 0 || min_players > 5) {

            // if they don't specify this value or it's out of range lets just ignore it
            min_players = 0; // force return of all available times regardless of available players
            return errorMessage("BAD_MIN_PLAYERS", "The number of players you specified is not valid. Valid values are 1-5.");

        }


        //
        // LOAD INTERFACE CONFIG DATA
        //
        PreparedStatement pstmt = null;
        Statement stmt = null;
        ResultSet rs = null;
        String availability_flags = "";
        String guest_type = "";
        int max_allowed_guests = 0;
        int max_days_in_advance = 0;
        double fee_per_player = 0;
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT guest_type, max_allowed_guests, availability_flags, max_days_in_advance, fee_per_player " +
                    "FROM teesheet_partner_config " +
                    "WHERE partner_id = " + partner_id);

            if ( rs.next() ) {

                guest_type = rs.getString("guest_type");
                max_allowed_guests = rs.getInt("max_allowed_guests");
                availability_flags = rs.getString("availability_flags");
                max_days_in_advance = rs.getInt("max_days_in_advance");
                fee_per_player = rs.getDouble("fee_per_player");
            }

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doAvailabilityRequest() Error loading allowed guest_type. club=" + club + ", err=" + exc.getMessage());
            return errorMessage("CONFIG", exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }


        // check the days in advance
        int max_date = (int)Utilities.getDate(con, max_days_in_advance);
        if (end_date > max_date) {

            return errorMessage("BAD_DATES", "The end date specified is outside the allowable range. Only times up to " + max_days_in_advance + " day inadvance may be requested. max_date="+ max_date + ", end_date="+end_date + ", max_days_in_advance="+max_days_in_advance + ", partner_id="+partner_id);
        }



        //
        // INSERT CUSTOMS TO THE INTERFACE HERE
        //



        
        //
        // PROCESS REQUEST
        //

        List<Object> availTimes = new ArrayList<Object>();
        int allowable = 0;
        int avail_slots = 0;
        boolean allow5somes = false;
        
        String custom_availability_flags = "";
        double custom_fee = 0;
                    
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new LinkedHashMap<String, Object>();

        container_map.put("foreTeesResp", response_map);

        response_map.put("transactionId", transaction);
        response_map.put("respDateTime", sdfDate.format(now));
        response_map.put("respEpochTime", System.currentTimeMillis());
        response_map.put("serverId", ProcessConstants.SERVER_ID);
        response_map.put("processingTime", "");
      //response_map.put("courseName", course);
      //response_map.put("sdate", sdate);
        response_map.put("teeTime", availTimes); // put the array list in now and we'll populate it below
        
        try {

            // gather the requested tee times for the date (range) that are either empty or already has the special guest type in it
            pstmt = con.prepareStatement (
                        "SELECT teecurr_id, date, time, t1.courseName, fb, day, player1, player2, player3, player4, player5, t2.clubparm_id AS course_id " +
                        "FROM teecurr2 t1 " +
                        "LEFT OUTER JOIN clubparm2 t2 ON t2.courseName = t1.courseName " +
                        "WHERE " +
                            "date BETWEEN ? AND ? AND " +
                            "event = '' AND lottery = '' AND blocker = '' AND " +
                            ((courseName.equals("-ALL-")) ? "" : "t1.courseName = ? AND ") +
                            "(player1 = '' || player1 LIKE ?) " +
                        "ORDER BY date, time, t1.courseName");

            pstmt.clearParameters();
            pstmt.setInt(1, Integer.parseInt(sdate));
            pstmt.setInt(2, Integer.parseInt(edate));
            
            if (courseName.equals("-ALL-") || course_id == 0) {

                pstmt.setString(3, guest_type);

            } else {

                pstmt.setString(3, courseName);
                pstmt.setString(4, guest_type + "%");
            }
            
            rs = pstmt.executeQuery();

            while ( rs.next() ) {

                // count the available open player positions
                avail_slots = 0; // reset
                if (rs.getString("player1").equals("")) avail_slots++;
                if (rs.getString("player2").equals("")) avail_slots++;
                if (rs.getString("player3").equals("")) avail_slots++;
                if (rs.getString("player4").equals("")) avail_slots++;
                if (allow5somes && rs.getString("player5").equals("")) avail_slots++;

                // restrict available slots if is violates max allowed
                if (avail_slots > max_allowed_guests) avail_slots = max_allowed_guests;

                // if the slot doesn't contain enough space skip further checking
                if (avail_slots >= min_players) {

                    // only add valid times to the map
                    allowable = checkTeeTime(rs.getInt("date"), rs.getInt("time"), rs.getString("courseName"), rs.getInt("fb"), rs.getString("day"), guest_type, avail_slots, con);

                    // custom allowable and fee for the golf simulator
                    if (club.equals("demov4") && rs.getString("courseName").equals("Golf Simulator")) {

                        custom_fee = 45.50;
                        allowable = 2;      // force max of 2 for golf simulator
                    }

                    // if the slot has enough space then add it to the response
                    if (allowable > 0 && allowable >= min_players) {

                        //
                        // Availablity flags can be customized for each course at a club.
                        //
                        custom_availability_flags = "";

                        if (club.equals("demov4") && rs.getString("courseName").equals("Resort Course")) {

                            custom_availability_flags = "NYNY"; // 2 or 4 players
                        }

                        //
                        // Fee can be customized for each course at a club
                        //
                        custom_fee = 0;

                        if (club.equals("demov4") && rs.getString("courseName").equals("Third Course")) {

                            custom_fee = 100.0;
                        }

                        Map<String, String> teetime_map = new HashMap<String, String>();

                        teetime_map.put("teeTimeId", rs.getString("teecurr_id"));
                        teetime_map.put("date", rs.getString("date"));
                        teetime_map.put("time", rs.getString("time"));
                        teetime_map.put("courseId", rs.getString("course_id"));
                        teetime_map.put("courseName", rs.getString("courseName"));
                        teetime_map.put("maxPlayers", String.valueOf(allowable));
                        teetime_map.put("availabilityFlags", ((!custom_availability_flags.equals("")) ? custom_availability_flags : availability_flags));
                        teetime_map.put("feePerPlayer", String.valueOf((custom_fee != 0) ? custom_fee : fee_per_player));

                        availTimes.add(teetime_map);

                    } // end if allowable ok

                } // end avail_slots ok

            } // end while loop

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.doAvailabilityRequest() club=" + club + ", err=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        if (availTimes.size() == 0) {

            response_map.put("noTeeTimesFound", "true");
        }

        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms"); // update this key's value with the elapsed time
        
        Gson gson_obj = new Gson();
        
        return gson_obj.toJson(container_map);

    } // end doAvailabilityRequest



    private int checkTeeTime(int date, int time, String course, int fb, String day, String guest_type, int max_allowed_guests, Connection con) {

        int result = 0;
        result = max_allowed_guests; // default

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        String debug_entry = date + " " + time + ", " + course + ", " + day + " ==> ";

        //Utilities.logErrorTxt(debug_entry, "tontoverde");

        try {

            // first check for guest restrictions matching their configured guest type
            pstmt = con.prepareStatement (
                    "SELECT g1.id, g1.num_guests, g1.recurr, g1.name " +
                    "FROM guestres2 g1 " +
                    "LEFT OUTER JOIN guestres2_gtypes g2 ON g2.guestres_id = g1.id " +
                    "WHERE activity_id = 0 AND " +
                        "sdate <= ? AND edate >= ? AND " +
                        "stime <= ? AND etime >= ? AND " +
                        "(courseName = '-ALL-' OR courseName = ?) AND " +
                        "(fb = 'Both' OR fb = ?) AND " +
                        "g2.guest_type = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, date);
            pstmt.setInt(2, date);
            pstmt.setInt(3, time);
            pstmt.setInt(4, time);
            pstmt.setString(5, course);
            pstmt.setString(6, (fb == 0) ? "Front" : "Back");
            pstmt.setString(7, guest_type);

            rs = pstmt.executeQuery();

            rs_loop1:
            while (rs.next()) {

                debug_entry = " * checking '" + rs.getString("name") + "' for " + rs.getString("recurr") + ": ";

                // check the recurr to see if it applies to today
                if (rs.getString("recurr").equalsIgnoreCase("every " + day)
                    || rs.getString("recurr").equalsIgnoreCase("every day")
                    || (rs.getString("recurr").equalsIgnoreCase("all weekdays")
                    && !day.equalsIgnoreCase("saturday")
                    && !day.equalsIgnoreCase("sunday"))
                    || (rs.getString("recurr").equalsIgnoreCase("all weekends")
                    && (day.equalsIgnoreCase("saturday") || day.equalsIgnoreCase("sunday")))) {

                    // this restriction does apply to this day of the week
                    debug_entry += "applies";
                    
                    // check to see if it's suspended
                    // returns true if suspended, false if not)
                    if (!verifySlot.checkRestSuspend(-99, rs.getInt("id"), 0, date, time, day, course, con)) {

                        debug_entry += ", not suspended.";
                        //Utilities.logErrorTxt(debug_entry + " BREAK", "tontoverde");

                        // restriction was not suspended
                        // prevent access to this tee time
                        result = 0; // indicates that no player postions are available for this tee time

                        /*
                        // if the number of allowed guests
                        if (rs.getInt("num_guests") < max_allowed_guests) {

                            // the restriction allow more guests than are even available so return the # of avail
                            result = max_allowed_guests;

                        } else {

                            // set the result to the number of allowed guests in the restriction
                            result = rs.getInt("num_guests");

                        }
                         */
                        
                        break rs_loop1;

                    } else {

                        // restriction was suspended - this time is allowed
                        // the max_allowed_guests passed is the avail_slots this tee time had
                        // continue searching for additional restrictions
                        result = max_allowed_guests;
                        debug_entry += ", is suspended!";

                    }

                } else {

                    debug_entry += "does not apply.";

                }// end recurr check

                //Utilities.logErrorTxt(debug_entry, "tontoverde");

            } // end while loop of restrictions

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.checkTeeTime() Error checking guest restrictions. Err=" + exc.getMessage());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        return result;

    }


    private void unlockTeeTime(int teecurr_id, String user, Connection con) {


        PreparedStatement pstmt = null;

        try {

            pstmt = con.prepareStatement(
                    "UPDATE teecurr2 " +
                    "SET in_use = 0 " +
                    "WHERE in_use_by = ? AND teecurr_id = ?");

            pstmt.clearParameters();
            pstmt.setString(1, user);
            pstmt.setInt(2, teecurr_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Partner_interface.unlockTeeTime() Error freeing tee time. teecurr_id=" + teecurr_id + ", err=" + exc.getMessage());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
    }


    private String errorMessage(String errorCode, String errorMessage) {


        Gson gson_obj = new Gson();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesErrorResp", response_map);

        response_map.put("errorCode", errorCode);
        response_map.put("errorMessage", errorMessage);
        response_map.put("errorTime", System.currentTimeMillis());  // the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
        response_map.put("serverId", ProcessConstants.SERVER_ID);
        
        return gson_obj.toJson(container_map);

    }


    private String buildPlayerName(int added, String guest_type, String fullname) {


        return (added == 0) ? guest_type + " " + fullname : guest_type;

    }












/*

        List<Object> teetimes = new ArrayList<Object>();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesResp", response_map);

        Integer transaction_id = 87346274;

        response_map.put("transactionId",transaction_id);
        response_map.put("teeTime", teetimes);


        //Example of adding 4 tee times
        for(int i = 0; i < 4; i++) {
            Map<String, String> teetime_map = new HashMap<String,String>();

            teetime_map.put("ID", "77"+i);
            teetime_map.put("Date", "9:40 AM");
            teetime_map.put("Time", "11/14/2012");
            teetime_map.put("CourseName", "Member's Course");

            teetimes.add(teetime_map);
        }


        // Make a json string first
        Gson gson_obj = new Gson();

        return gson_obj.toJson(container_map);

    }

*/





    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
