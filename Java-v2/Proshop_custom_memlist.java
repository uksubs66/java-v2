/***************************************************************************************     
 *   Proshop_custom_memlist:  This servlet will provide an easy way to add custom displays
 *                            when a member selection list is needed.
 * 
 *         Currently used for Interlachen to display guest quota counts for a selected member.
 *
 *
 *   called by:  proshop menu and self
 *
 *   created: 3/07/2013   Bob P.
 *
 *   last updated:
 *
 *        5/01/13   Interlachen - changed the guest quota name from Preferred Guests to Advanced Guests.
 * 
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.client.SystemLingo;
import com.foretees.common.getActivity;
import com.foretees.common.Utilities;
import com.foretees.common.alphaTable;
import com.foretees.common.verifySlot;
import com.foretees.common.verifyCustom;

import com.foretees.common.Connect;

public class Proshop_custom_memlist extends HttpServlet {

                               
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //
 //****************************************************
 // Process the call from the menu 
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     
     doPost(req, resp);
 }

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }    
   
   Connection con = Connect.getCon(req);                     // get DB connection
   
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
   
    // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
       SystemUtils.restrictProshop("REPORTS", out);
       return;
   }
       
   String club = (String)session.getAttribute("club");      // get club name
   String user = (String)session.getAttribute("user");      // get user
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   int sess_activity_id = (Integer)session.getAttribute("activity_id");   // get Activity indicator (golf=0)
    
   //
   //  See if we are in the timeless tees mode
   //
   int tmp_tlt = (Integer)session.getAttribute("tlt");
   boolean IS_TLT = (tmp_tlt == 1) ? true : false;
   
   // setup our custom sytem text veriables
   SystemLingo sysLingo = new SystemLingo();
   sysLingo.setLingo(IS_TLT);
   
   
   String user_id = (req.getParameter("user_id") == null) ? "" : req.getParameter("user_id");  // get username if passed
   
   if (req.getParameter("memdata") != null) {        // if iframe loaded - display the member data
       
       memDataTable(club, user_id, out, con);    
       return;
   }
   
   
   //
   //   output the html page
   //
   out.println(SystemUtils.HeadTitle2("Proshop Custom Member List"));

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   if (club.equals("interlachen")) {
                
       out.println("<h2 align=center>Member Guest Quota Counts</h2>");
       
   } else if (club.equals("kiawahislandclub")) {
                
       out.println("<h2 align=center>Member Advance Time Quota Counts</h2>");
       
   } else {
       
       out.println("<h2 align=center>  ???   </h2>");    //  all others (future)
   }
    
   out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
   
   if (club.equals("interlachen")) {
                
      out.println("<p>Use this tool to display the current guest quota counts for a member.</p>");
      out.println("<p>Select the member you wish to check.</p>");
   
   } else if (club.equals("kiawahislandclub")) {
                
      out.println("<p>Use this tool to display the current advance time quota counts for a member.</p>");
      out.println("<p>Select the member you wish to check.</p>");
   
   } else {
       
       out.println("<p>  ???   </p>");    //  all others (future)
   }
    
   out.println("</font></td></tr></table><BR>");
   

   //  Output the member list form
   
   listMems(club, user_id, out, con);
   
   
   // insert an iframe to hold the member data table so it can run independently
   out.println("<iframe name=\"memdataiframe\" id=\"memdataiframe\" src=\"Proshop_custom_memlist?memdata\" width=\"90%\" scrolling=no frameborder=no></iframe>");

    
   out.println("</font>");
   out.println("<font size=\"2\"><BR>");
   out.println("<form method=\"get\" action=\"Proshop_announce\">");
   out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");

   out.println("</center>");
   out.println("</body>");
   out.println("</html>");
   out.close();

 }

 
 private void listMems(String club, String user_id, PrintWriter out, Connection con) {
    
    Utilities.proSlotScripts(out);
  
    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<form method=\"POST\" name=playerform onsubmit=\"false\">");        
    out.println("<tr bgcolor=\"#336633\"><td align=center><b><font color=white>&nbsp;Display Member Guest Quota Counts</font></b></td></tr>");
    out.println("<tr><td align=\"center\">");
    
   out.println("<font size=\"1\" face=\"Arial, Helvetica, Sans-serif\"><BR>");
   
   out.println("<input type=text name=DYN_search onkeyup=\"DYN_triggerChange()\" onkeypress=\"DYN_moveOnEnterKey(event); return DYN_disableEnterKey(event)\" onclick=\"this.select()\" value=\"Type Last Name\">"); // return DYN_disableEnterKey(event)
   
   out.println("&nbsp;&nbsp;(Name Search)<BR>Member Name (Member Number)</font><BR>");
    
    out.println("<input type=hidden name=user_id value=''>");   // username of member selected
    out.println("<select size=15 name=\"bname\" onclick=\"selectMem(this.options[this.selectedIndex].value)\" onkeypress=\"DYN_moveOnEnterKey(event); return false\" style=\"cursor:hand\" style=\"width:220px\">");

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi, memNum, username "
                + "FROM member2b "
                + "WHERE inact = 0 AND billable = 1 "
                + "ORDER BY last_only, memNum, name_first, name_mi";
    
    String last = "";
    String first = "";
    String mid = "";
    String mnum = ""; 
    String username = "";
    String dname = "";

    try {
        
        pstmt = con.prepareStatement(sql);
        pstmt.clearParameters();
        rs = pstmt.executeQuery();
        
        while (rs.next()) {
            
            last = rs.getString("name_last");          // full last name (with suffix, if appended)
            first = rs.getString("name_first");
            mid = rs.getString("name_mi");
            mnum = rs.getString("memNum");
            username = rs.getString("username");

            if (mid.equals("")) {
                dname = last + ", " + first + " (" +mnum+ ")";
            } else {
                dname = last + ", " + first + " " + mid + " (" +mnum+ ")";
            }

            
            out.print("<option value=\"" + username + "\"");
            
            if (user_id.equals(username)) out.print(" selected");
            
            out.println(">" + dname + "</option>");
        }
        pstmt.close();

    } catch (Exception exc) {
        
        // handle error
    
    } finally {
    
        try {rs.close();} catch (Exception ignore){}
        try {pstmt.close();} catch (Exception ignore){}
    }
    
    out.println("</select>");
    out.println("</td></tr></form>"); 
    
    out.println("<tr><td align=\"center\">");
    out.println("<a href=\"Proshop_custom_memlist\">Refresh Name List</a>");      
    out.println("</td></tr>"); 
    out.println("</table>");  
    
    out.println("<script type=\"text/javascript\">");
    out.println("function selectMem(user) {");
    out.println(" var fr = document.getElementById('memdataiframe');");          // get the iframe
    out.println(" frDoc = fr.contentDocument || fr.contentWindow.document;");    // get the iframe doc
    out.println(" f = frDoc.getElementById('memdataform');");                    // get the form within the iframe
    out.println(" f.user_id.value = user;");                                     // set the username of member selected
    out.println(" f.submit();");                                                 // kick the frame content
    out.println("}");
    out.println("</script>");
             
 }    // end of listMems
 
 
 private void memDataTable(String club, String user, PrintWriter out, Connection con) {
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    
    
    //
    //  Get current date
    //
    Calendar cal = new GregorianCalendar();             // get current date & time (Central Time)
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH);
    int day = cal.get(Calendar.DAY_OF_MONTH);
    
    long date = (year * 10000) + (month * 100) + day;         // get adjusted date for today
    
    
    String sql = "SELECT SUBSTRING_INDEX(name_last, '_', 1) AS last_only, name_last, name_first, name_mi "
                + "FROM member2b "
                + "WHERE username = ?";
    
    String last = "";
    String first = "";
    String mid = "";
    String dname = "";
    String quarter1_str = "";
    String quarter2_str = "";
    int quotaCount1 = 0;
    int quotaCount2 = 0;
    int num_guests1 = 0;
    int num_guests2 = 0;
    int advTimeCount1 = 0;
    int advTimeCount2 = 0;
    int advTimeLimit = 2;

    if (!user.equals("")) {    // if member provided
        
        try {

            pstmt = con.prepareStatement(sql);
            pstmt.clearParameters();
            pstmt.setString(1, user);
            rs = pstmt.executeQuery();

            if (rs.next()) {

                last = rs.getString("name_last");          // full last name (with suffix, if appended)
                first = rs.getString("name_first");
                mid = rs.getString("name_mi");
            }
            pstmt.close();
            
            if (club.equals("interlachen")) {
                
                //  Gather Guest Quota Counts for member and family
                
                int id = 0;
                int num_guests = 0;
                String name = "";
                String per = "";
            
                int quotaCount = verifySlot.getGuestQuotaCount(date, 0, con);      // get number of guest quotas for golf effective today (end date > today)

                if (quotaCount > 0) {        // if any

                    // Get guest quotas for golf

                    pstmt = con.prepareStatement(
                            "SELECT id, name, num_guests, per "
                            + "FROM guestqta4 "
                            + "WHERE edate >= ? AND activity_id = 0");     

                    pstmt.clearParameters();      
                    pstmt.setLong(1, date);

                    rs = pstmt.executeQuery();    

                    while (rs.next()) {

                        id = rs.getInt("id");
                        name = rs.getString("name");                 // Quota name
                        num_guests = rs.getInt("num_guests");        // Max guests allowed
                        per = rs.getString("per");                   // Per Member or Family

                        /*if (name.equals("Low Rate Guests")) {

                            quotaCount1 = verifySlot.getGuestQuotaCountMem(user, club, id, 0, con);    // get number of guests used for this member and this guest quota
                            num_guests1 = num_guests;
                            
                        } else */if (name.equals("Advanced Guests")) {    // was called Preferred Guests (in 2012)

                            quotaCount2 = verifySlot.getGuestQuotaCountMem(user, club, id, 0, con);    // get number of guests used for this member and this guest quota
                            num_guests2 = num_guests;
                        }

                    }  // end WHILE loop
                }            
            } else if (club.equals("kiawahislandclub")) {

                PreparedStatement pstmt2 = null;
                ResultSet rs2 = null;
                
                int curr_date = (int) Utilities.getDate(con);    // Time-zone adjusted date value

                int yy = curr_date / 10000;
                int mm = (curr_date - (yy * 10000)) / 100;
                int dd = curr_date - ((yy * 10000) + (mm * 100));

                int quarter1 = 0;
                int quarter2 = 0;
                int year1 = yy;
                int year2 = yy;
                int year_short1 = yy - 2000;
                int year_short2 = yy - 2000;

                String mship = "";

                if (mm <= 3) {
                    quarter1 = 1;
                    quarter2 = 2;
                    quarter1_str = "1/1/" + year_short1 + " - 3/31/" + year_short1;
                    quarter2_str = "4/1/" + year_short1 + " - 6/30/" + year_short1;
                } else if (mm <= 6) {
                    quarter1 = 2;
                    quarter2 = 3;
                    quarter1_str = "4/1/" + year_short1 + " - 6/30/" + year_short1;
                    quarter2_str = "7/1/" + year_short1 + " - 9/30/" + year_short1;
                } else if (mm <= 9) {
                    quarter1 = 3;
                    quarter2 = 4;
                    quarter1_str = "7/1/" + year_short1 + " - 9/30/" + year_short1;
                    quarter2_str = "10/1/" + year_short1 + " - 12/31/" + year_short1;
                } else {
                    quarter1 = 4;
                    quarter2 = 1;
                    year2++;
                    year_short2++;
                    quarter1_str = "10/1/" + year_short1 + " - 12/31/" + year_short1;
                    quarter2_str = "1/1/" + year_short2 + " - 3/31/" + year_short2;
                }

                try {
                    pstmt2 = con.prepareStatement("SELECT m_ship FROM member2b WHERE username = ?");
                    pstmt2.clearParameters();
                    pstmt2.setString(1, user);

                    rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {
                        mship = rs2.getString("m_ship");
                    }

                } catch (Exception e) {
                    mship = "";
                    Utilities.logError("Proshop_custom_memlist.advTimeDataTable - " + club + " - Failed looking up m_ship for user = " + user + " - Error = " + e.toString());
                } finally {
                    Connect.close(rs2, pstmt2);
                }
                
                if (!mship.equals("")) {
                    advTimeCount1 = verifyCustom.checkKiawahAdvanceTimes(user, mship, year1, quarter1, con);
                    advTimeCount2 = verifyCustom.checkKiawahAdvanceTimes(user, mship, year2, quarter2, con);
                }
            }
                
            //  other clubs' customs can go here !!!!!!!!!!
            
        } catch (Exception exc) {

            // handle error

        } finally {

            try {rs.close();} catch (Exception ignore){}
            try {pstmt.close();} catch (Exception ignore){}
        }

        //  create a name field for display purposes
        if (mid.equals("")) {
            dname = last + ", " + first;
        } else {
            dname = last + ", " + first + " " + mid;
        }
        
    } else {
        
        dname = "<i>Select Name Above</i>";
    }
    
    
    // Set up form to trigger this method by clicking the member in the name-list on the main page
    out.println("<form action=\"Proshop_custom_memlist\" method=\"POST\" name=\"memdataform\" id=\"memdataform\">");
    out.println("<input type=\"hidden\" name=\"user_id\" value=\"\">");
    out.println("<input type=\"hidden\" name=\"memdata\" value=\"yes\">");
    out.println("</form>");
    
    
    out.println("<table border=0 align=center bgcolor=#F5F5DC width=\"650\">");
    out.println("<tr><td align=center><HR>");
    out.println("<strong>" +dname+ "</strong><BR>");
    
    if (club.equals("interlachen")) {
    
//        out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;Currently has booked " +quotaCount1+ " of their " +num_guests1+ " allowed <strong>Low Rate Guests</strong>.</p>");

        out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;Currently has booked " +quotaCount2+ " of their " +num_guests2+ " allowed <strong>Advanced Tee Times</strong>.</p>");           

    } else if (club.equals("kiawahislandclub")) {
        
        int advTimeDisp1 = advTimeLimit - advTimeCount1;
        int advTimeDisp2 = advTimeLimit - advTimeCount2;
        
        if (advTimeDisp1 < 0) {
            advTimeDisp1 = 0;
        } else if (advTimeDisp1 > advTimeLimit) {
            advTimeDisp1 = advTimeLimit;
        }
        
        if (advTimeDisp2 < 0) {
            advTimeDisp2 = 0;
        } else if (advTimeDisp2 > advTimeLimit) {
            advTimeDisp2 = advTimeLimit;
        }
        
         out.println("<p align=\"center\">Currently has <span style=\"font-weight:bold;\">" + advTimeDisp1 + "</span> out of <span style=\"font-weight:bold;\">2</span> Advance Times remaining for the <span style=\"font-weight:bold;\">" + quarter1_str + "</span> quarter.</p>");
         out.println("<p align=\"center\">Currently has <span style=\"font-weight:bold;\">" + advTimeDisp2 + "</span> out of <span style=\"font-weight:bold;\">2</span> Advance Times remaining for the <span style=\"font-weight:bold;\">" + quarter2_str + "</span> quarter.</p>");
    } else {
        
        out.println("<p>&nbsp;&nbsp;&nbsp;&nbsp;  ????  </p>");      // other clubs (future)     
    }
        
    out.println("<BR></td></tr></table>");
    
 }      // end of memDataTable
 
 
 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>You must enter the last name, or some portion of it.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</CENTER></BODY></HTML>");
 }

}
