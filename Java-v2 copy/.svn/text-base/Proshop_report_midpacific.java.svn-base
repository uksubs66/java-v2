/***************************************************************************************
 *   Proshop_report_midpacific:   This servlet will implement the custom Mid Pacific quota report
 *
 *
 *   Called by:     Menu tabs and self
 *
 *
 *   Created:       09/10/09
 *
 *
 *   Revisions:
 *
 *   12/07/12   Fixed issue with midPacParms.mship not getting populated, which caused past round totals to not report properly.
 *    6/23/11   Fixed incorrect values being used as rounds played limits for Class J.
 *    6/23/11   Fixed form name that was causing the dynamic search box to not be functional.
 *   11/19/09   alphaTable.nameList_simple call updated to match changes to that method
 *
 *
 *                  
 *                  
 ***************************************************************************************
 */


import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

// foretees imports
import com.foretees.common.alphaTable;
import com.foretees.common.MidPacificCustom;
import com.foretees.common.parmMidPacific;

public class Proshop_report_midpacific extends HttpServlet {

    private static final String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)


    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doPost(req, resp);

    }

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

        PrintWriter out = resp.getWriter();

        String excel = (req.getParameter("excel") != null) ? req.getParameter("excel")  : "";

        // set response content type
        try{
            if (excel.equals("yes")) {                // if user requested Excel Spreadsheet Format
                resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
            } else {
                resp.setContentType("text/html");
            }
        }
        catch (Exception exc) {
        }

        HttpSession session = SystemUtils.verifyPro(req, out);
        if (session == null) return;
        Connection con = SystemUtils.getCon(session);

        if (con == null) {

            SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
            return;
        }

        String templott = (String)session.getAttribute("lottery");
        int lottery = Integer.parseInt(templott);

        // Check Feature Access Rights for current proshop user
        if (!SystemUtils.verifyProAccess(req, "REPORTS", con, out)) {
            SystemUtils.restrictProshop("REPORTS", out);
            return;
        }

        String club = (String)session.getAttribute("club");
        
        String name = "";

        // START DEFAULT PAGE OUTPUT
        out.println("<html><head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
        out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
        out.println("<script language=\"javascript\" src=\"/" +rev+ "/web utilities/foretees.js\"></script>");
        out.println("</head>");
        out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

        if (!excel.equals("yes")) SystemUtils.getProshopSubMenu(req, out, lottery);

        if (req.getParameter("name") != null && !req.getParameter("name").equals("")) {        // if user specified a name to search for

            viewRounds(req, con, out);
            return;
        }

        out.println("<script type=\"text/javascript\">");
        out.println("<!--");
        out.println("function cursor() { document.forms['f'].name.focus(); }");
        out.println("function movename(name) {");
        out.println(" document.forms['f'].name.value = name;");
        out.println("}");
        out.println("function movename(nameinfo) {");
        out.println(" array = nameinfo.split(':');"); // split string (name, mship, username)
        out.println(" var name = array[0];");
        out.println(" var mship = array[1];");
        out.println(" var username = array[2];");
        out.println(" f = document.forms['playerform'];");
        out.println(" f.name.value = name;");
        out.println(" f.mship.value = mship;");
        out.println(" f.username.value = username;");
        out.println("}");
        out.println("// -->");
        out.println("</script>");

        out.println("<font face=\"Arial, Helvetica, Sans-serif\">");

        out.println("<p align=center><font size=5>Round Quota Report</font></p>");

        out.println("<table border=\"1\" bgcolor=\"#336633\" cellpadding=\"5\" align=\"center\">");
        out.println("<tr><td>");
        out.println("<font color=\"#FFFFFF\" face=\"Arial, Helvetica, Sans-serif\">");
        out.println("<p>Select a member from the name list to view their rounds played");
        out.println("</p></font>");
        out.println("</td></tr></table>");

        out.println("<form action=\"Proshop_report_midpacific\" method=\"post\" name=\"playerform\">");

        out.println("<br><table border=\"0\" align=\"center\" width=\"600\">");
        out.println("<tr><td align=\"center\">");
        out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" align=\"center\" width=\"600\">");
        out.println("<tr>");
        out.println("<td valign=\"top\" align=\"center\">");

        out.println("<table border=\"0\" bgcolor=\"#F5F5DC\" valign=\"center\" align=\"center\" width=\"450\" height=\"430\">");
        out.println("<tr><td align=\"center\">Please select a name from the list to the right.");
        out.println("<br><br>Name: ");
        out.println("<input type=\"text\" name=\"name\" size=\"20\" maxlength=\"40\" onchange=\"document.forms['playerform'].name.value=this.value\" " +
                "onfocus=\"this.blur()\" ondblclick=\"alert('Choose a member using the lists on the right.');\">");
        out.println("<br><br><input type=\"checkbox\" name=\"pastOnly\"><font size=\"2\"> - View only past rounds (excludes current bookings)</font>");
        out.println("<input type=\"hidden\" name=\"mship\" value=\"\">");
        out.println("<input type=\"hidden\" name=\"username\" value=\"\">");
        out.println("<br><br><input type=\"submit\" value=\"View Rounds\" name=\"view\">");
        out.println("</td></tr></table>");
        out.println("<td>");

        alphaTable.nameList_simple(club, true, out, con);

        out.println("</td>");
        out.println("</tr>");
        out.println("</table>");
        out.println("</td></tr>");
        out.println("<tr><td align=\"center\" colspan=\"2\"><br><button onClick=\"self.close()\">Close</button></td></tr>");
        out.println("</table></body></html>");
    }

    private static void viewRounds(HttpServletRequest req, Connection con, PrintWriter out) {
        
        
        // Declare variables
        String name = "";           // full name of selected member
        String username = "";       // username of selected member
        String mship = "";          // mship of selected member
        String memberClass = "";    // member's member class
        String errMsg = "";         // error message

        boolean pastOnly = false;   // whether or not only past played rounds should be counted

        parmMidPacific midPacParms = new parmMidPacific();

        // Get parameters from request
        name = req.getParameter("name");
        
        if (req.getParameter("pastOnly") != null) pastOnly = true;
        
        if (req.getParameter("username") != null) {
            username = req.getParameter("username");
        } else {
            errMsg = "Username not found for this member!";
        }

        if (req.getParameter("mship") != null) {
            mship = req.getParameter("mship");
        } else {
            errMsg = "Membership Type not found for this member!";
        }

        out.println("<table align=\"center\" border=\"0\" cellpadding=\"5\">");
        out.println("<tr><td align=\"center\"><h2>Rounds for: " + name + " - " + mship + "</h2></td></tr>");
        out.println("<tr><td align=\"center\">");
        
        midPacParms.mship = mship;       

        if (errMsg.equals("")) {

            midPacParms.memberClass = MidPacificCustom.checkMidPacificMemberClass(username, mship, con);
            midPacParms.user = username;
            midPacParms.teecurr_id = -1;        // set to -1 so later methods know we're not coming from a tee time 
            
            if (midPacParms.memberClass.equals("A")) { 

                int non_total = 0;

                int [] non_month = new int[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    non_month[i] = 0;
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;

                    MidPacificCustom.checkMidPacificClassA(midPacParms, con, pastOnly);

                    // store results
                    non_month[i] = midPacParms.non_month;
                    non_total += midPacParms.non_month;
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");
                // Print row of round totals

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total (no limit)</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + non_month[i] + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + non_total + "</b></td>");
                out.println("</tr>");

                out.println("</table>");

            } else if (midPacParms.memberClass.equals("B")) {

                int non_total = 0;
                int res_total = 0;
                int propGuest_total = 0;

                int [] non_month = new int[12];
                int [] res_month = new int[12];
                int [] propGuest_month = new int[12];
                String [] color_res_month = new String[12];
                String [] color_propGuest_month = new String[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    non_month[i] = 0;
                    res_month[i] = 0;
                    propGuest_month[i] = 0;
                    color_res_month[i] = "";
                    color_propGuest_month[i] = "";
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;
                    midPacParms.res_month = 0;
                    midPacParms.propGuestRounds = 0;

                    MidPacificCustom.checkMidPacificUnrestrictedRounds(midPacParms, con, pastOnly);

                    MidPacificCustom.checkMidPacificClassB(midPacParms, con, pastOnly);

                    // store results
                    non_month[i] = midPacParms.non_month;
                    non_total += midPacParms.non_month;
                    res_month[i] = midPacParms.res_month;
                    res_total += midPacParms.res_month;
                    propGuest_month[i] = midPacParms.propGuestRounds;
                    propGuest_total += midPacParms.propGuestRounds;
                    color_res_month[i] = (res_month[i] >= MidPacificCustom.restricted_month_classB) ? "red" : "green";
                    color_propGuest_month[i] = (propGuest_month[i] >= MidPacificCustom.propGuest_classB) ? "red" : "green";
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");

                // Print row of unrestricted round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Unrestricted (no limit)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + non_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + non_total + "</b></td>");
                out.println("</tr>");

                // Print row of restricted round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Restricted (limit " + MidPacificCustom.restricted_month_classB + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_res_month[i] + "\">" + res_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + res_total + "</b></td>");
                out.println("</tr>");

                // Print row of prop guest round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Guest of Prop. Member (limit " + MidPacificCustom.propGuest_classB + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_propGuest_month[i] + "\">" + propGuest_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + propGuest_total + "</b></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + (res_month[i] + non_month[i] + propGuest_month[i]) + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + (non_total + res_total + propGuest_total) + "</b></td>");
                out.println("</tr>");

                out.println("</table>");
                        
            } else if (midPacParms.memberClass.equals("C")) {

                int non_total = 0;
                int res_total = 0;
                int propGuest_total = 0;

                int [] non_month = new int[12];
                int [] res_month = new int[12];
                int [] propGuest_month = new int[12];
                String [] color_res_month = new String[12];
                String [] color_propGuest_month = new String[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    non_month[i] = 0;
                    res_month[i] = 0;
                    propGuest_month[i] = 0;
                    color_res_month[i] = "";
                    color_propGuest_month[i] = "";
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;
                    midPacParms.res_month = 0;
                    midPacParms.propGuestRounds = 0;

                    MidPacificCustom.checkMidPacificUnrestrictedRounds(midPacParms, con, pastOnly);

                    MidPacificCustom.checkMidPacificClassC(midPacParms, con, pastOnly);

                    // store results
                    non_month[i] = midPacParms.non_month;
                    non_total += midPacParms.non_month;
                    res_month[i] = midPacParms.res_month;
                    res_total += midPacParms.res_month;
                    propGuest_month[i] = midPacParms.propGuestRounds;
                    propGuest_total += midPacParms.propGuestRounds;
                    color_res_month[i] = (res_month[i] >= MidPacificCustom.restricted_month_classC) ? "red" : "green";
                    color_propGuest_month[i] = (propGuest_month[i] >= MidPacificCustom.propGuest_classC) ? "red" : "green";
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");

                // Print row of unrestricted round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Unrestricted (no limit)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + non_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + non_total + "</b></td>");
                out.println("</tr>");

                // Print row of restricted round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Restricted (limit " + MidPacificCustom.restricted_month_classC + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_res_month[i] + "\">" + res_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + res_total + "</b></td>");
                out.println("</tr>");

                // Print row of prop guest round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Guest of Prop. Member (limit " + MidPacificCustom.propGuest_classC + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_propGuest_month[i] + "\">" + propGuest_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + propGuest_total + "</b></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + (res_month[i] + non_month[i] + propGuest_month[i]) + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + (non_total + res_total + propGuest_total) + "</b></td>");
                out.println("</tr>");

                out.println("</table>");

            } else if (midPacParms.memberClass.equals("D")) {

                int non_total = 0;

                int [] non_month = new int[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    non_month[i] = 0;
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;

                    MidPacificCustom.checkMidPacificClassD(midPacParms, con, pastOnly);

                    // store results
                    non_month[i] = midPacParms.non_month;
                    non_total += midPacParms.non_month;
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");
                // Print row of round totals

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total (no limit)</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + non_month[i] + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + non_total + "</b></td>");
                out.println("</tr>");

                out.println("</table>");
                
            } else if (midPacParms.memberClass.equals("H1")) {

                int non_total = 0;

                int [] non_month = new int[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    non_month[i] = 0;
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;

                    MidPacificCustom.checkMidPacificUnrestrictedRounds(midPacParms, con, pastOnly);

                    // store results
                    non_month[i] = midPacParms.non_month;
                    non_total += midPacParms.non_month;
                }

                // Print Total Rounds This Year table
                out.println("<h3>Total Rounds This Year: " +
                        "<font color=\"" + ((non_total >= MidPacificCustom.unrestricted_year_classH1) ? "red" : "green") + "\">" + non_total + "</font>" +
                        "/" + MidPacificCustom.unrestricted_year_classH1 + "</h3>");

                out.println("</td></tr><tr><td align=\"center\">");

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");
                // Print row of round totals

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Unrestricted (limit 12/yr)</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + non_month[i] + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + non_total + "</b></td>");
                out.println("</tr>");

                out.println("</table>");
                
            } else if (midPacParms.memberClass.equals("H2")) {

                int rounds9_total = 0;
                int rounds18_total = 0;
                int combined_total = 0;

                int [] rounds9_month = new int[12];
                int [] rounds18_month = new int[12];
                int [] combined_month = new int[12];
                String [] color_combined_month = new String[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    rounds9_month[i] = 0;
                    rounds18_month[i] = 0;
                    combined_month[i] = 0;
                    color_combined_month[i] = "";
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.rounds9_month = 0;
                    midPacParms.rounds18_month = 0;

                    MidPacificCustom.checkMidPacificClassH2(midPacParms, con, pastOnly);

                    // store results
                    rounds9_month[i] = midPacParms.rounds9_month;
                    rounds9_total += midPacParms.rounds9_month;
                    rounds18_month[i] = midPacParms.rounds18_month;
                    rounds18_total += midPacParms.rounds18_month;
                    combined_month[i] = (midPacParms.rounds9_month * 1) + (midPacParms.rounds18_month * 2);
                    combined_total += (midPacParms.rounds9_month * 1) + (midPacParms.rounds18_month * 2);
                    color_combined_month[i] = (combined_month[i] >= MidPacificCustom.unrestrictedTot_month_classH2) ? "red" : "green";
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");

                // Print row of unrestricted 9-hole round totals
                out.println("<tr>");
                out.println("<td align=\"right\">9-Hole Unrestricted (limit " + MidPacificCustom.unrestrictedTot_month_classH2 + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + rounds9_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + rounds9_total + "</b></td>");
                out.println("</tr>");

                // Print row of unrestricted 18-hole round totals
                out.println("<tr>");
                out.println("<td align=\"right\">18-Hole Unrestricted (limit " + (MidPacificCustom.unrestrictedTot_month_classH2 / 2) + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + rounds18_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + rounds18_total + "</b></td>");
                out.println("</tr>");

                // Print row of combined round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Combined (18-Hole = 2, 9-Hole = 1) (limit " + MidPacificCustom.unrestrictedTot_month_classH2 + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_combined_month[i] + "\"><b>" + combined_month[i] + "</b></font></td>");
                }
                out.println("<td align=\"center\"><b>" + combined_total + "</b></td>");
                out.println("</tr>");

                out.println("</table>");

            } else if (midPacParms.memberClass.equals("H3")) {
                
                boolean includeTotalRounds = true;

                int rounds_total = 0;
                int [] res_month = new int[12];
                int [] non_month = new int[12];
                String [] color_res_month = new String[12];
                String [] color_non_month = new String[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    res_month[i] = 0;
                    non_month[i] = 0;
                    color_res_month[i] = "";
                    color_non_month[i] = "";
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;
                
                // Gather monthly data
                for (int i=0; i<12; i++) {

                    if (i>0) includeTotalRounds = false;

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.playerRounds_year = 0;
                    midPacParms.res_month = 0;
                    midPacParms.non_month = 0;

                    MidPacificCustom.checkMidPacificClassH3(midPacParms, con, includeTotalRounds, pastOnly);

                    // store results
                    if (i==0) rounds_total = midPacParms.playerRounds_year;
                    res_month[i] = midPacParms.res_month;
                    non_month[i] = midPacParms.non_month;
                    color_res_month[i] = ((res_month[i] >= MidPacificCustom.restricted_month_classH3) ||
                                          ((res_month[i] + non_month[i]) >= MidPacificCustom.total_month_classH3)) ? "red" : "green";
                    color_non_month[i] = ((res_month[i] + non_month[i]) >= MidPacificCustom.total_month_classH3) ? "red" : "green";
                }

                // Print Total Rounds This Year table
                out.println("<h3>Total Rounds This Year: " +
                        "<font color=\"" + ((rounds_total >= MidPacificCustom.playerRounds_year_classH3) ? "red" : "green") + "\">" + rounds_total + "</font>" +
                        "/" + MidPacificCustom.playerRounds_year_classH3 + "</h3>");

                out.println("</td></tr><tr><td align=\"center\">");

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"13\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Tee Times Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("</tr>");

                // Print row of restricted tee time totals
                out.println("<tr>");
                out.println("<td align=\"right\">Restricted (limit " + MidPacificCustom.restricted_month_classH3 + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_res_month[i] + "\">" + res_month[i] + "</font></td>");
                }
                out.println("</tr>");

                // Print row of unrestricted tee time totals
                out.println("<tr>");
                out.println("<td align=\"right\">Unrestricted (limit " + MidPacificCustom.total_month_classH3 + " - restricted times/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_non_month[i] + "\">" + non_month[i] + "</font></td>");
                }
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total (limit " + MidPacificCustom.total_month_classH3 + "/mo)</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_non_month[i] + "\"><b>" + (res_month[i] + non_month[i]) + "</b></font></td>");
                }
                out.println("</tr>");

                out.println("</table>");

            } else if (midPacParms.memberClass.equals("J")) {

                int res_total = 0;
                int propGuest_total = 0;
                int rounds9_total = 0;
                int rounds18_total = 0;
                int combined_total = 0;

                int [] res_month = new int[12];
                int [] propGuest_month = new int[12];
                int [] rounds9_month = new int[12];
                int [] rounds18_month = new int[12];
                int [] combined_month = new int[12];
                String [] color_combined_month = new String[12];
                String [] color_res_month = new String[12];
                String [] color_propGuest_month = new String[12];

                // initialize array
                for (int i=0; i<12; i++) {
                    res_month[i] = 0;
                    propGuest_month[i] = 0;
                    rounds9_month[i] = 0;
                    rounds18_month[i] = 0;
                    combined_month[i] = 0;
                    color_combined_month[i] = "";
                    color_res_month[i] = "";
                    color_propGuest_month[i] = "";
                }

                // Get the current month
                Calendar cal = new GregorianCalendar();
                int month = cal.get(Calendar.MONTH) + 1;

                // Gather monthly data
                for (int i=0; i<12; i++) {

                    // Set the new time mode (month #)
                    midPacParms.time_mode = i + 1;

                    // Clear parm block values for each iteration
                    midPacParms.non_month = 0;
                    midPacParms.res_month = 0;
                    midPacParms.propGuestRounds = 0;
                    midPacParms.rounds9_month = 0;
                    midPacParms.rounds18_month = 0;

                    MidPacificCustom.checkMidPacificClassJ(midPacParms, con, pastOnly);

                    // store results
                    res_month[i] = midPacParms.res_month;
                    res_total += midPacParms.res_month;
                    propGuest_month[i] = midPacParms.propGuestRounds;
                    propGuest_total += midPacParms.propGuestRounds;
                    rounds9_month[i] = midPacParms.rounds9_month;
                    rounds9_total += midPacParms.rounds9_month;
                    rounds18_month[i] = midPacParms.rounds18_month;
                    rounds18_total += midPacParms.rounds18_month;
                    combined_month[i] = (midPacParms.rounds9_month * 1) + (midPacParms.rounds18_month * 2);
                    combined_total += (midPacParms.rounds9_month * 1) + (midPacParms.rounds18_month * 2);
                    color_combined_month[i] = (combined_month[i] >= MidPacificCustom.unrestrictedTot_month_classJ) ? "red" : "green";
                    color_res_month[i] = (res_month[i] >= MidPacificCustom.restricted_month_classJ) ? "red" : "green";
                    color_propGuest_month[i] = (propGuest_month[i] >= MidPacificCustom.propGuest_classJ) ? "red" : "green";
                }

                // Print Tee Times Per Month table
                out.println("<table border=\"1\" cellpadding=\"5\" bgcolor=\"#F5F5DC\">");
                out.println("<tr><td align=\"center\" colspan=\"14\" bgcolor=\"#336633\"><font color=\"white\" size=\"4\">Rounds Per Month</font></td></tr>");
                out.println("<tr>");
                out.println("<td align=\"right\">Month</td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jan</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 2) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Feb</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 3) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Mar</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 4) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Apr</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 5) ? "#CCCCAA" : "#F5F5DC") + "\"><b>May</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 6) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jun</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 7) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Jul</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 8) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Aug</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 9) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Sep</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 10) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Oct</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 11) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Nov</b></td>");
                out.println("<td align=\"center\" bgcolor=\"" + ((month == 12) ? "#CCCCAA" : "#F5F5DC") + "\"><b>Dec</b></td>");
                out.println("<td align=\"center\"><b>Total</b></td>");
                out.println("</tr>");

                // Print row of unrestricted 9-hole round totals
                out.println("<tr>");
                out.println("<td align=\"right\">9-Hole Unrestricted (limit " + MidPacificCustom.unrestrictedTot_month_classJ + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + rounds9_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + rounds9_total + "</b></td>");
                out.println("</tr>");

                // Print row of unrestricted 18-hole round totals
                out.println("<tr>");
                out.println("<td align=\"right\">18-Hole Unrestricted (limit " + (MidPacificCustom.unrestrictedTot_month_classJ / 2) + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\">" + rounds18_month[i] + "</td>");
                }
                out.println("<td align=\"center\"><b>" + rounds18_total + "</b></td>");
                out.println("</tr>");

                // Print row of combined round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Combined (18-Hole = 2, 9-Hole = 1) (limit " + MidPacificCustom.unrestrictedTot_month_classJ + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_combined_month[i] + "\"><b>" + combined_month[i] + "</b></font></td>");
                }
                out.println("<td align=\"center\"><b>" + combined_total + "</b></td>");
                out.println("</tr>");

                // Print break between two sections of report
                out.println("<tr><td colspan=\"14\"></td></tr>");

                // Print row of restricted round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Restricted (limit " + MidPacificCustom.restricted_month_classJ + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_res_month[i] + "\">" + res_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + res_total + "</b></td>");
                out.println("</tr>");

                // Print row of prop guest round totals
                out.println("<tr>");
                out.println("<td align=\"right\">Guest of Prop. Member (limit " + MidPacificCustom.propGuest_classJ + "/mo)</td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><font color=\"" + color_propGuest_month[i] + "\">" + propGuest_month[i] + "</font></td>");
                }
                out.println("<td align=\"center\"><b>" + propGuest_total + "</b></td>");
                out.println("</tr>");

                out.println("<tr>");
                out.println("<td align=\"right\"><b>Total</b></td>");
                for (int i=0; i<12; i++) {
                    out.println("<td align=\"center\" bgcolor=\"" + ((month == i + 1) ? "#CCCCAA" : "#F5F5DC") + "\"><b>" + (res_month[i] + propGuest_month[i]) + "</b></td>");
                }
                out.println("<td align=\"center\"><b>" + (res_total + propGuest_total) + "</b></td>");
                out.println("</tr>");

                out.println("</table>");
                        
            } else {
                out.println("Membership type not recognized for the selected member.");
            }

            out.println("</td></tr>");
            out.println("</table>");
            out.println("<table align=\"center\" border=\"0\" cellpadding=\"5\">");
            out.println("<tr>");
            out.println("<td align=\"right\"><button onClick=\"self.close()\">Close</button></td>");
            out.println("<td align=\"left\"><button onClick=\"location.href='Proshop_report_midpacific'\">Search Again</button></td>");
            out.println("</tr></table></body></html>");

        }

    }

}