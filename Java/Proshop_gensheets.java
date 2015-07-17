/*******************************************************************************
 *
 *   Proshop_gensheets:  This servlet will display the time sheets for an activity
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
 *
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


public class Proshop_gensheets extends HttpServlet {
    
 String rev = SystemUtils.REVLEVEL;
    
 String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

 
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

    Connection con = SystemUtils.getCon(session);

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
    String num = "";
    String allowable_views = "";

    boolean allow_tabbed = false;
    boolean allow_summary = false;
    boolean allow_detail = false;
    boolean today = false;          // set to true if viewing todays time sheet
    boolean checkinAccess = SystemUtils.verifyProAccess(req, "TS_CHECKIN", con, out);
    boolean oldSheets = (req.getParameter("old") != null) ? true : false;   // flag for determining if we are viwing old time sheets

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

        dayShort_name = dayShort_table[cal.get(Calendar.DAY_OF_WEEK)];   // get short name for day
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

        dayShort_name = dayShort_table[cal.get(Calendar.DAY_OF_WEEK)];   // get short name for day

    }

    if (date == Utilities.getDate(con)) today = true;
    
    //debug
    out.println("<!-- date=" + date + " today=" + today + " -->");

    out.println(SystemUtils.HeadTitle2("Proshop Select Date Page"));

    // include files for dynamic calendars
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");

    // include files for tabber
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/tabber.css\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/tabber.js\"></script>");

    out.println("<style>");
    out.println("body { text-align: center; }");
    out.println("</style>");

    out.println("<script type=\"text/javascript\">");
    out.println("function gotoTimeSlot(slot_id, group_id, date, layout, consec) {");
    out.println("//top.document.location.href=\"/" + rev + "/servlet/Proshop_activity_slot?slot_id=\"+slot_id+\"&group_id=\"+group_id+\"&date=\"+date+\"&layout_mode=\"+layout;");
    out.println("  top.document.location.href=\"/" + rev + "/servlet/Proshop_activity_slot?slot_id=\"+slot_id+\"&group_id=\"+group_id+\"&date=\"+date+\"&layout_mode=\"+layout+\"&consec=\"+consec;");
    out.println("}");
    out.println("</script>");
    
    out.println("</head><body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page


    //
    // START THE FORM
    //

    // this is the form that gets submitted when the user selects a day from the calendar
    out.println("<form action=\"/" +rev+ "/servlet/Proshop_gensheets\" method=\"get\" name=\"frmLoadDay\">");

    out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
    out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");

    out.println("<input type=\"hidden\" name=\"last_tab\" value=\"" + last_tab + "\">");
    out.println("<input type=\"hidden\" name=\"last_tab_index\" value=\"" + last_tab_index + "\">");

    out.println("<input type=\"hidden\" name=\"layout_mode\" value=\"" + layout_mode + "\">");

    if (oldSheets) out.println("<input type=\"hidden\" name=\"old\" value=\"\"><input type=\"hidden\" name=\"chkallin\" value=\"0\">");



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
    // Display Full Activity Tree - do this first regardless
    //
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
                        "DATE_FORMAT(t2.date_time, '%Y%m%d') = ? AND " +
                        "activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?)");

            pstmt.clearParameters();
            pstmt.setInt(1, date);
            pstmt.setInt(2, group_id);
            pstmt.executeUpdate();

        } catch (Exception exc) {

            Utilities.logError("Error in Proshop_gensheets updating show values for " + date + ". Exc=" + exc.getMessage() );

        } finally {

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }

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
                "SELECT allowable_views " +
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

     if (allow_tabbed) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='1';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Tabbed View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(1)\" class=\"ctrlPanelLink\" title=\"Switch to Tabbed View\">");
        out.println("Tabbed View</a></td></tr>");
     }

     if (allow_summary && !club.equals("quecheeclubtennis")) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='2';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(2)\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("Summary View</a></td></tr>");
     }

     if ((Common_Server.SERVER_ID == 4 || club.equals("quecheeclubtennis")) && allow_summary) {
        out.println("<tr><td align=\"center\">");
        //out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].layout_mode.value='2';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Switch to Summary View\">");
        out.println("<a href=\"#\" onclick=\"reloadPage(4)\" class=\"ctrlPanelLink\" title=\"Switch to New Summary View\">");
        out.println("New Summary View</a></td></tr>");
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
        out.println("<a href=\"/" +rev+ "/servlet/Proshop_activity_sendemail?group_id=" +group_id+ "&date=" + date + "\" target=\"bot\" class=\"ctrlPanelLink\" title=\"Send Email To All Members On Selected Sheet\" alt=\"Send Email\">");
        out.println("Send Email to Members</a></td></tr>");
     }
        
        // member lookup
        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"javascript:void(0)\" onclick=\"memberLookup(); return false;\" title=\"Member Look-Up\" class=\"ctrlPanelLink\">");
        out.println("Member Look-Up</a>");       
        out.println("</font></td></tr>");
        
        // print sheets
        out.println("<tr><td align=\"center\">");
        out.println("<a href=\"/" +rev+ "/servlet/Proshop_activity_print_sheet?date=" +date+ "&group_id=" +group_id+ "\" target=\"_blank\" class=\"ctrlPanelLink\" title=\"Print Sheets\" alt=\"Print Sheets\">");
        out.println("Print Sheets</a>");       
        out.println("</font></td></tr>");
        
        // print notes - hide if old sheets
        if (!oldSheets) {
            out.println("<tr><td align=\"center\">");
            out.println("<a href=\"/" +rev+ "/servlet/Proshop_activity_print_sheet?date=" +date+ "&group_id=" +group_id+ "&prtoption=notes\" target=\"_blank\" class=\"ctrlPanelLink\" title=\"Print Notes\" alt=\"Print Notes\">");
            out.println("Print Notes</a>");
            out.println("</font></td></tr>");
        }

        if (checkinAccess && oldSheets) {
            // if old sheets then show check in all link
            out.println("<tr><td align=\"center\">");
            out.println("<a href=\"javascript: void(0)\" onclick=\"document.forms['frmLoadDay'].chkallin.value='1';document.forms['frmLoadDay'].submit()\" class=\"ctrlPanelLink\" title=\"Check All In\">");
            out.println("Check All In</a>");
            out.println("</font></td></tr>");
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

    out.println("function openHistoryWindow(slot_id) {");
    out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_activity_history?slot_id=' +slot_id+ '&history=yes','historyPopup','width=800,height=455,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
    out.println(" w.creator = self;");
    out.println("}");

    out.println("function openNotesWindow(slot_id) {");
    out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_activity_history?slot_id=' +slot_id+ '&notes=yes','notesPopup','width=640,height=360,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');");
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
    out.println("  doc.location.href='/"+rev+"/servlet/Proshop_sheet_checkin?psid='+player_slot_uid+'&imgId='+imgId;");
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
    out.println("  doc.location.href='/"+rev+"/servlet/Proshop_sheet_checkin?all&psid='+sheet_id;");
    out.println(" }");
    out.println("}");

    out.println("function memberLookup() {");
    out.println(" var y = prompt('Enter the member number you would like to lookup.', '');");
    out.println(" if (y==null) return;");
    out.println(" y=y.replace(/^\\s+|\\s+$/g, '');"); // trim leading & trailing
    out.println(" if (y == '') return;");
    //out.println(" var ex = /^[0-9]{1,10}$/;"); // regex to enforce numeric only and 1-10 digits
    //out.println(" if (!ex.test(y)) { alert('Enter numeric characters only.'); return; }");
    out.println(" w = window.open ('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','width=480,height=200,scrollbars=1,dependent=0,directories=0,location=0,menubar=0,resizable=1,status=0,toolbar=0');"); // add modal=yes to non-ie browsers
    out.println(" w.creator = self;");
    //out.println(" window.showModalDialog('/" +rev+ "/servlet/Proshop_member_lookup?mem_num='+y,'memberLookupPopup','status:no;dialogWidth:620px;dialogHeight:280px;resizable:yes;center:yes;help=no');");
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
    }
    out.println("</script>");

    out.println("<script language=\"javascript\">\ndoCalendar('0');\n</script>");
    if (!oldSheets) out.println("<script language=\"javascript\">\ndoCalendar('1');\n</script>");


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
                        ">" + // onclick=\"window.open('/"+rev+"/servlet/Member_gensheets?event=" + rs.getString("name") + "', 'newwindow', 'height=430, width=550, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no');return false;\"
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
                            "e.name, e.color, r.color AS rcolor, r.name AS rname, " +
                            "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                        "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                        "WHERE " +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "ORDER BY sort_by, activity_name, date_time");
                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
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
                String lname = "";
                String consec_csv = "";

                ArrayList<Integer> consec = new ArrayList<Integer>();

                while (rs.next()) {

                    emptySlot = true;
                    color = "";
                    lname = "";

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
                        if (rs.getString("in_use_by").equals("")) { //  && rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0
                            
                            out.println("<tr class=\"timesheetTR\"><td align=center><button class=\"btnSlot\" " +
                                        "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," +
                                        "" + layout_mode + "," + (consec.size() > 0 ? consec.get(0) : 0) + ")\">" + rs.getString("time") + "</button></td>");
                        } else {
                            
                            out.println("<tr class=\"timesheetTR\"><td class=\"timesheetTDtime\" " +
                                        "align=\"center\">" + rs.getString("time") + "</td>");
                            
                        }

                        // optional consecutive selection column
                        if (consec.size() > 0) {

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
                            result = getLessonName(rs.getInt("lesson_id"), con);

                            StringTokenizer tok = new StringTokenizer( result, "|" );

                            lname = tok.nextToken();      // first token is the name
                            color = tok.nextToken();      // second is the color

                        } else if (rs.getInt("rest_id") != 0) {

                            out.println("<!-- RESTRICTION FOUND -->");
                            color = rs.getString("rcolor");
                            restArray.add(rs.getInt("rest_id"));

                        } else {

                            out.println("<!-- NO EVENT/LESSON/RESTRICTION -->");

                        }

                            try {

                                pstmt2 = con.prepareStatement("" +
                                        "SELECT * " +
                                        "FROM activity_sheets_players " +
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
                                                  "&nbsp;" + rs2.getString("player_name") + "</td>");

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

                                    result = getLessonName(rs.getInt("lesson_id"), con);

                                    StringTokenizer tok = new StringTokenizer( result, "|" );

                                    lname = tok.nextToken();       // get the name of this lesson
                                    String lcolor = tok.nextToken();      // get the color for this lesson

                                    out.println("<td colspan='" + rs.getInt("max_players") + "' align=center class=timesheetTD nowrap " +
                                                "style=\"background-color:" + lcolor + "\"><i>" + lname + "</i></td>");

                                    players_found = rs.getInt("max_players");

                                } else if (rs.getString("rname") != null) {

                                    // if no players are here and there is a restriction covering this time - display its name
                                    out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                                "" + color + "\"><i>" + rs.getString("rname") + "</i></td>");

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

                                result = getLessonName(rs.getInt("lesson_id"), con);

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

            buildRestHolder(restArray, con, out);


        } else if ( layout_mode == 2 ) {


            //
            // SUMMARY VIEW
            //

            // this array will hold the activities and the order in which they are displayed
            ArrayList<Integer> order = new ArrayList<Integer>();

            out.println("<table align=\"center\" border=\"0\">");
            out.println("<tr valign=\"top\"><td>");

            try {

                pstmt = con.prepareStatement("" +
                        "SELECT t2.activity_name, t2.activity_id " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "WHERE " +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "GROUP BY t2.activity_name " +
                        "ORDER BY t2.sort_by, t2.activity_name");

                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
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
                        "SELECT t1.*, t2.activity_name, t2.max_players, e.name, e.color, r.color AS rcolor, " +
                            "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                        "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                        "WHERE " +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "ORDER BY date_time, sort_by, activity_name");
                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                String last_time = "";
                String onclick = "";
                String td_style = "";
                int i = 0;
                int order_pos = 0;

                while ( rs.next() ) {

                    // see if we are switching times (new col)
                    if ( !last_time.equals(rs.getString("time")) ) {

                        last_time = rs.getString("time");
                        out.println("</table></td><td>");           // end current column and start new one
                        out.println("<table class=\"timeSheet\">");     // start new table inside this column
                        out.println("<tr><td class=\"headerTD\" nowrap align=\"center\"><b>" + last_time + "</b></td></tr>");
                        i = 0;
                        order_pos = 0;
                    }

                    // if not is use and not blocked & not a lesson & not an event
                    if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0 && 
                        rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("rest_id") == 0) {

                        onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + ")\"";
                        td_style = "style=\"background: " + ((oldSheets) ? "white" : "#E9F4BB") + "; cursor:pointer\"";

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

                        // time is covered by a restriction
                        onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + ")\"";
                        td_style = "style=\"background: " + rs.getString("rcolor") + ";\"";
                        
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

                                out.print("<td class=\"timesheetTD2\" align=\"left\" nowrap ");

                                // if there is room to join and existing players have not said no to joining then color as normal
                                if (rs.getInt("disallow_joins") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {

                                    // time slot is not full and others can join
                                    out.print(onclick + " " + td_style);

                                } else {

                                    // time slot is full - make clickable but change bgcolor in indicate it's full
                                    out.print(onclick + " " + "style=\"background: white;\"");

                                }
                                out.print(">");

                                // if time slot can be joined display the green bar
                                if (!oldSheets && rs.getInt("disallow_joins") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {
                                    
                                    out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                    
                                }
                                
                                out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ")<nobr>");

                            } else {

                                // EMPTY TIME SLOT

                                out.print("<td class=\"timesheetTD2\" " + onclick + " " + td_style + " nowrap>");

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

            buildRestHolder(restArray, con, out);

        } else if (layout_mode == 3) {


            // LAYOUT MODE = 3 - Detailed View
            

            // FIND OUT THE MAX # OF PLAYER POSITIONS & CONSECUTIVE TIMES FOR GROUP OF ACTIVITIES
            int max_players = 0;
            int max_consec_pro = 0;

            String consec_csv = "";

            ArrayList<Integer> consec = new ArrayList<Integer>();

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("" +
                        "SELECT MAX(max_players), MAX(consec_pro) " +
                        "FROM activities " +
                        "WHERE activity_id IN (SELECT activity_id FROM activities WHERE parent_id = " + group_id + ")");

                if (rs.next()) {
                    max_players = rs.getInt(1);
                    max_consec_pro = rs.getInt(2);
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
            if (max_consec_pro > 1) out.println("<td class=\"headerTD\">Min.</td>");
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
                            "t2.activity_name, t2.max_players, t2.consec_pro, t2.consec_pro_csv, t2.interval, t2.alt_interval, " +
                            "e.name, e.color, r.color AS rcolor, r.name AS rname, " +
                            "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                        "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                        "WHERE " +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "ORDER BY date_time, sort_by, activity_name");
                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                int players_found = 0;
                int interval = 0;
                boolean alt = true; // #5D8733
                boolean emptySlot = true;
                String last_time = "";

                while (rs.next()) {

                    emptySlot = true;   // reset

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
                            out.println("<tr class=\"timesheetTR\"><td align=\"center\"><button class=\"btnSlot\" " +
                                        "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," +
                                        "" + layout_mode + "," + (consec.size() > 0 ? consec.get(0) : 0) + ")\">" + rs.getString("time") + "</button></td>");

                            if (consec.size() > 0) {

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

                            } else if (max_consec_pro > 0) {

                                // other activities being displayed allow consecutive times but not this one
                                out.print("<td>&nbsp;</td>");

                            }

                        } else {
                        
                            // non-selectable time
                            out.println("<tr class=\"timesheetTR\"><td align=\"center\">" + rs.getString("time") + "</td>");

                            if (consec.size() > 0) out.println("<td>&nbsp;</td>");

                        }

                        out.print("<td class=\"" + ((alt) ? "timesheetTD3alt" : "timesheetTD3") + "\" nowrap>" +
                                  "" + rs.getString("activity_name") + "</td>");

                        // only look up the players is time slot is not for a lesson or an event
                        if (rs.getInt("event_id") == 0 && rs.getInt("lesson_id") == 0) {

                            if (rs.getInt("rest_id") != 0) {

                                color = rs.getString("rcolor");
                                restArray.add(rs.getInt("rest_id"));

                            }

                            try {

                                pstmt2 = con.prepareStatement("" +
                                        "SELECT * " +
                                        "FROM activity_sheets_players " +
                                        "WHERE activity_sheet_id = ? " +
                                        "ORDER BY pos");
                                pstmt2.clearParameters();
                                pstmt2.setInt(1, rs.getInt("sheet_id"));
                                rs2 = pstmt2.executeQuery();

                                while ( rs2.next() ) {

                                    players_found++;
                                    emptySlot = false;
                                    
                                    out.print("<td class=\"timesheetTD\" nowrap ");
                                    out.print(((rs.getInt("rest_id") != 0) ? "style=\"background-color:" + color + "\"" : ""));
                                    
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
                                                  "&nbsp;" + rs2.getString("player_name") + "</td>");

                                    }

                                } // end player loop

                                pstmt2.close();

                            } catch (Exception exc) {

                                out.println("<p>ERROR LOADING PLAYER:" + exc.toString() + "</p>");

                            }

                            // if no players are here and there is a restriction covering this time - display its name
                            if (emptySlot && rs.getString("rname") != null) {

                                out.println("<td colspan=\"" + rs.getInt("max_players") + "\" style=\"text-align:center;background-color:" +
                                            "" + color + "\"><i>" + rs.getString("rname") + "</i></td>");
                                
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

                                String result = getLessonName(rs.getInt("lesson_id"), con);
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
                            out.print((rs.getInt("rest_id") != 0) ? "style=\"background-color:" + color + "\">" : ">");
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

            buildRestHolder(restArray, con, out);

            // end of layout mode 3

        } else {
            
            
            //
            // LAYOUT MODE 4 - NEW SUMMARY VIEW
            //

            // this array will hold the activities and the order in which they are displayed
            ArrayList<Integer> order = new ArrayList<Integer>();

            out.println("<script>");
            out.println("function hiLite(i) {");
            out.println(" document.getElementById('row'+i).className='timesheetTD2over';");
            //out.println(" window.status = 'row='+i;");
            out.println("}");
            out.println("function hiLiteOff(i) {");
            out.println(" document.getElementById('row'+i).className='timesheetTD2';");
            out.println("}");
            out.println("</script>");

            out.println("<table align=\"center\" border=\"1\">");
            out.println("<tr valign=\"top\"><td>");

            try {

                pstmt = con.prepareStatement("" +
                        "SELECT t2.activity_name, t2.activity_id " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "WHERE " +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "GROUP BY t2.activity_name " +
                        "ORDER BY t2.sort_by, t2.activity_name");

                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                out.println("<table class=\"timeSheet\">");
                out.println("<tr>");
                out.println("<td class=\"headerTD\" nowrap><b>Time</b></td>");

                int i = 0;

                while ( rs.next() ) {

                    if (club.equals("quecheeclubtennis") && rs.getInt("activity_id") == 8) {

                        // for now just just do nothing and skip this court

                    } else {

                        order.add(rs.getInt("activity_id"));

                        out.println("<td class=\"headerTD\" nowrap id=\"row" + i + "\" align=\"center\"><b>" + rs.getString(1) + "</b></td>"); // timesheetTD2
                        i++;

                    }
                }
                //out.println("</tr>");

            } catch (Exception exc) {


            }

            //out.println("</td><td>");
            //out.println("<table class=timeSheet>");

            try {

                pstmt = con.prepareStatement("" +
                        "SELECT t1.*, t2.activity_name, t2.max_players, e.name, e.color, r.color AS rcolor, " +
                            "DATE_FORMAT(date_time, \"%l:%i %p\") AS time, " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") AS date " +
                        "FROM activity_sheets t1 " +
                        "LEFT OUTER JOIN activities t2 ON t2.activity_id = t1.activity_id " +
                        "LEFT OUTER JOIN events2b e ON e.event_id = t1.event_id " +
                        "LEFT OUTER JOIN restriction2 r ON r.id = t1.rest_id " +
                        "WHERE " +
                            ((club.equals("quecheeclubtennis")) ? "t2.activity_id <> 8 AND " : "") +
                            "t1.activity_id IN (SELECT activity_id FROM activities WHERE parent_id = ?) AND " +
                            "DATE_FORMAT(date_time, \"%Y%m%d\") = ? " +
                        "ORDER BY date_time, sort_by, activity_name");
                pstmt.clearParameters();
                pstmt.setInt(1, group_id); // activity_id
                pstmt.setInt(2, date);
                rs = pstmt.executeQuery();

                String last_time = "";
                String onclick = "";
                String td_style = "";
                int i = 0;
                int order_pos = 0;

                while ( rs.next() ) {

                    // see if we are switching times (new row)
                    if ( !last_time.equals(rs.getString("time")) ) {

                        last_time = rs.getString("time");
                        //if (i != 0) out.println("</td></tr><tr><td>");      // end current row and start new one
                        //out.println("<table class=\"timeSheet\">");                 // start new table inside this column
                        out.println("</tr>");
                        out.println("<tr><td class=\"headerTD\" nowrap align=\"center\"><b>" + last_time + "</b></td>"); // </tr>
                        i = 0;
                        order_pos = 0;
                    }

                    // if not in use and not blocked & not a lesson & not an event
                    if (rs.getString("in_use_by").equals("") && rs.getInt("blocker_id") == 0 && rs.getInt("auto_blocked") == 0 && 
                        rs.getInt("lesson_id") == 0 && rs.getInt("event_id") == 0 && rs.getInt("rest_id") == 0) {

                        onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + ")\"";
                        td_style = "style=\"background: " + ((oldSheets) ? "white" : "#E9F4BB") + "; cursor:pointer\"";

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

                        // time is covered by a restriction
                        onclick = "onclick=\"gotoTimeSlot(" + rs.getInt("sheet_id") + "," + group_id + "," + date + "," + layout_mode + ")\"";
                        td_style = "style=\"background: " + rs.getString("rcolor") + ";\"";
                        
                        restArray.add(rs.getInt("rest_id"));

                    } else if (!rs.getString("in_use_by").equals("")) {

                        // time is in-use
                        onclick = "";
                        td_style = "style=\"background: yellow;\"";

                    }

                    // see if we've skipped over any courts
                    while (order.get(order_pos) != rs.getInt("activity_id")) {
                        //out.println("<tr class=\"timesheetTR\"><td class=\"timesheetTD2\">&nbsp;</td></tr>");
                        out.println("<td class=\"timesheetTD2\">&nbsp;</td>");
                        i++;
                        order_pos++;
                    }

//                    out.println("<tr onmouseover=\"hiLite(" + i + ")\" onmouseout=\"hiLiteOff(" + i + ")\">");
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

                                out.print("<td class=\"timesheetTD2\" align=\"left\" nowrap ");

                                // if there is room to join and existing players have not said no to joining then color as normal
                                if (rs.getInt("disallow_joins") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {

                                    // time slot is not full and others can join
                                    out.print(onclick + " " + td_style);

                                } else {

                                    // time slot is full - make clickable but change bgcolor in indicate it's full
                                    out.print(onclick + " " + "style=\"background: white;\"");

                                }
                                out.print(">");

                                // if time slot can be joined display the green bar
                                if (!oldSheets && rs.getInt("disallow_joins") == 0 && rs2.getInt(1) < rs.getInt("max_players")) {
                                    
                                    out.print("<span style=\"background-color:darkGreen\">&nbsp;</span>");
                                    
                                }
                                
                                out.print("<nobr>&nbsp;Member (" + rs2.getInt(1) + ")<nobr>");

                            } else {

                                // EMPTY TIME SLOT

                                out.print("<td class=\"timesheetTD2\" " + onclick + " " + td_style + " nowrap>");

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
                    out.println("</td>");//</tr>

                } // end while loop for all the time slots

                pstmt.close();

            } catch (Exception exc) {

                out.println("<p>ERROR:" + exc.toString() + "</p>");

            }

            // end the main tables
            out.println("<tr></table>");
            out.println("</td><tr></table>");

            buildRestHolder(restArray, con, out);
            
        }


    } // end if group_ip set
    
    
    // debug
    //out.println("<!-- group_id=" + group_id + ", activity_id=" + activity_id + " -->");

    out.println("<br></body></html>");

    out.close();
 }


 private void buildRestHolder(ArrayList<Integer> restArray, Connection con, PrintWriter out) {

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

                html += "&nbsp;<button style='background-color:" + rs.getString(2) + "'>" + rs.getString(1) + "</button>&nbsp;";

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


 private String getLessonName(int lesson_id, Connection con) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String result = "";

    try {

        // if negative it's a lesson group, if positive then it's a individual lesson
        if (lesson_id < 0) {

            // lesson group (go to lessongrp5 - bind lesson_id to one another and get lname)
            pstmt = con.prepareStatement("" +
                    "SELECT lname, color " +
                    "FROM lessongrp5 " +
                    "WHERE lesson_id = ?");
            lesson_id = Math.abs(lesson_id);
            
        } else {

            // individual lesson (go to lessonbook5 - bind lesson_id to recid and get ltype)
            pstmt = con.prepareStatement("" +
                    "SELECT ltype, color " +
                    "FROM lessonbook5 " +
                    "WHERE recid = ?");
        }

        pstmt.clearParameters();
        pstmt.setInt(1, lesson_id);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            result = rs.getString(1) + "|" + rs.getString(2);

        }

    } catch (Exception exc) {

        Utilities.logError("<p>ERROR GETTING LESSON NAME:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    return result;

 }

}