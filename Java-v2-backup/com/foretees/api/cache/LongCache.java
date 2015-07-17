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
public class LongCache extends CacheBase {
    
     public Long val;
     
     public LongCache(Long val){
         this.val = val;
         this.setExpired();
     }
     
     public LongCache(Long val, int exp_minutes){
         this.val = val;
         this.setExpirationMinutes(exp_minutes);
     }
     
     public LongCache(Long val, long exp_miliseconds){
         this.val = val;
         this.setExpirationMillis(exp_miliseconds);
     }
     
     public static LongCache get(Long key, Map<Long, LongCache> map){
        LongCache cache = map.get(key);
        if(cache == null){
            cache = new LongCache(null);
            map.put(key, cache);
        }
        return cache;
    }
    
    public static LongCache get(String key, Map<String, LongCache> map){
        LongCache cache = map.get(key);
        if(cache == null){
            cache = new LongCache(null);
            map.put(key, cache);
        }
        return cache;
    }
    
}
