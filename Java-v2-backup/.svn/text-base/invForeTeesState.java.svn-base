/***************************************************************************************
 *   invForeTeesState:  This servlet will track and update the invoice ForeTees state.
 *
 * ****************************************************************************
 * ****************************************************************************
 * 
 * NO longer used.
 * This FILE class has bee REPLACED by invoice_data.java.
 * 
 * ****************************************************************************
 * ****************************************************************************
 *   called by:  Support_invoicing
 *
 *   created:  11/16/2011   Tim K.
 *
 *   last updated:
 *
 *      3/09/12  This file class has been replaced by invoice_data.java. See that file for the ForeTees state.
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


public class invForeTeesState {
    
    //************************************************************************
    //
    //  Description: The invForeTees state will be used by ForeTees support
    //      in keeping track of when a club is due for an invoice, the full life 
    //      of an invoice, and accessing the database club table.
    //
    //************************************************************************
 
    //
    // Invoicing ForeTees state and event enums
    //  
    public enum foreTeesStType {Unknown, Current, CurrentSavePending, InvoiceNotify, InvoicePrepareNext, 
        InvoicePrepareNotify, InvoiceSent; }
    public enum eventType {CreateInv, SaveInv, UpdateInv, NotifyClub, MarkPaid; }
    
    // ForeTees state data fields.
    private foreTeesStType foreTeesState;
    private int notifyDays;
    private Calendar notifyDate;
    private String errorMsg;
    
    private String yrStr;                // Date parsing variables.
    private String monStr;
    private String dayStr;
    private int yr, mon, day;

    public static final int SUCCESS = 0;      // return values on function calls
    public static final int FAILURE = -1;
 
    
public invForeTeesState () {

   // Initialize foreTeesState to Unknown. Use currentState() function to
   // get state from club database. 
   // 
   foreTeesState = foreTeesStType.Unknown;
   notifyDays = 1;
   notifyDate = new GregorianCalendar();
   notifyDate.add(Calendar.DAY_OF_MONTH, -notifyDays);

}
  
public void currentState (Connection con)  throws Exception {

   // Get the current ForeTees state, update if notify date has been reached.
   // NOTE: Use invoice due to create the notify date.
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
             "SELECT ForeTeesState, notifyDays, nextDueDate FROM invoice_data");

     pstmt.clearParameters();
     rs = pstmt.executeQuery();
     if (rs.next()) {
       foreTeesState = convertIntToFTstateType( rs.getInt("ForeTeesState") );
       notifyDays = rs.getInt("notifyDays");
       parseDate( rs.getString("nextDueDate") );
       notifyDate.set(yr, mon, day);
       notifyDate.add(Calendar.DAY_OF_MONTH, -notifyDays);

     }

   } catch (Exception e) {

       throw (new Exception("ForeTeesState: currentState(), Exception: " + e.getMessage()) );
      
   } finally {

        try { rs.close(); }
        catch (Exception ignore) {}

        try { pstmt.close(); }
        catch (Exception ignore) {}

   }

   
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

public void updateFTstateToDb(Connection con) throws Exception {
 
   PreparedStatement pstmt = null;
   ResultSet rs = null;
   
   //
   //  Update ForeTees state in club table.
   //
   
   try {
     
     // Put new ForeTees State in club table.
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

//
// Tim - TBD: This function was to be used with invoice ForeTees State processing
//      but may not be necessary.
//
// Process event and "invoice status" to determine next ForeTees state.
//
// TABLE: Valid Events for the following ForeTees state and Invoice Status.
//------------------------------------------------------------------------------------*
//  ForeTees State  |           Invoice Status                                        |
//------------------------------------------------------------------------------------|
//                  | Paid      | Pending  | Ack Receipt | Ack Payment |  New         |
//------------------------------------------------------------------------------------|
// Current          | CreateInv |          |             |             |              |
//------------------------------------------------------------------------------------|
// Invoice Notify   | CreateInv |          |             |             |              |
//------------------------------------------------------------------------------------|
// Prepare Next     |           |          |             |             | SaveInv      |
//                  |           |          |             |             | NotifyClub   |
//                  |           |          |             |             | UpdateInv    |
//------------------------------------------------------------------------------------|
// Prepare Notify   |           |          |             |             |same as PrepNext|
//------------------------------------------------------------------------------------|
// Invoice Sent     |           | MarkPaid | MarkPaid    | MarkPaid    |              |
//------------------------------------------------------------------------------------|
// Process Payment  |AcctComplete|         |             |             |              |
//------------------------------------------------------------------------------------*
public int process (eventType evt, invoice.statusType status, Connection con)  throws Exception {

   PreparedStatement pstmt = null;
   ResultSet rs = null;
   foreTeesStType newFtState = foreTeesStType.Unknown;
   int rc = FAILURE;
   String ftStr = "ForeTees State = ";
   String statusStr = "Status = ";
   String evtStr = "Event = ";
   
   switch (foreTeesState) {
       
       case Current:
           if (evt == eventType.CreateInv) {
               
               if (status == invoice.statusType.Paid) {
                   newFtState = foreTeesStType.InvoiceNotify;
                   rc = SUCCESS;
               }
               else {
                   errorMsg = ftStr + getForeTeesStateStr() + ", " + statusStr + status 
                           + " in error (" + evtStr + evt + ")";
               }
           }
           else {
               errorMsg = ftStr + getForeTeesStateStr() + ", " + evtStr + evt + 
                   " in error (" + statusStr + status + ")";
           }
           break;
           
   
       case InvoiceNotify:
           if (evt == eventType.CreateInv) {
               
               if (status == invoice.statusType.Paid) {
                   newFtState = foreTeesStType.InvoicePrepareNotify;
                   rc = SUCCESS;
               }
               else {
                    errorMsg = ftStr + getForeTeesStateStr() + ", " + statusStr + status 
                           + " in error (" + evtStr + evt + ")";
               }
           }
           else {
               errorMsg = ftStr + getForeTeesStateStr() + ", " + evtStr + evt + 
                   " in error (" + statusStr + status + ")";
           }
           break;

       //-------------------------*
       //-------------------------*
       case InvoicePrepareNext:
       case InvoicePrepareNotify:
           
           switch (evt) {
               
               case SaveInv:
               case UpdateInv:                         
                   if (status == invoice.statusType.New) {
                       newFtState = foreTeesState;      // keep current state.
                       rc = SUCCESS;
                   }
                   else {
                      errorMsg = ftStr + getForeTeesStateStr() + ", " + statusStr + status 
                            + " in error (" + evtStr + evt + ")";
                   }
                   break;

               case NotifyClub:
                   if (status == invoice.statusType.New) {
                       newFtState = foreTeesStType.InvoiceSent;
                       rc = SUCCESS;
                   }
                   else {
                      errorMsg = ftStr + getForeTeesStateStr() + ", " + statusStr + status 
                            + " in error (" + evtStr + evt + ")";
                   }
                   break;

               default:
                 errorMsg = ftStr + getForeTeesStateStr() + ", " + evtStr + evt + 
                     " in error (" + statusStr + status + ")";
                   break;
           }
           break;

       case InvoiceSent:
           if (evt == eventType.MarkPaid) {
               
               if (status == invoice.statusType.Pending) {
                   newFtState = foreTeesStType.Current;
                   rc = SUCCESS;
               }
               else {
                   errorMsg = ftStr + getForeTeesStateStr() + ", " + statusStr + status 
                           + " in error (" + evtStr + evt + ")";
               }
           }
           else {
               errorMsg = ftStr + getForeTeesStateStr() + ", " + evtStr + evt + 
                   " in error (" + statusStr + status + ")";
               }
           break;

   } // switch (foreTeesState)
   
   
   if (rc == SUCCESS) {
       
       if (foreTeesState != newFtState){
          
           foreTeesState = newFtState;
           updateFTstateToDb(con);
       }
   }
   
   return (rc);

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

}  // invForeTeesState class
