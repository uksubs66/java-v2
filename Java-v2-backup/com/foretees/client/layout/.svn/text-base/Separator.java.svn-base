/***************************************************************************************
 *   Separator:  This class represents an html HR (head rule)
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.layout;

import java.io.*;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly generate a head rule element.  If
 *   not specified the default value for the width is 100%.
 *
 ***************************************************************************************
 **/

public class Separator implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes
  private String width = "100%";
  private String styleSheetClass = "";
  private String height = "";

  /**
  ***************************************************************************************
  *
  * This constructor creates a new Separator with the default values.
  *
  ***************************************************************************************
  **/

  public Separator(){

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the head rule with a width.
  *
  ***************************************************************************************
  **/

  public Separator(String theWidth){

     width = theWidth;

  }

  /**
  ***************************************************************************************
  *
  * Returns the name of the style sheet class to use for this head rule.
  *
  * @return the name of the style sheet class.
  *
  ***************************************************************************************
  **/

  public String getStyleSheetClass(){

    return styleSheetClass;

  }

   /**
  ***************************************************************************************
  *
  * Sets the style sheet class to use for this head rule.
  *
  * @param the name of the style sheet class.
  *
  ***************************************************************************************
  **/

  public void setStyleSheetClass(String theClass){

    styleSheetClass = theClass;

  }

}