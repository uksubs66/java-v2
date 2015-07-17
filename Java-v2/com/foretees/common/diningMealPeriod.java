/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.sql.*;

/**
 *
 * @author Owner
 */
public class diningMealPeriod {
    
    public int id = 0;
    public String name;
    public int start_time = 0;
    public int end_time = 0;
    
    public diningMealPeriod(int mp_id, String mp_name, int mp_start_time, int mp_end_time){
        id = mp_id;
        name = mp_name;
        start_time = mp_start_time;
        end_time = mp_end_time;
    }
    
}
