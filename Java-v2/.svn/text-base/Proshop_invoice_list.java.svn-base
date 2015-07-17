
/***************************************************************************************
 *   Proshop_invoice_list: This servlet will display a list of the clubs invoices
 *
 *
 *   called by:  Proshop_*** (most servlets as a return to Home)
 *               Proshop Tools Menu (doGet menu=yes)
 *               self (doPost from doGet)
 *               v_/ae.jsp (do Post)
 *
 *
 *   created: 4/03/2015   John K. (Refactored from Proshop_announce)
 *
 *   last updated:            ******* keep this accurate *******
 *
 *       04/03/15   Created
 *
 ***************************************************************************************
 */
import com.foretees.common.Common_skin;
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;

import org.apache.commons.lang.*;
import com.google.gson.*; // for json

import com.foretees.common.ProcessConstants;
import com.foretees.common.Utilities;
import com.foretees.common.Connect;
import com.foretees.common.VerifyUser;
import com.foretees.common.reqUtil;

import com.foretees.api.records.Invoice;
import com.foretees.api.records.Club;
import com.foretees.common.timeUtil;

public class Proshop_invoice_list extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Common_skin.setNoCacheHtml(resp);
        PrintWriter out = resp.getWriter();

        req.setAttribute(ProcessConstants.RQA_RWD, true); // Always uses RWD
        req.setAttribute(ProcessConstants.RQA_PROSHOP_HYBRID, true); // Always force Proshop Hybrid mode

        VerifyUser verify_user = VerifyUser.verifyPro(req);
        if (verify_user.session == null) {
            out.print(verify_user.htmlResponse);
            return;
        }
        
        String club = reqUtil.getSessionString(req, "club", "");
        //String user = reqUtil.getSessionString(req, "user", "");
        int activity_id = reqUtil.getSessionInteger(req, "activity_id", 0);
        boolean show_all = req.getParameter("show_all") != null;
        String list_title = show_all?"Invoices":"Unpaid Invoices";
        
        Common_skin.outputHeader(club, activity_id, "Invoice Listing", true, out, req);
        Common_skin.outputBody(club, activity_id, out, req);
        Common_skin.outputPageStart(club, activity_id, out, req);
        
        out.print("<div class=\"ftInvoiceList\">");
        
        try {
            Club club_record = new Club(club);
            if (club_record.last_error != null) {
                out.print(errorResponse(club_record.last_error));
            }
            List<Invoice> invoice_list;
            if (show_all) {
                invoice_list = Invoice.getSentVisibleToProByClubId(club_record.id);
            } else {
                invoice_list = Invoice.getUnpaidVisibleToProByClubId(club_record.id);
            }

            out.print("<table class=\"ftClubInvoiceListing standard_list_table rwdTable\"><caption><h2>" + list_title + "</h2></caption>");
            out.print("<thead><tr><th>Invoice#</th><th>Date</th><th>Date Due</th><th>Days Past Due</th><th>Amount Due</th><th>Status</th><th></th></tr></thead>");
            out.print("<tbody>");
            List<String> rowClass; 
            String status;
            for (Invoice invoice : invoice_list) {
                rowClass = new ArrayList<String>();
                
                if (invoice.amount_due > 0) {
                    rowClass.add("ftInvoiceUnpaid");
                    status = "Unpaid";
                } else {
                    rowClass.add("ftInvoicePaid");
                    status = "Paid";
                }
                if (invoice.days_past_due > 0) {
                    rowClass.add("ftInvoicePastDue");
                    status = "Past Due";
                }
                out.print("<tr class=\"" + Utilities.implode(rowClass, " ") + "\">");
                out.print("<td class=\"ftInvoiceNumber\">" + invoice.id + "</td>");
                out.print("<td class=\"ftInvoiceDate\">" + timeUtil.formatTzDate(invoice.date, "M/d/yyyy") + "</td>");
                out.print("<td class=\"ftInvoiceDueDate\">" + timeUtil.formatTzDate(invoice.due_date, "M/d/yyyy") + "</td>");
                out.print("<td class=\"ftInvoicePastDue\">" + invoice.days_past_due + "</td>");
                out.print("<td class=\"ftInvoiceAmountDue\">$" + String.format("%,.2f", invoice.amount_due) + "</td>");
                out.print("<td class=\"ftInvoiceStatus\">" + status + "</td>");
                out.print("<td class=\"ftInvoiceView\"><a href=\"#\" data-ftinvoiceid=\"" + invoice.id + "\" class=\"ftViewInvoiceButon standard_button\">View Invoice</a></td>");
                out.print("</tr>");
            }
            
            out.print("</tbody>");
            
            if (invoice_list.isEmpty()) {
                out.print("<tfoot class=\"invoiceListEmpty\"><tr><td colspan=\"7\">No " + list_title + " Found</td></tr></tfoot>");
            }
            
            out.print("</table>");

            
        } catch (Exception e) {
            Connect.logError("Proshop_invoice_list.doGet: Err=" + org.apache.commons.lang.exception.ExceptionUtils.getStackTrace(e));
            out.print(errorResponse(e.toString()));
        }
        
        out.print("<div class=\"ftInvoiceSelectMode\"><a class=\"standard_button\" href=\"Proshop_invoice_list"+(show_all?"":"?show_all")+"\">"+(show_all?"Show Unpaid Invoices":"Show All Invoices")+"</a></div>");
        
        out.print("</div>");
        
        Common_skin.outputPageEnd(club, activity_id, out, req);
        
        out.close();

    }  // end of doGet
    
    private static String errorResponse(String error){
        StringBuilder result = new StringBuilder();
        result.append("<div class=\"ApiError\"></div>");
        result.append(error);
        result.append("</div>");
        return result.toString();
    }
}
