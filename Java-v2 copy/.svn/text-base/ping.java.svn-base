/*
 * Keeps user session alive, responding user name, club, server time, and last activity time
 * 
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

import com.foretees.common.Utilities;

/**
 *
 * @author sindep
 */
public class ping extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //
        //  Prevent caching 
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();

        Gson gson_obj = new Gson();
        Map<String, Object> result_map = new LinkedHashMap<String, Object>();
        String user = "";
        String club = "";
        java.util.Date serverTime = new java.util.Date();
        long timeStamp = serverTime.getTime();
        long lastActivity = 0;
        long thisActivity = 0;
        if (req.getParameter("last_activity") != null) {
            try {
                thisActivity = Long.parseLong(req.getParameter("last_activity"));
            } catch (NumberFormatException nfe) {
                thisActivity = 0;
            }
        }
        try {

            //Connection con = null;
            HttpSession session = req.getSession(false);

            if (session == null) {
                out.println("No session");
                //con = dbConn.Connect(club);
            } else {
                //con = SystemUtils.getCon(session);
                user = (String) session.getAttribute("user");                 // get username
                club = (String) session.getAttribute("club");                 // get club name
                if (session.getAttribute("lastActivityTimer") != null) {
                    lastActivity = (Long) session.getAttribute("lastActivityTimer");
                }
                if (thisActivity > lastActivity) {
                    session.setAttribute("lastActivityTimer", thisActivity);
                    lastActivity = thisActivity;
                }
                result_map.put("user", user);
                result_map.put("club", club);
                result_map.put("last_activity", lastActivity);
                result_map.put("server_time", timeStamp);
                out.print(gson_obj.toJson(result_map)); // output json string
            }

        } catch (Exception exc) {
            out.println("Session error");
            Utilities.logError("data_loader: Fatal Error. err=" + exc.toString());

        } finally {
            out.close();
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
