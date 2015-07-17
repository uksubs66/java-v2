/***************************************************************************************
 *   parmSlotm:  This class will define a paramter block object to be used for lottery processing.
 *
 *
 *   called by:  Member_slotm, Proshop_slotm
 *
 *   created:    1/05/2005   Bob P.
 *
 *   last updated:
 * 
 *        10/11/2013  Added make_private field.
 *        12/25/2012  Added time6-15 and sstime fields.
 *        12/15/2011  Removed player_per_group 
 *        12/14/2011  Added players_per_group
 *        12/14/2011  Added default CW override map
 *        12/14/2011  Added parameter abstraction methods
 *         7/22/2010  Added proShortName (case 1555).
 *         4/09/2010  Added gplayer and hit4 for use with guest tracking errors
 *         2/11/2009  Add guest_id1-25 for holding guest tracking ids
 *         8/28/2009  Add showlott string
 *         2/17/2009  Added skipDining string
 *        10/13/2008  Added a "suppressEmails" field (case 1454).
 *         6/26/2008  Add teecurr_id array so we can more easily identify the tee time.
 *         4/23/2008  Add tflag strings (case 1357).
 *         4/27/2006  Added show1...25 and precheckin
 *         4/13/2007  Add custom_int, custom_string and custom_dispx for custom fields in teecurr.
 *
 *
 *
 ***************************************************************************************
 */
package com.foretees.common;

import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.lang.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class parmSlotm extends parmCore {
    
    public boolean error = false;
    public boolean hit = false;
    public boolean hit2 = false;
    public boolean hit4 = false;     // used with guest tracking errors
    public long date = 0;
    public int in_use = 0;
    public int time = 0;
    public int time1 = 0;            // max of 5 consecutive tee times
    public int time2 = 0;
    public int time3 = 0;
    public int time4 = 0;
    public int time5 = 0;
    public int time6 = 0;
    public int time7 = 0;
    public int time8 = 0;
    public int time9 = 0;
    public int time10 = 0;
    public int time11 = 0;
    public int time12 = 0;
    public int time13 = 0;
    public int time14 = 0;
    public int time15 = 0;
    public int stime = 0;
    public int etime = 0;
    public int fb = 0;
    public int mm = 0;
    public int dd = 0;
    public int yy = 0;
    public int inval1 = 0;
    public int inval2 = 0;
    public int inval3 = 0;
    public int inval4 = 0;
    public int inval5 = 0;
    public int inval6 = 0;
    public int inval7 = 0;
    public int inval8 = 0;
    public int inval9 = 0;
    public int inval10 = 0;
    public int inval11 = 0;
    public int inval12 = 0;
    public int inval13 = 0;
    public int inval14 = 0;
    public int inval15 = 0;
    public int inval16 = 0;
    public int inval17 = 0;
    public int inval18 = 0;
    public int inval19 = 0;
    public int inval20 = 0;
    public int inval21 = 0;
    public int inval22 = 0;
    public int inval23 = 0;
    public int inval24 = 0;
    public int inval25 = 0;
    public int members = 0;
    public int memg1 = 0;
    public int memg2 = 0;
    public int memg3 = 0;
    public int memg4 = 0;
    public int memg5 = 0;
    public int players = 0;
    public int players_per_group = 0;
    public int hide = 0;
    public int lstate = 0;
    public int slots = 0;
    public int ftime = 0;
    public int ltime = 0;
    public int groups = 0;
    public int grps = 0;
    public int i3 = 0;
    public int guests = 0;
    public int guestsg1 = 0;
    public int guestsg2 = 0;
    public int guestsg3 = 0;
    public int guestsg4 = 0;
    public int guestsg5 = 0;
    public int memNew = 0;
    public int memMod = 0;
    public int ind = 0;            // numeric value of index
    public int p91 = 0;
    public int p92 = 0;
    public int p93 = 0;
    public int p94 = 0;
    public int p95 = 0;
    public int p96 = 0;
    public int p97 = 0;
    public int p98 = 0;
    public int p99 = 0;
    public int p910 = 0;
    public int p911 = 0;
    public int p912 = 0;
    public int p913 = 0;
    public int p914 = 0;
    public int p915 = 0;
    public int p916 = 0;
    public int p917 = 0;
    public int p918 = 0;
    public int p919 = 0;
    public int p920 = 0;
    public int p921 = 0;
    public int p922 = 0;
    public int p923 = 0;
    public int p924 = 0;
    public int p925 = 0;
    public int precheckin = 0;
    public int custom_int1 = 0;
    public int custom_int2 = 0;
    public int custom_int3 = 0;
    public int custom_int4 = 0;
    public int custom_int5 = 0;
    public int make_private = 0;
    public short show1 = 0;
    public short show2 = 0;
    public short show3 = 0;
    public short show4 = 0;
    public short show5 = 0;
    public short show6 = 0;
    public short show7 = 0;
    public short show8 = 0;
    public short show9 = 0;
    public short show10 = 0;
    public short show11 = 0;
    public short show12 = 0;
    public short show13 = 0;
    public short show14 = 0;
    public short show15 = 0;
    public short show16 = 0;
    public short show17 = 0;
    public short show18 = 0;
    public short show19 = 0;
    public short show20 = 0;
    public short show21 = 0;
    public short show22 = 0;
    public short show23 = 0;
    public short show24 = 0;
    public short show25 = 0;
    public float hndcp1 = 0;
    public float hndcp2 = 0;
    public float hndcp3 = 0;
    public float hndcp4 = 0;
    public float hndcp5 = 0;
    public float hndcp6 = 0;
    public float hndcp7 = 0;
    public float hndcp8 = 0;
    public float hndcp9 = 0;
    public float hndcp10 = 0;
    public float hndcp11 = 0;
    public float hndcp12 = 0;
    public float hndcp13 = 0;
    public float hndcp14 = 0;
    public float hndcp15 = 0;
    public float hndcp16 = 0;
    public float hndcp17 = 0;
    public float hndcp18 = 0;
    public float hndcp19 = 0;
    public float hndcp20 = 0;
    public float hndcp21 = 0;
    public float hndcp22 = 0;
    public float hndcp23 = 0;
    public float hndcp24 = 0;
    public float hndcp25 = 0;
    public int grest_num = 0;
    public short rfb = 0;
    public String club = "";
    public String course = "";
    public String returnCourse = "";
    public String course2 = "";          // course name for error msg
    public String suppressEmails = "";   // guest restriction per option (Member or Tee Time)
    public String skipDining = "";
    public String day = "";
    public String conf = "";
    public String player = "";
    public String gplayer = "";
    public String mship = "";
    public String mtype = "";
    public String period = "";
    public String rest_name = "";
    public String hides = "";
    public String notes = "";
    public String index = "";
    public String index2 = "";
    public String p5 = "";
    public String p5rest = "";
    public String jump = "";
    public String sfb = "";
    public String sstime = "";
    public String in_use_by = "";
    public String orig_by = "";
    public String position_orig_by = "";
    public String grest_per = "";
    public String showlott = "";
    public String proShortName = "";      // name of proshop user that is making the tee times
    public String player1 = "";
    public String player2 = "";
    public String player3 = "";
    public String player4 = "";
    public String player5 = "";
    public String player6 = "";
    public String player7 = "";
    public String player8 = "";
    public String player9 = "";
    public String player10 = "";
    public String player11 = "";
    public String player12 = "";
    public String player13 = "";
    public String player14 = "";
    public String player15 = "";
    public String player16 = "";
    public String player17 = "";
    public String player18 = "";
    public String player19 = "";
    public String player20 = "";
    public String player21 = "";
    public String player22 = "";
    public String player23 = "";
    public String player24 = "";
    public String player25 = "";
    public String user1 = "";
    public String user2 = "";
    public String user3 = "";
    public String user4 = "";
    public String user5 = "";
    public String user6 = "";
    public String user7 = "";
    public String user8 = "";
    public String user9 = "";
    public String user10 = "";
    public String user11 = "";
    public String user12 = "";
    public String user13 = "";
    public String user14 = "";
    public String user15 = "";
    public String user16 = "";
    public String user17 = "";
    public String user18 = "";
    public String user19 = "";
    public String user20 = "";
    public String user21 = "";
    public String user22 = "";
    public String user23 = "";
    public String user24 = "";
    public String user25 = "";
    
   
    
    public String pcw1 = "";
    public String pcw2 = "";
    public String pcw3 = "";
    public String pcw4 = "";
    public String pcw5 = "";
    public String pcw6 = "";
    public String pcw7 = "";
    public String pcw8 = "";
    public String pcw9 = "";
    public String pcw10 = "";
    public String pcw11 = "";
    public String pcw12 = "";
    public String pcw13 = "";
    public String pcw14 = "";
    public String pcw15 = "";
    public String pcw16 = "";
    public String pcw17 = "";
    public String pcw18 = "";
    public String pcw19 = "";
    public String pcw20 = "";
    public String pcw21 = "";
    public String pcw22 = "";
    public String pcw23 = "";
    public String pcw24 = "";
    public String pcw25 = "";
    public String mNum1 = "";
    public String mNum2 = "";
    public String mNum3 = "";
    public String mNum4 = "";
    public String mNum5 = "";
    public String mNum6 = "";
    public String mNum7 = "";
    public String mNum8 = "";
    public String mNum9 = "";
    public String mNum10 = "";
    public String mNum11 = "";
    public String mNum12 = "";
    public String mNum13 = "";
    public String mNum14 = "";
    public String mNum15 = "";
    public String mNum16 = "";
    public String mNum17 = "";
    public String mNum18 = "";
    public String mNum19 = "";
    public String mNum20 = "";
    public String mNum21 = "";
    public String mNum22 = "";
    public String mNum23 = "";
    public String mNum24 = "";
    public String mNum25 = "";
    public String pNum1 = "";
    public String pNum2 = "";
    public String pNum3 = "";
    public String pNum4 = "";
    public String pNum5 = "";
    public String pNum6 = "";
    public String pNum7 = "";
    public String pNum8 = "";
    public String pNum9 = "";
    public String pNum10 = "";
    public String pNum11 = "";
    public String pNum12 = "";
    public String pNum13 = "";
    public String pNum14 = "";
    public String pNum15 = "";
    public String pNum16 = "";
    public String pNum17 = "";
    public String pNum18 = "";
    public String pNum19 = "";
    public String pNum20 = "";
    public String pNum21 = "";
    public String pNum22 = "";
    public String pNum23 = "";
    public String pNum24 = "";
    public String pNum25 = "";
    public String mship1 = "";
    public String mship2 = "";
    public String mship3 = "";
    public String mship4 = "";
    public String mship5 = "";
    public String mship6 = "";
    public String mship7 = "";
    public String mship8 = "";
    public String mship9 = "";
    public String mship10 = "";
    public String mship11 = "";
    public String mship12 = "";
    public String mship13 = "";
    public String mship14 = "";
    public String mship15 = "";
    public String mship16 = "";
    public String mship17 = "";
    public String mship18 = "";
    public String mship19 = "";
    public String mship20 = "";
    public String mship21 = "";
    public String mship22 = "";
    public String mship23 = "";
    public String mship24 = "";
    public String mship25 = "";
    public String mtype1 = "";
    public String mtype2 = "";
    public String mtype3 = "";
    public String mtype4 = "";
    public String mtype5 = "";
    public String mtype6 = "";
    public String mtype7 = "";
    public String mtype8 = "";
    public String mtype9 = "";
    public String mtype10 = "";
    public String mtype11 = "";
    public String mtype12 = "";
    public String mtype13 = "";
    public String mtype14 = "";
    public String mtype15 = "";
    public String mtype16 = "";
    public String mtype17 = "";
    public String mtype18 = "";
    public String mtype19 = "";
    public String mtype20 = "";
    public String mtype21 = "";
    public String mtype22 = "";
    public String mtype23 = "";
    public String mtype24 = "";
    public String mtype25 = "";
    public String mstype1 = "";
    public String mstype2 = "";
    public String mstype3 = "";
    public String mstype4 = "";
    public String mstype5 = "";
    public String mstype6 = "";
    public String mstype7 = "";
    public String mstype8 = "";
    public String mstype9 = "";
    public String mstype10 = "";
    public String mstype11 = "";
    public String mstype12 = "";
    public String mstype13 = "";
    public String mstype14 = "";
    public String mstype15 = "";
    public String mstype16 = "";
    public String mstype17 = "";
    public String mstype18 = "";
    public String mstype19 = "";
    public String mstype20 = "";
    public String mstype21 = "";
    public String mstype22 = "";
    public String mstype23 = "";
    public String mstype24 = "";
    public String mstype25 = "";
    public String fname1 = "";
    public String lname1 = "";
    public String mi1 = "";
    public String fname2 = "";
    public String lname2 = "";
    public String mi2 = "";
    public String fname3 = "";
    public String lname3 = "";
    public String mi3 = "";
    public String fname4 = "";
    public String lname4 = "";
    public String mi4 = "";
    public String fname5 = "";
    public String lname5 = "";
    public String mi5 = "";
    public String fname6 = "";
    public String lname6 = "";
    public String mi6 = "";
    public String fname7 = "";
    public String lname7 = "";
    public String mi7 = "";
    public String fname8 = "";
    public String lname8 = "";
    public String mi8 = "";
    public String fname9 = "";
    public String lname9 = "";
    public String mi9 = "";
    public String fname10 = "";
    public String lname10 = "";
    public String mi10 = "";
    public String fname11 = "";
    public String lname11 = "";
    public String mi11 = "";
    public String fname12 = "";
    public String lname12 = "";
    public String mi12 = "";
    public String fname13 = "";
    public String lname13 = "";
    public String mi13 = "";
    public String fname14 = "";
    public String lname14 = "";
    public String mi14 = "";
    public String fname15 = "";
    public String lname15 = "";
    public String mi15 = "";
    public String fname16 = "";
    public String lname16 = "";
    public String mi16 = "";
    public String fname17 = "";
    public String lname17 = "";
    public String mi17 = "";
    public String fname18 = "";
    public String lname18 = "";
    public String mi18 = "";
    public String fname19 = "";
    public String lname19 = "";
    public String mi19 = "";
    public String fname20 = "";
    public String lname20 = "";
    public String mi20 = "";
    public String fname21 = "";
    public String lname21 = "";
    public String mi21 = "";
    public String fname22 = "";
    public String lname22 = "";
    public String mi22 = "";
    public String fname23 = "";
    public String lname23 = "";
    public String mi23 = "";
    public String fname24 = "";
    public String lname24 = "";
    public String mi24 = "";
    public String fname25 = "";
    public String lname25 = "";
    public String mi25 = "";
    public String tflag1 = "";
    public String tflag2 = "";
    public String tflag3 = "";
    public String tflag4 = "";
    public String tflag5 = "";
    public String tflag6 = "";
    public String tflag7 = "";
    public String tflag8 = "";
    public String tflag9 = "";
    public String tflag10 = "";
    public String tflag11 = "";
    public String tflag12 = "";
    public String tflag13 = "";
    public String tflag14 = "";
    public String tflag15 = "";
    public String tflag16 = "";
    public String tflag17 = "";
    public String tflag18 = "";
    public String tflag19 = "";
    public String tflag20 = "";
    public String tflag21 = "";
    public String tflag22 = "";
    public String tflag23 = "";
    public String tflag24 = "";
    public String tflag25 = "";
    //
    //  Custom fields for making custom processing easier
    //
    public String custom_string1 = "";
    public String custom_string2 = "";
    public String custom_string3 = "";
    public String custom_string4 = "";
    public String custom_string5 = "";
    public String custom_disp1 = "";
    public String custom_disp2 = "";
    public String custom_disp3 = "";
    public String custom_disp4 = "";
    public String custom_disp5 = "";
    public String custom_disp6 = "";
    public String custom_disp7 = "";
    public String custom_disp8 = "";
    public String custom_disp9 = "";
    public String custom_disp10 = "";
    public String custom_disp11 = "";
    public String custom_disp12 = "";
    public String custom_disp13 = "";
    public String custom_disp14 = "";
    public String custom_disp15 = "";
    public String custom_disp16 = "";
    public String custom_disp17 = "";
    public String custom_disp18 = "";
    public String custom_disp19 = "";
    public String custom_disp20 = "";
    public String custom_disp21 = "";
    public String custom_disp22 = "";
    public String custom_disp23 = "";
    public String custom_disp24 = "";
    public String custom_disp25 = "";
    public int guest_id1 = 0;
    public int guest_id2 = 0;
    public int guest_id3 = 0;
    public int guest_id4 = 0;
    public int guest_id5 = 0;
    public int guest_id6 = 0;
    public int guest_id7 = 0;
    public int guest_id8 = 0;
    public int guest_id9 = 0;
    public int guest_id10 = 0;
    public int guest_id11 = 0;
    public int guest_id12 = 0;
    public int guest_id13 = 0;
    public int guest_id14 = 0;
    public int guest_id15 = 0;
    public int guest_id16 = 0;
    public int guest_id17 = 0;
    public int guest_id18 = 0;
    public int guest_id19 = 0;
    public int guest_id20 = 0;
    public int guest_id21 = 0;
    public int guest_id22 = 0;
    public int guest_id23 = 0;
    public int guest_id24 = 0;
    public int guest_id25 = 0;
    //
    //  guest array for Member_lott
    //
    public String[] gstA = new String[25];     // guests (entire player name)
    public String[] g = new String[25];        // guest type of player position (if guest)
    public String[] userg = new String[25];    // and user guest names
    public String[] memA = new String[25];      // member names assigned to guests
    public int[] atimeA = new int[5];           // tee time array per request (save area)
    public int[] teecurr_idA = new int[5];      // tee time ids - one per tee time
    
    public parmSlot getSingleParmSlot(int group){
        return getSingleParmSlot(group, null);
    }
    
    public parmSlot getSingleParmSlot(int group, HttpServletRequest req){
 
        parmSlot parm1 = new parmSlot();
        
        int groupSize = (p5.equals("Yes")?5:4);
        int groupStart = group * groupSize;
        int aGrStart = groupStart;
        int aGrEnd = groupStart + groupSize;
        //
        //  set relavent parms for selected group
        //
        parm1.time = this.getTime(group);
        parm1.date = this.date;
        parm1.fb = this.fb;
        parm1.p5 = this.p5;
        parm1.course = this.course;
        parm1.notes = this.notes; //(group>0?"":this.notes); // Omit notes for all but first group
        parm1.hide = this.hide; //(group>0?0:this.hide); // Omit hide notes for all but first group
        parm1.make_private = this.make_private;
        parm1.custom_string = this.getStringByName("custom_string%", group);
        parm1.custom_int = this.getIntByName("custom_int%", group);
        parm1.orig_by = this.orig_by;
        parm1.precheckin = this.precheckin;
        
        parm1.setParameterByName("player%", this.getStringArrayByName("player%", groupStart, groupSize));
        parm1.setParameterByName("lname%", this.getStringArrayByName("lname%", groupStart, groupSize));
        parm1.setParameterByName("fname%", this.getStringArrayByName("fname%", groupStart, groupSize));
        parm1.setParameterByName("mi%", this.getStringArrayByName("mi%", groupStart, groupSize));
        parm1.setParameterByName("user%", this.getStringArrayByName("user%", groupStart, groupSize));
        parm1.setParameterByName("userg%", Arrays.copyOfRange(this.userg, aGrStart, aGrEnd));
        parm1.setParameterByName("mem%", Arrays.copyOfRange(this.memA, aGrStart, aGrEnd));
        parm1.setParameterByName("mship%", this.getStringArrayByName("mship%", groupStart, groupSize));
        parm1.setParameterByName("mtype%", this.getStringArrayByName("mtype%", groupStart, groupSize));
        parm1.setParameterByName("mstype%", this.getStringArrayByName("mstype%", groupStart, groupSize));
        parm1.setParameterByName("p%cw", this.getStringArrayByName("pcw%", groupStart, groupSize));
        parm1.setParameterByName("custom_disp%", this.getStringArrayByName("custom_disp%", groupStart, groupSize));
        
        parm1.setParameterByName("mNum%", this.getStringArrayByName("mNum%", groupStart, groupSize));
        parm1.setParameterByName("pnum%", this.getStringArrayByName("pNum%", groupStart, groupSize));
        parm1.setParameterByName("tflag%", this.getStringArrayByName("tflag%", groupStart, groupSize));
        
        parm1.setParameterByName("g%", Arrays.copyOfRange(this.g, aGrStart, aGrEnd));
        
        parm1.setParameterByName("hndcp%", this.getFloatArrayByName("hndcp%", groupStart, groupSize));
        parm1.setParameterByName("show%", this.getShortArrayByName("show%", groupStart, groupSize));
        
        parm1.setParameterByName("p9%", this.getIntArrayByName("p9%", groupStart, groupSize));
        parm1.setParameterByName("guest_id%", this.getIntArrayByName("guest_id%", groupStart, groupSize));

        if(req == null){
            // We're just copying
            //parm1.setParameterByName("orig%", this.getShortArrayByName("orig%", groupStart, groupSize));
            
        } else {
            // We're proccessing a request
            parm1.user = reqUtil.getSessionString(req,"user","");
            
            parm1.orig1 = (!parm1.player1.equals("") ? this.position_orig_by : "");
            parm1.orig2 = (!parm1.player2.equals("") ? this.position_orig_by : "");
            parm1.orig3 = (!parm1.player3.equals("") ? this.position_orig_by : "");
            parm1.orig4 = (!parm1.player4.equals("") ? this.position_orig_by : "");
            parm1.orig5 = (!parm1.player5.equals("") ? this.position_orig_by : "");

            parm1.show1 = (this.precheckin == 1 && parm1.show1 == 0) ? 2 : parm1.show1;
            parm1.show2 = (this.precheckin == 1 && parm1.show2 == 0) ? 2 : parm1.show2;
            parm1.show3 = (this.precheckin == 1 && parm1.show3 == 0) ? 2 : parm1.show3;
            parm1.show4 = (this.precheckin == 1 && parm1.show4 == 0) ? 2 : parm1.show4;
            parm1.show5 = (this.precheckin == 1 && parm1.show5 == 0) ? 2 : parm1.show5;
        }
        
        return parm1;
        
        
    }
    
    /*
     * Abstractions for legacy non-array parameters specific to parmSlotm
     * 
     */

    @Override
    public String getG(int index){
        return g[index];
    }
    
    @Override
    public String[] getGArray(int length){
        return Arrays.copyOfRange(g,0,length-1);
    }
    
    public int getTime(int index){
        return getIntByName("time%", index);
    }
    
    public int[] getTimeArray(int length){
        return getIntArrayByName("time%", length);
    }
    
    // Get time value for a particular player's group. index is 0-base
    public int getPlayerTime(int index) {
        
        int groupSize = (p5.equals("Yes")?5:4);
        int group = index / groupSize;
        
        return this.getTime(group);
    }
    
    @Override
    public void setG(String value, int index){
        g[index] = value;
    }
    
    @Override
    public void setG(String[] array){
        g = array;
    }
    
    public void setTime(int value, int index){
        setParameterByName("time%",value,  index);
    }
    
    public void setTime(int[] array){
        setParameterByName("time%", array);
    }
    
    
    
}  // end of class