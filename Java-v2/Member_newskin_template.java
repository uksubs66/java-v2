/*
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */



import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;

public class Member_newskin_template extends HttpServlet {


 static String rev = ProcessConstants.REV;


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so all buttons are properly displayed, etc.
    //
    resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder
    if (session == null) return;

    // if we made it here we are authenticated
    // now get the club name from the session
    String club = (String)session.getAttribute("club");
    int activity_id = (Integer)session.getAttribute("activity_id");

    // get a database connection
    Connection con = Connect.getCon(req);

    String clubName = Utilities.getClubName(con, true);


    // start the html output
    Common_skin.outputHeader(club, activity_id, "Page Title", true, out, req);
    Common_skin.outputBody(club, activity_id, out, req);
    Common_skin.outputTopNav(req, club, activity_id, out, con);
    Common_skin.outputBanner(club, activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
    Common_skin.outputSubNav(club, activity_id, out, con, req);
    Common_skin.outputPageStart(club, activity_id, out, req);
    Common_skin.outputBreadCrumb(club, activity_id, out, "Breadcrumb Title", req);
    Common_skin.outputLogo(club, activity_id, out, req);

    out.println("<div id=\"tt1_left\">");
    out.println("<p><strong>Manage Partners</strong></p></div>");



    // body here




    Common_skin.outputPageEnd(club, activity_id, out, req);
    
    out.close();

    try { con.close(); }
    catch (Exception ignore) {}

 }

}
