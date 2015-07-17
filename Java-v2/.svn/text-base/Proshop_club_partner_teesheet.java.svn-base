/***************************************************************************************     
 *   Proshop_club_partner_teesheet:  This servlet will provide configuration options
 *                                   for defining how clubs expose their tee sheets
 *                                   to third-parties
 *
 *
 *   called by:  Proshop_club (doGet)
 *
 *   created: 09/28/2012
 *
 *
 *   last updated:
 *
 *       11/17/2012  Tweaked UI
 *       10/22/2012  Updated
 *
 *
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
//import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;

public class Proshop_club_partner_teesheet extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }

    String club = (String)session.getAttribute("club");

    int partner_id = (req.getParameter("partner_id") == null) ? 0 : Integer.valueOf(req.getParameter("partner_id"));

    String guest_type = "";
    String guest_tmode = "";
    String display_name = "";
    String partner_name = "";
    String availability_flags = "";
    int allow_members_to_join = 0;
    int hide_outside_times = 0;
    int enabled = 0;
    int max_allowed_guests = 0;
    int max_days_in_advance = 0;
    double fee_per_player = 0;

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;


    // DONE PERFORMING PRE-PROCCESSING ACTIONS


    // START PAGE OUTPUT
    out.println("<!DOCTYPE html>");
    out.println("<html lang=\"en-US\">");
    out.println("<head>");
  //out.println("<script src=\"http://code.jquery.com/ui/1.9.1/jquery-ui.js\"></script>");
    out.println("<style>");
    out.println("#tsp_table {");
    out.println(" background-color: #F5F5DC;");
    out.println(" border: 1px solid #E4E4E4;");
    out.println(" padding: 10px;");
    out.println("}");
    out.println("#tsp_table table td {");
  //out.println(" text-align: center;");
    out.println("}");
    out.println("#tsp_table table th {");
    out.println(" text-align: center;");
    out.println(" background-color: #336633;");
    out.println(" font-size: 14px;");
    out.println(" font-weight: bold;");
    out.println(" color: white;");
    out.println("}");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("");
    out.println("</style>");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    
    if (partner_id == 0) {

        // DISPLAY A LIST OF ALL AVAILABLE INTERFACES FOR SELECTION

        out.println("<h2 align=center>Tee Sheet Partner List</h2>");
        
        // instructions
        out.println("<table align=center bgcolor=\"#336633\">");
        out.println("<tr><td><font color=white size=2>");
        out.println("<b>Instructions:</b>&nbsp; Any available interfaces will be listed below along with the status of it.");
        out.println("");
        out.println("</font></td></tr>");
        out.println("</table><br><br>");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("" +
                "SELECT t1.id, t1.name, t2.enabled AS is_active, " +
                "   (SELECT 1 FROM teesheet_partner_config WHERE partner_id = t1.id) AS is_configuered " +
                "FROM v5.teesheet_partners t1 " +
                "LEFT OUTER JOIN teesheet_partner_config t2 ON t1.id = t2.partner_id " +
                "WHERE t1.enabled = 1 " +
                "ORDER BY is_configuered DESC, t1.name");

            out.println("<table align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\" width=\"400px\">");
            out.print("<tr>");
            out.print("<th align=center><b>Name</b></th>");
            out.print("<th align=center><b>Configured</b></th>");
            out.print("<th align=center><b>Active</b></th>");
            out.println("</tr>");

            while ( rs.next() ) {

                out.print("<tr>");
                out.print("<td>&nbsp; <a href=\"?partner_id=" + rs.getInt("id") + "\">" + rs.getString("name") + "</a></td>");
                out.print("<td>&nbsp; " + ((rs.getInt("is_configuered") == 1) ? "Yes" : "No") + "</td>");
                out.print("<td>&nbsp; " + ((rs.getInt("is_active") == 1) ? "Yes" : "No") + "</td>");
                out.println("</tr>");

            }

            out.println("</table>");

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading available partner list.", exc.getMessage(), out, false);
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    } else {

        // DISPLAY FORM FOR CONFIGURING THE SELECTED INTERFACE


        // first update the tmodes table for this club
        SystemUtils.doTableUpdate_tmodes(con);

        try {

            pstmt = con.prepareStatement (         // load the selected interface details ensuring that it's enabled in v5
                "SELECT t1.*, t2.name " +
                "FROM teesheet_partner_config t1 " +
                "LEFT OUTER JOIN v5.teesheet_partners t2 ON t1.partner_id = t2.id " +
                "WHERE t2.enabled = 1 AND t1.partner_id = ?");

            pstmt.clearParameters();
            pstmt.setInt(1, partner_id);
            rs = pstmt.executeQuery();

            if ( rs.next() ) {

                partner_name = rs.getString("name");
                guest_type = rs.getString("guest_type");
                guest_tmode = rs.getString("guest_tmode");
                display_name = rs.getString("display_name");
                allow_members_to_join = rs.getInt("allow_members_to_join");
                hide_outside_times = rs.getInt("hide_outside_times");
                enabled = rs.getInt("enabled");
                max_allowed_guests = rs.getInt("max_allowed_guests");
                max_days_in_advance = rs.getInt("max_days_in_advance");
                fee_per_player = rs.getDouble("fee_per_player");
                availability_flags = rs.getString("availability_flags");
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading partner data.", exc.getMessage(), out, false);
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        out.println("<h2 align=center>Tee Sheet Partner Configuration</h2>");

        out.println("<table align=center bgcolor=\"#336633\">");
        out.println("<tr><td><font color=white size=2>");
        out.println("<b>Instructions:</b>&nbsp; Use the settings below to control how your tee times are made available and how your members see these times.");
        out.println("");
        out.println("</font></td></tr>");
        out.println("</table><br><br>");

        out.println("<form method=\"POST\">");
        out.println("<table width=\"740\" align=\"center\" border=\"2\" bgcolor=\"#F5F5DC\">"); // id=\"tsp_tables\" <tr valign=top><td>

        // GUEST NAMES
        //out.println("<table>");
        out.println("<tr><th colspan=\"2\">" + partner_name + "</th></tr>");

        try {

            // guest types
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM guest5 WHERE gOpt = 1 ORDER BY guest");

            out.println("<tr><td>Choose a guest type that all players will use that are booked over this interface. Only 'pro-only' guest types are allowed. If no guest types are listed then check your guest configuration under Club Setup.</td><td>Select Guest Type:&nbsp;");
            out.println("<select name=\"guest_type\" size=\"1\">"); //  style=\"width: 100px\"

            while (rs.next()) {

                Common_Config.buildOption(rs.getString("guest"), rs.getString("guest"), guest_type, out);
            }

            out.println("</select>");

            // guest tmodes
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT tmode, tmodea FROM tmodes GROUP BY tmodea ORDER BY tmodea");

            out.println("<tr><td>Choose a Choose a mode of transportation to pre-assign all guests booked over this interface.</td><td>Select Mode of Transportation:&nbsp;");
            out.println("<select name=\"guest_tmode\" size=\"1\">"); //  style=\"width: 100px\"

            while (rs.next()) {

                Common_Config.buildOption(rs.getString("tmodea"), rs.getString("tmode") + "(" + rs.getString("tmodea") + ")", guest_tmode, out);
            }

            out.println("</select>");

        } catch (Exception exc) {

            out.println("Error loading up information." + exc.getMessage());
            return;

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { stmt.close(); }
            catch (Exception ignore) {}

        }

        out.println("</td></tr>");

        //out.println("<tr><td></td></tr>");

        //out.println("<tr><td align=center><input type=\"submit\" value=\"Save\" name=\"add\" style=\"width:80px\"></td></tr>");

        out.println("" +
                "<tr>" +
                "<td>Specify the name used on the member side tee sheet.</td>" +
                "<td><input type=\"text\" size=\"24\" name=\"display_name\" maxlength=\"24\" value=\"" + display_name + "\"></td>" +
                "</tr>");
        
        out.println("" +
                "<tr>" +
                "<td>You have the option to restrict the maximum number of outside guests per reservations made through this interface. How many outside guests would like to allow per reservation?</td>");
            out.println("<td><select name=\"max_allowed_guests\" size=\"1\">");

            for (int i = 1; i < 5; i++) {

                Common_Config.buildOption(i, i + " Guests", max_allowed_guests, out);
            }

            out.println("</select></td></tr>");

        out.println("" +
                "<tr>" +
                "<td>You can control the size of groups booked over this interface.  Choose between allowing groups of any size or restricting their size snf makeup.</td>");
            out.println("<td><select name=\"availability_flags\" size=\"1\">");
            
            Common_Config.buildOption("YYYY", "Any size group 1-4", availability_flags, out);
            Common_Config.buildOption("NYYY", "Two or more - no singles", availability_flags, out);
            Common_Config.buildOption("NYNY", "Twosomes & Foursomes only", availability_flags, out);
            Common_Config.buildOption("NNNY", "Foursomes only", availability_flags, out);
            
            /*
            out.println("<option value=\"YYYY\">YYYY = Any size 1-4 players");
            out.println("<option value=\"NYNN\">NYNN = Twosomes only");
            out.println("<option value=\"YNNN\">YNNN = 1 players only");
            out.println("<option value=\"NYNY\">NYNY = 2 or 4 players only");
            out.println("<option value=\"YNYN\">YNYN = 1 or 3 players only");
            out.println("<option value=\"YYYY\">YYYY = ");*/
            out.println("</select></td></tr>");

        out.println("" +
                "<tr>" +
                "<td>Specify the fee per player.</td>" +
                "<td><input type=\"text\" name=\"fee_per_player\" maxlength=\"6\" size=\"7\" value=\"" + fee_per_player + "\"></td>" +
                "</tr>");

        out.println("" +
                "<tr>" +
                "<td>Maximum days in advance outside rounds may be booked on to your tee sheets over this interface.</td>" +
                "<td><input type=\"text\" name=\"max_days_in_advance\" value=\"" + max_days_in_advance + "\" maxlength=\"3\" size=\"4\">&nbsp;Days</td>" +
                "</tr>");

        out.println("" +
                "<tr>" +
                "<td>Outside players cannot join existing tee times with members but you can allow your members to join a tee time with an outside group.<br>Would you like to allow your members to join outside groups?</td>" +
                "<td nowrap><input id=\"chk1\" type=\"checkbox\" name=\"allow_members_to_join\" value=\"1\" " + ((allow_members_to_join == 1) ? "checked" : "") + ">&nbsp;<label for=\"chk1\">Allow Members To Join Outside Groups</label></td>" +
                "</tr>");

        out.println("" +
                "<tr>" +
                "<td>You have the option to show or hide tee times for your outside rounds. Would you like to hide or show these outside rounds on your memeber's tee sheets?</td>" +
                "<td><input id=\"chk2\" type=\"checkbox\" name=\"hide_outside_times\" value=\"1\" " + ((hide_outside_times == 1) ? "checked" : "") + ">&nbsp;<label for=\"chk2\">Hide Outside Rounds on Tee Sheet</label></td>" +
                "</tr>");

        out.println("" +
                "<tr>" +
                "<td>Controls whether or not the interface is active.</td>" +
                "<td><input id=\"chk3\" type=\"checkbox\" name=\"enabled\" value=\"1\" " + ((enabled == 1) ? "checked" : "") + ">&nbsp;<label for=\"chk3\">Enabled?</label></td>" +
                "</tr>");

        out.println("<tr><td colspan=\"2\" align=\"center\"><input type=\"submit\" value=\" Save Changes \"></td></tr>");

        out.println("");

        out.println("</table>");
        out.println("</form>");

        //out.println("</td></tr></table>");

        out.println("<br><center>");
        out.println("<form><input type=button value=\"  Close  \" onclick=\"window.close()\"></form>");
        out.println("</center>");
/*
        out.println("<script type=\"text/javascript\">");
        out.println("function selectTees(pCourseId) {");
        out.println(" f = document.forms[\"frmTees\"];");
        out.println(" f.course_id.value = pCourseId;");
        out.println(" f.tee_id.value = 0;");
        out.println(" f.method = \"GET\";");
        out.println(" f.submit();");
        out.println("}");
        out.println("function showTees(pTeeId) {");
        out.println(" f = document.forms[\"frmTees\"];");
        out.println(" f.method = \"GET\";");
        out.println(" f.submit();");
        out.println("}");
        out.println("</script>");
*/
    }
    
    out.println("</body></html>");
    
 }  // end of doGet

 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     
     
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }

    // GET FORM VALUES
    String guest_type = (req.getParameter("guest_type") == null) ? "" : req.getParameter("guest_type");
    String display_name = (req.getParameter("display_name") == null) ? "" : req.getParameter("display_name");
    String guest_tmode = (req.getParameter("guest_tmode") == null) ? "" : req.getParameter("guest_tmode");
    int partner_id = (req.getParameter("partner_id") == null) ? 0 : Integer.valueOf(req.getParameter("partner_id"));
    int allow_members_to_join = (req.getParameter("allow_members_to_join") == null) ? 0 : 1; //Integer.valueOf(req.getParameter("allow_members_to_join"));
    int hide_outside_times = (req.getParameter("hide_outside_times") == null) ? 0 : 1; //Integer.valueOf(req.getParameter("hide_outside_times"));
    int max_allowed_guests = (req.getParameter("max_allowed_guests") == null) ? 0 : Integer.valueOf(req.getParameter("max_allowed_guests"));
    int max_days_in_advance = (req.getParameter("max_days_in_advance") == null) ? 0 : Integer.valueOf(req.getParameter("max_days_in_advance"));
    String availability_flags = (req.getParameter("availability_flags") == null) ? "" : req.getParameter("availability_flags");
    int enabled = (req.getParameter("enabled") == null) ? 0 : 1; //Integer.valueOf(req.getParameter("enabled"));

    double fee_per_player = 0;

    try {
        fee_per_player = Double.valueOf(req.getParameter("fee_per_player"));
    } catch (Exception ignore) {}

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    
    // DISPLAY FORM FOR CONFIGURING THE SELECTED INTERFACE
    try {

        pstmt = con.prepareStatement ( // partner_id is unique key
            "INSERT INTO teesheet_partner_config " +
                "(partner_id, guest_type, guest_tmode, display_name, allow_members_to_join, hide_outside_times, max_allowed_guests, max_days_in_advance, fee_per_player, availability_flags, enabled) " +
            "VALUES " +
                "(?,?,?,?,?,?,?,?,?,?,?) " +
            "ON DUPLICATE KEY UPDATE " +
                "guest_type = VALUES(guest_type), " +
                "guest_tmode = VALUES(guest_tmode), " +
                "display_name = VALUES(display_name), " +
                "allow_members_to_join = VALUES(allow_members_to_join), " +
                "hide_outside_times = VALUES(hide_outside_times), " +
                "max_allowed_guests = VALUES(max_allowed_guests), " +
                "max_days_in_advance = VALUES(max_days_in_advance), " +
                "fee_per_player = VALUES(fee_per_player), " +
                "availability_flags = VALUES(availability_flags), " +
                "enabled = VALUES(enabled)");

        pstmt.clearParameters();
        pstmt.setInt(1, partner_id);
        pstmt.setString(2, guest_type);
        pstmt.setString(3, guest_tmode);
        pstmt.setString(4, display_name);
        pstmt.setInt(5, allow_members_to_join);
        pstmt.setInt(6, hide_outside_times);
        pstmt.setInt(7, max_allowed_guests);
        pstmt.setInt(8, max_days_in_advance);
        pstmt.setDouble(9, fee_per_player);
        pstmt.setString(10, availability_flags);
        pstmt.setInt(11, enabled);
        pstmt.executeUpdate();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error saving partner data.", exc.getMessage(), out, false);
        return;

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("<!DOCTYPE html>");
    out.println("<html lang=\"en-US\">");
    out.println("<head>");
    out.println("<meta http-equiv=\"Refresh\" content=\"2; url=/" + rev + "/servlet/Proshop_club_partner_teesheet?partner_id=" + partner_id + "\">");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<br><br><center><h2>Changes Saved</h2></center>");

    out.println("<br><center>");
    out.println("<form method=\"GET\"><input type=\"hidden\" name=\"partner_id\" value=\"" + partner_id + "\"><input type=\"submit\" value=\" Continue \"></form>");
    out.println("</center>");
        
 }
 
}
