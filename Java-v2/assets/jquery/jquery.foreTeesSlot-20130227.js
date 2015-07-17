/*************************************************************
 *
 * foreTeesSlot Plugin
 * 
 * Used by member slot pages
 * 
 *
 *       2/27/13  showPartners will now include a style parameter in the option elements it prints.
 *      12/25/12  Added massConsecutiveTimeTitle, massConsecutiveTimeSuccess, massConsecutiveTimeNotice to notify
 *      12/03/12  Add aria-haspopup="true" to the Add Recipients button for IE10 touch compatability
 *       8/03/12  Fix event tmode options
 *       5/24/12  Added options.notify.timesOccupiedTitle and options.notify.timesOccupiedNotice strings.
 *  
 *************************************************************/


/*************************************************************
 *
 * foreTeesEmail Plugin
 *
 * Used by the Member's Email Tool page
 *  
 *************************************************************/
(function($){
    
    var pluginData = {
        
        jsonRunnin:false,
        jsonData:{}

    };
    
    var options = {
        nameListHeader:'Name List',
        nameListInstructions:'Click on name to add',
        mainInstructions:'<h3>Instructions:</h3>'
        + '<p>Select recipents that will receive this email, enter the subject and message you would like to send, then click "[options.sendEmailButton]".</p>'
        + '<p><b>Note:</b> Members without an email addresses registered with the system will not be available as recipients.</p>'
        + '<p>The maximum number of recipients is [options.maxReciptients].</p>',
        distListInstructions:'<h3>Instructions:</h3>'
        + '<p>Click the approriate icon to edit or delete existing distribution lists.  To add a new list, select "[options.addDistributionList]"</p>',
        maxReciptients: 100,
        emailError:'Error Sending Email',
        tooManyRecipients:'Sorry, the maximum number of recipients allowed is [maxReciptients].',
        noRecipient:'You must add at least one recipient.',
        noSubject:'You must enter a subject.',
        noMessage:'You must enter a message.',
        successTitle:'Your Message Has Been Sent',
        successful:'<h3>Thank you!</h3><p>Your email message has now been queued for delivery. Your recipients should receive their email message soon.</p>',
        cancelButton:'Cancel',
        continueButton:'Continue',
        recipentTitle:'Recipients:',
        subjectTitle:'Subject:',
        messageTitle:'Message:',
        addRecipients:'Add Recipients',
        addMembers:'Find and add Members',
        addDistributionList:'Add Distribution List',
        addPartners:'Add Partners',
        sendEmailButton:'Send Email',
        endUrl:'Member_announce',  // Where to go after we are done
        recipientAddColor:"#00FF00",  // Color to "flash" the slot field of the newly added player
        recipientAddDuration:1000,  // how long to flash the field for, in ms.
        recipientDuplicateColor:"#FFFF00", // Color to flash the slot field of a player that is already on the list
        recipientDuplicateDuration:1000  // how long to flash the field for, in ms.
    };
    
        
    var methods = {
        
        init: function(option){
        
            $(this).each(function(){
                var obj = $(this);
                var parms = obj.data("ft-emailParms");
                if(typeof(parms) == "undefined"){
                    parms = {};
                    obj.data("ft-emailParms",parms);
                    parms.options = options;
                }
                if(typeof(option) == "undefined"){
                    option = {
                        mode:"email"
                    }
                }
                switch (option.mode) {
                    default:
                    case "email":
                        obj.foreTeesEmail("generateEmailForm");
                        obj.foreTeesEmail("initRecepientList");
                        obj.foreTeesEmail("initButtons");
                        break;
                    case "distributionList":
                        obj.foreTeesEmail("generateDistributionListForm");
                }

            });
        },
        
                
        displayMemberList: function(){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            obj.find("select.partner_list").each(function(){
                $(this).scrollTop(0); // reset list position to top
                $(this).find("option").remove(); // clear the list
                for(var key in parms.partner_list){ // Build the list
                    var name = parms.partner_list[key]["name"];
                    var option = $('<option value="'+parms.partner_list[key]["display"]+'">'+name+'</option>');
                    parms.partner_list[key]["parentObj"] = obj;
                    option.data("ft-partnerData", parms.partner_list[key]);
                    $(this).append(option);
                    $(this).scrollTop(0); 
                }
                $(this).find("option:selected").removeAttr("selected");
                
                $(this).foreTeesListSearch("clear");
                $(this).foreTeesListSearch("init", {
                    name: ftReplaceKeyInString(options.nameListHeader,parms),
                    instructions: ftReplaceKeyInString(options.nameListInstructions,parms),
                    slotParms: parms,
                    change: function(){
                        // name has been clicked
                        $(this).find("option:selected").each(function(){
                            if(typeof($(this).data("ft-partnerData")) != "undefined"){
                                var partnerData = $(this).data("ft-partnerData");
                                var obj = partnerData.parentObj;
                                obj.foreTeesEmail("addRecipient", partnerData);
                            }
                        });
                    }
                });
            });
            if(typeof(parms.firstLoad) == "function"){
                parms.firstLoad(obj);
                delete parms.firstLoad;
            }
        },
        
        loadMemberList: function(letter){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            parms.last_name_list_type = letter;
            var url = "data_loader?json_mode=true&email&name_list=true&letter="+letter;
            // // console.log(url);
            
            // Block user input
            obj.foreTeesModal("pleaseWait");
            // Update list of partners
            $.ajax({  
                url: url,
                context:obj,
                dataType: 'json',
                success: function(data){
                    //// console.log("got partner list");
                    var obj = $(this);
                    var parms = obj.data("ft-emailParms");
                    parms.partner_list = data;
                    obj.foreTeesEmail("displayMemberList");
                    obj.foreTeesModal("pleaseWait", "close");
                            
                },
                error: function(xhr){
                    ft_logError("Error loading member list", xhr);
                    // console.log("error recv. partner list");
                    obj.foreTeesModal("pleaseWait", "close");
                    obj.foreTeesModal("ajaxError", {
                        context:this,
                        errorThrown:"Unable to load data",
                        tryAgain:function(option){
                            var parms = option.context.data("ft-emailParms");
                            $(option.context).foreTeesEmail("loadMemberList",parms.last_name_list_type);
                        },
                        allowRefresh: true
                    });      
                }
            });
            
        },
        
        addRecipients: function(recipients){
            var obj = $(this);
            for(var key in recipients){ // Add partner list
                obj.foreTeesEmail("addRecipient", recipients[key]);
            }
        },
        
        addRecipient: function(recipient){
            $(this).each(function(){
                var obj = $(this);
                var parms = obj.data("ft-emailParms");
                
                if(typeof(parms.recipients[recipient.username]) == "undefined"){
                    parms.recipients[recipient.username] = recipient;
                    var recipient_container = $('<div><p><a href="#" class="email_recipient" title="Remove '+recipient.display+' from recipient list">&nbsp;</a>'+recipient.display+'</p></div>');
                    parms.recipients[recipient.username].conatiner = recipient_container;
                    var recipient_button = recipient_container.find(".email_recipient");
                    recipient_button.data("ft-reciptientData",recipient);
                    recipient_container.data("ft-reciptientData",recipient);
                    recipient_button.data("ft-emailObj",obj);
                    // Find where we should be adding this recipient (alpha sort)
                    var addBefore = parms.addRecipientsObj;
                    parms.addRecipientsObj.prevAll().each(function(){
                        var recipient_data = $(this).data("ft-reciptientData");
                        if(typeof(recipient_data) == "object" && recipient.name < recipient_data.name){
                            addBefore = $(this);
                        }
                    });
                    
                    addBefore.before(recipient_container);
                    recipient_button.click(function(){
                        var obj = $(this).data("ft-emailObj");
                        var parms = obj.data("ft-emailParms");
                        var recipient = $(this).data("ft-reciptientData");
                        if(typeof(parms.recipients[recipient.username]) != "undefined"){
                            delete parms.recipients[recipient.username];
                            $(this).parents("div").first().remove();
                        }
                        return false;
                    });
                    recipient_container.find("p").first().foreTeesHighlight(options.recipientAddColor, options.recipientAddDuration); 
                } else {
                    var recipient_container = parms.recipients[recipient.username].conatiner;
                    recipient_container.find("p").first().foreTeesHighlight(options.recipientDuplicateColor, options.recipientDuplicateDuration); 
                    
                } 
            });
        },
        
        initRecepientList: function(){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            var listContainer = obj.find(".reciptient_container");
            parms.recipients = {};
            parms.addRecipientsObj = $('<div><ul><li aria-haspopup="true"><a href="#">'+options.addRecipients+'</a>'
                +'<ul>'
                +'<li><a href="#" class="select_members">'+options.addMembers+'</a></li>'
                +'<li><a href="#" class="select_distribution_list">'+options.addDistributionList+'</a></li>'
                +'<li><a href="#" class="add_partners">'+options.addPartners+'</a></li>'
                +'</ul>'
                +'</li></ul></div>');
            listContainer.append(parms.addRecipientsObj);
            // Disable clicking on menu tab
            listContainer.find("ul a").first().click(function(){
                return false; 
            });
            listContainer.find("ul a.select_members").each(function(){
                $(this).data("ft-emailObj", obj);
                $(this).click(function(){
                    var obj = $(this).data("ft-emailObj");
                    var parms = obj.data("ft-emailParms");
                    obj.foreTeesModal("getMemberRecipients");
                    return false; 
                })
            });
            listContainer.find("ul a.select_distribution_list").each(function(){
                $(this).data("ft-emailObj", obj);
                $(this).click(function(){
                    var obj = $(this).data("ft-emailObj");
                    var parms = obj.data("ft-emailParms");
                    obj.foreTeesModal("getMemberDistributionList");
                    return false; 
                })
            });
            listContainer.find("ul a.add_partners").each(function(){
                $(this).data("ft-emailObj", obj);
                $(this).click(function(){
                    var obj = $(this).data("ft-emailObj");
                    var parms = obj.data("ft-emailParms");
                    var listContainer = obj.find(".reciptient_container");
                    var url = "data_loader?json_mode=true&email&name_list=true&letter=partners";
                    // // console.log(url);
            
                    // Block user input
                    obj.foreTeesModal("pleaseWait");
                    // Update list of partners
                    $.ajax({
                        url: url,
                        context:$(this),
                        dataType: 'json',
                        success: function(data){
                            var obj = $(this).data("ft-emailObj");
                            //// console.log("got partner list");
                            //var parms = obj.data("ft-emailParms");
                            obj.foreTeesModal("pleaseWait", "close");
                            obj.foreTeesEmail("addRecipients", data);
                            
                        },
                        error: function(xhr){
                            // console.log("error recv. partner list");
                            ft_logError("Unable to load recipient list", xhr);
                            obj.foreTeesModal("ajaxError", {
                                context:this,
                                errorThrown:"Unable to load data",
                                tryAgain:function(option){
                                    $(option.context).click();
                                },
                                allowRefresh: true
                            });
                        }
                    });
                    return false; 
                })
            });
            
            
        },
        
        sendEmail: function (){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
                    
            var subject = $.trim(obj.find('input[name="subject"]').val());
            var message = $.trim(obj.find('textarea[name="message"]').val());
                    
            if(ftCount(parms.recipients) < 1){
                obj.foreTeesModal("alertNotice", {
                    title:options.emailError,
                    message_head: options.noRecipient
                });
            } else if(subject.length < 1){
                obj.foreTeesModal("alertNotice", {
                    title:options.emailError,
                    message_head: options.noSubject
                });
            } else if(message.length < 1){
                obj.foreTeesModal("alertNotice", {
                    title:options.emailError,
                    message_head: options.noMessage
                });
            } else {
                // Submit email
                var url = 'Member_email?send_email';
                var names = [];
                //console.log(JSON.stringify(parms.recipients));
                for(var key in parms.recipients){ // Create username array
                    names.push(parms.recipients[key].username);
                }
                var form_data = {
                    recipients:JSON.stringify(names),
                    subject:subject,
                    message:message
                };
                obj.foreTeesModal("pleaseWait");
                $.ajax({
                    url: url,
                    context:obj,
                    dataType: 'json',
                    data: form_data,
                    type: 'POST',
                    success: function(data){
                        var obj = $(this);
                        //// console.log("got partner list");
                        var parms = obj.data("ft-emailParms");
                        obj.foreTeesModal("pleaseWait", "close");
                                
                        if(!data.successful){
                            obj.foreTeesModal("alertNotice", {
                                title:options.emailError,
                                message_head: ftReplaceKeyInString(data.message, options)
                            });
                        } else {
                            obj.foreTeesModal("alertNotice", {
                                alertMode: false,
                                title:options.successTitle,
                                message: ftReplaceKeyInString(data.message, options),
                                continueAction:function(modalObj){
                                    var obj = modalObj.data("ft-modalParent");
                                    obj.foreTeesEmail("exitEmail");
                                },
                                allowClose: false,
                                allowContinue: true
                                        
                            });
                        }
                            
                    },
                    error: function(xhr){
                        // console.log("error recv. partner list");
                        ft_logError("Unable to send email", xhr);
                        var obj = $(this);
                        obj.foreTeesModal("ajaxError", {
                            context:obj,
                            errorThrown:"Unable to send email",
                            tryAgain:function(option){
                                option.context.foreTeesEmail("sendEmail");
                            },
                            allowRefresh: true
                        });
                            
                    }
                });
            }       
        },
        
        exitEmail: function(){
            var obj = $(this);
            obj.foreTeesModal("pleaseWait");
            window.location = options.endUrl;
        },
        
        initButtons: function(){
            
            var obj = $(this);
           
            obj.find(".submit_request_button").each(function(){
                $(this).data("ft-emailObj", obj);
                $(this).click(function(){
                    var obj = $(this).data("ft-emailObj");
                    obj.foreTeesEmail("sendEmail");
                    return false;
                }); 
            });
            
            obj.find(".go_back_button").each(function(){
                $(this).data("ft-emailObj", obj);
                $(this).click(function(){
                    obj.foreTeesEmail("exitEmail");
                });
                
                return false;
            });

        },
        
        generateEmailForm: function(){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            
            var headHtml = "";

            headHtml += '<div class="main_instructions\">';
            headHtml += ftReplaceKeyInString(options.mainInstructions,parms);
            headHtml += "</div>";
            
            $(this).before(headHtml);
            var html = "";
            html += '<fieldset class="standard_fieldset"><legend>'+options.recipentTitle+'</legend>';
            html += '<div class="reciptient_container"></div></fieldset>';
            html += '<fieldset class="standard_fieldset"><legend>'+options.subjectTitle+'</legend>';
            html += '<input type="text" name="subject"></fieldset>';
            html += '<fieldset class="standard_fieldset"><legend>'+options.messageTitle+'</legend>';
            html += '<textarea name="message"></textarea></fieldset>';
            
            html += '<div class="button_container"><a class="go_back_button" href="#">'+options.cancelButton+'</a><a class="submit_request_button" href="#">'+options.sendEmailButton+'</a></div>';
            
            $(this).addClass("sub_main_tan");
            $(this).append(html);

        },
        
        deleteDistributionList: function(option){
        
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            
            var data = {
                del_dist_list:option.listName
            }
                
            var url = "data_loader?json_mode=true&email"; 
            
            obj.foreTeesModal("pleaseWait");
            
            $.ajax({  
                url: url,
                context:option,
                dataType: 'json',
                data: data,
                type: "post",
                success: function(data){
                    //// console.log("got list");
                    option = this;
                    obj.foreTeesModal("pleaseWait", "close");
                    if(typeof(option.onComplete) == "function"){
                        option.onComplete(option);
                    }
                },
                error: function(xhr){
                    option = this;
                    ft_logError("Enable to delete distribution list", xhr);
                    obj.foreTeesModal("pleaseWait", "close");
                    if(typeof(option.onComplete) == "function"){
                        option.onComplete(option);
                    }
                }
            });
        
        },
        
        saveDistributionList: function(){
        
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            var names = [];
            var data = {};
            for(var key in parms.recipients){
                names.push(parms.recipients[key].username);
            }
            data.save_dist_list = parms.listOptions.listName;
            data.names = JSON.stringify(names);
            var newListName = data.save_dist_list;
            obj.find('input[name="distribution_list_name"]').each(function(){
                newListName = $(this).val();
            });
            
            if(newListName.length < 1){
                obj.foreTeesModal("alertNotice", {
                    title:"Data entry error:",
                    message_head: "You must enter a name for this distribution list."
                });
                return;
            } else if (newListName != data.save_dist_list && !parms.listOptions.newListMode){
                data.new_list_name = newListName;
            } else if (parms.listOptions.newListMode){
                data.new_list = true;
                data.save_dist_list = newListName;
            }
                
            var url = "data_loader?json_mode=true&email"; 
            
            obj.foreTeesModal("pleaseWait");
            
            $.ajax({  
                url: url,
                context:obj,
                dataType: 'json',
                data: data,
                type: "post",
                success: function(data){
                    //// console.log("got list");
                    var obj = $(this);
                    var parms = obj.data("ft-emailParms");
                    obj.foreTeesModal("pleaseWait", "close");
                    if(data.success){
                        if(typeof(parms.listOptions) == "object" && typeof(parms.listOptions.onSaveComplete) == "function"){
                            parms.listOptions.onSaveComplete(obj);
                        }
                    } else {
                        obj.foreTeesModal("alertNotice", {
                            title:"Error Saving Distribution List",
                            message_head: ftReplaceKeyInString(data.message, parms)
                        });
                    }
                },
                error: function(xhr){
                    var obj = $(this);
                    ft_logError("Unable to save distrubution list", xhr);
                    obj.foreTeesModal("ajaxError", {
                        context:obj,
                        errorThrown:'Unable to save distribution list.',
                        tryAgain:function(option){
                            option.context.foreTeesEmail("saveDistributionList");
                        },
                        allowRefresh: true
                    });
                }
            });
        
        },
        
        getNewDistributionListName: function(){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            
            var listNameBase = "New List";
            var listNumber = 0;
            var listName = listNameBase;
            var foundName = false;
            
            while(!foundName){
                if($.inArray(listName,parms.distribution_lists) > -1){
                    listNumber ++;
                    listName = listNameBase + " " + listNumber;
                } else {
                    foundName = true;
                }
            }
            
            return listName;

        },
        
        generateDistributionListForm: function(){
            
            var obj = $(this);
            var parms = obj.data("ft-emailParms");
            
            if(!parms.initialized){
                parms.initialized = true;
                var headHtml = "";

                headHtml += '<div class="main_instructions\">';
                headHtml += ftReplaceKeyInString(options.distListInstructions,parms);
                headHtml += "</div>";
            
                obj.before(headHtml);
            }
  
            var url = "data_loader?json_mode=true&email&dist_list_names";
            
            obj.foreTeesModal("pleaseWait");
                
            $.ajax({  
                url: url,
                context:obj,
                dataType: 'json',
                success: function(data){
                    //// console.log("got list");
                    var obj = $(this);
                    var parms = obj.data("ft-emailParms");
                    parms.distribution_lists = data;
                    obj.foreTeesEmail("displayMemberList");
                    obj.foreTeesModal("pleaseWait", "close");

                    var html = "";
                    var htmlObj = obj.find(".standard_fieldset.distribution_lists");
                    if(!htmlObj.length){
                        html += '<fieldset class="standard_fieldset distribution_lists"><legend>Distribution Lists:</legend>';
                        html += '<div class="list_container"></div></fieldset>';
                        htmlObj = $(html);
                        obj.append(htmlObj);
                    }
                    
                    var listContainer = htmlObj.find(".list_container");
                    listContainer.empty();
                    for(var i=0; i < data.length; i++){
                        var listObj = $('<div><p><a href="#" class="button_delete_icon" title="Delete &quot;'+data[i]+'&quot;">&nbsp;</a><a href="#" class="button_edit_icon" title="Edit &quot;'+data[i]+'&quot;">&nbsp;</a><label>'+data[i]+'</label></p></div>');
                        listObj.find("a.button_edit_icon").each(function(){
                            $(this).data("ft-linkData", {
                                obj:obj, 
                                listName:data[i]
                            });
                            $(this).click(function(event){
                                var linkData = $(this).data("ft-linkData");
                                linkData.obj.foreTeesModal("getMemberRecipients", {
                                    listName:linkData.listName,
                                    title:'Edit Distribution List: [listName]',
                                    addButton:'Save Changes',
                                    callbackAction: function(){
                                        $(this).foreTeesEmail("saveDistributionList");
                                    },
                                    onSaveComplete: function(modalObj){
                                        var obj = modalObj.data("ft-modalParent");
                                        modalObj.dialog("close");
                                        obj.foreTeesEmail("generateDistributionListForm");
                                    }
                                });
                                return false;
                            });
                        });
                        listObj.find("a.button_delete_icon").each(function(){
                            $(this).data("ft-linkData", {
                                obj:obj, 
                                listName:data[i]
                            });
                            $(this).click(function(event){
                                var linkData = $(this).data("ft-linkData");
                                linkData.obj.foreTeesModal("alertNotice", {
                                    title:"Delete List Confirmation",
                                    message_head: 'Are you sure you want to permanently remove list &quot'+linkData.listName+'&quot?',
                                    closeButton:"No, Cancel",
                                    continueButton: 'Yes, Delete List',
                                    allowContinue: true,
                                    listName: linkData.listName,
                                    continueAction: function(modalObj, option){
                                        var obj = modalObj.data("ft-modalParent");
                                        obj.foreTeesEmail("deleteDistributionList",{
                                            listName: option.listName,
                                            context: modalObj,
                                            onComplete: function(option){
                                                obj = option.context.data("ft-modalParent");
                                                option.context.dialog("close");
                                                obj.foreTeesEmail("generateDistributionListForm");
                                            }
                                        });
                                    }
                                });
                                return false;
                            });
                        });
                        listContainer.append(listObj);
                    }
                    var addListObj = $('<div><a class="button_add_icon" href="#">'+options.addDistributionList+'</a></div>');
                    addListObj.data("ft-linkData", {
                        obj:obj
                    });
                    addListObj.click(function(event){
                        // Add new distibution list modal
                        var linkData = $(this).data("ft-linkData");
                        linkData.obj.foreTeesModal("getMemberRecipients", {
                            listName:linkData.obj.foreTeesEmail("getNewDistributionListName"),
                            title:'New Distribution List',
                            addButton:'Save List',
                            newListMode: true,
                            callbackAction: function(){
                                $(this).foreTeesEmail("saveDistributionList");
                            },
                            onSaveComplete: function(modalObj){
                                var obj = modalObj.data("ft-modalParent");
                                modalObj.dialog("close");
                                obj.foreTeesEmail("generateDistributionListForm");
                            }
                        });
                        return false;
                    });
                    listContainer.append(addListObj);
                            
                },
                error: function(xhr){
                    ft_logError("Unable to load distribution list names", xhr);
                // console.log("error recv. partner list");
                // 
                }
            });
        },

        setOptions: function ( option ) {

            if(typeof(option) == "object"){
                //console.log("setting options");
                $.extend(true, options,option);
            }

        },

        getOptions: function () {

            return options;

        }
    };
    

    $.fn.foreTeesEmail = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof(method) === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesEmail' );
        }     
    }
    
})(jQuery);

/*************************************************************
 *
 * foreTeesSlot Plugin
 * 
 * Used by new slot pages
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
            playerInfoTitle:"Additional Player Information",
            playerQuestionsOpen:"<table class=\"question_list\"><thead><tr><th>Questions:</th></tr></thead><tbody>",
            playerQuestionsClose:"</tbody></table>",
            question1:"<tr><td><b>1.</b> [this_callback.other_questions_a.0]</td></tr>",
            question2:"<tr><td><b>2.</b> [this_callback.other_questions_a.1]</td></tr>",
            question3:"<tr><td><b>3.</b> [this_callback.other_questions_a.2]</td></tr>",
            missingQuestion1:' is missing an answer to "[this_callback.other_questions_a.0]"',
            missingQuestion2:' is missing an answer to "[this_callback.other_questions_a.1]"',
            missingQuestion3:' is missing an answer to "[this_callback.other_questions_a.2]"',
            homeclubPrompt:"Home Club, State",
            phonePrompt:"Phone",
            addressPrompt:"Mailing Address",
            emailPrompt:"Email",
            shoePrompt:"Shoe Size",
            shirtPrompt:"Shirt Size",
            playerPrompt:"Players",
            guestPrompt:"Guests",
            //q1Prompt:"Question #1",
            //q2Prompt:"Question #2",
            //q3Prompt:"Question #3"
            q1Prompt:'[this_callback.other_questions_a.0]',
            q2Prompt:'[this_callback.other_questions_a.1]',
            q3Prompt:'[this_callback.other_questions_a.2]'
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
            consecutiveTimePromptNotAvailible: "The requested length of time was not available.",
            consecutiveTimePromptIntructions: "Please select a length for this reservation from the options below."
            + "<br><br><b>Note</b>: Only available time options are selectable.",
            consecutiveTimePromptIntructions2: "One or more of the time slots you requested is currently busy or otherwise unavailable."
            + "<br><br>The time we did find for you is as follows:", 
            consecutiveTimePromptButtonListOpen: '<div class="slot_button_list"><label>Length:</label>',
            consecutiveTimePromptButtonListClose: '</div>',
            massConsecutiveTimeTitle: "Consecutive Time Request",
            massConsecutiveTimeSuccess: "Thank you! Your reservation has been accepted and processed!", 
            massConsecutiveTimeNotice: "Sorry, but an issue was encountered while booking your conecutive tee times. Please try again. If the problem persists, please contact the golf shop staff at the club for assistance.", 
            "----":"----" // keep Netbeans auto-format from messing up the last line
            
        },
        
        lottery: {
            
            detailsTitle:"[slot_type] Registration",
            date:"Date: <b>[day] [mm]/[dd]/[yy]</b>",
            dateCourse:"Date: <b>[day] [mm]/[dd]/[yy]</b> Course: <b>[course]</b>",
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
        'Add players to the group(s) and click on "[options.buttonSubmit]" to complete the request.',
        'Time remaining to complete this reservation: <b class="slot_timer">00:00</b>'
        ],
        mainInstructionsNoTimer:[
        'Add players to the group(s) and click on "[options.buttonSubmit]" to complete the request.'
        ],
        // mainInstructions:'', // Generated dynamicaly from mainInstructionsTimer/NoTimer depending on parameters passed
        subInstructionsName:"[slot_type]:<b>[name]</b>",
        subInstructionsDate:"Date:<b>[mm]/[dd]/[yy]</b>",
        subInstructionsTime:"Time:<b>[stime]</b>",
        subInstructionsDayDate:"Date:<b>[day]&nbsp;&nbsp;[mm]/[dd]/[yy]</b>",
        subInstructionsFirstTime:"First Time Requested:<b>[stime]</b>",
        subInstructionsCourse:"Course:<b>[course_disp]</b>",
        subInstructionsSeasonLong:'<b>Season Long</b>',
        buttonGoBack:'Go Back',
        buttonSubmitNew:'Submit Request',
        buttonSubmitEdit:'Submit Changes',
        buttonSubmitMoreNew:'Continue Request',
        buttonSubmitMoreEdit:'Continue Changes',
        // buttonSubmit:'', // Generated dynamicaly from buttonSubmitNew or buttonSubmitEdit depending on parameters passed
        buttonCancel:'Cancel [signup_type]',
        buttonHelp:'Click for Help',
        slotListHeader:'Add or Remove Players',
        slotListIntructions:'Note: Click on Names <b>&rarr;</b>',
        slotListErase:'<span>erase</span>',
        slotListPlayerHead:'Players',
        slotListTransHead:'Trans',
        slotList9holeHead:'9-holes',
        slotListGiftPackHead:'[gift_pack_text]',
        slotListGhinHead:'[ghin_text]',
        slotListGenderHead:'Gender',
        timePickerFrom:'From:',
        timePickerTo:'To:',
        nameListHeader:'Name List',
        nameListInstructions:'Click on name to add',
        memberListHeader:'Member List',
        memberTbdHeader:'[member_tbd_text] TBD',
        memberTbdInstructions:'Use "X" to reserve a position for a [member_tbd_text].',
        guestTypeInstructions:'** Add guests immediately <b>after</b> host member.',
        disabledGuestTypeColor:"#FF0000", // Color for disabled guest type options
        disabledGuestTypeMobileText:" (disabled)", // Message to append to disabled guest type options, if a mobile user
        disabledGuestMessage:'Guest types in <b>red</b> are not allowed for this time.', // Message to disable before list, if there are disabled guest types
        disabledGuestMessageMobile:'Guest types marked <b>disabled</b> are not allowed for this time.', // Message to disable before list for mobile users, if there are disabled guest types
        labelNotesToPro:'Notes to pro:',
        labelContactNotes:'Contact Info / Notes:',
        forceSinglesMatch:'Force Singles Match:',
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
                    
                    // Bind to unload, freeing tee time if page is navigated away from
                    $(window).unload($.proxy(function(){
                        //console.log("winow-unload");
                        var obj = $(this);
                        obj.foreTeesSlot("leaveForm", true);
                    },obj));
                    // Bind onbeforeunload prompt
                    window.ftSlotObject = obj;
                    window.onbeforeunload = function(){
                        //console.log("onbefore");
                        var obj = window.ftSlotObject;
                        var slotParms = obj.data("ft-slotParms");
                        var options = obj.foreTeesSlot("getOptions");
                        if(!slotParms.skip_unload){
                            return options.onBeforeUnloadMessage;
                        }
                    //obj.foreTeesSlot("leaveForm");
                    //return;
                    }
                
                    // Capture all relevant navigation outside of our slot container, and prompt
                    // user about leaving the slot page
                    var problemLinks = $('a:not([target]):not([href="#"]):not([onclick^="window"])').filter(function(){
                        return $(this).parents(".slot_container").length < 1
                    });
                    problemLinks.addClass("away-from-slot-page-link"); // for debug
                    problemLinks.data("ft-slotParent", obj);
                    problemLinks.click(function(){
                        var answer = confirm(options.navagateAwayMessage);
                        if(answer){
                            var obj = $(this).data("ft-slotParent");
                            obj.foreTeesSlot("leaveForm", true);
                        }else{
                            return false;
                        }
                    });

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
                
                //console.log("setting options:"+slotParms.page_start_title);
                slotParms.options = obj.foreTeesSlot("getOptions");
                slotParms.modalOptions = obj.foreTeesModal("getOptions");
                //console.log("test:" + slotParms.options.buttonSubmit);

                // Check if we have any page start notifications
                obj.foreTeesSlot("pageStartNotification");
                
                if(!slotParms.disableSlotPage  && (slotParms.slot_url == "Member_slot" || slotParms.slot_url == "Member_slotm" )){
                    // Double check that our slot page has been marked in_use properly
                    // If a user clicks the browser refresh button on a slot page, it's possible
                    // a window.onunload event cleared our in_use after this page loaded.
                    obj.foreTeesSlot("checkSlotStatus");
                }
                
                // If no page start notifications,
                if(!slotParms.disableSlotPage){
                    
                    // Create slot page
                    obj.foreTeesSlot("generateSlotPage");
                    
                    // Init slot timer
                    obj.foreTeesSlot("initSlotTimer");

                    // Load partner list
                    obj.foreTeesSlot("loadPartners","partners");
                
                    // Load name list
                    obj.foreTeesSlot("initPlayerSlots");
                
                    // Initialize search table;
                    obj.foreTeesSlot("initPartnerSearchTable");
                
                    // Guest types;
                    obj.foreTeesSlot("initGuestTypes");
                
                    // Member TBD button
                    obj.foreTeesSlot("initGuestTbd");

                    // Submit button
                    obj.foreTeesSlot("initSubmitButton");
                
                    // Go Back button
                    obj.foreTeesSlot("initGoBackButton");
                    
                }
                
            });

        },
        
        refreshSlot: function(){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
          
            obj.foreTeesModal("pleaseWait");
            slotParms.skip_unload = true;
            // Reload slot page, getting json data
            $.ajax({
                type: 'POST',
                url: slotParms.slot_url,
                data: slotParms.callback_map,
                dataType: 'json',
                context: obj,
                success: function(slotParms){
                    //console.log(data);
                    //return;
                    slotParms.skip_unload = false;
                    var obj = $(this);
                    obj.data("ft-slotParms", slotParms);
                    obj.foreTeesModal("pleaseWait","close");
                    obj.foreTeesSlot();
                                    
                    return;
                },
                error: function(xhr, e_text, e){
                    ft_logError("Error refreshing slot page", xhr);
                    var obj = $(this);
                    var slotParms = obj.data("ft-slotParms");
                    slotParms.skip_unload = false;
                    slotParms.disableSlotPage = true;
                    obj.foreTeesModal("pleaseWait","close");
                    obj.foreTeesModal("ajaxError", {
                        jqXHR: xhr, 
                        textStatus: e_text, 
                        errorThrown: e, 
                        allowBack: true,
                        allowRefresh: false,
                        tryAgain: $.proxy(function() {
                            $(this).foreTeesModal("slotPageLoadNotification");
                        },obj)
                    });
                }
            });  
        },
        
        initSlotTimer: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            if(slotParms.time_remaining > -1){
                slotParms.timer_start = new Date().getTime();
                $(".slot_timer").each(function(){
                    var timerObj = $(this);
                    obj.data("ft-slotTimer", timerObj);
                    slotParms.timer_interval = setInterval($.proxy(function(){
                        var obj = $(this);
                        var slotParms = obj.data("ft-slotParms");
                        var timerObj = obj.data("ft-slotTimer");
                        var current_time = new Date().getTime();
                        var elapsed_time = (current_time - slotParms.timer_start);
                        var time_remaining = (slotParms.time_remaining - elapsed_time);
                        if((time_remaining/1000) < (1)){
                            time_remaining = 0;
                            clearTimeout(slotParms.timer_interval);
                            obj.foreTeesModal("slotTimeout");
                        }
                        timerObj.html(ftTimeString(time_remaining));
                        if((time_remaining/1000) < options.lowTimerTrigger){
                            timerObj.addClass("low_timer_trigger");
                        }else{
                            timerObj.removeClass("low_timer_trigger");
                        }
                    },obj),1000);
                });
            }
        },
        
        checkSlotStatus: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            obj.foreTeesModal("pleaseWait");
            // Reload slot page, getting json data
            $.ajax({
                type: 'POST',
                url: slotParms.slot_url,
                //url: "bad_url_test",
                data: slotParms.callback_map,
                dataType: 'json',
                //async: false, // Wait for this to complete before continuing
                context: obj,
                success: function(checkSlotParms){
                    //console.log(data);
                    //return;
                    var obj = $(this);
                    var slotParms = obj.data("ft-slotParms");
                    obj.foreTeesModal("pleaseWait","close");
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
                    ft_logError("Error checking slot status", xhr);
                    var obj = $(this);
                    var slotParms = obj.data("ft-slotParms");
                    slotParms.disableSlotPage = true;
                    obj.foreTeesModal("pleaseWait","close");
                    obj.foreTeesModal("ajaxError", {
                        jqXHR: xhr, 
                        textStatus: e_text, 
                        errorThrown: e, 
                        allowBack: true,
                        allowRefresh: false,
                        tryAgain: $.proxy(function() {
                            $(this).foreTeesSlot("checkSlotStatus");
                        },obj)
                    });
                }
            });

        },
        
        pageStartNotification: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            if(slotParms.page_start_notifications.length > 0 || slotParms.page_start_instructions.length > 0){
                // We have notifications for page start -- display modal and abort activation of slot page
                slotParms.disableSlotPage = true;
                obj.foreTeesModal("slotPageLoadNotification");
            }
        },
        
        addGuest: function(guestData){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            // Set default transport type for guest
            var default_cw = "";
            if(typeof(slotParms.guest_type_cw_map[guestData.guest_type]) == "string"){
                default_cw = slotParms.guest_type_cw_map[guestData.guest_type];
            } else if(typeof(slotParms.guest_type_cw_map["_default_"]) == "string") {
                default_cw = slotParms.guest_type_cw_map["_default_"];
            }
            // Put guest in first blank
            for(var i = 0; i < slotParms.player_count; i++){
                if(((i % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group){
                    if ($.trim(slotParms.player_a[i]) == "" && slotParms.lock_player_a[i] == false){
                        if(typeof(guestData.guest_name) != "string"){
                            guestData.guest_name = "";
                        }
                        if(typeof(guestData.guest_id) == "undefined"){
                            guestData.guest_id = 0;
                        }
                        if(guestData.guest_type == "X"){
                            slotParms.player_a[i] = guestData.guest_type;
                        }else{
                            slotParms.player_a[i] = guestData.guest_type + " " + guestData.guest_name;
                        }
                        slotParms.guest_id_a[i] = guestData.guest_id;
                        slotParms.pcw_a[i] = default_cw;
                        ftSetIfDefined(slotParms.gender_a, i, guestData.gender);
                        ftSetIfDefined(slotParms.ghin_a, i, guestData.ghin);
                        ftSetIfDefined(slotParms.email_a, i, guestData.email1);
                        ftSetIfDefined(slotParms.phone_a, i, guestData.phone1);
                        ftSetIfDefined(slotParms.address_a, i, guestData.address1);
                        ftSetIfDefined(slotParms.homeclub_a, i, guestData.homeclub);
                        // Update player slots
                        obj.foreTeesSlot("initPlayerSlots");
                        var inputObj = $(obj.find("tr.slot_player_row input.player_name").get(i))
                        inputObj.foreTeesHighlight(options.slotAddColor, options.slotAddDuration); // Flash new field green
                        inputObj.focus().val(inputObj.val());
                        return;
                    }
                }
            }
            obj.find("tr.slot_player_row input.player_name").foreTeesHighlight(options.slotListFull, options.slotListFullDuration);  // Flash all field red
            return;
        },
        
        addPlayer: function(partnerData){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            // Check if the name is in the list, and set the first blank player
            var first_blank = 0;
            var found_player = 0;
            for(var i = 0; i < slotParms.player_count; i++){
                //console.log("searching:"+slotParms.player_a[i]);
                if(((i % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group){
                    if(slotParms.player_a[i] == partnerData.display){
                        // This name is already in the list
                        found_player = i + 1;
                        //console.log("found match:"+slotParms.player_a[i]+":"+partnerData.display+":"+i);
                        break;
                    }else if ($.trim(slotParms.player_a[i]) == "" && first_blank < 1 && slotParms.lock_player_a[i] == false){
                        // // console.log("found blank");
                        first_blank = (i+1);
                    }
                }
            }
            if(found_player > 0){
                // We already have thisplay are the list -- we could do somthing to let the user know
                //// console.log("player used:" + partnerData.partner_display);
                
                $(obj.find("tr.slot_player_row input.player_name").get(found_player - 1)).foreTeesHighlight(options.slotDuplicateColor, options.slotDuplicateDuration);  // Flash used field yellow
                
            } else if (first_blank < 1) {
                // Player list is full -- we could do somthing to let the user know
                //// console.log("list full:"+slotParms.player_count+":" + partnerData.partner_display);
                obj.find("tr.slot_player_row input.player_name").foreTeesHighlight(options.slotListFull, options.slotListFullDuration);  // Flash all field red
                
            } else {
                // Set the player
                //// console.log("adding player:" + partnerData.partner_display);
                slotParms.player_a[first_blank - 1] = partnerData.display;
                
                if(slotParms.gender_a[first_blank - 1] !== undefined){
                    slotParms.gender_a[first_blank - 1] = partnerData.gender
                }
                if(slotParms.ghin_a[first_blank - 1] !== undefined){
                    slotParms.ghin_a[first_blank - 1] = partnerData.ghin
                }
                // Set the player's default transport type
                
                //console.log("AddPlayer:");
                //console.log(partnerData);
                
                var tmode = "";
                if(slotParms.use_default_member_tmode){
                    tmode = partnerData.wc;
                }
                
                tmode = obj.foreTeesSlot("validateMemberTmode", tmode, true);
                
                slotParms.pcw_a[first_blank - 1] = tmode;
                // Update player slots
                obj.foreTeesSlot("initPlayerSlots");
                $(obj.find("tr.slot_player_row input.player_name").get(first_blank - 1)).foreTeesHighlight(options.slotAddColor, options.slotAddDuration); // Flash new field green
            }
            
        },
        
        validateMemberTmode: function(tmode, apply_override){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            //console.log("checking:"+tmode);
            //console.log(slotParms.full_tmode_map);
            
            obj.foreTeesSlot("initTmodes");

            if(!((typeof(slotParms.full_tmode_map[tmode]) == "string" && slotParms.full_tmode_map[tmode].length) || !slotParms.verify_member_tmode)){
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
        
        initGuestTbd: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            obj.find("a.member_tbd_button").each(function(){
                $(this).data("ft-slotParent", obj);
                $(this).click(function(){
                    obj = $(this).data("ft-slotParent");
                    obj.foreTeesSlot("addGuest", {
                        guest_type:"X"
                    });
                    return false;
                });
            });
        },
        
        initSubmitButton: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            obj.find("a.submit_request_button").each(function(){
                $(this).data("ft-slotParent", obj);
                $(this).click(function(){
                    obj = $(this).data("ft-slotParent");
                    obj.foreTeesSlot("submitForm");
                    return false;
                });
            });
            
            obj.find("a.cancel_request_button").each(function(){
                $(this).data("ft-slotParent", obj);
                $(this).click(function(){
                    obj = $(this).data("ft-slotParent");
                    obj.foreTeesSlot("cancelSlot");
                    return false;
                });
            });
        },
        
        initGoBackButton: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            obj.find("a.go_back_button").each(function(){
                $(this).data("ft-slotParent", obj);
                $(this).click(function(){
                    var obj = $(this).data("ft-slotParent");
                    var slotParms = obj.data("ft-slotParms");
                    obj.foreTeesSlot("leaveForm", false, true);
                    //obj.foreTeesSlot("exitSlotPage");
                    return false;
                });
            });
        },
        
        initGuestTypes: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            var hasDisabledTypes = false;
            // Activate Guest Types
            obj.find(".guest_type_list").each(function(){
                // Initialize each option
                for(var key in slotParms.guest_types_map){
                    var optionObj = $('<option value="'+slotParms.guest_types_map[key]["guest_type"]+'">'+slotParms.guest_types_map[key]["guest_type"]+'</option>');
                    slotParms.guest_types_map[key]["slotParent"] = obj;
                    optionObj.data("ft-guestData", slotParms.guest_types_map[key]);
                    if(("guest_type_disabled" in slotParms.guest_types_map[key]) && (slotParms.guest_types_map[key].guest_type_disabled)){
                        // Guest is disabled
                        hasDisabledTypes = true;
                        optionObj.css("background-color", options.disabledGuestTypeColor);
                        if(ftIsMobile()){
                            optionObj.html(optionObj.html() + options.disabledGuestTypeMobileText);
                        }
                    }else{
                        if(slotParms.guest_types_map[key]["guest_type_db"] > 0){
                            optionObj.data("ft-addGuestEvent", $.proxy(function(){
                                var guestData = $(this).data("ft-guestData");
                                var obj = guestData.slotParent;
                                //// console.log("clicked");
                                //obj.foreTeesSlot("addGuest", guestData);
                                obj.foreTeesModal("guestDbPrompt","open", {
                                    callback:$.proxy(function(guestDbData){
                                        var guestData = $(this).data("ft-guestData");
                                        var obj = guestData.slotParent;
                                        //guestData["guest_name"] = guestDbData.guest_name;
                                        //guestData["guest_id"] = guestDbData.guest_id;
                                        obj.foreTeesModal("guestDbPrompt", "close");
                                        obj.foreTeesSlot("addGuest", $.extend({},guestDbData,guestData)); 
                                    },$(this))
                                });
                            }, optionObj));
                        }else{
                            optionObj.data("ft-addGuestEvent", $.proxy(function(event){
                                var guestData = $(this).data("ft-guestData");
                                var obj = guestData.slotParent;
                                //// console.log("clicked");
                                obj.foreTeesSlot("addGuest", guestData);
                            }, optionObj));
                        }
                    }
                    $(this).append(optionObj);
                    $(this).scrollTop(0); 
                //}
                }
                $(this).change(function(){
                    $(this).find("option").removeClass("selected_option");
                    $(this).find("option:selected").each(function(){
                        $(this).addClass("selected_option");
                        $(this).data("ft-addGuestEvent")();
                    });
                    // Deselect all items
                    
                    $(this).find("option:selected").attr("selected",false);
                    $(this).find("option:selected").removeAttr("selected"); 
                    $(this).get(0).selectedIndex = -1;
                    
                });
                if(hasDisabledTypes){
                    if(ftIsMobile()){
                        $(this).before('<p class="small_instructions">'+options.disabledGuestMessageMobile+'</p>');   
                    }else{
                        $(this).before('<p class="small_instructions">'+options.disabledGuestMessage+'</p>');
                    }
                }
            });
            
        },
        
        showPartners: function(){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            var nameLookup = new Object();
            obj.find("select.partner_list").each(function(){
                $(this).scrollTop(0); // reset list position to top
                $(this).find("option").remove(); // clear the list
                for(var key in slotParms.partner_list){ // Build the list
                    var skip_name = false;
                    // Check if this member should be displayed
                    if(!(slotParms.last_name_list_type == "partners" && slotParms.filter_partner_list == false)){
                        for (var field in slotParms.name_list_filter_map){
                            var filter = slotParms.name_list_filter_map[field];
                            if(filter.length < 3){
                                filter[2] = new RegExp(filter[0], filter[1]);
                            }
                            if(slotParms.partner_list[key][field].match(filter[2]) != null){
                                skip_name = true;
                            }
                        }
                    }
                    
                    if(!skip_name){
                        var name = slotParms.partner_list[key]["name"];
                        if(slotParms.show_ghin_in_list && slotParms.partner_list[key]["ghin"].length){
                            name += " " + slotParms.partner_list[key]["ghin"];
                        }
                        var option = $('<option value="'+slotParms.partner_list[key]["display"]+'" style="'+slotParms.partner_list[key]["style"]+'">'+name+'</option>');
                        slotParms.partner_list[key]["slotParent"] = obj;
                        option.data("ft-partnerData", slotParms.partner_list[key]);
                        $(this).append(option);
                        $(this).scrollTop(0); 
                        nameLookup[slotParms.partner_list[key]["display"]] = option;
                    }
                }
                $(this).find("option:selected").removeAttr("selected");
                obj.data("ft-slotPartnerNameLookup", nameLookup);  // Store our reverse lookup for each option
                
                $(this).foreTeesListSearch("clear");
                $(this).foreTeesListSearch("init", {
                    name: ftReplaceKeyInString(options.nameListHeader,slotParms),
                    instructions: ftReplaceKeyInString(options.nameListInstructions,slotParms),
                    slotParms: slotParms,
                    change: function(){
                        // add selected player
                        $(this).find("option:selected").each(function(){
                            if(typeof($(this).data("ft-partnerData")) != "undefined"){
                                var partnerData = $(this).data("ft-partnerData");
                                var obj = partnerData.slotParent;
                                obj.foreTeesSlot("addPlayer", partnerData);
                            }
                        });
                    }
                });
            });
            obj.foreTeesSlot("initPlayerSlots");
            
        },
        
        loadPartners: function(letter){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            slotParms.last_name_list_type = letter;
            var url = "data_loader?json_mode=true&name_list=true&letter="+letter;
            // // console.log(url);
            
            // Block user input
            obj.foreTeesModal("pleaseWait");
            // Update list of partners
            $.ajax({
                        
                url: url,
                context:obj,
                dataType: 'json',
                success: function(data){
                    //// console.log("got partner list");
                    var slotParms = obj.data("ft-slotParms");
                    slotParms.partner_list = data;
                    obj.foreTeesSlot("showPartners");
                    obj.foreTeesModal("pleaseWait", "close");
                            
                },
                error: function(xhr){
                    ft_logError("Error loading partner list", xhr);
                    // console.log("error recv. partner list");
                    obj.foreTeesModal("pleaseWait", "close");
                    obj.foreTeesModal("ajaxError", {
                        context:this,
                        errorThrown:"Unable to load name list data",
                        tryAgain:function(option){
                            var slotParms = option.context.data("ft-slotParms");
                            $(option.context).foreTeesSlot("loadPartners",slotParms.last_name_list_type);
                        },
                        allowRefresh: true
                    });  
                            
                }
            });
            
        },
        
        initPartnerSearchTable: function(){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            // Activate the letter buttons
            obj.find(".member_search_letter_button").each(function(){
            
                $(this).data("ft-slotParent", obj);
                $(this).click(function(event){
                    var obj =  $(this).data("ft-slotParent");
                    obj.foreTeesSlot("loadPartners", String.fromCharCode(parseInt($(this).attr("data-ftinteger"))));
                    event.preventDefault();
                });
            
            });
            // Activate the partner buttons
            obj.find(".member_search_partners_button").each(function(){
            
                $(this).data("ft-slotParent", obj);
                $(this).click(function(event){
                    var obj =  $(this).data("ft-slotParent");
                    obj.foreTeesSlot("loadPartners", "partners");
                    event.preventDefault();
                });
            
            });
            
            
        },
        
        exitSlotPage: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            obj.foreTeesModal("pleaseWait");
            history.back(-1);
        },
        
        submitForm: function(){
            
            var obj = $(this);
            obj.foreTeesModal("slotSubmit", "open", "submit_slot");
            
        },
        
        cancelSlot: function(){
            
            var obj = $(this);
            obj.foreTeesModal("slotCancelPrompt");
            
        },
        
        leaveForm: function(syncMode, exitOnComplete){
            
            if (exitOnComplete === null || typeof(exitOnComplete) == "undefined"){
                exitOnComplete = false;
            }
            if (syncMode == null || typeof(syncMode) == "undefined"){
                syncMode = false;
                //console.log("default sync mode");
            }
            //console.log("async:"+(!syncMode));
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            slotParms.syncMode = syncMode;
            slotParms.exitOnComplete = exitOnComplete;
            var formValues = obj.foreTeesSlot("getFormParameters");
            formValues["cancel"] = "cancel";
            formValues["json_mode"] = "true";
            
            if(typeof(slotParms.timer_interval) != "undefined"){
                clearTimeout(slotParms.timer_interval);
            }
            
            if(typeof(slotParms.skip_unload) == "undefined"){
                slotParms.skip_unload = false;
            }
            if(!slotParms.skip_unload){
                //console.log("freeing slot...");
                //console.log("sending:"+JSON.stringify(formValues));
                $.ajax({
                    type: 'POST',
                    url: slotParms.slot_url,
                    data: formValues,
                    async: !syncMode,
                    dataType: 'text',
                    context: obj,
                    success: function(data){
                        var obj = $(this);
                        var slotParms = obj.data("ft-slotParms");
                        if(slotParms.exitOnComplete){
                            obj.foreTeesSlot("exitSlotPage");
                        }
                        //console.log("Free slot result:"+data);
                        return;
                    },
                    error: function(xhr){
                        ft_logError("Error exiting slot page", xhr);
                        var obj = $(this);
                        var slotParms = obj.data("ft-slotParms");
                        if(slotParms.exitOnComplete){
                            obj.foreTeesSlot("exitSlotPage");
                        }
                        
                    //console.log("Free slot error.");
                    }
                });
            }
            slotParms.skip_unload = true;
        },
        
        getFormParameters: function(){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            var formParameters = {};
            for(var name in slotParms.slot_submit_map){
                $.extend(formParameters,ftFieldToObject(slotParms[slotParms.slot_submit_map[name]], name));
            }
            return formParameters;
            
        },
        
        initTmodes: function(){
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            // Create full tmode lookup object, if one does not exist
            if(typeof(slotParms.full_tmode_map) != "object"){
                slotParms.full_tmode_map = {};
                for(var i = 0; i < slotParms.allowed_tmodes_list.length; i++){
                    if(typeof(slotParms.course_parms.tmodea[i]) == "string" && slotParms.allowed_tmodes_list[i] != ""){
                        slotParms.full_tmode_map[slotParms.allowed_tmodes_list[i]] = slotParms.allowed_tmodes_list[i];
                    }
                }
                /*
                for(var key in slotParms.guest_type_cw_map){
                    if(typeof(slotParms.guest_type_cw_map[key]) == "string" && slotParms.guest_type_cw_map[key] != ""){
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
        
        initPlayerSlots: function(){
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            var nameLookup = obj.data("ft-slotPartnerNameLookup");
            
            if(typeof(nameLookup) != "object"){
                //console.log("no name lookup");
                nameLookup = {};
                obj.data("ft-slotPartnerNameLookup", nameLookup);
            }
            obj.find("select.partner_list option").removeClass("on_slot");
            
            obj.foreTeesSlot("initTmodes");
            
            // Init pro notes
            obj.find("tr.notes_row").each(function(){
                var noteRow = $(this);
                noteRow.find("textarea").each(function(){
                    var noteText = $(this);
                    noteText.val(slotParms.notes);
                    noteText.data("ft-slotParent", obj);
                    noteText.change(function(){
                        var obj = $(this).data("ft-slotParent");
                        var slotParms = obj.data("ft-slotParms");
                        slotParms.notes = $(this).val();
                    });
                    noteRow.find("a.notes_erase_button").each(function(){
                        $(this).data("ft-slotParent", obj);
                        $(this).data("ft-slotProNoteText", noteText);
                        $(this).click(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            noteText = $(this).data("ft-slotProNoteText");
                            noteText.val("");
                            slotParms.notes = "";
                            return false;
                        });
                    });
                });                
            });
            
            // Initialize player slots
            obj.find("tr.slot_player_row").each(function(player_index){ 
                // Fill transport select list
                var objPlayerRow = $(this);
                $(this).find("select.transport_type").each(function(){
                    var selectObj = $(this);
                    if(slotParms.lock_player_a[player_index]){
                        $(this).attr("disabled", "diabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    
                    // Validate tmode for this player
                    if(slotParms.pcw_a[player_index].length){
                        slotParms.pcw_a[player_index] = obj.foreTeesSlot("validateMemberTmode", slotParms.pcw_a[player_index]);
                    }
                    
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        // Create and set the transport options
                        selectObj.find("option").remove(); // clear the list
                        var selected = ((slotParms.pcw_a[player_index] == "")?" selected":"");
                        var option = $('<option value=""'+selected+'></option>');
                        option.data("ft-slotPlayerIndex", player_index);
                        selectObj.append(option); // Add blank option as first
                        selectObj.data("ft-slotPlayerIndex", player_index);
                        selectObj.data("ft-slotParent", obj);
                        for (var i = 0; i < slotParms.tmodes_list.length; i++){
                            if(slotParms.tmodes_list[i] != ""){
                                selected = ((slotParms.tmodes_list[i] == slotParms.pcw_a[player_index])?" selected":"");
                                option = $('<option value="'+slotParms.tmodes_list[i]+'"'+selected+'>'+slotParms.tmodes_list[i]+'</option>');
                                option.data("ft-slotPlayerIndex", player_index);
                                selectObj.append(option);
                            }
                        }
                        // Bind our on-change event
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).find("option:selected").first().data("ft-slotPlayerIndex");
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
                        if(typeof(slotParms.full_tmode_map[slotParms.pcw_a[player_index]]) == "string" || !slotParms.verify_member_tmode){
                            option = $('<option value="'+slotParms.pcw_a[player_index]+'" selected>'+slotParms.pcw_a[player_index]+'</option>');
                            option.data("ft-slotPlayerIndex", player_index);
                            option.data("ft-slotCustomTransportType", slotParms.pcw_a[player_index])
                            $(this).append(option);
                        }else{
                            slotParms.pcw_a[player_index] = "";  // Default tmode not availible -- remove it
                        }
                    }
                    if(typeof(slotParms.full_tmode_map[slotParms.pcw_a[player_index]]) == "string"){
                        selectObj.val(slotParms.pcw_a[player_index]);
                    }
                 
                });
                // Fill player names
                $(this).find("input.player_name").each(function(){
                    if(slotParms.lock_player_a[player_index]){
                        $(this).attr("disabled", "diabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            slotParms.player_a[player_index] = $.trim($(this).val());
                        });
                    }
                    $(this).val(slotParms.player_a[player_index]);
                });
                // Fill 9-Holes
                if(slotParms.set_default_fb_value){
                    slotParms.p9_a[player_index] = slotParms.default_fb_value;
                }
                $(this).find("input.slot_9holes").each(function(){
                    if(slotParms.lock_player_a[player_index] || slotParms.lock_fb){
                        $(this).attr("disabled", "disabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            slotParms.p9_a[player_index] = (($(this).prop("checked"))?1:0);
                        });
                    }
                    $(this).prop("checked", (slotParms.p9_a[player_index] > 0));
                });
                // Fill Gift pack
                $(this).find("input.slot_gift_pack").each(function(){
                    if(slotParms.lock_player_a[player_index]){
                        $(this).attr("disabled", "diabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            slotParms.gift_pack_a[player_index] = (($(this).prop("checked"))?1:0);
                        });
                    }
                    $(this).prop("checked", (slotParms.gift_pack_a[player_index] > 0));
                });
                // Fill Gender
                $(this).find("select.slot_gender").each(function(){
                    if(slotParms.lock_player_a[player_index]){
                        $(this).attr("disabled", "diabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).append('<option value=""></option>');
                        $(this).append('<option value="M">M</option>');
                        $(this).append('<option value="F">F</option>');
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            ftSetIfDefined(slotParms.gender_a, player_index, $(this).val());
                        });
                    }
                    if(slotParms.gender_a[player_index] !== undefined){
                        $(this).val(slotParms.gender_a[player_index]);
                    }
                });
                // Fill Ghin
                $(this).find("input.slot_ghin").each(function(){
                    if(slotParms.lock_player_a[player_index]){
                        $(this).attr("disabled", "diabled");
                    }else{
                        $(this).removeAttr("disabled");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            ftSetIfDefined(slotParms.ghin_a, player_index, $(this).val());
                        //console.log(JSON.stringify(slotParms.ghin_a));
                        });
                    }
                    if(slotParms.ghin_a[player_index] !== undefined){
                        $(this).val(slotParms.ghin_a[player_index]);
                    }
                });
                // Initialize the player erase buttons
                $(this).find(".player_erase_button").each(function(){
                    if(slotParms.lock_player_a[player_index]){
                        $(this).css("visibility", "hidden");
                    }
                    if(typeof($(this).data("ft-slotPlayerIndex")) == "undefined"){
                        $(this).data("ft-slotPlayerIndex", player_index);
                        $(this).data("ft-slotParent", obj);
                        $(this).click(function(event){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            var player_index = $(this).data("ft-slotPlayerIndex");
                            var nameLookup = obj.data("ft-slotPartnerNameLookup");
                            if(typeof(nameLookup[slotParms.player_a[player_index]]) != "undefined"){
                                //console.log("found in namelookup");
                                if(nameLookup[slotParms.player_a[player_index]].val() == obj.find("select.partner_list").val()){
                                    obj.find("select.partner_list option").removeAttr("selected");
                                }
                                nameLookup[slotParms.player_a[player_index]].removeAttr("selected");
                            }
                            slotParms.player_a[player_index] = "";
                            slotParms.pcw_a[player_index] = "";
                            ftSetIfDefined(slotParms.custom_a, player_index, "");
                            ftSetIfDefined(slotParms.ghin_a, player_index, "");
                            objPlayerRow.find(".slot_ghin").val("");
                            ftSetIfDefined(slotParms.gender_a, player_index, "");
                            objPlayerRow.find(".slot_gender").val("");
                            ftSetIfDefined(slotParms.email_a, player_index, "");
                            ftSetIfDefined(slotParms.phone_a, player_index, "");
                            ftSetIfDefined(slotParms.shoe_a, player_index, "");
                            ftSetIfDefined(slotParms.shirt_a, player_index, "");
                            ftSetIfDefined(slotParms.otherA1_a, player_index, "");
                            ftSetIfDefined(slotParms.otherA2_a, player_index, "");
                            ftSetIfDefined(slotParms.otherA3_a, player_index, "");
                            slotParms.p9_a[player_index] = slotParms.default_fb_value;
                            obj.foreTeesSlot("initPlayerSlots");
                            event.preventDefault();
                        });
                    }
                });
                // Update class of partner list
                if(typeof(nameLookup[slotParms.player_a[player_index]]) != "undefined"){
                    //console.log("found:" + nameLookup[slotParms.player_a[player_index]].val());
                    obj.find('select.partner_list option[value="'+nameLookup[slotParms.player_a[player_index]].val()+'"]').addClass("on_slot")
                }
            });
            // Fill "Force Singles"
            $(this).find('select[name="force_singles"]').each(function(){
                if(typeof($(this).data("ft-slotInitialized")) == "undefined"){
                    $(this).data("ft-slotInitialized", true);
                    $(this).data("ft-slotParent", obj);
                    $(this).append('<option value="0">No</option>');
                    $(this).append('<option value="1">Yes</option>');
                    $(this).change(function(){
                        var obj = $(this).data("ft-slotParent");
                        var slotParms = obj.data("ft-slotParms");
                        slotParms.force_singles = $(this).val();
                    });
                }
                $(this).val(slotParms.force_singles);
            });
            // Fill time picker
            $(this).find('tr.time_picker_row').each(function(){
                
                // Minutes
                $(this).find('input[name$="_min"]').each(function(){
                    if(typeof($(this).data("ft-slotInitialized")) == "undefined"){
                        $(this).data("ft-slotInitialized", true);
                        $(this).data("ft-slotParent", obj);
                        $(this).click(function(){
                            $(this).select();
                        });
                        $(this).attr("maxlength", 2);
                        $(this).data("ft-lastValue", $(this).attr("name"));
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            $(this).keyup();
                            var new_value = parseInt($(this).val());
                            if(isNaN(new_value)){
                                new_value = 0;
                            }
                            $(this).val(ftPadInt(new_value,2));
                            slotParms[$(this).attr("name")] = $(this).val();
                            $(this).data("ft-lastValue", new_value);
                        });
                        $(this).keyup(function(){
                            var error = false;
                            var last_value = parseInt($(this).data("ft-lastValue"));
                            if(isNaN(last_value)){
                                last_value = 0;
                                error = true;
                            }
                            var new_value = parseInt($(this).val());
                            if(isNaN(new_value) && $(this).val() != ""){
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
                                $(this).foreTeesHighlight(options.badMinColor, options.badMinDuration);
                                $(this).val(ftPadInt(new_value,2));
                            }
                        });
                    }
                    $(this).val(slotParms[$(this).attr("name")]);
                    $(this).change();
                });
                
                // Hours
                $(this).find('select[name$="_hr"]').each(function(){
                    if(typeof($(this).data("ft-slotInitialized")) == "undefined"){
                        $(this).data("ft-slotInitialized", true);
                        $(this).data("ft-slotParent", obj);
                        for(var hour = 1; hour <= 12; hour ++ ){
                            $(this).append('<option value="'+hour+'">'+hour+'</option>');
                        }
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            slotParms[$(this).attr("name")] = $(this).val();
                        });
                    }
                    $(this).val(slotParms[$(this).attr("name")]);
                });
                
                // Am Pm
                $(this).find('select[name$="_ampm"]').each(function(){
                    if(typeof($(this).data("ft-slotInitialized")) == "undefined"){
                        $(this).data("ft-slotInitialized", true);
                        $(this).data("ft-slotParent", obj);
                        $(this).append('<option value="AM">AM</option>');
                        $(this).append('<option value="PM">PM</option>');
                        $(this).change(function(){
                            var obj = $(this).data("ft-slotParent");
                            var slotParms = obj.data("ft-slotParms");
                            slotParms[$(this).attr("name")] = $(this).val();
                        });
                    }
                    $(this).val(slotParms[$(this).attr("name")]);
                });
            });
        },
        
        generateSlotPage: function () {
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            
            //
            //  Build page to prompt user for names
            //
            
            if(slotParms.time_remaining > -1){
                options.mainInstructions = options.mainInstructionsTimer;
            }else{
                options.mainInstructions = options.mainInstructionsNoTimer;
            }

            var all_columns = 8;
            var text_columns = 6;
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
                slotHeadHtml += ftReplaceKeyInString(options.subInstructionsName,slotParms);
            }
            if(!slotParms.season_long){
                if(slotParms.day.length){
                    slotHeadHtml += ftReplaceKeyInString(options.subInstructionsDayDate,slotParms);
                }else{
                    slotHeadHtml += ftReplaceKeyInString(options.subInstructionsDate,slotParms);
                }
                if(slotParms.slots > 1){
                    slotHeadHtml += ftReplaceKeyInString(options.subInstructionsFirstTime,slotParms);
                } else if (slotParms.stime.length) {
                    slotHeadHtml += ftReplaceKeyInString(options.subInstructionsTime,slotParms);
                }
            }else{
                slotHeadHtml += ftReplaceKeyInString(options.subInstructionsSeasonLong,slotParms);
            }
            if (!slotParms.course_disp == "") {
                slotHeadHtml += ftReplaceKeyInString(options.subInstructionsCourse,slotParms);
            }
            slotHeadHtml += "</div>"; // end date/time/course div
            
            for (var i = 0; i < slotParms.slot_header_notes.length; i++) {
                slotHeadHtml += "<div class=\"sub_instructions slot_header_notes\">" + ftReplaceKeyInString(slotParms.slot_header_notes[i],slotParms) + "</div>";
            }
            
            $(this).before(slotHeadHtml);
            
            for (var i = 0; i < slotParms.slot_footer_notes.length; i++) {
                $(this).after("<div class=\"sub_instructions slot_footer_notes\">" + ftReplaceKeyInString(slotParms.slot_footer_notes[i],slotParms) + "</div>");
            }

            var slotHtml = "";
            slotHtml += "<div class=\"res_left\">"; // start of left column
            slotHtml += "<div class=\"request_container sub_main\">"; // start of request container
            slotHtml += "<h3 class=\"left_header\">"+ftReplaceKeyInString(options.slotListHeader,slotParms)+"</h3>";
            slotHtml += "<h3 class=\"right_header\">"+ftReplaceKeyInString(options.slotListIntructions,slotParms)+"</h3>";
            slotHtml += "<div style=\"clear:both;\"></div>";
            // Build player request table
            slotHtml += "<div class=\"element_group\">";  // Start slot list element group
            slotHtml += "<form>";
            slotHtml += "<table class=\"slot_table\">";
            slotHtml += "<thead><tr><th></th><th></th><th>"+ftReplaceKeyInString(options.slotListPlayerHead,slotParms)+"</th>";
            if (slotParms.show_transport == true) {
                slotHtml += "<th>"+ftReplaceKeyInString(options.slotListTransHead,slotParms)+"</th>";
            }
            if (slotParms.show_ghin == true) {
                slotHtml += "<th>"+ftReplaceKeyInString(options.slotListGhinHead,slotParms)+"</th>";
            }
            if (slotParms.show_gender == true) {
                slotHtml += "<th>"+ftReplaceKeyInString(options.slotListGenderHead,slotParms)+"</th>";
            }
            if (slotParms.show_fb == true) {
                slotHtml += "<th>"+ftReplaceKeyInString(options.slotList9holeHead,slotParms)+"</th>";
            }
            if (slotParms.show_gift_pack == true) {
                slotHtml += "<th>"+ftReplaceKeyInString(options.slotListGiftPackHead,slotParms)+"</th>";
            }
            slotHtml += "</tr></thead>";
            slotHtml += "<tbody>";
            for (var i2 = 0; i2 < slotParms.player_count; i2++) {
                if ((i2 % slotParms.players_per_group == 0) && (i2 > 0)) {  // Check if we are in a new group
                    slotHtml += "</tbody>";
                    slotHtml += "<tbody>";
                }
                slotHtml += "<tr class=\"slot_player_row"+((((i2 % slotParms.players_per_group) + 1) <= slotParms.visible_players_per_group)?"":" no_display")+"\" id=\"slot_player_row_" + i2 + "\">";
                slotHtml += "<td><a class=\"tip_text player_erase_button\" href=\"#\">"+ftReplaceKeyInString(options.slotListErase,slotParms)+"</a></td>";
                slotHtml += "<td class=\"player_count_cell\">" + (i2 + 1) + ":</td>";
                slotHtml += "<td class=\"player_cell\"><input class=\"player_name\" type=\"text\"></td>";
                if (slotParms.show_transport == true) {
                    slotHtml += "<td><select class=\"transport_type\"></select></td>";
                }
                if (slotParms.show_ghin == true) {
                    slotHtml += "<td><input type=\"text\" "+((slotParms.lock_ghin)?"disabled=\"disabled\" ":"")+"class=\"slot_ghin\"></td>";
                }
                if (slotParms.show_gender == true) {
                    slotHtml += "<td><select class=\"slot_gender\"></select></td>";
                }
                if (slotParms.show_fb == true) {
                    slotHtml += "<td><input class=\"slot_9holes\" type=\"checkbox\"></td>";
                }
                if (slotParms.show_gift_pack == true) {
                    if (slotParms.gift_pack_a[i2] == 1) {
                        slotHtml += "<td><input class=\"slot_gift_pack\" type=\"checkbox\" value=\"1\"></td>";
                    } else {
                        slotHtml += "<td><input class=\"slot_gift_pack\" type=\"checkbox\" disabled></td>";
                    }
                }
                slotHtml += "</tr>";
            }
            slotHtml += "</tbody>";
            
            if (slotParms.show_force_singles_match) {  // Show singles match selection
                slotHtml += "<tbody>";
                slotHtml += "<tr class=\"force_singles_row\">";
                slotHtml += "<td></td>";
                slotHtml += "<td></td>";
                slotHtml += "<td class=\"spanned_cell\" colspan=\"" + (text_columns - 2) + "\">"
                + "<label for=\"force_singles\">"+ftReplaceKeyInString(options.forceSinglesMatch,slotParms)+"</label>"
                + "<select name=\"force_singles\"></select>"
                + "</td>";
                slotHtml += "</tr>";
                slotHtml += "</tbody>";
            }
            
            if (slotParms.show_time_picker) {  // Show wait list time picker
                slotHtml += "<tbody>";
                slotHtml += "<tr class=\"time_picker_row\">";
                slotHtml += '<td colspan="2">'+ftReplaceKeyInString(options.timePickerFrom,slotParms)+"</td>";
                slotHtml += "<td class=\"spanned_cell\" colspan=\"" + (text_columns - 2) + "\">"
                + "<select name=\"start_hr\"></select>"
                + "<b>:</b>"
                + '<input type="text" name="start_min">'
                + '<b></b>'
                + "<select name=\"start_ampm\"></select>"
                + "</td>";
                slotHtml += "</tr>";
                slotHtml += "<tr class=\"time_picker_row\">";
                slotHtml += '<td colspan="2">'+ftReplaceKeyInString(options.timePickerTo,slotParms)+"</td>";
                slotHtml += "<td class=\"spanned_cell\" colspan=\"" + (text_columns - 2) + "\">"
                + "<select name=\"end_hr\"></select>"
                + "<b>:</b>"
                + '<input type="text" name="end_min">'
                + '<b></b>'
                + "<select name=\"end_ampm\"></select>"
                + "</td>";
                slotHtml += "</tr>";
                slotHtml += "</tbody>";
            }
            
            if (slotParms.hide_notes == 0) {  // if proshop wants to hide the notes, do not display the text box or notes
                slotHtml += "<tbody>";
                slotHtml += "<tr class=\"notes_row\">";
                slotHtml += "<td><a class=\"tip_text notes_erase_button\" href=\"#\">"+ftReplaceKeyInString(options.slotListErase,slotParms)+"</a></td>";
                slotHtml += "<td></td>";
                slotHtml += "<td class=\"spanned_cell\" colspan=\"" + (text_columns - 2) + "\">"
                + "<p>"+ftReplaceKeyInString(slotParms.notes_prompt,slotParms)+"<p>"
                + "<textarea class=\"pro_notes\"></textarea>"
                + "</td>";
                slotHtml += "</tr>";
                slotHtml += "</tbody>";
            }
            
            if (slotParms.show_transport == true) {  // Display tmode legend
                slotHtml += "<tbody>";
                slotHtml += "<tr><td class=\"spanned_cell\" colspan=\"" + all_columns + "\">" + slotParms.transport_legend + "</td></tr>";
                slotHtml += "</tbody>";
            }
            
            slotHtml += "</table>";
            slotHtml += "</form>";
            slotHtml += "</div>";  // Start slot list element group
            slotHtml += "<div class=\"button_container\">";
            slotHtml += "<a href=\"#\" class=\"go_back_button\">"+ftReplaceKeyInString(options.buttonGoBack,slotParms)+"</a>";
            if (slotParms.allow_cancel == true  && slotParms.show_contact_to_cancel == false) {
                slotHtml += "<a href=\"#\" class=\"cancel_request_button\">"+ftReplaceKeyInString(options.buttonCancel,slotParms)+"</a>";
            }
            slotHtml += "<a href=\"#\" class=\"submit_request_button\">"+ftReplaceKeyInString(options.buttonSubmit,slotParms)+"</a>";
            
            if(slotParms.show_contact_to_cancel){
                slotHtml += "<p>"+ftReplaceKeyInString(options.contactToCancel,slotParms)+"</p>";
            }

            slotHtml += "<a class=\"help_link\" href=\"javascript:void(0);\" onClick=\"window.open ('" + slotParms.slot_help_url + "', 'newwindow', config='Height=500, width=680, toolbar=no, menubar=no, scrollbars=auto, resizable=no, location=no directories=no, status=no')\">"
            + ftReplaceKeyInString(options.buttonHelp,slotParms)
            + "</a>";
            slotHtml += "<div style=\"clear:both;\"></div>";
            slotHtml += "</div>";
            slotHtml += "</div>"; // end request container
            slotHtml += "</div>"; // end of left column

            //
            //  add a widget for displaying member list
            //
            if (slotParms.show_member_select == true) { // Show memeber/partner name list
                slotHtml += "<div class=\"res_mid\">"; // start of middle column
                slotHtml += "<div class=\"sub_instructions name_list_container\">"; // start name list container
                //slotHtml += "<h3>"+ftReplaceKeyInString(options.nameListHeader,slotParms)+"</h3>";
                //slotHtml += "<p class=\"small_instructions\">"+ftReplaceKeyInString(options.nameListInstructions,slotParms)+"</p>";
                slotHtml += "<div class=\"element_group\">";  // Start partner list element group
                slotHtml += "<select size=\"15\" class=\"partner_list\"></select>";
                slotHtml += "</div>";  // end  parter list element group
                slotHtml += "</div>"; // end name list container
                slotHtml += "</div>"; // end of middle column
            }

            slotHtml += "<div class=\"res_right\">"; // start of right column
            
            //
            //  add a widget for searching memeber by the first letter of thier last name
            //
            if (slotParms.show_member_select == true) {  // Alpha table for member search
                slotHtml += "<div class=\"sub_instructions member_search_container\">"; // start member search table container
                slotHtml += "<h3>"+ftReplaceKeyInString(options.memberListHeader,slotParms)+"</h3>";
                slotHtml += "<div class=\"element_group\">";  // Start element group
                slotHtml += "<table class=\"member_search_letter_table\"><tbody><tr>";
                for (var i2 = 0; i2 < 26; i2++) { // output memberletter search table table
                    if ((i2 % 6 == 0) && i2 > 0) {  // start new row of letters every 6 letters
                        slotHtml += "</tr><tr>";
                    }
                    slotHtml += "<td><a class=\"member_search_letter_button\" data-ftinteger=\"" + (65 + i2) + "\" href=\"#\">&#" + (65 + i2) + ";</a></td>";  // Output html number for letters A to Z
                }
                if (slotParms.user.indexOf("proshop") == 0) {
                    slotHtml += "<td colspan=\"4\"><a class=\"member_search_list_all_button\" href=\"#\">List All</a></td>";
                } else if (slotParms.user == "buddy") {       // if from Member_buddy
                    slotHtml += "<td></td>";
                    slotHtml += "<td></td>";
                    slotHtml += "<td></td>";
                    slotHtml += "<td></td>";
                } else {                            // a member
                    slotHtml += "<td colspan=\"4\"><a class=\"member_search_partners_button\" href=\"#\">Partners</a></td>";
                }
                slotHtml += "</tr></tbody></table></div></div>"; 
            }

            //
            //  add a widget for Member TBD 'x'
            //
            if (slotParms.show_member_tbd == true) {
                
                slotHtml += "<div class=\"sub_main member_tbd_container\">"; // start member tbd container
                slotHtml += "<h3>"+ftReplaceKeyInString(options.memberTbdHeader,slotParms)+"</h3>";
                slotHtml += "<p class=\"small_instructions\">"+ftReplaceKeyInString(options.memberTbdInstructions,slotParms)+"</p>";
                slotHtml += "<div class=\"element_group\">";  // Start TBD/X list element group
                slotHtml += "<a class=\"member_tbd_button\" href=\"#\">X</a>";
                slotHtml += "</div>";  // end TBD/X list element group
                slotHtml += "</div>"; // end member tbd container
            }

            //
            //  add a widget for displaying guest types
            //
            if (ftCount(slotParms.guest_types_map) > 0 && slotParms.show_guest_types == true) { // if guest names, display them in list

                var size = ftCount(slotParms.guest_types_map);
                var xCount = size;
                if (xCount < 2) {
                    xCount = 2;             // set size to at least 2
                }
                if (xCount > 8) {
                    xCount = 8;             // set size to no more than 8 showing at once (it will scroll)
                }
                slotHtml += "<div class=\"sub_instructions member_tbd_container\">"; // start guest type container
                slotHtml += "<h3>Guest Types</h3>";
                slotHtml += "<p class=\"small_instructions\">"+ftReplaceKeyInString(options.guestTypeInstructions,slotParms)+"</p>";
                slotHtml += "<div class=\"element_group\">";  // Start guest list element group
                slotHtml += "<select size=\"" + xCount + "\" class=\"guest_type_list\">";
                slotHtml += "</select>";
                slotHtml += "</div>";  // end guest list element group
                for (var footer_note in slotParms.guest_type_footer_notes) {
                    slotHtml += "<p class=\"small_instructions\">" + footer_note + "</p>";
                }
                slotHtml += "</div>";      // end guest type container

            }

            slotHtml += "</div>"; // end of right column
            slotHtml += "<div style=\"clear:both;\"></div>"; // end slot container
            
            $(this).append(slotHtml);

        },
       
        setOptions: function ( option ) {
            
            if(typeof(option) == "object"){
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
        } else if ( typeof(method) === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesSlot' );
        }     
    }
    
})(jQuery);