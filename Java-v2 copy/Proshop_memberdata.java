/***************************************************************************************
 *   Proshop_memberdata: This servlet will allow the proshop to manage certain parts
 *                       of the member's information, primarily their ratings
 * 			   
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       10/28/2009 by Paul
 *
 *
 *   Last Updated:  
 *
 *      10/06/11   Modified alphaTable.nameList() calls to pass an additional parameter used for displaying inact members when modifying past tee times.
 *       6/24/10   Modified alphaTable calls to pass the new enableAdvAssist parameter which is used for iPad compatability
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

import com.foretees.common.alphaTable;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;

public class Proshop_memberdata extends HttpServlet {

    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
     
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String templott = (String)session.getAttribute("lottery");      // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");
    String club = (String)session.getAttribute("club");
    
    // START DEFAULT PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    
    if (todo.equals("ratings")) getRatingData(req, session, sess_activity_id, club, todo, out, con);
    
    out.println("<h3 align=center>Select the action you wish to perform.</p>");
    
    out.println("<center>");
    // link for adjusting individual member ratings
    out.println("<a href=\"Proshop_memberdata?todo=ratings&solo\">Adjust Individual Member Ratings</a>");
    // link for adjusting bulk member ratings
    out.println("<a href=\"Proshop_memberdata?todo=ratings&bulk\">Adjust Bulk Member Ratings</a>");
    out.println("</center>");
    
    out.println("</body></html>");
    
    out.close();
    
 }
    
 
 private void getRatingData(HttpServletRequest req, HttpSession session, int sess_activity_id, String club, String todo, PrintWriter out, Connection con) {
     

    String user = (String)session.getAttribute("user");
    String mshipOpt = (String)session.getAttribute("mshipOpt");
    String mtypeOpt = (String)session.getAttribute("mtypeOpt");
   
    out.println("<table align=center border=1><tr><td>");
    
    // if user clicked on a name letter or mtype
    if (req.getParameter("letter") != null || req.getParameter("return") != null || req.getParameter("mtypeopt") != null) { 

        if (req.getParameter("mtypeopt") != null) {

            mtypeOpt = req.getParameter("mtypeopt");
            session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
        }
        if (req.getParameter("mshipopt") != null) {
            mshipOpt = req.getParameter("mshipopt");
            session.setAttribute("mshipOpt", mshipOpt);
        }

    }

    String letter = "%";         // default is 'List All'
    if (req.getParameter("letter") != null) {

        letter = req.getParameter("letter");

        if (letter.equals( "List All" )) {
            letter = "%";
        } else {
            letter = letter + "%";
        }
    }
    out.println("<form action=\"Proshop_memberdata\" method=\"get\" name=\"playerform\" id=\"playerform\">");
    out.println("<input type=hidden name=todo value=\"getMember\">");
    
    //
    //   Output the List of Names
    //

    //out.println("VARS: club=" + club + ", letter=" + letter + ", mshipOpt=" + mshipOpt + ", mtypeOpt=" + mtypeOpt);

    boolean enableAdvAssist = Utilities.enableAdvAssist(req);

    alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, true, null, enableAdvAssist, false, out, con);


    out.println("</td>");                                      // end of this column
    out.println("<td valign=\"top\">");

    //
    //   Output the Alphabit Table for Members' Last Names
    //
    alphaTable.getTable(out, user);


    //
    //   Output the Mship and Mtype Options
    //
    alphaTable.typeOptions(club, mshipOpt, mtypeOpt, out, con);

    out.println("");
    out.println("</td></tr>");
    out.println("</form></table>");
     
 }
 
}
