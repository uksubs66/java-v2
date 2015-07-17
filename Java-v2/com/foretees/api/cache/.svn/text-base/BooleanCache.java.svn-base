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
public class BooleanCache extends CacheBase {
    
    public Boolean val;

    public BooleanCache(Boolean val) {
        this.val = val;
        this.setExpired();
    }

    public BooleanCache(Boolean val, int exp_minutes) {
        this.val = val;
        this.setExpirationMinutes(exp_minutes);
    }

    public BooleanCache(Boolean val, long exp_miliseconds) {
        this.val = val;
        this.setExpirationMillis(exp_miliseconds);
    }

    public static BooleanCache get(Long key, Map<Long, BooleanCache> map) {
        BooleanCache cache = map.get(key);
        if (cache == null) {
            cache = new BooleanCache(null);
            map.put(key, cache);
        }
        return cache;
    }

    public static BooleanCache get(String key, Map<String, BooleanCache> map) {
        BooleanCache cache = map.get(key);
        if (cache == null) {
            cache = new BooleanCache(null);
            map.put(key, cache);
        }
        return cache;
    }

}
