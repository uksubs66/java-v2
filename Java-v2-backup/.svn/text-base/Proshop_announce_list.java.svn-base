
/***************************************************************************************
 *   Proshop_announce_list: This servlet will display a list of ForeTees Announcements
 *
 *
 *   called by:  Proshop_*** (most servlets as a return to Home)
 *               Proshop Tools Menu (doGet menu=yes)
 *               self (doPost from doGet)
 *               v_/ae.jsp (do Post)
 *
 *
 *   created: 4/03/2015   John K. (Refactored from Proshop_announce)
 *
 *   last updated:            ******* keep this accurate *******
 *
 *       04/03/15   Created
 *
 ***************************************************************************************
 */
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.VerifyUser;
import com.foretees.common.reqUtil;

import com.foretees.api.records.Invoice;
import com.foretees.api.records.Club;
import com.foretees.common.timeUtil;

import com.foretees.api.records.ForeTeesAnnouncement;
import com.foretees.api.clientReportTypes.*;

public class Proshop_announce_list extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Common_skin.setNoCacheHtml(resp);
        PrintWriter out = resp.getWriter();

        req.setAttribute(ProcessConstants.RQA_RWD, true); // Always uses RWD
        req.setAttribute(ProcessConstants.RQA_PROSHOP_HYBRID, true); // Always force Proshop Hybrid mode

        VerifyUser verify_user = VerifyUser.verifyPro(req);
        if (verify_user.session == null) {
            out.print(verify_user.htmlResponse);
            return;
        }
        
        String club = reqUtil.getSessionString(req, "club", "");
        //String user = reqUtil.getSessionString(req, "user", "");
        int activity_id = reqUtil.getSessionInteger(req, "activity_id", 0);
        
        Gson gson = new Gson();
        
        Common_skin.outputHeader(club, activity_id, "Announcements", true, out, req);
        Common_skin.outputBody(club, activity_id, out, req);
        Common_skin.outputPageStart(club, activity_id, out, req);
        
        StringBuilder dashboard_select = new StringBuilder();
        
        dashboard_select.append("<div class=\"mp_dashboard_selection ftd_hide\">"); // Triggers the dashboard scripts
        dashboard_select.append("</div>");
        
        out.println(dashboard_select);
        
        StringBuilder dashboard_single = new StringBuilder();
        
        dashboard_single.append(
                Common_skin.getElementContainer(
                "ftd_announcement_list ftd_report",
                null,
                "data-ftautoload=\""+reqUtil.getParameterString(req, "announcement_id", "") +"\" data-ftreport=\"" + StringEscapeUtils.escapeHtml(gson.toJson(new visibleForeTeesAnnouncements(true)))+ "\""));

        StringBuilder dashboard = new StringBuilder();
        dashboard.append(Common_skin.getElementContainer("ftd_single", dashboard_single.toString()));
        out.println(Common_skin.getElementContainer("ftd_container", dashboard.toString()));
        
        Common_skin.outputPageEnd(club, activity_id, out, req);
        
        out.close();

    }  // end of doGet

}
