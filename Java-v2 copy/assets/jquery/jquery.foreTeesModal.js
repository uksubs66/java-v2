/*************************************************************
 *
 * foreTeesModal plugin
 *  
 *  2/28/14  Add validation check for making sure the user selcts a recurrence option when updating a recurring lottery request
 *  1/06/14  Additions for use with FlxRez alternate time searching.
 * 10/11/12  Fixed issue with event info/registrations modal windows where the registrations page Sign Up button wasn't being controlled by use_signup_button. 
 *  6/20/12  BP - Tweak some error message titles and add some help info to 
 *                assist members in resolving an IE problem.
 *  
 *  
 *************************************************************/

(function($){
    
    var pluginData = {
        
        jsonRunnin:false,
        jsonData:{}

    };
    
    var options = {
        
        events: {
            onCreate: function(){

            },
            onOpen: function(){

            },
            onCloase: function(){

            }
        },
        
        pleaseWait: {
            width:200,
            height:200,
            title:"Please Wait..."
        },
        waitListPrompt: {
            width:620,
            height:400,
            title:"Member Wait List Registration",
            instructionsHead:"Wait List Registration:",
            instructions:"The golf shop is running a wait list [day]. The wait list you've selected is running from [start_time] till [end_time].",
            onlist:"You are already signed up for this wait list.",
            contactModify:"Contact the golf shop to make changes or cancel your entry.",
            contactList:"Contact the golf shop to get on the wait list.",
            errorMessageTitle:"Unable to load the Wait List:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.",
            dateFormat:"dddd m/d/yyyy" 
            
        },
        waitList: {
            width:820,
            height:400,
            title:"Wait List Sign-ups",
            listName:'<i>"[list_name]"</i>',
            listDate:'<i>List Generated on [report_date]</i>',
            errorMessageTitle:"Unable to load the Wait List:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.",
            dateFormat:"dddd m/d/yyyy" 
            
        },
        lotteryPrompt: {
            width:620,
            height:400,
            titleLoading:"Please Wait...", // Title of modal, before JSON request has loaded
            title:"[lottery_text] Registration",
            instructionsHead:"For [long_date]:",
            instructions:'To create a new request, select "Create New Request" button below.',
            totalRegistrations:"Other Requests:",
            viewButton:"View Other Requests",
            viewEditButton:"View or Edit Other Requests",
            newRequestButton:"Create New Request",
            errorWindowTitle:"Error",
            errorMessageTitle:"Unable to load page:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this.",
            dateFormat:"dddd m/d/yyyy", 
            showViewEditButton:true
        },
        lotteryList: {
            width:800,
            height:400,
            title:"Current [lottery_text]s",
            instructionsHead:"For [long_date]:",
            selectLottery:'<b>To select a [lottery_text]:</b> Click on the box containing the time (1st column).',
            noSelectNote:'<b>Note:</b> If the request is full and you are not already included, you will not be allowed to select it.',
            frontBackLegend:'<b>F/B Legend:</b> <span>F = Front 9,</span> <span>B = Back 9,</span> <span>O = Other</span>',
            frontBackDecode:{
                "0":"F", 
                "1":"B", 
                "9":"O"
            },
            dateFormat:"dddd m/d/yyyy" 
        //  Decode for the F/B parm    0 = Front 9, 1 = Back 9, 9 = none (open for cross-over)
            
        },
        eventPrompt: {
            width:780,
            height:400,
            title:"[event_text] Registration",
            registered:'<b>You are registered for this event.</b>',
            unregistered:'<b>You are not registered for this event.</b>',
            viewButton:"View Current Registrations",
            viewEditButton:"View or Edit Current Registrations",
            selectButtonInstructions: 'the "Select" button in the table below',
            selectButton:"Select",
            sign_up_button:"Sign Up",
            new_team_button:'New Team',
            errorWindowTitle:"Error",
            errorMessageTitle:"Unable to load page:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        eventList: {
            width:800,
            height:400,
            title:"Current [event_name] Registrations"   
        },
        
        linkModal: {
            width:780,
            height:400,
            title:"",
            closeButton:"Close",
            loadingTitle:"Please Wait"
        },
        
        guestDbPrompt: {
            width:780,
            height:400,
            minGuestListHeight:200,
            maxGuestListHeight:300,
            title:"Guest Registration",
            clickGuestSelect: "Click on name to select",
            addGuestButton:"Add Guest",
            cancelButton:"Cancel",
            searchPrompt:"Search Guests",
            searchName:"Guests:",
            noSearchResults:["No results found:", "Click the clear button", "to try again"],
            noResults:["No guests in database:", "Use the form on the left", "to create a new guest."],
            errorWindowTitle:"Error",
            errorMessageTitle:"Unable to load page:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        
        guestDbSubmit: {
            width:600,
            height:400,
            loadingTitle:"Please Wait...",
            successTitle:"Guest Registration",
            failTitle:"Guest Registration Error",
            continueButton:"Continue",
            closeButton:"Close",
            errorWindowTitle:"Error adding guest",
            errorMessageTitle:"Unable to load page:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        
        slotSubmit: {
            width:650,
            height:300,
            loadingTitle:"Please Wait...",
            continueButton:"Continue",
            closeButton:"Close",
            errorMessageTitle:"Unable to submit:",
            yesContinueButton:"Yes, Continue",
            noGoBackButton:"No, Go Back",
            
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again."
        },
        
        slotCancelPrompt: {
            width:650,
            height:300,
            title:"Cancel [signup_type] Confirmation",
            continueButton:"Continue",
            closeButton:"Return",
            promptMessages:[
            'This will remove <b>ALL players</b> from this [signup_type].<br>If this is what you want to do, then click on "Continue" below.',
            'If you only want to remove yourself, or a portion of the players, click on "Return" below. Then use the "erase" and "Submit" buttons to remove only those players you wish to remove.'             
            ]
        },
        
        slotPageLoadNotification: {
            width:650,
            height:400,
            //title:"Cancel [slot_type] Confirmation",
            continueButton:"Continue",
            acceptButton:"Yes, Continue",
            cancelButton:"Cancel Request",
            goBackButton:"Go Back",

            errorMessageTitle:"Unable to load page:",
            errorMessage:"This may be caused by your session timing out, or a network connection issue. Please login and try again.  NOTICE:  If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this."
        },
        
        slotTimeout: {
            width:550,
            height:300,
            title:"Reservation timer expired",
            continueButton:"Continue",
            message:'Sorry, your reservation timer has expired.<br><br>Please select "[modalOptions.slotTimeout.continueButton]" to return and select another time.'
        },
        
        calendarError: {
            width:350,
            height:200,
            title:"Error Refreshing Calendar",
            message1:"Unable to update Calendars.",
            message2:"This may be caused from your session timing out, or a network connection issue.",
            tryAgainButton:"Try Again",
            refreshPageButton:'Refresh Page'
        },
        
        getMemberRecipients: {
            width:800,
            height:300,
            title:"Search For Members",
            instructions: '<h3>Instructions:</h3><p>To add recipients, select the first letter of the member\'s last name from the table of letters, then click the name in the list on the right.</p>'
            +'<p>When finished, click "[addButton]".</p>',
            addButton:"Add Recipients",
            cancelButton:'Cancel',
            callbackAction: function(){
                var modalObj = $(this);
                var obj = $(this).data("ft-modalParent");
                var parms = modalObj.data("ft-emailParms");
                obj.foreTeesEmail("addRecipients", parms.recipients);
                $(this).ftDialog("close");
            }
        },
        
        getMemberDistributionList: {
            width:700,
            height:300,
            title:"Distribution Lists",
            instructions: 'Select the lists you would like to send your email to and click "[addButton]"',
            addButton:"Add Selected Lists",
            manageButton:"Manage Distribution Lists",
            cancelButton:'Cancel'
        },
        
        manageMemberDistributionLists: {
            width:600,
            height:300,
            title:"Manage Distribution Lists",
            closeButton:'Close'
        },
        
        ajaxError: {
            width:400,
            height:200,
            title:"[errorThrown]",
            message1:"Error loading data: [errorThrown]",
            message2:"This may be caused from your session timing out, or a network connection issue.",
            tryAgainButton:"Try Again",
            refreshPageButton:'Refresh Page',
            goBackButton:'Go Back'
            
        },
        
        alertNotice: {
            width:400,
            height:200,
            title:"Alert",
            alertMode: true,
            message_head:"Notice",
            message:"",
            allowClose:true,
            closeButton:"Close",
            tryAgainButton:"Try Again",
            refreshPageButton:'Refresh Page',
            goBackButton:'Go Back',
            continueButton: 'Continue',
            continueAction: function(modalObj){
                modalObj.ftDialog("close");
                ft_historyBack();
            }
        },
        
        htmlBlock: {
            width:500,
            height:200,
            title:"Notice",
            closeButton: 'Close',
            url: "error",
            data: {}
        },
        
        timeoutError: {
            width:700,
            height:400,
            title:"Timeout Error",
            message1:"Communication seems to be taking longer than expected.",
            message2:"If you have mistakenly received this error, and you are using Internet Explorer, please click on the Help? menu item at the top of the ForeTees page for instructions on how to correct this.",
            tryAgainButton:"Continue to Wait",
            goBackButton:'Go Back'
            
        }
        
    };
    
    var methods = {
        
        init: function(option){
        // By default, do nothing

        },
        
        initActivity: function(option){
            // Remove the close "X" button from the modal
            $(this).parent().find(".ui-dialog-titlebar-close").first().hide();
            if(typeof option =='object' && option.noTitlebar){
                $(this).parent().find(".ui-dialog-titlebar").first().remove();
            }
            // Create the activity indecator
            if (typeof($(this).parent().data("ft-activityIndicator")) != "object"){
                var activityObj = $('<div class="pleaseWaitActivityIndicator" style="position:absolute; top:0; right:0; bottom:0; left:0;"></div>');
                $(this).parent().append(activityObj);
                $(this).parent().data("ft-activityIndicator", activityObj);
            }  
        },
        
        showActivity: function(option){
            $(this).parent().find(".ui-dialog-titlebar-close").hide();
            $(this).parent().data("ft-activityIndicator").show();
            $(this).parent().data("ft-activityIndicator").activity();  
            $(this).ftDialog({
                closeOnEscape: false
            });
        },
        
        hideActivity: function(option){
            $(this).parent().data("ft-activityIndicator").activity("stop");
            $(this).parent().data("ft-activityIndicator").hide();
            $(this).parent().find(".ui-dialog-titlebar-close").show();
            $(this).ftDialog({
                closeOnEscape: true
            });

        },
        
        hideActivityOnly: function(option){
            $(this).parent().data("ft-activityIndicator").activity("stop");
            $(this).parent().data("ft-activityIndicator").hide();
        //$(this).parent().find(".ui-dialog-titlebar-close").show();

        },
        
        showTitleClose: function(option){
            $(this).parent().find(".ui-dialog-titlebar-close").show();
        },
        
        triggerResize: function(option){
            $(this).foreTeesModal("resize");
            $(this).foreTeesModal("resizeTimer");
        },
        
        resizeTimer: function(option){
            
            var timeOutCount = 0;
            
            // Check if this was just a zoom on a mobile device.  If so, skip the resize
            // We detect by checking if the window aspect ratio is the same
            var currentAspectRatio = Math.round((window.innerHeight / window.innerWidth)*100)/100;
            var lastAspectRatio = 0;
            var timerMs = 20;
            if(ftIsMobile("android")){
                // Android needs a little longer
                timerMs = 500;
            }
            if(typeof($(this).data("ft-resizeWindowLastAspectRatio")) != "undefined"){
                lastAspectRatio = $(this).data("ft-resizeWindowLastAspectRatio");
            }
            // console.log("AspectRatio:"+lastAspectRatio+":"+currentAspectRatio);
            if(lastAspectRatio != currentAspectRatio){
                if(typeof($(this).data("ft-resizeTimeoutCount")) != "undefined"){
                    timeOutCount = $(this).data("ft-resizeTimeoutCount");
                }
                /*
                timeOutCount ++;
                $(this).data("ft-resizeTimeoutCount", timeOutCount);
                setTimeout($.proxy(function(){
                    $(this).foreTeesModal("resizeTimeout");
                },$(this)),timerMs);
                */
               var currentTimer = $(this).data("ft-resizeTimeout");
               if(currentTimer){
                   clearTimeout(currentTimer);
               }
               var currentTimer = $(this).data("ft-resizeTimeout", 
                    setTimeout($.proxy(function(){
                        $(this).foreTeesModal("resizeTimeout");
                    },$(this)),timerMs)
                );
                
            }
            $(this).data("ft-resizeWindowLastAspectRatio", currentAspectRatio);
            
        },
        
        resizeTimeout: function(option){
            
            //var timeOutCount = $(this).data("ft-resizeTimeoutCount");
            //timeOutCount --;
            //if(timeOutCount >= 0){
            //    $(this).data("ft-resizeTimeoutCount", timeOutCount);
            //}
            //if(timeOutCount == 0){
            //console.log('rstime');
                $(this).foreTeesModal("resize");
            //}
            
        },
        
        resize: function(option){
    
            var width = $(this).data("ft-width");
            var height = $(this).data("ft-height");
            var oldDisplay = $(this).parent().css("display");
            var type = $(this).data("ft-modalType");
            $(this).parent().css("display", "none");
            if(ftIsMobile("ios") || ftIsMobile("android") || ftIsMobile("blackberry")){
                var windowWidth = window.innerWidth;
                var windowHeight = window.innerHeight;
            }else{
                var windowWidth = $(window).innerWidth();
                var windowHeight = $(window).innerHeight();
            
            }
            var docHeight = $(document).innerHeight();
            var bodyHeight = $('body').innerHeight();
            var pageX = $(window).scrollLeft();
            var pageY = $(window).scrollTop();
            var offsetY = 0;
            var padBody = false;
            $(this).parent().css("display", oldDisplay);
            var modalHeight = ($(this).parent().outerHeight()) + 5;
            if(modalHeight < height){
                modalHeight = height;
            }
            // Set modal position differently if we are in an iframe
            // and we are able to use postMessage
            if(parent != window && window.postMessage){
                padBody = true;
                offsetY = 0-parentSize.offsetTop;
                pageY = (parentSize.scrollTop);
                windowHeight = parentSize.height;
            } else if(parent != window){
                // If we're in an iframe, but dont support postMessage,
                // Just allow the positioner to resize the iframe, if needed
                padBody = true;
            }
            
            var minHeight = ((windowHeight < height)? windowHeight : height);
            //var minWidth = ((windowWidth < width)? windowWidth : width);
            //var maxHeight = windowHeight;
            //var maxWidth = windowWidth;
            
            $(this).ftDialog("option",{
                //position:"center", 
                width:width, 
                minHeight:minHeight
            });
            // Center the window
            var parent_width = $(this).parent().outerWidth();
            var parent_height = $(this).parent().outerHeight();
            

            var top = (windowHeight - parent_height) / 2;
            //var topO = top;
            //var topB = "null";
            if((top + parent_height) > windowHeight){ 
                top = top - ((top + parent_height) - windowHeight);
            //topB = top;
                
            }
            //console.log({docHeight:docHeight,bodyHeight:bodyHeight});
            var newTop = top + pageY + offsetY;
            if((newTop + parent_height) > bodyHeight + iFramePad){
                newTop = (docHeight - parent_height) - 1;
            }
            if(newTop < 0){
                newTop = 0;
            }
            if((newTop + parent_height) > bodyHeight + iFramePad && padBody){
                //console.log('Modal too large for body');
                ftModalPads[type] = ((newTop + parent_height) - (bodyHeight+iFramePad))+1;
                ftSetMaxIframePad();
            } else {
                ftModalPads[type] = 0;
                ftSetMaxIframePad();
            }
            
            var left = (windowWidth - parent_width) / 2;
            if((left + parent_width) > windowWidth){ 
                left = left - ((left + parent_width) - windowWidth);
            }
            if((left + pageX) < 0){ 
                left = 0;
            }else{
                left += pageX;
            }
            $(this).parent().offset({
                top: newTop,
                left: left
            });
        },
        
        error: function(option, option2){
            var buttons = {
                "Close": function(){
                    $(this).ftDialog("close");
                }
            }
            var modalObj;
            var obj;
            if(typeof($(this).data("ft-modalParent")) != "object"){
                modalObj = $(this).data("ft-"+option+"Obj");
                //// console.log("find child:" + option);
                if(typeof modalObj != "object"){
                //// console.log("Cannot find modal object");
                }
                obj = $(this);
            }else{
                //// console.log("find parent");
                modalObj = $(this);
                obj = $(this).data("ft-modalParent");
            }
            modalObj.foreTeesModal("hideActivity");
            modalObj.empty();
            var message = '<div class="sub_instructions">' +
            '<h2>'+options[option].errorMessageTitle+'</h2>';
            if(typeof option2 == "string" && option2.length){
                message += '<p>'+option2+'</p>';
            }else{
                message += '<p>'+options[option].errorMessage+'</p>';
                buttons["Reload Page"] = function(){
                    window.location.reload();
                }
                buttons["Try Again"] = jQuery.proxy(function(){
                    var modalObj = $(this);
                    var obj = modalObj.data("ft-modalParent");
                    modalObj.ftDialog("close");
                    obj.foreTeesModal(option);
                },modalObj);
            }
            message += '</div>';
            modalObj.append(message);
                        
            modalObj.ftDialog("option",{
                        
                title: options[option].errorWindowTitle,
                buttons: buttons

            });  
        },
        
        getModalObject: function (option){
            
            var obj = $(this);
            if (typeof(obj.data("ft-"+option+"Obj")) != "object"){
                var modalObj = $('<div class="modal_list '+option+'_container"></div>');
                $("body").append(modalObj);
                obj.data("ft-"+option+"Obj", modalObj);
            } else {
                var modalObj = obj.data("ft-"+option+"Obj");
            }
            modalObj.empty();
            modalObj.data("ft-modalParent",obj);
            modalObj.data("ft-modalType",option);
            
            return modalObj;
            
        },
        
        initModalObject: function (title, option_override){
            
            var modalObj = $(this);
            var option = modalObj.data("ft-modalType");
            //var obj = $(this).data("ft-modalParent");
            var options_obj = options[option];
            options_obj.iframeAction = false;
            if(typeof option_override == "object"){
                options_obj = $.extend({}, options_obj, option_override);
            }
            var width = options_obj.width;
            var height = options_obj.height;
            modalObj.data("ft-width",width);
            modalObj.data("ft-height",height);
            var width = modalObj.data("ft-width");
            var height = modalObj.data("ft-height");
            var minHeight = (($(window).innerHeight() < height)? $(window).innerHeight() : height);
            var minWidth = (($(window).innerWidth() < width)? $(window).innerWidth() : width);
            var maxHeight = $(window).innerHeight();
            var maxWidth = $(window).innerWidth();
            
            modalObj.ftDialog({
                resizable: false,
                modal:true,
                width:width,
                minHeight:minHeight,
                autoOpen: false,
                title:title,
                create:function(){
                    //console.log('create');
                    // Remove the close "X" button from the modal
                    //$(this).foreTeesModal("triggerEvent","onCreate");
                    $(this).foreTeesModal("initActivity",options_obj);
                    //$(this).foreTeesModal("triggerResize");
                    //setTimeout(function(){$(window).trigger('resize');},50);
                },
                focus:function(){
                    if(!$(this).data('didFocus')){
                        // If this is the first focus since the modal was opened,
                        // resize/reposition the modal to ensure it is in the correct location.
                        $(this).data('didFocus',true);
                        $(this).foreTeesModal("resize");
                    }                    
                },
                open:function(){
                    //console.log('open');
                    // Display the activity indicator
                    var mobj = $(this);
                    //console.log(mobj);
                    mobj.foreTeesModal("triggerEvent","onOpen");
                    mobj.foreTeesModal("showActivity");
                    mobj.foreTeesModal("bindResize");
                    mobj.foreTeesModal("triggerResize");
                    var parent = mobj.closest('.ui-dialog');
                    if(parent.length){
                        //console.log("found parent");
                        parent.siblings('.ui-widget-overlay').each(function(){
                            //console.log("test...");
                            var overlay = $(this);
                            if(overlay.length && !overlay.data('ftInit')){
                                overlay.data('ftInit', mobj);
                                overlay.click(function(){
                                    //console.log('overlay_click');
                                    var ftModal = overlay.data('ftInit');
                                    ftModal.foreTeesModal("triggerResize");
                                    $(window).trigger("ftModalCenter");
                                });
                            }
                            
                        });
                        /*
                        if(overlay.length && !overlay.data('ftInit')){
                            overlay.data('ftInit', $(this));
                            overlay.click(function(){
                                //console.log('overlay_click');
                                var ftModal = overlay.data('ftInit');
                                ftModal.foreTeesModal("triggerResize");
                                $(window).trigger("ftModalCenter");
                            });
                        }
                        */
                    }
                    
                    setTimeout(function(){$(window).trigger('resize');},50);
                    
                },
                close:function(){
                    // Stop the activity indicator
                    delete ftModalPads[$(this).data("ft-modalType")];
                    $(this).data('didFocus',false);
                    $(this).foreTeesModal("triggerEvent","onClose");
                    $(window).trigger("ftModalCenter");
                    $(this).foreTeesModal("hideActivity");
                    $(this).foreTeesModal("unbindResize");
                    ftSetMaxIframePad();
                    $('.ui-widget-overlay').click();
                    //$('body').css('height','auto');
                //$(this).ftDialog("destroy");
                },
                buttons:{}
            });
            
            if(options_obj.iframeAction){
                //console.log('enable close iframe on exit');
                modalObj.on("dialogbeforeclose",function(){
                    //console.log('closing iframe');
                    ftIframeAction(options_obj.iframeAction);
                });
            }
            
            modalObj.empty();
                    
                    
        },
        
        triggerEvent: function(event){
            if(typeof options.events[event] == "function"){
                options.events[event]();
            }
        },
        
        bindResize: function(){
            var option = $(this).data("ft-modalType");
            // Keep the modal centered on resize
            $(window).bind('resize.ft-'+option,(jQuery.proxy(function(){
                $(this).foreTeesModal("resizeTimer");
            },$(this))));
            //  Re-center on orientation change on Android
            if("onorientationchange" in window){
                $(window).bind('orientationchange.ft-'+option,(jQuery.proxy(function(){
                    //console.log("change");
                    $(this).foreTeesModal("resizeTimer");
                },$(this))));
            }
            $(window).bind('ftModalCenter.ft-'+option,(jQuery.proxy(function(){
                $(this).foreTeesModal("resizeTimer");
            },$(this))));
            $(window).bind('ftModalForceCenter.ft-'+option,(jQuery.proxy(function(){
                //console.log('force resize...');
                //console.log(option);
                $(this).foreTeesModal("resize");
            },$(this))));
            
        },
        
        unbindResize: function(){
            
            var option = $(this).data("ft-modalType");
            $(window).unbind('resize.ft-'+option);
            $(window).unbind('ftModalCenter.ft-'+option);
            $(window).unbind('ftModalForceCenter.ft-'+option);
            if("onorientationchange" in window){
                $(window).unbind('orientationchange.ft-'+option);
            }
            
        },
        
        bindForm: function(formObj, data){
            
            // Bind to changable elements
            var formElem = formObj.find("select, input, textarea, checkbox, radio, password");
            formElem.data("ft-formData", data);
            formElem.each(function(){
                $(this).change(function(){
                    var obj = $(this);
                    var formData = obj.data("ft-formData");
                    // A form element may conatin multiple data parameters, delimited by a "|";
                    var val = obj.val();
                    var name = obj.attr("name");
                    //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
                    if(obj.is(":radio") && obj.is(":checkbox")){
                        val = obj.closest("form").find(obj.prop('tagName')+'[name='+name+']:checked').val();
                    }
                    var values = val.split(/\|/g);
                    var names = name.split(/\|/g);
                    if(names.length == values.length){
                        //console.log("setting values");
                        for(var i in names){
                            formData[names[i]] = values[i];
                        }
                    }else{
                        formData[name] = val;
                    }
                });
                $(this).change(); // trigger change event to set default in data object
            });
            // Bind to clickable elements
            formElem = formObj.find('button, input[type="button"]');
            formElem.data("ft-formData", data);
            formElem.each(function(){
                $(this).click(function(){
                    var obj = $(this);
                    var formData = obj.data("ft-formData");
                    // A form element may conatin multiple data parameters, delimited by a "|";
                    var values = obj.val().split(/\|/g);
                    var names = obj.attr("name").split(/\|/g);
                    //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
                    if(names.length == values.length){
                        //console.log("setting values");
                        for(var i in names){
                            formData[names[i]] = values[i];
                        }
                    }else{
                        formData[obj.attr("name")] = obj.val();
                    }
                    return false;
                });
                $(this).change(); // trigger change event to set default in data object
            });
            
            return formObj;
        },
        
        buildForm: function(fields, data, parms){
            var formObj = $("<form></form>");
            formObj.data("ft-resubmit-page", function(context){
                
            });
            var current_container = formObj;
            var current_block = formObj;
            for(var key in fields){
                var type_a = fields[key]["type"].split(/_/g);
                var type = type_a[0];
                var disabled = "";
                var force_store = false;
                if(type_a[1] == "disabled"){
                    disabled =  'disabled="disabled" ';
                    force_store = true;
                }
                var update_target = false;
                var lable_text = ftReplaceKeyInString(fields[key]["name"],parms);
                var label = '<label for="'+key+'">' + lable_text + ':</label>';
                var value = fields[key]["value"];
                //console.log(key+":"+type+":"+value);
                var sol = '<p class="field_container">';
                var eol = "</p>";
                var field = "";
                var eclass = "";
                var maxlen = "";
                var size = "";
                var checked = "";
                var required = "";
                //console.log("Field:"+key+";value:"+value);
                if(fields[key]["class"].length > 0){
                    eclass = ' class="'+fields[key]["class"]+'"'; 
                }
                if(fields[key]["maxlen"] > 0){
                    maxlen = ' maxlength="'+fields[key]["maxlen"]+'"'; 
                }
                if(fields[key]["size"] > 0){
                    size = ' size="'+fields[key]["size"]+'"'; 
                }
                if(fields[key]["value"] == "1"){
                    checked = " checked";
                }
                if(fields[key]["required"]){
                    required = " <span class=\"require_notice\">*</span>";
                }
                switch(type){
                    
                    case "fieldset":
                        var object = $('<fieldset'+eclass+'></fieldset>');
                        current_block.append(object);
                        current_container = object;
                        current_container.append('<ledgend>'+lable_text+'</ledgend>');
                        break;
                        
                    case "fieldblock":
                        var object = $('<div'+eclass+'><h3>'+lable_text+'</h3></div>');
                        formObj.append(object);
                        current_block = object;
                        current_container = object;
                        break;
                        
                    case "messageblock":
                        var object = $('<div'+eclass+'><h4>'+lable_text+'</h4>'+ftReplaceKeyInString(value,parms)+'</div>');
                        current_block.append(object);
                        break;
                    
                    case "hidden":
                        force_store = true;
                        label = "";
                        sol = "";
                        required = "";
                        eol = "";
                        field = '<input type="hidden" name="'+key+'" value="'+value+'">';
                        break;
                        
                    case "display":
                        force_store = true;
                        field = '<input disabled="disabled" type="text" name="'+key+'" value="'+value+'"'+size+''+maxlen+''+eclass+'>';
                        break;
                                        
                    case "text":
                        field = '<input '+disabled+'type="text" name="'+key+'" value="'+value+'"'+size+''+maxlen+''+eclass+'>';
                        break;
                                        
                    case "checkbox":
                        field = '<input '+disabled+'type="checkbox" name="'+key+'" value="'+value+'"'+eclass+''+checked+'>';
                        break;
                                        
                    case "select":
                        field = $('<select '+disabled+'name="'+key+'"'+eclass+'></select>');
                        for(var option_name in fields[key]["options"]){
                            var option_value = fields[key]["options"][option_name];
                            var option_target = false;
                            if(typeof option_value == "object"){
                                option_target = option_value;
                                option_value = option_target['value'];
                            }
                            var selected = "";
                            if(option_value == value){
                                selected = " selected";
                            }
                            var option_obj = $('<option value="'+option_value+'"'+selected+'>'+option_name+'</option>');
                            if(option_target){
                                option_obj.data("ft-changeTarget", option_target);
                                update_target = true;
                            }
                            field.append(option_obj);
                        }
                        break;
                }
                if(field.length > 0){
                    var parmObj = $(sol+label+eol);
                    var fieldObj = $(field);
                    if(typeof data == "object"){
                        //If a data object was passed, set the on-change event of the form element
                        //to set the related parameter in the data object
                        if(!force_store){
                            fieldObj.data("ft-formData", data);
                            fieldObj.change(function(){
                                var obj = $(this);
                                var formData = obj.data("ft-formData");
                                // A form element may conatin multiple data parameters, delimited by a "|";
                                var values = obj.val().split(/\|/g);
                                var names = obj.attr("name").split(/\|/g);
                                //console.log("n/v:"+obj.attr("name")+"!"+obj.val()+"!"+names.length+"!"+values.length);
                                if(names.length == values.length){
                                    //console.log("setting values");
                                    for(var i in names){
                                        formData[names[i]] = values[i];
                                    }
                                }else{
                                    formData[obj.attr("name")] = obj.val();
                                }
                            });
                            fieldObj.change(); // trigger change event to set default in data object
                        }else{
                            data[key] = value; // set default in data object
                        }
                    }
                    if(update_target){
                        fieldObj.change(function(){
                            var obj = $(this);
                            //console.log("trigger:"+obj.attr('name'));
                            var option_obj = obj.find('option:selected');
                            //console.log("option:"+option_obj.val());
                            var target_data = option_obj.data("ft-changeTarget");
                            var target_obj = obj.parents('form').first().find('[name="'+target_data['name']+'"]');
                            //console.log("found:"+target_obj.attr('name'));
                            var selected = "";
                            if(target_obj.length){
                                target_obj.find('option').remove();
                                for(var option_name in target_data["data"]){
                                    var option_value = target_data["data"][option_name];
                                    //console.log("option:"+option_value+";default:"+target_data["default"]+";");
                                    selected = "";
                                    if(option_value == target_data["default"]){
                                        selected = " selected";
                                        //console.log("selected");
                                    }
                                    var option_obj = $('<option value="'+option_value+'"'+selected+'>'+option_name+'</option>');
                                    target_obj.append(option_obj);
                                    target_obj.change();
                                }
                            }
                        });
                    }
                    parmObj.append(fieldObj);
                    parmObj.append(required);
                    current_container.append(parmObj);
                }
            }
            return formObj;  
        },
          
        htmlBlock: function (new_option) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.htmlBlock, new_option);
            } else {
                option = $.extend({}, options.htmlBlock);
                option.action = new_option;
            }
            
            var obj = $(this);
            obj.data("ft-htmlBlockModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","htmlBlock");
            
            option.options = options;
            
            switch (option.action) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option), option);
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    
                    $.ajax({  
                        url: option.url,
                        //context:obj,
                        data: option.data,
                        dataType: 'html',
                        success: function(data){
                            //// console.log("got partner list");
                            //var obj = $(this);
                            var modalObj = obj.data("ft-htmlBlockObj");
                            modalObj.foreTeesModal("hideActivity");
                            modalObj.append(data);
                            // Reposition and resize the window
                            modalObj.foreTeesModal("triggerResize");
                        },
                        error: function(jqXHR) {
                            var errorText = "Error opening window.  Please try again later.";
                            ft_logError(errorText, jqXHR);
                            //var obj = $(this);
                            var modalObj = obj.data("ft-htmlBlockObj");
                            modalObj.foreTeesModal("hideActivity");
                            modalObj.append(errorText);
                            // Reposition and resize the window
                            modalObj.foreTeesModal("triggerResize");
                        }
                    });
                    
                    var buttons = {};
                    
                    buttons[ftReplaceKeyInString(option.closeButton, option)] = function(){
                        $(this).ftDialog("close");
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        alertNotice: function (new_option) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.alertNotice, new_option);
            } else {
                option = $.extend({}, options.alertNotice);
                option.action = new_option;
            }
            
            var obj = $(this);
            obj.data("ft-alertNoticeModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","alertNotice");
            
            option.options = options;
            
            switch (option.action) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    if(option.allowClose){
                        modalObj.foreTeesModal("hideActivity");
                    } else {
                        modalObj.foreTeesModal("hideActivityOnly");
                    }
                    var message = "";
                    if(option.alertMode){
                        var message_ext = ftReplaceKeyInString(option.message, option);
                    
                        if(message_ext.length > 0){
                            message_ext = '<br><p>'+message_ext+'</p>'
                        }
                    
                        
                        message += '<div class="sub_instructions"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(option.message_head, option)+'</p>'+message_ext+'</div>';
                    } else {
                        message += '<div class="main_instructions">'+ftReplaceKeyInString(option.message, option)+'</div>' 
                    }
                    modalObj.append(message);

                    var buttons = {};

                    if(typeof option.tryAgain == "function"){
                        buttons[ftReplaceKeyInString(option.tryAgainButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            option = obj.data("ft-alertNoticeModalData");
                            $(this).ftDialog("close");
                            option.tryAgain(option);
                        }
                    }
                    
                    if(option.allowContinue && typeof option.continueAction == "function"){
                        buttons[ftReplaceKeyInString(option.continueButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            option = obj.data("ft-alertNoticeModalData");
                            option.continueAction($(this), option);
                        }
                    }
                    
                    if(option.allowRefresh){
                        buttons[ftReplaceKeyInString(option.refreshPageButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            obj.foreTeesModal("pleaseWait");  
                            window.location.reload();
                        }
                    }
                    
                    if(option.allowBack){
                        buttons[ftReplaceKeyInString(option.goBackButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            obj.foreTeesModal("pleaseWait");  
                            ft_historyBack();
                        }
                    }
                    
                    if(option.allowClose){
                        buttons[ftReplaceKeyInString(option.closeButton, option)] = function(){
                            $(this).ftDialog("close");
                        }
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        timeoutError: function (option) {
            
            var obj = $(this);
            //obj.foreTeesModal("pleaseWait","close");
            obj.data("ft-timeoutErrorModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","timeoutError");
            
            var action = null;
            if(typeof option == "object"){
                action = option.action;   
            }else {
                option = {};
            }
            if(action == null){
                action = "open";
            } 
            
            
            option.options = options;
            option.modalObj = modalObj;

            switch (action) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.timeoutError.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");
                    
                    ft_logError("Timeout Waiting for PleaseWait")
                    /*
                    var postdata = {
                        appCodeName: navigator.appCodeName,
                        appName: navigator.appName,
                        cookieEnabled: navigator.cookieEnabled,
                        platform: navigator.platform,
                        userAgent: navigator.userAgent,
                        error: "Timeout Waiting for PleaseWait",
                        page: document.URL
                    }
                    $.ajax({  
                        url: "data_logger?" + ftObjectToUri(postdata),
                        context:obj,
                        dataType: 'text',
                        success: function(data){

                        },
                        error: function(jqXHR){
                            //ft_logError("", jqXHR);
                            
                        }
                    });
                    */
                    var message = "";
                    message += '<div class="sub_instructions"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(options.timeoutError.message1, option)+'</p><br><p>'+ftReplaceKeyInString(options.timeoutError.message2, option)+'</p></div>';
                    
                    modalObj.append(message);

                    var buttons = {};

                    buttons[ftReplaceKeyInString(options.timeoutError.tryAgainButton, option)] = function(){
                        var obj = $(this).data("ft-modalParent");
                        //obj.foreTeesModal("pleaseWait"); 
                        $(this).ftDialog("close");
                    }
                    
                    buttons[ftReplaceKeyInString(options.timeoutError.goBackButton, option)] = function(){
                        var obj = $(this).data("ft-modalParent");
                        //obj.foreTeesModal("pleaseWait");  
                        ftLocation("Member_announce");
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
   
        ajaxError: function (option) {
            
            var obj = $(this);
            obj.data("ft-ajaxErrorModalData", option);
            var modalObj = obj.foreTeesModal("getModalObject","ajaxError");
            
            var action = option.action;
            if(action == null){
                action = "open";
            }
            
            option.options = options;
            option.modalObj = modalObj;

            switch (action) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.ajaxError.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");

                    var message = "";
                    message += '<div class="sub_instructions"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(options.ajaxError.message1, option)+'</p><br><p>'+ftReplaceKeyInString(options.ajaxError.message2, option)+'</p></div>';
                    
                    modalObj.append(message);

                    var buttons = {};

                    if(typeof option.tryAgain == "function"){
                        buttons[ftReplaceKeyInString(options.ajaxError.tryAgainButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            option = obj.data("ft-ajaxErrorModalData");
                            $(this).ftDialog("close");
                            option.tryAgain(option);
                        }
                    }
                    
                    if(option.allowRefresh){
                        buttons[ftReplaceKeyInString(options.ajaxError.refreshPageButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            obj.foreTeesModal("pleaseWait");  
                            window.location.reload();
                        }
                    }
                    
                    if(option.allowBack){
                        buttons[ftReplaceKeyInString(options.ajaxError.goBackButton, option)] = function(){
                            var obj = $(this).data("ft-modalParent");
                            obj.foreTeesModal("pleaseWait");  
                            ft_historyBack();
                        }
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        manageMemberDistributionLists: function (new_option) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.manageMemberDistributionLists, new_option);
            } else {
                option = $.extend({}, options.manageMemberDistributionLists);
                option.mode = new_option;
            }
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","manageMemberDistributionLists");
            var parms = modalObj.data("ft-emailParms");
            if(typeof parms == "undefined"){
                parms = {};
                modalObj.data("ft-emailParms", parms)
            }

            switch (option.mode) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                            
                    modalObj.foreTeesModal("hideActivity");

                    var html = "";
                    html += '<div class="manage_distribution_lists_container"></div>'
                    
                    var htmlObj  = $(html);
                    modalObj.append(htmlObj);
                    
                    modalObj.find(".manage_distribution_lists_container").foreTeesEmail("init",{
                        mode:"distributionList"
                    });
                   
                    var buttons = {};

                    buttons[ftReplaceKeyInString(option.closeButton, option)] = function(){
                        // Check if we were called from a Distributions Lists modal, and if so, refresh it.
                        var obj = $(this).data("ft-modalParent");
                        var rootobj = obj.data("ft-modalParent");
                        if(typeof rootobj == "object" && typeof(rootobj.data("ft-getMemberDistributionListObj")) == "object"){
                            rootobj.foreTeesModal("getMemberDistributionList");
                        }
                        $(this).ftDialog("close");
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });
                    modalObj.foreTeesModal("triggerResize");
 
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        getMemberDistributionList: function (new_option) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.getMemberDistributionList, new_option);
            } else {
                option = $.extend({}, options.getMemberDistributionList);
                option.mode = new_option;
            }
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","getMemberDistributionList");
            var parms = modalObj.data("ft-emailParms");
            if(typeof parms == "undefined"){
                parms = {};
                modalObj.data("ft-emailParms", parms)
            }
            parms.recipients = {};
            parms.distributionListModalOptions = option;

            switch (option.mode) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    
                    var url = "data_loader?json_mode=true&email&dist_list_names";
                    
                    $.ajax({  
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //// console.log("got partner list");
                            //var obj = $(this);

                            obj.foreTeesEmail("displayMemberList");
                            //obj.foreTeesModal("pleaseWait", "close");
                            var modalObj = obj.data("ft-getMemberDistributionListObj");
                            var parms = modalObj.data("ft-emailParms");
                            parms.partner_list = data;
                            var option = parms.distributionListModalOptions;
                            
                            modalObj.foreTeesModal("hideActivity");

                            var html = "";
                            html += '<div class="main_instructions">'
                            html += ftReplaceKeyInString("[instructions]", option);
                            html += '</div>';
                    
                            html += '<fieldset class="standard_fieldset"><legend>Distribution Lists:</legend>';
                            html += '<div class="list_container"></div></fieldset>';
                    
                            var htmlObj  = $(html);
                            modalObj.append(htmlObj);
                            var listContainer = htmlObj.find(".list_container");
                            if(data.length < 1){
                                listContainer.append('<p>You currently do not have any distribution lists configured.</p><p>Select "Manage Lists" below to create a new list.</p>');
                            }else{
                                for(var i=0; i < data.length; i++){
                                    var listObj = $('<div><p><input type="checkbox" name="list_'+ i +'" value="'+data[i]+'"><label for="list_'+ i +'">'+data[i]+'</label></p></div>');
                                    listObj.find("p").first()
                                    .click(function(event){
                                        var checkbox = $(this).find('input[type="checkbox"]');
                                        checkbox.prop("checked",!checkbox.prop("checked"));
                                        event.stopPropagation();
                                    })
                                    .mousedown(function(event){
                                        return false;
                                    })
                                    .hover(
                                        function(){
                                            $(this).addClass("highlight");
                                        },
                                        function(){
                                            $(this).removeClass("highlight");
                                        }
                                        )
                                    .find('input[type="checkbox"]').click(function(event){
                                        event.stopPropagation();
                                    });
                                    listContainer.append(listObj);
                                }   
                            }
                            parms.listContainer = listContainer;

                            var buttons = {};
                            
                            buttons[ftReplaceKeyInString(option.manageButton, option)] = function(){
                        
                                $(this).foreTeesModal("manageMemberDistributionLists");

                            }

                            buttons[ftReplaceKeyInString(option.addButton, option)] = function(){
                        
                                var modalObj = $(this);
                                //var obj = $(this).data("ft-modalParent");
                                var parms = modalObj.data("ft-emailParms");
                                
                                //obj.foreTeesEmail("addRecipients", parms.recipients);
                                //$(this).ftDialog("close");
                                // Get selected lists into array
                                var lists = [];
                                parms.listContainer.find("input:checked").each(function(){
                                    lists.push($(this).attr("value"));
                                });
                                
                                var url = "data_loader?json_mode=true&email&dist_lists="+JSON.stringify(lists);
                                
                                if(lists.length > 0){
                                
                                    obj.foreTeesModal("pleaseWait");
                                    
                                    $.ajax({  
                                        url: url,
                                        //context:obj,
                                        dataType: 'json',
                                        success: function(data){
                                            //// console.log("got partner list");
                                            //var obj = $(this);
                                            var modalObj = obj.data("ft-getMemberDistributionListObj");
                                            obj.foreTeesModal("pleaseWait", "close");
                                            modalObj.ftDialog("close");
                                            obj.foreTeesEmail("addRecipients", data);
                                            
                                        },
                                        error: function(jqXHR){
                                            obj.foreTeesModal("pleaseWait", "close");
                                            ft_logError("Error loading email distributions list", jqXHR);
                                        }
                                    });
                                } else {
                                    modalObj.ftDialog("close");
                                }

                            }
                    
                            buttons[ftReplaceKeyInString(option.cancelButton, option)] = function(){
                        
                                $(this).ftDialog("close");

                            }

                            modalObj.ftDialog("option",{

                                buttons: buttons

                            });
                            modalObj.foreTeesModal("triggerResize");

                            
                            
                        },
                        error: function(jqXHR){
                            // console.log("error recv. partner list");
                            // 
                            ft_logError("Error loading email distribution list names", jqXHR);
                        }
                    });
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
        
        getMemberRecipients: function (new_option) {
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.getMemberRecipients, new_option);
            } else {
                option = $.extend({}, options.getMemberRecipients);
                option.mode = new_option;
            }

            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","getMemberRecipients");
            var parms = modalObj.data("ft-emailParms");
            if(typeof parms == "undefined"){
                parms = {};
                modalObj.data("ft-emailParms", parms)
            }
            
            parms.recipients = {};
            parms.listOptions = option;

            switch (option.mode) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(option.title, option));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivity");

                    var html = "";
                    html += '<div class="name_list_container">'
                    
                    html += '<div class="widget_container_right">';
                    
                    html += "<div class=\"sub_instructions name_list_container\">"; // start name list container
                    html += "<div class=\"element_group\">";  // Start partner list element group
                    html += "<select size=\"15\" class=\"partner_list\"></select>";
                    html += "</div>";  // end  parter list element group
                    html += "</div>"; // end name list container

                    html += "<div class=\"sub_instructions member_search_container\">"; // start member search table container
                    html += "<h3>Member List</h3>";
                    html += "<div class=\"element_group\">";  // Start element group
                    html += "<table class=\"member_search_letter_table\"><tbody><tr>";
                    for (var i2 = 0; i2 < 26; i2++) { // output memberletter search table table
                        if ((i2 % 6 == 0) && i2 > 0) {  // start new row of letters every 6 letters
                            html += "</tr><tr>";
                        }
                        html += "<td><a class=\"member_search_letter_button\" data-ftinteger=\"" + (65 + i2) + "\" href=\"#\">&#" + (65 + i2) + ";</a></td>";  // Output html number for letters A to Z
                    }
                    html += "<td colspan=\"4\"><a class=\"member_search_partners_button\" href=\"#\">Partners</a></td>";
                    html += "</tr></tbody></table>"; 
                    html += '</div>';
                    html += '</div>';
                    
                    html += '</div>';
                    
                    html += '<div class="widget_container_fill">'
                    html += '<div class="main_instructions">'
                    html += ftReplaceKeyInString("[instructions]", option);
                    html += '</div>';
                    html += '<div class="sub_main_tan">'
                    if(typeof option.listName == "string"){
                        html += '<fieldset class="standard_fieldset"><legend>List Name:</legend>';
                        html += '<input type="text" name="distribution_list_name" value="'+option.listName+'" /></fieldset>';
                    }
                    html += '<fieldset class="standard_fieldset"><legend>Recipients:</legend>';
                    html += '<div class="reciptient_container"></div></fieldset>';
                    html += '</div></div>';
                    
                    html += '<div style="clear:both;" />';
                    html += '</div>';
                    
                    var htmlObj  = $(html);
                    var listContainer = htmlObj.find(".reciptient_container"); 
                    parms.addRecipientsObj = $('<div></div>');
                    listContainer.append(parms.addRecipientsObj);
                    modalObj.append(htmlObj);
                    parms.recipients = {};
                    
                    if(typeof option.listName == "string" && option.listName.length > 0){
                        
                        var url = "data_loader?json_mode=true&email&dist_lists="+JSON.stringify([option.listName]);  
                        obj.foreTeesModal("pleaseWait");
                        
                        $.ajax({  
                            url: url,
                            //context:obj,
                            dataType: 'json',
                            success: function(data){
                                //// console.log("got partner list");
                                //var obj = $(this);
                                var modalObj = obj.data("ft-getMemberRecipientsObj");
                                var parms = modalObj.data("ft-emailParms");
                                obj.foreTeesModal("pleaseWait", "close");
                                if(ftCount(data) > 0){
                                    modalObj.foreTeesEmail("addRecipients", data);
                                }
                            },
                            error: function(jqXHR,testStatus,errorThrown){
                                // console.log("error recv. partner list");
                                // 
                                var errorText = 'Unable to load distribution list.';
                                ft_logError(errorText, jqXHR);
                                //var obj = $(this);
                                obj.foreTeesModal("ajaxError", {
                                    //context:obj,
                                    errorThrown:errorText,
                                    tryAgain:function(option){
                                        option.context.foreTeesEmail("getMemberRecipients", option.context.distributionListModalOptions);
                                    },
                                    allowRefresh: true
                                });
                            }
                        });
                    }
                    
                    htmlObj.find(".member_search_letter_button").each(function(){
            
                        $(this).data("ft-parentObj", modalObj);
                        $(this).click(function(event){
                            var obj =  $(this).data("ft-parentObj");
                            obj.foreTeesEmail("loadMemberList", String.fromCharCode(parseInt($(this).attr("data-ftinteger"))));
                            event.preventDefault();
                        });
            
                    });
                    
                    // Activate the partner buttons
                    parms.firstLoad = function(modalObj){
                        modalObj.foreTeesModal("triggerResize");
                    }
                    
                    htmlObj.find(".member_search_partners_button").each(function(){
            
                        $(this).data("ft-parentObj", modalObj);
                        $(this).click(function(event){
                            var obj =  $(this).data("ft-parentObj");
                            obj.foreTeesEmail("loadMemberList", "partners");
                            event.preventDefault();
                        });
                        $(this).click();
            
                    });

                    var buttons = {};

                    buttons[ftReplaceKeyInString(option.addButton, option)] = option.callbackAction;
                    
                    buttons[ftReplaceKeyInString(option.cancelButton, option)] = function(){
                        $(this).ftDialog("close");
                    }
                   
                    modalObj.ftDialog("option",{
                        buttons: buttons
                    });
                    
                    modalObj.foreTeesModal("triggerResize");

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },

        calendarError: function (option) {
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","calendarError");

            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.calendarError.title, options));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");

                    var message = "";
                    message += '<div class="sub_instructions"><p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>'+ftReplaceKeyInString(options.calendarError.message1, options)+'</p><br><p>'+ftReplaceKeyInString(options.calendarError.message2, options)+'</p></div>';
                    
                    modalObj.append(message);

                    var buttons = {};

                    buttons[ftReplaceKeyInString(options.calendarError.tryAgainButton, options)] = function(){
                        
                        var obj = $(this).data("ft-modalParent");
                        $(this).ftDialog("close");
                        obj.foreTeesMemberCalendar("reload");   

                    }
                    
                    buttons[ftReplaceKeyInString(options.calendarError.refreshPageButton, options)] = function(){
                        
                        var obj = $(this).data("ft-modalParent");
                        obj.foreTeesModal("pleaseWait");  
                        window.location.reload();

                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });

                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },

        slotTimeout: function (option) {
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","slotTimeout");
            var slotParms = obj.data("ft-slotParms");
            obj.foreTeesSlot("leaveForm");

            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.slotTimeout.title, slotParms));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");
                    
                    var message = "";
                    message += '<div class="sub_instructions">' + ftReplaceKeyInString(options.slotTimeout.message, slotParms) + '</div>';
                    
                    modalObj.append(message);

                    var buttons = {};

                    buttons[ftReplaceKeyInString(options.slotTimeout.continueButton, slotParms)] = function(){
                        
                        var obj = $(this).data("ft-modalParent");
                        var slotParms = obj.data("ft-slotParms");
                        obj.foreTeesSlot("exitSlotPage");     

                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });
                    
                    
                      
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
 
      
        slotPageLoadNotification: function (option) {
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","slotPageLoadNotification");
            var slotParms = obj.data("ft-slotParms");

            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(slotParms.page_start_title, slotParms));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivityOnly");
                    
                    var message = "";
                    if(slotParms.page_start_notifications.length > 0){
                        message += '<div class="sub_instructions">';
                        for (var i = 0; i < slotParms.page_start_notifications.length; i ++ ){
                            if(i>0){
                                message += '<br><br>';
                            }
                            message +=  ftReplaceKeyInString(slotParms.page_start_notifications[i], slotParms);
                        }
                        message += '</div>';
                    }
                    
                    if(slotParms.page_start_instructions.length > 0){
                        message += '<div class="main_instructions">';
                        for (var i = 0; i < slotParms.page_start_instructions.length; i ++ ){
                            if(i>0){
                                message += '<br><br>';
                            }
                            message +=  ftReplaceKeyInString(slotParms.page_start_instructions[i], slotParms);
                        }
                        message += '</div>';
                    }
                    
                    modalObj.append(message);
                    
                    if(slotParms.callback_form_html.length > 0){
                        //console.log("binding form");
                        var formObj = $('<div class="main_instructions"><form>'+ftReplaceKeyInString(slotParms.callback_form_html, slotParms)+'</form></div>');
                        modalObj.append(obj.foreTeesModal("bindForm", formObj, slotParms.callback_map));
                    }           
                    
                    if(slotParms.alt_callback_form_html.length > 0){
                        var formObj = $('<div class="main_instructions"><form>'+ftReplaceKeyInString(slotParms.alt_callback_form_html, slotParms)+'</form></div>');
                        modalObj.append(obj.foreTeesModal("bindForm", formObj, slotParms.callback_map));
                    }
                    
                    if (slotParms.location_disp.length > 0) {
                        $("#breadcrumb").html("<a href='Member_announce'>Home</a> / " + slotParms.location_disp + " Signup");
                    }
                    
                    if(ftCount(slotParms.callback_form_map) > 0){
                        var formContainer = $('<div class="main_instructions field_list_container"></div>');
                        modalObj.append(formContainer);
                        formContainer.append(obj.foreTeesModal("buildForm", slotParms.callback_form_map, slotParms.callback_map, slotParms));
                    }
                    
                    // Bind to any form buttons
                    modalObj.find('button.continue_slot, input.continue_slot[type="button"]').each(function(){
                        $(this).data("ft-slotNotificationModal", modalObj);
                        $(this).click(function(){
                            var modalObj = $(this).data("ft-slotNotificationModal");
                            var obj = modalObj.data("ft-modalParent");
                            //var slotParms = obj.data("ft-slotParms");
                            modalObj.ftDialog("close");
                            obj.foreTeesSlot("refreshSlot");
                        });
                    });
                    
                    var buttons = {};
                            
                    // Build our close/continue button
                    // buttons[ftReplaceKeyInString(options.slotPageLoadNotification.goBackButton, slotParms)] = function(){
                    //     $(this).ftDialog("close");
                    // }

                    if(slotParms.page_start_button_go_back){
                        buttons[ftReplaceKeyInString(options.slotPageLoadNotification.goBackButton, slotParms)] = function(){
                        
                            var obj = $(this).data("ft-modalParent");
                            //var slotParms = obj.data("ft-slotParms");
                            obj.foreTeesSlot("leaveForm", false, true);
                            //obj.foreTeesSlot("exitSlotPage");
                            $(this).ftDialog("close");

                        }
                    }
                    
                    if(slotParms.page_start_button_continue || slotParms.page_start_button_accept){
                        var button_text = options.slotPageLoadNotification.continueButton;
                        if(slotParms.page_start_button_accept){
                            button_text = options.slotPageLoadNotification.acceptButton;
                        }
                        
                        buttons[ftReplaceKeyInString(button_text, slotParms)] = function(){
                        
                            var obj = $(this).data("ft-modalParent");
                            //var slotParms = obj.data("ft-slotParms");
                            $(this).ftDialog("close");
                            obj.foreTeesSlot("refreshSlot");
                        }
                    }
                    
                    if(ftCount(slotParms.callback_button_map) > 0){
                        for(var key in slotParms.callback_button_map){
                            var button_value = ftReplaceKeyInString(slotParms.callback_button_map[key].value, slotParms);
                            if(slotParms.callback_button_lookup === undefined){
                                slotParms.callback_button_lookup = {};
                            }
                            slotParms.callback_button_lookup[button_value] = key;
                            buttons[button_value] = function(){
                                var obj = $(this).data("ft-modalParent");
                                var slotParms = obj.data("ft-slotParms");
                                var button_value = $(this).ftDialog("widget").find("button.ui-button.ui-state-hover span.ui-button-text").html();
                                var key = slotParms.callback_button_lookup[button_value];
                                //console.log("button_key:"+key+":"+button_value);
                                if(typeof slotParms.callback_button_map[key].action != "undefined"){
                                    //console.log("action:"+slotParms.callback_button_map[key].action);
                                    obj.foreTeesSlot(slotParms.callback_button_map[key].action);
                                }else{
                                    //console.log("default action:"+key);
                                    slotParms.callback_map[key] = button_value;
                                    if("suppress" in slotParms.callback_button_map[key]){
                                        for(var i = 0; i < slotParms.callback_button_map[key].suppress.length; i++){
                                            delete slotParms.callback_map[slotParms.callback_button_map[key].suppress[i]];
                                        }
                                    }
                                    if ("update_recur" in slotParms.callback_map && !slotParms.callback_map.update_recur.length) {
                                        alert("Please choose your \"Recurrence Update Option\".");
                                    } else {
                                        $(this).ftDialog("close");
                                        obj.foreTeesSlot("refreshSlot");
                                    }
                                }
                            }
                        }
                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });        
                    break;
                    
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },

        slotCancelPrompt: function (option) {
            
            var obj = $(this);
            var modalObj = obj.foreTeesModal("getModalObject","slotCancelPrompt");
            var slotParms = obj.data("ft-slotParms");

            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.slotCancelPrompt.title, slotParms));
                    modalObj.ftDialog("open");
                    modalObj.empty();
                    modalObj.foreTeesModal("hideActivity");
                    
                    var message = "";
                    for (var i = 0; i < options.slotCancelPrompt.promptMessages.length; i ++ ){
                        message += '<div class="sub_instructions">' + ftReplaceKeyInString(options.slotCancelPrompt.promptMessages[i], slotParms) + '</div>';
                    }
                    
                    modalObj.append(message);

                    var buttons = {};
                            
                    // Build our close/continue button
                    buttons[ftReplaceKeyInString(options.slotCancelPrompt.closeButton, slotParms)] = function(){
                        $(this).ftDialog("close");
                    }

                    buttons[ftReplaceKeyInString(options.slotCancelPrompt.continueButton, slotParms)] = function(){
                        
                        var obj = $(this).data("ft-modalParent");
                        var slotParms = obj.data("ft-slotParms");
                        $(this).ftDialog("close");
                        obj.foreTeesModal("slotSubmit", "open", "cancel_slot");

                    }
                   
                    modalObj.ftDialog("option",{

                        buttons: buttons

                    });
                    
                    
                      
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
            
        },
 
        slotSubmit: function (option, mode) {
            
            var obj = $(this);
            var slotParms = obj.data("ft-slotParms");
            var modalObj = obj.foreTeesModal("getModalObject","slotSubmit");
            var formValues = {};
            
            if("alternate_parms" in slotParms && slotParms.alternate_parms != null){
                // Check if we have an override of what we will be posting
                formValues = ftMixedObjectToObject(slotParms.alternate_parms);
            }else{
                // Get form values
                formValues = obj.foreTeesSlot("getFormParameters");
            /*
                $.extend(formValues,ftObjectFromArray("player%",slotParms.player_a), formValues);
                $.extend(formValues,ftObjectFromArray("p9%",slotParms.p9_a));
                $.extend(formValues,ftObjectFromArray("p%cw",slotParms.pcw_a));
                $.extend(formValues,ftObjectFromArray("p%cw",slotParms.pcw_a)); 
                $.extend(formValues,ftObjectFromArray("guest_id%",slotParms.guest_id_a));
                formValues["notes"] = slotParms.notes;
                 */
            }
            
            if(slotParms.allow_submit_retry){
                mode = slotParms.last_submit_mode;
                slotParms.allow_submit_retry = false;
            }

            switch (mode){
                case "cancel_slot":
                    slotParms.last_submit_mode = mode;
                    formValues["remove"] = "submit";
                    formValues["ack_remove"] = "true";
                    break;
                case "submit_slot":
                    slotParms.last_submit_mode = mode;
                    formValues["submitForm"] = "submit";
                    break;    
            }
            formValues["json_mode"] = true;
            
            //console.log("submitting form");
            //console.log(JSON.stringify(formValues));

            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.slotSubmit.loadingTitle);
                    
                    modalObj.ftDialog("open");
                    //console.log("posting form...");
                    $.ajax({
                        type: 'POST',
                        url: slotParms.slot_url,
                        //url: "bad_url",
                        data: formValues,
                        dataType: 'text',
                        context: obj,
                        success: function(data, textStatus, jqXHR){
                            var obj = $(this);
                            var slotParms = obj.data("ft-slotParms");
                            var modalObj = obj.data("ft-slotSubmitObj");
                            obj.data("ft-slotSubmitData", data);
                            modalObj.empty();
                            modalObj.foreTeesModal("hideActivityOnly");
                            var result = {};
                            slotParms.alternate_parms = null;
                            try {
                                // Try getting the result as json
                                result = JSON.parse(data);
                            //console.log('Parsed Json');
                            } catch (e) {
                                //console.log("Not json");
                                // Old skin HTML response -- try to clean it up and parse what we can from it
                                //console.log(data);
                            
                                // Get the modal title
                                result.title = ftExtractValue(data, /<title[^>]*>([^<]*)<\/title[^>]*>/i, "");
                                result.header = ftExtractValue(data, /<h3[^>]*>((?:[\s\S](?!<\/h3))*[^<]*)/i, "");
                                if(result.header.length > 0){
                                    result.title = result.header;
                                }
                                // Get and clean up contents of the message body for display in modal
                                var message_body = ftExtractValue(data, /<body[^>]*>((?:[\s\S](?!<\/body))*[^<]*)/i, "");
                                // console.log(message_body);
                                // strip form from body
                                message_body = message_body.replace(/<form[^>]*>((?:[\s\S](?!<\/form))*)[^<]*<\/form[^>]*>/ig, "");
                                // strip unwanted tags from body
                                message_body = message_body.replace(/(<hr[^>]*>|<img[^>]*>|<center[^>]*>|<\/center[^>]*>|<font[^>]*>|<\/font[^>]*>)/ig, "");
                                // Remove header (if it exists, we used it as the modal title, above)
                                message_body = message_body.replace(/<h3[^>]*>((?:[\s\S](?!<\/h3))*)[^<]*<\/h3[^>]*>/ig, "");
                                // Strip links we don't want
                                message_body = message_body.replace(/<a\s+href=['"]javascript[^>]*>((?:[\s\S](?!<\/a))*)[^<]*<\/a[^>]*>/ig, "");
                                // Strip leading/ending line breaks
                                message_body = message_body.replace(/(^[\r\n\s]*(<br[^>]*>[\r\n\s]*)*[\r\n\s]*|[\r\n\s]*(<br[^>]*>[\r\n\s]*)*[\r\n\s]*$)/ig, "");
                                // Strip leading/ending line breaks in p tags
                                //message_body = message_body.replace(/(<p[^>]*>[\r\n\s]*(<br[^>]*>[\r\n\s]*)*[\r\n\s]*|[\r\n\s]*(<br[^>]*>[\r\n\s]*)*[\r\n\s]*<\/p[^>]*>)/ig, "");
                                // Strip leading/ending &nbsp;
                                message_body = message_body.replace(/(^([\r\n\s]*(&nbsp;)[\r\n\s]*)+|([\r\n\s]*(&nbsp;)[\r\n\s]*)+$)/ig, "");
                                // Remove empty paragraph tags at the start and end
                                message_body = message_body.replace(/(^([\r\n\s]*<p[^>]*>[\r\n\s]*(&nbsp;)*(<br[^>]*>)*(&nbsp;)*[\r\n\s]*<\/p[^>]*>[\r\n\s]*)+|([\r\n\s]*<p[^>]*>[\r\n\s]*(&nbsp;)*(<br[^>]*>)*(&nbsp;)*[\r\n\s]*<\/p[^>]*>[\r\n\s]*)+$)/ig, "");
                            
                                // console.log(message_body);
                                result.message_array = [message_body];
                            
                                // Check if we will be returning to the slot page
                                result.back_to_slotpage = (data.match(/(<input type=["']hidden["'] name=["']player1["'] value=|(action|href)=["']javascript:history.back\()/i) != null);
                                // Check if we were successful
                                result.successful = (data.match(/Your [a-z\s0-9]+ has been (accepted|processed) and (accepted|processed)/i) != null);
                                if(!result.successful){
                                    result.successful = (data.match(/(The reservation has been cancelled|the request has been removed from the system|The event entry has been cancelled|The wait list entry has been cancelled|The notification has been cancelled)/i) != null);
                                }

                            }
                            
                            if(!result.successful && result.page_start_button_go_back){
                                //console.log(result);
                                // This is a page start or error noticication -- 
                                // we'll partially convert it to a slot page response
                                result.back_to_slotpage = true;
                                result.title = result.page_start_title;
                                result.head = result.page_start_title;
                                result.message_array = result.page_start_notifications;
                                result.successful = false;
                            }
                            
                            if(!result.back_to_slotpage && !result.successful && !result.prompt_yes_no && !result.prompt_close_continue){
                               ft_logError("Error Submitting Slot and user will be unable to resolve", jqXHR); 
                            }
                            
                            slotParms.this_callback = result;
                            
                            //console.log(slotParms);
                            
                            var message = '';
                            if("warning_list" in result){
                                message += '<div class="sub_instructions">';
                                if("warning_head" in result){
                                    message += '<h3>'+ftReplaceKeyInString(result.warning_head, slotParms)+'</h3>';
                                }
                                for(var i in result.warning_list){
                                    if(i > 0){
                                        message += "<br><br>";
                                    }
                                    message += ftReplaceKeyInString(result.warning_list[i], slotParms);
                                
                                }
                                message += '</div>';
                            }
                            
                            var block_head = '<div class="main_instructions">';
                            if(!result.successful){
                                block_head = '<div class="sub_instructions">';
                            }else{
                                slotParms.skip_unload = true;
                            }
                            message += block_head;
                            var c = 0;
                            for(var i in result.message_array){
                                var text = result.message_array[i];
                                if(text == "[new_block]"){
                                    message += '</div>';
                                    message += block_head;
                                    c = 0;
                                } else {
                                    if(c > 0){
                                        message += "<br><br>";
                                    }
                                    message += ftReplaceKeyInString(text, slotParms);
                                    c++;
                                }
                                
                                
                            }
                            message += '</div>';
                            
                            if("notice_list" in result){
                                message += '<div class="sub_instructions">';
                                if("notice_head" in result){
                                    message += '<h3>'+ftReplaceKeyInString(result.notice_head, slotParms)+'</h3>';
                                }
                                for(var i in result.notice_list){
                                    if(i > 0){
                                        message += "<br><br>";
                                    }
                                    message += ftReplaceKeyInString(result.notice_list[i], slotParms);
                                
                                }
                                message += '</div>';
                            }
                            
                            modalObj.append(message);
                            
                            if("modal_width" in result){
                                //console.log("setting width");
                                modalObj.data("ft-width", result.modal_width);
                            }
                            
                            if(ftCount(result.callback_form_map) > 0){
                                var formContainer = $('<div class="main_instructions field_list_container"></div>');
                                modalObj.append(formContainer);
                                formContainer.append(obj.foreTeesModal("buildForm", result.callback_form_map, result.field_override_hidden_map, slotParms));
                            }
                            
                            if("form_html" in result){
                                //console.log("binding form");
                                var formObj = $('<form>'+ftReplaceKeyInString(result.form_html, slotParms)+'</form>');
                                modalObj.append(obj.foreTeesModal("bindForm", formObj, result.field_override_hidden_map));
                            }
                            ftActivateElements(modalObj);
                            var buttons = {};
                            
                            // Build our close/continue button
                            if(!result.prompt_yes_no && !result.prompt_close_continue){
                                if(!result.back_to_slotpage){
                                    obj.foreTeesSlot("leaveForm");
                                    buttons[ftReplaceKeyInString(options.slotSubmit.continueButton, slotParms)] = function(){
                                        var obj = $(this).data("ft-modalParent");
                                        var slotParms = obj.data("ft-slotParms");
                                        slotParms.skip_unload = true;
                                        obj.foreTeesSlot("exitSlotPage");

                                    }
                                }else{
                                    modalObj.foreTeesModal("showTitleClose");
                                    buttons[ftReplaceKeyInString(options.slotSubmit.closeButton, slotParms)] = function(){
                                        $(this).ftDialog("close");
                                    }
                                }
                            }else{
                                // Prompt Yes/No
                                modalObj.foreTeesModal("showTitleClose");
                                var backButton = "";
                                var continueButton = "";
                                if(result.prompt_yes_no){
                                    backButton = ftReplaceKeyInString(options.slotSubmit.noGoBackButton, slotParms);
                                    continueButton = ftReplaceKeyInString(options.slotSubmit.yesContinueButton, slotParms);
                                }else{
                                    backButton = ftReplaceKeyInString(options.slotSubmit.closeButton, slotParms);
                                    continueButton = ftReplaceKeyInString(options.slotSubmit.continueButton, slotParms); 
                                }
                                buttons[backButton] = function(){
                                    $(this).ftDialog("close");
                                }
                                buttons[continueButton] = function(){
                                    var obj = $(this).data("ft-modalParent");
                                    var slotParms = obj.data("ft-slotParms");
                                    slotParms.alternate_parms = result.field_override_hidden_map;
                                    slotParms.skip_unload = true;
                                    $(this).ftDialog("close");
                                    obj.foreTeesSlot("submitForm");
                                    slotParms.skip_unload = false;
                                    
                                }
                            }
                            modalObj.ftDialog("option",{

                                buttons: buttons,
                                title: ftReplaceKeyInString(result.title, slotParms)

                            });
                            
                            // Reposition and resize the window
                            modalObj.foreTeesModal("triggerResize");
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error Submitting Slot", jqXHR);
                            var obj = $(this);
                            var slotParms = obj.data("ft-slotParms");
                            slotParms.allow_submit_retry = true;
                            $(this).foreTeesModal("error", "slotSubmit");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        
        linkModal: function (new_option) {
            
            var obj = $(this);
            
            var option = {};
            
            if(typeof(new_option) == "object"){
                option = $.extend({}, options.linkModal, new_option);
            } else {
                option = $.extend({}, options.linkModal);
                option.mode = new_option;
            }
            var skipInit = false;
            if(option.modalObj){
                modalObj = option.modalObj;
                skipInit = true;
            } else {
                var modalObj = obj.foreTeesModal("getModalObject","linkModal");
                modalObj.empty();
            }
            switch (option) {
                default:
                case "open":
                    if(!skipInit){
                        modalObj.foreTeesModal("initModalObject",option.loadingTitle);
                        modalObj.dialog("open");
                    } else {
                        modalObj.foreTeesModal("showActivity");
                    }
                    //console.log("posting form...");
                    $.ajax({
                        type: 'POST',
                        url: option.link,
                        dataType: 'text',
                        success: function(data, textStatus, jqXHR){
                            //var modalObj = obj.data("ft-linkModalObj");
                            modalObj.foreTeesModal("hideActivity");
                            // Get possible title
                            var title = ftExtractValue(data, /<title[^>]*>([^<]*)<\/title[^>]*>/i, "");
                            if(option.title.length < 1){
                                option.title = title;
                            }
                            // Strip all script out off response.
                            data = data.replace(/<script[^>]*>[\s\S]*?<\/script[^>]*>/ig,'');
                            // Get and clean up contents of the message body for display in modal
                            var message_body = ftExtractValue(data, /<body[^>]*>((?:[\s\S](?!<\/body))*[^<]*)/i, "");
                            //console.log(message_body);
                            
                            var message = $(message_body);
                            
                            message.find('input[type=radio][name=grpby]').each(function(){
                                var o = $(this);
                                o.removeAttr('onclick');
                                o.data('linkParentObject', obj);
                                o.click(function(){
                                    var o = $(this);
                                    var obj = o.data('linkParentObject');
                                    var link = ftUpdateQueryString(o.attr('name'),o.attr('value'),obj.attr('data-ftlink'));
                                    var title =  obj.text();
                                    obj.foreTeesModal("linkModal",{
                                        link:link,
                                        title:title,
                                        modalObj:obj.data("ft-linkModalObj")
                                    });
                                });
                            });
                            
                            modalObj.empty();
                            modalObj.append(message);

                            var buttons = {};
                            
                            buttons[option.closeButton] = function(){
                                $(this).dialog("close");
                            }
                            modalObj.dialog("option",{
                                buttons: buttons,
                                title: option.title
                            });
                            modalObj.foreTeesModal("triggerResize");
                        },
                        error: function(jqXHR){
                            ft_logError("Error Loading Dining Modal", jqXHR);
                            $(this).dialog("close");
                            alert("Unable to load data.  Your session may have timed out.");
                        }
                    });
                    
                    break;
                case "close":
                    modalObj.dialog("close");
                    break;
            }
          
        },


        guestDbSubmit: function (option, parms) {
            
            var obj = $(this);
            
            if(typeof parms != "object"){
                parms = obj.data("ft-guestDbSubmitParms");
            }else{
                obj.data("ft-guestDbSubmitParms", parms);
            }

            var url = "Common_guestdb?json_mode=true&modal&submitGuestInfo&" + parms.serialFormElements;
            // // console.log(url);
            var modalObj = obj.foreTeesModal("getModalObject","guestDbSubmit");
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.guestDbSubmit.loadingTitle);
                    
                    modalObj.ftDialog("open");
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //// console.log(data);
                            
                            //var obj = $(this);
                            /*
                            if(typeof data.access_error == "string" && data.access_error.length){
                                obj.foreTeesModal("guestDbSubmit", "error", data.access_error);
                                return false;
                            }
                             */
                            var modalObj = obj.data("ft-guestDbSubmitObj");
                            obj.data("ft-guestDbSubmitData", data);
                            modalObj.empty();
                            modalObj.foreTeesModal("hideActivity");
                            var message = '';
                            var title = options.guestDbSubmit.successTitle;
                            
                            if(!data.successful){
                                title = options.guestDbSubmit.failTitle;
                                message += '<div class="sub_instructions">';
                            }else{
                                message += '<div class="main_instructions">';
                            }
                            for(var i = 0; i < data.message_list.length; i++){
                                if(i == 0){
                                    message += '<p><b>'+data.message_list[i]+'</b></p>';
                                }else{
                                    message += '<p>'+data.message_list[i]+'</p>';
                                }
                            }
                            if(data.result_msg.length){
                                message += '<p><b>'+data.result_msg+'</b></p>';
                            }
                            message += '</div>';
                            
                            modalObj.append(message);

                            var buttons = {};
                            
                            // Build our close/continue button
                            if(data.successful){
                                modalObj.data("ft-modalGuestData", data.guest_data);
                                buttons[options.guestDbSubmit.continueButton] = function(){
                                    var obj = $(this).data("ft-modalParent");
                                    var parms = obj.data("ft-guestDbSubmitParms");
                                    $(this).ftDialog("close");
                                    parms.callback($(this).data("ft-modalGuestData"));
                                }
                            }else{
                                buttons[options.guestDbSubmit.closeButton] = function(){
                                    $(this).ftDialog("close");
                                }
                            }

                            modalObj.ftDialog("option",{

                                buttons: buttons,
                                title: title

                            });
                            
                            // Reposition and resize the window
                            modalObj.foreTeesModal("triggerResize");
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error submitting Guest DB Entry", jqXHR);
                            $(this).foreTeesModal("error", "guestDbSubmit");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },

        guestDbPrompt: function (option, parms) {
            
            var obj = $(this);

            var url = "Common_guestdb?json_mode=true&modal";
            //// console.log(url);
            var modalObj = obj.foreTeesModal("getModalObject","guestDbPrompt");
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.guestDbPrompt.title);
                    
                    modalObj.ftDialog("open");
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //// console.log(data);
                            //var obj = $(this);
                            /*
                            if(typeof data.access_error == "string" && data.access_error.length){
                                obj.foreTeesModal("guestDbPrompt", "error", data.access_error);
                                return false;
                            }
                             */
                            
                            var modalObj = obj.data("ft-guestDbPromptObj");
                            obj.data("ft-guestDbPromptData", data);
                            modalObj.empty();
                            modalObj.foreTeesModal("hideActivity");
                            data.passedParameters = parms;
                            var message = '<div class="main_instructions">';
                            for(var i = 0; i < data.message_list.length; i++){
                                message += '<p>'+data.message_list[i]+'</p>';
                            }
                            message += '</div>';

                            message +=  '<div class="forms_container">';
                                
                            message += '<div class="left_container">';
                            
                            if(data.guest_info_fields.field_notes.length > 0){
                                message += '<div class="sub_instructions">';
                                for(var i = 0; i < data.guest_info_fields.field_notes.length; i++){
                                    message += '<p>'+data.guest_info_fields.field_notes[i]+'</p>';
                                }
                                message += '</div>';
                            }
                            
                            message += '<div class="main_instructions field_list_container">';

                            message += '<div style="clear:both;"></div></div>'; // end sub intructions
                            message += '<div style="clear:both;"></div>';
                            message += '</div>'; // end left container
                            
                            message += '<div class="right_container">';
                            message += '<div class="sub_instructions modal_guest_list">';
                            message += '<select name="guestselect" size="15"></select>';
                            message += '</div>';
                            message += '<div style="clear:both;"></div>';
                            message += '</div>'; // end right container
                            
                            message += '</div>';// end forms container
                            
                            modalObj.append(message);
                            
                            var fieldListObj = modalObj.find(".field_list_container");
                            var rightContainer = modalObj.find(".right_container");
                            var leftContainer = modalObj.find(".left_container");
                            
                            fieldListObj.append(obj.foreTeesModal("buildForm", data.guest_info_fields.fields));

                            var guestSelect = modalObj.find(".modal_guest_list select");
                            guestSelect.data("ft-modalGuestParms", parms);
                            
                            // Populate guest select list
                            for(var key in data.guest_db_list){
                                var optionObj = $('<option value="'+data.guest_db_list[key]["display_name"]+'">'+data.guest_db_list[key]["display_name"]+'</option>');
                                data.guest_db_list[key].modalObj = modalObj;
                                data.guest_db_list[key].obj = obj;
                                optionObj.data("ft-modalGuestData", data.guest_db_list[key]);
                                guestSelect.append(optionObj);
                            }
                            
                            guestSelect.foreTeesListSearch("init", {
                                name: options.guestDbPrompt.searchName,
                                noResults: options.guestDbPrompt.noResults,
                                searchPrompt: options.guestDbPrompt.searchPrompt,
                                change: function(){
                                    var parms = $(this).data("ft-modalGuestParms");
                                    $(this).find("option").removeClass("selected_option");
                                    $(this).find("option:selected").each(function(){
                                        if(typeof($(this).data("ft-modalGuestData")) != "undefined"){
                                            //console.log($(this).data("ft-modalGuestData"));
                                            $(this).addClass("selected_option");
                                            parms.callback($(this).data("ft-modalGuestData"));
                                        }
                                    });
                                    // Deselect all items
                                    $(this).find("option:selected").prop("selected", false);   
                                }
                            });

                            data.fieldListObj = fieldListObj;
                           
                            //var fieldContainer = modalObj.find("field_container");

                            // Build our close button first
                            var buttons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                },
                                "Add New Guest": function(){
                                    var modalObj = $(this);
                                    var obj = modalObj.data("ft-modalParent");
                                    data = obj.data("ft-guestDbPromptData");
                                    var serialFormElements = data.fieldListObj.find("form").serialize();
                                    var parms = data.passedParameters;
                                    parms.serialFormElements = serialFormElements;
                                    obj.foreTeesModal("guestDbSubmit","open", parms);
                                }
                            };

                            modalObj.ftDialog("option",{

                                buttons: buttons

                            });
                            
                            // Set the size of the right and left column to be the same
                            var heightDiff = (leftContainer.height() - rightContainer.height());
                            var newHeight = guestSelect.height() + heightDiff;
                            if(newHeight < options.guestDbPrompt.minGuestListHeight){
                                newHeight = options.guestDbPrompt.minGuestListHeight;
                            } else if (newHeight > options.guestDbPrompt.maxGuestListHeight) {
                                newHeight = options.guestDbPrompt.maxGuestListHeight; 
                            }
                            if(!ftIsMobile()){
                                guestSelect.height(newHeight);
                            }
                            // Resize
                            heightDiff = (leftContainer.height() - rightContainer.height());
                            if(heightDiff > 0){
                                rightContainer.find(".modal_guest_list").height(rightContainer.find(".modal_guest_list").height() + heightDiff);
                            }else {
                                leftContainer.find(".field_list_container").height(leftContainer.find(".field_list_container").height() + Math.abs(heightDiff));
                            }
                            
                            // Reposition and resize the window
                            modalObj.foreTeesModal("triggerResize");
                            
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error getting GuestDB prompt", jqXHR);
                            $(this).foreTeesModal("error", "guestDbPrompt");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
 
        eventPrompt: function (new_option) {
            
            var obj = $(this);
            
            var option = {};
            
            if(typeof new_option == "object"){
                option = $.extend({}, options.eventPrompt, new_option);
            } else {
                option = $.extend({}, options.eventPrompt);
                option.mode = new_option;
            }

            // Check Json
            var jsondata = obj.attr("data-ftjson");
            try {
                var json = JSON.parse(jsondata);
            //// console.log('Parsed Json');
            } catch (e) {
                //// console.log('Bad Json');
                return false;
            }
            if(typeof json['base_url'] != "string"){
                json['base_url'] = '';
            }
            // Build url for JSON request via REST.
            obj.data("ft-eventRequest", json);
            var url = "Member_events2?jsonMode=true&" + ftObjectToUri(json, {});
            //// console.log(url);
            var modalObj = obj.foreTeesModal("getModalObject","eventPrompt");
            
            switch (option.mode) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",json.name,{iframeAction:json['iframe_action']});
                    
                    modalObj.ftDialog("open");
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //console.log(data);
                            //var obj = $(this);
                            if(typeof data.access_error == "string" && data.access_error.length){
                                obj.foreTeesModal("eventPrompt", "error", data.access_error);
                                return false;
                            }
                            
                            var modalObj = obj.data("ft-eventPromptObj");
                            obj.data("ft-eventPromptData", data);
                            var request = obj.data("ft-eventRequest");
                            modalObj.empty();
                            
                            //var eventDate = new Date(data.event_date.year, data.event_date.month -1, data.event_date.day, data.event_date.hour, data.event_date.minute);
                            //var signUpDate = new Date(data.sign_up_date.year, data.sign_up_date.month -1, data.sign_up_date.day, data.sign_up_date.hour, data.sign_up_date.minute);
                            // Set text values used by text options ( use "["key-name"]" in option)
                            var textValues = {
                                
                                view_edit_button: ((data.select_count > 0)?options.eventPrompt.viewEditButton:options.eventPrompt.viewButton),
                                signup_button: options.eventPrompt[data.signup_button_type],
                                event_name: request.name
                                
                            }
                            var listTextValues = {
                                
                                view_edit_button: options.eventPrompt.selectButtonInstructions,
                                signup_button: options.eventPrompt[data.signup_button_type],
                                event_name: request.name
                                
                            }
                            
                            obj.data("ft-eventTextValues", textValues);
                            // Build our close button first
                            var buttons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                }
                            };
                            var listButtons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                }
                            };
                            modalObj.foreTeesModal("hideActivity");
                            var message = '<div class="main_instructions">';
                            message +='<ul class="clearfix modal_field_list">';
                            // Display fields
                            for (var key in data.dashboard_map){
                                if(typeof data.dashboard_map[key]["class"] == "string"){
                                    message +='<li class="clearfix '+data.dashboard_map[key]["class"]+'">';
                                }else{
                                    message +='<li class="clearfix">';
                                }
                                message += '<div class="clearfix"><b>'+key+':</b><div>'+data.dashboard_map[key]["value"]+'</div></div>';
                                message +='</li>';
                            }
                            message += '</ul>';
                            message += '</div>';
                            if(data.view_list){
                                // Create view/edit list button
                                buttons[textValues.view_edit_button] = function(){
                                    $(this).data("ft-modalParent").foreTeesModal("eventList");
                                }
                            }
                            var listInstructions = "";
                            var instructions = "";
                            if(data.signup_count>0){
                                // Display instructions
                                message += '<div class="sub_instructions">';
                                listInstructions += '<div class="sub_instructions">';
                                for (var key in data.instructions){
                                    
                                    message += '<p class="instruction_block">'+ftReplaceKeyInString(data.instructions[key], textValues)+'</p>';
                                    listInstructions += '<p class="instruction_block">'+ftReplaceKeyInString(data.instructions[key], listTextValues)+'</p>';
                                }
                                message += '</div>';
                                listInstructions += '</div>';
                                // Check if user is registered
                                var is_reg = option.unregistered;
                                if(data.user_to_event_map[$.fn.foreTeesSession("get","user")]){
                                    var is_reg = option.registered;
                                }
                                message += '<div class="sub_instructions">';
                                message += '<p class="instruction_block">'+is_reg+'</p>';
                                message += '</div>'
                                if(typeof data.team_player_status == "string"){
                                    message += '<div class="sub_instructions">';
                                    message += '<p class="instruction_block">'+ftReplaceKeyInString(data.team_player_status, textValues)+'</p>';
                                    message += '</div>'
                                }
                                
                                // Crate sign-up button
                                if(data.use_signup_button){
                                    buttons[textValues.signup_button] = function(){
                                        $(this).foreTeesModal("pleaseWait","open",true);
                                        var obj = $(this).data("ft-modalParent");
                                        var d = obj.data("ft-eventPromptData");
                                        var waitlistDate = new Date (d.date);
                                        var request = obj.data("ft-eventRequest");
                                        request["new"] = "new";
                                        ftSlotPost('Member_evntSignUp',request,{type:true});
                                    }
                                    
                                    listButtons[textValues.signup_button] = function(){
                                        $(this).foreTeesModal("pleaseWait","open",true);
                                        var obj = $(this).data("ft-modalParent");
                                        var d = obj.data("ft-eventPromptData");
                                        var waitlistDate = new Date (d.date);
                                        var request = obj.data("ft-eventRequest");
                                        request["new"] = "new";
                                        ftSlotPost('Member_evntSignUp',request);
                                    }
                                }
                                
                            }else{
                                // Display reason for no signup buttons
                                if(typeof data.reason_msg == "string"){
                                    instructions += '<div class="sub_instructions">';
                                    instructions += '<p class="instruction_block"><b>'+ftReplaceKeyInString(data.reason_msg, textValues)+'</b></p>';
                                    instructions += '</div>'
                                }
                            }
                            
                            obj.data("ft-eventIntructions", listInstructions + instructions);
                            obj.data("ft-eventListButtons", listButtons);
                            modalObj.append(message);
                            modalObj.append(instructions);
                            modalObj.ftDialog("option",{

                                buttons: buttons

                            });
                            modalObj.foreTeesModal("triggerResize");
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error Getting Event Prompt", jqXHR);
                            $(this).foreTeesModal("error", "eventPrompt");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        eventList: function (option) {
            
            var obj = $(this);

            // Check object
            var data = obj.data("ft-eventPromptData");
            //// console.log(promptData.wait_list_id );
            if((typeof data != "object")){
                //// console.log('No event prompt object');
                return false;
            }
            var request = obj.data("ft-eventRequest");
            if(typeof request != "object"){
                //// console.log('No eventPrompt JSON');
                return false;
            }

            var textValues = obj.data("ft-eventTextValues");
            
            var modalObj = obj.foreTeesModal("getModalObject","eventList");
            //modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.eventList.title, textValues));
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.eventList.title, textValues));

                    // build table
                    var table = '<table class="standard_list_table">';
                    // build header
                    table += '<thead><tr>';
                    var cellClass = "";
                    var cellType = "";
                    for (var key in data.column_map){
                        cellClass = "";
                        cellType = "";
                        if(typeof data.column_map[key]["class"] == "string"){
                            cellClass = ' class="'+data.column_map[key]["class"]+'"';
                        }
                        if(typeof data.column_map[key]["type"] == "string"){
                            cellType = data.column_map[key]["type"];
                        }
                        table += '<th'+cellClass+'>';
                        
                        if(typeof data.column_map[key]["value"] == "string"){
                            table += data.column_map[key]["value"];
                        }
                        if(typeof data.column_map[key]["value_small"] == "string"){
                            table += '<span class="value_small"></span>'+data.column_map[key]["value_small"]+'</span>';
                        }
                        table += '</th>';
                        
                    }
                    table += '</tr></thead>';
                    // Build rows
                    table += '<tbody>';
                    for (var row_key in data.row_map){
                        table += '<tr>';
                        for (var key in data.row_map[row_key]){
                            cellClass = "";
                            cellType = "";
                            if(typeof data.row_map[row_key][key]["type"] == "string"){
                                cellType = data.row_map[row_key][key]["type"];
                            }
                            if(typeof data.row_map[row_key][key]["class"] == "string"){
                                cellClass = ' class="'+data.row_map[row_key][key]["class"]+'"';
                            }
                            table += '<td'+cellClass+'>';
                            switch (cellType){
                                case "select":
                                    var this_request = $.extend(true,{},request);
                                    this_request['id:'+data.row_map[row_key][key]["value"]] = "Submit";
                                    table += '<a href="#" data-ftjson="'+escape(JSON.stringify(this_request))+'" class="standard_button event_select_button">'+options.eventPrompt.selectButton+'</a>';
                                    break;
                                default:
                                    //console.log("key:"+key+":value:"+data.row_map[row_key][key]["value"]);
                                    if(key=="select" && (typeof data.row_map[row_key][key]["value"] == "string")){
                                        table += '<div class="event_slot">';
                                    }
                                    if(typeof data.row_map[row_key][key]["value"] == "string"){
                                        table += data.row_map[row_key][key]["value"];
                                    }
                                    if(typeof data.row_map[row_key][key]["value_small"] == "string"){
                                        table += '<span class="value_small">'+data.row_map[row_key][key]["value_small"]+'</span>';
                                    }
                                    if(key=="select" && (typeof data.row_map[row_key][key]["value"] == "string")){
                                        table += '</div>';
                                    }
                                    break;
                            }
                            
                            table += '</td>';
                        }
                        table += '</tr>';
                    }
                    table += '</tbody>';
                    var buttons = obj.data("ft-eventListButtons");
                    // add html to DOM
                    modalObj.append(obj.data("ft-eventIntructions"));
                    modalObj.append(table);
                    modalObj.find(".event_select_button").click(function(){
                        $(this).foreTeesModal("pleaseWait","open",true);
                        var buttonData = JSON.parse(unescape($(this).attr("data-ftjson")));
                        ftSlotPost('Member_evntSignUp',request,{},buttonData);
                        return false;
                    });

                    modalObj.ftDialog("option",{

                        buttons: buttons,
                        positon:"top"

                    });
                    modalObj.ftDialog("open");
                    modalObj.foreTeesModal("resize");
                    modalObj.foreTeesModal("hideActivity");
                    break;
                    
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        lotteryPrompt: function (option, option2) {
            
            var obj = $(this);

            // Check Json
            var jsondata = obj.attr("data-ftjson");
            try {
                var json = JSON.parse(jsondata);
            //// console.log('Parsed Json');
            } catch (e) {
                //// console.log('Bad Json');
                return false;
            }
            if(json.type == "Member_lott"){
                // Don't prompt -- go directly to lottery page
                obj.foreTeesModal("pleaseWait","open",true);
                var formObj = $('<form action="'+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(json, {}) + '</form>'); 
                $('body').append(formObj);
                formObj.submit();
                return;
            }
            // Build url for JSON request via REST.
            json.stime = ftFindKey("time",json);
            obj.data("ft-lotteryRequest", json);
            var url = "Member_mlottery?jsonMode=true&" + ftObjectToUri(json, {});
            
            var modalObj = obj.foreTeesModal("getModalObject","lotteryPrompt");
            //modalObj.foreTeesModal("initModalObject",options.lotteryPrompt.titleLoading);
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.lotteryPrompt.titleLoading);
                    modalObj.ftDialog("open");
                    
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //var obj = $(this);
                            if(typeof data.access_error == "string" && data.access_error.length){
                                obj.foreTeesModal("error","lotteryPrompt", data.access_error);
                                return false;
                            }
                            // lottery state (lstate)
                            //    1 = before time to take requests (too early for requests)
                            //    2 = after start time, before stop time (ok to take requests)
                            //    3 = after stop time, before process time (late, but still ok for pro)
                            //    4 = requests have been processed but not approved (no new tee times now)
                            //    5 = requests have been processed & approved (ok for all tee times now)
                            
                            var modalObj = obj.data("ft-lotteryPromptObj");
                            obj.data("ft-lotteryPromptData", data);
                            var request = obj.data("ft-lotteryRequest");
                            modalObj.empty();
                            
                            var itemDate = ftIntDateToDate(request.date);
                            // Set text values used by text options ( use "["key-name"]" in option)
                            var textValues = {
                                lottery_text: ((typeof data.lottery_text == "string" && data.lottery_text.length) ? data.lottery_text : "Lottery Request"),
                                long_date: itemDate.format(options.lotteryPrompt.dateFormat)
                                
                            }
                            
                            obj.data("ft-lotteryTextValues", textValues);
                            
                            modalObj.foreTeesModal("hideActivity");
                            var message = '<div class="main_instructions"><h2>'+ftReplaceKeyInString(options.lotteryPrompt.instructionsHead, textValues)+'</h2>' + 
                            '<p>'+ftReplaceKeyInString(options.lotteryPrompt.instructions, textValues)+'</p>' +
                            '</div>';
                            /*
                            if(data.onlist > 0){
                                message += '<div class="sub_instructions"><p><b>'+options.lotteryPrompt.onlist+'</b></p></div>';
                                if(data.member_access < 1){
                                    message += '<div class="sub_instructions"><p><b>'+options.lotteryPrompt.contactModify+'</b></p></div>';
                                }
                            }else if(data.member_access < 1){
                                message += '<div class="sub_instructions"><p><b>'+options.lotteryPrompt.contactList+'</b></p></div>';
                            }
                             */
                            message += '<div class="sub_instructions">';
                            message += '<p class="lottery_block"><span>'+ftReplaceKeyInString(options.lotteryPrompt.totalRegistrations, textValues)+'</span><b>'+data.request_count+'</b></p>';
                            message += '</div>'
                            
                            var buttons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                }
                            };
                            var viewEditButtonName = options.lotteryPrompt.viewButton;
                            if(data.allow_count){
                                var viewEditButtonName = options.lotteryPrompt.viewEditButton;
                            }
                            if(data.request_count > 0 && options.lotteryPrompt.showViewEditButton == true){
                                buttons[viewEditButtonName] = function(){
                                    $(this).data("ft-modalParent").foreTeesModal("lotteryList");
                                }
                            }
                            buttons[options.lotteryPrompt.newRequestButton] = function(){
                                $(this).foreTeesModal("pleaseWait","open",true);
                                var obj = $(this).data("ft-modalParent");
                                var d = obj.data("ft-lotteryPromptData");
                                var waitlistDate = new Date (d.date);
                                var request = obj.data("ft-lotteryRequest");
                                if(typeof request.day == "undefined"){
                                    var slotDate = ftIntDateToDate(request.date);
                                    request.day = slotDate.format("dddd");
                                }
                                if(typeof request.jump == "undefined"){
                                    request.jump = "0";
                                }
                                var formHtml = '<form action="'+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(request,{}) + '</form>';
                                var formObj = $(formHtml);
                                $("body").append(formObj);
                                formObj.submit();
                            }
                            modalObj.append(message);
                            modalObj.ftDialog("option",{

                                buttons: buttons,
                                title:ftReplaceKeyInString(options.lotteryPrompt.title, textValues)

                            });
                            modalObj.foreTeesModal("triggerResize");
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error Getting Lottery Prompt", jqXHR);
                            $(this).foreTeesModal("error", "lotteryPrompt");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        lotteryList: function (option) {
            
            var obj = $(this);

            // Check object
            var data = obj.data("ft-lotteryPromptData");
            //// console.log(promptData.wait_list_id );
            if((typeof data != "object")){
                //// console.log('No lottery prompt object');
                return false;
            }
            var request = obj.data("ft-lotteryRequest");
            if(typeof request != "object"){
                //// console.log('No lotteryPrompt JSON');
                return false;
            }

            var itemDate = ftIntDateToDate(request.date);
            // Set text values used by text options ( use "["key-name"]" in option)
            
            var textValues = obj.data("ft-lotteryTextValues");
            
            var modalObj = obj.foreTeesModal("getModalObject","lotteryList");
            //modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.lotteryList.title, textValues));
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",ftReplaceKeyInString(options.lotteryList.title, textValues));
                    
                    var use_course = (typeof data.course == "string" && (data.course.length>0));
                    var message = '<div class="main_instructions"><h2>'+ftReplaceKeyInString(options.lotteryList.instructionsHead, textValues)+'</h2>';
                    message += '</div>';
                    message += '<div class="sub_instructions">';
                    message += '<p>'+ftReplaceKeyInString(options.lotteryList.selectLottery, textValues)+'</p>';
                    message += '<p>'+ftReplaceKeyInString(options.lotteryList.noSelectNote, textValues)+'</p>';
                    message += '</div>';
                    message += '<div class="sub_instructions">';
                    message += '<p>'+ftReplaceKeyInString(options.lotteryList.frontBackLegend, textValues)+'</p>';
                    message += '</div>';
                    message += '<table class="standard_list_table">';
                    message += '<thead><tr>';
                    message += '<th>Time</th>';
                    if(use_course){
                        message += '<th>Course</th>';
                    }
                    message += '<th>F/B</th><th>Player 1</th><th>Player 2</th><th>Player 3</th><th>Player 4</th>';
                    if(data.fives != 0){
                        message += '<th class="row_player_5">Player 5</th>';
                    }
                    message += '</tr></thead>';
                    message += '<tbody>';

                    var request_index = 0;
                    var requestDate = new Date();
                    var groupHtml = "";
                    var showGroup = false;
                    var buttonData = {};
                    var max_players_per_group = 0;
                    var rowClass = "";
                    
                    // Loop over each request
                    for (var requestKey in data.requests) {
                        requestDate = new Date(data.requests[requestKey].year, (data.requests[requestKey].month)-1, data.requests[requestKey].day, data.requests[requestKey].hour, data.requests[requestKey].minute, 0);
                        rowClass = "";
                        if(data.requests[requestKey].participant){
                            rowClass = "member_participant"
                        }
                        message += '<tr class="'+rowClass+'"><td>';
                        if(data.requests[requestKey].allow){
                            var buttonData = {
                                date:requestDate.format("yyyymmdd"),
                                day:requestDate.format("dddd"),
                                course:data.requests[requestKey].lcourse,
                                lname:request.lname,
                                lottid:data.requests[requestKey].lottid,
                                slots:data.requests[requestKey].slots,
                                lstate:request.lstate,
                                index:request.index,
                                displayOpt:data.requests[requestKey].displayOpt,
                                stime:data.requests[requestKey].stime,
                                fb:data.requests[requestKey].fb,
                                p5:((data.requests[requestKey].players_per_group == 5)?"Yes":"No")
                            }
                            message += '<a href="#" class="standard_button lotterylist_button" data-ftjson="'+escape(JSON.stringify(buttonData))+'">' + requestDate.format("shortTime") + '</a>';
                        }else{
                            message += requestDate.format("shortTime");
                        }
                        message += '</td>';
                        if(use_course){
                            message += '<td>'+ data.requests[requestKey].lcourse + '</td>';
                        }
                        // Loop over each group
                        for (var i = 0; i < data.requests[requestKey].player_count; i += data.requests[requestKey].players_per_group) {
                            groupHtml = "";
                            showGroup = false;
                            if(i > 0){
                                // If we are past the first group, start a new row
                                groupHtml +='</tr><tr class="'+rowClass+' group_row"><td>Group '+((i/data.requests[requestKey].players_per_group)+1)+'</td>'
                                if(use_course){
                                    groupHtml += '<td></td>';
                                }
                            }
                            groupHtml += '<td>'+options.lotteryList.frontBackDecode[data.requests[requestKey].fb.toString()]+'</td>';
                            for(var i2 = i; i2 < i + data.requests[requestKey].players_per_group; i2 ++){
                                groupHtml += '<td>'+data.requests[requestKey].players["player_" + i2]+'</td>';
                                if(data.requests[requestKey].players["player_" + i2] != ""){
                                    showGroup = true;
                                }
                            }
                            if(i == 0 || showGroup){
                                if(data.requests[requestKey].players_per_group > max_players_per_group){
                                    max_players_per_group = data.requests[requestKey].players_per_group;
                                }
                                message += groupHtml;
                            }
                        }
                        message += '</tr>';
                    }
                    message += '</tbody>';
                    message += '</table>';

                    var buttons = {
                        "Close": function(){
                            $(this).ftDialog("close");
                        }
                    };
                    // add our table to the DOM
                    modalObj.append(message);
                    // Remove the fifth player column header, if needed
                    modalObj.find(".row_player_" + (max_players_per_group + 1)).remove();
                    // Activate the request edit buttons
                    modalObj.find(".lotterylist_button").click(function(){
                        $(this).foreTeesModal("pleaseWait");
                        var buttonData = JSON.parse(unescape($(this).attr("data-ftjson")));
                        var formObj = $('<form action="'+ftSetJsid('Member_lott')+'" method="post">' + ftObjectToForm(buttonData, {}) + '</form>'); 
                        $('body').append(formObj);
                        formObj.submit();
                        return false;
                    });

                    modalObj.ftDialog("option",{

                        buttons: buttons,
                        positon:"top"

                    });
                    modalObj.ftDialog("open");
                    modalObj.foreTeesModal("resize");
                    modalObj.foreTeesModal("hideActivity");
                    break;
                    
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        pleaseWait: function (option, popstate, newtimeout) {
            
            var obj = $(this);
            
            var timeout = 145000; // pleaseWait timeout set to 145 seconds
            if(newtimeout){
                timeout = newtimeout;
            }
            if(!$.mobile){
                var modalObj = obj.foreTeesModal("getModalObject","pleaseWait");
            }
            //modalObj.foreTeesModal("initModalObject",options.pleaseWait.title);
            var timers = obj.data("ft-timeoutTimers");
            if (typeof timers == "undefined"){
                timers = [];
                obj.data("ft-timeoutTimers", timers)
            }
            switch (option) {
          
                default:
                case "open":
                    
                    //var timeout = 10;
                    
                    if(!$.mobile){
                        modalObj.foreTeesModal("initModalObject",'',{noTitlebar:true});
                    }
                    // Work-around for iOS5 back-button browser cache issue
                    if(popstate && (ftIsMobile("ios"))){
                        $(window).bind('popstate.ft-pleaseWait',function(){
                            $(window).unbind('popstate.ft-pleaseWait');
                            location.reload();
                        });
                    } else if (popstate && (ftIsMobile("ie11"))){
                        window.addEventListener('pageshow',function(){
                            history.go(0);
                        },false);
                    } else {
                        timers.push(setTimeout(function(){
                            obj.foreTeesModal("timeoutError");
                        },timeout));
                    }
                    if(!$.mobile){
                        var dialogObj = modalObj.parents('.ui-dialog').first();
                        dialogObj.removeClass('ui-widget-content');
                        modalObj.ftDialog("open");
                    } else {
                        $.mobile.showPageLoadingMsg();
                    }
                    
                    break;
                case "close":
                    if(!$.mobile){
                        modalObj.ftDialog("close");
                    } else {
                        $.mobile.hidePageLoadingMsg();
                    }
                    
                    clearTimeout(timers.pop());
                    if(popstate && ftIsMobile("ios")){
                        $(window).unbind('popstate.ft-pleaseWait');
                    }
                    break;
            }
          
        },
        
        waitListPrompt: function (option) {
            
            var obj = $(this);

            // Check Json
            var jsondata = obj.attr("data-ftjson");
            try {
                var json = JSON.parse(jsondata);
            //// console.log('Parsed Json');
            } catch (e) {
                //// console.log('Bad Json');
                return false;
            }
            obj.data("ft-waitListRequest", json);
            // Build url for JSON request via REST.
            var url = "Member_waitlist?jsonMode=true&" + ftObjectToUri(json, {
                type:false
            });
            
            var modalObj = obj.foreTeesModal("getModalObject","waitListPrompt");
            //modalObj.foreTeesModal("initModalObject",ptions.waitListPrompt.title);
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.waitListPrompt.title);
                    
                    modalObj.ftDialog("open");
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(data){
                            //var obj = $(this);
                            var modalObj = obj.data("ft-waitListPromptObj");
                            var waitlistDate = new Date (data.date);
                            modalObj.empty();
                            obj.data("ft-waitListPromptData", data);
                            modalObj.parent().data("ft-activityIndicator").activity("stop");
                            modalObj.parent().data("ft-activityIndicator").hide();
                            modalObj.parent().find(".ui-dialog-titlebar-close").show();
                            // Set text values used by text options ( use "["key-name"]" in option)
                            var textValues = {
                                
                                start_time: data.start_time,
                                end_time: data.end_time,
                                day: ((data.index == 0) ? "today" : "on this day"),
                                course: data.course,
                                date: waitlistDate.format(options.waitListPrompt.dateFormat),
                                name: data.name,
                                notice: data.waitlist_notice,
                                count: data.count
                                
                            }
                            
                            var message = '<div class="main_instructions"><h2>'+options.waitListPrompt.instructionsHead+'</h2>' + 
                            '<p>'+ftReplaceKeyInString(options.lotteryPrompt.instructions, textValues)+'</p>' +
                            '</div>';
                            if(data.onlist > 0){
                                message += '<div class="sub_instructions"><p><b>'+ftReplaceKeyInString(options.waitListPrompt.onlist, textValues)+'</b></p></div>';
                                if(data.member_access < 1){
                                    message += '<div class="sub_instructions"><p><b>'+ftReplaceKeyInString(options.waitListPrompt.contactModify, textValues)+'</b></p></div>';
                                }
                            }else if(data.member_access < 1){
                                message += '<div class="sub_instructions"><p><b>'+ftReplaceKeyInString(options.waitListPrompt.contactList, textValues)+'</b></p></div>';
                            }
                            message += '<div class="sub_instructions">';
                            message += '<p class="waitlist_block"><span>Date:</span><b>'+waitlistDate.format(options.waitListPrompt.dateFormat)+'</b></p>';
                            message += '<p class="waitlist_block"><span>Course:</span><b>'+ data.course +'</b></p>';
                            message += '<p class="waitlist_block"><span>Wait List:</span><b>'+ data.start_time + ' to ' + data.end_time +'</b></p>';
                            message += '<p class="waitlist_block"><span>Signups:</span><b>'+((data.member_view > 0)?data.count:'N/A')+'</b></p>';
                            message += '</div>'

                            var buttons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                }
                            };
                            if(data.count > 0 && data.member_access > 0){
                                buttons["View Wait List"] = function(){
                                    $(this).data("ft-modalParent").foreTeesModal("waitList");
                                }
                            }
                            if(data.onlist < 1 && data.member_access > 0){
                                buttons["Continue With Sign-up"] = function(){
                                    $(this).foreTeesModal("pleaseWait", "open", true);
                                    var obj = $(this).data("ft-modalParent");
                                    var d = obj.data("ft-waitListPromptData");
                                    //console.log(d);
                                    var waitlistDate = new Date (d.date);
                                    var json = obj.data("ft-waitListRequest");
                                    if(typeof d.jump == "undefined"){
                                        d.jump = "0";
                                    }
                                    var request = {
                                        waitListId:d.wait_list_id,
                                        sdate:waitlistDate.format("yyyymmdd"),
                                        day:waitlistDate.format("dddd"),
                                        index:d.index,
                                        course:json.course,
                                        returnCourse:json.returnCourse,
                                        jump:json.jump
                                    }
                                    var formHtml = '<form action="'+ftSetJsid('Member_waitlist_slot')+'" method="post">' + ftObjectToForm(request,{}) + '</form>';
                                    var formObj = $(formHtml);
                                    $("body").append(formObj);
                                    formObj.submit();
                                }
                            }else if(data.member_access > 0){
                                buttons["Modify Your Sign-up"] = function(){
                                    $(this).foreTeesModal("pleaseWait", "open", true);
                                    var obj = $(this).data("ft-modalParent");
                                    var d = obj.data("ft-waitListPromptData");
                                    var waitlistDate = new Date (d.date);
                                    var json = obj.data("ft-waitListRequest");
                                    if(typeof d.jump == "undefined"){
                                        d.jump = "0";
                                    }
                                    var request = {
                                        waitListId:d.wait_list_id,
                                        sdate:waitlistDate.format("yyyymmdd"),
                                        day:waitlistDate.format("dddd"),
                                        index:d.index,
                                        signupId:d.onlist,
                                        course:json.course,
                                        returnCourse:json.returnCourse,
                                        jump:json.jump
                                    }
                                    var formHtml = '<form action="'+ftSetJsid('Member_waitlist_slot')+'" method="post">' + ftObjectToForm(request,{}) + '</form>';
                                    var formObj = $(formHtml);
                                    $("body").append(formObj);
                                    formObj.submit();
                                }
                            }
                            
                            modalObj.append(message);
                            modalObj.ftDialog("option",{

                                buttons: buttons

                            });
                            modalObj.foreTeesModal("triggerResize");

                            
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error Getting Wait List Prompt", jqXHR);
                            $(this).foreTeesModal("error", "waitListPrompt");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
        },
        
        waitList: function (option) {
            
            var obj = $(this);

            // Check object
            var promptData = obj.data("ft-waitListPromptData");
            //// console.log(promptData.wait_list_id );
            if((typeof promptData == "object") && (typeof promptData.wait_list_id != "undefined")){
                var waitlistDate = new Date (promptData.date);
                var requestObj = {
                    view:'current',
                    waitListId:promptData.wait_list_id,
                    sdate:waitlistDate.format("yyyymmdd"),
                    index:promptData.index
                }
            }else{
                //// console.log('No waitlist prompt object');
                return false;
            }

            // Build url for JSON request via REST.
            
            var url = "Member_waitlist?jsonMode=true&" + ftObjectToUri(requestObj,{});
            //// console.log(url);
            
            var modalObj = obj.foreTeesModal("getModalObject","waitList");
            //modalObj.foreTeesModal("initModalObject",ptions.waitList.title);
            
            switch (option) {
                default:
                case "open":
                    modalObj.foreTeesModal("initModalObject",options.waitList.title);
                    
                    modalObj.ftDialog("open");
                    $.ajax({
                        
                        url: url,
                        //context:obj,
                        dataType: 'json',
                        success: function(response){
                            
                            //var obj = $(this);
                            var modalObj = obj.data("ft-waitListObj");
                            var data = obj.data("ft-waitListPromptData")
                            var waitlistDate = new Date (data.date);
                            modalObj.empty();
                            obj.data("ft-waitListData", response);
                            modalObj.parent().data("ft-activityIndicator").activity("stop");
                            modalObj.parent().data("ft-activityIndicator").hide();
                            modalObj.parent().find(".ui-dialog-titlebar-close").show();
                            var message = '<div class="main_instructions"><h2>'+options.waitList.listName.replace("[list_name]",data.name)+'</h2>';
                            message += '<p class="waitlist_block"><span>Date:</span><b>'+waitlistDate.format(options.waitList.dateFormat)+'</b></p>';
                            message += '<p class="waitlist_block"><span>Course:</span><b>'+ data.course +'</b></p>';
                            //message += '<p class="waitlist_block"><span>Wait List:</span><b>'+ data.start_time + ' to ' + data.end_time +'</b></p>';
                            message += '<p class="waitlist_block"><span>Signups:</span><b>'+((data.member_view > 0)?data.count:'N/A')+'</b></p>';
                            message += '</div>';
                            message += '<div class="sub_instructions">';
                            message += '<p>'+options.waitList.listDate.replace("[report_date]",response.options.report_date)+'</p>';
                            message += '</div>';
                            message += '<table class="standard_list_table">';
                            message += '<thead><tr>';
                            message += '<th>Pos</th><th>Members</th><th>Desired Time</th><th>Players</th>';
                            message += '</tr></thead>';
                            message += '<tbody>';

                            var players = '';
                            var position = 1;
                            // Loop over each signup
                            for (var signupKey in response.signups) {
                                // Loop over each player
                                players = '';
                                for (var playerKey in response.signups[signupKey].players) {
                                    if (players.length){
                                        players += ", ";
                                    }
                                    players += '<span>' + response.signups[signupKey].players[playerKey].player_name + ' <span>(' + response.signups[signupKey].players[playerKey].cw;
                                    if( response.signups[signupKey].players[playerKey]["9hole"] > 0){
                                        players += '9'; 
                                    }
                                    players += ')</span></span>';
                                }
                                message += '<tr><td>'+position+'</td><td class="waitlist_players">'+players+'</td><td><span>'+response.signups[signupKey].options.start_time+' - '+response.signups[signupKey].options.end_time+'<span></td><td>'+response.signups[signupKey].options.player_count+'</td></tr>';
                                position ++;
                            }
                            message += '</tbody>';
                            message += '</table>';
                            
                            var buttons = {
                                "Close": function(){
                                    $(this).ftDialog("close");
                                }
                            };

                            modalObj.append(message);
                            modalObj.ftDialog("option",{

                                buttons: buttons

                            });
                            modalObj.foreTeesModal("resize");
                            modalObj.foreTeesModal("hideActivity");
                                                      
                            
                        },
                        error: function(jqXHR){
                            ft_logError("Error Getting Waitlist", jqXHR);
                            $(this).foreTeesModal("error", "waitList");
                        }
                    
                    });
                    
                    break;
                case "close":
                    modalObj.ftDialog("close");
                    break;
            }
          
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
    
    $.fn.foreTeesModal = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.foreTeesModal' );
        }     
    }
    
  

})(jQuery);


// Simulate some of JqueryUI's "dialog" functionality in jQueryMobile, 
// so we can create modals on mobile without heavy modification to foreTeesModal
(function($){
    
    var pluginData = {
        
        jsonRunnin:false,
        jsonData:{}

    };
    
    var options = {
        
        // Options
        autoOpen: true,
        buttons: {},
        closeOnEscape: true, // does nothing?
        closeText: 'close', // does nothing
        dialogClass: '',
        draggable: true, // does nothing
        height: 'auto',
        hide: null,
        maxHeight: false,
        maxWidth: false,
        minHeight: 150,
        minWidth: 150,
        modal: true, // always true
        position: {my:'center', at:'center',of:window}, // does nothing
        resizable: false, // does nothing
        show: null, // does nothing
        title: null,
        width: 300,
        
        // Events
        beforeClose: function(event,ui){},
        close: function(event,ui){},
        create: function(event,ui){},
        drag: function(event,ui){}, // na
        dragStart: function(event,ui){}, // na
        dragStop: function(event,ui){}, // na
        focus: function(event,ui){},
        open: function(event,ui){},
        resize: function(event,ui){}, // ??
        resizeStart: function(event,ui){}, // ??
        resizeStop: function(event,ui){} // ??
        
        
    };
    
    var methods = {
        
        close: function () {
            
        },
        
        destroy: function() {
            
        },
        
        isOpen: function() {
            
        },
        
        moveToTop: function() {
        // Do nothing  
        },
        
        open: function() {
            
        },

        option: function ( opt, val ) {
            
            if(typeof options[opt] == "undefined" && typeof val == "undefined"){
                return options;
            } else if(typeof options[opt] == "object"){
                for(var k in opt){
                    methods.option(k, opt[k]);
                }
            } else if(typeof options[opt] == "undefined" && typeof val != "undefined"){
                $.error( 'Option "' + opt + '" is not valid for jQuery.ftMobileDialog' );
            } else if(typeof val == "undefined"){
                //// console.log("setting options");
                return options[opt];
            } else {
                switch(opt){
                    case 'buttons':
                        if($.isArray(val)){
                            var oVal = {};
                            for (var i = 0; i < val.length; i++){
                                oVal[val[i].title] = oVal[val[i].click];
                            }
                            val = oVal;
                        }
                        options[opt] = val;
                        break;
                    default:
                        options[opt] = val;
                        break;
                }
            }
        },
        
        widget: function(){
            
        }
        
        
    };
    
    $.fn.ftMobileDialog = function ( method ){
        if (methods[method]) {
            return methods[ method ].apply (this, Array.prototype.slice.call( arguments, 1));
        } else if ( typeof method === 'object' || ! method) {
            return methods.init.apply( this, arguments );
        } else {
            $.error( 'Method ' + method + ' does not exist on jQuery.ftMobileDialog' );
        }     
    }
    
  

})(jQuery);

