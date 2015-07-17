/***************************************************************************************
 *   mTypeArrays:  This class defines an array of Member Types and Membership Types.
 *
 *   created:  3/07/2006   Bob P.
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.common;

import java.io.*;

/**
 ***************************************************************************************
 *
 *  This class is a utility that contains an array for Member Types and Membership Types
 *
 ***************************************************************************************
 **/

public class mTypeArrays {

   public int MAX_Mems = Labels.MAX_MEMS;
   public int MAX_Mships = Labels.MAX_MSHIPS;
    
   //
   //  Member types
   //
   public String [] mem = new String [MAX_Mems];          // new member types

   //
   //  Mship types
   //
   public String [] mship = new String [MAX_Mships];        // new mship types

}
