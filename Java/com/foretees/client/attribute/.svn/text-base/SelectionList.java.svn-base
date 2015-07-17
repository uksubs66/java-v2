/***************************************************************************************
 *   SelectionList:  This class can be used to construct a dropdown selection list
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *   04/14/10  Updated the options array decleration so that it's not using unchecked raw type
 *
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;
import javax.servlet.http.*;
import java.util.ArrayList;

//import com.foretees.client.attribute.Option;

/**
 ***************************************************************************************
 *
 *   This class holds information to construct a html select component
 *
 ***************************************************************************************
 **/

public class SelectionList {

  // Initialize the attributes

  private String name = "";
  private String label = "";
  private String tooltip = "";
  private String onClick = "";
  private ArrayList<Option> options = new ArrayList<Option>();
  private boolean oneIsSelected = false;
  private boolean required = true;
  private String width = "1";
  private boolean multi = false;
  private String size = "1";
  private boolean showAsList = false;

   /**
  ***************************************************************************************
  *
  * This constructor initializes the name for the selection list.
  *
  ***************************************************************************************
  **/

  public SelectionList(String theName, String theLabel, boolean isRequired)
  {
    label = theLabel;
    name = theName;
    required = isRequired;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the selection list and if a value
  * is required to be set for this.  If a value is required the field will be rendered
  * without a blank option.  By default an option will be required
  *
  ***************************************************************************************
  **/

  public SelectionList(String theName)
  {

    name = theName;

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the selection list as well as creates
  * options for each element in the String array passed in.  The display name and display value
  * will be identical.  This will also set the element that matches the selected string as the
  * selected option.
  *
  ***************************************************************************************
  **/

  public SelectionList(String theName, String theLabel, String[] content, String selected)
  {
    this(theName, theLabel, content, null, selected);
  }

    /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the selection list as well as creates
  * options for each element in the String array passed in.  The display name and display value
  * will be identical.  This will also set the element that matches the selected string as the
  * selected option.
  *
  ***************************************************************************************
  **/

  public SelectionList(String theName, String theLabel, String[] content, String[] displayValues, String selected)
  {
    label = theLabel;
    name = theName;


    if (content != null && content.length > 0)
    {
      for (int i=0; i<content.length; i++){

        String option = content[i];
        boolean optSelected = false;

        if (option != null  && !(option.equals("")))
        {

          if (option.equals(selected))
          {
            optSelected = true;
          }

          String dispValue = null;

          if (displayValues != null && displayValues.length > 0)
          {
            dispValue = displayValues[i];
          }

          if (dispValue == null)
            dispValue = option;


          this.addOption(dispValue, option, optSelected);
        }

      }
    }
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this selection list when displayed in the user interface.
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
  * Returns the text to be used as the label for this selection list when displayed in the user
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
  * Sets whether this selection list should allow more than one selection.  The default if not
  * set is false.
  *
  * @param allowMulti the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setMultiSelect(boolean allowMulti){

    multi = allowMulti;
  }

  /**
  ***************************************************************************************
  *
  * Returns whether this selection list allows more than one selection
  *
  * @return indicates if this list allows multi selection
  *
  *
  ***************************************************************************************
  **/

  public boolean isMultiSelect(){

    return multi;
  }


  /**
  ***************************************************************************************
  *
  * Adds a option to the selection list.
  *
  * @param displayName the text to show for this option
  * @param displayValue the id to use for this option
  *
  ***************************************************************************************
  **/

  public void addOption(String displayName, String displayValue, boolean isSelected){

    Option option = new Option(displayName, displayValue, isSelected);

    //we need to keep track if one is selected so when this component is rendered
    //the renderer can determine the appropriate behavior eg:  set the first as default
    //render an empty option if that is valid (based on whether a selection is required).
    oneIsSelected = isSelected;

    options.add(option);

  }

  /**
  ***************************************************************************************
  *
  * Returns the option from the specified index.
  *
  * @return the option at the specified index.
  *
  ***************************************************************************************
  **/

  public Option getOption(int theIndex){

    return (options.get(theIndex));
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
  * Sets the width to use when displaying this attribute in the user interface when in edit mode.
  *
  * @param theWidth the width for the input field.
  *
  ***************************************************************************************
  **/

  public void setWidth(String theWidth){

    width = theWidth;
  }

  /**
  ***************************************************************************************
  *
  * Returns the width to be used when displaying this attribute in the user
  * interface in edit mode.
  *
  * @return the width.
  *
  *
  ***************************************************************************************
  **/

  public String getWidth(){

    return width;
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
  * Sets the function to execute upon the onClick event for this component
  *
  * @param theFunction the function to execute.
  *
  ***************************************************************************************
  **/

  public void setOnClick(String theFunction)
  {

    onClick = theFunction;
  }

  /**
  ***************************************************************************************
  *
  * Returns the function to use when clicking on the list.
  *
  * @return the function.
  *
  ***************************************************************************************
  **/

  public String getOnClick()
  {

    return onClick;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether to always show this as a list or use the default logic
  *
  * @param alwaysShowAsList indicates whether to always display as list if true
  *
  ***************************************************************************************
  **/

  public void setAlwaysShowAsList(boolean alwaysShowAsList)
  {

    showAsList = alwaysShowAsList;
  }

  /**
  ***************************************************************************************
  *
  * Returns whether to always show as list
  *
  * @return the function.
  *
  ***************************************************************************************
  **/

  public boolean getAlwaysShowAsList()
  {

    return showAsList;
  }


  /**
  ***************************************************************************************
  *
  * Returns the option that has the given option name.
  *
  * @return the option, null if a option is not found with that name.
  *
  ***************************************************************************************
  **/

  public Option getOption(String name)
  {

    Option option = null;
    for (int i=0; i<options.size(); i++)
    {

      Option nxtOpt = options.get(i);
      if ((nxtOpt.getValue()).equals(name))
      {
        option = nxtOpt;
        break;
      }
    }
    return option;

  }

  /**
  ***************************************************************************************
  *
  * Returns the selected option if there is one.
  *
  * @return the selected option, null if an option is not selected.
  *
  ***************************************************************************************
  **/

  public Option getSelectedOption()
  {

    Option option = null;
    for (int i=0; i<options.size(); i++)
    {

      Option nxtOpt = options.get(i);
      if (nxtOpt.isSelected())
      {
        option = nxtOpt;
        break;
      }
    }
    return option;

  }

  /**
  ***************************************************************************************
  *
  * Updates the value of this selection list with the data from the form.
  *
  * @param request the request object that contains the posted form data
  * @param response the response object
  *
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
  {

    if (options != null)
    {
      Option option = getSelectedOption();

      String newValue = request.getParameter(getName());

      if (newValue != null)
      {

        if (option != null && !((option.getValue()).equals(newValue)))
        {
          option.setSelected(false);
        }

        Option selectedOption = getOption(newValue);

        if (selectedOption != null)
        {
          selectedOption.setSelected(true);
        }
      }

    }
  }

}
