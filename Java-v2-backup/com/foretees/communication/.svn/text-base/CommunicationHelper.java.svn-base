/***************************************************************************************
 *   CommunicationHelper:  This utility class contains methods to save and retrieve information for gathering
 *                 information necessary to create and edit distribution lists
 *
 *
 *   created: 1/19/2004   jag
 *
 *
 *   last updated:
 *
 *      6/14/13  Fixed issue where the incorrect list name value was being used when updating a proshop dist list, and made it possible to update the name of a list on the proshop side.
 *      9/07/11  Add support for new Pro Distribution Lists (unlimited # of entries).
 *      6/23/11  Burlington CC (burlington) - only allow members to have 10 entries in dist lists (case 1998).
 *     12/10/09  Druid Hills - only allow members to have 12 entries in dist list (case 1754).
 *
 *
 ***************************************************************************************
 */

package com.foretees.communication;

import java.io.*;
import java.util.ArrayList;
import java.sql.*;
import javax.servlet.http.*;

import com.foretees.client.action.Action;
import com.foretees.client.action.ActionHelper;
import com.foretees.client.action.ActionModel;
import com.foretees.client.form.FormModel;
import com.foretees.client.table.Cell;
import com.foretees.client.table.RowModel;
import com.foretees.client.table.TableModel;
import com.foretees.common.FeedBack;
import com.foretees.common.Labels;
import com.foretees.common.Utilities;
import com.foretees.common.ProcessConstants;
import com.foretees.member.Member;
import com.foretees.member.MemberHelper;

/**
 ***************************************************************************************
 *
 *  This helper class contains methods for commonly used functions related to
 *  creating and editing members.
 *
 ***************************************************************************************
 **/

public class CommunicationHelper {

   //initialize the attributes
  private static String versionId = ProcessConstants.CODEBASE;


  /**
  ***************************************************************************************
  *
  * Validates whether the information provided in the request is valid for creating a new
  * distribution list.
  *
  * @param form that contains the information from the request
  *
  ***************************************************************************************
  **/

  public static FeedBack validate(HttpServletRequest req, String theUserName, FormModel form, Connection con, HttpSession session, boolean isNew)
  {

    FeedBack feedback = new FeedBack();
    //verify that the distribution list name does not already exist for this user
    String list_name = req.getParameter(DistributionList.LIST_NAME);
    String user = (String)session.getAttribute("user");

    if (list_name == null || list_name.equals(""))
    {
        feedback = new FeedBack();
        feedback.setPositive(false);
        feedback.addMessage("You must enter a name for the list. ");
        feedback.setAffectedField(DistributionList.LIST_NAME);

    }
    else
    {

      if (isNew)
      {
        feedback = DistributionList.listExists(list_name, user, con, session);
      }
    }

    return feedback;
  }

  /**
  ***************************************************************************************
  *
  * Adds a new distribution list to the database.  The information should be validated
  * before calling this method.
  *
  * @param form that contains the information from the request
  *
  ***************************************************************************************
  **/

  public static FeedBack persist(HttpServletRequest req, FormModel form, PrintWriter out, HttpSession session, Connection con)
  {

    PreparedStatement pstmt = null;     
    int i = 0;
    int max_list_size = DistributionList.getMaxListSize(session);
    String table_name = DistributionList.getTableName(session);

    boolean isProshopUser = ProcessConstants.isProshopUser((String)session.getAttribute("user"));

    //get the table from the form and add the name in the list
    RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
    TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

    //  get this user's user id
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String club = (String)session.getAttribute("club");     // get club

    //get the name for the distribution list
    String list_name = req.getParameter(DistributionList.LIST_NAME);

    // Process according to user - member is limited to 30 entries, proshop is unlimited
    
    if (isProshopUser == false) {      // if member
    
       String[] all_names = new String[max_list_size];

       for (i=0; i<max_list_size; i++)
       {
         all_names[i] = "";       // init array
       }

       for (i=0; i<names.size(); i++)
       {
         all_names[i] = (names.getRow(i)).getId();       // put usernames in array
       }

       // save the distribution list
       try {

          //build the correct statement using the appropriate database table based on the
          //type of the user
          String statement = "INSERT INTO " + table_name + " (name, owner";

          for (int j=1; j<=max_list_size; j++)
          {
           statement = statement + ", user" + j;
          }

          statement = statement + ") VALUES (?,?,";

          for (int k=0; k<max_list_size-1; k++)
          {
            statement = statement + "?,";
          }

          statement = statement + "?)";

          pstmt = con.prepareStatement (statement);

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, list_name);       // put the parm in pstmt
          pstmt.setString(2, user);

          for (i=0; i<max_list_size; i++)
          {
            pstmt.setString(i+3, all_names[i]);
          }

          pstmt.executeUpdate();          // execute the prepared stmt

          pstmt.close();   // close the stmt

       }
       catch (Exception exc) {

         Utilities.logError("Error in CommunicationHelper.persist: Club = " +club+ ", Error = " + exc.getMessage());
        // exc.printStackTrace();
       }

    } else {     // proshop user
       
       ArrayList<String> mem_names = new ArrayList<String>();   // allow for an unlimited number of users
       
       String name = "";

       for (i=0; i<names.size(); i++) {
          
          name = (names.getRow(i)).getId();       // get the rest of the usernames
       
          mem_names.add( name );                  // save the username
       }
       
       i = mem_names.size();              // get number of members to add
       
       // save the distribution list
       try {

          //  Establish the distribution list
          String statement = "INSERT INTO distribution_lists (name, owner, enabled) VALUES (?,?,1)";

          pstmt = con.prepareStatement (statement);

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, list_name);       // put the parm in pstmt
          pstmt.setString(2, user);

          pstmt.executeUpdate();          // execute the prepared stmt
          
          //  Create the entries for this table

          int list_id = 0;
          pstmt = con.prepareStatement("SELECT LAST_INSERT_ID()");
          
          ResultSet rsLastID = pstmt.executeQuery();
          
          if (rsLastID.next()) {
             list_id = rsLastID.getInt(1);         // get the id of the list we just created
          }

          for (int j=0; j < i; j++) {
             
             name = mem_names.get(j);       // get a username 
             
             if (!name.equals("") && name != null) {
             
                pstmt = con.prepareStatement ("INSERT INTO distribution_lists_entries (distribution_list_id, username) VALUES (?,?)");

                pstmt.clearParameters();        // clear the parms
                pstmt.setInt(1, list_id);       // put the parm in pstmt
                pstmt.setString(2, name);

                pstmt.executeUpdate();          // execute the prepared stmt
             }
          }
          
          pstmt.close();   // close the stmt

       }
       catch (Exception exc) {

         Utilities.logError("Error in CommunicationHelper.persist: Club = " +club+ ", Error = " + exc.getMessage());
         //exc.printStackTrace();
       }       
    }  
       
    return new FeedBack();
  }

    /**
  ***************************************************************************************
  *
  * Updates distribution list in the database.  The information should be validated
  * before calling this method.
  *
  * @param form that contains the information from the request
  *
  ***************************************************************************************
  **/

  public static FeedBack update(HttpServletRequest req, FormModel form, PrintWriter out, HttpSession session, Connection con)
  {

    PreparedStatement pstmt = null;     
    int i = 0;
    int max_list_size = DistributionList.getMaxListSize(session);
    String table_name = DistributionList.getTableName(session);

    boolean isProshopUser = ProcessConstants.isProshopUser((String)session.getAttribute("user"));

    //get the table from the form and add the name in the list
    RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
    TableModel names = (TableModel)(((Cell)row.get(0)).getContent());
    
    //get the name for the distribution list
    String list_name = req.getParameter(DistributionList.LIST_NAME);
    String org_list_name = req.getParameter(DistributionList.ORIGINAL_LIST_NAME);

    //
    //  get this user's user id
    //
    String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
    String club = (String)session.getAttribute("club");     // get club

    
    // Process according to user - member is limited to 30 entries, proshop is unlimited
    
    if (isProshopUser == false) {      // if member    
    
       String[] all_names = new String[max_list_size];

       for (i=0; i<max_list_size; i++)
       {
         all_names[i] = "";       // init array
       }

       for (i=0; i<names.size(); i++) {
      
         all_names[i] = (names.getRow(i)).getId();       // put usernames in array
       }

       // save the distribution list
       try {

          String statement = "UPDATE " + table_name + " SET name = ?";

          for (int j=1; j<=max_list_size; j++)
          {
            statement = statement + ", user" + j + " = ?";
          }

          statement = statement + " WHERE name = ? AND owner = ?";        
          
          pstmt = con.prepareStatement (statement);

          pstmt.clearParameters();        // clear the parms
          pstmt.setString(1, list_name);       // put the parm in pstmt
          pstmt.setString(max_list_size + 2, org_list_name);
          pstmt.setString(max_list_size + 3, user);

          for (i=0; i<max_list_size; i++)
          {
            pstmt.setString(i+2, all_names[i]);
          }

          pstmt.executeUpdate();          // execute the prepared stmt

          pstmt.close();   // close the stmt

       }
       catch (Exception exc) {
         Utilities.logError("Error in CommunicationHelper.update: Club = " +club+ ", Error = " + exc.getMessage());
       }

    } else {     // proshop user - for now we will simply delete all the entries and rebuild them - may need to change this later for members to opt out
       
       ArrayList<String> mem_names = new ArrayList<String>();   // allow for an unlimited number of users
       
       String name = "";

       for (i=0; i<names.size(); i++) {
      
          name = (names.getRow(i)).getId();       // get the first username
          
          mem_names.add( name );                  // save the username
       }
       
       i = mem_names.size();             // get number of members to add
       
       // delete the old entries and then save the updated distribution list
       try {
          
          int list_id = 0;

          //  Establish the distribution list
          String statement = "SELECT id FROM distribution_lists WHERE name = ? AND owner = ? AND enabled = 1";

          pstmt = con.prepareStatement (statement);
        
          pstmt.clearParameters();      
          pstmt.setString(1, org_list_name);     
          pstmt.setString(2, user);     

          ResultSet rsLastID = pstmt.executeQuery();        
          
          if (rsLastID.next()) {
             list_id = rsLastID.getInt(1);         // get the id of the list
          }

          //  Remove all the old entries
          pstmt = con.prepareStatement ("DELETE FROM distribution_lists_entries WHERE distribution_list_id = ?");

          pstmt.clearParameters();       
          pstmt.setInt(1, list_id);       

          pstmt.executeUpdate();          
          
                    
          //  Create the new entries for this table

          for (int j=0; j < i; j++) {
             
             name = mem_names.get(j);       // get a username 
             
             if (!name.equals("") && name != null) {
             
                pstmt = con.prepareStatement ("INSERT INTO distribution_lists_entries (distribution_list_id, username) VALUES (?,?)");

                pstmt.clearParameters();        // clear the parms
                pstmt.setInt(1, list_id);       // put the parm in pstmt
                pstmt.setString(2, name);

                pstmt.executeUpdate();          // execute the prepared stmt
             }
          }
          
          // Update name, if necessary
          if (!list_name.equals(org_list_name)) {
              pstmt = con.prepareStatement("UPDATE distribution_lists SET name = ? WHERE name = ? AND owner = ?");
              pstmt.clearParameters();
              pstmt.setString(1, list_name);
              pstmt.setString(2, org_list_name);
              pstmt.setString(3, user);
              
              pstmt.executeUpdate();
          }
          
          pstmt.close();   // close the stmt

       }
       catch (Exception exc) {

         Utilities.logError("Error in CommunicationHelper.update: Club = " +club+ ", Error = " + exc.getMessage());
         //exc.printStackTrace();
       }       
    }  
    
    return new FeedBack();
  }

  /**
  ***************************************************************************************
  *
  * Deletes a distribution list in the  database.
  *
  * @param form that contains the information from the request
  *
  ***************************************************************************************
  **/

  public static FeedBack delete(HttpServletRequest req, FormModel form, PrintWriter out, HttpSession session, Connection con)
  {

    PreparedStatement stmt = null;
    String list_name = req.getParameter(DistributionList.LIST_NAME);
    String user = (String)session.getAttribute("user");
    String club = (String)session.getAttribute("club");     // get club
    boolean isProshopUser = ProcessConstants.isProshopUser(user);
    String table_name = DistributionList.getTableName(session);


    // Process according to user - member is limited to 30 entries, proshop is unlimited
    
    if (isProshopUser == false) {      // if member    
    
       try
       {
           stmt = con.prepareStatement (
                    "Delete FROM " + table_name + " WHERE name = ? AND owner = ?");

           stmt.clearParameters();               // clear the parms
           stmt.setString(1, list_name);            // put the parm in stmt
           stmt.setString(2, user);
           stmt.executeUpdate();                 // execute the prepared stmt

           stmt.close();
       }
       catch (Exception exc)
       {
         Utilities.logError("Error in CommunicationHelper.delete: Club = " +club+ ", Error = " + exc.getMessage());
       }

    } else {     // proshop user 
       
       // delete the old entries and then delete the list
       try {
          
          int list_id = 0;

          //  Establish the distribution list
          String statement = "SELECT id FROM distribution_lists WHERE name = ? AND owner = ? AND enabled = 1";

          stmt = con.prepareStatement (statement);

          stmt.clearParameters();      
          stmt.setString(1, list_name);     
          stmt.setString(2, user);     

          ResultSet rsLastID = stmt.executeQuery();        
          
          if (rsLastID.next()) {
             list_id = rsLastID.getInt(1);         // get the id of the list
          }

          //  Remove all the old entries
          stmt = con.prepareStatement ("DELETE FROM distribution_lists_entries WHERE distribution_list_id = ?");

          stmt.clearParameters();       
          stmt.setInt(1, list_id);       

          stmt.executeUpdate();          
          
          //  Remove the list
          stmt = con.prepareStatement ("DELETE FROM distribution_lists WHERE id = ?");

          stmt.clearParameters();       
          stmt.setInt(1, list_id);       

          stmt.executeUpdate();  
          
          stmt.close();
       
       }
       catch (Exception exc)
       {
         Utilities.logError("Error in CommunicationHelper.delete: Club = " +club+ ", Error = " + exc.getMessage());
       }
       
    }
       
    return new FeedBack();

  }

  
  
  public static FeedBack addToList(FormModel form, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    String items = req.getParameter(ActionHelper.SELECTED_ITEMS_STRING);
    FeedBack feedback = null;

    if (items != null && !(items.equals("")))
    {

      ArrayList user_names = ActionHelper.getSelectedNames(items);

      //get the table from the form and add the name in the list
      RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
      TableModel names = (TableModel)(((Cell)row.get(0)).getContent());
      
      int max = DistributionList.getMaxListSize(session);    // get max members allowed in dist list

      String user = (String)session.getAttribute("user");     // get username ('proshop' or member's username)
      String club = (String)session.getAttribute("club");     // get club

      if (club.equals("dhgc") && !user.startsWith("proshop")) {   // if Druid Hills and a member
         
         max = 12;           // max of 12 entries allowed
      }

      if (club.equals("burlington") && !user.startsWith("proshop")) {   // If Burlington CC and a member

         max = 10;      // max of 10 entries allowed
      }
         

      for (int i=0; i<user_names.size(); i++)
      {

        if (names.size() < max)
        {

          //check to see if this name is already in the list
          String name_to_add = (String)(user_names.get(i));
          RowModel nameRow = names.getRow(name_to_add);

          if (nameRow == null)
          {
            try
            {
              nameRow = new RowModel();
              nameRow.setId(name_to_add);
              String displayName = "";

              String search_type = (String)(req.getParameter(ActionHelper.SEARCH_TYPE));

              if (search_type.equals(ActionHelper.SEARCH_MEMBERS))
              {
                displayName = MemberHelper.getMemberDisplayName(con, name_to_add, out);
              }

              nameRow.add(displayName);
              ActionModel actions = new ActionModel();
              String removeUrl = "javascript:removeNameFromList('" + versionId + "servlet/Edit_distributionlist', '" + ActionHelper.REMOVE_FROM_LIST + "', '" + name_to_add + "')";
              Action removeAction = new Action(ActionHelper.REMOVE, Labels.REMOVE, "Remove this member from the list.", removeUrl);

              actions.add(removeAction);
              nameRow.add(actions);
              names.addRow(nameRow);
            }
            catch (SQLException sqle)
            {
              //what to do
            }
          }
        }
        else
        {
          feedback = new FeedBack();
          feedback.setPositive(false);
          feedback.addMessage("The maximum number of members for a list is " + max + ".  Some of the selected members may not have been added.");
        }

      }

      ActionModel model = names.getContextActions();
      Action searchAction = (Action)(model.get(0));
      if (names.size()<DistributionList.getMaxListSize(session))
      {
        searchAction.setSelected(false);
      }
      else
      {
        searchAction.setSelected(true);
      }

    }

    return feedback;

  }

  public static void removeFromList(FormModel form, HttpSession session, HttpServletRequest req, HttpServletResponse res, Connection con, PrintWriter out)
    throws IOException
  {

    //the user has submitted a name to add to the list
    String name_to_remove = req.getParameter(Member.REQ_USER_NAME);

    if (name_to_remove != null && !(name_to_remove.equals("")))
    {

      //get the table from the form and add the name in the list
      RowModel row = form.getRow(DistributionList.LIST_OF_NAMES);
      TableModel names = (TableModel)(((Cell)row.get(0)).getContent());

      names.remove(name_to_remove);

      ActionModel model = names.getContextActions();
      Action searchAction = (Action)(model.get(0));
      if (names.size()<DistributionList.getMaxListSize(session))
      {
        searchAction.setSelected(false);
      }
      else
      {
        searchAction.setSelected(true);
      }
    }


  }

}
