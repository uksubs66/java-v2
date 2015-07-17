/***************************************************************************************
 *   Proshop_sheet_notes: This servlet will implement the notes for both tee times
 *                        and activities.
 *
 *
 *   Called by:     called by Proshop_sheet, Proshop_oldsheets, Proshop_gensheets
 *
 *
 *   Created:       2/18/2010
 *
 *
 *   Notes: Possible enhancement would be to add the username and a time stamp
 *          for each notes update, similar to a blog.
 *
 *
 *
 *   Last Updated:
 *
 *      11/21/2012  New doctype decleration 
 *       3/15/2011  Added FlxRez support
 *       9/01/2010  Added default message
 *       2/24/2010  Added limited access user control
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


public class Proshop_sheet_notes extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     doPost(req, resp);

 }

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

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
        out.close();

        return;
    }

    boolean tsNotesUpdate = SystemUtils.verifyProAccess(req, "TS_NOTES_UPDATE", con, out);

    int date = 0;

    String club = (String)session.getAttribute("club");
    //String user = (String)session.getAttribute("user");

    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    if (req.getParameter("date") == null) {

        out.println("Missing Parameters.");
        return;

    } else {

        String tmp = req.getParameter("date");
        date = Integer.parseInt(tmp);

    }

    // set the default message
    String DEFAULT_MESSAGE = "";
    if (sess_activity_id == 0) {

        DEFAULT_MESSAGE = "NOTICE: This area is intended for use by the golf shop only. These notes are not viewable by members. You can create Member Notices by going to System Config > Member Notices and you can define exactly when and where your message is displayed.";

    } else {

        DEFAULT_MESSAGE = "NOTICE: This area is intended for use by the staff only. These notes are not viewable by members.";

    }

    String notes = (req.getParameter("notes") == null) ? "" : req.getParameter("notes").trim();

    if (notes.contains(DEFAULT_MESSAGE)) notes = "";

    boolean no_notes = notes.equals(""); // no notes is true if empty


    if (tsNotesUpdate && req.getParameter("todo") != null && req.getParameter("todo").equals("update") && !notes.equals("")) {

        // update the notes
        try {

            pstmt = con.prepareStatement (
                        "INSERT INTO activity_sheet_notes " +
                        "(activity_id, date, notes) VALUES (?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE notes = VALUES(notes)");
            
            // "UPDATE activity_notes SET notes = ? WHERE activity_id = ? AND date = ?"
                                                
            pstmt.clearParameters();
            pstmt.setInt(1, sess_activity_id);
            pstmt.setInt(2, date);
            pstmt.setString(3, notes);
            pstmt.executeUpdate();

            pstmt.close();

        } catch (Exception exc) {
            
            Utilities.logError("Proshop_sheet_notes: Error Saving Notes! club=" + club + ", date=" + date + ", activity_id=" + sess_activity_id + ", err=" + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

    }

    // load the notes for display
    try {

        // try to load notes - if exist then display and allow editing - if none exists allow editing (which will makes new)
        pstmt = con.prepareStatement ("SELECT * FROM activity_sheet_notes WHERE activity_id = ? AND DATE_FORMAT(date, '%Y%m%d') = ?");

        pstmt.clearParameters();
        pstmt.setInt(1, sess_activity_id);
        pstmt.setInt(2, date);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {
            
            notes = rs.getString("notes");
            no_notes = notes.equals("");
            
        }

    } catch (Exception exc) {
        
        Utilities.logError("Proshop_sheet_notes: Error Loading Notes! club=" + club + ", date=" + date + ", activity_id=" + sess_activity_id + ", err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }
    
    //String notesBGcolor = (notes.equals("")) ? "white" : "yellow";


    out.println(SystemUtils.DOCTYPE); // "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
  //out.println("<meta http-equiv=\"Content-Type\" content=\"text/html;charset=iso-8859-1\">");
    out.println("<html>");
    out.println("<head>");
    out.println("<style type=\"text/css\">");
    out.println("html body {");
    out.println("  margin-left:0;");
    out.println("  margin-right:0;");
    out.println("  margin-top:0;");
    out.println("  margin-bottom:0;");
    out.println("  font-size:14px;");
    out.println("  font-family:arial;");
    out.println("}");
    out.println(".btnSaveDefault {");
    out.println("  width: 475px;");
    out.println("  height: 25px;");
    out.println("  font-size: 14px;");
    out.println("  font-family: arial;");
    out.println("  color: black;");
    out.println("  font-weight: normal;");
    out.println("  background-color: #F5F5DC");
    out.println("");
    out.println("");
    out.println("}");
    out.println(".btnSaveChanged {");
    out.println("  width: 475px;");
    out.println("  height: 27px;");
    out.println("  font-size: 14px;");
    out.println("  font-family: arial;");
    out.println("  color: black;");
    out.println("  font-weight: bold;");
    out.println("  background-color: yellow");
    out.println("");
    out.println("");
    out.println("}");
    out.println("</style>");
     /*
    out.println("<script type=\"text/javascript\">");
     out.println("function fTNotes_save(date) {");
     out.println(" var iframe = document.getElementById('fraTSNotes');");
     out.println(" var doc = null;");
     out.println(" if (iframe.contentDocument) {");         // Firefox, Safari, Opera
     out.println("  doc = iframe.contentDocument;");
     out.println(" } else if (iframe.contentWindow) {");    // IE
     out.println("  doc = iframe.contentWindow;");
     out.println(" } else if (iframe.document) {");         // last ditch effor?
     out.println("  doc = iframe.document;");
     out.println(" }");
     out.println(" if (doc == null) {");
     out.println("  throw 'Unable to save tee sheet notes.  Your browser does not seem to be supported.';");
     out.println(" } else {");
     out.println("  doc.location.href='Proshop_sheet_notes?date='+date+'&activity_id=0';");
     out.println(" }");
     out.println("}");
     out.println("function fTNotes_show() {");
     out.println(" document.getElementById('fTSNotes').style.display='block'");
     out.println("}");
     out.println("function fTNotes_hide() {");
     out.println(" document.getElementById('fTSNotes').style.display='none'");
     out.println("}");
     out.println("");
     out.println("");
     out.println("");
     out.println("");
    out.println("</script>");
     */
    out.println("</head>");

    out.println("<body " + ((no_notes) ? "onload=\"top.bot.fTNotes_hide()\"" : "") + ">");

    out.println("<form method=\"post\" name=\"frmTSNotes\" id=\"frmTSNotes\">");
     out.println("<input type=\"hidden\" value=\"update\" name=\"todo\">");
     // the onkeypress does most of the effect I wanted but
     // the onchange will detect the keys that (IE, Safari) ignore like 'Delete' and 'backspace' when erasing selected text.


     out.print("<div align=center>" +
             "<textarea name=\"notes\" style=\"width:100%; height:" +
             ((!tsNotesUpdate) ? "135px" : "105px") + ";" +
             "font-family:verdana;font-size:12px;" +
             ((no_notes) ? "color:grey;" : "") +
             "border:1px solid darkGreen\" " + ((!tsNotesUpdate) ? "READONLY" : "") + " " + // onfocus=\"blur()\"
             "onkeypress=\"document.getElementById('btnSaveNotes').className='btnSaveChanged';this.style.color='black'\" " +
             "onchange=\"document.getElementById('btnSaveNotes').className='btnSaveChanged';this.style.color='black'\" " +
             ((no_notes) ? "onfocus=\"this.select()\">" : ">"));


     // add any customs here

     out.print(((no_notes) ? DEFAULT_MESSAGE : notes));

         //  background-color:" + notesBGcolor + "    onkeypress=\"document.getElementById('btnSaveNotes').setAttribute('class', 'btnSaveChanged')\"
     
     out.println("</textarea></div>");

     if (tsNotesUpdate) {
         out.println("<div style=\"width:1px;height:5px\"></div>");
         out.println("<div align=center><input type=\"submit\" value=\"" + ((sess_activity_id == 0) ? " Save Tee Sheet Notes " : "  Save Notes  ") + "\" class=\"btnSaveDefault\" id=\"btnSaveNotes\"></div>"); // onclick=\"fTNotes_save()\"
     }
    out.println("</form>");

    out.println("</body>");
    out.println("</html>");
     
 }

} // end servlet public class
