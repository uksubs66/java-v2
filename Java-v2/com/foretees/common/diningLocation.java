/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

/**
 *
 * @author Owner
 */
public class diningLocation {
    
    public int id;
    public int covers; // ??
    public int max_party_size;
    public int max_online_size; // ??
    public String name;
    
    public diningLocation(int location_id, String location_name, int location_covers, int location_party_size, int location_online_size){
        
        id = location_id;
        covers = location_covers;
        max_party_size = location_party_size;
        max_online_size = location_online_size;
        name = location_name;
        
    }
    
}
