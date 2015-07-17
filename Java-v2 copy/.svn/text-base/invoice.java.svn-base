/***************************************************************************************
 *   invoice:  This servlet will provide invoice data members and processing methods.
 *
 *   called by:  Support_invoicing
 *
 *   created:  11/16/2011   Tim K.
 *
 *   last updated:
 *
 *      3/14/12  Correct UPDATE statement in updateFullInvoiceToDb(). Add updAfterCreate2(). Add getDueDateCal() and setDueDateCal().
 *      3/09/12  Remove Rep initials on printable invoice, move frequency of billing to invoice_data.java.
 *      3/01/12  Add getAmountDueStr(), customerView(), setStatus(), and change getPaidDate().
 *      2/24/12  Add check number to invoice. Change club5 invoice references to new table, invoice_data.
 *      2/16/12  Add updatePending() function processing. Update print invoice to use table css definitions.
 *
 *
 ***************************************************************************************
 */


//package com.foretees.common;

import java.io.*;
import java.text.NumberFormat;
import java.sql.*;
import javax.servlet.*;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;


public class invoice {
    
    //************************************************************************
    //
    //  Description: The initial invoice object will just set invoice fields
    //      to canned data and does not access the database.
    //
    //  There are two ways update a full invoice: 
    //      1) create() - from provided invoice data, write to database.
    //      2) retrieve() - read a specified (current) invoice in the "club" table from the "invoice" table.
    //
    //      An interface will be provided to update specified invoice fields. 
    //
    //************************************************************************
 
    //
    // Invoice status and billing rate enums
    //  
    public enum statusType {Unknown, Paid, Pending, AckReceipt, AckPayment, New;}
    public enum billingRateTyp {None, Member, MShip, Setup; }
    
    // Invoice data fields.
    private int id;
    private int invoiceNumber;
    private Calendar dueDate;
    private Calendar generateDate;
    private Calendar paidDate;
    private statusType status;
    private billingRateTyp billingRate;
    private int totalCnt;                 // Member or membership counts
    private int inactCnt;
    private int excludedCnt;
    private int billableCnt;
    private int amountDue;  
    private int checkNumber;
   
    private String yrStr;                // Date parsing variables.
    private String monStr;
    private String dayStr;
    private int yr, mon, day;

    public static final int SUCCESS = 0;      // return values on function calls
    public static final int FAILURE = -1;
 
    
public invoice () {

   // Initialize the invoice. These will change depending on whether this is
   //   a new invoice or an active invoice to be read from the database.
   // 
   status = statusType.Unknown;
   id = 0; 
   invoiceNumber = 0;
   
   // Set all dates to a valid (note all zeroes not valid) initial date, Jan 5, 2001.
   dueDate = new GregorianCalendar (2001, Calendar.JANUARY, 5);
   generateDate = new GregorianCalendar (2001, Calendar.JANUARY, 5);   
   paidDate = new GregorianCalendar (2001, Calendar.JANUARY, 5); 

   totalCnt = 0;
   inactCnt = 0;
   excludedCnt = 0;
   billableCnt = 0;
       
   billingRate = billingRateTyp.None;
   amountDue = 0;
   checkNumber = 0;

}
  
public void create (int invNum, boolean resetDateDue, billingRateTyp bRate, int totCnt,
        int iaCnt, int exclCnt, int billCnt, 
        Connection con, boolean insertF)  throws Exception {

   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   //
   // Create an invoice with input from caller.
   // 
   status = statusType.New;              // default to new.
   id = 0;                               // set when added to db.
   invoiceNumber = invNum;

   // Check for reset of due date from invoice_data table.
   if (resetDateDue){
       
       getNextDueDate(con);
   }
   
   generateDate = new GregorianCalendar ();    // set to current date.

   totalCnt = totCnt;
   inactCnt = iaCnt;
   excludedCnt = exclCnt;
   billableCnt = billCnt;
       
   billingRate = bRate;
   amountDue = billCnt * getBillRate();

   if (insertF) {
       try {
           insertInvoiceToDb(con);
       } catch (Exception exc){
           
         throw (new Exception("invoice: create(), Exception: " + exc.getMessage()) );
           
       }
   }
}

private void getNextDueDate(Connection con) throws Exception {

   PreparedStatement pstmt = null;
   ResultSet rs = null;
   

   try {

       // Get next invoice due date for this invoice.
       pstmt = con.prepareStatement ( "SELECT nextDueDate FROM invoice_data");

       pstmt.clearParameters();
       rs = pstmt.executeQuery();
       if (rs.next()) {
   
           parseInvoiceDate(rs.getString("nextDueDate"));
           dueDate.set(yr, mon, day);
       }        

   } catch (Exception e) {

         throw (new Exception("invoice: getNextDueDate(), Exception: " + e.getMessage()) );
      
     } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

   }
    
}


public void updAfterCreate(Connection con, int invNum, int blOverride)
        throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects

   //
   //  Update invoice number and check if billable count needs override.
   //
   invoiceNumber = invNum;
   
   try {

     if (blOverride > 0) {
         
       billableCnt = blOverride;
       amountDue = blOverride * getBillRate();
       
       pstmt1 = con.prepareStatement("UPDATE invoice SET invoiceNumber = " + invoiceNumber
           + ", billableCnt = " + billableCnt + ", amountDue = " + amountDue +
             " WHERE id = " + id); 
     }
     else {
       pstmt1 = con.prepareStatement("UPDATE invoice SET invoiceNumber = " + invoiceNumber
           + " WHERE id = " + id);     
     }
     
     pstmt1.clearParameters();
     pstmt1.executeUpdate();
                 
   }
   catch (Exception exc) {

      throw (new Exception("invoice: updAfterCreate(), Exception: " + exc.getMessage()) );
   }

}

//
// Update database after save on Setup fee.
//
public void updAfterCreate2(Connection con, int invNum, int setupFee)
        throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects

   //
   //  Update invoice number, billing rate, and amount due.
   //
   invoiceNumber = invNum;
   billingRate = billingRateTyp.Setup;
   amountDue = setupFee;
   
   try {
         
       
       pstmt1 = con.prepareStatement("UPDATE invoice SET invoiceNumber = " + invoiceNumber
           +  ", amountDue = " + amountDue +
             " WHERE id = " + id); 
     
     pstmt1.clearParameters();
     pstmt1.executeUpdate();
        
         
   }
   catch (Exception exc) {

      throw (new Exception("invoice: updAfterCreate2(), Exception: " + exc.getMessage()) );
   }

}

public void updatePaid(Connection con, int yr, int mon, int day, int chkNum)
        throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects
   String paidDstr = "";

   //
   //  Update check number and paid date variables.
   //
   checkNumber = chkNum;
   setInvoiceDate(paidDate, yr, mon, day);
   paidDstr = createInvoiceDate(paidDate);
   
   try {

       pstmt1 = con.prepareStatement("UPDATE invoice SET paidDate = '" + paidDstr + "', checkNumber = " + checkNumber
           + " WHERE id = " + id); 
     
       pstmt1.clearParameters();
       pstmt1.executeUpdate();        
         
   }
   catch (Exception exc) {

      throw (new Exception("invoice: updatePaid(), Exception: " + exc.getMessage()) );
   }

}

public void updatePending (int invNum, boolean resetDateDue, billingRateTyp bRate, int totCnt,
        int iaCnt, int exclCnt, int billCnt, 
        Connection con, boolean insertF)  throws Exception {

   // Update an invoice with input from caller.
   // 
   status = statusType.New;              // default to new.
   invoiceNumber = invNum;
   
   if (resetDateDue) {
       
       getNextDueDate(con);
   }
   
   generateDate = new GregorianCalendar ();    // set to current date.

   totalCnt = totCnt;
   inactCnt = iaCnt;
   excludedCnt = exclCnt;
   billableCnt = billCnt;
       
   billingRate = bRate;
   amountDue = billCnt * getBillRate();

   if (insertF) {
       try {
           updateFullInvoiceToDb(con);
       } catch (Exception exc){
           
         throw (new Exception("invoice: update(), Exception: " + exc.getMessage()) );
           
       }
   }
}


public void retrieve (Connection con)  throws Exception {

   // Get the current invoice ID from invoice_data, then update invoice with current invoice.
   // 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   int invId = 0;
   int freq = 0;
    
   try {

     // Get current invoice data.
     pstmt = con.prepareStatement ( "SELECT currInvoice, frequency FROM invoice_data");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next()) {
         invId = rs.getInt("currInvoice");
//         freq = Integer.parseInt( rs.getString("frequency") );
//         frequency = convertIntToFreqType(freq);
     }        
     
     //
     // Go get invoice values.
     //
     retrieve(con, invId);

   } catch (Exception e) {

       throw (new Exception("invoice: retrieve() - id from invoice_data, Exception: " + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}


public void retrieve (Connection con, int invId)  throws Exception {

   //
   // Update this invoice with the database invoice for the ID specified.
   // 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   id = invId;
    
   try {

     //
     // Get invoice values.
     //
     pstmt = con.prepareStatement ( 
             "SELECT invoiceNumber, invStatus, billingRate, totalCnt, "
             + "inactCnt, excludedCnt, billableCnt, dateCreated, "
             + "payDueDate, paidDate, amountDue, checkNumber FROM invoice WHERE id=" +id);
     
     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next()) {
       invoiceNumber = rs.getInt("invoiceNumber");
       totalCnt = rs.getInt("totalCnt");
       inactCnt = rs.getInt("inactCnt");
       excludedCnt = rs.getInt("excludedCnt");
       billableCnt = rs.getInt("billableCnt");
       
       status = convertIntToStatusType( rs.getInt("invStatus") );
       billingRate = convertIntToBRateType( rs.getInt("billingRate") );
       parseInvoiceDate( rs.getString("dateCreated") );
       generateDate.set(yr, mon, day);
       parseInvoiceDate( rs.getString("payDueDate") );
       dueDate.set(yr, mon, day);
       parseInvoiceDate( rs.getString("paidDate") );
       paidDate.set(yr, mon, day);
       amountDue = rs.getInt("amountDue");
       checkNumber = rs.getInt("checkNumber");
 
     }

   } catch (Exception e) {

       throw (new Exception("invoice: retrieve() requested invoice, Exception: " + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}

/**
 //************************************************************************
 //
 //  Database updating functions.
 //
 //************************************************************************
 **/
public void insertInvoiceToDb(Connection con)  throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects
   ResultSet rs = null;
   String genDstr = "", dueDstr = "", paidDstr = "";

   //
   //  Insert new invoice into database
   //
   try {

     // create date strings.
     //
     genDstr = createInvoiceDate(generateDate);
     dueDstr = createInvoiceDate(dueDate);
     paidDstr = createInvoiceDate(paidDate);
     
     pstmt1 = con.prepareStatement("INSERT INTO invoice (invoiceNumber, invStatus, "
           + "dateCreated, totalCnt, inactCnt, excludedCnt, billingRate, billableCnt, "
           + "payDueDate, paidDate, amountDue, checkNumber)"
           + " VALUES (" +invoiceNumber+ ", '" + convertStatusToInt(status) + "', '" 
           + genDstr + "', " +totalCnt+ ", " +inactCnt+ ", " +excludedCnt
           + ", '" + convertBrateToInt(billingRate)
           + "', " +billableCnt+ ", '" + dueDstr + "', '" + paidDstr + "', " + amountDue + ", " + checkNumber + ")");
   

     pstmt1.clearParameters();
     pstmt1.executeUpdate();
         
     // get invoice id and put in club table.
     pstmt1 = con.prepareStatement("SELECT MAX(id) FROM invoice");
     pstmt1.clearParameters();
     rs = pstmt1.executeQuery();
     if (rs.next())
         id = rs.getInt(1);

//     pstmt1 = con.prepareStatement(
//          "UPDATE invoice_data SET currInvoice = " + id + ", frequency = '" + 
//             convertFreqToInt(frequency) +"'");
     pstmt1 = con.prepareStatement(
          "UPDATE invoice_data SET currInvoice = " + id );
     pstmt1.clearParameters();
     pstmt1.executeUpdate();
   }
   catch (Exception exc) {

      throw (new Exception("insertInvoiceToDb(), " + exc.getMessage()) );
   }

}

public void updateFullInvoiceToDb(Connection con) throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects
   String genDstr = "", dueDstr = "", paidDstr = "";

   //
   //  Update full invoice into database
   //
   try {
       
     // create date strings.
     //
     genDstr = createInvoiceDate(generateDate);
     dueDstr = createInvoiceDate(dueDate);
     paidDstr = createInvoiceDate(paidDate);

     pstmt1 = con.prepareStatement("UPDATE invoice SET invoiceNumber = " + invoiceNumber
           + ", invStatus = '" + convertStatusToInt(status) + "', dateCreated = '" 
           + genDstr + "', totalCnt = " +totalCnt+ ", inactCnt = " + inactCnt
           + ", excludedCnt = " + excludedCnt + ", billingRate = '"
           + convertBrateToInt(billingRate) + "', billableCnt = " + billableCnt 
           + ", payDueDate = '" + dueDstr + "', paidDate = '" + paidDstr + "', amountDue = " + amountDue 
           + ", checkNumber = " + checkNumber + " WHERE id = " + id); 

     pstmt1.clearParameters();
     pstmt1.executeUpdate();
     
         
   }
   catch (Exception exc) {

      throw (new Exception("updateFullInvoiceToDb(), " + exc.getMessage()) );
   }

}

public void updateFieldToDb(Connection con, String fieldStr)   throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects
   
   //
   //  Update specified invoice field(s) into database
   //
   try {

     pstmt1 = con.prepareStatement("UPDATE invoice SET " + fieldStr +
           " WHERE id = " + id);

     pstmt1.clearParameters();
     pstmt1.executeUpdate();
     
   }
   catch (Exception exc) {

      throw (new Exception("updateFieldInvoiceToDb(), " + exc.getMessage()) );
   }

}


/**
 //************************************************************************
 //
 //  Status field functions.
 //
 //************************************************************************
 **/

public statusType convertIntToStatusType(int stNum) {
    
    statusType stTyp = statusType.Unknown;
    
    // Convert int to status type.
    switch (stNum) {
      case 1: stTyp = statusType.Paid;
          break;
      case 2: stTyp = statusType.Pending;
          break;
      case 3: stTyp = statusType.AckReceipt;
          break;
      case 4: stTyp = statusType.AckPayment;
          break;
      case 5: stTyp = statusType.New;
          break;
      case 0: 
      default: stTyp = statusType.Unknown;
          break;
    }
    
    return (stTyp);
}

public int convertStatusToInt(statusType stTyp) {
    
    int stVal = 0;
    
    // Convert status type to int.
    switch (stTyp) {
      case Paid: stVal = 1;
          break;
      case Pending: stVal = 2;
          break;
      case AckReceipt: stVal = 3;
          break;
      case AckPayment: stVal = 4;
          break;
      case New: stVal = 5;
          break;
      case Unknown: 
      default: stVal = 0;
          break;
    }
    
    return (stVal);
}


public String getStatusStr() {
   
    String statusStr = "";
    
    // Get the status string.
    switch (status) {
        case Paid:    statusStr = "Paid";
            break;
        case Pending: statusStr = "Pending";
            break;
        case AckReceipt: statusStr = "ACK Receipt";
            break;
        case AckPayment: statusStr = "ACK Payment";
            break;
        case New:     statusStr = "New";
            break;
        case Unknown: 
        default:      statusStr = "Unknown";
            break;
    }
    
    return (statusStr);
}


/**
 //************************************************************************
 //
 //  Billing Rate field functions.
 //
 //************************************************************************
 **/

public billingRateTyp convertIntToBRateType(int brNum) {
    
    billingRateTyp brTyp = billingRateTyp.None;
    
    // Convert int to billing rate type.
    switch (brNum) {
      case 1: brTyp = billingRateTyp.Member;
          break;
      case 2: brTyp = billingRateTyp.MShip;
          break;
      case 3: brTyp = billingRateTyp.Setup;
          break;
      case 0: 
      default: brTyp = billingRateTyp.None;
          break;
       }
    
    return (brTyp);
}

public int convertBrateToInt(billingRateTyp brTyp) {
    
    int brate = 0;
    
    // Convert billing rate type to int.
    switch (brTyp) {
      case Member: brate = 1;
          break;
      case MShip: brate = 2;
          break;
      case Setup: brate = 3;
          break;
      case None: 
      default: brate = 0;
          break;
       }
    
    return (brate);
}


private int getBillRate() {
    
    int bRate = 0;
    
    // Get Billing Rate string.
    switch (billingRate) {
        case Member: bRate = 10;
            break;
            
        case MShip: bRate = 20;
            break;
            
        case None:
        case Setup: 
        default: 
            break;
       }
    
    return (bRate);
}

public String getBillRateStr() {
    
    String billRateStr = "";
    
    // Get Billing Rate string.
    switch (billingRate) {
        case Member: billRateStr = "Member ($10)";
            break;
            
        case MShip: billRateStr = "MShip ($20)";
            break;
            
        case Setup: billRateStr = "Setup";
            break;
            
        case None: 
        default: billRateStr = "None";
            break;
       }
    
    return (billRateStr);
}

public billingRateTyp getBillingRateType() {
    
    
    return (billingRate);
}


//
// Parse date from database to store in Calendar class.
//
private void parseInvoiceDate(String dateStr) {
    
    int start, end;
    
    if (dateStr == null)        // return if null string.
        return;
    
    start = 0;
    end = dateStr.indexOf("-");
    yrStr = dateStr.substring(start, end);
    
    start = end + 1;
    end = dateStr.indexOf("-", start);
    monStr = dateStr.substring(start, end);
    
    dayStr = dateStr.substring(end + 1);
    
    // Get integer values and plug into date values.
    yr = Integer.parseInt(yrStr);
    mon = Integer.parseInt(monStr) - 1;         // adjust month
    day = Integer.parseInt(dayStr);
    
}

//
// Create date from Calendar to store in database.
//
private String createInvoiceDate(Calendar calDate) {
    
    
    // Get integer values and plug into date values.
    yr = calDate.get(Calendar.YEAR);
    mon = calDate.get(Calendar.MONTH) + 1;     // adjust month
    day = calDate.get(Calendar.DATE);
    
    return (yr + "-" + mon + "-" + day);
    
}

//
// Type parmater: type = 1,  mm-dd-yyyy,
//                type = 2,  mm/dd/yyyy,
//                type = 3,  month dd, yyyy, ex. January 22, 2012
//                type = 4,  mon dd, yyyy, ex. Jan 22, 2012
//
//
private String formatDate(Calendar calDate, int type) {
    
    String dateStr = "";
    
    // Get integer values and plug into date values.
    yr = calDate.get(Calendar.YEAR);
    mon = calDate.get(Calendar.MONTH) + 1;     // adjust month
    day = calDate.get(Calendar.DATE);
    
    switch (type) {
        
        case 1:
        default:  dateStr = mon + "-" + day + "-" + yr;
            break;
            
        case 2:  dateStr = mon + "/" + day + "/" + yr;
            break;
            
        case 3:  dateStr = getMonth(mon-1) + " " + day + ", " + yr;
            break;
            
        case 4:  dateStr = getMonth2(mon-1) + " " + day + ", " + yr;
            break;
    }
    
    return (dateStr);
    
}


//
// Set date fields for the date passed.
//
private void setInvoiceDate(Calendar calDate, int yr, int mon, int day) {
    
    // Update respective fields with parameters passed.
    calDate.set(Calendar.YEAR, yr);
    calDate.set(Calendar.MONTH, mon - 1);
    calDate.set(Calendar.DATE, day);
    
}

/**
 //************************************************************************
 //
 //  Get single field functions.
 //
 //************************************************************************
 **/
public int getId() {    return (id); }

public int getInvoiceNumber() {    return (invoiceNumber); }

public String getDueDate() {
    String dstr = createInvoiceDate (dueDate);
    return (dstr);
}

public String getDueDate(int type) {
    String dstr = formatDate (dueDate, type);
    return (dstr);
}

public Calendar getDueDateCal() {
    return (dueDate);
}

public String getGenerateDate() {
    String dstr = createInvoiceDate (generateDate);
    return (dstr);
}

public String getGenerateDate(int type) {
    String dstr = formatDate (generateDate, type);
    return (dstr);
}

public String getPaidDate(boolean onlyDate) {
    
    String dstr = createInvoiceDate (paidDate);
    
    //
    // The waiting flag will be used to display "waiting" for the paid date on a new invoice or an outstanding invoice.
    //
    if (!onlyDate) {
      
        switch (status) {
            case Paid:    // use date string.
                break;
                
            case Pending:
            case AckReceipt:
            case AckPayment:
                dstr = "Outstanding";
                break;
                
            case New:
            case Unknown:
            default:
                dstr = "New";
                break;
        }
    }
    
    return (dstr);
}

public String getPaidDate(boolean onlyDate, int type) {
    
    String dstr = formatDate (paidDate, type);
    
    //
    // The onlyDate flag will be used for the return of paid date, otherwise process paid date
    // based on the invoice status.
    //
    if (!onlyDate) {
      
        switch (status) {
            case Paid:    // use date string.
                break;
                
            case Pending:
            case AckReceipt:
            case AckPayment:
                dstr = "Outstanding";
                break;
                
            case New:
            case Unknown:
            default:
                dstr = "New";
                break;

        }
    }
    
    return (dstr);
}

public statusType getStatus() {    return (status); }

public int getTotalCnt() {    return (totalCnt); }

public int getInactCnt() {    return (inactCnt); }

public int getExcludedCnt() {    return (excludedCnt); }

public int getBillableCnt() {    return (billableCnt); }

public int getAmountDue() {    return (amountDue); }

public String getAmountDueStr() {
    
    NumberFormat frmter = NumberFormat.getCurrencyInstance();
    frmter.setMinimumFractionDigits(2);
    frmter.setMaximumFractionDigits(2);
    String amountDueStr = frmter.format(amountDue);

    return (amountDueStr);
}

public int getCheckNumber() {    return (checkNumber); }


//
// Calendar functions to add month to invoice description.
//
public String getStartMonth() {
    
    int month = dueDate.get(Calendar.MONTH);
    return (getMonth(month) );
}

public String getEndMonth() {
    
    int month = dueDate.get(Calendar.MONTH);
    month--;
    if (month < 0) {
        
        month = 11;       // reset to December (0-based).
    }
    return (getMonth(month) );
}

//
// parameter month is zero-based.
//
public String getMonth (int month) {
  
    String monthStr = "";
    
    switch (month) {
        default:
        case 0: monthStr = "January"; break;
        case 1: monthStr = "February"; break;
        case 2: monthStr = "March"; break;
        case 3: monthStr = "April"; break;
        case 4: monthStr = "May"; break;
        case 5: monthStr = "June"; break;
        case 6: monthStr = "July"; break;
        case 7: monthStr = "August"; break;
        case 8: monthStr = "September"; break;
        case 9: monthStr = "October"; break;
        case 10: monthStr = "November"; break;
        case 11: monthStr = "December"; break;
    }
    
    return (monthStr);
}

//
// Abbreviated month text, parameter month is zero-based.
//
public String getMonth2 (int month) {
  
    String monthStr = "";
    
    switch (month) {
        default:
        case 0: monthStr = "Jan"; break;
        case 1: monthStr = "Feb"; break;
        case 2: monthStr = "Mar"; break;
        case 3: monthStr = "Apr"; break;
        case 4: monthStr = "May"; break;
        case 5: monthStr = "Jun"; break;
        case 6: monthStr = "Jul"; break;
        case 7: monthStr = "Aug"; break;
        case 8: monthStr = "Sep"; break;
        case 9: monthStr = "Oct"; break;
        case 10: monthStr = "Nov"; break;
        case 11: monthStr = "Dec"; break;
    }
    
    return (monthStr);
}


/**
 //************************************************************************
 //
 //  Set single field functions.
 //
 //************************************************************************
 **/
public int setId(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        id = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setInvoiceNumber(int val) { 
    
    int rc = FAILURE;    
    if (val >= 0) {
        invoiceNumber = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setStatus(statusType styp) { 
    
    int rc = SUCCESS;    

    status = styp;
    
    return (rc);
}

public int setStatus(Connection con, statusType styp) { 
    
    int rc = FAILURE;    

    status = styp;
    try {
        
       updateFieldToDb(con, "invStatus = '" + convertStatusToInt(status) + "'");
       rc = SUCCESS;
       
    } catch (Exception exc) {
        
        
    }
    
    return (rc);
}

public int setBillingRate(billingRateTyp btyp) { 
    
    int rc = FAILURE;    

    billingRate = btyp;
    rc = SUCCESS;
    
    return (rc);
}

//
// More fields, set dates
//
public int setDueDate(String dateVal) {
    
    int rc = FAILURE;    
    if (dateVal.length() > 0) {
        parseInvoiceDate(dateVal);
        dueDate.set(yr, mon, day);
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setDueDateCal(Calendar cal) {
       
    dueDate = cal;   
    return (SUCCESS);
}

public int setGeneratDate(String dateVal) {
    
    int rc = FAILURE;    
    if (dateVal.length() > 0) {
        parseInvoiceDate(dateVal);
        generateDate.set(yr, mon, day);
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setPaidDate(String dateVal) {
    
    int rc = FAILURE;    
    if (dateVal.length() > 0) {
        parseInvoiceDate(dateVal);
        paidDate.set(yr, mon, day);
        rc = SUCCESS;
    }
    
    return (rc);
}

//
// Set counts
//
public int setTotalCnt(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        totalCnt = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setInactCnt(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        inactCnt = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setExcludedCnt(int val) { 
    
    int rc = FAILURE;    
    if (val >= 0) {
        excludedCnt = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setBillableCnt(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        billableCnt = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setAmountDue(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        amountDue = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setCheckNumber(int val) {
    
    int rc = FAILURE;    
    if (val >= 0) {
        checkNumber = val;
        rc = SUCCESS;
    }
    
    return (rc);
}

 //
 //  Display club invoice page.
 //
 public void displayClubInvoice(Connection con, PrintWriter out, String rev) throws Exception {
 
   NumberFormat frmter = NumberFormat.getInstance();
   frmter.setMinimumFractionDigits(2);
   frmter.setMaximumFractionDigits(2);
   String amountDueStr = frmter.format(amountDue);
   
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   int year = dueDate.get(Calendar.YEAR);
   String clubName = SystemUtils.getClubName(con);
   String addr1 = "";
   String addr2 = "";
   String cityStr = "";
   String stateStr = "";
   String zip = "";
   
   addDocTypeHdr(out);
   out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
   out.println("<head><title>Current Invoice</title>");
   out.println("  <link rel=\"stylesheet\" type=\"text/css\" href=\"/" +rev+ "/web utilities/support_customer.css\" />");
   out.println("</head>");
   out.println("<body class=\"print_invoice\">");
   out.println("  <div id=\"print_wrapper\">");
   out.println("  <div id=\"content\">");

   try {

       // Get new invoice due date for this invoice.
       pstmt = con.prepareStatement ( "SELECT address1, address2, city, state, zipcode FROM invoice_data");

       pstmt.clearParameters();
       rs = pstmt.executeQuery();
       if (rs.next()) {
   
           addr1 = rs.getString("address1");
           
           if (rs.getString("address2") != null) {
               addr2 = rs.getString("address2");
           }
           cityStr = rs.getString("city");
           stateStr = rs.getString("state");
           zip = rs.getString("zipcode");

       }        


   out.println("    <div id=\"topleft_club_inv\">");
   out.println("    <div id=\"return_addr\">");
   out.println("      ForeTees LLC<br />");   
   out.println("      550 Village Center Drive<br />");
   out.println("      Suite 400<br />");
   out.println("      St. Paul, MN 55127-3004<br />");
   out.println("      651-486-0715<br /><br />");
   out.println("      TIN #: 03-0493751<br />");
   out.println("    </div>");
   out.println("    <table id=\"invoice_club_addr\">");
   out.println("    <tbody>");
   out.println("      <tr><td class=\"shade\">Bill To:</td>");
   out.println("      </tr><tr>");
   out.println("        <td>" + clubName + "<br />");
   out.println("        " + addr1 + "<br />");
   
   if (addr2.length() > 0) {
       out.println("        " + addr2 + "<br />");
   }
   out.println("        " + cityStr + ", " + stateStr + " " + zip +"<br /><br />");
   out.println("        Attn: Accounts Payable");
   out.println("        </td>");
   out.println("      </tr>     <!-- tblClubAddr -->");
   out.println("      </tbody>");
   out.println("      </table>     <!-- invoice_club_addr -->");
   out.println("    </div>    <!-- topleft_club_inv -->");
 
   out.println("    <div id=\"topright_club_inv\">");
   out.println("    <div id=\"ft_logo\">");
   out.println("      <img class=\"logo_ft1\" src=\"/v5/images/Logo_Foretees1.gif\" alt=\"ForeTees logo\" />");
   out.println("      <p class=\"logo\">Invoice</p>");
   out.println("    </div>");
   out.println("    <table id=\"invoice_due_date\">");
   out.println("    <tbody>");
   out.println("      <tr><td>Due Date</td>");
   out.println("      </tr><tr>");
   out.println("        <td>" + getDueDate(2) + "</td>");
   out.println("      </tr>");
   out.println("    </tbody>");
   out.println("    </table>");
   out.println("    <table id=\"invoice_terms\">");
   out.println("    <tbody>");
   out.println("      <tr>");
   out.println("        <td class=\"shade\">Date</td>");
   out.println("        <td class=\"shade\">Invoice No.</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td>" + getGenerateDate(2) + "</td>");
   out.println("        <td>" + invoiceNumber + "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td class=\"shade\">Terms</td>");
   out.println("        <td class=\"shade\"></td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td>Due on receipt</td>");
   out.println("        <td></td>");
   out.println("      </tr>");
   out.println("    </tbody>");
   out.println("    </table>    <!-- invoice_terms --> ");
   out.println("    </div>    <!-- top_right_club_inv --> ");
  
   out.println(" ");
   out.println("    <div id=\"bottom_club_inv\">");
   out.println("    <table id=\"invoice_charges\">");
   out.println("    <thead>");
   out.println("      <tr><th>Item</th><th>Description</th><th>Quantity</th><th>Rate</th><th>Amount</th></tr>");
   out.println("    </thead>");
   out.println("    <tbody>");
   out.println("      <tr>");
   out.println("        <td class=\"item\">Annual Per Member Fee</td>");
   out.println("        <td class=\"description\">Per Member Fee for the ForeTees Online Reservation "
           + "System for the period of " + getStartMonth() + " " + year + " through " + getEndMonth() + " " + (year + 1) + "</td>");
   out.println("        <td class=\"quantity\">" + billableCnt + "</td>");
   out.println("        <td class=\"rate\">" + frmter.format(getBillRate()) + "</td>");
   out.println("        <td class=\"amount\">" + amountDueStr + "</td>");
   out.println("      </tr>");
   out.println("      <tr>");
   out.println("        <td colspan=\"3\" class=\"total total_thanks\">Thank you!</td>");
   out.println("        <td class=\"total total_desc\">Total</td>");
   out.println("        <td class=\"total total_amount\">$" + amountDueStr + "</td>");
   out.println("      </tr>");
   out.println("    </tbody>");
   out.println("    </table>    <!-- invoice_charges -->");
   out.println("    </div>    <!-- bottom_club_inv -->");
   
   out.println("  </div>    <!-- content -->");
   out.println("  </div>    <!-- wrapper  -->");
   out.println("</body></html>");
     
   
      } catch (Exception e) {

         throw (new Exception("invoice: getNextDueDate(), Exception: " + e.getMessage()) );
      
     } finally {

          try { rs.close(); }
          catch (Exception ignore) {}

          try { pstmt.close(); }
          catch (Exception ignore) {}

   }


 } // displayClubInvoice()
 
 
 // *********************************************************
 // Add XML version and XHTML 1.1 output to data being sent to browser.
 // *********************************************************

 private void addDocTypeHdr(PrintWriter out) {

   out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
   out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\"");
   out.println("  \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
 
 }
 
 
 //
 // Customer viewable invoices based on invoice status.
 //
 public boolean customerView() {
     
     boolean custView = false;
     
     if ((status == statusType.Paid) || (status == statusType.Pending) || (status == statusType.AckReceipt) || 
         (status == statusType.AckPayment)) {
         
         custView = true;
     }
     
     return(custView);
 }
 
}  // invoice class
