/***************************************************************************************
 *   TimerGHIN:  This class will set a timer task to update the handicap records for
 *               clubs configured with a compatible interface.
 *
 *   Functions:  Main (set a timer to run each night)
 *               timeTask (get control from timer to run the updates)
 *
 *
 *   called by:  self to process timer exp & reset the timer.
 *               Login (init) to set timer
 *               Common_ghin to reset timer
 *
 *   created: 6/03/2008   Bob P.
 *
 *   last updated:
 *
 *             1/24/2013  Wrap external calls in seperate try/catch and do golfnet processing first
 *            12/30/2012  Add golfnet to nightly processing
 *
 ***************************************************************************************
 */

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;


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
         //  Retrieve posted scores from GolfNet & Ghin ES server
         //
         try {

            Common_golfnet.getPostedScores(null);       // do golfnet first since it's much faster
              
         } catch (Exception ignore) { }

         try {

            Common_ghin.getPostedScores(null);

         } catch (Exception ignore) { }
      
      }
   }
   
   public static void Main() {

      new TimerGHIN();       // schedule the timer
        
   }

}  // end of class
