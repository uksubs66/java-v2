/***************************************************************************************
 *   TsheetTimer:  This class will set a timer task to email the current tee sheet to
 *                 each club.
 *
 *   Functions:  Main (set a timer)
 *               timeTask (get control from timer to email tee sheets)
 *
 *
 *   called by:  self to process timer exp & reset the timer.
 *               Login (init) to set timer
 *               SystemUtils (tsheetTimer) to reset timer
 *
 *
 *   created: 2/03/2003   Bob P.
 *
 *   last updated:
 *
 *       07/24/07  Minor changes to setting the time value for the timer.  Use cal.add instead of cal.roll.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class TsheetTimer {

   Timer timer;


   public TsheetTimer() {

     timer = new Timer();

     //
     //  Here to set the timer (4:00 AM, or 10:00 PM)
     //
     Calendar cal = new GregorianCalendar();       // get todays date
     int hr = cal.get(Calendar.HOUR_OF_DAY);       // get 24 hr clock value

     if (hr > 21 && hr < 24) {                     // if current time is between 10:00 PM and Midnight
       
        cal.add(Calendar.DATE,1);                  // roll ahead 1 day
          
        hr = 4;                                    // Set timer for 4:00 AM

     } else {

        if (hr < 4) {                              // if current time is between Midnight and 4:00 AM

           hr = 4;                                 // Set timer for 4:00 AM

        } else {

           if ((hr > 3) && (hr < 22)) {            // if between 4 AM and 10:00 PM

              hr = 22;                             // set for 10:00 PM
           }
        }
     }

     cal.set(Calendar.HOUR_OF_DAY,hr);             // Set the hour for next time
     cal.set(Calendar.MINUTE,00);

     java.util.Date date = cal.getTime();

     if (Common_Server.SERVER_ID == SystemUtils.TIMER_SERVER) SystemUtils.logError("Resetting Tee Sheet Timer on Node " + Common_Server.SERVER_ID + " for " + date);
     
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
         try{
             
            SystemUtils.tsheetTimer();
              
         }
         catch (Exception ignore) {
         }
      }
   }
   
   public static void Main() {

      new TsheetTimer();       // schedule the timer
        
   }

}  // end of class
