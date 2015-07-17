/***************************************************************************************     
 *   Proshop_cancel:  This servlet will process the Cancel Tee Time request from the Tee Sheet
 *                    when the pro clicks on the Trash Can in the Edit column.
 *
 *
 *   called by:  Proshop_sheet
 *
 *   created: 6/18/2015  BP
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Connect;
import com.foretees.common.verifySlot;
import com.foretees.common.parmSlot;


public class Proshop_cancel extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 //**************************************************
 // Display Cancel Tee Time Prompt
 //**************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
    
    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();
    
    Statement stmt = null;
    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = Connect.getCon(req);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }
    
    String club = (String)session.getAttribute("club");      // get club name
    String user = (String)session.getAttribute("user");      // get user
    
    int tid = 0;
    
    String tmp = "";
    String index = "0";
    
    if (req.getParameter("index") != null) index = req.getParameter("index");
    
    if (req.getParameter("tid") != null) tmp = req.getParameter("tid");
    
    try {
        tid = Integer.parseInt(tmp);
    }
    catch (NumberFormatException e) {
        return;
    }
        
    resp.setDateHeader("Expires",0);
    resp.setHeader("Pragma","no-cache");
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");
    resp.setContentType("text/html");

    
    // Check Feature Access Rights for current proshop user
    if (!SystemUtils.verifyProAccess(req, "TS_UPDATE", con, out)) {
        SystemUtils.restrictProshop("TS_UPDATE", out);
    }
    
    
    if (req.getParameter("conf") != null) {         // if Confirm received - go cancel the tee time
          
        doCancel(user, club, req, tid, out, con);
        return;
    }
    
    //
    //   Prompt user to confirm this request
    //
    out.println(SystemUtils.HeadTitle("Cancel Tee Time"));
   
    out.println("<script>");
    out.println("function openCanConf(pID,ind) {");
    out.println("document.location.href='Proshop_cancel?tid=' +pID+'&index=' +ind+'&conf';");
    out.println("}");
    out.println("</script>");
    
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
    out.println("<table cellpadding=0 cellspacing=0 style=\"border: 1px solid #336633\" align=center width=\"90%\" bgcolor=\"#F5F5DC\">");
    out.println("<tr><td align=center>");
    out.println("<br><br><H2>Cancel Tee Time</H2><br><br>");
    out.println("Are you sure you want to Cancel this tee time?<br><br><br>");
    out.println("</td></tr><tr>");
    
    out.println("<form>");
    out.println("<td align=center>");
    out.println("<input type=button value=\" YES - Continue \" onclick=\"openCanConf(" + tid + "," + index + ")\" style=\"background-color: #8B8970\">");
    out.println("&nbsp; &nbsp; &nbsp;");
    out.println("<input type=button value=\" NO - Return \" onclick=\"window.close()\" style=\"background-color: #8B8970\">");
    out.println("<br><br></td></form>");
    out.println("</tr></table>");
    
    out.println("</div>");
    out.println("</body>");
    out.println("</html>");
    
    
 }  // end of doGet


 
 //****************************************************************
 //
 //   User confirmed the cancel - go cancel the tee time
 //
 //****************************************************************
 //
 private void doCancel(String user, String club, HttpServletRequest req, int pTeeCurrID, PrintWriter out, Connection con) {
     

    ResultSet rs = null;
    PreparedStatement pstmt = null;
    
    long date = 0;
    
    int in_use = 0;
    int time = 0;
    int mm = 0;
    int yy = 0;
    int show1 = 0;
    int show2 = 0;
    int show3 = 0;
    int show4 = 0;
    int show5 = 0;
    int fb = 0;
    int jump = 0;
    int guest_id1 = 0;
    int guest_id2 = 0;
    int guest_id3 = 0;
    int guest_id4 = 0;
    int guest_id5 = 0;

    String returnCourse = "";
    String course = "";
    String p5 = "";
    String p5rest = "";
    String player1 = "";
    String player2 = "";
    String player3 = "";
    String player4 = "";
    String player5 = "";
    String p1cw = "";
    String p2cw = "";
    String p3cw = "";
    String p4cw = "";
    String p5cw = "";
    String day = "";
    String notes = "";
    String conf = "";

    String index = "0";
    
    if (req.getParameter("index") != null) index = req.getParameter("index");
    
    //
    //  parm block to hold the tee time parms
    //
    parmSlot slotParms = new parmSlot();          // allocate a parm block

    slotParms.club = club;                        // save club name

    
    //
    //   Get required parms for the requested tee time and call Proshop_slot to process the Cancel
    //
    try {
        
        pstmt = con.prepareStatement("SELECT * FROM teecurr2 WHERE teecurr_id = ?");
        pstmt.clearParameters();
        pstmt.setInt(1, pTeeCurrID);
        rs = pstmt.executeQuery();

        if (rs.next()) {

            date = rs.getLong("date");
            mm = rs.getInt("mm");
            yy = rs.getInt("yy");
            time = rs.getInt("time");
            fb = rs.getInt("fb");
            day = rs.getString("day");
            course = rs.getString("courseName");
            player1 = rs.getString("player1");
            player2 = rs.getString("player2");
            player3 = rs.getString("player3");
            player4 = rs.getString("player4");
            player5 = rs.getString("player5");
            p1cw = rs.getString("p1cw");
            p2cw = rs.getString("p2cw");
            p3cw = rs.getString("p3cw");
            p4cw = rs.getString("p4cw");
            p5cw = rs.getString("p5cw");
            guest_id1 = rs.getInt("guest_id1");
            guest_id2 = rs.getInt("guest_id2");
            guest_id3 = rs.getInt("guest_id3");
            guest_id4 = rs.getInt("guest_id4");
            guest_id5 = rs.getInt("guest_id5");
            show1 = rs.getInt("show1");
            show2 = rs.getInt("show2");
            show3 = rs.getInt("show3");
            show4 = rs.getInt("show4");
            show5 = rs.getInt("show5");
        }

        pstmt.close();
    
    }
    catch (Exception e1) {

        SystemUtils.buildDatabaseErrMsg(e1.getMessage(), e1.toString(), out, false);
        return;
    }
    
    
    //
    //   Make sure tee time is not busy - set in use if not
    //
    try {

          in_use = verifySlot.checkInUse(date, time, fb, course, user, slotParms, req);

    }
    catch (Exception e1) {

       SystemUtils.logError("Proshop_cancel Check in use flag failed - Exception: " + e1.getMessage());          

       in_use = 1;          // make like the time is busy
    }
    
    if (in_use != 0) {              // if time slot already in use

       out.println("<CENTER><BR><BR><H2>Tee Time Slot Busy</H2>");
       out.println("<BR><BR>Sorry, but this tee time slot is currently busy.<BR>");
       out.println("<BR>Please try again later.");
       out.println("<BR><BR>");
       out.println("<form>");
       out.println("<input type=button value=\" Return \" onclick=\"window.close()\" style=\"background-color: #8B8970\">");
       out.println("</form>");
       out.println("</CENTER>");
       out.close();
       return;
    }
    
    out.println("<HTML>");
    out.println("<HEAD>");
    out.println("<Title>Cancel Tee Time</Title>");
    out.println("<meta http-equiv=\"Refresh\" content=\"0; url=Proshop_slot?trashCan=yes&remove=yes&date=" +date+ "&time=" +time+ "&mm=" +mm+ "&yy=" +yy+ "&index=" +index+ ""
                 + "&returnCourse=" +returnCourse+ "&p5=" +p5+ "&p5rest=" +p5rest+ "&course=" +course+ "&player1=" +player1+ "player2=" +player2+ "player3=" +player3+ "player4=" +player4+ ""
                 + "player5=" +player5+ "&p1cw=" +p1cw+ "&p2cw=" +p2cw+ "&p3cw=" +p3cw+ "&p4cw=" +p4cw+ "&p5cw=" +p5cw+ "&guest_id1=" +guest_id1+ "&guest_id2=" +guest_id2+ ""
                 + "&guest_id3=" +guest_id3+ "&guest_id4=" +guest_id4+ "&guest_id5=" +guest_id5+ "&show1=" +show1+ "&show2=" +show2+ "&show3=" +show3+ "&show4=" +show4+ "&show5=" +show5+ ""
                 + "&day=" +day+ "&fb=" +fb+ "&notes=" +notes+ "&jump=" +jump+ "&conf=" +conf+ "\">");
      out.println("</HEAD>");
      out.println("<BODY><CENTER><BR>");
      out.println("<hr width=\"40%\">");
      out.println("<BR><BR><H3>Cancel Tee Time</H3>");
      out.println("<BR><BR>Click Continue to proceed with the cancel.");
      out.println("<BR><BR>");

      out.println("<font size=\"2\">");
      out.println("<form action=\"Proshop_slot\" method=\"post\">");
      out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
      
      out.println("<input type=\"hidden\" name=\"trashCan\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"remove\" value=\"yes\">");
      out.println("<input type=\"hidden\" name=\"date\" value=\"" +date+ "\">");
      out.println("<input type=\"hidden\" name=\"time\" value=\"" +time+ "\">");
      out.println("<input type=\"hidden\" name=\"mm\" value=\"" +mm+ "\">");
      out.println("<input type=\"hidden\" name=\"yy\" value=\"" +yy+ "\">");
      out.println("<input type=\"hidden\" name=\"index\" value=\"" +index+ "\">");
      out.println("<input type=\"hidden\" name=\"returnCourse\" value=\"" +returnCourse+ "\">");
      out.println("<input type=\"hidden\" name=\"p5\" value=\"" +p5+ "\">");
      out.println("<input type=\"hidden\" name=\"p5rest\" value=\"" +p5rest+ "\">");
      out.println("<input type=\"hidden\" name=\"course\" value=\"" +course+ "\">");
      out.println("<input type=\"hidden\" name=\"player1\" value=\"" +player1+ "\">");
      out.println("<input type=\"hidden\" name=\"player2\" value=\"" +player2+ "\">");
      out.println("<input type=\"hidden\" name=\"player3\" value=\"" +player3+ "\">");
      out.println("<input type=\"hidden\" name=\"player4\" value=\"" +player4+ "\">");
      out.println("<input type=\"hidden\" name=\"player5\" value=\"" +player5+ "\">");
      out.println("<input type=\"hidden\" name=\"p1cw\" value=\"" +p1cw+ "\">");
      out.println("<input type=\"hidden\" name=\"p2cw\" value=\"" +p2cw+ "\">");
      out.println("<input type=\"hidden\" name=\"p3cw\" value=\"" +p3cw+ "\">");
      out.println("<input type=\"hidden\" name=\"p4cw\" value=\"" +p4cw+ "\">");
      out.println("<input type=\"hidden\" name=\"p5cw\" value=\"" +p5cw+ "\">");
      out.println("<input type=\"hidden\" name=\"guest_id1\" value=\"" +guest_id1+ "\">");
      out.println("<input type=\"hidden\" name=\"guest_id2\" value=\"" +guest_id2+ "\">");
      out.println("<input type=\"hidden\" name=\"guest_id3\" value=\"" +guest_id3+ "\">");
      out.println("<input type=\"hidden\" name=\"guest_id4\" value=\"" +guest_id4+ "\">");
      out.println("<input type=\"hidden\" name=\"guest_id5\" value=\"" +guest_id5+ "\">");
      out.println("<input type=\"hidden\" name=\"show1\" value=\"" +show1+ "\">");
      out.println("<input type=\"hidden\" name=\"show2\" value=\"" +show2+ "\">");
      out.println("<input type=\"hidden\" name=\"show3\" value=\"" +show3+ "\">");
      out.println("<input type=\"hidden\" name=\"show4\" value=\"" +show4+ "\">");
      out.println("<input type=\"hidden\" name=\"show5\" value=\"" +show5+ "\">");
      out.println("<input type=\"hidden\" name=\"day\" value=\"" +day+ "\">");
      out.println("<input type=\"hidden\" name=\"fb\" value=\"" +fb+ "\">");
      out.println("<input type=\"hidden\" name=\"notes\" value=\"" +notes+ "\">");
      out.println("<input type=\"hidden\" name=\"jump\" value=\"" +jump+ "\">");
      out.println("<input type=\"hidden\" name=\"conf\" value=\"" +conf+ "\">");
      
      out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
    
 }    // end of doCancel
 
}
