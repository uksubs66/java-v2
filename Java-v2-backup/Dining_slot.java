/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

//import javax.mail.internet.*;
//import javax.mail.*;

import org.apache.commons.lang.*;
//import com.google.gson.*;

// foretees imports
//import com.foretees.common.DaysAdv;
import com.foretees.common.diningUtil;
import com.foretees.common.parmDining2;
import com.foretees.common.parmDiningCosts;
import com.foretees.common.parmDiningSeatings;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.formUtil;
//import com.foretees.common.nameLists;
import com.foretees.common.parmSlotPage;
import com.foretees.common.htmlTags;
import com.foretees.common.Connect;
import com.foretees.common.timeUtil;
import com.foretees.common.parmClub;
import com.foretees.common.getClub;
import com.foretees.common.diningLocation;
//import com.foretees.common.diningEvent;
import com.foretees.common.Question;
import com.foretees.common.diningMealPeriod;
//import com.foretees.common.slotId;
import com.foretees.common.reqUtil;
import com.foretees.common.verifySlot;
import com.foretees.common.slotPostBack;

/**
 *
 * @author Owner
 */
public class Dining_slot extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    String rev = SystemUtils.REVLEVEL;                       // Software Revision Level (Version)

    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        //
        //  Prevent caching so all buttons are properly displayed, etc.
        //
        //Utilities.logDebug( "JGK2","Entered Slot Page" );

        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();


        ResultSet rs = null;
        
        HttpSession session;
        boolean ext_login = false;            // not external login (from email link)
        if (reqUtil.getSessionString(req, "ext-user", null) != null) {   // if from Login or Member_evntSignUp for an external login user
            ext_login = true;        // member came from link in email message  (via Login.ProcessExtLogin)
            session = SystemUtils.verifyMem(req, out, true);       // validate external login 

        } else {

            session = SystemUtils.verifyMem(req, out);       // check for intruder
        }

        // = SystemUtils.verifyMem(req, out);             // check for intruder

        //Utilities.logDebug( "JGK2","Passed Verify Mem" );

        if (session == null) {

            return;
        }

        Connection con = Connect.getCon(req);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER><img src=\"/" + rev + "/images/foretees.gif\"><BR>");
            out.println("<hr width=\"40%\">");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"javascript:history.back(1)\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        //Utilities.logDebug( "JGK2","Passed Get Con" );

        //
        //  Get this session's username (to be saved in teecurr)
        //
        String club = (String) session.getAttribute("club");
        String user = (String) session.getAttribute("user");
        //String name = (String) session.getAttribute("name");          // get users full name
        String userMship = (String) session.getAttribute("mship");    // get users mship type
        String mtype = (String) session.getAttribute("mtype");        // get users mtype
        String mship = (String) session.getAttribute("mship");        // get users mship
        //String pcw = (String) session.getAttribute("wc");             // get users walk/cart preference
        int activity_id = (Integer) session.getAttribute("activity_id");
        //boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        boolean json_mode = req.getParameter("json_mode") != null;

        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        if (req.getParameter("cancel") != null) {
            //cancel(req, out, club, con);       // process cancel request
            return;
        }
        
        
        //
        //   Custom for Hillwood CC to block access by Whitworth Athletic members (they can possibly get here from the App)
        //
        if (club.equals("hillwoodcc") && mship.equals("Whitworth - Athletic")) {

            out.println(SystemUtils.HeadTitle("Access Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Access Not Allowed</H3>");
            out.println("<BR><BR>Sorry, your membership class does not allow access to this feature.");
            out.println("<BR><BR>");
            out.println("</CENTER></BODY></HTML>");
            return;
        }
            
   
        

        //String action = Utilities.getParameterString(req, "action", "");
        int reservation_id = Utilities.getParameterInteger(req, "reservation_id", 0);
        int event_id = Utilities.getParameterInteger(req, "event_id", 0);
        //boolean is_event = (req.getParameter("event") != null || event_id > 0);
        //boolean event = ((req.getParameter("event") != null) || event_id > 0);

        //Utilities.logDebug( "JGK2","Passed Init 1" );

        // Create fill slot page parameters we already know, fill in the rest later
        parmSlotPage slotPageParms = new parmSlotPage();

        slotPageParms.activity_id = activity_id;

        parmClub parmC = new parmClub(activity_id, con);
        getClub.loadParms(con, parmC, activity_id);

        Common_slot.setGuestTypes(con, slotPageParms, parmC);

        slotPageParms.club = club;
        slotPageParms.club_name = clubName;
        slotPageParms.slot_url = "Dining_slot";
        slotPageParms.notice_message = "";
        slotPageParms.slot_help_url = "";
        slotPageParms.slot_type = "Registration";
        slotPageParms.member_tbd_text = "Member";
        slotPageParms.page_title = "Dining Reservation";
        slotPageParms.bread_crumb = "Dining Reservation";
        slotPageParms.user = user;
        slotPageParms.mship = userMship;
        slotPageParms.mtype = mtype;
        slotPageParms.zip_code = (String) session.getAttribute("zipcode");
        slotPageParms.show_fb = false;

        slotPageParms.callback_map.put("json_mode", true);

        slotPageParms.options.put("player", "person");
        slotPageParms.options.put("playerPlural", "people");
        slotPageParms.options.put("playerProper", "Person");
        slotPageParms.options.put("playerProperPlural", "People");
        
        Connection con_d = Connect.getDiningCon();

        processSlot(reservation_id, event_id, slotPageParms, req, con_d);

        if (json_mode) {
            out.print(Common_slot.slotJson(slotPageParms));
        } else {
            Common_slot.displaySlotPage(out, slotPageParms, req, con);
        }

        out.close();

        try {
            con_d.close();
        } catch (Exception ignore) {
        }
    }

    private void processSlot(int id, int event_id, parmSlotPage slotPageParms, HttpServletRequest req, Connection con_d) {

        //boolean is_event = (req.getParameter("event") != null || event_id > 0);
        //Utilities.logDebug( "JGK2","Entered Slot Process" );

        boolean rwd = Utilities.getRequestBoolean(req, ProcessConstants.RQA_RWD, false);
        
        Connection con = Connect.getCon(req);

        String user = reqUtil.getSessionString(req, "user", reqUtil.getSessionString(req, "ext-user", ""));
        String name = Utilities.getSessionString(req, "name", "");
        String club = Utilities.getSessionString(req, "club", "");
        
        htmlTags tags = new htmlTags(rwd);

        int person_id = Utilities.getPersonId(user, con);
        int user_id = Utilities.getUserId(person_id);

        int organization_id = Utilities.getOrganizationId(con);

        parmDining2 parmD; /* dining parm for existing reservation (or new) */
        parmDining2 cparmD; /* chenged (posted) dining parms (or same as parmD for new) */

        // determin how to populate parmD
        if (id > 0) {
            // we are here to view/modify an existing reservation from the database
            parmD = new parmDining2(organization_id, id, con_d, req);
            cparmD = new parmDining2(organization_id, req, con_d);

        } else {
            // we are here at some stage of completing a new reservation
            // load from the request object
            parmD = new parmDining2(organization_id, req, con_d);
            cparmD = parmD;
        }

        if (parmD.reservation_load_error != null) {
            slotPageParms.page_start_button_go_back = true;
            slotPageParms.page_start_title = "Unable to load reservation";
            slotPageParms.page_start_notifications.add("<h2>Sorry, there was an issue loading this reservation:</h2> " + parmD.reservation_load_error);
            slotPageParms.page_start_notifications.add("Please try again, or contact support.");
            return;
        }

        //boolean can_delete = parmD.id > 0 && parmD.member_created
        boolean can_delete = parmD.id > 0 
                && (diningUtil.isUserMasterReservee(parmD.id, person_id, con_d)
                || (diningUtil.canNonMasterReserveeEdit(organization_id, con_d) && diningUtil.isUserReservee(user, parmD)));

        /* set some defaults for cparmD, if not passed via form */
        if (cparmD.time < 0) {
            cparmD.time = parmD.time;
        }

        if (cparmD.date == 0 || event_id > 0) { // always override submitted date
            cparmD.date = parmD.date;
        }

        if (cparmD.location_id == 0) {
            cparmD.location_id = parmD.location_id;
        }

        if (cparmD.occasion_id < 0) {
            cparmD.occasion_id = parmD.occasion_id;
            //if (cparmD.occasion_id < 0) {
            //  cparmD.occasion_id = 0;
            //parmD.occasion_id = 0;
            //}
        }

        if (cparmD.covers < 1) {
            cparmD.covers = parmD.covers;
            /*
            if (cparmD.covers < 1) {
            cparmD.covers = 1;
            parmD.covers = 1;
            }
             * 
             */
        }
        
        //
        // Check if we are asking to remove ourself
        //
        if (cparmD.stage == parmDining2.REMOVE_SELF) {
            List<String> result = new ArrayList<String>();
            Integer my_index = parmD.getIndexByUser(user);
            Integer my_releated_id = parmD.related_id.get(my_index);
            String reason = null;
            if (parmD.id > 0 && my_index != null && my_releated_id != null && my_releated_id > 0 
                    && diningUtil.isUserReservee(user, parmD)) {
                reason = "Removed self: " + name;
                result = diningUtil.removeReservee(organization_id, event_id, user_id, my_releated_id, reason, con_d, req);
            } else {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Unable to cancel reservation";
                if (parmD.id > 0) {
                    result.add("Sorry, you do not have access to cancel this reservation.");
                } else {
                    result.add("No reservation found.");
                }
            }
            if (result.isEmpty()) {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Removed from reservation";
                slotPageParms.page_start_instructions.add("You have been removed from reservation #" + parmD.reservation_number);
                diningUtil.sendClubNotification(name + " Removed Self from Reservation", organization_id, parmD.reservation_number, parmD, req, con_d);
            } else {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Unable to cancel reservation";
                slotPageParms.page_start_notifications.addAll(result);
            }
            return;
        }

        //
        // Check if we should cancel the entire reservation
        //
        if (cparmD.stage == parmDining2.DELETE) {
            List<String> result = new ArrayList<String>();
            String reason = null;
            if (can_delete) {
                reason = "Cancelled by " + name;
                result = diningUtil.cancelReservation(organization_id, parmD.event_id, user_id, parmD.id, reason, con_d, req);

            } else {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Unable to cancel reservation";
                if (parmD.id > 0) {
                    result.add("Sorry, you do not have access to cancel this reservation.");
                } else {
                    result.add("No reservation found.");
                }
            }
            if (result.isEmpty()) {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Reservation cancelled";
                slotPageParms.page_start_instructions.add("Reservation #" + parmD.reservation_number + " cancelled.");
                diningUtil.sendClubNotification(reason, organization_id, parmD.reservation_number, parmD, req, con_d);
            } else {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Unable to cancel reservation";
                slotPageParms.page_start_notifications.addAll(result);
            }
            return;
        }

        //
        // Check if we should process a slot sumbission
        //
        if (cparmD.stage == parmDining2.PROCESS) {

            //Utilities.logDebug( "JGK2","Entered Request Process" );

            // Start comparing parmD and cparmD to find differences

            if (parmD.slot_data_error) {
                // There's a fatal error submitting.  cancel.
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Error submitting reservation.";
                slotPageParms.page_start_notifications.add("Unknown error submitting reservation.  Please try again later.");
                return;
            }

            /*
             * Default callbacks 
             */
            
            // When we're in slot verify/process, 
            // we use postback to communicate with client.  
            // Yeah, I know -- confusing.
            // We'll try to iron all this out here and in all other slot pages
            // after old-skin and old-mobile code paths are fully retired and removed.
            slotPostBack postback = new slotPostBack(); 

            // Fields that most of our callbacks will need
            postback.title = "Undefined State"; // This should be changed below.  If we ever see this output, something went wrong
            postback.callback_map.put("json_mode", true);
            postback.callback_map.put("owner", cparmD.owner);
            postback.callback_map.put("slot_submit_action", "update");
            postback.callback_map.put("date", cparmD.date);
            postback.callback_map.put("reservation_id", parmD.id);
            postback.callback_map.put("reservation[location_id]", cparmD.location_id);
            postback.callback_map.put("reservation[occasion_id]", cparmD.occasion_id);
            postback.callback_map.put("reservation[member_special_requests]", cparmD.member_special_requests);
            postback.callback_map.put("event_id", parmD.event_id);
            postback.callback_map.put("reservation[reservation_time]", cparmD.time);
            postback.callback_map.put("notes", cparmD.member_special_requests);
            postback.callback_map.put("player%", cparmD.names);
            postback.callback_map.put("user%", cparmD.usernames);
            postback.callback_map.put("check_num%", cparmD.check_num);

            postback.callback_map.put("answer%", cparmD.answers); // May be overidden later (shouldn't need this)
            postback.callback_map.put("group_answer#", cparmD.group_answers); // May be overidden later (shouldn't need this)

            if (parmD.event_id > 0) {
                postback.callback_map.put("meal_option%", cparmD.price_category);
            }

            // Validate slot page submission
            // TODO:  Simplify this proccess - expand parmDining2's lookups to use maps instead of using them here

            Map<Integer, Integer> existing_members = new LinkedHashMap<Integer, Integer>(); //<new_index, existing_index>
            Map<Integer, Integer> existing_guests = new LinkedHashMap<Integer, Integer>(); //<new_index, existing_index>
            List<Integer> new_members = new ArrayList<Integer>(); //<new_index>
            List<Integer> new_guests = new ArrayList<Integer>(); //<new_index>
            List<Integer> removed = new ArrayList<Integer>(); //<existing_index>


            // Create lookup tables for existing reservation
            Map<String, Integer> user_lookup = diningUtil.buildUserMap(parmD.usernames, parmD.dining_id);
            Map<String, List<Integer>> guest_lookup = diningUtil.buildGuestMap(parmD.usernames, parmD.names);

            if (parmD.id > 0) {
                // we're editing an existing reservation
                // find new/exisiting/removed members and guests

                if (!parmD.owner.equals(cparmD.owner)) {
                    // Owners have changed.  
                    // If previous owner is still in the reservation, We must treat previous owner as a new member, 
                    // since they will be removed when re-delegating.
                    // We do this by removing them from the existing member lookup list
                    diningUtil.popMember(parmD.owner, user_lookup);
                }

                // Compare existing (parmD) to submitted reservation (cparmD)
                String cuser, cplayer;
                Integer index;
                for (int i = 0; i < cparmD.names.size(); i++) {
                    cuser = cparmD.usernames.get(i).trim().toLowerCase();
                    cplayer = cparmD.names.get(i).trim().toLowerCase();
                    index = diningUtil.popMember(cuser, user_lookup);
                    if (index != null) {
                        // Found an existing member
                        cparmD.related_id.set(i, parmD.related_id.get(index)); // Set the related id
                        existing_members.put(i, index);
                    } else if (cuser.isEmpty()) {
                        // Found a guest
                        if (!cplayer.isEmpty()) { // Make sure there is a length to the guest name
                            index = diningUtil.popGuest(cplayer, guest_lookup);
                            if (index != null) {
                                // Existing guest
                                cparmD.related_id.set(i, parmD.related_id.get(index)); // Set the related id
                                existing_guests.put(i, index);
                            } else {
                                // New guest to the reservation
                                new_guests.add(i);
                            }
                        }
                    } else {
                        // Is a new member to the reservation
                        new_members.add(i);
                    }
                }
                // Any remaining guests/member in the lookup lists do not exist in the submitted reservation
                // They will need to be removed from the existing reservation
                // (Note: if we're removing ourselves and/or the master (owner), we'll actually put them back
                //  on the reservation in saveRaservation, then pull them back off.)
                for (Map.Entry<String, Integer> entry : user_lookup.entrySet()) {
                    removed.add(entry.getValue());
                }
                for (Map.Entry<String, List<Integer>> entry : guest_lookup.entrySet()) {
                    List<Integer> list = entry.getValue();
                    for (Integer guestIndex : list) {
                        removed.add(guestIndex);
                    }
                }

            } else {
                // New reservation
                String cuser, cplayer;
                for (int i = 0; i < cparmD.names.size(); i++) {
                    cuser = cparmD.usernames.get(i).trim().toLowerCase();
                    cplayer = cparmD.names.get(i).trim().toLowerCase();
                    if (cuser.isEmpty()) {
                        // Found a guest
                        if (!cplayer.isEmpty()) { // Make sure there is a length to the guest name
                            // New guest to the reservation
                            new_guests.add(i);
                        }
                    } else {
                        // Found member
                        new_members.add(i);
                    }
                }
            }

            // Check if any submitted members do not have a valid dining/person id
            Map<String, Integer> invalid_user_lookup = diningUtil.buildInvalidUserMap(cparmD.usernames, cparmD.dining_id);
            if (!invalid_user_lookup.isEmpty()) {
                // Something is wrong with the club/dining database if we get here
                postback.title = "Unable to submit reservation";
                postback.notice_head = "The following members to not appear to be properly configured in the dining system:";
                for (Integer invalid_index : invalid_user_lookup.values()) {
                    postback.notice_list.add(cparmD.names.get(invalid_index));
                }
                postback.notice_list.add("Please try again, or contact support.");
                slotPageParms.process_postback = postback;
                return;
            }

            // Build map of submitted members for testing
            Map<String, Integer> new_user_lookup = diningUtil.buildUserMap(cparmD.usernames, cparmD.dining_id);

            // Check that we have at least one member in the reservation
            if (new_user_lookup.isEmpty()) {
                // No members in this reservation.
                postback.title = "Unable to submit reservation";
                postback.notice_list.add("You must have at least one member in this reservation.");
                slotPageParms.process_postback = postback;
                return;
            }

            if (parmD.id > 0
                    && !parmD.owner.equalsIgnoreCase(user)
                    && !diningUtil.canNonMasterReserveeEdit(parmD.organization_id, con_d)) {
                // This user can't edit this reservation
                postback.title = "Unable to submit reservation";
                postback.notice_list.add("Only the member that created this reservation can modify it.");
                slotPageParms.process_postback = postback;
                return;
            }
            
            // Check if any members have a conflicting reservation
            parmDining2 conparmD = null;
            for(Map.Entry<String, Integer> entry : new_user_lookup.entrySet()){
                Integer check_person_id = cparmD.dining_id.get(entry.getValue());
                List<String> conflicts = new ArrayList<String>();
                if(check_person_id != null){
                    conparmD = diningUtil.getConflictingReservation(organization_id, parmD.id, cparmD.location_id, check_person_id, parmD.event, cparmD.date, cparmD.time, con_d, req);
                    if(conparmD != null){
                        // Looks like we have a conflicting reservation. Add it to the list
                        Integer c_index = conparmD.getIndexByPersonId(check_person_id);
                        if(c_index != null){
                            if(cparmD.event_id > 0 && cparmD.event_id == conparmD.event_id){
                             conflicts.add(conparmD.names.get(c_index)+" is already registered for "
                                    + conparmD.event.name + " on " + timeUtil.getVerboseDate(req,conparmD.date, conparmD.time));   
                            } else {
                            conflicts.add(conparmD.names.get(c_index)+" has a conflicting "
                                    +(conparmD.event_id>0?"event ":"") + " registration on " + timeUtil.getVerboseDate(req,conparmD.date, conparmD.time));
                            }
                        }                        
                    }
                }
                if(!conflicts.isEmpty()){
                    // We have one or more conflicting reservtions. tell the user.
                    postback.title = "Conflicting reservation"+(conflicts.size()>1?"s":"")+" found";
                    postback.notice_list.addAll(conflicts);
                    slotPageParms.process_postback = postback;
                    return;
                }
            }

            // Check if owner is one of the submitted members
            Integer owner_index = diningUtil.getMember(cparmD.owner, new_user_lookup);
            if (owner_index == null) {
                // No reservation owner is reservation.
                /*
                postback.title = "Unable to submit reservation";
                postback.notice_list.add("One of the members in this reservation must be selected as the reservation's owner.");
                slotPageParms.process_postback = postback;
                return;
                 * 
                 */
                // Set owner as first member
                owner_index = new_user_lookup.entrySet().iterator().next().getValue();
                cparmD.owner = cparmD.usernames.get(owner_index);
                cparmD.owner_person_id = cparmD.dining_id.get(owner_index);
            }
            
            //
            //  Check if less reservees were submitted than originally requested
            //
            int requestedCovers = reqUtil.getParameterInteger(req, "covers", 0);
            if (requestedCovers > cparmD.names.size() 
                    && cparmD.names.size() > 0 
                    && reqUtil.getParameterInteger(req, "skip_cover_check", 0) < 1) {

                // Build form/messages
                postback.title = "Reservation May Not Be Complete";
                postback.warning_list.add("You have requested a party size of " + requestedCovers + " for this reservation, but have only entered details for " + cparmD.names.size() + ".");
                postback.warning_list.add("If you intended a <b>reservation for "+cparmD.names.size()+"</b>, select \"Continue\" below, otherwise select \"Close\" and fill in details for the empty participants.");
                postback.callback_map.put("skip_cover_check", 1);
                postback.prompt_close_continue = true;

                // Return it
                slotPageParms.process_postback = postback;
                return;

            }

            //
            // Generate question prompts while checking answers 
            // 
            List<Object> questions = new ArrayList<Object>();
            Map<String, String> questionsRequireMap = new LinkedHashMap<String, String>();

            String submit_name, question_text, require_text;
            String value;
            Question question;
            int question_id;
            List<String> question_errors = new ArrayList<String>();

            // Generate and check group questions
            List<Object> group_questions = new ArrayList<Object>();
            for (Map.Entry<Integer, Question> entry : cparmD.group_questions.entrySet()) {
                question = entry.getValue();
                question_id = entry.getKey();
                if (question.select_text != null && question.select_list.length > 1) {
                    question_text = question.select_text;
                } else {
                    question_text = question.text;
                }
                if (!question.guest_only || cparmD.has_guests) {
                    value = cparmD.group_answers.get(question_id);// Get posted answer
                    if (value == null) {
                        if (parmD.group_answers != null) {
                            value = parmD.group_answers.get(question_id); // Get original answer (null if new reservation)
                        } else {
                            value = "";
                        }
                    }
                    submit_name = "group_answer" + question_id;

                    require_text = "You must answer the reservation question: " + question_text;
                    if ((value == null || value.equals(""))) {
                        if (question.requires_answer) {
                            // Required question has not been answered 
                            // (shouldn't get here, since it's eforced on the client side);
                            question_errors.add(require_text);
                        }
                        value = "";
                    }
                    if (question.select_text != null && question.select_list.length > 1) {
                        for (int t = 0; t < question.select_list.length; t++) {
                            if (question.select_list[t].equalsIgnoreCase(value.trim())){
                                value = question.select_list[t];
                                continue;
                            }    
                        }
                    }
                    if (question.select_text != null && question.select_list.length > 1) {
                        group_questions.add(formUtil.select(submit_name, submit_name, question_text, value, question.select_list));
                    } else {
                        group_questions.add(formUtil.text(submit_name, submit_name, question_text, value, question.requires_answer));
                    }
                    if (question.requires_answer) {
                        questionsRequireMap.put(submit_name, require_text);
                    }
                }
            }
            
            
            
            if (!group_questions.isEmpty()) {
                questions.add(formUtil.startFieldBlock("Questions for entire reservation:"));
                questions.addAll(group_questions);
                questions.add(formUtil.endFieldBlock());
            }

            // Generate and check per individual questions
            for (Map.Entry<Integer, Question> entry : cparmD.questions.entrySet()) {
                question = entry.getValue();
                question_id = entry.getKey();
                if (question.select_text != null && question.select_list.length > 1) {
                    question_text = question.select_text;
                } else {
                    question_text = question.text;
                }
                List<Object> player_questions = new ArrayList<Object>();
                for (int i = 0; i < cparmD.names.size(); i++) {
                    submit_name = "answer" + (i + 1) + "_" + question_id;
                    if (!question.guest_only || cparmD.dining_id.get(i) == 0) {
                        value = cparmD.answers.get(i).get(question_id);// Get posted answer
                        if (value == null) {
                            Integer relatedIndex = parmD.getIndexByRelatedId(cparmD.related_id.get(i));
                            if (relatedIndex != null) {
                                Map<Integer, String> answer_map = parmD.answers.get(relatedIndex);
                                if(answer_map != null){
                                    value = answer_map.get(question_id); // Get original answer (null if new reservation)
                                }
                            } else {
                                value = "";
                            }
                        }
                        require_text = "You must answer \"" + question_text + "\" for " + cparmD.names.get(i);
                        if ((value == null || value.equals(""))) {
                            if (question.requires_answer) {
                                // Required question has not been answered 
                                // (shouldn't get here, since it's eforced on the client side);
                                question_errors.add(require_text);
                            }
                            value = "";
                        }
                        if (question.select_text != null && question.select_list.length > 1) {
                            // see if the value exists in the list
                            for (int t = 0; t < question.select_list.length; t++) {
                                if (question.select_list[t].equalsIgnoreCase(value.trim())){
                                    value = question.select_list[t];
                                    continue;
                                }    
                            }
                            player_questions.add(formUtil.select(submit_name, submit_name, cparmD.names.get(i), value, question.select_list));
                        } else {
                            player_questions.add(formUtil.text(submit_name, submit_name, cparmD.names.get(i), value, question.requires_answer));
                        }
                        if (question.requires_answer) {
                            questionsRequireMap.put(submit_name, require_text);
                        }
                    } else {
                        // Guest only, but we're not a guest
                        postback.callback_map.put(submit_name, "N/A");
                    }
                }
                if (!player_questions.isEmpty()) {
                    questions.add(formUtil.startFieldBlock(question_text));
                    questions.addAll(player_questions);
                    questions.add(formUtil.endFieldBlock());
                }
            }

            if (!cparmD.has_answers) {
                // If there are not answers posted, then there cannot be any question errors yet
                question_errors.clear();

            }

            postback.debug.put("cparmD", cparmD);
            postback.debug.put("parmD", parmD);
            postback.debug.put("questions", questions);

            // If we have unanswered questions, ask them
            if (!questions.isEmpty() 
                    && (!cparmD.has_answers || !question_errors.isEmpty())
                    ) {

                // We'll build a form to sumbit these values instead, clear them
                postback.callback_map.remove("answer%");
                postback.callback_map.remove("group_answer#");

                // Build form/messages
                postback.title = "Reservation Details";
                postback.warning_list.addAll(question_errors);
                postback.callback_form_list.addAll(questions);
                postback.require.putAll(questionsRequireMap);
                postback.prompt_close_continue = true;

                // Return it
                slotPageParms.process_postback = postback;
                return;

            }


            // Process changes
            List<String> success = new ArrayList<String>();
            List<String> errors = new ArrayList<String>();

            // Check if any reservees need to be removed
            if (errors.isEmpty() && !removed.isEmpty()) {
                Integer previous_owner_index = diningUtil.getMember(parmD.owner, user_lookup);
                for (Integer remove_index : removed) {
                    if ((previous_owner_index != null
                            && previous_owner_index.equals(remove_index))
                            || (parmD.usernames.get(remove_index).equalsIgnoreCase(user))) {
                        // We won't remove the owner or ourself here (that will be done in saveReservation)
                        continue;
                    }
                    String reason = name + " removed reservee " + parmD.names.get(remove_index);
                    List<String> result = diningUtil.removeReservee(parmD.organization_id, parmD.event_id, user_id, parmD.related_id.get(remove_index), reason, con_d, req);
                    if (result.isEmpty()) {
                        // removal sucessful
                        //success.add("Removed reservee: "+parmD.names.get(remove_index));
                    } else {
                        // Something whent wrong with removal
                        errors.add("Unable to remove reservee: " + parmD.names.get(remove_index));
                        errors.addAll(result);
                    }
                }
            }

            // Try saving the reservation
            if (errors.isEmpty()) {
                List<String> result = diningUtil.saveReservation(cparmD, owner_index, con_d, req);
                if (result.isEmpty()) {
                    // removal sucessful
                    success.add("Thank you! Your reservation for "+cparmD.names.size()+" has been accepted and processed.");
                } else {
                    // Something whent wrong with save
                    errors.addAll(result);
                }
            }

            // Report errors and/or success
            if (errors.isEmpty()) {
                // We did it!
                if (parmD.id > 0) {
                    postback.title = "Reservation Modified Successfully";
                    diningUtil.sendClubNotification("Reservation Modified by: "  + name, organization_id, parmD.reservation_number, parmD, req, con_d);
                } else {
                    postback.title = "Reservation Created Successfully";
                    diningUtil.sendClubNotification("Reservation Created by: "  + name, organization_id, parmD.reservation_number, parmD, req, con_d);
                }
                postback.message_list = success;
                postback.back_to_slotpage = false;

            } else {
                // Error
                if (parmD.id > 0) {
                    postback.title = "Unable to complete changes";
                } else {
                    postback.title = "Unable to create reservation";
                }
                postback.message_list = success;
                postback.warning_list = errors;
            }

            // Return our result
            slotPageParms.process_postback = postback;
            return;

        } // End of slot process and save

        int originallySelectedDate = cparmD.date; // in case we need it later.  We may change the selected date below.

        //cparmD.allow_day_of = diningUtil.anyLocationAllowDayOf(organization_id, con_d);
        //out.println("<!-- ** parmD.anyLocationAllowDayOf=" + parmD.allow_day_of + " -->");

        boolean hide_special_occasions = (club.equals("misquamicut"));

        //long minimumTs = timeUtil.getEpoch(day, id);

        long minUnixTime = 0; // Min time allowed to add/edit reservation
        long maxUnixTime = 0; // Max time allowed to add/edit reservation
        long defaultUnixTime = 0; // Default time to add reservation
        long now = timeUtil.getCurrentUnixTime();

        String lockReason = null;

        if (parmD.id > 0 || parmD.event.id > 0) {
            // We're editing a reservation or booking a new event.  Check to see if we can still do it
            List<diningMealPeriod> mealPeriod = diningUtil.getMealPeriods(parmD.location_id, parmD.event.id, parmD.date, con_d);
            String editStatus = diningUtil.reservationTimeStatus(organization_id, parmD.location_id, parmD.date, parmD.time, parmD, mealPeriod, req, con_d);
            if (editStatus != null) {
                lockReason = editStatus;
                slotPageParms.debug.put("parmD", parmD);
                slotPageParms.debug.put("cparmD", cparmD);
                slotPageParms.debug.put("lockReason", lockReason);
                //return;
                if(parmD.id == 0){
                    // Trying to sign up for an event before or after it's available
                    slotPageParms.page_start_button_go_back = true;
                    slotPageParms.page_start_title = "Unable to create event registration";
                    slotPageParms.page_start_notifications.add(lockReason);
                    return;
                }
            }
        }

        if (parmD.event_id == 0) {
            // New/Edit a la carte reservation.  Get min/max dates
            long[] startEndDefault = diningUtil.getStartEndDefault(organization_id, 0, parmD, null, req, con_d);

            minUnixTime = startEndDefault[diningUtil.START];
            maxUnixTime = startEndDefault[diningUtil.END];
            defaultUnixTime = startEndDefault[diningUtil.DEFAULT];

            if (parmD.id == 0) {
                // We're adding a new reservation
                long requestedUnixTime = timeUtil.getClubUnixTime(req, cparmD.date, cparmD.time);
                if (cparmD.date == 0) {
                    // No date selected yet.  Set it to default date
                    cparmD.date = timeUtil.getClubDate(req, defaultUnixTime);
                } else if (requestedUnixTime < minUnixTime) {
                    // Selected date is too low.  Set it to min date
                    cparmD.date = timeUtil.getClubDate(req, minUnixTime);
                } else if (requestedUnixTime > maxUnixTime) {
                    // Selected date is too high.  Set it to max date
                    cparmD.date = timeUtil.getClubDate(req, maxUnixTime);
                }
            }

        } else {
            // Event -- do we need to do anything?
        }

        int startDate = timeUtil.getClubDate(req, minUnixTime);
        int endDate = timeUtil.getClubDate(req, maxUnixTime);
        int today = timeUtil.getClubDate(req);

        int days_in_advance = timeUtil.daysBetween(today, cparmD.date);


        //  Setup the daysArray (currently not used -- evetually we'll use this to color the calendar)
        //List<Integer>daysList = diningUtil.daysList(organization_id, startDate, endDate, req, con_d);


        // TODO: if editing an existing reservation then it's possible the reserved location may no longer be available due to the locations
        // 'days in advance to stop taking rez' has passed this is even more likely if editing a rez for 'today'
        // so we should ensure the already reserved location is always in the list.

        String reservation = (parmD.is_event) ? "Event Reservation" : "Dining Reservation";
        String title = (id > 0) ? "Modify " + reservation : "New " + reservation;

        //cparmD.loadLocationData(con_d); // fills location_name / maximum_party_size for selected location.  Must be done after setting date



        //
        // Check If View Only Mode (view details - provide link to remove themselves)
        //
        if (
            (parmD.id > 0 && ( //parmD.member_created == false ||
                lockReason != null || 
                (
                !parmD.owner.equalsIgnoreCase(user) 
                && !diningUtil.canNonMasterReserveeEdit(organization_id, con_d)
                )
                || !diningUtil.isUserReservee(user, parmD))) 
            || (parmD.event_id > 0 && !parmD.event.allow_online_signup)) {

            if (!diningUtil.isUserReservee(user, parmD) && !diningUtil.areDiningReservationsPublic(organization_id, con_d)) {
                slotPageParms.page_start_button_go_back = true;
                slotPageParms.page_start_title = "Cannot view reservation";
                slotPageParms.page_start_notifications.add("Sorry, you can not modify or view reservations you are not part of.");
                return;
            }

            //
            //  "View Only".  Display some basic reservation information and allow user
            //  to remove themselves from the reservation
            //
            // Set title
            slotPageParms.page_start_title = "View Existing " + reservation;
            // Configure view only prompt
            //viewOnlyPrompt(slotPageParms, parmD, req, con, con_d);
            // Exit

            Map<String, String> promptDetails = new LinkedHashMap<String, String>();
            
            boolean allowRemove = false;
            
            if(!diningUtil.isUserReservee(user, parmD)){
                slotPageParms.page_start_notifications.add(
                    "Since you are not part of this reservation, you can only view it.");
            } else if(lockReason != null){
                slotPageParms.page_start_notifications.add(lockReason);
            } else if(!parmD.event.allow_online_signup && parmD.event_id > 0 && parmD.id > 0){
                 slotPageParms.page_start_notifications.add(
                    "Sorry, this event does not allow editing online.  Please call the club for changes.");
            } else if(!parmD.event.allow_online_signup && parmD.event_id > 0){
                 slotPageParms.page_start_notifications.add(
                    "Sorry, this event does not allow online registration.  Please call the club for details.");
            } else {
                slotPageParms.page_start_notifications.add(
                    "Since you did not create this reservation the changes you can make are limited to cancelling yourself.");
                if(parmD.id > 0){
                    allowRemove = true;
                }
            }
            
            promptDetails.put("Reservation #", ""+parmD.reservation_number);

            if (parmD.is_event) {
                promptDetails.put("Event Name", parmD.event.name);
            }

            promptDetails.put("Date", timeUtil.getStringDateMMDDYYYY(parmD.date));
            promptDetails.put("Time", Utilities.getSimpleTime(parmD.time));

            promptDetails.put("Location", parmD.location_name);

            if (!parmD.is_event && parmD.occasion_id != 0) {
                promptDetails.put("Occasion", diningUtil.getOccasionName(parmD.occasion_id));
            }

            if (!parmD.member_special_requests.equals("")) {
                promptDetails.put("Special Requests", parmD.member_special_requests);
            }

            StringBuilder detailsHtml = new StringBuilder();

            if (promptDetails.size() > 0) {
                detailsHtml.append("<div class=\"main_instructions\"><ul class=\"modal_field_list\">");
                for (Map.Entry<String, String> entry : promptDetails.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    detailsHtml.append("<li class=\"");
                    if (key.length() + value.length() > 40) {
                        detailsHtml.append(" triple_width");
                    } else if (key.length() + value.length() > 20) {
                        detailsHtml.append(" double_width");
                    }
                    detailsHtml.append("\"><div><b>");
                    detailsHtml.append(key);
                    detailsHtml.append(":");
                    detailsHtml.append("</b><div>");
                    detailsHtml.append(value);
                    detailsHtml.append("</div></div></li>");
                }
                detailsHtml.append("</ul></div>");
                slotPageParms.page_start_htmlblocks.add(detailsHtml.toString());
                
            }

            // output names
            StringBuilder namesHtml = new StringBuilder();
            StringBuilder namesHeader = new StringBuilder();

            for (int i = 0; i < parmD.names.size(); i++) {
                if (!parmD.names.get(i).equals("")) {
                    namesHtml.append(tags.openTr());
                    namesHtml.append(tags.getTd(parmD.names.get(i)));
                    namesHtml.append(tags.getTd(parmD.check_num.get(i).toString()));
                    if (parmD.is_event) {
                        namesHtml.append(tags.getTd(parmD.price_category.get(i) + " $" + parmD.event.getMealOptionCost(parmD.price_category.get(i))));
                    }
                    namesHtml.append(tags.closeTr());
                }
            }

            if (namesHtml.length() > 0) {
                namesHeader.append(tags.openThead());
                namesHeader.append(tags.openTr());
                namesHeader.append(tags.getTh("Name"));
                namesHeader.append(tags.getTh("Check #"));
                if (parmD.is_event) {
                    namesHeader.append(tags.getTh("Cost"));
                }
                namesHeader.append(tags.closeTr());
                namesHeader.append(tags.closeThead());
                slotPageParms.page_start_htmlblocks.add(tags.openTable("standard_list_table")+namesHeader.toString()+tags.openTbody()+namesHtml.toString()+tags.closeTbody()+tags.closeTable());
            }
            slotPageParms.page_start_button_go_back = true;
            
            if(allowRemove){
                // Member is part of reservation.  Let them remove themselves.
                //slotPageParms.reservation_id = parmD.id;
                slotPageParms.callback_map.put("reservation_id", parmD.id);
                //slotPageParms.slot_submit_map.put("reservation_id", "reservation_id"); // Need this so cancel button works
                slotPageParms.callback_button_map.put("remove_self",
                        formUtil.makeButton("Remove Myself", // Button name
                        formUtil.keyValMap(new String[]{"slot_submit_action", "remove_self"}), // Set callback values
                        formUtil.keyValMap(
                        new String[]{ // Other Options
                            "confirm", "Are you sure you want to remove yourself from this reservation?", // Click Confirmation
                            "class", "redButton"
                        })));
            }

            return;
        }

        //
        //  Prompt user for date/location/time information on reservation, before building the slot page
        //

        if (cparmD.stage == parmDining2.PROMPT) {

            //slotPageParms.callback_map.put("date", parmD.date); 
            slotPageParms.callback_map.put("reservation_id", parmD.id);
            slotPageParms.callback_map.put("event_id", parmD.event.id);
            slotPageParms.reservation_id = parmD.id;
            slotPageParms.slot_submit_map.put("reservation_id", "reservation_id"); // Need this so cancel button works
            slotPageParms.event_id = parmD.event.id;
            slotPageParms.slot_submit_map.put("event_id", "event_id");

            // We'll re-calculate the "reservation id for person" on submit by comparing the submitted
            // slot with the stored dining slot.
            // slotPageParms.reservation_id_a = Arrays.copyOfRange(parmD.related_id,1,parmD.related_id.length);

            // Set title
            slotPageParms.page_start_title = title;
            //slotPageParms.page_start_instructions.add("<h2>Reservation Basics:</h2>");

            slotPageParms.page_start_force = true;

            // Get club specific message(s)
            List<String> clubMessage = getClubMessage(cparmD, parmD, req);
            if (!clubMessage.isEmpty()) {
                slotPageParms.page_start_messages.addAll(clubMessage);
            }

            String dow = timeUtil.getDayOfWeek(cparmD.date);
            String selected_date = Utilities.getDateFromYYYYMMDD(cparmD.date, 3);

            if (parmD.event_id > 0) {
                // event
                slotPageParms.callback_form_list.add(formUtil.startFieldBlock("Event: " + parmD.event.name));
                slotPageParms.callback_form_list.add(formUtil.fieldHtml("<p class=\"field_container center\"><strong>" + timeUtil.getVerboseDate(req, parmD.date) + "</strong></p>"));
                slotPageParms.callback_form_list.add(formUtil.endFieldBlock());
                slotPageParms.callback_map.put("date", Utilities.getDateFromYYYYMMDD(parmD.date, 3));
            } else {
                // a la carte
                slotPageParms.callback_form_list.add(formUtil.startFieldBlock("Select Reservation Date:"));

                slotPageParms.callback_form_list.add(formUtil.dateField("date", "<b>" + dow + "</b>", selected_date, selected_date, Utilities.getDateFromYYYYMMDD(startDate, 3), Utilities.getDateFromYYYYMMDD(endDate, 3), "date_selected"));

                if (isEventDay(parmD, con, con_d) && parmD.id == 0) {
                    // If there is an event, and tis is a new reservaton, 
                    // let member know that there is an event they could be after
                    slotPageParms.callback_form_list.add(formUtil.fieldHtml("<div class=\"sub_instructions\">"
                            + "<p><b>Note:</b> " + dow + ", " + selected_date + " has one or more events scheduled on it.  "
                            + "<b>If by chance you are intending to register for an event, "
                            + "please use the <a class=\"standard_button\" href=\"Dining_home?view_events\">event signup</a>.</p></b></div>"));
                }
                slotPageParms.callback_form_list.add(formUtil.endFieldBlock());
            }

            slotPageParms.callback_form_list.add(formUtil.startFieldBlock("Select Location and Time:"));

            
            List<Object> active_locations = new ArrayList<Object>();
            List<Object> open_locations = new ArrayList<Object>();
            List<Object> closed_locations = new ArrayList<Object>();

            String time = Utilities.get24HourTime(cparmD.time);
            
            String closed_message = "Please Call the Club";
            
            // add custom location closed wording here
            if (club.equals("stalbans")) {
                
                closed_message = "Closed";
            }

            ArrayList<diningLocation> locations = diningUtil.getDiningLocations(organization_id, days_in_advance, parmD.location_id, parmD.event_id, con_d);

            List<diningMealPeriod> mealPeriods;
            long[] locStartEnd = new long[3];

            // Add location radio buttons with time/covers selection in each
            for (diningLocation loc : locations) {

                //int location_id = Integer.parseInt(loc[0]);
                List<Map<String, String>> locationTimes;
                boolean closed = false;
                boolean oversized = false;
                boolean selected = parmD.id > 0 && parmD.location_id == loc.id && cparmD.date == parmD.date;

                List<Object> locations_object = open_locations;

                //String time = (selected)?Utilities.get24HourTime(parmD.time):"";

                locationTimes = diningUtil.getLocationTimesList(organization_id, loc.id, cparmD.date, parmD.event_id);

                if (cparmD.date == today && parmD.event_id < 1) {
                    mealPeriods = diningUtil.getMealPeriods(loc.id, parmD.event_id, cparmD.date, con_d);
                    locStartEnd = diningUtil.getStartEndDefault(organization_id, loc.id, parmD, mealPeriods, req, con_d);
                    cparmD.debug.put(loc.name+": StartEndDefault", locStartEnd);
                }
                
                boolean found = false;

                /*
                 * TODO: if locationTimes.size() == 0, there was probably an error communicating with the dining server
                 * but it may happen for other reasons (not sure if the dining server would ever have a valid reason to return
                 * empty) 
                 */

                Map<String, Object> timeSelectMap = new LinkedHashMap<String, Object>();

                // Create map of times
                for (Map<String, String> timeMap : locationTimes) {
                    String tmTime = timeMap.get("time");
                    boolean add_this = true;
                    if (tmTime != null) {
                        if (tmTime.equals("closed")) {
                            closed = true;
                        } else {
                            if (cparmD.date == today && parmD.event_id < 1) {
                                // Check if this time is outside any custome close times
                                if (timeUtil.getClubUnixTime(req, cparmD.date, Utilities.getIntTime(timeMap.get("value"))) < locStartEnd[diningUtil.START]) {
                                    add_this = false;
                                    int[] debug_datetime = timeUtil.getClubDateTime(req, locStartEnd[diningUtil.START]);
                                    cparmD.debug.put(loc.name+" TIME:"+timeMap.get("value")+" Less than start", debug_datetime[timeUtil.DATE]+" "+debug_datetime[timeUtil.TIME]);
                                }
                            }
                            if (add_this) {
                                timeSelectMap.put(timeMap.get("text"), timeMap.get("value"));
                                if (timeMap.get("value").equals(time)) {
                                    found = true;
                                }
                            }
                        }
                    }
                }

                if (!found && selected) {
                    // If we're editing this reservation, and our time doesnt't exist, add it.
                    closed = false;
                    timeSelectMap.put(Utilities.getSimpleTime(parmD.time), time);
                }

                if (timeSelectMap.isEmpty()) {
                    closed = true;
                }

                if (parmD.id > 0 && !selected && parmD.covers > loc.max_party_size) {
                    // Editing an existing registration with a part size that is too large for this location
                    oversized = true;
                }


                if (closed) {
                    closed_locations.add(formUtil.radioSelect("location_id_" + loc.id, "reservation[location_id]", loc.name + " <span class=\"smallSign\">" + closed_message + "</span>", "", cparmD.location_id == loc.id || locations.size() == 1, "warning"));
                    closed_locations.add(formUtil.startRadioBlock("location_block_" + loc.id, "", ""));
                    closed_locations.add(formUtil.fieldHtml(
                            "<div class=\"sub_instructions\">Sorry online reservations are not being taken at this time, please call the club.</div>"));
                    closed_locations.add(formUtil.endRadioBlock());
                } else if (oversized) {
                    closed_locations.add(formUtil.radioSelect("location_id_" + loc.id, "reservation[location_id]", loc.name + " <span class=\"smallSign\">Party Too Large</span>", "", cparmD.location_id == loc.id || locations.size() == 1, "warning"));
                    closed_locations.add(formUtil.startRadioBlock("location_block_" + loc.id, "", ""));
                    closed_locations.add(formUtil.fieldHtml(
                            "<div class=\"sub_instructions\">Sorry, " + loc.name + " has a maximum party size of " + loc.max_party_size + ".  Your current party size is " + parmD.covers + ".<br><br>  Please select a different date or location.</div>"));
                    closed_locations.add(formUtil.endRadioBlock());
                } else {

                    if (parmD.location_id == loc.id) {
                        // If this is the selected, existing, location, float it to the top of the list
                        locations_object = active_locations;
                    }

                    locations_object.add(formUtil.radioSelect("location_id_" + loc.id, "reservation[location_id]", loc.name, Integer.toString(loc.id), cparmD.location_id == loc.id || locations.size() == 1));
                    locations_object.add(formUtil.startRadioBlock("location_block_" + loc.id, "", ""));
                    locations_object.add(formUtil.select("time_" + loc.id, "reservation[reservation_time]", "Reservation Time", time, timeSelectMap));

                    int minPartySize = 1;
                    int maxPartySize = 0;

                    if (selected) {
                        // Editing an existing registration for this location
                        maxPartySize = Math.max(loc.max_party_size, parmD.covers);
                    } else {
                        // New reservation, or different location
                        // Use current max party size for this location
                        maxPartySize = loc.max_party_size;
                    }
                    if (parmD.id > 0 && parmD.covers > minPartySize) {
                        minPartySize = parmD.covers;
                    }

                    // Set our default covers, if needed.
                    int covers = cparmD.covers;
                    if (covers < 1) {
                        covers = diningUtil.getDefaultCovers(maxPartySize, loc.id, parmD.event_id, con_d, req);
                    }

                    // Create party size select list
                    Map<String, Object> partySizeMap = new LinkedHashMap<String, Object>();
                    for (int i = minPartySize - 1; i < maxPartySize; i++) {
                        partySizeMap.put(Integer.toString(i + 1), Integer.toString(i + 1));
                    }
                    locations_object.add(formUtil.select("covers_" + loc.id, "covers", "Party Size", Integer.toString(covers), partySizeMap));

                    locations_object.add(formUtil.endRadioBlock());

                }

            }

            slotPageParms.callback_form_list.addAll(active_locations);
            slotPageParms.callback_form_list.addAll(open_locations);
            slotPageParms.callback_form_list.addAll(closed_locations);
            if(locations.isEmpty()){
                slotPageParms.callback_form_list.add(formUtil.fieldHtml(tags.getSubInst("Sorry, there are no available locations for "+timeUtil.getStringDateMDYYYY(cparmD.date) +".  Please select a different date, or call the club for more information.","ftCenterText")));
            }
            slotPageParms.callback_form_list.add(formUtil.endFieldBlock());

            if (hide_special_occasions || parmD.event_id > 0) {
                slotPageParms.callback_map.put("reservation[occasion_id]", "0");
            } else {
                slotPageParms.callback_form_list.add(formUtil.startFieldBlock("Options:"));
                slotPageParms.callback_form_list.add(formUtil.select("reservation[occasion_id]", "reservation[occasion_id]", "Special Occasion", Integer.toString(cparmD.occasion_id), diningUtil.occasionMap));
                slotPageParms.callback_form_list.add(formUtil.endFieldBlock());
            }

            slotPageParms.page_start_button_go_back = true;

            // Create cancel button if user is the creator
            if (can_delete) {

                slotPageParms.callback_button_map.put("cancel_reservation",
                        formUtil.makeButton("Cancel Reservation", // Button name
                        //formUtil.keyValMap(new String[]{"slot_submit_action", "delete"}), // Set callback values
                        formUtil.keyValMap(new String[]{}), // Set callback values
                        formUtil.keyValMap(
                        new String[]{ // Other Options
                            /*
                            "confirm", "Are you sure you want to cancel this reservation?", // Click Confirmation
                            "class", "redButton" // Class
                             * 
                             */
                            "action", "cancelSlot"
                        })));
            }
            
            if(!parmD.is_event && parmD.id == 0 && diningUtil.canViewOthersSignups(req)){
                // If a new, non-event, reservation, add a button to show reservations for the selected date
                // Create continue button
                Map<String, Object> reservationButtonMap = formUtil.makeButton("List Reservations for "+timeUtil.getStringDateMDYYYY(cparmD.date));
                Map<String, Object> modalMap = new HashMap<String, Object>();
                Map<String, Object> methodMap = new HashMap<String, Object>();
                modalMap.put("method", "eventList");
                modalMap.put("data", methodMap);
                methodMap.put("type","dining_reservation_list");
                methodMap.put("base_url",Utilities.getBaseUrl(req, ProcessConstants.DINING_ACTIVITY_ID, club));
                methodMap.put("servlet", "Member_events2");
                methodMap.put("date",cparmD.date);
                reservationButtonMap.put("modal", modalMap);
                slotPageParms.callback_button_map.put("show_reservations", reservationButtonMap);
            }

            // Create continue button
            Map<String, Object> continueMap = formUtil.makeButton("Continue", formUtil.keyValMap(new String[]{"action", "continue"}));
            // Set required data and no entry message.
            Map<String, String> requireMap = new LinkedHashMap<String, String>();
            requireMap.put("reservation[location_id]", "You must first select an open location.");
            requireMap.put("reservation[reservation_time]", "You must first select a reservation time.");
            continueMap.put("require", requireMap);
            slotPageParms.callback_button_map.put("show_slot", continueMap);

            /*
             * TODO:
            if (parmD.id 
            
            == 0 && parmDining.areDiningReservationsPublic (parmD.organization_id, con_d) 
            ) {
            //out.println("<input id=\"view\" name=\"view\" type=\"button\" value=\"View Reservations\" onclick=\"view_reservations()\">");
            out.println("<br><a href=\"#\" data-ftlink=\"Dining_home?nowrap&view_reservations&date=" + parmD.date + "\" class=\"dining_event_modal\"><button id=\"view_res\" name=\"view_res\">View All Reservations for Selected Date</button></a>");
            out.println("<br><a href=\"#\" data-ftlink=\"Dining_home?nowrap&view_reservations&date=" + Utilities.getDate(con) + "\" class=\"dining_event_modal\"><button id=\"view_res\" name=\"view_res\">View All Reservations for Today</button></a>");
            }
            
             * */

            slotPageParms.debug.put("parmD", parmD);
            slotPageParms.debug.put("cparmD", cparmD);
            slotPageParms.debug.put("locations", locations);

            return;

        } // End initial prompt
        
        

        //
        //  Check if we should build the slot page
        //
        if (cparmD.stage == parmDining2.SLOT) {

            // Copy dining arrays to slotPage arrays

            // use cparmD for elemetns that may have been changed by the initial prompt
            // use parmD for elemts you want to pull from an existing reservation

            if (!parmD.names.isEmpty()) {

                slotPageParms.time_remaining = verifySlot.getSlotHoldTime(req);

                slotPageParms.show_check_num = !cparmD.hide_check_numbers;

                slotPageParms.player_count = cparmD.covers;

                slotPageParms.show_guest_types = true;
                slotPageParms.show_member_select = true;

                int[] date_a = timeUtil.parseIntDate(cparmD.date);
                slotPageParms.yy = date_a[timeUtil.YEAR];
                slotPageParms.mm = date_a[timeUtil.MONTH];
                slotPageParms.dd = date_a[timeUtil.DAY];

                slotPageParms.day = timeUtil.getDayOfWeek(cparmD.date);
                slotPageParms.stime = Utilities.getSimpleTime(cparmD.time);

                slotPageParms.location_disp = cparmD.location_name;
                slotPageParms.max_players = cparmD.maximum_party_size;
                slotPageParms.add_players = true;

                slotPageParms.allow_cancel = can_delete;

                slotPageParms.use_owner = true;
                //slotPageParms.lock_owner = parmD.id == 0;

                slotPageParms.owner = parmD.owner; // Take this from stored data
                slotPageParms.occasion_id = cparmD.occasion_id;
                slotPageParms.notes = parmD.member_special_requests; // Take this from stored data
                slotPageParms.notes_prompt = "Special Requests";
                slotPageParms.slot_submit_map.put("reservation[occasion_id]", "occasion_id");
                slotPageParms.slot_submit_map.put("reservation[member_special_requests]", "notes");
                slotPageParms.slot_submit_map.put("owner", "owner");
                slotPageParms.slot_submit_map.put("covers", "player_count");

                //slotPageParms.answers = parmD.answers; // We only have answers on an edit, so load from the unmodified source
                //slotPageParms.slot_submit_map.put("answers%", "answers");


                /*
                // ** TODO ** Encrypt ids and the the like with a time limmted key to minimize tampering
                slotId slot_id = new slotId();
                slot_id.location_id = cparmD.location_id;
                slot_id.reservation_id = parmD.id;
                slot_id.date = cparmD.date;
                slot_id.time = cparmD.time;
                slot_id.event_id = parmD.event.id;
                
                slotPageParms.slot_hdata = slot_id.getHashedJson();
                slotPageParms.slot_hkey = slot_id.iv;
                
                slotPageParms.slot_submit_map.put("slot_hdata", "slot_hdata");
                slotPageParms.slot_submit_map.put("slot_hkey", "slot_hkey");
                 */

                slotPageParms.location_id = cparmD.location_id;
                slotPageParms.reservation_id = parmD.id; // Take this from stored data
                slotPageParms.date = cparmD.date;
                slotPageParms.time = cparmD.time;
                slotPageParms.event_id = parmD.event.id; // Take this from stored data


                slotPageParms.slot_submit_map.put("reservation[location_id]", "location_id");
                slotPageParms.slot_submit_map.put("reservation_id", "reservation_id");
                slotPageParms.slot_submit_map.put("date", "date");
                slotPageParms.slot_submit_map.put("reservation[reservation_time]", "time");
                slotPageParms.slot_submit_map.put("event_id", "event_id");


                slotPageParms.debug.put("parmD", parmD);

                if (parmD.names.size() > slotPageParms.player_count) {
                    slotPageParms.player_count = parmD.names.size(); // Take this from stored data, if it's larger than posted data
                }

                slotPageParms.check_num_a = ArrayUtils.toPrimitive(parmD.check_num.toArray(new Integer[parmD.check_num.size()])); // Take this from stored data
                slotPageParms.user_a = parmD.usernames.toArray(new String[parmD.usernames.size()]); // Take this from stored data
                slotPageParms.player_a = parmD.names.toArray(new String[parmD.names.size()]); // Take this from stored data

                slotPageParms.slot_submit_map.put("player%", "player_a");
                slotPageParms.slot_submit_map.put("user%", "user_a");
                slotPageParms.slot_submit_map.put("check_num%", "check_num_a");
                //slotPageParms.slot_submit_map.put("guest_id%", "guest_id_a");

                if (parmD.id > 0) {
                    slotPageParms.reservation_number = "" + parmD.reservation_number;
                }

                // extract the cost for the event
                if (parmD.event_id > 0) {

                    slotPageParms.slot_type = "Event";
                    slotPageParms.name = parmD.event.name; // Take this from stored data
                    slotPageParms.show_name = true;

                    slotPageParms.meal_options = parmD.event.mealOptions;
                    slotPageParms.meal_option_a = parmD.price_category.toArray(new String[parmD.price_category.size()]); // Take this from stored data
                    slotPageParms.slot_submit_map.put("meal_option%", "meal_option_a");
                }
            }

            return; 

        } // End Build slot page

        // We shouldn't ever get here.
        slotPageParms.page_start_button_go_back = true;
        slotPageParms.page_start_title = "Unable to proccess request.";
        slotPageParms.page_start_notifications.add("Unknown error processing reservation.  Please try again.");
        return;

    }

    private boolean isEventDay(parmDining2 parmD, Connection con, Connection con_d) {

        // display any applicable event notices

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        boolean result = false;

        try {

            pstmt = con_d.prepareStatement(""
                    + "SELECT id, name, members_can_make_reservations "
                    + "FROM events "
                    + "WHERE organization_id = ? AND date = '" + parmD.date + "' AND members_can_make_reservations = true");

            pstmt.setInt(1, parmD.organization_id);
            //pstmt.setString(2, "" + parmD.date);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                result = true;
            }

        } catch (Exception exc) {

            Utilities.logError("Dining_slot.output_form: event lookup: Err=" + exc.toString());

        } finally {

            try {
                rs.close();
            } catch (Exception ignore) {
            }

            try {
                pstmt.close();
            } catch (Exception ignore) {
            }

        }

        return result;

    }

    private List<String> getClubMessage(parmDining2 cparmD, parmDining2 parmD, HttpServletRequest req) {
        
        String club = Utilities.getSessionString(req, "club", "");

        StringBuilder message = new StringBuilder();
        
        List<String> result = new ArrayList<String>(); 

        if (club.equals("mirabel")) {

            message.append("<h2>Please note the following FlexRez guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted for groups sizes of 6 or less</li>");
            message.append("<li>Reservations made online, must be done at a minimum of 24 hours in advance</li>");
            message.append("<li>Times are limited to 25 people per half hour.  If your desired time isn't visible, you may either choose a different time, or call the club for further assistance</li>");
            message.append("</ul>");
            message.append("<p>If you have any questions please call 480.437.1500 or email <a href=\"mailto:reservations@mirabel.com\">reservations@mirabel.com</a></p>");

        } else if (club.equals("thelegendclubs")) {

            message.append("<p><b>Note:</b>&nbsp; We do allow same day reservations.&nbsp; If you do not see your desired dining time or dining location available, please call the club, this is due to a high volume of reservations.&nbsp; For online reservations we do have a maximum table size reservation limit.&nbsp; If you have reservation count that is higher then the maximum limit, please call the club so we may accommodate your large group accordingly.</p>");

        } else if (club.equals("esterocc")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>The casual dining area is called the Dining Room, and the bar area is called Mulligans.</li>");
            message.append("<li>Reservations made online are allowed for the same day. Same day reservations do cut off 30 minutes before the breakfast, lunch and dinner times begin at the club.</li>");
            message.append("<li>If your desired time isn't visible, you may either choose a different time, or call the club for further assistance as tables may still be available.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.</li>");
            message.append("</ul>");

        } else if (club.equals("charlottecc")) {

            message.append("<ul>");
            message.append("<li>Thank you in advance for honoring your reservation time. In order to best serve and provide you with a quality dining experience, we politely request that you and your party arrive to the dining room at, or before, your reservation time. Please feel free to join us prior to your reservation for a cocktail.</li>");
            message.append("<li>We welcome large parties! If you would like to make a reservation for a party of more than 6 guests, please call the Front Desk. 704-334-0836.</li>");
            message.append("</ul>");

        } else if (club.equals("minikahda")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted the same day up until 30 minutes before the breakfast, lunch and dinner seating times begin at the Club.</li>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>Please remember NO Highchairs or Booster Seats allowed in the Dining Room or on the Dining Room Patio.</li>");
            message.append("</ul>");

        } else if (club.equals("oneidagcc")) {  //  || club.equals("demov4")

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted for groups sizes of 8 or less, 4 or less on the Terrace</li>");
            message.append("<li>Reservations made online are accepted same day. Reservations must be made 2 hours before the lunch or dinner meal period begins at the Club</li>");
            message.append("<li>Times are limited to 15 people per quarter hour. if your desired time isn't visible, you may either choose a different time, or call the Club for further assistance</li>");
            message.append("</ul>");
            message.append("<p><b>If you have any questions please call 920.498.6683 or email cjsmith@TroonGolf.com</b></p>");

        } else if (club.equals("golfcrestcc")) {

            message.append("<h2>Thank you for making your reservation in advance.</h2>");
            message.append("<p>When making your breakfast, lunch or dinner reservation an hour or less before the Club's dining times begin, we ask that you call the Club at 281-485-4323, to ensure our staff have time to prepare for your party.</p>");

        } else if (club.equals("ccyork")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full.  Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.  If you do not receive this email, the club did not receive your reservation!  Please call the Club if you do not receive a confirmation within 5 minutes of your reservation being made online confirming your new reservation.  You may also confirm the status of all of your current reservations online at: http://www.ccyork.org/mybookings</li>");
            message.append("<li>If you have any questions please call 717.843.8078 or email reservations@ccyork.org for assistance.</li>");
            message.append("</ul>");

        } else if (club.equals("interlachen")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>The main level bar area of the Club is called the Bobby Jones Pub the lower level bar area of the Club is called the Willie Kidd Pub. Please note the Bobby Jones Pub will not be available for lunch reservations during the summer.</li>");
            message.append("<li>Reservations made online are allowed for the same day. Same day reservations do cut off 2 hours before the breakfast, lunch and dinner times begin at the Club.</li>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.</li>");
            message.append("</ul>");

        } else if (club.equals("zzzdemov4")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.  If you do not receive this email, the club did not receive your reservation!  Please call the Club if you do not receive a confirmation within 5 minutes of your reservation being made online confirming your new reservation.  You may also confirm the status of all of your current reservations online at: http://www.ccyork.org/mybookings</li>");
            message.append("<li>If you have any questions please call 717.843.8078 or email reservations@ccyork.org for assistance.</li>");
            message.append("</ul>");

        } else if (club.equals("pinehurstcountryclub")) {

            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail. If you do not receive this e-mail, the Club did not receive your reservation. Please call the Club if you do not receive this confirmation within 5 minutes of making your reservation online.</li>");
            message.append("</ul>");

        } else if (club.equals("denvercc")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted the same day up until 2 hours before service begins for breakfast, lunch and dinner.</li>");
            message.append("<li>If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>If you would like accelerated service, please note it in your comments and we will preset menus and notify your server of the request.</li>");
            message.append("</ul>");

        } else if (club.equals("tavistockcc")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>On specific dates certain areas of the Club may be closed for Club events. We may need to move your ala carte reservation location if this occurs.</li>");
            message.append("<li>The Bar area of the Club is called the Lounge and the casual dining room is the Grille, please be cautious when making reservations.</li>");
            message.append("<li>Reservations made online are allowed for the same day. Same day reservations do cut off 2 hours before the breakfast, lunch and dinner times begin at the club.</li>");
            message.append("<li>Times are limited to 30 people per half hour. If you do not see your desired time available, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.</li>");
            message.append("</ul>");

        } else if (club.equals("altolakes")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>We allow online reservations for the Lounge, First Dining Room and Second Dining Room for up to 10 people. If your party size is larger then 10 please call the Club to make your reservation.</li>");
            message.append("<li>Reservations made online are allowed up to 2 hours in advance before the meal period for Breakfast, Lunch and Dinner begins. If your desired time isn't visible, it is due to the time slot being full. Please select a different time or call the Club for further assistance.</li>");
            message.append("<li>Due to high volume on Tuesdays, Fridays and Saturdays it is not always possible to accommodate special requests such as specific wait staff and tables.  This may also alter the dining room you request. Please specify dress code needs when requesting the Lounge, First Dining Room and Second Dining Room. Thank you for your cooperation and understanding and we look forward to seeing you at the Club!</li>");
            message.append("</ul>");

        } else if (club.equals("lacumbrecc")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are taken up to 30 minutes before the lunch and dinner meal periods begin at the Club. If you do not see your desired time available it is because the advance online reservation time limit has expired or the time may be full with other reservations. Please feel free to contact the Club at 805-687-2421.</li>");
            message.append("</ul>");

        } else if (club.equals("mediterra")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Please select the location in which you would like to dine. Once you complete the dining reservation you will be prompted to choose a dining area preference.</li>");
            message.append("<li>We will try to accommodate all dining area preferences and special requests. However, due to capacity limits and the potential for inclement weather, dining area preferences may need to be adjusted by Club Management.</li>");
            message.append("<li>Once your reservation request is complete, you will receive a confirmation e-mail.</li>");
            message.append("</ul>");

        } else if (club.equals("ccdcranch")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Reservations for a la carte dining can be made 15 days in advance.</li>");
            message.append("<li>Reservations may be made for parties of 6 or less. If your party is larger than 6, please call 480.342.7222</li>");
            message.append("<li>The Wine bar and Piano bar are open seating and on a first come, first serve basis.</li>");
            message.append("</ul>");

        } else if (club.equals("kiawahislandclub")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted for party sizes of six or less.</li>");
            message.append("<li>Reservations made online, must be done three days in advance to best accommodate our Members.</li>");
            message.append("<li>Please note, times are limited per half hour. If your desired time isn't available, please contact Member Services at 843.768.6120 or MemberServices@Kiawah.com.</li>");
            message.append("</ul>");

        } else if (club.equals("bergamont")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>Online reservations are accepted for party sizes of six or less.</li>");
            message.append("<li>Reservations made online must be done at a minimum of 24 hours in advance.</li>");
            message.append("<li>We will make every attempt to best accommodate special requests, but specific requests may not be guaranteed for reservations made online.</li>");
            message.append("<li>If your desired time is not available, you have a reservation larger than 6 guests, or you have any additional questions, please contact the Club.</li>");
            message.append("<li>If you have any questions or require further assistance, please contact Aeryn Barry at abarry@thelegendatbergamont.com or call the Club at 608-291-2400.</li>");
            message.append("</ul>");

        } else if (club.equals("rivertoncc")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>If your desired time isn't available, the time slot is full. Please select a different time or call the club for further assistance.</li>");
            message.append("<li>After your reservation has been made online you will receive a confirmation email. If you do not receive a confirmation e-mail within 5 minutes, your reservation was not submitted, please call the club. You may also confirm the status of your current reservations "
                    + "online by viewing your <a href=\"http://www.therivertoncountryclub.com/right/club-calendar-15.html\">Club Calendar</a></li>");
            message.append("<li>Table on the Patio cannot be reserved. If you would like to request a table on the Patio, please make a note in the special request box. We will do our best to accommodate your request.</li>");
            message.append("<li>If you have any questions, please call 856-829-5500 or email us at reservations@rivertoncc.com</li>");
            message.append("</ul>");

        } else if (club.equals("baldpeak")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li><u>Cancellation Policy.</u>  There is a 48-hour cancellation policy for all Club social events, i.e., Dinner Dances, 4th of July, etc.  Anyone with a reservation that is not cancelled within 48 hours, which cannot be replaced by someone on a waiting list, will be charged for the event in full. Cancellations due to medical or unexpected emergencies must be submitted in writing to the House Committee Chair.</li>");
            message.append("<li>There is a fee of $25 per person for all a la carte reservations that do not show or cancel by noon the day of the reservation date.</li>");
            message.append("</ul>");

        } else if (club.equals("brooklawn")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>For events,  you must cancel your reservation 24 hours in advance or there will be a charge.  (this does NOT include regular dining, only events)</li>");
            message.append("</ul>");

        } else if (club.equals("roccdallas")) {
            
            message.append("<h2>Please note the following Dining guidelines:</h2>");
            message.append("<ul>");
            message.append("<li>The Creek is our all adult dining restaurant.  All diners must be 21 years of age.</li>");
            message.append("<li>The Terrace Grill is our family dining restaurant.  It is the responsibility of the parents to monitor all of their children’s activities while dining in the Terrace Grill.  Please be sure all children do not play on the golf course.</li>");
            message.append("<li>Reservations made online are allowed for the same day.  Same day reservations do cut off 2 hours before the reservation times begin.  Please be sure to specify if any diners in your reservation will need a highchair or a booster.</li>");
            message.append("<li>If your desired time isn’t visible, you may either choose a different time, or call the club for further assistance as tables may still be available.</li>");
            message.append("<li>After your reservation is made online you will receive a new reservation confirmation e-mail.</li>");
            message.append("<li>Proper attire is required.  Please refer to the clubhouse dress code.</li>");
            message.append("</ul>");

        }
 
        if(message.length() > 0){
            result.add(message.toString());
        }
        
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}