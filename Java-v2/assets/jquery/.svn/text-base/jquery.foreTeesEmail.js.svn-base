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
        mainInstructions:'<h2 class=\"altTitle\">Instructions:</h2>'
        + '<p>Select recipents that will receive this email, enter the subject and message you would like to send, then click "[options.sendEmailButton]".</p>'
        + '<p><b>Note:</b> Members without an email address registered with the system will not be available as recipients.</p>'
        + '<p>The maximum number of recipients is [options.maxRecipients].</p>',
        distListInstructions:'<h2 class="altTitle">Instructions:</h2>'
        + '<p>Click the approriate icon to edit or delete existing distribution lists.  To add a new list, select "[options.addDistributionList]"</p>',
        maxRecipients: 100,
        emailError:'Error Sending Email',
        tooManyRecipients:'Sorry, the maximum number of recipients allowed is [maxRecipients].',
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
        noResultsPartner:['Your partner list is empty.', 'Or, your selected', 'partners do not have', 'valid email addresses.', '', 'Select "Partners" from', 'the main menu to add','partners.'],
        noResultsLetter:['No members found with','a valid email address','having a last name','starting in "[last_name_list_type]".', '', 'Select a different letter.'],
        endUrl:'Member_announce',  // Where to go after we are done
        recipientAddColor:"#00FF00",  // Color to "flash" the slot field of the newly added player
        recipientAddDuration:1000,  // how long to flash the field for, in ms.
        recipientDuplicateColor:"#FFFF00", // Color to flash the slot field of a player that is already on the list
        recipientDuplicateDuration:1000,  // how long to flash the field for, in ms.
        newListBase:"New List"
    };
    
        
    var methods = {
        
        init: function(option){
        
            $(this).each(function(){
                var obj = $(this);
                var parms = obj.data("ft-emailParms");
                if(typeof parms == "undefined"){
                    parms = {};
                    obj.data("ft-emailParms",parms);
                    parms.options = options;
                }
                if(typeof option == "undefined"){
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
                
                $(this).find("option:selected").prop("selected",false);
                
                $(this).foreTeesListSearch("clear");
                $(this).foreTeesListSearch("init", {
                    name: ftReplaceKeyInString(options.nameListHeader,parms),
                    instructions: ftReplaceKeyInString(options.nameListInstructions,parms),
                    slotParms: parms,
                    noResultsPartner:options.noResultsPartner,
                    noResultsLetter:options.noResultsLetter,
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
            if(typeof parms.firstLoad == "function"){
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
                //context:obj,
                dataType: 'json',
                success: function(data){
                    //// console.log("got partner list");
                    //var obj = $(this);
                    //var parms = obj.data("ft-emailParms");
                    parms.partner_list = data;
                    obj.foreTeesEmail("displayMemberList");
                    obj.foreTeesModal("pleaseWait", "close");
                            
                },
                error: function(xhr){
                    ft_logError("Error loading member list", xhr);
                    // console.log("error recv. partner list");
                    obj.foreTeesModal("pleaseWait", "close");
                    obj.foreTeesModal("ajaxError", {
                        //context:this,
                        errorThrown:"Unable to load data",
                        tryAgain:function(option){
                            var parms = obj.data("ft-emailParms");
                            obj.foreTeesEmail("loadMemberList",parms.last_name_list_type);
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
                
                if(typeof parms.recipients[recipient.username] == "undefined"){
                    parms.recipients[recipient.username] = recipient;
                    var recipient_container = $('<div><p><a href="#" class="email_recipient" title="Remove '+recipient.display+' from recipient list">&nbsp;</a><label>'+recipient.display+'</label></p></div>');
                    parms.recipients[recipient.username].conatiner = recipient_container;
                    var recipient_button = recipient_container.find(".email_recipient");
                    recipient_button.data("ft-reciptientData",recipient);
                    recipient_container.data("ft-reciptientData",recipient);
                    recipient_button.data("ft-emailObj",obj);
                    // Find where we should be adding this recipient (alpha sort)
                    var addBefore = parms.addRecipientsObj;
                    parms.addRecipientsObj.prevAll().each(function(){
                        var recipient_data = $(this).data("ft-reciptientData");
                        if(typeof recipient_data == "object" && recipient.name < recipient_data.name){
                            addBefore = $(this);
                        }
                    });
                    
                    addBefore.before(recipient_container);
                    recipient_button.click(function(){
                        var obj = $(this).data("ft-emailObj");
                        var parms = obj.data("ft-emailParms");
                        var recipient = $(this).data("ft-reciptientData");
                        if(typeof parms.recipients[recipient.username] != "undefined"){
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
            var popUp = parms.addRecipientsObj.find('ul ul');
            popUp.on('ft-hide',function(){
                var el = $(this);
                el.css('display','none');
                setTimeout(function(){el.css('display','')},100)
            });
            // Disable clicking on menu tab
            listContainer.find("ul a").first().click(function(e){
                e.preventDefault(); 
            });
            listContainer.find("ul a.select_members").click(function(e){
                obj.foreTeesModal("getMemberRecipients");
                popUp.trigger('ft-hide');
                e.preventDefault(); 
            });
            listContainer.find("ul a.select_distribution_list").click(function(e){
                obj.foreTeesModal("getMemberDistributionList");
                popUp.trigger('ft-hide');
                e.preventDefault(); 
            });
            listContainer.find("ul a.add_partners").click(function(e){
                var url = "data_loader?json_mode=true&email&name_list=true&letter=partners";
                // // console.log(url);
            
                // Block user input
                var el = $(this);
                obj.foreTeesModal("pleaseWait");
                // Update list of partners
                $.ajax({
                    url: url,
                    //context:$(this),
                    dataType: 'json',
                    success: function(data){
                        obj.foreTeesModal("pleaseWait", "close");
                        obj.foreTeesEmail("addRecipients", data);
                            
                    },
                    error: function(xhr){
                        obj.foreTeesModal("pleaseWait", "close");
                        // console.log("error recv. partner list");
                        ft_logError("Unable to load recipient list", xhr);
                        obj.foreTeesModal("ajaxError", {
                            //context:this,
                            errorThrown:"Unable to load data",
                            tryAgain:function(){
                                el.click();
                            },
                            allowRefresh: true
                        });
                    }
                });
                popUp.trigger('ft-hide');
                e.preventDefault();
            });
            ftActivateElements(obj);
            
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
                    //context:obj,
                    dataType: 'json',
                    data: form_data,
                    type: 'POST',
                    success: function(data){
                        //var obj = $(this);
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
                        //var obj = $(this);
                        obj.foreTeesModal("ajaxError", {
                            //context:obj,
                            errorThrown:"Unable to send email",
                            tryAgain:function(option){
                                obj.foreTeesEmail("sendEmail");
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
            window.location = ftSetJsid(options.endUrl);
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

            headHtml += '<div class="main_instructions pageHelp" data-fthelptitle="Instructions">';
            headHtml += ftReplaceKeyInString(options.mainInstructions,parms);
            headHtml += "</div>";
            
            obj.before(headHtml);
            var html = "";
            html += '<fieldset class="standard_fieldset"><legend>'+options.recipentTitle+'</legend>';
            html += '<div class="reciptient_container"></div></fieldset>';
            html += '<fieldset class="standard_fieldset"><legend>'+options.subjectTitle+'</legend>';
            html += '<input type="text" name="subject"></fieldset>';
            html += '<fieldset class="standard_fieldset"><legend>'+options.messageTitle+'</legend>';
            html += '<textarea name="message"></textarea></fieldset>';
            
            html += '<div class="button_container"><a class="go_back_button" href="#">'+options.cancelButton+'</a><a class="submit_request_button" href="#">'+options.sendEmailButton+'</a></div>';
            
            obj.addClass("sub_main_tan");
            obj.append(html);
            ftActivateElements(obj);

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
                    if(typeof option.onComplete == "function"){
                        option.onComplete(option);
                    }
                },
                error: function(xhr){
                    option = this;
                    ft_logError("Enable to delete distribution list", xhr);
                    obj.foreTeesModal("pleaseWait", "close");
                    if(typeof option.onComplete == "function"){
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
                        if(typeof parms.listOptions == "object" && typeof parms.listOptions.onSaveComplete == "function"){
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

            var listNumber = 0;
            var listName = options.newListBase;
            var foundName = false;
            
            while(!foundName){
                if($.inArray(listName,parms.distribution_lists) > -1){
                    listNumber ++;
                    listName = options.newListBase + " " + listNumber;
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

                headHtml += '<div class="main_instructions pageHelp\" data-fthelptitle="Instructions">';
                headHtml += ftReplaceKeyInString(options.distListInstructions,parms);
                headHtml += "</div>";
                
                var headObj = $(headHtml);
            
                obj.before(headObj);
                ftActivateElements(headObj.parent());
            }
  
            var url = "data_loader?json_mode=true&email&dist_list_names";
            
            //obj.foreTeesModal("pleaseWait");
                
            $.ajax({  
                url: url,
                //context:obj,
                dataType: 'json',
                success: function(data){
                    //// console.log("got list");
                    //var obj = $(this);
                    var parms = obj.data("ft-emailParms");
                    parms.distribution_lists = data;
                    obj.foreTeesEmail("displayMemberList");
                    //obj.foreTeesModal("pleaseWait", "close");

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
                                        modalObj.ftDialog("close");
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
                                                option.context.ftDialog("close");
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
                            baseName:options.newListBase,
                            title:'New Distribution List',
                            addButton:'Save List',
                            newListMode: true,
                            callbackAction: function(){
                                $(this).foreTeesEmail("saveDistributionList");
                            },
                            onSaveComplete: function(modalObj){
                                var obj = modalObj.data("ft-modalParent");
                                modalObj.ftDialog("close");
                                obj.foreTeesEmail("generateDistributionListForm");
                            }
                        });
                        return false;
                    });
                    listContainer.append(addListObj);
                            
                },
                error: function(xhr){
                     //console.log("error recv. partner list");
                    ft_logError("Unable to load distribution list names", xhr);
                
                // 
                }
            });
        },

        setOptions: function ( option ) {

            if(typeof option == "object"){
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
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesEmail' );
        }     
    }
    
})(jQuery);
