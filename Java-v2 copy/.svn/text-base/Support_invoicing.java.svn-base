/***************************************************************************************     
 *   Support_invoicing:  This servlet will allow the sales staff to create an invoice
 *                       for each club.
 *
 *                   This is used to gather the number of billable members for a club.
 *                   The billing information is then used to create an invoice. 
 *                   The full life cycle of an invoice is processed here along with display of archived invoices.
 *
 *
 *   called by:  Sales/Support main menu and self
 *
 *   created: 11/16/2011   Tim K.
 *
 *   last updated:
 *
 *      6/16/12  BP - Add menu to servlet so we can allow user to switch clubs.  Other misc changes.
 *      3/15/12  Advance next due date one year when invoice mark paid. Initial ForeTees state to current in setup.
 *      3/14/12  Implement default setting values. Add displayBillingInAgain() for rerunning billing input.
 *               Implement "Setup Fee" processing. Implement frequency of billing and add createAdditionalInvoices().
 *      3/09/12  Implement invoice setup processing. Update State and Status page to show new/unpaid invoices.
 *      3/01/12  Update displayError(), update displayInvoices() to display summary and full record, add sendInvoiceEmail().
 *      2/24/12  Remove processing input for multiple clubs (process 1 club at a time). Add check number to invoice record.
 *      2/16/12  Update servlet with the new skin look.
 *     11/16/11  New file contains previous Support_billing.java and additional invoice processing.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import javax.mail.internet.*;
import javax.mail.*;

// foretees imports
import com.foretees.common.getClub;
import com.foretees.common.mTypeArrays;
import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;


public class Support_invoicing extends HttpServlet {
 
       
 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 String sales = "sales";


   //********************************************************
   // Some constants for emails sent within this class
   //********************************************************
   //
   static String host = ProcessConstants.HOST;

   static String port = ProcessConstants.PORT;

   static String efrom = ProcessConstants.EFROM;
   
   //
   //  Golf Invoice Items, Item Descriptions, and Rates (these are in the exact order so they match and ine index can grab all 3)
   //
   String[] line_itemA = {"Annual Membership Fee", "Annual Per Member Fee", "Annual Corp Membership Fee", "Bi-Annual Per Member Fee", "Monthly Fee", "Quarterly Fee", "Setup Fee"};

   String[] descriptionA = {"", 
                            "", 
                            "", 
                            "", 
                            "", 
                            "", 
                            "Initial Setup of the ForeTees Online Reservation System."};
   
   String[] descriptionA2 = {"Membership Fee for the ForeTees Online Reservation System for the period of ", 
                            "Per Member Fee for the ForeTees Online Reservation System for the period of ", 
                            "Membership Fee for the ForeTees Online Reservation System for the period of ", 
                            "Bi-Annual Per Member Fee for the ForeTees Online Reservation System for the period of ", 
                            "Monthly Membership Fee for the ForeTees Online Reservation System for the month of ", 
                            "Quarterly Membership Fee for the ForeTees Online Reservation System for the period of "};


   int[] ratesA = {20, 10, 40, 10, 10, 10, 2900};

   
   


 //*********************************************************************************
 // Process the request from sales menu and invoice summary page.
 //*********************************************************************************

 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
           
   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();
        
   Connection con = null;                  // init DB objects
     
   HttpSession session = null; 
   String itype = "";                      // invoice type

   
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
   
   
   //
   //  See if user requested that we switch the club to process
   //
   if (req.getParameter("clubswitch") != null && req.getParameter("clubswitch").equals("1") && req.getParameter("club") != null) {

       //
       //  Request is to switch clubs - switch the db (TPC or Demo sites)
       //
       String newClub = req.getParameter("club");

       //
       //  release the old connection
       //
       ConnHolder holder = (ConnHolder) session.getAttribute("connect");

       if (holder != null) {

           con = holder.getConn();      // get the connection for previous club
       }


       if (con != null) {

           // abandon any unfinished transactions
           try { con.rollback(); }
           catch (Exception ignore) {}

           // close/release the connection
           try { con.close(); }
           catch (Exception ignore) {}
       }

       
       //
       //  Connect to the new club
       //
       try {
          con = dbConn.Connect(newClub);           // get connection to this club's db
       }
       catch (Exception ignore) {}

       holder = new ConnHolder(con);

       session.setAttribute("club", newClub);
       session.setAttribute("connect", holder);
       
       out.println("<HTML><HEAD><Title>Switching Sites</Title>");
       out.println("<meta http-equiv=\"Refresh\" content=\"0; url=/" +rev+ "/servlet/Support_invoicing?home=yes\">");
       out.println("</HEAD>");
       out.println("<BODY><CENTER><BR>");
       out.println("<BR><H2>Switching Sites</H2><BR>");
       out.println("<a href=\"/" +rev+ "/servlet/Support_invoicing?home=yes\">Continue</a><br>");
       out.println("</CENTER></BODY></HTML>");
       out.close();
       return;
   }
   
   
   //  See if we have a connection
   
   ConnHolder holder = (ConnHolder) session.getAttribute("connect");

   if (holder != null) {

        con = holder.getConn();      // get the connection
   }
    
   if (con == null) {

       try {
          con = dbConn.Connect(club);

       }
       catch (Exception exc) {

          // Error connecting to db....

          addDocTypeHdr(out);
          out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
          out.println("<head><title>DB Connection Error Received</title>");
          out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
          out.println("</head>");


          out.println("<body><h3>DB Connection Error</h3>");
          out.println("<br /><br />Unable to connect to the DB.");
          out.println("<br />Exception: "+ exc.getMessage());
          if (user.equals( support )) {                                              // if support (do not allow for sales)
             out.println("<br /><br /> <a href=\"/" +rev+ "/Servlet/Support_main\">Return</a>.");
          } else {
             out.println("<br /><br /> <a href=\"/" +rev+ "/sales_main.htm\">Return</a>.");
          }
          out.println("</body></html>");
          return;
       }
       
       holder = new ConnHolder(con);
       session.setAttribute("connect", holder);     //  save for this session       
   }
    

   String sdate = String.valueOf(new java.util.Date());       // get date and time string
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   
   
   if (req.getParameter("createInvSubmit") != null) {         // if Create New Invoice
              
       displayBillingIn(con, clubName, out, user);
       return;
   }

   
   //  Process Home page if user just entered Invoice processing or returning to Home Page
   
   if (req.getParameter("home") != null || req.getParameter("itype") == null) {

       //  display the Home Page and allow user to switch clubs
       
       addDocTypeHdr(out);
       
       addHeadTitle(out, "Invoicing Home Page", true);  
       
       addBodyStartPage(out, "Menu", false);
       
       out.println("<hr class=\"menu\" />");
       out.println("<p class=\"menu\">Invoice Main Menu</p>");

       out.println("<p><br />");    //  add drop down list of all clubs - for switch
       
       out.println("<form action=\"/" +rev+ "/servlet/Support_invoicing\" method=\"get\" name=\"cform\">");
       out.println("<input type=\"hidden\" name=\"clubswitch\" value=\"1\">");

       out.println("<p align=center>You are currently connected to: &nbsp;&nbsp;<b>" + clubName + "</b><br><br><br>");
       out.println("To switch sites, select the desired club name below.<br>");
       out.println("<br><b>Club:</b>&nbsp;&nbsp;");
      
       out.println("<select size=\"1\" name=\"club\" onChange=\"document.cform.submit()\">");

       String clubname = "";
       String fullname = "";
      
       Connection con2 = null;

       try {
          con2 = dbConn.Connect(rev);           // get connection to the Vx db
          
          //
          //  Get the club names for each club
          //
          //
          //  Get the club names for each TPC club
          //
          PreparedStatement pstmt = con2.prepareStatement("SELECT clubname, fullname FROM clubs WHERE inactive=0 ORDER BY fullname");

          pstmt.clearParameters();
          ResultSet rs = pstmt.executeQuery();

          while (rs.next()) {

             clubname = rs.getString("clubname");             // get the club's site name             
             fullname = rs.getString("fullname");             // get the club's full name
             
             if (clubname.equals(club)) {
                 out.println("<option selected value=\"" + clubname + "\">" + fullname + " (" +clubname+ ")</option>");    
             } else {
                 out.println("<option value=\"" + clubname + "\">" + fullname + " (" +clubname+ ")</option>"); 
             }
          }  
          pstmt.close();
          
       }
       catch (Exception e) {
        
       } finally {

            try { con2.close(); }
            catch (SQLException ignored) {}
       }          
       
       out.println("</select><br /></p></form>");
       
          out.println("<table class=\"main_menu\">");
          out.println("<thead>");
            out.println("<tr>");
              out.println("<th>Invoice Menu</th>");
            out.println("</tr>");
          out.println("</thead>");
          out.println("<tbody>");
            out.println("<tr>");
              out.println("<td><a href=\"/" +rev+ "/servlet/Support_invoicing?createInvSubmit=yes\">");
                 out.println("Create New Invoice</a></td>");
            out.println("</tr>");
            out.println("<tr>");
              out.println("<td><a href=\"/" +rev+ "/servlet/Support_invoicing?itype=process\">");
                 out.println("Current Invoices</a></td>");
            out.println("</tr>");
            out.println("<tr>");
              out.println("<td><a href=\"/" +rev+ "/servlet/Support_invoicing?itype=display\">");
                 out.println("Invoice History</a></td>");
            out.println("</tr>");
            out.println("<tr>");
              out.println("<td><a href=\"/" +rev+ "/servlet/Support_invoicing?itype=setup\">");
                 out.println("Customer Profile</a></td>");
            out.println("</tr>");
          out.println("</tbody>");
          out.println("</table>");   //  main_menu 

          out.println("<br />");
          out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/sales_main.htm\">Return to Sales Menu</a></p>");
          out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
        out.println("</div>");
        out.println("</div>");
       out.println("</body>");
       out.println("</html>");
       out.close();
   
   } else {
   
       // Get and parse the invoicing choice.
       if (req.getParameter("itype") != null) {

          itype = req.getParameter("itype");             // get itype parameter
       }

       
       if (itype.equals("process")){
           
           displayInvoiceProcessState(con, out, clubName, sdate);   // display invoice processing: state and status page
       
       } else if (itype.equals("setup")){
           
           displayInvoiceSetup(con, out, clubName);      // show the display invoice setup page
       
       } else if (itype.equals("display")){
           
           displayInvoices(con, out, clubName, 1);       // show the display invoices page
           
       } else {     
           
           displayBillingIn(con, clubName, out, user);   // default to billing input page.
       }
   }
   
 }        // end of doGet
   
   
 //*********************************************************************************
 // Display the create invoice (billing) input page.
 //*********************************************************************************
 private void displayBillingIn(Connection con, String clubName, PrintWriter out, String user)
 {
     

   mTypeArrays mArrays = new mTypeArrays();     // setup for mship and mtype arrays

   String roundsPer = "";
   
   int maxMems = mArrays.MAX_Mems;
   int maxMships = mArrays.MAX_Mships;
   invoice.billingRateTyp billType = invoice.billingRateTyp.None;
   int i = 0;
   String memberBillCheck = "checked=\"checked\""; 
   String mShipBillCheck = ""; 
     
  
   //
   //  Get the mtype and mship types from club5 
   //
   try {

      mArrays = getClub.getMtypes(mArrays, con);         // skip it all if this fails

   }
   catch (Exception exc) {
   }

   //
   // Get the default billing type setting.
   //
   billType = getBillRate(con);
   if (billType == invoice.billingRateTyp.None) {
       
       memberBillCheck = "";       
   }
   else if (billType == invoice.billingRateTyp.MShip) {
       
       memberBillCheck = "";       
       mShipBillCheck = "checked=\"checked\"";  
   }


   //
   //  Now present a form to display and gather the options
   //   
   addDocTypeHdr(out);
   addHeadTitle(out, "Create Invoice Processing (Support Billing)", true);
   addBodyStartPage(out, "Create Invoice", true);
         
   out.println("      <b>Instructions:</b><br />");
   out.println("      <br />Indicate which Membership and Member Types are eligible for billing.<br />");
   out.println("    </div>");
   
   out.println("    <h3 class=\"support_ctr\">" +clubName+ "</h3>");
   
   out.println("    <h3 class=\"support_ctr\">Set Club Billing Options</h3>");

      out.println("    <form action=\"Support_invoicing\" method=\"post\">");
      out.println("    <table id=\"billing_input_data\">");
      out.println("    <tbody>");
      out.println("      <tr class=\"top\">");
      out.println("        <td>Membership Types: (Rounds Allowed)</td>");
      out.println("        <td>Member Types:</td>");
      
      out.println("      </tr><tr>");
      out.println("        <td>");
        
      for (i=0; i < maxMships; i++) {

         if (!mArrays.mship[i].equals( "" )) {
            
            roundsPer = getRoundsPer(mArrays.mship[i], con);      // get the number of rounds per 'period' info for this mship type

            out.println("      <input class=\"invoice\" type=\"checkbox\" name=\"mship" +i+ "\" value=\"1\" />");
            out.println("        " +mArrays.mship[i]+ "&nbsp;&nbsp;&nbsp; " +roundsPer+ " <br />");
         }
      }
      
      out.println("        </td>");
      out.println("        <td>");

      for (i=0; i < maxMems; i++) {

         if (!mArrays.mem[i].equals( "" )) {

            out.println("      <input class=\"invoice\" type=\"checkbox\" name=\"mtype" +i+ "\" value=\"1\" />");
            out.println("        " +mArrays.mem[i]+ "<br />");
         }
      }
      
      out.println("        </td>");
      out.println("      </tr><tr>");
      out.println("        <td>");
      out.println("        <br />Select Billing Type:</div>");
      out.println("        </td>");
      out.println("        <td>");
           
      out.println("      <input class=\"invoice\" type=\"radio\" name=\"rate\" value=\"peradult\" " +memberBillCheck+ " />");
      out.println("        $10 Per Adult&nbsp;&nbsp;<br />");
      out.println("      <input class=\"invoice\" type=\"radio\" name=\"rate\" value=\"permship\" " +mShipBillCheck+ " />");
      out.println("        $20 Per Membership<br />");
      out.println("      <input class=\"invoice\" type=\"radio\" name=\"rate\" value=\"setup\" />");
      out.println("        Setup Fee");

      out.println("      </td>");
      out.println("      </tr>");

      out.println("    </tbody>");
      out.println("    </table>    <!-- billing_input_data -->");

      out.println("    <div class=\"note_instructions\">");
      out.println("      <strong>NOTE:</strong> We only charge for Membership Types that are allowed to golf ");
      out.println("      <strong>More Than 1 Time per Month</strong>.<br /><br />");
      out.println("      We do not charge for Employees, so skip any types like Staff, Employee, etc.<br /><br/>");
      out.println("    </div>");
      out.println("    <p class=\"invoice\">");
      out.println("      <input type=\"submit\" name=\"billingSubmit\" value=\"Submit - Web Page\" />");
      out.println("      <input type=\"submit\" name=\"excel\" value=\"Submit - Excel\" />");
      out.println("    </p>");
      out.println("    </form>");
      out.println("    <p class=\"general_button\">");
     
      if (user.equals( support )) {                                              // if support (do not allow for sales)
         out.println("    <br /> <a href=\"/" +rev+ "/servlet/Support_main\">Return - Cancel</a>");
      } else {
         out.println("    <br /> <a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a>");
      }
      
      out.println("    </p>");
      
   out.println("  </div>");
   out.println("  </div>    <!-- wrapper  -->");

   out.println("</body></html>");
   out.close();

 }        // end of displayBillingIn


 /*     // no longer needed ???
 //*********************************************************************************
 // Display billing input page again.
 //*********************************************************************************
 private void displayBillingInAgain(PrintWriter out, String clubName, Connection con, String user) 
 {

   invoice_data invData = new invoice_data();

   
   // Update db with new values.
   // Get invoice data.
   try {
   
       // Set the ForeTees state for this invoice to Save Pending which will allow this invoice to be overwritten.
       //
       invData.setForeTeesState(invoice_data.foreTeesStType.CurrentSavePending);
       invData.updateFTstateToDb(con);
   } 
   catch (Exception exc) {

     addDocTypeHdr(out);
     out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
     out.println("<head><title>Update Billing Invoice</title>");
     out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
     out.println("</head>");
     out.println("<body class=\"serifFont\">");

     out.println("<div id=\"wrapper\">");

     out.println("    <br /><br />displayBillingInAgain, Exception: "+ exc.getMessage());
     out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
     out.println("</div>");
     out.println("</body></html>");
     return;
   }
   
   displayBillingIn(con, clubName, out, user);


 }   // displayBillingInAgain()
  */
 
 
 //*********************************************************************************
 // Process the form request from above
 //*********************************************************************************

 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   resp.setContentType("text/html");
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   boolean excel = false;
   String optionPick = "";
   
   HttpSession session = null;


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = (String)session.getAttribute("user");   // get username
   
   if (user == null) {
      
      user = (String) session.getAttribute("ext-user");       // get this username when external login (proshop user only)
   }

   if (!user.equals( support ) && !user.startsWith( sales ) && !user.startsWith( "proshop" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   String club = (String)session.getAttribute("club");   // get club name

   //  See if we have a connection
   
   ConnHolder holder = (ConnHolder) session.getAttribute("connect");

   if (holder != null) {

        con = holder.getConn();      // get the connection
   }
    
   if (con == null) {

       try {
          con = dbConn.Connect(club);

       }
       catch (Exception exc) {

          // Error connecting to db....

          addDocTypeHdr(out);
          out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");

          out.println("<head><title>DB Connection Error Received</title>");
          out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
          out.println("</head>");
          out.println("<body class=\"serifFont\">");
          out.println("<div id=\"wrapper\">");
          out.println("<h3>DB Connection Error</h3>");
          out.println("<br /><br />Unable to connect to the DB.");
          out.println("<br />Exception: "+ exc.getMessage());
          if (user.equals( support )) {                                              // if support (do not allow for sales)
             out.println("<br /><br /> <a href=\"/" +rev+ "/servlet/Support_main\">Return</a>.");
          } else {
             out.println("<br /><br /> <a href=\"/" +rev+ "/sales_main.htm\">Return</a>.");
          }
          out.println("</div>    <!-- wrapper -->");
          out.println("</body></html>");
          return;
       }
   }
   
   //
   //  Get the parms passed
   //      
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
   if (req.getParameter("excel") != null) {
      
      excel = true;
   }
   
   if (req.getParameter("createInvSubmit") != null) {
              
       displayBillingIn(con, clubName, out, user);
       return;
   }

   if (req.getParameter("chgCurrentInvSubmit") != null) {

       //
       // Get already created (unpaid) invoice for processing.
       //
       invoice inv = new invoice();
       invoice_data invData = new invoice_data();
       String idStr = "";
       int idNum = 0;

       idStr = req.getParameter("invoiceId");  

       if (idStr.length() > 0) {

           idNum = Integer.parseInt(idStr);
       }

       try {
           invData.updateCurrInvoiceToDb(con, idNum);
           inv.retrieve(con);

           // update the ForeTees state for this invoice.
           invData.updState(invoice_data.eventType.SaveInv, con);

           if ((inv.getStatus() == invoice.statusType.Pending) ||
               (inv.getStatus() == invoice.statusType.AckReceipt) || (inv.getStatus() == invoice.statusType.AckPayment) ){

               invData.updState(invoice_data.eventType.NotifyClub, con);
           }


           invData.currentState(con, false);
           invoice_data.foreTeesStType ftStateVal = invData.getForeTeesState();

           displayActiveInvoice(out, clubName, invData, inv, "Invoice Processing");
           return;

       } catch (Exception exc) {

           // handle exception
       }

       return;
   }
            
   if (req.getParameter("billingSubmit") != null) {
       
       displayBillingOut(req, resp, out, clubName, con, excel);
       return;
   }

   
   if (req.getParameter("billsaveSubmit") != null) {
       
       String setupFee = req.getParameter("setupfee");
       String billOverride = req.getParameter("bloverride");
       String invNumberStr = req.getParameter("invnumber");
       
       String item = "";
       String description = "";
       
       int setupFeeAmount = 0;
       int billOverNum = 0;
       int invNum = 0;
       int itemIndex = 0;
       
       if (req.getParameter("line_item") != null) {    // if item provided (index into item array)

           itemIndex = Integer.parseInt(req.getParameter("line_item"));
           
           item = line_itemA[itemIndex];        // get item text
       }
       
       if (req.getParameter("description") != null) {    // if description provided

           description = req.getParameter("description");
       }
       
       
       // Check if setup fee save.
       if (setupFee != null) {
           
           if (setupFee.length() > 0) {
               setupFeeAmount = Integer.parseInt(setupFee);
           }
           
           optionPick = "setupfee";    
       }
       else {
           // get billing override value.
           //
           if (billOverride.length() > 0) {

              billOverNum = Integer.parseInt(billOverride);
           }

           if (billOverNum < 0) billOverNum = 0;
           
           /*
           optionPick = req.getParameter("billInv");  
           
           if (optionPick.equals("updbillinput")) {
               
               displayBillingInAgain(out, clubName, con, user);
               return;
           }
            */
       }
       
       if (invNumberStr.length() > 0)
           invNum = Integer.parseInt(invNumberStr);
       
       
       saveInvoiceResults(out, clubName, con, invNum, billOverNum, setupFeeAmount, optionPick, item, description);    // show save invoice results.
       return;
   }
   
   if (req.getParameter("activeInvSubmit") != null) {
       
       if (req.getParameter("savedInv") != null) {

           optionPick = req.getParameter("savedInv");

           if (optionPick.equals("updInvoice")) {

             displayBillingOut2(req, resp, out, clubName, con, excel);
             
           } else if (optionPick.equals("notifyClub")) {
              
             promptEmail(out, clubName, club, con);  
              
           } else {
              
             activeInvoiceProcessing(req, out, clubName, club, con, optionPick);  
           }
           
       } else {
           
           //  No option selected - error
           String errMsg = "You MUST select an Option for the Invoice.";
           inputError(out, errMsg);           
       }
       return;
   }

   if (req.getParameter("showClubInvSubmit") != null) {
       
       // 
       // Two ways to get to this point. (1) Active invoice, Show Club Invoice button, this will be current
       // invoice, (2) Display history, show the invoice selected from that page.
       invoice inv = new invoice();
       boolean current = false;
       String invNumberStr = req.getParameter("invoiceNumber");
       String idStr = "";
       int idNum = 0;
       int invNum = 0;
       
       if (invNumberStr.length() > 0) {
           
          invNum = Integer.parseInt(invNumberStr);
       }
       
       if (invNum == 0) {
           current = true;     // this was from the active invoice page.
       }
       else {                  // from invoice display, get invoice ID.
           idStr = req.getParameter("invoiceId");  
           
           if (idStr.length() > 0) {
               
               idNum = Integer.parseInt(idStr);
           }
       }
       
       // Get invoice data.
       //
       try {

         if (current) {
             inv.retrieve(con);
         }
         else {
             inv.retrieve(con, idNum);
         }
         
         inv.displayClubInvoice(con, out, rev);
       }
       catch (Exception exc) {

         addDocTypeHdr(out);
         out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
         out.println("<head><title>Update Billing Invoice</title>");
         out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
         out.println("</head>");
         out.println("<body class=\"serifFont\">");

         out.println("<div id=\"wrapper\">");

         out.println("    <br /><br />doPost, Exception: "+ exc.getMessage());
         out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
         out.println("</div>");
         out.println("</body></html>");
         return;
       }       
       return;
   }
   
   if (req.getParameter("displayInvSubmit") != null) {
       
       String selection = req.getParameter("invdisplay");
       int displayOpt = 0;
       displayOpt = Integer.parseInt(selection);

       // check for valid range.
       if ((displayOpt >= 1) && (displayOpt <= 3) ) {
           
           displayInvoices(con, out, clubName, displayOpt);
       }
       else {

           displayError(req, resp, out, "doPost()", "Display processing option was not found");
       }
       
       return;       
   }
   
   if (req.getParameter("setupInvSubmit") != null) {
                 
       displaySetupResults(req, out, clubName, con, false);
      
       return;
   }
   
   if (req.getParameter("setupSaveSubmit") != null) {
                 
       displaySetupSaveResults(req, out, clubName, con);      // update the profile table
  
       return;
   }

   //
   // Option to process not found, display error page.
   //
   displayError(req, resp, out, "doPost()", "Main processing option was not found");
        
 } // doPost()

 
 //*********************************************************************************
 // Display the billing results output page.
 //*********************************************************************************
 private void displayBillingOut(HttpServletRequest req, HttpServletResponse resp,
         PrintWriter out, String clubName, Connection con, boolean excel) 
         throws ServletException, IOException
 {

   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   ResultSet rs = null;
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


   String mtype = "";
   String mship = "";
   String mshipName = "";
   String rate = "";
     
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

   if (req.getParameter("rate") != null) {
      
      rate = req.getParameter("rate");
   }
   
   // If setup bill then display setup page.
   //
   if (rate.equals("setup")){
       
       displayBillingOutSetup(req, resp, out, clubName, con, excel);
       return;
   }

   if (!rate.equals("peradult") && !rate.equals("permship")) {
           
   //   out.println(SystemUtils.HeadTitle("Access Error - Redirect"));
      addDocTypeHdr(out);
      out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
      out.println("<head><title>Access Error - Redirect</title>");
      out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
      out.println("</head>");
      out.println("<body class=\"serifFont\">");
      out.println("<div id=\"wrapper\">");
      out.println("<img src=\"/" +rev+ "/images/foretees.gif\" /><br />");
      out.println("<hr class=\"menu\">");
      out.println("<br /><h2>Input Error</h2><br />");
      out.println("<br /><br />Sorry, you must select a Billing Type.<br /><br />");
      out.println("<form method=\"get\" action=\"javascript:history.back(1)\">");
      out.println("<input type=\"submit\" value=\"Return\" style=\"text-decoration:underline; background-color:#8B8970;\" />");
      out.println("</form>");
      out.println("</div>");
      out.println("</body></html>");
      return;
   }
   
   
   
   //
   //  Output results in EXCEL format
   //
   if (excel == true) {
      
      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
   }

   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Create Invoice Results", false);
   
      out.println("<script language='JavaScript'>");             // Move the Description into textarea
      out.println("<!--");
      out.println("function updateDesc(index) {");
      out.println("var desc = '';");
      out.println("if (index == '0') {");               
      out.println("  desc = document.invform.desc0.value;");
      out.println("} else if (index == '1') {");               
      out.println("  desc = document.invform.desc1.value;");
      out.println("} else if (index == '2') {");               
      out.println("  desc = document.invform.desc2.value;");
      out.println("} else if (index == '3') {");               
      out.println("  desc = document.invform.desc3.value;");
      out.println("} else if (index == '4') {");               
      out.println("  desc = document.invform.desc4.value;");
      out.println("} else if (index == '5') {");               
      out.println("  desc = document.invform.desc5.value;");
      out.println("} else {");               
      out.println("  desc = document.invform.desc6.value;");
      out.println("}");               
      out.println("document.invform.description.value = desc;");   // put description in text area
      out.println("}");                  // end of script function
      out.println("// -->");
      out.println("</script>");          // End of script

   out.println("</head>");          // End of Head
     
   addBodyStartPage(out, "Create Invoice Results", false);

   out.println("    <h3 class=\"support_ctr\">Billing Information For " +clubName+ "</h3>");
   out.println("    <p class=\"invoice\"><b>Date: </b> " +sdate);
   out.println("    </p><br />");

   out.println("    <table id=\"billing_results_data\">");
   out.println("    <tbody>");
      out.println("      <tr class=\"col_desc\">");
      out.println("        <td>Membership Type</td>");
      out.println("        <td>Member Type</td>");
      out.println("        <td class=\"counts\">Total</td>");
      out.println("        <td class=\"counts\">Inact</td>");
      out.println("        <td class=\"counts\">Excluded</td>");
      out.println("        <td class=\"counts\">Billable</td>");
      out.println("      </tr>");

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
        
         mshipTotal = 0;       // reset total counts for mship type
         mshipInact = 0;
         mshipExclude = 0;
         mshipBill = 0;

         
         if (rate.equals("peradult")) {    
            
            // Per Adult fees - must count each member type selected for this mship
           
            out.println("      <tr>");     // one row for mship type
            out.println("        <td>" +mship+ "</td>");
            out.println("        <td></td>");
            out.println("        <td></td>");
            out.println("        <td></td>");
            out.println("        <td></td>");
            out.println("        <td></td>");
            out.println("      </tr>");

            i2 = 0;
            mtype = mtypeA[i2];                             // get first mtype
            i2++;
            
            mshipName = " ";                            // put space in mship column for each mtype row

            while (i2 < Max && !mtype.equals( "" )) {       // do each requested mtype

               pstmt1 = con.prepareStatement (
                        "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ?");

               pstmt1.clearParameters();         
               pstmt1.setString(1, mship);            
               pstmt1.setString(2, mtype);
               rs = pstmt1.executeQuery();            // execute the prepared stmt

               if (rs.next()) {

                  mtypeTotal = rs.getInt("COUNT(*)");
               }
               pstmt1.close();

               pstmt1 = con.prepareStatement (
                        "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ? AND inact = 1");

               pstmt1.clearParameters();
               pstmt1.setString(1, mship);
               pstmt1.setString(2, mtype);
               rs = pstmt1.executeQuery();            // execute the prepared stmt

               if (rs.next()) {

                  mtypeInact = rs.getInt("COUNT(*)");
               }
               pstmt1.close();

               pstmt1 = con.prepareStatement (
                        "SELECT COUNT(*) FROM member2b WHERE m_ship = ? AND m_type = ? AND billable = 0");

               pstmt1.clearParameters();
               pstmt1.setString(1, mship);
               pstmt1.setString(2, mtype);
               rs = pstmt1.executeQuery();            // execute the prepared stmt

               if (rs.next()) {

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

               if (rs.next()) {

                  noBill = rs.getInt("COUNT(*)");
               }
               pstmt1.close();


               mtypeBill = mtypeTotal - noBill;         // get billable count

               out.println("      <tr>");     // one row per mtype
               out.println("        <td></td>");
               out.println("        <td>" + mtype + "</td>");
               out.println("        <td class=\"counts\">" +mtypeTotal+ "</td>");
               out.println("        <td class=\"counts\">" +mtypeInact+ "</td>");
               out.println("        <td class=\"counts\">" +mtypeExclude+ "</td>");
               out.println("        <td class=\"counts\">" + mtypeBill+ "</td>");
               out.println("      </tr>");

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

         } else {
            
            //  Per Membership Type fees - count one per family
            
            String mNum = "";
            String lastNum = "";
            int billable = 0;
            int inact = 0;
            noBill = 0;
            
            mshipName = mship;     
            
            pstmt1 = con.prepareStatement (
                     "SELECT memNum, billable, inact FROM member2b WHERE m_ship = ? ORDER BY memNum");

            pstmt1.clearParameters();         
            pstmt1.setString(1, mship);            
            rs = pstmt1.executeQuery();            // execute the prepared stmt

            while (rs.next()) {

               mNum = rs.getString("memNum");
               billable = rs.getInt("billable");
               inact = rs.getInt("inact");
               
               if (mNum.equals("") || !mNum.equals( lastNum )) {       // if new mNum or no member number specified
               
                  mshipTotal++;
                  
                  if (inact == 1) mshipInact++;
                  
                  if (billable == 0) mshipExclude++;
                  
                  if (inact == 1 || billable == 0) noBill++;
                  
                  lastNum = mNum;
               }
            }
            pstmt1.close();

            mshipBill = mshipTotal - noBill;         // get billable count
         }
         
         out.println("      <tr>");     // row for mship totals 
         out.println("        <td>" + mshipName + "</td>");
         out.println("        <td class=\"counts\">Total for Mship:</td>");
         out.println("        <td class=\"counts\">" + mshipTotal + "</td>");
         out.println("        <td class=\"counts\">" + mshipInact + "</td>");
         out.println("        <td class=\"counts\">" + mshipExclude + "</td>");
         out.println("        <td class=\"counts\">" + mshipBill + "</td>");
         out.println("      </tr>");

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
      out.println("      <tr class=\"total\">");     // Grand Totals
      out.println("        <td>GRAND TOTALS:</td>");
      out.println("        <td></td>");
      out.println("        <td class=\"counts\">" + grandTotal + "</td>");
      out.println("        <td class=\"counts\">" + grandInact + "</td>");
      out.println("        <td class=\"counts\">" + grandExclude + "</td>");
      out.println("        <td class=\"counts\">" + grandBill + "</td>");
      out.println("      </tr>");
      out.println("    </tbody>");
      out.println("    </table>    <!-- billing_results_data -->");

      out.println("    <div class=\"note_instructions\">");

      if (rate.equals("permship")) {    
            
        out.println("      <b>Notice:</b> Make sure this club has the same Member Number for the family and all Member Numbers are specified in roster!!");
        out.println("      <br /><br />");
      }
      
      out.println("      <b>Notice:</b> Inactive members are those that are not billable and/or have been marked as inactive by the admin or via roster sync.");
      out.println("      <br />Excluded members are those that are not billable and have been marked as excluded by the admin. This is for clubs that have roster sync");
      out.println("      where the inact flag can be cleared by the roster sync process when members are included in the RS file.<br />");
      out.println("    </div>    <!-- note_instructions -->");

   }
   catch (Exception exc) {

      out.println("    <p class=\"invoice\">");
      out.println("    <br /><br />Exception: "+ exc.getMessage());
      out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
      out.println("    </p>");
      out.println("  </div>");
      out.println("  </div>    <!-- wrapper -->");
      out.println("</body></html>");
      return;
   }

   
   //
   //   Save processing for the billing results.
   //
   invoice.billingRateTyp billRate = invoice.billingRateTyp.MShip;
   String freqYearCheck = "checked=\"checked\"";
   String freqBiannualCheck = "";
   String freqQuarterCheck = "";
   String description = "";

   if (rate.equals("peradult"))
       billRate = invoice.billingRateTyp.Member;
     

   invoice inv = new invoice();
   invoice_data invData = new invoice_data();
   int invNum = 0;
   
   //
   //   Get due date and plug in period values for invoice to use below
   //
   try {
       
       invData.currentState(con, false);      // get the next due date and set it up in invoice
       
   } catch (Exception exc) {
   }
   
   Calendar cal = invData.getNextDueDateCal();       // get the next due date Calendar that was just created
   
   int rc = inv.setDueDateCal(cal);          // set the new due date
   
   String smonth = inv.getStartMonth();
   String syear = String.valueOf(inv.getDueYear());
   String emonth = inv.getEndMonth();
   String eyear = String.valueOf(inv.getDueYear() + 1);
   
   //
   //  Plug the dates into the descriptions
   //
   descriptionA[0] = descriptionA2[0] + smonth+ " " +syear+ " through " +emonth+ " " +eyear+ ".";
   descriptionA[1] = descriptionA2[1] + smonth+ " " +syear+ " through " +emonth+ " " +eyear+ ".";
   descriptionA[2] = descriptionA2[2] + smonth+ " " +syear+ " through " +emonth+ " " +eyear+ ".";
   descriptionA[3] = descriptionA2[3] + smonth+ " " +syear+ " through " +emonth+ " " +eyear+ ".";
   descriptionA[4] = descriptionA2[4] + smonth+ " " +syear+ ".";
   descriptionA[5] = descriptionA2[5] + smonth+ " " +syear+ " through " +emonth+ " " +eyear+ ".";
   
   
   
   try {
       
     invData.currentState(con, false);
     
     //
     // Check for invoice created by user but not saved.
     // If found get that invoice and overwrite the data with the new billing results.
     //     Note: do not have to update state since the user needs to do a "Save Invoice" to keep for processing.
     //
     if (invData.getForeTeesState() == invoice_data.foreTeesStType.CurrentSavePending) {
         
         inv.retrieve(con);
         inv.updatePending(invNum, true, billRate, grandTotal,
                 grandInact, grandExclude, grandBill, con, true);
     }
     else {
         
         inv.create(invNum, true, billRate, grandTotal,
                 grandInact, grandExclude, grandBill, con, true);

         invData.updState(invoice_data.eventType.CreateInv, con );
     }
     
     // Set frequency of billing default.
     //
     if (invData.getFreq() == invoice_data.frequencyType.Quarterly) {
         
         freqYearCheck = "";
         freqBiannualCheck = "";
         freqQuarterCheck = "checked=\"checked\"";
     }
     else if (invData.getFreq() == invoice_data.frequencyType.Biannually) {
         freqYearCheck = "";
         freqBiannualCheck = "checked=\"checked\"";
         freqQuarterCheck = "";
     }
     
     //
     //  Determine the default item description
     //
    if (rate.equals("permship") && !freqYearCheck.equals("")) {          // if per mship and annual
        description = descriptionA[0];    
    }
    if (rate.equals("peradult") && !freqYearCheck.equals("")) {          // if per member and annual
        description = descriptionA[1];    
    }
    if (rate.equals("peradult") && !freqBiannualCheck.equals("")) {          // if per member and bi-annual
        description = descriptionA[3];    
    }
    if (rate.equals("peradult") && !freqQuarterCheck.equals("")) {          // if per member and quarterly
        description = descriptionA[5];    
    }
     
     
     out.println("    <div class=\"middle_instructions\">");
      
        
     out.println("      <b>Instructions:</b><br /><br />");
     out.println("      Before saving this invoice update the billable count");
     out.println("      if necessary. Enter the invoice accounting number.");
     out.println("      If setup is selected enter the setup amount.");
     out.println("    </div>");
       
     out.println("    <form action=\"Support_invoicing\" method=\"post\" name=\"invform\">");
     out.println("    <table id=\"save_invoice_data\">");
     out.println("    <tbody>");
     out.println("      <tr>");             
     out.println("        <td class=\"bold\">Billable Member Override</td>");
     out.println("        <td>");
     out.println("        <input class=\"invoice2\" type=\"text\" name=\"bloverride\" size=\"10\" ");
     out.println("          value = \"" + grandBill + "\" />");
     out.println("        </td>");
     out.println("      </tr><tr>");
     out.println("        <td class=\"bold\">Invoice Number</td>");
     out.println("        <td>");
     out.println("        <input class=\"invoice2\" type=\"text\" name=\"invnumber\" size=\"10\" ");
     out.println("          value = \"" + invNum + "\" />");
     out.println("        </td>");
     out.println("      </tr><tr>");
     out.println("        <td class=\"bold\">Invoice Frequency</td>");
     out.println("        <td>");
     out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"year\" " +freqYearCheck+ " />");
     out.println("          Yearly<br />");
     out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"biannual\" " +freqBiannualCheck+ " />");
     out.println("          Biannually<br />");
     out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"quarter\" " +freqQuarterCheck+ " />");
     out.println("          Quarterly");
     out.println("        </td>");
     
     out.println("</tr><tr>");
     out.println("<td class=\"bold\">Line Item</td>");
     out.println("<td>");
       out.println("<select size=\"1\" name=\"line_item\" onChange=\"updateDesc(this.form.line_item.value)\">");

             if (rate.equals("permship") && !freqYearCheck.equals("")) {          // if per mship and annual
                 out.println("<option selected value=\"0\">" + line_itemA[0] + "</option>");    
             } else {
                 out.println("<option value=\"0\">" + line_itemA[0] + "</option>"); 
             }
             if (rate.equals("peradult") && !freqYearCheck.equals("")) {          // if per member and annual
                 out.println("<option selected value=\"1\">" + line_itemA[1] + "</option>");    
             } else {
                 out.println("<option value=\"1\">" + line_itemA[1] + "</option>"); 
             }
             out.println("<option value=\"2\">" + line_itemA[2] + "</option>");       // Annual Corp Membership Fee
             if (rate.equals("peradult") && !freqBiannualCheck.equals("")) {          // if per member and bi-annual
                 out.println("<option selected value=\"3\">" + line_itemA[3] + "</option>");    
             } else {
                 out.println("<option value=\"3\">" + line_itemA[3] + "</option>"); 
             }
             out.println("<option value=\"4\">" + line_itemA[4] + "</option>");        // monthly    
             if (rate.equals("peradult") && !freqQuarterCheck.equals("")) {          // if per member and quarterly
                 out.println("<option selected value=\"5\">" + line_itemA[5] + "</option>");    
             } else {
                 out.println("<option value=\"5\">" + line_itemA[5] + "</option>"); 
             }
     out.println("</select></td>");
     
     out.println("</tr><tr>");
     out.println("<td class=\"bold\">Item Description</td>");
     out.println("<td>");
     out.println("<textarea name=\"description\" value=\"\" id=\"description\" cols=\"60\" rows=\"2\">" + description + "</textarea>");
     out.println("</td>");
     
     out.println("</tr>");
     out.println("</tbody>");
     out.println("</table>     <!-- save_invoice_data    -->");
      
     out.println("<p class=\"invoice\"><input class=\"invInputFont\" type=\"submit\" name=\"billsaveSubmit\" value=\"Save Invoice\" /></p>");
     
     //
     //  hidden parms only here to hold the description values so they can be moved into the text box
     //
     out.println("<input type=\"hidden\" name=\"desc0\" value=\"" + descriptionA[0] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc1\" value=\"" + descriptionA[1] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc2\" value=\"" + descriptionA[2] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc3\" value=\"" + descriptionA[3] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc4\" value=\"" + descriptionA[4] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc5\" value=\"" + descriptionA[5] + "\">"); 
     out.println("<input type=\"hidden\" name=\"desc6\" value=\"" + descriptionA[6] + "\">"); 
     out.println("</form>");
   }
   catch (Exception exc) {

      out.println("    <p class=\"general_button\">");
      out.println("      <br /><br />Exception (Save billing results): "+ exc.getMessage());
      out.println("      <br /><br /> <a href=\"Support_invoicing\">Return</a>");
      out.println("    </p>");
      out.println("  </div>");
      out.println("  </div>");
      out.println("</body></html>");
      return;
   }

   //
   //   Finish the page 
   //
   if (excel == false) {
      
      out.println("  <p class=\"general_button\"><a href=\"javascript:history.back(1)\">Return</a></p>");
   }
      
      
   out.println("  </div>");
   out.println("  </div>    <!-- wrapper -->");
   out.println("</body></html>");
   out.close();
     
 }   // displayBillingOut()
 
 //*********************************************************************************
 // Display the "update invoice", billing results, output page.
 //*********************************************************************************
 private void displayBillingOut2(HttpServletRequest req, HttpServletResponse resp,
         PrintWriter out, String clubName, Connection con, boolean excel) 
         throws ServletException, IOException
 {

   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   ResultSet rs = null;
   
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();

   
   // Update db with new values.
   // Get invoice data.
   try {
   
     invData.currentState(con, false);
     inv.retrieve(con);
   } 
   catch (Exception exc) {

     addDocTypeHdr(out);
     out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
     out.println("<head><title>Update Billing Invoice</title>");
     out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
     out.println("</head>");
     out.println("<body class=\"serifFont\">");

     out.println("<div id=\"wrapper\">");

     out.println("    <br /><br />displayBillingOut2, Exception: "+ exc.getMessage());
     out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
     out.println("</div>");
     out.println("</body></html>");
     return;
   }
   
   String sdate = String.valueOf(new java.util.Date());         // get date and time string

   
   //
   //  Output results in EXCEL format
   //
   if (excel == true) {
      
      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
   }

   addDocTypeHdr(out);
   addHeadTitle(out, "Invoice Processing", true);
   addBodyStartPage(out, "Update Invoice", false);
   
   out.println("    <h3 class=\"support_ctr\">Billing Information for " +clubName+ "</h3>");
   out.println("    <p class=\"invoice\"><b>Date: </b> " +sdate);
   out.println("    </p><br />");

   out.println("    <table id=\"billing_results_data\">");
   out.println("    <tbody>");
   out.println("      <tr class=\"col_desc\">");
   out.println("        <td></td>");
   out.println("        <td>Rate</td>");
   out.println("        <td class=\"counts\">Total</td>");
   out.println("        <td class=\"counts\">Inact</td>");
   out.println("        <td class=\"counts\">Excluded</td>");
   out.println("        <td class=\"counts\">Billable</td>");
   out.println("      </tr>");
       
   out.println("      <tr>");     // blank row,contains bill type 
   out.println("        <td></td>");
   out.println("        <td>" + inv.getBillRateStr() + "</td>");
   out.println("        <td></td>");
   out.println("        <td></td>");
   out.println("        <td></td>");
   out.println("        <td></td>");
   out.println("      </tr>");

   //
   //  do row with invoice Totals
   //
   out.println("      <tr class=\"total\">");     // Grand Totals
   out.println("        <td>GRAND TOTALS:</td>");
   out.println("        <td></div>");
   out.println("        <td class=\"counts\">" + inv.getTotalCnt() + "</td>");
   out.println("        <td class=\"counts\">" + inv.getInactCnt() + "</td>");
   out.println("        <td class=\"counts\">" + inv.getExcludedCnt() + "</td>");
   out.println("        <td class=\"counts\">" + inv.getBillableCnt() + "</td>");
   out.println("      </tr>");
   out.println("    </tbody>");
   out.println("    </table>    <!-- billing_results_data -->");
     
   
   //
   //   Save processing for the billing results.
   //
   out.println("    <div class=\"middle_instructions\">");
   out.println("      <b>Instructions:</b><br /><br />This is the update invoice page. ");
   out.println("        The user can modify the billable count and invoice number.<br />");
   out.println("        Save this invoice for changes made.<br />");
   out.println("    </div>");

   out.println("    <form action=\"Support_invoicing\" method=\"post\">");
   out.println("    <table id=\"save_invoice_data\">");
   out.println("    <tbody>");
   out.println("      <tr><td class=\"bold\">Billable Member Override</td>");
   out.println("        <td>");
   out.println("        <input class=\"invoice2\" type=\"text\" name=\"bloverride\" size=\"10\" ");
   out.println("          value = \"" + inv.getBillableCnt() + "\" />");
   out.println("        </td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"bold\">Invoice Number</td>");
   out.println("        <td>");
   out.println("        <input class=\"invoice2\" type=\"text\" name=\"invnumber\" size=\"10\" ");
   out.println("          value = \"" + inv.getInvoiceNumber() + "\" />");
   out.println("        </td>");
   out.println("      </tr>");
   /*
   out.println("      <tr>");
   out.println("        <td>");
   out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"save\" checked=\"checked\" />");
   out.println("          Save Changes<br />");
   out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"updbillinput\" />");
   out.println("          Update Billing Input");
   out.println("        </td>");
   out.println("        <td></td>");
   out.println("      </tr>");
    */
   out.println("      <input type=hidden name=\"billInv\" value=\"save\" />");    // always do save now 6/16/12 BP
   out.println("    </tbody>");
   out.println("    </table>     <!-- save_invoice_data    -->");
      
   out.println("    <p class=\"invoice\"><input class=\"invInputFont\" type=\"submit\" name=\"billsaveSubmit\" value=\"Save Changes\" /></p>");
   out.println("    </form>");

   //
   //   Finish the page 
   //
   if (excel == false) {
      
      out.println("    <p class=\"general_button\"><a href=\"javascript:history.back(1)\">Return</a></p>");
   }   
      
   out.println("  </div>");
   out.println("  </div>    <!-- wrapper -->");
   out.println("</body></html>");
   out.close();
     
 }   // displayBillingOut2()
 
 
 //*********************************************************************************
 // Display the billing results, setup output page.
 //*********************************************************************************
 private void displayBillingOutSetup(HttpServletRequest req, HttpServletResponse resp,
         PrintWriter out, String clubName, Connection con, boolean excel) 
         throws ServletException, IOException
 {

   PreparedStatement pstmt1 = null;
   Statement stmt = null;
   ResultSet rs = null;
    
   
   //
   //   Create a new invoice or use a pending invoice.
   //
   invoice.billingRateTyp billRate = invoice.billingRateTyp.Setup;
     
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();
   int invNum = 0;
      
   try {
     invData.currentState(con, false);
     
     //
     // Check for invoice created by user but not saved.
     // If found get that invoice and overwrite the data with the new billing results.
     //     Note: do not have to update state since the user needs to do a "Save Invoice" to keep for processing.
     //
     if (invData.getForeTeesState() == invoice_data.foreTeesStType.CurrentSavePending) {
         inv.retrieve(con);
         inv.updatePending(invNum, true, billRate, 0, 0, 0, 0, con, true);

     }
     else {
         
         inv.create(invNum, true, billRate, 0, 0, 0, 0, con, true);
         invData.updState(invoice_data.eventType.CreateInv, con );
     }
   }
   catch (Exception exc) {

     addDocTypeHdr(out);
     out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
     out.println("<head><title>Update Billing Invoice</title>");
     out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
     out.println("</head>");
     out.println("<body class=\"serifFont\">");

     out.println("<div id=\"wrapper\">");

     out.println("    <br /><br />displayBillingOutSetup, Exception: "+ exc.getMessage());
     out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
     out.println("</div>");
     out.println("</body></html>");
     return;
   }
   
   String sdate = String.valueOf(new java.util.Date());         // get date and time string

   
   //
   //  Output results in EXCEL format
   //
   if (excel == true) {
      
      resp.setContentType("application/vnd.ms-excel");    // response in Excel Format
   }

   addDocTypeHdr(out);
   addHeadTitle(out, "Invoice Processing", true);
   addBodyStartPage(out, "Create Setup Fee Invoice Results", false);
   
   out.println("    <h3 class=\"support_ctr\">Billing Information for " +clubName+ "</h3>");
   out.println("    <p class=\"invoice\"><b>Date: </b> " +sdate);
   out.println("    </p><br />");
   
   //
   //   Save processing for the billing setup results.
   //
   out.println("    <div class=\"middle_instructions\">");
   out.println("      <b>Instructions:</b><br /><br />");
   out.println("        Enter the setup fee for this club. Enter the invoice number.<br />");
   out.println("    </div>");
   out.println("    <div class=\"note_instructions\">");
   out.println("              <b>Notice:</b> The Setup Fee is a one time charge for a club. <br />");
   out.println("    </div>");

   out.println("    <form action=\"Support_invoicing\" method=\"post\">");
   out.println("    <table id=\"save_invoice_data\">");
   out.println("    <tbody>");
   out.println("      <tr><td class=\"bold\">Setup Fee Amount</td>");
   out.println("        <td> $ ");
   out.println("        <input class=\"invoice2\" type=\"text\" name=\"setupfee\" size=\"8\" ");
   out.println("          value = \"0\" />");
   out.println("        </td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"bold\">Invoice Number</td>");
   out.println("        <td>");
   out.println("        <input class=\"invoice2\" type=\"text\" name=\"invnumber\" size=\"10\" ");
   out.println("          value = \"0\" />");
   out.println("        </td>");
   out.println("      </tr>");
   /*
   out.println("      <tr>");
   out.println("        <td>");
   out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"save\" checked=\"checked\" />");
   out.println("          Save Changes<br />");
   out.println("        <input class=\"invoice\" type=\"radio\" name=\"billInv\" value=\"updbillinput\" />");
   out.println("          Update Billing Input");
   out.println("        </td>");
   out.println("        <td></td>");
   out.println("      </tr>");
    */
   out.println("      <input type=hidden name=\"billInv\" value=\"save\" />");    // always do save now 6/16/12 BP
   out.println("    </tbody>");
   out.println("    </table>     <!-- save_invoice_data    -->");
      
   out.println("    <p class=\"invoice\"><input class=\"invInputFont\" type=\"submit\" name=\"billsaveSubmit\" value=\"Save Changes\" /></p>");
   out.println("    </form>");

   //
   //   Finish the page 
   //
   if (excel == false) {
      
      out.println("    <p class=\"general_button\"><a href=\"javascript:history.back(1)\">Return</a></p>");
   }   
      
   out.println("  </div>");
   out.println("  </div>    <!-- wrapper -->");
   out.println("</body></html>");
   out.close();

 }   // displayBillingOutSetup()
 

 //
 //  Invoice processing - state and status page.
 //
 private void displayInvoiceProcessState(Connection con, PrintWriter out, String clubName, String sdate) {
     
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();
   int idNum = 0;
   int invoiceNumber = 0;
   int rowNum = 0;
   
   
   // 
   // Get current state and status of invoicing.
   //
   try {
       inv.retrieve(con);
       idNum = inv.getId();
       invoiceNumber = inv.getInvoiceNumber();
       invData.currentState(con, true);
       
   } catch (Exception exc) {
       
   } finally {
       
       try { rs.close(); }
       catch (Exception ignore) {}
       
       try { pstmt.close(); }
       catch (Exception ignore) {}

   }
   
   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Current Invoices", true);
   addBodyStartPage(out, "Current Invoices", true);
        
   //
   // output main instructions.
   //
   out.println("      <b>Instructions:</b><br />");
   out.println("      <br />This page shows all outstanding invoices.  Select the invoice to update it.");
   /*
   out.println("      <br />This page " +
       "contains two invoicing processing options:<br /> &nbsp;&nbsp;&nbsp;1. Continue processing the last invoice (if not yet paid), or<br />" +
       " &nbsp;&nbsp;&nbsp;2. Select a created invoice hyperlink from the new or unpaid table and process.<br /><br />" +
       "The ForeTees state below represents the state of the last processed invoice. ");
    */
   out.println("    </div>");         // complete main insturctions.
   out.println("    <h3 class=\"support_ctr\">" + clubName + "</h3>");
   out.println("    <br />");
   out.println("    <p class=invoice><strong>Date: </strong>" +sdate+ "</p>");
    
   out.println("");
   out.println("<script type=\"text/javascript\">");
   out.println("  function submitProcessStateForm(invId, invNum) {" );
   out.println("    f = document.getElementById(\"frmProcessState\");");
   out.println("    id = f.invoiceId;");
   out.println("    num = f.invoiceNumber;");
   out.println("    newInv = f.newInvoice;");
   out.println("    id.value = invId;");
   out.println("    num.value = invNum;");
   out.println("    f.submit();");
   out.println("  }");
   
   out.println("</script>");
 
   out.println("    <form action=\"Support_invoicing\" method=\"post\" name=frmProcessState " +
               "id=frmProcessState>");
   out.println("      <input type=hidden name=\"chgCurrentInvSubmit\" value=\"1\" />");
   out.println("      <input type=hidden name=\"invoiceId\" value=\"0\" />");       // will be updated in javascript.
   out.println("      <input type=hidden name=\"invoiceNumber\" value=\"0\" />");
   
   /*
   out.println("    <table id=\"invoice_process_results_state\">");
   out.println("    <thead>");
   out.println("      <tr>");
  // out.println("        <th></th>");
   out.println("        <th class=\"last_invnum\">Last Invoice Processed</th>");
   out.println("        <th>ForeTees State</th>");
   out.println("        <th>Invoice Status</th>");
   out.println("      </tr>");
   out.println("    </thead>");
   out.println("    <tbody>");
   out.println("      <tr>");
   //out.println("        <td><input type=\"submit\" name=\"createInvSubmit\" value=\"Create New Invoice\" /></td>");  // MOVED TO MAIN MENU !
   
   if (invData.getForeTeesState() == invoice_data.foreTeesStType.Current) {
       
     out.println("        <td class=\"last_invnum\">" +invoiceNumber+ "</td>"); 
   }
   else {
     out.println("        <td class=\"last_invnum\"><a href=\"#\" onclick=\"submitProcessStateForm(" +idNum+ ", " +invoiceNumber+ ")\">" +invoiceNumber+ "</a></td>"); 
   }
   out.println("        <td>" + invData.getForeTeesStateStr() + "</td>");
   out.println("        <td>" + inv.getStatusStr() + "</td>");
   out.println("      </tr>");
   out.println("    </tbody>");
   out.println("    </table>    <!-- invoice_process_results_state -->");
    */
     
   out.println("    <h3 class=\"support_ctr\">New or Unpaid Invoices</h3>");

   
   // 
   // Get invoices with status of "Not Paid" and display.
   //
   try {

     pstmt = con.prepareStatement ( 
          "SELECT id FROM invoice WHERE invStatus!='1' ORDER BY payDueDate");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();

     rs.last();          // need last to get row count.
     rowNum = rs.getRow();
     if (rowNum > 0) {
         
         out.println("    <table id=\"invoice_process_results_new_unpaid\">");
         out.println("    <thead>");
         out.println("      <tr>");
         out.println("        <th>Date Due</th>");
         out.println("        <th>Date Generated</th>");
         out.println("        <th>Invoice Number</th>");
         out.println("        <th>Amount</th>");
         out.println("        <th>Status</th>");
         out.println("      </tr>");
         out.println("      </thead>");
         out.println("      <tbody>");
         
         rs.afterLast();    // now move to after last to display all unpaid.
         while (rs.previous() ) {

           idNum = rs.getInt("id");
           inv.retrieve(con, idNum);
           invoiceNumber = inv.getInvoiceNumber();

           out.println("        <tr>");
           out.println("          <td>" + inv.getDueDate(2) + "</td>");
           out.println("          <td>" + inv.getGenerateDate(2) + "</td>"); 
           out.println("          <td><a href=\"#\" onclick=\"submitProcessStateForm(" +idNum+ ", " +invoiceNumber+ ")\">" 
                      + invoiceNumber + "</a></td>");
           out.println("          <td>" + inv.getAmountDueStr() + "</td>");
           out.println("          <td>" + inv.getStatusStr() + "</td>");
           out.println("        </tr>");
         }
     }

   } catch (Exception exc) {

     out.println("    </tbody>");
     out.println("    </table>");
     out.println("    <p class=\"invoice\">");
     out.println("    <br /><b>Exception</b> (Display processing): "+ exc.getMessage());
     out.println("    </p>");

     out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a></p>");
     out.println("</div>");
     out.println("</div>");
     out.println("</body></html>");
     return;

   } finally {

     try { rs.close(); }
     catch (Exception ignore) {}

     try { pstmt.close(); }
     catch (Exception ignore) {}

   }

   //
   // Were unpaid entries found, then close table, else print message.
   if (rowNum > 0) {
       out.println("    </tbody>");
       out.println("    </table>    <!-- invoice_process_results_new_unpaid -->");   
   }
   else {
         
       out.println("    <p class=\"invoice\">No new or unpaid invoices found.</p>");         
   }
   out.println("</form>");
   out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Invoice Main Menu</a></p>");
   out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
   out.println("  </div> <!-- main  -->");
   out.println("  </div> <!-- wrapper  -->");
   out.println("</body></html>");
     
   return;

} // displayInvoiceProcessState()


 //
 //  Display summary invoice history for this club page.
 //
 private void displayInvoices(Connection con, PrintWriter out, String clubName, int option) {
     
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();

   int idNum = 0;
   int invoiceNumber = 0;
   int amountDue = 0;
   int checkNumber = 0;
   int invStatus = 0;
   String dueDate = "";
   String paidDate = "";
   String checkNumStr = "";
   boolean full = (option == 3);
   boolean firstPass = true;
   boolean foundInvoice = false;
   
   //
   // Output top of page.
   //
   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Invoice Display History", false);
   out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_customer.css\" />");

   out.println("<script type=\"text/javascript\">");
   out.println("  function submitInvNumForm(invId, invNum) {" );
   out.println("    f = document.getElementById(\"frmInvoiceNumDisplay\");");
   out.println("    id = f.invoiceId;");
   out.println("    num = f.invoiceNumber;");
   out.println("    id.value = invId;");
   out.println("    num.value = invNum;");
   out.println("    f.submit();");
   out.println("  }");

   out.println("  function submitInvDisplayForm(type) {" );
   out.println("    f = document.getElementById(\"frmInvoiceDisplayHist\");");
   out.println("    disp = f.invdisplay;");
   out.println("    if (disp.value == type) {");
   out.println("      return;");
   out.println("    }");
   out.println("    disp.value = type;");
   out.println("    f.submit();");
   out.println("  }");
   
   out.println("</script>");

   out.println("</head>");
   addBodyStartPage(out, "Invoice Display History", true);
        
   out.println("    <b>Instructions:</b><br />");
   out.println("    <br />To display the full invoice, select the invoice number.");
   out.println("    <br /><b>All</b> - contains the paid and outstanding invoices issued to this club, and new invoices.");
   out.println("    <br /><b>Unpaid</b> - contains the outstanding invoices issued to this club.");
   out.println("    <br /><b>Full Record</b> - contains all fields of all invoices for this club.");
   out.println("  </div>");
   
   out.println("    <h3 class=\"support_ctr\">" + clubName + "</h3>");

   out.println("    <form action=\"Support_invoicing\" method=\"post\" name=frmInvoiceDisplayHist "
              + "id=frmInvoiceDisplayHist>");
   out.println("      <input type=hidden name=\"displayInvSubmit\" value=\"Display Invoice History\" />");
   out.println("      <input type=hidden name=\"currChoice\" value=\"" +option+ "\" />");

   //
   // Display radio buttons based on summary or full selection.
   //
   if (full) {
       out.println("    <p class=\"invoice\"><strong>");
       out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"1\" onclick=\"submitInvDisplayForm(1)\" />");
       out.println("        All &nbsp;&nbsp;");
       out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"2\" onclick=\"submitInvDisplayForm(2)\" />");
       out.println("        Unpaid &nbsp;&nbsp;");
       out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"3\" checked=\"checked\" " 
                  + " onclick=\"submitInvDisplayForm(3)\" />");
       out.println("        Full Record</strong><br />");
       out.println("    </p>");
   }
   else {
       
       out.println("    <p class=\"invoice\"><strong>");
       
       if (option == 1) {
           
           out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"1\" checked=\"checked\" "
                      + " onclick=\"submitInvDisplayForm(1)\" />");
           out.println("        All &nbsp;&nbsp;");
           out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"2\" onclick=\"submitInvDisplayForm(2)\" />");
           out.println("        Unpaid");
       }
       else {
           out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"1\" onclick=\"submitInvDisplayForm(1)\" />");
           out.println("        All &nbsp;&nbsp;");
           out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"2\" checked=\"checked\" "
                      + " onclick=\"submitInvDisplayForm(2)\" />");
           out.println("        Unpaid");           
       }
       
       out.println("      <input type=\"radio\" name=\"invdisplay\" value=\"3\" onclick=\"submitInvDisplayForm(3)\" />");
       out.println("        Full Record</strong><br />");
       out.println("    </p>");       
   } // else (if full)
   
   out.println("    <br />");
   out.println("    </form>");
      
   
   if (full) {
       
       out.println("    <table class=\"invoice_record\">");
       out.println("    <tbody>");

       // 
       // Get all invoices from the database and display.
       //
       try {

         pstmt = con.prepareStatement ( 
              "SELECT id FROM invoice ORDER BY payDueDate");

         pstmt.clearParameters();
         rs = pstmt.executeQuery();

         rs.afterLast();
         while (rs.previous() ) {

           idNum = rs.getInt("id");
           inv.retrieve(con, idNum);

           if (firstPass) {
               firstPass = false;
           }
           else {

               out.println("      <tr><td class=\"space\" colspan=\"7\">* * *</td></tr>"); 
           }

           out.println("      <tr><td colspan=\"4\">Invoice Number: " + inv.getInvoiceNumber() + "</td>");
           out.println("        <td colspan=\"3\">Status: " + inv.getStatusStr() + "</td>");
           out.println("      </tr>");
           out.println("      <tr class=\"col_desc\">");
           out.println("        <td class=\"size1\">Date Due</td>");      
           out.println("        <td class=\"size1\">Billable Type</td>");      
           out.println("        <td class=\"counts\">Total</td>");      
           out.println("        <td class=\"counts\">Inact</td>");      
           out.println("        <td class=\"counts\">Excluded</td>");      
           out.println("        <td class=\"counts\">Billable</td>");      
           out.println("        <td class=\"size1 counts\">Amount Due</td>");      
           out.println("      </tr><tr>");
           out.println("        <td>" + inv.getDueDate(4) + "</td>");
           out.println("        <td class=\"size1\">" +inv.getBillRateStr()+ "</td>");
           out.println("        <td class=\"counts\">" +inv.getTotalCnt()+ "</td>");
           out.println("        <td class=\"counts\">" +inv.getInactCnt()+ "</td>");
           out.println("        <td class=\"counts\">" +inv.getExcludedCnt() + "</td>");
           out.println("        <td class=\"counts\">" +inv.getBillableCnt() + "</td>");
           out.println("        <td class=\"size1 counts\">" +inv.getAmountDueStr() + "</td>");
           out.println("      </tr>");
           out.println("      <tr>");
           out.println("        <td colspan=\"2\">Generated on: " + inv.getGenerateDate(4) + "</td>");

           // Only display paid date for invoice status of "Paid".
           out.println("        <td colspan=\"3\">Paid on: " + inv.getPaidDate(false, 4) + "</td>");
           out.println("        <td colspan=\"2\">Check: " + inv.getCheckNumber() + "</td>");
           out.println("      </tr>");

         }

       } catch (Exception exc) {

         out.println("    </tbody>");
         out.println("    </table>");
         out.println("    <p class=\"invoice\">");
         out.println("    <br /><b>Exception</b> (Display processing): "+ exc.getMessage());
         out.println("    </p>");

         out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a></p>");
         out.println("</div>");
         out.println("</div>");
         out.println("</body></html>");
         return;

       } finally {

         try { rs.close(); }
         catch (Exception ignore) {}

         try { pstmt.close(); }
         catch (Exception ignore) {}

       }

       out.println("    </tbody>");
       out.println("    </table>    <!-- invoice_record -->");
       
   } else {    // Display summary history of invoices.
       
      out.println("    <form action=\"Support_invoicing\" method=\"post\" target=\"_blank\" name=frmInvoiceNumDisplay "
              + "id=frmInvoiceNumDisplay>");
      out.println("      <input type=hidden name=\"showClubInvSubmit\" value=\"Show Invoice\" />");
      out.println("      <input type=hidden name=\"invoiceId\" value=\"0\" />");       // will be updated in javascript.
      out.println("      <input type=hidden name=\"invoiceNumber\" value=\"0\" />");

      out.println("    <table id=\"invoice_display_customer\">");
      out.println("    <thead>");
      out.print("      <tr><th>Date Due</th><th>Invoice</th><th class=\"amount\">Amount</th><th>Check #</th>");
      out.println("<th>Paid Date</th></tr>");
      out.println("    </thead>");
      out.println("    <tbody>");
      
      // 
      // Get all invoices from the database and display.
      //
      try {
         
        pstmt = con.prepareStatement ( 
             "SELECT id, invoiceNumber, invStatus, payDueDate, amountDue, checkNumber, paidDate FROM invoice ORDER BY payDueDate");
     
        pstmt.clearParameters();
        rs = pstmt.executeQuery();

        rs.afterLast();
        while (rs.previous() ) {
            
          idNum = rs.getInt("id");
          invoiceNumber = rs.getInt("invoiceNumber");
          dueDate = rs.getString("payDueDate");
          paidDate = rs.getString("paidDate");
          amountDue = rs.getInt("amountDue");
          checkNumber = rs.getInt("checkNumber");
          invStatus = rs.getInt("invStatus");
          
          // Use invoice to format dates.
          inv.setDueDate(dueDate);
          inv.setPaidDate(paidDate);
          inv.setAmountDue(amountDue);
          inv.setStatus(inv.convertIntToStatusType(invStatus));  // set status to be used in paid date setting.
                    
          //
          // Display all invoice records or just unpaid records.
          //
          if ((option == 1) || (inv.getStatus() != invoice.statusType.Paid)) {
    
              foundInvoice = true;
              
              // Update check number output.
              checkNumStr = "-";
              if (inv.getStatus() == invoice.statusType.Paid) {
                  checkNumStr = "" + checkNumber;
              }
              out.println("      <tr><td>" + inv.getDueDate(2) + "</td>");
              out.println("        <td><a href=\"#\" onclick=\"submitInvNumForm(" +idNum+ ", " +invoiceNumber+ ")\" >" + invoiceNumber + "</a></td>");
              out.println("        <td class=\"amount\">" + inv.getAmountDueStr() + "</td>");      
              out.println("        <td>" + checkNumStr + "</td>");
              out.println("        <td>" + inv.getPaidDate(false, 2) + "</td>");
              out.println("      </tr>");
          }
        } // while (rs.previous)

        if (!foundInvoice) {
            
            // no invoice was output, so put out a message.
            //
            out.println("      <tr>");
            if (option == 1) {
                out.println("        <td colspan=\"5\">No invoices were found for your club.</td>");              
            }
            else {
                out.println("        <td colspan=\"5\">There are no outstanding invoices for your club. <br />Thank you.</td>");
            }
            out.println("      </tr>");
        }
        
        
      } catch (Exception exc) {
          
        out.println("    </tbody>");
        out.println("    </table>");
        out.println("    </form>");
        out.println("    <p class=\"invoice\">");
        out.println("    <br /><b>Exception</b> (Display processing): "+ exc.getMessage());
        out.println("    </p>");
    
        out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a></p>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body></html>");
        return;
      
      } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

      }
      
      out.println("    </tbody>");
      out.println("    </table>    <!-- invoice_display_customer-->");
      out.println("    </form>");
   }
   
   out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a></p>");
   out.println("  </div>");
   out.println("  </div>   <!-- wrapper  -->");
   out.println("</body></html>");             

 } // displayInvoices()                
 
  
   
 //*********************************************************************************
 // Active invoice processing is performed here. The selected option for the active
 // page is parsed and acted upon.
 //*********************************************************************************
 private void activeInvoiceProcessing(HttpServletRequest req, PrintWriter out, String clubName, String club, Connection con,
         String activeOption) {
 
   String activeDesc = "";
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();
   String monStr = "";
   String dayStr = "";
   String yearStr = "";
   int mon = 1;
   int day = 10;
   int year = 2012;
   String checkNumStr = "";
   int checkNum = 0;
   
   // Process active page action.
   //
   try {

     inv.retrieve(con);
     invData.currentState(con, true);    
 
     if (activeOption.equals("notifyClub2")) {
         
         activeDesc = "Notify complete (no email)";
         //
         // Check if this club is using the invoicing system.
         //
         if (invData.getInvoiceOn()) {
             
             sendInvoiceEmail(req, club, con);
             activeDesc = "Notify complete";
         }
         inv.setStatus(con, invoice.statusType.Pending);
         invData.updState(invoice_data.eventType.NotifyClub, con );
     }
     else {
         if (activeOption.equals("updInvoice")) {
             
             // handle update invoice in doPost().
         }
         else {
             if (activeOption.equals("markPayRecvd")) {
                 
                 monStr = req.getParameter("payMon");
                 dayStr = req.getParameter("payDay");
                 yearStr = req.getParameter("payYear");
                 
                 if (monStr.length() > 0) {
                     
                     mon = Integer.parseInt(monStr);    // month.
                 }
                 
                 if (dayStr.length() > 0) {
                     
                     day = Integer.parseInt(dayStr);    // day.
                 }
                 
                 if (yearStr.length() > 0) {
                     
                     year = Integer.parseInt(yearStr);  // year.
                 }
                 
                 checkNumStr = req.getParameter("checkNum");
                 
                 if (checkNumStr.length() > 0) {
                     
                     checkNum = Integer.parseInt(checkNumStr);    // check number.
                 }
                
                 inv.updatePaid(con, year, mon, day, checkNum);
                 inv.setStatus(con, invoice.statusType.Paid);
                 activeDesc = "Mark payment complete";
                 invData.updState(invoice_data.eventType.MarkPaid, con );
                 
                 if (inv.getBillingRateType() != invoice.billingRateTyp.Setup) {
                     //
                     // Advance next due date one year.
                     //
                     Calendar newNextDueDate = invData.getNextDueDateCal();
                     newNextDueDate.add(Calendar.YEAR, 1);
                     invData.setNextDueDateCal(newNextDueDate);
                     invData.updateNextDueDateToDb(con);
                 }
             }
             else {
                 activeDesc = "Internal Error, no option";
             } // else "markPayRcvd"
         } // else "updInvoice"
     } // else "notifyClub"     

   } catch (Exception exc) {
      addDocTypeHdr(out);
      out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
      out.println("<head><title>Active Invoice Processing</title>");
      out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
      out.println("</head>");
      out.println("<body class=\"serifFont\">");
      out.println("<div id=\"wrapper\">");
      out.println("    <br /><br />Exception (Save processing): "+ exc.getMessage());
      //out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
    
      out.println("</div>");
      out.println("</body></html>");
      return;
      
   } finally {
   }
     
   displayActiveInvoice(out, clubName, invData, inv, activeDesc);
 
 }
        
   
 //
 //  Display active invoice page.
 //
 private void displayActiveInvoice(PrintWriter out, String clubName, invoice_data invData,
         invoice inv, String processStatus) {
 
   int ftNum = invData.convertFTstateToInt(invData.getForeTeesState() );
   int ftInvSentNum = invData.convertFTstateToInt(invoice_data.foreTeesStType.InvoiceSent);
   Calendar today = new GregorianCalendar ();
   String freqStr = "One Time";
     
   //
   //  Present results and an options form
   //   
   addDocTypeHdr(out);
   addHeadTitle(out, "Active Invoice Processing", true);
   addBodyStartPage(out, "Active Invoice", false);
   
   out.println("    <h3 class=\"support_ctr\">Active Invoice for " +clubName+ "</h3>");   
   out.println("    <br />");   
   out.println("    <table class=\"invoice_record\">");
   out.println("    <tbody>");
   out.println("      <tr>");
   out.println("        <td colspan=\"4\"><strong>Invoice Number:</strong> " +inv.getInvoiceNumber()+ "</td>");
   out.println("        <td colspan=\"3\"><strong>Status:</strong> " +inv.getStatusStr()+ ".</td>");
   out.println("      </tr>");
   out.println("      <tr class=\"col_desc\">");
   out.println("        <td class=\"size1\">Date Due</td>");
   out.println("        <td class=\"size1\">Billing Rate</td>");
   out.println("        <td class=\"counts\">Total</td>");
   out.println("        <td class=\"counts\">Inact</td>");
   out.println("        <td class=\"counts\">Excluded</td>");
   out.println("        <td class=\"counts\">Billable</td>");
   out.println("        <td class=\"size1 counts\">Amount Due</td>");
   out.println("      </tr>");
   
   out.println("      <tr>");
   out.println("        <td class=\"size1\">" +inv.getDueDate(4) + "</td>");
   out.println("        <td class=\"size1\">" +inv.getBillRateStr()+ "</td>");
   out.println("        <td class=\"counts\">" +inv.getTotalCnt()+ "</td>");
   out.println("        <td class=\"counts\">" +inv.getInactCnt()+ "</td>");
   out.println("        <td class=\"counts\">" +inv.getExcludedCnt() + "</td>");
   out.println("        <td class=\"counts\">" +inv.getBillableCnt() + "</td>");
   out.println("        <td class=\"size1 counts\">" +inv.getAmountDueStr() + "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td colspan=\"2\"><strong>Generated on:</strong> " + inv.getGenerateDate(4) + "</td>");
   
   out.println("        <td colspan=\"3\"><strong>Paid on:</strong> " + inv.getPaidDate(false, 4) + "</td>");
   out.println("        <td colspan=\"2\"><strong>Check:</strong> " + inv.getCheckNumber() + "</td>");
   out.println("      </tr>");
   out.println("    </tbody>");
  
   out.println("    </table>    <!-- invoice_record -->");
   
      out.println("    <br />");
     
      // Invoice processing options.
      out.println("    <form action=\"Support_invoicing\" method=\"post\">");
      out.println("    <table id=\"invoice_options\">");
      out.println("    <tbody>");
      /*
      out.println("      <tr>");
      out.println("        <td class=\"bold\">Process Status:</td>");
      out.println("        <td>" + processStatus + ".</td>");
      out.println("      </tr>");
       */
      out.println("      <tr>");
      out.println("        <td class=\"bold\">ForeTees State:</td>");
      out.println("        <td>" + invData.getForeTeesStateStr() + ".</td>");
      out.println("      </tr>");
      out.println("      <tr>");
      
      if (inv.getBillingRateType() != invoice.billingRateTyp.Setup) {
          freqStr = invData.getFrequencyStr();
      }
      out.println("        <td class=\"bold\">Frequency:</td>");
      out.println("        <td>" + freqStr + ".</td>");
      out.println("      </tr>");
      
      if (invData.getForeTeesState() != invoice_data.foreTeesStType.Current) {
          out.println("      <tr>");
          out.println("        <td class=\"bold\"><br />Options for this invoice:</td>");
          out.println("        <td>");


          //
          // Display options for processing this invoice.
          //
          
          out.println("        <input class=\"invoice3\" type=\"radio\" name=\"savedInv\" value=\"notifyClub\"  />");
          out.println("          Notify Club<br />");
          out.println("        <input type=\"radio\" name=\"savedInv\" value=\"updInvoice\"  />");
          out.println("          Update Invoice<br />");
          
          if (ftNum < ftInvSentNum) {
              out.println("        <input type=\"radio\" name=\"savedInv\" value=\"markPayRecvd\" disabled=\"disabled\" />");
              out.println("          Mark payment received<br />");
          }
          else {
              
              out.println("        <input type=\"radio\" name=\"savedInv\" value=\"markPayRecvd\" />");
              out.println("          Mark payment received<br />");
              out.println("        <div id=\"invoice_date_paid_in\">");
              out.println("        &nbsp;&nbsp;&nbsp;DATE: <br />&nbsp;&nbsp;&nbsp;");
              out.println("        mm<input type=\"text\" name=\"payMon\" size=\"1\" value=\"" + (today.get(Calendar.MONTH) + 1) + "\" />");
              out.println("        dd<input type=\"text\" name=\"payDay\" size=\"1\" value=\"" + today.get(Calendar.DATE) + "\" />");
              out.println("        yy<input type=\"text\" name=\"payYear\" size=\"2\" value=\"" + today.get(Calendar.YEAR) + "\" />");
              out.println("        </div>");
              out.println("        <br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Check # ");
              out.println("        <input class=\"invoice2\" type=\"text\" name=\"checkNum\" size=\"10\" value=\"0\" />");
          }

          out.println("        </td>");
          out.println("      </tr>");
      }
      
      out.println("    </tbody>");
      out.println("    </table>   <!-- invoice_options   -->");
          
      out.println("    <p class=\"invoice\">");
      
      if (invData.getForeTeesState() == invoice_data.foreTeesStType.Current) {          
        out.println("      <input type=\"submit\" name=\"activeInvSubmit\" value=\"Submit\" disabled=\"disabled\" />");
      }
      else {
        out.println("      <input type=\"submit\" name=\"activeInvSubmit\" value=\"Submit\" />");
      }
      
      out.println("    </p>");
      out.println("    </form>");
      
      // Provide option to display club invoice.
      out.println("    <form action=\"Support_invoicing\" target=\"_blank\" method=\"post\">");
      out.println("      <input type=hidden name=\"invoiceId\" value=\"0\" />");   
      out.println("      <input type=hidden name=\"invoiceNumber\" value=\"0\" />");
      out.println("    <p class=\"invoice\">");
      out.println("      <input type=\"submit\" name=\"showClubInvSubmit\" value=\"Show Club Invoice\" />");
      out.println("    </p>");
      out.println("    </form>");
            

   out.println("    <p class=\"general_button\"><a href=\"Support_invoicing?itype=process\">");
   out.println("      Return</a></p>");
   out.println("  </div>");
   out.println("  </div>    <!-- wrapper  -->");
   out.println("</body></html>");
 
 } // displayActiveInvoice()
        
   
 //*********************************************************************************
 // Save invoice results and options.
 //*********************************************************************************
 private void saveInvoiceResults(PrintWriter out, String clubName, Connection con,
         int invNum, int blOver, int setupFeeAmount, String saveOption, String item, String description) {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;

   invoice inv = new invoice();
   String saveDesc = "Save complete";
   invoice_data invData = new invoice_data();
   invoice_data.frequencyType freqTyp = invoice_data.frequencyType.Yearly;

   
   // Parse selected frequency option.
   //
   if (saveOption.equals("quarter") || saveOption.equals("biannual")) {
       
       if (saveOption.equals("quarter")) {
           
           freqTyp = invoice_data.frequencyType.Quarterly;
           createAdditionalInvoices(out, con, invNum, blOver, 4, freqTyp, item, description);
       }
       else {
           freqTyp = invoice_data.frequencyType.Biannually;
           createAdditionalInvoices(out, con, invNum, blOver, 2, freqTyp, item, description);           
       }
       
       try {
           inv.retrieve(con);                         // get the current invoice for displaying.
           invData.updState(invoice_data.eventType.SaveInv, con );
           
       } catch (Exception exc) {
           addDocTypeHdr(out);

           out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
           out.println("<head><title>Active Invoice Processing</title>");
           out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
           out.println("</head>");
           out.println("<body class=\"serifFont\">");
           out.println("<div id=\"wrapper\">");
           out.println("    <br /><br />Exception (Frequency Save processing): "+ exc.getMessage());
           //out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");

           out.println("</div>");
           out.println("</body></html>");
           return;

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }
   } 
   else {
       //
       // Update db with invoice number and fields based on save type.
       //
       try {

         invData.currentState(con, false);
         invData.updFrequency(con, freqTyp);
         inv.retrieve(con);
 
         if (saveOption.equals("setupfee")) {

             inv.updAfterCreate2(con,invNum, setupFeeAmount, item, description);
         }
         else {
             inv.updAfterCreate(con, invNum, blOver, item, description);
         }
         invData.updState(invoice_data.eventType.SaveInv, con );

       } catch (Exception exc) {
         addDocTypeHdr(out);

         out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
         out.println("<head><title>Active Invoice Processing</title>");
         out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
         out.println("</head>");
         out.println("<body class=\"serifFont\">");
         out.println("<div id=\"wrapper\">");
         out.println("    <br /><br />Exception (Save processing): "+ exc.getMessage());
         //out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");

         out.println("</div>");
         out.println("</body></html>");
         return;

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }
   } // else (if (quarterly or biannually billing)
     
   displayActiveInvoice(out, clubName, invData, inv, saveDesc);

 } 
 
 //
 // Create additional invoices when billing is NOT yearly.
 //
 private void createAdditionalInvoices(PrintWriter out, Connection con, int invNum, int blOver, int totInvoices,
         invoice_data.frequencyType freqTyp, String item, String description) {
 

   invoice inv = new invoice();           // current invoice.
   invoice inv2 = new invoice();          // new invoice(s).
   invoice_data invData = new invoice_data();
   int remainder = 0;
   int quotient = 0;
   int billCntFirst = 0;
   int billCntOther = blOver;
   int currInvId = 0;
   int i;
   Calendar dueDate = new GregorianCalendar();
   int monthsAdd = 12 / totInvoices;       // months to add on next due date.
   
   try {
       inv.retrieve(con);
       invData.currentState(con, false);
       invData.updFrequency(con, freqTyp);
       
       currInvId = inv.getId();

       remainder = billCntOther % totInvoices;
       quotient = billCntOther / totInvoices;
       billCntFirst = quotient + remainder;
       billCntOther = quotient;
       
       inv.updAfterCreate(con, invNum, billCntFirst, item, description);     // update the invoice
       
       dueDate = inv.getDueDateCal();
       
       // build additional invoices.
       for (i = 1; i < totInvoices; i++) {

           // update due date for next invoice.
           dueDate.add(Calendar.MONTH, monthsAdd);
           inv2.setDueDateCal(dueDate);
          
           inv2.create(invNum, false, inv.getBillingRateType(), inv.getTotalCnt(),
                 inv.getInactCnt(), inv.getExcludedCnt(), billCntOther, con, true);
           
           invNum++;
           inv2.updAfterCreate(con, invNum, billCntOther, item, description);
            
       }
       
       //
       //   update the current invoice id in the club profile table
       //
       invData.updateCurrInvoiceToDb(con, currInvId);

   } catch (Exception exc) {
       
       addDocTypeHdr(out);
       out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
       out.println("<head><title>Active Invoice Processing</title>");
       out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
       out.println("</head>");
       out.println("<body class=\"serifFont\">");
       out.println("<div id=\"wrapper\">");
       out.println("    <br /><br />Exception (Save processing): "+ exc.getMessage());
       //out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
       out.println("</div>");
       out.println("</body></html>");
       return;
   }
     
 }
 
 //
 //  Display invoice setup input page.
 //
 private void displayInvoiceSetup(Connection con, PrintWriter out, String clubName) {
    
   Calendar today = new GregorianCalendar ();
   Calendar nextDueDate = new GregorianCalendar ();
   invoice_data invData = new invoice_data();
   String freqYearCheck = "checked=\"checked\""; 
   String freqQuarterCheck = ""; 
   String freqBiannualCheck = ""; 
   String billtypeNoneCheck = "checked=\"checked\""; 
   String billtypeMemberCheck = ""; 
   String billtypeMShipCheck = ""; 
   String invoiceOnCheck = "checked=\"checked\"";
   int invoiceOn = 1;
   int ftState = 0;

   try {
       invData.retrieve(con);       // get values from the database.
       nextDueDate = invData.getNextDueDateCal();
       ftState = invData.convertFTstateToInt(invData.getForeTeesState());

   } catch (Exception exc) {
       addDocTypeHdr(out);

       out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
       out.println("<head><title>Active Invoice Processing</title>");
       out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
       out.println("</head>");
       out.println("<body class=\"serifFont\">");
       out.println("<div id=\"wrapper\">");
       out.println("    <br /><br />Exception (displayInvoiceSetup(): "+ exc.getMessage());

       out.println("</div>");
       out.println("</body></html>");
       return;
   } 

   // If first time in setup or resetting invoice_data, then use canned values.
   //
   if (invData.getInvEmailContact().equals("first")) {
       
       // Use canned initial data if the first time using setup for this club.
       invData.setInvoiceOn(true);
       ftState = invData.convertFTstateToInt(invoice_data.foreTeesStType.Current);
       today.add(Calendar.MONTH, 1);              // set 1st due date 1 month from today.
       nextDueDate.set(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH) );
       invData.setNextDueDateCal(today);
       invData.setNotifyDays(14);
       invData.setDefBillType(invoice.billingRateTyp.None);
       invData.setFreq(invoice_data.frequencyType.Yearly);
       invData.setAddr1("");
       invData.setAddr2("");
       invData.setCity("");
       invData.setState("");
       invData.setZipCode("");
       invData.setInvEmailContact("jd@club.com");
   }
   
   //
   // Change frequency checked setting if different than default of "Yearly".
   //
   if (invData.getFreq() == invoice_data.frequencyType.Biannually) {
   
       freqYearCheck = "";       
       freqBiannualCheck = "checked=\"checked\"";       
       freqQuarterCheck = "";       
   }
   else if (invData.getFreq() == invoice_data.frequencyType.Quarterly) {
   
       freqYearCheck = "";       
       freqBiannualCheck = "";       
       freqQuarterCheck = "checked=\"checked\"";       
   }
  
   //
   // Change billing type checked setting if different than default of "None".
   //
   if (invData.getDefBillType() == invoice.billingRateTyp.Member) {

       billtypeNoneCheck = "";
       billtypeMemberCheck = "checked=\"checked\"";
       billtypeMShipCheck = "";
   }
   else if (invData.getDefBillType() == invoice.billingRateTyp.MShip) {

       billtypeNoneCheck = "";
       billtypeMemberCheck = "";
       billtypeMShipCheck = "checked=\"checked\"";
   }

   // Change invoice on if currently off.
   //
   if (!invData.getInvoiceOn()) {
       
       invoiceOnCheck = "";
       invoiceOn = 0;
   }

   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Customer Profile", true);
   addBodyStartPage(out, "Customer Profile Settings", true);
   
   out.println("      <b>Instructions:</b><br />");
   out.println("      <br />This is the Customer Settings page. See field descriptions below.");
   out.println("    </div>");

   out.println("  ");
   out.println("    <h3 class=\"support_ctr\">" + clubName + "</h3>");
 
   out.println("    <form action=\"Support_invoicing\" method=\"post\">");
   out.println("      <input type=hidden name=\"ftState\" value=\"" +ftState+ "\" />");
   out.println("    <div id=\"invoice_setup_wrapper\">");
   out.println("    <div id=\"invoice_setup\">");
   out.println("      <table id=\"invoice_setup_info\">");
   out.println("      <tbody>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Invoice On:</td>");
   out.println("        <td class=\"parm_input\"><input type=\"checkbox\" name=\"invoiceOn\" value=\"" + invoiceOn + "\" " +invoiceOnCheck+ " />");
   out.println("        </td>");
   out.println("        <td>Turn invoicing on / off for this club.</td>");
   out.println("      </tr>");
   
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Next Billing Date: </td>");
   out.println("        <td class=\"parm_input\">Date:<br />mm / dd / yy<br />");
   out.println("        <input type=\"text\" name=\"payMon\" size=\"1\" value=\"" + (nextDueDate.get(Calendar.MONTH) + 1) + "\" />" +
               "/<input type=\"text\" name=\"payDay\" size=\"1\" value=\"" +nextDueDate.get(Calendar.DAY_OF_MONTH) + "\" />");
   out.println("        /<input type=\"text\" name=\"payYear\" size=\"2\" value=\"" +nextDueDate.get(Calendar.YEAR) + "\" />");
   out.println("        </td>");
   out.println("        <td>Next invoice due date for club. Will be incremented when invoice processing is completed.</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Notify Days: </td>");
   out.println("        <td class=\"parm_input\"><input type=\"text\" name=\"notifydays\" size=\"2\" value=\"" +invData.getNotifyDays()+"\" /></td>");
   out.println("        <td>Number of days before due date that ForeTees will send invoice.</td>");
   out.println("      </tr>");
      
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Frequency: </td>");
   out.println("        <td class=\"parm_input\"><input type=\"radio\" name=\"frequency\" value=\"0\" " +freqYearCheck+ " />Yearly <br />");
   out.println("          <input type=\"radio\" name=\"frequency\" value=\"1\" " +freqBiannualCheck+ " />Biannually <br />");
   out.println("          <input type=\"radio\" name=\"frequency\" value=\"2\" " +freqQuarterCheck+ " />Quarterly <br />");
   out.println("        </td>");
   out.println("        <td>Frequency of billing for which invoices will be sent or prepared for club.</td>");
   out.println("      </tr>");      
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Billing Type Default: </td>");
   out.println("        <td class=\"parm_input\"><input type=\"radio\" name=\"billtype\" value=\"0\" " +billtypeNoneCheck+ " />"
             + "None <br />");
   out.println("          <input type=\"radio\" name=\"billtype\" value=\"1\" " +billtypeMemberCheck+ " />"
             + "$10 Per Adult <br />");
   out.println("          <input type=\"radio\" name=\"billtype\" value=\"2\" " +billtypeMShipCheck+ "\" />$20 Per Membership <br />");
   out.println("        </td>");
   out.println("        <td>Default billing type setting for this club.</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Club Address: </td>");
   out.println("        <td class=\"parm_input\">street 1<br /><input type=\"text\" name=\"street1\" size=\"20\"  "
               + "value=\"" + invData.getAddr1()+ "\" />");
   out.println("        <br />street 2<br /><input type=\"text\" name=\"street2\" size=\"20\" value=\"" +invData.getAddr2()+ "\" />");
   out.println("        <br />city<br /><input type=\"text\" name=\"city\" size=\"20\" value=\"" +invData.getCity()+ "\" />");
   out.println("        <br />state <input type=\"text\" name=\"state\" size=\"4\" value=\"" +invData.getState()+ "\" />");
   out.println("        <br />zip <input type=\"text\" name=\"zip\" size=\"6\" value=\"" +invData.getZipCode()+ "\" />");
   out.println("        </td>");
   out.println("        <td>Mailing address of this club.</td>");
   out.println("      </tr>");
   /*
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Email Contact: </td>");
   out.println("        <td class=\"parm_input\"><input type=\"text\" name=\"email\" size=\"20\" "
               + "value=\"" +invData.getInvEmailContact()+"\" />");
   out.println("        </td>");
   out.println("        <td>Person who email notification will be sent to.</td>");
   out.println("      </tr>");
    */

   out.println("    </tbody>");
   out.println("    </table>   <!-- invoice_setup_info   -->");
   out.println("    </div>");
   out.println("    </div>    <!-- invoice_setup_wrapper -->");
   out.println("    <p class=\"invoice\">");
   out.println("      <input type=\"submit\" name=\"setupInvSubmit\" value=\"Submit\" />");
   out.println("    </p>");

   out.println("    </form>");  
   out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Return - Cancel</a></p>");
   out.println("  </div>");
   out.println("  </div>   <!-- wrapper  -->");
   out.println("</body></html>");             
              
 }     
  
 
 private void displaySetupResults(HttpServletRequest req, PrintWriter out, String clubName, Connection con, boolean save){
     
   // Get parameters from setup form.
   //
   invoice_data invData = new invoice_data();
   invoice inv = new invoice();              // use to convert bill type.
   String monStr = req.getParameter("payMon");
   String dayStr = req.getParameter("payDay");
   String yearStr = req.getParameter("payYear");
   String notifyDaysStr = req.getParameter("notifydays");
   String emailContact = "";
   String addr1 = req.getParameter("street1");
   String addr2 = req.getParameter("street2");
   String city = req.getParameter("city");
   String state = req.getParameter("state");
   String zipcode = req.getParameter("zip");
   //emailContact = req.getParameter("email");       //  remove this !!!!!!!  not needed  
   String invoiceOnVal = "Off";
   int mon = 1;
   int day = 10;
   int year = 2012;
   int notifyDays = 0;
   int freq = Integer.parseInt(req.getParameter("frequency"));
   int billType =  Integer.parseInt(req.getParameter("billtype"));
   int invoiceOn = 0;
   int ftState = Integer.parseInt(req.getParameter("ftState"));
   Calendar nextDueDate = new GregorianCalendar ();
   
   if (monStr.length() > 0) {
       mon = Integer.parseInt(monStr);
   }
   if (dayStr.length() > 0) {
       day = Integer.parseInt(dayStr);
   }
   if (yearStr.length() > 0) {
       year = Integer.parseInt(yearStr);
   }
   
   nextDueDate.set(year, mon-1, day);

   if (notifyDaysStr.length() > 0) {
       notifyDays = Integer.parseInt(notifyDaysStr);
   }
   
   if (req.getParameter("invoiceOn") != null) {
       
       invoiceOn = Integer.parseInt(req.getParameter("invoiceOn"));
   }
  
   //
   // Update invoice_data values.
   //
   invData.setNextDueDateCal(nextDueDate);
   invData.setNotifyDays(notifyDays);
   invData.setAddr1(addr1);
   invData.setAddr2(addr2);
   invData.setCity(city);
   invData.setState(state);
   invData.setZipCode(zipcode);
   invData.setInvEmailContact(emailContact);
   invData.setFreq(invData.convertIntToFreqType(freq));
   invData.setDefBillType(inv.convertIntToBRateType(billType));
   invData.setForeTeesState(invData.convertIntToFTstateType(ftState));
   invData.setInvoiceOn(invoiceOn >= 1);
   
   if (invData.getInvoiceOn()) {
       
       invoiceOnVal = "On";        // Invoicing on, show "on".
   }
   
   // Special case set default billing rate in invoice variable.
   //
   inv.setBillingRate(invData.getDefBillType());
   
   /*            // this is done by the form below
   if (save) {
       
       displaySetupSaveResults(invData, out, clubName, con);     
       return;
   }
    */
   
   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Invoice Setup Settings", true);
   addBodyStartPage(out, "Invoice Settings", true);
   
   out.println("      <b>Instructions:</b><br />");
   out.println("      <br />Check the invoicing setup settings below and save if correct.");
   out.println("    </div>");

   out.println("  ");
   out.println("    <h3 class=\"support_ctr\">" + clubName + "</h3>");
    
   out.println("    <form action=\"Support_invoicing\" method=\"post\">");
   out.println("      <input type=hidden name=\"ftState\" value=\"" +ftState+ "\" />");
   out.println("      <input type=hidden name=\"payMon\" value=\"" +monStr+ "\" />");
   out.println("      <input type=hidden name=\"payDay\" value=\"" +dayStr+ "\" />");
   out.println("      <input type=hidden name=\"payYear\" value=\"" +yearStr+ "\" />");
   
   out.println("      <input type=hidden name=\"invoiceOn\" value=\"" +invoiceOn+ "\" />");
   out.println("      <input type=hidden name=\"notifydays\" value=\"" +notifyDays+"\" />");
      
   out.println("      <input type=hidden name=\"frequency\" value=\"" +freq+ "\" />");
   out.println("      <input type=hidden name=\"billtype\" value=\"" +billType+ "\" />");
   out.println("      <input type=hidden name=\"street1\" value=\"" + addr1 + "\" />");
   out.println("      <input type=hidden name=\"street2\" value=\"" + addr2 + "\" />");
   out.println("      <input type=hidden name=\"city\" value=\"" + city + "\" />");
   out.println("      <input type=hidden name=\"state\" value=\"" + state + "\" />");
   out.println("      <input type=hidden name=\"zip\" value=\"" + zipcode + "\" />");
   //out.println("      <input type=hidden name=\"email\" value=\"" + emailContact + "\" />");
   
   out.println("    <div id=\"invoice_setup_wrapper\">");
   out.println("    <div id=\"invoice_setup\">");
   out.println("      <table id=\"invoice_setup_info\">");
   out.println("      <tbody>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Invoice On:</td>");
   out.println("        <td>" +invoiceOnVal+ "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Next Billing Date: </td>");
   out.println("        <td>" + mon + "/" + day + "/" + year + "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Notify Days: </td>");
   out.println("        <td>" +notifyDays+ "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Frequency: </td>");
   out.println("        <td>" +invData.getFrequencyStr()+ "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Billing Type Default: </td>");
   out.println("        <td>" +inv.getBillRateStr()+ "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Club Address:</td>");
   out.println("        <td>" +addr1+ "<br />");
   
   if (addr2.length() > 0) {
       out.println("        " +addr2+ "<br />");
   }
   
   out.println("        " +city+", "+state+ " " +zipcode+ "<br /></td>");
   out.println("      </tr>");
   /*
   out.println("      <tr>");
   out.println("        <td class=\"parm_name\">Email Contact:</td>");
   out.println("        <td>" +emailContact+ "</td>");
   out.println("      </tr>");
    */
   out.println("      </tbody>");
   out.println("      </table>   <!-- invoice_setup_info   -->");
   out.println("    </div>");
   out.println("    </div>    <!-- invoice_setup_wrapper -->");
   out.println("    <p class=\"invoice\">");
   out.println("      <input type=\"submit\" name=\"setupSaveSubmit\" value=\"Save Settings\" />");
   out.println("    </p>");

   out.println("</form>");
   out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Invoice Main Menu</a></p>");
   out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
   out.println("</div>");
   out.println("</div>   <!-- wrapper  -->");
   out.println("</body></html>");             
    
 }

 
 private void displaySetupSaveResults(HttpServletRequest req, PrintWriter out, String clubName, Connection con) {
  
   PreparedStatement pstmt1 = null;       // init DB objects
   ResultSet rs = null;
   invoice_data invData = new invoice_data();

   //
   // Get parameters from invData and write to invoice_data database table.
   //
   String monStr = req.getParameter("payMon");
   String dayStr = req.getParameter("payDay");
   String yearStr = req.getParameter("payYear");
   String notifyDaysStr = req.getParameter("notifydays");
   String addr1 = req.getParameter("street1");
   String addr2 = req.getParameter("street2");
   String city = req.getParameter("city");
   String state = req.getParameter("state");
   String zipcode = req.getParameter("zip");
   String emailContact = "";
   String freq = req.getParameter("frequency");
   String billType = req.getParameter("billtype");
   String ftState = req.getParameter("ftState");
   
   //int freq = Integer.parseInt(req.getParameter("frequency"));
   //int billType =  Integer.parseInt(req.getParameter("billtype"));
   int invoiceOn = 0;
   //int ftState = Integer.parseInt(req.getParameter("ftState"));
   int mon = 1;
   int day = 10;
   int year = 2012;
   int notifyDays = 0;
   
   if (monStr.length() > 0) {
       mon = Integer.parseInt(monStr);
   }
   if (dayStr.length() > 0) {
       day = Integer.parseInt(dayStr);
   }
   if (yearStr.length() > 0) {
       year = Integer.parseInt(yearStr);
   }
   
   Calendar nextDueDate = new GregorianCalendar ();
   
   nextDueDate.set(year, mon-1, day);
   
   int rc = invData.setNextDueDateCal(nextDueDate);
   String nextDueDateS = invData.getNextDueDate();

   if (notifyDaysStr.length() > 0) {
       notifyDays = Integer.parseInt(notifyDaysStr);
   }
   
   if (req.getParameter("invoiceOn") != null) {
       
       invoiceOn = Integer.parseInt(req.getParameter("invoiceOn"));
   }
  
   
   boolean exists = false;
   
   
   try {
      
     pstmt1 = con.prepareStatement("SELECT invoiceOn FROM invoice_data WHERE state != ''");        // does an entry already exist??
     pstmt1.clearParameters();
     rs = pstmt1.executeQuery();

     if (rs.next()) {

        exists = true;
     }
      
     if (exists == true) {
      

        pstmt1 = con.prepareStatement("UPDATE invoice_data SET ForeTeesState = ?, " +
                                      "nextDueDate = ?, notifyDays = ?, " + 
                                      "address1 = ?, address2 = ?, city = ?, state = ?, " +
                                      "zipcode = ?, invEmailContact = ?, frequency = ?, " + 
                                      "defBillType = ?, invoiceOn = ?");

        pstmt1.clearParameters();

        pstmt1.setString(1, ftState);
        pstmt1.setString(2, nextDueDateS);       
        pstmt1.setInt(3, notifyDays);
        pstmt1.setString(4, addr1);       
        pstmt1.setString(5, addr2);       
        pstmt1.setString(6, city);       
        pstmt1.setString(7, state);       
        pstmt1.setString(8, zipcode);       
        pstmt1.setString(9, emailContact);       
        pstmt1.setString(10, freq);
        pstmt1.setString(11, billType);
        pstmt1.setInt(12, invoiceOn);

        pstmt1.executeUpdate();

     } else {
        
         pstmt1 = con.prepareStatement (
           "INSERT INTO invoice_data " +
           "(ForeTeesState, nextDueDate, notifyDays, currInvoice, address1, address2, city, state, " +
           "zipcode, invEmailContact, frequency, defBillType, invoiceOn) " +
           "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

         pstmt1.clearParameters();        // clear the parms
         pstmt1.setString(1, ftState);
         pstmt1.setString(2, nextDueDateS);       
         pstmt1.setInt(3, notifyDays);
         pstmt1.setInt(4, 0);
         pstmt1.setString(5, addr1);       
         pstmt1.setString(6, addr2);       
         pstmt1.setString(7, city);       
         pstmt1.setString(8, state);       
         pstmt1.setString(9, zipcode);       
         pstmt1.setString(10, emailContact);       
         pstmt1.setString(11, freq);
         pstmt1.setString(12, billType);
         pstmt1.setInt(13, invoiceOn);

         pstmt1.executeUpdate();       
     }
     
   } catch (Exception exc) {
    
     addDocTypeHdr(out);
   
     out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
     out.println("<head><title>Active Invoice Processing</title>");
     out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
     out.println("</head>");
     out.println("<body class=\"serifFont\">");
     out.println("<div id=\"wrapper\">");
     out.println("    <br /><br />Setup save results processing: "+ exc.getMessage());
     //out.println("    <br /><br /> <a href=\"Support_invoicing\">Return</a>");
    
     out.println("</div>");
     out.println("</body></html>");
     out.close();
     return;
     
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt1.close(); }
        catch (Exception ignore) {}
   }
         
   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Invoice Setup Settings Saved", true);
   addBodyStartPage(out, "Invoice Settings Saved", true);
   
   out.println("<b>Save confirmation.</b><br />");
   out.println("</div>");

   out.println("<h3 class=\"support_ctr\">" + clubName + "</h3>");

   out.println("<p class=\"invoice\">The Club Profile Settings have been saved.</p>");

   out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Invoice Main Menu</a></p>");
   //out.println("<p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Logout\">Logout</a></p>");
   out.println("</div>");
   out.println("</div>   <!-- wrapper  -->");
   out.println("</body></html>");             
    
 }

 
 private invoice.billingRateTyp getBillRate (Connection con) {

   PreparedStatement pstmt = null;
   ResultSet rs = null;
   invoice.billingRateTyp retBillTyp = invoice.billingRateTyp.None;
   invoice inv = new invoice();
   
   try {

     // Get default billing rate type from invoice_data table.
     pstmt = con.prepareStatement ( "SELECT defBillType FROM invoice_data");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next())
         retBillTyp = inv.convertIntToBRateType(rs.getInt("defBillType"));

   } catch (Exception e) {

       return (retBillTyp);
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

   return(retBillTyp);
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
   out.println("<body class=\"serifFont\">");
   out.println("<div id=\"wrapper\">");
   out.println("<img src=\"/" +rev+ "/images/foretees.gif\" /><br />");
   out.println("<hr class=\"menu\">");
   out.println("<br /><h2>Access Error</h2><br />");
   out.println("<br /><br />Sorry, you must login before attempting to access these features.<br />");
   out.println("<br /><br />Please <a href=\"Logout\">login</a>");
   out.println("</div>    <!-- wrapper  -->");
   out.println("</body></html>");

 }
 
 
 //*********************************************************************************
 // User wants to notify club that an invoice is ready.  Prompt user to verify and
 // change the email message if desired.
 //*********************************************************************************
 private void promptEmail(PrintWriter out, String clubName, String club, Connection con) {
    
   //
   //  Send email notification if necessary
   //
   String errorMsg = "";      
   ResultSet rs = null;
   
   int activity_id = 0;         // just use Golf for now !!!!!!!!!!!!!!!!!!!!!!!

   String eaddrTo  = "";     // holds the email addresses
   
   String msg = "\nDear ForeTees Customer," +
                "\n\nThe purpose of this email is to inform you that an invoice for the ForeTees system has been posted." +
                "\n\nPlease let me know if you have any questions. \n\nThank you and have a great day!" +
                "\n\nMelanie Parise\nForeTees\nbilling@foretees.com";

   try {

      //
      // Get email contact from staff_list table ***** NOTE *****  Just do golf for now!!!
      //
      Statement pstmt = con.createStatement();

      rs = pstmt.executeQuery("SELECT address1 FROM staff_list " +
                                 "WHERE address1 != '' AND invoice_golf1 > 0 AND email_bounced1 = 0");

      while (rs.next()) {
          
          eaddrTo += rs.getString("address1") + " ";       // get email address and separate with a space
      }

      rs = pstmt.executeQuery("SELECT address2 FROM staff_list " +
                                 "WHERE address2 != '' AND invoice_golf2 > 0 AND email_bounced2 = 0");

      while (rs.next()) {
          
          eaddrTo += rs.getString("address2") + " ";       // get email address and separate with a space
      }

   }
   catch (Exception e1) {
      errorMsg = "Error1 in prmptEmail gathering email addresses: ";
      errorMsg = errorMsg + e1;                              // build error msg
      Utilities.logError(errorMsg);                                       // log it
   }

   //
   //  Prompt user to send the emails - allow user to change the message and email list
   //
    addDocTypeHdr(out);
    addHeadTitle(out, "Prompt Email Page", true);  
    addBodyStartPage(out, "Menu", false);

    out.println("<hr class=\"menu\" />");
    out.println("<p class=\"menu\">Send Email Prompt</p>");

    out.println("<br />");  

    out.println("<div class=\"main_instructions\">");
    out.println("<p>You are about to send an email to <b>" + clubName + "</b> informing them that an invoice is ready.<br><br>");
    out.println("You may change the message and the email addresses if desired.<br>" +
                "A copy will be sent to <strong>mel@foretees.com</strong> automatically (no need to add it).<br><br>" +
                 "NOTE: Email addresses MUST be separated by a space ONLY - no commas or other characters.</p></div>");

    out.println("<form action=\"/" +rev+ "/servlet/Support_invoicing\" method=\"post\">");
    out.println("<input type=\"hidden\" name=\"savedInv\" value=\"notifyClub2\">");

    out.println("<p align=left>Email Message:&nbsp;<textarea name=\"message\" value=\"\" id=\"message\" cols=\"80\" rows=\"12\">" + msg + "</textarea></p>");

    out.println("<p align=left>Email Addresses:&nbsp;<textarea name=\"emails\" value=\"\" id=\"emails\" cols=\"76\" rows=\"2\">" + eaddrTo + "</textarea></p>");
   
    out.println("<p align=center><input type=\"submit\" value=\"Send The Email\" id=\"activeInvSubmit\" name=\"activeInvSubmit\"></p>");

    out.println("</form><br />");
    out.println("<form action=\"javascript:history.back(1)\" method=\"get\"><p align=center><input type=\"submit\" value=\"Go Back - Do Not Send\" id=\"submit\"></p></form>");
    out.println("</div>");
    out.println("</div>");
    out.println("</body>");
    out.println("</html>");
    out.close();
        
 }      // end of promptEmail
 
 
 
 // **************************************************************************************************************
 //  Process send email to notify club of invoice.
 // **************************************************************************************************************

 private void sendInvoiceEmail(HttpServletRequest req, String club, Connection con) throws Exception {


   //
   //  Send email notification if necessary
   //
   String errorMsg = "";      
   ResultSet rs = null;
   
   int activity_id = 0;         // just use Golf for now !!!!!!!!!!!!!!!!!!!!!!!

   ArrayList<ArrayList<String>> eaddrTo  = new ArrayList<ArrayList<String>>();     // holds the email addresses
   ArrayList<String> eaddrProCopy = new ArrayList<String>();
   
   
   String msg = req.getParameter("message");       // get the email message 

   String emails = req.getParameter("emails");       // get the email addresses
   
   if (msg != null && !msg.equals("") && emails != null && !emails.equals("")) {     // if both parms provided
   
      //
      //  Create a URL for the external login link to view the invoice
      //
      String URL = "";
      String ELS = Utilities.getELS(club, "proshop");      // get encrypted clubname and username for security purposes
      String efrom = "billing@foretees.com";
      String replyTo = "billing@foretees.com";
      String user = "proshop";
      String emailAddr = "";

      //URL = "http://web.foretees.com/"+rev+"/servlet/Login?extlogin=yes&caller=invoice&act_id=" +activity_id+ "&els=" +ELS;

      // ************ Use the following for TESTING - DEV SERVER *********************
      URL = "http://dev.foretees.com/"+rev+"/servlet/Login?extlogin=yes&caller=invoice&act_id=" +activity_id+ "&els=" +ELS;


      msg += "\n\n<a href=\"" +URL+ "\">Click Here to View Your Invoice</a>";

      String subject = "ForeTees Invoice Available";

      String trailerInv = "\n\n\n*****************************************************************************************" +
                          "\nThis message was sent by the ForeTees Finance department. " +
                          "\nPlease contact ForeTees at support@foretees.com if you have questions. " +
                          "\nThank you for using the ForeTees Reservation System." +
                          "\n********************************************************************************************";

      //
      //   Parse the email addresses and put in the array for sendEamil
      //
      StringTokenizer tok = new StringTokenizer( emails, " " );     // delimiter is a space

      int tokcount = tok.countTokens();

      while (tokcount > 0) {
                  
          emailAddr = tok.nextToken();
          eaddrTo.add(new ArrayList<String>());
          eaddrTo.get(eaddrTo.size() - 1).add(emailAddr);
          eaddrTo.get(eaddrTo.size() - 1).add("");                           // no username
          
          tokcount--;
      }


      //
      //  Add Mel to the list
      //
      eaddrProCopy.add( "biling@foretees.com" );


      parmEmail emailParm = new parmEmail();
      emailParm.type = "Invoice";
      emailParm.subject = subject;
      emailParm.txtBody = msg;
      emailParm.from = efrom;
      emailParm.replyTo = replyTo;
      emailParm.activity_name = "ForeTees";
      emailParm.activity_id = activity_id;
      emailParm.club = club;
      emailParm.user = user;
      emailParm.message = trailerInv;

      StringBuffer vCalMsg = new StringBuffer();  // no vCal for email tool (at least not yet!)

      //
      //  Send the email
      //
      sendEmail.doSending(eaddrTo, eaddrProCopy, replyTo, subject, msg, vCalMsg, emailParm, con, "none", null);
   }

 }  // end of sendInvoiceEmail
 
 // ************************************************************************
 //  Process getAuthenticator for email authentication
 // ************************************************************************

 //private static Authenticator getAuthenticator(final String user, final String pass) {
 private Authenticator getAuthenticator(final String user, final String pass) {

    Authenticator auth = new Authenticator() {

       public PasswordAuthentication getPasswordAuthentication() {

         return new PasswordAuthentication(user, pass); // credentials
         //return new PasswordAuthentication("support@foretees.com", "fikd18"); // credentials
       }
    };

    return auth;
 }
 
 
 //
 //  Display error page.
 //
 private void displayError(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, String fnctn, String emsg) {
  
   addDocTypeHdr(out);
   addHeadTitle(out, "ForeTees Invoice Error", true);
   addBodyStartPage(out, "Invoice Processing Error", true);

   out.println("    <b>Error Description:</b><br />");

   out.println("    <br />Function originated: " + fnctn + ".");
   out.println("    <br />Message: " + emsg + ".");
   out.println("    </div>");
   
   out.println("    <p class=\"general_button\"><a href=\"/" +rev+ "/servlet/Support_invoicing\">Invoice Main Menu</a></p>");

   out.println("  </div>");
   out.println("  </div> <!-- wrapper  -->");
   out.println("</body>");
   out.println("</html>");
   out.close();                         
 }     
 
 
 
 // *********************************************************
 //   User input error
 // *********************************************************

 private void inputError(PrintWriter out, String msg) {

      addDocTypeHdr(out);
      out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");

      out.println("<head><title>Input Error</title>");
      out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
      out.println("</head>");
      out.println("<body class=\"serifFont\">");
      out.println("<div id=\"wrapper\">");
      out.println("<h3>Input Error</h3>");
      out.println("<br /><br />" +msg);
      out.println("<br /><p class=\"general_button\"><a href=\"javascript:history.back(1)\">Return</a></p>");
      out.println("</div>    <!-- wrapper -->");
      out.println("</body></html>");
      out.close();           
 }
 
 
 
 // *********************************************************
 // Add XML version and XHTML 1.1 output to data being sent to browser.
 // *********************************************************

 private void addDocTypeHdr(PrintWriter out) {

   out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"");
   out.println("  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
 
 }
 
 private void addHeadTitle(PrintWriter out, String title, boolean close_head) {
     
   out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
   out.println("<head><title>" + title + "</title>");
   out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_foretees.css\" />");
  
   if (close_head) {
       out.println("</head>");
   }
 }

   
 private void addBodyStartPage(PrintWriter out, String crumb, boolean openMainInstruct) {

   out.println("<body>");
   
   out.println("  <div id=\"top\"></div>");   
   out.println("  <div id=\"wrapper\">");
   out.println("  <div id=\"main\">");
   out.println("    <div id=\"breadcrumb\"><a href=\"/" +rev+ "/servlet/Support_invoicing?home=yes\">Main Menu</a> / " + crumb + "</div>");
   out.println("    <div id=\"main_ftlogo\"><img src=\"/" +rev+ "/assets/images/foretees_logo.png\" alt=\"ForeTees\"></div>");
   
   if (openMainInstruct) {
       out.println("    <div class=\"main_instructions\">");
   }
}

}
