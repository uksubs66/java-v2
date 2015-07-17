/***************************************************************************************
 *   Dining_slot:  This servlet will display the dining reservation form and process it
 *
 *
 *   called by:
 *
 *   created: 05/13/11
 *
 *
 *   last updated:
 *
 *      1/07/14  Add custom message for ccyork
 *      8/16/13  Change default redirection from calendar to announce when completing a reservation
 *      5/07/13  Add custom message for minikahda
 *      4/23/13  Add custom message for charlottecc
 *      4/19/13  Minikahda - if today and past 5pm then block access to today
 *      3/07/13  Added denvercc, peninsula and hyperion to the email verification
 *      3/04/13  Mirabel - Added email verification support
 *      2/18/13  Added custom messages to main reservation screen for miribel and thelegendclubs
 *      1/29/13  Updated the cancel reservation processing
 *      1/27/13  Fixed who can edit & cancel vs. remove themselves issue
 *      1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *     12/29/12  Fixed canNonMasterReserveeEdit check
 *     12/01/12  Add conflicting reservation checking to all reservations
 *     10/31/12  Add a cover count check when submitting form2 - warn user if requested covers <> actual covers
 *     10/29/12  Add additional sanity to form elements that are passed to the dining server
 *      9/21/12  Fix the Go Back return for events (was failing, now returns to event list page in Dining_home).
 *      9/18/12  Move the dining logo into the white portion of the page so we don't have to change colors when the background changes.
 *      9/06/12  Updated outputTopNav calls to also pass the HttpServletRequest object.
 *      7/13/12  Added a notice if user selects a date from the reservation page that has an event scheduled on it
 *      7/13/12  Added new button on the reservation page for viewing reservations for today
 *      6/12/12  Custom for misquamicut - hide the check number and special occasion select boxes
 *      6/20/12  Check for missing time and location settings and reject if missing.
 *      4/27/12  Minor text changes on slot page.
 *      9/02/11  Allow for external login from Login.processExtLogin to allow member to register for an event (link in email).
 *      8/25/11  Get the name of the club for the banner.
 *               If not an event, check the max online party size when building the Add Another link.
 *               Change the Go Back links to not use the HTML back and instead link back as appropriate.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import javax.mail.internet.*;
import javax.mail.*;

import org.apache.commons.lang.*;
//import com.google.gson.*;

// foretees imports
import com.foretees.common.DaysAdv;
import com.foretees.common.parmDining;
import com.foretees.common.parmDiningCosts;
import com.foretees.common.parmDiningSeatings;
import com.foretees.common.Connect;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.nameLists;


public class Dining_slot extends HttpServlet {


 static String rev = ProcessConstants.REV;                               // Software Revision Level (Version)
 static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System

 //
 //**********************************************************
 //
 // Process the dining slot pages
 //
 //**********************************************************
 //


 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     /*
     // Jump to new Dining_slot2 if we're on a dev server
     if (ProcessConstants.SERVER_ID == 4) {
            Dining_slot2 slot = new Dining_slot2();
            slot.doPost(req, resp);
            return;
    }
      * 
      */

    if (req.getParameter("ev") != null && req.getParameter("ev").equals("1")) {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder

        if (session == null) return;

        // send verification email and terminate
        String club = (String)session.getAttribute("club");
        String msg = req.getParameter("msg");
        String subject = req.getParameter("subject");
        String to = "";

        
        //
        // ADD CLUB SPECIFIC EMAIL ADDRESES HERE (don't forget to add club to useEmailVerification in verify_form() below)
        //
        if (club.equals("mirabel")) {

            to = "reservations@mirabel.com";

        } else if (club.equals("denvercc")) {

            to = "diningreservations@denvercc.net";

        } else if (club.equals("peninsula")) {

            to = "james.cunningham@thepeninsulaclub.com";

        } else if (club.equals("hyperion")) {

            to = "emeyer@hyperionfc.com";

        } else if (club.equals("ccyork")) {
            
            to = "reservations@ccyork.org";
            
        }


        if (!to.equals("") && !subject.equals("")) {
           
            Properties properties = new Properties();
            properties.put("mail.smtp.host", SystemUtils.host);                     // set outbound host address
            properties.put("mail.smtp.port", SystemUtils.port);                     // set outbound port
            properties.put("mail.smtp.auth", "true");                               // set 'use authentication'

            Session mailSess = Session.getInstance(properties, SystemUtils.getAuthenticator());   // get session properties

            MimeMessage message = new MimeMessage(mailSess);

            try {

                message.setFrom(new InternetAddress(SystemUtils.EFROM));                  // set from addr
                message.setSubject(subject);                                              // set subject line
                message.setSentDate(new java.util.Date());                                // set date/time sent
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));  // add recipient
                message.setText(msg);                                                     // put msg in email text area
                
                Transport.send(message);                                                  // send it!!
            
            } catch (Exception exc) {

                Utilities.logError("Dining_slot: Error sending custom email verification. " + exc.getMessage() + ", " + exc.toString());

            }

        }
        
    } else {

        doGet(req, resp);

    }

 }



 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
     /*
     // Jump to new Dining_slot2 if we're demov4 and on a dev server
     if (ProcessConstants.SERVER_ID == 4) {
            Dining_slot2 slot = new Dining_slot2();
            slot.doGet(req, resp);
            return;
    }
*/

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = null;

    boolean ext_login = false;            // not external login (from email link)
   
    if (req.getParameter("ext_login") != null) {   // if originated from Login for an external login user

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
            out.println("<BR><BR><a href=\"Logout\" target=\"_top\">Exit</a><BR><BR>");
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

    // make sure we are in dining mode now (shouldn't need this here but for dev it helps)
    session.setAttribute("activity_id", ProcessConstants.DINING_ACTIVITY_ID);

    String user = "";
   
    if (ext_login == false) {
       user = (String)session.getAttribute("user");              // get username
    } else {
       user = (String)session.getAttribute("ext-user");              // get username when external (done so verifymem will fail)
    }
    String club = (String)session.getAttribute("club");                 // get club name
    int activity_id = (Integer)session.getAttribute("activity_id");     //   and activity id for user
    int organization_id = Utilities.getOrganizationId(con);

    //boolean event = (req.getParameter("event") != null);

    int reservation_id = 0;
    int event_id = 0;
    //int date = 0;

    try { reservation_id = Integer.parseInt(req.getParameter("reservation_id")); }
    catch (Exception ignore) { }

    try { event_id = Integer.parseInt(req.getParameter("event_id")); }
    catch (Exception ignore) { }

    boolean event = ((req.getParameter("event") != null) || event_id > 0);

/*
    try { date = Integer.parseInt(req.getParameter("date")); }
    catch (Exception ignore) {
        date = (int)Utilities.getDate(con);
    }
*/
    activity_id = dining_activity_id;

    String title = "ForeTees Dining Home";

    String clubName = "ForeTees Country Club";     // in case of an error below
   
    try {
      
      clubName = Utilities.getClubName(con);        // get the full name of this club

    } catch (Exception exc) {
    }


    //
    //   Start with the common html
    //
    Common_skin.outputHeader(club, activity_id, title, true, out, req);

    Common_skin.outputBody(club, activity_id, out, req);

    if (ext_login == false) Common_skin.outputTopNav(req, club, activity_id, out, con);

    Common_skin.outputBanner(club, activity_id, clubName, "", out, req);

    if (ext_login == false) {
        
        Common_skin.outputSubNav(club, activity_id, out, con, req);
           
    } else {
           
       out.println("<p>&nbsp;</p><p>&nbsp;</p>");   // blank line to make room for the dining logo
    }
    
    Common_skin.outputPageStart(club, activity_id, out, req);

    String action = (req.getParameter("action") != null) ? req.getParameter("action") : "";

    out.println("<!-- *************** BEGIN BODY *************** -->");
    out.println("<!-- " + organization_id + " " + club + " " + user + " -->");
    out.println("<!-- reservation_id=" + reservation_id + ", event_id=" + event_id + ", event_mode=" + event + " -->");

    //out.println("<!-- reservation[location_id]=" + req.getParameter("reservation[location_id]") + " -->");

    //  TEMP to capture all the input parms and values
    /*
      Enumeration enum1 = req.getParameterNames();
      out.println("<!-- Query String: " + req.getQueryString() + " -->");
      while (enum1.hasMoreElements()) {
         String name = (String) enum1.nextElement();
         String values[] = req.getParameterValues(name);
         if (values != null) {
            for (int i=0; i<values.length; i++) {
               out.println("<!-- " +name+ " (" +i+ "): " +values[i]+ " -->");
            }
         }
      }
      */
     //  end of temp 
    
    
    
    //
    //  Check if user submitted a reservation request
    //
    if (req.getParameter("commit_sub") != null && req.getParameter("commit_sub").equals("Reserve") && 
       (req.getParameter("force_form2") == null || req.getParameter("force_form2").equals("0"))) {

        verify_form(reservation_id, event_id, user, club, req, out, con, con_d);
        //return;

    } else if (action.equals("verify-UNUSED")) {

        // verify form
        // kick back if error
        // display confirmation if all good and allow user to post form to diningRoR
        // via script on confirmation
        //verify_form(reservation_id, user, req, out, con, con_d);

    } else if (!event && action.equals("new")) { //  && date != 0

        output_form(reservation_id, user, club, req, out, con, con_d);

    } else if (!event && action.equals("edit") && reservation_id != 0) {

        output_form(reservation_id, user, club, req, out, con, con_d);

    } else if (!event && action.equals("cancel") && reservation_id != 0) {

        cancel_reservation(reservation_id, user, req, out, con, con_d);

    } else if (event && action.equals("new") && event_id != 0 && reservation_id == 0) {

        output_form2(reservation_id, event_id, user, club, req, out, con, con_d);    // new event registration request

    } else if (event && action.equals("edit") && event_id != 0 && reservation_id != 0) {

        output_form2(reservation_id, event_id, user, club, req, out, con, con_d);

    } else {

        out.println("Invalid request...");

    }

    out.println("<!-- *************** END OF BODY *************** -->");

    //  end the page
    Common_skin.outputPageEnd(club, activity_id, out, req);

    out.close();

    try { con_d.close(); }
    catch (Exception ignore) {}

 }


 //private boolean checkForAnswers(int id, int event_id, String user, String club, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {
 // returns true if answers ok, false if missing answers
 private boolean missingAnswers(parmDining parmD, String user, HttpServletRequest req, PrintWriter out, Connection con_d) {

    // if answers are null then questions have not been asked
    // if answers are not null and any required answers are missing then redisplay questions
    // if answers are not null and the required answers are all present then return true


    boolean missing = false;

    if (req.getParameter("QAFLAG") != null) { //  related_reservations[1]answers[0][question_id]

        out.println("<!-- QUESTIONS HAVE BEEN ASKED - CHECKING ANSWERS -->");

        // at least one answer present
        for (int i = 1; i < parmD.covers; i++) {

            out.println("<!-- CHECKING PERSON " + i + " -->");

            for (int q = 1; q < parmD.question_count; q++) {

                out.println("<!-- CHECKING QUESTION " + q + " -->");

                if (parmD.question_for_whole_party[q] == 1) {

                    out.println("<!-- QUESTION IS FOR WHOLE PARTY -->");

                    // if question is for party then answer will be with the first cover

                    if (parmD.question_guest_only[q] == 0 || (parmD.question_guest_only[q] == 1 && parmD.has_guests)) {

                        if (parmD.answers[1][q].equals("")) {  // was [i][0]

                            if (parmD.question_requires_answer[q] == 1) {

                                missing = true;
                                out.println("<!-- MISSING REQUIRED ANSWER -->");

                            } else {

                                out.println("<!-- NO ANSWER GIVEN, NONE REQUIRED -->");

                            }
                        }

                    }

                } else {

                    out.println("<!-- QUESTION IS FOR INDIVIDUAL PERSON -->");

                    if (parmD.question_guest_only[q] == 0 || (parmD.question_guest_only[q] == 1 && parmD.dining_id[i] == 0)) {

                        if (parmD.answers[i][q].equals("")) {

                            if (parmD.question_requires_answer[q] == 1) {

                                missing = true;
                                out.println("<!-- MISSING REQUIRED ANSWER -->");

                            } else {

                                out.println("<!-- NO ANSWER GIVEN, NONE REQUIRED -->");

                            }

                        }

                    }

                }

                /*
                // if question is either not for guests only or for guests and there is a guest present
                if ( (parmD.question_guest_only[q] == 0 || (parmD.question_guest_only[q] == 1 && parmD.dining_id[i] == 0)) &&
                     ( (parmD.question_for_whole_party[q] == 1 && parmD.answers[i][0].equals("")) ||
                       (parmD.question_for_whole_party[q] == 0 && parmD.answers[i][q].equals(""))
                     ) &&
                    parmD.question_requires_answer[q] == 1) {

                    // answer is missing
                    missing = true;
                    break;

                }
                */

            } // loop over questions

        } // loop over names

    } else {

        out.println("<!-- QUESTIONS HAVE NOT BEEN ASKED YET -->");

        // no answers found (answer #1 was null meaning they have not been asked yet)
        // check the question count and if zero then this is ok so return true
        if (parmD.question_count == 0) {
            missing = false;
        } else {
            missing = true;
        }

    }

    return missing;

 }


 //private void displayQuestions(int id, int event_id, String user, String club, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {
 private void displayQuestions(parmDining parmD, String user, String club, HttpServletRequest req, PrintWriter out, int person_id, Connection con_d) {


    boolean event = (parmD.event_id != 0);

    boolean ext_login = false;            // not external login (from email link)
   
    if (req.getParameter("ext_login") != null) ext_login = true;   // if from Login for an external login user

/*
    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);

    parmDining parmD = new parmDining();

    // populate parmD from the request object
    parmD.id = id;
    parmD.event_id = event_id;

    if (event && req.getParameter("reservation[location_id]") == null) {
        out.println("<!-- LOADING RES DATA FROM DB -->");
        parmD.populate(parmD, con_d);
    } else {
        out.println("<!-- LOADING RES DATA FROM REQ -->");
        parmD.populate(parmD, req, con_d);
    }
*/


    out.println("<!-- meal_period_id=" + parmDining.getMealPeriodId(parmD, con_d) + " -->");
    out.println("<!-- question_count=" + parmD.question_count + " -->");

    // if here to modify an existing reservation then load any answers already saved in the db
    if (parmD.id > 0) {

        parmDining.loadAnswers(parmD, con_d);

    } else {
        // the answers should be in the req object and therefore already added to the parmD block
    }







    String reservation = (event) ? "Event Reservation" : "Reservation";
    String title = (parmD.id > 0) ? "Modify Existing " + reservation : "Create New " + reservation;

    //
    //  Begin the html output
    //
    out.println("<div class=\"reservations\">");

    //
    // output bread crumb trail
    //
    Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Dining " + reservation, req);
    Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
    //out.println("  <BR><BR>");
    /*
    out.println("<div id=\"breadcrumb\">Home / Dining " + reservation + " / Questions</div>");
    * 
    */

    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div id=\"reservation_action_title\">" + title + "</div>");


    //
    // output a warning message if modifying
    //
    if (parmD.id != 0) out.println("<div class=\"main_warning\">" +
            "<strong>Warning</strong>:  " +
            "You have <strong>6 minutes</strong> to complete this reservation. " +
            "If you want to return without completing a reservation, <strong>do not use your browser's BACK</strong> button/option. " +
            "Instead select the <strong>Go Back</strong> option below.</div>");


    //
    // output the instructions
    //
    //out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>:  This is where we can tell the user how to complete the reservation.</div>");
    

    out.print("<div id=\"tt1_left\">");
    out.print("Date:  <strong>" + parmD.day_of_week + " " + parmD.sdate + "</strong> &nbsp;&nbsp;&nbsp;<strong>" + parmD.location_name + "</strong> &nbsp;&nbsp;&nbsp;Time: <strong>" + Utilities.getSimpleTime(parmD.time) + "</strong>");
    out.println("</div>");


    out.println("<div class=sub_main>");

    //
    // start dining form
    //
    out.println("<form action=\"Dining_slot\" id=\"dining_reservation_form\" name=\"dining_reservation_form\" method=\"post\">");

    out.println("<input id=\"QAFLAG\" name=\"QAFLAG\" type=\"hidden\" value=\"1\">");

    out.println("<input id=\"force_form2\" name=\"force_form2\" type=\"hidden\" value=\"0\">");

    out.println("<input id=\"page2\" name=\"page2\" type=\"hidden\" value=\"1\">");
    out.println("<input id=\"bump_covers\" name=\"bump_covers\" type=\"hidden\" value=\"0\">");
    out.println("<input id=\"action\" name=\"action\" type=\"hidden\" value=\"" + req.getParameter("action") + "\">");

    out.println("<input id=\"date\" name=\"date\" type=\"hidden\" value=\"" + req.getParameter("date") + "\">");
    out.println("<input id=\"foretees_login\" name=\"foretees_login\" type=\"hidden\" value=\"XV3\">");
    out.println("<input id=\"organization_id\" name=\"organization_id\" type=\"hidden\" value=\"" + parmD.organization_id + "\">");
    out.println("<input id=\"username\" name=\"username\" type=\"hidden\" value=\"" + user + "\">");
    out.println("<input id=\"user_id\" name=\"user_id\" type=\"hidden\" value=\"" + Utilities.getUserId(person_id) + "\">");
    out.println("<input id=\"reservation_number\" name=\"reservation_number\" type=\"hidden\" value=\"" + parmD.reservation_number + "\">");
    out.println("<input id=\"reservation_id\" name=\"reservation_id\" type=\"hidden\" value=\"" + parmD.id + "\">");
    out.println("<input id=\"event_id\" name=\"event_id\" type=\"hidden\" value=\"" + parmD.event_id + "\">");
    out.println("<input id=\"reservation_person_id\" name=\"reservation[person_id]\" type=\"hidden\" value=\"" + person_id + "\">");
    out.println("<input id=\"reservation_member_created\" name=\"reservation[member_created]\" type=\"hidden\" value=\"true\">");

    out.println("<input id=\"reservation_organization_id\" name=\"reservation[organization_id]\" type=\"hidden\" value=\"" + parmD.organization_id + "\">");

    // answers from step 1
    out.println("<input name=\"reservation[location_id]\" type=\"hidden\" value=\"" + parmD.location_id + "\">");
    out.println("<input name=\"reservation[reservation_date]\" type=\"hidden\" value=\"" + parmD.sdate + "\">");
    out.println("<input name=\"reservation[reservation_time]\" type=\"hidden\" value=\"" + parmD.time + "\">");
    out.println("<input name=\"reservation[number_of_checks]\" type=\"hidden\" value=\"" + parmD.covers + "\">");
    out.println("<input name=\"reservation[covers]\" type=\"hidden\" value=\"" + parmD.covers + "\">");
    out.println("<input name=\"reservation[occasion_id]\" type=\"hidden\" value=\"" + parmD.occasion_id + "\">");


    // answers from step 2
    for (int i = 1; i <= parmD.names_found; i++) {

        out.println("<input type=\"hidden\" name=\"name_" + i + "\" value=\"" + parmD.names[i].trim() + "\">");
        out.println("<input type=\"hidden\" name=\"data_" + i + "\" value=\"" + parmD.dining_id[i] + "\">");
        out.println("<input type=\"hidden\" name=\"reservation_id_for_person_" + i + "\" value=\"" + parmD.related_id[i] + "\">");
        out.println("<input type=\"hidden\" name=\"related_reservations[" + i + "][covers]\" value=\"1\">");
        out.println("<input type=\"hidden\" name=\"related_reservations[" + i + "][user_identity]\" value=\"" + ((parmD.dining_id[i] != 0) ? parmD.getUserIdentity(parmD.dining_id[i], con_d) : "") + "\">");
        out.println("<input type=\"hidden\" name=\"check_num_for_person_" + i + "\" value=\"" + parmD.check_num[i] + "\">");
        out.println("<input type=\"hidden\" name=\"related_reservations[" + i + "][price_category]\" value=\"" + parmD.price_category[i] + "\">");

    }

    out.println("<input type=\"hidden\" name=\"reservation[member_special_requests]\" value=\"" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) + "\">");
    
    if (ext_login == true) {
       
       out.println("<input type=\"hidden\" name=\"ext_login\" value=\"yes\">");
    }


    boolean did_header = false;

    // now display the questions
    for (int i = 1; i <= parmD.question_count; i++) {

        out.println("<!-- loop 1 processing question #" + i + ", has_guests=" + parmD.has_guests + " -->");

        // only process questions that are for the entire party
        if (parmD.question_for_whole_party[i] == 1) {

            // if question is guest only then only ask question if the dining party contains at least one guest
            if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.has_guests)) {

                if (!did_header) {

                    out.println("<div class=\"res_qa\">");

                    out.println("<strong>Questions for dining party</strong>");
                    did_header = true;
                }

                // this question only needs to be asked once and is for the entire dining party
                // in this case we will assign the answers to the primary reservation holder
                out.println("<table id=\"res_qa_table\">");

                // present the question
                out.println("<tr><td class=\"question\" colspan=\"2\">" + parmD.question_text[i] + "</td>");

                
                //out.println("<td><input type=\"text\" name=\"related_reservations[1]answers[" + i + "]\" value=\"\"></td>");  // " + parmD.answer[i] + "
                out.println("<td>");
                out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                out.println("<input type=\"text\" name=\"answers[" + i + "][answer_text]\" value=\"" + parmD.answers[1][i] + "\">");
                if (parmD.answers[1][i].equals("") && parmD.question_requires_answer[i] == 1) out.println("<span class=\"required\">(required)</span>");
                out.println("</td>");

                out.println("</tr></table>");

            } else { out.println("<!-- QUESTION IS FOR GUESTS BUT NO GUESTS FOUND -->"); }

        }

    }

    if (did_header) out.println("</div>"); // close res_qa div

    out.println("<br>");

    did_header = false;
    
    for (int i = 1; i <= parmD.question_count; i++) {

        out.println("<!-- loop 2 processing question #" + i + ", has_guests=" + parmD.has_guests + " -->");

        // only ask if question is for each individual AND
        // is either not a guest question or is a guest question and there is at least one guest
        if ( parmD.question_for_whole_party[i] == 0 && (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.has_guests)) ) {

            if (!did_header) {

                out.println("<div class=res_qa>");

                out.println("<strong>Questions for each individual</strong>");
                did_header = true;
            }

            out.println("<table id=\"res_qa_table\">");

            // present the question
            out.println("<tr><td class=\"question\" colspan=\"2\">" + parmD.question_text[i] + "</td></tr>");

            // display one answer box for each cover being asked
            for (int i2 = 1; i2 <= parmD.names_found; i2++) {

                out.println("<tr>");

                // if question is for guests only then only ask if this person is not a member
                if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.dining_id[i2] == 0)) {

                    out.println("<td class=\"names\" nowrap>" + parmD.names[i2] + "</td>");
                    //out.println("<td><input type=\"text\" name=\"related_reservations[" + i2 + "]answers[" + i + "]\" value=\"\">");  // " + parmD.answer[i] + "
                    out.println("<td>");
                    if(i2 == 1) {
                        // primary person so don't use related_reservation prefix and exclude the reservation id
                        out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                        out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                        out.println("<input type=\"text\" name=\"answers[" + i + "][answer_text]\" value=\"" + parmD.answers[i2][i] + "\">");
                        if (parmD.answers[i2][i].equals("") && parmD.question_requires_answer[i] == 1) out.println("<span class=\"required\">(required)</span>");
                    } else {
                        out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                        out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][reservation_id]\" value=\"" + parmD.id + "\">");
                        out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                        out.println("<input type=\"text\" name=\"related_reservations[" + i2 + "]answers[" + i + "][answer_text]\" value=\"" + parmD.answers[i2][i] + "\">");
                        if (parmD.answers[i2][i].equals("") && parmD.question_requires_answer[i] == 1) out.println("<span class=\"required\">(required)</span>");
                    }
                    out.println("</td>");

                }

                out.println("</tr>");

            } // end covers loop
            
            out.println("</table>");

        } // end if we are asking this question

    } // end question loop

    if (did_header) out.println("</div>"); // close res_qa div


    // display the go back and submit buttons
    out.println("<div style=\"text-align:center;\">");
    out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false\">");
    out.println("<input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Reserve\">");
    out.println("</div>");
    
    out.println("</form>");
             
    out.println("</div>");
    out.println("</div>"); // close reservations div

    //
    // Scripts
    //
    out.println("<script type=\"text/javascript\">");

    out.println("function goBack() {");
  //out.println("  window.history.back(-1);");
    out.println("$(\"#force_form2\").val(\"1\");");
    out.println("$(\"#dining_reservation_form\").submit();");
  //out.println("$(\"#dining_reservation_form\").attr(\"ACTION\", \"\")");
    out.println("}");
    
     // This function will probably be overriden by foretees-global.js
    out.println("function ftReturnToCallerPage() {");
    out.println("  window.location = 'Dining_home';");
    out.println("}");
    
    out.println("</script>");

 }


 private void output_form(int id, String user, String club, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {


     // don't need this anymore
    boolean event = (req.getParameter("event") != null);

    
    if ((req.getParameter("page2") != null ||
        (req.getParameter("commit_sub") != null && req.getParameter("commit_sub").equals("Continue"))) || event) {

        // could add check here to make sure any requireds are present (location_id, time, covers, etc)
        // if not then don't jump to 2nd page and instead redisplay this page with an error message
        
        output_form2(id, 0, user, club, req, out, con, con_d);
        return;

    }

    
    String orig = "";
    
    if (req.getParameter("orig") != null) {
       
       orig = req.getParameter("orig");    //  Get the origin of this call (from Calendar or from Rservation Link)
    }
    

    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);

    parmDining parmD = new parmDining();

    parmD.id = id;
    parmD.organization_id = organization_id;

    // determin how to populate parmD
    if (parmD.id > 0) {

        // we are here to view/modify an existing reservation from the database
        parmD.populate(parmD, con_d);

    } else {

        // we are here at some stage of completing a new reservation
        // load from the request object
        parmD.populate(parmD, req, con_d);

    }

    parmD.allow_day_of = parmD.anyLocationAllowDayOf(organization_id, con_d);
    out.println("<!-- ** parmD.anyLocationAllowDayOf=" + parmD.allow_day_of + " -->");

    int days_in_advance = SystemUtils.getIndexFromToday(parmD.date, con);

    boolean hide_special_occasions = (club.equals("misquamicut"));

    //
    // ADD CUSTOMS HERE TO BLOCK ACCESS TO TODAY IF PAST CERTAIN TIME
    //
    //out.println("<!-- ** current time: " + Utilities.getTime(con) + " -->");
    if (club.equals("minikahda") && Utilities.getTime(con) >= 1700) {

        //out.println("<!-- ** minikahda after 5pm -->");
        parmD.allow_day_of = false;
        if (days_in_advance == 0) {
            
            // they currently have today selected but it's not too late
            Calendar cal_date = new GregorianCalendar();             // get current date & time (Central Time)

            cal_date.add(Calendar.DAY_OF_MONTH, 1);

            int year = cal_date.get(Calendar.YEAR);
            int month = cal_date.get(Calendar.MONTH) + 1;
            int day = cal_date.get(Calendar.DAY_OF_MONTH);
            
            parmD.day_of_week = ProcessConstants.DAYS_OF_WEEK[cal_date.get(Calendar.DAY_OF_WEEK) - 1];
            parmD.date = (year * 10000) + (month * 100) + day;
            parmD.sdate = Utilities.getDateFromYYYYMMDD(parmD.date, 2);
            days_in_advance = SystemUtils.getIndexFromToday(parmD.date, con);
            
        }
    }

    //
    // DEBUG
    //
    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        out.println("<!-- days_in_advance=" + days_in_advance + " -->");
        out.println("<!-- covers=" + parmD.covers + " -->");
        out.println("<!-- names_found=" + parmD.names_found + " -->");
        out.println("<!-- meal_period_id=" + parmDining.getMealPeriodId(parmD, con_d) + " -->");
        out.println("<!-- question_count=" + parmD.question_count + " -->");
        out.println("<!-- location_id=" + parmD.location_id + " -->");
        out.println("<!-- allow_day_of=" + parmD.allow_day_of + " -->");
        out.println("<!-- date=" + parmD.date + " -->");
        out.println("<!-- sdate=" + parmD.sdate + " -->");
        out.println("<!-- reservation_number=" + parmD.reservation_number + " -->");
    }


    //ArrayList<String[]> locations = Utilities.getDiningLocations(organization_id, days_in_advance, con_d);
    ArrayList<String[]> locations = parmD.getDiningLocations(organization_id, days_in_advance, parmD.location_id, con_d);

    // TODO: if editing an existing reservation then it's possible the reserved location may no longer be available due to the locations
    // 'days in advance to stop taking rez' has passed this is even more likely if editing a rez for 'today'
    // so we should ensure the already reserved location is always in the list.

    String reservation = (event) ? "Event Reservation" : "Reservation";
    String title = (id > 0) ? "Modify Existing " + reservation : "Create New " + reservation;

    // calendar includes
    //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/calv40-scripts.js\"></script>");
    //out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/calv40-styles.css\" type=\"text/css\">");

    // output calendar
    out.println("<div id=\"cal_elem_0\" style=\"display:none; position: absolute; top: 0px; left: 0px; width: 180px; height: 150px\"></div>");

    //
    //  Begin the html output
    //
    out.println("<div class=\"reservations\">");

    
    //
    //  RDP - Create a url for the go back links based on who called this
    //
    String goBackURL = "Dining_home";     // default
    
    if (orig.equals("calendar")) goBackURL = "Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID + "";    // if came from calendar page
    

    //
    // output bread crumb trail
    //
    Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Dining " + reservation, req);
    Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
    //out.println("  <BR><BR>");

    out.println("<div class=\"preContentFix\"></div>"); // clear the float


    //
    // DEBUG
    //
    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        out.println("<!-- parmD.id=" + parmD.id + " -->");
        out.println("<!-- parmD.member_created=" + parmD.member_created + " -->");
        out.println("<!-- parmD.isUserMasterReservee=" + parmD.isUserMasterReservee(parmD.id, person_id, con_d) + " -->");
        out.println("<!-- canNonMasterReserveeEdit=" + parmD.canNonMasterReserveeEdit(parmD.organization_id, con_d) + " -->");
    }


    //
    // View Only Mode (view details - provide link to remove themselves)
    //
    if ( parmD.id != 0 && (
            //parmD.member_created == false ||
            parmD.isUserMasterReservee(parmD.id, person_id, con_d) == false)
       ) {

        // if reservation was not created by a member
        // OR
        // if user is not the master
        // OR
        // if non-master's cannot edit

        // extract the cost for the event
        parmDiningCosts parmCosts = new parmDiningCosts();
        parmCosts.costs = parmD.costs;

        out.println("<div id=\"reservation_action_title\">" + "View Existing " + reservation + "</div>");

        out.println("<div class=\"sub_main\">");

        out.println("<!--<span style=\"margin-left: 10px\">--><strong>Since you did not create this reservation the changes you can make are limited to canceling yourself. " +
                    "<br>For other changes, please contact the club.</strong><!--</span>-->");

        out.println("<div id=\"res_confirm\">");

        //
        // display the details
        out.println("<div class=res_qa>");

        out.println("<table>");

        if (event) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Event Name:</td>");
            out.println("<td class=\"detail\" nowrap>" + parmD.event_name + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td class=\"detail_name\" nowrap>Event Location:</td>");
            out.println("<td class=\"detail\" nowrap>" + parmD.location_name + "</td>"); // event_location_name
            out.println("</tr>");

        }

        out.println("<tr>");
        out.println("<td class=\"detail_name\">Date:</td>");
        out.println("<td class=\"detail\" nowrap>" + parmD.day_of_week + " " + parmD.sdate + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td>Time:</td>");
        out.println("<td class=\"detail\" nowrap>" + Utilities.getSimpleTime(parmD.time) + "</td>");
        out.println("</tr>");

        if (!event) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Location:</td>");
            out.println("<td class=\"detail\" nowrap>" + parmD.location_name + "</td>");
            out.println("</tr>");

        }

        // output names
        for (int i = 1; i <= parmD.covers; i++) {

            if (!parmD.names[i].equals("")) {

                out.println("<tr>");
                out.println("<td>Name #" + i + ":</td>");
                out.println("<td class=detail nowrap>");
                out.println(parmD.names[i] + "<br>");
                if (event) {
                    int cost_id = parmCosts.findIndex(parmCosts, parmD.price_category[i]);
                    //out.println("<!-- cost_id=" + cost_id + " -->");
                    //out.flush();
                    //out.println("<!-- parmCosts.price_categoryA[cost_id]=" + parmCosts.price_categoryA[cost_id] + " -->");
                    out.println(parmD.price_category[i] + " $" + parmCosts.costA[cost_id] + "<br>");
                }
                out.println("Check # " + parmD.check_num[i]);
                out.println("</td>");
                out.println("</tr>");
            }
        }

        if (!event && parmD.occasion_id != 0) {

            out.println("<tr>");
            out.println("<td>Occasion:</td>");
            out.println("<td class=detail>" + parmD.occasion + "</td>");
            out.println("</tr>");
        }

        if (!parmD.member_special_requests.equals("")) {

            out.println("<tr>");
            out.println("<td>Special Requests:</td>");
            out.println("<td class=detail>" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) + "</td>");
            out.println("</tr>");
        }

        out.println("</table>");

        out.println("<div id=\"message\" style=\"text-align:center;font-weight:bold\">");
        out.println("</div>");


        out.println("<div style=\"text-align:center;\">");
        out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack()\">");
        out.println("<input id=\"cancel\" name=\"cancel\" type=\"button\" value=\"Remove Myself\" onclick=\"doCancel()\">");
        out.println("</div>");

        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");

        out.println("<script type=\"text/javascript\">");
        out.println("function goBack() {");
        out.println(" ftReturnToCallerPage();");
        out.println("}");
        
         // This function will probably be overriden by foretees-global.js
        out.println("function ftReturnToCallerPage() {");
        out.println(" window.location.href='" +goBackURL+ "';");
        out.println("}");

        // remove just the current person from reservation
        out.println("function doCancel() {");
        out.println(" if(confirm('Are you sure you want to remove yourself from this reservation?') == false) return;");
        out.println(" var form_data = 'foretees_login=XV3';");
        out.println(" form_data += '&organization_id=" + organization_id + "';");
        out.println(" form_data += '&user_id=" + Utilities.getUserId(person_id) + "';");    // the user we will authenticate as and cancel from the reservation
        out.println(" form_data += '&cancel_id=" + parmD.id + "';");                        // the proxy will add this to the url
        out.println(" form_data += '&new_master_id=this';");                                // flag to only cancel this one record
        out.println(" form_data += '&_method=delete';");

        out.println(" $('#cancel').attr('disabled', 'disabled');");
        out.println(" $('#message').html(\"Removing...\")");
        out.println(" $(document).foreTeesModal(\"pleaseWait\");");

        out.println(" $.ajax({");
        out.println("   type: \"POST\", ");
        out.println("   url: \"/v5/dprox.php\", ");
        out.println("   data: form_data, ");
        out.println("   dataType: \"xml\", ");
        //out.println("   beforeSend: function(x) { ");
        //out.println("    if(x && x.overrideMimeType) { ");
        //out.println("     x.overrideMimeType(\"application/xml;charset=UTF-8\"); ");
        //out.println("    }");
        //out.println("   }, ");
        out.println("   success: function(response) {");
        out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
        out.println("     var ok = $('success', response).text();");
        out.println("     if(ok == '1'){");
        out.println("       $('#message').html(\"<h2>You Have Been Removed</h2>\")");
        out.println("       .fadeIn(1500, function() {");
        out.println("           $('#message').append(\"Please wait...\");");
        out.println("           ftReturnToCallerPage();");
        //out.println("           $('#message').append(\"Loading Calendar...\");");
        //out.println("           window.location.href='" +goBackURL+ "';");
        out.println("       });");
        out.println("     }else{");
        out.println("       $('error', response).each(function(id){");
        out.println("           if(id==0)$('#message').html('<h2>Error Encountered</h2>');");
        out.println("           var errMsg = $(response).find('error').eq(id).text();");
        out.println("           $('#message').append(errMsg+'<br>');");
        out.println("           $('#cancel').removeAttr('disabled');");
        out.println("       })");
        out.println("     }");
        out.println("   }, ");
        out.println("   error: function(xhr, ajaxOptions, thrownError) {");
        out.println("     $('#cancel').removeAttr('disabled');");
        out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
        out.println("     $('#message').html(\"<h2>Unexpected Error</h2>\")");
        out.println("     $('#message').append(\"Error: \" + thrownError);");
        out.println("   }");
        out.println(" });");
        out.println("}");

        out.println("</script>");

        return;

    } // end view only mode output



    //
    //  Get current date/time and setup parms to use when building the calendar
    //
    Calendar cal_date = new GregorianCalendar();             // get current date & time (Central Time)

    if (!parmD.allow_day_of) cal_date.add(Calendar.DAY_OF_MONTH, 1);

    int year = cal_date.get(Calendar.YEAR);
    int month = cal_date.get(Calendar.MONTH) + 1;
    int day = cal_date.get(Calendar.DAY_OF_MONTH);

    
    //  Setup the daysArray
    DaysAdv daysArray = new DaysAdv();          // allocate an array object for 'days in adv'
    daysArray = parmDining.daysInAdv(daysArray, organization_id, club, user, con, con_d);

  //int max = daysArray.maxview;

    String start_date = month + "/" + day + "/" + year;
/*
    // reset in case we incemented the day
    parmD.day_of_week = ProcessConstants.DAYS_OF_WEEK[cal_date.get(Calendar.DAY_OF_WEEK) - 1];
    parmD.sdate = start_date;
    parmD.date = (year * 10000) + (month * 100) + day;
    days_in_advance = SystemUtils.getIndexFromToday(parmD.date, con);
  */
    cal_date.add(Calendar.DAY_OF_MONTH, daysArray.maxview); // add the days in advance

    year = cal_date.get(Calendar.YEAR);
    month = cal_date.get(Calendar.MONTH) + 1;
    day = cal_date.get(Calendar.DAY_OF_MONTH);
    
    String end_date = month + "/" + day + "/" + year;

    //
    // DEBUG
    //
    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        out.println("<!-- days_in_advance=" + days_in_advance + " -->");
        out.println("<!-- allow_day_of=" + parmD.allow_day_of + " -->");
        out.println("<!-- date=" + parmD.date + " -->");
        out.println("<!-- sdate=" + parmD.sdate + " -->");
    }

    //
    // output page title
    //
    out.println("<div id=\"reservation_action_title\">" + title + "</div>");


    //
    // output a warning message if modifying
    //
    if (parmD.id != 0) out.println("<div class=\"main_warning\"><strong>Warning</strong>:  You have <strong>6 minutes</strong> to complete this reservation.   If you want to return without completing a reservation, <strong>do not use your browser's BACK</strong> button/option.  Instead select the <strong>Go Back</strong> option below.</div>");


    //
    // output the instructions
    //
    //out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>:  This is where we can tell the user how to complete the reservation.</div>");
    



    out.println("<div class=\"sub_main\">");

    out.println("<strong>Reservation Basics</strong>");

    out.println("<div class=\"res_select\">");


    if (club.equals("mirabel") || club.equals("thelegendclubs") || club.equals("esterocc") || club.equals("charlottecc") || club.equals("ccyork") || 
        club.equals("minikahda") || club.equals("oneidagcc") || club.equals("golfcrestcc") || (club.equals("demov4") && Common_Server.SERVER_ID == 4) ) {

        out.println("<div id=\"custom_message\" style=\"text-align:left;font-weight:normal;float:right;width:420px;background-color:white;padding:10px;border:1px solid #ffdca8;border-radius: 3px 3px 3px 3px; \">");

        if (club.equals("mirabel")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");
            out.println("Please note the following FlexRez guidelines:");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">Online reservations are accepted for groups sizes of 6 or less</li>");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">Reservations made online, must be done at a minimum of 24 hours in advance</li>");

            out.println("<li style=\"margin-left:25px;padding-top:5px;padding-bottom:5px\">Times are limited to 25 people per half hour.  If your desired time isn't visible, you may either choose a different time, or call the club for further assistance</li>");

            out.println("If you have any questions please call 480.437.1500 or email <a href=\"mailto:reservations@mirabel.com\">reservations@mirabel.com</a>");

            out.println("</ul>");

        } else if (club.equals("thelegendclubs")) {

            out.println("<p>");
            out.println("<b>Note:</b>&nbsp; We do allow same day reservations.&nbsp; If you do not see your desired dining time or dining location available, please call the club, this is due to a high volume of reservations.&nbsp; For online reservations we do have a maximum table size reservation limit.&nbsp; If you have reservation count that is higher then the maximum limit, please call the club so we may accommodate your large group accordingly.");
            //out.println("");
            //out.println("");
            //out.println("");
            out.println("</p>");

        } else if (club.equals("esterocc")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<b>Please note the following Dining guidelines:</b>&nbsp; ");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">The Bar area of the Club is called Mulligans and the casual dining room is the Grill, please be cautious when making reservations.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">Reservations made online are allowed for the same day. Same day reservations do cut off 30 minutes before the breakfast, lunch and dinner times begin at the club.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">If your desired time isn't visible, you may either choose a different time, or call the club for further assistance as tables may still be available.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">After your reservation is made online you will receive a new reservation confirmation e-mail.</li>");

            out.println("</ul>");

        } else if (club.equals("charlottecc")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">Thank you very much for arriving at the Club prior to your reservation time if you would like to enjoy a cocktail. In order to serve and provide a quality experience for as many members as possible, we will politely request your presence in the dining room at your reservation time.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">We welcome large parties! If you would like to make a reservation for a party of more than 6 guests, please call the Front Desk. 704-334-0836.</li>");

            out.println("</ul>");

        } else if (club.equals("minikahda")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<b>Please note the following Dining guidelines:</b>&nbsp; ");
            
            out.println("<li style=\"margin-left:25px;padding-top:5px\">Online reservations are accepted the same day up until 30 minutes before the breakfast, lunch and dinner seating times begin at the Club.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">Please remember NO Highchairs or Booster Seats allowed in the Dining Room or on the Dining Room Patio.</li>");

            out.println("</ul>");

        } else if (club.equals("oneidagcc")) {  //  || club.equals("demov4")

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<b>Please note the following Dining guidelines:</b>&nbsp; ");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">Online reservations are accepted for groups sizes of 8 or less, 4 or less on the Terrace</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">Reservations made online are accepted same day. Reservations must be made 2 hours before the lunch or dinner meal period begins at the Club</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">Times are limited to 15 people per quarter hour. if your desired time isn't visible, you may either choose a different time, or call the Club for further assistance</li>");
            
            out.println("<b>If you have any questions please call 920.498.6683 or email cjsmith@TroonGolf.com</b>&nbsp; ");

            out.println("</ul>");

        } else if (club.equals("golfcrestcc")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<b>Thank you for making your reservation in advance.</b>&nbsp; ");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">When making your breakfast, lunch or dinner reservation an hour or less before the Club's dining times begin, we ask that you call the Club at 281-485-4323, to ensure our staff have time to prepare for your party.</li>");

            out.println("</ul>");

        } else if (club.equals("ccyork")) {

            out.println("<ul style=\"list-style: disc; padding:10px;\">");

            out.println("<b>Please note the following dining guidelines:</b>&nbsp; ");

            out.println("<li style=\"margin-left:25px;padding-top:5px\">If your desired time isn't visible, it is due to the time slot being full.  Please select a different time or call the Club for further assistance.</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">After your reservation is made online you will receive a new reservation confirmation e-mail.  If you do not receive this email, the club did not receive your reservation!  Please call the Club if you do not receive a confirmation within 5 minutes of your reservation being made online confirming your new reservation.  You may also confirm the status of all of your current reservations online at: http://www.ccyork.org/mybookings</li>");
            out.println("<li style=\"margin-left:25px;padding-top:5px\">If you have any questions please call 717.843.8078 or email <a href=\"mailto:reservations@ccyork.org\">reservations@ccyork.org</a> for assistance.</li>");

            out.println("</ul>");
            
        }

        out.println("</div>");

    }

    
    out.println("<div class=\"qa\">");
    out.print("Your Reservation Date: <span id=\"date_box\" style=\"margin-right:5px\"><strong>" + parmD.day_of_week + ", " + parmD.sdate + "</strong></span>");
  //out.println("<a href=\"javascript:void(0)\" onclick=\"popupCalendar(this, document.getElementById('cal_elem_0'))\">(click here to change)</a>");

    out.print("<input type=text class=\"ft_date_picker ft-date-picker-dining\" data-ftstartdate=\""+start_date+"\" data-ftenddate=\""+end_date+"\" value=\"\" name=\"date_select\" size=\"12\" />");
    out.print("<span style=\"font-weight:bold;font-size:1.7em;margin-left:10px\">&larr;</span><span style=\"font-weight:bold;font-size:14px\">&nbsp;(use calendar to change date)</span>");
    out.println("</div>");



    // display any applicable event notices

    PreparedStatement pstmt = null;
    ResultSet rs = null;
    boolean did_header = false;

    try {

        pstmt = con_d.prepareStatement ("" +
                "SELECT id, name, members_can_make_reservations " +
                "FROM events " +
                "WHERE organization_id = ? AND date = '" + parmD.date + "' AND members_can_make_reservations = true");

        pstmt.setInt(1, organization_id);
        //pstmt.setString(2, "" + parmD.date);

        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            out.println("<div class=\"qa\">");
            out.println("<p><b>Note:</b> Your selected day has an event scheduled on it.  If by chance you are intending to register for this event, please use the <a href=\"Dining_home?view_events\">event signup</a>.</p>");
            out.println("</div>");
        }

/*
        while ( rs.next() ) {

            if (!did_header) {


                out.println("<div class=\"qa\">");
                out.println("<p>Your selected day has an event scheduled on it.  If you are intending to register for an event, please use the event signup.</p>");
                did_header = true;
            }

            out.println("");
            out.println("");

        }
        out.println("</div>");
*/

    } catch (Exception exc) {

        Utilities.logError("Dining_slot.output_form: event lookup: Err=" + exc.toString());

    } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

    }

    //
    // start dining form
    //
    //out.println("<div>");
    out.println("<form action=\"Dining_slot\" id=\"dining_reservation_form\" name=\"dining_reservation_form\" method=\"post\">");

    out.println(" <input id=\"action\" name=\"action\" type=\"hidden\" value=\"" + req.getParameter("action") + "\">");
    out.println(" <input id=\"date\" name=\"date\" type=\"hidden\" value=\"" + parmD.date + "\">"); // YYYYMMDD
    out.println(" <input id=\"orig\" name=\"orig\" type=\"hidden\" value=\"" + orig + "\">");
    out.println(" <input id=\"reservation_id\" name=\"reservation_id\" type=\"hidden\" value=\"" + parmD.id + "\">");
    out.println(" <input id=\"reservation_number\" name=\"reservation_number\" type=\"hidden\" value=\"" + parmD.reservation_number + "\">");
    out.println(" <input id=\"reservation[member_special_requests]\" name=\"reservation[member_special_requests]\" type=\"hidden\" value=\"" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) + "\">");
    out.println(" <input id=\"reservation_category\" name=\"reservation[category]\" type=\"hidden\" value=\"" + ((event) ? "event" : "dining") + "\">");

    // i don't think this one is used yet at this point
    out.println(" <input id=\"reservation_date\" name=\"reservation[date]\" type=\"hidden\" value=\"" + parmD.sdate + "\">"); // MM/DD/YYYY


/*
    out.println("<input id=\"foretees_login\" name=\"foretees_login\" type=\"hidden\" value=\"XV3\" />");
    out.println("<input id=\"organization_id\" name=\"organization_id\" type=\"hidden\" value=\"" + organization_id + "\" />");
    out.println("<input id=\"username\" name=\"username\" type=\"hidden\" value=\"" + user + "\" />");
    out.println("<input id=\"user_id\" name=\"user_id\" type=\"hidden\" value=\"" + Utilities.getUserId(person_id) + "\" />");
    out.println("<input id=\"person_id\" name=\"person_id\" type=\"hidden\" value=\"" + person_id + "\" />");
    out.println("<input id=\"reservation_person_id\" name=\"reservation[person_id]\" type=\"hidden\" value=\"" + person_id + "\" />");
    out.println("<input id=\"reservation_member_created\" name=\"reservation[member_created]\" type=\"hidden\" value=\"true\" />");
    out.println("<input id=\"reservation_organization_id\" name=\"reservation[organization_id]\" type=\"hidden\" value=\"" + organization_id + "\" />");
    out.println("<input id=\"next_related_reservation\" name=\"next_related_reservation\" type=\"hidden\" value=\"10\" />");
    out.println("<input id=\"step\" name=\"step\" type=\"hidden\" value=\"location\" />");
*/

    //
    // dump all reservation data to form elements
    //
    for (int i = 1; i <= parmD.covers; i++) {

        out.println(" <input type=\"hidden\" value=\"" + parmD.names[i] + "\" id=\"name_" + i + "\" name=\"name_" + i + "\">");
        out.println(" <input type=\"hidden\" value=\"" +  parmD.dining_id[i] + "\" id=\"data_" + i + "\" name=\"data_" + i + "\">");
        out.println(" <input type=\"hidden\" value=\"" + parmD.check_num[i] + "\" id=\"check_num_for_person_" + i + "\" name=\"check_num_for_person_" + i + "\">");
        out.println(" <input type=\"hidden\" value=\"" + parmD.related_id[i] + "\" id=\"reservation_id_for_person_" + i + "\" name=\"reservation_id_for_person_" + i + "\">"); // related_reservations[" + i + "][reservation_id]

    }


    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        
        out.println("<!-- parmD.location_id=" + parmD.location_id + " -->");
    }

    
    out.println("<div class=\"qa\">");
    out.println("Where would you like to dine?<br>");
    out.println("<select name=\"reservation[location_id]\" size=\"1\" onchange=\"updateForm()\">");
    if (parmD.location_id == 0) Common_Config.buildOption(0, "Choose...", 0, out);
    boolean found = false;

    for (int i = 0; i < locations.size(); i++) {

        String [] loc = new String [1];
        loc = locations.get(i);

        try {

        if ( parmD.location_id == Integer.parseInt(loc[0]) ) found = true;
        
        Common_Config.buildOption(loc[0], loc[1], Integer.toString(parmD.location_id), out);

        } catch (Exception exc) {
            out.println("\n\n" + exc.toString() + "<br>\n" + exc.getMessage() + "\n\n");
        }
        // if only one location was available then default to it
        // TODO: why do I have this in the for/next loop??????
        if (locations.size() == 1) {
            parmD.location_id = Integer.parseInt(loc[0]);
            parmD.loadLocationData(parmD, con_d);
        }
        
    }

    // if here to edit an existing rez then ensure that the currently reserved location is in the list
    if (parmD.id > 0 && !found) {
        
        Common_Config.buildOption(parmD.location_id, parmD.location_name, parmD.location_id, out);
    }


    out.println("</select>");
    out.println("</div>");

        
    //} else {
    if (parmD.location_id != 0) {
        
        // location already selected - display & provide link to change
        //out.println("Location: " + parmDining.get_location_name(parmD.location_id, con_d));

        out.println("<!-- parmD.time=" + parmD.time + " -->");

        //ArrayList<String[]> times = parmDining.getLocationTimes(organization_id, parmD.location_id, parmD.day_of_week, parmD.date, con_d);

        String time = "";
        if (parmD.time > 0) {

            int hr = parmD.time / 100;
            int min = parmD.time - (hr * 100);
            time = Utilities.ensureDoubleDigit(hr) + ":" + Utilities.ensureDoubleDigit(min);
            // time / 100, time - ((time / 100) * 100);
        }

        String htmlOptions = parmDining.getLocationTimesFromDining(organization_id, parmD.location_id, parmD.date, parmD.event_id, time);
        
        out.println("<div class=\"qa\">");
        out.println("When would you like to dine?<br>");
        out.println("<select name=\"reservation[reservation_time]\" size=\"1\">");

        // if here to edit an existing rez then ensure that the currently reserved time is in the list
        if (parmD.id > 0 && htmlOptions.indexOf(time) == -1) {

            Common_Config.buildOption(time, Utilities.getSimpleTime(parmD.time), time, out);

        }
        out.println(htmlOptions);
        out.println("</select>");
        out.println("</div>");

        //
        // TODO:  We could add some js here to count the options and if only one "CLOSED" option then display the warning div
        //

/*
        if (times.size() == 0) {

            // no times found
            out.println("<div class=\"qa\">");
            out.println("<font color=red>There are no online reservation times available on this day.<br>Please choose another location or date.</font>");
            out.println("</div>");

            // disable the Continue button
            out.println("<script type=\"text/javascript\">");
            out.println(" $('#commit_sub').attr('disabled', 'disabled');");
            out.println("</script>");

        } else {

            // display time selection
            //out.println("<input name=\"reservation[reservation_time]\" type=\"hidden\" value=\"14:15\" />");
            out.println("<div class=\"qa\">");
            out.println("When would you like to dine?<br>");
            out.println("<select name=\"reservation[reservation_time]\" size=\"1\">");
            for (int i = 0; i < times.size(); i++) {

                String [] time = new String [1];
                time = times.get(i);

                Common_Config.buildOption(time[0], time[1], (parmD.time / 100) + ":" + Utilities.ensureDoubleDigit((parmD.time - ((parmD.time / 100) * 100))), out);

            }
            out.println("</select>");
            out.println("</div>");

        }
*/
        //if (parmD.covers == 0) {

        if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
            out.println("<!-- parmD.covers=" + parmD.covers + " -->");
            out.println("<!-- parmD.dining_maximum_online_size=" + parmD.dining_maximum_online_size + " -->");
            out.println("<!-- parmD.dining_maximum_party_size=" + parmD.dining_maximum_party_size + " -->");
            out.println("<!-- parmD.occasion_id=" + parmD.occasion_id + " -->");
        }

        out.println("<div class=\"qa\">");
        out.println("How many people in the dining party?<br>");
        out.println("<select name=\"reservation[covers]\" size=\"1\">");
        
        // if either the location or the time were missing then don't allow the user to add additional covers
        for (int i = 1; i <= ((parmD.id > 0 && (htmlOptions.indexOf(time) == -1 || !found)) ? parmD.covers : parmD.dining_maximum_party_size); i++) {

            Common_Config.buildOption(i, i, parmD.covers, out);
        }
        out.println("</select>");
        out.println("</div>");
        //}

    }


    if (hide_special_occasions) {

        // hide the special occasion select box
        out.println("<input type=\"hidden\" id=\"reservation_occasion_id\" name=\"reservation[occasion_id]\" value=\"0\">");

    } else {

        out.println("<div class=\"qa\">");
         out.println("Are you celebrating a special occasion?<br>");
         out.println("<select id=\"reservation_occasion_id\" name=\"reservation[occasion_id]\"><option value=\"0\">No</option>");
         Common_Config.buildOption(1, "Birthday", parmD.occasion_id, out);
         Common_Config.buildOption(2, "Anniversary", parmD.occasion_id, out);
         Common_Config.buildOption(3, "Graduation", parmD.occasion_id, out);
         Common_Config.buildOption(4, "Promotion", parmD.occasion_id, out);
         Common_Config.buildOption(5, "Groom's Dinner", parmD.occasion_id, out);
         Common_Config.buildOption(6, "Engagement", parmD.occasion_id, out);
         Common_Config.buildOption(7, "Wedding Reception", parmD.occasion_id, out);
         Common_Config.buildOption(8, "Special Occasion", parmD.occasion_id, out);
         out.println("</select>");
        out.println("</div>");
    
    }


    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div id=\"message\" style=\"text-align:center;font-weight:bold\">");
    out.println("</div>");
    
    
    out.println("<div align=\"center\">");
     out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack()\">");
     // only allow user to cancel if they are the one that created the reservation
     if ( parmD.id != 0 && parmD.member_created && (parmD.isUserMasterReservee(parmD.id, person_id, con_d) || parmD.canNonMasterReserveeEdit(parmD.organization_id, con_d)) ) {
         out.println("<input id=\"cancel\" name=\"cancel\" type=\"button\" value=\"Cancel Reservation\" onclick=\"cancel_reservation()\">");
     }
     /*
     if (parmD.id == 0) {
         //out.println("<input id=\"view\" name=\"view\" type=\"button\" value=\"View Reservations\" onclick=\"view_reservations()\">");
         out.println("<a href=\"Dining_home?nowrap&view_reservations&date=" + parmD.date + "\" class=\"tu_iframe_620x400\"><button id=\"view_res\" name=\"view_res\">View Reservations</button></a>");
     }
      */
     
    out.println("<input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Continue\" " + ((parmD.location_id == 0) ? "disabled" : "") + ">");

    if (parmD.id == 0 && parmDining.areDiningReservationsPublic(parmD.organization_id, con_d)) {
         //out.println("<input id=\"view\" name=\"view\" type=\"button\" value=\"View Reservations\" onclick=\"view_reservations()\">");
         out.println("<br><a href=\"#\" data-ftlink=\"Dining_home?nowrap&view_reservations&date=" + parmD.date + "\" class=\"dining_event_modal\"><button id=\"view_res\" name=\"view_res\">View All Reservations for Selected Date</button></a>");
         out.println("<br><a href=\"#\" data-ftlink=\"Dining_home?nowrap&view_reservations&date=" + Utilities.getDate(con) + "\" class=\"dining_event_modal\"><button id=\"view_res\" name=\"view_res\">View All Reservations for Today</button></a>");
    }
    out.println("</div>");

    out.println("</form>");

    out.println("<p>&nbsp;</p>");


    out.println("</div>"); // res_select
    out.println("</div>"); // sub_main


    //out.println("</div>"); // form div
    out.println("</div>"); // reservations

    cal_date = new GregorianCalendar();             // get current date & time (Central Time)
    year = cal_date.get(Calendar.YEAR);
    month = cal_date.get(Calendar.MONTH) + 1;
    day = cal_date.get(Calendar.DAY_OF_MONTH);

    out.println("<script type=\"text/javascript\">");
    out.println("function updateForm() {");
    out.println(" document.dining_reservation_form.submit();");
    out.println("}");
    out.println("function goBack() {");
   if (parmD.id != 0) {
    out.println(" if (confirm('Are you sure you wish to abort making changes to this reservation?')) {");
   } else {
    out.println(" if (confirm('Are you sure you wish to abort making this reservation?')) {");
   }
    out.println("  ftReturnToCallerPage();");
    out.println(" } ");
    out.println("}");
    
    // This function will probably be overriden by foretees-global.js
    out.println("function ftReturnToCallerPage() {");
    out.println(" window.location.href='" +goBackURL+ "';");
    out.println("}");

    out.println("function view_reservations() {");
    out.println(" window.location.href = \"Dining_home?view_reservations&date=" + parmD.date + "\"");
    out.println("}");

    // remove user from reservation !!!CANCELED ENTIRE RESERVATION!!!
    out.println("function cancel_reservation() {");
    out.println(" if(confirm('Are you sure you want to cancel yourself from this reservation?') == false) return;");
    out.println(" var form_data = 'foretees_login=XV3';");
    out.println(" form_data += '&organization_id=" + organization_id + "';");
    out.println(" form_data += '&user_id=" + Utilities.getUserId(person_id) + "';");
    out.println(" form_data += '&cancel_id=" + parmD.id + "';");
    out.println(" form_data += '&_method=delete';");
    out.println(" $(document).foreTeesModal(\"pleaseWait\");");
    out.println(" $('#message').html(\"Removing...\")");
    out.println(" $.ajax({");
    out.println("   type: \"POST\", ");
    out.println("   url: \"/v5/dprox.php\", ");
    out.println("   data: form_data, ");
    out.println("   dataType: \"xml\", ");
    //out.println("   beforeSend: function(x) { ");
    //out.println("    if(x && x.overrideMimeType) { ");
    //out.println("     x.overrideMimeType(\"application/xml;charset=UTF-8\"); ");
    //out.println("    }");
    //out.println("   }, ");
    out.println("   success: function(response) {");
    out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
    out.println("     var ok = $('success', response).text();");
    out.println("     if(ok == '1'){");
    out.println("       $('#message').html(\"<h2>You Have Been Removed</h2>\")");
    out.println("       .fadeIn(1500, function() {");
    out.println("           $('#message').append(\"Please Wait...\");");
    out.println("           ftReturnToCallerPage();");
    //out.println("           window.location.href='Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID + "';");
    out.println("       });");
    out.println("     }else{");
    out.println("       $('error', response).each(function(id){");
    out.println("           if(id==0)$('#message').html('<h2>Error Encountered</h2>');");
    out.println("           var errMsg = $(response).find('error').eq(id).text();");
    out.println("           $('#message').append(errMsg+'<br>');");
    out.println("           $('#cancel').removeAttr('disabled');");
    out.println("       })");
    out.println("     }");
    out.println("   }, ");
    out.println("   error: function(xhr, ajaxOptions, thrownError) {");
    out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
    out.println("     $('#message').html(\"<h2>Unexpected Error</h2>\")");
    out.println("     $('#message').append(\"Error: \" + thrownError);");
    out.println("     $('#cancel').removeAttr('disabled');");
    out.println("   }");
    out.println(" });");
    //out.println(" $('#cancel').attr('disabled', '');");
    out.println(" return false;");
    out.println("}"); // end cancel_reservation

/*
    out.println("</script>");

    
    out.println("<script type=\"text/javascript\">");

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

    // set calendar date parts
    out.println("g_cal_month[0] = " + month + ";");
    out.println("g_cal_year[0] = " + year + ";");
    out.println("g_cal_beginning_month[0] = " + month + ";");
    out.println("g_cal_beginning_year[0] = " + year + ";");
    out.println("g_cal_beginning_day[0] = " + day + ";");

    cal_date.add(Calendar.DAY_OF_MONTH, max); // add the days in advance

    year = cal_date.get(Calendar.YEAR);
    month = cal_date.get(Calendar.MONTH) + 1;
    day = cal_date.get(Calendar.DAY_OF_MONTH);
    out.println("g_cal_ending_month[1] = " + month + ";");
    out.println("g_cal_ending_day[1] = " + day + ";");
    out.println("g_cal_ending_year[1] = " + year + ";");

    out.print("var daysArray = new Array(");
    int js_index = 0;
    for (js_index = 0; js_index <= max; js_index++) {
       out.print(daysArray.days[js_index]);
       if (js_index != max) out.print(",");
    }
    out.println(");");

    out.println("var max = " + max + ";");
    
    out.println("function popupCalendar(locationElem, calendarElem) {");

    out.println(" var posX = 5, posY = 5;");
    out.println(" var elem = locationElem;");
    out.println(" var px = document.layers ? '' : 'px';");

    out.println(" if (elem.offsetParent) {");
    out.println("  do {");
    out.println("   posX += elem.offsetLeft;");
    out.println("   posY += elem.offsetTop;");
    out.println("  } while (elem = elem.offsetParent)");
    out.println(" }");

    out.println(" calendarElem.style.top=posY+px;");
    out.println(" calendarElem.style.left=posX+px;");

    out.println(" doCalendar('0');");

    out.println(" document.getElementById('cal_elem_0').style.display='block';");

  //out.println(" resultElem = textBoxElem;");

    out.println("}");
*/

    //out.println("function sd(pCal, pMonth, pDay, pYear) {");
    out.println("function sd(textDate) {");
    
    out.println(" document.getElementById('reservation_date').value=textDate;");

    out.println(" var date_array = textDate.split('/');");
    out.println(" var year = parseInt(date_array[2], 10);");
    out.println(" var month = parseInt(date_array[0], 10);");
    out.println(" var day = parseInt(date_array[1], 10);");
    out.println(" var d = (year * 10000) + (month * 100) + (day * 1);");

    out.println(" document.getElementById('date').value=d;");
/*
    out.println(" document.getElementById('cal_elem_0').style.display='none';");
    out.println(" document.getElementById('date_box').textContent = pMonth + '/' + pDay + '/' + pYear;");
    out.println(" document.getElementById('reservation_date').value=pMonth + '/' + pDay + '/' + pYear;");
    out.println(" var d = (pYear * 10000) + (pMonth * 100) + pDay;");
    out.println(" document.getElementById('date').value=d;");
*/
    out.println(" updateForm();");
    
    out.println("}");
    
    out.println("</script>");

 }


 private void output_form2(int id, int event_id, String user, String club, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {


    boolean ext_login = false;            // not external login (from email link)
   
    boolean event = (event_id != 0);

    //boolean require_check_numbers = true; // eventually get this from organizations table in postgres

    boolean hide_check_numbers = (club.equals("misquamicut"));

    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);
    
    String errMsg = "";
    String orig = "";
    
    if (req.getParameter("orig") != null) {
       
       orig = req.getParameter("orig");    //  Get the origin of this call (from Calendar or from Rservation Link)
    }
        
    if (req.getParameter("ext_login") != null) {   // if originated from Login for an external login user
       
       ext_login = true;
    }


    parmDining parmD = new parmDining();

    // populate parmD from the request object
    parmD.id = id;
    parmD.event_id = event_id;
    parmD.organization_id = organization_id;

    int reservation_id = 0;
    try { reservation_id = Integer.parseInt(req.getParameter("reservation_id")); }
    catch (Exception ignore) { }

    // if coming in for inital page request for an existing event rez then load from db
    // otherwise load all data from req object
    if (event && reservation_id != 0 && req.getParameter("page2") == null) {
        out.println("<!-- POPULATING EVENT RES DATA FROM DB -->");
        parmD.populate(parmD, con_d);
    } else {
        out.println("<!-- LOADING RES DATA FROM REQ -->");
        parmD.populate(parmD, req, con_d);
    }


    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        out.println("<!-- user=" + user + " -->");
        out.println("<!-- covers=" + parmD.covers + " -->");
        out.println("<!-- names_found=" + parmD.names_found + " -->");
        out.println("<!-- meal_period_id=" + parmDining.getMealPeriodId(parmD, con_d) + " -->");
        out.println("<!-- question_count=" + parmD.question_count + " -->");
        out.println("<!-- reservation_number=" + parmD.reservation_number + " -->");
        //out.println("<!-- member_special_requests=" + parmD.member_special_requests + " -->");
    }


    // extract the cost for the event
    parmDiningCosts parmCosts = new parmDiningCosts();
    parmCosts.costs = parmD.costs;

    if ( !parmCosts.parseCosts() ) {

        out.println("<!--  FATAL ERROR: " + parmCosts.err_string + " -->");
        out.println("<!--  FATAL ERROR: " + parmCosts.err_message + " -->");

    }

    parmDiningSeatings parmSeatings = new parmDiningSeatings();
    if ( parmD.time_format.equals("multiple") ) {
        // extract the seating times for the event (if applicable)
        parmSeatings.seatings = parmD.seatings;

        if ( !parmSeatings.parseSeatings() ) {

            out.println("<!--  FATAL ERROR: " + parmSeatings.err_string + " -->");
            out.println("<!--  FATAL ERROR: " + parmSeatings.err_message + " -->");

        }

        if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) out.println("<!--  parmSeatings.seatings_found=" + parmSeatings.seatings_found + " -->");

    }

    
    // check for time and location - required
    
    if (parmD.time == 0) {
        
        errMsg = "The time of day is missing. &nbsp;You must select a time from the options provided.";
        displayError(errMsg, club, out, req);
        return;
        
    } else if (parmD.location_name == null || parmD.location_name.equals("")) {
        
        errMsg = "The location is missing. &nbsp;You must select a location from the options provided.";
        displayError(errMsg, club, out, req);
        return;
    }   
    

/*
    if (parmD.id > 0) {

        // we are here to view/modify an existing reservation from the database
        parmD.load_reservation(parmD, con_d);

    } else {

        // we are here at some stage of completing a new reservation
        // load from the request object
        parmD.load_reservation(parmD, req, con_d);

    }
*/

    // auto add the user if this is a new reservation and the 1st pos is empty
    if (parmD.id == 0 && parmD.names[1].equals("")) {

        // put current info in 1st position
        parmD.names[1] = Utilities.getFullNameFromUsername(user, con);
        parmD.dining_id[1] = Utilities.getPersonId(user, con);

    }

    
    
    //
    //  RDP - Create a url for the go back links based on who called this
    //
    String goBackURL = "Dining_home";     // default
    
    if (orig.equals("calendar")) goBackURL = "Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID;    // if came from calendar page
    
    if (event) goBackURL = "Dining_home?view_events=yes";    // if came from calendar page


    String reservation = (event) ? "Event Reservation" : "Reservation";
    String title = (parmD.id > 0) ? "Modify Existing " + reservation : "Create New " + reservation;

    title += (event) ? " for " + parmD.event_name : "";
    

    //
    // output bread crumb trail
    //
    Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Dining " + reservation, req);
    Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
    //out.println("  <BR><BR>");
    /*
    if (orig.equals("calendar")) {
    
        out.println("<div id=\"breadcrumb\">Home / Dining Calendar / Dining " + reservation + "</div>");

    } else {

        out.println("<div id=\"breadcrumb\">Home / Dining " + reservation + "</div>");
    }
    * 
    */

    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div id=\"reservation_action_title\">" + title + "</div>");


    //
    // output a warning message if modifying
    //
    if (parmD.id != 0) {

        out.println("<div class=\"main_warning\">" +
            "<strong>Warning</strong>:  " +
            "You have <strong>6 minutes</strong> to complete this reservation. " +
            "If you want to return without completing a reservation, <strong>do not use your browser's BACK</strong> button/option. " +
            "Instead select the <strong>Go Back</strong> option below. Also make sure to add members by selecting them from your Partner List or by using " +
            "the Member List.</div>");
        
    } else {

        out.println("<div class=\"main_warning\">" +
            "<strong>Notice</strong>:  " +
            "To add other members to your reservation please select their name from either your Partner List or by using the Member List.  Do not " +
            "type names in to the text boxes or the system may not recognize them as members and may assign them as guests.  When you select a Guest / TBD option " +
            "you will be able to type a respective name in the appropriate text box.</div>");

    }

    //
    // output the instructions
    //
    //out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>:  This is where we can tell the user how to complete the reservation.</div>");


    //
    // start dining form
    //
    out.println("<form action=\"Dining_slot\" id=\"dining_reservation_form\" name=\"dining_reservation_form\" method=\"post\">");

    out.println("<input id=\"page2\" name=\"page2\" type=\"hidden\" value=\"1\">");
    out.println("<input id=\"bump_covers\" name=\"bump_covers\" type=\"hidden\" value=\"0\">");
    out.println("<input id=\"orig\" name=\"orig\" type=\"hidden\" value=\"" + orig + "\">");
    out.println("<input id=\"action\" name=\"action\" type=\"hidden\" value=\"" + req.getParameter("action") + "\">");
    
    out.println("<input id=\"date\" name=\"date\" type=\"hidden\" value=\"" + req.getParameter("date") + "\">");
    out.println("<input id=\"foretees_login\" name=\"foretees_login\" type=\"hidden\" value=\"XV3\">");
    out.println("<input id=\"organization_id\" name=\"organization_id\" type=\"hidden\" value=\"" + organization_id + "\">");
    out.println("<input id=\"username\" name=\"username\" type=\"hidden\" value=\"" + user + "\">");
    out.println("<input id=\"user_id\" name=\"user_id\" type=\"hidden\" value=\"" + Utilities.getUserId(person_id) + "\">");

    out.println("<input id=\"reservation_id\" name=\"reservation_id\" type=\"hidden\" value=\"" + parmD.id + "\">");
    out.println("<input id=\"reservation_number\" name=\"reservation_number\" type=\"hidden\" value=\"" + parmD.reservation_number + "\">");
    out.println("<input id=\"event_id\" name=\"event_id\" type=\"hidden\" value=\"" + parmD.event_id + "\">");
    out.println("<input id=\"reservation_person_id\" name=\"reservation[person_id]\" type=\"hidden\" value=\"" + person_id + "\">");
    out.println("<input id=\"reservation_member_created\" name=\"reservation[member_created]\" type=\"hidden\" value=\"true\">");

    out.println("<input id=\"reservation_organization_id\" name=\"reservation[organization_id]\" type=\"hidden\" value=\"" + organization_id + "\">");

    // answers from step 1
  //out.println("<input name=\"reservation[location_id]\" type=\"hidden\" value=\"" + parmD.location_id + "\">");
    out.println("<input name=\"reservation[reservation_date]\" type=\"hidden\" value=\"" + parmD.sdate + "\">");
    out.println("<input name=\"reservation[number_of_checks]\" type=\"hidden\" value=\"" + parmD.covers + "\">");
    out.println("<input name=\"reservation[covers]\" type=\"hidden\" value=\"" + parmD.covers + "\">");
  //out.println("<input name=\"reservation[covers]\" type=\"hidden\" value=\"1\" />");
    out.println("<input name=\"reservation[occasion_id]\" type=\"hidden\" value=\"" + parmD.occasion_id + "\">");

    if (ext_login == true) {    // if external login
       out.println("<input name=\"ext_login\" type=\"hidden\" value=\"yes\">");
    }


    if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
        out.println("<!-- parmD.location_id=" + parmD.location_id + " -->");
        out.println("<!-- parmD.location_name=" + parmD.location_name + " -->");
        out.println("<!-- parmD.event_default_location_id=" + parmD.event_default_location_id + " -->");
        out.println("<!-- parmD.event_default_location_name=" + parmD.event_default_location_name + " -->");
        out.println("<!-- parmD.event_location_count=" + parmD.event_location_count + " -->");
    }
   
    out.print("<div id=\"tt1_left\">");
    out.println("Date:  <strong>" + parmD.day_of_week + " " + parmD.sdate + "</strong> &nbsp;&nbsp;&nbsp;&nbsp;");
    
    //
    //  If only the time to display, then display it now next to the date, else wrap the info/selection boxes in a table to draw more attention
    //
    if (parmD.event_location_count == 0 && !parmD.time_format.equals("multiple") && !parmD.time_format.equals("ongoing")) {
    
        out.println("<input name=\"reservation[location_id]\" type=\"hidden\" value=\"" + parmD.location_id + "\">");
        out.println("<input name=\"reservation[reservation_time]\" type=\"hidden\" value=\"" + parmD.time + "\">");
    
        out.print("Time: <strong>" + Utilities.getSimpleTime(parmD.time) + "</strong>");    
        out.println("</div>");     // end ttl_left
        
    } else {

        out.println("</div>");     // end ttl_left

        out.println("<div class=\"res_select\">");
        out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

        out.println("<tr valign=\"top\">");
        out.println("<td>");

        if (parmD.event_location_count > 0) { // event_location_count

            // go count covers now for latest values

            out.println("&nbsp;&nbsp;Select a location:&nbsp;&nbsp;&nbsp;");

            out.print("<select name=\"reservation[location_id]\" size=\"1\" onchange=\"update_times(this.options[this.selectedIndex].value)\">");
            // display available locations
            for (int i = 0; i < parmD.event_location_count; i++) {

                String times = parmD.getLocationTimesFromDining(organization_id, Integer.parseInt(parmD.event_locations[i][3]), Integer.parseInt(req.getParameter("date")), parmD.event_id, "", "");

                if (times.indexOf(":") > 0) {

                    // at least one time was found for this location
                    out.print("<option value=\"" + parmD.event_locations[i][3] + "\"" + ((Integer.parseInt(parmD.event_locations[i][3]) == parmD.location_id) ? " selected" : "") + ">" + parmD.event_locations[i][0] + "</option>");
                }


              //  if (parmD.countEventCovers(organization_id, parmD.event_id, Integer.parseInt(parmD.event_locations[i][3]), true, con_d) < Integer.parseInt(parmD.event_locations[i][2]) ) {

              //      out.print("<option value=\"" + parmD.event_locations[i][3] + "\"" + ((Integer.parseInt(parmD.event_locations[i][3]) == parmD.location_id) ? " selected" : "") + ">" + parmD.event_locations[i][0] + "</option>");

              //  }

            }
            out.println("</select> &nbsp;&nbsp;&nbsp;");

        } else {

            // this else block should'nt be needed since all events should have at least one booking entry
            out.print("<strong>" + parmD.event_default_location_name + "</strong> &nbsp;&nbsp;&nbsp;");
            out.println("<input name=\"reservation[location_id]\" type=\"hidden\" value=\"" + parmD.location_id + "\">");

        }

        // seating_timeA
        // out.println("<div id=\"dining_time_list_content\">");
        if (parmD.time_format.equals("multiple")) {

            // mulitple
            // TODO: if here editing an exiting reservation that is now full their current time will not be returned in the list - so for now if editing, do not allow users to change time?
            String time = "";
            if (parmD.time > 0) {
                time = Utilities.ensureDoubleDigit(parmD.time / 100) + ":" + Utilities.ensureDoubleDigit(parmD.time - (parmD.time / 100 * 100));
            }
            out.print("&nbsp;&nbsp;Select a time:&nbsp;&nbsp;&nbsp;<select name=\"reservation[reservation_time]\" size=\"1\">");
            out.println(parmDining.getLocationTimesFromDining(organization_id, parmD.location_id, parmD.date, parmD.event_id, time));
            //for (int i = 0; i < parmSeatings.seatings_found; i++) {
            //    Common_Config.buildOption(parmSeatings.seating_time_valueA[i], parmSeatings.seating_timeA[i], time, out);
            //}
            out.print("</select>");

        } else if (parmD.time_format.equals("ongoing")) {

            // ongoing
            String time = "";
            if (parmD.time > 0) {

                time = Utilities.ensureDoubleDigit(parmD.time / 100) + ":" + Utilities.ensureDoubleDigit(parmD.time - (parmD.time / 100 * 100));
            }

            out.print("&nbsp;&nbsp;Select a time:&nbsp;&nbsp;&nbsp;<select name=\"reservation[reservation_time]\" size=\"1\">");
            out.println(parmDining.getLocationTimesFromDining(organization_id, parmD.location_id, parmD.date, parmD.event_id, time));
            out.print("</select>");

        } else {

            // single event time or ala carte reservation
            out.print("Time: <strong>" + Utilities.getSimpleTime(parmD.time) + "</strong>");
            out.println("<input name=\"reservation[reservation_time]\" type=\"hidden\" value=\"" + parmD.time + "\">");
        }
        //out.println("</div>"); // end dining_time_list_content

        out.println("</td></tr></table>");
        out.println("</div>"); // end res_select div

        //out.println("</div>");     // end ttl_left (moved to above)
        
    }        // end of IF only time to display


    out.println("<div class=\"res_left\">");

        out.println("<div class=\"sub_main\">");

            out.println("<strong>Please Specify Each Person</strong>  &nbsp; &nbsp;&nbsp; &nbsp;&nbsp; &nbsp; Click on Names &nbsp;&gt;&gt;");

            out.println("<div class=\"res_select\">");
                out.println("<table cellpadding=\"0\" cellspacing=\"0\" border=\"0\">");

                // table cols
                //
                // erase link
                // name box
                // check #
                // spacer

                out.println("<tr valign=\"top\">");
                out.println("<td>&nbsp;</td><td><strong>Names</strong></td>");
                out.println("<td nowrap>" + ((hide_check_numbers) ? "" : "<strong>Check #</strong>") + "</td>");
                out.println("<td></td></tr>");
                //out.println("</tr>");

                for (int i = 1; i <= parmD.covers; i++) {
                    
                    out.println("<tr>");

                    // erase link
                    out.println("<td>");
                    out.println("<input type=\"hidden\" name=\"data_" + i + "\" value=\"" + parmD.dining_id[i] + "\">");
                    out.println("<input type=\"hidden\" name=\"duid\" value=\"" + Utilities.getUserId(parmD.dining_id[i]) +  "\">");
                    out.println("<input type=\"hidden\" name=\"orig_name\" value=\"" + parmD.names[i].trim() +  "\">");
                    out.println("<input type=\"hidden\" name=\"reservation_id_for_person_" + i + "\" value=\"" + parmD.related_id[i] + "\">");
                    out.println("<input type=\"hidden\" name=\"related_reservations[" + i + "][covers]\" value=\"1\">");
                    out.println("<input type=\"hidden\" name=\"related_reservations[" + i + "][user_identity]\" value=\"" + ((parmD.dining_id[i] != 0) ? parmD.getUserIdentity(parmD.dining_id[i], con_d) : "") + "\">");

                    // don't let the user change the first name
                    /*
                    if (i != 1) { //  && !(!parmD.names[i].trim().equals("") && parmD.dining_id[i] == 0)
                        out.println("<a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"erasename('name_" + i + "')\">Erase</a>");
                    } else {
                        // display a cancel link
                        out.println("<a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"cancel_related('" + parmD.related_id[i] + "')\">Cancel</a>");
                    }
                     * 
                     */
                    out.println("<a href=\"javascript:void(0);\" class=\"tip_text\" onclick=\"cancel_related('" + parmD.related_id[i] + "', this)\">Remove</a>");
                    out.println("</td>");

                    // name box
                    out.println("<td nowrap>" + i + ": <input type=\"text\" id=\"name_" + i + "\" name=\"name_" + i + "\" class=\"res_name\" " +
                                "value=\"" + parmD.names[i].trim() + "\"" + ((!parmD.reservee_category[i].equalsIgnoreCase("Child") && !parmD.reservee_category[i].equalsIgnoreCase("Guest")) ? " onfocus=\"this.blur()\"" : "") + "></td>"); // this.blur()
                              //"value=\"" + parmD.names[i].trim() + "\"" + ((i == 1 || (!parmD.names[i].trim().equals("") && parmD.dining_id[i] == 0)) ? " onfocus=\"\"" : "") + "></td>"); // this.blur()

                    // check number select
                    if (hide_check_numbers) {

                        // include the default then (not sure if this is needed)
                        out.println("<td><input type=\"hidden\" name=\"check_num_for_person_" + i + "\" value=\"1\"></td>");

                    } else {

                        out.println("<td><select size=\"1\" name=\"check_num_for_person_" + i + "\">");
                        for (int chk_num = 1; chk_num <= parmD.covers; chk_num++) {
                            Common_Config.buildOption(chk_num, chk_num, parmD.check_num[i], out);
                        }
                        out.println("</select></td>");
                    }

                    // price category (new row)
                    if (event) {

                        out.println("</tr><tr><td></td>");
                        out.println("<td colspan=\"4\">&nbsp;&nbsp;&nbsp;&nbsp;" +
                                "Choose a meal option: " +
                                "<select size=\"1\" name=\"related_reservations[" + i + "][price_category]\">");
                        for (int i2 = 0; i2 < parmCosts.costs_found; i2++) {
                            Common_Config.buildOption(parmCosts.price_categoryA[i2], parmCosts.price_categoryA[i2] + ": $" + parmCosts.costA[i2], parmD.price_category[i], out);
                        }
                        out.println("</select></td>");
                    }
                    
                    out.println("</tr>");

                } // end loop of names boxes to display (covers)

                if (Common_Server.SERVER_ID == 4 || club.startsWith("demo")) {
                    out.println("<!-- maximum_party_size=" + parmD.maximum_party_size + " -->");
                    out.println("<!-- online maximum_party_size=" + parmD.dining_maximum_online_size + " -->");
                    out.println("<!-- online dining_maximum_party_size=" + parmD.dining_maximum_party_size + " -->");
                }

                // add person link
                // if (!event || (event && parmD.covers < parmD.maximum_party_size)) {         // RDP - added check for max online size if not event 
                if ((!event && parmD.covers < parmD.dining_maximum_party_size) || (event && parmD.covers < parmD.maximum_party_size)) {

                    out.println("<tr>");
                        out.println("<td align=\"left\">&nbsp;&nbsp;<a href=\"javascript:addname()\" class=\"add_diner_text\">Add Another<!--<img src=\"/"+rev+"/images/dts_newrow.gif\" name=\"img_add_name\" alt=\"Add Another Person\" width=\"35\" height=\"25\" border=\"0\">--></a></td>");
                        //out.println("<td></td>");
                    out.println("</tr>");

                }

                // spacer row
                out.println("<tr><td><img src=/v5/images/shim.gif width=1 height=1 border=0></td></tr>");
                
                // notes
                out.println("<tr>" +
                            "<td><a href=\"javascript:void(0);\" class=\"tip_text\">Erase</a></td>" +
                            "<td colspan=\"3\">" +
                                "Are there any special requests for this reservation?" +
                                "<textarea id=\"member_special_requests\" name=\"reservation[member_special_requests]\" rows=\"5\" cols=\"60\" style=\"width:370px;height:80px\">" +
                                "" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) +
                                "</textarea>" +
                            "</td></tr>");


                out.println("</table>");
            out.println("</div>"); // end res_select div

            out.println("<div class=\"res_select\"><strong>NOTE:</strong> &nbsp;To add a Guest, click on one of the Guest types listed in the 'Guest/TBD' box to the right. &nbsp;Add the guest " +
                        "immediately after the host member. &nbsp;To include the name of a guest, type the name after the guest type in the player box above. " +
                        "&nbsp;Use the Check #s to indicate separate checks, if desired.</div>");

            out.println("<div id=\"message\" style=\"text-align:center;font-weight:bold\">");
            out.println("</div>");

            // display the go back and submit buttons
            out.println("<div style=\"text-align:center;\">");

             out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false\" />");
             if (event && parmD.id != 0 && parmD.member_created && (parmD.isUserMasterReservee(parmD.id, person_id, con_d) || parmD.canNonMasterReserveeEdit(parmD.organization_id, con_d)) ) {
                 out.println("<input id=\"cancel\" name=\"cancel\" type=\"button\" value=\"Cancel Reservation\" onclick=\"cancel_reservation()\">");
             }
             out.println("<input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Reserve\" " + ((event) ? "" : "onclick=\"return checkCovers()\"") + "/>");

             out.println("<div class=\"tip_text\"><a href=\"javascript:void(0);\">Click For Help</a></div>");

            out.println("</div>");

        out.println("</div>"); // end sub_main div
   
   
    out.println("</div>"); // end res_left div
   
    //
    // output partner list
    //
    out.println("<div class=\"res_mid\" id=\"name_select\">");

        out.println(" <div class=\"name_list\" id=\"name_list_content\">");
            nameLists.displayPartnerList(user, ProcessConstants.DINING_ACTIVITY_ID, 0, con, out);         // use dining_activity_id - using zero for golf now
        out.println("</div>"); // end name_list_content div

        out.println("<div class=\"main_warning\" style=\"margin-top:10px;\">Note: DO NOT USE Your Browser's Back Button!</div>");
        
    out.println("</div>"); // end res_mid
   
    
    //
    // output letter box list
    //
    out.println("<div class=\"res_right\">");
    nameLists.getTable(out, user);


    //
    // TBD
    // TODO: conditionally include the TBD and guest options
    //
    out.println("<div class=\"sub_main\" style=\"margin-top:10px;\"><strong>Guest / TBD</strong> ");
        out.println("<div class=\"tip_text\">Be sure to include the names of your guests. Use 'TBD' for someone to be named later.</div>");
        out.println("<div class=\"res_select_ltr\">");
        out.println(" <a href=\"javascript:void(0);\" onclick=\"move_name('Guest ')\">Guest</a>");
        out.println(" <a href=\"javascript:void(0);\" onclick=\"move_name('Child ')\">Child</a>");
        out.println(" <a href=\"javascript:void(0);\" onclick=\"move_name('TBD')\">TBD</a>");
        out.println("</div>");
    out.println("</div>"); // end sub_main div

    out.println("</div>"); // end res_right div

    out.println("</form>");


    out.println("<div class=\"preContentFix\"></div>"); // clear the float

/*
    //
    // Form for canceling a reservation (only events are canceled from this page - ala carte reservations are deleted from output_form method)
    //
    if (event && parmD.id != 0) {
        out.println("<form id=\"dining_cancel_form\" method=\"post\" action=\"http://dining.foretees.com/self_service/dining_reservations/" + parmD.id + ".xml\" target=\"_result\">");
        out.println("<input type=hidden name=\"_method\" value=\"delete\">");
        out.println("</form>");
    }
*/

    //
    // Scripts
    //
    out.println("<script type=\"text/javascript\">");
    out.println("function checkCovers() {");
    out.println(" var empty = 0;");
    out.println(" var found = 0;");
    out.println(" var f = document.dining_reservation_form;");
    out.println(" var names = new Array();");
    for (int i = 1; i <= parmD.covers; i++) {

        out.println(" names[" + i + "] = f.name_" + i + ".value;");
    }
    out.println(" for (i = 1; i <= " + parmD.covers + "; i++) {");
    out.println("  if ($.trim(names[i]) == '') {");                   // not filled out
    out.println("   empty = 1;");
    out.println("   break;");
    out.println("  } else { found++; }");
    out.println(" }");

    out.println(" if (empty == 1) {");
    out.println("   return confirm(\"You requested a reservation for \" + " + parmD.covers + " + \" people.\\n\\nAre you sure you want to submit your reservation with only \" + found + \" people?\");");
    out.println(" } else {");
    out.println("   return true;");
    out.println(" }");
    out.println("}");
    out.println("function goBack() {");
    out.println(" if (confirm('Returning to the previous page will cause you to lose any changes made on this page.\\n\\nAre you sure you wish to return to the previous page?')) {");
    
    if (event && ext_login == false) {
        //out.println("  document.dining_reservation_form.submit();");
        out.println(" ftReturnToCallerPage();");
    } else {
        out.println(" window.history.back(-1);");
       // out.println(" window.location.href='" +goBackURL+ "';");   // where to go back from here????
    }
    
    out.println(" } ");
    out.println("}");
    
    // This function will probably be overriden by foretees-global.js
    out.println("function ftReturnToCallerPage() {");
        out.println(" window.location.href=ftSetJsid('" +goBackURL+ "');");   // go back to Dining_home (view events)
    out.println("}");
    
    out.println("function subletter(x) {");
    out.println(" $(\"#name_list_content\").load(ftSetJsid(\"data_loader?name_list&letter=\" + x));");
    out.println("}");
    out.println("function update_times(loc_id) {");
    out.println(" $(\"#dining_time_list_content\").load(ftSetJsid(\"data_loader?dining_times&date=" + Integer.parseInt(req.getParameter("date")) + "&event_id=" + parmD.event_id + "&location_id=\" + loc_id));");
    out.println("}");
    out.println("function erasename(elem) {");
    out.println(" eval(\"document.dining_reservation_form.\" + elem + \".value = '';\")");
    out.println("}");
    
    out.println("function addname() {");
    out.println(" document.dining_reservation_form.bump_covers.value = '1';");
    out.println(" document.dining_reservation_form.submit();");
    out.println("}");
    
    out.println("function move_name(pName) {");
    out.println(" var f = document.dining_reservation_form;");
    out.println(" var names = new Array();");
    for (int i = 1; i <= parmD.covers; i++) {

        out.println(" names[" + i + "] = f.name_" + i + ".value;");
    }

    out.println(" array = pName.split(':'); ");
    out.println(" var name = array[0];");
    out.println(" var data = array[1];"); // person_id - if absent it's 'undefined' so test and set to zero too
    out.println(" if (isNaN(data)) data = 0;");
    out.println(" skip = 0;");
    

    //
    // check for duplicate names
    out.println(" if ($.trim(name) != 'Guest' && $.trim(name) != 'Child' && name != 'TBD') {"); // skip the dup check for anything that is allowed multiple times
    out.println("  for (i = 1; i <= " + parmD.covers + "; i++) {");
    out.println("   if (names[i] == name) {");                   // if name is already here
    out.println("    skip = 1;");
    out.println("    break;");
    out.println("   }");
    out.println("  }");
    out.println(" }");

    //
    // loop each and place in first empty name box
    out.println(" if (skip == 0) {");
    out.println("  for (i = 1; i <= " + parmD.covers + "; i++) {");
    out.println("   if (names[i] == '') {");                   // if name is empty
    out.println("    eval('f.name_' + i + '.value = name;');");
    out.println("    eval('f.data_' + i + '.value = data;');");
    out.println("    if ($.trim(name) == 'Guest' || $.trim(name) == 'Child') {");
    out.println("     $('#name_' + i).removeAttr('onFocus');");
    out.println("     $('#name_' + i).focus();");
    out.println("    }");
    out.println("    ");
    out.println("    break;");
    out.println("   }");

    out.println("  }");
    out.println(" }"); // end skip

    out.println("}"); // end move_name

    out.println("");

    //out.println("$(document).ready(function() {");
    //out.println(" $(document).loading({onAjax:true, text: 'Loading Names...', effect: 'ellipsis'});");
    //out.println("});");

    //out.println("");

    // only events are canceled from this page - ala carte reservations are deleted from output_form method
    out.println("function cancel_reservation() {");
    out.println(" if(confirm('Are you sure you want to cancel this reservation?') == false) return;");
    out.println(" var form_data = 'foretees_login=XV3';");
    out.println(" form_data += '&organization_id=" + organization_id + "';");
    out.println(" form_data += '&user_id=" + Utilities.getUserId(person_id) + "';");
    out.println(" form_data += '&cancel_id=" + parmD.id + "';");
    out.println(" form_data += '&_method=delete';");
    out.println(" $(document).foreTeesModal(\"pleaseWait\");");
    
    out.println(" $.ajax({");
    out.println("   type: \"POST\", ");
    out.println("   url: \"/v5/dprox.php\", ");
    out.println("   data: form_data, ");
    out.println("   dataType: \"xml\", ");
    //out.println("   beforeSend: function(x) { ");
    //out.println("    if(x && x.overrideMimeType) { ");
    //out.println("     x.overrideMimeType(\"application/xml;charset=UTF-8\"); ");
    //out.println("    }");
    //out.println("   }, ");
    out.println("   success: function(response) {");
    out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
    out.println("     var ok = $('success', response).text();");
    out.println("     if(ok == '1'){");
  //out.println("       alert('You have been removed from the event reservation.');");
    out.println("       $('#message').html(\"<h2>You Have Been Removed</h2>\")");
    out.println("       .fadeIn(1500, function() {");
    out.println("           $('#message').append(\"Please Wait...\");");
    out.println("           ftReturnToCallerPage();");
    //out.println("           window.location.href='Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID + "';");
    out.println("       });");
    out.println("     }else{");
    out.println("       $('error', response).each(function(id){");
    out.println("           if(id==0)$('#message').html('<h2>Error Encountered</h2>');");
    out.println("           var errMsg = $(response).find('error').eq(id).text();");
    out.println("           $('#message').append(errMsg+'<br>');");
    out.println("           $('#cancel').removeAttr('disabled');");
    out.println("       })");
    out.println("     }");
    out.println("   }, ");
    out.println("   error: function(xhr, ajaxOptions, thrownError) {");
    out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
    out.println("     $('#message').html(\"<h2>Unexpected Error</h2>\")");
    out.println("     $('#message').append(\"Error: \" + thrownError);");
    out.println("     $('#cancel').removeAttr('disabled');");
    out.println("   }");
    out.println(" });");
    //out.println(" $('#cancel').attr('disabled', '');");
    out.println(" return false;");
    out.println("}"); // end cancel_reservation

    // remove just the current person from reservation
    out.println("function cancel_related(res_id, obj) {");
    //out.println(" var data = {foretees_login:'XV3',organization_id:'" + organization_id + "',user_id:'" + Utilities.getUserId(person_id) + "',cancel_id:res_id,new_master_id:'this','_method':'delete'}");
    out.println(" var data = {"
            + "foretees_login:'XV3',"
            + "organization_id:'" + organization_id + "',"
            + "user_id:'" + Utilities.getUserId(person_id) + "',"
            + "reservation_id:res_id,"
            + "new_master_id:'this'}");

    out.println(" ftRemoveReservationPrompt(data, obj);");
    
    out.println("}");

    out.println("</script>");
 }


 private void verify_form(int id, int event_id, String user, String club, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {


    //
    // ADD CLUBS HERE THAT WANT EMAIL COPIES OF ALL RESERVATIONS AS THEY ARE SUBMITTED TO THE DINING SERVER (don't foget to add the email addy above in post)
    // 
    boolean useEmailVerification = (club.equals("mirabel") || club.equals("denvercc") || club.equals("peninsula") || club.equals("hyperion") || club.equals("ccyork"));

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    boolean ext_login = false;            // not external login (from email link)
    boolean event = (event_id != 0);
    
    //boolean require_check_numbers = true; // eventually get this from organizations table in postgres
    boolean hide_check_numbers = (club.equals("misquamicut"));

    int person_id = Utilities.getPersonId(user, con);
    int organization_id = Utilities.getOrganizationId(con);

    if (req.getParameter("ext_login") != null) {   // if originated from Login for an external login user
       
       ext_login = true;
    }

    String errMsg = "";
    String orig = "";

    if (req.getParameter("orig") != null) {

       orig = req.getParameter("orig");    //  Get the origin of this call (from Calendar or from Rservation Link)
    }

    parmDining parmD = new parmDining();

    // populate parmD from the request object
    parmD.id = id;
    parmD.event_id = event_id;
    parmD.organization_id = organization_id;
    
    parmD.populate(parmD, req, con_d);


    // extract the cost for the event
    parmDiningCosts parmCosts = new parmDiningCosts();
    parmCosts.costs = parmD.costs;

    if ( !parmCosts.parseCosts() ) {

        out.println("<!--  FATAL ERROR: " + parmCosts.err_string + " -->");
        out.println("<!--  FATAL ERROR: " + parmCosts.err_message + " -->");

    }

    out.println("<!-- reservation[location_id]=" + parmD.location_id + " -->");
    out.println("<!-- reservation[occasion_id]=" + parmD.occasion_id + " -->");

    
    // check for time and location - required
    
    if (parmD.time == 0) {
        
        errMsg = "The time of day is missing. &nbsp;You must select a time from the options provided.";
        displayError(errMsg, club, out, req);
        return;
        
    } else if (parmD.location_name == null || parmD.location_name.equals("")) {
        
        errMsg = "The location is missing. &nbsp;You must select a location from the options provided.";
        displayError(errMsg, club, out, req);
        return;
    }


    // first we need to load the existing people in the res from the db
    // then compare that list to the current list of names so that we
    // make sure we check and new names added to the reservation

    //
    // if this is a new reservtion then check to see if any members have conflicting reservations
    //
    if (true || parmD.id == 0) {

        int conflict_player = 0;
        int conflict_time = 0;
        
        loop:
        for (int i = 1; i <= parmD.names_found; i++) {

            if (!parmD.names[i].equals("") && parmD.dining_id[i] != 0) {

                // name is here and it's a member (not guest,tbd, staff, etc.)
                // check for conflicting reservations
                // ignore event or ala carte and just look at the minutes between
                try {

                    /*
                                "ABS(" +
                                    "DATE_PART('hour', time::time - '" + parmD.time + "'::time) * 60 + " +
                                    "DATE_PART('minute', time::time - '" + parmD.time + "'::time)" +
                                ") AS minutes_diff " + */

                    String tmp_sql = "" +
                            "SELECT to_char(time, 'HH24MI') AS conflict_time " +
                            "FROM reservations " +
                            "WHERE " +
                                "organization_id = ? AND " +
                                "person_id = ? AND " +
                                "to_char(date, 'YYYYMMDD')::int = ? AND " +
                                "state <> 'cancelled' AND ";

                    if (id != 0) {

                        tmp_sql += "(reservation_number <> ?) AND "; // "(id <> ? AND parent_id <> ?) AND ";
                    }

                    String pg_time = (parmD.time < 1000) ? "0" + parmD.time : "" + parmD.time;

                    if (!event) {

                        // ala carte - just check for ANY existing reservation within # minutes
                        tmp_sql += "ABS(" +
                                    "DATE_PART('hour', time::time - '" + pg_time + "'::time) * 60 + " +
                                    "DATE_PART('minute', time::time - '" + pg_time + "'::time)" +
                                ") < ? " +
                                "LIMIT 1";
                    } else {

                        // event - check for any reservation for this event OR any reservation within # minutes
                        tmp_sql += "(ABS(" +
                                    "DATE_PART('hour', time::time - '" + pg_time + "'::time) * 60 + " +
                                    "DATE_PART('minute', time::time - '" + pg_time + "'::time)" +
                                ") < ? OR (category = 'event' AND event_id = ?)) " +
                            "LIMIT 1";
                    }

                    if (Common_Server.SERVER_ID == 4) {
                        
                        
                        out.println("<!-- " + tmp_sql + " -->");
                        out.println("<!-- parmD.organization_id=" + parmD.organization_id + " -->");
                        out.println("<!-- parmD.dining_id[" + i + "]=" + parmD.dining_id[i] + " -->");
                        out.println("<!-- parmD.date=" + parmD.date + " -->");
                        out.println("<!-- parmD.reservation_number=" + parmD.reservation_number + " -->");
                    }

                    pstmt = con_d.prepareStatement (tmp_sql);

                    //pstmt.setInt(1, parmD.id);
                    //pstmt.setInt(2, parmD.id);

                    pstmt.setInt(1, parmD.organization_id);
                    pstmt.setInt(2, parmD.dining_id[i]);
                    pstmt.setInt(3, parmD.date);

                    if (id != 0) {

                        pstmt.setInt(4, parmD.reservation_number);
                        pstmt.setInt(5, 120); // at least 2 hours between reservations
                        if (event) pstmt.setInt(6, parmD.event_id);

                    } else {

                        pstmt.setInt(4, 120); // at least 2 hours between reservations
                        if (event) pstmt.setInt(5, parmD.event_id);

                    }

                    rs = pstmt.executeQuery();

                    if ( rs.next() ) {

                        // found a conflicting reservation
                        conflict_player = i;
                        conflict_time = rs.getInt("conflict_time");

                        // debug
                        out.println("<!-- CONFLICTING RESERVATION FOUND FOR " + parmD.names[conflict_player] + " (" + parmD.dining_id[conflict_player] + ")  AT " + conflict_time + " -->");

                        break;


                        /*if (rs.getInt("minutes_diff") > 120) {

                            player_found = i;
                            existing_time = rs.getInt(1);
                            break;

                        } else {

                            out.println("<!-- RESERVATIONS FOUND FOR " + rs.getInt(1) + " which is within " + rs.getInt(2) + " minutes of " + parmD.time + " -->");

                        }*/

                    } else {

                        // debug
                        out.println("<!-- NO RESERVATIONS FOUND FOR " + parmD.names[i] + " (" + parmD.dining_id[i] + ") around " + parmD.time + " -->");
                    }

                } catch (Exception exc) {

                    Utilities.logError("Dining_slot - Error looking for existing resrvations: Err=" + exc.toString());

                } finally {

                    try { rs.close(); }
                    catch (Exception ignore) {}

                    try { pstmt.close(); }
                    catch (Exception ignore) {}

                }

            } // end if person is a member

        } // end loop of all people in reservation


        if (conflict_player != 0) {

            //errMsg = parmD.names[player_found] + " is already scheduled to " + ((event) ? "participate in the event" : "dine at the resteraunt this day.") + "";
            //displayError(errMsg, club, out);

            out.println("<div class=\"reservations\">");

            //
            // output bread crumb trail
            //
            Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Error", req);
            Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
            //out.println("  <BR><BR>");

            out.println("<div class=\"preContentFix\"></div>"); // clear the float

            out.println("<div id=\"reservation_action_title\">Conflict Found</div>");

            out.println("<div class=\"main_warning\">" +
                    "<strong>A Conflicting Reservations Was Found</strong><BR><BR>  " +
                    "Sorry, but " + parmD.names[conflict_player] + " is already scheduled to " +
                    ((event) ? "participate in this event" : "dine at the restaurant this day at " + Utilities.getSimpleTime(conflict_time)) + ".<BR><BR>" +
                    "Please use the 'Go Back' button to return to the previous screen and adjust the reservation accordingly.</div>");

            // display the go back button
            out.println("<form><div style=\"text-align:center;\">");
            out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false\">");
            out.println("</div></form>");

            out.println("</div>"); // close reservations div

            //
            // Scripts
            //
            out.println("<script type=\"text/javascript\">");
            out.println("function goBack() {");
            out.println("  window.history.back(-1);");
            out.println("}");
            
            // This function will probably be overriden by foretees-global.js
            out.println("function ftReturnToCallerPage() {");
            out.println("  window.location.href='Dining_home';");
            out.println("}");
            
            out.println("</script>");

            return;

        } else {

            // debug
            out.println("<!-- NO CONFLICTING RESERVATIONS FOUND -->");

        }

    } // end if new reservation / check for existing reservations


    // check for questions and required answers first
    //if (!checkForAnswers(reservation_id, event_id, user, club, req, out, con, con_d)) {
    if (parmD.questionsToAsk(parmD) && missingAnswers(parmD, user, req, out, con_d)) { // parmD.question_count > 0

        // false - missing answers
        //displayQuestions(reservation_id, event_id, user, club, req, out, con, con_d);
        displayQuestions(parmD, user, club, req, out, person_id, con_d);
        //return;

    } else {

        final String br = "\r\n";
        StringBuffer ev = new StringBuffer();
        String ev_subject = "";
        
/*
        // out debug info if here for an event
        if (event) {
            out.println("<!-- price_categoryA.length=" + parmCosts.price_categoryA.length + " -->");
            out.println("<!-- costs_found=" + parmCosts.costs_found + " -->");

            for (int i = 0; i < parmCosts.price_categoryA.length; i++) {
                out.println("<!-- parmCosts.price_categoryA[" + i + "]=" + parmCosts.price_categoryA[i] + " -->");
            }

            //out.println("<!-- index for Guests=" + parmCosts.findIndex(parmCosts, "Guests") + " -->");
            //out.println("<!-- index for Members=" + parmCosts.findIndex(parmCosts, "Members") + " -->");
        }
        out.println("<!-- question_count=" + parmD.question_count + " -->");
        out.println("<!-- questionsToAsk=" + parmD.questionsToAsk(parmD) + " -->");
        out.println("<!-- missingAnswers=" + missingAnswers(parmD, user, req, out, con_d) + " -->");
*/

        //
        // Output our hidden iframe for our rs calls
        //
        //out.println("<iframe id=\"RSIFrame\" name=\"RSIFrame\" style=\"width:20px; height:20px; border: 1px\" src=\"about:blank\"></iframe>");


        String reservation = (event) ? "Event Reservation" : "Reservation";
        String title = (parmD.id > 0) ? "Modify Existing " + reservation : "Create New " + reservation;


        //
        //  Begin the html output
        //
        out.println("<div class=\"reservations\">");


        //
        // output bread crumb trail
        //
        Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Dining " + reservation, req);
        Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
        //out.println("  <BR><BR>");
        /*
        if (orig.equals("calendar")) {

            out.println("<div id=\"breadcrumb\">Home / Dining Calendar / Dining " + reservation + " / Confirm</div>");

        } else {

            out.println("<div id=\"breadcrumb\">Home / Dining " + reservation + " / Confirm</div>");

        }
        */

        out.println("<div class=\"preContentFix\"></div>"); // clear the float

        out.println("<div id=\"reservation_action_title\">" + title + "</div>");

        ev.append("Action: ");
        ev.append(title);
        ev.append(br);


        //
        // output a warning message if modifying
        //
        if (parmD.id != 0) out.println("<div class=\"main_warning\">" +
                "<strong>Warning</strong>:  " +
                "You have <strong>6 minutes</strong> to complete this reservation. " +
                "If you want to return without completing a reservation, <strong>do not use your browser's BACK</strong> button/option. " +
                "Instead select the <strong>Go Back</strong> option below.</div>");


        //
        // output the instructions
        //
        //out.println("<div class=\"sub_instructions\"><strong>Instructions</strong>:  This is where we can tell the user how to complete the reservation.</div>");


        out.print("<div id=\"tt1_left\">");
        out.print("Date:  <strong>" + parmD.day_of_week + " " + parmD.sdate + "</strong> &nbsp;&nbsp;&nbsp;<strong>" + parmD.location_name + "</strong> &nbsp;&nbsp;&nbsp;Time: <strong>" + Utilities.getSimpleTime(parmD.time) + "</strong>");
        out.println("</div>");

        ev.append("Date: ");
        ev.append(parmD.day_of_week);
        ev.append(" ");
        ev.append(parmD.sdate);
        ev.append(br);

        ev.append("Time: ");
        ev.append(Utilities.getSimpleTime(parmD.time));
        ev.append(br);

        // build subject line of email if it'll be needed
        if (useEmailVerification) {

            ev_subject = parmD.sdate + " @ " + Utilities.getSimpleTime(parmD.time) + " - " + parmD.location_name + " for " + parmD.names[1];
            if (parmD.names_found > 1) ev_subject += " + " + (parmD.names_found - 1);

        }

        out.println("<div class=\"preContentFix\"></div>"); // clear the float


        out.println("<div class=\"sub_main\">");

            out.println("<strong>Confirm Reservation " + ((parmD.id != 0) ? "Changes" : "Details") + "</strong>");

            out.println("<div id=\"res_confirm\">");

        //
        // display the details
        //out.println("<div class=\"res_qa\">");


        out.println("<table>");

        if (event) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Event Name:</td>");
            out.println("<td class=\"detail\">" + parmD.event_name + "</td>");
            out.println("</tr>");

            out.println("<tr>");
            out.println("<td class=\"detail_name\" nowrap>Event Location:</td>");
            out.println("<td class=\"detail\">" + parmD.location_name + "</td>"); // was event_location_name
            out.println("</tr>");


            ev.append("Event Name: ");
            ev.append(parmD.event_name);
            ev.append(br);
        }

        out.println("<tr>");
        out.println("<td class=\"detail_name\">Date:</td>");
        out.println("<td class=\"detail\">" + parmD.day_of_week + " " + parmD.sdate + "</td>");
        out.println("</tr>");

        out.println("<tr>");
        out.println("<td class=\"detail_name\">Time:</td>");
        out.println("<td class=\"detail\">" + Utilities.getSimpleTime(parmD.time) + "</td>");
        out.println("</tr>");

        if (!event) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Location:</td>");
            out.println("<td class=\"detail\" nowrap>" + parmD.location_name + "</td>");
            out.println("</tr>");

        }

        ev.append("Location: ");
        ev.append(parmD.location_name);
        ev.append(br);


        // output names
        for (int i = 1; i <= parmD.covers; i++) {

            if (!parmD.names[i].equals("")) {

                out.println("<tr>");
                out.println("<td class=\"detail_name\">Name <span class=\"info\">#" + i + "</span>:</td>");
                out.println("<td class=\"detail\" nowrap>");
                out.println(parmD.names[i] + "&nbsp;&nbsp;");
                out.println("<span class=\"info\">");

                ev.append("Person #" + i + ": ");
                ev.append(parmD.names[i]);

                if (event) {
                    try {
                        int cost_id = parmCosts.findIndex(parmCosts, parmD.price_category[i]);
                        out.println(parmD.price_category[i] + " $" + parmCosts.costA[cost_id] + "&nbsp;&nbsp;");

                        ev.append(", ");
                        ev.append(parmD.price_category[i]);

                        out.println("<!-- cost_id=" + cost_id + " parmD.price_category[i]=" + parmD.price_category[i] + " -->");
                        out.println("<!-- parmD.price_category[i]=" + parmD.price_category[i] + " -->");
                        out.println("<!-- parmCosts.costA[cost_id]=" + parmCosts.costA[cost_id] + " -->");

                    } catch (Exception exc) {
                        out.println("<!-- ERROR displaying event cost options. err=" + exc.toString() + " -->");
                    }
                }

                // maybe we hide the check numbers if they are all the same
                if (!hide_check_numbers) out.println("Check # " + parmD.check_num[i]);

                ev.append(", Check #");
                ev.append(parmD.check_num[i]);
                ev.append(br);

                out.println("</span>");
                out.println("</td>");
                out.println("</tr>");

            }
        }

        if (!event && parmD.occasion_id != 0) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Occasion:</td>");
            out.println("<td class=\"detail\">" + parmD.occasion + "</td>");
            out.println("</tr>");

            ev.append("Occasion:");
            ev.append(parmD.occasion);
            ev.append(br);
        }

        if (!parmD.member_special_requests.equals("")) {

            out.println("<tr>");
            out.println("<td class=\"detail_name\">Special Requests:</td>");
            out.println("<td class=\"detail\" style=\"white-space:normal\">" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) + "</td>");
            out.println("</tr>");

            ev.append("Special Requests:");
            ev.append(StringEscapeUtils.escapeHtml(parmD.member_special_requests));
            ev.append(br);
        }

        out.println("<tr>");
        out.println("<td>&nbsp;</td><td>&nbsp;</td>");
        out.println("</tr>");

        out.println("</table>");

        out.println("</div>");


        String time = "";
        int hr = parmD.time / 100;
        int min = parmD.time - (hr * 100);
        time = "" + hr + ":" + Utilities.ensureDoubleDigit(min);



        if (parmD.id == 0) {

            if (!event) {

                // ala carte
                out.println("<form action=\"http://dining.foretees.com/self_service/dining_reservations.xml\" id=\"new_dining_reservation_form\" method=\"post\" target=\"RSIFrame\" onsubmit=\"check_status()\">");

            } else {

                // event
                out.println("<form action=\"http://dining.foretees.com/self_service/event_reservations.xml\" id=\"new_event_reservation_form\" method=\"post\" target=\"RSIFrame\" onsubmit=\"check_status()\">");

            }

        } else {

            if (!event) {

                // ala carte
                out.println("<form action=\"http://dining.foretees.com/self_service/dining_reservations/" + parmD.id + ".xml\" id=\"new_dining_reservation_form\" method=\"post\" target=\"RSIFrame\" onsubmit=\"check_status()\">");
                out.println(" <input type=\"hidden\" name=\"reservation_id\" value=\"" + parmD.id + "\">");
                out.println(" <input type=\"hidden\" name=\"_method\" value=\"put\">");

            } else {

                // event
                out.println("<form action=\"http://dining.foretees.com/self_service/event_reservations/" + parmD.id + ".xml\" id=\"new_event_reservation_form\" method=\"post\" target=\"RSIFrame\" onsubmit=\"check_status()\">");
                out.println(" <input type=\"hidden\" name=\"reservation_id\" value=\"" + parmD.id + "\">");
                out.println(" <input type=\"hidden\" name=\"_method\" value=\"put\">");

            }
        }

        out.println(" <input type=\"hidden\" name=\"foretees_login\" value=\"XV3\">");
        out.println(" <input type=\"hidden\" name=\"organization_id\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"user_id\" value=\"" + Utilities.getUserId(person_id) + "\">");
        if (event) {
            out.println(" <input type=\"hidden\" name=\"reservation[event_id]\" value=\"" + parmD.event_id + "\">");
        }
        out.println(" <input type=\"hidden\" name=\"reservation[organization_id]\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[person_id]\" value=\"" + person_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[category]\" value=\"" + ((event) ? "event" : "dining") + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[date]\" value=\"" + parmD.sdate + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[reservation_time]\" value=\"" + time + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_special_requests]\" value=\"" + StringEscapeUtils.escapeHtml(parmD.member_special_requests) + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[occasion_id]\" value=\"" + parmD.occasion_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[location_id]\" value=\"" + parmD.location_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[email_user]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_created]\" value=\"true\">");
        out.println(" <input type=\"hidden\" name=\"reservation[number_of_checks]\" value=\"" + parmD.number_of_checks + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[covers]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[reservee_category]\" value=\"member\">");

        if (event) {

            //out.println(" <input type=\"hidden\" name=\"charges[1]\" value=\"1\">"); // force this to 1 since they have to specify the names of all others

            int cost_id = parmCosts.findIndex(parmCosts, parmD.price_category[1]);


            out.println("<!-- " + cost_id + " -->");
            out.println(" <input type=\"hidden\" name=\"charges[0][covers]\" value=\"1\">");
            out.println(" <input type=\"hidden\" name=\"charges[0][price_category]\" value=\"" + parmD.price_category[1] + "\">");    // A single price category from the associated event
            out.println(" <input type=\"hidden\" name=\"charges[0][price_type_id]\" value=\"" + parmCosts.price_typeA[cost_id] + "\">");     // PRICE_TYPE_ID_FROM_ASSOCIATED_EVENT_COST
            out.println(" <input type=\"hidden\" name=\"charges[0][price]\" value=\"" + parmCosts.costA[cost_id] + "\">");             // MONETARY_VALUE_FROM_ASSOCIATED_EVENT_COST

        }


        if (hide_check_numbers || parmD.covers == 1) {
            out.println(" <input type=\"hidden\" name=\"reservation[check_number]\" value=\"1\">");     // set the chk# for the member making the res
        } else {
            out.println(" <input type=\"hidden\" name=\"reservation[check_number]\" value=\"" + parmD.check_num[1] + "\">");
        }


        // add other names
        out.println(" <input type=\"hidden\" name=\"has_related_reservations\" value=\"" + ((parmD.covers > 1) ? "yes" : "no") + "\">");
        if (parmD.covers > 1) {

            int used_i = 1; // needs to start with var i in the for loop below

            for (int i = 2; i <= parmD.covers; i++) {

                if (!parmD.names[i].equals("")) {

                    out.println(" <input type=\"hidden\" name=\"full_name_" + used_i + "\" value=\"" + parmD.names[i] + "\">");
                    //out.println(" <input type=\"hidden\" name=\"name_" + used_i + "\" value=\"" + parmD.names[i] + "\">");
                    //out.println(" <input type=\"hidden\" name=\"data_" + used_i + "\" value=\"" + parmD.dining_id[i] + "\">");
                    out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][check_number]\" value=\"" + parmD.check_num[i] + "\">");
                    out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][user_identity]\" value=\"" + parmD.getUserIdentity(parmD.dining_id[i], con_d) + "\">");
                    out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][reservee_category]\" value=\"" + parmD.reservee_category[i] + "\">"); // member|reciprocal|guest|non_member

                    if (event) {

                        out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][price_category]\" value=\"" + parmD.price_category[i] + "\">");    // A single price category from the associated event
                        out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][charges]\" value=\"1\">");

                        //out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][charges]\" value=\"" + parmD.reservee_category[i] + "\">"); // member|reciprocal|guest|non_member
                        //out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][covers]\" value=\"1\">");
                        //out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][price_type_id]\" value=\"1\">");     // PRICE_TYPE_ID_FROM_ASSOCIATED_EVENT_COST

                    } else {

                        out.println(" <input type=\"hidden\" name=\"related_reservations[" + used_i + "][covers]\" value=\"1\">");
                    }

                    used_i++;
                }
            }

        }



        // debug
        out.println("<!-- Q&A RESULTS-->");
        for (int i = 1; i <= parmD.question_count; i++) {

                out.println("");
                out.println("<!-- question #" + i + " -->");

            for (int i2 = 1; i2 <= parmD.covers; i2++) {
                out.println("");
                out.println(" <!-- question #" + i + " person #" + i2 + " -->");
                out.println(" <!-- answers[" + i + "][question_id] = " + parmD.question_id[i] + " -->");
                out.println(" <!-- answers[" + i + "][question_text] = " + parmD.question_text[i] + " -->");
                out.println(" <!-- answers[" + i + "][answer_text] = " + parmD.answers[i2][i] + " -->");
            }
        }





        //
        // Display dining party Q&A
        //
        boolean did_header = false;
        int a = 0;

        // now display the questions
        for (int i = 1; i <= parmD.question_count; i++) {

            // only process questions that are for the entire party
            if (parmD.question_for_whole_party[i] == 1) {

                // if question is guest only then only ask question if the dining party contains at least one guest
                if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.has_guests)) {

                    if (!did_header) {

                        out.println("<div class=res_qa>");

                        out.println("<strong>Questions for dining party</strong>");
                        did_header = true;
                    }

                    // this question only needs to be asked once and is for the entire dining party
                    // in this case we will assign the answers to the primary reservation holder
                    out.println("<table id=\"res_qa_table\">");

                    // present the questiodn
                    out.println("<tr><td class=\"question\" colspan=\"2\">" + parmD.question_text[i] + "</td>");


                    //out.println("<td><input type=\"text\" name=\"related_reservations[1]answers[" + i + "]\" value=\"\"></td>");  // " + parmD.answer[i] + "
                    out.println("<td>");
                    out.println("" + ((parmD.answers[1][i].equals("") ? "<i>Unanswered</i>" : parmD.answers[1][i])) + "");
                    out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                    out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                    out.println("<input type=\"hidden\" name=\"answers[" + i + "][answer_text]\" value=\"" + parmD.answers[1][i] + "\">");
                    out.println("</td>");

                    out.println("</tr></table>");

                } else { out.println("<!-- QUESTION IS FOR GUESTS BUT NO GUESTS FOUND -->"); }

            }

        }

        if (did_header) out.println("</div>"); // close res_qa div

        out.println("<br>");

        //
        // Display individual Q&A
        //
        did_header = false;
        for (int i = 1; i <= parmD.question_count; i++) {

            out.println("<!-- loop 2 processing question #" + i + ", has_guests=" + parmD.has_guests + " -->");

            // only ask if question is for each individual AND
            // is either not a guest question or is a guest question and there is at least one guest
            if ( parmD.question_for_whole_party[i] == 0 && (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.has_guests)) ) {

                if (!did_header) {

                    out.println("<div class=res_qa>");

                    out.println("<strong>Questions for each individual</strong>");
                    did_header = true;
                }

                out.println("<table id=\"res_qa_table\">");

                // present the question
                out.println("<tr><td class=\"question\" colspan=\"2\">" + parmD.question_text[i] + "</td></tr>");

                // display one answer box for each cover being asked
                for (int i2 = 1; i2 <= parmD.names_found; i2++) {

                    out.println("<tr>");

                    // if question is for guests only then only ask if this person is not a member
                    if (parmD.question_guest_only[i] == 0 || (parmD.question_guest_only[i] == 1 && parmD.dining_id[i2] == 0)) {

                        out.println("<td class=\"names\" nowrap>" + parmD.names[i2] + "</td>");
                        //out.println("<td><input type=\"text\" name=\"related_reservations[" + i2 + "]answers[" + i + "]\" value=\"\">");  // " + parmD.answer[i] + "
                        out.println("<td>");
                        //out.println("" + parmD.answers[i2][i] + "");
                        out.println("" + ((parmD.answers[i2][i].equals("") ? "<i>Unanswered</i>" : parmD.answers[i2][i])) + "");
                        if(i2 == 1) {
                            // primary person so don't use related_reservation prefix and exclude the reservation id
                            out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                            out.println("<input type=\"hidden\" name=\"answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                            out.println("<input type=\"hidden\" name=\"answers[" + i + "][answer_text]\" value=\"" + parmD.answers[i2][i] + "\">");
                        } else {
                            out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][question_id]\" value=\"" + parmD.question_id[i] + "\">");
                            out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][reservation_id]\" value=\"" + parmD.id + "\">");
                            out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][question_text]\" value=\"" + parmD.question_text[i] + "\">");
                            out.println("<input type=\"hidden\" name=\"related_reservations[" + i2 + "]answers[" + i + "][answer_text]\" value=\"" + parmD.answers[i2][i] + "\">");
                        }
                        out.println("</td>");

                    }

                    out.println("</tr>");

                } // end covers loop

                out.println("</table>");

            } // end if we are asking this question

        } // end question loop

        if (did_header) out.println("</div>"); // close res_qa div






        out.println(" <input type=\"hidden\" name=\"next_related_reservation\" value=\"5\">");

        //out.println(" <input type=\"submit\" name=\"commit_sub\" value=\"Reserve\">");

        out.println("<div id=\"message\" style=\"text-align:center;font-weight:bold\">");
        out.println("</div>");

        //
        // display the go back and submit buttons
        out.println("<div style=\"text-align:center;\">");
        out.println(" <input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false\">");
      //out.println(" <input id=\"commit_sub\" name=\"commit_sub\" type=\"submit\" value=\"Confirm\" onclick=\"submit_reservation(); return false\">"); //
        out.println(" <input id=\"commit_sub\" name=\"commit_sub\" type=\"button\" value=\"Confirm\" onclick=\"submit_reservation()\">"); //
        out.println("</div>");

        out.println("</form>");


        out.println("<p>&nbsp;</p>");

        out.println("</div>"); // close res_select
        out.println("</div>"); // close sub_main

        out.println("</div>"); // close reservations (was main_message) div

    /*
        out.println("<div id=contact_form>");
        out.println("CONTACT FORM");
        out.println("</div>");
    */


        // debug
        for (int i = 1; i <= parmD.covers; i++) {
            out.println("<!-- parmD.related_id["+i+"] = " + parmD.related_id[i] + " -->");
        }


        if (useEmailVerification) {

            out.println("<iframe id=\"ifEV\" name=\"ifEV\" style=\"width:1px;height1px;\" width=\"1\" height=\"1\"></iframe>");
            out.println("<form action=\"Dining_slot\" method=\"post\" id=\"frmEV\" target=\"ifEV\">");
            out.println("<input type=\"hidden\" name=\"ev\" value=\"1\">");
            out.println("<input type=\"hidden\" name=\"subject\" value=\"" + StringEscapeUtils.escapeHtml(ev_subject) + "\">");
            out.println("<input type=\"hidden\" name=\"msg\" value=\"" + StringEscapeUtils.escapeHtml(ev.toString()) + "\">");
            out.println("</form>");
        }


        //
        // Scripts
        //
        out.println("<script type=\"text/javascript\">");

        //out.println("document.domain='foretees.com';");

        out.println("function goBack() {");
      //out.println(" if (confirm('Are you sure you wish to return to the previous page?')) {");
        out.println("  window.history.back(-1);");
      //out.println(" }");
        out.println("}");
        
        // This function will probably be overriden by foretees-global.js
        out.println("function ftReturnToCallerPage() {");
        out.println(" window.location.href=ftSetJsid('Dining_home');");
        out.println("}");

        out.println("function submit_reservation() {");

      //out.println(" var f = document.dining_reservation_form;");
        out.println(" var form_data = 'foretees_login=XV3';");
        out.println(" form_data += '&organization_id=" + organization_id + "';");

        // user id of the person we are authenticating as
        out.println(" form_data += '&user_id=" + Utilities.getUserId(person_id) + "';");

        // if updating existing then add reservation id and _method=put
        if (parmD.id != 0) {

            out.println(" form_data += '&reservation_id=" + parmD.id + "';");
          //out.println(" form_data += '&event_id=" + parmD.event_id + "';");
            out.println(" form_data += '&_method=PUT';");
        }

        if (event) {
            out.println(" form_data += '&reservation[event_id]=" + parmD.event_id + "';");
          //out.println(" form_data += '&reservation[id]=" + parmD.id + "';");
        }

        out.println(" form_data += '&reservation[organization_id]=" + organization_id + "';");
        out.println(" form_data += '&reservation[reservee_category]=member';");
        out.println(" form_data += '&reservation[person_id]=" + person_id + "';");
        out.println(" form_data += '&reservation[category]=" + ((event) ? "event" : "dining") + "';");
        out.println(" form_data += '&reservation[date]=" + parmD.sdate + "';");
        out.println(" form_data += '&reservation[reservation_time]=" + time + "';");
        out.println(" form_data += '&reservation[member_special_requests]=" + StringEscapeUtils.escapeHtml(parmD.member_special_requests).replace("\r\n","%20").replace("'","\\'").replace("&", "%26").replace(" ", "%20") + "';");
        out.println(" form_data += '&reservation[occasion_id]=" + parmD.occasion_id + "';");
        out.println(" form_data += '&reservation[location_id]=" + parmD.location_id + "';");
        out.println(" form_data += '&reservation[email_user]=1';");

        out.println(" form_data += '&reservation[number_of_checks]=" + parmD.number_of_checks + "';");

        if (hide_check_numbers || parmD.covers == 1) {
            out.println(" form_data += '&reservation[check_number]=1';");     // set the chk# for the member making the res
        } else {
            out.println(" form_data += '&reservation[check_number]=" + parmD.check_num[1] + "';");
        }

        out.println(" form_data += '&reservation[covers]=1';");
        out.println(" form_data += '&reservation[member_created]=true';");

      //out.println(" form_data += '&reservation[confirmation_email]=';");


        if (event) {

            int cost_id = parmCosts.findIndex(parmCosts, parmD.price_category[1]);

            //out.println("<!-- " + cost_id + " -->");
            out.println(" form_data += '&charges[0][covers]=1';");
            out.println(" form_data += '&charges[0][price_category]=" + parmD.price_category[1].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");
            out.println(" form_data += '&charges[0][price_type_id]=" + parmCosts.price_typeA[cost_id] + "';");
            out.println(" form_data += '&charges[0][price]=" + parmCosts.costA[cost_id] + "';");

        }

        // add other names
        out.println(" form_data += '&has_related_reservations=" + ((parmD.names_found > 1) ? "yes" : "no") + "';");


        // output answers for booking member (answers[] array in form is zero based)
        for (int q = 1; q <= parmD.question_count; q++) {

            out.println(" form_data += '&answers[" + (q - 1) + "][question_id]=" + parmD.question_id[q] + "';");
            if (parmD.id != 0) out.println(" form_data += '&answers[" + (q - 1) + "][id]=" + parmD.related_id[q] + "';");
            out.println(" form_data += '&answers[" + (q - 1) + "][question_text]=" + parmD.question_text[q].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");
            out.println(" form_data += '&answers[" + (q - 1) + "][answer_text]=" + parmD.answers[1][q].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");
        }

        // if there are related reservations
        if (parmD.names_found > 1) {

            int i2 = 1; // index for use in form

            // outout each of the related reservations (1st relsated is stored in names[2])
            for (int i = 2; i <= parmD.names_found; i++) {

                if (!parmD.names[i].equals("")) {

                    //if (parmD.dining_id[i] != 0) user_identity = parmD.getUserIdentity(parmD.dining_id[i], con_d);

                    out.println(" form_data += '&full_name_" + i2 + "=" + parmD.names[i].replace("'", "\\'") + "';");
                    if (parmD.id != 0) out.println(" form_data += '&related_reservations[" + i2 + "][id]=" + parmD.related_id[i] + "';");
                    out.println(" form_data += '&related_reservations[" + i2 + "][reservee_category]=" + parmD.reservee_category[i] + "';");
                    out.println(" form_data += '&related_reservations[" + i2 + "][user_identity]=" + parmD.getUserIdentity(parmD.dining_id[i], con_d).replace("'", "\\'") + "';");
                    out.println(" form_data += '&related_reservations[" + i2 + "][covers]=1';");
                    out.println(" form_data += '&related_reservations[" + i2 + "][check_number]=" + ((hide_check_numbers) ? "1" : parmD.check_num[i]) + "';");

                    if (event) {

                        out.println(" form_data += '&related_reservations[" + i2 + "][price_category]=" + parmD.price_category[i].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");
                        out.println(" form_data += '&related_reservations[" + i2 + "][charges][1]=1';");

                    } else {

                        // already specified above
                        //out.println(" form_data += '&related_reservations[" + i2 + "][covers]=1';");
                    }

                    for (int q = 1; q <= parmD.question_count; q++) {

                        if (parmD.question_for_whole_party[q] == 1) {
    /*
                            if (parmD.question_guest_only[q] == 0 || (parmD.question_guest_only[q] == 1 && parmD.has_guests)) {

                                out.println(" form_data += '&answers[" + q + "][question_id]=" + parmD.question_id[q] + "';");
                                out.println(" form_data += '&answers[" + q + "][question_text]=" + parmD.question_text[q] + "';");
                                out.println(" form_data += '&answers[" + q + "][answer_text]=" + parmD.answers[1][q] + "';");

                            }
    */
                        } else {

                            if (parmD.question_guest_only[q] == 0 || (parmD.question_guest_only[q] == 1 && parmD.dining_id[i] == 0)) {

                                out.println(" form_data += '&related_reservations[" + i2 + "][answers][" + (q - 1) + "][question_id]=" + parmD.question_id[q] + "';");
                                // maybe only include this item is id is not zero?
                                if (parmD.id != 0) out.println(" form_data += '&related_reservations[" + i2 + "][answers][" + (q - 1) + "][reservation_id]=" + parmD.related_id[i] + "';");
                                out.println(" form_data += '&related_reservations[" + i2 + "][answers][" + (q - 1) + "][question_text]=" + parmD.question_text[q].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");
                                out.println(" form_data += '&related_reservations[" + i2 + "][answers][" + (q - 1) + "][answer_text]=" + parmD.answers[i][q].replace("'", "\\'").replace("&", "%26").replace(" ", "%20") + "';");

                            }

                        }

                    } // end question loop

                    i2++;
                } // end if name if present

            } // end loop of covers

            out.println(" form_data += '&next_related_reservation=10';");
            out.println(" form_data += '&done_adding_related_reservations=true';");

        } // end if more than one cover



        // add something to tell user we are working on their reservation and disable the submit button until this routine finishes

        out.println(" $('#commit_sub').attr('disabled', 'disabled');");
        out.println(" $('#back').attr('disabled', 'disabled');");
        out.println(" $('#message').html(\"Processing Reservation...\")");
        out.println(" $(document).foreTeesModal(\"pleaseWait\");");

        out.println(" $.ajax({");
        out.println("   type: 'POST', ");
        out.println("   url: '/v5/dprox.php', ");
        //out.println("   contentType: \"application/x-www-form-urlencoded\", ");
        out.println("   dataType: 'xml', ");
        out.println("   data: form_data, ");
        //out.println("   beforeSend: function(x) { ");
        //out.println("    if(x && x.overrideMimeType) { ");
        //out.println("     x.overrideMimeType(\"application/xml;charset=UTF-8\"); ");
        //out.println("    }");
        //out.println("   }, ");
        out.println("   success: function(response) {");
        out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
        out.println("     var ok = $('success', response).text();");
        out.println("     if(ok == '1'){");
       if (useEmailVerification) {
        out.println("       $(\"#frmEV\").submit();");
       }
       if (parmD.id == 0) {
        out.println("       $('#message').html(\"<h2>Reservation Accepted</h2>\")");
       } else{
        out.println("       $('#message').html(\"<h2>Reservation Updated</h2>\")");
       }
        //out.println("     .append(\"<p>We will be in touch soon.</p>\")");
        out.println("       .hide()");
        out.println("       .fadeIn(1500, function() {");
        out.println("           $('#message').append(\"Please Wait...\");");
        //if (ext_login == false) {
        //   out.println("           window.location.href='Member_teelist?activity_id=" + ProcessConstants.DINING_ACTIVITY_ID + "';");
        //} else {
        //   out.println("           window.location.href='Dining_home?ext_login&event&event_id=" +event_id+ "';");
        //}
        out.println("           ftReturnToCallerPage();");
        out.println("       });");
        out.println("     }else{");
        // if response contains 'error' then hilite the message div in red or something
        out.println("       $('#message').html(\"<h2>Communication Error</h2>\")");
        out.println("       $('#back').attr('disabled', '');"); // get the go-back button re-enabled
        out.println("       $('error', response).each(function(id){");
        out.println("           if(id==0)$('#message').html('<h2>Problem Detected</h2>');");
        out.println("           var errMsg = $(response).find('error').eq(id).text();");
        out.println("           $('#message').append(errMsg+'<br>');");
        out.println("           $('#commit_sub').removeAttr('disabled');");
        out.println("           $('#back').removeAttr('disabled');");
        out.println("       })");
        out.println("     }");
        out.println("   }, ");
        out.println("   error: function(xhr, ajaxOptions, thrownError) {");
        out.println("     $(document).foreTeesModal(\"pleaseWait\",\"close\");");
        out.println("     $('#message').html(\"<h2>Unexpected Error</h2>\")");
        out.println("     $('#message').append(\"Error: \" + thrownError);");
        out.println("     $('#commit_sub').removeAttr('disabled');");
        out.println("     $('#back').removeAttr('disabled');");
        out.println("   }");
        out.println(" });"); // end of ajax call
        // re-enable the buttons
        //out.println(" $('#commit_sub').attr('disabled', '');");
        //out.println(" $('#back').attr('disabled', '');");
        out.println("}"); // end submit_reservation

        out.println("</script>");




    /*

        out.println("<form action=\"http://dining.foretees.com/self_service/dining_reservations\" id=\"new_dining_reservation_form\" method=\"post\">");

        out.println(" <input type=\"hidden\" name=\"foretees_login\" value=\"XV3\">");
        out.println(" <input type=\"hidden\" name=\"organization_id\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"user_id\" value=\"" + Utilities.getUserId(person_id) + "\">");

        out.println(" <input type=\"hidden\" name=\"reservation[organization_id]\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[person_id]\" value=\"" + person_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[category]\" value=\"" + ((event) ? "event" : "dining") + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[date]\" value=\"" + parmD.sdate + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[reservation_time]\" value=\"" + time + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_special_requests]\" value=\"" + parmD.special_requests + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[occasion_id]\" value=\"" + parmD.occasion_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[location_id]\" value=\"" + parmD.location_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[number_of_checks]\" value=\"" + parmD.covers + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[covers]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[email_user]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_created]\" value=\"true\">");
        out.println(" <input type=\"hidden\" name=\"reservation[check_number]\" value=\"1\">");     // set the chk# for the member making the res

        // add other names
        out.println(" <input type=\"hidden\" name=\"has_related_reservations\" value=\"" + ((parmD.covers > 1) ? "yes" : "no") + "\">");
        if (parmD.covers > 1) {

            for (int i = 1; i <= parmD.covers; i++) {

                out.println(" <input type=\"hidden\" name=\"full_name_" + i + "\" value=\"" + parmD.names[i] + "\">");
                out.println(" <input type=\"hidden\" name=\"related_reservations[" + i + "][user_identity]\" value=\"" + parmD.getUserIdentity(parmD.dining_id[i], con_d) + "\">");
                out.println(" <input type=\"hidden\" name=\"related_reservations[" + i + "][covers]\" value=\"1\">");
                out.println(" <input type=\"hidden\" name=\"related_reservations[" + i + "][check_number]\" value=\"" + parmD.check_num[i] + "\">");
            }

        }

        out.println(" <input type=\"hidden\" name=\"next_related_reservation\" value=\"5\">");

        out.println(" <input type=submit name=\"commit_sub\" value=\"Reserve\">");

        out.println("</form>");

    */


    /*

        out.println("<form action=\"http://dining.foretees.com/self_service/dining_reservations\" id=\"new_dining_reservation_form2\" method=\"post\">");

        out.println(" <input type=\"hidden\" name=\"foretees_login\" value=\"XV3\">");
        out.println(" <input type=\"hidden\" name=\"organization_id\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"user_id\" value=\"" + Utilities.getUserId(person_id) + "\">");

        out.println(" <input type=\"hidden\" name=\"reservation[organization_id]\" value=\"" + organization_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[person_id]\" value=\"" + person_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[category]\" value=\"" + ((event) ? "event" : "dining") + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[date]\" value=\"" + parmD.sdate + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[reservation_time]\" value=\"" + time + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_special_requests]\" value=\"" + parmD.special_requests + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[occasion_id]\" value=\"" + parmD.occasion_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[location_id]\" value=\"" + parmD.location_id + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[number_of_checks]\" value=\"" + parmD.covers + "\">");
        out.println(" <input type=\"hidden\" name=\"reservation[covers]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[email_user]\" value=\"1\">");
        out.println(" <input type=\"hidden\" name=\"reservation[member_created]\" value=\"true\">");
        out.println(" <input type=\"hidden\" name=\"has_related_reservations\" value=\"no\">");
        out.println(" <input type=\"hidden\" name=\"reservation[check_number]\" value=\"1\">");
        out.println(" <input type=submit name=\"commit_sub\" value=\"Reserve\">");

        out.println("</form>");
    */

    } // end if displaying questions or verify page

 }


 private void cancel_reservation(int id, String user, HttpServletRequest req, PrintWriter out, Connection con, Connection con_d) {




 }
 
 
 private void displayError(String errMsg, String club, PrintWriter out, HttpServletRequest req) {

     
    //
    //  Begin the html output
    //
    out.println("<div class=\"reservations\">");

    //
    // output bread crumb trail
    //
    //out.println("<div id=\"breadcrumb\">Home / Error</div>");
    Common_skin.outputBreadCrumb(club, ProcessConstants.DINING_ACTIVITY_ID, out, "Error", req);
    Common_skin.outputLogo(club, ProcessConstants.DINING_ACTIVITY_ID, out, req);
    //out.println("  <BR><BR>");

    out.println("<div class=\"preContentFix\"></div>"); // clear the float

    out.println("<div id=\"reservation_action_title\">Procedure Error</div>");

    out.println("<div class=\"main_warning\">" +
            "<strong>Required Information Not Provided</strong><BR><BR>  " +
            "Sorry, but one or more required items are missing:<BR> " + errMsg + "<BR><BR>" +
            "Please return, provide the missing information and try again.</div>");

    // display the go back button
    out.println("<form><div style=\"text-align:center;\">");
    out.println("<input id=\"back\" name=\"back\" type=\"button\" value=\"Go Back\" onclick=\"goBack(); return false\">");
    out.println("</div></form>");
    
    out.println("</div>"); // close reservations div

    //
    // Scripts
    //
    out.println("<script type=\"text/javascript\">");
    out.println("function goBack() {");
    out.println("  window.history.back(-1);");
    out.println("}");
    
    // This function will probably be overriden by foretees-global.js
    out.println("function ftReturnToCallerPage() {");
    out.println(" window.location.href=ftSetJsid('Dining_home');");
    out.println("}");
    out.println("</script>");

 }      // end of displayError
 
 
}
