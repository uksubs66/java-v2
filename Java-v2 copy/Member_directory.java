
/***************************************************************************************
 *   Member_directory:  This servlet will display the club's member directory and staff 
 *                      directory.
 *
 *
 *
 *   called by:  Common_skin - main tab (Directory)
 *
 *   created: 10/23/2012   Bob P.
 *
 *   last updated:
 *
 *         1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *
 *
 ***************************************************************************************
 */
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


public class Member_directory extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
       
    static final int ft_connect_directory_id = ProcessConstants.CONNECT_DIRECTORY_ID;     // FT Connect Directory Tab

    
    //******************************************************
    //  doGet
    //******************************************************
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> event_map = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

        if (session == null) {
            return;
        }

        Connection con = SystemUtils.getCon(session);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        String club = (String) session.getAttribute("club");   // get club name
        String caller = (String) session.getAttribute("caller");
        String user = (String) session.getAttribute("user");
        String mship = (String) session.getAttribute("mship");             // get member's mship type
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        int sess_activity_id = (Integer) session.getAttribute("activity_id");
        
        int activity_id = ft_connect_directory_id;  // 8001 = FT Connect Directory
        
        
        String dirType = "";
        
        if (req.getParameter("list") != null) {         // if list type specified

            dirType = req.getParameter("list");         // member or staff
        }


        //
        //   build the HTML page for the display
        //
        Common_skin.outputHeader(club, activity_id, "Member Directory", true, out, req);
        Common_skin.outputBody(club, activity_id, out, req);
        Common_skin.outputTopNav(req, club, activity_id, out, con);
        Common_skin.outputBanner(club, activity_id, clubName, "", out, req); 
        Common_skin.outputSubNav(club, activity_id, out, con, req);
        Common_skin.outputPageStart(club, activity_id, out, req);
        Common_skin.outputBreadCrumb(club, activity_id, out, "Directory", req);
        Common_skin.outputLogo(club, activity_id, out, req);
        
        //  Process accoring to the directory requested (member or staff)
        
        if (dirType.equalsIgnoreCase("member")) {     // if Member Dir requested

            out.println("<div class=\"main_instructions\">"
                    + "<p>"
                    + "Member Directory"
                    + "</p>"
                    + "</div>");
            
            out.println("<p align=center><BR><BR><img src=\"/" +rev+ "/images/Member-Directory.png\" width=600 height=619 border=0></p>");
            
        } else {
            
            out.println("<div class=\"main_instructions\">"
                    + "<p>"
                    + "Staff Directory"
                    + "</p>"
                    + "</div>");
            
            out.println("<p align=center><BR><BR><img src=\"/" +rev+ "/images/Staff-Directory.png\" width=600 height=990 border=0></p>");
        }


        Common_skin.outputPageEnd(club, activity_id, out, req);

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