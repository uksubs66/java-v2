/***************************************************************************************
 *   statsTimer:  This class will set a 10 minute timer task to update various system statistics.
 *
 *   Functions:  Main (set a timer)
 *               timeTask (get control from timer)
 *
 *
 *   called by:  self to process timer exp
 *               SystemUtils (inactTimer) to reset timer
 *               Login (init) to set timer
 *
 *   created: 6/14/2006   Paul S.
 *
 *   last updated:
 *
 *        
 *
 *
 ***************************************************************************************
 */

import java.io.*;
import javax.servlet.*;
import java.util.Timer;
import java.util.TimerTask;
  

public class statsTimer {

   Timer timer;

   public statsTimer() {

     timer = new Timer();

     //
     //  Here to set the timer
     //
     timer.schedule(new statsTask(), 10 * 60 * 1000);    // 10 minute timer
   }

   //*****************************************************
   //  timeTask gets control when the timer expires
   //*****************************************************

   class statsTask extends TimerTask {

      public void run() {

         timer.cancel();     // kill the timer thread
         
         // update login stats in REVLEVEL db with current login/sheet counts
         // loginCountsMem[];
         // loginCountsPro[];
         // sheetCountsMem[];
         // sheetCountsPro[];
         
         
         
      }
   }
   public static void Main() {

      new statsTimer();       // schedule the timer
   }
}  // end of class
