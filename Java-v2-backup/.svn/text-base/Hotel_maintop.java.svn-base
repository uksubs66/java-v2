/***************************************************************************************
 *   Hotel_maintop:  This servlet will display the Hotel Users navigation bar (Top of Page).
 *
 *
 *   called by:  Login servlet
 *
 *   created:  1/7/2004   JAG
 *
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionModel;
import com.foretees.client.action.TabBarRenderer;


import com.foretees.client.layout.Separator;
import com.foretees.client.attribute.Image;

public class Hotel_maintop extends HttpServlet {

 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   //
   //  Prevent cacheing so sessions are not mangled
   //
   resp.setHeader("Pragma","no-cache");               // for HTTP 1.0
   resp.setHeader("Cache-Control","no-store, no-cache, must-revalidate");    // for HTTP 1.1
   resp.setDateHeader("Expires",0);                   // prevents caching at the proxy server

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

    HttpSession session = SystemUtils.verifyHotel(req, out);             // check for intruder

    if (session == null) {

      return;
    }

    String club = "";
    String zip = "";
    String user = "";
    String fullname = "";    

    club = (String)session.getAttribute("club");
    zip = (String)session.getAttribute("zipcode");
    user = (String)session.getAttribute("user");
    
    //
    //  Get this year
    //
    Calendar cal = new GregorianCalendar();       // get todays date
    int year = cal.get(Calendar.YEAR);            // get the year

    
    if (club.startsWith("tpc")) {

       Connection con = null;

       try {
          con = dbConn.Connect(rev);           // get connection to the Vx db
          
          //
          //  Get the club names for each TPC club
          //
          PreparedStatement pstmt = con.prepareStatement("SELECT fullname FROM clubs WHERE clubname=?");
          
          pstmt.clearParameters();
          pstmt.setString(1, club);
          ResultSet rs = pstmt.executeQuery();

          if (rs.next()) {

             fullname = rs.getString("fullname");       // get the club's full name
          }
          pstmt.close();
          
       }
       catch (Exception e) {
       }
    }
    
   
    //
    //  Build the HTML page (main menu) 
    //
    out.println("<html><head>");

    out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=windows-1252\">");
    out.println("<meta http-equiv=\"Content-Language\" content=\"en-us\">");
    out.println("<title> \"ForeTees Hotel Main Title Page\"</title>");
    out.println("<link rel=\"stylesheet\" href=\"/" +rev+ "/web utilities/foretees.css\" type=\"text/css\">");
    out.println("<script type=\"text/javascript\" src=\"/" +rev+ "/web utilities/jquery.js\"></script>");

    out.println("<script type=\"text/javascript\">");
    out.println("function countHit(provider) {");
    out.println(" $.get('stats?countHit&provider='+provider, function(data) { });");
    out.println("}");
    out.println("</script>");

    out.println("</head>");

    out.println("<body leftmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" topmargin=\"0\">");

    out.println("<table width=\"100%\" height=\"82\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" bgcolor=\"#CCCCAA\">");

    out.println("<tr valign=\"top\" height=\"41\">");
    out.println("<td width=\"12%\" align=\"center\" valign=\"middle\" rowspan=\"2\">");
  
    if (club.startsWith("tpc") && !club.equals("tpctc")) {
        out.println("<img src=\"/" +club+ "/images/logo.gif\" border=0>");
    } else {
        out.println("<img src=\"/" +club+ "/images/logo.jpg\" border=0>");
    }
    out.println("</td>");

    out.println("<td valign=\"middle\" width=\"12%\" align=\"left\" rowspan=\"2\">");
     if (zip != null && !zip.equals( "" )) {     // if zipcode provided
         out.print("<a href=\"http://wwwa.accuweather.com/forecast.asp?partner=&zipcode=" +zip+ "\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
     } else {
         out.print("<a href=\"http://wwwa.accuweather.com/adcbin/public/golf_index.asp?partner=accuweather\" target=\"_blank\" onclick=\"countHit('ACCU')\">");
     }

      out.println("<img src=\"/" +rev+ "/images/weather-grn-off.gif\" border=0></a>");
      out.println("</td>");

      out.println("<td width=\"52%\" align=\"center\">");
      out.println("<font size=\"5\">Tee Time Management For Hotel Guests</font><br>");
      if (!fullname.equals("")) {
         out.println("<font size=\"4\" color=\"blue\">" +fullname+ "</font>");
      }
      out.println("</td>");

       out.println("<td width=\"24%\" align=\"right\" rowspan=\"2\">");
        out.println("<p>");
        out.println("<a href=\"http://www.foretees.com\" target=\"_blank\">");
         out.println("<img src=\"/" +rev+ "/images/foretees_nav.jpg\" border=0></a>&nbsp;");
        out.println("<br><font size=\"1\" color=\"#000000\">Copyright&nbsp;</font>");
        out.println("<font size=\"2\" color=\"#000000\">&#169;&nbsp;</font>");
        out.println("<font size=\"1\" color=\"#000000\">ForeTees, LLC&nbsp;<br>" +year+ " All rights reserved.&nbsp;");
        out.println("</font></p>");
      out.println("</td>");
    out.println("</tr>");

    out.println("<tr height=\"39\" valign=\"bottom\">");
      out.println("<td align=\"center\" nowrap>");
      out.println("<nobr>");
       if (club.startsWith("tpc")) {
          out.println("<a href=\"Hotel_home\" target=\"bot\"><img name=\"Hotel_Home\" src=\"/" +rev+ "/images/Hotel_Home.png\" hspace=\"0\" border=\"0\" title=\"Home\" alt=\"Home\"></a>");
       } else {
          out.println("<a href=\"/" +rev+ "/hotel_mainleft.htm\" target=\"bot\"><img name=\"Hotel_Home\" src=\"/" +rev+ "/images/Hotel_Home.png\" hspace=\"0\" border=\"0\" title=\"Home\" alt=\"Home\"></a>");
       }
       out.println("<a href=\"Hotel_select\" target=\"bot\"><img name=\"Hotel_View_Tee_Sheets\" src=\"/" +rev+ "/images/Hotel_View_Tee_Sheets.png\" hspace=\"0\" border=\"0\" title=\"View Tee Sheets\" alt=\"View the Tee Sheets\"></a>");
       out.println("<a href=\"Hotel_select\" target=\"bot\"><img name=\"Hotel_New_Tee_Time\" src=\"/" +rev+ "/images/Hotel_New_Tee_Time.png\" hspace=\"0\" border=\"0\" title=\"Make a New Tee Time\" alt=\"Make a New Tee Time\"></a>");
       out.println("<a href=\"Hotel_search\" target=\"bot\"><img name=\"Hotel_My_Reservations\" src=\"/" +rev+ "/images/Hotel_My_Reservations.png\" hspace=\"0\" border=\"0\" title=\"Display Your Reservations\" alt=\"Display Your Reservations\"></a>");
       out.println("<a href=\"/" +rev+ "/hotel_search.htm\" target=\"bot\"><img name=\"Hotel_Search\" src=\"/" +rev+ "/images/Hotel_Search.png\" hspace=\"0\" border=\"0\" title=\"Search For Guests\" alt=\"Search For Guests\"></a>");
       out.println("<a href=\"Logout\" target=\"_top\"><img name=\"Hotel_Logout\" src=\"/" +rev+ "/images/Hotel_Logout.png\" hspace=\"0\" border=\"0\" title=\"Logout\" alt=\"Logout\"></a>");
       out.println("<a href=\"/" +rev+ "/hotel_help_tmp.htm\" target=\"bot\"><img name=\"Hotel_Help\" src=\"/" +rev+ "/images/Hotel_Help.png\" hspace=\"0\" border=\"0\" title=\"Help\" alt=\"Help\"></a>");
      out.println("</nobr></td>");
    out.println("</tr>");
    out.println("<tr height=\"2\" valign=\"bottom\">");
      out.println("<td align=\"center\" colspan=\"4\" nowrap><HR width=\"100%\"></td>");
    out.println("</tr>");
    out.println("</table>");
    out.println("</body></html>");
    out.close();
   
   
   
   
   /*
   out.println(SystemUtils.HeadTitle("ForeTees Hotel Main Title Page"));

   ActionModel tabs = new ActionModel();
   tabs.setLabel("Tee Time Management for Hotel Guests");

   Action tab1 = new Action("home", "Home", "", "/hotel_mainleft.htm");
   tab1.setTarget("bot");
   tab1.setOnMouseOver("document.images['Hotel_Home'].src = '/images/Hotel_Home-over.png'");
   tab1.setOnMouseOut("document.images['Hotel_Home'].src = '/images/Hotel_Home.png'");
   Image image1 = new Image("Hotel_Home", "/images/Hotel_Home.png", "Return to Main Menu");
   tab1.setImage(image1);
   tabs.add(tab1);

   Action tab2 = new Action("view tee times", "View Tee Sheets", "", "/servlet/Hotel_select");
   tab2.setTarget("bot");
   tab2.setOnMouseOver("document.images['Hotel_View_Tee_Sheets'].src = '/images/Hotel_View_Tee_Sheets-over.png'");
   tab2.setOnMouseOut("document.images['Hotel_View_Tee_Sheets'].src = '/images/Hotel_View_Tee_Sheets.png'");
   Image image2 = new Image("Hotel_View_Tee_Sheets", "/images/Hotel_View_Tee_Sheets.png", "View the Tee Sheets");
   tab2.setImage(image2);
   tabs.add(tab2);

   Action tab3 = new Action("new tee time", "New Tee Time", "", "/servlet/Hotel_select");
   tab3.setTarget("bot");
   tab3.setOnMouseOver("document.images['Hotel_New_Tee_Time'].src = '/" +rev+ "/images/Hotel_New_Tee_Time-over.png'");
   tab3.setOnMouseOut("document.images['Hotel_New_Tee_Time'].src = '/" +rev+ "/images/Hotel_New_Tee_Time.png'");
   Image image3 = new Image("Hotel_New_Tee_Time", "/images/Hotel_New_Tee_Time.png", "Make a New Tee Time");
   tab3.setImage(image3);
   tabs.add(tab3);

   Action tab4 = new Action("view tee times", "My Reservations", "", "/servlet/Hotel_search");
   tab4.setTarget("bot");
   tab4.setOnMouseOver("document.images['Hotel_My_Reservations'].src = '/" +rev+ "/images/Hotel_My_Reservations-over.png'");
   tab4.setOnMouseOut("document.images['Hotel_My_Reservations'].src = '/" +rev+ "/images/Hotel_My_Reservations.png'");
   Image image4 = new Image("Hotel_My_Reservations", "/images/Hotel_My_Reservations.png", "Search for All Guests' Tee Times");
   tab4.setImage(image4);
   tabs.add(tab4);

   Action tab5 = new Action("view tee times", "Search For Guests", "", "/hotel_search.htm");
   tab5.setTarget("bot");
   tab5.setOnMouseOver("document.images['Hotel_Search'].src = '/" +rev+ "/images/Hotel_Search-over.png'");
   tab5.setOnMouseOut("document.images['Hotel_Search'].src = '/" +rev+ "/images/Hotel_Search.png'");
   Image image5 = new Image("Hotel_Search", "/images/Hotel_Search.png", "Search for Individual Guest Tee Times");
   tab5.setImage(image5);
   tabs.add(tab5);

   Separator sep = new Separator();
   tabs.add(sep);

   Action tab6 = new Action("view tee times", "Logout", "", "/servlet/Logout");
   tab6.setTarget("_top");
   tab6.setOnMouseOver("document.images['Hotel_Logout'].src = '/" +rev+ "/images/Hotel_Logout-over.png'");
   tab6.setOnMouseOut("document.images['Hotel_Logout'].src = '/" +rev+ "/images/Hotel_Logout.png'");
   Image image6 = new Image("Hotel_Logout", "/images/Hotel_Logout.png", "Exit ForeTees");
   tab6.setImage(image6);
   tabs.add(tab6);

   Action tab7 = new Action("view tee times", "Help", "", "/hotel_help.htm");
   tab7.setTarget("_blank");
   tab7.setOnMouseOver("document.images['Hotel_Help'].src = '/" +rev+ "/images/Hotel_Help-over.png'");
   tab7.setOnMouseOut("document.images['Hotel_Help'].src = '/" +rev+ "/images/Hotel_Help.png'");
   Image image7 = new Image("Hotel_Help", "/images/Hotel_Help.png", "Get Help on Using ForeTees");
   tab7.setImage(image7);
   tabs.add(tab7);

   TabBarRenderer.render(tabs, "", rev, out);
    */


 }  // end of doGet

}
