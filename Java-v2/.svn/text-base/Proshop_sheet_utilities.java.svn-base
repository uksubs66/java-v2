/***************************************************************************************
 *   Proshop_sheet_utilities: This servlet will process any miscellaneous functions
 *                            initiated from the Pro Tee Sheet.
 *
 *
 *   Called by:     called by Proshop_sheet, Proshop_oldsheets
 *
 *
 *   Created:       2/03/2011
 *
 *
 *
 *   Last Updated:
 *
 *        4/11/14  Added updatePayNow to handle the on-the-fly updating of the advanced PayNow options from Proshop_sheet
 *        8/17/12  Added displayPhotos method
 *        3/21/11  Modified updateTmode so that it puts a current timestamp in last_mod_date
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.common.Utilities;
import com.foretees.common.parmCourse;
import com.foretees.common.getParms;

import com.foretees.common.Connect;

public class Proshop_sheet_utilities extends HttpServlet {

    String rev = SystemUtils.REVLEVEL;                              // Software Revision Level (Version)

    
    
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

     //doPost(req, resp);

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    Connection con = Connect.getCon(req);

    if (con == null) return;

  //String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");
    
    String todo = (req.getParameter("todo") == null) ? "" : req.getParameter("todo");

    if (todo.equals("updCW")) {

        updateTmode(club, req, out, con);

    } else if (todo.equals("updPN")) {

        updatePayNow(club, req, out, con);

    } else if (todo.equals("displayPhotos")) {

        displayPhotos(club, req, out, con);

    } else {

        out.println("nothing todo");

    }

 }


 private void displayPhotos(String club, HttpServletRequest req, PrintWriter out, Connection con) {


    //out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    //out.println("<html><head>");
    //out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/assets/jquery/jquery-1.7.1.min.js\"></script>");
    //out.println("<script type=\"text/javascript\" src=\"/v5/assets/jquery/fancybox/resize_fancybox.js\"></script>");

    //out.println("</head><body>");

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    ArrayList<String> usernames = new ArrayList<String>();
    ArrayList<String> player_names = new ArrayList<String>();
    ArrayList<String> filenames = new ArrayList<String>();

    int tid = 0;
    try {
        tid = Integer.parseInt(req.getParameter("tid"));
    } catch (Exception ignore) {
        out.println("<p>MISSING TEE TIME ID</p>");
        return;
    }

    try {

        pstmt = con.prepareStatement("" +
                "SELECT " +
                    "username1, username2, username3, username4, username5, " +
                    "player1, player2, player3, player4, player5 " +
                "FROM teecurr2 " +
                "WHERE teecurr_id = ? ");

        pstmt.clearParameters();
        pstmt.setInt(1, tid);
        rs = pstmt.executeQuery();

        if ( rs.next() ) {

            // ADD ONLY THE PLAYERS TO THE ARRAYS
            if (!rs.getString("player1").equals("")) {
                usernames.add(rs.getString("username1"));
                player_names.add(rs.getString("player1"));
            }

            if (!rs.getString("player2").equals("")) {
                usernames.add(rs.getString("username2"));
                player_names.add(rs.getString("player2"));
            }

            if (!rs.getString("player3").equals("")) {
                usernames.add(rs.getString("username3"));
                player_names.add(rs.getString("player3"));
            }

            if (!rs.getString("player4").equals("")) {
                usernames.add(rs.getString("username4"));
                player_names.add(rs.getString("player4"));
            }

            if (!rs.getString("player5").equals("")) {
                usernames.add(rs.getString("username5"));
                player_names.add(rs.getString("player5"));
            }
        }

    } catch (Exception exc) {

        out.println("displayPhoto: Gather usernames. Error=" + exc.getMessage());

    } finally {
        Connect.close(rs, pstmt);
    }
    
    // gather the filenames for member photos
    for (int i = 0; i < usernames.size(); i++) {

        try {
            
            String filename = "";

            if (!usernames.get(i).equals("")) {

                pstmt = con.prepareStatement(
                          "SELECT filename "
                        + "FROM member2b m "
                        + "INNER JOIN member_photos mp ON m.id = mp.member_id "
                        + "WHERE username = ? and approved = 1");

                pstmt.clearParameters();
                pstmt.setString(1, usernames.get(i));
                rs = pstmt.executeQuery();

                if ( rs.next() ) {
                    
                    if (!rs.getString("filename").equals("")) {
                        
                        filename = rs.getString("filename");
                        
                        File f = new File("/srv/webapps/" + rev + "/member_photos/" + club + "/" + filename);
                        
                        if (!f.isFile()) {
                            filename = "";    // File doesn't exist, so treat it like they don't have a photo
                        }
                    }
                }
            }
            
            filenames.add(filename);

        } catch (Exception exc) {
            out.println("displayPhoto: Gather photos. Error=" + exc.getMessage());
        } finally {
            Connect.close(rs, pstmt);
        }
    }

    // we now have all our data, lets build the display
    out.println("<table width=\"100%\" border=\"0\" cellspacing=\"5\">");
    out.println("<caption><h2>Member Photos</h2></caption>");
    //out.println("<tr><td colspan=\"" + usernames.size() + "\" align=\"center\">Member Photos</td></tr>");
    out.println("<tr valign=\"top\">");

    for (int i = 0; i < player_names.size(); i++) {

        try {

            if (!filenames.get(i).equals("")) {

                out.print("<td align=\"center\"><b>");
                out.print(player_names.get(i));
                out.print("</b><br>");
                out.print("<img src=\"/v5/member_photos/" + club + "/" + filenames.get(i) + "\" border=\"1\" width=\"200\">");
                out.println("</td>");

            } else {

                //out.println("<td>No Photo Available</td>");

                out.print("<td align=\"center\"><b>");
                out.print(player_names.get(i));
                out.print("</b><br>");
                out.print("<img src=\"/v5/member_photos/default_avatar.jpg\" border=\"1\" width=\"200\">"); // use different colored border for members/guests?
                out.println("</td>");

            }

        } catch(Exception exc) {

            out.println("displayPhoto: Display photos. Error=" + exc.getMessage());
            
        }
    }

    out.println("</tr>");
    out.println("</table>");
    
    //out.println("</body></html>");

 }

 
 private void updateTmode(String club, HttpServletRequest req, PrintWriter out, Connection con) {


    PreparedStatement pstmt = null;

    String temp = "";

    int teecurr_id = 0;
    int playern = 0;

    String result = "";
    int count = 0;


    //
    //  Get the parms for this request
    //
    boolean changeAll = (req.getParameter("changeAll") != null && req.getParameter("changeAll").equals("true"));
    boolean nineHole = (req.getParameter("ninehole") != null && req.getParameter("ninehole").equals("true"));

    String pcw = (req.getParameter("tmode") == null) ? "" : req.getParameter("tmode");

    if (req.getParameter("playerSlot") != null) {

       temp = req.getParameter("playerSlot");

       if (!temp.equals( "" )) playern = Integer.parseInt(temp);
    }

    if (req.getParameter("tid") != null) {

       temp = req.getParameter("tid");

       if (!temp.equals( "" )) teecurr_id = Integer.parseInt(temp);
    }


    if (!changeAll) {

       //  update the player's mode of trans

       try {

         pstmt = con.prepareStatement (
            "UPDATE teecurr2 " +
            "SET p" +playern+ "cw = ?, p9" + playern + " = ?, last_mod_date = now() " +
            "WHERE teecurr_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, pcw);
         pstmt.setInt(2, (nineHole) ? 1 : 0);
         pstmt.setInt(3, teecurr_id);

         count = pstmt.executeUpdate();

         if (count > 0) {
             result = "OK";
         } else {
             result = "FAIL";
         }


       } catch (Exception e) {

           Utilities.logError("Proshop_sheet_utilities: updateTmode single - Err=" + e.toString());
           result = "ERROR1";

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

    } else {

       //
       //  Update the tee time and set it no longer in use
       //
       try {

          pstmt = con.prepareStatement (
             "UPDATE teecurr2 SET " +
             "p1cw=?, p2cw=?, p3cw=?, p4cw=?, in_use=0, p5cw=?, p91=?, p92=?, p93=?, p94=?, p95=?, last_mod_date = now() " +
             "WHERE teecurr_id = ?");

          pstmt.clearParameters();          // clear the parms
          pstmt.setString(1, pcw);
          pstmt.setString(2, pcw);
          pstmt.setString(3, pcw);
          pstmt.setString(4, pcw);
          pstmt.setString(5, pcw);
          pstmt.setInt(6, (nineHole) ? 1 : 0);
          pstmt.setInt(7, (nineHole) ? 1 : 0);
          pstmt.setInt(8, (nineHole) ? 1 : 0);
          pstmt.setInt(9, (nineHole) ? 1 : 0);
          pstmt.setInt(10, (nineHole) ? 1 : 0);
          pstmt.setInt(11, teecurr_id);

          count = pstmt.executeUpdate();

          if (count > 0) {
             result = "OK";
          } else {
             result = "FAIL";
          }

       } catch (Exception e) {

           Utilities.logError("Proshop_sheet_utilities: updateTmode ALL - Err=" + e.toString());
           result = "ERROR2";

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

    }

    out.println(result);

 }

 
 private void updatePayNow(String club, HttpServletRequest req, PrintWriter out, Connection con) {

    PreparedStatement pstmt = null;

    String temp = "";

    int teecurr_id = 0;
    int playern = 0;

    String result = "";
    int count = 0;


    //  Get the parms for this request
    boolean changeAll = (req.getParameter("changeAll") != null && req.getParameter("changeAll").equals("true"));

    String pos = (req.getParameter("pos") == null) ? "" : req.getParameter("pos");

    if (req.getParameter("playerSlot") != null) {
        
       temp = req.getParameter("playerSlot");

       if (!temp.equals( "" )) playern = Integer.parseInt(temp);
    }

    if (req.getParameter("tid") != null) {

       temp = req.getParameter("tid");

       if (!temp.equals( "" )) teecurr_id = Integer.parseInt(temp);
    }


    if (!changeAll) {

       //  update only this player's pos value
       try {

         pstmt = con.prepareStatement (
            "UPDATE teecurr2 " +
            "SET pos" +playern+ " = ?, last_mod_date = now() " +
            "WHERE teecurr_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, pos);
         pstmt.setInt(2, teecurr_id);

         count = pstmt.executeUpdate();

         if (count > 0) {
             result = "OK";
         } else {
             result = "FAIL";
         }


       } catch (Exception e) {

           Utilities.logError("Proshop_sheet_utilities: updatePayNow single - Err=" + e.toString());
           result = "ERROR1";

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

    } else {

       //  Update pos values for all players
       try {

          pstmt = con.prepareStatement (
             "UPDATE teecurr2 SET " +
             "pos1=?, pos2=?, pos3=?, pos4=?, pos5=?, in_use=0, last_mod_date = now() " +
             "WHERE teecurr_id = ?");

          pstmt.clearParameters();          // clear the parms
          pstmt.setString(1, pos);
          pstmt.setString(2, pos);
          pstmt.setString(3, pos);
          pstmt.setString(4, pos);
          pstmt.setString(5, pos);
          pstmt.setInt(6, teecurr_id);

          count = pstmt.executeUpdate();

          if (count > 0) {
             result = "OK";
          } else {
             result = "FAIL";
          }

       } catch (Exception e) {

           Utilities.logError("Proshop_sheet_utilities: updatePayNow ALL - Err=" + e.toString());
           result = "ERROR2";

       } finally {

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }

    }

    out.println(result);

 }

/*
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {


    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    PreparedStatement pstmt = null;
    ResultSet rs = null;

    HttpSession session = SystemUtils.verifyPro(req, out);             // check for intruder

    if (session == null) return;

    String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");

    Connection con = Connect.getCon(req);                      // get DB connection

    if (con == null) {

        out.println(SystemUtils.HeadTitle("DB Connection Error"));
        out.println("<BODY><CENTER><BR>");
        out.println("<BR><BR><H3>Database Connection Error</H3>");
        out.println("<BR><BR>Unable to connect to the Database.");
        out.println("<BR>Please try again later.");
        out.println("<BR><BR>If problem persists, contact customer support.");
        out.println("<BR><BR>");
        out.println("<a href=\"javascript:history.back(1)\">Return</a>");
        out.println("</CENTER></BODY></HTML>");
        out.close();

        return;
    }


    int sess_activity_id = (Integer)session.getAttribute("activity_id");

    
    //
    //  Process according to the task requested
    //
    if (req.getParameter("changeMOT") != null) {

        changeMOT(club, req, out, con);          // Change MOT - first call from Proshop_sheet
        return;
    }

    if (req.getParameter("changeMOT2") != null) {

        changeMOT2(club, req, out, con);         // Change MOT - 2nd call from submit in chamgeMOT below
        return;
    }

 }     // end of doPost
    
    
 // ********************************************************************
 //  Request to change a player's MOT
 // ********************************************************************

 private void changeMOT(String club, HttpServletRequest req, PrintWriter out, Connection con) {


    String temp = "";
    
    int teecurr_id = 0;
    int playern = 0;
    
    boolean updateAccess = SystemUtils.verifyProAccess(req, "TS_UPDATE", con, out);    // can user update tee times?

    //
    //  parm block to hold the course parameters
    //
    parmCourse parmc = new parmCourse();          // allocate a parm block

    
    //
    //  Get the parms for this request
    //
    String course = (req.getParameter("course") == null) ? "" : req.getParameter("course");
    
    String player = (req.getParameter("player") == null) ? "" : req.getParameter("player");

    String pcw = (req.getParameter("pcw") == null) ? "" : req.getParameter("pcw");
    
    if (req.getParameter("playern") != null) {

       temp = req.getParameter("playern");
       
       if (!temp.equals( "" )) playern = Integer.parseInt(temp);
    }
       
    if (req.getParameter("teecurr_id") != null) {

       temp = req.getParameter("teecurr_id");
       
       if (!temp.equals( "" )) teecurr_id = Integer.parseInt(temp);
    }    
    
    if (pcw.endsWith( "9" )) {        // strip 9-hole indicator if there
       
       pcw = pcw.substring(0,pcw.length()-1);       
    }
    
    
    // START PAGE OUTPUT
    out.println(SystemUtils.HeadTitle("Proshop Change Mode"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\"><center>");
    
    out.println("<p align=center><font size=4>Change Mode of Transportation</font></p>");
    
    if (updateAccess == false) {
       
       out.println("<p align=center><font size=2>Sorry, you do not have the authority to make this change.</font></p>");
       
    } else if (playern < 1 || teecurr_id < 1) {
       
       out.println("<p align=center><font size=2>System Error - Please inform ForeTees support. </font></p>");
       
    } else {
    
       //  all ok - get the available modes
       
       out.println("<p align=center><font size=2>Player: <strong>" +player+ "</strong>, Current Mode: <strong>" +pcw+ "</strong></font></p>");
       
       out.println("<p align=center><font size=2>Select the new mode from the following list:</font></p>");
       
       try {

          getParms.getTmodes(con, parmc, course);      // get the tmodes for this course

       } catch (Exception exc) {

           Utilities.logError("Proshop_sheet_utilities: Error Loading Modes. Club=" + club + ", err=" + exc.toString());
       }
         
       
       out.println("<form action=\"Proshop_sheet_utilities\" method=post>");   // return to doPost on submit
       out.println("<input type=\"hidden\" name=\"teecurr_id\" value=\"" + teecurr_id + "\">");
       out.println("<input type=\"hidden\" name=\"playern\" value=\"" + playern + "\">");
       out.println("<input type=\"hidden\" name=\"changeMOT2\" value=\"yes\">");
       
       out.println("<table align=center width=120 border=0>");
       
       for (int i=0; i<parmc.tmode_limit; i++) {        // get all c/w options

           if (!parmc.tmodea[i].equals( "" )) {
              
              out.println("<tr><td align=left width=40>");
       
              if (parmc.tmodea[i].equals( pcw )) {
                
                  out.println("<input type=\"radio\" checked name=\"pcw\" value=\"" +parmc.tmodea[i]+ "\">");
                 
              } else {
              
                  out.println("<input type=\"radio\" name=\"pcw\" value=\"" +parmc.tmodea[i]+ "\">");              
              }

              out.println("</td><td align=left>" +parmc.tmodea[i]+ "</td></tr>");      
           }
       }
     
       out.println("</table>");
       
       out.println("<p align=center><input type=\"submit\" value=\"Change Mode\" alt=\"submit\"></form></p>");
       
    }
    
    out.println("<form><p align=center><br>");
    out.println("<input type=\"button\" value=\"Cancel - Close\" onClick='self.close();'>");
    out.println("</p></form>");
    
    
    out.println("</center></body></html>");
    out.close();
    
 }   // end of changeMOT   
    
 
    
    
 // ********************************************************************
 //  Request to change a player's MOT (submit from above)
 // ********************************************************************

 private void changeMOT2(String club, HttpServletRequest req, PrintWriter out, Connection con) {


    PreparedStatement pstmt = null;

    ResultSet rs = null;

    String temp = "";
    
    int teecurr_id = 0;
    int playern = 0;
    
    //
    //  Get the parms for this request
    //
    String pcw = (req.getParameter("pcw") == null) ? "" : req.getParameter("pcw");
    
    if (req.getParameter("playern") != null) {

       temp = req.getParameter("playern");
       
       if (!temp.equals( "" )) playern = Integer.parseInt(temp);
    }
       
    if (req.getParameter("teecurr_id") != null) {

       temp = req.getParameter("teecurr_id");
       
       if (!temp.equals( "" )) teecurr_id = Integer.parseInt(temp);
    }    
    
    
    // START PAGE OUTPUT
    out.println(SystemUtils.HeadTitle("Proshop Change Mode"));
    out.println("<body bgcolor=\"#FFFFFF\" text=\"#000000\"><center>");
    
    out.println("<p align=center><font size=4>Change Mode of Transportation</font></p>");
        
    
    //
    //  Verify parms
    //
    if (pcw.equals("") || playern < 1 || teecurr_id < 1) {
       
       out.println("<p align=center><font size=2>System Error - Please inform ForeTees support. </font></p>");
       
       out.println("<p align=center><font size=2>Pcw = " +pcw+ ", playern = " +playern+ ", id = " +teecurr_id+ "</font></p>");
 
    } else {
    
       //  update the player's mode of trans
       
       try {

         pstmt = con.prepareStatement (
            "UPDATE teecurr2 SET p" +playern+ "cw = ? " +
            "WHERE teecurr_id = ?");

         pstmt.clearParameters();        // clear the parms
         pstmt.setString(1, pcw);
         pstmt.setInt(2, teecurr_id);
    
         pstmt.executeUpdate();      // execute the prepared stmt

         pstmt.close();
    
 
          out.println("<p align=center><font size=2>Thank you. The mode has been changed to <strong>" +pcw+ "</strong>.</font></p>");

          out.println("<p align=center><font size=2>NOTE: You must refresh the tee sheet to see the change.</font></p>");
 
       } catch (Exception exc) {
            
           Utilities.logError("Proshop_sheet_utilities: Error Changing Mode. Club=" + club + ", err=" + exc.toString());

           out.println("<p align=center><font size=2>System Error - Please inform ForeTees support. </font></p>");
           out.println("<p align=center><font size=2>Error = " + exc.toString() +"</font></p>");
        
       } finally {

           try { rs.close(); }
           catch (Exception ignore) {}

           try { pstmt.close(); }
           catch (Exception ignore) {}

       }
    }
    
    out.println("<form><p align=center><br>");
    out.println("<input type=\"button\" value=\"Close\" onClick='self.close();'>");
    out.println("</p></form>");
    
    out.println("</center></body></html>");
    out.close();
    
 }   // end of changeMOT2   
*/

} // end servlet public class
