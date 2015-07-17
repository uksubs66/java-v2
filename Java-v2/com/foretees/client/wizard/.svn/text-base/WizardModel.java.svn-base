/***************************************************************************************
 *   WizardModel:  This class represents the steps and content of a Wizard
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 *          4/24/08  Update ArrayList to use WizardStep object instead of raw type
 *
 *
 ***************************************************************************************
 */

package com.foretees.client.wizard;

import java.io.*;
import java.util.ArrayList;

import javax.servlet.http.*;

import com.foretees.client.form.FormModel;
import com.foretees.client.table.Cell;
import com.foretees.client.table.RowModel;
import com.foretees.client.wizard.WizardStep;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly construct a wizard.
 *
 ***************************************************************************************
 **/

public class WizardModel {

  //*****************************************************
  // Initialize the attributes
  //*****************************************************

  private ArrayList <WizardStep>steps = new ArrayList<WizardStep>();
  private String label = "Header label is not initialized";
  private String name = "";
  private int selectedStep = 0;

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the wizard
  *
  ***************************************************************************************
  **/

  public WizardModel(String theName, String theLabel)
  {
    label = theLabel;
    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this wizard when displayed in the user interface.
  *
  * @param theLabel the string text to use as the label.
  *
  ***************************************************************************************
  **/

  public void setLabel(String theLabel)
  {

    label = theLabel;
  }

   /**
  ***************************************************************************************
  *
  * Returns the text to be used as the label for this wizard when displayed in the user
  * interface.
  *
  * @return the text to use as the label.
  *
  *
  ***************************************************************************************
  **/

  public String getLabel()
  {

    return label;
  }

  /**
  ***************************************************************************************
  *
  * Adds a step to the wizard.
  *
  * @param theStep the wizard step to add to this wizard.
  *
  ***************************************************************************************
  **/

  public void addStep(WizardStep theStep)
  {

    steps.add(theStep);

  }

  /**
  ***************************************************************************************
  *
  * Sets this step as the selected step.
  *
  * @param theStep the wizard step to add to this wizard.
  *
  ***************************************************************************************
  **/

  public void setSelectedStep(int theStep)
  {

    selectedStep = theStep;


  }

  /**
  ***************************************************************************************
  *
  * Returns the selected step.
  *
  * @param theStep the selected wizard step.
  *
  ***************************************************************************************
  **/

  public int getSelectedStep()
  {

    if (selectedStep < 0 || selectedStep > steps.size())
      selectedStep = 0;
    return selectedStep;


  }

  /**
  ***************************************************************************************
  *
  * Returns the wizard step at the given index.
  *
  * @param theIndex the index of the wizard step to return.
  *
  ***************************************************************************************
  **/

  public WizardStep getStep(int theIndex)
  {

    return (WizardStep)(steps.get(theIndex));
  }

   /**
  ***************************************************************************************
  *
  * Returns the number of steps in this wizard.
  *
  * @return the number of steps.
  *
  ***************************************************************************************
  **/

  public int size()
  {

    return steps.size();

  }

  /**
  ***************************************************************************************
  *
  * Updates the form for the selected step with the form data.
  *
  * @param theStep the selected wizard step.
  *
  ***************************************************************************************
  **/

  public void update(HttpServletRequest request, HttpServletResponse response, PrintWriter out)
  {


    WizardStep selectedStep = getStep(getSelectedStep());

    if (selectedStep != null)
    {
      FormModel form = selectedStep.getForm();

      if (form != null)
      {
        for (int i=0; i<form.numRows(); i++)
        {

          RowModel row = form.getRow(i);

          if (row != null)
          {
            for (int j=0; j<row.size(); j++)
            {
              Cell cell = row.get(j);

              cell.update(request, response, out);

            }
          }


        }
      }
    }

  }

}