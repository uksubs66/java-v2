/***************************************************************************************     
 *   Dining_home:  This servlet contains the majority of the methods required for the dining system.
 *
 *
 *   called by:  
 *
 *   created: 05/03/11  
 *
 *
 *   last updated:
 *
 *      1/16/14  tontoverde - hide signup period and display 'please call the club' text instead
 *      8/16/13  Prevent access to existing event reservations once the signup period has passed
 *      4/22/13  Increase the size of the event info popup iframe (tu_)
 *      1/30/13  Excluded dining events that have been cancelled
 *      1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *     11/01/12  Changed event links for Register Now and Cancel / Edit Signup so they are CSS and can be customized for clubs
 *               Tonto Verde now defaults to the new By Reservation signup listing
 *     10/30/12  Add group by reservation option to the event signup listing
 *     10/12/12  Do not display the breadcrumb on the announcement page (home page).
 *      9/18/12  Move the dining logo into the white portion of the page so we don't have to change colors when the background changes.
 *      9/06/12  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *      9/04/12  Event Listing page will now popup the event if an id is passed in (used by our seamless interface)
 *      7/13/12  Added aditional information to the event informational pages
 *      5/14/12  Minor changes to the Event Info page to change the description layout.
 *      5/08/12  Change the message displayed when members cannot register for an event.
 *     11/06/11  Change paths for announcement pages - now using NFS and discrete club folders
 *      9/06/11  Only show the sign-up lists for events if the dining system is configured to allow it (organizations.members_can_see_other_members_reservations)
 *      9/02/11  Allow for external login from Login.processExtLogin to allow member to register for an event (link in email).
 *      8/26/11  Minor changes to the info display in viewEventInfo so the registration link is more visible.
 *      8/25/11  Add orig=calendar parm in viewCalendar when calling Dining_slot for a new reservation or to edit one
 *               so the Go Back links will return to the calendar.
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
import com.foretees.common.parmEvent;
import com.foretees.common.parmDining;
import com.foretees.common.parmDiningCosts;
import com.foretees.common.parmDiningSeatings;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.nameLists;


public class Dining_home extends HttpServlet {
                         
                                 
 static String rev = ProcessConstants.REV;                               // Software Revision Level (Version)
 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System  

 //
 //**********************************************************
 //
 // Process the dining request form display
 //
 //**********************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     doPost(req, resp);

 }


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   HttpSession session = null;

   boolean ext_login = false;            // not external login (from email link)
   
   if (req.getParameter("ext-dReq") != null || req.getParameter("ext_login") != null) {   // if from Login for an external login user, or returned from Dining_slot

       session = req.getSession(false);
       
       // if the user sits too long on the exernal welcome page their special session may of expired
       if (session == null || (String)session.getAttribute("ext-user") == null) {

            out.println("<HTML>");
            out.println("<HEAD>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
            out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
            out.println("<meta http-equiv=\"Content-Style-Type\" content=\"text/css\">");
            out.println("<TITLE>Access Error</TITLE></HEAD>");
            out.println("<BODY><CENTER>");
            out.println("<H2>Access Error - Please Read</H2>");
            out.println("Sorry, your session either timed out, you didn't login, or your computer does not allow the use of Cookies.");
            out.println("<BR><BR>This site requires the use of Cookies for security purposes.");
            out.println("<BR><HR width=\"500\"><BR>");
            out.println("If you feel that you have received this message in error,");
            out.println("<BR>please email us at <a href=\"mailto:support@foretees.com\">support@foretees.com</a>.");
            out.println("<BR><b>Provide your name or member number, the name of your club and a detailed description of your problem.</b>");
            out.println("<BR>Thank you.");
            out.println("<BR><BR><a href=\"Logout\">Exit</a><BR><BR>");
            out.println("<CENTER>Server: " + Common_Server.SERVER_ID + "</CENTER>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
       }
       
       ext_login = true;        // member came from link in email message  (via Login.ProcessExtLogin)

   } else {

      session = SystemUtils.verifyMem(req, out);             // check for intruder 
   }

   if (session == null) return;

   Connection con = SystemUtils.getCon(session);                      // get DB connection

   if (con == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      if (ext_login == false) {
         out.println("<a href=\"Member_announce\">Home</a>");
      } else {
         out.println("<a href=\"Logout\">Exit</a>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   // make sure we are in dining mode now
   session.setAttribute("activity_id", ProcessConstants.DINING_ACTIVITY_ID);  // activity indicator

   // get connection to dining db
   Connection con_d = Connect.getDiningCon();

   if (con_d == null) {

      out.println(SystemUtils.HeadTitle("DB Connection Error"));
      out.println("<BODY><CENTER>");
      out.println("<BR><BR><H3>Database Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the Dining Database.");
      out.println("<BR>Please try again later.");
      out.println("<BR><BR>If problem persists, contact customer support.");
      out.println("<BR><BR>");
      if (ext_login == false) {
         out.println("<a href=\"Member_announce\">Home</a>");
      } else {
         out.println("<a href=\"Logout\">Exit</a>");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }

   String user = "";
   
   if (ext_login == false) {
      user = (String)session.getAttribute("user");              // get username
   } else {
      user = (String)session.getAttribute("ext-user");              // get username when external (done so verifymem will fail)
   }
   String club = (String)session.getAttribute("club");              // get club name
   int activity_id = (Integer)session.getAttribute("activity_id");   //   and activity id for user
   int organization_id = Utilities.getOrganizationId(con);
/*
   int reservation_id = 0;
   int event_id = 0;
   
   try { reservation_id = Integer.parseInt(req.getParameter("reservation_id")); }
   catch (Exception ignore) { }

   try { event_id = Integer.parseInt(req.getParameter("event_id")); }
   catch (Exception ignore) { }
*/
   activity_id = ProcessConstants.DINING_ACTIVITY_ID;
   
   String title = "ForeTees Dining Home";
   
   String clubName = "ForeTees Country Club";     // in case of an error below
   
   try {
      
      clubName = Utilities.getClubName(con);        // get the full name of this club

   } catch (Exception exc) {
   }

   //Common_skin.outputHeader(club, activity_id, title, out);

   // unless we pass nowrap flag
   if (req.getParameter("nowrap") == null) {

       //
       //   Build the Dining Home page
       //
       Common_skin.outputHeader(club, activity_id, title, true, out, req);

       Common_skin.outputBody(club, activity_id, out, req);

       if (ext_login == false) Common_skin.outputTopNav(req, club, activity_id, out, con);

       Common_skin.outputBanner(club, activity_id, clubName, "", out, req);    // no zip code for Dining

       if (ext_login == false) {
           
           Common_skin.outputSubNav(club, activity_id, out, con, req);
           
       } else {
           
           out.println("<p>&nbsp;</p><p>&nbsp;</p>");   // blank line to make room for the dining logo
       }

       Common_skin.outputPageStart(club, activity_id, out, req);

   }

   //
   //  Build the body for this request
   //
   if (req.getParameter("view_events") != null) {

       // view an event and see who is signed up
       viewEventListing(organization_id, club, user, req, out, con, con_d);

   } else if (req.getParameter("event_popup") != null) {

       //viewEventInfo(club, user, req, out, con, con_d);
       Common_skin.outputHeader(club, activity_id, title, true, out, req);
       viewEventInfoPopup(club, user, req, out, con, con_d);

   } else if (req.getParameter("event") != null) {

       viewEventInfoPage(club, user, req, out, con, con_d, ext_login);

   } else if (req.getParameter("view_reservations") != null) {

       // view the ala carte reservations for a day
       Common_skin.outputHeader(club, activity_id, title, true, out, req);
       viewDailyReservations(organization_id, club, user, req, out, con, con_d);

   } else if (req.getParameter("calendar") != null) {

       viewCalendar(organization_id, club, user, out, con, con_d);  // NOW USES Member_teelist ******** 

   } else if (req.getParameter("partners") != null) {

       viewPartnersPage(club, user, req, resp, out, con);

   } else if (req.getParameter("search") != null) {

       if (req.getParameter("search_name") != null) {

           viewSearchResults(club, user, req, out, con_d);

       } else {

           viewSearchPage(club, user, req, out, con);

       }

   } else if (req.getParameter("settings") != null) {

       viewSettingsPage(club, user, req, resp, out, con, con_d);

   } else {

       // default
       announcementPage(club, req, out);

   }


   //  end the page
   if (req.getParameter("nowrap") == null) {

       Common_skin.outputPageEnd(club, activity_id, out, req);
   }

   out.close();

   try { con_d.close(); }
   catch (Exception ignore) {}
   
 }


 private void viewPartnersPage(String club, String user, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con) {


    //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Partners <a href=\"#\" onclick=\"goBack()\">&lt;&lt; Go Back</a></div>");

    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Manage Partners",req);
       
    Common_skin.outputLogo(club, dining_activity_id, out, req);

    out.println("<div id=\"tt1_left\">");
    //out.println("<p><strong>Manage Partners</strong></p></div>");

    try {

        Member_partner partners = new Member_partner();

        partners.doPost2(req, resp, true);

    } catch (Exception exc) {

        out.println("ERROR: " + exc.toString());

    }

 }


 private void viewSearchPage(String club, String user, HttpServletRequest req, PrintWriter out, Connection con) {


    String fullname = Utilities.getFullNameFromUsername(user, con);   // get user's full name
    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);


    out.println("<div class=\"reservations\">");

   // out.println("<div id=\"breadcrumb\">Home / Search Dining Reservations <a href=\"#\" onclick=\"window.location.href='Dining_home'\">&lt;&lt; Go Back</a></div>");

    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Search", req);
       
    Common_skin.outputLogo(club, dining_activity_id, out, req);


    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    //out.println("<div id=\"reservation_action_title\">Search</div>");

    //
    // output the instructions
    //
    out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>: Use the Partner List or Member List to select the member you wish to search for. Click on 'Search Your Reservations' to find your current reservations.</div>");

    out.println("<br><br><form action=\"Dining_home\" id=\"dining_search_form\" name=\"dining_search_form\" method=\"get\">");
    out.println(" <input type=\"hidden\" name=\"search\" value=\"\">");

    out.println("<div class=\"res_left\">");

        out.println("<div class=\"sub_main\">");
        out.println("<strong>Please Select a Member</strong>  &nbsp; &nbsp; Note: Click on Names &nbsp;&gt;&gt;");
        
        out.println("<br><a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"erasename()\">Erase</a>&nbsp;&nbsp;");          // added 8/25/11
        
        out.println(" <input type=\"text\" name=\"search_name\" class=\"res_name\" value=\"\" onfocus=\"this.blur()\"" + "></td>");
        out.println(" <input type=\"hidden\" name=\"search_data\" value=\"\">");
        out.println("</div>"); // end sub_main

        // display the go back and submit buttons
        out.println("<br><div style=\"text-align:center;\">");
      //  out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false;\" />");
        out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Home\" onclick=\"window.location.href='Dining_home'\" />");
        out.println("<input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Search\" />");
        out.println("</div>");
        
        
       //  NEW - BP 8-24-11  (added link to search for user's reservations)
        
       out.println("<br><br><div style=\"text-align:center;\">");
       out.println("<input type=\"hidden\" name=\"search_name_self\" value=\"\">");
       out.println("<input id=\"commit_subl\" name=\"submit_long\" type=\"button\" value=\"Search Your Reservations\" onclick=\"doSelf('" +fullname+ "','" +person_id+ "')\" />");
       out.println("</div>");

    out.println("</div>"); // end res_left

    //
    // output partner list
    //
    out.println("<div class=\"res_mid\" id=\"name_select\">");

        out.println(" <div class=\"name_list\" id=\"name_list_content\">");
            nameLists.displayPartnerList(user, ProcessConstants.DINING_ACTIVITY_ID, 0, con, out);         // use dining_activity_id - using zero for golf now
        out.println("</div>"); // end name_list_content div

    //    out.println("<div class=\"main_warning\" style=\"margin-top:10px;\">Note: DO NOT USE Your Browser's Back Button!</div>");

    out.println("</div>"); // end res_mid

    //
    // output letter box list
    //
    out.println("<div class=\"res_right\">");
    nameLists.getTable(out, user);
    out.println("</div>"); // end res_right div

    out.println("</form>");

    out.println("<div class=\"clearFix\"></div>"); // clear the float

    out.println("</div>"); // end reservations

    
    //
    out.println("<script type=\"text/javascript\">");
    out.println("function goBack() {");
    out.println(" window.history.back(-1);");
    out.println("}");

    out.println("function erasename(elem) {");
    out.println(" eval(\"document.dining_search_form.search_name.value = '';\")");
    out.println("}");
    
    out.println("function goSearch() {");
    out.println(" var f = document.dining_search_form;");
    out.println(" if(f.name.value='') {");
    out.println("  alert('Please select a name first.');");
    out.println(" } else {");
    out.println("  f.submit();");
    out.println(" }");
    out.println("}");

    out.println("function doSelf(fullname,person_id) {");
    out.println(" var f = document.dining_search_form;");
    out.println(" f.search_name_self.value = fullname;");
    out.println(" f.search_data.value = person_id;");
    out.println(" f.submit();");
    out.println("}");

    out.println("function move_name(pName) {");
    out.println(" var f = document.dining_search_form;");
    out.println(" var names = new Array();");
    out.println(" array = pName.split(':'); ");
    out.println(" var name = array[0];");
    out.println(" var data = array[1];"); // person_id - if absent it's 'undefined' so test and set to zero too
    out.println(" if (isNaN(data)) data = 0;");
    out.println(" f.search_name.value = name;");
    out.println(" f.search_data.value = data;");
    out.println("}");

    out.println("function subletter(x) {");
    out.println(" $(\"#name_list_content\").load(\"data_loader?name_list&letter=\" + x);");
    out.println("}");

    //out.println("$(document).ready(function() {");
    //out.println(" $(document).loading({onAjax:true, text: 'Loading Names...', effect: 'ellipsis'});");
    //out.println("});");

    out.println("</script>");
 }


 private void viewSearchResults(String club, String user, HttpServletRequest req, PrintWriter out, Connection con_d) {


    PreparedStatement pstmt = null, pstmt2 = null;
    ResultSet rs = null, rs2 = null;

    int person_id = 0;

    try { person_id = Integer.parseInt(req.getParameter("search_data")); }
    catch (Exception ignore) { }

    //int organization_id = Utilities.getOrganizationId(con);
    
    String fullname = req.getParameter("search_name_self");     
    
    if (fullname == null || fullname.equals("")) {       // if user's name not provided
       
       fullname = req.getParameter("search_name");   // then user wants to search for another member's reservations - get that name
    }


    out.println("<div class=\"reservations\">");

    //out.println("<div id=\"breadcrumb\">Home / Search Reservations / Results <a href=\"#\" onclick=\"goBack()\">&lt;&lt; Go Back</a></div>");

    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Search Results", req);
       
    Common_skin.outputLogo(club, dining_activity_id, out, req);


    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div id=\"reservation_action_title\">Search Results</div>");

    out.println("<br>");

    //
    // output the instructions
    //
    //out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>: This is where we can tell the user how to search for another member's reservations.</div>");


    //
    // display member's name
    //
    //out.println("<br><h2 align=center>Upcoming Reservations for " + req.getParameter("search_name") + "</h2><br>");
    out.println("<br><h2 align=center>Upcoming Reservations for " + fullname + "</h2><br>");

    
    out.println("<div id=\"event_list_table_brdr\">");

    out.println("<div id=\"event_list_table\">");
    out.println("<table cellpadding=0 cellspacing=0 border=0><tbody>");
    out.println("<tr valign=\"top\">");
    out.println("<td class=\"head\">DATE</td>");
    out.println("<td class=\"head\">TIME</td>");
    out.println("<td class=\"head\">LOCATION</td>");
    out.println("<td class=\"head\" align=\"right\">CATEGORY&nbsp;&nbsp;</td>");
    out.println("</tr>");

    try {

        int count = 0;

        // lookup this member in the reservations table to see if they are already signed up for this dining event
        pstmt = con_d.prepareStatement ("" +
                "SELECT r.*, " +
                    "to_char(r.time, 'HH24MI') AS time_int, " +
                    "to_char(r.date, 'YYYYMMDD')::int AS date_int, " +
                    "events.name AS event_name, locations.name AS location_name " +
                "FROM reservations r " +
                "LEFT OUTER JOIN events ON events.id = r.event_id " +
                "LEFT OUTER JOIN locations ON locations.id = r.location_id " +
                "WHERE " +
                    "r.state <> 'cancelled' AND " +
                    "r.person_id = ? AND " +
                    "r.date >= now() " +
                "ORDER BY r.date ASC, r.time ASC");

        pstmt.setInt(1, person_id);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            //
            // output each signup
            out.println("<tr valign=\"top\">");
            out.println("<td>" + Utilities.getDateFromYYYYMMDD(rs.getInt("date_int"), 2) + "</td>");
            out.println("<td>" + Utilities.getSimpleTime(rs.getInt("time_int")) + "</td>");
            out.println("<td>" + rs.getString("location_name") + "</td>");
          //out.println("<td align=\"right\">" + ((rs.getString("category").equals("dining")) ? "Dining" : rs.getString("event_name")) + "</td>");
            if (rs.getString("category").equals("dining")) {
                out.println("<td align=\"right\">Dining</td>");
            } else {
                out.println("<td align=\"right\"><a href=\"#\" data-ftlink=\"Dining_home?nowrap&event_popup&event_id=" + rs.getInt("event_id") + "\" class=\"dining_event_modal\">" + rs.getString("event_name") + "</a></td>");
            }
            //out.println("<td>" + ((has_guests) ? "Yes" : "No") + "</td>");
            out.println("</tr>");

            count++;
        }

        out.println("<tr valign=\"top\">");
        out.print("<td colspan=\"4\" align=\"center\"><b>");
        if ( count == 0 ) {
            out.print("No Registrations Yet");
        } else {
            out.print(count + " Reservations");
        }
        out.println(  "</b></td>");
        out.println(" </tr>");


    } catch (Exception exc) {

        Utilities.logError("Dining_home.viewEventInfo: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</table>");

    out.println("</div>"); // nested event_list_table
    out.println("</div>"); // nested event_list_table_brdr



    // display the go back button
    out.println("<br><div style=\"text-align:center;\">");
    out.println("<button id=\"back\" name=\"back\" value=\"Go Back\" onclick=\"goBack();\">Go Back</button>");
    out.println("</div>");


    out.println("<div class=\"clearFix\"></div>"); // clear the float

    out.println("</div>"); // end reservations


    //
    out.println("<script type=\"text/javascript\">");
    out.println("function goBack() {");
    out.println(" window.history.back(-1);");
    out.println("}");
    out.println("</script>");
 }


 private void viewSettingsPage(String club, String user, HttpServletRequest req, HttpServletResponse resp, PrintWriter out, Connection con, Connection con_d) {

    //
    // Notes:  if club has roster sync enabled then they should not be allowed to change their email address from here
    //

    int person_id = Utilities.getPersonId(user, con_d);

    //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Settings</div>"); // <a href=\"#\" onclick=\"goBack()\">&lt;&lt; Go Back</a>
    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Settings", req);
       
    Common_skin.outputLogo(club, dining_activity_id, out, req);


    out.println("<div id=\"tt1_left\">");
    //out.println("<p><strong>Manage Settings</strong></p></div>");


    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>:  Use this page to change your personal settings. Only change the fields you want to update. Click the 'Update' button to save the changes.</div>");

    try {

        Member_services services = new Member_services();

        services.doGet(req, resp);

    } catch (Exception exc) {

        out.println("ERROR: " + exc.toString());

    }

 }


 private void viewDailyReservations(int organization_id, String club, String user, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {


    int date = 0;

    try { date = Integer.parseInt(req.getParameter("date")); }
    catch (Exception ignore) { }


    PreparedStatement pstmt = null;
    ResultSet rs = null;
/*
    // do entire html page
    out.println("<html>");
    out.println("<head>" +
           "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
           "<link href=\"/" +rev+ "/assets/stylesheets/sitewide_dining.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />\n" +
           "<link rel=\"stylesheet\" href=\"/" +rev+ "/assets/jquery/jquery.loading.1.6.css\" type=\"text/css\" />\n" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/assets/jquery/jquery.js\"></script>\n" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/assets/jquery/jquery.loading.1.6.4.js\"></script>\n" +
           "<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>\n" +
           "</head>");
*/
    out.println("<body id=\"event_details_window\">");

    //boolean has_guests = false;

    out.println("<br><h2 align=center>Reservations For " + Utilities.getDateFromYYYYMMDD(date, 2) + "</h2><br>");

    out.println("<div id=\"event_list_table_brdr\">");

    out.println("<div id=\"event_list_table\">");
    out.println("<table cellpadding=0 cellspacing=0 border=0><tbody>");
    out.println("<tr valign=\"top\">");
    out.println("<td class=\"head\">NAME</td>");
    out.println("<td class=\"head\">TIME</td>");
    out.println("<td class=\"head\" align=\"right\">LOCATION</td>");
  //out.println("<td class=\"head\" align=\"right\">GUESTS&nbsp;&nbsp;</td>");
    out.println("</tr>");

    try {

        int count = 0;

        // query all member reservations for this date
        pstmt = con_d.prepareStatement ("" +
                "SELECT reservee_name, parent_id, " +
                    "to_char(r.time, 'HH24MI') AS time_int, " +
                    "loc.name AS location_name " +
                "FROM reservations r " +
                "LEFT OUTER JOIN locations AS loc ON r.location_id = loc.id " +
                "WHERE " +
                    "r.organization_id = ? AND " +
                    "r.state <> 'cancelled' AND " +
                    "r.reservee_category = 'member' AND " +
                    "r.category = 'dining' AND " +
                    "to_char(r.date, 'YYYYMMDD')::int = ? " +
                "ORDER BY r.reservee_name, location_name");

        pstmt.setInt(1, organization_id);
        pstmt.setInt(2, date);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            //
            // output each signup
            out.println("<tr valign=\"top\">");
            out.println("<td>" + rs.getString("reservee_name") + "</td>");
            out.println("<td>" + Utilities.getSimpleTime(rs.getInt("time_int")) + "</td>");
            out.println("<td align=\"right\">" + rs.getString("location_name") + "</td>");
          //out.println("<td>" + ((has_guests) ? "Yes" : "No") + "</td>");
            out.println("</tr>");

            count++;
        }

        out.println("<tr valign=\"top\">");
        out.print("<td colspan=\"4\" align=\"center\"><b>");
        if ( count == 0 ) {
            out.print("No Registrations Yet");
        } else {
            out.print(count + " Reservations");
        }
        out.println("</b></td>");
        out.println("</tr>");


    } catch (Exception exc) {

        Utilities.logError("Dining_home.viewDailyReservations: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</table>");

    out.println("</div>");
    out.println("</div>");
/*
    out.println("<div align=\"center\">");
    out.println("<button id=\"back\" name=\"back\" onclick=\"TopUp.close()\">Close</button>");
    out.println("</div>");
*/

    out.println("</body>");
    out.println("</html>");

 }


 //
 // view event info in a standalone page
 // called by: clicking on event link within the calendar
 //
 private void viewEventInfoPage(String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d, boolean ext_login) {

    if (ext_login == false) {    // if normal entry 
    
       //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / <a href=\"Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID + "\">Calendar</a> / View Event</div>");
       Common_skin.outputBreadCrumb(club, dining_activity_id, out, "View Event", req);
       
       Common_skin.outputLogo(club, dining_activity_id, out, req);


    } else {   // came from external login (link in email)

       out.println("<div id=\"breadcrumb\"><a href=\"Logout\">Exit</a> / View Event</div>");
    }

    out.println("<div id=\"tt1_left\">");
    out.println("<p><strong>Dining Event Information</strong></p></div>");

    viewEventInfo(club, user, req, out, con, con_d, ext_login);

 }


 //
 // view event info inside a popup
 // called by the event listing page
 //
 private void viewEventInfoPopup(String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d) {


    out.println("<body id=\"event_details_window\">");

    viewEventInfo(club, user, req, out, con, con_d);

    out.println("</body>");
    out.println("</html>");
    
 }


 //
 // output the raw event info content (needs to be wrapped in something)
 // called by viewEventInfoPage, viewEventInfoPopup
 //
 private void viewEventInfo(String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d) {

    viewEventInfo(club, user, req, out, con, con_d, false);    
 }

 private void viewEventInfo(String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d, boolean ext_login) {


    int event_id = 0;

    try { event_id = Integer.parseInt(req.getParameter("event_id")); }
    catch (Exception ignore) { }

    boolean isFTCP_user = Utilities.isFTCPuser(req);

    if (event_id > 0) {

        PreparedStatement pstmt = null, pstmt2 = null;
        ResultSet rs = null, rs2 = null;

        boolean is_signed_up = false;
        boolean can_sign_up = false;
        boolean in_signup_period = false;

        int signup_begin_day = 0, signup_end_day = 0;
        int signup_begin_date = 0, signup_end_date = 0;
        int person_id = Utilities.getPersonId(user, con);
        int organization_id = Utilities.getOrganizationId(con);
        int today = (int)Utilities.getDate(con);

        String state = "", cost = "", costs = "";
    

        out.println("<div id=\"event_details_window_brdr\">");

        out.println("<div id=\"event_details_table\">");

        out.println("<table>");

        //out.println("<tr>");
        //out.println("<td align=center>");

        //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Dining Event Listing / Event Details</div>");

        //out.println("<div style=\"clear:both;\"></div>");


        //out.println("<div id=\"main\" align=\"center\">");
        int reservation_id = 0;
        try {

            pstmt = con_d.prepareStatement ("" +
                        "SELECT e.id, e.name, e.members_can_make_reservations, " +
                            "e.minimum_advance_days, e.maximum_advance_days, " +
                            "e.time_format, e.seatings, " +
                            "to_char(e.start_time, 'HH24:MI') AS time1, " +
                            "to_char(e.start_time, 'HH24MI') AS stime, " +
                            "to_char(e.end_time, 'HH24MI') AS etime, " +
                            "to_char(e.date, 'YYYYMMDD')::int AS our_date, " +
                            "e.costs, e.online_message, e.maximum_party_size, " +
                            "e.notes, e.theme, " +
                            "loc.name AS location_name," +
                            "dc.name AS dress_code, " +
                            "ms.name AS musical_style " +
                        "FROM events e " +
                        "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                        "LEFT OUTER JOIN dress_codes AS dc ON e.dress_code_id = dc.id " +
                        "LEFT OUTER JOIN musical_styles AS ms ON e.musical_style_id = ms.id " +
                        "WHERE e.id = ?");

            pstmt.setInt(1, event_id);

            rs = pstmt.executeQuery();

            if ( rs.next() ) {


                // lookup this member in the reservations table to see if they are already signed up for this dining event
                pstmt2 = con_d.prepareStatement ("" +
                        "SELECT state, id " +
                        "FROM reservations " +
                        "WHERE " +
                            "category = 'event' AND " +
                            "state <> 'cancelled' AND " +
                            "event_id = ? AND " +
                            "person_id = ? " +
                            "ORDER BY updated_at ASC " +
                            "LIMIT 1");

                pstmt2.setInt(1, event_id);
                pstmt2.setInt(2, person_id);

                rs2 = pstmt2.executeQuery();

                if ( rs2.next() ) {

                    is_signed_up = true;
                    state = rs2.getString("state");
                    reservation_id = rs2.getInt("id");

                } else {

                    is_signed_up = false;
                    state = "";
                }

                // override if user is cancelled - this will allow them to sign up again (not used)
                //if (state.equalsIgnoreCase("cancelled")) is_signed_up = false;

                can_sign_up = rs.getBoolean("members_can_make_reservations");

                signup_end_day = rs.getInt("minimum_advance_days");
                signup_begin_day = rs.getInt("maximum_advance_days");

                signup_begin_date = Utilities.getDate(rs.getInt("our_date"), (signup_begin_day * -1));
                signup_end_date = Utilities.getDate(rs.getInt("our_date"), (signup_end_day * -1));

                in_signup_period = (signup_begin_date <= today && signup_end_date >= today);

                // extract the cost for the event
                parmDiningCosts parmCosts = new parmDiningCosts();
                parmCosts.costs = rs.getString("costs");

                if ( !parmCosts.parseCosts() ) {

                    out.println("<!--  FATAL ERROR: " + parmCosts.err_string + " -->");
                    out.println("<!--  FATAL ERROR: " + parmCosts.err_message + " -->");

                }

                parmDiningSeatings parmSeatings = new parmDiningSeatings();
                if ( rs.getString("time_format").equals("multiple") ) {
                    // extract the seating times for the event (if applicable)
                    parmSeatings.seatings = rs.getString("seatings");

                    if ( !parmSeatings.parseSeatings() ) {

                        out.println("<!--  FATAL ERROR: " + parmSeatings.err_string + " -->");
                        out.println("<!--  FATAL ERROR: " + parmSeatings.err_message + " -->");

                    }

                    out.println("<!--  parmSeatings.seatings_found=" + parmSeatings.seatings_found + " -->");

                }
                
                out.println("<tr><td class=\"name\">");
                out.println("" + rs.getString("name") + "");
                out.println("</td></tr>");

                if (rs.getString("notes") != null) {
                    out.println("<tr><td class=\"detail\">");
                    out.println("<span style=\"font-size:12px; font-weight:normal\">" + rs.getString("notes").replace("\r", "<br>"));
                    out.println("</span><br></td></tr>");

                }

                out.println("<tr><td class=\"detail\">Where & When: <span style=\"font-size:12px; font-weight:normal\">");
                out.println("&nbsp;" + rs.getString("location_name") + " on ");
                out.println("" + Utilities.getDayNameFromDate(rs.getInt("our_date")) + " " + Utilities.getDateFromYYYYMMDD(rs.getInt("our_date"), 2) + "");
                out.println(" from " + Utilities.getSimpleTime(rs.getInt("stime")) + " to " + Utilities.getSimpleTime(rs.getInt("etime")) + "");
                out.println("</span></td></tr>");

                
                /*    // replaced with above single line
                out.println("<tr><td class=\"detail\">");
                out.println("" + rs.getString("location_name") + "");
                out.println("</td></tr>");

                out.println("<tr><td class=\"detail\">");
                out.println("" + Utilities.getDayNameFromDate(rs.getInt("our_date")) + " " + Utilities.getDateFromYYYYMMDD(rs.getInt("our_date"), 2) + "");
                out.println("</td></tr>");

                out.println("<tr><td class=\"detail\">");
                out.println("" + Utilities.getSimpleTime(rs.getInt("stime")) + " to " + Utilities.getSimpleTime(rs.getInt("etime")) + "");
                out.println("</td></tr>");
                 */

                if ( rs.getString("time_format").equals("multiple") ) {
                    
                    out.println("<tr><td class=\"detail\">");
                    out.println("Seating Times: ");
                    out.println("<span style=\"font-size:12px; font-weight:normal\">");
                    for (int i = 0; i < parmSeatings.seatings_found; i++) {

                        out.println("" + ((i==0) ? "" : ", ") + parmSeatings.seating_timeA[i]);
                    }
                    out.println("</span></td></tr>");
                
                }
                
                out.println("<tr><td class=\"detail\">");
                out.println("Options:<br>");
                out.println("<span style=\"font-size:12px; font-weight:normal\">");
                for (int i = 0; i < parmCosts.costs_found; i++) {

                    out.println("&nbsp;&nbsp;" + parmCosts.price_categoryA[i] + ": $" + parmCosts.costA[i] + "<br>");
                }
                out.println("</span></td></tr>");

                if (rs.getString("theme") != null && !rs.getString("theme").equals("")) {

                    out.println("<tr><td class=\"detail\">");
                    out.println("Theme: &nbsp;<span style=\"font-size:12px; font-weight:normal\">" + rs.getString("theme"));
                    out.println("</span></td></tr>");

                }
                
                if (rs.getString("dress_code") != null) {

                    out.println("<tr><td class=\"detail\">");
                    out.println("Dress Code: &nbsp;<span style=\"font-size:12px; font-weight:normal\">" + rs.getString("dress_code"));
                    out.println("</span></td></tr>");
                
                }

                if (rs.getString("musical_style") != null) {

                    out.println("<tr><td class=\"detail\">");
                    out.println("Musical Style: &nbsp;<span style=\"font-size:12px; font-weight:normal\">" + rs.getString("musical_style"));
                    out.println("</span></td></tr>");

                }

              //  out.println("<tr><td class=detail style=\"font-weight:normal\">");
                out.println("<tr><td class=\"detail\">");
                out.println("Registration: &nbsp;<span style=\"font-size:12px; font-weight:normal\">Begins " + Utilities.getDateFromYYYYMMDD(signup_begin_date, 2) + " " +
                        "and ends " + Utilities.getDateFromYYYYMMDD(signup_end_date, 2) + "");
                out.println("</span></td></tr>");

              //  out.println("<tr><td class=detail style=\"font-weight:normal\">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                out.println("<tr><td class=\"detail\"><BR>Current Status: &nbsp;<span style=\"font-size:12px; font-weight:normal\">");
                
                if (is_signed_up) {

                    //we are signed up for this event (we have an active non-cancelled reservation)

                   if (ext_login == false) { 
                       out.println("<i>You are currently " + state + " for this event.</i> &nbsp;&nbsp;");

                        if ( can_sign_up && in_signup_period) { // true || !isFTCP_user

                            out.println("(<a class=\"dining_signup_linkB\" href=\"Dining_slot?event&amp;action=edit&amp;orig=calendar&amp;reservation_id=" + reservation_id + "&amp;event_id=" + rs.getInt("id") + "&amp;reservation[category]=event&amp;date=" + rs.getInt("our_date") + "&amp;" +
                            "reservation[reservation_time]=" + rs.getString("time1") + "&amp;reservation[covers]=" + rs.getInt("maximum_party_size") + "\">Cancel / Edit Sign-up</a>)");
                        } else {

                           // member should not be able to access this reservation since it's call in only
                           out.println("Please call the club to request changes to your reservation.");
                        }

                   } else {
                       out.println("<i>You are already registered for this event.</i> &nbsp;&nbsp;Please login normally to make any changes.");  // external login from email link
                   }
                       
                } else if (can_sign_up) {

                   // we are NOT signed and event does allow online signups

                   if (ext_login == false) {

                       if (in_signup_period) {

                         if (parmDining.isEventOpen(organization_id, rs.getInt("id"), con, con_d)) {

                             out.println("<i>You are not currently signed up for this event.</i> &nbsp;&nbsp;");

                             //if ( true || !isFTCP_user ) {

                                out.println("(<a class=\"dining_signup_linkA\" href=\"Dining_slot?event&amp;action=new&amp;orig=calendar&amp;event_id=" + rs.getInt("id") + "&amp;reservation[category]=event&amp;date=" + rs.getInt("our_date") + "&amp;" +
                                        "reservation[reservation_time]=" + rs.getString("time1") + "&amp;reservation[covers]=" + rs.getInt("maximum_party_size") + "\">Register Now</a>)");
                             //}
                             
                         } else {

                             out.println("<i>Please call the club if you wish to register for this event.</i>");

                         }

                       } else {
/*
                            // reservation period has either not begun or has ended
                            if (signup_begin_date > today) {
                                out.println("  Registration<br>Opens " + Utilities.getDateFromYYYYMMDD(signup_begin_date, 2) + "");
                            } else {
                                out.println("  Registration<br>Ended " + Utilities.getDateFromYYYYMMDD(signup_end_date, 2) + "");
                            }
                            //out.println("<i>You are not registered for this event.</i>");
*/
                       }

                   } else {     // came from external login - from email link to register for the event

                       if (in_signup_period) {

                         if (parmDining.isEventOpen(organization_id, rs.getInt("id"), con, con_d)) {

                             out.println("<i>You are not currently signed up for this event.</i> &nbsp;&nbsp;" +
                                         "(<a class=\"dining_signup_linkA\" href=\"Dining_slot?event&amp;action=new&amp;ext_login=yes&amp;event_id=" + rs.getInt("id") + "&amp;reservation[category]=event&amp;date=" + rs.getInt("our_date") + "&amp;" + 
                                         "reservation[reservation_time]=" + rs.getString("time1") + "&amp;reservation[covers]=" + rs.getInt("maximum_party_size") + "\">Register Now</a>)");

                         } else {

                             out.println("<i>Please call the club if you wish to register for this event.</i>");

                         }

                       } else {

                           out.println("<i>You are not registered for this event.</i>");

                       }
                   }
                       
                } else {
                    out.println("<i>Sorry, online reservations for this event are not being taken at this time.<br>Please contact the club as openings may be available.</i>");
                }
                out.println("</span></td></tr>");
                
                out.println("<tr><td>&nbsp;</td></tr>");
                out.println("<tr><td class=\"message\">");
                out.println("<p>" + rs.getString("online_message") + "</p>");
                out.println("</td></tr>");

            }

        } catch (Exception exc) {

            Utilities.logError("Dining_home.viewEventInfo: Err=" + exc.toString());

        } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

        }

        // marker for auto-scroll
        out.println("<tr><td>&nbsp;<span id=\"scrollMarker\"</td></tr>");

        
        boolean public_registration_lists = parmDining.areEventReservationsPublic(Utilities.getOrganizationId(con), con_d);

        if (public_registration_lists) {

            boolean group_reservations = false; // default

            // set custom default for any clubs that want it
            if (club.equals("tontoverde")) group_reservations = true; // default for tontoverde

            // now apply the users selection if one has been made
            if (req.getParameter("grpby") != null && req.getParameter("grpby").equals("1")) group_reservations = true;

            out.println("<tr><td class=\"detail\" align=\"center\"><p>REGISTRATION LIST</p></td></tr>");

            out.println("<tr><td class=\"detail\" align=\"center\" style=\"font-size:12px\">");
            out.println("<form>");
            out.println("<input type=\"hidden\" name=\"nowrap\" value=\"nowrap\">");
            out.println("<input type=\"hidden\" name=\"event_popup\" value=\"event_popup\">");
            out.println("<input type=\"hidden\" name=\"event_id\" value=\"" + event_id + "\">");
            out.println("<label for=\"rbtn1\" style=\"display:inline-block\">By Name</label> <input id=\"rbtn1\" type=\"radio\" name=\"grpby\" value=\"0\" onclick=\"this.form.submit()\" " + ((group_reservations) ? "" : "checked") + ">&nbsp; &nbsp; &nbsp;");
            out.println("<label for=\"rbtn2\" style=\"display:inline-block\">By Reservation</label> <input id=\"rbtn2\" type=\"radio\" name=\"grpby\" value=\"1\" onclick=\"this.form.submit()\" " + ((group_reservations) ? "checked" : "") + ">");
            out.println("</form>");
            out.println("</td></tr>");

            out.println("<tr>");
            out.println("<td align=\"center\">");


            // SHOW SIGNUPS
            
            out.println("<div id=\"event_list_table_brdr\">");

            out.println("<div id=\"event_list_table\">");
            out.println("<table cellpadding=0 cellspacing=0 border=0><tbody>");
            out.println("<tr valign=\"top\">");
            out.println("<td class=\"head\">NAME</td>");
            out.println("<td class=\"head\">TIME</td>");
            out.println("<td class=\"head\">STATUS</td>");
            out.println("<td class=\"head\" align=\"right\">REGISTERED AT&nbsp;&nbsp;</td>");
            out.println("</tr>");

            try {

                int count = 0;
                int non_member = 0;
                int groups = 0;
                int last_res_id = 0;

                if (!group_reservations) {

                    pstmt = con_d.prepareStatement ("" +
                            "SELECT reservee_name, state, reservee_category, " +
                                "to_char(time, 'HH24MI') AS time_int, " +
                                "to_char(created_at, 'HH24MI')::int AS rtime_int, " +
                                "to_char(created_at, 'YYYYMMDD')::int AS rdate_int " +
                            "FROM reservations " +
                            "WHERE " +
                                "category = 'event' AND " +
                                "state <> 'cancelled' AND " +
                                "event_id = ? " +
                            "ORDER BY reservee_name");

                } else {

                    pstmt = con_d.prepareStatement ("" +
                            "SELECT reservee_name, state, reservation_number, " +
                                "to_char(time, 'HH24MI') AS time_int, " +
                                "to_char(created_at, 'HH24MI')::int AS rtime_int, " +
                                "to_char(created_at, 'YYYYMMDD')::int AS rdate_int," +
                                "CASE WHEN parent_id IS NULL THEN true " +
                                "ELSE false " +
                                "END AS master " +
                            "FROM reservations " +
                            "WHERE " +
                                "category = 'event' AND " +
                                "state <> 'cancelled' AND " +
                                "event_id = ? " +
                            "ORDER BY time, reservation_number, master DESC");
                }
                
                pstmt.setInt(1, event_id);

                rs = pstmt.executeQuery();

                while ( rs.next() ) {
                    
                    if (group_reservations) {

                        if (last_res_id != rs.getInt("reservation_number")) {

                            // switching reservations - dispay seperator (skip the very first time through here)
                            if (last_res_id != 0) out.println("<tr><td colspan=\"4\"><hr></td></tr>");
                            last_res_id = rs.getInt("reservation_number");
                            groups++;
                        }
                    }

                    //
                    // output each signup (if no group then hide none member reservations and bump guest count)
                    if ( !group_reservations && !rs.getString("reservee_category").equals("member") ) {

                        non_member++;

                    } else {

                        out.println("<tr valign=\"top\">");
                        out.println("<td>" + rs.getString("reservee_name") + "</td>");
                        out.println("<td>" + Utilities.getSimpleTime(rs.getInt("time_int")) + "</td>");
                        out.println("<td>" + Utilities.titleCase(rs.getString("state")) + "</td>");
                        out.println("<td align=\"right\">" + Utilities.getDateFromYYYYMMDD(rs.getInt("rdate_int"), 2) + " " + Utilities.getSimpleTime(rs.getInt("rtime_int")) + "&nbsp;&nbsp;</td>");
                        //out.println("<td>" + ((has_guests) ? "Yes" : "No") + "</td>");
                        out.println("</tr>");

                    }
                    count++;
                }

                out.println("<tr valign=\"top\">");
                out.print("<td colspan=\"4\" align=\"center\"><b>");
                if ( count == 0 ) {
                    out.print("No Registrations Yet");
                } else {
                    if (group_reservations) {
                        out.print(count + " Reservations in " + groups + " groups.");
                    } else {
                        out.print(count + " Reservations");
                        if (non_member > 0) out.println(" including " + non_member + " guest reservations");
                    }
                }
                out.println("</b></td></tr>");


            } catch (Exception exc) {

                Utilities.logError("Dining_home.viewEventInfo: Err=" + exc.toString());

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { pstmt.close(); }
                catch (Exception ignore) {}

            }

            out.println("</table>");

            out.println("</div>"); // nested event_list_table
            out.println("</div>"); // nested event_list_table_brdr

            out.println("</td>");
            out.println("</tr>");

        }
        
        out.println("</table>");

        out.println("</div>");
        out.println("</div>");

        // if the grouping radio button were used at all then auto-scroll the window to the registration list
        if (req.getParameter("grpby") != null) {
            
            out.println("<script>");
            out.println("$(document).ready(function() {");
            out.println("window.scrollTo(0,$(\"#scrollMarker\").offset().top);");
            out.println("");
            out.println("});");
            out.println("</script>");

        }

    } // end event id check
    
 }

 private void viewEventListing_new(int organization_id, String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d) {
     

        //ArrayList<Object> eventList = new ArrayList<Object>();

        parmEvent parm = new parmEvent();
        
        ArrayList<parmEvent> eventList = Utilities.getEvents(ProcessConstants.DINING_ACTIVITY_ID, user, con, con_d);
     
        out.println("<!-- # of events found = " + eventList.size() + " -->");
        
    int today = (int)Utilities.getDate(con);
    int event_id = 0;
    String event = req.getParameter("event_id");
    try {
        event_id = Integer.parseInt(event);
    } catch (NumberFormatException ignore) {}


    if (event_id != 0) {

        out.println("<script type=\"text/javascript\">");
        out.println("$(document).ready(function() {");
        out.println(" $(\"#defaultEvent\").click();");
        out.println("});");
        out.println("</script>");
    }
    
    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Dining Event Listing", req);
        
    Common_skin.outputLogo(club, dining_activity_id, out, req);
        
        
    out.println("<div id=\"tt1_left\">");
    //out.println("<p><strong>Upcoming Dining Events</strong></p>");
    out.println("</div>");

    out.println("<div id=\"event_list_table_brdr\">");

    out.println("<div id=\"event_list_table\">");
    out.println("<table cellpadding=0 cellspacing=0 border=0><tbody>");
    out.println("<tr valign=\"top\">");
    out.println("<td class=\"head\">EVENT NAME</td>");
    out.println("<td class=\"head\">DATE</td>");
    out.println("<td class=\"head\">TIME FRAME</td>");
    out.println("<td class=\"head\">LOCATION</td>");
    out.println("<td class=\"head\">YOUR STATUS</td>");
    out.println("<td class=\"head\">PRICING</td>");
    out.println("<td class=\"head\" align=\"right\">SIGN-UP PERIOD&nbsp;&nbsp;</td>");
    out.println("</tr>");

    try {
    
    for (int i = 0; i < eventList.size(); i++) {
        
        parm = eventList.get(i);
        
        if (parm == null) {
            out.println("<!-- parm is null -->");
        } else {
            out.println("<!-- parm.id=" + parm.id + ", parm.name=" + parm.name + " -->");
        }
        
                //
                // output each event
                out.println("<tr valign=\"top\">");
                out.println("<td><a " + ((event_id == parm.id) ? "id=defaultEvent " : "") + "href=\"#\" data-ftlink=\"Dining_home?nowrap&event_popup&event_id=" + parm.id + "\" class=\"dining_event_modal\">" + parm.name + "</a></td>"); //  toptions=\"resizable = 1\"  onclick=\"showEvent('" + rs.getInt("id") + "')\"
                out.println("<td>" + parm.dateString + "</td>");
                out.println("<td>" + parm.startTimeString + " to " + parm.endTimeString + "</td>");
                out.println("<td>" + parm.locationName + "</td>");
                if (parm.isSignedUp == true) {

                    //we are signed up for this event (we have an active non-cancelled reservation)

                    if (!parm.state.equalsIgnoreCase("registered")) {
                        out.println("<td>" + parm.state + "</td>");
                    } else {
                        out.println("<td><a href=\"Dining_slot?event&action=edit&orig=calendar&reservation_id=" + parm.reservationId + "&event_id=" + parm.id + "&reservation[category]=event&date=" + parm.date + "&reservation[reservation_time]=" + parm.startTime24hString + "&reservation[covers]=" + parm.maximumPartySize + "\">" + parm.state + "</a></td>");
                    }

                } else if (parm.canSignUp == true) { //  || Common_Server.SERVER_ID == 4

                    // we are NOT signed and event does allow online signups

                    if (parm.inSignUpPeriod == true) {

                        if (parm.isEventOpen == true) {

                            //if (!club.equals("demoroger")) {
                                
                                out.println("<td><a href=\"Dining_slot?event&action=new&orig=calendar&event_id=" + parm.reservationId + "&reservation[category]=event&date=" + parm.date + "&reservation[reservation_time]=" + parm.startTime24hString + "&reservation[covers]=" + parm.maximumPartySize + "\">Register Now</a></td>");  // reservation[date]=" + rs.getString("date1") + "&
                            //}

                        } else {
                            out.println("<td>Please call the club</td>");
                        }

                    } else {
                        // reservation period has either not begun or has ended
                        if (parm.registrationStart > today) {
                            out.println("<td>Registration<br>Opens " + parm.registrationStartString + "</td>");
                        } else {
                            out.println("<td>Registration<br>Ended " + parm.registrationEndString + "</td>");
                        }
                    }

                } else {

                    // we are NOT signed and event does NOT allow online signups

                    out.println("<td>N/A</td>");

                }
                out.println("<td>");

                for (int i2 = 0; i2 < parm.eventCostPrice.size(); i2++) {

                    out.println(parm.eventCostCategory.get(i2) + ": $" + parm.eventCostPrice.get(i2) + "<br>");
                }

                out.println("</td>");
                
                if (club.equals("tontoverde")) {
                    out.println("<td align=\"right\">Please call the club&nbsp;&nbsp;</td>");
                } else {
                    out.println("<td align=\"right\">" + parm.registrationStartString + " - " + parm.registrationEndString + "&nbsp;&nbsp;</td>");
                }
                out.println("</tr>");
     
    }
    
    } catch (Exception exc) {
        out.println("<!-- ERROR: " + exc.toString() + " -->");
        
    }
    
    
    out.println("</tbody></table>");
    out.println("</div>");

    out.println("</div>");

    out.println("<div id=\"tt2_left\" align=\"center\"><input type=\"button\" value=\"Home\" id=\"back\" onclick=\"window.location.href='Member_announce'\" /></div>");

    out.println("<div class=\"clearFix\"></div>"); // clear the float

 }

 private void viewEventListing(int organization_id, String club, String user, HttpServletRequest req, PrintWriter out, Connection con,  Connection con_d) {


    PreparedStatement pstmt = null, pstmt2 = null;
    ResultSet rs = null, rs2 = null;
    
    boolean is_signed_up = false;
    boolean can_sign_up = false;
    boolean in_signup_period = false;

    int signup_begin_day = 0, signup_end_day = 0;
    int signup_begin_date = 0, signup_end_date = 0;
    int person_id = Utilities.getPersonId(user, con);
    int today = (int)Utilities.getDate(con);
    
    String state = "", cost = "", price_category = "", price_type_id = ""; // costs = "",
    String no_online_text = "N/A";

    /*
     *  default behavior is to show all upcoming events - but if the club
     *  wants to hide events that members cannot signup for online
     *  then set hide_offline_events to true for that club. the no_online_text
     *  string can be changed to say 'Please Call' or something
     */
    boolean hide_offline_events = false;    // customize this value if clubs want to display events members cannot sign up for online

    int event_id = 0;
    String event = req.getParameter("event_id");
    try {
        event_id = Integer.parseInt(event);
    } catch (NumberFormatException ignore) {}

    out.println("<!-- event_id=" + event_id + " -->");

    out.println("<script type=\"text/javascript\">");

    if (event_id != 0) {

        out.println("$(document).ready(function() {");
        out.println(" $(\"#defaultEvent\").click();");
        out.println("});");

    }
    /*
    out.println("function showEvent(id) {");
    out.println(" ");
    out.println(" ");
    out.println("}"); // event&event_id=
    
    out.println("");

    out.println(" // document.dining_reservation_form.submit();");
    */
    out.println("</script>");


    //out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Dining Event Listing</div>");
    Common_skin.outputBreadCrumb(club, dining_activity_id, out, "Dining Event Listing", req);
        
    Common_skin.outputLogo(club, dining_activity_id, out, req);


    out.println("<div id=\"tt1_left\">");
    //out.println("<p><strong>Upcoming Dining Events</strong></p>");
    out.println("</div>");

    out.println("<div id=\"event_list_table_brdr\">");

    out.println("<div id=\"event_list_table\">");
    out.println("<table cellpadding=0 cellspacing=0 border=0><tbody>");
    out.println("<tr valign=\"top\">");
    out.println("<td class=\"head\">EVENT NAME</td>");
    out.println("<td class=\"head\">DATE</td>");
    out.println("<td class=\"head\">TIME FRAME</td>");
    out.println("<td class=\"head\">LOCATION</td>");
    out.println("<td class=\"head\">YOUR STATUS</td>");
    out.println("<td class=\"head\">PRICING</td>");
    out.println("<td class=\"head\" align=\"right\">SIGN-UP PERIOD&nbsp;&nbsp;</td>");
    out.println("</tr>");



    try {

        int reservation_id = 0;

        pstmt = con_d.prepareStatement ("" +
                        "SELECT e.id, e.name, e.members_can_make_reservations, " +
                            "e.minimum_advance_days, e.maximum_advance_days, " +
                            "to_char(e.start_time, 'HH24:MI') AS time1, " +
                            "to_char(e.start_time, 'HH24MI') AS stime, " +
                            "to_char(e.end_time, 'HH24MI') AS etime, " +
                            "to_char(e.date, 'YYYYMMDD')::int AS our_date, " +
                            "to_char(e.date, 'MM/DD/YYYY') AS date1, " +
                            "e.costs, loc.name AS location_name, " +
                            "e.start_time, e.date, e.maximum_party_size " +
                        "FROM events e " +
                        "LEFT OUTER JOIN locations AS loc ON e.location_id = loc.id " +
                        "WHERE e.organization_id = ? AND e.cancelled = false AND " +
                            "to_char(e.date, 'YYYYMMDD')::int >= ? " +
                        "ORDER BY e.date, e.start_time");
        
        pstmt.clearParameters();
        pstmt.setInt(1, organization_id);
        pstmt.setLong(2, Utilities.getDate(con));

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            // extract the event info

            can_sign_up = rs.getBoolean("members_can_make_reservations");

            //
            // hide events that members cannot sign up for online
            if (can_sign_up || (!can_sign_up && !hide_offline_events)) { //  || Common_Server.SERVER_ID == 4

                //costs = rs.getString("costs");

                signup_end_day = rs.getInt("minimum_advance_days");
                signup_begin_day = rs.getInt("maximum_advance_days");

                signup_begin_date = Utilities.getDate(rs.getInt("our_date"), (signup_begin_day * -1));
                signup_end_date = Utilities.getDate(rs.getInt("our_date"), (signup_end_day * -1));

                in_signup_period = (signup_begin_date <= today && signup_end_date >= today);

                parmDiningCosts parmCosts = new parmDiningCosts();
                parmCosts.costs = rs.getString("costs");

                if ( !parmCosts.parseCosts() ) {

                    out.println("<!--  FATAL ERROR: " + parmCosts.err_string + " -->");
                    out.println("<!--  FATAL ERROR: " + parmCosts.err_message + " -->");

                }

                out.println("<!-- costs_found=" + parmCosts.costs_found + " -->");
                
                // lookup this member in the reservations table to see if they are already signed up for this dining event
                pstmt2 = con_d.prepareStatement ("" +
                        "SELECT id, state " +
                        "FROM reservations " +
                        "WHERE " +
                            "category = 'event' AND " +
                            "organization_id = ? AND " +
                            "event_id = ? AND " +
                            "state <> 'cancelled' AND " +
                            "person_id = ?");

                pstmt2.setInt(1, organization_id);
                pstmt2.setInt(2, rs.getInt("id"));
                pstmt2.setInt(3, person_id);

                rs2 = pstmt2.executeQuery();

                if ( rs2.next() ) {

                    is_signed_up = true;
                    state = rs2.getString("state");
                    reservation_id = rs2.getInt("id");

                } else {

                    is_signed_up = false;
                    state = "";
                }

                //
                // output each event
                out.println("<tr valign=\"top\">");
                out.println("<td><a " + ((event_id == rs.getInt("id") ? "id=defaultEvent " : "")) + "href=\"#\" data-ftlink=\"Dining_home?nowrap&event_popup&event_id=" + rs.getInt("id") + "\" class=\"dining_event_modal\">" + rs.getString("name") + "</a></td>"); //  toptions=\"resizable = 1\"  onclick=\"showEvent('" + rs.getInt("id") + "')\"
                out.println("<td>" + Utilities.getDateFromYYYYMMDD(rs.getInt("our_date"), 2) + "</td>");
                out.println("<td>" + Utilities.getSimpleTime(rs.getInt("stime")) + " to " + Utilities.getSimpleTime(rs.getInt("etime")) + "</td>");
                out.println("<td>" + rs.getString("location_name") + "</td>");
                if (is_signed_up) {

                    //we are signed up for this event (we have an active non-cancelled reservation)

                    if (!state.equals("registered")) {
                        out.println("<td>" + Utilities.titleCase(state) + "</td>");
                    } else {
                        out.println("<td><a href=\"Dining_slot?event&action=edit&orig=calendar&reservation_id=" + reservation_id + "&event_id=" + rs.getInt("id") + "&reservation[category]=event&date=" + rs.getInt("our_date") + "&reservation[reservation_time]=" + rs.getString("time1") + "&reservation[covers]=" + rs.getInt("maximum_party_size") + "\">" + Utilities.titleCase(state) + "</a></td>");
                    }

                } else if (can_sign_up) { //  || Common_Server.SERVER_ID == 4

                    // we are NOT signed and event does allow online signups

                    if (in_signup_period) {

                        if (parmDining.isEventOpen(organization_id, rs.getInt("id"), con, con_d)) {

                            //if (!club.equals("demoroger")) {
                                
                                out.println("<td><a href=\"Dining_slot?event&action=new&orig=calendar&event_id=" + rs.getInt("id") + "&reservation[category]=event&date=" + rs.getInt("our_date") + "&reservation[reservation_time]=" + rs.getString("time1") + "&reservation[covers]=" + rs.getInt("maximum_party_size") + "\">Register Now</a></td>");  // reservation[date]=" + rs.getString("date1") + "&
                            //}

                        } else {
                            out.println("<td>Please call the club</td>");
                        }

                    } else {
                        // reservation period has either not begun or has ended
                        if (signup_begin_date > today) {
                            out.println("<td>Registration<br>Opens " + Utilities.getDateFromYYYYMMDD(signup_begin_date, 2) + "</td>");
                        } else {
                            out.println("<td>Registration<br>Ended " + Utilities.getDateFromYYYYMMDD(signup_end_date, 2) + "</td>");
                        }
                    }

                } else {

                    // we are NOT signed and event does NOT allow online signups

                    out.println("<td>" + no_online_text + "</td>");

                }
                out.println("<td>");

                for (int i = 0; i < parmCosts.costs_found; i++) {

                    out.println(parmCosts.price_categoryA[i] + ": $" + parmCosts.costA[i] + "<br>");
                }

                out.println("</td>");
                
                if (club.equals("tontoverde")) {
                    out.println("<td align=\"right\">Please call the club&nbsp;&nbsp;</td>");
                } else {
                    out.println("<td align=\"right\">" + Utilities.getDateFromYYYYMMDD(signup_begin_date, 2) + " - " + Utilities.getDateFromYYYYMMDD(signup_end_date, 2) + "&nbsp;&nbsp;</td>");
                  //out.println("<td>" + Utilities.getDateFromYYYYMMDD(signup_end_date, 2) + "</td>");
                }
                out.println("</tr>");

            } // end hide this row




        }

    } catch (Exception exc) {

        Utilities.logError("Dining_home.viewEventListing: Err=" + exc.toString());

    } finally {

        try { rs2.close(); }
        catch (Exception ignore) {}

        try { pstmt2.close(); }
        catch (Exception ignore) {}

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    out.println("</tbody></table>");
    out.println("</div>");

    out.println("</div>");

    out.println("<div id=\"tt2_left\" align=\"center\"><input type=\"button\" value=\"Home\" id=\"back\" onclick=\"window.location.href='Member_announce'\" /></div>");

    out.println("<div class=\"clearFix\"></div>"); // clear the float

 }


 private void viewCalendar(int organization_id, String club, String user, PrintWriter out, Connection con, Connection con_d) {


     
    // NOTICE:  WE NOW USE Member_teelist for this !!!!!!!!!!!!!!!!!!!!!!
    
    
    /*
    PreparedStatement pstmt = null;
    PreparedStatement pstmt2 = null;
    ResultSet rs = null;
    ResultSet rs2 = null;

    int [] diningE = new int [32];          // dining events reservation
    int [] diningA = new int [32];          // dining ala carte reservations
    int months = 4;                         // default months to display
    int person_id = Utilities.getPersonId(user, con);
    int todayDate = (int)Utilities.getDate(con);
    int year = (int)todayDate / 10000;
    int month = ((int)todayDate - (year * 10000)) / 100;
    int today = ((int)todayDate - (year * 10000)) - (month * 100);
    int day = 0;
    int col = 0;
    int day_num = 0;
    int date = 0;
    int sdate = 0;
    int edate = 0;

    
    // need to find the minimum & maximum days for reservations in available locations
    int min = 0;
    int max = 30;

    boolean didone = false;

    int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    //
    //  Num of days in Feb indexed by year starting with 2000 - 2040
    //
    int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                         28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

    String [] mm_table = { "inv", "January", "February", "March", "April", "May", "June", "July", "August",
                           "September", "October", "November", "December" };

    String [] day_table = { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" };

    String height_spacer = "<br><br><br><br>";



    out.println("<div id=\"breadcrumb\"><a href=\"Dining_home\">Home</a> / Dining Calendar</div>");

    out.println("<div id=\"tt1_left\">");
    out.println("<p><strong>Dining Calendar</strong></p></div>");

    boolean legendDone = false;

    //
    // loop to display # of calendars
    for (int i2 = 0; i2 < months; i2++) {

      int numDays = numDays_table[month];               // number of days in month

      if (numDays == 0) {                           // if Feb

         int leapYear = year - 2000;
         numDays = feb_table[leapYear];             // get days in Feb
      }

      //
      //  Adjust values to start at the beginning of the month
      //
      Calendar cal = new GregorianCalendar();
      cal.set(Calendar.YEAR, year);                 // set year in case it changed below
      cal.set(Calendar.MONTH, month-1);             // set the current month value
      cal.set(Calendar.DAY_OF_MONTH, 1);            // start with the 1st
      day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)
      day = 1;
      col = 0;

      //
      //  Locate all the Events for this member & month and set the array indicators for each day
      //
      sdate = (year * 10000) + (month * 100) + 0;       // start of the month (for searches)
      edate = (year * 10000) + (month * 100) + 32;      // end of the month

      //
      //  init the indicator arrays to start new month
      //
      for (int i = 0; i < 32; i++) {
         diningE[i] = 0;
         diningA[i] = 0;
      }

      //
      //  Get all dining events for this month
      //
      try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT EXTRACT(DAY FROM date) " +
                "FROM events " +
                "WHERE organization_id = ? AND " +
                    "to_char(date, 'YYYYMMDD')::int > ? AND " +
                    "to_char(date, 'YYYYMMDD')::int < ?");

        pstmt.setInt(1, organization_id);
        pstmt.setLong(2, sdate);
        pstmt.setLong(3, edate);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            diningE[rs.getInt(1)] = 1;       // set indicator for this day (dining event exists)

        }

      } catch (Exception e1) {

          Utilities.logError("Dining_home.viewCalendar: Error getting dining events for " +club+ ", Error: " +e1.getMessage());
      }


      //
      //  Get all dining reservations for this month and member
      //
      try {

        pstmt = con_d.prepareStatement ("" +
            "SELECT EXTRACT(DAY FROM date) " +
            "FROM reservations " +
            "WHERE " +
                "category = 'dining' AND " +
                "state <> 'cancelled' AND " +
                "organization_id = ? AND " +
                "person_id = ? AND " +
                "to_char(date, 'YYYYMMDD')::int > ? AND " +
                "to_char(date, 'YYYYMMDD')::int < ?");

        pstmt.setInt(1, organization_id);
        pstmt.setInt(2, person_id);
        pstmt.setLong(3, sdate);
        pstmt.setLong(4, edate);

        rs = pstmt.executeQuery();

        while ( rs.next() ) {

            diningA[rs.getInt(1)] = 1;       // set indicator for this day (dining reservation exists)

        }

      } catch (Exception e1) {

          Utilities.logError("Dining_home.viewCalendar: Error getting dining reservations for " +club+ ", Error: " +e1.getMessage());
      }


      //
      //  table for the month
      //
      out.println("<div id=\"lg_calendar\">");

      if (!legendDone) {

          out.println("<table cellspacing=\"0\" width=\"50%\">");
          out.println("<tr><td align=\"center\" class=\"month\">Legend</td></tr>");
          out.println("<tr><td><span class=\"diningLinkA\">Dining reservations you make appear in dark blue.</span></td></tr>");
          out.println("<tr><td><span class=\"diningLinkB\">Dining reservations made by others that you are part of will appear in black.</span></td></tr>");
          out.println("<tr><td><span class=\"diningEventLink\">Dining events will appear in a maroon color.</span></td></tr>");
          out.println("</table>");
          
          legendDone = true;
      }

      out.println("<table cellspacing=\"0\">");
      out.println("<tr><td colspan=\"7\" align=\"center\" class=\"month\">");
         out.println(mm_table[month] + "&nbsp;&nbsp;" + year);
      out.println("</td></tr><tr class=\"grey\">");
         out.println("<td align=\"center\">Sun</td>");
         out.println("<td align=\"center\">Mon</td>");
         out.println("<td align=\"center\">Tue</td>");
         out.println("<td align=\"center\">Wed</td>");
         out.println("<td align=\"center\">Thu</td>");
         out.println("<td align=\"center\">Fri</td>");
         out.println("<td align=\"center\">Sat</td>");
      out.println("</tr>");
      out.println("<tr>");        // first row of days

      for (int i = 1; i < day_num; i++) {    // skip to the first day
         out.println("<td class=\"old\">&nbsp;" + height_spacer + "<br></td>");
         col++;
      }

      while (day < today) {
         //out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">" + day + "</font>");  // put in day of month
         out.println("<td class=\"old1\"><span class=day>" + day + "" + height_spacer + "</span></td>");  // put in day of month
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr><tr>");
         }
      }

      //
      // start with today, or 1st day of month, and go to end of month
      //
      while (day <= numDays) {

         //
         //  create a date field for queries
         //
         date = (year * 10000) + (month * 100) + day;      // create a date field of yyyymmdd for this day

         //String mysql_date = year + "-" + SystemUtils.ensureDoubleDigit(month) + "-" + SystemUtils.ensureDoubleDigit(day);

         didone = false;        // init 'did one' flag

         if (col == 0) {      // if new row

            out.println("<tr>");
         }

         //out.println("<td align=\"left\" valign=\"top\"><font size=\"1\">");   // day of month
         out.print("<td " + ((date == todayDate) ? "class=\"today\"" : "") + ">");  // day of month

         if (max > 0) {            // if member could book a reservation on this day

            //
            //  Add link to page that they can use to make a reservation and see who is already coming
            //
            out.print("<a href=\"Dining_slot?action=new&orig=calendar&date=" +date+ "\">");

            out.println(day + "</a>");

            max--;

         } else {

            out.println("<span class=day>" + day + "</span>");         // just put in day of month
         }
         //out.println("<br>");


         //
         // if we had found DINING EVENT on this day then dig deeper
         //
         if (diningE[day] != 0) {

            try {

                String state = "";

                pstmt = con_d.prepareStatement ("" +
                       "SELECT id, name, members_can_make_reservations, to_char(start_time, 'HH24MI') AS stime, to_char(end_time, 'HH24MI') AS etime " +
                       "FROM events " +
                       "WHERE organization_id = ? AND to_char(date, 'YYYYMMDD')::int = ?");

               pstmt.clearParameters();
               pstmt.setInt(1, organization_id);
               pstmt.setLong(2, date);

               rs = pstmt.executeQuery();

               while (rs.next()) {

                    state = ""; // reset

                    // lookup this member in the reservations table to see if they are already signed up for this dining event
                    // just return the most recent reservation that matches in case they cancelled and signed back up or something
                    pstmt2 = con_d.prepareStatement ("" +
                            "SELECT state " +
                            "FROM reservations " +
                            "WHERE " +
                                "category = 'event' AND " +
                                "organization_id = ? AND " +
                                "event_id = ? AND " +
                                "state <> 'cancelled' AND " +
                                "person_id = ? " +
                            "ORDER BY updated_at ASC " +
                            "LIMIT 1");

                    pstmt2.setInt(1, organization_id);
                    pstmt2.setInt(2, rs.getInt("id"));
                    pstmt2.setInt(3, person_id);

                    rs2 = pstmt2.executeQuery();

                    if ( rs2.next() ) {

                        state = rs2.getString("state");
                    }

                    String mNum = "";

                    if (user != null && !user.equals("")) {

                        mNum = Utilities.getmNum(user, con);      // get this mem's member number
                    }

                    int dtime = Integer.parseInt(rs.getString("stime"));

                    // if member can signup for the dining event then make it a link
                    // show their reservation status if already signed up
                    // otherwise just display the name of the event
                    if ( rs.getBoolean("members_can_make_reservations") ) {

                        // only make it a link if they are able to sign up
                        out.print("<a href=\"Dining_home?event&event_id=" + rs.getInt("id") + "\">"); // + "&organization_id=" + organization_id + "\">");
                    }

                    out.print("<span class=\"diningEventLink\">" + rs.getString("name") + " at " + Utilities.getSimpleTime(dtime));
                    if (!state.equals("")) out.println("<br>(" + state + ")");
                    out.print("</span>");

                    if ( rs.getBoolean("members_can_make_reservations") ) {

                        out.println("</a>");
                    }


             //       } else {

                        // display the event name
             //           out.print("<span class=\"diningLink\">" + rs.getString("name") + " at " + Utilities.getSimpleTime(dtime));
             //           if (!state.equals("")) out.println("<br>(" + state + ")");
             //           out.println("</span>");

             //       }

                    didone = true;

                } // end while loop of events

                pstmt.close();

            } catch (Exception e1) {

                Utilities.logError("Dining_home.viewCalendar(): Error getting specific dining event info for " +club+ ", Error: " +e1.getMessage());
            }

        } // end IF ala carte reservation found for this day


        //
        // if we had found ALA CARTE on this day then dig deeper
        //
        if (diningA[day] != 0) {

            try {

                int dtime = 0;
                //int id = 0;
                //int parent_id = 0;
                boolean member_created = false;

                pstmt = con_d.prepareStatement ("" +
                            "SELECT id, parent_id, member_created, to_char(time, 'HH24MI') AS stime " +
                            "FROM reservations " +
                            "WHERE " +
                                "category = 'dining' AND " +
                                "state <> 'cancelled' AND " +
                                "organization_id = ? AND " +
                                "person_id = ? AND " +
                                "to_char(date, 'YYYYMMDD')::int = ? " +
                            "ORDER BY time ASC");

               pstmt.clearParameters();
               pstmt.setInt(1, organization_id);
               pstmt.setInt(2, person_id);
               pstmt.setLong(3, date);

               rs = pstmt.executeQuery();

               while (rs.next()) {

                   didone = true;
                   dtime = Integer.parseInt(rs.getString("stime"));
                   member_created = rs.getBoolean("member_created");
                   //parent_id = rs.getInt("parent_id");
                   //id = rs.getInt("id");
                   //if (parent_id > 0) id = parent_id;
                   out.print("<a href=\"Dining_slot?action=edit&orig=calendar&reservation_id=" + rs.getInt("id") + "\">"); // &organization_id=" + organization_id + "
                   out.print("<span class=\"diningLink" + ((member_created) ? "A" : "B") + "\">Reservation at <nobr>" + Utilities.getSimpleTime(dtime) + "</nobr></span>");
                   out.println("</a>");
               }

                pstmt.close();

            } catch (Exception e1) {

                Utilities.logError("Dining_home.viewCalendar() Error getting specific dining reservation info for " +club+ ", Error: " +e1.getMessage());
            }

        }


         //
         //**********************************************************
         //  End of display for this day - get next day
         //**********************************************************
         //
         if (didone == true) {          // if we added something to the day
            out.println("</td>");       // end of column (day)
         } else {
            out.println(height_spacer + "</td>"); //
         }
         col++;
         day++;

         if (col == 7) {
            col = 0;                             // start new week
            out.println("</tr>");
         }

      } // end if while loop for days in month

      // finish off the week if nessesary
      if (col != 0) {      // if not at the start

         while (col != 0 && col < 7) {      // finish off this row if not at the end

            out.println("<td class=old>&nbsp;" + height_spacer + "</td>");
            col++;
         }
         out.println("</tr>");
      }

      //
      // end of calendar row
      //
      out.println("</table>");
      out.println("</div>");
      out.println("<br>");

      today = 1;       // ready for next month
      month++;

      if (month > 12) {     // if end of year
         year++;
         month = 1;
      }

    } // end loop of months
    * 
    */

 }


 @SuppressWarnings("deprecation")
 private void announcementPage(String club, HttpServletRequest req, PrintWriter out) {


   //
   //  Output the Dining Announcement Page
   //
   File f;
   FileReader fr;
   BufferedReader br = null;

   String path = "";

   //Common_skin.outputBreadCrumb(club, dining_activity_id, out, "");  // do not include breadcrumb on Home page (not needed)
   
   HttpSession session = SystemUtils.verifyMem(req, out);
   String caller = (String)session.getAttribute("caller");

   if (!caller.equals("FLEXWEBFT")) Common_skin.outputLogo(club, dining_activity_id, out, req);

   out.println("<div class=\"preContentFix\"></div>"); // clear the float

   out.println("<div class=\"announcement_container\">");

   try {
       
       String tmp = "";
       path = req.getRealPath("") + "/announce/" +club+ "/" +club+ "_announce_dining.htm";
       f = new File(path);
       fr = new FileReader(f);
       br = new BufferedReader(fr);
       
       if (f.isFile()) {
   
           try {

              while( (tmp = br.readLine()) != null )
                  out.println(tmp);

           } catch (Exception ignore) {}
       
       }
   
   } catch (FileNotFoundException e) {
   
       out.println("<p>&nbsp;</p><p align=center><i>Missing Announcement Page.</i></p>");
       out.println("<!-- path=" + path + " -->");
   
   } catch (SecurityException se) {
     
       out.println("<p>&nbsp;</p><p align=center><i>Access Denied.</i></p>");
   
   } finally {
       
       try { br.close(); }
       catch (Exception ignore) {}
   
   }

   out.println("<div class=\"clearfloat\"></div>");
   out.println("</div><!-- closing announcement_container -->");
   
 }

}
