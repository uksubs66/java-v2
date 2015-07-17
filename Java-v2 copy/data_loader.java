/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.lang.reflect.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json
import com.google.gson.reflect.*; // for json

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.nameLists;
import com.foretees.common.sendEmail;
import com.foretees.common.parmEmail;
import com.foretees.common.iCalendar;
import com.foretees.common.Connect;
import com.foretees.common.parmDining;
import com.foretees.common.getClub;

/**
 *
 * @author sindep
 */
public class data_loader extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        Connection con = null;
        boolean close_con = false;

        //
        //  Prevent caching 
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        try {

            HttpSession session = req.getSession(false);

            String user = "";
            String club = "";
            boolean json_mode = false;
            int activity_id = 0;

            Gson gson_obj = new Gson();
            Map<String, Map<String, Object>> partner_list = new LinkedHashMap<String, Map<String, Object>>();

            json_mode = ((req.getParameter("json_mode") != null) ? true : false);
            
            if (session == null) {

                user = (req.getParameter("user") != null) ? req.getParameter("user") : "";
                club = (req.getParameter("club") != null) ? req.getParameter("club") : "";

                if (club == null || club.equals("")) {

                    // likely an improper call
                    out.print("Error: Improper request.");
                    out.close();
                    return;

                }

                con = Connect.getCon(club);

                if (con == null) {

                    // likely an improper call
                    out.print("Error: No con available.");
                    out.close();
                    return;
                }

                close_con = true;

                if (!getClub.getClubName(con).equals(club)) {

                    // likely an improper call
                    out.print("Error: Wrong request.");
                    out.close();
                    return;
                }

                if(json_mode){
                    out.print("Error: Session timeout.");
                    out.close();
                    return;
                }

            } else {

                con = SystemUtils.getCon(session);
                user = (String) session.getAttribute("user");                 // get username
                club = (String) session.getAttribute("club");                 // get club name
                activity_id = (Integer) session.getAttribute("activity_id");     // and activity id for user

                if (!getClub.getClubName(con).equals(club)) {

                    // likely an improper call
                    out.print("Error: Database session failed.");
                    out.close();
                    return;
                }
            }


            if (user == null) {

                // likely an improper call
                out.print("Error: Improper request.");
                out.close();
                return;

            }

            int organization_id = Utilities.getOrganizationId(con);
/*
            boolean isMember = false;
            boolean isProshop = false;

            if (user.startsWith("proshop")) {

                isProshop = true;

            } else {

                isMember = true;

            }
*/
            if (req.getParameter("del_dist_list") != null) {
                
                nameLists.deleteMemberDistributionList((String) req.getParameter("del_dist_list"), session, con);
            
            } else if (req.getParameter("save_dist_list") != null && req.getParameter("names") != null) {

                String list_name = (String) req.getParameter("save_dist_list");
                String new_list_name = (String) req.getParameter("new_list_name");
                Type data_type = new TypeToken<List<String>>(){}.getType();
                List<String> names = gson_obj.fromJson(req.getParameter("names"), data_type);
                boolean new_list_mode = (req.getParameter("new_list") != null);
                Map<String, Object> result = nameLists.saveMemberDistributionList(list_name, new_list_name, new_list_mode, names, session, con);
                out.print(gson_obj.toJson(result)); // output json string

            } else if (req.getParameter("dist_list_names") != null) {

                List<String> lists = nameLists.getMemberDistributionLists(session, con);
                out.print(gson_obj.toJson(lists)); // output json string

            } else if (req.getParameter("dist_lists") != null) {

                int mode = 0;
                if (req.getParameter("email") != null) {
                    mode = 2;
                }
                Type data_type = new TypeToken<List<String>>(){}.getType();
                List<String> dist_lists = gson_obj.fromJson(req.getParameter("dist_lists"), data_type);
                
                partner_list = nameLists.getMemebersFromDistributionList(dist_lists, mode, session, con);  // Get map object of partner list
                out.print(gson_obj.toJson(partner_list)); // output json string


            } else if (req.getParameter("name_list") != null && req.getParameter("letter") != null) {

                String letter = req.getParameter("letter");
                if (!json_mode) {
                    out.println("<!-- LOADING: " + letter + " FOR activity_id " + activity_id + " -->");
                }
                int mode = 0;
                if(req.getParameter("email") != null){
                   mode = 2;
                }
                if (letter.equalsIgnoreCase("partners")) {
                    if (!json_mode) {
                        nameLists.displayPartnerList(user, activity_id, 0, con, out, false);
                    } else {
                        partner_list = nameLists.getPartnerList(user, activity_id, mode, con, out);  // Get map object of partner list
                        out.print(gson_obj.toJson(partner_list)); // output json string
                    }

                } else if (letter.equalsIgnoreCase("listall")) {
                    // implement later once we are using this on the pro side
                    //nameLists.displayNameList(letter, user, activity_id, 0, con, out, true);
                    if (!json_mode) {
                        nameLists.displayNameList("", club, activity_id, 0, con, out, true);
                    } else {
                        partner_list = nameLists.getNameList("", club, activity_id, mode, con, out);  // Get map object of partner list
                        out.print(gson_obj.toJson(partner_list)); // output json string
                    }
                } else if (letter.length() == 1) {
                    if (!json_mode) {
                        nameLists.displayNameList(letter, club, activity_id, 0, con, out, true);
                    } else {
                        partner_list = nameLists.getNameList(letter, club, activity_id, mode, con, out);  // Get map object of partner list
                        out.print(gson_obj.toJson(partner_list)); // output json string
                    }
                }

            } else if (req.getParameter("dining_times") != null) {
                if (!json_mode) {
                    out.println("<!-- LOADING: dining_times -->");
                    int event_id = (req.getParameter("event_id") != null) ? Integer.parseInt(req.getParameter("event_id")) : 0;
                    int date = (req.getParameter("date") != null) ? Integer.parseInt(req.getParameter("date")) : 0;
                    int location_id = (req.getParameter("location_id") != null) ? Integer.parseInt(req.getParameter("location_id")) : 0;
                    String times = parmDining.getLocationTimesFromDining(organization_id, location_id, date, event_id, "", "");
                    out.println("<select name=\"reservation[location_id]\" size=\"1\" onchange=\"update_times(this.options[this.selectedIndex].value)\">");
                    out.println(times);
                    out.println("</select>");

                }
            } else if (req.getParameter("name_list") != null) {
                if (!json_mode) {
                    out.println("<!-- LOADING: name_list -->");
                }
            } else if (req.getParameter("partner_list") != null) {
                if (!json_mode) {
                    out.println("<!-- LOADING: partner_list -->");
                }
            } else if (req.getParameter("memNumTOname") != null) {
                if (!json_mode) {
                    String memNum = req.getParameter("memNumTOname");
                    out.print(Utilities.getNameDataFromMemNum(memNum, con));
                }
            } else if (req.getParameter("content") != null) {

                int email_content_id = 0;
                parmEmail email = new parmEmail();

                // setup the email parm for our needs
                email.date = (req.getParameter("date") != null) ? Integer.parseInt(req.getParameter("date")) : 0;
                email.time = (req.getParameter("time") != null) ? Integer.parseInt(req.getParameter("time")) : 0;
                email.type = (req.getParameter("type") != null) ? req.getParameter("type") : "";

                email_content_id = sendEmail.getContentID(email, sendEmail.CONTENT_AREA_1, con);

                String content = sendEmail.getContent(email_content_id, con, club, user);
                if (!json_mode) {
                    out.print(content);
                }
            } else if (req.getParameter("ical") != null) {

                int id = (req.getParameter("id") != null) ? Integer.parseInt(req.getParameter("id")) : 0;

                String id_type = (req.getParameter("id_type") != null) ? req.getParameter("id_type") : "";

                // only continue if both requireds are here
                if (id > 0 && !id_type.equals("")) {

                    iCalendar iCal = new iCalendar();
                    iCal.id = id;
                    iCal.id_type = id_type;
                    iCal.club_name = club;

                    iCal.populateICS(con);

                    iCal.buildICS(con);

                    if ((req.getParameter("string") == null)) {
                        resp.setContentType("text/calendar;method=REQUEST");    // overide what we set earlier
                        resp.setHeader("Content-Disposition", "filename=\"foretees.ics\""); // include "attachment;" before filename?
                    }
                    if (iCal.ICS_FILE.equals("")) {
                        out.println("EMPTY");
                    }
                    out.print(iCal.ICS_FILE);
                    out.flush();

                }

            }

        } catch (Exception exc) {

            Utilities.logError("data_loader: Fatal Error. err=" + exc.toString() + " trace=" + Utilities.getStackTraceAsString(exc));

        } finally {

            out.close();

            // close the db connection if we opened it here
            if (close_con) {

                try { con.close(); }
                catch (Exception ignore) {}

            }

        }
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