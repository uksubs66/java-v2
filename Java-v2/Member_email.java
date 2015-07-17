/*
 *
 *  Member_email
 *
 *      Send emails to other members and manage distribution lists.
 *
 *      Called by:   Member Email Menu
 *
 *      Updates:
 * 
 *     11/22/13  Pass a send email type field to getEmailAddress(es) when getting email addresses so they can be filtered by the member's subscription settings.
 *      8/22/13  Fixed bug in email check, nest else/if typo
 *      1/17/13  Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *      9/18/12  Move the dining logo into the white portion of the page so we don't have to change colors when the background changes.
 *
 */

import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.lang.reflect.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json
import com.google.gson.reflect.*; // for json

import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.sendEmail;
import com.foretees.common.parmEmail;
import com.foretees.common.parmSMTP;
import com.foretees.common.getSMTP;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;
import com.foretees.common.Connect;

public class Member_email extends HttpServlet {

    static String rev = ProcessConstants.REV;
    static int dining_activity_id = ProcessConstants.DINING_ACTIVITY_ID;    // Global activity_id for Dining System
   
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);                          // call doPost processing

    }   // end of doGet

    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //
        //  Prevent caching so all buttons are properly displayed, etc.
        //
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
        resp.setContentType("text/html");

        PrintWriter out = resp.getWriter();

        HttpSession session = SystemUtils.verifyMem(req, out);             // check for intruder
        if (session == null) {
            return;
        }
        
        boolean json_mode = false;
        
        if (req.getParameter("send_email") != null) {
            json_mode = true;
        }

        // if we made it here we are authenticated
        // now get the club name from the session
        String club = (String) session.getAttribute("club");
        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String caller = (String) session.getAttribute("caller");     // get caller (web site?)
        int activity_id = (Integer) session.getAttribute("activity_id");

        // get a database connection
        Connection con = Connect.getCon(req);
        
        String page_title = "Send Email";
        if(req.getParameter("manage_distribution_lists") != null) {
            page_title = "Manage Distribution Lists";
        }

        String clubName = Utilities.getClubName(con, true);

        
            if (!json_mode) {
                // start the html output
                Common_skin.outputHeader(club, activity_id, page_title, true, out, req);
                Common_skin.outputBody(club, activity_id, out, req);
                Common_skin.outputTopNav(req, club, activity_id, out, con);
                Common_skin.outputBanner(club, activity_id, clubName, (String) session.getAttribute("zipcode"), out, req);    // no zip code for Dining
                Common_skin.outputSubNav(club, activity_id, out, con, req);
                Common_skin.outputPageStart(club, activity_id, out, req);
                Common_skin.outputBreadCrumb(club, activity_id, out, page_title, req);
                Common_skin.outputLogo(club, activity_id, out, req);
                //if (activity_id == dining_activity_id) {                  // if dining
                    //out.println("<BR><BR><BR>");                          // bump down to allow for the logo
                    // PLEASE correct the css rather than using <br>'s when there is layout issues.
                    // If you're tempted to use a <br>, there is a bug in the CSS.  Adding BR's will only complicate layout
                    // in the future.
                //}
            }

            //
            // Check if member has email address
            //
            if (!MemberHelper.hasEmailAddress(user, con, out)) {

                out.println("<div class=\"sub_instructions\"><h3>Notice:</h3>"
                        + "To use the email feature you must first register an email address with the system.</div>");
                out.println("<div class=\"main_instructions\">Use the "
                        + "<a href=\"Member_services\">Settings</a> "
                        + "page to enter your email address.</div>");


                //
                // Check club allows email feature
                //
            } else if (club.equals("trooncc") || club.equals("fountaingrovegolf") || club.equals("hiwan") || club.equals("coloradogc") || club.equals("hillwoodcc")) {

                out.println("<div class=\"sub_instructions\">Sorry, but the email feature is disabled for your club.</div>");

                //
                // Passed all tests, output email form
                //
            } else {
                
                if (req.getParameter("send_email") != null) {

                    Gson gson_obj = new Gson();
                    Map<String, Object> result = sendEmail(session, req, con);
                    out.print(gson_obj.toJson(result));

                } else if(req.getParameter("manage_distribution_lists") != null) {
                
                    out.println("<div class=\"manage_distribution_lists_container\"></div>");
                
                } else {
                    /*
                    Map<String, Object> emailConfig = new LinkedHashMap<String, Object>();
                    emailConfig.put("show_add_partners", true);
                    emailConfig.put("show_select_members", true);
                    emailConfig.put("show_select_distribution_list", true);
                    */
                    out.println("<div class=\"compose_email_container\"></div>");

                }

            }

            // End of page
            if (!json_mode) {
                Common_skin.outputPageEnd(club, activity_id, out, req);
            }
            
        out.close();

        try {
            con.close();
        } catch (Exception ignore) {
        }

    }

    //******************************************************************************************************
    //   sendEmail
    //******************************************************************************************************
    //
    private static Map<String, Object> sendEmail(HttpSession session, HttpServletRequest req, Connection con) {

        Statement stmtN = null;
        ResultSet rs = null;

        String activity_name = "";
        String activity_efrom = "";

        Gson gson_obj = new Gson();
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        result.put("successful", true);

        //  ***** get user id so we know if proshop or member

        String user = (String) session.getAttribute("user");     // get username ('proshop' or member's username)
        String club = (String) session.getAttribute("club");
        String clubName = Utilities.getClubName(con, true);

        int sess_activity_id = (Integer) session.getAttribute("activity_id");

        if (!clubName.equals("")) {
            clubName = StringEscapeUtils.unescapeHtml(clubName);
          //clubName = SystemUtils.unfilter(clubName);   //  Filter out special characters - change from html format to real chars
        }

        //
        //  Get name of activity if user is logged into one
        //
        if (sess_activity_id > 0) {

            try {

                stmtN = con.createStatement();
                rs = stmtN.executeQuery("SELECT activity_name FROM activities WHERE activity_id = " + sess_activity_id);

                if (rs.next()) {
                    activity_name = rs.getString("activity_name");
                }

                // if there was no email address specified then try and get it from the parent activity (should need if configured correctly)
                if (activity_efrom.equals("")) {
                }

            } catch (Exception ignore) {
            } finally {

                try {
                    rs.close();
                } catch (Exception ignore) {
                }

                try {
                    stmtN.close();
                } catch (Exception ignore) {
                }
            }
        }


        //
        // Get the SMTP parmaters this club is using
        //
        parmSMTP parm = new parmSMTP();

        try {

            getSMTP.getParms(con, parm);        // get the SMTP parms

        } catch (Exception ignore) {
        }

        //get the subject, message and the to list

        String subject = req.getParameter("subject");
        String trailer = "";
        String message = "";
        String memberName = SystemUtils.getFullNameFromUsername(user, con);

        message = memberName + " has sent you the following message.\n\n";

        List<String> names = new ArrayList();

        String user_message = req.getParameter("message");

        try {
            Type data_type = new TypeToken<List<String>>() {
            }.getType();
            names = gson_obj.fromJson(req.getParameter("recipients"), data_type);
        } catch (Exception ignore) {
        }

        // Check if the form was filled out correctly
        if (names == null || names.size() < 1) {
            result.put("successful", false);
            result.put("message", "[noRecipient]");
        } else if (names == null || names.size() > 100) {
            result.put("successful", false);
            result.put("message", "[tooManyRecipients]");
        } else if (user_message == null || user_message.length() < 1) {
            result.put("successful", false);
            result.put("message", "[noMessage]");
        } else if (subject == null || subject.length() < 1) {
            result.put("successful", false);
            result.put("message", "[noSubject]");
        } else {
            // Successfull

            message += user_message;

            //
            //  Add the name of the club to the subject to ensure it is included (for members that belong to multiple clubs)
            //
            if (!club.equals("fortcollins")) {
                if (!clubName.equals("")) {
                    subject += " (" + clubName + ")";
                }
            }

            // determin the address we are going to use as the 'from' address
            String efrom = "";
            // this is a member
            efrom = MemberHelper.getEmailAddress(user, con, null, Member.GET_CALLER_EMAIL);  // get caller's email address (member)

            String replyTo = efrom;                                       // copy for ReplyTo field

            efrom = parm.EMAIL_FROM_MEM;

            if (club.equalsIgnoreCase("tripoli")) {      
                efrom = "tripolimember@fortees.com";
            }

            trailer = ProcessConstants.TRAILERMEM;

            //we need to get the email address for each user from the database

            ArrayList<ArrayList<String>> eaddrTo = new ArrayList<ArrayList<String>>();
            ArrayList<String> eaddrProCopy = new ArrayList<String>();
            ArrayList<String> member_addresses = new ArrayList<String>();

            // Loop over user names and get email address(s)
            for (String user_name : names) {

                // get one or both email address for this member
                member_addresses = MemberHelper.getEmailAddresses(user_name, con, null, Member.MEM_SEND_EMAIL);
                if (member_addresses.size() > 0 && (member_addresses.get(0) != null && !member_addresses.get(0).equals(""))) {

                    // found at least one email address - add it
                    eaddrTo.add(new ArrayList<String>());
                    eaddrTo.get(eaddrTo.size() - 1).add(member_addresses.get(0)); // add their first email address
                    eaddrTo.get(eaddrTo.size() - 1).add(user_name); // add the username

                    // now check to see if they had two emails address specified
                    if (member_addresses.size() > 1 && (member_addresses.get(1) != null && !member_addresses.get(1).equals(""))) {

                        eaddrTo.add(new ArrayList<String>());
                        eaddrTo.get(eaddrTo.size() - 1).add(member_addresses.get(1)); // add their second email address
                        eaddrTo.get(eaddrTo.size() - 1).add(user_name); // add the username again

                    }

                }

            } // end loop of names arraylist


            //
            //  Add trailer to message
            //

            parmEmail emailParm = new parmEmail();
            emailParm.type = "EmailToolMem";
            emailParm.subject = subject;
            emailParm.txtBody = message;
            emailParm.from = efrom;
            emailParm.replyTo = replyTo;
            emailParm.activity_name = activity_name;
            emailParm.activity_id = sess_activity_id;
            emailParm.club = club;
            emailParm.user = user;
            emailParm.message = trailer;

            StringBuffer vCalMsg = new StringBuffer();  // no vCal for email tool (at least not yet!)

            sendEmail.doSending(eaddrTo, eaddrProCopy, replyTo, subject, message, vCalMsg, emailParm, con, "");

            result.put("successful", true);
            result.put("message", "[successful]");

        }

        return result;

    }
}