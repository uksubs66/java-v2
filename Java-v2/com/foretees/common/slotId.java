/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.util.*;

import com.google.gson.*; // for json
import com.google.gson.reflect.*;

import java.lang.reflect.Type;

/**
 *
 * @author Owner
 */
public class slotId {
    
    public Integer reservation_id;
    public Integer event_id;
    public Integer location_id;
    public Integer date;
    public Integer time;
    public Integer maximum_players;
    public String iv;
    
    private String encKey = "a.hddadi osdjaslkja;soijdaslkjdas";

    public slotId(){
        iv = AESencrypt.encodedIV();
    }
    
    public slotId(String hashedJson, String publicKey){
        
        Gson gson = new Gson();

        String json = null;

        byte[] this_iv = AESencrypt.decodeIV(publicKey);

        try {

            json = AESencrypt.decryptBase64(hashedJson, getKey(), this_iv);

        } catch (Exception exc) {
            json = null;
            
            Utilities.logError("SlotId Err=" + exc.toString());
        }

        if(json != null){
            // Define the structure of our response (A list of String key/String value Maps.)
            Type dataType = new TypeToken<slotId>(){}.getType();
            slotId result = null;
            try {
                // Try parsing json response
                result = gson.fromJson(json, dataType);

            } catch (JsonParseException e) {
                // Inavlid json
                result = null;
                Utilities.logError("slotId: Err=" + e.toString() + " Json=" + json);
            }
            if(result != null){
                this.reservation_id = result.reservation_id;
                this.event_id = result.event_id;
                this.location_id = result.location_id;
                this.date = result.date;
                this.time = result.time;
                this.maximum_players = result.maximum_players;
            }
        }
    }
    
    private String getKey(){
        Long keyTime = (System.currentTimeMillis() / 1000L) / 3600;
        return encKey + keyTime; // Only allow key to work for an hour
    }
    
    public final String getHashedJson(){
        
        String hashedJson = null;
        Gson gson = new Gson();
        String json = gson.toJson(this);
        byte[] this_iv = AESencrypt.decodeIV(iv);
        
        try {

            hashedJson = AESencrypt.encryptBase64(json, getKey(), this_iv);

        } catch (Exception exc) {
            hashedJson = null;
            
            Utilities.logError("SlotId Err=" + exc.toString());
        }
        
        return hashedJson;
        
    }
    
}
