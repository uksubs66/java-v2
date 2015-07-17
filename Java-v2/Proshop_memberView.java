/***************************************************************************************
 *   Proshop_memberView:  This servlet will process the 'Memebr View' request from
 *                        the Proshop's navigation panel.
 *
 *
 *   called by:  Proshop_maintop
 *
 *
 *   created: 9/10/2013   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 *
 ***************************************************************************************
 */

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.LoginCredentials;
import com.foretees.common.Labels;
import com.foretees.client.attribute.SelectionList;
import com.foretees.client.attribute.Option;
import com.foretees.member.MemberHelper;

import com.foretees.common.Connect;

public class Proshop_memberView extends HttpServlet {

   String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
   
   int NUM_MEMBER_SUB_TYPES = MemberHelper.NUM_MEMBER_SUB_TYPES;  



 //*****************************************************
 // Process the request from Proshop_maintop
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


   //
   //  Check if pro is returning from Member View and wants to go back to proshop
   //
   if (req.getParameter("returnToPro") != null) {        

      returnToPro(req, resp);
      return;
   }

   
   //
   //  Prevent caching so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) return;
   
   
   Connection con = Connect.getCon(req);                      // get DB connection

   if (con == null) dbError(out);  
   
   
   PreparedStatement pstmt = null;
   Statement stmt = null;
   ResultSet rs = null;
   
   boolean useSubType = false;
   
   String club = (String)session.getAttribute("club");   // get user's club
   String user = (String)session.getAttribute("user");   // get user's username value
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   String [] memship = new String [Labels.MAX_MSHIPS];         // membership types
   String [] memType = new String [Labels.MAX_MEMS];           // member types
   String [] memSubType = new String [NUM_MEMBER_SUB_TYPES];   // member sub-types

   SelectionList memSubTypes = null;
   
   String mtype = "";
   String subtype = "";
   
   String id = LoginCredentials.id;
   
   int i = 0;
   
   for (i=0; i<Labels.MAX_MEMS; i++) {    // init the arrays
      
      memType[i] = "";
   }
            
   for (i=0; i<Labels.MAX_MSHIPS; i++) {   
      
      memship[i] = "";
   }
   
   for (i=0; i<NUM_MEMBER_SUB_TYPES; i++) {   
      
      memSubType[i] = "";
   }
   
   i = 0;   // reset
   
   
   try {
      pstmt = con.prepareStatement (
         "SELECT mship " +
         "FROM mship5 WHERE activity_id = ?");

      pstmt.clearParameters();         
      pstmt.setInt(1, activity_id);     
      rs = pstmt.executeQuery();       

      while (rs.next() && i < Labels.MAX_MSHIPS) {

         memship[i] = rs.getString("mship");
         
         if (memship[i] != null && !memship[i].equals( "" )) {

            i++;
         }
      }
      
      pstmt.close();

      i = 0;
      
      stmt = con.createStatement(); 
      rs = stmt.executeQuery("SELECT mem1, mem2, mem3, mem4, mem5, mem6, mem7, mem8, " +
                                       "mem9, mem10, mem11, mem12, mem13, mem14, mem15, mem16, " +
                                       "mem17, mem18, mem19, mem20, mem21, mem22, mem23, mem24 " +
                                 "FROM club5 WHERE clubName != ''");

      if (rs.next()) {

         for (int i2=0; i2<Labels.MAX_MEMS; i2++) {
            
            mtype = rs.getString(i2+1);
            
            if (mtype != null && !mtype.equals("")) {
               
               memType[i] = mtype;
               i++;
            }
         }
      }

      stmt.close();
      
      i = 0;              // init index

      //
      //   Check if club uses any member sub-types
      //
      useSubType = Utilities.isSubTypeClub(con);  
      
      if (useSubType == true) {
         
         // now available to all clubs (optional)
         memSubTypes = MemberHelper.getMemberSubTypes(con, null, null);   // get list of all available member sub_type
         
         //
         //  Determine which ones are used at this club
         //
         for (int i2=0; i2<memSubTypes.size(); i2++){

            Option option  = memSubTypes.getOption(i2);

            if ( option != null ) {
               
               subtype = option.getValue();

               pstmt = con.prepareStatement (
                  "SELECT gender " +
                  "FROM member2b WHERE msub_type = ?");      // are there any members with this subtype ?

               pstmt.clearParameters();         
               pstmt.setString(1, subtype);     
               rs = pstmt.executeQuery();       

               if (rs.next() && i < NUM_MEMBER_SUB_TYPES) {

                  memSubType[i] = subtype;     // use this subtype in list below
                  i++;
               }
            }
         }
      }

      pstmt.close();

   }
   catch (Exception ignore) {
      dbError(out);
      return;
   }
   
   
   
  
   //
   //   Pro is requesting that we open a new window as a member so they can verify their configuration, etc.
   //   We will save some proshop session settings so they can be restored later.  We will then change the session 
   //   settings so it appears to be for a member.
   //
   //   Gather some session data to be saved in the new member session and can then be restored prior to returning to proshop
   //
   int organization_id = Utilities.getOrganizationId(con);

   
   
   //  save proshop session data in member session
 
   session.setAttribute("prouser", user);                   // save username
   session.setAttribute("proactivity_id", activity_id);     // save pro's activity id in case he/she switches while in as a member
                                                   // setup member settings
   session.setAttribute("sess_id", id);            // set session id for member validation ("foretees")
   session.setAttribute("name", "Club Staff");     // use as members full name
   session.setAttribute("caller", "proshop");      // save caller's name
   session.setAttribute("mship", "");              // set member's mship type
   session.setAttribute("mtype", "");              // set member's mtype
   session.setAttribute("wc", "");                 // set member's walk/cart pref (for _slot)
   session.setAttribute("organization_id", organization_id);  // organization_id (set if using ForeTeesDining system)
   session.setAttribute("new_skin", "1");                     // new skin flag
   session.setAttribute("ftConnect", 0);             // FT Connect Web Site indicator
   session.setAttribute("view_as_member",true);
   
   SystemUtils.sessionLog("Proshop Member-View Login Successful ", user, "", club, "", con);         // log it - no pw

   
   //
   //  Initial request to switch - inform the pro
   //
   out.println("<HTML>");
   out.println("<HEAD>");
   out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web%20utilities/foretees2.css\" type=\"text/css\">");
   out.println("<TITLE>Proshop Member View</TITLE>");
   out.println("</HEAD>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" link=\"#FFFFFF\" vlink=\"#FFFFFF\" alink=\"#FF0000\" topmargin=\"0\">");
   out.println("<font face=\"Arial, Helvetica, Sans-serif\"><center>");

      out.println("<BR><img src=\"/" +rev+ "/images/foretees.gif\"><BR><BR>");
      out.println("<table border=\"0\" width=\"100%\" align=\"center\" valign=\"top\">");  // large table for whole page
      out.println("<tr><td valign=\"top\" align=\"center\">");

      out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"3\">");
         out.println("<tr>");
         out.println("<td width=\"580\" align=\"center\">");
         out.println("<font size=\"3\">");
         out.println("<br><b>Member View</b><br><br></font>");
         out.println("<font size=\"2\">");

         out.println("<p>Please select the type of member you wish to emulate from the following lists.");
         out.println("</p><br /><br />");

         out.println("<form action=\""+Utilities.getBaseUrl(req, activity_id, club)+"Proshop_memberView\" method=\"post\" name=\"proForm\" target=\"_blank\">");
         out.println("<input type=\"hidden\" name=\"proshopPrompt\" value=\"yes\">");

         out.println("Membership Type: &nbsp;<select size=\"1\" name=\"mship\">");                 

         for (i=0; i<Labels.MAX_MSHIPS; i++ ) {

            if (!memship[i].equals("")) {

               out.println("<option value=\"" + memship[i] + "\">" + memship[i] + "</option>");
            }
         }
         out.println("</select>");

         out.println("<BR><BR>Member Type: &nbsp;<select size=\"1\" name=\"mtype\">");                 

         for (i=0; i<Labels.MAX_MEMS; i++ ) {

            if (!memType[i].equals("")) {

               out.println("<option value=\"" + memType[i] + "\">" + memType[i] + "</option>");
            }
         }
         out.println("</select>");   
         
         if (useSubType == true) {        // if this club uses member sub_types

            out.println("<BR><BR><BR>The following is optional.  Leave as '- none -' unless you want a specific sub-type.");
            
            out.println("<BR><BR>Member Sub-Type: &nbsp;<select size=\"1\" name=\"msubtype\">");                 

            out.println("<option value=\"\">- none -</option>");   // allow them to specify none ("")
            
            for (i=0; i<NUM_MEMBER_SUB_TYPES; i++ ) {

               if (!memSubType[i].equals("")) {

                  out.println("<option value=\"" + memSubType[i] + "\">" + memSubType[i] + "</option>");
               }
            }
            out.println("</select>");   
         }
         
         out.println("<BR><BR><BR><BR><input type=submit value=\"Continue to Member View\" name=\"continue\"></form>");
            
         out.println("<br>");
         out.println("<form action=\"Proshop_memberView\" method=\"get\">");
         out.println("<input type=\"hidden\" name=\"returnToPro\" value=\"yes\">");
         out.println("<input type=submit value=\"Return to Proshop Session\"></form>");
         out.println("<br><br>");
         out.println("</font></td></tr>");
         out.println("</table><br>");

         out.println("</td>");
         out.println("</tr>");
      out.println("</table>");
      out.println("</font></center></body></html>");
      out.close();
      return;                 // exit and wait for reply

 }     // end of doGet

 
 
 //*****************************************************
 // Process the request from doGet above - switch to member
 //*****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   PrintWriter out = resp.getWriter();
   Common_skin.setNoCacheHtml(resp);

   HttpSession session = null;
           
   session = SystemUtils.verifyMem(req, out);             // check for member now
   
   if (session == null) return;                   // exit if error detected (msg already displayed)
   
   String activityURL = "Member_announce";      // default destination   
   String mship = req.getParameter("mship");    // mship type selected by proshop user
   String mtype = req.getParameter("mtype");    // mtype selected 
   
   String msubtype = "";
   
   if (req.getParameter("msubtype") != null) {    // if sub-type selected 
      
      msubtype = req.getParameter("msubtype");   
   }
   
   
   String club = (String)session.getAttribute("club");   // get user's club
   String user = (String)session.getAttribute("user");   // get user's username value
   int activity_id = (Integer)session.getAttribute("activity_id");
   
   long member_id = 100;  
   
   session.setAttribute("user", "staffuser");    // set new username so not detected as a proshop user
   session.setAttribute("mship", mship);         // set member's mship type
   session.setAttribute("mtype", mtype);         // set member's mtype
   session.setAttribute("msubtype", msubtype);   // set member's sub-type
   session.setAttribute("member_id", member_id); // set the member id - use '100' just to have a value

    
   //
   //  Route proshop user (emulating a member) to Member_announce
   //
   Common_skin.outputHeader(club, activity_id, "Route Proshop Page", true, out, req, 0, "");

   Common_skin.outputBody(club, activity_id, out, req);

   out.println("<body>");
   out.println("<div id=\"wrapper_login\" align=\"center\">");
   out.println("<div id=\"title\">Thank You</div>");
   out.println("<div id=\"main_login\" align=\"center\">");
   out.println("<h1>Member Access Accepted</h1>");
   out.println("<div class=\"main_message\">");
   out.println("<h2>Welcome Club Staff</h2><br /><br />");
   out.println("<center><div class=\"sub_instructions\">");
              
   out.println("You will now be able to view the system as a member with the classifications you selected.<BR>");
   out.println("However, you are simply emulating a member, <strong>your session is not tied to a specific member record.</strong><BR>");
   out.println("You will not have all the functionality of an actual club member.<BR>");
   out.println("The purpose of this portal is to allow you to verify system settings, view member messages, etc.<BR>");
   out.println("<strong>Please do not use this to book reservations.</strong><BR>");

   out.println("</div>");     // close out the sub_instructions above
   out.println("<form method=\"get\" action=\"Member_announce\">");
   out.println("<input type=\"submit\" value=\"Continue\" id=\"submit\">");
   out.println("</form>");
   out.println("</center></div></div>");        
   Common_skin.outputPageEnd(club, activity_id, out, req);    // finish the page       
   out.close();           

 }     // end of doPost

 
 
 
 // *********************************************************
 //  Return to Proshop User
 // *********************************************************

 private void returnToPro(HttpServletRequest req, HttpServletResponse resp) {

    
   PrintWriter out = null;    
    
   try { 
      
      resp.setContentType("text/html");
      out = resp.getWriter();
      
   }
   catch (Exception ignore) {
   }
      

   HttpSession session = SystemUtils.verifyMem2(req, out);     // Get member session (changed above on initial entry)

   if (session == null) dbError(out);  
   
   
   //
   //   Gather some session data from the new member session and restore the proshop session
   //
   String club = (String)session.getAttribute("club");                  // get user's club
   String user = (String)session.getAttribute("prouser");               // get saved proshop's username value
   int activity_id = (Integer)session.getAttribute("proactivity_id");   //       and activity_id
   
    
   //  proshop session - restore proshop values
 
   session.setAttribute("user", user);                  // restore proshop username
   session.setAttribute("activity_id", activity_id);    //    and activity id
   session.setAttribute("caller", "none");              // clear caller so user can logout properly
   session.setAttribute("new_skin", "0");               // reset new skin flag
   session.setAttribute("view_as_member",false);
 
 
   out.println("<html><head><title>Proshop Return Page</title>");
   out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" + rev + "/proshop_welcome.htm\">");
   out.println("</head>");
   out.println("<body bgcolor=\"white\"><center><img src=\"/" +rev+ "/images/foretees.gif\"><br>");
   out.println("<hr width=\"40%\">");
   out.println("<p>&nbsp;</p>");
   out.println("<h2>Return To Proshop Session</h2><br>");

      out.println("<table border=\"2\" bgcolor=\"#F5F5DC\" cellpadding=\"12\"><tr><td align=\"center\">");
      out.println("<p>If you do not return to your proshop session automatically, please click the Continue button below.</p>");
      out.println("</td></tr></table><br>");
   out.println("<br><br><font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" + rev + "/proshop_welcome.htm\">");
   out.println("<input type=\"submit\" value=\"Continue\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</form></font>");
   out.println("</center></body></html>");
   out.close();
    
 }   // end of returnToPro
 


 // *********************************************************
 //  Database Error
 // *********************************************************

 private void dbError(PrintWriter out) {

      out.println(SystemUtils.HeadTitle("DB Error"));
      out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
      out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
      out.println("<CENTER><BR><BR><H3>Unexpected Error</H3>");
      out.println("<BR><BR>We encountered an error processing your request.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, please contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"javascript:history.back(1)\">Return</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

 }

}
