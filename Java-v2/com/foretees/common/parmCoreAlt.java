/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

/**
 *
 * @author Owner
 */
public class parmCoreAlt extends parmCore {
    
       /*
     * Abstractions for legacy non-array parameters specific to parmSlotm, parmNSlot, etc.
     * 
     */
    
    public String getOldPlayer(int index){
        return getStringByName("oldPlayer%", index);
    }
    public String getOldUser(int index){
        return getStringByName("oldUser%", index);
    }
    public String getOldCw(int index){
        return getStringByName("oldp%cw", index);
    }
    public String getOldOrig(int index){
        return getStringByName("oldOrig%", index);
    }
    public int getOldGuestId(int index){
        return getIntByName("oldguest_id%", index);
    }
    public int getOldP9(int index){
        return getIntByName("oldp9%", index);
    }
    public short getPos(int index){
        return getShortByName("pos%", index);
    }
    public int getNoPost(int index){
        return getIntByName("nopost%", index);
    }
    
    public String[] getOldPlayerArray(int length){
        return getStringArrayByName("oldPlayer%", length);
    }
    public String[] getOldUserArray(int length){
        return getStringArrayByName("oldUser%", length);
    }
    public String[] getOldCwArray(int length){
        return getStringArrayByName("oldp%cw", length);
    }
    public String[] getOldOrigArray(int length){
        return getStringArrayByName("oldOrig%", length);
    }
    public int[] getOldGuestIdArray(int length){
        return getIntArrayByName("oldguest_id%", length);
    }
    public int[] getOldP9Array(int length){
        return getIntArrayByName("oldp9%", length);
    }
    public short[] getPosArray(int length){
        return getShortArrayByName("pos%", length);
    }
    public int[] getNoPostArray(int length){
        return getIntArrayByName("nopost%", length);
    }
    
    public void setOldPlayer(String value, int index){
        setParameterByName("oldPlayer%", value, index);
    }
    public void setOldUser(String value, int index){
        setParameterByName("oldUser%", value, index);
    }
    public void setOldCw(String value, int index){
        setParameterByName("oldp%cw", value, index);
    }
    public void getOldOrig(String value, int index){
        setParameterByName("oldOrig%", value, index);
    }
    
    public void setOldGuestId(int value, int index){
        setParameterByName("oldguest_id%", value, index);
    }
    public void setOldP9(int value, int index){
        setParameterByName("oldp9%", value, index);
    }
    public void setPos(short value, int index){
        setParameterByName("pos%", value, index);
    }
    public void setNoPost(int value, int index){
        setParameterByName("nopost%", value, index);
    }
    
    public void setOldPlayer(String[] array){
        setParameterByName("oldPlayer%", array);
    }
    public void setOldUser(String[] array){
        setParameterByName("oldUser%", array);
    }
    public void setOldCw(String[] array){
        setParameterByName("oldp%cw", array);
    }
    public void getOldOrig(String[] array){
        setParameterByName("oldOrig%", array);
    }
    
    public void setOldGuestId(int[] array){
        setParameterByName("oldguest_id%", array);
    }
    public void setOldGuestId(Integer[] array){
        setParameterByName("oldguest_id%", array);
    }
    public void setOldP9(int[] array){
        setParameterByName("oldp9%", array);
    }
    public void setOldP9(Integer[] array){
        setParameterByName("oldp9%", array);
    }
    public void setPos(short[] array){
        setParameterByName("pos%", array);
    }
    public void setPos(Short[] array){
        setParameterByName("pos%", array);
    }
    public void setNoPost(int[] array){
        setParameterByName("nopost%", array);
    }
    public void setNoPost(Integer[] array){
        setParameterByName("nopost%", array);
    }
    
    
    public void fillPos(short value, int length) {
        fillParameterByName("pos%", value, length);
    }
    
    @Override
    public String getCw(int index){
        return getStringByName("p%cw", index);
    }
    
    @Override
    public String[] getCwArray(int length){
        return getStringArrayByName("p%cw", length);
    }
    
    @Override
    public void setCw(String value, int index){
        setParameterByName("p%cw",value,  index);
    }
    
    @Override
    public void setCw(String[] array){
        setParameterByName("p%cw", array);
    }
    
    @Override
    public void fillCw(String value, int length) {
        fillParameterByName("p%cw", value, length);
    }
    
    
}
