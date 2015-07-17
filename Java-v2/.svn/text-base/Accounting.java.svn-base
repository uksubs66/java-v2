/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.foretees.common.Common_skin;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.*;


import com.foretees.common.*;
import com.foretees.api.*;
import com.foretees.api.records.*;

//import com.google.gson.*; // for json


/**
 *
 * @author Owner
 */
public class Accounting extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        Common_skin.setNoCacheHtml(resp);
        
        PrintWriter out = resp.getWriter();
        
        req.setAttribute(ProcessConstants.RQA_RWD, true); // Accounting always uses RWD
        req.setAttribute(ProcessConstants.RQA_ACCOUNTING, true); // Always force Accouting Mode
        
        User ua = null;
        String error_message = null;
        
        if(req.getParameter("logout") != null){
            // Logout current user
            HttpSession session = req.getSession(false);
            if(session != null){
                session.removeAttribute(ApiConstants.sess_user_id);
                session.invalidate();
            }
        }
        
        if(req.getParameter("username") != null && req.getParameter("password") != null){
            // Login a user
            String username = reqUtil.getParameterString(req, "username", "");
            String password = reqUtil.getParameterString(req, "password", "");
            String club = reqUtil.getParameterString(req, "club", "");
            ua = new User(club, username, password);
            if(ua.last_error == null && ua.access.accounting_app){
                HttpSession session = req.getSession(false);
                if(session != null){
                    // Blow away any existing session
                    session.removeAttribute(ApiConstants.sess_user_id);
                    session.invalidate();
                }
                session = req.getSession();
                if(session != null){
                    session.setAttribute(ApiConstants.sess_user_id, ua.id);
                    session.setAttribute("name", ua.name);
                    ApiAccess.setAuthenticationToken(req, club, username, password);
                }
            }
            ua = ApiAccess.getUser(req); // Get access levels for authenticated user to test
            if(ua.last_error != null || !ua.access.accounting_app) {
                if(ua.last_error != null){
                    error_message += "Username or password not correct. Please try again.";
                } else {
                    error_message += "Accounting Access Denied.  Please use a different account.";
                }
                
                HttpSession session = req.getSession(false);
                if(session != null){
                    session.removeAttribute(ApiConstants.sess_user_id);
                    session.invalidate();
                }
            } else {
                // Success!
                resp.setStatus(302);
                resp.setHeader( "Location", "Accounting");
                resp.setHeader( "Connection", "close" );
                return;
            }
        }
        
        ua = ApiAccess.getUser(req); // Get access levels for this user
        
        int activity_id = 0;
        String club = "";
        String title;
        boolean allow_access = false;
        if(ua != null && ua.access.accounting_app){
            title = "ForeTees Accounting";
            allow_access = true;
        } else {
            title = "ForeTees Accounting Login";
            HttpSession session = req.getSession(false);
            if(session != null){
                session.removeAttribute(ApiConstants.sess_user_id);
                session.invalidate();
            }
        }

        Common_skin.outputHeader(club, activity_id, title, true, out, req);
        Common_skin.outputBody(club, activity_id, out, req);
        if(allow_access){
            Common_skin.outputTopNav(req, club, activity_id, out, null);
        }
        Common_skin.outputBanner(club, activity_id, title, "", out, req);
        Common_skin.outputPageStart(club, activity_id, out, req);
        //Common_skin.outputBreadCrumb(club, activity_id, out, title, req);
        
        if(error_message != null){
            out.print("<div class=\"sub_instructions error_message\"><h2>");
            out.print(error_message);
            out.print("</h2></div>");
        }
        
        if(!allow_access){
            // Show accouting login page
            out.print("<fieldset><legend>Login</legend>");
            out.print("<form method=\"post\" action=\"Accounting\">");
            out.print("<div><label>Username: <input type=\"text\" name=\"username\" /></label></div>");
            out.print("<div><label>Password: <input type=\"password\" name=\"password\" /></label></div>");
            out.print("<input type=\"submit\" value=\"Login\" />");
            out.print("</form>");
            out.print("</fieldset>");
            
        } else {
            // Show default accouting page
            out.print("<div class=\"main_instructions\"><h2>Welcome to ForeTees Accounting.</h2>");
            out.print("<p>Please use the menu above.</p></div>");
            // Show default accouting page
            out.print("<div class=\"sub_instructions\">");
            out.print("<p>(Placeholder for dashboard items)</p></div>");
        }
        
        // End of page
        Common_skin.outputPageEnd(club, activity_id, out, req);
        
        // Debug
        //Gson gson_obj = new Gson();
        //out.print(gson_obj.toJson(ua));
        
        out.close();
        
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
