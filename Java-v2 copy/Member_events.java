
/***************************************************************************************
 *   Member_events:  This servlet will search the events table for any events scheduled
 *                   for anytime after today's date.
 *
 *
 *
 *   called by:  member_main.htm
 *
 *   created: 1/11/2002   Bob P.
 *
 *   last updated:
 *
 *        1/23/13   Baltusrol GC (baltusrolgc) - Do not display events that don't allow online sign-ups (case 2218).
 *        1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *        9/06/12   Add a style class (event_list_td) to the td's in the event list table to make the font larger.
 *        9/06/12   Updated outputTopNav calls to also pass the HttpServletRequest object.
 *        4/10/12   The Sagamore Club (sagamore) - Do not display events that don't allow online sign-ups (case 2141).
 *        4/05/12   Aronimink GC (aronimink) - Do not display events that don't allow online sign-ups.
 *        3/08/12   New Skin - do not show the category instructions if there are no categories defined.
 *        1/19/12   Add support for new skin
 *       12/20/11   Added support for filtering the list of available events by defined event categories (case 2076).
 *        8/15/11   Olympic Club (olyclub) - Do not display events that do not allow online signups (case 2067).
 *        8/15/11   Monterey Peninsula CC (mpccpb) - Do not display events that do not allow online signups.
 *        4/25/11   Rolling Hills GC (rollinghillsgc) - Do not display events that do not allow online signups (case 1976).
 *        4/13/11   Add note in instruction box to let members know they can change the order of the event list by clicking on the heading.
 *        4/06/11   Ramsey CC (ramseycountryclub) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        5/21/10   Awbrey Glen (awbreyglen) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/11/10   Elmcrest CC (elmcrestcc) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        3/11/10   Belle Haven (bellehaven) - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *       12/09/09   When looking for events only check those that are active.
 *       11/04/09   Blackstone - Sort by 'season' field first when sorting by date so season long events rise to the top of the list
 *        9/28/09   Added support for Activities
 *        2/10/09   Sort by gender for Patterson Club
 *       12/10/08   Allow users to access events that do not allow online signup
 *        8/11/08   Stonebridge Ranch - limit the course options based on mship type (case 1529).
 *        4/09/08   Fix for default sorting (was reverse sorting for females)
 *        4/02/08   Change sorting for season long events, now sorts by cutoff date
 *        4/02/08   Change default sorting so only tcclub defaults to gender, all others by date
 *        3/27/08   Add gender column and sorting options to event listing
 *       10/07/07   Highlight the signup date/time cells if current date is within the signup range.
 *        4/25/07   Congressional - pass the date for the ourse Name Labeling.
 *        3/20/07   Custom for Congressional - abstract the course name depending on the day (Course Name Labeling)
 *        3/09/05   Ver 5 - allow members to view all member events.
 *        1/24/05   Ver 5 - change club2 to club5.
 *       10/06/04   Ver 5 - add sub-menus.
 *        7/14/04   RDP Do not show column for date/time event sign-up starts if Old Oaks.
 *        6/30/04   RDP Add column for date/time event sign-up ends.
 *        1/13/04   JAG Modifications to match new color scheme.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        3/12/03   Change events2 to events2b for wait list.
 *        2/14/03   Add event sign up processing - call Member_events2
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.congressionalCustom;
import com.foretees.common.Labels;
import com.foretees.common.Utilities;

public class Member_events extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

//********************************************************************************
//
//  doGet - gets control from member_main to display a list of events
//
//********************************************************************************
//
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        //
        //  Prevent caching so sessions are not mangled
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Statement stmtm = null;
        ResultSet rs = null;

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> event_map = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

        if (session == null) {

            return;
        }

        Connection con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        String club = (String) session.getAttribute("club");   // get club name
        String caller = (String) session.getAttribute("caller");
        String user = (String) session.getAttribute("user");
        String mship = (String) session.getAttribute("mship");             // get member's mship type
        boolean new_skin = ((String) session.getAttribute("new_skin")).equals("1");
        int activity_id = (Integer) session.getAttribute("activity_id");
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        int sess_activity_id = (Integer) session.getAttribute("activity_id");

        String omit = "";
        String name = "";
        String sampm = "";
        String eampm = "";
        String su_ampm = "";
        String c_ampm = "";
        String course = "";
        String ecolor = "";
        String highlight = "";
        String categoryFilters = "";
        String sign_up_ends_field = "";
        String sign_up_starts_field = "";
        String date_time_field = "";
        String course_field = "";
        String category_style = "";

        boolean show_sign_up_starts = true;

        long date = 0;
        long edate = 0;
        long c_date = 0;
        long su_date = 0;

        int time = 0;
        int c_time = 0;
        int su_time = 0;
        int month = 0;
        int day = 0;
        int year = 0;
        int su_month = 0;
        int su_day = 0;
        int su_year = 0;
        int c_month = 0;
        int c_day = 0;
        int c_year = 0;
        int mm = 0;
        int dd = 0;
        int yy = 0;
        int shr = 0;
        int smin = 0;
        int su_hr = 0;
        int su_min = 0;
        int c_hr = 0;
        int c_min = 0;
        int ehr = 0;
        int emin = 0;
        int signUp = 0;
        int gst = 0;
        int multi = 0;
        int gender = 0;
        int season = 0;
        int event_id = 0;

        boolean popupEvent = false;
        boolean doHeader = true;
        boolean skipEvent = false;
        boolean sortBySeason = false;

        boolean showOld = (req.getParameter("showOld") != null) ? true : false;
        boolean sortDesc = (req.getParameter("desc") != null) ? true : false;

        String default_sortBy = (club.equals("tcclub") || club.equals("pattersonclub")) ? "gender" : "date";

        String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : default_sortBy; // default is to sort by date

        // for remembering the requested name and course
        String req_name = (req.getParameter("name") != null) ? req.getParameter("name") : "";
        String req_course = (req.getParameter("course") != null) ? req.getParameter("course") : "";

        String tmp_gender = "";
        String tmp_mtype = "";

        ArrayList<Integer> category_ids = new ArrayList<Integer>();
        ArrayList<Integer> selected_category_ids = new ArrayList<Integer>();
        ArrayList<String> category_names = new ArrayList<String>();


        //
        // If the user is not trying to sort by any rows, then by default lets try to sort by their gender
        //
        //if (req.getParameter("sortBy") == null) {
        if (default_sortBy.equals("gender")) {

            try {

                PreparedStatement pstmt = con.prepareStatement("SELECT m_type, gender FROM member2b WHERE username = ?");

                pstmt.clearParameters();
                pstmt.setString(1, user);
                rs = pstmt.executeQuery();

                if (rs.next()) {

                    tmp_gender = rs.getString("gender");
                    tmp_mtype = rs.getString("m_type");

                    if (tmp_gender.equalsIgnoreCase("f") || tmp_mtype.toLowerCase().endsWith("female")) {
                        sortDesc = true;
                    }
                }

                pstmt.close();

            } catch (Exception ignore) {
            }

        }


        if (sess_activity_id == 0) {

            //
            //   Get multi option for this club
            //
            try {

                stmtm = con.createStatement();        // create a statement
                rs = stmtm.executeQuery("SELECT multi FROM club5 WHERE clubName != ''");

                if (rs.next()) {
                    multi = rs.getInt(1);
                }

                stmtm.close();
            } catch (Exception exc) {

                out.println(SystemUtils.HeadTitle("Member Events Page - Error"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\">");
                out.println("<CENTER><BR>");
                out.println("<BR><BR><H3>Database Access Error</H3>");
                out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
                out.println("<BR>Error:" + exc.getMessage());
                out.println("<BR><BR>Please try again later.");
                out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
                out.println("<br><br><a href=\"Member_announce\">Return</a>");
                out.println("</CENTER></BODY></HTML>");
                out.close();
                return;
            }

        }

        if (club.equals("oldoaks")) {
            show_sign_up_starts = false;
        }

        //
        //   Get current date and time (adjusted for time zone)
        //
        date = SystemUtils.getDate(con);
        time = SystemUtils.getTime(con);

        int cal_hourDay = time / 100;                    // get adjusted hour
        int cal_min = time - (cal_hourDay * 100);        // get minute value
               
        //
        // use the date to search table
        //
        try {

            if (club.equals("blackstone") || club.equals("bellehaven") || club.equals("elmcrestcc") || club.equals("awbreyglen") || club.equals("ramseycountryclub")) {
                sortBySeason = true;
            } else {
                sortBySeason = false;
            }

            String sql = ""
                    + "SELECT name, date, year, month, day, color, act_hr, act_min, courseName, signUp, "
                    + "c_month, c_day, c_year, c_hr, c_min, c_date, c_time, "
                    + "su_month, su_day, su_year, su_hr, su_min, su_date, su_time, gender, season, "
                    + "IF(season=1, c_date, date) AS date2, event_id "
                    + "FROM events2b "
                    + "WHERE IF(season=1, c_date, date) >= ? AND gstOnly = ? AND activity_id = ? AND inactive = 0 ";

            if (sortBy.equals("date")) {               // sort by event date, time
                sql += "ORDER BY " + (sortBySeason ? "season DESC, " : "") + "date2 " + ((sortDesc) ? "DESC" : "") + ", act_hr;";
            } else if (sortBy.equals("name")) {        // sort by event name
                sql += "ORDER BY name " + ((sortDesc) ? "DESC" : "") + ";";
            } else if (sortBy.equals("gender")) {        // sort by gender

                if (sortDesc) {
                    // womens first
                    sql += "ORDER BY gender = 0, gender = 1, gender = 2, gender = 3, date2, act_hr;";
                } else {
                    // mens first
                    sql += "ORDER BY gender = 0, gender = 1, gender = 3, gender = 2, date2, act_hr;";
                }

            }

            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.clearParameters();
            stmt.setLong(1, date);
            stmt.setInt(2, gst);
            stmt.setInt(3, sess_activity_id);
            rs = stmt.executeQuery();

            //
            //   build the HTML page for the display
            //
            if (new_skin) {

                Common_skin.outputHeader(club, sess_activity_id, "Member Events", false, out, req);

                if (!req_name.equals("")) {

                    out.println("<script type=\"text/javascript\">");
                        out.println("$(document).ready(function() {");
                        out.println(" $(\"#popupEvent\").click();");
                        out.println("});");
                    out.println("</script>");

                }
                out.println("</head>");
                
                Common_skin.outputBody(club, sess_activity_id, out, req);
                Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
                Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
                Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
                Common_skin.outputPageStart(club, sess_activity_id, out, req);
                Common_skin.outputBreadCrumb(club, sess_activity_id, out, "Upcoming Special Events", req);
                Common_skin.outputLogo(club, sess_activity_id, out, req);


            } else {

                out.println(SystemUtils.HeadTitle("Member Events Page"));
                out.println("<body bgcolor=\"#ccccaa\" text=\"#000000\" style=\"font-family:Arial\">");
                SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page
                out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

                out.println("<table border=\"0\" valign=\"top\">");       // table for main page

                out.println("<tr><td align=\"center\" valign=\"top\" width=\"75%\">");
                out.println("<font size=\"2\">");

            }

            while (rs.next()) {

                if (doHeader) {

                    // Build a list of all event category_ids that have been configured for this activity
                    category_ids = Utilities.buildEventCategoryIdList(sess_activity_id, con);

                    if (new_skin) {
                        out.println("<div class=\"main_instructions\">"
                                + "<p>"
                                + "To view the event information or to sign up for an event, click on the event name."
                                + "<br>If unable to select the event, then it is not available for signup.");
                        if (category_ids.size() > 0) {
                            out.println("<br><br>To filter the event list and only display events in a certain category, "
                                + "check the checkboxes next to the desired categories and click Apply Filters."
                                + "<br>If multiple categories are selected, only events that match all selected categories will be listed.");
                        }
                        out.println("<br><br><b>NOTE:</b> To change the order of the list, click on any underlined heading (ie. <u>Gender</u>)."
                                + "</p>"
                                + "</div>");
                    } else {
                        out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" width=\"540\" cellpadding=\"5\" valign=\"top\">");
                        out.println("<tr><td align=\"center\">");
                        out.println("<font size=\"3\">");
                        out.println("<b>Upcoming Special Events</b>");
                        out.println("</font><font size=\"2\"><br><br>");
                        out.println("To view the event information or to sign up for an event, click on the event name.<br>");
                        out.println("If unable to select the event, then it is not available for signup.");
                        if (category_ids.size() > 0) {
                            out.println("<br><br>To filter the event list and only display events in a certain category, check the<br>"
                                    + "checkboxes next to the desired categories and click Apply Filters.");
                        }
                        out.println("<br><br><b>NOTE:</b> To change the order of the list, click on any underlined heading (ie. <u>Gender</u>).");
                        if (category_ids.size() > 0) {
                            out.println("<br><b>NOTE:</b> If multiple categories are selected, only events that match all selected categories will be listed.");
                        }
                        out.println("</font></td>");
                        out.println("</tr></table>");

                        out.println("</td></tr>");
                    }

                    if (category_ids.size() > 0) {      // Only proceed if at least one event category was found

                        boolean checked = false;

                        // Now build a corresponding list of all the event category names for display (ordering in the ArrayList will match the list of Ids), and a list of any currently selected ids
                        category_names = Utilities.buildEventCategoryNameList(sess_activity_id, con);

                        if (req.getParameter("clearFilters") == null) {
                            selected_category_ids = Utilities.buildEventCategoryListFromReq(req, sess_activity_id, con);   // Don't load checked categories if "Reset Filters" was clicked
                        }
                        for (int i = 0; i < selected_category_ids.size(); i++) {
                            categoryFilters += "&amp;category_id_" + selected_category_ids.get(i);
                        }

                        if (new_skin) {

                            out.println("<div class=\"sub_instructions event_category_filters\">"
                                    + "<form action=\"Member_events\" method=\"get\" name=\"frmFilters\">"
                                    + "<fieldset><legend>Filter By Category</legend>");

                        } else {

                            out.println("<tr><td align=\"center\">");
                            out.println("<form action=\"Member_events\" method=\"POST\" name=\"frmFilters\">");
                            out.println("<table border=\"0\" valign=\"top\" cellpadding=\"5\">");
                            out.println("<tr><td align=\"center\" colspan=\"5\"><b>Filter by Category</b></td></tr>");

                            out.println("<tr>");

                        }
                        for (int i = 0; i < category_ids.size(); i++) {

                            checked = false;

                            if (!new_skin) {
                                // Move to a new row every 5 categories
                                if (i % 5 == 0) {
                                    out.println("</tr><tr>");
                                }
                            }

                            for (int j = 0; j < selected_category_ids.size(); j++) {

                                if (category_ids.get(i) == selected_category_ids.get(j)) {
                                    checked = true;
                                    break;
                                }
                            }
                            if(category_names.get(i).length() > 15){
                                category_style = "class=\"double_width\" ";
                            } else {
                                category_style = "";
                            }

                            if (new_skin) {
                                out.println("<div><div><input type=\"checkbox\" name=\"category_id_" + category_ids.get(i) + "\" value=\"1\"" + (checked ? " checked" : "") + " id=\"category_id_" + category_ids.get(i) + "\"></div>"
                                        + "<label "+category_style+"for=\"category_id_" + category_ids.get(i) + "\">" + category_names.get(i) + "</label></div>");
                            } else {
                                out.println("<td align=\"left\">");
                                out.println("<input type=\"checkbox\" name=\"category_id_" + category_ids.get(i) + "\" value=\"1\"" + (checked ? " checked" : "") + " id=\"category_id_" + category_ids.get(i) + "\"><label for=\"category_id_" + category_ids.get(i) + "\"><font size=\"2\">&nbsp;&nbsp;" + category_names.get(i) + "</font></label><br>");
                                out.println("</td>");
                            }
                        }

                        if (new_skin) {
                            out.println("</fieldset>");
                        }else{
                            out.println("</tr>");
                        }
                        if (!sortBy.equals("")) {
                            out.println("<input type=\"hidden\" name=\"sortBy\" value=\"" + sortBy + "\">");
                        }
                        if (sortDesc) {
                            out.println("<input type=\"hidden\" name=\"sortDesc\" value=\"1\">");
                        }
                        if (showOld) {
                            out.println("<input type=\"hidden\" name=\"showOld\" value=\"1\">");
                        }
                        if (new_skin) {
                            out.println("<input class=\"standard_button\" type=\"submit\" name=\"applyFilters\" value=\"Apply Filters\">&nbsp;&nbsp;");
                            out.println("<input class=\"standard_button\" type=\"submit\" name=\"clearFilters\" value=\"Clear Filters\">");
                            out.println("</form></div>");
                        } else {
                            out.println("<tr><td align=\"center\" colspan=\"5\">");
                            out.println("<input type=\"submit\" name=\"applyFilters\" value=\"Apply Filters\">&nbsp;&nbsp;");
                            out.println("<input type=\"submit\" name=\"clearFilters\" value=\"Clear Filters\">");
                            out.println("</td></tr>");
                            out.println("</table>");
                            out.println("</form>");
                            out.println("</td></tr>");
                        }
                    }

                    if (new_skin) {

                        // Open table and head
                        out.println("<table class=\"standard_list_table event_list_table\">"
                                + "<thead><tr>");
                        out.println(getColumnHeader("name", "Event Name", sortBy, sortDesc, showOld, categoryFilters));
                        if (sess_activity_id == 0 && multi != 0) {
                            out.println("<th>Course</th>");
                        }
                        out.println(getColumnHeader("gender", "Gender", sortBy, sortDesc, showOld, categoryFilters));
                        out.println(getColumnHeader("date", "Date/Time", sortBy, sortDesc, showOld, categoryFilters));
                        if (show_sign_up_starts) {
                            out.println("<th>Sign Up Starts</th>");
                        }
                        out.println("<th>Sign Up Ends</th>");
                        // close head, start body
                        out.println("</tr></thead><tbody>");

                    } else {

                        out.println("<tr><td align=\"center\" valign=\"top\">");
                        out.println("<font size=\"2\">");
                        out.println("<table border=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"5\" valign=\"top\">");
                        out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
                        out.println("<tr bgcolor=\"#336633\"><td align=\"center\">");
                        out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                        out.println("<a href='?sortBy=name" + ((sortBy.equals("name") && !sortDesc) ? "&amp;desc" : "") + ((showOld) ? "&amp;showOld" : "") + categoryFilters + "' style='color:white'>Event Name</a>");
                        out.println("</b></font></td>");
                        if (sess_activity_id == 0 && multi != 0) {
                            out.println("<td align=\"center\">");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<b>Course</b>");
                            out.println("</font></td>");
                        }
                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                        out.println("<a href='?sortBy=gender" + ((sortBy.equals("gender") && !sortDesc) ? "&amp;desc" : "") + ((showOld) ? "&amp;showOld" : "") + categoryFilters + "' style='color:white'>Gender</a>");
                        out.println("</b></font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#ffffff\" size=\"2\"><b>");
                        out.println("<a href='?sortBy=date" + ((sortBy.equals("date") && !sortDesc) ? "&amp;desc" : "") + ((showOld) ? "&amp;showOld" : "") + categoryFilters + "' style='color:white'>Date/Time</a>");
                        out.println("</b></font></td>");

                        if (show_sign_up_starts) {
                            out.println("<td align=\"center\">");
                            out.println("<font color=\"#ffffff\" size=\"2\">");
                            out.println("<b>Sign Up Starts</b>");
                            out.println("</font></td>");
                        }
                        out.println("<td align=\"center\">");
                        out.println("<font color=\"#ffffff\" size=\"2\">");
                        out.println("<b>Sign Up Ends</b>");
                        out.println("</font></td></tr>");

                    }

                    doHeader = false;
                }

                name = rs.getString("name");
                edate = rs.getLong("date");
                yy = rs.getInt("year");
                mm = rs.getInt("month");
                dd = rs.getInt("day");
                ecolor = rs.getString("color");
                shr = rs.getInt("act_hr");
                smin = rs.getInt("act_min");
                course = rs.getString("courseName");
                signUp = rs.getInt("signUp");
                c_month = rs.getInt("c_month");
                c_day = rs.getInt("c_day");
                c_year = rs.getInt("c_year");
                c_hr = rs.getInt("c_hr");
                c_min = rs.getInt("c_min");
                c_date = rs.getLong("c_date");
                c_time = rs.getInt("c_time");
                su_month = rs.getInt("su_month");
                su_day = rs.getInt("su_day");
                su_year = rs.getInt("su_year");
                su_hr = rs.getInt("su_hr");
                su_min = rs.getInt("su_min");
                su_date = rs.getLong("su_date");
                su_time = rs.getInt("su_time");
                gender = rs.getInt("gender");
                season = rs.getInt("season");
                event_id = rs.getInt("event_id");

                skipEvent = false;                // init

                //
                //   If Stonebridge Ranch CC - course selection based on mship type
                //
                if (club.equals("stonebridgeranchcc") && !mship.equals("Dual")) {

                    if (mship.equals("Dye") && !course.equals("Dye")) {

                        skipEvent = true;
                    }
                    if (mship.equals("Hills") && course.equals("Dye")) {

                        skipEvent = true;
                    }
                }

                // Custom - Do not display events that do not allow online signups
                if ((club.equals("rollinghillsgc") || club.equals("mpccpb") || club.equals("aronimink") || club.equals("sagamore") || club.equals("baltusrolgc")) && signUp == 0) {
                    skipEvent = true;
                }

                // Olympic Club - Do not display events that members cannot currently sign up for
                if (club.equals("olyclub") && (signUp == 0 || date < su_date || date > c_date)) {
                    skipEvent = true;
                }

                if (skipEvent == false && selected_category_ids.size() > 0) {

                    String tempCat = Utilities.checkEventCategoryBindings(event_id, selected_category_ids, con);

                    if (!tempCat.equals("")) {
                        skipEvent = true;
                        out.println("<!-- SKIPPING " + name + " - Event did not match the following filter category (" + tempCat + ") -->");
                    }
                }

                if (skipEvent == false) {          // if ok to list this event

                    sampm = " AM";            // do start time
                    if (shr == 12) {
                        sampm = " PM";
                    }
                    if (shr > 12) {
                        sampm = " PM";
                        shr = shr - 12;    // convert to conventional time
                    }

                    c_ampm = " AM";           // do sign-up end time (cut-off time)
                    if (c_hr == 12) {
                        c_ampm = " PM";
                    }
                    if (c_hr > 12) {
                        c_ampm = " PM";
                        c_hr = c_hr - 12;    // convert to conventional time
                    }

                    su_ampm = " AM";           // do sign-up start time
                    if (su_hr == 12) {
                        su_ampm = " PM";
                    }
                    if (su_hr > 12) {
                        su_ampm = " PM";
                        su_hr = su_hr - 12;    // convert to conventional time
                    }

                    if (new_skin) {
                        highlight = "";
                    } else {
                        highlight = "#F5F5DC";         // default background color
                    }

                    if (signUp != 0 && date >= su_date && date <= c_date) {     // if within signup period, highlight the sign up info 
                        if (new_skin) {
                            highlight = "open_event";
                        } else {
                            highlight = "#86B686";        // use highlight background color
                        }
                    }

                    if (club.equals("congressional")) {
                        course_field = congressionalCustom.getFullCourseName(edate, dd, course);
                    } else {
                        course_field = course;
                    }
                    if (season == 0) {
                        date_time_field = mm + "/" + dd + "/" + yy + " at " + shr + ":" + SystemUtils.ensureDoubleDigit(smin) + sampm;
                    } else {
                        date_time_field = "Season Long";
                    }
                    if (show_sign_up_starts) {
                        if (signUp != 0) {       // if members can sign up
                            sign_up_starts_field = su_month + "/" + su_day + "/" + su_year + " at " + su_hr + ":" + SystemUtils.ensureDoubleDigit(su_min) + su_ampm;
                        } else {
                            sign_up_starts_field = "N/A";
                        }
                    }
                    if (signUp != 0) {       // if members can sign up
                        sign_up_ends_field = c_month + "/" + c_day + "/" + c_year + " at " + c_hr + ":" + SystemUtils.ensureDoubleDigit(c_min) + c_ampm;
                    } else {
                        sign_up_ends_field = "N/A";
                    }


                    //
                    //  Build the HTML for each record found
                    //

                    if (new_skin) {

                        popupEvent = (name.equals(req_name)); //  && course.equals(req_course));

                        event_map.clear();
                        event_map.put("type", "Member_events2");
                        event_map.put("name", name);
                        event_map.put("course", course);
                        out.print("<tr>");
                        out.print("<td><a " + ((popupEvent) ? "id=popupEvent " : "") + "href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(event_map)) + "\" class=\"standard_button event_button\" style=\"background-color:" + ecolor + ";\">" + name + "</a></td>");
                        if (sess_activity_id == 0 && multi != 0) {
                            out.print("<td class=\"event_list_td\">" + course_field + "</td>");
                        }
                        out.print("<td class=\"event_list_td\">" + Labels.gender_opts[gender] + "</td>");
                        out.print("<td class=\"event_list_td\">" + date_time_field + "</td>");
                        if (show_sign_up_starts) {
                            out.print("<td class=\""+highlight+" event_list_td\">" + sign_up_starts_field + "</td>");
                        }
                        out.print("<td class=\""+highlight+" event_list_td\">" + sign_up_ends_field + "</td>");
                        out.print("</tr>");

                    } else {

                        out.println("<form action=\"Member_events2\" method=\"post\" target=\"bot\">");

                        out.println("<tr><td align=\"center\">");
                        out.println("<font size=\"2\">");
                        //
                        //   if signup is yes, then allow member to select it
                        //
                        //if (signUp != 0) {       // if members can sign up

                        out.println("<p>");
                        out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                        out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                        out.println("<input type=\"submit\" value=\"" + name + "\" style=\"background:" + ecolor + "\">");
                        out.println("</p>");
                        /*
                        } else {
                        
                        out.println("<p>" + name + "</p>");
                        }*/
                        out.println("</font></td>");


                        if (sess_activity_id == 0 && multi != 0) {

                            out.println("<td align=\"center\">");
                            out.println("<font size=\"2\">" + course_field + "</font></td>");
                        }

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">");
                        out.println(Labels.gender_opts[gender]);
                        out.println("</font></td>");

                        out.println("<td align=\"center\">");
                        out.println("<font size=\"2\">" + date_time_field + "</font></td>");

                        if (show_sign_up_starts) {
                            out.println("<td bgcolor=\"" + highlight + "\" align=\"center\">");
                            out.println("<font size=\"2\">" + sign_up_starts_field + "</font></td>");
                        }

                        out.println("<td bgcolor=\"" + highlight + "\" align=\"center\">");
                        out.println("<font size=\"2\">" + sign_up_ends_field + "</font></td>");
                        out.println("</tr></form>");

                    }
                }

            }    // end of while

            if (doHeader) {                                   // no records found

                if (new_skin) {
                    out.println("<div class=\"sub_instructions\"><h3>There are no events scheduled at this time.</h3></div>");
                } else {
                    out.println("<br><br><br><br>");
                    out.println("<p align=\"center\">There are no events scheduled at this time.</p>");
                }
                
            } else {
                
                if (new_skin) {
                    out.println("</tbody></table>");
                }
                
            }

            stmt.close();

            if (new_skin) {

                Common_skin.outputPageEnd(club, sess_activity_id, out, req);

            } else {

                out.println("</font></td></tr>");
                out.println("</table>");                   // end of main page table

                out.println("<br><font size=\"2\">");
                out.println("<form method=\"get\" action=\"Member_announce\">");
                out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;\">");
                out.println("</form></font>");

                //
                //  End of HTML page
                //
                out.println("</center></font></body></html>");


            }

            out.close();

        } catch (Exception exc) {

            out.println(SystemUtils.HeadTitle("Database Error"));
            out.println("<BODY bgcolor=\"#ccccaa\">");
            out.println("<CENTER>");
            out.println("<BR><BR><H3>Database Access Error</H3>");
            out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
            out.println("<BR>Error:" + exc.getMessage());
            out.println("<BR><BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your golf shop (provide this information).");
            out.println("<BR><BR><a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
        }

    }   // end of doGet

    //
    //******************************************************************************
    //
    //  doPost processing - is done by Member_events2 (so Member_jump will work)
    //
    //******************************************************************************
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        doGet(req, resp);          // call doGet processing

    }   // end of doPost

    private String getColumnHeader(String name, String proper_name, String sortBy, boolean sortDesc, boolean showOld, String categoryFilters) {

        String css_class = "sortable_column";
        if (sortBy.equals(name) && !sortDesc) {
            css_class += " sort_asc";
        } else if (sortBy.equals(name)) {
            css_class += " sort_desc";
        }
        return "<th><a href='?sortBy=" + name + ((sortBy.equals(name) && !sortDesc) ? "&amp;desc" : "") + ((showOld) ? "&amp;showOld" : "") + categoryFilters + "' class=\"" + css_class + "\">" + proper_name + "</a></th>";

    }
}
