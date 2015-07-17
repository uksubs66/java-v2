/***************************************************************************************
 *   Common_report_members: This servlet will ouput a list of the member roster.
 *
 *
 *   Called by:     Admin report memu
 *                  Proshop report menu
 *
 *
 *   Created:       1/29/2007
 *
 *
 *   Last Updated:
 *
 *      4/06/2007  Add processing for 'inactive' flag in member2b.                  
 *
 *                  
 ***************************************************************************************
 */

// standard java imports
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.*;


public class Common_report_members extends HttpServlet {
    
    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)
    boolean g_debug = true;
    
 
 //****************************************************
 // Process the get - initial call from menu
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


   //
   //  Prevent caching of this page
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyProAdm(req, out);    // check for intruder

   if (session == null) {
      return;
   }
     
   String user = (String)session.getAttribute("user");               // get user name

   Connection con = SystemUtils.getCon(session);                   // get DB connection
   if (con == null) {
       displayDatabaseErrMsg(user, "Cannot establish connection.", "", out);
       return;
   }

   String templott = "";     
   int lottery = 0;
     
   if (user.startsWith( "proshop" )) {
      templott = (String)session.getAttribute("lottery");        // get lottery support indicator
      lottery = Integer.parseInt(templott);
   }


   // start ouput
   out.println(SystemUtils.HeadTitle("Common - Member Roster Report"));
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
     
   if (user.startsWith( "proshop" )) {
      SystemUtils.getProshopSubMenu(req, out, lottery);                // required to allow submenus on this page
   }

   out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\"><center>");

   // start main table for this page
   out.println("<table border=\"0\" align=\"center\">");
   out.println("<tr><td>");
   out.println("<font size=\"2\">");

   // output instructions
   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\" align=\"center\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\"><b>Member Roster Report</b></font><br>");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<br>This report will list all members currently in the Roster.<br>");
   out.println("<br>Select the report options below, then click");
   out.println("<br>on <b>Go</b> to generate the report.</font></td></tr>");
   out.println("</table>");

   // start submission form
   out.println("<form action=\"Common_report_members\" method=\"post\">");

      out.println("<BR><BR><b>Columns To Display</b><BR>");

      out.println("<font size=\"2\">");

      out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
      out.println("<tr>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input checked type=\"checkbox\" name=\"lname\" value=\"1\">&nbsp;&nbsp;Last Name");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input checked type=\"checkbox\" name=\"fname\" value=\"1\">&nbsp;&nbsp;First Name");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input checked type=\"checkbox\" name=\"mi\" value=\"1\">&nbsp;&nbsp;Middle Initial");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input checked type=\"checkbox\" name=\"uname\" value=\"1\">&nbsp;&nbsp;User Name");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
         out.println("<input checked type=\"checkbox\" name=\"mnum\" value=\"1\">&nbsp;&nbsp;Member Number");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"webid\" value=\"1\">&nbsp;&nbsp;Web Site Id");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"mship\" value=\"1\">&nbsp;&nbsp;Membership Type");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"mtype\" value=\"1\">&nbsp;&nbsp;Member Type");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"email1\" value=\"1\">&nbsp;&nbsp;Email Address 1");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"email2\" value=\"1\">&nbsp;&nbsp;Email Address 2");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"phone1\" value=\"1\">&nbsp;&nbsp;Phone 1");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
         out.println("<input type=\"checkbox\" name=\"phone2\" value=\"1\">&nbsp;&nbsp;Phone 2");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"bag\" value=\"1\">Bag Room Number&nbsp;&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"posid\" value=\"1\">&nbsp;&nbsp;POS Id");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"ghin\" value=\"1\">&nbsp;&nbsp;Handicap Id");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"wc\" value=\"1\">&nbsp;&nbsp;Transportation Preference");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("<tr><td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"birth\" value=\"1\">Birth Date&nbsp;&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"active\" value=\"1\">&nbsp;&nbsp;Active/Inactive");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("<input type=\"checkbox\" name=\"tflag\" value=\"1\">&nbsp;&nbsp;Tee Sheet Flag");
      out.println("</font></td>");
      out.println("<td align=\"left\"><font size=\"2\">");
      out.println("&nbsp;&nbsp;");
      out.println("</font></td>");
      out.println("</tr>");

      out.println("</table>");
      out.println("<BR><BR>");

   // report button (go)
   out.println("<p align=\"center\"><input type=\"submit\" value=\"  Go  \"></p>");

   // end date submission form
   out.println("</form>");

   // output back button form
   if (user.startsWith( "proshop" )) {
      out.println("<form method=\"get\" action=\"Proshop_announce\">");
   } else {
      out.println("<form method=\"get\" action=\"Admin_announce\">");
   }
   out.println("<p align=\"center\"><input type=\"submit\" value=\"Home\" style=\"text-decoration:underline; background:#8B8970\"></p>");
   out.println("</form>");

   out.println("</center></font>");
   out.println("</body></html>");
   out.close();                            // wait for 'Go'

 } // end of doGet routine
 
 
 //****************************************************
 // Process the post - call from get processing
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {
    

   //
   //  Prevent caching of this page
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
   resp.setContentType("text/html");

   PrintWriter out = resp.getWriter();                             // normal output stream
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyProAdm(req, out);          // check for intruder
   if (session == null) { return; }

   String user = (String)session.getAttribute("user");               // get user name

   Connection con = SystemUtils.getCon(session);                   // get DB connection
   if (con == null) {
       displayDatabaseErrMsg(user, "Cannot establish connection.", "", out);
       return;
   }

   String club = (String)session.getAttribute("club");   

   String templott = "";
   int lottery = 0;

   if (user.startsWith( "proshop" )) {
      templott = (String)session.getAttribute("lottery");        // get lottery support indicator
      lottery = Integer.parseInt(templott);
   }

   boolean error = false; 
   boolean lname = false;
   boolean fname = false;
   boolean mi = false;
   boolean uname = false;
   boolean mnum = false;
   boolean webid = false;
   boolean mship = false;
   boolean mtype = false;
   boolean email1 = false;
   boolean email2 = false;
   boolean phone1 = false;
   boolean phone2 = false;
   boolean bag = false;
   boolean posid = false;
   boolean ghin = false;
   boolean wc = false;
   boolean birth = false;
   boolean active = false;
   boolean tflag = false;
     
   String slname = "";
   String sfname = "";
   String smi = "";
   String suname = "";
   String smnum = "";
   String swebid = "";
   String smship = "";
   String smtype = "";
   String semail1 = "";
   String semail2 = "";
   String sphone1 = "";
   String sphone2 = "";
   String sbag = "";
   String sposid = "";
   String sghin = "";
   String swc = "";
   String tFlag = "";

   String format = "";
   String temp = "";
   String errorMsg = "";

   int i = 0;
   int inact = 0;
   int ibirth = 0;
   int colCount = 0;

   String bgcolor = "#F5F5DC";
   String bgcolor1 = "#F5F5DC";
   String bgcolor2 = "#CDCDB4";

   //
   //  Get the input parms
   //
   format = (req.getParameter("report_format") != null) ? req.getParameter("report_format")  : "";

   if (req.getParameter("lname") != null) {
      lname = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("fname") != null) {
      fname = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("mi") != null) {
      mi = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("uname") != null) {
      uname = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("mnum") != null) {
      mnum = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("webid") != null) {
      webid = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("mship") != null) {
      mship = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("mtype") != null) {
      mtype = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("email1") != null) {
      email1 = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("email2") != null) {
      email2 = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("phone1") != null) {
      phone1 = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("phone2") != null) {
      phone2 = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("bag") != null) {
      bag = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("posid") != null) {
      posid = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("ghin") != null) {
      ghin = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("wc") != null) {
      wc = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("birth") != null) {
      birth = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("active") != null) {
      active = true;
      colCount++;                    // count the columns required
   }
   if (req.getParameter("tflag") != null) {
      tflag = true;
      colCount++;                    // count the columns required
   }

   // set response content type based on format requested (excel or web page)
   try{
       if (format.equals("excel")) {                           // if user requested Excel Spreadsheet Format
           resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
       }
   }
   catch (Exception exc) {
   }


   //******************************************************************
   //  Build the report
   //******************************************************************

   if (!format.equals("excel")) { out.println(SystemUtils.HeadTitle("Common - Member Roster Report")); }
   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!format.equals("excel") && user.startsWith( "proshop" )) { SystemUtils.getProshopSubMenu(req, out, lottery); }               // required to allow submenus on this page

   // start report output
   out.println("<table border=\"0\" align=\"center\">");    // table for whole page
   out.println("<tr>");
   out.println("<td align=\"center\">");
   out.println("<font size=\"2\">");
     
   out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\" bgcolor=\"#F5F5DC\">");   // heading table
   out.println("<tr>");
   if (format.equals("excel")) {
      out.println("<td colspan=\"" +colCount+ "\" align=\"center\">");
   } else {
      out.println("<td align=\"center\">");
   }
   out.println("<font size=\"3\">");
     
   out.println("<p><b>Member Roster Report</b>");

   out.println("<br>" +buildDisplayDateTime());
   out.println("</font></p></td>");
   if (format.equals("excel")) {            // force table across the page
      out.println("</tr><tr bgcolor=\"#FFFFFF\">");
      for (i=0; i<colCount; i++) {
         out.println("<td>&nbsp;</td>");
      }
   }
   out.println("</tr></table");

   out.println("</td></tr>");
   out.println("<tr><td align=\"center\">");

   if (!format.equals("excel")) { 

      out.println("<table align=\"center\">");  
      out.println("<tr><td align=\"center\">");
      out.println("<form method=\"get\" action=\"Common_report_members\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
      out.println("</td><td align=\"center\">");
      out.println("</td>&nbsp;&nbsp;<td align=\"center\">");

      out.println("<form method=\"post\" action=\"Common_report_members\" target=\"_blank\">");
      out.println("<input type=\"hidden\" name=\"report_format\" value=\"excel\">");
      if (lname == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"lname\" value=\"1\">");
      }
      if (fname == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"fname\" value=\"1\">");
      }
      if (mi == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"mi\" value=\"1\">");
      }
      if (uname == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"uname\" value=\"1\">");
      }
      if (mnum == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"mnum\" value=\"1\">");
      }
      if (webid == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"webid\" value=\"1\">");
      }
      if (mship == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"mship\" value=\"1\">");
      }
      if (mtype == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"mtype\" value=\"1\">");
      }
      if (email1 == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"email1\" value=\"1\">");
      }
      if (email2 == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"email2\" value=\"1\">");
      }
      if (phone1 == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"phone1\" value=\"1\">");
      }
      if (phone2 == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"phone2\" value=\"1\">");
      }
      if (bag == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"bag\" value=\"1\">");
      }
      if (posid == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"posid\" value=\"1\">");
      }
      if (ghin == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"ghin\" value=\"1\">");
      }
      if (wc == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"wc\" value=\"1\">");
      }
      if (birth == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"birth\" value=\"1\">");
      }
      if (active == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"active\" value=\"1\">");
      }
      if (tflag == true) {          // if column to be displayed
         out.println("<input type=\"hidden\" name=\"tflag\" value=\"1\">");
      }
      out.println("<input type=\"submit\" value=\"Create Excel Spreadsheet\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</form>");
  
      out.print("</td></tr></table><br>");
   }

   out.println("<table border=\"1\" cellpadding=\"5\" align=\"center\" bgcolor=\"#CDCDB4\">");

   out.println("<tr bgcolor=\"#8B8970\">");     // heading row

   if (lname == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Last Name</b>");
      out.println("</font></td>");
   }

   if (fname == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>First Name</b>");
      out.println("</font></td>");
   }

   if (mi == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>MI</b>");
      out.println("</font></td>");
   }

   if (uname == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>User Name</b>");
      out.println("</font></td>");
   }

   if (mnum == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Member #</b>");
      out.println("</font></td>");
   }

   if (webid == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Website Id</b>");
      out.println("</font></td>");
   }

   if (mship == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Membership Type</b>");
      out.println("</font></td>");
   }

   if (mtype == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Member Type</b>");
      out.println("</font></td>");
   }

   if (email1 == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Email Address 1</b>");
      out.println("</font></td>");
   }

   if (email2 == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Eamil Address 2</b>");
      out.println("</font></td>");
   }

   if (phone1 == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Phone 1</b>");
      out.println("</font></td>");
   }

   if (phone2 == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Phone 2</b>");
      out.println("</font></td>");
   }

   if (bag == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Bag #</b>");
      out.println("</font></td>");
   }

   if (posid == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>POS Id</b>");
      out.println("</font></td>");
   }

   if (ghin == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Handicap Id</b>");
      out.println("</font></td>");
   }

   if (wc == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Trans Preference</b>");
      out.println("</font></td>");
   }

   if (birth == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Birth Date</b>");
      out.println("</font></td>");
   }

   if (active == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Act/Inact</b>");
      out.println("</font></td>");
   }

   if (tflag == true) {          // if column to be displayed

      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Tee Flag</b>");
      out.println("</font></td>");
   }

   out.println("</tr>");

   //
   //  output one row for each member
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
               "SELECT * FROM member2b ORDER BY name_last, name_first");

      stmt.clearParameters();               // clear the parms
      rs = stmt.executeQuery();            // execute the prepared stmt

      while(rs.next()) {

         suname = rs.getString("username");
         slname = rs.getString("name_last");
         sfname = rs.getString("name_first");
         smi = rs.getString("name_mi");
         smship = rs.getString("m_ship");
         smtype = rs.getString("m_type");
         semail1 = rs.getString("email");
         swc = rs.getString("wc");
         smnum = rs.getString("memNum");
         sghin = rs.getString("ghin");
         sbag = rs.getString("bag");
         ibirth = rs.getInt("birth");
         sposid = rs.getString("posid");
         semail2 = rs.getString("email2");
         sphone1 = rs.getString("phone1");
         sphone2 = rs.getString("phone2");
         swebid = rs.getString("webid");
         inact = rs.getInt("inact");
         tFlag = rs.getString("tflag");

         //
         //  output the row - alternate colors
         //
         out.println("<tr bgcolor=\"" +bgcolor+ "\">");     // data row(s)

         if (lname == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(slname);
            out.println("</font></td>");
         }

         if (fname == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sfname);
            out.println("</font></td>");
         }

         if (mi == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(smi);
            out.println("</font></td>");
         }

         if (uname == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(suname);
            out.println("</font></td>");
         }

         if (mnum == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(smnum);
            out.println("</font></td>");
         }

         if (webid == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(swebid);
            out.println("</font></td>");
         }

         if (mship == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(smship);
            out.println("</font></td>");
         }

         if (mtype == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(smtype);
            out.println("</font></td>");
         }

         if (email1 == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(semail1);
            out.println("</font></td>");
         }

         if (email2 == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(semail2);
            out.println("</font></td>");
         }

         if (phone1 == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sphone1);
            out.println("</font></td>");
         }

         if (phone2 == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sphone2);
            out.println("</font></td>");
         }

         if (bag == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sbag);
            out.println("</font></td>");
         }

         if (posid == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sposid);
            out.println("</font></td>");
         }

         if (ghin == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(sghin);
            out.println("</font></td>");
         }

         if (wc == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(swc);
            out.println("</font></td>");
         }

         if (birth == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(ibirth);
            out.println("</font></td>");
         }

         if (active == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            if (inact == 0) {
               out.println("Act");
            } else {
               out.println("Inact");
            }
            out.println("</font></td>");
         }

         if (tflag == true) {          // if column to be displayed

            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">");
            out.println(tFlag);
            out.println("</font></td>");
         }

         out.println("</tr>");

         if (bgcolor.equals( bgcolor1 )) {

            bgcolor = bgcolor2;

         } else {

            bgcolor = bgcolor1;
         }

      }          // do all the rows (end of WHILE)

      stmt.close();
        
      out.println("</table");

   }
   catch (Exception exc) {
      displayDatabaseErrMsg(user, "Error looking up member names.", exc.getMessage(), out);
      return;
   }

   out.println("</td></tr></table>");         // end of main table

   if (!format.equals("excel")) {
      out.println("<p align=\"center\"><a href=\"Common_report_members\">Return</a></p>");
      out.println("</center></font>");
      out.println("</body></html>");
   }
   out.close();                   
    
 } // end doPost
 
 

 //**************************************************
 // Common Method for Displaying Date/Time of Report
 //**************************************************
 //
 private String buildDisplayDateTime() {

    GregorianCalendar cal = new GregorianCalendar();
    DateFormat df_full = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    return "<i>This report was generated on " + df_full.format(cal.getTime()) + "</i>";

 }


 //**************************************************
 // Common Method for Displaying Input Errors
 //**************************************************
 //
 private void displayInputErrMsg(String pMessage, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Input Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Invalid Request</H2>");
    out.println("<BR><BR>Sorry, we are unable to process the report.");
    out.println("<BR><br>" + pMessage);
    out.println("<BR>Please try again.");
    out.println("<BR><BR>If problem persists, contact customer support.");
    out.println("<font size=\"2\">");
    out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
    out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
    out.println("</form></font>");
    out.println("</CENTER></BODY></HTML>");
 }


 //**************************************************
 // Common Method for Displaying Database Errors
 //**************************************************
 //
 private void displayDatabaseErrMsg(String user, String pMessage, String pException, PrintWriter out) {
    out.println(SystemUtils.HeadTitle("Database Error"));
    out.println("<BODY><CENTER>");
    out.println("<BR><BR><H2>Database Access Error</H2>");
    out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
    out.println("<BR>Please try again later.");
    out.println("<BR><br>Fatal Error: " + pMessage);
    out.println("<BR><br>Exception: " + pException);
    out.println("<BR><BR>If problem persists, contact customer support.");
    if (user.startsWith( "admin" )) {
       out.println("<BR><BR><a href=\"Admin_announce\">Home</a>");
    } else {
       out.println("<BR><BR><a href=\"Proshop_announce\">Home</a>");
    }
    out.println("</CENTER></BODY></HTML>");
 }
 
} // end servlet
