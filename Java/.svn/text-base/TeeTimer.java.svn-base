/***************************************************************************************
 *   TeeTimer:  This class will set a timer task to keep the tee sheets current.
 *
 *   Functions:  Main (set a timer to build & move tee sheet each night)
 *               timeTask (get control from timer to build/move tee sheets each night)
 *
 *
 *   called by:  self to process timer exp & reset the timer.
 *               Login (init) to set timer
 *               SystemUtils (teeTimer) to reset timer
 *
 *   created: 12/20/2001   Bob P.
 *
 *   last updated:
 *
 *       06/05/08  RDP  Remove GHIN processing - moved to TimerGHIN.
 *       08/24/07  PTS  Moved errorlog trim statement to here
 *       07/24/07  RDP  Do not add a day to the clock if current time is between midnight and 1:05.
 *       05/16/07  PTS  Changed the timer back an hour to run at 1:05AM instead of 2:05AM
 *                      this was because on Wednesday's the optimize db is done 
 *                      and may not be done before the backup starts at 3:00AM
 *       10/03/06  PTS  Added call to SystemUtils.purgeBouncedEmails() to run method
 *        3/11/04  RDP  Change the time from 3:00 AM to 2:05 AM as the databases
 *                      are backed up at 3 and that causes our processing to fail.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class TeeTimer {

   Timer timer;


   public TeeTimer() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     Calendar cal = new GregorianCalendar();       // get todays date

     int hr = cal.get(Calendar.HOUR_OF_DAY);
     int min = cal.get(Calendar.MINUTE);

     int curTime = (hr * 100) + min;
       
     if (curTime > 104) {                          // if later than timer time

        cal.add(Calendar.DATE,1);                  // roll ahead 1 day
     }
       
     cal.set(Calendar.HOUR_OF_DAY,01);             // Set the time for 01:05 AM Central
     cal.set(Calendar.MINUTE,05);

     java.util.Date date = cal.getTime();

     //
     // Set the timer and return
     //
     timer.schedule(new timeTask(), date);  

   }


   //*****************************************************
   //  timeTask gets control when the timer expires
   //*****************************************************

   class timeTask extends TimerTask {


    public void run() {

        timer.cancel();      // kill the timer thread

        //
        //  call scantee to build and move tee sheets
        //
        try {

            SystemUtils.teeTimer();

        } catch (Exception ignore) { }

        //
        //  Call xmail bouncer job
        //
        try {

            SystemUtils.purgeBouncedEmails();

        } catch (Exception ignore) { }
         
        //
        //  Trim the errorlog
        //
        Connection con = null;
        Statement stmt = null;
        try {

            con = dbConn.Connect("v5");
            stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM errorlog WHERE err_timestamp < DATE_ADD(now(), INTERVAL -7 DAY)");

        } catch (Exception ignore) {
        } finally {

            try {
                if (stmt != null) stmt.close();
            } catch (Exception ignore) { }

            try {
                if (con != null) con.close();
            } catch (Exception ignore) { }
        }
      }
   }
   
   public static void Main() {

      new TeeTimer();       // schedule the timer
        
   }

}  // end of class
