/***************************************************************************************
 *   Checkox:  This class represents a checkbox
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *       7/08/08  Added proshop boolean to be used for alternate rendering if for use with proshop user pages
 *       5/23/07  Do not set slected=false in update as this clears checkboxes in rows
 *                that have not been processed yet (we only do 15 at a time - update does all).
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;
import java.lang.Boolean;

import javax.servlet.http.*;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly generate an attribute value.
 *
 ***************************************************************************************
 **/

public class Checkbox implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attribute
  private String label = "Label not initialized";
  private boolean selected = false;
  private boolean proshop = false;
  private String name = "";
  private String value = null;


  /**
  ***************************************************************************************
  *
  * This constructor initializes the checkbox with a name, label, value, and if it is selected
  *
  ***************************************************************************************
  **/

  public Checkbox(String theName, String theLabel, String theValue, boolean isSelected){

     label = theLabel;
     selected = isSelected;
     value = theValue;
     name = theName;
     proshop = false;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the checkbox with a name, label, value, if it is selected, and whether it is for the proshop user page.
  *
  ***************************************************************************************
  **/

  public Checkbox(String theName, String theLabel, String theValue, boolean isSelected, boolean isProshop){

     label = theLabel;
     selected = isSelected;
     value = theValue;
     name = theName;
     proshop = isProshop;
  }  
  
  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this checkbox when displayed in the user interface.
  *
  * @param theLabel the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setLabel(String theLabel){

    label = theLabel;
  }

  /**
  ***************************************************************************************
  *
  * Returns the text to be used as the label for this checkbox when displayed in the user
  * interface.
  *
  * @return the text to use as the label.
  *
  *
  ***************************************************************************************
  **/

  public String getLabel(){

    return label;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this checkbox.  This name should be unique for all checkboxs
  * especially when used within the same form.
  *
  * @param theName the name to use to identify this checkbox.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this checkbox.
  *
  * @return the name to use for this checkbox.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

    /**
  ***************************************************************************************
  *
  * Sets the text to use as the value for this checkbox when displayed in the user interface.
  *
  * @param theValue the string to use as the value.
  *
  ***************************************************************************************
  **/

  public void setValue(String theValue){

    value = theValue;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the value for this checkbox when displayed in the user
  * interface.
  *
  * @return the string to use as the value.
  *
  *
  ***************************************************************************************
  **/

  public String getValue(){

    return value;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether this checkbox is selected.
  *
  * @param isSelected value indicating if the checkbox is selected.
  *
  ***************************************************************************************
  **/

  public void setSelected(boolean isSelected){

    selected = isSelected;
  }

  /**
  ***************************************************************************************
  *
  * Returns the true of false to indicate if this checkbox is selected.
  *
  * @return the boolean value indicating if the checkbox is selected.
  *
  *
  ***************************************************************************************
  **/

  public boolean isSelected(){

    return selected;
  }
  
  /**
  ***************************************************************************************
  *
  * Returns the true of false to indicate if this checkbox is for proshop user pages.
  *
  * @return the boolean value indicating if the checkbox is for proshop user pages.
  *
  *
  ***************************************************************************************
  **/

  public boolean isProshop(){

    return proshop;
  }
  /**
  ***************************************************************************************
  *
  * Updates the value of this attribute with the data from the form.
  *
  * @param request the request object that contains the posted form data
  * @param response the response object
  *
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response)
  {

      String newValue = request.getParameter(getName());      // get the parm with the name from this cell

      if (newValue != null && !(newValue.equals("")))
      {
        selected = true;
      }
        
   /*                // DO NOT set selected=false because the parameter will not be passed for all rows each time.
      else
      {
        selected = false;
      }
   */
     
  }

}
