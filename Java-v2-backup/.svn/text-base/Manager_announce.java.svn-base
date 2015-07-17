/******************************************************************************************
 *   Manager_announce:  This servlet will display club calendar for the manager portal
 *
 *   called by:  Login, menus, 
 *
 *   created: 05/12/2015   John K.
 *
 *   last updated:
 *
 *         
 *
 *
 *******************************************************************************************
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
import com.foretees.common.Labels;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;
import com.foretees.common.getActivity;
import com.foretees.common.reqUtil;

import com.foretees.api.ApiAccess;
import com.foretees.api.records.User;
import com.foretees.api.records.UserAccess;
import com.foretees.api.records.ForeTeesAnnouncement;
import com.foretees.api.clientReportTypes.*;


public class Manager_announce extends HttpServlet {

    //String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    
    //******************************************************
    //  doGet
    //******************************************************
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        Common_skin.setNoCache(resp);
        
        PrintWriter out = resp.getWriter();

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder
        
        // make sure they have MP access
        User ua = ApiAccess.getUser(req);
        if (!ua.access.view_foretees_announcements) session = null;
        if (session == null) {
            Common_skin.outputErrorPage(req, resp, ProcessConstants.ERROR_ACCESS, ProcessConstants.ERROR_ACCESS_TITLE, ProcessConstants.ERROR_ACCESS_MSG, out);
            return;
        }

        Connection con = Connect.getCon(req);            // get DB connection
        if (con == null) {
            Common_skin.outputErrorPage(req, resp, ProcessConstants.ERROR_DB, ProcessConstants.ERROR_DB_TITLE, ProcessConstants.ERROR_DB_MSG, out);
            return;
        }
        
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club
        
        Gson gson = new Gson();

        int activity_id = ProcessConstants.MANAGERS_PORTAL;
        
        
        //
        //   build the HTML page for the display
        //
        Common_skin.outputHeader(ua.club, activity_id, "Manager's Portal", true, out, req);
        Common_skin.outputBody(ua.club, activity_id, out, req);
        Common_skin.outputTopNav(req, ua.club, activity_id, out, con);
        Common_skin.outputBanner(ua.club, activity_id, clubName, "", out, req); 
        Common_skin.outputSubNav(ua.club, activity_id, out, con, req);
        Common_skin.outputPageStart(ua.club, activity_id, out, req);
        Common_skin.outputBreadCrumb(ua.club, activity_id, out, "Announcements", req);
        Common_skin.outputLogo(ua.club, activity_id, out, req);
        
        
        int minimum_date = timeUtil.getClubDate(req);
        int selected_date = minimum_date;
        
        if (req.getParameter("date") != null) {
            try {
                int requested_date = timeUtil.getClubDateTimeFromDb(req, reqUtil.getParameterString(req, "date", ""))[timeUtil.DATE];
                if (requested_date >= minimum_date) {
                    selected_date = requested_date;
                }
            } catch (Exception e) {
                // Bad or no time passed
            }
        }
        
        //String selected_sdate = timeUtil.getStringDateMMDDYYYY(selected_date);
        //String minimum_sdate = timeUtil.getStringDateMMDDYYYY(minimum_date);
        
        int selected_activity = reqUtil.getParameterInteger(req, "activity_id", ProcessConstants.ALL_ACTIVITIES);
        
        /*
        out.println("<div class=\"main_instructions\">"
                + "<p>"
                + "Manager's Portal"
                + "</p>"
                + "</div>");
         * 
         */
        
        StringBuilder dashboard_select = new StringBuilder();
        
        dashboard_select.append("<div class=\"mp_dashboard_selection ftd_hide\">");
        /*
        dashboard_select.append("<div class=\"rwdCourseSelect\">");
        dashboard_select.append("<label><b>Activity:</b>");
        getActivity.buildActivitySelect(1, selected_activity, "class=\"mp_selected_activity_id\"", true, con, dashboard_select);
        dashboard_select.append("</label>");
        dashboard_select.append("</div>");
        
        dashboard_select.append("<div class=\"rwdDateSelect\">");
        dashboard_select.append("<label><b>Report Date:</b><input type=\"text\" class=\"ft_date_picker_field\" data-ftstartdate=\"");
        dashboard_select.append( minimum_sdate);
        dashboard_select.append("\" data-ftdefaultdate=\"");
        dashboard_select.append(selected_sdate);
        dashboard_select.append("\"  value=\"");
        dashboard_select.append(selected_sdate);
        dashboard_select.append("\"></label>");
        dashboard_select.append("</div>");
        */
        dashboard_select.append("</div>");
        
        out.println(dashboard_select);
        
        StringBuilder dashboard_single = new StringBuilder();
        
        dashboard_single.append(
                Common_skin.getElementContainer(
                "ftd_announcement_list ftd_report",
                null,
                "data-ftautoload=\""+reqUtil.getParameterString(req, "announcement_id", "") +"\" data-ftreport=\"" + StringEscapeUtils.escapeHtml(gson.toJson(new visibleForeTeesAnnouncements()))+ "\""));

        StringBuilder dashboard = new StringBuilder();
        dashboard.append(Common_skin.getElementContainer("ftd_single", dashboard_single.toString()));
        out.println(Common_skin.getElementContainer("ftd_container", dashboard.toString()));
        
        Common_skin.outputPageEnd(ua.club, activity_id, out, req);

        out.close();

    }   // end of doGet

   
    //******************************************************************************
    //  doPost 
    //******************************************************************************
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);          // call doGet processing

    }   // end of doPost
    
    

}
