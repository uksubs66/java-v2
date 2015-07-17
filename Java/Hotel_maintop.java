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


 }  // end of doGet

}
