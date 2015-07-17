/*******************************************************************************
 *
 *   Proshop_gensheets:  This servlet will display the time sheets for an activity.
 *
 *
 *   called by:  various servlets and menus
 *
 *
 *   created: 1/21/2009   Paul
 *
 *
 *   last updated:
 *
 *         2/28/14  Winged Foot GC (wingedfoot) - Updated weekend consecutive option to add a 30 minute option.
 *         2/04/14  Winged Foot GC (wingedfoot) - Updated detailed view to make the time selection drop-downs have different options based on the day of the week (case 2363).
 *         8/06/13  Added additional processing to showRest for FlxRez which takes locations into account to properly display legend items when sub-activities are used.
 *         7/09/13  gotoTimeSlot will now remove the onclick event from the triggering element to prevent issues with users double-clicking the buttons and locking themselves out.
 *         3/21/13  Revamped how the restriction legend is built so that it functions like the member side. The previous method resulted in certain restrictions not appearing in the legend.
 *         1/17/13  Add refresh sheet link to the control panel
 *        10/31/12  If no children were found for this activity, display a graceful message instead of attempting to load time sheets that don't exist.
 *        10/18/12  Time sheets will now display a message indicating that time sheets were not found for this activity instead of an error if a root activity has no aub-activities configured.
 *         9/12/12  Added hooks for Limited Access Proshop users for TS_VIEW and TS_PAST_VIEW for viewing current and past time sheets, respectively.
 *         6/07/12  New summary view now has fixed width columns. Header column will span vertically to display full court (activity) name.
 *         5/08/12  Updated summary view to group by activity_id instead of activity_name when gathering activity times to format the time sheet display. 
 *                  This will allow for courts with the same name to be displayed properly instead of grouped into a single court.
 *         4/12/12  Lessons and Clinics will now be color-coded on the new summary view, and when hovered over, the pro and lesson/clinic name will be displayed.
 *        12/06/11  Ballantyne CC (ballantyne) - Highlight 3 days in advance in green on the calendar to display the days the members have access to (case 2086).
 *        12/04/11  Updated the summary view to display a second table of activity_ids if they have over a certain number of activity sheets (cols_per_row in activities table).
 *        10/25/11  Fixed bug with displaying tflags when player was not a member, and updated showing of tflags to be a custom specific to Charlotte CC.
 *        10/20/11  Updated Tabbed and Detailed view to pull and display the tflag field from member2b records, if populated.
 *         9/28/11  Updated getRestData() method to be static so it can be referenced from the print options page.
 *         9/26/11  Updated getLessonInfo method calls to call it at its new location: verifyLesson.getLessonInfo
 *         9/21/11  Updated summary view to coincide with changes to how the amount of time to book selected on this view.
 *         5/10/11  Changed how sub-activities are selected and navigated between to be less cumbersome. No visible change for clubs with no sub-activities.
 *         4/29/11  Tennis Shop Notes box will now use the name of the sess_activity_id instead of Tennis.
 *         3/22/11  Fixed issues with restriction colorings not showing correctly, rest names not being displayed if rest color is Default, and the option of whether or not
 *                  a restriction is to be displayed on the time sheets not being enforced.
 *         3/17/11  Using the "Suspend for Today" option on a restriction will no longer attempt to set the rest_id for those time slots to 0, which could result in
 *                  issues if more than one restriction applies to a given time.  The suspension checking process will handle the on-the-fly suppressing of the restriction.
 *         3/17/11  Applied numerous changes to how restriction suspensions were being gathered and displayed so they now function properly.
 *         3/15/11  Add Activity Sheet Notes
 *         3/09/11  Pull allowable view modes from pro_allowable_views field in activities table instead of the allowable_views field.
 *         2/25/11  Fixed an issue with the Tabbed View still displaying the consec selection drop-down and time slot selection button if a time slot is occupied by an event or lesson.
 *         2/25/11  Fixed a bug with a blank cell not being printed for the consec column when an unselectable time would not normally have consec times associated with it.
 *         2/23/11  Layout modes 2 and 4 (old summary view and summary view) will now use the first number of the consec_pro_csv string for the consec value, if populated.
 *         2/17/11  Converted all references to disallow_joins over to the new force_singles field.
 *         1/19/11  Clubs with view 4 turned on will no longer see the Old Summary View in the control panel.
 *         1/19/11  Altered new summary view to always display the last name of the first member in the slot.
 *         1/17/11  Lakewood CC (lakewoodcc) - Highlight times booked within 24 hours in yellow in the time sheets (case 1901).
 *        10/16/10  Added ability for users to quick suspend a member type restriction for the current day being viewed.
 *         8/23/10  Allow proshop users to click on the event and restriction buttons above the time sheets to view details and edit them
 *         8/17/10  Pro name and member name will now be included along with the lesson type when a lesson occupies a time slot
 *         8/13/10  Peninsula - default summary view to 4 consec times
 *         8/05/10  Added row highlighting and member names (or Guest or X) to new summary view - also removed code hiding court 7 for quecheeclubtennis
 *         7/16/10  Fixes for problem with loading consecutive options for clubs with consec_pro_csv blank
 *         7/12/10  Changed how consecutive options are read from database and displayed.  Now defined by a custom csv of ints instead of a max consec value.
 *         6/28/10  Added a new summary view - may replace existing summary view (ONLY ON DEV SERVER FOR NOW)
 *         4/06/10  Added sort_by field to the order by clause of all database queries pulling activity names
 *         3/27/10  Numberous fixes regarding displaying restrictions, events, lessons in each view 
 *         1/14/10  Added support for booking consecutive times
 *        12/21/09  Implemented new activity selection process - no longer using parent_id
 *        12/08/09  When looking for events only check those that are active.
 *        11/02/09  Do not display restrictions that are configured to not show on sheets.
 *        11/01/09  Display restrictions on the time sheets
 *        10/28/09  Started to add support for old time sheets
 *        10/22/09  Added row highlighting to the time sheet
 * 
 * 
 * 
 ******************************************************************************/



import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.verifyCustom;
import com.foretees.common.parmRest;
import com.foretees.common.getRests;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyLesson;

import com.foretees.common.Connect;

public class Proshop_gensheets extends HttpServlet {
    
 String rev = SystemUtils.REVLEVEL;
    
 final String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
 final String [] day_table = { "inv", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

 
 /*
  *
  *     To see all the top level activity groups
  *     > SELECT * FROM activities WHERE parent_id = 0
  *
  *     To see all the final activities (these will have sheets associated with them)
  *     > SELECT * FROM activities WHERE activity_id NOT IN (SELECT parent_id FROM activities)
  *
  *     insert into activity_sheets VALUES (NULL, 17, "2009-01-31 10:50:00", 0, 0, 0, 0, "", "0000-00-00 00:00:00", "", 0,"", "0000-00-00 00:00:00", 0, 0, 0, 0)
  *
  *     to manually adjust the sheets for testing
  *     update activity_sheets set date_time = DATE_ADD(date_time, INTERVAL 2 MONTH)
  *
  *     to free up hung activity times
  *     UPDATE activity_sheets
  *     SET in_use_by = '', in_use_at = '0000-00-00 00:00:00'
  *     WHERE DATE_ADD(in_use_at, INTERVAL 6 MINUTE) < now();
  *
  */
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;
   
    String club = (String)session.getAttribute("club");               // get club name
    String user = (String)session.getAttribute("user");               // get user name
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    Connection con = Connect.getCon(req);

    int group_id = 0;
    int activity_id = 0;
    int last_tab = 0; // passed back form slot
    int last_tab_index = 0;
    int layout_mode = 0;
    int month = 0;
    int day = 0;
    int year = 0;
    int date = 0;

    // used for old sheets
    int oldest_year = 0;
    int oldest_month = 0;
    int oldest_day = 0;
    int minDate = 0;

    //
    //  First, check for Event or Restriction calls - user clicked on an event or restriction in the Legend
    //
    if (req.getParameter("event") != null) {
        String eventName = req.getParameter("event");

        displayEvent(eventName, out, con);             // display the information
        return;
    }

    String sid = req.getParameter("group_id");
    try {
        group_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("activity_id");
    try {
        activity_id = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("last_tab");
    try {
        last_tab = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("last_tab_index");
    try {
        last_tab_index = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}

    sid = req.getParameter("layout_mode");
    try {
        layout_mode = Integer.parseInt(sid);
    } catch (NumberFormatException ignore) {}
    
    if (activity_id == 0) activity_id = sess_activity_id;

    // this array will hold all the rest_id's we encounter while building the time sheets
    ArrayList<Integer> restArray = new ArrayList<Integer>();

    // debug
    out.println("<!-- last_tab=" + last_tab + ", group_id=" + group_id + ", activity_id=" + activity_id + ", sess_activity_id=" + sess_activity_id + ", layout_mode=" + layout_mode + " -->");
    
    String calDate = (req.getParameter("calDate") != null) ? req.getParameter("calDate") : "";
    String dayShort_name = "";
    String day_name = "";
    String num = "";
    String allowable_views = "";

    boolean allow_tabbed = false;
    boolean allow_summary = false;
    boolean allow_newsummary = false;
    boolean allow_detail = false;
    boolean today = false;          // set to true if viewing todays time sheet
    boolean checkinAccess = SystemUtils.verifyProAccess(req, "TS_CHECKIN", con, out);
    boolean tsNotesView = SystemUtils.verifyProAccess(req, "TS_NOTES_VIEW", con, out);
    boolean oldSheets = (req.getParameter("old") != null) ? true : false;   // flag for determining if we are viwing old time sheets
    
    if (oldSheets) {
        // Check Feature Access Rights for current proshop user
        if (!SystemUtils.verifyProAccess(req, "TS_PAST_VIEW", con, out)) {
            SystemUtils.restrictProshop("TS_PAST_VIEW", out);
        }
    } else {
        // Check Feature Access Rights for current proshop user
        if (!SystemUtils.verifyProAccess(req, "TS_VIEW", con, out)) {
            SystemUtils.restrictProshop("TS_VIEW", out);
        }
    }

    //
    //  parm block to hold the member restrictions for this date and member
    //
    parmRest parmr = new parmRest();          // allocate a parm block
    
    //
    // Determin the date we are here to display
    //
    if (!calDate.equals("") && calDate.indexOf("/") > 0) {

        //
        //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
        //
        try {
            StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

            num = tok.nextToken();                    // get the mm value
            month = Integer.parseInt(num);
            num = tok.nextToken();                    // get the dd value
            day = Integer.parseInt(num);
            num = tok.nextToken();                    // get the yyyy value
            year = Integer.parseInt(num);
        } catch (Exception exc) { out.println("ERROR: " + exc.toString() + " calDate="+calDate); }

        //
        //  Get today's date and then set the requested date to get the day name, etc.
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        cal.set(Calendar.YEAR, year);                 // change to requested date
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        dayShort_name = dayShort_table[cal.get(Calendar.DAY_OF_WEEK)];      // get short name for day
        day_name = day_table[cal.get(Calendar.DAY_OF_WEEK)];                // get full name for day
        date = year * 10000 + month * 100 + day;

    } else {

        calDate = (req.getParameter("date") != null) ? req.getParameter("date") : "";

        if ( calDate.equals("") ) {
            
            date = (int)Utilities.getDate(con, (oldSheets) ? -1 : 0);
            
        } else {
            
            try {
                date = Integer.parseInt(calDate);
            } catch (NumberFormatException ignore) {}

            // if the date is past then force oldSheets to true
            // this is for when returning to gensheets from slot when the 'old' flag is not returned
            if (date < (int)Utilities.getDate(con)) oldSheets = true;

        }

        year = date / 10000;
        int temp = year * 10000;
        month = date - temp;
        temp = month / 100;
        temp = temp * 100;
        day = month - temp;
        month = month / 100;

        Calendar cal = new GregorianCalendar();       // get todays date

        cal.set(Calendar.YEAR, year);                 // change to requested date
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        dayShort_name = dayShort_table[cal.get(Calendar.DAY_OF_WEEK)];      // get short name for day
        day_name = day_table[cal.get(Calendar.DAY_OF_WEEK)];                // get full name for day
        day_name = day_table[cal.get(Calendar.DAY_OF_WEEK)];                  // get full name for day

    }

    if (date == Utilities.getDate(con)) today = true;

    if (req.getParameter("rest") != null) {

        int restID = Integer.parseInt(req.getParameter("rest"));

        displayRest(restID, (req.getParameter("suspend") != null), date, out, con);             // display the information
        return;
    }

    //
    //  Get all restrictions for this day and user (for use when checking each tee time below)
    //
    parmr.user = user;
    parmr.mship = "";
    parmr.mtype = "";
    parmr.date = date;
    parmr.day = day_name;
    parmr.course = "";
    parmr.activity_id = sess_activity_id;     // use Root id for now

    try {

       getRests.getAll(con, parmr);              // get the restrictions

    } catch (Exception exc) {

        Utilities.logError("Proshop_gensheets: getRests failed. user=" + user + ", date=" + date + ", day_name=" + day_name + ", activity_id=" + sess_activity_id + ", err=" + exc.toString());

    }


    //debug
    out.println("<!-- date=" + date + " today=" + today + " -->");

    out.println(SystemUtils.HeadTitle2("Proshop Select Date Page"));

    // include files for dynamic calendars
    if (club.equals("ballantyne") && sess_activity_id == 1) {  // Different style sheets needed for color-coding days in adv for Ballantyne
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\">");
        out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");
    } else {
        out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
        out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
    }

    // include files for tabber
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/tabber.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/tabber.js\"></script>");

    out.println("<style>");
    out.println("body { text-align: center; }");
    out.println("</style>");

    out.println("<script type=\"text/javascript\">");
    out.println("function gotoTimeSlot(slot_id, group_id, date, layout, consec) {");
    out.println("  top.document.location.href=\"Proshop_activity_slot?slot_id=\"+slot_id+\"&group_id=\"+group_id+\"&date=\"+date+\"&layout_mode=\"+layout+\"&consec=\"+consec;");
    out.println("  document.getElementById(\"btnSlot_\"+slot_id).onclick=\"\";");
    out.println("}");
    out.println("</script>");
    
    out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page


    //
    // START THE FORM
    //

    // this is the form that gets submitted when the user selects a day from the calendar
    out.println("<form action=\"Proshop_gensheets\" method=\"get\" name=\"frmLoadDay\">");

    out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

    out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");

    out.println("<input type=\"hidden\" name=\"last_tab\" value=\"" + last_tab + "\">");
    out.println("<input type=\"hidden\" name=\"last_tab_index\" value=\"" + last_tab_index + "\">");

    out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + layout_mode + "\">");

    if (oldSheets) out.println("<input type=\"hidden\" name=\"old\" value=\"\"><input type=\"hidden\" name=\"chkallin\" value=\"0\">");


/*
    int child_count = 0;
    int has_parent = 0;

    try {

        pstmt = con.prepareStatement("" +
                "SELECT parent_id " +
                "FROM activities " +
                "WHERE activity_id = ? AND parent_id <> 0");

        pstmt.clearParameters();
        pstmt.setInt(1, activity_id);

        rs = pstmt.executeQuery();

        if ( rs.next() ) has_parent = 1;

    } catch (Exception exc) {

            out.println("<p>Error getting parent_count:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    child_count = getActivity.getSubActivityCount(activity_id, con);

    if (child_count > 0 || has_parent > 0) {

        String result = "";

        ArrayList<Integer> array = new ArrayList<Integer>();

        try { array = getActivity.getAllParentsForActivity(activity_id, con); }
        catch (Exception exc) { Utilities.logError("Error in Member_gensheets building path for activity id#" + activity_id + " ERR=" + exc.toString()); }

        for (int i = 0; i < array.size(); i++) {

            result = "<a href=\"javascript:void(" + array.get(i) + ")\" onclick=\"document.forms['frmLoadDay'].activity_id.value='" + array.get(i) + "';document.forms['frmLoadDay'].last_tab_index.value='';document.forms['frmLoadDay'].last_tab.value='';document.forms['frmLoadDay'].submit()\">" + getActivity.getActivityName(array.get(i), con) + "</a> > " + result;

        }

        if (result.endsWith(" > ") && activity_id != sess_activity_id) result = result.substring(0, result.length() - 3);

        out.println("<br>");
        out.println(result);

    }


    // If there are no 'sub activites' defined for this parent activity then set group_id = activity_id
    if ( child_count == 0 ) {

        group_id = activity_id;

        out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

        out.println("<!-- NO CHILD ACTIVITY FOUND FOR "  + getActivity.getActivityName(activity_id, con) + " -->");

        //out.println("<br><b><font size=6 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b>");

    } else {

        //
        // DISPLAY CHILD ACTIVITES
        //
        out.println("<select name=activity_id onchange=\"document.forms['frmLoadDay'].last_tab_index.value='';document.forms['frmLoadDay'].last_tab.value='';document.forms['frmLoadDay'].submit()\">");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                                    "SELECT activity_id, activity_name " +
                                    "FROM activities " +
                                    "WHERE parent_id = '" + activity_id + "' AND activity_id IN (SELECT parent_id FROM activities)");

            out.println("<option value=\"0\">CHOOSE...</option>");

            while (rs.next()) {

                Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), -1, out);

            }

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        out.println("</select>");



        //
        // NOW LET'S CHECK TO SEE IF THIS ACTIVITY HAS ANY BARREN CHILDERN ACTIVITES ASSOCIATED WITH IT
        //
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                    "SELECT activity_id, activity_name " +
                    "FROM activities " +
                    "WHERE parent_id = '" + activity_id + "' AND activity_id NOT IN (SELECT parent_id FROM activities)");

            if (rs.next()) {

                group_id = activity_id;

                out.println("<input type=\"hidden\" name=\"activity_id\" value=\"" + activity_id + "\">");

                out.println("<!-- FOUND AT LEAST ONE BARREN ACTIVITY FOUND FOR "  + getActivity.getActivityName(activity_id, con) + " -->");

                //out.println("<br><b><font size=6 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b>");

            }

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }


    } // end if no sub activites
*/

    /*
     * DEFINITIONS
     *
     *  Activity        - Very generic - could refer to anything from a root level activity down to a 'court' level activity.
     *  Root Activity   - The top most level for an activity - this value is stored in the session (sess_activity_id) and indicates which type of sport they are viewing and how foretees will function. (Logins, permissions, events, lesson books, etc are all based off of the root id.
     *  Sub Activity    - Essentially the same thing as a Parent Activity.  Specifically it is an activity beneath a Root Activity that has children associated to it.  I use this term when referring "down" the tree to another Parent Activity that has more of a child relationship. Could call this Child Activity too...
     *  Parent Activity - Every activity will have a parent, and only one parent.  A parent of zero indicates the activity is a Root Activity.
     *  Barren Activity - An activity that a time sheet built for it.  These are the only activities that can have a time sheet built for it.  A Barren Activity will not have any Sub Activities child activities associated to it.
     *
     */

    //
    // Display Full Activity Tree (only if subactivities found)
    //
    if (getActivity.getSubActivityCount(sess_activity_id, con) > 0) {

        getActivity.buildActivityTree(activity_id, 0, out, con, req);
    }

    // If the current activity_id has has no child sub activities (only barren children), set group_id = activity_id so time sheets are shown
    if (getActivity.getSubActivityCount(activity_id, con) == 0) {
        group_id = activity_id;
    }
    
    out.println("<br><br>");


    //
    // DO THE CHECK ALL IN IF REQUESTED
    //
    if (checkinAccess && req.getParameter("chkallin") != null && req.getParameter("chkallin").equals("1")) {

        try {

            pstmt = con.prepareStatement ("" +
                    "UPDATE activity_sheets_players t1, activity_sheets t2 " +
                    "SET `show` = 1 " +
                    "WHERE " +
                        "player_name <> '' AND " +
                        "t1.activity_sheet_id = t2.sheet_id AND " +
                        //"DATE_FORMAT(t2.date_time, '%Y%m%d') = ? AND " +
                        " date_time BETWEEN ? AND ? AND " +
                        "activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?)");

            pstmt.clearParameters();
            pstmt.setString(1, Utilities.get_mysql_timestamp((int)date, 0));
            pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 2359));
            //pstmt.setInt(1, date);
            pstmt.setInt(3, group_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Error in Proshop_gensheets updating show values for " + date + ". Exc=" + exc.getMessage() );

        } finally {

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }

    }


    //
    //  Add Activity Sheet Notes
    //
    if (tsNotesView) {

        out.println("<table align=center cellpadding=0 cellspacing=0 style=\"border: 2px solid darkGreen\">");
        out.println("<tr><td style=\"padding: 5px\" align=\"center\">");
        out.println("<div id=\"fTSHeader\" width=\"100%\" onclick=\"toggleTSN()\" style=\"cursor: pointer; padding: 5px; background-color: darkGreen; font-size: 16px; color: white; font-weight: bold\">");
        out.println("&nbsp; &nbsp; &nbsp; " + getActivity.getActivityName(sess_activity_id, con) + " Shop Notes &nbsp; &nbsp; &nbsp;");
        out.println("</div>");
        out.println("<div id=\"fTSNotes\" width=\"100%\">"); // style=\"position:relative\" onmouseover=\"fTSNotes_show()\" onmouseout=\"fTSNotes_hide()\"
        out.println("<iframe src=\"Proshop_sheet_notes?date=" + date + "&activity_id=" + sess_activity_id + "\" scrolling=\"no\" id=\"fraTSNotes\" name=\"fraTSNotes\" width=\"100%\" style=\"width:485px;height:145px\" frameborder=\"0\"></iframe>");
        out.println("</div></td></tr>");
        out.println("</table><br>");

        
        out.println("<script type=\"text/javascript\">");
        out.println("var fTSN_visible = false;");
        out.println("function toggleTSN() {");
        out.println(" fTSN_visible = (!fTSN_visible);");
        out.println(" if (fTSN_visible) {");
        out.println("  fTNotes_show();");
        out.println(" } else {");
        out.println("  fTNotes_hide();");
        out.println(" }");
        out.println("}");
        out.println("function fTNotes_show() {");
        out.println(" document.getElementById('fTSNotes').style.display='block'");
        out.println("}");
        out.println("function fTNotes_hide() {");
        out.println(" document.getElementById('fTSNotes').style.display='none'");
        out.println("}");
        out.println("</script>");

    }



    // layout modes:
    // 2 = summary 'condensed' view
    // 3 = detailed 'all' view
    // 1 = tabbed view

    
    //
    // SEE WHICH VIEWS ARE ALLOWED BY THE CONFIGURATION FOR THIS ACTIVITY
    //
    try {

        pstmt = con.prepareStatement ("" +
                "SELECT pro_allowable_views " +
                "FROM activities " +
                "WHERE activity_id = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, group_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) allowable_views = rs.getString(1);

    } catch (Exception exc) {

        Utilities.logError("Error in Proshop_gensheets looking up allowable views for activity_id " + group_id + ". Exc=" + exc.getMessage() );

    } finally {

        try { rs.close(); }
        catch (Exception ignored) {}

        try { pstmt.close(); }
        catch (Exception ignored) {}

    }

    out.println("<!-- ALLOWABLE VIEWS FOR activity_id " + group_id + " ARE " + allowable_views + " -->");

    if ( allowable_views.indexOf("1") != -1 ) allow_tabbed = true;
    if ( allowable_views.indexOf("2") != -1 ) allow_summary = true;
    if ( allowable_views.indexOf("3") != -1 ) allow_detail = true;
    if ( allowable_views.indexOf("4") != -1 || allowable_views.indexOf("2") != -1 ) allow_newsummary = true;

    //
    // SET THE DEFAULT LAYOUT MODE FOR THIS ACTIVITY
    //
    if (layout_mode == 0) {

        layout_mode = 3; // defaut if integer conversion fails
        try { layout_mode = Integer.parseInt(allowable_views.substring(0, 1)); }
        catch (Exception ignore) {}

        out.println("<!-- USING DEFAULT VIEW OF " + layout_mode + " -->");

    } else {

        out.println("<!-- USING LAYOUT MODE " + layout_mode + " -->");

    }

    // CLOSE THE FORM
    out.println("</form>");


    out.println("</center>");


    //
    // Add the Control Panel
    //
    out.println("<div id=ctrlPanel style=\"position: absolute; top: 50px; left: 30px; width: 150px\">"); //; height: 200px
    
        out.println("<table class=ctrlPanel border=\"1\" cellspacing=\"2\" cellpadding=\"3\">"); //  width=\"150\" bgcolor=\"8B8970\" align=\"center\"
        out.println("<tr>");
        out.println("<td align=\"center\" class=\"ctrlPanelHeader\"><b>Control Panel</b><br>");
        out.println("</td></tr>");

        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Refresh Sheet\">");
        out.println("Refresh Sheet</a></td></tr>");

     if (allow_tabbed) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='1';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Tabbed View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(1)\" class=\"ctrlPanelLink\" title=\"Switch to Tabbed View\">");
        out.println("Tabbed View</a></td></tr>");
     }

     if (allow_newsummary) {  // (Common_Server.SERVER_ID == 4 || club.equals("quecheeclubtennis")) &&
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='2';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(4)\" class=\"ctrlPanelLink\" title=\"Switch to New Summary View\">");
        out.println("Summary View</a></td></tr>");
     }

     if (allow_summary && !club.equals("quecheeclubtennis")) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='2';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(2)\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("Old Summary View</a></td></tr>");
     }

     if (allow_detail) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='3';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Detailed View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(3)\" class=\"ctrlPanelLink\" title=\"Switch to Detailed View\">");
        out.println("Detailed View</a></td></tr>");
     }

     // send email to members
     if (SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"Proshop_activity_sendemail?group_id=" +group_id+ "&date=" + date + "\" target=\"bot\" class=\"ctrlPanelLink\" title=\"Send Email To All Members On Selected Sheet\" alt=\"Send Email\">");
        out.println("Send Email to Members</a></td></tr>");
        
        if (Utilities.allowMobileApp(club, con)) {
            out.println("<tr><td align=\"center\">");
            out.println("<a href=\"Proshop_activity_sendemail?group_id=" +group_id+ "&date=" + date + "&push=1\" target=\"bot\" class=\"ctrlPanelLink\" title=\"Send Push Notification To All Members On Selected Sheet\" alt=\"Send Push Notificiation\">");
            out.println("Send Push Notification to Members</a></td></tr>");
        }
     }
        
        // member lookup
        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"javascript:void(0)\" onclick=\"memberLookup(); return false;\" title=\"Member Look-Up\" class=\"ctrlPanelLink\">");
        out.println("Member Look-Up</a>");       
        out.println("</td></tr>");
        
        // print sheets
        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"Proshop_activity_print_sheet?date=" +date+ "&group_id=" +group_id+ "\" target=\"_blank\" class=\"ctrlPanelLink\" title=\"Print Sheets\" alt=\"Print Sheets\">");
        out.println("Print Sheets</a>");       
        out.println("</td></tr>");
        
        // print notes - hide if old sheets
        if (!oldSheets) {
            out.println("<tr><td align=\"center\">");
            out.println("<a href=\"Proshop_activity_print_sheet?date=" +date+ "&group_id=" +group_id+ "&prtoption=notes\" target=\"_blank\" class=\"ctrlPanelLink\" title=\"Print Notes\" alt=\"Print Notes\">");
            out.println("Print Notes</a>");
            out.println("</td></tr>");
        }

        if (checkinAccess && oldSheets) {
            // if old sheets then show check in all link
            out.println("<tr><td align=\"center\">");
            out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].chkallin.value='1';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Check All In\">");
            out.println("Check All In</a>");
            out.println("</td></tr>");
        }
        out.println("</table>");
        
    out.println("</div>");

    //
    // Add hidden iframe for check-in
    //
    out.println("<span style=\"position:absolute;top:0px;left:-100px\"><iframe src=\"about:blank\" id=\"fraCheckIn\" style=\"width:0px;height:0px\"></iframe></span>");
    out.println("<script type=\"text/javascript\">");
    out.println("<!-- ");

    out.println("function reloadPage(mode) {");
    out.println(" var f = document.forms['frmLoadDay'];");
    out.println(" f.layout_mode.value=mode");
    out.println(" f.submit();");
    out.println("}");

    out.println("function reloadPageActId(activity_id) {");
    out.println(" var f = document.forms['frmLoadDay'];");
    out.println(" f.activity_id.value = activity_id;");
    out.println(" f.submit();");
    out.println("}");

    out.println("function openHistoryWindow(slot_id) {");
    out.println(" w = window.open ('Proshop_activity_history?slot_id=' +slot_id+ '&history=yes','historyPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println(" w.creator = self;");
    out.println("}");

    out.println("function openNotesWindow(slot_id) {");
    out.println(" w = window.open ('Proshop_activity_history?slot_id=' +slot_id+ '&notes=yes','notesPopup','width=640,height=360,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println(" w.creator = self;");
    out.println("}");

    out.println("function doCheckIn(player_slot_uid,imgId) {");
    out.println(" var iframe = document.getElementById('fraCheckIn');");
    out.println(" var doc = null;");
    out.println(" if (iframe.contentDocument) {");         // Firefox, Safari, Opera
    out.println("  doc = iframe.contentDocument;");
    out.println(" } else if (iframe.contentWindow) {");    // IE
    out.println("  doc = iframe.contentWindow;");
    out.println(" } else if (iframe.document) {");         // last ditch effor?
    out.println("  doc = iframe.document;");
    out.println(" }");
    out.println(" if (doc == null) {");
    out.println("  throw 'Unable to process check-in request.  Your browser does not seem to be supported.';");
    out.println(" } else {");
    out.println("  doc.location.href='Proshop_sheet_checkin?psid='+player_slot_uid+'&imgId='+imgId;");
    out.println(" }");
    out.println("}");

    out.println("function doCheckInAll(sheet_id) {");
    out.println(" var iframe = document.getElementById('fraCheckIn');");
    out.println(" var doc = null;");
    out.println(" if (iframe.contentDocument) {");         // Firefox, Safari, Opera
    out.println("  doc = iframe.contentDocument;");
    out.println(" } else if (iframe.contentWindow) {");    // IE
    out.println("  doc = iframe.contentWindow;");
    out.println(" } else if (iframe.document) {");         // last ditch effor?
    out.println("  doc = iframe.document;");
    out.println(" }");
    out.println(" if (doc == null) {");
    out.println("  throw 'Unable to process check-in request.  Your browser does not seem to be supported.';");
    out.println(" } else {");
    out.println("  doc.location.href='Proshop_sheet_checkin?all&psid='+sheet_id;");
    out.println(" }");
    out.println("}");

    out.println("function memberLookup() {");
    out.println(" var y = prompt('Enter the member number you would like to lookup.', '');");
    out.println(" if (y==null) return;");
    out.println(" y=y.replace(/^\\s+|\\s+$/g, '');"); // trim leading & trailing
    out.println(" if (y == '') return;");
    //out.println(" var ex = /^[0-9]{1,10}$/;"); // regex to enforce numeric only and 1-10 digits
    //out.println(" if (!ex.test(y)) { alert('Enter numeric characters only.'); return; }");
    out.println(" w = window.open ('Proshop_member_lookup?mem_num='+y,'memberLookupPopup','width=480,height=200,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');"); // add modal=yes to non-ie browsers
    out.println(" w.creator = self;");
    //out.println(" window.showModalDialog('Proshop_member_lookup?mem_num='+y,'memberLookupPopup','status:no;dialogWidth:620px;dialogHeight:280px;resizable:yes;center:yes;help=no');");
    out.println("}");

    out.println("// -->");
    out.println("</script>");

    // IF WE ARE HERE FOR THE OLD TIME SHEETS THEN RETREIVE THE OLDEST DATE
    if (oldSheets) {

        try {

            pstmt = con.prepareStatement("" +
                    "SELECT MIN(DATE_FORMAT(date_time, '%Y%m%d')) AS minDate " +
                    "FROM activity_sheets " +
                    "WHERE activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?)");

            pstmt.clearParameters();
            pstmt.setInt(1, group_id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                minDate = rs.getInt(1);
                oldest_year = minDate / 10000;
                int temp = oldest_year * 10000;
                oldest_month = minDate - temp;
                temp = oldest_month / 100;
                temp = temp * 100;
                oldest_day = oldest_month - temp;
                oldest_month = oldest_month / 100;

            }

        } catch (Exception exc) {

            out.println("<p>ERROR GETTING OLDEST DATE FOR ACTIVITY:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        out.println("<!-- OLDEST DATE FOUND: " + minDate + " FOR group_id " + group_id + " -->");

    } // end if old sheets



    //
    // DISPLAY CALENDARS
    //
    out.println("<table align=center border=0 height=165>\n<tr valign=top>\n<td>");   // was 190 !!!

    out.println(" <div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");

    out.println("</td>");
    if (!oldSheets) {

        out.println("<td>&nbsp; &nbsp;</td>\n<td>");

        out.println(" <div id=cal_elem_1 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");
    }
    out.println("</td>\n<tr>\n</table>");

    Calendar cal_date = new GregorianCalendar();
    if (oldSheets) cal_date.add(Calendar.DAY_OF_MONTH, -1); // get yesterdays date
    int cal_year = cal_date.get(Calendar.YEAR);
    int cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
    int cal_day = cal_date.get(Calendar.DAY_OF_MONTH);

    out.println("<script type=\"text/javascript\">");

     out.println("var g_cal_bg_color = '#F5F5DC';");
     out.println("var g_cal_header_color = '#8B8970';");
     out.println("var g_cal_border_color = '#8B8970';");

     out.println("var g_cal_count = " + ((oldSheets) ? 1 : 2) + ";"); // number of calendars on this page
     out.println("var g_cal_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
     out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
     out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");

     // set calendar date parts
     out.println("g_cal_month[0] = " + ((oldSheets) ? month : cal_month) + ";");
     out.println("g_cal_year[0] = " + ((oldSheets) ? year : cal_year) + ";");
     if (oldSheets) {
         out.println("g_cal_beginning_month[0] = " + oldest_month + ";");
         out.println("g_cal_beginning_year[0] = " + oldest_year + ";");
         out.println("g_cal_beginning_day[0] = " + oldest_day + ";");
     } else {
         out.println("g_cal_beginning_month[0] = " + cal_month + ";");
         out.println("g_cal_beginning_year[0] = " + cal_year + ";");
         out.println("g_cal_beginning_day[0] = " + cal_day + ";");
     }
     out.println("g_cal_ending_month[0] = " + cal_month + ";");
     out.println("g_cal_ending_day[0] = " + ((oldSheets) ? cal_day : 31) + ";");
     out.println("g_cal_ending_year[0] = " + cal_year + ";");

    if (!oldSheets) {
     cal_date.add(Calendar.MONTH, 1); // add a month
     cal_year = cal_date.get(Calendar.YEAR);
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
        // ((month > cal_month) ? month : cal_month)
        // ((year > cal_year) ? year : cal_year)
     out.println("g_cal_month[1] = " + (((month > cal_month && year >= cal_year) || year > cal_year) ? month : cal_month) + ";");
     out.println("g_cal_year[1] = " + ((year > cal_year) ? year : cal_year) + ";");
     out.println("g_cal_beginning_month[1] = " + cal_month + ";");
     out.println("g_cal_beginning_year[1] = " + cal_year + ";");
     out.println("g_cal_beginning_day[1] = 0;");

     cal_date.add(Calendar.MONTH, -1); // subtract a month
     cal_date.add(Calendar.YEAR, 1); // add a year
     cal_year = cal_date.get(Calendar.YEAR);
     cal_month = cal_date.get(Calendar.MONTH) + 1; // month is zero based
     out.println("g_cal_ending_month[1] = " + cal_month + ";");
     out.println("g_cal_ending_day[1] = " + cal_day + ";");
     out.println("g_cal_ending_year[1] = " + cal_year + ";");
     
     if (club.equals("ballantyne") || sess_activity_id == 1) {   //add color coding for Beverly GC

         out.print("var daysArray = new Array(");
         int js_index = 0;
         int max = 365;
         int[] days = new int[max+1];
         for (int m=0; m<max+1; m++) {
             if (m<=3) {
                 days[m] = 1;
             } else {
                 days[m] = 0;
             }
         }

         for (js_index = 0; js_index <= max; js_index++) {
             out.print(days[js_index]);
             if (js_index != max) out.print(",");
         }
         out.println(");");

         out.println("var max = " + max + ";");
     }
    }
    out.println("</script>");

    out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
    if (!oldSheets) out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");


    if (!getActivity.isChildlessActivity(sess_activity_id, con)) {
        
        //
        // ONLY DISPLAY THE TIME SHEETS IF THE GROUP ID IS SET
        //
        if (group_id > 0) {


            //
            // DISPLAY THE DATE OF THE TIME SHEET
            //
            if (date == 0) {

                out.println("<p align=center><b>Select a date.</b></p>");

            } else {

                out.println("<p align=center><b>" + dayShort_name + " " + month + "/" + day + "/" + year + " Time Sheet</b></p>");

            }

            out.println("<p align=center><b><font size=5 color=#336633>" + getActivity.getActivityName(activity_id, con) + "</font></b></p>");

            //
            // DISPLAY ANY EVENTS FOUND FOR THIS DAY
            //
            try {

                boolean found = false;

                pstmt = con.prepareStatement("" +
                        "SELECT event_id, name, color " +
                        "FROM events2b " +
                        "WHERE " +
                            "activity_id = ? AND date = ? AND inactive = 0 " +
                        "ORDER BY act_hr, act_min, name");

                pstmt.clearParameters();
                pstmt.setInt(1, sess_activity_id);
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();


                rs.last();

                if (rs.getRow() > 0) {
                    found = true;
                    out.println("<p align=center><b>" + ((oldSheets) ? "This Day's" : "Today's") + " Events:</b><br>");
                }

                rs.beforeFirst();

                while ( rs.next() ) {

                    //out.println("&nbsp;<button style=\"background-color:" + rs.getString("color") + "\" onclick=\"displayEvent('" + rs.getString("name") + "')\">" + rs.getString("name") + "</button>&nbsp;");

                    out.println("&nbsp;" +
                            "<a href=\"javascript: void(0)\" " +
                            "onclick=\"window.open('Proshop_gensheets?event=" + rs.getString("name") + "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no');return false;\">" +
                            "<button style=\"background-color:" + rs.getString("color") + "\">" + rs.getString("name") + "" +
                            "</button></a>" +
                            "&nbsp;");

                }

                if (found) out.println("</p>");

            } catch (Exception exc) {

                out.println("<p>ERROR CHECKING FOR ACTIVITY EVENTS:" + exc.toString() + "</p>");

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }
            
            
                
            // NOW CHECK FOR RESTRICTIONS
            try {

                boolean found = false;

                pstmt2 = con.prepareStatement (
                         "SELECT name, recurr, color, id, stime, etime, locations " +
                         "FROM restriction2 " +
                         "WHERE sdate <= ? AND edate >= ? AND showit = 'Yes' AND activity_id = '" + sess_activity_id + "' " +
                         "ORDER BY stime");

                //
                //  Scan the events, restrictions and lotteries to build the legend
                //
                pstmt2.clearParameters();          // clear the parms
                pstmt2.setLong(1, date);
                pstmt2.setLong(2, date);

                rs = pstmt2.executeQuery();      // find all matching restrictions, if any

                while (rs.next()) {

                    String rest_recurr = rs.getString("recurr");

                    boolean showRest = getRests.showRest(rs.getInt("id"), -99, rs.getInt("stime"), rs.getInt("etime"), date, day_name, "", rs.getString("locations"), sess_activity_id, activity_id, con);

                    if (showRest) {    // Only display on legend if not suspended for entire day

                        //
                        //  We must check the recurrence for this day (Monday, etc.)
                        //
                        if ((rest_recurr.equals( "Every " + day_name )) ||          // if this day
                            (rest_recurr.equalsIgnoreCase( "every day" )) ||        // or everyday
                            ((rest_recurr.equalsIgnoreCase( "all weekdays" )) &&    // or all weekdays (and this is one)
                              (!day_name.equalsIgnoreCase( "saturday" )) &&
                              (!day_name.equalsIgnoreCase( "sunday" ))) ||
                            ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&    // or all weekends (and this is one)
                             (day_name.equalsIgnoreCase( "saturday" ))) ||
                            ((rest_recurr.equalsIgnoreCase( "all weekends" )) &&
                             (day_name.equalsIgnoreCase( "sunday" )))) {


                            if (found == false) {      // if first one

                                found = true;
                                out.println("<p align=center><b>Today's Restrictions:</b><br>");
                            }
                            
                            out.println("&nbsp;<a href='javascript:void(0)' onClick='window.open(&quot;Proshop_gensheets?rest=" + rs.getInt("id") + "&amp;date=" + date + "&quot;, &quot;newwindow&quot;, &quot;height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no&quot;);return false;'>" +
                                    "<button style='background-color:" + rs.getString("color") + "'>" + rs.getString("name") + "</button></a>&nbsp;");

                        }

                    } // end if showRest

                } // end of while

                if (found) out.println("</p>");

                //   endof Restrictions !!!!!!!


            } catch (Exception exc) {

                out.println("<p>ERROR CHECKING FOR ACTIVITY EVENTS:" + exc.toString() + "</p>");

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

                try { pstmt2.close(); }
                catch (Exception ignore) {}

            }


            //
            //**********************************************
            //   Check for Member Notice from Pro
            //**********************************************
            //
            String memNoticeMsg = verifySlot.checkMemNotice(date, 0, 0, 0, "", day_name, "teesheet", true, sess_activity_id, con);

            if (!memNoticeMsg.equals("")) {

                int notice_mon = 0;
                int notice_tue = 0;
                int notice_wed = 0;
                int notice_thu = 0;
                int notice_fri = 0;
                int notice_sat = 0;
                int notice_sun = 0;

                String notice_msg = "";
                String notice_bgColor = "";

                try {

                    // Get relevent member notice data from database
                    pstmt = con.prepareStatement(
                          "SELECT mon, tue, wed, thu, fri, sat, sun, message, bgColor " +
                          "FROM mem_notice " +
                          "WHERE sdate <= ? AND edate >= ? AND activity_id = ? AND teesheet=1 AND proside=1");

                    pstmt.clearParameters();
                    pstmt.setLong(1, date);
                    pstmt.setLong(2, date);
                    pstmt.setInt(3, sess_activity_id);

                    rs = pstmt.executeQuery();

                    out.println("<br><table align=\"center\" border=\"2\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"0\" cellspacing=\"0\">");
                    out.println("<tr><td><table border=\"0\" cols=\"1\" bgcolor=\"#f5f5dc\" cellpadding=\"3\">");
                    out.println("<tr><td align=\"center\" valign=\"center\"><font size=\"4\"><b>*** Important Notice ***</b></font></td></tr>");

                    while (rs.next()) {

                        notice_mon = rs.getInt("mon");
                        notice_tue = rs.getInt("tue");
                        notice_wed = rs.getInt("wed");
                        notice_thu = rs.getInt("thu");
                        notice_fri = rs.getInt("fri");
                        notice_sat = rs.getInt("sat");
                        notice_sun = rs.getInt("sun");
                        notice_msg = rs.getString("message");
                        notice_bgColor = rs.getString("bgColor");

                        if ((notice_mon == 1 && day_name.equals( "Monday" )) || (notice_tue == 1 && day_name.equals( "Tuesday" )) || (notice_wed == 1 && day_name.equals( "Wednesday" )) ||
                            (notice_thu == 1 && day_name.equals( "Thursday" )) || (notice_fri == 1 && day_name.equals( "Friday" )) || (notice_sat == 1 && day_name.equals( "Saturday" )) ||
                            (notice_sun == 1 && day_name.equals( "Sunday" ))) {

                            out.println("<tr>");
                            if (!notice_bgColor.equals("")) {
                                out.println("<td width=\"700\" bgColor=\"" + notice_bgColor + "\" align=\"center\">");
                            } else {
                                out.println("<td width=\"700\" align=\"center\">");
                            }
                            out.println("<font size=\"2\">" + notice_msg + "</font></td></tr>");
                        }

                    }  // end WHILE loop

                    out.println("</table></td></tr></table><br>");

                } catch (Exception e1) {

                    out.println("<p>Error: Unable to display member notice.</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                }

            } // end if notice found


            //
            // CREATE PLACE HOLDER FOR ANY RESTRICTIONS WE FIND
            //
            out.println("<div id=restHolder align=center></div><br>");


            //
            // DISPLAY THE TIME SHEETS
            //

            if ( layout_mode == 1 ) {

                //
                // TABBED VIEW
                //

                out.println("<table align=center border=0>");        // table to center the tabbed sheets
                out.println("<tr valign=top><td>");

               // out.println("<div align=center style=\"align:center; margin:0px auto;\" id=tabberHolder>"); // width:640px
               //  out.println("<div align=center class=\"tabber\">");

                out.println("<div style=\"align:center; margin:0px auto;\" id=tabberHolder>"); // width:640px
                out.println("<div class=\"tabber\">");

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t1.*, " +
                                "t2.activity_name, t2.max_players, t2.consec_pro, t2.consec_pro_csv, t2.interval, t2.alt_interval, " +
                                "e.name, e.color, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                " date_time BETWEEN ? AND ? " +
                            "ORDER BY sort_by, activity_name, date_time");
                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    //pstmt.setInt(2, date);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 0));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)date, 2359));
                    rs = pstmt.executeQuery();

                    int players_found = 0;
                    int last_activity_id = 0;
                    boolean tmp_first = true;
                    boolean alt = true; // #5D8733
                    boolean emptySlot = true;
                    String tabbertabdefault = "";
                    int tab_index = 0;
                    String result = "";
                    int interval = 0;
                    String color = "";
                    String rest_color = "";
                    String rest_name = "";
                    String lname = "";
                    String consec_csv = "";

                    ArrayList<Integer> consec = new ArrayList<Integer>();

                    while (rs.next()) {

                        emptySlot = true;
                        color = "";
                        lname = "";
                        rest_color = "";
                        rest_name = "";

                        // see if we are switching activities (new row)
                        if (last_activity_id != rs.getInt("activity_id")) {

                            last_activity_id = rs.getInt("activity_id");

                            consec_csv = rs.getString("t2.consec_pro_csv").trim();

                            consec.clear();

                            if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                            if (!tmp_first) {
                                tab_index++;
                                out.println("</table></div>");
                            }
                            if (tmp_first) tmp_first = false;
                            alt = true; // reset for new tab tabbertabdefault

                            tabbertabdefault = "";
                            if (last_tab == rs.getInt("activity_id") || last_tab_index == tab_index) tabbertabdefault = " tabbertabdefault";
                            out.println("<div class=\"tabbertab" + tabbertabdefault + "\" title=\"" + rs.getString("activity_name") + "\">"); 

                            out.println("<table class=\"timesheet\">");

                            // header row
                            out.println("<tr class=timesheetTH>");
                            out.println("<td class=headerTD>Time</td>");
                            if (consec.size() > 0) out.println("<td class=headerTD>Min.</td>");
                            for (int i = 1; i <= rs.getInt("max_players"); i++) {

                                out.print("<td class=headerTD>Player " + i + "</td>");
                            }
                            if (today) out.println("<td class=headerTD style=\"width:30px\">X</td>");
                            out.println("<td class=headerTD style=\"width:20px\">N</td>");
                            out.println("<td class=headerTD style=\"width:20px\">H</td>");
                            out.println("</tr>");
                        }

                        alt = (alt == false); // toggle row shading

                        // hide blocked times
                        if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                            players_found = 0; // reset

                            // button / time column
                            if (rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0) {

                                out.println("<tr class=\"timesheetTR\"><td align=center><button class=\"btnSlot\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" " +
                                            "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," +
                                            "" + layout_mode + "," + (consec.size() > 0 ? consec.get(0) : 0) + ")\">" + rs.getString("time") + "</button></td>");
                            } else {

                                out.println("<tr class=\"timesheetTR\"><td class=\"timesheetTDtime\" " +
                                            "align=\"center\">" + rs.getString("time") + "</td>");

                            }

                            // optional consecutive selection column
                            if (consec.size() > 0) {

                                if (rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0) {

                                    out.print("<td><select name=\"consec_" + rs.getInt("sheet_id") + "\" size=\"1\" " +
                                            "onchange=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date +
                                            "," + layout_mode + ",this.options[this.selectedIndex].value)\">");

                                    if (rs.getInt("interval") == rs.getInt("alt_interval") || rs.getInt("alt_interval") == 0) {
                                        interval = rs.getInt("interval");
                                    } else {
                                        interval = 1;
                                    }

                                    for (int i = 0; i < consec.size(); i++) {
                                        out.print("<option value=\"" + consec.get(i) + "\">" + (consec.get(i) * interval));
                                    }
                                    out.println("</select></td>");

                                } else {
                                    out.println("<td>&nbsp;</td>");
                                }
                            }

                            /*
                            if (rs.getInt("event_id") == 0 && rs.getInt("lesson_id") == 0) {

                                if (rs.getInt("rest_id") != 0) {

                                    color = rs.getString("rcolor");
                                    restArray.add(rs.getInt("rest_id"));

                                }*/


                            //
                            // determin which color we are going to shade this row
                            //
                            if (rs.getInt("event_id") != 0 && rs.getString("color") != null) {

                                out.println("<!-- EVENT FOUND -->");
                                color = rs.getString("color");

                            } else if (rs.getInt("lesson_id") != 0) {

                                out.println("<!-- LESSON FOUND -->");
                                result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);

                                StringTokenizer tok = new StringTokenizer( result, "|" );

                                lname = tok.nextToken();      // first token is the name
                                color = tok.nextToken();      // second is the color

                            } else if (rs.getInt("rest_id") != 0) {

                                try {

                                    // check all restrinctions to see if this time is affected
                                    String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                                    if (!restData.equals("")) {      // if something found

                                        out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                        // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                        StringTokenizer tok = new StringTokenizer( restData, ":" );

                                        rest_color = tok.nextToken();               // get the color for this time
                                        rest_name = tok.nextToken();
                                    }

                                } catch (Exception exc) {

                                    out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                                }

                                if (!rest_color.equals("")) color = rest_color;

                                restArray.add(rs.getInt("rest_id"));

                            } else {

                                out.println("<!-- NO EVENT/LESSON/RESTRICTION -->");

                                if (club.equals("lakewoodcc") && verifyCustom.checkLakewoodActivityTimes(rs.getInt("sheet_id"), con)) {

                                    color = "yellow";
                                }

                            }

                                try {

                                    pstmt2 = con.prepareStatement("" +
                                            "SELECT asp.*, m.tflag " +
                                            "FROM activity_sheets_players asp " +
                                            "LEFT OUTER JOIN member2b m ON m.username = asp.username " + 
                                            "WHERE activity_sheet_id = ? " +
                                            "ORDER BY pos");
                                    pstmt2.clearParameters();
                                    pstmt2.setInt(1, rs.getInt("sheet_id"));
                                    rs2 = pstmt2.executeQuery();

                                    while (rs2.next()) {

                                        players_found++;
                                        emptySlot = false;

                                        out.print("<td class=timesheetTD nowrap ");
                                        //out.print(((rs.getInt("rest_id") != 0) ? "style=\"background-color:" + color + "\"" : ""));
                                        out.print((( !color.equals("") ) ? "style=\"background-color:" + color + "\"" : ""));
                                        out.print(">");

                                        if (checkinAccess) {   // only print button if proshop user has access to checkin

                                            String tmp_title;
                                            out.print("&nbsp;<img src=\"/" +rev+ "/images/");

                                            switch (rs2.getInt("show")) {
                                            case 1:
                                                tmp_title = "Click here to set as a no-show (blank).";
                                                out.print("xbox.gif");
                                                break;
                                            case 2:
                                                tmp_title = "Click here to acknowledge new signup (pre-check in).";
                                                out.print("rmtbox.gif");
                                                break;
                                            default:
                                                tmp_title = "Click here to check player in (x).";
                                                out.print("mtbox.gif");
                                                break;
                                            }

                                            // check-in image and player name
                                            out.print("\" border=\"1\" name=\"noShow\" title=\"" + tmp_title + "\" " +
                                                      "onclick=\"doCheckIn("+rs2.getInt("activity_sheets_player_id")+",this.id);\" " +
                                                      "id=\"chkbox_"+rs2.getInt("activity_sheets_player_id")+"\">" +
                                                      "&nbsp;" + rs2.getString("player_name") + ((rs2.getString("m.tflag") != null && !rs2.getString("m.tflag").equals("") && club.equals("charlottecc")) ? (" " + rs2.getString("m.tflag")) : "") + "</td>");
                                            
                                        }

                                    } // end player loop

                                    pstmt2.close();

                                } catch (Exception exc) {

                                    out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                                }


                                // if no players then check to see if it was an event or lesson
                                // and if not then check for a restriction and coloring the rows
                                // accordingly. the player slots will all be full if any of these match
                                if (emptySlot) {

                                    if (rs.getInt("event_id") != 0) {

                                        // time is covered by an event
                                        out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD " +
                                                    "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\" " +
                                                    "nowrap><i>" + rs.getString("name") + "</i></td>");

                                        players_found = rs.getInt("max_players");

                                    } else if (rs.getInt("lesson_id") != 0) {

                                        result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);

                                        StringTokenizer tok = new StringTokenizer( result, "|" );

                                        lname = tok.nextToken();       // get the name of this lesson
                                        String lcolor = tok.nextToken();      // get the color for this lesson

                                        out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD nowrap " +
                                                    "style=\"background-color:" + lcolor + "\"><i>" + lname + "</i></td>");

                                        players_found = rs.getInt("max_players");

                                    } else if (!rest_name.equals("")) {

                                        // if no players are here and there is a restriction covering this time - display its name
                                        out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                    "" + color + "\"><i>" + rest_name + "</i></td>");

                                        players_found = rs.getInt("max_players");
                                    }

                                }
                            /*
                            } else {

                                if (rs.getInt("event_id") != 0) {

                                    // time is covered by an event
                                    out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD " +
                                                "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\" " +
                                                "nowrap><i>" + rs.getString("name") + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                } else if (rs.getInt("lesson_id") != 0) {

                                    result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);

                                    StringTokenizer tok = new StringTokenizer( result, "|" );

                                    String lname = tok.nextToken();       // get the name of this lesson
                                    String lcolor = tok.nextToken();      // get the color for this lesson

                                    out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD nowrap " +
                                                "style=\"background-color:" + lcolor + "\"><i>" + lname + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                }

                            }
                            */
                            // see if we need to fill in any remaining player positions for this time slot
                            while (players_found < rs.getInt("max_players")) {

                                out.print("<td class=\"timesheetTD\"" + (( !color.equals("") ) ? " style=\"background-color:" + color + "\"" : "") + ">&nbsp;</td>");
                                players_found++;
                            }

                            if (!emptySlot) {

                                if (today) out.println("<td class=\"timesheetTDtime\"><img src=\"/" + rev + "/images/checkall.gif\" " +
                                                       "title=\"Click here to check all players in.\" " +
                                                       "onclick=\"doCheckInAll("+rs.getInt("sheet_id")+")\"></td>");

                                if (rs.getString("notes").length() > 0) {
                                    out.println("<td class=\"timesheetTDtime\"><a href=\"javascript:void(0)\" " +
                                                "onclick=\"openNotesWindow(" + rs.getInt("sheet_id") + ")\">" +
                                                "<img src=\"/" + rev + "/images/notes.jpg\" width=\"12\" height=\"13\" border=\"0\"></a></td>");
                                } else {
                                out.println("<td class=\"timesheetTDtime\">&nbsp;</td>");
                                }
                            } else {
                                if (today) out.println("<td class=\"timesheetTDtime\">&nbsp;</td>");
                                out.println("<td class=\"timesheetTDtime\">&nbsp;</td>");
                            }
                            out.println("<td class=\"timesheetTDtime\" align=\"center\"><a href=\"javascript:void(0)\" " +
                                        "onclick=\"openHistoryWindow(" + rs.getInt("sheet_id") + ")\">" +
                                        "<img src=\"/" + rev + "/images/history.gif\" width=\"12\" height=\"13\" border=\"0\"></a></td>");
                            out.println("</tr>");

                        } // end if blocked

                    } // end time slot rs loop

                    pstmt.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                }

                out.println("</table>");

                out.println("</div>");

                out.println("</div>");

                // end the main table
                out.println("</td><tr></table>");

                //buildRestHolder(restArray, date, con, out);


            } else if ( layout_mode == 2 ) {


                //
                // SUMMARY VIEW
                //

                // this array will hold the activities and the order in which they are displayed
                ArrayList<Integer> order = new ArrayList<Integer>();
                ArrayList<Integer> consec = new ArrayList<Integer>();

                String consec_csv = "";

                out.println("<table align=\"center\" border=\"0\">");
                out.println("<tr valign=\"top\"><td>");

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t2.activity_name, t2.activity_id " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                " date_time BETWEEN ? AND ? " +
                            "GROUP BY t2.activity_name " +
                            "ORDER BY t2.sort_by, t2.activity_name");

                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    //pstmt.setInt(2, date);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 0));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)date, 2359));
                    rs = pstmt.executeQuery();

                    out.println("<table class=\"timeSheet\">");
                    out.println("<tr>");
                    out.println("<td class=\"headerTD\" nowrap><b>Time</b></td>");
                    out.println("</tr>");

                    int i = 0;

                    while ( rs.next() ) {

                        order.add(rs.getInt("activity_id"));

                        out.println("<tr>");
                        out.println("<td class=\"timesheetTD2\" nowrap id=\"row" + i + "\"><b>" + rs.getString(1) + "</b></td>");
                        out.println("</tr>");
                        i++;

                    }
                    //out.println("</table>");

                } catch (Exception exc) {


                }

                //out.println("</td><td>");
                //out.println("<table class=timeSheet>");

                out.println("<script>");
                out.println("function hiLite(i) {");
                out.println(" document.getElementById('row'+i).className='timesheetTD2over';");
                //out.println(" window.status = 'row='+i;");
                out.println("}");
                out.println("function hiLiteOff(i) {");
                out.println(" document.getElementById('row'+i).className='timesheetTD2';");
                out.println("}");
                out.println("</script>");

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t1.*, t2.activity_name, t2.max_players, t2.consec_pro_csv, e.name, e.color, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                " date_time BETWEEN ? AND ? " +
                            "ORDER BY date_time, sort_by, activity_name");
                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    //pstmt.setInt(2, date);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 0));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)date, 2359));
                    rs = pstmt.executeQuery();

                    String last_time = "";
                    String onclick = "";
                    String td_style = "";
                    String rest_color = "";
                    int i = 0;
                    int order_pos = 0;

                    while ( rs.next() ) {

                        rest_color = "";

                        // see if we are switching times (new col)
                        if ( !last_time.equals(rs.getString("time")) ) {

                            last_time = rs.getString("time");
                            out.println("</table></td><td>");           // end current column and start new one
                            out.println("<table class=\"timeSheet\">");     // start new table inside this column
                            out.println("<tr><td class=\"headerTD\" nowrap align=\"center\"><b>" + last_time + "</b></td></tr>");
                            i = 0;
                            order_pos = 0;
                        }

                        consec_csv = rs.getString("t2.consec_pro_csv").trim();

                        consec.clear();

                        if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                        // if not in use and not blocked & not a lesson & not an event
                        if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0 && 
                            rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("rest_id") == 0) {

                            onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + (consec.size() > 0 ? "," + consec.get(0) : "") + ")\"";
                            td_style = "style=\"background: " + ((oldSheets) ? "white" : "#E9F4BB") + "; cursor:pointer\"";

                            if (club.equals("lakewoodcc") && verifyCustom.checkLakewoodActivityTimes(rs.getInt("sheet_id"), con)) {

                                td_style = "style=\"background: yellow; cursor:pointer\"";
                            }

                        } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                            // time is blocked
                            onclick = "";
                            td_style = "style=\"background: black;\"";

                        } else if (rs.getInt("lesson_id") != 0) {

                            // time is taken by a lesson booking
                            onclick = "";
                            td_style = "style=\"background: #E9F4BB;\"";

                        } else if (rs.getInt("event_id") != 0) {

                            // time is covered by an event
                            onclick = "";
                            td_style = "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\"";


                        } else if (rs.getInt("rest_id") != 0) {

                            try {

                                // check all restrinctions to see if this time is affected
                                String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                                if (!restData.equals("")) {      // if something found

                                    out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                    // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                    StringTokenizer tok = new StringTokenizer( restData, ":" );

                                    rest_color = tok.nextToken();               // get the color for this time

                                } else {       // no restrinctions found

                                    if (oldSheets) {
                                        rest_color = "white";
                                    } else {
                                        rest_color = "#E9F4BB";
                                    }
                                }

                            } catch (Exception exc) {

                                out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                            }

                            // time is covered by a restriction
                            onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + (consec.size() > 0 ? "," + consec.get(0) : "") + ")\"";
                            td_style = "style=\"background: " + rest_color + ";\"";

                            restArray.add(rs.getInt("rest_id"));

                        } else if (!rs.getString("in_use_by").equals("")) {

                            // time is in-use
                            onclick = "";
                            td_style = "style=\"background: yellow;\"";

                        }

                        // see if we've skipped over some times
                        while (order.get(order_pos) != rs.getInt("activity_id")) {
                            out.println("<tr class=\"timesheetTR\"><td class=\"timesheetTD2\">&nbsp;</td></tr>");
                            i++;
                            order_pos++;
                        }

                        out.println("<tr onmouseover=\"hiLite(" + i + ")\" onmouseout=\"hiLiteOff(" + i + ")\">");
                        i++;
                        order_pos++;

                        // output the players (if any)
                        try {

                            pstmt2 = con.prepareStatement("" +
                                    "SELECT COUNT(*) AS c " +
                                    "FROM activity_sheets_players " +
                                    "WHERE activity_sheet_id = ? " +
                                    "ORDER BY pos");
                            pstmt2.clearParameters();
                            pstmt2.setInt(1, rs.getInt("sheet_id"));
                            rs2 = pstmt2.executeQuery();

                            if ( rs2.next() ) {

                                if (rs2.getInt(1) > 0) {

                                    // EXISTING PLAYERS

                                    out.print("<td class=\"timesheetTD2\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" align=\"left\" nowrap ");

                                    // if there is room to join and existing players have not said no to joining then color as normal
                                    if (rs.getInt("force_singles") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {

                                        // time slot is not full and others can join
                                        out.print(onclick + " " + td_style);

                                    } else {

                                        // time slot is full - make clickable but change bgcolor in indicate it's full
                                        out.print(onclick + " " + "style=\"background: white;\"");

                                    }
                                    out.print(">");

                                    // if time slot can be joined display the green bar
                                    if (!oldSheets && rs.getInt("force_singles") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {

                                        out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");

                                    }

                                    out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ")<nobr>");

                                } else {

                                    // EMPTY TIME SLOT

                                    out.print("<td class=\"timesheetTD2\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" " + onclick + " " + td_style + " nowrap>");

                                    if ( rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && 
                                         rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                                        // NOT IN USE
                                        if (!oldSheets) out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                        out.print("&nbsp;");

                                    } else {

                                        // IN USE OR TAKEN BY LESSON
                                        if (rs.getInt("lesson_id") != 0) {

                                            out.println("<center><i>" + ((rs.getInt("lesson_id") > 0) ? "Lesson" : "Clinic") + "</i></center>");

                                        } else if (rs.getInt("event_id") > 0) {

                                            out.println("<center><i>Event</i></center>");
                                            //out.println("<center><i>" + rs.getString("name") + "</i></center>");

                                        } else if (rs.getInt("blocker_id") > 0) {

                                            out.println("<center><i><font color=\"white\">Blocked</font></i></center>");

                                        } else {

                                            out.println("&nbsp;<i>in use</i>&nbsp;");

                                        }
                                    }

                                }

                            }

                            pstmt2.close();

                        } catch (Exception exc) {

                            out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");

                        }

                        // end player time slot td/tr
                        out.println("</td></tr>");

                    } // end while loop for all the time slots

                    pstmt.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                }

                // end the main table
                out.println("</td><tr></table>");

                //buildRestHolder(restArray, date, con, out);

            } else if (layout_mode == 3) {


                // LAYOUT MODE = 3 - Detailed View


                // FIND OUT THE MAX # OF PLAYER POSITIONS & CONSECUTIVE TIMES FOR GROUP OF ACTIVITIES
                int max_players = 0;
                boolean disp_consec = false;

                String consec_csv = "";

                ArrayList<Integer> consec = new ArrayList<Integer>();

                try {

                    stmt = con.createStatement();
                    rs = stmt.executeQuery("" +
                            "SELECT max_players, consec_pro_csv " +
                            "FROM activities " +
                            "WHERE activity_id IN (SELECT activity_id FROM activities WHERE parent_id = " + group_id + ")");

                    while (rs.next()) {
                        
                        if (rs.getInt("max_players") > max_players) {
                            max_players = rs.getInt("max_players");
                        }
                        
                        String [] temp = rs.getString("consec_pro_csv").split(",");
                        
                        if (temp.length > 1) {
                            disp_consec = true;
                        }
                    }

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { stmt.close(); }
                    catch (Exception ignore) {}

                }
                
                String color = "";

                out.println("<table align=\"center\" border=\"0\">");     // one big table for whole sheet to align in center of page
                out.println("<tr valign=top><td>");

    //          out.println("<div id=tabberHolder style=\"align:center; margin:0px auto;\">"); // width:640px
    //          out.println("<div class=\"tabber\">");

                out.println("<table align=\"center\" class=\"timesheet\">");

                // header row
                out.println("<tr class=\"timesheetTH\">");
                out.println("<td class=\"headerTD\">Time</td>");
                if (disp_consec) out.println("<td class=\"headerTD\">Min.</td>");
                out.println("<td class=\"headerTD\" width=\"100\">Court</td>");
                for (int i = 1; i <= max_players; i++) {

                    out.print("<td class=\"headerTD\">Player " + i + "</td>");
                }
                if (today) out.println("<td class=\"headerTD\" width=\"30\" style=\"width:30px\">X</td>"); //  style=\"width:30px\"
                out.println("<td class=\"headerTD\" width=\"20\" style=\"width:20px\">N</td>"); //  style=\"width:20px\"
                out.println("<td class=\"headerTD\" width=\"20\" style=\"width:20px\">H</td>");
                out.println("</tr>");

                try {

                    pstmt = con.prepareStatement("" +
                            "SELECT t1.*, " +
                                "t2.activity_name, t2.max_players, t2.consec_pro, t2.consec_pro_csv, t2.interval, t2.alt_interval, t2.hndcpProSheet, " +
                                "e.name, e.color, " +
                                "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                            "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                " date_time BETWEEN ? AND ? " +
                            "ORDER BY date_time, sort_by, activity_name");
                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    //pstmt.setInt(2, date);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 0));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)date, 2359));
                    rs = pstmt.executeQuery();

                    int players_found = 0;
                    int interval = 0;
                    boolean alt = true; // #5D8733
                    boolean emptySlot = true;
                    boolean dispHdcp = false;
                    String last_time = "";
                    String rest_color = "";
                    String rest_name = "";

                    while (rs.next()) {

                        emptySlot = true;   // reset
                        dispHdcp = false;
                        color = "";
                        rest_color = "";
                        rest_name = "";
                        
                        if (rs.getInt("hndcpProSheet") == 1) {
                            dispHdcp = true;
                        }

//                        if (club.equals("gallerygolf")) {
//                            
//                            int tempTime = rs.getInt("intTime");
//                                
//                            if (day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) {
//                                
//                                if (tempTime != 700 && tempTime != 830 && tempTime != 1000 && tempTime != 1130
//                                        && tempTime != 1300 && tempTime != 1430 && tempTime != 1600 && tempTime != 1730) {
//                                    continue;
//                                }
//
//                            } else {
//
//                                if (tempTime != 600 && tempTime != 730 && tempTime != 900 && tempTime != 1030 && tempTime != 1200
//                                        && tempTime != 1330 && tempTime != 1500 && tempTime != 1630 && tempTime != 1800) {
//                                    continue;
//                                }
//                            }
//                        }
                        
                        if ( !last_time.equals(rs.getString("time")) ) {

                            last_time = rs.getString("time");
                            alt = (alt == false); // toggle row shading
                        }

                        // hide blocked times
                        if (rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                            players_found = 0; // reset

                            // if not in use, not covered by a lesson or an event then it is selectable
                            if (rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0) {

                                consec_csv = rs.getString("t2.consec_pro_csv").trim();

                                consec.clear();

                                if (!consec_csv.equals("")) consec = Utilities.buildActivityConsecList(consec_csv);

                                // selectable time
                                out.println("<tr class=\"timesheetTR\"><td align=\"center\"><button class=\"btnSlot\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" " +
                                            "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," +
                                            "" + layout_mode + "," + (consec.size() > 0 ? consec.get(0) : 0) + ")\">" + rs.getString("time") + "</button></td>");

                                if (consec.size() > 1) {

                                    out.print("<td><select name=\"consec_" + rs.getInt("sheet_id") + "\" size=\"1\" " +
                                            "onchange=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date +
                                            "," + layout_mode + ",this.options[this.selectedIndex].value)\">");

                                    if (rs.getInt("interval") == rs.getInt("alt_interval") || rs.getInt("alt_interval") == 0) {
                                        interval = rs.getInt("interval");
                                    } else {
                                        interval = 1;
                                    }

                                    for (int i = 0; i < consec.size(); i++) {
                                        out.print("<option value=\"" + consec.get(i) + "\">" + (consec.get(i) * interval));
                                    }
                                    out.println("</select></td>");

                                } else if (disp_consec) {

                                    // other activities being displayed allow consecutive times but not this one
                                    out.print("<td>&nbsp;</td>");

                                }

                            } else {

                                // non-selectable time
                                out.println("<tr class=\"timesheetTR\"><td align=\"center\">" + rs.getString("time") + "</td>");

                                if (disp_consec) out.println("<td>&nbsp;</td>");

                            }

                            out.print("<td class=\"" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + "\" nowrap>" +
                                      "" + rs.getString("activity_name") + "</td>");

                            // only look up the players is time slot is not for a lesson or an event
                            if (rs.getInt("event_id") == 0 && rs.getInt("lesson_id") == 0) {

                                if (rs.getInt("rest_id") != 0) {

                                    try {

                                        // check all restrinctions to see if this time is affected
                                        String restData = getRestData(parmr, rs.getInt("intTime"), rs.getInt("activity_id"), rs.getInt("rest_id"));

                                        if (!restData.equals("")) {      // if something found

                                            out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                            // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                            StringTokenizer tok = new StringTokenizer( restData, ":" );

                                            rest_color = tok.nextToken();               // get the color for this time
                                            rest_name = tok.nextToken();
                                        }

                                    } catch (Exception exc) {

                                        out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                                    }

                                    if (!rest_color.equals("")) color = rest_color;

                                    restArray.add(rs.getInt("rest_id"));

                                }

                                if (club.equals("lakewoodcc") && verifyCustom.checkLakewoodActivityTimes(rs.getInt("sheet_id"), con)) {

                                    color = "yellow";
                                }

                                try {

                                    pstmt2 = con.prepareStatement(""
                                            + "SELECT asp.*, m.tflag, m.ntrp_rating, m5.tflag "
                                            + "FROM activity_sheets_players asp "
                                            + "LEFT OUTER JOIN member2b m ON m.username = asp.username "
                                            + "LEFT OUTER JOIN mship5 m5 ON m.m_ship = m5.mship AND m5.activity_id = ? "
                                            + "WHERE activity_sheet_id = ? "
                                            + "ORDER BY pos");
                                    pstmt2.clearParameters();
                                    pstmt2.setInt(1, sess_activity_id);
                                    pstmt2.setInt(2, rs.getInt("sheet_id"));
                                    rs2 = pstmt2.executeQuery();

                                    while ( rs2.next() ) {

                                        players_found++;
                                        emptySlot = false;

                                        out.print("<td class=\"timesheetTD\" nowrap ");
                                        out.print(((rs.getInt("rest_id") != 0 || (club.equals("lakewoodcc") && !color.equals(""))) ? "style=\"background-color:" + color + "\"" : ""));

                                        out.print(">");

                                        if (checkinAccess) {   // only print button if proshop user has access to checkin

                                            String tmp_title;
                                            out.print("&nbsp;<img src=\"/" +rev+ "/images/");

                                            switch (rs2.getInt("show")) {
                                            case 1:
                                                tmp_title = "Click here to set as a no-show (blank).";
                                                out.print("xbox.gif");
                                                break;
                                            case 2:
                                                tmp_title = "Click here to acknowledge new signup (pre-check in).";
                                                out.print("rmtbox.gif");
                                                break;
                                            default:
                                                tmp_title = "Click here to check player in (x).";
                                                out.print("mtbox.gif");
                                                break;
                                            }

                                            // check-in image and player name
                                            out.print("\" border=\"1\" name=\"noShow\" title=\"" + tmp_title + "\" "
                                                    + "onclick=\"doCheckIn(" + rs2.getInt("activity_sheets_player_id") + ",this.id);\" "
                                                    + "id=\"chkbox_" + rs2.getInt("activity_sheets_player_id") + "\">"
                                                    + "&nbsp;" + rs2.getString("player_name")
                                                    + (dispHdcp && rs2.getDouble("m.ntrp_rating") > 0 ? " " + rs2.getDouble("ntrp_rating") : "") 
//                                                    + ((club.equals("theranchcc") && sess_activity_id == 1 && rs2.getDouble("m.ntrp_rating") != 0) ? " " + rs2.getDouble("ntrp_rating") : "") 
                                                    + ((rs2.getString("m.tflag") != null && !rs2.getString("m.tflag").equals("") && club.equals("charlottecc")) ? (" " + rs2.getString("m.tflag")) : "")
                                                    + (rs2.getString("m5.tflag") != null && !rs2.getString("m5.tflag").equals("") ? " " + rs2.getString("m5.tflag") : "")
                                                    + "</td>");

                                        }

                                    } // end player loop

                                    pstmt2.close();

                                } catch (Exception exc) {

                                    out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                                }

                                // if no players are here and there is a restriction covering this time - display its name
                                if (emptySlot && !rest_name.equals("")) {

                                    out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                "" + color + "\"><i>" + rest_name + "</i></td>");

                                    players_found = rs.getInt("max_players");
                                }

                            } else {

                                // this is an event or lesson time
                                if (rs.getInt("event_id") != 0) {

                                    // time is covered by an event
                                    out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                "" + ((rs.getString("color") == null) ? "" : rs.getString("color")) + "\">" +
                                                "<i>" + rs.getString("name") + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                } else if (rs.getInt("lesson_id") != 0) {

                                    String result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);
                                    StringTokenizer tok = new StringTokenizer( result, "|" );

                                    String lname = tok.nextToken();       // get the name of this lesson
                                    String lcolor = tok.nextToken();      // get the color for this lesson

                                    out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                "" + lcolor + "\"><i>" + lname + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                }
                            }

                            // see if we need to fill in any remaining player positions for this time slot based upon the activity
                            while (players_found < rs.getInt("max_players")) {

                                out.print("<td class=timesheetTD ");
                                out.print((rs.getInt("rest_id") != 0 || (club.equals("lakewoodcc") && !color.equals(""))) ? "style=\"background-color:" + color + "\">" : ">");
                                out.print("&nbsp;</td>");

                                players_found++;
                            }

                            // now see if we need to fill in any remaining player positions for this time slot based upon the activity group
                            while (players_found < max_players) {

                                // if we're in here then this activity must support less players than others being displayed
                                // so lets build td that are grayed out
                                out.print("<td class=timesheetTD style=\"background-color: black\">&nbsp</td>");

                                players_found++;
                            }

                            if (!emptySlot) {

                                if (today) out.println("<td class=\"timesheetTD\" style=\"width:30px\">" +
                                                       "<img src=\"/" + rev + "/images/checkall.gif\" " +
                                                       "title=\"Click here to check all players in.\" " +
                                                       "onclick=\"doCheckInAll("+rs.getInt("sheet_id")+")\"></td>");

                                if (rs.getString("notes").length() > 0) {

                                    out.println("<td class=\"timesheetTD\" style=\"width:20px\"><a href=\"javascript:void(0)\" " +
                                                "onclick=\"openNotesWindow(" + rs.getInt("sheet_id") + ")\">" +
                                                "<img src=\"/" + rev + "/images/notes.jpg\" border=0></a></td>");

                                } else {

                                    out.println("<td class=\"timesheetTD\" style=\"width:20px\">&nbsp;</td>");

                                }

                            } else {

                                if (today) out.println("<td class=\"timesheetTD\" style=\"width:30px\">&nbsp;</td>");   // check all
                                out.println("<td class=\"timesheetTD\" style=\"width:20px\">&nbsp;</td>");              // notes

                            }

                            out.println("<td class=\"timesheetTD\" style=\"width:20px\" align=center><a href=\"javascript:void(0)\" " +
                                        "onclick=\"openHistoryWindow(" + rs.getInt("sheet_id") + ")\">" +
                                        "<img src=\"/" + rev + "/images/history.gif\" width=12 height=13 border=0></a></td>");

                            out.println("</tr>");

                        } // end if blocked

                    } // end time slot rs loop

                    pstmt.close();

                } catch (Exception exc) {

                    out.println("<p>ERROR:" + exc.toString() + "</p>");

                }

                out.println("</table>");
    //          out.println("</div>");
    //          out.println("</div>");

                // end the main table
                out.println("</td><tr></table>");

                //buildRestHolder(restArray, date, con, out);

                // end of layout mode 3

            } else if (layout_mode == 4) {


                //
                // LAYOUT MODE 4 - NEW SUMMARY VIEW
                //

                // this array will hold the activities and the order in which they are displayed
                ArrayList<Integer> order = new ArrayList<Integer>();
                ArrayList<String> activity_names = new ArrayList<String>();

                String curr_activity_instring = "";

                int activity_ids_per_row = getActivity.getColsPerRow(group_id, con);      // Determines how many activity ids are displayed in each row of the time sheet
                
                List<Integer> cols_per_row = getActivity.getColsPerRowCsv(group_id, con);

                out.println("<script type=\"text/javascript\">");
                out.println("function hiLite(i) {");
                out.println(" document.getElementById('row'+i).className='headerTDover';");
                out.println("}");
                out.println("function hiLiteOff(i) {");
                out.println(" document.getElementById('row'+i).className='headerTD';");
                out.println("}");
                out.println("</script>");

                try {

                    // Load all the relevant activity_ids and activity_names into their respective ArrayLists
                    pstmt = con.prepareStatement("" +
                            "SELECT t2.activity_name, t2.activity_id " +
                            "FROM activity_sheets t1 " +
                            "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                            "WHERE " +
                                "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                                //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                " date_time BETWEEN ? AND ? " +
                            "GROUP BY t2.activity_id " +
                            "ORDER BY t2.sort_by, t2.activity_name");

                    pstmt.clearParameters();
                    pstmt.setInt(1, group_id); // activity_id
                    //pstmt.setInt(2, date);
                    pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 0));
                    pstmt.setString(3, Utilities.get_mysql_timestamp((int)date, 2359));
                    rs = pstmt.executeQuery();

                    while ( rs.next() ) {

                        order.add(rs.getInt("activity_id"));
                        activity_names.add(rs.getString("activity_name"));
                    }

                } catch (Exception exc) {
                    Utilities.logError("Proshop_gensheets.doGet - Error while printing layout_mode 4 header row - Err: " + exc.toString());
                }

                int col_sum = 0;
                
                // Get the sum of all values in list, to see if the specified rows account for all courts
                for (int i = 0; i < cols_per_row.size(); i++) {                
                    col_sum += cols_per_row.get(i);
                }
                
                // Continually add the last-specified value until we have enough to account for all courts
                while (col_sum < order.size()) {
                    col_sum += cols_per_row.get(cols_per_row.size() - 1);
                    cols_per_row.add(cols_per_row.get(cols_per_row.size() - 1));
                }

                int col_count = 0;

                // Loop and print n activity_ids at a time (this is determined by the "cols_per_row" field in the parent activity database entry)
//                for (int i=0; i<=((order.size()-1)/activity_ids_per_row); i++) {
                for (int i = 0; i < cols_per_row.size(); i++) {

                    curr_activity_instring = "";

                    out.println("<table align=\"center\" border=\"1\">");
                    out.println("<tr valign=\"top\"><td>");

                    out.println("<table class=\"timeSheet\">");
                    out.println("<tr>");
                    out.println("<td class=\"headerTD\" nowrap><b>Time</b></td>");

//                    for (int j = (i*activity_ids_per_row); j < ((i+1)*activity_ids_per_row) && j < order.size(); j++) {
                    for (int j = col_count; j < (col_count + cols_per_row.get(i)) && j < order.size(); j++) {

                        curr_activity_instring += (!curr_activity_instring.equals("") ? "," : "") + order.get(j);   // Add activity_ids to a csv string to be used when pulling activity_sheets below
                        out.println("<td class=\"headerTD\" style=\"width:90px\" id=\"col" + j + "\" align=\"center\"><b>" + activity_names.get(j) + "</b></td>"); // timesheetTD2
                    }         

                    try {

                        pstmt = con.prepareStatement("" +
                                "SELECT t1.*, t2.activity_name, t2.max_players, t2.consec_pro_csv, e.name, e.color, " +
                                    "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                                    "DATE_FORMAT(date_time, \"%H%i\") AS intTime, " +
                                    "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                                "FROM activity_sheets t1 " +
                                "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                                "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                                "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                                "WHERE " +
                                    "t1.activity_id IN (" + curr_activity_instring + ") AND " +
                                    //"DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                                    " date_time BETWEEN ? AND ? " +
                                "ORDER BY date_time, sort_by, activity_name");
                        pstmt.clearParameters();
                        //pstmt.setInt(1, date);
                        pstmt.setString(1, Utilities.get_mysql_timestamp((int)date, 0));
                        pstmt.setString(2, Utilities.get_mysql_timestamp((int)date, 2359));
                        rs = pstmt.executeQuery();

                        String last_time = "";
                        String onclick = "";
                        String td_style = "";
                        int row = i*1000;
                        int order_pos = 0;
                        int consec = 0;
                        int player_count = 0;
                        int curr_activity_id = 0;
                        String username = "";
                        String last_name = "";
                        String rest_color = "";

                        while ( rs.next() ) {

                            rest_color = "";
                            curr_activity_id = rs.getInt("activity_id");

//                            if (club.equals("gallerygolf")) {
//
//                                int tempTime = rs.getInt("intTime");
//
//                                if (day_name.equalsIgnoreCase("Saturday") || day_name.equalsIgnoreCase("Sunday")) {
//
//                                    if (tempTime != 700 && tempTime != 830 && tempTime != 1000 && tempTime != 1130
//                                            && tempTime != 1300 && tempTime != 1430 && tempTime != 1600 && tempTime != 1730) {
//                                        continue;
//                                    }
//
//                                } else {
//
//                                    if (tempTime != 600 && tempTime != 730 && tempTime != 900 && tempTime != 1030 && tempTime != 1200
//                                            && tempTime != 1330 && tempTime != 1500 && tempTime != 1630 && tempTime != 1800) {
//                                        continue;
//                                    }
//                                }
//                            }

                            // see if we are switching times (new row)
                            if ( !last_time.equals(rs.getString("time")) ) {

                                last_time = rs.getString("time");
                                out.println("</tr>");
                                out.println("<tr onmouseover=\"hiLite(" + row + ")\" onmouseout=\"hiLiteOff(" + row + ")\"><td class=\"headerTD\" nowrap align=\"center\" id=\"row" + row + "\"><b>" + last_time + "</b></td>"); // </tr>
//                                order_pos = i*activity_ids_per_row;
                                order_pos = col_count;
                                row++;
                            }

                            consec = getActivity.getMaxConsecTimes(user, curr_activity_id, con);

                            // if not in use and not blocked & not a lesson & not an event
                            if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0 && 
                                rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("rest_id") == 0) {

                                onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + (consec > 0 ? "," + consec : "") + ")\"";
                                td_style = "style=\"background: " + ((oldSheets) ? "white" : "#E9F4BB") + "; cursor:pointer\"";

                                if (club.equals("lakewoodcc") && verifyCustom.checkLakewoodActivityTimes(rs.getInt("sheet_id"), con)) {

                                    td_style = "style=\"background: yellow; cursor:pointer\"";
                                }

                            } else if (rs.getInt("blocker_id") != 0 || rs.getInt("auto_blocked") != 0) {

                                // time is blocked
                                onclick = "";
                                td_style = "style=\"background: black;\"";

                            } else if (rs.getInt("lesson_id") != 0) {

                                // time is taken by a lesson booking
                                String result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);
                                StringTokenizer tok = new StringTokenizer( result, "|" );

                                String lname = tok.nextToken();       // get the name of this lesson
                                String lcolor = tok.nextToken();      // get the color for this lesson

                                onclick = "";
                                td_style = "style=\"background:" + lcolor + ";\"";

                            } else if (rs.getInt("event_id") != 0) {

                                // time is covered by an event
                                onclick = "";
                                td_style = "style=\"background: " + ((rs.getString("color") == null) ? "" : rs.getString("color")) + ";\"";


                            } else if (rs.getInt("rest_id") != 0) {

                                try {

                                    // check all restrinctions to see if this time is affected
                                    String restData = getRestData(parmr, rs.getInt("intTime"), curr_activity_id, rs.getInt("rest_id"));

                                    if (!restData.equals("")) {      // if something found

                                        out.println("<!-- FOUND RESTRICTION: " + restData + " -->");

                                        // parse the string to get the restriction color and the 'allow' indicator (is member restricted?)
                                        StringTokenizer tok = new StringTokenizer( restData, ":" );

                                        rest_color = tok.nextToken();               // get the color for this time
                                    }

                                } catch (Exception exc) {

                                    out.println("<p>Error checking restrictions: " + exc.toString() + "</p>");
                                }

                                if (rest_color.equals("")) {
                                    if (oldSheets) {
                                        rest_color = "white";
                                    } else {
                                        rest_color = "#E9F4BB";
                                    }
                                }

                                // time is covered by a restriction
                                onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + (consec > 0 ? "," + consec : "") + ")\"";
                                td_style = "style=\"background: " + rest_color + ";\"";

                                restArray.add(rs.getInt("rest_id"));

                            } else if (!rs.getString("in_use_by").equals("")) {

                                // time is in-use
                                onclick = "";
                                td_style = "style=\"background: yellow;\"";

                            }

                            // see if we've skipped over any courts
                            while (order.get(order_pos) != curr_activity_id) {
                                out.println("<td class=\"timesheetTD2\">&nbsp;</td>");
                                order_pos++;
                            }

                            order_pos++;

                            // output the players (if any)
                            try {

                                // for these clubs we will display the first members last name instead of the generic 'Member' tag
                                pstmt2 = con.prepareStatement("" +
                                        "SELECT COUNT(*) AS c, username, SUBSTRING_INDEX(player_name, ' ', -1) AS last_name " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "GROUP BY activity_sheet_id " +
                                        "ORDER BY pos");

                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));
                                rs2 = pstmt2.executeQuery();

                                if ( rs2.next() ) {

                                    player_count = rs2.getInt("c");
                                    username = rs2.getString("username");
                                    last_name = rs2.getString("last_name");

                                } else {
                                    player_count = 0;
                                }

                                if (player_count > 0) {

                                    // EXISTING PLAYERS

                                    out.print("<td class=\"timesheetTD2\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" align=\"left\" nowrap ");

                                    // if there is room to join and existing players have not said no to joining then color as normal
                                    if (rs.getInt("force_singles") == 0 && player_count < rs.getInt("max_players")) {

                                        // time slot is not full and others can join
                                        out.print(onclick + " " + td_style);

                                    } else {

                                        // time slot is full - make clickable but change bgcolor in indicate it's full
                                        if (club.equals("lakewoodcc") && verifyCustom.checkLakewoodActivityTimes(rs.getInt("sheet_id"), con)) {
                                            out.print(onclick + " " + "style=\"background: yellow;\"");
                                        } else {
                                            out.print(onclick + " " + "style=\"background: white;\"");
                                        }

                                    }
                                    out.print(">");

                                    // if time slot can be joined display the green bar
                                    if (!oldSheets && rs.getInt("force_singles") == 0 && player_count < rs.getInt("max_players")) {

                                        out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");

                                    }

                                    out.print("<nobr>&nbsp;" + ((username.equals("")) ? ((last_name.equalsIgnoreCase("x")) ? "X" : "Guest") : last_name) + " (" + player_count + ")<nobr>");


                                } else {

                                    // EMPTY TIME SLOT

                                    out.print("<td class=\"timesheetTD2\" id=\"btnSlot_" + rs.getInt("sheet_id") + "\" " + onclick + " " + td_style + " nowrap>");

                                    if ( rs.getString("in_use_by").equals("") && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 &&
                                         rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0) {

                                        // NOT IN USE
                                        if (!oldSheets) out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                        if (club.equalsIgnoreCase("hillcrestid")) {
                                            out.print(" Available");
                                        } else {
                                            out.print("&nbsp;");
                                        }


                                    } else {

                                        // IN USE OR TAKEN BY LESSON
                                        if (rs.getInt("lesson_id") != 0) {

                                            String result = verifyLesson.getLessonInfo(rs.getInt("lesson_id"), con);
                                            StringTokenizer tok = new StringTokenizer( result, "|" );

                                            String lname = tok.nextToken();       // get the name of this lesson
                                            String lcolor = tok.nextToken();      // get the color for this lesson

                                            out.println("<span title=\"" + lname + "\"><center><i>" + ((rs.getInt("lesson_id") > 0) ? "Lesson" : "Clinic") + "</i></center></span>");

                                        } else if (rs.getInt("event_id") > 0) {

                                            out.println("<center><i>Event</i></center>");
                                            //out.println("<center><i>" + rs.getString("name") + "</i></center>");

                                        } else if (rs.getInt("blocker_id") > 0) {

                                            out.println("<center><i><font color=\"white\">Blocked</font></i></center>");

                                        } else {

                                            out.println("&nbsp;<i>in use</i>&nbsp;");

                                        }
                                    }

                                }

                            } catch (Exception exc) {
                                out.println("<p>ERROR LOADING PLAYERS:" + exc.toString() + "</p>");
                            } finally {
                                Connect.close(rs2, pstmt2);
                            }

                            // end player time slot td/tr
                            out.println("</td>");//</tr>

                        } // end while loop for all the time slots

                    } catch (Exception exc) {
                        out.println("<p>ERROR:" + exc.toString() + "</p>");
                    } finally {
                        Connect.close(rs, pstmt);
                    }

                    // end the main tables
                    out.println("<tr></table>");
                    out.println("</td><tr></table><br>");
                    
                    col_count += cols_per_row.get(i);
                }

                //buildRestHolder(restArray, date, con, out);

            }


        } // end if group_ip set
        
    } else {
        out.println("<div style=\"text-align:center;\">No time sheets found for this activity.</div>");
    }
    
    // debug
    //out.println("<!-- group_id=" + group_id + ", activity_id=" + activity_id + " -->");

    out.println("<br></body></html>");

    out.close();
 }

/*
 private void buildRestHolder(ArrayList<Integer> restArray, int date, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    ArrayList<Integer> newRestArray = new ArrayList<Integer>();

    HashSet<Integer> h = new HashSet<Integer>(restArray);
    restArray.clear();
    restArray.addAll(h);

    newRestArray = restArray;
                
    String html = "";
    
    for (int i = 0; i < newRestArray.size(); i++) {
    
        try {

            pstmt = con.prepareStatement("" +
                        "SELECT name, color " +
                        "FROM restriction2 " +
                        "WHERE id = ? AND showit = 'Yes'");

            pstmt.clearParameters();
            pstmt.setInt(1, newRestArray.get(i));
            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                html += "&nbsp;<a href='javascript:void(0)' onClick='window.open(&quot;Proshop_gensheets?rest=" + newRestArray.get(i) + "&amp;date=" + date + "&quot;, &quot;newwindow&quot;, &quot;height=380, width=550, toolbar=no, menubar=no, scrollbars=yes, resizable=yes, location=no directories=no, status=no&quot;);return false;'>" +
                        "<button style='background-color:" + rs.getString(2) + "'>" + rs.getString(1) + "</button></a>&nbsp;";

            } else {

                out.println("<!-- ERROR: Cannot find rest_id " + newRestArray.get(i) + " (this shouldn't occur - means a rest was deleted but not removed from the time sheets!) -->");

            }

        } catch (Exception exc) {

            Utilities.logError("<p>ERROR BUILDING RESTRICTION BUTTONS:" + exc.toString() + "</p>");

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }
        
    }
    
    if (newRestArray.size() > 0) {
        
        out.println("<script type=\"text/javascript\">");
        out.println("document.getElementById('restHolder').innerHTML = \"<b>Today's Restrictions:</b><br>" + html + "<br>\";");
        out.println("</script>");
        
    }
    
 }
  */

 // *********************************************************
 //  Display event information in new pop-up window
 // *********************************************************

 private void displayEvent(String name, PrintWriter out, Connection con) {

   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;

   int year = 0;
   int month = 0;
   int day = 0;
   int act_hr = 0;
   int act_min = 0;
   int signUp = 0;
   int type = 0;
   int holes = 0;
   int max = 0;
   int size = 0;
   int guests = 0;
   int teams = 0;
   int c_month = 0;
   int c_day = 0;
   int c_year = 0;
   int c_time = 0;
   int c_hr = 0;
   int c_min = 0;

   String locations = "";
   String location_names = "";
   String format = "";
   String pairings = "";
   String memcost = "";
   String gstcost = "";
   String itin = "";
   String c_ampm = "";
   String act_ampm = "";
   String fb = "";

   //
   //  Locate the event and display the content
   //
   try {


      pstmt = con.prepareStatement ("SELECT * FROM events2b WHERE name = ?");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);
      rs = pstmt.executeQuery();      // execute the prepared pstmt

      if (rs.next()) {

         year = rs.getInt("year");
         month = rs.getInt("month");
         day = rs.getInt("day");
         type = rs.getInt("type");
         act_hr = rs.getInt("act_hr");
         act_min = rs.getInt("act_min");
         locations = rs.getString("locations");
         signUp = rs.getInt("signUp");
         format = rs.getString("format");
         pairings = rs.getString("pairings");
         size = rs.getInt("size");
         max = rs.getInt("max");
         guests = rs.getInt("guests");
         memcost = rs.getString("memcost");
         gstcost = rs.getString("gstcost");
         c_month = rs.getInt("c_month");
         c_day = rs.getInt("c_day");
         c_year = rs.getInt("c_year");
         c_time = rs.getInt("c_time");
         itin = rs.getString("itin");

      } else {           // name not found - try filtering it

         name = SystemUtils.filter(name);

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, name);
         rs = pstmt.executeQuery();      // execute the prepared pstmt

         if (rs.next()) {

            year = rs.getInt("year");
            month = rs.getInt("month");
            day = rs.getInt("day");
            type = rs.getInt("type");
            act_hr = rs.getInt("act_hr");
            act_min = rs.getInt("act_min");
            locations = rs.getString("locations");
            signUp = rs.getInt("signUp");
            format = rs.getString("format");
            pairings = rs.getString("pairings");
            size = rs.getInt("size");
            max = rs.getInt("max");
            guests = rs.getInt("guests");
            memcost = rs.getString("memcost");
            gstcost = rs.getString("gstcost");
            c_month = rs.getInt("c_month");
            c_day = rs.getInt("c_day");
            c_year = rs.getInt("c_year");
            c_time = rs.getInt("c_time");
            itin = rs.getString("itin");
         }
      }
      pstmt.close();


      // Get location names
      try {
          if (!locations.equals("")) {

              stmt = con.createStatement();
              rs = stmt.executeQuery("SELECT activity_name FROM activities WHERE activity_id IN (" + locations + ")");

              while (rs.next()) {

                  if (!location_names.equals("")) location_names += ", ";
                  location_names += rs.getString("activity_name");
              }

              stmt.close();
          }
      } catch (Exception exc) {
          location_names = "Location names not found.";
      }

      //
      //  Create time values
      //
      act_ampm = "AM";

      if (act_hr == 0) {

         act_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (act_hr == 12) {

            act_ampm = "PM";         // change to Noon
         }
      }
      if (act_hr > 12) {

         act_hr = act_hr - 12;
         act_ampm = "PM";             // change to 12 hr clock
      }

      c_hr = c_time / 100;
      c_min = c_time - (c_hr * 100);

      c_ampm = "AM";

      if (c_hr == 0) {

         c_hr = 12;                 // change to 12 AM (midnight)

      } else {

         if (c_hr == 12) {

            c_ampm = "PM";         // change to Noon
         }
      }
      if (c_hr > 12) {

         c_hr = c_hr - 12;
         c_ampm = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Event Information"));
      
      out.println("<style>");
      out.println("table.main {");
      out.println("  border:0px");
      out.println("  vertical-align:top;");
      out.println("}");
      out.println("td {");
      out.println("  text-align:left;");
      out.println("  font-size:14px;");
      out.println("  padding:5px 10px 10px 5px;");
      out.println("}");
      out.println("td.hdr { ");
      out.println("  text-align:center;");
      out.println("  font-size:16px;");
      out.println("}");
      out.println("</style>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table class=\"main\">");       // table for main page

      out.println("<tr><td colspan=\"3\" class=\"hdr\">");
      out.println("<font size=\"3\">");
      out.println("Event: <b>" + name + "</b>");
      out.println("</font></td></tr>");

      out.println("<tr><td>");
      out.println("<b>Date:</b> " + month + "/" + day + "/" + year);
      out.println("</td><td>");
      out.println("<b>Time:</b> " + act_hr + ":" + (act_min < 10 ? "0" : "") + act_min + " " + act_ampm);
      out.println("</td></tr>");

      out.println("<tr><td colspan=\"3\">");
      out.println("<b>Locations:</b> " + location_names);
      out.println("</td></tr>");

      if (signUp != 0) {       // if members can sign up

         out.println("<tr><td colspan=\"3\">");
         out.println("<b>Format:</b> " + format);
         out.println("</td></tr>");

         out.println("<tr><td>");
         out.println("<b>Pairings by:</b> " + pairings);
         out.println("</td><td>");
         out.println("<b># of Teams:</b> " + max);
         out.println("</td><td>");
         out.println("<b>Team Size:</b> " + size);
         out.println("</td></tr>");

         out.println("<tr><td>");
         out.println("<b>Guests per Member:</b> " + guests);
         out.println("</td><td>");
         out.println("<b>Cost per Guest:</b> " + gstcost);
         out.println("</td><td>");
         out.println("<b>Cost per Member:</b> " + memcost);
         out.println("</td></tr>");

         out.println("<tr><td colspan=\"3\">");
         out.println("<b>Must Sign Up By:</b> " + c_hr + ":" + (c_min < 10 ? "0" : "") + c_min + " " + c_ampm +  " on " + c_month + "/" + c_day + "/" + c_year);
         out.println("</td></tr>");

         out.println("<tr><td colspan=\"3\">");
         out.println("<b>Itinerary:</b> " + itin);
         out.println("</td></tr>");

      } else {

         out.println("<tr><td colspan=\"3\">");
         out.println("<b>Itinerary:</b> " + itin);
         out.println("</td></tr>");

         out.println("<tr><td colspan=\"3\" style=\"align:center\">");
         out.println("Online sign up was not selected for this event.");
         out.println("</td></tr>");
      }

      //
      //  End of HTML page
      //
      out.println("</table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"Proshop_events\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Event\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   }
   catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact ForeTees support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   }
 }


 // *********************************************************
 //  Display restriction information in new pop-up window
 // *********************************************************

 private void displayRest(int id, boolean create_suspension, int date, PrintWriter out, Connection con) {

   Statement stmt = null;
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   int year1 = 0;
   int month1 = 0;
   int day1 = 0;
   int year2 = 0;
   int month2 = 0;
   int day2 = 0;
   int hr1 = 0;
   int min1 = 0;
   int hr2 = 0;
   int min2 = 0;
   int suspend_count = 0;
   int count = 0;

   String locations = "";
   String location_names = "";
   String courseName = "";
   String recurr = "";
   String ampm1 = "AM";
   String ampm2 = "AM";
   String name = "";

   boolean mtypeFound = false;
   boolean mshipFound = false;

   String [] mtype = new String [8];                     // member types
   String [] mship = new String [8];                     // membership types


   if (create_suspension) {

        //out.println("<br>id=" + id);
        //out.println("<br>date=" + date);

        //
        //   Build the html page
        //
        out.println(SystemUtils.HeadTitle("Create Full Day Restriction Suspension"));

        out.println("<style>");
        out.println("body {");
        out.println("  text-align:center;");
        out.println("  font-size:14px;");
        out.println("</style>");

        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<h3><b>Quick Suspend Restriction</b></h3>");

        try {
        
            pstmt = con.prepareStatement (
                    "SELECT courseName, locations " +
                    "FROM restriction2 " +
                    "WHERE id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                
                courseName = rs.getString(1);
                locations = rs.getString(2);
            }

            pstmt = con.prepareStatement("" +
                    "INSERT INTO rest_suspend " +
                        "(mrest_id, courseName, locations, sdate, edate, " +
                        "stime, etime, eo_week, sunday, monday, tuesday, wednesday, thursday, friday, saturday) " +
                    "VALUES (?,?,?,?,?,0,2359,0,1,1,1,1,1,1,1)");

            pstmt.clearParameters();
            pstmt.setInt(1, id);
            pstmt.setString(2, courseName);
            pstmt.setString(3, locations);
            pstmt.setInt(4, date);          // date being viewed on time sheet
            pstmt.setInt(5, date);          // this suspension is just for one day
            count = pstmt.executeUpdate();

            if (count > 0) {

                // clear the restriction
                /*
                pstmt = con.prepareStatement("" +
                        "UPDATE activity_sheets " +
                        "SET rest_id = 0 " +
                        "WHERE rest_id = ? AND DATE(date_time) = ? AND activity_id IN (" + locations + ")");

                pstmt.clearParameters();
                pstmt.setInt(1, id);
                pstmt.setString(2, Utilities.getDateFromYYYYMMDD(date, 1)); // get date in to yyyy-mm-dd format
                count = pstmt.executeUpdate();
*/
                //if (count > 0) {

                // success
                out.println("<p>Restriction was suspended and removed from the time sheets for " + Utilities.getDateFromYYYYMMDD(date, 2) + ".</p>");
                out.println("<p>Please refresh the time sheet to see the changes.</p>");

                //} else {

                    // failed
                    //out.println("<p>Restriction was suspended but visually it may NOT of been removed from time sheets for " + Utilities.getDateFromYYYYMMDD(date, 2) + ".</p>");

                //}

            } else {

                // failed
                out.println("<p>Something went wrong and the restriction was NOT suspended and was NOT removed from the time sheets for " + Utilities.getDateFromYYYYMMDD(date, 2) + ".</p>");

            }

        } catch (Exception exc) {

            out.println("<p>Error creating full day suspension: " + exc.toString() + "</p>");
            out.println("<p>Something went wrong and the restriction was NOT suspended and was NOT removed from the time sheets for " + Utilities.getDateFromYYYYMMDD(date, 2) + ".</p>");
            
        } finally {
            
            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }


        out.println("<form>");
        out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
        out.println("</form>");

        out.println("</center></body></html>");

        return;

   } // end if we are here to create a full day suspension


   //
   //  Locate the event and display the content
   //
   try {

      pstmt = con.prepareStatement (
         "SELECT *, (SELECT COUNT(id) FROM rest_suspend WHERE mrest_id = ?) AS suspend_count " +
         "FROM restriction2 " +
         "WHERE id = ?");

      pstmt.clearParameters();
      pstmt.setInt(1, id);
      pstmt.setInt(2, id);
      rs = pstmt.executeQuery();

      if (rs.next()) {

         name = rs.getString("name");
         month1 = rs.getInt("start_mm");
         day1 = rs.getInt("start_dd");
         year1 = rs.getInt("start_yy");
         hr1 = rs.getInt("start_hr");
         min1 = rs.getInt("start_min");
         month2 = rs.getInt("end_mm");
         day2 = rs.getInt("end_dd");
         year2 = rs.getInt("end_yy");
         hr2 = rs.getInt("end_hr");
         min2 = rs.getInt("end_min");
         recurr = rs.getString("recurr");
         mtype[0] = rs.getString("mem1");
         mtype[1] = rs.getString("mem2");
         mtype[2] = rs.getString("mem3");
         mtype[3] = rs.getString("mem4");
         mtype[4] = rs.getString("mem5");
         mtype[5] = rs.getString("mem6");
         mtype[6] = rs.getString("mem7");
         mtype[7] = rs.getString("mem8");
         mship[0] = rs.getString("mship1");
         mship[1] = rs.getString("mship2");
         mship[2] = rs.getString("mship3");
         mship[3] = rs.getString("mship4");
         mship[4] = rs.getString("mship5");
         mship[5] = rs.getString("mship6");
         mship[6] = rs.getString("mship7");
         mship[7] = rs.getString("mship8");
         locations = rs.getString("locations");
         suspend_count = rs.getInt("suspend_count");

      }
      pstmt.close();

      // Get location names
      try {
          if (!locations.equals("")) {

              stmt = con.createStatement();
              rs = stmt.executeQuery("SELECT activity_name FROM activities WHERE activity_id IN (" + locations + ")");

              while (rs.next()) {

                  if (!location_names.equals("")) location_names += ", ";
                  location_names += rs.getString("activity_name");
              }

              stmt.close();
          }
      } catch (Exception exc) {
          location_names = "Location names not found.";
      }

      //
      //  Create time values
      //
      if (hr1 == 0) {
         hr1 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr1 == 12) {
            ampm1 = "PM";         // change to Noon
         }
      }
      if (hr1 > 12) {
         hr1 = hr1 - 12;
         ampm1 = "PM";             // change to 12 hr clock
      }

      if (hr2 == 0) {
         hr2 = 12;                 // change to 12 AM (midnight)
      } else {
         if (hr2 == 12) {
            ampm2 = "PM";         // change to Noon
         }
      }
      if (hr2 > 12) {
         hr2 = hr2 - 12;
         ampm2 = "PM";             // change to 12 hr clock
      }

      //
      //   Build the html page
      //
      out.println(SystemUtils.HeadTitle("Proshop Restriction Information"));
      
      out.println("<style>");
      out.println("table.main {");
      out.println("  border:0px");
      out.println("  vertical-align:top;");
      out.println("}");
      out.println("td {");
      out.println("  text-align:left;");
      out.println("  font-size:14px;");
      out.println("  padding:5px 10px 10px 5px;");
      out.println("}");
      out.println("td.hdr { ");
      out.println("  text-align:center;");
      out.println("  font-size:16px;");
      out.println("}");
      out.println("</style>");

      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<table border=\"0\" valign=\"top\" cellpadding=\"5\">");       // table for main page

      out.println("<tr><td align=\"center\" colspan=\"2\">");
      out.println("<font size=\"3\">");
      out.println("Restriction: <b>" + name + "</b>");
      out.println("</font></td></tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
      out.println("<b>Start Date of Restriction:</b> " + month1 + "/" + day1 + "/" + year1);
      out.println("</font></td><td align=\"left\"><font size=\"2\">");
      out.println("<b>Start Time:</b> " + hr1 + ":" + (min1 < 10 ? "0" : "") + min1 + " " + ampm1);
      out.println("</font></td></tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
      out.println("<b>End Date of Restriction:</b> " + month2 + "/" + day2 + "/" + year2);
      out.println("</font></td><td align=\"left\"><font size=\"2\">");
      out.println("<b>End Time:</b> " + hr2 + ":" + (min2 < 10 ? "0" : "") + min2 + " " + ampm2);
      out.println("</font></td></tr>");

      out.println("<tr><td align=\"left\" colspan=\"2\"><font size=\"2\">");
      out.println("<b>Recurrence:</b> " +recurr+ "");
      out.println("</font></td></tr>");

      out.println("<tr><td align=\"left\" colspan=\"2\"><font size=\"2\">");
      out.println("<b>Locations:</b> " + location_names);
      out.println("</font></td></tr>");

      // if any member types specified
      for (int i=0; i<8; i++) {
          if (!mtype[i].equals("")) {
              if (!mtypeFound) {
                  mtypeFound = true;
                  out.println("<tr><td align=\"left\" colspan=\"2\"><font size=\"2\">");
                  out.println("<b>Member Types Restricted:</b> " + mtype[i]);
              } else {
                  out.print(", " + mtype[i]);
              }
          }
      }
      if (mtypeFound) out.println("</font></td></tr>");

      // if any membership types specified
      for (int i=0; i<8; i++) {
          if (!mship[i].equals("")) {
              if (!mshipFound) {
                  mshipFound = true;
                  out.println("<tr><td align=\"left\" colspan=\"2\"><font size=\"2\">");
                  out.println("<b>Membership Types Restricted:</b> " + mship[i]);
              } else {
                  out.print(", " + mship[i]);
              }
          }
      }
      if (mshipFound) out.println("</font></td></tr>");


      out.println("<tr><td align=\"left\" colspan=\"2\"><font size=\"2\">");
      out.println("<b>Has Suspensions:</b> " + ((suspend_count == 0) ? "None" : "Yes (" + suspend_count + ")") + "");
      out.println("</font></td></tr>");

      //
      //  End of HTML page
      //
      out.println("</table><br>");
      out.println("<table border=\"0\" valign=\"top\">");       // table for main page
      out.println("<tr><td align=\"center\">");
      out.println("<form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</td><td>&nbsp;&nbsp;");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"post\" action=\"Proshop_mrest\">");
      out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
      out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
      out.println("<input type=\"hidden\" name=\"sheet\" value=\"yes\">");
      out.println("<input type=\"submit\" value=\"Edit Restriction\">");
      out.println("</form>");
      out.println("</td><td align=\"center\">");
      out.println("<form method=\"get\" action=\"Proshop_gensheets\">");
      out.println("<input type=\"hidden\" name=\"rest\" value=\"" + id + "\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
      out.println("<input type=\"hidden\" name=\"suspend\" value=\"\">");
      out.println("<input type=\"submit\" value=\"Suspend for Today\" onclick=\"return confirm('This will create a new suspension for this restriction that will disable it completely for today.\\n\\nAre you sure you want to continue?')\">");
      out.println("</form>");
      out.println("</td></tr></table>");
      out.println("</center></font></body></html>");
      out.close();

   } catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Error:" + exc.getMessage());
      out.println("<BR><BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact FlxRez support (provide this information).");
      out.println("<br><br><form>");
      out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
      out.println("</form>");
      out.println("</center></font></body></html>");
      out.close();
   
   } finally {

      try { rs.close(); }
      catch (Exception ignore) {}

      try { pstmt.close(); }
      catch (Exception ignore) {}

   }
   
 }

 // *********************************************************
 //  Check for a member restriction for the time selected
 // *********************************************************

 public static String getRestData(parmRest parmr, int time, int activity_id, int rest_id) {


    String color = "none";
    String name = "";
    String result = "";

    boolean suspend = false;

    int ind = 0;

    // First check the restriction that has its rest_id populated in this time slot.
    loop1:
    while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {           // loop over possible restrictions

        if (parmr.rest_id[ind] == rest_id && !parmr.color[ind].equals("") && !parmr.color[ind].equalsIgnoreCase("Default") &&
            parmr.showit[ind].equals("Yes") && parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

            // Check to make sure no suspensions apply
            suspend = false;

            for (int k=0; k<parmr.MAX; k++) {

                if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                    break;   // don't bother checking any more

                } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension

                    // check to see if this activity_id is in the locations csv for this suspension
                    StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                    while (tok.hasMoreTokens()) {

                        if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                            break loop1;

                        } // end if restriction applies to this activity_id (court level)
                        
                    } // end while loop of the locations csv
                }
            }

            if (!suspend) {

                // check to see if this activity_id is in the locations csv for this restriction
                StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                while (tok.hasMoreTokens()) {

                    if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                        // found a restriction that matches activity, date, time, day, mtype & mship of this member
                        color = parmr.color[ind];
                        name = parmr.restName[ind];
                        break loop1;

                    } // end if restriction applies to this activity_id (court level)

                } // end while loop of the locations csv

            } // end if not suspended

        } // end if color not blank/default and time matches

       ind++;

    } // end of while loop of all restrictions

    // If a color wasn't found above, look through the rest of the restrictions
    if (color.equals("none") && name.equals("")) {

        boolean searchColor = true;

        loop1:
        for (int i=0; i<2; i++) {

            if (i == 1) {
                searchColor = false;
            }

            ind = 0;

            loop2:
            while (ind < parmr.MAX && !parmr.restName[ind].equals("")) {           // loop over possible restrictions

                if ((!searchColor || (parmr.rest_id[ind] != rest_id && !parmr.color[ind].equalsIgnoreCase("Default"))) &&
                     !parmr.color[ind].equals("") && parmr.showit[ind].equals("Yes") &&
                      parmr.stime[ind] <= time && parmr.etime[ind] >= time) {      // matching time ?

                    // Check to make sure no suspensions apply
                    suspend = false;

                    loop3:
                    for (int k=0; k<parmr.MAX; k++) {

                        if (parmr.susp[ind][k][0] == 0 && parmr.susp[ind][k][1] == 0) {

                            break loop3;   // don't bother checking any more

                        } else if (parmr.susp[ind][k][0] <= time && parmr.susp[ind][k][1] >= time) {    // time falls within a suspension

                            // check to see if this activity_id is in the locations csv for this suspension
                            StringTokenizer tok = new StringTokenizer( parmr.susp_locations[ind][k][0], "," );

                            while (tok.hasMoreTokens()) {

                                if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                                    suspend = true;
                                    break loop3;     // don't bother checking any more

                                } // end if restriction applies to this activity_id (court level)

                            } // end while loop of the locations csv
                        }
                    }

                    if (!suspend) {

                        // check to see if this activity_id is in the locations csv for this restriction
                        StringTokenizer tok = new StringTokenizer( parmr.locations[ind], "," );

                        while (tok.hasMoreTokens()) {

                            if ( activity_id == Integer.parseInt(tok.nextToken()) ) {

                                // found a restriction that matches activity, date, time, day, mtype & mship of this member
                                if (!parmr.color[ind].equalsIgnoreCase("Default")) {
                                    color = parmr.color[ind];
                                } else {
                                    color = "none";
                                }
                                name = parmr.restName[ind];
                                break loop1;

                            } // end if restriction applies to this activity_id (court level)

                        } // end while loop of the locations csv

                    } // end if not suspended

                } // end if color not blank/default and time matches

               ind++;

            } // end of while loop of all restrictions
        }
    }

    if (!color.equals("none") || !name.equals("")) {
        result = color + ":" + name;
    }

    return result;
 }

}