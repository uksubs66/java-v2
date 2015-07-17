/***************************************************************************************     
 *   Proshop_jump:  This servlet will process a request from Proshop_slot (verify) to
 *                  process the transition from the servlet w/o frames back to Proshop_sheet
 *                  which uses frames.
 *
 *
 *   called by:  Proshop_slot verify processing when res is complete or cancelled.
 *               Proshop_select to get to _sheet with unique header.
 *               Proshop_evntSignUp to return to Proshop_events2.
 *               Proshop_lott to return to Proshop_sheet.
 *               Proshop_lesson.
 *
 *
 *   parms passed by Proshop_slot:
 *
 *               index = index (index value for which day's tee sheet to display)
 *               index2 = index2 (index value for Proshop_mlottery)
 *               course = name of course
 *               name = name of event (if from Proshop_evntSignUp)
 *               jump = jump index for Proshop_sheet
 *               search = jump indicator for Proshop_searchmem
 *
 *   jump parm passed by Proshop_select and Proshop_lott:
 *
 *               jump=select (_select)
 *               jump2 = jump index for Proshop_sheet
 *
 *
 *   created: 1/01/2003   Bob P.
 *
 *   last updated:      ******* keep this accurate *******
 *
 *       11/21/12   Utiltize a unified frameset output method
 *        8/31/11   Added processing to reroute users back to the lesson set pages if they entered a lesson time from there.
 *        6/04/10   Add #jump to the url string when returning to _sheet.
 *       12/21/09   Updated activity method - no longer using parent_id or group_id
 *        5/11/09   Added additional hook for activities to handle defaulting to correct tab on returns
 *        8/28/09   Add showlott option to tee sheet so pro can pre-book lottery times - make sure we always return this to sheet (case 1703).
 *        5/11/09   Added hook for activities to handle returns
 *        7/06/08   Added hook for wait list for handling returns
 *        1/05/07   Added oldsheets function for TLT system
 *       10/02/06   Changes for TLT system
 *       10/26/04   Ver 5 - add support for Lesson Book returns.
 *        7/18/03   Enhancements for Version 3 of the software.
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
import com.foretees.common.Connect;

public class Proshop_jump extends HttpServlet {
                

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 //*****************************************************
 // Process a GoBack (return w/o changes) from Proshop_slot
 //*****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }


 //***************************************************************************************************
 //  Gets control from Proshop_slot (verify) or Proshop_lott when reservation complete or cancelled
 //***************************************************************************************************
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

    HttpSession sess = SystemUtils.verifyPro(req, out);       // check for intruder
    if (sess == null) return;
    
    Connection con = Connect.getCon(req);                      // get DB connection

    String [] dayShort_table = { "inv", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };

    //
    //  Num of days in each month
    //
    int [] numDays_table = { 0, 31, 0, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    //
    //  Num of days in Feb indexed by year starting with 2000 - 2040
    //
    int [] feb_table = { 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29,  +
                            28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29, 28, 28, 28, 29 };

    int month = 0;
    int day = 0;
    int day_num = 0;
    int ind = 0;
    int activity_id = 0;
    int jump = 0;

    String title = "";
    String index = "";
    String name = "";
    String num = "";
    String jumps = "0";                            // default to zero (jump index for _sheet)
    String course = "";


    //
    // Jumps to call out to process requests
    //
    if (req.getParameter("switch") != null) {           // if call is from Proshop_activity_slot

        num = req.getParameter("activity_id");          //  get the index value passed
        try { activity_id = Integer.parseInt(num); }
        catch (NumberFormatException e) {}
        num = ""; // reset
        
        HttpSession session = req.getSession(false);        // Create a session object
        session.setAttribute("activity_id", activity_id);   // activity indicator
        
        // if golf then go to the announcement page
        //if (activity_id == 0) {

            outputFrameset(title, "Proshop_announce", out);
            /*
            out.println(SystemUtils.HeadTitle(title));
            out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
            out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
            out.println("<frame name=\"bot\" src=\"Proshop_announce\" marginheight=\"1\">");
            out.println("</frameset>");

            out.println("</html>");
            */
            out.close();
            return;
            
        //} // end if golf
    }
    
    if (req.getParameter("activity") != null) {         // if call is from Proshop_activity_slot

        activity(req, out);
        return;
    }

    if (req.getParameter("waitList") != null) {         // if call is from Proshop_waitlist_slot

        waitList(req, out);
        return;
    }
    
    if (req.getParameter("newnotify") != null) {        // if call is from TLT_slot (for Proshop_notify) 

        newNotify(req, out);
        return;
    }
    
    if (req.getParameter("search") != null) {           // if call is from _slot (for Proshop_searchmem) 

        searchmem(req, out);
        return;
    }

    if (req.getParameter("lesson") != null) {           // if call is from Proshop_lesson

        lesson(req, out);
        return;
    }

    if (req.getParameter("index2") != null) {           // if call is from Proshop_lott

        lottery(req, out);
        return;
    }

    if (req.getParameter("name") != null) {             // if event name passed, then call is from Proshop_evntSignUp

        event(req, out);
        return;
    }

    if (req.getParameter("oldsheets") != null) {        // if oldsheets passed then it's from Proshop_dsheet  (TLT)

        oldsheets(req, out);
        return;
    }
    
    //
    // Process Request
    //
    String showlott = "";                              // default to No (do not show lottery times)
    
    if (req.getParameter("showlott") != null) {        // if time during a lottery

       showlott = req.getParameter("showlott");        // get the value
    }
    
    if (showlott.equals( "" ) || showlott == null) showlott = "no";    // default to no
      
    if (req.getParameter("course") != null) {

        course = req.getParameter("course");            //  get the course name passed
    }

    if (req.getParameter("jump") != null) {

        jumps = req.getParameter("jump");               //  get the jump index
    }

    if (!jumps.equalsIgnoreCase( "select" )) {
            
        //
        //************************************************************
        //  Control came from Proshop_slot or _lott
        //************************************************************
        //
        num = req.getParameter("index");         //  get the index value passed

        try {
            
            ind = Integer.parseInt(num);
            jump = Integer.parseInt(jumps);
        }
        catch (NumberFormatException e) {
        }
        
        //
        //   Adjust jump so we jump to the selected line minus 3 so its not on top of page
        //
        if (jump < 4 || jump > 999) {

           jump = 0;         // jump to top of page

        } else {

           jump -= 3;        // back up a few lines so desired row is not at the top
        }
   

        //
        //  Get today's date and then use the value passed to locate the requested date
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        while (ind != 0) {                          // if not today

            cal.roll(Calendar.DATE, true);              // roll ahead one day

            day = cal.get(Calendar.DAY_OF_MONTH);      // get new day

            if (day == 1) {
                cal.roll(Calendar.MONTH, true);          // adjust month if starting a new one
            }

            month = cal.get(Calendar.MONTH) + 1;        // adjust our new month

            if ((month == 1) && (day == 1)) {
                cal.roll(Calendar.YEAR, true);           // adjust year if starting a new one
            }

            ind = ind - 1;

        }   // end of while

        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);
        day_num = cal.get(Calendar.DAY_OF_WEEK);      // day of week (01 - 07)

        month = month + 1;                            // month starts at zero

        String dayShort_name = dayShort_table[day_num];   // get short name for day


        if (course.equals( "" )) {

            title = dayShort_name + " " + month + "/" + day + " Tee Sheet Page";
        } else {
            title = dayShort_name + " " + month + "/" + day + " " + course + " Tee Sheet Page";
        }
        

        //
        //  Build the HTML page that will automatically jump to Proshop_sheet
        //
        outputFrameset(title, "Proshop_sheet?index=" + num + "&course=" + course + "&showlott=" + showlott + "#jump" + jump, out);
        /*
        out.println(SystemUtils.HeadTitle(title));
        out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
        out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
        // out.println("<frame name=\"bot\" src=\"Proshop_sheet?index=" + num + "&course=" + course + "&jump=" + jumps + "&showlott=" + showlott + "\" marginheight=\"1\">");
        out.println("<frame name=\"bot\" src=\"Proshop_sheet?index=" + num + "&course=" + course + "&showlott=" + showlott + "#jump" + jump + "\" marginheight=\"1\">");
        out.println("</frameset>");

        out.println("</html>");
         */
        out.close();

    } else {

        //
        //************************************************************
        //  From Proshop_select
        //
        //   Convert the date received from mm/dd/yyyy to 'index' value
        //
        //   (index no longer passed from Proshop_select as of V4)
        //
        //************************************************************
        //
        String calDate = "";
        int year = 0;

        //
        //  make sure we have the date value
        //
        if (req.getParameter("calDate") != null) {

            calDate = req.getParameter("calDate");       //  get the date requested (mm/dd/yyyy)

        } else {

            out.println(SystemUtils.HeadTitle("Procedure Error"));
            out.println("<BODY><CENTER>");
            out.println("<BR><BR><H3>Access Procdure Error</H3>");
            out.println("<BR><BR>Required Parameter is Missing - Proshop_select.");
            out.println("<BR>Please exit and try again.");
            out.println("<BR><BR>If problem persists, report this error to ForeTees support.");
            out.println("<BR><BR>");
            out.println("<a href=\"/" +rev+ "/proshop_announce.htm\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        //
        //  Convert the index value from string (mm/dd/yyyy) to ints (month, day, year)
        //
        StringTokenizer tok = new StringTokenizer( calDate, "/" );     // space is the default token - use '/'

        num = tok.nextToken();                    // get the mm value
        month = Integer.parseInt(num);
        num = tok.nextToken();                    // get the dd value
        day = Integer.parseInt(num);
        num = tok.nextToken();                    // get the yyyy value
        year = Integer.parseInt(num);

        //
        //  Get today's date and then set the requested date to get the day name, etc.
        //
        Calendar cal = new GregorianCalendar();       // get todays date

        cal.set(Calendar.YEAR, year);                 // change to requested date
        cal.set(Calendar.MONTH, month-1);
        cal.set(Calendar.DAY_OF_MONTH, day);

        day_num = cal.get(Calendar.DAY_OF_WEEK);          // day of week (01 - 07)

        String dayShort_name = dayShort_table[day_num];   // get short name for day

        int req_date = (year * 10000) + (month * 100) + day;

        //
        // Calculate the number of days between today and the date requested (=> ind)
        //
        //BigDate today = BigDate.localToday();                 // get today's date
        //BigDate thisdate = new BigDate(year, month, day);     // get requested date

        //ind = (thisdate.getOrdinal() - today.getOrdinal());   // number of days between



        int date = (int)Utilities.getDate(con, 0);
/*
        int today_year = date / 10000;
        int temp = today_year * 10000;
        int today_month = date - temp;
        temp = today_month / 100;
        temp = temp * 100;
        int today_day = today_month - temp;
        today_month = today_month / 100;

        Calendar cal_today = new GregorianCalendar();
        cal_today.set(today_year, today_month-1, today_day);
        
        ind = (int)( (cal.getTimeInMillis() - cal_today.getTimeInMillis() ) / (1000 * 60 * 60 * 24) );

        if (ind < 0) ind = 0 - ind;

        out.println("<!-- cal=" + cal.getTimeInMillis() + ", cal_today=" + cal_today.getTimeInMillis() + ", ind=" + ind + " -->");

*/
      //out.println("<!-- req_date=" + req_date + ", today_date="+date);
           try {

                PreparedStatement pstmt1 = con.prepareStatement("" +
                       "SELECT DATEDIFF(?, ?) AS i;");
                pstmt1.clearParameters();
                pstmt1.setInt(1, req_date);
                pstmt1.setInt(2, date);
                ResultSet rs = pstmt1.executeQuery();
                if (rs.next()) {
                    ind = rs.getInt(1);
                }

           } catch (Exception e) {

               SystemUtils.buildDatabaseErrMsg(e.getMessage(), e.toString(), out, false);
           }

      //out.println("<!-- ind=" + ind + " -->");


        if (course.equals( "" )) {

            title = dayShort_name + " " + month + "/" + day + " Tee Sheet Page";
        } else {
            
            title = dayShort_name + " " + month + "/" + day + " " + course + " Tee Sheet Page";
        }

      //out.println("</head>");

        //
        //  Build the remainder of the HTML page that will automatically jump to Proshop_sheet
        //
        outputFrameset(title, "Proshop_sheet?index=" + ind + "&course=" + course + "&showlott=" + showlott, out);
        /*
        out.println(SystemUtils.HeadTitle(title));
        out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
        out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
        // out.println("<frame name=\"bot\" src=\"Proshop_sheet?index=" + ind + "&course=" + course + "&jump=" + jumps + "&showlott=" + showlott + "\" marginheight=\"1\">");
        out.println("<frame name=\"bot\" src=\"Proshop_sheet?index=" + ind + "&course=" + course + "&showlott=" + showlott + "\" marginheight=\"1\">");
        out.println("</frameset>");

        out.println("</html>");
        */
        out.close();
    }

 }  // end of doPost


 // *********************************************************
 //  Control from Proshop_lesson - return to it
 // *********************************************************

 private void oldsheets(HttpServletRequest req, PrintWriter out) {


    String tmp = "";
    if (req.getParameter("calDate") != null) {
        tmp += "?post&calDate=" + req.getParameter("calDate");
        tmp += "&course=" + req.getParameter("course");
    }
    
    //
    //  Build the HTML page that will automatically jump to Proshop_mlottery
    //
    outputFrameset("ForeTees Proshop", "Proshop_oldsheets" + tmp, out);
    /*
    out.println(SystemUtils.HeadTitle("ForeTees Proshop"));

    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\"Proshop_oldsheets" + tmp + "\"\" marginheight=\"1\">");
    out.println("</frameset>");

    out.println("</html>");
     *
     */
    out.close();
 }
 

 // *********************************************************
 //  Control from Proshop_lesson - return to it
 // *********************************************************

 private void lesson(HttpServletRequest req, PrintWriter out) {


   String proid = req.getParameter("proid");      
   String calDate = req.getParameter("calDate");    
   String jump = req.getParameter("jump");
   String caller = "";
   String url = "";

   int set_id = 0;

   if (req.getParameter("caller") != null && !req.getParameter("caller").equals("")) {

       caller = req.getParameter("caller");

       if (caller.equals("lessonset")) {
           set_id = Integer.parseInt(req.getParameter("set_id"));
       }
   }

   if (!caller.equals("") && set_id != 0) {
       if (caller.equals("lessonset")) {
       url = "Proshop_lesson?sets2&proid=" +proid+ "&set_id=" +set_id;
       }
   } else {
       url = "Proshop_lesson?proid=" +proid+ "&calDate=" +calDate+ "&jump=" +jump;
   }

   //
   //  Build the HTML page that will automatically jump to Proshop_mlottery
   //
   outputFrameset("ForeTees Proshop Lessons", url, out);
   /*
   out.println(SystemUtils.HeadTitle("Proshop Lesson"));
   out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
   if (!caller.equals("") && set_id != 0) {
       if (caller.equals("lessonset")) {
       out.println("<frame name=\"bot\" src=\"Proshop_lesson?sets2&proid=" +proid+ "&set_id=" +set_id+ "\" marginheight=\"1\">");
       }
   } else {
       out.println("<frame name=\"bot\" src=\"Proshop_lesson?proid=" +proid+ "&calDate=" +calDate+ "&jump=" +jump+ "\" marginheight=\"1\">");
   }
   out.println("</frameset>");

   out.println("</html>");
    *
    */
   out.close();
 }


 // *********************************************************
 //  Control from Proshop_lott - return to Proshop_mlottery
 // *********************************************************

 private void lottery(HttpServletRequest req, PrintWriter out) {


   String index2 = req.getParameter("index2");       //  get the index value passed
   String course = req.getParameter("course");       //  get the course name passed

   //
   //  Build the HTML page that will automatically jump to Proshop_mlottery
   //
   outputFrameset("ForeTees Proshop Lottery", "Proshop_mlottery?jump=yes&course=" + course + "&" + index2 + "=" + index2, out);
   /*
   out.println(SystemUtils.HeadTitle("Proshop Lottery"));

   out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
   out.println("<frame name=\"bot\" src=\"Proshop_mlottery?jump=yes&course=" + course + "&" + index2 + "=" + index2 + "\" marginheight=\"1\">");
   out.println("</frameset>");

   out.println("</html>");
    *
    */
   out.close();
 }


 // *********************************************************
 //  Control from Proshop_evntSignUp - return to Proshop_events2
 // *********************************************************

 private void event(HttpServletRequest req, PrintWriter out) {


   String name = req.getParameter("name");           //  get the event name value passed
   String course = req.getParameter("course");       //  get the course name passed

   //
   //  Build the HTML page that will automatically jump to Proshop_events2
   //
   outputFrameset("ForeTees Proshop Events", "Proshop_events2?name=" + name + "&course=" + course, out);
   /*
   out.println(SystemUtils.HeadTitle("ForeTees Proshop Events"));

   out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
   out.println("<frame name=\"bot\" src=\"Proshop_events2?name=" + name + "&course=" + course + "\" marginheight=\"1\">");
   out.println("</frameset>");

   out.println("</html>");
    *
    */
   out.close();
 }


 // *********************************************************
 //  Control from Proshop_slot - return to Proshop_searchmem
 // *********************************************************

 private void searchmem(HttpServletRequest req, PrintWriter out) {

   //
   //  Build the HTML page that will automatically jump to Proshop_searchmem
   //
   outputFrameset("ForeTees Proshop Search", "Proshop_searchmem", out);
   /*
   out.println(SystemUtils.HeadTitle("ForeTees Proshop Search"));

   out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
   out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
   out.println("<frame name=\"bot\" src=\"Proshop_searchmem\" marginheight=\"1\">");
   out.println("</frameset>");

   out.println("</html>");
    *
    */
   out.close();
 }

 
 // *********************************************************
 //  Handle requests from TLT system
 // *********************************************************

 private void newNotify(HttpServletRequest req, PrintWriter out) {
     
     
    String course = "";
    String yy = "";
    String mm = "";
    String dd = "";
     
    if (req.getParameter("course") != null) {

        course = req.getParameter("course");       //  get the course name passed
    }
    
    if (req.getParameter("yy") != null) {

        yy = req.getParameter("yy");       //  get the year
    }
    
    if (req.getParameter("mm") != null) {

        mm = req.getParameter("mm");       //  get the month
    }
    
    if (req.getParameter("dd") != null) {

        dd = req.getParameter("dd");       //  get the day of month
    }
    
    //
    //  Build the HTML page that will automatically jump to Proshop_sheet
    //
    outputFrameset("ForeTees Proshop Notifications", "Proshop_notify?course=" + course + "&mm=" + mm + ((!dd.equals("")) ? "&dd=" + dd : "") + "&yy=" + yy, out);
    /*
    out.println(SystemUtils.HeadTitle("New Member Notification"));

    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\"Proshop_notify?course=" + course + "&mm=" + mm + ((!dd.equals("")) ? "&dd=" + dd : "") + "&yy=" + yy + "\" marginheight=\"1\">");
    out.println("</frameset>");

    out.println("</html>");
    *
    */
    out.close();
 }
 
 
 private void waitList(HttpServletRequest req, PrintWriter out) {
     
    String returnCourse = "";
    String waitListId = "";
    String date = "";
    String day = "";
    String index = "";
     
    if (req.getParameter("returnCourse") != null) {

        returnCourse = req.getParameter("returnCourse");    //  get the course name passed
    }
    
    if (req.getParameter("waitListId") != null) {

        waitListId = req.getParameter("waitListId");        //  get the waitListId
    }
    
    if (req.getParameter("date") != null) {

        date = req.getParameter("date");                    //  get the date
    }
    
    if (req.getParameter("day") != null) {

        day = req.getParameter("day");                      //  get the name of the day
    }
    
    if (req.getParameter("index") != null) {

        index = req.getParameter("index");                  //  get the index
    }
    
    
    //
    //  Build the HTML page that will automatically jump to Proshop_sheet
    //
    outputFrameset("ForeTees Golf Shop Tee Time Management", "Proshop_waitlist?waitListId=" + waitListId + "&date=" + date + "&day=" + day + "&index=" + index + "&returnCourse=" + returnCourse, out);
    /*
    out.println(SystemUtils.HeadTitle("ForeTees Golf Shop Tee Time Management"));

    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\"Proshop_waitlist?waitListId=" + waitListId + "&date=" + date + "&day=" + day + "&index=" + index + "&returnCourse=" + returnCourse + "\" marginheight=\"1\">");
    out.println("</frameset>");

    out.println("</html>");
     * 
     */
    out.close();
 }


 private void activity(HttpServletRequest req, PrintWriter out) {

    String activity_id = "";
    String sheet_id = "";
    String last_tab = "";
    String date = "";
    String layout = "";
 
/*   
    String index = "";
    String group_id = "";
    String parent_id = "";

    if (req.getParameter("group_id") != null) {

        group_id = req.getParameter("group_id");
    }
    
    if (req.getParameter("parent_id") != null) {

        parent_id = req.getParameter("parent_id");
    }

    if (req.getParameter("index") != null) {

        index = req.getParameter("index");
    }
*/
    
    if (req.getParameter("sheet_id") != null) {

        sheet_id = req.getParameter("sheet_id");
    }
    
    if (req.getParameter("activity_id") != null) {

        activity_id = req.getParameter("activity_id");
    }
    
    if (req.getParameter("date") != null) {

        date = req.getParameter("date");
    }

    if (req.getParameter("last_tab") != null) {

        last_tab = req.getParameter("last_tab");
    }

    if (req.getParameter("layout_mode") != null) {

        layout = req.getParameter("layout_mode");
    }


    //
    //  Build the HTML page that will automatically jump to Proshop_sheet
    //
    outputFrameset("ForeTees Activity Time Management", "Proshop_gensheets?date=" + date + "&layout_mode=" + layout + "&sheet_id=" + sheet_id + "&activity_id=" + activity_id + "&last_tab=" + last_tab, out);

    /*
    out.println(SystemUtils.HeadTitle("ForeTees Activity Time Management"));

    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\"Proshop_gensheets?date=" + date + "&layout_mode=" + layout + "&sheet_id=" + sheet_id + "&activity_id=" + activity_id + "&last_tab=" + last_tab + "\" marginheight=\"1\">"); // &group_id=" + group_id + "
    out.println("</frameset>");

    out.println("</html>");
    */
    out.close();
 }

/*
 * The output of this method should match what's in /v5/proshop_welcomems.htm (the html frameset file we goto after login)
 */
 private void outputFrameset(String title, String botFrameSRC, PrintWriter out) {


    out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\" \"http://www.w3.org/TR/html4/frameset.dtd\">");
    out.println("<html>");
    out.println("<head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=9\">");
    out.println("<title>" + title + "</title>");
    out.println("</head>");
    
    out.println("<frameset rows=\"82,*\" frameborder=\"7\" framespacing=\"0\" border=\"0\">");
    out.println("<frame name=\"top\" src=\"Proshop_maintop\" marginheight=\"1\" scrolling=\"no\">");
    out.println("<frame name=\"bot\" src=\"" + botFrameSRC + "\" marginheight=\"1\">");
    out.println("</frameset>");

    out.println("</html>");
 }

} // end Proshop_jump
