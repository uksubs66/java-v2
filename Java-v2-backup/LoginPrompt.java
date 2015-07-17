/*
 * Login Prompt (replacement for login.jsp)
 * 
 */

import com.foretees.common.Common_skin;
import java.io.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.*;

import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Connect;
import com.foretees.common.uaUtil;

/**
 *
 * @author John Kielkopf
 */
public class LoginPrompt extends HttpServlet {

    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setHeader("Pragma", "no-cache");               // for HTTP 1.0
        resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");    // for HTTP 1.1
        resp.setDateHeader("Expires", 0);                   // prevents caching at the proxy server
        resp.setContentType("text/html");
        
        PrintWriter out = resp.getWriter();
        
        Connection con = null;
        ResultSet rs = null;
        Statement stmt = null;
        
        boolean done = false;
        String errMsg = null;
        
        String club = Utilities.getParameterString(req, "cn", "").toLowerCase();
        String domain = req.getServerName();
        String clubAlias = club;
        
        String[] domainParts = domain.split("\\.");
        
        boolean mobile_requested = domainParts[0].equalsIgnoreCase("m"); // Mobile mode from domain (m.foretees.com, m.???.foretees.com, etc.)
        
        boolean forceOldMobile = mobile_requested; // false; //ProcessConstants.SERVER_ID != 4;  // If were not on development, force old mobile mode if mobile requested.
        
        String original_club = club;
        
        Map<String, Object> redirect_details = null;
        
       // add shared sites here to redefine the club
        if (club.equals("greeleycc")) {
            club = "fortcollins";
        }
        
        String zip = "";
        String club_name = "";
        String website_url = "";
        String custom_styles = "";
        String redirect_url = "";
        String redirect_msg = "";
        int no_reservations = 0;
        int foretees_mode = 0;
        int genrez_mode = 0;
        int block_direct_member_login = 0;
        int redirect_timeout = 0;

        req.setAttribute(ProcessConstants.RQA_LOGIN, true); // Flag that we're using login scripts
        
        // get the current year
        Calendar cal = new GregorianCalendar();
        int year = cal.get(Calendar.YEAR);
        
        // Validate club
        String clubTest = Utilities.validateClub(club);
        boolean clubAliased = false;
        
        if(clubTest == null){
            done = true;
        } else if(!clubTest.equalsIgnoreCase(club)){
            clubAlias = club;
            club = clubTest;
            clubAliased = true;
        }
        
        if(!done){
            // Get club db connection
            try {
                con = dbConn.Connect(clubAlias);
            } catch (Exception exc) {
                // Could be a database connection issue, or could be an invalid club name
                done = true;
            }
        }
        
        if(done){
            
            resp.setStatus(503);
            out.print("<html><head></head><body>");
            out.print("Unable to access club: '" +club+ "'. If this problem continues, please contact support.");
            out.print("</body></html>");
            
        } else {
            
            try {

                stmt = con.createStatement();
                
                rs = stmt.executeQuery("" +
                    "SELECT clubName, zipcode, no_reservations, foretees_mode, genrez_mode, website_url, custom_styles, block_direct_member_login, redirect_url, redirect_msg, redirect_timeout " +
                    "FROM club5 "); // +
            //"WHERE clubName <> '';");

                if (rs.next()) {

                    club_name = rs.getString("clubName");
                    zip = rs.getString("zipcode");
                    no_reservations = rs.getInt("no_reservations");
                    foretees_mode = rs.getInt("foretees_mode");
                    genrez_mode = rs.getInt("genrez_mode");
                    website_url = rs.getString("website_url");
                    custom_styles = rs.getString("custom_styles");
                    block_direct_member_login = rs.getInt("block_direct_member_login");
                    redirect_url = rs.getString("redirect_url");
                    redirect_msg = rs.getString("redirect_msg");
                    redirect_timeout = rs.getInt("redirect_timeout");
                }

            } catch (Exception exc) {

            } finally {

                try { rs.close(); }
                catch (Exception ignore) {}

                try { stmt.close(); }
                catch (Exception ignore) {}

            }
            
            boolean allowRwd = Utilities.isResponsiveAllowed(con);
            boolean forceRwd = Utilities.isResponsiveForced(con);
            
            //allowRwd = (Common_Server.SERVER_ID == 60 || Common_Server.SERVER_ID == 4);
            if(Utilities.getParameterInteger(req, "allowRwd", 0) == 1) {
                allowRwd = true;
            }
            if(Utilities.getParameterInteger(req, "forceRwd", 0) == 1) {
                forceRwd = true;
            }
            
            if(uaUtil.isUnsupportedBrowser(req)){
                forceOldMobile = true; // If it's an old browser, and they requested mobile, we'll go to the old mobile interface
            }
            
            
            String unsupportedBrowserMessage = null;
            if(allowRwd || forceRwd){
                unsupportedBrowserMessage = uaUtil.getUnsupportedMessage(req);
                if(unsupportedBrowserMessage != null){
                    allowRwd = false;
                    forceRwd = false;
                }
            }

            // Check if the device is probably mobile
            if(uaUtil.isMobile(req)){
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD);
            }
            
            // Get last RWD mode user selected on this device
            Cookie rwdStateCookie = Utilities.getCookie(req,ProcessConstants.COOKIE_RWD_STATE);
            if(rwdStateCookie != null){
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD, rwdStateCookie.getValue().equals("ON"));
            }
            
            if(mobile_requested && !forceOldMobile){
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD);
            }
            
            if(!allowRwd){
                Utilities.clearBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD);
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_BLOCK_RWD_SWITCH);
            } else if (forceRwd) {
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD);
                Utilities.setBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_BLOCK_RWD_SWITCH);
            }
            
            int app_mode_current = Utilities.getRequestInteger(req, ProcessConstants.RQA_APPMODE, 0);
            int app_mode_rwd = Utilities.setBit(app_mode_current, ProcessConstants.APPMODE_RWD);
            int app_mode_desktop = Utilities.clearBit(app_mode_current, ProcessConstants.APPMODE_RWD);

            if(!allowRwd && !mobile_requested){
                // Club does not have resposnive enabled
                // Generate the old, unresponsive login prompt
                
                String label = "Tee Times";
                String brand = "ForeTees";
                
                // define our product label
                if (no_reservations == 1) {
                    label = "Notification";
                } else if (genrez_mode == 1) {
                    label = "Reservation";
                    if (foretees_mode == 0) brand = "FlxRez";
                }
                
                // create the html css link if needed
                if (!custom_styles.equals("")) {
                        custom_styles = "<link href=\"../assets/stylesheets/custom/" + custom_styles + "\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />";
                }
                
                String custom_css = "";
                File checkFile = new File("/srv/webapps/" + club + "/assets/stylesheets/club.css");
                if(checkFile != null && checkFile.isFile()){
                    custom_css += "<link rel=\"stylesheet\" href=\"/" + club + "/assets/stylesheets/club.css\" type=\"text/css\" />";
                }
                
                String baseurl = Utilities.getBaseUrl(req, 0, club, app_mode_desktop);

                out.println("<!DOCTYPE html>");
                out.println("<html lang=\"en-US\">");
                out.println("<head>");
                out.println("<meta name=\"application-name\" content=\"ForeTees\" />");
                out.println("<meta name=\"ft-server-id\" content=\""+ProcessConstants.SERVER_ID+"\" />");
                out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
                out.println("<link href=\"../assets/stylesheets/sitewide.css\" rel=\"stylesheet\" type=\"text/css\" media=\"all\" />");
                out.println(custom_styles);
                out.println(custom_css);
                out.println("<title>"+brand+" Login</title>");
                out.println("<script type=\"text/javascript\">");
                out.println("window.onload = function(){document.getElementById(\"user_name\").focus();};");
                out.println("</script>");
                out.println("</head>");
                out.println("<body>");
                out.println("<div id=\"login_body\">");
                out.println("<div id=\"wrapper_login\">");
                out.println("<div id=\"title\">"+club_name+"</div>");

                out.println("<div id=\"main_login\">");
                out.println("<div id=\"login_welcome\">");
                out.println("<h2>Welcome to the ForeTees Reservation System</h2>");
                out.println("<br />");
                out.println("Enter your username and password, then click Log In.");
                out.println("</div>");
                if(unsupportedBrowserMessage != null){
                    out.println(unsupportedBrowserMessage);
                }
                out.println("<h1>Please Log In</h1>");
                out.println("<form action=\""+baseurl+"Login\" method=\"post\" name=\"login\" id=\"login\">");
                out.println("<input type=\"hidden\" name=\"clubname\" value=\""+club+"\">");
                out.println("<input type=\"hidden\" name=\"zipcode\" value=\""+zip+"\">");
                out.println("User Name<div><input id=\"user_name\" name=\"user_name\" class=\"login\" type=\"text\" maxlength=\"15\" /></div>");
                out.println("Password<div><input name=\"password\" class=\"login\" type=\"password\"/></div><br />");
                out.println("<div id=\"login_help\">");
                out.println("<a href=\""+baseurl+"Login?help=yes&amp;clubname="+club+"\">Need Assistance?</a><br />");
                out.println("<a href=\"/"+club+"/mlogin.jsp\">Use Mobile Login</a>");
                out.println("</div>");
                out.println("<input type=\"submit\" value=\" Log In \" name=\"submit\" class=\"login_button_lg\">");
                out.println("</form>");
                out.println("</div>");

                out.println("<div id=\"footer\">");
                out.println("<div id=\"local_links\">");
                out.println("Other Links:<br />");
                out.println("<a href=\"http://"+website_url+"\" target=\"_blank\">visit your club's website</a><br />");
                out.println("<a href=\"http://www.accuweather.com/adcbin2/local_index?zipcode="+zip+"\" target=\"_blank\">view local weather</a><br />");
                out.println("<a href=\"http://www.foretees.com/\" target=\"_blank\">visit our corporate site</a>");
                out.println("</div>");
                out.println("<div id=\"footertext\"><strong>");
                out.println("FOR CLUB MEMBERS & STAFF ONLY!");
                out.println("Contact your club manager or professional staff for assistance.");
                out.println("Privacy Statement: We absolutely will not <a style=\"text-decoration:inherit; color:inherit;\" href=\"LoginPrompt?cn="+original_club+"&allowRwd=1\">divulge</a> any information from this site to a third party under any circumstances.</strong><br />");
                out.println("Copyright &copy; "+year+" ForeTees, LLC , All rights reserved.<br />");
                out.println("</div>");
                out.println("</div>");

                out.println("</div>");
                out.println("</div>");
                out.println("</body>");
                out.println("</html>");
                
                done = true;
            }
            
           if(!done && mobile_requested && (forceOldMobile || !allowRwd)){
                // We're forcing old mobile mode 
                // Redirect to old mobile login
                resp.setStatus(303);
                resp.setHeader( "Location", "/"+club+"/mlogin.jsp");
                resp.setHeader( "Connection", "close" );
                done = true;
                //Utilities.clearBitInRequest(req, ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_RWD);
           }
            
            if(!done){
                
                if (block_direct_member_login == 1 && !redirect_url.equals("") && req.getParameter("proshop") == null) {
                    
                    // This club has opted to have a redirect page displayed in place of their standard login page.  
                    // If a redirect_msg has been provided, use that, otherwise use club5.clubName and redirect_url to redirect the members. 
                    // If redirect_timeout is non-zero, also set a meta-refresh to auto-forward the members.
                    
                    if (!redirect_msg.equals("")) {
                        out.println(redirect_msg);
                    } else {
                        out.println("<html>");
                        out.println("<head>");
                           out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
                           out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
                           if (redirect_timeout > 0) {
                               out.println("<meta http-equiv=\"Refresh\" content=\"" + redirect_timeout + "; url=" + redirect_url + "\">");
                           }
                           out.println("<title>ForeTees Redirect Page</title>");
                        out.println("</head>");

                        out.println("<body style=\"bgcolor:#FFFFFF; color:#000000; font-family:Arial, Helvetica, Sans-serif;\">");
                        out.println("<div style=\"width:50%; margin-left:auto; margin-right:auto; text-align:center;\">");

                              out.println("<img src=\"/v5/images/foretees.gif\" border=0>");
                              out.println("<br><br><br>");
                              out.println("<H2>Important Notice From Your Golf Shop</H2>");
                              out.println("<span style=\"font-size:small;\">");
                                 out.println("<br><br>");
                                 out.println("Access to the ForeTees system is now only available through the " + club_name + " website.");
                                 out.println("<br><br>");
                                 out.println("The website will link you to ForeTees where you will continue to be able to make tee times, sign up for events, etc. "
                                         + "You may want to bookmark the web site's home page once you get there.");
                                 out.println("<br><br>");
                                 out.println("Please contact your Golf Shop staff if you have any questions or problems.  Thank you.");
                                 out.println("<br><br>");
                                 out.println("To get to ForeTees, login using your website credentials in the Member Login panel on the " + club_name + " website at ");
                                 out.println("<a href=\"" + redirect_url + "\" target=\"_top\">" + redirect_url + "</a> and then follow the Golf link.");
                              out.println("</span>");
                        out.println("</div></BODY></HTML>");
                    }
                    
                } else {
                    
                    // Responive login prompt
                    Common_skin.outputHeader(club, 0, "ForeTees", false, out, req, con);
                    // Call to Google analytics output here

                    out.println("<script type=\"text/javascript\">");
                    out.println("window.onload = function(){document.getElementById(\"user_name\").focus();};");
                    out.println("</script>");

                    out.print("</head>");

                    // Output login prompt page
                    out.print("<body class=\"LoginPrompt\">");
                    out.print("<div id=\"login_body\">");
                        out.print("<div id=\"wrapper_login\">");
                            out.print("<div id=\"title\">"+club_name+"</div>");
                            out.print("<div id=\"main_login\">");
                                out.print("<div id=\"content_wrapper\">");
                                    out.print("<div id=\"login_welcome\">");
                                        out.print("<h2>Welcome to the ForeTees Reservation System</h2>");
                                        //out.print("<br />");
                                        //out.print("Enter your username and password, then click <span class=\"nowrap\">Log In.</span>");
                                    out.print("</div>");
                                    out.print("<form action=\"/v5/servlet/Login\" method=\"post\" name=\"login\" id=\"login\">");
                                    out.print("<h1>Please Log In</h1>");
                                    out.print("<div class=\"fields\">");
                                    out.print("<input type=\"hidden\" name=\"clubname\" value=\""+club+"\">");
                                    out.print("<input type=\"hidden\" name=\"zipcode\" value=\""+zip+"\">");
                                    out.print("<input type=\"hidden\" name=\"store_rwd_cookie\" value=\"1\">");
                                    out.print("<label><span>User Name</span><span><input id=\"user_name\" name=\"user_name\" type=\"text\" maxlength=\"15\" /></span></label>");
                                    out.print("<label><span>Password</span><span><input name=\"password\" type=\"password\"/></span></label>");
                                    if(!forceRwd && !forceOldMobile && !Utilities.getBitFromRequest(req,ProcessConstants.RQA_APPMODE, ProcessConstants.APPMODE_BLOCK_RWD_SWITCH)){
                                        out.print("<div class=\"radioBarGroup\">");
                                        out.print("<label onclick=\";\"><input name=\"s_m\" type=\"radio\" value=\""+app_mode_desktop+"\""+(!Utilities.getBit(app_mode_current,ProcessConstants.APPMODE_RWD)?" checked":"")+"/><span>Desktop <span>"+ProcessConstants.LOGIN_DESKTOP_TOOLTIP+"</span></span></label>");
                                        out.print("<label onclick=\";\"><input name=\"s_m\" type=\"radio\" value=\""+app_mode_rwd+"\""+(Utilities.getBit(app_mode_current,ProcessConstants.APPMODE_RWD)?" checked":"")+"/><span>Mobile <span>"+ProcessConstants.LOGIN_MOBILE_TOOLTIP+"</span></span></label>");
                                        out.print("</div>");
                                    } else if(!forceOldMobile){
                                        out.print("<input type=\"hidden\" name=\"s_m\" value=\""+app_mode_current+"\">");
                                    } else {
                                        out.print("<input type=\"hidden\" name=\"s_m\" value=\"0\">");
                                    }
                                    out.print("<div id=\"login_help_wrapper\">");
                                        out.print("<div id=\"login_help\">");
                                            out.print("<div class=\"helpLink\"><a href=\"/v5/servlet/Login?help=yes&amp;clubname="+club+"\"><span>Need Assistance?</span></a></div>");
                                            if(forceOldMobile){
                                                out.print("<div class=\"helpLink\"><a href=\"/"+club+"/mlogin.jsp\"><span>Use Mobile Login</span></a></div>");
                                            }
                                        out.print("</div>");
                                    out.print("</div>");
                                    out.print("<input type=\"submit\" value=\"Log In\" name=\"submit\" class=\"login_button_lg\">");
                                    out.print("</div>");
                                out.print("</form>");
                            out.print("</div>");
                        out.print("</div>");
                        out.print("<div id=\"footer\">");
                            out.print("<div id=\"local_links\">");
                                out.print("<h3>Other Links:</h3>");
                                    out.print("<div>");
                                        out.print("<a href=\"http://"+website_url+"\" target=\"_blank\">visit your club's website</a>");
                                        out.print("<a href=\"http://www.accuweather.com/adcbin2/local_index?zipcode="+zip+"\" target=\"_blank\">view local weather</a>");
                                        out.print("<a href=\"http://www.foretees.com/\" target=\"_blank\">visit our corporate site</a>");
                                        if(!forceOldMobile){
                                            out.print("<a href=\"/"+club+"/mlogin.jsp\">use former mobile login</a>");
                                        }
                                    out.print("</div>");
                                out.print("</div>");
                                out.print("<div id=\"footertext\">");
                                    out.print("<p>");
                                    out.print(" FOR CLUB MEMBERS & STAFF ONLY!");
                                    out.print(" Contact your club manager or professional staff for assistance.");
                                    out.print(" Privacy Statement: We absolutely will not divulge any information from this site to a third party under any circumstances.");
                                    out.print("</p><p>");
                                    out.print(" <span>Copyright &copy; "+year+" ForeTees, LLC</span> <span>All rights reserved.</span>");
                                    out.print("</p>");
                                out.print("</div>");
                            out.print("</div>");
                        out.print("</div>");
                    out.print("</div>");
                    out.print("</body>");
                    out.print("</html>");
                }
            }
            out.close();
            
        }
        Connect.close(rs,stmt,con);
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

