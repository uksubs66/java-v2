/***************************************************************************************
 *   WizardStep:  This class represents a step in a wizard.
 *
 *
 *   created: 10/02/2003   jag
 *
 *   last updated:       ******* keep this accurate *******
 *
 ***************************************************************************************
 */

package com.foretees.client.wizard;

import java.io.*;

import com.foretees.client.form.FormModel;

/**
 ***************************************************************************************
 *
 *   This class holds information necessary to properly construct a step in a wizard.
 *
 ***************************************************************************************
 **/

public class WizardStep {

  // Initialize the attributes

  private String name = "";
  private String label = "Label not initialized";
  private boolean selected = false;
  private FormModel form = null;

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the wizard step as well as whether
  * the step is selected.
  *
  ***************************************************************************************
  **/

  public WizardStep(String theName, String theLabel, FormModel theForm, boolean isSelected)
  {
    label = theLabel;
    selected = isSelected;
    name = theName;
    form = theForm;
  }

  /**
  ***************************************************************************************
  *
  * This constructor initializes the name and label for the action.
  *
  ***************************************************************************************
  **/

  public WizardStep(String theName, String theLabel, FormModel theForm)
  {
    label = theLabel;
    name = theName;
    form = theForm;
  }

  /**
  ***************************************************************************************
  *
  * Sets the form to use for this step.
  *
  * @param theForm the form model for this step
  *
  ***************************************************************************************
  **/

  public void setForm(FormModel theForm){

    form = theForm;
  }

  /**
  ***************************************************************************************
  *
  * Returns the form for this step.
  *
  * @return the form model.
  *
  *
  ***************************************************************************************
  **/

  public FormModel getForm(){

    return form;
  }

  /**
  ***************************************************************************************
  *
  * Sets the text to use as the label for this step when displayed in the user interface.
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
  * Returns the text to be used as the label for this step when displayed in the user
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
  * Sets the name to use to identify this step.  This name should be unique for all steps
  * within a WizardModel.
  *
  * @param theName the name to use to identify this action.
  * @see WizardModel
  *
  ***************************************************************************************
  **/

  public void setName(String theName){

    name = theName;
  }

  /**
  ***************************************************************************************
  *
  * Returns the name that identifies this step.
  *
  * @return the name to use for this step.
  *
  ***************************************************************************************
  **/

  public String getName(){

    return name;
  }

  /**
  ***************************************************************************************
  *
  * Sets whether this step should show as selected in the user interface.
  *
  * @param isSelected indicates if this step should show as selected.
  * @see WizardModel
  *
  ***************************************************************************************
  **/

  public void setSelected(boolean isSelected){

    selected = isSelected;
  }

  /**
  ***************************************************************************************
  *
  * Returns true if this step should be displayed as selected in the user interface.
  *
  * @return true if this action should be selected.
  * @see WizardModel
  *
  ***************************************************************************************
  **/

  public boolean isSelected(){

    return selected;
  }
}