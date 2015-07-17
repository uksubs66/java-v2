/***************************************************************************************
 *   TextBox:  This class represents an attribute of an object for a large amount of text
 *
 *
 *   created: 1/19/2004   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;

import javax.servlet.http.*;

import com.foretees.client.attribute.Attribute;

  /**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly generate an attribute value.
 *
 ***************************************************************************************
 **/

public class TextBox extends Attribute implements Serializable
{
    
    private static final long serialVersionUID = 1L;

  protected String size = "5"; //rows
  protected String maxLength = "50";  //cols



  /**
  ***************************************************************************************
  *
  * This constructor initializes the attribute with a label and a string value.
  *
  ***************************************************************************************
  **/

  public TextBox(String theLabel, String theValue)
  {

     super(theLabel,theValue);

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the attribute with a name, label, a string value, and a mode.
  *
  ***************************************************************************************
  **/

  public TextBox(String theName, String theLabel, String theValue, String theMode)
  {
     super(theName, theLabel, theValue, theMode);
  }

}