/*
 * thirdPartyAuth
 * 
 * Used to visit foretees in parent/top window of premier application
 * then pass control back to premier application to allow third party cookies
 * from foretees to be used in an iframe in the premier application.
 * 
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
//import java.lang.reflect.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import java.sql.*;

//import org.apache.commons.lang.*;
//import com.google.gson.*; // for json
//import com.google.gson.reflect.*; // for json

//import com.foretees.common.ProcessConstants;
//import com.foretees.common.Utilities;
//import com.foretees.common.nameLists;
//import com.foretees.common.sendEmail;
//import com.foretees.common.parmEmail;
//import com.foretees.common.iCalendar;
//import com.foretees.common.Connect;
//import com.foretees.common.parmDining;
//import com.foretees.common.getClub;

/**
 *
 * @author jkielkopf
 */
public class thirdPartyAuth extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param req servlet request
     * @param resp servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        
        String error = "";
        //
        //  Prevent caching 
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html;charset=UTF-8");

        PrintWriter out = resp.getWriter();
        
        Map<String, Object> verifyMap = SystemUtils.verifyMem(req);
        
        HttpSession session = (HttpSession) verifyMap.get("session");
        /*
        Connection con = null;
        

        if (session == null) {
            error = "Unable to establish session.";
        } else {
            con = SystemUtils.getCon(session);            // get DB connection
        }
        
        if (con == null) {
            error = "Unable to open database.";
        }
         * 
         */
        //session.setAttribute("sso_tpa_mode", "return");
        Common_skin.outputHeader((String)verifyMap.get("club"), (Integer)verifyMap.get("activity_id"), "", true, out, req);
        out.println("</html>");
        
        
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
