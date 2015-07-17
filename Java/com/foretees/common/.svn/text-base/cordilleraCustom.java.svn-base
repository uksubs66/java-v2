/**************************************************************************************************************
 *   cordilleraCustom:  This will provide the common methods for processing custom requests for Cordillera CC.
 *
 *       called by:  Hotel_sheet
 *                   Member_sheet
 *
 *
 *   created:  2/01/2006   Bob P.
 *
 *
 *   last updated:
 *
 *             4/26/10 Changes for 2010.
 *             7/02/09 Adjust times on Mondays and Tuesdays for 2009 per club's request.
 *             3/07/09 Adjust times for 2009.
 *             6/26/08 Change 7/24 from 2-4-1 to 1-4-2 for an event.
 *             5/03/08 Custom times for 6/17.
 *             3/04/08 Adjust times for 2008.
 *             1/25/07 Corrected times for .
 *             1/09/07 Adjusted times for  per Penti's request.
 *             2/28/06 Adjusted times on 6/25 and 7/28 per Penti's request.
 *
 **************************************************************************************************************
 */


package com.foretees.common;

import java.io.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;


public class cordilleraCustom {


/**
 // *********************************************************
 //  Check for custom hotel guest restrictions for Cordillera
 // *********************************************************
 **/

 public static boolean checkCordillera(long date, int time, String course, String caller) {



   boolean allow = true;        // default return status to 'ok'
   boolean hit = false;

   int stime = 1059;      // earliest time to check
   int etime = 1451;      // latest time to check

   int stime1 = 1059;     // 11:00 - 2:50    (time range 1)    TIMES AVAILABLE FOR MEMBERS!!!!
   int etime1 = 1451;

   int stime2 = 1429;     // 2:30 - 2:50     (time range 2)
   int etime2 = 1451;

   int stime3 = 1329;     // 1:30 - 2:50     (time range 3)
   int etime3 = 1451;

   int stime4 = 1329;     // 1:30 - 1:50     (time range 4)
   int etime4 = 1351;

   int stime5 = 1059;     // 11:00 - 2:10    (time range 5)
   int etime5 = 1411;

   int stime6 = 1329;     // 1:30 - 1:40     (time range 6)
   int etime6 = 1341;

   int stime7 = 1409;     // 2:10 - 2:50     (time range 7)
   int etime7 = 1451;

   int mstime = 1200;     // 12:00 - 1:30    (Member Times - ok for members, not for hotel guests)
   int metime = 1329;

   //
   //  Patterns (a, b, c)  where:
   //
   //        course:   Mountain        Valley         Summit
   //                  --------        ------         ------
   //
   //    time range:      a              b              c
   //
   //
   //  Process:  If the requested tee time is between 11:00 and 3:00, then check for the
   //            above listed restricted times based on the course and date.  If a match is
   //            found, then allow or disallow the request based on the user (hotel or member).
   //            These times are restricted for hotel guests and the other times in this time
   //            frame are ok.  These times are ok for members, but the other times in this time
   //            frame are restricted for members.           
   //
   //         1.  Only check times between 11:00 AM and 3:00 PM each day (and never on the Short Course).
   //         2.  Check if time is a Member Only Time (based on course, dates and times in spreadsheet).
   //         3.  If time is a Member Only Time, then do not allow Hotel Guests
   //         4.  If time is NOT a Member Only Time and it is between 12:00 & 2:50 PM, then DO NOT allow members.
   //
   //****************************************************************************************************************
   //  NOTE:  The blacked out times in the spreadsheet are 'For Members Only'.  The other times are 'Lodge Times'!!!
   //         hit = true is set if the time falls in the blacked out range.
   //****************************************************************************************************************
   //
   //
   //********************************************************************************************************************
   //

   //
   //   Create mmdd date
   //
   date = date - ((date / 10000) * 10000);                   // get mmdd (i.e.  20060512 - 20060000 = 512)

   //
   //  Only check if the tee time is between 11:00 and 3:00 and in this year's date range (change anually!!!!!)
   //
   if (date > 415 && date < 1101 && time > stime && time < etime && !course.equals( "Short" )) {   

      //
      //  Check if requested tee time is restricted for hotel guests - based on date, time and course
      //
      if (date == 420 || date == 427 || date == 504 || date == 511 || date == 518) {

         //
         //     Pattern = 1-1-1
         //
         if (course.equals( "Mountain" ) || course.equals( "Valley" ) || course.equals( "Summit" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }
         }
         
      } else if (date == 608 || date == 615 || date == 617 || date == 618 || date == 619 || date == 622 || date == 629 || 
                 date == 706 || date == 712 || date == 713 || date == 715 || date == 720 || date == 724 || date == 727 || 
                 date == 803 || date == 810 || date == 817 || date == 824 || date == 831 ||
                 date == 907) {

         //
         //     Pattern = 1-1-2
         //
         if (course.equals( "Mountain" ) || course.equals( "Valley" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Summit" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
         
      } else if ((date > 415 && date < 521) || date == 607 || date == 621 || date == 628 || 
                  date == 705 || date == 708 || date == 719 || date == 722 || date == 726 || 
                  date == 802 || date == 809 || date == 816 || date == 823 || 
                  date == 906 || date == 908 || date == 927 || date == 929 ||  
                  date == 1002 || date == 1004 || date == 1006 || date == 1008 ||  
                  date == 1010 || date == 1011 || date == 1013 || date == 1015 ||  
                  (date > 1016 && date < 1101)) {  

         //
         //     Pattern = 1-2-1
         //
         if (course.equals( "Mountain" ) || course.equals( "Summit" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
         

     
       } else if (date == 829 || date == 911) {

         //
         //     Pattern = 1-2-3
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime3 && time < etime3) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
      

    
       } else if (date == 703 || date == 820) {

         //
         //     Pattern = 1-2-4
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime4 && time < etime4) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
      

      /*
      } else if (date == 907) { 

         //
         //     Pattern = 1-3-2
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime3 && time < etime3) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime2 && time < etime2) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
       */
         
         
      /*
      } else if (date == 1005) {

         //
         //     Pattern = 1-4-1
         //
         if (course.equals( "Mountain" ) || course.equals( "Summit" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime4 && time < etime4) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
       */
         
         
      } else if (date == 610 || date == 614 || date == 625 || 
                 date == 710 || date == 730 || date == 805 || date == 826 || date == 827 || 
                 date == 902 || date == 920 || date == 921 || date == 922 || 
                 date == 923 || date == 924) {

         //
         //     Pattern = 1-4-2
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime4 && time < etime4) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
                    
            } else {

               if (course.equals( "Summit" )) {

                  if ((time > stime2 && time < etime2)) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
    

      } else if (date == 627) {

         //
         //     Pattern = 1-4-4
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" ) || course.equals( "Summit" )) {

               if (time > stime4 && time < etime4) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
    

      /*
       } else if (date == 6) {

         //
         //     Pattern = 1-5-4
         //
         if (course.equals( "Mountain" )) {

            if (time > stime1 && time < etime1) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime5 && time < etime5) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime4 && time < etime4) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
      }
       */

      } else if (date == 521 || date == 525 || date == 601 || date == 603 || date == 609 || date == 616 || date == 623 || 
                 date == 630 || date == 701 || date == 707 || date == 714 || date == 721 || date == 723 || date == 728 ||
                 date == 804 || date == 811 || date == 818 || date == 825 || date == 830 ||
                 date == 901 || date == 928 || date == 930 ||
                 date == 1001 || date == 1003 || date == 1005 || date == 1007 || date == 1009 || date == 1012 || 
                 date == 1014 || date == 1016) {            

         //
         //     Pattern = 2-1-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" ) || course.equals( "Summit" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
         
         
      } else if (date == 914 || date == 915 || date == 916 || date == 917 || date == 919) {

         //
         //     Pattern = 2-1-2
         //
         if (course.equals( "Mountain" ) || course.equals( "Summit" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
         
         
      } else if (date == 828 || date == 908 ||
                 date == 910 || date == 912 || date == 918) {

         //
         //     Pattern = 2-1-3
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime3 && time < etime3) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
         
      } else if (date == 605 || date == 612 || date == 702 || date == 704 || date == 709 ||
                 date == 711 || date == 717 || date == 905) {

         //
         //     Pattern = 2-1-4
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                                // indicate we hit a restricted time
            }
                 
         } else {

            if (course.equals( "Valley" )) {

               if (time > stime3 && time < etime3) {         // if restricted time

                  hit = true;                                // indicate we hit a restricted time
               }

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                                // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime4 && time < etime4) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
    

      /*
       } else if (date == ) {

         //
         //     Pattern = 2-2-1
         //
         if (course.equals( "Mountain" ) || course.equals( "Valley" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Summit" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
       */
      

      } else if (date == 526 || date == 529 || date == 602 || 
                 date == 822) {

         //
         //     Pattern = 2-3-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime3 && time < etime3) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime1 && time < etime1) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
         
      } else if (date == 613 || date == 624 || date == 729 || date == 812 || 
                 date == 926) {

         //
         //     Pattern = 2-4-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime4 && time < etime4) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime1 && time < etime1) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
         
      } else if (date == 909) {

         //
         //     Pattern = 3-1-2
         //
         if (course.equals( "Mountain" )) {

            if (time > stime3 && time < etime3) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime2 && time < etime2) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
    

      
      /*
       } else if (date == ) {    

         //
         //     Pattern = 3-1-3
         //
         if (course.equals( "Mountain" ) || course.equals( "Summit" )) {

            if (time > stime3 && time < etime3) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
       */
      

      } else if (date == 527 || date == 528 || date == 530 || date == 808) {

         //
         //     Pattern = 3-2-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime3 && time < etime3) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime1 && time < etime1) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
   

      /*
       } else if (date == ) {

         //
         //     Pattern = 4-1-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime4 && time < etime4) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" ) || course.equals( "Summit" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }
            }
         }
       */
      

      } else if (date == 801 || date == 806 || date == 807 || date == 813 || date == 815 || date == 819 || 
                 date == 903 || date == 913 || date == 925) {   

         //
         //     Pattern = 4-1-2
         //
         if (course.equals( "Mountain" )) {

            if (time > stime4 && time < etime4) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime1 && time < etime1) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime2 && time < etime2) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
         
      } else if (date == 604 || date == 606 || date == 611 || date == 626 ||
                 date == 716 || date == 718 || date == 725 || date == 731 || 
                 date == 814 || date == 821 || 
                 date == 904) {

         //
         //     Pattern = 4-2-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime4 && time < etime4) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime1 && time < etime1) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
    

      /*
       } else if (date == 6) {          // custom for this one!!

         //
         //     Pattern = custom
         //
         if (course.equals( "Mountain" )) {

            if (time > 1329 && time < 1421) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if (time > stime2 && time < etime2) {         // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if ((time > stime2 && time < etime2) || (time > 1059 && time < 1351)) {   // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
       */

    
       } else if (date == 620) {

         //
         //     Pattern = 2-?-1
         //
         if (course.equals( "Mountain" )) {

            if (time > stime2 && time < etime2) {         // if restricted time

               hit = true;                             // indicate we hit a restricted time
            }

         } else {

            if (course.equals( "Valley" )) {

               if ((time > stime2 && time < etime2) || (time > 1059 && time < 1151)) {   // if restricted time

                  hit = true;                             // indicate we hit a restricted time
               }

            } else {

               if (course.equals( "Summit" )) {

                  if (time > stime1 && time < etime1) {         // if restricted time

                     hit = true;                             // indicate we hit a restricted time
                  }
               }
            }
         }
         
      }      // end of date checks


      //
      //  Now process the request according to the caller; hotel or member
      //
      //      Member:  if we hit on a time, then member is ok, else they might be restricted
      //
      //      Hotel:   if we hit on a time, then hotel guests are restricted, else they are ok
      //
      allow = true;                // default to ok
        
      if (caller.equals( "hotel" )) {
        
         if (hit == true) {        // if this is a 'Member Only' time
           
            allow = false;         // hotel guests are restricted
         }
           
      } else {                    // member
        
         if (hit == false) {                           // if NOT a 'Member Only' time

            if (time < mstime || time > metime) {      // if a 'Lodge Time' (noon - 2:50)
              
               allow = false;                          // members are restricted
            }
         }
      }

   }           // end of IF within time frame (11:00 - 3:00)

   return(allow);

 }  // end of checkCordillera

 
 
/**
 // *********************************************************
 //  Check for custom Starter Times for Cordillera
 // *********************************************************
 **/

 public static boolean checkStarterTime(long date, int time, String course, String caller) {



   boolean allow = true;        // default return status to 'ok'
   boolean hit = false;

   int stime = 1129;      // earliest time to check
   int etime = 1151;      // latest time to check

   //
   //   Create mmdd date
   //
   date = date - ((date / 10000) * 10000);                   // get mmdd (i.e.  20060512 - 20060000 = 512)

   //
   //  Only check if the tee time is between 11:30 and 11:50 and in this year's date range (change anually!!!!!)
   //
   if (date > 531 && date < 1001 && time > stime && time < etime && !course.equals( "Short" )) {   

      //
      //  Check if requested tee time is a starter time - based on date and course
      //
      if (course.equals( "Mountain" )) {

         if ((date > 531 && date < 607) || date == 609 || date == 611 || date == 612 || date == 613 || 
             date == 616 || date == 620 || date == 623 || date == 624 || date == 626 || date == 630 ||  
             date == 701 || date == 702 || date == 704 || date == 707 || date == 709 || date == 711 || date == 714 || 
             date == 716 || date == 717 || date == 718 || date == 721 || date == 723 || date == 725 || date == 728 || 
             date == 729 || date == 731 || date == 801 || date == 804 || date == 806 || date == 807 || date == 808 || 
             date == 811 || date == 812 || date == 813 || date == 814 || date == 815 || date == 818 || date == 819 || 
             date == 821 || date == 822 || date == 825 || date == 828 || date == 830 || date == 901 || date == 903 || 
             date == 904 || date == 905 || date == 909 || date == 910 || (date > 911 && date < 920) || date == 925 || 
             date == 926 || date == 928 || date == 930) {

            hit = true;                             // indicate we hit a Starter time
         }
         
      } else if (course.equals( "Valley" )) {
         
         if (date == 602 || date == 604 || date == 606 || date == 607 || date == 610 || date == 611 || date == 613 || 
             date == 614 || date == 621 || (date > 623 && date < 629) ||   
             date == 703 || date == 705 || date == 708 || date == 710 || date == 716 || date == 718 || date == 719 || 
             date == 722 || date == 725 || date == 726 || date == 729 || date == 730 || date == 731 ||  
             date == 802 || date == 805 || date == 808 || date == 809 || date == 812 || date == 814 || date == 816 || 
             date == 820 || date == 821 || date == 822 || date == 823 || date == 826 || date == 827 || date == 829 || 
             date == 902 || date == 904 || date == 906 || date == 908 || date == 911 || (date > 919 && date < 925) || 
             date == 926 || date == 927 || date == 929) {

            hit = true;                             // indicate we hit a Starter time
         }
         
      } else if (course.equals( "Summit" )) {
         
         if (date == 605 || date == 608 || date == 610 || date == 612 || date == 614 || date == 615 || 
             date == 617 || date == 618 || date == 619 || date == 622 || date == 625 || date == 627 || date == 629 ||  
             date == 702 || date == 703 || date == 704 || date == 706 || date == 709 || date == 710 || date == 711 || 
             date == 712 || date == 713 || date == 715 || date == 717 || date == 720 || date == 724 || date == 727 || 
             date == 730 || date == 801 || date == 803 || date == 805 || date == 806 || date == 807 || date == 810 || 
             date == 813 || date == 815 || date == 817 || date == 819 || date == 820 || date == 824 || date == 826 || 
             date == 827 || date == 828 || date == 829 || date == 831 || date == 902 || date == 903 || date == 905 || 
             date == 907 || (date > 908 && date < 926)) {

            hit = true;                             // indicate we hit a Starter time
         }
         
      }      // end of course and date checks


      //
      //  Now process the request according to the caller; hotel or member
      //
      //      Member:  if we hit on a time, then member is ok, else they might be restricted
      //
      //      Hotel:   if we hit on a time, then hotel guests are restricted, else they are ok
      //
      allow = true;                // default to ok
        
      if (hit == true) {        // if this is a 'Starter' time

         allow = false;         // members NOT allowed
      }

   }           // end of IF within time frame 

   return(allow);

 }  // end of checkStarterTime

 
 
}  // end of cordilleraCustom class

