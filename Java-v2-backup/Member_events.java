
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
import com.foretees.common.Common_skin;
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
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.htmlTags;
import com.foretees.common.Connect;
import com.foretees.common.verifyCustom;

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

        Connection con = Connect.getCon(req);            // get DB connection

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
        String msubtype = (String)session.getAttribute("msubtype");        // get member's sub_type
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
        boolean isOlyClubWGN = false;
        
        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        
        String space = null;
        String preAmpm = null;
        String postAmpm = null;
        if(rwd){
            space = " ";
            preAmpm = "<span class=\"ampm\">";
            postAmpm = "</span>";
        } else {
            space = " ";
            preAmpm = " ";
            postAmpm = "";
        }

        if (club.equals("olyclub")) {
            isOlyClubWGN = verifyCustom.isOlyClubWGN(user, req);
        }
        
        String default_sortBy = (club.equals("tcclub") || club.equals("pattersonclub")) ? "gender" : "date";

        String sortBy = (req.getParameter("sortBy") != null) ? req.getParameter("sortBy") : default_sortBy; // default is to sort by date

        // for remembering the requested name and course
        String req_name = (req.getParameter("name") != null) ? req.getParameter("name") : "";
        String req_course = (req.getParameter("course") != null) ? req.getParameter("course") : "";

        int req_event_id = 0;
        String event = req.getParameter("event_id");
        try {
            req_event_id = Integer.parseInt(event);
        } catch (NumberFormatException ignore) {}
    
        String tmp_gender = "";
        String tmp_mtype = "";

        ArrayList<Integer> category_ids = new ArrayList<Integer>();
        ArrayList<Integer> selected_category_ids = new ArrayList<Integer>();
        ArrayList<String> category_names = new ArrayList<String>();
        
        htmlTags tags = new htmlTags(rwd);

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
                    + "WHERE IF(season=1, c_date, date) >= ? AND gstOnly = ? AND signup_on_teesheet = 0 AND activity_id = ? AND inactive = 0 ";

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
            Common_skin.outputHeader(club, sess_activity_id, "Member Events", false, out, req);

            if (!req_name.equals("") || req_event_id > 0) {

                out.println("<script type=\"text/javascript\">");
                    out.println("$(document).ready(function() {");
                    out.println(" $(\"#popupEvent\").click();");
                    out.println("});");
                out.println("</script>");

            }
            out.println("</head>");
            
            String breadcrumb_text = "Upcoming Special Events";
            
            if (club.equals("mediterra") && sess_activity_id == 3) {
                breadcrumb_text = "Upcoming Fitness Classes";
            }

            Common_skin.outputBody(club, sess_activity_id, out, req);
            Common_skin.outputTopNav(req, club, sess_activity_id, out, con);
            Common_skin.outputBanner(club, sess_activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
            Common_skin.outputSubNav(club, sess_activity_id, out, con, req);
            Common_skin.outputPageStart(club, sess_activity_id, out, req);
            Common_skin.outputBreadCrumb(club, sess_activity_id, out, breadcrumb_text, req);
            Common_skin.outputLogo(club, sess_activity_id, out, req);


            while (rs.next()) {

                if (doHeader) {

                    // Build a list of all event category_ids that have been configured for this activity
                    if (!club.equals("olyclub")) {    // Don't display anything regarding event categories for Olympic Club. If they want to legitimately use them later, we must adjust this custom
                        category_ids = Utilities.buildEventCategoryIdList(sess_activity_id, con);
                    }

                    if (club.equals("mediterra") && sess_activity_id == 3) {
                        out.println("<div class=\"main_instructions pageHelp\" data-ftHelpTitle=\"Instructions\">"
                                + "<p>"
                                + "To view the class information or to sign up for a class, click on the class name."
                                + "<br>If unable to select the class, then it is not available for signup.");
                        if (category_ids.size() > 0) {
                            out.println("<br><br>To filter the event list and only display classes in a certain category, "
                                + "check the checkboxes next to the desired categories and click Apply Filters."
                                + "<br>If multiple categories are selected, only classes that match all selected categories will be listed.");
                        }
                    } else {
                        out.println("<div class=\"main_instructions pageHelp\" data-ftHelpTitle=\"Instructions\">"
                                + "<p>"
                                + "To view the event information or to sign up for an event, click on the event name."
                                + "<br>If unable to select the event, then it is not available for signup.");
                        if (category_ids.size() > 0) {
                            out.println("<br><br>To filter the event list and only display events in a certain category, "
                                + "check the checkboxes next to the desired categories and click Apply Filters."
                                + "<br>If multiple categories are selected, only events that match all selected categories will be listed.");
                        }
                    }
                    out.println("<br><br><b>NOTE:</b> To change the order of the list, click on any underlined heading (ie. <u>Gender</u>)."
                            + "</p>"
                            + "</div>");
                    
                    if (club.equals("sawgrass") && sess_activity_id == 15) {
                        if (!rwd) { // If Responsive, we'll do this a bit differently below
                            out.println("<div class=\"main_instructions date_course\">");
                            out.println(" <span class=\"serverClock\"><span>The Server Time is: <b class=\"jquery_server_clock\" data-ftclub=\"" + club + "\"></b></span>");
                            out.println("<a class=\"tip_text helpButton\" href=\"javascript:void(0);\" onclick=\"showInfo(); return false;\" title=\"How is this clock used?\"><span>How is this clock used?</span></a></span>");
                            out.println("</div>");
                        }
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

                        out.println("<div class=\"sub_instructions event_category_filters pageHelp"+(selected_category_ids.size()>0?" activeOnLoad":"")+"\" data-ftHelpTitle=\"Filter By Category\">"
                                + "<form action=\"Member_events\" method=\"get\" name=\"frmFilters\">"
                                + "<fieldset><legend>Filter By Category</legend>");

                        for (int i = 0; i < category_ids.size(); i++) {

                            checked = false;
                            category_style = "";
                            
                            if (club.equals("desertmountain") && category_ids.get(i) == 11) {    // Desert Mountain wants to hide this category from members
                                continue;
                            }
                            if (club.equals("oceanreef") && category_ids.get(i) == 1) {    // Oceanreef wants to hide this category from members
                                continue;
                            }
                            if (selected_category_ids.contains(category_ids.get(i))) {
                                checked = true;
                            }
                            
                            if(category_names.get(i).length() > 15){
                                category_style = "class=\"double_width\" ";
                            }

                            out.println("<div><div><input type=\"checkbox\" name=\"category_id_" + category_ids.get(i) + "\" value=\"1\"" + (checked ? " checked" : "") + " id=\"category_id_" + category_ids.get(i) + "\"></div>"
                                    + "<label "+category_style+"for=\"category_id_" + category_ids.get(i) + "\">" + category_names.get(i) + "</label></div>");
                        }

                        out.println("</fieldset>");
                        
                        if (!sortBy.equals("")) {
                            out.println("<input type=\"hidden\" name=\"sortBy\" value=\"" + sortBy + "\">");
                        }
                        
                        if (sortDesc) {
                            out.println("<input type=\"hidden\" name=\"sortDesc\" value=\"1\">");
                        }
                        
                        if (showOld) {
                            out.println("<input type=\"hidden\" name=\"showOld\" value=\"1\">");
                        }
                        
                        out.println("<input class=\"standard_button\" type=\"submit\" name=\"applyFilters\" value=\"Apply Filters\">&nbsp;&nbsp;");
                        out.println("<input class=\"standard_button\" type=\"submit\" name=\"clearFilters\" value=\"Clear Filters\">");
                        out.println("</form></div>");
                    }

                    if(rwd){
                        // Create sort by select list for use when in small/medium screen mode
                        Map<String, String> sortColumns = new LinkedHashMap<String, String>();
                        sortColumns.put("Event Name","name");
                        sortColumns.put("Gender","gender");
                        sortColumns.put("Date/Time","date");
                        Map<String, String> sortOrder = new LinkedHashMap<String, String>();
                        sortOrder.put("Ascending","asc");
                        sortOrder.put("Descending","desc");

                        out.print("<div class=\"main_instructions rwdMediumScreenBlock columnSort\">");
                        out.print("<label><span>Sort By</span>");
                        out.print(Common_skin.getSelectListFromMap(sortColumns, sortBy, "sortColumn"));
                        out.print("</label>");
                        out.print("<label><span>Sort Order</span>");
                        out.print(Common_skin.getSelectListFromMap(sortOrder, (sortDesc?"desc":""), "sortOrder"));
                        out.print("</label>");
                        out.print("</div>");
                    }

                    // Open table and head
                    out.print(tags.openTable("standard_list_table event_list_table rwdCompactible rwdWideData"));
                    out.print(tags.openThead()+tags.openTr());
                    out.println(getColumnHeader("name", "Event Name", sortBy, sortDesc, showOld, categoryFilters, tags.th));
                    if (sess_activity_id == 0 && multi != 0) {
                        out.println(tags.getTh("Course","sName"));
                    }
                    out.println(getColumnHeader("gender", "Gender", sortBy, sortDesc, showOld, categoryFilters, tags.th));
                    out.println(getColumnHeader("date", "Date/Time", sortBy, sortDesc, showOld, categoryFilters, tags.th));
                    if (show_sign_up_starts) {
                        out.println(tags.getTh("Sign Up Starts","sDate"));
                    }
                    out.println(tags.getTh("Sign Up Ends","sDate"));
                    // close head, start body
                    out.print(tags.closeTr()+tags.closeThead());
                    out.print(tags.openTbody());

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
                if ((club.equals("rollinghillsgc") || club.equals("mpccpb") || club.equals("aronimink") || club.equals("sagamore") || club.equals("baltusrolgc") 
                        || club.equals("thewoodlands") || club.equals("valleyclub") || club.equals("castlepines") || club.equals("marincountryclub") 
                        || club.equalsIgnoreCase("Sonnenalp") || club.equalsIgnoreCase("woodsideplantation") || club.equalsIgnoreCase("lakesidecc") 
                        || club.equalsIgnoreCase("glenoaks") || club.equals("wingedfoot") || club.equals("stanwichclub") || club.equals("snoqualmieridge")) && signUp == 0) {
                    skipEvent = true;
                }

                // Olympic Club - Do not display events that members cannot currently sign up for
                if (club.equals("olyclub") && (signUp == 0 || date < su_date || date > c_date)) {
                    skipEvent = true;
                }
                
                if (club.equals("desertmountain") && !msubtype.equalsIgnoreCase("League")) {
                    
                    ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                    temp_category_ids.add(11);
                    
                    if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                        skipEvent = true;
                    }
                } else if (club.equals("olyclub") && !isOlyClubWGN) {
                                 
                    ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                    temp_category_ids.add(1);
                    
                    if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                        skipEvent = true;
                    }
                } else if (club.equals("oceanreef") && !msubtype.equalsIgnoreCase("Friday Boys")) {
                    
                    ArrayList<Integer> temp_category_ids = new ArrayList<Integer>();
                    temp_category_ids.add(1);
                    
                    if (Utilities.checkEventCategoryBindings(event_id, temp_category_ids, con).equals("")) {
                        skipEvent = true;
                    }
                }

                if (skipEvent == false && selected_category_ids.size() > 0) {

                    String tempCat = Utilities.checkEventCategoryBindings(event_id, selected_category_ids, con);

                    if (!tempCat.equals("")) {
                        skipEvent = true;
                        //out.println("<!-- SKIPPING " + name + " - Event did not match the following filter category (" + tempCat + ") -->");
                    }
                }

                if (skipEvent == false) {          // if ok to list this event

                    sampm = "AM";            // do start time
                    if (shr == 12) {
                        sampm = "PM";
                    }
                    if (shr > 12) {
                        sampm = "PM";
                        shr = shr - 12;    // convert to conventional time
                    }

                    c_ampm = "AM";           // do sign-up end time (cut-off time)
                    if (c_hr == 12) {
                        c_ampm = "PM";
                    }
                    if (c_hr > 12) {
                        c_ampm = "PM";
                        c_hr = c_hr - 12;    // convert to conventional time
                    }

                    su_ampm = "AM";           // do sign-up start time
                    if (su_hr == 12) {
                        su_ampm = "PM";
                    }
                    if (su_hr > 12) {
                        su_ampm = "PM";
                        su_hr = su_hr - 12;    // convert to conventional time
                    }

                    highlight = "";


                    if (signUp != 0 && date >= su_date && date <= c_date) {     // if within signup period, highlight the sign up info 
                        highlight = "open_event";
                    }

                    if (club.equals("congressional")) {
                        course_field = congressionalCustom.getFullCourseName(edate, dd, course);
                    } else {
                        course_field = course;
                    }
                    if (season == 0) {
                        date_time_field = "<span class=\"date\">"+ mm + "/" + dd + "/" + yy + "</span>"+space+"<span class=\"sep\">at</span>"+space+"<span class=\"time\">" + shr + ":" + SystemUtils.ensureDoubleDigit(smin) + "</span>" + preAmpm + sampm + postAmpm;
                    } else {
                        date_time_field = "<span class=\"season\">Season Long</span>";
                    }
                    if (show_sign_up_starts) {
                        if (signUp != 0) {       // if members can sign up
                            sign_up_starts_field = "<span class=\"date\">"+ su_month + "/" + su_day + "/" + su_year + "</span>" +space + "<span class=\"sep\">at</span>"+space+"<span class=\"time\">" + su_hr + ":" + SystemUtils.ensureDoubleDigit(su_min) + "</span>" + preAmpm + su_ampm + postAmpm;
                        } else {
                            sign_up_starts_field = "<span class=\"na\">N/A</span>";
                        }
                    }
                    if (signUp != 0) {       // if members can sign up
                        sign_up_ends_field = "<span class=\"date\">"+ c_month + "/" + c_day + "/" + c_year + "</span>" +space + "<span class=\"sep\">at</span>"+space+"<span class=\"time\">" + c_hr + ":" + SystemUtils.ensureDoubleDigit(c_min) + "</span>" + preAmpm + c_ampm + postAmpm;
                    } else {
                        sign_up_ends_field = "<span class=\"na\">N/A</span>";
                    }


                    //
                    //  Build the HTML for each record found
                    //
                    popupEvent = (name.equalsIgnoreCase(req_name)); //  && course.equals(req_course));
                    if (!popupEvent) popupEvent = (req_event_id == event_id);

                    event_map.clear();
                    event_map.put("type", "event");
                    event_map.put("id", event_id);
                    //event_map.put("course", course);
                    out.print(tags.openTr());
                    out.print(tags.getTd("<a " + ((popupEvent) ? "id=popupEvent " : "") + "href=\"#\" data-ftjson=\"" + StringEscapeUtils.escapeHtml(gson_obj.toJson(event_map)) + "\" class=\"standard_button event_button\" style=\"background-color:" + ecolor + ";\">" + name + "</a>","sT"));
                    if (sess_activity_id == 0 && multi != 0) {
                        out.print(tags.getTd("<span>" + course_field + "</span>","sN"));
                    }
                    out.print(tags.getTd(Labels.gender_opts[gender],"event_list_td sG"));
                    out.print(tags.getTd(date_time_field,"event_list_td sD evDate"));
                    if (show_sign_up_starts) {
                        out.print(tags.getTd("<span>" + sign_up_starts_field + "</span>",highlight+" event_list_td sD startDate"));
                    }
                    out.print(tags.getTd("<span>" + sign_up_ends_field + "</span>",highlight+" event_list_td sD endDate"));
                    out.print(tags.closeTr());
                }
            }    // end of while

            if (doHeader) {                                   // no records found

                out.println("<div class=\"sub_instructions\"><h3>There are no events scheduled at this time.</h3></div>");
                
            } else {

                out.print(tags.closeTbody());
                out.print(tags.closeTable());
            }

            stmt.close();

            Common_skin.outputPageEnd(club, sess_activity_id, out, req);

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

    private String getColumnHeader(String name, String proper_name, String sortBy, boolean sortDesc, boolean showOld, String categoryFilters, String tag) {

        String css_class = "sortable_column";
        if (sortBy.equals(name) && !sortDesc) {
            css_class += " sort_asc";
        } else if (sortBy.equals(name)) {
            css_class += " sort_desc";
        }
        return "<"+tag+" class=\"rwdTh sSortable\"><a href='?sortBy=" + name + ((sortBy.equals(name) && !sortDesc) ? "&amp;desc" : "") + ((showOld) ? "&amp;showOld" : "") + categoryFilters + "' class=\"" + css_class + "\">" + proper_name + "</a></"+tag+">";

    }
}
