/***************************************************************************************     
 *   Member_jump:  This servlet will process a request from Member_slot (verify) to 
 *                 process the transition from the servlet w/o frames back to Member_sheet
 *                 which uses frames.
 *
 *
 *   called by:  Member_slot verify processing when res is complete or cancelled
 *
 *
 *   parms passed by Member_slot:
 *
 *               index = index (index value for which day's tee sheet to display
 *               course = name of course
 *
 *   parms passed by Member_select:
 *
 *               calDate = date
 *               course = name of course
 *
 *
 *   created: 9/29/2002   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *       10-14-12   Added call to Utilities.validateRequestedActivity method when switching activities.
 *        1/11/12   The event name of "none" will now also route the user to the event listing page, the same as with passing "clubessential".
 *       11/17/11   Add switch capabilities
 *       11/10/11   New skin changes - if new skin don't call frameset, rather just direct to the announcement page
 *        9/06/11   If an eventname parameter of "clubessential" is passed, go to the general event listing page instead of to a specific event.
 *       12/21/09   Updated activity method - no longer using parent_id or group_id
 *       10/30/09   Add index=888 return (Member_searchmem) to activity returns.
 *        4/30/09   Get today's adjusted date in calDate method to determine the tee sheet index.
 *        9/12/06   Changes for TLT System
 *        3/24/05   Custom for Oakmont and Saucon Valley - use 365 day calendars - come here from 
 *                  member_select with course and calDate.
 *       12/07/04   Version 5 - add support for lessons.
 *        7/18/03   Enhancements for Version 3 of the software.
 *        2/13/03   Bob P.  Ver 3 Changes - Add support for Member_evntSingUp
 *       12/23/02   Bob P.  Ver 2 Changes
 *                  Add 'course' parm and change html files for new page format.
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.BigDate;
import com.foretees.common.Utilities;


public class Member_jump extends HttpServlet {
                

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 //*****************************************************
 // Process a GoBack (return w/o changes) from Member_slot
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }


 //*****************************************************************************************************
 //  Gets control from Member_slot or Member_evntSignUp (verify) when reservation complete or cancelled
 //*****************************************************************************************************
 //
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

    HttpSession sess = SystemUtils.verifyMem(req, out);     // check for intruder
    if (sess == null) return;
    Connection con = SystemUtils.getCon(sess);

    //
    //  See if we are in teh timeless tees mode
    //
    int tmp_tlt = (Integer)sess.getAttribute("tlt");
    boolean IS_TLT = (tmp_tlt == 1) ? true : false;

    boolean new_skin = ((String)sess.getAttribute("new_skin")).equals("1");

    String jumps = "0";                                     // default to zero (jump index for _sheet)
    String num = "";
    int activity_id = 0;
    //String title = "";

    if (req.getParameter("new_skin") != null) {

        new_skin = new_skin == false;
        sess.setAttribute("new_skin", (new_skin) ? "1" : "0");
    }

    //
    // Jumps to call out to process requests
    //
    if (req.getParameter("switch") != null) {               // if call is from Member_activity_slot

        num = req.getParameter("activity_id");              //  get the index value passed
        try { activity_id = Integer.parseInt(num); }
        catch (NumberFormatException e) {}
        num = ""; // reset

        if ( Utilities.validateRequestedActivity(activity_id, con) ) {
            
            HttpSession session = req.getSession(false);        // get the session object
            session.setAttribute("activity_id", activity_id);   // set new default activity

            if (new_skin) {

                try {

                    Member_announce announce = new Member_announce();

                    announce.doGet(req, resp);

                } catch (Exception exc) {

                    out.println("ERROR: " + exc.toString());

                }

                out.close();
                return;

            } else {

                out.println("<html><head><title>ForeTees Member System</title>");
                out.println("</head>");
                out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
                out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");
                out.println("<frame name=\"bot\" src=\"Member_announce\" marginheight=\"1\">");
                out.println("</frameset>");

                out.println("</html>");
                out.close();
                return;

            }
            
        } // end if new activity allowed

    }

    if (req.getParameter("activity") != null) {         // if call is from Member_activity_slot

        activity(req, out);
        return;
    }


   if (req.getParameter("name") != null) {        // if event name passed, then call is from Member_evntSignUp

      event(req, out);                    
      return;
   }
     
   if (req.getParameter("lesson") != null) {        // if call is from Member_lesson

      lesson(req, out);
      return;
   }

   if (req.getParameter("calDate") != null) {        // if call is from Member_select 

      calDate(req, out, IS_TLT);
      return;
   }

   //
   //  Control came from Member_slot
   //
   String index = req.getParameter("index");         //  get the index value passed
   String course = req.getParameter("course");       //  get the course name passed

   if (req.getParameter("jump") != null) {

        jumps = req.getParameter("jump");         //  get the jump index
   }

    if (new_skin) {

        try {

            Member_announce announce = new Member_announce();

            announce.doGet(req, resp);

        } catch (Exception exc) {

            out.println("ERROR: " + exc.toString());

        }

    } else {

        //
        //  Build the HTML page that will automatically jump to Member_sheet
        //
        out.println("<html><head><title>ForeTees Member Tee Times</title>");
        out.println("</head>");

        out.println("<frameset rows=\"82,*\" frameborder=\"0\" framespacing=\"5\" border=\"0\">");
        out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");
        out.println("<frame name=\"bot\" src=\""+((IS_TLT) ? "MemberTLT_sheet" : "Member_sheet")+"?index=" + index + "&course=" + course + "&jump=" + jumps + "\" marginwidth=\"3\" marginheight=\"5\">");
        out.println("</frameset>");

        out.println("</html>");

    }

    out.close();

 }  // end of doPost


 // *********************************************************
 //  Control from Member_evntSignUp - return to Member_events2
 // *********************************************************

 private void event(HttpServletRequest req, PrintWriter out) {


   String name = req.getParameter("name");           //  get the event name value passed
   String course = req.getParameter("course");       //  get the course name passed

   String index = "";

   if (req.getParameter("index") != null) {

      index = req.getParameter("index");         //  get the index (who to return to)
   }

   //
   //  Build the HTML page that will automatically jump to Member_events2
   //
   out.println("<HTML><HEAD><Title>ForeTees Member Events</Title>");
   out.println("</HEAD>");

   out.println("<frameset rows=\"82,*\" frameborder=\"0\" framespacing=\"5\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");
   if (name.equals("clubessential") || name.equalsIgnoreCase("none")) {
       out.println("<frame name=\"bot\" src=\"Member_events\" marginwidth=\"3\" marginheight=\"5\">");
   } else {
       out.println("<frame name=\"bot\" src=\"Member_events2?name=" + name + "&course=" + course + "&index=" + index + "\" marginwidth=\"3\" marginheight=\"5\">");
   }
   out.println("</frameset>");
   out.println("</html>");
   out.close();
 }


 // *********************************************************
 //  Control from Member_select - jump to Member_sheet
 // *********************************************************
 
 private void calDate(HttpServletRequest req, PrintWriter out, boolean pIS_TLT) {

    //
    //************************************************************
    //   Convert the date received from mm/dd/yyyy to 'index' value
    //************************************************************
    //
    String course = req.getParameter("course");         //  get the course name passed
    String calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)
    String todays = req.getParameter("thisDate");       //  get today's date - adjusted for time zone (yyyymmdd)

    //
    //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
    //
    StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

    String num = tok.nextToken();                    // get the mm value

    int month = Integer.parseInt(num);

    num = tok.nextToken();                    // get the dd value

    int day = Integer.parseInt(num);

    num = tok.nextToken();                    // get the yyyy value

    int year = Integer.parseInt(num);

    //
    //  Convert today's adjusted date from string (yyyymmdd) to ints (month, day, year)
    //
    int today = Integer.parseInt(todays);   
    int yy = today / 10000;
    int mm = (today - (yy * 10000)) / 100;
    int dd = today - ((yy * 10000) + (mm * 100));


    //
    // Calculate the number of days between today and the date requested (=> ind)
    //
    //BigDate today = BigDate.localToday();                 // get today's date
    BigDate todaydate = new BigDate(yy, mm, dd);            // get today's adjusted date (adjusted for time zone)
    BigDate thisdate = new BigDate(year, month, day);       // get requested date

    int ind = (thisdate.getOrdinal() - todaydate.getOrdinal());   // number of days between

    //
    //  Build the remainder of the HTML page that will automatically jump to Member_sheet
    //
    out.println("<HTML><HEAD><Title>ForeTees Member Tee Sheet</Title>");
    out.println("</HEAD>");
    out.println("<frameset rows=\"82,*\" frameborder=\"0\" framespacing=\"5\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\""+((pIS_TLT) ? "MemberTLT_sheet" : "Member_sheet")+"?index=" + ind + "&course=" + course + "\" marginheight=\"1\">");
    out.println("</frameset>");
    out.println("</html>");
    out.close();
 }


 // *********************************************************
 //  Control from Member_lesson - return to it
 // *********************************************************

 private void lesson(HttpServletRequest req, PrintWriter out) {


   String calDate = "";
   String ltype = "";

   String proid = req.getParameter("proid");         //  get the id of the pro selected
   String index = req.getParameter("index");         //  get the index value passed

   if (req.getParameter("calDate") != null) {

      calDate = req.getParameter("calDate");         //  get the date
   }
   if (req.getParameter("ltype") != null) {

      ltype = req.getParameter("ltype");             //  get the lesson type
   }

   //
   //  Build the HTML page that will automatically jump to Member_lesson
   //
   out.println("<HTML><HEAD><Title>ForeTees Member Lessons</Title>");
   out.println("</HEAD>");

   out.println("<frameset rows=\"82,*\" frameborder=\"0\" framespacing=\"5\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");
   if (!index.equals( "" )) {     // if orignated from Member_teelist
      out.println("<frame name=\"bot\" src=\"Member_teelist\" marginwidth=\"3\" marginheight=\"5\">");
   } else {
      if (!ltype.equals( "" )) {
         out.println("<frame name=\"bot\" src=\"Member_lesson?proid=" +proid+ "&calDate=" +calDate+ "&ltype=" +ltype+ "\" marginwidth=\"3\" marginheight=\"5\">");
      } else {
         out.println("<frame name=\"bot\" src=\"Member_lesson?proid=" +proid+ "\" marginwidth=\"3\" marginheight=\"5\">");
      }
   }
   out.println("</frameset>");
   out.println("</html>");
   out.close();
 }


 private void activity(HttpServletRequest req, PrintWriter out) {

    //String sheet_id = "";
    //String group_id = "";
    String last_tab = "";
    String date = "";
    String activity_id = "";
    String index = "";
    String layout = "";


/*
    if (req.getParameter("sheet_id") != null) {

        sheet_id = req.getParameter("sheet_id");
    }
    
    if (req.getParameter("group_id") != null) {

        group_id = req.getParameter("group_id");
    }
*/
    if (req.getParameter("activity_id") != null) {

        activity_id = req.getParameter("activity_id");
    }

    if (req.getParameter("date") != null) {

        date = req.getParameter("date");
    }

    if (req.getParameter("index") != null) {

        index = req.getParameter("index");
    }

    if (req.getParameter("last_tab") != null) {

        last_tab = req.getParameter("last_tab");
    }

    if (req.getParameter("layout_mode") != null) {

        layout = req.getParameter("layout_mode");
    }


    //
    //  Build the HTML page that will automatically jump to Member_sheet
    //
    out.println(SystemUtils.HeadTitle("ForeTees Activity Time Management"));

    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Member_maintop\" marginheight=\"1\" scrolling=\"no\">");

    if (index.equals("999")) {
        out.println("<frame name=\"bot\" src=\"Member_teelist\" marginheight=\"1\">");
    } else if (index.equals("998")) {
       out.println("<frame name=\"bot\" src=\"Member_teelist_list\" marginheight=\"1\">");
    } else if (index.equals("888")) {
       out.println("<frame name=\"bot\" src=\"Member_searchmem\" marginheight=\"1\">");
    } else {
        out.println("<frame name=\"bot\" src=\"Member_gensheets?date=" + date + "&layout_mode=" + layout + "&activity_id=" + activity_id + "&last_tab=" + last_tab + "\" marginheight=\"1\">"); // &parent_id=" + parent_id + "&sheet_id=" + sheet_id + "
    }
    out.println("</frameset>");

    out.println("</html>");
    out.close();
 }

}
