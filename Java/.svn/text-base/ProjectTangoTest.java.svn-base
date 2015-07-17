
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.math.*;
import java.net.URL;
import javax.jws.WebService;
import javax.xml.ws.WebServiceRef;

import org.tempuri.*;

public class ProjectTangoTest extends HttpServlet {
 
 static IntegrationServiceSoap service;


 public void doGet(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {


    resp.setContentType("text/html;charset=UTF-8");
    PrintWriter out = resp.getWriter();

    out.println("<br><table border=0 align=center><tr><td align=center colspan=3><font size=4><b>ProjectTangoTesting Tool</b></font></td></tr><tr><td>");
    
    out.println("<br><table border=1><tr bgcolor=silver><td align=center>addSaleOnlineV2</td></tr><tr><td>");

    try { // Call Web Service Operation

        // this is their main object
        org.tempuri.IntegrationService service = new org.tempuri.IntegrationService();

        // their port is the getIntegrationServiceSoap object
        org.tempuri.IntegrationServiceSoap port = service.getIntegrationServiceSoap();

        // these two object contain the charge codes and the qty of each
        ArrayOfString code = new ArrayOfString();
        ArrayOfInt qty = new ArrayOfInt();

        // populate the objects with the respective charge codes and qty for each
        code.getString().add("GF18");
        qty.getInt().add(2);

        code.getString().add("Cart18");
        qty.getInt().add(1);

        code.getString().add("Caddie18");
        qty.getInt().add(1);

        code.getString().add("Cart18Guest");
        qty.getInt().add(1);

        code.getString().add("Caddie18Guest");
        qty.getInt().add(1);

        String acct = "00002"; // this is the test member account number (pos id)

        // the result oject will contain the response we receive
        org.tempuri.OnlineSaleResult result = port.addSaleOnlineV2(acct, code, qty);

        // output are result for visual display
        out.println("<br>Result = "+result);
        out.println("<br>ReturnText = "+result.getReturnText());
        out.println("<br>getSubTotal = "+result.getSubTotal());
        out.println("<br>getTax = "+result.getTax());
        out.println("<br>getTotal = "+result.getTotal());
        out.println("<br>getSaleNumber = "+result.getSaleNumber());
        out.println("<br>");

    } catch (Exception ex) {
        out.println("<p>Error: " + ex.toString() + "<br>Message: " + ex.getMessage() + "</p>");
    }

    out.println("</td></tr></table>");


    out.println("</td><td>&nbsp; &nbsp;</td><td>");


    out.println("<br><table border=1><tr bgcolor=silver><td align=center>addSaleOnlineV2WithNotes</td></tr><tr><td>");

    try { // Call Web Service Operation

        // this is their main object
        org.tempuri.IntegrationService service = new org.tempuri.IntegrationService();

        // their port is the getIntegrationServiceSoap object
        org.tempuri.IntegrationServiceSoap port = service.getIntegrationServiceSoap();

        // these two object contain the charge codes and the qty of each
        ArrayOfString code = new ArrayOfString();
        ArrayOfInt qty = new ArrayOfInt();

        // populate the objects with the respective charge codes and qty for each
        code.getString().add("GF18");
        qty.getInt().add(2);

        code.getString().add("Cart18");
        qty.getInt().add(1);

        code.getString().add("Caddie18");
        qty.getInt().add(1);

        code.getString().add("Cart18Guest");
        qty.getInt().add(1);

        code.getString().add("Caddie18Guest");
        qty.getInt().add(1);

        String acct = "00002"; // this is the test member account number (pos id)
        String notes = "These are the notes. These notes are 55 charactes long.";

        // the result oject will contain the response we receive
        org.tempuri.OnlineSaleResult result = port.addSaleOnlineV2WithNotes(acct, code, qty, notes);

        // output are result for visual display
        out.println("<br>Result = "+result);
        out.println("<br>ReturnText = "+result.getReturnText());
        out.println("<br>getSubTotal = "+result.getSubTotal());
        out.println("<br>getTax = "+result.getTax());
        out.println("<br>getTotal = "+result.getTotal());
        out.println("<br>getSaleNumber = "+result.getSaleNumber());
        out.println("<br>");

    } catch (Exception ex) {
        out.println("<p>Error: " + ex.toString() + "<br>Message: " + ex.getMessage() + "</p>");
    }

    out.println("</td></tr></table>");

    out.println("</td></tr></table>");


 } // end of doGet routine

/*
 *
 * This method doesn't work....
 *
 *
 public void doPost(HttpServletRequest req, HttpServletResponse resp)
    throws ServletException, IOException {

    resp.setContentType("text/html");
    PrintWriter out = resp.getWriter();

    AddSaleOnline addSale = new AddSaleOnline();

    addSale.setAcct("00001");
    addSale.setGreenFeeCode("GF18");
    addSale.setGreenFeeQty(1);
    addSale.setCartFeeCode("Cart18");
    addSale.setCartFeeQty(1);
    addSale.setCaddieFeeCode("Caddie18");
    addSale.setCaddieFeeQty(1);

    OnlineSaleResult result = new OnlineSaleResult();

    //IntegrationService service = new IntegrationService();

    //IntegrationServiceSoap soap = service.getIntegrationServiceSoap();

    //result = IntegrationServiceSoap.addSaleOnline("00001","GF18",1,"Cart18",1,"Caddie18",1,"",0,"",0);

    service = new IntegrationServiceSoap();

    //result = service.addSaleOnline("00001","GF18",1,"Cart18",1,"Caddie18",1,"",0,"",0);


    //IntegrationServiceSoap soap = service.getIntegrationServiceSoap(addSale);//addSale

    //OnlineSaleResult result = service.getIntegrationServiceSoap();

  //service.getPort();

  //URL url = service.getWSDLDocumentLocation();

    String txt = result.getReturnText();
    String txt2 = result.getSaleNumber();
    BigDecimal subtotal = result.getSubTotal();

  //out.println("<br>URL="+url.toString());
    out.println("<br>getReturnText="+txt);
    out.println("<br>getSaleNumber="+txt2);
    out.println("<br>getSubTotal="+subtotal);

    out.close();

 } // end of doPost routine
*/
} // end servlet public class