/***************************************************************************************     
 *   Support_export:  This servlet will handle exporting data out of ForeTees and in to
 *                    a format needed by our various partners.
 *                   
 *
 *
 *   called by:  support_main.htm
 *
 *   created: 1/19/2011   Paul S.
 *
 *   last updated:
 *
 *          
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;

public class Support_export extends HttpServlet {
    
    
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     

   if (req.getParameter("cdga") != null && req.getParameter("file") != null) {

       dumpCDGA_sheets(req, resp, true);

   } else if (req.getParameter("cdga") != null) {

       dumpCDGA_sheets(req, resp, false);
       return;
   }
   
 }
    


 private void dumpCDGA_sheets(HttpServletRequest req, HttpServletResponse resp, boolean file) {
     

    if (!file) {
        resp.setContentType("text/csv");                         // text file
        resp.setHeader("Content-Disposition", "attachment;filename=\"teesheet.csv\"");         // default file name
    }

    PrintWriter out = null;
    Connection con = null;

    try { out = resp.getWriter();
    } catch (Exception ignore) {}

    HttpSession session = null; 

    session = req.getSession(false);  // Get user's session object (no new one)

    if (session == null) {

        invalidUser(out);            // Intruder - reject
        return;
    }

    String user = (String)session.getAttribute("user");   // get username
    String club = (String)session.getAttribute("club");   // get club name

    if (!user.equals( "support" )) {

        invalidUser(out);            // Intruder - reject
        return;
    }


    try {
        
        con = dbConn.Connect(club);

    } catch (Exception exc) {

        // Error connecting to db....

        out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
        out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the DB.");
        out.println("<BR>Exception: "+ exc.getMessage());
        out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_init.htm\">Return</A>.");
        out.println("</CENTER></BODY></HTML>");
        return;
    }

    //
    // Now go dump the sheets
    //

    if (!file) {

        out.println("<p>Dumping " + club + " file to the cdga folder.</p>");
    }

    SystemUtils.dumpCDGA_sheets(club, con, (file) ? null : out);
 
 }


 private void invalidUser(PrintWriter out) {

    out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
    out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
    out.println("<hr width=\"40%\">");
    out.println("<BR><H2>Access Error</H2><BR>");
    out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
    out.println("<BR><BR>Please <A HREF=\"Logout\">login</A>");
    out.println("</CENTER></BODY></HTML>");
 }
    
    
}