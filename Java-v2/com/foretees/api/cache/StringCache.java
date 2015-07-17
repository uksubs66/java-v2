/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.api.cache;

import java.util.Map;

/**
 *
 * @author Owner
 */
public class StringCache extends CacheBase {
    
     public String val;
     
     public StringCache(String val){
         this.val = val;
         this.setExpired();
     }
     
     public StringCache(String val, int exp_minutes){
         this.val = val;
         this.setExpirationMinutes(exp_minutes);
     }
     
     public StringCache(String val, long exp_miliseconds){
         this.val = val;
         this.setExpirationMillis(exp_miliseconds);
     }
     
     public static StringCache get(Long key, Map<Long, StringCache> map){
        StringCache cache = map.get(key);
        if(cache == null){
            cache = new StringCache(null);
            map.put(key, cache);
        }
        return cache;
    }
    
    public static StringCache get(String key, Map<String, StringCache> map){
        StringCache cache = map.get(key);
        if(cache == null){
            cache = new StringCache(null);
            map.put(key, cache);
        }
        return cache;
    }
    
}
