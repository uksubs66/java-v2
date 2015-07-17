/***************************************************************************************     
 *   Proshop_dining:  This servlet will procces the dining configuration page.
 *
 *
 *   called by:  proshop menu (doGet) and self.
 *
 *   created: 01/13/09   Brad K.
 *
 *
 *   last updated:
 *
 *       11/27/12   Tweak iframe resize code
 *        8/30/11   Allow for Dining proshop user (Admin) to access these pages in non-frame mode.
 *        6/16/11   Updated dining request form to display Time fields in red text so they stand out.
 *       10/20/10   Populate new parmEmail fields
 *       01/11/10   Fixed 'Cancel' links from not working on the member dining form
 *       05/29/09   Changes to allow for correct stat tracking and link display for members entering via email links
 *       05/29/09   Change how button links were set up to allow functionality on IE
 *       05/06/09   Correct the dining room drop down list so it includes the first entry.
 *       03/10/09   Added ability to invoke from email links
 *       03/02/09   Many adjustments since class creation
 *       01/13/09   Created class.
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
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.alphaTable;


public class Proshop_dining extends HttpServlet {
                         
                                 
 public static final String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 static String DINING_USER = ProcessConstants.DINING_USER;               // Dining username for Admin user from Dining System


 //
 //**********************************************************
 //
 // Process the initial request
 //
 //**********************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
   
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

   if (session == null) {
     
      return;
   }

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   
   
   if (req.getParameter("dReq") != null) {
      
       if (!user.equals(DINING_USER)) {     // skip test if Dining Admin User
      
          // Check Feature Access Rights for current proshop user
          if (!SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out)) {
              SystemUtils.restrictProshop("DINING_REQUEST", out);
          }
       }
       buildDiningForm(req, out, session, con);
       return;
   }
   
   if (req.getParameter("reqSubmit") != null) {

       if (!user.equals(DINING_USER)) {     // skip test if Dining Admin User
      
          // Check Feature Access Rights for current proshop user
          if (!SystemUtils.verifyProAccess(req, "DINING_REQUEST", con, out)) {
              SystemUtils.restrictProshop("DINING_REQUEST", out);
          }
       }
       processDiningForm(req, out, session, con);
       return;
   }
   
   if (!user.equals(DINING_USER)) {     // skip test if Dining Admin User
      
      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "DINING_CONFIG", con, out)) {
          SystemUtils.restrictProshop("DINING_CONFIG", out);
      }
   }
              
   
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   // Make sure dining system is enabled first
   try {
       stmt = con.createStatement();
       rs = stmt.executeQuery("SELECT dining, organization_id FROM club5");

       if (rs.next()) {
           if (rs.getInt("dining") != 1 && rs.getInt("organization_id") == 0) {
               out.println(SystemUtils.HeadTitle("Dining RequestSystem Not Active"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Dining Request System Is Not Active</H3>");
               out.println("<BR><BR>The Dining Request system is not currently active for your club.");
               out.println("<BR><BR>The system may be turned on by setting it within your Club Options.");
               out.println("<BR><BR>");
               out.println("<a href=\"Proshop_announce\">Home</a>");
               out.println("</CENTER></BODY></HTML>");
               return;
           } 
       }
   } catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   if (req.getParameter("preview") != null) {
       displayPreview(req, out, session, con);
       return;
   }
   
   if (req.getParameter("submitDining") != null) {
       updateDining(req, out, session, con);
       return;
   }
   
   if (req.getParameter("addMsg") != null || req.getParameter("updateMsg") != null) {
       updateMessage(req, out, session, con);
   }
   
   if (req.getParameter("removeMsg") != null) {
       removeMessage(req, out, session, con);
   }
   
   if (req.getParameter("addEmail") != null) {
       addEmail(req, out, session, con);
   }
   
   if (req.getParameter("removeEmail") != null) {
       removeEmail(req, out, session, con);
   }
   
   if (req.getParameter("addRoom") != null || req.getParameter("updateRoom") != null) {
       updateDiningRoom(req, out, session, con);
   }
   
   if (req.getParameter("removeRoom") != null) {
       removeDiningRoom(req, out, session, con);
   }
   
   if (req.getParameter("dining") != null) {
       doDining(req, out, session, con);
       return;
   }
   
   if (req.getParameter("messages") != null) {
       doMessages(req, out, session, con);
       return;
   }
   
   if (req.getParameter("emails") != null) {
       doEmails(req, out, session, con);
       return;
   }
   
   if (req.getParameter("rooms") != null) {
       doRooms(req, out, session, con);
       return;
   }
   
   // 
   //  Build the main html page.  Use separate iframes to encapsulate the Dining Config, 
   //  Dining Emails Management, and Dining Rooms Management sections
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Dining System Configuration"));
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
   if (!user.equals(DINING_USER)) {
      SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   }
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"3\" face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<b>Dining Request Setup</b><br>");
   out.println("Modify the Dining System settings below.<br>");
   out.println("</font>");
   out.println("</td></tr></table>");
   
   if (!user.equals(DINING_USER)) {
      out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_announce'\">Return</button><br>");
      out.println("<br>For help with the Dining Configuration below, click here:&nbsp;<button type=\"button\" style=\"width:80px\" onclick=\"location.href='http://www.foretees.com/newfeatures/Dining Request.pdf'\" target=\"_blank\">Help</button><br>");
   } else {
      out.println("<br><form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return \" onClick='self.close()' alt=\"Close\"><br>");
      out.println("<br>For help with the Dining Configuration below, click here:&nbsp;<button type=\"button\" style=\"width:80px\" onclick=\"location.href='http://www.foretees.com/newfeatures/Dining-Config.pdf'\" target=\"_blank\">Help</button><br>");
   }
   
   
   //
   //  Dining System Config iframe
   //
   out.println("<br><table border=\"1\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
   out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Dining Request System Configuration</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<iframe id=\"diningiframe\" src=\"Proshop_dining?dining\" style=\"width:800px;\" scrolling=no frameborder=no></iframe>");
   out.println("</td></tr></table><br>");
   
   //
   //  Dining Custom Messages Management iframe
   //
   out.println("<br><table border=\"1\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
   out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Dining Request Custom Messages Configuration</font></td></tr>");
   out.println("<tr><td align=\"center\">");
   out.println("<iframe id=\"messagesiframe\" src=\"Proshop_dining?messages\" style=\"width:800px\" scrolling=no frameborder=no></iframe>");
   out.println("</td></tr><table><br>");


   //
   // If using the full dining system then skip the email & room config sections
   //
   if (Utilities.getOrganizationId(con) == 0) {

       //
       //  Dining Emails Management iframe
       //
       out.println("<br><table border=\"1\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
       out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Dining Request Email Configuration</font></td></tr>");
       out.println("<tr><td align=\"center\">");
       out.println("<iframe id=\"emailsiframe\" src=\"Proshop_dining?emails\" style=\"width:800px\" scrolling=no frameborder=no></iframe>");
       out.println("</td></tr><table><br>");

       //
       //  Dining Rooms Management iframe
       //
       out.println("<br><table border=\"1\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
       out.println("<tr><td align=\"center\" colspan=\"2\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">Dining Room Configuration</font></td></tr>");
       out.println("<tr><td align=\"center\">");
       out.println("<iframe id=\"roomsiframe\" src=\"Proshop_dining?rooms\" style=\"width:800px\" scrolling=no frameborder=no></iframe>");
       out.println("</td></tr>");
   
   }

   out.println("</table>");
   if (!user.equals(DINING_USER)) {
      out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_announce'\">Return</button><br>");
   } else {
      out.println("<br><form><input type=\"button\" style=\"text-decoration:underline; background:#8B8970\" Value=\"  Return \" onClick='self.close()' alt=\"Close\"><br>");
   }
   
   out.println("</center></font></body>");
   
   //
   //*******************************************************************
   //  Script for dynamic iframe resizing
   //*******************************************************************
   //
   out.println("<script type='text/javascript'>");           
   out.println("<!--");
   out.println("function resizeIFrame(divHeight, iframeName) {");
   out.println("document.getElementById(iframeName).height = divHeight;");
   out.println("}");
   out.println("// -->");
   out.println("</script>");          // End of script
   
   //
   //*******************************************************************
   //  Script for reloading select box on change
   //*******************************************************************
   //
   out.println("<script type='text/javascript'>");            
   out.println("<!--");
   out.println("function selectReload() {");
   out.println("if (document.getElementById(diningRoom).options[document.getElementById(diningRoom).selectedIndex].value != \"\")");
   out.println("{");
   out.println("location.href=\"Proshop_dining?rooms&id=\" + eval(\"document.getElementById(diningRoom).options[document.getElementById(diningRoom).selectedIndex].value\")");
   out.println("}");
   out.println("}");
   out.println("// -->");
   out.println("</script>");          // End of script
   
   out.println("</html>");

 }  // end of doGet

 //
 //****************************************************************
 //
 // Process the form request from Proshop_dining page displayed above
 //
 //    parms passed:  see list below
 //
 //
 //****************************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection
       
   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   if (req.getParameter("dReq") != null) {
       buildDiningForm(req, out, session, con);
       return;
   }
   
   if (req.getParameter("reqSubmit") != null) {
       processDiningForm(req, out, session, con);
       return;
   }

   
   String club = (String)session.getAttribute("club");               // get club name
   String user = (String)session.getAttribute("user");

   if (!user.equals(DINING_USER)) {     // skip test if Dining Admin User
      
      // Check Feature Access Rights for current proshop user
      if (!SystemUtils.verifyProAccess(req, "DINING_CONFIG", con, out)) {
          SystemUtils.restrictProshop("DINING_CONFIG", out);
      }
   }
     
   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);
   
   // Make sure dining system is enabled first
   try {
       stmt = con.createStatement();
       rs = stmt.executeQuery("SELECT dining, organization_id FROM club5");

       if (rs.next()) {
           if (rs.getInt("dining") != 1 && rs.getInt("organization_id") == 0) {
               out.println(SystemUtils.HeadTitle("Dining RequestSystem Not Active"));
               out.println("<BODY><CENTER>");
               out.println("<BR><BR><H3>Dining Request System Is Not Active</H3>");
               out.println("<BR><BR>The Dining Request system is not currently active for your club.");
               out.println("<BR><BR>The system may be turned on by setting it within your Club Options.");
               out.println("<BR><BR>");
               out.println("<a href=\"Proshop_announce\">Home</a>");
               out.println("</CENTER></BODY></HTML>");
               return;
           } 
       }
   } catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
    
   if (req.getParameter("preview") != null) {
       displayPreview(req, out, session, con);
       return;
   }
   
   if (req.getParameter("submitDining") != null) {
       updateDining(req, out, session, con);
       return;
   }
   
   if (req.getParameter("addMsg") != null || req.getParameter("updateMsg") != null) {
       updateMessage(req, out, session, con);
   }
   
   if (req.getParameter("removeMsg") != null) {
       removeMessage(req, out, session, con);
   }
   
   if (req.getParameter("addEmail") != null) {
       addEmail(req, out, session, con);
   }
   
   if (req.getParameter("removeEmail") != null) {
       removeEmail(req, out, session, con);
   }
   
   if (req.getParameter("addRoom") != null || req.getParameter("updateRoom") != null) {
       updateDiningRoom(req, out, session, con);
   }
   
   if (req.getParameter("removeRoom") != null) {
       removeDiningRoom(req, out, session, con);
   }
   
   if (req.getParameter("dining") != null) {
       doDining(req, out, session, con);
       return;
   }
   
   if (req.getParameter("messages") != null) { 
       doMessages(req, out, session, con);
       return;
   }
   
   if (req.getParameter("emails") != null) {
       doEmails(req, out, session, con);
       return;
   }
   
   if (req.getParameter("rooms") != null) {
       doRooms(req, out, session, con);
       return;
   }
 }
         
 // ********************************************************************************
 //  Process the Main Dining System Config portion of the dining system configuration
 // ********************************************************************************
 
 private void doDining(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
        
   Statement stmt = null;
   ResultSet rs = null;
   
   String club = (String)session.getAttribute("club");
   String user = (String)session.getAttribute("user");
   
   
   //
   //  Declare variables
   //
   String formText = "";
   String linkText = "";
   String promptText = "";
   String customURL = "";
   String checkProMain = "";
   String checkProTeetime = "";
   String checkProLesson = "";
   String checkMemMain = "";
   String checkMemTeetime = "";
   String checkMemLesson = "";
   String checkEmailTeetime = "";
   String checkEmailLesson = "";
   
   int proMain = 0;
   int proTeetime = 0;
   int proLesson = 0;
   int memMain = 0;
   int memTeetime = 0;
   int memLesson = 0;
   int emailTeetime = 0;
   int emailLesson = 0;
   
   boolean firstTime = false;
   
   //
   //  Gather existing data
   //
   
   try {
       stmt = con.createStatement();
       rs = stmt.executeQuery("SELECT * FROM dining_config");

       if (rs.next()) {
           formText = rs.getString("form_text");
           linkText = rs.getString("link_text");
           promptText = rs.getString("prompt_text");
           //customURL = rs.getString("custom_url");
           proMain = rs.getInt("pro_main");
           proTeetime = rs.getInt("pro_teetime");
           proLesson = rs.getInt("pro_lesson");
           memMain = rs.getInt("mem_main");
           memTeetime = rs.getInt("mem_teetime");
           memLesson = rs.getInt("mem_lesson");
           emailTeetime = rs.getInt("email_teetime");
           emailLesson = rs.getInt("email_lesson");
       } else {
           firstTime = true;
               
           formText = "";
           linkText = "";
           promptText = "";
           customURL = "";
           proMain = 0;
           proTeetime = 0;
           proLesson = 0;
           memMain = 0;
           memTeetime = 0;
           memLesson = 0;
           emailTeetime = 0;
           emailLesson = 0;
       }
       
       stmt.close();
       
   } catch (Exception exc) {
      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   if (proMain == 1) {checkProMain = " checked";}
   if (proTeetime == 1) {checkProTeetime = " checked";}
   if (proLesson == 1) {checkProLesson = " checked";}
   if (memMain == 1) {checkMemMain = " checked";}
   if (memTeetime == 1) {checkMemTeetime = " checked";}
   if (memLesson == 1) {checkMemLesson = " checked";}
   if (emailTeetime == 1) {checkEmailTeetime = " checked";}
   if (emailLesson == 1) {checkEmailLesson = " checked";}
    
   out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('diningiframediv').offsetHeight, 'diningiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
   out.println("<div id=\"diningiframediv\">");
   out.println("<form action=\"Proshop_dining?dining\" method=\"POST\" name=\"diningform\">");
   out.println("<table border=\"0\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
   out.println("<tr><td align=\"left\" colspan=\"2\">");
   out.println("Fill out the form below with the default messages you'd like to display for the dining system, as well as the locations you'd like prompts to the dining request form to be displayed." +
           "<br><br>Use the <b>Submit</b> button below the location checkboxes to save any changes.<br>");
   out.println("</td></tr>");
   
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      
      out.println("<tr>");
      out.println("<td align=\"left\"><b>Form Text - Default</b><br>" +
              "Default text to display at the top of the Dining Request Form:</td>");
      out.println("<td align=\"center\" width=\"300\">");
      out.println("<textarea rows=\"8\" cols=\"40\" name=\"formText\">" + formText.trim() + "</textarea>");
      out.println("</td>"); 
      out.println("</tr>");
      
   } else {
      
       out.println("<input type=\"hidden\" name=\"formText\" value=\"\">");
   }
   out.println("<tr>");
   out.println("<td align=\"left\"><b>Prompt Text - Default</b><br>" +
           "Default text to display on the page where users are asked if they want to make a dining request:</td>");
   out.println("<td align=\"center\" width=\"300\">");
   out.println("<textarea rows=\"8\" cols=\"40\" name=\"promptText\">" + promptText.trim() + "</textarea>");
   out.println("</td>");
   out.println("</tr>");
   out.println("<tr>");
   out.println("<td align=\"left\"><b>Link Text - Default</b><br>" +
           "Default text to display as the link that follows the dining request prompt above:<td align=\"left\" width=\"300\">");
   out.println("<input type=\"text\" name=\"linkText\" size=\"40\" maxlength=\"80\" value=\"" + linkText.trim() + "\">");
   out.println("</td>");
   out.println("</tr>");
   
   out.println("<tr>");
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      out.println("<td align=\"left\">Select which locations you'd like to display prompts/links for making a dining request.  " +
               "The first set of options may be enabled separately for Proshop and Member sides of the system.");
   } else {
      out.println("<td align=\"left\">Select which locations you'd like to display prompts/links for making a dining request.");
   }
   out.println("</td><td>");
   out.println("<table border=\"0\"  bgcolor=\"#F5F5DC\">");        // Checkbox table
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      out.println("<tr align=\"center\" width=\"300\"><td></td><td>Proshop</td><td>Member</td></tr>");
      out.println("<tr align=\"center\" width=\"300\">");
      out.println("<td align=\"right\">Main Menu</td>");
      out.println("<td><input type=\"checkbox\" name=\"proMain\"" + checkProMain + "></td>");
      out.println("<td><input type=\"checkbox\" name=\"memMain\"" + checkMemMain + "></td></tr>");
   }
   out.println("<tr align=\"center\" width=\"300\">");
   out.println("<td align=\"right\">Tee Time Confirmation Page</td>");
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      out.println("<td><input type=\"checkbox\" name=\"proTeetime\"" + checkProTeetime + "></td>");
   } else {
      out.println("<td>&nbsp;</td>");
   }
   out.println("<td><input type=\"checkbox\" name=\"memTeetime\"" + checkMemTeetime + "></td>");
   out.println("</tr><tr align=\"center\" width=\"300\">");
   out.println("<td align=\"right\">Lesson Book Confirmation Page</td>");
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      out.println("<td><input type=\"checkbox\" name=\"proLesson\"" + checkProLesson + "></td>");
   } else {
      out.println("<td>&nbsp;</td>");
   }
   out.println("<td><input type=\"checkbox\" name=\"memLesson\"" + checkMemLesson + "></td>");
   if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      out.println("</tr><tr align=\"center\" width=\"300\">");
      out.println("<td colspan=\"3\" align=\"center\"><hr></td>");
      out.println("</tr><tr align=\"center\" width=\"300\">");
      out.println("<td align=\"right\">Tee Time Email Notifications</td>");
      out.println("<td colspan=\"2\"><input type=\"checkbox\" name=\"emailTeetime\"" + checkEmailTeetime + "></td>");
      out.println("</tr><tr align=\"center\" width=\"300\">");
      out.println("<td align=\"right\">Lesson Book Email Notifications</td>");
      out.println("<td colspan=\"2\"><input type=\"checkbox\" name=\"emailLesson\"" + checkEmailLesson + "></td>");
   }
   out.println("</tr>");
   out.println("</table>");     // End of checkbox table
   out.println("</td>");
   out.println("</tr>");
   
   /*
   out.println("<tr>");
   out.println("<td align=\"right\"><b>URL</b> to dining room website section (club website), dining/menu pdf, etc (optional):</td>");
   out.println("<td align=\"center\" width=\"300\">");
   out.println("<input type=\"text\" name=\"customURL\" value=\"" + customURL.trim() + "\" size=\"50\" maxlength=\"50\">");
   out.println("</td>");
   out.println("</tr>");
   */
   
   // Hidden inputs
   if (firstTime) {
       out.println("<input type=\"hidden\" name=\"firstTime\" value=\"true\">");
   }
   out.println("<input type=\"hidden\" name=\"customURL\" value=\"\">");
   
   // Print submit/reset buttons
   out.println("<tr><td colspan=\"2\" align=\"center\"><br>");
   out.println("<input type=\"submit\" name=\"submitDining\" value=\"Submit\" align=\"center\">");
   out.println("&nbsp;");
   
   out.println("<input type=\"submit\" name=\"resetDining\" value=\"Reset\" align=\"center\">");
   out.println("</td></tr>");
   
   out.println("</table><br><br>");
   out.println("</form>");
   
   out.println("</div>");
   out.println("</body>");
   out.println("</html>");
   
 }  // end of doDining
 
 // ********************************************************************************
 //  Process the Dining Custom Messages Management portion of the dining system configuration
 // ********************************************************************************

 private void doMessages(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     int msgId = 0;
     int priority = 0;
     int syear = 0;
     int smonth = 0;
     int sday = 0;
     int eyear = 0;
     int emonth = 0;
     int eday = 0;
     int cal_year = 0;
     int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values
     
     String name = "";
     String sdate = "";
     String edate = "";
     String formText = "";
     String promptText = "";
     String linkText = "";
     String params = "";
     String [] selectArr = new String [31];
     
     //
     //  Dining Request Emails List
     //
     try {
         if (req.getParameter("diningMsg") != null && req.getParameter("updateMsg") == null && req.getParameter("removeMsg") == null) {
             msgId = Integer.parseInt(req.getParameter("diningMsg"));
         }
         
         if (msgId != 0) {
             pstmt = con.prepareStatement("SELECT * FROM dining_messages WHERE id = ? AND active=1");
             pstmt.clearParameters();
             pstmt.setInt(1, msgId);
             
             rs = pstmt.executeQuery();
             
             if (rs.next()) {
                 name = rs.getString("name");
                 sdate = String.valueOf(rs.getString("sdate"));
                 edate = String.valueOf(rs.getString("edate"));
                 formText = rs.getString("form_text");
                 promptText = rs.getString("prompt_text");
                 linkText = rs.getString("link_text");
                 priority = rs.getInt("priority");
                 recurrArr[0] = rs.getInt("eo_week");
                 
                 recurrArr[1] = rs.getInt("sunday");
                 recurrArr[2] = rs.getInt("monday");
                 recurrArr[3] = rs.getInt("tuesday");
                 recurrArr[4] = rs.getInt("wednesday");
                 recurrArr[5] = rs.getInt("thursday");
                 recurrArr[6] = rs.getInt("friday");
                 recurrArr[7] = rs.getInt("saturday");
             }
             
             pstmt.close();
         }
         if (req.getParameter("returning") != null) {
             msgId = Integer.parseInt(req.getParameter("id"));
             name = req.getParameter("name");
             sdate = req.getParameter("sdate");
             edate = req.getParameter("edate");
             formText = req.getParameter("formText");
             promptText = req.getParameter("promptText");
             linkText = req.getParameter("linkText");
             priority = Integer.parseInt(req.getParameter("priority"));
             recurrArr[0] = Integer.parseInt(req.getParameter("eo_week"));
             
             for (int i=1; i<8; i++) {
                 recurrArr[i] = Integer.parseInt(req.getParameter("day" + String.valueOf(i)));
             }
         }
       
         
         //
         //  Get today's date (defaults)
         //
         Calendar cal = new GregorianCalendar();       // get todays date
         smonth = cal.get(Calendar.MONTH) + 1;
         emonth = cal.get(Calendar.MONTH) + 1;
         sday = cal.get(Calendar.DAY_OF_MONTH);
         eday = cal.get(Calendar.DAY_OF_MONTH);
         cal_year = cal.get(Calendar.YEAR);         // Split up the date if necessary
         
         if (!sdate.equals("")) {
             StringTokenizer sdateTok = new StringTokenizer(sdate, "-");
             if (sdateTok.countTokens() == 3) {
                 syear = Integer.parseInt(sdateTok.nextToken());
                 smonth = Integer.parseInt(sdateTok.nextToken());
                 sday = Integer.parseInt(sdateTok.nextToken());
             }
         }
         if (!edate.equals("")) {
             StringTokenizer edateTok = new StringTokenizer(edate, "-");
             if (edateTok.countTokens() == 3) {
                 eyear = Integer.parseInt(edateTok.nextToken());
                 emonth = Integer.parseInt(edateTok.nextToken());
                 eday = Integer.parseInt(edateTok.nextToken());
             }
         }
         
         out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('messagesiframediv').offsetHeight, 'messagesiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
         out.println("<div id=\"messagesiframediv\">");
         out.println("<table border=\"0\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");        // Outer table
         out.println("<tr><td align=\"left\" colspan=\"2\">");
         if (!user.equals(DINING_USER)) {     // if not Dining Admin User
            out.println("Add, Update, or Remove any desired custom messages below.  These messages will display instead of the default messages (defined above) during their timeframe.  " +
                    "Note that custom messages with shorter timeframes have a higher display priority than those with longer timeframes.  At least one of the three texts " +
                    "(Form, Prompt, or Link) must contain a value, and any others left blank will be filled with the default messages.  The Preview button can be used to see " +
                    "which message will be displayed for a specific day." +
                    "<br><br>Changes are saved whenever the Add, Update, or Remove buttons are clicked as long as all data in the form is valid.<br>");
         } else {
            out.println("Add, Update, or Remove any desired custom messages below.  These messages will display instead of the default messages (defined above) during their timeframe.  " +
                    "Note that custom messages with shorter timeframes have a higher display priority than those with longer timeframes.  At least one of the 2 texts " +
                    "(Prompt or Link) must contain a value, and if the other is left blank, it will be filled with the default message.  The Preview button can be used to see " +
                    "which message will be displayed for a specific day." +
                    "<br><br>Changes are saved whenever the Add, Update, or Remove buttons are clicked as long as all data in the form is valid.<br>");
         }
         out.println("</td></tr>");
         out.println("<form action=\"Proshop_dining?messages\" method=\"POST\" name=\"messagesform\">");
         out.println("<tr><td align=\"center\" valign=\"middle\">");
         out.println("<table border=\"0\" bgcolor=\"F5F5DC\">");      // Left side table (select box, submit buttons)
         out.println("<tr><td align=\"center\" width=\"250\">");
         out.println("<select name=\"diningMsg\" size=\"25\" style=\"width: 250\" onchange=\"document.messagesform.submit()\">");
         
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT * FROM dining_messages WHERE active=1 ORDER BY name");
         
         while (rs.next()) {
             if (Integer.parseInt(rs.getString("id")) == msgId) {
                 out.println("<option value=\"" + rs.getString("id") + "\" selected>" + rs.getString("name") + "</option>");
             } else {
                 out.println("<option value=\"" + rs.getString("id") + "\">" + rs.getString("name") + "</option>");
             }
         }
         
         stmt.close();
         
         out.println("</select></td></tr>");
         out.println("<tr><td align=\"center\" valign=\"top\">");
         // Print submit buttons
         if (msgId != 0) {
             out.println("<input type=\"submit\" name=\"updateMsg\" value=\"Update\" style=\"width:70px\">");
         } else {
             out.println("<input type=\"submit\" name=\"addMsg\" value=\"Add\" style=\"width:70px\">");
         }
         out.println("&nbsp;");
         out.println("<input type=\"submit\" name=\"removeMsg\" value=\"Remove\" style=\"width:70px\">");
         out.println("&nbsp;<button style=\"width:70px\" onclick=\"location.href='Proshop_dining?messages'\">Reset</button>");
         
         if (!sdate.equals("")) {
             params = "&date=" + sdate;
         }
         out.println("<br><br><button align=\"center\" style=\"width:80px\" onclick=\"window.open('Proshop_dining?preview')\">Preview</button>");
         out.println("</td></tr>");
         out.println("</table>");       // End of left side table
         out.println("</td><td align=\"center\" valign=\"top\">");
         out.println("<table border=\"0\" bgcolor=\"F5F5DC\" cellpadding=\"3\">");      // Right side table (name/description text spaces)
         out.println("<tr><td align=\"right\">Name:</td>");
         out.println("<td align=\"left\"><input type=\"text\" name=\"name\" value=\"" + name + "\" size=\"30\" maxlength=\"30\"></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Start Date:</td>");
         out.println("<td align=\"left\" nowrap>");
         
         for (int i=0; i<12; i++) {
             if (smonth == i+1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("Month:&nbsp;<select name=\"smonth\">");
              out.println("<option" + selectArr[0] + " value=\"01\">JAN</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">FEB</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">MAR</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">APR</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">MAY</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">JUN</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">JUL</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">AUG</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">SEP</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">OCT</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">NOV</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">DEC</option>");
         out.println("</select>&nbsp;&nbsp;");
         
         out.println("Day:&nbsp;&nbsp;<select name=\"sday\">");
         for (int i=0; i<31; i++) {
             if (sday == i+1) {
                 out.println("<option selected value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             } else {
                 out.println("<option value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             }
         }
         out.println("</select>&nbsp&nbsp;");
         
         out.println("Year:&nbsp;&nbsp;<select name=\"syear\">");
            if ((syear == 0 || syear == cal_year)) {
                out.println("<option selected value=\"" + cal_year + "\">" + cal_year + "</option>");
                out.println("<option value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
            } else {
                out.println("<option value=\"" + cal_year + "\">" + cal_year + "</option>");
                out.println("<option selected value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
            }
         out.println("</select></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">End Date:</td>");
         out.println("<td align=\"left\" nowrap>");
         
         for (int i=0; i<12; i++) {
             if (emonth == i+1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("Month:&nbsp;<select name=\"emonth\">");
              out.println("<option" + selectArr[0] + " value=\"01\">JAN</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">FEB</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">MAR</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">APR</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">MAY</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">JUN</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">JUL</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">AUG</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">SEP</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">OCT</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">NOV</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">DEC</option>");
         out.println("</select>&nbsp;&nbsp;");
         
         out.println("Day:&nbsp;&nbsp;<select name=\"eday\">");
         for (int i=0; i<31; i++) {
             if (eday == i+1) {
                 out.println("<option selected value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             } else {
                 out.println("<option value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             }
         }
         out.println("</select>&nbsp&nbsp;");
         
         out.println("Year:&nbsp;&nbsp;<select name=\"eyear\">");
         if ((eyear == 0 || eyear == cal_year)) {
             out.println("<option selected value=\"" + cal_year + "\">" + cal_year + "</option>");
             out.println("<option value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
         } else {
             out.println("<option value=\"" + cal_year + "\">" + cal_year + "</option>");
             out.println("<option selected value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
         }
         
         out.println("</select></td>");
         out.println("</tr><tr>");
         
         if (!user.equals(DINING_USER)) {     // skip this section if Dining Admin User
      
            out.println("<td align=\"right\">Form Text:</td>");
            out.println("<td align=\"left\"><textarea rows=\"6\" cols=\"40\" name=\"formText\">" + formText.trim() + "</textarea></td>");
            out.println("</tr><tr>");
      
         } else {

             out.println("<input type=\"hidden\" name=\"formText\" value=\"\">");
         }
         out.println("<td align=\"right\">Prompt Text:</td>");
         out.println("<td align=\"left\"><textarea rows=\"6\" cols=\"40\" name=\"promptText\">" + promptText.trim() + "</textarea></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Link Text:</td>");
         out.println("<td><input type=\"text\" name=\"linkText\" size=\"40\" maxlength=\"80\" value=\"" + linkText + "\"></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\" valign=\"top\">&nbsp;Recurrence: </td>");
         out.println("<td align=\"left\" valign=\"top\">");
         out.println("<table border=\"0\" cellpadding=\"2\" style=\"font-size: 10pt; font-weight: normal;\">");     // Recurrence table
         out.println("<tr><td align=\"left\" valign=\"top\">");
         
         String checkedE = "";
         String checkedEO = "";
         
         if (recurrArr[0] != 1) {
             checkedE = "checked";
         } else {
             checkedEO = "checked";
         }
         
         
         out.println("<input type=\"radio\" name=\"eo_week\" value=\"every\" " + checkedE + ">Every<br>");
         out.println("<input type=\"radio\" name=\"eo_week\" value=\"everyother\" " + checkedEO + ">Every other");
         out.println("</td><td>");
         
         String [] checked = new String[7];
         for (int i=0; i<7; i++) {
             if (recurrArr[i+1] == 1) {
                 checked[i] = "checked";
             } else {
                 checked[i] = "";
             }
         }
         out.println("<input type=\"checkbox\" name=\"day1\" value=\"yes\" " + checked[0] + ">Sunday<br>");
         out.println("<input type=\"checkbox\" name=\"day2\" value=\"yes\" " + checked[1] + ">Monday<br>");
         out.println("<input type=\"checkbox\" name=\"day3\" value=\"yes\" " + checked[2] + ">Tuesday<br>");
         out.println("<input type=\"checkbox\" name=\"day4\" value=\"yes\" " + checked[3] + ">Wednesday<br>");
         out.println("<input type=\"checkbox\" name=\"day5\" value=\"yes\" " + checked[4] + ">Thursday<br>");
         out.println("<input type=\"checkbox\" name=\"day6\" value=\"yes\" " + checked[5] + ">Friday<br>");
         out.println("<input type=\"checkbox\" name=\"day7\" value=\"yes\" " + checked[6] + ">Saturday<br>");
         
         out.println("</td></tr></table>");     // End of recurrence table
         
         out.println("<input type=\"hidden\" name=\"priority\" value=\"" + priority + "\">");
         
         out.println("</td></tr>");
         out.println("</table>");       // End of right side table
         out.println("</td></tr>");
         out.println("</form>");
         out.println("</table><br><br>");       // End of outer table
         out.println("</div>");
         out.println("</body></html>");
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of doMessages
 
 
 // ********************************************************************************
 //  Process the Dining Emails Management portion of the dining system configuration
 // ********************************************************************************

 private void doEmails(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     Statement stmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     //
     //  Dining Request Emails List
     //
     try {
         
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT * FROM dining_emails ORDER BY address");
         
         out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('emailsiframediv').offsetHeight, 'emailsiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
         out.println("<div id=\"emailsiframediv\">");
         out.println("<table border=\"0\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");
         out.println("<tr><td align=\"right\">Add the email addresses you would like <b>all</b> dining requests to be sent to:</td>");
         out.println("<td align=\"center\" width=\"300\">");
         out.println("<table border=\"0\"  bgcolor=\"#F5F5DC\" width=\"300\">");        // Emails table
         out.println("<form action=\"Proshop_dining?emails\" method=\"POST\" name=\"emailsform\">");
         out.println("<tr align=\"center\" width=\"300\">");
         out.println("<td><select name=\"diningEmail\" size=\"7\" style=\"width: 300\">");
         
         while (rs.next()) {
             out.println("<option value=\"" + rs.getString("id") + "\">" + rs.getString("address") + "</option>");
         }
         
         out.println("</select></td></tr>");
         out.println("<tr><td align=\"center\"><input type=\"submit\" value=\"Remove\" name=\"removeEmail\" style=\"width:80px\" onclick=\"if (document.getElementById('diningEmail').selectedIndex == -1) return false; return confirm('Are you sure you want to delete the selected Email Address?');\"></td></tr>");
         out.println("<tr><td align=\"center\"><hr></td></tr>");
         out.println("</form><form action=\"Proshop_dining?emails\" method=\"POST\" name=\"emailsform2\">");
         out.println("<tr><td align=\"center\"><input type=\"text\" name=\"emailToAdd\" size=\"30\" maxlength=\"50\"></td>");
         out.println("<tr><td align=\"center\"><input type=\"submit\" value=\"Add\" name=\"addEmail\" style=\"width:80px\" onclick=\"if (document.getElementById('emailToAdd').value == '') return false;\"></td></tr>");
         out.println("</form>");
         out.println("</table>");
         out.println("</td></tr>");
         out.println("</table><br><br>");
         out.println("</div>");
         out.println("</body></html>");
         
         
         stmt.close();
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of doEmails
 
 
 // ********************************************************************************
 //  Process the Dining Rooms Management portion of the dining system configuration
 // ********************************************************************************

 private void doRooms(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException {
     
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     int roomId = 0;
     
     String roomName = "";
     String roomDesc = "";
     
     //
     //  Dining Request Emails List
     //
     try {
         if (req.getParameter("diningRoom") != null && req.getParameter("updateRoom") == null) {
             roomId = Integer.parseInt(req.getParameter("diningRoom"));
         }
         
         if (roomId != 0) {
             pstmt = con.prepareStatement("SELECT * FROM dining_rooms WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, roomId);
             
             rs = pstmt.executeQuery();
             
             if (rs.next()) {
                 roomName = rs.getString("name");
                 roomDesc = rs.getString("description");
             }
             
             pstmt.close();
         }
         
         out.println("<body onload=\"parent.window.resizeIFrame(document.getElementById('roomsiframediv').offsetHeight, 'roomsiframe');\" bgcolor=\"#F5F5DC\" text=\"#000000\">");
         out.println("<div id=\"roomsiframediv\">");
         out.println("<table border=\"0\" cellpadding=\"5\" width=\"800\" bgcolor=\"F5F5DC\">");        // Outer table
         out.println("<tr><td align=\"center\" colspan=\"2\">");
         out.println("Add the names and descriptions of all dining rooms at your club to the list below for them to be selectable on the dining request form.  If no dining rooms are defined, users will be able to enter their own dining room name." +
                 "<br><br>Changes are saved whenever the Add, Update, or Remove buttons are clicked as long as all data in the form is valid.<br>");
         out.println("</td></tr>");
         out.println("<form action=\"Proshop_dining?rooms\" method=\"POST\" name=\"roomsform\">");
         out.println("<tr><td align=\"center\">");
         out.println("<table border=\"0\" bgcolor=\"F5F5DC\">");      // Right side table (select box, submit buttons)
         out.println("<tr><td align=\"center\" valign=\"top\" width=\"300\">");
         out.println("<select name=\"diningRoom\" size=\"7\" style=\"width: 300\" onchange=\"document.roomsform.submit()\">");
         
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT * FROM dining_rooms ORDER BY name");
         
         while (rs.next()) {
             if (Integer.parseInt(rs.getString("id")) == roomId) {
                 out.println("<option value=\"" + rs.getString("id") + "\" selected>" + rs.getString("name") + "</option>");
             } else {
                 out.println("<option value=\"" + rs.getString("id") + "\">" + rs.getString("name") + "</option>");
             }
         }
         
         stmt.close();
         
         out.println("</select></td></tr>");
         out.println("<tr><td align=\"center\" valign=\"top\">");
         // Print submit buttons
         if (roomId != 0) {
             out.println("<input type=\"submit\" name=\"updateRoom\" value=\"Update\" style=\"width:80px\">");
         } else {
             out.println("<input type=\"submit\" name=\"addRoom\" value=\"Add\" style=\"width:80px\">");
         }
         out.println("&nbsp;");
         out.println("<input type=\"submit\" name=\"removeRoom\" value=\"Remove\" style=\"width:80px\">");
         out.println("&nbsp;<button style=\"width:80px\" onclick=\"location.href='Proshop_dining?rooms'\">Reset</button>");
         out.println("</td></tr>");
         out.println("</table>");       // End of right side table
         out.println("</td><td align=\"center\">");
         out.println("<table border=\"0\" bgcolor=\"F5F5DC\">");      // Left side table (name/description text spaces)
         out.println("<tr><td align=\"right\">Name:</td>");
         out.println("<td align=\"left\"><input type=\"text\" name=\"name\" value=\"" + roomName + "\" size=\"30\" maxlength=\"30\"></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Description:</td>");
         out.println("<td align=\"left\"><textarea rows=\"8\" cols=\"40\" name=\"description\">" + roomDesc.trim() + "</textarea></td>");
         out.println("</tr>");
         out.println("</table>");       // End of left side table
         out.println("</td></tr>");
         out.println("</form>");
         out.println("</table><br><br>");       // End of outer table
         out.println("</div>");
         out.println("</body></html>");
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of doRooms
 
 
 // ********************************************************************************
 //  Process updates for the Main Dining System Configuration
 // ********************************************************************************

 private void updateDining(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     //
     //  Declare variables
     //
     String formText = "";
     String linkText = "";
     String promptText = "";
     String customURL = "";
     
     int proMain = 0;
     int proTeetime = 0;
     int proLesson = 0;
     int memMain = 0;
     int memTeetime = 0;
     int memLesson = 0;
     int emailTeetime = 0;
     int emailLesson = 0;
     int result = 0;
     
     boolean warnNoLinks = false;
     
     try {
         //  Get parameter values
         formText = req.getParameter("formText");
         linkText = req.getParameter("linkText");
         promptText = req.getParameter("promptText");
         customURL = req.getParameter("customURL");

         if (req.getParameter("proMain") != null) {
             proMain = 1;
         }
         if (req.getParameter("proTeetime") != null) {
             proTeetime = 1;
         }
         if (req.getParameter("proLesson") != null) {
             proLesson = 1;
         }
         if (req.getParameter("memMain") != null) {
             memMain = 1;
         }
         if (req.getParameter("memTeetime") != null) {
             memTeetime = 1;
         }
         if (req.getParameter("memLesson") != null) {
             memLesson = 1;
         }
         if (req.getParameter("emailTeetime") != null) {
             emailTeetime = 1;
         }
         if (req.getParameter("emailLesson") != null) {
             emailLesson = 1;
         }

         // Sanity Checking
         if (proMain == 0 && proTeetime == 0 && proLesson == 0 && memMain == 0 && memTeetime == 0 &&
                 memLesson == 0 && emailTeetime == 0 && emailLesson == 0) {
             warnNoLinks = true;
         }
         
         if (req.getParameter("firstTime") != null) {
            
             if (user.equals(DINING_USER)) memMain = 0;       // DO NOT set this here if using ForeTees Dining - we will set this manually to turn on or off member access!!!
            
             pstmt = con.prepareStatement("INSERT INTO dining_config " +
                     "(form_text, link_text, prompt_text, pro_main, pro_teetime, " +
                     "pro_lesson, mem_main, mem_teetime, mem_lesson, email_teetime, " +
                     "email_lesson, custom_url) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
             pstmt.clearParameters();
             pstmt.setString(1, formText.trim());
             pstmt.setString(2, linkText.trim());
             pstmt.setString(3, promptText.trim());
             pstmt.setInt(4, proMain);
             pstmt.setInt(5, proTeetime);
             pstmt.setInt(6, proLesson);
             pstmt.setInt(7, memMain);
             pstmt.setInt(8, memTeetime);
             pstmt.setInt(9, memLesson);
             pstmt.setInt(10, emailTeetime);
             pstmt.setInt(11, emailLesson);
             pstmt.setString(12, customURL.trim());
             
             result = pstmt.executeUpdate();
             
         } else { 
            
             if (user.equals(DINING_USER)) {   // DO NOT set mem_main here if using ForeTees Dining - we will set this manually to turn on or off member access!!!
            
                pstmt = con.prepareStatement("UPDATE dining_config SET " +
                        "form_text = ?, link_text = ?, prompt_text = ?, pro_main = ?, pro_teetime = ?, " +
                        "pro_lesson = ?, mem_teetime = ?, mem_lesson = ?, email_teetime = ?, " +
                        "email_lesson = ?, custom_url = ?");
                
             } else {
                
                pstmt = con.prepareStatement("UPDATE dining_config SET " +
                        "form_text = ?, link_text = ?, prompt_text = ?, pro_main = ?, pro_teetime = ?, " +
                        "pro_lesson = ?, mem_teetime = ?, mem_lesson = ?, email_teetime = ?, " +
                        "email_lesson = ?, custom_url = ?, mem_main = ?");
             }
             pstmt.clearParameters();
             pstmt.setString(1, formText);
             pstmt.setString(2, linkText);
             pstmt.setString(3, promptText);
             pstmt.setInt(4, proMain);
             pstmt.setInt(5, proTeetime);
             pstmt.setInt(6, proLesson);
             pstmt.setInt(7, memTeetime);
             pstmt.setInt(8, memLesson);
             pstmt.setInt(9, emailTeetime);
             pstmt.setInt(10, emailLesson);
             pstmt.setString(11, customURL);
             if (!user.equals(DINING_USER)) {  
                pstmt.setInt(12, memMain);
             }

             result = pstmt.executeUpdate();
         }
         
         String url = "Proshop_dining?dining";
         
         
         out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
         
         if (result > 0) {
             out.println("<h4 align=\"center\">Update Successful!</h4>");
             out.println("<p align=\"center\">The Dining System Configuration was updated successfully!");
         } else {
             out.println("<h4 align=\"center\">Update Failed!</h4>");
             out.println("<p align=\"center\">The Dining System Configuration was not updated." +
                     "<br><br>Please contact Support if the problem persists.");
         }
         
         if (warnNoLinks) {
             out.println("<p align=\"center\"><h2><b>Warning:</b></h2>No prompt/link locations were selected.  This means no members or proshop users currently have access to making Dining Requests!!" +
                     "<br><br>The desired updates <i>have</i> been made, though promp/link locations will need to be selected once the system is to be used.");
         }
         
         out.println("<br><br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='" + url + "'\">Continue</button>");
         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=" + url + "\">"); // auto-refresh
         
         pstmt.close();
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?dining\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     
 }  // end of updateDining
 
 
 // ********************************************************************************
 //  Add a dining request email address to the system
 // ********************************************************************************

 private void updateMessage(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     boolean daysSelected = false;
     boolean msgAbsent = false;
     boolean nameError = false;
     boolean dateError = false;
     boolean dateRangeError = false;
     
     int results = 0;
     int id = 0;
     int count = 0;
     int priority = 0;
     int sdateCheck = 0;
     int edateCheck = 0;
     int smonthCheck = 0;
     int emonthCheck = 0;
     int sdayCheck = 0;
     int edayCheck = 0;
     int syearCheck = 0;
     int eyearCheck = 0;
     
     String name = "";
     String sdate = "";
     String smonth = "";
     String sday = "";
     String syear = "";
     String edate = "";
     String emonth = "";
     String eday = "";
     String eyear = "";
     String formText = "";
     String promptText = "";
     String linkText = "";
     
     int [] recurrArr = new int [8];        // index 0 = eo_week value, 1-7 = Sun-Sat values
     
     try {

         //
         //  Add a new Custom Message set using the currently defined data
         // 

         if (req.getParameter("addMsg") != null || (req.getParameter("updateMsg") != null && req.getParameter("diningMsg") != null)) {
             
             if (req.getParameter("updateMsg") != null && req.getParameter("diningMsg") != null) {
                 id = Integer.parseInt(req.getParameter("diningMsg"));
             }
             
             name = req.getParameter("name");
             smonth = req.getParameter("smonth");
             sday = req.getParameter("sday");
             syear = req.getParameter("syear");
             emonth = req.getParameter("emonth");
             eday = req.getParameter("eday");
             eyear = req.getParameter("eyear");
             formText = req.getParameter("formText").trim();
             promptText = req.getParameter("promptText").trim();
             linkText = req.getParameter("linkText");
             priority = Integer.parseInt(req.getParameter("priority"));

             if (req.getParameter("eo_week") != null && req.getParameter("eo_week").equalsIgnoreCase("everyother")) {
                 recurrArr[0] = 1;
             } else { 
                 recurrArr[0] = 0;
             }

             for (int i=1; i<8; i++) {
                 if (req.getParameter("day" + String.valueOf(i)) != null) {
                     recurrArr[i] = 1;
                     daysSelected = true;        // so we know if any days were selected
                 } else {
                     recurrArr[i] = 0;
                 }
             }
             
             if (smonth.length() == 1) { smonth = "0" + smonth; }
             if (sday.length() == 1) { sday = "0" + sday; }
             if (emonth.length() == 1) { emonth = "0" + emonth; }
             if (eday.length() == 1) { eday = "0" + eday; }
             
             sdate = syear + "-" + smonth + "-" + sday;
             edate = eyear + "-" + emonth + "-" + eday;
             
             smonthCheck = Integer.parseInt(smonth);
             emonthCheck = Integer.parseInt(emonth);
             sdayCheck = Integer.parseInt(sday);
             edayCheck = Integer.parseInt(eday);
             syearCheck = Integer.parseInt(syear);
             eyearCheck = Integer.parseInt(eyear);
             
             sdateCheck = Integer.parseInt(syear + smonth + sday);
             edateCheck = Integer.parseInt(eyear + emonth + eday);
             
             // Sanity checking
             if (name.equals("")) {
                 nameError = true;
             }
             if (sdate.equals("") || edate.equals("") ||
                     !Utilities.isValidDate(smonthCheck, sdayCheck, syearCheck) || !Utilities.isValidDate(emonthCheck, edayCheck, eyearCheck)) {
                 dateError = true;
             }
             if (sdateCheck > edateCheck) {
                 dateRangeError = true;
             }
             if (formText.equals("") && promptText.equals("") && linkText.equals("")) {
                 msgAbsent = true;
             }

             if (nameError || dateError || dateRangeError || msgAbsent || !daysSelected) {

                 out.println("<BODY><CENTER><BR>");
                 out.println("<BR><BR><H3>Invalid data entered!</H3>");
                 out.println("<BR><BR>The following problems were present in the custom message you submitted:<BR>");
                 out.println("<ul>");
                 if (nameError) {
                     out.println("  <li>Must specify a name for the custom message</li>");
                 }
                 if (dateError) {
                     out.println("  <li>Must enter valid start and end dates</li>");
                 }
                 if (dateRangeError) {
                     out.println("  <li>Start date cannot be later than the end date</li>");
                 }
                 if (msgAbsent) { 
                     out.println("  <li>Must include at least one custom message text (Form, Prompt, or Link)</li>");
                 }
                 if (!daysSelected) {
                     out.println("  <li>Must select at least one day of the week for message to occur on</li>");
                 }
                 out.println("</ul>");
                 out.println("Please correct the above and submit the dining request again.");
                 out.println("<form method=\"post\" action=\"Proshop_dining?messages\">");
                 out.println("<input type=\"hidden\" name=\"returning\" value=\"true\">");
                 out.println("<input type=\"hidden\" name=\"id\" value=\"" + id + "\">");
                 out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
                 out.println("<input type=\"hidden\" name=\"sdate\" value=\"" + sdate + "\">");
                 out.println("<input type=\"hidden\" name=\"edate\" value=\"" + edate + "\">");
                 out.println("<input type=\"hidden\" name=\"formText\" value=\"" + formText + "\">");
                 out.println("<input type=\"hidden\" name=\"promptText\" value=\"" + promptText + "\">");
                 out.println("<input type=\"hidden\" name=\"linkText\" value=\"" + linkText + "\">");
                 out.println("<input type=\"hidden\" name=\"priority\" value=\"" + priority + "\">");
                 out.println("<input type=\"hidden\" name=\"eo_week\" value=\"" + recurrArr[0] + "\">");
                 for (int i=1; i<8; i++) {
                     out.println("<input type=\"hidden\" name=\"day" + String.valueOf(i) + "\" value=\"" + recurrArr[i] + "\">");
                 }
                 out.println("<br><input type=\"submit\" value=\"Return\">");
                 out.println("</form>");
                 
                 out.close();
                 
             } else {
                 
                 // Check for other messages that would share the same priority and warn the user
                 String paramStr = "";
                 String priStr = "";
                 String eoweekStr = "";
                 
                 pstmt = con.prepareStatement("SELECT * FROM dining_messages " +
                         "WHERE id<>? AND active=1 AND ((sdate >= ? AND sdate <= ?) OR (edate <= ? AND edate >= ?)) " + paramStr +
                         "AND priority >= (-1 - DATEDIFF(?,?)) " + eoweekStr +
                         "ORDER BY priority DESC");
                 pstmt.clearParameters();
                 pstmt.setInt(1, id);
                 pstmt.setString(2, sdate);
                 pstmt.setString(3, edate);
                 pstmt.setString(4, edate);
                 pstmt.setString(5, sdate);
                 pstmt.setString(6, edate);
                 pstmt.setString(7, sdate);
                 if (!eoweekStr.equals("")) {
                     pstmt.setString(8, sdate);
                 }
                 
                 rs = pstmt.executeQuery();
                 
                 if (rs.next()) {
                     out.println("<BODY><CENTER><BR>");
                     out.println("<BR><BR><H3>Possible conflicts detected!</H3>");
                     out.println("<BR><BR>The message you just added may conflict with existing custom messages<BR><BR>" +
                             "You may wish to use the preview option below to verify that this message is properly displaying" +
                             " on days where it overlaps with any of the custom messages listed below (up to 5 most relevant shown):<br><br>");
                     out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
                     out.println("<tr><td align=\"left\">");
                     out.println("<ul>");
                     out.println("<li><b>" + rs.getString("name") + "</b>:&nbsp;&nbsp;" + rs.getString("sdate") + " - " + rs.getString("edate") + "</li>");
                     count++;
                     while (rs.next() && count < 5) {
                        out.println("<li><b>" + rs.getString("name") + "</b>:&nbsp;&nbsp;" + rs.getString("sdate") + " - " + rs.getString("edate") + "</li>");
                        count++;
                     }
                     out.println("</ul>");
                     out.println("</td></tr>");
                     out.println("<tr><td align=\"center\">");
                     
                     out.println("<button align=\"center\" style=\"width:80px\" onclick=\"location.href='Proshop_dining?preview&date=" + sdate + "'\">Preview</button>");
                     out.println("<button align=\"center\" style=\"width:80px\" onclick=\"location.href='Proshop_dining?messages'\">Continue</button>");
                     out.println("</td></tr></table>");
                     out.close();
                 }
                 
                 pstmt.close();
                 
                 String [] daysArr = new String[7];
                 
                 daysArr[0] = "sunday";
                 daysArr[1] = "monday";
                 daysArr[2] = "tuesday";
                 daysArr[3] = "wednesday";
                 daysArr[4] = "thursday";
                 daysArr[5] = "friday";
                 daysArr[6] = "saturday";
                 
                 for (int i=0; i<7; i++) {
                     if (recurrArr[i+1] == 1) {
                         if (paramStr.equals("")) {
                             paramStr = "AND (";
                         } else {
                             paramStr += " OR ";
                         }
                         paramStr += daysArr[i] + "=1";
                     }
                 }
                 
                 if (!paramStr.equals("")) {
                     paramStr += ") ";
                 }
                 
                 if (recurrArr[0] == 1) {
                     
                     // Occurrs every other week, can ignore any that don't occur on opposite weeks
                     eoweekStr = "AND (eo_week=0 OR MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2)) ";
                 }
                 
                 if (req.getParameter("addMsg") != null) {
                     pstmt = con.prepareStatement("INSERT INTO dining_messages " +
                             "(name, sdate, edate, form_text, prompt_text, link_text, priority, eo_week, sunday, monday, " +
                             "tuesday, wednesday, thursday, friday, saturday) " +
                             "VALUES " +
                             "(?,?,?,?,?,?,(-1 - DATEDIFF(?,?)),?,?,?," +
                             "?,?,?,?,?)");
                     pstmt.clearParameters();
                     pstmt.setString(1, name);
                     pstmt.setString(2, sdate);
                     pstmt.setString(3, edate);
                     pstmt.setString(4, formText);
                     pstmt.setString(5, promptText);
                     pstmt.setString(6, linkText);
                     pstmt.setString(7, edate);
                     pstmt.setString(8, sdate);
                     pstmt.setInt(9, recurrArr[0]);
                     for (int i=0; i<7; i++) {
                         pstmt.setInt(10+i, recurrArr[i + 1]);
                     }

                 } else {       // updateMsg parameter
                     pstmt = con.prepareStatement("UPDATE dining_messages SET " +
                             "name = ?, sdate = ?, edate = ?, form_text = ?, prompt_text = ?, " +
                             "link_text = ?, priority = (-1 - DATEDIFF(?,?)), eo_week = ?, sunday = ?, monday = ?, " +
                             "tuesday = ?, wednesday = ?, thursday = ?, friday = ?, saturday = ? " +
                             "WHERE id = ?");
                     pstmt.clearParameters();
                     pstmt.setString(1, name);
                     pstmt.setString(2, sdate);
                     pstmt.setString(3, edate);
                     pstmt.setString(4, formText);
                     pstmt.setString(5, promptText);
                     pstmt.setString(6, linkText);
                     pstmt.setString(7, edate);
                     pstmt.setString(8, sdate);
                     pstmt.setInt(9, recurrArr[0]);
                     for (int i=0; i<7; i++) {
                         pstmt.setInt(10+i, recurrArr[i + 1]);
                     }
                     pstmt.setInt(17, id);
                 }

                 results = pstmt.executeUpdate();

                 if (results <= 0) {
                     out.println(SystemUtils.HeadTitle("DB Connection Error"));
                     out.println("<BODY><CENTER>");                 
                     out.println("<BR><BR><H3>Database Connection Error</H3>");
                     out.println("<BR><BR>Unable to connect to the Database.");
                     out.println("<BR>Please try again later.");
                     out.println("<BR><BR>If problem persists, contact customer support.");
                     out.println("<BR><BR>");
                     out.println("<a href=\"Proshop_dining?messages\">Home</a>");
                     out.println("</CENTER></BODY></HTML>");
                     out.close();
                     return;
                 }

                 pstmt.close();
             }
         }
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?messages\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         out.close();
         return;
     }
 }      // End of updateMessage
 
 
 // ********************************************************************************
 //  Set a dining room as inactive (remove from use, save for future use)
 // ********************************************************************************

 private void removeMessage(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     int results = 0;
     
     try {
         if (req.getParameter("diningMsg") != null) {
             
             int id = Integer.parseInt(req.getParameter("diningMsg"));
             
             pstmt = con.prepareStatement("UPDATE dining_messages SET active=0 WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, id);

             results = pstmt.executeUpdate();

             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?messages\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
         }
           
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?messages\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of removeDiningRoom
 
 
 // ********************************************************************************
 //  Add a dining request email address to the system
 // ********************************************************************************

 private void addEmail(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     int results = 0;
     
     try {
         if (req.getParameter("emailToAdd") != null && !req.getParameter("emailToAdd").equals("")) {
             
             String emailToAdd = req.getParameter("emailToAdd");

             pstmt = con.prepareStatement("INSERT INTO dining_emails (address) VALUES (?)");
             pstmt.clearParameters();
             pstmt.setString(1, emailToAdd);

             results = pstmt.executeUpdate();

             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?emails\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
         }
               
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?emails\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of addEmail
 
 
 // ********************************************************************************
 //  Remove a dining request email address from the system
 // ********************************************************************************
 private void removeEmail(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     int results = 0;
     
     try {
         if (req.getParameter("diningEmail") != null) {
             
             int id = Integer.parseInt(req.getParameter("diningEmail"));
             
             pstmt = con.prepareStatement("DELETE FROM dining_emails WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, id);

             results = pstmt.executeUpdate();

             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?emails\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
         }
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?emails\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of removeEmail
 
 // ********************************************************************************
 //  Add a dining room to the system
 // ********************************************************************************

 private void updateDiningRoom(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     
     int results = 0;
     int id = 0;
     
     String name = "";
     String description = "";
      
     try {
         if (req.getParameter("addRoom") != null && !req.getParameter("name").equals("")) {
             
             //
             //  Add a new Dining Room using the currently defined data
             // 
             name = req.getParameter("name");
             description = req.getParameter("description");

             pstmt = con.prepareStatement("INSERT INTO dining_rooms (name, description) VALUES (?,?)");
             pstmt.clearParameters();
             pstmt.setString(1, name);
             pstmt.setString(2, description.trim());

             results = pstmt.executeUpdate();
             
             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");                 
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
             
         } else if (req.getParameter("updateRoom") != null && req.getParameter("diningRoom") != null &&
                 !req.getParameter("name").equals("")) {
             
             //
             //  Apply updates to the currently selected Dining Room
             // 
             id = Integer.parseInt(req.getParameter("diningRoom"));
             name = req.getParameter("name");
             
             if (req.getParameter("description") != null) {
                 description = req.getParameter("description");
             }
             
             pstmt = con.prepareStatement("UPDATE dining_rooms SET name = ?, description = ? WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setString(1, name);
             pstmt.setString(2, description);
             pstmt.setInt(3, id);
             
             results = pstmt.executeUpdate();
             
             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
         }
               
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
 }      // End of updateDiningRoom
 
 // ********************************************************************************
 // remove a dining room from the system
 // ********************************************************************************

 private void removeDiningRoom(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     PreparedStatement pstmt = null;
     int results = 0;
     
     try {
         if (req.getParameter("diningRoom") != null) {
             
             int id = Integer.parseInt(req.getParameter("diningRoom"));
             
             pstmt = con.prepareStatement("DELETE FROM dining_rooms WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, id);

             results = pstmt.executeUpdate();

             if (results <= 0) {
                 out.println(SystemUtils.HeadTitle("DB Connection Error"));
                 out.println("<BODY><CENTER>");
                 out.println("<BR><BR><H3>Database Connection Error</H3>");
                 out.println("<BR><BR>Unable to connect to the Database.");
                 out.println("<BR>Please try again later.");
                 out.println("<BR><BR>If problem persists, contact customer support.");
                 out.println("<BR><BR>");
                 out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
                 out.println("</CENTER></BODY></HTML>");
                 return;
             }
             
             pstmt.close();
         }
           
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
 }  // end of removeDiningRoom
 
 // ********************************************************************************
 //  display a preview of the Dining Request form that will display with the current configuration
 // ********************************************************************************

 private void displayPreview(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
   
   
     Calendar cal = new GregorianCalendar();       // get todays date
     Calendar cal2 = new GregorianCalendar();
     
     String date = "";
     String name = "";
     String day_name = "";
     String formText = "";
     String promptText = "";
     String linkText = "";
     String tempMonth = "";
     String tempDay = "";
     String tempYear = "";
     
     int year = 0;
     int month = 0;
     int day = 0;
     int dayOfWeek = 0;
     int curDate = 0;
     int newDate = 0;
     int cal_year = cal2.get(Calendar.YEAR);
     int cal_month = cal2.get(Calendar.MONTH) + 1; // month is 0-based
     int cal_day = cal2.get(Calendar.DAY_OF_MONTH);
     
     try {
         if (req.getParameter("date") != null) {
             
             // Set date to date other than today's date
             date = req.getParameter("date");
             
             StringTokenizer tok = new StringTokenizer(date, "-");
             
             if (tok.countTokens() == 3) {                
                 year = Integer.parseInt(tok.nextToken());
                 month = Integer.parseInt(tok.nextToken());
                 day = Integer.parseInt(tok.nextToken());
             } else {   
                 year = Integer.parseInt(date.substring(0,3));
                 month = Integer.parseInt(date.substring(4,5));
                 day = Integer.parseInt(date.substring(6,7));
             }
             
             cal.set(year, month - 1, day);
         }
         
         dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
         
         if (dayOfWeek == 1) { day_name = "sunday"; }
         if (dayOfWeek == 2) { day_name = "monday"; }
         if (dayOfWeek == 3) { day_name = "tuesday"; }
         if (dayOfWeek == 4) { day_name = "wednesday"; }
         if (dayOfWeek == 5) { day_name = "thursday"; }
         if (dayOfWeek == 6) { day_name = "friday"; }
         if (dayOfWeek == 7) { day_name = "saturday"; }
         
         tempYear = String.valueOf(cal_year);
         tempMonth = String.valueOf(cal_month);
         tempDay = String.valueOf(cal_day);
         
         if (tempMonth.length() == 1) { tempMonth = "0" + tempMonth; }
         if (tempDay.length() == 1) { tempDay = "0" + tempDay; }
         
         curDate = Integer.parseInt(tempYear + tempMonth + tempDay);
         
         tempYear = String.valueOf(year);
         tempMonth = String.valueOf(month);
         tempDay = String.valueOf(day);
         
         if (tempMonth.length() == 1) { tempMonth = "0" + tempMonth; }
         if (tempDay.length() == 1) { tempDay = "0" + tempDay; }
         
         newDate = Integer.parseInt(tempYear + tempMonth + tempDay);
         
         if (newDate < curDate) {       
             year = cal_year;
             month = cal_month;
             day = cal_day;
         }
         
         tempYear = String.valueOf(year);
         tempMonth = String.valueOf(month);
         tempDay = String.valueOf(day);
         
         if (tempMonth.length() == 1) { tempMonth = "0" + tempMonth; }
         if (tempDay.length() == 1) { tempDay = "0" + tempDay; }
         
         date = tempYear + "-" + tempMonth + "-" + tempDay;
         
         pstmt = con.prepareStatement("SELECT name, priority, form_text, prompt_text, link_text FROM dining_messages " +
                 "WHERE active=1 AND sdate <= ? AND edate >= ? AND " + day_name + "=1 AND " +
                 "(eo_week = 0 OR (MOD(DATE_FORMAT(sdate, '%U'), 2) = MOD(DATE_FORMAT(?, '%U'), 2))) " +
                 "ORDER BY priority DESC");
         pstmt.clearParameters();
         pstmt.setString(1, date);
         pstmt.setString(2, date);
         pstmt.setString(3, date);
         rs = pstmt.executeQuery();
         
         if (rs.next()) {
             name = rs.getString("name");
             formText = rs.getString("form_text");
             promptText = rs.getString("prompt_text");
             linkText = rs.getString("link_text");
         }
         
         pstmt.close();
         
         if (formText.equals("") || promptText.equals("") || linkText.equals("")) {
             
             // get default values for whatever's missing
             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT form_text, prompt_text, link_text FROM dining_config");
             
             if (rs.next()) {
                 if (formText.equals("")) { formText = rs.getString("form_text"); }
                 if (promptText.equals("")) { promptText = rs.getString("prompt_text"); }
                 if (linkText.equals("")) {linkText = rs.getString("link_text"); }
             }
             
             stmt.close();
         }
           
         out.println(SystemUtils.HeadTitle2("Proshop - Custom Dining Message Preview"));
         out.println("</head>");
         
         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");
         out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
    
         out.println("<table border=\"0\" cellpadding=\"5\" align=\"center\">");
         out.println("<tr><td align=\"center\">");
         out.println("<h3>Custom Dining Message Preview for " + month + "-" + day + "-" + year + "</h3>");
         out.println("</td></tr>");
         
         out.println("<tr><td align=\"center\">");
         
         // include files for dynamic calendars
         out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv30-styles.css\">");
         out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv30-scripts.js\"></script>");
     
         // div for cal
         out.println("<div id=cal_elem_0 style=\"position: relative; top: 0px; left: 0px; width: 180px; height: 150px\"></div>\n");
         
         // start calendar javascript setup code
         out.println("<script type=\"text/javascript\">");
         out.println("<!--");
         
         out.println("var g_cal_bg_color = '#F5F5DC';");
         out.println("var g_cal_header_color = '#8B8970';");
         out.println("var g_cal_border_color = '#8B8970';");
         
         out.println("var g_cal_count = 1;"); // number of calendars on this page
         out.println("var g_cal_year = new Array(g_cal_count - 1);");
         out.println("var g_cal_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_month = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_day = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_day = new Array(g_cal_count - 1);");
         out.println("var g_cal_beginning_year = new Array(g_cal_count - 1);");
         out.println("var g_cal_ending_year = new Array(g_cal_count - 1);");
         
         
         // set calendar date parts in js
         out.println("g_cal_month[0] = " + month + ";");
         out.println("g_cal_year[0] = " + year + ";");
         out.println("g_cal_beginning_month[0] = " + cal_month + ";");
         out.println("g_cal_beginning_year[0] = " + cal_year + ";");
         out.println("g_cal_beginning_day[0] = " + cal_day + ";");
         
         cal2.add(Calendar.YEAR, 1); // add a year
         cal_year = cal2.get(Calendar.YEAR);
         cal_month = cal2.get(Calendar.MONTH) + 1; // month is zero based
         out.println("g_cal_ending_month[0] = " + cal_month + ";");
         out.println("g_cal_ending_day[0] = " + cal_day + ";");
         out.println("g_cal_ending_year[0] = " + cal_year + ";");
         cal2.add(Calendar.YEAR, -1); // subtract a year
         
         // override the function that's called when user clicks day on calendar
         out.println("function sd(pCal, pMonth, pDay, pYear) {");
         out.println("  f = document.forms['frmPreview'];");
         out.println("  f.date.value = pYear + \"-\" + pMonth + \"-\" + pDay;");
         out.println("  f.submit();");
         out.println("}");
         
         out.println("// -->");
         out.println("</script>");
         
         out.println("<script type=\"text/javascript\">\ndoCalendar('0');\n</script>");
         
         out.println("</td></tr>");
         if (!name.equals("")) {
             out.println("<tr><td align=\"center\">Custom Message: " + name + "</td></tr>");
         } else {
             out.println("<tr><td align=\"center\">No Custom Message - Default Displayed</td></tr>");
         }
         
         if (!linkText.equals("")) {
             out.println("<tr><td align=\"center\">");
             out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt;\">");
             if (!user.equals(DINING_USER)) {
                out.println("<tr><td align=\"center\" bgcolor=\"#336633\"><font size=\"3\" color=\"#FFFFFF\"><b>Prompt & Link Text</b></font></td></tr>");
             }
             out.println("<tr><td align=\"center\">");
             if (!promptText.equals("")) {
                 out.println("<p>" + promptText + "<br><br>");
             }
             out.println("<a href=\"Proshop_dining?preview&date=" + date + "\">" + linkText + "</a>");
             out.println("</td></tr></table><br>");
             out.println("</td></tr>");
         }
         
         if (!formText.equals("") && !user.equals(DINING_USER)) {
             out.println("<tr><td align=\"center\">");
             out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" width=\"350px\">");
             out.println("<tr><td align=\"center\" bgcolor=\"#336633\"><font size=\"3\" color=\"#FFFFFF\"><b>Form Text</b></font></td></tr>");
             out.println("<tr><td align=\"center\" width=\"350px\">");
             out.println("<pre><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">" + formText + "</font></pre>");
             out.println("</td></tr></table><br>");
             out.println("</td></tr>");
         }
         
         out.println("<tr><td align=\"center\">");
         out.println("<button align=\"center\" style=\"width:80px\" onclick=\"location.href='JavaScript:window.close()'\">Close</button>");
         out.println("</td></tr></table>");
         
         out.println("<form method=\"POST\" name=\"frmPreview\" action=\"Proshop_dining?preview\">");
         out.println("<input type=\"hidden\" name=\"date\" value=\"" + date + "\">");
         out.println("</form>");
         out.println("</body></html>");
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_dining?rooms\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     
 }  // end of displayPreview
 
 
 // ********************************************************************************
 //  Method to build and display the dining request form.
 // ********************************************************************************
 public static void buildDiningForm(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     // Declare variables
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     boolean isProshop = false;
     boolean diningRooms = false;
     
     String name = "";
     String email = "";
     String email2 = "";
     String phone1 = "";
     String phone2 = "";
     String formText = "";
     String diningRoom = "";
     String notes = "";
     String ampm = "";
     String min = "00";
     String caller = "main";        // main, teetime, lesson, email
     String sub = "";
     String index = "";
     String course = "";
     String jump = "";
     String proid = "";
     String calDate = "";
     String search = "";
     String user_group = "";        // proshop, member, email
     String username = "";
     String date = "";
     String group = "";
     String [] selectArr = new String[31];          // Array to hold selection values for form selection boxes
     
     
     int month = 0;
     int day = 0;
     int year = 0;
     int cal_year = 0;
     int hour = 0;
     int diningRoomId = 0;
     int partySize = 0;
     int customId = 0;
     
     String user = (String)session.getAttribute("user");
     if (user == null) user = (String)session.getAttribute("ext-user");
     String club = (String)session.getAttribute("club");

     // Verify that system is active and configured
     if (!Utilities.checkDiningStatus(con)) {
         out.println(SystemUtils.HeadTitle("Dining System Not Active"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Dining System Not Configured</H3>");
         out.println("<BR><BR>The dining system is not active or not configured properly.");
         out.println("<BR>Please verify that the system is properly set up and try again.");
         out.println("<BR><BR>If the problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     //  Determine if user is a proshop user or not
     isProshop = ProcessConstants.isProshopUser(user);
     
     out.println("<script type=\"text/javascript\">");
     out.println("<!--");
     out.println("function subletter(x) {");
     out.println(" document.playerform.letter.value = x;");
     out.println(" playerform.submit();");
     out.println("}");
     out.println("function movename(nameinfo) {");
     out.println(" array = nameinfo.split(':');"); // split string (name, email, email2, phone1, phone2)
     out.println(" var name = array[0];");
     out.println(" var email = array[1];");
     out.println(" var email2 = array[2];");
     out.println(" var phone1 = array[3];");
     out.println(" var phone2 = array[4];");
     out.println(" f = document.forms['diningRequestForm'];");
     out.println(" f.name.value = name;");
     out.println(" f.email.value = email;");
     out.println(" f.email2.value = email2;");
     out.println(" f.phone1.value = phone1;");
     out.println(" f.phone2.value = phone2;");
     out.println("}");
     out.println("function submitForm(frameName) {");
     out.println(" f = document.forms[frameName];");
     out.println(" f.submit();");
     out.println("}");
     out.println("// -->");
     out.println("</script>");  
        
     //
     //  Get today's date
     //
     Calendar cal = new GregorianCalendar();       // get todays date
     
     month = cal.get(Calendar.MONTH) + 1;
     day = cal.get(Calendar.DAY_OF_MONTH);
     cal_year = cal.get(Calendar.YEAR);
     
     // 
     //  Build the main html page for the Dining Request Form
     //
     try {
         out.println(SystemUtils.HeadTitle2("Dining Request Form"));

         out.println("<script language=\"javascript\" src=\"/" +rev+ "/timeBox-scripts.js\"></script>");

         out.println("</head>");

         out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

         if (isProshop) {
             String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
             int lottery = Integer.parseInt(templott);
             SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
         } else {
             String caller2 = (String)session.getAttribute("caller");
             if (caller2 != null) SystemUtils.getMemberSubMenu(req, out, caller2);
         }
         
         // get caller and related parameters
         if (req.getParameter("caller") != null) { caller = req.getParameter("caller"); }
         if (req.getParameter("sub") != null) { sub = req.getParameter("sub"); }
         if (req.getParameter("index") != null) { index = req.getParameter("index"); }
         if (req.getParameter("course") != null) { course = req.getParameter("course"); }
         if (req.getParameter("jump") != null) { jump = req.getParameter("jump"); }
         if (req.getParameter("proid") != null) { proid = req.getParameter("proid"); }
         if (req.getParameter("group") != null) { group = req.getParameter("group"); }
         if (req.getParameter("calDate") != null) { calDate = req.getParameter("calDate"); }
         if (req.getParameter("search") != null) { search = req.getParameter("search"); }
         try {
             if (req.getParameter("customId") != null) { customId = Integer.parseInt(req.getParameter("customId")); }
         } catch (NumberFormatException ignore) {
             customId = 0;
         }
         try {
             if (req.getParameter("num") != null) { partySize = Integer.parseInt(req.getParameter("num")); }
         } catch (NumberFormatException ignore) {
             partySize = 0;
         }
         if (req.getParameter("usr") != null) { username = req.getParameter("usr"); }
         if (req.getParameter("date") != null) { date = req.getParameter("date"); }

         // Submit view stats
         if (isProshop) {
             user_group = "proshop";
         } else {
             user_group = "member";
         }
         
         out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");

         out.println("<center>");

         if (req.getParameter("returning") != null) {
             
             // Returning from a failed submission, repopulate the form with previous data
             name = req.getParameter("name");
             email = req.getParameter("email");
             email2 = req.getParameter("email2");
             phone1 = req.getParameter("phone1");
             phone2 = req.getParameter("phone2");
             diningRoomId = Integer.parseInt(req.getParameter("diningRoomId"));
             diningRoom = req.getParameter("diningRoom");
             partySize = Integer.parseInt(req.getParameter("partySize"));
             month = Integer.parseInt(req.getParameter("month"));
             day = Integer.parseInt(req.getParameter("day"));
             year = Integer.parseInt(req.getParameter("year"));
             hour = Integer.parseInt(req.getParameter("hour"));
             min = req.getParameter("min");
             ampm = req.getParameter("ampm");
             notes = req.getParameter("notes");
             
         } else {
             
             // Record this as a view (don't want to record the same "view" multiple times if they get
             // kicked back to the request form due to invalid input
             int statCount = 0;
             stmt = con.createStatement();
             statCount = stmt.executeUpdate("INSERT INTO dining_stats (date, date_time, type, user_group, caller, custom_id, count) VALUES (now(), \"0000-00-00 00:00:00\",\"view\", \"" + user_group + "\", \"" + caller + "\", " + customId + ", 1) ON DUPLICATE KEY UPDATE count = count + 1");
             
             stmt.close();
             
             if (!isProshop || !username.equals("")) {
                 if (username.equals("")) {
                     username = user;
                 }
                 
                 // Get data for this member from the database
                 pstmt = con.prepareStatement("SELECT name_first, name_last, memNum, email, email2, phone1, phone2 " +
                         "FROM member2b WHERE username = ?");
                 pstmt.clearParameters();
                 pstmt.setString(1, username);
                 rs = pstmt.executeQuery();

                 if (rs.next()) {
                     name = rs.getString("name_first") + " " + rs.getString("name_last") + " - " + rs.getString("memNum");
                     email = rs.getString("email");
                     email2 = rs.getString("email2");
                     phone1 = rs.getString("phone1");
                     phone2 = rs.getString("phone2");

                     if (email.equals("") && !email2.equals("")) {
                         email = email2;
                         email2 = "";
                     }

                     if (phone1.equals("") && !phone2.equals("")) {
                         phone1 = phone2;
                         phone2 = "";
                     }
                 }

                 pstmt.close();
             }
         }

         if (!date.equals("")) {
             StringTokenizer dateTok = new StringTokenizer(date, "-");
             
             if (dateTok.countTokens() == 3) {
                 year = Integer.parseInt(dateTok.nextToken());
                 month = Integer.parseInt(dateTok.nextToken());
                 day = Integer.parseInt(dateTok.nextToken());
             } else if (dateTok.countTokens() == 1) {       // date in yyyymmdd format
                 year = Integer.parseInt(date.substring(0, 4));
                 month = Integer.parseInt(date.substring(4, 6));
                 day = Integer.parseInt(date.substring(6));
             }
         }
         
         if (customId != 0) {
             String tempText = Utilities.getCustomDiningText("form_text", customId, con).trim();
             if (!tempText.equals("")) {
                 formText = tempText;
             }
         } 
         
         if (formText.equals("")) {
             
             // No custom form text, get the default
             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT form_text FROM dining_config");
             
             if (rs.next()) {
                 formText = rs.getString("form_text").trim();
             }
             
             stmt.close();
         }
         
         
         out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\"><tr><td align=\"center\" width=\"250\">");
         out.println("<font color=\"#FFFFFF\" size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
         out.println("<b>Dining Request Form</b><br>");
         out.println("Fill out the dining request form below.<br>");
         out.println("This is <u><b>not</b></u> a reservation, but a notification to the club so they can be prepared to best accomodate your party.<br>");
         out.println("</font>");
         out.println("</td></tr></table>");
         out.println("<br>");
         
         if (!formText.equals("")) {
             out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" width=\"350px\"><tr><td align=\"center\" width=\"350px\">");
             out.println("<font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">");
             out.println("<pre><font size=\"2\" face=\"Arial, Helvetica, Sans-serif\">" + formText + "</font></pre>");
             out.println("</font>");
             out.println("</td></tr></table>");
             out.println("<br>");
         }
         
         out.println("<table border=\"1\" cellpadding=\"5\" width=\"750px\" bgcolor=\"F5F5DC\">");      // Outer table
         
         if (formText.equals("")) {
             out.println("<tr><td colspan=\"3\" height=\"30\" align=\"center\" valign=\"center\" bgcolor=\"#336633\">");
             out.println("<font color=\"white\"><h3>Dining Request Form</h3></font>");
             out.println("</td></tr>");
         }
         out.println("<tr><td align=\"center\">");
         
         if (isProshop) {
             out.println("<form name=\"diningRequestForm\" method=\"POST\" action=\"Proshop_dining\">");
         } else {
             out.println("<form name=\"diningRequestForm\" method=\"POST\" action=\"Member_dining\">");

             if (req.getParameter("ext-dReq") != null) {
                 out.println("<input type=\"hidden\" name=\"ext-dReq\" value=\"\">");
             }
         }
         out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
         if (!sub.equals("")) { out.println("<input type=\"hidden\" name=\"sub\" value=\"" + sub + "\">"); }
         if (!index.equals("")) { out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">"); }
         if (!course.equals("")) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">"); }
         if (!jump.equals("")) { out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">"); }
         if (!proid.equals("")) { out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">"); }
         if (!group.equals("")) { out.println("<input type=\"hidden\" name=\"group\" value=\"" + group + "\">"); }
         if (!calDate.equals("")) { out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">"); }
         if (!search.equals("")) { out.println("<input type=\"hidden\" name=\"search\" value=\"" + search + "\">"); }
         if (customId != 0) { out.println("<input type=\"hidden\" name=\"customId\" value=\"" + customId + "\">"); }
         
         out.println("<table border=\"0\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt\">");    // Main request form table
         
         out.println("<tr><td align=\"right\">Name: </td><td align=\"left\">");
         if (isProshop) {
             out.println("<input type=\"text\" name=\"name\" value=\"" + name + "\"><font size=\"2\"> (Required)</font>");       
         } else {
             out.println(name);
             out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
         }
         
         out.println("</td></tr>");
         out.println("<tr>");
         out.println("<td align=\"right\">Email 1: </td><td align=\"left\"><input type=\"text\" name=\"email\" value=\"" + email.trim() + "\" size=\"20\" maxlength=\"50\"><font size=\"2\"> (Required)</font></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Email 2: </td><td align=\"left\"><input type=\"text\" name=\"email2\" value=\"" + email2.trim() + "\" size=\"20\" maxlength=\"50\"></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Phone 1: </td><td align=\"left\"><input type=\"text\" name=\"phone1\" value=\"" + phone1.trim() + "\" size=\"20\" maxlength=\"24\"></td>");
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Phone 2: </td><td align=\"left\"><input type=\"text\" name=\"phone2\" value=\"" + phone2.trim() + "\" size=\"20\" maxlength=\"24\"></td>");
         out.println("</tr><tr>");
         
         // Print the Dining Room selection box
         stmt = con.createStatement();
         rs = stmt.executeQuery("SELECT * FROM dining_rooms");
         
         if (rs.next()) {
             // If dining rooms are configured, print a select box containing all dining rooms
             diningRooms = true;
             
             out.println("<td align=\"right\">Dining Room: </td><td align=\"left\"><select name=\"diningRoomId\" style=\"width:160px\">");
             
             rs = stmt.executeQuery("SELECT * FROM dining_rooms");        // have to get them again
         
             while (rs.next()) {
                 
                 if (diningRoomId != 0 && rs.getInt("id") == diningRoomId) {
                     out.println("<option value=\"" + rs.getString("id") + "\" selected>" + rs.getString("name") + "</option>");
                 } else {
                     out.println("<option value=\"" + rs.getString("id") + "\">" + rs.getString("name") + "</option>");
                 }
             }
             
             out.println("</select></td>");
             
         } else {
             // If no dining rooms are configured, print a text box for user entry
             out.println("<td align=\"left\">Dining Room: </td><td align=\"left\"><input type=\"text\" name=\"diningRoom\" value=\"" + diningRoom + "\"size=\"30\" maxlength=\"30\"></td>");
         }
         
         stmt.close();
         
         out.println("</tr><tr>");
         
         // Print the party size selection box (1-30)
         out.println("<td align=\"right\">Party Size: </td><td align=\"left\"><select name=\"partySize\">");
         
         for (int i=0; i<50; i++) {
             if (((i+1) == partySize) || (partySize == 0 && i == 0)) {
                 out.println("<option value=\"" + (i+1) + "\" selected>" + (i+1) + "</option>");
             } else {
                 out.println("<option value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             }
         }
         out.println("</select></td>");
         out.println("</tr><tr>");
         
         // Print the date select boxes 
         out.println("<td align=\"right\">Date: </td>");
         out.println("<td align=\"left\" nowrap>");
         
         for (int i=0; i<12; i++) {
             if (month == i+1) {
                 selectArr[i] = " selected";
             } else {
                 selectArr[i] = "";
             }
         }
         out.println("Month:&nbsp;<select name=\"month\">");
              out.println("<option" + selectArr[0] + " value=\"01\">JAN</option>");
              out.println("<option" + selectArr[1] + " value=\"02\">FEB</option>");
              out.println("<option" + selectArr[2] + " value=\"03\">MAR</option>");
              out.println("<option" + selectArr[3] + " value=\"04\">APR</option>");
              out.println("<option" + selectArr[4] + " value=\"05\">MAY</option>");
              out.println("<option" + selectArr[5] + " value=\"06\">JUN</option>");
              out.println("<option" + selectArr[6] + " value=\"07\">JUL</option>");
              out.println("<option" + selectArr[7] + " value=\"08\">AUG</option>");
              out.println("<option" + selectArr[8] + " value=\"09\">SEP</option>");
              out.println("<option" + selectArr[9] + " value=\"10\">OCT</option>");
              out.println("<option" + selectArr[10] + " value=\"11\">NOV</option>");
              out.println("<option" + selectArr[11] + " value=\"12\">DEC</option>");
         out.println("</select>&nbsp;&nbsp;");
         
         out.println("Day:&nbsp;&nbsp;<select name=\"day\">");
         for (int i=0; i<31; i++) {
             if (day == i+1) {
                 out.println("<option selected value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             } else {
                 out.println("<option value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             }
         }
         out.println("</select>&nbsp&nbsp;");
         
         out.println("Year:&nbsp;&nbsp;<select name=\"year\">");
            if ((year == 0 || year == cal_year)) {
                out.println("<option selected value=\"" + cal_year + "\">" + cal_year + "</option>");
                out.println("<option value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
            } else {
                out.println("<option value=\"" + cal_year + "\">" + cal_year + "</option>");
                out.println("<option selected value=\"" + (cal_year + 1) + "\">" + (cal_year + 1) + "</option>");
            }
         out.println("</select>");
         
         out.println("</td></tr>");
                     
         out.println("</tr><tr>");
         
         // Print the Time select boxes
         out.println("<td align=\"right\"><span style=\"color:red;\">Time: </span></td>");
         out.println("<td align=\"left\" nowrap><span style=\"color:red;\">");
         out.println("Hr:&nbsp;&nbsp;<select name=\"hour\">");
         for (int i=0; i<12; i++) {
             if (hour == (i+1) || (hour == 0 && (i+1) == 12)) {
                 out.println("<option selected value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             } else {
                 out.println("<option value=\"" + (i+1) + "\">" + (i+1) + "</option>");
             }
         }
         out.println("</select>&nbsp;");
         out.println("Min:&nbsp;&nbsp;");
         out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"" + min + "\" name=\"min\">");
         out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
            
         out.println("<select size=\"1\" name=\"ampm\">");
            if (ampm.equalsIgnoreCase("AM") || ampm.equals("")) {
                out.println("<option value=\"AM\" selected>AM</option>");
                out.println("<option value=\"PM\">PM</option>");
            } else {
                out.println("<option value=\"AM\">AM</option>");
                out.println("<option value=\"PM\" selected>PM</option>");
            }
         out.println("</select>");
         out.println("</span></td>");
            
         out.println("</tr><tr>");
         out.println("<td align=\"right\">Notes/Special Requests: </td>");
         out.println("<td align=\"left\"><textarea name=\"notes\" rows=\"5\" cols=\"40\" wrap=\"hard\">" + notes.trim() + "</textarea></td>");
         out.println("</tr>");
         
         // Submit/Reset/Cancel buttons
         out.println("<tr><td align=\"center\" colspan=\"2\">");
         out.println("<table border=\"0\" cellpadding=\"5\" bgcolor=\"F5F5DC\" style=\"font-size:10pt\">");
         out.println("<tr><td align=\"right\" nowrap>");
         out.println("<input type=\"submit\" name=\"reqSubmit\" value=\"Submit\" style=\"width:80px\">");
         out.println("&nbsp;");
         out.println("<input type=\"reset\" name=\"reset\" value=\"Reset\" style=\"width:80px\">");
         out.println("</td></form>");
         
         if (isProshop) {
             if (caller.equals("main")) {
                 out.println("<td align=\"left\"><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_announce'\">Cancel</button><br></td>");
             } else if (caller.equals("teetime")) {
                 if (!sub.equals("")) {
                     if (!search.equals("")) {
                         out.println("<td align=\"left\"><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_jump&search=yes'\">Cancel</button><br></td>");
                     } else {
                         out.println("<td align=\"left\"><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_" + sub + ".htm'\">Cancel</button><br></td>");
                     }
                 } else {
                     out.println("<td align=\"left\">");
                     out.println("<button align=\"center\" style=\"width:80px\" onclick=\"submitForm('proTeetimeForm')\">Cancel</button>");
                     out.println("</td>");
                     out.println("<form name=\"proTeetimeForm\" action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("</form>");
                 }
             } else if (caller.equals("lesson")) {
                 out.println("<td align=\"left\">");
                 out.println("<button align=\"center\" style=\"width:80px\" onclick=\"submitForm('proLessonForm')\">Cancel</button>");
                 out.println("</td>");
                 out.println("<form name=\"proLessonForm\" action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                 out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                 out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                 out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                 out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                 out.println("</form>");
             }
             out.println("</tr></table>");
             
         } else {

             if (caller.equals("email")) { // name=\"Cancel\" value=\"Cancel\"
                 out.println("<td align=\"left\"><button onclick=\"window.close()\" style=\"width:80px\">Close</button></td>");
             } else if (caller.equals("main")) {
                 out.println("<td align=\"left\"><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Member_announce'\">Cancel</button><br></td>");
             } else if (caller.equals("teetime")) {
                 if (!sub.equals("")) {
                     out.println("<td align=\"left\"><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Member_" + sub + ".htm'\">Cancel</button><br></td>");
                 } else {
                     out.println("<td align=\"left\">");
                     out.println("<button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"submitForm('memTeetimeForm')\">Cancel</button>");
                     out.println("</td>");
                     out.println("<form name=\"memTeetimeForm\" action=\"Member_jump\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                     out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                     out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                     out.println("</form>");
                 }
             } else if (caller.equals("lesson")) {
                 out.println("<td align=\"left\">");
                 out.println("<button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"submitForm('memLessonForm')\">Cancel</button>");
                 out.println("</td>");
                 if (!group.equals("")) {
                     if (!proid.equals("")) {
                         out.println("<form name=\"memLessonForm\" action=\"Member_lesson\" method=\"post\">");
                         out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                         out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
                     } else {
                         out.println("<form name=\"memLessonForm\" action=\"Member_teelist\" method=\"get\">");
                     }
                 } else {
                     out.println("<form name=\"memLessonForm\" action=\"Member_jump\" method=\"post\" target=\"_top\">");
                     out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                     out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                     out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                 }
                 out.println("</form>");
             }
             out.println("</tr></table>");
         }
         out.println("</td></tr></font></table>");
         
         if (isProshop) {
             out.println("<td align=\"center\" valign=\"top\">");
             /*
             // if user clicked on a name letter or mtype
             if (req.getParameter("letter") != null || req.getParameter("return") != null || req.getParameter("mtypeopt") != null) { 
                 
                 if (req.getParameter("mtypeopt") != null) {
                     
                     mtypeOpt = req.getParameter("mtypeopt");
                     session.setAttribute("mtypeOpt", mtypeOpt);   //  Save the member class options in the session for next time
                 }
                 if (req.getParameter("mshipopt") != null) {
                     mshipOpt = req.getParameter("mshipopt");
                     session.setAttribute("mshipOpt", mshipOpt);
                 }
                 
             }
             
             String letter = "%";         // default is 'List All'
             if (req.getParameter("letter") != null) {
                 
                 letter = req.getParameter("letter");
                 
                 if (letter.equals( "List All" )) {
                     letter = "%";
                 } else {
                     letter = letter + "%";
                 }
             }
             */
             out.println("<form method=\"post\" name=\"playerform\" action=\"Proshop_dining?dReq&caller=" + caller + "\">");
             
             //
             //   Output the List of Names
             //
             alphaTable.nameList2(club, "%", "ALL", "ALL", out, con);
             
             
             out.println("</td>");     
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
             */
      
         } else {
             out.println("</td>");
         }
         out.println("</form></tr>");
         
         if (diningRooms) {
             out.println("<tr><td colspan=\"3\" align=\"center\" bgcolor=\"#336633\"><font color=\"white\" size=\"3\">");
             out.println("Dining Room Descriptions</font></td></tr>");

             // Print out dining room definitions
             stmt = con.createStatement();
             rs = stmt.executeQuery("SELECT * FROM dining_rooms");


             while (rs.next()) {
                 out.println("<tr><td colspan=\"3\">");
                     out.println("<table border=\"0\" cellpadding=\"5\" bgcolor=\"#F5F5DC\" style=\"font-size:10pt; width:750px;\">");     // Dining Room Descriptions
                        out.println("<tr><td align=\"left\"><b>" + rs.getString("name") + ":</b></td></tr>");
                        out.println("<tr><td align=\"left\">" + rs.getString("description") + "</td></tr>");
                     out.println("</table>");
                 out.println("</td></tr>");
             }
             
         }
         out.println("</table><br><br>");
         out.println("</font></body></html>");
         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>Error: " + exc.getMessage());
         out.println("<BR><BR>Error: " + exc.toString());
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
 }      // end of buildDiningForm
 
 
 // ********************************************************************************
 //  Method to process a dining request form submission
 // ********************************************************************************
 public static void processDiningForm(HttpServletRequest req, PrintWriter out, HttpSession session, Connection con)
         throws ServletException, IOException {
     
     // Declare Variables
     PreparedStatement pstmt = null;
     Statement stmt = null;
     ResultSet rs = null;
     
     parmEmail parme = new parmEmail();
     
     String name = "";
     String email = "";
     String email2 = "";
     String phone1 = "";
     String phone2 = "";
     String month = "";
     String day = "";
     String year = "";
     String hour = "";
     String min = "";     
     String ampm = "";
     String date = "";
     String time = "";
     String notes = "";
     String diningRoom = "";
     String caller = "main";
     String sub = "";
     String index = "";
     String course = "";
     String jump = "";
     String proid = "";
     String calDate = "";
     String search = "";
     String group = "";
     String user_group = "";
     
     int diningRoomId = 0;
     int partySize = 0;
     int customId = 0;
     
     boolean invalidMins = false;
     boolean noName = false;
     boolean noEmail = false;
     boolean isProshop = false;

     int sess_activity_id = (Integer)session.getAttribute("activity_id");

     String club = (String)session.getAttribute("club");
     String user = (String)session.getAttribute("user");
     if (user == null) user = (String)session.getAttribute("ext-user");

     // Verify that system is active and configured
     if (!Utilities.checkDiningStatus(con)) {
         out.println(SystemUtils.HeadTitle("Dining System Not Active"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Dining System Not Configured</H3>");
         out.println("<BR><BR>The dining system is not active or not configured properly.");
         out.println("<BR>Please verify that the system is properly set up and try again.");
         out.println("<BR><BR>If the problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     //  Determine if user is a proshop user or not
     isProshop = ProcessConstants.isProshopUser(user);
     
     try {
         // Take in parameter data
         if (req.getParameter("caller") != null) { caller = req.getParameter("caller"); }
         if (req.getParameter("sub") != null) { sub = req.getParameter("sub"); }
         if (req.getParameter("index") != null) { index = req.getParameter("index"); }
         if (req.getParameter("course") != null) { course = req.getParameter("course"); }
         if (req.getParameter("jump") != null) { jump = req.getParameter("jump"); }
         if (req.getParameter("proid") != null) { proid = req.getParameter("proid"); }
         if (req.getParameter("group") != null) { group = req.getParameter("group"); }
         if (req.getParameter("calDate") != null) { calDate = req.getParameter("calDate"); }
         if (req.getParameter("search") != null) { search = req.getParameter("search"); }
         if (req.getParameter("customId") != null) { customId = Integer.parseInt(req.getParameter("customId")); }
         if (req.getParameter("name") != null) { name = req.getParameter("name"); }
         if (req.getParameter("email") != null) { email = req.getParameter("email"); }
         if (req.getParameter("email2") != null) { email2 = req.getParameter("email2"); }
         if (req.getParameter("phone1") != null) { phone1 = req.getParameter("phone1"); }
         if (req.getParameter("phone2") != null) { phone2 = req.getParameter("phone2"); }
         month = req.getParameter("month");
         day = req.getParameter("day");
         year = req.getParameter("year");
         hour = req.getParameter("hour");
         if (req.getParameter("min") != null) { min = req.getParameter("min"); }
         ampm = req.getParameter("ampm");
         if (req.getParameter("notes") != null) { notes = req.getParameter("notes"); }

         if (req.getParameter("diningRoomId") != null) {

             diningRoomId = Integer.parseInt(req.getParameter("diningRoomId"));

             pstmt = con.prepareStatement("SELECT name FROM dining_rooms WHERE id = ?");
             pstmt.clearParameters();
             pstmt.setInt(1, diningRoomId);

             rs = pstmt.executeQuery();

             if (rs.next()) {
                 diningRoom = rs.getString("name");
             }
         } else {
             diningRoom = req.getParameter("diningRoom");
         }

         partySize = Integer.parseInt(req.getParameter("partySize"));
                  
         // Shuffle emails and phones to fill empty slots
         if (email.equals("") && !email2.equals("")) {
             email = email2;
             email2 = "";
         }
         
         if (phone1.equals("") && !phone2.equals("")) {
             phone1 = phone2;
             phone2 = "";
         }
         
         if (min.equals("") || Integer.parseInt(min) < 0 || Integer.parseInt(min) > 59) { invalidMins = true; }
         if (name.trim().equals("")) { noName = true; }
         if (email.trim().equals("")) { noEmail = true; }
         
         if (min.length() == 1) { min = "0" + min; }
         
         time = hour + ":" + min + " " + ampm.toUpperCase();
         date = month + "-" + day + "-" + year;
         
         if (noName || noEmail || invalidMins) {            // If there was invalid data sent, reject and return them
         
             out.println(SystemUtils.HeadTitle("Invalid Data"));
             out.println("<BODY><CENTER><BR>");
             out.println("<BR><BR><H3>Invalid data entered!</H3>");
             out.println("<BR><BR>The following problems were present in the dining request you submitted:<BR>");
             out.println("<ul>");
             if (noName) {
                 out.println("  <li>Must specify a name for the primary contact</li>");
             }
             if (noEmail) {
                 out.println("  <li>Must have at least one email address specified</li>");
             }
             if (invalidMins) { 
                 out.println("  <li>Minutes must be between 0 and 59</li>");
             }
             out.println("</ul>");
             out.println("Please correct the above and submit the dining request again.");
             if (isProshop) {
                 out.println("<form method=\"post\" action=\"Proshop_dining\">");
             } else {
                 out.println("<form method=\"post\" action=\"Member_dining\">");
             }
             out.println("<input type=\"hidden\" name=\"returning\" value=\"true\">");
             out.println("<input type=\"hidden\" name=\"caller\" value=\"" + caller + "\">");
             if (req.getParameter("ext-dReq") != null) {
                 out.println("<input type=\"hidden\" name=\"ext-dReq\" value=\"\">");
             }
             if (!sub.equals("")) { out.println("<input type=\"hidden\" name=\"sub\" value=\"" + sub + "\">"); }
             if (!index.equals("")) { out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">"); }
             if (!course.equals("")) { out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">"); }
             if (!jump.equals("")) { out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">"); }
             if (!proid.equals("")) { out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">"); }
             if (!group.equals("")) { out.println("<input type=\"hidden\" name=\"group\" value=\"" + group + "\">"); }
             if (!calDate.equals("")) { out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">"); }
             if (!search.equals("")) { out.println("<input type=\"hidden\" name=\"search\" value=\"" + search + "\">"); }
             if (customId != 0) { out.println("<input type=\"hiddein\" name=\"customId\" value=\"" + customId + "\">"); }
             out.println("<input type=\"hidden\" name=\"name\" value=\"" + name + "\">");
             out.println("<input type=\"hidden\" name=\"email\" value=\"" + email + "\">");
             out.println("<input type=\"hidden\" name=\"email2\" value=\"" + email2 + "\">");
             out.println("<input type=\"hidden\" name=\"phone1\" value=\"" + phone1 + "\">");
             out.println("<input type=\"hidden\" name=\"phone2\" value=\"" + phone2 + "\">");
             out.println("<input type=\"hidden\" name=\"diningRoomId\" value=\"" + diningRoomId + "\">");
             out.println("<input type=\"hidden\" name=\"diningRoom\" value=\"" + diningRoom + "\">");
             out.println("<input type=\"hidden\" name=\"partySize\" value=\"" + partySize + "\">");
             out.println("<input type=\"hidden\" name=\"month\" value=\"" + month + "\">");
             out.println("<input type=\"hidden\" name=\"day\" value=\"" + day + "\">");
             out.println("<input type=\"hidden\" name=\"year\" value=\"" + year + "\">");
             out.println("<input type=\"hidden\" name=\"hour\" value=\"" + hour + "\">");
             out.println("<input type=\"hidden\" name=\"min\" value=\"" + min + "\">");
             out.println("<input type=\"hidden\" name=\"ampm\" value=\"" + ampm + "\">");
             out.println("<input type=\"hidden\" name=\"notes\" value=\"" + notes + "\">");
             out.println("<br><input type=\"submit\" name=\"dReq\" value=\"Return\">");
             out.println("</form>");
             out.println("</CENTER></BODY></HTML>");
             
         } else {
             
             //  Populate the email parm block
             parme.activity_id = sess_activity_id;
             parme.club = club;
             parme.guests = 0;
             parme.type = "drequest";
             parme.emailNew = 1;
             parme.email = email;
             parme.user = user;
             parme.message = "Dining Room:  " + diningRoom + "\n";
             parme.message += "Date:  " + date + "\n";
             parme.message += "Time:  " + time + "\n";
             parme.message += "Party Size:  " + partySize + "\n\n";
             
             parme.message += "Name:  " + name + "\n";
             parme.message += "Email 1:  " + email + "\n";
             if (!email2.equals("")) {
                 parme.message += "Email 2:  " + email2 + "\n";
             }
             if (!phone1.equals("")) {
                 parme.message += "Phone 1:  " + phone1 + "\n";
             }
             if (!phone2.equals("")) {
                 parme.message += "Phone 2:  " + phone2 + "\n\n";
             }
             
             if (!notes.trim().equals("")) {
                 parme.message += "Notes:  " + notes + "\n";
             }
             
             sendEmail.sendIt(parme, con);
             
             // Construct the correctly formated dates and times and submit stats to database             
             if (ampm.equalsIgnoreCase("pm") && !hour.equals("12")) {
                 hour = String.valueOf(Integer.parseInt(hour) + 12);
             } else if (ampm.equalsIgnoreCase("am") && hour.equals("12")) {
                 hour = "0";
             }
             
             int ihour = Integer.parseInt(hour);
             
             if (ihour < 12) {
                 hour = "11";
             } else if (ihour <= 17) {
                 hour = "14";
             } else {
                 hour = "18";
             }
             
             if (month.length() == 1) { month = "0" + month; }
             if (day.length() == 1) { day = "0" + day; }
             
             if (isProshop) {
                 user_group = "proshop";
             } else {
                 user_group = "member";
             }
             
             int statCount = 0;
             stmt = con.createStatement();
             statCount = stmt.executeUpdate("INSERT INTO dining_stats (date, date_time, type, user_group, caller, custom_id, count) VALUES (\"0000-00-00\", \"" + year + "-" + month + "-" + day + " " + hour + ":00:00\",\"submit\", \"" + user_group + "\", \"" + caller + "\", " + customId + ", 1) ON DUPLICATE KEY UPDATE count = count + 1");
             
             stmt.close();
             
             // Print confirmation message
             out.println("<body bgcolor=\"#F5F5DC\" text=\"#000000\">");
             out.println("<font face=\"Arial, Helvetica, Sans-serif\" size=\"2\">");
             out.println("<h4 align=\"center\">Dining Request Sent!</h4>");
             out.println("<p align=\"center\">The dining request was sent!");
             
             out.println("<center>");
             if (isProshop) {
                 if (caller.equals("main")) {
                     out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_announce'\">Return</button><br>");
                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_announce\">"); // auto-refresh
                 } else if (caller.equals("teetime")) {
                     if (!sub.equals("")) {
                         if (!search.equals("")) {
                             out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclic=\"location.href='Proshop_jump&search=yes'\">Return</button><br>");
                             out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?search=yes\">");
                         } else {
                             out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Proshop_" + sub + ".htm'\">Return</button><br>");
                         }
                     } else {
                         out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                         out.println("<br><input type=\"submit\" value=\"Return\">");
                         out.println("</form>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?index=" + index + "&course=" + course + "&jump=" + jump + "\">");
                     }
                 } else if (caller.equals("lesson")) {
                     if (!group.equals("")) {                        
                         out.println("<form action=\"Proshop_lesson\" method=\"post\">");
                         out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                         out.println("<input type=\"submit\" value=\"Return\" style=\"background:#8B8970\">");
                         out.println("</form>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_lesson?proid=" +proid+ "&calDate=" +calDate+ "&jump=" +jump+ "\">");
                     } else {
                         out.println("<form action=\"Proshop_jump\" method=\"post\" target=\"_top\">");
                         out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                         out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                         out.println("<input type=\"hidden\" name=\"calDate\" value=\"" + calDate + "\">");
                         out.println("<input type=\"submit\" value=\"Return\" style=\"background:#8B8970\">");
                         out.println("</form>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Proshop_jump?lesson=yes&proid=" +proid+ "&calDate=" +calDate+ "&jump=" +jump+ "\">");
                     }
                 }
                 
             } else {
                 
                 if (caller.equals("email")) {
                     out.println("<td><button onclick=\"window.close()\" style=\"width:80px\">Close</button></td>");
                 } else if (caller.equals("main")) {
                     out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='Member_announce'\">Return</button><br>");
                     out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_announce\">"); // auto-refresh
                 } else if (caller.equals("teetime")) {
                     if (!sub.equals("")) {
                         out.println("<br><button align=\"center\" type=\"button\" style=\"width:80px\" onclick=\"location.href='/" + rev + "/Member_" + sub + ".htm'\">Return</button><br>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=/" +rev+ "/Member_" + sub + ".htm\">");
                     } else {
                         out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                         out.println("<input type=\"hidden\" name=\"index\" value=" + index + ">");
                         out.println("<input type=\"hidden\" name=\"course\" value=\"" + course + "\">");
                         out.println("<input type=\"hidden\" name=\"jump\" value=\"" + jump + "\">");
                         out.println("<br><input type=\"submit\" value=\"Return\">");
                         out.println("</form>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?index=" + index + "&course=" + course + "&jump=" + jump + "\">");
                     }
                 } else if (caller.equals("lesson")) {
                     if (!group.equals("")) {
                         if (!proid.equals("")) {
                             out.println("<form name=\"memLessonForm\" action=\"Member_lesson\" method=\"post\">");
                             out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                             out.println("<input type=\"hidden\" name=\"group\" value=\"yes\">");
                             out.println("</form>");
                             out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_lesson?proid=" + proid + "&group=yes\">");
                         } else {
                             out.println("<form name=\"memLessonForm\" action=\"Member_teelist\" method=\"get\">");
                             out.println("</form>");
                             out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_teelist\">");
                         }
                     } else {
                         out.println("<form action=\"Member_jump\" method=\"post\" target=\"_top\">");
                         out.println("<input type=\"hidden\" name=\"lesson\" value=\"yes\">");
                         out.println("<input type=\"hidden\" name=\"proid\" value=\"" + proid + "\">");
                         out.println("<input type=\"hidden\" name=\"index\" value=\"" + index + "\">");
                         out.println("<input type=\"submit\" value=\"Return\" style=\"background:#8B8970\">");
                         out.println("</form>");
                         out.println("<meta http-equiv=\"Refresh\" content=\"1; url=Member_jump?lesson=yes&proid=" + proid + "&index=" + index + "\">");
                     }
                 }
             }
             out.println("</center>");
         }         
     } catch (Exception exc) {
         out.println(SystemUtils.HeadTitle("DB Connection Error"));
         out.println("<BODY><CENTER>");
         out.println("<BR><BR><H3>Database Connection Error</H3>");
         out.println("<BR><BR>Unable to connect to the Database.");
         out.println("<BR>Please try again later.");
         out.println("<BR><BR>If problem persists, contact customer support.");
         out.println("<BR><BR>");
         out.println("<a href=\"Proshop_announce\">Home</a>");
         out.println("</CENTER></BODY></HTML>");
         return;
     }
     
     return;
 }      // end of processDiningForm
}
