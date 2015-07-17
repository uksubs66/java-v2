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
   
   public void setLingo(String type, String club, int activity_id) {
       
       if (type.equals("Lesson Book")) {
           
           if ((club.equals("ballantyne") || club.equals("trooncc") || club.equals("sierraviewcc")) && activity_id == 1) {
               setLessonBookLingo("Ball Machine");
           } else if (club.equals("kopplinandkuebler")) {
               setLessonBookLingo("Meetings");      // custom site for scheduling meetings - Kopplin & Kuebler (Greg DaRosa)
           } else if (club.equals("orchidisland") && activity_id == 1) {
               setLessonBookLingo("Spa");
           } else if (club.equals("gallerygolf") && activity_id == 9) {
               setLessonBookLingo("Fitness");
           } else if (club.equals("westwoodccoh") && activity_id == 0) {
               setLessonBookLingo("Golf Simulator");
           } else {
               setLessonBookLingo("default");
           }
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
           
       } else if (type.equals("Golf Simulator")) {
   
           TEXT_Individual_Lessons = "Golf Simulator";
           TEXT_Individual_Lesson = "Golf Simulator";
           TEXT_Lesson_Pro = "Golf Simulator";
           TEXT_Lesson_Pro_Contact = "Golf Shop";
           TEXT_Lesson = "Golf Simulator";
           TEXT_lesson = "golf simulator";
           TEXT_Lesson_Reservation = "Golf Simulator Reservation";
           TEXT_lesson_reservation = "golf simulator reservation";
           TEXT_Lesson_Reservations = "Golf Simulator Reservations";
           TEXT_Lesson_Pro_Select = "Golf Simulator Selection";
           TEXT_Golf_Shop = "the Golf Shop";
           
       } else if (type.equals("Meetings")) {       // changed to Interviews (SystemLingo name remains as Meetings)
   
           TEXT_Individual_Lessons = "Interviews";
           TEXT_Individual_Lesson = "Interview";
           TEXT_Lesson_Pro = "Search Executive";
           TEXT_Lesson_Pro_Contact = "Kopplin & Kuebler";
           TEXT_Lesson = "Interview";
           TEXT_lesson = "interview";
           TEXT_Lesson_Reservation = "Interview";
           TEXT_lesson_reservation = "interview";
           TEXT_Lesson_Reservations = "Interviews";
           TEXT_Lesson_Pro_Select = "Search Executive Selection";
           TEXT_Golf_Shop = "your Search Executive";
           
       } else if (type.equals("Spa")) {
   
           TEXT_Individual_Lessons = "Spa Services";
           TEXT_Individual_Lesson = "Spa Service";
           TEXT_Lesson_Pro = "Therapist";
           TEXT_Lesson_Pro_Contact = "Spa Staff";
           TEXT_Lesson = "Spa Service";
           TEXT_lesson = "spa service";
           TEXT_Lesson_Reservation = "Spa Service Reservation";
           TEXT_lesson_reservation = "spa service reservation";
           TEXT_Lesson_Reservations = "Spa Service Reservations";
           TEXT_Lesson_Pro_Select = "Therapist Selection";
           TEXT_Golf_Shop = "the Spa Staff";
           
       } else if (type.equals("Fitness")) {
   
           TEXT_Individual_Lessons = "Personal Training Sessions";
           TEXT_Individual_Lesson = "Personal Training Session";
           TEXT_Lesson_Pro = "Personal Trainer";
           TEXT_Lesson_Pro_Contact = "Personal Trainer";
           TEXT_Lesson = "Training Session";
           TEXT_lesson = "training session";
           TEXT_Lesson_Reservation = "Training Session";
           TEXT_lesson_reservation = "training session";
           TEXT_Lesson_Reservations = "Training Sessions";
           TEXT_Lesson_Pro_Select = "Personal Trainer Selection";
           TEXT_Golf_Shop = "the Fitness Staff";
           
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