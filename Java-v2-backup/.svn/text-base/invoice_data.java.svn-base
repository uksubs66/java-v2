/***************************************************************************************
 *   invoice_data:  This servlet will update the invoice_data database fields.
 *
 *   called by:  Support_invoicing
 *
 *   created:  3/9/2011   Tim K.
 *
 *   last updated:
 *
 *      3/15/12  Add updateNextDueDateToDb(). Save nextDueDate in currentState().
 *      3/14/12  Add biannual frequency billing, add updFrequency(). Add checkNotify parameter on currentState().
 *       3/9/12  Renamed invForeTeesState.java (created 11/16/11) to match new db table invoice_data.
 *               Add all invoice_data database fields so they can be initialized with the invoicing setup process. Remove unused functions.
 *      2/24/12  Move club5 invoice fields to new table, invoice_data.
 *      2/16/12  Add CurrentSavePending ForeTees state.
 *
 *
 ***************************************************************************************
 */


import java.sql.*;
import javax.servlet.*;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class invoice_data {
    
    //************************************************************************
    //
    //  Description: The invoice_data class will contain the Support invoicing fields which
    //      which are part of the invoice_data database table. The ForeTees state will be used by 
    //      ForeTees support to keep track of when a club is due for an invoice and the full life
    //      cycle of the current invoice being processed.
    //
    //      The invoice_data class will contain functions for accessing the invoice_data database table.
    //
    //************************************************************************
 
    //
    // Invoicing ForeTees state and event enums
    //  
    public enum foreTeesStType {Unknown, Current, CurrentSavePending, InvoiceNotify, InvoicePrepareNext, 
        InvoicePrepareNotify, InvoiceSent; }
    public enum eventType {CreateInv, SaveInv, UpdateInv, NotifyClub, MarkPaid; }
    public enum frequencyType {Yearly, Biannually, Quarterly; }
    
    // Fields from invoice_data, database table.
    private boolean invoiceOn;
    private foreTeesStType foreTeesState;
    private Calendar nextDueDate;
    private int notifyDays;
    private int currInvoice;
    private String addr1;
    private String addr2;
    private String city;
    private String state;
    private String zipCode;
    private String invEmailContact;
    private frequencyType freq;
    private invoice.billingRateTyp defBillType;    // Use billing rate definition from invoice.

    
    // Additional ForeTees state data fields.
    private Calendar notifyDate;
    private String errorMsg;
    
    private String yrStr;                // Date parsing variables.
    private String monStr;
    private String dayStr;
    private int yr, mon, day;

    public static final int SUCCESS = 0;      // return values on function calls
    public static final int FAILURE = -1;
 
    
public invoice_data () {

   // Initialize foreTeesState to Unknown. Use currentState() function to
   // get state from invoice_data database table. 
   // 
   invoiceOn = false;
   foreTeesState = foreTeesStType.Unknown;
   nextDueDate = new GregorianCalendar();    // set to current date.
   notifyDays = 1;
   currInvoice = 0;
   addr1 = "";
   addr2 = "";
   city = "";
   zipCode = "";
   invEmailContact = "";
   freq = frequencyType.Yearly;
   defBillType = invoice.billingRateTyp.Member;
   
   notifyDate = new GregorianCalendar();
   notifyDate.add(Calendar.DAY_OF_MONTH, -notifyDays);

}
  
public void currentState (Connection con, boolean checkNotify)  throws Exception {

   // Get the current ForeTees state, update if notify date has been reached.
   // NOTE: Use invoice next due date to create the notify date.
   //
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   Calendar todaysDate = new GregorianCalendar();
   boolean notifyChgF = false;
   
   // Get ForeTees state.
   //
   try {

     // Get current invoice data.
     pstmt = con.prepareStatement ( 
             "SELECT ForeTeesState, notifyDays, nextDueDate, currInvoice, invoiceOn, frequency FROM invoice_data");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next()) {
       foreTeesState = convertIntToFTstateType( rs.getInt("ForeTeesState") );
       notifyDays = rs.getInt("notifyDays");
       parseDate( rs.getString("nextDueDate") );
       nextDueDate.set(yr, mon, day);
       notifyDate.set(yr, mon, day);
       notifyDate.add(Calendar.DAY_OF_MONTH, -notifyDays);
       currInvoice = rs.getInt("currInvoice");
       invoiceOn = rs.getInt("invoiceOn") >= 1;
       freq = convertIntToFreqType(rs.getInt("frequency"));

     }

   } catch (Exception e) {

       throw (new Exception("ForeTeesState: currentState(), Exception: " + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

   
   if (checkNotify) {
       
       //
       // Check if notify date has been reached.
       //
       if (notifyDate.before(todaysDate)) {
         if (foreTeesState == foreTeesStType.Current) {

             foreTeesState = foreTeesState.InvoiceNotify;
             notifyChgF = true;
         }
         else {
             if (foreTeesState == foreTeesState.InvoicePrepareNext) {

                 foreTeesState = foreTeesState.InvoicePrepareNotify;
                 notifyChgF = true;
             }
         }
       }
       if (notifyChgF) {

           updateFTstateToDb(con);
       }
   }

}

public void updateFTstateToDb(Connection con) throws Exception {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   //
   //  Update ForeTees state in invoice_data table.
   //
   try {
     
     pstmt = con.prepareStatement(
             "UPDATE invoice_data SET ForeTeesState = '" + convertFTstateToInt(foreTeesState) + "'"); 

     pstmt.clearParameters();
     pstmt.executeUpdate();
    
   } catch (Exception e) {

       throw (new Exception("ForeTeesState: updateFTstateToDb(), Exception: "
               + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}

public int updState (eventType evt, Connection con)  throws Exception {

   foreTeesStType newFtState = foreTeesStType.Unknown;
   boolean stateChg = false;
   
   switch (evt) {
       
       case CreateInv:
           newFtState = foreTeesStType.CurrentSavePending;
           break;       
   
       case SaveInv:
           if (foreTeesState == foreTeesStType.InvoiceNotify) {
               
               newFtState = foreTeesStType.InvoicePrepareNotify;
               stateChg = true;
           }
           else {
               newFtState = foreTeesStType.InvoicePrepareNext;
               stateChg = true;
           }
           break;
           
       case UpdateInv:
           // No change.
           break;

       case NotifyClub:
           newFtState = foreTeesStType.InvoiceSent;
           stateChg = true;
           break;

      case MarkPaid:
           newFtState = foreTeesStType.Current;
           stateChg = true;
           break;
          

     default:
   
   } // switch (foreTeesState)
   
   
   if (stateChg) {
       
       if (foreTeesState != newFtState){
          
           foreTeesState = newFtState;
           updateFTstateToDb(con);
       }
   }
   
   return (SUCCESS);

}

public void updFrequency(Connection con, frequencyType freq2) throws Exception {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   if (freq2 != freq) {
                                // only update if different than current setting.
       freq = freq2;
       
       //
       //  Update frequency in database table.
       //
       try {

         pstmt = con.prepareStatement(
                 "UPDATE invoice_data SET frequency = '" + convertFreqTypeToInt(freq) + "'"); 

         pstmt.clearParameters();
         pstmt.executeUpdate();

       } catch (Exception e) {

           throw (new Exception("invoice_data: updFrequency(), Exception: "
                   + e.getMessage()) );

       } finally {

            try { rs.close(); }
            catch (Exception ignore) {}

            try { pstmt.close(); }
            catch (Exception ignore) {}

       }
   }
   
}

public void updateCurrInvoiceToDb(Connection con, int id) throws Exception {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   //
   //  Update current invoice in database table.
   //
   
   try {
     
     pstmt = con.prepareStatement(
             "UPDATE invoice_data SET currInvoice = '" + id + "'"); 

     pstmt.clearParameters();
     pstmt.executeUpdate();
    
   } catch (Exception e) {

       throw (new Exception("invoice_data: updateCurrInvoiceToDb(), Exception: "
               + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}

public void updateNextDueDateToDb(Connection con) throws Exception {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   //
   //  Update next due date in database table.
   //
   
   try {
     
     pstmt = con.prepareStatement(
             "UPDATE invoice_data SET nextDueDate = '" + createDate(nextDueDate) + "'"); 

     pstmt.clearParameters();
     pstmt.executeUpdate();
    
   } catch (Exception e) {

       throw (new Exception("invoice_data: updateNextDueDateToDb(), Exception: "
               + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}


public void retrieve (Connection con)  throws Exception {

   //
   // Update invoice_data fields with the database invoice_data values.
   // 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   invoice inv = new invoice();    // need to convert bill type.
    
   try {

     //
     // Get invoice_data values.
     //
     pstmt = con.prepareStatement ( 
             "SELECT ForeTeesState, nextDueDate, notifyDays, currInvoice, address1, address2, city, "
             + "state, zipcode, invEmailContact, frequency, defBillType, invoiceOn FROM invoice_data");
     
     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next()) {
       foreTeesState = convertIntToFTstateType(rs.getInt("ForeTeesState"));
       parseDate( rs.getString("nextDueDate") );
       nextDueDate.set(yr, mon, day);
       notifyDays = rs.getInt("notifyDays");
       currInvoice = rs.getInt("currInvoice");
       addr1 = rs.getString("address1");
       addr2 = rs.getString("address2");
       city = rs.getString("city");
       state = rs.getString("state");
       zipCode = rs.getString("zipcode");
       invEmailContact = rs.getString("invEmailContact");
       freq = convertIntToFreqType(rs.getInt("frequency"));   
       defBillType = inv.convertIntToBRateType(rs.getInt("defBillType"));
       invoiceOn = rs.getInt("invoiceOn") >= 1;
 
     }

   } catch (Exception e) {

       throw (new Exception("invoice_data: retrieve(), Exception: " + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

}

public void update(Connection con) throws Exception {
 
   PreparedStatement pstmt1 = null;       // init DB objects
   invoice inv = new invoice();           // need to convert bill type.

   //
   //  Update all invoice_data into database
   //
   try {

     pstmt1 = con.prepareStatement("UPDATE invoice_data SET ForeTeesState = '" + convertFTstateToInt(foreTeesState)
           + "', nextDueDate = '" + createDate(nextDueDate) + "', notifyDays = " 
           + notifyDays + ", currInvoice = " +currInvoice+ ", address1 = '" + addr1
           + "', address2 = '" + addr2 + "', city = '" + city + "', state = '" + state 
           + "', zipcode = '" + zipCode + "', invEmailContact = '" + invEmailContact + 
             "', frequency = '" + convertFreqTypeToInt(freq) + 
             "', defBillType = '" + inv.convertBrateToInt(defBillType) + "', invoiceOn = " + (invoiceOn ? 1 : 0));

     pstmt1.clearParameters();
     pstmt1.executeUpdate();
     
         
   }
   catch (Exception exc) {

      throw (new Exception("invoice_data: update(), " + exc.getMessage()) );
   }

}


/**
 //************************************************************************
 //
 //  ForeTees State field functions.
 //
 //************************************************************************
 **/

public foreTeesStType convertIntToFTstateType(int ftNum) {
    
    foreTeesStType ftTyp = foreTeesStType.Unknown;
    
    // Convert int to ForeTees State type.
    switch (ftNum) {
      case 1: ftTyp = foreTeesStType.Current;
          break;
      case 2: ftTyp = foreTeesStType.CurrentSavePending;
          break;
      case 3: ftTyp = foreTeesStType.InvoiceNotify;
          break;
      case 4: ftTyp = foreTeesStType.InvoicePrepareNext;
          break;
      case 5: ftTyp = foreTeesStType.InvoicePrepareNotify;
          break;
      case 6: ftTyp = foreTeesStType.InvoiceSent;
          break;
      case 0: 
      default: ftTyp = foreTeesStType.Unknown;
          break;
    }
    
    return (ftTyp);
}

public int convertFTstateToInt(foreTeesStType ftTyp) {
    
    int stVal = 0;
    
    // Convert status type to int.
    switch (ftTyp) {
      case Current: stVal = 1;
          break;
      case CurrentSavePending: stVal = 2;
          break;
      case InvoiceNotify: stVal = 3;
          break;
      case InvoicePrepareNext: stVal = 4;
          break;
      case InvoicePrepareNotify: stVal = 5;
          break;
      case InvoiceSent: stVal = 6;
          break;
      case Unknown: 
      default: stVal = 0;
          break;
    }
    
    return (stVal);
}


public String getForeTeesStateStr() {
   
    String ftStateStr = "";
    
    // Get the status string.
    switch (foreTeesState) {
        case Current:       ftStateStr = "Current";
            break;
        case InvoiceNotify: ftStateStr = "Invoice Notify";
            break;
        case InvoicePrepareNext: ftStateStr = "Invoice Prepare Next";
            break;
        case InvoicePrepareNotify: ftStateStr = "Invoice Prepare Notify";
            break;
        case InvoiceSent:     ftStateStr = "Invoice Sent";
            break;
        case Unknown: 
        default:              ftStateStr = "Unknown";
            break;
    }
    
    return (ftStateStr);
}


public foreTeesStType getForeTeesState() {    return (foreTeesState); }

public String getErrorMsg() {    return (errorMsg); }

/**
 //************************************************************************
 //
 //  Frequency field functions.
 //
 //************************************************************************
 **/

public frequencyType convertIntToFreqType(int freqNum) {
    
    frequencyType freqTyp = frequencyType.Yearly;
    
    // Convert int to Frequency type.
    switch (freqNum) {
      case 0:
      default: freqTyp = frequencyType.Yearly;
          break;
      case 1: freqTyp = frequencyType.Biannually;
          break;
      case 2: freqTyp = frequencyType.Quarterly;
          break;
    }
    
    return (freqTyp);
}

public int convertFreqTypeToInt(frequencyType freqTyp) {
    
    int freqVal = 0;
    
    // Convert frequency type to int.
    switch (freqTyp) {
      case Yearly:
      default: freqVal = 0;
          break;
      case Biannually: freqVal = 1;
          break;
      case Quarterly: freqVal = 2;
          break;
    }
    
    return (freqVal);
}

public String getFrequencyStr() {
   
    String frequencyStr = "";
    
    // Get the status string.
    switch (freq) {
        default:
        case Yearly:    frequencyStr = "Yearly";
            break;
        case Biannually: frequencyStr = "Biannually";
            break;
        case Quarterly: frequencyStr = "Quarterly";
            break;
    }
    
    return (frequencyStr);
}

public int getFrequencyVal() {
    
    int freqValue = 1;
    
    // Value used to convert yearly amount due, to billing frequency.
    switch (freq) {
        default: 
        case Yearly: freqValue = 1;
            break;
            
        case Biannually: freqValue = 2;
            break;

        case Quarterly: freqValue = 4;
            break;
       }
    
    return (freqValue);
}


/**
 //************************************************************************
 //
 //  Date parse and create functions.
 //
 //  NOTE: Store dates in database with 1-based month.
 //        GregorianCalendar class stores dates with 0-based month.
 //        Therefore month will be adjusted accordingly.
 //************************************************************************
 **/

//
// Parse date from database to store in Calendar class.
//
private void parseDate(String dateStr) {
    
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
private String createDate(Calendar calDate) {
    
    
    // Get integer values and plug into date values.
    yr = calDate.get(Calendar.YEAR);
    mon = calDate.get(Calendar.MONTH) + 1;     // adjust month
    day = calDate.get(Calendar.DATE);
    
    return (yr + "-" + mon + "-" + day);
    
}


public String getNotifyDate() {
    
    String dstr = createDate (notifyDate);
    return (dstr);
}

public String getNextDueDate() {
    
    String dstr = createDate (nextDueDate);
    return (dstr);
}

public Calendar getNextDueDateCal() {    return(nextDueDate); }

public int getNotifyDays() {    return (notifyDays); }

public int getCurrInvoice() {    return (currInvoice); }

public String getAddr1() {    return (addr1); }

public String getAddr2() {    return (addr2); }

public String getCity() {    return (city); }

public String getState() {    return (state); }

public String getZipCode() {    return (zipCode); }

public String getInvEmailContact() {    return (invEmailContact); }

public frequencyType getFreq() {    return (freq); }

public invoice.billingRateTyp getDefBillType() {    return (defBillType); }

public boolean getInvoiceOn() {    return (invoiceOn); }

/**
 //************************************************************************
 //
 //  Set single field functions.
 //
 //************************************************************************
 **/

public int setForeTeesState(foreTeesStType ftState) { 
    
    int rc = FAILURE;    

    foreTeesState = ftState;
    rc = SUCCESS;
    
    return (rc);
}

public int setNextDueDateCal(Calendar cal) {
    
    int rc = FAILURE;    
    if (cal.get(Calendar.YEAR) > 2010) {
        
        nextDueDate = cal;
        rc = SUCCESS;
    }
    
    return (rc);
}

public int setNotifyDays(int nDays) {
    
    int rc = FAILURE;    
    if (nDays >= 0) {
        
        notifyDays = nDays;
        rc = SUCCESS;
    }
    
    return (rc);
    
}

public int setFreq(frequencyType freqVal) {
    
    freq = freqVal;    
    return (SUCCESS);    
}

public int setDefBillType(invoice.billingRateTyp bType) {
    
    defBillType = bType;    
    return (SUCCESS);    
}

public int setInvoiceOn(boolean invOnFlag) {
    
    invoiceOn = invOnFlag;
    return(SUCCESS);
}

public int setAddr1(String addrStr) {
    
    addr1 = addrStr;    
    return (SUCCESS);   
}
public int setAddr2(String addrStr) {
    
    addr2 = addrStr;    
    return (SUCCESS);   
}
public int setCity(String cityStr) {
    
    city = cityStr;    
    return (SUCCESS);   
}
public int setState(String stateStr) {
    
    state = stateStr;    
    return (SUCCESS);   
}
public int setZipCode(String zip) {
    
    zipCode = zip;    
    return (SUCCESS);   
}

public int setInvEmailContact(String eContact) {
    
    invEmailContact = eContact;    
    return (SUCCESS);   
}

}  // invoice_data class
