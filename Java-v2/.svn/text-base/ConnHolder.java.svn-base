/***************************************************************************************
 *   ConnHolder:  This class will provide a holder mechanism for the DB connection
 *                so that it may be saved in the session block.
 *
 *   Implements:  HttpSessionBindingListener (manages the session's lifecycle)
 *
 *   created: 12/05/2001   Bob P.
 *
 *   last updated:
 *
 *       11/19/12   Enable auto-commits
 *        3/17/10   Cleaned up the valueUnbound method
 *        1/24/05   Ver 5 - change club2 to club5.
 *        7/18/03   Enhancements (none) for Version 3 of the software.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 *
 ***************************************************************************************
 */


import java.sql.*;
import javax.servlet.http.*;

import com.foretees.common.Utilities;


class ConnHolder implements HttpSessionBindingListener { 
    
  private Connection con = null;        //transient
      
  public ConnHolder(Connection con) {

     this.con = con;                    // save the connection

     try { con.setAutoCommit(true);    // xactions can extend between pages
     } catch(SQLException e) {}
  }
    
  //
  // This method will be called by others to get the saved connection
  //
  public Connection getConn() {
      
     return con;    // return the connection
  }
       
  //
  // This method will be called by the listener when the session is bound
  //
  public void valueBound(HttpSessionBindingEvent event) {
     // ignore this Bind Event..
  }
       
  //
  // This method will be called by the listener when the session is unbound
  //
  public void valueUnbound(HttpSessionBindingEvent event) {
            
    Statement stmt = null;

    try {

        if ( con.isValid(1) == true ) { // con != null

            try {

               //
               //  Adjust the number of users logged in
               //
               stmt = con.createStatement();
               stmt.executeUpdate("UPDATE club5 SET logins = (logins - 1) WHERE logins > 0");

            } catch (Exception exc) {

                Utilities.logErrorTxt("valueUnbound: read/update club5 error: " + exc.toString(), "unknown");

            } finally {

                try { stmt.close(); }
                catch (Exception ignore) {}

            }

            // abandon any unfinished transactions
            try { con.rollback(); }
            catch (Exception ignore) {}

        } // end if con is valid
        
    } catch (SQLException ignore) {

    } finally {
        
        // return/close the connection (already closed - rollback closes??)
        try { con.close(); }
        catch (Exception ignore) {}

    }

    con = null;
    
  }

}
