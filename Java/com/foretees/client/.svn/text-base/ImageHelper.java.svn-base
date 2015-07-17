/***************************************************************************************
 *   ImageHelper:  This utility class will help with returning images
 *
 *
 *   created: 10/03/2003   jag
 *
 *
 *   last updated:
 *
 *
 *
 ***************************************************************************************
 */

package com.foretees.client;

import java.io.*;

import com.foretees.common.ProcessConstants;

/**
 ***************************************************************************************
 *
 *  This helper class contains functions for returning images to be used in the user interface
 *
 ***************************************************************************************
 **/

public class ImageHelper {

  // Initialize the attributes

  /**
  ***************************************************************************************
  *
  * This method will return a string array of images to use for the tabs based on the
  * user type (admin, member, proshop).
  *
  * @param userType the type of user the page is being built for
  * @param out the PrintWriter to write out the html
  *
  ***************************************************************************************
  **/

  public static String[] getTabImages(String userType)
  {

    String[] images = null;

    if (userType.equals(ProcessConstants.ADMIN))
    {
      images = new String[8];
      images[0] = ProcessConstants.CODEBASE + "images/AdminHome-over.png";
      images[1] = ProcessConstants.CODEBASE + "images/AdminMembers-over.png";
      images[2] = ProcessConstants.CODEBASE + "images/AdminReports-over.png";
      images[3] = ProcessConstants.CODEBASE + "images/AdminAnnouncements-over.png";
      images[4] = ProcessConstants.CODEBASE + "images/AdminSettings1-over.png";
      images[5] = ProcessConstants.CODEBASE + "images/AdminSupport-over.png";
      images[6] = ProcessConstants.CODEBASE + "images/AdminLogout-over.png";
      images[7] = ProcessConstants.CODEBASE + "images/AdminHelp-over.png";
    }
    else if (userType.equals(ProcessConstants.MEMBER))
    {
      images = new String[10];
      images[0] = ProcessConstants.CODEBASE + "images/Mem_Home-over.png";
      images[1] = ProcessConstants.CODEBASE + "images/Mem_View_Tee_Sheets-over.png";
      images[2] = ProcessConstants.CODEBASE + "images/Mem_New_Tee_Time-over.png";
      images[3] = ProcessConstants.CODEBASE + "images/Mem_My_Tee_Times-over.png";
      images[4] = ProcessConstants.CODEBASE + "images/Mem_Search-over.png";
      images[5] = ProcessConstants.CODEBASE + "images/Mem_Events-over.png";
      images[6] = ProcessConstants.CODEBASE + "images/Mem_Partners-over.png";
      images[7] = ProcessConstants.CODEBASE + "images/Mem_Settings-over.png";
      images[8] = ProcessConstants.CODEBASE + "images/Mem_Exit-over.png";
      images[9] = ProcessConstants.CODEBASE + "images/Mem_Help-over.png";
    }
    else if (userType.equals(ProcessConstants.PROSHOP))
    {
      images = new String[10];
      images[0] = ProcessConstants.CODEBASE + "images/ProHome-over.png";
      images[1] = ProcessConstants.CODEBASE + "images/ProTee_Sheets-over.png";
      images[2] = ProcessConstants.CODEBASE + "images/Event_Sign_Up-over.png";
      images[3] = ProcessConstants.CODEBASE + "images/ProSystem_Config-over.png";
      images[4] = ProcessConstants.CODEBASE + "images/ProSearch-over.png";
      images[5] = ProcessConstants.CODEBASE + "images/ProReports-over.png";
      images[6] = ProcessConstants.CODEBASE + "images/ProSettings-over.png";
      images[7] = ProcessConstants.CODEBASE + "images/ProLogout-over.png";
      images[8] = ProcessConstants.CODEBASE + "images/ProHelp-over.png";
      images[9] = ProcessConstants.CODEBASE + "images/ProSupport-over.png";
    }

    return images;

  }

}