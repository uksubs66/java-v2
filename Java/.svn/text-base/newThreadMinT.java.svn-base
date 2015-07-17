/***************************************************************************************
 *   newThreadMinT:  These methods will run on their own thread in background.
 *              This class will call inactTimer in SystemUtils to scan for
 *              the in_use indicator in teecurr, etc.  It is called every
 *              2 minutes by minTimer.
 *
 *   called by:  minTimer  (discontinued on 6/04/04 !!!!!!!!!!!!!!!!!!!!)
 *
 *   created: 3/28/2004   Bob P.
 *
 *   last updated:
 *
 *
 ***************************************************************************************
 */

import java.util.*;
import javax.servlet.*;
  
   
public class newThreadMinT extends Thread {

   public newThreadMinT() {

     super();         // invoke the run method
   }

   public void run() {
         //
         //  call inactTimer to process timer scan of teecurr
         //
         SystemUtils.inactTimer();
   }
}  // end of class
