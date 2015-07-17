/***************************************************************************************     
 *   Proshop_addgqta:  This servlet will process the 'add Guest Quota Restriction' request
 *                      from Proshop's addgqta page.
 *
 *
 *   called by:  Proshop_gqta
 *
 *   created: 3/09/2004   Bob P.
 *
 *   last updated:
 *
 *        5/19/10   Use Utilities.getCourseNames to get the course names so we can support an unlimited number of courses.
 *        9/02/08   Javascript compatability updates
 *        8/12/08   Update to limited access user restrictions
 *        7/24/08   Added limited access proshop users checks
 *        9/20/04   V5 - change getClub from SystemUtils to common.
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
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.Utilities;


public class Proshop_addgqta extends HttpServlet {
 
                                
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //****************************************************
 // Process the 'add' request from Proshop_gqta
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Statement stmt = null;
   ResultSet rs = null;
   //String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }
   
   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   //  Array to hold the course names
   //
   ArrayList<String> course = new ArrayList<String>();

   String courseName = "";        // course names
   int multi = 0;                 // multiple course support
   int hotel = 0;
   int index = 0;
   int i = 0;
   int i2 = 0;

   //
   //  parm block to hold the club parameters
   //
   parmClub parm = new parmClub(0, con); // no guest quotas in FlxRez yet


   try {
      //
      //  get the club parameters (multi, guest types, hotel)
      //
      getClub.getParms(con, parm);

      multi = parm.multi;
      hotel = parm.hotel;

      if (multi != 0) {           // if multiple courses supported for this club

         course = Utilities.getCourseNames(con);     // get all the course names
      }

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H1>Database Access Error</H1>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR><br>Exception: " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Output HTML page to prompt for parms
   //
   out.println(SystemUtils.HeadTitle2("Proshop - Add Guest Quota Main"));
      out.println("<script type=\"text/javascript\">");
      out.println("<!--");
      out.println("function cursor() { document.forms['f'].restrict_name.focus(); }");
      out.println("// -->");
      out.println("</script>");
   out.println("</head>");

   out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\" onload=cursor()>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<font face=\"Arial, Helvetica, Sans-serif\">");
   out.println("<center>");

   out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#336633\">");
   out.println("<tr><td align=\"center\">");
   out.println("<font color=\"#FFFFFF\" size=\"2\">");
   out.println("<b>Guest Quota Restrictions</b><br>");
   out.println("Complete the following information for each Restriction to be added.<br>");
   out.println("Click on 'Add' to add the Restriction.");
   out.println("</font></td></tr></table><br>");

   out.println("<table border=\"1\" cols=\"1\" bgcolor=\"#F5F5DC\">");
      out.println("<form action=\"/" +rev+ "/servlet/Proshop_addgqta\" method=\"post\" target=\"bot\" name=\"f\">");
      out.println("<tr><td width=\"500\">");
         out.println("<font size=\"2\">");
         out.println("<p align=\"left\">&nbsp;&nbsp;Restriction Name:&nbsp;&nbsp;");
         out.println("<input type=\"text\" name=\"restrict_name\" size=\"30\" maxlength=\"30\">");
         out.println("</input>&nbsp;&nbsp;&nbsp;* Must be unique");
         out.println("</p>");

         if (multi != 0) {

            out.println("<p align=\"left\">&nbsp;&nbsp;Select a Course:&nbsp;&nbsp;");
            out.println("<select size=\"1\" name=\"course\">");
            out.println("<option selected value=\"-ALL-\">ALL</option>");

            for (index=0; index<course.size(); index++) {

               courseName = course.get(index);                    // get course name from array
               out.println("<option value=\"" + courseName + "\">" + courseName + "</option>");
            }
            out.println("</select>");
            out.println("</p>");

         } else {

            out.println("<input type=\"hidden\" name=\"course\" value=\"\">");
         }
         out.println("<p align=\"left\">&nbsp;&nbsp;Tees:&nbsp;&nbsp;");
           out.println("<select size=\"1\" name=\"fb\">");
             out.println("<option value=\"Both\">Both</option>");
             out.println("<option value=\"Front\">Front</option>");
             out.println("<option value=\"Back\">Back</option>");
           out.println("</select></p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;Start Date:&nbsp;&nbsp;");
         out.println("Month:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"month\">");
         out.println("<option value=\"01\">JAN</option>");
         out.println("<option value=\"02\">FEB</option>");
         out.println("<option value=\"03\">MAR</option>");
         out.println("<option value=\"04\">APR</option>");
         out.println("<option value=\"05\">MAY</option>");
         out.println("<option value=\"06\">JUN</option>");
         out.println("<option value=\"07\">JUL</option>");
         out.println("<option value=\"08\">AUG</option>");
         out.println("<option value=\"09\">SEP</option>");
         out.println("<option value=\"10\">OCT</option>");
         out.println("<option value=\"11\">NOV</option>");
         out.println("<option value=\"12\">DEC</option>");
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"day\">");
         out.println("<option value=\"01\">01</option>");
         out.println("<option value=\"02\">02</option>");
         out.println("<option value=\"03\">03</option>");
         out.println("<option value=\"04\">04</option>");
         out.println("<option value=\"05\">05</option>");
         out.println("<option value=\"06\">06</option>");
         out.println("<option value=\"07\">07</option>");
         out.println("<option value=\"08\">08</option>");
         out.println("<option value=\"09\">09</option>");
         out.println("<option value=\"10\">10</option>");
         out.println("<option value=\"11\">11</option>");
         out.println("<option value=\"12\">12</option>");
         out.println("<option value=\"13\">13</option>");
         out.println("<option value=\"14\">14</option>");
         out.println("<option value=\"15\">15</option>");
         out.println("<option value=\"16\">16</option>");
         out.println("<option value=\"17\">17</option>");
         out.println("<option value=\"18\">18</option>");
         out.println("<option value=\"19\">19</option>");
         out.println("<option value=\"20\">20</option>");
         out.println("<option value=\"21\">21</option>");
         out.println("<option value=\"22\">22</option>");
         out.println("<option value=\"23\">23</option>");
         out.println("<option value=\"24\">24</option>");
         out.println("<option value=\"25\">25</option>");
         out.println("<option value=\"26\">26</option>");
         out.println("<option value=\"27\">27</option>");
         out.println("<option value=\"28\">28</option>");
         out.println("<option value=\"29\">29</option>");
         out.println("<option value=\"30\">30</option>");
         out.println("<option value=\"31\">31</option>");
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"year\">");
         out.println("<option value=\"2004\">2004</option>");
         out.println("<option value=\"2005\">2005</option>");
         out.println("<option value=\"2006\">2006</option>");
         out.println("<option value=\"2007\">2007</option>");
         out.println("<option value=\"2008\">2008</option>");
         out.println("<option value=\"2009\">2009</option>");
         out.println("<option value=\"2010\">2010</option>");
         out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Date:&nbsp;&nbsp;&nbsp;");
         out.println("Month:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"emonth\">");
         out.println("<option value=\"01\">JAN</option>");
         out.println("<option value=\"02\">FEB</option>");
         out.println("<option value=\"03\">MAR</option>");
         out.println("<option value=\"04\">APR</option>");
         out.println("<option value=\"05\">MAY</option>");
         out.println("<option value=\"06\">JUN</option>");
         out.println("<option value=\"07\">JUL</option>");
         out.println("<option value=\"08\">AUG</option>");
         out.println("<option value=\"09\">SEP</option>");
         out.println("<option value=\"10\">OCT</option>");
         out.println("<option value=\"11\">NOV</option>");
         out.println("<option value=\"12\">DEC</option>");
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;Day:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"eday\">");
         out.println("<option value=\"01\">01</option>");
         out.println("<option value=\"02\">02</option>");
         out.println("<option value=\"03\">03</option>");
         out.println("<option value=\"04\">04</option>");
         out.println("<option value=\"05\">05</option>");
         out.println("<option value=\"06\">06</option>");
         out.println("<option value=\"07\">07</option>");
         out.println("<option value=\"08\">08</option>");
         out.println("<option value=\"09\">09</option>");
         out.println("<option value=\"10\">10</option>");
         out.println("<option value=\"11\">11</option>");
         out.println("<option value=\"12\">12</option>");
         out.println("<option value=\"13\">13</option>");
         out.println("<option value=\"14\">14</option>");
         out.println("<option value=\"15\">15</option>");
         out.println("<option value=\"16\">16</option>");
         out.println("<option value=\"17\">17</option>");
         out.println("<option value=\"18\">18</option>");
         out.println("<option value=\"19\">19</option>");
         out.println("<option value=\"20\">20</option>");
         out.println("<option value=\"21\">21</option>");
         out.println("<option value=\"22\">22</option>");
         out.println("<option value=\"23\">23</option>");
         out.println("<option value=\"24\">24</option>");
         out.println("<option value=\"25\">25</option>");
         out.println("<option value=\"26\">26</option>");
         out.println("<option value=\"27\">27</option>");
         out.println("<option value=\"28\">28</option>");
         out.println("<option value=\"29\">29</option>");
         out.println("<option value=\"30\">30</option>");
         out.println("<option value=\"31\">31</option>");
         out.println("</select>");
         out.println("&nbsp;&nbsp;&nbsp;Year:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"eyear\">");
         out.println("<option value=\"2004\">2004</option>");
         out.println("<option value=\"2005\">2005</option>");
         out.println("<option value=\"2006\">2006</option>");
         out.println("<option value=\"2007\">2007</option>");
         out.println("<option value=\"2008\">2008</option>");
         out.println("<option value=\"2009\">2009</option>");
         out.println("<option value=\"2010\">2010</option>");
         out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;Start Time: &nbsp;&nbsp; hr &nbsp;");
         out.println("<select size=\"1\" name=\"start_hr\">");
         out.println("<option value=\"01\">01</option>");
         out.println("<option value=\"02\">02</option>");
         out.println("<option value=\"03\">03</option>");
         out.println("<option value=\"04\">04</option>");
         out.println("<option value=\"05\">05</option>");
         out.println("<option value=\"06\">06</option>");
         out.println("<option value=\"07\">07</option>");
         out.println("<option value=\"08\">08</option>");
         out.println("<option value=\"09\">09</option>");
         out.println("<option value=\"10\">10</option>");
         out.println("<option value=\"11\">11</option>");
         out.println("<option value=\"12\">12</option>");
         out.println("</select>");
         out.println("&nbsp; min &nbsp;");
         out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"start_min\">");
         out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"start_ampm\">");
         out.println("<option value=\"00\">AM</option>");
         out.println("<option value=\"12\">PM</option>");
         out.println("</select>");
         out.println("</p>");
         out.println("<p align=\"left\">&nbsp;&nbsp;End Time: &nbsp;&nbsp;&nbsp;&nbsp; hr &nbsp;");
         out.println("<select size=\"1\" name=\"end_hr\">");
         out.println("<option value=\"01\">01</option>");
         out.println("<option value=\"02\">02</option>");
         out.println("<option value=\"03\">03</option>");
         out.println("<option value=\"04\">04</option>");
         out.println("<option value=\"05\">05</option>");
         out.println("<option value=\"06\">06</option>");
         out.println("<option value=\"07\">07</option>");
         out.println("<option value=\"08\">08</option>");
         out.println("<option value=\"09\">09</option>");
         out.println("<option value=\"10\">10</option>");
         out.println("<option value=\"11\">11</option>");
         out.println("<option value=\"12\">12</option>");
         out.println("</select>");
         out.println("&nbsp; min &nbsp;");
         out.println("<input type=\"text\" size=\"2\" maxlength=\"2\" value=\"00\" name=\"end_min\">");
         out.println("&nbsp;(enter 00 - 59)&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"end_ampm\">");
         out.println("<option value=\"00\">AM</option>");
         out.println("<option value=\"12\">PM</option>");
         out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"left\">&nbsp;&nbsp;Guest Types to be Restricted (select all that apply):");
         i2 = 1;
         for (i = 0; i < parm.MAX_Guests; i++) {
            if (!parm.guest[i].equals( "" )) {
               out.println("<br>");
               out.println("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
               out.println("<input type=\"checkbox\" name=\"guest" +i2+ "\" value=\"" + parm.guest[i] + "\">&nbsp;&nbsp;" + parm.guest[i]);
               i2++;
            }
         }
         out.println("</p>");
           
         out.println("<p align=\"Left\">&nbsp;&nbsp;Max Number of Guests:&nbsp;&nbsp;");
         out.println("<input type=\"text\" size=\"3\" maxlength=\"3\" value=\"0\" name=\"guests\">");
         out.println("&nbsp;&nbsp;Per:&nbsp;&nbsp;");
         out.println("<select size=\"1\" name=\"per\">");
         out.println("<option value=\"Member\">Member</option>");
         out.println("<option value=\"Membership Number\">Membership Number</option>");
         out.println("</select>");
         out.println("</p>");

         out.println("<p align=\"center\">");
           out.println("<input type=\"submit\" value=\"Add\">");
           out.println("</input>");
         out.println("</p>");
         out.println("</font>");
      out.println("</td></tr>");
      out.println("</form>");
   out.println("</table>");
   out.println("</form>");
  out.println("</font>");
  out.println("<font size=\"2\">");
  out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_gqta\">");
  out.println("<input type=\"submit\" value=\"Done\" style=\"text-decoration:underline; background:#8B8970\">");
  out.println("</input></form></font>");
  out.println("</center>");
  out.println("</body>");
  out.println("</html>");

 } // end of doGet processing


 //****************************************************
 // Process the form request from proshop_addgqta page
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   ResultSet rs = null;
   String omit = "";

   HttpSession session = SystemUtils.verifyPro(req, out);       // check for intruder

   if (session == null) {

      return;
   }

   Connection con = SystemUtils.getCon(session);            // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      out.println("<a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // Check Feature Access Rights for current proshop user
   if (!SystemUtils.verifyProAccess(req, "SYSCONFIG_RESTRICTIONS", con, out)) {
       SystemUtils.restrictProshop("SYSCONFIG_RESTRICTIONS", out);
   }

   String templott = (String)session.getAttribute("lottery");        // get lottery support indicator
   int lottery = Integer.parseInt(templott);

   //
   // Get all the parameters entered
   //
   String name = req.getParameter("restrict_name");        //  restriction name
   String s_month = req.getParameter("month");             //  month (00 - 12)
   String s_day = req.getParameter("day");                 //  day (01 - 31)
   String s_year = req.getParameter("year");               //  year (2004 - 20xx)
   String start_hr = req.getParameter("start_hr");         //  start hour (01 - 12)
   String start_min = req.getParameter("start_min");       //  start min (00 - 59)
   String start_ampm = req.getParameter("start_ampm");     //  AM/PM (00 or 12)
   String e_month = req.getParameter("emonth");             
   String e_day = req.getParameter("eday");                 
   String e_year = req.getParameter("eyear");               
   String end_hr = req.getParameter("end_hr");                
   String end_min = req.getParameter("end_min");              
   String end_ampm = req.getParameter("end_ampm");            
   String sguests = req.getParameter("guests");               //  number of guests per member
   String courseName = req.getParameter("course");
   String fb = req.getParameter("fb");
   String per = req.getParameter("per");
     
   String recurr = "";           // NOT USED
     
   String guest1 = "";           // Guest Types
   String guest2 = "";
   String guest3 = "";
   String guest4 = "";
   String guest5 = "";
   String guest6 = "";
   String guest7 = "";
   String guest8 = "";
   String guest9 = "";
   String guest10 = "";
   String guest11 = "";
   String guest12 = "";
   String guest13 = "";
   String guest14 = "";
   String guest15 = "";
   String guest16 = "";
   String guest17 = "";
   String guest18 = "";
   String guest19 = "";
   String guest20 = "";
   String guest21 = "";
   String guest22 = "";
   String guest23 = "";
   String guest24 = "";
   String guest25 = "";
   String guest26 = "";
   String guest27 = "";
   String guest28 = "";
   String guest29 = "";
   String guest30 = "";
   String guest31 = "";
   String guest32 = "";
   String guest33 = "";
   String guest34 = "";
   String guest35 = "";
   String guest36 = "";
   String color = "F5F5DC";       // default

   int smonth = 0;                // init integer date/time values
   int sday = 0;
   int syear = 0;
   int emonth = 0;
   int eday = 0;
   int eyear = 0;
   int s_hr = 0;
   int s_min = 0;
   int s_ampm = 0;
   int e_hr = 0;
   int e_min = 0;
   int e_ampm = 0;
   
   int guests = 0;


   //
   //  indicate parm specified for those that were
   //
   if (req.getParameter("guest1") != null) {
      guest1 = req.getParameter("guest1");
   }
   if (req.getParameter("guest2") != null) {
      guest2 = req.getParameter("guest2");
   }
   if (req.getParameter("guest3") != null) {
      guest3 = req.getParameter("guest3");
   }
   if (req.getParameter("guest4") != null) {
      guest4 = req.getParameter("guest4");
   }
   if (req.getParameter("guest5") != null) {
      guest5 = req.getParameter("guest5");
   }
   if (req.getParameter("guest6") != null) {
      guest6 = req.getParameter("guest6");
   }
   if (req.getParameter("guest7") != null) {
      guest7 = req.getParameter("guest7");
   }
   if (req.getParameter("guest8") != null) {
      guest8 = req.getParameter("guest8");
   }
   if (req.getParameter("guest9") != null) {
      guest9 = req.getParameter("guest9");
   }
   if (req.getParameter("guest10") != null) {
      guest10 = req.getParameter("guest10");
   }
   if (req.getParameter("guest11") != null) {
      guest11 = req.getParameter("guest11");
   }
   if (req.getParameter("guest12") != null) {
      guest12 = req.getParameter("guest12");
   }
   if (req.getParameter("guest13") != null) {
      guest13 = req.getParameter("guest13");
   }
   if (req.getParameter("guest14") != null) {
      guest14 = req.getParameter("guest14");
   }
   if (req.getParameter("guest15") != null) {
      guest15 = req.getParameter("guest15");
   }
   if (req.getParameter("guest16") != null) {
      guest16 = req.getParameter("guest16");
   }
   if (req.getParameter("guest17") != null) {
      guest17 = req.getParameter("guest17");
   }
   if (req.getParameter("guest18") != null) {
      guest18 = req.getParameter("guest18");
   }
   if (req.getParameter("guest19") != null) {
      guest19 = req.getParameter("guest19");
   }
   if (req.getParameter("guest20") != null) {
      guest20 = req.getParameter("guest20");
   }
   if (req.getParameter("guest21") != null) {
      guest21 = req.getParameter("guest21");
   }
   if (req.getParameter("guest22") != null) {
      guest22 = req.getParameter("guest22");
   }
   if (req.getParameter("guest23") != null) {
      guest23 = req.getParameter("guest23");
   }
   if (req.getParameter("guest24") != null) {
      guest24 = req.getParameter("guest24");
   }
   if (req.getParameter("guest25") != null) {
      guest25 = req.getParameter("guest25");
   }
   if (req.getParameter("guest26") != null) {
      guest26 = req.getParameter("guest26");
   }
   if (req.getParameter("guest27") != null) {
      guest27 = req.getParameter("guest27");
   }
   if (req.getParameter("guest28") != null) {
      guest28 = req.getParameter("guest28");
   }
   if (req.getParameter("guest29") != null) {
      guest29 = req.getParameter("guest29");
   }
   if (req.getParameter("guest30") != null) {
      guest30 = req.getParameter("guest30");
   }
   if (req.getParameter("guest31") != null) {
      guest31 = req.getParameter("guest31");
   }
   if (req.getParameter("guest32") != null) {
      guest32 = req.getParameter("guest32");
   }
   if (req.getParameter("guest33") != null) {
      guest33 = req.getParameter("guest33");
   }
   if (req.getParameter("guest34") != null) {
      guest34 = req.getParameter("guest34");
   }
   if (req.getParameter("guest35") != null) {
      guest35 = req.getParameter("guest35");
   }
   if (req.getParameter("guest36") != null) {
      guest36 = req.getParameter("guest36");
   }
     
   //
   // Convert the numeric string parameters to Int's
   //
   try {
      smonth = Integer.parseInt(s_month);
      sday = Integer.parseInt(s_day);
      syear = Integer.parseInt(s_year);
      s_hr = Integer.parseInt(start_hr);
      s_min = Integer.parseInt(start_min);
      s_ampm = Integer.parseInt(start_ampm);
      emonth = Integer.parseInt(e_month);
      eday = Integer.parseInt(e_day);
      eyear = Integer.parseInt(e_year);
      e_hr = Integer.parseInt(end_hr);
      e_min = Integer.parseInt(end_min);
      e_ampm = Integer.parseInt(end_ampm);
      guests = Integer.parseInt(sguests);
   }
   catch (NumberFormatException e) {
      // ignore error - let verify catch it
   }

   //
   //  verify the required fields (name reqr'ed & at least one guest type - rest are automatic)
   //
   if ((name.equals( omit )) ||
       ((guest1.equals( "" )) && (guest2.equals( "" )) && (guest3.equals( "" )) && (guest4.equals( "" )) &&
        (guest5.equals( "" )) && (guest6.equals( "" )) && (guest7.equals( "" )) && (guest8.equals( "" )) &&
        (guest9.equals( "" )) && (guest10.equals( "" )) && (guest11.equals( "" )) && (guest12.equals( "" )) &&
        (guest13.equals( "" )) && (guest14.equals( "" )) && (guest15.equals( "" )) && (guest16.equals( "" )) &&
        (guest17.equals( "" )) && (guest18.equals( "" )) && (guest19.equals( "" )) && (guest20.equals( "" )) &&
        (guest21.equals( "" )) && (guest22.equals( "" )) && (guest23.equals( "" )) && (guest24.equals( "" )) &&
        (guest25.equals( "" )) && (guest26.equals( "" )) && (guest27.equals( "" )) && (guest28.equals( "" )) &&
        (guest29.equals( "" )) && (guest30.equals( "" )) && (guest31.equals( "" )) && (guest32.equals( "" )) &&
        (guest33.equals( "" )) && (guest34.equals( "" )) && (guest35.equals( "" )) && (guest36.equals( "" )))) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  Check for quotes in name
   //
   boolean error = SystemUtils.scanQuote(name);           // check for single quote

   if (error == true) {

      out.println(SystemUtils.HeadTitle("Data Entry Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Invalid Parameter Value Specified</H3>");
      out.println("<BR><BR>Apostrophes (single quotes) cannot be part of the Name.");
      out.println("<BR>Please remove the apostrophe and try again.");
      out.println("<br><br><font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   //
   //  Scan name for special characters and replace with HTML supported chars (i.e. '>' = &gt)
   //
   name = SystemUtils.filter(name);

   //
   //  Verify length of name (special chars can force error)
   //
   if (name.length() > SystemUtils.GQTAL) {

      out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
      out.println("<BODY><CENTER><BR>");
      out.println("<br><br>");
      out.println("<H3>Input Error</H3>");
      out.println("<BR>Sorry, name field is too long.<BR>");
      out.println("<BR>Special characters (i.e. #, *, &, etc.) generate additional characters which");
      out.println("<BR>can cause the maximum length to be exceeded.<BR>");
      out.println("<BR>Please reduce the length of the name field and try again.<BR>");
      out.println("<BR><BR>");
      out.println("<font size=\"2\">");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
      out.println("</input></form></font>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;
   }

   //
   //  Check if restriction already exists in database
   //
   try {

      PreparedStatement stmt = con.prepareStatement (
              "SELECT sdate FROM guestqta4 WHERE name = ? AND courseName = ?");

      stmt.clearParameters();        // clear the parms
      stmt.setString(1, name);       // put the parm in stmt
      stmt.setString(2, courseName);
      rs = stmt.executeQuery();      // execute the prepared stmt
        
      if (rs.next()) {

         dupMem(out);    // Guest Quota Restriction exists - inform the user and return
         stmt.close();
         return;
      }
      stmt.close();
   }
   catch (Exception ignored) {
   }

   //
   //  adjust some values for the table
   //
   long sdate = syear * 10000;       // create a date field from input values
   sdate = sdate + (smonth * 100);
   sdate = sdate + sday;             // date = yyyymmdd (for comparisons)

   long edate = eyear * 10000;       // ditto
   edate = edate + (emonth * 100);
   edate = edate + eday;             

   if (s_hr != 12) {                // _hr specified as 01 - 12 (_ampm = 00 or 12)
                              
      s_hr = s_hr + s_ampm;         // convert to military time (12 is always Noon, or PM)
   }

   if (e_hr != 12) {                // ditto

      e_hr = e_hr + e_ampm;       
   }

   int stime = s_hr * 100;
   stime = stime + s_min;
   int etime = e_hr * 100;
   etime = etime + e_min;

   //
   //  verify the date and time fields
   //
   if ((sdate > edate) || (stime > etime)) {

      invData(out);    // inform the user and return
      return;
   }

   //
   //  add restriction data to the database
   //
   try {

      PreparedStatement pstmt = con.prepareStatement (
        "INSERT INTO guestqta4 (name, sdate, start_mm, start_dd, start_yy, start_hr, start_min, " +
        "stime, edate, end_mm, end_dd, end_yy, end_hr, end_min, etime, recurr, num_guests, " +
        "guest1, guest2, guest3, guest4, guest5, guest6, guest7, guest8, courseName, fb, color, " +
        "guest9, guest10, guest11, guest12, guest13, guest14, guest15, guest16, " +
        "guest17, guest18, guest19, guest20, guest21, guest22, guest23, guest24, " +
        "guest25, guest26, guest27, guest28, guest29, guest30, guest31, guest32, " +
        "guest33, guest34, guest35, guest36, per) " +
        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, " +
        "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

      pstmt.clearParameters();        // clear the parms
      pstmt.setString(1, name);       // put the parm in pstmt
      pstmt.setLong(2, sdate);
      pstmt.setInt(3, smonth);
      pstmt.setInt(4, sday);
      pstmt.setInt(5, syear);
      pstmt.setInt(6, s_hr);
      pstmt.setInt(7, s_min);
      pstmt.setInt(8, stime);
      pstmt.setLong(9, edate);
      pstmt.setInt(10, emonth);
      pstmt.setInt(11, eday);
      pstmt.setInt(12, eyear);
      pstmt.setInt(13, e_hr);
      pstmt.setInt(14, e_min);
      pstmt.setInt(15, etime);
      pstmt.setString(16, recurr);
      pstmt.setInt(17, guests);
      pstmt.setString(18, guest1);
      pstmt.setString(19, guest2);
      pstmt.setString(20, guest3);
      pstmt.setString(21, guest4);
      pstmt.setString(22, guest5);
      pstmt.setString(23, guest6);
      pstmt.setString(24, guest7);
      pstmt.setString(25, guest8);
      pstmt.setString(26, courseName);
      pstmt.setString(27, fb);
      pstmt.setString(28, color);
      pstmt.setString(29, guest9);
      pstmt.setString(30, guest10);
      pstmt.setString(31, guest11);
      pstmt.setString(32, guest12);
      pstmt.setString(33, guest13);
      pstmt.setString(34, guest14);
      pstmt.setString(35, guest15);
      pstmt.setString(36, guest16);
      pstmt.setString(37, guest17);
      pstmt.setString(38, guest18);
      pstmt.setString(39, guest19);
      pstmt.setString(40, guest20);
      pstmt.setString(41, guest21);
      pstmt.setString(42, guest22);
      pstmt.setString(43, guest23);
      pstmt.setString(44, guest24);
      pstmt.setString(45, guest25);
      pstmt.setString(46, guest26);
      pstmt.setString(47, guest27);
      pstmt.setString(48, guest28);
      pstmt.setString(49, guest29);
      pstmt.setString(50, guest30);
      pstmt.setString(51, guest31);
      pstmt.setString(52, guest32);
      pstmt.setString(53, guest33);
      pstmt.setString(54, guest34);
      pstmt.setString(55, guest35);
      pstmt.setString(56, guest36);
      pstmt.setString(57, per);

      pstmt.executeUpdate();          // execute the prepared stmt

      pstmt.close();   // close the stmt

   }
   catch (Exception exc) {

      out.println(SystemUtils.HeadTitle("Database Error"));
      out.println("<BODY><CENTER><BR>");
      out.println("<BR><BR><H3>Database Access Error</H3>");
      out.println("<BR><BR>Sorry, we are unable to access the database at this time.");
      out.println("<BR>Please try again later.");
      out.println("<BR>Exception:   " + exc.getMessage());
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR><a href=\"/" +rev+ "/servlet/Proshop_announce\">Home</a>");
      out.println("</CENTER></BODY></HTML>");
      out.close();
      return;

   }

   //
   // Database updated - inform user
   //
   out.println(SystemUtils.HeadTitle("Proshop Add Restriction"));
   out.println("<BODY>");
   SystemUtils.getProshopSubMenu(req, out, lottery);        // required to allow submenus on this page
   out.println("<CENTER><BR>");
   out.println("<BR><BR><H3>Guest Quota Restriction Has Been Added</H3>");
   out.println("<BR><BR>Thank you, the restriction has been added to the system database.");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"/" +rev+ "/servlet/Proshop_addgqta\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }   // end of doPost   


 // *********************************************************
 // Missing or invalid data entered...
 // *********************************************************

 private void invData(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, some data you entered is missing or invalid.<BR>");
   out.println("<BR>Please try again.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

 // *********************************************************
 // Guest Quota Restriction already exists
 // *********************************************************

 private void dupMem(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Input Error - Redirect"));
   out.println("<BODY><CENTER><BR>");
   out.println("<p>&nbsp;</p>");
   out.println("<BR><H3>Input Error</H3><BR>");
   out.println("<BR><BR>Sorry, the <b>restriction name</b> you specified already exists in the database.<BR>");
   out.println("<BR>Please use the edit feature to change an existing restriction record.<BR>");
   out.println("<BR><BR>");
   out.println("<font size=\"2\">");
   out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
   out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background:#8B8970\">");
   out.println("</input></form></font>");
   out.println("</CENTER></BODY></HTML>");
   out.close();
 }

}
