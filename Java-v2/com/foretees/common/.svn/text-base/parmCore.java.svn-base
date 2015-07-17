/***************************************************************************************
 *   parmCore:  Methods to abstract parameter lists (Like player1, player2, player3, etc.)
 *              into array structures.  
 * 
 *              Allows use of [parm].getPlayer(index), .setPlayer(index), getPlayerArray(size), etc.
 * 
 *              Once anything that uses a parm has been converted over to access arrays by index, 
 *              The parm can then be converted to use lists, arrays, etc., and the use of reflection to dynamically
 *              build the arrays from the legacy playerX variable names can be removed.
 * 
 *              This is a transitional tool.  Because it uses reflection, it will impose a small performance hit
 *              until the transition has been completed.  Caching is used to dramatically reduce this performance hit.
 *
 *
 *   used by:  parmSlotm, parmSlot, parmNSlot, parmEmail, parmCoreAlt
 *
 *   created:    12/14/2011   John K.
 *
 *   last updated:
 *        08/08/2014  Simplified field lookups, increased performance, added get[FieldName]/set[FieldName] abstraction methods.
 *        12/14/2011  Added parameter abstraction methods
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.*;
import org.apache.commons.lang.*;

public class parmCore {
 
    private Map<String, Map<Integer, Field>> fieldCacheMap = new HashMap<String, Map<Integer, Field>>();

    /****************************************
     *  
     * Methods for abstracting parameters 
     * 
     ****************************************
     */
    
    private Field getFieldByName(int index, String name) throws Exception {
        // Cache field lookups to reduce performance hit of reflection
        Map<Integer, Field> fieldMap = fieldCacheMap.get(name);
        if(fieldMap == null){
            fieldMap = new HashMap<Integer, Field>();
            fieldCacheMap.put(name, fieldMap);
        }
        
        Field field = fieldMap.get(index);
        if(field == null){
            String search = ArrayUtil.getFieldName(name, index);
            field = this.getClass().getField(search);
            fieldMap.put(index, field);
        }
        
        return field;
        
    }
    
    protected String getStringByName(String name, int index) {

        String result = null;
        try {
            result = (String) getFieldByName(index, name).get(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    protected int getIntByName(String name, int index) {

        int result = 0;
        try {
            result = getFieldByName(index, name).getInt(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    protected float getFloatByName(String name, int index) {

        float result = 0;
        try {
            result = getFieldByName(index, name).getFloat(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    protected boolean getBooleanByName(String name, int index) {

        boolean result = false;
        try {
            result = getFieldByName(index, name).getBoolean(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    protected short getShortByName(String name, int index) {

        short result = 0;
        try {
            result = getFieldByName(index, name).getShort(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    protected String[] getStringArrayByName(String name, int length) {
        return getStringArrayByName(name, 0, length);
    }

    protected String[] getStringArrayByName(String name, int start, int length) {
        
        String[] result = new String[length];
        for (int i = 0; i < length; i++) {
            try {
                result[i] = (String) getFieldByName(i+start, name).get(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;

    }
    
    
    protected int[] getIntArrayByName(String name, int length) {
        return getIntArrayByName(name, 0, length);
    }
    
    protected int[] getIntArrayByName(String name, int start, int length) {

        int[] result = new int[length];
        for (int i = 0; i < length; i++) {
            try {
                result[i] = getFieldByName(i+start, name).getInt(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;

    }
    
    protected float[] getFloatArrayByName(String name, int length) {
        return getFloatArrayByName(name, 0, length);
    }

    protected float[] getFloatArrayByName(String name, int start, int length) {

        float[] result = new float[length];
        for (int i = 0; i < length; i++) {
            try {
                result[i] = getFieldByName(i+start, name).getFloat(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;

    }
    
    protected short[] getShortArrayByName(String name, int length) {
        return getShortArrayByName(name, 0, length);
    }
    
    protected short[] getShortArrayByName(String name, int start, int length) {

        short[] result = new short[length];
        for (int i = 0; i < length; i++) {
            try {
                result[i] = getFieldByName(i+start, name).getShort(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;

    }
    
    protected boolean[] getBooleanArrayByName(String name, int length) {
        return getBooleanArrayByName(name, 0, length);
    }

    protected boolean[] getBooleanArrayByName(String name, int start, int length) {

        boolean[] result = new boolean[length];
        for (int i = start; i < length; i++) {
            try {
                result[i] = getFieldByName(i+start, name).getBoolean(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return result;

    }
    
    protected void fillParameterByName(String name, Object object, int length) {
        fillParameterByName(name, object, 0, length);
    }
    
    protected void fillParameterByName(String name, Object object, int start, int length) {
        for(int i = start; i < length; i++){
            setParameterByName(name, object, i);
        }
    }
    

    protected void setParameterByName(String name, Object object) {
        setParameterByName(name, object, 0);
    }

    protected void setParameterByName(String name, Object object, int index) {

        if (object.getClass().isArray()) {
            String ctype = object.getClass().getComponentType().toString();
            if (ctype.equals("class java.lang.String")) {
                for (int i = 0; (i < ((String[]) object).length) && (((String[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((String[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Integer")) {
                for (int i = 0; (i < ((Integer[]) object).length) && (((Integer[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((Integer[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Float")) {
                for (int i = 0; (i < ((Float[]) object).length) && (((Float[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((Float[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Boolean")) {
                for (int i = 0; (i < ((Boolean[]) object).length) && (((Boolean[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((Boolean[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Short")) {
                for (int i = 0; (i < ((Short[]) object).length) && (((Short[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((Short[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("int")) {
                for (int i = 0; (i < ((int[]) object).length) && (((int[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((int[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("boolean")) {
                for (int i = 0; (i < ((boolean[]) object).length) && (((boolean[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((boolean[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("float")) {
                for (int i = 0; (i < ((float[]) object).length) && (((float[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((float[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("short")) {
                for (int i = 0; (i < ((short[]) object).length) && (((short[]) object).length > 0); i++) {
                    try {
                        getFieldByName(i, name).set(this, ((short[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new RuntimeException("Unsupported array type:" + ctype);
            }

        } else {
            // not an array source
            try {
                getFieldByName(index, name).set(this, object);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        }

    }
   
    /*
    public String getCtype(Object object) {
    
    Class test = object.getClass().getComponentType();
    return test.toString() + "::" + object.getClass().isArray();
    
    
    }
     *
     */
    
    /*
     * Abstractions for common legacy non-array parameters
     * 
     */
    public String getPlayer(int index){
        return getStringByName("player%", index);
    }
    public String getMtype(int index){
        return getStringByName("mtype%", index);
    }
    public String getMstype(int index){
        return getStringByName("mstype%", index);
    }
    public String getMship(int index){
        return getStringByName("mship%", index);
    }
    public String getMnum(int index){
        return getStringByName("mNum%", index);
    }
    public String getPnum(int index){
        return getStringByName("pnum%", index);
    }
    public String getUser(int index){
        return getStringByName("user%", index);
    }
    public String getCw(int index){
        return getStringByName("pcw%", index);
    }
    public String getUserg(int index){
        return getStringByName("userg%", index);
    }
    public String getTflag(int index){
        return getStringByName("tflag%", index);
    }
    public String getG(int index){
        return getStringByName("g%", index);
    }
    public String getCustomDisp(int index){
        return getStringByName("custom_disp%", index);
    }
    public String getOrig(int index){
        return getStringByName("orig%", index);
    }
    
    public int getP9(int index){
        return getIntByName("p9%", index);
    }
    public int getInval(int index){
        return getIntByName("inval%", index);
    }
    public int getGuestId(int index){
        return getIntByName("guest_id%", index);
    }
    
    public float getHndcp(int index){
        return getFloatByName("hndcp%", index);
    }
    
    public short getShow(int index){
        return getShortByName("show%", index);
    }
    
    
    public String[] getPlayerArray(int length){
        return getStringArrayByName("player%", length);
    }
    public String[] getMtypeArray(int length){
        return getStringArrayByName("mtype%", length);
    }
    public String[] getMstypeArray(int length){
        return getStringArrayByName("mstype%", length);
    }
    public String[] getMshipArray(int length){
        return getStringArrayByName("mship%", length);
    }
    public String[] getMnumArray(int length){
        return getStringArrayByName("mNum%", length);
    }
    public String[] getPnumArray(int length){
        return getStringArrayByName("pnum%", length);
    }
    public String[] getUserArray(int length){
        return getStringArrayByName("user%", length);
    }
    public String[] getCwArray(int length){
        return getStringArrayByName("pcw%", length);
    }
    public String[] getUsergArray(int length){
        return getStringArrayByName("userg%", length);
    }
    public String[] getTflagArray(int length){
        return getStringArrayByName("tflag%", length);
    }
    public String[] getGArray(int length){
        return getStringArrayByName("g%", length);
    }
    public String[] getCustomDispArray(int length){
        return getStringArrayByName("custom_disp%", length);
    }
    public String[] getOrigArray(int length){
        return getStringArrayByName("orig%", length);
    }
    
    public int[] getP9Array(int length){
        return getIntArrayByName("p9%", length);
    }
    public int[] getInvalArray(int length){
        return getIntArrayByName("inval%", length);
    }
    public int[] getGuestIdArray(int length){
        return getIntArrayByName("guest_id%", length);
    }
    
    public float[] getHndcpArray(int length){
        return getFloatArrayByName("hndcp%", length);
    }
    
    public short[] getShowArray(int length){
        return getShortArrayByName("show%", length);
    }

    
    public void setPlayer(String value, int index){
        setParameterByName("player%",value,  index);
    }
    public void setMtype(String value, int index){
        setParameterByName("mtype%",value,  index);
    }
    public void setMstype(String value, int index){
        setParameterByName("mstype%",value,  index);
    }
    public void setMship(String value, int index){
        setParameterByName("mship%",value,  index);
    }
    public void setMnum(String value, int index){
        setParameterByName("mNum%",value,  index);
    }
    public void setPnum(String value, int index){
        setParameterByName("pnum%",value,  index);
    }
    public void setUser(String value, int index){
        setParameterByName("user%",value,  index);
    }
    public void setCw(String value, int index){
        setParameterByName("pcw%",value,  index);
    }
    public void setUserg(String value, int index){
        setParameterByName("userg%", value,  index);
    }
    public void setTflag(String value, int index){
        setParameterByName("tflag%", value, index);
    }
    public void setG(String value, int index){
        setParameterByName("g%", value, index);
    }
    public void setCustomDisp(String value, int index){
        setParameterByName("custom_disp%", value, index);
    }
    public void setOrig(String value, int index){
        setParameterByName("orig%", value, index);
    }
    
    public void setP9(int value, int index){
        setParameterByName("p9%", value, index);
    }
    public void setInval(int value, int index){
        setParameterByName("inval%", value, index);
    }
    public void setGuestId(int value, int index){
        setParameterByName("guest_id%", value, index);
    }
    
    public void setHndcp(float value, int index){
        setParameterByName("hndcp%", value, index);
    }
    
    public void setShow(short value, int index){
        setParameterByName("show%", value, index);
    }
    
    
    public void setPlayer(String[] array){
        setParameterByName("player%",array);
    }
    public void setMtype(String[] array){
        setParameterByName("mtype%",array);
    }
    public void setMstype(String[] array){
        setParameterByName("mstype%",array);
    }
    public void setMship(String[] array){
        setParameterByName("mship%",array);
    }
    public void setMnum(String[] array){
        setParameterByName("mNum%",array);
    }
    public void setPnum(String[] array){
        setParameterByName("pnum%",array);
    }
    public void setUser(String[] array){
        setParameterByName("user%",array);
    }
    public void setCw(String[] array){
        setParameterByName("pcw%",array);
    }
    public void setUserg(String[] array){
        setParameterByName("userg%", array);
    }
    public void setTflag(String[] array){
        setParameterByName("tflag%", array);
    }
    public void setG(String[] array){
        setParameterByName("g%", array);
    }
    public void setCustomDisp(String[] array){
        setParameterByName("custom_disp%", array);
    }
    public void setOrig(String[] array){
        setParameterByName("orig%", array);
    }
    
    public void setP9(int[] array){
        setParameterByName("p9%", array);
    }
    public void setInval(int[] array){
        setParameterByName("inval%", array);
    }
    public void setGuestId(int[] array){
        setParameterByName("guest_id%", array);
    }
    
    public void setHndcp(float[] array){
        setParameterByName("hndcp%", array);
    }
    
    public void setShow(short[] array){
        setParameterByName("show%", array);
    }
    
    public void setP9(Integer[] array){
        setParameterByName("p9%", array);
    }
    public void setInval(Integer[] array){
        setParameterByName("inval%", array);
    }
    public void setGuestId(Integer[] array){
        setParameterByName("guest_id%", array);
    }
    
    public void setHndcp(Float[] array){
        setParameterByName("hndcp%", array);
    }
    
    public void setShow(Short[] array){
        setParameterByName("show%", array);
    }
    
    
    public void fillPlayer(String value, int length) {
        fillParameterByName("player%", value, length);
    }
    
    public void fillUser(String value, int length) {
        fillParameterByName("user%", value, length);
    }
    
    public void fillCw(String value, int length) {
        fillParameterByName("pcw%", value, length);
    }
    
    public void fillUserg(String value, int length) {
        fillParameterByName("userg%", value, length);
    }
    
    public void fillMship(String value, int length) {
        fillParameterByName("mship%", value, length);
    }
    
    public void fillMtype(String value, int length) {
        fillParameterByName("mtype%", value, length);
    }
    
    public void fillMstype(String value, int length) {
        fillParameterByName("mstype%", value, length);
    }
    
    public void fillCustomDisp(String value, int length) {
        fillParameterByName("custom_disp%", value, length);
    }
    
    public void fillMnum(String value, int length) {
        fillParameterByName("mNum%", value, length);
    }
    
    public void fillOrig(String value, int length) {
        fillParameterByName("orig%", value, length);
    }
    
    public void fillTflag(String value, int length) {
        fillParameterByName("tflag%", value, length);
    }
    
    public void fillGuestId(int value, int length) {
        fillParameterByName("guest_id%", value, length);
    }
    
    public void fillP9(int value, int length) {
        fillParameterByName("p9%", value, length);
    }
    
    public void fillInval(int value, int length) {
        fillParameterByName("inval%", value, length);
    }
    
    public void fillShow(short value, int length) {
        fillParameterByName("show%", value, length);
    }
    
    public void fillHndcp(long value, int length) {
        fillParameterByName("hndcp%", value, length);
    }
    
    
}  // end of class
