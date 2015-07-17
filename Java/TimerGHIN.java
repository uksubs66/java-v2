/***************************************************************************************
 *   TeeGHIN:  This class will set a timer task to update the GHIN records for each club that uses GHIN.
 *
 *   Functions:  Main (set a timer to run each night)
 *               timeTask (get control from timer to run GHIN updates)
 *
 *
 *   called by:  self to process timer exp & reset the timer.
 *               Login (init) to set timer
 *               Common_GHIN to reset timer
 *
 *   created: 6/03/2008   Bob P.
 *
 *   last updated:
 *
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;


public class TimerGHIN {

   Timer timer;


   public TimerGHIN() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     Calendar cal = new GregorianCalendar();       // get todays date

     int hr = cal.get(Calendar.HOUR_OF_DAY);
     int min = cal.get(Calendar.MINUTE);

     int curTime = (hr * 100) + min;
       
     if (curTime > 344) {                          // if later than timer time - in case we bounce at this time OR
                                                   // if this is NOT server #1 we reset timer immediately so we must bump to next day!!!!

        cal.add(Calendar.DATE,1);                  // roll ahead 1 day
     }
       
     cal.set(Calendar.HOUR_OF_DAY,03);             // Set the time for 03:45 AM Central
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

         //
         //  Retrieve posted scores from Ghin ES server
         //
         try {
             
            Common_ghin.getPostedScores(null);
              
         }
         catch (Exception ignore) {
         }
      
      }
   }
   
   public static void Main() {

      new TimerGHIN();       // schedule the timer
        
   }

}  // end of class
