/***************************************************************************************
 *   SystemLingo:  This class represents the different words to be used by the various
 *                 ForeTees systems.
 *
 *
 *   created: 9/23/2006   PTS
 *
 *
 *   revisions:       
 *
 *        4/04/14   Added 'Meetings' for custom site for Kopplin & Kuebler (Greg DaRosa).
 *        4/03/12   Added the 'Ball Machine' keyword for setLessonBookLingo and populated the associated terms.
 *        4/03/12   Added Lesson Book terms and setLessonBookLingo for setting them based on a passed Keyword.
 *           
 *
 ***************************************************************************************
 */

package com.foretees.client;


public class SystemLingo {

    
   // declare public variables that we'll access for various words   
   public String TEXT_tee_time = "";
   public String TEXT_tee_times = ""; 
   public String TEXT_Tee_Time = "";
   public String TEXT_Tee_Times = "";
     
   public String TEXT_reservation = "";  
   public String TEXT_Reservation = "";
   public String TEXT_reservations = "";  
   public String TEXT_Reservations = "";
   
   public String TEXT_Individual_Lessons = "";
   public String TEXT_Individual_Lesson = "";
   public String TEXT_Lesson_Pro = "";
   public String TEXT_Lesson_Pro_Contact = "";
   public String TEXT_Lesson = "";
   public String TEXT_lesson = "";
   public String TEXT_Lesson_Reservation = "";
   public String TEXT_lesson_reservation = "";
   public String TEXT_Lesson_Reservations = "";
   public String TEXT_Lesson_Pro_Select = "";
   public String TEXT_Golf_Shop = "";
   
      
   public void setLingo(boolean pTLT) {
       
       if (pTLT) {
   
           TEXT_tee_time = "notification";
           TEXT_Tee_Time = "Notification";
           TEXT_tee_times = "notifications";
           TEXT_Tee_Times = "Notifications";
           TEXT_reservation = "notification";
           TEXT_Reservation = "Notification";
           TEXT_reservations = "notifications";
           TEXT_Reservations = "Notifications";
       
       } else {
           
           TEXT_tee_time = "tee time";
           TEXT_Tee_Time = "Tee Time";
           TEXT_tee_times = "tee times";
           TEXT_Tee_Times = "Tee Times";
           TEXT_reservation = "reservation";
           TEXT_Reservation = "Reservation";
           TEXT_reservations = "reservations";
           TEXT_Reservations = "Reservations";
       
       }
   }
   
   public void setLessonBookLingo(String type) {
       
       if (type.equals("Ball Machine")) {
   
           TEXT_Individual_Lessons = "Ball Machine";
           TEXT_Individual_Lesson = "Ball Machine";
           TEXT_Lesson_Pro = "Ball Machine";
           TEXT_Lesson_Pro_Contact = "Pro Shop";
           TEXT_Lesson = "Ball Machine";
           TEXT_lesson = "ball machine";
           TEXT_Lesson_Reservation = "Ball Machine Reservation";
           TEXT_lesson_reservation = "ball machine reservation";
           TEXT_Lesson_Reservations = "Ball Machine Reservations";
           TEXT_Lesson_Pro_Select = "Ball Machine Selection";
           TEXT_Golf_Shop = "the Golf Shop";
           
       } else if (type.equals("Meetings")) {
   
           TEXT_Individual_Lessons = "Meetings";
           TEXT_Individual_Lesson = "Meeting";
           TEXT_Lesson_Pro = "Search Executive";
           TEXT_Lesson_Pro_Contact = "Kopplin & Kuebler";
           TEXT_Lesson = "Meeting";
           TEXT_lesson = "meeting";
           TEXT_Lesson_Reservation = "Meeting";
           TEXT_lesson_reservation = "meeting";
           TEXT_Lesson_Reservations = "Meetings";
           TEXT_Lesson_Pro_Select = "Search Executive Selection";
           TEXT_Golf_Shop = "your Search Executive";
           
       } else {
   
           TEXT_Individual_Lessons = "Individual Lessons";
           TEXT_Individual_Lesson = "Individual Lesson";
           TEXT_Lesson_Pro = "Lesson Pro";
           TEXT_Lesson_Pro_Contact = "Lesson Pro";
           TEXT_Lesson = "Lesson";
           TEXT_lesson = "lesson";
           TEXT_Lesson_Reservation = "Lesson";
           TEXT_lesson_reservation = "lesson";
           TEXT_Lesson_Reservations = "Lessons";
           TEXT_Lesson_Pro_Select = "Lesson Pro Selection";
           TEXT_Golf_Shop = "the Golf Shop";
       }
   }

}