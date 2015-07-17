/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

import java.util.*;
import java.util.regex.*;

/**
 *
 * @author Owner
 */
public class Question {
    
    public String text = null;
    public String select_text = null;
    public boolean requires_answer = false;
    public boolean guest_only = false;
    public boolean for_whole_party = false;
    public String[] select_list;
    //public String[] select_list_values;
    
    public Question (String text, boolean requires_answer, boolean guest_only, boolean for_whole_party){
        this.text = text;
        this.requires_answer = requires_answer;
        this.guest_only = guest_only;
        this.for_whole_party = for_whole_party;
        // See if this question contains an embedded select list
        // Like "My Yes or No Question? [Yes,No]"
        Pattern p = Pattern.compile("([^\\[]+)\\s*\\[([^\\]]+)\\]\\s*");
        Matcher m = p.matcher(text);
        if(m.matches()){
            this.select_text = m.group(1).trim();
            this.select_list = m.group(2).trim().split(",");
            for(int i = 0; i < this.select_list.length; i++){
                this.select_list[i] = this.select_list[i].trim();
            }
            //this.select_list_values = this.select_list;
        }
    }
    
    
    public Question (String text, boolean requires_answer, boolean guest_only, boolean for_whole_party, List<String> select_list, boolean add_empty){
        this.text = text;
        this.requires_answer = requires_answer;
        this.guest_only = guest_only;
        this.for_whole_party = for_whole_party;
        if(add_empty){
            //select_list.add(0,""); // can't do this in all cases
            List<String> new_list = new ArrayList<String>();
            new_list.add("");
            new_list.addAll(select_list);
            select_list = new_list;
        }
        this.select_text = this.text;
        this.select_list = select_list.toArray(new String[select_list.size()]);
    }
    /*
    public Question (String text, boolean requires_answer, boolean guest_only, boolean for_whole_party, String[] select_list, String[] select_list_values){
        this.text = text;
        this.requires_answer = requires_answer;
        this.guest_only = guest_only;
        this.for_whole_party = for_whole_party;
        this.select_text = this.text;
        this.select_list = select_list;
        this.select_list_values = select_list_values;
    }
     * 
     */
    
}
