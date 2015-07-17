/***************************************************************************************
 *   MultiChooser:  This class can be used to construct a multi chooser component including
 *                  two select lists and the buttons to move the elements from one list to
 *                  another
 *
 *
 *   created: 11/25/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;

import com.foretees.client.attribute.SelectionList;

/**
 ***************************************************************************************
 *
 *   This class holds information to construct a a multi chooser component
 *
 ***************************************************************************************
 **/

public class MultiChooser implements Serializable {

    private static final long serialVersionUID = 1L;
    
  // Initialize the attributes

  private String name = "";
  private String label = "";
  private String tooltip = "";
  private ArrayList options = new ArrayList();
  private SelectionList source = new SelectionList("source");
  private SelectionList target = new SelectionList("target");
  private boolean oneIsSelected = false;
  private boolean required = true;
  private String size = "1";


  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the multi chooser and if a value
  * is required to be set for this.  By default an option will be required
  *
  ***************************************************************************************
  **/

  public MultiChooser(String theName, String theLabel, boolean isRequired)
  {
    label = theLabel;
    name = theName;
    required = isRequired;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the multi chooser as well as creates
  * options for each element in the String array passed in for the source and the target list.
  * The display name and display value will be identical.
  *
  ***************************************************************************************
  **/

  public MultiChooser(String theName, String theLabel, String[] sourceItems, String[] targetItems)
  {
    label = theLabel;
    name = theName;


    updateLists(sourceItems, targetItems);

  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this multi chooser when displayed in the user interface.
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
  * Returns the text to be used as the label for this multi chooser when displayed in the user
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
  * Returns the source selection list for this multi chooser.
  *
  * @return the source selection list.
  *
  ***************************************************************************************
  **/

  public SelectionList getSourceList(){

    return source;
  }

  /**
  ***************************************************************************************
  *
  * Returns the target selection list for this multi chooser.
  *
  * @return the source selection list.
  *
  ***************************************************************************************
  **/

  public SelectionList getTargetList(){

    return target;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the tooltip for this selection list when displayed in the user
  * interface.
  *
  * @param theToolTip the text to use for this selection list.
  *
  ***************************************************************************************
  **/

  public void setToolTip(String theToolTip){

    tooltip = theToolTip;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the tooltip for this selection list when displayed in the user
  * interface.
  *
  * @return the tooltip to use for this selection list.
  *
  ***************************************************************************************
  **/

  public String getToolTip(){

    return tooltip;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this selection list.  This name should be unique for all
  * selection lists that will be shown in the same page.  This name will be used for the
  * component rendered in the html form.
  *
  * @param theName the name to use to identify this selection list.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }


  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this selection list.
  *
  * @return the url to use for this action.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Returns true if a value is required to be selected.
  *
  * @return indicates if a selected value is required.
  *
  ***************************************************************************************
  **/

  public boolean isRequired(){

    return required;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether a value is required for the selection list.
  *
  * @param isRequired the value to set for if a value is required.
  *
  ***************************************************************************************
  **/

  public void setRequired(boolean isRequired){

    required = isRequired;
  }

  /**
  ***************************************************************************************
  *
  * Returns true if one of the options are selected.
  *
  * @return the value indicating if one of the options is selected.
  *
  ***************************************************************************************
  **/

  public boolean isOneSelected(){

    return oneIsSelected;
  }

  /**
  ***************************************************************************************
  *
  * Returns the number of options available for this selection list.
  *
  * @return the number of options.
  *
  ***************************************************************************************
  **/

  public int size(){

    return options.size();
  }

   /**
  ***************************************************************************************
  *
  * Sets the size to use when displaying this selection list in the user interface when in edit mode.
  *
  * @param theSize the width for the input field.
  *
  ***************************************************************************************
  **/

  public void setSize(String theSize){

    size = theSize;
  }

  /**
  ***************************************************************************************
  *
  * Returns the size to be used when displaying this selection list in the user
  * interface in edit mode.
  *
  * @return the size.
  *
  *
  ***************************************************************************************
  **/

  public String getSize(){

    return size;
  }

  /**
  ***************************************************************************************
  *
  * Updates the value of this multi chooser with the data from the form.
  *
  * @param request the request object that contains the posted form data
  * @param response the response object
  *
  *
  ***************************************************************************************
  **/

  private void updateLists(String[] sourceItems, String []targetItems)
  {
     if (sourceItems != null && sourceItems.length > 0)
    {
      for (int i=0; i<sourceItems.length; i++){

        String option = sourceItems[i];

        if (option != null  && !(option.equals("")))
        {
          source.addOption(option, option, false);
        }

      }
    }

    if (targetItems != null && targetItems.length > 0)
    {
      oneIsSelected = true;
      for (int i=0; i<targetItems.length; i++){

        String option = targetItems[i];

        if (option != null  && !(option.equals("")))
        {
          target.addOption(option, option,false);
        }

      }
    }
  }

}