/***************************************************************************************
 *   parmEvent:  This class defines a parameter block object for an event.
 *
 *
 *   called by:  several
 *
 *
 *   created:  2/25/2014   Paul S.
 *
 *
 *   last updated:
 *
 *      2/25/14   ...
 * 
 * 
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.util.*;


public class parmEvent {

    public int id = 0;
    public int reservationId = 0;
    public int organizationId = 0;
    public int maximumPartySize = 0;
    
    public String name = "";
    public String charges = "";
    public String costs = "";
    public String seatings = "";
    //public String timeFormat = "";
    public String state = "";
    
    public int date = 0;
    //public String dateString = "";
    public int startTime = 0;
    //public String startTimeString = "";
    public int endTime = 0;
    //public String endTimeString = "";
    //public String startTime24hString = "";
    public int registrationStart = 0;
    //public String registrationStartString = "";
    public int registrationEnd = 0;
    //public String registrationEndString = "";
    
    public String locationName = "";
    public String onlineMessage = "";
    
    public boolean canSignUp = false;
    public boolean isSignedUp = false;
    public boolean inSignUpPeriod = false;
    public boolean isEventOpen = false;
 
    public List<String> eventCostCategory = new ArrayList<String>();
    public List<String> eventCostPrice = new ArrayList<String>();
    public List<String> eventSeatingTimes = new ArrayList<String>();
    
    
}