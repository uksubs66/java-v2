
/***************************************************************************************
 *   Member_statements:  This servlet will display the member statements and bill pay pages.
 *
 *
 *
 *   called by:  Common_skin - main tab (Directory)
 *
 *   created: 10/23/2012   Bob P.
 *
 *   last updated:
 *
 *    1/17/13   Add the Request object to outputBanner, outputSubNav, and outputPageEnd so we can get the session object to test caller.
 *
 *
 ***************************************************************************************
 */
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.security.*;
import java.security.spec.*;
import java.net.*;
import java.net.URLEncoder;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

// foretees imports
import com.foretees.common.Labels;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.jonasAPI;

public class Member_statements extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)

    static final int ft_connect_statements_id = ProcessConstants.CONNECT_STATEMENTS_ID;     // FT Connect Directory Tab

    
    //******************************************************
    //  doGet
    //******************************************************
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {


        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();

        Gson gson_obj = new Gson(); // Create Json response for later use
        Map<String, Object> event_map = new LinkedHashMap<String, Object>(); // Create hashmap response for later use

        HttpSession session = SystemUtils.verifyMem(req, out);       // check for intruder

        if (session == null) {
            return;
        }

        Connection con = Connect.getCon(req);            // get DB connection

        if (con == null) {

            out.println(SystemUtils.HeadTitle("DB Connection Error"));
            out.println("<BODY bgcolor=\"#ccccaa\"><CENTER>");
            out.println("<BR><BR><H3>Database Connection Error</H3>");
            out.println("<BR><BR>Unable to connect to the Database.");
            out.println("<BR>Please try again later.");
            out.println("<BR><BR>If problem persists, contact your club manager.");
            out.println("<BR><BR>");
            out.println("<a href=\"Member_announce\">Return</a>");
            out.println("</CENTER></BODY></HTML>");
            out.close();
            return;
        }

        String club = (String) session.getAttribute("club");   // get club name
        String caller = (String) session.getAttribute("caller");
        String user = (String) session.getAttribute("user");
        String mship = (String) session.getAttribute("mship");             // get member's mship type
        String clubName = Utilities.getClubName(con, true);        // get the full name of this club

        int sess_activity_id = (Integer) session.getAttribute("activity_id");
        
        int activity_id = ft_connect_statements_id;  // 8002 = FT Connect Statements
        
        
        String reqType = "";
        
        if (req.getParameter("type") != null) {         // if request type specified

            reqType = req.getParameter("type");         // statements or billpay
        }


        String iframeURL = "about:blank";
        
        if (req.getParameter("viewStatement") != null) {
            
            
/*            
            // Assign Time – Number of milliseconds since Jan 1st, 1970
            String timeStamp = Long.toString(System.currentTimeMillis());
            // Assign Vendor code – a unique code provided by CHO E3 Support
            String vendorcode = "1669042831";
            // Assign member number used to define a user - this should be pulled from your local data 
            String userid = "0061"; 
            // Assign Page – (optional) the URL to redirect after successful log in
            String pageurl = "/Statements.aspx";
            // Load your private key - do NOT store the private key file in a web-accessible place
            InputStream inStream = new FileInputStream("/root/myrsakey.pkcs8"); 
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();

            int count;
            byte[] inputbuf = new byte[1024]; 
            while (true)
            {
             count = inStream.read(inputbuf); 
             if (count <= 0)
              {
                break;
              }
              outStream.write(inputbuf, 0, count); 
            }
            byte[] encodedKey = outStream.toByteArray(); 
            inStream.close();

            String value = "";
            
            try {

                KeySpec ks = new PKCS8EncodedKeySpec(encodedKey);
                PrivateKey privateKey = KeyFactory.getInstance("RSA").generatePrivate(ks);
                StringBuilder dataForSignature = new StringBuilder(); 
                dataForSignature.append(timeStamp); 
                dataForSignature.append("|"); 
                dataForSignature.append(vendorcode); 
                dataForSignature.append("|"); 
                dataForSignature.append(userid); 
                dataForSignature.append("|"); 
                dataForSignature.append(pageurl);

                // or SHA1withRSA if you generated an RSA key
                Signature signWithRSA = Signature.getInstance("SHA1withRSA"); 
                signWithRSA.initSign(privateKey); 
                signWithRSA.update(dataForSignature.toString().getBytes("UTF-16LE"));

                byte[] signedData = signWithRSA.sign();
                value = new sun.misc.BASE64Encoder().encode(signedData);

            } catch (Exception exc) {
                out.println("Error: " + exc.getMessage());
            }
            
            StringBuilder redirectString = new StringBuilder();
            // change the URL www.clubname.com to the appropriate URL for the club’s website.
            // http://spurwing.clubhouseonline-e3.com/Statements.aspx
            redirectString.append("http://spurwing.clubhouseonline-e3.com/passthrough.aspx?time="); 
            redirectString.append(timeStamp);
            redirectString.append("&vendor=");
            redirectString.append(vendorcode);
            redirectString.append("&userid="); 
            redirectString.append(userid); 
            redirectString.append("&page="); 
            redirectString.append(pageurl); 
            redirectString.append("&value="); 
            redirectString.append(URLEncoder.encode(value,"UTF-8"));

            iframeURL = redirectString.toString();
            
            resp.sendRedirect(redirectString.toString()); 

            //out.println(redirectString.toString());
*/
            
            
            String tmpUser = jonasAPI.scrubUser(club, user);
            if (club.equals("democonnect")) tmpUser = "0061";
            if (club.equals("spurwingcc") && user.equals("test")) tmpUser = "0061";
            
            String redirectURL = jonasAPI.getRequestURL(club, tmpUser, "http://spurwing.clubhouseonline-e3.com/passthrough.aspx", "/Statements.aspx");
            
            resp.sendRedirect(redirectURL); 
            
            out.close();
            return;
        }
        
        //
        //   build the HTML page for the display
        //
        Common_skin.outputHeader(club, activity_id, "Member Statements", true, out, req);
        Common_skin.outputBody(club, activity_id, out, req);
        Common_skin.outputTopNav(req, club, activity_id, out, con);
        Common_skin.outputBanner(club, activity_id, clubName, "", out, req); 
        Common_skin.outputSubNav(club, activity_id, out, con, req);
        Common_skin.outputPageStart(club, activity_id, out, req);
        Common_skin.outputBreadCrumb(club, activity_id, out, "Statements", req);
        Common_skin.outputLogo(club, activity_id, out, req);
        
        //  Process according to the request type
        
        if (reqType.equalsIgnoreCase("statements")) {     // if Statements requested

            if (club.equals("democonnect") || club.equals("spurwingcc")) {
/*
                out.println("<div class=\"main_instructions\">"
                        + "<p>"
                        + "Member Statements"
                        + "</p>"
                        + "</div>");
*/              
                
                out.println("<center><a href=\"Member_statements?viewStatement\" target=\"jonasPopup\" class=\"standard_button\" style=\"width:350px\">View Current Statements</a></center>");
                
                
                
/*              
                out.println("<p>"+ user + "=" + jonasAPI.scrubUser(club, user) + "</p>");
                out.println("<p>78A=" + jonasAPI.scrubUser(club, "78A") + "</p>");
                out.println("<p>1=" + jonasAPI.scrubUser(club, "1") + "</p>");
                
         
                out.println("<script type=\"text/javascript\">");
                    out.println("$(document).ready(function() {");
                    out.println(" $(\"#jonasLink\").click();");
                    out.println(" $(\"#cnnLink\").click();");
                    out.println("});");
                out.println("</script>");
                    
                out.println("<a id=\"jonasLink\" href=\"" + StringEscapeUtils.escapeHtml(iframeURL)+ "\" target=\"remoteContent\">Open Member Statements</a>");
        
                out.println("<script type=\"text/javascript\">");
                
                out.println("var windowObjectReference = null;");
                //out.println("var popupFeatures = \"height=480,width=640,menubar=no,location=no,resizable=yes,scrollbars=no,status=no\";");

                out.println("function openRequestedPopup(strUrl, strWindowName) {");
                out.println("  if(windowObjectReference == null || windowObjectReference.closed) {");
                out.println("    windowObjectReference = window.open(strUrl, strWindowName, 'resizable,scrollbars,status');");
                out.println("  } else {");
                out.println("    windowObjectReference.focus();");
                out.println("  };");
                out.println("}");
                
                //out.println("function openPopup() {");
                //out.println(" choWinRef = window.open(\"" + iframeURL+ "\", \"choPopupName\", popupFeatures);");
                //out.println("}");
                
                //out.println("openRequestedPopup(\"" + iframeURL+ "\", \"jonasPopup\");");
                
                out.println("</script>");
                
                out.println("<a href=\"javascript:openRequestedPopup('" + StringEscapeUtils.escapeHtml(iframeURL) + "', 'jonasPopup');\">Open Member Statements</a>");
                
                out.println("<a id=\"cnnLink\" href=\"javascript:openRequestedPopup('http://www.cnn.com/', 'jonasPopup');\">Open CNN</a>");
/*
                out.println("<p align=center><BR><BR><img src=\"/" + rev + "/images/Statements-Main.png\" width=600 height=354 border=0>");
                out.println("<BR><BR><img src=\"/" + rev + "/images/Statement-Month.png\" width=600 height=531 border=0>");
                out.println("<BR><BR><img src=\"/" + rev + "/images/Statement-Month-Details.png\" width=600 height=479 border=0></p>");
*/              
            } else {
                out.println("<div class=\"main_instructions\" style=\"text-align:center\"><p>Member Statements - Coming Soon!</p></div>");
            }
            
        } else {

            if (club.equals("democonnect")) {

                out.println("<div class=\"main_instructions\">"
                        + "<p>"
                        + "Make Payments"
                        + "</p>"
                        + "</div>");

                out.println("<a href=\"Member_statements?viewStaffList\" target=\"jonasPopup\" class=\"\">View Staff List</a>");
                out.println("<p></p>");
                out.println("<a href=\"Member_statements?viewRoster\" target=\"jonasPopup\" class=\"\">View Member Directory</a>");
                //out.println("<p align=center><BR><BR><img src=\"/" + rev + "/images/Statement-Pay-Balance.png\" width=600 height=676 border=0></p>");
                
            } else {
                out.println("<div class=\"main_instructions\" style=\"text-align:center\"><p>Make Payments - Coming Soon!</p></div>");
            }
        }


        Common_skin.outputPageEnd(club, activity_id, out, req);

        out.close();

    }   // end of doGet

   
    //******************************************************************************
    //  doPost 
    //******************************************************************************
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        doGet(req, resp);          // call doGet processing

    }   // end of doPost

}
