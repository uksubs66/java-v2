/***************************************************************************************
 *   DatePicker:  This class can be used to construct a month/day/year attribute
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.attribute;

import java.io.*;
import java.lang.Integer;
import javax.servlet.http.*;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly construct an editable date field.
 *
 ***************************************************************************************
 **/

public class DatePicker implements Serializable {
    
    private static final long serialVersionUID = 1L;

  // Initialize the attributes

  private String name = "";
  private String label = "Label not initialized";
  private boolean required = true;
  private String tooltip = "";
  private int month = 0;
  private int day = 0;
  private int year = 0;
  private int years = 5;
  private int startYear = 0;
  private int endYear = 0;

  public static String[] monthsDisplay = new String[12];
  public static String[] months = new String[12];
  public static String[] days = new String[31];
  public String dayField = "";
  public String monthField = "";
  public String yearField = "";


  static {

    monthsDisplay[0] = "JAN";
    monthsDisplay[1] = "FEB";
    monthsDisplay[2] = "MAR";
    monthsDisplay[3] = "APR";
    monthsDisplay[4] = "MAY";
    monthsDisplay[5] = "JUN";
    monthsDisplay[6] = "JUL";
    monthsDisplay[7] = "AUG";
    monthsDisplay[8] = "SEP";
    monthsDisplay[9] = "OCT";
    monthsDisplay[10] = "NOV";
    monthsDisplay[11] = "DEC";



    int count = 0;
    while (count < 12)
    {
      months[count] = (new Integer(count + 1)).toString();
      count++;
    }

    count = 0;
    while (count < 31)
    {
      days[count] = (new Integer(count + 1)).toString();
      count++;
    }

  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the date picker.
  *
  ***************************************************************************************
  **/

  public DatePicker(String theName, String theLabel)
  {
    label = theLabel;
    name = theName;
    dayField = name + "_" + "day";
    monthField = name + "_" + "month";
    yearField = name + "_" + "year";
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name, label, month, day and year for this date picker.
  *
  ***************************************************************************************
  **/

  public DatePicker(String theName, String theLabel, int theMonth, int theDay, int theYear)
  {
    label = theLabel;
    name = theName;
    month = theMonth;
    day = theDay;
    year = theYear;
    dayField = name + "_" + "day";
    monthField = name + "_" + "month";
    yearField = name + "_" + "year";

  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this date picker when displayed in the user interface.
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
  * Returns the text to be used as the label for this date picker when displayed in the user
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
  * Sets the month to show as selected for this date picker.
  *
  * @param theMonth the month to show as selected
  *
  ***************************************************************************************
  **/

  public void setMonth(int theMonth){

    month = theMonth;
  }

  /**
  ***************************************************************************************
  *
  * Returns the month to be used when displaying this date picker in the user
  * interface.
  *
  * @return the month to show as selected.
  *
  ***************************************************************************************
  **/

  public int getMonth(){

    return month;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the tooltip for this date picker when displayed in the user
  * interface.
  *
  * @param theToolTip the text to use for this date picker.
  *
  ***************************************************************************************
  **/

  public void setToolTip(String theToolTip){

    tooltip = theToolTip;
  }

  /**
  ***************************************************************************************
  *
  * Returns the string to be used as the tooltip for this date picker when displayed in the user
  * interface.
  *
  * @return the tooltip to use for this date picker.
  *
  ***************************************************************************************
  **/

  public String getToolTip(){

    return tooltip;
  }

  /**
  ***************************************************************************************
  *
  * Sets the name to use to identify this date picker.  This name should be unique for all
  * date pickers that will be shown in the same page.  This name will be used for the
  * component rendered in the html form.
  *
  * @param theName the name to use to identify this date picker.
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this date picker.
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
  * Sets the day of the month to be selected for this date picker.
  *
  * @param theDay the day of the month to be selected.
  *
  ***************************************************************************************
  **/

  public void setDay(int theDay){

    day = theDay;
  }

  /**
  ***************************************************************************************
  *
  * Returns the day of the month to be selected for this date picker.
  *
  * @return the day of the month to be selected.
  *
  ***************************************************************************************
  **/

  public int getDay(){

    return day;
  }

  /**
  ***************************************************************************************
  *
  * Sets the year to be selected when displaying this date picker.
  *
  * @param theYear the year to display as selected for this date picker.
  *
  ***************************************************************************************
  **/

  public void setYear(int theYear){

    year = theYear;
  }

  /**
  ***************************************************************************************
  *
  * Returns the year to be selected when displaying this date picker.
  *
  * @return the year to be selected for this date picker.
  *
  ***************************************************************************************
  **/

  public int getYear(){

    return year;
  }

    /**
  ***************************************************************************************
  *
  * Sets the year to start the date picker.
  *
  * @param theYear the year to start for this date picker.
  *
  ***************************************************************************************
  **/

  public void setStartYear(int theYear){

    startYear = theYear;
  }

  /**
  ***************************************************************************************
  *
  * Returns the year to start the date picker.
  *
  * @return the year start for this date picker.
  *
  ***************************************************************************************
  **/

  public int getStartYear(){

    return startYear;
  }

    /**
  ***************************************************************************************
  *
  * Sets the year to end this date picker.
  *
  * @param theYear the year to end this date picker.
  *
  ***************************************************************************************
  **/

  public void setEndYear(int theYear){

    endYear = theYear;
  }

  /**
  ***************************************************************************************
  *
  * Returns the year to end this date picker.
  *
  * @return the year to end this date picker.
  *
  ***************************************************************************************
  **/

  public int getEndYear(){

    return endYear;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether this date field is required.
  *
  * @param required whether the date is required.
  *
  ***************************************************************************************
  **/

  public void setRequired(boolean isRequired){

    required = isRequired;
  }

  /**
  ***************************************************************************************
  *
  * Returns whether this date field is required.
  *
  * @return whether the date is required.
  *
  ***************************************************************************************
  **/

  public boolean isRequired(){

    return required;
  }

  /**
  ***************************************************************************************
  *
  * Sets the number of years to allow for selection in addition to the current year.  The
  * default is 5 if not set.
  *
  * @param theYears indicates the number of years to allow for selection in addition to the
  *                 current year
  *
  ***************************************************************************************
  **/

  public void setYears(int theYears){

    years = theYears;
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

    String day = request.getParameter(dayField);
    if (day == null || day.equals(""))
      day = "0";
    setDay(new Integer(day).intValue());

    String month = request.getParameter(monthField);
    if (month == null || month.equals(""))
      month = "0";
    setMonth(new Integer(month).intValue());

    String year = request.getParameter(yearField);
    if (year == null || year.equals(""))
      year = "0";
    setYear(new Integer(year).intValue());

  }

}