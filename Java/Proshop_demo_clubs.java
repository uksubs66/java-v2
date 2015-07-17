/***************************************************************************************
 *   Proshop_demo_clubs: This servlet will implement demo equipment tracking feature
 *
 *
 *   Called by:     called by self and start w/ direct call main menu option
 *
 *
 *   Created:       1/18/2009 by Paul
 *
 *
 *   Bugs:  If you delete a mfr then close the popup window and select that mfr
 *          from the list you can add a club with that invalid mfr - Solution: force refresh page
 *          after closing the popup window.  Need to check on IE if brower resets form on reload if so
 *          we may need to pass the form elements thru again to repopulate the page OR use a iframe to
 *          reload that select box.
 *
 *          Need to trap error when inserting new mfr - if it already exists it bombs out - need to
 *          catch and see if it's a duplicate key error and warn accordingly if so.
 *
 *
 *
 *
 *   Last Updated:  
 *
 *   6/24/10   Modified alphaTable.nameList calls to pass the new enableAdvAssist parameter which is used for iPad compatability
 *   5/11/10   Added printMemberHistory and printClubHistory reports to Demo Club system.  
 *   5/10/10   Added bag storage #s to Demo Clubs Checked Out report (printClubsOut()).  Will only display on Golf side.
 *   5/10/10   Fixed Demo Equipment reports from displaying entries from all activities.  Will now display only ones for the current activity, as intended.
 *   3/27/10   Updated remove club processing to set ICN to null when club is removed.  Also fixed grammar issues where EQUIP_LABEL was being used
 *  12/14/09   Changed demo club removal to update the club_name by appending the club_id to the front, allowing club names to be reused
 *  12/11/09   Changes to allow clubs to be set inactive, so they are effectly 'deleted' from the club's point of view, but remain
 *             in the database for use with future reports/options.  Added 'Remove' button on maintain clubs page that, when clicked,
 *             sets inact = 1 for that club
 *  11/05/09   Converted Club Types to now work off the demo_club_types table and a type_id field in demo_clubs
 *  10/24/09   Added support for Activites
 *   7/24/09   Changed checkin system to identify via 'id' from demo_club_usage table instead of 'club_id', which was causing a bug in the stats
 *   7/08/09   Changed wording of "Done" buttons on check out, check in, and maintain inventory page to read "Cancel" instead to avoid confusion
 *   4/08/09   Added display of ICN # to Check Out page
 *   3/13/09   Added clarification text after ICN text box to emphasize that it's an optional field and must be unique
 *   3/11/09   Added Limited Access Proshop User hooks.
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

// foretees imports
import com.foretees.common.alphaTable;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;


public class Proshop_demo_clubs extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;
    
    
 //*****************************************************
 // Process the a get method on this page as a post call
 //*****************************************************
 //
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

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
        
    String result_message = "";
    String club = (String)session.getAttribute("club");
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    
    String EQUIP_LABEL = (sess_activity_id == 0) ? "Clubs" : "Equipment";
    String EQUIP_LABEL2 = (sess_activity_id == 0) ? "Club" : "Equipment";
    
    int club_id = 0;
    
    String cid = (req.getParameter("club_id") == null) ? "0" : req.getParameter("club_id");
    
    try {
        club_id = Integer.parseInt(cid);
    } catch (Exception ignore) { }

    // Do a blind insert to try and add "Other" into demo_clubs_types for this activity_id and ignore the error if it fails
    try {
        pstmt = con.prepareStatement("INSERT INTO demo_clubs_types (id, activity_id, type) VALUES (NULL,?,'Other')");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        pstmt.executeUpdate();
        
        pstmt.close();
        
    } catch (Exception ignore) {}
    
    // handle adding/removing of values
    if (req.getParameter("printClubs") != null || req.getParameter("printClubUsage") != null ||
        req.getParameter("printClubsOut") != null || req.getParameter("getCheckIn") != null || 
        req.getParameter("doCheckIn") != null || req.getParameter("doCheckOut") != null || 
        req.getParameter("checkInOut") != null) {
        
        // Check Feature Access Rights for current proshop user
        if (!SystemUtils.verifyProAccess(req, "DEMOCLUBS_CHECKIN", con, out)) {
            SystemUtils.restrictProshop("DEMOCLUBS_CHECKIN", out);
        }
    }
    if (req.getParameter("printClubs") != null) {
    
        printClubs(req, resp, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("printClubUsage") != null) {
    
        printClubUsage(req, resp, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("printClubsOut") != null) {
    
        printClubsOut(req, resp, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("printMemberHistory") != null) {

        printMemberHistory(req, resp, lottery, sess_activity_id, club, con, out);
        return;
        
    } else if (req.getParameter("printClubHistory") != null) {

        printClubHistory(req, resp, lottery, sess_activity_id, con, out);
        return;

    } else if (req.getParameter("getCheckIn") != null) {
    
        getCheckIn(req, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("doCheckIn") != null) {
    
        doCheckIn(req, session, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("doCheckOut") != null) {
    
        doCheckOut(req, session, con, out, lottery, sess_activity_id);
        return;
        
    } else if (req.getParameter("checkInOut") != null) {
    
        getCheckOut(req, session, con, out, lottery, sess_activity_id);
        return;
        
    } 
    
    // Everything below is for demo club management.
    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "DEMOCLUBS_MANAGE", con, out)) {
        SystemUtils.restrictProshop("DEMOCLUBS_MANAGE", out);
    }
    
    if (req.getParameter("mfrCfg") != null) {
        
        String errMsg = "";
        
        try {
            if (req.getParameter("removeMfr") != null) {
                
                if (req.getParameter("mfr_id") != null) {
                    
                    int mfr_id = Integer.parseInt(req.getParameter("mfr_id"));
                    
                    if (mfr_id > 2) {
                        
                        pstmt = con.prepareStatement("SELECT id FROM demo_clubs WHERE mfr_id = ? AND inact = 0 LIMIT 1");
                        pstmt.clearParameters();
                        pstmt.setInt(1, mfr_id);
                        rs = pstmt.executeQuery();
                        
                        if (rs.next()) {
                            errMsg += "This manufacturer cannot be removed because it is in use.  " +
                                    "Please ensure that no equipment is using this manufacturer and try again.";
                            
                        } else {
                            pstmt.close();
                            
                            pstmt = con.prepareStatement("DELETE FROM demo_clubs_mfr WHERE id = ?");
                            pstmt.clearParameters();
                            pstmt.setInt(1, mfr_id);
                            pstmt.executeUpdate();

                            pstmt.close();
                            
                        }
                        
                    } else {
                        errMsg += "This manufacturer may not be removed.";
                    }
                }
                
            } else if (req.getParameter("addMfr") != null) {
                
                if (req.getParameter("mfrToAdd") != null && !req.getParameter("mfrToAdd").equals("")) {
                    
                    String mfr = req.getParameter("mfrToAdd");
                    
                    pstmt = con.prepareStatement("INSERT INTO demo_clubs_mfr (id, activity_id, mfr) VALUES (NULL, ?, ?)");
                    pstmt.clearParameters();
                    pstmt.setInt(1, sess_activity_id);
                    pstmt.setString(2, mfr);
                    pstmt.executeUpdate();
                    
                    pstmt.close();
                }
            }
            
            // START PAGE OUTPUT
            out.println(SystemUtils.HeadTitle("Manage Demo Club Manufacturers"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
            
            if (!errMsg.equals("")) {
                out.println("<table border=\"1\" align\"center\" bgcolor=\"#F5F5DC\"><tr><td align=\"center\">");
                out.println("<font size=\"2\">" + errMsg + "</font>");
                out.println("</td></tr></table><br>");
            }
            
            out.println("<table border=\"1\" align\"center\" width=\"300px\" bgcolor=\"#F5F5DC\"><tr><td align=\"center\">");
            out.println("<font size=\"2\">Add or remove manufacturers below.");
            out.println("<br><b>Note</b> that a manufacturer may only be removed if no equipment is currently set to it.</font>");
            out.println("</td></tr></table><br>");
            
            out.println("<table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
            out.println("<input type=\"hidden\" name=\"mfrCfg\">");
            out.println("<tr><td align=\"center\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Demo " + EQUIP_LABEL + " Manufacturers</font></td></tr>");
            out.println("<tr><td align=\"center\">");
            
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM demo_clubs_mfr WHERE id > 2 ORDER BY mfr");
            
            out.println("<select name=\"mfr_id\" size=\"15\">");
            while (rs.next()) {
                out.println("<option value=\"" + rs.getInt("id") + "\">" + rs.getString("mfr") + "</option>");
            }
            out.println("</select>");
            
            out.println("</td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<input type=\"submit\" name=\"removeMfr\" value=\"Remove\" style=\"width:80px\">");
            out.println("</form>");
            out.println("</td></tr><tr><td align=\"center\"><hr></td></tr>");
            out.println("<form method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
            out.println("<input type=\"hidden\" name=\"mfrCfg\">");
            out.println("<tr><td align=\"center\"><input type=\"text\" name=\"mfrToAdd\" size=\"20\"></td></tr>");
            out.println("<tr><td align=\"center\"><input type=\"submit\" name=\"addMfr\" value=\"Add\" style=\"width:80px\"></td></tr>");
            out.println("</form>");
            out.println("</table>");
            out.println("<br><form align=\"center\"><input type=\"button\" onclick=\"window.close()\" value=\"Close\" style=\"width:70px\"></form>");
            out.println("</body></html>");
            
            return;
            
        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error updating manufacturer list.", exc.getMessage(), out, false);
        } finally {
            try {rs.close();} catch (Exception ignore) {}
            try {stmt.close();} catch (Exception ignore) {}
        }
        
    } else if (req.getParameter("typeCfg") != null) {

        String errMsg = "";

        try {
            if (req.getParameter("removeType") != null) {

                if (req.getParameter("type_id") != null) {

                    int type_id = Integer.parseInt(req.getParameter("type_id"));

                    pstmt = con.prepareStatement("SELECT id FROM demo_clubs WHERE type_id = ? AND activity_id = ? AND inact = 0 LIMIT 1");
                    pstmt.clearParameters();
                    pstmt.setInt(1, type_id);
                    pstmt.setInt(2, sess_activity_id);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        errMsg += "This equipment type cannot be removed because it is in use.  " +
                                "Please ensure that no equipment is using this equipment type and try again.";

                    } else {
                        
                        pstmt.close();

                        pstmt = con.prepareStatement("DELETE FROM demo_clubs_types WHERE id = ? AND activity_id = ?");
                        pstmt.clearParameters();
                        pstmt.setInt(1, type_id);
                        pstmt.setInt(2, sess_activity_id);
                        pstmt.executeUpdate();

                        pstmt.close();

                    }
                }

            } else if (req.getParameter("addType") != null) {

                if (req.getParameter("typeToAdd") != null && !req.getParameter("typeToAdd").equals("")) {

                    String type = req.getParameter("typeToAdd");

                    pstmt = con.prepareStatement("INSERT INTO demo_clubs_types (id, activity_id, type) VALUES (NULL,?,?)");
                    pstmt.clearParameters();
                    pstmt.setInt(1, sess_activity_id);
                    pstmt.setString(2, type);
                    pstmt.executeUpdate();

                    pstmt.close();
                }
            }

            // START PAGE OUTPUT
            out.println(SystemUtils.HeadTitle("Manage Demo Equipment Types"));
            out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            if (!errMsg.equals("")) {
                out.println("<table border=\"1\" align\"center\" bgcolor=\"#F5F5DC\"><tr><td align=\"center\">");
                out.println("<font size=\"2\">" + errMsg + "</font>");
                out.println("</td></tr></table><br>");
            }

            out.println("<table border=\"1\" align\"center\" width=\"300px\" bgcolor=\"#F5F5DC\"><tr><td align=\"center\">");
            out.println("<font size=\"2\">Add or remove " + EQUIP_LABEL2.toLowerCase() + " types below.");
            out.println("<br><b>Note</b> that a" + (sess_activity_id == 0 ? " club" : "n equipment") + " type may only be removed if no " + (sess_activity_id == 0 ? "clubs are" : "equipment is") + " currently set to it.</font>");
            out.println("</td></tr></table><br>");

            out.println("<table border=\"0\" align=\"center\" bgcolor=\"#F5F5DC\">");
            out.println("<form method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
            out.println("<input type=\"hidden\" name=\"typeCfg\">");
            out.println("<tr><td align=\"center\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Demo " + EQUIP_LABEL + " Types</font></td></tr>");
            out.println("<tr><td align=\"center\">");

            pstmt = con.prepareStatement("SELECT * FROM demo_clubs_types WHERE activity_id = ? AND type <> 'Other' ORDER BY type");
            pstmt.clearParameters();
            pstmt.setInt(1, sess_activity_id);
            rs = pstmt.executeQuery();

            out.println("<select name=\"type_id\" size=\"15\">");
            while (rs.next()) {
                out.println("<option value=\"" + rs.getInt("id") + "\">" + rs.getString("type") + "</option>");
            }
            out.println("</select>");

            out.println("</td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<input type=\"submit\" name=\"removeType\" value=\"Remove\" style=\"width:80px\">");
            out.println("</form>");
            out.println("</td></tr><tr><td align=\"center\"><hr></td></tr>");
            out.println("<form method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
            out.println("<input type=\"hidden\" name=\"typeCfg\">");
            out.println("<tr><td align=\"center\"><input type=\"text\" name=\"typeToAdd\" size=\"20\"></td></tr>");
            out.println("<tr><td align=\"center\"><input type=\"submit\" name=\"addType\" value=\"Add\" style=\"width:80px\"></td></tr>");
            out.println("</form>");
            out.println("</table>");
            out.println("<br><form align=\"center\"><input type=\"button\" onclick=\"window.close()\" value=\"Close\" style=\"width:70px\"></form>");
            out.println("</body></html>");

            return;

        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error updating equipment type list.", exc.getMessage(), out, false);
        } finally {
            try {rs.close();} catch (Exception ignore) {}
            try {stmt.close();} catch (Exception ignore) {}
        }

    } else if (req.getParameter("remove") != null) {

        if (club_id > 0) {
            
            try {
                pstmt = con.prepareStatement("SELECT id FROM demo_clubs_usage WHERE club_id = ? AND datetime_in='0000-00-00 00:00:00'");
                pstmt.clearParameters();
                pstmt.setInt(1, club_id);
                rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    out.println(SystemUtils.HeadTitle("Manage Demo Club Manufacturers"));
                    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
                    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");
                    out.println("<table border=\"0\" bgcolor=\"FFFFFF\" align=\"center\" style=\"width:650px\">");
                    out.println("<tr><td align=\"center\"><br><br><br><h3>Club Not Available!</h3></td></tr>");
                    out.println("<tr><td align=\"center\">");
                    out.println("This club is currently checked out.");
                    out.println("<br><br>Please check the club in before attempting to remove it, or disable it from the Maintain Inventory page if the club is lost.");
                    out.println("</td></tr><tr><td align=\"center\">");
                    out.println("<br><a href=\"/" + rev + "/servlet/Proshop_demo_clubs?demoConfig&club_id=" + club_id + "\" style=\"text-decoration:underline\"><button style=\"width:80px\">Return</button></a>");
                    out.println("&nbsp;<a href=\"/" + rev + "/servlet/Proshop_demo_clubs?getCheckIn\" style=\"text-decoration:underline\"><button style=\"width:100px\">Club Check-In</button></a>");
                    out.println("</td></tr></table>");
                    out.close();
                    
                } else {
                    pstmt.close();

                    // Grab the club name from demo_clubs and update it with the club_id appended to the front
                    pstmt = con.prepareStatement("SELECT name FROM demo_clubs WHERE id = ?");
                    pstmt.clearParameters();
                    pstmt.setInt(1, club_id);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        
                        String temp_name = rs.getString("name").trim();

                        temp_name = club_id + " " + temp_name;

                        pstmt2 = con.prepareStatement("UPDATE demo_clubs SET name = ?, icn=null, inact = 1 WHERE id = ?");
                        pstmt2.clearParameters();
                        pstmt2.setString(1, temp_name);
                        pstmt2.setInt(2, club_id);
                        pstmt2.executeUpdate();

                        pstmt2.close();
                        
                    }

                    pstmt.close();
                }
                
                club_id = 0;
                
            } catch (Exception exc) {
                SystemUtils.buildDatabaseErrMsg("Error removing demo club from system.", exc.getMessage(), out, false);
            } finally {
                try {pstmt.close();} catch (Exception ignore) {}
            }
        }
        
    } else if (req.getParameter("update") != null) {
        
        String club_name = (req.getParameter("club_name") == null) ? "" : req.getParameter("club_name").trim();
        String club_icn = (req.getParameter("club_icn") == null) ? "" : req.getParameter("club_icn").trim();
        int mfr_id = (req.getParameter("mfr_id") == null) ? 0 : Integer.parseInt(req.getParameter("mfr_id"));
        int type_id = (req.getParameter("type_id") == null) ? 0 : Integer.parseInt(req.getParameter("type_id"));
        String notes = (req.getParameter("notes") == null) ? "" : req.getParameter("notes").trim();
        String senabled = (req.getParameter("enabled") == null) ? "" : req.getParameter("enabled");
        int for_sale = (req.getParameter("for_sale") == null) ? 0 : 1;
        int enabled = 0;
        
        if (senabled.equals("1")) enabled = 1;
        
        if (!club_name.equals("")) {
          
           club_name = SystemUtils.unfilter(club_name);     // change special chars back to normal form
        }
        
        try {
            
            // if an empty string is passed in for the icn then lets set to null otherwise update it
            int tmp = 7;
            
            pstmt = con.prepareStatement(
                "UPDATE demo_clubs " +
                "SET name = ?, type_id = ?, mfr_id = ?, notes = ?, for_sale = ?, " +
                "enabled = ?, " + ((!club_icn.equals("")) ? "icn = ? " : "icn = NULL ") + 
                "WHERE id = ?");
            
            pstmt.clearParameters();
            pstmt.setString(1, club_name);
            pstmt.setInt(2, type_id);
            pstmt.setInt(3, mfr_id);
            pstmt.setString(4, notes);
            pstmt.setInt(5, for_sale);
            pstmt.setInt(6, enabled);
            if (!club_icn.equals("")) {
                pstmt.setString(7, club_icn);
                tmp++;
            }
            pstmt.setInt(tmp, club_id);
            
            tmp = pstmt.executeUpdate();
            
            pstmt.close();

            if (tmp==0) {
                result_message = "No changes were made.";
            } else {
                result_message = "Demo " + EQUIP_LABEL2.toLowerCase() + " updated successfully.";
                club_id = 0;
            }
            
        } catch (Exception exc) {
            
            if (exc.getMessage().indexOf("uplicate entry") != -1) {

                if (exc.getMessage().indexOf("key 2") != -1) {
                    result_message = "Name already exists. The names assigned to the demo equipment must be unique.";
                } else {
                    result_message = "The specified Inventory Control Number is already in use. The ICN must be unique.";
                }
            } else {
                
                SystemUtils.buildDatabaseErrMsg("Error updating demo club information.", exc.getMessage(), out, false);
                
            }
            
        } finally {

            try {pstmt.close();} catch (Exception ignore) {}

        }
        
       
    } else if (req.getParameter("add") != null) {
        
        String club_name = (req.getParameter("club_name") == null) ? "" : req.getParameter("club_name").trim();
        String club_icn = (req.getParameter("club_icn") == null) ? "" : req.getParameter("club_icn").trim();
        int type_id = (req.getParameter("type_id") == null) ? 0 : Integer.parseInt(req.getParameter("type_id"));
        int mfr_id = (req.getParameter("mfr_id") == null) ? 0 : Integer.parseInt(req.getParameter("mfr_id"));
        String notes = (req.getParameter("notes") == null) ? "" : req.getParameter("notes").trim();
        int for_sale = (req.getParameter("for_sale") == null) ? 0 : 1;
        
        if (!club_name.equals("")) {
            
            try {
                
                pstmt = con.prepareStatement(
                        "INSERT INTO demo_clubs (id, name, type_id, mfr_id, notes, for_sale, enabled, activity_id, icn) VALUES (NULL, ?, ?, ?, ?, ?, 1, ?, " + ((!club_icn.equals("")) ? "?" : "NULL") + ");");
                pstmt.clearParameters();
                pstmt.setString(1, club_name);
                pstmt.setInt(2, type_id);
                pstmt.setInt(3, mfr_id);
                pstmt.setString(4, notes);
                pstmt.setInt(5, for_sale);
                pstmt.setInt(6, sess_activity_id);
                if (!club_icn.equals("")) pstmt.setString(7, club_icn);
                pstmt.executeUpdate();
                
                /*
                pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
                rs = pstmt.executeQuery();
                if (rs.next()) club_id = rs.getInt(1);
                */
                
                pstmt.close();
                
                club_id = 0;
                result_message = "Demo " + EQUIP_LABEL2.toLowerCase() + " has been added.  You may now select it from the list below to configure it.";
                
            } catch (Exception exc) {
                
                if (exc.getMessage().indexOf("uplicate entry") == 1) {
                    
                    result_message = "Name already exists. The name assigned to the demo " + EQUIP_LABEL2.toLowerCase() + " must be unique.";
                }
                
            } finally {
                
                try {rs.close();} catch (Exception ignore) {}
                try {pstmt.close();} catch (Exception ignore) {}
                
            }
        }
        
    }
    
    // DONE PERFORMING PRE-PROCCESSING ACTIONS - DEFAULTS TO CLUB MANAGEMENT PAGE
    
    
    // START PAGE OUTPUT
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    /*
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    */
    
    
    out.println("<br><h2 align=center>Demo " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " Management</h2>");
    
    out.println("<table width=660 align=center cellpadding=5 bgcolor=\"#336633\">");
    out.println("<tr><td><font color=white size=2>");
    out.println("<b>Instructions:</b>&nbsp;&nbsp;");
    out.println("Use the form below to manage your demo " + ((sess_activity_id == 0) ? "clubs" : "equipment") + ".&nbsp; " +
            "Once your demo " + ((sess_activity_id == 0) ? "clubs are" : "equipment is") + " defined in the ");
    out.println("system, your staff will be able to check them out to members. " + ((sess_activity_id == 0) ? "Clubs that are" : "Equipment that is") + " currently " +
            "<b>disabled</b> are shown below a divider and are displayed in gray.  To <b>remove</b> " + ((sess_activity_id == 0) ? "a club" : "equipment") + ", " +
            "click 'Remove'.<br><br>");
    out.println("To ADD " + ((sess_activity_id == 0) ? "a club" : "equipment") + ", type the description in the text box following 'Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + " Name' and click on the Add button.<br>");
    //out.println("To REMOVE a club, click on the club in the list and then click on the Remove button.<br>");
    out.println("To UPDATE " + ((sess_activity_id == 0) ? "a club" : "equipment") + ", click on the item in the list, make the changes and then click on the Update button.");
    out.println("</font></td></tr>");
    out.println("</table><br><br>");
    
    // OUTPUT ERROR MESSAGE
    if (!result_message.equals("")) {
        
        out.println("<span align=center style=\"align:center; background-color:#F5F5DC; border:1px solid #336633; padding:7px\">");
        out.println(result_message);
        out.println("</span><br><br>");
    }
    
    if (req.getParameter("demoConfig") != null) {

        out.println("<table border=0 align=center bgcolor=#F5F5DC>");

        // DEFINED CLUBS
        out.println("<form method=get name=frmDemoClubs>");
        out.println("<input type=hidden name=demoConfig>");
        out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Demo " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + "</font></b></td></tr>");
        out.println("<tr><td align=\"center\">");
        out.println("<select name=club_id size=15 onchange=\"selectClub(this.options[this.selectedIndex].value)\" style=\"width:545px\">");
//        out.println("<select name=club_id size=10 onchange=\"selectClub(this.options[this.selectedIndex].value)\" style=\"width:185px\">");

        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT dc.*, dcm.mfr, dct.type FROM demo_clubs dc " +
                    "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                    "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                    "WHERE dc.activity_id = '" + sess_activity_id + "' AND dc.inact = 0 " +
                    "ORDER BY enabled DESC, dcm.mfr, dct.type, name");

            boolean linePrinted = false;
                    
            while (rs.next()) {

                if (rs.getInt("enabled") == 0 && !linePrinted) {
                    linePrinted = true;
                    out.println("<option value=\"0\">------------------------------------------------------------------------------</option>");
                }
                out.print("<option value=\"" + rs.getInt("dc.id") + "\"");
                if (rs.getInt("enabled") == 0) out.print(" style=\"color:grey\"");
                if (club_id == rs.getInt("dc.id")) out.print(" selected");
                out.print(">" + rs.getString("dcm.mfr") + " - " + rs.getString("dct.type") + " - " + rs.getString("name") + "</option>");

            }

            stmt.close();

            out.println("</select>");
            out.println("</td></tr>");

            //out.println("<tr><td align=center><input type=submit value=\" Remove \" name=remove style=\"width:80px\" onclick=\"if (document.getElementById('club_id').selectedIndex == -1) return false; return confirm('Are you sure you want to delete the selected demo club?');\"></td></tr>");

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);

        } finally {

            try {rs.close();} catch (Exception ignore) {}
            try {stmt.close();} catch (Exception ignore) {}

        }
    

        if (club_id != 0 && req.getParameter("add") == null) {

            out.println("<tr><td align=center>");

            try {

                pstmt = con.prepareStatement("SELECT * FROM demo_clubs WHERE id = ?");
                pstmt.clearParameters();
                pstmt.setInt(1, club_id);
                rs = pstmt.executeQuery();

                out.println("<table align=center>");

                
                if (rs.next()) {
                   
                    String club_name = rs.getString("name");
                    String notes = rs.getString("notes");

                    int type_id = rs.getInt("type_id");     // equipment type id #
                    int mfr_id = rs.getInt("mfr_id");       // manufacturer id #

                    if (!club_name.equals("")) {

                       club_name = SystemUtils.filter(club_name);     // change special chars to HTML form
                    }
        
                    out.println("<tr><td align=\"center\" colspan=\"2\"><hr></td></tr>");
                    stmt = con.createStatement();
                    rs2 = stmt.executeQuery("SELECT * FROM demo_clubs_mfr WHERE id > 2 ORDER BY mfr");
                    
                    out.println("<tr><td align=\"right\"><font size=2>Manufacturers:</font></td><td><select name=\"mfr_id\" size=\"1\">");
                    out.println("<option value=\"1\"" + ((mfr_id == 1) ? " selected" : "") + ">None/Unknown</option>");
                    while (rs2.next()) {
                        out.println("<option value=\"" + rs2.getString("id") + "\"" + ((mfr_id == rs2.getInt("id")) ? " selected" : "") + ">" + rs2.getString("mfr") + "</option>");
                    }
                    out.println("<option value=\"2\"" + ((mfr_id == 2) ? " selected" : "") + ">Other</option>");
                    stmt.close();
                    
                    out.println("</select>&nbsp;");
                    out.println("<font size=\"2\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_demo_clubs?mfrCfg', 'mfrCfg', 'height=550, width=400, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no')\">Configure Manufacturers</a></font>");
                    out.println("</td></tr>");

                    // Gather and display the Demo Equipment Type selection box including all equipment types for this activity_id
                    pstmt = con.prepareStatement("SELECT * FROM demo_clubs_types WHERE activity_id = ? ORDER BY type");
                    pstmt.clearParameters();
                    pstmt.setInt(1, sess_activity_id);
                    rs2 = pstmt.executeQuery();

                    out.println("<tr><td align=\"right\"><font size=2>" + EQUIP_LABEL + " Type:</font></td><td align=\"left\"><select name=\"type_id\" size=\"1\">");
                    while(rs2.next()) {
                        out.println("<option value=\"" + rs2.getString("id") + "\"" + (type_id == rs2.getInt("id") ? " selected" : "") + ">" + rs2.getString("type") + "</option>");
                    }
                    out.println("</select>&nbsp;");
                    out.println("<font size=\"2\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_demo_clubs?typeCfg', 'typeCfg', 'height=550, width=400, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no')\">Configure " + (sess_activity_id == 0 ? "Club" : "Equipment") + " Types</a></font>");
                    out.println("</td></tr>");

                    pstmt.close();
                    
                    out.println("<tr><td align=\"right\"><font size=2>Name:</font></td><td><input type=text name=club_name maxlength=254 size=80 value=\"" + club_name + "\"></td></tr>");
                    out.println("<tr><td align=\"right\"><font size=2>Inventory #:</font></td><td><input type=text name=club_icn maxlength=16 size=80 value=\"" + ((rs.getString("icn")==null) ? "" : rs.getString("icn")) + "\">&nbsp;<font size=\"2\">(Optional - If used, must be unique!)</font></td></tr>");
                    
                    out.println("<tr><td align=\"right\"><font size=2>For Sale:</font></td><td><input type=\"checkbox\" name=\"for_sale\" value=\"1\"" + ((rs.getInt("for_sale") == 1) ? " checked" : "") + "></td></tr>");
                    out.println("<tr><td align=\"right\"><font size=2>Enabled:</font></td><td><input type=checkbox name=enabled value=\"1\"" + ((rs.getInt("enabled") == 1) ? " checked" : "") + "></td></tr>");
                    out.println("<tr><td align=\"right\" valign=\"top\"><font size=2>Notes:</font></td><td><textarea name=\"notes\" cols=\"28\" rows=\"2\">" + notes + "</textarea></td></tr>");
                }

                out.println("<tr><td align=center colspan=2>");
                out.println("<input type=submit name=update value=\"Update\" style=\"text-decoration:underline;width:80px\"> &nbsp;&nbsp; ");
                out.println("<input type=submit name=remove value=\"Remove\" style=\"text-decoration:underline;width:80px\" onclick=\"return confirm('This club will be removed from the system.  Are you sure you want to do this?');\"> &nbsp;&nbsp; ");
                out.println("<a href=\"/" + rev + "/servlet/Proshop_demo_clubs?demoConfig\" style=\"text-decoration:underline;color:black\"><button style=\"width:80px\">Reset</button></a>");
                out.println("</td></tr>");

            } catch (Exception exc) {

                SystemUtils.buildDatabaseErrMsg("Error loading up demo equipment information.", exc.getMessage(), out, false);

            } finally {

                try {rs.close();} catch (Exception ignore) {}
                try {pstmt.close();} catch (Exception ignore) {}
                try {rs2.close();} catch (Exception ignore) {}
                try {stmt.close();} catch (Exception ignore) {}
            }

            out.println("</table>");
            out.println("</td></tr>");
            
        } else {

            out.println("<tr><td align=\"center\"><hr></td></tr>");
            out.println("<tr><td align=\"center\">");
            out.println("<table align=\"center\" border=\"0\">");

            // Gather and display the Demo Equipment Manufacturers selection box (universal list for all activity_ids)
            try {
                stmt = con.createStatement();
                rs2 = stmt.executeQuery("SELECT * FROM demo_clubs_mfr WHERE id > 2 ORDER BY mfr");

                out.println("<tr><td align=\"right\"><font size=2>Manufacturer:</font></td><td align=\"left\"><select name=\"mfr_id\" size=\"1\">");
                out.println("<option value=\"1\">None/Unknown</option>");
                while (rs2.next()) {
                    out.println("<option value=\"" + rs2.getString("id") + "\">" + rs2.getString("mfr") + "</option>"); 
                }
                out.println("<option value=\"2\">Other</option>");
                stmt.close();
                
                out.println("</select>&nbsp;");
                out.println("<font size=\"2\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_demo_clubs?mfrCfg', 'mfrCfg', 'height=550, width=400, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no')\"\">Configure Manufacturers</a></font>");
                out.println("</td></tr>");

                stmt.close();

                pstmt = con.prepareStatement("SELECT * FROM demo_clubs_types WHERE activity_id = ? ORDER BY type");
                pstmt.clearParameters();
                pstmt.setInt(1, sess_activity_id);
                rs2 = pstmt.executeQuery();

                out.println("<tr><td align=\"right\"><font size=2>" + EQUIP_LABEL +  " Type:</font></td><td align=\"left\"><select name=\"type_id\" size=\"1\">");
                while(rs2.next()) {
                    out.println("<option value=\"" + rs2.getString("id") + "\">" + rs2.getString("type") + "</option>");
                }
                out.println("</select>&nbsp;");
                out.println("<font size=\"2\"><a href=\"javascript: void(0)\" onclick=\"window.open ('/" +rev+ "/servlet/Proshop_demo_clubs?typeCfg', 'typeCfg', 'height=550, width=400, toolbar=no, menubar=no, scrollbars=auto, resizable=yes, location=no directories=no, status=no')\">Configure " + (sess_activity_id == 0 ? "Club" : "Equipment") + " Types</a></font>");
                out.println("</td></tr>");

                pstmt.close();
                
            } catch (Exception exc) {
                SystemUtils.buildDatabaseErrMsg("Error loading up demo equipment information.", exc.getMessage(), out, false);
            } finally {
                try {rs2.close();} catch (Exception ignore) {}
                try {stmt.close();} catch (Exception ignore) {}
                try {pstmt.close();} catch (Exception ignore) {}
            }
            
            out.println("<tr><td align=right><font size=2>Name:&nbsp; </font></td><td align=\"left\"><input type=text size=80 name=club_name maxlength=254></font></td></tr>"); // Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + " 
            out.println("<tr><td align=right><font size=2>Inventory #:&nbsp; </font></td><td align=\"left\"><input type=text size=80 name=club_icn maxlength=16></font>&nbsp;<font size=\"2\">(Optional - If used, must be unique!)</font></td></tr>");

            out.println("<tr><td align=\"right\"><font size=2>For Sale:</font></td><td align=\"left\"><input type=\"checkbox\" name=\"for_sale\" value=\"1\"></td></tr>");
            out.println("<tr><td align=\"right\" valign=\"top\"><font size=2>Notes:</font></td><td align=\"left\"><textarea name=\"notes\" cols=\"28\" rows=\"2\"></textarea></td></tr>");

            out.println("<tr><td align=center colspan=\"2\"><input type=submit value=\"Add\" name=add style=\"text-decoration:underline;width:80px\" onclick=\"if (document.getElementById('club_name').value == '') return false;\"></td></tr>");

            out.println("</table>");
            out.println("</td></tr>");
        }
            
        out.println("</form>");
        out.println("</table>");

        out.println("<script type=\"text/javascript\">");
        out.println("function selectClub(pClubId) {");
        out.println(" f = document.forms[\"frmDemoClubs\"];");
        out.println(" f.club_id.value = pClubId;");
        out.println(" f.method = \"GET\";");
        out.println(" f.submit();");
        out.println("}");
        out.println("function showTees(pTeeId) {");
        out.println(" f = document.forms[\"frmTees\"];");
        out.println(" f.method = \"GET\";");
        out.println(" f.submit();");
        out.println("}");
        out.println("</script>");
    
        out.println("<br>");
        out.println("<center>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\"Cancel\" style=\"text-decoration:underline;width:90px\">");
        out.println("</form></center>");   
    }
    
    /*   don't need now that we have a menu tab
     
    out.println("<table width='75%' align=center>");
    out.println("<tr align=center style=\"font-weight:bold; size:14pt\"><td>Check In/Out</td><td>Reporting</td><td>Configuration</td></tr>");
    out.println("<tr><td>&nbsp;</td></tr>");
    out.println("<tr valign=top><td align=center>");
        out.println("<form><input type=hidden name=checkInOut><input type=submit value='Check Clubs Out'></form>");
        out.println("<br>");
        out.println("<form><input type=hidden name=getCheckIn><input type=submit value='Check Clubs In'></form>");
    out.println("</td><td align=center>");
        out.println("<form target=_dcReport><input type=hidden name=printClubs><input type=submit value='Print Club Listing'></form>");
        out.println("<form target=_dcReport><input type=hidden name=printClubUsage><input type=submit value='Print Club Usage'></form>");
        out.println("<form target=_dcReport><input type=hidden name=printClubsOut><input type=submit value='Print Club Out Listing'></form>");
    out.println("</td><td align=center>");
    out.println("<form><input type=hidden name=demoConfig><input type=submit value='Configure Demo Clubs'></form>");
    out.println("</td></tr></table>");
     */
    
    out.println("</body></html>");
    
 }  // end of doGet


 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    resp.setHeader("Pragma", "no-cache");
    resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
    resp.setDateHeader("Expires", 0);
    resp.setContentType("text/html");                               

    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);                      // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
    }
    
    
    String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
    int lottery = Integer.parseInt(templott);
    
    int sess_activity_id = (Integer)session.getAttribute("activity_id");
    
    
    manageClubs(req, con, out, lottery, sess_activity_id);
    
    
 } // end of doPost routine
 
 
 private void manageClubs(HttpServletRequest req, Connection con, PrintWriter out, int lottery, int sess_activity_id) {
    
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<form name=clubForm id=clubForm>");
    out.println("<input type=hidden name='' value=''>");
    out.println("<select size=10 name=club_id onselect=\"loadClub(document.forms['clubForm'].club_id.selectedIndex)\">");

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    try {
        
        pstmt = con.prepareStatement("SELECT * FROM demo_clubs WHERE activity_id = ? AND inact = 0 ORDER BY name");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            out.println("<option value=\"" + rs.getInt("club_id") + "\">" + rs.getString("club_name") + "</option>");
            
        }

        pstmt.close();

    } catch (Exception exc) {
        
        // handle error
    
    } finally {
    
        try {rs.close();} catch (Exception ignore){}
        try {pstmt.close();} catch (Exception ignore){}
        
    }
    out.println("</select>");
    
    out.println("<input type=text name=new_club_name value='' size=16 maxlength=32><br>");
 //   out.println("<input type=text name=new_club_icn value='' size=16 maxlength=16>");
    out.println("<input type=submit name=btnNewClub value='Add Club' style=\"text-decoration:underline;width:90px\">");
    out.println("");
    out.println("</form>");
     
    
 }
 
 
 private void printClubs(HttpServletRequest req, HttpServletResponse resp, Connection con, PrintWriter out, int lottery, int sess_activity_id) {
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    boolean toExcel = false;
    
    try{
       if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

          toExcel = true;
          resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       }
    }
    catch (Exception exc) {
    }

    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format    
       out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));
    }
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format
        SystemUtils.getProshopSubMenu(req, out, lottery);
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Club" : "Equipment") + " Listing</h2>");
        out.println("");
        out.println("");

        out.println("<table align=center><tr>");
        out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubs\">");
        out.println("<td><input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table><br>");
    }
   // out.println("<table align=center border=1 width=\"75%\">");
    out.println("<table align=center border=1 cellpadding=5 bgcolor=\"#F5F5DC\">");
    
    out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td>Manufacturer</td><td>" + ((sess_activity_id == 0) ? "Club " : "") + " Type</td><td>Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</td><td>ICN</td><td>For Sale</td><td>Enabled</td><td>Notes</td></tr>");
    
    try {

        pstmt = con.prepareStatement("SELECT dc.*, dct.type, dcm.mfr FROM demo_clubs dc " +
                "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                "WHERE dc.activity_id = ? AND dc.inact = 0 " +
                "ORDER BY enabled DESC, dcm.mfr, dct.type, name");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            out.println("<tr align=center><td align=left><font size=1>" + rs.getString("dcm.mfr") + "&nbsp;</font></td><td align=left><font size=1>" + rs.getString("dct.type") + "&nbsp;</font></td><td align=left><font size=1>&nbsp;" + rs.getString("name") + "&nbsp;</font></td><td><font size=1>" + ((rs.getString("icn")==null) ? "N/A" : rs.getString("dc.icn")) + "</font></td><td align=left><font size=1>" + ((rs.getInt("for_sale") == 1) ? "Yes" : "No") + "&nbsp;</font></td><td><font size=1>" + ((rs.getInt("enabled") == 1) ? "Enabled" : "Disabled") + "</font></td><td align=left width=\"200px\"><font size=1>" + rs.getString("notes").trim() + "&nbsp;</font></td></tr>");
            
        }

        pstmt.close();
        
    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up demo equipment information.", exc.getMessage(), out, false);
        
    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {pstmt.close();} catch (Exception ignore) {}
        
    }
     
    out.println("</table>");
    
    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format      
        out.println("<br><table align=center><tr>");
        out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubs\">");
        out.println("<td><input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table>");
    }
    out.println("</body></html>");
    
 }
 
 
 private void printClubUsage(HttpServletRequest req, HttpServletResponse resp, Connection con, PrintWriter out, int lottery, int sess_activity_id) {
     
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    boolean toExcel = false;
    
    try{
       if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

          toExcel = true;
          resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       }
    }
    catch (Exception exc) {
    }

    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format    
        out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));
    }
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    
    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format    
        SystemUtils.getProshopSubMenu(req, out, lottery);
  
        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Club" : "Equipment") + " Usage Report</h2>");
        out.println("");
        out.println("");
   
        out.println("<table align=center><tr>");
        out.println("<form><td><input type=button value=\"  Print  \" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubUsage\">");
        out.println("<td><input type=\"submit\" value=\" Excel \" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\" Home \" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table><br>");
    }
  //  out.println("<table align=center border=1 width=\"75%\">");
    out.println("<table align=center border=1 cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
    
    out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td>Manufacturer</td><td>" + ((sess_activity_id == 0) ? "Club " : "") + "Type</td><td>Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</td><td>ICN</td><td>Days Out</td></tr>");
    
    try {

        pstmt = con.prepareStatement(
                "SELECT COUNT(*) AS total, dc.icn, dc.name, dct.type, dcm.mfr " +
                "FROM demo_clubs_usage dcu " + 
                "LEFT OUTER JOIN demo_clubs dc ON dc.id = dcu.club_id " +
                "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                "WHERE dc.inact = 0 AND dc.activity_id = ? " +
                "GROUP BY dcu.club_id " + 
                "ORDER BY dcm.mfr, dct.type, dc.name");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);

        rs = pstmt.executeQuery();

        while (rs.next()) {
            
            out.println("<tr align=center><td align=left><font size =1>&nbsp;" + rs.getString("dcm.mfr") + "</font></td><td align=left><font size =1>&nbsp;" + rs.getString("dct.type") + "</font></td><td align=left><font size =1>&nbsp;" + rs.getString("name") + "</font></td><td><font size =1>" + ((rs.getString("icn")==null) ? "N/A" : rs.getString("icn")) + "</font></td><td><font size =1>" + rs.getInt("total") + "</font></td></tr>");
            
        }

        pstmt.close();
        
    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading demo club usage report.", exc.getMessage(), out, false);
        
    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {pstmt.close();} catch (Exception ignore) {}
        
    }
     
    out.println("</table>");
    
    if (!toExcel) {    
        out.println("<br><table align=center><tr>");
        out.println("<form><td><input type=button value=\"  Print  \" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubUsage\">");
        out.println("<td><input type=\"submit\" value=\" Excel \" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\" Home \" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table>");
    }
    out.println("</body></html>");
    
 }
 
 
 private void printClubsOut(HttpServletRequest req, HttpServletResponse resp, Connection con, PrintWriter out, int lottery, int sess_activity_id) {
        
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    boolean toExcel = false;
    
    try{
       if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

          toExcel = true;
          resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       }
    }
    catch (Exception exc) {
    }

    if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format    
        out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));
    }
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    if (!toExcel) {
        SystemUtils.getProshopSubMenu(req, out, lottery);

        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " Checked Out</h2>");
        out.println("");
        out.println("");

        out.println("<table align=center><tr>");
        out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubsOut\">");
        out.println("<td><input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table><br>");
    }
   // out.println("<table align=center border=1 width=\"75%\">");
    out.println("<table align=center border=1 cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

    out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td>Manufacturer</td><td>" + ((sess_activity_id == 0) ? "Club " : "") + "Type</td><td>Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</td><td>ICN</td><td>Name</td><td>Mem #</td>" + (sess_activity_id == 0 ? "<td>Bag #</td>" : "") + "<td>Date Out</td><td>Days Out</td></tr>");

    try {

        pstmt = con.prepareStatement(
                "SELECT dcu.*, dc.*, dcm.mfr, dct.type, CONCAT(m.name_first, ' ', m.name_last) AS memName, m.memNum, m.bag, " +
                "DATE_FORMAT(datetime_out, '%m/%d/%Y %l:%i %p') AS dateout," +
                "DATEDIFF(now(), datetime_out) AS days_out " +
                "FROM demo_clubs_usage dcu " +
                "LEFT OUTER JOIN demo_clubs dc ON dc.id = dcu.club_id " +
                "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                "LEFT OUTER JOIN member2b m ON m.username = dcu.username " +
                "WHERE datetime_in = '0000-00-00 00:00:00' AND dc.inact = 0 AND dc.activity_id = ? " +
                "ORDER BY datetime_out ASC");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);

        rs = pstmt.executeQuery();

        while (rs.next()) {

            out.println("<tr align=center><td align=left><font size =1>&nbsp;" + rs.getString("dcm.mfr") + "</font></td><td align=left><font size =1>&nbsp;" + rs.getString("dct.type") + "</font></td>" +
                    "<td align=left><font size =1>&nbsp;" + rs.getString("name") + "</font></td><td><font size =1>" + ((rs.getString("icn")==null) ? "N/A" : rs.getString("icn")) + "</font></td>" +
                    "<td align=left><font size =1>&nbsp;" + rs.getString("memName") + "</font></td><td><font size =1>" + rs.getString("memNum") + "</font></td>" +
                    (sess_activity_id == 0 ? "<td><font size =1>&nbsp;" + rs.getString("m.bag") + "</font></td>" : "") +
                    "<td><font size =1>" + rs.getString("dateout") + "</font></td><td><font size =1>" + rs.getInt("days_out") + "</font></td></tr>");

        }

        pstmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading demo clubs out report.", exc.getMessage(), out, false);

    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {pstmt.close();} catch (Exception ignore) {}

    }

    out.println("</table>");

    if (!toExcel) {
        out.println("<br><table align=center><tr>");
        out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"text-decoration:underline;width:90px\"></td>");
        out.println("</form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
        out.println("<input type=\"hidden\" name=\"excel\">");
        out.println("<input type=\"hidden\" name=\"printClubsOut\">");
        out.println("<td><input type=\"submit\" value=\"Excel\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form><td>&nbsp;</td>");
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<td><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline;width:90px\">");
        out.println("</td></form>");
        out.println("</tr></table>");
    }
    out.println("</body></html>");

 }

 private void printMemberHistory(HttpServletRequest req, HttpServletResponse resp, int lottery, int sess_activity_id, String club, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String member = "";

    boolean toExcel = false;
    boolean error = false;
    boolean histFound = false;

    // If member has not been selected, display member list
    if (req.getParameter("member_username") == null || req.getParameter("member_username").equals("")) {

        out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));

        SystemUtils.getProshopSubMenu(req, out, lottery);

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function movename(namewc) {");
        out.println(" array = namewc.split(':');"); // split string in to 2 (name, wc)
        out.println(" var name = array[0];");
        out.println(" var username = array[1];");
        out.println(" f = document.forms['frmSelectMem'];");
        out.println(" var member = f.member.value;");
        out.println(" f.member.value = name;");
        out.println(" f.member_username.value = username;");
        out.println("}");
        out.println("function submitForm() {");
        out.println(" f = document.forms['frmSelectMem'];");
        out.println(" if (f.member.value=='') {");
        out.println("  alert('You must select a member first.');");
        out.println("  return;");
        out.println(" }");
        out.println(" f.submit();");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<table border=\"0\" bgcolor=\"#FFFFFF\">");
        out.println("<td align=\"center\">");
        out.println("<br><h2 align=center>Member Demonstration " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " History</h2>");
        out.println("</td></tr>");
        out.println("</table>");


        out.println("<table align=center border=\"1\" cellpadding=\"10\" bgcolor=\"#F5F5DC\"><tr>");
        out.println("<form method=\"get\" name=\"frmSelectMem\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\">");
        out.println("<input type=\"hidden\" name=\"printMemberHistory\">");
        out.println("<tr><td align=\"center\" valign=\"center\">");
        out.println("<h3>Please select a member from the list to the right</h3><br>");
        out.println("<b>Member:&nbsp;<input type=\"text\" name=\"member\" onfocus=\"this.blur()\" ondblclick=\"alert('Choose a member using the list on the right.');\">&nbsp;&nbsp;");
        out.println("<input type=\"hidden\" name=\"member_username\" value=\"\">");
        out.println("<br><br><input type=button value=\"Run Report\" onclick=\"submitForm()\" name=btnSubmit style=\"width:100px\">");
        out.println("<input type=\"button\" value=\"Home\" onclick=\"location.href='/" + rev + "/servlet/Proshop_announce'\">");
        out.println("</td></form><form name=\"playerform\"><td align=\"center\">");

        alphaTable.nameList_simple(club, 20, false, out, con);      // Print name list

        out.println("</td></form></tr></table><br>");
        out.println("</center></font></body></html>");


    } else {    //  Member has been selected, display the report for the selected member

        // Get the selected user
        member = req.getParameter("member_username");

        try{
           if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

              toExcel = true;
              resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
           }
        }
        catch (Exception exc) {
        }

        if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format
            out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));
        }
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        if (!toExcel) {
            SystemUtils.getProshopSubMenu(req, out, lottery);

            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " Member History</h2>");

            out.println("<table align=center><tr>");
            out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"width:90px\"></td>");
            out.println("</form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\">");
            out.println("<input type=\"hidden\" name=\"printMemberHistory\">");
            out.println("<input type=\"hidden\" name=\"member_username\" value=\"" + member + "\">");
            out.println("<td><input type=\"submit\" value=\"Excel\" style=\"width:90px\">");
            out.println("</td></form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<td><input type=\"submit\" value=\"Home\" style=\"width:90px\">");
            out.println("</td></form>");
            out.println("</tr></table><br>");
        }

        try {

            //  Gather and display member information

            pstmt = con.prepareStatement(
                    "SELECT CONCAT(name_first, ' ', IF(name_mi <> '', CONCAT(name_mi, ' '), ''), name_last) AS memName, memNum, bag " +
                    "FROM member2b " +
                    "WHERE username = ?");
            pstmt.clearParameters();
            pstmt.setString(1, member);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<table align=center border=0 cellpadding=\"5\" bgcolor=\"#FFFFFF\">");
                out.println("<tr><td align=\"center\">Member: " + rs.getString("memName") + "&nbsp;&nbsp;Mem #: " + rs.getString("memNum") + "&nbsp;&nbsp;Bag #: " + (!rs.getString("bag").equals("") ? rs.getString("bag") : "N/A") + "</td></tr>");
                out.println("</table><br>");
            } else {
                error = true;
            }

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        // Start main output table
        out.println("<table align=center border=1 cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

        out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td>Manufacturer</td><td>" + ((sess_activity_id == 0) ? "Club " : "") + "Type</td><td>Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</td><td>ICN</td><td>Date Out</td><td>Date In</td><td>Days Out</td></tr>");


        try {

            //  Gather and list all past demo equipment usage by this member
            pstmt = con.prepareStatement(
                    "SELECT dcu.*, dc.*, dcm.mfr, dct.type, " +
                    "DATE_FORMAT(datetime_out, '%c/%d/%Y %l:%i %p') AS dateout," +
                    "IF (dcu.datetime_in <> '0000-00-00 00:00:00', DATE_FORMAT(dcu.datetime_in, '%c/%d/%Y %l:%i %p'), 'N/A') AS datein, " +
                    "IF (dcu.datetime_in <> '0000-00-00 00:00:00', DATEDIFF(dcu.datetime_in, dcu.datetime_out), '-1') AS days_out " +
                    "FROM demo_clubs_usage dcu " +
                    "LEFT OUTER JOIN demo_clubs dc ON dc.id = dcu.club_id " +
                    "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                    "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                    "LEFT OUTER JOIN member2b m ON m.username = dcu.username " +
                    "WHERE dcu.username = ? AND dc.inact = 0 AND dc.activity_id = ? " +
                    "ORDER BY datetime_out ASC");
            pstmt.clearParameters();
            pstmt.setString(1, member);
            pstmt.setInt(2, sess_activity_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                
                histFound = true;

                out.println("<tr align=center><td align=left><font size =1>&nbsp;" + rs.getString("dcm.mfr") + "</font></td><td align=left><font size =1>&nbsp;" + rs.getString("dct.type") + "</font></td>" +
                        "<td align=left><font size =1>&nbsp;" + rs.getString("name") + "</font></td><td><font size =1>" + ((rs.getString("icn")==null) ? "N/A" : rs.getString("icn")) + "</font></td>" +
                        "<td><font size =1>" + rs.getString("dateout") + "</font></td><td><font size =1>" + rs.getString("datein") + "</font></td>" +
                        "<td><font size =1>" + (rs.getInt("days_out") >= 0 ? rs.getInt("days_out") : "N/A") + "</font></td></tr>");
            }

            if (!histFound) {
                out.println("<tr><td align=\"center\" colspan=\"7\">No demo " + (sess_activity_id == 0 ? "club" : "equipment") + " history found for this member.</td></tr>");
            }

            pstmt.close();

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading member demo clubs history report.", exc.getMessage(), out, false);

        } finally {

            try {rs.close();} catch (Exception ignore) {}
            try {pstmt.close();} catch (Exception ignore) {}

        }

        out.println("</table>");

        if (!toExcel) {
            out.println("<br><table align=center><tr>");
            out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"width:90px\"></td>");
            out.println("</form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\">");
            out.println("<input type=\"hidden\" name=\"printMemberHistory\">");
            out.println("<input type=\"hidden\" name=\"member_username\" value=\"" + member + "\">");
            out.println("<td><input type=\"submit\" value=\"Excel\" style=\"width:90px\">");
            out.println("</td></form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<td><input type=\"submit\" value=\"Home\" style=\"width:90px\">");
            out.println("</td></form>");
            out.println("</tr></table>");
        }
        out.println("</font></body></html>");
    }

 }

 private void printClubHistory(HttpServletRequest req, HttpServletResponse resp, int lottery, int sess_activity_id, Connection con, PrintWriter out) {

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    int club_id = 0;

    boolean toExcel = false;
    boolean error = false;
    boolean histFound = false;

    // If member has not been selected, display member list
    if (req.getParameter("club_id") == null || req.getParameter("club_id").equals("")) {

        out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));

        SystemUtils.getProshopSubMenu(req, out, lottery);

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function submitForm() {");
        out.println(" f = document.forms['frmSelectClub'];");
        out.println(" if (f.club_id.value=='') {");
        out.println("  alert('You must select a member first.');");
        out.println("  return;");
        out.println(" }");
        out.println(" f.submit();");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

        out.println("<table border=\"0\" bgcolor=\"#FFFFFF\">");
        out.println("<td align=\"center\">");
        out.println("<br><h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " History</h2>");
        out.println("</td></tr>");
        out.println("</table>");


        out.println("<table border=0 align=center bgcolor=#FFFFFF>");
        out.println("<tr><td align=\"center\">");
        out.println("Pease select a club from the list below and click 'Run Report' to continue.");
        out.println("</td></tr>");

        out.println("<tr><td align=\"center\">");
        out.println("<table border=1 align=center bgcolor=#F5F5DC>");

        // DEFINED CLUBS
        out.println("<form method=get name=frmSelectClub>");
        out.println("<input type=\"hidden\" name=\"printClubHistory\">");
        out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Demo " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + "</font></b></td></tr>");
        out.println("<tr><td align=\"center\">");
        out.println("<select name=club_id size=15 style=\"width:400px\">");

        try {

            boolean linePrinted = false;

            pstmt = con.prepareStatement("SELECT dc.*, dcm.mfr, dct.type FROM demo_clubs dc " +
                    "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                    "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                    "WHERE dc.activity_id = ? AND dc.inact = 0 " +
                    "ORDER BY enabled DESC, dcm.mfr, dct.type, name");
            pstmt.clearParameters();
            pstmt.setInt(1, sess_activity_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                if (rs.getInt("enabled") == 0 && !linePrinted) {
                    linePrinted = true;
                    out.println("<option value=\"0\">------------------------------------------------------------------------------</option>");
                }
                out.print("<option value=\"" + rs.getInt("dc.id") + "\"");
                if (rs.getInt("enabled") == 0) out.print(" style=\"color:grey\"");
                if (club_id == rs.getInt("dc.id")) out.print(" selected");
                out.print(">" + rs.getString("dcm.mfr") + " - " + rs.getString("dct.type") + " - " + rs.getString("name") + "</option>");

            }

            pstmt.close();

            out.println("</select>");
            out.println("</td></tr>");

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);

        } finally {

            try {rs.close();} catch (Exception ignore) {}
            try {pstmt.close();} catch (Exception ignore) {}

        }

        out.println("<tr><td align=\"center\">");
        out.println("<br><br><input type=button value=\"Run Report\" onclick=\"submitForm()\" name=btnSubmit style=\"width:100px\">");
        out.println("<input type=\"button\" value=\"Home\" onclick=\"location.href='/" + rev + "/servlet/Proshop_announce'\">");
        out.println("</td></tr>");

        out.println("</form></table><br>");
        out.println("</td></tr></table>");
        out.println("</center></font></body></html>");


    } else {    //  Member has been selected, display the report for the selected member

        // Get the selected user
        club_id = Integer.parseInt(req.getParameter("club_id"));

        try{
           if (req.getParameter("excel") != null) {     // if user requested Excel Spreadsheet Format

              toExcel = true;
              resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
           }
        }
        catch (Exception exc) {
        }

        if (!toExcel) {  // Don't print header if user requested Excel Spreadsheet Format
            out.println(SystemUtils.HeadTitle("Proshop Demo Equipment Page"));
        }
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        if (!toExcel) {
            SystemUtils.getProshopSubMenu(req, out, lottery);

            out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

            out.println("<h2 align=center>Demonstration " + ((sess_activity_id == 0) ? "Clubs" : "Equipment") + " Club History</h2>");

            out.println("<table align=center><tr>");
            out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"width:90px\"></td>");
            out.println("</form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\">");
            out.println("<input type=\"hidden\" name=\"printClubHistory\">");
            out.println("<input type=\"hidden\" name=\"club_id\" value=\"" + club_id + "\">");
            out.println("<td><input type=\"submit\" value=\"Excel\" style=\"width:90px\">");
            out.println("</td></form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<td><input type=\"submit\" value=\"Home\" style=\"width:90px\">");
            out.println("</td></form>");
            out.println("</tr></table><br>");
        }

        try {

            //  Gather and display member information


            pstmt = con.prepareStatement("SELECT dc.name, dcm.mfr, dct.type FROM demo_clubs dc " +
                    "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                    "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                    "WHERE dc.id = ? ");
            pstmt.clearParameters();
            pstmt.setInt(1, club_id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                out.println("<table align=center border=0 cellpadding=\"5\" bgcolor=\"#FFFFFF\">");
                out.println("<tr><td align=\"center\">" + (sess_activity_id == 0 ? "Club" : "Equipment") + ":&nbsp;&nbsp;" + rs.getString("dcm.mfr") + " - " + rs.getString("dct.type") + " - " + rs.getString("dc.name") + "</td></tr>");
                out.println("</table><br>");
            } else {
                error = true;
            }

            pstmt.close();

        } catch (Exception exc) {
            error = true;
        }

        // Start main output table
        out.println("<table align=center border=1 cellpadding=\"5\" bgcolor=\"#F5F5DC\">");

        out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td>Member Name</td><td>Mem #</td><td>Date Out</td><td>Date In</td><td>Days Out</td></tr>");


        try {

            //  Gather and list all past demo equipment usage by this member
            pstmt = con.prepareStatement(
                    "SELECT dcu.*, " +
                    "CONCAT(m.name_first, ' ', IF(m.name_mi <> '', CONCAT(m.name_mi, ' '), ''), m.name_last) AS memName, m.memNum, " +
                    "DATE_FORMAT(dcu.datetime_out, '%c/%d/%Y %l:%i %p') AS dateout, " +
                    "IF (dcu.datetime_in <> '0000-00-00 00:00:00', DATE_FORMAT(dcu.datetime_in, '%c/%d/%Y %l:%i %p'), 'N/A') AS datein, " +
                    "IF (dcu.datetime_in <> '0000-00-00 00:00:00', DATEDIFF(dcu.datetime_in, dcu.datetime_out), '-1') AS days_out " +
                    "FROM demo_clubs_usage dcu " +
                    "LEFT OUTER JOIN member2b m ON m.username = dcu.username " +
                    "WHERE dcu.club_id = ? " +
                    "ORDER BY dcu.datetime_out ASC");
            pstmt.clearParameters();
            pstmt.setInt(1, club_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {

                histFound = true;

                out.println("<tr align=center><td align=left><font size =1>&nbsp;" + rs.getString("memName") + "</font></td><td align=center><font size =1>&nbsp;" + rs.getString("m.memNum") + "</font></td>" +
                        "<td><font size =1>" + rs.getString("dateout") + "</font></td><td><font size =1>" + rs.getString("datein") + "</font></td>" +
                        "<td><font size =1>" + (rs.getInt("days_out") >= 0 ? rs.getInt("days_out") : "N/A") + "</font></td></tr>");
            }

            if (!histFound) {
                out.println("<tr><td align=\"center\" colspan=\"5\">No history found for this " + (sess_activity_id == 0 ? "club" : "equipment") + ".</td></tr>");
            }

            pstmt.close();

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading demo club history report.", exc.getMessage(), out, false);

        } finally {

            try {rs.close();} catch (Exception ignore) {}
            try {pstmt.close();} catch (Exception ignore) {}

        }

        out.println("</table>");

        if (!toExcel) {
            out.println("<br><table align=center><tr>");
            out.println("<form><td><input type=button value=\"Print\" onclick=\"window.print()\" style=\"width:90px\"></td>");
            out.println("</form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" target=\"_blank\">");
            out.println("<input type=\"hidden\" name=\"excel\">");
            out.println("<input type=\"hidden\" name=\"printClubHistory\">");
            out.println("<input type=\"hidden\" name=\"club_id\" value=\"" + club_id + "\">");
            out.println("<td><input type=\"submit\" value=\"Excel\" style=\"width:90px\">");
            out.println("</td></form><td>&nbsp;</td>");
            out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
            out.println("<td><input type=\"submit\" value=\"Home\" style=\"width:90px\">");
            out.println("</td></form>");
            out.println("</tr></table>");
        }
        out.println("</font></body></html>");
    }

 }

 private void getCheckOut(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out, int lottery, int sess_activity_id) {

    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    Statement stmt = null;
    ResultSet rs = null;
    ResultSet rs2 = null;
    
    String club = (String)session.getAttribute("club");
    String member = (req.getParameter("member") == null) ? "" : req.getParameter("member");
    String cid = (req.getParameter("club_id") == null) ? "" : req.getParameter("club_id");
    int mfr_id = (req.getParameter("mfr_id") == null) ? 0 : Integer.parseInt(req.getParameter("mfr_id"));
    int type_id = (req.getParameter("type_id") == null) ? 0 : Integer.parseInt(req.getParameter("type_id"));
    
    int club_id = 0;
    int count = 0;
    int colCount = 1;      // # of clubs per column
    int i = 0;             // club counter
    int clubId = 0;
    int clubMfr = 0;
    int currMfr = 0;
    int clubTypeId = 0;
    String clubName = "";
    String clubType = "";
    String letter = "%";         // default is 'List All'
    String sortParams = "";
    String smfr = "-ALL-";
    String symbols = "";
    String icn = "";
    
    try {
        club_id = Integer.parseInt(cid);
    } catch (Exception ignore) {}
    
   
    // Determine if additional sorting is necessary
    if (type_id > 0) {
        sortParams += " AND type_id = '" + type_id + "'";
    }
    if (mfr_id > 0) {
        sortParams += " AND mfr_id = " + mfr_id;
    }
    
    //
    //  Get the number of demo clubs currently defined so we can build check box table below
    //
    try {

        pstmt = con.prepareStatement("" +
                 "SELECT COUNT(*) FROM demo_clubs WHERE enabled = 1" + sortParams + " AND id NOT IN (" +
                    "SELECT dc.id " + 
                    "FROM demo_clubs dc " + 
                    "LEFT OUTER JOIN demo_clubs_usage dcu ON dcu.club_id = dc.id " + 
                    "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username <> '' AND dcu.club_id > 0 AND activity_id = ? AND dc.inact = 0" +
                ") ORDER BY name");
        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        rs = pstmt.executeQuery();
        
       if (rs.next()) {
            
            count = rs.getInt(1);
        }

        pstmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up demo equipment information.", exc.getMessage(), out, false);
        
    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {pstmt.close();} catch (Exception ignore) {}
    }
    /*
    //
    //  Determine the number of demo clubs to list in each of the table columns
    //
    if (count > 2) {
       
       colCount = (count+2)/3;    // clubs per col - add 2 to allow for remainder (last col may contain less)
    }
    */
    
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<script type=\"text/javascript\">");
    out.println("<!--");
    out.println("function subletter(x) {");
    out.println(" document.playerform.letter.value = x;");
    out.println(" playerform.submit();");
    out.println("}");
    out.println("function movename(namewc) {");
    out.println(" array = namewc.split(':');"); // split string in to 2 (name, wc)
    out.println(" var name = array[0];");
    out.println(" f = document.forms['frmCheckInOut'];");
    out.println(" var member = f.member.value;");
    out.println(" f.member.value = name;");
    out.println("}");
    out.println("function submitForm() {");
    out.println(" f = document.forms['frmCheckInOut'];");
    out.println(" if (f.member.value=='') {");
    out.println("  alert('You must select a member first.');");
    out.println("  return;");
    out.println(" }");
    out.println(" f.submit();");
    out.println("}");
    out.println("// -->");
    out.println("</script>");  

    String user = (String)session.getAttribute("user");
    
    String mshipOpt = "";
    String mtypeOpt = "";
    /*
    String mshipOpt = (String)session.getAttribute("mshipOpt");
    String mtypeOpt = (String)session.getAttribute("mtypeOpt");

    if (mshipOpt.equals( "" ) || mshipOpt == null) {

        mshipOpt = "ALL";
    }
    if (mtypeOpt.equals( "" ) || mtypeOpt == null) {

        mtypeOpt = "ALL";
    }
     */
    
    
    out.println("<br>");

    out.println("<p align=center><font size=5>Checkout Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</font></p>");
    
    out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");

    out.println("<table align=center border=1 style=\"border: 1px solid #336633\" cellpadding=5 bgcolor=\"#336633\"><tr><td>");
    out.println("<font color=white size=2>Use the check boxes to select the " + ((sess_activity_id == 0) ? "club(s)" : "equipment") + ", then select the Member from the Name List.");
    out.println("</font></td></tr></table><br><br>");
    
    out.println("<table border=0 cellpadding=5 align=center>");
    out.println("<form name=\"sortForm\" method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
    out.println("<tr><td align=\"center\">");
    out.println("<b>Display Options</b>:&nbsp;");
    
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM demo_clubs_types WHERE id IN (SELECT type_id FROM demo_clubs WHERE enabled = 1 AND inact = 0 GROUP BY type_id) ORDER BY type");
        
        out.println( ((sess_activity_id == 0) ? "Club" : "Equipment") + " Type:&nbsp;<select name=\"type_id\" size=\"1\">");
        out.println("<option value=\"0\"" + (type_id == 0 ? " selected" : "") + ">-ALL-</option>");
        while (rs.next()) {
            
            int tempTypeId = rs.getInt("id");
            String tempType = rs.getString("type");
            
            out.println("<option value=\"" + tempTypeId + "\"" + (type_id == tempTypeId ? " selected" : "") + ">" + tempType + "</option>");
        }
        out.println("</select>&nbsp;");
        
    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }
         
    try {
        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT * FROM demo_clubs_mfr WHERE id > 2 AND id IN (SELECT mfr_id FROM demo_clubs WHERE enabled = 1 AND inact = 0 GROUP BY mfr_id) ORDER BY mfr");

        out.println("&nbsp; Manufacturer:&nbsp;<select name=\"mfr_id\" size=\"1\">");
        out.println("<option value=\"0\"" + ((mfr_id == 0) ? " selected" : "") + ">-ALL-</option>");
        out.println("<option value=\"1\"" + ((mfr_id == 1) ? " selected" : "") + ">None/Unknown</option>"); 
        while (rs.next()) {
            out.println("<option value=\"" + rs.getString("id") + "\"" + ((mfr_id == rs.getInt("id")) ? " selected" : "") + ">" + rs.getString("mfr") + "</option>");
        }
        out.println("<option value=\"2\"" + ((mfr_id == 2) ? " selected" : "") + ">Other</option>"); 
        out.println("</select>");
        
    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }
    
    out.println("&nbsp; <input type=\"submit\" name=\"checkInOut\" value=\"Update List\" style=\"text-decoration:underline;width:100px\">");
    out.println("&nbsp; <a href=\"/" + rev + "/servlet/Proshop_demo_clubs?checkInOut\" style=\"text-decoration:underline;color:black\"><button style=\"width:80px\">Reset</button></a>");
    out.println("</form>");
                    
    out.println("</td></tr>");
    out.println("</table>");
    
    if (count > 0) {
        //out.println("<table align=center border=1 style=\"border: 1px solid #336633\"><tr><td>");
        out.println("<table border=0 cellpadding=\"5\" align=center width=\"65%\"><tr>"); // bgcolor=#CCCCAA

        out.println("<form action=/" +rev+ "/servlet/Proshop_demo_clubs method=GET name=frmCheckInOut id=frmCheckInOut>");
        out.println("<input type=hidden name=doCheckOut>");

        out.println("<td align=left width=\"80%\" valign=top>");

        out.println("<table align=\"center\" width=\"100%\"><tr><td align=\"center\">");
        out.println("<tr><td align=center><b>Member:&nbsp;<input type=text name=member value=\"" + member + "\" onchange=\"document.forms['playerform'].member.value=this.value\" onfocus=\"this.blur()\" ondblclick=\"alert('Choose a member using the lists on the right.');\">&nbsp;&nbsp;");
        out.println("<input type=button value=\"Checkout\" onclick=\"submitForm()\" name=btnSubmit style=\"text-decoration:underline;width:100px\"><br><br>");
        out.println("</td></tr>");
        out.println("<tr><td>");
           out.println("<table border=1 cellpadding=\"5\" width=\"100%\" align=center valign=top bgcolor=\"#F5F5DC\">");  // table to hold the check boxes (demo clubs) 1 row, 3 cols

           out.println("<tr><td align=left valign=top>");  

           try {
               pstmt = con.prepareStatement("" +
                     "SELECT dc.id, name, mfr_id, icn, for_sale, dct.type FROM demo_clubs dc " +
                     "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " +
                     "LEFT OUTER JOIN demo_clubs_types dct ON dct.id = dc.type_id " +
                     "WHERE enabled = 1 AND inact = 0" + sortParams + " AND dc.activity_id = ? AND dc.id NOT IN (" +
                        "SELECT dc.id " + 
                        "FROM demo_clubs dc " + 
                        "LEFT OUTER JOIN demo_clubs_usage dcu ON dcu.club_id = dc.id " + 
                        "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username <> '' AND dcu.club_id > 0" +
                    ") ORDER BY dcm.mfr, dct.type, name");
               pstmt.clearParameters();
               pstmt.setInt(1, sess_activity_id);
               rs = pstmt.executeQuery();

               out.println("<div style=\"height:400px; overflow:auto\">");
               out.println("<table border=0 bgcolor=\"#F5F5DC\">");  // table for each column
               
               while (rs.next()) {

                   clubId = rs.getInt("id");
                   clubName = rs.getString("name");
                   clubMfr = rs.getInt("mfr_id");
                   clubType = rs.getString("dct.type");
                   
                   if (rs.getString("icn") != null) {
                       
                       icn = rs.getString("icn");
                       
                       if (!icn.equals("")) {
                           icn += " - ";
                       }
                       
                   } else {
                       icn = "";
                   }
                   
                   
                   if (clubMfr != currMfr) {
                       
                       currMfr = clubMfr;
                       
                       pstmt2 = con.prepareStatement("SELECT mfr FROM demo_clubs_mfr WHERE id = ?");
                       pstmt2.clearParameters();
                       pstmt2.setInt(1, clubMfr);
                       rs2 = pstmt2.executeQuery();
                       
                       if (rs2.next()) {
                           out.println("<tr><td align=\"left\"><font size=\"2\"><b>" + rs2.getString("mfr") + "</b></font></td></tr>");
                       }
                       
                       pstmt2.close();
                   }
                   
                   symbols = "";
                   
                   if (rs.getInt("for_sale") == 1) {
                       symbols += "&nbsp;<font color=\"green\"><b>$</b></font>";
                   }
                   
                   out.println("<tr>");  
                   out.println("<td align=\"right\"><input type=\"checkbox\" name=\"dc" + clubId + "\" value=\"1\"></td>");                         
                   out.println("<td align=\"left\"><font size=\"1\">&nbsp;" + clubType + " - " + icn + clubName + symbols + "&nbsp;&nbsp;</font></td>"); 
                   out.println("</tr>");

               }

               pstmt.close();
               
               out.println("</table>");
               out.println("</div>");

/*
               stmt = con.createStatement();
               rs = stmt.executeQuery("" +
                     "SELECT id, name, for_sale, notes FROM demo_clubs WHERE enabled = 1 AND inact = 0" + sortParams + " AND id NOT IN (" +
                        "SELECT dc.id " + 
                        "FROM demo_clubs dc " + 
                        "LEFT OUTER JOIN demo_clubs_usage dcu ON dcu.club_id = dc.id " + 
                        "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username <> '' AND dcu.club_id > 0" +
                    ") ORDER BY name");

               while (rs.next()) {

                   clubId = rs.getInt("id");
                   clubName = rs.getString("name");
                   
                   symbols = "";
                   
                   if (rs.getInt("for_sale") == 1) {
                       symbols += "&nbsp;<font color=\"green\"><b>$</b></font>";
                   }
                   

                   i++;       // bump # of clubs in this col

                   if (i > colCount) {         // can we add to this col ?

                      i = 1;
                      out.println("</table></td><td align=left valign=top width=\"33%\">");    // new main column
                      out.println("<table border=0 bgcolor=\"#F5F5DC\">");             // table for each column
                   }

                   out.println("<tr><td>");  
                   out.println("<input type=\"checkbox\" name=\"dc" + clubId + "\" value=\"1\">");                         
                   out.println("</td><td><font size=\"1\">&nbsp;" + clubName + symbols + "&nbsp;&nbsp;</font></td></tr>");                         

               }
               stmt.close();

               out.println("</table></td>");
*/
           } catch (Exception exc) {

               SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);

           } finally {

               try {rs.close();} catch (Exception ignore) {}
               try {pstmt.close();} catch (Exception ignore) {}
           }

           out.println("</td></tr></table>");      // end of check box table
           out.println("</td></tr>");
           out.println("<tr></tr>");
           out.println("</table>");
           out.println("</form>");
           out.println("</td><td align=\"center\">");

           /*      // remove the alphabit table and mship/mtype options to see if anyone complains
            *      //  if we restore this, then we need to pass the check box parms when user clicks a letter or option!!!
           if (req.getParameter("mshipopt") != null || req.getParameter("mtypeopt") != null) { 
                if (req.getParameter("mtypeopt") != null) {
                    mtypeOpt = req.getParameter("mtypeopt");
                   session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
               }
               if (req.getParameter("mshipopt") != null) {
                   mshipOpt = req.getParameter("mshipopt");
                   session.setAttribute("mshipOpt", mshipOpt);
               }
           }
            if (req.getParameter("letter") != null) {
                letter = req.getParameter("letter");
                if (letter.equals( "List All" )) {
                   letter = "%";
               } else {
                   letter = letter + "%";
               }
           }  
           */

           
           out.println("<form action=\"/" +rev+ "/servlet/Proshop_demo_clubs\" method=\"get\" name=\"playerform\" id=\"playerform\">");
           out.println("<input type=hidden name=checkInOut>");
           out.println("<input type=hidden name=club_id value=\"" + club_id + "\">");
           out.println("<input type=hidden name=member value=\"" + member + "\">");

           boolean enableAdvAssist = Utilities.enableAdvAssist(req);

           //
           //   Output the List of Names
           //
           alphaTable.nameList(club, letter, mshipOpt, mtypeOpt, false, null, enableAdvAssist, out, con);

            /*
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
           out.println("</td>");
            */
           out.println("</td></tr>");
           out.println("</form>");
           out.println("<tr>");
           out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
           out.println("<td align=center>");
           out.println("<input type=submit value=\" Cancel \" style=\"text-decoration:underline;width:90px\"></td></tr></form>");
           out.println("</table>"); 
           
    } else {
        
        // No clubs found! Display notification message
        out.println("<table border=0 cellpadding=5 align=center>");
        out.println("<form name=\"resetForm\" method=\"get\" action=\"/" + rev + "/servlet/Proshop_demo_clubs\">");
        out.println("<tr><td align=\"center\"><h3>No " + ((sess_activity_id == 0) ? "clubs" : "equipment") + " found!</h3></td></tr>");

        String type = "";
        
        try {
            
            pstmt = con.prepareStatement("SELECT mfr FROM demo_clubs_mfr WHERE id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, mfr_id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                smfr = rs.getString("mfr");
            }

            pstmt.close();

            pstmt = con.prepareStatement("SELECT type FROM demo_clubs_types WHERE id = ?");
            pstmt.clearParameters();
            pstmt.setInt(1, type_id);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                type = rs.getString("type");
            }

            pstmt.close();
            
        } catch (Exception exc) {
            SystemUtils.buildDatabaseErrMsg("Error loading up manufacturer information.", exc.getMessage(), out, false);
        } finally {
            try {rs.close();} catch (Exception ignore) {}
            try {pstmt.close();} catch (Exception ignore) {}
        }
        
        out.println("<tr><td align=\"center\"><br>No " + ((sess_activity_id == 0) ? "clubs were" : "equipment was") + " found using the specified filters (" + type + ", " + smfr + ").");
        out.println("<br><br>Please adjust the filters above and try again or reset the form below.");
        out.println("</td></tr>");
        out.println("<tr><td align=\"center\">");
        out.println("<br><input type=\"submit\" name=\"checkInOut\" value=\"Reset\" style=\"width:80px\">");
        out.println("</td></tr>");
        out.println("</form>");
        out.println("</table></body></html>");
    }
    out.println("</body></html>");    
 }
 
 
 
 private void doCheckOut(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out, int lottery, int sess_activity_id) {
    
     
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
     
    String member = (req.getParameter("member") == null) ? "" : req.getParameter("member");
    String notes = (req.getParameter("notes") == null) ? "" : req.getParameter("notes").trim();
    
    String username = SystemUtils.getUsernameFromFullName(member, con);
    String user = (String)session.getAttribute("user");
    String result_message = "";
    
    int club_id = 0;
    int count = 0;
    int i = 0;
    int done = 0;
   
    //
    //  Get the number of demo clubs currently defined so we can build check box table below
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                 "SELECT COUNT(*) FROM demo_clubs WHERE enabled = 1 AND id NOT IN (" +
                    "SELECT dc.id " + 
                    "FROM demo_clubs dc " + 
                    "LEFT OUTER JOIN demo_clubs_usage dcu ON dcu.club_id = dc.id " + 
                    "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username <> '' AND dcu.club_id > 0 AND dc.inact = 0" +
                ") ORDER BY name");
        
        if (rs.next()) {
            
            count = rs.getInt(1);
        }
        stmt.close();

    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }
    
    if (count < 1) count = 1;
    
    //
    //  Build an array to hold all the potential club ids (although only selected clubs will be stored here)
    //
    int [] clubIdA = new int [count];
    
    //
    //  Now populate the array with the selected club ids
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                 "SELECT id FROM demo_clubs WHERE enabled = 1 AND id NOT IN (" +
                    "SELECT dc.id " + 
                    "FROM demo_clubs dc " + 
                    "LEFT OUTER JOIN demo_clubs_usage dcu ON dcu.club_id = dc.id " + 
                    "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username <> '' AND dcu.club_id > 0 AND dc.inact = 0" +
                ") ORDER BY name");
        
        while (rs.next() && i < count) {
            
            club_id = rs.getInt(1);
            
             if (req.getParameter("dc" + club_id) != null) {      // if this club was checked
                
                clubIdA[i] = club_id;                             // save it in the array   
                i++;                                              // bump array index
             }
        }
        stmt.close();

    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }
    
    
    int found = 0;
    
    
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    
    /*          add later ????
     
    // check to see if this member has any demo clubs out now
    try {

        pstmt = con.prepareStatement("" +
                "SELECT dcu.*, dc.*, " + 
                    "DATE_FORMAT(datetime_out, '%m/%d/%Y %l:%i %p') AS dateout, " + 
                    "DATEDIFF(now(), datetime_out) AS days_out " +
                "FROM demo_clubs_usage dcu " + 
                "LEFT OUTER JOIN demo_clubs dc ON dc.id = dcu.club_id " + 
                "LEFT OUTER JOIN member2b m ON m.username = dcu.username " + 
                "WHERE datetime_in = '0000-00-00 00:00:00' AND dcu.username = ?  AND dc.inact = 0 " +
                "ORDER BY datetime_out ASC");
        
        pstmt.clearParameters();
        pstmt.setString(1, username);
        rs = pstmt.executeQuery();
        
        while (rs.next()) {

            if (found == 0) {
                
                out.println("<h3 align=center>Demo Clubs Currently Checked Out By Member</h3>");
                out.println("<table align=center border=1 width='75%'>");
                out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td></td><td>Club</td><td>ICN</td><td>Date Out</td><td>Days Out</td></tr>");
            }
            
            found++;
            
            out.println("<tr><!--<td>&nbsp;<a href=\"/"+rev+"/servlet/Proshop_demo_clubs?doCheckOut&doCheckIn="+ rs.getInt("club_id") + "&member=" + member + "&club_id=" + club_id + "\">Return Now</a>&nbsp;</td>-->");
            out.println("<td>" + rs.getString("name") + "</td>");
            out.println("<td>" + ((rs.getString("icn") == null) ? "" : rs.getString("icn")) + "</td>");
            out.println("<td>" + rs.getString("dateout") + "</td>");
            out.println("<td>" + rs.getInt("days_out") + "</td></tr>"); // hilight days under x in green, red if over
            
       }
        
       if (found > 0) out.println("</table>");
       pstmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up demo club information.", exc.getMessage(), out, false);

    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {pstmt.close();} catch (Exception ignore) {}

    }
     */
    
    
    if (found > 0 && req.getParameter("confirmCheckOut") == null) {
        
        out.println("<br><p align=center>This member already has " + found + " " + ((sess_activity_id == 0) ? "club(s)" : "item(s)") + " checked out.  Are you sure you want to check another " + ((sess_activity_id == 0) ? "club" : "item") + " out to this member?</p>");
        out.println("<br>");
        out.println("<table align=center><tr>");
        out.println("<form><td>");
        out.println("<input type=hidden name=doCheckOut>");
        out.println("<input type=hidden name=confirmCheckOut>");
        out.println("<input type=hidden name=member value=\"" + member + "\">");
        out.println("<input type=hidden name=club_id value=\"" + club_id + "\">");
        out.println("<input type=submit value=\" Yes \"></td>");
        out.println("<td>&nbsp; &nbsp;</td>");
        out.println("</form><form><td>");
        out.println("<input type=submit value=\"  No  \">");
        out.println("</td></form></tr></table>");
        
    } else {
    
        // Do the checkout
        try {

            pstmt = con.prepareStatement(
                    "INSERT INTO demo_clubs_usage (club_id, username, out_by, notes, datetime_out) VALUES (?, ?, ?, ?, now());");

            loop1:
            for (i=0; i<count; i++) {     
               
               club_id = clubIdA[i];         // get selected club id

               if (club_id > 0) {
                
                  pstmt.clearParameters();
                  pstmt.setInt(1, club_id);
                  pstmt.setString(2, username);
                  pstmt.setString(3, user);
                  pstmt.setString(4, notes);
                  pstmt.executeUpdate();
                  
                  done++;
                  
               } else {
                  break loop1;        // exit if reached end 
               }
            }             
            pstmt.close();

            if (done > 1) {          // if any clubs checked out
               
               result_message = "Demo " + ((sess_activity_id == 0) ? "clubs have" : "equipment has") + " been checked out to " + member + ".";
            } else if (done > 0) {
               
               result_message = "Demo " + ((sess_activity_id == 0) ? "club" : "equipment") + " has been checked out to " + member + ".";
            } else {
                
               result_message = "No " + ((sess_activity_id == 0) ? "clubs were" : "equipment was") + " checked out.  You must select one or more " + ((sess_activity_id == 0) ? "clubs" : "items") + " from the list (check the box).";
            }

        } catch (Exception exc) {

            SystemUtils.buildDatabaseErrMsg("Error loading demo clubs out report.", exc.getMessage(), out, false);

        } finally {

            try {pstmt.close();} catch (Exception ignore) {}

        }
    
    
        out.println("<br><br>");
        out.println("<h3 align=center>" + result_message + "</h3>");

        out.println("<br><br>");
        out.println("<center>");
        
        if (done == 0) {          // if No clubs checked out
               
           out.println("<a href=\"javascript:history.back(1)\">Return to Checkout</a>");
           out.println("<br><br>");
        }
        
        out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
        out.println("<input type=\"submit\" value=\" Home \" style=\"text-decoration:underline;width:90px\">");
        out.println("&nbsp;<a href=\"/" + rev + "/servlet/Proshop_demo_clubs?checkInOut\" style=\"text-decoration:underline;color:black\"><button style=\"width:120px\">Back to Checkout</button></a>");
        out.println("</form></center>");
    
    }
    
    out.println("</body></html>");
    
 }
 
 
 private void getCheckIn(HttpServletRequest req, Connection con, PrintWriter out, int lottery, int sess_activity_id) {

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
   
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<script type=\"text/javascript\">");
    out.println("<!--");
    out.println("function submitForm() {");
    out.println(" f = document.forms['frmCheckInOut'];");
    out.println(" if (f.member.value=='') {");
    out.println("  alert('You must select a member first.');");
    out.println("  return;");
    out.println(" }");
    out.println(" f.submit();");
    out.println("}");
    out.println("// -->");
    out.println("</script>");  

    out.println("<br>");

    out.println("<p align=center><font size=5>Check-in Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</font></p>");
    
    out.println("<table bgcolor=\"#336633\" align=center border=1 style=\"border: 1px solid #336633;\" bgcolor=\"#F5F5DC\"><tr><td><font color=white size=2>");
    out.println("&nbsp;&nbsp;Use the check boxes to select the item(s), then click 'Check Selected In' to complete the check-in.&nbsp;&nbsp;");
    out.println("</font></td></tr></table><br>");

        
    out.println("<table border=0 align=center width='75%'><tr><td>"); // bgcolor=#CCCCAA
    
    out.println("<table align=center border=1 width='100%' bgcolor=\"#F5F5DC\">");
    
    out.println("<form action=/" +rev+ "/servlet/Proshop_demo_clubs method=GET name=frmCheckIn id=frmCheckIn>");
    out.println("<input type=hidden name=doCheckIn>");
    
    out.println("<tr align=center style=\"background:#336633; color:white; font-size:10pt; font-weight:bold\"><td><td>Demo " + ((sess_activity_id == 0) ? "Club" : "Equipment") + "</td><td>ICN</td><td>Member</td><td>Mem #</td><td>Date Out</td><td>Days</td></tr>");
    
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT dcu.*, dc.*, dcm.mfr, CONCAT(m.name_first, ' ', m.name_last) AS memName, m.memNum, " + 
                "DATE_FORMAT(datetime_out, '%m/%d/%Y %l:%i %p') AS dateout, " + 
                "DATEDIFF(now(), datetime_out) AS days_out " +
                "FROM demo_clubs_usage dcu " +
                "LEFT OUTER JOIN demo_clubs dc ON dc.id = dcu.club_id " +
                "LEFT OUTER JOIN demo_clubs_mfr dcm ON dcm.id = dc.mfr_id " + 
                "LEFT OUTER JOIN member2b m ON m.username = dcu.username " + 
                "WHERE datetime_in = '0000-00-00 00:00:00' AND dc.inact = 0 " +
                "ORDER BY datetime_out ASC");
        
        while (rs.next()) {
           
            int checkoutId = rs.getInt("dcu.id");
            
            out.println("<tr align=center><td>");
            out.println("<input type=\"checkbox\" name=\"dc" +checkoutId+ "\" value=\"1\">");
            out.println("</td>");
            
            out.println("<td align=left><font size=1>&nbsp;" + rs.getString("name") + "</font></td><td><font size=2>" + ((rs.getString("icn") == null) ? "N/A" : rs.getString("icn")) + "</font></td>" +
                    "<td align=left><font size=2>&nbsp;" + rs.getString("memName") + "</font></td><td><font size=2>" + rs.getString("memNum") + "</font></td>" +
                    "<td><font size=2>" + rs.getString("dateout") + "</font></td><td><font size=2>" + rs.getInt("days_out") + "</font></td></tr>");          
        }

        stmt.close();

    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        
    } finally {

        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
        
    }
    
    out.println("</table>");
    out.println("</td></tr>");
    out.println("</table><br>");
 
    out.println("<table align=center><tr>");
    out.println("<td><input type=\"submit\" value=\"Check Selected In\" style=\"text-decoration:underline;width:140px\">");
    out.println("</td></form><td>&nbsp;</td>");
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
    out.println("<td><input type=\"submit\" value=\" Cancel \" style=\"text-decoration:underline;width:90px\">");     
    out.println("</td></form>");
    out.println("</tr></table>");
    
    out.println("</body></html>");
    
 }
 
 
 private void doCheckIn(HttpServletRequest req, HttpSession session, Connection con, PrintWriter out, int lottery, int sess_activity_id) {

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
     
    String user = (String)session.getAttribute("user");
    String result_message = "";
    
    int checkout_id = 0;
    int count = 0;
    int done_count = 0;
    int i = 0;
    
   
    //
    //  Get the number of demo clubs currently checked out so we can build check box table below
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT COUNT(*) " + 
                "FROM demo_clubs_usage " + 
                "WHERE datetime_in = '0000-00-00 00:00:00'"); 
        
        if (rs.next()) {
            
            count = rs.getInt(1);
        }
        stmt.close();

    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }
    
    if (count < 1) count = 1;
    
    //
    //  Build an array to hold all the potential club ids (although only selected clubs will be stored here)
    //
    int [] checkoutIdA = new int [count];
    
    //
    //  Now populate the array with the selected club ids
    //
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("" +
                "SELECT id " + 
                "FROM demo_clubs_usage " + 
                "WHERE datetime_in = '0000-00-00 00:00:00'"); 
        
        while (rs.next() && i < count) {
            
            checkout_id = rs.getInt(1);
            
            if (req.getParameter("dc" + checkout_id) != null) {      // if this club was checked
                
               checkoutIdA[i] = checkout_id;                             // save it in the array
               i++;                                              // bump array index
            }
        }
        stmt.close();

    } catch (Exception exc) {
        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
    } finally {
        try {rs.close();} catch (Exception ignore) {}
        try {stmt.close();} catch (Exception ignore) {}
    }

    //
    //     Check In the selected Clubs
    //
    try {
        
        pstmt = con.prepareStatement(
            "UPDATE demo_clubs_usage " +
            "SET datetime_in = now(), in_by = ? " + 
            "WHERE id = ?");
    
         loop1:
         for (i=0; i<count; i++) {     

            checkout_id = checkoutIdA[i];         // get selected club id

            if (checkout_id > 0) {

              pstmt.clearParameters();
              pstmt.setString(1, user);
              pstmt.setInt(2, checkout_id);

              pstmt.executeUpdate();       
              
              done_count++;                   // count number of clubs checked in

            } else {
               break loop1;        // exit if reached end 
            }
         }             
         pstmt.close();
    
    } catch (Exception exc) {

        SystemUtils.buildDatabaseErrMsg("Error loading up club information.", exc.getMessage(), out, false);
        
    } finally {

        try {pstmt.close();} catch (Exception ignore) {}      
    }
  
    if (done_count > 0) {       // not currently used - will be 1 unless db error
        result_message = done_count + " Demo " + ((sess_activity_id == 0) ? "clubs were" : "equipment was") + " checked in successfully.";
    } else {
        result_message = "No Demo " + ((sess_activity_id == 0) ? "clubs were" : "equipment was") + " checked in.";
    }
  
    out.println(SystemUtils.HeadTitle("Proshop Demo Clubs Page"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    SystemUtils.getProshopSubMenu(req, out, lottery);
    out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

    out.println("<p align=center>" + result_message + "</p>");
    
    out.println("<br><br>");
    out.println("<center>");
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_demo_clubs\">");
    out.println("<input type=\"hidden\" value=\"yes\" name=\"getCheckIn\">");
    out.println("<input type=\"submit\" value=\" Return To List \" style=\"text-decoration:underline;width:110px\">");
    out.println("</form></center>");
    out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_announce\">");
    out.println("<input type=\"submit\" value=\" Home \" style=\"text-decoration:underline;width:90px\">");
    out.println("</form></center>");
        
 }
 
} // end servlet public class
