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
public class IntegerCache extends CacheBase {
    
     public Integer val;
     
     public IntegerCache(Integer val){
         this.val = val;
         this.setExpired();
     }
     
     public IntegerCache(Integer val, int exp_minutes){
         this.val = val;
         this.setExpirationMinutes(exp_minutes);
     }
     
     public IntegerCache(Integer val, long exp_miliseconds){
         this.val = val;
         this.setExpirationMillis(exp_miliseconds);
     }
     
     public static IntegerCache get(Long key, Map<Long, IntegerCache> map) {
        IntegerCache cache = map.get(key);
        if (cache == null) {
            cache = new IntegerCache(null);
            map.put(key, cache);
        }
        return cache;
    }

    public static IntegerCache get(String key, Map<String, IntegerCache> map) {
        IntegerCache cache = map.get(key);
        if (cache == null) {
            cache = new IntegerCache(null);
            map.put(key, cache);
        }
        return cache;
    }
    
}
