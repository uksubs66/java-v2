/***************************************************************************************
 *   parmMidPacific:  This class will define a paramter block object to be used for Mid Pacific custom mship restrictions
 *
 *
 *   called by:  verifyCustom
 *
 *   created: 7/22/2009   Brad K.
 *
 *   last updated: 
 *
 *                  9/17/09   Added 'mship' strings to parm block
 *                  9/14/09   Added 'user' and 'memberClass' strings to parm block
 *                  8/11/09   Updated version that should largely be final
 *                  7/22/09   Created and populated with initial parameter variables
 *
 *
 ***************************************************************************************
 */
   
package com.foretees.common;


public class parmMidPacific {

    // General values
    public int res_month = 0;            // Restricted rounds this month
    public int res_year = 0;             // Restricted rounds this year
    public int non_month = 0;            // Unrestricted rounds this month
    public int non_year = 0;             // Unrestricted rounds this year
    public int total_month = 0;          // Total rounds this month (res & non)
    public int total_year = 0;           // Total rounds this year (res & non)
    public int rounds18_month = 0;       // Number of 18 hole rounds this month
    public int rounds18_year = 0;        // Number of 18 hole rounds this year
    public int rounds9_month = 0;        // Number of 9 hole rounds this month
    public int rounds9_year = 0;         // Number of 9 hole rounds this month
    public int propGuestRounds = 0;      // Number of rounds as the guest of a Proprietary member
    public int playerRounds_year = 0;    // Total player rounds this year (A tee time with 1 member and 3 guests would count as 4 towards to this total)
    public long teecurr_id = -1;          // Teecurr_id of current tee time.  Defaults to -1, pass -1 if irrelevent
    public int time_mode = 0;            // Time mode for queries.  0 = all year, 1-12 = respective month
    public int time_mode_year = 0;       // Time mode year for queries, only to be used when time mode = 1-12. If blank, current calendar year will be used.

    public String user = "";             // Username of member
    public String memberClass = "";      // Member class of member
    public String mship = "";            // Membership Type of member
    public String errorMsg = "";         // Error message to return
    public String player = "";           // Name of player that's cause of problem
    public String propUser = "";         // Username of Prop. member to be the guest of

    public boolean bookAsPropGuest = false;     // True if round should be booked as a prop guest round, false if not

}  // end of class
