/***************************************************************************************
 *   parmCore:  Methods to abstract parameter lists (Like player1, player2, player3, etc.)
 *              into array structures.
 *
 *
 *   called by:  parmSlotm, parmSlot
 *
 *   created:    12/14/2011   John K.
 *
 *   last updated:
 *        12/14/2011  Added parameter abstraction methods
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.lang.*;

public class parmCore {


    /****************************************
     *  
     * Methods for abstracting parameters 
     * 
     ****************************************
     */
    public String getStringByName(String name, int index) {

        String result = "";
        String search = "";
        search = name.replace("%", "" + (index + 1));
        try {
            Class myClass = this.getClass();
            Field myField = myClass.getField(search);
            result = myField.get(this).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    public int getIntByName(String name, int index) {

        int result = 0;
        String search = "";
        search = name.replace("%", "" + (index + 1));
        try {
            Class myClass = this.getClass();
            Field myField = myClass.getField(search);
            result = myField.getInt(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    public float getFloatByName(String name, int index) {

        float result = 0;
        String search = "";
        search = name.replace("%", "" + (index + 1));
        try {
            Class myClass = this.getClass();
            Field myField = myClass.getField(search);
            result = myField.getFloat(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    public boolean getBooleanByName(String name, int index) {

        boolean result = false;
        String search = "";
        search = name.replace("%", "" + (index + 1));
        try {
            Class myClass = this.getClass();
            Field myField = myClass.getField(search);
            result = myField.getBoolean(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }
    
    public short getShortByName(String name, int index) {

        short result = 0;
        String search = "";
        search = name.replace("%", "" + (index + 1));
        try {
            Class myClass = this.getClass();
            Field myField = myClass.getField(search);
            result = myField.getShort(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;

    }

    public String[] getStringArrayByName(String name) {

        ArrayList<String> result = new ArrayList<String>();
        String search = "";
        boolean loop = true;
        int i = 0;
        while (loop) {
            search = name.replace("%", "" + (i + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                result.add(myField.get(this).toString());
                i++;
            } catch (Exception e) {
                loop = false;
                //throw new RuntimeException(e);
            }

        }
        if (i == 0) {
            throw new RuntimeException("No string parmeter list found: \"" + search + "\"");
        } else {
            return result.toArray(new String[0]);
        }

    }

    public int[] getIntArrayByName(String name) {

        ArrayList<Integer> result = new ArrayList<Integer>();
        String search = "";
        boolean loop = true;
        int i = 0;
        while (loop) {
            search = name.replace("%", "" + (i + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                result.add(myField.getInt(this));
                i++;
            } catch (Exception e) {
                loop = false;
                //throw new RuntimeException(e);
            }

        }
        if (i == 0) {
            throw new RuntimeException("No integer parmeter list found: \"" + search + "\"");
        } else {
            int[] res = new int[result.size()];
            for (int i2 = 0; (i2 < res.length) && (res.length > 0); i2++) {
                res[i2] = result.get(i2);
            }
            return res;
        }

    }

    public float[] getFloatArrayByName(String name) {

        ArrayList<Float> result = new ArrayList<Float>();
        String search = "";
        boolean loop = true;
        int i = 0;
        while (loop) {
            search = name.replace("%", "" + (i + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                result.add(myField.getFloat(this));
                i++;
            } catch (Exception e) {
                loop = false;
                //throw new RuntimeException(e);
            }

        }
        if (i == 0) {
            throw new RuntimeException("No float parmeter list found: \"" + search + "\"");
        } else {
            float[] res = new float[result.size()];
            for (int i2 = 0; (i2 < res.length) && (res.length > 0); i2++) {
                res[i2] = result.get(i2);
            }
            return res;
        }

    }

    public short[] getShortArrayByName(String name) {

        ArrayList<Short> result = new ArrayList<Short>();
        String search = "";
        boolean loop = true;
        int i = 0;
        while (loop) {
            search = name.replace("%", "" + (i + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                result.add(myField.getShort(this));
                i++;
            } catch (Exception e) {
                loop = false;
                //throw new RuntimeException(e);
            }

        }
        if (i == 0) {
            throw new RuntimeException("No short parmeter list found: \"" + search + "\"");
        } else {
            short[] res = new short[result.size()];
            for (int i2 = 0; (i2 < res.length) && (res.length > 0); i2++) {
                res[i2] = result.get(i2);
            }
            return res;
        }

    }

    public boolean[] getBooleanArrayByName(String name) {

        ArrayList<Boolean> result = new ArrayList<Boolean>();
        String search = "";
        boolean loop = true;
        int i = 0;
        while (loop) {
            search = name.replace("%", "" + (i + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                result.add(myField.getBoolean(this));
                i++;
            } catch (Exception e) {
                loop = false;
                //throw new RuntimeException(e);
            }

        }
        if (i == 0) {
            throw new RuntimeException("No boolean parmeter list found: \"" + search + "\"");
        } else {
            boolean[] res = new boolean[result.size()];
            for (int i2 = 0; (i2 < res.length) && (res.length > 0); i2++) {
                res[i2] = result.get(i2);
            }
            return res;
        }

    }

    public void setParameterByName(String name, Object object) {
        setParameterByName(name, object, 0);
    }

    public void setParameterByName(String name, Object object, int index) {

        String search = "";
        if (object.getClass().isArray()) {
            String ctype = object.getClass().getComponentType().toString();
            if (ctype.equals("class java.lang.String")) {
                for (int i = 0; (i < ((String[]) object).length) && (((String[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((String[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Integer")) {
                for (int i = 0; (i < ((Integer[]) object).length) && (((Integer[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((Integer[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Float")) {
                for (int i = 0; (i < ((Float[]) object).length) && (((Float[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((Float[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Boolean")) {
                for (int i = 0; (i < ((Boolean[]) object).length) && (((Boolean[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((Boolean[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("class java.lang.Short")) {
                for (int i = 0; (i < ((Short[]) object).length) && (((Short[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((Short[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("int")) {
                for (int i = 0; (i < ((int[]) object).length) && (((int[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((int[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("boolean")) {
                for (int i = 0; (i < ((boolean[]) object).length) && (((boolean[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((boolean[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("float")) {
                for (int i = 0; (i < ((float[]) object).length) && (((float[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((float[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (ctype.equals("short")) {
                for (int i = 0; (i < ((short[]) object).length) && (((short[]) object).length > 0); i++) {
                    search = name.replace("%", "" + (i + 1));
                    try {
                        Class myClass = this.getClass();
                        Field myField = myClass.getField(search);
                        myField.set(this, ((short[]) object)[i]);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                throw new RuntimeException("Unsupported array type:" + ctype);
            }

        } else {
            // not an array source

            search = name.replace("%", "" + (index + 1));
            try {
                Class myClass = this.getClass();
                Field myField = myClass.getField(search);
                myField.set(this, object);
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
}  // end of class
