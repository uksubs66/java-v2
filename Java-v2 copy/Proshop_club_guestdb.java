/***************************************************************************************     
 *   Proshop_club_guestdb:  This servlet will provide configuration options for the guest tracking system
 *
 *
 *   called by:  Proshop_club
 *
 *   created: 12/30/09
 *
 *
 *   last updated:
 *
 *        6/10/10   Updated config to include the option to allow Members/Proshop to select 'TBA' and bypass guest selection
 *        4/15/10   Changed guestdb configuration page to function of a set of 3 radio buttons instead of 2 check boxes
 *        1/19/10   Changed to make names always be a required field.  Can no longer be changed in the configuration
 *       12/30/09   Class added
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

public class Proshop_club_guestdb extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     doPost(req, resp);
 }
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }

    // Get activity_id from session
    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    // Declare vars
    int count = 0;
    int allow_tba = 0;
    int force_uid = 0;
    int display_uid = 0;


    String uid = "N";
    String name = "R";
    String email = "N";
    String phone = "N";
    String address = "N";
    String gender = "N";
    String hdcp_num = "N";
    String hdcp_index = "N";
    String home_club = "N";

    String result_msg = "";

    // Check to see if we've reached her from a form submission
    if (req.getParameter("applyCfg") != null) {

        // Gather config option values from request object
        uid = (req.getParameter("uid") != null ? req.getParameter("uid") : "N");
        name = (req.getParameter("name") != null ? req.getParameter("name") : "R");
        email = (req.getParameter("email") != null ? req.getParameter("email") : "N");
        phone = (req.getParameter("phone") != null ? req.getParameter("phone") : "N");
        address = (req.getParameter("address") != null ? req.getParameter("address") : "N");
        gender = (req.getParameter("gender") != null ? req.getParameter("gender") : "N");
        hdcp_num = (req.getParameter("hdcp_num") != null ? req.getParameter("hdcp_num") : "N");
        hdcp_index = (req.getParameter("hdcp_index") != null ? req.getParameter("hdcp_index") : "N");
        home_club = (req.getParameter("home_club") != null ? req.getParameter("home_club") : "N");
        allow_tba = (req.getParameter("allow_tba") != null ? Integer.parseInt(req.getParameter("allow_tba")) : 0);
        force_uid = (req.getParameter("force_uid") != null ? 1 : 0);
        display_uid = (req.getParameter("display_uid") != null ? 1 : 0);
        
        try {

            // Apply any changes
            pstmt = con.prepareStatement(
                    "UPDATE guestdb SET " +
                    "uid = ?, name = ?, email = ?, phone = ?, address = ?, " +
                    "gender = ?, hdcp_num = ?, hdcp_index = ?, home_club = ?, allow_tba = ?, " +
                    "force_uid = ?, display_uid = ? " +
                    "WHERE activity_id = ?");
            pstmt.clearParameters();
            pstmt.setString(1, uid);
            pstmt.setString(2, name);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setString(5, address);
            pstmt.setString(6, gender);
            pstmt.setString(7, hdcp_num);
            pstmt.setString(8, hdcp_index);
            pstmt.setString(9, home_club);
            pstmt.setInt(10, allow_tba);
            pstmt.setInt(11, force_uid);
            pstmt.setInt(12, display_uid);

            pstmt.setInt(13, sess_activity_id);

            count = pstmt.executeUpdate();

            pstmt.close();
            
        } catch (Exception exc) {
            out.println("<!-- Error applying changes: " + exc.getMessage() + " -->");
        }

        if (count > 0) {

            out.println("<script type=\"text/javascript\">");
            out.println("<!--");
            out.println("window.close();");
            out.println(" // -->");
            out.println("</script>");

            return;

        } else {
            result_msg = "Failed to apply changes.  Please try again, or contact ForeTees Pro Support for assistance if the problem continues.";
        }

    }
    
    // START PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    out.println("<h2 align=center>Guest Tracking Configuration</h2>");
    
    out.println("<table width=560 align=center bgcolor=\"#336633\" cellpadding\"5\">");
    out.println("<tr><td style=\"text-align:center\"><font color=white size=2>");
    out.println("<b>Instructions</b>");
    out.println("</font></td></tr><tr><td><font color=white size=2>");
    out.println("Listed below are the information fields that can be stored as part of the guest tracking system.  " +
            "<br><br>For each item, select one of the three options:" +
            "<br><b>N = Not Used</b> - This item will not appear when adding/editing guests." +
            "<br><b>O = Optional</b> - This item may optionally be filled in when adding/editing guests, but is not required." +
            "<br><b>R = Required</b> - This item will be required when adding/editing guests (proshop can override)." +
            "<br><br><b>Unique ID</b> is a value entered only by Proshop users that <b>must</b> be unique for each guest. " +
            "<br>Entering Unique IDs for guests will help prevent the use of duplicate guests in the system " +
            "<br>and is highly recommended for any club that wishing to enforce guest policies on play!" +
            "<br><br><b>Allow TBA</b> - This setting will add a 'TBA' option to the top of the list of guests " +
            "<br>when booking reservations.  Selecting 'TBA' will allow tracked guest selection to be bypassed " +
            "<br>in the case where the identity of the guest is not known at the time of booking.");
    out.println("</font></td></tr>");
    out.println("</table><br><br>");
    
    out.println("<table border=0><tr valign=top><td align=center>");

    // If an error message is present, display it before continuing.
    if (!result_msg.equals("")) {
        out.println("<tr><td align=\"center\">" + result_msg + "</td></tr>");
    }
    
    // Print table to house ask/req option checkboxes
    out.println("<table border=0  bgcolor=#F5F5DC>");
    out.println("<form action=\"Proshop_club_guestdb\" method=\"POST\" name=\"guestdbCfg\">");
    out.println("<tr bgcolor=\"#336633\"><td align=center colspan=\"4\"><b><font color=white>Guest Information Options</font></b></td></tr>");
    try {

        // Gather up current configuration settings
        pstmt = con.prepareStatement("SELECT * FROM guestdb WHERE activity_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            uid = rs.getString("uid");
            name = rs.getString("name");
            email = rs.getString("email");
            phone = rs.getString("phone");
            address = rs.getString("address");
            gender = rs.getString("gender");
            hdcp_num = rs.getString("hdcp_num");
            hdcp_index = rs.getString("hdcp_index");
            home_club = rs.getString("home_club");
            allow_tba = rs.getInt("allow_tba");
            force_uid = rs.getInt("force_uid");
            display_uid = rs.getInt("display_uid");

        } else {

            // If a guestdb config does not exist for this activity_id, add one (options were defaulted to '0' at declaration, no action needed)
            pstmt.close();

            pstmt = con.prepareStatement("INSERT INTO guestdb (activity_id) VALUES (?)");
            pstmt.clearParameters();
            pstmt.setInt(1, sess_activity_id);

            pstmt.executeUpdate();
        }

        pstmt.close();

        // Print the header row
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\">N</td>");
        out.println("<td style=\"text-align:center; width:20px;\">O</td>");
        out.println("<td style=\"text-align:center; width:20px;\">R</td>");
        out.println("<td align=\"left\">(N = Not Used,&nbsp; O = Optional,&nbsp; R = Required)</td>");
        out.println("</tr>");

        // Unique ID (require only)
        out.println("<tr>");
        out.println("<td align=\"center\"><input type=\"radio\" name=\"uid\" value=\"N\"" + (uid.equals("N") ? " checked" : "") + "></td>");
        out.println("<td></td>");
        out.println("<td align=\"center\"><input type=\"radio\" name=\"uid\" value=\"R\"" + (uid.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Unique ID (Proshop Only)");
        out.println("</tr>");

        // Name
        /*
        out.println("<tr>");
        out.println("<td></td>");
        out.println("<td align=\"center\"><input type=\"radio\" name=\"name\" value=\"O\"" + (name.equals("O") ? " checked" : "") + "></td>");
        out.println("<td align=\"center\"><input type=\"radio\" name=\"name\" value=\"R\"" + (name.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Name");
        out.println("</tr>");
         */
        //out.println("<input type=\"hidden\" name=\"name\" value=\"R\">");
        /*
        out.println("<input type=\"hidden\" name=\"ask_name\" value=\"1\">");
        out.println("<input type=\"hidden\" name=\"req_name\" value=\"1\">");
        out.println("<tr>");
        out.println("<td align=\"center\"><input type=\"checkbox\" name=\"ask_name\"" + (ask_name == 1 ? " checked" : "") + " onclick=\"updateCheckbox(this.name,'req_name')\"></td>");
        out.println("<td align=\"center\"><input type=\"checkbox\" name=\"req_name\"" + (req_name == 1 ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Include Name?");
        out.println("</tr>");
         */

        // Email Address
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"email\" value=\"N\"" + (email.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"email\" value=\"O\"" + (email.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"email\" value=\"R\"" + (email.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Email Address");
        out.println("</tr>");

        // Phone Number
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"phone\" value=\"N\"" + (phone.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"phone\" value=\"O\"" + (phone.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"phone\" value=\"R\"" + (phone.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Phone Number");
        out.println("</tr>");

        // Address
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"address\" value=\"N\"" + (address.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"address\" value=\"O\"" + (address.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"address\" value=\"R\"" + (address.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Home Address");
        out.println("</tr>");

        // Gender
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"gender\" value=\"N\"" + (gender.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"gender\" value=\"O\"" + (gender.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"gender\" value=\"R\"" + (gender.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Gender");
        out.println("</tr>");

        // Handicap Number
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_num\" value=\"N\"" + (hdcp_num.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_num\" value=\"O\"" + (hdcp_num.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_num\" value=\"R\"" + (hdcp_num.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Handicap Number");
        out.println("</tr>");

        // Handicap Index
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_index\" value=\"N\"" + (hdcp_index.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_index\" value=\"O\"" + (hdcp_index.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"hdcp_index\" value=\"R\"" + (hdcp_index.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Handicap Index");
        out.println("</tr>");

        // Home Club
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"home_club\" value=\"N\"" + (home_club.equals("N") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"home_club\" value=\"O\"" + (home_club.equals("O") ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"home_club\" value=\"R\"" + (home_club.equals("R") ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Home Club");
        out.println("</tr>");

        // Additional Options Header
        out.println("<tr><td>&nbsp;</td></tr>");
        out.println("<tr bgcolor=\"#336633\"><td align=center colspan=\"4\"><b><font color=white>Additional Options</font></b></td></tr>");

        // Print the header row
        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\">N</td>");
        out.println("<td style=\"text-align:center; width:20px;\">P</td>");
        out.println("<td style=\"text-align:center; width:20px;\">M</td>");
        out.println("<td align=\"left\">(N = Not Used,&nbsp; P = Pro Only,&nbsp; M = Mem &amp; Pro)</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"allow_tba\" value=\"0\"" + (allow_tba == 0 ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"allow_tba\" value=\"1\"" + (allow_tba == 1 ? " checked" : "") + "></td>");
        out.println("<td style=\"text-align:center; width:20px;\"><input type=\"radio\" name=\"allow_tba\" value=\"2\"" + (allow_tba == 2 ? " checked" : "") + "></td>");
        out.println("<td align=\"left\">Allow use of 'TBA' option when selecting guests.");
        out.println("</tr>");

        /*
        // Force UID Entry
        out.println("<tr>");
        out.println("<td align=\"center\"><input type=\"checkbox\" name=\"force_uid\"" + (force_uid == 1 ? " checked" : "") + "></td>");
        out.println("<td align=\"left\" colspan=\"2\">Force Unique ID entry at check-in when missing? (Non-Overridable! Unique ID must be required)</td>");
        out.println("</tr>");

        // Display Existing UID
        out.println("<tr>");
        out.println("<td align=\"center\"><input type=\"checkbox\" name=\"display_uid\"" + (display_uid == 1 ? " checked" : "") + "></td>");
        out.println("<td align=\"left\" colspan=\"2\">Display existing Unique IDs at check-in for verification purposes?</td>");
        out.println("</tr>");
        */

        out.println("<input type=\"hidden\" name=\"force_uid\" value=\"0\">");
        out.println("<input type=\"hidden\" name=\"display_uid\" value=\"0\">");



        out.println("</table>");
    
    } catch (Exception exc) {

        out.println("<!-- Error gathering data/displaying form: " + exc.getMessage() + " -->");
        return;
    }
    
    
    out.println("</td></tr>");

    out.println("<tr><td align=\"center\"><br><br>");
    out.println("<input type=\"submit\" name=\"applyCfg\" value=\"Apply\">&nbsp;&nbsp;");
    out.println("<input type=\"button\" width=\"80px\" value=\"Cancel\" onclick=\"window.close()\">");
    out.println("</td></tr>");
    
    out.println("</form>");

    out.println("<tr><td align=\"left\">");
    out.println("<br><b>Note:</b> Guest types will need to be individually selected for use with the Guest Tracking system on the " +
            "Guest Types page before system will take effect.");
    out.println("</td></tr></table>");

    //
    // Scripts
    //
    /*
    out.println("<script type=\"text/javascript\">");
    out.println("<!--");

    // Whenever a radio button is changed, see if any were set to required, and set name to required and disable it if so
    out.println("function updateRadioButtons() {");
    out.println("  f = document.forms['guestdbCfg'];");
    out.println("  if (f.email[2].checked == true || f.phone[2].checked == true || f.address[2].checked == true || f.gender[2].checked == true || " +
                "      f.hdcp_num[2].checked == true || f.hdcp_index[2].checked == true || f.home_club[2].checked == true || f.uid[1].checked == true) {");
    out.println("    f.name[1].checked = true;");
    out.println("    f.name[0].disabled = true;");
    out.println("    f.name[1].disabled = true;");
    out.println("  } else {");
    out.println("    f.name[0].disabled = false;");
    out.println("    f.name[1].disabled = false;");
    out.println("  }");
    out.println("}");

    // Update the radio buttons to start
    out.println("updateRadioButtons();");

    out.println(" // -->");
    out.println("</script>");
    */
    
    out.println("</body></html>");
    
 }  // end of doGet

}
