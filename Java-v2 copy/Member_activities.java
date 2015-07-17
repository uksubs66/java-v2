/*******************************************************************************
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 ******************************************************************************/



import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;


public class Member_activities extends HttpServlet {
    
 String rev = SystemUtils.REVLEVEL;
    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyMem(req, out);      // check for intruder

    if (session == null) return;

    Connection con = SystemUtils.getCon(session);               // get DB connection

    if (con == null) {

        resp.setContentType("text/html");

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();
        return;
    }

    //
    // Get needed vars out of session obj
    //
    String club = (String)session.getAttribute("club");
    String user = (String)session.getAttribute("user");
    String caller = (String)session.getAttribute("caller");

    int activity_id = (Integer)session.getAttribute("activity_id");

    int foretees_mode = 0;
    
    String stype_id = req.getParameter("type_id");
    int type_id = 0;
    
    String sgroup_id = req.getParameter("group_id");
    int group_id = 0;
    
    String sitem_id = req.getParameter("item_id");
    int item_id = 0;
    
    try {
        type_id = Integer.parseInt(stype_id);
    } catch (NumberFormatException ignore) {}
    
    
    try {
        group_id = Integer.parseInt(sgroup_id);
    } catch (NumberFormatException ignore) {}
    
    try {
        item_id = Integer.parseInt(sitem_id);
    } catch (NumberFormatException ignore) {}
    
    
    out.println("<!-- type_id=" + type_id + ", group_id=" + group_id + ", item_id=" + item_id + " -->");

    
    // 
    // START PAGE OUTPUT
    //
    out.println(SystemUtils.HeadTitle("Member Acivities"));
    out.println("<style>");
    out.println(".actLink { color: black }");
    out.println(".actLink:hover { color: #336633 }");
    //out.println(".playerTD {width:125px}");
    out.println("</style>");
    out.println("<body bgcolor=\"#CCCCAA\" text=\"#000000\" link=\"#336633\" vlink=\"#8B8970\" alink=\"#8B8970\">");
    SystemUtils.getMemberSubMenu(req, out, caller);        // required to allow submenus on this page



    //
    // DISPLAY A LIST OF AVAILABLE ACTIVITIES
    //
    out.println("<p align=center><b><font size=5 color=#336633><BR><BR>Available Activities</font></b></p>");

    out.println("<p align=center><b><font size=3 color=#000000>Select your desired activity from the list below.<br>NOTE: You can set your default activity under <a href=\"Member_services\" class=actLink>Settings</a>.</font></b></p>");

    out.println("<table align=center>");

    try {

        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT foretees_mode FROM club5 WHERE clubName <> '';");

        if (rs.next()) {
            foretees_mode = rs.getInt(1);
        }

        // if they have foretees then give a link in to the golf system
        if (foretees_mode != 0) {

            out.println("<tr><td align=center><b><a href=\"Member_jump?switch&activity_id=0\" class=linkA style=\"color:#336633\" target=_top>Golf</a></b></td></tr>"); // ForeTees

        }

        // build a link to any activities they have access to
        rs = stmt.executeQuery("SELECT * FROM activities " +
                               "WHERE parent_id = 0 " +
                               "ORDER BY activity_name");

        while (rs.next()) {

            out.println("<tr><td align=center><b><a href=\"Member_jump?switch&activity_id=" + rs.getInt("activity_id") + "\" class=linkA style=\"color:#336633\" target=_top>" + rs.getString("activity_name") + "</a></b></td></tr>");

        }

        stmt.close();

    } catch (Exception exc) {

        out.println("<p>ERROR:" + exc.toString() + "</p>");

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { stmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</table>");

    out.println("</body></html>");


/*

    out.println("<script>");
    
    out.println("function load_types() {");
    out.println(" try {document.forms['frmSelect'].item_id.selectedIndex = -1; } catch (err) {}");
    out.println(" document.forms['frmSelect'].group_id.selectedIndex = -1;");
    out.println(" document.forms['frmSelect'].submit();");
    out.println("}");
    
    out.println("function load_groups() {");
    out.println(" document.forms['frmSelect'].submit();");
    out.println("}");
    
    out.println("function load_times(id) {");
    out.println(" top.bot.location.href='Member_gensheets?id=' + id;");
    out.println("}");
    
    out.println("</script>");
   
    out.println("<form name=frmSelect>");
    
    // LOAD ACTIVITY TYPES
    out.println("<select name=type_id onchange=\"load_types()\">");
    
    if (type_id == 0) {
        
        out.println("<option>CHOOSE TYPE</option>");
        
    }
        
    try {

        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT * FROM activities WHERE parent_id = 0");

        while (rs.next()) {
            
            Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), type_id, out);

        }
        stmt.close();

    } catch (Exception exc) {

        out.println("<p>ERROR:" + exc.toString() + "</p>");

    }
    
    out.println("");
    out.println("</select>");
    
    
    // LOAD ACTIVITIES BY GROUP TYPE
    out.println("<select name=group_id onchange=\"load_groups()\">");
    
    if (type_id == 0) {
        
        out.println("<option>CHOOSE TYPE</option>");
        
    } else {
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = " + type_id);

            rs.last();
            if (rs.getRow() == 1) {
                group_id = rs.getInt("activity_id");
                out.println("<!-- ONLY FOUND 1 GROUP -->");
            } else {
                out.println("<option value=\"0\">CHOOSE...</option>");
            }
            
            rs.beforeFirst();
                
            while (rs.next()) {
                
                Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), group_id, out);
                
            }
            stmt.close();
            
        } catch (Exception exc) {
        
            out.println("<p>ERROR:" + exc.toString() + "</p>");
            
        }
        
    }
    
    out.println("");
    out.println("</select>");
    
    boolean do_load = false;
    
    if (group_id > 0 ) { //|| sitem_id != null
        
        // LOAD ACTIVITIES BY ITEM TYPE
        
        try {
            
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT activity_id, activity_name FROM activities WHERE parent_id = " + group_id);

            rs.last();
            if (rs.getRow() == 0) {
                
                // no sub groups found
                do_load = true;
                item_id = group_id;
                
            } else if (rs.getRow() == 1) {
                
                // single sub group found (pre select it)
                item_id = rs.getInt("activity_id");
                out.println("<!-- ONLY FOUND 1 ITEM -->");
                
            } else {
                
                out.println("<select name=item_id onchange=\"load_times(this.options[this.selectedIndex].value)\">");
                out.println("<option value=\"0\">CHOOSE...</option>");
                
            }
            
            if (!do_load) {
            
                rs.beforeFirst();

                while (rs.next()) {

                    Common_Config.buildOption(rs.getInt("activity_id"), rs.getString("activity_name"), item_id, out);

                }
                
            }
            stmt.close();
            
            out.println("");
            out.println("</select>");
            
        } catch (Exception exc) {
            
            out.println("<p>ERROR:" + exc.toString() + "</p>");
            
        }
        
    
    }
    
    out.println("</form>");
    
    out.println("<p><a href=\"Member_genrez\">Reset</a></p>");
    
    try {
        con.close();
    } catch (Exception ignore) {}
    
    
    if (do_load) out.println("<script>load_times(" + item_id + ")</script>");
    
    
    //out.println("<iframe name=ifSheet src=\"\" style=\"width:640px height:480px\"></iframe>");
*/
    
    out.close();
 }

 
 
 
 
 
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Connection con = null;                 // init DB objects
    PreparedStatement pstmt = null;
    Statement stmt = null;
    ResultSet rs = null;
     
    try {
        con = dbConn.Connect("demopaul");
    } catch (Exception ignore) {}
   
    
    String stype_id = req.getParameter("type_id");
    int type_id = 0;
    
    String sgroup_id = req.getParameter("group_id");
    int group_id = 0;
    
    String sitem_id = req.getParameter("item_id");
    int item_id = 0;
    
    try {
        type_id = Integer.parseInt(stype_id);
    } catch (NumberFormatException ignore) {}
    
    
    try {
        group_id = Integer.parseInt(sgroup_id);
    } catch (NumberFormatException ignore) {}
    
    try {
        item_id = Integer.parseInt(sitem_id);
    } catch (NumberFormatException ignore) {}
    
    
    out.println("<!-- type_id=" + type_id + ", group_id=" + group_id + ", item_id=" + item_id + " -->");
    
    out.println("<script>");
    
    out.println("function load_types() {");
    out.println(" try {document.forms['frmSelect'].item_id.selectedIndex = -1; } catch (err) {}");
    out.println(" document.forms['frmSelect'].group_id.selectedIndex = -1;");
    out.println(" document.forms['frmSelect'].submit();");
    out.println("}");
    
    out.println("function load_groups() {");
    out.println(" document.forms['frmSelect'].submit();");
    out.println("}");
    
    out.println("</script>");
   
    out.println("<form name=frmSelect>");
    
    // LOAD ACTIVITY TYPES
    out.println("<select name=type_id onchange=\"load_types()\">");
    
    if (type_id == 0) {
        
        out.println("<option>CHOOSE TYPE</option>");
        
    }
        
    try {

        stmt = con.createStatement();

        rs = stmt.executeQuery("SELECT * FROM activity_types");

        while (rs.next()) {
            
            Common_Config.buildOption(rs.getInt("type_id"), rs.getString("type_name"), type_id, out);

        }
        stmt.close();

    } catch (Exception exc) {

        out.println("<p>ERROR:" + exc.toString() + "</p>");

    }
    
    out.println("");
    out.println("</select>");
    
    
    // LOAD ACTIVITIES BY GROUP TYPE
    out.println("<select name=group_id onchange=\"load_groups()\">");
    
    if (type_id == 0) {
        
        out.println("<option>CHOOSE TYPE</option>");
        
    } else {
        
        try {

            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT group_id, group_name FROM activity_groups WHERE type_id = " + type_id);

            rs.last();
            if (rs.getRow() == 1) {
                group_id = rs.getInt("group_id");
                out.println("<!-- ONLY FOUND 1 GROUP -->");
            } else {
                out.println("<option value=\"0\">CHOOSE...</option>");
            }
            
            rs.beforeFirst();
                
            while (rs.next()) {
                
                Common_Config.buildOption(rs.getInt("group_id"), rs.getString("group_name"), group_id, out);
                
            }
            stmt.close();

        } catch (Exception exc) {

            out.println("<p>ERROR:" + exc.toString() + "</p>");

        }

    }
    
    out.println("");
    out.println("</select>");
    
    
    if (group_id > 0 ) { //|| sitem_id != null
        
        // LOAD ACTIVITIES BY ITEM TYPE
        out.println("<select name=item_id onchange=\"load_times()\">");

        if (group_id == 0) {

            out.println("<option value=\"0\">CHOOSE GROUP</option>");

        } else {

            try {

                stmt = con.createStatement();
                rs = stmt.executeQuery("SELECT item_id, item_name FROM activity_items WHERE group_id = " + group_id);

                rs.last();
                if (rs.getRow() == 1) {
                    item_id = rs.getInt("item_id");
                    out.println("<!-- ONLY FOUND 1 ITEM -->");
                } else {
                    out.println("<option value=\"0\">CHOOSE...</option>");
                }

                rs.beforeFirst();
            
                while (rs.next()) {

                    Common_Config.buildOption(rs.getInt("item_id"), rs.getString("item_name"), item_id, out);
                    
                }
                stmt.close();

            } catch (Exception exc) {

                out.println("<p>ERROR:" + exc.toString() + "</p>");

            }

        }
        
        out.println("");
        out.println("</select>");
    
    }
    
    out.println("</form>");
    
    out.println("<p><a href=\"Member_genrez\">Reset</a></p>");
    
    try {
        con.close();
    } catch (Exception ignore) {}
    
    out.close();
 }
 
}