/***************************************************************************************
 *   parmSMTP:  This class will define a paramter block object to be used for email communications.
 *
 *
 *   called by:  several
 *
 *   created: 03/05/2008   Paul S.
 *
 *   last updated:
 * 
 *   04/28/09 - Added EMAIL_FROM_FB for food & bev proshop users (proshopfb) (case 1641).
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;


public class parmSMTP {
   
   //
   // Define the parameter block with the defaults for our mail server 
   //
    
   public String SMTP_ADDR = "216.243.184.88";
   public String SMTP_PORT = "20025";   // 20025
   
   public String SMTP_USER = "support@foretees.com";
   public String SMTP_PASS = "fikd18";
      
   public String EMAIL_FROM = "auto-send@foretees.com";             // default address for notification emails
   public String EMAIL_FROM_PRO = "YourGolfShop@foretees.com";      // default address for emails from pro
   public String EMAIL_FROM_MEM = "aMemberOfYourClub@foretees.com"; // default address for emails from members
   public String EMAIL_FROM_FB = "YourFoodAndBevDept@foretees.com"; // default address for emails from food & bev (proshopfb)
  
   public boolean SMTP_AUTH = true;
  
}