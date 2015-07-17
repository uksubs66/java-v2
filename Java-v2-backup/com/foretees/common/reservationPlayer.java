/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.foretees.common;

/**
 *
 * @author Owner
 */
public class reservationPlayer {
    
    public String user;
    public String name;
    public String alpha_name; // Used for display in alpha sort
    public String cw;
    public String status;
    public String sort; // Used for alpha sorting players
    public String orig; // Username that originated this position
    public Integer p9;
    public Integer guest_id;
    public Integer group_index; // Used to point to a particular time in a group
    public Integer slot_index; // Used to point to a particular position in a time
    
    public reservationPlayer(String user, String name, String alphaName, String status) {
        initPlayer(user, name, alphaName, status, null, null, null, null, null, null);
    }
    
    public reservationPlayer(String user, String name, String alphaName, String status, String cw) {
        initPlayer(user, name, alphaName, status, cw, null, null, null, null, null);
    }
    
    public reservationPlayer(String user, String name, String alphaName, String status, String cw, Integer guest_id) {
        initPlayer(user, name, alphaName, status, cw, guest_id, null, null, null, null);
    }
    
    public reservationPlayer(String user, String name, String alphaName, String status, String cw, Integer guest_id, Integer group_index, Integer slot_index) {
        initPlayer(user, name, alphaName, status, cw, guest_id, group_index, slot_index, null, null);
    }
    
    public reservationPlayer(String user, String name, String alphaName, String status, String cw, Integer guest_id, Integer group_index, Integer slot_index, String orig) {
        initPlayer(user, name, alphaName, status, cw, guest_id, group_index, slot_index, orig, null);
    }
    
    public reservationPlayer(String user, String name, String alphaName, String status, String cw, Integer guest_id, Integer group_index, Integer slot_index, String orig, Integer p9) {
        initPlayer(user, name, alphaName, status, cw, guest_id, group_index, slot_index, orig, p9);
    }



    public final void initPlayer(String user, String name, String alphaName, String status, String cw, Integer guest_id, Integer group_index, Integer slot_index, String orig, Integer p9) {

        this.user = user;
        this.name = name;
        if(alphaName != null){
        this.alpha_name = alphaName;
            if (user != null && !user.isEmpty()) {
                // User
                this.sort = alphaName;
            } else {
                // Guest
                this.sort = "ZZZZ_" + alphaName;
            }
        }
        if (status != null && !status.isEmpty()) {
            this.status = status;
        }
        if (cw != null && !cw.isEmpty()) {
            this.cw = cw;
        }
        if (guest_id != null) {
            this.guest_id = guest_id;
        }
        if (group_index != null) {
            this.group_index = group_index;
        }
        if (slot_index != null) {
            this.slot_index = slot_index;
        }
        if (p9 != null) {
            this.p9 = p9;
        }

    }
    
    
}
