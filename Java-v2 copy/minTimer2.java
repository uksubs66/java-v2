/***************************************************************************************
 *   minTimer2:  This class will set a 2 minute timer task to handle customs and other processes
 *               that have burdened the other 2 minute timer.          
 *          
 *
 *   Functions:  Main (set a timer)
 *               timeTask2 (get control from timer)
 *
 *
 *   called by:  self to process timer exp
 *               SystemUtils (inactTimer2) to reset timer
 *               Login (init) to set timer
 *
 *   created: 5/08/2009   Bob P.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import java.util.Timer;
import java.util.TimerTask;
  

public class minTimer2 {

   Timer timer;

   public minTimer2() {

     timer = new Timer();
     
     long timerDate = SystemUtils.min2Time2;        // last set date/time for this timer

     //
     //  Here to set the timer
     //
     if (timerDate == 0) {
        
        timer.schedule(new timTask(), 180*1000);    // 3 minutes first time
        
     } else {
        
        timer.schedule(new timTask(), 120*1000);    // 2 minutes thereafter so it alternates with other 2 min timer
     }
   }

   //*****************************************************
   //  timeTask gets control when the timer expires
   //*****************************************************

   class timTask extends TimerTask {

      public void run() {

         timer.cancel();     // kill the timer thread

         //
         //  call inactTimer2 to process timer scan of teecurr
         //
         SystemUtils.inactTimer2();

//         new newThreadMinT().start();         // spawn a new thread to process the timer expiration 
      }
   }
   public static void Main() {

      new minTimer();       // schedule the timer
   }
}  // end of class
