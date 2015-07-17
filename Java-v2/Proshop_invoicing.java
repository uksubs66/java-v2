/***************************************************************************************
 *   Proshop_invoicing:  This servlet will process invoices from ForeTees.
 *
 *
 *   created: 3/9/2012   Tim K.
 *
 *   last updated:
 *
 *      6/27/12  BP - Initial changes to tweak the main page.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.sql.*;
import java.text.NumberFormat;


// foretees imports
import com.foretees.common.parmEmail;
import com.foretees.common.sendEmail;
import com.foretees.common.Utilities;
import com.foretees.common.getActivity;


public class Proshop_invoicing extends HttpServlet {


 String rev = SystemUtils.REVLEVEL;       // Software Revision Level (Version)
 String support = "support";             // valid username
 String sales = "sales";

 //****************************************************
 // Process the doGet call (go to doPost)
 //****************************************************
 //
 public void doGet(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {

   doPost(req, resp);      // call doPost processing

 }   // end of doGet


 //****************************************************
 // Process all requests here (doPost)
 //****************************************************
 //
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
         throws ServletException, IOException {
   
   resp.setContentType("text/html");  
   PrintWriter out = resp.getWriter();

   Connection con = null;                  // init DB objects
   
   HttpSession session = null;


   // Make sure user didn't enter illegally.........

   session = req.getSession(false);  // Get user's session object (no new one)

   if (session == null) {

      invalidUser(out);            // Intruder - reject
      return;
   }

   String user = "";
   boolean ext_login = false;            // not external login (from email link)

   if (req.getParameter("ext-dReq") != null) {   // if from Login for an external login user

       ext_login = true;        // proshop user came from link in email message  (via Login.ProcessExtLogin)
   }
   

   if (ext_login == true) {        // if from an external login (email link)

      user = (String) session.getAttribute("ext-user");       // get this user's username

   } else {

      user = (String) session.getAttribute("user");             // get this user's username
   }
        

   if (!user.startsWith( "proshop" )) {

      invalidUser(out);            // Intruder - reject
      return;
   }


   // Load the JDBC Driver and connect to DB.........

   String club = (String)session.getAttribute("club");   // get club name
//   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   int sess_activity_id = 0;
       
   try {
      con = dbConn.Connect(club);
      
   }
   catch (Exception exc) {
   }

   if (con == null) {

      out.println(SystemUtils.HeadTitle2("ForeTees Billing"));
      out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_customer.css\" />");
      out.println("</head>");
      out.println("<body class=\"serifFont\">");
      out.println("<div id=\"wrapper\">");
      out.println("  <br /><br />");
      out.println("  <h3 class=\"cust_ctr\">Database Connection Error.</h3>");
      out.println("  <br /><br />");
      out.println("  <p class=\"cust_ctr\">Invoicing unable to connect to database error.</p>");
      out.println("  <br /><p class=\"cust_ctr\">Please try again later.</p>");
      out.close();
      return;
   }
   
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club

   // default to all invoice display.
   displayInvoices(con, out, session, club, user, sess_activity_id, clubName, true, ext_login);

   out.close();

 }   // end of doPost

 //
 //  Display summary invoice history for this club page.
 //
 private void displayInvoices(Connection con, PrintWriter out, HttpSession session, String club, String user, int sess_activity_id,
         String clubName, boolean all, boolean ext_login) {
     
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   invoice inv = new invoice();
   invoice_data invData = new invoice_data();
   int idNum = 0;
   int invoiceNumber = 0;
   int amountDue = 0;
   int balance = 0;
   int checkNumber = 0;
   int invStatus = 0;
   String dueDate = "";
   String paidDate = "";
   String checkNumStr = "";
   int choice = (all ? 1 : 2);
   boolean foundInvoice = false;      // output message for no invoices displayed.
   

   //
   // Output top of page.
   //
   out.println(SystemUtils.HeadTitle2("ForeTees Billing"));
   out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_customer.css\" />");
   out.println("</head>");
   out.println("<body class=\"serifFont\">");
   out.println("<div id=\"wrapper\"><div id=\"main\">");
   
   //
   // Check if (invoicing on) for this club.
   //
   try {

       pstmt = con.prepareStatement ( 
            "SELECT invoiceOn FROM invoice_data");

       pstmt.clearParameters();
       rs = pstmt.executeQuery();
       
       if (rs.next()) {
           
           invData.setInvoiceOn(rs.getInt("invoiceOn") >= 1);
       }

   } catch (Exception exc) {
          
       out.println("  <br /><br />");
       out.println("  <h3 class=\"cust_ctr\">Database Error during invoice processing.</h3>");
       out.println("  <br /><br />");
       out.println("  <p class=\"cust_ctr\">Exception message: " +exc.getMessage()+ "</p>");
       out.println("  <br /><p class=\"cust_ctr\">Please try again later.</p>");
       out.close();
       return;
      
   }
   
   // If no invoicing put out a message and return.
   //
   if (!invData.getInvoiceOn()) {
       
       out.println("  <br /><br />");
       out.println("  <h3 class=\"cust_ctr\">Invoicing Not Configured.</h3>");
       out.println("  <br /><br />");
       out.println("  <p class=\"cust_ctr\">Invoicing has not yet been configured for your club. Please contact ForeTees support.</p>");
       out.println("  <br />");
       out.close();
       return;
   }
   
   //
   //   Determine the outstanding balance
   //
   try {

     pstmt = con.prepareStatement ( 
          "SELECT amountDue FROM invoice WHERE invStatus='2'");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();

     while (rs.next()) {

        amountDue = rs.getInt("amountDue");
       
        balance += amountDue;       // tally the balance
     }

   } catch (Exception exc) {

   } finally {

     try { rs.close(); }
     catch (Exception ignore) {}

     try { pstmt.close(); }
     catch (Exception ignore) {}
   }
   

 
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
   
   out.println("<div class=\"cust_ctr\">");
   out.println("<h3>ForeTees Billing Information For<BR>" + clubName + "</h3>");

   
   out.println("<div class=\"main_instructions\">"); 
   out.println("<p>To view or print an invoice simply click on the invoice number below.<br>" +
               "<strong>We appreciate your business!</strong></p>");
   
   if (ext_login == true) {
      
      out.println("<p><strong>NOTE:</strong> &nbsp;You can access this Invoice page any time by logging into ForeTees as a proshop user and " +
                  "selecting Tools - Invoices.</p>");
   }
   out.println("  </div>");
  
   NumberFormat frmter = NumberFormat.getCurrencyInstance();    //  convert int to string - current balance
   frmter.setMinimumFractionDigits(2);
   frmter.setMaximumFractionDigits(2);

   out.println("<h4>Your current outstanding balance is " + frmter.format(balance) + "</h4>");
   
   out.println("  </div>");      
      
   out.println("  <form action=\"Support_invoicing\" method=\"post\" target=\"_blank\" name=frmInvoiceNumDisplay id=frmInvoiceNumDisplay>");
   out.println("    <input type=hidden name=\"showClubInvSubmit\" value=\"Show Invoice\" />");
   out.println("    <input type=hidden name=\"invoiceId\" value=\"0\" />");       // will be updated in javascript.
   out.println("    <input type=hidden name=\"invoiceNumber\" value=\"0\" />");

   out.println("    <table id=\"invoice_display_customer\">");
   out.println("    <thead>");
   out.print("      <tr><th>Date Due</th><th>Invoice #</th><th class=\"amount\">Amount</th><th>Check #</th>");
   out.println("<th>Date Paid</th></tr>");
   out.println("    </thead>");
   out.println("    <tbody>");

   // 
   // Get all invoices from the database and display.
   //
   try {

     pstmt = con.prepareStatement ( 
          "SELECT id, invoiceNumber, invStatus, payDueDate, amountDue, checkNumber, paidDate FROM invoice ORDER BY payDueDate ASC");

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

       //
       // Set status in invoice to check if customer viewable and when displaying paid date setting.
       inv.setStatus(inv.convertIntToStatusType(invStatus));
       if (inv.customerView() ) {

         // Display all customer viewable, or unpaid customer.
         //
         if (all || (inv.getStatus() != invoice.statusType.Paid)) {

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
       }
     }

     if (!foundInvoice) {

         // no invoice was output, so put out a message.
         //
         out.println("      <tr>");
         if (all) {
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
     out.println("    <p class=\"cust_ctr\">");
     out.println("    <br /><b>Exception</b> (Display processing): "+ exc.getMessage());
     out.println("    </p><br /><br />");

     out.println("    <div class=\"cust_ctr\">");
        if (ext_login == false) {
            out.println("<p class=\"general_button\"><a href=\"Proshop_announce\">Home</a></p></div>");
        } else {
            out.println("<p class=\"general_button\"><a href=\"Logout\">Exit</a></p></div>");
        }
     out.println("    </div>");
     out.println("  </div>");
     out.println("  </div>");
     out.close();
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

   out.println("    <div class=\"cust_ctr\">");
   
   if (ext_login == false) {
       
      out.println("<p class=\"general_button\"><a href=\"Proshop_announce\">Home</a></p></div>");

   } else {

      out.println("<p class=\"general_button\"><a href=\"Logout\">Exit</a></p></div>");
   }

   out.println("</div></div>");    // end of main and wrapper

   out.close();

 } // displayInvoices()                
 
  

 // *********************************************************
 //  Common functions for database errors
 // *********************************************************
 
private void displayError(PrintWriter out, HttpSession session, HttpServletRequest req, Connection con,
        String titleStr, String emsg) {

   String club = (String)session.getAttribute("club");               // get name of club
   int sess_activity_id = (Integer)session.getAttribute("activity_id");
   boolean new_skin = ((String)session.getAttribute("new_skin")).equals("1");
   String clubName = SystemUtils.getClubName(con);            // get the full name of this club
 
   String user = (String)session.getAttribute("user");   // get username

    out.println(SystemUtils.HeadTitle("ForeTees Billing"));
    out.println("<body class=\"serifFont\">");
    out.println("<div id=\"wrapper\">");
   
   out.println("  <br /><br /><h3 class=\"lesson_ctr\">Invoicing Error</h3>");
   out.println("  <p class=\"lesson_ctr\">");
   out.println("    <br /><br />Message: " + emsg + ".");
   out.println("  </p>");
   out.close();

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


 // *********************************************************
 // Add XML version and XHTML 1.1 output to data being sent to browser.
 // *********************************************************

 private void addDocTypeHdr(PrintWriter out) {

   out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"");
   out.println("  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
 
 }


}

