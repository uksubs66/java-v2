/***************************************************************************************
 *   Common_webapi:  This servlet will provide a stateless interface which will allow data.
 *                access by our website product
 *
 *
 *   called by:  ForeTees 'Premier' Website
 *
 *   created: 12/28/2012
 *
 *
 *   last updated:
 *
 *        1/16/2014  Update buildItemDescription method so that the time is not first
 *        9/30/2013  Added getActivitities method for returning a list of configured activities
 *        4/10/2013  Updated getCalendarData so it can work with custom date range
 *        2/25/2013  Add member record sync methods
 *        2/05/2013  Re-purpose performSSO
 *        1/01/2013  Clean up and document
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
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.UUID;
import java.sql.*;
import java.lang.reflect.*;

import org.apache.commons.lang.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;

import de.odysseus.staxon.json.JsonXMLConfig;
import de.odysseus.staxon.json.JsonXMLConfigBuilder;
import de.odysseus.staxon.json.JsonXMLInputFactory;
import de.odysseus.staxon.xml.util.PrettyXMLEventWriter;

import org.joda.time.DateTime;
import org.joda.time.DateMidnight;
import org.joda.time.Days;

// foretees imports
import com.foretees.client.SystemLingo;

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.DaysAdv;
import com.foretees.common.parmClub;
import com.foretees.common.getActivity;
import com.foretees.common.verifySlot;
import com.foretees.common.getClub;
import com.foretees.common.parmDining;
import com.foretees.common.FeedBack;
import com.foretees.common.AESencrypt;

import com.foretees.member.Member;

import com.google.gson.*;
import com.google.gson.reflect.*; // for json

import sun.misc.BASE64Decoder;

/**
 *
 * @author sindep
 */
public class Common_webapi extends HttpServlet {


    final static String SESSION_ID = SystemUtils.id;

    
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
        
        //String club = "";
        //String username = "";
        //String name = "";       // full name of member - used for sso requests
        //String mship = "", mtype = "", wc = "";
        
        String result = "";
        
        // get the passed parameters in to local variables
        String clubid = req.getParameter("club_id") == null ? "" : req.getParameter("club_id");         // club id
        String type = req.getParameter("type") == null ? "" : req.getParameter("type");                 // action
        String respType = req.getParameter("respType") == null ? "" : req.getParameter("respType");     // response type
        String user = req.getParameter("user") == null ? "" : req.getParameter("user");                 // flexid of user


        // get the activity we are requesting data for
        int activity_id = 0;
        String tmp = req.getParameter("activity_id") == null ? "" : req.getParameter("activity_id");
        try {
            activity_id = Integer.parseInt(tmp);
        } catch (Exception ignore) {}

        
        // Try to load the user based on request
        Map<String, Object> userMap = loadUser(req, type, clubid, user);
       
        boolean is_error = (Boolean)userMap.get("is_error");
        // If user loaded
        if (!is_error) {
            Connection con = (Connection)userMap.get("db_connection");

            String mship = (String)userMap.get("mship");
            String mtype = (String)userMap.get("mtype");
            String wc = (String)userMap.get("wc");
            String club = (String)userMap.get("club");
            String name = (String)userMap.get("name");
            String username = (String)userMap.get("username");
            String remote_ip = (String)userMap.get("remote_ip");
            user = (String)userMap.get("user"); // We grab user incase it was set via SSO
            clubid = (String)userMap.get("clubid"); // We grab clubid incase it was set via SSO

            //
            // AT THIS POINT THIS REQUEST IS CONSIDERED VALID AND WE'LL PROCESS IT
            //

            // process each type of request
            if (type.equalsIgnoreCase("SSO")) { //  && req.getMethod().equals("POST")

                result = encodeMap(performSSO(req, resp, activity_id, user, username, club, ProcessConstants.FT_PREMIER_CALLER, mship, mtype, name, wc, remote_ip, benchmark_start, con));
            }

            if (type.equalsIgnoreCase("MRU") && req.getMethod().equals("POST")) {

                result = encodeMap(performMemberUpdate(req, resp, activity_id, user, club, ProcessConstants.FT_PREMIER_CALLER, mship, mtype, name, wc, remote_ip, benchmark_start, con));
            }

            if (type.equalsIgnoreCase("caldata") && req.getMethod().equals("GET")) {

                result = encodeMap(getCalendarData(req, resp, activity_id, user, username, club, ProcessConstants.FT_PREMIER_CALLER, mship, mtype, benchmark_start, con));

            }

            if (type.equalsIgnoreCase("activities") && req.getMethod().equals("GET")) {

                result = encodeMap(getActivitities(req, resp, activity_id, user, username, club, ProcessConstants.FT_PREMIER_CALLER, mship, mtype, benchmark_start, con));

            }


            // if we got to here with no result then we did not do any processing (type didn't match)
            if (result.equals("")) {

                result = errorMessage("INVALID_REQUEST","Nothing to do. " + clubid + " " + club);

            }

        } else {
            // Unable to load user
            // We have an error convert it to json
            result = encodeMap(wrapErrorMap((Map<String, Object>)userMap.get("error")));
        }// end no result (error msg)
             


        //
        // Output the result default is json - convert to xml if requested
        //
        if (respType.equalsIgnoreCase("XML")) {

            PrintWriter out = resp.getWriter();
            resp.setContentType("application/xml;charset=UTF-8"); // http://www.rfc-editor.org/rfc/rfc3023.txt

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
                out.println("UNEXPECTED ERROR PROCESSING XML: " + exc.toString());

            } finally {

                /*
                 * As per StAX specification, XMLEventReader/Writer.close() doesn't close
                 * the underlying stream.
                 */
                input.close();

            }

            out.close();
            
        } else {


            if (req.getParameter("callback") != null) {

                PrintWriter out = resp.getWriter();
                resp.setContentType("application/javascript;charset=UTF-8");
                out.print(req.getParameter("callback") + "(");
                out.print(result);
                out.print(");");
                out.close();

            } else {

                PrintWriter out = resp.getWriter();
                resp.setContentType("application/json;charset=UTF-8"); // http://www.ietf.org/rfc/rfc4627.txt
                out.print(result);
                out.close();
            }

        }

/*
        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        try {

        } finally { 
            out.close();
        }
*/
    } 


    private static List<String> decryptUID (Long keyTime, String base64_iv, String uid) {


        try {

            String key = AESencrypt.encryptionKey + keyTime.toString();

            String clear = "", decrypt_err = "", type_err = "";

            BASE64Decoder base64decoder = new BASE64Decoder();

            byte[] iv = base64decoder.decodeBuffer( base64_iv );
            //String hex_iv = asHex(iv);

            byte[] credentials = base64decoder.decodeBuffer( uid );

            //String hex = asHex(credentials);

            try {

                clear = AESencrypt.decrypt(credentials, key, iv);

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.decryptUID error1. err=" + exc.toString() + ", key=" + key + ", uid=" + uid + ", base64_iv=" + base64_iv);
            }

            Gson gson = new Gson();
            List<String> uid_list = new ArrayList();

            try {

                Type data_type = new TypeToken<List<String>>() {
                }.getType();
                uid_list = gson.fromJson(clear, data_type);

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.decryptUID error2. err=" + exc.toString() + ", clear=" + clear);

            }


            if (uid_list == null || uid_list.size() != 2) {

                return null;

            } else {

                return uid_list;
            }

        } catch (Exception exc) {

            Utilities.logError("Common_webapi.decryptUID unexpected error! err=" + exc.toString());

        }

        return null;

    }
    
    public static Map<String, Object> loadUser(HttpServletRequest req, String type, String clubid, String user){
        
        Map<String, Object> map_result = new LinkedHashMap<String, Object>();
        Map<String, Object> error_result = new LinkedHashMap<String, Object>();
        String mship = "", mtype = "", wc = "", club = "", name = "", username = "", uid = "", base64_iv = "";
        String remote_ip = req.getHeader("x-forwarded-for");  // get remote IP for access control
        boolean user_allowed = false;
        boolean ip_allowed = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Connection con = null;
        
        if (type.equalsIgnoreCase("SSO")) {

            if(req.getParameter("sso_uid") == null){
                uid = req.getParameter("uid") == null ? "" : req.getParameter("uid");                    // encrypted club/user values
                base64_iv = req.getParameter("iv") == null ? "" : req.getParameter("iv");                // initialization vector
            } else {
                uid = req.getParameter("sso_uid") == null ? "" : req.getParameter("sso_uid");                    // encrypted club/user values
                base64_iv = req.getParameter("sso_iv") == null ? "" : req.getParameter("sso_iv");                // initialization vector
            }
            Long keyTime = (System.currentTimeMillis() / 1000L) / 3600;                                     // get hours since Epoch

            List<String> uid_list = new ArrayList();

            // try to decrypt the current hour
            uid_list = decryptUID(keyTime, base64_iv, uid);

            // if it failed try forward an hour
            if (uid_list == null || uid_list.size() != 2) {

                uid_list = decryptUID(keyTime + 1, base64_iv, uid);
            }

            // if it failed again try back an hour
            if (uid_list == null || uid_list.size() != 2) {

                uid_list = decryptUID(keyTime - 1, base64_iv, uid);
            }

            // if failed after trying rolling forward and back an hour then abort
            if (uid_list == null || uid_list.size() != 2) {

                error_result = errorMessageObj("INVALID_UID","The UID provided was not valid. unadjusted key=" + AESencrypt.encryptionKey + keyTime.toString() + ", pass_uid=" + uid + ", passed_base64_iv=" + base64_iv);
            
            } else {

                // if still here these should be available
                user = uid_list.get(0).toString();
                clubid = uid_list.get(1).toString();
            }

        }


        // MAKE SURE THE CLUB SPECIFIED EXISTS AND ACCESS IS ALLOWED

        if (error_result.size() < 1) {

            if (remote_ip == null || remote_ip.equals("")) remote_ip = req.getRemoteAddr();

            // verify user credentials (user access & club is configured for access)

            try {

                if (Integer.valueOf(clubid) > 0) {

                    con = Connect.getCon(ProcessConstants.REV);

                    club = Utilities.getClubName(Integer.valueOf(clubid), con);

                    if (club != null && !club.equals("")) {

                        con = Connect.getCon(club);

                        pstmt = con.prepareStatement (
                                    "SELECT seamless_caller " +
                                    "FROM club5 " +
                                    "WHERE 1 = ? AND clubName <> '';");

                        pstmt.clearParameters();
                        pstmt.setInt(1, 1);
                        rs = pstmt.executeQuery();

                        if (rs.next()) {

                            if (!rs.getString("seamless_caller").equalsIgnoreCase("PDG4735") && !rs.getString("seamless_caller").equalsIgnoreCase("FLEXSCAPE4865") && !rs.getString("seamless_caller").equalsIgnoreCase(ProcessConstants.FT_PREMIER_CALLER)) {

                                //if (ProcessConstants.SERVER_ID != 4) result = errorMessage("BAD_CLUB_ID","The club requested is not configured for API access. " + clubid);

                            }
                        }

                        // only do IP verification on production servers and non-sso reqs
                        if (ProcessConstants.SERVER_ID != 4 && !type.equalsIgnoreCase("SSO")) {

                            // now look up the allowable IP
                            pstmt = con.prepareStatement (
                                        "SELECT id " +
                                        "FROM v5.flexweb_ip_addresses " +
                                        "WHERE ip = ?");

                            pstmt.clearParameters();
                            pstmt.setString(1, remote_ip);
                            rs = pstmt.executeQuery();

                            if (rs.next()) ip_allowed = true;

                        } else {

                            // sso or on dev server so allow it regardless
                            ip_allowed = true;
                        }
                        // Store the connection object for later use by caller
                        map_result.put("db_connection", con);

                    } else {

                        error_result = errorMessageObj("BAD_CLUB_ID","The club requested was not found. " + clubid);

                    }

                } else {

                    //Utilities.logError("Common_webapi.loadUser() Error finding club. club_id=" + clubid + ", club=" + club + ", remote_ip=" + remote_ip);

                    error_result = errorMessageObj("UNEXPECTED_ERROR","Error loading initial data.  Invalid.");

                }

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.loadUser() Error loading initial data. club_id=" + clubid + ", club=" + club + ", remote_ip=" + remote_ip + ", err=" + exc.getMessage());

                error_result = errorMessageObj("UNEXPECTED_ERROR","Error loading initial data. " + exc.getMessage());

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }
            
        } // end if no error yet


        // MAKE SURE USER EXISTS AND IS NOT INACTIVE OR MARKED AS NON-BILLABLE

        if (error_result.size() < 1) {

            try {

                String stmtString = "" +
                        "SELECT username, name_last, name_first, name_mi, m_ship, m_type, email, count, wc, message, " +
                            "memNum, email2, email_bounced, email2_bounced, iCal1, iCal2, default_activity_id, mobile_count, mobile_iphone " +
                        "FROM member2b " +
                        "WHERE inact = 0 AND billable = 1 AND flexid = ?";

                pstmt = con.prepareStatement (stmtString);

                pstmt.clearParameters();
                pstmt.setString(1, user);
                rs = pstmt.executeQuery();

                if (rs.next()) {


                    // Get the member's full name.......
                    StringBuffer mem_name = new StringBuffer(rs.getString("name_first"));   // get first name

                    String mi = rs.getString("name_mi");                                    // middle initial
                    if (!mi.equals( "" )) {
                        mem_name.append(" ");
                        mem_name.append(mi);
                    }
                    mem_name.append(" " + rs.getString("name_last"));                       // last name

                    name = mem_name.toString();             // convert to one string

                    // Get the member's membership type
                    mship = rs.getString("m_ship");         // Get mship type
                    mtype = rs.getString("m_type");         // Get member type
                    wc = rs.getString("wc");                // Get default tmode
                    username = rs.getString("username");

                    user_allowed = true;
                }

            } catch (Exception exc) {

                Utilities.logError("Common_webapi.loadUser() Error validating user. user=" + user + ", club=" + club + ", remote_ip=" + remote_ip + ", err=" + exc.getMessage());

                error_result = errorMessageObj("UNEXPECTED_ERROR","Error validating user. " + exc.getMessage());

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }


            if (!user_allowed) {

                error_result = errorMessageObj("MEMBER_NOT_FOUND","No active member record found for requested user. user=" + user + ", club=" + club);

            }

        } // end if no error yet

        
        map_result.put("mship", mship);
        map_result.put("mtype", mtype);
        map_result.put("wc", wc);
        map_result.put("club", club);
        map_result.put("clubid", clubid);
        map_result.put("name", name);
        map_result.put("user", user);
        map_result.put("username", username);
        map_result.put("remote_ip", remote_ip);
        map_result.put("user_allowed", user_allowed);
        map_result.put("ip_allowed", ip_allowed);
        map_result.put("is_error", (error_result.size() > 0));
        
        if(error_result.size() > 0){
            map_result.put("error", error_result);
        }
        return map_result;
        
    }

    public static String asHex(byte buf[])
        {
                StringBuffer strbuf = new StringBuffer(buf.length * 2);

                for(int i=0; i< buf.length; i++)
                {
                        if(((int) buf[i] & 0xff) < 0x10)
                                strbuf.append("0");
                        strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
                }
                return strbuf.toString();
        }


    public Map<String, Object> performMemberUpdate(HttpServletRequest req, HttpServletResponse resp, int activity_id, final String user, final String club, final String caller, String mship, String mtype, final String name, final String wc, final String remote_ip, final long benchmark_start, Connection con)
            throws ServletException, IOException {


        //Member member = new Member();

        // load all variables from req to local

        //constants for request parameter names
        String COURSE_HANDICAP = "c_hancap";
        String USGA_HANDICAP = "g_hancap";
        String FIRST_NAME = "memname_first";
        String LAST_NAME = "memname_last";
        String MIDDLE_INITIAL = "memname_mi";
        String USER_NAME = "mem_username";
        String PASSWORD = "mem_password";
        String MEMSHIP_TYPE = "memship_type";
        String MEM_TYPE = "mem_type";
        String MEM_SUB_TYPE = "mem_subtype";
        String EMAIL = "mem_email";
        String EMAIL2 = "mem_email2";
        String WALK_CART = "walk_cart";
        String MEM_NUM = "memNum";
        String WEBID = "webid";
        String GHIN = "ghin";
        String LOCKER = "locker";
        String BAG_SLOT = "bag";
        String BIRTH_MONTH = "bmm";
        String BIRTH_DAY = "bdd";
        String REQ_USER_NAME = "username";
        String REQ_ACTIVITY_ID = "activity_id";
        String EXISTING_USER = "existing_user";
        String GUEST_TYPE = "guest_type";
        String POS_ID = "posid";
        String PHONE_NUM = "phoneNum";
        String PHONE_NUM2 = "phoneNum2";
        String BIRTH_DATE = "birthDate";
        String HDCP_CLUB_NUM = "hdcpClubNum";
        String HDCP_ASSOC_NUM = "hdcpAssocNum";
        String GENDER = "gender";
        String MEM_INACT = "mem_inact";
        String EXCLUDE = "exclude";
        String T_FLAG = "tflag";                 // tee sheet flag
        String USTA_NUM = "usta_num";
        String NTRP_RATING = "ntrp_rating";

        String fname = SystemUtils.scrubString(req.getParameter(Member.FIRST_NAME));
        String lname = SystemUtils.scrubString(req.getParameter(Member.LAST_NAME));
        String mi = SystemUtils.scrubString(req.getParameter(Member.MIDDLE_INITIAL));
        String email1 = SystemUtils.scrubString(req.getParameter(Member.EMAIL));
        String email2 = SystemUtils.scrubString(req.getParameter(Member.EMAIL2));
        String hdcp_num = SystemUtils.scrubString(req.getParameter(Member.GHIN));
        String birth_date = SystemUtils.scrubString(req.getParameter(Member.BIRTH_DATE));
        String gender = SystemUtils.scrubString(req.getParameter(Member.GENDER));
        String phone1 = SystemUtils.scrubString(req.getParameter(Member.PHONE_NUM));
        String phone2 = SystemUtils.scrubString(req.getParameter(Member.PHONE_NUM2));
        //String phone2 = SystemUtils.scrubString(req.getParameter(Member.PHONE_NUM2));



        //
        //  convert birth date (mm/dd/yyyy to yyyymmdd)
        //
        int birth = 0;

        if (!birth_date.equals( "" )) {

            String b1 = "";
            String b2 = "";
            String b3 = "";
            int mm = 0;
            int dd = 0;
            int yy = 0;

            StringTokenizer tok = new StringTokenizer( birth_date, "/-" );     // delimiters are / & -

            if ( tok.countTokens() > 2 ) {

                b1 = tok.nextToken();
                b2 = tok.nextToken();
                b3 = tok.nextToken();

                mm = Integer.parseInt(b1);
                dd = Integer.parseInt(b2);
                yy = Integer.parseInt(b3);

                if (yy < 100) {            // if only 2 digits

                    if (yy < 15) {

                        yy = 2000 + yy;

                    } else {

                        yy = 1900 + yy;
                    }
                }

                if (yy < 1900) {                             // check for invalid date

                    birth = 0;

                } else {

                    birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd
                }

            } // end if enough parts

        } // end birthday conversion


        // convert the primary indicator

        String primary = "";

        if (primary.equals("0")) {

            mtype = "Full Golf - Male";

            if (gender.equals("")) {
                gender = "M";
            }

        } else if (primary.equals("1")) {

            mtype = "Full Golf - Female";

            if (gender.equals("")) {
                gender = "F";
            }

        } else {

            mtype = "Dependent";
        }

        fname = toTitleCase(fname);
        lname = toTitleCase(lname);


        String comma = "";

        StringBuilder sql = new StringBuilder("UPDATE member2b SET");

        if (!fname.equals("")) {
            sql.append(comma);
            sql.append(" name_first = ? ");
            comma = ",";
        }
        if (!lname.equals("")) {
            sql.append(comma);
            sql.append(" name_last = ? ");
            comma = ",";
        }
        if (!mi.equals("")) {
            sql.append(comma);
            sql.append(" name_mi = ? ");
            comma = ",";
        }
        if (!email1.equals("")) {
            sql.append(comma);
            sql.append(" email = ? ");
            comma = ",";
        }
        if (!email2.equals("")) {
            sql.append(comma);
            sql.append(" email2 = ? ");
            comma = ",";
        }
        if (!hdcp_num.equals("")) {
            sql.append(comma);
            sql.append(" ghin = ? ");
            comma = ",";
        }
        if (!birth_date.equals("")) {
            sql.append(comma);
            sql.append(" birth = ? ");
            comma = ",";
        }

        sql.append("WHERE flexid = ?");


        PreparedStatement pstmt = null;
        int count = 0;

        try {

            pstmt = con.prepareStatement("");
            pstmt.clearParameters();
            pstmt.setString(1, fname);

            count = pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Common_webapi.performMemberUpdate() " + club + " err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) { }
            
        }




        Gson gson_obj = new Gson();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();

        container_map.put("foreTeesActionResp", response_map);

        //response_map.put("result", String.valueOf(count));
        response_map.put("result", (count == 0) ? "FAIL" : "OK");
        response_map.put("message", "Updated " + count + " record(s).");
        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
        response_map.put("serverId", ProcessConstants.SERVER_ID);

        return container_map;

    }


/*
    private static void flexSync(Connection con, InputStreamReader isr, String club, boolean forceSetInact) {

   PreparedStatement pstmt2 = null;
   Statement stmt = null;
   ResultSet rs = null;


   Member member = new Member();

   String line = "";
   String password = "";
   String temp = "";

   int i = 0;

   // Values from Flexscape records
   //
   String fname = "";
   String lname = "";
   String mi = "";
   String suffix = "";
   String posid = "";
   String gender = "";
   String ghin = "";
   String memid = "";
   String webid = "";
   String mNum = "";
   String u_hndcp = "";
   String c_hndcp = "";
   String mship = "";
   String mtype = "";
   String bag = "";
   String email = "";
   String email2 = "";
   String phone = "";
   String phone2 = "";
   String mobile = "";
   String primary = "";
   String active = "";
   String entity_id = "";


   String mship2 = ""; // used to tell if match was found

   float u_hcap = 0;
   float c_hcap = 0;
   int birth = 0;

   // Values from ForeTees records
   //
   String fname_old = "";
   String lname_old = "";
   String mi_old = "";
   String mship_old = "";
   String mtype_old = "";
   String email_old = "";
   String mNum_old = "";
   String ghin_old = "";
   String bag_old = "";
   String posid_old = "";
   String email2_old = "";
   String phone_old = "";
   String phone2_old = "";
   String suffix_old = "";
   String memid_old = "";
   String webid_old = "";

   float u_hcap_old = 0;
   float c_hcap_old = 0;
   int birth_old = 0;

   // Values for New ForeTees records
   //
   String fname_new = "";
   String lname_new = "";
   String mi_new = "";
   String mship_new = "";
   String mtype_new = "";
   String email_new = "";
   String mNum_new = "";
   String ghin_new = "";
   String bag_new = "";
   String posid_new = "";
   String email2_new = "";
   String phone_new = "";
   String phone2_new = "";
   String suffix_new = "";
   String memid_new = "";
   String last_mship = "";
   String last_mnum = "";
   String webid_new = "";

   float u_hcap_new = 0;
   float c_hcap_new = 0;
   int birth_new = 0;
   int rcount = 0;
   int newCount = 0;
   int modCount = 0;
   int setInactCount = 0;
   int work = 0;

   String errorMsg = "Error in Common_sync.flexSync: ";

   boolean failed = false;
   boolean changed = false;
   boolean skip = false;
   boolean headerFound = false;
   boolean found = false;
   boolean memidChanged = false;
   boolean useWebid = false;

   String mNum_curr = "";
   int depCount = 0;


   SystemUtils.logErrorToFile("FlexScape: Error log for " + club + "\nStart time: " + new java.util.Date().toString() + "\n", club, false);

   try {

      BufferedReader br = new BufferedReader(isr);

      while (true) {

         line = br.readLine();

         if (line == null) {
            break;
         }

         //  Skip the 1st row (header row)

         if (headerFound == false) {

            headerFound = true;

         } else {

           skip = false;
           found = false;        // default to club NOT found

           //  All other Flexscape Clubs - Remove the dbl quotes and check for embedded commas

           line = cleanRecord4( line );       // insert a ? if 2 commas found w/o data between them
           line = cleanRecord2( line );       // remove double quotes and embedded commas
           line = cleanRecord4( line );       // check for empty fields again - insert ? between 2 consecutive commas

           rcount++;                          // count the records

           //  parse the line to gather all the info

           StringTokenizer tok = new StringTokenizer( line, "," );     // delimiters are comma

           if ( tok.countTokens() > 10 ) {     // enough data ?

              webid = tok.nextToken();
              memid = tok.nextToken();
              tok.nextToken();          // eat this value, not used
              fname = tok.nextToken();
              mi = tok.nextToken();
              lname = tok.nextToken();
              gender = tok.nextToken();
              email = tok.nextToken();
              phone = tok.nextToken();
              phone2 = tok.nextToken();
              temp = tok.nextToken();             // usr_birthday column
              primary = tok.nextToken();          // usr_relationship column
              mship = tok.nextToken();            // grp_name column

              if (tok.countTokens() > 0) {

                  entity_id = tok.nextToken();    // entity_id col - if provided

              } else {

                  entity_id = "";
              }

              mNum = "";
              suffix = "";
              mtype = "";
              email2 = "";
              bag = "";
              ghin = "";
              u_hndcp = "";
              c_hndcp = "";
              posid = "";
              mobile = "";
              active = "";

              //
              //  Check for ? (not provided)
              //
              if (webid.equals( "?" )) {

                 webid = "";
              }
              if (memid.equals( "?" )) {

                 memid = "";
              }
              if (fname.equals( "?" )) {

                 fname = "";
              }
              if (mi.equals( "?" )) {

                 mi = "";
              }
              if (lname.equals( "?" )) {

                 lname = "";
              }
              if (mship.equals( "?" )) {

                 mship = "";
              }
              if (gender.equals( "?" )) {

                 gender = "";
              }
              if (email.equals( "?" )) {

                 email = "";
              }
              if (phone.equals( "?" )) {

                 phone = "";
              }
              if (phone2.equals( "?" )) {

                 phone2 = "";
              }
              if (temp.equals( "?" ) || temp.equals( "0" )) {

                 temp = "";
              }
              if (primary.equals( "?" )) {

                 primary = "";
              }
              if (entity_id.equals( "?" )) {

                 entity_id = "";
              }

              //
              //  Check for bad first name
              //
              if (!fname.startsWith("a") && !fname.startsWith("b") && !fname.startsWith("c") && !fname.startsWith("d") && !fname.startsWith("e") &&
                  !fname.startsWith("f") && !fname.startsWith("g") && !fname.startsWith("h") && !fname.startsWith("i") && !fname.startsWith("j") &&
                  !fname.startsWith("k") && !fname.startsWith("l") && !fname.startsWith("m") && !fname.startsWith("n") && !fname.startsWith("o") &&
                  !fname.startsWith("p") && !fname.startsWith("q") && !fname.startsWith("r") && !fname.startsWith("s") && !fname.startsWith("t") &&
                  !fname.startsWith("u") && !fname.startsWith("v") && !fname.startsWith("w") && !fname.startsWith("x") && !fname.startsWith("y") &&
                  !fname.startsWith("z") && !fname.startsWith("A") && !fname.startsWith("B") && !fname.startsWith("C") && !fname.startsWith("D") &&
                  !fname.startsWith("E") && !fname.startsWith("F") && !fname.startsWith("G") && !fname.startsWith("H") && !fname.startsWith("I") &&
                  !fname.startsWith("J") && !fname.startsWith("K") && !fname.startsWith("L") && !fname.startsWith("M") && !fname.startsWith("N") &&
                  !fname.startsWith("O") && !fname.startsWith("P") && !fname.startsWith("Q") && !fname.startsWith("R") && !fname.startsWith("S") &&
                  !fname.startsWith("T") && !fname.startsWith("U") && !fname.startsWith("V") && !fname.startsWith("W") && !fname.startsWith("X") &&
                  !fname.startsWith("Y") && !fname.startsWith("Z")) {

                 fname = "";          // skip this record
              }


              //
              //  Determine if we should process this record (does it meet the minimum requirements?)
              //
              if (!webid.equals( "" ) && !memid.equals( "" ) && !lname.equals( "" ) && !fname.equals( "" )) {

                 //
                 //  Remove spaces, etc. from name fields
                 //
                 tok = new StringTokenizer( fname, " " );     // delimiters are space

                 fname = tok.nextToken();                     // remove any spaces and middle name

                 if ( tok.countTokens() > 0 ) {

                    mi = tok.nextToken();                     // over-write mi if already there
                 }

                 if (!suffix.equals( "" )) {                     // if suffix provided

                    tok = new StringTokenizer( suffix, " " );     // delimiters are space

                    suffix = tok.nextToken();                     // remove any extra (only use one value)
                 }

                 tok = new StringTokenizer( lname, " " );     // delimiters are space

                 lname = tok.nextToken();                     // remove suffix and spaces

                 if (!suffix.equals( "" )) {                  // if suffix provided

                    lname = lname + "_" + suffix;             // append suffix to last name

                 } else {                                     // sufix after last name ?

                    if ( tok.countTokens() > 0 ) {

                       suffix = tok.nextToken();
                       lname = lname + "_" + suffix;          // append suffix to last name
                    }
                 }

                 //
                 //  Determine the handicaps
                 //
                 u_hcap = -99;                    // indicate no hndcp
                 c_hcap = -99;                    // indicate no c_hndcp


                 //
                 //  convert birth date (mm/dd/yyyy to yyyymmdd)
                 //
                 birth = 0;

                 if (!temp.equals( "" )) {

                    String b1 = "";
                    String b2 = "";
                    String b3 = "";
                    int mm = 0;
                    int dd = 0;
                    int yy = 0;

                    tok = new StringTokenizer( temp, "/-" );     // delimiters are / & -

                    if ( tok.countTokens() > 2 ) {

                       b1 = tok.nextToken();
                       b2 = tok.nextToken();
                       b3 = tok.nextToken();

                       mm = Integer.parseInt(b1);
                       dd = Integer.parseInt(b2);
                       yy = Integer.parseInt(b3);

                       if (yy < 100) {            // if only 2 digits

                           if (yy < 15) {

                               yy = 2000 + yy;

                           } else {

                               yy = 1900 + yy;
                           }
                       }

                       if (yy < 1900) {                             // check for invalid date

                          birth = 0;

                       } else {

                          birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd
                       }

                    } else {            // try 'Jan 20, 1951' format

                       tok = new StringTokenizer( temp, ", " );          // delimiters are comma and space

                       if ( tok.countTokens() > 2 ) {

                          b1 = tok.nextToken();
                          b2 = tok.nextToken();
                          b3 = tok.nextToken();

                          if (b1.startsWith( "Jan" )) {
                             mm = 1;
                          } else {
                           if (b1.startsWith( "Feb" )) {
                              mm = 2;
                           } else {
                            if (b1.startsWith( "Mar" )) {
                               mm = 3;
                            } else {
                             if (b1.startsWith( "Apr" )) {
                                mm = 4;
                             } else {
                              if (b1.startsWith( "May" )) {
                                 mm = 5;
                              } else {
                               if (b1.startsWith( "Jun" )) {
                                  mm = 6;
                               } else {
                                if (b1.startsWith( "Jul" )) {
                                   mm = 7;
                                } else {
                                 if (b1.startsWith( "Aug" )) {
                                    mm = 8;
                                 } else {
                                  if (b1.startsWith( "Sep" )) {
                                     mm = 9;
                                  } else {
                                   if (b1.startsWith( "Oct" )) {
                                      mm = 10;
                                   } else {
                                    if (b1.startsWith( "Nov" )) {
                                       mm = 11;
                                    } else {
                                     if (b1.startsWith( "Dec" )) {
                                        mm = 12;
                                     } else {
                                        mm = Integer.parseInt(b1);
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

                          dd = Integer.parseInt(b2);
                          yy = Integer.parseInt(b3);

                          birth = (yy * 10000) + (mm * 100) + dd;      // yyyymmdd

                          if (yy < 1900) {                             // check for invalid date

                             birth = 0;
                          }
                       }
                    }
                 }

                 password = lname;

                 //
                 //  if lname is less than 4 chars, fill with 1's
                 //
                 int length = password.length();

                 while (length < 4) {

                    password = password + "1";
                    length++;
                 }

                 //
                 //  Verify the email addresses
                 //
                 if (!email.equals( "" )) {      // if specified

                    email = email.trim();           // remove spaces

                    FeedBack feedback = (member.isEmailValid(email));

                    if (!feedback.isPositive()) {    // if error

                       email = "";                   // do not use it
                    }
                 }
                 if (!email2.equals( "" )) {      // if specified

                    email2 = email2.trim();           // remove spaces

                    FeedBack feedback = (member.isEmailValid(email2));

                    if (!feedback.isPositive()) {    // if error

                       email2 = "";                   // do not use it
                    }
                 }

                 // if email #1 is empty then assign email #2 to it
                 if (email.equals("")) email = email2;


                 //*********************************************
                 //   Start of club specific processing
                 //*********************************************


                 //******************************************************************
                 //   Ocean Reef - oceanreef
                 //******************************************************************
                 //
                 if (club.equals( "oceanreef" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, "oceanreef", true);

                     } else if (!memid.equals( "" )) {                 // must have a webid

                         mNum = memid;

                         posid = mNum;


                         if (primary.equals( "" )) {

                             primary = "0";
                         }

                         if (mNum.endsWith("-000")) {
                             primary = "0";
                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 gender = "M";
                                 mtype = "Primary Male";
                             }
                         } else if (mNum.endsWith("-001")) {
                             primary = "1";
                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 gender = "F";
                                 mtype = "Spouse Female";
                             }
                         } else if (mNum.endsWith("-002")) {
                             primary = "2";
                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Family Female";
                             } else {
                                 gender = "M";
                                 mtype = "Family Male";
                             }
                         } else {

                             try {

                                 int tempPri = Integer.parseInt(mNum.substring(mNum.length() - 3));

                                 if (tempPri < 50) {

                                     primary = mNum.substring(mNum.length() - 1);

                                     if (gender.equalsIgnoreCase("F")) {
                                         mtype = "Dependent Female";
                                     } else {
                                         gender = "M";
                                         mtype = "Dependent Male";
                                     }

                                 } else {
                                     skip = true;
                                     SystemUtils.logErrorToFile("EMPLOYEE MNUM *ABOVE* -050 - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                                 }

                             } catch (Exception exc) {
                                 primary = "3";
                             }
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                         if (!skip) {

                             if (mship.equals("100") || mship.equals("105") || mship.equals("109") || mship.equals("110") || mship.equals("115") ||
                                     mship.equals("150") || mship.equals("159") || mship.equals("160") || mship.equals("180") || mship.equals("199") ||
                                     mship.equals("300") || mship.equals("360")) {
                                 mship = "Social";
                             } else if (mship.equals("101")) {
                                 mship = "Multi-Game Card";
                             } else if (mship.equals("102")) {
                                 mship = "Summer Option";
                             } else if (mship.equals("130")) {
                                 mship = "Social Legacy";
                             } else if (mship.equals("415") || mship.equals("435")) {
                                 mship = "Junior Legacy";
                             } else if (mship.equals("400") || mship.equals("445") || mship.equals("450") || mship.equals("460") || mship.equals("470") || mship.equals("480")) {
                                 mship = "Charter";
                             } else if (mship.equals("420") || mship.equals("425") || mship.equals("430")) {
                                 mship = "Charter Legacy";
                             } else if (mship.equals("401")) {
                                 mship = "Charter w/Trail Pass";
                             } else if (mship.equals("500") || mship.equals("540") || mship.equals("570") || mship.equals("580")) {
                                 mship = "Patron";
                             } else if (mship.equals("515") || mship.equals("520") || mship.equals("530")) {
                                 mship = "Patron Legacy";
                             } else if ((mship.equals("800") || mship.equals("801") || mship.equals("860") || mship.equals("880")) && !mtype.startsWith("Dependent")) {
                                 mship = "Other";
                             } else {
                                 skip = true;
                                 SystemUtils.logErrorToFile("MSHIP NON-GOLF OR UNKNOWN TYPE - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                             }

                             if (mship.equals( "" )) {

                                 skip = true;            // skip record if mship not one of above
                                 SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);
                             }
                         }

                     } else {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED", club, true);
                     }
                 }      // end of if oceanreef


                 //******************************************************************
                 //   Colorado Springs CC - coloradospringscountryclub
                 //******************************************************************
                 //
                 if (club.equals( "coloradospringscountryclub" )) {

                     found = true;        // club found

                     webid = entity_id;     // webid is located in the entity_id field for this club

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         posid = mNum;

                         while (posid.length() < 8) {
                             posid = "0" + posid;
                         }

                         primary = mNum.substring(mNum.length() - 1);

                         if (mNum.endsWith("-000") || mNum.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Adult Female";
                             } else {
                                 mtype = "Adult Male";
                             }

                         } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                    mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                             mtype = "Youth";
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                         if (mship.equalsIgnoreCase("Recreational") || mship.equalsIgnoreCase("Clubhouse")) {

                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE - SKIPPED", club, true);
                         }

                     }
                 }      // end of if coloradospringscountryclub


                 //******************************************************************
                 //   Forest Highlands GC - foresthighlands
                 //******************************************************************
                 //
                 if (club.equals( "foresthighlands" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED", club, true);

                     } else if (fname.toUpperCase().startsWith("USE")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("FIRST NAME STARTING WITH 'USE' (MEMBER HAS ANOTHER, MAIN RECORD ALREADY) - SKIPPED", club, true);

                     } else {

                         useWebid = true;

                         posid = memid.substring(0, memid.length() - 1) + "0";

                         while (posid.length() < 8) {
                             posid = "0" + posid;
                         }

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }
                         mNum = memid;

                         if (mNum.endsWith("-000") || mNum.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Adult Female";
                             } else {
                                 mtype = "Adult Male";
                             }

                         } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                    mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                             mtype = "Dependents";

                             Calendar cal = new GregorianCalendar();        // get todays date
                             int year = cal.get(Calendar.YEAR);
                             int month = cal.get(Calendar.MONTH)+1;
                             int day = cal.get(Calendar.DAY_OF_MONTH);

                             year = year - 25;                              // date to determine if > 24 yrs old

                             int date25 = (year * 10000) + (month * 100) + day;

                             if (birth <= date25) {
                                 skip = true;              // skip record if webid not provided
                                 SystemUtils.logErrorToFile("DEPENDENT OVER 24 - SKIPPED", club, true);
                             }
                         }

                         if (!mNum.equals("")) mNum = mNum.substring(0, mNum.length() - 4);

                         if (mNum.equals("522")) {
                             skip = true;
                         }

                         if (mship.equalsIgnoreCase("Regular Member")) {
                             mship = "Regular";
                         } else if (mship.equalsIgnoreCase("Founding Member")) {
                             mship = "Founding";
                         } else if (mship.equalsIgnoreCase("Special Member")) {
                             mship = "Special";
                         } else if (mship.equalsIgnoreCase("Employee Member")) {
                             mship = "Employee";
                         } else {
                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("UNKNOWN OR NON-GOLF MEMBERSHIP TYPE (" + mship + ") - SKIPPED", club, true);
                         }

                     }
                 }      // end of if foresthighlands


                 //******************************************************************
                 //   Marshfield CC - marshfieldcc
                 //******************************************************************
                 //
                 if (club.equals( "marshfieldcc" )) {

                     found = true;        // club found

                     webid = entity_id;     // webid is located in the entity_id field for this club

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         posid = mNum;

                         while (posid.length() < 8) {
                             posid = "0" + posid;
                         }

                         primary = mNum.substring(mNum.length() - 1);

                         if (mNum.endsWith("-000")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 gender = "M";
                                 mtype = "Primary Male";
                             }

                         } else if (mNum.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 gender = "F";
                                 mtype = "Spouse Female";
                             }

                         } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                    mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Dependent Female";
                             } else {
                                 gender = "M";
                                 mtype = "Dependent Male";
                             }
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                         if (mship.equalsIgnoreCase("Employee") || mship.startsWith("Family") || mship.startsWith("Honor") || mship.startsWith("Intermediate") || mship.equalsIgnoreCase("Par/Child Under 16") ||
                             mship.equalsIgnoreCase("Senior Full Play") || mship.equalsIgnoreCase("Special Senior Fam") || mship.equalsIgnoreCase("Special Senior Full") || mship.equalsIgnoreCase("Super Senior Full") ||
                             mship.startsWith("Trial")) {

                             mship = "Full";

                         } else if (mship.startsWith("Junior") || mship.equalsIgnoreCase("Unaffiliated Junior")) {

                             mship = "Junior";

                         } else if (mship.equalsIgnoreCase("Provisional member")) {

                             mship = "Provisional";

                         } else if (mship.equalsIgnoreCase("Senior Limited Play") || mship.equalsIgnoreCase("Special Senior Limit") || mship.equalsIgnoreCase("Super Senior Limited")) {

                             mship = "Limited Play";

                         } else if (mship.startsWith("Social") || mship.equalsIgnoreCase("Special Social") || mship.equalsIgnoreCase("Wait/List Applicant")) {

                             mship = "Social";

                         } else {
                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("UNKNOWN OR NON-GOLF MEMBERSHIP TYPE (" + mship + ") - SKIPPED", club, true);
                         }


                     }
                 }      // end of if marshfieldcc


                 //******************************************************************
                 //   Sugar Valley GC - sugarvalleygc
                 //******************************************************************
                 //
                 if (club.equals( "sugarvalleygc" )) {

                     found = true;        // club found

                     webid = entity_id;     // webid is located in the entity_id field for this club

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         primary = mNum.substring(mNum.length() - 1);

                         if (mNum.endsWith("-000")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 gender = "M";
                                 mtype = "Primary Male";
                             }

                         } else if (mNum.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 gender = "F";
                                 mtype = "Spouse Female";
                             }

                         } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                    mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Dependent Female";
                             } else {
                                 gender = "M";
                                 mtype = "Dependent Male";
                             }
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                         if (mship.equalsIgnoreCase("Leave of Absence") || mship.equalsIgnoreCase("House")) {
                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE (" + mship + ") - SKIPPED", club, true);
                         }

                     }
                 }      // end of if sugarvalleygc


                 //******************************************************************
                 //   Broadmoor GC - broadmoorgolfclub
                 //******************************************************************
                 //
                 if (club.equals( "broadmoorgolfclub" )) {

                     found = true;        // club found

                     //webid = entity_id;     // REMOVED due to there being duplicate entity_ids in the file

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         if (mi.equals("&") || mi.startsWith("(")) {
                             mi = "";
                         }

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         primary = mNum.substring(mNum.length() - 1);

                         if (gender.equals("") || gender.equals("?")) {
                             gender = "M";
                         }

                         if (mNum.endsWith("-000")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 gender = "M";
                                 mtype = "Primary Male";
                             }

                         } else if (mNum.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 gender = "F";
                                 mtype = "Spouse Female";
                             }

                         } else if (mNum.endsWith("-002") || mNum.endsWith("-003") || mNum.endsWith("-004") || mNum.endsWith("-005") ||
                                    mNum.endsWith("-006") || mNum.endsWith("-007") || mNum.endsWith("-008") || mNum.endsWith("-009")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Dependent Female";
                             } else {
                                 gender = "M";
                                 mtype = "Dependent Male";
                             }
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                         if (mship.startsWith("30-34") || mship.startsWith("35 & Over") || mship.equalsIgnoreCase("Under 30 Non-Stock Member")) {
                             mship = "Under 40";
                         } else if (mship.startsWith("Adv. Int")) {
                             mship = "Advanced Intermediate";
                         } else if (mship.equalsIgnoreCase("Honorary")) {
                             mship = "Honorary";
                         } else if (mship.equalsIgnoreCase("Lifetime")) {
                             mship = "Lifetime Golf";
                         } else if (mship.equalsIgnoreCase("New Lifetime")) {
                             mship = "Lifetime";
                         } else if (mship.equalsIgnoreCase("Non-Resident")) {
                             mship = "Non-Resident";
                         } else if (mship.equalsIgnoreCase("Senior Social Golf")) {
                             mship = "Senior Social Golf";
                         } else if (mship.startsWith("Men S/H") || mship.startsWith("W. S/H") || mship.startsWith("Women S/H")) {

                             if (mtype.startsWith("Dependent")) {
                                 mship = "Junior";
                             } else if (mtype.endsWith("Female")) {
                                 mship = "Member Women";
                             } else {
                                 mship = "Member Men";
                             }
                         } else {

                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("UNKNOWN OR NON-GOLF MEMBERSHIP TYPE", club, true);
                         }

                     }
                 }      // end of if broadmoorgolfclub


                 //******************************************************************
                 //   Glen View Club - glenviewclub
                 //******************************************************************
                 //
                 if (club.equals( "glenviewclub" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         if (mi.equals("&") || mi.startsWith("(")) {
                             mi = "";
                         }

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         if (mship.startsWith("Junior")) {
                             mship = "Junior";
                         } else if (mship.startsWith("Non-Resident")) {
                             mship = "Non-Resident";
                         } else if (mship.startsWith("Social") || mship.equalsIgnoreCase("Super Senior - Social")) {
                             mship = "Social";
                         } else if (mship.equalsIgnoreCase("Guest") || mship.equalsIgnoreCase("Inactive") || mship.equalsIgnoreCase("Shooting")) {

                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE", club, true);
                         }

                         if (primary.equals("0")) {
                             gender = "M";
                             mtype = "Class A Male";
                         } else if (primary.equals("1")) {
                             gender = "F";
                             mtype = "Class AA Female";
                         } else {
                             mtype = "Dependent";
                         }

                         mNum = mNum.substring(0, mNum.length() - 4);

                     }
                 }      // end of if glenviewclub


                 //******************************************************************
                 //   Park Meadows CC - parkmeadowscc
                 //******************************************************************
                 //
                 if (club.equals( "parkmeadowscc" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         if (mNum.indexOf("-") != -1) {
                             mNum = mNum.substring(0, mNum.length() - 4);
                         }

                         if (mship.equalsIgnoreCase("Social")) {
                             mship = "Social";
                         // } else if (!mship.startsWith("Employee") && !mship.equalsIgnoreCase("House Account")) {
                         } else if (!mship.equalsIgnoreCase("House Account")) {
                             mship = "Golf";
                         } else {
                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("NON-GOLF MEMBERSHIP TYPE", club, true);
                         }

                         if (memid.endsWith("-000")) {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 mtype = "Primary Male";
                             }

                         } else if (memid.endsWith("-001")) {

                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 mtype = "Spouse Female";
                             }

                         } else {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Dependent Female";
                             } else {
                                 mtype = "Dependent Male";
                             }
                         }

                     }
                 }      // end of if parkmeadowscc


                 //******************************************************************
                 //   Hillwood CC - hillwoodcc
                 //******************************************************************
                 //
                 if (club.equals( "hillwoodcc" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED (Note: Webid located in the entity_id field)", club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         // Reduce -00# dash-value to -# instead, where # is the primary indicator
                         if (memid.indexOf("-") != -1) {
                             memid = memid.substring(0, memid.length() - 3) + memid.substring(memid.length() - 1);
                         }

                         // If -0, remove the dash-value from the username
                         if (memid.endsWith("-0")) {
                             memid = memid.substring(0, memid.length() - 2);
                         }

                         mNum = memid;

                         if (mNum.indexOf("-") != -1) {
                             mNum = mNum.substring(0, mNum.length() - 2);
                         }

                         if (mship.equalsIgnoreCase("Honorary")) {
                             mship = "Honorary";
                         } else if (mship.equalsIgnoreCase("Intermediate")) {
                             mship = "Intermediate";
                         } else if (mship.equalsIgnoreCase("Junior")) {
                             mship = "Junior";
                         } else if (mship.equalsIgnoreCase("Non-Resident")) {
                             mship = "Non Resident";
                         } else if (mship.equalsIgnoreCase("Pre-Resident")) {
                             mship = "Pre-Resident";
                         } else if (mship.equalsIgnoreCase("Resident")) {
                             mship = "Resident";
                         } else if (mship.startsWith("Senior - A")) {
                             mship = "Senior A";
                         } else if (mship.startsWith("Senior - R")) {
                             mship = "Senior Resident R";
                         } else if (mship.startsWith("Pre-Social")) {
                             mship = "Pre-Social";
                         } else if (mship.startsWith("Social")) {
                             mship = "Social";
                         } else if (mship.startsWith("Super Senior")) {
                             mship = "Super Senior";
                         } else if (mship.startsWith("Whitworth - Athletic")) {
                             mship = "Whitworth - Athletic";
                         } else {
                             skip = true;              // skip record if webid not provided
                             SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                         }

                         if (memid.endsWith("-1")) {

                             if (gender.equalsIgnoreCase("M")) {
                                 mtype = "Spouse Male";
                             } else {
                                 mtype = "Spouse Female";
                             }

                         } else if (memid.endsWith("-2") || memid.endsWith("-3") || memid.endsWith("-4") || memid.endsWith("-5")
                                 || memid.endsWith("-6") || memid.endsWith("-7") || memid.endsWith("-8") || memid.endsWith("-9")) {

                             Calendar cal = new GregorianCalendar();        // get todays date
                             int year = cal.get(Calendar.YEAR);
                             int month = cal.get(Calendar.MONTH)+1;
                             int day = cal.get(Calendar.DAY_OF_MONTH);

                             year = year - 18;                              // date to determine if < 18 yrs old

                             int date18 = (year * 10000) + (month * 100) + day;

                             year = year - 8;                              // date to determine if < 26 yrs old

                             int date26 = (year * 10000) + (month * 100) + day;

                             if (birth > date18) {
                                 mtype = "Dependent up to 17";
                             } else if (birth > date26) {
                                 mtype = "Dependent 18 to 25";
                             } else {
                                 mtype = "Dependent";
                             }

                         } else {

                             if (gender.equalsIgnoreCase("F")) {
                                 mtype = "Primary Female";
                             } else {
                                 mtype = "Primary Male";
                             }

                         }

                     }
                 }      // end of if hillwoodcc



                 //******************************************************************
                 //   Windstar Club - windstarclub
                 //******************************************************************
                 //
                 if (club.equals( "windstarclub" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (memid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         //
                         //  Convert the mship
                         //
                         if (mship.equalsIgnoreCase("Golf Equity") || mship.equalsIgnoreCase("Golf Non Resident") || mship.equalsIgnoreCase("Golf Non-Equity Member") ||
                             mship.equalsIgnoreCase("Golf Resident Zero Buy In") || mship.equalsIgnoreCase("Golf - $1.00 Equity (Resident)") ||
                             mship.equalsIgnoreCase("Junior Golf")) {

                            mship = "Full Golf";

                         } else if (mship.startsWith("Golf Socia")) {

                            mship = "Social Golf";

                         } else if (mship.equalsIgnoreCase("Executive Golf") || mship.equalsIgnoreCase("Junior Executive Golf")) {

                            mship = "Executive Golf";

                         } else if (!mship.equalsIgnoreCase("Tenant Trans Golf") && !mship.equalsIgnoreCase("Tenant Golf")) {

                             skip = true;              // skip record if non-golf or unknown mship
                             SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                         }

                         if (primary.equals("0") || primary.equals("1")) {

                            if (primary.equals("0")) {

                                if (gender.equals("")) {
                                    gender = "M";
                                }

                            } else if (primary.equals("1")) {

                                memid += "A";

                                if (gender.equals("")) {
                                    gender = "F";
                                }
                            }

                            if (gender.equalsIgnoreCase("F")) {
                                mtype = "Adult Female";
                            } else {
                                mtype = "Adult Male";
                            }

                         } else {

                             mtype = "Dependent";

                             if (primary.equals("2")) {
                                 memid += "B";
                             } else if (primary.equals("3")) {
                                 memid += "C";
                             } else if (primary.equals("4")) {
                                 memid += "D";
                             } else if (primary.equals("5")) {
                                 memid += "E";
                             } else if (primary.equals("6")) {
                                 memid += "F";
                             } else if (primary.equals("7")) {
                                 memid += "G";
                             } else if (primary.equals("8")) {
                                 memid += "H";
                             } else if (primary.equals("9")) {
                                 memid += "I";
                             }
                         }

                         lname = toTitleCase(lname);

                     }
                 }      // end of if windstarclub



                 //******************************************************************
                 //   CC of York - ccyork
                 //******************************************************************
                 //
                 if (club.equals( "ccyork" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     /*if (mship.equals( "" )) {

                         skip = true;            // skip record if mship not one of above
                         SystemUtils.logErrorToFile("MSHIP MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else*/
    /*
                     if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (memid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else {

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         mNum = memid;

                         if (mNum.equals(mNum_curr)) {
                             if (primary.equals("2")) {
                                 depCount++;
                             }
                         } else {
                             depCount = 0;
                             mNum_curr = mNum;
                         }

                         if (mship.equals("1") || mship.equals("2") || mship.equals("3") || mship.equals("4")
                          || mship.equals("20") || mship.equals("21") || mship.equals("22") || mship.equals("23")) {
                             mship = "Resident Member";
                         } else if (mship.equals("14") || mship.equals("15")) {
                             mship = "Corporate Member";
                         } else if (mship.equals("5") || mship.equals("6")) {
                             mship = "Junior Sporting Member";
                         } else if (mship.equals("8") || mship.equals("26")) {
                             mship = "Non-Resident Member";
                         } else if (mship.equals("31") || mship.equals("32")) {
                             mship = "Regional Member";
                         } else if (mship.equals("11") || mship.equals("13") || mship.equals("24") || mship.equals("25")) {
                             mship = "Resident Junior Member";
                         } else if (mship.equals("18") || mship.equals("19")) {
                             mship = "Senior Member";
                         } else if (mship.equals("7") || mship.equals("9")) {
                             mship = "Sporting Member";
                         } else if (mship.equals("17")) {
                             mship = "Surviving Spouse Member";
                         } else if (mship.equals("16")) {
                             mship = "Widow Member";
                         } else {
                             skip = true;
                             SystemUtils.logErrorToFile("NON-GOLF OR UNKNOWN MEMBERSHIP TYPE (" + mship + ")", club, true);
                         }

                         if (primary.equals("0") || primary.equals("1")) {

                            if (primary.equals("0")) {

                                if (gender.equals("")) {
                                    gender = "M";
                                }

                            } else if (primary.equals("1")) {

                                memid += "A";

                                if (gender.equals("")) {
                                    gender = "F";
                                }
                            }

                            if (gender.equalsIgnoreCase("F")) {
                                mtype = "Adult Female";
                            } else {
                                mtype = "Adult Male";
                            }

                         } else if (primary.equals("2")) {

                             if (depCount == 1) {
                                 memid += "B";
                             } else if (depCount == 2) {
                                 memid += "C";
                             } else if (depCount == 3) {
                                 memid += "D";
                             } else if (depCount == 4) {
                                 memid += "E";
                             } else if (depCount == 5) {
                                 memid += "F";
                             } else if (depCount == 6) {
                                 memid += "G";
                             } else if (depCount == 7) {
                                 memid += "H";
                             } else if (depCount == 8) {
                                 memid += "I";
                             }

                             Calendar cal = new GregorianCalendar();        // get todays date
                             int year = cal.get(Calendar.YEAR);
                             int month = cal.get(Calendar.MONTH)+1;
                             int day = cal.get(Calendar.DAY_OF_MONTH);

                             year = year - 18;                              // date to determine if < 18 yrs old

                             int date18 = (year * 10000) + (month * 100) + day;

                             if (birth > date18) {
                                 mtype = "Junior 17 and Under";
                             } else {
                                 mtype = "Dependent 18 - 25";
                             }
                         }

                         fname = toTitleCase(fname);
                         lname = toTitleCase(lname);

                     }
                 }      // end of if ccyork



                /*
                 //******************************************************************
                 //   ForeTees - Flexscape DEMO site (demoflex) for testing ForeTees Premiere Website
                 //******************************************************************
                 //
                 if (club.equals( "demoflex" )) {

                     found = true;        // club found

                     //
                     //  Determine if we should process this record
                     //
                     if (webid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("WEBID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else if (memid.equals("")) {

                         skip = true;              // skip record if webid not provided
                         SystemUtils.logErrorToFile("MEMID MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + work, club, true);

                     } else {

                         mship = "Golf";

                         useWebid = true;

                         posid = memid;

                         while (memid.startsWith("0")) {
                             memid = memid.substring(1);
                         }

                         // Reduce -00# dash-value to -# instead, where # is the primary indicator
                         if (memid.indexOf("-") != -1) {
                             memid = memid.substring(0, memid.length() - 3) + memid.substring(memid.length() - 1);
                         }

                         // If -0, remove the dash-value from the username
                         if (memid.endsWith("-0")) {
                             memid = memid.substring(0, memid.length() - 2);
                         }

                         mNum = memid;

                         if (mNum.indexOf("-") != -1) {
                             mNum = mNum.substring(0, mNum.length() - 2);
                         }

                         //  Set mtype

                         if (gender.equals("")) {

                             gender = "M";

                             if (primary.equals("1")) {

                                 gender = "F";
                             }
                         }

                         if (primary.equals("0") || primary.equals("1")) {

                            if (gender.equals("F")) {

                                mtype = "Adult Female";
                                memid = memid + "A";

                            } else {

                                mtype = "Adult Male";
                            }

                         } else {

                             mtype = "Dependent";

                             memid = memid + "-" +primary;
                         }

                         lname = toTitleCase(lname);

                     }
                 }      // end of if demoflex
                 *
                 */
/*


                 //*********************************************
                 //  End of club specific processing
                 //*********************************************

              } else {

                  skip = true;              // skip record if memid or name not provided
                  SystemUtils.logErrorToFile("USERNAME/NAME MISSING - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);

              }                 // end of IF minimum requirements met (memid, etc)



              //
              //******************************************************************
              //  Common processing - add or update the member record
              //******************************************************************
              //
              if (skip == false && found == true && !fname.equals("") && !lname.equals("")) {

                 //
                 //   now determine if we should update an existing record or add the new one
                 //
                 memid_old = "";
                 fname_old = "";
                 lname_old = "";
                 mi_old = "";
                 mship_old = "";
                 mtype_old = "";
                 email_old = "";
                 mNum_old = "";
                 posid_old = "";
                 phone_old = "";
                 phone2_old = "";
                 birth_old = 0;
                 webid_old = "";

                 memidChanged = false;
                 changed = false;

                 //
                 //  Truncate the string values to avoid sql error
                 //
                 if (!mi.equals( "" )) {       // if mi specified

                    mi = truncate(mi, 1);           // make sure it is only 1 char
                 }
                 if (!memid.equals( "" )) {

                    memid = truncate(memid, 15);
                 }
                 if (!password.equals( "" )) {

                    password = truncate(password, 15);
                 }
                 if (!lname.equals( "" )) {

                    lname = truncate(lname, 20);
                 }
                 if (!fname.equals( "" )) {

                    fname = truncate(fname, 20);
                 }
                 if (!mship.equals( "" )) {

                    mship = truncate(mship, 30);
                 }
                 if (!mtype.equals( "" )) {

                    mtype = truncate(mtype, 30);
                 }
                 if (!email.equals( "" )) {

                    email = truncate(email, 50);
                 }
                 if (!email2.equals( "" )) {

                    email2 = truncate(email2, 50);
                 }
                 if (!mNum.equals( "" )) {

                    mNum = truncate(mNum, 15);
                 }
                 if (!ghin.equals( "" )) {

                    ghin = truncate(ghin, 16);
                 }
                 if (!bag.equals( "" )) {

                    bag = truncate(bag, 12);
                 }
                 if (!posid.equals( "" )) {

                    posid = truncate(posid, 15);
                 }
                 if (!phone.equals( "" )) {

                    phone = truncate(phone, 24);
                 }
                 if (!phone2.equals( "" )) {

                    phone2 = truncate(phone2, 24);
                 }
                 if (!suffix.equals( "" )) {

                    suffix = truncate(suffix, 4);
                 }
                 if (!webid.equals( "" )) {

                     webid = truncate(webid, 15);
                 }


                 if (useWebid == false) {            // use webid to locate member?

                     pstmt2 = con.prepareStatement (
                             "SELECT * FROM member2b WHERE username = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, memid);

                 } else {                            // use webid

                     pstmt2 = con.prepareStatement (
                          "SELECT * FROM member2b WHERE webid = ?");

                     pstmt2.clearParameters();
                     pstmt2.setString(1, webid);
                 }

                 rs = pstmt2.executeQuery();            // execute the prepared stmt

                 if(rs.next()) {

                    memid_old = rs.getString("username");
                    lname_old = rs.getString("name_last");
                    fname_old = rs.getString("name_first");
                    mi_old = rs.getString("name_mi");
                    mship_old = rs.getString("m_ship");
                    mtype_old = rs.getString("m_type");
                    email_old = rs.getString("email");
                    mNum_old = rs.getString("memNum");
                    birth_old = rs.getInt("birth");
                    posid_old = rs.getString("posid");
                    phone_old = rs.getString("phone1");
                    phone2_old = rs.getString("phone2");
                    webid_old = rs.getString("webid");
                 }
                 pstmt2.close();              // close the stmt

                 if (!fname_old.equals( "" )) {            // if member found

                    changed = false;                       // init change indicator

                    memid_new = memid_old;

                    if (club.equals("foresthighlands")) {       // don't change usernames (initial setup, can remove later if desired)
                        memid = memid_old;
                    }

                    if (!memid.equals( memid_old )) {       // if username has changed

                       memid_new = memid;                   // use new memid
                       changed = true;
                       memidChanged = true;
                    }

                    lname_new = lname_old;

                    if (!lname.equals( "" ) && !lname_old.equals( lname )) {

                       lname_new = lname;         // set value from Flexscape record
                       changed = true;
                    }

                    fname_new = fname_old;

                    fname = fname_old;         // DO NOT change first names

/*
                    if (!fname.equals( "" ) && !fname_old.equals( fname )) {

                       fname_new = fname;         // set value from Flexscape record
                       changed = true;
                    }
*/
    /*
                    if (club.equals("foresthighlands")) {
                        mi = mi_old;
                    }

                    mi_new = mi_old;

                    if (!mi.equals( "" ) && !mi_old.equals( mi )) {

                       mi_new = mi;         // set value from Flexscape record
                       changed = true;
                    }

                    mship_new = mship_old;

                    if (!mship_old.equals( mship )) {   // if the mship has changed

                        mship_new = mship;         // set value from Flexscape record
                        changed = true;
                    }

                    mtype_new = mtype_old;

                    if (!mtype.equals( "" ) && !mtype_old.equals( mtype )) {
                        mtype_new = mtype;         // set value from Flexscape record
                        changed = true;
                    }

                    if (birth > 0 && birth != birth_old) {

                       birth_new = birth;         // set value from Flexscape record
                       changed = true;
                    }

                    posid_new = posid_old;

                    if (!posid.equals( "" ) && !posid_old.equals( posid )) {

                       posid_new = posid;         // set value from Flexscape record
                       changed = true;
                    }

                    phone_new = phone_old;

                    if (!phone.equals( "" ) && !phone_old.equals( phone )) {

                       phone_new = phone;         // set value from Flexscape record
                       changed = true;
                    }

                    phone2_new = phone2_old;

                    if (!phone2.equals( "" ) && !phone2_old.equals( phone2 )) {

                       phone2_new = phone2;         // set value from Flexscape record
                       changed = true;
                    }

                    email_new = email_old;        // do not change emails

                    if (club.equals("coloradospringscountryclub") || club.equals("oceanreef") || club.equals("hillwoodcc")) {
                        email = email_old;
                    }

                    if (!email.equals( "" ) && !email_old.equals( email )) {

                       email_new = email;         // set value from Flexscape record
                       changed = true;
                    }

                    email2_new = email2_old;

                    if (club.equals("coloradospringscountryclub") || club.equals("oceanreef") || club.equals("hillwoodcc")) {
                        email2 = email2_old;
                    }

                    if (!email2.equals( "" ) && !email2_old.equals( email2 )) {

                       email2_new = email2;         // set value from Flexscape record
                       changed = true;
                    }

                    webid_new = webid_old;

                    if (!webid_old.equals( webid )) {   // if the mship has changed

                        webid_new = webid;         // set value from Flexscape record
                        changed = true;
                    }

                    // don't allow both emails to be the same
                    if (email_new.equalsIgnoreCase(email2_new)) email2_new = "";

                    //
                    //  NOTE:  mNums can change for this club!!
                    //
                    //         DO NOT change the webid!!!!!!!!!
                    //
                    mNum_new = mNum_old;

                    if (!mNum.equals( "" ) && !mNum_old.equals( mNum )) {

                       mNum_new = mNum;         // set value from Flexscape record

                       //
                       //  mNum changed - change it for all records that match the old mNum.
                       //                 This is because most dependents are not included in web site roster,
                       //                 but are in our roster.
                       //
                       if (club.equals("coloradospringscountryclub") || club.equals("oceanreef")) {

                           pstmt2 = con.prepareStatement (
                           "UPDATE member2b SET memNum = ? " +
                           "WHERE memNum = ?");

                           pstmt2.clearParameters();        // clear the parms
                           pstmt2.setString(1, mNum_new);
                           pstmt2.setString(2, mNum_old);
                           pstmt2.executeUpdate();

                           pstmt2.close();              // close the stmt
                       }
                    }


                    //
                    //  Update our record
                    //
                    if (changed == true) {

                       modCount++;             // count records changed
                    }

                    try {

                       pstmt2 = con.prepareStatement (
                       "UPDATE member2b SET username = ?, name_last = ?, name_first = ?, " +
                       "name_mi = ?, m_ship = ?, m_type = ?, email = ?, " +
                       "memNum = ?, birth = ?, posid = ?, phone1 = ?, " +
                       "phone2 = ?, inact = 0, last_sync_date = now(), gender = ?, webid = ? " +
                       "WHERE username = ?");

                       pstmt2.clearParameters();        // clear the parms
                       pstmt2.setString(1, memid_new);
                       pstmt2.setString(2, lname_new);
                       pstmt2.setString(3, fname_new);
                       pstmt2.setString(4, mi_new);
                       pstmt2.setString(5, mship_new);
                       pstmt2.setString(6, mtype_new);
                       pstmt2.setString(7, email_new);
                       pstmt2.setString(8, mNum_new);
                       pstmt2.setInt(9, birth_new);
                       pstmt2.setString(10, posid_new);
                       pstmt2.setString(11, phone_new);
                       pstmt2.setString(12, phone2_new);
                       pstmt2.setString(13, gender);
                       pstmt2.setString(14, webid_new);
                       pstmt2.setString(15, memid_old);
                       pstmt2.executeUpdate();

                       pstmt2.close();              // close the stmt

                    }
                    catch (Exception e9) {

                       //errorMsg = errorMsg + " Error updating record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e9.getMessage();   // build msg
                       //SystemUtils.logError(errorMsg);                                                  // log it
                       errorMsg = "Error in Common_sync.flexSync: ";
                       SystemUtils.logErrorToFile("UPDATE MYSQL ERROR! - SKIPPED - name: " + lname + ", " + fname + " - " + memid + " - ERR: " + e9.toString(), club, true);
                    }


                    //
                    //  Now, update other tables if the username has changed
                    //
                    if (memidChanged == true) {

                       StringBuffer mem_name = new StringBuffer( fname_new );       // get the new first name

                       if (!mi_new.equals( "" )) {
                          mem_name.append(" " +mi_new);               // new mi
                       }
                       mem_name.append(" " +lname_new);               // new last name

                       String newName = mem_name.toString();          // convert to one string

                       Admin_editmem.updTeecurr(newName, memid_new, memid_old, con);      // update teecurr with new values

                       Admin_editmem.updTeepast(newName, memid_new, memid_old, con);      // update teepast with new values

                       Admin_editmem.updLreqs(newName, memid_new, memid_old, con);        // update lreqs with new values

                       Admin_editmem.updPartner(memid_new, memid_old, con);        // update partner with new values

                       Admin_editmem.updEvents(newName, memid_new, memid_old, con);        // update evntSignUp with new values

                       Admin_editmem.updLessons(newName, memid_new, memid_old, con);       // update the lesson books with new values

                       Admin_editmem.updDemoClubs(memid_new, memid_old, con);              // update demo_clubs_usage with new values
                    }

                    // If organization_id is greater than 0, Dining system is in use.  Push updates to this member's record over to the dining system database
                    if (Utilities.getOrganizationId(con) > 0) {
                        Admin_editmem.updDiningDB(memid_new, con);
                    }


                 } else {

                    //
                    //  New member - first check if name already exists
                    //
                    boolean dup = false;

                    pstmt2 = con.prepareStatement (
                             "SELECT username FROM member2b WHERE name_last = ? AND name_first = ? AND name_mi = ?");

                    pstmt2.clearParameters();
                    pstmt2.setString(1, lname);
                    pstmt2.setString(2, fname);
                    pstmt2.setString(3, mi);
                    rs = pstmt2.executeQuery();            // execute the prepared stmt

                    if (rs.next()) {

                       dup = true;
                    }
                    pstmt2.close();              // close the stmt

                    if (dup == false) {

                       //
                       //  New member - add it
                       //
                       newCount++;             // count records added

                       try {

                          pstmt2 = con.prepareStatement (
                             "INSERT INTO member2b (username, password, name_last, name_first, name_mi, " +
                             "m_ship, m_type, email, count, c_hancap, g_hancap, wc, message, emailOpt, memNum, " +
                             "ghin, locker, bag, birth, posid, msub_type, email2, phone1, phone2, name_pre, name_suf, " +
                             "webid, last_sync_date, gender) " +
                             "VALUES (?,?,?,?,?,?,?,?,0,?,?,'','',1,?,?,'',?,?,?,'',?,?,?,'',?,?,now(),?)");

                          pstmt2.clearParameters();        // clear the parms
                          pstmt2.setString(1, memid);        // put the parm in stmt
                          pstmt2.setString(2, password);
                          pstmt2.setString(3, lname);
                          pstmt2.setString(4, fname);
                          pstmt2.setString(5, mi);
                          pstmt2.setString(6, mship);
                          pstmt2.setString(7, mtype);
                          pstmt2.setString(8, email);
                          pstmt2.setFloat(9, c_hcap);
                          pstmt2.setFloat(10, u_hcap);
                          pstmt2.setString(11, mNum);
                          pstmt2.setString(12, ghin);
                          pstmt2.setString(13, bag);
                          pstmt2.setInt(14, birth);
                          pstmt2.setString(15, posid);
                          pstmt2.setString(16, email2);
                          pstmt2.setString(17, phone);
                          pstmt2.setString(18, phone2);
                          pstmt2.setString(19, suffix);
                          pstmt2.setString(20, webid);
                          pstmt2.setString(21, gender);
                          pstmt2.executeUpdate();          // execute the prepared stmt

                          pstmt2.close();              // close the stmt

                       }
                       catch (Exception e8) {

                          //errorMsg = errorMsg + " Error adding record (#" +rcount+ ") for " +club+ ", line = " +line+ ": " + e8.getMessage();   // build msg
                          //SystemUtils.logError(errorMsg);                                                  // log it
                          errorMsg = "Error in Common_sync.flexSync: ";
                          SystemUtils.logErrorToFile("INSERT MYSQL ERROR! - SKIPPED - name: " + lname + ", " + fname + " - " + memid + " - ERR: " + e8.toString(), club, true);
                       }

                    } else {      // Dup name

               //        errorMsg = errorMsg + " Duplicate Name found, name = " +fname+ " " +lname+ ", record #" +rcount+ " for " +club+ ", line = " +line;   // build msg
               //        SystemUtils.logError(errorMsg);                                                  // log it
                       errorMsg = "Error in Common_sync.flexSync: ";
                       SystemUtils.logErrorToFile("DUPLICATE NAME! - SKIPPED - name: " + lname + ", " + fname + " - " + memid, club, true);
                    }
                 }
              }   // end of IF skip
           }   // end of IF record valid (enough tokens)
         }   // end of IF header row

      }   // end of while

      //
      //  Done with this file for this club - now set any members that were excluded from this file inactive
      //
      if (found == true) {       // if we processed this club

         // Unless manually force-overridden, check to see if more than 20 members are about to be set inactive, and if so, log an error and SKIP setting them inactive.
         //if (!forceSetInact) setInactCount = checkSyncSetInactCount(con);

         if (setInactCount < 200) {

              // Set anyone inactive that didn't sync, but has at one point.
              pstmt2 = con.prepareStatement (
                      "UPDATE member2b SET inact = 1 " +
                      "WHERE last_sync_date != DATE(now()) AND last_sync_date != '0000-00-00' AND inact = 0");

              pstmt2.clearParameters();        // clear the parms
              pstmt2.executeUpdate();

              pstmt2.close();              // close the stmt

         } else {
             Utilities.logDebug("BSK", "flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");    // Temp to make it easier to spot
             Utilities.logError("flexSync - Roster Sync for (" + club + ") was about to set (" + setInactCount + ") members inactive. Action skipped. Contact club to verify.");
         }


          //
          //  Roster File Found for this club - make sure the roster sync indicator is set in the club table
          //
          //setRSind(con, club);
      }

   }
   catch (Exception e3) {

      errorMsg = errorMsg + " Error processing roster (record #" +rcount+ ") for " +club+ ", line = " +line+ ": " + e3.getMessage();   // build msg
      SystemUtils.logError(errorMsg);                                                  // log it
      errorMsg = "Error in Common_sync.flexSync: ";
   }

 }
*/

 // *********************************************************
 //  Return a string with the specified length from a possibly longer field
 // *********************************************************

 private final static String truncate( String s, int slength ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [slength];


      if (slength < ca.length) {       // if string is longer than allowed

         for ( int i=0; i<slength; i++ ) {
            ca2[i] = ca[i];
         } // end for

      } else {

         return (s);
      }

      return new String (ca2);

 } // end truncate


 // *********************************************************
 //  Convert Upper case names to title case (Bob P...)
 // *********************************************************

 private final static String toTitleCase( String s ) {

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


 // *********************************************************
 //  Remove dbl quotes and embedded commas from record
 //  Replace 2 dbl quotes in a row with a ?
 // *********************************************************

 private final static String cleanRecord2( String s ) {

      char[] ca = s.toCharArray();
      char[] ca2 = new char [ca.length];
      char letter;
      char lastLetter = 'a';          // init for complier
      int i2 = 0;
      boolean inquotes = false;

      for ( int i=0; i<ca.length; i++ ) {
         letter = ca[i];
         if ( letter != '"' ) {            // if not a quote
            if ( letter == ',' ) {         // is it a comma?
               if (inquotes == false) {    // skip commas while in between quotes
                  ca2[i2] = letter;        // save good letter
                  i2++;
               }
            } else {                       // not a quote or a comma - keep it

               ca2[i2] = letter;        // save good letter
               i2++;
            }

         } else {                      // quote - skip it or replace it, and check for 'between quotes'

            if (lastLetter == '"') {     // if 2 quotes in a row

               ca2[i2] = '?';            // replace with a '?'
               i2++;
            }

            if (inquotes == true) {

               inquotes = false;       // exit 'between quotes' mode

            } else {

               inquotes = true;        // enter 'between quotes' mode
            }
         }
         lastLetter = letter;          // save last letter
      }

      char[] ca3 = new char [i2];

      for ( int i=0; i<i2; i++ ) {
         letter = ca2[i];        // get from first array
         ca3[i] = letter;             // move to correct size array
      }

      return new String (ca3);

 } // end cleanRecord2


 private final static String cleanRecord4( String s ) {

     while (s.contains(",,")) {
         s = s.replace(",,", ",?,");
     }

     if (s.endsWith(",")) {
         s += "?";
     }

     return s;

 }// end cleanRecord4


    public static Map<String,Object> performSSO(HttpServletRequest req, HttpServletResponse resp, int activity_id, final String user, final String username, final String club, final String caller, final String mship, final String mtype, final String name, final String wc, final String remote_ip, final long benchmark_start, Connection con)
            throws ServletException, IOException {

        /*
         * NOTE:  resp object could be NULL!!
         * 
         * Once here we already know the caller, club and user are all valid and active
         * Test for an existing session and if not found, create one
        */

        int result = 0;
        String resp_message = "";
        String uuid = null;
        
        

        if (req.getParameter("logout") != null) {

            // we're here to detroy the users session

            HttpSession session = req.getSession(false);

            if (session == null) {

                // there was no session to release
                result = 0;
                resp_message = "No existing session found for user " + user;

            } else {

                //
                //  Release the connection if it exists
                //
                Connection sess_con = null;

                //
                // Get the connection holder saved in the session object
                //
                ConnHolder holder = (ConnHolder) session.getAttribute("connect");

                if (holder != null) {

                    sess_con = holder.getConn();      // get the connection
                }

                if (sess_con != null) {

                    // adjust the number of users logged in
                    //countLogout(con);
            /*
                    // abandon any unfinished transactions
                    try { con.rollback(); }
                    catch (Exception ignore) {}
            */
                    // close/release the connection
                    try { sess_con.close(); }
                    catch (Exception ignore) {}

                }

                // clear the users session variables
                session.removeAttribute("user");
                session.removeAttribute("club");
                session.removeAttribute("connect");

                // end the users session
                session.invalidate();

                result = 1;
                resp_message = "The existing session was found and detroyed for user " + user;
            }

        } else {


            PreparedStatement pstmt = null;
            ResultSet rs = null;

            int organization_id = Utilities.getOrganizationId(con);
            int mobile = (req.getParameter("mobile") != null) ? 1 : 0;

            String zipcode = "";

            //
            //  Get club's POS Type for _slot processing
            //
            String posType = Login.getPOS(con);

            //
            //  Get TLT indicator
            //
            int tlt = (Utilities.isNotificiationClub(con)) ? 1 : 0;

            try {

                pstmt = con.prepareStatement (
                  "SELECT rsync, seamless, zipcode, primaryif, mnum, mapping, stripzero, seamless_caller, stripalpha, stripdash " +
                  "FROM club5");

                pstmt.clearParameters();
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    zipcode = rs.getString("zipcode");
                }

                pstmt.close();

            } catch (Exception exc) {

                //invalidRemote(new_skin, default_activity_id, "Unable to Connect to Club Database for options. Error: " + exc.getMessage(), req, out, con);
                //return;
            }


            //
            //  Trace good logins - display parms passed for verification purposes
            //
            SystemUtils.sessionLog("Remote Login Successful: user_name from website=" +user+ ", Primary=No, mNum=No, IP=" + remote_ip + " ", user, "", club, caller, con);            // log it

            Login.recordLogin(user, "", club, remote_ip, 1);

            // Save the connection in the session block for later use.......
            HttpSession session = req.getSession(false);
            
            boolean createSession = true;

            if (session != null) {

                // user has an existing session
                String session_user = (String)session.getAttribute("user");       // get user
                String session_club = (String)session.getAttribute("club");       // get club name

                // simple sanity validation
                if (!session_user.equals(username) || !session_club.equals(club)) {

                    // clear the users session variables
                    session.removeAttribute("user");
                    session.removeAttribute("club");
                    session.removeAttribute("connect");

                    // The current session is not for the requested user.
                    // end the current session (the session will be recreated with different user below)
                    session.invalidate();
                    

                    //result = -1;
                    //resp_message = "A session mismatch has occured. Details have been logged.";

                    //Utilities.logError("*** API SSO BUG - username=" + username + ", session_user=" + session_user + ", club=" + club + ", session_club=" + session_club + ", ");

                    
                    
                } else {

                    
                    result = 1;
                    resp_message = "An existing session was found and touched for user " + user;
                    createSession = false;
                    
                }

            }
            
            if (createSession){

                //
                // No existing session - create one now
                //
                session = req.getSession(true);

                ConnHolder holder = new ConnHolder(con);      // create a new holder from ConnHolder class

                session.setAttribute("connect", holder);      // save DB connection holder
                session.setAttribute("sess_id", SESSION_ID);          // set session id for validation ("foretees")
                session.setAttribute("user", username);       // save username
                session.setAttribute("name", name);           // save members full name
                session.setAttribute("club", club);           // save club name
                session.setAttribute("caller", caller);       // save caller's name
                session.setAttribute("mship", mship);         // save member's mship type
                session.setAttribute("mtype", mtype);         // save member's mtype
                session.setAttribute("wc", wc);               // save member's w/c pref (for _slot)
                session.setAttribute("posType", posType);     // save club's POS Type
                session.setAttribute("zipcode", zipcode);     // save club's zipcode
                session.setAttribute("tlt", tlt);             // timeless tees indicator
                //session.setAttribute("mobile", mobile);       // set mobile indicator (0 = NOT, 1 = Mobile)
                session.setAttribute("activity_id", activity_id);  // activity indicator
                session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
                //session.setAttribute("new_skin", "1");        // new skin flag
                //session.setAttribute("premier_referrer", req.getHeader("referer"));       // referer

                //
                // set inactivity timer for this session
                //
                session.setMaxInactiveInterval( Login.MEMBER_TIMEOUT );

                result = 2;
                resp_message = "A new session was created for user " + user;
            }
            
            session.setAttribute("premier_referrer", req.getHeader("referer"));
            session.setAttribute("caller", caller);  
            session.setAttribute("mobile", mobile); 
            session.setAttribute("new_skin", "1");        // new skin flag
            //session.setAttribute("activity_id", activity_id); 
            
            uuid = (String) session.getAttribute("session_uuid");
            if(uuid == null){
                uuid = UUID.randomUUID().toString();
                session.setAttribute("session_uuid", uuid);
            }
            

            //
            //  Count the number of users logged in
            //
            //countLogin("mem", con);

            // new stats logging routine
            //recordLoginStat(2);

        }

        //Gson gson_obj = new Gson();

        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new HashMap<String, Object>();
        
        /*if (req.getParameter("callback") != null) {

            container_map.put(req.getParameter("callback"), response_map);

            response_map.put("valid", result);
            response_map.put("message", resp_message);
            response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
            response_map.put("serverId", ProcessConstants.SERVER_ID);

        } else {*/


            container_map.put("foreTeesSSOResp", response_map);

            response_map.put("valid", result);
            response_map.put("message", resp_message);
            response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
            response_map.put("serverId", ProcessConstants.SERVER_ID);
            response_map.put("sessionUuid", uuid);
            
        //}
        
        //return gson_obj.toJson(container_map);
           return container_map;

    }



    private Map<String, Object> getActivitities(HttpServletRequest req, HttpServletResponse resp, int activity_id, final String user, final String username, final String club, final String caller, String mship, String mtype, final long benchmark_start, Connection con)
            throws ServletException, IOException {


        Statement stmt = null;
        ResultSet rs = null;

        boolean allow = false;

        int foretees_mode = 0;
        int dining_mode = 0;
        int flxrez_staging = 0;
        int dining_staging = 0;
        int genrez_mode = 0;
        int count = 0;
        int i = 0;

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


            // only query activities is club is not is staging mode
            if (genrez_mode == 1 && flxrez_staging == 0) {

                rs = stmt.executeQuery(""
                        + "SELECT activity_id "
                        + "FROM activities "
                        + "WHERE parent_id = 0 AND enabled != 0");

                if (rs.next()) {

                    rs.last();
                    count = rs.getRow();   // get the number of activities found (excluding golf)
                }

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

            Utilities.logError("Common_webapi.getActivities() err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close();}
            catch (Exception ignore) {}
        }


        String[] activity_names = new String[count];
        int[] activity_ids = new int[count];

        // get configured flxrez activities
        try {

            // Get activity names from database
            stmt = con.createStatement();
            rs = stmt.executeQuery(""
                    + "SELECT activity_name, activity_id "
                    + "FROM activities "
                    + "WHERE parent_id = 0 AND enabled != 0 "
                    + "ORDER BY sort_by, activity_name");

            while (rs.next()) {

                activity_names[i] = rs.getString("activity_name");
                activity_ids[i] = rs.getInt("activity_id");
                i++;
            }

        } catch (Exception exc) {

            Utilities.logError("Common_webapi.getActivities: Error loading activity names. club=" + club + ", err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}
        }


        Map<String, Object> container_map = new HashMap<String, Object>();
        Map<String, Object> response_map = new LinkedHashMap<String, Object>();

        container_map.put("foreTeesActivityListResp", response_map);

        List<Map<String,Object>> activityList = new ArrayList<Map<String,Object>>();
        
        int activityCount = 0;
        
        if (foretees_mode > 0) {

            Map<String,Object> activityMap = new HashMap<String, Object>();
            activityMap.put("name", "Golf");
            activityMap.put("id", 0);
            activityMap.put("baseUrl", Utilities.getBaseUrl(req, 0, club));
            activityMap.put("externalBaseUrl", Utilities.getExternalBaseUrl(req, 0, club));
            
            activityList.add(activityMap);
            activityCount++;
        }
        
        if (dining_mode > 0 && dining_staging != 1) {
            
            Map<String,Object> activityMap = new HashMap<String, Object>();
            activityMap.put("name", "Dining");
            activityMap.put("id", ProcessConstants.DINING_ACTIVITY_ID);
            activityMap.put("baseUrl", Utilities.getBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club));
            activityMap.put("externalBaseUrl", Utilities.getExternalBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club));
            
            activityList.add(activityMap);
            activityCount++;
        }

        for (i = 0; i < activity_names.length; i++) {
            
            Map<String,Object> activityMap = new HashMap<String, Object>();
            activityMap.put("name", activity_names[i]);
            activityMap.put("id", activity_ids[i]);
            activityMap.put("baseUrl", Utilities.getBaseUrl(req, activity_ids[i], club));
            activityMap.put("externalBaseUrl", Utilities.getExternalBaseUrl(req, activity_ids[i], club));
            
            activityList.add(activityMap);
            activityCount++;
        }
        
        response_map.put("activity", activityList);
        response_map.put("activityCount", activityCount);
            
        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
        response_map.put("serverId", ProcessConstants.SERVER_ID);

        return container_map;

    }



    public Map<String, Object> getCalendarData(HttpServletRequest req, HttpServletResponse resp, int activity_id, final String user, final String username, final String club, final String caller, String mship, String mtype, final long benchmark_start, Connection con)
            throws ServletException, IOException {


        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        ResultSet rs2 = null;
        ResultSet rsev = null;

        //String clubName = SystemUtils.getClubName(con);            // get the full name of this club

        int req_month = 0;
        int req_year = 0;
        
        int app_mode = 0;
        
        if(caller.equals(ProcessConstants.FT_PREMIER_CALLER)){
            app_mode = Utilities.setBit(app_mode,ProcessConstants.APPMODE_HIDE_SUB_NAV);
            app_mode = Utilities.setBit(app_mode,ProcessConstants.APPMODE_HIDE_TOP_NAV);
            app_mode = Utilities.setBit(app_mode,ProcessConstants.APPMODE_HIDE_HOME_LINKS);
        }

        try {
            req_month = Integer.parseInt(req.getParameter("req_month"));
            req_year = Integer.parseInt(req.getParameter("req_year"));
        } catch (Exception ignore) {}

        int req_sdate = 0;
        int req_edate = 0;

        try {
            req_sdate = Integer.parseInt(req.getParameter("req_sdate"));
            req_edate = Integer.parseInt(req.getParameter("req_edate"));
        } catch (Exception ignore) {}

        //String omit = "";
        String ampm = "";
        String monthName = "";
        String course = "";
        String lname = "";
        String lgname = "";
        String ename = "";
        String dayname = "";
        String rest = "";
        String rest5 = "";
        String p5 = "";
        String stime = "";
        String stime2 = "";
        String ltype = "";
        String lotteryName = "";
        String lottery_color = "";
        String lotteryText = "";         // replacement text for "Lottery"
        String activity_name = "";
        String selected_act_name = "";
        String selected_act_children = "";
        String item_name = "";
        String zone = "";

        int root_id = 0;
        long date = 0;
        long edate = 0;
        long sdate = 0;
        long ldate = 0;
        long lottid = 0;
        long todayDate = 0;
        long tomorrowDate = 0;

        int multi = 0;
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int month = 0;
        int months = 0;
        int day = 0;
        int numDays = 0;
        int days_in_month = 0;
        int today = 0;
        boolean todayFlag = true;
        int day_num = 0;
        int year = 0;
        int hr = 0;
        int min = 0;
        int time = 0;
        int ctime = 0;
        int index = 0;
        int i = 0;
        int i2 = 0;
        int max = 30;    // default
        int col = 0;
        int lottery = 0;
        int lstate = 0;
        int slots = 0;
        //int fives = 0;
        int signUp = 0;
        int fb = 0;
        int proid = 0;
        int etype = 0;
        int wait = 0;
        int lesson_id = 0;
        int person_id = 0;
        int selected_act_id = activity_id;      // selected activity id (which activities to include in calendar)
        int returned_activity_id = 0;           // activity id returned from queries (used to filter which items to display as links when displaying ALL)

        boolean IS_TLT = Utilities.isNotificiationClub(con);

        boolean showLessons = true;             // Used to hide page elements related to the Lesson Book if the Lesson Book is not configured.
        boolean flxRezAct_selected = false;     // Signifies that the currently selected activity is a FlxRez activity. Used to simplify conditionals where this needs to be determined
        boolean flxRezAct_sess = false;         // Signifies that the activity_id is a FlxRez activity. Used to simplify conditionals where this needs to be determined
        boolean childlessAct_selected = false;  // Signifies that the currently selected activity has no children activities (thus no time sheets).
        boolean childlessAct_sess = false;      // Signifies that the activity_id has no children activities (thus no time sheets).
        boolean legendPrinted_scheduledEvents = false;    // Signifies that the legend item for "Your Scheduled Events" has been printed
        boolean legendPrinted_joinableEvents = false;     // Signifies that the legend item for "Events You May Join" has been printed
        boolean legendPrinted_otherEvents = false;        // Signifies that the legend item for "Other Events" has been printed
        boolean legendPrinted_scheduledLessons = false;   // Signifies that the legend item for "Your Scheduled Lessons" has been printed

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> hashMap = new LinkedHashMap<String, Object>(); // Create hashmap response for later use
        
        Map<String, Object> debugMap = new LinkedHashMap<String, Object>();

        int organization_id = Utilities.getOrganizationId(con);


        String dining_base = dining_base = Utilities.getExternalBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club, app_mode);
        String golf_base = golf_base = Utilities.getExternalBaseUrl(req, 0, club, app_mode);
        String flxrez_base = flxrez_base = Utilities.getExternalBaseUrl(req, activity_id, club, app_mode); // what about 999?

        // Setup the daysArray
        DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
        daysArray = SystemUtils.daysInAdv(daysArray, club, mship, mtype, username, con);

        //
        //  parm block to hold the club parameters
        //
        parmClub parm = new parmClub(activity_id, con);

        //boolean events = false;
        //boolean didone = false;
        boolean restricted = false;
        boolean allAct = false;
        boolean restrictAllTees = false;         // restrict all tee times
        boolean check5somes = false;


        boolean eventsOnly = req.getParameter("events_only") != null;
        int[] activityList = null;

        //debugMap.put("found req activity id " + activity_id, "yes");

        if (req.getParameterValues("activity_id") != null) {
            String[] tmpActivityList = req.getParameterValues("activity_id");
            activityList = new int[tmpActivityList.length];

            for (i = 0; i < tmpActivityList.length; i++) {
                try {
                    activityList[i] = Integer.parseInt(tmpActivityList[i]);
                    //debugMap.put("found activity id "+activityList[i], "yes");
                } catch (NumberFormatException ignore) {};
            }
            i=0;
        } else {
            activityList = new int[0];
        }

        // setup our custom sytem text veriables
        SystemLingo sysLingo = new SystemLingo();

        if ((club.equals("ballantyne") || club.equals("sierraviewcc") || club.equals("fortcollins")) && activity_id == 1) {
            sysLingo.setLessonBookLingo("Ball Machine");
        } else {
            sysLingo.setLessonBookLingo("default");
        }

        //
        //  boolean for clubs that want to block member access to tee times after a lottery has been processed.
        //
        //  NOTE:  see same flag in Member_sheet and Member_teelist_list and Member_teelist_mobile !!!!!!!!!!!!!!!!!!
        //
        boolean noAccessAfterLottery = false;

        if (club.equals("bonniebriar") || club.equals("braeburncc")) {   // add other clubs here!!

            noAccessAfterLottery = true;      // no member access after lottery processed
        }


        String[] mm_table = {"inv", "January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

        String[] day_table = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};

        //
        //  Num of days in each month
        //
        int[] numDays_table = {0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        //
        //  Num of days in Feb indexed by year starting with 2000 - 2040
        //
        int[] feb_table = {29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, +28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29};

        //
        //  Arrays to hold the event indicators - one entry per day (is there one of the events on this day)
        //
        int[] eventA = new int[32];          //  events (32 entries so we can index by day #)
        int[] teetimeA = new int[32];        //  tee times
        int[] lessonA = new int[32];         //  lesson times
        int[] lessongrpA = new int[32];      //  lesson group times
        int[] lotteryA = new int[32];        //  lotteries
        int[] waitlistA = new int[32];       //  wait list signups
        int[] activitiesA = new int[32];     //  activity signups
        int[] diningA = new int[32];         //  dining events
        int[] diningR = new int[32];         //  dining reservations

        if (organization_id != 0) {

            person_id = Utilities.getPersonId(username, con);

        }


        if (activity_id != 0 && activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

            flxRezAct_sess = true;

            if (getActivity.isChildlessActivity(activity_id, con)) {
                childlessAct_sess = true;
            }
        }

        // Set a boolean for easy reference later
        if (selected_act_id == ProcessConstants.ALL_ACTIVITIES) {
            allAct = true;
        } else {
            allAct = false;
        }

        if (selected_act_id != 0 && selected_act_id != ProcessConstants.DINING_ACTIVITY_ID && selected_act_id != ProcessConstants.ALL_ACTIVITIES) {

            flxRezAct_selected = true;
            selected_act_name = getActivity.getActivityName(selected_act_id, con);       // Get name for this activity
            selected_act_children = getActivity.buildInString(selected_act_id, 1, con);  // Get csv string of all children of this activity

            if (getActivity.isChildlessActivity(selected_act_id, con)) {
                childlessAct_selected = true;
            } else {
                childlessAct_selected = false;
            }
        }

        if (selected_act_id != ProcessConstants.ALL_ACTIVITIES) {
            showLessons = Utilities.isLessonBookConfigured(selected_act_id, con);
        }

        try {

            //
            // Get the days in advance and time for advance from the club db
            //
            getClub.getParms(con, parm, activity_id);        // get the club parms

            multi = parm.multi;
            lottery = parm.lottery;
            zone = parm.adv_zone;

            //
            //  use the member's mship type to determine which 'days in advance' parms to use
            //
            verifySlot.getDaysInAdv(con, parm, mship, activity_id);        // get the days in adv data for this member

            max = parm.memviewdays + 1;        // days this member can view tee sheets

        } catch (Exception ignore) { }


        //
        //  get today's date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        int cal_hourDay = cal.get(Calendar.HOUR_OF_DAY);     // get current time
        int cal_min = cal.get(Calendar.MINUTE);

        //
        //    Adjust the time based on the club's time zone (we are Central)
        //
        ctime = (cal_hourDay * 100) + cal_min;        // get time in hhmm format

        ctime = SystemUtils.adjustTime(con, ctime);   // adjust the time

        if (ctime < 0) {                // if negative, then we went back or ahead one day

            ctime = 0 - ctime;           // convert back to positive value

            if (ctime < 1200) {           // if AM, then we rolled ahead 1 day

                //
                // roll cal ahead 1 day (its now just after midnight, the next day Eastern Time)
                //
                cal.add(Calendar.DATE, 1);                     // get next day's date

            } else {                        // we rolled back 1 day

                //
                // roll cal back 1 day (its now just before midnight, yesterday Pacific or Mountain Time)
                //
                cal.add(Calendar.DATE, -1);                     // get yesterday's date
            }
        }

        cal_hourDay = ctime / 100;                      // get adjusted hour
        cal_min = ctime - (cal_hourDay * 100);          // get minute value

        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH) + 1;
        dd = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);        // day of week (01 - 07)

        todayDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

        year = yy;
        month = mm;
        day = dd;

        int today_month = month;
        //int today_year = yy;
        today = day;                                  // save today's number

        int dayDiff = 0;
        boolean custom_dates = false;

        // if request dates are valid use them otherwise leave default to the current month/year
        if (req_year == 0 && req_month == 0 && req_sdate > 0 && req_edate > 0 && req_sdate <= req_edate) {

            // start date
            yy = req_sdate / 10000;
            int temp = yy * 10000;
            mm = req_sdate - temp;
            temp = mm / 100;
            temp = temp * 100;
            dd = mm - temp;
            mm = mm / 100;

            year = yy;
            month = mm;
            day = dd;

            DateMidnight start = new DateMidnight(yy+"-"+String.format("%02d", mm)+"-"+String.format("%02d", dd));

            // end date
            yy = req_edate / 10000;
            temp = yy * 10000;
            mm = req_edate - temp;
            temp = mm / 100;
            temp = temp * 100;
            dd = mm - temp;
            mm = mm / 100;

            DateMidnight end = new DateMidnight(yy+"-"+String.format("%02d", mm)+"-"+String.format("%02d", dd));

            //
            // Get days between the start date and end date.
            //
            dayDiff = Days.daysBetween(start, end).getDays();

            custom_dates = true;

            // enforce 28 day max
            // spanning more than a month is problematic with the current
            // indexing used for the arrays - would need to decouple the day
            // of month number from the indexes below
            // these custom dates are only being used to request upto 10 days
            // so we should be able to get by with this for now
            if (dayDiff > 28) {

                return wrapErrorMessage("BAD_DATE_RANGE","Date range requested was greater than 28.");
            }

        } else if ((req_year >= yy && req_year <= yy + 1) && (req_month >= 1 && req_month <= 12)) {
            
            year = req_year;
            month = req_month;

            // adjust max value
            DateMidnight start = new DateMidnight(yy+"-"+String.format("%02d", mm)+"-"+String.format("%02d", day));
            DateMidnight end = new DateMidnight(req_year+"-"+String.format("%02d", req_month)+"-01");

            //
            // Get days between the start date and end date.
            //
            dayDiff = Days.daysBetween(start, end).getDays();
            //Date past = new Date(110, 5, 20); // June 20th, 2010
            //Date today1 = new Date(110, 6, 24); // July 24th
            //int days = Days.daysBetween(new DateTime(past), new DateTime(today1)).getDays();
            
        }

        //
        //  Get tomorrow's date for cutoff test
        //
        cal.add(Calendar.DATE, 1);                     // get next day's date
        yy = cal.get(Calendar.YEAR);
        mm = cal.get(Calendar.MONTH) + 1;
        dd = cal.get(Calendar.DAY_OF_MONTH);

        tomorrowDate = (yy * 10000) + (mm * 100) + dd;     // create a date field of yyyymmdd

        yy = 0;       // reset
        mm = 0;
        dd = 0;

        
        
        //
        //  Check if its earlier than the time specified for days in advance
        //
        if (parm.advtime1 > ctime) {

            //
            //  If this club set the max days to view equal to the days in advance, and all days in advance are the same, and
            //  all times of the days are the same, then adjust the days to view.  This is the only way we can do this!!
            //
            if (parm.memviewdays > 0 && parm.memviewdays == parm.advdays1 && parm.memviewdays == parm.advdays2 && parm.memviewdays == parm.advdays3
                    && parm.memviewdays == parm.advdays4 && parm.memviewdays == parm.advdays5 && parm.memviewdays == parm.advdays6 && parm.memviewdays == parm.advdays7
                    && parm.advtime1 == parm.advtime2 && parm.advtime1 == parm.advtime3 && parm.advtime1 == parm.advtime4 && parm.advtime1 == parm.advtime5
                    && parm.advtime1 == parm.advtime6 && parm.advtime1 == parm.advtime7) {

                max--;
            }
        }


        //
        //  Custom 1595 for Timarron - days to view must match the days in advance so members cannot view any sooner than they can book.
        //
        if (club.equals("timarroncc") && activity_id == 0) {   // per Pro's request

            max = 5;                               // normally 4 days in advance (this method requires max+1 value)

            if (day_num == 2 || ctime < 700) {     // if Monday (any time), then do not allow access to Friday, OR if before 7:00 AM

                max = 4;
            }
        }


        //
        //  Wollaston GC - if today is Monday, then do not allow access to any tee times, no matter what day the tee sheet is for (case 1819).
        //
        /*
        if (club.equals( "wollastongc" )) {

            restrictAllTees = verifyCustom.checkWollastonMon();         // restrict all day if today is Monday
        }*/


        //  Do not allow members access to tee times on the Edina 2010 site
        if (club.equals("edina2010")) {

            restrictAllTees = true;
        }


/*
        //
        //  Display 3 tables - 1 for each of the next 3 months
        //
        months = 3;                                      // default = 3 months

        if ((club.equals("deserthighlands") && activity_id == 0) || club.equals("mirabel")) {

            months = 12;                               // they want 12 months
        }

        if ((club.equals("rtjgc") || club.equals("pattersonclub") || club.equals("blackrockcountryclub")) && activity_id == 0) {     // Robert Trent Jones GC

            months = 6;                               // they want 6 months
        }
*/

        months = 1; // override

        int days_done = 0;


        Map<String, Object> container_map = new LinkedHashMap<String, Object>();
        Map<String, Object> response_map = new LinkedHashMap<String, Object>();

        container_map.put("foreTeesMemberCalendarData", response_map);
        //int i3 = 0, i4 = 0;

        
        for (i2 = 0; i2 < months; i2++) {                 // do each month

            monthName = mm_table[month];                  // month name

            numDays = numDays_table[month];               // number of days in month

            if (numDays == 0) {                           // if Feb

                numDays = feb_table[year - 2000];             // get days in Feb
            }

            if (custom_dates) {


                days_in_month = numDays;    // save for later
                numDays = dayDiff;          // re-purpose

                sdate = req_sdate;
                edate = req_edate;

            } else {

                //
                //  Adjust values to start at the beginning of the month
                //
                cal.set(Calendar.YEAR, year);                 // set year in case it changed below
                cal.set(Calendar.MONTH, month - 1);             // set the current month value
                cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
                day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
                day = 1;

                sdate = (year * 10000) + (month * 100) + 0;       // start of the month (for searches)
                edate = (year * 10000) + (month * 100) + 32;      // end of the month

            }


            //
            //  init the indicator arrays to start new month
            //
            for (i = 0; i < 32; i++) {
                eventA[i] = 0;
                teetimeA[i] = 0;
                lessonA[i] = 0;
                lotteryA[i] = 0;
                waitlistA[i] = 0;
                activitiesA[i] = 0;
                diningA[i] = 0;
                diningR[i] = 0;
            }

            //
            //  Locate all the Tee Times for this member & month and set the array indicators for each day
            //
            if (!eventsOnly && parm.foretees_mode == 1 && (activity_id == 0 || activity_id == ProcessConstants.ALL_ACTIVITIES)) {

                try {

                    //
                    // search for this member's tee times for choosen month
                    //
                    pstmt1 = con.prepareStatement(
                              "SELECT dd "
                            + "FROM teecurr2 "
                            + "WHERE ("
                            +   "username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ? OR "
                            +   "userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ?) "
                            +   "AND date BETWEEN ? AND ? AND lottery_email = 0 "
                            + "ORDER BY date");

                    pstmt1.clearParameters();
                    pstmt1.setString(1, username);
                    pstmt1.setString(2, username);
                    pstmt1.setString(3, username);
                    pstmt1.setString(4, username);
                    pstmt1.setString(5, username);
                    pstmt1.setString(6, username);
                    pstmt1.setString(7, username);
                    pstmt1.setString(8, username);
                    pstmt1.setString(9, username);
                    pstmt1.setString(10, username);
                    pstmt1.setLong(11, sdate);
                    pstmt1.setLong(12, edate);
                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        teetimeA[rs.getInt(1)] = 1;       // set indicator for this day (tee time exists)
                    }

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting tee times for " + club + ", username: " + username + ", Error: " + exc.getMessage());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignored) {}

                    try { pstmt1.close(); }
                    catch (Exception ignored) {}

                }

                //
                //  Check for any lottery requests, if supported
                //
                if (lottery > 0) {

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT dd "
                                + "FROM lreqs3 "
                                + "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR "
                                + "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR "
                                + "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR "
                                + "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR "
                                + "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? OR "
                                + "orig_by LIKE ?) "
                                + "AND date BETWEEN ? AND ? "
                                + "ORDER BY date");

                        pstmt1.clearParameters();
                        pstmt1.setString(1, username);
                        pstmt1.setString(2, username);
                        pstmt1.setString(3, username);
                        pstmt1.setString(4, username);
                        pstmt1.setString(5, username);
                        pstmt1.setString(6, username);
                        pstmt1.setString(7, username);
                        pstmt1.setString(8, username);
                        pstmt1.setString(9, username);
                        pstmt1.setString(10, username);
                        pstmt1.setString(11, username);
                        pstmt1.setString(12, username);
                        pstmt1.setString(13, username);
                        pstmt1.setString(14, username);
                        pstmt1.setString(15, username);
                        pstmt1.setString(16, username);
                        pstmt1.setString(17, username);
                        pstmt1.setString(18, username);
                        pstmt1.setString(19, username);
                        pstmt1.setString(20, username);
                        pstmt1.setString(21, username);
                        pstmt1.setString(22, username);
                        pstmt1.setString(23, username);
                        pstmt1.setString(24, username);
                        pstmt1.setString(25, username);
                        pstmt1.setString(26, username);
                        pstmt1.setLong(27, sdate);
                        pstmt1.setLong(28, edate);
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {
                            
                            //debugMap.put("found_lott_"+rs.getString(1),"yes");

                            lotteryA[rs.getInt(1)] = 1;       // set indicator for this day (lottery req exists)
                        }

                    } catch (Exception exc) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting lottery reqs for " + club + ", username: " + username + ", Error: " + exc.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }
                } // end of IF lottery
            }

            //
            //  Get all lesson times for this user this month (unless we're here and only pulling back dining results)
            //
            if (!eventsOnly && activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                try {

                    pstmt1 = con.prepareStatement(
                            "SELECT date "
                            + "FROM lessonbook5 "
                            + "WHERE date BETWEEN ? AND ? AND memid = ? "
                            + (!allAct ? "AND activity_id = ? " : "")
                            + "ORDER BY date");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setString(3, username);
                    if (!allAct) {
                        pstmt1.setInt(4, selected_act_id);
                    }
                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        ldate = rs.getLong(1);

                        ldate = ldate - ((ldate / 100) * 100);     // get day
                        dd = (int) ldate;

                        lessonA[dd] = 1;       // set indicator for this day (lesson time exists)
                        debugMap.put("found_lesson_"+rs.getLong(1),ldate);

                    }
                    pstmt1.close();

                    pstmt1 = con.prepareStatement(
                            "SELECT date "
                            + "FROM lgrpsignup5 "
                            + "WHERE date BETWEEN ? AND ? AND memid = ? "
                            + "ORDER BY date");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setString(3, username);
                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        ldate = rs.getLong(1);

                        ldate = ldate - ((ldate / 100) * 100);     // get day
                        dd = (int) ldate;

                        lessongrpA[dd] = 1;       // set indicator for this day (lesson time exists)
                        
                    }

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting lesson times for " + club + ", username: " + username + ", Error: " + exc.getMessage());                           // log it

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt1.close(); }
                    catch (Exception ignore) {}

                }
            }

            //
            //  Get all non-dining events for this month (skip if we're here only for dining results)
            //
            if (activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                try {

                    if (activity_id == ProcessConstants.ALL_ACTIVITIES) {     // if ALL selected

                        if (club.equals("tcclub")) {

                            pstmt1 = con.prepareStatement(
                                    "SELECT day "
                                    + "FROM events2b WHERE date BETWEEN ? AND ? AND inactive = 0");

                        } else {

                            pstmt1 = con.prepareStatement(
                                    "SELECT day "
                                    + "FROM events2b WHERE date BETWEEN ? AND ? AND gstOnly = 0 AND inactive = 0");
                        }

                        pstmt1.setLong(1, sdate);
                        pstmt1.setLong(2, edate);

                    } else {

                        if (club.equals("tcclub")) {

                            pstmt1 = con.prepareStatement(
                                    "SELECT day "
                                    + "FROM events2b WHERE date BETWEEN ? AND ? AND activity_id = ? AND inactive = 0");

                        } else {

                            pstmt1 = con.prepareStatement(
                                    "SELECT day "
                                    + "FROM events2b WHERE date BETWEEN ? AND ? AND gstOnly = 0 AND activity_id = ? AND inactive = 0");
                        }


                        pstmt1.setLong(1, sdate);
                        pstmt1.setLong(2, edate);
                        pstmt1.setInt(3, activity_id);
                    }

                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        eventA[rs.getInt(1)] = 1;       // set indicator for this day (event exists)
                    }

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting events for " + club + ", Error: " + exc.getMessage());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt1.close(); }
                    catch (Exception ignore) {}

                }
            }


            //
            //  Get all dining events & ala carte reservations for this month
            //
            if (organization_id != 0 && (activity_id == ProcessConstants.DINING_ACTIVITY_ID || activity_id == ProcessConstants.ALL_ACTIVITIES)) { // only query for them if Dining is enabled

                Connection con_d = null;

                try {

                    con_d = Connect.getDiningCon();

                    if (con_d != null) {

                        // quick fix - set the correct max value for dining
                        max = parmDining.getMaxDays(organization_id, con_d);

                        // get all dining event reservations
                        pstmt1 = con_d.prepareStatement(""
                                + "SELECT EXTRACT(DAY FROM date) "
                                + "FROM events "
                                + "WHERE organization_id = ? AND "
                                + "to_char(date, 'YYYYMMDD')::int >= ? AND "
                                + "to_char(date, 'YYYYMMDD')::int <= ? AND "
                                + "cancelled = false");

                        pstmt1.setInt(1, organization_id);
                        pstmt1.setLong(2, sdate);
                        pstmt1.setLong(3, edate);

                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            diningA[rs.getInt(1)] = 1;       // set indicator for this day (dining event exists)
                        }

                        if (!eventsOnly) {

                            //  get all dining reservations for this month and member
                            pstmt1 = con_d.prepareStatement(""
                                    + "SELECT EXTRACT(DAY FROM date) "
                                    + "FROM reservations "
                                    + "WHERE "
                                    + "category = 'dining' AND state <> 'cancelled' AND "
                                    + "organization_id = ? AND "
                                    + "person_id = ? AND "
                                    + "to_char(date, 'YYYYMMDD')::int >= ? AND "
                                    + "to_char(date, 'YYYYMMDD')::int <= ?");

                            pstmt1.setInt(1, organization_id);
                            pstmt1.setInt(2, person_id);
                            pstmt1.setLong(3, sdate);
                            pstmt1.setLong(4, edate);

                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                diningR[rs.getInt(1)] = 1;       // set indicator for this day (dining reservation exists)
                            }

                        } // if not events only

                    } // if dining db con ok

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting dinning events/res for " + club + ", Error: " + exc.getMessage());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignored) {}

                    try { pstmt1.close(); }
                    catch (Exception ignored) {}

                    try { con_d.close(); }
                    catch (Exception ignored) {}

                }

            } // end if dining club


            if (!eventsOnly && parm.foretees_mode == 1 && (activity_id == 0 || activity_id == ProcessConstants.ALL_ACTIVITIES)) {

                try {

                    //
                    // search for this user's unconverted wait list signups for this month
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT DATE_FORMAT(wls.date, '%d') AS dd, wls.wait_list_signup_id "
                            + "FROM wait_list_signups wls "
                            + "LEFT OUTER JOIN wait_list_signups_players wlp ON wlp.wait_list_signup_id = wls.wait_list_signup_id "
                            + "WHERE DATE_FORMAT(wls.date, '%Y%m%d') >= ? AND DATE_FORMAT(wls.date, '%Y%m%d') <= ? AND wlp.username = ? AND converted = 0 "
                            + "ORDER BY wls.date");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setString(3, username);

                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        waitlistA[rs.getInt(1)] = rs.getInt(2);       // set indicator for this day (wait list signup exists)

                    }

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting wait list signups for " + club + ", username: " + username + ", Error: " + exc.getMessage());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignored) {}

                    try { pstmt1.close(); }
                    catch (Exception ignored) {}

                }
            }


            if (!eventsOnly && parm.genrez_mode != 0 && (flxRezAct_selected || activity_id == ProcessConstants.ALL_ACTIVITIES)) {      // if any activites defined for this club

                try {

                    //
                    // search for this user's activity signups for this month
                    //
                    pstmt1 = con.prepareStatement(
                            "SELECT DATE_FORMAT(a.date_time, '%d') AS dd, a.sheet_id "
                            + "FROM activity_sheets a "
                            + "LEFT OUTER JOIN activity_sheets_players ap ON ap.activity_sheet_id = a.sheet_id "
                            + "WHERE DATE_FORMAT(a.date_time, '%Y%m%d') >= ? AND DATE_FORMAT(a.date_time, '%Y%m%d') <= ? AND "
                            +   "ap.username = ? " + (selected_act_id != ProcessConstants.ALL_ACTIVITIES ? "AND a.activity_id IN (" + selected_act_children + ") " : "")
                            + "ORDER BY a.date_time");

                    pstmt1.clearParameters();
                    pstmt1.setLong(1, sdate);
                    pstmt1.setLong(2, edate);
                    pstmt1.setString(3, username);

                    rs = pstmt1.executeQuery();

                    while (rs.next()) {

                        activitiesA[rs.getInt(1)] = 1;       // set indicator for this day (activity signup exists)
                    }

                } catch (Exception exc) {

                    Utilities.logError("Common_webapi.getCalendarData() Error getting activity signups for " + club + ", username: " + username + ", Error: " + exc.getMessage());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignored) {}

                    try { pstmt1.close(); }
                    catch (Exception ignored) {}

                }
            }


            //
            //  BEGIN CALENDAR OUTPUT
            //

            int entry = 0;
            //int orig_max = max;
            max = max - dayDiff;
            index = index + dayDiff;

            //response_map.put("req_month", req_month);
            //response_map.put("req_year", req_year);
            //response_map.put("today_month", today_month);
            //response_map.put("today", today);
            //response_map.put("orig_max", orig_max);
            //response_map.put("max", max);
            //response_map.put("dayDiff", dayDiff);
            
            response_map.put("club", club);
            response_map.put("user", user);
            response_map.put("activityIdRequested", String.valueOf(activity_id));  // indicates which activites we are returning in this response
            
            if (custom_dates) {

                // include the requested dates
                response_map.put("req_sdate", req_sdate);
                response_map.put("req_edate", req_edate);
                
            } else {

                // include the date parts for the month being processed
                response_map.put("monthName", monthName);
                response_map.put("month", month);
                response_map.put("year", year);
            }
            
            //response_map.put("numDays", numDays);
            //response_map.put("daysInMonth", days_in_month);
            //response_map.put("custom_dates", custom_dates);
            response_map.put("timestamp", benchmark_start);  // let's resuse this since we already have it here
            response_map.put("serverId", ProcessConstants.SERVER_ID);


            //
            // start with today, or 1st day of month, and go to end of month
            //
            while ( day <= ((custom_dates) ? 31 : numDays) ) {

                entry = 0; // reset

                // skip all past days if processing the current month
                if ((month == today_month && day >= today) || month != today_month) {

                date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd

                // see if we've exceeded the end date if doing a custom range
                if (custom_dates && req_edate < date) break;


                //
                //  create a date field for queries
                //
                String mysql_date = year + "-" + Utilities.ensureDoubleDigit(month) + "-" + Utilities.ensureDoubleDigit(day);


                // start a new day map
                Map<String, Object> calendar_day = new LinkedHashMap<String, Object>();

                calendar_day.put("dayOfMonth", String.valueOf(day));
                calendar_day.put("date", mysql_date);
                
        
                // if this day should be selectable (not passed max allowed AND not returning all activities and if flxrez then make sure it's not a childless
                if (max > 0 && activity_id != ProcessConstants.ALL_ACTIVITIES && (activity_id == 0 || activity_id == ProcessConstants.DINING_ACTIVITY_ID || !getActivity.isChildlessActivity(activity_id, con))) {
                    
                    //
                    //  Add link to tee sheets, time sheets, or dining slot page (do not specify a course, it will default to 1st one)
                    //
                    if (activity_id == 0) {          // if Golf
                        
                        calendar_day.put("sheetLink", golf_base + "Member" + ((IS_TLT) ? "TLT" : "") + "_sheet?index=" + index);
                        calendar_day.put("sheetLinkAlt", "Select a Tee Time for this day");
                      
                    } else if (activity_id == ProcessConstants.DINING_ACTIVITY_ID) {
                        
                        calendar_day.put("sheetLink", dining_base + "Dining_slot?action=new&orig=calendar&date=" + date);
                        calendar_day.put("sheetLinkAlt", "Create a reservation for this day");
                      
                    } else {
                        
                        calendar_day.put("sheetLink", flxrez_base + "Member_gensheets?date=" + date);
                        calendar_day.put("sheetLinkAlt", "Select a Time Sheet for this day");
                    
                    }

                    index++;              // next day
                    max--;

                } else {

                  //out.println("<div class=\"day\">" + day + "</div>");         // just put in day of month
                }


                if (IS_TLT) {


                    // check to see if there are any notifications for this day

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT n.notification_id, DATE_FORMAT(n.req_datetime, '%l:%i %p') AS pretty_time "
                                + "FROM notifications n, notifications_players np "
                                + "WHERE n.notification_id = np.notification_id "
                                + "AND np.username = ? "
                                + "AND DATE(n.req_datetime) = ? ");

                        pstmt1.clearParameters();
                        pstmt1.setString(1, username);
                        pstmt1.setString(2, mysql_date);
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                            data_map.put("url", "MemberTLT_slot");
                            data_map.put("base_url", golf_base);

                            Map<String, Object> form_data_map = new LinkedHashMap<String, Object>();
                            form_data_map.put("stime", rs.getString("pretty_time"));
                            form_data_map.put("notifyId", rs.getInt("notification_id"));
                            form_data_map.put("date", date);
                            form_data_map.put("day", day);
                            form_data_map.put("index", 999);
                            data_map.put("data", form_data_map);

                            Map<String, String> calendar_entry = new HashMap<String, String>();
                            calendar_entry.put("activityId", "0");
                            calendar_entry.put("displayText", buildItemDescription("Golf", rs.getString("pretty_time")));
                            calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                            calendar_entry.put("cssClass", "tee_list_tlt_time");
                            calendar_day.put("entry_" + entry, calendar_entry);

                            entry++;
                        }

                    } catch (Exception ignored) {

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } else {

                    //*******************************************************************************
                    //  Check for any tee times for this day
                    //*******************************************************************************
                    //
                    if (teetimeA[day] == 1) {        // if any tee times exist for this day

                        try {

                            pstmt1 = con.prepareStatement(
                                    "SELECT teecurr_id, mm, dd, yy, day, hr, min, time, event, event_type, fb, "
                                    + "lottery, courseName, rest5, lottery_color "
                                    + "FROM teecurr2 WHERE (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                    + "OR username5 = ? OR userg1 = ? OR userg2 = ? OR userg3 = ? OR userg4 = ? OR userg5 = ? OR orig_by = ?) "
                                    + "AND date = ? ORDER BY time");

                            pstmt1.clearParameters();
                            pstmt1.setString(1, username);
                            pstmt1.setString(2, username);
                            pstmt1.setString(3, username);
                            pstmt1.setString(4, username);
                            pstmt1.setString(5, username);
                            pstmt1.setString(6, username);
                            pstmt1.setString(7, username);
                            pstmt1.setString(8, username);
                            pstmt1.setString(9, username);
                            pstmt1.setString(10, username);
                            pstmt1.setString(11, username);
                            pstmt1.setLong(12, date);
                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                mm = rs.getInt("mm");
                                dd = rs.getInt("dd");
                                yy = rs.getInt("yy");
                                dayname = rs.getString("day");
                                hr = rs.getInt("hr");
                                min = rs.getInt("min");
                                time = rs.getInt("time");
                                ename = rs.getString("event");
                                etype = rs.getInt("event_type");
                                fb = rs.getInt("fb");
                                lotteryName = rs.getString("lottery");
                                course = rs.getString("courseName");
                                rest5 = rs.getString("rest5");
                                lottery_color = rs.getString("lottery_color");

                                //
                                //  Check if a member restriction has been set up to block ALL mem types or mship types for this date & time
                                //
                                restricted = Utilities.checkRests(date, time, fb, course, dayname, mship, mtype, club, con);

                                ampm = " AM";
                                if (hr == 12) {
                                    ampm = " PM";
                                }
                                if (hr > 12) {
                                    ampm = " PM";
                                    hr = hr - 12;    // convert to conventional time
                                }

                                stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                                stime = stime2 + ampm;        // create a value for time parm

                                p5 = "No";                   // default = no 5-somes
                                //fives = 0;
                                check5somes = true;

                                long month_day = (mm * 100) + dd;     // get adjusted date

                                //
                                //  Customs 5-some restrictions for members (do not allow access to 5th player)
                                //
                                if (club.equals("rtjgc") || club.equals("coloradogc")) {

                                    check5somes = false;

                                } else if (club.equals("foresthighlands") && (month_day > 424 && month_day < 1001)) {

                                    check5somes = false;

                                } else if (club.equals("columbine") && (month_day > 331 && month_day < 1001)) {

                                    check5somes = false;

                                } else if (club.equals("estanciaclub") && (month_day <= 515 || month_day >= 1015)) {

                                    check5somes = false;

                                } else if (club.equals("lakes") && (mm < 6 || mm > 10)) {

                                    check5somes = false;
                                }


                                //
                                //  check if 5-somes allowed on this course
                                //
                                if (check5somes == true) {

                                    PreparedStatement pstmt3 = null;

                                    try {

                                        pstmt3 = con.prepareStatement(
                                                "SELECT fives FROM clubparm2 WHERE courseName = ?");

                                        pstmt3.clearParameters();
                                        pstmt3.setString(1, course);
                                        rs2 = pstmt3.executeQuery();

                                        if (rs2.next()) {

                                            if ((rs2.getInt(1) != 0) && (rest5.equals(""))) {   // if 5-somes and not restricted

                                                p5 = "Yes";
                                            }
                                        }

                                    } catch (Exception ignore) {

                                    } finally {

                                        try { rs2.close(); }
                                        catch (Exception ignored) {}

                                        try { pstmt3.close(); }
                                        catch (Exception ignored) {}

                                    }
                                }

                                //
                                // Check for a shotgun event during this time
                                //
                                if (!ename.equals("") && etype == 1) {

                                    //
                                    // Tee time is during a shotgun event
                                    //

                                    PreparedStatement pstmtev = null;

                                    try {

                                        //
                                        // Get the parms for this event
                                        //
                                        pstmtev = con.prepareStatement(
                                                "SELECT act_hr, act_min " +
                                                "FROM events2b " +
                                                "WHERE name = ?");

                                        pstmtev.clearParameters();
                                        pstmtev.setString(1, ename);
                                        rsev = pstmtev.executeQuery();

                                        if (rsev.next()) {

                                            hr = rsev.getInt("act_hr");
                                            min = rsev.getInt("act_min");
                                        }

                                    } catch (Exception ignore) {

                                    } finally {

                                        try { rsev.close(); }
                                        catch (Exception ignore) {}

                                        try { pstmtev.close(); }
                                        catch (Exception ignore) {}

                                    }

                                    //
                                    //  Create time value for email msg
                                    //
                                    ampm = " AM";

                                    if (hr == 0) {

                                        hr = 12;              // change to 12 AM (midnight)

                                    } else if (hr == 12) {

                                        ampm = " PM";         // change to Noon

                                    } else if (hr > 12) {

                                        hr = hr - 12;
                                        ampm = " PM";         // change to 12 hr clock
                                    }

                                    //
                                    //  convert time to hour and minutes for email msg
                                    //
                                    stime2 = hr + ":" + Utilities.ensureDoubleDigit(min) + ampm;

                                    // add new calendar entry
                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", "0");
                                    calendar_entry.put("displayText", buildItemDescription("Shotgun Start", stime2));
                                    calendar_entry.put("cssClass", "shotgun");
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                } else {

                                    //
                                    // Standard tee time
                                    //

                                    boolean cutoff = false;

                                    //
                                    //  Check for Member Cutoff specified in Club Options
                                    //
                                    if (parm.cutoffdays < 99) {        // if option specified

                                        if (parm.cutoffdays == 0 && date == todayDate && ctime > parm.cutofftime) {  // if cutoff day of and we are doing today and current time is later than cutoff time

                                            cutoff = true;         // indicate no member access

                                        } else {

                                            if (parm.cutoffdays == 1 && (date == todayDate || (date == tomorrowDate && ctime > parm.cutofftime))) {    // if cutoff day is the day before

                                                cutoff = true;         // indicate no member access
                                            }
                                        }
                                    }

                                    //
                                    //  Check for lottery time that has already been processed
                                    //
                                    if (!lotteryName.equals("")) {

                                        //
                                        //  Get the current state of this lottery on the day of this tee time
                                        //
                                        lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lotteryName, course, con);

                                        if (lstate < 5 || noAccessAfterLottery) {    // if lottery not approved OR access not allowed after approval

                                            cutoff = true;        // do not allow access to this tee time (pre-booked lottery time)
                                        }

                                    } else {

                                        if (!lottery_color.equals("") && noAccessAfterLottery) {   // if it was a lottery time and access not allowed after processed

                                            cutoff = true;                    // do not allow access to this tee time
                                        }
                                    }


                                    if (restrictAllTees == true) {    // if all tee times are restricted

                                        cutoff = true;        // do not allow access to this tee time
                                    }


                                    // if user can edit tee time
                                    if (!restricted && !cutoff && !(date == todayDate && time <= ctime) && (activity_id == 0 || activity_id == ProcessConstants.ALL_ACTIVITIES)) {

                                        //
                                        // This tee time is a clickable link
                                        //
                                        
                                        hashMap.clear();
                                        hashMap.put("type", "Member_slot");
                                        hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + String.format("%02d", min) + ampm + "|" + fb + "|" + username));
                                        hashMap.put("date", date);
                                        hashMap.put("course", course);
                                        hashMap.put("fb", fb);
                                        hashMap.put("p5", p5);
                                        hashMap.put("index", 999);
                                        hashMap.put("stime", stime);
                                        hashMap.put("day", dayname);


                                        Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                        data_map.put("url", "Member_slot");
                                        data_map.put("base_url", golf_base);
                                        data_map.put("data", hashMap);

                                        Map<String, String> calendar_entry = new HashMap<String, String>();
                                        calendar_entry.put("activityId", "0");
                                        calendar_entry.put("displayText", buildItemDescription("Tee Time", stime));
                                        calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                        calendar_entry.put("cssClass", "tee_list_color_4");
                                        calendar_day.put("entry_" + entry, calendar_entry);

                                    } else {

                                        Map<String, String> calendar_entry = new HashMap<String, String>();
                                        calendar_entry.put("activityId", "0");
                                        calendar_entry.put("displayText", buildItemDescription("Tee Time", stime));
                                        calendar_entry.put("cssClass", "teetime");
                                        calendar_day.put("entry_" + entry, calendar_entry);

                                    }
                                }

                                entry++;

                            } // end of WHILE

                        } catch (Exception e1) {

                            Utilities.logError("Common_webapi.getCalendarData() Error getting tee times for " + club + ", Day=" + day + ", username: " + username + ", Error: " + e1.getMessage());                           // log it

                        } finally {

                            try { rs.close(); }
                            catch (Exception ignored) {}

                            try { pstmt1.close(); }
                            catch (Exception ignored) {}

                        }

                    } // end if tee time found for this day

                } // end if tlt


                //*******************************************************************************
                //  Check for any lotteries for this day (all will be zero if not supported)
                //*******************************************************************************
                //
                if (lotteryA[day] == 1 && (activity_id == 0 || activity_id == ProcessConstants.ALL_ACTIVITIES)) {        // if any lotteries  exist for this day
                    //debugMap.put("testing_lott_"+day,"yes");
                    try {
                        // why using a LIKE here instead of direct match
                        pstmt1 = con.prepareStatement(
                                "SELECT name, mm, dd, yy, day, hr, min, time, "
                                + "fb, courseName, id "
                                + "FROM lreqs3 "
                                + "WHERE (user1 LIKE ? OR user2 LIKE ? OR user3 LIKE ? OR user4 LIKE ? OR user5 LIKE ? OR "
                                + "user6 LIKE ? OR user7 LIKE ? OR user8 LIKE ? OR user9 LIKE ? OR user10 LIKE ? OR "
                                + "user11 LIKE ? OR user12 LIKE ? OR user13 LIKE ? OR user14 LIKE ? OR user15 LIKE ? OR "
                                + "user16 LIKE ? OR user17 LIKE ? OR user18 LIKE ? OR user19 LIKE ? OR user20 LIKE ? OR "
                                + "user21 LIKE ? OR user22 LIKE ? OR user23 LIKE ? OR user24 LIKE ? OR user25 LIKE ? OR "
                                + "orig_by LIKE ?) "
                                + "AND date = ? ORDER BY time");

                        pstmt1.clearParameters();
                        pstmt1.setString(1, username);
                        pstmt1.setString(2, username);
                        pstmt1.setString(3, username);
                        pstmt1.setString(4, username);
                        pstmt1.setString(5, username);
                        pstmt1.setString(6, username);
                        pstmt1.setString(7, username);
                        pstmt1.setString(8, username);
                        pstmt1.setString(9, username);
                        pstmt1.setString(10, username);
                        pstmt1.setString(11, username);
                        pstmt1.setString(12, username);
                        pstmt1.setString(13, username);
                        pstmt1.setString(14, username);
                        pstmt1.setString(15, username);
                        pstmt1.setString(16, username);
                        pstmt1.setString(17, username);
                        pstmt1.setString(18, username);
                        pstmt1.setString(19, username);
                        pstmt1.setString(20, username);
                        pstmt1.setString(21, username);
                        pstmt1.setString(22, username);
                        pstmt1.setString(23, username);
                        pstmt1.setString(24, username);
                        pstmt1.setString(25, username);
                        pstmt1.setString(26, username);
                        pstmt1.setLong(27, date);

                        rs = pstmt1.executeQuery();

                        while (rs.next()) {
                            
                            //debugMap.put("looping_lott_"+day,"yes");

                            lname = rs.getString(1);
                            mm = rs.getInt(2);
                            dd = rs.getInt(3);
                            yy = rs.getInt(4);
                            dayname = rs.getString(5);
                            hr = rs.getInt(6);
                            min = rs.getInt(7);
                            time = rs.getInt(8);
                            fb = rs.getInt(9);
                            course = rs.getString(10);
                            lottid = rs.getLong(11);

                            ampm = " AM";
                            if (hr == 12) {
                                ampm = " PM";
                            }
                            if (hr > 12) {
                                ampm = " PM";
                                hr = hr - 12;    // convert to conventional time
                            }

                            stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            stime = stime2 + ampm;        // create a value for time parm

                            //
                            //  Check if 5-somes supported for this course
                            //
                            int fives = 0;        // init

                            PreparedStatement pstmtc = con.prepareStatement(
                                    "SELECT fives "
                                    + "FROM clubparm2 WHERE first_hr != 0 AND courseName = ?");

                            pstmtc.clearParameters();        // clear the parms
                            pstmtc.setString(1, course);
                            rs2 = pstmtc.executeQuery();      // execute the prepared stmt

                            if (rs2.next()) {

                                fives = rs2.getInt(1);          // 5-somes
                            }
                            pstmtc.close();

                            //
                            //  check if 5-somes restricted for this time
                            //
                            rest = "";     // no rest5

                            if (fives != 0) {

                                PreparedStatement pstmtr = con.prepareStatement(
                                        "SELECT rest5 "
                                        + "FROM teecurr2 WHERE date = ? AND time =? AND fb = ? AND courseName = ?");

                                pstmtr.clearParameters();        // clear the parms
                                pstmtr.setLong(1, date);
                                pstmtr.setInt(2, time);
                                pstmtr.setInt(3, fb);
                                pstmtr.setString(4, course);
                                rs2 = pstmtr.executeQuery();      // execute the prepared stmt

                                if (rs2.next()) {

                                    rest = rs2.getString(1);
                                }
                                pstmtr.close();
                            }

                            p5 = "No";                   // default = no 5-somes

                            if (fives != 0 && rest.equals("")) {     // if 5-somes are supported & not restricted

                                p5 = "Yes";                   // 5-somes ok
                            }


                            //
                            //  get the slots value and determine the current state for this lottery
                            //
                            PreparedStatement pstmt7d = con.prepareStatement(
                                    "SELECT slots "
                                    + "FROM lottery3 WHERE name = ?");

                            pstmt7d.clearParameters();          // clear the parms
                            pstmt7d.setString(1, lname);

                            rs2 = pstmt7d.executeQuery();      // find all matching lotteries, if any

                            if (rs2.next()) {

                                slots = rs2.getInt(1);
                            }
                            pstmt7d.close();


                            //
                            //  Get the current state of this lottery on the day of this tee time
                            //
                            lstate = SystemUtils.getLotteryState(date, mm, dd, yy, lname, course, con);


                            //
                            //  Form depends on the state
                            //

                            hashMap.clear();
                            hashMap.put("day", dayname);
                            hashMap.put("date", date);
                            hashMap.put("course", course);
                            hashMap.put("p5", p5);
                            hashMap.put("index", 999);
                            
                            //debugMap.put("lstate_lott_"+day,lstate);

                            //String lotteryInfoStr = "'" + date + "','" + dayname + "','" + p5 + "','" + course + "','" + fb + "','" + stime + "','" + lname + "','" + lottid + "','";
                            if (lstate == 2) {       // if still ok to process lottery requests

                                hashMap.put("fb", fb);
                                hashMap.put("stime", stime);
                                hashMap.put("lname", lname);
                                hashMap.put("lottid", lottid);
                                hashMap.put("type", "Member_lott");
                                hashMap.put("lstate", lstate);
                                hashMap.put("slots", slots);

                                if (club.equals("oldoaks")) {
                                    item_name = buildItemDescription("Tee Time Request", stime);
                                } else if (!lotteryText.equals("")) {
                                    item_name = buildItemDescription(lotteryText, stime);
                                } else {
                                    item_name = buildItemDescription("Lottery Request", stime);
                                }

                                //
                                //  Display the lottery time as a clickable link (see form & js above)
                                //
                                /*
                                out.print("<div class=\"item_container\">");
                                if (activity_id == 0) {
                                    //out.print("<a href=\"javascript: exeLott2Form(" + lotteryInfoStr + slots + "','" + lstate + "')\" class=mlottery>");
                                    out.println("<a class=\"lottery_button tee_list_color_6\" href=\"#\""
                                            + " data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">"
                                            + item_name + "</a>");
                                } else {
                                    out.println("<div class=\"tee_list_color_6\">" + item_name + "</div>");
                                }
                                out.print("</div>");
                                */

                                Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                data_map.put("url", "Member_lott");
                                data_map.put("base_url", golf_base); // all lotteries are golf
                                data_map.put("data", hashMap);

                                Map<String, String> calendar_entry = new HashMap<String, String>();
                                calendar_entry.put("activityId", "0"); // all lotteries are golf
                                calendar_entry.put("displayText", item_name);
                                calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                calendar_entry.put("cssClass", "tee_list_color_6");
                                calendar_day.put("entry_" + entry, calendar_entry);
                                
                                
                            } else {

                                if (lstate == 5) {       // if lottery has already been processed

                                    hashMap.put("type", "Member_slot");
                                    hashMap.put("ttdata", Utilities.encryptTTdata(hr + ":" + String.format("%02d", min) + ampm + "|" + fb + "|" + username));

                                    //
                                    //  Display the lottery time as a clickable link (see form & js above)
                                    //
                                    /*
                                    out.print("<div class=\"item_container\">");
                                    out.println("<a class=\"teetime_button tee_list_color_6\" href=\"#\""
                                            + " data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">" + item_name + "</a>");
                                    //out.println("      <a href=\"javascript: exeLott5Form(" + lotteryInfoStr + lstate + "')\" class=mlottery>");
                                    out.print("</div>");*/

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "Member_slot");
                                    data_map.put("base_url", golf_base); // all lotteries are golf
                                    data_map.put("data", hashMap);

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", "0"); // all lotteries are golf
                                    calendar_entry.put("displayText", item_name);
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_entry.put("cssClass", "tee_list_color_6");
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                } else {

                                    /*out.print("<div class=\"item_container\">");
                                    out.print("<div class=\"tee_list_lott_time tee_list_color_6\">" + item_name + "</div>");
                                    out.print("</div>");*/

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", "0");
                                    calendar_entry.put("displayText", item_name);
                                    calendar_entry.put("cssClass", "tee_list_color_6");
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                }
                            }

                            entry++;

                        } // end of WHILE

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting tee times for " + club + ", Day=" + day + ", username: " + username + ", Error: " + e1.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } // end of IF lotteries


                //**********************************************************
                //  Check for any lessons for this day
                //**********************************************************
                //
                if (lessonA[day] == 1) {        // if any lessons  exist for this day

                    try {
                        lesson_id = 0;

                        pstmt1 = con.prepareStatement(
                                "SELECT proid, time, ltype, recid, activity_id, date "
                                + "FROM lessonbook5 "
                                + "WHERE memid = ? AND date = ? AND num > 0 "
                                + (!allAct ? "AND activity_id = ? " : "")
                                + "ORDER BY time");

                        pstmt1.clearParameters();        // clear the parms
                        pstmt1.setString(1, username);
                        pstmt1.setLong(2, date);
                        if (!allAct) {
                            pstmt1.setInt(3, selected_act_id);
                        }
                        rs = pstmt1.executeQuery();      // execute the prepared stmt
                        
                        debugMap.put("search_lesson_"+((allAct)?"all_act":selected_act_id)+"_"+date,username);

//               if (rs.next()) {               // just get the first one as each lesson can have multiple entries
                        while (rs.next()) {              // get all lesson times that have a num greater than zero (not a subsequent time)

                            proid = rs.getInt(1);
                            time = rs.getInt(2);
                            ltype = rs.getString(3);
                            lesson_id = rs.getInt("recid");
                            returned_activity_id = rs.getInt("activity_id");

                            activity_name = getActivity.getActivityName(returned_activity_id, con);
                            
                            debugMap.put("loop_lesson_"+rs.getLong(6),time);

                            hr = time / 100;
                            min = time - (hr * 100);

                            ampm = " AM";
                            if (hr == 12) {
                                ampm = " PM";
                            }
                            if (hr > 12) {
                                ampm = " PM";
                                hr = hr - 12;    // convert to conventional time
                            }

                            stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            dayname = day_table[col];                           // get the name of this day

                            String calDate = month + "/" + day + "/" + year;       // set parm for form

                            //
                            //  Display the lesson time as a clickable link (see form & js above)
                            //
                            //     refer to 'web utilities/foretees2.css' for style info
                            //
                            // Only display as selectable link if on current activity
                            if (club.equals("interlachenspa")) {
                                item_name = buildItemDescription("Spa Reservation", stime2+ampm);
                            } else {
                                item_name = buildItemDescription(activity_name + " " + sysLingo.TEXT_Lesson, stime2+ampm);
                            }/*
                            out.print("<div class=\"item_container\">");
                            if (returned_activity_id == activity_id) {
                                out.println("<a href=\"javascript: exeLtimeForm('" + proid + "','" + calDate + "','" + date + "','" + time + "','" + dayname + "','" + ltype + "','" + lesson_id + "','" + activity_id + "')\" class=\"tee_list_color_5\">"
                                        + item_name + "</a>");
                            } else {
                                out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                            }
                            out.print("</div>");*/

                            // populate this!!! (exeLtimeForm - LtimeForm)
                            hashMap.clear();
                            hashMap.put("proid", proid);
                            hashMap.put("calDate", calDate);
                            hashMap.put("date", date);
                            hashMap.put("time", time);
                            hashMap.put("dayname", dayname);
                            hashMap.put("day", dayname);
                            hashMap.put("ltype", ltype);
                            hashMap.put("lesson_id", lesson_id);
                            hashMap.put("activity_id", returned_activity_id);
                            hashMap.put("reqtime", "yes");
                            hashMap.put("index", "999");


                            Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                            data_map.put("url", "Member_lesson"); 
                            data_map.put("base_url", (returned_activity_id == 0) ? golf_base : Utilities.getExternalBaseUrl(req, returned_activity_id, club, app_mode));
                            data_map.put("data", hashMap);

                            Map<String, String> calendar_entry = new HashMap<String, String>();
                            calendar_entry.put("activityId", String.valueOf(returned_activity_id));
                            calendar_entry.put("displayText", item_name);
                            calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                            calendar_entry.put("cssClass", "tee_list_color_5");
                            calendar_day.put("entry_" + entry, calendar_entry);
                            
                            entry++;

                        } // end of WHILE

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting lesson times for " + club + ", Day=" + day + ", username: " + username + ", Error: " + e1.getMessage());                           // log it

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } // end of IF lessons


                //**********************************************************
                //  Check for any lessongrps for this day
                //**********************************************************
                //
                if (lessongrpA[day] == 1) {        // if any lessongrps exist for this day

                    try {

                        lesson_id = 0;

                        pstmt1 = con.prepareStatement(
                                "SELECT proid, lname "
                                + "FROM lgrpsignup5 "
                                + "WHERE memid = ? AND date = ?");

                        pstmt1.clearParameters();
                        pstmt1.setString(1, username);
                        pstmt1.setLong(2, date);
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            proid = rs.getInt(1);
                            lgname = rs.getString(2);

                            //
                            //  Get the start time for this group lesson
                            //
                            PreparedStatement pstmt7d = con.prepareStatement(
                                    "SELECT stime, activity_id, lesson_id "
                                    + "FROM lessongrp5 WHERE proid = ? AND lname = ? "
                                    + (!allAct ? "AND activity_id = ?" : ""));

                            pstmt7d.clearParameters();
                            pstmt7d.setInt(1, proid);
                            pstmt7d.setString(2, lgname);
                            if (!allAct) {
                                pstmt7d.setInt(3, selected_act_id);
                            }

                            rs2 = pstmt7d.executeQuery();

                            if (rs2.next()) {

                                time = rs2.getInt("stime");
                                returned_activity_id = rs2.getInt("activity_id");
                                lesson_id = rs2.getInt("lesson_id");

                                hr = time / 100;
                                min = time - (hr * 100);

                                ampm = " AM";
                                if (hr == 12) {
                                    ampm = " PM";
                                }
                                if (hr > 12) {
                                    ampm = " PM";
                                    hr = hr - 12;    // convert to conventional time
                                }

                                stime2 = hr + ":" + Utilities.ensureDoubleDigit(min);

                            } else {                 // end of IF
                                continue;  // If no data found (means it was a different activity), skip over the rest of the loop for this iteration
                            }
                            pstmt7d.close();

                            //
                            //  Display the lesson time as a clickable link (see form & js above)
                            //
                            //     refer to 'web utilities/foretees2.css' for style info
                            //
/*
                            out.print("<div class=\"item_container\">");
                            item_name = buildItemDescription("Group Lesson", stime2+ampm);
                            if (returned_activity_id == activity_id) {
                                out.println("<a href=\"javascript: exeLgroupForm('" + proid + "','" + date + "','" + lgname + "','" + lesson_id + "','" + activity_id + "')\" class=\"tee_list_color_5\">"
                                        + item_name + "</a>");
                            } else {
                                out.println("<div class=\"tee_list_color_5\">" + item_name + "</div>");
                            }
                            out.print("</div>");
*/
                            
                            if (returned_activity_id == activity_id) {

                                activity_name = getActivity.getActivityName(returned_activity_id, con);
                                item_name = buildItemDescription(activity_name + " " + sysLingo.TEXT_Lesson, stime2+ampm);

                                // exeLgroupForm - LgroupForm
                                hashMap.clear();
                                hashMap.put("proid", proid);
                                hashMap.put("date", date);
                                hashMap.put("lgname", lgname);
                                hashMap.put("lesson_id", lesson_id);
                                hashMap.put("ltype", ltype);
                                hashMap.put("lesson_id", lesson_id);
                                hashMap.put("activity_id", returned_activity_id);

                                Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                data_map.put("url", "Member_lesson"); // post?
                                data_map.put("base_url", golf_base);
                                data_map.put("data", hashMap);

                                Map<String, String> calendar_entry = new HashMap<String, String>();
                                calendar_entry.put("activityId", String.valueOf(returned_activity_id));
                                calendar_entry.put("displayText", item_name);
                                calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                calendar_entry.put("cssClass", "tee_list_color_5");
                                calendar_day.put("entry_" + entry, calendar_entry);

                                entry++;
                            }

                        } // end of WHILE

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting group lesson times for " + club + ", Day=" + day + ", username: " + username + ", Error: " + e1.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } // end of IF lessongrps


                //**********************************************************
                //  Check for any events for this day
                //**********************************************************
                //
                if (eventA[day] == 1) {        // if any events  exist for this day

                    try {

                        int event_id = 0;
                        int event_type = 0;
                        int event_hr = 0;
                        int event_min = 0;

                        String event_ampm = "";

                        if (club.equals("tcclub")) {

                            pstmt1 = con.prepareStatement(
                                    "SELECT name, coursename, signup, activity_id, event_id, act_hr, act_min, type "
                                    + "FROM events2b WHERE date = ? AND inactive = 0 "
                                    + (!allAct ? "AND activity_id = ? " : " ")
                                    + "ORDER BY act_hr, act_min");

                        } else {

                            pstmt1 = con.prepareStatement(
                                    "SELECT name, coursename, signup, activity_id, event_id, act_hr, act_min, type "
                                    + "FROM events2b WHERE date = ? AND gstOnly = 0 AND inactive = 0 "
                                    + (!allAct ? "AND activity_id = ? " : " ")
                                    + "ORDER BY act_hr, act_min");
                        }

                        pstmt1.clearParameters();
                        pstmt1.setLong(1, date);
                        if (!allAct) {
                            pstmt1.setInt(2, selected_act_id);
                        }
                        rs = pstmt1.executeQuery();

                        while (rs.next()) {

                            ename = rs.getString("name");
                            course = rs.getString("coursename");
                            signUp = rs.getInt("signup");
                            event_hr = rs.getInt("act_hr");
                            event_min = rs.getInt("act_min");
                            event_type = rs.getInt("type");
                            //stime = Utilities.getSimpleTime(rs.getInt("stime"));
                            returned_activity_id = rs.getInt("activity_id");
                            event_id = rs.getInt("event_id");

                            try {
                                root_id = getActivity.getRootIdFromActivityId(returned_activity_id, con);
                            } catch (Exception ignore) {
                                root_id = returned_activity_id;
                            }

                            activity_name = getActivity.getActivityName(root_id, con);

                            if (activity_name.equals("")) {
                                activity_name = "Golf";
                            }

                            hashMap.clear();
                            hashMap.put("type", "Member_events2");
                            hashMap.put("name", ename);
                            hashMap.put("course", course);
                            hashMap.put("activity_id", returned_activity_id);
                            hashMap.put("base_url", Utilities.getBaseUrl(req, returned_activity_id, club, app_mode));
                            hashMap.put("index", 999);       // was 995 (teelist_list) ???

                            //
                            //  Check if this member is signed up
                            //
                            boolean signedup = false;
                            int id = 0;

                            if (event_type == 1 || returned_activity_id != 0) {

                                // Create time value for actual start time of event if it's a shotgun event
                                event_ampm = " AM";

                                if (event_hr == 0) {

                                    event_hr = 12;                 // change to 12 AM (midnight)

                                } else {

                                    if (event_hr == 12) {

                                        event_ampm = " PM";         // change to Noon
                                    }
                                }
                                if (event_hr > 12) {

                                    event_hr = event_hr - 12;
                                    event_ampm = " PM";             // change to 12 hr clock
                                }

                                //  convert time to hour and minutes
                                stime = event_hr + ":" + String.format("%02d", event_min) + event_ampm;

                            } else {

                                stime = "";
                            }

                            if (signUp != 0) {           // if members can signup

                                PreparedStatement pstmte = con.prepareStatement(
                                        "SELECT id, wait " +
                                        "FROM evntsup2b " +
                                        "WHERE name = ? AND inactive = 0 AND " +
                                            "(username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? OR username5 = ?)");

                                pstmte.clearParameters();
                                pstmte.setString(1, ename);
                                pstmte.setString(2, username);
                                pstmte.setString(3, username);
                                pstmte.setString(4, username);
                                pstmte.setString(5, username);
                                pstmte.setString(6, username);
                                rs2 = pstmte.executeQuery();

                                if (rs2.next()) {

                                    signedup = true;              // set member signed up
                                    wait = rs2.getInt("wait");
                                    id = rs2.getInt("id");
                                }
                                pstmte.close();


                                if (signedup == true) {     // if member is registered

                                    //
                                    //  Display the event name as a clickable link (see form & js above)
                                    //
                                    if (returned_activity_id == activity_id || returned_activity_id == selected_act_id || selected_act_id == ProcessConstants.ALL_ACTIVITIES) {


                                        Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                        data_map.put("url", "Member_events2");
                                        data_map.put("base_url", (returned_activity_id == 0) ? golf_base : Utilities.getExternalBaseUrl(req, returned_activity_id, club, app_mode));
                                        data_map.put("data", hashMap);

                                        Map<String, String> calendar_entry = new HashMap<String, String>();
                                        calendar_entry.put("activityId", String.valueOf(returned_activity_id));
                                        calendar_entry.put("registered", "true");
                                        calendar_entry.put("waitlisted", String.valueOf(wait != 0));
                                        calendar_entry.put("displayText", buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - " + ((wait == 0) ? "Registered" : "Wait List"), stime));
                                        calendar_entry.put("cssClass", "tee_list_color_1");
                                        calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                        calendar_day.put("entry_" + entry, calendar_entry);

                                        entry++;
                                        
                                      //out.println(iCalLink(id, "evntsup"));
/*
                                        out.println("<a class=\"event_button tee_list_color_1\" href=\"#\" "
                                                + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");

                                        if (wait == 0) {
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Registered", stime));

                                        } else {
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Wait List", stime));
                                        }

                                        out.println("</a>");
*/
                                    } else {
/*
                                        out.println("");
                                        if (wait == 0) {

                                            out.println(iCalLink(id, "eventsup"));

                                            out.println("<div class=\"tee_list_color_1\">");
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Registered", stime));
                                            out.println("</div>");

                                        } else {
                                            out.println("<div class=\"tee_list_color_1\">");
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Wait List", stime));
                                            out.println("</div>");
                                        }
*/
                                    }

                                } else {     // member not registered
/*
                                    //  Custom to check for dependents registered for this event
                                    if ((club.equals("denvercc") || club.startsWith("demo")) && !mtype.equalsIgnoreCase("Dependent")) {  // Custom to check for Dependents registered

                                        //  Get each dependent and check if they are registered
                                        String mNumD = "";
                                        PreparedStatement pstmtDenver = null;
                                        PreparedStatement pstmtDenver2 = null;
                                        ResultSet rsDenver = null;
                                        ResultSet rsDenver2 = null;

                                        try {

                                            pstmtDenver = con.prepareStatement(
                                                    "SELECT memNum "
                                                    + "FROM member2b WHERE username = ?");      // get this member's mNum

                                            pstmtDenver.clearParameters();
                                            pstmtDenver.setString(1, user);
                                            rsDenver = pstmtDenver.executeQuery();

                                            if (rsDenver.next()) {

                                                mNumD = rsDenver.getString("memNum");
                                            }
                                            pstmtDenver.close();

                                            if (!mNumD.equals("")) {        // if member number found for this member

                                                //  Locate any Dependents for this member and search their event signups

                                                pstmtDenver = con.prepareStatement(
                                                        "SELECT username "
                                                        + "FROM member2b WHERE memNum = ? AND m_type = 'Dependent'");

                                                pstmtDenver.clearParameters();
                                                pstmtDenver.setString(1, mNumD);
                                                rsDenver = pstmtDenver.executeQuery();

                                                loopDenver:
                                                while (rsDenver.next()) {

                                                    String userD = rsDenver.getString("username");   // get the dependent's username and look for any event signups for this child

                                                    pstmtDenver2 = con.prepareStatement(
                                                            "SELECT id, wait "
                                                            + "FROM evntsup2b WHERE name = ? AND inactive = 0 "
                                                            + "AND (username1 = ? OR username2 = ? OR username3 = ? OR username4 = ? "
                                                            + "OR username5 = ?)");

                                                    pstmtDenver2.clearParameters();
                                                    pstmtDenver2.setString(1, ename);
                                                    pstmtDenver2.setString(2, userD);
                                                    pstmtDenver2.setString(3, userD);
                                                    pstmtDenver2.setString(4, userD);
                                                    pstmtDenver2.setString(5, userD);
                                                    pstmtDenver2.setString(6, userD);
                                                    rsDenver2 = pstmtDenver2.executeQuery();

                                                    if (rsDenver2.next()) {

                                                        signedup = true;              // set member/dependent signed up
                                                        wait = rsDenver2.getInt("wait");
                                                        id = rsDenver2.getInt("id");

                                                        out.print("<div class=\"item_container\">");

                                                        //
                                                        //  Display the event name as a clickable link if event is for current activity, or the selected id, or selected id is ALL
                                                        //
                                                        if (returned_activity_id == activity_id || returned_activity_id == selected_act_id || selected_act_id == ProcessConstants.ALL_ACTIVITIES) {

                                                            out.println(iCalLink(id, "evntsup"));
                                                            out.print("<a class=\"event_button tee_list_color_1\" href=\"#\" "
                                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");

                                                            if (wait == 0) {

                                                                out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Child Registered", stime));

                                                            } else {

                                                                out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Child on Wait List", stime));
                                                            }

                                                        } else {

                                                            if (wait == 0) {

                                                                out.println(iCalLink(id, "eventsup"));

                                                                out.println("<div class=\"tee_list_color_1\">");
                                                                out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Child Registered", stime));
                                                                out.println("</div>");

                                                            } else {

                                                                out.println("<div class=\"tee_list_color_1\">");
                                                                out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : "") + " - Child on Wait List", stime));
                                                                out.println("</div>");
                                                            }
                                                        }

                                                        break loopDenver;       // don't need to check any other dependents
                                                    }
                                                    pstmtDenver2.close();

                                                }            // check all dependents
                                                pstmtDenver.close();

                                            }    // end of IF mNumD (if member number found)

                                        } catch (Exception e9) {

                                            Utilities.logError("Common_webapi.getCalendarData() Error processing events (custom) for " + club + ", Day=" + day + ", User: " + user + ", Error: " + e9.getMessage());

                                        } finally {

                                            try {
                                                if (rsDenver != null) rsDenver.close();
                                            } catch (Exception ignored) {
                                            }
                                            try {
                                                if (rsDenver2 != null) rsDenver2.close();
                                            } catch (Exception ignored) {
                                            }

                                            try {
                                                if (pstmtDenver != null) pstmtDenver.close();
                                            } catch (Exception ignored) {
                                            }
                                            try {
                                                if (pstmtDenver2 != null) pstmtDenver2.close();
                                            } catch (Exception ignored) {
                                            }

                                        }
                                    }      // end of custom for dependents
*/
                                    if (signedup == false) {     // if dependents not registered either - add link to event info

                                        //out.println(iCalLink(event_id, "event"));

                                        if (returned_activity_id == activity_id || returned_activity_id == selected_act_id || selected_act_id == ProcessConstants.ALL_ACTIVITIES) {

                                            Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                            data_map.put("url", "Member_events2");
                                            data_map.put("base_url", (returned_activity_id == 0) ? golf_base : Utilities.getExternalBaseUrl(req, returned_activity_id, club, app_mode));
                                            data_map.put("data", hashMap);

                                            Map<String, String> calendar_entry = new HashMap<String, String>();
                                            calendar_entry.put("activityId", String.valueOf(returned_activity_id));
                                            calendar_entry.put("registered", "false");
                                            calendar_entry.put("waitlisted", "false");
                                            calendar_entry.put("displayText", buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                            calendar_entry.put("cssClass", "tee_list_color_2");
                                            calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                            calendar_day.put("entry_" + entry, calendar_entry);

                                            entry++;
/*
                                            out.print("<a class=\"event_button tee_list_color_2\" href=\"#\" "
                                                    + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                            //out.println("      <a href=\"javascript: exeEventForm('" + ename + "','" + course + "')\" class=tee_list_color_2>");
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                            out.println("</a>");
*/
                                        } else {/*
                                            out.println("<div class=\"tee_list_color_2\">");
                                            out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                            out.println("</div>");*/
                                        }
                                    }
                                }


                            } else {     // no sign up available

                              //out.println(iCalLink(event_id, "event"));
                                if (allAct || returned_activity_id == activity_id) {

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "Member_events2");
                                    data_map.put("base_url", (returned_activity_id == 0) ? golf_base : Utilities.getExternalBaseUrl(req, returned_activity_id, club, app_mode));
                                    data_map.put("data", hashMap);

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", String.valueOf(returned_activity_id));
                                    calendar_entry.put("registered", "false");
                                    calendar_entry.put("waitlisted", "false");
                                    calendar_entry.put("displayText", buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                    calendar_entry.put("cssClass", "tee_list_color_3");
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                    entry++;
/*
                                    out.print("<a class=\"event_button tee_list_color_3\" href=\"#\" "
                                            + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");
                                    //out.println("      <a href=\"javascript: exeEventForm2('" + ename + "')\" class=tee_list_color_3>");
                                    out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                    out.println("</a>");
 */
                                } else {/*
                                    out.println("<div class=\"tee_list_color_3\">");
                                    out.println(buildItemDescription(ename + (selected_act_id == ProcessConstants.ALL_ACTIVITIES ? " - (" + activity_name + ")" : ""), stime));
                                    out.println("</div>");*/
                                }

                            } // end of IF signup

                        } // end of WHILE events

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error processing events for " + club + ", Day=" + day + ", username: " + username + ", Error: " + e1.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } // end of IF events


                //
                // Check for any wait list signups for this day
                //
                if (waitlistA[day] != 0) {

                    try {

                        pstmt1 = con.prepareStatement(
                                "SELECT wl.wait_list_id, wl.course, wls.date, wls.ok_stime, wls.ok_etime, wls.wait_list_signup_id, wl.color, " +
                                    "DATE_FORMAT(wls.date, '%W') AS day_name, DATE_FORMAT(wls.date, '%Y%m%d') AS dateymd " +
                                "FROM wait_list_signups wls " +
                                "LEFT OUTER JOIN wait_list wl ON wls.wait_list_id = wl.wait_list_id " +
                                "WHERE wls.wait_list_signup_id = ?");

                        pstmt1.clearParameters();
                        pstmt1.setInt(1, waitlistA[day]);
                        rs = pstmt1.executeQuery();

                        if (rs.next()) {

                            if (activity_id == 0) {

                                hashMap.clear();
                                hashMap.put("type", "Member_waitlist");
                                hashMap.put("waitListId", rs.getInt("wait_list_id"));
                                hashMap.put("course", rs.getString("course"));
                                hashMap.put("returnCourse", rs.getString("course"));
                                hashMap.put("index", 995);
                                hashMap.put("date", rs.getInt("dateymd"));

                                //  style=\"background-color:"+rs.getString("color")+";\"
                                //out.print("<a class=\"waitlist_button calendar_link tee_list_color_7\" href=\"#\" "
                                //        + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">");

                                //out.println("      <a class=\"mWLsignup\" href=\"javascript:void(0)\" "
                                //        + "&index=999&day=" + rs.getString("day_name") + "&course=" + rs.getString("course") + "&returnCourse=" + rs.getString("course") + "'\" >");
                                //out.print(SystemUtils.getSimpleTime(rs.getInt("ok_stime")) + " - " + SystemUtils.getSimpleTime(rs.getInt("ok_etime")));
                                //out.print("Wait List Sign-up");
                                //out.println("</a>");

                                Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                data_map.put("url", "Member_waitlist");
                                data_map.put("base_url", golf_base); // wait lists are all golf
                                data_map.put("data", hashMap);

                                Map<String, String> calendar_entry = new HashMap<String, String>();
                                calendar_entry.put("activityId", "0"); // wait lists are all golf
                                calendar_entry.put("displayText", buildItemDescription("Wait List Sign-up", stime));
                                calendar_entry.put("cssClass", "tee_list_color_7");
                                calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                calendar_day.put("entry_" + entry, calendar_entry);

                                entry++;

                            } else {
                                //out.println("<div class=\"tee_list_color_7\">Wait List Sign-up</div>");
                            }

                        }

                    } catch (Exception exc) {
                        
                        Utilities.logError("Common_webapi.getCalendarData() Error processing wait list signups for " + club + ", Day=" + day + ", username: " + username + ", Error: " + exc.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                    }

                } // end IF wait list signup found


                // if FlxRez enabled for club and we are requesting a flxrez activity or ALL activities
                if (parm.genrez_mode != 0 && activity_id != 0 && activity_id != ProcessConstants.DINING_ACTIVITY_ID) {

                    //
                    // Check for any activity signups for this day
                    //
                    if (activitiesA[day] != 0) {

                        try {

                            pstmt1 = con.prepareStatement(
                                    "SELECT * FROM ("
                                    + "SELECT a.activity_id, a.sheet_id, "
                                    + "IF(a.related_ids <> '', a.related_ids, a.sheet_id) as related_ids, "
                                    + "DATE_FORMAT(a.date_time, '%Y%m%d') AS dateymd, "
                                    + "DATE_FORMAT(a.date_time, '%l:%i %p') AS pretty_time "
                                    + "FROM activity_sheets a, activity_sheets_players ap "
                                    + "WHERE a.sheet_id = ap.activity_sheet_id "
                                    + (selected_act_id != ProcessConstants.ALL_ACTIVITIES ? "AND a.activity_id IN (" + selected_act_children + ") " : "")
                                    + "AND DATE_FORMAT(a.date_time, '%Y%m%d') = ? "
                                    + "AND ap.username = ? "
                                    + "ORDER BY a.date_time"
                                    + ") AS act_times "
                                    + "GROUP BY related_ids");

                            pstmt1.clearParameters();
                            pstmt1.setLong(1, date);
                            pstmt1.setString(2, username);
                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                try {
                                    root_id = getActivity.getRootIdFromActivityId(rs.getInt("activity_id"), con);
                                } catch (Exception ignore) {
                                    root_id = rs.getInt("activity_id");
                                }

                                activity_name = getActivity.getActivityName(root_id, con);

                                item_name = buildItemDescription(activity_name, rs.getString("pretty_time"));

                                // only include if the root id matches the requested id
                                if (root_id == activity_id || activity_id == 999) {
                                    hashMap.clear();
                                    hashMap.put("type", "Member_activity_slot");
                                    hashMap.put("slot_id", rs.getInt("sheet_id"));
                                    hashMap.put("date", rs.getString("dateymd"));
                                    hashMap.put("index", 999);

                                    /*out.print("<a class=\"activity_button calendar_link tee_list_color_6\" href=\"#\" "
                                            + "data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(hashMap)) + "\">"
                                            + item_name + "</a>");*/

                                    //out.println("      <a  class=\"tee_list_color_6\" href=\"javascript:void(0)\" ");
                                    //out.println("      onclick=\"top.location.href='Member_activity_slot?slot_id=" + rs.getInt("sheet_id") + "&date=" + rs.getInt("dateymd") + "&index=999'\">");
                                    //out.print(activity_name + " at " + rs.getString("pretty_time"));
                                    //out.println("</a>");

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "Member_activity_slot");
                                    data_map.put("base_url", Utilities.getExternalBaseUrl(req, root_id, club, app_mode));
                                    data_map.put("data", hashMap);

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", String.valueOf(root_id));
                                    calendar_entry.put("displayText", item_name);
                                    calendar_entry.put("cssClass", "tee_list_color_7"); // is this right?
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                    entry++;

                                } else {
                                    //out.println("<div class=\"tee_list_color_6\">" + item_name + "</div>");
                                }


                            } // end of while loop

                        } catch (Exception exc) {

                            Utilities.logError("Common_webapi.getCalendarData() Error loading activity times: " + club + ", Day=" + day + ", username: " + username + ", Error: " + exc.toString());

                        } finally {

                            try { rs.close(); }
                            catch (Exception ignored) {}

                            try { pstmt1.close(); }
                            catch (Exception ignored) {}

                        }

                    } // end if FlxRez reservation found
                }



                // add a check here to see if dining is enabled and if so loop the dining event array
                if (organization_id != 0 && diningA[day] != 0 && (selected_act_id == ProcessConstants.ALL_ACTIVITIES || selected_act_id == ProcessConstants.DINING_ACTIVITY_ID)) {

                    Connection con_d = null;

                    PreparedStatement pstmt2 = null;
                    
                    try {

                        String state = "";
                        int dtime = 0;

                        con_d = Connect.getDiningCon();

                        if (con_d != null) {

                            pstmt1 = con_d.prepareStatement(""
                                    + "SELECT id, name, members_can_make_reservations, to_char(start_time, 'HH24MI') AS stime, to_char(end_time, 'HH24MI') AS etime "
                                    + "FROM events "
                                    + "WHERE organization_id = ? AND to_char(date, 'YYYYMMDD')::int = ? AND cancelled = false "
                                    + "ORDER BY stime");

                            pstmt1.clearParameters();
                            pstmt1.setInt(1, organization_id);
                            pstmt1.setLong(2, date);

                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                state = ""; // reset
                                dtime = Integer.parseInt(rs.getString("stime")); // first set to the event start time

                                // lookup this member in the reservations table to see if they are already signed up for this dining event
                                pstmt2 = con_d.prepareStatement(""
                                        + "SELECT state, to_char(time, 'HH24MI') AS stime  "
                                        + "FROM reservations "
                                        + "WHERE "
                                        + "category = 'event' AND state <> 'cancelled' AND "
                                        + "organization_id = ? AND "
                                        + "event_id = ? AND "
                                        + "person_id = ?");

                                pstmt2.setInt(1, organization_id);
                                pstmt2.setInt(2, rs.getInt("id"));
                                pstmt2.setInt(3, person_id);

                                rs2 = pstmt2.executeQuery();

                                if (rs2.next()) {

                                    state = rs2.getString("state");
                                    dtime = Integer.parseInt(rs2.getString("stime")); // override dtime to time of actual reservation if found

                                }
/*
                                String mNum = "";

                                if (user != null && !user.equals("")) {

                                    mNum = Utilities.getmNum(user, con);      // get this mem's member number
                                }
*/

                                item_name = buildItemDescription(rs.getString("name"), Utilities.getSimpleTime(dtime));

                                // if member can signup for the dining event AND they are not already signed-up then make it a link
                                // and show their reservation status, otherwise just display the name of the event

                                // only make it a link if they are able to sign up but have not yet signed up.
                                if (rs.getBoolean("members_can_make_reservations") == false && 1==2) {

                                    // event does not allow online signup - display non-link
                                    //out.print("<div class=\"tee_list_color_10\">" + item_name + "</div>");

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "");
                                    data_map.put("base_url", dining_base);
                                    data_map.put("data", "");

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", String.valueOf(ProcessConstants.DINING_ACTIVITY_ID));
                                    calendar_entry.put("displayText", item_name);
                                    calendar_entry.put("cssClass", "tee_list_color_10");
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                } else if (state.equals("") || state.equals("cancelled")) {

                                    // event allows signups but user is not signed up or has a cancelled signup
                                    //out.print("<a href=\""+dining_base+"Dining_home?nowrap&event_popup&event_id=" + rs.getInt("id") + "\" class=\"tu_iframe_620x400 tee_list_color_10\">"); // tu_iframe_620x400
                                    //out.print(item_name);
                                    //out.println("</a>");

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "Dining_home?nowrap&event_popup&event_id=" + rs.getInt("id"));
                                    data_map.put("base_url", dining_base);
                                    data_map.put("data", "");

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", String.valueOf(ProcessConstants.DINING_ACTIVITY_ID));
                                    calendar_entry.put("displayText", item_name);
                                    calendar_entry.put("cssClass", "tee_list_color_10");
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                } else {

                                    // user is already signed up - display link to their reservation
                                    //out.print("<a href=\""+dining_base+"Dining_home?nowrap&event_popup&event_id=" + rs.getInt("id") + "\" class=\"tu_iframe_620x400 tee_list_color_9\">");
                                    //out.print(item_name + " - Registered");
                                    //out.println("</a>");

                                    Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                    data_map.put("url", "Dining_home?nowrap&event_popup&event_id=" + rs.getInt("id"));
                                    data_map.put("base_url", dining_base);
                                    data_map.put("data", "");

                                    Map<String, String> calendar_entry = new HashMap<String, String>();
                                    calendar_entry.put("activityId", String.valueOf(ProcessConstants.DINING_ACTIVITY_ID));
                                    calendar_entry.put("displayText", item_name + " - Registered");
                                    calendar_entry.put("cssClass", "tee_list_color_9");
                                    calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                    calendar_day.put("entry_" + entry, calendar_entry);

                                    //out.print("<span class=\"item_state\">(" + state + ")</span>");

                                    //out.println("</div>");
                                }

                                entry++;

                            } // end while loop of events

                        } // if con_d not null

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting specific dinning event info for " + club + ", Error: " + e1.getMessage());

                    } finally {


                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                        try { con_d.close(); }
                        catch (Exception ignored) {}

                    }

                } // end IF dining event signup found


                //
                // If a dining reservation was found for this day then output its details
                //
                if (organization_id != 0 && diningR[day] != 0 && (selected_act_id == ProcessConstants.ALL_ACTIVITIES || selected_act_id == ProcessConstants.DINING_ACTIVITY_ID)) {

                    Connection con_d = null;

                    try {

                        int dtime = 0;

                        con_d = Connect.getDiningCon();

                        if (con_d != null) {

                            pstmt1 = con_d.prepareStatement(""
                                    + "SELECT id, to_char(time, 'HH24MI') AS stime "
                                    + "FROM reservations "
                                    + "WHERE "
                                    + " category = 'dining' AND state <> 'cancelled' AND "
                                    + " organization_id = ? AND "
                                    + " person_id = ? AND "
                                    + " to_char(date, 'YYYYMMDD')::int = ? "
                                    + "ORDER BY time");

                            pstmt1.clearParameters();
                            pstmt1.setInt(1, organization_id);
                            pstmt1.setInt(2, person_id);
                            pstmt1.setLong(3, date);

                            rs = pstmt1.executeQuery();

                            while (rs.next()) {

                                dtime = Integer.parseInt(rs.getString("stime"));

                                item_name = buildItemDescription("Dining Reservation", Utilities.getSimpleTime(dtime));

                                //out.print("<div class=\"item_container\">");
                                //out.println("<a class=\"tee_list_color_9\" "
                                //        + "href=\""+dining_base+"Dining_slot?action=edit&orig=calendar&reservation_id=" + rs.getInt("id") + "\">"
                                //        + item_name + "</a>");
                                //out.print("</div>");

                                Map<String, Object> data_map = new LinkedHashMap<String, Object>();
                                data_map.put("url", "Dining_slot?action=edit&orig=calendar&reservation_id=" + rs.getInt("id"));
                                data_map.put("base_url", dining_base);
                                data_map.put("data", "");

                                Map<String, String> calendar_entry = new HashMap<String, String>();
                                calendar_entry.put("activityId", String.valueOf(ProcessConstants.DINING_ACTIVITY_ID));
                                calendar_entry.put("displayText", item_name);
                                calendar_entry.put("cssClass", "tee_list_color_9");
                                calendar_entry.put("data-ftjson", gson_obj.toJson(data_map));
                                calendar_day.put("entry_" + entry, calendar_entry);

                                entry++;
                            }

                        }

                    } catch (Exception e1) {

                        Utilities.logError("Common_webapi.getCalendarData() Error getting specific dinning reservation info for " + club + ", Error: " + e1.getMessage());

                    } finally {

                        try { rs.close(); }
                        catch (Exception ignored) {}

                        try { pstmt1.close(); }
                        catch (Exception ignored) {}

                        try { con_d.close(); }
                        catch (Exception ignored) {}

                    }

                } // end if dining rez found


                response_map.put("day" + day, calendar_day);

                } // end skipping of past days in current month

                day++; // increment the day-of-the-month value

                days_done++; // incement the days processed count (used for spanning months with custom dates)


                if (custom_dates && day > days_in_month && days_done < numDays) {

                    day = 1;
                    month++;

                    if (month == 13) {
                        month = 1;
                        year++;
                    }

                } // end month spanning code

            } // end if while loop for days in month

        } // end for month loop

        //response_map.put("total", String.valueOf(i3) + ":" + String.valueOf(i4));

        //response_map.put("daysDone", String.valueOf(days_done));

        response_map.put("processingTime", (System.currentTimeMillis() - benchmark_start) + "ms");
        response_map.put("debug", debugMap);

        //return gson_obj.toJson(container_map);
        return container_map;

    }


    private String buildItemDescription(String name, String time) {

        return name + (!time.equals("") ? "<br>" + time + "" : "");
      //return (!time.equals("") ? "<b>" + time + "</b>" + ": " + name : name);

    }

    private String iCalURL(int id, String type, String user, String club) {

        return "data_loader?ical&id=" + id + "&id_type=" + type + "&user=" + user + "&club=" + club;

    }


    private static Map buildMapEntry(int activity_id, int day, int time, String display_text, String text_color, String link_url) {

        
        Map<String, String> calendar_entry = new HashMap<String, String>();

        calendar_entry.put("activityId", String.valueOf(activity_id));
        calendar_entry.put("date", String.valueOf(day));
        calendar_entry.put("time", String.valueOf(time));
        calendar_entry.put("displayText", display_text);
        calendar_entry.put("textColor", text_color);
        calendar_entry.put("linkURL", link_url);

        return (calendar_entry);
    }
    
    private static Map<String, Object> errorMessageObj(String errorCode, String errorMessage) {

        Map<String, Object> response_map = new HashMap<String, Object>();

        response_map.put("errorCode", errorCode);
        response_map.put("errorMessage", errorMessage);
        response_map.put("errorTime", System.currentTimeMillis());  // the difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
        response_map.put("serverId", ProcessConstants.SERVER_ID);

        return response_map;

    }


    private static String errorMessage(String errorCode, String errorMessage) {

        return encodeMap(wrapErrorMap(errorMessageObj(errorCode, errorMessage)));

    }
    
    private static Map<String, Object> wrapMap(String type, Map<String, Object> innerMap){
        
        Map<String, Object> container_map = new HashMap<String, Object>();

        container_map.put(type, innerMap );
        return container_map;
        
    }
    
    private static Map<String, Object> wrapErrorMap(Map<String, Object> errorMap){
        return wrapMap("foreTeesErrorResp", errorMap);
    }
    
    private static Map<String, Object> wrapErrorMessage(String errorCode, String errorMessage){
        return wrapErrorMap(errorMessageObj(errorCode, errorMessage));
    }
    
    private static String encodeMap(Map<String, Object> map){
        
        Gson gson_obj = new Gson();
        return gson_obj.toJson(map);
        
    }


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
