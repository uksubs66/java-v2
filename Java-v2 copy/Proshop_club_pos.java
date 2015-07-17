/***************************************************************************************     
 *   Proshop_club_pos:  This servlet will provide configuration options
 *                      for clubs POS systems
 *
 *
 *   called by:  Proshop_club (doGet)
 *
 *   created: 03/02/2011
 *
 *
 *   last updated:
 *
 *          12/30/11  Updated the IBS_Config method better indicate communication errors
 *          07/08/11  Updated the IBS_Config method to handle null values better
 *
 *
 *   notes:  if ISB doesn't want us to expose the url/user/pass to the end user then
 *           we could wrap that part of the form around an if user == proshop4tea
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.net.URL;
import javax.xml.ws.Holder;
import javax.xml.namespace.QName;

import org.ibsservices.*; // IBS

public class Proshop_club_pos extends HttpServlet {
                         
                                 
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

 
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
    resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
    resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
    resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server
    resp.setContentType("text/html");
    
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);
    if (session == null) return;
    Connection con = SystemUtils.getCon(session);
    
    if (con == null) {
        
        SystemUtils.buildDatabaseErrMsg("Unable to connect to the database.", "", out, true);
        return;
    }

    String club = (String)session.getAttribute("club");

    String type = (req.getParameter("type") == null) ? "" : req.getParameter("type");

    // add more matching POS types (from caller proshop_club) here to call
    // their specific interface configuration
    if (type.equalsIgnoreCase("IBS")) {

        if (req.getParameter("getDeptID") != null || req.getParameter("getInvMenuID") != null ||
            req.getParameter("getTenderMethods") != null || req.getParameter("getTaxCodes") != null ||
            req.getParameter("getChargeCodes") != null) {

            IBS_QueryData(req, out, con);

        } else {

            IBS_Config(req, out, con);
        
        }

    } else {

        out.println("<p>NOTHING TO DO!</p>");

    }

 }  // end of doGet


 private void IBS_QueryData(HttpServletRequest req, PrintWriter out, Connection con) {


    Statement stmt = null;
    ResultSet rs = null;

    String ws_url = "", ws_user = "", ws_pass = "", ibs_uidDeptID = ""; //, ibs_uidInvMenuID = "";

    // LOAD UP ALL IBS CONFIG DATA FROM club5
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT pos_ws_url, pos_ws_user, pos_ws_pass, ibs_uidDeptID FROM club5"); // , ibs_uidInvMenuID

        if (rs.next()) {

            ws_url = rs.getString("pos_ws_url");
            ws_user = rs.getString("pos_ws_user");
            ws_pass = rs.getString("pos_ws_pass");
            ibs_uidDeptID = rs.getString("ibs_uidDeptID");
            //ibs_uidInvMenuID = rs.getString("ibs_uidInvMenuID");

        }

    } catch (Exception exc) {

        out.println("<p>Fatal error loading up IBS specific values from club5!</p>Error = " + exc.toString());
        return;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

    }

    javax.xml.ws.Holder<String> holderMessage = new javax.xml.ws.Holder<String>();
    javax.xml.ws.Holder<String> holderResponse = new javax.xml.ws.Holder<String>();

    try {

        URL url = new URL(ws_url);

        IBSWebMemberAPI service = null;

        service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

        IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

        // perform the requested query
        if (req.getParameter("getDeptID") != null) {

            out.println("<h3 align=center>ALL SYSTEM CODES BY RETAIL DEPARTMENTS</p>");

            port.getSystemCodeAllRetailDepartmentsString(ws_user, ws_pass, "CSV", holderMessage, holderResponse);

        } else if (req.getParameter("getInvMenuID") != null) {

            out.println("<h3 align=center>ALL INFENTORY MENU CODES FOR DEFINED DEPARTMENT</p>");

            port.getInvMenusForDepartmentString(ws_user, ws_pass, ibs_uidDeptID, "CSV", holderMessage, holderResponse);

        } else if (req.getParameter("getTenderMethods") != null) {

            out.println("<h3 align=center>ALL TENDER CODES FOR DEFINED DEPARTMENT</p>");

            port.getTenderAllTenderMethodsForDepartmentString(ws_user, ws_pass, ibs_uidDeptID, "CSV", holderMessage, holderResponse);

        } else if (req.getParameter("getTaxCodes") != null) {

            out.println("<h3 align=center>ALL TAX CODES FOR DEFINED DEPARTMENT</p>");

            port.getSystemCodeAllTaxCodesForDepartmentString(ws_user, ws_pass, ibs_uidDeptID, "CSV", holderMessage, holderResponse);

        } else if (req.getParameter("getChargeCodes") != null) {

            out.println("<h3 align=center>ALL CHARGE CODES FOR DEFINED DEPARTMENT</p>");

            //port.getAllItemsForDepartmentString(ws_user, ws_pass, ibs_uidDeptID, "CSV", holderMessage, holderResponse);
            port.getAllRetailItemsString(ws_user, ws_pass, "CSV", holderMessage, holderResponse);
            //port.getInvItemsForInvMenuString(ws_user, ws_pass, ibs_uidInvMenuID, "CSV", holderMessage, holderResponse);


        }

    } catch (Exception exc) {

        out.println("<p>Fatal error connection to the IBS interface!</p>Error = " + exc.toString());

    }

    outputCSVasTable(holderResponse.value, out);

 }


 private void IBS_Config(HttpServletRequest req, PrintWriter out, Connection con) {

    Statement stmt = null;
    PreparedStatement pstmt = null;
    ResultSet rs = null;

    String ws_url = "", ws_user = "", ws_pass = "", deptID = "", tenderID = "", invMenuID = "", taxID = "";

    int count = 0;

    // FIRST SEE IF WE NEED TO UPDATE ANYTHING
    if (req.getParameter("update") != null) {

        try {
            ws_url = req.getParameter("pos_ws_url").trim();
        } catch (NullPointerException exc) {}

        try {
            ws_user = req.getParameter("pos_ws_user").trim();
        } catch (NullPointerException exc) {}

        try {
            ws_pass = req.getParameter("pos_ws_pass").trim();
        } catch (NullPointerException exc) {}

        try {
            deptID = req.getParameter("deptID").toUpperCase().trim();
        } catch (NullPointerException exc) {}

        try {
            tenderID = req.getParameter("tenderID").toUpperCase().trim();
        } catch (NullPointerException exc) {}

        invMenuID = ""; //req.getParameter("invMenuID").toUpperCase().trim();

        try {
            taxID = req.getParameter("taxID").toUpperCase().trim();
        } catch (NullPointerException exc) {}

        try {

            pstmt = con.prepareStatement(
                "UPDATE club5 " +
                "SET " +
                    "pos_ws_url = ?, pos_ws_user = ?, pos_ws_pass = ?, " +
                    "ibs_uidDeptID = ?, ibs_uidTenderID = ?, ibs_uidInvMenuID = ?, ibs_uidTaxID = ?");

            pstmt.clearParameters();
            pstmt.setString(1, ws_url);
            pstmt.setString(2, ws_user);
            pstmt.setString(3, ws_pass);
            pstmt.setString(4, deptID);
            pstmt.setString(5, tenderID);
            pstmt.setString(6, invMenuID);
            pstmt.setString(7, taxID);

            count = pstmt.executeUpdate();

            if (count == 0) {

                out.println("<p>Update Failed!</p>");

            } else {

                out.println("<p>Update Succeeded!</p>");

            }


        } catch (Exception exc) {

            out.println("<p>Fatal error updating IBS specific values in club5!</p>Error = " + exc.toString());

        } finally {

            try { pstmt.close(); }
            catch (SQLException ignored) {}

        }

    } // end conditional update of config data


    // LOAD UP ALL IBS CONFIG DATA FROM club5
    try {

        stmt = con.createStatement();
        rs = stmt.executeQuery("SELECT pos_ws_url, pos_ws_user, pos_ws_pass, ibs_uidDeptID, ibs_uidTenderID, ibs_uidInvMenuID, ibs_uidTaxID FROM club5");

        if (rs.next()) {

            ws_url = rs.getString("pos_ws_url");
            ws_user = rs.getString("pos_ws_user");
            ws_pass = rs.getString("pos_ws_pass");
            deptID = rs.getString("ibs_uidDeptID");
            tenderID = rs.getString("ibs_uidTenderID");
            invMenuID = rs.getString("ibs_uidInvMenuID");
            taxID = rs.getString("ibs_uidTaxID");
            
        }

    } catch (Exception exc) {

        out.println("<p>Fatal error loading up IBS specific values from club5!</p>Error = " + exc.toString());
        return;

    } finally {

        try { rs.close(); }
        catch (SQLException ignored) {}

        try { stmt.close(); }
        catch (SQLException ignored) {}

    }

    boolean missing_basics = (ws_url.equals("") || ws_user.equals("") || ws_pass.equals(""));
    boolean comm_ok = false;

/*
    if (deptID.equals("") || tenderID.equals("") || invMenuID.equals("") || taxID.equals("")) {

        out.println("<p>WARNING!  Before this interface can be used the rest of the configuration must be set.</p>");

    }
*/

    // START PAGE OUTPUT
    out.println("<html><head>");
    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("</head>");
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\">");

    out.println("<h2 align=center>IBS Interface Configuration</h2>");

    out.println("<table width=560 align=center bgcolor=\"#336633\">");
    out.println("<tr><td><font color=white size=3>");
    out.println("<b>Instructions:</b>&nbsp; Fill out the form below in its entirety.  All information is required.");
    out.println("</font></td></tr>");
    out.println("</table><br><br>");

    out.println("<table border=0 align=center cellspacing=3><tr valign=top><td bgcolor=#F5F5DC>");

    out.println("<form method=get>");
    out.println("<input type=hidden name=type value=\"IBS\">");

    if (missing_basics) {

        out.println("<p>&nbsp; &nbsp;First you must specify the URL and the credentials to use.</p>");

    } else {

        // test basic communication and report results

        boolean con_fail = false;

        try {

            URL url = new URL(ws_url);

            IBSWebMemberAPI service = null;

            service = new IBSWebMemberAPI(url, new QName("http://ibsservices.org/", "IBSWebMemberAPI"));

            IBSWebMemberAPISoap port = service.getIBSWebMemberAPISoap();

            comm_ok = port.areYouThere(ws_user, ws_pass);

        } catch (Exception exc) {

            con_fail = true;
            out.println("<p align=center style=\"color:red;font-weight:bold\">Fatal error connection to the IBS interface! Check the URL is accessible.</p>" +
                    "<p style=\"padding:10px\">Error: " + exc.toString() + "</p>");

        }

        if (comm_ok) {

            out.println("<p align=center><b>Interface communication successful!</b></p>");

        } else if (!con_fail) {

            // since we didn't throw an error connecting it's likely the credentials are wrong
            out.println("<p align=center style=\"color:orange;font-weight:bold\">Seemed to connect but could not communicate. Check the user credentials.</p>");

        }

    }

    out.println("<table>");

    out.println("<tr><td>Web Service URL:</td><td><input type=text size=75 maxlength=128 name=\"pos_ws_url\" value=\"" + ws_url + "\"></td></tr>");
    out.println("<tr><td>Web Service USER:</td><td><input type=text size=32 maxlength=32 name=\"pos_ws_user\" value=\"" + ws_user + "\"></td></tr>");
    out.println("<tr><td>Web Service PASS:</td><td><input type=text size=32 maxlength=32 name=\"pos_ws_pass\" value=\"" + ws_pass + "\"></td></tr>");

    out.println("<tr><td colspan=2>&nbsp;</td></tr>");

    if (!missing_basics) {

        out.println("<tr><td>" +
                "<a href=\"Proshop_club_pos?type=IBS&getDeptID\" target=_ibsLookup style=\"color:black\">" +
                "Department ID" +
                "</a>:</td><td><input type=text size=50 maxlength=50 name=\"deptID\" value=\"" + deptID + "\"></td></tr>");
/*
        out.println("<tr><td>" +
                ((deptID.equals("")) ? "" : "<a href=\"Proshop_club_pos?type=IBS&getInvMenuID\" target=_ibsLookup style=\"color:black\">") +
                "Inventory Menu ID" +
                ((deptID.equals("")) ? "" : "</a>") +
                ":</td><td><input type=text size=50 maxlength=50 name=\"invMenuID\" value=\"" + invMenuID + "\"></td></tr>");
*/
        out.println("<tr><td>" +
                ((deptID.equals("")) ? "" : "<a href=\"Proshop_club_pos?type=IBS&getTenderMethods\" target=_ibsLookup style=\"color:black\">") +
                "Tender ID" +
                ((deptID.equals("")) ? "" : "</a>") +
                ":</td><td><input type=text size=50 maxlength=50 name=\"tenderID\" value=\"" + tenderID + "\"></td></tr>");

        out.println("<tr><td>" +
                ((deptID.equals("")) ? "" : "<a href=\"Proshop_club_pos?type=IBS&getTaxCodes\" target=_ibsLookup style=\"color:black\">") +
                "Tax ID" +
                ((deptID.equals("")) ? "" : "</a>") +
                ":</td><td><input type=text size=50 maxlength=50 name=\"taxID\" value=\"" + taxID + "\"></td></tr>");

    }

    out.println("<tr><td colspan=2>&nbsp;</td></tr>");

        out.println("<tr><td>" +
                ((deptID.equals("")) ? "" : "<a href=\"Proshop_club_pos?type=IBS&getChargeCodes\" target=_ibsLookup style=\"color:black\">") +
                "Get Charge Codes" +
                ((deptID.equals("")) ? "" : "</a>") +
                "</td><td>&nbsp;</tr>");

    //out.println("<tr><td colspan=2><button onclick=\"\">Get All Charge Codes</button></td></tr>");

    out.println("<tr><td colspan=2>&nbsp;</td></tr>");
    out.println("<tr><td colspan=2 align=center><input type=submit name=update value=\"  Update  \"></td></tr>");

    out.println("</table>");

    out.println("</form>");

    out.println("</td></tr></table>");

    out.println("<p align=center><button onclick=\"window.close()\">Close</button></p>");

    out.println("</body></html>");

 }


 private void outputCSVasTable(String csv, PrintWriter out) {


    String [] line = csv.split("\\n");

    int index = 0;
    String LINE_SEPARATOR = System.getProperty("line.separator");
    String delimiter = ",";
    String textQualifier = "\"";
    String replaceNewline = "%BREAK%";
    boolean inTextQualifiedField = false;

    boolean error = false;

    String newCSV = "";

    for (int i=1; i < line.length; i++) { // start with 1 so we skip the header row

       boolean start = true;

       while( index >= 0 ) {

           if( inTextQualifiedField ) {

               index = line[i].indexOf(textQualifier, index);

               if( index > -1 ) {
                   index += textQualifier.length();
                   inTextQualifiedField = false;
               } else {
                   //Hit the end of line ( ie. newline in text qualified field )
                   // Add replaceNewline and continue;
                   newCSV += line[i];
                   newCSV += replaceNewline;
                   index = -2;
               }

           } else {

               if( start ) {
                   //Check beginning of line for text qualifier
                   if( line[i].indexOf(textQualifier, index) == 0 ) {
                       inTextQualifiedField = true;
                       index += textQualifier.length();
                   }
                   start = false;
                   continue;
               }

               index = line[i].indexOf(delimiter, index);

               if( index > -1 ) {
                   //found delimiter check next
                   index += delimiter.length();

                   if( line[i].indexOf(textQualifier, index) == index ) {
                       //Next character is textQualifier
                       inTextQualifiedField = true;
                       index += textQualifier.length();
                   }

               }
           }
       }

       if( !inTextQualifiedField ) {
           newCSV += line[i];
           newCSV += LINE_SEPARATOR;
       }

       index = 0;

    }


    // re-assign to new csv string
    line = newCSV.split("\\n");


    // table output
    out.println("<table align=center border=1>");
    for (int i=0; i < line.length-1; i++) {

        newCSV = line[i].replace(",", "<td nowrap>");
        newCSV = newCSV.replace("\\n", "<tr><td nowrap>");
        newCSV = newCSV.replace("\\r,", "<tr><td nowrap>");

        out.println("<tr><td nowrap>" + newCSV + "</td></tr>");

    }
    out.println("</table>");
 }

}
