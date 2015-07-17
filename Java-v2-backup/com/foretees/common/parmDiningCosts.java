/*****************************************************************************************
 *   parmDiningCosts:  This class parse the dining event costs string in to useable parts
 *
 *
 *   called by:  several
 *
 *   created: 06/16/2011   Paul S.
 *
 *   last updated:
 *
 *
 *
 *
 *****************************************************************************************
 */


package com.foretees.common;

import java.util.ArrayList;


/**
 *
 * @author sindep
 */
public class parmDiningCosts {

    
    public String costs = "";   // holds the data from the "costs" field in the dining event table
    public String err_string = "";
    public String err_message = "";
    
    public String [] costA;
    public String [] cost_codeA;
    public String [] price_categoryA;
    public String [] price_typeA;
    
    public int costs_found = 0;
    
    public String cost;
    public String cost_code;
    public String price_category;
    public String price_type;
    
    
 public void setOne(parmDiningCosts parmDC, String pPrice_category) {

    // reset for sanity
    cost = "";
    cost_code = "";
    price_category = "";
    price_type = "";

    // loop over the array and find the requested category
    // then store all the cost information in
    for (int i = 0; i < parmDC.price_categoryA.length; i++) {

        if (parmDC.price_categoryA[i].equals(pPrice_category)) {

            cost = parmDC.costA[i];
            cost_code = parmDC.cost_codeA[i];
            price_category = parmDC.price_categoryA[i];
            price_type = parmDC.price_typeA[i];

        }

    }

 }
 

 public int findIndex(parmDiningCosts parmDC, String pPrice_category) {

    int i = 0;
    boolean found = false;

    // loop over the array and find the requested category
    // then store all the cost information in
    for (i = 0; i < parmDC.price_categoryA.length; i++) {
        if (parmDC.price_categoryA[i].equals(pPrice_category)) {
            found = true;
            break;
        }
    }

    return (found) ? i : -1;

 }


 public boolean parseCosts() {
     
 
    boolean result = false;
    
    int pos1 = 0, pos2 = 0, marker = 0, last = 0, i = 0;
    String tmp = "";

    last = costs.lastIndexOf("!ruby/object:Cost");

    // use array lists for temporary storage
    ArrayList<String> listArray1 = new ArrayList<String> ();
    ArrayList<String> listArray2 = new ArrayList<String> ();
    ArrayList<String> listArray3 = new ArrayList<String> ();

    try {

        while (marker < last) {

            pos1 = costs.indexOf("amount: ", marker) + 7;
            pos2 = costs.indexOf(" currency", marker);
            tmp = costs.substring(pos1, pos2).trim();
            if (tmp.endsWith(".0")) tmp += "0";
            listArray1.add(tmp); // cost array

            //costA[i] = tmp;
            //Utilities.logError("parmDiningCosts: costA[" + i + "], " + pos1 + ", " + pos2 + ", " + costA[i] + ", " + marker + "");

            pos1 = costs.indexOf("price_category: ", marker) + 15;
            pos2 = costs.indexOf("price_type_id:", marker);
            tmp = costs.substring(pos1, pos2).replace("\"", "").trim();
            listArray2.add(tmp); // category array

            //price_categoryA[i] = tmp.trim();
            //Utilities.logError("<parmDiningCosts: price_categoryA[" + i + "], " + pos1 + ", " + pos2 + ", " + price_categoryA[i] + ", " + marker + "");

            pos1 = costs.indexOf("price_type_id: ", marker) + 14;
            pos2 = costs.indexOf("remote_item_id:", pos1);
            tmp = costs.substring(pos1, pos2).replace("\"", "").trim();
            listArray3.add(tmp); // price type array

            //price_typeA[i] = tmp.trim().replace("\"", "");
            //Utilities.logError("parmDiningCosts: price_typeA[" + i + "], " + pos1 + ", " + pos2 + ", " + price_typeA[i] + ", " + marker + "");

            marker = pos2;
            i++;

            costA = new String[listArray1.size()];
            price_categoryA = new String[listArray2.size()];
            price_typeA = new String[listArray3.size()];

            listArray1.toArray(costA);
            listArray2.toArray(price_categoryA);
            listArray3.toArray(price_typeA);

            costs_found = i;

        }
        
        result = true;

    } catch (Exception exc) {

        Utilities.logError("parmDiningCosts: FATAL ERROR: " + exc.toString() + ", MSG: " + exc.getMessage() + "");
        
        err_string = exc.toString();
        err_message = exc.getMessage();
        
    }    

    return result;
     
 } // end of parseCosts


 public static String extractPriceCategory(String charges) {

    int pos1 = 0, pos2 = 0;
    
    pos1 = charges.indexOf("price_category:") + 15;
    pos2 = charges.indexOf("price_type_id:");

    return charges.substring(pos1, pos2).trim();

/*
---
- !ruby/object:Charge
  covers: 1
  price: !ruby/object:ASAP::Money
    amount: 85.0
    currency: !ruby/object:ASAP::Currency
      code: US$
      name: US Dollar
  price_category: Members
  price_type_id: "1"
  remote_item_id:
  remote_menu_id:
  remote_room_id:
*/

 }

//
//
//

/*
 * --- - !ruby/object:Cost price: !ruby/object:ASAP::Money amount: 50.0 currency: !ruby/object:ASAP::Currency code: US$ name: US Dollar price_category: Dinner price_type_id: "" remote_item_id: "" remote_menu_id: "" remote_room_id: ""
 */

/*
 * ---
    - !ruby/object:Cost
      price: !ruby/object:ASAP::Money
        amount: 50.0
        currency: !ruby/object:ASAP::Currency
          code: US$
          name: US Dollar
      price_category: Dinner
      price_type_id: "1"
      remote_item_id: ""
      remote_menu_id: ""
      remote_room_id: ""
    - !ruby/object:Cost
      price: !ruby/object:ASAP::Money
        amount: 80.0
        currency: !ruby/object:ASAP::Currency
          code: US$
          name: US Dollar
      price_category: Dinner & Wine
      price_type_id: "1"
      remote_item_id: ""
      remote_menu_id: ""
      remote_room_id: ""
    - !ruby/object:Cost
      price: !ruby/object:ASAP::Money
        amount: 25.0
        currency: !ruby/object:ASAP::Currency
          code: US$
          name: US Dollar
      price_category: Wine Tasting
      price_type_id: "1"
      remote_item_id: ""
      remote_menu_id: ""
      remote_room_id: ""
 */

} // end of class
