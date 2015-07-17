/***************************************************************************************
 *   Admin_emailReport:  This servlet will display a list of members with bounced emails from the member table.
 *
 *   called by:  Admin_memlist
 *
 *
 *   created: 7/01/2008   Bob P.
 *
 *   last updated:
 *
 *      6/12/14   Allow access from the proshop users.  Link added to the proshop Email menu.
 *      7/24/08   Add a "Clear All Bounced FLags" button so admin user can clear all flags.
 *
 *
 ***************************************************************************************
 */

//thrird party imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

//foretees imports
import com.foretees.client.ScriptHelper;

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.ActionHelper;

import com.foretees.client.form.FormModel;
import com.foretees.client.form.FormRenderer;

import com.foretees.client.layout.LayoutHelper;

import com.foretees.client.table.Cell;
import com.foretees.client.table.Column;
import com.foretees.client.table.TableModel;
import com.foretees.client.table.TableRenderer;
import com.foretees.client.table.RowModel;

import com.foretees.common.Labels;
import com.foretees.common.ProcessConstants;

import com.foretees.common.help.Help;

import com.foretees.member.Member;
import com.foretees.common.Connect;

/**
***************************************************************************************
*
* This servlet will draw the table for listing the members based on the letter selection
* picked by the user
*
***************************************************************************************
**/

public class Admin_emailReport extends HttpServlet {

  private static String versionId = ProcessConstants.CODEBASE;

  /**
  ***************************************************************************************
  *
  * This method will forward the request and response onto the the post method
  *
  ***************************************************************************************
  **/

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
          doPost(req, resp);
        }


 /**
  ***************************************************************************************
  *
  * Prints the report for the members that have one or more email bounced flag set.
  *
  *
  ***************************************************************************************
  **/
  
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

    //
    //  Prevent caching so sessions are not mangled
    //
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    Connection con = null;                 // init DB objects
    ResultSet rs = null;
    PreparedStatement stmt1 = null;
    Statement stmt = null;

    int count1 = 0;
    int count2 = 0;
    int lottery = 0;

    HttpSession session = null;      
   
    session = SystemUtils.verifyProAdm(req, out);       // check for intruder - check both admin and proshop

    if (session == null) {
      return;
    }
    

    String club = (String)session.getAttribute("club");
    String user = (String)session.getAttribute("user");   

    if (!user.startsWith( "admin" )) {    // if proshop user
    
        String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
        lottery = Integer.parseInt(templott);
    }
    
    con = Connect.getCon(req);            // get DB connection

    if (con == null) {

      out.println(SystemUtils.HeadTitleAdmin("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      if (user.startsWith( "admin" )) {
          out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_announce\">Return</a>");
      } else {
          out.println("<BR><BR><a href=\"" +versionId+ "servlet/Proshop_announce\">Return</a>");          
      }
      out.println("</CENTER></BODY></HTML>");
      return;
    }

    // Check Feature Access Rights for current proshop user
    if (user.startsWith("proshop")) {

        if (!SystemUtils.verifyProAccess(req, "TOOLS_EMAIL", con, out)) {
            SystemUtils.restrictProshop("TOOLS_EMAIL", out);
            return;
        }
    }

    //
    //  Get Roster Sync indicator
    //
    int rsync = getRS(con);

    
    //
    //  if user click on the 'Clear All' button, clear all bounced flags
    //
    if (req.getParameter("clearall") != null) {

      //
      //    Clear all bounced flags
      //
      try {

         stmt = con.createStatement();              // create a statement

         stmt.executeUpdate("UPDATE member2b SET email_bounced = 0, email2_bounced = 0");

         stmt.close();

      }
      catch (Exception exc) {
      }

      out.println(SystemUtils.HeadTitleAdmin("Admin Bounce Flags"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Email Bounce Flags Reset</H3>");
      out.println("<br><font size=\"2\">");
      out.println("The Email Bounced Flags have been reset for ALL members.</font>");
      out.println("<BR><BR><a href=\"" +versionId+ "servlet/Admin_emailReport\">Return</a>");
      out.println("</CENTER></BODY></HTML>");

      return;
    }

    if (user.startsWith( "admin" )) {    // if admin user
       
        out.println(SystemUtils.HeadTitleAdmin("ForeTees Admin Members Page"));
        LayoutHelper.drawBeginPageContentWrapper(null, null, out);
        
    } else {
        
        out.println(SystemUtils.HeadTitle2("Proshop Update Member Emails"));

        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
        SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
    }

    
     try {
      
        //
        //  Get total number of members in db table with bounced flags set
        //
        stmt1 = con.prepareStatement (
                 "SELECT COUNT(*) FROM member2b where email_bounced = 1");

        stmt1.clearParameters();               // put the parm in stmt
        rs = stmt1.executeQuery();            // execute the prepared stmt

        if(rs.next()) {

           count1 = rs.getInt("COUNT(*)");
        }
        stmt1.close();

        stmt1 = con.prepareStatement (
                 "SELECT COUNT(*) FROM member2b where email2_bounced = 1");

        stmt1.clearParameters();               // put the parm in stmt
        rs = stmt1.executeQuery();            // execute the prepared stmt

        if(rs.next()) {

           count2 = rs.getInt("COUNT(*)");
        }
        stmt1.close();

        count1 = count1 + count2;

        out.println("<center><br><H2>Email Address Problem Report</H2>");
        out.println("<font size=\"3\">");
        out.println("<b>Number of bounced emails:&nbsp;&nbsp; " + count1 + "</b><br><br>");
        out.println("</font><font size=\"2\">");
         
        if (user.startsWith( "admin" )) {    // if admin user
        
            out.println("<table>");
        
        } else {
        
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\">");
        }
        out.println("<tr><td align=\"left\"><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("The following email addresses have encountered errors when ForeTees attempted to<br>");
        out.println("send messages to them.  These addresses are now flagged as bounced and are inactive.<br><br>");
        out.println("The most likely reason for this is an invalid or unavailable email address.<br><br>");
        
        if (rsync == 1) {
           
           out.println("To resolve this problem, the email address must be corrected in the system<br>");
           out.println("where the roster sync originates (web site or accounting system).<br><br>");
        
        } else {
           
           out.println("To resolve this problem, the email address must be corrected in the ForeTees member record.<br>");
           if (user.startsWith( "admin" )) {    // if admin user
               out.println("You can do that by updating the member record here in Admin, or have the member ");
           } else {
               out.println("If you know the correct email address for the member, you can change it by going to<br>"
                         + "Tools - Email - Member Email Settings, or have the member ");
           }
           out.println("<br>correct the email address in Settings when they login to ForeTees.<br><br>");
        }
           
        out.println("To reset the bounce flags for all members, click on the 'Clear ALL Bounced Flags' button below.<br>");
        out.println("This will allow us to attempt sending emails again, but will only be temporary if the real problem<br>has not been resolved.<br>");

        out.println("</td></tr></table>");
           
        out.println("<br><center><font size=\"2\">");
        out.println("<form method=\"post\" action=\"/" + SystemUtils.REVLEVEL + "/servlet/Admin_emailReport\">");
        out.println("<input type=\"hidden\" name=\"clearall\" id=\"clearall\" value=\"Yes\">");
        out.println("<input type=\"submit\" value=\"Clear ALL Bounced Flags\" style=\"text-decoration:underline; background:#8B8970\">");
        out.println("</form></font></center><br>");

        TableModel emails = new TableModel("Email Failure Report");
        
        if (user.startsWith( "admin" )) {    // if admin user
        
            emails.setPrintNumItems(false);
            emails.addColumn(new Column("memname", "Member Name"));
            emails.addColumn(new Column("memnum", "Member Number"));
            emails.addColumn(new Column("email", "Email Address"));
            
        } else {
            
            out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"5\" align=\"center\">");
            out.println("<tr>");
            out.println("<td bgcolor=\"#336633\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("Member Name</font></td>");
            out.println("<td bgcolor=\"#336633\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("Member Number</font></td>");
            out.println("<td bgcolor=\"#336633\" align=\"center\"><font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
            out.println("Email Address</font></td></tr>");
        }

        
        //
        //  Get each bad email address
        //
        stmt1 = con.prepareStatement (
           "SELECT name_last, name_first, name_mi, email, memNum, email2, email_bounced, email2_bounced " +
           "FROM member2b " +
           "WHERE email_bounced <> 0 OR email2_bounced <> 0 ORDER BY name_last, name_first");

        stmt1.clearParameters();               // put the parm in stmt
        rs = stmt1.executeQuery();            // execute the prepared stmt

        while(rs.next()) {

           String lname = rs.getString("name_last");
           String fname = rs.getString("name_first");
           String mi = rs.getString("name_mi");
           String email = rs.getString("email");
           String mNum = rs.getString("memNum");
           String email2 = rs.getString("email2");
           int bounce1 = rs.getInt("email_bounced");
           int bounce2 = rs.getInt("email2_bounced");
             
           StringBuffer mem_name = new StringBuffer(lname);        // get last name

           mem_name.append(", ");
           
           mem_name.append(fname);                           // first name

           if (!mi.equals( "" )) {
              mem_name.append(" ");
              mem_name.append(mi);
           }

           String name = mem_name.toString();                // convert to one string
           
           if (bounce1 == 1 && !email.equals( "" )) {

               if (user.startsWith( "admin" )) {    // if admin user
        
                    RowModel row = new RowModel();

                    row.add(name);
                    row.add(mNum);
                    row.add(email);

                    emails.addRow(row);
                    
               } else {
                   
                    out.println("<tr>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(name+ "</font></td>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(mNum+ "</font></td>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(email+ "</font></td></tr>");
               }
           }

           if (bounce2 == 1 && !email2.equals( "" )) {

               if (user.startsWith( "admin" )) {    // if admin user
        
                    RowModel row = new RowModel();

                    row.add(name);
                    row.add(mNum);
                    row.add(email2);

                    emails.addRow(row);
                    
               } else {
                   
                    out.println("<tr>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(name+ "</font></td>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(mNum+ "</font></td>");
                    out.println("<td><font color=\"#000000\" size=\"2\">");
                    out.println(email2+ "</font></td></tr>");
               }
           }
        }

        stmt1.close();
             
        if (user.startsWith( "admin" )) {    // if admin user
        
            TableRenderer.render(emails, out);
            
        } else {
            
            out.println("</font></td></tr></table>");
        }

        out.println("<br><center><font size=\"2\">");
        if (user.startsWith( "admin" )) {
            out.println("<form method=\"get\" action=\"/" + SystemUtils.REVLEVEL + "/servlet/Admin_members\">");
            out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
        } else {
            out.println("<form method=\"get\" action=\"/" + SystemUtils.REVLEVEL + "/servlet/Proshop_announce\">");
            out.println("<input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\">");
        }
        out.println("</form></font></center><br>");

        LayoutHelper.drawEndMainBodyContentWrapper(out);
        LayoutHelper.drawEndPageContentWrapper(out);

      }
      catch (Exception exc) {
        dbError(out, exc);
      }

  }


 // ***************************************************************
 //  getRS(Connection con)
 //
 //      Get the club's Roster Sync support indicator.
 //
 // ***************************************************************

 private int getRS(Connection con) {

    Statement stmt = null;
    ResultSet rs = null;
    int rsync = 0;

    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT rsync FROM club5");
        if (rs.next()) rsync = rs.getInt(1);
        stmt.close();
      
    } catch (Exception ignore) {
       
    } finally {

        if (rs != null) {
           try {
              rs.close();
           } catch (SQLException ignored) {}
        }

        if (stmt != null) {
           try {
              stmt.close();
           } catch (SQLException ignored) {}
        }
    }

    return (rsync);
 }


 // *********************************************************
 // Database Error
 // *********************************************************

 private void dbError(PrintWriter out, Exception exc) {

   out.println(SystemUtils.HeadTitleAdmin("Database Error"));
   out.println("<BODY><CENTER>");
   out.println("<BR><BR><H3>Database Access Error</H3>");
   out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
   out.println("<BR><BR>Exception: "+ exc.getMessage());
   out.println("<BR><BR>Please try again later.");
   out.println("<BR>If problem persists, contact customer support.");
   out.println("<BR><BR>");
   out.println("<a href=\"javascript:history.back(1)\">Return</a>");
   out.println("</CENTER></BODY></HTML>");

 }

}
