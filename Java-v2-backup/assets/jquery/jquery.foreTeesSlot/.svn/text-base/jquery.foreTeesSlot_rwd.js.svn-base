/*************************************************************
 *
 * foreTeesSlot_rwd Plugin
 * 
 * Used by member slot pages in responsive mode
 *  
 *************************************************************/

(function($){
    
    var pluginData = {
        
        jsonRunnin:false,
        jsonData:{}

    };

    var options = {
        
        waitlist: {
            slotBusyTitle: "Wait List Sign-Up Busy",
            slotBusyNotice: "Sorry, but this wait list entry is currently busy."
            +"<br><br>Please select another time or try again later.",
            noMemberAccessTitle: "Restriction Found",
            noMemberAccessNotice: "b>This wait list is not configured for member access.<br>Please contact your golf shop for assistance.</b>"
        },
        
        activity: {
            multiSlotMessageNew: "NOTE: You have requested [activity_slots] consecutive times.  The player information you enter will be copied to the other times automatically.",
            multiSlotMessageEdit: "NOTE: The time you have requested is part of block of consecutive times.  The changes you make to this time will be copied to the other times automatically."
        },
        
        event: {
            moreInfoTitle:"Additional Information for Sign-up",
            missingInfoPrompt:"Missing Required Information:",
            requiredPrompt:'<b>Note</b>: The questions with <b><span style="background-color:[this_callback.required_field_color];">[this_callback.required_field_color]</span> boxes are required</b> and need to be completed in order to complete your sign-up.',
            pleaseCorrect:"Please correct this and try again.",
            guestInfoTitle:"Additional Guest Only Information",
            playerInfoTitle:"Additional Player Information"
        },
        
        notify: {
            sessionTimeOutTitle: "Session Timed Out",
            sessionTimeOutNotice: "Sorry, but your session has timed out or your database connection has been lost.<BR>"
            +"<BR>Please exit ForeTees and try again.",
            noticeFromGolfShopTitle: "Notice From Your Golf Shop",
            continueWithRequestPrompt: "Would you like to continue with this request?",
            slotBusyTitle: "Time Slot Busy",
            slotBusyNotice: "Sorry, but at least one of the requested tee times are currently busy."
            + "<br><br>All [slots] tee times must be on the same nine and completely unoccupied."
            + "<br><br>Please select another time or try again later.",
            maxOccurancesTitle: "Max Allowed Round Originations Reached",
            dbAccessErrorTitle: "Database Access Error",
            dbAccessErrorNotice: "Sorry, we are unable to process your request at this time."
            + "<BR><BR>Please try again later."
            + "<BR><BR>If problem persists, contact your golf shop.",
            recordInUseTitle: "DB Record In Use Error",
            recordInUseNotice: "Sorry, but this request has been returned to the system."
            + "<br>The system timed out and released the request.",
            eventSignupErrorTitle: "Database Access Error",
            eventSignupErrorNotice: "Sorry, we are unable to process your request at this time."
            + "<BR><BR>Please try again later."
            + "<BR><BR>If problem persists, contact your golf shop.",
            eventBusyTitle: "Event Entry Busy",
            eventBusyNotice: "Sorry, but this entry is currently busy."
            + "<BR><BR>Please select another entry or try again later.",
            timesUnavailableTitle: "Times Unavailable",
            timesUnavailableNotice: "Sorry but we were unable to find enough consecutive times to fulfill your request."
            + "<BR><BR>Please return to the time sheet and select another time.",
            timeUnavailableTitle: "Time Slot Busy",
            timeUnavailableNotice: "Sorry, but this time slot is currently busy."
            + "<BR><BR>Please select another time or try again later.",
            timesOccupiedTitle: "Time Slot Occupied",
            timesOccupiedNotice: "Sorry, but one of the selected time slots is already occupied by another reservation."
            + "<BR><BR>Please select another time or location for this reservation.",
            consecutiveTimePromptTitle: "Member Prompt - Consecutive Time Request",
            consecutiveTimePromptNotAvailable: "The requested length of time was not available.",
            consecutiveTimePromptIntructions: "Please select a length for this reservation from the options below."
            + "<br><br><b>Note</b>: Only available time options for this location are selectable.",
            consecutiveTimePromptIntructions2: "One or more of the time slots you requested is currently busy or otherwise unavailable."
            + "<br><br>The time we did find for you is as follows:", 
            consecutiveTimeUnavailableInstructions: 'Sorry, but the time you selected is currently occupied.',
            consecutiveTimeUnavailableInstructions2: 'Please select from the <span style="font-weight:bold;">other options</span> below, or return to the time sheets.',
            consecutiveTimePromptButtonListOpen: '<div class="slot_button_list"><label>Length:</label>',
            consecutiveTimePromptButtonListClose: '</div>',
            altConsecutiveTimeSuccessPromptTitle:'Time Slots Found',
            altConsecutiveTimeSuccessPromptInstructions:'The following alternate time was found.  Please click <span style="font-weight:bold;">Yes, Continue</span> to '
            + 'accept this time and continue, or <span style="font-weight:bold;">Go Back</span> to return to the time sheet.',
            altConsecutiveTimePromptButtonListOpen: '<div class="slot_button_list">',
            altConsecutiveTimePromptButtonListClose: '</div>',
            massConsecutiveTimeTitle: "Consecutive Time Request",
            massConsecutiveTimeSuccess: "Thank you! Your reservation has been accepted and processed!", 
            massConsecutiveTimeNotice: "Sorry, but an issue was encountered while booking your conecutive tee times. Please try again. If the problem persists, please contact the golf shop staff at the club for assistance.", 
            "----":"----" // keep Netbeans auto-format from messing up the last line
            
        },
        
        lottery: {
            
            detailsTitle:"[slot_type] Registration",
            date:"Date: <b>[day] [mm]/[dd]/[yy]</b>",
            dateCourse:'<span class="ft-textGroup">Date: <b>[day] [mm]/[dd]/[yy]</b></span> <span class="ft-textGroup">Course: <b>[course]</b></span>',
            requestTime:"Time and Tee Requested",
            requestCourse:"Preferred Course",
            requestOtherCourse:"Try the other course if times not available?",
            requestOtherCourses:"Try other courses if times not available?",
            requestSlots:"Number of consecutive tee times you wish to request",
            requestMinsBefore:"Number of hours/minutes <b>before</b> this time you will accept",
            requestMinsAfter:"Number of hours/minutes <b>after</b> this time you will accept",
                
            intructionsOwner:'Provide the requested information below and click on <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.continueButton]"</span> to continue, '
            + '<span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.cancelButton]"</span> to delete the request, or <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.goBackButton]"</span> to return without changes.'
            + "<br><br><b>NOTE:</b> Only the person that originated the request will be allowed to cancel it or change these values.",
        
            intructionsNew:'Provide the requested information below and click on <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.continueButton]"</span> to continue, '
            + 'or <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.goBackButton]"</span> to return without changes.'
            + "<br><br><b>NOTE:</b> Only the person that originated the request will be allowed to cancel it or change these values.",
        
            intructions:'Review the information below and click on <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.continueButton]"</span> to continue, '
            + 'or <span style="white-space: nowrap;">"[modalOptions.slotPageLoadNotification.goBackButton]"</span> to return without changes.'
            + "<br><br><b>NOTE:</b> Only the person that originated the request will be allowed to cancel it or change these values.",
    
            footer:"Tee times to be awarded within the boundaries established by your golf professionals."
            
        },

        player:"player",
        playerPlural:"players",
        playerProper:"Player",
        playerProperPlural:"Players",
        navagateAwayMessage:"Are you sure you want to leave this page without saving your data?",
        onBeforeUnloadMessage:"You are about leave without saving your data.",
        slotAddColor:"#00FF00",  // Color to "flash" the slot field of the newly added player
        slotAddDuration:500,  // how long to flash the field for, in ms.
        slotDuplicateColor:"#FFFF00", // Color to flash the slot field of a player that is already on the list
        slotDuplicateDuration:500,  // how long to flash the field for, in ms.
        slotListFull:"#FF0000", // Color to flash all slot fields when trying to add a player to a full slot page
        slotListFullDuration:500,  // how long to flash the fields for, in ms.
        badMinColor:"#FF0000",  // Color to "flash" the field if a vad value was entered
        badMinDuration:500,  // how long to flash the field for, in ms.
        mainInstructionsTimer:[
        'Add [options.playerPlural] to the reservation and click on "[options.buttonSubmit]" to complete the request. <a class=\"helpButton\" href="#" data-fthelp="slot_page_help" title="How do I use this registration page?"><span>How do I use this registration page?</span></a>',
        'Time remaining: <b class="slot_timer">00:00</b> <a class=\"helpButton\" href="#" data-fthelp="time_remaining" title="What is this timer for?"><span>Help</span></a>'
        ],
        mainInstructionsNoTimer:[
        'Add [options.playerPlural] to the reservation and click on "[options.buttonSubmit]" to complete the request. <a class=\"helpButton\" href="#" data-fthelp="slot_page_help" title="How do I use this registration page?"><span>How do I use this registration page?</span></a>'
        ],
        // mainInstructions:'', // Generated dynamicaly from mainInstructionsTimer/NoTimer depending on parameters passed
        subInstructionsName:"[slot_type]:<b>[name]</b>",
        subInstructionsNumber:"Reservation Number:<b>[reservation_number]</b>",
        subInstructionsDate:"Date:<b>[mm]/[dd]/[yy]</b>",
        subInstructionsTime:"Time:<b>[stime]</b>",
        subInstructionsDayDate:"Date:<b>[day]&nbsp;&nbsp;[mm]/[dd]/[yy]</b>",
        subInstructionsFirstTime:"First Time Requested:<b>[stime]</b>",
        subInstructionsCourse:"Course:<b>[course_disp]</b>",
        subInstructionsLocation:"Location:<b>[location_disp]</b>",
        subInstructionsSeasonLong:'<b>Season Long</b>',
        buttonGoBack:'Go Back',
        buttonSubmitNew:'Submit Request',
        buttonSubmitEdit:'Submit Changes',
        buttonSubmitMoreNew:'Continue Request',
        buttonSubmitMoreEdit:'Continue Changes',
        buttonCloseMemberSelect:'Close',
        // buttonSubmit:'', // Generated dynamicaly from buttonSubmitNew or buttonSubmitEdit depending on parameters passed
        noEditGuestTitle:"Unable to Edit Guest Name",
        noEditGuestMessage:"<p>Guests that have been selected from the foretees system can not be edited directly.</p> <p>To change the guest in this position, use the clear <span class=\"deleteButtonSmall\"></span> icon next to the position, add the new guest.</p>",
        noEditGuestTbdTitle:"Unable to Edit Guest",
        noEditGuestTbdMessage:"<p>Positions that have been marked \"To Be Decided\" (X or TBD) can not be edited directly.</p> <p>To change, use the clear <span class=\"deleteButtonSmall\"></span> icon next to the position, then add the new participant.</p>",
        noEditMemberTitle:"Unable to Edit Member Name",
        noEditMemberMessage:"<p>Member names can not be edited directly.</p> <p>To change the member in this position, use the clear <span class=\"deleteButtonSmall\"></span> icon next to the position, then add the new member.</p>",
        noEditLockedTitle:"Unable to Edit Locked Position",
        noEditLockedMessage:"<p>Sorry, this position is locked and cannot be modified.</p> <p>Please contact the golf shop if you have any questions.</p>",
        addMemberTitle:"Adding a Member",
        addMemberMessage:"<p>You can add members using the member selection tool on the right.</p>",
        dragMemberTitle:"Moving a Participant",
        dragMemberMessage:'To move a participant to a different position, simply grab and drag the <span class="ui-icon ui-icon-arrowthick-2-n-s" style="display:inline-block"></span> icon to the desired position.',
        noOpenSlotTitle:"No Empty Slot Available",
        noOpenSlotMessage:"<p>If you would like to replace a participant with another, first clear the current participant using the <span class=\"deleteButtonSmall\"><b>Clear</b></span> icon on the left.</p>",
        buttonCancel:'Cancel Reservation',
        buttonHelp:'Click for Help',
        slotListHeader:'Add or Remove [options.playerProperPlural]',
        slotListIntructions:'<span>Select Names</span><b>&rarr;</b>',
        slotListErase:'<span>erase</span>',
        slotListPlayerHead:'[options.playerProperPlural]',
        selectMemberPrompt:"Select [options.playerProper] #",
        fullSlotPrompt:"No Empty Slot",
        playerTypeMember:"Member",
        slotListTransHead:'Trans',
        slotList9holeHead:'9 holes',
        slotListGiftPackHead:'[gift_pack_text]',
        slotListGhinHead:'[ghin_text]',
        slotListGenderHead:'Gender',
        slotListCheckNumHead:'Check#',
        slotListMealOptionHead:'Meal Option',
        addPlayerButton:'Add Another [options.playerProper]',
        timePickerFrom:'From:',
        timePickerTo:'To:',
        timePickerInstruct:'Select your desired time range',
        nameListHeader:'Name List',
        nameListInstructions:'Click on name to add',
        guestTypeInstructions:'Add guests immediately <b>after</b> host member.',
        labelNotesToPro:'Notes to pro',
        labelContactNotes:'Contact Info / Notes:',
        forceSinglesMatch:'Singles Match?',
        legendRecur:'Request Recurrence',
        labelRecurType:{
            none:'Do not recur this request',
            weekly:'Recur this request <b>weekly</b>',
            everyOther:'Recur this request <b>every other week</b>'
        },
        labelRecurLength:{
            from:'From:',
            to:'To:',
            until:'Repeat recurrence until:'
        },
        lowTimerTrigger:10 // number of second remaining to set low timer trigger
        
    };
    
    var methods = {
        
        init: function(){
        
            $(this).each(function(){
                
                var obj = $(this);
                var slotParms = {};
                
                switch(slotParms.slot_url){
                    case "Member_slot":
                    case "Member_slotm":
                        slotParms.slot_type = "tee_time";
                        break;
                    case "Member_lott":
                        slotParms.slot_type = "lottery";
                        break;
                }
                
                if(typeof(obj.data("ft-slotParms")) == "undefined"){
                    
                    try{
                        slotParms = JSON.parse($(this).attr("data-ftjson"));
                    }catch(e){
                        // // console.log("bad json");
                        return false;
                    }
                    
                    // Store slot parms 
                    obj.data("ft-slotParms", slotParms);

                }else{
                    slotParms = obj.data("ft-slotParms");
                }
                
                slotParms.disableSlotPage = false;
                
                // Configure some options
                if (slotParms.edit_mode == true && slotParms.ask_more == false) {
                    options.buttonSubmit = options.buttonSubmitEdit
                }else if(slotParms.edit_mode == true && slotParms.ask_more == true){
                    options.buttonSubmit = options.buttonSubmitMoreEdit
                }else if(slotParms.edit_mode == false && slotParms.ask_more == true){
                    options.buttonSubmit = options.buttonSubmitMoreNew
                }else{
                    options.buttonSubmit = options.buttonSubmitNew
                }
                
                if(slotParms.signup_type == ""){
                    slotParms.signup_type = slotParms.slot_type;
                }
                
                options = $.extend(true,{},options,slotParms.options);
                
                //console.log("setting options:"+slotParms.page_start_title);
                slotParms.options = options;
                slotParms.modalOptions = obj.foreTeesModal("getOptions");
                //console.log("test:" + slotParms.options.buttonSubmit);

                // Check if we have any page start notifications
                methods.pageStartNotification(obj);
                
                if(!slotParms.disableSlotPage  && (slotParms.slot_url == "Member_slot" || slotParms.slot_url == "Member_slotm" )){
                    // Double check that our slot page has been marked in_use properly
                    // If a user clicks the browser refresh button on a slot page, it's possible
                    // a window.onunload event cleared our in_use after this page loaded.
                    methods.checkSlotStatus(obj);
                }
                
                // If no page start notifications,
                if(!slotParms.disableSlotPage){
                    
                    // Add a list of the slot parmas we're using (all parameters used by slot page must be listed here)
                    // 
                    // This should eventually be moved to parmSlotPage.java, but will require each slot page to init it correctly
                    // 
                    // Used when changing player order, and when clearing player from slot
                    slotParms.slotParamTable = {
                        
                            "player_a":{defaultValue:"",resetOnClear:true},
                            "user_a":{defaultValue:"",resetOnClear:true},
                            "orig_a":{defaultValue:"",resetOnClear:true},
                            "pcw_a":{defaultValue:"",resetOnClear:true},
                            "custom_a":{defaultValue:"",resetOnClear:true},
                            "custom_disp_a":{defaultValue:"",resetOnClear:true},
                            "homeclub_a":{defaultValue:"",resetOnClear:true},
                            "ghin_a":{defaultValue:"",resetOnClear:true},
                            "gender_a":{defaultValue:"",resetOnClear:true},
                            "email_a":{defaultValue:"",resetOnClear:true},
                            "phone_a":{defaultValue:"",resetOnClear:true},
                            "shoe_a":{defaultValue:"",resetOnClear:true},
                            "shirt_a":{defaultValue:"",resetOnClear:true},
                            "otherA1_a":{defaultValue:"",resetOnClear:true},
                            "otherA2_a":{defaultValue:"",resetOnClear:true},
                            "otherA3_a":{defaultValue:"",resetOnClear:true},
                            "meal_option_a":{defaultValue:"",resetOnClear:true},
                            
                            "gift_pack_a":{defaultValue:0,resetOnClear:true},
                            "guest_id_a":{defaultValue:0,resetOnClear:true},
                            "check_num_a":{defaultValue:1,resetOnClear:true},
                            "p9_a":{defaultValue:slotParms.default_fb_value,resetOnClear:true},
                            
                            "lock_player_a":{resetOnClear:false},
                            "lock_player_a_fb":{resetOnClear:false}
                    };
                    
                    // Create slot page
                    methods.generateSlotPage(obj);
                    
                    // Init slot timer
                    methods.initSlotTimer(obj);
                
                    // Load name list
                    methods.initPlayerSlots(obj);
                    
                    // Member Search (RWD Only (Includes Guest type, TBD, Member search and Partners))
                    methods.initMemberSelect(obj);

                    // Submit button
                    methods.initSubmitButton(obj);
                    
                    // Go Back button
                    methods.initGoBackButton(obj);
                    
                }
                if(!obj.data("ftSlotUnloadInit")){
                    // Handle navigation away from slot page
                    obj.data("ftSlotUnloadInit",true);
                    ftOnSlotExit({
                        onUnload:function(){
                            //console.log("On UNLOAD");
                            //console.log(obj);
                            methods.leaveForm(true, null, obj);
                        },
                        onExit:function(){
                            //console.log("On EXIT");
                            //console.log(obj);
                            methods.leaveForm(true, null, obj);
                        },
                        onBeforeUnload:function(){
                            if(!slotParms.skip_unload){
                                //console.log("On ONBEFORE");
                                return options.onBeforeUnloadMessage;
                            }
                        },
                        navagateAwayMessage:options.navagateAwayMessage
                    });
                }
                $('body').foreTeesModal('pleaseWait', 'close');
                
            });

        },
        
        refreshSlot: function(obj){

            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
          
            //obj.foreTeesModal("pleaseWait");
            slotParms.skip_unload = true;
            // Reload slot page, getting json data
            $.ajax({
                type: 'POST',
                url: slotParms.slot_url,
                data: slotParms.callback_map,
                dataType: 'json',
                success: function(slotParms){
                    //console.log(data);
                    //return;
                    slotParms.skip_unload = false;
                    obj.data("ft-slotParms", slotParms);
                    //obj.foreTeesModal("pleaseWait","close");
                    obj.foreTeesSlot();
                                    
                    return;
                },
                error: function(xhr, e_text, e){
                    slotParms.skip_unload = false;
                    slotParms.disableSlotPage = true;
                    $('body').foreTeesModal("pleaseWait","close");
                    obj.foreTeesModal("ajaxError", {
                        title:"Error Submitting Data",
                        jqXHR: xhr, 
                        textStatus: e_text, 
                        errorThrown: e, 
                        allowBack: true,
                        allowRefresh: false,
                        slotParms: slotParms,
                        tryAgain: function() {
                            methods.refreshSlot(obj);
                        }
                    });
                }
            });  
        },
        
        initSlotTimer: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");

            ftTimer({
                timers:$(".slot_timer"),
                time:slotParms.time_remaining,
                onTimeout:function(){
                    obj.foreTeesModal("slotTimeout");
                }
            });
        },
        
        checkSlotStatus: function(obj){
            
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            
            //obj.foreTeesModal("pleaseWait");
            // Reload slot page, getting json data
            $.ajax({
                type: 'POST',
                url: slotParms.slot_url,
                //url: "bad_url_test",
                data: slotParms.callback_map,
                dataType: 'json',
                //async: false, // Wait for this to complete before continuing
                //context: obj,
                success: function(checkSlotParms){
                    //console.log(data);
                    //return;
                    //obj.foreTeesModal("pleaseWait","close");
                    if(checkSlotParms.page_start_notifications.length > 0){
                        // We have notifications for page start -- display modal and abort activation of slot page
                        // Replace slot parms with new slot parms
                        checkSlotParms.disableSlotPage = true;
                        obj.data("ft-slotParms", checkSlotParms);
                        obj.foreTeesModal("slotPageLoadNotification");
                        
                    }
                    
                    if(slotParms.page_start_notifications.length > 0){
                        // We have notifications for page start -- display modal and abort activation of slot page
                        slotParms.disableSlotPage = true;
                        obj.foreTeesModal("slotPageLoadNotification");
                    }
                    
                    return;
                },
                error: function(xhr, e_text, e){
                    slotParms.disableSlotPage = true;
                    //obj.foreTeesModal("pleaseWait","close");
                    obj.foreTeesModal("ajaxError", {
                        jqXHR: xhr, 
                        title: "Error checking slot status",
                        textStatus: e_text, 
                        errorThrown: e, 
                        allowBack: true,
                        allowRefresh: false,
                        tryAgain: function() {
                            methods.checkSlotStatus(obj);
                        }
                    });
                }
            });

        },
        
        pageStartNotification: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            
            if(slotParms.page_start_notifications.length > 0 || slotParms.page_start_instructions.length > 0 || slotParms.page_start_force){
                // We have notifications for page start -- display modal and abort activation of slot page
                slotParms.disableSlotPage = true;
                obj.foreTeesModal("slotPageLoadNotification");
            }
        },
        
        addGuest: function(guestData, obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            
            // Set default transport type for guest
            var default_cw = "";
            if(typeof slotParms.guest_type_cw_map[guestData.guest_type] == "string"){
                default_cw = slotParms.guest_type_cw_map[guestData.guest_type];
            } else if(typeof slotParms.guest_type_cw_map["_default_"] == "string") {
                default_cw = slotParms.guest_type_cw_map["_default_"];
            }
 
            // Put guest in first blank
            for(var i = 0; i < slotParms.player_count; i++){
                if((((i % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group) || slotParms.players_per_group == 0){
                    if ($.trim(slotParms.player_a[i]) == "" && slotParms.lock_player_a[i] == false){
                        if(typeof guestData.guest_name != "string"){
                            guestData.guest_name = "";
                        }
                        if(typeof guestData.guest_id == "undefined"){
                            guestData.guest_id = 0;
                        }
                        
                        // Check if guest from guest db is already in list
                        if(guestData.guest_id){
                            for(var i2 = 0; i2 < slotParms.player_count; i2++){
                                if(slotParms.guest_id_a[i2] == guestData.guest_id){
                                    // Guest is already on slotpage
                                    var tools = obj.find('.ftS-tools.toolsOpen .slotMemberSelect');
                                    var slotT = obj.find('.ftS-slots');
                                    if(tools.length && tools.offset().left < (slotT.width()+slotT.offset().left)){
                                        obj.foreTeesModal("alertNotice",{
                                            title:"Duplicate Entry",
                                            message:'<p>' + guestData.guest_name + ' has already been added as '+slotParms.options.player+' #'+methods.getSlotNumber(i2, obj)+'.</p>  <p>Please select '+slotParms.options.player+' #'+methods.getSlotNumber(i, obj)+', or close the '+slotParms.options.player+' selection tool using the "Close" button in the upper right corner if you have finished adding '+slotParms.options.playerPlural+'.</p>',
                                            alertMode:false
                                        });
                                    }
                                    $(obj.find(".slot_player_row input.ftS-playerNameInput").get(i2)).foreTeesHighlight(options.slotDuplicateColor, options.slotDuplicateDuration);  // Flash used field yellow
                                    return false;
                                }
                            }
                        }
                        
                        if(guestData.guest_type == "X"){
                            slotParms.player_a[i] = guestData.guest_type;
                        }else{
                            slotParms.player_a[i] = guestData.guest_type + " " + guestData.guest_name;
                        }
                        slotParms.user_a[i] = "";
                        slotParms.guest_id_a[i] = guestData.guest_id;
                        slotParms.pcw_a[i] = default_cw;
                        ftSetIfDefined(slotParms.gender_a, i, guestData.gender);
                        ftSetIfDefined(slotParms.ghin_a, i, guestData.ghin);
                        ftSetIfDefined(slotParms.email_a, i, guestData.email1);
                        ftSetIfDefined(slotParms.phone_a, i, guestData.phone1);
                        ftSetIfDefined(slotParms.address_a, i, guestData.address1);
                        ftSetIfDefined(slotParms.homeclub_a, i, guestData.homeclub);
                        // Update player slots
                        methods.initPlayerSlots(obj);
                        var inputObj = obj.find("#slot_player_row_"+i+" input.ftS-playerNameInput");
                        inputObj.foreTeesHighlight(options.slotAddColor, options.slotAddDuration); // Flash new field green
    
                        //if(!obj.find('.ftS-selectNamesButton a.ftS-playerPrompt:visible').length){
                            // If we're not in small screen mode, give focus to the guest input field
                            inputObj.focus().val(inputObj.val());
                        //}
                        
                        return i;
                    }
                }
            }
            obj.find(".slot_player_row input.ftS-playerNameInput").foreTeesHighlight(options.slotListFull, options.slotListFullDuration);  // Flash all field red
            return false;
        },
        
        firstOpenSlot: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            var first_blank = null;
            for(var i = 0; i < slotParms.player_count; i++){
                //console.log("searching:"+slotParms.player_a[i]);
                if((((i % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group) || slotParms.players_per_group == 0){
                    if ($.trim(slotParms.player_a[i]) == "" && first_blank < 1 && slotParms.lock_player_a[i] == false){
                        // // console.log("found blank");
                        first_blank = i;
                    }
                }
            }
            return first_blank;
        },
        
        addPlayer: function(member,obj){
            
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            /*
            if(!partnerData.display){
                // Build display name
                partnerData.display = partnerData.first + " " + ((partnerData.middle != "")?partnerData.middle+" ":"") + partnerData.last;
            }
            */
            // Check if the name is in the list, and set the first blank player
            var first_blank = -1;
            var found_player = -1;
            for(var i = 0; i < slotParms.player_count; i++){
                //console.log("searching:"+slotParms.player_a[i]);
                if((((i % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group) 
                    || (slotParms.players_per_group == 0 && i < slotParms.player_count)){
                    if(slotParms.player_a[i] == member.display || 
                        (slotParms.user_a && slotParms.user_a[i] 
                        && slotParms.user_a[i].length 
                        && slotParms.user_a[i] == member.username ) ){
                        // This name is already in the list
                        found_player = i;
                        //console.log("found match:"+slotParms.player_a[i]+":"+partnerData.display+":"+i);
                        //break;
                    }else if ($.trim(slotParms.player_a[i]) == ""
                        && slotParms.lock_player_a[i] == false && first_blank == -1){
                        // // console.log("found blank");
                        first_blank = i;
                    }
                }
            }
            if(found_player > -1){
                // We already have this player in the list -- we could do somthing to let the user know
                //console.log("player used:" + member.partner_display);
                var tools = obj.find('.ftS-tools.toolsOpen .slotMemberSelect');
                var slotT = obj.find('.ftS-slots');
                if(tools.length && tools.offset().left < (slotT.width()+slotT.offset().left)){
                    obj.foreTeesModal("alertNotice",{
                        title:"Duplicate Entry",
                        message:'<p>' + member.display + ' has already been added as '+slotParms.options.player+' #'+methods.getSlotNumber(found_player, obj)+'.</p>  <p>Please select '+slotParms.options.player+' #'+methods.getSlotNumber(first_blank, obj)+', or close the '+slotParms.options.player+' selection tool using the "Close" button in the upper right corner if you have finished adding '+slotParms.options.playerPlural+'.</p>',
                        alertMode:false
                    });
                }
                $(obj.find(".slot_player_row input.ftS-playerNameInput").get(found_player)).foreTeesHighlight(options.slotDuplicateColor, options.slotDuplicateDuration);  // Flash used field yellow
                return "duplicateEntry";
            } else if (first_blank < 0) {
                // Player list is full -- we could do somthing to let the user know
                //console.log("list full:"+slotParms.player_count+":" + member.partner_display);
                obj.find(".slot_player_row input.ftS-playerNameInput").foreTeesHighlight(options.slotListFull, options.slotListFullDuration);  // Flash all field red
                return "entryError";
            } else {
                // Set the player
                //console.log("adding player:" + member.partner_display);
                slotParms.player_a[first_blank] = member.display;
                slotParms.user_a[first_blank] = member.username;
                slotParms.guest_id_a[first_blank] = 0;
                
                if(slotParms.gender_a[first_blank] !== undefined){
                    slotParms.gender_a[first_blank] = member.gender
                }
                if(slotParms.ghin_a[first_blank] !== undefined){
                    slotParms.ghin_a[first_blank] = member.ghin
                }
                
                
                // Set the player's default transport type
                
                //console.log("AddPlayer:");
                //console.log(partnerData);
                
                var tmode = "";
                if(slotParms.use_default_member_tmode){
                    tmode = member.wc;
                }
                tmode = methods.validateMemberTmode(tmode, true, obj);
                
                slotParms.pcw_a[first_blank] = tmode;
                // Update player slots
                methods.initPlayerSlots(obj);
                $(obj.find(".slot_player_row input.ftS-playerNameInput").get(first_blank)).foreTeesHighlight(options.slotAddColor, options.slotAddDuration); // Flash new field green
                return "successfulEntry";
            }
            
        },
        
        validateMemberTmode: function(tmode, apply_override, obj){
            
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            
            //console.log("checking:"+tmode);
            //console.log(slotParms.full_tmode_map);
            
            methods.initTmodes(obj);

            if(!((typeof slotParms.full_tmode_map[tmode] == "string" && slotParms.full_tmode_map[tmode].length) || !slotParms.verify_member_tmode)){
                tmode = ""; // we do not allow overriding course
            }
            if(tmode == "" && slotParms.default_member_wc != ""){
                tmode = slotParms.default_member_wc ;
            }
            if(slotParms.default_member_wc_override != "" && apply_override){
                tmode = slotParms.default_member_wc_override ;
            }
            
            return tmode;
            
        },
        
        // Create responsive member selection widget for slot page
        initMemberSelect: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            if(slotParms.show_guest_types){
                // If club allows "X" and "TBD",
                // add "X" and "TBD" as guest types
                slotParms.guest_types_map_full['gt_X'] = {
                    guest_type:'X',
                    guest_type_db:0
                };
                slotParms.guest_types_map_full['gt_TBD'] = {
                    guest_type:'TBD',
                    guest_type_db:0
                };
            }
            var slots = obj.find('.ftS-slot');
            var tools = obj.find('.ftS-tools');
            var memberSelectObj = obj.find(".slotMemberSelect");
            var pageOverlay = $('<div class="ftMs-pageOverlay"></div>');
            var openToolsClass = 'toolsOpen';
            
            // Bind close button
            memberSelectObj.find('.ftS-closeButton a').click(function(e){
                tools.removeClass(openToolsClass);
                $(window).trigger('ft-resize');
                e.preventDefault();
            });
            obj.click(function(e){
                /* Fix IEMobile event bubble 
                 * without this, click will bubble up to pageOverlay and close the modal */
                //e.preventDefault(); 
            });
            // Set overlay click to close modal
            pageOverlay.click(function(e){
                tools.removeClass(openToolsClass);
                $(window).trigger('ft-resize');
                e.preventDefault();
            });
            $(window).on('ft-resize',function(){
                $('.ftS-tools.toolsOpen .slotMemberSelect').foreTeesModal("resize");
                $('.ftS-tools:not(.toolsOpen) .slotMemberSelect').css("top","").css("right","").css("height","").css("width","");
            });
            // Arm select player# button
            obj.find('.ftS-selectNamesButton a.ftS-playerPrompt').click(function(e){
                if(!slots.hasClass('fullSlot')){
                    tools.addClass(openToolsClass);
                    $(window).trigger('ft-resize');
                } else {
                    obj.foreTeesModal("alertNotice",{
                        title:slotParms.options.noOpenSlotTitle,
                        message:slotParms.options.noOpenSlotMessage,
                        alertMode:false
                    });
                }
                e.preventDefault();
            });
            
            // Create overlay for member selection tool when in modal mode
            memberSelectObj.before(pageOverlay); 
            // Use resize and bindResize methods from foreTeesModal for positioning
            memberSelectObj.foreTeesModal("resize",{
                box:memberSelectObj,
                maxWidth:290,
                minHeight:150,
                maxHeight:400,
                scaleHeight:true,
                type:"ftS-memberSelect"
            });
            memberSelectObj.foreTeesModal("bindResize");
            memberSelectObj.foreTeesMemberSelect("init",{
                partnerSelect: (slotParms.user != "buddy"),
                memberSearch: slotParms.show_member_select,
                guestTbd: slotParms.show_member_tbd,
                guestTypes: slotParms.show_guest_types?slotParms.guest_types_map:{},
                showGhin: slotParms.show_ghin_in_list,
                selectedUsers: slotParms.user_a,
                onPartnerSelect:function(record, o){
                    methods.addPlayer(record,obj);
                    o.selectedUsers = slotParms.user_a;
                },
                onMemberSelect:function(record, o){
                    methods.addPlayer(record,obj);
                    o.selectedUsers = slotParms.user_a;
                },
                onGuestSelect:function(record, o){
                    tools.removeClass(openToolsClass);
                    $(window).trigger('ft-resize');
                    if(record.guest_type_db > 0){
                        obj.foreTeesModal("guestDbPrompt","open", {
                            callback:function(guestDbData){
                                obj.foreTeesModal("guestDbPrompt", "close");
                                methods.addGuest($.extend({},guestDbData,record), obj);
                            }
                        });
                    } else {
                        methods.addGuest(record, obj);
                    }
                },
                onTbdSelect:function(record, o){
                    methods.addGuest(record, obj);
                },
                onAfterSelect:function(record, o){
                    if(slots.hasClass('fullSlot')){
                        tools.removeClass(openToolsClass);
                        $(window).trigger('ft-resize');
                    }
                }
            });
        },
        
        initSubmitButton: function(obj){
            if(!obj){
                obj = $(this);
            }
            //var slotParms = obj.data("ft-slotParms");
            
            obj.find("a.submit_request_button").each(function(){
                var button = $(this);
                //button.data("ft-slotParent", obj);
                button.click(function(e){
                    //obj = $(this).data("ft-slotParent");
                    methods.submitForm(obj);
                    e.preventDefault();
                });
            });
            
            obj.find("a.cancel_request_button").each(function(){
                var button = $(this);
                //button.data("ft-slotParent", obj);
                button.click(function(e){
                    //obj = $(this).data("ft-slotParent");
                    methods.cancelSlot(obj);
                    e.preventDefault();
                });
            });
        },
        
        initGoBackButton: function(obj){
            if(!obj){
                obj = $(this);
            }
            //var slotParms = obj.data("ft-slotParms");
            
            obj.find("a.go_back_button").each(function(){
                var button = $(this);
                //$(this).data("ft-slotParent", obj);
                button.click(function(e){
                    //var obj = $(this).data("ft-slotParent");
                    //var slotParms = obj.data("ft-slotParms");\
                    methods.leaveForm(false, true, obj)
                    //obj.foreTeesSlot("exitSlotPage");
                    e.preventDefault();
                });
            });
        },
        
        exitSlotPage: function(obj){
            if(!obj){
                obj = $(this);
            }
            //var slotParms = obj.data("ft-slotParms");
            obj.foreTeesModal("pleaseWait");
            ft_historyBack();
        },
        
        submitForm: function(obj){
            if(!obj){
                obj = $(this);
            }
            obj.foreTeesModal("slotSubmit", "open", "submit_slot");
            
        },
        
        cancelSlot: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            if(slotParms.clientSideCancel){
                obj.foreTeesModal("slotCancelPrompt");
            } else {
                obj.foreTeesModal("slotSubmit", "open", "cancel_prompt");
            }
            
            
        },
        
        leaveForm: function(syncMode, exitOnComplete, obj){
            if(!obj){
                obj = $(this);
            }
            if (exitOnComplete === null || typeof exitOnComplete == "undefined"){
                exitOnComplete = false;
            }
            if (syncMode == null || typeof syncMode == "undefined"){
                syncMode = false;
                //console.log("default sync mode");
            }
            //console.log("async:"+(!syncMode));
            var slotParms = obj.data("ft-slotParms");
            //console.log("leaveForm:");
            //console.log(slotParms);
            slotParms.syncMode = syncMode;
            slotParms.exitOnComplete = exitOnComplete;
            var formValues = methods.getFormParameters(obj);
            formValues["cancel"] = "cancel";
            formValues["json_mode"] = "true";
            
            if(typeof slotParms.timer_interval != "undefined"){
                clearTimeout(slotParms.timer_interval);
            }
            
            if(typeof slotParms.skip_unload == "undefined"){
                slotParms.skip_unload = false;
            }
            if(!slotParms.skip_unload){
                //console.log("freeing slot...");
                //console.log("sending "+((syncMode)?"sync":"async")+" to:"+slotParms.slot_url+"|"+JSON.stringify(formValues));
                $.ajax({
                    type: 'GET',
                    url: slotParms.slot_url,
                    data: formValues,
                    async: !syncMode,
                    dataType: 'text',
                    //context: obj,
                    success: function(data){
                        //var obj = $(this);
                        //var slotParms = obj.data("ft-slotParms");
                        //console.log("Slot freed");
                        slotParms.skip_unload = true;
                        if(typeof exitOnComplete == "function" ){
                            exitOnComplete();
                        } else if(slotParms.exitOnComplete){
                            methods.exitSlotPage(obj);
                        }
                        //console.log("Free slot result:"+data.replace(/(\r\n|\n|\r)/gm,""));
                        //return;
                    },
                    error: function(xhr){
                        //console.log("Error unloading slot");
                        ft_logError("Error exiting slot page", xhr);
                        //var obj = $(this);
                        //var slotParms = obj.data("ft-slotParms");
                        if(typeof exitOnComplete == "function" ){
                            exitOnComplete();
                        } else if(slotParms.exitOnComplete){
                            methods.exitSlotPage(obj);
                        }
                        
                    //console.log("Free slot error.");
                    }
                });
            } else {
                //console.log("Skipping unload...");
            }
            
        },
        
        getFormParameters: function(obj){
            
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            var formParameters = {};
            //console.log(slotParms.slot_submit_map);
            for(var name in slotParms.slot_submit_map){
                $.extend(formParameters,ftFieldToObject(slotParms[slotParms.slot_submit_map[name]], name));
            }
            return formParameters;
            
        },
        
        initTmodes: function(obj){
            if(!obj){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            // Create full tmode lookup object, if one does not exist
            if(typeof slotParms.full_tmode_map != "object"){
                slotParms.full_tmode_map = {};
                for(var i = 0; i < slotParms.allowed_tmodes_list.length; i++){
                    if(typeof slotParms.course_parms.tmodea[i] == "string" && slotParms.allowed_tmodes_list[i] != ""){
                        slotParms.full_tmode_map[slotParms.allowed_tmodes_list[i]] = slotParms.allowed_tmodes_list[i];
                    }
                }
                /*
                for(var key in slotParms.guest_type_cw_map){
                    if(typeof slotParms.guest_type_cw_map[key] == "string" && slotParms.guest_type_cw_map[key] != ""){
                        slotParms.full_tmode_map[slotParms.guest_type_cw_map[key]] = slotParms.guest_type_cw_map[key];
                    }
                }

                if(slotParms.default_member_wc != ''){
                    slotParms.full_tmode_map[slotParms.default_member_wc] = slotParms.default_member_wc;
                }
                
                if(slotParms.default_member_wc_override != ''){
                    slotParms.full_tmode_map[slotParms.default_member_wc_override] = slotParms.default_member_wc_override;
                }
                //console.log(slotParms.full_tmode_map);
                */
            }
        },
        
        initPlayerSlots: function(obj){
            
            if(obj == null){
                obj = $(this);
            }
            
            var tag = ftGetTagNames();

            var slotParms = obj.data("ft-slotParms");
            var initKey = "ft-slotInitialized";
            var playerIndexKey = "ft-slotPlayerIndex"
            
            var useTitles = (slotParms.group_titles && slotParms.group_titles.length);

            obj.find("select.partner_list option").removeClass("on_slot");
            
            methods.initTmodes(obj);
            
            slotParms.playerSlotLookup = {};
 
            var playerSlotContainer = obj.find(".ftS-playerSlots");
            
            
            if(slotParms.rebuildSlots){
                //console.log("rebuilding slot page");
                
                // Make sure arrays are sized properly
                for(var k in slotParms.slotParamTable){
                    if(typeof slotParms[k] == "undefined"){
                        slotParms[k] = [];
                    }
                    for(var ki = 0; ki < slotParms.player_count; ki++){
                        if(typeof slotParms[k][ki] == "undefined"){
                            slotParms[k][ki] = slotParms.slotParamTable[k].defaultValue;
                        }
                    }
                }
            
                if(playerSlotContainer.hasClass("ui-sortable")){
                    playerSlotContainer.sortable("destroy");
                }
                
                var playerSlotHtml = "";
                for (var i2 = 0; i2 < slotParms.player_count; i2++) {
                   playerSlotHtml += methods.generatePlayerSlot(i2, obj);
                }
                playerSlotContainer.empty(playerSlotHtml);
                playerSlotContainer.append(playerSlotHtml);
                
                //obj.find(".slotMemberSelect").trigger("ftMs-update");

            }
            
            // Add player slot button
            if(slotParms.add_players){
                var aplc = obj.find('.ftS-addPlayers');
                var gl = aplc.get(0);
                if(slotParms.player_count >= slotParms.max_players){
                    aplc.addClass('hide');
                } else {
                    aplc.removeClass('hide');
                }
                if(gl && !gl.ftInit){
                    aplc.find('button').click(function(e){
                        
                        // Rebuild the slots
                        slotParms.player_count ++;
                        if(slotParms.player_count > slotParms.max_players){
                            slotParms.player_count = slotParms.max_players;
                        }
                        slotParms.rebuildSlots = true;
                        methods.initPlayerSlots(obj);
                        //console.log('Add Player:'+slotParms.player_count);
                        
                        e.preventDefault();
                        
                    });
                    gl.ftInit = true;
                }
            }
            
            // Init pro notes
            obj.find(".ftS-notes").each(function(){
                var noteRow = $(this);
                noteRow.find("textarea").each(function(){
                    var noteText = $(this);
                    noteText.val(slotParms.notes);
                    noteText.change(function(){
                        slotParms.notes = $(this).val();
                    });
                    noteRow.find("a.notes_erase_button").each(function(){
                        var button = $(this);
                        button.click(function(e){
                            noteText.val("");
                            slotParms.notes = "";
                            e.preventDefault();
                        });
                    });
                });                
            });
            
            // Initialize player slots
            obj.find(".slot_player_row").each(function(player_index){ 
                
                // Configure check_num
                if(slotParms.show_check_num){
                    $(this).find("select.slot_check_num").each(function(){
                        var el = $(this);
                        var gl = el.get(0);
                        if(!gl.ftInit){
                            // Add check type options
                            var ar = slotParms.check_num_a;
                            gl.ftInit = true;
                            var html = '';
                            for(var i = 0; i < slotParms.player_count; i++){
                                html += '<option value="'+(i+1)+'">'+(i+1)+'</option>';
                            }
                            el.append(html);
                            if(!ar[player_index]){
                                ar[player_index] = 1;
                            }
                            el.val(ar[player_index]);
                            el.change(function(player_index){
                                return function(){
                                    ar[player_index] = $(this).val();
                                }
                            }(player_index));
                        }
                    });
                }
                
                // Configure meal_options
                if(slotParms.meal_options && slotParms.meal_options.length){
                    $(this).find("select.slot_meal_option").each(function(){
                        var el = $(this);
                        var gl = el.get(0);
                        var found = false;
                        var ar = slotParms.meal_option_a;
                        if(!gl.ftInit){
                            // Add meal options
                            gl.ftInit = true;
                            for(var i = 0; i < slotParms.meal_options.length; i++){
                                var mo = slotParms.meal_options[i];
                                var op = $('<option></option>');
                                var val = mo.category;
                                op.text(val+': $'+mo.cost);
                                op.attr('value',val);
                                if(val == ar[player_index]){
                                    op.prop('selected', true);
                                    found = true;
                                }
                                el.append(op);
                            }
                            if(!found){
                                ar[player_index] = slotParms.meal_options[0].category;
                            }
                            el.val(ar[player_index]);
                            el.change(function(player_index){
                                return function(){
                                    ar[player_index] = $(this).val();
                                }
                            }(player_index));
                        } else if(typeof ar[player_index] == 'undefined' || !ar[player_index].length) {
                            el.change();
                        }
                    });
                }
                
                // Fill transport select list
                var objPlayerRow = $(this);
                $(this).find("select.transport_type").each(function(){
                    var selectObj = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        selectObj.attr("disabled", "diabled");
                    }else{
                        selectObj.removeAttr("disabled");
                    }
                    
                    // Validate tmode for this player
                    if(slotParms.pcw_a[player_index].length){
                        slotParms.pcw_a[player_index] = methods.validateMemberTmode(slotParms.pcw_a[player_index], null, obj);
                    }
                    
                    if(typeof(selectObj.data(playerIndexKey)) == "undefined"){
                        // Create and set the transport options
                        selectObj.find("option").remove(); // clear the list
                        var selected = ((slotParms.pcw_a[player_index] == "")?" selected":"");
                        var option = $('<option value=""'+selected+'></option>');
                        option.data(playerIndexKey, player_index);
                        selectObj.append(option); // Add blank option as first
                        selectObj.data(playerIndexKey, player_index);
                        for (var i = 0; i < slotParms.tmodes_list.length; i++){
                            if(slotParms.tmodes_list[i] != ""){
                                selected = ((slotParms.tmodes_list[i] == slotParms.pcw_a[player_index])?" selected":"");
                                option = $('<option value="'+slotParms.tmodes_list[i]+'"'+selected+'>'+slotParms.tmodes_list[i]+'</option>');
                                option.data(playerIndexKey, player_index);
                                selectObj.append(option);
                            }
                        }
                        // Bind our on-change event
                        selectObj.change(function(){
                            var player_index = $(this).find("option:selected").first().data(playerIndexKey);
                            slotParms.pcw_a[player_index] = $(this).val();
                        });
                    }
                    
                    // Select the proper transport option
                    selectObj.find("option").each(function(){
                        if(typeof($(this).data("ft-slotCustomTransportType")) == "string" && ($(this).data("ft-slotCustomTransportType") != slotParms.pcw_a[player_index])){
                            $(this).remove(); // Clear custom type, if it's not ours
                        }else if ($(this).attr("value") == slotParms.pcw_a[player_index]){
                            selectObj.val(slotParms.pcw_a[player_index]); // select this type, if it is ours
                        }
                    });
                    // Add missing type if not set, and select it
                    if((selectObj.val() == "") && (slotParms.pcw_a[player_index] != "")){
                        // Check if this is an availible pro tmode
                        if(typeof slotParms.full_tmode_map[slotParms.pcw_a[player_index]] == "string" || !slotParms.verify_member_tmode){
                            option = $('<option value="'+slotParms.pcw_a[player_index]+'" selected>'+slotParms.pcw_a[player_index]+'</option>');
                            option.data(playerIndexKey, player_index);
                            option.data("ft-slotCustomTransportType", slotParms.pcw_a[player_index])
                            selectObj.append(option);
                        }else{
                            slotParms.pcw_a[player_index] = "";  // Default tmode not availible -- remove it
                        }
                    }
                    if(typeof slotParms.full_tmode_map[slotParms.pcw_a[player_index]] == "string"){
                        selectObj.val(slotParms.pcw_a[player_index]);
                    }
                 
                });
                // Fill player names
                objPlayerRow.find("input.ftS-playerNameInput").each(function(){
                    var playerCell = objPlayerRow.find('.ftS-playerCell');
                    var el = $(this);
                    var pt = objPlayerRow.find('.playerType');
                    var playerName = $.trim(slotParms.player_a[player_index]);
                    var lastPlayerName = playerCell.data('ftS-lastPlayerName');
                    if(slotParms.lock_player_a[player_index]){
                        el.attr("disabled", "disabled");
                    }else{
                        el.removeAttr("disabled");
                    }
                    
                    //console.log("Searcing for guest type in:"+playerName);
                    var playerTest = playerName.toLowerCase();
                    var found_gt = null;
                    var found_gt_name = null;
                    var playerTypeCode = 0;
                    //var origPlayerTypeName = pt.html();
                    var playerTypeName = "";
                    if(playerName !== lastPlayerName){
                        playerCell.data('ftS-lastPlayerName',playerName);
                        //console.log("Parsing player name:"+playerName);
                        for(var key in slotParms.guest_types_map_full){
                            var spgtm = slotParms.guest_types_map_full[key];
                            var gt = spgtm.guest_type_filter;
                            var gte = spgtm.guest_type_filter_exact;
                            if(!gt){
                                gt = spgtm.guest_type.toLowerCase()+" ";
                                gte = spgtm.guest_type.toLowerCase();
                                spgtm.guest_type_filter = gt;
                                spgtm.guest_type_filter_exact = gte;
                            }
                            //console.log("testing:"+gt);
                            if((playerTest.substring(0,gt.length) == gt || playerTest == gte) && (found_gt == null || gt.length > slotParms.guest_types_map_full[found_gt].guest_type.length)){
                                //console.log("found:"+gt);
                                found_gt = key;
                                found_gt_name = gte;
                            }
                        }
                        if(found_gt != null){
                            //console.log(found_gt_name);
                            playerTypeName = $.trim(slotParms.guest_types_map_full[found_gt].guest_type);
                            objPlayerRow.removeClass("playerTypeMember").removeClass("emptySlot");
                            if(slotParms.guest_types_map_full[found_gt].guest_type_db > 0){
                                // Guest from database
                                playerTypeCode = 2;
                                objPlayerRow.removeClass("playerTypeGuest").addClass("playerTypeGuestDb");
                                el.prop("readonly", true);
                            } else if(found_gt_name == 'tbd' || found_gt_name == 'x'){
                                // Guest tbd
                                playerTypeCode = 3;
                                objPlayerRow.removeClass("playerTypeGuest").removeClass("playerTypeGuestDb").addClass("playerTypeGuestTbd");
                                el.prop("readonly", true);
                            } else {
                                // Guest from user entry
                                playerTypeCode = 4;
                                objPlayerRow.removeClass("playerTypeGuestDb").addClass("playerTypeGuest");
                                el.prop("readonly", false);
                            }
                            playerName = $.trim(playerName.substring(playerTypeName.length));
                        }else if(playerTest == "x"){
                            // Guest tbd
                            playerTypeName = "X";
                            playerName = "";
                            playerTypeCode = 3;
                            objPlayerRow.removeClass("playerTypeGuest").removeClass("playerTypeGuestDb").addClass("playerTypeGuestTbd");
                            el.prop("readonly", true);
                        }else if(playerName != ""){
                            playerTypeName = slotParms.options.playerTypeMember;
                            playerTypeCode = 1;
                            objPlayerRow.removeClass("playerTypeGuestTbd").removeClass("playerTypeGuestDb").removeClass("playerTypeGuest").removeClass("emptySlot").addClass("playerTypeMember");
                            el.prop("readonly", true);
                        } else {
                            objPlayerRow.removeClass("playerTypeGuestTbd").removeClass("playerTypeGuestDb").removeClass("playerTypeGuest").removeClass("playerTypeMember").addClass("emptySlot");
                            el.prop("readonly", true);
                        }
                        pt.html(playerTypeName);
                        pt.data('ft-playerType',playerTypeCode);
                        el.val(playerName);
                        objPlayerRow.addClass("redrawBug"); // Fix an issue where webkit will not resize text input
                        objPlayerRow.removeClass("redrawBug");
                    }
                    
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        // What to do when data in the text box changes
                        el.data(playerIndexKey, player_index);
                        el.change(function(){
                            var player_index = $(this).data(playerIndexKey);
                            var newName = $(this).val();
                            if(pt.data('ft-playerType')>1){
                                newName = $.trim(pt.html())+" "+newName;
                            }
                            slotParms.player_a[player_index] = newName;
                        });
                        // Handle clicks into the player name cell
                        playerCell.click(function(e){
                            var el = $(this);
                            if(el.closest('.slot_player_row.ftS-lockedPlayer').length){
                                obj.foreTeesModal("alertNotice",{
                                    title:slotParms.options.noEditLockedTitle,
                                    message:slotParms.options.noEditLockedMessage,
                                    alertMode:false
                                });
                                return;
                            }
                            var pulseMemberSelect = function(){
                                obj.find('#memberSelectPrompt').addClass('ftS-noticeChange').removeClass('ftS-noticeChange',800);
                            }
                            switch(pt.data('ft-playerType')){
                                case 0: // Empty slot
                                    var playerSelectButton = obj.find('.ftS-selectNamesButton a.ftS-playerPrompt:visible');
                                    if(playerSelectButton.length){
                                        // If we're in small screen mode, just open the member select modal
                                        playerSelectButton.click();
                                    } else {
                                        // Else, display a message telling the user to use the tools on the right.
                                        obj.foreTeesModal("alertNotice",{
                                            title:slotParms.options.addMemberTitle,
                                            message:slotParms.options.addMemberMessage,
                                            alertMode:false,
                                            suppressCode:"add_member_prompt",
                                            onSuppress:pulseMemberSelect
                                        });
                                    }
                                    break;
                                case 1: // Member
                                    // Display a message telling the user they must first remove a member from the slot to change them
                                    obj.foreTeesModal("alertNotice",{
                                        title:slotParms.options.noEditMemberTitle,
                                        message:slotParms.options.noEditMemberMessage,
                                        alertMode:false,
                                        suppressCode:"edit_member_prompt",
                                        onSuppress:pulseMemberSelect
                                    });
                                    break;
                                case 2: // Guest from guest db
                                    // Display a message telling the user they must first remove a guest from the slot to change them
                                    obj.foreTeesModal("alertNotice",{
                                        title:slotParms.options.noEditGuestTitle,
                                        message:slotParms.options.noEditGuestMessage,
                                        alertMode:false,
                                        suppressCode:"edit_guest_prompt",
                                        onSuppress:pulseMemberSelect
                                    });
                                    break;
                                case 3: // TBD Guest type
                                    // Display a message telling the user they must first remove the TBD from the slot to change it
                                    obj.foreTeesModal("alertNotice",{
                                        title:slotParms.options.noEditGuestTbdTitle,
                                        message:slotParms.options.noEditGuestTbdMessage,
                                        alertMode:false,
                                        suppressCode:"edit_tbd_prompt",
                                        onSuppress:pulseMemberSelect
                                    });
                                    break;
                            }
                        });
                    }
                    
                });
                // Fill 9-Holes
                if(slotParms.set_default_fb_value){
                    slotParms.p9_a[player_index] = slotParms.default_fb_value;
                }
                objPlayerRow.find("input.slot_9holes").each(function(){
                    var el = $(this);
                    if(slotParms.lock_player_fb_a[player_index] || slotParms.lock_player_a[player_index] || slotParms.lock_fb){
                        el.attr("disabled", "disabled");
                    }else{
                        el.removeAttr("disabled");
                    }
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        el.data(playerIndexKey, player_index);
                        el.change(function(){
                            var player_index = $(this).data(playerIndexKey);
                            slotParms.p9_a[player_index] = (($(this).prop("checked"))?1:0);
                        });
                    }
                    el.prop("checked", (slotParms.p9_a[player_index] > 0));
                });
                // Fill Gift pack
                objPlayerRow.find("input.slot_gift_pack").each(function(){
                    var el = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        el.attr("disabled", "diabled");
                    }else{
                        el.removeAttr("disabled");
                    }
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        el.data(playerIndexKey, player_index);
                        el.change(function(){
                            var player_index = $(this).data(playerIndexKey);
                            slotParms.gift_pack_a[player_index] = (($(this).prop("checked"))?1:0);
                        });
                    }
                    el.prop("checked", (slotParms.gift_pack_a[player_index] > 0));
                });
                // Fill Gender
                objPlayerRow.find("select.slot_gender").each(function(){
                    var el = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        el.attr("disabled", "diabled");
                    }else{
                        el.removeAttr("disabled");
                    }
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        el.data(playerIndexKey, player_index);
                        el.append('<option value=""></option>');
                        el.append('<option value="M">M</option>');
                        el.append('<option value="F">F</option>');
                        el.change(function(){
                            var player_index = $(this).data(playerIndexKey);
                            ftSetIfDefined(slotParms.gender_a, player_index, $(this).val());
                        });
                    }
                    if(slotParms.gender_a[player_index] !== undefined){
                        el.val(slotParms.gender_a[player_index]);
                    }
                });
                // Fill Ghin
                objPlayerRow.find("input.slot_ghin").each(function(){
                    var el = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        el.attr("disabled", "diabled");
                    }else{
                        el.removeAttr("disabled");
                    }
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        el.data(playerIndexKey, player_index);
                        el.change(function(){
                            var player_index = $(this).data(playerIndexKey);
                            ftSetIfDefined(slotParms.ghin_a, player_index, $(this).val());
                        //console.log(JSON.stringify(slotParms.ghin_a));
                        });
                    }
                    if(slotParms.ghin_a[player_index] !== undefined){
                        el.val(slotParms.ghin_a[player_index]);
                    }
                });
                // Initialize the player erase buttons
                objPlayerRow.find(".player_erase_button").each(function(){
                    var el = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        el.css("visibility", "hidden");
                    }
                    if(typeof(el.data(playerIndexKey)) == "undefined"){
                        el.data(playerIndexKey, player_index);
                        el.click(function(e){
                            var player_index = $(this).data(playerIndexKey);
                            // Check if we can do this
                            var allowRemove = true;
                            if(slotParms.lock_owner && slotParms.user_a[player_index] == slotParms.owner){
                                // Trying to remove self from new registration
                                allowRemove = false;
                                $(document).foreTeesModal("alertNotice", {
                                    width:700,
                                    title:"Unable to remove "+slotParms.player_a[player_index],
                                    message_head: 'Sorry, you must be part of any reservation you create.',
                                    closeButton:"Close"
                                });
                                e.preventDefault(); 
                                return;
                            }
                            /*
                            if(slotParms.use_owner && slotParms.user_a[player_index] == slotParms.owner){
                                // Check if there are other valid users:
                                var owner_options = [];
                                for(var i = 0; i < slotParms.player_count; i++){
                                    if(slotParms.user_a[i] != "" && slotParms.user_a[i] != slotParms.owner){
                                        owner_options.push({text:slotParms.player_a[i],value:i});
                                    }
                                }
                                if(!users.length){
                                    // No other valid memebers
                                    allowRemove = false;
                                    $(document).foreTeesModal("alertNotice", {
                                        width:700,
                                        title:"Unable to remove "+slotParms.player_a[player_index],
                                        message_head: 'Before '+slotParms.player_a[player_index]+' can be removed from this reservation, another member must be part of the reservation.',
                                        closeButton:"Close"
                                    });
                                    e.preventDefault(); 
                                    return;
                                } else {
                                    // Prompt for the new owner
                                    allowRemove = false;
                                    var form_data = {
                                        new_owner:owner_options[0].value
                                    }
                                    var form_list = [
                                        {
                                            key:'new_owner',
                                            type:'select',
                                            label:'Select New Reservation Owner',
                                            value:owner_options
                                        }
                                    ]
                                    $(document).foreTeesModal("alertNotice", {
                                        width:700,
                                        title:"Remove "+slotParms.player_a[player_index],
                                        message_head: slotParms.player_a[player_index]+' is the owner of this reservation. Please select another member as the new owner.',
                                        form_data: form_data,
                                        form_list: form_list,
                                        closeButton:"Close",
                                        continueButton:"Continue",
                                        continueAction:function(modal){
                                            // remove and switch owner
                                            modal.ftDialog("close");
                                            var newOwner = parseInt(form_data.new_owner,10);
                                            slotParms.owner = slotParms.user_a[newOwner];
                                            for(var k in slotParms.slotParamTable){
                                                if(slotParms[k] && slotParms.slotParamTable[k].resetOnClear){
                                                    slotParms[k][player_index] = slotParms.slotParamTable[k].defaultValue;
                                                }
                                            }
                                            obj.foreTeesSlot("initPlayerSlots");
                                        }
                                    });
                                    e.preventDefault(); 
                                    return;
                                    
                                }
                            }
                            */
                            // reset slot params for this slot
                            if(allowRemove){
                                for(var k in slotParms.slotParamTable){
                                    if(slotParms[k] && slotParms.slotParamTable[k].resetOnClear){
                                        slotParms[k][player_index] = slotParms.slotParamTable[k].defaultValue;
                                    }
                                }
                                methods.initPlayerSlots(obj);
                                obj.find(".slotMemberSelect").trigger("ftMs-update");
                                e.preventDefault(); 
                            }
                            
                        });
                    }
                });
            });
            
            // Clean up slots
            if(slotParms.rebuildSlots || !slotParms.madeSortable){
                //console.log('Binding sortable');
                slotParms.rebuildSlots = false;
                slotParms.madeSortable = true;
                var phObj;
               //var colCount = playerSlotContainer.find('.slot_player_row').first().find('.rwdTd').length;
               //for(var pc = 0; pc < colCount; pc++){
               playerSlotContainer.find('.slot_player_row').first().find('.rwdTd').each(function(){
                   var el = $(this);
                   phObj += $('<'+tag.td+' class="'+el.attr("class")+'"><div>&nbsp;</div></'+tag.td+'>');
               });
                   
               //}

                playerSlotContainer.sortable({
                    items: ".slot_player_row.ftS-unlockedPlayer",
                    axis:"y",
                    handle:".sortHandle",
                    forcePlaceholderSize: true,
                    placeholder:'ftS-sortHolder',
                    helper:function(e, ui){
                        var originals = ui.children();
                        var helper = ui.clone();
                        helper.children().each(function(index)
                        {
                            // Set helper cell sizes to match the original sizes
                            $(this).width(originals.eq(index).innerWidth());
                        });
                        return helper;
                    },
                    start:function( e, ui ) {
                        slotParms.sotableStart = ui.item.index() - (useTitles?1:0);
                        // Build a placeholder cell that spans all the cells in the row
                        // Keeps table from collapsing
                        /*
                        var cellCount = 0;
                        $('td, th', ui.helper).each(function () {
                            // For each TD or TH try and get it's colspan attribute, and add that or 1 to the total
                            var colspan = 1;
                            var colspanAttr = $(this).attr('colspan');
                            if (colspanAttr > 1) {
                                colspan = colspanAttr;
                            }
                            cellCount += colspan;
                        });

                        // Add the placeholder UI - note that this is the item's content, so TD rather than TR
                        ui.placeholder.html('<td colspan="' + cellCount + '"><div>&nbsp;</div></td>');
                        */
                       
                       ui.placeholder = phObj;
                    },
                    stop: function( e, ui ) {
                        var start = slotParms.sotableStart;
 
                        var end = ui.item.index() - (useTitles?1:0);
                        var i2;
                        // Compensate for the spacer <tr>'s between groups in the DOM
                        for (i2 = 0; i2 < start; i2++) {
                            if ((i2 > 0) && (i2 % slotParms.players_per_group == 0)) {
                                start--;
                            }
                        }
                        for (i2 = 0; i2 < end; i2++) {
                            if ((i2 > 0) && (i2 % slotParms.players_per_group == 0)) {
                                end--;
                            }
                        }
                        // Check if there are any locked slots in the range
                        var locked = [];
                        var istart = start;
                        var locked_offset = -1;
                        if(istart > end){
                            locked_offset = 1;
                            istart = end;
                        }
                        var iend = end;
                        if(iend < start){
                            iend = start;
                        }
                        for(i2 = istart; i2 <= iend; i2++){
                            if(slotParms.lock_player_a[i2]){
                                locked.push(i2);
                            }
                        }
                        // Change slot order based on what the sortable did
                        for(var k in slotParms.slotParamTable){
                            if(slotParms[k]){
                                var origVal = slotParms[k][start];
                                slotParms[k].splice(start,1);
                                slotParms[k].splice(end,0,origVal);
                            }
                        }
                        for(i2 in locked){
                            start = locked[i2] + locked_offset;
                            end = locked[i2];
                            // Move locked back to original position
                            for(k in slotParms.slotParamTable){
                                if(slotParms[k]){
                                    origVal = slotParms[k][start];
                                    slotParms[k].splice(start,1);
                                    slotParms[k].splice(end,0,origVal);
                                }
                            }
                        }
                        // Rebuild the slots
                        slotParms.rebuildSlots = true;
                        methods.initPlayerSlots(obj);
                    }
                });
            }
            
            // Fill Recur Type Selection  
            obj.find('input[name=recur_type]').each(function(){
                //console.log("found...");
                var el = $(this);
                if(typeof(el.data(initKey)) == "undefined"){
                    //console.log("binding...");
                    el.data(initKey, true);
                    el.change(function(){
                        var pForm = $(this).closest("form");
                        var val = pForm.find('input[name='+$(this).attr('name')+']:checked').val();
                        var lDiv = pForm.find(".ftS-recur .recurLength");
                        if(val && val.length > 0 && parseInt(val,10) > 0 ){
                            lDiv.css('display','block');
                        } else {
                            lDiv.css('display','none');
                            val = 0;
                        }
                        slotParms.recur_type = val;
                    });
                    if(el.val() == slotParms.recur_type){
                        el.prop('checked', true);
                    }
                    el.change();
                }
            });
            
            // Recur Length
            obj.find('input[name=recur_end]').each(function(){
                var el = $(this);
                if(typeof(el.data(initKey)) == "undefined"){
                    //console.log("binding...");
                    el.data(initKey, true);
                    el.change(function(){
                        slotParms.recur_end = $(this).val();
                    });
                    // set default for the recurr end date
                    el.val(slotParms.recur_start);
                    el.change();
                }
            });

            // Fill "Force Singles"
            obj.find('select[name="force_singles"]').each(function(){
                var el = $(this);
                if(typeof(el.data(initKey)) == "undefined"){
                    el.data(initKey, true);
                    el.append('<option value="0">No</option>');
                    el.append('<option value="1">Yes</option>');
                    el.change(function(){
                        slotParms.force_singles = $(this).val();
                    });
                }
                el.val(slotParms.force_singles);
            });
            // Fill time picker
            obj.find('.ftS-timePicker').each(function(){
                var tr = $(this);
                // Minutes
                tr.find('input[name$="_min"]').each(function(){
                    var el = $(this);
                    if(typeof(el.data(initKey)) == "undefined"){
                        el.data(initKey, true);
                        el.click(function(){
                            $(this).select();
                        });
                        el.attr("maxlength", 2);
                        el.data("ft-lastValue", $(this).attr("name"));
                        el.change(function(){
                            var el2 = $(this);
                            el2.keyup();
                            var new_value = parseInt(el2.val());
                            if(isNaN(new_value)){
                                new_value = 0;
                            }
                            el2.val(ftPadInt(new_value,2));
                            slotParms[el2.attr("name")] = el2.val();
                            el2.data("ft-lastValue", new_value);
                        });
                        el.keyup(function(){
                            var el2 = $(this);
                            var error = false;
                            var last_value = parseInt(el2.data("ft-lastValue"));
                            if(isNaN(last_value)){
                                last_value = 0;
                                error = true;
                            }
                            var new_value = parseInt(el2.val());
                            if(isNaN(new_value) && el2.val() != ""){
                                new_value = last_value;
                                error = true;
                            }
                            if(new_value > 59){
                                new_value = last_value;
                                error = true;
                            }
                            if(new_value < 0){
                                new_value = last_value;
                                error = true;
                            }
                            if(error){
                                el2.foreTeesHighlight(options.badMinColor, options.badMinDuration);
                                el2.val(ftPadInt(new_value,2));
                            }
                        });
                    }
                    el.val(slotParms[el.attr("name")]);
                    el.change();
                });
                
                // Hours
                tr.find('select[name$="_hr"]').each(function(){
                    var el = $(this);
                    if(typeof(el.data(initKey)) == "undefined"){
                        el.data(initKey, true);
                        for(var hour = 1; hour <= 12; hour ++ ){
                            el.append('<option value="'+hour+'">'+hour+'</option>');
                        }
                        el.change(function(){
                            slotParms[$(this).attr("name")] = $(this).val();
                        });
                    }
                    el.val(slotParms[el.attr("name")]);
                });
                
                // Am Pm
                tr.find('select[name$="_ampm"]').each(function(){
                    var el = $(this);
                    if(typeof(el.data(initKey)) == "undefined"){
                        el.data(initKey, true);
                        el.append('<option value="AM">AM</option>');
                        el.append('<option value="PM">PM</option>');
                        el.change(function(){
                            slotParms[$(this).attr("name")] = $(this).val();
                        });
                    }
                    el.val(slotParms[el.attr("name")]);
                });
            });
            var openSlot = methods.firstOpenSlot(obj);
            if(openSlot != null){
                obj.find('.ftS-slot').addClass('emptySlot').removeClass('fullSlot');
                obj.find('.ftS-playerPrompt').html(ftReplaceKeyInString(options.selectMemberPrompt,slotParms)+ methods.getSlotNumber(openSlot, obj) );
                obj.find('#memberSelectPrompt').addClass('ftS-noticeChange').removeClass('ftS-noticeChange',800);
            } else {
                obj.find('.ftS-slot').addClass('fullSlot').removeClass('emptySlot');
                obj.find('.ftS-tools').removeClass('toolsOpen'); /* Close memberSelect dialog, if open */
                obj.find('.ftS-playerPrompt').html(ftReplaceKeyInString(options.fullSlotPrompt,slotParms));
                obj.find('#memberSelectPrompt').addClass('ftS-warningChange').removeClass('ftS-warningChange',800);
                $(window).trigger('ft-resize');
            }
        },
        
        // Converts position index in player array to the position as the user sees it on the slot page.
        getSlotNumber: function (slotNum, obj) {
            if(obj==null){
                obj = $(this);
            }
            var slotParms = obj.data("ft-slotParms");
            var group = 0;
            if(slotParms.players_per_group > 0){
                group = Math.floor(slotNum / slotParms.players_per_group);
            }
            var numOffset = (slotParms.players_per_group - slotParms.visible_players_per_group) * group;
            return ((slotNum+1) - numOffset);
        },
        
        generatePlayerSlot: function (slotNum, obj) {
            if(obj==null){
                obj = $(this);
            }
            var tag = ftGetTagNames();
            var slotHtml = "";
            var startHtml = "";
            var slotParms = obj.data("ft-slotParms");
            var rowClass = ["slot_player_row"];
            var cells = 3;
            var playerCell = 3;
            
            var useTitles = (slotParms.group_titles && slotParms.group_titles.length);
            
            if ((slotNum > 0) && (slotNum % slotParms.players_per_group == 0)) {
                rowClass.push("ftS-groupStart");
            } else if ((slotNum > 0) && ((slotNum+1) % slotParms.players_per_group == 0)) {
                rowClass.push("ftS-groupEnd");
            } else {
                rowClass.push("ftS-groupChild");
            }
            if(((slotNum % slotParms.players_per_group) + 1) > slotParms.visible_players_per_group){
                rowClass.push("ftS-noDisplay");
            }
            if(slotParms.lock_player_a[slotNum]){
                rowClass.push("ftS-lockedPlayer");
            } else {
                rowClass.push("ftS-unlockedPlayer");
            }
            
            
            slotHtml += "<"+tag.tr+" class=\"rwdTr "+rowClass.join(" ")+"\" id=\"slot_player_row_" + slotNum + "\">";
            slotHtml += "<"+tag.td+" class=\"rwdTd ftS-clearButtonCell\"><a class=\"deleteButtonLarge player_erase_button\" href=\"#\" alt=\"Clear Slot\"><b>Clear</b></a></"+tag.td+">";
            slotHtml += "<"+tag.td+" class=\"rwdTd ftS-playerCountCell\">" + methods.getSlotNumber(slotNum, obj) + "</div>";
            slotHtml += "<"+tag.td+" class=\"rwdTd ftS-playerCell\"><div><div class=\"playerType\"></div><div class=\"playerName\"><input class=\"ftS-playerNameInput\" type=\"text\"></div></div></"+tag.td+">";
            if (slotParms.show_transport == true) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-trasportCell ftS-option\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListTransHead,slotParms)+"</span><select class=\"transport_type\"></select></"+tag.td+">";
                cells++;
            }
            if (slotParms.show_ghin == true) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-ghinCell ftS-option\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListGhinHead,slotParms)+"</span><input type=\"text\" "+((slotParms.lock_ghin)?"disabled=\"disabled\" ":"")+"class=\"slot_ghin\"></"+tag.td+">";
                cells++;
            }
            if (slotParms.show_gender == true) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-genderCell ftS-option\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListGenderHead,slotParms)+"</span><select class=\"slot_gender\"></select></"+tag.td+">";
                cells++;
            }
            if (slotParms.show_fb == true) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-9holeCell ftS-option\"><label onclick=\"\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotList9holeHead,slotParms)+"</span><input class=\"slot_9holes\" type=\"checkbox\"></label></"+tag.td+">";
                cells++;
            }
            if (slotParms.show_check_num == true) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-checkNumCell ftS-option\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListCheckNumHead,slotParms)+"</span><select class=\"slot_check_num\"></select></"+tag.td+">";
                cells++;
            }
            if (slotParms.meal_options && slotParms.meal_options.length) {
                slotHtml += "<"+tag.td+" class=\"rwdTd ftS-mealOptionCell ftS-option\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListMealOptionHead,slotParms)+"</span><select class=\"slot_meal_option\"></select></"+tag.td+">";
                cells++;
            }
            if (slotParms.show_gift_pack == true) {
                cells++;
                if (slotParms.gift_pack_a[slotNum] == 1) {
                    slotHtml += "<"+tag.td+" class=\"rwdTd ftS-giftPackCell ftS-option\"><label onclick=\"\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListGiftPackHead,slotParms)+"</span><input class=\"slot_gift_pack\" type=\"checkbox\" value=\"1\"></label></"+tag.td+">";
                } else {
                    slotHtml += "<"+tag.td+" class=\"rwdTd ftS-giftPackCell ftS-option\"><label onclick=\"\"><span class=\"columnTitle\">"+ftReplaceKeyInString(options.slotListGiftPackHead,slotParms)+"</span><input class=\"slot_gift_pack\" type=\"checkbox\" disabled></label></"+tag.td+">";
                }
            }
            if(slotParms.lock_player_a[slotNum]){
                slotHtml += "<"+tag.td+" class=\"rwdTd sortHandle\"><div class=\"ui-icon ui-icon-locked\"></div></"+tag.td+">"
            } else {
                slotHtml += "<"+tag.td+" class=\"rwdTd sortHandle\"><div class=\"ui-icon ui-icon-arrowthick-2-n-s\"></div></"+tag.td+">"
            }
            
            slotHtml += "</"+tag.tr+">";
                
            if ((slotNum > 0 || useTitles) && (slotNum % slotParms.players_per_group == 0)) {
                startHtml = '<'+tag.tr+' class="rwdTr ftS-groupSeparator'+(useTitles?' ftS-withTitle':'')+'">';
                for(var i = 0; i < cells +1; i++){
                    if(useTitles && i == 2){
                        startHtml += "<"+tag.td+" class=\"rwdTd"+((i+1 == playerCell)?' ftS-playerCell':'')+"\"><div class=\"ftS-groupTitle\">"+slotParms.group_titles[slotNum / slotParms.players_per_group]+"</div></"+tag.td+">";
                    } else {
                        startHtml += "<"+tag.td+" class=\"rwdTd"+((i+1 == playerCell)?' ftS-playerCell':'')+"\"><div>&nbsp;</div></"+tag.td+">";
                    }
                    
                }
                startHtml += "</"+tag.tr+">";
            }
            return startHtml+slotHtml;
        },
        
        generateSlotPage: function (obj) {
            
            if(!obj){
                obj = $(this);
            }
            var tag = ftGetTagNames();
            var slotParms = obj.data("ft-slotParms");

            //
            //  Build page to prompt user for names
            //
            
            if(slotParms.time_remaining > -1){
                options.mainInstructions = options.mainInstructionsTimer;
            }else{
                options.mainInstructions = options.mainInstructionsNoTimer;
            }

            var all_columns = 9;
            var text_columns = 7;
            if (slotParms.show_gift_pack == false) {
                all_columns--;
            }
            if (slotParms.show_transport == false) {
                all_columns--;
                text_columns--;
            }
            if (slotParms.show_fb == false) {
                all_columns--;
            }
            if (slotParms.show_ghin == false) {
                all_columns--;
                text_columns--;
            }
            if (slotParms.show_gender == false) {
                all_columns--;
                text_columns--;
            }
            
            var slotHeadHtml = "";

            slotHeadHtml += "<div class=\"main_instructions\">";
            for(var index in options.mainInstructions){
                slotHeadHtml += '<p>'+ftReplaceKeyInString(options.mainInstructions[index],slotParms)+'</p>';
            }
            slotHeadHtml += "</div>";

            slotHeadHtml += "<div class=\"sub_instructions date_time_course\">"; // Start date/time/course div
            if(slotParms.show_name){
                slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsName,slotParms)+'</span>';
            }
            if(slotParms.reservation_number){
                slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsNumber,slotParms)+'</span>';
            }
            if(!slotParms.season_long){
                if(slotParms.day.length){
                    slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsDayDate,slotParms)+'</span>';
                }else{
                    slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsDate,slotParms)+'</span>';
                }
                if(slotParms.slots > 1){
                    slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsFirstTime,slotParms)+'</span>';
                } else if (slotParms.stime.length) {
                    slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsTime,slotParms)+'</span>';
                }
            }else{
                slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsSeasonLong,slotParms)+'</span>';
            }
            if (!slotParms.course_disp == "") {
                slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsCourse,slotParms)+'</span>';
            } else if (!slotParms.location_disp == "") {
                slotHeadHtml += '<span>'+ftReplaceKeyInString(options.subInstructionsLocation,slotParms)+'</span>';
            }
            slotHeadHtml += "</div>"; // end date/time/course div
            
            for (var i = 0; i < slotParms.slot_header_notes.length; i++) {
                slotHeadHtml += "<div class=\"sub_instructions slot_header_notes\">" + ftReplaceKeyInString(slotParms.slot_header_notes[i],slotParms) + "</div>";
            }
            var slotHeader = $(slotHeadHtml);
            //
            
            for (var i = 0; i < slotParms.slot_footer_notes.length; i++) {
                $(this).after("<div class=\"sub_instructions slot_footer_notes\">" + ftReplaceKeyInString(slotParms.slot_footer_notes[i],slotParms) + "</div>");
            }

            var slotHtml = "";

            slotHtml += "<div class=\"ftS-tools\">"; // start of right column/member select
            slotHtml += '<div class="slotMemberSelect"><div id="memberSelectPrompt"><div class="ftS-playerPrompt"></div><div class=\"ftS-closeButton\"><a class="standard_button" href="#"><b>'+ftReplaceKeyInString(options.buttonCloseMemberSelect,slotParms)+'</b></a></div></div></div>';
            slotHtml += "</div>"; // end of right column

            slotHtml += "<div class=\"ftS-requestWrapper\">"; // start of left column
            slotHtml += "<div class=\"ftS-requestContainer\">"; // start of request container
            slotHtml += "<div class=\"ftS-header\">"; // start header 
            slotHtml += "<h3 class=\"right_header ftS-selectNamesPrompt\"><span>"+ftReplaceKeyInString(options.slotListIntructions,slotParms)+"</span></h3>";
            slotHtml += "<h3 class=\"right_header ftS-selectNamesButton\"><a class=\"ftS-playerPrompt standard_button\" href=\"#\"></a></h3>";
            slotHtml += "<h3 class=\"left_header\"><span>"+ftReplaceKeyInString(options.slotListHeader,slotParms)+"</span></h3>";
            
            slotHtml += "</div>"; // end header
            // Build player request table
            slotHtml += "<div class=\"ftS-slots\">";  // Start slot list element group
            slotHtml += "<form>";
            slotHtml += "<"+tag.table+" class=\"rwdTable ftS-slotTable\">";
            
            /*
            // Full mode debug
            slotParms.show_transport = true
            slotParms.show_ghin = true
            slotParms.show_gender = true
            slotParms.show_fb = true
            slotParms.show_gift_pack = true
            slotParms.show_force_singles_match = true
            slotParms.show_time_picker = true
            slotParms.show_recur = true
            slotParms.show_transport = true
            */
            
            // Build slot column header
            slotHtml += "<"+tag.thead+" class=\"rwdThead\"><"+tag.tr+" class=\"rwdTr\"><"+tag.th+" class=\"rwdTh\"></"+tag.th+"><"+tag.th+" class=\"rwdTh\"></"+tag.th+">";
            slotHtml += "<"+tag.th+" class=\"rwdTh\"><span>"+ftReplaceKeyInString(options.slotListPlayerHead,slotParms)+"</span></"+tag.th+">";
            if (slotParms.show_transport == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh\"><span>"+ftReplaceKeyInString(options.slotListTransHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.show_ghin == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh\"><span>"+ftReplaceKeyInString(options.slotListGhinHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.show_gender == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh\"><span>"+ftReplaceKeyInString(options.slotListGenderHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.show_fb == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh ftS-headOpt ftS-head9hole\"><span>"+ftReplaceKeyInString(options.slotList9holeHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.show_check_num == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh ftS-headOpt ftS-headCheckNum\"><span>"+ftReplaceKeyInString(options.slotListCheckNumHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.meal_options && slotParms.meal_options.length) {
                slotHtml += "<"+tag.th+" class=\"rwdTh ftS-headOpt ftS-headMealOption\"><span>"+ftReplaceKeyInString(options.slotListMealOptionHead,slotParms)+"</span></"+tag.th+">";
            }
            if (slotParms.show_gift_pack == true) {
                slotHtml += "<"+tag.th+" class=\"rwdTh ftS-headOpt ftS-headGiftPack\"><span>"+ftReplaceKeyInString(options.slotListGiftPackHead,slotParms)+"</span></"+tag.th+">";
            }
            slotHtml += "</"+tag.tr+"></"+tag.thead+">";
            
            // Build initial slots
            slotHtml += "<"+tag.tbody+" class=\"rwdTbody ftS-playerSlots\">";
            /*
            Slots will be built later
            */
            slotHtml += "</"+tag.tbody+">";
            slotHtml += "</"+tag.table+">"; // End slot table
            
            // Add extra options
            
            var slotOptions = "";
            
            if(slotParms.add_players){
                slotOptions += '<div class="ftS-addPlayers hide"><button class="standard_button">'+ ftReplaceKeyInString(options.addPlayerButton,slotParms) + '</button></div>';
            }
            
            if (slotParms.show_force_singles_match) {  // Show singles match selection
                slotOptions += "<fieldset class=\"ftS-forceSingles ftS-optionBlock\">"
                + '<legend>'+ftReplaceKeyInString(options.forceSinglesMatch,slotParms)+'</legend>'
                + "<label onclick=\"\">"
                + "<select name=\"force_singles\"></select></label>";
                slotOptions += "</fieldset>";
            }
            
            if (slotParms.show_time_picker) {  // Show wait list time picker
                slotOptions += "<fieldset class=\"ftS-timePicker ftS-optionBlock\">"
                + '<legend>'+ftReplaceKeyInString(options.timePickerInstruct,slotParms)+'</legend>'
                + '<div class="ftS-timePickerFrom"><span>'+ftReplaceKeyInString(options.timePickerFrom,slotParms)+"</span>"
                + "<span><select name=\"start_hr\"></select></span>"
                + "<b>:</b>"
                + '<span><input type="text" name="start_min"></span>'
                + '<b></b>'
                + "<span><select name=\"start_ampm\"></select></span></div>";
            
                slotOptions += '<div class="ftS-timePickerTo"><span>'+ftReplaceKeyInString(options.timePickerTo,slotParms)+"</span>"
                + "<span><select name=\"end_hr\"></select></span>"
                + "<b>:</b>"
                + '<span><input type="text" name="end_min"></span>'
                + '<b></b>'
                + "<span><select name=\"end_ampm\"></select></span></div>";
                slotOptions += "</fieldset>";
            }
            
            if (!slotParms.hide_notes) {  // if proshop wants to hide the notes, do not display the text box or notes
               
                slotOptions += "<fieldset class=\"ftS-notes ftS-optionBlock\">"
                + '<legend>'+ftReplaceKeyInString(slotParms.notes_prompt,slotParms)
                +((slotParms.protect_notes)?'':"<span><a class=\"deleteButtonSmall notes_erase_button\" href=\"#\"><b>"
                + ftReplaceKeyInString(options.slotListErase,slotParms)+'</b></a></span>')+'</legend>';
       
                slotOptions += "<div><textarea"+((slotParms.protect_notes)?' disabled':'')+" class=\"pro_notes\"></textarea></div>";
                
                slotOptions += "</fieldset>";
            }
            
            if (slotParms.show_recur) {  // Display recur checkbox ? (for recurring lottery requests - possibly tee times)
               
                slotOptions += '<fieldset class="ftS-recur ftS-optionBlock">'
                    + '<legend>'+ftReplaceKeyInString(options.legendRecur,slotParms)+'</legend>'
                
                    + '<div class="recurType">'
                    + '<label onclick=""><input name="recur_type" value="0" type="radio">'
                    + ftReplaceKeyInString(options.labelRecurType.none,slotParms)+'</label>'
                    + '<label onclick=""><input name="recur_type" value="1" type="radio">'
                    + ftReplaceKeyInString(options.labelRecurType.weekly,slotParms)+'</label>'
                    + '<label onclick=""><input name="recur_type" value="2" type="radio">'
                    + ftReplaceKeyInString(options.labelRecurType.everyOther,slotParms)+'</label>'
                    + '</div>'                
                    + '<div class="recurLength" style="display:none;">'                  
                    + '<label onclick="">'+ftReplaceKeyInString(options.labelRecurLength.until,slotParms)
                    // set default date above
                    + '<input name="recur_end" type="text" value="" class="ft_date_picker_field" data-ftdefaultdate="true" data-ftstartdate="'+slotParms.recur_start+'" data-ftenddate="'+slotParms.recur_end+'">'
                    + '</label>'
                    + '</div>'

                    + '</fieldset>';
            }
            
            // End slot options
            if(slotOptions.length){
                slotHtml += '<div class="ftS-slotOptions">'+slotOptions+'</div>';
            }

            if (slotParms.show_transport == true) {  // Display tmode legend
                slotHtml += "<div class=\"ftS-tmodeLegend\">";
                slotHtml += "<div><span>" + slotParms.transport_legend + "</span></div>";
                slotHtml += "</div>";
            }

            slotHtml += "</form>";
            slotHtml += "</div>";  // 
            
            slotHtml += "<div class=\"button_container\">";
            slotHtml += "<a href=\"#\" class=\"go_back_button\">"+ftReplaceKeyInString(options.buttonGoBack,slotParms)+"</a>";
            if (slotParms.allow_cancel == true  && slotParms.show_contact_to_cancel == false) {
                slotHtml += "<a href=\"#\" class=\"cancel_request_button\">"+ftReplaceKeyInString(options.buttonCancel,slotParms)+"</a>";
            }
            slotHtml += "<a href=\"#\" class=\"submit_request_button\">"+ftReplaceKeyInString(options.buttonSubmit,slotParms)+"</a>";
            
            if(slotParms.show_contact_to_cancel){
                slotHtml += "<p>"+ftReplaceKeyInString(options.contactToCancel,slotParms)+"</p>";
            }

            //slotHtml += "<a class=\"help_link\" href=\"javascript:void(0);\" onClick=\"window.open ('" + ftSetJsid(slotParms.slot_help_url) + "', 'newwindow', config='Height=500, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">"
            //+ ftReplaceKeyInString(options.buttonHelp,slotParms)
            //+ "</a>";
            //slotHtml += "</div>";
            slotHtml += "</div>";
            slotHtml += "</div>"; // end request container
            slotHtml += "</div>"; // end of left column
            
            var sObj = $('<div class="ftS-slot">'+slotHtml+'</div>');
            
            obj.before(slotHeader);
            obj.append(sObj);
            
            slotParms.rebuildSlots = true;
            
            ftActivateElements(obj); // Activate common dynamic form elements, like date-pickers, help buttons, etc.
            ftActivateElements(slotHeader);

        },
       
        setOptions: function ( option ) {
            
            if(typeof option == "object"){
                //// console.log("setting options");
                $.extend(true, options,option);
            }
            
        },
       
        getOptions: function () {
            
            return options;
            
        }
        
    };
    
    $.fn.foreTeesSlot = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesSlot' );
        }     
    }
    
})(jQuery);
