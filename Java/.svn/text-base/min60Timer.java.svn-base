/***************************************************************************************
 *   min60Timer:  This class will set a 60 minute timer task to scan teecurr for X's.
 *
 *   Functions:  Main (set a timer)
 *               timeTask (get control from timer)
 *
 *
 *   called by:  self to process timer exp
 *               SystemUtils (xTimer) to reset timer
 *               Login (init) to set timer
 *
 *   created: 2/089/2003   Bob P.
 *
 *   last updated:
 *
 *
 *
 ***************************************************************************************
 */

import java.util.Timer;
import java.util.TimerTask;


public class min60Timer {

   Timer timer;

   public min60Timer() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     timer.schedule(new tim60Task(), 60*60*1000);    // schedule for one hour from now
     
     //if (Common_Server.SERVER_ID == 1) SystemUtils.logError("min60Timer: scheduled for 1 hour from now.");
     
   }

   //*****************************************************
   //  timeTask gets control when the timer expires
   //*****************************************************

   class tim60Task extends TimerTask {

      public void run() {

         timer.cancel();     // kill the timer thread
         
         SystemUtils.xTimer();

      }
   }
   
   public static void Main() {

      new min60Timer();       // schedule the timer
        
   }

}  // end of class
