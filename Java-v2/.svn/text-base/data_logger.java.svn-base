/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;


/**
 *
 * @author sindep
 */
public class data_logger extends HttpServlet {
   
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        //
        //  If call from self, then go to doPost
        //
        //if (req.getParameter("go") != null) {         // if call from the following process

        doPost(req, resp);      // call doPost processing
        //}
    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        try {

            // for now just check to see if at least one of the fields are present
            if (req.getParameter("appCodeName") != null) {

                logClientMessage(req, out);

                out.println("LOGGED");
            } else {
                out.println("INVALID CALL");
            }


        } finally { 
            out.close();
        }
    } 

    private void logClientMessage(HttpServletRequest req, PrintWriter out) {


       PreparedStatement pstmt = null;
       Connection con = null;

       String appCodeName = req.getParameter("appCodeName");
       String appName = req.getParameter("appName");
       String cookieEnabled = req.getParameter("cookieEnabled");
       String platform = req.getParameter("platform");
       String userAgent = req.getParameter("userAgent");
       String error = req.getParameter("error");
       String page = req.getParameter("page");
       String rawResponse = req.getParameter("rawResponse");
       String userName = req.getParameter("userName");
       String clubName = req.getParameter("clubName");
       
       try {

          con = Connect.getCon(ProcessConstants.REV);

          if (con != null) {      // con to v5 passed ?     ******* MUST be v5 or current rev level **********

             pstmt = con.prepareStatement (
                  "INSERT INTO v5.client_errorlog (id, err_timestamp, " +
                      "node, appCodeName, appName, cookieEnabled, platform, userAgent, error, page, rawResponse, userName, clubName) " +
                  "VALUES (null,now()," +
                      "?,?,?,?,?,?,?,?,?,?,?)");

             pstmt.clearParameters();
             pstmt.setInt(1, ProcessConstants.SERVER_ID);

             pstmt.setString(2, appCodeName);
             pstmt.setString(3, appName);
             pstmt.setString(4, cookieEnabled);
             pstmt.setString(5, platform);
             pstmt.setString(6, userAgent);
             pstmt.setString(7, error);
             pstmt.setString(8, page);
             pstmt.setString(9, rawResponse);
             pstmt.setString(10, userName);
             pstmt.setString(11, clubName);
             pstmt.executeUpdate();

          }

       } catch (Exception exc) {

           out.println("Error: " + exc.toString());

           // write it to the text error log
           Utilities.logErrorTxt("Fatal error trying to write to v5.client_errorlog", "");

           // then dump a stack trace to the catalina log file
           exc.printStackTrace();

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

           try { con.close(); }
           catch (Exception ignore) {}

       }

    }

}
