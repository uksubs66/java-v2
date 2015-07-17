/***************************************************************************************
 *   minTimer:  This class will set a 2 minute timer task to scan teecurr busy tee slots.
 *              If the in_use indicator is non-zero, it will be incremented every 2 minutes.              
 *              If more than 6 then a timeout condition has occurred.
 *
 *   Functions:  Main (set a timer)
 *               timeTask (get control from timer)
 *
 *
 *   called by:  self to process timer exp
 *               SystemUtils (inactTimer) to reset timer
 *               Login (init) to set timer
 *
 *   created: 1/09/2002   Bob P.
 *
 *   last updated:
 *
 *        6/04/04   Remove call to create new thread as it was fialing often.
 *        3/28/04   When timer expires, spawn a new thread to process on so system
 *                  is not bogged down waiting for this to complete.
 *        9/18/02   Enhancements for Version 2 of the software.
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import java.util.Timer;
import java.util.TimerTask;
  

public class minTimer {

   Timer timer;

   public minTimer() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     timer.schedule(new timTask(), 120*1000);    // 2 minute timer
   }

   //*****************************************************
   //  timeTask gets control when the timer expires
   //*****************************************************

   class timTask extends TimerTask {

      public void run() {

         timer.cancel();     // kill the timer thread

         //
         //  call inactTimer to process timer scan of teecurr
         //
         SystemUtils.inactTimer();

//         new newThreadMinT().start();         // spawn a new thread to process the timer expiration 
      }
   }
   public static void Main() {

      new minTimer();       // schedule the timer
   }
}  // end of class
