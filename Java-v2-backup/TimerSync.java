/***************************************************************************************
 *   TimerSync:  This class will set a timer task to check for roster files from MFirst.
 *
 *   Functions:  Main (set a timer to do the check)
 *               timeTask (get control from timer to check each night)
 *
 *
 *   called by:  self to process timer exp & reset the timer.
 *               Login (init) to set timer
 *               Common_sync to reset timer
 *
 *   created: 2/24/2005   Bob P.
 *
 *   last updated:
 *
 *       02/16/08  RDP  Change time in curTime test from 445 to 444 so we don't reset the timer immediately after it processes.
 *       07/24/07  RDP  Do not add a day to the clock if current time is between midnight and 4:45.
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class TimerSync {

   Timer timer;


   public TimerSync() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     Calendar cal = new GregorianCalendar();       // get todays date

     int hr = cal.get(Calendar.HOUR_OF_DAY);
     int min = cal.get(Calendar.MINUTE);

     int curTime = (hr * 100) + min;

     if (curTime > 444) {                          // if later or equal than timer time (4:45)

        cal.add(Calendar.DATE,1);                  // roll ahead 1 day
     }

     cal.set(Calendar.HOUR_OF_DAY,4);              // Set the time for 04:45 AM Central
     cal.set(Calendar.MINUTE,45);

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
          
         try{
             
            Common_sync.rosterSync();       // call method to check for rosters
              
         }
         catch (Exception ignore) {
         }
      }
   }
   
   public static void Main() {

      new TimerSync();       // schedule the timer
        
   }

}  // end of class
