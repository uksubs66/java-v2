/***************************************************************************************     
 *   Support_billing:  This servlet will allow the sales staff to create a billing file for each club.
 *
 *                   This is used to gather the number of billable members for a club.
 *
 *
 *   called by:  support_main.htm
 *               sales_main.htm 
 *
 *   created: 6/06/2007   Bob P.
 *
 *   last updated:
 *
 *          7/19/07  Add support for 'billing' flag in member2b (for excluded members - Roster Sync clubs).
 *
 *
 ***************************************************************************************
 */
    
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.mTypeArrays;


public class Support_billing extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 String sales = "sales";



 //*********************************************************************************
 // Process the request from s...._main.htm
 //*********************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
   Statement stmt = null;
   ResultSet rs = null;
     
   HttpSession session = null; 


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.startsWith ( sales )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   mTypeArrays mArrays = new mTypeArrays();     // setup for mship and mtype arrays

   String roundsPer = "";
   
   int maxMems = mArrays.MAX_Mems;
   int maxMships = mArrays.MAX_Mships;
   int i = 0;
     
  
   //
   //  Get the mtype and mship types from club5 
   //
   try {

      mArrays = getClub.getMtypes(mArrays, con);         // skip it all if this fails

   }
   catch (Exception exc) {
   }


   //
   //  Now present a form to display and gather the options
   //
   out.println("<HTML><HEAD><TITLE>Support Billing</TITLE></HEAD>");
   out.println("<BODY><CENTER><BR><H3>Set Club Billing Options</H3>");
     
   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" width=\"500\">");
   out.println("<tr><td align=\"center\">");
   out.println("<BR>Indicate which Membership and Member Types are eligible for billing.");
   out.println("<BR><BR>The report will be provided in Excel format.<BR>Save the report for each club and each billing period for future reference.");
   out.println("</td></tr></table>");
   out.println("<BR>");

   out.println("<table border=\"1\" bgcolor=\"#F5F5DC\" cellpadding=\"6\">");
        
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>&nbsp;&nbsp;Membership Types: (Rounds Allowed)</b>&nbsp;&nbsp;");
      out.println("</font></td><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>&nbsp;&nbsp;Member Types:</b>&nbsp;&nbsp;");
      out.println("</font></td></tr>");

      out.println("<form action=\"/" +rev+ "/servlet/Support_billing\" method=\"post\">");
        
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
     
      for (i=0; i < maxMships; i++) {

         if (!mArrays.mship[i].equals( "" )) {
            
            roundsPer = getRoundsPer(mArrays.mship[i], con);      // get the number of rounds per 'period' info for this mship type

            out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"mship" +i+ "\" value=\"1\">&nbsp;&nbsp;" +mArrays.mship[i]+ "&nbsp;&nbsp;&nbsp;&nbsp;" +roundsPer+ " <br>");
         }
      }
      out.println("</font></td><td align=\"left\">");
      out.println("<font size=\"2\">");
   
      for (i=0; i < maxMems; i++) {

         if (!mArrays.mem[i].equals( "" )) {

            out.println("&nbsp;&nbsp;<input type=\"checkbox\" name=\"mtype" +i+ "\" value=\"1\">&nbsp;&nbsp;" +mArrays.mem[i]+ "<br>");
         }
      }
      out.println("</font></td></tr>");

      out.println("</table>");

      out.println("<p align=\"center\">");
      out.println("<BR><b>NOTE:</b> We only charge for Membership Types that are allowed to golf <b>More Than 1 Time per Month</b>.<BR><BR>");
      out.println("We do not charge for Employees, so skip any types like Staff, Employee, etc.<BR><BR>");
      out.println("<input type=\"submit\" name=\"Submit\" value=\"Submit - Web Page\">");
      out.println("<input type=\"submit\" name=\"excel\" value=\"Submit - Excel\">");
      out.println("</p></form>");

      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return - Cancel</A>.");
      } else {
         out.println("<BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return - Cancel</A>.");
      }
   out.println("</CENTER></BODY></HTML>");
   out.close();

 }        // end of doGet
 

 //*********************************************************************************
 // Process the form request from above
 //*********************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   ResultSet rs = null;

   HttpSession session = null;


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username

   if (!user.equals( support ) && !user.startsWith( sales )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name

   try {
      con = dbConn.Connect(club);

   }
   catch (Exception exc) {

      // Error connecting to db....

      out.println("<HTML><HEAD><TITLE>DB Connection Error Received</TITLE></HEAD>");
      out.println("<BODY><CENTER><H3>DB Connection Error</H3>");
      out.println("<BR><BR>Unable to connect to the DB.");
      out.println("<BR>Exception: "+ exc.getMessage());
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   mTypeArrays mArrays = new mTypeArrays();     // setup for mship and mtype arrays

   int Max = mArrays.MAX_Mems;
   int i = 0;
   int i2 = 0;
   int i3 = 0;

   int mtypeTotal = 0;       // counts
   int mtypeInact = 0;
   int mtypeExclude = 0;
   int mtypeBill = 0;
   int mshipTotal = 0;      
   int mshipInact = 0;
   int mshipExclude = 0;
   int mshipBill = 0;
   int grandTotal = 0;
   int grandInact = 0;
   int grandExclude = 0;
   int grandBill = 0;
   int noBill = 0;
   
   boolean excel = false;

   String mtype = "";
   String mship = "";
     
   String sdate = String.valueOf(new java.util.Date());         // get date and time string


   //
   //  Get the mtype and mship types from club5
   //
   try {

      mArrays = getClub.getMtypes(mArrays, con);         // skip it all if this fails

   }
   catch (Exception exc) {
   }


   //
   //  Arrays to hold selections
   //
   String [] mshipA = new String [Max];
   String [] mtypeA = new String [Max];

   //
   //  init the arrays
   //
   for (i=0; i<Max; i++) {
     
      mshipA[i] = "";
      mtypeA[i] = "";
   }


   //
   //  Get the parms passed
   //
   if (req.getParameter("excel") != null) {
      
      excel = true;
   }

   for (i=0; i<Max; i++) {

      if (req.getParameter("mship" +i) != null) {

         mshipA[i2] = mArrays.mship[i];           //  get selected mship type
         i2++;                    
      }

      if (req.getParameter("mtype" +i) != null) {

         mtypeA[i3] = mArrays.mem[i];           //  get selected mem type
         i3++;
      }
   }


   //
   //  Output results in EXCEL format
   //
   if (excel == true) {
      
      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
   }

   out.println("<HTML><HEAD><TITLE>Billing Complete</TITLE></HEAD>");
   out.println("<BODY><CENTER><H3>Billing Information For " +club+ "</H3>");
   out.println("<BR><b>Date: </b> " +sdate);
   out.println("<BR><BR>");

   out.println("<table bgcolor=\"#F5F5DC\" border=\"1\">");
      out.println("<tr><td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Membership Type</b>");
      out.println("</font></td>");
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">");
      out.println("<b>Member Type</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Total</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Inact</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Excluded</b>");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\">");
      out.println("<b>Billable</b>");
      out.println("</font></td></tr>");

   //
   //  Gather the counts - billable members per mship and mtype
   //
   try {

      i = 0;
      i2 = 0;
      i3 = 0;
        
      mship = mshipA[i];                             // get first mship type
      i++;

      while (i < Max && !mship.equals( "" )) {       // do each requested mship type
        
         out.println("<tr><td align=\"left\">");     // one row for mship type
         out.println("<font size=\"2\">");
         out.println(mship);
         out.println("</font></td>");
         out.println("<td align=\"left\">");
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td></tr>");
     
         mshipTotal = 0;       // reset total counts for mship type
         mshipInact = 0;
         mshipExclude = 0;
         mshipBill = 0;
           
         i2 = 0;
         mtype = mtypeA[i2];                             // get first mtype
         i2++;

         while (i2 < Max && !mtype.equals( "" )) {       // do each requested mtype

            pstmt1 = con.prepareStatement (
                     "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ?");

            pstmt1.clearParameters();         
            pstmt1.setString(1, mship);            
            pstmt1.setString(2, mtype);
            rs = pstmt1.executeQuery();            // execute the prepared stmt

            if(rs.next()) {

               mtypeTotal = rs.getInt("COUNT(*)");
            }
            pstmt1.close();

            pstmt1 = con.prepareStatement (
                     "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ? AND inact = 1");

            pstmt1.clearParameters();
            pstmt1.setString(1, mship);
            pstmt1.setString(2, mtype);
            rs = pstmt1.executeQuery();            // execute the prepared stmt

            if(rs.next()) {

               mtypeInact = rs.getInt("COUNT(*)");
            }
            pstmt1.close();

            pstmt1 = con.prepareStatement (
                     "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ? AND billable = 0");

            pstmt1.clearParameters();
            pstmt1.setString(1, mship);
            pstmt1.setString(2, mtype);
            rs = pstmt1.executeQuery();            // execute the prepared stmt

            if(rs.next()) {

               mtypeExclude = rs.getInt("COUNT(*)");
            }
            pstmt1.close();


            noBill = 0;             // init counter for combined inact OR excluded members (one member could be both!!)

            pstmt1 = con.prepareStatement (
                     "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ? AND (billable = 0 OR inact = 1)");

            pstmt1.clearParameters();
            pstmt1.setString(1, mship);
            pstmt1.setString(2, mtype);
            rs = pstmt1.executeQuery();            // execute the prepared stmt

            if(rs.next()) {

               noBill = rs.getInt("COUNT(*)");
            }
            pstmt1.close();


            mtypeBill = mtypeTotal - noBill;         // get billable count


            out.println("<tr><td align=\"left\">");     // one row per mtype
            out.println("<font size=\"2\">&nbsp;");
            out.println("</font></td>");
            out.println("<td align=\"left\">");
            out.println("<font size=\"2\">");
            out.println(mtype);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">" +mtypeTotal);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">" +mtypeInact);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">" +mtypeExclude);
            out.println("</font></td>");
            out.println("<td align=\"center\">");
            out.println("<font size=\"2\">" + mtypeBill);
            out.println("</font></td></tr>");
  
            mshipTotal += mtypeTotal;       // gather total counts for mship type
            mshipInact += mtypeInact;
            mshipExclude += mtypeExclude;
            mshipBill += mtypeBill;
              
            //
            //  Do next mtype
            //
            mtype = mtypeA[i2];                          // get next mtype
            i2++;
         }                      // end of WHILE mtypes

         out.println("<tr><td align=\"left\">");     // row for mship totals 
         out.println("<font size=\"2\">&nbsp;");
         out.println("</font></td>");
         out.println("<td align=\"left\">");
         out.println("<font size=\"2\">");
         out.println("<b>Total for Mship:</b>");
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">" + mshipTotal);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">" + mshipInact);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">" + mshipExclude);
         out.println("</font></td>");
         out.println("<td align=\"center\">");
         out.println("<font size=\"2\">" + mshipBill);
         out.println("</font></td></tr>");

         grandTotal += mshipTotal;       // gather grand total counts
         grandInact += mshipInact;
         grandExclude += mshipExclude;
         grandBill += mshipBill;

         //
         //  Do next mship type
         //
         mship = mshipA[i];                             // get next mship type
         i++;
      }                      // end of WHILE mships

      //
      //  do row for Grand Totals
      //
      out.println("<tr><td align=\"left\">");     // Grand Totals
      out.println("<font size=\"2\">&nbsp;");
      out.println("<b>GRAND TOTALS:</b>");
      out.println("</font></td>");
      out.println("<td align=\"left\">");
      out.println("<font size=\"2\">&nbsp;");
      out.println("</font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><b>" + grandTotal);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><b>" + grandInact);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><b>" + grandExclude);
      out.println("</b></font></td>");
      out.println("<td align=\"center\">");
      out.println("<font size=\"2\"><b>" + grandBill);
      out.println("</b></font></td></tr>");
      out.println("</table>");

      out.println("<BR><BR><b>Notice:</b> Inactive members are those that are not billable and/or have been marked as inactive by the admin or via roster sync.");
      out.println("<br>Excluded members are those that are not billable and have been marked as excluded by the admin. This is for clubs that have roster sync");
      out.println("<br>where the inact flag can be cleared by the roster sync process when members are included in the RS file.");

   }
   catch (Exception exc) {

      out.println("</td></tr></table>");
      out.println("<BR><BR>Exception: "+ exc.getMessage());
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }
      out.println("</CENTER></BODY></HTML>");
      return;
   }


   //
   //   Finish the page 
   //
   if (excel == false) {
      
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/support_main.htm\">Return</A>.");
      } else {
         out.println("<BR><BR> <A HREF=\"/" +rev+ "/sales_main.htm\">Return</A>.");
      }
   }
      
      
   out.println("</CENTER></BODY></HTML>");
   out.close();

   if (con != null) {
      try {
         con.close();       // Close the db connection........
      }
      catch (SQLException ignored) {
      }
   }
     
 }   
 
 
 
 private String getRoundsPer(String mship, Connection con) {
    
    PreparedStatement pstmt = null;
    ResultSet rs = null;
   
   
    String roundsPer = "";
    String period = "";
    int mtimes = 0;
    
    //
    //  Get the number of rounds per 'period' that the specified mship can play
    //
    try {

      pstmt = con.prepareStatement (
              "SELECT mtimes, period FROM mship5 WHERE mship = ?");

      pstmt.clearParameters();
      pstmt.setString(1, mship);
      rs = pstmt.executeQuery();

      if (rs.next()) {

          mtimes = rs.getInt("mtimes");
          period = rs.getString("period");
          
          if (mtimes == 0) {
             
             roundsPer = "(Unlimited)";
             
          } else {
           
             roundsPer = "(" +mtimes+ " per " +period + ")";
          }
          
      } else {
         
         roundsPer = "(mship type Not Found)";
      }
    
    

   } catch (Exception e) {

       roundsPer = "(ERROR - notify Brad. Error=" + e.getMessage() + ")";
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }
    
   return(roundsPer);
 }
 
 
    
 // *********************************************************
 // Illegal access by user - force user to login....
 // *********************************************************

 private void invalidUser(PrintWriter out) {

   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
   out.println("<BODY><CENTER><img src=\"/" +rev+ "/images/foretees.gif\"><BR>");
   out.println("<hr width=\"40%\">");
   out.println("<BR><H2>Access Error</H2><BR>");
   out.println("<BR><BR>Sorry, you must login before attempting to access these features.<BR>");
   out.println("<BR><BR>Please <A HREF=\"/" +rev+ "/servlet/Logout\">login</A>");
   out.println("</CENTER></BODY></HTML>");

 }

}
